package com.hbl.solr.process.thread;

import org.apache.log4j.Logger;

import com.hbl.solr.config.Config;
import com.hbl.solr.util.LogUtils;
import com.hbl.solr.util.MysqlJdbcUtil;

public class CommonProcess {
	
	public static final int PAGESIZE = Integer.parseInt(Config.getString("max_count"));
	private MysqlJdbcUtil jdbc;
	private static String LASTTIME_INDEX ; // 记录最后插入索引的updatetime
	private int idxItv;//索引更新时间间隔
	private String processUrl; // 索引url
	
	public static final Logger log = Logger.getLogger(CommitIndex.class);
	
	public CommonProcess(MysqlJdbcUtil jdbc, String processUrl){
		try {
			this.jdbc = jdbc;
			this.processUrl = processUrl;
			LASTTIME_INDEX = Config.getString("index_lastuptimefile");
			idxItv = Config.getInt("idxItv", 60*1000);
			//设置多长时间做一次索引，如果设置值小于一秒，那么系统默认1分钟更新一次索引
			if(idxItv < 1000) {
				idxItv = 60 * 1000;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 做索引
	 * @throws Exception
	 */
	public void processIndex() throws Exception {
		CommitIndex commitIndex = new CommitIndex(this.processUrl, jdbc);
		try {
			boolean touchEnd = false;
			boolean errorOccur = false;
			try {
				touchEnd = index(commitIndex);
			}catch(Exception e) {
				e.printStackTrace();
				log.error("INDEX STOPPED WITH ERROR");
				errorOccur = true;
			}
			if(errorOccur || touchEnd) {
				try {
					Thread.sleep(idxItv);
				} catch (InterruptedException e) {
					log.error("while sleep itv : " + idxItv, e);
				}
			}
			if(errorOccur) {
				System.gc();
			}
		} finally {
			jdbc.closeConn();
			log.info("关闭数据库连接");
		}
	}
	
	/**
	 * 做索引
	 * @param isUpdate
	 * 		是否更新索引，true更新，false不更新
	 * @param commitIndex
	 * 		提交索引类实例
	 * @return
	 */
	public boolean index(CommitIndex commitIndex){
		boolean isNewest = commitIndex.work(this.getUpTime());
		log.info("commit changes");
		//从时间配置文件中拿出上轮做索引时记录的最后更新时间
		String nowUpTime = this.getUpTime();
		/**
		 * 如果索引更新的最后一条记录的时间不等于配置文件中的时间，那么需要更新配置文件中的时间
		 * 同时重新加载搜索器
		 */
		if(commitIndex.getUpTime()!=null&&!commitIndex.getUpTime().equals(nowUpTime)){
			this.setUpTime(commitIndex.getUpTime());
		}
		return isNewest;	
	}
	
	/**
	 * 设置索引数据最后更新时间
	 * @param last
	 */
	private void setUpTime(String upTime) {
		LogUtils.UpdateId(Integer.parseInt(upTime), LASTTIME_INDEX);
	}
	/**
	 * 获得索引数据最后更新时间
	 * @return
	 */
	private String getUpTime() {
		return LogUtils.readFiles(LASTTIME_INDEX);
	}
}
