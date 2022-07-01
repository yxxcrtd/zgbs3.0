/*
 * 文件名：DBConnect.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统
 * 修改人：lihuadong
 * 修改时间：2017年2月7日
 * 修改内容：新建
 * 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.interf.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.joyintech.tams.report.interf.common.constant.InterfConstant;
import com.joyintech.tams.report.interf.common.dto.InterfEntity;

/**
 * 资讯信息直接登录db操作共通
 * 
 * @author lihuadong
 * @version 1.0 2017年2月7日
 * @see DBConnect
 * @since 1.0
 */
public class DBConnect {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(DBConnect.class);

	/**
	 * 数据插入数据库
	 * 
	 * @param tablename
	 *            表名
	 * @param sqlList
	 *            输入语句列表
	 * @param basicDataSource
	 *            DB信息
	 */
	public static void insertData(String tablename, List<String> sqlList, DruidDataSource basicDataSource) {
		Connection conn = null;
		Statement stmt= null;
		try {
			conn = basicDataSource.getConnection();

			// 关闭数据库连接 自动提交事务
			conn.setAutoCommit(false);

			stmt = conn.createStatement();
			for (int i = 0; i < sqlList.size(); i++) {
				if (!"".equals(sqlList.get(i))) {
					stmt.addBatch(sqlList.get(i));
				}
				try {
					if (i > 0 && i % InterfConstant.SQL_COMMIT_CNT == 0) {
						stmt.executeBatch();
						// 提交事务
						conn.commit();
						stmt.clearBatch();
					} else if (i == sqlList.size() - 1) {
						stmt.executeBatch();
						// 提交事务
						conn.commit();
						stmt.clearBatch();
					}
				} catch (SQLException e) {
					logger.error("数据插入数据出错...表名：" + tablename);
					logger.error(e.toString());
					continue;
				}

			}
		} catch (SQLException e) {
			logger.error("数据插入数据出错...表名：" + tablename);
			logger.error(e.toString());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}

	}

	/**
	 * 从数据库检出数据
	 * 
	 * @param <T>
	 *            目标实体类
	 * @param targetClass
	 *            返回数据类型
	 * @param sql
	 *            输入语句列表
	 * @param basicDataSource
	 *            DB信息
	 * @return 数据列表
	 * @throws IllegalAccessException
	 *             IllegalAccessException
	 * @throws InstantiationException
	 *             InstantiationException
	 */
	public static <T extends InterfEntity> List<T> selectData(String sql, Class<T> targetClass,
			DruidDataSource basicDataSource) throws InstantiationException, IllegalAccessException {
		ArrayList<T> result = new ArrayList<T>();
		try (Connection conn = basicDataSource.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(sql);){
			while (rs.next()) {
				T entity = targetClass.newInstance();
				Pattern p = Pattern.compile("AS\\s([^(, )]*)");
				Matcher matcher = p.matcher(sql);
				List<String> columnsName = new ArrayList<String>();
				while (matcher.find()) {
					columnsName.add(matcher.group(1));
				}
				IntStream.range(0, columnsName.size()).boxed().map(i -> {
					try {
						return new Object[] { columnsName.get(i), rs.getObject(i + 1) };
					} catch (SQLException e) {
						logger.error("map property error:" + columnsName.get(i));
						logger.error(e.getMessage(),e);
						return new Object[] { "", null };
					}
				}).forEach(col -> {
					try {
						if (col[1] != null) {
							Class<? extends Object> c = col[1].getClass();
							if (c == Timestamp.class) {
								c = Date.class;
							}
							// Java反射调用方法
							Method m = entity.getClass()
									.getMethod(InterfConstant.SET + FileUtil.captureName((String) col[0]), c);
							m.invoke(entity, col[1]);
						}
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						logger.error("invoke set property error:" + (String) col[0]);
						logger.error(e.getMessage(),e);
					}
				});
				result.add(entity);
			}
		} catch (SQLException e) {
			logger.error("数据读取数据出错...SQL：" + sql);
            logger.error(e.getMessage(),e);
		} 
		return result;
	}

	/**
	 * 数据插入数据库
	 * 
	 * @param tablename
	 *            表名
	 * @param sqlList
	 *            输入语句列表
	 * @param printProgress
	 *            是否打印执行进度
	 * @param basicDataSource
	 *            DB信息
	 */
	public static void insertData(String tablename, List<String> sqlList, DruidDataSource basicDataSource,
			boolean printProgress) {
		Connection conn = null;
		Statement stmt =null;
		try {
			conn = basicDataSource.getConnection();

			// 关闭数据库连接 自动提交事务
			conn.setAutoCommit(false);

			stmt = conn.createStatement();
			for (int i = 0; i < sqlList.size(); i++) {
				if (!"".equals(sqlList.get(i))) {
					stmt.addBatch(sqlList.get(i));
				}
				try {
					if (printProgress) {
						if (i > 0 && i % 50 == 0) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							logger.info(String.format("处理进度:%.2f%%", i * 100.0f / sqlList.size()));
							stmt.clearBatch();
						} else if (i == sqlList.size() - 1) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							logger.info(String.format("处理进度:%.2f%%", i * 100.0f / sqlList.size()));
							stmt.clearBatch();
						}
					} else {
						if (i > 0 && i % InterfConstant.SQL_COMMIT_CNT == 0) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							stmt.clearBatch();
						} else if (i == sqlList.size() - 1) {
							stmt.executeBatch();
							// 提交事务
							conn.commit();
							stmt.clearBatch();
						}
					}
				} catch (SQLException e) {
					logger.error("数据插入数据出错...表名：" + tablename);
					logger.error(e.toString());
					continue;
				}

			}
		} catch (SQLException e) {
			logger.error("数据插入数据出错...表名：" + tablename);
			logger.error(e.toString());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
}
