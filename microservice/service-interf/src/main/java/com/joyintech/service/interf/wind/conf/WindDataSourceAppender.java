/*
 * 文件名：BaseDataSourceAppender.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2019年1月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.interf.wind.conf;

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
 * @author 何阳阳
 * @version 1.0 2019年1月14日
 * @see WindDataSourceAppender
 * @since 1.0
 */
@Component
public class WindDataSourceAppender extends DataSourceAppender {

	@ConfigurationProperties(prefix = "wind.datasource")
	class WindDataSourceProperties extends DruidConnectionPoolProperties {

	}

	@EnableConfigurationProperties(WindDataSourceProperties.class)
	class WindDataSource {
		/**
		 * @return DataSource
		 * @throws SQLException
		 */
		@Bean(name = "windDataSource", destroyMethod = "close")
		public DataSource dataSource(WindDataSourceProperties properties) throws SQLException {
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
//			initDataSource(InterfDataSourceType.WIND, dataSource);
//			return dataSource;
			return buildDruidDataSource(InterfDataSourceType.WIND, properties);
		}

	}
}
