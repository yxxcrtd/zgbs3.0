
/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 微服务访问路径定义
 *
 * @author 何阳阳
 * @version 2.0 2018年9月8日
 * @see ServiceApplication
 * @since 1.0
 */
@Configuration("valuationServiceApplication")
public class ServiceApplication {

	/**
	 * 共通服务
	 */
	public static String VALUATION_SERVICE_NAME = "http://valuationService";

	/**
	 * 自定义接口服务
	 */
	public static String CUSTOMER_INTERF_NAME = "http://customerInterfService";

	/**
	 * 自定义接口服务
	 */
	public static String VALUATION_BASICDATA_SERVICE_NAME = "http://valuationBasicdataService";

	/**
	 * 报表管理
	 */
	public static String REPORT_SERVICE_NAME = "http://reportService";

	/**
	 * 减值模型微服务
	 */
	public static String LOSS_MODEL_SERVICE_NAME = "http://lossModelService";

	/**
	 * 估值模型微服务
	 */
	public static String VAL_MODEL_SERVICE_NAME = "http://valModelService";

	@Configuration
	@ConditionalOnProperty(prefix = "application.micorservice.contextpath", name = "valuationService")
	@EnableConfigurationProperties(ContextPathConfiguration.class)
	class AutoConfig {

		@Autowired
		private ContextPathConfiguration config;

		@PostConstruct
		private void setConfig() {
			if(!config.isSingle()) {
				VALUATION_SERVICE_NAME = VALUATION_SERVICE_NAME + config.getValuationService();
				CUSTOMER_INTERF_NAME = CUSTOMER_INTERF_NAME + config.getCustomerInterfService();
				VALUATION_BASICDATA_SERVICE_NAME = VALUATION_BASICDATA_SERVICE_NAME + config.getValuationBasicdataService();
				LOSS_MODEL_SERVICE_NAME = LOSS_MODEL_SERVICE_NAME + config.getLossModelService();
				VAL_MODEL_SERVICE_NAME = VAL_MODEL_SERVICE_NAME + config.getValModelService();
				REPORT_SERVICE_NAME = REPORT_SERVICE_NAME + config.getReportService();

			}
			else {
				VALUATION_SERVICE_NAME = config.getValuationService();
				CUSTOMER_INTERF_NAME = config.getCustomerInterfService();
				VALUATION_BASICDATA_SERVICE_NAME = config.getValuationBasicdataService();
				LOSS_MODEL_SERVICE_NAME = config.getLossModelService();
				VAL_MODEL_SERVICE_NAME = config.getValModelService();
				REPORT_SERVICE_NAME = config.getReportService();
			}
		}
	}



	@ConfigurationProperties(prefix = "application.micorservice.contextpath")
	class ContextPathConfiguration {

		private boolean single;

		private String valuationService;

		private String customerInterfService;

		private String valuationBasicdataService;

		private String lossModelService;

		private String reportService;

		private String valModelService;


		public String getCustomerInterfService() {
			return customerInterfService;
		}

		public void setCustomerInterfService(String customerInterfService) {
			this.customerInterfService = customerInterfService;
		}

		public boolean isSingle() {
			return single;
		}

		public void setSingle(boolean single) {
			this.single = single;
		}

		public String getValuationService() {
			return valuationService;
		}

		public void setValuationService(String valuationService) {
			this.valuationService = valuationService;
		}

		public String getValuationBasicdataService() {
			return valuationBasicdataService;
		}

		public void setValuationBasicdataService(String valuationBasicdataService) {
			this.valuationBasicdataService = valuationBasicdataService;
		}

		public String getLossModelService() {
			return lossModelService;
		}

		public void setLossModelService(String lossModelService) {
			this.lossModelService = lossModelService;
		}
		public String getValModelService() {
			return valModelService;
		}

		public void setValModelService(String valModelService) {
			this.valModelService = valModelService;
		}

		public String getReportService() {
			return reportService;
		}

		public void setReportService(String reportService) {
			this.reportService = reportService;
		}
	}

}
