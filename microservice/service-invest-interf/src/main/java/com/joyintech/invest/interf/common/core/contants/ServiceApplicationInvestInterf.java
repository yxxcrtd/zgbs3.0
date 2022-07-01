
/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.invest.interf.common.core.contants;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微服务访问路径定义
 * 
 * @author 何阳阳
 * @version 2.0 2018年9月8日
 * @see ServiceApplicationInvestInterf
 * @since 1.0
 */
@Configuration("investInterfServiceApplication")
public class ServiceApplicationInvestInterf {

    /**
     * 共通服务
     */
    public static String COMMON_SERVICE_NAME = "http://commonService";

    /**
     * 基础数据及行情管理
     */
    public static String BASICDATA_SERVICE_NAME = "http://basicDataService";

    /**
     * 文档管理
     */
    public static String DOCUMENT_SERVICE_NAME = "http://documentService";

    /**
     * 投资委托
     */
    public static String ENTRUST_SERVICE_NAME = "http://entrustService";

    /**
     * 存续期管理
     */
    public static String PERIOD_SERVICE_NAME = "http://periodService";

    /**
     * 资产管理
     */
    public static String ASSET_SERVICE_NAME = "http://assetService";

    /**
     * 组合管理
     */
    public static String PORTFOLIO_SERVICE_NAME = "http://portfolioService";

    /**
     * 产品端管理
     */
    public static String PRODUCT_SERVICE_NAME = "http://productService";

    /**
     * 限额管理
     */
    public static String QUOTA_SERVICE_NAME = "http://quotaService";

    /**
     * 报表管理
     */
    public static String REPORT_SERVICE_NAME = "http://reportService";

    /**
     * 风险管理
     */
    public static String RISK_SERVICE_NAME = "http://riskService";

    /**
     * 后台清算
     */
    public static String SETTLE_SERVICE_NAME = "http://settleService";

    /**
     * 投资交易
     */
    public static String TRADE_SERVICE_NAME = "http://tradeService";

    /**
     * 页面端
     */
    public static String PINGAN_SERVICE_NAME = "http://tams-server";

    @Configuration
    @ConditionalOnProperty(prefix = "application.micorservice.contextpath", name = "commonService")
    @EnableConfigurationProperties(ContextPathConfiguration.class)
    class AutoConfig {

        @Autowired
        private ContextPathConfiguration config;

        @PostConstruct
        private void setConfig() {
            if (!config.isSingle()) {
                COMMON_SERVICE_NAME = COMMON_SERVICE_NAME + config.getCommonService();
                BASICDATA_SERVICE_NAME = BASICDATA_SERVICE_NAME + config.getBasicDataService();
                DOCUMENT_SERVICE_NAME = DOCUMENT_SERVICE_NAME + config.getDocumentService();
                ENTRUST_SERVICE_NAME = ENTRUST_SERVICE_NAME + config.getEntrustService();
                PERIOD_SERVICE_NAME = PERIOD_SERVICE_NAME + config.getPeriodService();
                ASSET_SERVICE_NAME = ASSET_SERVICE_NAME + config.getAssetService();
                PORTFOLIO_SERVICE_NAME = PORTFOLIO_SERVICE_NAME + config.getPortfolioService();
                PRODUCT_SERVICE_NAME = PRODUCT_SERVICE_NAME + config.getProductService();
                QUOTA_SERVICE_NAME = QUOTA_SERVICE_NAME + config.getQuotaService();
                REPORT_SERVICE_NAME = REPORT_SERVICE_NAME + config.getReportService();
                RISK_SERVICE_NAME = RISK_SERVICE_NAME + config.getRiskService();
                SETTLE_SERVICE_NAME = SETTLE_SERVICE_NAME + config.getSettleService();
                TRADE_SERVICE_NAME = TRADE_SERVICE_NAME + config.getTradeService();
                PINGAN_SERVICE_NAME = PINGAN_SERVICE_NAME + config.getTamsServer();
            }
            else {
                COMMON_SERVICE_NAME = config.getCommonService();
                BASICDATA_SERVICE_NAME = config.getBasicDataService();
                DOCUMENT_SERVICE_NAME = config.getDocumentService();
                ENTRUST_SERVICE_NAME = config.getEntrustService();
                PERIOD_SERVICE_NAME = config.getPeriodService();
                ASSET_SERVICE_NAME = config.getAssetService();
                PORTFOLIO_SERVICE_NAME = config.getPortfolioService();
                PRODUCT_SERVICE_NAME = config.getProductService();
                QUOTA_SERVICE_NAME = config.getQuotaService();
                REPORT_SERVICE_NAME = config.getReportService();
                RISK_SERVICE_NAME = config.getRiskService();
                SETTLE_SERVICE_NAME = config.getSettleService();
                TRADE_SERVICE_NAME = config.getTradeService();
                PINGAN_SERVICE_NAME = config.getTamsServer();
            }
        }
    }

    @ConfigurationProperties(prefix = "application.micorservice.contextpath")
    class ContextPathConfiguration {

        private boolean single;

        private String commonService;

        private String basicDataService;

        private String documentService;

        private String entrustService;

        private String periodService;

        private String assetService;

        private String portfolioService;

        private String channelService;

        private String productService;

        private String quotaService;

        private String reportService;

        private String riskService;

        private String settleService;

        private String tradeService;

        private String tamsServer;

        public String getTamsServer() {
            return tamsServer;
        }

        public void setTamsServer(String tamsServer) {
            this.tamsServer = tamsServer;
        }

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

        public String getDocumentService() {
            return documentService;
        }

        public void setDocumentService(String documentService) {
            this.documentService = documentService;
        }

        public String getEntrustService() {
            return entrustService;
        }

        public void setEntrustService(String entrustService) {
            this.entrustService = entrustService;
        }

        public String getPeriodService() {
            return periodService;
        }

        public void setPeriodService(String periodService) {
            this.periodService = periodService;
        }

        public String getAssetService() {
            return assetService;
        }

        public void setAssetService(String assetService) {
            this.assetService = assetService;
        }

        public String getPortfolioService() {
            return portfolioService;
        }

        public void setPortfolioService(String portfolioService) {
            this.portfolioService = portfolioService;
        }

        public String getProductService() {
            return productService;
        }

        public void setProductService(String productService) {
            this.productService = productService;
        }

        public String getQuotaService() {
            return quotaService;
        }

        public void setQuotaService(String quotaService) {
            this.quotaService = quotaService;
        }

        public String getReportService() {
            return reportService;
        }

        public void setReportService(String reportService) {
            this.reportService = reportService;
        }

        public String getRiskService() {
            return riskService;
        }

        public void setRiskService(String riskService) {
            this.riskService = riskService;
        }

        public String getSettleService() {
            return settleService;
        }

        public void setSettleService(String settleService) {
            this.settleService = settleService;
        }

        public String getTradeService() {
            return tradeService;
        }

        public void setTradeService(String tradeService) {
            this.tradeService = tradeService;
        }

        /**
         * 获取 commonService
         * 
         * @return commonService.
         */
        public String getCommonService() {
            return commonService;
        }

        /**
         * 设置 commonService
         * 
         * @param commonService commonService
         */
        public void setCommonService(String commonService) {
            this.commonService = commonService;
        }

        /**
         * 获取 basicDataService
         * 
         * @return basicDataService.
         */
        public String getBasicDataService() {
            return basicDataService;
        }

        /**
         * 设置 basicDataService
         * 
         * @param basicDataService basicDataService
         */
        public void setBasicDataService(String basicDataService) {
            this.basicDataService = basicDataService;
        }

        /**
         * 获取 channelService
         * 
         * @return channelService.
         */
        public String getChannelService() {
            return channelService;
        }

        /**
         * 设置 channelService
         * 
         * @param channelService channelService
         */
        public void setChannelService(String channelService) {
            this.channelService = channelService;
        }

    }

}
