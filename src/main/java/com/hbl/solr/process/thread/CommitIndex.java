package com.hbl.solr.process.thread;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.hbl.solr.bean.Product;
import com.hbl.solr.util.MysqlJdbcUtil;
import com.hbl.solr.util.UnixDateUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CommitIndex {

	public static final Logger log = Logger.getLogger(CommitIndex.class);
	private final String SELECT_ITEMS = " oId,articleTitle,articleTags,articleContent,articlePermalink,articleCreateDate,articleUpdateDate,articleIsPublished ";
	private static final int LIMIT = 50;
	private final String APPEND = " order by articleUpdateDate limit " + LIMIT;
	private final String SELECT_BY_UPTIME = "select " + SELECT_ITEMS + "  from b3_solo_article where articleUpdateDate>? " + APPEND;
	private String upTime;
	private int roundNum=0;
	private HttpSolrClient server; // solr server
	private MysqlJdbcUtil jdbc;
	
	public CommitIndex(String url, MysqlJdbcUtil jdbc){
		this.server = new HttpSolrClient(url);
		this.jdbc = jdbc;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("关闭HttpSolrClient");
				try {
					server.close();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 创建索引，并根据isUpdate判断是否更新索引
	 * @param uptime
	 * 		文件中记录的最后创建索引的updatetime
	 * @param isUpdate
	 * 		是否更新索引，true更新，false不更新
	 * @return
	 */
	public boolean work(String uptime) {
		log.info("doIndex----->"+uptime);
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("doIndex----->"+uptime);
		this.setUpTime(uptime);
		try {
			this.doIndex(SELECT_BY_UPTIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.touchEnd();
	}
	
	/**
	 * 用于判断索引是否已经做到最新
	 * 如果带查询出来的条数limit 减去处理成功的条数 大于100，说明数据已经做到最新
	 * 如果小于100，说明数据还有读取空间
	 * @return
	 */
	public boolean touchEnd() {
		//5000-本轮索引所做的条数>100返回true
		boolean result = LIMIT - this.roundNum > 10;
		this.roundNum = 0;
		return result;
	}
	
	/**
	 * 从数据库中查询数据并向solr中创建/更新索引
	 * @param sql
	 * 		数据库执行的sql
	 * @param updateIndex
	 * 		false是创建索引，执行SELECT_BY_UPTIME；true是更新索引，执行SELECT_BY_RECID
	 */
	public void doIndex(String sql) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String recordCurrentId = null;
		try {
			ps = jdbc.getConn().prepareStatement(sql);
			ps.setString(1, UnixDateUtil.TimeStampDate(this.getUpTime(), UnixDateUtil.NORMAL_FORMATSTRING));
			rs = ps.executeQuery();
			List indexdata = new ArrayList<Product>();
			while(rs!=null&&rs.next()){
				roundNum++;
				String id = rs.getString("oId");
				recordCurrentId = id;
				/*String createTime = rs.getString("articleCreateDate").substring(0, rs.getString("articleCreateDate").indexOf("."));
				String updateTime = rs.getString("articleUpdateDate").substring(0, rs.getString("articleUpdateDate").indexOf("."));*/
				int updateTime = UnixDateUtil.DateToInt(rs.getString("articleUpdateDate"), UnixDateUtil.NORMAL_FORMATSTRING);
				String curUpdateTime = updateTime + "";
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", id);
				doc.addField("articleTitle",rs.getString("articleTitle"));
				doc.addField("articleTags",rs.getString("articleTags"));
				doc.addField("articleContent", rs.getString("articleContent"));
				doc.addField("articlePermalink",rs.getString("articlePermalink"));
				doc.addField("articleCreateDate",rs.getString("articleCreateDate").substring(0, rs.getString("articleCreateDate").indexOf(".")));
				doc.addField("articleUpdateDate",rs.getString("articleUpdateDate").substring(0, rs.getString("articleUpdateDate").indexOf(".")));
				doc.addField("articleIsPublished", rs.getInt("articleIsPublished"));
				indexdata.add(doc);
				this.setUpTime(curUpdateTime);//存放updateTime
			}
			commitindex(indexdata);
			indexdata.clear();
			indexdata.removeAll(indexdata);
			indexdata = null;
		} catch(Exception e) {
			e.printStackTrace();
			log.error("when index cid =  " + recordCurrentId, e);
		} finally {
			try {
				if(rs!=null) {
					rs.close();
					rs = null;
				}
				if(ps!=null) {
					ps.close();
					ps = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断contentId是否在索引中存在
	 * @param id
	 * @return true存在，false不存在
	 * @throws Exception
	 */
	public boolean hasId(String id){
		try{
			SolrQuery solrQuery = new SolrQuery("id:" + id);
			QueryResponse query = server.query(solrQuery);
			SolrDocumentList results = query.getResults();
			return results.size()>0;
		}catch(Exception e){
			e.getStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除solr中的索引
	 * @param query
	 */
	public void deleteindex(String query){
		try {
			server.deleteByQuery(query);
			server.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("delete failed " + query, e);
		}
	}
	
	/**
	 * 向solr中提交索引
	 * @param docs
	 * 		提交索引的数据集合
	 */
	public void commitindex(Collection docs){
		try {
			long start = System.currentTimeMillis();
			if (docs.size() > 0) { // Are there any documents left over?
				server.add(docs); // Commit within 5 minutes
			}
			log.info("-----occupy memory is-----: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			server.commit(); // Only needs to be done at the end,
			
			long endTime = System.currentTimeMillis();
			log.info("commit sepnd times："+((endTime - start)));
			docs.clear();
			docs=null;
			log.info(".........end index........");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUpTime() {
		return upTime;
	}
	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
}
