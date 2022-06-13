/*
 * 文件名：EventBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年8月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.riskinfrds.base.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.intf.IDictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.common.entity.IValue;
import com.joyintech.redis.common.entity.Value;
import com.joyintech.redis.common.util.JoyinRedisUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.common.base.entity.RskIsupTrial;
import com.joyintech.tams.common.base.entity.RskIsupTrialCls;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.calendar.entity.ICalendar;
import com.joyintech.tams.invest.common.riskinfrds.calendar.service.CalendarService;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEvent;
import com.joyintech.tams.invest.common.riskinfrds.event.entity.IEventParaRel;
import com.joyintech.tams.invest.common.riskinfrds.event.service.RskEventService;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicator;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicatorResult;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.common.riskinfrds.indic.service.IndicatorService;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdAssetAmount;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.FundValuationService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.ProdAssetAmountService;
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
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;

/**
 * 预占信息计算Service
 * 
 * @author zhangrui1
 * @version 1.0 2019年8月29日
 * @see CalcOccupyInfoService
 * @since 1.0
 */
@Service
public class CalcOccupyInfoService {

    /**
     * 控制台日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

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
     * 日历信息Redis读写服务
     */
    @Autowired
    private CalendarService calendarSrc;

    @Autowired
    private IndicatorTrendCalcService indTrendCalcSrc;

    /**
     * 资产实时规模信息
     */
    @Autowired
    private ProdAssetAmountService prodAssetAmtSrc;

    /**
     * 基金估值行情Redis读写Service
     */
    @Autowired
    private FundValuationService fundValSrc;

    /**
     * 本地缓存
     */
    private ThreadLocal<Map<String, Object>> localCache = ThreadLocal.withInitial(HashMap::new);

    public static final String SEQ_NO = "seqNo";
    /**
     * KEY:组合代码
     */
    public static final String PORTFOLIOCODE = "portfolioCode";

    /**
     * KEY:交易对手
     */
    public static final String COUNTERID = "counterid";
    /**
     * KEY:资产代码
     */
    public static final String ASSETCODE = "assetCode";

    /**
     * KEY:交易方向
     */
    public static final String PS = "ps";

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
     * KEY:监控时点
     */
    public static final String TIME_POINT = "timePoint";

    /**
     * KEY:资产代码
     */
    public static final String ASSET_CODE = "assetCode";

    /**
     * KEY:资产类型
     */
    public static final String ASSET_TYPE = "assetType";

    /**
     * KEY:上调系数
     */
    public static final String FACTOR = "factor";

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
     * KEY:计算表达式
     */
    public static final String KRIEXPR = "kriExpr";

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
     * KEY:试算结果列表
     */
    public static final String RSK_LIST = "RSK_LIST";

    /**
     * KEY:自动启动工作流系统
     */
    public static final String INF_STAPP_SYS = "infStappSys";

    public static final String MIXAMOUNTKEY = "mixAmount";

    public static final String TRADENUM = "tradeNum";

    public static final String AMOUNT = "amount";

    public static final String REVOKEFLG = "revokeFlg";

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
    
    public static final String LOG_NO_IND_VAL = "指标参数值不存在";

    protected static final String ASSET_AMT_VAL = "assetAmtVal";

    protected static final List<String> indReplaceList = Arrays.asList("CPZZC","CPJZCJZ");

    public void startTransaction() {
        cleanLocalCache();
    }

    public void commit() {
        cleanLocalCache();
    }

    protected boolean isNotEmptyNum(Object obj) {
        return StringUtil.isNotEmpty(obj)
               && BigDecimal.ZERO.compareTo(new BigDecimal(obj.toString())) != 0;
    }

    protected void calcAmount(Map<String, Object> request,
                              com.joyintech.tams.invest.common.CommonUtil.DoHelper<BigDecimal> consumer) {
        final String old = "Old";
        Object amountObj = request.get(AMOUNT);
        Object trdNumObj = request.get(TRADENUM);
        // 备份数据
        request.put(AMOUNT + old, amountObj);
        request.put(TRADENUM + old, trdNumObj);
        if (!isNotEmptyNum(amountObj) && !isNotEmptyNum(trdNumObj)) {
            return;
        }
        String assetCode = (String)request.get(ASSET_CODE);
        BigDecimal unitNet = consumer == null ? BigDecimal.ONE : consumer.handle();
        if (unitNet == null) {
            // 如果基金净值没有则按1计算
            logger.debug("没有取到{}的净值，按1计算", assetCode);
            unitNet = BigDecimal.ONE;
        }
        if (!isNotEmptyNum(amountObj)) {
            BigDecimal trdNum = trdNumObj instanceof BigDecimal ? (BigDecimal)trdNumObj : new BigDecimal(
                trdNumObj.toString());
            BigDecimal amount = trdNum.multiply(unitNet);
            request.put(AMOUNT, amount);
        }
        else if (!isNotEmptyNum(trdNumObj)) {
            BigDecimal amount = amountObj instanceof BigDecimal ? (BigDecimal)amountObj : new BigDecimal(
                amountObj.toString());
            BigDecimal trdNum = CommonUtil.math(amount, unitNet,
                BussinessDictCodeListRds.操作符_除.getValue());
            request.put(TRADENUM, trdNum);
        }
    }

    /**
     * 从redis取得净值
     * 
     * @param assetCode 资产代码
     * @return 净值
     */
    protected BigDecimal getUnitPrice(String assetCode) {
        return fundValSrc.getFundValuation(assetCode);
    }

    /**
     * 金额*上调系数
     * 
     * @param request 报文体
     */
    protected void factor(Map<String, Object> request) {
        Object amountObj = request.get(AMOUNT);
        // 资产大类
        Object assetType = request.get(ASSET_TYPE);
        // 交易方向
        Object ps = request.get(PS);
        // 事件类型
        Object eventType = request.get(EVENT_TYPE);
        StringBuilder builder = new StringBuilder(FACTOR);
        builder.append(GlobalConstant.UNDER_LINE).append(eventType).append(
            GlobalConstant.UNDER_LINE).append(assetType).append(GlobalConstant.UNDER_LINE).append(
                ps);
        String factor = CacheUtil.getSysParam(builder.toString());
        // 若系数不存在则不计算
        if (StringUtil.isEmpty(factor)) {
            return;
        }
        BigDecimal factorNum = new BigDecimal(factor);
        BigDecimal amount = amountObj instanceof BigDecimal ? (BigDecimal)amountObj : new BigDecimal(
            amountObj.toString());
        request.put(AMOUNT, amount.multiply(factorNum));
    }

    /**
     * 生成预占信息
     * 
     * @param preCode 预占编号
     * @param mapData 报文体
     * @return 预占信息
     */
    public OccupyRelParam doOccupy(String preCode, Map<String, Object> mapData) {
        // 取资产基本信息
        assetInfo(mapData);
        // 匹配风控事项
        List<IEvent> eveList = matchEvent(mapData);
        // 若风险事件不存在，则预占失败
        if (StringUtil.isEmptyList(eveList)) {
            return null;
        }
        // 取得基金估值行情
        calcAmount(mapData, () -> getUnitPrice((String)mapData.get(ASSET_CODE)));
        // 金额*上调系数
        factor(mapData);
        // 组合（包）列表
        Set<String> prodSet = getProdSet(mapData);
        // 从风险事件列表中取出相关的参数列表
        Set<String> paraIdList = getParamIdList(eveList);
        // 取得所有指标参数信息
        Map<String, IParam> paramMap = getParam(paraIdList);
        // 构建组合与指标参数索引
        Map<String, Map<String, IValue<BigDecimal>>> ptlParamMap = unionPtlParam(prodSet,
            paraIdList, mapData, paramMap);
        // 构建预占信息
        return createPreOcp(preCode, eveList, mapData, ptlParamMap);
    }

    /**
     * 释放预占
     * 
     * @param preOcpInfo 预占信息
     */
    public void releaseOccupy(OccupyRelParam preOcpInfo, Map<String, Object> mapData) {
        if (preOcpInfo == null || CollectionUtils.isEmpty(preOcpInfo.getItems())) {
            return;
        }
        Map<String, IOcpRelParamItem> items = preOcpInfo.getItems();
        // 撤销的场合，额度直接置0
        if (isRevoke(mapData)) {
            items.entrySet().forEach(en -> {
                IOcpRelParamItem item = en.getValue();
                // 额度直接置0
                item.setOccupyVal(BigDecimal.ZERO);
            });
        }
        // 部分释放的场合，减去报文中的额度
        else {
            items.entrySet().forEach(en -> {
                IOcpRelParamItem item = en.getValue();
                // 公式操作符
                String relOpr = item.getFormulaOpr();
                // 若非公式时则为关联操作符
                relOpr = StringUtil.isEmpty(relOpr) ? item.getOccupyItem() : relOpr;
                // 关联对象
                String relItem = item.getOccupyItem();
                // 占用额度
                BigDecimal val = item.getOccupyVal();
                // 报文中额度
                BigDecimal tmp = CommonUtil.getBigDecimal(mapData, relItem, BigDecimal.ZERO);
                tmp = tmp.negate();
                item.setOccupyVal(CommonUtil.math(val, tmp, relOpr));
            });
        }
    }

    /**
     * 判断预占是否为空
     * 
     * @param ocp 预占信息
     * @return true : 预占为空
     */
    protected boolean isEmpty(IOccupyRelParam ocp) {
        return ocp == null || CollectionUtils.isEmpty(ocp.getItems());
    }

    /**
     * 变更预占信息（减去实占部分）
     * 
     * @param preOcpInfo 预占信息
     * @param ocpInfo 实占信息
     */
    public void subtractOccupy(OccupyRelParam preOcpInfo, OccupyRelParam ocpInfo) {
        // 若预占信息为空，则结束处理
        if (isEmpty(preOcpInfo) || isEmpty(ocpInfo)) {
            return;
        }
        // 预占参数列表
        Map<String, IOcpRelParamItem> items = preOcpInfo.getItems();
        // 实占参数列表
        Map<String, IOcpRelParamItem> items2 = ocpInfo.getItems();
        // 遍历执行预占 - 实占
        items.entrySet().forEach(en -> {
            // 预占额度
            IOcpRelParamItem item = en.getValue();
            BigDecimal val = item.getOccupyVal();
            boolean isFormula = StringUtil.isNotEmpty(item.getFormulaOpr());
            // 实占额度
            IOcpRelParamItem item2 = items2.get(en.getKey());
            if (item2 == null){
                return;
            }
            BigDecimal val2 = item2.getOccupyVal();
            boolean isFormula2 = StringUtil.isNotEmpty(item2.getFormulaOpr());
            // 如果预占是公式实占不是时
            if (isFormula && !isFormula2) {
                val2 = reverseMath(val2, item.getFormulaOpr(), item.getFormulaVal());
            }
            // 如果预占不是公式时
            else if (!isFormula) {
                val2 = item2.doFormula();
            }
            // 预占 - 实占
            item.setOccupyVal(BigDecimal.ZERO.max(ArithmeticUtil.subtract(val, val2)));
        });
    }

    protected BigDecimal reverseMath(BigDecimal paraVal, String opr, BigDecimal val) {
        if (CommonUtil.isType(opr, BussinessDictCodeListRds.操作符_乘)) {
            return CommonUtil.math(paraVal, val, BussinessDictCodeListRds.操作符_除.getValue());
        }
        else if (CommonUtil.isType(opr, BussinessDictCodeListRds.操作符_除)) {
            return CommonUtil.math(paraVal, val, BussinessDictCodeListRds.操作符_乘.getValue());
        }
        else if (CommonUtil.isType(opr, BussinessDictCodeListRds.操作符_加)) {
            return CommonUtil.math(paraVal, val, BussinessDictCodeListRds.操作符_减.getValue());
        }
        else if (CommonUtil.isType(opr, BussinessDictCodeListRds.操作符_减)) {
            return CommonUtil.math(paraVal, val, BussinessDictCodeListRds.操作符_加.getValue());
        }
        return null;
    }

    /**
     * 是否撤销预占/实占
     * 
     * @param mapData 请求数据
     * @return true:是
     */
    protected boolean isRevoke(Map<String, Object> mapData) {
        String revokeFlg = (String)mapData.get(REVOKEFLG);
        return CommonUtil.isType(revokeFlg, BussinessDictCodeListRds.是否_是);
    }

    /**
     * 取占比金额
     * 
     * @param mapData 报文
     * @return 占比金额
     */
    protected BigDecimal getMixAmount(Map<String, Object> mapData) {
        // 组合代码
        String portfolioCode = (String)mapData.get(PORTFOLIOCODE);
        // 资产代码
        String assetCode = (String)mapData.get(ASSET_CODE);
        // 资产代码不存在，则返回金额
        if (StringUtil.isEmpty(assetCode)) {
            return CommonUtil.getBigDecimal(mapData, AMOUNT, BigDecimal.ZERO);
        }
        // 取出资产实时规模
        IProdAssetAmount info = getProdAssetAmount(portfolioCode, assetCode);
        // 计算占比金额
        return calcMixAmount(info, CommonUtil.getBigDecimal(mapData, TRADENUM, BigDecimal.ZERO));
    }

    /**
     * 计算占比金额【目标数量 * 原金额 / 原数量】
     * 
     * @param info 实时资产规模
     * @param newNum 目标数量
     * @return 目标金额
     */
    protected BigDecimal calcMixAmount(IProdAssetAmount info, BigDecimal newNum) {
        BigDecimal newAmt = ArithmeticUtil.multiply(2, RoundingMode.HALF_UP, newNum,
            info.getAmount());
        return CommonUtil.math(newAmt, info.getNum(), BussinessDictCodeListRds.操作符_除.getValue());
    }

    /**
     * 构建预占信息
     * 
     * @param preCode 预占编号
     * @param eveList 风控事项
     * @param mapData 报文体
     * @param paraValMap 参数项
     * @return 预占信息
     */
    protected OccupyRelParam createPreOcp(String preCode, List<IEvent> eveList,
                                          Map<String, Object> mapData,
                                          Map<String, Map<String, IValue<BigDecimal>>> paraValMap) {
        OccupyRelParam preOcpInfo = new OccupyRelParam();
        // 预占编号
        preOcpInfo.setOccupyCode(preCode);
        // 类型
        preOcpInfo.setOccupyType(PRE_OCCUPY);
        // 参数明细
        Map<String, IOcpRelParamItem> ocpPmItemMap = new HashMap<>();
        preOcpInfo.setItems(ocpPmItemMap);
        forEachEvent(eveList, epr -> {
            // 参数代码
            String paraCoce = epr.getParaCode();
            // 关系类型
            String relType = epr.getRelType();
            // 关系预算的对象值
            BigDecimal val = epr.getRelValue();
            // 占比金额
            if (StringUtil.equals(epr.getRelItem(), MIXAMOUNTKEY)
                || (epr.isFormula() && StringUtil.equals(epr.formulaRelItem(), MIXAMOUNTKEY))) {
                val = getMixAmount(mapData);
            }
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<BigDecimal>> valMap = paraValMap.get(paraCoce);
            // 判空
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            // redis识别域
            HashSet<String> hs = new HashSet<>();
            valMap.forEach((key, value) -> hs.add(key));
            OcpRelParamItem ocpPmItem = new OcpRelParamItem();
            // 预占/实占的对象
            ocpPmItem.setOccupyItem(epr.isFormula() ? epr.formulaRelItem() : epr.getRelItem());
            // 预占/实占操作符号
            ocpPmItem.setOccupyOper(relType);
            // 预占/实占金额
            ocpPmItem.setOccupyVal(val);
            // 指标参数编号
            ocpPmItem.setParamCode(paraCoce);
            // 参数值Redis存储Field
            ocpPmItem.setRedisFields(hs);
            // 预占额度系数
            ocpPmItem.setFormulaVal(epr.getFormulaParaVal());
            // 预占额度系数操作符
            ocpPmItem.setFormulaOpr(epr.formulaOprType());
            // 添加明细
            ocpPmItemMap.put(paraCoce, ocpPmItem);
        });
        return preOcpInfo;
    }

    /**
     * 关联的组合及所有相关的组合包
     * 
     * @param mapData 报文体
     * @return 组合（包）列表
     */
    protected Set<String> getProdSet(Map<String, Object> mapData) {
        // 组合代码
        String portfolioCode = (String)mapData.get(PORTFOLIOCODE);
        Set<String> prodSet = new HashSet<>();
        prodSet.add(portfolioCode);
        // 取得组合关联指标信息
        IPtlIndicator ptlInd = getPtlIndicator(mapData);
        if (ptlInd != null && !CollectionUtils.isEmpty(ptlInd.getPtlBagIdList())) {
            // 组合所在的组合包
            prodSet.addAll(ptlInd.getPtlBagIdList().keySet());
        }
        return prodSet;
    }

    /**
     * 返回<组合代码,<指标代码,redis识别域>>
     * 
     * @param prodSet 组合（包）代码列表
     * @param paraIdList 参数代码
     * @param mapData 报文体
     * @return <组合代码,<指标代码,redis识别域>>
     */
    protected Map<String, Map<String, IValue<BigDecimal>>> unionPtlParam(Set<String> prodSet,
                                                                         Set<String> paraIdList,
                                                                         Map<String, Object> mapData,
                                                                         Map<String, IParam> paramMap) {
        Map<String, Object> newMap = new HashMap<>();
        newMap.putAll(mapData);
        // 返回值
        Map<String, Map<String, IValue<BigDecimal>>> ptlParamMap = new HashMap<>();
        paraIdList.forEach(paraCode -> {
            IParam param = paramMap.get(paraCode);
            // Redis识别域
            String[] arr = param.getRedisFieldArr();
            // 枚举型、不更新Redis、非Redis、不是SQL的、redis识别域为空的统统跳过
            if (param.isEnumType() || !param.isUpRedis()
                || !CommonUtil.isType(param.getParamMathType(),
                    BussinessDictCodeListRds.指标参数计算方式_REDIS)
                || !CommonUtil.isType(param.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_SQL)
                || arr == null || arr.length == 0) {
                return;
            }
            // Map<指标参数代码,指标参数值Redis识别域>
            Map<String, IValue<BigDecimal>> pam = new HashMap<>();
            // 遍历组合关联指标列表
            prodSet.forEach(prod -> {
                newMap.put(PORTFOLIOCODE, prod);
                // redis识别域
                String field = getField(arr, newMap);
                pam.put(field, null);
            });
            ptlParamMap.put(paraCode, pam);
        });
        return ptlParamMap;
    }

    /**
     * 资产基本信息
     * 
     * @param mapData 报文
     */
    protected void assetInfo(Map<String, Object> mapData) {
        // 取得资产基本信息
        Map<String, Object> assetInfo = getAssetInfo(mapData);
        // 若资产基本信息不为空
        if (!CollectionUtils.isEmpty(assetInfo)) {
            mapData.putAll(assetInfo);
        }
    }

    /**
     * 匹配风控
     * 
     * @param mapData 报文体
     * @return 风控列表
     */
    protected List<IEvent> matchEvent(Map<String, Object> mapData) {
        // 取得日历信息
        ICalendar calendar = getCalendar((String)mapData.get(CALENDAR_ID));
        // 匹配风控事项
        return getEvent(mapData, calendar);
    }

    @SuppressWarnings("rawtypes")
    protected <T> T getFromLocalOrRedis(Map<String, Object> mapData, String key,
                                        Function<String, T> fun) {
        // 取得关键字
        Object obj = mapData.get(key);
        // 若不存在则返回空
        if (!StringUtil.isNotEmpty(obj)) {
            return null;
        }
        // 拼接索引
        String uri = key + GlobalConstant.COLON + obj.toString();
        // 从本地缓存数据
        T t = getInfo(uri);
        // 若本地缓存不存在从redis取
        if (t == null || (t instanceof Map && CollectionUtils.isEmpty((Map)t))
            || (t instanceof Collection && CollectionUtils.isEmpty((Collection)t))) {
            t = fun.apply(obj.toString());
            // 保存本地缓存
            putInfo(uri, t);
        }
        // 返回数据
        return t;
    }

    /**
     * 计算参数值
     * 
     * @param preOcpInfo 预占信息
     * @param paramVal 参数列表
     * @param details 计算明细
     * @return 计算后参数列表
     */
    protected Map<String, Map<String, IValue<Object>>> calcParamVal(OccupyRelParam preOcpInfo,
                                                                    Map<String, Map<String, IValue<Object>>> paramVal,
                                                                    List<RskIndicatorParaCalcdetail> details) {
        // 返回值 Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>>
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        // 备份
        newValMap.putAll(paramVal);
        // 遍历计算参数值
        ocpItemsForeach(preOcpInfo, item -> {
            // 参数代码
            String paraCoce = item.getParamCode();
            // 关系类型
            String relType = item.getOccupyOper();
            // 关系预算的对象值
            BigDecimal val = item.doFormula();
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<Object>> valMap = paramVal.get(paraCoce);
            if (CollectionUtils.isEmpty(valMap)) {
                return;
            }
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<Object>> temp = new HashMap<>();
            valMap.entrySet().forEach(en -> {
                // redis识别域
                String key = en.getKey();
                // 参数值
                IValue<Object> paraVal = en.getValue();
                // 为空时置0
                BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal)paraVal.value();
                // 新建目标值
                IValue<Object> newVal = new Value<>(
                    str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                // 指标参数值计算
                newVal.set(CommonUtil.math(par, val, relType));
                temp.put(key, newVal);
                // 计算明细
                details.add(createDetail(paraCoce, key, par, (BigDecimal)newVal.value(),
                    CommonUtil.math(BigDecimal.ZERO, val, relType)));
            });
            newValMap.put(paraCoce, temp);
        });
        // 记录未变更的参数值
        paramVal.entrySet().forEach(en -> {
            String paraCode = en.getKey();
            // 参数值
            Map<String, IValue<Object>> paraMap = en.getValue();
            if (containsPara(details, paraCode) || paraMap == null) {
                return;
            }
            paraMap.entrySet().forEach(pMap -> {
                String key = pMap.getKey();
                // 参数值
                IValue<Object> paraVal = pMap.getValue();
                BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal)paraVal.value();
                // 计算明细
                details.add(createDetail(paraCode, key, par, par, BigDecimal.ZERO));
            });
        });
        return newValMap;
    }

    /**
     * 遍历预占参数计算明细
     * 
     * @param preOcpInfo 预占信息
     * @param consumer 业务处理
     */
    protected void ocpItemsForeach(OccupyRelParam preOcpInfo,
                                   Consumer<IOcpRelParamItem> consumer) {
        if (consumer == null || isEmpty(preOcpInfo)) {
            return;
        }
        // 遍历计算参数值
        preOcpInfo.getItems().entrySet().forEach(item -> consumer.accept(item.getValue()));
    }

    /**
     * 取出所有变化的参数值
     * 
     * @param preOcpInfo 预占信息
     * @return 所有变化的参数值
     */
    protected Map<String, Map<String, IValue<Object>>> collectParam(OccupyRelParam preOcpInfo) {
        // 返回值 Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>>
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        // 遍历计算参数值
        ocpItemsForeach(preOcpInfo, item -> {
            // 参数代码
            String paraCoce = item.getParamCode();
            // 关系预算的对象值
            BigDecimal val = item.doFormula();
            // Map<指标参数值Redis识别域, 指标参数值>
            Map<String, IValue<Object>> temp = new HashMap<>();
            item.getRedisFields().forEach(key -> {
                // 新建目标值
                IValue<Object> newVal = new Value<>(
                    str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                // 指标参数值计算
                newVal.set(val);
                temp.put(key, newVal);
            });
            newValMap.put(paraCoce, temp);
        });
        return newValMap;
    }

    /**
     * 计算参数值，保存信息
     * 
     * @param preOcpInfo 预占信息
     * @param mapData 报文
     */
    protected Map<String, Object> justSave(OccupyRelParam preOcpInfo,
                                           Map<String, Object> mapData) {
        Map<String, Object> resMap = new HashMap<>();
        // 指标参数计算明细
        List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        final Map<String, Map<String, IValue<Object>>> paraList = collectParam(preOcpInfo);
        // 取得所有指标参数信息
        Map<String, IParam> paramMap = getParam(paraList.keySet());
        CommonUtil.handleByLock(() -> {
            String uuid = String.valueOf(System.currentTimeMillis());
            mapData.put(SEQ_NO, uuid);
            IndicatorParamValService service = getParamValSrv();
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = service.getParamVal(paraList);
            // 若指标参数值未初始化到Redis则抛出异常
            if (CollectionUtils.isEmpty(paraValMap)) {
                logger.error(LOG_NO_IND_VAL);
                return -1;
            }
            // 计算参数值
            Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(preOcpInfo,
                paraValMap, details);
            // 更新Redis
            service.setParamVal(newValMap);
            // 更新预占/实占信息
            setOccupyRelParam(preOcpInfo);
            return 0;
        });
        // 参数计算明细
        freshDetail(details, paramMap);
        saveDetails(details, mapData);
        resMap.put("PARA_VAL_DETAILS", details);
        return resMap;
    }

    /**
     * 是否计算指标
     * 
     * @param eventList 风险事件列表
     * @return true:计算指标,false:不计算指标
     */
    protected boolean isCalcIndicator(List<IEvent> eventList) {
        for (IEvent event : eventList) {
            if (CommonUtil.isType(event.getIsCalcIndicator(), BussinessDictCodeListRds.是否_否)) {
                logger.info("风控事项 {} 不计算指标", event.getEventName());
                return false;
            }
        }
        return true;
    }

    /**
     * 计算参数、指标并保存Redis
     * 
     * @param preOcpInfo 预占信息
     * @param mapData 报文
     * @return 指标计算结果
     */
    public Map<String, Object> calcAndSave(OccupyRelParam preOcpInfo,
                                           Map<String, Object> mapData) {
        mapData.put(PROD_ID, mapData.get(PORTFOLIOCODE));
        // 取得风控事项
        List<IEvent> eveList = matchEvent(mapData);
        if (isEmpty(preOcpInfo) || CollectionUtils.isEmpty(eveList)) {
            return new HashMap<>();
        }
        // 触发的风险事件不计算指标
        if (!isCalcIndicator(eveList)) {
            logger.info("触发的风控事项不计算指标");
            return justSave(preOcpInfo, mapData);
        }
        Map<String, Object> resMap = new HashMap<>();
        // 指标参数计算明细
        List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        String occupyCode = preOcpInfo.getOccupyCode();
        // 取得组合关联指标信息
        IPtlIndicator ptlInd = getPtlIndicator(mapData);
        // 取得所有指标代码
        Set<String> indIdList = getIndIdList(getPtlIndicator(mapData));
        // 若组合未关联指标信息，则试算通过，直接进行预占
        if (CollectionUtils.isEmpty(indIdList)) {
            logger.info(LOG_INFO_PASS);
            return justSave(preOcpInfo, mapData);
        }
        // 取得所有的指标信息
        Map<String, IIndicator> indMap = getIndicator(indIdList);
        // 若指标信息不存在，则抛出异常，不进行预占处理
        if (CollectionUtils.isEmpty(indMap)) {
            logger.info("指标信息不存在，试算通过");
            return justSave(preOcpInfo, mapData);
        }
        // 判断监督时点，若不满足则指标不计算,判断是否剔除资产，若满足则指标不计算
        checkTpAndIndivAd(indMap, mapData, ptlInd);
        // 当指标都被剔除时，试算通过，直接进行预占
        Map<String, Set<String>> ptlIndMap = ptlInd.getPtlIndicator();
        if (CollectionUtils.isEmpty(ptlIndMap) || CollectionUtils.isEmpty(ptlIndMap.values())) {
            logger.info("无指标需要计算，试算通过");
            return justSave(preOcpInfo, mapData);
        }
        // 从风险事件列表中取出相关的参数列表
        Set<String> paraIdList = getParamIdList(eveList);
        // 风险事件相关参数与指标相关参数糅合去重(并集)
        unionParamId(indMap, paraIdList, ptlInd.getPtlIndicator());
        // 一条指标都没有的时候
        if (CollectionUtils.isEmpty(indMap) || CollectionUtils.isEmpty(indMap.values())) {
            return justSave(preOcpInfo, mapData);
        }
        // 取得所有指标参数信息
        Map<String, IParam> paramMap = getParam(paraIdList);
        // 构建组合与指标参数索引
        Map<String, Map<String, String>> ptlParamMap = getPtlParamMap(ptlInd, indMap);
        RskIsupTrialCls isupTrialCls = new RskIsupTrialCls();
        // 业务类型
        isupTrialCls.setTradeType((String)mapData.get(EVENT_TYPE));
        // 业务编号
        isupTrialCls.setTradeId(occupyCode);
        Map<String, Object> response = new HashMap<>();
        // 执行事务处理（Redis操作）
        CommonUtil.handleByLock(() -> {
            String uuid = String.valueOf(System.currentTimeMillis());
            mapData.put(SEQ_NO, uuid);
            // 取得所有指标参数的值
            Map<String, Map<String, IValue<Object>>> paraValMap = getParamValSrv().getParamVal(
                createCondition(paramMap, mapData, ptlParamMap));
            // 计算参数值
            Map<String, Map<String, IValue<Object>>> newValMap = calcParamVal(preOcpInfo,
                paraValMap, details);
            // 指标信息存在
            if (!CollectionUtils.isEmpty(indMap)) {
                // 固定值参数
                Map<String, Object> map = getFixedParaVal(paramMap,mapData);
                Map<String, IIndicator> leftIndMap = getIndTrendCalcSrc().execute(indMap, paramMap,
                    getIndTrendCalcSrc().filterValMap(new HashMap<>(), paraValMap), eveList,
                    mapData, (newIndMap, indValMap, newIndValMap) -> {
                        IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd,
                            newIndMap);
                        // 指标参数原值
                        Map<String, Map<String, Object>> pValMap = getIndTrendCalcSrc().parseParaVal(
                            ptlParamMap, indValMap);
                        // 加入固定值
                        unionParaVal(pValMap, map);
                        // 指标计算原值
                        calcIndicator(newPtlInd, newIndMap, mapData, pValMap, true);
                        // 指标参数新值
                        pValMap = getIndTrendCalcSrc().parseParaVal(ptlParamMap, newIndValMap);
                        // 加入固定值
                        unionParaVal(pValMap, map);
                        // 指标计算原值
                        calcIndicator(newPtlInd, newIndMap, mapData, pValMap, false);
                    });
                if (!CollectionUtils.isEmpty(leftIndMap)) {
                    IPtlIndicator newPtlInd = getIndTrendCalcSrc().newPtlInd(ptlInd, leftIndMap);
                    // 指标参数原值
                    Map<String, Map<String, Object>> pValMap = parseParaVal(ptlParamMap,
                        paraValMap);
                    // 加入固定值
                    unionParaVal(pValMap, map);
                    // 指标计算原值
                    calcIndicator(newPtlInd, indMap, mapData, pValMap, true);
                    // 指标参数新值
                    pValMap = parseParaVal(ptlParamMap,
                        calcValWithoutEvt(getParamIdList(eveList), eveList, newValMap));
                    // 加入固定值
                    unionParaVal(pValMap, map);
                    // 指标计算新值
                    calcIndicator(newPtlInd, indMap, mapData, pValMap, false);
                }
                // 判断指标趋势
                indMap.entrySet().forEach(en -> checkIndTrend(en.getValue()));
                // 设置指标趋势到本地缓存
                getIndTrendCalcSrc().setTrend(indMap, mapData,
                    occupyCode + GlobalConstant.COLON + PRE_OCCUPY);
                // 处理返回结果
                writeResponse(response, indMap);
            }
            // 更新Redis
            getParamValSrv().setParamVal(newValMap);
            // 更新预占/实占信息
            setOccupyRelParam(preOcpInfo);
            // 更新指标趋势信息
            getIndTrendCalcSrc().saveIndTrendList();
            return 0;
        });
        String rskStatus = (String)response.get(RSKSTATUS);
        // 试算结论
        isupTrialCls.setConclusion(rskStatus);
        // 流水号
        isupTrialCls.setSeqNo((String)mapData.get("uuid"));
        // 保存风险计算结论
        setUserDt(isupTrialCls);
        resMap.put("ISUP_TRIAL_CLS", isupTrialCls);
        // 保存监督指标计算结果
        saveRskIsupTrial(response, mapData, occupyCode, resMap);
        // 参数计算明细
        freshDetail(details, paramMap);
        saveDetails(details, mapData);
        resMap.put("PARA_VAL_DETAILS", details);
        return resMap;
    }

    /**
     * 清除缓存内容
     *
     */
    protected void cleanLocalCache() {
        localCache.remove();
    }

    /**
     * 设置缓存内容
     * 
     * @param map 内容
     */
    protected void setLocalCache(Map<String, Object> map) {
        localCache.set(map);
    }

    /**
     * 取得缓存内容
     * 
     * @return 缓存内容
     */
    protected Map<String, Object> getLocalCache() {
        return localCache.get();
    }

    /**
     * 取得缓存内容
     * 
     * @param key 索引
     * @return 缓存内容
     */
    @SuppressWarnings("unchecked")
    protected <T> T getInfo(String key) {
        return (T)getLocalCache().get(key);
    }

    /**
     * 设置缓存内容
     * 
     * @param key 索引
     * @param obj 内容
     */
    protected void putInfo(String key, Object obj) {
        getLocalCache().put(key, obj);
    }

    /**
     * 保存 指标参数计算明细
     * 
     * @param details 参数计算明细
     * @param mapData 报文
     */
    protected void saveDetails(List<RskIndicatorParaCalcdetail> details,
                               Map<String, Object> mapData) {
        if (!StringUtil.isEmptyList(details)) {
            details.forEach(detail -> {
                // 业务编号
                detail.setTradeId((String)mapData.get(EVENT_ID));
                // 业务类型
                detail.setTradeType(String.valueOf(0));
                // 设置监督日期
                detail.setSupDate(DateUtil.parse((String)mapData.get("sysDate")));
                // 设置时间戳
                Object obj = mapData.get(SEQ_NO);
                detail.setDotimestamp(DateUtil.format(new Date(new Long(obj.toString())),
                    DateUtil.DEFAULT_DATETIME_FORMAT_SSS));
                // 设置UUID
                detail.setUuid(IDGenerator.generate());
                String user = "batch";
                // 创建人
                detail.setCreateUser(user);
                // 更新人
                detail.setUpdateUser(user);
            });
        }
    }

    /**
     * 固定值参数加入指标计算的参数列表
     * 
     * @param paraVal 指标计算的参数列表
     * @param map 固定值参数
     */
    protected void unionParaVal(Map<String, Map<String, Object>> paraVal,
                                Map<String, Object> map) {
        if (!CollectionUtils.isEmpty(map)) {
            paraVal.entrySet().forEach(en -> {
                Map<String, Object> value = en.getValue();
                value.putAll(map);
            });
        }
    }

    /**
     * 固定值参数值
     * 
     * @param paramMap 指标参数
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

    protected String makeParaCode(String code, IDictCodeList gara) {
        return code + GlobalConstant.COLON + gara.getValue();
    }

    /**
     * 过滤参数该粒度没有被影响，但需要参与计算且其它粒度参数
     * 
     * @param paraIdList 被影响的参数
     * @param paraValMap 参数值列表
     * @return 过滤结果
     */
    protected Map<String, Map<String, IValue<Object>>> filter(Set<String> paraIdList,
                                                              Map<String, Map<String, IValue<Object>>> paraValMap) {
        Map<String, Map<String, IValue<Object>>> tempMap = new HashMap<>();
        paraValMap.entrySet().forEach(en -> {
            // 参数代码
            String paraCode = en.getKey();
            // 参数该粒度没有被影响，但需要参与计算且其它粒度被影响时
            String pCd = StringUtil.substring(paraCode, 0,
                StringUtil.indexOf(paraCode, GlobalConstant.COLON));
            if (!paraIdList.contains(paraCode)
                && (paraIdList.contains(makeParaCode(pCd, BussinessDictCodeListRds.参数粒度_买入))
                    || paraIdList.contains(makeParaCode(pCd, BussinessDictCodeListRds.参数粒度_卖出))
                    || paraIdList.contains(makeParaCode(pCd, BussinessDictCodeListRds.参数粒度_实时)))) {
                tempMap.put(paraCode, en.getValue());
            }
        });
        return tempMap;
    }

    /**
     * 计算 需要参与计算却没有受到风险事件影响的参数
     * @param paraIdList 被影响的参数
     * @param eveList 风险事件
     * @param paraValMap 参数值列表
     */
    protected Map<String, Map<String, IValue<Object>>> calcValWithoutEvt(Set<String> paraIdList, List<IEvent> eveList, Map<String, Map<String, IValue<Object>>> paraValMap){
        Map<String, Map<String, IValue<Object>>> tempMap = filter(paraIdList, paraValMap);
        if (CollectionUtils.isEmpty(tempMap)) {
            return paraValMap;
        }
        Map<String, Map<String, IValue<Object>>> newValMap = new HashMap<>();
        newValMap.putAll(paraValMap);
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
                // Map<指标参数值Redis识别域, 指标参数值>
                Map<String, IValue<Object>> valMap = en.getValue();
                if (!StringUtil.equals(pCd2, pCd) || CollectionUtils.isEmpty(valMap)) {
                    return;
                }
                // Map<指标参数值Redis识别域, 指标参数值>
                Map<String, IValue<Object>> temp = new HashMap<>();
                valMap.entrySet().forEach(item -> {
                    String key = item.getKey();
                    // 参数值
                    IValue<Object> paraVal = item.getValue();
                    BigDecimal par = paraVal == null ? BigDecimal.ZERO : (BigDecimal) paraVal.value();
                    IValue<Object> newVal = new Value<>(
                        str -> StringUtil.isEmpty(str) ? BigDecimal.ZERO : new BigDecimal(str));
                    // 指标参数值计算
                    newVal.set(CommonUtil.math(par, epr.doFormula(val), relType));
                    temp.put(key, newVal);
                });
                newValMap.put(tempCode, temp);
            });
        });
        return newValMap;
    }

    /**
     * 取得 事件的时点
     * 
     * @param mapData 输入报文体
     * @return 事件的时点
     * @see
     * @since 1.0
     */
    protected String getTimePoint(Map<String, Object> mapData) {
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
    protected String getTimePoint(String eventType) {
        if (StringUtil.equals(eventType, "0")) {
            return BussinessDictCodeListRds.监督时分_委托监控.getValue();
        }
        else if (StringUtil.equals(eventType, "1")) {
            return BussinessDictCodeListRds.监督时分_交易监控.getValue();
        }
        else if (StringUtil.equals(eventType, "2")) {
            return BussinessDictCodeListRds.监督时分_交易监控.getValue();
        }
        return eventType;
    }

    /**
     * 判断监督时点，若不满足则指标不计算,判断是否剔除资产，若满足则指标不计算
     * 
     * @param indMap 指标列表
     * @param mapData 接口输入报文
     * @see
     * @since 1.0
     */
    protected void checkTpAndIndivAd(Map<String, IIndicator> indMap, Map<String, Object> mapData,
                                     IPtlIndicator ptlInd) {
        // 事件时点
        String timePoint = getTimePoint(mapData);
        // 资产代码
        String assetCode = (String)mapData.get(ASSET_CODE);
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

    /**
     * 校验监督时点
     * 
     * @param key 指标代码
     * @param ind 指标信息
     * @param indList 指标列表
     * @param timePoint 监督时点
     */
    protected void checkTimePoint(String key, IIndicator ind, Set<String> indList,
                                  String timePoint) {
        // 指标监督时点
        String tp = ind.getTimePoint();
        // 当指标监督时点不符合事件时点时，该指标不参与计算
        if (!StringUtil.contains(tp, timePoint)) {
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
     */
    protected void checkIndividAstDel(IIndicator ind, Set<String> indList, String ptlCode,
                                      String assetCode) {
        // 取得剔除资产代码信息
        Set<String> tpList = ind.getPtlIndividAstDel(ptlCode);
        // 若目标资产在剔除的列表中，则删除该组合与指标的关系
        if (tpList.contains(assetCode)) {
            indList.remove(ind.getIndId());
        }
    }

    /**
     * 取得预占/实占信息
     * 
     * @param ocpCode 接口输入报文体
     * @param type 预占/实占类型
     * @return 预占/实占信息
     */
    protected IOccupyRelParam getOccupy(String ocpCode, int type) {
        // 预占实占参数信息
        IOccupyRelParam ocpRelParam = null;
        if (!StringUtil.isEmpty(ocpCode)) {
            // 取得预占/实占信息
            ocpRelParam = getOccupyRelParam(ocpCode, type);
        }
        return ocpRelParam;
    }

    /**
     * 判断指标趋势
     * 
     * @param ind 指标列表
     */
    protected void checkIndTrend(IIndicator ind) {
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
     */
    protected Map<String, Map<String, String>> getPtlParamMap(IPtlIndicator ptlInd,
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
     * @param mapData 报文
     * @param pValMap 指标参数值 <b>Map<指标参数代码, Map<指标参数值Redis识别域, 指标参数值>></b>
     * @param isOld 是否原指标值
     */
    protected void calcIndicator(IPtlIndicator ptlInd, Map<String, IIndicator> indMap,
                                 Map<String, Object> mapData,
                                 Map<String, Map<String, Object>> pValMap, boolean isOld) {
        calcService.calcIndicator(ptlInd, indMap, mapData, pValMap, isOld);
    }

    protected Map<String, Map<String, Object>> parseParaVal(Map<String, Map<String, String>> ptlParamMap,
                                                            Map<String, Map<String, IValue<Object>>> valMap) {
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
                pMap.put(key, bVal == null ? BigDecimal.ZERO : bVal.value());
            });
            pValMap.put(ptlCode, pMap);
        });
        return pValMap;
    }

    /**
     * 取得 风险事件
     * 
     * @param mapData 委托/交易信息
     * @param calendar 日历信息
     * @return 风险事件列表
     */
    protected List<IEvent> getEvent(Map<String, Object> mapData, ICalendar calendar) {
        return getFromLocalOrRedis(mapData, EVENT_ID,
            eventId -> rskEventSrv.matchEvent(mapData, calendar));
    }

    /**
     * 取得 组合关联指标信息
     * 
     * @param mapData 委托/交易信息
     * @return 组合关联指标信息
     */
    protected IPtlIndicator getPtlIndicator(Map<String, Object> mapData) {
        return getFromLocalOrRedis(mapData, PORTFOLIOCODE,
            ptlCode -> ptlIndService.getPtlIndicator(ptlCode));
    }

    /**
     * 取得 组合关联的所有指标代码
     * 
     * @param ptlIndicator 组合关联指标信息
     * @return 组合关联的所有指标代码
     */
    protected Set<String> getIndIdList(IPtlIndicator ptlIndicator) {
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
     */
    protected Map<String, IIndicator> getIndicator(Set<String> indIdList) {
        return indService.getIndicatorMap(indIdList);
    }

    /**
     * 从风险事件列表中取出相关的参数列表
     * 
     * @param eveList 风险事件列表
     * @return 参数列表
     */
    protected Set<String> getParamIdList(List<IEvent> eveList) {
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
     */
    protected void unionParamId(Map<String, IIndicator> indMap, Set<String> paraIdList,
                                Map<String, Set<String>> ptlIndMap) {
        Set<String> indCodeSet = new HashSet<>();
        Set<String> temp = new HashSet<>();
        for (IIndicator ind : indMap.values()) {
            if (StringUtil.isNotEmpty(ind)) {
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
     */
    protected Map<String, IParam> getParam(Set<String> paraIdList) {
        return indParamSrv.getParamList(paraIdList);
    }

    /**
     * 取得 预占/实占参数信息
     * 
     * @param ocpCode 预占/实占代码
     * @param type 预占/实占类型
     * @return 预占/实占参数信息
     */
    protected IOccupyRelParam getOccupyRelParam(String ocpCode, int type) {
        return ocpService.getOccupyRelParamMap(ocpCode, type);
    }

    /**
     * 保存 预占/实占参数信息
     * 
     * @param ocpRelParam 预占/实占代码
     * @return 预占/实占参数信息
     */
    protected void setOccupyRelParam(IOccupyRelParam ocpRelParam) {
        ocpService.saveToRedis(ocpRelParam);
    }

    /**
     * 取得 日历信息
     * 
     * @param calendarId 日历编号
     * @return 日历信息
     */
    protected ICalendar getCalendar(String calendarId) {
        return calendarSrc.getCalendar(calendarId);
    }

    /**
     * 遍历风险事件相关的指标参数
     * 
     * @param eveList 风险事件 <b>List<{@link IEvent}></b>
     * @param var1 处理逻辑
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
     */
    protected Map<String, Map<String, IValue<Object>>> createCondition(Map<String, IParam> paramMap,
                                                                       Map<String, Object> mapData,
                                                                       Map<String, Map<String, String>> ptlParamMap) {
        // 查询条件列表
        Map<String, Map<String, IValue<Object>>> paraValList = new HashMap<>();
        // 指标参数信息
        paramMap.entrySet().forEach(pEn -> {
            IParam para = paramMap.get(pEn.getKey());
            String pCode = pEn.getKey();
            if (para != null
                && para.isUpRedis()
                && CommonUtil.isType(para.getParamMathType(),
                    BussinessDictCodeListRds.指标参数计算方式_REDIS)
                && CommonUtil.isType(para.getParaDisplayType(),
                    BussinessDictCodeListRds.指标参数实现类型_SQL)
                && !para.isEnumType() && para.getRedisFieldArr() != null
                && para.getRedisFieldArr().length > 0) {
                String field = getField(para.getRedisFieldArr(), mapData);
                // 指标参数值Redis识别域
                Map<String, IValue<Object>> map = paraValList.getOrDefault(pEn.getKey(),
                    new HashMap<>());
                map.put(field, null);
                paraValList.put(pCode, map);
            }
        });
        // 遍历组合相关的指标参数
        ptlParamMap.entrySet().forEach(en -> {
            // 组合代码
            String ptlCode = en.getKey();
            // Map<指标参数代码, 指标参数值Redis识别域>
            Map<String, String> paramList = en.getValue();
            mapData.put(PORTFOLIOCODE, ptlCode);
            paramList.entrySet().forEach(pEn -> {
                // 指标参数代码
                String pCode = pEn.getKey();
                // 指标参数信息
                IParam para = paramMap.get(pCode);
                if (para != null && para.isUpRedis()
                    && CommonUtil.isType(para.getParamMathType(),
                        BussinessDictCodeListRds.指标参数计算方式_REDIS)
                    && CommonUtil.isType(para.getParaDisplayType(),
                        BussinessDictCodeListRds.指标参数实现类型_SQL)
                    && !para.isEnumType()
                    && para.getRedisFieldArr() != null && para.getRedisFieldArr().length > 0) {
                    // 指标参数值Redis识别域
                    String field = getField(para.getRedisFieldArr(), mapData);
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
        // 设置产品代码
        detail.setPortfolioId(keyArr != null && keyArr.length > 0 ? keyArr[0] : "");
        // 设置资产代码
        detail.setAssetCode(keyArr != null && keyArr.length > 1 ? keyArr[1] : "");
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
        for (RskIndicatorParaCalcdetail detail : details) {
            if (detail != null && StringUtil.equals(paraCode,
                detail.getParaCode() + GlobalConstant.COLON + detail.getParaGranularity())) {
                return true;
            }
        }
        return false;
    }

    protected String getField(String[] redisFld, Map<String, Object> mapData) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < redisFld.length; i++ ) {
            Object val = mapData.getOrDefault(redisFld[i], StringUtil.EMPTY);
            builder.append(GlobalConstant.COMMA).append(val);
        }
        return builder.length() > 1 ? builder.substring(1) : StringUtil.EMPTY;
    }

    @SuppressWarnings("unchecked")
    protected void saveRskIsupTrial(Map<String, Object> response, Map<String, Object> mapData,
                                    String occupyCode, Map<String, Object> resMap) {
        Object obj = response.get(RSK_LIST);
        List<RskIsupTrial> sendList = new ArrayList<>();
        if (obj != null) {
            List<Map<String, Object>> riskList = (List<Map<String, Object>>)obj;
            Object obj2 =  mapData.get("eventDate");
            Date dt = obj2 instanceof Date ? (Date) obj2 : DateUtil.parse(obj2.toString());
            riskList.forEach(map -> {
                RskIsupTrial info = new RskIsupTrial();
                info.setUuid(IDGenerator.generate());
                // 业务编号
                info.setTradeId(occupyCode);
                // 组合代码
                info.setPortfolioId(getStrFromMap(map, PORTFOLIOCODE));
                // 监督指标编号
                info.setIndId(getStrFromMap(map, KRICODE));
                // 监督日期
                info.setSupDate(dt);
                // 监督值
                info.setSupVal(getStrFromMap(map, KRIVALUE));
                // 原值
                info.setOldSupVal(getStrFromMap(map, OLDVALUE));
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
                // 流水号
                info.setSeqNo((String)mapData.get("uuid"));
                // 资产代码
                info.setAssetCode((String)mapData.get(ASSET_CODE));
                // 设置创建人时间等
                setUserDt(info);
                sendList.add(info);
            });
        }
        resMap.put("ISUP_TRIAL", sendList);
    }

    private String getStrFromMap(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        return obj == null ? StringUtil.EMPTY : String.valueOf(obj);
    }

    /**
     * 将试算的结果写入返回的报文中
     * 
     * @param response 返回的报文
     * @param resMap 试算的结果
     * @see
     * @since 1.0
     */
    protected void writeResponse(Map<String, Object> response, Map<String, IIndicator> resMap) {
        response.put(RSKSTATUS, BussinessDictCodeListRds.指标状态_通过.getValue());
        if (CollectionUtils.isEmpty(resMap)) {
            return;
        }
        List<Map<String, Object>> rskList = new ArrayList<>();
        resMap.entrySet().forEach(en -> {
            IIndicator ind = en.getValue();
            Map<String, IIndicatorResult> uniteMap = ind.getIndicatorResult();
            if (CollectionUtils.isEmpty(uniteMap)) {
                return;
            }
            uniteMap.entrySet().forEach(resEn -> {
                String ptlCode = resEn.getKey();
                IIndicatorResult res = resEn.getValue();
                rskList.add(getResMap(response, ptlCode, ind, res, resMap));
            });
        });
        response.put(RSK_LIST, rskList);
    }

    protected Map<String, Object> getResMap(Map<String, Object> response, String ptlCode,
                                            IIndicator ind, IIndicatorResult res,
                                            Map<String, IIndicator> resMap) {
        Map<String, Object> temp = new HashMap<>();
        // 产品代码
        temp.put(PORTFOLIOCODE, ptlCode);
        // 指标名称
        temp.put(KRICODE, ind.getIndId());
        // 指标名称
        temp.put(KRINAME, ind.getIndName());
        // 指标类型
        String indCellType = ind.getIndType();
        temp.put(KRITYPE, indCellType);
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
        // 计算表达式
        temp.put(KRIEXPR, res.getCalcExpr());
        // 指标计算值
        temp.put(KRIVALUE, CommonUtil.convertKriVal(res.getIndicatorVal(), ind));
        // 指标原值
        temp.put(OLDVALUE, CommonUtil.convertKriVal(res.getIndicatorOldVal(), ind));
        // 趋势
        temp.put(KRITREND, trendStr(res.getIndicatorTrend()));
        Set<String> uniteList = ind.getPtlIndividUnite(ptlCode);
        String kriStatus = res.getIndStatus();
        String uSts = kriStatus;
        // 指标状态
        temp.put(KRISTATUS, kriStatus);
        // 是否有联合指标设置
        boolean hasUnite = !CollectionUtils.isEmpty(uniteList);
        // 当存在某个指标状态超过整体状态且趋势向坏的指标时试算不通过
        String rskStatus = (String)response.get(RSKSTATUS);
        // 有联合指标时
        if (hasUnite) {
            uSts = checkStatus(kriStatus, uniteList, ptlCode, resMap);
            res.setUniteStatus(uSts);
            temp.put(UNITESTATUS, uSts);
        }
        if (StringUtil.isNotEmpty(uSts) && uSts.compareTo(rskStatus) > 0
            && res.getIndicatorTrend() < 0) {
            response.put(RSKSTATUS, uSts);
        }
        return temp;
    }

    /**
     * 趋势
     * 
     * @param inTrend 趋势
     * @return 趋势
     */
    private String trendStr(int inTrend) {
        if (inTrend == 0) {
            return "不变";
        }
        else {
            return inTrend > 0 ? "向好" : "向坏";
        }
    }

    /**
     * 阈值条件解析
     * 
     * @param indCellType 指标类型
     * @param fun 阈值条件取得接口
     * @return 解析结果
     */
    private String changeCond(String indCellType, Limit fun) {
        try {
            return RiskUtil.transferWarnFoul(fun.limit(), indCellType);
        }
        catch (Exception e) {
            logger.error("阈值条件解析异常", e);
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
     */
    protected String checkStatus(String rskStatus, Set<String> indList, String ptlCode,
                                 Map<String, IIndicator> resMap) {
        String min = rskStatus;
        List<IIndicatorResult> resList = new ArrayList<>();
        for (String str : indList) {
            IIndicator ind = resMap.get(str);
            IIndicatorResult res = ind.getPtlIndicatorResult(ptlCode);
            String temp = res.getIndStatus();
            if (StringUtil.isEmpty(temp)) {
                continue;
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
     * 取得实时资产规模信息
     * 
     * @param portfolioCode 组合代码
     * @param assetCode 资产代码
     * @return 实时资产规模信息
     */
    protected IProdAssetAmount getProdAssetAmount(String portfolioCode, String assetCode) {
        return prodAssetAmtSrc.getProdAssetAmount(portfolioCode, assetCode);
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
     * 取得 资产基本信息
     * 
     * @param mapData 报文
     * @return 资产基本信息
     */
    protected Map<String, Object> getAssetInfo(Map<String, Object> mapData) {
        return getFromLocalOrRedis(mapData, ASSET_CODE,
            assetCode -> assetInfoSrc.getAssetInfoAsMap(assetCode));
    }

    /**
     * 设置创建人时间等
     * 
     * @param info 数据
     */
    protected void setUserDt(BaseEntity info) {
        String user = "system";
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

    public IndicatorTrendCalcService getIndTrendCalcSrc() {
        return indTrendCalcSrc;
    }
}
