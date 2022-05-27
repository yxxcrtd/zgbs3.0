/*
 * 文件名：EntrustRdsCommonAop.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.common.aop;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.constant.PageFieldToInterfFactory;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.riskrds.service.RiskRouterService;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
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
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.RiskTrialComm;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易撤销时调用风险逻辑aop
 *
 * @author zhangrui1
 * @version 1.0 2020年7月2日
 * @see EntrustRdsCommonAop
 * @since 1.0
 */
@Component
@Aspect
public class ExchangeEntrustRdsCommonAop {

    public static final String DELETE = "delete";

    private static final String BATCH_INFO_LOG = "调用批量处理接口{}";

    public List<String> listInfPs = Arrays.asList(DictCodeListExt1.交易方向_债转股.getValue(),
        DictCodeListExt2.交易方向_转换申请.getValue(), DictCodeListExt2.交易方向_转换赎回申请.getValue());

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(ExchangeEntrustRdsCommonAop.class);

    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    @Autowired
    private RiskTrialComm riskTrialComm;

    /**
     * 线程缓存
     */
    private static ThreadLocal<List<? extends Object>> localCache = ThreadLocal.withInitial(
        ArrayList::new);

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
        // 执行预占
        AjaxResult ar = service.checkRiskRds(new InterfRiskRdsDto(),
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
        // 执行预占
        AjaxResult ar = null;
        service.checkRiskRds(new InterfRiskRdsDto(),
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

    /**
     * 取得接口组件Service（单资产类型）
     * 
     * @return 接口组件Service
     */
    @SuppressWarnings("unchecked")
    public static InterfRiskRdsService<InterfRiskRdsDto> getInterfRiskRdsService() {
        return SpringContextUtil.getApplicationContext().getBean(InterfRiskRdsService.class);
    }

    // add by zhangrui1 多种类型资产批量预占 start
    /**
     * 取得接口组件Service（多资产类型）
     * 
     * @return 接口组件Service
     */
    public static RiskRouterService getRiskRouterService() {
        return SpringContextUtil.getApplicationContext().getBean(RiskRouterService.class);
    }

    /**
     * 多种类型资产的批量预占
     * 
     * @param pageId 画面ID
     * @param dtoList 批量列表
     * @param infCode 接口代码
     * @return 预占结果
     */
    public static final <T> AjaxResult doListPreOccupy(String pageId, List<T> dtoList,
                                                       String infCode) {
        if (!isNeedPreoccupy()) {
            return null;
        }
        // 接口组件Service
        RiskRouterService service = getRiskRouterService();
        AjaxResult ar = service.execute(infCode, pageId, dtoList);
        // 判断结果，若正常则缓存数据，后面执行异常时使用缓存数据进行预占释放
        if (ar == null) {
            Map<String, Object> dtoMap = new HashMap<>();
            dtoMap.put(pageId, dtoList);
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(dtoMap);
            setLocalCache(list);
        }
        return ar;
    }
    // add by zhangrui1 多种类型资产批量预占 end

    /**
     * 退回时在待办中再提交
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doUpdate(..))")
    protected void updatePoint() {
        // 退回时在待办中再提交
        logger.info("----------委托指令再提交----------");
    }

    /**
     * 撤销操作
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doComplete(..))")
    protected void revokePoint() {
        // 撤销操作
        logger.info("----------委托指令撤销----------");
    }

    /**
     * 退回操作
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doBack(..))")
    protected void backPoint() {
        // 退回操作
        logger.info("----------委托指令流程退回----------");
    }

    /**
     * 取得操作类型
     * 
     * @param mapTrd 参数
     * @return 操作类型
     */
    protected Object getOperateType(Map<String, Object> mapTrd) {
        return CollectionUtils.isEmpty(mapTrd) ? StringUtil.EMPTY : mapTrd.get("operateType");
    }

    /**
     * 是删除或者退回
     * 
     * @param target 目标操作
     * @return true:退回或删除
     */
    protected boolean isRevokeOrDel(Object target) {
        return StringUtil.equals(BussinessConstant.REVOKE, target)
               || StringUtil.equals(DELETE, target);
    }

    /**
     * 委托指令审批退回时需要释放预占额度
     * 
     * @param joinPoint 参数列表
     */
    @AfterReturning("backPoint()")
    public void backEntrustMethod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (!isNeedPreoccupy() || args == null || args.length == 0) {
            return;
        }
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        // 如果是撤销的退回再提交直接退去，不需要调实占
        if (isRevokeOrDel(getOperateType(mapTrd)) || checkRevokeAction(mapTrd)) {
            return;
        }
        revokeBackRiskCommon(mapTrd, joinPoint);
    }

    /**
     * 委托指令撤销时需要释放预占额度
     * 
     * @param joinPoint 参数列表
     */
    @AfterReturning("revokePoint()")
    public void revokeEntrustMethod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (!isNeedPreoccupy() || args == null || args.length <= 1) {
            return;
        }
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        if (StringUtil.isNotEmpty(mapTrd) && StringUtil.isNotEmpty(mapTrd.get("elementId"))
                && mapTrd.get("elementId").toString().indexOf("EtrIbRepoRk")!=-1){
            return;
        }
        if (OperationType.REVOKE.equals(args[1]) || OperationType.DELETE.equals(args[1])) {
            revokeBackRiskCommon(mapTrd, joinPoint);
        }
    }

    @AfterReturning("updatePoint()")
    public void updateEntrustMethod(JoinPoint joinPoint) {
        if (!isNeedPreoccupy() || !StringUtil.isNotEmpty(joinPoint.getArgs())) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        // add by yzg 期货暂时不用风险 20201211 start
        if(StringUtil.equals("F36", mapTrd.get("finprodType2"))) {
        	return;
        }
        // add by yzg 期货暂时不用风险 20201211 end
        if (StringUtil.equals(getOperateType(mapTrd), BussinessConstant.SUBMIT)) {
            //如果是撤销的退回再提交直接退去，不需要调实占
            if (checkRevokeAction(mapTrd)) {
                return;
            }
            String infCode = BussinessDictCodeListRds.接口系统ID_预占接口.getValue();
            if (listInfPs.contains(
                CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))
            // ZGZGST-2517:质押式回购多组合走预占批量接口 fuyongsheng 20210917 add start
            || mapTrd.containsKey("etrInvestRepoInfoDtos")
            // ZGZGST-2517:质押式回购多组合走预占批量接口 fuyongsheng 20210917 add end
            // ZGZGST-2768:现券多组合指令走批量预占接口 fuyongsheng 20210917 add start
            || mapTrd.containsKey("etrInvestBondInfoDtos")
            // ZGZGST-2768:现券多组合指令走批量预占接口 fuyongsheng 20210917 add end
            || StringUtil.equals(mapTrd.get("isMulti"),DictCodeList.是否_是.getValue())
            ) {
                logger.info(BATCH_INFO_LOG, infCode);
                infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
            }
            revokeBackRiskCommon(mapTrd, infCode, joinPoint);
        }
    }

    private String initRepoLendCommon(String finprodType2, String assetType) {
        if (CommonUtil.isType(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购)
            || CommonUtil.isType(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购)) {
            return DictCodeList.平安资产类型_买断式回购.getValue();
        }
        if (CommonUtil.isType(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购)
            || CommonUtil.isType(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购)
                || StringUtil.equals(finprodType2, "ZYSHG")) {
            return DictCodeList.平安资产类型_质押式回购.getValue();
        }
        return assetType;
    }

    private void revokeBackRiskCommon(Map<String, Object> mapTrd, String infCode,
                                      JoinPoint joinPoint) {
        // 设置投管分类等
        setEachAstType(mapTrd, joinPoint);
        String assetType = StringUtil.EMPTY;
        Object finprodId = mapTrd.get(InterfUtil.finprodId);
        Object finprodType2 = mapTrd.get(InterfUtil.finprodType2);
        // 取得资产类型
        if (StringUtil.isNotEmpty(finprodId)) {
            IAssetInfo assetInfo = assetInfoSrc.getInfoFromRedis(RedisKeyEnum.资产基本信息.key(),
                finprodId.toString());
            assetType = assetInfo == null ? assetType : assetInfo.getAssetType();
        }
        else if (null != mapTrd.get("assetType")) {
            assetType = (String)mapTrd.get("assetType");
        }
        if (CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷)
            || CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融入)
            || CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融出)
            //债券一级指令不进行预占
            || CommonUtil.isType(assetType, DictCodeListExt2.平安资产类型_一级市场债)) {
            return;
        }
        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        // 事件类型 预占
        interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        // 接口代码
        interfRiskRdsDto.setInfCode(infCode);
        // 资产类型
        interfRiskRdsDto.setAssetType(initRepoLendCommon((String)finprodType2, assetType));
        // 金融产品类型
        interfRiskRdsDto.setFinType(
            finprodType2 != null ? finprodType2.toString() : StringUtil.EMPTY);
        try {
            ClientData result = getInterfRiskRdsService().sendRiskRdsData(mapTrd,
                interfRiskRdsDto);
            // 调用接口返回错误信息时抛出异常
            if (result != null
                && StringUtil.equals(result.getHeadField(InterfUtil.std400msgt), InterfUtil.E)
                && StringUtil.isNotEmpty(result.getHeadField(InterfUtil.stdrtninfo))) {
                throw new BusinessException(
                    InterfUtil.MSG + result.getHeadField(InterfUtil.stdrtninfo));
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    private String getFinprodType2(String simpleName, Object ps) {
        // 买断式回购(由于退回再提交时无法识别买断式)
        if (simpleName.contains("EtrBoutRepoService")) {
            if (StringUtil.isNotEmpty(ps)
                && StringUtil.equals(ps, DictCodeList.回购拆借方向_正回购.getValue())) {
                return DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue();
            }
            else {
                return DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue();
            }
        }
        // 质押式回购
        if (simpleName.contains("EtrRepo002Service")) {
            if (StringUtil.isNotEmpty(ps)
                && StringUtil.equals(ps, DictCodeList.回购拆借方向_正回购.getValue())) {
                return DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            }
            else {
                return DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            }
        }
        // 期权
        if (simpleName.contains("EtrOptionService")) {
            return DictCodeListExt3.金融产品类型_投管用_期权.getValue();
        }
        // 即期
        if (simpleName.contains("EtrWtSpotexService")) {
            return DictCodeListExt1.金融产品类型_投管用_即期.getValue();
        }
        // 掉期
        if (simpleName.contains("EtrWtFxswapService")) {
            return DictCodeListExt1.金融产品类型_投管用_掉期.getValue();
        }
        // 远期
        if (simpleName.contains("EtrForwexService")) {
            return DictCodeListExt1.金融产品类型_投管用_远期.getValue();
        }
        // 拆借
        if (simpleName.contains("EtrLendSpecInfoService")) {
            if (StringUtil.isNotEmpty(ps)
                && StringUtil.equals(ps, BussinessDictCodeList.回购拆借方向_拆出.getValue())) {
                return BussinessDictCodeList.金融产品类型_投管用_拆出.getValue();
            }
            else {
                return BussinessDictCodeList.金融产品类型_投管用_拆入.getValue();
            }
        }
        return null;
    }

    /**
     * 设置每个交易品种过来的类型
     * @param mapTrd
     * @param joinPoint
     */
    private void setEachAstType(Map<String, Object> mapTrd, JoinPoint joinPoint) {
        String simpleName = joinPoint.getTarget().getClass().getSimpleName();
        Object ps = mapTrd.get(InterfUtil.ps);
        if (!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))) {
            mapTrd.put(InterfUtil.finprodType2, getFinprodType2(simpleName, ps));
        }
        //债转股
        if (simpleName.contains("EtrBondToStockService")) {
            mapTrd.put(InterfUtil.ps, DictCodeListExt1.交易方向_债转股.getValue());
        }
        // 拆借
        if (simpleName.contains("EtrLendSpecInfoService")) {
            if (StringUtil.equals(mapTrd.get("assetType1"),
                BussinessDictCodeList.拆借类型_特殊目的载体.getValue())) {
                mapTrd.put(InterfUtil.finprodType2,
                    BussinessDictCodeList.金融产品类型_投管用_特拆拆入.getValue());
            }
        }
        // 债券借贷
        if (simpleName.contains("EtrSecLendService")) {
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷.getValue());
        }
    }

    /**
     * 如果是撤销的退回再提交直接退去，不需要调实占
     * 
     * @param mapTrd
     * @return
     */
    private boolean checkRevokeAction(Map<String, Object> mapTrd) {
        Object workflowId = mapTrd.get("workflowId");
        if(StringUtil.isNotEmpty(workflowId)) {
            String actionType = riskTrialComm.selectWorkflowActionType(workflowId.toString());
            return isRevokeOrDel(actionType);
        }
        return false;
    }

    /**
     * 提交流程操作
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.controller..*.save(..))")
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    @AfterThrowing(value = "savePoint()", throwing = "exception")
    protected void saveTradeMethod(JoinPoint joinPoint, Throwable exception) {
        Object[] args = joinPoint.getArgs();
        if (!isNeedPreoccupy() || args == null || args.length < 1) {
            return;
        }
        Map<String,Object> mapTrd=JsonUtils.object2Map(args[0]);
        if (StringUtil.equals(getOperateType(mapTrd), BussinessConstant.SUBMIT)) {
            List<?> list = getTarget();
            if (CollectionUtils.isEmpty(list)) {
                revokeBackRiskCommon(mapTrd, joinPoint);
                return;
            }
            for (Object obj : list) {
                // 场内指令批量导入
                if (obj instanceof Map && !CollectionUtils.isEmpty((Map<String, Object>)obj) 
                    && ((Map<String, Object>)obj).size() == 1) {
                    ((Map<String, Object>)obj).forEach((pageId, dtoList) -> {
                        if (dtoList instanceof List) {
                            doListPreOccupy(pageId, (List)dtoList,
                                BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue());
                        }
                    });
                    continue;
                }
                revokeBackRiskCommon(JsonUtils.object2Map(obj), joinPoint);
            }
        }
        // 清除线程缓存
        cleanLocalCache();
    }

    private void revokeBackRiskCommon(Map<String, Object> mapTrd, JoinPoint joinPoint) {
        // 接口代码
        String infCode = BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue();
        // 是否是需要批量执行的交易方向
        if (listInfPs.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
            logger.info(BATCH_INFO_LOG, infCode);
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }

        // add by etliu 20210902 start
        // 债券多组合指令，走预占释放批量接口
        // ZGZGST-2517:质押式回购多组合走预占释放批量接口 fuyongsheng 20210917 edit start
        if (mapTrd.containsKey("etrInvestBondInfoDtos") || mapTrd.containsKey("etrInvestRepoInfoDtos")) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }
        // ZGZGST-2517:质押式回购多组合走预占释放批量接口 fuyongsheng 20210917 edit end
        // add by etliu 20210902 end

        revokeBackRiskCommon(mapTrd, infCode, joinPoint);
    }

    public interface Func<V, K> {

        void accept(V v, K k);

    }
    public static final <V> void riskListToChildren(List<RiskTrialRetDto> rskList, List<V> dtoList,
                                                    Func<V, List<RiskTrialRetDto>> consumer) {
        if (consumer == null) {
            return;
        }
        if (!StringUtil.isEmptyList(rskList)) {
            Map<Integer, List<RiskTrialRetDto>> groupMap = rskList.stream().collect(
                    Collectors.groupingBy(RiskTrialRetDto::getGroupId));
            for (int index = 0; index < dtoList.size(); index++ ) {
                V dto = dtoList.get(index);
                consumer.accept(dto, groupMap.get(index));
            }
        }
    }

    public  void revokeBackRiskCommonInteface(Map<String, Object> mapTrd,  String simpleName) {
        // 接口代码
        String infCode = BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue();
        // 是否是需要批量执行的交易方向
        if (listInfPs.contains(CommonUtil.findValue(mapTrd, PageFieldToInterfFactory.INF_PS))) {
            logger.info(BATCH_INFO_LOG, infCode);
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }

        // add by etliu 20210902 start
        // 债券多组合指令，走预占释放批量接口
        // ZGZGST-2517:质押式回购多组合走预占释放批量接口 fuyongsheng 20210917 edit start
        if (mapTrd.containsKey("etrInvestBondInfoDtos") || mapTrd.containsKey("etrInvestRepoInfoDtos")) {
            infCode = BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }
        // ZGZGST-2517:质押式回购多组合走预占释放批量接口 fuyongsheng 20210917 edit end
        // add by etliu 20210902 end
        // 设置投管分类等
        setEachAstTypeBySimpleName(mapTrd, simpleName);
        String assetType = StringUtil.EMPTY;
        Object finprodId = mapTrd.get(InterfUtil.finprodId);
        Object finprodType2 = mapTrd.get(InterfUtil.finprodType2);
        // 取得资产类型
        if (StringUtil.isNotEmpty(finprodId)) {
            IAssetInfo assetInfo = assetInfoSrc.getInfoFromRedis(RedisKeyEnum.资产基本信息.key(),
                    finprodId.toString());
            assetType = assetInfo == null ? assetType : assetInfo.getAssetType();
        }
        else if (null != mapTrd.get("assetType")) {
            assetType = (String)mapTrd.get("assetType");
        }
        if (CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷)
                || CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融入)
                || CommonUtil.isType((String)finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融出)
                //债券一级指令不进行预占
                || CommonUtil.isType(assetType, DictCodeListExt2.平安资产类型_一级市场债)) {
            return;
        }
        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        // 事件类型 预占
        interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        // 接口代码
        interfRiskRdsDto.setInfCode(infCode);
        // 资产类型
        interfRiskRdsDto.setAssetType(initRepoLendCommon((String)finprodType2, assetType));
        // 金融产品类型
        interfRiskRdsDto.setFinType(
                finprodType2 != null ? finprodType2.toString() : StringUtil.EMPTY);
        try {
            ClientData result = getInterfRiskRdsService().sendRiskRdsData(mapTrd,
                    interfRiskRdsDto);
            // 调用接口返回错误信息时抛出异常
            if (result != null
                    && StringUtil.equals(result.getHeadField(InterfUtil.std400msgt), InterfUtil.E)
                    && StringUtil.isNotEmpty(result.getHeadField(InterfUtil.stdrtninfo))) {
                throw new BusinessException(
                        InterfUtil.MSG + result.getHeadField(InterfUtil.stdrtninfo));
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 设置每个交易品种过来的类型
     * @param mapTrd
     * @param joinPoint
     */
    private void setEachAstTypeBySimpleName(Map<String, Object> mapTrd, String simpleName) {
        //String simpleName = joinPoint.getTarget().getClass().getSimpleName();
        Object ps = mapTrd.get(InterfUtil.ps);
        if (!StringUtil.isNotEmpty(mapTrd.get(InterfUtil.finprodType2))) {
            mapTrd.put(InterfUtil.finprodType2, getFinprodType2(simpleName, ps));
        }
        //债转股
        if (simpleName.contains("EtrBondToStockService")) {
            mapTrd.put(InterfUtil.ps, DictCodeListExt1.交易方向_债转股.getValue());
        }
        // 拆借
        if (simpleName.contains("EtrLendSpecInfoService")) {
            if (StringUtil.equals(mapTrd.get("assetType1"),
                    BussinessDictCodeList.拆借类型_特殊目的载体.getValue())) {
                mapTrd.put(InterfUtil.finprodType2,
                        BussinessDictCodeList.金融产品类型_投管用_特拆拆入.getValue());
            }
        }
        // 债券借贷
        if (simpleName.contains("EtrSecLendService")) {
            mapTrd.put(InterfUtil.finprodType2, DictCodeListExt1.金融产品类型_投管用_债券借贷.getValue());
        }
    }
}
