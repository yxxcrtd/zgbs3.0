/*
 * 文件名：TrdDepoDuration001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trddepoduration.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.depo.AutoPeriodDepoCommonService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trddepoduration.dto.TrdDepoDuration001Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.setWorkflowId;

/**
 *
 * 存款存续期service
 * @author dingzhipeng
 * @version 1.0 2018年10月11日
 * @see TrdDepoDuration001Service
 * @since 1.0
 */
@Service
public class TrdDepoDuration001Service extends CommonExportService<TrdDepoDuration001Dto>
implements BaseProcessService<TrdDepoDuration001Dto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdDepoDuration001Service.class);

    @Autowired
    private TrdDepoDurationDealEntity dealEntityService;
//    /**
//     * 业务共通组件.资产头寸现金持仓共通组件
//     */
//    @Autowired
//    private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    /**
     * AutoPeriodDepoCommonService
     */
    @Autowired
    private AutoPeriodDepoCommonService autoPeriodDepoCommonService;
    /**
     * periodCommonService
     */
    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    /**
     * 划款指令
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;
    @Override
    public void doInsert(TrdDepoDuration001Dto entity, OperationType type) {
    	if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }
    	dealEntityService.saveDataWhenStartWorkflow(entity, type);
    }
    /**
     * 工作流更新
     */
    @Override
    public void doUpdate(TrdDepoDuration001Dto entity) {
//        baseTradeProductDealCommServiceImpl.delete(entity.getWorkflowId());
//        防止生成错误的ps
        entity.setPs("");

        if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getBusiCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getBusiCode());
        }

        //删除持仓流水 start
        if(entity.getBaseTotalTradeInfoDto()!=null&&entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList()!=null) {
        	PtlSecPositionFlowDto pFlowDto = new PtlSecPositionFlowDto();
        	pFlowDto.setEntrust(true);
        	for(BaseTradeInfoDto baseTradeInfoDto:entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList()) {
        		if(baseTradeInfoDto.getTrdProductDeal()!=null) {
        			pFlowDto.setTradeId(baseTradeInfoDto.getTrdProductDeal().getTradeId());
                	basePtlSecPositionInfoService.deleteSecPosition(pFlowDto);
        		}
        	}
        }
        //删除持仓流水 end
        
        entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
        ProcessMark newEntity = null;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdDepoDuration001Dto.class, true);
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
        dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }
//    public void doUpdate(TrdDepoDuration001Dto entity) {
//        if("submit".equals(entity.getOperateType())) {
//            doDelete(entity.getWorkflowId());
//        }
//        dealEntityService.saveDataWhenStartWorkflow(entity, OperationType.NEW);
//    }
    /**
     * 工作流完成
     */
    @Override
    public void doComplete(TrdDepoDuration001Dto entity, OperationType type) {
    	entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == type) {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
        	dealEntityService.execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
        }
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/doComplete", entity,
            String.class);
    }
//    @Override
//    public void doComplete(TrdDepoDuration001Dto entity, OperationType type) {
//        entity.setRepIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
//        FinProductTradeDto tradeDto = dealEntityService.genFinProductTradeDto(entity);
//        tradeDto.setNeedSum(true);
//        if (OperationType.REVOKE == type) {
//            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
//            entity.setOperateType(BussinessConstant.REVOKE);
//        }
//        else {
//            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
//        }
//        baseTradeProductDealCommServiceImpl.execute(tradeDto);
//        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
//        restTemplate.postForObject(
//            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/doComplete", entity,
//            String.class);
//    }

    /**
     * 获取组合代码下拉菜单
     * @return 下拉菜单
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
     * 市场代码下拉菜单
     * @return 查询结果
     */
    public Page<SelectOptionDto> getFinprodMarketId() {
        Map<String, Object> params = RequestUtil.getPageParam();
        params.put("finprodType2",
            DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        params.put("DEPO_ASSET_TYPE_02",
            DictCodeList.存款类资产类型_存放同业.getValue());
        params.put("DEPO_ASSET_TYPE_03",
                DictCodeListExt3.存款类资产类型_活期存放.getValue());
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getFinprodMarketId",
            params, SelectOptionDto.class);
    }

    /**
     * 查询方法
     * @param params 画面查询参数
     * @return 返回结果
     */
    public Page<TrdDepoDuration001Dto> findPage(Map<String, Object> params) {
        if (params.get("statusValid") != null) {
            params.put("statusValid",
                StringUtil.splitToList(",", (String)params.get("statusValid")));
        }
        else {
            params.put("statusValid", new ArrayList<>());
        }
        //多选下拉框处理（合作资产）
        Object chlFinprodIdObj = params.get("chlId");
        if (chlFinprodIdObj != null){
            params.put("chlIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA,(String) chlFinprodIdObj));
        }else {
            params.put("chlIdList",new ArrayList<>());
        }
        params.put("userName", LoginUserUtil.getUserName());
        String cashType = (String)params.get("cashType");
        if(StringUtil.isNotEmpty(cashType)) {
            if(StringUtil.equals(cashType, DictCodeListExt1.平安业务类型_存款提前支取.getValue())) {
                params.put("cashTypeC", "cashTypeC");
            }
            else if(StringUtil.equals(cashType, DictCodeListExt1.平安业务类型_存款中间付息.getValue())){
                params.put("cashTypeC", DictCodeList.现金流类型_中间收息.getValue());
            }
            else {
                params.put("cashTypeC", DictCodeList.现金流类型_到期还本.getValue());
            }
        }
        params.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findPage", params,
            TrdDepoDuration001Dto.class);
    }

    /**
     * 判断其在现金流对账模块是否完成匹配
     * <功能详细描述>
     * @param busiCode 交易编号
     * @return 统计结果
     * @since 1.0
     */
    public int countCashMatch(String busiCode) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/countCashMatch", busiCode, Integer.class);
    }
    
    //add by yujiaping 2020-12-5 BS-1562【存款类存续期】同一资产同一计息结束日只能做一次活期存放结息start
    @SuppressWarnings("unchecked")
	public Map<String,Object> getTradeId(Map<String, Object> pageParam) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getTradeId", pageParam, Map.class);
    }
    //add by yujiaping 2020-12-5 BS-1562【存款类存续期】同一资产同一计息结束日只能做一次活期存放结息end

    /**
     * 资产代码联动查询
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 画面查询结果
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> finprodMarketIdLink(Map<String, Object> pageParam) {
        pageParam.put("FINPRODTYPE2_F07",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        pageParam.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("YN_N", DictCodeList.是否_否.getValue());
        pageParam.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        pageParam.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        pageParam.put("CASH_TYPE_1_03", DictCodeListExt1.现金流一级类型_费用.getValue());
        pageParam.put("CASH_TYPE_2_C08", DictCodeListExt1.现金流二级类型_资产推荐费.getValue());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/finprodMarketIdLink", pageParam, Map.class);
    }

    /**
     * 组合代码联动查询
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 画面查询结果
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> portfolioIdLink(Map<String, Object> pageParam) {
        pageParam.put("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/portfolioIdLink", pageParam, Map.class);
    }

    /**
     * 组合代码联动查询
     * add by jiangpeng on 20201215 ZGBS-69
     * @param pageParam 画面的条件
     * @return 画面查询结果
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getPortfolioName(Map<String, Object> pageParam) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getPortfolioName", pageParam, Map.class);
    }

    /**
     * 剩余本金联动处理
     * <功能详细描述>
     * @param pageParam 画面的条件
     * @return 剩余现金
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> calResiPrincipal(Map<String, Object> pageParam) {
        pageParam.put("FINPRODTYPE2_F07",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        if (!StringUtil.isNotEmpty(pageParam.get("chlId"))){
            pageParam.put("chlId","999999" );
        }
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/calResiPrincipal", pageParam, Map.class);
    }

    public Map<String,Object> calTheoryIntDemand(Map<String, Object> pageParam) {
        pageParam.put("FINPRODTYPE2_F07",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/calTheoryIntDemand", pageParam, Map.class);
    }

    /**
     * 查询dto
     * <功能详细描述>
     * @param param 查询参数
     * @return 匹配结果
     * @see
     * @since 1.0
     */
    public TrdDepoDuration001Dto findEffectiveData(Map<String, Object> param) {
    	param.put("userName", LoginUserUtil.getUserName());
    	return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findEffectiveData", param, TrdDepoDuration001Dto.class);
    }
    /**
     * 作废流程
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
       baseTradeProductDealCommServiceImpl.delete(workflowId);
//       TrdCommonDeleteDto trdCommonDeleteDto = new TrdCommonDeleteDto();
//       trdCommonDeleteDto.setWorkflowId(workflowId);
//       restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doDelete",
//           trdCommonDeleteDto, String.class);
    }
//    @Override
//    public void doDelete(String workflowId) {
//        TrdDepoDuration001Dto entity = getBussinessEntityByWorkflowId(workflowId);
//        List<WflTrdProductDeal> wflTrdProductDeals = dealEntityService.gen交易业务流程(entity);
//        List<WflStlPayOrders> wflStlPayOrders = dealEntityService.gen划款指令流程(entity);
//        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
//        wflStlPayOrders.forEach(wflStlPayOrders1 -> {
//            StlPayOrdersDto orders = new StlPayOrdersDto();
//            BeanUtils.copyProperties(wflStlPayOrders1, orders);
//            stlPayOrdersList.add(orders);
//        });
//        TrdProductDeal trdProductDeal = dealEntityService.getTrdProductDeal(entity);
//        List<PtlSecPositionFlowDto> ptlSecPositions = new ArrayList<>();
//        List<PtlCashPositionFlowDto> ptlCashPositions = new ArrayList<>();
//        PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
//        inputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
//        inputDto.setTrdProductDeal(trdProductDeal);
//        inputDto.setPortfolioId(entity.getPortfolioId());
//
//        PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
//        cashInputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
//        cashInputDto.setTrdProductDeal(trdProductDeal);
//        cashInputDto.setPortfolioId(entity.getPortfolioId());
//        cashInputDto.setStlPayOrders(stlPayOrdersList);
//        List<PtlSecPositionFlowDto> ptlSecPositionsGet = assetPositionsCashCommServiceImpl.positionDeal(
//            inputDto);
//        List<PtlCashPositionFlowDto> ptlCashPositionsGet = assetPositionsCashCommServiceImpl.cashDeal(
//            cashInputDto);
//        if (ptlSecPositionsGet != null) {
//            ptlSecPositions.addAll(ptlSecPositionsGet);
//        }
//        if (ptlCashPositionsGet != null) {
//            ptlCashPositions.addAll(ptlCashPositionsGet);
//        }
//        TrdCommonDeleteDto trdCommonDeleteDto = new TrdCommonDeleteDto();
//        trdCommonDeleteDto.setWorkflowId(workflowId);
//        trdCommonDeleteDto.setWflTrdFinProductDealTotal(dealEntityService.gen汇总流程表(entity));
//        trdCommonDeleteDto.setPtlCashPositions(ptlCashPositions);
//        trdCommonDeleteDto.setPtlSecPositions(ptlSecPositions);
//        restTemplate.postForObject(
//            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/doDelete",
//            trdCommonDeleteDto, String.class);
//    }

    /**
     * 构建导出用参数
     * @param exportParam 导出参数
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config(
            "/com/joyintech/tams/invest/period/trddepoduration/conf/export_conf.xml").dataSource(
                DataSourceType.MASERT);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdDepoDuration001Dto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }
    /**
     * 查询所有的待校验数据
     * <功能详细描述>
     * @param map 参数
     * @return 检索结果
     * @since 1.0
     */
    public List<TrdDepoDuration001Dto> getAllDataForCheck(Map<String, Object> map) {
    	map.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getAllDataForCheck",
            map, TrdDepoDuration001Dto.class);
    }
    /**
     * 设置导出单元格格式
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(StringUtil.equals("vdateProd", propName)||StringUtil.equals("mdateProd", propName)
            ||StringUtil.equals("tradeDate", propName)) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }

    /**
     *
     * 根据汇总交易编号查询交易
     * <功能详细描述>
     * @param busiCode 汇总交易编号
     * @return 查询结果
     * @since 1.0
     */
    public List<TrdProductDeal> findDealByTotalId( String busiCode){
        return restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findDealByTotalId",
            busiCode, TrdProductDeal.class);
    }
    /**
     *
     * 查询业务编号
     * <功能详细描述>
     * @param map 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBusiCode(Map<String, Object> map) {
        // add by wanshiwei ZGZGST-3391 存款类资产-查询界面字段下拉框异常 因ZGZGST-1611:增加拆借存续期业务编号查询条件 改造 20210926 start
        List<String>finProdTypeList = new ArrayList<>();
        finProdTypeList.add(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        map.put("finProdTypeList",finProdTypeList);
        map.put("IS_CANCEL",DictCodeList.是否_否.getValue());
        map.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        map.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        map.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        map.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        map.put("COMMON_BUSINESS_TYPETRD11", DictCodeList.共通业务类型_存款类资产存续期.getValue());
        map.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        map.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        map.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        map.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        map.put("COM_TYPE_TRD03", DictCodeList.业务类型_存款类资产交易.getValue());
        map.put("userName", LoginUserUtil.getUserName());
        // add by wanshiwei ZGZGST-3391 存款类资产-查询界面字段下拉框异常 因ZGZGST-1611:增加拆借存续期业务编号查询条件 改造 20210926 end
        return restTemplate.postForPage(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/getBusiCode",
                map, SelectOptionDto.class);
    }
	/**
	 * 处理map
	 * <功能详细描述>
	 * @param dto 业务entity
	 * @return 对应map
	 * @see
	 * @since 1.0
	 */
	public Map<String,Object> genParams(TrdDepoDuration001Dto dto) {
		return new HashMap<String, Object>() {
			{
				put("adjustDate", DateUtil.format(dto.getMdateProd()));
				put("vDate", DateUtil.format(dto.getVdateProd()));
                put("mDate", DateUtil.format(dto.getMdateProd()));
                put("loadRate", dto.getLoadRate());
                put("interestPayDay", DateUtil.format(dto.getAdjustDate()));
                put("finprodId", dto.getFinprodId());
                put("busiCode", dto.getBusiCode());
                put("statusValid", dto.getStatusValid());
                // <!-- 通道交易 新增 强成西 2019/03/26 start -->
                put("chlId", dto.getChlId());
                // <!-- 通道交易 新增 强成西 2019/03/26 end -->
			}
		};
	}
	/**
	 * 根据业务实体查询对应的利息明细
	 * <功能详细描述>
	 * @param genParams 参数
	 * @return 利息明细
	 * @see
	 * @since 1.0
	 */
	public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> genParams) {
		List<FinAssetIntInfo> assetIntInfo= restTemplate.postForList(
	            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findAssetIntInfo",
	            genParams, FinAssetIntInfo.class);
		for (int i = 0; i < assetIntInfo.size(); i++ ) {
			assetIntInfo.get(i).setCalcRuleString(
                SysDataUtil.getNameFromDataDict(DictCode.频率, assetIntInfo.get(i).getCalcRule()));
        }
		return assetIntInfo;
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
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdDepoDuration001Dto entity) {
//        组合代码不为空设置实际交易组合
        if(StringUtil.isNotEmpty(entity.getPortfolioId())){
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        }
        BaseTotalTradeInfoDto result = autoPeriodDepoCommonService.genFinProductTradeDto(entity);
//        if(StringUtil.isEmptyList(entity.getFinAssetIntInfoList())&&StringUtil.equals("draw",entity.getOpType())){
//            // 处理划款指令
//            List<BaseTradeInfoDto> baseTradeInfoDtos = result.getBaseTradeInfoDtoList();
//            if (!StringUtil.isEmptyList(baseTradeInfoDtos)) {
//                for (BaseTradeInfoDto baseTradeInfoDto : baseTradeInfoDtos) {
//                    List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(
//                            periodCommonService.transformOrderDto(entity.getBussTrdTransferOrder()));
//                    baseTradeInfoDto.setStlPayOrders(
//                        dealEntityService.gen划款指令流程(stlPayOrdersList, baseTradeInfoDto.getTrdFinProductDealTotal(), entity));
//                }
//            }
//
//        }
        return result;
    }

    @Override
    public void doBack(TrdDepoDuration001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    @Override
    public void doRevoke(TrdDepoDuration001Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    public int countTradeForDemand(Map<String,Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/countTradeForDemand", map, Integer.class);
    }

}
