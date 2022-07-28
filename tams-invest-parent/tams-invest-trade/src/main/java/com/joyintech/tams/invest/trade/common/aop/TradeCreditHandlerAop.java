package com.joyintech.tams.invest.trade.common.aop;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static com.joyintech.tams.constant.DictCodeListExt1.金融产品类型_投管用_债券质押式正回购;
import static com.joyintech.tams.constant.DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购;

@Component
@Aspect
public class TradeCreditHandlerAop {

    @Autowired
    private TradeCommonService tradeCommonService;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.check(..))")
    protected void checkPoint(){

    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doUpdate(..))")
    protected void updatePoint() {

    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doInsert(..))")
    protected void insertPoint() {

    }


    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doComplete(..))")
    protected void completePoint() {

    }

    /**
     * 退回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..*.*.doBack(..))")
    protected void backPoint() {
    }

    /**
     * 交易员拒收、退回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade..service.*.returnEntrust(..))")
    protected void returnEntrust() {
    }

    @AfterReturning(pointcut = "checkPoint()", returning = "ajaxResult")
    public void creditOccupyCheck(JoinPoint joinPoint, AjaxResult ajaxResult) throws BusinessException {
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            return;
        }
        if (ajaxResult != null){
            try {
                Object[] args = joinPoint.getArgs();
                TrdBondInvestTradeDto dto = new TrdBondInvestTradeDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, args[0]);
                if ("delete".equals(dto.getOperateType()) || "confirm".equals(dto.getOperateType()) || "revoke".equals(dto.getOperateType())) {
                    return;
                }
                
             // add by etliu 20210129 start
                // 选择的成交编号存在对应的报价，则不影响风险
                if (StringUtil.isNotBlank(dto.getfTradeId())
                    && StringUtil.equals(DictCodeList.是否_是.getValue(),
                        tradeCommonService.checkExecIdHasQuote(dto.getfTradeId()))) {
                    return;
                }
             // add by etliu 20210129 end
                
                AjaxResult ar = tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"check");
                if (ar != null && !StringUtil.isEmptyList(ar.getErrorList())){
                    ajaxResult.setErrorList(ar.getErrorList());
                }
                if (ar != null && StringUtil.isNotEmpty(ar.getData())){
                    if (StringUtil.equals(ajaxResult.getData().toString(),"请确认是否要提交?")
                            || StringUtil.equals(ajaxResult.getData().toString(),"请确认是否要保存?")){
                        ajaxResult.setData(ar.getData());
                    }else {
                        ajaxResult.setData(ar.getData() + String.valueOf(ajaxResult.getData()));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 买入、申购等交易方向提交到流程中时需要占用授信，操作类型submit
     * @param joinPoint
     * @throws BusinessException
     */
    @After("insertPoint()")
    public void creditOccupyMethodInsert(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            OperationType operationType = (OperationType) args[1];
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
            
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
            if (StringUtil.isNotEmpty(mapTrd.get("operateType"))&& mapTrd.get("operateType").equals("submit") && (operationType.equals(OperationType.NEW)||operationType.equals(OperationType.MODIFY))) {
                tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"doInsert");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 买入、申购等交易方向保存到待办事项，从待办事项提交到流程中时需要占用授信，操作类型submit
     * @param joinPoint
     * @throws BusinessException
     */
    @After("updatePoint()")
    public void creditOccupyMethodUpdate(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
            
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
            if (StringUtil.isNotEmpty(mapTrd.get("operateType"))&& mapTrd.get("operateType").equals("submit")) {
                tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"doUpdate");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 买入、申购等交易方向从流程中退回时需要释放授信
     * @param joinPoint
     * @throws BusinessException
     */
    @After("backPoint()")
    public void creditReleaseMethodBack(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
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
            tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_撤销.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"doBack");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 卖出生效释放授信、买入撤销生效释放授信和卖出撤销生效占用授信
     */
    @After("completePoint()")
    public void creditOccupyOrReleaseMethod(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            OperationType operationType = (OperationType) args[1];
            
            // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
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
            
            if (operationType.equals(OperationType.REVOKE)||operationType.equals(OperationType.DELETE)) {
                ManualSettleDto settleDto = (ManualSettleDto)args[0];
                if(StringUtil.equals(金融产品类型_投管用_债券质押式正回购.getValue(), CommonUtil.findValue(mapTrd, "finprodType2")) ||
                   StringUtil.equals(金融产品类型_投管用_债券质押式逆回购.getValue(),CommonUtil.findValue(mapTrd, "finprodType2"))) {//针对协议回购交收失败
                    settleDto.setSettleAmt(new BigDecimal(CommonUtil.findValue(mapTrd, "tradedVolume").toString()));
                    settleDto.setOrderId((String)CommonUtil.findValue(mapTrd, "iOrderId"));
                }
                tradeCommonService.executeOccupyCreditLines(settleDto, DictCodeListExt1.交易状态_撤销.getValue(),DictCodeListRsk.授信占用类别_实占.getValue(),"doComplete");
                //TODO 这里需要处理将首期实占重新占回,这个逻辑只在协议回购中存的到期续作中存在
                if(StringUtil.equals(mapTrd.get("busiType"),"09") || StringUtil.equals(mapTrd.get("busiType"),"15") ||
                        StringUtil.equals(mapTrd.get("busiType"),"20") || StringUtil.equals(mapTrd.get("busiType"),"24")) {
                    FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
                    finTradeProductKey.setBranch(0L);
                    finTradeProductKey.setFinprodId(settleDto.getFinprodId());
                    FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
                    if(Objects.nonNull(finTradeProduct)) {
                        settleDto.setSettleAmt(finTradeProduct.getPrin());
                        settleDto.setCounterId(finTradeProduct.getCounterId());
                    }
                    settleDto.setOrderId((String)CommonUtil.findValue(mapTrd, "entrustNo"));
                    tradeCommonService.executeOccupyCreditLines(settleDto, DictCodeListExt1.交易状态_初始.getValue(),DictCodeListRsk.授信占用类别_预占.getValue(),"doComplete");
                }
            }else {
                tradeCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(), DictCodeListRsk.授信占用类别_实占.getValue(),"doComplete");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @After("returnEntrust()")
    public void creditReleaseMethodClose(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            Map<String,Object> mapTrd= JsonUtils.object2Map(args[0]);
            tradeCommonService.executeOccupyCreditLines(mapTrd.get("entrustIds"), DictCodeListExt1.交易状态_撤销.getValue(),DictCodeListRsk.授信占用类别_预占.getValue(),"close");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
