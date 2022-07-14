/*
 * 文件名：FinNetProductDurationService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zqz 修改时间：2019年1月15日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.productduration.netproductduration.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import com.joyintech.framework.grid.Page;
import com.joyintech.tams.common.base.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinBaseRuleInfoMapper;
import com.joyintech.tams.common.base.dao.FinCashCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashInfoMapper;
import com.joyintech.tams.common.base.dao.FinFloatCalcRuleDetailMapper;
import com.joyintech.tams.common.base.dao.FinFloatCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.dao.PaTrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PrdSaleParamMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdAsynHandleStatusMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.entity.BokPositionPrd;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.FinTradeProductKey;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.PaTrdProductDeal;
import com.joyintech.tams.common.base.entity.PrdMdateAdjust;
import com.joyintech.tams.common.base.entity.PrdSaleParam;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.RepDuration;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdAsynHandleStatus;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.core.impl.dao.BaseGetBussinessIdMapper;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlCashPositionInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.common.trade.service.impl.BaseEtrCashPositionCommServiceImpl;
import com.joyintech.tams.invest.product.common.service.ProductCommonService;
import com.joyintech.tams.invest.product.productduration.netproductduration.dto.FinCashPlanDto;
import com.joyintech.tams.invest.product.productduration.netproductduration.dto.FinNetProductDurationDto;
import com.joyintech.tams.invest.product.productduration.netproductduration.dto.FinNetProductDurationLayerDto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.dto.FinNetValueProductBuild003Dto;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 *
 * @author zqz
 * @version 1.0 2019年1月15日
 * @see FinNetProductDurationService
 * @since 1.0
 */
@Service
public class FinNetProductDurationService extends CommonExportService<FinNetProductDurationDto> implements BaseProcessService<FinNetProductDurationDto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(FinNetProductDurationService.class);

    @Autowired
    private TradeProductDealCommService baseTradeProductDealCommServiceImpl;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private ProductCommonService productCommonService;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

	@Autowired
	private FinCashInfoMapper finCashInfoMapper;
	
	@Autowired
	private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

	@Autowired
	private BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;

	@Autowired
	private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
	
	@Autowired
	private PaTrdProductDealMapper paTrdProductDealMapper;
	
	@Autowired
	private TrdProductDealMapper trdProductDealMapper;
	
	@Autowired
	private TrdProductDealAddMapper trdProductDealAddMapper;
	
	@Autowired
	private PtlTradeMapper ptlTradeMapper;
	
	@Autowired
	private StlPayOrdersMapper stlPayOrdersMapper;
	
	@Autowired
	private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
	
	@Autowired
	private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
	
	@Autowired
	private PrdSaleParamMapper prdSaleParamMapper;
	
	@Autowired
	private	FinProductAddMapper finProductAddMapper;
	
	@Autowired
	private FinCashCalcRuleMapper finCashCalcRuleMapper;
	
	@Autowired
	private FinFloatCalcRuleMapper finFloatCalcRuleMapper;
	
	@Autowired
	private FinFloatCalcRuleDetailMapper finFloatCalcRuleDetailMapper;
	
	@Autowired
	private FinTradeProductAddMapper finTradeProductAddMapper;
	
	@Autowired
    private TrdAsynHandleStatusMapper trdAsynHandleStatusMapper;
	
	@Autowired
	private BaseGetBussinessIdMapper baseGetBussinessIdMapper;
	
	@Autowired
	private FinBaseRuleInfoMapper finBaseRuleInfoMapper;

	@Autowired
	private PrdMdateAdjustMapper prdMdateAdjustMapper;
	
	 @Autowired
	private BasePtlCashPositionInfoMapper basePtlCashPositionInfoMapper;
	

	@Autowired
	private BaseEtrCashPositionCommServiceImpl baseEtrCashPositionCommServiceImpl;
    /**
     * {@inheritDoc}
     */
	
	 @Override
	    public void doInsert(FinNetProductDurationDto dto, OperationType operationType) {
		 if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
    		 this.handleBussFlowData(dto);
    	 }		
	 }	 	
	 
	 @Override
	    public void doBack(FinNetProductDurationDto dto) {
		 if (!StringUtil.equals("revoke", dto.getOperateType())
                 && !StringUtil.equals("delete", dto.getOperateType())) {
	    	 this.delhandleBussFlowData(dto);
	    	 }
	    }
	 
	 private void handleBussFlowData(FinNetProductDurationDto dto) {
		 if( (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType()) ||
							DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())  ||
							DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())  ||		
					        DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())  ||
					        DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType()) )) {
			 //组合现金流水
			 List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
		        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
		            for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
		                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
		                ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());
		                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
		                ptlCashPositionFlowDto.setCcy(dto.getCcy());		               
		                    if (orderDto.getPs().equals("01")) {
		                        ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
		                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
		                    }
		                    else {
		                        ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
		                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
		                    }		                
		                ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());
		                if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
		                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getRedPayDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getExpirePayDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                }
		                else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
		                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getRedConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getExpireConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getBonusCashBonusDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getBonusShareConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                }

		            }
		        }
		        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
		        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows); 
		        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
	                    && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
	                for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
	                    basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
	                }
	            }
		  //指令现金流水
		        baseEtrCashPositionCommServiceImpl.portfolioEtrCashPosition(ptlPositionHoldDto, true);
		 }
	 }
	 
	 private void delhandleBussFlowData(FinNetProductDurationDto dto) {
		 if(DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())
					&& (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType()) ||
							DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())  ||
							DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())  ||		
					        DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())  ||
					        DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType()) )) {
			 //组合现金流水
			 List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
		        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
		            for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
		                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
		                ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());
		                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
		                ptlCashPositionFlowDto.setCcy(dto.getCcy());		               		                    
		                ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
		                ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());	                
		                ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());
		                if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
		                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getRedPayDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getExpirePayDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                }
		                else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
		                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getRedConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getExpireConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
		                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getBonusCashBonusDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                    else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
		                        ptlCashPositionFlowDto.setCdate(dto.getBonusShareConfirmDate03());
		                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
		                    }
		                }

		            }
		        }
		        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
		        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows); 
		        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
	                    && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
	                for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
	                	basePtlCashPositionInfoMapper.deletePtlCashPositionFlow(ptlCashPositionFlow);
	                }
	            }
		  //指令现金流水		        
		        baseEtrCashPositionCommServiceImpl.portfolioEtrCashPosition(ptlPositionHoldDto, true);
		 }
	 }
	 
	 
	 
    @Override
    public void doComplete(FinNetProductDurationDto dto, OperationType operationType) {
        if (operationType == OperationType.NEW) {
            doCompleteInsertOrUpdate(dto, false);
            // 募集报送
            saveRepDuration(dto, false);
        }
        else if (operationType == OperationType.REVOKE || operationType == OperationType.DELETE) {
            doCompleteInsertOrUpdate(dto, true);
            saveRepDuration(dto, true);
        }
        // 删除工作流表
        baseTradeProductDealCommServiceImpl.delete(dto.getWorkflowId());
    }
    
    /**
     * ZGBSS-382:待办中进行删除后提交，删除不掉
     */
    @Override
    public void doUpdate(FinNetProductDurationDto dto) {
    	if (!StringUtil.equals("revoke", dto.getOperateType()) && !StringUtil.equals("delete", dto.getOperateType())) {
    		try {
    			 if(DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())||DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())){
        			 //1. 获取前端页面数据
        			 ProcessMark newEntity = WorkFlowUtil.getBusinessObjectFromRequest(FinNetProductDurationDto.class,true);
        			 FinNetProductDurationDto requestDto = getBussinessEntity(newEntity);
        			 //2. 利用前端数据 覆盖 dto中指定字段
        			 dto.setAddReduceDealAmt(requestDto.getAddReduceDealAmt());
        			 dto.setAddReduceDealNum(requestDto.getAddReduceDealNum());
    				 if(dto.getAddReduceDealAmt()==null) {
    					 dto.setBussTrdTransferOrder(null);
    				 }
    			 }
    		}catch (Exception e) {
				logger.error(e.getMessage(),e);
    		}
    		 this.handleBussFlowData(dto);
    	}
    }

    private void saveRepDuration(FinNetProductDurationDto dto, boolean isRevoke) {
        RepDuration repDuration = new RepDuration();
        repDuration.setRepFinprodId(dto.getFinprodId());
        // 根据收益类型和交易类型确定报送时间
        setRepDurationRegDate(repDuration, dto);
        // repDuration.setRepDurationRegDate(DateUtil.parse(dto.getTradeDate()));
        // repDuration = bussCommonMapperDelegateService.selectBySinglePrimaryKey(RepDurationMapper.class, repDuration, RepDuration.class);
//        if (repDuration == null) {
//            repDuration = genRepDuration(dto);
//            // bussCommonMapperDelegateService.insertSelective(RepDurationMapper.class, repDuration);
//       }
//        else {
//            // 汇总更新
//
//           // bussCommonMapperDelegateService.updateByPrimaryKey(RepDurationMapper.class, repDuration);
//        }

    }

    private void setRepDurationRegDate(RepDuration repDuration, FinNetProductDurationDto dto) {
        if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getSubConfirmDate());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getPurConfirmDate());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getRedConfirmDate());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getExpireConfirmDate());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getAddReduceConfirmDate());
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getSubConfirmDate03());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getPurConfirmDate03());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getRedConfirmDate03());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getExpireConfirmDate03());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getAddReduceConfirmDate03());
            }
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getBonusCashBonusDate03());
            }
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getBonusShareConfirmDate03());
            }
        }

    }

    /**
     * 生成报送信息
     *
     * @param dto
     * @return
     */
    private RepDuration genRepDuration(FinNetProductDurationDto dto) {
        RepDuration repDuration = new RepDuration();
        repDuration.setRepFinprodId(dto.getFinprodId());
        repDuration.setRepCcy(dto.getCcy());

        if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getSubConfirmDate());
                // 该币种累计申购金额（元）
                repDuration.setRepApplyCny(dto.getSubDealAmt());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getPurConfirmDate());
                // 该币种累计申购金额（元）
                repDuration.setRepApplyCny(dto.getPurDealAmt());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getRedConfirmDate());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getExpireConfirmDate());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getAddReduceConfirmDate());
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getSubConfirmDate03());
                // 该币种累计申购金额（元）
                repDuration.setRepApplyCny(dto.getSubDealAmt03());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getPurConfirmDate03());
                // 该币种累计申购金额（元）
                repDuration.setRepApplyCny(dto.getPurDealAmt03());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getRedConfirmDate03());

            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getExpireConfirmDate03());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getAddReduceConfirmDate03());
            }
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getBonusCashBonusDate03());
            }
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                repDuration.setRepDurationRegDate(dto.getBonusShareConfirmDate03());
            }
        }
        return null;
    }

    private void doCompleteInsertOrUpdate(FinNetProductDurationDto dto, boolean isRevoke) {
		//如果是净值型产品的认购交易，需要新建子产品入库
		dto.setBranch(0L);
		FinTradeProduct finTradeProduct = new FinTradeProduct();
		if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())
				&& DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())
				&& (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType()) ||
						DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType()))){		//字典.隐性管理 eq 变量DTO.期限管理	
			if(!DictCodeListExt2.期限类型_无固定期限.getValue().equals(dto.getTermType())) {			//无固定期限 eq dto.期限类型	
				if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
					finTradeProduct.setFinprodId(dto.getFinprodId()+"_"+dto.getTermType()+"_"+dto.getSubTermDays());		//dto.期限类型+dto.期限值
				}
				else {
					finTradeProduct.setFinprodId(dto.getFinprodId()+"_"+dto.getTermType()+"_"+dto.getPurTermDays());		//dto.期限类型+dto.期限值
				}
			}
			else {
				finTradeProduct.setFinprodId(dto.getFinprodId()+"_"+dto.getTermType());		//dto.期限类型		debug
			}
			finTradeProduct.setBranch(dto.getBranch());			//分支序号
			if("02".equals(dto.getManeWay())) {
				String periodId = "";		//期次代码
				//字典.隐性管理 eq 变量DTO.期限管理	
				if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
					//分支序号			认购确认日
					if(StringUtil.isNotEmpty(dto.getSubTermDays())) {
						finTradeProduct.setTerm(Long.valueOf(dto.getSubTermDays()));   //期限
					}
					finTradeProduct.setVdate(dto.getSubConfirmDate());		//确认日
		            finTradeProduct.setMdate(dto.getSubCycleEndDate());		//期次结束日
		            periodId = DateUtil.format(dto.getSubConfirmDate(), "yyyyMMdd")+dto.getSubTermDays()
		            				+CommonUtil.getSequenceIndexData(DataIndexSequenceType.期次管理, "%06d");
				}
				else {
					//分支序号			申购确认日
					if(StringUtil.isNotEmpty(dto.getPurTermDays())) {
						finTradeProduct.setTerm(Long.valueOf(dto.getPurTermDays()));   //期限
					}
					finTradeProduct.setVdate(dto.getPurConfirmDate());		//确认日
		            finTradeProduct.setMdate(dto.getPurCycleEndDate());		//期次结束日
		            periodId = DateUtil.format(dto.getPurConfirmDate(), "yyyyMMdd")+dto.getPurTermDays()
		            				+CommonUtil.getSequenceIndexData(DataIndexSequenceType.期次管理, "%06d");
				}
				//分支序号、待修改  debugger
				Long branch = Long.valueOf(baseGetBussinessIdMapper.getNextValBySequenceName("QCGL_FZXH_SEQUENCE"));
				finTradeProduct.setBranch(branch);			//设置分支序号
				//隐性管理设置期次代码              期次开始日+投资周期（天）+6位序号  
				finTradeProduct.setPeriodId(periodId);
				if (isRevoke) {
					Map<String, Object> requestMap = new HashMap<String, Object>();
					requestMap.put("tradeId", dto.getTradeId());
					TrdProductDeal trdProductDeal = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
        						+ "/FinNetProductDuration/getTrdProductDealByTradeId",
        						requestMap, TrdProductDeal.class);
					Map<String, Object> map = new HashMap<String, Object>();
					String cashId = commonBusinessService.generateCashFlowCode(trdProductDeal.getFinprodId(), 
							DictCodeListExt1.现金流一级类型_费用.getValue(), trdProductDeal.getBranch(), DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
			        map.put("finprodId", trdProductDeal.getFinprodId());
			        map.put("branch", trdProductDeal.getBranch());
			        map.put("cashId", cashId);
			        map.put("vDate", finTradeProduct.getVdate());
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
			                                   	+ "/FinProductEstablish/deleteFinTradeProductByFinprodId",
		                   map, Integer.class);
			        //删除金融产品标的拓展表
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
		                					+ "/FinNetProductDuration/deleteFinTradeProductAddByFinprodId",
		                	map, Integer.class);
			        // 现金流信息、现金计算规则（金融产品代码+成立日分支序号）
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
			                                   	+ "/FinProductEstablish/deleteFinCashInfoByFinprodId",
		                   map, Integer.class);
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
			                                   	+ "/FinProductEstablish/deleteFinCashCalcRuleByFinprodId",
		                   map, Integer.class);
			        //删除业绩报酬计算规则(FIN_FLOAT_CALC_RULE)				
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
		                					+ "/FinNetProductDuration/deleteFinFloatCalcRuleByFinprodId",
		                	map, Integer.class);
			        //删除业绩报酬计算规则明细(FIN_FLOAT_CALC_RULE_DETAIL)				
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
		                					+ "/FinNetProductDuration/deleteFinFloatCalcRuleDetailByFinprodId",
		                	map, Integer.class);
			        ////删除业绩基准表(FIN_BASE_RULE_INFO)				
			        restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
        					+ "/FinNetProductDuration/deleteFinBaseRuleInfoByFinprodId",
        						map, Integer.class);
				}else{
					//finTradeProduct 、 finTradeProductAdd 入库
					finTradeProduct = genFinTradeProduct(dto , finTradeProduct);
					//现金流相关输入入库
					//genFinCashInfo(dto,finTradeProduct);
				}
			}
			//显性管理	设置期次子产品分支序号
			else {
				if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
					//分支序号			期次子产品分支序号
					finTradeProduct.setBranch(dto.getSubPeriodBranch());
					finTradeProduct.setPeriodId(dto.getSubPeriodId());		//期次代码
				}
				else {
					//分支序号			期次子产品分支序号
					finTradeProduct.setBranch(dto.getPurPeriodBranch());
					finTradeProduct.setPeriodId(dto.getPurPeriodId());		//期次代码
				}
				Map<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("periodId", finTradeProduct.getPeriodId());
				finTradeProduct = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
    						+ "/FinNetProductDuration/getFinTradeProductByPeriodId",
    						requestMap, FinTradeProduct.class);
			}
		}
		// add by gcl 2019/3/27 start
	    if (isRevoke) {
		    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
		        // 只有当交易表中该产品无认购交易时才将发行状态修改为确认发行
		        Map<String, Object> map = new HashMap<String, Object>();
	            map.put("finprodId", dto.getFinprodId());
	            map.put("tradeId", dto.getTradeId());
	            Integer OtherSurDeal = restTemplate.postForObject(
	                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEstablish/selectOtherSurDeal", map, Integer.class);
	            if (OtherSurDeal < 1) {
	                FinNetValueProductBuild003Dto finNetValueProductBuild003Dto = new FinNetValueProductBuild003Dto();
	                finNetValueProductBuild003Dto.setFinprodId(dto.getFinprodId());
	                finNetValueProductBuild003Dto.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
	                finNetValueProductBuild003Dto.setNewIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
	                finNetValueProductBuild003Dto.setCreateUser(dto.getCreateUser());
	                finNetValueProductBuild003Dto.setCreateDept(dto.getCreateDept());
	                finNetValueProductBuild003Dto.setCreateTime(dto.getCreateTime());
	                finNetValueProductBuild003Dto.setUpdateUser(dto.getUpdateUser());
	                finNetValueProductBuild003Dto.setUpdateTime(dto.getCreateTime());
	                restTemplate.postForObject(
	                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductBuild/updateIssueStatusByFinprodId",
	                    finNetValueProductBuild003Dto, Integer.class);
	            }
	        }
	    }
	    else {
	        if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
	            FinNetValueProductBuild003Dto finNetValueProductBuild003Dto = new FinNetValueProductBuild003Dto();
	            finNetValueProductBuild003Dto.setFinprodId(dto.getFinprodId());
	            finNetValueProductBuild003Dto.setIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
	            finNetValueProductBuild003Dto.setNewIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
	            finNetValueProductBuild003Dto.setCreateUser(dto.getCreateUser());
	            finNetValueProductBuild003Dto.setCreateDept(dto.getCreateDept());
	            finNetValueProductBuild003Dto.setCreateTime(dto.getCreateTime());
	            finNetValueProductBuild003Dto.setUpdateUser(dto.getUpdateUser());
	            finNetValueProductBuild003Dto.setUpdateTime(dto.getCreateTime());
	            restTemplate.postForObject(
	                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductBuild/updateIssueStatusByFinprodId",
	                finNetValueProductBuild003Dto, Integer.class);
	        }
	    }
	    // add by gcl 2019/3/27 end
        // 金融产品交易汇总表(TRD_FIN_PRODUCT_DEAL_TOTAL)
        TrdFinProductDealTotal trdFinProductDealTotal = genTrdFinProductDealTotal(dto);
        // 根据汇总交易编号查询金融产品交易汇总
        if (isRevoke) {
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);

            // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
            // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
            trdFinProductDealTotal.setTotleTradeId(
            trdFinProductDealTotal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
            trdFinProductDealTotal.setWorkflowId(dto.getWorkflowId());
        }

        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
        // add by zlp 20190318 start //指令凭证打印添加类型
        // modify by wm start 净值分档不在此处处理处理 2019-06-26
        PaTrdProductDeal paTrdProductDeal = null;
		if(!(StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())
              && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), dto.getProfitType()))) {
            paTrdProductDeal = new PaTrdProductDeal();
			paTrdProductDeal.setTradeId(dto.getTradeId());
			if (StringUtil.equals(DictCodeList.交易方向_产品认购.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品认购.getValue());
				paTrdProductDeal.setCustomersBeginNum(dto.getCustomersBeginNum());//期初客户数
			} else if (StringUtil.equals(DictCodeList.交易方向_产品申购.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品申购.getValue());
				paTrdProductDeal.setCustomersAddNum(dto.getCustomersAddNum());//增加客户数
			} else if (StringUtil.equals(DictCodeList.交易方向_份额强增.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强增.getValue());
			} else if (StringUtil.equals(DictCodeList.交易方向_产品赎回.getValue(), dto.getDealType())
					|| StringUtil.equals(DictCodeList.交易方向_产品提前赎回.getValue(), dto.getDealType())
					|| StringUtil.equals(DictCodeList.交易方向_产品违约赎回.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品赎回_提前赎回_违约赎回.getValue());
				paTrdProductDeal.setCustomersReduceNum(dto.getCustomersReduceNum());//减少客户数
			} else if (StringUtil.equals(DictCodeList.交易方向_产品到期.getValue(), dto.getDealType())
					|| StringUtil.equals(DictCodeList.交易方向_提前终止.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品到期_提前终止.getValue());
			} else if (StringUtil.equals(DictCodeList.交易方向_份额强减.getValue(), dto.getDealType())) {
				paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强减.getValue());
			}
		}
		if (paTrdProductDeal != null) {
			if (isRevoke) {
				paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
            }
        }
        if (paTrdProductDeal != null) {
            if (isRevoke) {
                paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
            }

            long isInsert = this.checkId(paTrdProductDeal.getTradeId());
            if(isInsert == 0)
            {
                paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            }

        }
        
        // PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // paTrdProductDeal.setTradeId(dto.getTradeId());
        // if (StringUtil.equals(DictCodeList.交易方向_产品认购.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品认购.getValue());
        // paTrdProductDeal.setCustomersBeginNum(dto.getCustomersBeginNum());//期初客户数
        // } else if (StringUtil.equals(DictCodeList.交易方向_产品申购.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品申购.getValue());
        // paTrdProductDeal.setCustomersAddNum(dto.getCustomersAddNum());//增加客户数
        // } else if (StringUtil.equals(DictCodeList.交易方向_份额强增.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强增.getValue());
        // } else if (StringUtil.equals(DictCodeList.交易方向_产品赎回.getValue(), dto.getDealType())
        // || StringUtil.equals(DictCodeList.交易方向_产品提前赎回.getValue(), dto.getDealType())
        // || StringUtil.equals(DictCodeList.交易方向_产品违约赎回.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品赎回_提前赎回_违约赎回.getValue());
        // paTrdProductDeal.setCustomersReduceNum(dto.getCustomersReduceNum());//减少客户数
        // } else if (StringUtil.equals(DictCodeList.交易方向_产品到期.getValue(), dto.getDealType())
        // || StringUtil.equals(DictCodeList.交易方向_提前终止.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品到期_提前终止.getValue());
        // } else if (StringUtil.equals(DictCodeList.交易方向_份额强减.getValue(), dto.getDealType())) {
        // paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强减.getValue());
        // }
        // bussCommonMapperDelegateService.insertSelective(PaTrdProductDealMapper.class, paTrdProductDeal);
        // add by zlp 20190318 end
        // modify by wm end 净值分档不在此处处理2019-06-26
        if (isRevoke) {
            List<TrdProductDeal> trdProductDealList = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdProductDealByTotleTradeId?tradeId="
                                                                              + dto.getTradeId(),
                TrdProductDeal.class);
            trdProductDealList.forEach(trdProductDeal -> {
                // 由于撤销时取的值未原交易的值，交易新建时，分层信息取到的tradeId为空，故在此处赋值
                if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        if (StringUtil.equals(trdProductDeal.getFinprodId(), finNetProductDurationLayerDto.getFinprodId())) {
                            finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());
                        }
                    }
                }
                // 交易状态=“撤销”
                //trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
                trdProductDealMapper.updateByPrimaryKeySelective(trdProductDeal);

				trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                trdProductDeal.setpTradeId(trdProductDeal.getTradeId());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                trdProductDeal.setTotleTradeId(
                    trdProductDeal.getTotleTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                trdProductDeal.setTradeId(trdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 start 2019-09-02
                trdProductDeal.setCreateTime(DateUtil.now());
                trdProductDeal.setUpdateTime(DateUtil.now());
                // add by wm将带R的交易的创建时间与更新时间修改为最新的系统时间 end 2019-09-02
                // 撤销说明=画面.撤销说明
                trdProductDeal.setCancelRemark(dto.getRevokeReason());
                trdProductDealMapper.insertSelective(trdProductDeal);

                // 调用估值入账接口
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(trdProductDeal.getFinprodId());
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), trdProductDeal.getFinprodType2())) {
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
                }
                else {
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                try {
                    inputDto.setProdVdate(
                        DateUtil.parseDate(String.valueOf(trdProductDeal.getBranch()), DateUtil.DEFAULT_OTHER_FORMAT));
                }
                catch (ParseException e) {
                    inputDto.setProdVdate(trdProductDeal.getVdate());
                }
                inputDto.setTrdProductDeal(trdProductDeal);
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

            });

            List<TrdProductDealAdd> trdProductDealAddList = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdProductDealAddByTotleTradeId?tradeId="
                                                                                    + dto.getTradeId(),
                TrdProductDealAdd.class);
            trdProductDealAddList.forEach(trdProductDealAdd -> {
                trdProductDealAdd.setTradeId(trdProductDealAdd.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
            });

            List<PtlTrade> listPtlTrade = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryPtlTradeByTotleTradeId?tradeId="
                                                                  + dto.getTradeId(),
                PtlTrade.class);
            listPtlTrade.forEach(ptlTrade -> {
                ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                ptlTradeMapper.insertSelective(ptlTrade);
            });

            List<StlPayOrders> listStlPayOrders = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryStlPayOrdersByTradeId?tradeId="
                                                                          + dto.getTradeId(),
                StlPayOrders.class);
            listStlPayOrders.forEach(stlPayOrdersDto -> {
                stlPayOrdersDto.setpTradeId(dto.getTradeId());
                stlPayOrdersDto.setOrderId(
                    StringUtil.defaultString(stlPayOrdersDto.getOrderId()) + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrdersDto.setTradeId(
                    StringUtil.defaultString(stlPayOrdersDto.getTradeId()) + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                stlPayOrdersDto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
            });

            // add by zhangJin 邮储项目个性化-TA交易发送 start
            // 发送TA申购数据
            if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
                if (StringUtil.isNotEmpty(dto.getDealType())
                    && (StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品申购.getValue())
                        || StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品赎回.getValue())
                        || StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品提前赎回.getValue()))) {
                    BigDecimal tradeAmt = BigDecimal.ZERO;
                    BigDecimal shareAmt = BigDecimal.ZERO;
                    BigDecimal feeAmt = BigDecimal.ZERO;
                    for (TrdProductDeal temp : trdProductDealList) {
                        tradeAmt = ArithmeticUtil.doAdd(tradeAmt, temp.getTradeAmt());
                        shareAmt = ArithmeticUtil.doAdd(shareAmt, temp.getShareAmt());
                        feeAmt = ArithmeticUtil.doAdd(feeAmt, temp.getFeeAmt());
                    }
                    TrdProductDeal deal = new TrdProductDeal();
                    deal.setFinprodId(dto.getFinprodId());
                    deal.setTradeAmt(tradeAmt);
                    deal.setSettleDate(trdProductDealList.get(0).getSettleDate());
                    deal.setPs(dto.getDealType());
                    deal.setFeeAmt(feeAmt);
                    deal.setShareAmt(shareAmt);
                    deal.setTradeId(dto.getTradeId());
                    deal.setStatus("3");
                    restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/custodian/sendProdTADeal", deal,
                        String.class);
                }
                // add by zhangJin 邮储项目个性化-TA交易发送 end
            }
        }
        else {
        	List<BaseTradeProductDealDto> listBaseTradeProductDealDto = genBaseTradeProductDealDto(dto , finTradeProduct);
            for (BaseTradeProductDealDto baseTradeProductDealDto : listBaseTradeProductDealDto) {
                // listBaseTradeProductDealDto.forEach(baseTradeProductDealDto -> {
            	if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDeal())) {
            		trdProductDealMapper.insertSelective(baseTradeProductDealDto.getTrdProductDeal());
            	}
            	if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdProductDealAdd())) {
            		trdProductDealAddMapper.insertSelective(baseTradeProductDealDto.getTrdProductDealAdd());
            	}
            	if(StringUtil.isNotEmpty(baseTradeProductDealDto.getTrdTradeFeeDetailList())) {
            		for(TrdTradeFeeDetail trdTradeFeeDetail : baseTradeProductDealDto.getTrdTradeFeeDetailList()) {
            			trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
            		}
            	}
                // add by wm 净值型分档金融产品交易扩展在此处存储 start 2019-06-26
                if (baseTradeProductDealDto.getPaTrdProductDeal() != null) {
                    paTrdProductDealMapper.insertSelective(baseTradeProductDealDto.getPaTrdProductDeal());
                }
                // add by wm 净值型分档金融产品交易扩展在此处存储 end 2019-06-26
                // 调用估值入账接口
                AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                inputDto.setProdid(baseTradeProductDealDto.getTrdProductDeal().getFinprodId());
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(),
                    baseTradeProductDealDto.getTrdProductDeal().getFinprodType2())) {
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
                }
                else {
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                }
                try {
                    inputDto.setProdVdate(DateUtil.parseDate(
                        String.valueOf(baseTradeProductDealDto.getTrdProductDeal().getBranch()), DateUtil.DEFAULT_OTHER_FORMAT));
                }
                catch (ParseException e) {
                    inputDto.setProdVdate(baseTradeProductDealDto.getTrdProductDeal().getVdate());
                }
                //净值型 周期型 使用母产品入账
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                    TrdProductDeal trdProductDealforVal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDealforVal,baseTradeProductDealDto.getTrdProductDeal());
                    trdProductDealforVal.setFinprodId(dto.getFinprodId());
                    trdProductDealforVal.setBranch(0l);
                    trdProductDealforVal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                    trdProductDealforVal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                    inputDto.setTrdProductDeal(trdProductDealforVal);
                    inputDto.setProdid(dto.getFinprodId());
                    inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                } else {
                    TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
                    inputDto.setTrdProductDeal(trdProductDeal);
                }
                // 强增，强减只输入交易金额或交易份额，不调用估值接口
//                if (DictCodeList.交易方向_份额强增.getValue().equals(inputDto.getTrdProductDeal().getPs())
//                    || DictCodeList.交易方向_份额强减.getValue().equals(inputDto.getTrdProductDeal().getPs())) {
//                    if (inputDto.getTrdProductDeal().getShareAmt() == null
//                        || inputDto.getTrdProductDeal().getTradeAmt() == null) {
//                        continue;
//                    }
//                }
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            }

            // 组合交易表(PTL_TRADE)
            List<PtlTrade> listPtlTrade = getPtlTrade(dto, listBaseTradeProductDealDto);
            listPtlTrade.forEach(ptlTrade -> {
                ptlTradeMapper.insertSelective(ptlTrade);
            });

            // 划款指令
            if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
                BussTrdTransferOrderDto orderDto = dto.getBussTrdTransferOrder().get(0);
                StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, orderDto);
                stlPayOrdersDto.setTradeId(dto.getTradeId());
                if (orderDto.getPs().equals("01")) {
                    stlPayOrdersDto.setMyAcctNo(orderDto.getRecAcctNo());
                    stlPayOrdersDto.setCounterAcctNo(orderDto.getPayAcctNo());
                }
                else {
                    stlPayOrdersDto.setMyAcctNo(orderDto.getPayAcctNo());
                    // SHBS-1205 lijun add 20180322
                    stlPayOrdersDto.setCounterAcctNo(orderDto.getRecAcctNo());
                }
                if (isRevoke) {
                    stlPayOrdersDto.setSettleAmt(stlPayOrdersDto.getSettleAmt().negate());
                    stlPayOrdersDto.setTradeId(dto.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    stlPayOrdersDto.setOrderId(orderDto.getOrderId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    stlPayOrdersDto.setpTradeId(orderDto.getOrderId());
                }
                stlPayOrdersDto.setTradeId(dto.getTradeId());
                stlPayOrdersDto.setPayType(orderDto.getPayType());
                stlPayOrdersDto.setWorkflowId(dto.getWorkflowId());
                stlPayOrdersMapper.insertSelective(stlPayOrdersDto);
            }

            // add by zhangJin 邮储项目个性化-TA交易发送 start
            if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
                List<TrdProductDeal> dealList = new ArrayList<>();
                for (BaseTradeProductDealDto baseTradeProductDealDto : listBaseTradeProductDealDto) {
                    dealList.add(baseTradeProductDealDto.getTrdProductDeal());
                }
                // 发送建账
                if (StringUtil.isNotEmpty(dto.getDealType())
                    && StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品认购.getValue())) {
                    restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/custodian/sendProdRelease",
                        dto.getFinprodId(), String.class);
                }
                // 发送TA申购数据
                else if (StringUtil.isNotEmpty(dto.getDealType())
                         && (StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品申购.getValue())
                             || StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品赎回.getValue())
                             || StringUtil.equals(dto.getDealType(), DictCodeList.交易方向_产品提前赎回.getValue()))) {
                    BigDecimal tradeAmt = BigDecimal.ZERO;
                    BigDecimal shareAmt = BigDecimal.ZERO;
                    BigDecimal feeAmt = BigDecimal.ZERO;
                    for (TrdProductDeal temp : dealList) {
                        tradeAmt = ArithmeticUtil.doAdd(tradeAmt, temp.getTradeAmt());
                        shareAmt = ArithmeticUtil.doAdd(shareAmt, temp.getShareAmt());
                        feeAmt = ArithmeticUtil.doAdd(feeAmt, temp.getFeeAmt());
                    }
                    TrdProductDeal deal = new TrdProductDeal();
                    deal.setFinprodId(dto.getFinprodId());
                    deal.setTradeAmt(tradeAmt);
                    deal.setSettleDate(dealList.get(0).getSettleDate());
                    deal.setPs(dto.getDealType());
                    deal.setFeeAmt(feeAmt);
                    deal.setShareAmt(shareAmt);
                    deal.setTradeId(dto.getTradeId());
                    deal.setStatus("0");
                    restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/custodian/sendProdTADeal", deal,
                        String.class);
                }
                // add by zhangJin 邮储项目个性化-TA交易发送 end
            }
        }
        // 组合持仓流水Entity
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = genPtlSecPositionFlowDto(dto, finTradeProduct, isRevoke);
        // 组合现金流水Entity
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = genPtlCashPositionFlowDto(dto, isRevoke);
        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
        if (ptlPositionHoldDto != null) {
            if (ptlPositionHoldDto.getPtlSecPositionFlows() != null
                    && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                    basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
                }
            }
            if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
                    && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
                for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                    basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
                }
            }
        }
        if(DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType()) ||
				DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())  ||
				DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())  ||		
		        DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())  ||
		        DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())){
        	if(isRevoke) {
             ptlPositionHoldDto.setPtlSecPositionFlows(null);
             baseEtrCashPositionCommServiceImpl.portfolioEtrCashPosition(ptlPositionHoldDto, true);
        	}
        }else {
        	 ptlPositionHoldDto.setPtlSecPositionFlows(null);
             baseEtrCashPositionCommServiceImpl.portfolioEtrCashPosition(ptlPositionHoldDto, true);
        }
        // add by xy 2019-04-29 start (因为现金流查询需要，交易信息入金融产品交易汇总扩展表)
        if (isRevoke) {
            // 根据汇总交易编号查询金融产品交易汇总扩展
            PaTrdFinProductDealTotal paTrdFinProductDealTotal = productCommonService.genPaTrdFinProductDealTotalRevoke(
                dto.getTradeId());
            if (paTrdFinProductDealTotal != null) {
                paTrdFinProductDealTotalMapper.insert(paTrdFinProductDealTotal);
            }
        }
        else {
            PaTrdFinProductDealTotal paTrdFinProductDealTotal = genPaTrdFinProductDealTotal(dto);
            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

            if ("001".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))) {
                if (DictCodeList.交易方向_产品赎回.getValue().equals(trdFinProductDealTotal.getPs())
                    || DictCodeList.交易方向_产品到期.getValue().equals(trdFinProductDealTotal.getPs())
                    || DictCodeList.交易方向_产品现金分红.getValue().equals(trdFinProductDealTotal.getPs())) {
                    // 调用个性化接口- 资金到账标识接口 lijun add by 2019-08-12
                    restTemplate.postForObject(
                        ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/FinProductInterf/sendFundToTAccount",
                        trdFinProductDealTotal, String.class);
                }
            }
        }
        
        //20191008新增 
    	TrdAsynHandleStatus trdAsynHandleStatus = new TrdAsynHandleStatus();
        trdAsynHandleStatus.setTradeId(trdFinProductDealTotal.getTotleTradeId());		//交易汇总id
        trdAsynHandleStatus.setTrdTableType("01");		// 交易表类型(01：金融产品交易汇总表，02：金融产品交易表)
        trdAsynHandleStatus.setHandleStatus("01");		//01	待处理
        trdAsynHandleStatusMapper.insertSelective(trdAsynHandleStatus);
    }

    // add by xy start 2019-04-30(净值型产品存续期交易存金融产品交易汇总扩展表)
    private PaTrdFinProductDealTotal genPaTrdFinProductDealTotal(FinNetProductDurationDto dto) {
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())
            || DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            paTrdFinProductDealTotal.setTotleTradeId(dto.getTradeId());
            paTrdFinProductDealTotal.setUpdateUser(dto.getUpdateUser());
            paTrdFinProductDealTotal.setCreateUser(dto.getCreateUser());
            paTrdFinProductDealTotal.setCreateDept(dto.getCreateDept());
            paTrdFinProductDealTotal.setWorkflowId(dto.getWorkflowId());
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品成立.getValue());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品申购.getValue());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品赎回.getValue());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品到期.getValue());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品强增.getValue());
            }
            else if (DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品强减.getValue());
            }
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                // 中间付息和现金分红存的平安业务类型为产品中间付息
                paTrdFinProductDealTotal.setPaBussType(DictCodeListExt1.平安业务类型_产品现金分红.getValue());
            }
        }
        return paTrdFinProductDealTotal;
    }

    /**
     * 金融交易扩展表dto add by wm
     *
     * @param trdProductDeal 交融交易信息
     * @param dto 界面传入信息
     * @return 金融交易扩展表
     * @see
     * @since 1.0
     */
    private PaTrdProductDeal genPaTrdProductDeal(TrdProductDeal trdProductDeal, FinNetProductDurationDto dto) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
        if (StringUtil.equals(DictCodeList.交易方向_产品认购.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品认购.getValue());
            paTrdProductDeal.setCustomersBeginNum(dto.getCustomersBeginNum());// 期初客户数
        }
        else if (StringUtil.equals(DictCodeList.交易方向_产品申购.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品申购.getValue());
            paTrdProductDeal.setCustomersAddNum(dto.getCustomersAddNum());// 增加客户数
        }
        else if (StringUtil.equals(DictCodeList.交易方向_份额强增.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强增.getValue());
        }
        else if (StringUtil.equals(DictCodeList.交易方向_产品赎回.getValue(), dto.getDealType())
                 || StringUtil.equals(DictCodeList.交易方向_产品提前赎回.getValue(), dto.getDealType())
                 || StringUtil.equals(DictCodeList.交易方向_产品违约赎回.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品赎回_提前赎回_违约赎回.getValue());
            paTrdProductDeal.setCustomersReduceNum(dto.getCustomersReduceNum());// 减少客户数
        }
        else if (StringUtil.equals(DictCodeList.交易方向_产品到期.getValue(), dto.getDealType())
                 || StringUtil.equals(DictCodeList.交易方向_提前终止.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_产品到期_提前终止.getValue());
        }
        else if (StringUtil.equals(DictCodeList.交易方向_份额强减.getValue(), dto.getDealType())) {
            paTrdProductDeal.setOrderBussType(DictCodeListExt1.指令业务类型_强减.getValue());
        }
        return paTrdProductDeal;
    }

    private List<BaseTradeProductDealDto> genBaseTradeProductDealDto(FinNetProductDurationDto dto , FinTradeProduct finTradeProduct) {
        List<BaseTradeProductDealDto> listBaseTradeProductDealDto = new ArrayList<BaseTradeProductDealDto>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        TrdProductDeal trdProductDeal = genTrdProductDealCommon(dto);
        TrdProductDealAdd trdProductDealAdd = genTrdProductDealAddCommon(dto);
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
        if(DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())){
        	DictCodeList dictCodeList = this.getBussinessDictCodeList(dto);
        	if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())){
        		//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓净值型周期型改造 	20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        		if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
        			trdProductDeal.setFinprodId(finTradeProduct.getFinprodId()); 		//产品代码
        			trdProductDeal.setBranch(finTradeProduct.getBranch()); 			//分支序号
        		}
        		//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑净值型周期型改造	20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                if(!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                		&&StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())){
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();

                        trdProductDeal = genTrdProductDealCommon(dto);
                        trdProductDealAdd = genTrdProductDealAddCommon(dto);
                        if (StringUtil.isNotEmpty(finNetProductDurationLayerDto.getTradeId())) {
                            trdProductDeal.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        }
                        else {
                            String tradeId = genTradeId(dictCodeList);

                            trdProductDeal.setTradeId(tradeId);
                        }

                        finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());

                        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());

                        trdProductDeal.setCpriceDate(dto.getvDate());
                        trdProductDeal.setUnitCprice(BigDecimal.ONE);
                        trdProductDeal.setVdate(dto.getvDate());
                        trdProductDeal.setTradeDate(dto.getSubConfirmDate());
                        trdProductDeal.setFinprodType2(finNetProductDurationLayerDto.getFinprodType2());
                        trdProductDeal.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        trdProductDeal.setSettleDate(dto.getSubConfirmDate());
                        trdProductDeal.setShareAmt(finNetProductDurationLayerDto.getSubDealLayerAmt());
                        trdProductDeal.setPrinAmt(finNetProductDurationLayerDto.getSubDealLayerAmt());
                        trdProductDeal.setFpriceAmt(finNetProductDurationLayerDto.getSubDealLayerAmt());
                        trdProductDeal.setFeeAmtUnpay(finNetProductDurationLayerDto.getSubLayerFee());
                        trdProductDeal.setFeeAmt(finNetProductDurationLayerDto.getSubLayerFee());
                        trdProductDeal.setTradeAmt(finNetProductDurationLayerDto.getSubDealLayerAmt());
                        trdProductDeal.setTotleTradeId(dto.getTradeId());
                        trdProductDeal.setRemark(dto.getSubRemark());

                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                        PaTrdProductDeal paTrdProductDeal = genPaTrdProductDeal(trdProductDeal, dto);

                        baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);

                        if (finNetProductDurationLayerDto.getSubLayerFee() != null
                            && finNetProductDurationLayerDto.getSubLayerFee().compareTo(BigDecimal.ZERO) != 0) {
                            trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
                            TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                            // 设计增加
                            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                            trdTradeFeeDetail.setFeeAmt(finNetProductDurationLayerDto.getSubLayerFee());
                            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                            trdTradeFeeDetailList.add(trdTradeFeeDetail);
                        }

                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                    // 总费用 为了不影响代码结果放这里存储
                    if (dto.getSubFee() != null && dto.getSubFee().compareTo(BigDecimal.ZERO) != 0) {
                        TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                        trdTradeFeeDetail.setTradeId(dto.getTradeId());
                        trdTradeFeeDetail.setFeeAmt(dto.getSubFee());
                        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                        trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
                    }
                }
                else {
                    trdProductDeal.setTradeDate(dto.getSubConfirmDate());
                    trdProductDeal.setSettleDate(dto.getSubConfirmDate());
                    trdProductDeal.setShareAmt(dto.getSubDealNum());
                    trdProductDeal.setPrinAmt(dto.getSubDealAmt());
                    trdProductDeal.setFpriceAmt(dto.getSubDealAmt());
                    trdProductDeal.setFeeAmtUnpay(dto.getSubFee());
                    trdProductDeal.setFeeAmt(dto.getSubFee());
                    trdProductDeal.setTradeAmt(dto.getSubDealAmt());
                    trdProductDeal.setRemark(dto.getSubRemark());
                    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓净值型周期型改造 	20191017 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            		if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            			trdProductDeal.setVdate(finTradeProduct.getVdate());			//期次开始日
            			trdProductDeal.setMdate(finTradeProduct.getMdate());			//期次结束
            		}
            		//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑净值型周期型改造	20191017 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                    if (dto.getSubFee() != null && dto.getSubFee().compareTo(BigDecimal.ZERO) != 0) {
                        TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                        trdTradeFeeDetail.setFeeAmt(dto.getSubFee());
                        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                        trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    }
                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
            	//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓净值型周期型改造 	20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        		if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
        			trdProductDeal.setFinprodId(finTradeProduct.getFinprodId()); 		//产品代码
        			trdProductDeal.setBranch(finTradeProduct.getBranch()); 			//分支序号
        		}
        		//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑净值型周期型改造	20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();

                        trdProductDeal = genTrdProductDealCommon(dto);
                        trdProductDealAdd = genTrdProductDealAddCommon(dto);

                        if (StringUtil.isNotEmpty(finNetProductDurationLayerDto.getTradeId())) {
                            trdProductDeal.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        }
                        else {
                            String tradeId = genTradeId(dictCodeList);

                            trdProductDeal.setTradeId(tradeId);
                        }
                    	finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());
                    	trdProductDealAdd.setTermType(dto.getTermType()); 			//净值型周期型改造 	20190920	debug	dto.期限品种
                        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                        trdProductDeal.setFinprodType2(finNetProductDurationLayerDto.getFinprodType2());
                        trdProductDeal.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        trdProductDeal.setVdate(dto.getvDate());

                        trdProductDeal.setCpriceDate(finNetProductDurationLayerDto.getPurNetValueLayerDate());
                        trdProductDeal.setUnitCprice(finNetProductDurationLayerDto.getPurUnitNetLayerValue());
                        trdProductDeal.setTradeDate(dto.getPurConfirmDate());
                        trdProductDeal.setSettleDate(dto.getPurConfirmDate());
                        trdProductDeal.setShareAmt(finNetProductDurationLayerDto.getPurDealLayerNum());
                        trdProductDeal.setPrinAmt(finNetProductDurationLayerDto.getPurDealLayerAmt());
                        trdProductDeal.setFpriceAmt(finNetProductDurationLayerDto.getPurDealLayerAmt());
                        trdProductDeal.setFeeAmtUnpay(finNetProductDurationLayerDto.getPurLayerFee());
                        trdProductDeal.setFeeAmt(finNetProductDurationLayerDto.getPurLayerFee());
                        trdProductDeal.setTradeAmt(finNetProductDurationLayerDto.getPurDealLayerAmt());
                        trdProductDeal.setRemark(dto.getPurRemark());

                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                        PaTrdProductDeal paTrdProductDeal = genPaTrdProductDeal(trdProductDeal, dto);

                        baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);

                        if (finNetProductDurationLayerDto.getPurLayerFee() != null
                            && finNetProductDurationLayerDto.getPurLayerFee().compareTo(BigDecimal.ZERO) != 0) {
                            trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
                            TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                            // 设计增加
                            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                            trdTradeFeeDetail.setFeeAmt(finNetProductDurationLayerDto.getPurLayerFee());
                            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                            trdTradeFeeDetailList.add(trdTradeFeeDetail);
                        }
                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                    // 总费用
                    if (dto.getPurFee() != null && dto.getPurFee().compareTo(BigDecimal.ZERO) != 0) {
                        TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                        trdTradeFeeDetail.setTradeId(dto.getTradeId());
                        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                        trdTradeFeeDetail.setFeeAmt(dto.getPurFee());
                        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                        trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
                    }
                }
                else {
                    trdProductDeal.setTradeDate(dto.getPurConfirmDate());
                    trdProductDeal.setSettleDate(dto.getPurConfirmDate());
                    trdProductDeal.setShareAmt(dto.getPurDealNum());
                    trdProductDeal.setPrinAmt(dto.getPurDealAmt());
                    trdProductDeal.setFpriceAmt(dto.getPurDealAmt());
                    trdProductDeal.setFeeAmtUnpay(dto.getPurFee());
                    trdProductDeal.setFeeAmt(dto.getPurFee());
                    trdProductDeal.setTradeAmt(dto.getPurDealAmt());
                    trdProductDeal.setRemark(dto.getPurRemark());
                    //申购时将申购净值存入
                    trdProductDeal.setUnitCprice(dto.getPurUnitNetValue());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                    trdProductDeal.setCpriceDate(dto.getPurNetValueDate());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓净值型周期型改造 	20191017 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            		if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            			trdProductDeal.setVdate(finTradeProduct.getVdate());			//期次开始日
            			trdProductDeal.setMdate(finTradeProduct.getMdate());			//期次结束
            		}
            		//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑净值型周期型改造	20191017 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                    if (dto.getPurFee() != null && dto.getPurFee().compareTo(BigDecimal.ZERO) != 0) {
                        TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                        trdTradeFeeDetail.setFeeAmt(dto.getPurFee());
                        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                        trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    }
                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                    List<FinNetProductDurationDto> listDto = dto.getRedCycleFinNetProductDurationDtoList();
                    for (FinNetProductDurationDto entity : listDto) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();
                        trdProductDeal = genTrdProductDealCommon(entity);
                        String tradeId = genTradeId(DictCodeList.共通业务类型_产品赎回);
                        trdProductDeal.setTradeId(tradeId);
                        trdProductDeal.setTradeDate(dto.getRedConfirmDate());
                        trdProductDeal.setSettleDate(dto.getRedPayDate());
                        trdProductDeal.setShareAmt(entity.getRedDealNum());
                        trdProductDeal.setPrinAmt(entity.getRedDealAmt());
                        trdProductDeal.setFpriceAmt(entity.getRedDealAmt());
                        trdProductDeal.setFeeAmtUnpay(
                            entity.getRedPurFeeHd() == null ? BigDecimal.ZERO : entity.getRedPurFeeHd().add(
                                entity.getRedFee() == null ? BigDecimal.ZERO : entity.getRedFee()));
                        trdProductDeal.setFeeAmtPay(entity.getRedFeeLc());
						trdProductDeal.setFeeAmt(entity.getRedPurFeeHd() == null ? BigDecimal.ZERO .add(dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()): entity.getRedPurFeeHd().add(
								entity.getRedFee() == null ? BigDecimal.ZERO : entity.getRedFee()).add(
								entity.getRedFeeLc() == null ? BigDecimal.ZERO : entity.getRedFeeLc()));
                        trdProductDeal.setTradeAmt(entity.getRedTotalNetValue());
                        trdProductDeal.setRemark(entity.getRedRemark());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                        trdProductDeal.setCpriceDate(dto.getRedNetValueDate());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                        //净值型 周期型新增		20190927
                        trdProductDeal.setCpriceDate(entity.getRedNetValueDate());		//净值日期
                        trdProductDeal.setUnitCprice(entity.getRedUnitNetValue());		//单位净值
                        trdProductDeal.setTotleTradeId(dto.getTradeId());		//汇总交易编号
                        trdProductDeal.setPs(dto.getDealType());
                        trdProductDeal.setCcy(dto.getCcy());		//币种
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        trdProductDealAdd = genTrdProductDealAddCommon(entity);
                        trdProductDealAdd.setTradeId(tradeId);
                        trdProductDealAdd.setRedProfit(entity.getRedProfit());
                        trdProductDealAdd.setRedCost(entity.getRedCost());
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
                        //期次子产品：赎回费		20190927
                        if (entity.getRedFee() != null && entity.getRedFee().compareTo(BigDecimal.ZERO) != 0) {
                        	trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
                            TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                            trdTradeFeeDetail.setFeeAmt(entity.getRedFee());
                            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                            trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    	}
                        //净值型周期型新增	期次子产品：浮动管理费	20190927
                        if (entity.getRedManaFreeVal() != null && entity.getRedManaFreeVal().compareTo(BigDecimal.ZERO) != 0) {
                        	BaseTradeProductDealDto baseTradeProductDealDto_new = new BaseTradeProductDealDto();
                        	TrdProductDeal trdProductDeal_new = genTrdProductDealCommon(entity);
                        	trdProductDeal_new.setTradeId(genTradeId(DictCodeList.共通业务类型_产品赎回));	
                        	trdProductDeal_new.setTradeDate(dto.getRedConfirmDate());
                        	trdProductDeal_new.setSettleDate(dto.getRedPayDate());
                        	trdProductDeal_new.setIntAmt(entity.getRedManaFreeVal());	//利息总额（浮动管理费）
                        	trdProductDeal_new.setBusiType(DictCodeListExt2.业务类型_浮动管理费挂账交易.getValue());		//业务类型
                        	trdProductDeal_new.setPs(dto.getDealType());			//交易方向
                        	trdProductDeal_new.setTotleTradeId(dto.getTradeId());		//汇总交易编号
                        	trdProductDeal_new.setCcy(dto.getCcy());		//币种
                        	baseTradeProductDealDto_new.setTrdProductDeal(trdProductDeal_new);
                        	listBaseTradeProductDealDto.add(baseTradeProductDealDto_new);
                    	}
                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                         && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();

                        trdProductDeal = genTrdProductDealCommon(dto);
                        trdProductDealAdd = genTrdProductDealAddCommon(dto);

                        if (StringUtil.isNotEmpty(finNetProductDurationLayerDto.getTradeId())) {
                            trdProductDeal.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        }
                        else {
                            String tradeId = genTradeId(dictCodeList);

                            trdProductDeal.setTradeId(tradeId);
                        }
                        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                        trdProductDeal.setFinprodType2(finNetProductDurationLayerDto.getFinprodType2());
                        trdProductDeal.setFinprodId(finNetProductDurationLayerDto.getFinprodId());

                        finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());

                        trdProductDeal.setCpriceDate(finNetProductDurationLayerDto.getRedNetValueLayerDate());
                        trdProductDeal.setUnitCprice(finNetProductDurationLayerDto.getRedUnitNetLayerValue());
                        trdProductDeal.setTradeDate(dto.getRedConfirmDate());
                        trdProductDeal.setSettleDate(dto.getRedPayDate());
                        trdProductDeal.setShareAmt(finNetProductDurationLayerDto.getRedDealLayerNum());
                        trdProductDeal.setPrinAmt(finNetProductDurationLayerDto.getRedDealLayerAmt());
                        trdProductDeal.setFpriceAmt(finNetProductDurationLayerDto.getRedDealLayerAmt());
                        trdProductDeal.setFeeAmtUnpay(
                            finNetProductDurationLayerDto.getRedPurLayerFeeHd() == null ? BigDecimal.ZERO : finNetProductDurationLayerDto.getRedPurLayerFeeHd().add(
                                finNetProductDurationLayerDto.getRedFeeLcLayer() == null ? BigDecimal.ZERO : finNetProductDurationLayerDto.getRedFeeLcLayer()));
                        trdProductDeal.setFeeAmtPay(finNetProductDurationLayerDto.getRedFeeLcLayer());
                        trdProductDeal.setFeeAmt(
                            finNetProductDurationLayerDto.getRedPurLayerFeeHd() == null ? BigDecimal.ZERO : finNetProductDurationLayerDto.getRedPurLayerFeeHd().add(
                                finNetProductDurationLayerDto.getRedLayerFee() == null ? BigDecimal.ZERO : finNetProductDurationLayerDto.getRedLayerFee()).add(
                                    finNetProductDurationLayerDto.getRedFeeLcLayer() == null ? BigDecimal.ZERO : finNetProductDurationLayerDto.getRedFeeLcLayer()));
                        trdProductDeal.setTradeAmt(finNetProductDurationLayerDto.getRedTotalNetLayerValue());
                        trdProductDeal.setRemark(dto.getRedRemark());
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        trdProductDealAdd.setRedProfit(finNetProductDurationLayerDto.getRedLayerProfit());
                        trdProductDealAdd.setRedCost(finNetProductDurationLayerDto.getRedLayerCost());
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                        PaTrdProductDeal PaTrdProductDeal = genPaTrdProductDeal(trdProductDeal, dto);
                        baseTradeProductDealDto.setPaTrdProductDeal(PaTrdProductDeal);

                        if (dto.getRedFee() != null && dto.getRedFee().compareTo(BigDecimal.ZERO) != 0) {
                            trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
                            TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                            trdTradeFeeDetail.setFeeAmt(finNetProductDurationLayerDto.getRedLayerFee());
                            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                            trdTradeFeeDetailList.add(trdTradeFeeDetail);
                        }
                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else {
                	//申购时将申购净值存入
                    trdProductDeal.setUnitCprice(dto.getRedUnitNetValue());
                    
                    trdProductDeal.setTradeDate(dto.getRedConfirmDate());
                    trdProductDeal.setSettleDate(dto.getRedPayDate());
                    trdProductDeal.setShareAmt(dto.getRedDealNum());
                    trdProductDeal.setPrinAmt(dto.getRedDealAmt());
                    trdProductDeal.setFpriceAmt(dto.getRedDealAmt());
					trdProductDeal.setFeeAmtUnpay(dto.getRedPurFeeHd() == null ? BigDecimal.ZERO.add(dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()) : dto.getRedPurFeeHd().add(
							dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()));
                    trdProductDeal.setFeeAmtPay(dto.getRedFeeLc());
                    trdProductDeal.setFeeAmt(dto.getRedPurFeeHd() == null ? BigDecimal.ZERO : dto.getRedPurFeeHd().add(
                        dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()).add(
                            dto.getRedFeeLc() == null ? BigDecimal.ZERO : dto.getRedFeeLc()));
                    trdProductDeal.setTradeAmt(dto.getRedTotalNetValue());
                    trdProductDeal.setRemark(dto.getRedRemark());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                    trdProductDeal.setCpriceDate(dto.getRedNetValueDate());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                    
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                    trdProductDealAdd.setRedProfit(dto.getRedProfit());
                    trdProductDealAdd.setRedCost(dto.getRedCost());
                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                    if (dto.getRedFee() != null && dto.getRedFee().compareTo(BigDecimal.ZERO) != 0) {
                        TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                        trdTradeFeeDetail.setFeeAmt(dto.getRedFee());
                        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                        trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    }
                    // if(dto.getRedPurFeeHd()!=null && dto.getRedPurFeeHd().compareTo(BigDecimal.ZERO)!=0){
                    // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    // trdTradeFeeDetail.setFeeAmt(dto.getRedPurFeeHd());
                    // trdTradeFeeDetail.setFeeType(DictCodeListExt2.费用类型_申购费后端.getValue());
                    // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    // }
                    // if(dto.getRedFeeLc()!=null && dto.getRedFeeLc().compareTo(BigDecimal.ZERO)!=0){
                    // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    // trdTradeFeeDetail.setFeeAmt(dto.getRedFeeLc());
                    // trdTradeFeeDetail.setFeeType(DictCodeListExt2.费用类型_赎回费产品留存.getValue());
                    // trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                    // //add by zlp 20190320 start//产品费用存值调整
                    // trdProductDeal.setFeeAmtPay(dto.getRedFeeLc());//页面的赎回费产品留存
                    // if(dto.getRedDealAmt()!=null && dto.getRedDealAmt().compareTo(BigDecimal.ZERO)!=0){
                    // trdProductDeal.setTradeAmt(dto.getRedDealAmt().subtract(dto.getRedFeeLc()));//页面的交易金额-页面的赎回费产品留存
                    // }
                    // //add by zlp 20190320 end
                    // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    // }
                    // if(dto.getRedLiquidatedDamages()!=null && dto.getRedLiquidatedDamages().compareTo(BigDecimal.ZERO)!=0){
                    // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    // trdTradeFeeDetail.setFeeAmt(dto.getRedLiquidatedDamages());
                    // trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_违约金.getValue());
                    // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                    // }
                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                    List<FinNetProductDurationDto> listDto = dto.getExpireCycleFinNetProductDurationDtoList();
                    for (FinNetProductDurationDto entity : listDto) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();
                        trdProductDeal = genTrdProductDealCommon(entity);
                        String tradeId = genTradeId(DictCodeList.共通业务类型_收益型产品赎回到期管理);
                        trdProductDeal.setTradeId(tradeId);
                        trdProductDeal.setTradeDate(dto.getExpireConfirmDate());
                        trdProductDeal.setSettleDate(dto.getExpirePayDate());
                        trdProductDeal.setShareAmt(entity.getExpireDealNum());
                        trdProductDeal.setPrinAmt(entity.getExpireDealAmt());
                        trdProductDeal.setFpriceAmt(entity.getExpireDealAmt());
                        trdProductDeal.setTradeAmt(entity.getExpireDealAmt());
                        trdProductDeal.setRemark(entity.getExpireRemark());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                        trdProductDeal.setCpriceDate(dto.getExpireNetValueDate());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                        //净值型 周期型新增		20190927
                        trdProductDeal.setCpriceDate(entity.getExpireNetValueDate());		//净值日期
                        trdProductDeal.setUnitCprice(entity.getExpireUnitNetValue());		//单位净值
                        trdProductDeal.setTotleTradeId(dto.getTradeId());		//汇总交易编号
                        trdProductDeal.setPs(dto.getDealType());
                        trdProductDeal.setCcy(dto.getCcy());
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        trdProductDealAdd = genTrdProductDealAddCommon(entity);
                        trdProductDealAdd.setTradeId(tradeId);
                        trdProductDealAdd.setRedProfit(entity.getExpireProfit());
                        trdProductDealAdd.setRedCost(entity.getExpireCost());
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
                        //净值型周期型新增	期次子产品：浮动管理费	20190927
                        if (entity.getExpireManaFreeVal() != null && entity.getExpireManaFreeVal().compareTo(BigDecimal.ZERO) != 0) {
                        	BaseTradeProductDealDto baseTradeProductDealDto_new = new BaseTradeProductDealDto();
                        	TrdProductDeal trdProductDeal_new = genTrdProductDealCommon(entity);
                        	trdProductDeal_new.setTradeId(genTradeId(DictCodeList.共通业务类型_产品赎回));	
                        	trdProductDeal_new.setTradeDate(dto.getExpireConfirmDate());
                        	trdProductDeal_new.setSettleDate(dto.getExpirePayDate());
                        	trdProductDeal_new.setIntAmt(entity.getExpireManaFreeVal());	//利息总额（浮动管理费）
                        	trdProductDeal_new.setBusiType(DictCodeListExt2.业务类型_浮动管理费挂账交易.getValue());		//业务类型	debug
                        	trdProductDeal_new.setPs(dto.getDealType());			//交易方向
                        	trdProductDeal_new.setTotleTradeId(dto.getTradeId());		//汇总交易编号
                        	trdProductDeal_new.setCcy(dto.getCcy());
                        	baseTradeProductDealDto_new.setTrdProductDeal(trdProductDeal_new);
                        	listBaseTradeProductDealDto.add(baseTradeProductDealDto_new);
                    	}
                        //baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
                        
                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                         && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();

                        trdProductDeal = genTrdProductDealCommon(dto);
                        trdProductDealAdd = genTrdProductDealAddCommon(dto);
                        if (StringUtil.isNotEmpty(finNetProductDurationLayerDto.getTradeId())) {
                            trdProductDeal.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        }
                        else {
                            String tradeId = genTradeId(dictCodeList);

                            trdProductDeal.setTradeId(tradeId);
                        }

                        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                        trdProductDeal.setFinprodType2(finNetProductDurationLayerDto.getFinprodType2());
                        trdProductDeal.setFinprodId(finNetProductDurationLayerDto.getFinprodId());

                        finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());

                        trdProductDeal.setCpriceDate(finNetProductDurationLayerDto.getExpireNetValueLayerDate());
                        trdProductDeal.setUnitCprice(finNetProductDurationLayerDto.getExpireUnitNetLayerValue());
                        trdProductDeal.setTradeDate(dto.getExpireConfirmDate());
                        trdProductDeal.setSettleDate(dto.getExpirePayDate());
                        trdProductDeal.setShareAmt(finNetProductDurationLayerDto.getExpireDealLayerNum());
                        trdProductDeal.setPrinAmt(finNetProductDurationLayerDto.getExpireDealLayerAmt());
                        trdProductDeal.setFpriceAmt(finNetProductDurationLayerDto.getExpireDealLayerAmt());
                        trdProductDeal.setTradeAmt(finNetProductDurationLayerDto.getExpireDealLayerAmt());
                        trdProductDeal.setRemark(dto.getExpireRemark());
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        trdProductDealAdd.setRedProfit(finNetProductDurationLayerDto.getExpireLayerProfit());
                        trdProductDealAdd.setRedCost(finNetProductDurationLayerDto.getExpireLayerCost());
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        PaTrdProductDeal PaTrdProductDeal = genPaTrdProductDeal(trdProductDeal, dto);
                        baseTradeProductDealDto.setPaTrdProductDeal(PaTrdProductDeal);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else {
                	//净值日存入deal表
                    trdProductDeal.setUnitCprice(dto.getExpireUnitNetValue());
                    
                    trdProductDeal.setTradeDate(dto.getExpireConfirmDate());
                    trdProductDeal.setSettleDate(dto.getExpirePayDate());
                    trdProductDeal.setShareAmt(dto.getExpireDealNum());
                    trdProductDeal.setPrinAmt(dto.getExpireDealAmt());
                    trdProductDeal.setFpriceAmt(dto.getExpireDealAmt());
                    trdProductDeal.setTradeAmt(dto.getExpireDealAmt());
                    trdProductDeal.setRemark(dto.getExpireRemark());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                    trdProductDeal.setCpriceDate(dto.getExpireNetValueDate());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                    trdProductDealAdd.setRedProfit(dto.getExpireProfit());
                    trdProductDealAdd.setRedCost(dto.getExpireCost());
                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                    List<FinNetProductDurationDto> listDto = dto.getAddReduceCycleFinNetProductDurationDtoList();
                    for (FinNetProductDurationDto entity : listDto) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();
                        trdProductDeal = genTrdProductDealCommon(entity);
                        String tradeId = genTradeId(DictCodeList.共通业务类型_收益型产品提前赎回管理);
                        trdProductDeal.setTradeId(tradeId);
                        trdProductDeal.setTradeDate(dto.getAddReduceConfirmDate());
                        trdProductDeal.setSettleDate(dto.getAddReduceConfirmDate());
                        trdProductDeal.setShareAmt(entity.getAddReduceDealNum());
                        trdProductDeal.setPrinAmt(entity.getAddReduceDealAmt());
                        trdProductDeal.setFpriceAmt(entity.getAddReduceDealAmt());
                        trdProductDeal.setTradeAmt(entity.getAddReduceDealAmt());
                        trdProductDeal.setRemark(entity.getAddReduceRemark());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                        trdProductDeal.setCpriceDate(dto.getAddReduceNetValueDate());
                        //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                        //净值型 周期型新增		20190927
                        trdProductDeal.setCpriceDate(entity.getAddReduceNetValueDate());		//净值日期
                        trdProductDeal.setUnitCprice(entity.getAddReduceUnitNetValue());		//单位净值
                        trdProductDeal.setTotleTradeId(dto.getTradeId());		//汇总交易编号
                        trdProductDeal.setPs(dto.getDealType());
                        trdProductDeal.setCcy(dto.getCcy());
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        trdProductDealAdd = genTrdProductDealAddCommon(entity);
                        trdProductDealAdd.setTradeId(tradeId);
                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
                        
                        //baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                         && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        baseTradeProductDealDto = new BaseTradeProductDealDto();

                        trdProductDeal = genTrdProductDealCommon(dto);
                        trdProductDealAdd = genTrdProductDealAddCommon(dto);

                        if (StringUtil.isNotEmpty(finNetProductDurationLayerDto.getTradeId())) {
                            trdProductDeal.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        }
                        else {
                            String tradeId = genTradeId(dictCodeList);

                            trdProductDeal.setTradeId(tradeId);
                        }

                        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                        trdProductDeal.setFinprodType2(finNetProductDurationLayerDto.getFinprodType2());
                        trdProductDeal.setFinprodId(finNetProductDurationLayerDto.getFinprodId());

                        finNetProductDurationLayerDto.setTradeId(trdProductDeal.getTradeId());

                        trdProductDeal.setCpriceDate(finNetProductDurationLayerDto.getAddReduceNetValueLayerDate());
                        trdProductDeal.setUnitCprice(finNetProductDurationLayerDto.getAddReduceUnitNetLayerValue());
                        trdProductDeal.setTradeDate(dto.getAddReduceConfirmDate());
                        trdProductDeal.setSettleDate(dto.getAddReduceConfirmDate());
                        trdProductDeal.setShareAmt(finNetProductDurationLayerDto.getAddReduceDealLayerNum());
                        trdProductDeal.setPrinAmt(finNetProductDurationLayerDto.getAddReduceDealLayerAmt());
                        trdProductDeal.setFpriceAmt(finNetProductDurationLayerDto.getAddReduceDealLayerAmt());
                        trdProductDeal.setTradeAmt(finNetProductDurationLayerDto.getAddReduceDealLayerAmt());
                        trdProductDeal.setRemark(dto.getAddReduceRemark());
                        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                        baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                        baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                        PaTrdProductDeal PaTrdProductDeal = genPaTrdProductDeal(trdProductDeal, dto);
                        baseTradeProductDealDto.setPaTrdProductDeal(PaTrdProductDeal);

                        listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                    }
                }
                else {
                	//净值日存入deal表
                    trdProductDeal.setUnitCprice(dto.getAddReduceUnitNetValue());
                    
                    trdProductDeal.setTradeDate(dto.getAddReduceConfirmDate());
                    trdProductDeal.setSettleDate(dto.getAddReduceConfirmDate());
                    trdProductDeal.setShareAmt(dto.getAddReduceDealNum());
                    trdProductDeal.setPrinAmt(dto.getAddReduceDealAmt());
                    trdProductDeal.setFpriceAmt(dto.getAddReduceDealAmt());
                    trdProductDeal.setTradeAmt(dto.getAddReduceDealAmt());
                    trdProductDeal.setRemark(dto.getAddReduceRemark());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 start
                    trdProductDeal.setCpriceDate(dto.getAddReduceNetValueDate());
                    //add by chengzhong for 净值型产品缺少净值日 2019-11-12 end
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                trdProductDeal.setTradeDate(dto.getSubConfirmDate03());
                trdProductDeal.setSettleDate(dto.getSubConfirmDate03());
                trdProductDeal.setShareAmt(dto.getSubDealAmt03());
                trdProductDeal.setPrinAmt(dto.getSubDealAmt03());
                trdProductDeal.setFpriceAmt(dto.getSubDealAmt03());
                trdProductDeal.setFeeAmtUnpay(dto.getSubFee03());
                trdProductDeal.setFeeAmt(dto.getSubFee03());
                trdProductDeal.setTradeAmt(dto.getSubDealAmt03());
                trdProductDeal.setRemark(dto.getSubRemark03());

                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                if (dto.getSubFee03() != null && dto.getSubFee03().compareTo(BigDecimal.ZERO) != 0) {
                    TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    trdTradeFeeDetail.setFeeAmt(dto.getSubFee03());
                    trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                    trdTradeFeeDetailList.add(trdTradeFeeDetail);
                }
                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                trdProductDeal.setTradeDate(dto.getPurConfirmDate03());
                trdProductDeal.setSettleDate(dto.getPurConfirmDate03());
                trdProductDeal.setShareAmt(dto.getPurDealAmt03());
                trdProductDeal.setPrinAmt(dto.getPurDealAmt03());
                trdProductDeal.setFpriceAmt(dto.getPurDealAmt03());
                trdProductDeal.setFeeAmtUnpay(dto.getPurFee03());
                trdProductDeal.setFeeAmt(dto.getPurFee03());
                trdProductDeal.setTradeAmt(dto.getPurDealAmt03());
                trdProductDeal.setRemark(dto.getPurRemark03());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                if (dto.getPurFee03() != null && dto.getPurFee03().compareTo(BigDecimal.ZERO) != 0) {
                    TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    trdTradeFeeDetail.setFeeAmt(dto.getPurFee03());
                    trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                    trdTradeFeeDetailList.add(trdTradeFeeDetail);
                }
                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                trdProductDeal.setTradeDate(dto.getRedConfirmDate03());
                trdProductDeal.setSettleDate(dto.getRedPrinPayDate03());
                trdProductDeal.setShareAmt(dto.getRedPrinAmt03());
                // trdProductDeal.setIntAmt(dto.getRedProfit03());
                trdProductDeal.setPrinAmt(dto.getRedPrinAmt03());
                trdProductDeal.setFpriceAmt(dto.getRedPrinAmt03());
                trdProductDeal.setFeeAmtUnpay(dto.getRedPurFeeHd03() == null ? BigDecimal.ZERO : dto.getRedPurFeeHd03().add(
                    dto.getRedFee03() == null ? BigDecimal.ZERO : dto.getRedFee03()));
                // trdProductDeal.setFeeAmtPay(dto.getRedFeeLc03());
                trdProductDeal.setFeeAmt(dto.getRedPurFeeHd03() == null ? BigDecimal.ZERO : dto.getRedPurFeeHd03().add(
                    dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()).add(
                        dto.getRedFeeLc03() == null ? BigDecimal.ZERO : dto.getRedFeeLc03()));
                trdProductDeal.setTradeAmt(dto.getRedPrinAmt03()); 
                trdProductDeal.setRemark(dto.getRedRemark03());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                if (dto.getRedFee03() != null && dto.getRedFee03().compareTo(BigDecimal.ZERO) != 0) {
                    TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                    trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                    trdTradeFeeDetail.setFeeAmt(dto.getRedFee03());
                    trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                    trdTradeFeeDetailList.add(trdTradeFeeDetail);
                }
                // if(dto.getRedPurFeeHd03()!=null && dto.getRedPurFeeHd03().compareTo(BigDecimal.ZERO)!=0){
                // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                // trdTradeFeeDetail.setFeeAmt(dto.getRedPurFeeHd03());
                // trdTradeFeeDetail.setFeeType(DictCodeListExt2.费用类型_申购费后端.getValue());
                // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                // }
                // if(dto.getRedFeeLc03()!=null && dto.getRedFeeLc03().compareTo(BigDecimal.ZERO)!=0){
                // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                // trdTradeFeeDetail.setFeeAmt(dto.getRedFeeLc03());
                // trdTradeFeeDetail.setFeeType(DictCodeListExt2.费用类型_赎回费产品留存.getValue());
                // trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                // }
                // if(dto.getRedLiquidatedDamages03()!=null && dto.getRedLiquidatedDamages03().compareTo(BigDecimal.ZERO)!=0){
                // TrdTradeFeeDetail trdTradeFeeDetail = genTrdTradeFeeDetailCommon(dto);
                // trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                // trdTradeFeeDetail.setFeeAmt(dto.getRedLiquidatedDamages03());
                // trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_违约金.getValue());
                // trdTradeFeeDetailList.add(trdTradeFeeDetail);
                // }
                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);

                if (dto.getRedProfit03() != null && dto.getRedProfit03().compareTo(BigDecimal.ZERO) != 0) {
                    baseTradeProductDealDto = new BaseTradeProductDealDto();

                    TrdProductDeal trdProductDealInt = genTrdProductDealCommon(dto);

                    TrdProductDealAdd trdProductDealAddInt = genTrdProductDealAddCommon(dto);

                    String tradeId = genTradeId(DictCodeList.共通业务类型_收益型产品中间付息管理);

                    trdProductDealInt.setTradeId(tradeId);
                    trdProductDealAddInt.setTradeId(trdProductDealInt.getTradeId());

                    trdProductDealInt.setTradeDate(dto.getRedConfirmDate03());
                    trdProductDealInt.setSettleDate(dto.getRedProfitPayDate03());

                    trdProductDealInt.setPs(DictCodeList.交易方向_赎回收益确认.getValue());

                    trdProductDealInt.setVdate(dto.getvDate());
                    //trdProductDealInt.setMdate(dto.getmDate());
                    trdProductDealInt.setMdate(dto.getRedConfirmDate03());

                    // trdProductDealInt.setPrinAmt(dto.getRedPrinAmt03());
                    trdProductDealInt.setFpriceAmt(dto.getRedProfit03());

                    trdProductDealInt.setIntAmt(dto.getRedProfit03());
                    if (dto.getRedFeeLc03() != null) {
                        trdProductDealInt.setTradeAmt(dto.getRedProfit03().subtract(dto.getRedFeeLc03()));
                    }
                    else {
                        trdProductDealInt.setTradeAmt(dto.getRedProfit03());
                    }
                    trdProductDealInt.setRemark(dto.getExpireRemark());
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDealInt);

                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAddInt);

                    trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                    paTrdProductDeal.setTradeId(trdProductDealInt.getTradeId());

                    baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                trdProductDeal.setTradeDate(dto.getExpireConfirmDate03());
                trdProductDeal.setSettleDate(dto.getExpirePrinPayDate03());
                trdProductDeal.setShareAmt(dto.getExpirePrinAmt03());
                trdProductDeal.setPrinAmt(dto.getExpirePrinAmt03());
                // trdProductDeal.setIntAmt(dto.getExpireProfit03());
                trdProductDeal.setFpriceAmt(dto.getExpirePrinAmt03());
                trdProductDeal.setTradeAmt(dto.getExpirePrinAmt03());
                trdProductDeal.setRemark(dto.getExpireRemark03());
                trdProductDeal.setVdate(dto.getvDate());
                trdProductDeal.setMdate(dto.getmDate());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);

                if (dto.getExpireProfit03() != null && dto.getExpireProfit03().compareTo(BigDecimal.ZERO) != 0) {
                    baseTradeProductDealDto = new BaseTradeProductDealDto();

                    TrdProductDeal trdProductDealInt = genTrdProductDealCommon(dto);

                    TrdProductDealAdd trdProductDealAddInt = genTrdProductDealAddCommon(dto);

                    String tradeId = genTradeId(DictCodeList.共通业务类型_收益型产品中间付息管理);

                    trdProductDealInt.setTradeId(tradeId);
                    trdProductDealAddInt.setTradeId(trdProductDealInt.getTradeId());

                    trdProductDealInt.setTradeDate(dto.getExpireConfirmDate03());
                    trdProductDealInt.setSettleDate(dto.getExpireProfitPayDate03());

                    trdProductDealInt.setPs(DictCodeList.交易方向_赎回收益确认.getValue());

                    trdProductDealInt.setIsClean(DictCodeList.是否_是.getValue());

                    trdProductDealInt.setVdate(dto.getvDate());
                    trdProductDealInt.setMdate(dto.getmDate());

                    // trdProductDealInt.setPrinAmt(dto.getRedPrinAmt03());
                    trdProductDealInt.setFpriceAmt(dto.getExpireProfit03());

                    trdProductDealInt.setIntAmt(dto.getExpireProfit03());
                    trdProductDealInt.setTradeAmt(dto.getExpireProfit03());

                    trdProductDealInt.setRemark(dto.getExpireRemark());
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDealInt);

                    baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAddInt);

                    baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                    PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                    paTrdProductDeal.setTradeId(trdProductDealInt.getTradeId());

                    baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);

                    listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                }
            }else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())) {
				trdProductDeal.setTradeDate(dto.getAddReduceConfirmDate03());
				trdProductDeal.setSettleDate(dto.getAddReduceConfirmDate03());
				trdProductDeal.setShareAmt(dto.getAddReduceDealNum03());
				trdProductDeal.setPrinAmt(dto.getAddReduceDealAmt03());
				trdProductDeal.setFpriceAmt(dto.getAddReduceDealAmt03());
				trdProductDeal.setTradeAmt(dto.getAddReduceDealAmt03());
				trdProductDeal.setRemark(dto.getAddReduceRemark03());
				baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

				baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

				baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

				listBaseTradeProductDealDto.add(baseTradeProductDealDto);
			} else if (DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                trdProductDeal.setTradeDate(dto.getAddReduceConfirmDate03());
                trdProductDeal.setSettleDate(dto.getAddReduceConfirmDate03());
                trdProductDeal.setShareAmt(dto.getAddReduceDealNum03());
                trdProductDeal.setPrinAmt(dto.getAddReduceDealNum03());
                trdProductDeal.setFpriceAmt(dto.getAddReduceDealNum03());
                trdProductDeal.setTradeAmt(dto.getAddReduceDealNum03());
                trdProductDeal.setRemark(dto.getAddReduceRemark03());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);
                //modify by hxy TISHKBS-2510 场景：货币型产品，份额强减时，金额和份额不一致
				if (dto.getAddReduceDealAmt03().compareTo(dto.getAddReduceDealNum03()) != 0) {
					baseTradeProductDealDto = new BaseTradeProductDealDto();

					TrdProductDeal trdProductDealInt = genTrdProductDealCommon(dto);

					TrdProductDealAdd trdProductDealAddInt = genTrdProductDealAddCommon(dto);

					String tradeId = genTradeId(DictCodeList.共通业务类型_收益型产品中间付息管理);

					trdProductDealInt.setTradeId(tradeId);
					trdProductDealAddInt.setTradeId(trdProductDealInt.getTradeId());

					trdProductDealInt.setTradeDate(dto.getAddReduceConfirmDate03());
					trdProductDealInt.setSettleDate(dto.getAddReduceConfirmDate03());

					trdProductDealInt.setPs(DictCodeList.交易方向_赎回收益确认.getValue());

					trdProductDealInt.setVdate(dto.getvDate());
//                    trdProductDealInt.setMdate(dto.getmDate());
					trdProductDealInt.setMdate(dto.getAddReduceConfirmDate03());

					// trdProductDealInt.setPrinAmt(dto.getRedPrinAmt03());
					trdProductDealInt.setFpriceAmt(ArithmeticUtil.subtract(dto.getAddReduceDealAmt03(),dto.getAddReduceDealNum03()));

					trdProductDealInt.setIntAmt(ArithmeticUtil.subtract(dto.getAddReduceDealAmt03(),dto.getAddReduceDealNum03()));

					trdProductDealInt.setTradeAmt(ArithmeticUtil.subtract(dto.getAddReduceDealAmt03(),dto.getAddReduceDealNum03()));

					trdProductDealInt.setRemark(dto.getExpireRemark());
					baseTradeProductDealDto.setTrdProductDeal(trdProductDealInt);

					baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAddInt);

					trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
					baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

					PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
					paTrdProductDeal.setTradeId(trdProductDealInt.getTradeId());

					baseTradeProductDealDto.setPaTrdProductDeal(paTrdProductDeal);

					listBaseTradeProductDealDto.add(baseTradeProductDealDto);
				}
            }
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                // modify by wm 交易日存红利下发日 start 2019-07-11
                // trdProductDeal.setTradeDate(dto.getBonusCashEndDate03());
                trdProductDeal.setTradeDate(dto.getBonusCashBonusDate03());
                // modify by wm 交易日存红利下发日 end 2019-07-11
                trdProductDeal.setMdate(dto.getBonusCashEndDate03());
                trdProductDeal.setSettleDate(dto.getBonusCashBonusDate03());
                trdProductDeal.setCpriceAmt(dto.getBonusCashBonusAmt03());
                trdProductDeal.setFpriceAmt(dto.getBonusCashBonusAmt03());
                trdProductDeal.setTradeAmt(dto.getBonusCashBonusAmt03());
                trdProductDeal.setRemark(dto.getBonusCashRemark03());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);
            }
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                // modify by wm 交易日存再投结转日 start 2019-07-11
                // trdProductDeal.setTradeDate(dto.getBonusShareEndDate03());
                trdProductDeal.setTradeDate(dto.getBonusShareConfirmDate03());
                // modify by wm 交易日存再投结转日 end 2019-07-11
                trdProductDeal.setMdate(dto.getBonusShareEndDate03());

                trdProductDeal.setSettleDate(dto.getBonusShareConfirmDate03());
                trdProductDeal.setShareAmt(dto.getBonusShareAmt03());
                trdProductDeal.setCpriceAmt(dto.getBonusShareAmt03());
                trdProductDeal.setFpriceAmt(dto.getBonusShareAmt03());
                trdProductDeal.setTradeAmt(dto.getBonusShareAmt03());
                trdProductDeal.setRemark(dto.getBonusShareRemark03());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);

                listBaseTradeProductDealDto.add(baseTradeProductDealDto);
            }
        }
        return listBaseTradeProductDealDto;
    }

    private TrdTradeFeeDetail genTrdTradeFeeDetailCommon(FinNetProductDurationDto dto) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        // trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        trdTradeFeeDetail.setCreateUser(dto.getCreateUser());
        trdTradeFeeDetail.setCreateTime(dto.getCreateTime());
        trdTradeFeeDetail.setUpdateUser(dto.getCreateUser());
        trdTradeFeeDetail.setUpdateTime(dto.getCreateTime());
        return trdTradeFeeDetail;
    }

	private void genFinCashInfo(FinNetProductDurationDto dto , FinTradeProduct finTradeProduct) {
		//期限现金流id				debug	dto.期限代码
		String cashId = commonBusinessService.generateCashFlowCode(finTradeProduct.getFinprodId()+"", 
				DictCodeListExt1.现金流一级类型_费用.getValue(), finTradeProduct.getBranch(), DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
		//sql参数集合
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("finProdId", finTradeProduct.getFinprodId());	//产品代码
		requestMap.put("finBrach", 0L);	//分支序号
		requestMap.put("cashFree", DictCodeListExt1.现金流一级类型_费用.getValue());		//现金流一级类型_费用
		requestMap.put("cashManeFree", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());		//现金流二级类型_浮动管理费
		requestMap.put("baseRuleType01", DictCodeListExt1.业绩基准类型_产品披露.getValue());		//业绩基准类型
		requestMap.put("baseRuleType02", DictCodeListExt1.业绩基准类型_浮动管理费.getValue());		//业绩基准类型
		requestMap.put("vDate", finTradeProduct.getVdate());			//确认日（认申购）
		Map<String, Object> cashMap = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
                + "/FinNetProductDuration/getFinCashInfo",requestMap,
                Map.class);
		if(cashMap != null && cashMap.size()>0){
			ObjectMapper mapper = new ObjectMapper();
			//现金流信息
			FinCashInfo finCashInfo = mapper.convertValue(cashMap.get("finCashInfo"),FinCashInfo.class);
			if(StringUtil.isNotEmpty(finCashInfo)) {
				finCashInfo.setCashId(cashId);		//现金流id
				finCashInfo.setCcy(dto.getCcy());		//币种
				finCashInfo.setFinprodId(finTradeProduct.getFinprodId());		//产品代码
				finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());		//业务类型
				finCashInfo.setBranch(finTradeProduct.getBranch());			//分支序号
				finCashInfo.setIsCalc(DictCodeListExt2.是否_否.getValue());		//是否参与计算   否
				finCashInfoMapper.insertSelective(finCashInfo);
			}
			//现金流计算规则
			FinCashCalcRule finCashCalcRule = mapper.convertValue(cashMap.get("finCashCalcRule"),FinCashCalcRule.class);
			if(StringUtil.isNotEmpty(finCashCalcRule)) {
				finCashCalcRule.setCashId(cashId);		//现金流id
				finCashCalcRule.setFinprodId(finTradeProduct.getFinprodId());		//产品代码
				finCashCalcRule.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());		//产品类型
				finCashCalcRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());		//产品类型2
				finCashCalcRule.setBranch(finTradeProduct.getBranch());			//分支序号
				finCashCalcRule.setEffDate(finTradeProduct.getVdate());			//生效期（认申购）
				finCashCalcRuleMapper.insertSelective(finCashCalcRule);
			}
			//业绩报酬计算规则
			FinFloatCalcRule finFloatCalcRule = mapper.convertValue(cashMap.get("finFloatCalcRule"),FinFloatCalcRule.class);
			if(StringUtil.isNotEmpty(finFloatCalcRule)) {
				finFloatCalcRule.setCashId(cashId);		//现金流id
				finFloatCalcRule.setEffDate(finTradeProduct.getVdate());			//生效期（认申购）
				finFloatCalcRuleMapper.insertSelective(finFloatCalcRule);
			}
			//业绩报酬计算规则明细
			String baseRuleId = com.joyintech.tams.invest.common.CommonUtil
					.generateCashFlowCode(finTradeProduct.getFinprodId(), null, finTradeProduct.getBranch()
							, DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
			FinFloatCalcRuleDetail finFloatCalcRuleDetail = mapper.convertValue(cashMap.get("finFloatCalcRuleDetail"),FinFloatCalcRuleDetail.class);
			if(StringUtil.isNotEmpty(finFloatCalcRuleDetail)) {
				finFloatCalcRuleDetail.setCashId(cashId);		//现金流id
				finFloatCalcRuleDetail.setEffDate(finTradeProduct.getVdate());			//生效期（认申购）
				finFloatCalcRuleDetail.setBaseRuleId(baseRuleId);
				finFloatCalcRuleDetailMapper.insertSelective(finFloatCalcRuleDetail);
			}
			//业绩基准类型为01产品纰漏的业绩基准数据
			FinBaseRuleInfo finBaseRuleInfo = mapper.convertValue(cashMap.get("finBaseRuleInfo01"),FinBaseRuleInfo.class);
			if(StringUtil.isNotEmpty(finBaseRuleInfo)) {
				finBaseRuleInfo.setBranch(finTradeProduct.getBranch());		//分支序号
				finBaseRuleInfo.setBaseRuleId(com.joyintech.tams.invest.common.CommonUtil
						.generateCashFlowCode(finTradeProduct.getFinprodId(), null, finTradeProduct.getBranch()
								, DictCodeListExt1.业绩基准类型_产品披露.getValue()));		//业绩基准代码
				finBaseRuleInfoMapper.insertSelective(finBaseRuleInfo);
			}
			//业绩基准类型为02浮动管理费的业绩基准数据
			finBaseRuleInfo = mapper.convertValue(cashMap.get("finBaseRuleInfo02"),FinBaseRuleInfo.class);
			if(StringUtil.isNotEmpty(finBaseRuleInfo)) {
				finBaseRuleInfo.setEffDate(finTradeProduct.getVdate());
				finBaseRuleInfo.setBranch(finTradeProduct.getBranch());		//分支序号
				finBaseRuleInfo.setBaseRuleId(baseRuleId);		//业绩基准代码
				finBaseRuleInfoMapper.insertSelective(finBaseRuleInfo);
			}
		}
	}	

    private FinCashPayRule genFinCashPayRule(FinCashInfo finCashInfo, FinNetProductDurationDto dto) {
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            finCashPayRule.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
            finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
        }
        else {
            finCashPayRule.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
            finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        }
        finCashPayRule.setCashId(finCashInfo.getCashId());
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        finCashPayRule.setPayType(DictCodeList.分红方式_现金分红.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_不规则.getValue());
        finCashPayRule.setFinprodId(dto.getFinprodId());
        finCashPayRule.setPayCycle(BussinessConstant.DEFAULT_PAY_CYCLE);
        finCashPayRule.setBranch(dto.getBranch());
        if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
            finCashPayRule.setEffDate(dto.getSubConfirmDate());
            finCashPayRule.setRemark(dto.getSubRemark());
            finCashPayRule.setFirstPayDate(dto.getSubCyclePayDate());
        }
        else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
            finCashPayRule.setEffDate(dto.getPurConfirmDate());
            finCashPayRule.setRemark(dto.getPurRemark());
            finCashPayRule.setFirstPayDate(dto.getPurCyclePayDate());
        }
        return finCashPayRule;
    }

    private List<PtlCashPositionFlowDto> genPtlCashPositionFlowDto(FinNetProductDurationDto dto, boolean isRevoke) {
        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<PtlCashPositionFlowDto>();
        if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            for (BussTrdTransferOrderDto orderDto : dto.getBussTrdTransferOrder()) {
                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                ptlCashPositionFlowDto.setPortfolioId(dto.getPortfolioId());

                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
                ptlCashPositionFlowDto.setCcy(dto.getCcy());
                if (isRevoke) {
                    if (orderDto.getPs().equals("01")) {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                    }
                    else {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                    }
                }
                else {
                    if (orderDto.getPs().equals("01")) {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getRecAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt());
                    }
                    else {
                        ptlCashPositionFlowDto.setAcctId(orderDto.getPayAcctNo());
                        ptlCashPositionFlowDto.setAmount(orderDto.getSettleAmt().negate());
                    }

                }
                ptlCashPositionFlowDto.setOrderId(orderDto.getOrderId());
                if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getRedPayDate());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getExpirePayDate());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                }
                else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
                    if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getSubConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getPurConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getRedConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getExpireConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                             || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getAddReduceConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getBonusCashBonusDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                    else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                        ptlCashPositionFlowDto.setCdate(dto.getBonusShareConfirmDate03());
                        ptlCashPositionFlows.add(ptlCashPositionFlowDto);
                    }
                }

            }
        }
        return ptlCashPositionFlows;
    }

	private List<PtlSecPositionFlowDto> genPtlSecPositionFlowDto(FinNetProductDurationDto dto, FinTradeProduct finTradeProduct, boolean isRevoke) {
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<PtlSecPositionFlowDto>();
        PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
        ptlSecPositionFlowDto.setTradeId(dto.getTradeId());
        ptlSecPositionFlowDto.setPortfolioId(dto.getPortfolioId());
        ptlSecPositionFlowDto.setFinprodId(dto.getFinprodId());
        ptlSecPositionFlowDto.setBranch(dto.getBranch());
        ptlSecPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        ptlSecPositionFlowDto.setCcy(dto.getCcy());
        ptlSecPositionFlowDto.setSecAcctId("999999");
        ptlSecPositionFlowDto.setInvAim("999999");
        if(DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())){
        	if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())){
            	ptlSecPositionFlowDto.setCdate(dto.getSubConfirmDate());
            	//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            	//是否为周期型产品
            	if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            		ptlSecPositionFlowDto.setFinprodId(finTradeProduct.getFinprodId()); 	//产品代码
            		ptlSecPositionFlowDto.setBranch(finTradeProduct.getBranch()); 		//分支序号
            		if(isRevoke) {
            			ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt().negate());		//认购交易金额
            		}else {
            			ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt());		//认购交易金额
            		}
            		ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            	}
            	//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
            	else if(!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                		&&StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())){
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                        ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        if (isRevoke) {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getSubDealLayerAmt().negate());
                        }
                        else {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getSubDealLayerAmt());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                    }
                }
                else {
                    if (isRevoke) {
                        ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt().negate());
                    }
                    else {
                        ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt());
                    }

                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }

            }else if(DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())){
            	ptlSecPositionFlowDto.setCdate(dto.getPurConfirmDate());
            	//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            	//是否为周期型产品
            	if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            		ptlSecPositionFlowDto.setFinprodId(finTradeProduct.getFinprodId()); 	//产品代码
            		ptlSecPositionFlowDto.setBranch(finTradeProduct.getBranch()); 		//分支序号
            		if(isRevoke) {
            			ptlSecPositionFlowDto.setAmount(dto.getPurDealNum().negate());		//申购交易份额
            		}else {
            			ptlSecPositionFlowDto.setAmount(dto.getPurDealNum());		//申购交易份额
            		}
            		ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            	}
            	//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
            	else if(!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                		&&StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())){
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                        ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        if (isRevoke) {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getPurDealLayerNum().negate());
                        }
                        else {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getPurDealLayerNum());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                    }
                }
                else {
                    if (isRevoke) {
                        ptlSecPositionFlowDto.setAmount(dto.getPurDealNum().negate());
                    }
                    else {
                        ptlSecPositionFlowDto.setAmount(dto.getPurDealNum());
                    }
                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getRedConfirmDate());
                //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            	//是否为周期型产品
                if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle()) && 
                		!StringUtil.isEmptyList(dto.getRedCycleFinNetProductDurationDtoList())) {
                	for(FinNetProductDurationDto finNetProductDurationDto : dto.getRedCycleFinNetProductDurationDtoList()){
                		PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                		BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setTradeId(StringUtil.isNotEmpty(
                				finNetProductDurationDto.getTradeId()) ? finNetProductDurationDto.getTradeId() : dto.getTradeId());
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationDto.getFinprodId());
                		ptlSecPositionFlowDtoTmp.setBranch(finNetProductDurationDto.getBranch());
                		if(isRevoke) {		
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getRedDealNum());																		
                		} else {																					
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getRedDealNum().negate());																			
                		}																					
                		ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                	}
                }
                //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                        ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        if (isRevoke) {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getRedDealLayerNum());
                        }
                        else {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getRedDealLayerNum().negate());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                    }
                }
                else {
                    if (isRevoke) {
                        ptlSecPositionFlowDto.setAmount(dto.getRedDealNum());
                    }
                    else {
                        ptlSecPositionFlowDto.setAmount(dto.getRedDealNum().negate());
                    }
                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getExpireConfirmDate());
                //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            	//是否为周期型产品
                if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle()) && !StringUtil.isEmptyList(dto.getExpireCycleFinNetProductDurationDtoList())) {
                	for(FinNetProductDurationDto finNetProductDurationDto : dto.getExpireCycleFinNetProductDurationDtoList()){
                		PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                		BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setTradeId(StringUtil.isNotEmpty(
                				finNetProductDurationDto.getTradeId()) ? finNetProductDurationDto.getTradeId() : dto.getTradeId());
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationDto.getFinprodId());
                		ptlSecPositionFlowDtoTmp.setBranch(finNetProductDurationDto.getBranch());
                		if(isRevoke) {		
                			ptlSecPositionFlowDtoTmp.setAmount(dto.getExpireDealNum());																		
                		} else {																					
                			ptlSecPositionFlowDtoTmp.setAmount(dto.getExpireDealNum().negate());																			
                		}																					
                		ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                	}
                }
                //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                        ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                        ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                        if (isRevoke) {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getExpireDealLayerAmt());
                        }
                        else {
                            ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getExpireDealLayerAmt().negate());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                    }
                }
                else {
                    if (isRevoke) {
                        ptlSecPositionFlowDto.setAmount(dto.getExpireDealAmt());
                    }
                    else {
                        ptlSecPositionFlowDto.setAmount(dto.getExpireDealAmt().negate());
                    }
                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getAddReduceConfirmDate());
                //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
                //是否为周期型产品
                if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle()) && !StringUtil.isEmptyList(dto.getAddReduceCycleFinNetProductDurationDtoList())) {
                	for(FinNetProductDurationDto finNetProductDurationDto : dto.getAddReduceCycleFinNetProductDurationDtoList()){
                		PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                		BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setTradeId(StringUtil.isNotEmpty(
                				finNetProductDurationDto.getTradeId()) ? finNetProductDurationDto.getTradeId() : dto.getTradeId());
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationDto.getFinprodId());
                		ptlSecPositionFlowDtoTmp.setBranch(finNetProductDurationDto.getBranch());
                		if(isRevoke) {		
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getAddReduceDealNum().negate());																		
                		} else {																					
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getAddReduceDealNum());																			
                		}																					
                		ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                	}
                }
                //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        if (finNetProductDurationLayerDto.getAddReduceDealLayerNum() != null) {
                            PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                            ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                            ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                            if (isRevoke) {
                                ptlSecPositionFlowDtoTmp.setAmount(
                                    finNetProductDurationLayerDto.getAddReduceDealLayerNum().negate());
                            }
                            else {
                                ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getAddReduceDealLayerNum());
                            }
                            ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                        }
                    }
                }
                else {
                    if (dto.getAddReduceDealNum() != null) {
                        if (isRevoke) {
                            ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum().negate());
                        }
                        else {
                            ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                    }
                }
            }
            else if (DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getAddReduceConfirmDate());
                //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 净值型周期型需求 add  20190920 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
                //是否为周期型产品
                if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle()) && !StringUtil.isEmptyList(dto.getAddReduceCycleFinNetProductDurationDtoList())) {
                	for(FinNetProductDurationDto finNetProductDurationDto : dto.getAddReduceCycleFinNetProductDurationDtoList()){
                		PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                		BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setTradeId(StringUtil.isNotEmpty(
                				finNetProductDurationDto.getTradeId()) ? finNetProductDurationDto.getTradeId() : dto.getTradeId());
                		/**20200106  taoran add 重新设置交易编号**/
                		ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationDto.getFinprodId());
                		ptlSecPositionFlowDtoTmp.setBranch(finNetProductDurationDto.getBranch());
                		if(isRevoke) {		
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getAddReduceDealNum());																		
                		} else {																					
                			ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationDto.getAddReduceDealNum().negate());																			
                		}																					
                		ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                	}
                }
                //↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑	净值型周期型需求 add  20190920 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
                else if (!StringUtil.isEmptyList(dto.getFinNetProductDurationLayerList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())) {
                    for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : dto.getFinNetProductDurationLayerList()) {
                        if (finNetProductDurationLayerDto.getAddReduceDealLayerNum() != null) {
                            PtlSecPositionFlowDto ptlSecPositionFlowDtoTmp = new PtlSecPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTmp, ptlSecPositionFlowDto);
                            ptlSecPositionFlowDtoTmp.setTradeId(finNetProductDurationLayerDto.getTradeId());
                            ptlSecPositionFlowDtoTmp.setFinprodId(finNetProductDurationLayerDto.getFinprodId());
                            if (isRevoke) {
                                ptlSecPositionFlowDtoTmp.setAmount(finNetProductDurationLayerDto.getAddReduceDealLayerNum());
                            }
                            else {
                                ptlSecPositionFlowDtoTmp.setAmount(
                                    finNetProductDurationLayerDto.getAddReduceDealLayerNum().negate());
                            }
                            ptlSecPositionFlows.add(ptlSecPositionFlowDtoTmp);
                        }
                    }
                }
                else {
                    if (dto.getAddReduceDealNum() != null) {
                        if (isRevoke) {
                            ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum());
                        }
                        else {
                            ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum().negate());
                        }
                        ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                    }
                }
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getSubConfirmDate03());
                if (isRevoke) {
                    ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt03().negate());
                }
                else {
                    ptlSecPositionFlowDto.setAmount(dto.getSubDealAmt03());
                }
                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getPurConfirmDate03());
                if (isRevoke) {
                    ptlSecPositionFlowDto.setAmount(dto.getPurDealAmt03().negate());
                }
                else {
                    ptlSecPositionFlowDto.setAmount(dto.getPurDealAmt03());
                }
                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getRedConfirmDate03());
                if (isRevoke) {
                    ptlSecPositionFlowDto.setAmount(dto.getRedDealAmt03());
                }
                else {
                    ptlSecPositionFlowDto.setAmount(dto.getRedDealAmt03().negate());
                }
                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getExpireConfirmDate03());
                if (isRevoke) {
                    ptlSecPositionFlowDto.setAmount(dto.getExpireDealAmt03());
                }
                else {
                    ptlSecPositionFlowDto.setAmount(dto.getExpireDealAmt03().negate());
                }
                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())) {
            	//modify by yujiaping 20210125 ZGBSS-49 货币型产品审批份额强增流程报错 start
                if (dto.getAddReduceDealAmt03() != null || dto.getAddReduceDealNum03() != null) {
                    ptlSecPositionFlowDto.setCdate(dto.getAddReduceConfirmDate03());
                    if (isRevoke) {
                    	if(dto.getAddReduceDealAmt03() != null && dto.getAddReduceDealNum03() == null) {
                    		 ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealAmt03().negate());
                    	}else{
                    		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum03().negate());                    		
                    	}
                    }
                    else {
                    	if(dto.getAddReduceDealAmt03() != null && dto.getAddReduceDealNum03() == null) {
                    		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealAmt03());
                    	}else{                    		
                    		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum03());
                    	}
                    }
                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }
            }
            else if (DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                if (dto.getAddReduceDealAmt03() != null || dto.getAddReduceDealNum03() != null) {
                    ptlSecPositionFlowDto.setCdate(dto.getAddReduceConfirmDate03());
                    if (isRevoke) {
                    	if(dto.getAddReduceDealAmt03() != null && dto.getAddReduceDealNum03() == null) {
                    		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealAmt03());
                    	}else{                    		
                    		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum03());
                    	}
                    }
                    else {
                    	if(dto.getAddReduceDealAmt03() != null && dto.getAddReduceDealNum03() == null) {
                   		 ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealAmt03().negate());
                   	}else{
                   		ptlSecPositionFlowDto.setAmount(dto.getAddReduceDealNum03().negate());                    		
                   	}
                    }
                    ptlSecPositionFlows.add(ptlSecPositionFlowDto);
                }
            }
            //modify by yujiaping 20210125 ZGBSS-49 货币型产品审批份额强增流程报错 start
            
            //delete by wm 现金分红无需登组合持仓流水表 start 2019-12-27
//            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
//                ptlSecPositionFlowDto.setCdate(dto.getBonusCashBonusDate03());
//                if (isRevoke) {
//                    ptlSecPositionFlowDto.setAmount(dto.getBonusCashBonusAmt03());
//                }
//                else {
//                    ptlSecPositionFlowDto.setAmount(dto.getBonusCashBonusAmt03().negate());
//                }
//                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
//            }
            //delete by wm 现金分红无需登组合持仓流水表 end 2019-12-27
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                ptlSecPositionFlowDto.setCdate(dto.getBonusShareConfirmDate03());
                //持仓流水，手动交易取
                if(dto.getBonusShareAmt03()!=null){
                    if (isRevoke) {
                        ptlSecPositionFlowDto.setAmount(dto.getBonusShareAmt03().negate());
                    }
                    else {
                        ptlSecPositionFlowDto.setAmount(dto.getBonusShareAmt03());
                    }
                }
//                if (isRevoke) {
//                    ptlSecPositionFlowDto.setAmount(dto.getBonusShareAfterShareNum03());
//                }
//                else {
//                    ptlSecPositionFlowDto.setAmount(dto.getBonusShareAfterShareNum03().negate());
//                }
                ptlSecPositionFlows.add(ptlSecPositionFlowDto);
            }
        }

        return ptlSecPositionFlows;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private FinTradeProduct genFinTradeProduct(FinNetProductDurationDto dto,FinTradeProduct finTradeProduct) {
    	//交易类金融产品(fin_trade_product),如果多次 认申购，周期子产品已经存在，则 不需要插入数据      
        //先根据产品id+期次开始结束日查询，如果已经存在，则不新建
    	Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("finprodId", finTradeProduct.getFinprodId());
		requestMap.put("vDate", finTradeProduct.getVdate());
		requestMap.put("mDate", finTradeProduct.getMdate());
        FinTradeProduct finTradeProductData = restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME
				+ "/FinNetProductDuration/getFinTradeProductByPeriodId",
				requestMap, FinTradeProduct.class);
        if(!StringUtil.isNotEmpty(finTradeProductData)){
            finTradeProduct.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
            finTradeProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
            finTradeProduct.setFinprodType(dto.getProfitType());  		//收益类型
            finTradeProduct.setCcy(dto.getCcy());		//币种
            finTradeProduct.setTermType(dto.getTermType()); 	//期限类型
            finTradeProductMapper.insertSelective(finTradeProduct);
            FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
            finTradeProductAdd.setFinprodId(finTradeProduct.getFinprodId());
            finTradeProductAdd.setFinprodType(finTradeProduct.getFinprodType());
            finTradeProductAdd.setFinprodType2(finTradeProduct.getFinprodType2());
            finTradeProductAdd.setBranch(finTradeProduct.getBranch());
            finTradeProductAddMapper.insertSelective(finTradeProductAdd);
            //现金流相关输入入库
            genFinCashInfo(dto,finTradeProduct);
            return finTradeProduct;
        }
        else {
        	return finTradeProductData;
        }
        
    }

    /**
     * 组装 组合交易表(PTL_TRADE) entity
     *
     * @param dto
     * @param listBaseTradeProductDealDto
     * @return
     * @see
     * @since 1.0
     */
    private List<PtlTrade> getPtlTrade(FinNetProductDurationDto dto, List<BaseTradeProductDealDto> listBaseTradeProductDealDto) {
        List<PtlTrade> listPtlTrade = new ArrayList<PtlTrade>();
        // if(StringUtil.equals(DictCodeList.处理模式_分档.getValue(), dto.getDealMode())){
        // PtlTrade ptlTrade = new PtlTrade();
        // ptlTrade.setPortfolioId(dto.getPortfolioId());
        // ptlTrade.setBusiId(dto.getFinprodId());
        // ptlTrade.setBusiType(dto.getDealType());
        // ptlTrade.setCreateUser(dto.getCreateUser());
        // ptlTrade.setCreateDept(dto.getCreateDept());
        // ptlTrade.setCreateTime(dto.getCreateTime());
        // ptlTrade.setUpdateUser(dto.getUpdateUser());
        // ptlTrade.setUpdateTime(dto.getCreateTime());
        // ptlTrade.setBusiNo(dto.getTradeId());
        // ptlTrade.setBusiTable(dto.getTradeId());
        // ptlTrade.setSettleDate(dto.getSubConfirmDate());
        // listPtlTrade.add(ptlTrade);
        // }
        // else if(DictCodeList.是否_是.getValue().equals(dto.getIsCycle())){
        //
        // }
        // else{
        // for(BaseTradeProductDealDto baseTradeProductDealDto:listBaseTradeProductDealDto){
        // PtlTrade ptlTrade = new PtlTrade();
        // ptlTrade.setPortfolioId(dto.getPortfolioId());
        // ptlTrade.setBusiId(dto.getFinprodId());
        // ptlTrade.setBusiType(dto.getDealType());
        // ptlTrade.setCreateUser(dto.getCreateUser());
        // ptlTrade.setCreateDept(dto.getCreateDept());
        // ptlTrade.setCreateTime(dto.getCreateTime());
        // ptlTrade.setUpdateUser(dto.getUpdateUser());
        // ptlTrade.setUpdateTime(dto.getCreateTime());
        // ptlTrade.setBusiNo(dto.getTradeId());
        // ptlTrade.setBusiTable(dto.getTradeId());
        // ptlTrade.setSettleDate(dto.getSubConfirmDate());
        // listPtlTrade.add(ptlTrade);
        // }
        // }
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(dto.getDealType());
        ptlTrade.setCreateUser(dto.getCreateUser());
        ptlTrade.setCreateDept(dto.getCreateDept());
        ptlTrade.setCreateTime(dto.getCreateTime());
        ptlTrade.setUpdateUser(dto.getUpdateUser());
        ptlTrade.setUpdateTime(dto.getCreateTime());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(dto.getTradeId());
        ptlTrade.setSettleDate(dto.getSubConfirmDate());
        listPtlTrade.add(ptlTrade);
        return listPtlTrade;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd genTrdProductDealAddCommon(FinNetProductDurationDto dto) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setCreateUser(dto.getCreateUser());
        trdProductDealAdd.setCreateDept(dto.getCreateDept());
        trdProductDealAdd.setCreateTime(dto.getCreateTime());
        trdProductDealAdd.setUpdateUser(dto.getUpdateUser());
        trdProductDealAdd.setUpdateTime(dto.getCreateTime());
        return trdProductDealAdd;
    }

    private String genTradeId(DictCodeList dict) {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(dict);
        String tradeId = baseGetBusinessId.buildBussinessCode(parameter);
        return tradeId;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDeal genTrdProductDealCommon(FinNetProductDurationDto dto) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());
        trdProductDeal.setFinprodId(dto.getFinprodId());
        if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
            trdProductDeal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            trdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());

        }
        if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
        }
        else {
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        }
        trdProductDeal.setBranch(dto.getBranch());
        trdProductDeal.setCcy(dto.getCcy());
        trdProductDeal.setPs(dto.getDealType());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        trdProductDeal.setCreateUser(dto.getCreateUser());
        trdProductDeal.setCreateDept(dto.getCreateDept());
        trdProductDeal.setCreateTime(dto.getCreateTime());
        trdProductDeal.setUpdateUser(dto.getUpdateUser());
        trdProductDeal.setUpdateTime(dto.getCreateTime());
        trdProductDeal.setTotleTradeId(dto.getTradeId());
        return trdProductDeal;
    }

    /**
     * 组装交易汇总信息
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdFinProductDealTotal genTrdFinProductDealTotal(FinNetProductDurationDto dto) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getTradeId());
        trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
        trdFinProductDealTotal.setPortfolioId(dto.getPortfolioId());
        if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
            trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
        }
        else {
            trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        }
        trdFinProductDealTotal.setBranch(dto.getBranch());
        trdFinProductDealTotal.setCcy(dto.getCcy());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setCreateUser(dto.getCreateUser());
        trdFinProductDealTotal.setCreateDept(dto.getCreateDept());
        trdFinProductDealTotal.setCreateTime(dto.getCreateTime());
        trdFinProductDealTotal.setUpdateUser(dto.getUpdateUser());
        trdFinProductDealTotal.setUpdateTime(dto.getCreateTime());
        trdFinProductDealTotal.setPs(dto.getDealType());
        if (DictCodeList.收益类型_净值型.getValue().equals(dto.getProfitType())) {
            trdFinProductDealTotal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getSubConfirmDate());
                trdFinProductDealTotal.setSettleDate(dto.getSubConfirmDate());
                trdFinProductDealTotal.setUnitInt(BigDecimal.ONE);
                trdFinProductDealTotal.setShareAmt(dto.getSubDealNum());
                trdFinProductDealTotal.setPrinAmt(dto.getSubDealAmt());
                trdFinProductDealTotal.setFpriceAmt(dto.getSubDealAmt());
                trdFinProductDealTotal.setFeeAmtUnpay(dto.getSubFee());
                trdFinProductDealTotal.setFeeAmt(dto.getSubFee());
                trdFinProductDealTotal.setTradeAmt(dto.getSubDealAmt());
                trdFinProductDealTotal.setRemark(dto.getSubRemark());
                trdFinProductDealTotal.setUnitCprice(BigDecimal.ONE);
                //净值型周期型新增    期次结束日，期次支付日
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                	trdFinProductDealTotal.setVdate(dto.getSubConfirmDate());
                	trdFinProductDealTotal.setMdate(dto.getSubCycleEndDate());
					//add by jianxx 20210331,CLONE - 【净值化产品存续期交易】周期型产品，产品存续期，期次维度，期次开始日申购，双击打开查看，期次支付日不应展示为空 start
					trdFinProductDealTotal.setPayDate(dto.getPurCyclePayDate());
					//add by jianxx 20210331,CLONE - 【净值化产品存续期交易】周期型产品，产品存续期，期次维度，期次开始日申购，双击打开查看，期次支付日不应展示为空 end
                }
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getPurConfirmDate());
                trdFinProductDealTotal.setSettleDate(dto.getPurConfirmDate());
                trdFinProductDealTotal.setShareAmt(dto.getPurDealNum());
                trdFinProductDealTotal.setPrinAmt(dto.getPurDealAmt());
                trdFinProductDealTotal.setFpriceAmt(dto.getPurDealAmt());
                trdFinProductDealTotal.setFeeAmtUnpay(dto.getPurFee());
                trdFinProductDealTotal.setFeeAmt(dto.getPurFee());
                trdFinProductDealTotal.setTradeAmt(dto.getPurDealAmt());
                trdFinProductDealTotal.setRemark(dto.getPurRemark());
                trdFinProductDealTotal.setUnitCprice(dto.getPurUnitNetValue());
                // 净值日存放到起息日字段
                trdFinProductDealTotal.setVdate(dto.getPurNetValueDate());
                //净值型周期型新增    期次结束日，期次支付日
                if (DictCodeList.是否_是.getValue().equals(dto.getIsCycle())) {
                	trdFinProductDealTotal.setVdate(dto.getPurConfirmDate());
                	trdFinProductDealTotal.setMdate(dto.getPurCycleEndDate());
                }
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getRedConfirmDate());
                trdFinProductDealTotal.setSettleDate(dto.getRedPayDate());
                trdFinProductDealTotal.setShareAmt(dto.getRedDealNum());
                trdFinProductDealTotal.setPrinAmt(dto.getRedDealAmt());
                trdFinProductDealTotal.setFpriceAmt(dto.getRedDealAmt());
				trdFinProductDealTotal.setFeeAmtUnpay(dto.getRedPurFeeHd() == null ? BigDecimal.ZERO.add(
						dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()) : dto.getRedPurFeeHd().add(
						dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()));
                trdFinProductDealTotal.setFeeAmtPay(dto.getRedFeeLc());
                trdFinProductDealTotal.setFeeAmt(dto.getRedPurFeeHd() == null ? BigDecimal.ZERO : dto.getRedPurFeeHd().add(
                    dto.getRedFee() == null ? BigDecimal.ZERO : dto.getRedFee()).add(
                        dto.getRedFeeLc() == null ? BigDecimal.ZERO : dto.getRedFeeLc()));
                trdFinProductDealTotal.setTradeAmt(dto.getRedTotalNetValue());
                trdFinProductDealTotal.setRedProfit(dto.getRedProfit());
                trdFinProductDealTotal.setRedCost(dto.getRedCost());
                trdFinProductDealTotal.setRemark(dto.getRedRemark());
                trdFinProductDealTotal.setUnitCprice(dto.getRedUnitNetValue());
                // 净值日存放到起息日字段
                trdFinProductDealTotal.setVdate(dto.getRedNetValueDate());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getExpireConfirmDate());
                trdFinProductDealTotal.setSettleDate(dto.getExpirePayDate());
                trdFinProductDealTotal.setShareAmt(dto.getExpireDealNum());
                trdFinProductDealTotal.setPrinAmt(dto.getExpireDealAmt());
                trdFinProductDealTotal.setFpriceAmt(dto.getExpireDealAmt());
                trdFinProductDealTotal.setTradeAmt(dto.getExpireDealAmt());
                trdFinProductDealTotal.setRedProfit(dto.getExpireProfit());
                trdFinProductDealTotal.setRedCost(dto.getExpireCost());
                trdFinProductDealTotal.setRemark(dto.getExpireRemark());
                trdFinProductDealTotal.setUnitCprice(dto.getExpireUnitNetValue());
                // 净值日存放到起息日字段
                trdFinProductDealTotal.setVdate(dto.getExpireNetValueDate());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getAddReduceConfirmDate());
                trdFinProductDealTotal.setSettleDate(dto.getAddReduceConfirmDate());
                trdFinProductDealTotal.setShareAmt(dto.getAddReduceDealNum());
                trdFinProductDealTotal.setPrinAmt(dto.getAddReduceDealAmt());
                trdFinProductDealTotal.setFpriceAmt(dto.getAddReduceDealAmt());
                trdFinProductDealTotal.setTradeAmt(dto.getAddReduceDealAmt());
                trdFinProductDealTotal.setRemark(dto.getAddReduceRemark());
                trdFinProductDealTotal.setUnitCprice(dto.getAddReduceUnitNetValue());
                // 净值日存放到起息日字段
                trdFinProductDealTotal.setVdate(dto.getAddReduceNetValueDate());
            }
        }
        else if (DictCodeList.收益类型_货币型.getValue().equals(dto.getProfitType())) {
            trdFinProductDealTotal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
            if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getSubConfirmDate03());
                trdFinProductDealTotal.setSettleDate(dto.getSubConfirmDate03());
                trdFinProductDealTotal.setUnitInt(BigDecimal.ONE);
                trdFinProductDealTotal.setShareAmt(dto.getSubDealAmt03());
                trdFinProductDealTotal.setPrinAmt(dto.getSubDealAmt03());
                trdFinProductDealTotal.setFpriceAmt(dto.getSubDealAmt03());
                trdFinProductDealTotal.setFeeAmtUnpay(dto.getSubFee03());
                trdFinProductDealTotal.setFeeAmt(dto.getSubFee03());
                trdFinProductDealTotal.setTradeAmt(dto.getSubDealAmt03());
                trdFinProductDealTotal.setRemark(dto.getSubRemark03());
            }
            else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getPurConfirmDate03());
                trdFinProductDealTotal.setSettleDate(dto.getPurConfirmDate03());
                trdFinProductDealTotal.setShareAmt(dto.getPurDealAmt03());
                trdFinProductDealTotal.setPrinAmt(dto.getPurDealAmt03());
                trdFinProductDealTotal.setFpriceAmt(dto.getPurDealAmt03());
                trdFinProductDealTotal.setFeeAmtUnpay(dto.getPurFee03());
                trdFinProductDealTotal.setFeeAmt(dto.getPurFee03());
                trdFinProductDealTotal.setTradeAmt(dto.getPurDealAmt03());
                trdFinProductDealTotal.setRemark(dto.getPurRemark03());
            }
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getRedConfirmDate03());
                trdFinProductDealTotal.setSettleDate(dto.getRedPrinPayDate03());
                trdFinProductDealTotal.setShareAmt(dto.getRedPrinAmt03());
                trdFinProductDealTotal.setPrinAmt(dto.getRedPrinAmt03());
                trdFinProductDealTotal.setIntAmt(dto.getRedProfit03());
                trdFinProductDealTotal.setFpriceAmt(dto.getRedDealAmt03());
				trdFinProductDealTotal.setFeeAmtUnpay(
						dto.getRedPurFeeHd03() == null ? BigDecimal.ZERO.add(
								dto.getRedFee03() == null ? BigDecimal.ZERO : dto.getRedFee03()) : dto.getRedPurFeeHd03().add(
								dto.getRedFee03() == null ? BigDecimal.ZERO : dto.getRedFee03()));
                trdFinProductDealTotal.setFeeAmtPay(dto.getRedFeeLc03());
                trdFinProductDealTotal.setFeeAmt(dto.getRedPurFeeHd03() == null ? BigDecimal.ZERO : dto.getRedPurFeeHd03().add(
                    dto.getRedFee03() == null ? BigDecimal.ZERO : dto.getRedFee03()).add(
                        dto.getRedFeeLc03() == null ? BigDecimal.ZERO : dto.getRedFeeLc03()));
                trdFinProductDealTotal.setTradeAmt(dto.getRedTotalNetValue03());
                trdFinProductDealTotal.setRemark(dto.getRedRemark03());
                // 增加赎回本金与赎回收益存储
                trdFinProductDealTotal.setRedCost(dto.getRedPrinAmt03());
                trdFinProductDealTotal.setRedProfit(dto.getRedProfit03());
            }
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                     || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getExpireConfirmDate03());
                trdFinProductDealTotal.setSettleDate(dto.getExpirePrinPayDate03());
                trdFinProductDealTotal.setShareAmt(dto.getExpirePrinAmt03());
                trdFinProductDealTotal.setPrinAmt(dto.getExpirePrinAmt03());
                trdFinProductDealTotal.setIntAmt(dto.getExpireProfit03());
                trdFinProductDealTotal.setFpriceAmt(dto.getExpireDealAmt03());
                trdFinProductDealTotal.setTradeAmt(dto.getExpireDealAmt03());
                trdFinProductDealTotal.setRemark(dto.getExpireRemark03());
                // 增加赎回本金与赎回收益存储
                trdFinProductDealTotal.setRedCost(dto.getExpirePrinAmt03());
                trdFinProductDealTotal.setRedProfit(dto.getExpireProfit03());
            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getAddReduceConfirmDate03());
                trdFinProductDealTotal.setSettleDate(dto.getAddReduceConfirmDate03());
                trdFinProductDealTotal.setShareAmt(dto.getAddReduceDealNum03());
                trdFinProductDealTotal.setPrinAmt(dto.getAddReduceDealAmt03());
                trdFinProductDealTotal.setFpriceAmt(dto.getAddReduceDealAmt03());
                trdFinProductDealTotal.setTradeAmt(dto.getAddReduceDealAmt03());
                trdFinProductDealTotal.setRemark(dto.getAddReduceRemark03());
            }else if (DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
				trdFinProductDealTotal.setTradeDate(dto.getAddReduceConfirmDate03());
				trdFinProductDealTotal.setSettleDate(dto.getAddReduceConfirmDate03());
				trdFinProductDealTotal.setShareAmt(dto.getAddReduceDealNum03());
				trdFinProductDealTotal.setPrinAmt(dto.getAddReduceDealNum03());
				trdFinProductDealTotal.setIntAmt(ArithmeticUtil.subtract(dto.getAddReduceDealAmt03(),dto.getAddReduceDealNum03()));
                trdFinProductDealTotal.setFpriceAmt(dto.getAddReduceDealAmt03());
				trdFinProductDealTotal.setTradeAmt(dto.getAddReduceDealAmt03());
				trdFinProductDealTotal.setRemark(dto.getAddReduceRemark03());
				trdFinProductDealTotal.setRedProfit(ArithmeticUtil.subtract(dto.getAddReduceDealAmt03(),dto.getAddReduceDealNum03()));
				trdFinProductDealTotal.setRedCost(dto.getAddReduceDealNum03());
			}
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getBonusCashEndDate03());
                trdFinProductDealTotal.setSettleDate(dto.getBonusCashBonusDate03());
                trdFinProductDealTotal.setCpriceAmt(dto.getBonusCashBonusAmt03());
                trdFinProductDealTotal.setFpriceAmt(dto.getBonusCashBonusAmt03());
                trdFinProductDealTotal.setTradeAmt(dto.getBonusCashBonusAmt03());
                trdFinProductDealTotal.setRemark(dto.getBonusCashRemark03());
            }
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
                trdFinProductDealTotal.setTradeDate(dto.getBonusShareEndDate03());
                trdFinProductDealTotal.setSettleDate(dto.getBonusShareConfirmDate03());
                trdFinProductDealTotal.setShareAmt(dto.getBonusShareAmt03());
                trdFinProductDealTotal.setCpriceAmt(dto.getBonusShareAmt03());
                trdFinProductDealTotal.setFpriceAmt(dto.getBonusShareAmt03());
                trdFinProductDealTotal.setTradeAmt(dto.getBonusShareAmt03());
                trdFinProductDealTotal.setRemark(dto.getBonusShareRemark03());
            }
        }

        return trdFinProductDealTotal;
    }

    /**
     * 根据产品处理模式(普通、周期)，收益类型(净值、货币)、运作方式(开放、封闭)确认产品支持的交易类型
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getDealTypeList(Map<String, Object> map) {
        String isCycle = map.get("isCycle").toString();
        String profitType = map.get("profitType").toString();
        String operationType = map.get("operationType").toString();
        String finprodId = map.get("finprodId").toString();
        // 查询产品要素信息
        PrdSaleParam prdSaleParam = prdSaleParamMapper.selectByPrimaryKey(finprodId);

        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        List<SelectOptionDto> options = new ArrayList<>();
        List<DictCodeList> list = new ArrayList<DictCodeList>();
        String issueStatus = finProductAdd.getIssueStatus();
        if (issueStatus != null && issueStatus.equals(DictCodeListExt1.发行状态_确认发行.getValue())) {
            list.add(DictCodeList.交易方向_产品认购);
        }
        else if (issueStatus != null && issueStatus.equals(DictCodeListExt1.发行状态_发行成功.getValue())) {
            list.add(DictCodeList.交易方向_产品认购);
            list.add(DictCodeList.交易方向_产品到期);

            list.add(DictCodeList.交易方向_份额强增);
            list.add(DictCodeList.交易方向_份额强减);

            if (StringUtil.equals(prdSaleParam.getBeforeendFlag(), DictCodeList.是否_是.getValue())) {
				Map<String, Object> param = new HashMap<>();
				param.put("finprodId", finprodId);
				param.put("adjustType", DictCodeListExt2.调整类型_提前终止.getValue());
				Date mdate = prdMdateAdjustMapper.selectMdate(param);
				if (StringUtil.equals(DateUtil.format(mdate, "yyyy-MM-dd"), DateUtil.getSysDate())) {
					list.add(DictCodeList.交易方向_提前终止);
				}
            }

            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                if (DictCodeList.开放类型_开放式.getValue().equals(operationType)) {
                    list.add(DictCodeList.交易方向_产品申购);
                    if (DictCodeList.是否_是.getValue().equals(isCycle)) {
                        list.add(DictCodeList.交易方向_产品赎回);
                        //bug BS2.1.0BSTOZ-1230 周期型产品交易类型缺少“提前赎回”。 20191109
                        if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getRedFlag())){
                        	list.add(DictCodeList.交易方向_产品提前赎回);
                        }
                    }
                    else {
                        list.add(DictCodeList.交易方向_产品赎回);
                        // update by gcl 2019/3/28 start
                        // 如果产品支持提前赎回
                        /* if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getRedFlag())){ */
                        list.add(DictCodeList.交易方向_产品提前赎回);
                        /* } */
                        // 如果产品支持违约赎回
                        /* if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getDefaultredFlag())){ */
                        // list.add(DictCodeList.交易方向_产品违约赎回);
                        /* } */
                        // update by gcl 2019/3/28 end
                    }
                }
                else if (DictCodeList.开放类型_封闭式.getValue().equals(operationType)) {
                    if (DictCodeList.是否_否.getValue().equals(isCycle)) {
                        // 如果产品支持提前赎回
                        /* if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getRedFlag())){ */
                        list.add(DictCodeList.交易方向_产品提前赎回);
                        /* } */
                        // 如果产品支持违约赎回
                        /* if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getDefaultredFlag())){ */
                        // list.add(DictCodeList.交易方向_产品违约赎回);
                        /* } */
                    }
                }
            }
            else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                // 查询货币型产品分红方式
                map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
                map.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
                List<String> payTypeList = restTemplate.postForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getFinprodPayType", map, List.class);
                if (!StringUtil.isEmptyList(payTypeList)) {
                    if (StringUtil.equals(payTypeList.get(0), DictCodeList.分红方式_现金分红.getValue())) {
                        list.add(DictCodeList.交易方向_产品现金分红);
                    }
                    else if (StringUtil.equals(payTypeList.get(0), DictCodeList.分红方式_红利再投.getValue())) {
                        list.add(DictCodeList.交易方向_产品红利转投);
                    }
                    else {
                        list.add(DictCodeList.交易方向_产品现金分红);
                        list.add(DictCodeList.交易方向_产品红利转投);
                    }
                }
                else {
                    list.add(DictCodeList.交易方向_产品现金分红);
                    list.add(DictCodeList.交易方向_产品红利转投);
                }
                if (DictCodeList.开放类型_开放式.getValue().equals(operationType)) {
                    list.add(DictCodeList.交易方向_产品申购);
                    list.add(DictCodeList.交易方向_产品赎回);
                    // 如果产品支持提前赎回
                    if (DictCodeList.是否_是.getValue().equals(prdSaleParam.getRedFlag())) {
                        list.add(DictCodeList.交易方向_产品提前赎回);
                    }
                    // 如果产品支持违约赎回
                    // if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getDefaultredFlag())){
                    // list.add(DictCodeList.交易方向_产品违约赎回);
                    // }
                    // list.add(DictCodeList.交易方向_产品现金分红);
                    // list.add(DictCodeList.交易方向_产品红利转投);
                }
                else if (DictCodeList.开放类型_封闭式.getValue().equals(operationType)) {
                    // 如果产品支持提前赎回
                    if (DictCodeList.是否_是.getValue().equals(prdSaleParam.getRedFlag())) {
                        list.add(DictCodeList.交易方向_产品提前赎回);
                    }
                    // 如果产品支持违约赎回
                    // if(DictCodeList.是否_是.getValue().equals(prdSaleParam.getDefaultredFlag())){
                    // list.add(DictCodeList.交易方向_产品违约赎回);
                    // }
                    // list.add(DictCodeList.交易方向_产品现金分红);
                    // list.add(DictCodeList.交易方向_产品红利转投);
                }
            }
        }
        // 排序
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<DictCodeList>() {

                @Override
                public int compare(DictCodeList o1, DictCodeList o2) {
                    int a = 0;
                    int b = 0;
                    if (o1.getValue().equals(DictCodeList.交易方向_产品认购.getValue())) {
                        a = 1;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品申购.getValue())) {
                        a = 2;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品赎回.getValue())) {
                        a = 3;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品提前赎回.getValue())) {
                        a = 4;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品违约赎回.getValue())) {
                        a = 5;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品现金分红.getValue())) {
                        a = 6;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品红利转投.getValue())) {
                        a = 7;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_份额强增.getValue())) {
                        a = 8;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_份额强减.getValue())) {
                        a = 9;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_产品到期.getValue())) {
                        a = 10;
                    }
                    else if (o1.getValue().equals(DictCodeList.交易方向_提前终止.getValue())) {
                        a = 11;
                    }

                    if (o2.getValue().equals(DictCodeList.交易方向_产品认购.getValue())) {
                        b = 1;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品申购.getValue())) {
                        b = 2;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品赎回.getValue())) {
                        b = 3;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品提前赎回.getValue())) {
                        b = 4;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品违约赎回.getValue())) {
                        b = 5;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品现金分红.getValue())) {
                        b = 6;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品红利转投.getValue())) {
                        b = 7;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_份额强增.getValue())) {
                        b = 8;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_份额强减.getValue())) {
                        b = 9;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_产品到期.getValue())) {
                        b = 10;
                    }
                    else if (o2.getValue().equals(DictCodeList.交易方向_提前终止.getValue())) {
                        b = 11;
                    }
                    return a - b;
                }
            });
        }
        combiOption(options, list);
        return options;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param options
     * @param arr
     * @see
     * @since 1.0
     */
    private void combiOption(List<SelectOptionDto> options, List<DictCodeList> arr) {
        for (DictCodeList dict : arr) {
            SelectOptionDto option = new SelectOptionDto();
            // 值
            option.setId(dict.getValue());
            // 文本
            option.setText(dict.getCodeName());
            // 简称
            option.setCol(dict.getDictCode().getValue());
            options.add(option);
        }
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFinprodMarketIdList(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getFinprodMarketIdList",
            map, SelectOptionDto.class);
    }

	/**
	 * <一句话功能简述> <功能详细描述>
	 *
	 * @param map
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> getFinprodMarketIdListForPgae(Map<String, Object> map) {
		return restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getFinprodMarketIdListForPgae",
				map, SelectOptionDto.class);
	}

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public FinNetProductDurationDto getCycleInfo(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public FinNetProductDurationDto getNetValueInfo(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getNetValueInfo", map,
            FinNetProductDurationDto.class);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getTdyBuyShareAmt(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getTdyBuyShareAmt",
            map, BigDecimal.class);
    }

    /**
     * @param finprodId 产品代码
     * @return
     * @see
     * @since 1.0
     */
    public Long getPurSpeed(String finprodId) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getPurSpeed",
            finprodId, Long.class);
    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getAfterTdySellShareAmt(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getAfterTdySellShareAmt", map, BigDecimal.class);
    }

    public DictCodeList getBussinessDictCodeList(FinNetProductDurationDto dto) {
        DictCodeList bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品认申购管理;
        if (DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品认申购管理;
        }
        else if (DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_产品申购;
        }
        else if (DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                 || DictCodeList.交易方向_产品提前赎回.getValue().equals(dto.getDealType())
                 || DictCodeList.交易方向_产品违约赎回.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_产品赎回;
        }
        else if (DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())
                 || DictCodeList.交易方向_提前终止.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品赎回到期管理;
        }
        else if (DictCodeList.交易方向_份额强增.getValue().equals(dto.getDealType())
                 || DictCodeList.交易方向_份额强减.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品提前赎回管理;
        }
        else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品中间付息管理;
        }
        else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dto.getDealType())) {
            bussinessDictCodeList = DictCodeList.共通业务类型_收益型产品中间付息管理;
        }
        return bussinessDictCodeList;
    }

    /**
     * 根据汇总交易表的交易id查询净值化产品存续期交易信息，组装成FinNetProductDurationDto
     *
     * @param param
     * @return
     */
    public FinNetProductDurationDto selectInfo(Map<String, Object> param) {
        FinNetProductDurationDto finNetProductDurationDto = restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/selectInfo", param, FinNetProductDurationDto.class);
        String dealType = finNetProductDurationDto.getDealType();
        String profitType = finNetProductDurationDto.getProfitType();
        if (DictCodeList.交易方向_产品认购.getValue().equals(dealType)) {

        }
        else if (DictCodeList.交易方向_产品申购.getValue().equals(dealType)) {

        }
        else if (DictCodeList.交易方向_产品赎回.getValue().equals(dealType) || DictCodeList.交易方向_产品提前赎回.getValue().equals(dealType)
                 || DictCodeList.交易方向_产品违约赎回.getValue().equals(dealType)) {
            // 查询费用明细
            List<TrdTradeFeeDetail> listTrdTradeFeeDetail = restTemplate.getForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdTradeFeeDetailByTotleTradeId?tradeId="
                                                                                    + finNetProductDurationDto.getTradeId(),
                TrdTradeFeeDetail.class);
            if (DictCodeList.收益类型_净值型.getValue().equals(profitType)) {
                if (listTrdTradeFeeDetail != null) {
                    for (TrdTradeFeeDetail trdTradeFeeDetail : listTrdTradeFeeDetail) {
                        if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(trdTradeFeeDetail.getFeeType())) {
                            finNetProductDurationDto.setRedFee(trdTradeFeeDetail.getFeeAmt());
                            finNetProductDurationDto.setRedPurFeeHd(ArithmeticUtil.subtract(
                                finNetProductDurationDto.getRedPurFeeHd(), finNetProductDurationDto.getRedFee()));
                        }
                        // else if(DictCodeListExt2.费用类型_申购费后端.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedPurFeeHd(trdTradeFeeDetail.getFeeAmt());
                        // }else if(DictCodeListExt2.费用类型_赎回费产品留存.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedFeeLc(trdTradeFeeDetail.getFeeAmt());
                        // }else if(DictCodeListExt1.一次性费用类型_违约金.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedLiquidatedDamages(trdTradeFeeDetail.getFeeAmt());
                        // }
                    }
                }
            }
            else if (DictCodeList.收益类型_货币型.getValue().equals(profitType)) {
                if (listTrdTradeFeeDetail != null) {
                    for (TrdTradeFeeDetail trdTradeFeeDetail : listTrdTradeFeeDetail) {
                        if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(trdTradeFeeDetail.getFeeType())) {
                            finNetProductDurationDto.setRedFee03(trdTradeFeeDetail.getFeeAmt());
                            if(finNetProductDurationDto.getRedPurFeeHd03().compareTo(BigDecimal.ZERO) != 0) {
                            	finNetProductDurationDto.setRedPurFeeHd03(ArithmeticUtil.subtract(
                                        finNetProductDurationDto.getRedPurFeeHd03(), finNetProductDurationDto.getRedFee03()));
                            }
                            // dto.setRedPurFeeHd03(total.getFeeAmtUnpay()==null?BigDecimal.ZERO:total.getFeeAmtUnpay()
                            // .subtract(total.getFeeAmtPay()==null?BigDecimal.ZERO:total.getFeeAmtPay()));
                        }
                        // else if(DictCodeListExt2.费用类型_申购费后端.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedPurFeeHd03(trdTradeFeeDetail.getFeeAmt());
                        // }else if(DictCodeListExt2.费用类型_赎回费产品留存.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedFeeLc03(trdTradeFeeDetail.getFeeAmt());
                        // }else if(DictCodeListExt1.一次性费用类型_违约金.getValue().equals(trdTradeFeeDetail.getFeeType())){
                        // finNetProductDurationDto.setRedLiquidatedDamages03(trdTradeFeeDetail.getFeeAmt());
                        // }
                    }
                }
            }

        }
        else if (DictCodeList.交易方向_产品到期.getValue().equals(dealType) || DictCodeList.交易方向_提前终止.getValue().equals(dealType)) {

        }
        else if (DictCodeList.交易方向_份额强增.getValue().equals(dealType) || DictCodeList.交易方向_份额强减.getValue().equals(dealType)) {

        }
        else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dealType)) {

        }
        else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dealType)) {

        }
        return finNetProductDurationDto;
    }

    public FinCashPlanDto queryBonusRule(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryBonusRule", map,
            FinCashPlanDto.class);
    }

    /**
     * 查询存续交易的 后续交易
     *
     * @param dto
     * @return
     */
    public List<TrdFinProductDealTotal> queryAfterTrdFinProductDealTotal(FinNetProductDurationDto dto) {
        return restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryAfterTrdFinProductDealTotal", dto,
            TrdFinProductDealTotal.class);
    }

    /**
     * 查询产品最后一天的头寸
     *
     * @param dto
     * @return
     */
    public BokPositionPrd queryMaxDateBokPositionPrd(FinNetProductDurationDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryMaxDateBokPositionPrd", dto,
            BokPositionPrd.class);
    }

    /**
     * add by dingjunfeng 产品在做提前终止和产品到期之前 需要判断资产有没有全部处置
     *
     * @param dto
     * @return
     */
    public Integer queryMaxDatePtlSecPosition(FinNetProductDurationDto dto) {
        return restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryMaxDatePtlSecPosition", dto, Integer.class);
    }

    public List<TrdFinProductDealTotal> queryAfterAssetTrdFinProductDealTotal(FinNetProductDurationDto dto) {
        return restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryAfterAssetTrdFinProductDealTotal", dto,
            TrdFinProductDealTotal.class);
    }

    public List<TrdFinProductDealTotal> queryTrdFinProductDealTotalByModel(TrdFinProductDealTotal cond) {
        return restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdFinProductDealTotalByModel", cond,
            TrdFinProductDealTotal.class);
    }

    public List<PrdMdateAdjust> queryPrdMdateAdjustByModel(PrdMdateAdjust condPrdMdateAdjust) {
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryPrdMdateAdjustByModel",
            condPrdMdateAdjust, PrdMdateAdjust.class);
    }

    /**
     * add by wm
     *
     * @param dto FinNetProductDurationDto
     * @return
     * @see
     * @since 1.0
     */
    public Integer queryIsExistSubTrade(FinNetProductDurationDto dto) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", dto.getFinprodId());
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryIsExistSubTrade",
            map, Integer.class);
    }

    /**
     * 根据金融产品代码查询分层金额明细
     *
     * @param map 参数
     * @return 分层金额明细
     * @see
     * @since 1.0
     */
    public List<FinNetProductDurationLayerDto> queryLayAmtDetailByFinprodId(Map<String, Object> map) {
        List<FinNetProductDurationLayerDto> layerList = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryLayAmtDetailByFinprodId", map,
            FinNetProductDurationLayerDto.class);
        if (!StringUtil.isEmptyList(layerList) && !StringUtil.equals(DictCodeList.交易方向_产品认购.getValue(), map.get("dealType"))) {
            if (StringUtil.isNotEmpty(map.get("dealDate"))) {
                map.put("dealDate", DateUtil.addDay(map.get("dealDate").toString(), -1));
            }
            for (FinNetProductDurationLayerDto finNetProductDurationLayerDto : layerList) {
                map.put("finprodId", finNetProductDurationLayerDto.getFinprodId());
                FinNetProductDurationDto finNetProductDurationDto = this.getNetValueInfo(map);
                if (finNetProductDurationDto != null) {
                    if (StringUtil.equals(DictCodeList.交易方向_产品申购.getValue(), map.get("dealType"))) {
                        finNetProductDurationLayerDto.setPurNetValueLayerDate(finNetProductDurationDto.getPurNetValueDate());
                        finNetProductDurationLayerDto.setPurUnitNetLayerValue(finNetProductDurationDto.getPurUnitNetValue());
                    }
                    else if (StringUtil.equals(DictCodeList.交易方向_产品赎回.getValue(), map.get("dealType"))
                             || StringUtil.equals(DictCodeList.交易方向_产品提前赎回.getValue(), map.get("dealType"))) {
                        finNetProductDurationLayerDto.setRedNetValueLayerDate(finNetProductDurationDto.getPurNetValueDate());
                        finNetProductDurationLayerDto.setRedUnitNetLayerValue(finNetProductDurationDto.getPurUnitNetValue());
                    }
                    else if (StringUtil.equals(DictCodeList.交易方向_产品到期.getValue(), map.get("dealType"))
                             || StringUtil.equals(DictCodeList.交易方向_提前终止.getValue(), map.get("dealType"))) {
                        finNetProductDurationLayerDto.setExpireNetValueLayerDate(finNetProductDurationDto.getPurNetValueDate());
                        finNetProductDurationLayerDto.setExpireUnitNetLayerValue(finNetProductDurationDto.getPurUnitNetValue());
                        BigDecimal purDealNum = BigDecimal.ZERO;
                        if (finNetProductDurationDto != null && finNetProductDurationDto.getPurDealNum() != null) {
                            purDealNum = finNetProductDurationDto.getPurDealNum();
                        }
                        BigDecimal tdyBuyShareAmt = this.getTdyBuyShareAmt(map);
                        if (tdyBuyShareAmt != null) {
                            purDealNum = purDealNum.add(tdyBuyShareAmt);
                        }
                        BigDecimal afterTdySellShareAmt = this.getAfterTdySellShareAmt(map);
                        if (afterTdySellShareAmt != null) {
                            purDealNum = purDealNum.subtract(afterTdySellShareAmt);
                        }
                        finNetProductDurationLayerDto.setExpireDealLayerNum(purDealNum);
                        if (StringUtil.isNotEmpty(finNetProductDurationDto.getPurUnitNetValue())
                            && StringUtil.isNotEmpty(finNetProductDurationDto.getPurDealNum())) {
                            finNetProductDurationLayerDto.setExpireDealLayerAmt(
                                finNetProductDurationDto.getPurUnitNetValue().multiply(purDealNum));
                        }
                        // finNetProductDurationLayerDto.setExpireDealLayerAmt(finNetProductDurationDto.getPurDealAmt());

                    }
                    else if (StringUtil.equals(DictCodeList.交易方向_份额强增.getValue(), map.get("dealType"))
                             || StringUtil.equals(DictCodeList.交易方向_份额强减.getValue(), map.get("dealType"))) {
                        finNetProductDurationLayerDto.setAddReduceNetValueLayerDate(
                            finNetProductDurationDto.getPurNetValueDate());
                        finNetProductDurationLayerDto.setAddReduceUnitNetLayerValue(
                            finNetProductDurationDto.getPurUnitNetValue());
                    }
                }
            }
        }
        return layerList;
    }

    /**
     * 根据金融产品代码查询分层金额明细
     *
     * @return 分层金额明细
     * @see
     * @since 1.0
     */
    public List<FinNetProductDurationLayerDto> queryLayAmtDetailByBussId(Map<String, Object> map) {
        List<FinNetProductDurationLayerDto> layerList = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryLayAmtDetailByBussId", map,
            FinNetProductDurationLayerDto.class);
        if (!StringUtil.isEmptyList(layerList)) {
            String dealType = map.get("dealType").toString();
            if (DictCodeList.交易方向_产品赎回.getValue().equals(dealType) || DictCodeList.交易方向_产品提前赎回.getValue().equals(dealType)
                || DictCodeList.交易方向_产品违约赎回.getValue().equals(dealType)) {
                // 查询费用明细
                List<TrdTradeFeeDetail> listTrdTradeFeeDetail = restTemplate.getForList(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/queryTrdTradeFeeDetailByTotleTradeId?tradeId="
                                                                                        + map.get("tradeId"),
                    TrdTradeFeeDetail.class);
                if (!StringUtil.isEmptyList(listTrdTradeFeeDetail)) {
                    for (TrdTradeFeeDetail trdTradeFeeDetail : listTrdTradeFeeDetail) {
                        for (FinNetProductDurationLayerDto layerDto : layerList) {
                            if (DictCodeListExt1.一次性费用类型_赎回费.getValue().equals(trdTradeFeeDetail.getFeeType())
                                && StringUtil.equals(layerDto.getTradeId(), trdTradeFeeDetail.getTradeId())) {
                                layerDto.setRedLayerFee(trdTradeFeeDetail.getFeeAmt());
                                layerDto.setRedPurLayerFeeHd(
                                    ArithmeticUtil.subtract(layerDto.getRedPurLayerFeeHd(), layerDto.getRedLayerFee()));
                                break;
                            }
                        }
                    }
                }
            }
        }

        return layerList;
    }

    public PrdSaleParam queryFinProductInfo(String finprodId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/queryFinProductInfo",
            map, PrdSaleParam.class);
    }
    /**
     * 根据金融产品代码查询投资期限
     * @return 投资周期
     * @see
     * @since 1.0
     */
	@SuppressWarnings("unchecked")
	public List<SelectOptionDto> getTermDaysList(Map<String, Object> map) {
		return restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getTermDaysList", map,
				List.class);
    }
	/**
     * 根据金融产品代码、起息日查询期次信息
     * @return 期次信息
     * @see
     * @since 1.0
     */
	@SuppressWarnings("unchecked")
	public List<SelectOptionDto> getPeriodsList(Map<String, Object> map) {
		return restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getPeriodsList", map,
				List.class);
    }
	/**
     * 获取系统参数
     * @return 
     * @see
     * @since 1.0
     */
	public String getSysParameter(String sysCode,String paraCode) {
		return restTemplate.getForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectParam/"+sysCode+"/"+paraCode
				, String.class);
    }
	/**
	 * 存续期新建产品画面期次子产品信息信息查询
	 */
	@SuppressWarnings("unchecked")
	public List<FinNetProductDurationDto> getPeriodInfoList(Map<String, Object> map) {
		map.put("cDate", DateUtil.platDateFromCache());			//平台日
		List<FinNetProductDurationDto> list = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getPeriodInfoList", map,
				List.class);
		return list;
    }
	/**
	 * 存续期产品赎回画面期次信息查询(交易成功数据)
	 */
	@SuppressWarnings("unchecked")
	public List<FinNetProductDurationDto> getPeriodInfoListInTrade(Map<String, Object> map) {
		map.put("busiType1", DictCodeList.业务类型_净值型TA交易.getValue());
		map.put("busiType2", DictCodeListExt2.业务类型_浮动管理费挂账交易.getValue());
		List<FinNetProductDurationDto> list = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getPeriodInfoListIntrade", map,
				List.class);
		return list;
    }
	/**
	 * 根据选择的净值日查询单位净值
	 */
	public BigDecimal getTrdValueByCdate(Map<String, Object> map) {
		map.put("branch", 0L);
		BokPositionPrd bokPositionPrd = restTemplate.postForObject(
				ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getTrdValueByCdate", map,
				BokPositionPrd.class);
		if(bokPositionPrd!=null && StringUtil.isNotEmpty(bokPositionPrd)) {
			return bokPositionPrd.getTdyUnitValue();
		}
		else {
			return BigDecimal.valueOf(0);
		}
		
   }
    public long checkId(String tradeId) {
        return restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME
                                         + "/FinNetProductDuration/checkId?tradeId="
                                         + tradeId,
                                         long.class);
    }
    /**
     * 查询产品 查询日期最近的净值信息-货币型产品分红专用
     * @param map 传入条件
     * @return dto 货币型产品当日份额及分红相关信息
     */
    public FinNetProductDurationDto getNetValueInfoForHB(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinNetProductDuration/getNetValueInfoForHB", map,
            FinNetProductDurationDto.class);
    }
}