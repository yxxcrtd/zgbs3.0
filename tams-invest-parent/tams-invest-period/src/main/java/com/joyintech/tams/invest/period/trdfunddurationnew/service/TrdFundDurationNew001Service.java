/*
 * 文件名：TrdFundDurationNew001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdfunddurationnew.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdfunddurationnew.dto.TrdFundDurationNew001Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.setWorkflowId;

/**
 * 〈一句话功能简述〉
 * 基金存续期改造
 * @author sjj
 * @version 1.0 2021年03月02日
 * @see TrdFundDurationNew001Service
 * @since 1.0
 */
@Service
public class TrdFundDurationNew001Service extends CommonExportService<TrdFundDurationNew001Dto>
implements BaseProcessService<TrdFundDurationNew001Dto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdFundDurationNew001Service.class);

    private final String 非货币总集复核_BTN = "TrdFundDurationNew002_btn_total";
    /**
     * 业务编号
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    @Autowired
    private TrdFundDurationDealNewEntity dealEntityService;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    /**
     * 业务共通组件.资产头寸现金持仓共通组件
     */
    @Autowired
    private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;
    @Autowired
    private PeriodCommonService periodCommonService;
    /**
     * 划款指令共通
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;

    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 获取资产代码
     * <功能详细描述>
     * @return 资产代码
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodMarketId() {
        Map<String, Object> params = RequestUtil.getPageParam();
        params.put("finprodType2",
            DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/getFinprodMarketId",
            params, SelectOptionDto.class);
    }

    /**
     * 工作流doInsert
     * @param entity 业务entity
     * @param type 操作类型
     */
    @Override
    public void doInsert(TrdFundDurationNew001Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
    	if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }
        dealEntityService.saveDataWhenStartWorkflow(entity, type, DictCodeListExt1.交易流程状态_流程中.getValue(), false);
    }

    /**
     * 工作流doUpdate
     * @param entity entity 业务entity
     */
    @Override
    public void doUpdate(TrdFundDurationNew001Dto entity) {
//    	baseTradeProductDealCommServiceImpl.delete(entity.getWorkflowId());
        entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdFundDurationNew001Dto.class, true);
            // 工作流ID
            newEntity.setWorkflowId(entity.getWorkflowId());
            newEntity.setStatus(entity.getStatus());
            newEntity.setBusinessData(JsonUtils.object2Json(entity));
            // 更新业务表数据
            // 操作留痕表
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue(), false);
    }

    /**
     * 工作流删除
     * @param workflowId
     */
    @Override
    public void doDelete(String workflowId) {
        TrdFundDurationNew001Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        if(StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_现金分红.getValue())){
                //&& StringUtil.equals(entity.getBaseTotalTradeInfoDto().getTrdFinProductDealTotal().getTradeStatus(),DictCodeListExt1.交易状态_初始.getValue())){
            entity.setWorkflowId(null);
            entity.setbTradeId(entity.getTotleTradeId());
            getWorkFlowPatameter(entity);
            List<FinAssetIntInfo> listFinAssetIntInfo= entity.getFinAssetIntInfoList();
            listFinAssetIntInfo = listFinAssetIntInfo.stream().filter(finAssetIntInfo -> StringUtil.equals(finAssetIntInfo.getBonusMode(), DictCodeList.分红方式_现金分红.getValue())).collect(Collectors.toList());
            entity.setFinAssetIntInfoList(listFinAssetIntInfo);
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            entity.getBaseTotalTradeInfoDto().setIsNeedStlPayOrders(true);
            entity.setOperateType(BussinessConstant.SUBMIT);
            dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue(), true);
        }
    }
    /**
     * 工作流完成
     */
    @Override
    public void doComplete(TrdFundDurationNew001Dto entity, OperationType type) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == type) {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销, false);
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_有效, false);
        }
        entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        if(StringUtil.isEmpty(entity.getPstype())){
            entity.setPs(DictCodeList.交易方向_除权登记.getValue());
        }else if(StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_现金分红.getValue())){
            entity.setPs(DictCodeList.交易方向_现金分红.getValue());
        }else if(StringUtil.equals(entity.getPstype(),DictCodeList.分红方式_红利再投.getValue())){
            entity.setPs(DictCodeList.交易方向_红利再投.getValue());
        }
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/doComplete", entity,
            String.class);

        if(StringUtil.equals(CacheUtil.getSysParam("ISChongQing"),DictCodeList.是否_是.getValue())){
            return;
        }
        //    除权登记生成总集复核数据
        if(OperationType.REVOKE != type && StringUtil.isEmpty(entity.getbTradeId())){
//        设置工作流代码
        entity.setWorkflowId(null);
        Boolean bonusTypeXjfh = false;
        Boolean bonusTypeHlzt = false;
        for(FinAssetIntInfo finAssetIntInfo : entity.getFinAssetIntInfoList()){
             if(StringUtil.equals(DictCodeList.分红方式_现金分红.getValue(),finAssetIntInfo.getBonusMode())){
                 bonusTypeXjfh = true;
                 break;
             }
        }
        for(FinAssetIntInfo finAssetIntInfo : entity.getFinAssetIntInfoList()){
            if(StringUtil.equals(entity.getAssetType(),DictCodeList.基金类资产类型_非货币基金.getValue())
                    && StringUtil.equals(DictCodeList.分红方式_红利再投.getValue(),finAssetIntInfo.getBonusMode())){
                bonusTypeHlzt = true;
                break;
            }
        }
        entity.setbTradeId(entity.getTotleTradeId());
        List<FinAssetIntInfo> listFinAssetIntInfo= entity.getFinAssetIntInfoList();
        if(bonusTypeXjfh){
            getWorkFlowPatameter(entity);
            entity.setPstype(DictCodeList.分红方式_现金分红.getValue());
            List<FinAssetIntInfo> listFinAssetIntInfotemp1= listFinAssetIntInfo;
            listFinAssetIntInfotemp1 = listFinAssetIntInfotemp1.stream().filter(finAssetIntInfo -> StringUtil.equals(finAssetIntInfo.getBonusMode(), DictCodeList.分红方式_现金分红.getValue())).collect(Collectors.toList());
            entity.setFinAssetIntInfoList(listFinAssetIntInfotemp1);
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            entity.getBaseTotalTradeInfoDto().setIsNeedStlPayOrders(true);
            Boolean isNotNeedPosition = true;
            if(StringUtil.isNotEmpty(entity.getFinAssetIntInfoList().get(0).getSecAcctId())){
                isNotNeedPosition = false;
            }
            dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue(), isNotNeedPosition);
        }
        if(bonusTypeHlzt){
//            非货币红利再投
            entity.setElementId("TrdFundDurationNew002_btn_save");
            WorkFlowParameter<TrdFundDurationNew001Dto> param = getWorkFlowPatameter(entity);
            entity.setPstype(DictCodeList.分红方式_红利再投.getValue());
            List<FinAssetIntInfo> listFinAssetIntInfotemp2= listFinAssetIntInfo;
            listFinAssetIntInfotemp2 = listFinAssetIntInfotemp2.stream().filter(finAssetIntInfo -> StringUtil.equals(finAssetIntInfo.getBonusMode(), DictCodeList.分红方式_红利再投.getValue())).collect(Collectors.toList());
            entity.setFinAssetIntInfoList(listFinAssetIntInfotemp2);
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            BigDecimal sumtradeAmt = BigDecimal.ZERO;
            BigDecimal sumRegistShare = BigDecimal.ZERO;
            for(FinAssetIntInfo finAssetIntInfo : entity.getFinAssetIntInfoList()){
                sumtradeAmt = sumtradeAmt.add(finAssetIntInfo.getTradeAmt());
                sumRegistShare = sumRegistShare.add(finAssetIntInfo.getRegistShare());
            }
            entity.setRegistShare(sumRegistShare);
            entity.setTradeAmt(sumtradeAmt);
            BigDecimal unit = periodCommonService.getLatestValuation(entity.getFinprodId(), DateUtil.format(entity.getExRightsDate()));
            entity.setQty(ArithmeticUtil.divide(entity.getTradeAmt(), unit == null ? BigDecimal.ONE:unit, 4));
            periodCommonService.autoDealQtyForSubmit(entity.getFinAssetIntInfoList(),entity.getRegistShare(),entity.getQty());
            entity.setCreateUser(entity.getFinAssetIntInfoList().get(0).getCreateUser());
            try {
                //启动流程
                WorkFlowUtil.startProcess(param,"","", entity.getCreateUser(),OperationType.NEW,false,entity);
                //设置为提交 插入流程表
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
    }

    /**
     * 设置流程所需parameter
     * @param dto 流程dto
     * @return 构建的dto
     */
    public WorkFlowParameter<TrdFundDurationNew001Dto> getWorkFlowPatameter(TrdFundDurationNew001Dto dto){
        WorkFlowParameter<TrdFundDurationNew001Dto> parameter= new WorkFlowParameter<>();
        parameter.setPageId("TrdFundDurationNew002");
        parameter.setServiceName(WorkFlowUtil.getServiceName(this.getClass()));
        parameter.setElementId(dto.getElementId());
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        parameter.setBusinessCode(dto.getTotleTradeId());
        parameter.setBusinessType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        parameter.setComment(entity -> WorkFlowUtil.concatComment(
                this.getMessage("TrdFundDurationNew001.finprodName"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodMarketName()
                        + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDurationNew001.assetType"),
                GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(
                        DictCode.基金类资产类型, entity.getAssetType()) + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDurationNew001.tradeId"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getTotleTradeId()
                        + GlobalConstant.CLOSE_PARENTHESIS));
        return parameter;
    }
    /**
     * 获取组合代码
     * 组合代码获取功能
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getPortfolioId() {
        Map<String,Object> map = RequestUtil.getPageParam();
        if(map.get("initValue") == null){
            map.put("userId", LoginUserUtil.getUserName());
        }
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getPortfolioId",
            map, SelectOptionDto.class);
    }

    /**
     * 画面查询
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdFundDurationNew001Dto> findPage(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = pageParam.get("chlId");
        if (chlFinprodIdObj != null){
            pageParam.put("chlIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA,(String) chlFinprodIdObj));
        }else {
            pageParam.put("chlIdList",new ArrayList<>());
        }
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findPage", pageParam,
            TrdFundDurationNew001Dto.class);
    }
    /**
     * @Description: 构建导出参数
     * @param: [exportParam 参数]
     * @author: 丁志鹏
     * @Date: 2018/11/23
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            exportParam.config(
                    "/com/joyintech/tams/invest/period/trdfunddurationnew/conf/export_conf_ls.xml").dataSource(
                    DataSourceType.MASERT);
        }else{
            exportParam.config(
                    "/com/joyintech/tams/invest/period/trdfunddurationnew/conf/export_conf.xml").dataSource(
                    DataSourceType.MASERT);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdFundDurationNew001Dto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }
    /**
     * 资产代码联动查询
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 画面查询结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> finprodMarketIdLink(Map<String, Object> pageParam) {
        pageParam.put("FINPRODTYPE2_F06",DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/finprodMarketIdLink", pageParam, Map.class);
    }

    /**
     * 组合代码联动查询
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 画面查询结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> portfolioIdLink(Map<String, Object> pageParam) {
        pageParam.put("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/portfolioIdLink", pageParam, Map.class);
    }
    /**
     * 登记份额查询
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 登记份额
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> calRegistShare(Map<String, Object> pageParam) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/calRegistShare", pageParam, Map.class);
    }

    /**
     * 获取有效或者撤销的数据
     * @param param
     * @return
     */
    public TrdFundDurationNew001Dto findEffectiveData(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findEffectiveData", param, TrdFundDurationNew001Dto.class);
    }
    /**
     * 获取现金分红流程中
     * @param param 查询参数
     * @return 匹配结果
     */
    public TrdFundDurationNew001Dto findCashDividend(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findCashDividend", param, TrdFundDurationNew001Dto.class);
    }
    /**
     * 统计是否有后续交易
     * @param map 参数
     * @return 统计数
     */
    public int countFollowDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/countFollowDeal", map, Integer.class);
    }

    /**
     * 处理导出参数，格式化导出的格式
     * @param beanName beanName
     * @param cell 单元格
     * @param propName 配置
     * @param value 值
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals("registDate", propName)||StringUtil.equals("exRightsDate", propName)
                ||StringUtil.equals("arriveDate", propName)||StringUtil.equals("reInvesDate", propName)) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }
    /**
     *
     * 获取利息明细信息
     * <功能详细描述>
     * @param pageParam 界面参数
     * @return 查询到的结果
     * @see
     * @since 1.0
     */
    public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> pageParam) {
    	pageParam.put("userName", LoginUserUtil.getUserName());
        List<FinAssetIntInfo> finAssetIntInfos = restTemplate.postForList(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findAssetIntInfo", pageParam,
                FinAssetIntInfo.class);
    	return finAssetIntInfos;
    }

    /**
     * 以组合维度生成dto，就是以利息明细维度组装新的dto，作为属性保存到流程entity中<br/>
     * 汇总 <br/>
     * 汇总扩展 <br/>
     * 汇总费用明细 <br/>
     * List<Dto> | 交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 附属 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 扩展 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 费用明细 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| 组合交易 <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| List<划款指令>
     */
	public BaseTotalTradeInfoDto genFinProductTradeDto(TrdFundDurationNew001Dto entity) {
		BaseTotalTradeInfoDto result = dealEntityService.genBaseTotalTradeInfoDto(entity);
        return result;
	}
    /**
     * 根据交易编号删除流程表中数据
     * <功能详细描述>
     * @param tradeId 交易代码
     * @see
     * @since 1.0
     */
    public int deleteWflByTradeId(String tradeId) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/deleteWflByTradeId",tradeId , Integer.class);
    }

    @Override
    public void doBack(TrdFundDurationNew001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    @Override
    public void doRevoke(TrdFundDurationNew001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    /**
     * 统计是否有流程中的分红交易
     * @param map 参数
     * @return 统计数
     */
    public int countFollowFHDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/countFollowFHDeal", map, Integer.class);
    }
}
