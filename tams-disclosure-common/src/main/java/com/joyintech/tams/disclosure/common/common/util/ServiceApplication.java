
/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.disclosure.common.common.util;

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
@Configuration
public class ServiceApplication {

    /**
     * 共通服务
     */
    public static String VALUATION_SERVICE_NAME = "http://disclosureService";

	@Configuration
	@ConditionalOnProperty(prefix = "application.micorservice.contextpath", name = "disclosureService")
	@EnableConfigurationProperties(ContextPathConfiguration.class)
	class AutoConfig {

		@Autowired
		private ContextPathConfiguration config;

		@PostConstruct
		private void setConfig() {
			if(!config.isSingle()) {
				VALUATION_SERVICE_NAME = VALUATION_SERVICE_NAME + config.getDisclosureService();
			}
			else {
				VALUATION_SERVICE_NAME = config.getDisclosureService();

			}
		}
	}



	@ConfigurationProperties(prefix = "application.micorservice.contextpath")
    class ContextPathConfiguration {

        private boolean single;

		private String disclosureService;

		public boolean isSingle() {
			return single;
		}

		public void setSingle(boolean single) {
			this.single = single;
		}

		public String getDisclosureService() {
			return disclosureService;
		}

		public void setDisclosureService(String disclosureService) {
			this.disclosureService = disclosureService;
		}

    }

}
