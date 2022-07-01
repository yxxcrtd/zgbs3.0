package com.joyintech.service.risk.conf;

import java.sql.SQLException;

import javax.sql.DataSource;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.joyintech.framework.config.ConnectionPoolProperties;
import com.joyintech.framework.config.DruidConnectionPoolProperties;
import com.joyintech.framework.jdbc.helper.DataSourceAppender;
import com.joyintech.framework.util.StringUtil;

/**
 * 邮储压力测试专用数据源（有专门的Oracle用户：stress_test）
 * 
 * @author jiaoxujin
 * @version 1.0 2019年8月22日
 * @see StressTestDataSourceAppender
 * @since 1.0
 */
@Component
public class StressTestDataSourceAppender extends DataSourceAppender {

	@ConfigurationProperties(prefix = "stresstest.datasource")
	class StressTestDataSourceProperties extends DruidConnectionPoolProperties {

	}

	@EnableConfigurationProperties(StressTestDataSourceProperties.class)
	class StressTestDataSource {
		/**
		 * @return DataSource
		 * @throws SQLException
		 */
		@Bean(name = "stresstestDataSource", destroyMethod = "close")
		public DataSource dataSource(StressTestDataSourceProperties properties) throws SQLException {
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
///*			else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//		        ((org.apache.tomcat.jdbc.pool.DataSource)dataSource).setLogValidationErrors(true);
//		        bindPropertiesForPoolDataSource((org.apache.tomcat.jdbc.pool.DataSource)dataSource, properties);
//			}*/
//			// 参数dbType命名有些不妥，应改为shcemaName更合适些。JIAOXUJIN
//			//initDataSource(DatabaseType.DATABASE_TYPE_ORACLE.getValue(), dataSource);
//			initDataSource("stresstestDataSource", dataSource);
//			return dataSource;
			return buildDruidDataSource("stresstestDataSource", properties);
		}
	}
/**
	 * 设置DataSource的属性
	 */
    protected void bindPropertiesForPoolDataSource(
                                                   org.apache.tomcat.jdbc.pool.DataSource dataSource,
                                                   ConnectionPoolProperties properties) {
        if (StringUtil.isNotBlank(properties.getConnectionProperties())) {
            dataSource.setConnectionProperties(properties.getConnectionProperties());
        }
        if (StringUtil.isNotBlank(properties.getValidationQuery())) {
            dataSource.setValidationQuery(properties.getValidationQuery());
        }
        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMaxActive(properties.getMaxTotal());
        dataSource.setMaxIdle(properties.getMaxIdle());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxWait((int)(properties.getMaxWaitMillis() / 1000));
        if (null != properties.getDefaultAutoCommit()) {
            dataSource.setDefaultAutoCommit(properties.getDefaultAutoCommit());
        }
        if (null != properties.getDefaultReadOnly()) {
            dataSource.setDefaultReadOnly(properties.getDefaultReadOnly());
        }
        dataSource.setDefaultTransactionIsolation(properties.getDefaultTransactionIsolation());
        if (StringUtil.isNotEmpty(properties.getDefaultCatalog())) {
            dataSource.setDefaultCatalog(properties.getDefaultCatalog());
        }
        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
        dataSource.setTestOnReturn(properties.isTestOnReturn());
        dataSource.setTestOnConnect(properties.isTestOnCreate());
        dataSource.setTestWhileIdle(properties.isTestWhileIdle());
        dataSource.setNumTestsPerEvictionRun(properties.getNumTestsPerEvictionRun());
        dataSource.setTimeBetweenEvictionRunsMillis((int)(properties.getTimeBetweenEvictionRunsMillis() / 1000));
        dataSource.setMinEvictableIdleTimeMillis((int)(properties.getMinEvictableIdleTimeMillis()));
        //dataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
        //dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        dataSource.setRemoveAbandoned(properties.isRemoveAbandonedOnBorrow());
        //dataSource.setRemoveAbandonedOnMaintenance(properties.isRemoveAbandonedOnMaintenance());
        dataSource.setRemoveAbandonedTimeout(properties.getRemoveAbandonedTimeout());
        dataSource.setLogAbandoned(properties.isLogAbandoned());

        //dataSource.setMaxAge();
        //dataSource.setPoolProperties(poolProperties);
    }
}
