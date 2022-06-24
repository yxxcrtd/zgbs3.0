/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.report.common.constant;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微服务访问路径定义
 * 
 * @author zhangzhongliang
 * @version 2.0 2019年04月19日
 * @see SvrServiceApplication
 * @since 1.0
 */
@Configuration
public class SvrServiceApplication {

    /**
     * 监管报送共通服务
     */
    public static String COMMON_SERVICE_NAME = "http://svrcomService";
    
    /**
     * 中债三期服务
     */
    public static String CHB3_SERVICE_NAME = "http://chb3Service";
    
    /**
     * 中债一期服务
     */
    public static String CHB_SERVICE_NAME = "http://chbService";
    
    /**
     * 人行报表服务
     */
    public static String PBC_SERVICE_NAME = "http://pbcService";

    /**
     * 自定义接口服务
     */
    public static String CUSTOMER_INTERF_NAME = "http://customerInterfService";

    @Configuration
    @ConfigurationProperties(prefix = "application.micorservice.contextpath")
    class AutoConfig {
        private boolean single;

        private String svrcomService;

        private String chb3Service;
        
        private String chbService;
        
        private String pbcService;
        
        private String customerInterfService;

        /**
         * 获取 single
         * 
         * @return single.
         */
        public boolean isSingle() {
            return single;
        }

        /**
         * 设置 single
         * 
         * @param single single
         */
        public void setSingle(boolean single) {
            this.single = single;
        }

        public String getSvrcomService() {
            return svrcomService;
        }

        public void setSvrcomService(String svrcomService) {
            this.svrcomService = svrcomService;
        }

		public String getChb3Service() {
			return chb3Service;
		}

		public void setChb3Service(String chb3Service) {
			this.chb3Service = chb3Service;
		}

		public String getChbService() {
			return chbService;
		}

		public void setChbService(String chbService) {
			this.chbService = chbService;
		}

		public String getPbcService() {
			return pbcService;
		}

		public void setPbcService(String pbcService) {
			this.pbcService = pbcService;
		}
    
		public String getCustomerInterfService() {
			return customerInterfService;
		}

		public void setCustomerInterfService(String customerInterfService) {
			this.customerInterfService = customerInterfService;
		}

        @PostConstruct
        private void setConfig() {
            if(!this.isSingle()) {
            	if(this.getSvrcomService()!=null)
                COMMON_SERVICE_NAME = COMMON_SERVICE_NAME + this.getSvrcomService();
            	if(this.getChb3Service()!=null)
                CHB3_SERVICE_NAME  = CHB3_SERVICE_NAME + this.getChb3Service();
            	if(this.getChbService()!=null)
                CHB_SERVICE_NAME  = CHB_SERVICE_NAME + this.getChbService();
            	if(this.getPbcService()!=null)
                PBC_SERVICE_NAME  = PBC_SERVICE_NAME + this.getPbcService();
            	if(this.getCustomerInterfService()!=null)
            	CUSTOMER_INTERF_NAME = CUSTOMER_INTERF_NAME + this.getCustomerInterfService();
            }
            else {
                COMMON_SERVICE_NAME = this.getSvrcomService();
                CHB3_SERVICE_NAME = this.getChb3Service();
                CHB_SERVICE_NAME = this.getChbService();
                PBC_SERVICE_NAME = this.getPbcService();
                CUSTOMER_INTERF_NAME = this.getCustomerInterfService();
            }
        }
    }


}
