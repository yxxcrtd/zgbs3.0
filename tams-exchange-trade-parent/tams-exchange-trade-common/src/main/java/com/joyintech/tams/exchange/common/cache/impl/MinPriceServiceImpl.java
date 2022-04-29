package com.joyintech.tams.exchange.common.cache.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.cache.MinPriceService;
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
 * 最小价格变动单位
 */
@Service
public class MinPriceServiceImpl implements MinPriceService {

    public static final String DEFAULT = "defaults";
    public static final MinPrice DEFAULT_MIN_PRICE = null;
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(MinPriceServiceImpl.class);
    private final List<MinPrice> minPriceList = Lists.newArrayList();
    /**
     * 不会有并发写场景，故使用hashMap
     */
    private final Map<String, MinPrice> minPriceMap = Maps.newHashMap();

    @Override
    public void saveInCache(List<MinPrice> minPrices) {
        log.info("saveInCache() called with parameters => 【minPrices = {}】", minPrices);
        minPriceList.clear();
        minPriceMap.clear();
        if (CollUtil.isEmpty(minPrices)) {
            // 程序中的兜底方案
            minPriceMap.put(DEFAULT, DEFAULT_MIN_PRICE);
            return;
        }

        minPriceList.addAll(minPrices);

        Map<String, MinPrice> collect = minPriceList.stream()
                .collect(Collectors.toMap(item -> {
                    List<String> keys = ListUtil.of(
                            item.getTradeMarket(),
                            item.getTradePlat(),
                            item.getAssetType(),
                            item.getAssetType1()
                    );
                    return Joiner.on(StrUtil.DASHED).useForNull(StrUtil.AT).join(keys);
                }, Function.identity(), (existing, replacement) -> existing));
        minPriceMap.putAll(collect);
        minPriceMap.put(DEFAULT, DEFAULT_MIN_PRICE);
        log.info("saveInCache() end with bean => 【minPriceMap = {}】", minPriceMap);
    }

    /**
     * 获取最小价格变动单位
     *
     * @param instrument 合约对象
     * @return 最小价格变动单位
     */
    @Override
    public MinPrice getMinPrice(Instruction instruction, Instrument instrument) {
        String tradeMarket = instruction.getTradeMarket();
        String tradePlat = TradePlatUtils.trd2EtrTradePlat(instruction);
        String assetType = instrument.getAssetType();
        String assetType1 = instrument.getAssetType1();
        return getMinPrice(tradeMarket, tradePlat, assetType, assetType1);
    }

    /**
     * 获取最小价格变动单位
     *
     * @param tradeMarket 交易市场
     * @param tradePlat   交易平台
     * @param assetType   资产类型
     * @param assetType1  资产类型一
     * @return 最小价格变动单位
     */
    @Override
    public MinPrice getMinPrice(String tradeMarket, String tradePlat, String assetType, String assetType1) {
        List<String> queries = ListUtil.of(tradeMarket, tradePlat, assetType, assetType1);
        log.info("getMinPrice() called with parameters => 【key = {}】", queries);

        String res = minPriceMap.keySet().stream()
                .filter(key -> diff(StrUtil.split(key, StrUtil.DASHED), queries))
                .min(Comparator.comparing(item -> StrUtil.count(item, StrUtil.AT)))
                .orElse(DEFAULT);
        MinPrice minPrice = minPriceMap.get(res);
        log.debug("getMinPrice() end with result => 【res = {}, minPrice = {}】", res, minPrice);
        return minPrice;
    }

    @SuppressWarnings("unused")
    private boolean diff(String source, String target) {
        if (StrUtil.isEmpty(source) || StrUtil.isEmpty(target)) {
            return false;
        }
        List<String> sources = StrUtil.split(source, StrUtil.DASHED);
        List<String> targets = StrUtil.split(target, StrUtil.DASHED);
        return diff(sources, targets);
    }

    private boolean diff(List<String> sources, List<String> targets) {
        if (CollUtil.isEmpty(sources) || CollUtil.isEmpty(targets)) {
            return false;
        }
        for (int i = 0, len = sources.size(); i < len; i++) {
            String s = sources.get(i);
            String d = targets.get(i);
            if (!StrUtil.AT.equals(s) && !s.equals(d)) {
                return false;
            }
        }
        return true;
    }
}
