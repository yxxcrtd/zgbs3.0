package com.joyintech.tams.cfets.etr.aop;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrRepoSaveRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrautomanagementaccount.dto.EtrAutoManagementAccountDto;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrIbBondinfo002Dto;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtrIbRepoinfo002Dto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class CfetsEntrustCreditHandlerAop {

    @Autowired
    private EtrCommonService etrCommonService;

    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.checkSaveError(..))")
    protected void checkPoint(){

    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doUpdate(..))")
    protected void updatePoint() {

    }

    /**
     * 保存至代办，从代办中提交到流程中，需要占用授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doInsert(..))")
    protected void insertPoint() {

    }


    @Pointcut("execution(* com.joyintech.tams.cfets.etr..service.*.closeDelegate(..))")
    protected void closeDelegatePoint() {

    }

    /**
     * 流程中退回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doBack(..))")
    protected void backPoint() {
    }

    /**
     * 撤回操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..service.*.recallDelegate(..))")
    protected void recallPoint() {
    }

    @AfterReturning(pointcut = "checkPoint()", returning = "ajaxResultMessage")
    public void creditOccupyCheck(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        AjaxResult ajaxResult=ajaxResultMessage.getData();
        String code=ajaxResultMessage.getCode();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            return;
        }
        try {
            if (ajaxResult != null){
                Object[] args = joinPoint.getArgs();
                CfetsEtribSaveRequest saveReq = new CfetsEtribSaveRequest();
                BeanUtil.copyPropertiesIgnoreNull(saveReq, args[0]);
                if(StringUtil.isNotEmpty(saveReq)
                        &&  StringUtil.equals(saveReq.getCreditIsContinue(),"Y")){
                    return;
                }
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
                    if(args[0] instanceof CfetsEtribSaveRequest){
//                        CfetsEtribSaveRequest pageInputDto = (CfetsEtribSaveRequest) args[0];
                        CfetsEtribSaveRequest pageInputDto = new CfetsEtribSaveRequest();
                        BeanUtil.copyPropertiesIgnoreNull(pageInputDto, args[0]);
                        //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
                        if(StringUtil.isNotEmpty(pageInputDto.getTradeAmt())){
                            pageInputDto.setEntrustNum(ArithmeticUtil.divide(pageInputDto.getTradeAmt(),new BigDecimal(10000),8));
                        }
                        if(StringUtil.isNotEmpty(pageInputDto.getEntrustOwnNum())){
                            pageInputDto.setEntrustOwnNum(ArithmeticUtil.divide(pageInputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
                        }
                        if(StringUtil.isNotEmpty(pageInputDto.getEntrustIntegrateNum())){
                            pageInputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(pageInputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));
                        }
                        if(StringUtil.isNotEmpty(pageInputDto.getEntrustAmt())){
                            pageInputDto.setEntrustAmt(ArithmeticUtil.divide(pageInputDto.getEntrustAmt(),new BigDecimal(10000),8));
                        }
                        EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
                        BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
                        inputDto.setVdate(pageInputDto.getEtrDate());
                        ar = etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_初始.getValue(),"check");
                    }else if(args[0] instanceof CfetsEtrRepoSaveRequest){
                        //正回购不占授信  逆回购无券 也不占用授信
                        CfetsEtrRepoSaveRequest pageInputDto = new CfetsEtrRepoSaveRequest();
                        BeanUtil.copyPropertiesIgnoreNull(pageInputDto, args[0]);
                        EtrIbRepoinfo002Dto inputDto = new EtrIbRepoinfo002Dto();
                        BeanUtil.copyPropertiesIgnoreNull(inputDto, args[0]);
                        inputDto.setVdate(pageInputDto.getEntrustDate());
                        ar = etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_初始.getValue(),"check");
                    }
                    else {
                        ar =null;
                    }
                }
                if (ar != null && !StringUtil.isEmptyList(ar.getErrorList())){
                        AjaxResult errorRs = new AjaxResult();
                        errorRs.setErrorList(ar.getErrorList());
                        ajaxResultMessage.setData(errorRs);

                }
                if (ar != null && StringUtil.isNotEmpty(ar.getData())){
                        AjaxResult m=new AjaxResult();
                        String ms=ar.getData().toString().replaceAll(BussinessConstant.MSG_SPLIT,GlobalConstant.VERTICAL_LINE);
                        m.setMessage(ms, MessageType.确认消息);
                        ajaxResultMessage.setData(m);
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
                CfetsEtribBondInfo003Dto pageInputDto = (CfetsEtribBondInfo003Dto) args[0];
                 EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
                BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
                //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
                inputDto.setEntrustNum(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(10000),8));
                inputDto.setEntrustOwnNum(ArithmeticUtil.divide(inputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
                inputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(inputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));

                etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_撤销.getValue(),"doBack");
            }
          //update zhangweiqi 20210119 修改授信end
            //add by longpeng 债券一级指令多对多导入不占授信 20201228 start
            if(mapTrd.get("elementId") != null && mapTrd.get("elementId").equals("EtrBondFirstLBidding002_btn_submit") ){
            	 return;
            }
          //add by longpeng 债券一级指令多对多导入不占授信 20201228 end
            if (mapTrd.get("elementId") != null && String.valueOf(mapTrd.get("elementId")).endsWith("_btn_submit")
                    && (operationType.equals(OperationType.NEW) || operationType.equals(OperationType.MODIFY))) {

                CfetsEtribBondInfo003Dto pageInputDto = (CfetsEtribBondInfo003Dto) args[0];
                if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),pageInputDto.getAssetType())){
                    return ;
                }
               EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
                BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
                //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
                inputDto.setEntrustNum(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(10000),8));
                inputDto.setEntrustOwnNum(ArithmeticUtil.divide(inputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
                inputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(inputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));

                etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_初始.getValue(),"doInsert");
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
                if(!(args[0] instanceof CfetsEtribBondInfo003Dto)) return;
                CfetsEtribBondInfo003Dto pageInputDto = (CfetsEtribBondInfo003Dto) args[0];
                if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),pageInputDto.getAssetType())){
                    return ;
                }
                EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
                BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
                //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
                inputDto.setEntrustNum(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(10000),8));
                inputDto.setEntrustOwnNum(ArithmeticUtil.divide(inputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
                inputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(inputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));

                etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_初始.getValue(),"doUpdate");
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
            CfetsEtribBondInfo003Dto pageInputDto = (CfetsEtribBondInfo003Dto) args[0];
            if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),pageInputDto.getAssetType())){
                return ;
            }
           EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
            BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
            //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
            inputDto.setEntrustNum(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(10000),8));
            inputDto.setEntrustOwnNum(ArithmeticUtil.divide(inputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
            inputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(inputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));

            etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_撤销.getValue(),"doBack");
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
            CfetsEtribBondInfo003Dto pageInputDto = (CfetsEtribBondInfo003Dto) args[0];
            if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),pageInputDto.getAssetType())){
                return ;
            }
           EtrIbBondinfo002Dto inputDto = new EtrIbBondinfo002Dto();
            BeanUtil.copyPropertiesIgnoreNull(inputDto, pageInputDto);
            //后续处理针对界面券面总额等字段乘10000，此时需换算成万元
            inputDto.setEntrustNum(ArithmeticUtil.divide(inputDto.getEntrustNum(),new BigDecimal(10000),8));
            inputDto.setEntrustOwnNum(ArithmeticUtil.divide(inputDto.getEntrustOwnNum(),new BigDecimal(10000),8));
            inputDto.setEntrustIntegrateNum(ArithmeticUtil.divide(inputDto.getEntrustIntegrateNum(),new BigDecimal(10000),8));

            etrCommonService.executeOccupyCreditLines(inputDto, DictCodeListExt1.交易状态_撤销.getValue(),"close");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
