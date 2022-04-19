package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.constant.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * create By MAXX on 2020/10/15
 */
@SuppressWarnings("unused")
@Component
public class InstrumentManagement extends BaseService {
    private Logger logger = LoggerFactory.getLogger(InstrumentManagement.class);
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

    public ResultBean flushInstrumentData(Map<String, Object> params) {
        try {
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
                        if (NetPriceFlag.IS_NET_PRICE_FLAG.value.equals(instrumentFromInvest.getNetPricFlag())) {
                            instrumentFromInvest.setIsFullPrice(FullPriceConst.NET_PRICE.value);
                        } else {
                            instrumentFromInvest.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
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
        return new ResultBean(ErrorCode.SUCCESS);
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

    /***
     * @Description: 合约数据获取
     * @Param: [market, stockCode, pageNo, count] 
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Instrument>
     * @Author: MAXX
     * @Date: 2020/10/15 
     */
    public List<Instrument> getStockInfo(Map<String, Object> params) {
        try {
            logger.info("获取合约信息: params:{}, url:{}{}", params, ServiceApplication.BASIC_DATA_SERVICE ,TradeServiceURL.selectStockInfo);
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
}
