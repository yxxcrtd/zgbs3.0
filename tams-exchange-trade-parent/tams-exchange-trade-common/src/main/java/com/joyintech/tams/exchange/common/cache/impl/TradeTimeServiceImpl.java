package com.joyintech.tams.exchange.common.cache.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.joyintech.tams.common.base.entity.TrdPriceTimeConfig;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.investment.TradeTimeBean;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.joyin.DeclarationType;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.enums.invest.BoardTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.FinprodTypeTgEnum;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TradePlatUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.*;

/**
 * 交易时间段
 */
@Service
public class TradeTimeServiceImpl implements TradeTimeService {

    public static final String DEFAULT = "defaults";
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(TradeTimeServiceImpl.class);
    private final List<TrdPriceTimeConfig> trdPriceTimeConfigList = Lists.newArrayList();
    private final Map<String, TradeTimeBean> tradeTimeBeanMap = new ConcurrentHashMap<>();
    private final Map<String, List<OpenCloseTime>> openCloseTimeMap = new ConcurrentHashMap<>();

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
        log.info("saveInCache() called with parameters => 【trdPriceTimeConfigs = {}】", trdPriceTimeConfigs);
        trdPriceTimeConfigList.clear();
        openCloseTimeMap.clear();
        // 程序中的兜底方案
        openCloseTimeMap.put(DEFAULT, ListUtil.of(
                new OpenCloseTime(OpenCloseTime.PriceTimeTypeEnum.OPEN.getCode(), "9:15:00", "9:25:00"),
                new OpenCloseTime(OpenCloseTime.PriceTimeTypeEnum.KEEP.getCode(), "9:30:00", "11:30:00"),
                new OpenCloseTime(OpenCloseTime.PriceTimeTypeEnum.CLOSE.getCode(), "14:57:00", "15:30:00")
        ));
        if (CollUtil.isEmpty(trdPriceTimeConfigs)) {
            return;
        }

        trdPriceTimeConfigList.addAll(trdPriceTimeConfigs);

        /*List<OpenCloseTime> defaults = trdPriceTimeConfigs.stream()
                .filter(item -> ObjectUtil.isEmpty(item.getAssetClassify1()))
                .map(item -> BeanUtil.toBean(item, OpenCloseTime.class))
                .collect(toList());*/

        Map<String, List<OpenCloseTime>> collect = trdPriceTimeConfigList.stream()
//                .filter(item -> !ObjectUtil.isEmpty(item.getAssetClassify1()))
                .collect(groupingBy(item -> {
                            List<String> keys = ListUtil.of(item.getAssetClassify1()
                                    , item.getAssetClassify2(),
                                    item.getDealMode(),
                                    item.getTradeMarket(),
                                    item.getTradePlat());
                            return Joiner.on(StrUtil.COMMA).useForNull(StrUtil.AT).join(keys);
                        }, mapping(item -> new OpenCloseTime(item.getPriceTimeType(), item.getOpenTime(), item.getCloseTime()), toList())
                ));
        openCloseTimeMap.putAll(collect);
//        openCloseTimeMap.put(DEFAULT, defaults);
        log.info("saveInCache() end with bean => 【openCloseTimeMap = {}】", openCloseTimeMap);
    }

    /**
     * 获取交易时间段列表
     *
     * @param instruction 指令对象
     * @param order       订单对象
     * @return 交易时间段列表
     */
    @Override
    public List<OpenCloseTime> getTradePeriod(Instruction instruction, BaseOrderRequest order, Instrument instrument) {
        log.info("getTradePeriod() called with parameters => 【instruction = {}】, 【order = {}】",instruction, order);
        String assetType = instruction.complexAssertTypeWhenConvertibleBond();
        if (InvestmentAssetType.STOCK.codeEquals(assetType)
                && EnumUtil.in(instrument.getSubStockType(), BoardTypeEnum.KCB, BoardTypeEnum.CYB)) {
            assetType = assetType + instrument.getSubStockType();
        }
        String assetType1 = instruction.getAssetType1();
        String entrustProp = order.getEntrustProp();
        String tradeMarket = instruction.getTradeMarket();
        String tradePlat = TradePlatUtils.trd2EtrTradePlat(instruction);
        List<String> queries = ListUtil.of(assetType, assetType1, entrustProp, tradeMarket, tradePlat);

        String res = openCloseTimeMap.keySet().stream()
                .filter(key -> diff(StrUtil.split(key, StrUtil.COMMA), queries))
                .min(Comparator.comparing(item -> StrUtil.count(item, StrUtil.AT)))
                .orElse(DEFAULT);
        List<OpenCloseTime> openCloseTimes = openCloseTimeMap.get(res);
        log.debug("getTradePeriod() end with result => 【key = {}, res = {}, openCloseTimes = {}】", queries, res, openCloseTimes);
        return openCloseTimes;
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
