package com.joyintech.tams.exchange.datacommon.db.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.TradePlat;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.InstrumentExample;
import com.joyintech.tams.exchange.common.utils.InvestmentUtils;
import com.joyintech.tams.exchange.datacommon.cache.impl.AsyncServiceImpl;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.InstrumentMapper;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2.平安资产类型_股票;

@Service
public class InstrumentServiceImpl extends AsyncServiceImpl<String, Instrument> implements InstrumentService {

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(InstrumentServiceImpl.class);
    private final Map<String, Map<String, Instrument>> marketMap = Maps.newConcurrentMap();
    private final Map<String, Map<String, Instrument>> assetTypeMap = Maps.newConcurrentMap();
    private final Map<String, MinPrice> minPriceMap = Maps.newConcurrentMap();
    private final Map<String, MinTrdUnitLimit> minTrdUnitLimitMap = Maps.newConcurrentMap();
    // 缓存合约下价差、交易上下限数据 --- 仅查询时缓存
    private final Map<String, MinPriceAndMinTrdUnitLimitBean> minPriceAndMinTrdUnitLimitBeanMap = Maps.newConcurrentMap();
    @Autowired
    private InstrumentMapper instrumentMapper;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;

    @Override
    public Instrument queryByInnerCode(String innerCode) {
        return findById(innerCode);
    }

    @Override
    public List<Instrument> queryByMarket(String market) {
        return new ArrayList<>(marketMap.getOrDefault(market, new HashMap<>()).values());
    }

    @Override
    public void clearCache() {
        memory_cache.clear();
        marketMap.clear();
        assetTypeMap.clear();
    }

    @Override
    protected void update(Instrument instrument) {
        save(instrument);
    }

    @Override
    protected void save(Instrument instrument) {
        saveMemoryCache(instrument);
    }

    private void saveMemoryCache(Instrument instrument) {
        Map<String, Instrument> map = marketMap.get(instrument.getMarket());
        if (Objects.isNull(map)) {
            map = new ConcurrentHashMap<>();
            marketMap.put(instrument.getMarket(), map);
        }
        map.put(instrument.getInnerCode(), instrument);
        Map<String, Instrument> assetMap = assetTypeMap.get(instrument.getAssetType());
        if (Objects.isNull(assetMap)) {
            assetMap = new ConcurrentHashMap<>();
            assetTypeMap.put(instrument.getAssetType(), assetMap);
        }
        assetMap.put(instrument.getInnerCode(), instrument);
    }

    @Override
    protected void delete(String id) {
        Instrument instrument = findById(id);
        if (Objects.nonNull(instrument)) {
            marketMap.get(instrument.getMarket()).remove(instrument.getInnerCode());
        }
    }

    @Override
    public IMybatisMapper<Instrument, String, InstrumentExample> getMapper() {
        return instrumentMapper;
    }

    @Override
    protected String genDbId() {
        return null;
    }

    @Override
    public void saveMinPrice(List<MinPrice> minPriceList) {
        log.info("saveMinPrice() called with parameters => 【minPriceList = {}】", minPriceList);
        futuresInstrumentService.clearMinPriceAndMinTrdUnitLimitCache();
        minPriceAndMinTrdUnitLimitBeanMap.clear();
        minPriceMap.clear();
        if (CollUtil.isEmpty(minPriceList)) {
            return;
        }

        String ps = null;
        minPriceList.forEach(minPrice -> {
            minPrice.setMarket(InvestmentUtils.convertInvestMarketToTrade(minPrice.getTradeMarket()));
            ArrayList<Object> list = Lists.newArrayList(minPrice.getMarket(), minPrice.getTradePlat(), ps,
                    minPrice.getAssetType(), minPrice.getAssetType1(), minPrice.getAssetType2());
//            String key = minPrice.getMarket() + "|" + minPrice.getTradePlat() + "||" + minPrice.getAssetType() + "|"
//                    + minPrice.getAssetType1() + "|" + minPrice.getAssetType2();
            minPriceMap.put(Joiner.on(GlobalConstant.VERTICAL_LINE).useForNull(StringUtils.EMPTY).join(list), minPrice);
        });
    }

    @Override
    public void saveTrdUnitLimit(List<MinTrdUnitLimit> minTrdUnitLimitList) {
        log.info("saveTrdUnitLimit() called with parameters => 【minTrdUnitLimitList = {}】", minTrdUnitLimitList);
        futuresInstrumentService.clearMinPriceAndMinTrdUnitLimitCache();
        minPriceAndMinTrdUnitLimitBeanMap.clear();
        minTrdUnitLimitMap.clear();
        if (CollUtil.isEmpty(minTrdUnitLimitList)) {
            return;
        }

        minTrdUnitLimitList.forEach(minTrdUnitLimit -> {
            minTrdUnitLimit.setMarket(InvestmentUtils.convertInvestMarketToTrade(minTrdUnitLimit.getTradeMarket()));
            ArrayList<Object> list = Lists.newArrayList(minTrdUnitLimit.getMarket(), minTrdUnitLimit.getTradePlat(),
                    minTrdUnitLimit.getPs(), minTrdUnitLimit.getAssetType(), minTrdUnitLimit.getAssetType1());
            // 查询时根据合约的数据进行查询。合约上的字段根据 资产类型 有不同取值
            if (minTrdUnitLimit.getAssetType().equals(平安资产类型_股票.getValue())) {
                list.add(minTrdUnitLimit.getMarketType());
            } else {
                list.add(minTrdUnitLimit.getAssetType2());
            }
            minTrdUnitLimitMap.put(Joiner.on(GlobalConstant.VERTICAL_LINE).useForNull(StringUtils.EMPTY).join(list), minTrdUnitLimit);
//            String key = minTrdUnitLimit.getMarket() + "|" + minTrdUnitLimit.getTradePlat() + "|" + minTrdUnitLimit.getPs() + "|"
//                    + minTrdUnitLimit.getAssetType() + "|" + minTrdUnitLimit.getAssetType1() + "|" + minTrdUnitLimit.getAssetType2();
//            minTrdUnitLimitMap.put(key, minTrdUnitLimit);
        });
    }

    @Override
    public MinPrice getMinPrice(String tradePlat, String market, Instrument instrument) {
        String key = createKey(tradePlat, market, instrument, 0, null);
        for (int i = 1; i <= 3; i++) {
            MinPrice minPrice = minPriceMap.get(key);
            if (Objects.nonNull(minPrice)) {
                return minPrice;
            } else {
                key = createKey(tradePlat, market, instrument, i, null);
            }
        }
        return null;
    }

    @Override
    public MinTrdUnitLimit getMinTrdUnitLimit(String tradePlat, String market, Instrument instrument, String instructionPs) {
        if (instrument == null) {
            return null;
        }
        String key = createKey(tradePlat, market, instrument, 0, instructionPs);
        for (int i = 1; i <= 3; i++) {
            MinTrdUnitLimit minTrdUnitLimit = minTrdUnitLimitMap.get(key);
            if (Objects.nonNull(minTrdUnitLimit)) {
                return minTrdUnitLimit;
            } else {
                key = createKey(tradePlat, market, instrument, i, instructionPs);
            }
        }
        return null;
    }

    @Override
    public MinPriceAndMinTrdUnitLimitBean queryMinPriceAndMinTrdUnitLimit(String stockCode, String tradePlat, String instructionPs) {
        String key = stockCode + "|" + tradePlat + "|" + instructionPs;
        MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean = minPriceAndMinTrdUnitLimitBeanMap.get(key);
        if (Objects.nonNull(minPriceAndMinTrdUnitLimitBean)) {
            return minPriceAndMinTrdUnitLimitBean;
        }
        Instrument instrument = queryByInnerCode(stockCode);
        if (Objects.nonNull(instrument)) {
            minPriceAndMinTrdUnitLimitBean = new MinPriceAndMinTrdUnitLimitBean(stockCode, ErrorCode.SUCCESS.value, ErrorCode.SUCCESS.errorMsg);
            minPriceAndMinTrdUnitLimitBean.setIsFullPrice(instrument.getIsFullPrice());
            List<MinPrice> minPriceList = new ArrayList<>();
            List<MinTrdUnitLimit> minTrdUnitLimitList = new ArrayList<>();
            if (!Strings.isNullOrEmpty(tradePlat)) {
                MinPrice minPrice = getMinPrice(tradePlat, instrument.getMarket(), instrument);
                if (Objects.nonNull(minPrice)) {
                    minPriceList.add(minPrice);
                } else {
                    logger.info("未找对应的的价差信息：market：{} assetType {} tradePlat {} assetType1 {}  assetType2 {}", instrument.getMarket(), tradePlat, instrument.getAssetType(), instrument.getAssetType1(), instrument.getSubStockType());
                }
                MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(tradePlat, instrument.getMarket(), instrument, instructionPs);
                if (Objects.nonNull(minTrdUnitLimit)) {
                    minTrdUnitLimitList.add(minTrdUnitLimit);
                } else {
                    logger.info("未找对应的的交易单位信息：market：{} assetType {} tradePlat {} assetType1 {}  assetType2 {} instructionPs {}", instrument.getMarket(), tradePlat, instrument.getAssetType(), instrument.getAssetType1(), instrument.getSubStockType(), instructionPs);
                }
                minPriceAndMinTrdUnitLimitBean.setMinPriceList(minPriceList);
                minPriceAndMinTrdUnitLimitBean.setMinTrdUnitLimitList(minTrdUnitLimitList);
            } else {
                TradePlat[] tradePlats = TradePlat.values();
                for (TradePlat plat : tradePlats) {
                    MinPrice minPrice = getMinPrice(plat.value, instrument.getMarket(), instrument);
                    if (Objects.nonNull(minPrice)) {
                        minPriceList.add(minPrice);
                    } else {
                        logger.info("无平台信息，未找对应的的价差信息：market：{} assetType {} tradePlat {} assetType1 {}  assetType2 {}", instrument.getMarket(), plat, instrument.getAssetType(), instrument.getAssetType1(), instrument.getSubStockType());
                    }
                    MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(plat.value, instrument.getMarket(), instrument, instructionPs);
                    if (Objects.nonNull(minTrdUnitLimit)) {
                        minTrdUnitLimitList.add(minTrdUnitLimit);
                    } else {
                        logger.info("无平台信息，未找对应的的交易单位信息：market：{} assetType {} tradePlat {} assetType1 {}  assetType2 {} instructionPs {}", instrument.getMarket(), plat, instrument.getAssetType(), instrument.getAssetType1(), instrument.getSubStockType(), instructionPs);
                    }
                    minPriceAndMinTrdUnitLimitBean.setMinPriceList(minPriceList);
                    minPriceAndMinTrdUnitLimitBean.setMinTrdUnitLimitList(minTrdUnitLimitList);
                }
            }
            minPriceAndMinTrdUnitLimitBeanMap.put(key, minPriceAndMinTrdUnitLimitBean);
            return minPriceAndMinTrdUnitLimitBean;
        } else {
            return new MinPriceAndMinTrdUnitLimitBean(stockCode, ErrorCode.REQ_PARAMETER_STOCK_CODE_ERROR.value, ErrorCode.REQ_PARAMETER_STOCK_CODE_ERROR.errorMsg);
        }
    }

    @Override
    public List<Instrument> queryByAssetType(String assetType) {
        return new ArrayList<>(assetTypeMap.getOrDefault(assetType, new ConcurrentHashMap<>()).values());
    }

    @Override
    public void trdUnitLimitFlush() {
        logger.info("minPriceAndMinTrdUnitLimitBeanMap:{}", minPriceAndMinTrdUnitLimitBeanMap);
        minPriceAndMinTrdUnitLimitBeanMap.clear();
    }

    private String createKey(String tradePlat, String market, Instrument instrument, int type, String ps) {
        List<String> list = Lists.newArrayList(market, tradePlat);
        if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(instrument.getAssetType())) {
            ps = InstructionPs.POSITIVEREPO.value;
        }
        list.add(ps);
        String assetType1 = instrument.getAssetType();
        String assetType2 = instrument.getAssetType1();
        if (type == 2) {
            assetType2 = null;
        }
        String assetType3 = instrument.getSubStockType();
        if (type == 1 || type == 2) {
            assetType3 = null;
        }
        list.add(assetType1);
        list.add(assetType2);
        list.add(assetType3);
        return Joiner.on(GlobalConstant.VERTICAL_LINE).useForNull(StringUtils.EMPTY).join(list);
//        String marketKey = market + "|" + tradePlat + "|";
//        if (ps != null) {
//            if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(instrument.getAssetType())) {
//                ps = InstructionPs.POSITIVEREPO.value;
//            }
//            marketKey += ps + "|";
//        }
//        String assetType1 = instrument.getAssetType();
//        String assetType2 = instrument.getAssetType1();
//        if (type == 2) {
//            assetType2 = null;
//        }
//        String assetType3 = instrument.getSubStockType();
//        if (type == 1 || type == 2) {
//            assetType3 = null;
//        }
//        String assetTypeKey = assetType1 + "|" + assetType2 + "|" + assetType3;
//        return marketKey + assetTypeKey;
    }
}
