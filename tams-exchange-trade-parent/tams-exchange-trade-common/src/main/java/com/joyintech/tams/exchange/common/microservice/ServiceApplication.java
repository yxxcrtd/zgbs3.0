
/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.common.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 微服务访问路径定义
 *
 * @author
 * @version 2.0 2018年9月8日
 * @see ServiceApplication
 * @since 1.0
 */
@Configuration("exchangeServiceApplication")
public class ServiceApplication {

    /**
     * 投管服务
     */
    public static String INVESTMENT_MANAGEMENT_SERVICE = "";

    /**
     * 场内交易
     */
    public static String EXCHANGE_SERVICE_NAME = "http://exchangeService";

    /**
     * 场内交易行情
     */
    public static String QUOTE_SERVICE_NAME = "http://quoteService";

    /**
     * 投管风控服务
     */
    public static String CUSTOMER_INTERFACE_NAME = "http://customerInterfService";

    /**
     * 投管基础数据服务
     */
    public static String BASIC_DATA_SERVICE = "http://basicDataService";

    /**
     * 投资委托
     */
    public static String INSTRUCTION_SERVICE = "http://entrustService";

    /**
     * 投资交易
     */
    public static String TRADE_SERVICE_NAME = "http://tradeService";

    public static String PORTFOLIO_SERVICE_NAME = "http://portfolioService";//组合权限

    @Configuration
    @ConditionalOnProperty(prefix = "application.micorservice.contextpath", name = "entrustService")
    @EnableConfigurationProperties(ContextPathConfiguration.class)
    class AutoConfig {

        @Autowired
        private ContextPathConfiguration config;

        @PostConstruct
        private void setConfig() {
            if (config.isSingle()) {
                EXCHANGE_SERVICE_NAME = config.getExchangeService();
                QUOTE_SERVICE_NAME = config.getQuoteService();
                CUSTOMER_INTERFACE_NAME = config.getCustomerInterfService();
                BASIC_DATA_SERVICE = config.getBasicDataService();
                INSTRUCTION_SERVICE = config.getEntrustService();
                TRADE_SERVICE_NAME = config.getTradeService();
                PORTFOLIO_SERVICE_NAME = config.getPortfolioService();
            }
        }
    }

    @ConfigurationProperties(prefix = "application.micorservice.contextpath")
    public static
    class ContextPathConfiguration {

        private boolean single;

        private String exchangeService;

        private String quoteService;

        private String customerInterfService;

        private String basicDataService;

        private String entrustService;
        private String tradeService;

        private String portfolioService;

        public String getQuoteService() {
            return quoteService;
        }

        public void setQuoteService(String quoteService) {
            this.quoteService = quoteService;
        }

        public String getBasicDataService() {
            return basicDataService;
        }

        public void setBasicDataService(String basicDataService) {
            this.basicDataService = basicDataService;
        }

        public String getEntrustService() {
            return entrustService;
        }

        public void setEntrustService(String entrustService) {
            this.entrustService = entrustService;
        }

        public boolean isSingle() {
            return single;
        }

        public void setSingle(boolean single) {
            this.single = single;
        }

        public String getExchangeService() {
            return exchangeService;
        }

        public void setExchangeService(String exchangeService) {
            this.exchangeService = exchangeService;
        }

        public String getCustomerInterfService() {
            return customerInterfService;
        }

        public void setCustomerInterfService(String customerInterfService) {
            this.customerInterfService = customerInterfService;
        }

        public String getTradeService() {
            return tradeService;
        }

        public void setTradeService(String tradeService) {
            this.tradeService = tradeService;
        }

        public String getPortfolioService() {
            return portfolioService;
        }

        public void setPortfolioService(String portfolioService) {
            this.portfolioService = portfolioService;
        }
    }
}
