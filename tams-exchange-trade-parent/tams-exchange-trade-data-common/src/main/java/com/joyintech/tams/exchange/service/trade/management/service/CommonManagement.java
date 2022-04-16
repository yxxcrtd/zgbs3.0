package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.syspara.dto.SysParaSet001Dto;
import com.joyintech.system.syspara.service.SysParaSet001Service;
import com.joyintech.tams.common.base.entity.TrdPriceTimeConfig;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.SysParamBean;
import com.joyintech.tams.exchange.common.bean.investment.PtlAuthInfo;
import com.joyintech.tams.exchange.common.bean.investment.TradeTimeBean;
import com.joyintech.tams.exchange.common.bean.request.DeviationDegreeRequest;
import com.joyintech.tams.exchange.common.bean.request.EtrDispatcherInfoRequest;
import com.joyintech.tams.exchange.common.bean.response.ExchangeContextResponse;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.cache.config.DataServiceConfig;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.ExchangeConstant;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.ServiceNode;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.InvestmentUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.TradeTimeConvertUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.TrdPriceTimeConfigByManualMapper;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.ServiceNodeService;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.scheduled.TokenJob;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeOtherCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommonManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(CommonManagement.class);
    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private ServiceNodeService serviceNodeService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private TokenJob tokenJob;
    @Autowired
    private DataServiceConfig dataServiceConfig;
    @Autowired
    private TradeTimeService tradeTimeService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private AccountPositionHelper accountPositionHelper;
    @Autowired
    private TrdPriceTimeConfigByManualMapper trdPriceTimeConfigByManualMapper;
    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;
    private String fairCode = ExchangeConstant.TRUE_IN_PARA;

    private String platDateStr = DateUtil.getSysDate();

    private String tradingDay = "";

    private String riskApprovalCode = ExchangeConstant.TRUE_IN_PARA;

    private String tradeTimeSwitch = ExchangeConstant.ETR_TRADE_TIME_SWITCH;
    //TODO 交易单独设置时间开关
    /*private String tradeTimeSwitch = ExchangeConstant.TRD_TRADE_TIME_SWITCH;*/

    private String fairModeEntrustPriceBestThanInstruction = ExchangeConstant.TRUE_IN_PARA;

    private String fairModeWithUnacceptInstruction = ExchangeConstant.TRUE_IN_PARA;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private SysParaSet001Service sysParaSet001Service;

    public String getFairCode() {
        return fairCode;
    }

    public void setFairCode(String fairCode) {
        this.fairCode = fairCode;
    }

    public String getTradeTimeSwitch() {
        return tradeTimeSwitch;
    }

    public void setTradeTimeSwitch(String tradeTimeSwitch) {
        this.tradeTimeSwitch = tradeTimeSwitch;
    }

    public String getPlatDateStr() {
        return platDateStr;
    }

    public void setPlatDateStr(String platDateStr) {
        this.platDateStr = platDateStr;
        this.tradingDay = this.platDateStr.replace("-", "");
    }

    public Date getPlatDate() {
        try {
            if (StringUtil.isEmpty(platDateStr)) {
                platDateStr = queryPlatDate();
                setPlatDateStr(platDateStr);
            }
            return dateFormat.parse(platDateStr);
        } catch (ParseException e) {
            logger.error("convertDateFormat error: {}", e.getMessage());
            return null;
        }
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public String getRiskApprovalCode() {
        return riskApprovalCode;
    }

    public void setRiskApprovalCode(String riskApprovalCode) {
        this.riskApprovalCode = riskApprovalCode;
    }

    public String getFairModeEntrustPriceBestThanInstruction() {
        return fairModeEntrustPriceBestThanInstruction;
    }

    public void setFairModeEntrustPriceBestThanInstruction(String fairModeEntrustPriceBestThanInstruction) {
        this.fairModeEntrustPriceBestThanInstruction = fairModeEntrustPriceBestThanInstruction;
    }

    public String getFairModeWithUnacceptInstruction() {
        return fairModeWithUnacceptInstruction;
    }

    public void setFairModeWithUnacceptInstruction(String fairModeWithUnacceptInstruction) {
        this.fairModeWithUnacceptInstruction = fairModeWithUnacceptInstruction;
    }

    public String queryFairTradeParam() {
        return CacheUtil.getSysParam(ExchangeConstant.FAIR_CODE_IN_PARA);
    }

    public ExchangeContextResponse queryExchangeContext() {
        ExchangeContextResponse response = new ExchangeContextResponse();

        User user = SecurityUtil.getLoginUser();
        response.setUserId(Objects.isNull(user) ? "zzz1" : user.getUserName());
        response.setUserName(Objects.isNull(user) ? "zzz1" : user.getRealName());

        response.setPlatDate(DateUtil.platDateFromCache());
        response.setShTradingDay(checkTradeDate("3", DateUtil.getSysDate()));
        response.setSzTradingDay(checkTradeDate("4", DateUtil.getSysDate()));

        response.setFairMode(CacheUtil.getSysParam(ExchangeConstant.FAIR_CODE_IN_PARA));
        response.setFairModeWithUnacceptInstruction(CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_WITH_UNACCEPT_INSTRUCTION_IN_PARA));
        response.setFairModeEntrustPriceBestThanInstruction(CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION_IN_PARA));
        response.setTradeTimeSwitch(CacheUtil.getSysParam(ExchangeConstant.ETR_TRADE_TIME_SWITCH));
        // moddify by etliu 20220301 bond_traded_unit系统参数删除，默认张 start
//        response.setBondTradedUnit(CacheUtil.getSysParam(ExchangeConstant.BOND_TRADED_UNIT));
        response.setBondTradedUnit("01");
        // moddify by etliu 20220301 end

        response.setTrdInstructionRefreshTime(CacheUtil.getSysParam(ExchangeConstant.TRD_INSTRUCTION_REFRESH_TIME));
        response.setTrdQuoteRefreshTime(CacheUtil.getSysParam(ExchangeConstant.TRD_QUOTE_REFRESH_TIME));
        response.setTrdEntrustRefreshTime(CacheUtil.getSysParam(ExchangeConstant.TRD_ENTRUST_REFRESH_TIME));

        return response;
    }

    /**
     * 查询输入日期是否交易日
     *
     * @param tradeCalendarType 交易日历类型  3-上交所; 4-深交所
     * @param CDATE             日期 格式：yyyyMMdd，如20200512
     * @return 是否交易日 0-是；1-否
     */
    public String checkTradeDate(String tradeCalendarType, String CDATE) {
        try {
            String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.checkTradeDate;
            String result = "";
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeCalendarType", tradeCalendarType);
            pageParam.put("CDATE", CDATE);
            if (isSingle) {
                result = simpleRestTemplate.postForObject(url, pageParam, String.class);
            } else {
                result = restTemplate.postForObject(url, pageParam, String.class);
            }
            return JSON.parseObject(result).getString("tradeDayFlag");
        } catch (Exception e) {
            logger.error("取交易日调用投管接口出错");
            return StrUtil.EMPTY;
        }
    }

    public List<SysParaSet001Dto> querySysPara(Map<String, Object> pageParam) {
        try {
            return sysParaSet001Service.getReportInfoList(pageParam);
        } catch (Exception e) {
            logger.warn("get fairCode form investment error {}", e);
            SysParaSet001Dto sysParaSet001Dto = new SysParaSet001Dto();
            sysParaSet001Dto.setParamValue(fairCode);
            ArrayList<SysParaSet001Dto> sysParaSet001DtoList = new ArrayList<>();
            sysParaSet001DtoList.add(sysParaSet001Dto);
            return sysParaSet001DtoList;
        }
    }

    public ResultBean queryInstrument(String market, String assetType, String keyword) {
        List<Instrument> instrumentList;
        if (!Strings.isNullOrEmpty(assetType)) {
            instrumentList = instrumentService.queryByAssetType(assetType);
            return new ResultBean(ErrorCode.SUCCESS, filterInstrumentByKeyword(instrumentList, keyword));
        }
        if (!Strings.isNullOrEmpty(market)) {
            instrumentList = instrumentService.queryByMarket(market);
            return new ResultBean(ErrorCode.SUCCESS, filterInstrumentByKeyword(instrumentList, keyword));
        }
        instrumentList = instrumentService.findAll();
        return new ResultBean(ErrorCode.SUCCESS, filterInstrumentByKeyword(instrumentList, keyword));
    }

    private List<Instrument> filterInstrumentByKeyword(List<Instrument> instrumentList, String keyword) {
        if (Strings.isNullOrEmpty(keyword)) return instrumentList;
        List<Instrument> instruments = instrumentList.stream().filter(instrument -> {
            if (instrument.getInnerCode().contains(keyword)) return true;
            else return instrument.getStockName().contains(keyword);
        }).collect(Collectors.toList());
        return instruments.size() > 10 ? instruments.subList(0, 10) : instruments;
    }

    public ResultBean queryFuturesInstrument(String stockCode) {
        if (Strings.isNullOrEmpty(stockCode)) {
            List<FuturesInstrument> futuresInstruments = futuresInstrumentService.findAll();
            return new ResultBean(ErrorCode.SUCCESS, futuresInstruments);

        } else {
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(stockCode);
            if (futuresInstrument != null) {
                List<FuturesInstrument> futuresInstruments = new ArrayList();
                futuresInstruments.add(futuresInstrument);
                return new ResultBean(ErrorCode.SUCCESS, futuresInstruments);
            } else {
                return new ResultBean(ErrorCode.REQ_PARAMETER_STOCK_CODE_ERROR);
            }
        }
    }

    public List<ServiceNode> queryServiceNode(String nodeName) {
        List<ServiceNode> serviceNodeList;
        if (Strings.isNullOrEmpty(nodeName)) {
            serviceNodeList = serviceNodeService.findAll();
        } else {
            serviceNodeList = Arrays.asList(serviceNodeService.findByNodeName(nodeName));
        }
        return serviceNodeList;
    }

    public List<ServiceInstance> getEurekaServices(String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }

    public ResultBean queryMaster() {
        if (tokenJob.enableToken) {
            return new ResultBean(ErrorCode.SUCCESS, TokenJob.masterAddress);
        } else
            return new ResultBean(ErrorCode.MASTER_TOKEN_DISABLE, TokenJob.masterAddress);
    }

    public ResultBean setDataServiceConfig(String userId, boolean asyncDb, boolean useRedis) {
        logger.info("user {} change asyncDb config from {} to {}", userId, dataServiceConfig.isAsyncDb(), asyncDb);
        dataServiceConfig.setAsyncDb(asyncDb);
        logger.info("user {} change useRedis config from {} to {}", userId, dataServiceConfig.isUseRedis(), useRedis);
        dataServiceConfig.setUseRedis(useRedis);
        return new ResultBean(ErrorCode.SUCCESS, "是否异步db ： " + asyncDb + "是否使用redis： " + useRedis);
    }

    public Date queryInvestSystemDate() {
        return DateUtil.platDateFromCache();
    }

    public String queryPlatDate() {
        String result = "";
        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.querySysDate;
        logger.debug("调用微服务获取平台日[{}] ", url);
        logger.debug("请求报文[]");
        if (isSingle) {
            result = simpleRestTemplate.postForObject(url, null, String.class);
        } else {
            result = restTemplate.postForObject(url, null, String.class);
        }
        logger.debug("应答报文[{}]", result);
        return JSONUtil.parseObj(result).getStr("sysDate");
    }

    public String querySysParam(SysParamBean bean) {
        String result = "";
        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.querySysParam;
        logger.debug("调用微服务获取系统参数[{}] ", url);
        logger.debug("请求报文[{}]", JSONUtil.toJsonStr(bean));
        if (isSingle) {
            result = simpleRestTemplate.postForObject(url, bean, String.class);
        } else {
            result = restTemplate.postForObject(url, bean, String.class);
        }
        logger.debug("应答报文[{}]", result);
        return JSONUtil.parseObj(result).getStr("paramValue");
    }

    public ResultBean getTradeDate(String tradeCalendarType, String CDATE, int NDay) {
        try {
            String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryTradeDate;
            String result = "";
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeCalendarType", tradeCalendarType);
            pageParam.put("CDATE", CDATE);
            pageParam.put("NDay", NDay);
            if (isSingle) {
                result = simpleRestTemplate.postForObject(url, pageParam, String.class);
            } else {
                result = restTemplate.postForObject(url, pageParam, String.class);
            }
            String tradeDate = JSON.parseObject(result).getString("tradeDate");
            return new ResultBean(ErrorCode.SUCCESS, tradeDate);
        } catch (Exception e) {
            logger.info("取交易日调用投管接口出错");
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }
    }

    public ResultBean queryTradeTime(String tradeMarket, String tradePlat, String assetType, String assetType1, String assetType2, String declarationType, String finProdId) {
        try {
            String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryTradeTime;
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("tradeMarket", tradeMarket);
            pageParam.put("tradePlat", tradePlat);
            pageParam.put("assetType", assetType);
            pageParam.put("assetType1", assetType1);
            pageParam.put("assetType2", assetType2);
            pageParam.put("declarationType", declarationType);
            pageParam.put("finprodId", finProdId);
            ResultBean resultBean;
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(url, pageParam, ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(url, pageParam, ResultBean.class);
            }
            return resultBean;
        } catch (Exception e) {
            logger.info("调用交易时间段查询接口失败！！！ {}", e);
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }
    }

    public ResultBean tradeTimeCache() {
        logger.info("开始更新交易时间信息到内存...");
        ResultBean resultBean = queryTradeTime(null, null, null, null, null, null, null);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<TradeTimeBean> tradeTimeBeans = JsonUtils.getObjectListFromJson(JSONArray.toJSONString(resultBean.getData()), TradeTimeBean.class);
            if (Objects.nonNull(tradeTimeBeans)) {
                tradeTimeBeans.forEach(tradeTimeBean -> {
                    tradeTimeBean.setMarket(InvestmentUtils.convertInvestMarketToTrade(tradeTimeBean.getTradeMarket()));
                    tradeTimeBean.setEarlyCloseTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getEarlyCloseTime()));
                    tradeTimeBean.setEarlyOpenTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getEarlyOpenTime()));
                    tradeTimeBean.setNoonOpenTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getNoonOpenTime()));
                    tradeTimeBean.setNoonCloseTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getNoonCloseTime()));
                    tradeTimeBean.setNightOpenTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getNightOpenTime()));
                    tradeTimeBean.setNightCloseTime(TradeTimeConvertUtils.convertTradeTime(tradeTimeBean.getNightCloseTime()));
                });
                tradeTimeService.saveTradeTimeCache(tradeTimeBeans);
            }
            return new ResultBean(ErrorCode.SUCCESS);
        } else {
            logger.info("交易时间段获取失败，未能成功存入信息！！ {} - {}", resultBean.getErrorCode(), resultBean.getErrorMsg());
            return new ResultBean(ErrorCode.FLUSH_TRADE_TIME_FAILED);
        }
    }

    public ResultBean setPositionCheckFlag(String userId, boolean positionCheckFlag) {
        logger.info("改变账户持仓检查状态 {} to {}", accountPositionHelper.isPositionCheckFlag(), positionCheckFlag);
        accountPositionHelper.setPositionCheckFlag(positionCheckFlag);
        return new ResultBean(ErrorCode.SUCCESS, "切换成功");
    }

    /*** 
     * @Description: 组合权限查询
     * @Param: [userId]
     * @return: java.util.List<java.lang.String>
     * @Author: MAXX
     * @Date: 2020/12/21
     */
    public List<String> getPtlAuthInfo(String userId) {
        try {
            ResultBean resultBean;
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("userId", userId);
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + TradeServiceURL.getPtlAuthInfo, pageParam, ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + TradeServiceURL.getPtlAuthInfo, pageParam, ResultBean.class);
            }
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                List<PtlAuthInfo> array = JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), PtlAuthInfo.class);
                if (Objects.isNull(array) || array.size() == 0) {
                    return null;
                }
                List<String> list = new ArrayList<String>();
                array.forEach(obj -> list.add(obj.getPortfolioCode()));
                return list;
            }
        } catch (Exception e) {
            logger.error("getPtlAuthInfo error:", e);
        }
        return null;
    }

    public Message<Map<String, Object>> getEtrDispatcherInfo(EtrDispatcherInfoRequest request) {
        try {
            String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getEtrDispatcherInfo + request.getBusiTypePosition();
            Message<Map<String, Object>> response = null;
            if (isSingle) {
                response = simpleRestTemplate.postForObject(url, request, Message.class);
            } else {
                response = restTemplate.postForObject(url, request, Message.class);
            }
            return response;
        } catch (Exception e) {
            logger.error("调用指令明细信息查询接口失败，" + e.getMessage(), e);
            return new Message<>(ResultBean.error(ErrorCode.API_RETURN_ERROR));
        }
    }

    public Message<String> getDeviationDegree(DeviationDegreeRequest request) {
        try {
            String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getDeviationDegree;
            Map<String, Object> pageParam = BeanUtil.object2Map(request);
            Message<String> result = null;
            if (isSingle) {
                result = simpleRestTemplate.postForObject(url, pageParam, Message.class);
            } else {
                result = restTemplate.postForObject(url, pageParam, Message.class);
            }
            return result;
        } catch (Exception e) {
            logger.error("调用偏离度查询接口失败，" + e.getMessage(), e);
            return new Message<>(ResultBean.error(ErrorCode.API_RETURN_ERROR));
        }
    }

    @Autowired
    private EtrTradeOtherCommonService etrTradeOtherCommonService;

    public Message<BigDecimal> getFaceValue(String finprodId) {
        Map<String, Object> params = new HashMap<>();
        params.put("finprodId", finprodId);
        BigDecimal faceValue = etrTradeOtherCommonService.selectFaceValue(params);
        return new Message<>(faceValue);
    }
}
