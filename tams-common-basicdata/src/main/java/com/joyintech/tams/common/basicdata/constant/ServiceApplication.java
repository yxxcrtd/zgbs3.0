package com.joyintech.tams.common.basicdata.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author jinmaodong
 * @version 1.0 2021年4月15日
 * @see ServiceApplication
 * @since 1.0
 */
@Configuration("commonBasicDataServiceApplication")
public class ServiceApplication {

    /**
     * 公共基础数据服务
     */
    public static String CommonBasicDataService = "http://commonBasicDataService";


    @Configuration
    @ConditionalOnProperty(prefix = "application.micorservice.contextpath", name = "valuationService")
    @EnableConfigurationProperties(ContextPathConfiguration.class)
    class AutoConfig {

        @Autowired
        private ContextPathConfiguration config;

        @PostConstruct
        private void setConfig() {
            if(!config.isSingle()) {
                CommonBasicDataService = CommonBasicDataService + config.getValuationService();
            }
            else {
                CommonBasicDataService = config.getValuationService();
            }
        }
    }



    @ConfigurationProperties(prefix = "application.micorservice.contextpath")
    class ContextPathConfiguration {

        private boolean single;

        private String valuationService;

        private String customerInterfService;

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


    }

}
