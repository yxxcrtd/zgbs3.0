package com.joyintech.tams.invest.trade.buyoutrepo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTrade002Dto;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTrade003Dto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrdOutRightRepoTrade002Service extends BaseService<TrdOutRightRepoTrade002Dto>
		implements BaseProcessService<TrdOutRightRepoTrade002Dto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(TrdOutRightRepoTrade002Service.class);

	/**
	 * {@linkplain}
	 */
	@Autowired(required = false)
	private BussTrdTransferOrderService bussTrdTransferOrderService;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private BussCommonMapperDelegateService bussCommonMapperDelegateService;

	/**
	 * {@linkplain}
	 */
	@Autowired(required = false)
	private BaseTradeProductDealCommService baseTradeProductDealCommService;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private CommonBusinessService commonBusinessService;

	/**
	 * 风险试算组件Service
	 */
	@Autowired(required = false)
	private BasRiskMathCommService basRiskMathCommService;

	/**
	 * 费用查询
	 */
	@Autowired(required = false)
	private BaseTradeFeeCommService baseTradeFeeCommService;

	/**
	 * {@linkplain}
	 */
	@Autowired
	private IPlatformDate iPlatformDate;

	/**
	 * 附件上传类
	 */
	@Autowired(required = false)
	private FileAttachmentProcess fileAttachmentProcess;

	/**
	 * 投管接口共通组件
	 */
	@Autowired
	private InvestInterfCommService investInterfCommService;
	/**
	 * BaseGetBusinessId
	 */
	@Autowired(required = false)
	private BaseGetBusinessId baseGetBusinessId;

	@Autowired(required = false)
	private FinCashInfoMapper finCashInfoMapper;

	@Autowired(required = false)

	private FinCashPayRuleMapper finCashPayRuleMapper;

	@Autowired(required = false)
	private FinCashCalcRuleMapper finCashCalcRuleMapper;

	@Autowired(required = false)
	private FinRateInfoMapper finRateInfoMapper;

	@Autowired(required = false)
	private FinCashPlanMapper finCashPlanMapper;

	@Autowired
	private FinDealPledgeMapper finDealPledgeMapper;

	@Autowired
	private FinTradeProductMapper finTradeProductMapper;

	@Autowired
	private PtlTradeMapper ptlTradeMapper;

	@Autowired
	private FinProductTypeMapper finProductTypeMapper;

	@Autowired
	private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

	@Autowired
	private WflTrdProductDealMapper wflTrdProductDealMapper;

	@Autowired
	private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

	@Autowired
	private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

	@Autowired
	private PaTrdProductDealMapper paTrdProductDealMapper;

	@Autowired
	private WflStlPayOrdersMapper wflStlPayOrdersMapper;

	@Autowired
	private TradeCommonService tradeCommonService;
	
	@Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

	@Autowired
	private TrdCommonBaseService trdCommonBaseService;

	@Override
	public void doInsert(TrdOutRightRepoTrade002Dto entity, OperationType type) {
		if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
			this.handleBussFlowData(entity, false);
		} else {
			this.doInsertRevoke(entity);
		}
	}

	/**
	 * 工作流中doUpdate
	 *
	 * @param input input
	 */
	@Override
	public void doUpdate(TrdOutRightRepoTrade002Dto input) {
		if (!StringUtil.equals("revoke", input.getOperateType())
				&& !StringUtil.equals("delete", input.getOperateType())) {
			// 先删除流程中数据
			baseTradeProductDealCommService.deleteWflData(input.getWorkflowId());
			TrdOutRightRepoTrade002Dto entity = this.getBussinessEntityByWorkflowId(input.getWorkflowId());
			this.ptlSecPositionFlowLogicDelete(entity);

			// 重新insert
			this.handleBussFlowData(input, true);
		}
	}

	/**
	 * 工作流中doDelete
	 *
	 * @param workflowId workflowId
	 */
	@Override
	public void doDelete(String workflowId) {
		// OPCIS表Entity TODO
		baseTradeProductDealCommService.deleteWflData(workflowId);
		// 根据工作流代码查询交易编号、组合代码
		WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
				+ "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);

		if (wflTrdProductDealAdd != null) {
			this.deleteFinDealPledgeByTradeId(wflTrdProductDealAdd.getTradeId());
			this.deleteFinTradeProductByTradeId(wflTrdProductDealAdd.getTradeId());
		}
		TrdOutRightRepoTrade002Dto entity = this.getBussinessEntityByWorkflowId(workflowId);
		this.ptlSecPositionFlowLogicDelete(entity);
		
        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
	}

	@Override
	public void doComplete(TrdOutRightRepoTrade002Dto entity, OperationType operationType) {
		if (operationType == OperationType.NEW) {
			this.doCompleteInsert(entity);
		} else if (operationType == OperationType.REVOKE || operationType == OperationType.DELETE) {
			this.doCompleteRevoke(entity);
		}

		// 7.2删除原业务流程表中的交易记录
		String workflowId = entity.getWorkflowId();
		if (StringUtil.isBlank(workflowId)) {
			WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
			wflTrdProductDealKey.setTradeId(entity.getTradeId());
			wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
			WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
					WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

			if (wflTrdProductDeal != null) {
				workflowId = wflTrdProductDeal.getWorkflowId();
			}
		}

		this.deleteBussFlowData(workflowId);

//        调用接口  add by zhuyongkang 20191127 start
		List<String> tradeIds = new ArrayList<>();
		tradeIds.add(entity.getTradeId());
		try {
			restTemplate.postForObject(
					ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo",
					tradeIds, Map.class);
		} catch (Exception e) {

		}
//        调用接口  add by zhuyongkang 20191127 end

	}

	/**
	 * 撤销doRevokeComplete
	 *
	 * @param entity input
	 * @see
	 * @since 1.0
	 */
	private void doCompleteRevoke(TrdOutRightRepoTrade002Dto entity) {
	    //删除授信占用
        if (null != entity.getTradeId()  && StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	    
		entity.setActPortfolioId(entity.getPortfolioId());
		entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

		// 提前查询判断  是否  撤销 并且是 有转仓交易 并且转仓交易方向ps为买入
		String reverseTradeId = "";

		TrdProductDeal reverseTrdProductDeal = trdCommonBaseService.selectZCDealByTid(entity.getTradeId(),false);
		if (reverseTrdProductDeal != null && StringUtil.equals(DictCodeList.交易方向_买入.getValue(), reverseTrdProductDeal.getPs())) {
			reverseTradeId = reverseTrdProductDeal.getTradeId();
		}


		// 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
		List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate
				.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
						+ entity.getTradeId(), BaseTradeProductDealDto.class);

		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
			for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
				baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
				baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
				baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
				if (entity.getSecTotalPrin() != null) {
					baseDto.setSecAmount(ArithmeticUtil.divide(entity.getSecTotalPrin(), 100, 2));
				}
				baseDto.setSecLendFinprodId(entity.getFinprodId());
				PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(baseDto.getTrdProductDeal().getTradeId());
				paTrdProductDeal.setTradeId(
						paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
				paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
				paTrdProductDeal.setCreateTime(new Date());
				paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
				paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
				paTrdProductDealMapper.insertSelective(paTrdProductDeal);

				PtlTrade ptlTrade = new PtlTrade();
				ptlTrade.setPortfolioId(entity.getPortfolioId());
				ptlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
				ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
				ptlTrade = ptlTradeMapper.selectByPrimaryKey(ptlTrade);
				ptlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
				ptlTradeMapper.insertSelective(ptlTrade);

				if (StringUtil.isNotEmpty(entity.getActPortfolioId())
						&& !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
					PtlTrade actPtlTrade = new PtlTrade();
					BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
					actPtlTrade.setPortfolioId(entity.getActPortfolioId());
					actPtlTrade.setBusiNo(baseDto.getTrdProductDeal().getTradeId());
					actPtlTrade = ptlTradeMapper.selectByPrimaryKey(ptlTrade);
					actPtlTrade.setBusiNo(ptlTrade.getBusiNo() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
					ptlTradeMapper.insertSelective(actPtlTrade);
				}
			}

			finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
			finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);
			// 委托代码不为空处理委托代码
			finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
				String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId()
						: item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
				if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
					item.setEntrustId(entrustId);
				}
			});

			// 撤销时，子组合需要调估值共通
			if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
					finProductTrade.setActPortfolioId(entity.getActPortfolioId());
				});
			}
			// 调用业务组件.【金融产品交易共通.交易产品接口】, insert
			// 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
			baseTradeProductDealCommService.execute(finProductTradeDto);

			if (!StringUtil.isNotEmpty(entity.getActPortfolioId())) {
				entity.setActPortfolioId(entity.getPortfolioId());
				entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
			}

			if (StringUtil.isNotEmpty(entity.getActPortfolioId())
					&& !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getActPortfolioId()));
				baseTradeProductDealCommService.insertPosition(finProductTradeDto);
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getPortfolioId()));
			}
		}

		// 先删除流程中数据
		baseTradeProductDealCommService.deleteWflData(entity.getWorkflowId());

		// 写入质押品的组合持仓流水
		this.ptlSecPositionFlowLogic(entity, true);

		//basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

		// 调用基础组件.【交易报送组件】删除交易报送信息
		restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
				Arrays.asList(entity.getTradeId()), Boolean.class);

		// 调用资产负债信息报送组件 删除拆借报送信息
		restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepAssetDebt",
				Arrays.asList(entity.getTradeId()), Boolean.class);
		restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepLiabilities",
				Arrays.asList(entity.getTradeId()), Boolean.class);

		// 调用投管接口 add by wqd 20190218 start
		InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
		investInterfCommDto.setPageId("TrdOutRightRepoTrade002");
		investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
		investInterfCommDto.setTrdProductDeal(genTrdProductDeal(entity));
		investInterfCommDto.setTrdProductDealAdd(genTrdProductDealAdd(entity));
		investInterfCommDto.setTradeFeeDetaillist(setTrdFeeDetail(entity));
		investInterfCommDto.setFinTradeProduct(genFinTradeProduct(entity, investInterfCommDto.getTrdProductDeal().getBranch(),
				investInterfCommDto.getTrdProductDeal().getFinprodType2()));
//		investInterfCommDto.setStlPayOrders(entity.getStlPayOrders());
		investInterfCommService.doInvestInterf(investInterfCommDto);
		// 调用投管接口 add by wqd 20190218 end

		if(StringUtil.isNotEmpty(reverseTradeId)){
			reverseTrdProductDeal.setFinprodType2("");
			TrdProductDeal trdProductDeal = new TrdProductDeal();
			trdProductDeal.setTradeId(reverseTradeId);
			trdProductDeal.setCancelRemark(entity.getRevokeReason());
			trdProductDeal.setFinprodId(entity.getFinprodId());
			commonBusinessService.revokeReverseTrade(trdProductDeal,false);

		}

	}

	/**
	 * 撤销交易doInsertRevoke
	 *
	 * @param input 参数input
	 * @see
	 * @since 1.0
	 */
	private void doInsertRevoke(TrdOutRightRepoTrade002Dto input) {
		// 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
		List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate
				.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
						+ input.getTradeId(), BaseTradeProductDealDto.class);

		if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
			for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
				if (input.getSecTotalPrin() != null) {
					baseDto.setSecAmount(ArithmeticUtil.divide(input.getSecTotalPrin(), 100, 2));
				}
				baseDto.setSecLendFinprodId(input.getFinprodId());
				// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
				WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
				BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
				// 交易状态=“撤销”
				wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
				// 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
				wflTrdProductDeal.setpTradeId(input.getTradeId());
				// 撤销说明=画面.撤销说明
				wflTrdProductDeal.setCancelRemark(input.getRevokeReason());
				// 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
				wflTrdProductDeal.setTradeId(input.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
				wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
				wflTrdProductDeal.setWorkflowId(input.getWorkflowId());

				// 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
				WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
				BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
				wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
				wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
				wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
				wflTrdProductDealAdd.setWorkflowId(input.getWorkflowId());

				wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
				wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
				// 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
				if (!StringUtil.isEmptyList(baseDto.getStlPayOrdersList())) {
					List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
					for (StlPayOrdersDto stlPayOrdersDto : baseDto.getStlPayOrdersList()) {
						WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
						BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
						wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
						wflStlPayOrders.setWorkflowId(input.getWorkflowId());

						String orderId;
						if (baseGetBusinessId != null) {
							BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
							bussinessCodeParameter.setSeqNo("9998");
							orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
						} else {
							orderId = IDGenerator.generate();
						}
						wflStlPayOrders.setOrderId(orderId);
						wflStlPayOrders.setTradeId(wflTrdProductDeal.getTradeId());
						wflStlPayOrders.setpTradeId(stlPayOrdersDto.getOrderId());
						wflStlPayOrders.setProdId(wflTrdProductDeal.getTradeId());
						wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
					}
				}
			}
		}
	}

	/**
	 * 提交时登录业务流程表
	 *
	 * @param entity   画面参数dto
	 * @param isUpdate
	 * @see
	 * @since 1.0
	 */
	private void handleBussFlowData(TrdOutRightRepoTrade002Dto entity, boolean isUpdate) {
		entity.setActPortfolioId(entity.getPortfolioId());
		entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
		if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getTradeId())) {
			// 将附件和当前业务对象绑定（更新附件的业务代码）
			fileAttachmentProcess.updateBussinessNo(
					entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()),
					entity.getTradeId());
		}

		if (!StringUtil.equals("submit", entity.getOperateType())) {
			return;
		}

		TrdProductDeal trdProductDeal = this.genTrdProductDeal(entity);
		WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
		if (StringUtils.isNotBlank(entity.getDealBussId())) {
			trdProductDeal.setfTradeId(entity.getDealBussId());
		}
		wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
		wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

		// 交易类金融产品
		FinTradeProduct finTradeProduct = this.genFinTradeProduct(entity, trdProductDeal.getBranch(),
				trdProductDeal.getFinprodType2());
		// 回购质押品
		FinDealPledge finDealPledge = this.genFinDealPledge(entity, trdProductDeal.getBranch());

		if (isUpdate) {
			FinTradeProduct tempfinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProduct);
			if(tempfinTradeProduct!=null) {
				finTradeProductMapper.updateByPrimaryKeySelective(finTradeProduct);
			}else{
				finTradeProductMapper.insertSelective(finTradeProduct);
			}

			FinDealPledge tempFinDealPledge = finDealPledgeMapper.selectByPrimaryKey(finDealPledge);
			if(tempFinDealPledge!=null) {
				finDealPledgeMapper.updateByPrimaryKeySelective(finDealPledge);
			}else{
				finDealPledgeMapper.insertSelective(finDealPledge);
			}
		} else {
			finTradeProductMapper.insertSelective(finTradeProduct);
			finDealPledgeMapper.insertSelective(finDealPledge);
		}

		// 调用授信共同 sjj start
		AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
		trdProductDeal.setFinprodId(entity.getFinprodId());
		inputDto.setTrdProductDeal(trdProductDeal);
		trdProductDeal.setFinprodId(entity.getTradeId());
		AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
		// 调用授信共同 sjj end

		TrdProductDealAdd trdProductDealAdd = this.genTrdProductDealAdd(entity);
		WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDealAdd);
		wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
		wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
		wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());

		PaTrdProductDeal paTrdProductDeal = this.genPaTrdProductDeal(entity);
		WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
		wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
		wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

		// add by wangpeng 20190906 买断式回购流程中时 shareAmt存入券面总额 （ 此额度在生效时会被重新赋值）
		if (StringUtil.isNotEmpty(entity.getEntrustId()) && StringUtil.isNotEmpty(entity.getSecTotalPrin())) {
			wflTrdProductDeal.setShareAmt(ArithmeticUtil.divide(entity.getSecTotalPrin(), new BigDecimal(100), 2));
		}
		// add by wangpeng 20190906 end
		// bussCommonMapperDelegateService.insertAutoClassifyBatch(wflTrdProductDeal,
		// wflTrdProductDealAdd, wflPaTrdProductDeal);
		wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
		wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
		wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
		// 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
		if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
			WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
			BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, entity.getBussTrdTransferOrder().get(0));
			wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
			wflStlPayOrders.setTradeId(trdProductDeal.getTradeId());
			wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
			wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
		}

		BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
		// 金融产品交易
		baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
		// 金融产品交易附属
		baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
		// 划款指令
		baseTradeProductDealDto.setStlPayOrdersList(this.genStlPayOrdersDto(entity));

		if (entity.getSecTotalPrin() != null) {
			baseTradeProductDealDto.setSecAmount(ArithmeticUtil.divide(entity.getSecTotalPrin(), 100, 2));
		}
		baseTradeProductDealDto.setSecLendFinprodId(entity.getFinprodId());

		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
		finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
		// 委托代码不为空处理委托代码
		finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
			String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId()
					: item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
			if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
				item.setEntrustId(entrustId);
			}
		});
		// 调用业务组件.【金融产品交易共通.交易产品接口】, insert
		// 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
		baseTradeProductDealCommService.execute(finProductTradeDto);
		if (StringUtil.isNotEmpty(entity.getActPortfolioId())
				&& !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getActPortfolioId()));
			baseTradeProductDealCommService.insertPosition(finProductTradeDto);
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getPortfolioId()));
		}
		
		this.ptlSecPositionFlowLogic(entity, false);

	}

	/**
	 * doComplete
	 *
	 * @param entity 买断式回购交易Dto
	 * @see
	 * @since 1.0
	 */
	public void doCompleteInsert(TrdOutRightRepoTrade002Dto entity) {
		entity.setActPortfolioId(entity.getPortfolioId());
		entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
		// 金融产品交易
		TrdProductDeal trdProductDeal = this.genTrdProductDeal(entity);

		// 金融产品交易附属
		TrdProductDealAdd trdProductDealAdd = this.genTrdProductDealAdd(entity);

		// 组合交易表
		PtlTrade ptlTrade = new PtlTrade();
		ptlTrade.setPortfolioId(entity.getPortfolioId());
		ptlTrade.setBusiNo(entity.getTradeId());
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		ptlTrade.setBusiId(entity.getTradeId());
		ptlTrade.setBusiType(DictCodeList.共通业务类型_买断式回购交易.getValue());
		ptlTrade.setSecManageAcctId(entity.getSecAcctId());
		ptlTrade.setSettleDate(entity.getTradeDate());

		List<PtlTrade> ptlTradeList = new ArrayList<>();
		ptlTradeList.add(ptlTrade);
		if (!entity.getPortfolioId().equals(entity.getActPortfolioId())) {
			PtlTrade ptlTrade1 = new PtlTrade();
			ptlTrade1.setPortfolioId(entity.getActPortfolioId());
			ptlTrade1.setBusiNo(entity.getTradeId());
			ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
			ptlTrade1.setBusiId(entity.getTradeId());
			ptlTrade1.setBusiType(DictCodeList.共通业务类型_买断式回购交易.getValue());
			ptlTrade1.setSecManageAcctId(entity.getSecAcctId());
			ptlTrade1.setSettleDate(entity.getTradeDate());
			// bussCommonMapperDelegateService.insertAutoClassifyBatch(ptlTrade1);
			ptlTradeMapper.insert(ptlTrade1);
			ptlTradeList.add(ptlTrade1);
		}

		// 划款指令
		List<StlPayOrdersDto> stlPayOrdersDtoList = this.genStlPayOrdersDto(entity);

		// 交易类金融产品
		FinTradeProduct finTradeProduct = this.genFinTradeProduct(entity, trdProductDeal.getBranch(),
				trdProductDeal.getFinprodType2());

		// 金融产品分类表
		FinProductType finProductType = new FinProductType();
		finProductType.setFinprodId(entity.getTradeId());
		finProductType.setBranch(trdProductDeal.getBranch());
		finProductType.setType6(DictCodeList.回购类型_买断式回购.getValue());
		finProductType.setType7(entity.getPs());
		finProductType.setType8(DictCodeList.平安资产类型_买断式回购.getValue());
		/* 添加五级分类映射 lishuping 2019/08/13 start */
		FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
		finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
		finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_买断式回购.getValue());
		finProductTypeConfig.setInputValue2(entity.getPs());
		finProductTypeConfig = restTemplate.postForObject(
				ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType", finProductTypeConfig,
				FinProductTypeConfig.class);
		finProductType.setType9(finProductTypeConfig.getOutputValue2());
		finProductType.setType10(finProductTypeConfig.getOutputValue3());
		finProductType.setType11(finProductTypeConfig.getOutputValue4());
		finProductType.setType12(finProductTypeConfig.getOutputValue5());
		/* 添加五级分类映射 lishuping 2019/08/13 end */

		// 一体化对应etliu start
		ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
		// 一体化对应etliu end

		// 金融产品交易扩展
		PaTrdProductDeal paTrdProductDeal = this.genPaTrdProductDeal(entity);

		TrdProductDeal trdProductDeal1 = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal1, trdProductDeal);
		if (StringUtils.isNotBlank(entity.getDealBussId())) {
			trdProductDeal1.setfTradeId(entity.getDealBussId());
		}
		BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
		// 金融产品交易
		baseTradeProductDealDto.setTrdProductDeal(trdProductDeal1);
		// 金融产品交易附属
		baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
		// 划款指令
		baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtoList);
		// 买断式回购特殊处理
		if (entity.getSecTotalPrin() != null) {
			baseTradeProductDealDto.setSecAmount(ArithmeticUtil.divide(entity.getSecTotalPrin(), 100, 2));
		}
		baseTradeProductDealDto.setSecLendFinprodId(entity.getFinprodId());
		// add by wangguoxian 费用设置 20190722 start
		List<TrdTradeFeeDetail> feeDetailList = setTrdFeeDetail(entity);
		if (feeDetailList != null) {
			baseTradeProductDealDto.setTrdTradeFeeDetailList(feeDetailList);
		}
		// add by wangguoxian 费用设置 20190722 end

		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
		finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
		// 委托代码不为空处理委托代码
		finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
			String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId()
					: item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
			if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
				item.setEntrustId(entrustId);
			}
		});
		// 调用业务组件.【金融产品交易共通.交易产品接口】, insert
		// 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
		baseTradeProductDealCommService.execute(finProductTradeDto);

		if (StringUtil.isNotEmpty(entity.getActPortfolioId())
				&& !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getActPortfolioId()));
			baseTradeProductDealCommService.insertPosition(finProductTradeDto);
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getPortfolioId()));
		}

		// 回购质押品
		// FinDealPledge finDealPledge = this.genFinDealPledge(entity,
		// trdProductDeal.getBranch());

		// 同时登录组合交易表与金融产品扩展表
		ptlTradeMapper.insertSelective(ptlTrade);
		finProductTypeMapper.insertSelective(finProductType);
		paTrdProductDealMapper.insertSelective(paTrdProductDeal);

		// 现金流信息-本金
		FinCashInfo finCashInfoAmt = new FinCashInfo();
		finCashInfoAmt.setCashId(
				CommonUtil.generateCashFlowCode(entity.getTradeId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
						trdProductDeal.getBranch(), DictCodeListExt1.现金流二级类型_本金.getValue()));
		finCashInfoAmt.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
		finCashInfoAmt.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
		finCashInfoAmt.setCcy(entity.getCcy());
		finCashInfoAmt.setFinprodId(entity.getTradeId());
		finCashInfoAmt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
		finCashInfoAmt.setBranch(trdProductDeal.getBranch());

		// 现金流信息-利息
		FinCashInfo finCashInfoInt = new FinCashInfo();
		finCashInfoInt.setCashId(
				CommonUtil.generateCashFlowCode(entity.getTradeId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
						trdProductDeal.getBranch(), DictCodeListExt1.现金流二级类型_利息.getValue()));
		finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
		finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
		finCashInfoInt.setCcy(entity.getCcy());
		finCashInfoInt.setFinprodId(entity.getTradeId());
		finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
		finCashInfoInt.setBranch(trdProductDeal.getBranch());

		// 现金流支付规则表Entity-利息
		FinCashPayRule finCashPayRule = new FinCashPayRule();
		finCashPayRule.setCashId(finCashInfoInt.getCashId());
		finCashPayRule.setEffDate(entity.getVdate());
		// TODO
		// finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
		// finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
		finCashPayRule.setFirstPayDate(entity.getMdate());
		finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
		finCashPayRule.setFinprodId(entity.getTradeId());
		finCashPayRule.setFinprodType2(trdProductDeal.getFinprodType2());
		finCashPayRule.setBranch(trdProductDeal.getBranch());

		// 现金流计算规则表Entity-利息
		FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
		finCashCalcRule.setCashId(finCashInfoInt.getCashId());
		finCashCalcRule.setEffDate(entity.getVdate());
		finCashCalcRule.setCalcType(DictCodeList.计提类型_固定频率.getValue());
		finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
		finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
		finCashCalcRule.setBasis(entity.getBasis());
		finCashCalcRule.setYield(ArithmeticUtil.divide(entity.getRepoRate(), 100, 14));
		finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
		finCashCalcRule.setFinprodId(entity.getTradeId());
		finCashCalcRule.setFinprodType2(trdProductDeal.getFinprodType2());
		finCashCalcRule.setBranch(trdProductDeal.getBranch());

		// 现金流利率信息表Entity-利息
		FinRateInfo finRateInfo = new FinRateInfo();
		finRateInfo.setCashId(finCashInfoInt.getCashId());
		finRateInfo.setEffDate(entity.getVdate());
		finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
		finRateInfo.setBasis(entity.getBasis());
		finRateInfo.setRate(ArithmeticUtil.divide(entity.getRepoRate(), 100, 14));
		finRateInfo.setFinprodId(entity.getTradeId());
		finRateInfo.setFinprodType2(trdProductDeal.getFinprodType2());
		finRateInfo.setBranch(trdProductDeal.getBranch());

		// 现金流计划表Entity(本金)
		FinCashPlan finCashPlanAmt = new FinCashPlan();
		finCashPlanAmt.setCashId(finCashInfoAmt.getCashId());
		finCashPlanAmt.setCashNum(new Long(1));
		finCashPlanAmt.setCashType(DictCodeList.现金流类型_到期还本.getValue());
		finCashPlanAmt.setVdateUnadjust(entity.getVdate());
		finCashPlanAmt.setMdateUnadjust(entity.getMdate());
		finCashPlanAmt.setPayDateUnadjust(entity.getMdate());
		finCashPlanAmt.setVdate(entity.getVdate());
		finCashPlanAmt.setMdate(entity.getMdate());
		finCashPlanAmt.setPayDate(entity.getMdate());
		finCashPlanAmt.setTermdays(DateUtil.getBetweenDayNumber(entity.getVdate(), entity.getMdate()));
		finCashPlanAmt.setPrinAmt(trdProductDeal.getPrinAmt());
		finCashPlanAmt.setIntPrinAmt(trdProductDeal.getPrinAmt());
		finCashPlanAmt.setIntAmt(BigDecimal.ZERO);
		finCashPlanAmt.setIsPayInt(DictCodeList.是否_是.getValue());
		finCashPlanAmt.setCashAmt(trdProductDeal.getPrinAmt());
		finCashPlanAmt.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
		finCashPlanAmt.setFinprodId(entity.getTradeId());
		finCashPlanAmt.setFinprodType2(trdProductDeal.getFinprodType2());
		finCashPlanAmt.setBranch(trdProductDeal.getBranch());
		finCashPlanAmt.setPayType(DictCodeList.分红方式_现金分红.getValue());
		// 现金流计划表Entity(利息)
		FinCashPlan finCashPlanInt = new FinCashPlan();
		finCashPlanInt.setCashId(finCashInfoInt.getCashId());
		finCashPlanInt.setCashNum(new Long(2));
		finCashPlanInt.setCashType(DictCodeList.现金流类型_到期收息.getValue());
		finCashPlanInt.setVdateUnadjust(entity.getVdate());
		finCashPlanInt.setMdateUnadjust(entity.getMdate());
		finCashPlanInt.setPayDateUnadjust(entity.getMdate());
		finCashPlanInt.setVdate(entity.getVdate());
		finCashPlanInt.setMdate(entity.getMdate());
		finCashPlanInt.setPayDate(entity.getMdate());
		finCashPlanInt.setTermdays(DateUtil.getBetweenDayNumber(entity.getVdate(), entity.getMdate()));
		finCashPlanInt.setPrinAmt(BigDecimal.ZERO);
		finCashPlanInt.setIntPrinAmt(trdProductDeal.getPrinAmt());
		finCashPlanInt.setIntAmt(ArithmeticUtil.subtract(entity.getmSettleAmt(), entity.getfSettleAmt()));
		finCashPlanInt.setIsPayInt(DictCodeList.是否_是.getValue());
		finCashPlanInt.setCashAmt(ArithmeticUtil.subtract(entity.getmSettleAmt(), entity.getfSettleAmt()));
		finCashPlanInt.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
		finCashPlanInt.setFinprodId(entity.getTradeId());
		finCashPlanInt.setFinprodType2(trdProductDeal.getFinprodType2());
		finCashPlanInt.setBranch(trdProductDeal.getBranch());
		finCashPlanInt.setPayType(DictCodeList.分红方式_现金分红.getValue());

		// 通道交易 新增 强成西 2019/03/26 start
		TrdTradeFeeDetail trdTradeFeeDetail = null;
		if (StringUtil.isNotEmpty(trdProductDeal.getChlId())) {
			// 交易费用明细
			trdTradeFeeDetail = AllTradeCommonUtil.genChannelTradeFeeDetail(entity.getTradeId(),
					entity.getChannelFee());
			// bussCommonMapperDelegateService.insertBatch(TrdTradeFeeDetailMapper.class,
			// trdTradeFeeDetail
			// );
			trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
		}
		// 通道交易 新增 强成西 2019/03/26 end

		// 金融产品现金流信息接口
		// bussCommonMapperDelegateService.insertAutoClassifyBatch(finCashInfoAmt,
		// finCashInfoInt, finCashPayRule,
		// finCashCalcRule, finRateInfo, finCashPlanAmt, finCashPlanInt);
		finCashInfoMapper.insertSelective(finCashInfoAmt);
		finCashInfoMapper.insertSelective(finCashInfoInt);
		finCashPayRuleMapper.insertSelective(finCashPayRule);
		finCashCalcRuleMapper.insertSelective(finCashCalcRule);
		finRateInfoMapper.insertSelective(finRateInfo);
		finCashPlanMapper.insertSelective(finCashPlanAmt);
		finCashPlanMapper.insertSelective(finCashPlanInt);
		// 写入交易信息进交易报送信息表，为中债报送用
		String prodRegistCode = restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
				+ "/common/trdcommonbase/selectProdRegistCode/" + entity.getPortfolioId(), null, String.class);
		if (StringUtil.isNotEmpty(prodRegistCode)) {
			// 交易报送信息Entity
			RepTrade repTrade = new RepTrade();
			repTrade.setRepFinprodRegId(prodRegistCode);
			repTrade.setRepBankTradeCode(entity.getTradeId());
			repTrade.setRepBankAssetCode(entity.getTradeId());
			repTrade.setRepCcy(entity.getCcy());
			repTrade.setRepOccurMoney(trdProductDeal.getPrinAmt());
			// 折算人民币金额（元） = 画面.本金金额*收盘价(利用画面.币种与交易日查询汇率行情表中最新的收盘价)
			repTrade.setRepMoneyCny(ArithmeticUtil.multiply(trdProductDeal.getPrinAmt(),
					commonBusinessService.selectClosePrice(entity.getCcy(), DateUtil.format(entity.getTradeDate()))));
			if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
				repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue());
			} else if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
				repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue());
			}
			repTrade.setRepTradeDate(entity.getTradeDate());
			if (entity.getTrdOutRightRepoPersonInfo() != null) {
				repTrade.setRepTradeComponent(entity.getTrdOutRightRepoPersonInfo().getCounterId());
			}
			repTrade.setRepDelCode(entity.getDealBussId());
			repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
			repTrade.setRepStatusDate(iPlatformDate.getSysDate());
			repTrade.setRepRemark(entity.getRemark());

			// 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
			restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepTrade",
					Arrays.asList(repTrade), Boolean.class);

			// 资产负债报送信息Entity
			RepAssetDept repAssetDept = new RepAssetDept();
			repAssetDept.setRepFinprodId(entity.getTradeId());
			repAssetDept.setFinprodMarketId(entity.getTradeId());
			repAssetDept.setRepIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
			repAssetDept.setRepAssetType(entity.getPs());
			repAssetDept.setRepCcy(entity.getCcy());
			repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
		      /*【回购】 通道交易 买断式回购，质押式回购，REP_ASSET_DEPT 不应存值 add by hxy 20200413*/
			if (StringUtil.isEmpty(trdProductDeal.getChlId())) {
				// 调用资产负债信息报送组件
				restTemplate.postForObject(
						ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
						Arrays.asList(repAssetDept), Boolean.class);		
			}
/*			// 调用资产负债信息报送组件
			restTemplate.postForObject(
					ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepAssetDebt",
					Arrays.asList(repAssetDept), Boolean.class);*/

			// 拆入、正回购 -> 负债(拆入或正回购)类要素登记
			if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
				// 负债(同业拆入/卖出回购)报送信息Entity
				RepLiabilities repLiabilities = new RepLiabilities();
				repLiabilities.setRepFinprodId(entity.getTradeId());
				repLiabilities.setRepValueDate(entity.getVdate());
				repLiabilities.setRepExpiringDate(entity.getMdate());
				if (entity.getTrdOutRightRepoPersonInfo() != null) {
					repLiabilities.setRepCounterparty(entity.getTrdOutRightRepoPersonInfo().getCounterId());
				}
				repLiabilities.setRepAnnualInterestRate(entity.getRepoRate());
				repLiabilities.setRepBasis(entity.getBasis());
				repLiabilities.setRepRemark(entity.getRemark());
				repLiabilities.setRepTargetType("01");
				repLiabilities.setRepBuyBackMoney(trdProductDeal.getPrinAmt());

				// 调用负债(同业拆入卖出回购)报送信息组件
				restTemplate.postForObject(
						ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepLiabilities",
						Arrays.asList(repLiabilities), Boolean.class);
			}
			// 拆出、逆回购 -> 拆放同业及买入返售
			else {
				RepAssetInterLend repAssetInterLend = new RepAssetInterLend();
				repAssetInterLend.setRepFinprodId(entity.getTradeId());
				repAssetInterLend.setRepVdate(entity.getVdate());
				repAssetInterLend.setRepMdate(entity.getMdate());
				if (entity.getTrdOutRightRepoPersonInfo() != null) {
					repAssetInterLend.setRepTradeTarget(entity.getTrdOutRightRepoPersonInfo().getCounterId());
				}
				repAssetInterLend.setRepRateYear(entity.getRepoRate());
				repAssetInterLend.setRepBasis(entity.getBasis());
				repAssetInterLend.setRepRemark(entity.getRemark());
				repAssetInterLend.setRepTargetType("01");
				repAssetInterLend.setRepTargetAmt(trdProductDeal.getPrinAmt());

				// 调用负债(同业拆入卖出回购)报送信息组件
				restTemplate.postForObject(
						ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertRepDlRepoInfo",
						Arrays.asList(repAssetInterLend), Boolean.class);
			}
		}

		// 风险试算
		//basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

		// 一体化对应etliu start
		AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
		inputDto.setTrdProductDeal(trdProductDeal);
		inputDto.setProdid(entity.getPortfolioId());
		// <!-- 通道交易 新增 强成西 2019/03/26 start -->
		inputDto.setTrdProductDealAdd(trdProductDealAdd);
		inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
		// <!-- 通道交易 新增 强成西 2019/03/26 end -->
		inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());

		if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
			inputDto.setActPortfolioId(entity.getActPortfolioId());
		}
		inputDto.setPtlTradeList(ptlTradeList);
		if (stlPayOrdersDtoList != null && !stlPayOrdersDtoList.isEmpty()) {
			inputDto.setStlPayOrders(stlPayOrdersDtoList.get(0));
		}
		AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
		// 一体化对应etliu end
		// 调用投管接口 add by wqd 20190218 start
		InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
		investInterfCommDto.setPageId("TrdOutRightRepoTrade002");
		investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
		investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
		investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
		investInterfCommDto.setFinTradeProduct(finTradeProduct);
		investInterfCommDto.setTradeFeeDetaillist(feeDetailList);
		investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
		
		investInterfCommService.doInvestInterf(investInterfCommDto);
		// 调用投管接口 add by wqd 20190218 end
	}

	/**
	 * 查询债券代码下拉框
	 *
	 * @param map 查询参数
	 * @return 债券代码下拉框
	 */
	public List<SelectOptionDto> querySecFinprodIdList(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/querySecFinprodIdList", map,
				SelectOptionDto.class);
	}

	/**
	 * 查询委托过来的剩余委托额度
	 */
	public BigDecimal selectDoEntrustMoney(String entrustId) {
		String[] entrustIdList = entrustId.split("\\,");
		Map<String, Object> param = new HashMap<>();
		param.put("entrustIdList", entrustIdList);

		BigDecimal EntrustMoney = restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/selectDoEntrustMoney", param,
				BigDecimal.class);
		return EntrustMoney;
	}

	/**
	 * 根据交易编号查询买断式回购信息
	 * 
	 * @param tradeId 交易编号
	 * @return 买断式回购信息
	 */
	public TrdOutRightRepoTrade002Dto queryBuyOutRepoByTradeId(String tradeId) {
		TrdOutRightRepoTrade002Dto TrdOutRightRepoTrade002Dto = restTemplate
				.getForObject(
						ServiceApplication.TRADE_SERVICE_NAME
								+ "/TrdOutRightRepoTrade/queryBuyOutRepoByTradeId?tradeId=" + tradeId,
						TrdOutRightRepoTrade002Dto.class);
		if (TrdOutRightRepoTrade002Dto != null) {
			TrdOutRightRepoTrade002Dto.setTradeId(tradeId);
			TrdProductDeal trdProductDeal = bussCommonMapperDelegateService
					.selectBySinglePrimaryKey(TrdProductDealMapper.class, tradeId, TrdProductDeal.class);
			if (trdProductDeal != null) {
				TrdInvestPersonInfo002Dto trdOutRightRepoPersonInfo = new TrdInvestPersonInfo002Dto();
				trdOutRightRepoPersonInfo.setTrader(trdProductDeal.getTrader());
				trdOutRightRepoPersonInfo.setCounterId(trdProductDeal.getCounterId());
				trdOutRightRepoPersonInfo.setIsOutTrade(trdProductDeal.getIsOutTrade());
				trdOutRightRepoPersonInfo.setCounterTrader(trdProductDeal.getCounterTrader());
				trdOutRightRepoPersonInfo.setContactInfo(trdProductDeal.getCounterContact());
				TrdOutRightRepoTrade002Dto.setTrdOutRightRepoPersonInfo(trdOutRightRepoPersonInfo);
			}
			// 获取费用信息
			TrdTradeFeeDetailKey feeDetailKey = new TrdTradeFeeDetailKey();
			feeDetailKey.setTradeId(tradeId);
			feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_交易手续费.getValue());
			TrdTradeFeeDetail procedureAmtDetail = bussCommonMapperDelegateService
					.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class, feeDetailKey, TrdTradeFeeDetail.class);
			if (procedureAmtDetail != null) {
				TrdOutRightRepoTrade002Dto.setProcedureAmt(procedureAmtDetail.getFeeAmt());
				TrdOutRightRepoTrade002Dto.setProcedureAmtRate(procedureAmtDetail.getFeeRate());

			}
			feeDetailKey.setFeeType(BussinessDictCodeList.费用类型_结算手续费.getValue());
			TrdTradeFeeDetail settleAmtDetail = bussCommonMapperDelegateService
					.selectByMultiPrimaryKey(TrdTradeFeeDetailMapper.class, feeDetailKey, TrdTradeFeeDetail.class);
			if (settleAmtDetail != null) {

				TrdOutRightRepoTrade002Dto.setSettleAmt(settleAmtDetail.getFeeAmt());
				TrdOutRightRepoTrade002Dto.setSettleAmtRate(settleAmtDetail.getFeeRate());
			}
		}
		return TrdOutRightRepoTrade002Dto;
	}

	public TrdProductDeal genTrdProductDeal(TrdOutRightRepoTrade002Dto entity) {
		// 金融产品交易
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		trdProductDeal.setTradeId(entity.getTradeId());
		trdProductDeal.setBusiType(DictCodeList.共通业务类型_买断式回购交易.getValue());
		trdProductDeal.setFinprodId(entity.getTradeId());
		if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
			trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
		} else if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
			trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
		}
		trdProductDeal.setSecManageAcctId(entity.getSecAcctId());
		trdProductDeal.setChlId(entity.getChlAgrtId());
		trdProductDeal.setfTradeId(entity.getDealBussId());
		trdProductDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		trdProductDeal.setUnitCprice(entity.getfCprice());
		// add by wanshiwei ZGZGST-3595 回购类资产“利率%”取值错误，取资产的回购利率 20210928 start
		trdProductDeal.setUnitInt(ArithmeticUtil.divide(entity.getRepoRate(), 100, 8));
		// add by wanshiwei ZGZGST-3595 回购类资产 “利率%”取值错误，取资产的回购利率 20210928 end
		trdProductDeal.setUnitFprice(entity.getfFprice());
		// trdProductDeal.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(entity.getfCprice(),
		// entity.getSecTotalPrin()), 100, 2));
		trdProductDeal.setCpriceAmt(entity.getfSettleAmt());// 净价总额=首次结算金额
		trdProductDeal.setTradeMarket(entity.getTradeMarket());
		if (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(entity.getTradeMarket())
				|| DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
			trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
		}
		trdProductDeal.setCcy(entity.getCcy());
		trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
		trdProductDeal.setTradeDate(entity.getTradeDate());
		trdProductDeal.setVdate(entity.getVdate());
		trdProductDeal.setMdate(entity.getMdate());
		trdProductDeal.setSettleDate(entity.getTradeDate());
		if (StringUtil.equals(entity.getSettleSpeed(), DictCodeList.清算速度_T_0.getValue())) {
			trdProductDeal.setSettleSpeed(new Long(0));
		} else if (StringUtil.equals(entity.getSettleSpeed(), DictCodeList.清算速度_T_1.getValue())) {
			trdProductDeal.setSettleSpeed(new Long(1));
		}
		// trdProductDeal.setShareAmt(ArithmeticUtil.divide(entity.getSecTotalPrin(),
		// 100, 2));
		trdProductDeal.setShareAmt(entity.getfSettleAmt());// 交易份额=首次结算金额
//		trdProductDeal.setPrinAmt(entity.getfSettleAmt());// 交易本金=首次结算金额
		trdProductDeal.setPrinAmt(entity.getSecTotalPrin());//交易本金=页面券面总额
		// trdProductDeal.setIntAmt(entity.getfIntAmt());
		trdProductDeal.setIntAmt(BigDecimal.ZERO);// 利息总额=0
		// trdProductDeal.setFpriceAmt(
		// ArithmeticUtil.divide(ArithmeticUtil.multiply(entity.getfFprice(),
		// entity.getSecTotalPrin()), 100, 2));
		trdProductDeal.setFpriceAmt(entity.getfSettleAmt());// 全价总额=首次结算金额
		trdProductDeal.setFeeAmtPay(BigDecimal.ZERO);// 随交易支付费用=0
		trdProductDeal.setTradeAmt(entity.getfSettleAmt());
		trdProductDeal.setYield(ArithmeticUtil.divide(entity.getfYield(), 100, 8));
		trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
		trdProductDeal.setDeliveryType(entity.getfSettleMethod());
		if (entity.getTrdOutRightRepoPersonInfo() != null) {
			trdProductDeal.setTrader(entity.getTrdOutRightRepoPersonInfo().getTrader());
			trdProductDeal.setCounterId(entity.getTrdOutRightRepoPersonInfo().getCounterId());
			trdProductDeal.setIsOutTrade(entity.getTrdOutRightRepoPersonInfo().getIsOutTrade());
			trdProductDeal.setCounterTrader(entity.getTrdOutRightRepoPersonInfo().getCounterTrader());
			trdProductDeal.setCounterContact(entity.getTrdOutRightRepoPersonInfo().getContactInfo());
		}
		trdProductDeal.setRemark(entity.getRemark());
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		trdProductDeal.setUpdateTime(entity.getUpdateTime());
		trdProductDeal.setUpdateUser(entity.getUpdateUser());
		trdProductDeal.setCreateDept(entity.getCreateDept());
		trdProductDeal.setCreateTime(entity.getCreateTime());
		trdProductDeal.setCreateUser(entity.getCreateUser());

		// <!-- 通道交易 新增 强成西 2019/03/26 start -->
		trdProductDeal.setChlId("999999".equals(entity.getChlId()) ? null : entity.getChlId());
		if (DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getPs())) {
			trdProductDeal.setTradeAmt(ArithmeticUtil.doAdd(trdProductDeal.getTradeAmt(), entity.getChannelFee()));
		} else {
			trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(trdProductDeal.getTradeAmt(), entity.getChannelFee()));
		}
		trdProductDeal.setFeeAmtPay(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmtPay(), entity.getChannelFee()));
		trdProductDeal.setFeeAmt(ArithmeticUtil.doAdd(trdProductDeal.getFeeAmt(), entity.getChannelFee()));
		// <!-- 通道交易 新增 强成西 2019/03/26 end -->
		return trdProductDeal;
	}

	private TrdProductDealAdd genTrdProductDealAdd(TrdOutRightRepoTrade002Dto entity) {
		// 金融产品交易附属
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		trdProductDealAdd.setTradeId(entity.getTradeId());
		trdProductDealAdd.setPortfolioId(entity.getPortfolioId());
		trdProductDealAdd.setEntrustId(entity.getEntrustId());
		trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		trdProductDealAdd.setUpdateTime(entity.getUpdateTime());
		trdProductDealAdd.setUpdateUser(entity.getUpdateUser());
		trdProductDealAdd.setCreateDept(entity.getCreateDept());
		trdProductDealAdd.setCreateTime(entity.getCreateTime());
		trdProductDealAdd.setCreateUser(entity.getCreateUser());

		return trdProductDealAdd;
	}

	private PaTrdProductDeal genPaTrdProductDeal(TrdOutRightRepoTrade002Dto entity) {
		// 金融产品交易扩展
		PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
		paTrdProductDeal.setTradeId(entity.getTradeId());
		paTrdProductDeal.setBackLendDirection(entity.getPs());
		if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
			paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_买断式正回购.getValue());
		} else if (StringUtil.equals(entity.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
			paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
		}
		if (entity.getTrdOutRightRepoPersonInfo() != null) {
			paTrdProductDeal.setDeptCode(entity.getTrdOutRightRepoPersonInfo().getTraderDept());
		}
		paTrdProductDeal.setUpdateTime(entity.getUpdateTime());
		paTrdProductDeal.setUpdateUser(entity.getUpdateUser());
		paTrdProductDeal.setCreateDept(entity.getCreateDept());
		paTrdProductDeal.setCreateTime(entity.getCreateTime());
		paTrdProductDeal.setCreateUser(entity.getCreateUser());

		return paTrdProductDeal;
	}

	private List<StlPayOrdersDto> genStlPayOrdersDto(TrdOutRightRepoTrade002Dto entity) {
		// 划款指令
		List<StlPayOrdersDto> stlPayOrdersDtoList = new ArrayList<StlPayOrdersDto>();
		List<BussTrdTransferOrderDto> orderList = bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder());
		if (!StringUtil.isEmptyList(orderList)) {
			for (BussTrdTransferOrderDto bussTrdTransferOrderDto : orderList) {
				StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
				BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, bussTrdTransferOrderDto);
				stlPayOrdersDto.setTradeId(entity.getTradeId());
				stlPayOrdersDto.setWorkflowId(entity.getWorkflowId());
				stlPayOrdersDto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());

				stlPayOrdersDtoList.add(stlPayOrdersDto);
			}
		}

		return stlPayOrdersDtoList;
	}

	private void ptlSecPositionFlowLogicDelete(TrdOutRightRepoTrade002Dto input) {
		this.deletePtlSecPositionFlow(input.getTradeId());
	}

	/**
	 * 写入质押品的组合持仓流水
	 *
	 * @param input    画面参数Dto
	 * @param isNegate 是否冲销
	 */
	private boolean ptlSecPositionFlowLogic(TrdOutRightRepoTrade002Dto input, boolean isNegate) {
		List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
		// 冲销时（撤销）
		if (isNegate) {
			// 根据交易编号检索组合持仓流水（持仓类型为实际）
			List<PtlSecPositionFlow> lstReal = this.selectPtlSecPositionFlow(input.getTradeId());
			if (!StringUtil.isEmptyList(lstReal)) {
				PtlSecPositionFlowDto resultDto;
				for (PtlSecPositionFlow ptlSecPositionFlow : lstReal) {
					resultDto = new PtlSecPositionFlowDto();
					// 组合代码
					resultDto.setPortfolioId(ptlSecPositionFlow.getPortfolioId());
					// 证券管理账户/通道代码
					resultDto.setSecAcctId(ptlSecPositionFlow.getSecAcctId());
					// 金融产品代码
					resultDto.setFinprodId(ptlSecPositionFlow.getFinprodId());
					// 分支序号
					resultDto.setBranch(ptlSecPositionFlow.getBranch());
					// 持仓类型
					resultDto.setHodingType(ptlSecPositionFlow.getHodingType());
					// 日期
					resultDto.setCdate(ptlSecPositionFlow.getCdate());
					// 数量
					resultDto.setAmount(ptlSecPositionFlow.getAmount().negate());
					// 币种
					resultDto.setCcy(ptlSecPositionFlow.getCcy());
					// 交易编号
					resultDto.setTradeId(ptlSecPositionFlow.getTradeId());
					// 投资目的
					resultDto.setInvAim(ptlSecPositionFlow.getInvAim());

					lstPtlSecPositionFlowDto.add(resultDto);

					if (StringUtil.isNotEmpty(input.getActPortfolioId())
							&& !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
						PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
						BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
						subResultDto.setPortfolioId(input.getActPortfolioId());
						lstPtlSecPositionFlowDto.add(subResultDto);
					}
				}
			}
			restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
					+ "/TrdOutRightRepoTrade/updatePledgeBondForRevoke/" + input.getTradeId(), "null", Long.class);
		} else {
			// List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
			// 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
			// 可质押券信息（含投资目的）

			if (DictCodeList.回购拆借方向_正回购.getValue().equals(input.getPs())) {
				Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input).stream()
						.collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
				avaMap.entrySet().stream().forEach(entry -> {
					Collections.sort(entry.getValue(), new Comparator<PledgeBondInfoDto>() {

						@Override
						public int compare(PledgeBondInfoDto o1, PledgeBondInfoDto o2) {
							return o1.getInvAimSort().compareTo(o2.getInvAimSort());
						}
					});
				});

				// 待分配投资目的的质押券面总额
				BigDecimal pledgePrin = input.getSecTotalPrin();

				FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
						input.getFinprodId(), FinProduct.class);
				List<PledgeBondInfoDto> pledgeList = avaMap.get(finProduct.getFinprodMarketId());
				if (!StringUtil.isEmptyList(pledgeList)) {
					for (PledgeBondInfoDto avaBond : pledgeList) {
						//优先级为0    融入券  不可以融出
						if(StringUtil.equals("0", avaBond.getInvAimSort().toString())){
							continue;
						}
						PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
						// 当前投资目的可分配券面总额
						BigDecimal avaPrin = avaBond.getAvaPledgePrin();
						// 当前投资目的决定分配的券面总额
						BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

						// 组合代码 = 画面.组合代码
						resultDto.setPortfolioId(input.getPortfolioId());
						// 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
						// resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId()) ? "999999" :
						// input.getSecAcctId());
						resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId())
								? (StringUtil.isEmpty(input.getChlId()) ? "999999" : input.getChlId())
								: input.getSecAcctId());
						// 金融产品代码 = 输出质押品DTO.金融产品代码
						resultDto.setFinprodId(input.getFinprodId());
						// 分支序号 = 系统固定参数，默认为0
						resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
						// 持仓类型
						resultDto.setHodingType(DictCodeList.持仓类型_融出.getValue());
						// 日期 = 金融产品交易Entity.交易日
						resultDto.setCdate(input.getTradeDate());
						// 数量 = 输出质押品DTO.质押数量
						resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
						// 币种 = 金融产品交易Entity.币种
						resultDto.setCcy(input.getCcy());
						// 交易编号 = 金融产品交易Entity.交易编号
						resultDto.setTradeId(input.getTradeId());
						// 母金融产品代码
						resultDto.setpFinprodId(input.getTradeId());

						// 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
						resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

						if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
							lstPtlSecPositionFlowDto.add(resultDto);
							if (StringUtil.isNotEmpty(input.getActPortfolioId())
									&& !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
								PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
								BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
								subResultDto.setPortfolioId(input.getActPortfolioId());
								lstPtlSecPositionFlowDto.add(subResultDto);
							}
						}
						// 分配完毕后,停止可质押券信息（含投资目的）的循环
						pledgePrin = pledgePrin.subtract(prin);
						if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					}
				}
			} else {
				PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
				// 组合代码 = 画面.组合代码
				resultDto.setPortfolioId(input.getPortfolioId());
				// 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
				// resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId()) ? "999999" :
				// input.getSecAcctId());
				resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId())
						? (StringUtil.isEmpty(input.getChlId()) ? "999999" : input.getChlId())
						: input.getSecAcctId());
				// 金融产品代码 = 输出质押品DTO.金融产品代码
				resultDto.setFinprodId(input.getFinprodId());
				// 分支序号 = 系统固定参数，默认为0
				resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
				// 持仓类型
				resultDto.setHodingType(DictCodeList.持仓类型_融入.getValue());
				// 日期 = 金融产品交易Entity.交易日
				resultDto.setCdate(input.getTradeDate());
				// 数量 = 输出质押品DTO.质押数量
				resultDto.setAmount(ArithmeticUtil.divide(input.getSecTotalPrin(), 100, 4));
				// 币种 = 金融产品交易Entity.币种
				resultDto.setCcy(input.getCcy());
				// 交易编号 = 金融产品交易Entity.交易编号
				resultDto.setTradeId(input.getTradeId());

				// 母金融产品代码
				resultDto.setpFinprodId(input.getTradeId());

				// 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
				resultDto.setInvAim("999999");
				lstPtlSecPositionFlowDto.add(resultDto);

				if (StringUtil.isNotEmpty(input.getActPortfolioId())
						&& !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
					PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
					BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
					subResultDto.setPortfolioId(input.getActPortfolioId());
					lstPtlSecPositionFlowDto.add(subResultDto);
				}
			}
			if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
				return true;
			} else {
				PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
				ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
				return restTemplate.postForObject(
						ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
						ptlPositionHoldDto, Boolean.class);
			}
		}
		return true;
	}

	/**
	 * 根据交易编号检索组合持仓流水（持仓类型为融入融出）
	 *
	 * @param bizCode 交易编号
	 * @return List<PtlSecPositionFlowDto>
	 * @see
	 * @since 1.0
	 */
	public List<PtlSecPositionFlow> selectPtlSecPositionFlow(String bizCode) {
		return restTemplate.postForList(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/selectPtlSecPositionFlow/" + bizCode,
				null, PtlSecPositionFlow.class);
	}

	/**
	 * 根据交易编号删除组合持仓流水（持仓类型为融入融出）
	 *
	 * @param bizCode 交易编号
	 * @return List<PtlSecPositionFlowDto>
	 * @see
	 * @since 1.0
	 */
	public long deletePtlSecPositionFlow(String bizCode) {
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/deletePtlSecPositionFlow/" + bizCode,
				"null", Long.class);
	}

	/**
	 * 检查日期区间是否有 付息计划
	 * 
	 * @param finprodId 债券金融产品代码
	 * @param vdate     起息日
	 * @param mdate     到期日
	 * @return 是否有 付息计划
	 */
	public int checkDateInPayPlan(String finprodId, Date vdate, Date mdate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("finprodId", finprodId);
		map.put("vdate", DateUtil.format(vdate));
		map.put("mdate", DateUtil.format(mdate));
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/checkDateInPayPlan", map, Integer.class);
	}

	/**
	 * 根据画面条件检索可质押券信息（含投资目的）
	 *
	 * @param input 画面参数
	 * @return List<PledgeBondInfoDto>
	 */
	public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdOutRightRepoTrade002Dto input) {
		Map<String, Object> param = new HashMap<>();
		param.put("portfolioId", input.getPortfolioId());
		param.put("ps", input.getPs());
		param.put("isWithInvAim", true);
		param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
		param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
		param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
		param.put("chlId", input.getChlId());
		param.put("secAcctId", input.getSecAcctId());
		// return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME +
		// "/BussRepoSec/selectAvaPledgeBondDetailList",
		// param, PledgeBondInfoDto.class);
		return restTemplate.postForList(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/selectAvaPledgeBondDetailList", param,
				PledgeBondInfoDto.class);
	}

	/**
	 * 删除业务流程数据
	 *
	 * @param workflowId 工作流代码
	 * @see
	 * @since 1.0
	 */
	private void deleteBussFlowData(String workflowId) {
		// // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
		// bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL",
		// "WORKFLOW_ID",
		// workflowId);
		//
		// // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
		// bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
		// "WORKFLOW_ID", workflowId);
		//
		// // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
		// bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
		// "WORKFLOW_ID", workflowId);
		//
		// // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
		// bussCommonMapperDelegateService.deleteBySpecificKey("WFL_STL_PAY_ORDERS",
		// "WORKFLOW_ID",
		// workflowId);
		baseTradeProductDealCommService.deleteWflData(workflowId);
	}

	/**
	 * 查询委托信息
	 *
	 * @param entrustId 委托编号
	 * @return 委托信息
	 * @see
	 * @since 1.0
	 */
	public TrdOutRightRepoTrade002Dto getEntrustInfo(String entrustId) {
		TrdOutRightRepoTrade002Dto trdOutRightRepoTrade002Dto = new TrdOutRightRepoTrade002Dto();
		BaseEtrInvestInfoDto baseEtrInvestInfoDto = restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/getEntrustInfo/" + entrustId, null,
				BaseEtrInvestInfoDto.class);
		if (baseEtrInvestInfoDto != null) {
			// 委托信息
			if (baseEtrInvestInfoDto.getEtrEntrust() != null) {
				// 基本信息
				trdOutRightRepoTrade002Dto.setEntrustId(entrustId);
				if (StringUtil.isNotEmpty(baseEtrInvestInfoDto.getEtrEntrust().getChlId())){
					trdOutRightRepoTrade002Dto.setChlId(baseEtrInvestInfoDto.getEtrEntrust().getChlId());
				}
				trdOutRightRepoTrade002Dto.setFinprodId(baseEtrInvestInfoDto.getEtrEntrust().getFinprodId());
				trdOutRightRepoTrade002Dto.setPortfolioId(baseEtrInvestInfoDto.getEtrEntrust().getPortfolioId());
				trdOutRightRepoTrade002Dto.setTradeMarket(baseEtrInvestInfoDto.getEtrEntrust().getTradeMarket());
				trdOutRightRepoTrade002Dto.setDealCalId(baseEtrInvestInfoDto.getEtrEntrust().getCalendarId());
				trdOutRightRepoTrade002Dto.setCcy(baseEtrInvestInfoDto.getEtrEntrust().getCcy());
				// 扣除流程中的委托额度与已完成委托额度
				BigDecimal entrustNum = tradeCommonService.getEntrustAmtByEntrustId(entrustId);
				BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(entrustId,
						DictCodeList.平安资产类型_买断式回购.getValue(), null);
				trdOutRightRepoTrade002Dto.setSecTotalPrin(
						ArithmeticUtil.divide(ArithmeticUtil.subtract(entrustNum, wflPrinAmt), new BigDecimal(100), 4)); // 委托的是数量，应该乘以100面值，页面是万元，所以再除以10000
				// 扣除流程中的委托额度与已完成委托额度
//                trdOutRightRepoTrade002Dto.setSecTotalPrin(
//                    ArithmeticUtil.divide(baseEtrInvestInfoDto.getEtrEntrust().getEntrustNum(), new BigDecimal(100), 2)); // 委托的是数量，应该乘以100面值，页面是万元，所以再除以10000
				trdOutRightRepoTrade002Dto.setRepoRate(
						ArithmeticUtil.multiply(baseEtrInvestInfoDto.getEtrEntrust().getPrice(), new BigDecimal(100)));
				// 人员信息
				TrdInvestPersonInfo002Dto trdOutRightRepoPersonInfo = new TrdInvestPersonInfo002Dto();
				trdOutRightRepoPersonInfo.setTrader(LoginUserUtil.getUserName());
				trdOutRightRepoPersonInfo.setTraderDept(LoginUserUtil.getDeptCode());
				trdOutRightRepoPersonInfo.setCounterId(baseEtrInvestInfoDto.getEtrEntrust().getCounterId());
				trdOutRightRepoPersonInfo.setCounterTrader(baseEtrInvestInfoDto.getEtrEntrust().getCounterTrader());
				trdOutRightRepoPersonInfo.setContactInfo(baseEtrInvestInfoDto.getEtrEntrust().getContactInfo());
				trdOutRightRepoTrade002Dto.setTrdOutRightRepoPersonInfo(trdOutRightRepoPersonInfo);
			}
			if (baseEtrInvestInfoDto.getEtrEntrustAdd() != null) {
				trdOutRightRepoTrade002Dto.setPs(baseEtrInvestInfoDto.getEtrEntrustAdd().getBusiPs());
				trdOutRightRepoTrade002Dto.setTermType(baseEtrInvestInfoDto.getEtrEntrustAdd().getTermType());
				trdOutRightRepoTrade002Dto.setVdate(baseEtrInvestInfoDto.getEtrEntrustAdd().getVdate());
				trdOutRightRepoTrade002Dto.setMdate(baseEtrInvestInfoDto.getEtrEntrustAdd().getMdate());
				if (baseEtrInvestInfoDto.getEtrEntrustAdd().getVdate() != null
						&& baseEtrInvestInfoDto.getEtrEntrustAdd().getMdate() != null) {
					trdOutRightRepoTrade002Dto.setActAmtDays(
							DateUtil.getBetweenDayNumber(baseEtrInvestInfoDto.getEtrEntrustAdd().getVdate(),
									baseEtrInvestInfoDto.getEtrEntrustAdd().getMdate()) + "");
				}
				trdOutRightRepoTrade002Dto.setRepoterm(baseEtrInvestInfoDto.getEtrEntrustAdd().getTermDays());
				trdOutRightRepoTrade002Dto.setSecAcctId(baseEtrInvestInfoDto.getEtrEntrustAdd().getSecAcctId());
			}
			trdOutRightRepoTrade002Dto.setSettleSpeed(baseEtrInvestInfoDto.getSettleSpeedStr());

			TrdOutRightRepoTrade003Dto entrustInfo = new TrdOutRightRepoTrade003Dto();
			entrustInfo.setEntrustAmt(
					ArithmeticUtil.multiply(trdOutRightRepoTrade002Dto.getSecTotalPrin(), new BigDecimal(100)));
			entrustInfo.setEntrustRepoRate(trdOutRightRepoTrade002Dto.getRepoRate());
			entrustInfo.setEntrustVdate(trdOutRightRepoTrade002Dto.getVdate());
			entrustInfo.setEntrustMdate(trdOutRightRepoTrade002Dto.getMdate());
			if (baseEtrInvestInfoDto.getEtrEntrustAdd() != null) {
				entrustInfo.setEntrustTermStart(baseEtrInvestInfoDto.getEtrEntrustAdd().getTermDays());
				entrustInfo.setEntrustTermEnd(baseEtrInvestInfoDto.getEtrEntrustAdd().getTermDays2());
			}
			trdOutRightRepoTrade002Dto.setEntrustInfo(entrustInfo);
		}

		return trdOutRightRepoTrade002Dto;
	}

	/**
	 * 生成回购质押品信息
	 *
	 * @param entity 买断式回购信息
	 * @param branch 分支序号
	 * @return 回购质押品信息
	 */
	private FinDealPledge genFinDealPledge(TrdOutRightRepoTrade002Dto entity, Long branch) {
		FinDealPledge finDealPledge = new FinDealPledge();
		finDealPledge.setFinprodId(entity.getTradeId());
		finDealPledge.setBranch(branch);
		finDealPledge.setPledgeFinprodId(entity.getFinprodId());
		finDealPledge.setPledgeFaceValue(new BigDecimal(100));
		finDealPledge.setPledgeNumber(ArithmeticUtil.divide(entity.getSecTotalPrin(), 100, 2));
		finDealPledge.setPledgeRatio(ArithmeticUtil.divide(entity.getfCprice(), 100, 8));
		finDealPledge.setPledgeAmt(ArithmeticUtil.multiply(entity.getSecTotalPrin(), finDealPledge.getPledgeRatio()));

		return finDealPledge;
	}

	/**
	 * 根据交易编号删除质押品信息
	 * 
	 * @param tradeId 交易编号
	 * @return Db
	 */
	private void deleteFinDealPledgeByTradeId(String tradeId) {
		restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
				+ "/TrdOutRightRepoTrade/deleteFinDealPledgeByTradeId?tradeId=" + tradeId, Integer.class);
	}

	/**
	 * 生成交易类金融产品
	 *
	 * @param entity       买断式回购信息
	 * @param branch       分支序号
	 * @param finprodType2 投管分类
	 * @return 交易类金融产品
	 */
	private FinTradeProduct genFinTradeProduct(TrdOutRightRepoTrade002Dto entity, Long branch, String finprodType2) {
		FinTradeProduct finTradeProduct = new FinTradeProduct();
		finTradeProduct.setFinprodId(entity.getTradeId());
		finTradeProduct.setBranch(branch);
		finTradeProduct.setFinprodType2(finprodType2);
		finTradeProduct.setPrin(entity.getfSettleAmt());
		finTradeProduct.setCcy(entity.getCcy());
		finTradeProduct.setVdate(entity.getVdate());
		finTradeProduct.setMdate(entity.getMdate());
		finTradeProduct.setTerm(entity.getRepoterm());
		finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
		finTradeProduct.setIntRate(ArithmeticUtil.divide(entity.getRepoRate(), 100, 8));
		finTradeProduct.setBasis(entity.getBasis());
		finTradeProduct.setmUnitCprice(entity.getmCprice());
		finTradeProduct.setmUnitInt(entity.getmIntAmt());
		finTradeProduct.setmUnitFprice(entity.getmFprice());
		finTradeProduct.setmYield(ArithmeticUtil.divide(entity.getmYield(), 100, 8));
		finTradeProduct.setmPrinAmt(entity.getfSettleAmt());
		finTradeProduct.setmIntAmt(ArithmeticUtil.subtract(entity.getmSettleAmt(), entity.getfSettleAmt()));// 到期结算金额
																											// -
																											// 首次结算金额
		finTradeProduct.setmTradeAmt(entity.getmSettleAmt());
		// 本金收益特征 = 逆回购：保本，正回购不存
		if (DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getPs())) {
			// ※若画面联动1.7查询出的组合为保本
			finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
		}
		finTradeProduct.setTradeMarket(entity.getTradeMarket());
		finTradeProduct.setCalendarId(entity.getDealCalId());
		finTradeProduct.setTermType(entity.getTermType());
		finTradeProduct.setmDeliveryType(entity.getmSettleMethod());
		if (entity.getTrdOutRightRepoPersonInfo() != null) {
			finTradeProduct.setCounterId(entity.getTrdOutRightRepoPersonInfo().getCounterId());
		}

		return finTradeProduct;
	}

	/**
	 * 根据交易编号删除交易类金融产品
	 * 
	 * @param tradeId 交易编号
	 * @return Db
	 */
	private void deleteFinTradeProductByTradeId(String tradeId) {
		restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
				+ "/TrdOutRightRepoTrade/deleteFinTradeProductByTradeId?tradeId=" + tradeId, Integer.class);
	}

	// add by wangguoxian 费用设置 20190801 start
	/**
	 * 计算交易费用，结算费，佣金
	 * 
	 * @param dto
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdOutRightRepoTrade002Dto calFee(TrdOutRightRepoTrade002Dto dto) {
		List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
		TrdOutRightRepoTrade002Dto resultDto = new TrdOutRightRepoTrade002Dto();
		BasicTradeFeeInputDto inputDto = new BasicTradeFeeInputDto();
		inputDto.setTradeDate(dto.getTradeDate());
		inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_买断式回购.getValue());
		inputDto.setPs(dto.getPs());
		inputDto.setFpriceAmt(dto.getfSettleAmt());
		inputDto.setPrinAmt(ArithmeticUtil.multiply(dto.getSecTotalPrin(), new BigDecimal(10000)));
		String tradeMarketBank = BussinessDictCodeList.交易场所_银行间.getValue();
		String tradeMarket = dto.getTradeMarket();
		List<String> feeTypeList = new ArrayList<>();
		// 银行间费用设置
		if (StringUtil.equals(tradeMarketBank, tradeMarket)) {
			inputDto.setTradeMarket(tradeMarketBank);
			feeTypeList.add(BussinessDictCodeList.费用类型_交易手续费.getValue());
			feeTypeList.add(BussinessDictCodeList.费用类型_结算手续费.getValue());
			inputDto.setFeeTypeList(feeTypeList);
			// 登记托管机构用证券托管户的登记托管机构
			String registOrg = restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
					+ "/TrdBondInvestTrade/getRegistOrg?secAcctId=" + dto.getSecAcctId(), String.class);
			inputDto.setRegistOrg(registOrg);
			inputDto.setTermType(dto.getTermType());
			inputDto.setIsGrade(BussinessDictCodeList.是否_否.getValue());
			tradeFeeList = baseTradeFeeCommService.innerBankFeeDeal(inputDto);
		}
		// 交易所费用设置
		else {
			inputDto.setTradeMarket(tradeMarket);
			feeTypeList.add(BussinessDictCodeList.费用类型_佣金.getValue());
			inputDto.setFeeTypeList(feeTypeList);
			inputDto.setSecAcctId(dto.getSecAcctId());
			tradeFeeList = baseTradeFeeCommService.bourseFeeDeal(inputDto);
		}
		resultDto.setTradeFeeList(tradeFeeList);
		return resultDto;
	}

	/**
	 * 设置交易费用明细
	 * 
	 * @param entity 查询参数明细
	 * @return 费用明细列表
	 * @see
	 * @since 1.0
	 */
	private List<TrdTradeFeeDetail> setTrdFeeDetail(TrdOutRightRepoTrade002Dto entity) {
		List<TrdTradeFeeDetail> tradeFeeList = new ArrayList<>();
		if (entity.getSettleAmt() != null) {
			TrdTradeFeeDetail settleAmtDetail = getTrdTradeFeeDetail(entity,
					BussinessDictCodeList.费用类型_结算手续费.getValue());
			tradeFeeList.add(settleAmtDetail);
		}
		if (entity.getProcedureAmt() != null) {
			TrdTradeFeeDetail procedureAmtDetail = getTrdTradeFeeDetail(entity,
					BussinessDictCodeList.费用类型_交易手续费.getValue());
			tradeFeeList.add(procedureAmtDetail);
		}
		/*
		 * if (entity.getCommissionAmt() != null) { TrdTradeFeeDetail
		 * commissionAmtDetail = getTrdTradeFeeDetail(entity,
		 * BussinessDictCodeList.费用类型_佣金.getValue());
		 * tradeFeeList.add(commissionAmtDetail); }
		 */
		return tradeFeeList;

	}

	/**
	 * 获取交易费用详细
	 * 
	 * @param entity  画面输入实体
	 * @param feeType 费用类型
	 * @return 交易费用详细
	 */
	private TrdTradeFeeDetail getTrdTradeFeeDetail(TrdOutRightRepoTrade002Dto entity, String feeType) {
		TrdTradeFeeDetail amtDetail = new TrdTradeFeeDetail();
		/*
		 * if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_佣金.getValue())) {
		 * amtDetail.setFeeAmt(entity.getCommissionAmt());
		 * amtDetail.setFeeRate(entity.getCommissionAmtRate()); }else
		 */
		if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_交易手续费.getValue())) {
			amtDetail.setFeeAmt(entity.getProcedureAmt());
			amtDetail.setFeeRate(entity.getProcedureAmtRate());
		} else if (StringUtil.equals(feeType, BussinessDictCodeList.费用类型_结算手续费.getValue())) {
			amtDetail.setFeeAmt(entity.getSettleAmt());
			amtDetail.setFeeRate(entity.getSettleAmtRate());
		}
		amtDetail.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
		amtDetail.setFeeType(feeType);
		amtDetail.setCcy(entity.getCcy());
		amtDetail.setTradeId(entity.getTradeId());
		amtDetail.setCreateDept(entity.getCreateDept());
		amtDetail.setCreateTime(entity.getCreateTime());
		amtDetail.setCreateUser(entity.getCreateUser());
		amtDetail.setStatus(entity.getStatus());
		amtDetail.setUpdateTime(entity.getUpdateTime());
		amtDetail.setUpdateUser(entity.getUpdateUser());
		amtDetail.setWorkflowId(entity.getWorkflowId());
		return amtDetail;
	}
	// add by wangguoxian 费用设置 20190801 e

	@Override
	public void doBack(TrdOutRightRepoTrade002Dto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
		
		//删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}

	@Override
	public void doRevoke(TrdOutRightRepoTrade002Dto entity) {
		AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTradeId());
		
		//删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}

	/**
	 * 根据委托代码获取首次净价区间
	 * 
	 * @param entrustId
	 * @return
	 */
	public TrdOutRightRepoTrade002Dto getfCpriceBtw(String entrustId) {
		return restTemplate.getForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/getfCpriceBtw?entrustId=" + entrustId,
				TrdOutRightRepoTrade002Dto.class);
	}

	public BigDecimal calRepoRate(Map<String, Object> param) {
		BigDecimal R = BigDecimal.ZERO;
		TrdOutRightRepoTrade002Dto trdOutRightRepoTrade002Dto = new TrdOutRightRepoTrade002Dto();
		try {
			trdOutRightRepoTrade002Dto = (TrdOutRightRepoTrade002Dto) JsonUtils.map2entity(param,
					TrdOutRightRepoTrade002Dto.class);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		List<FinCashPlan> list = getFinCashPlanForRepoRate(param);
		if (trdOutRightRepoTrade002Dto.getmSettleAmt() != null && trdOutRightRepoTrade002Dto.getfSettleAmt() != null) {
			// 未发生中间付息
			if (list == null || list.size() == 0) {
				BigDecimal r1 = ArithmeticUtil.divide(trdOutRightRepoTrade002Dto.getmSettleAmt(),
						trdOutRightRepoTrade002Dto.getfSettleAmt(), 12);
				BigDecimal r2 = ArithmeticUtil.subtract(r1, new BigDecimal(1));
				BigDecimal r3 = ArithmeticUtil.divide(new BigDecimal(trdOutRightRepoTrade002Dto.getRepoterm()),
						new BigDecimal(365), 12);
				R = ArithmeticUtil.divide(r2, r3, 8);
			}
			// 已发生中间付息
			else {
				// 回购期间债券支付利息额
				BigDecimal sum1 = BigDecimal.ZERO;
				BigDecimal sum2 = BigDecimal.ZERO;
				for (FinCashPlan item : list) {
					Long d = DateUtil.getBetweenDayNumber(item.getPayDate(), trdOutRightRepoTrade002Dto.getMdate());
					sum2 = ArithmeticUtil.doAdd(
							ArithmeticUtil.multiply(ArithmeticUtil.divide(new BigDecimal(d), new BigDecimal(365), 12),
									item.getCashAmt()),
							sum2);
					sum1 = ArithmeticUtil.doAdd(item.getCashAmt(), sum1);
				}
				BigDecimal 分子 = ArithmeticUtil.doAdd(
						ArithmeticUtil.subtract(trdOutRightRepoTrade002Dto.getmSettleAmt(),
								trdOutRightRepoTrade002Dto.getfSettleAmt()),
						ArithmeticUtil.multiply(ArithmeticUtil.divide(trdOutRightRepoTrade002Dto.getSecTotalPrin(),
								new BigDecimal(100), 12), sum1));
				BigDecimal 分母 = ArithmeticUtil.subtract(
						ArithmeticUtil.multiply(trdOutRightRepoTrade002Dto.getfSettleAmt(),
								ArithmeticUtil.divide(new BigDecimal(trdOutRightRepoTrade002Dto.getRepoterm()),
										new BigDecimal(365), 12)),
						ArithmeticUtil.multiply(ArithmeticUtil.divide(trdOutRightRepoTrade002Dto.getSecTotalPrin(),
								new BigDecimal(100), 12), sum2));
				R = ArithmeticUtil.divide(分子, 分母, 8);
			}
		}
		return ArithmeticUtil.multiply(R, new BigDecimal(100));
	}

	public List<FinCashPlan> getFinCashPlanForRepoRate(Map<String, Object> param) {
		return restTemplate.postForList(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/getFinCashPlanForRepoRate", param,
				FinCashPlan.class);
	}

	@Override
	public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
		Map<String, Object> variables = new HashMap<>();
		TrdOutRightRepoTrade002Dto dto = this.getBussinessEntity(entity);
		if (taskId != null) {
			try {
//				return commonBusinessService.是否需要上级审批(dto.getTrdOutRightRepoPersonInfo().getCounterId(),
//						dto.getfSettleAmt(), LoginUserUtil.getUserName(), DictCodeList.平安资产类型_买断式回购.getValue(), null,
//						dto.getTradeMarket(), dto.getPs()
//						// 资产类型改为授权类型 add by renping 2019-08-06 start
//						, null
//				// 资产类型改为授权类型 add by renping 2019-08-06 end
//				);
                Map<String, Object> params = CommonBusinessService.setParams(dto.getTrdOutRightRepoPersonInfo().getCounterId(), dto.getfSettleAmt(), 
                    LoginUserUtil.getUserName(),DictCodeList.平安资产类型_买断式回购.getValue(), null, dto.getTradeMarket(), dto.getPs()
                    , null ,entity.getWorkflowId());
				params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批(params);
			}catch (WorkFlowException e) {
				// 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
				throw new WorkFlowException(e);
			}
		} else {
			return null;
		}
//        return null;
	}

	/* add by msk 新增查询通道划款交易 20201111 start */
	/**
	 * 查找通道划款交易
	 *
	 * @param map 交易编号
	 * @return 通道划款交易信息
	 * @see
	 * @since 1.0
	 */
	public int selectchlTrade(Map<String,Object> map) {
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/selectchlTrade/", map,
				Integer.class);
	}
	/* add by msk 新增查询通道划款交易 20201111 end */
}
