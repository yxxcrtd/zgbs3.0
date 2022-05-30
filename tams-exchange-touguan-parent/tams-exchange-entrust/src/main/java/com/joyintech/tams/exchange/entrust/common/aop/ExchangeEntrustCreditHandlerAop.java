package com.joyintech.tams.exchange.entrust.common.aop;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.entrust.common.service.EtrExchangeCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrautomanagementaccount.dto.EtrAutoManagementAccountDto;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class ExchangeEntrustCreditHandlerAop {

    @Autowired
    private EtrExchangeCommonService etrCommonService;

    private static Logger logger = LoggerFactory.getLogger(ExchangeEntrustCreditHandlerAop.class);


    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.checkSave(..))")
    protected void checkPoint(){
        logger.info("----------委托指令check----------");
    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doUpdate(..))")
    protected void updatePoint() {
        logger.info("----------委托指令update----------");
    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doInsert(..))")
    protected void insertPoint() {

    }


    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..service.*.closeDelegate(..))")
    protected void closeDelegatePoint() {

    }

    /**
     * 流程中退回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..*.*.doBack(..))")
    protected void backPoint() {
    }

    /**
     * 撤回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust..service.*.recallDelegate(..))")
    protected void recallPoint() {
    }

    @AfterReturning(pointcut = "checkPoint()", returning = "ajaxResult")
    public void creditOccupyCheck(JoinPoint joinPoint, AjaxResult ajaxResult) throws BusinessException {
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            return;
        }
        try {
            if (ajaxResult != null){
                Object[] args = joinPoint.getArgs();
                EtrInvestBondInfoDto dto = new EtrInvestBondInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, args[0]);
                if (!(StringUtil.isNotEmpty(dto.getOperateType())&& dto.getOperateType().equals("submit"))) {
                    return;
                }
                Map<String,Object> mapTrd= null;
                if (!(args[0] instanceof List)){
                    mapTrd= JsonUtils.object2Map(args[0]);
                }
                AjaxResult ar;
                Class<?> clazz = args[0].getClass();
                //债券一级指令不影响授信
                if(args[0] instanceof EtrBondFirstLBiddingDto)return;
                if (!(args[0] instanceof EtrAutoManagementAccountDto) && mapTrd != null && mapTrd.get("selectedItem") != null && mapTrd.get("selectedItem") instanceof List<?> && ((List) mapTrd.get("selectedItem")).size() > 0){
                    List<Object> list = new ArrayList<>();
                    for (Object obj : (List<?>)mapTrd.get("selectedItem")){
                        list.add(JSON.parseObject(JSON.toJSONString(obj), clazz));
                    }
                    ar = etrCommonService.executeOccupyCreditLines(list, DictCodeListExt1.交易状态_初始.getValue(),"check");
                }else {
                    ar = etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),"check");
                }
                if (ar != null && !StringUtil.isEmptyList(ar.getErrorList())){
                    ajaxResult.setErrorList(ar.getErrorList());
                    ajaxResult.setMessageType(MessageType.错误消息);
                }
                if (ar != null && StringUtil.isNotEmpty(ar.getData())){
                      ajaxResult.setData(StringUtil.replaceAll(ar.getData().toString(), BussinessConstant.MSG_SPLIT,""));
                      ajaxResult.setMessageType(MessageType.确认消息);
//                    if (StringUtil.equals(ajaxResult.getData().toString(),"请确认是否要提交?")
//                            || StringUtil.equals(ajaxResult.getData().toString(),"请确认是否要保存?")){
//                        ajaxResult.setData(ar.getData());
//                    }else {
//                        ajaxResult.setData(ar.getData() + String.valueOf(ajaxResult.getData()));
//                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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
            //update zhangweiqi 20210119 修改授信begin
            if(args[0] instanceof EtrAutoManagementAccountDto && operationType.equals(OperationType.MODIFY)){
            	etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_撤销.getValue(),"doBack");
            }
          //update zhangweiqi 20210119 修改授信end
            //add by longpeng 债券一级指令多对多导入不占授信 20201228 start
            if(mapTrd.get("elementId") != null && mapTrd.get("elementId").equals("EtrBondFirstLBidding002_btn_submit") ){
            	 return;
            }
          //add by longpeng 债券一级指令多对多导入不占授信 20201228 end
            if (mapTrd.get("elementId") != null && String.valueOf(mapTrd.get("elementId")).endsWith("_btn_submit")
                    && (operationType.equals(OperationType.NEW) || operationType.equals(OperationType.MODIFY))) {
                etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),"doInsert");
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
            //add by longpeng 债券一级指令多对多导入不占授信 20201228 start
            if(mapTrd.get("elementId") != null && mapTrd.get("elementId").equals("EtrBondFirstLBidding002_btn_submit") ){
            	 return;
            }
          //add by longpeng 债券一级指令多对多导入不占授信 20201228 end
            if (StringUtil.isNotEmpty(mapTrd.get("operateType"))&& mapTrd.get("operateType").equals("submit")) {
                etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_初始.getValue(),"doUpdate");
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
    @After("backPoint() || recallPoint()")
    public void creditReleaseMethodBack(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_撤销.getValue(),"doBack");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @After("closeDelegatePoint()")
    public void creditReleaseMethodClose(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        try {
            Object[] args = joinPoint.getArgs();
            etrCommonService.executeOccupyCreditLines(args[0], DictCodeListExt1.交易状态_撤销.getValue(),"close");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
