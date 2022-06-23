package com.joyintech.tams.invest.period.trdsecoverdueduration.service;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.setWorkflowId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.bond.AutoPeriodBondCommonService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.autoperiod.secover.AutoPeriodSecOverCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdbondduration.dto.TrdBondDuration002ProcessDto;
import com.joyintech.tams.invest.period.trdsecoverdueduration.dto.TrdSecOverdueDuration002Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @ClassName TrdSecOverdueDurationService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2019/12/4 0004
 * @Version V1.0
 **/
@Service
public class TrdSecOverdueDuration002Service extends BaseService<TrdSecOverdueDuration002Dto> implements BaseProcessService<TrdSecOverdueDuration002Dto>  {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdSecOverdueDuration002Service.class);
	
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    
    /**
     * AutoPeriodBondCommonService
     */
    @Autowired
    private AutoPeriodSecOverCommonService autoPeriodSecOverCommonService;
    
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    

    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;
	
	
	@Override
	public void doInsert(TrdSecOverdueDuration002Dto entity, OperationType type) {
//        entity.setCreateUser(SecurityUtil.getLoginUser().getUserName());
//        entity.setCreateDept(SecurityUtil.getLoginUser().getDeptCode());
//        Date date = new Date();
//        entity.setCreateTime(date);
//        entity.setUpdateTime(date);
//        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
        	setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        }

        saveDataWhenStartWorkflow(entity, type);
    }

	private void saveDataWhenStartWorkflow(TrdSecOverdueDuration002Dto entity, OperationType type) {
		 if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
	                && StringUtil.isNotEmpty(entity.getBusiCode())) {
	            // 将附件和当前业务对象绑定（更新附件的业务代码）
	            fileAttachmentProcess.updateBussinessNo(
	                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
	                            Collectors.toList()),
	                    entity.getBusiCode());
	        }
		if (BussinessConstant.SUBMIT.equals(entity.getOperateType()) && type != OperationType.REVOKE) {
			restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
					entity, String.class);
			if (type != OperationType.REVOKE) {
				execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中);
			}
		} else if (type == OperationType.REVOKE) {
			restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/doRevoke", entity,
					String.class);
		}
	}
	
    /**
     * 
     * 批量调用共通
     * <功能详细描述>
     * @param entity 业务实体
     * @param 交易流程状态
     * @see
     * @since 1.0
     */
    private void execute交易共通(TrdSecOverdueDuration002Dto entity, DictCodeListExt1 交易流程状态) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setIsOverdueDeal(DictCodeList.是否_是.getValue());//是否逾期交易

        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        //是否需要入库应收款持仓信息
        boolean isInsertRecePosition = true;
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (entity.getBaseTotalTradeInfoDto() != null) {
        	for(BaseTradeInfoDto baseTradeInfoDto:entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList()){
        		//是否需要清算
            	boolean isStlPayOrder = true;
            	//利息信息
            	TrdProductDeal deal = baseTradeInfoDto.getTrdProductDeal();
                if(deal != null){
                	TrdProductDeal dealExe = new TrdProductDeal();
                    BeanUtils.copyProperties(deal, dealExe);
                	setDeal(baseTradeProductDealDtos, baseTradeInfoDto, baseTradeInfoDto.getTrdProductDealAdd(), dealExe, entity,baseTradeInfoDto,isStlPayOrder);
                	isStlPayOrder = false;
                }
                //本金信息
                if(baseTradeInfoDto.getBaseTrade()!=null){
                	 deal = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
                     if(deal != null){
                     	TrdProductDeal dealExe = new TrdProductDeal();
                         BeanUtils.copyProperties(deal, dealExe);
                     	setDeal(baseTradeProductDealDtos, baseTradeInfoDto, baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe, entity,baseTradeInfoDto.getBaseTrade(),isStlPayOrder);
                     	isStlPayOrder = false;
                     }
                }
                //还本不带息信息
                if(!StringUtil.isEmptyList(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList())){
                	baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(repaymentUnpayInterestBaseTradeInfoDto->{
                		if(repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDeal()!=null){
                			TrdProductDeal dealExe = new TrdProductDeal();
                            BeanUtils.copyProperties(repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDeal(), dealExe);
                			setDeal(baseTradeProductDealDtos, baseTradeInfoDto, repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDealAdd(), dealExe, entity,repaymentUnpayInterestBaseTradeInfoDto,false);
                		}
                	});
                }
//                清算未被使用，本金和利息都不存在，存在罚息信息 存入罚息信息 调用流水，不调用应收款持仓信息
                if(isStlPayOrder){
                	if(!StringUtil.isEmptyList(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList())){
                		TrdProductDeal dealExe = new TrdProductDeal();
                		//仅使用该交易信息，带入清算，不更新持仓信息
                		BeanUtils.copyProperties(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().get(0).getTrdProductDeal(), dealExe);
                		setDeal(baseTradeProductDealDtos, baseTradeInfoDto, baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().get(0).getTrdProductDealAdd(), dealExe, entity,baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().get(0),isStlPayOrder);
                	}
                	isInsertRecePosition = false;
                }
        	}
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        } 
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
        //是否需要入库应收款持仓信息
        if(isInsertRecePosition){
        	basePtlRecePositionService.insertPosition(tradeDto);
        }
        execute交易共通子组合(entity,entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList(),交易流程状态, entity.getBusiCode(),isInsertRecePosition);
    }

    private void execute交易共通子组合(TrdSecOverdueDuration002Dto entity,List<BaseTradeInfoDto> baseTradeInfoDtoList, DictCodeListExt1 交易流程状态, String tradeId,boolean isInsertRecePosition) {
        // 调用业务组件.【金融产品交易共通.交易产品接口】
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        tradeDto.setIsOverdueDeal(DictCodeList.是否_是.getValue());//是否逾期交易
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        // 调交易共通时，一个利息明细一条交易，但存表时，一个交易明细可能有两个交易（收息、还本）
        if (entity.getBaseTotalTradeInfoDto() != null) {
            baseTradeInfoDtoList.forEach(baseTradeInfoDto -> {
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(x->{
                	//是否需要清算
                	boolean isStlPayOrder = true;
                	//利息信息
                	TrdProductDeal deal = x.getTrdProductDeal();
                    if(deal != null){
                    	TrdProductDeal dealExe = new TrdProductDeal();
                        BeanUtils.copyProperties(deal, dealExe);
                    	setDeal(baseTradeProductDealDtos, x, x.getTrdProductDealAdd(), dealExe, entity,x,isStlPayOrder);
                    	isStlPayOrder = false;
                    }
                    //本金信息
                    if(x.getBaseTrade()!=null){
                    	deal = x.getBaseTrade().getTrdProductDeal();
                        if(deal != null){
                        	TrdProductDeal dealExe = new TrdProductDeal();
                            BeanUtils.copyProperties(deal, dealExe);
                        	setDeal(baseTradeProductDealDtos, x, x.getBaseTrade().getTrdProductDealAdd(), dealExe, entity,x.getBaseTrade(),isStlPayOrder);
                        	isStlPayOrder = false;
                        }
                    }
                    //还本不带息信息
                    if(!StringUtil.isEmptyList(x.getRepaymentUnpayInterestBaseTradeInfoDtoList())){
                    	x.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(repaymentUnpayInterestBaseTradeInfoDto->{
                    		if(repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDeal()!=null){
                    			TrdProductDeal dealExe = new TrdProductDeal();
                                BeanUtils.copyProperties(repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDeal(), dealExe);
                    			setDeal(baseTradeProductDealDtos, null, repaymentUnpayInterestBaseTradeInfoDto.getTrdProductDealAdd(), dealExe, entity,repaymentUnpayInterestBaseTradeInfoDto,false);
                    		}
                    	});
                    }
                    
//                  清算未被使用，本金和利息都不存在，存在罚息信息 存入罚息信息 调用流水，不调用应收款持仓信息
                    if(isStlPayOrder){
                    	if(!StringUtil.isEmptyList(x.getPenaltyIntBaseTradeInfoDtoList())){
                    		TrdProductDeal dealExe = new TrdProductDeal();
                    		//仅使用该交易信息，带入清算，不更新持仓信息
                    		BeanUtils.copyProperties(x.getPenaltyIntBaseTradeInfoDtoList().get(0).getTrdProductDeal(), dealExe);
                    		setDeal(baseTradeProductDealDtos, baseTradeInfoDto, x.getPenaltyIntBaseTradeInfoDtoList().get(0).getTrdProductDealAdd(), dealExe, entity,x.getPenaltyIntBaseTradeInfoDtoList().get(0),isStlPayOrder);
                    	}
                    }
                    
                });
            });
            baseTradeProductDealDtos.forEach(baseTradeProductDealDto -> {
                        baseTradeProductDealDto.getTrdProductDeal().setPs(baseTradeProductDealDto.getTrdProductDeal().getStatus());
                        baseTradeProductDealDto.getTrdProductDeal().setTradeId(tradeId);
                    }
            );
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        }
        baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
        //是否需要入库应收款持仓信息
        if(isInsertRecePosition){
        	basePtlRecePositionService.insertPosition(tradeDto);
        }
    }
    
    


    /**
     * 
     * @param baseTradeProductDealDtos 被组装list（组装信息存入此list）
     * @param baseTradeInfoDto 组合层信息
     * @param add对应交易附属表信息
     * @param dealExe 对应交易表信息
     * @param entity 
     * @param baseTrade  获取对应应收款流水信息
     * @param isStlPayOrder  是否需要清算信息
     */
    private void setDeal(List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto, TrdProductDealAdd add, TrdProductDeal dealExe,
    		TrdSecOverdueDuration002Dto entity, BaseTrade baseTrade,boolean isStlPayOrder) {
        dealExe.setPs(dealExe.getStatus());
    	dealExe.setFpriceAmt(dealExe.getTradeAmt());
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        dealExe.setTradeId(entity.getBusiCode());
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if(baseTradeInfoDto!=null&&isStlPayOrder&&baseTradeInfoDto.getStlPayOrders()!=null){
        	BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[]{orders}));
        }
        baseTradeProductDealDto.setRecType(baseTrade.getRecType());//应收款持仓
        baseTradeProductDealDto.setPayDate(baseTrade.getPayDate());//计划支付日
        if(StringUtil.isNotEmpty(baseTrade.getAmount())){
        	 baseTradeProductDealDto.setRecAmount(baseTrade.getAmount());//实际更新持仓
        }
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
    }

	@Override
	public void doUpdate(TrdSecOverdueDuration002Dto entity) {
      entity.setBaseTotalTradeInfoDto(genFinProductTradeDto2(entity));
      ProcessMark newEntity;
      try {
          newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdBondDuration002ProcessDto.class, true);
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
      saveDataWhenStartWorkflow(entity, OperationType.MODIFY);
	}

	@Override
	public void doDelete(String workflowId) {
		baseTradeProductDealCommServiceImpl.delete(workflowId);
	}

	@Override
	public void doBack(TrdSecOverdueDuration002Dto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
	}

	@Override
	public void doRevoke(TrdSecOverdueDuration002Dto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
	}

	@Override
	public void doComplete(TrdSecOverdueDuration002Dto entity, OperationType operationType) {
		entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
		if (OperationType.REVOKE == operationType) {
			execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);
			entity.setOperateType(BussinessConstant.REVOKE);
		} else {
			execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
		}
		entity.setIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
		// 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
		restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/doComplete", entity,
				String.class);
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
    public BaseTotalTradeInfoDto genFinProductTradeDto2(TrdSecOverdueDuration002Dto entity) {
        return autoPeriodSecOverCommonService.genFinProductTradeDto(entity);
    }

	public TrdSecOverdueDuration002Dto selectNewBasicdata(Map<String, Object> params) {
		return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectNewBasicdata", params,
				TrdSecOverdueDuration002Dto.class);
	}

	public List<FinAssetIntInfo> findAssetIntInfo(Map<String, Object> params) {
		return restTemplate.postForList(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectNewInterestDetails", params,
                FinAssetIntInfo.class);
	}

	 /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdSecOverdueDuration002Dto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(entity.getBusiCode());
//        trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdProductDeal.setFinprodId(entity.getFinprodId());
        // trdProductDeal.setFinprodType(); TODO 估值核算 金融产品类型
//        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        trdProductDeal.setFinprodType2(entity.getFinprodType2());
        if (!entity.isChannel()) {
            trdProductDeal.setSecManageAcctId(entity.getSecAcctId());
        }
        else {
            trdProductDeal.setChlId("999999".equals(entity.getSecAcctId()) ? null : entity.getSecAcctId());
        }
        trdProductDeal.setCcy(entity.getCcy());
        String ps = AutoPeriodCommonService.change现金流类型To交易方向(entity.getCashType());
        trdProductDeal.setPs(ps);
        trdProductDeal.setTradeDate(entity.getTradeDate());
        trdProductDeal.setVdate(DateUtil.parse(entity.getVdateStr()));
        trdProductDeal.setMdate(DateUtil.parse(entity.getMdateStr()));
        trdProductDeal.setSettleDate(entity.getTradeDate());
//        trdProductDeal.setShareAmt(ArithmeticUtil.divide(entity.getQty(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        trdProductDeal.setPrinAmt(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()));
        trdProductDeal.setIntAmt(entity.getIntAmt());
        trdProductDeal.setTradeAmt(entity.getTradeAmt());
        trdProductDeal.setTradeMarket(entity.getTradeMarket());

        // 一体化对应etliu start
        trdProductDeal.setFpriceAmt(trdProductDeal.getTradeAmt());
        // 一体化对应etliu end

        return trdProductDeal;
    }

	public TrdSecOverdueDuration002Dto findSecOverdueData(Map<String, Object> params) {
		return restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/findSecOverdueData", params,
                TrdSecOverdueDuration002Dto.class);
	}

    /**
     * 根据汇总的汇总交易编号查询有效的数据
     * @param busiCode
     * @return
     */
    public TrdSecOverdueDuration002Dto findEffectiveData(String busiCode) {
        return simpleRestTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/findEffectiveData", busiCode, TrdSecOverdueDuration002Dto.class);
    }
}
