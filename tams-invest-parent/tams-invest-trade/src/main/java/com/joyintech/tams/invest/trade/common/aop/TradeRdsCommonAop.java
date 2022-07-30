/*
 * 文件名：TradeRdsCommonAop.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.common.aop;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.constant.PageFieldToInterfFactory;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.risktrialrds.RiskTrialComm;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt1.金融产品类型_投管用_债券质押式正回购;
import static com.joyintech.tams.constant.DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购;

/**
 * 交易撤销时调用风险逻辑aop
 *
 * @author 裴家俊
 * @version 1.0 2020年1月7日
 * @see TradeRdsCommonAop
 * @since 1.0
 */
@Component
@Aspect
public class TradeRdsCommonAop {
    
    // add by zhangrui1 20200509 批量接口逻辑 start
    public static final List<String> 批量接口相关交易方向 = Arrays.asList(DictCodeListExt1.交易方向_债转股.getValue(),
            DictCodeListExt2.交易方向_转换申请.getValue(),DictCodeListExt2.交易方向_转换赎回申请.getValue());
    // add by zhangrui1 20200509 批量接口逻辑 end
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(TradeRdsCommonAop.class);

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    @Autowired
    private RiskTrialComm riskTrialComm;
    
 // add by etliu 20210129 start
    /**
     * TradeCommonService
     */
    @Autowired
    private TradeCommonService tradeCommonService;
 // add by etliu 20210129 end

    // add by zhangrui1 start
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;


    /**
     * 线程缓存
     */
    private static ThreadLocal<List<? extends Object>> localCache = ThreadLocal.withInitial(ArrayList::new);

    /**
     * 设置目标数据到线程缓存
     * 
     * @param list 目标数据
     */
    public static final <V extends Object> void setLocalCache(List<V> list) {
        localCache.set(list);
    }

    /**
     * 设置目标数据到线程缓存
     * 
     * @param list 目标数据
     */
    @SuppressWarnings("unchecked")
	public static final <V extends Object> void setLocalCache(V... list) {
        localCache.set(Arrays.asList(list));
    }

    private static final List<? extends Object> getTarget() {
        return localCache.get();
    }

    private static final void cleanLocalCache() {
        localCache.remove();
    }
    // add by zhangrui1 end

    // add by zhangrui1 批量调用接口 start
    /**
     * 取得接口组件Service
     * 
     * @return 接口组件Service
     */
    @SuppressWarnings("unchecked")
    public static InterfRiskRdsService<InterfRiskRdsDto> getInterfRiskRdsService() {
        return SpringContextUtil.getApplicationContext().getBean(InterfRiskRdsService.class);
    }

    /**
     * 批量接口调用
     * 
     * @param dtoList 批量数据
     * @param busiType 业务类型
     * @param isSingleCounter 是否同一个交易对手
     * @param etrInvestPersonInfo 投资人员信息
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public static final <T> AjaxResult doListOccupy(List<T> dtoList, String busiType,
                                                    boolean isSingleCounter,
                                                    TrdInvestPersonInfo002Dto etrInvestPersonInfo)
        throws Exception {
        if (!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"),
            BussinessDictCodeListRds.是否_是)) {
            return null;
        }
        // 接口组件Service
        InterfRiskRdsService<InterfRiskRdsDto> service = getInterfRiskRdsService();
        // 执行预占
        AjaxResult ar = service.checkRiskRds(new InterfRiskRdsDto(),
            BussinessDictCodeListRds.事件类型_投资交易.getValue(),
            BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue(), busiType,
            en -> service.sendRiskRdsListData(en, dtoList, map -> {
                // 个别处理
                // 交易对手
                if (isSingleCounter && etrInvestPersonInfo != null) {
                    map.put(PageFieldToInterfFactory.INF_COUNTERID,
                        etrInvestPersonInfo.getCounterId());
                }
            }));
        // 判断结果，若正常则缓存数据，后面执行异常时使用缓存数据进行预占释放
        if (ar == null) {
            setLocalCache(dtoList);
        }
        return ar;
    }
    // add by zhangrui1 批量调用接口 end

    /**
     * 忽略service
     * modify by jiangpeng on 20210511 ZGBSSE-351
     * 【标准券管理】标准券入库交易审核报错,标准券出库交易审核报错
     */
    private List<String> ignoreList=Arrays.asList("ChannelFeePay002Service","TrdStandardBond002Service","TrdStandardBond003Service","NetPlanTransfer001Service");
    /*@Pointcut("execution(* com.joyintech.workflow.engine.service.ProcessStartService.start(..))")
    protected void startPoint() {

    }*/

    /**
     * 退回时在待办中再提交
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doUpdate(..))")
    protected void updatePoint() {

    }

    /**
     * 撤销操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doComplete(..))")
    protected void revokePoint() {

    }

    /**
     * 退回操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doBack(..))")
    protected void backPoint() {
    }

    @AfterReturning("updatePoint()")
    public void updateTradeMethod(JoinPoint joinPoint) throws BusinessException {
        if(!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)||!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        Map<String,Object> mapTrd=JsonUtils.object2Map(args[0]);
        if (StringUtil.isNotEmpty(mapTrd.get("operateType"))&&mapTrd.get("operateType").equals("submit")) {
            //如果是撤销的退回再提交直接退去，不需要调实占
            if(!checkRevokeAction(mapTrd.get("workflowId"))){
                return;
            }
            
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            if (mapTrd.get("fTradeId") != null) {
                String fTradeId = (String) mapTrd.get("fTradeId");
                if (StringUtil.isNotBlank(fTradeId) && StringUtil.equals(DictCodeList.是否_是.getValue(),
                    tradeCommonService.checkExecIdHasQuote(fTradeId))) {
                    return;
                }
            }
            // add by etliu 20210129 end
            //信用保护凭证买入、信用保护凭证卖出的交收失败风险不在这里处理
            if(StringUtil.equals(mapTrd.get("pageModel"), DictCodeListExt3.交收操作类型_交收失败.getValue())
                    &&(StringUtil.equals(mapTrd.get("busiType"),"27") || StringUtil.equals(mapTrd.get("busiType"),"28"))){
                return;
            }
            // modify by zhangrui1 20200509 批量接口逻辑 start
//            revokeBackRiskCommon(mapTrd,BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),joinPoint);
            String infCode = BussinessDictCodeListRds.接口系统ID_实占接口.getValue();
            if (批量接口相关交易方向.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
                logger.info("调用批量处理接口{}", infCode);
                infCode = BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue();
            }
            revokeBackRiskCommon(mapTrd, infCode, joinPoint);
            // modify by zhangrui1 20200509 批量接口逻辑 end
        }
    }

    @AfterReturning("revokePoint()")
    public void revokeTradeMethod(JoinPoint joinPoint) throws Exception {
        if(!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)||!StringUtil.isNotEmpty(joinPoint.getArgs())||joinPoint.getArgs().length<=1){
            return;
        }
        Object[] args = joinPoint.getArgs();
        OperationType operationType = (OperationType) args[1];
        Map<String,Object> mapTrd=JsonUtils.object2Map(args[0]);
        if (operationType.equals(OperationType.REVOKE)||operationType.equals(OperationType.DELETE)) {
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            if (mapTrd.get("fTradeId") != null) {
                String fTradeId = (String) mapTrd.get("fTradeId");
                if (StringUtil.isNotBlank(fTradeId) && StringUtil.equals(DictCodeList.是否_是.getValue(),
                    tradeCommonService.checkExecIdHasQuote(fTradeId))) {
                    return;
                }
            }
            // add by etliu 20210129 end
            //信用保护凭证买入、信用保护凭证卖出的交收失败风险不在这里处理
            if(StringUtil.equals(mapTrd.get("pageModel"), DictCodeListExt3.交收操作类型_交收失败.getValue())
                    &&(StringUtil.equals(mapTrd.get("busiType"),"27") || StringUtil.equals(mapTrd.get("busiType"),"28"))){
                return;
            }
            // modify by zhangrui1 20200509 批量接口逻辑 start
//            revokeBackRiskCommon(mapTrd,BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),joinPoint);
            String infCode = BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue();
            if (批量接口相关交易方向.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
                logger.info("调用批量处理接口{}", infCode);
                infCode = BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
            }
            mapTrd.put("operateType", "revoke");
            // 手工交收 -> 交收失败
            if (args[0] instanceof ManualSettleDto ){
                if(DictCodeListExt3.交收状态_交收失败.getValue().equals(mapTrd.get("pageModel"))) {
                    // 资产类型
                    String assetType = StringUtil.EMPTY;
                    String assetCode = (String) CommonUtil.findValue(mapTrd, InterfUtil.finprodId);
                    if (StringUtil.isNotEmpty(assetCode)) {
                        AssetInfoRedisService assetInfoSrc = SpringContextUtil.getBean(AssetInfoRedisService.class);
                        if (assetInfoSrc != null) {
                            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(assetCode));
                            if (StringUtil.isNotEmpty(mapAsset) && StringUtil.isNotEmpty(mapAsset.values())) {
                                for (IAssetInfo iAst : mapAsset.values()) {
                                    assetType = StringUtil.isNotEmpty(iAst) ? iAst.getAssetType() : assetType;
                                }
                            }
                        }
                        mapTrd.put(PageFieldToInterfFactory.INF_ASSETTYPE, assetType);
                    }
                    // 交易金额
                    if (CommonUtil.findValue(mapTrd, "settleAmt") != null && CommonUtil.findValue(mapTrd, "settleNum") != null) {
                        // 交易金额
                        BigDecimal price = new BigDecimal(mapTrd.get("settleAmt").toString()).divide(new BigDecimal(mapTrd.get("settleNum").toString()), 4, RoundingMode.HALF_UP);
                        mapTrd.put("tradeAmt", CommonUtil.findValue(mapTrd, "settleAmt"));
                        mapTrd.put("tradeNum", CommonUtil.findValue(mapTrd, "settleNum"));
                        mapTrd.put("netPrice", price);
                        mapTrd.put("allPrice", price);
                        mapTrd.put("price", price);
                    }
                    String tradeId = null;
                    // 实际编号
                    if (CommonUtil.findValue(mapTrd, "orderId") != null) {
                        mapTrd.put(PageFieldToInterfFactory.INF_EVENTID, CommonUtil.findValue(mapTrd, "orderId"));
                        mapTrd.put("tradeId", CommonUtil.findValue(mapTrd, "orderId"));
                        if (StringUtil.equals(金融产品类型_投管用_债券质押式正回购.getValue(), CommonUtil.findValue(mapTrd, "finprodType2")) ||
                                StringUtil.equals(金融产品类型_投管用_债券质押式逆回购.getValue(), CommonUtil.findValue(mapTrd, "finprodType2"))) {//针对协议回购交收失败组装实占编号
                            tradeId = String.format("%s-%s-%s", CommonUtil.findValue(mapTrd, "entrustNo"), CommonUtil.findValue(mapTrd, "portfolioId"), CommonUtil.findValue(mapTrd, "iOrderId"));
                            mapTrd.put("tradeId", isDqxz(mapTrd) ? tradeId + "T01" : tradeId);
                            mapTrd.put("tradeAmt", CommonUtil.findValue(mapTrd, "tradedVolume"));
                            mapTrd.put("tradeNum", CommonUtil.findValue(mapTrd, "tradedVolume"));
                        }
                    }
                    // 预占编号
                    if (CommonUtil.findValue(mapTrd, "entrustNo") != null) {
                        mapTrd.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE, CommonUtil.findValue(mapTrd, "entrustNo"));
                        mapTrd.put("entrustId", CommonUtil.findValue(mapTrd, "entrustNo"));
                    }
                    if(CommonUtil.findValue(mapTrd, "settleDateStr") != null) {
                        mapTrd.put("mdate", CommonUtil.findValue(mapTrd, "settleDateStr"));
                        mapTrd.put("entrustDate", CommonUtil.findValue(mapTrd, "settleDateStr"));
                    }
                    mapTrd.put("manualSettle", "Y");
                    // 实占释放
                    revokeManualSettle(mapTrd, infCode, BussinessDictCodeListRds.事件类型_投资交易.getValue(), assetType, joinPoint);
                    //TODO 到期续作失败时占回原实占
                    if (isDqxz(mapTrd) && StringUtil.isNotEmpty(tradeId)) {
                        mapTrd.put("tradeId", tradeId + "T07");
                        revokeManualSettle(mapTrd, BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(), BussinessDictCodeListRds.事件类型_投资交易.getValue(), assetType, joinPoint);
                    }
                    // 预占释放
                    Map<String, Object> etrMap = new HashMap<>(mapTrd);
                    // 预占编号
                    if (CommonUtil.findValue(etrMap, "entrustNo") != null) {
                        etrMap.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE, CommonUtil.findValue(etrMap, "entrustNo"));
                        etrMap.put("tradeId", CommonUtil.findValue(etrMap, "entrustNo"));
                        etrMap.put("entrustId", CommonUtil.findValue(etrMap, "entrustNo"));
                    }
                    // 交易金额
                    if (CommonUtil.findValue(etrMap, "settleAmt") != null) {
                        etrMap.put("entrustNum", CommonUtil.findValue(etrMap, "settleAmt"));
                    }
                    etrMap.put("manualSettle", "Y");
                    if (StringUtil.equals(金融产品类型_投管用_债券质押式正回购.getValue(), CommonUtil.findValue(mapTrd, "finprodType2")) ||
                            StringUtil.equals(金融产品类型_投管用_债券质押式逆回购.getValue(), CommonUtil.findValue(mapTrd, "finprodType2"))) {//针对协议回购交收失败
                        etrMap.put("tradeAmt", CommonUtil.findValue(mapTrd, "tradedVolume"));
                        etrMap.put("entrustNum", CommonUtil.findValue(mapTrd, "tradedVolume"));
                        etrMap.put("manualSettle", "N");
                    }
                    if (isDqxz(mapTrd)) {
                        etrMap.put("ps", DictCodeList.交易方向_到期续做.getValue());
                    }
                    revokeManualSettle(etrMap, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(), BussinessDictCodeListRds.事件类型_投资委托.getValue(), assetType, joinPoint);
                    //throw new BusinessException("aaa");
                }
            }else {
                revokeBackRiskCommon(mapTrd, infCode, joinPoint);
            }
            // modify by zhangrui1 20200509 批量接口逻辑 end
        }
    }
    @AfterReturning("backPoint()")
    public void backTradeMethod(JoinPoint joinPoint) throws BusinessException {
        if(!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)||!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        Map<String,Object> mapTrd=JsonUtils.object2Map(args[0]);
        if(StringUtil.isNotEmpty(mapTrd)&&StringUtil.isNotEmpty(mapTrd.get("operateType"))){
            if(StringUtil.equals(mapTrd.get("operateType").toString(),"delete")||StringUtil.equals(mapTrd.get("operateType").toString(),"revoke")){
                return;
            }
        }
        //如果是撤销的退回再提交直接退去，不需要调实占
        if(!checkRevokeAction(mapTrd.get("workflowId"))){
            return;
        }
        
        // add by etliu 20210129 start
        // 选择的成交编号存在对应的报价，则不影响风险
        if (mapTrd.get("fTradeId") != null) {
            String fTradeId = (String) mapTrd.get("fTradeId");
            if (StringUtil.isNotBlank(fTradeId) && StringUtil.equals(DictCodeList.是否_是.getValue(),
                tradeCommonService.checkExecIdHasQuote(fTradeId))) {
                return;
            }
        }
        // add by etliu 20210129 end
        //信用保护凭证买入、信用保护凭证卖出的交收失败风险不在这里处理
        if(StringUtil.equals(mapTrd.get("pageModel"), DictCodeListExt3.交收操作类型_交收失败.getValue())
                &&(StringUtil.equals(mapTrd.get("busiType"),"27") || StringUtil.equals(mapTrd.get("busiType"),"28"))){
            return;
        }
        // modify by zhangrui1 20200509 批量接口逻辑 start
//        revokeBackRiskCommon(mapTrd,BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),joinPoint);
        String infCode = BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue();
        if (批量接口相关交易方向.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
            logger.info("调用批量处理接口{}", infCode);
            infCode = BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
        }
        revokeBackRiskCommon(mapTrd, infCode, joinPoint);
        // modify by zhangrui1 20200509 批量接口逻辑 end
    }

    private void revokeManualSettle(Map<String,Object> mapTrd,String infCode,String eventType,String assetType,JoinPoint joinPoint){
        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        interfRiskRdsDto.setEventType(eventType);
        interfRiskRdsDto.setInfCode(infCode);
        interfRiskRdsDto.setAssetType(StringUtil.isNotEmpty(assetType)?assetType:StringUtil.EMPTY);
        interfRiskRdsDto.setFinType(StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))?mapTrd.get(InterfUtil.finprodType2).toString():StringUtil.EMPTY);
        try {
            ClientData result = interfRiskRdsService.sendRiskRdsData(mapTrd, interfRiskRdsDto);
            if(StringUtil.isNotEmpty(result)&&StringUtil.isNotEmpty(result.getHead())&&StringUtil.isNotEmpty(result.getHead().get(InterfUtil.std400msgt))){
                if(StringUtil.equals(result.getHead().get(InterfUtil.std400msgt).toString(),InterfUtil.E)&&StringUtil.isNotEmpty(result.getHead().get(InterfUtil.stdrtninfo))){
                    throw new BusinessException(InterfUtil.MSG+result.getHead().get(InterfUtil.stdrtninfo).toString());
                }
            }
        } catch (Exception e) {
            logger.error("风险处理异常",e);
            throw new BusinessException(e);
        }
    }

    private void revokeBackRiskCommon(Map<String,Object> mapTrd,String infCode,JoinPoint joinPoint) throws BusinessException {
        //通道费支付去除 标准券入库 modify by jiangpeng on 20210511 ZGBSSE-351
        if(ignoreList.contains(joinPoint.getTarget().getClass().getSimpleName())){
            return;
        }
        setEachAstType(mapTrd,joinPoint);
        String assetType=StringUtil.EMPTY;
        String assetCode = (String)CommonUtil.findValue(mapTrd, InterfUtil.finprodId);
        if(StringUtil.isNotEmpty(assetCode)){
            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(assetCode));
            if(StringUtil.isNotEmpty(mapAsset)&&StringUtil.isNotEmpty(mapAsset.values())) {
                for (IAssetInfo iAst : mapAsset.values()) {
                    assetType = StringUtil.isNotEmpty(iAst)?iAst.getAssetType():assetType;
                }
            }
        }
        else if (null != mapTrd.get("assetType")) {
            assetType = (String)mapTrd.get("assetType");
        }
        String key = "finprodType2";
        if (CommonUtil.isType((String)mapTrd.get(key), DictCodeListExt1.金融产品类型_投管用_债券借贷)
            || CommonUtil.isType((String)mapTrd.get(key), DictCodeListExt1.金融产品类型_投管用_债券借贷融入)
            || CommonUtil.isType((String)mapTrd.get(key), DictCodeListExt1.金融产品类型_投管用_债券借贷融出)) {
            return;
        }
        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
        interfRiskRdsDto.setInfCode(infCode);
        interfRiskRdsDto.setAssetType(StringUtil.isNotEmpty(assetType)?assetType:StringUtil.EMPTY);
        interfRiskRdsDto.setFinType(StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))?mapTrd.get(InterfUtil.finprodType2).toString():StringUtil.EMPTY);
        try {
            // add by zhangrui1 一级投标维护 start
            if (StringUtil.isNotEmpty(CommonUtil.findValue(mapTrd, BOND_FIRST_SIGN))) {
                doFirstBondTrade(mapTrd, infCode);
            }
            else {
            doBondBid(mapTrd, joinPoint, interfRiskRdsDto);
            // add by zhangrui1 一级投标维护 end
            ClientData result = interfRiskRdsService.sendRiskRdsData(mapTrd, interfRiskRdsDto);
            if(StringUtil.isNotEmpty(result)&&StringUtil.isNotEmpty(result.getHead())&&StringUtil.isNotEmpty(result.getHead().get(InterfUtil.std400msgt))){
                if(StringUtil.equals(result.getHead().get(InterfUtil.std400msgt).toString(),InterfUtil.E)&&StringUtil.isNotEmpty(result.getHead().get(InterfUtil.stdrtninfo))){
                    throw new BusinessException(InterfUtil.MSG+result.getHead().get(InterfUtil.stdrtninfo).toString());
                }
            }
            // add by zhangrui1 一级投标维护 start
            }
            // add by zhangrui1 一级投标维护 end
        } catch (Exception e) {
            logger.error("风险处理异常",e);
            throw new BusinessException(e);
        }
    }

    // add by zhangrui1 start
    @SuppressWarnings("unchecked")
    private void doBondBid(Map<String, Object> mapTrd, JoinPoint joinPoint,
                           InterfRiskRdsDto interfRiskRdsDto) {
        String clzName = joinPoint.getTarget().getClass().getSimpleName();
        String listKey = null;
        // 汇总确认
        if (clzName.contains("PrimaryBiddingManage002Service")) {
            listKey = "primaryBiddingPortfolioDtolist";
            interfRiskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_中标结果确认.getValue());
        }
        // 中标结果维护
        else if (clzName.contains("PrimaryBiddingManage003Service")) {
            listKey = "bidOrderBidPriceList";
            interfRiskRdsDto.setEventStatus(BussinessDictCodeList.共同业务类型_中标结果维护.getValue());
        }
        else {
            return;
        }
        String portfolioIdKey = "portfolioId";
        String entrustIdKey = "entrustId";
        List<Map<String, Object>> list = (List<Map<String, Object>>)mapTrd.get(listKey);
        if (!StringUtil.isEmptyList(list)) {
            Map<String, Object> map = list.get(0);
            mapTrd.put(portfolioIdKey, map.get(portfolioIdKey));
            mapTrd.put(entrustIdKey, map.get(entrustIdKey));
        }
        interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        String infCode = interfRiskRdsDto.getInfCode();
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放批量接口)) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口)) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
        }
        interfRiskRdsDto.setInfCode(infCode);
    }
    // add by zhangrui1 end

    /**
     * 设置每个交易品种过来的类型
     * @param mapTrd
     * @param joinPoint
     */
    private void setEachAstType(Map<String,Object> mapTrd,JoinPoint joinPoint) throws BusinessException {
        //买断式回购(由于退回再提交时无法识别买断式)
        if(!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))&&joinPoint.getTarget().getClass().getSimpleName().contains("TrdOutRightRepoTrade002Service")){
            if(StringUtil.isNotEmpty(mapTrd.get(InterfUtil.ps))&&StringUtil.equals(mapTrd.get(InterfUtil.ps).toString(),DictCodeList.回购拆借方向_正回购.getValue())){
                mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
            }else{
                mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
            }
        }
        //期权
        if(!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))&&joinPoint.getTarget().getClass().getSimpleName().contains("TrdOptionTrade002Service")){
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        }
        //即期
        if(!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))&&joinPoint.getTarget().getClass().getSimpleName().contains("TrdSpotexInvestTrade002Service")){
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        }
        //掉期
        if(!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))&&joinPoint.getTarget().getClass().getSimpleName().contains("TrdFxswapInvestTrade002Service")){
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        }
        //远期
        if(!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))&&joinPoint.getTarget().getClass().getSimpleName().contains("TrdForwexInvestTrade002Service")){
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_远期.getValue());
        }
        //债转股
        if(joinPoint.getTarget().getClass().getSimpleName().contains("TrdBondToStock002Service")) {
            try {
                if (StringUtil.isNotEmpty(mapTrd.get("baseTotalTradeInfoDto"))) {
                    BaseTotalTradeInfoDto baseTotalTradeInfoDto = (BaseTotalTradeInfoDto) JsonUtils.map2entity((Map) mapTrd.get("baseTotalTradeInfoDto"), BaseTotalTradeInfoDto.class);
                    if (StringUtil.isNotEmpty(baseTotalTradeInfoDto) && StringUtil.isNotEmpty(baseTotalTradeInfoDto.getTrdFinProductDealTotal())) {
                        mapTrd.put(InterfUtil.ps, baseTotalTradeInfoDto.getTrdFinProductDealTotal().getPs());
                    }
                }
            }catch (Exception e){
                throw new BusinessException(e);
            }
        }
    }

    /**
     * 如果是撤销的退回再提交直接退去，不需要调实占
     * @param workflowId
     * @param <T>
     * @return
     */
    private <T> boolean checkRevokeAction(T workflowId){
        if(StringUtil.isNotEmpty(workflowId)) {
            String actionType = riskTrialComm.selectWorkflowActionType(workflowId.toString());
            if (actionType.equals("revoke")||actionType.equals("delete")) {
                return false;
            }
        }
        return true;
    }

    // add by zhangrui1 cotroller.save出错后需要撤销原来实占的数据 start
    /**
     * 提交流程操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.controller..*.save(..))")
    protected void savePoint() {
        // 提交流程操作
    }

    /**
     * 清除线程缓存
     * 
     * @param joinPoint 切面参数
     */
    @AfterReturning(value = "savePoint()")
    protected void afterSaveTradeMethod(JoinPoint joinPoint) {
        // 清除线程缓存
        cleanLocalCache();
    }

    /**
     * 若提交流程出错则需要将实占的数据撤销掉
     * 
     * @param joinPoint 切面参数
     * @param exception 抛出的异常
     */
    @AfterThrowing(value = "savePoint()", throwing = "exception")
    protected void saveTradeMethod(JoinPoint joinPoint, Throwable exception) {
        Object[] args = joinPoint.getArgs();
        if(!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)
                || args == null || args.length<1){
            return;
        }
        Map<String,Object> mapTrd=JsonUtils.object2Map(args[0]);
        if(CommonUtil.findValue(mapTrd,"operateType").equals(BussinessConstant.SUBMIT)) {
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            if (mapTrd.get("fTradeId") != null) {
                String fTradeId = (String) mapTrd.get("fTradeId");
                if (StringUtil.isNotBlank(fTradeId) && StringUtil.equals(DictCodeList.是否_是.getValue(),
                    tradeCommonService.checkExecIdHasQuote(fTradeId))) {
                    return;
                }
            }
            // add by etliu 20210129 end
            
            List<?> list = getTarget();
            if (CollectionUtils.isEmpty(list)) {
                String infCode = BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue();
                if (批量接口相关交易方向.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
                    logger.info("调用批量处理接口{}", infCode);
                    infCode = BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
                }
                revokeBackRiskCommon(mapTrd, infCode, joinPoint);
                return;
            }
            for (Object obj : list) {
                Map<String, Object> dataMap = JsonUtils.object2Map(obj);
                String infCode = BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue();
                if (批量接口相关交易方向.contains(CommonUtil.findValue(dataMap, PageFieldToInterfFactory.INF_PS))) {
                    logger.info("调用批量处理接口{}", infCode);
                    infCode = BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
                }
                revokeBackRiskCommon(dataMap, infCode, joinPoint);
            }
    	}
        // 清除线程缓存
        cleanLocalCache();
    }
    // add by zhangrui1 cotroller.save出错后需要撤销原来实占的数据 end

    // add by zhangrui1 增加指令预占释放 start
    /**
     * 是否开启预占功能 <br>
     * 系统参数【is_need_preoccupy（Y:开启预占）】
     * 
     * @return true:开启预占功能
     */
    public static boolean isNeedPreoccupy() {
        return CommonUtil.isType(CacheUtil.getSysParam("is_need_preoccupy"),
            BussinessDictCodeListRds.是否_是);
    }

    /**
     * 执行风险预占计算
     * 
     * @param dto 委托指令内容
     * @param infCode 接口代码
     * @param busiType 业务类型
     * @return 执行结果
     * @throws Exception 异常
     */
    @SuppressWarnings("unchecked")
    public static final <T> AjaxResult doPreOccupy(T dto, String infCode, String busiType)
        throws Exception {
        if (!isNeedPreoccupy()) {
            return null;
        }
        // 接口组件Service
        InterfRiskRdsService<InterfRiskRdsDto> service = getInterfRiskRdsService();
        InterfRiskRdsDto rdsDto = new InterfRiskRdsDto();
        if (CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果确认)
            || CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果维护)) {
            rdsDto.setEventStatus(busiType);
            busiType = DictCodeListExt2.平安资产类型_一级市场债.getValue();
        }
        // 执行预占
        AjaxResult ar = service.checkRiskRds(rdsDto,
            BussinessDictCodeListRds.事件类型_投资委托.getValue(), infCode, busiType,
            en -> service.sendRiskRdsData(dto, en));
        // 判断结果，若正常则缓存数据，后面执行异常时使用缓存数据进行预占释放
        if (ar == null) {
            setLocalCache(dto);
        }
        // 返回结果
        return ar;
    }

    /**
     * 批量接口调用
     * 
     * @param dtoList 批量数据
     * @param busiType 业务类型
     * @param infCode 接口代码
     * @param isSingleCounter 是否同一个交易对手
     * @param etrInvestPersonInfo 投资人员信息
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public static final <T> AjaxResult doListPreOccupy(List<T> dtoList, String busiType,
                                                       String infCode, boolean isSingleCounter,
                                                       BussEtrInvestPersonInfoDto etrInvestPersonInfo)
        throws Exception {
        if (!isNeedPreoccupy()) {
            return null;
        }
        // 接口组件Service
        InterfRiskRdsService<InterfRiskRdsDto> service = getInterfRiskRdsService();
        InterfRiskRdsDto rdsDto = new InterfRiskRdsDto();
        if (CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果确认)
            || CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果维护)) {
            rdsDto.setEventStatus(busiType);
            busiType = DictCodeListExt2.平安资产类型_一级市场债.getValue();
        }
        // 执行预占
        AjaxResult ar = service.checkRiskRds(rdsDto,
            BussinessDictCodeListRds.事件类型_投资委托.getValue(), infCode,
            busiType, en -> service.sendRiskRdsListData(en, dtoList, map -> {
                // 个别处理
                // 交易对手
                if (isSingleCounter && etrInvestPersonInfo != null) {
                    map.put(PageFieldToInterfFactory.INF_COUNTERID,
                        etrInvestPersonInfo.getCounterId());
                }
            }));
        // 判断结果，若正常则缓存数据，后面执行异常时使用缓存数据进行预占释放
        if (ar == null) {
            setLocalCache(dtoList);
        }
        return ar;
    }
    // add by zhangrui1 增加指令预占释放 start
    // add by zhangrui1 一级市场债分销缴款特殊处理 start
    public static final String BOND_FIRST_SIGN = "bondFirstSign";

    public static final String O_FINPROD_ID = "oFinprodId";

    public static final String IS_DIFF_OCCUPY = "isDiffOccupy";

    public static final String DIFF_OCCUPY_FLG = "diffOccupyFlg";

    public static final String BID_ID = "bidId";

    public static final String TRADE_ID = "tradeId";

    public static final String TOTLE_TRADE_ID = "totleTradeId";

    public static final String BUSICODE = "busiCode";

    public static final String ENTRUST_NUM = "entrustNum";

    public static final String CPRICE_AMT = "cpriceAmt";

    public static final String UNIT_CPRICE = "unitCprice";

    public static final String ENTRUST_ID = "entrustId";

    public static final String IS_REVERSE_OCCUPY = "isReverseOccupy";

    @SuppressWarnings("unchecked")
    public static final <T> AjaxResult doFirstBondTrade(T dto, String infCode)
        throws Exception {
        Map<String, Object> trdMap = CommonUtil.toMap(dto);
        // 是一级市场债分销缴款时
        if (!StringUtil.isNotEmpty(trdMap.get(BOND_FIRST_SIGN))) {
            return null;
        }
        // 如果是实占则先进行预占
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
            firstBondPreOcpp(trdMap, infCode);
        }
        // 接口组件Service
        InterfRiskRdsService<InterfRiskRdsDto> service = getInterfRiskRdsService();
        Map<String, Object> ocpMap = new HashMap<>();
        ocpMap.putAll(trdMap);
        ocpMap.remove(ENTRUST_ID);
        // 执行实占
        AjaxResult ar = service.checkRiskRds(new InterfRiskRdsDto(),
            BussinessDictCodeListRds.事件类型_投资交易.getValue(),
            infCode, DictCodeList.平安资产类型_债券.getValue(),
            en -> service.sendRiskRdsDataCommon(ocpMap, en));
        // 判断结果，若正常则缓存数据，后面执行异常时使用缓存数据进行预占释放
        if (ar == null && CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
            setLocalCache(dto);
        }
        // 异常时需要把前面的数据释放掉
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
            firstBondPreOcpp(trdMap, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue());
        }
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
            ClientData result = firstBondPreOcpp(trdMap,
                BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue());
            // 出错需要回滚前面的数据
            if (!isOK(result)) {
                service.checkRiskRds(new InterfRiskRdsDto(),
                    BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                    BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),
                    DictCodeList.平安资产类型_债券.getValue(),
                    en -> service.sendRiskRdsDataCommon(ocpMap, en));
            }
        }
        return ar;
    }

    public static final ClientData firstBondPreOcpp(Map<String, Object> trdMap, String infCode)
        throws Exception {
        if (isNeedPreoccupy()) {
            // 接口组件Service
            InterfRiskRdsService<InterfRiskRdsDto> service = getInterfRiskRdsService();
            Map<String, Object> preMap = new HashMap<>();
            preMap.putAll(trdMap);
            if (StringUtil.isNotEmpty(trdMap.get("finprodType2")) && 
                    StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(),(String)trdMap.get("finprodType2"))){
                preMap.put(InterfUtil.finprodId, trdMap.get(O_FINPROD_ID) == null ? trdMap.get(InterfUtil.finprodId) : trdMap.get(O_FINPROD_ID));
            }else{
                preMap.put(InterfUtil.finprodId, trdMap.get(O_FINPROD_ID));   
            }
            preMap.put(IS_DIFF_OCCUPY, BussinessDictCodeList.是否_是.getValue());
            Object deffOcpFlg = CommonUtil.findValue(trdMap, TOTLE_TRADE_ID);
            if (!StringUtil.isNotEmpty(deffOcpFlg)) {
                deffOcpFlg = CommonUtil.findValue(trdMap, TRADE_ID);
            }
            if (!StringUtil.isNotEmpty(deffOcpFlg)) {
                deffOcpFlg = CommonUtil.findValue(trdMap, BUSICODE);
            }
            preMap.put(DIFF_OCCUPY_FLG, deffOcpFlg);
            preMap.put(IS_REVERSE_OCCUPY, BussinessDictCodeList.是否_是.getValue());
            BigDecimal cpriceAmt = CommonUtil.getBigDecimal(preMap, CPRICE_AMT, BigDecimal.ZERO);
            BigDecimal unitCprice = CommonUtil.getBigDecimal(preMap, UNIT_CPRICE, BigDecimal.ONE);
            preMap.put(ENTRUST_NUM, ArithmeticUtil.multiply(
                ArithmeticUtil.divide(cpriceAmt, unitCprice, 4), new BigDecimal(100)));
            InterfRiskRdsDto rdsDto = new InterfRiskRdsDto();
            rdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
            rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
            if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                rdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占接口.getValue());
            }
            else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
                rdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue());
            }
            else {
                rdsDto.setInfCode(infCode);
            }
            return service.sendRiskRdsDataCommon(preMap, rdsDto);
        }
        return null;
    }

    public static final boolean isOK(ClientData result) {
        if (result != null && StringUtil.isNotEmpty(result.getHead())) {
            Object std400msgt = result.getHead().get(InterfUtil.std400msgt);
            if (StringUtil.equals(std400msgt, InterfUtil.E)) {
                return false;
            }
        }
        return true;
    }
    // add by zhangrui1 一级市场债分销缴款特殊处理 end

    /**
     * 是否时到期续作
     * @param mapTrd
     * @return
     */
    private boolean isDqxz(Map<String,Object> mapTrd) {
        return StringUtil.equals(mapTrd.get("busiType"),"09") || StringUtil.equals(mapTrd.get("busiType"),"15") ||
                StringUtil.equals(mapTrd.get("busiType"),"20") || StringUtil.equals(mapTrd.get("busiType"),"24");
    }
}
