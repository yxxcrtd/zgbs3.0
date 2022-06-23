/*
 * 文件名：TrdFundDuration001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdfundduration.service;

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
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdfundduration.dto.TrdFundDuration001Dto;
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
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年10月15日
 * @see TrdFundDuration001Service
 * @since 1.0
 */
@Service
public class TrdFundDuration001Service extends CommonExportService<TrdFundDuration001Dto>
implements BaseProcessService<TrdFundDuration001Dto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdFundDuration001Service.class);

    private final String 非货币总集复核_BTN = "TrdFundDuration002_btn_total";
    /**
     * 业务编号
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    @Autowired
    private TrdFundDurationDealEntity dealEntityService;
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
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/getFinprodMarketId",
            params, SelectOptionDto.class);
    }

    /**
     * 工作流doInsert
     * @param entity 业务entity
     * @param type 操作类型
     */
    @Override
    public void doInsert(TrdFundDuration001Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && com.joyintech.framework.util.StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
    	if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {

    	    if(StringUtil.isNotEmpty(entity.getTotleTradeId())) {
                periodCommonService.deleteWflByTradeId(entity.getTotleTradeId());
            }
            setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }
        dealEntityService.saveDataWhenStartWorkflow(entity, type, DictCodeListExt1.交易流程状态_流程中.getValue(), false);
    }

    /**
     * 工作流doUpdate
     * @param entity entity 业务entity
     */
    @Override
    public void doUpdate(TrdFundDuration001Dto entity) {
        if("03".equals(entity.getType()) && StringUtil.isNotEmpty(entity.getBussTrdTransferOrder())){
            for (BussTrdTransferOrderDto dto : entity.getBussTrdTransferOrder()){
                AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), dto.getOrderId());
            }
        }else{
            AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        }
//    	baseTradeProductDealCommServiceImpl.delete(entity.getWorkflowId());
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            periodCommonService.deleteWflByTradeId(entity.getTotleTradeId());
        }

        entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && com.joyintech.framework.util.StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdFundDuration001Dto.class, true);
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
        TrdFundDuration001Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        if(StringUtil.equals(entity.getBonusMode(),DictCodeList.分红方式_现金分红.getValue())
                && StringUtil.equals(entity.getBaseTotalTradeInfoDto().getTrdFinProductDealTotal().getTradeStatus(),DictCodeListExt1.交易状态_初始.getValue())){
            entity.setWorkflowId(null);
            entity.setType(StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_非货币基金.getValue())?"03":"06");
            getWorkFlowPatameter(entity);
//            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue(), true);
        }
    }
    /**
     * 工作流完成
     */
    @Override
    public void doComplete(TrdFundDuration001Dto entity, OperationType type) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE != type) {
            if (StringUtil.equals(entity.getType(), "02") || StringUtil.equals(entity.getType(), "05")) {
                entity.setTypeFlag("01");
            } else {
                entity.setTypeFlag("02");
            }
        }
////        根据流程操作类型处理基础entity
//        if (OperationType.REVOKE == type) {
//            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
//            entity.setOperateType(BussinessConstant.REVOKE);
//        }
//        else {
//            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
//        }
////        构造划款指令信息
//        dealEntityService.generateOrder(entity);
//        tradeDto.setNeedSum(true);
//        baseTradeProductDealCommServiceImpl.execute(tradeDto);
//        // 插入Entity对应的业务流程DB中, 并删除流程表中的数据
//        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/doComplete", entity, String.class);
////        除权登记生成总集复核数据
//        if(OperationType.REVOKE != type&&StringUtil.equals(entity.getType(),"02")){
////            设置工作流代码
//            entity.setWorkflowId(null);
//            if(StringUtil.equals(entity.getBonusMode(),DictCodeList.分红方式_现金分红.getValue())){
////              非货币现金分红
//                entity.setType("03");
////               处理数据参数
//                getWorkFlowPatameter(entity);
//                dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW);
//            }
//            else{
////                非货币红利再投
//                entity.setType("04");
//                entity.setElementId("TrdFundDuration002_btn_save");
//                WorkFlowParameter<TrdFundDuration001Dto> param = getWorkFlowPatameter(entity);
//                try {
////          启动流程
//                    WorkFlowUtil.startProcess(param,"","",entity.getTrader(),OperationType.NEW,false,entity);
////              设置为提交 插入流程表
//                } catch (Exception e) {
//                }
//            }
//        }
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == type) {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销, false);
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_有效, false);
        }
        entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/doComplete", entity,
            String.class);
        if(StringUtil.equals(CacheUtil.getSysParam("ISChongQing"),DictCodeList.是否_是.getValue())) {
            return;
        }
//    除权登记生成总集复核数据
    if(OperationType.REVOKE != type&&(StringUtil.equals(entity.getType(),"02")||StringUtil.equals(entity.getType(),"05"))){
//        设置工作流代码
        entity.setWorkflowId(null);
        if(StringUtil.equals(entity.getBonusMode(),DictCodeList.分红方式_现金分红.getValue())){
//          非货币现金分红
            entity.setType(StringUtil.equals(entity.getAssetType(), DictCodeList.基金类资产类型_非货币基金.getValue())?"03":"06");
//           处理数据参数
            getWorkFlowPatameter(entity);
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue(), false);
        }
        else{
//            非货币红利再投
            entity.setType("04");
            entity.setElementId("TrdFundDuration002_btn_save");
                BigDecimal unit = periodCommonService.getLatestValuation(entity.getFinprodId(), DateUtil.format(entity.getExRightsDate()));
                entity.setQty(ArithmeticUtil.divide(entity.getTradeAmt(), unit == null ? BigDecimal.ONE:unit, 4));
            WorkFlowParameter<TrdFundDuration001Dto> param = getWorkFlowPatameter(entity);
            periodCommonService.autoDealQtyForSubmit(entity.getFinAssetIntInfoList(),entity.getRegistShare(),entity.getQty());
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            entity.setCreateUser(entity.getFinAssetIntInfoList().get(0).getCreateUser());
            try {
//      启动流程
                WorkFlowUtil.startProcess(param,"","", entity.getCreateUser(),OperationType.NEW,false,entity);
//          设置为提交 插入流程表
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
    public WorkFlowParameter<TrdFundDuration001Dto> getWorkFlowPatameter(TrdFundDuration001Dto dto){
        WorkFlowParameter<TrdFundDuration001Dto> parameter= new WorkFlowParameter<>();
        parameter.setPageId("TrdFundDuration002");
        parameter.setServiceName(WorkFlowUtil.getServiceName(this.getClass()));
        parameter.setElementId(dto.getElementId());
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        //       为非货币现金分红及非货币红利再投时 设置前置交易编号
        if(StringUtil.equals(dto.getType(),"03")||StringUtil.equals(dto.getType(),"04")||StringUtil.equals(dto.getType(),"06")){
            dto.setbTradeId(dto.getTotleTradeId());
        }
        dto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        parameter.setBusinessCode(dto.getTotleTradeId());
        parameter.setBusinessType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        parameter.setComment(entity -> WorkFlowUtil.concatComment(
                this.getMessage("TrdDepoDuration001.finprodName"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodMarketName()
                        + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDuration001.assetType"),
                GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(
                        DictCode.基金类资产类型, entity.getAssetType()) + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDuration001.tradeId"),
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
    public Page<TrdFundDuration001Dto> findPage(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = pageParam.get("chlId");
        if (chlFinprodIdObj != null){
            pageParam.put("chlIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA,(String) chlFinprodIdObj));
        }else {
            pageParam.put("chlIdList",new ArrayList<>());
        }
        Page<TrdFundDuration001Dto>  page =restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findPage", pageParam,
            TrdFundDuration001Dto.class);
        for (TrdFundDuration001Dto dto: page.getRoot()) {
            if (StringUtil.equals(dto.getAssetType(),DictCodeList.基金类资产类型_货币基金.getValue())){
              dto.setRegistDate(null); dto.setExRightsDate(null);
              dto.setArriveDate(null); dto.setRegistShare(null);
              dto.setUnitCprice(null); dto.setTradeAmt(null);
            }
        }
        return page;
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
                    "/com/joyintech/tams/invest/period/trdfundduration/conf/export_conf_ls.xml").dataSource(
                    DataSourceType.MASERT);
        }else{
            exportParam.config(
                    "/com/joyintech/tams/invest/period/trdfundduration/conf/export_conf.xml").dataSource(
                    DataSourceType.MASERT);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdFundDuration001Dto> selectPageData(Map<String, Object> selectParam) {
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
    public TrdFundDuration001Dto findEffectiveData(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveData", param, TrdFundDuration001Dto.class);
    }
    /**
     * 获取现金分红流程中
     * @param param 查询参数
     * @return 匹配结果
     */
    public TrdFundDuration001Dto findCashDividend(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findCashDividend", param, TrdFundDuration001Dto.class);
    }
    /**
     * 统计是否有后续交易
     * @param map 参数
     * @return 统计数
     */
    public int countFollowDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/countFollowDeal", map, Integer.class);
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
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findAssetIntInfo", pageParam,
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
	public BaseTotalTradeInfoDto genFinProductTradeDto(TrdFundDuration001Dto entity) {
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
    public void doBack(TrdFundDuration001Dto entity) {
        if(("03".equals(entity.getType())||"06".equals(entity.getType())) && StringUtil.isNotEmpty(entity.getBussTrdTransferOrder())){
            for (BussTrdTransferOrderDto dto : entity.getBussTrdTransferOrder()){
                AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), dto.getOrderId());
            }
            return;
        }
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    @Override
    public void doRevoke(TrdFundDuration001Dto entity) {
        if("03".equals(entity.getType()) && StringUtil.isNotEmpty(entity.getBussTrdTransferOrder())){
            for (BussTrdTransferOrderDto dto : entity.getBussTrdTransferOrder()){
                AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), dto.getOrderId());
            }
            return;
        }
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    /**
     * 统计是否存在流程中分红交易
     * @param map 参数
     * @return 统计数
     */
    public int countFollowFHDeal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/countFollowFHDeal", map, Integer.class);
    }
}
