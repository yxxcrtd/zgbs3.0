
/*
 * 文件名：ServiceApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.constant;

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
@Configuration("investServiceApplication")
public class ServiceApplication {

    /**
     * 适配器服务
     */
    public static String ADAPTER_SERVICE_NAME = "http://adapterService";

    /**
     * 自定义接口服务
     */
    public static String CUSTOMER_INTERF_NAME = "http://customerInterfService";

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
     * 风险管理(授信授权等)
     */
    public static String RISK_SERVICE_NAME = "http://riskService";

    /**
     * 风险管理(Redis)
     */
    public static String RISK_RDS_SERVICE_NAME = "http://riskRedisService";

    /**
     * 风险管理(接口)
     */
    public static String RISK_INF_SERVICE_NAME = "http://riskRedisInfService";

    /**
     * 后台清算
     */
    public static String SETTLE_SERVICE_NAME = "http://settleService";

    /**
     * 投资交易
     */
    public static String TRADE_SERVICE_NAME = "http://tradeService";

    /**
     * 投管接口
     */
    public static String INVEST_INTERF_SERVICE_NAME = "http://investInterfService";

    /**
     * 中债服务
     */
    public static String CHB_SERVICE_NAME = "http://chbService";

    /**
     * 交易系统接口
     */
    public static String EXCHANGE_SERVICE_NAME = "http://exchangeService";

    /**
     * 行情系统接口
     */
    public static String QUOTE_SERVICE_NAME = "http://quoteService";

    /**
     * 银行间上行
     */
    public static String BANKUPLINK_SERVICE_NAME = "http://bankuplinkService";

    /**
     * 页面端
     */
    public static String PINGAN_SERVICE_NAME = "http://tams-server";
    // 批处理调用画面端服务增加 add by renping 2019-07-31 start
    /**
     * 投管页面SERVLET_CONTEXT_PATH
     */
    public static String SERVLET_CONTEXT_PATH = "/tams";
    // 批处理调用画面端服务增加 add by renping 2019-07-31 end

    /**
     * 接口平台接口 add by czj 20210416
     */
    public static String INTERF_PLACE_SERVICE_NAME = "http://interfPlaceService";

    @Configuration
    @ConditionalOnProperty(prefix = "application.micorservice.contextPath", name = "commonService")
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
                RISK_RDS_SERVICE_NAME = RISK_RDS_SERVICE_NAME + config.getRiskRdsService();
                RISK_INF_SERVICE_NAME = RISK_INF_SERVICE_NAME + config.getRiskInfService();
                SETTLE_SERVICE_NAME = SETTLE_SERVICE_NAME + config.getSettleService();
                TRADE_SERVICE_NAME = TRADE_SERVICE_NAME + config.getTradeService();
                CUSTOMER_INTERF_NAME = CUSTOMER_INTERF_NAME + config.getCustomerInterfService();
                PINGAN_SERVICE_NAME = PINGAN_SERVICE_NAME + config.getTamsServer();
                INVEST_INTERF_SERVICE_NAME = INVEST_INTERF_SERVICE_NAME + config.getInvestInterfService();

                CHB_SERVICE_NAME = CHB_SERVICE_NAME + config.getChbService();
                EXCHANGE_SERVICE_NAME = EXCHANGE_SERVICE_NAME + config.getExchangeService();
                QUOTE_SERVICE_NAME = EXCHANGE_SERVICE_NAME + config.getQuoteService();
                BANKUPLINK_SERVICE_NAME = BANKUPLINK_SERVICE_NAME + config.getBankuplinkService();
                INTERF_PLACE_SERVICE_NAME = INTERF_PLACE_SERVICE_NAME + config.getInterfPlaceService();
            } else {
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
                RISK_RDS_SERVICE_NAME = config.getRiskRdsService();
                RISK_INF_SERVICE_NAME = config.getRiskInfService();
                SETTLE_SERVICE_NAME = config.getSettleService();
                TRADE_SERVICE_NAME = config.getTradeService();
                CUSTOMER_INTERF_NAME = config.getCustomerInterfService();
                PINGAN_SERVICE_NAME = config.getTamsServer();
                INVEST_INTERF_SERVICE_NAME = config.getInvestInterfService();

                CHB_SERVICE_NAME = config.getChbService();
                EXCHANGE_SERVICE_NAME = config.getExchangeService();
                QUOTE_SERVICE_NAME = config.getQuoteService();
                BANKUPLINK_SERVICE_NAME = config.getBankuplinkService();
                INTERF_PLACE_SERVICE_NAME = config.getInterfPlaceService();

            }
        }
    }

    @ConfigurationProperties(prefix = "application.micorservice.contextPath")
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

        private String riskRdsService;

        private String riskInfService;

        private String settleService;

        private String tradeService;

        private String customerInterfService;

        private String tamsServer;

        /**
         * 投管接口
         */
        private String investInterfService;

        private String chbService;

        private String exchangeService;

        private String quoteService;

        private String bankuplinkService;

        private String interfPlaceService;

        public String getInterfPlaceService() {
            return interfPlaceService;
        }

        public void setInterfPlaceService(String interfPlaceService) {
            this.interfPlaceService = interfPlaceService;
        }

        /**
         * 获取 investInterfService
         *
         * @return investInterfService.
         */
        public String getInvestInterfService() {
            return investInterfService;
        }

        /**
         * 设置 investInterfService
         *
         * @param investInterfService investInterfService
         */
        public void setInvestInterfService(String investInterfService) {
            this.investInterfService = investInterfService;
        }

        public String getTamsServer() {
            return tamsServer;
        }

        public void setTamsServer(String tamsServer) {
            this.tamsServer = tamsServer;
        }

        public String getCustomerInterfService() {
            return customerInterfService;
        }

        public void setCustomerInterfService(String customerInterfService) {
            this.customerInterfService = customerInterfService;
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

        public String getRiskRdsService() {
            return riskRdsService;
        }

        public void setRiskRdsService(String riskRdsService) {
            this.riskRdsService = riskRdsService;
        }

        public String getRiskInfService() {
            return riskInfService;
        }

        public void setRiskInfService(String riskInfService) {
            this.riskInfService = riskInfService;
        }

        public String getChbService() {
            return chbService;
        }

        public void setChbService(String chbService) {
            this.chbService = chbService;
        }

        public String getExchangeService() {
            return exchangeService;
        }

        public void setExchangeService(String exchangeService) {
            this.exchangeService = exchangeService;
        }

        public String getQuoteService() {
            return quoteService;
        }

        public void setQuoteService(String quoteService) {
            this.quoteService = quoteService;
        }

        public String getBankuplinkService() {
            return bankuplinkService;
        }

        public void setBankuplinkService(String bankuplinkService) {
            this.bankuplinkService = bankuplinkService;
        }
    }

}
