/*
 * 文件名：RiskRedisInitService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.redisinit.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.asset.service.BaseCommonAssetCommonService;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.mock.asset.MockAssetInfoDto;
import com.joyintech.tams.invest.common.dto.mock.basicdata.MockQuotationInfoDto;
import com.joyintech.tams.invest.common.redisinit.dto.*;
import com.joyintech.tams.invest.common.riskinfrds.info.service.*;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskIndividConditionDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.common.base.dao.RskIsupLogMapper;
import com.joyintech.tams.invest.common.component.base.core.ICond;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dao.RskRedisInitMapper;
import com.joyintech.tams.invest.common.redisinit.service.RiskIndConditionLineService;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.EventMap;
import com.joyintech.tams.invest.common.riskinfrds.event.service.RskEventService;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorService;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetIndex;
import com.joyintech.tams.invest.common.riskinfrds.occupy.service.OccupyRelParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.riskinfrds.prod.service.PtlIndicatorService;
import com.joyintech.tams.invest.common.risktrialrds.dao.SupervisionCalcDao;
import com.joyintech.tams.invest.common.risktrialrds.dto.PointDetail;
import com.joyintech.tams.invest.common.risktrialrds.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;

/**
 * redis数据初始化
 *
 * @author 裴家俊
 * @version 1.0 2019年9月2日
 * @see RiskRedisInitServiceImpl
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class RiskRedisInitServiceImpl extends BaseService implements RiskRedisInitService<ICond> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private RskRedisInitMapper rskRedisInitMapper;

    @Autowired
    private RskIsupLogMapper rskIsupLogMapper;

    @Autowired
    private RskEventService rskEventService;

    @Autowired
    private IndicatorParamService indicatorParamService;

    @Autowired
    private IndicatorParamValService paramValService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private PtlIndicatorService ptlIndicatorService;

    @Autowired
    private ProdInfoRedisService prodInfoRedisService;

    @Autowired
    private AssetInfoRedisService assetInfoRedisService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private OccupyRelParamService occupyRelParamService;

    @Autowired
    private SupervisionCalcDao supervisionCalcDao;

    @Autowired(required = false)
    public IPlatformDate platformDate;

    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public AssetIndexService assetIndexSrc;

    @Autowired
    public ExchargeInfoService exchangeSrc;

    @Autowired
    public FundValuationService fundValuationSrc;

    // add by zhangrui1 start
    @Autowired
    private ProdAssetAmountService prodAssetAmtSrc;
    // add by zhangrui1 end

    @Autowired
    private ProdAssetDurationService prodAssetDurationSrc;

    @Autowired(required = false)
    private RiskTrialCommMapper riskTrialCommMapper;

    // add by zhangrui1 自定义条件阈值 start
    @Autowired
    private RiskIndConditionLineService lineService;
    // add by zhangrui1 自定义条件阈值 end

    private Map<String,Object> batchSucMap=new HashMap<>();

    @Autowired
    private BaseCommonAssetCommonService baseCommonAssetCommonService;

    /**
     * 获取待处理数据
     *
     * @param cond 传递参数
     * @since 1.0
     */
    public Map<String,Object> execute(ICond cond) {
        batchSucMap = new HashMap<>();
        // 初始化平台日
        setPlatDate(cond);
        // ----消除redis中所有数据----------
        indicatorParamService.flushDBExceptKey("tm:");
        // 处理组合信息
        dealPortData(cond);
        // 处理资产信息
        dealAssetData(cond);
        // 处理日历信息
        dealCalendarData(cond);
        // 处理风险事件
        dealRiskEvent();
        // 处理风险指标参数
        dealRiskIndParam(cond);
        // 处理风险指标
        dealRiskIndicator(cond);
        // 处理组合关联指标
        dealRiskPtlIndicator(cond);
        // 处理资产索引信息
        //dealAssetIndex(cond);
        // 处理汇率行情信息
        dealExchargeInfo(cond);
        // 处理基金净值类估值行情信息
        dealFundValuation(cond);
        // add by zhangrui1 start
        // 处理资产实时规模信息
        dealProdAssetAmount(cond);
        // add by zhangrui1 end
        return batchSucMap;
    }

    @Override
    public void setPlatDate(ICond var) {
        if (!StringUtil.isNotEmpty(var.getPlateDate())) {
            Date sysDate = platformDate.getSysDate();
            var.setPlateDate(StringUtil.isNotEmpty(sysDate) ? sysDate : new Date());
        }
    }

    /**
     * 处理组合信息
     */
    public void dealPortData(ICond cond) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("paramIdList", cond.getPortfIdArray());
        dealValDate(cond,mapPass);
        //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
        List<ImplPortf> implPortList = rskRedisInitMapper.getPtlPortfolioData(mapPass);
        if (cond.isDel()) {
            prodInfoRedisService.deleteInfoFromRedis(RedisKeyEnum.产品基本信息.key(),
                    cond.getPortfIdArray().toArray(new String[cond.getPortfIdArray().size()]));
        }
        else {
            ImplPortf[] arr = implPortList.toArray(new ImplPortf[implPortList.size()]);
            clearRedisProperty(arr);
            prodInfoRedisService.saveToRedis(arr);
        }
    }

    /**
     * 处理资产信息
     */
    public void dealAssetData(ICond cond) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("paramIdList", cond.getAssetIdArray());
        dealValDate(cond,mapPass);
        //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
        List<ImplAsset> implAssetList = rskRedisInitMapper.getPtlAssetData(mapPass);
        if (cond.isDel()) {
            assetInfoRedisService.deleteInfoFromRedis(RedisKeyEnum.资产基本信息.key(),
                    cond.getAssetIdArray().toArray(new String[cond.getAssetIdArray().size()]));
        }
        else {
            ImplAsset[] arr = implAssetList.toArray(new ImplAsset[implAssetList.size()]);
            clearRedisProperty(arr);
            assetInfoRedisService.saveToRedis(arr);
        }
    }

    /**
     * 处理日历信息
     */
    public void dealCalendarData(ICond cond) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("paramIdList", cond.getCalendarIdArray());
        dealValDate(cond,mapPass);
        List<ImplCalendar> returnCalList = new ArrayList<>();
        //接一年之内的日历数据
        List<ImplCalendar> implCalendarList = rskRedisInitMapper.getCalendarData(mapPass);
        Map<String, List<ImplCalendar>> calendarListMap = implCalendarList.stream().collect(
                Collectors.groupingBy(ImplCalendar::getCalendarId));
        calendarListMap.keySet().forEach(key -> {
            ImplCalendar implCalendar = new ImplCalendar();
            Map<String, Long> calendarMap = new HashMap<>();
            calendarListMap.get(key).stream().forEach(item -> {
                calendarMap.put(DateUtil.format(item.getCdate()), item.getIsHoliday());
            });
            implCalendar.setCalendarId(key);
            implCalendar.setCalendarMap(calendarMap);
            returnCalList.add(implCalendar);
        });
        if (cond.isDel()) {
            calendarService.deleteInfoFromRedis(RedisKeyEnum.日历信息.key(),
                    cond.getCalendarIdArray().toArray(new String[cond.getCalendarIdArray().size()]));
        }
        else {
            ImplCalendar[] arr = returnCalList.toArray(new ImplCalendar[returnCalList.size()]);
            clearRedisProperty(arr);
            calendarService.saveToRedis(arr);
        }
    }

    /**
     * 处理预占/实占信息
     */
    public void dealOccupyRelParamData() {
        List<ImplOccupyRel> implOccupyRelList = rskRedisInitMapper.getOccupyRelData();
        clearRedisProperty(implOccupyRelList.toArray(new ImplOccupyRel[implOccupyRelList.size()]));
        occupyRelParamService.saveToRedis(
                implOccupyRelList.toArray(new ImplOccupyRel[implOccupyRelList.size()]));
    }

    /**
     * 处理风险事件
     */
    public void dealRiskEvent() {
        List<ImplEvent> implEventList = rskRedisInitMapper.getRskEventData();
        List<ImplEventItem> implEventItemList = rskRedisInitMapper.getRskEventItemData();
        List<ImplEventParaRel> implEventParaRelList = rskRedisInitMapper.getRskEventParaResData();
        EventMap eventEntry = new EventMap();
        implEventList.forEach(item -> {
            List<ImplEventItem> iEventItemList = implEventItemList.stream().filter(
                    entry -> entry.getEventCode().equals(item.getEventCode())).collect(
                    Collectors.toList());
            if (StringUtil.isEmptyList(iEventItemList)) {
                return;// 执行下一个
            }
            List<ImplEventParaRel> iEventParaRelList = implEventParaRelList.stream().filter(
                    entry -> entry.getEventCode().equals(item.getEventCode())).collect(
                    Collectors.toList());
            if (StringUtil.isEmptyList(iEventParaRelList)) {
                return;// 执行下一个
            }
            clearRedisProperty(iEventItemList.toArray(new ImplEventItem[iEventItemList.size()]));
            clearRedisProperty(
                    iEventParaRelList.toArray(new ImplEventParaRel[iEventParaRelList.size()]));
            item.setImplEventItemList(iEventItemList);// 风险事项项赋值
            item.setiEventParaRelList(iEventParaRelList);// 风险事件关联参数赋值
            clearRedisProperty(item);
            eventEntry.add(item);
        });
        rskEventService.saveToRedis(eventEntry);
    }

    /**
     * 处理风险指标参数
     *
     * @param cond
     */
    public void dealRiskIndParam(ICond cond) {
        setPlatDate(cond);// 初始化平台日
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("cond", cond);
        mapPass.put("paramIdList", cond.getParamCodeArray());
        dealValDate(cond,mapPass);
        //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
        List<ImplParam> implIndParamList = rskRedisInitMapper.getRskIndParamDtlData(mapPass);
        Map<String, List<ImplParam>> implParmMap = implIndParamList.stream().collect(
                Collectors.groupingBy(ImplParam::getParaCode));
        if (!CollectionUtils.isEmpty(implParmMap)) {
            implParmMap.keySet().forEach(key -> {
                implParmMap.get(key).forEach(item -> {
                    item.setFreshCanTrdRedis(item.getIsFreshCanTrd());
                    item.setEnumTypeRedis(item.getIsForbid());
                    item.setFieldArrayRedis(item.getRedisField());
                });
            });
            // 保存参数值
            if (cond.isUpdParVal()) {
                dealParamValue(implParmMap, cond.getPlateDate(), cond);
            }
        }
        if (cond.isDel()) {
            indicatorParamService.deleteInfoFromRedis(RedisKeyEnum.指标参数配置.key(),
                    cond.getParamCodeArray().toArray(new String[cond.getParamCodeArray().size()]));
        }
        else {
            ImplParam[] arr = implIndParamList.toArray(new ImplParam[implIndParamList.size()]);
            clearRedisProperty(arr);
            indicatorParamService.saveToRedis(arr);
        }
    }

    /**
     * 解析动态SQL处理指标参数值
     *
     * @param implParmMap
     * @param sysDate
     */
    private void dealParamValue(Map<String, List<ImplParam>> implParmMap, Date sysDate, ICond cond) {
        //初始化参数日志信息
        batchSucMap.clear();
        Map<String, Map<String, IValue<Object>>> totalParamValMap = new HashMap<>();
        CountDownLatch countDown = new CountDownLatch(implParmMap.keySet().size());
        List<Future<Exception>> futures = new ArrayList<>(implParmMap.keySet().size());
        implParmMap.keySet().forEach(key -> {
            futures.add(threadPoolTaskExecutor.submit(() -> {
                try {
                    implParmMap.get(key).forEach(item -> {
                        if(StringUtil.equals(item.getParamMathType(),BussinessDictCodeListRds.指标参数计算方式_DB.getValue())
                                ||StringUtil.equals(item.getParamMathType(),BussinessDictCodeListRds.指标参数计算方式_PLUGIN.getValue())){
                            return;
                        }
                        item.setFreshCanTrdRedis(item.getIsFreshCanTrd());
                        item.setEnumTypeRedis(item.getIsForbid());
                        item.setFieldArrayRedis(item.getRedisField());
                        Map<String, IValue<Object>> mkParamValMap = new HashMap<>();
                        List<Map<String,Object>> offList = null;
                        if (cond.isLoadOffset()) {
                            offList = getOffsetVal(item, sysDate, cond);
                        }
                        final List<Map<String,Object>> offListTmp = offList;
                        paramForEach(item, sysDate, ob -> {
                            if (ob.keySet().size() > 1) {
                                Object value = ob.get(RiskUtil.redisVal);
                                Object rdsKey = ob.get(RiskUtil.redisKey);
                                if (value == null || rdsKey == null) {
                                    return;
                                }
                                //添加对于日期及禁可投非数字的处理
                                if(item.getIsForbid().equals(BussinessDictCodeListRds.是否_是.getValue())||StringUtil.equals(item.getParamDataType(),BussinessDictCodeListRds.参数数据类型_日期.getValue())){
                                    Value<Object> rValue = new Value<>(str -> str);
                                    if(StringUtil.equals(item.getParamDataType(),BussinessDictCodeListRds.参数数据类型_日期.getValue())){
                                        rValue = new Value<>(str -> DateUtil.format(DateUtil.parse(str),DateUtil.DEFAULT_DATE_FORMAT_EN));
                                    }
                                    rValue.value(value.toString());
                                    mkParamValMap.put(rdsKey.toString(), rValue);
                                }else {
                                    Value<Object> rValueObj = new Value<>(str -> new BigDecimal(str));
                                    Value<BigDecimal> rValue = new Value<>(str -> new BigDecimal(str));
                                    rValue.value(value.toString());
                                    if (cond.isLoadOffset()) {
                                        rValue.set(addOffset(rValue.value(), offListTmp, rdsKey.toString()));
                                        if (cond.getCondList() == null || cond.getCondList().contains(rdsKey)) {
                                            rValueObj.value(rValue.value().toString());
                                            mkParamValMap.put(rdsKey.toString(), rValueObj);
                                        }
                                        return;
                                    }
                                    rValueObj.value(rValue.value().toString());
                                    mkParamValMap.put(rdsKey.toString(), rValueObj);
                                }

                            }
                        });
                        // 指标代码+参数粒度为KEY，存储对应的以组合+资产代码为key的Map参数值集合
                        totalParamValMap.put(item.getField(), mkParamValMap);

                    });
                }
                catch (Exception e) {
                    logger.error("指标参数："+key+GlobalConstant.COMMA+e.getMessage(), e);
                    return new Exception(new StringBuilder().append("指标参数：").append(key).append(" 处理异常！").append(e.getMessage()).toString());
                }
                finally {
                    // 线程计数
                    countDown.countDown();
                }
                return null;
            }));

        });
        try {
            // 等待线程结束
            countDown.await();
        }
        catch (InterruptedException e) {
            logger.error("线程池中断异常", e);
            Thread.currentThread().interrupt();
        }
        List<Exception> exceptions = new ArrayList<>();
        for(Future<Exception> future : futures){
            try {
                Exception expFuture = future.get();
                if(null != expFuture){
                    exceptions.add(expFuture);
                }
            } catch (InterruptedException e) {
                logger.error("线程池获取错误信息对象超时！", e);
            } catch (ExecutionException e) {
                logger.error("线程池获取错误信息对象执行异常！", e);
            }
        }
        if(exceptions.size()>0){
            batchSucMap.put("redisErrorMessageList",exceptions);
        }
        if (!CollectionUtils.isEmpty(totalParamValMap)) {
            totalParamValMap.entrySet().forEach(en -> {
                if (StringUtil.isNotEmpty(en.getKey())) {
                    indicatorParamService.deleteInfoFromRedis(RedisKeyEnum.指标参数值.key() + en.getKey(), null);
                }
            });
        }
        paramValService.setParamVal(totalParamValMap);

    }

    /**
     * 查询每个SQL的指标参数值
     *
     * @param itemEntry
     * @param sysDate
     * @param val
     */
    private void paramForEach(ImplParam itemEntry, Date sysDate,
                              Consumer<Map<String, Object>> val) {
        // 固定值参数的值存在指标参数信息里面，不需要和SQL值存一起
        if (CommonUtil.isType(itemEntry.getParaDisplayType(),
                BussinessDictCodeListRds.指标参数实现类型_固定值)) {
            return;
        }
        String content = itemEntry.getContent();
        // 如果是自定义参数,取出SQL
        if (CommonUtil.isType(itemEntry.getParaDisplayType(),
                BussinessDictCodeListRds.指标参数实现类型_自定义)) {
            JsonNode node = JsonUtils.toJsonNode(content);
            if(!StringUtil.isNotEmpty(node)){
                return;
            }
            JsonNode sql = node.findValue("sql");
            content = sql == null ? "" : sql.asText();
        }
        // SQL文空时，不进行redis更新
        if (StringUtil.isEmpty(content)) {
            return;
        }
        // 构造参数条件
        SystemParam sysParam = new SystemParam();
        sysParam.setValDate(Const.DateFmt.format(sysDate));
        logger.debug("待执行的脚本:{}:{}", itemEntry.getParaCode(), content);

        List<Map<String, Object>> resultSet = supervisionCalcDao.selectOne(
                sysParam.replaceForString(content));
        resultSet.forEach(val);
    }

    private List<Map<String,Object>> getOffsetVal(ImplParam itemEntry, Date sysDate, ICond cond){
        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("valDate", sysDate);
        paraMap.put("paraCode", itemEntry.getParaCode() + GlobalConstant.COLON + itemEntry.getParaGranularity());
        paraMap.put("condList", cond.getCondList());
        return rskRedisInitMapper.selectParamValOffset(paraMap);
    }

    private BigDecimal addOffset(BigDecimal val, List<Map<String,Object>> offList, String rkey) {
        if (!StringUtil.isEmptyList(offList)) {
            for(Map<String,Object> off : offList){
                Object rKeyObj = off.get(RiskUtil.redisKey);
                if (StringUtil.equals(rkey, rKeyObj)) {
                    return val.add(new BigDecimal(off.get(RiskUtil.redisVal).toString()));
                }
            }
        }
        return val;
    }

    /**
     * 处理风险指标
     *
     * @param cond
     */
    public void dealRiskIndicator(ICond cond) {
        setPlatDate(cond);// 初始化平台日
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("isEnabled", BussinessDictCodeListRds.使用状态_启用.getValue());
        mapPass.put("cond", cond);
        mapPass.put("indIdList", cond.getIndicatorCodeArray());
        dealValDate(cond,mapPass);
        //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
        List<ImplParam> implIndParamList = rskRedisInitMapper.getFixParamDtlData(mapPass);
        Set<String> fixParamList = implIndParamList.stream().map(
                ImplParam::getParaCode).collect(Collectors.toSet());
        List<ImplIndicator> implIndList = rskRedisInitMapper.getRskIndicatorData(mapPass);
        // add by zhangrui1 自定义条件阈值 start
        if (CollectionUtils.isEmpty(implIndList)) {
            return;
        }
        Map<String, Map<String, IIndividLimit>> limitMap = lineService.getRiskIndConditionLine(
            cond.getPlateDate(), implIndList.stream().map(ImplIndicator::getIndId).collect(Collectors.toList()));
        // add by zhangrui1 自定义条件阈值 end
        // 查询联合设置
        List<RskIndividUnite> rskUniteList = rskRedisInitMapper.getRskIndUniteData(mapPass);
        //以产品维度分组
        Map<String, List<RskIndividUnite>> mapTaskMap = rskUniteList.stream().collect(
                Collectors.groupingBy(RskIndividUnite::getPrtBagId));

        mapPass.put("uniteType", BussinessDictCodeListRds.阶段类型_特殊阶段.getValue());
        // 查询特殊阈值
        List<RskIndividSpecialStage> rskSpecialList = rskRedisInitMapper.getRskIndIndivLimit(
                mapPass);

        mapPass.put("uniteType", BussinessDictCodeListRds.阶段类型_特殊资产阶段.getValue());
        // 查询特殊资产阈值
        List<RskIndividSpecialStage> rskSpecialAstList = rskRedisInitMapper.getRskIndIndivLimit(
                mapPass);

        // 查询剔除资产
        List<RskIndividAssetDel> rskRemoveAstList = rskRedisInitMapper.getRskRemoveAssetData(
                mapPass);
        // 日终跑的批为第二天所用，必须要第二天的日期进行比较
        //Date nextPlateDate = DateUtil.addDay(cond.getPlateDate(), 1);
        Date nextPlateDate =DateUtil.parse(mapPass.get("valDate").toString());
        implIndList.forEach(item -> {
            // 处理特殊阈值
            if (!StringUtil.isEmptyList(rskSpecialList)) {
                Map<String, IIndividLimit> rskSpeMap = individStageDeal(item, nextPlateDate,
                        rskSpecialList);
                item.setIndividSpecLimitRedis(rskSpeMap);
            }
            // add by zhangrui1 自定义条件阈值 start
            if (!CollectionUtils.isEmpty(limitMap)) {
                Map<String, IIndividLimit> custom = limitMap.get(item.getIndId());
                if (!CollectionUtils.isEmpty(custom)
                    && !CollectionUtils.isEmpty(item.getPtlIndividLimit())) {
                    // 将特殊阶段阈值覆盖到自定义条件阈值中
                    custom.putAll(item.getPtlIndividLimit());
                }
                item.setIndividSpecLimitRedis(custom);
            }
            // add by zhangrui1 自定义条件阈值 end
            // 处理联合设置
            Map<String, Set<String>> dealIndUnite = new HashMap<>();
            mapTaskMap.keySet().forEach(key -> {
                Set<String> uniteIdList=mapTaskMap.get(key).stream().filter(v->v.getIndId().equals(item.getIndId()))
                        .map(entry->entry.getUniteId()).collect(Collectors.toSet());
                Set<String> indIdSet = mapTaskMap.get(key).stream().filter(k -> uniteIdList.contains(k.getUniteId())).map(entry -> entry.getIndId()).collect(Collectors.toSet());
             /*   for (RskIndividUnite entry : mapTaskMap.get(key)) {
                    if (entry.getIndId().equals(item.getIndId())) {
                        Set<String> indSet = mapTaskMap.get(key).stream().filter(
                            entity -> entity.getIndId().equals(item.getIndId())).map(
                                entity -> entity.getIndId()).collect(Collectors.toSet());
                        dealIndUnite.put(entry.getPrtBagId(), indSet);
                        break;
                    }
                }*/
                dealIndUnite.put(key, indIdSet);
            });
            item.setIndividUnite(dealIndUnite);

            // 处理特殊资产阈值
            Map<String, Map<String, IIndividLimit>> dealSpecAst = new HashMap<>();
            if (!StringUtil.isEmptyList(rskSpecialAstList)) {
                dealSpecAst = individStageAstDeal(item, nextPlateDate, rskSpecialAstList);
            }
            item.setIndividSpecAstLimit(dealSpecAst);

            // 处理剔除资产
            Map<String, Set<String>> dealIndAstDel = new HashMap<>();
            if (!StringUtil.isEmptyList(rskRemoveAstList)) {
                Map<String, List<RskIndividAssetDel>> rskAstDelMap = rskRemoveAstList.stream().filter(
                        entry -> entry.getIndId().equals(item.getIndId())).collect(
                        Collectors.groupingBy(RskIndividAssetDel::getPrtBagId));
                rskAstDelMap.keySet().forEach(key -> {
                    Set<String> astDelSet = rskAstDelMap.get(key).stream().filter(
                            entity -> DateUtil.compareDate(entity.getStartDate(), nextPlateDate) <= 0
                                    && DateUtil.compareDate(nextPlateDate,
                                    entity.getEndDate()) <= 0).map(
                            entity -> entity.getFinprodId()).collect(
                            Collectors.toSet());
                    if (astDelSet.size() > 0) {
                        dealIndAstDel.put(key, astDelSet);
                    }
                });
            }
            item.setIndividAstDel(dealIndAstDel);

            // 处理指标自身信息
            try {
                PointDetail point = new PointDetail(item.getFoulCond(), item.getApproveCond(),
                        item.getWarnCond(), item.getIndType());
                item.setAbsRedis(point.isAbs());
            }
            catch (Exception e) {
                // 若有异常时默认为无绝对值，不能影响后面计算
                item.setAbsRedis(false);
            }
            // 把每个表达式的参数附加上MIN,MAX
            if(item.getIndType().indexOf(RiskUtil.LIMIT)>=0||item.getIndType().indexOf(RiskUtil.INNER)>=0) {
                item.setExprGradRedis(RiskUtil.getParamWithMode(fixParamList, item.getExpr(),
                        StringUtil.isNotEmpty(
                                item.getFoulCond()) ? item.getFoulCond() : StringUtil.isNotEmpty(
                                item.getApproveCond()) ? item.getApproveCond() : item.getWarnCond()));
            }else{
                //枚举型指标默认以01模式匹配，切记，要不然风控事项配置时(实时)可以匹配不上
                item.setExprGradRedis(item.getExpr().replace(RiskUtil.paramSuff,StringUtil.EMPTY)+GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_实时.getValue()+RiskUtil.paramSuff);
            }
            // 设置List参数列表带粒度的
            item.setIndParamList();

        });
        if (cond.isDel()) {
            indicatorService.deleteInfoFromRedis(RedisKeyEnum.指标信息配置.key(),
                    cond.getIndicatorCodeArray().toArray(
                            new String[cond.getIndicatorCodeArray().size()]));
        }
        else {
            clearRedisProperty(implIndList.toArray(new ImplIndicator[implIndList.size()]));
            indicatorService.saveToRedis(
                    implIndList.toArray(new ImplIndicator[implIndList.size()]));
        }
    }

    /**
     * 处理特殊资产阶段阈值
     *
     * @param item
     * @param sysDate
     * @param rskSpecialAstList
     * @return
     */
    private Map<String, Map<String, IIndividLimit>> individStageAstDeal(ImplIndicator item,
                                                                        Date sysDate,
                                                                        List<RskIndividSpecialStage> rskSpecialAstList) {
        Map<String, Map<String, IIndividLimit>> dealSpecAst = new HashMap<>();
        Map<String, List<RskIndividSpecialStage>> rskSpecAstMap = rskSpecialAstList.stream().filter(
                entry -> entry.getIndId().equals(item.getIndId())).collect(
                Collectors.groupingBy(RskIndividSpecialStage::getPrtBagId));
        rskSpecAstMap.keySet().forEach(key -> {
            List<RskIndividSpecialStage> astDelSet = rskSpecAstMap.get(key).stream().filter(
                    entity -> DateUtil.compareDate(entity.getStartDate(), sysDate) <= 0
                            && DateUtil.compareDate(sysDate, entity.getEndDate()) <= 0).collect(
                    Collectors.toList());

            Map<String, IIndividLimit> rskSpeMap = new HashMap<>();
            for (RskIndividSpecialStage rskStage : astDelSet) {
                IIndividLimit limitEntry = new ImplIndicator();
                BeanUtil.copyPropertiesIgnoreNull(limitEntry, rskStage);
                rskSpeMap.put(rskStage.getFinprodId(), limitEntry);
            }
            if (!CollectionUtils.isEmpty(rskSpeMap)) {
                dealSpecAst.put(key, rskSpeMap);
            }
        });
        return dealSpecAst;
    }

    /**
     * 处理特殊阶段阈值
     *
     * @param item
     * @param sysDate
     * @param rskSpecialAstList
     * @return
     */
    private Map<String, IIndividLimit> individStageDeal(ImplIndicator item, Date sysDate,
                                                        List<RskIndividSpecialStage> rskSpecialAstList) {
        Map<String, IIndividLimit> rskSpeMap = new HashMap<>();
        Map<String, List<RskIndividSpecialStage>> rskSpecAstMap = rskSpecialAstList.stream().filter(
                entry -> entry.getIndId().equals(item.getIndId())).collect(
                Collectors.groupingBy(RskIndividSpecialStage::getPrtBagId));
        rskSpecAstMap.keySet().forEach(key -> {
            List<RskIndividSpecialStage> astDelSet = rskSpecAstMap.get(key).stream().filter(
                    entity -> DateUtil.compareDate(entity.getStartDate(), sysDate) <= 0
                            && DateUtil.compareDate(sysDate, entity.getEndDate()) <= 0).collect(
                    Collectors.toList());
            for (RskIndividSpecialStage rskStage : astDelSet) {
                IIndividLimit limitEntry = new ImplIndicator();
                BeanUtil.copyPropertiesIgnoreNull(limitEntry, rskStage);
                rskSpeMap.put(key, limitEntry);
            }
        });
        return rskSpeMap;
    }

    /**
     * 处理组合关联指标
     *
     * @param cond
     */
    public void dealRiskPtlIndicator(ICond cond) {
        setPlatDate(cond);// 初始化平台日
        Map<String, Object> mapPass = new HashMap<>();
        if (!StringUtil.isEmptyList(cond.getPortfBagIdArray())) {
            // 处理画面端组合包修改
            mapPass.put("portfIdList", dealPortfBagUpd(cond));
        }
        else {
            mapPass.put("portfIdList", cond.getPortfIdArray());
        }
        mapPass.put("portfolioLevel", BussinessDictCodeListRds.组合层次_单组合.getValue());
        mapPass.put("isEnabled", BussinessDictCodeListRds.使用状态_启用.getValue());
        // 日终跑的批为第二天所用，必须要第二天的日期进行比较
        dealValDate(cond,mapPass);
        //mapPass.put("valDate", DateUtil.addDay(DateUtil.format(cond.getPlateDate()), 1));// 有效期条件
        mapPass.put("cond", cond);
        List<ImplPtlIndicator> resultPtlInd = new ArrayList<>();
        // 查询每个组合绑定的组合包，查询出来的结果是组合ID为维度分组，组合包ID以逗号分隔显示出来的.这里面组合包中不需要排除到期组合，在参数SQL中已排除了
        List<ImplPtlIndicator> implPtlBagList = rskRedisInitMapper.getRskPtlRelateBagData(mapPass);
        // 查询每个组合或组合包关联的指标
        //在画面端根据组合找到对应的组合包，然后合并在一起作为条件去查询下面的逻辑
        if(StringUtil.isNotEmpty(cond.getPortfIdArray())||StringUtil.isNotEmpty(cond.getPortfBagIdArray())){
            List<String> portIdEachList= implPtlBagList.stream().map(item->item.getPortfolioId()).collect(Collectors.toList());
            implPtlBagList.forEach(item->{
                if (StringUtil.isNotEmpty(item.getPortfolioBagId())) {
                    Set<String> portBagIdEachList = new ArrayList<>(
                            Arrays.asList(item.getPortfolioBagId().split(GlobalConstant.COMMA))).stream().collect(Collectors.toSet());
                    portIdEachList.addAll(portBagIdEachList);
                }
            });
            Set<String> portIdEachSet=portIdEachList.stream().collect(Collectors.toSet());
            mapPass.put("portfIdList", portIdEachSet);
        }
        //查询组合关联指标任务，排除到期的组合以及不在有效区间内的指标，以组合ID或组合包ID为维度分组，指标ID以逗号分隔显示出来的
        List<ImplPtlIndicator> implPtlIndList = rskRedisInitMapper.getRskPtlIndicatorData(mapPass);

        //查询所有的个性化条件设置信息
        List<PtlPortfolioValuation>  riskPortfValList=new ArrayList<>();
        //---流动性风险改造 start---
        List<FinOpenPlan>  riskPortOpenPlanList=new ArrayList<>();
        //---流动性风险改造 end---
        List<RiskIndividConditionDto>  riskIndividConditionDtoList =  riskTrialCommMapper.getRiskIndividCondition();
        if(!StringUtil.isEmptyList(riskIndividConditionDtoList)){
            riskPortfValList =  riskTrialCommMapper.getPortfValuation(mapPass.get("valDate").toString());
            riskPortOpenPlanList =  riskTrialCommMapper.getPortfOpenPlan(mapPass.get("valDate").toString());
        }
        // add by zhangrui1 自定义条件阈值 start
        List<String> ptlList = new ArrayList<>();
        // add by zhangrui1 自定义条件阈值 end
        for (ImplPtlIndicator implPtlBag : implPtlBagList) {
            ImplPtlIndicator dealImplPtlInd = new ImplPtlIndicator();
            Map<String, Set<String>> ptlIndicatorMap = new HashMap<>();
            // 组合包信息，key为组合包代码，value为组合包名称，这个组合包指当前组合下的组合包
            Map<String, Object> ptlBagSet = new HashMap<>();
            // 组合包集合，组合包代码为内部值
            List<String> ptlAndBagList = new ArrayList<String>();
            // 把组合包加入list中
            if (StringUtil.isNotEmpty(implPtlBag.getPortfolioBagId())) {
                ptlAndBagList = new ArrayList<>(
                        Arrays.asList(implPtlBag.getPortfolioBagId().split(GlobalConstant.COMMA)));
                ptlBagSet = ptlAndBagList.stream().collect(Collectors.toMap(key -> key,
                        key -> new ArrayList<>(Arrays.asList(
                                implPtlBag.getPortfolioBagName().split(GlobalConstant.COMMA))).get(
                                Arrays.asList(implPtlBag.getPortfolioBagId().split(
                                        GlobalConstant.COMMA)).indexOf(key))));
            }
            // 把当前这个组合也加入list中
            ptlAndBagList.add(implPtlBag.getPortfolioId());
            //这个里面存的是某个组合与组合关联组合包的ID集合
            List<String> ptlAndBagNewList = ptlAndBagList;
            // 找到该组合及组合所包含组合包的指标之和，以组合或该组合的组合包代码为key
            List<ImplPtlIndicator> implPtlIndFilterList = implPtlIndList.stream().filter(
                    item -> ptlAndBagNewList.contains(item.getPortfolioId())).collect(
                    Collectors.toList());
            for (ImplPtlIndicator implPtlInd : implPtlIndFilterList) {
                HashSet<String> indSetListFilter= new HashSet<String>(Arrays.asList(implPtlInd.getPtlIndicatorId().split(GlobalConstant.COMMA)));
                HashSet<String> indSetList=new HashSet<>();
                //循环处理当前组合下的关联指标来调用个性化条件设置，以便排除不符合条件的指标
                for(String item:indSetListFilter){
                    //---流动性风险改造 start---
                    if(filterDividCondition(riskIndividConditionDtoList,riskPortfValList,implPtlInd,item,cond.getPlateDate())
                            &&RiskUtil.filterEnableTime(riskIndividConditionDtoList,riskPortOpenPlanList,implPtlInd.getPortfolioId(),item,cond.getPlateDate())){
                        indSetList.add(item);
                    }
                    //---流动性风险改造 end---
                }
                //if(indSetList.size()>0) {
                ptlIndicatorMap.put(implPtlInd.getPortfolioId(), indSetList);
                // }
            }
            if (!CollectionUtils.isEmpty(ptlIndicatorMap)) {
                dealImplPtlInd.setPtlIndicatorRedis(ptlIndicatorMap);
            }
            if (ptlBagSet.size() > 0) {
                dealImplPtlInd.setPortfolioBagIdRedis(ptlBagSet);
            }
            if (!StringUtil.isEmptyList(implPtlIndFilterList)) {
                dealImplPtlInd.setPortfIdRedis(implPtlBag.getPortfolioId());
                dealImplPtlInd.setPtlNameRedis(implPtlBag.getPtlName());
                resultPtlInd.add(dealImplPtlInd);
            }
            // add by zhangrui1 自定义条件阈值 start
            if (!CollectionUtils.isEmpty(dealImplPtlInd.getPtlIndicator())) {
                ptlList.addAll(dealImplPtlInd.getPtlIndicator().keySet().stream().collect(
                    Collectors.toList()));
            }
            // add by zhangrui1 自定义条件阈值 end
        }
        // add by zhangrui1 自定义条件阈值 start
        // 剔除未启用的自定义条件项
        Map<String, Set<String>> ptlIndMap = lineService.getPtlIndMap(ptlList);
        if (!CollectionUtils.isEmpty(resultPtlInd)) {
            resultPtlInd.forEach(dealImplPtlInd -> {
                Map<String, Set<String>> ptlIndicatorMap = dealImplPtlInd.getPtlIndicatorRedis();
                if (CollectionUtils.isEmpty(ptlIndicatorMap)) {
                    return;
                }
                //取出组合自身挂指标的MAP，可能为空
                Set<String> ptlIndicatorSet = StringUtil.isNotEmpty(ptlIndicatorMap.get(dealImplPtlInd.getPtlCode()))?ptlIndicatorMap.get(dealImplPtlInd.getPtlCode()):new HashSet<>();
                ptlIndicatorMap.forEach((key, val) -> {
                    Set<String> indSet = ptlIndMap.get(key);
                    if (!CollectionUtils.isEmpty(indSet) && !CollectionUtils.isEmpty(val)) {
                        if(!StringUtil.equals(key,dealImplPtlInd.getPtlCode())) {
                            indSet.forEach(str -> ptlIndicatorSet.add(str));
                        }

                        indSet.forEach(str -> val.remove(str));
                    }
                });

                //如果存在挂在组合包但不计算组合包的指标，放入组合自身挂的指标中。
                if(StringUtil.isNotEmpty(ptlIndicatorSet)) {
                    ptlIndicatorMap.put(dealImplPtlInd.getPtlCode(),ptlIndicatorSet);
                }
            });
        }
        // add by zhangrui1 自定义条件阈值 end
        if (cond.isDel()) {
            ptlIndicatorService.deleteInfoFromRedis(RedisKeyEnum.组合关联指标配置.key(),
                    cond.getPortfIdArray().toArray(new String[cond.getPortfIdArray().size()]));
        }
        else {
            //当组合没有绑定指标时或绑定的全是事后指标，需要删除原来的那个组合关联指标
            if(StringUtil.isEmptyList(resultPtlInd) && cond.getPortfIdArray() != null){
                ptlIndicatorService.deleteInfoFromRedis(RedisKeyEnum.组合关联指标配置.key(),
                        cond.getPortfIdArray().toArray(new String[cond.getPortfIdArray().size()]));
            }
            else {
                clearRedisProperty(resultPtlInd.toArray(new ImplPtlIndicator[resultPtlInd.size()]));
                ptlIndicatorService.saveToRedis(
                        resultPtlInd.toArray(new ImplPtlIndicator[resultPtlInd.size()]));
            }
        }
    }

    /**
     * 过滤条件设置逻辑
     * @param riskIndividConditionDtoList
     * @param riskPortfValList
     * @param implPtlInd
     * @param paramIndId
     * @return
     */
    private boolean filterDividCondition(List<RiskIndividConditionDto> riskIndividConditionDtoList, List<PtlPortfolioValuation>riskPortfValList,ImplPtlIndicator implPtlInd,String paramIndId,Date supDate){
        List<RiskIndividConditionDto>  riskIndividCondSubList =riskIndividConditionDtoList.stream().filter(item->item.getPrtBagId().equals(implPtlInd.getPortfolioId())&&item.getIndId().equals(paramIndId)).collect(Collectors.toList()) ;
        if(StringUtil.isEmptyList(riskIndividCondSubList)){
            return true;
        }
        List<PtlPortfolioValuation> ptlPortValList=riskPortfValList.stream().filter(item->item.getPortfolioId().equals(implPtlInd.getPortfolioId())).collect(Collectors.toList());
        boolean divZcFlag=true; boolean divDwFlag=true;boolean divJcFlag=false;
        for(RiskIndividConditionDto dto:riskIndividCondSubList){
           /* if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_资产净值控制.getValue())&&!StringUtil.isEmptyList(ptlPortValList)){
                divZcFlag =  RiskUtil.compareNumberVal("NUM",dto.getControlType(), ArithmeticUtil.multiply(dto.getValueStr(),new BigDecimal(10000)),ArithmeticUtil.multiply(dto.getValueEnd(),new BigDecimal(10000)),ptlPortValList.get(0).getTdyAssetNetvalue());
            }
            if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_单位净值控制.getValue())&&!StringUtil.isEmptyList(ptlPortValList)){
                divDwFlag = RiskUtil.compareNumberVal("NUM",dto.getControlType(),dto.getValueStr(),dto.getValueEnd(),ptlPortValList.get(0).getTdyUnitValue());
            }*/
            //建仓
            if(dto.getConditionItem().equals(BussinessDictCodeListRds.条件项_建仓期时间.getValue())){
                //在建仓期期间不计算
                divJcFlag = RiskUtil.compareNumberVal("DATE",BussinessDictCodeListRds.控制类型_范围控制.getValue(),dto.getValueStr(),dto.getValueEnd(),supDate);
            }
        }
        if(!divZcFlag||!divDwFlag||divJcFlag){
            return false;
        }
        return true;
    }
    /**
     * 处理画面修改组合包
     *
     * @param cond
     */
    private List<String> dealPortfBagUpd(ICond cond) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("portfBagIdList", cond.getPortfBagIdArray());
        List<String> portfIdList = rskRedisInitMapper.getAllPortfIdWithPortfBag(mapPass);
        return portfIdList;
    }

    /**
     * 写日志
     *
     * @param message
     * @param sdate
     * @param logVal
     * @param functionId
     */
    public void insertTrialLog(String message, Date sdate, String logVal, String functionId) {
        RskIsupLog rskIsupLog = new RskIsupLog();
        rskIsupLog.setLogId(IDGenerator.generate());
        rskIsupLog.setLogLvl(logVal);
        rskIsupLog.setMessage(message);
        rskIsupLog.setLogTime(sdate);
        rskIsupLog.setFunctionId(functionId);
        rskIsupLogMapper.insertSelective(rskIsupLog);
    }

    /**
     * 清除实体字段,提高接口那边的速度
     *
     * @param tList
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> void clearRedisProperty(T... tList) {
        for (T t : tList) {
            t.setCreateTime(null);
            t.setCreateUser(null);
            t.setUpdateTime(null);
            t.setUpdateUser(null);
            t.setCreateDept(null);
            t.setWorkflowId(null);
            t.setStatus(null);
        }
    }

    @Override
    public void dealAssetIndex(ICond var) {
        Map<String, Object> mapPass = new HashMap<>();
        // 查询条件
        mapPass.put("paramIdList", var.getParamCodeArray());
        List<ImplAsset> assetList = rskRedisInitMapper.selectAssetIndex(mapPass);
        if (!StringUtil.isEmptyList(assetList)) {
            List<IAssetIndex> vList = new ArrayList<>();
            assetList.forEach(asset -> {
                ImplAssetIndex index = new ImplAssetIndex();
                // 资产代码
                index.setAssetCode(asset.getAssetCode());
                // 市场代码
                index.setMarketCode(asset.getMarketCode());
                // 交易场所
                index.setTradePalce(asset.getTradeMarket());
                vList.add(index);
            });
            assetIndexSrc.saveToRedis(vList.toArray(new ImplAssetIndex[vList.size()]));
        }
    }

    private void dealValDate(ICond cond,Map<String,Object> mapPass){
    	/*if(cond.isBatch()){
    		Date nextPlateDate=DateUtil.addDay(cond.getPlateDate(),0);
    		mapPass.put("valDate", DateUtil.format(nextPlateDate));
    	}else{*/
        mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));
        //}
    }

    @Override
    public void dealExchargeInfo(ICond var) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("valDate", var.getPlateDate());
        // 查询条件
        mapPass.put("condList", var.getCondList());
        List<ImplExcharge> exchangeList = rskRedisInitMapper.selectExchargeInfo(mapPass);
        if (!StringUtil.isEmptyList(exchangeList)) {
            exchangeSrc.saveToRedis(exchangeList.toArray(new ImplExcharge[exchangeList.size()]));
        }
    }

    @Override
    public void dealFundValuation(ICond var) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("valDate", var.getPlateDate());
        // 查询条件
        mapPass.put("condList", var.getCondList());
        List<ImplFundValuation> infoList = rskRedisInitMapper.selectFundVal(mapPass);
        if (!StringUtil.isEmptyList(infoList)) {
            fundValuationSrc.saveToRedis(infoList.toArray(new ImplFundValuation[infoList.size()]));
        }
    }

    // add by zhangrui1 start
    /**
     * 处理 资产实时规模信息
     * 
     * @param var 资产实时规模信息
     */
    public void dealProdAssetAmount(ICond var) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("valDate", var.getPlateDate());
        // 查询条件
        mapPass.put("condList", var.getCondList());
        List<ImplProdAssetAmount> infoList = rskRedisInitMapper.selectProdAssetAmount(mapPass);
        if (!StringUtil.isEmptyList(infoList)) {
        	prodAssetAmtSrc.saveToRedis(infoList.toArray(new ImplProdAssetAmount[infoList.size()]));
        }
    }
    // add by zhangrui1 end


    /**
     * 处理 资产现金流相关信息为久期指标用
     *
     * @param cond 资产现金流相关信息为久期指标用
     */
    public void dealAstCashDurationInfo(ICond cond) {
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("finprodIdList", cond.getAssetIdArray());
        mapPass.put("paramDate", DateUtil.format(cond.getPlateDate()));
        try {
            List<MockAssetInfoDto> 资产基本信息列表=baseCommonAssetCommonService.selectAsset(mapPass);
            if (!StringUtil.isEmptyList(资产基本信息列表)) {
                List<ImplAssetDuration> implAssetDurationList=资产基本信息列表.stream().map(item->{
                    ImplAssetDuration implAssetDuration=new ImplAssetDuration();
                    implAssetDuration.setAssetCode(item.getFinprodId());
                    implAssetDuration.setMockAssetInfoDto(item);
                    return implAssetDuration;
                }).collect(Collectors.toList());
                ImplAssetDuration[] implAssetDurations=implAssetDurationList.toArray(new ImplAssetDuration[implAssetDurationList.size()]);
                clearRedisDurationProperty(implAssetDurations);
                prodAssetDurationSrc.saveToRedis(implAssetDurations);
            }
        } catch (Exception e) {
            logger.error("资产久期信息装载出错"+e.getMessage(),e);
        }

    }

    /**
     * 清除实体字段,提高接口那边的速度
     *
     * @param tList
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    private <T extends ImplAssetDuration> void clearRedisDurationProperty(T... tList) {
        for (T t : tList) {
            //主实体
            clearRedisProperty(t);
            if(StringUtil.isNotEmpty(t.getMockAssetInfoDto())) {
                //主实体
                clearRedisProperty(t.getMockAssetInfoDto());
                //金融产品主表
                FinProduct newFinProduct = new FinProduct();
                FinProduct finProduct = t.getMockAssetInfoDto().getFinProduct();
                newFinProduct.setFinprodId(finProduct.getFinprodId());
                newFinProduct.setFinprodAbbr(finProduct.getFinprodAbbr());
                newFinProduct.setMdate(finProduct.getMdate());
                newFinProduct.setIsSus(finProduct.getIsSus());
                newFinProduct.setCouponSpecies(finProduct.getCouponSpecies());
                newFinProduct.setIsRight(finProduct.getIsRight());
                clearRedisProperty(newFinProduct);
                t.getMockAssetInfoDto().setFinProduct(newFinProduct);
                //金融产品附属表
                FinProductAdd newFinProductAdd = new FinProductAdd();
                FinProductAdd finProductAdd = t.getMockAssetInfoDto().getFinProductAdd();
                newFinProductAdd.setNoFixedTerm(finProductAdd.getNoFixedTerm());
                clearRedisProperty(newFinProductAdd);
                t.getMockAssetInfoDto().setFinProductAdd(newFinProductAdd);
                //金融产品分类
                FinProductType newFinProductType = new FinProductType();
                FinProductType finProductType = t.getMockAssetInfoDto().getFinProductType();
                newFinProductType.setType6(finProductType.getType6());
                newFinProductType.setType7(finProductType.getType7());
                newFinProductType.setType8(finProductType.getType8());
                clearRedisProperty(newFinProductType);
                t.getMockAssetInfoDto().setFinProductType(newFinProductType);
                //交易类金融产品
                FinTradeProduct newFinTradeProduct = new FinTradeProduct();
                FinTradeProduct finTradeProduct = t.getMockAssetInfoDto().getFinTradeProduct();
                newFinTradeProduct.setMdate(finTradeProduct.getMdate());
                clearRedisProperty(newFinTradeProduct);
                t.getMockAssetInfoDto().setFinTradeProduct(newFinTradeProduct);
                //交易类产品附属
                t.getMockAssetInfoDto().setFinTradeProductAdd(null);
                //现金流计算规则表Entity列表
                List<FinCashCalcRule> finCashCalcRuleList = t.getMockAssetInfoDto().getFinCashCalcRuleList();
                for (FinCashCalcRule finCashCalcRule : finCashCalcRuleList) {
                    finCashCalcRule.setBranch(null);
                    finCashCalcRule.setBasis(null);
                    finCashCalcRule.setFinprodId(null);
                    finCashCalcRule.setBaseDateType(null);
                    finCashCalcRule.setBaseType(null);
                    finCashCalcRule.setFinprodType(null);
                    finCashCalcRule.setIsInitial(null);
                    finCashCalcRule.setFinprodType2(null);
                    finCashCalcRule.setRemark(null);
                    finCashCalcRule.setCashId(null);
                    clearRedisProperty(finCashCalcRule);
                }
                //现金流支付规则批量查询
                List<FinCashPayRule> finCashPayRuleList = t.getMockAssetInfoDto().getFinCashPayRuleList();
                List<FinCashPayRule> newFinCashPayRuleList = new ArrayList<>();
                for (FinCashPayRule finCashPayRule : finCashPayRuleList) {
                    FinCashPayRule newFinCashPayRule = new FinCashPayRule();
                    newFinCashPayRule.setPayCycle(finCashPayRule.getPayCycle());
                    newFinCashPayRule.setPayCycleVal(finCashPayRule.getPayCycleVal());
                    clearRedisProperty(newFinCashPayRule);
                    newFinCashPayRuleList.add(newFinCashPayRule);
                }
                t.getMockAssetInfoDto().setFinCashPayRuleList(newFinCashPayRuleList);
                //现金流计划表扩展列表
                t.getMockAssetInfoDto().setMockFinCashPlanDtoList(null);
                //行情DTO列表
                List<MockQuotationInfoDto> mockQuotationInfoDtoList = t.getMockAssetInfoDto().getMockQuotationInfoDtoList();
                List<MockQuotationInfoDto> newMockQuotationInfoDtoList = new ArrayList<>();
                if (!StringUtil.isEmptyList(mockQuotationInfoDtoList)) {
                    clearRedisProperty(mockQuotationInfoDtoList.get(0));
                    newMockQuotationInfoDtoList.add(mockQuotationInfoDtoList.get(0));
                    t.getMockAssetInfoDto().setMockQuotationInfoDtoList(newMockQuotationInfoDtoList);
                }
                //行情DTO列表 (公允价值使用  原因：债券计算公允价值的行情的取值逻辑与久期行情逻辑不同)
                t.getMockAssetInfoDto().setMockQuotationInfoDtoListForGYJZ(null);
                //行权计划表Entity
                t.getMockAssetInfoDto().setFinExercisePlanList(null);
                //回售权下一行权计划列表
                List<FinExercisePlan> finExercisePlanList = t.getMockAssetInfoDto().getResaleFinExercisePlanList();
                List<FinExercisePlan> newFinExercisePlanList = new ArrayList<>();
                if (!StringUtil.isEmptyList(finExercisePlanList)) {
                    clearRedisProperty(finExercisePlanList.get(0));
                    newFinExercisePlanList.add(finExercisePlanList.get(0));
                    t.getMockAssetInfoDto().setResaleFinExercisePlanList(newFinExercisePlanList);
                }
                //基金开放计划
                List<FinOpenPlan> fundOpenPlanList = t.getMockAssetInfoDto().getFundOpenPlanList();
                for (FinOpenPlan finCashPayRule : fundOpenPlanList) {
                    clearRedisProperty(finCashPayRule);
                }
            }
        }
    }

}
