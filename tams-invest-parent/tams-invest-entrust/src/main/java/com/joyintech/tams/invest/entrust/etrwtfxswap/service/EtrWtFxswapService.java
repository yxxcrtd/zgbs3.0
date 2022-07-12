/*
 * 文件名：EtrWtFxswapService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangzhuangzhuang
 * 修改时间：2019年6月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrwtfxswap.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.entrust.etroption.dto.EtrOptionDto;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrWtSpotexDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etrwtfxswap.dto.EtrWtFxswapDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 外汇掉期委托Service
 * 〈功能详细描述〉
 * @author wangzhuangzhuang
 * @version 1.0 2019年6月18日
 * @see EtrWtFxswapService
 * @since 1.0
 */
@Service
public class EtrWtFxswapService extends CommonExportService<EtrWtFxswapDto> implements BaseProcessService<EtrWtFxswapDto>{
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * 日期相关
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired(required=false)
    private IPlatformDate iPlatformDate;
    /**
     * 项目类委托分页查询 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrWtFxswapDto> selectForPage(Map<String, Object> param) {
        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
            List<String> dateList = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            param.put("startDate", dateList.get(0));
            param.put("endDate", dateList.get(2));
        }
        if(StringUtil.isNotEmpty((String)param.get("portfolioId"))){
            List<String> portfolioIdList = StringUtil.splitToList(",", (String)param.get("portfolioId"));
            param.put("portfolioIdList", portfolioIdList);
        }
        if(StringUtil.isNotEmpty((String)param.get("tradeType"))){
            List<String> tradeTypeList = StringUtil.splitToList(",", (String)param.get("tradeType"));
            param.put("tradeTypeList", tradeTypeList);
        }
        if(StringUtil.isNotEmpty((String)param.get("tradePs"))){
            List<String> tradePsList = StringUtil.splitToList(",", (String)param.get("tradePs"));
            param.put("tradePsList", tradePsList);
        }
        if(StringUtil.isNotEmpty((String)param.get("ccyPair"))){
            List<String> ccyPairList = StringUtil.splitToList(",", (String)param.get("ccyPair"));
            param.put("ccyPairList", ccyPairList);
        }
        if(StringUtil.isNotEmpty((String)param.get("termType"))){
            List<String> termTypeList = StringUtil.splitToList(",", (String)param.get("termType"));
            param.put("termTypeList", termTypeList);
        }
        if(StringUtil.isNotEmpty((String)param.get("entrustStatus"))){
            List<String> entrustStatusList = StringUtil.splitToList(",", (String)param.get("entrustStatus"));
            // 去除空字符串值
            entrustStatusList = entrustStatusList.stream().filter(i->!i.isEmpty()).collect(Collectors.toList());
            param.put("entrustStatusList", entrustStatusList);
        }
        
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/findPage", param,
            EtrWtFxswapDto.class);
    }
    /**
     * 导出
     * 
     * @param exportParam 导出信息
     * @see
     * @since 1.0
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/entrust/etrwtfxswap/conf/export_conf.xml").dataSource(
            DataSourceType.MASERT);
    }
    
    @Override
    public Page<EtrWtFxswapDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals(propName, "tradePercent")){
            cell.setCellValue(new BigDecimal(100).multiply(StringUtil.isNotEmpty(value)?new BigDecimal(value.toString()):BigDecimal.ZERO)+"%");
        }
    }
    /**
     * 校验方法 
     * 
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrWtFxswapDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/check", dto,
            ValidMessage.class);
    }
    
    /**
     * 关闭委托 <功能详细描述>
     * 
     * @param entrustId
     * @param closeWay
     * @see
     * @since 1.0
     */
    public boolean closeDelegate(String entrustId, String closeWay) {
        return restTemplate.getForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/close?entrustId=" + entrustId
                                         + "&closeWay=" + closeWay,
            boolean.class);
    }
    
    /**
     * 撤回委托 <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrWtFxswapDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/recall", dto, boolean.class);
    }
    
    /**
     * 查询修改/查看返显数据 <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrWtFxswapDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                                         + "/EtrWtFxswap/selectByKey?entrustId=" + entrustId,
        EtrWtFxswapDto.class);
    }
    /**
     * 工作流完成
     */
    @Override
    public void doComplete(EtrWtFxswapDto dto, OperationType operationType) {
        if (doDeleteFlow(dto.getWorkflowId())){
            dto.setOperateType(operationType.toString());
            Date sysDate =  CacheUtil.getAppCache(GlobalConstant.NOW_SYS_DATE, Date.class);
            dto.setEntrustDate(sysDate);
            basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
            restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/doComplete", dto,
                    Integer.class);
        }
    }
    /**
     * 
     * 流程发起时调用
     */
     @Override
     public void doInsert(EtrWtFxswapDto dto, OperationType type){
         // GLCP-161 : 【指令查询】外汇掉期流程中数据没有在指令查询里显示 start
         //dto.setOperateType(type.toString());
         // GLCP-161 : 【指令查询】外汇掉期流程中数据没有在指令查询里显示 end
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/doInsert",dto,
             Integer.class);

         if ("submit".equals(dto.getOperateType())) {
             doInsertFlow(dto);
         }
     }

     /**
      * 
      * 流程修改时调用
      */
     @Override
     public void doUpdate(EtrWtFxswapDto dto){
         restTemplate.postForObject(
             ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/doInsert",dto,
             Integer.class);

         if (!StringUtil.equals("revoke", dto.getOperateType())
                 && !StringUtil.equals("delete", dto.getOperateType())) {

             // 执行通用方法删除并重新插入流程指令数据
             boolean isSuccess = doDeleteFlow(dto.getWorkflowId()) && doInsertFlow(dto);

             if(isSuccess){
                 ProcessMark newEntity;
                 try {
                     newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrOptionDto.class, true);
                     newEntity.setWorkflowId(dto.getWorkflowId());
                     newEntity.setStatus(dto.getStatus());
                     newEntity.setBusinessData(JsonUtils.object2Json(dto));
                     // 操作留痕表
                     WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
                 } catch (Exception e){
                     throw new RuntimeException("数据留痕失败", e);
                 }
             }
         }
     }
 
    @Override
    public void doBack(EtrWtFxswapDto dto) {
        doDeleteFlow(dto.getWorkflowId());
    }

    /**
     *
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/delete?entrustId="+entrustId,
            boolean.class);
    }
    /**
     * 组合可用现金查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getProAvailableCash(Map<String, Object> param) {
        Date vdate = DateUtil.parse((String)param.get("vdate"));
        String calId = (String)param.get("calId");
        if(StringUtil.isNotEmpty(calId)){
            List<String> holidayList = dateCalService.getDealCalendarHoliday(DateUtil.parse(DateUtil.format(vdate)),DateUtil.addDay(DateUtil.parse(DateUtil.format(vdate)), 15),calId);
            Date tradeDay = dateCalService.calculationTradingDay(DateUtil.parse(DateUtil.format(vdate)), 1, holidayList, "1");
            param.put("vdate1",DateUtil.format(tradeDay));
        }
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtFxswap/getProAvailableCash",param,
            BigDecimal.class);
    }
    
    
    /**
     * 货币账户查询
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> selectPtlCapAcctNo(Map<String, Object> param) {
        return restTemplate.postForList(
            ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/selectPtlCapAcctNo",param,
            SelectOptionDto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrWtFxswapDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
            	Map<String, Object> params = CommonBusinessService.setParams(
                        dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
                        DictCodeListExt2.平安资产类型_外汇掉期.getValue(), null, dto.getTradeMarket(), dto.getTradePs()
                        // 资产类型改为授权类型 add by renping 2019-08-06  start
                        , null, dto.getWorkflowId()
                        // 资产类型改为授权类型 add by renping 2019-08-06  end
                );
                return commonBusinessService.是否需要上级审批_委托用(params);
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }

    private boolean doInsertFlow(EtrWtFxswapDto dto){
        boolean isSuccess = false;
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        try {
            // EtrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            if(iPlatformDate != null){
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
            etrEntrust.setPs(dto.getTradePs());
            etrEntrust.setCcy(dto.getTradeCcy());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            etrEntrust.setEntrustAmt(dto.getTradeAmt());
            if(StringUtil.isNotEmpty(dto.getTradeCcyAvailablePosition())){
                etrEntrust.setProAvailableCash(dto.getTradeCcyAvailablePosition());
            }
            if(StringUtil.isNotEmpty(dto.getSwapsPoint())){
                etrEntrust.setPrice(dto.getSwapsPoint());
            }
            entity.setEtrEntrust(etrEntrust);

            // EtrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setEntrustId(dto.getEntrustId());
            etrEntrustAdd.setExcDealType(dto.getTradeType());
            etrEntrustAdd.setCurPair(dto.getCcyPair());
            etrEntrustAdd.setCorCcy(dto.getContraCcy());
            etrEntrustAdd.setDealCurAcct(dto.getTradeCcyAcct());
            etrEntrustAdd.setCorCurAcct(dto.getContraCcyAcct());
            etrEntrustAdd.setExcFxsTermType(dto.getTermType());
            etrEntrustAdd.setAssetType(DictCodeListExt2.平安资产类型_外汇掉期.getValue());

            if(StringUtil.isNotEmpty(dto.getProximalDeliveryDate())){
                List<String> proximalDeliveryDateList = StringUtil.splitToList(" ", dto.getProximalDeliveryDate());
                etrEntrustAdd.setVdate(DateUtil.parse(proximalDeliveryDateList.get(0)));
                etrEntrustAdd.setVdate2(DateUtil.parse(proximalDeliveryDateList.get(2)));
            }else{
                etrEntrustAdd.setVdate(null);
                etrEntrustAdd.setVdate2(null);
            }
            if(StringUtil.isNotEmpty(dto.getRemoteDeliveryDate())){
                List<String> remoteDeliveryDateList = StringUtil.splitToList(" ", dto.getRemoteDeliveryDate());
                etrEntrustAdd.setMdate(DateUtil.parse(remoteDeliveryDateList.get(0)));
                etrEntrustAdd.setMdate2(DateUtil.parse(remoteDeliveryDateList.get(2)));
            }else{
                etrEntrustAdd.setMdate(null);
                etrEntrustAdd.setMdate2(null);
            }
            entity.setEtrEntrustAdd(etrEntrustAdd);

            // 附件 MstFileInputParameter
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            entity.setMstFileInputParameter(mstFileInputParameter);

            // WorkflowId
            entity.setWorkFlowId(dto.getWorkflowId());

            isSuccess = restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                    Boolean.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 对象转换赋值失败
            throw new RuntimeException("对象转换赋值失败", e);
        }
        return isSuccess;
    }

    private boolean doDeleteFlow(String workflowId) {
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", workflowId);
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
                Boolean.class);
    }
    
}
