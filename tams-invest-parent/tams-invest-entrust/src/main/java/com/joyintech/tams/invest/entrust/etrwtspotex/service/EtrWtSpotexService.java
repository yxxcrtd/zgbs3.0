/*
 * 文件名：EtrWtSpotex001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangguoxian
 * 修改时间：2019年6月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrwtspotex.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.entrust.etroption.dto.EtrOptionDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrWtSpotexDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拆解委托画面端service 〈功能详细描述〉
 *
 * @author wangguoxian
 * @version 1.0 2019年6月25日
 * @see EtrWtSpotexService
 * @since 1.0
 */
@Service
public class EtrWtSpotexService extends CommonExportService<EtrWtSpotexDto> implements BaseProcessService<EtrWtSpotexDto> {

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

    /**
     * 外汇即期委托分页查询 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrWtSpotexDto> selectForPage(Map<String, Object> param) {
        if (StringUtil.isNotEmpty((String)param.get("entrustDate"))) {
            // 写法待定
            List<String> list = StringUtil.splitToList(" ", (String)param.get("entrustDate"));
            // get(0)可能报异常
            param.put("startDate", list.get(0));
            param.put("endDate", list.get(2));
        }
        if (StringUtil.isNotEmpty(param.get("excDealType"))) {
            List<String> excDealTypeList = StringUtil.splitToList(",",
                (String)param.get("excDealType"));
            param.put("excDealTypeList", excDealTypeList);
        }
        if (StringUtil.isNotEmpty(param.get("curPair"))) {
            List<String> curPairList = StringUtil.splitToList(",", (String)param.get("curPair"));
            param.put("curPairList", curPairList);
        }
        if (StringUtil.isNotEmpty(param.get("ps"))) {
            List<String> psList = StringUtil.splitToList(",", (String)param.get("ps"));
            param.put("psList", psList);
        }
        if (StringUtil.isNotEmpty(param.get("entrustStatus"))) {
            List<String> entrustStatusList = StringUtil.splitToList(",",
                (String)param.get("entrustStatus"));
            param.put("entrustStatusList", entrustStatusList);
        }
        param.put("loginUser", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/findPage", param,
            EtrWtSpotexDto.class);

    }

    /**
     * 撤回委托 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public boolean recallDelegate(EtrWtSpotexDto entity) {
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/recall", entity,
            boolean.class);
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
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/close?entrustId=" + entrustId
                                         + "&closeWay?=" + closeWay,
            boolean.class);
    }

    /**
     * 删除委托 <功能详细描述>
     * 
     * @param entrustId
     * @see
     * @since 1.0
     */
    public boolean deleteDelegate(String entrustId) {
        return restTemplate.getForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/delete?entrustId=" + entrustId,
            boolean.class);
    }

    /**
     * 校验方法
     *
     * @see
     * @since 1.0
     */
    public List<ValidMessage> check(EtrWtSpotexDto dto) {
        dto.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
        return restTemplate.postForList(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/check", dto,
            ValidMessage.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrWtSpotexDto dto, OperationType operationType) {
        if (doDeleteFlow(dto.getWorkflowId())){
            dto.setOperateType(operationType.toString());
            Date sysDate = DateUtil.platDateFromCache();
            dto.setEntrustDate(sysDate);
            basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());
            restTemplate.postForObject(
                    ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/doComplete", dto,
                    Integer.class);
        }
    }

    /**
     * 流程发起时调用
     */
    @Override
    public void doInsert(EtrWtSpotexDto dto, OperationType type) {
//        dto.setOperateType(type.toString());
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/doInsert", dto, Integer.class);
        
        if ("submit".equals(dto.getOperateType())) {
            doInsertFlow(dto);
        }
    }

    /**
     * 流程修改时调用
     */
    @Override
    public void doUpdate(EtrWtSpotexDto dto) {
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/doInsert", dto, Integer.class);

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
    public void doBack(EtrWtSpotexDto dto) {
        if (doDeleteFlow(dto.getWorkflowId())){
            restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrOption/doBack",dto,
                    Boolean.class);
        }
    }

    /**
     * 流程作废时调用
     */
    // @Override
    // public void doDelete(String workflowId){
    // restTemplate.getForObject(
    // ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/doDelete?workflowId="+workflowId,
    // Integer.class);
    // }
    /**
     * 修改/查看反显查询 <功能详细描述>
     * 
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrWtSpotexDto selectByKey(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                                         + "/EtrWtSpotex/selectByKey?entrustId=" + entrustId,
            EtrWtSpotexDto.class);
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
        exportParam.config(
            "/com/joyintech/tams/invest/entrust/etrwtspotex/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }

    @Override
    public Page<EtrWtSpotexDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (StringUtil.equals(propName, "tradeFinishAmt")) {
            cell.setCellValue(value + "%");
        }
    }

    /**
     * 组合可用现金查询 <功能详细描述>
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));

        Map<String, Object> cash = restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrWtSpotex/getProAvailableCash", param,
            Map.class);

        return cash;
    }

    /**
     * 查询文件上传详情 <功能详细描述>
     * 
     * @param businessId String
     * @return List<SysUploadFileInfo>
     * @see
     * @since 1.0
     */
    public List<SysUploadFileInfo> selectFileByBusinessId(String businessId) {
        return restTemplate.getForList(ServiceApplication.ENTRUST_SERVICE_NAME
                                       + "/EtrWtSpotex/selectFileByBusinessId?businessId="
                                       + businessId,
            SysUploadFileInfo.class);
    }

    /**
     * 查询授权额度 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectLimitAmt(EtrWtSpotexDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", SecurityUtil.getLoginUser().getUserName());
        map.put("assetType", DictCodeList.平安资产类型_拆借.getValue());
        map.put("tradeMarket", dto.getTradeMarket());
        map.put("ps", dto.getPs());
        return restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/SelectLimitAmt/selectLimitAmt", map,
            BigDecimal.class);
    }

    public List<SelectOptionDto> selectCurAcct(Map<String, Object> pageParam) {
        // TODO Auto-generated method stub
        return restTemplate.postForList(
            ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/selectPtlCapAcctNo", pageParam,
            SelectOptionDto.class);
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        EtrWtSpotexDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
            	 Map<String, Object> params = CommonBusinessService.setParams(
                         dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
                         DictCodeListExt2.平安资产类型_外汇即期.getValue(), null, dto.getTradeMarket(), dto.getPs()
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

    private boolean doInsertFlow(EtrWtSpotexDto dto){
        boolean isSuccess = false;
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();

        try {
            // EtrEntrust
            EtrEntrust etrEntrust = new EtrEntrust();
            BeanUtil.copyProperties(etrEntrust, dto);
            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setIsOutTrade(DictCodeList.是否_是.getValue());
            etrEntrust.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            etrEntrust.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
            entity.setEtrEntrust(etrEntrust);

            // EtrEntrustAdd
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeListExt2.平安资产类型_外汇即期.getValue());
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
