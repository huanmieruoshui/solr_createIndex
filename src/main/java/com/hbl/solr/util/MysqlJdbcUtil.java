package com.hbl.solr.util;


/**
 * JDBC数据操作工具类
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MysqlJdbcUtil {
	
	//定义连接数据库所需要的字段  
    private static String driveClassName = "com.mysql.jdbc.Driver";  
    private String url  ;  
    private String username ;  
    private String password ;  
    private Connection conn;
    //通过配置文件为以上字段赋值  
    static{  
        try {  
            //加载驱动类  
            Class.forName(driveClassName); 
    		
        } catch (Exception e) {  
            e.printStackTrace();
        }  
    }  
    
    public MysqlJdbcUtil(String url,String username,String password ){
    	this.url = url;
    	this.username = username;
    	this.password = password;
    	
    	try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    


	/**
	 * @param test
	 * @param rs
	 * @param conn
	 * @param closeConnection
	 * @return 返回类型HashMap
	 * @throws SQLException 
	 */
    public ResultSet query(String sql, Connection conn){
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * @param test
	 * @param rs
	 * @param conn
	 * @param closeConnection
	 * @return 返回类型HashMap
	 * @throws SQLException 
	 */
    public  ResultSet query(String sql){
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 
	 * 执行SQL查询语句（select）返回List<HashMap>结果集 @
	 * */
	public  List<HashMap> queryList(String sql) {
		try {
			return rsToList(query(sql, conn), conn);
		} catch (Exception e) {
			try {
				conn = DriverManager.getConnection(url, username, password);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return rsToList(query(sql, conn), conn);
		}
	}

	/**
	 * 执行SQL查询语句（select）返回一个HashMap结果
	 * 
	 * */
	public HashMap find(String sql, Connection conn,
			boolean closeConnection) {
		return rsToALine(query(sql, conn), conn, closeConnection);
	}

	/**
	 * 返回结果集中的第一个记录的HashMap形式结果
	 * 
	 * */
	public static HashMap find(ResultSet rs, Connection conn,
			boolean closeConnection) {
		return rsToALine(rs, conn, closeConnection);
	}

	/**
	 * 执行没有结果集的SQL语句(insert update 和 delete)
	 * 
	 * */
	public static int execSQL(String sql, Connection conn,
			boolean closeConnection) {
		int affectNum = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			affectNum = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (closeConnection) {
				closeAll(null, ps, conn);
			}
		}
		return affectNum;
	}
	
	/**
	 * 执行没有结果集的SQL语句(insert update 和 delete)
	 * 
	 * */
	public static int execSQL(String sql, Connection conn,Object[] val
			) {
		int affectNum = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<val.length;i++){
				ps.setString((i+1), val[i].toString());
			}
			
			affectNum = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
		return affectNum;
	}

	/**
	 * 将结果集转化为List<HashMap>
	 * 
	 * */
	public  List<HashMap> rsToList(ResultSet rs, Connection conn) {
		ArrayList ret = new ArrayList();
		ArrayList rsColNames = new ArrayList();
		if (rs != null) {
			try {
				while (rs.next()) {
					rsColNames = getRsColumns(rs, conn);
					// System.out.println(rs.getInt("replyId"));
					Map line = new HashMap();
					for (int i = 0; i < rsColNames.size(); i++) {
						line.put(rsColNames.get(i).toString(),
								rs.getObject(rsColNames.get(i).toString()));
					}
					ret.add(line);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				
			}
			// System.out.println(ret);
			return ret;
		}
		return null;
	}

	/**
	 * 返回结果集ResultSet中第一行的HashMap形式数据
	 * 
	 * */
	public static HashMap rsToALine(ResultSet rs, Connection conn,
			boolean closeConnection) {
		ArrayList rsColNames = new ArrayList();
		if (rs != null) {
			try {
				while (rs.next()) {
					rsColNames = getRsColumns(rs, conn);
					HashMap line = new HashMap();
					for (int i = 0; i < rsColNames.size(); i++) {
						line.put(rsColNames.get(i).toString(),
								rs.getObject(rsColNames.get(i).toString()));
					}
					closeAll(rs, null, null);
					return line;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (closeConnection) {
					closeAll(rs, null, conn);
				}
			}
		}
		return null;
	}

	/**
	 * 取得结果集ResultSet中表的字段信息
	 * 
	 * */
	public static ArrayList getRsColumns(ResultSet rs, Connection conn) {
		ArrayList ret = new ArrayList();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int k;
			k = rsmd.getColumnCount();
			for (int i = 1; i <= k; i++) {
				ret.add(rsmd.getColumnName(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public Connection getConn() {
		return conn;
	}
	
	public static void closeConnection(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放资源
	 * 
	 * */
	public static void closeAll(ResultSet rs, PreparedStatement ps,
			Connection conn) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public void closeConn() {
		try {
			if(conn!=null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}