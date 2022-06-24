/*
 * 文件名：TrdFundDuration001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdfundobversion.service;

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
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.period.trdfundduration.dto.TrdFundDuration001Dto;
import com.joyintech.tams.invest.period.trdfundobversion.dto.TrdFundObversionDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.setWorkflowId;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangxuelei
 * @version 1.0 2019年10月15日
 * @see TrdFundObversion001Service
 * @since 1.0
 */
@Service
public class TrdFundObversion001Service extends CommonExportService<TrdFundObversionDto>
implements BaseProcessService<TrdFundObversionDto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdFundObversion001Service.class);

    @Autowired
    private TrdFundObversionDealEntity dealEntityService;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
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
       String conversionType = (String) params.get("conversionType");
       if(StringUtils.equals(conversionType,DictCodeListExt1.折算类型_货币基金份额折算.getValue())){
                params.put("type6",DictCodeList.基金类资产类型_货币基金.getValue());
       }else if(StringUtils.equals(conversionType,DictCodeListExt1.折算类型_ETF份额折算.getValue())){
            params.put("trmFundType","01");
       }else if(StringUtils.equals(conversionType,DictCodeListExt1.折算类型_分级基金定期折算.getValue())){
           params.put("trmFundType","03");
           params.put("graFundType","M");
           params.put("graFundTypea","A");
       }else if(StringUtils.equals(conversionType,DictCodeListExt1.折算类型_分级基金向上折算.getValue())){
           params.put("trmFundType","03");
           params.put("graFundType","M");
           params.put("graFundTypea","A");
           params.put("graFundTypeb","B");
       }else if(StringUtils.equals(conversionType,DictCodeListExt1.折算类型_分级基金向下折算.getValue())){
           params.put("trmFundType","03");
           params.put("graFundType","M");
           params.put("graFundTypea","A");
           params.put("graFundTypeb","B");
       }
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/getFinprodMarketId",
            params, SelectOptionDto.class);
    }

    /**
     * 工作流doInsert
     * @param entity 业务entity
     * @param type 操作类型
     */
    @Override
    public void doInsert(TrdFundObversionDto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
    	if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }
        dealEntityService.saveDataWhenStartWorkflow(entity, type, DictCodeListExt1.交易流程状态_流程中.getValue());
    }

    /**
     * 工作流doUpdate
     * @param entity entity 业务entity
     */
    @Override
    public void doUpdate(TrdFundObversionDto entity) {
        entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
        entity.setBack(true);
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
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
        dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW, DictCodeListExt1.交易流程状态_流程中.getValue());
    }

    /**
     * 工作流删除
     * @param workflowId
     */
    @Override
    public void doDelete(String workflowId) {
    	baseTradeProductDealCommServiceImpl.delete(workflowId);
    }
    /**
     * 工作流完成
     */
    @Override
    public void doComplete(TrdFundObversionDto entity, OperationType type) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == type) {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
        }
        entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/doComplete", entity,
            String.class);
    }

    /**
     * 画面查询
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdFundObversionDto> findPage(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/findPage", pageParam,
                TrdFundObversionDto.class);
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
                    "/com/joyintech/tams/invest/period/trdfundobversion/conf/export_conf_ls.xml").dataSource(
                    DataSourceType.MASERT);
        }else{
            exportParam.config(
                    "/com/joyintech/tams/invest/period/trdfundobversion/conf/export_conf.xml").dataSource(
                    DataSourceType.MASERT);
        }

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdFundObversionDto> selectPageData(Map<String, Object> selectParam) {
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
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/finprodMarketIdLink", pageParam, Map.class);
    }

    /**
     * 获取有效或者撤销的数据
     * @param param
     * @return
     */
    public TrdFundObversionDto findEffectiveData(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/findEffectiveData", param, TrdFundObversionDto.class);
    }
    /**
     * 获取折算比例流程中
     * @param param 查询参数
     * @return 匹配结果
     */
    public TrdFundObversionDto findCashDividend(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/findCashDividend", param, TrdFundObversionDto.class);
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
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/findAssetIntInfo", pageParam,
                FinAssetIntInfo.class);
    	return finAssetIntInfos;
    }
    /**
     * @Author wangxuelei
     * @Description //根据totleTradeId获取利息明细信息
     * @Date 10:01 2019/11/12 0012
     * @Param [pageParam]
     * @return java.util.List<com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo>
     **/
    public List<FinAssetIntInfo> selectObversionByTotleTradeId(Map<String, Object> pageParam) {
        List<FinAssetIntInfo> finAssetIntInfos = restTemplate.postForList(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundObversion/selectObversionByTotleTradeId", pageParam,
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
	public BaseTotalTradeInfoDto genFinProductTradeDto(TrdFundObversionDto entity) {
		BaseTotalTradeInfoDto result = dealEntityService.genBaseTotalTradeInfoDto(entity);
        return result;
	}
    @Override
    public void doBack(TrdFundObversionDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }
    @Override
    public void doRevoke(TrdFundObversionDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }
}
