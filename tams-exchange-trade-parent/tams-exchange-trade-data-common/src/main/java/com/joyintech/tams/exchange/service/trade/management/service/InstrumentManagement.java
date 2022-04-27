package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.cache.MinPriceService;
import com.joyintech.tams.exchange.common.cache.MinTrdUnitLimitService;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.InvestmentUtils;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.business.futures.FuturesHelper;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.joyintech.tams.exchange.common.constant.FullPriceConst.FULL_PRICE;
import static com.joyintech.tams.exchange.common.constant.FullPriceConst.NET_PRICE;
import static com.joyintech.tams.exchange.common.constant.NetPriceFlag.IS_NET_PRICE_FLAG;

/**
 * create By MAXX on 2020/10/15
 */
@SuppressWarnings("all")
@Component
public class InstrumentManagement extends BaseService {
    private final Logger logger = LoggerFactory.getLogger(InstrumentManagement.class);
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private TerminalHelper terminalHelper;
    @Autowired
    private FuturesHelper futuresHelper;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;
    @Autowired
    private MinPriceService minPriceService;
    @Autowired
    private MinTrdUnitLimitService minTrdUnitLimitService;

    public ResultBean flushInstrumentData(Map<String, Object> params) {
        return updateInstrument2Cache(params);
        /*try {
            List<Instrument> instrumentList = new ArrayList<>();
            List<FuturesInstrument> futuresInstrumentList = new ArrayList<>();
            List<Instrument> instrumentListFromInvest = getStockInfo(params);
            logger.debug("list:{}", JSONArray.toJSONString(instrumentListFromInvest));
            if (CollUtil.isNotEmpty(instrumentListFromInvest)) {
                List<MinPrice> minPriceList = getMinPrice();
                logger.debug("minPriceList:{}", JSONArray.toJSONString(minPriceList));
                if (Objects.nonNull(minPriceList)) {
                    instrumentService.saveMinPrice(minPriceList);
                }
                List<MinTrdUnitLimit> minTrdUnitLimitList = getMinTrdUnitLimit();
                logger.debug("minTrdUnitLimitList:{}", JSONArray.toJSONString(minTrdUnitLimitList));
                if (Objects.nonNull(minTrdUnitLimitList)) {
                    instrumentService.saveTrdUnitLimit(minTrdUnitLimitList);
                }
                instrumentListFromInvest.forEach(instrumentFromInvest -> {
                    String assetType = instrumentFromInvest.getAssetType();
                    if (InvestmentAssetType.isFutures(assetType)) {
                        FuturesInstrument futuresInstrument = new FuturesInstrument();
                        futuresHelper.setFuturesInstrumentData(instrumentFromInvest, futuresInstrument);
                        futuresInstrumentList.add(futuresInstrument);
                    } else {
                        instrumentFromInvest.setMarket(InvestmentUtils.convertInvestMarketToTrade(instrumentFromInvest.getMarket()));
                        MinPrice minPrice = instrumentService.getMinPrice(TradePlat.NORMAL.value, instrumentFromInvest.getMarket(), instrumentFromInvest);
                        if (Objects.nonNull(minPrice)) {
                            instrumentFromInvest.setPriceStep(minPrice.getMinPrice());
                        }
                        MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(TradePlat.NORMAL.value, instrumentFromInvest.getMarket(), instrumentFromInvest, InstructionPs.BUY.value);
                        if (Objects.nonNull(minTrdUnitLimit)) {
                            instrumentFromInvest.setBuyUnit(minTrdUnitLimit.getMinStep());
                            instrumentFromInvest.setHighVolume(Objects.nonNull(minTrdUnitLimit.getMaxNum()) ? Long.valueOf(minTrdUnitLimit.getMaxNum()) : null);
                            instrumentFromInvest.setLowVolume(minTrdUnitLimit.getMinNum());
                            instrumentFromInvest.setMarketLowVolume(minTrdUnitLimit.getMinNumMarket());
                            instrumentFromInvest.setMarketHighVolume(minTrdUnitLimit.getMaxNumMarket());
                        }
                        if (IS_NET_PRICE_FLAG.value.equals(instrumentFromInvest.getNetPricFlag())) {
                            instrumentFromInvest.setIsFullPrice(NET_PRICE.value);
                        } else {
                            instrumentFromInvest.setIsFullPrice(FULL_PRICE.value);
                        }
                        instrumentList.add(instrumentFromInvest);
                    }
                });
                logger.info("合约数据 {}", instrumentList.size());
                if (!instrumentList.isEmpty()) instrumentService.asyncUpdate(instrumentList);
                logger.info("期货合约数据 {}", futuresInstrumentList.size());
                if (!futuresInstrumentList.isEmpty()) futuresInstrumentService.asyncUpdate(futuresInstrumentList);
            }
        } catch (Exception e) {
            logger.error("flushInstrumentData:", e);
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }
        logger.info("flushInstrumentData end");
        return new ResultBean(ErrorCode.SUCCESS);*/
    }

    /***
     * @Description: 合约数据获取
     * @Param: [market, stockCode, pageNo, count]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Instrument>
     * @Author: MAXX
     * @Date: 2020/10/15
     */
    public List<Instrument> getStockInfo(Map<String, Object> params) {
        try {
            logger.info("获取合约信息: params:{}, url:{}{}", params, ServiceApplication.BASIC_DATA_SERVICE, TradeServiceURL.selectStockInfo);
            ResultBean resultBean;
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectStockInfo, params, ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectStockInfo, params, ResultBean.class);
            }
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), Instrument.class);
            }
        } catch (Exception e) {
            logger.error("getStockInfo error:", e);
        }
        return ListUtil.empty();
    }

    /***
     * @Description: 最小价差数据查询
     * @Param: []
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.MinPrice>
     * @Author: MAXX
     * @Date: 2020/10/15
     */
    public List<MinPrice> getMinPrice() {
        try {
            ResultBean resultBean;
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinPrice, new HashMap<String, Object>(), ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinPrice, new HashMap<String, Object>(), ResultBean.class);
            }
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), MinPrice.class);
            }
        } catch (Exception e) {
            logger.error("getMinPrice error:", e);
        }
        return ListUtil.empty();
    }

    /***
     * @Description: 最小交易单位、交易上下限数据查询
     * @Param: []
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit>
     * @Author: MAXX
     * @Date: 2020/10/15
     */
    public List<MinTrdUnitLimit> getMinTrdUnitLimit() {
        try {
            ResultBean resultBean;
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinTrdUnitLimit, new HashMap<String, Object>(), ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinTrdUnitLimit, new HashMap<String, Object>(), ResultBean.class);
            }
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), MinTrdUnitLimit.class);
            }
        } catch (Exception e) {
            logger.error("getMinTrdUnitLimit error:", e);
        }
        return ListUtil.empty();
    }

    public ResultBean updateInstrument2Cache(Map<String, Object> params) {
        ResultBean result = ResultBean.success();
        List<Instrument> instruments = Lists.newArrayList();

        List<Instrument> instrumentListFromTg = queryInstruments(params);
        if (instrumentListFromTg.isEmpty()) {
            logger.warn("调用投管接口查询合约信息为空！");
            return result;
        }

        List<MinPrice> minPriceList = queryMinPrices();
        minPriceService.saveInCache(minPriceList);
        List<MinTrdUnitLimit> minTrdUnitLimitList = queryMinTrdUnitLimits();
        instrumentService.saveTrdUnitLimit(minTrdUnitLimitList);

        // 实际不建议使用合约中的这些信息，而是使用在查询指令列表时匹配生成到指令上的信息
        /*instrumentListFromTg.forEach(instrument -> {
            BigDecimal minPrice = minPriceService.getMinPrice(instrument);
            instrument.setPriceStep(minPrice);

            MinTrdUnitLimit minTrdUnitLimit = minTrdUnitLimitService.getMinTrdUnitLimit(instrument, InstructionPs.BUY.getCode());
            instrument.setBuyUnit(minTrdUnitLimit.getMinStep());
            instrument.setHighVolume(Objects.nonNull(minTrdUnitLimit.getMaxNum()) ? Long.valueOf(minTrdUnitLimit.getMaxNum()) : null);
            instrument.setLowVolume(minTrdUnitLimit.getMinNum());
            instrument.setMarketLowVolume(minTrdUnitLimit.getMinNumMarket());
            instrument.setMarketHighVolume(minTrdUnitLimit.getMaxNumMarket());

            instrument.setMarket(InvestmentUtils.convertInvestMarketToTrade(instrument.getMarket()));
            dealFullPriceFlag(instrument);

            instruments.add(instrument);
        });*/

        logger.info("处理合约数据总数 => 【{}】", instruments.size());
        if (!instruments.isEmpty()) {
            instrumentService.asyncUpdate(instruments);
        }

        return new ResultBean(ErrorCode.SUCCESS);
    }

    private void dealFullPriceFlag(Instrument instrument) {
        String netPricFlag = instrument.getNetPricFlag();
        if (StrUtil.isEmpty(netPricFlag)) {
            return;
        }
        if (IS_NET_PRICE_FLAG.codeEquals(netPricFlag)) {
            instrument.setIsFullPrice(NET_PRICE.getCode());
        } else {
            instrument.setIsFullPrice(FULL_PRICE.getCode());
        }
    }

    public ResultBean flushFuturesInstrumentData() {
        try {
            List<FuturesInstrument> futuresInstruments = terminalHelper.queryFuturesInstrument("", "", "");
            futuresInstrumentService.asyncUpdate(futuresInstruments);
        } catch (Exception e) {
            logger.error("flushFuturesInstrumentData:", e);
            return new ResultBean(ErrorCode.API_RETURN_ERROR);
        }
        logger.info("flushFuturesInstrumentData end");
        return new ResultBean(ErrorCode.SUCCESS);
    }

    /**
     * 调用投管接口查询合约数据
     *
     * @param params 查询参数
     * @return 合约列表
     */
    public List<Instrument> queryInstruments(Map<String, Object> params) {

        logger.info("调用投管接口查询合约信息");
        ResultBean resultBean = doPost(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectStockInfo, params);
        if (ErrorCode.isSuccess(resultBean.getErrorCode())) {
            return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), Instrument.class);
        }
        return ListUtil.empty();
    }

    /**
     * 调用投管接口查询最小价差
     *
     * @return 最小价差列表
     */
    private List<MinPrice> queryMinPrices() {
        logger.info("调用投管接口查询最小价差信息");
        ResultBean resultBean = doPost(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinPrice, Maps.newHashMap());
        if (ErrorCode.isSuccess(resultBean.getErrorCode())) {
            return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), MinPrice.class);
        }
        return ListUtil.empty();
    }

    /**
     * 调用投管接口查询最小交易单位、交易上下限
     *
     * @return 最小交易单位、交易上下限列表
     */
    private List<MinTrdUnitLimit> queryMinTrdUnitLimits() {
        logger.info("调用投管接口查询最小交易单位、交易上下限信息");
        ResultBean resultBean = doPost(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinTrdUnitLimit, Maps.newHashMap());
        if (ErrorCode.isSuccess(resultBean.getErrorCode())) {
            return JSONArray.parseArray(JSONArray.toJSONString(resultBean.getData()), MinTrdUnitLimit.class);
        }
        return ListUtil.empty();
    }

    public ResultBean minPrice(String tradePlat, Instrument instrument) {
        MinPrice minPrice = instrumentService.getMinPrice(tradePlat, instrument.getMarket(), instrument);
        return new ResultBean(ErrorCode.SUCCESS, minPrice);
    }

    public ResultBean minTrdUnitLimit(String tradePlat, Instrument instrument, String instructionPs) {
        MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(tradePlat, instrument.getMarket(), instrument, instructionPs);
        return new ResultBean(ErrorCode.SUCCESS, minTrdUnitLimit);
    }

    public ResultBean queryMinPriceAndMinTrdUnitLimit(String stockCode, String tradePlat, String instructionPs) {
        MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean = instrumentService.queryMinPriceAndMinTrdUnitLimit(stockCode, tradePlat, instructionPs);
        return new ResultBean(ErrorCode.SUCCESS, minPriceAndMinTrdUnitLimitBean);
    }

    public void trdUnitLimitFlush() {
        instrumentService.trdUnitLimitFlush();
    }

    private ResultBean doPost(String url, Map<String, Object> params) {

        logger.info("doPost() called with parameters => 【url = {}】, 【params = {}】", url, params);
        ResultBean resultBean;
        try {
            if (isSingle) {
                resultBean = simpleRestTemplate.postForObject(url, params, ResultBean.class);
            } else {
                resultBean = restTemplate.postForObject(url, params, ResultBean.class);
            }
        } catch (Exception e) {
            logger.error("doPost() called with exception => 【url = {}】, 【params = {}】", url, params, e);
            resultBean = ResultBean.error(ErrorCode.API_RETURN_ERROR);
        }
        return resultBean;
    }
}
