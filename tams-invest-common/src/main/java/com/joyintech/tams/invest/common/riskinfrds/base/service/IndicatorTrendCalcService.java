/*
 * 文件名：IndicatorTrendCalcService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2020年8月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.riskinfrds.base.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplIndicatorTrend;
import com.joyintech.tams.invest.common.redisinit.dto.ImplOcpTrend;
import com.joyintech.tams.invest.common.redisinit.dto.ImplPtlIndicator;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEventParaRel;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicatorTrend;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IOcpTrend;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorTrendService;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOcpRelParamItem;
import com.joyintech.tams.invest.common.riskinfrds.occupy.service.OccupyRelParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.riskinfrds.prod.entity.IPtlIndicator;

/**
 * 按指标趋势计算指标（趋势向好假设不发生，趋势向坏假设发生）Service
 * 
 * @author Administrator
 * @version 1.0 2020年8月11日
 * @see IndicatorTrendCalcService
 * @since 1.0
 */
@Service
public class IndicatorTrendCalcService {

    /**
     * key:组合代码
     */
    public static final String PORTFOLIOCODE = "prodIdForBk";

    /**
     * 事件编号
     */
    public static final String EVENT_ID = "eventId";

    /**
     * 处理类型：预占
     */
    public static final int PRE_OCCUPY = 1;

    /**
     * KEY:指标趋势信息
     */
    public static final String INDICATOR_TREND = "indicatorTrend";

    /**
     * KEY:预占实占信息
     */
    public static final String OCP_LIST = "ocpList";

    /**
     * KEY:有指标趋势信息的指标
     */
    public static final String HAS_TREND_INDICATOR = "hasTrendIndicator";

    // add by zhangrui1 差分预占 start
    /**
     * 差分预占实占
     */
    public static final String IS_DIFF_OCCUPY = "isDiffOccupy";

    /**
     * KEY:资管委托编号
     */
    public static final String ENTRUST_CODE = "entrustCode";
    // add by zhangrui1 差分预占 end

    /**
     * KEY:没有指标趋势的指标
     */
    public static final String NO_TREND_INDICATOR = "noTrendIndicator";

    protected static final String MIXAMOUNTKEY = "mixAmount";

    public static final String OLD_VAL_MAP = "oldValMap";

    @Autowired
    private IndicatorTrendService indTrendSrc;

    @Autowired
    private OccupyRelParamService ocpParamSrc;

    @Autowired
    private IndicatorParamValService paramValSrc;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务线程数据
     */
    private ThreadLocal<Map<String, Object>> serviceInfo = ThreadLocal.withInitial(HashMap::new);

    /**
     * 初始化所有指标趋势信息
     * 
     * @param portfolioCode 组合代码
     * @param indMap 指标列表
     * @param dataMap 报文信息
     */
    public void initIndTrendMap(String portfolioCode, Map<String, IIndicator> indMap,
                                Map<String, Object> dataMap) {
        // 指标代码列表
        Set<String> indCodeSet = indMap.keySet();
        // 取得所有指标趋势信息
        Map<String, IIndicatorTrend> indTrendMap = indTrendSrc.getIndicatorTrend(portfolioCode,
            indCodeSet);
        CommonUtil.removeNull(indTrendMap);
        if (!CollectionUtils.isEmpty(indTrendMap)) {
            // 从指标趋势中取得相关的预占实占代码
            Set<String> ocpCodeList = new HashSet<>();
            indTrendMap.values().forEach(item -> {
                if (null == item || CollectionUtils.isEmpty(item.getOcpSet())) {
                    return;
                }
                ocpCodeList.addAll(item.getOcpSet().stream().map(
                    ocp -> null == ocp ? null : ocp.getOcpCode()).collect(Collectors.toSet()));
            });
            String preOcpCode = getPreOcpCode(dataMap);
            if (!CollectionUtils.isEmpty(ocpCodeList) && !StringUtil.isEmpty(preOcpCode)) {
                ocpCodeList.add(preOcpCode);
            }
            // 删除自身
            removeSelf(ocpCodeList, dataMap);
            // 取得所有相关的预占实占信息
            Map<String, IOccupyRelParam> ocpList = ocpParamSrc.getOccupyRelParamList(ocpCodeList);
            CommonUtil.removeNull(ocpList);
            if (!CollectionUtils.isEmpty(ocpList)) {
                cleanIndTrendMap(indTrendMap, ocpList);
                // 过滤出没有趋势的指标信息
                Map<String, IIndicator> leftInd = CommonUtil.mapFilter(indMap,
                    ind -> !indTrendMap.containsKey(
                        ind.getKey() + GlobalConstant.COLON + portfolioCode));
                setServiceInfo(NO_TREND_INDICATOR, leftInd);
                // 过滤出有指标趋势的指标信息
                Map<String, IIndicator> rightInd = CommonUtil.mapFilter(indMap,
                    ind -> indTrendMap.containsKey(
                        ind.getKey() + GlobalConstant.COLON + portfolioCode));
                setServiceInfo(HAS_TREND_INDICATOR, rightInd);
            }
            // 保存信息到本地缓存
            setServiceInfo(OCP_LIST, ocpList);
            setServiceInfo(INDICATOR_TREND, indTrendMap);
        }
    }

    /**
     * 清除指标趋势信息中的垃圾数据（预占信息不存在的数据）
     * 
     * @param indTrendMap 指标趋势信息
     * @param ocpList 预占实占信息
     */
    private void cleanIndTrendMap(Map<String, IIndicatorTrend> indTrendMap,
                                  Map<String, IOccupyRelParam> ocpList) {
        Iterator<Entry<String, IIndicatorTrend>> it = indTrendMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, IIndicatorTrend> en = it.next();
            IIndicatorTrend indTrend = en.getValue();
            Set<IOcpTrend> trendSet = indTrend.getOcpSet();
            // 趋势列表为空，或者趋势列表中的预占信息不存在，则删除该指标趋势信息
            if (CollectionUtils.isEmpty(trendSet) || !CollectionUtils.containsAny(ocpList.keySet(),
                trendSet.stream().map(IOcpTrend::getOcpCode).collect(Collectors.toSet()))) {
                it.remove();
            }
        }
    }

    /**
     * 取出反向维度参数值
     * 
     * @param paraValMap 参数值列表
     */
    private void getParamVal(Map<String, Map<String, IValue<BigDecimal>>> paraValMap) {
        Set<String> keySet = paraValMap.keySet();
        Map<String, Map<String, IValue<Object>>> temp = new HashMap<>();
        for (String key : keySet) {
            String paraCode = reverseCode(key);
            if (!paraValMap.containsKey(paraCode)) {
                Map<String, IValue<Object>> newTemp = new HashMap<>();
                Map<String, IValue<BigDecimal>> valMap = paraValMap.get(key);
                if (!CollectionUtils.isEmpty(valMap)) {
                    valMap.entrySet().forEach(en -> newTemp.put(en.getKey(), null));
                    temp.put(paraCode, newTemp);
                }
            }
        }
        if (!CollectionUtils.isEmpty(temp)) {
            filterValMap(paraValMap, paramValSrc.getParamVal(temp));
        }
    }

    public Map<String, Map<String, IValue<BigDecimal>>> filterValMap(Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                                                                     Map<String, Map<String, IValue<Object>>> objMap) {
        if (CollectionUtils.isEmpty(objMap)) {
            return paraValMap;
        }
        objMap.entrySet().forEach(en -> {
            Map<String, IValue<Object>> valMap = en.getValue();
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            Map<String, IValue<BigDecimal>> tMap = new HashMap<>();
            valMap.entrySet().forEach(item -> {
                IValue<Object> objVal = item.getValue();
                if (null != objVal && objVal.value() instanceof BigDecimal) {
                    IValue<BigDecimal> numVal = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                    numVal.set((BigDecimal)objVal.value());
                    tMap.put(item.getKey(), numVal);
                }
                else {
                    IValue<BigDecimal> numVal = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                    numVal.set(BigDecimal.ZERO);
                    tMap.put(item.getKey(), numVal);
                }
            });
            if (!CollectionUtils.isEmpty(tMap)) {
                paraValMap.put(en.getKey(), tMap);
            }
        });
        return paraValMap;
    }

    private void log(boolean isOld, IIndicator ind,
                     Map<String, Map<String, IValue<BigDecimal>>> paraValMap) {
        String indName = ind.getIndName();
        StringBuilder builder = new StringBuilder();
        if (!CollectionUtils.isEmpty(paraValMap)) {
            Map<String, List<String>> map = new HashMap<>();
            paraValMap.entrySet().forEach(en -> {
                String paraCode = en.getKey();
                Map<String, IValue<BigDecimal>> temp = en.getValue();
                temp.entrySet().forEach(item -> {
                    String field = item.getKey();
                    IValue<BigDecimal> val = item.getValue();
                    if (null == val) {
                        return;
                    }
                    List<String> list = map.getOrDefault(field, new ArrayList<>());
                    list.add(paraCode + "=" + StringUtil.numberToString((BigDecimal)val.value()));
                    map.put(field, list);
                });
            });
            if (!CollectionUtils.isEmpty(map)) {
                map.entrySet().forEach(
                    en -> builder.append(en.getKey()).append(GlobalConstant.COLON).append(
                        "{").append(
                            StringUtil.joinString(en.getValue(), GlobalConstant.COMMA)).append(
                                "}"));
            }
        }
        String msg = builder.toString();
        logger.info("计算指标({}){}的参数值->{}", indName, isOld ? "原值" : "新值", msg);
    }

    /**
     * 计算处理入口
     * 
     * @param indMap 指标信息列表
     * @param paramMap 参数信息列表
     * @param paraValMap 参数值列表
     * @param eventList 风控事项列表
     * @param dataMap 请求报文
     * @param consumer 业务处理
     * @param handInfo 处理本地缓存信息
     * @return 剩余未处理指标
     */
    public Map<String, IIndicator> executeList(Map<String, IIndicator> indMap,
                                               Map<String, IParam> paramMap,
                                               Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                                               List<IEvent> eventList, Map<String, Object> dataMap,
                                               ITrendCalc consumer,
                                               Consumer<Map<String, Object>> handInfo) {
        return execute(indMap, paramMap, paraValMap, eventList, dataMap, consumer, handInfo);
    }

    /**
     * 计算处理入口
     * 
     * @param indMap 指标信息列表
     * @param paramMap 参数信息列表
     * @param paraValMap 参数值列表
     * @param eventList 风控事项列表
     * @param dataMap 请求报文
     * @param consumer 业务处理
     * @return 剩余未处理指标
     */
    public Map<String, IIndicator> execute(Map<String, IIndicator> indMap,
                                           Map<String, IParam> paramMap,
                                           Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                                           List<IEvent> eventList, Map<String, Object> dataMap,
                                           ITrendCalc consumer) {
        return execute(indMap, paramMap, paraValMap, eventList, dataMap, consumer, null);
    }

    /**
     * 计算处理入口
     * 
     * @param indMap 指标信息列表
     * @param paramMap 参数信息列表
     * @param paraValMap 参数值列表
     * @param eventList 风控事项列表
     * @param dataMap 请求报文
     * @param consumer 业务处理
     * @param handInfo 处理本地缓存信息
     * @return 剩余未处理指标
     */
    @SuppressWarnings("unchecked")
    public Map<String, IIndicator> execute(Map<String, IIndicator> indMap,
                                           Map<String, IParam> paramMap,
                                           Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                                           List<IEvent> eventList, Map<String, Object> dataMap,
                                           ITrendCalc consumer,
                                           Consumer<Map<String, Object>> handInfo) {
        // 取得所有被影响的参数代码
        Set<String> eveParamList = new HashSet<>();
        eventList.forEach(
            event -> event.getEventParaRel().forEach(rel -> eveParamList.add(rel.getParaCode())));
        // 组合代码
        String portfolioCode = (String)dataMap.get(PORTFOLIOCODE);
        // 清除本地缓存
        cleanServiceInfo();
        setServiceInfo("assetAmtVal", dataMap.get(MIXAMOUNTKEY));
        // 初始化数据
        initIndTrendMap(portfolioCode, indMap, dataMap);
        // 指标趋势信息
        Map<String, IIndicatorTrend> indTrendMap = (Map<String, IIndicatorTrend>)getServiceInfo(
            INDICATOR_TREND);
        // 预占实占信息
        Map<String, IOccupyRelParam> ocpList = (Map<String, IOccupyRelParam>)getServiceInfo(
            OCP_LIST);
        // 取参数值
        getParamVal(paraValMap);
        // 处理缓存信息
        if (handInfo != null) {
            Map<String, Object> info = new HashMap<>();
            indTrendMap = CollectionUtils.isEmpty(indTrendMap) ? new HashMap<>() : indTrendMap;
            ocpList = CollectionUtils.isEmpty(ocpList) ? new HashMap<>() : ocpList;
            info.put(INDICATOR_TREND, indTrendMap);
            info.put(OCP_LIST, ocpList);
            info.put(OLD_VAL_MAP, paraValMap);
            handInfo.accept(info);
        }
        // 判断是否需要按趋势计算参数值
        if (null == consumer || CollectionUtils.isEmpty(indTrendMap)
            || CollectionUtils.isEmpty(ocpList)) {
            addThisTrend(indMap, eveParamList, indTrendMap, dataMap);
            return indMap;
        }
        // 有趋势的指标信息
        Map<String, IIndicator> hasTrendIndMap = (Map<String, IIndicator>)getServiceInfo(
            HAS_TREND_INDICATOR);
        // 没有趋势的指标信息
        Map<String, IIndicator> noTrendIndMap = (Map<String, IIndicator>)getServiceInfo(
            NO_TREND_INDICATOR);
        noTrendIndMap = noTrendIndMap == null ? new HashMap<>() : noTrendIndMap;
        if (CollectionUtils.isEmpty(hasTrendIndMap)) {
            return indMap;
        }
        // add by zhangrui1 差分预占 start
        calcParamValWhenDiffOcp(paraValMap, dataMap, ocpList);
        // add by zhangrui1 差分预占 end
        // 遍历指标
        for (Entry<String, IIndicator> en : hasTrendIndMap.entrySet()) {
            // 指标趋势信息查询Key -> 指标代码:组合代码
            String key = en.getKey() + GlobalConstant.COLON + portfolioCode;
            // 取得指标趋势信息
            IIndicatorTrend indTrend = indTrendMap.get(key);
            IIndicator ind = en.getValue();
            // 若指标趋势信息不存在
            if (null == ind || CollectionUtils.isEmpty(ind.getIndParamList()) || null == indTrend
                || CollectionUtils.isEmpty(indTrend.getOcpSet())) {
                // 保存到没有趋势指标信息中
                noTrendIndMap.put(en.getKey(), ind);
                continue;
            }
            // 指标的参数列表
            List<String> paraList = ind.getIndParamList();
            // 过滤出该指标的参数值
            Map<String, Map<String, IValue<BigDecimal>>> oldValMap = CommonUtil.copyToNew(
                CommonUtil.mapFilter(paraValMap, item -> isContains(paraList, item.getKey())));
            // 根据指标趋势信息计算参数值
            calcParamWithTrend(indTrend, ocpList, paraList, oldValMap);
            // 检查指标参数在参数值Map中不存在时
            checkParaVal(oldValMap, paraList);
            // 过滤出指标需要的参数值
            oldValMap = CommonUtil.mapFilter(oldValMap, item -> paraList.contains(item.getKey()));
            log(true, ind, oldValMap);
            // 指标相关受影响的参数的风控关系配置信息
            List<IEventParaRel> paraRelList = getParaRelList(eventList, paraList);
            // 取出指标参数相关预占剩余额度
            Map<String, BigDecimal> ocpAmtMap = getOcpAmt(indTrend, dataMap, ocpList, paraList);
            // 参数新值
            Map<String, Map<String, IValue<BigDecimal>>> newValMap = calcNewValMap(paraRelList,
                oldValMap, ocpAmtMap);
            // 当前指标信息
            Map<String, IIndicator> newIndMap = new HashMap<>();
            newIndMap.put(en.getKey(), en.getValue());
            log(false, ind, newValMap);
            // 业务处理->指标计算
            consumer.handle(newIndMap, oldValMap, newValMap);
        }
        addThisTrend(indMap, eveParamList, indTrendMap, dataMap);
        return noTrendIndMap;
    }

    /**
     * 差分预占时将原来的预占额度抹除
     * 
     * @param mapData 报文信息
     * @param ocpList 预占信息
     */
    private void calcParamValWhenDiffOcp(Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                                         Map<String, Object> mapData,
                                         Map<String, IOccupyRelParam> ocpList) {
        // 差分预占的场合
        if (StringUtil.equals(mapData.get(IS_DIFF_OCCUPY),
            BussinessDictCodeListRds.是否_是.getValue()) && !CollectionUtils.isEmpty(paraValMap)) {
            String key = mapData.get(ENTRUST_CODE) + GlobalConstant.COLON + PRE_OCCUPY;
            if (ocpList.containsKey(key)) {
                IOccupyRelParam ocpItem = ocpList.get(key);
                if (null == ocpItem || CollectionUtils.isEmpty(ocpItem.getItems())) {
                    return;
                }
                Map<String, IOcpRelParamItem> items = ocpItem.getItems();
                paraValMap.entrySet().forEach(en -> {
                    // 参数代码
                    String paraCode = en.getKey();
                    // 预占列表
                    IOcpRelParamItem item = items.get(paraCode);
                    if (item == null) {
                        return;
                    }
                    // 参数列表
                    Map<String, IValue<BigDecimal>> valMap = en.getValue();
                    if (CollectionUtils.isEmpty(valMap)) {
                        return;
                    }
                    // 抹除差分预占前面的预占额度
                    valMap.entrySet().forEach(valEn -> {
                        String rdsKey = valEn.getKey();
                        BigDecimal diffVal = item.diffVal(rdsKey);
                        IValue<BigDecimal> val = valEn.getValue();
                        val.set(CommonUtil.math(val.value(),
                            null == diffVal ? BigDecimal.ZERO : item.doFormula(diffVal), item.getOccupyOper()));
                    });
                });
            }
        }
    }

    /**
     * 检查指标参数在参数值Map中不存在时，添加初始值
     * 
     * @param paraValMap 参数值Map
     * @param paraList 指标的参数列表
     */
    public void checkParaVal(Map<String, Map<String, IValue<BigDecimal>>> paraValMap,
                             List<String> paraList) {
        for (String code : paraList) {
            String reverseCode = reverseCode(code);
            if (!paraValMap.containsKey(code) && paraValMap.containsKey(reverseCode)) {
                Map<String, IValue<BigDecimal>> valMap = paraValMap.get(reverseCode);
                Map<String, IValue<BigDecimal>> temp = new HashMap<>();
                valMap.entrySet().forEach(en -> {
                    IValue<BigDecimal> val = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                    val.set(BigDecimal.ZERO);
                    temp.put(en.getKey(), val);
                });
                paraValMap.put(code, temp);
            }
            else if (paraValMap.containsKey(code) && paraValMap.containsKey(reverseCode)) {
                Map<String, IValue<BigDecimal>> valMap = paraValMap.get(reverseCode);
                Map<String, IValue<BigDecimal>> temp = paraValMap.get(code);
                mergeValMap(valMap, temp);
                mergeValMap(temp, valMap);
            }
        }
    }

    private void mergeValMap(Map<String, IValue<BigDecimal>> map1,
                             Map<String, IValue<BigDecimal>> map2) {
        map1.entrySet().forEach(en -> {
            if (!map2.containsKey(en.getKey())) {
                IValue<BigDecimal> val = new Value<>(
                    str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                val.set(BigDecimal.ZERO);
                map2.put(en.getKey(), val);
            }
        });
    }

    /**
     * 判断是否包含某参数或其反向维度参数
     * 
     * @param paraList 参数代码列表
     * @param code 参数代码
     * @return true:包含
     */
    private boolean isContains(List<String> paraList, String code) {
        return paraList.contains(code) || paraList.contains(reverseCode(code));
    }

    /**
     * 取出 实占对应的预占剩余额度
     * 
     * @param indTrend 指标趋势信息
     * @param dataMap 请求报文
     * @param ocpList 预占实占列表
     * @param paraList 参数代码列表
     * @return 实占对应的预占剩余额度
     */
    private Map<String, BigDecimal> getOcpAmt(IIndicatorTrend indTrend,
                                              Map<String, Object> dataMap,
                                              Map<String, IOccupyRelParam> ocpList,
                                              List<String> paraList) {
        Object eventType = dataMap.get("eventType");
        boolean isPreOccupy = StringUtil.equals(eventType, "1")
                              || StringUtil.equals(eventType, "2");
        if (!isPreOccupy) {
            return new HashMap<>();
        }
        IOccupyRelParam ocpInfo = ocpList.get(getPreOcpCode(dataMap));
        if (null == ocpInfo) {
            return new HashMap<>();
        }
        ImplOcpTrend tempTrend = new ImplOcpTrend();
        tempTrend.setTrend(-1);
        IOcpTrend ocpTrend = indTrend.getOcpSet().stream().filter(
            item -> StringUtil.equals(item.getOcpCode(), ocpInfo.getField())).findFirst().orElse(
                null);
        // 构建处理结构
        Map<IOcpRelParamItem, List<IOcpRelParamItem>> ocpItems = createOcpItems(ocpInfo, ocpList);
        // 过滤出相关的预占实占信息
        ocpItems = CommonUtil.mapFilter(ocpItems,
            item -> isContains(paraList, item.getKey().getParamCode()));
        Map<String, BigDecimal> ocpAmtMap = new HashMap<>();
        ocpItems.entrySet().forEach(en -> {
            IOcpRelParamItem ocpItem = en.getKey();
            List<IOcpRelParamItem> itemList = en.getValue();
            String paraCode = ocpItem.getParamCode();
            boolean hasCode = paraList.contains(paraCode);
            paraCode = hasCode ? paraCode : reverseCode(paraCode);
            if (null == ocpTrend) {
                ocpAmtMap.put(paraCode, hasCode ? BigDecimal.ZERO.max(
                    getOcpAmt(true, ocpItem, itemList)) : BigDecimal.ZERO);
            }
            else {
                ocpAmtMap.put(paraCode,
                    BigDecimal.ZERO.max(getOcpAmt(ocpTrend.isTrendBad(), ocpItem, itemList)));
            }
        });
        return ocpAmtMap;
    }

    /**
     * 计算参数新值
     * 
     * @param paraRelList 关系对象风控配置信息
     * @param oldValMap 参数原值
     * @param ocpAmtMap 预占剩余额度
     * @param paramMap 参数信息
     * @return 参数新值
     */
    private Map<String, Map<String, IValue<BigDecimal>>> calcNewValMap(List<IEventParaRel> paraRelList,
                                                                       Map<String, Map<String, IValue<BigDecimal>>> oldValMap,
                                                                       Map<String, BigDecimal> ocpAmtMap) {
        Map<String, Map<String, IValue<BigDecimal>>> newValMap = CommonUtil.copyToNew(oldValMap);
        paraRelList.forEach(rel -> {
            // 参数代码
            String paraCode = rel.getParaCode();
            // 关系计算对象的值
            BigDecimal relVal = rel.getRelValue();

            // 占比金额
            if (StringUtil.equals(rel.getRelItem(), MIXAMOUNTKEY)) {
                relVal = (BigDecimal)getServiceInfo("assetAmtVal");
            }
            BigDecimal newRelVal = relVal == null ? BigDecimal.ZERO : relVal;
            newRelVal = rel.doFormula(newRelVal);
            String newParaCode = oldValMap.containsKey(paraCode) ? paraCode : reverseCode(
                paraCode);
            // 预占剩余额度
            BigDecimal ocpAmt = ocpAmtMap.get(newParaCode);
            // 参与计算的额度
            BigDecimal temVal = null == ocpAmt ? BigDecimal.ZERO : ocpAmt;
            // Map<Redis识别域, 参数值>
            Map<String, IValue<BigDecimal>> valMap = oldValMap.get(newParaCode);
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            newValMap.put(newParaCode, calcVal(valMap, newRelVal, temVal, rel.getRelType()));
        });
        return newValMap;
    }

    private Map<String, IValue<BigDecimal>> calcVal(Map<String, IValue<BigDecimal>> valMap,
                                                    BigDecimal newRelVal, BigDecimal temVal,
                                                    String opr) {
        Map<String, IValue<BigDecimal>> temp = new HashMap<>();
        valMap.entrySet().forEach(en -> {
            String key = en.getKey();
            // 公式求出值
            BigDecimal tempVal = BigDecimal.ZERO.max(newRelVal.subtract(temVal));
            // 参数值
            IValue<BigDecimal> paraVal = en.getValue();
            BigDecimal par = paraVal == null ? BigDecimal.ZERO : paraVal.value();
            IValue<BigDecimal> newVal = new Value<>(
                str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
            // 指标参数值计算
            newVal.set(CommonUtil.math(par, tempVal, opr));
            temp.put(key, newVal);
        });
        return temp;
    }

    /**
     * 取出指标相关的风控事项配置信息
     * 
     * @param eventList 风控事项列表
     * @param paraList 指标相关参数信息
     * @return 风控事项配置信息
     */
    @SuppressWarnings("unchecked")
    private List<IEventParaRel> getParaRelList(List<IEvent> eventList, List<String> paraList) {
        List<IEventParaRel> resList = new ArrayList<>();
        eventList.forEach(event -> resList.addAll(CommonUtil.filter(event.getEventParaRel(),
            ArrayList.class, rel -> isContains(paraList, rel.getParaCode()))));
        return resList;
    }

    /**
     * 根据趋势计算指标参数值
     * 
     * @param indTrend 指标趋势信息
     * @param ocpList 预占实占列表
     * @param paraList 指标参数列表
     * @param paraValMap 指标参数值列表
     */
    private void calcParamWithTrend(IIndicatorTrend indTrend, Map<String, IOccupyRelParam> ocpList,
                                    List<String> paraList,
                                    Map<String, Map<String, IValue<BigDecimal>>> paraValMap) {
        // 若指标趋势信息存在
        for (IOcpTrend ocpTrend : indTrend.getOcpSet()) {
            // 取得相关预占信息
            IOccupyRelParam ocpInfo = ocpList.get(ocpTrend.getOcpCode());
            // 非预占信息则不处理
            if (null == ocpInfo || PRE_OCCUPY != ocpInfo.getOccupyType()) {
                continue;
            }
            // 构建处理结构
            Map<IOcpRelParamItem, List<IOcpRelParamItem>> ocpItems = createOcpItems(ocpInfo,
                ocpList);
            // 过滤出相关的预占实占信息
            ocpItems = CommonUtil.mapFilter(ocpItems,
                item -> isContains(paraList, item.getKey().getParamCode()));
            // 趋势向好假设不发生，趋势向坏假设发生规则处理
            calcOcpAmt(ocpTrend, ocpItems, paraValMap);
        }
    }

    /**
     * 构建预占实占处理结构
     * 
     * @param ocpInfo 预占信息
     * @param ocpList 预占实占列表
     * @return 预占实占处理结构
     */
    private Map<IOcpRelParamItem, List<IOcpRelParamItem>> createOcpItems(IOccupyRelParam ocpInfo,
                                                                         Map<String, IOccupyRelParam> ocpList) {
        Map<IOcpRelParamItem, List<IOcpRelParamItem>> result = new HashMap<>();
        // 参数值计算列表
        Map<String, IOcpRelParamItem> ocpItems = ocpInfo.getItems();
        // 预占下的实占代码
        Set<String> ocpCodeList = ocpInfo.getOcpCodeList();
        // 若不存在实占信息
        if (CollectionUtils.isEmpty(ocpCodeList)) {
            if (ocpItems == null) {
                return result;
            }

            ocpItems.entrySet().forEach(en -> {
                if (null == en.getValue()) {
                    return;
                }
                result.put(en.getValue(), new ArrayList<>());
            });
            return result;
        }
        // 过滤出预占下所有实占信息
        Map<String, IOccupyRelParam> target = CommonUtil.mapFilter(ocpList,
            en -> ocpCodeList.contains(en.getKey()));
        ocpItems.entrySet().forEach(en -> {
            if (null == en.getValue()) {
                return;
            }
            String paraCode = en.getKey();
            // 实时粒度的参数代码
            String paraCodeRel = replaceCode(paraCode, BussinessDictCodeListRds.参数粒度_实时);
            List<IOcpRelParamItem> value = new ArrayList<>();
            target.entrySet().forEach(rEn -> {
                IOccupyRelParam tempOcp = rEn.getValue();
                if (null != tempOcp && !CollectionUtils.isEmpty(tempOcp.getItems()) && StringUtil.isNotEmpty(tempOcp.getItems().get(paraCodeRel))) {
                    value.add(tempOcp.getItems().get(paraCodeRel));
                }
            });
            result.put(en.getValue(), value);
        });
        return result;
    }

    private void calcOcpAmt(IOcpTrend ocpTrend,
                            Map<IOcpRelParamItem, List<IOcpRelParamItem>> ocpItems,
                            Map<String, Map<String, IValue<BigDecimal>>> paraValMap) {
        if (!CollectionUtils.isEmpty(ocpItems)) {
            ocpItems.entrySet().forEach(en -> {
                // 预占信息
                IOcpRelParamItem preOcp = en.getKey();
                // 预占下的所有实占信息
                List<IOcpRelParamItem> ocpList = en.getValue();
                // 原预占触发的维度
                String paraCode = preOcp.getParamCode();
                // 如果趋势向坏，取原预占没有触发的维度，否则，取原预占触发的维度
                paraCode = ocpTrend.isTrendBad() ? reverseCode(paraCode) : paraCode;
                // 参数值信息
                Map<String, IValue<BigDecimal>> temp = paraValMap.get(paraCode);
                if (CollectionUtils.isEmpty(temp)) {
                    return;
                }
                // 预占剩余额度
                BigDecimal ocpAmt = getOcpAmt(ocpTrend.isTrendBad(), preOcp, ocpList);
                temp.entrySet().forEach(valEn -> {
                    IValue<BigDecimal> val = valEn.getValue();
                    if (null == val) {
                        return;
                    }
                    // 参数值与预占剩余额度计算
                    val.set(CommonUtil.math(val.value(), preOcp.doFormula(ocpAmt), preOcp.getOccupyOper()));
                });
                // 影响的参数设置为相同值
                paraValMap.put(paraCode, temp);
            });
        }
    }

    /**
     * 根据趋势取得预占剩余额度（或其负值）
     * 
     * @param isTrendBad 是否趋势向坏
     * @param preOcp 预占信息
     * @param ocpList 预占下的实占信息
     * @return 预占剩余额度（预占信息.额度 - sum(实占信息.额度)）
     */
    private BigDecimal getOcpAmt(boolean isTrendBad, IOcpRelParamItem preOcp,
                                 List<IOcpRelParamItem> ocpList) {
        // 预占额度
        BigDecimal ocpAmt = preOcp.defaultDiffVal();
        // 若实占信息存在，则减去所有实占额度
        if (!CollectionUtils.isEmpty(ocpList)) {
            for (IOcpRelParamItem ocp : ocpList) {
                ocpAmt = ocpAmt.subtract(ocp.getOccupyVal());
            }
        }
        // 趋势向坏则返回预占剩余额度，否则返回预占剩余额度的负值
        return isTrendBad ? ocpAmt : BigDecimal.ZERO.max(ocpAmt).negate();
    }

    /**
     * 构建并保存本次预占实占的趋势信息到本地缓存
     * 
     * @param indMap 指标信息
     * @param eveParamList 本次风控影响的参数值
     * @param indTrendMap 指标趋势信息
     * @param dataMap 请求报文
     */
    private void addThisTrend(Map<String, IIndicator> indMap, Set<String> eveParamList,
                              Map<String, IIndicatorTrend> indTrendMap,
                              Map<String, Object> dataMap) {
        // 组合代码
        String portfolioCode = (String)dataMap.get(PORTFOLIOCODE);
        Map<String, IIndicatorTrend> tempMap = CollectionUtils.isEmpty(
            indTrendMap) ? new HashMap<>() : indTrendMap;
        // 遍历指标信息
        indMap.entrySet().forEach(en -> {
            IIndicator ind = en.getValue();
            if (!isMoreThenTwo(ind, eveParamList)) {
                return;
            }
            // 查询趋势信息的Key -> 指标代码:组合代码
            String key = ind.getIndId() + GlobalConstant.COLON + portfolioCode;
            // 当存在趋势信息时，将本次趋势信息添加到原信息列表中
            if (tempMap.containsKey(key) && tempMap.get(key) != null) {
                return;
            }
            tempMap.put(key, newIndicatorTrend(ind, dataMap));
        });
        // 保存信息到本地缓存
        setServiceInfo(INDICATOR_TREND, tempMap);
    }

    private String getPreOcpCode(Map<String, Object> dataMap) {
        String preOcpCode = (String)dataMap.get("preOccupyCode");
        if (StringUtil.isEmpty(preOcpCode)) {
            return null;
        }
        return preOcpCode + GlobalConstant.COLON + PRE_OCCUPY;
    }

    protected void removeSelf(Set<String> ocpCodeList, Map<String, Object> dataMap) {
        Object eventId = dataMap.get(EVENT_ID);
        if (!CollectionUtils.isEmpty(ocpCodeList) && StringUtil.isNotEmpty(eventId)) {
            ocpCodeList.remove(eventId + GlobalConstant.COLON + PRE_OCCUPY);
        }
    }

    /**
     * 新建一个指标趋势信息
     * 
     * @param ind 指标信息
     * @param dataMap 请求报文
     * @return 指标趋势信息
     */
    private IIndicatorTrend newIndicatorTrend(IIndicator ind, Map<String, Object> dataMap) {
        ImplIndicatorTrend indTrend = new ImplIndicatorTrend();
        indTrend.setIndCode(ind.getIndId());
        indTrend.setPortfolioCode((String)dataMap.get(PORTFOLIOCODE));
        return indTrend;
    }

    protected boolean isMoreThenTwo(IIndicator ind, Set<String> eveParamList) {
        List<String> indParamList = ind.getIndParamList();
        if (null == indParamList || indParamList.size() < 2) {
            return false;
        }
        Set<String> newList = new HashSet<>();
        eveParamList.forEach(
            str -> newList.add(replaceCode(str, BussinessDictCodeListRds.参数粒度_实时)));
        int count = 0;
        for (String paraCode : indParamList) {
            if (newList.contains(replaceCode(paraCode, BussinessDictCodeListRds.参数粒度_实时))) {
                count++ ;
            }
        }
        return count >= 2;
    }

    protected String replaceCode(String str, BussinessDictCodeListRds dict) {
        String[] arr = StringUtil.split(str, GlobalConstant.COLON);
        if (arr == null || arr.length != 2) {
            return str;
        }
        return arr[0] + GlobalConstant.COLON + dict.getValue();
    }

    protected String reverseCode(String code) {
        String[] arr = StringUtil.split(code, GlobalConstant.COLON);
        if (arr == null || arr.length != 2) {
            return code;
        }
        if (CommonUtil.isType(arr[1], BussinessDictCodeListRds.参数粒度_买入)) {
            return arr[0] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_卖出.getValue();
        }
        else if (CommonUtil.isType(arr[1], BussinessDictCodeListRds.参数粒度_卖出)) {
            return arr[0] + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_买入.getValue();
        }
        return code;
    }

    /**
     * 设置指标趋势信息
     * 
     * @param indMap 指标列表
     * @param dataMap 请求报文
     * @param occupyCode 预占编号
     */
    public void setTrend(Map<String, IIndicator> indMap, Map<String, Object> dataMap,
                         String occupyCode) {
        if (CollectionUtils.isEmpty(indMap)) {
            return;
        }
        Object portfolioCode = dataMap.get(PORTFOLIOCODE);
        indMap.entrySet().forEach(en -> {
            IIndicator ind = en.getValue();
            if (null == ind.getIndicatorResult()
                || null == ind.getIndicatorResult().get(portfolioCode)) {
                return;
            }
            setTrend(dataMap, occupyCode, ind.getIndId(),
                ind.getIndicatorResult().get(portfolioCode).getIndicatorTrend());
        });
    }

    public void addIndTrendToMap(IIndicatorTrend indTrend, Map<String, IIndicatorTrend> trendMap) {
        if (trendMap.containsKey(indTrend.getField())) {
            IIndicatorTrend oldTrend = trendMap.get(indTrend.getField());
            Set<IOcpTrend> ocpSet = indTrend.getOcpSet();
            if (!CollectionUtils.isEmpty(ocpSet)) {
                ocpSet.forEach(ocpTrend -> recoverTrend(oldTrend, ocpTrend));
            }
        }
        else {
            trendMap.put(indTrend.getField(), indTrend);
        }
    }

    public List<IIndicatorTrend> createIndTrend(String portfolioCode, String occupyCode,
                                          Map<String, IIndicator> indMap) {
        if (CollectionUtils.isEmpty(indMap)) {
            return new ArrayList<>();
        }
        List<IIndicatorTrend> resList = new ArrayList<>();
        indMap.entrySet().forEach(en -> {
            IIndicator ind = en.getValue();
            if (null == ind.getIndicatorResult()
                || null == ind.getIndicatorResult().get(portfolioCode)) {
                return;
            }
            ImplIndicatorTrend indTrend = new ImplIndicatorTrend();
            IOcpTrend ocpTrend = newOcpTrend(occupyCode);
            ocpTrend.setTrend(ind.getIndicatorResult().get(portfolioCode).getIndicatorTrend());
            indTrend.setIndCode(ind.getIndId());
            indTrend.setPortfolioCode(portfolioCode);
            indTrend.addOcpTrend(ocpTrend);
            resList.add(indTrend);
        });
        return resList;
    }

    @SuppressWarnings("unchecked")
    public void setTrend(Map<String, Object> dataMap, String occupyCode, String indCode,
                         int trend) {
        Map<String, IIndicatorTrend> indTrendMap = (Map<String, IIndicatorTrend>)getServiceInfo(
            INDICATOR_TREND);
        IOcpTrend ocpTrend = newOcpTrend(occupyCode);
        ocpTrend.setTrend(trend);
        String code = indCode + GlobalConstant.COLON + dataMap.get(PORTFOLIOCODE);
        IIndicatorTrend indTrend = indTrendMap.get(code);
        recoverTrend(indTrend, ocpTrend);
        setServiceInfo(INDICATOR_TREND, indTrendMap);
    }

    private void recoverTrend(IIndicatorTrend indTrend, IOcpTrend ocpTrend) {
        if (null != indTrend) {
            Set<IOcpTrend> ocpSet = indTrend.getOcpSet();
            if (!CollectionUtils.isEmpty(ocpSet)) {
                for (IOcpTrend item : ocpSet) {
                    if (ocpTrend.equalsW(item)) {
                        ocpSet.remove(item);
                        break;
                    }
                }
            }
            indTrend.addOcpTrend(ocpTrend);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveIndTrendList() {
        Map<String, IIndicatorTrend> indTrendMap = (Map<String, IIndicatorTrend>)getServiceInfo(
            INDICATOR_TREND);
        if (!CollectionUtils.isEmpty(indTrendMap)) {
            indTrendSrc.saveToRedis(
                indTrendMap.values().toArray(new IIndicatorTrend[indTrendMap.size()]));
        }
    }

    /**
     * 构建本次预占的趋势信息
     * 
     * @param occupyCode 预占编号
     * @return 本次预占的趋势信息
     */
    private IOcpTrend newOcpTrend(String occupyCode) {
        ImplOcpTrend ocpTrend = new ImplOcpTrend();
        ocpTrend.setOcpCode(occupyCode);
        return ocpTrend;
    }

    /**
     * 取得 服务线程数据
     * 
     * @return 服务线程数据
     */
    private Map<String, Object> getServiceInfo() {
        return serviceInfo.get();
    }

    /**
     * 设置 服务线程数据
     * 
     * @param key 主键
     * @param info 数据
     */
    public void setServiceInfo(String key, Object info) {
        getServiceInfo().put(key, info);
    }

    /**
     * 取得 服务线程数据
     * 
     * @param key 主键
     * @return 服务线程数据
     */
    public Object getServiceInfo(String key) {
        return getServiceInfo().get(key);
    }

    /**
     * 清除缓存
     */
    public void cleanServiceInfo() {
        serviceInfo.remove();
    }

    public interface ITrendCalc {

        /**
         * 处理指标计算
         * 
         * @param indMap 指标列表
         * @param oldValMap 指标参数原值列表
         * @param newValMap 指标参数新值列表
         */
        void handle(Map<String, IIndicator> indMap,
                    Map<String, Map<String, IValue<BigDecimal>>> oldValMap,
                    Map<String, Map<String, IValue<BigDecimal>>> newValMap);
    }

    public Map<String, Map<String, Object>> parseParaVal(Map<String, Map<String, String>> ptlParamMap,
                                                         Map<String, Map<String, IValue<BigDecimal>>> valMap) {
        // 返回Map<组合代码, Map<指标参数代码, 指标参数值>>
        Map<String, Map<String, Object>> pValMap = new HashMap<>();
        // 遍历组合与相关指标索引
        ptlParamMap.entrySet().forEach(en -> {
            // 组合代码
            String ptlCode = en.getKey();
            // Map<参数代码, Redis识别域>
            Map<String, String> temp = en.getValue();
            // Map<参数代码, 参数值>
            Map<String, Object> pMap = new HashMap<>();
            temp.entrySet().forEach(item -> {
                // 指标参数代码
                String key = item.getKey();
                // Redis识别域
                String val = item.getValue();
                Map<String, IValue<BigDecimal>> valTemp = valMap.get(key);
                if (CollectionUtils.isEmpty(valTemp))
                    return;
                // 指标参数值
                IValue<BigDecimal> bVal = valTemp.get(val);
                pMap.put(key, null == bVal ? BigDecimal.ZERO : bVal.value());
            });
            pValMap.put(ptlCode, pMap);
        });
        return pValMap;
    }

    @SuppressWarnings("unchecked")
    public IPtlIndicator newPtlInd(IPtlIndicator ptlInd, Map<String, IIndicator> newIndMap) {
        ImplPtlIndicator newPtlInd = new ImplPtlIndicator();
        BeanUtil.copyPropertiesIgnoreNull(newPtlInd, ptlInd);
        Map<String, Set<String>> ptlIndList = newPtlInd.getPtlIndicator();
        Map<String, Set<String>> newPtlIndList = new HashMap<>();
        ptlIndList.entrySet().forEach(en -> {
            String key = en.getKey();
            Set<String> set = en.getValue();
            Set<String> newSet = new HashSet<>();
            newSet.addAll(
                CommonUtil.filter(set, ArrayList.class,
                    str -> !CollectionUtils.isEmpty(newIndMap) && newIndMap.containsKey(str)));
            newPtlIndList.put(key, newSet);
        });
        newPtlInd.setPtlIndicatorRedis(newPtlIndList);
        return newPtlInd;
    }
}
