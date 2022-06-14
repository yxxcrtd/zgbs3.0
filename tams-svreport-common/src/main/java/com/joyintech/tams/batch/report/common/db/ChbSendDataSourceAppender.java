package com.joyintech.tams.batch.report.common.db;

import java.sql.SQLException;

import javax.sql.DataSource;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.joyintech.framework.config.DruidConnectionPoolProperties;
import com.joyintech.framework.jdbc.helper.DataSourceAppender;

/**
 * 中债直连主从数据源配置（本服务器是一二期，单独部署时则配置三期的数据库地址，反之配置一二期数据库地址）
 * 
 * @author zhangliang
 * @version 1.0 2021年5月11日
 * @see ChbSendDataSourceAppender
 * @since 1.0
 */
@Component
public class ChbSendDataSourceAppender extends DataSourceAppender {

	@ConfigurationProperties(prefix = "chbsend.datasource")
	class ChbSendDataSourceProperties extends DruidConnectionPoolProperties {

	}

	@EnableConfigurationProperties(ChbSendDataSourceProperties.class)
	class ChbSendDataSource {
		/**
		 * @return DataSource
		 * @throws SQLException
		 */
		@Bean(name = "chbSendDataSource", destroyMethod = "close")
		public DataSource dataSource(ChbSendDataSourceProperties properties) throws SQLException {

//			DataSource dataSource = null;
//			if (StringUtil.isNotEmpty(properties.getJndiName())) {
//				JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
//				dataSourceLookup.setResourceRef(true);
//				try {
//					dataSource = dataSourceLookup.getDataSource(properties.getJndiName());
//				} catch (DataSourceLookupFailureException e) {
//					logger.error("无法获取JNDI数据源：" + properties.getJndiName(), e);
//					throw e;
//				}
//			} else {
//				dataSource = properties.initializeDataSourceBuilder().build();
//			}
//
//			if (dataSource instanceof DruidDataSource) {
//				DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
//				String validationQuery = databaseDriver.getValidationQuery();
//				if (validationQuery != null) {
//					((DruidDataSource) dataSource).setTestOnBorrow(true);
//					((DruidDataSource) dataSource).setValidationQuery(validationQuery);
//					bindPropertiesForBasicDataSource((DruidDataSource) dataSource, properties);
//				}
//			}
//			initDataSource("chbSendDataSource", dataSource);
//			return dataSource;
			return buildDruidDataSource("chbSendDataSource", properties);
		}
	}
}
