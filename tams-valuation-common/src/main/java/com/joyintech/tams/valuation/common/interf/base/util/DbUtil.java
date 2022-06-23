package com.joyintech.tams.valuation.common.interf.base.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maodongjin
 *	DBUTIL用于处理DB接口
 */
public class DbUtil {
	
	private String SERVER_IP;
	
	private int SERVER_PORT;
	
	private String SERVER_NAME;
	
	private String LOAD_USERNAME;
	
	private String LOAD_PASSWORD;
	
	private Connection dbConnection;
	
	private String connectionResult;
	/**
	 * 日志
	 */
//	private Logger logger = Logger.getLogger(DbUtil.class);
	private static Logger logger = LoggerFactory.getLogger(DbUtil.class);
	
	//驱动程序名
	private static String oracleDriver ="oracle.jdbc.driver.OracleDriver";

	public DbUtil(String serverIp, int serverPort, String serverName, String userName, String password) throws ClassNotFoundException {
		SERVER_IP = serverIp;
		SERVER_PORT = serverPort;
		SERVER_NAME = serverName;
		LOAD_USERNAME = userName;
		LOAD_PASSWORD = password;
		this.setDbConnection(null);
	}
	/**
	 * 执行指定SQL
	 * @param sql
	 */
	public void load(String sql) {
		// TODO Auto-generated method stub
		
	}

	//数据库初始化连接
	public void init() {
		// TODO Auto-generated method stub
		try {
			Class.forName(oracleDriver).newInstance();
			String url="jdbc:oracle:thin:@"+SERVER_IP+":"+SERVER_PORT+"/"+SERVER_NAME; 
			dbConnection = DriverManager.getConnection(url,LOAD_USERNAME,LOAD_PASSWORD);
			connectionResult = "成功";
		} catch (ClassNotFoundException e) {
//			System.out.println("对不起，找不到对应的驱动程序!");
			logger.info("对不起，找不到对应的驱动程序!");
			connectionResult ="失败      "+e.getMessage();
			logger.error("",e);
		}catch (SQLException e) {
//			System.out.println("对不起，数据库连接异常!");
			logger.info("对不起，数据库连接异常!");
			connectionResult ="失败      "+e.getMessage();
			logger.error("",e);
		} catch(Exception e){
			connectionResult ="失败      "+e.getMessage();
			logger.error("",e);
		}
	}
	public void release(){
        if (dbConnection != null) {
            try {
            	dbConnection.close();
            } catch (Exception e) {
            	logger.error("",e);
            }
        }
	}
	public Connection getDbConnection() {
		return dbConnection;
	}
	public void setDbConnection(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}
	/**
	 * @return the connectionResult
	 */
	public String getConnectionResult() {
		return connectionResult;
	}
	/**
	 * @param connectionResult the connectionResult to set
	 */
	public void setConnectionResult(String connectionResult) {
		this.connectionResult = connectionResult;
	}
	
}
