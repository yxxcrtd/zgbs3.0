package com.joyintech.tams.exchange.common.cache.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.cache.MinTrdUnitLimitService;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.utils.TradePlatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 有效价格申报范围
 */
@Service
public class MinTrdUnitLimitServiceImpl implements MinTrdUnitLimitService {

    public static final String DEFAULT = "defaults";
    public static final MinTrdUnitLimit DEFAULT_MIN_TRD_UNIT_LIMIT = null;
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(MinTrdUnitLimitServiceImpl.class);
    private final List<MinTrdUnitLimit> minTrdUnitLimitList = Lists.newArrayList();
    /**
     * 不会有并发写场景，故使用hashMap
     */
    private final Map<String, MinTrdUnitLimit> minTrdUnitLimitMap = Maps.newHashMap();

    @Override
    public void saveInCache(List<MinTrdUnitLimit> minTrdUnitLimits) {
        log.info("saveInCache() called with parameters => 【minTrdUnitLimits = {}】", minTrdUnitLimits);
        minTrdUnitLimitList.clear();
        minTrdUnitLimitMap.clear();
        if (CollUtil.isEmpty(minTrdUnitLimits)) {
            // 程序中的兜底方案
            minTrdUnitLimitMap.put(DEFAULT, DEFAULT_MIN_TRD_UNIT_LIMIT);
            return;
        }

        minTrdUnitLimitList.addAll(minTrdUnitLimits);
        Map<String, MinTrdUnitLimit> collect = minTrdUnitLimits.stream()
                .collect(Collectors.toMap(item -> {
                    List<String> keys = ListUtil.of(
                            item.getTradeMarket(), item.getTradePlat(), item.getAssetType(),
                            item.getAssetType1(), item.getMarketType(), item.getPs());
                    return Joiner.on(StrUtil.COMMA).useForNull(StrUtil.AT).join(keys);
                }, Function.identity(), (existing, replacement) -> existing));
        minTrdUnitLimitMap.putAll(collect);
        minTrdUnitLimitMap.put(DEFAULT, DEFAULT_MIN_TRD_UNIT_LIMIT);
        log.info("saveInCache() end with bean => 【minTrdUnitLimitMap = {}】", minTrdUnitLimitMap);
    }

    /**
     * 获取有效价格申报范围
     *
     * @param instrument 合约对象
     * @return 有效价格申报范围
     */
    @Override
    public MinTrdUnitLimit getMinTrdUnitLimit(Instruction instruction, Instrument instrument) {
        String tradeMarket = instruction.getTradeMarket();
        String tradePlat = TradePlatUtils.trd2EtrTradePlat(instruction);
        String assetType = instrument.getAssetType();
        String assetType1 = instrument.getAssetType1();
        String subStockType = instrument.getSubStockType();
        String ps = instruction.getPs();
        return getMinTrdUnitLimit(tradeMarket, tradePlat, assetType, assetType1, subStockType, ps);
    }

    /**
     * 获取有效价格申报范围
     *
     * @param tradeMarket 交易市场
     * @param tradePlat   交易平台
     * @param assetType   资产类型
     * @param assetType1  资产类型一
     * @param assetType2  资产类型二
     * @param ps          交易方向
     * @return 有效价格申报范围
     */
    @Override
    public MinTrdUnitLimit getMinTrdUnitLimit(String tradeMarket, String tradePlat, String assetType, String assetType1, String assetType2, String ps) {

        List<String> queries = ListUtil.of(tradeMarket, tradePlat, assetType, assetType1, assetType2, ps);
        log.debug("getMinTrdUnitLimit() called with parameters => 【key = {}】", queries);

        String res = minTrdUnitLimitMap.keySet().stream()
                .filter(key -> diff(StrUtil.split(key, StrUtil.COMMA), queries))
                .min(Comparator.comparing(item -> StrUtil.count(item, StrUtil.AT)))
                .orElse(DEFAULT);
        MinTrdUnitLimit minTrdUnitLimit = minTrdUnitLimitMap.get(res);
        log.debug("getMinTrdUnitLimit() end with result => 【res = {}, minTrdUnitLimit = {}】", res, minTrdUnitLimit);
        return minTrdUnitLimit;
    }

    @SuppressWarnings("unused")
    private boolean diff(String source, String dest) {
        if (StrUtil.isEmpty(source) || StrUtil.isEmpty(dest)) {
            return false;
        }
        List<String> sources = StrUtil.split(source, StrUtil.COMMA);
        List<String> targets = StrUtil.split(dest, StrUtil.COMMA);
        return diff(sources, targets);
    }

    private boolean diff(List<String> sources, List<String> targets) {
        if (CollUtil.isEmpty(sources) || CollUtil.isEmpty(targets)) {
            return false;
        }
        for (int i = 0, len = sources.size(); i < len; i++) {
            String s = sources.get(i);
            String d = targets.get(i);

            if (!StrUtil.AT.equals(s) && (!s.equals(d))) {
                return false;
            }
        }
        return true;
    }
}
