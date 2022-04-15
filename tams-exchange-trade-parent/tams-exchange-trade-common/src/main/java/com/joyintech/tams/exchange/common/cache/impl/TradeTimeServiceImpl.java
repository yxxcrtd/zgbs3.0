package com.joyintech.tams.exchange.common.cache.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.investment.TradeTimeBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.joyin.DeclarationType;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.TrdPriceTimeConfig;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.*;

/**
 * @Author: think
 * @Description: 交易时间段服务
 * @Date: Created in 9:12 2020/11/2
 */
@Service
public class TradeTimeServiceImpl implements TradeTimeService {

    private final List<TrdPriceTimeConfig> TRD_PRICE_TIME_CONFIG_LIST = Lists.newArrayList();
    private Map<String, TradeTimeBean> tradeTimeBeanMap = new ConcurrentHashMap<>();
    private Map<String, List<OpenCloseTime>> OPEN_CLOSE_TIME_MAP = new ConcurrentHashMap<>();

    @Override
    public void saveTradeTimeCache(List<TradeTimeBean> tradeTimeBeanList) {
        tradeTimeBeanMap.clear();
        if (Objects.nonNull(tradeTimeBeanList)) {
            tradeTimeBeanList.forEach(tradeTimeBean -> {
                String key = tradeTimeBean.getTradeMarket() + "|" + tradeTimeBean.getTradePlat() + "|"
                        + tradeTimeBean.getDeclarationType() + "|" + tradeTimeBean.getAssetType() + "|"
                        + tradeTimeBean.getAssetType1() + "|" + tradeTimeBean.getAssetType2();
                tradeTimeBeanMap.put(key, tradeTimeBean);
            });
        }
    }

    @Override
    public void saveInCache(List<TrdPriceTimeConfig> trdPriceTimeConfigs) {
        TRD_PRICE_TIME_CONFIG_LIST.clear();
        OPEN_CLOSE_TIME_MAP.clear();
        if (CollUtil.isEmpty(trdPriceTimeConfigs)) {
            return;
        }

        TRD_PRICE_TIME_CONFIG_LIST.addAll(trdPriceTimeConfigs);

        List<OpenCloseTime> defaults = trdPriceTimeConfigs.stream()
                .filter(item -> ObjectUtil.isEmpty(item.getAssetClassify1()))
                .map(item -> BeanUtil.toBean(item, OpenCloseTime.class))
                .collect(toList());

        Map<String, List<OpenCloseTime>> collect = TRD_PRICE_TIME_CONFIG_LIST.stream()
                .collect(groupingBy(item -> {
                            List<String> keys = ListUtil.of(item.getAssetClassify1()
                                    , item.getAssetClassify2(),
                                    item.getDealMode(),
                                    item.getTradeMarket(),
                                    item.getTradePlat());
                            return Joiner.on(StrUtil.COMMA).useForNull(StrUtil.AT).join(keys);
                        }, mapping(item -> new OpenCloseTime(item.getPriceTimeType(), item.getOpenTime(), item.getCloseTime()), toList())
                ));
        OPEN_CLOSE_TIME_MAP.putAll(collect);
        OPEN_CLOSE_TIME_MAP.put("default", defaults);

    }

    /**
     * 获取交易时间段列表
     *
     * @param instruction 指令对象
     * @param order       订单对象
     * @return 交易时间段列表
     */
    @Override
    public List<OpenCloseTime> getTradePeriod(Instruction instruction, BaseOrderRequest order) {
        String entrustProp = order.getEntrustProp();
        String tradeMarket = instruction.getTradeMarket();
        String tradePlat = instruction.getTradePlat();
        String assetType = instruction.getAssetType();
        String assetType1 = instruction.getAssetType1();
        String assetType10 = instruction.getAssetType10();
        List<String> querys = ListUtil.of(assetType
                , assetType1,
                entrustProp,
                tradeMarket,
                tradePlat);

        String res = OPEN_CLOSE_TIME_MAP.keySet().stream()
                .filter(key -> diff(StrUtil.split(key, StrUtil.COMMA), querys))
                .min(Comparator.comparing(item -> StrUtil.count(item, StrUtil.AT)))
                .orElse("default");
        return OPEN_CLOSE_TIME_MAP.get(res);
    }

    private boolean diff(String source, String dest) {
        if (StrUtil.isEmpty(source) || StrUtil.isEmpty(dest)) {
            return false;
        }
        List<String> sources = StrUtil.split(source, StrUtil.COMMA);
        List<String> dests = StrUtil.split(dest, StrUtil.COMMA);
        return diff(sources, dests);
    }

    private boolean diff(List<String> sources, List<String> dests) {
        if (CollUtil.isEmpty(sources) || CollUtil.isEmpty(dests)) {
            return false;
        }
        for (int i = 0, len = sources.size(); i < len; i++) {
            String s = sources.get(i);
            String d = dests.get(i);

            if (!StrUtil.AT.equals(s) && !s.equals(d)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TradeTimeBean getTradeTimeByInstructionAndEntrustProp(Instruction instruction, String assetType1, String assetType2, String entrustProp) {
        String marketKey = instruction.getTradeMarket() + "|" + instruction.getTradePlat() + "|"
                + DeclarationType.convertEntrustPropToDeclarationType(entrustProp) + "|";
        String assetTypeKey = instruction.getAssetType() + "|"
                + assetType1 + "|" + assetType2;
        int type = 0;
        while (type <= 3) {
            TradeTimeBean tradeTimeBean = tradeTimeBeanMap.get(marketKey + assetTypeKey);
            if (Objects.nonNull(tradeTimeBean)) {
                return tradeTimeBean;
            } else {
                type++;
                assetTypeKey = createKey(instruction.getAssetType(), assetType1, assetType2, type);
            }
        }
        return null;
    }

    private String createKey(String assetType, String assetType1, String assetType2, int type) {
        if (type == 3) {
            assetType = null;
        }
        if (type == 2 || type == 3) {
            assetType1 = null;
        }
        if (type == 1 || type == 2 || type == 3) {
            assetType2 = null;
        }
        return assetType + "|" + assetType1 + "|" + assetType2;
    }
}
