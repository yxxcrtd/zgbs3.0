/*
 * 文件名：EventBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.service.riskrdsinf.event.rsk007.service.Rsk007Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.redis.common.util.JoyinRedisUtil;
import com.joyintech.redis.core.data.IRedisHash;
import com.joyintech.redis.core.data.RedisHash;
import com.joyintech.redis.msg.entity.MessageBody.Operation;
import com.joyintech.service.riskrdsinf.cache.InterfCacheUtil;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.constant.InfColumns;
import com.joyintech.service.riskrdsinf.core.constant.InfDictCodeList;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.core.service.InfBaseService;
import com.joyintech.service.riskrdsinf.core.util.InfMapValUtil;
import com.joyintech.service.riskrdsinf.event.rsk003.entity.Rsk003InEntity;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.common.base.entity.RskIsupTrial;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.riskinfrds.base.service.IndicatorCalcService;
import com.joyintech.tams.invest.common.riskinfrds.base.service.IndicatorTrendCalcService;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.EventMap;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEventParaRel;
import com.joyintech.tams.invest.common.riskinfrds.event.service.RskEventService;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicatorResult;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorService;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetIndex;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetIndexService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.ProdInfoRedisService;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.IOcpRelParamItem;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.OccupyRelParam;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.OcpRelParamItem;
import com.joyintech.tams.invest.common.riskinfrds.occupy.service.OccupyRelParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.entity.IParam;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamService;
import com.joyintech.tams.invest.common.riskinfrds.param.service.IndicatorParamValService;
import com.joyintech.tams.invest.common.riskinfrds.prod.entity.IPtlIndicator;
import com.joyintech.tams.invest.common.riskinfrds.prod.service.PtlIndicatorService;
import com.joyintech.tams.invest.common.risktrialrds.dao.SupervisionCalcDao;
import com.joyintech.tams.invest.common.risktrialrds.dto.SystemParam;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;

/**
 * 事件接口基础服务类
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月29日
 * @see EventBaseService
 * @since 1.0
 */
public abstract class EventBaseService<V> extends InfBaseService<V> {

    /**
     * 指标关联组合信息读写服务
     */
    @Autowired
    private PtlIndicatorService ptlIndService;

    /**
     * 指标信息Redis读写服务
     */
    @Autowired
    private IndicatorService indService;

    /**
     * 风险前置服务
     */
    @Autowired
    private RskEventService rskEventSrv;

    /**
     * 预占/实占参数信息服务
     */
    @Autowired
    private OccupyRelParamService ocpService;

    /**
     * 指标参数信息Redis读写信息服务
     */
    @Autowired
    private IndicatorParamService indParamSrv;

    /**
     * 指标计算服务
     */
    @Autowired
    private IndicatorCalcService calcService;

    /**
     * 指标参数值读写服务
     */
    @Autowired
    private IndicatorParamValService paramValSrv;

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private ProdInfoRedisService prodInfoSrc;

    /**
     * 日历信息Redis读写服务
     */
    @Autowired
    private CalendarService calendarSrc;

    /**
     * 审批接口
     */
    @Autowired
    private Rsk007Service apprService;

    /**
     * 资产索引
     */
    @Autowired
    private AssetIndexService assetIndexSrc;
    
    @Autowired
    private OccupyDbService busiService;

    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired(required = false)
    public IPlatformDate platformDate;

    @Autowired
    private SupervisionCalcDao supervisionCalcDao;

    // add by zhangrui1 同向变动处理Service start
    @Autowired
    private IndicatorTrendCalcService indTrendCalcSrc;
    // add by zhangrui1 同向变动处理Service end

 	/**
     * KEY:事件日期
     */
    public static final String EVENT_DATE = "eventDate";
    
    /**
     * KEY:组合代码
     */
    public static final String PORTFOLIOCODE = "portfolioCode";

    /**
     * KEY:组合包代码
     */
    public static final String PTLBAGID = "ptlBagId";

    /**
     * KEY:组合(包)名称
     */
    public static final String PORTFOLIONAME = "portfolioName";

    /**
     * KEY:交易对手
     */
    public static final String COUNTERID = "counterId";

    /**
     * KEY:发行人
     */
    public static final String ISSUER = "issuer";

    /**
     * KEY:资产代码
     */
    public static final String ASSETCODE = "assetCode";

    /**
     * KEY:资产名称
     */
    public static final String ASSETNAME = "assetName";

    /**
     * KEY:分组代码
     */
    public static final String GROUPCODE = "groupCode";
    
    /**
     * KEY:市场代码
     */
    public static final String MARKET_CODE = "marketCode";

    /**
     * KEY:交易场所
     */
    public static final String TRADE_PLACE = "tradePlace";

    /**
     * KEY:交易方向
     */
    public static final String PS = "ps";

    /**
     * KEY:资产类型
     */
    public static final String ASSETTYPE = "assetType";

    /**
     * KEY:投资目的
     */
    public static final String INVAIM = "invAim";

    /**
     * KEY:资管委托编号
     */
    public static final String PROD_ID = "prodIdForBk";

    /**
     * KEY:事件代码
     */
    public static final String EVENT_ID = "eventId";

    /**
     * KEY:日历编号
     */
    public static final String CALENDAR_ID = "calendarCode";

    /**
     * KEY:资管委托编号
     */
    public static final String ENTRUST_CODE = "entrustCode";

    /**
     * KEY:事件类型
     */
    public static final String EVENT_TYPE = "eventType";

    /**
     * KEY:金额
     */
    public static final String AMOUNT = "amount";

    /**
     * KEY:数量
     */
    public static final String TRADE_NUM="tradeNum";

    /**
     * KEY:监控时点
     */
    public static final String TIME_POINT = "timePoint";

    /**
     * KEY:试算结果
     */
    public static final String RSKSTATUS = "rskStatus";

    /**
     * KEY:指标代码
     */
    public static final String KRICODE = "kriCode";

    /**
     * KEY:指标名称
     */
    public static final String KRINAME = "kriName";

    /**
     * KEY:指标类型
     */
    public static final String KRITYPE = "kriType";

    /**
     * KEY:指标类型名称
     */
    public static final String KRITYPECN = "kriTypeCn";

    /**
     * KEY:审批线
     */
    public static final String APPROVELINE = "approveLine";

    /**
     * KEY:预警线
     */
    public static final String WARNINGLINE = "warningLine";

    /**
     * KEY:禁止线
     */
    public static final String FORBIDDENLN = "forbiddenLn";

    /**
     * KEY:联合监控状态
     */
    public static final String UNITESTATUS = "uniteStatus";

    /**
     * KEY:指标计算值
     */
    public static final String KRIVALUE = "kriValue";

    /**
     * KEY:原值
     */
    public static final String OLDVALUE = "oldValue";

    /**
     * KEY:趋势
     */
    public static final String KRITREND = "kriTrend";

    /**
     * KEY:指标状态
     */
    public static final String KRISTATUS = "kriStatus";

   /**
     * KEY:计算表达式
     */
    public static final String KRIEXPR = "kriExpr";

    /**
     * KEY:原计算表达式
     */
    public static final String KRIOLDEXPR = "kriOldExpr";
    /**
     * KEY:试算结果列表
     */
    public static final String RSK_LIST = "RSK_LIST";

    /**
     * KEY:自动启动工作流系统
     */
    public static final String INF_STAPP_SYS = "infStappSys";

 	public static final List<String> repoParamList=Arrays.asList("TYCPHGFXJYREVERSE:01","TYCPHGFXJYREVERSE:02","TYCPHGFXJYREVERSE:03"
    ,"TYCPZQFXJYSELL:01","TYCPZQFXJYSELL:02","TYCPZQFXJYSELL:03","BTCPONEZQJYKZSELL:01","BTCPONEZQJYKZSELL:02","BTCPONEZQJYKZSELL:03"
            ,"BTCPFIVEZQJYKZSELL:01","BTCPFIVEZQJYKZSELL:02","BTCPFIVEZQJYKZSELL:03"  ,"TYCPFIVEJYRZQFXSELL:01","TYCPFIVEJYRZQFXSELL:02","TYCPFIVEJYRZQFXSELL:03");
            
    /**
     * KEY:试算禁止时是否更新参数保留计算流水
     */
    public static final String IS_CALC_WHEN_FBD = "isCalcParaWhenForbid";

    /**
     * KEY:交易阶段
     */
    public static final String TRADE_STAGE = "tradeStage";

    /**
     * KEY:上调系数
     */
    public static final String FACTOR = "factor";

    // add by zhangrui1 差分预占 start
    /**
     * 差分预占实占
     */
    public static final String IS_DIFF_OCCUPY = "isDiffOccupy";

    /**
     * 差分预占实占标识
     */
    public static final String DIFF_OCCUPY_FLAG = "diffOccupyFlg";

    /**
     * 逆占
     */
    public static final String IS_REVERSE_OCCUPY = "isReverseOccupy";

    /**
     * 计算普通场景
     */
    public static final int CALC_SCENE_OCP = 0;

    /**
     * 计算特殊场景：差分预占
     */
    public static final int CALC_SCENE_OCP_DIFF = 3;
    // add by zhangrui1 差分预占 start

    // add by zhangrui1 委托完成标识 start
    /**
     * 委托完成标识
     */
    public static final String ETRCOMPLETEFLG = "etrCompleteFlg";
    // add by zhangrui1 委托完成标识 end

    /**
     * 处理类型：预占
     */
    public static final int PRE_OCCUPY = 1;

    /**
     * 处理类型：计算
     */
    public static final int RSK_CALC = 0;

    /**
     * 处理类型：实占
     */
    public static final int REL_OCCUPY = 2;

    /**
     * 状态_占用
     */
    public static final String STATUS_OCCUPY = "0";

    /**
     * 状态_释放
     */
    public static final String STATUS_RELEASE = "1";

    public static final String LOG_INFO_PASS = "组合关联指标信息不存在，试算通过";

    public static final String LOG_INFO_EVENT = "未触发风控事项，试算通过";

 /**
     * 到期日(暂为资产久期所用)
     */
    public static final String MDATE="mdate";
    
 /**
     * 资产细类
     */
    public static final String ASSET_TYPE2="assetType2";

    public static final List<String> dayParamList=Arrays.asList("ZCSYQXDAY:01","ZCSYQXDAY:02","ZCSYQXDAY:03"
            ,"ZCSYCXQXDAY:01","ZCSYCXQXDAY:02","ZCSYCXQXDAY:03");

    public static final String BETWEEN_DATE = "betweenDate";
    /**
     * 达到预警线时是否需要人工确认
     * 
     * @param request 输入报文
     * @return true:是、false:否
     * @see
     * @since 1.0
     */
    public boolean isWarnNdCfm(ClientData request) {
        String isWarnNdCfm = (String)request.getBodyField(InfColumns.COM_ISWARNNDCFM.fieldName());
        return CommonUtil.isType(isWarnNdCfm, BussinessDictCodeListRds.是否_是);
    }

    /**
     * 达到审批线时是否需要人工确认
     * 
     * @param request 输入报文
     * @return true:是、false:否
     * @see
     * @since 1.0
     */
    public boolean isApprNdCfm(ClientData request) {
        String isApprNdCfm = (String)request.getBodyField(InfColumns.COM_ISAPPRNDCFM.fieldName());
        return CommonUtil.isType(isApprNdCfm, BussinessDictCodeListRds.是否_是);
    }

    /**
     * 取得 事件的时点
     * 
     * @param mapData 输入报文体
     * @return 事件的时点
     * @see
     * @since 1.0
     */
    public String getTimePoint(Map<String, Object> mapData) {
        String eventType = (String)mapData.get(EVENT_TYPE);
        String res = getTimePoint(eventType);
        mapData.putIfAbsent(TIME_POINT, res);
        return res;
    }

    /**
     * 根据事件类型判断监控时点
     * 
     * @param eventType 事件类型
     * @return 监控时点
     * @see
     * @since 1.0
     */
    public String getTimePoint(String eventType) {
        if (CommonUtil.isType(eventType, InfDictCodeList.事件类型_投资委托)) {
            return BussinessDictCodeListRds.监督时分_委托监控.getValue();
        }
        else if (CommonUtil.isType(eventType, InfDictCodeList.事件类型_投资交易)) {
            return BussinessDictCodeListRds.监督时分_交易监控.getValue();
        }
        else if (CommonUtil.isType(eventType, InfDictCodeList.事件类型_交易拆分)) {
            return BussinessDictCodeListRds.监督时分_交易监控.getValue();
        }
        return BussinessDictCodeListRds.监督时分_委托监控.getValue();
    }

    /**
     * 判断监督时点，若不满足则指标不计算,判断是否剔除资产，若满足则指标不计算
     * 
     * @param indMap 指标列表
     * @param mapData 接口输入报文
     * @see
     * @since 1.0
     */
    public void checkTpAndIndivAd(Map<String, IIndicator> indMap, Map<String, Object> mapData,
                                  IPtlIndicator ptlInd) {
        // 事件时点
        String timePoint = getTimePoint(mapData);
        // 资产代码
        String assetCode = (String)mapData.get(ASSETCODE);
        // 组合指标关联列表
        Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
        // 组合代码
        String ptlCode = ptlInd.getPtlCode();
        // 组合列表
        Set<String> indList = ptlIndMap.get(ptlCode);
        indMap.entrySet().forEach(en -> {
            // 指标代码
            String key = en.getKey();
            // 指标信息
            IIndicator ind = en.getValue();
            // 当指标监督时点不符合事件时点时，该指标不参与计算
            checkTimePoint(key, ind, indList, timePoint);
            // 判断是否剔除组合资产，若满足则指标不计算
            checkIndividAstDel(ind, indList, ptlCode, assetCode);
            Map<String, Object> ptlBagMap = ptlInd.getPtlBagIdList();
            Set<String> bagList = ptlBagMap == null ? new HashSet<>() : ptlBagMap.keySet();
            if (!CollectionUtils.isEmpty(bagList)) {
                bagList.forEach(str -> {
                    Set<String> tempList = ptlIndMap.get(str);
                    // 当指标监督时点不符合事件时点时，该指标不参与计算
                    checkTimePoint(key, ind, tempList, timePoint);
                    // 判断是否剔除组合包资产，若满足则指标不计算
                    checkIndividAstDel(ind, tempList, str, assetCode);
                });
            }
        });
    }

    public void checkTimePoint(String key, IIndicator ind, Set<String> indList, String timePoint) {
        // 指标监督时点
        String tp = ind.getTimePoint();
        // 当指标监督时点不符合事件时点时，该指标不参与计算
        if (!StringUtil.contains(tp, timePoint) && !CollectionUtils.isEmpty(indList)) {
            indList.remove(key);
        }
    }

    /**
     * 剔除资产判断
     * 
     * @param ind 指标信息
     * @param indList 指标列表
     * @param ptlCode 组合(包)代码
     * @param assetCode 剔除的资产代码
     * @see
     * @since 1.0
     */
    public void checkIndividAstDel(IIndicator ind, Set<String> indList, String ptlCode,
                                   String assetCode) {
        // 取得剔除资产代码信息
        Set<String> tpList = ind.getPtlIndividAstDel(ptlCode);
        // 若目标资产在剔除的列表中，则删除该组合与指标的关系
        if (tpList != null && tpList.contains(assetCode) && !CollectionUtils.isEmpty(indList)) {
            indList.remove(ind.getIndId());
        }
    }

    /**
     * 取得预占/实占信息
     * 
     * @param ocpCode 接口输入报文体
     * @param type 预占/实占类型
     * @return 预占/实占信息
     * @see
     * @since 1.0
     */
    public IOccupyRelParam getOccupy(String ocpCode, int type) {
        // 预占实占参数信息
        IOccupyRelParam ocpRelParam = null;
        if (!StringUtil.isEmpty(ocpCode)) {
            // 取得预占/实占信息
            ocpRelParam = getOccupyRelParam(ocpCode, type);
        }
        return ocpRelParam;
    }

    public List<IRedisHash<String, String, IValue<Object>>> createHashList(Set<String> paraIdList,
                                                                               IPtlIndicator ptlInd,
                                                                               Map<String, IParam> paramMap,
                                                                               Map<String, Object> mapData) {
        List<IRedisHash<String, String, IValue<Object>>> tList = new ArrayList<>();
        String ptlCode = ptlInd.getPtlCode();
        paraIdList.forEach(str -> {
            // 指标参数信息
            IParam para = paramMap.get(str);
            if (para != null && !CommonUtil.isType(para.getParaDisplayType(),
                BussinessDictCodeListRds.指标参数实现类型_类名)) {
                mapData.put(PORTFOLIOCODE, ptlCode);
                tList.add(createHash(str, para, mapData));
                ptlInd.getPtlBagIdList().keySet().forEach(ptlBag -> {
                    mapData.put(PORTFOLIOCODE, ptlBag);
                    tList.add(createHash(str, para, mapData));
                });
            }
        });
        return tList;
    }

    private IRedisHash<String, String, IValue<Object>> createHash(String paramId, IParam para,
                                                                      Map<String, Object> mapData) {
        // 指标参数值Redis识别域
        String field = getFiled(para.getRedisFieldArr(), mapData);
        RedisHash<String, String, IValue<Object>> hash = new RedisHash<>();
        hash.setKey(paramId);
        hash.setField(field);
        return hash;
    }

    /**
     * 判断指标趋势
     * 
     * @param ind 指标列表
     * @see
     * @since 1.0
     */
    public void checkIndTrend(IIndicator ind) {
        //-----zxjjpei---枚举型没有趋势一说-----
        if(ind.getIndType().indexOf(RiskUtil.FORBID)>=0){
            return;
        }
        String cond = ind.getLimitCond();
        Map<String, IIndicatorResult> resMap = ind.getIndicatorResult();
        if (CollectionUtils.isEmpty(resMap)) {
            return;
        }
        for (Entry<String, IIndicatorResult> resEn : resMap.entrySet()) {
            IIndicatorResult res = resEn.getValue();
            IIndividLimit limit = res.getIndividLimit();
            if (limit != null) {
                cond = limit.getLimitCond();
            }
            Object indValNew = res.getIndicatorVal();
            BigDecimal newVal = indValNew == null ? BigDecimal.ZERO : new BigDecimal(
                indValNew.toString());
            Object indValOld = res.getIndicatorOldVal();
            BigDecimal oldVal = indValOld == null ? BigDecimal.ZERO : new BigDecimal(
                indValOld.toString());
            int resCp = newVal.compareTo(oldVal);
            res.setIndicatorTrend(StringUtil.contains(cond, ">") ? -resCp : resCp);
        }
    }

    /**
     * 构建组合与相关的指标参数索引</br>
     * <b>Map<组合代码,Map<指标参数代码,指标参数值Redis识别域>></b>
     * 
     * @param ptlInd 组合关联指标信息
     * @param indMap 指标信息Map <b>Map<指标代码,指标信息></b>
     * @return 组合与相关的指标参数索引
     * @see
     * @since 1.0
     */
    public Map<String, Map<String, String>> getPtlParamMap(IPtlIndicator ptlInd,
                                                           Map<String, IIndicator> indMap) {
        // 返回值
        Map<String, Map<String, String>> ptlParamMap = new HashMap<>();
        // 组合关联指标列表Map<组合代码,List<指标代码>>
        Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
        // 遍历组合关联指标列表
        ptlIndMap.entrySet().forEach(en -> {
            // 组合代码
            String key = en.getKey();
            // List<指标代码>
            Set<String> val = en.getValue();
            // Map<指标参数代码,指标参数值Redis识别域>
            Map<String, String> pam = new HashMap<>();
            // 遍历指标代码列表
            val.forEach(str -> {
                // 指标信息
                IIndicator ind = indMap.get(str);
                if (ind != null) {
                    // List<指标参数代码>
                    List<String> pList = ind.getIndParamList();
                    // 遍历指标参数代码列表
                    pList.forEach(p -> pam.put(p, StringUtil.EMPTY));
                }
            });
            ptlParamMap.put(key, pam);
        });
        return ptlParamMap;
    }

    /**
     * 指标计算,计算结果设置到 <b>指标信息{@link IIndicator}</b> 中
     * 
     * @param ptlInd 组合关联指标信息 {@link IPtlIndicator}
     * @param indMap 指标信息 <b>Map<指标代码, 指标信息></b>
     * @param pValMap 指标参数值 <b>Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>></b>
     * @param isOld 是否原指标值
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public void calcIndicator(IPtlIndicator ptlInd, Map<String, IIndicator> indMap,
                              Map<String, Object> mapData,
                              Map<String, Map<String, Object>> pValMap, boolean isOld) {
        calcService.calcIndicator(ptlInd, indMap, mapData, pValMap, isOld);
    }

    public Map<String, Map<String, Object>> parseParaVal(Map<String, Map<String, String>> ptlParamMap,
                                                         Map<String, Map<String, IValue<Object>>> valMap,Map<String, IParam> paramMap) {
        // 返回值 Map<组合代码, Map<指标参数, 指标参数值>>
        Map<String, Map<String, Object>> pValMap = new HashMap<>();
        // 遍历组合与相关指标参数索引
        ptlParamMap.entrySet().forEach(en -> {
            // 组合代码
            String ptlCode = en.getKey();
            // Map<指标参数代码, 指标参数值Redis识别域>
            Map<String, String> temp = en.getValue();
            // Map<指标参数代码, 指标参数值>
            Map<String, Object> pMap = new HashMap<>();
            temp.entrySet().forEach(item -> {
                // 指标参数代码
                String key = item.getKey();
                // 指标参数值Redis识别域
                String val = item.getValue();
                // 指标参数值
                IValue<Object> bVal = JoyinRedisUtil.getValue(valMap, key, val);
                //----update by 裴家俊 禁投类参数为空时取设置值 20200621 start-------
                IParam iParam=paramMap.get(key);
                pMap.put(key, bVal == null ? StringUtil.isNotEmpty(iParam)?StringUtil.isNotEmpty(iParam.getParamValueNone())?iParam.getParamValueNone():BigDecimal.ZERO:BigDecimal.ZERO : bVal.value());
                //----update by 裴家俊 禁投类参数为空时取设置值 20200621 end---------
            });
            pValMap.put(ptlCode, pMap);
        });
        return pValMap;
    }

    /**
     * 固定值参数加入指标计算的参数列表
     * 
     * @param paraVal 指标计算的参数列表
     * @param map 固定值参数
     * @see
     * @since 1.0
     */
    protected void unionParaVal(Map<String, Map<String, Object>> paraVal,
                                Map<String, Object> map) {
        if (!CollectionUtils.isEmpty(map)) {
            //---------update by 裴家俊 20200713 为了指标久期及偏离度所用，数据库算参数会返回两个，一个原值一个现值 start--------
            Map<String, Object> newMap=new HashMap<>();
            newMap.putAll(map);
            paraVal.entrySet().forEach(en -> {
                Map<String, Object> value = en.getValue();
                newMap.keySet().stream().forEach(key->{
                    if(newMap.get(key) instanceof List){
                        List mapList=(List) newMap.get(key);
                        if(!StringUtil.isEmptyList(mapList)){
                            newMap.put(key,mapList.get(0));
                        }
                    }
                });
                value.putAll(newMap);
            });
            //---------update by 裴家俊 20200713 为了指标久期及偏离度所用，数据库算参数会返回两个，一个原值一个现值 end--------
        }
    }

    /**
     * 固定值参数值
     * wangyang1 二级法人改造
     *
     * @param paramMap 指标参数
     * @param mapData 指标参数
     * @return 固定值参数值
     * @see
     * @since 1.0
     */
    protected Map<String, Object> getFixedParaVal(Map<String, IParam> paramMap,Map<String, Object> mapData) {
        Map<String, Object> map = new HashMap<>();
        paramMap.entrySet().forEach(en -> {
            String key = en.getKey();
            IParam para = en.getValue();
            if (para != null && CommonUtil.isType(para.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_固定值)) {
                String content = para.getContent();
                BigDecimal value = new BigDecimal(content);
                map.put(key, value);
            }
        });
        if (MultilevelDataAuthorityUtil.isEnabled()) {
            paramMap.entrySet().forEach(en -> {
                String key = en.getKey();
                IParam para = en.getValue();
                String instCode = mapData.get("instCode").toString();
                if (!StringUtil.isEmpty(instCode)) {
                    if (para != null && CommonUtil.isType(para.getParaDisplayType(),
                            BussinessDictCodeListRds.指标参数实现类型_固定值) &&
                            instCode.toString().equals(para.getInstitutionCode())) {
                        String content = para.getContent();
                        BigDecimal value = new BigDecimal(content);
                        map.put(key.replace(instCode,BussinessDictCodeListRds.指标参数实现类型_固定值.getValue()), value);
                    }
                }
            });
        }
        return map;
    }

    /**
     * 固定值参数值
     * 
     * @param paramMap 指标参数
     * @return 固定值参数值
     * @see
     * @since 1.0
     */
    protected Map<String, Object> getFixedParaVal(Map<String, IParam> paramMap) {
        Map<String, Object> map = new HashMap<>();
        paramMap.entrySet().forEach(en -> {
            String key = en.getKey();
            IParam para = en.getValue();
            if (para != null && CommonUtil.isType(para.getParaDisplayType(),
                BussinessDictCodeListRds.指标参数实现类型_固定值)) {
                String content = para.getContent();
                BigDecimal value = new BigDecimal(content);
                map.put(key, value);
            }
        });
        return map;
    }

    /**
     * 走库参数值--add by zxjjpei 20200312---
     * @param ptlParamMap 组合与指标参数索引
     * @param paramMap 指标参数
     * @return 走库参数值
     * @see
     * @since 1.0
     */
    protected Map<String, Object> getDataBaseParaVal(List<IEvent> eveList,Map<String, Map<String, String>> ptlParamMap,Map<String, IParam> paramMap,Map<String, Object> mapData) {
        // 查询条件列表
        Set<String> paraValList = new HashSet<>();
        Map<String, Object> map = new HashMap<>();
        paramMap.entrySet().forEach(en -> {
            String key = en.getKey();
            IParam para = en.getValue();
            if (para != null && (CommonUtil.isType(para.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_SQL)||CommonUtil.isType(para.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_类名))&&CommonUtil.isType(para.getParamMathType(),
                    BussinessDictCodeListRds.指标参数计算方式_DB)) {
                paraValList.add(key);
            }
        });
        // 取得对应走库指标参数的值
        Map<String,IParam> dbParamMap=getParam(paraValList);
        // 构造参数条件
        SystemParam sysParam = new SystemParam();
        List<String> ptlBagList=new ArrayList<>();
        if(!CollectionUtils.isEmpty(dbParamMap)){
            sysParam.setValDate(StringUtil.isNotEmpty(mapData.get(EVENT_DATE))?mapData.get(EVENT_DATE).toString():Const.DateFmt.format(platformDate.getSysDate()));
            sysParam.setPortfolioCode(StringUtil.isNotEmpty(mapData.get(PORTFOLIOCODE))?mapData.get(PORTFOLIOCODE).toString():null);
            if(StringUtil.isNotEmpty(mapData.get(PORTFOLIOCODE))){
                ptlBagList=supervisionCalcDao.getPtlBagIdWithPortCode(mapData.get(PORTFOLIOCODE).toString());
            }
            sysParam.setAssetCode(StringUtil.isNotEmpty(mapData.get(ASSETCODE))?mapData.get(ASSETCODE).toString():null);
            sysParam.setAssetType(StringUtil.isNotEmpty(mapData.get(ASSETTYPE))?mapData.get(ASSETTYPE).toString():null);
            sysParam.setTradeCode(StringUtil.isNotEmpty(mapData.get(EVENT_ID))?mapData.get(EVENT_ID).toString():null);
            sysParam.setSupFrequency(BussinessDictCodeListRds.参数粒度_实时.getValue());
            sysParam.setAssetSecType(StringUtil.isNotEmpty(mapData.get(ASSET_TYPE2))?mapData.get(ASSET_TYPE2).toString():null);
            if(StringUtil.isNotEmpty(mapData.get(MDATE))){
                if(mapData.get(MDATE) instanceof Date){
                    sysParam.setmDate(StringUtil.isNotEmpty(mapData.get(MDATE))?DateUtil.format((Date)mapData.get(MDATE), DateUtil.DEFAULT_DATE_FORMAT_EN):null);
                }// 字符串格式的日期转换 add by yufei start
                else if(mapData.get(MDATE) instanceof String){
                    sysParam.setmDate(StringUtil.isNotEmpty(mapData.get(MDATE))? mapData.get(MDATE).toString() : null);
                }// 字符串格式的日期转换 add by yufei end
                else{
                    sysParam.setmDate(StringUtil.isNotEmpty(mapData.get(MDATE))?DateUtil.format(new Date(Long.parseLong(mapData.get(MDATE).toString())), DateUtil.DEFAULT_DATE_FORMAT_EN):null);
                }
            }
            sysParam.setTradeAmt(StringUtil.isNotEmpty(mapData.get(AMOUNT))?new BigDecimal(mapData.get(AMOUNT).toString()):null);
            sysParam.setTradeNum(StringUtil.isNotEmpty(mapData.get(TRADE_NUM))?new BigDecimal(mapData.get(TRADE_NUM).toString()):null);
            sysParam.setEventType(StringUtil.isNotEmpty(mapData.get(EVENT_TYPE))?mapData.get(EVENT_TYPE).toString():null);
            sysParam.setInvAim(StringUtil.isNotEmpty(mapData.get(INVAIM))?mapData.get(INVAIM).toString():null);
        }else{
            return map;
        }
        for(String key:dbParamMap.keySet()){
            //检查参数是否在组合或组合包参数索引里
            if(null==CommonUtil.findValue((Map)ptlParamMap,key)){
                continue;
            }
            String content=dbParamMap.get(key).getContent();
            // SQL文空时，直接返回出去
            if (StringUtil.isEmpty(content)) {
                continue;
            }
            boolean existKey=false;
            if(!CollectionUtils.isEmpty(map)){
               for(String mapKey:map.keySet()){
                    if(StringUtil.equals(mapKey.substring(0,mapKey.indexOf(GlobalConstant.COLON)+1),key.substring(0,key.indexOf(GlobalConstant.COLON)+1))) {
                        existKey=true;
                        map.put(key, map.get(mapKey));
                        break;
                    }
                };
               if(existKey)
                   continue;
            }
            Set<String> ptlFilterBagSet=new HashSet<>();
            //匹配这个参数的组合包，并不是所有组合包需要查询
            ptlBagList.forEach(keyPtl->{
                if(ptlParamMap.containsKey(keyPtl)&&ptlParamMap.get(keyPtl).containsKey(key)){
                    ptlFilterBagSet.add(keyPtl);
                }
            });
            if(!CollectionUtils.isEmpty(ptlFilterBagSet)){
                sysParam.setPtlBagId(RiskUtil.convertPortfCode(String.join(",",ptlFilterBagSet)));
            }
            getLogger().info("待执行的走库脚本:{}", key);
            long startTime = System.currentTimeMillis();
            List<Map<String, Object>> resultSet=new ArrayList<>();
            if (BussinessDictCodeListRds.指标参数实现类型_SQL.getValue().equals(dbParamMap.get(key).getParaDisplayType())) {
                resultSet= supervisionCalcDao.selectOne(sysParam.replaceForString(content));
                resultSet.forEach(entity-> {
                    if (entity.keySet().size() > 1) {
                        Object value = entity.get(RiskUtil.redisVal);
                        if(dbParamMap.get(key).isEnumType()&&StringUtil.isNotEmpty(value)){
                            value=value.toString()+(StringUtil.isNotEmpty(entity.get(RiskUtil.redisKey))?"||"+entity.get(RiskUtil.redisKey).toString():StringUtil.EMPTY);
                        }
                        map.put(key, value);
                    } else {
                        for (Entry<String, Object> entry : entity.entrySet()) {
                            map.put(key, entry.getValue());
                        }
                    }
                });
            }
            if (BussinessDictCodeListRds.指标参数实现类型_类名.getValue().equals(dbParamMap.get(key).getParaDisplayType())) {
                try {
                    List<IEventParaRel> iEventParaRelList=new ArrayList<>();
                    eveList.stream().forEach(item->iEventParaRelList.addAll(item.getEventParaRel()));
                    List<IEventParaRel> iEventParaList = iEventParaRelList.stream().filter(item->StringUtil.equals(item.getParaCode(),key)).collect(Collectors.toList());
                    PluginClass pluginClass = RiskUtil.getBean(content.trim());
                    SystemParam sysClsParam = new SystemParam();
                    BeanUtil.copyPropertiesIgnoreNull(sysClsParam,sysParam);
                    if(!StringUtil.isEmptyList(iEventParaList)&&StringUtil.equals(BussinessDictCodeListRds.操作符_减.getValue(),iEventParaList.get(0).getRelType())){
                        sysClsParam.setTradeAmt(sysClsParam.getTradeAmt().negate());
                        sysClsParam.setTradeNum(sysClsParam.getTradeNum().negate());
                    }
                    pluginClass.setSysParameter(null,sysClsParam);
                    Object paramValue = pluginClass.getParameterValue();// 获取每个参数实际值
                    if(StringUtil.isNotEmpty(paramValue)){
                        map.put(key, paramValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis();
            getLogger().info("获取单个参数消耗的时间："+ (endTime - startTime) );
        };
        return map;
    }


    /**
     * 枚举型参数值
     *
     * @param paramMap 指标参数
     * @return 枚举型参数值
     * @see
     * @since 1.0
     */
    protected Map<String, Object> getForbidParaVal(Map<String, IParam> paramMap,Map<String, Object> mapData) {
        // 查询条件列表
        Map<String, Map<String, IValue<Object>>> paraValList = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        paramMap.entrySet().forEach(en -> {
            String key = en.getKey();
            IParam para = en.getValue();
            if (para != null && CommonUtil.isType(para.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_SQL)&&para.isEnumType()) {
                String field = getFiled(para.getRedisFieldArr(), mapData);
                // 指标参数值Redis识别域
                Map<String, IValue<Object>> mapRedis = paraValList.getOrDefault(en.getKey(),
                        new HashMap<>());
                mapRedis.put(field, null);
                paraValList.put(key, mapRedis);
            }
        });
        // 取得对应枚举型指标参数的值
        Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(paraValList);
        paraValMap.entrySet().forEach(en -> {
            Map<String, IValue<Object>> mapVal=en.getValue();
                mapVal.entrySet().forEach(enx -> {
                    // 参数值
                    IValue<Object> paraVal = enx.getValue();
                    if(StringUtil.isNotEmpty(paraVal)) {
                        map.put(en.getKey(), paraVal.value());
                    }
                });
        });
        return map;
    }

    /**
     * 取得 风险事件
     * 
     * @param mapData 委托/交易信息
     * @param calendar 日历信息
     * @return 风险事件列表
     * @see
     * @since 1.0
     */
    public List<IEvent> getEvent(Map<String, Object> mapData, ICalendar calendar) {
        // modify by zhangrui1 公式配置 start
        // return rskEventSrv.matchEvent(mapData, calendar);
        List<IEvent> eveList = rskEventSrv.matchEvent(mapData, calendar);
        if (!CollectionUtils.isEmpty(eveList)) {
            // 取得所有风控事项关联参数配置中的公式参数
            Map<String, Map<String, IValue<Object>>> valMap = new HashMap<>();
            eveList.forEach(event -> valMap.putAll(event.getParaValMap(rdsField -> {
                String[] fieldArr = StringUtil.split(rdsField, GlobalConstant.COMMA);
                return getFiled(fieldArr, mapData);
            })));
            // 取得公式参数值
            Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(
                valMap);
            // 如果公式参数值有则设置公式参数值
            if (!CollectionUtils.isEmpty(paraValMap)) {
                Map<String, BigDecimal> formulaParaVal = new HashMap<>();
                paraValMap.forEach((key, temp) -> {
                    if (!CollectionUtils.isEmpty(temp)) {
                        temp.forEach((field, val) -> {
                            if (val != null) {
                                formulaParaVal.put(key, (BigDecimal)val.value());
                            }else{
                                if (dayParamList.contains(key)){
                                    formulaParaVal.put(key, StringUtil.isNotEmpty(mapData.get(BETWEEN_DATE))?new BigDecimal(mapData.get(BETWEEN_DATE).toString()):null);
                                }
                            }
                        });
                    }
                });
                eveList.forEach(event -> event.setFormulaParaVal(formulaParaVal));
            }
        }
        return eveList;
        // modify by zhangrui1 公式配置 end
    }

    /**
     * 取得 组合关联指标信息
     * 
     * @param mapData 委托/交易信息
     * @return 组合关联指标信息
     * @see
     * @since 1.0
     */
    public IPtlIndicator getPtlIndicator(Map<String, Object> mapData) {
        List<String> fieldList = new ArrayList<>();
        Map<String,Object> ptlMap = new HashMap<>();
        String ptlCode = (String)mapData.get(PORTFOLIOCODE);
        fieldList.add(ptlCode);
        // 交易对手对应的组合关联指标情况
        String issuer = (String)mapData.get(COUNTERID);
        if (!StringUtil.isEmpty(issuer)) {
            fieldList.add(issuer);
        }
        Map<String, IPtlIndicator> resultMap = ptlIndService.getMapFromRedis(
            RedisKeyEnum.组合关联指标配置.key(), fieldList);
        IPtlIndicator result = resultMap.get(ptlCode);
        IPtlIndicator issPtl = resultMap.get(issuer);
        // 有相同组合包则该组合包指标不进行试算，删除组合包
        if (issPtl != null && result != null && issPtl.getPtlBagIdList() != null
            && result.getPtlBagIdList() != null && result.getPtlIndicator() != null) {
            Map<String, Object> resMap = result.getPtlBagIdList();
            issPtl.getPtlBagIdList().entrySet().forEach(en -> {
                String key = en.getKey();
                resMap.remove(key);
                result.getPtlIndicator().remove(key);
            });
        }
        if (result != null) {
            ptlMap.put(result.getPtlCode(), result.getPtlName());
            if (result.getPtlBagIdList() != null) {
                ptlMap.putAll(result.getPtlBagIdList());
            }
        }
        setServiceInfo("PTL_NAME", ptlMap);
        return result;
    }

    /**
     * 取得 组合关联的所有指标代码
     * 
     * @param ptlIndicator 组合关联指标信息
     * @return 组合关联的所有指标代码
     * @see
     * @since 1.0
     */
    public Set<String> getIndIdList(IPtlIndicator ptlIndicator) {
        if (ptlIndicator == null) {
            return new HashSet<>();
        }
        Set<String> indIdList = new HashSet<>();
        Map<String, Set<String>> ptlIndMap = ptlIndicator.getPtlIndicator();
        if (CollectionUtils.isEmpty(ptlIndMap)) {
            return indIdList;
        }
        ptlIndMap.values().forEach(item -> {
            if (!CollectionUtils.isEmpty(item)) {
                indIdList.addAll(item);
            }
        });
        return indIdList;
    }

    /**
     * 取得 指标信息
     * 
     * @param indIdList 指标代码列表
     * @return 指标信息
     * @see
     * @since 1.0
     */
    public Map<String, IIndicator> getIndicator(Set<String> indIdList) {
        return indService.getIndicatorMap(indIdList);
    }

    /**
     * 从风险事件列表中取出相关的参数列表
     * 
     * @param eveList 风险事件列表
     * @return 参数列表
     * @see
     * @since 1.0
     */
    public Set<String> getParamIdList(List<IEvent> eveList) {
        // 指标中的所有参数代码
        Set<String> paraIdList = new HashSet<>();
        // 风险事件关联的所有指标参数代码
        forEachEvent(eveList, epr -> paraIdList.add(epr.getParaCode()));
        return paraIdList;
    }

    /**
     * 风险事件相关参数与指标相关参数糅合去重
     * 
     * @param indMap 指标列表
     * @param paraIdList 风险事件相关参数列表
     * @param ptlIndMap 组合关联指标
     * @see
     * @since 1.0
     */
    public void unionParamId(Map<String, IIndicator> indMap, Set<String> paraIdList,
                             Map<String, Set<String>> ptlIndMap) {
        Set<String> indCodeSet = new HashSet<>();
        Set<String> temp = new HashSet<>();
        for (IIndicator ind : indMap.values()) {
            if(StringUtil.isNotEmpty(ind)){
                // 指标参数列表
                List<String> indPList = ind.getIndParamList();
                if (StringUtil.isEmptyList(indPList)) {
                    continue;
                }
                // 判断指标参数是否受到影响
                if (CollectionUtils.containsAny(paraIdList, indPList)) {
                    temp.addAll(indPList);
                }
                else {
                    // 删除未受影响的指标
                    indCodeSet.add(ind.getField());
                }
            }
        }
        if (!CollectionUtils.isEmpty(temp)) {
            paraIdList.addAll(temp);
        }
        if (!CollectionUtils.isEmpty(indCodeSet) && !CollectionUtils.isEmpty(ptlIndMap)) {
            indCodeSet.forEach(code -> ptlIndMap.entrySet().forEach(set -> {
                Set<String> value = set.getValue();
                value.remove(code);
            }));
        }
    }

    /**
     * 取得 指标参数信息
     * 
     * @param paraIdList 指标参数代码
     * @return 指标参数信息 Map<指标参数代码,指标参数信息>
     * @see
     * @since 1.0
     */
    public Map<String, IParam> getParam(Set<String> paraIdList) {
        return indParamSrv.getParamList(paraIdList);
    }

    /**
     * 取得 预占/实占参数信息
     * 
     * @param ocpCode 预占/实占代码
     * @param type 预占/实占类型
     * @return 预占/实占参数信息
     * @see
     * @since 1.0
     */
    public IOccupyRelParam getOccupyRelParam(String ocpCode, int type) {
        return ocpService.getOccupyRelParamMap(ocpCode, type);
    }

    /**
     * 保存 预占/实占参数信息
     * 
     * @param ocpRelParam 预占/实占代码
     * @return 预占/实占参数信息
     * @see
     * @since 1.0
     */
    public void setOccupyRelParam(IOccupyRelParam ocpRelParam) {
        ocpService.saveToRedis(ocpRelParam);
    }

    /**
     * 取得 日历信息
     * 
     * @param calendarId 日历编号
     * @return 日历信息
     * @see
     * @since 1.0
     */
    public ICalendar getCalendar(String calendarId) {
        return calendarSrc.getCalendar(calendarId);
    }

    /**
     * 遍历风险事件相关的指标参数
     * 
     * @param eveList 风险事件 <b>List<{@link IEvent}></b>
     * @param var1 处理逻辑
     * @see
     * @since 1.0
     */
    private void forEachEvent(List<IEvent> eveList, Consumer<IEventParaRel> var1) {
        eveList.forEach(eve -> {
            List<IEventParaRel> eprList = eve.getEventParaRel();
            if (!StringUtil.isEmptyList(eprList)) {
                eprList.forEach(var1);
            }
        });
    }

    /**
     * 构建指标参数查询条件
     * 
     * @param paramMap 指标参数信息 <b>Map<指标参数代码, 指标参数信息></b>
     * @param mapData 委托/交易信息
     * @param ptlParamMap 组合相关的指标参数 <b>Map<组合代码, Map<指标参数代码, 指标参数值Redis识别域>></b>
     * @return 指标参数查询条件
     * @see
     * @since 1.0
     */
    public Map<String, Map<String, IValue<Object>>> createCondition(Map<String, IParam> paramMap,
                                                                        Map<String, Object> mapData,
                                                                        Map<String, Map<String, String>> ptlParamMap) {
        //---add by zxjjpei 20200312 获取走库计算的参数---
        // 查询条件列表
        Map<String, Map<String, IValue<Object>>> paraValList = new HashMap<>();
        // 指标参数信息
        paramMap.entrySet().forEach(pEn -> {
            IParam para = paramMap.get(pEn.getKey());
            String pCode = pEn.getKey();
            //---update by zxjjpei 20200312 获取走库计算的参数---
            if (para != null
                && !CommonUtil.isType(para.getParaDisplayType(), BussinessDictCodeListRds.指标参数实现类型_类名)
                && !CommonUtil.isType(para.getParaDisplayType(), BussinessDictCodeListRds.指标参数实现类型_固定值)
                && !para.isEnumType()&&CommonUtil.isType(para.getParamMathType(), BussinessDictCodeListRds.指标参数计算方式_REDIS)) {
                String field = getFiled(para.getRedisFieldArr(), mapData);
                // 指标参数值Redis识别域
                Map<String, IValue<Object>> map = paraValList.getOrDefault(pEn.getKey(),
                    new HashMap<>());
                map.put(field, null);
                paraValList.put(pCode, map);
            }
        });
        // 遍历组合相关的指标参数
        Map<String, Object> mapDateBk=new HashMap<>();
        mapDateBk.putAll(mapData);
        ptlParamMap.entrySet().forEach(en -> {
            // 组合代码
            String ptlCode = en.getKey();
            // Map<指标参数代码, 指标参数值Redis识别域>
            Map<String, String> paramList = en.getValue();
            mapDateBk.put(PORTFOLIOCODE, ptlCode);
            paramList.entrySet().forEach(pEn -> {
                // 指标参数代码
                String pCode = pEn.getKey();
                // 指标参数信息
                IParam para = paramMap.get(pCode);
                //---update by zxjjpei 20200312 获取走库计算的参数---
                if (para != null
                    && !CommonUtil.isType(para.getParaDisplayType(),
                        BussinessDictCodeListRds.指标参数实现类型_类名)
                    && !CommonUtil.isType(para.getParaDisplayType(),
                        BussinessDictCodeListRds.指标参数实现类型_固定值)
                    && !para.isEnumType()&&CommonUtil.isType(para.getParamMathType(), BussinessDictCodeListRds.指标参数计算方式_REDIS)) {
                    // 指标参数值Redis识别域
                    String field = getFiled(para.getRedisFieldArr(), mapDateBk);
                    pEn.setValue(field);
                    Map<String, IValue<Object>> map = paraValList.getOrDefault(pCode,
                        new HashMap<>());
                    map.put(field, null);
                    paraValList.put(pCode, map);
                }
            });
        });
        return paraValList;
    }

    public boolean checkSqlParamType(Map<String, IParam> paramMap) {
        boolean retFlag=false;
        //---add by zxjjpei 20200312 获取走库计算的参数---
        // 指标参数信息
        for(Map.Entry<String, IParam> pEn:paramMap.entrySet()){
            IParam para = paramMap.get(pEn.getKey());
            String pCode = pEn.getKey();
            //---update by zxjjpei 20200312 获取走库计算的参数---
            if (para != null
                    && !CommonUtil.isType(para.getParaDisplayType(), BussinessDictCodeListRds.指标参数实现类型_类名)
                    && !CommonUtil.isType(para.getParaDisplayType(), BussinessDictCodeListRds.指标参数实现类型_固定值)
                    && !para.isEnumType()&&CommonUtil.isType(para.getParamMathType(), BussinessDictCodeListRds.指标参数计算方式_REDIS)) {
                retFlag=true;
                break;
            }
        }
        return retFlag;
    }

    // add by zhangrui1 差分预占 start
    /**
     * 根据风险事件计算指标参数
     * 
     * @param eveList 风险事件 List<{@link IEvent}>
     * @param paraValMap 指标参数值 <b>Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>></b>
     * @param ocpRelParamObj 预占实占信息
     * @param details 计算流水明细列表
     * @see
     * @since 1.0
     */
    protected Map<String, Map<String, IValue<Object>>> calcParamVal(List<IEvent> eveList,
                                                                    Map<String, Map<String, IValue<Object>>> paraValMap,
                                                                    IOccupyRelParam ocpRelParamObj,
                                                                    List<RskIndicatorParaCalcdetail> details) {
        return calcParamVal(eveList, paraValMap, ocpRelParamObj, CALC_SCENE_OCP, null, details);
    }
    // add by zhangrui1 差分预占 end

    /**
     * 根据风险事件计算指标参数
     * 
     * @param eveList 风险事件 List<{@link IEvent}>
     * @param paraValMap 指标参数值 <b>Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>></b>
     * @param calcScene 计算场景
     * @param mapData 报文信息
     * @param ocpRelParamObj 预占实占信息
     * @param details 计算流水明细列表
     * @see
     * @since 1.0
     */
    protected Map<String, Map<String, IValue<Object>>> calcParamVal(List<IEvent> eveList,
                                                                    Map<String, Map<String, IValue<Object>>> paraValMap,
                                                                    IOccupyRelParam ocpRelParamObj,
                                                                    // add by zhangrui1 start
                                                                    int calcScene,
                                                                    Map<String, Object> mapData,
                                                                    // add by zhangrui1 end
                                                                    List<RskIndicatorParaCalcdetail> details) {
        // add by zhangrui1 start
        boolean isDiffOcp = null != mapData && isDiffOcp(mapData);
        // add by zhangrui1 end
        Map<String, IOcpRelParamItem> ocpPmItemMap = ocpRelParamObj == null || ocpRelParamObj.getItems() == null ? new HashMap<>() : ocpRelParamObj.getItems();
        // 预占过的指标参数列表
        Set<String> indSet = CollectionUtils.isEmpty(
            ocpPmItemMap) ? new HashSet<>() : ocpPmItemMap.keySet();
        // 返回值 Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>>
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        newValMap.putAll(paraValMap);
        forEachEvent(eveList, epr -> {
            boolean isPreOcp = false;
            // 参数代码
            String paraCode = epr.getParaCode();
            // 关系类型
            String relType = epr.getRelType();
            // 关系预算的对象值
            BigDecimal relVal = epr.getRelValue();
            // 占比金额
            if (StringUtil.equals(epr.getRelItem(), MIXAMOUNTKEY)
                || (epr.isFormula() && StringUtil.equals(epr.formulaRelItem(), MIXAMOUNTKEY))) {
                relVal = getMixAmount();
            }
            // 当参数代码在预占过的指标参数列表中
            if (indSet.contains(paraCode) && !isDiffOcp) {
                IOcpRelParamItem item = ocpPmItemMap.get(paraCode);
                BigDecimal tempVal = getAllOcpVal(ocpRelParamObj.getField(), paraCode).add(relVal == null ? BigDecimal.ZERO : relVal);
                // 实占大于预占时,变化值=实占-预占
                if (CommonUtil.compareV(tempVal, item.getOccupyVal(), BussinessDictCodeListRds.运算符_大于.getValue())) {
                    relVal = tempVal.subtract(item.getOccupyVal());
                }
                else {
                    return;
                }
                isPreOcp = true;
            }
            BigDecimal val = relVal;
            // add by zhangrui1 计算后参数值小于0时的处理 start
            IValue<BigDecimal> zeroDiff = new Value<>(
                str -> StringUtil.isNotEmpty(str) ? new BigDecimal(str) : BigDecimal.ZERO);
            zeroDiff.set(val);
            // add by zhangrui1 计算后参数值小于0时的处理 end
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<Object>> valMap = paraValMap.get(paraCode);
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<Object>> temp = new HashMap<>();
            HashSet<String> hs = new HashSet<>();
            valMap.entrySet().forEach(en -> {
                String key = en.getKey();
                // 参数值
                IValue<Object> paraVal = en.getValue();
                //----zxjjpei 更改类型------------------
                //若是禁投类或日期类处理
                if(StringUtil.isNotEmpty(paraVal)&&paraVal.value() instanceof String){
                    String parStr=paraVal.value().toString();
                    IValue<Object> newVal = new Value<>(
                            str -> StringUtil.isEmpty(str) ? StringUtil.EMPTY : str.toString());
                    newVal.set(parStr);
                    temp.put(key, newVal);
                    return;
                }
                BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal)paraVal.value();
                IValue<Object> newVal = new Value<>(
                    str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                // 指标参数值计算
                // modify by zhangrui1 start
                //newVal.set(CommonUtil.math(par, val, relType));
                //内置阈值型的参数特殊处理，可能出现小于0的情况
                if (repoParamList.contains(paraCode)) {
                    newVal.set(CommonUtil.math(par, val, relType));
                }
                else {
//                    newVal.set(BigDecimal.ZERO.max(CommonUtil.math(par, val, relType)));
                    // add by zhangrui1 差分预占 start
                    BigDecimal diffVal = val;
                    if (isDiffOcp && ocpPmItemMap.containsKey(paraCode)) {
                        OcpRelParamItem tempItem = (OcpRelParamItem)ocpPmItemMap.get(paraCode);
                        diffVal = tempItem.putDiffOcp(key, (String)mapData.get(DIFF_OCCUPY_FLAG),
                            val, StringUtil.equals(mapData.get(IS_REVERSE_OCCUPY),
                                BussinessDictCodeListRds.是否_是.getValue()));
                    }
                    // add by zhangrui1 差分预占 end
                    // add by zhangrui1 计算后参数值小于0时的处理 start
                    BigDecimal resVal = CommonUtil.math(par, epr.doFormula(diffVal), relType);
                    // 预占保持原值，实占时若<0则设置参数值为0
                    newVal.set(mapData != null && StringUtil.equals(mapData.get(EVENT_TYPE),
                        String.valueOf(PRE_OCCUPY)) ? resVal : BigDecimal.ZERO.max(resVal));
                    zeroDiff.set(
                        ArithmeticUtil.doAdd(diffVal,
                            mapData != null && StringUtil.equals(mapData.get(EVENT_TYPE),
                                String.valueOf(PRE_OCCUPY)) ? BigDecimal.ZERO : BigDecimal.ZERO.min(resVal)));
                    // add by zhangrui1 计算后参数值小于0时的处理 end
                }
                // modify by zhangrui1 end
                temp.put(key, newVal);
                hs.add(key);
                // 计算明细
                //----zxjjpei 更改类型------------------
                details.add(createDetail(paraCode, key, par, (BigDecimal)newVal.value(), CommonUtil.math(BigDecimal.ZERO, epr.doFormula(zeroDiff.value()), relType)));
            });
            newValMap.put(paraCode, temp);
            //----add by zxjjpei 20200401 检查是否需要更新redis start----
            if(!checkUpRdsParam(paraCode)){
                return;
            }
            //----add by zxjjpei 20200401 检查是否需要更新redis end----
            // add by zhangrui1 差分预占 start
            // 差分预占已经有初始预占额度时
            if (isDiffOcp && ocpPmItemMap.containsKey(paraCode)
                && null != ocpPmItemMap.get(paraCode)) {
                ocpPmItemMap.get(paraCode).getRedisFields().addAll(hs);
                return;
            }
            // add by zhangrui1 差分预占 end
            OcpRelParamItem ocpPmItem = new OcpRelParamItem();
            // 预占/实占的对象
            ocpPmItem.setOccupyItem(epr.isFormula() ? epr.formulaRelItem() : epr.getRelItem());
            // 预占/实占操作符号
            ocpPmItem.setOccupyOper(relType);
            // 预占/实占金额
            ocpPmItem.setOccupyVal(isPreOcp ? ocpPmItemMap.get(paraCode).getOccupyVal() : zeroDiff.value());
            // 指标参数编号
            ocpPmItem.setParamCode(paraCode);
            // 参数值Redis存储Field
            ocpPmItem.setRedisFields(hs);
            ocpPmItem.setOffsetVal(BigDecimal.ZERO);
            // 预占额度系数
            ocpPmItem.setFormulaVal(epr.getFormulaParaVal());
            // 预占额度系数操作符
            ocpPmItem.setFormulaOpr(epr.formulaOprType());
            ocpPmItemMap.put(paraCode, ocpPmItem);
        });
        // 记录未变更的参数值
        paraValMap.entrySet().forEach(en->{
        	String paraCode = en.getKey();
            // 参数值
            Map<String,IValue<Object>> paraMap = en.getValue();
        	if (!containsPara(details,paraCode) && paraMap != null) {
        		paraMap.entrySet().forEach(pMap -> {
        			String key = pMap.getKey();
                    // 参数值
                    IValue<Object> paraVal = pMap.getValue();
                    //----zxjjpei 更改类型------------------
                    if(StringUtil.isNotEmpty(paraVal)&&paraVal.value() instanceof String){
                        return;
                    }
                    BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal)paraVal.value();
                    // 计算明细
                    details.add(createDetail(paraCode, key, par, par, BigDecimal.ZERO));
            	});
        	}
        });
        return newValMap;
    }

    /**
     * 根据风险事件计算指标参数(针对固定值与走库等) ----zxjjpei 添加新方法------------------
     *
     * @param eveList 风险事件 List<{@link IEvent}>
     * @param paraFixMap 指标参数值 <b>Map<指标参数代码, 指标参数值></b>
     * @see
     * @since 1.0
     */
    protected void calcFixParamVal(List<IEvent> eveList, Map<String, Object> paraFixMap) {
        // 返回值 Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>>
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        forEachEvent(eveList, epr -> {
            // 参数代码
            String paraCode = epr.getParaCode();
            // 关系类型
            String relType = epr.getRelType();
            // 关系预算的对象值
            BigDecimal val = epr.getRelValue();
            // Map<指标参数值Redis识别域, 指标参数值>
            Object valMap = paraFixMap.get(paraCode);
            if (!StringUtil.isNotEmpty(valMap)) {
                return;
            }
            //----zxjjpei 更改类型 20200410 start------------------
            if(valMap instanceof String) {
                try{
                    // 指标参数值计算
                    BigDecimal par = new BigDecimal(valMap.toString());
                    paraFixMap.put(paraCode, CommonUtil.math(par, val, relType));
                }
                catch(NumberFormatException e)
                {
                    paraFixMap.put(paraCode,valMap.toString());
                }
                /*// 指标参数值计算
                paraFixMap.put(paraCode,valMap.toString());*/
            }
            //----add by 裴家俊 为了计算久期，走数据库计算出原值与新值两个,这里返回新值 20200713 start----
            else  if(valMap instanceof List) {
                // 指标参数值计算
                List<BigDecimal> valList = (List<BigDecimal>)valMap;
                if(!StringUtil.isEmptyList(valList)){
                    if(valList.size()>1){
                        //新值
                        paraFixMap.put(paraCode,CommonUtil.math(valList.get(1), val, relType));
                    }else{
                        paraFixMap.put(paraCode,CommonUtil.math(valList.get(0), val, relType));
                    }
                }else{
                    paraFixMap.put(paraCode,CommonUtil.math(BigDecimal.ZERO, val, relType));
                }
            }
            //----add by 裴家俊 为了计算久期，走数据库计算出原值与新值两个 20200713 end----
            else {
                BigDecimal par = (BigDecimal) valMap;
                // 指标参数值计算
                paraFixMap.put(paraCode, CommonUtil.math(par, val, relType));
            }
            //----zxjjpei 更改类型 20200410 end------------------
        });
    }

    //------------------------add by zxjjpei 20200401 start-------------------
    /**
     * 过滤掉SQL模式的且需要结合画面计算但不需要更新redis的参数
     * @param newValMap
     */
    protected Map<String, Map<String, IValue<Object>>> filterNoUpRdsParam(Map<String, Map<String, IValue<Object>>> newValMap){
        if(StringUtil.isNotEmpty(newValMap)&&newValMap.keySet().size()>0) {
            Map<String, IParam> newValParamMap = getParam(newValMap.keySet());
            newValParamMap.keySet().forEach(key -> {
                if (!newValParamMap.get(key).isUpRedis()) {
                    newValMap.remove(key);
                }
            });
        }
        return newValMap;
    }

    @SuppressWarnings("unchecked")
	protected boolean checkUpRdsParam(String paraCode){
        // modify by zhangrui1 start
        Map<String, IParam> newValParamMap=(Map<String, IParam>) getServiceInfo("paramMap");
        return newValParamMap != null && newValParamMap.containsKey(paraCode) && newValParamMap.get(paraCode).isUpRedis();
        // modify by zhangrui1 end
    }
    //------------------------add by zxjjpei 20200401 end-------------------
    /**
     * 设置参数值权重,用来计算可投资金额
     * 
     * @param eveList 风险事件
     * @param paramVal 参数值列表
     */
//    protected void putParaWeight(List<IEvent> eveList, Map<String, Map<String, Object>> pValMap){
//        forEachEvent(eveList, epr -> PreInvestAmtUtil.putWeight(pValMap, epr));
//    }

    /**
     * 计算 需要参与计算却没有受到风险事件影响的参数
     * @param paraIdList 被影响的参数
     * @param eveList 风险事件
     * @param paraValMap 参数值列表
     */
    protected Map<String, Map<String, IValue<Object>>> calcValWithoutEvt(Set<String> paraIdList, List<IEvent> eveList, Map<String, Map<String, IValue<Object>>> paraValMap){
    	Map<String, Map<String, IValue<Object>>> tempMap = new HashMap<>();
    	paraValMap.entrySet().forEach(en->{
    		// 参数代码
            String paraCode = en.getKey();
            // 参数该粒度没有被影响，但需要参与计算且其它粒度被影响时
            String pCd = StringUtil.substring(paraCode, 0, StringUtil.indexOf(paraCode, GlobalConstant.COLON));
            if (!paraIdList.contains(paraCode) && (
                   paraIdList.contains(pCd + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_买入.getValue())
                || paraIdList.contains(pCd + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_卖出.getValue())
                || paraIdList.contains(pCd + GlobalConstant.COLON + BussinessDictCodeListRds.参数粒度_实时.getValue()))) {
            	tempMap.put(paraCode,en.getValue());
            }
    	});
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        newValMap.putAll(paraValMap);
    	if (CollectionUtils.isEmpty(tempMap)) {
            return newValMap ;
    	}
    	forEachEvent(eveList, epr -> {
            // 参数代码
            String paraCode = epr.getParaCode();
            // 关系类型
            String relType = epr.getRelType();
            // 关系预算的对象值
            BigDecimal val = epr.getRelValue();
            String pCd = StringUtil.substring(paraCode, 0, StringUtil.indexOf(paraCode, GlobalConstant.COLON));
            tempMap.entrySet().forEach(en->{
                String tempCode = en.getKey();
                String pCd2 = StringUtil.substring(tempCode, 0, StringUtil.indexOf(tempCode, GlobalConstant.COLON));
                if (!StringUtil.equals(pCd2, pCd)) {
                	return;
                }
                // Map<指标参数值Redis识别域, 指标参数值>
                Map<String, IValue<Object>> valMap = en.getValue();
                if (CollectionUtils.isEmpty(valMap)) {
                    return;
                }
                // Map<指标参数值Redis识别域, 指标参数值>
                Map<String, IValue<Object>> temp = new HashMap<>();
                valMap.entrySet().forEach(item -> {
                    String key = item.getKey();
                    // 参数值
                    IValue<Object> paraVal = item.getValue();
                    //----zxjjpei 更改类型------------------
                    BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal)paraVal.value();
                    IValue<Object> newVal = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                    // 指标参数值计算
                    // modify by zhngrui1 start
                    //newVal.set(CommonUtil.math(par, val, relType));
                    //内置阈值型的参数特殊处理，可能出现小于0的情况
                    if (repoParamList.contains(paraCode)) {
                        newVal.set(CommonUtil.math(par, val, relType));
                    }
                    else {
                    newVal.set(
                        BigDecimal.ZERO.max(CommonUtil.math(par, epr.doFormula(val), relType)));
                        // newVal.set(CommonUtil.math(par, val, relType));
                    }
                    // modify by zhngrui1 end
                    temp.put(key, newVal);
                });
                newValMap.put(tempCode, temp);
            });
        });
    	return newValMap;
    }

    /**
     * 构建计算明细
     * 
     * @param paraCode 参数代码
     * @param key redis识别域
     * @param oldOne 原值
     * @param newOne 新值
     * @param chgOne 变化值
     * @return 计算明细
     */
    private RskIndicatorParaCalcdetail createDetail(String paraCode, String key, BigDecimal oldOne, BigDecimal newOne, BigDecimal chgOne){
    	// 计算明细
        RskIndicatorParaCalcdetail detail = new RskIndicatorParaCalcdetail();
        int index = StringUtil.indexOf(paraCode, GlobalConstant.COLON);
        // 参数代码
        detail.setParaCode(StringUtil.substring(paraCode, 0, index));
        // 参数粒度
        detail.setParaGranularity(StringUtil.substring(paraCode, index + 1));
        // 原值
        detail.setOldValue(oldOne);
        // 新值
        detail.setNewValue(newOne);
        String[] keyArr = StringUtil.split(key, GlobalConstant.COMMA);
        if (keyArr != null && keyArr.length > 0) {
            // 设置产品代码
            detail.setPortfolioId(keyArr[0]);
            // 设置资产代码
            detail.setAssetCode(keyArr.length > 1 ? keyArr[1] : keyArr[0]);
        }
        // 变化值
        detail.setChangeValue(chgOne);
        return detail;
    }

    protected void freshDetail(List<RskIndicatorParaCalcdetail> details, Map<String, IParam> paramMap){
        if (StringUtil.isEmptyList(details)) {
            return;
        }
        details.forEach(detail->{
            String paraCode = detail.getParaCode() + GlobalConstant.COLON + detail.getParaGranularity();
            IParam param = paramMap.get(paraCode);
            if (param == null) {
                return;
            }
            String[] arr = param.getRedisFieldArr();
            if (arr != null && arr.length == 1) {
                if (StringUtil.equals(arr[0], PORTFOLIOCODE)) {
                    detail.setAssetCode("");
                }
                else {
                    detail.setPortfolioId("");
                }
            }
        });
    }

    private boolean containsPara(List<RskIndicatorParaCalcdetail> details, String paraCode){
    	if (StringUtil.isEmptyList(details)) {
    		return false;
    	}
    	for(RskIndicatorParaCalcdetail detail:details) {
    		if (detail != null && StringUtil.equals(paraCode, detail.getParaCode() + GlobalConstant.COLON + detail.getParaGranularity())) {
    			return true;
    		}
    	}
    	return false;
    }

    protected String getFiled(String[] redisFld, Map<String, Object> mapData) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < redisFld.length; i++ ) {
            Object val = mapData.getOrDefault(redisFld[i], redisFld[i] + GlobalConstant.COLON + mapData.get(EVENT_ID));
            if (!StringUtil.isNotEmpty(val)) {
            	val = redisFld[i] + GlobalConstant.COLON + mapData.get(EVENT_ID);
            }
            builder.append(GlobalConstant.COMMA).append(val);
        }
        return builder.length() > 1 ? builder.substring(1) : StringUtil.EMPTY;
    }

    @SuppressWarnings("unchecked")
    public void saveRskIsupTrial(ClientData response, Map<String, Object> mapData,
                                 String occupyCode) {
        Object obj = response.getBodyField(RSK_LIST);
        List<RskIsupTrial> sendList = new ArrayList<>();
        if (obj != null) {
            List<Map<String, Object>> riskList = (List<Map<String, Object>>)obj;
            Object eventDate = mapData.get("eventDate");
            Date dt1 = null;
    		if (eventDate instanceof Long) {
    			dt1 = new Date((long)eventDate);
    		} else {
    			dt1 = DateUtil.parse(eventDate.toString());
    		}
    		Date dt = dt1;
            riskList.forEach(map -> {
                RskIsupTrial info = new RskIsupTrial();
                // 流水号
                info.setUuid(IDGenerator.generate());
                // 业务编号
                info.setTradeId(occupyCode);
                // 组合代码
                info.setPortfolioId(getStrFromMap(map, PORTFOLIOCODE));
                // 业务类型
                info.setTradeType(getStrFromMap(mapData, EVENT_TYPE));
                // 监督指标编号
                info.setIndId(getStrFromMap(map, KRICODE));
                // 资产代码
                if (!StringUtil.isEmpty((String)mapData.get(ASSETCODE))) {
                    info.setAssetCode((String)mapData.get(ASSETCODE));
                }
                // 发行人
                else if (!StringUtil.isEmpty((String)mapData.get(ISSUER))) {
                	info.setAssetCode((String)mapData.get(ISSUER));
                }
                // 监督日期
                info.setSupDate(dt);
                // 监督值
                info.setSupVal(getStrFromMap(map, KRIVALUE));
                // 原值
                info.setOldSupVal(getStrFromMap(map, OLDVALUE));
                // 趋势
                info.setSupTrend(getStrFromMap(map, KRITREND));
                // 监督状态
                info.setSupStat(getStrFromMap(map, KRISTATUS));
                // 联合监督状态
                info.setUniteSupStat(getStrFromMap(map, UNITESTATUS));
                // 预警条件
                info.setWarnCond(getStrFromMap(map, WARNINGLINE));
                // 审批条件
                info.setApproveCond(getStrFromMap(map, APPROVELINE));
                // 违规条件
                info.setFoulCond(getStrFromMap(map, FORBIDDENLN));
                // 设置流水号
                info.setSeqNo((String) mapData.get("uuid"));
                setUserDt(info);
                sendList.add(info);
            });
        }
        saveDb(Operation.W, sendList.toArray());
    }

    private String getStrFromMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        return obj == null ? StringUtil.EMPTY : String.valueOf(obj);
    }

    /**
     * 设置 预占下实占的总金额
     * 
     * @param preOcpInfo 预占信息
     */
    protected void setAllOcpVal(IOccupyRelParam preOcpInfo) {
        if (preOcpInfo == null) {
            return;
        }
        Set<String> ocpCodeList = preOcpInfo.getOcpCodeList();
        if (CollectionUtils.isEmpty(ocpCodeList)) {
            return;
        }
        Map<String, IOcpRelParamItem> preItems = preOcpInfo.getItems();
        if (CollectionUtils.isEmpty(preItems)) {
            return;
        }
        Map<String, IOccupyRelParam> ocpMap = ocpService.getMapFromRedis(RedisKeyEnum.预占实占信息.key(), ocpCodeList);
        if (CollectionUtils.isEmpty(ocpMap)) {
            return;
        }
        OccupyRelParam ocpInfo = new OccupyRelParam();
        Map<String, IOcpRelParamItem> allItems = new HashMap<>();
        // 循环实占信息
        ocpMap.values().forEach(ocp -> {
            Map<String, IOcpRelParamItem> items = ocp == null ? new HashMap<>() : ocp.getItems();
            if (CollectionUtils.isEmpty(items)) {
                return;
            }
            // 汇总
            preItems.entrySet().forEach(en -> {
                String key = en.getKey();
                int idx = StringUtil.indexOf(key, GlobalConstant.COLON);
                key = StringUtil.substring(key, 0, idx + 1) + BussinessDictCodeListRds.参数粒度_实时.getValue();
                IOcpRelParamItem item = allItems.get(en.getKey());
                IOcpRelParamItem temp = items.get(key);
                if (item == null) {
                    allItems.put(en.getKey(), temp);
                }
                else {
                    // ZGCPCN-1150:使用计算工具类对计算做null保护 modify by fuyongsheng 20220609 start
                    // item.setOccupyVal(item.getOccupyVal().add(temp.getOccupyVal()));
                    item.setOccupyVal(ArithmeticUtil.doAdd(item.getOccupyVal(), temp.getOccupyVal()));
                    // ZGCPCN-1150:使用计算工具类对计算做null保护 modify by fuyongsheng 20220609 end
                }
            });
        });
        // 空的占用列表时返回
        if (CollectionUtils.isEmpty(allItems)) {
            return;
        }
        // 设置占用列表
        ocpInfo.setItems(allItems);
        // 设置信息
        setServiceInfo(preOcpInfo.getField(), ocpInfo);
    }

    /**
     * 取出存储本地的所有实占合计金额/数量
     * 
     * @param preOcpCode 预占编号
     * @param paraCode 参数代码
     * @return 所有实占合计金额/数量
     */
    protected BigDecimal getAllOcpVal(String preOcpCode, String paraCode){
        OccupyRelParam ocpInfo = (OccupyRelParam) getServiceInfo(preOcpCode);
        if (ocpInfo == null) {
            return BigDecimal.ZERO;
        }
        Map<String, IOcpRelParamItem> allItems = ocpInfo.getItems();
        if (CollectionUtils.isEmpty(allItems)) {
            return BigDecimal.ZERO;
        }
        IOcpRelParamItem item = allItems.get(paraCode);
        return (item == null || item.getOccupyVal() == null) ? BigDecimal.ZERO : item.getOccupyVal();
    }

    /**
     * 刷新预占金额
     * 
     * @param preOcpInfo 预占信息
     * @param mapData 请求数据
     */
    protected void refreshPreOcpInfo(IOccupyRelParam preOcpInfo, IOccupyRelParam ocpInfo,
                                     Map<String, Object> mapData) {
        if (preOcpInfo == null) {
            return;
        }
        setAllOcpVal(preOcpInfo);
        Map<String, IOcpRelParamItem> items = preOcpInfo.getItems();
        Map<String, IOcpRelParamItem> opcItems = ocpInfo.getItems();
        if (CollectionUtils.isEmpty(items) || CollectionUtils.isEmpty(opcItems)) {
            return;
        }
        String field = preOcpInfo.getField();
        items.entrySet().forEach(en -> {
            String paraCode = en.getKey();
            IOcpRelParamItem item = en.getValue();
            BigDecimal relVal = getAllOcpVal(field, paraCode);
            // 实占金额大于预占金额时，刷新预占金额
            BigDecimal ocpVal = item.getOccupyVal();
            if (relVal != null && ocpVal != null && relVal.compareTo(ocpVal) > 0) {
                item.setOffsetVal(relVal.subtract(ocpVal).add(item.getOffsetVal()));
            }
        });
    }

    /**
     * 将试算的结果写入返回的报文中
     * 
     * @param response 返回的报文
     * @param resMap 试算的结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	protected void writeResponse(ClientData response, Map<String, IIndicator> resMap, Map<String, IParam> paraMap) {
        response.setBodyField(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
        if (CollectionUtils.isEmpty(resMap)) {
            return;
        }
        List<Map<String, Object>> rskList = new ArrayList<>();
        Map<String,Object> ptlMap = (Map<String, Object>) getServiceInfo("PTL_NAME");
        resMap.entrySet().forEach(en -> {
            IIndicator ind = en.getValue();
            Map<String, IIndicatorResult> uniteMap = ind.getIndicatorResult();
            if (CollectionUtils.isEmpty(uniteMap)) {
                return;
            }
            uniteMap.entrySet().forEach(resEn -> {
                String ptlCode = resEn.getKey();
                String ptlName = (String) ptlMap.get(ptlCode);
                IIndicatorResult res = resEn.getValue();
                rskList.add(getResMap(response, ptlCode, ptlName, ind, res, resMap));
            });
        });
        if (CommonUtil.isType((String)response.getBodyField(RSKSTATUS), BussinessDictCodeListRds.指标状态_异常_除0)) {
            setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 99);
            String msgKey = InfColumns.COM_RTNMSG.fieldName();
            setRtnMsg(response, CommonUtil.replaceMsgWithName((String)response.getHeadField(msgKey), paraMap));
            //return;
        }
        response.setBodyField(RSK_LIST, rskList);
    }

    protected Map<String, Object> getResMap(ClientData response, String ptlCode, String ptlName, IIndicator ind,
                                            IIndicatorResult res, Map<String, IIndicator> resMap) {
        Map<String, Object> temp = new HashMap<>();
       /* if (CommonUtil.isType((String)response.getBodyField(RSKSTATUS), BussinessDictCodeListRds.指标状态_异常_除0)) {
            return temp;
        }*/
        // 产品代码
        temp.put(PORTFOLIOCODE, ptlCode);
        // 组合（包）名称
        temp.put(PORTFOLIONAME, ptlName);
        // 指标名称
        temp.put(KRICODE, ind.getIndId());
        // 指标名称
        temp.put(KRINAME, ind.getIndName());
        // 指标类型
        String indCellType = ind.getIndType();
        temp.put(KRITYPE, indCellType);
        temp.put(KRITYPECN, SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.指标类型, indCellType));
        IIndividLimit limit = res.getIndividLimit();
        // 审批线
        temp.put(APPROVELINE, changeCond(indCellType,
            () -> limit != null ? limit.getApproveCond() : ind.getApproveCond()));
        // 预警线
        temp.put(WARNINGLINE, changeCond(indCellType,
            () -> limit != null ? limit.getWarnCond() : ind.getWarnCond()));
        // 禁止线
        temp.put(FORBIDDENLN, changeCond(indCellType,
            () -> limit != null ? limit.getFoulCond() : ind.getFoulCond()));
        //计算表达式
        temp.put(KRIEXPR,res.getCalcExpr());
        //-------zxjjpei 添加枚举型判断------
        boolean trendNoneFlag=false;
        if(ind.getIndType().indexOf(RiskUtil.LIMIT)>=0){
            //阈值型不管是走库还是走redis应该都有原值与趋势
            if(checkDisplayOldVal(ind)){
                trendNoneFlag=true;
                //原计算表达式
                temp.put(KRIOLDEXPR,res.getCalcOldExpr());
                // 指标计算值
                temp.put(KRIVALUE, CommonUtil.convertKriVal(res.getIndicatorVal(), limit == null ? ind : limit));
                // 指标原值
                temp.put(OLDVALUE, CommonUtil.convertKriVal(res.getIndicatorOldVal(), limit == null ? ind : limit));
                // 趋势
                temp.put(KRITREND, StringUtil.isNotEmpty(res.getIndicatorVal()) ? trendToStr(res.getIndicatorTrend()) : "");
           }else {
                // 指标计算值
                temp.put(KRIVALUE, CommonUtil.convertKriVal(res.getIndicatorVal(), limit == null ? ind : limit));
                // 指标原值
                temp.put(OLDVALUE, "");
                // 趋势
                temp.put(KRITREND, "");
            }
        }else{
            // 指标计算值
            temp.put(KRIVALUE, res.getIndicatorVal());
            // 指标原值
            temp.put(OLDVALUE, "");
            // 趋势
            temp.put(KRITREND, "");
        }

        Set<String> uniteList = ind.getPtlIndividUnite(ptlCode);
        String uSts = null;
        String kriStatus = res.getIndStatus();
        // 指标状态
        temp.put(KRISTATUS, kriStatus);
        // 是否有联合指标设置
        boolean hasUnite = !CollectionUtils.isEmpty(uniteList);
        // 当存在某个指标状态超过整体状态且趋势向坏的指标时试算不通过
        String rskStatus = (String)response.getBodyField(RSKSTATUS);
        IProdInfo prod = (IProdInfo) getServiceInfo(getInfId() + "_PROD");
        // 如果出现指标除0则
        if (CommonUtil.isType(kriStatus, BussinessDictCodeListRds.指标状态_异常_除0)) {
            if (ind.isExcpWhenDivZero(prod)) {
                response.setBodyField(RSKSTATUS, kriStatus);
                setRtnMsg(response, CommonUtil.getIndicatorFm(ind) + "为0或不存在");
                return temp;
            }
            kriStatus = BussinessDictCodeListRds.指标状态_通过.getValue();
        }
        // 建仓期是否直接通过
        if (ind.isPassWhenBuild(prod)) {
            kriStatus = BussinessDictCodeListRds.指标状态_通过.getValue();
        }
        // 有联合指标时
        if (hasUnite) {
            uSts = checkStatus(kriStatus, uniteList, ptlCode, resMap);
            res.setUniteStatus(uSts);
            temp.put(UNITESTATUS, uSts);
        } else {
            uSts = kriStatus;
        }
        if (StringUtil.isNotEmpty(uSts) && uSts.compareTo(rskStatus) > 0 && ((res.getIndicatorTrend() < 0 &&trendNoneFlag)||!trendNoneFlag)) {
            //----------update by 裴家俊 2019-11-15-----start-----
            response.setBodyField(RSKSTATUS, uSts);
            //----------update by 裴家俊 2019-11-15------end----
        }
        return temp;
    }

    /**
     * 金额*上调系数
     * 
     * @param request 报文体
     */
    protected void factor(Map<String,Object> request) {
        final String amountKey = "amount";
        Object amountObj = request.get(amountKey);
        // 资产大类
        Object assetType = request.get(ASSETTYPE);
        // 交易方向
        Object ps = request.get(PS);
        // 事件类型
        Object eventType = request.get(EVENT_TYPE);
        StringBuilder builder = new StringBuilder(FACTOR);
        builder.append(GlobalConstant.UNDER_LINE).append(eventType)
               .append(GlobalConstant.UNDER_LINE).append(assetType)
               .append(GlobalConstant.UNDER_LINE).append(ps);
        String factor = InterfCacheUtil.getSysParam(builder.toString());
        // 若系数不存在则不计算
        if (StringUtil.isEmpty(factor)) {
            return;
        }
        BigDecimal factorNum = new BigDecimal(factor);
        BigDecimal amount = amountObj instanceof BigDecimal ? 
                (BigDecimal) amountObj : new BigDecimal(amountObj.toString());
        request.put(amountKey, amount.multiply(factorNum));
    }

    /**
     * 判断是否显示原值---- add by zxjjpei 20200401 -----
     * @param ind
     * @return
     */
    private boolean checkDisplayOldVal(IIndicator ind){
        if (StringUtil.isNotEmpty(ind)&&StringUtil.equals(DictCodeList.是否_否.getValue(),ind.getIsDisplayOriginal())) {
            return false;
        }
        return true;
    }
    private String trendToStr(int trend) {
        String trendStr = "";
        if (trend == 0) {
            trendStr = "不变";
        }
        else {
            trendStr = trend > 0 ? "向好" : "向坏";
        }
        return trendStr;
    }

    /**
     * 阈值条件解析
     * 
     * @param indCellType 指标类型
     * @param fun 阈值条件取得接口
     * @return 解析结果
     * @see
     * @since 1.0
     */
    private String changeCond(String indCellType, Limit fun) {
        try {
            return RiskUtil.transferWarnFoul(fun.limit(), indCellType);
        }
        catch (Exception e) {
            getLogger().error("阈值条件解析异常", e);
            return "";
        }
    }

    interface Limit {

        String limit();
    }

    /**
     * 指标联合设置判断
     * 
     * @param rskStatus 指标状态
     * @param indList 联合指标
     * @param ptlCode 组合代码
     * @param resMap 指标列表
     * @return 指标联合设置判断结果
     * @see
     * @since 1.0
     */
    protected String checkStatus(String rskStatus, Set<String> indList, String ptlCode,
                                 Map<String, IIndicator> resMap) {
        String min = rskStatus;
        List<IIndicatorResult> resList = new ArrayList<>();
        for (String str : indList) {
            IIndicator ind = resMap.get(str);
            IIndicatorResult res = ind == null ? null : ind.getPtlIndicatorResult(ptlCode);
            if (ind != null && res == null) {
                return BussinessDictCodeListRds.指标状态_通过.getValue();
            }
            if (res == null || StringUtil.isEmpty(res.getIndStatus())) {
                continue;
            }
            String temp = res.getIndStatus();
            if (CommonUtil.isType(temp, BussinessDictCodeListRds.指标状态_异常_除0)) {
                temp = BussinessDictCodeListRds.指标状态_通过.getValue();
            }
            // 取最小状态
            min = min.compareTo(temp) < 0 ? min : temp;
            resList.add(res);
        }
        final String temp = min;
        resList.forEach(res -> res.setUniteStatus(temp));
        return min;
    }

    /**
     * 获取 ptlIndService
     * 
     * @return ptlIndService.
     */
    public PtlIndicatorService getPtlIndService() {
        return ptlIndService;
    }

    /**
     * 获取 indService
     * 
     * @return indService.
     */
    public IndicatorService getIndService() {
        return indService;
    }

    /**
     * 获取 rskEventSrv
     * 
     * @return rskEventSrv.
     */
    public RskEventService getRskEventSrv() {
        return rskEventSrv;
    }

    /**
     * 获取 ocpService
     * 
     * @return ocpService.
     */
    public OccupyRelParamService getOcpService() {
        return ocpService;
    }

    /**
     * 获取 indParamSrv
     * 
     * @return indParamSrv.
     */
    public IndicatorParamService getIndParamSrv() {
        return indParamSrv;
    }

    /**
     * 获取 calcService
     * 
     * @return calcService.
     */
    public IndicatorCalcService getCalcService() {
        return calcService;
    }

    /**
     * 获取 paramValSrv
     * 
     * @return paramValSrv.
     */
    public IndicatorParamValService getParamValSrv() {
        return paramValSrv;
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
     * 获取 prodInfoSrc
     * 
     * @return prodInfoSrc.
     */
    public ProdInfoRedisService getProdInfoSrc() {
        return prodInfoSrc;
    }

    /**
     * 获取 assetIndexSrc
     * 
     * @return assetIndexSrc
     */
    public AssetIndexService getAssetIndexSrc() {
		return assetIndexSrc;
	}

	/**
     * 取得 资产基本信息
     * 
     * @param mapData 数据信息
     * @return 资产基本信息
     * @see
     * @since 1.0
     */
    protected Map<String, Object> getAssetInfo(Map<String, Object> mapData) {
        // 资产代码
        String assetCode = (String)mapData.get(ASSETCODE);
        // 根据市场代码和交易场所取得资产代码
        if (StringUtil.isEmpty(assetCode)) {
        	String market = (String) mapData.get(MARKET_CODE);
        	String trade = (String) mapData.get(TRADE_PLACE);
        	IAssetIndex assetIndex = assetIndexSrc.getAssetIndex(market, trade);
        	if (assetIndex != null) {
        		assetCode = assetIndex.getAssetCode();
        	}
        }
        return assetInfoSrc.getAssetInfoAsMap(assetCode);
    }

    /**
     * 取得 产品基本信息
     * 
     * @param prodId 产品代码
     * @return 产品基本信息
     * @see
     * @since 1.0
     */
    protected IProdInfo getProdInfo(String prodId) {
        IProdInfo prod = prodInfoSrc.getProdInfo(prodId);
        if (prod != null) {
            setServiceInfo(getInfId() + "_PROD", prod);
        }
        return prod;
    }

    /**
     * 交易方向数据字典转换
     * 
     * @param mapData 请求数据
     * @see
     * @since 1.0
     */
    protected void changePsValue(Map<String, Object> mapData) {
        //----update by jjpei 投管产品不需要交易方向转换------
       /* String ps = (String)mapData.get(PS);
        ps = InterfCacheUtil.changeDictCode(InfDictCode.交易方向, ps);
        mapData.put(PS, ps);*/
        //----update by jjpei 投管产品不需要交易方向转换------
    }

    /**
     * 根据计算结论判断是否继续后续处理
     * 
     * @param request 请求数据
     * @param mapData 请求数据
     * @param response 应答数据
     * @return 0:表示继续后续处理；否则，中断处理
     * @see
     * @since 1.0
     */
    protected int isStopForReturn(ClientData request, Map<String, Object> mapData, ClientData response) {
        // 根据状态判断是否需要继续处理
        String rskStatus = (String)response.getBodyField(RSKSTATUS);
        String ignoreStatus = mapData.getOrDefault("ignoreStatus","N").toString();
        /*if (CommonUtil.isType(rskStatus, BussinessDictCodeListRds.指标状态_预警) && isWarnNdCfm(request)) {
            setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 0);
            setRtnMsg(response, "已达预警线，是否继续？");
            return -1;
        }
        boolean isApprove = CommonUtil.isType(rskStatus, BussinessDictCodeListRds.指标状态_审批);
        if (isApprove && isApprNdCfm(request)) {
            setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 0);
            setRtnMsg(response, "已达审批线，是否继续？");
            return -1;
        }
        String stAppSys = InterfCacheUtil.getSysParam(INF_STAPP_SYS);
        // 达到审批线且交互系统需要自动启动风险工作流时
        if (isApprove && StringUtil.contains(stAppSys, getSysCode())) {
            // 启动工作流
            String eventType = (String)request.getBodyField(EVENT_TYPE);
            // 恢复备份的产品代码
        	mapData.put(PORTFOLIOCODE, mapData.get(PROD_ID));
        	final String sysCode = getSysCode();
        	threadPoolTaskExecutor.execute(new Runnable(){
				@Override
				public void run() {
		            try {
		                apprService.setServiceInfo(InfColumns.COM_SYSCODE.fieldName(), sysCode);
		                apprService.startApproval(mapData, eventType);
		            }
		            catch (WorkFlowException e) {
		                getLogger().error("流程启动异常", e);
		            }
				}
        	});
        }*/
        if (CommonUtil.isType(rskStatus, BussinessDictCodeListRds.指标状态_禁止) && !"Y".equals(ignoreStatus)) {
//            setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 3);
//            setRtnMsg(response, "风险指标违规");
//            // 无视违规则返回0
//            return isIgnoreFoul(mapData) ? 0 : 1;

            boolean result = isIgnoreFoul(mapData);
            if (result) {
                return 0;
            }

            //增加软控硬控开关 add by jqq 20201216 start
            String institutionCode = "";
            if(MultilevelDataAuthorityUtil.isEnabled()){
                institutionCode =  MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
            }
            String isSoft = CacheUtil.getSpecialSysParam("IS_RISKMATH_SOFTCONTROL", institutionCode);
            if("02".equals(isSoft)){//硬控
                setRtnCode(response, InfDictCodeList.信息码_提示.getValue(), 3);
                setRtnMsg(response, "风险指标违规");
                return 1;
            }else{
                return 0;
            }
            //增加软控硬控开关 add by jqq 20201216 end
        }
        if (CommonUtil.isType(rskStatus, BussinessDictCodeListRds.指标状态_异常_除0)) {
            //----update by zxjjpei 20200320 暂处理为返回0 start----
            //return 5;
            //----update by zxjjpei 20200320 暂处理为返回0 end----
        }
        return 0;
    }

    // add by zhangrui1 指定场景违规时继续更新参数值 start
    /**
     * 是否无视违规
     * 
     * @param mapData 报文
     * @return true : 无视违规
     */
    protected boolean isIgnoreFoul(Map<String, Object> mapData) {
        return StringUtil.equals(mapData.get("isIgnoreFoul"),
            BussinessDictCodeListRds.是否_是.getValue());
    }
    // add by zhangrui1 指定场景违规时继续更新参数值 end

    /**
     * 是否计算指标
     * 
     * @param eventList 风险事件列表
     * @return true:计算指标,false:不计算指标
     */
    protected boolean isCalcIndicator(List<IEvent> eventList){
        Iterator<IEvent> iterator = eventList.iterator();
        while (iterator.hasNext()){
            IEvent event = iterator.next();
            if (CommonUtil.isType(event.getIsCalcIndicator(), BussinessDictCodeListRds.是否_否)) {
                getLogger().info("风控事项 {} 不计算指标",event.getEventName());
                iterator.remove();
            }
        }
        return !StringUtil.isEmptyList(eventList);
    }

    /**
     * 试算禁止时是否更新参数保留计算流水
     * 
     * @param dataMap 报文体
     * @return true:更新参数且保留计算流水
     */
    protected boolean isCalcParaWhenForbid(Map<String, Object> dataMap) {
        // 报文中的事件类型
        Object eventType = dataMap.get(EVENT_TYPE);
        // 取出系统参数配置
        String para = InterfCacheUtil.getSysParam(IS_CALC_WHEN_FBD + eventType);
        // 报文中的交易阶段
        Object tradeStage = dataMap.get(TRADE_STAGE);
        return tradeStage != null && StringUtil.contains(para, tradeStage.toString());
    }

    /**
     * 构建组合(包)与风险事件关联的参数关系索引
     * 
     * @param ptlParamMap 组合(包)与参数关系索引
     * @param paraIdList 风险事件
     */
    protected void putEventParam(Map<String, Map<String, String>> ptlParamMap, Set<String> paraIdList){
    	if (CollectionUtils.isEmpty(paraIdList)) {
    		return;
    	}
    	if (!CollectionUtils.isEmpty(ptlParamMap)) {
    		ptlParamMap.entrySet().forEach(en->{
    			Map<String, String> value = en.getValue();
    			paraIdList.forEach(item->value.putIfAbsent(item, StringUtil.EMPTY));
    		});
    	}
    }
    
    protected <T> void log(String msg, Iterable<T> var, ItConsumer<T, String> consumer){
    	if (var != null && consumer != null) {
    		StringBuilder builder = new StringBuilder();
    		var.forEach(item->builder.append(consumer.accept(item)).append(GlobalConstant.COMMA));
    		String logStr = builder.toString();
    		getLogger().info(msg, logStr);
    	}
    }

    protected <T> void setRemark(ClientData response,String msg, Iterable<T> var, ItConsumer<T, String> consumer,Map<String, IParam> paraMap){
        if (var != null && consumer != null) {
            StringBuilder builder = new StringBuilder();
            var.forEach(item->builder.append(consumer.accept(item)).append(GlobalConstant.COMMA));
            String logStr = builder.toString();
            if (!CollectionUtils.isEmpty(paraMap) && StringUtil.isNotEmpty(logStr)) {
                for(Map.Entry<String, IParam> en : paraMap.entrySet()) {
                    String paraCode = en.getKey();
                    if (StringUtil.contains(logStr, paraCode)&&StringUtil.isNotEmpty(en.getValue())&&StringUtil.isNotEmpty(en.getValue().getParaName())&&paraCode.indexOf(GlobalConstant.COLON)>=0) {
                        logStr = StringUtil.replace(logStr, paraCode, en.getValue().getParaName()+paraCode.substring(paraCode.indexOf(GlobalConstant.COLON),paraCode.length()));
                    }
                }
            }
            response.setHeadField(InfColumns.COM_STDREMARK.fieldName(),StringUtil.isNotEmpty(response.getHeadField(InfColumns.COM_STDREMARK.fieldName()))?(String)response.getHeadField(InfColumns.COM_STDREMARK.fieldName()):StringUtil.EMPTY+msg+logStr);
        }
    }

    /**
     * 保存 指标参数计算明细
     * 
     * @param details 参数计算明细
     * @param mapData 报文
     * @param occupyCode 预占实占编号
     */
    protected void saveDetails(List<RskIndicatorParaCalcdetail> details, Map<String, Object> mapData, String occupyCode){
        if (!StringUtil.isEmptyList(details)) {
        	details.forEach(detail->{
        		// 业务编号
        		detail.setTradeId(occupyCode);
        		// 业务类型
        		detail.setTradeType((String) mapData.get(EVENT_TYPE));
        		Object eventDate = mapData.get("eventDate");
        		if (eventDate instanceof Long) {
            		// 设置监督日期
            		detail.setSupDate(new Date((long)eventDate));
        		} else {
        		// 设置监督日期
        		detail.setSupDate(DateUtil.parse(eventDate.toString()));
        		}
        		// 设置时间戳
        		Object obj = mapData.get("uuid");
        		detail.setDotimestamp(DateUtil.format(new Date(new Long(obj.toString())),DateUtil.DEFAULT_DATETIME_FORMAT_SSS));
        		// 设置UUID
        		detail.setUuid(IDGenerator.generate());
        		// 创建人
        		detail.setCreateUser(getInfId());
        		// 更新人
        		detail.setUpdateUser(getInfId());
        	});
        	// 保存详细结果
//        	threadPoolTaskExecutor.execute(()->rskDetailSrc.consume(Operation.W, details));
            saveDb(Operation.W, details.toArray(new RskIndicatorParaCalcdetail[details.size()]));
        }
    }

    /**
     * 设置创建人时间等
     * 
     * @param info 数据
     * @see
     * @since 1.0
     */
    public void setUserDt(BaseEntity info) {
        String user = getInfId();
        // 创建人
        info.setCreateUser(user);
        // 更新人
        info.setUpdateUser(user);
        Date dt = DateUtil.now();
        // 创建时间
        info.setCreateTime(dt);
        // 更新时间
        info.setUpdateTime(dt);
    }

    protected void occupyWithoutCalcNoneEvent(String occupyCode, List<IEvent> eveList,
                                   Map<String, Object> mapData, IPtlIndicator ptlInd)
            throws InterfException {

        // 预占编号
        String preCode = (String)mapData.get("preOccupyCode");
        String preOccupyCode = StringUtil.isEmpty(preCode) ? ""
                : (String)mapData.get("sourceSys") + GlobalConstant.COLON + preCode;
        // 执行事务处理（Redis操作）
        int type = handleByLock(() -> {
            String uuid =  String.valueOf(System.currentTimeMillis());
            mapData.put("uuid", uuid);
            // 预占实占参数信息
            IOccupyRelParam ocpRelParam = getOccupy(preOccupyCode, PRE_OCCUPY);
            if (ocpRelParam == null && !StringUtil.isEmpty(preCode)) {
                throw new InterfException(preCode + "对应的预占信息不存在");
            }
            OccupyRelParam newOcpRelParam = new OccupyRelParam();
            Map<String, IOcpRelParamItem> ocpPmItem = new HashMap<>();
            newOcpRelParam.setItems(ocpPmItem);
            ocpRelParam = ocpRelParam == null ? newOcpRelParam : ocpRelParam;
            // 预占参数信息
            ocpPmItem.putAll(ocpRelParam.getItems());
            newOcpRelParam.setOccupyCode(occupyCode);
            // 预占/实占类型
            newOcpRelParam.setOccupyType(REL_OCCUPY);
            // 预占/实占参数信息列表
            Map<String, IOcpRelParamItem> temp = ocpRelParam == null ? new HashMap<>() : ocpRelParam.getItems();
            if (!CollectionUtils.isEmpty(ocpPmItem) && !CollectionUtils.isEmpty(temp)
                    && ocpPmItem.entrySet() != null && !StringUtil.isEmpty(preCode)) {
                Set<String> keys = new HashSet<>();
                temp.entrySet().forEach(en->{
                    if (en != null) {
                        keys.add(en.getKey());
                    }
                });
                keys.forEach(key->ocpPmItem.remove(key));
            }
            else {
                temp.putAll(ocpPmItem);
            }
            // 预占/实占参数信息列表
            newOcpRelParam.setItems(ocpPmItem);
            // 设置净值
            newOcpRelParam.setNetPrice((BigDecimal) mapData.get("unitNet"));
            // 更新预占/实占信息
            setOccupyRelParam(newOcpRelParam);
            return 0;
        });
        if (type == 0) {
            // 更新DB
            Rsk003InEntity record = InfMapValUtil.mapDataToObject(mapData, Rsk003InEntity.class);
            // 设置实占编号
            record.setEventId(occupyCode);
            // 设置预占编号
            record.setOccupyCode(preOccupyCode);
            // 设置组合代码
            record.setProdId((String)mapData.get(PROD_ID));
            // 设置备份数据
            record.setAmount(record.getAmountOld());
            record.setTradeNum(record.getTradeNumOld());
            // 实占信息保存DB
            busiService.saveOcpInfo(record, false, STATUS_OCCUPY, getInfId());

        }
    }

    // add by zhangrui1 试算返回值增加资产名称 start
    /**
     * 处理返回结果增加资产名称和资产代码
     * 
     * @param response 返回结果
     * @param assetInfo 资产信息
     */
    @SuppressWarnings("unchecked")
    protected void handleResponse(ClientData response, Map<String, Object> assetInfo, int groupId) {
        List<Map<String, Object>> rskList = (List<Map<String, Object>>) response.getBodyField(RSK_LIST);
        if (StringUtil.isEmptyList(rskList)) {
            return;
        }
        rskList.forEach(map -> map.put("groupId", groupId));
        if (!CollectionUtils.isEmpty(assetInfo)) {
            rskList.forEach(map -> map.put(ASSETNAME, assetInfo.get(ASSETNAME)));
        }
    }
    // add by zhangrui1 试算返回值增加资产名称 end
    // add by zhangrui1 同向变动处理Service start
    public IndicatorTrendCalcService getIndTrendCalcSrc() {
        return indTrendCalcSrc;
    }
    // add by zhangrui1 同向变动处理Service end
    // add by zhangrui1 差分预占 start
    public boolean isDiffOcp(Map<String, Object> mapData) {
        return StringUtil.equals(mapData.get(IS_DIFF_OCCUPY),
            BussinessDictCodeListRds.是否_是.getValue());
    }
    // add by zhangrui1 差分预占 end
    // add by zhangrui1 一级投标的修改预占试算 start
    /**
     * 重复预占/实占前的风险试算时，需要先抹除前面的预占/实占额度
     * 
     * @param newValMap 参数值列表
     * @param preOcpParam 预占实占信息
     * @param mapData 报文信息
     */
    protected void removeBeforePre(Map<String, Map<String, IValue<Object>>> newValMap,
                                IOccupyRelParam preOcpParam, Map<String, Object> mapData) {
        // 非重复预占/实占排除、差分预占排除
        if (preOcpParam == null || CollectionUtils.isEmpty(preOcpParam.getItems())
            || isDiffOcp(mapData) || StringUtil.isNotEmpty(mapData.get("preOccupyCode"))) {
            return;
        }
        Map<String, IOcpRelParamItem> items = preOcpParam.getItems();
        newValMap.entrySet().forEach(en -> {
            // 参数代码
            String paraCode = en.getKey();
            // 预占实占信息
            IOcpRelParamItem relParam = items.get(paraCode);
            if (relParam == null) {
                return;
            }
            // 操作符
            String opr = relParam.getOccupyOper();
            // 预占实占额度
            BigDecimal ocpVal = relParam.doFormula();
            // 参数值
            Map<String, IValue<Object>> valMap = en.getValue();
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            valMap.entrySet().forEach(valEn -> {
                IValue<Object> val = valEn.getValue();
                if (val.value() instanceof BigDecimal) {
                    BigDecimal parVal = (BigDecimal)val.value();
                    // 抹除预占实占的额度
                    val.set(CommonUtil.math(parVal,
                        ocpVal == null ? BigDecimal.ZERO : ocpVal.negate(), opr));
                }
            });
        });
    }

    protected IOccupyRelParam getRelParam(Map<String, Object> mapData,
                                          IOccupyRelParam preOcpParam) {
        if ((StringUtil.equals(mapData.get(EVENT_TYPE), "0") && isDiffOcp(mapData))
            || (StringUtil.isNotEmpty(mapData.get("preOccupyCode")))
            || !StringUtil.isNotEmpty(mapData.get(EVENT_ID))) {
            return preOcpParam;
        }
        return null;
    }
    // add by zhangrui1 一级投标的修改预占试算 end
    // add by zhangrui1 对于批量交易出现两笔同步交易时，排除影响的同一参数 start
    public static final String SAME_PARAM_LIST = "sameParamList:";

    public static final String GROUP_ID = "groupId";

    public static final String IS_SYNC = "isSync";

    /**
     * 对于批量交易出现两笔同步交易时，排除影响的同一参数
     * 
     * @param paraIdList 影响的参数列表
     */
    @SuppressWarnings("unchecked")
    protected void removeSameParam(Set<String> paraIdList, Map<String, Object> mapData, List<IEvent> eveList) {
        // 基金转换申请，由于混合基金投向维护的占比不同，参数值会变化，所以不排除影响的同一参数
        if (StringUtil.equals(DictCodeList.是否_是.getValue(),mapData.get("fundConvertFlg"))){
            return;
        }
        Set<String> sameParamList = (Set<String>)getServiceInfo(
            SAME_PARAM_LIST + mapData.get(GROUP_ID));
        if (!CollectionUtils.isEmpty(paraIdList) && !CollectionUtils.isEmpty(sameParamList)) {
            for (String item : sameParamList) {
                String code1 = StringUtil.substring(item, 0,
                    StringUtil.indexOf(item, GlobalConstant.COLON));
                Iterator<String> it = paraIdList.iterator();
                while(it.hasNext()) {
                    String code2 = it.next();
                    if (paraCodeEquals(code1, code2)) {
                        it.remove();
                        break;
                    }
                }
            }
            // 过滤出没有受影响的风险事项代码
            Set<String> temp = new HashSet<>();
            forEachEvent(eveList, rel -> {
                String paraCode = rel.getParaCode();
                if (containsParaCode(sameParamList, paraCode)) {
                    temp.add(rel.getEventCode());
                }
            });
            // 删除没有受影响的风险事项列表
            if (!CollectionUtils.isEmpty(temp)) {
                Iterator<IEvent> it = eveList.iterator();
                while (it.hasNext()) {
                    IEvent event = it.next();
                    if (temp.contains(event.getEventCode())) {
                        it.remove();
                    }
                }
            }
        }
    }

    protected boolean paraCodeEquals(String code1, String code2) {
        String c1 = code1;
        String c2 = code2;
        if (StringUtil.contains(c1, GlobalConstant.COLON)) {
            c1 = StringUtil.substring(code1, 0, StringUtil.indexOf(code1, GlobalConstant.COLON));
        }
        if (StringUtil.contains(c2, GlobalConstant.COLON)) {
            c2 = StringUtil.substring(code2, 0, StringUtil.indexOf(code2, GlobalConstant.COLON));
        }
        return StringUtil.equals(c1, c2);
    }

    /**
     * 选出每组正反同步委托/交易相同的参数并存到缓存
     * 
     * @param mapList 批量报文信息
     * @throws InterfException 异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void pickSameParam(List<Map> mapList, Map<String, Object> body)
        throws InterfException {
        if (CollectionUtils.isEmpty(mapList)) {
            return;
        }
        // 所有资产代码
        Set<String> assetSet = mapList.stream().map(map -> (String)map.get(ASSETCODE)).collect(Collectors.toSet());

        List<String> fldList = new ArrayList<>();
        fldList.add(EventMap.ALL);
        // 从Redis中获取所有的风险事件信息
        Map<String, EventMap> eMap = rskEventSrv.getMapFromRedis(RedisKeyEnum.风险事件配置.key(), fldList);
        if (CollectionUtils.isEmpty(eMap)) {
            return;
        }
        EventMap eventMap = eMap.get(EventMap.ALL);
        if (eventMap == null || CollectionUtils.isEmpty(eventMap.values())) {
            throw new BusinessException("MSG:风控事项列表为空");
        }
        Collection<IEvent> eList = eventMap.values();
        // 取得批量所有资产信息
        Map<String, IAssetInfo> assetMap = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), assetSet);
        // 所有日历代码
        Set<String> calendarSet = mapList.stream().filter(
            map -> StringUtil.isNotEmpty(assetMap.get((String)map.get(ASSETCODE)))).map(
            map -> assetMap.get((String)map.get(ASSETCODE)).getCalendarCode()).collect(
                Collectors.toSet());
        // 取得批量所有日历信息
        Map<String, ICalendar> calendarMap = calendarSrc.getMapFromRedis(RedisKeyEnum.日历信息.key(), calendarSet);
        if (CollectionUtils.isEmpty(assetMap) || CollectionUtils.isEmpty(calendarMap)) {
            return;
        }
        Set<String> paraIdList = new HashSet<>();
        Map<String, Set<String>> groupSet = new HashMap<>();
        // 循环取出触发的所有参数
        mapList.forEach(map -> {
            if (CollectionUtils.isEmpty(map)) {
                return;
            }
            // 外层加工的
            body.forEach((k, v) -> {
                if (!(v instanceof List)) {
                    map.put(k, v);
                }
            });
            IAssetInfo assetInfo = assetMap.get((String)map.get(ASSETCODE));
            if (null == assetInfo) {
                return;
            }
            map.putAll(BeanUtil.object2Map(assetInfo));
            ICalendar calendar = calendarMap.get((String)map.get(CALENDAR_ID));
            if (calendar == null) {
                return;
            }
            List<IEvent> eveList = matchEvent(map, eList, calendar);
            Set<String> tempList = getParamIdList(eveList);
            paraIdList.addAll(tempList);
            String groupId = (String)map.get(GROUP_ID);
            Set<String> tempSet = groupSet.getOrDefault(groupId, new HashSet<>());
            // 第一次
            if (CollectionUtils.isEmpty(tempSet)) {
                tempSet.addAll(tempList);
            }
            // 第二次比较如果两边都有参数取出相同的参数
            else if (!CollectionUtils.isEmpty(tempList)) {
                tempSet = (Set<String>)CommonUtil.filter(tempSet, HashSet.class,
                    item -> containsParaCode(tempList, item));
            }
            // 第二次比较如果一边没值表示没有相同的参数
            else {
                tempSet = new HashSet<>();
            }
            groupSet.put(groupId, tempSet);
        });
        if (CollectionUtils.isEmpty(groupSet) || CollectionUtils.isEmpty(paraIdList)) {
            return;
        }
        // 取得所有参数信息
        Map<String, IParam> paramMap = getParam(paraIdList);
        // 过滤出每组相同的参数并存到缓存中
        for (Entry<String, Set<String>> en : groupSet.entrySet()) {
            String groupId = en.getKey();
            Set<String> value = en.getValue();
            pickSameParam(value, paramMap,
                mapList.stream().filter(
                    map -> StringUtil.equals(map.get(GROUP_ID), groupId)).collect(
                        Collectors.toList()));
        }
    }

    protected boolean containsParaCode(Collection<String> list, String paraCode) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        String code = StringUtil.substring(paraCode, 0,
            StringUtil.indexOf(paraCode, GlobalConstant.COLON));
        for (String item : list) {
            if (StringUtil.equals(code,
                StringUtil.substring(item, 0, StringUtil.indexOf(item, GlobalConstant.COLON)))) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void pickSameParam(Set<String> paraIdList, Map<String, IParam> paramMap,
                                 List<Map> mapList)
        throws InterfException {
        if (CollectionUtils.isEmpty(mapList)) {
            return;
        }
        if (mapList.size() != 2) {
            throw new InterfException("MSG:报文格式不正确，正反同步型委托/交易必须成对");
        }
        Set<String> resSet = new HashSet<>();
        paraIdList.forEach(para -> {
            IParam param = paramMap.get(para);
            if (param == null) {
                return;
            }
            // Redis存储识别域
            String[] rdsFields = param.getRedisFieldArr();
            if (rdsFields == null || rdsFields.length == 0) {
                return;
            }
            // 两个识别域相等则说明这个参数是相同参数
            if (StringUtil.equals(getFiled(rdsFields, mapList.get(0)), getFiled(rdsFields, mapList.get(1)))) {
                resSet.add(para);
            }
        });
        setServiceInfo(SAME_PARAM_LIST + mapList.get(0).get(GROUP_ID), resSet);
    }

    protected List<IEvent> matchEvent(Map<String, Object> mapData, Collection<IEvent> eList,
                                      ICalendar calendar) {
        // 存在风险事件信息时
        List<IEvent> resList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mapData)) {
            // 循环所有风险事件信息匹配特征值
            eList.forEach(event -> {
                // 匹配成功则将风险事件加入返回列表
                if (event.match(mapData, calendar)) {
                    event.setRelValue(mapData);
                    resList.add(event);
                }
            });
        }
        return resList;
    }

    protected boolean isSync(Map<String, Object> mapData) {
        return !CollectionUtils.isEmpty(mapData) && StringUtil.equals(mapData.get(IS_SYNC),
            BussinessDictCodeListRds.是否_是.getValue());
    }
    // add by zhangrui1 对于批量交易出现两笔同步交易时，排除影响的同一参数 start
    // add by zhangrui1 委托完成标识 start
    /**
     * 是否为委托完成
     * 
     * @param dataMap 报文
     * @return true : 委托完成
     */
    protected boolean isEtrCompleteY(Map<String, Object> dataMap) {
        return !CollectionUtils.isEmpty(dataMap) && StringUtil.equals(dataMap.get(ETRCOMPLETEFLG),
            BussinessDictCodeListRds.是否_是.getValue());
    }

    /**
     * 是否为委托完成撤销
     * 
     * @param dataMap 报文
     * @return true : 委托完成撤销
     */
    protected boolean isEtrCompleteN(Map<String, Object> dataMap) {
        return !CollectionUtils.isEmpty(dataMap) && StringUtil.equals(dataMap.get(ETRCOMPLETEFLG),
            BussinessDictCodeListRds.是否_否.getValue());
    }
    // add by zhangrui1 委托完成标识 end
}
