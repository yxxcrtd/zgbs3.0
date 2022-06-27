/*
 * 文件名：RiskRefreshRedisServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年9月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.redisinit.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.tams.common.base.entity.RskBusinessRedisConfig;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.component.base.core.ICond;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dao.RskRedisInitMapper;
import com.joyintech.tams.invest.common.redisinit.dto.IParaRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.dto.ImplParam;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.redisinit.service.RiskRefreshRedisService;
import com.joyintech.tams.invest.common.riskinfrds.base.service.RedisBaseService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.*;
import com.joyintech.tams.invest.common.riskinfrds.occupy.service.OccupyRelParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.risktrialrds.dao.SupervisionCalcDao;
import com.joyintech.tams.invest.common.risktrialrds.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 功能模块刷新redis
 *
 * @author 裴家俊
 * @version 1.0 2020年6月18日
 * @see RiskRefreshRedisServiceImpl
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class RiskRefreshRedisServiceImpl extends BaseService implements RiskRefreshRedisService<IParaRds> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private RskRedisInitMapper rskRedisInitMapper;

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisWriter;

    @Autowired(required = false)
    public IPlatformDate platformDate;

    public static final String COMMON_BUSINESS_TYPE="commonBusinessType";
    public static final String PARA_CODE="paraCode";
    public static final String REDIS_MODEL="redisModel";

    /**
     * 预占/实占参数信息服务
     */
    @Autowired
    private OccupyRelParamService ocpSrc;

    /**
     * 指标参数信息Redis读写信息服务
     */
    @Autowired
    private IndicatorParamService paraSrc;

    /**
     * 指标参数值读写服务
     */
    @Autowired
    private IndicatorParamValService valSrc;

    /**
     * 产品基本信息更新Redis服务
     */
    @Autowired
    private ProdInfoRedisService prodInfoSrc;

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    /**
     * 汇率行情
     */
    @Autowired
    private ExchargeInfoService exchargeSrc;

    /**
     * 基金估值行情
     */
    @Autowired
    private FundValuationService fundValSrc;

    /**
     * 节假日信息
     */
    @Autowired
    private CalendarService calendarSrc;

    /**
     * 资产实时规模信息
     */
    @Autowired
    private ProdAssetAmountService prodAssetAmtSrc;

    @Autowired
    private SupervisionCalcDao supervisionCalcDao;

    @Autowired
    private IndicatorParamService indicatorParamService;

    @Autowired
    private IndicatorParamValService paramValService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 获取参数待处理数据
     *
     * @param cond 传递参数
     * @since 1.0
     */
    public void refreshParamRedis(IParaRds cond){
        //共通业务类型必填
        if(!StringUtil.isNotEmpty(cond.getCommonBusinessType())){
            return;
        }

        //查询配置信息并根据配置信息进行相应redis处理
        dealRefreshRedis(cond,entry-> {
            List<RskBusinessRedisConfig> entryList = entry.getValue();
            //刷新指标参数值
            Set<String> paraSet = entryList.stream().map(item -> item.getParaCode()).collect(Collectors.toSet());
            ImplCond iCond = new ImplCond();
            if (!StringUtil.isNotEmpty(iCond.getPlateDate())) {
                Date sysDate = platformDate.getSysDate();
                iCond.setPlateDate(StringUtil.isNotEmpty(sysDate) ? sysDate : new Date());
            }
            iCond.setLoadOffset(false);
            iCond.setIsUpdParVal(true);
            iCond.setDel(false);
            Map<String, Object> mapPass = new HashMap<>();
            mapPass.put("cond", iCond);
            mapPass.put("paramIdList", paraSet);
            mapPass.put("valDate", DateUtil.format(iCond.getPlateDate()));
            //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
            List<ImplParam> implIndParamList = rskRedisInitMapper.getRskIndParamDtlData(mapPass);
            threadPoolTaskExecutor.execute(()->{
                try {
                    Thread.sleep(6000);
                    if (!StringUtil.isEmptyList(implIndParamList)) {
                        dealRiskIndParam(iCond, implIndParamList);
                    }
                    //刷新除指标参数值之外的模块
                    Set<String> redisModelSet = entryList.stream().map(item -> item.getRedisModel()).collect(Collectors.toSet());
                    redisModelSet.forEach(para -> {
                        freshRedisInfo(para, cond.getField());
                    });
                    logger.info("===========功能模块数据刷新指标参数redis结束=============");
                }catch (Exception e){
                  throw new BusinessException(e);
                }
            });
        });

    }
    /**
     * 获取模块待处理数据
     *
     * @param cond 传递参数
     * @since 1.0
     */
    public void refreshModelRedis(IParaRds cond) {
        freshRedisInfo(cond.getRedisModel(),cond.getField());
    }

    /**
     * 查询redis参数配置信息
     * @param cond
     * @param consumer
     */
    private void dealRefreshRedis(IParaRds cond, Consumer<Map.Entry<String, List<RskBusinessRedisConfig>>> consumer){
        Map<String,Object> paraMap=new HashMap<>();
        paraMap.put(COMMON_BUSINESS_TYPE,RiskUtil.convertPortfCode(cond.getCommonBusinessType()));
        paraMap.put(PARA_CODE,cond.getParaCode());
        paraMap.put(REDIS_MODEL,cond.getRedisModel());
        List<RskBusinessRedisConfig> rskBusinessRedisConfigList = rskRedisInitMapper.selectRedisParamConfig(paraMap);
        if(!StringUtil.isEmptyList(rskBusinessRedisConfigList)) {
            Map<String, List<RskBusinessRedisConfig>> rksList = (Map<String, List<RskBusinessRedisConfig>>) rskBusinessRedisConfigList.stream().collect(Collectors.groupingBy(RskBusinessRedisConfig::getCommonBusinessType));
            rksList.entrySet().forEach(consumer);
        }
    }

    private void freshRedisInfo(String key, String filed) {
        ImplCond cond = new ImplCond();
        cond.setDel(false);
        cond.setPlateDate(DateUtil.parse(DateUtil.getSysDate()));
        RedisBaseService<?> baseService = getRedisBaseService(key);
        if (baseService != null && !StringUtil.equals(key, RedisKeyEnum.预占实占信息.key())) {
            baseService.deleteInfoFromRedis(key, !StringUtil.isEmpty(filed) ? filed : null);
        }

        if (StringUtil.equals(key, RedisKeyEnum.产品基本信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                cond.setPortfIdArray(filed);
            }
            redisWriter.dealPortData(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.资产基本信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                cond.setAssetIdArray(filed);
            }
            redisWriter.dealAssetData(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.汇率行情信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                List<String> fList = new ArrayList<>();
                fList.add(filed);
                cond.setCondList(fList);
            }
            redisWriter.dealExchargeInfo(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.基金估值行情信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                List<String> fList = new ArrayList<>();
                fList.add(filed);
                cond.setCondList(fList);
            }
            redisWriter.dealFundValuation(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.日历信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                cond.setCalendarIdArray(filed);
            }
            redisWriter.dealCalendarData(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.资产实时规模信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                List<String> fList = new ArrayList<>();
                fList.add(filed);
                cond.setCondList(fList);
            }
            redisWriter.dealProdAssetAmount(cond);
            return;
        }
        if (StringUtil.equals(key, RedisKeyEnum.资产久期附属信息.key())) {
            if (!StringUtil.isEmpty(filed)) {
                cond.setAssetIdArray(filed);
            }
            redisWriter.dealAstCashDurationInfo(cond);
            return;
        }
    }

    private RedisBaseService<?> getRedisBaseService(String key) {
        if (StringUtil.equals(key, RedisKeyEnum.指标参数配置.key())) {
            return getParaSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.预占实占信息.key())) {
            return getOcpSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.资产基本信息.key())) {
            return getAssetInfoSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.产品基本信息.key())) {
            return getProdInfoSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.汇率行情信息.key())) {
            return getExchargeSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.基金估值行情信息.key())) {
            return getFundValSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.日历信息.key())) {
            return getCalendarSrc();
        }
        if (StringUtil.equals(key, RedisKeyEnum.资产实时规模信息.key())) {
            return getProdAssetAmtSrc();
        }
        return null;
    }

    /**
     * 处理风险指标参数
     *
     * @param cond
     */
    public void dealRiskIndParam(ICond cond,List<ImplParam> implIndParamList) {
        if (!StringUtil.isNotEmpty(cond.getPlateDate())) {
            Date sysDate = platformDate.getSysDate();
            cond.setPlateDate(StringUtil.isNotEmpty(sysDate) ? sysDate : new Date());
        }
        Map<String, Object> mapPass = new HashMap<>();
        mapPass.put("cond", cond);
        mapPass.put("paramIdList", cond.getParamCodeArray());
        mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));
        //mapPass.put("valDate", DateUtil.format(cond.getPlateDate()));// 有效期条件
       // List<ImplParam> implIndParamList = rskRedisInitMapper.getRskIndParamDtlData(mapPass);
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
            //clearRedisProperty(arr);
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
        Map<String, Map<String, IValue<Object>>> totalParamValMap = new HashMap<>();
        implParmMap.keySet().forEach(key -> {
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
                }
        });
        //paramValService.delParamVal(totalParamValMap);
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

    /**
     * 获取 ocpSrc
     *
     * @return ocpSrc.
     */
    public OccupyRelParamService getOcpSrc() {
        return ocpSrc;
    }

    /**
     * 设置 ocpSrc
     *
     * @param ocpSrc ocpSrc
     */
    public void setOcpSrc(OccupyRelParamService ocpSrc) {
        this.ocpSrc = ocpSrc;
    }

    /**
     * 获取 paraSrc
     *
     * @return paraSrc.
     */
    public IndicatorParamService getParaSrc() {
        return paraSrc;
    }

    /**
     * 设置 paraSrc
     *
     * @param paraSrc paraSrc
     */
    public void setParaSrc(IndicatorParamService paraSrc) {
        this.paraSrc = paraSrc;
    }

    /**
     * 获取 valSrc
     *
     * @return valSrc.
     */
    public IndicatorParamValService getValSrc() {
        return valSrc;
    }

    /**
     * 设置 valSrc
     *
     * @param valSrc valSrc
     */
    public void setValSrc(IndicatorParamValService valSrc) {
        this.valSrc = valSrc;
    }

    /**
     * 获取 prodInfoSrc
     *
     * @return prodInfoSrc.
     */
    public ProdInfoRedisService getProdInfoSrc() {
        return prodInfoSrc;
    }

    /**
     * 设置 prodInfoSrc
     *
     * @param prodInfoSrc prodInfoSrc
     */
    public void setProdInfoSrc(ProdInfoRedisService prodInfoSrc) {
        this.prodInfoSrc = prodInfoSrc;
    }

    /**
     * 获取 assetInfoSrc
     *
     * @return assetInfoSrc.
     */
    public AssetInfoRedisService getAssetInfoSrc() {
        return assetInfoSrc;
    }

    /**
     * 设置 assetInfoSrc
     *
     * @param assetInfoSrc assetInfoSrc
     */
    public void setAssetInfoSrc(AssetInfoRedisService assetInfoSrc) {
        this.assetInfoSrc = assetInfoSrc;
    }

    public ExchargeInfoService getExchargeSrc() {
        return exchargeSrc;
    }

    public void setExchargeSrc(ExchargeInfoService exchargeSrc) {
        this.exchargeSrc = exchargeSrc;
    }

    public FundValuationService getFundValSrc() {
        return fundValSrc;
    }

    public void setFundValSrc(FundValuationService fundValSrc) {
        this.fundValSrc = fundValSrc;
    }

    public CalendarService getCalendarSrc() {
        return calendarSrc;
    }

    public void setCalendarSrc(CalendarService calendarSrc) {
        this.calendarSrc = calendarSrc;
    }

    public ProdAssetAmountService getProdAssetAmtSrc() {
        return prodAssetAmtSrc;
    }

    public void setProdAssetAmtSrc(ProdAssetAmountService prodAssetAmtSrc) {
        this.prodAssetAmtSrc = prodAssetAmtSrc;
    }
}
