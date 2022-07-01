/*
 * 文件名：TrdForwexInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：qiaominghui
 * 修改时间：2019年7月2日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.forwex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.constant.BussinessJixuConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.forwex.dto.TrdForwexInvestTradeDto;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 外汇远期投资交易 〈功能详细描述〉
 * 
 * @author qiaominghui
 * @version 1.0 2019年7月4日
 * @see TrdForwexInvestTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdForwexInvestTrade002Service extends BaseService<TrdForwexInvestTradeDto>
		implements BaseProcessService<TrdForwexInvestTradeDto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(TrdForwexInvestTrade002Service.class);
	/**
	 * 附件上传类
	 */
	@Autowired(required = false)
	private FileAttachmentProcess fileAttachmentProcess;

	/**
	 * BaseTradeProductDealCommService
	 */
	@Autowired(required = false)
	private BaseTradeProductDealCommService baseTradeProductDealCommService;

	/**
	 * MultiApproveTradeCheckService
	 */
	@Autowired(required = false)
	private MultiApproveTradeCheckService multiApproveTradeCheckService;

	/**
	 * 共通Mapper的通用代理方案
	 */
	@Autowired
	private BussCommonMapperDelegateService bussCommonMapperDelegateService;

	/**
	 * 风险试算组件Service
	 */
	@Autowired(required = false)
	private BasRiskMathCommService basRiskMathCommService;

	/**
	 * DateCalService
	 */
	@Autowired
	private DateCalService dateCalService;

	/**
	 * 共通组件
	 */
	@Autowired
	private CommonBusinessService commonBusinessService;

	/**
	 * 划款指令
	 */
	@Autowired(required = false)
	private BussTrdTransferOrderService bussTrdTransferOrderService;

	/**
	 * 估值接口
	 */
	@Autowired
	private ValuationInterfService valuationInterfService;

	@Autowired
	private PtlTradeMapper ptlTradeMapper;

	@Autowired
	private FinTradeProductMapper finTradeProductMapper;

	@Autowired
	private PaTrdProductDealMapper paTrdProductDealMapper;

	@Autowired
	private FinProductTypeMapper finProductTypeMapper;

	@Autowired
	private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

	@Autowired
	private StlFinProductAcctMapper stlFinProductAcctMapper;

	@Autowired
	private WflTrdProductDealMapper wflTrdProductDealMapper;

	@Autowired
	private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

	@Autowired
	private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

	@Autowired
	private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

	/**
	 * 投管接口共通组件
	 */
	@Autowired
	private InvestInterfCommService investInterfCommService;
	
	@Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

	/**
	 * 交易及存续期交易，保存进待办，点批量审批时候需要报错。 {@inheritDoc}
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@DataSource(DataSourceType.MASERT)
	public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
		if (isMutiApproval) {
			// 业务编号
			String tradeId = entity.getTradeId();
			if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
				return getMessage("BUSS_COM_E_0100", tradeId);
			}
		}
		return null;
	}

	/**
	 * 新建 {@inheritDoc}
	 */
	@Override
	public void doInsert(TrdForwexInvestTradeDto entity, OperationType operationType) {
		// 新建交易
		if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
			this.handleBussFlowData(entity);
		}
		// 撤销交易
		else {
			this.doRevokeInsert(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doUpdate(TrdForwexInvestTradeDto entity) {
		if (!StringUtil.equals("revoke", entity.getOperateType())
				&& !StringUtil.equals("delete", entity.getOperateType())) {
			// 先删除流程中数据
			this.doDelete(entity.getWorkflowId());

			// 重新insert
			this.handleBussFlowData(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doDelete(String workflowId) {
		baseTradeProductDealCommService.delete(workflowId);
		
//		//根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
	}

	/**
	 * 流程完成 {@inheritDoc}
	 */
	@Override
	public void doComplete(TrdForwexInvestTradeDto entity, OperationType operationType) {

		// 设置母组合代码和子组合代码--范双虎
		entity.setActPortfolioId(entity.getPortfolioId());
		entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

		this.prepareEntity(entity);
		// 到期交易entity
		TrdForwexInvestTradeDto entity2 = null;
		if (!StringUtil.equals(DictCodeListExt2.结算方式_差额清算.getValue(), entity.getDeliveryType())) {
			entity2 = restTemplate.postForObject(
					ServiceApplication.PERIOD_SERVICE_NAME + "/TrdExchangeDuration/doCreateEndDeal", entity,
					TrdForwexInvestTradeDto.class);
			entity2.setActPortfolioId(entity.getActPortfolioId());
		}

		if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
			entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
			List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
			if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
				for (StlPayOrdersDto item : entity.getBussTrdTransferOrder()) {
					StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
					BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
					if (entity2 != null) {
						stlPayOrdersDto.setTradeId(entity2.getTrdProductDeal().getTradeId());
					} else {
						stlPayOrdersDto.setTradeId(entity.getTrdProductDeal().getTradeId());
					}
					list.add(stlPayOrdersDto);
				}
			}
			if (entity2 != null) {
				entity2.setStlPayOrdersList(list);
				entity.setStlPayOrdersList(null);
			} else {
				entity.setStlPayOrdersList(list);
			}
		}

		// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

		// 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

		// 金融产品交易扩展表Entity -> 金融产品交易扩展表
		PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());

		if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
			// 交易类金融产品
			FinTradeProductDto finTradeProductDto = entity.getFinTradeProduct();
			// 组合交易信息表
			PtlTrade ptlTrade = entity.getPtlTrade();
			// 组合交易信息表
			PtlTrade actPtlTrade = new PtlTrade();
			BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, entity.getPtlTrade());
			// 母组合金融产品交易
			actPtlTrade.setPortfolioId(entity.getPortfolioId());
			// 批量插入
			// 金融产品账户信息 本方账户
			StlFinProductAcct stlFinProductAcct = entity.getStlFinProductAcct();
			// 金融产品账户信息 对手方账户
			StlFinProductAcct stlFinProductAcctCor = entity.getStlFinProductAcctCor();
			// 费用明细
			TrdTradeFeeDetail trdTradeFeeDetail = entity.getTrdTradeFeeDetail();
			// 金融产品分类
			FinProductType finProductType = entity.getFinProductType();

			// add by wangpeng 获取估值分类123 start
			if (!StringUtil.equals(DictCodeList.交易方向_到期.getValue(), trdProductDeal.getPs())) {
				FinTradeProduct finTradeProduct = new FinTradeProduct();
				BeanUtil.copyPropertiesIgnoreNull(finTradeProduct, finTradeProductDto);
				ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
			}
			// add by wangpeng end
			paTrdProductDealMapper.insertSelective(paTrdProductDeal);
			List<PtlTrade> ptlTradeList = new ArrayList<>();
			ptlTradeList.add(ptlTrade);
			// 调用转换
			if (StringUtil.equals(DictCodeList.交易方向_到期.getValue(), trdProductDeal.getPs())) {
				if (StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
					// 批量插入
					ptlTradeMapper.insertSelective(ptlTrade);
				} else {
					ptlTradeMapper.insertSelective(ptlTrade);
					ptlTradeMapper.insertSelective(actPtlTrade);
					ptlTradeList.add(actPtlTrade);
				}
			} else {
				if (StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
					ptlTradeMapper.insertSelective(ptlTrade);
				} else {
					ptlTradeMapper.insertSelective(ptlTrade);
					ptlTradeMapper.insertSelective(actPtlTrade);
					ptlTradeList.add(actPtlTrade);
				}

				finTradeProductMapper.insertSelective(finTradeProductDto);
				finProductTypeMapper.insertSelective(finProductType);
				trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
				if (stlFinProductAcct != null) {
					stlFinProductAcctMapper.insertSelective(stlFinProductAcct);
				}
				if (stlFinProductAcctCor != null) {
					stlFinProductAcctMapper.insertSelective(stlFinProductAcctCor);
				}
			}

			FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
			finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
			BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
			baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
			baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
			// add by wangpeng 20190903 到期时 页面生成划款指令未入库 导致现金流水未更新
			if (entity.getStlPayOrdersList() != null && !entity.getStlPayOrdersList().isEmpty()) {// 划款指令信息
				baseTradeProductDealDto.setStlPayOrdersList(entity.getStlPayOrdersList());
			}
			// add by wangpeng 20190903 end

			finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

			// 撤销时，子组合需要调估值共通
			if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
					finProductTrade.setActPortfolioId(entity.getActPortfolioId());
				});
			}
			// 调用业务组件.【金融产品交易共通.交易产品接口】,写入交易基本信息与[实际]的组合持仓流水及现金流水
			baseTradeProductDealCommService.execute(finProductTradeDto);

			// 子组合插入流水
			if (StringUtil.isNotEmpty(entity.getActPortfolioId())
					&& !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getActPortfolioId()));
				baseTradeProductDealCommService.insertPosition(finProductTradeDto);
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getPortfolioId()));
			}
			// 非差额清算时直接生成到期交易
			if (entity2 != null) {
				TrdProductDeal trdProductDeal2 = new TrdProductDeal();
				BeanUtil.copyPropertiesIgnoreNull(trdProductDeal2, entity2.getTrdProductDeal());
				trdProductDeal2.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

				// 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
				TrdProductDealAdd trdProductDealAdd2 = new TrdProductDealAdd();
				BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd2, entity2.getTrdProductDealAdd());

				// 金融产品交易扩展表Entity -> 金融产品交易扩展表
				paTrdProductDeal = new PaTrdProductDeal();
				BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity2.getPaTrdProductDeal());
				// 组合交易信息表
				ptlTrade = entity2.getPtlTrade();
				// 组合交易信息表
				actPtlTrade = new PtlTrade();
				BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
				// 母组合金融产品交易
				actPtlTrade.setPortfolioId(entity2.getActPortfolioId());
				// 调用转换
				paTrdProductDealMapper.insertSelective(paTrdProductDeal);
				if (StringUtil.equals(entity2.getPortfolioId(), entity2.getActPortfolioId())) {
					ptlTradeMapper.insertSelective(ptlTrade);
				} else {
					ptlTradeMapper.insertSelective(ptlTrade);
					ptlTradeMapper.insertSelective(actPtlTrade);
				}
				finProductTradeDto = new FinProductTradeDto();
				finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
				baseTradeProductDealDto = new BaseTradeProductDealDto();
				baseTradeProductDealDto.setTrdProductDeal(trdProductDeal2); // 金融产品交易
				baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd2); // 金融产品附属
				if (entity2.getStlPayOrdersList() != null && !entity2.getStlPayOrdersList().isEmpty()) {// 划款指令信息
					baseTradeProductDealDto.setStlPayOrdersList(entity2.getStlPayOrdersList());
				}

				finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

				// 撤销时，子组合需要调估值共通
				if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
						finProductTrade.setActPortfolioId(entity.getActPortfolioId());
					});
				}
				// 调用业务组件.【金融产品交易共通.交易产品接口】,写入交易基本信息与[实际]的组合持仓流水及现金流水
				baseTradeProductDealCommService.execute(finProductTradeDto);

				// 子组合插入流水
				if (StringUtil.isNotEmpty(entity.getActPortfolioId())
						&& !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos()
							.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
									.setPortfolioId(entity.getActPortfolioId()));
					baseTradeProductDealCommService.insertPosition(finProductTradeDto);
				}
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

			// // 7.3写入现金流相关信息表
			// this.cashFlowLogic(entity);

			// 插入风险试算结果信息
			//basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());

			// 所有交易生效后调用的共通方法
			AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
			inputDto.setProdid(entity.getPortfolioId());
			inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
			inputDto.setTrdProductDeal(trdProductDeal);
			if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
				inputDto.setActPortfolioId(entity.getActPortfolioId());
			}
			inputDto.setPtlTradeList(ptlTradeList);
			if (entity.getStlPayOrdersList() != null && !entity.getStlPayOrdersList().isEmpty()) {
				inputDto.setStlPayOrders(entity.getStlPayOrdersList().get(0));
			}
			AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

//			// jixu接口 start
//			List<StlFinProductAcct> stlFinProductAcctList = new ArrayList<>();
//			stlFinProductAcctList.add(stlFinProductAcct);
//			stlFinProductAcctList.add(stlFinProductAcctCor);
			List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
			if (entity.getStlPayOrdersList() != null) {
				stlPayOrdersList.addAll(entity.getStlPayOrdersList());
			}
//			TrdCommDto trdCommDto = new TrdCommDto();
//			trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//			trdCommDto.setStlFinProductAcctList(stlFinProductAcctList);
//			trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//			trdCommDto.setTrdProductDeal(trdProductDeal);
//			trdCommDto.setTrdProductDealAdd(trdProductDealAdd);
//			trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
//
//			trdCommDto.setHeader(BussinessJixuConstant.消息头_外汇远期开仓交易);
//			trdCommDto.setPtlTradeList(ptlTradeList);
//			if (StringUtil.equals(DictCodeList.交易方向_到期.getValue(), trdProductDeal.getPs())) {
//				trdCommDto.setHeader(BussinessJixuConstant.消息头_远期换汇轧差交割);
//			}
//			if (((StringUtil.equals(DictCodeList.交易方向_到期.getValue(), trdProductDeal.getPs()))
//					&& (StringUtil.equals(trdProductDeal.getDeliveryType(), DictCodeListExt2.结算方式_差额清算.getValue())))
//					|| (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), trdProductDeal.getPs()))) {
//				valuationInterfService.runValInvest2Jixu(trdCommDto);
//			}
//			// jixu接口 end
			// 调用投管接口 add by wqd 20190212 start
			if (entity != null) {
				InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
				if (StringUtil.equals(DictCodeList.交易方向_到期.getValue(), entity.getTrdProductDeal().getPs())) {
					investInterfCommDto.setPageId("TrdForwexInvestTrade002.01");
				} else {
					investInterfCommDto.setPageId("TrdForwexInvestTrade002.02");
				}
				investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
				investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
				investInterfCommDto.setTrdProductDeal(entity.getTrdProductDeal());
				investInterfCommDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
				investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
				investInterfCommDto.setTrdTradeFeeDetail(entity.getTrdTradeFeeDetail());
				investInterfCommDto.setOtherTypeForConvert(entity.getFinTradeProduct()==null?"":entity.getFinTradeProduct().getExcPs());
				investInterfCommService.doInvestInterf(investInterfCommDto);
			}
			if (entity2 != null) {
				InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
				investInterfCommDto.setPageId("TrdForwexInvestTrade002.02");
				investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
				investInterfCommDto.setPortfolioId(entity2.getActPortfolioId());
				investInterfCommDto.setTrdProductDeal(entity2.getTrdProductDeal());
				investInterfCommDto.setTrdProductDealAdd(entity2.getTrdProductDealAdd());
				investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
				investInterfCommDto.setOtherTypeForConvert(entity.getFinTradeProduct()==null?"":entity.getFinTradeProduct().getExcPs());
				investInterfCommService.doInvestInterf(investInterfCommDto);
			}
		}
		// 撤销
		else {
			this.doCompleteRevoke(entity);
		}
		// 调用接口 add by zhangpuyu 2019/12/6 start
		try {
			List<String> tradeIds = new ArrayList<>();
			tradeIds.add(entity.getTradeId());
			restTemplate.postForObject(
					ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo",
					tradeIds, Map.class);
		} catch (Exception e) {

		}
		// 调用接口 add by zhangpuyu 2019/12/6 end
	}

	/**
	 * 撤销doComplete
	 *
	 * @param entity 参数
	 * @see
	 * @since 1.0
	 */
	private void doCompleteRevoke(TrdForwexInvestTradeDto entity) {
		// 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
		List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate
				.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFxswapInvestTrade001/getFinDto?tradeId="
						+ entity.getTradeId(), BaseTradeProductDealDto.class);
		// restTemplate.getForList(
		// ServiceApplication.TRADE_SERVICE_NAME +
		// "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
		// BaseTradeProductDealDto.class);

		if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
			entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
			List<StlPayOrdersDto> list = new ArrayList<StlPayOrdersDto>();
			if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
				for (StlPayOrdersDto item : entity.getBussTrdTransferOrder()) {
					StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
					BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
					list.add(stlPayOrdersDto);
				}
			}
			entity.setStlPayOrdersList(list);
		}
		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		if (!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
			for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
				baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
				baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
				baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());

				if (bussCommonMapperDelegateService != null) {
					PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
							PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(),
							PaTrdProductDeal.class);
					paTrdProductDeal.setTradeId(
							paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
					paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
					paTrdProductDeal.setCreateTime(new Date());
					paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
					paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
					paTrdProductDealMapper.insertSelective(paTrdProductDeal);
				}

//				// jixu 接口 add by qmh start
				List<StlPayOrders> stlPayOrdersList = new ArrayList<StlPayOrders>();
				if (entity.getStlPayOrdersList() != null) {
					stlPayOrdersList.addAll(entity.getStlPayOrdersList());
				}
//				TrdCommDto trdCommDto = new TrdCommDto();
//				trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//				// trdCommDto.setStlFinProductAcctList(stlFinProductAcctList);
//				trdCommDto.setStlPayOrdersList(stlPayOrdersList);
//				trdCommDto.setTrdProductDeal(baseDto.getTrdProductDeal());
//				trdCommDto.setPtlTradeList(baseDto.getPtlTrade());
//				trdCommDto.setTrdProductDealAdd(baseDto.getTrdProductDealAdd());
//				trdCommDto.setHeader(BussinessJixuConstant.消息头_外汇远期开仓交易);
//				if (StringUtil.equals(DictCodeList.交易方向_到期.getValue(), baseDto.getTrdProductDeal().getPs())) {
//					trdCommDto.setHeader(BussinessJixuConstant.消息头_远期换汇轧差交割);
//				}
//				trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);
//				// valuationInterfService.runValInvest2Jixu(trdCommDto);
//				if (((StringUtil.equals(DictCodeList.交易方向_到期.getValue(), baseDto.getTrdProductDeal().getPs()))
//						&& (StringUtil.equals(baseDto.getTrdProductDeal().getDeliveryType(),
//								DictCodeListExt2.结算方式_差额清算.getValue())))
//						|| (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), baseDto.getTrdProductDeal().getPs()))) {
//					valuationInterfService.runValInvest2Jixu(trdCommDto);
//				}
//				//jixu 接口 add by qmh end
				// 调用投管接口 add by wqd 20190212 start
				if (entity != null) {
					InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
					if (StringUtil.equals(DictCodeList.交易方向_到期.getValue(), entity.getTrdProductDeal().getPs())) {
						investInterfCommDto.setPageId("TrdForwexInvestTrade002.01");
					} else {
						investInterfCommDto.setPageId("TrdForwexInvestTrade002.02");
					}
					investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
					investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
					investInterfCommDto.setTrdProductDeal(entity.getTrdProductDeal());
					investInterfCommDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
					investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
					investInterfCommDto.setTrdTradeFeeDetail(entity.getTrdTradeFeeDetail());
					investInterfCommDto.setOtherTypeForConvert(entity.getFinTradeProduct()==null?"":entity.getFinTradeProduct().getExcPs());
					investInterfCommService.doInvestInterf(investInterfCommDto);
				}
				// 调用投管接口 add by wqd 20190212 end

				//删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
                }
			}
			finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
			finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

			// 撤销时，子组合需要调估值共通
			if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
					finProductTrade.setActPortfolioId(entity.getActPortfolioId());
				});
			}
			// 调用业务组件.【金融产品交易共通.交易产品接口】, insert
			// 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
			baseTradeProductDealCommService.execute(finProductTradeDto);

			// 子组合插入流水
			if (StringUtil.isNotEmpty(entity.getActPortfolioId())
					&& !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getActPortfolioId()));
				baseTradeProductDealCommService.insertPosition(finProductTradeDto);
				finProductTradeDto.getBaseTradeProductDealDtos()
						.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
								.setPortfolioId(entity.getPortfolioId()));
			}
		}

		// 插入风险试算结果
		//basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
		// 删除原业务流程记录
		this.deleteBussFlowData(entity.getWorkflowId());

		// 调用基础组件.【交易报送组件】删除交易报送信息
		restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
				Arrays.asList(entity.getTradeId()), Boolean.class);

		// // 一体化对应etliu start
		// // 所有交易生效后调用的共通方法
		// for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
		// AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
		// inputDto.setProdid(baseDto.getTrdProductDealAdd().getPortfolioId());
		// inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
		// inputDto.setTrdProductDeal(baseDto.getTrdProductDeal());
		// AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
		// }
		// // 一体化对应etliu end
	}

	/**
	 * 委托代码联动带出画面必要信息
	 * 
	 * @param entrustId 资产代码
	 * @return 外汇掉期实体类
	 * @see
	 * @since 1.0
	 */
	public TrdForwexInvestTradeDto getDealInfoByEntrustId(String entrustId) {
		return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
				+ "/TrdForwexInvestTrade/getDealInfoByEntrustId?entrustId=" + entrustId, TrdForwexInvestTradeDto.class);
	}

	/**
	 * 组合可用现金查询 <功能详细描述>
	 * 
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Map<String, Object> getProAvailableCash(Map<String, Object> param) {
		Date sysDate = DateUtil.platDateFromCache();
		param.put("vdate", DateUtil.format(sysDate));
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdForwexInvestTrade/getProAvailableCash", param, Map.class);
	}

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param tradeId
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdForwexInvestTradeDto getForwexInvestTradeDto(String tradeId) {
		// TODO Auto-generated method stub
		return restTemplate.postForObject(
				ServiceApplication.TRADE_SERVICE_NAME + "/TrdForwexInvestTrade/getTrdForwexInvestTradeDto/" + tradeId,
				null, TrdForwexInvestTradeDto.class);
	}

	/**
	 * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
	 *
	 * @param dto 画面输入的参数dto
	 * @see
	 * @since 1.0
	 */
	public void prepareEntity(TrdForwexInvestTradeDto dto) {

		// 流程中不会塞子组合代码，这里直接塞成组合代码--范双虎
		if (!StringUtil.isNotEmpty(dto.getActPortfolioId())) {
			dto.setActPortfolioId(dto.getPortfolioId());
		}

		// 金融产品交易Entity 构造开始----------------------------------------------------------
		TrdProductDeal entry = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(entry, dto);
		// 交易编号
		entry.setTradeId(dto.getTradeId());
		// 业务类型
		entry.setBusiType(DictCodeListExt2.共通业务类型_外汇远期投资交易.getValue());
		// 金融产品代码 （到期交易界面存放finprodid 为首期交易的交易编号 ，首期交易直接取用首期交易编号）
		entry.setFinprodId(StringUtil.isNotEmpty(dto.getFinprodId()) ? dto.getFinprodId() : entry.getTradeId());
		// 金融产品类型（投管分类）
		entry.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
		// 金融产品类型（估值核算）
		entry.setFinprodType(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
		// 投资目的
		entry.setInvAim(dto.getInvAim());
		// 分支序号
		entry.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		// 单位净价 = 画面。即期汇率
		entry.setUnitCprice(dto.getUnitCprice());
		// 单位利息 = 画面。远期点
		entry.setUnitInt(dto.getUnitInt());
		// 单位全价 = 画面。远期全价
		entry.setUnitFprice(dto.getUnitFprice());
		// 单位面值 画面.货币对如有 JPY,则存100；否则，存1
		if (dto.getCurPair() != null && dto.getCurPair().contains(DictCodeList.币种_日元_JPY.getValue())) {
			entry.setParValue(new BigDecimal(100));
		} else {
			entry.setParValue(BigDecimal.ONE);
		}
		// 币种 为对应币种
		entry.setCcy(dto.getCorCcy());
		// 交易方向 = 数据字典缓存.交易方向.买入 到期时为到期
		if (StringUtil.equals(DictCodeList.是否_是.getValue(), dto.getIsDifSquEx())) {
			// 到期
			entry.setPs(DictCodeList.交易方向_到期.getValue());
			// 交易日 = 交割日
			entry.setTradeDate(dto.getSettleDate());
			// 交割日 = 交割日
			entry.setSettleDate(dto.getSettleDate());
			// 交易金额 = 对应货币金额
			entry.setTradeAmt(dto.getFpriceAmt());
			// 前置交易编号
			entry.setbTradeId(dto.getFinprodId());
		} else {
			// 买入
			entry.setPs(DictCodeList.交易方向_买入.getValue());
			// 交割日 = 交易日
			entry.setSettleDate(dto.getTradeDate());
			// 交割日 = 交易日
			entry.setSettleDate(dto.getTradeDate());
		}

		// 交易本金 = 交易币种金额
		entry.setPrinAmt(dto.getPrinAmt());
		// 全价总额 = 对应币种金额
		entry.setFpriceAmt(dto.getFpriceAmt());
		// 总交易费用 = 画面。交易费用
		entry.setFeeAmt(dto.getFeeAmt());
		// 不随交易支付费用 = 交易费用
		entry.setFeeAmtUnpay(dto.getFeeAmt());
		// 结算模式 = 画面。清算模式
		entry.setDeliveryType(dto.getDeliveryType());
		// 是否撤销 = ‘否’
		entry.setIsCancel(DictCodeList.是否_否.getValue());
		// 是否结清
		entry.setIsClean(DictCodeList.是否_是.getValue());

		TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
		if (personInfo != null) {
			// 本方交易员
			entry.setTrader(personInfo.getTrader());
			// 交易对手
			entry.setCounterId(personInfo.getCounterId());
			// 是否外部交易
			entry.setIsOutTrade(personInfo.getIsOutTrade());
			// 对手方交易员
			entry.setCounterTrader(personInfo.getCounterTrader());
			// 联系方式
			entry.setCounterContact(personInfo.getContactInfo());
		}
		dto.setTrdProductDeal(entry);
		// 交易编号、融产品代码、金融产品类型（投管分类）、证券管理户代码、关联系统交易编号、投资目的、交易日、
		// 起息日、到期日、交割日、单位净价、单位利息、单位全价、净价总额、交易场所、交易市场、交易平台、交易币种、交易方向、利息总额、全价总额、备注
		// 以上信息为画面上项目，不需要特殊设置
		// 金融产品交易Entity 构造结束----------------------------------------------------------

		// 金融产品交易附属表Entity
		// 构造开始----------------------------------------------------------
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, dto);
		// 金融产品代码
		trdProductDealAdd.setMpFinprodId(entry.getFinprodId());// 是交易编号，不是标的代码
		// 生成方式
		trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		// 差额收付货币
		if (StringUtil.equals(DictCodeList.是否_是.getValue(), dto.getIsDifSquEx())) {
			trdProductDealAdd.setDifPayCcy(DictCodeList.币种_人民币_CNY.getValue());
		} else {
			trdProductDealAdd.setDifPayCcy(DictCodeList.币种_人民币_CNY.getValue());
		}
		dto.setTrdProductDealAdd(trdProductDealAdd);
		// 金融产品交易附属表Entity
		// 构造结束----------------------------------------------------------

		// 组合交易表Entity 构造开始----------------------------------------------------------
		PtlTrade ptlTrade = new PtlTrade();
		// 组合代码
		ptlTrade.setPortfolioId(dto.getPortfolioId());
		// 业务编号
		ptlTrade.setBusiNo(dto.getTradeId());
		// 业务表
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		// 业务明细代码 = 金融产品代码
		ptlTrade.setBusiId(entry.getFinprodId());
		// 业务类型
		ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_外汇远期投资交易.getValue());
		// 实际发生日
		ptlTrade.setSettleDate(dto.getTradeDate());
		// 组合代码塞成子组合代码
		ptlTrade.setPortfolioId(dto.getActPortfolioId());
		dto.setPtlTrade(ptlTrade);
		// 组合交易表Entity 构造结束----------------------------------------------------------

		// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
		PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
		// 交易编号
		paTrdProductDeal.setTradeId(dto.getTradeId());
		// 平安业务类型(数据字典缓存.平安业务类型（外汇远期买入）)
		// paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇远期买入.getValue());
		if (StringUtil.equals(DictCodeList.是否_是.getValue(), dto.getIsDifSquEx())) {
			paTrdProductDeal.setPaBussType(DictCodeListExt2.平安业务类型_外汇远期到期.getValue());
		}
		// 交易方向
		paTrdProductDeal.setBackLendDirection(dto.getPs());
		// 交易所属部门
		paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
		// 工作流代码
		paTrdProductDeal.setWorkflowId(dto.getWorkflowId());
		dto.setPaTrdProductDeal(paTrdProductDeal);
		// 金融产品交易扩展Entity 构造结束----------------------------------------------------------

		// 金融产品交易业务流程其他信息Entity
		// 构造开始----------------------------------------------------------
		WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
		// 交易编号 = 金融产品交易Entity.交易编号
		wflTrdProductDealOth.setTradeId(dto.getTradeId());
		// 交易货币 = 画面.交易货币
		wflTrdProductDealOth.setDealCur(dto.getCcy());
		// 期限品种 = 画面.期限品种
		wflTrdProductDealOth.setTermType(dto.getTermType());
		// 到期日 = 画面.交割日
		wflTrdProductDealOth.setfMdate(dto.getSettleDate());
		// 交易方向 = 画面.交易方向
		wflTrdProductDealOth.setExcPs(dto.getPs());
		// 标的金融产品代码
		wflTrdProductDealOth.setPledgeFinprodId(dto.getFinprodId());
		// 标的金融产品类型
		wflTrdProductDealOth.setPledgeFinprodType(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
		// 数据状态 = 数据字典缓存.数据状态.新建
		wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
		// 工作流代码
		wflTrdProductDealOth.setWorkflowId(dto.getWorkflowId());
		dto.setWflTrdProductDealOth(wflTrdProductDealOth);
		// 金融产品交易业务流程其他信息Entity
		// 构造结束----------------------------------------------------------

		// 差额清算 到期交易 不插入以下表
		if (!StringUtil.equals(DictCodeList.是否_是.getValue(), dto.getIsDifSquEx())) {

			// 交易费用明细表Entity 构造开始----------------------------------------------------------
			TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
			// 交易编号
			trdTradeFeeDetail.setTradeId(dto.getTradeId());
			// 费用类型 交易手续费
			trdTradeFeeDetail.setFeeType(DictCodeList.费用品种类型_交易手续费.getValue());
			// 是否随交易支付
			trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
			// 结售汇时，存 人民币；否则，画面.交易货币
			if (DictCodeListExt2.外汇交易模式_本行结售汇.getValue().equals(dto.getExcDealType())) {
				trdTradeFeeDetail.setCcy(DictCodeList.币种_人民币_CNY.getValue());
			} else {
				trdTradeFeeDetail.setCcy(dto.getCcy());
			}
			// 费用金额
			trdTradeFeeDetail.setFeeAmt(dto.getFeeAmt());
			dto.setTrdTradeFeeDetail(trdTradeFeeDetail);
			// 交易费用明细表Entity 构造结束----------------------------------------------------------

			// 交易类金融产品Entity 构造开始----------------------------------------------------
			FinTradeProductDto finTradeProduct = new FinTradeProductDto();
			// 金融产品代码 = 金融产品交易Entity.交易编号
			finTradeProduct.setFinprodId(dto.getTradeId());
			// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
			finTradeProduct
					.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
			// 金融产品类型 = 数据字典缓存.金融产品类型（投管用）数据字典.外汇远期投资交易
			finTradeProduct.setFinprodType2(dto.getFinprodType2());
			// 名义本金 = 交易币种金额
			finTradeProduct.setPrin(entry.getPrinAmt());
			// 名义币种 = 画面.币种
			finTradeProduct.setCcy(dto.getCcy());
			// 起息日 = 画面.交易日
			finTradeProduct.setVdate(dto.getTradeDate());
			// 到期日 = 画面.交割日
			finTradeProduct.setMdate(dto.getSettleDate());
			// 期限(天) = 画面.交割日-画面.交易日
			finTradeProduct.setTerm(DateUtil.getBetweenDayNumber(dto.getTradeDate(), dto.getSettleDate()));
			// 到期单位净价 = 画面.即期汇率
			finTradeProduct.setmUnitCprice(dto.getUnitCprice());
			// 到期单位利息 = 画面.远期点
			finTradeProduct.setmUnitInt(dto.getUnitInt());
			// 到期单位全价 = 画面.远期全价
			finTradeProduct.setmUnitFprice(dto.getUnitFprice());
			// 到期本金 = 画面.交易货币金额
			finTradeProduct.setmPrinAmt(dto.getPrinAmt());
			// 到期金额 = 画面.对应货币金额
			finTradeProduct.setmTradeAmt(dto.getFpriceAmt());
			// 交易日历 = 画面.交易日历
			finTradeProduct.setCalendarId(dto.getCalendarId());
			// 期限品种 = 画面.期限
			finTradeProduct.setTermType(dto.getTermType());
			// 交易对手 = 画面.交易对手
			if (dto.getTrdInvestPersonInfo() != null) {
				finTradeProduct.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
			}
			// 外汇交易方向 = 画面.交易方向
			finTradeProduct.setExcPs(dto.getPs());
			// 折美元金额
			finTradeProduct.setUsdAmt(dto.getUsdAmt());
			// 货币对 = 画面.货币对
			finTradeProduct.setCurPair(dto.getCurPair());
	        // 持有目的 中债新增 20200409
	 		finTradeProduct.setHoldPurpose(dto.getHoldPurpose());
			dto.setFinTradeProduct(finTradeProduct);
			// 交易类金融产品Entity 构造结束----------------------------------------------------------

			// 金融产品账户信息Entity构造开始--------------------------------------------------------
			if (StringUtil.isNotEmpty(dto.getDealAcct())) {
				StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
				// 金融产品代码
				stlFinProductAcct.setFinprodId(entry.getFinprodId());
				// 金融产品分类(投管)
				stlFinProductAcct.setFinprodType2(dto.getFinprodType2());
				// 产品账户
				stlFinProductAcct.setAcctNo(dto.getDealAcct());
				// 币种
				stlFinProductAcct.setCcy(dto.getCcy());
				//
				stlFinProductAcct.setAcctType(DictCodeListExt2.标的账户类型_本方账户.getValue());
				dto.setStlFinProductAcct(stlFinProductAcct);
			}
			if (StringUtil.isNotEmpty(dto.getCorAcct())) {
				StlFinProductAcct stlFinProductAcctCor = new StlFinProductAcct();
				// 金融产品代码
				stlFinProductAcctCor.setFinprodId(entry.getFinprodId());
				// 金融产品分类(投管)
				stlFinProductAcctCor.setFinprodType2(dto.getFinprodType2());
				// 产品账户
				stlFinProductAcctCor.setAcctNo(dto.getCorAcct());
				// 币种
				stlFinProductAcctCor.setCcy(dto.getCorCcy());
				//
				stlFinProductAcctCor.setAcctType(DictCodeListExt2.标的账户类型_对手方账户.getValue());
				dto.setStlFinProductAcctCor(stlFinProductAcctCor);
			}
			// 金融产品账户信息Entity构造结束---------------------------

			// 交易产品分类表Entity 构造开始----------------------------------------------------------
			FinProductType finProductType = new FinProductType();
			// 金融产品代码 交易编号、
			finProductType.setFinprodId(entry.getFinprodId());
			// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
			finProductType.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
			// 分类1
			finProductType.setType8(DictCodeListExt2.平安资产类型_外汇远期.getValue());
			/* 添加五级分类映射 lishuping 2019/08/13 start */
			FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
			finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
			finProductTypeConfig.setInputValue1(DictCodeListExt2.平安资产类型_外汇远期.getValue());
			finProductTypeConfig = restTemplate.postForObject(
					ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType", finProductTypeConfig,
					FinProductTypeConfig.class);
			finProductType.setType9(finProductTypeConfig.getOutputValue2());
			finProductType.setType10(finProductTypeConfig.getOutputValue3());
			finProductType.setType11(finProductTypeConfig.getOutputValue4());
			finProductType.setType12(finProductTypeConfig.getOutputValue5());
			/* 添加五级分类映射 lishuping 2019/08/13 end */
			dto.setFinProductType(finProductType);
			// 交易产品分类表Entity 构造结束----------------------------------------------------------

		}

	}

	/**
	 * 保存时登录业务流程表
	 *
	 * @param entity 画面参数dto
	 * @see
	 * @since 1.0
	 */
	private void handleBussFlowData(TrdForwexInvestTradeDto entity) {
		if (CollectionUtils.isNotEmpty(entity.getUploadFileList()) && StringUtil.isNotEmpty(entity.getTradeId())) {
			// 将附件和当前业务对象绑定（更新附件的业务代码）
			fileAttachmentProcess.updateBussinessNo(
					entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()),
					entity.getTradeId());
		}

		if (!StringUtil.equals("submit", entity.getOperateType())) {
			return;
		}

		if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
			entity.setBussTrdTransferOrder(bussTrdTransferOrderService.save(entity.getBussTrdTransferOrder()));
			List<StlPayOrdersDto> list = new ArrayList<>();
			for (StlPayOrdersDto item : entity.getBussTrdTransferOrder()) {
				StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
				BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, item);
				stlPayOrdersDto.setTradeId(entity.getTradeId());
				list.add(stlPayOrdersDto);
			}
			entity.setStlPayOrdersList(list);
		}

		this.prepareEntity(entity);

		// 调用授信共同 sjj start
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
		AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		inputDto.setTrdProductDeal(trdProductDeal);
		AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
		// 调用授信共同 sjj end

		// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
		WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
		wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

		// 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
		WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
		wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

		// 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
		WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
		wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
		// add by wangpeng 20190919 流程中 交易方向存入回购交易方向字段
		wflPaTrdProductDeal.setBackLendDirection(entity.getPs());
		// add end

		// 金融产品交易Entity -> 金融产品交易其它业务流程表
		WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
		BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealOth, entity.getWflTrdProductDealOth());
		wflTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

		// 批量插入
		// bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
		// wflTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd,
		// WflPaTrdProductDealMapper.class, wflPaTrdProductDeal,
		// WflTrdProductDealOthMapper.class, wflPaTrdProductDealOth);
		wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
		wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
		wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
		wflTrdProductDealOthMapper.insertSelective(wflTrdProductDealOth);

		// 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
		BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
		baseTradeProductDealDto.setTrdProductDeal(entity.getTrdProductDeal());
		baseTradeProductDealDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
		if (!StringUtil.isEmptyList(entity.getBussTrdTransferOrder())) {
			baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(entity.getBussTrdTransferOrder().get(0)));
		}
		finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

		// 撤销时，子组合需要调估值共通
		if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
			finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
				finProductTrade.setActPortfolioId(entity.getActPortfolioId());
			});
		}
		baseTradeProductDealCommService.execute(finProductTradeDto);

		// 设置母组合代码和子组合代码--范双虎
		entity.setActPortfolioId(entity.getPortfolioId());
		entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));

		// 子组合插入流水
		if (StringUtil.isNotEmpty(entity.getActPortfolioId())
				&& !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getActPortfolioId()));
			baseTradeProductDealCommService.insertPosition(finProductTradeDto);
			finProductTradeDto.getBaseTradeProductDealDtos()
					.forEach(baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd()
							.setPortfolioId(entity.getPortfolioId()));
		}
		// // 写入组合 持仓流水
		// this.ptlPredgePositionFlowLogic(entity, false);
	}

	/**
	 * 撤销交易doInsert
	 *
	 * @param entity 参数entity
	 * @see
	 * @since 1.0
	 */
	private void doRevokeInsert(TrdForwexInvestTradeDto entity) {
		// 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
		List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate
				.getForList(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
						+ entity.getTradeId(), BaseTradeProductDealDto.class);

		if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
			for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
				// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
				WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
				BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
				// 交易状态=“撤销”
				wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
				// 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
				wflTrdProductDeal.setpTradeId(entity.getTradeId());
				// 撤销说明=画面.撤销说明
				wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
				// 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
				wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
				wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
				wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

				// 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
				WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
				BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
				wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
				wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
				wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
				wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
				if (StringUtil.isNotEmpty(baseDto.getActPortfolioId())) {
					wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
				}

				wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
				wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
				// 批量插入
				// bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,
				// wflTrdProductDeal, WflTrdProductDealAddMapper.class, wflTrdProductDealAdd);
			}
		}
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
		// // 金融产品交易其它信息Entity -> 金融产品交易其它信息业务流程表
		// bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH",
		// "WORKFLOW_ID",
		// workflowId);
		baseTradeProductDealCommService.deleteWflData(workflowId);
	}

	/**
	 * 重新封装实体类数据 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @see
	 * @since 1.0
	 */
	public void convert2TrdForwexInvestTradeDto(TrdForwexInvestTradeDto dto) {
		//
		TrdProductDeal trdProductDeal = dto.getTrdProductDeal();
		TrdProductDealAdd trdProductDealAdd = dto.getTrdProductDealAdd();
		FinTradeProduct finTradeProduct = dto.getFinTradeProduct();

		// 金融产品代码
		dto.setFinprodId(finTradeProduct.getFinprodId());
		// 委托代码
		dto.setEntrustId(trdProductDealAdd.getEntrustId());
		// 组合id
		dto.setPortfolioId(trdProductDealAdd.getPortfolioId());
		// 交易代码
		dto.setTradeId(trdProductDeal.getTradeId());
		// 交易类型
		dto.setExcDealType(trdProductDealAdd.getExcDealType());
		// 货币对
		dto.setCurPair(trdProductDealAdd.getCurPair());
		// 交易方向
		dto.setPs(finTradeProduct.getExcPs());
		// 交易日历
		dto.setCalendarId(finTradeProduct.getCalendarId());
		// 交易日期
		dto.setTradeDate(finTradeProduct.getVdate());
		// 期限
		dto.setTermType(finTradeProduct.getTermType());
		// 交割日
		dto.setSettleDate(finTradeProduct.getMdate());
		// 我方角色
		dto.setOurRole(trdProductDealAdd.getOurRole());
		// 交易模式
		dto.setDealMode(trdProductDealAdd.getDealMode());
		// 清算模式
		dto.setDeliveryType(trdProductDeal.getDeliveryType());
		// 定价日
		dto.setPricedDate(trdProductDealAdd.getPricedDate());
		// 差额交割参考利率
		dto.setRefRate(trdProductDealAdd.getRefRate());
		// 投资目的
		dto.setInvAim(trdProductDeal.getInvAim());
		// 即期汇率
		dto.setUnitCprice(trdProductDeal.getUnitCprice());
		// 远期点
		dto.setUnitInt(trdProductDeal.getUnitInt());
		// 远期全价
		dto.setUnitFprice(trdProductDeal.getUnitFprice());
		// 定价日即期汇率
		dto.setPricedDateRate(trdProductDealAdd.getPricedDateRate());
		// 差额收付方向
		dto.setDifPs(trdProductDealAdd.getDifPs());
		// 差额收付金额
		dto.setDifPayAmt(trdProductDealAdd.getDifPayAmt());
		// 交易货币
		dto.setCcy(finTradeProduct.getCcy());
		// 交易货币金额
		dto.setPrinAmt(trdProductDeal.getPrinAmt());
		// 交易货币可用持仓
		// 对应货币
		dto.setCorCcy(trdProductDeal.getCcy());
		// 对应货币金额
		dto.setFpriceAmt(trdProductDeal.getFpriceAmt());
		// 折美元金额
		dto.setUsdAmt(trdProductDealAdd.getUsdAmt());
		// 交易费用
		dto.setFeeAmt(trdProductDeal.getFeeAmt());
		// // 本方账户
		// dto.setDealAcct(dto.getDealAcct());
		// // 对手方账户
		// dto.setCorAcct(dto.getCorAcct());

		// 人员信息
		TrdInvestPersonInfo002Dto trdInvestPersonInfo = new TrdInvestPersonInfo002Dto();
		// 交易员
		trdInvestPersonInfo.setTrader(trdProductDeal.getTrader());
		// 交易对手
		trdInvestPersonInfo.setCounterId(trdProductDeal.getCounterId());
		// 是否外部交易
		trdInvestPersonInfo.setIsOutTrade(trdProductDeal.getIsOutTrade());
		// 对手方交易员
		trdInvestPersonInfo.setCounterTrader(trdProductDeal.getCounterTrader());
		// 联系方式
		trdInvestPersonInfo.setContactInfo(trdProductDeal.getCounterContact());
		dto.setTrdInvestPersonInfo(trdInvestPersonInfo);
		// 是否差额清算,到期
		dto.setIsDifSquEx(DictCodeList.是否_是.getValue());
	}

	/**
	 * 将差额清算外汇远期交易转化成划款指令共通用的交易
	 * 
	 * @param dto 参数
	 * @return 两个币种的两笔交易数据
	 * @see
	 * @since 1.0
	 */
	public List<TrdProductDeal> getEntity(TrdForwexInvestTradeDto dto) {
		List<TrdProductDeal> resultList = new ArrayList<>();
		if (dto.getSettleDate() == null || dto.getPrinAmt() == null || dto.getFpriceAmt() == null
				|| StringUtil.isEmpty(dto.getPs()) || StringUtil.isEmpty(dto.getCcy())
				|| StringUtil.isEmpty(dto.getCorCcy())
				|| StringUtil.isEmpty(dto.getTrdInvestPersonInfo().getCounterId())) {
			return resultList;
		}
		String psCcy;
		String psCorCcy;
		if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), dto.getPs())) {
			if (dto.getCurPair().startsWith(dto.getCcy())) {
				psCcy = DictCodeList.交易方向_收取.getValue();
				psCorCcy = DictCodeList.交易方向_支付.getValue();
			} else {
				psCcy = DictCodeList.交易方向_支付.getValue();
				psCorCcy = DictCodeList.交易方向_收取.getValue();
			}
		} else {
			if (dto.getCurPair().startsWith(dto.getCcy())) {
				psCcy = DictCodeList.交易方向_支付.getValue();
				psCorCcy = DictCodeList.交易方向_收取.getValue();
			} else {
				psCcy = DictCodeList.交易方向_收取.getValue();
				psCorCcy = DictCodeList.交易方向_支付.getValue();
			}
		}
		// 非差额清算时直接生成到期交易的信息
		if (StringUtil.isNotEmpty(dto.getDeliveryType())
				&& !StringUtil.equals(DictCodeListExt2.结算方式_差额清算.getValue(), dto.getDeliveryType())) {
			TrdProductDeal trdProductDeal = new TrdProductDeal();
			trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
			trdProductDeal.setPs(psCcy);
			trdProductDeal.setTradeDate(dto.getSettleDate());
			trdProductDeal.setTradeAmt(dto.getPrinAmt());
			trdProductDeal.setCcy(dto.getCcy());
			trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
			resultList.add(trdProductDeal);
			trdProductDeal = new TrdProductDeal();
			trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
			trdProductDeal.setPs(psCorCcy);
			trdProductDeal.setTradeDate(dto.getSettleDate());
			trdProductDeal.setTradeAmt(dto.getFpriceAmt());
			trdProductDeal.setCcy(dto.getCorCcy());
			trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
			resultList.add(trdProductDeal);
			return resultList;
		}
		if (StringUtil.isEmpty(dto.getDifPs()) || dto.getDifPayAmt() == null) {
			return resultList;
		}

		TrdProductDeal trdProductDeal = new TrdProductDeal();
		trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
		trdProductDeal.setPs(dto.getDifPs());
		trdProductDeal.setTradeDate(dto.getSettleDate());
		trdProductDeal.setTradeAmt(dto.getDifPayAmt());
		trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
		resultList.add(trdProductDeal);
		return resultList;

	}

	/**
	 * 获取业务参数
	 * 
	 * @param entity 业务实体
	 * @param taskId 任务代码
	 * @return 参数
	 * @throws WorkFlowException 工作流异常
	 */
	@Override
	public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
		Map<String, Object> variables = new HashMap<>();
		TrdForwexInvestTradeDto dto = this.getBussinessEntity(entity);
		if (taskId != null) {
			try {
				BigDecimal prinAmt = null;
				if (dto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
					if (StringUtil.equals(dto.getCcy(), DictCodeList.币种_人民币_CNY.getValue())) {
						prinAmt = dto.getPrinAmt();
					} else {
						prinAmt = dto.getFpriceAmt();
					}
				} else {
					CommonBusinessService commonBusinessService = SpringContextUtil
							.getBean(CommonBusinessService.class);
					if (commonBusinessService != null) {
						prinAmt = commonBusinessService.calExchangeAmt(dto.getPrinAmt(),
								DateUtil.addDay(dto.getTradeDate(), -1), dto.getCcy(),
								DictCodeList.币种_人民币_CNY.getValue());
					}
				}
//				return commonBusinessService.是否需要上级审批(dto.getCounterId(), prinAmt, LoginUserUtil.getUserName(),
//				DictCodeListExt2.平安资产类型_外汇远期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
//				// 资产类型改为授权类型 add by renping 2019-08-06 start
//				, null
//		// 资产类型改为授权类型 add by renping 2019-08-06 end
//		);
		
        Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), prinAmt,LoginUserUtil.getUserName(),
            DictCodeListExt2.平安资产类型_外汇远期.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getPs()
            , null ,entity.getWorkflowId());
        params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
        params.put("prinAmt", dto.getPrinAmt());
        return commonBusinessService.是否需要上级审批(params);
	} catch (WorkFlowException e) {
				// 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
				throw new WorkFlowException(e);
			}
		} else {
			return null;
		}
	}

	/* 添加交割日与期限联动规则 gaopeng 2019/10/08 start */
	/**
	 * 推算交割日
	 * 
	 * @return 交割日
	 */
	public Map<String, Object> getsettleDate(Map<String, Object> map) {
		CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
		String termType = (String) map.get("termType");
		String term = commonBusinessService.getTerm(termType);
		String tradeDateString = (String) map.get("tradeDate");
		// 获取当月最后一天
		LocalDate day = DateUtil.parseLocalDate(tradeDateString);
		String lastDay = day.withDayOfMonth(day.lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
		String calendarId = (String) map.get("calendarId");
		String settleDate = "";
		if (StringUtil.isNotEmpty(tradeDateString) && StringUtil.isNotEmpty(term)) {
			map.put("tradeDate", tradeDateString);
			String addDay = this.getWorkDay(map);
			// 增加俩个工作日后增加期限
			Date tradeDate = DateUtil.addDay(DateUtil.parse(addDay), Integer.parseInt(term));
			if (StringUtil.isNotEmpty(calendarId) && addDay != null) {
				// 若期限大于1M
				if (Integer.parseInt(term) >= 30) {
					// 判断交易日期+2是否为当月最后一个交易日,若不是则返回最后一个交易日
					map.put("lastDay", lastDay);
					try {
						String lastWorkDay = commonBusinessService.getNextLastWorkDay(map);
						if (addDay.equals(lastWorkDay)) {
							String theDay = DateUtil.format(tradeDate);
							// 获取对应月的最后一天
							LocalDate theLastday = DateUtil.parseLocalDate(theDay);
							String theMouthLastday = theLastday.withDayOfMonth(theLastday.lengthOfMonth())
									.format(DateTimeFormatter.ISO_LOCAL_DATE);
							map.put("lastDay", theMouthLastday);
							// 获取对应月的最后一个工作日
							settleDate = commonBusinessService.getNextLastWorkDay(map);
						} else {
							map.put("tradeDate", DateUtil.format(tradeDate));
							map.put("flag", "2");
							settleDate = this.getNextWorkDay(map);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				} else {
					map.put("tradeDate", DateUtil.format(tradeDate));
					map.put("flag", "1");
					settleDate = this.getNextWorkDay(map);
				}
			}
			map.put("settleDate", settleDate);
		}
		return map;
	}

	/**
	 * 获取工作日
	 *
	 * @param parameters 画面参数
	 * @return 下一个工作日
	 * @see
	 * @since 1.0
	 */
	public String getWorkDay(Map<String, Object> parameters) {
		if (dateCalService != null) {
			Date tradeDate = DateUtil.parse((String) parameters.get("tradeDate"));
			List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
					(String) parameters.get("calendarId"));
			Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 2, holiday, "1");
			return DateUtil.format(tradingDay);
		}
		return null;
	}

	/**
	 * 获取下一个工作日
	 *
	 * @param parameters 画面参数
	 * @return 下一个工作日
	 * @see
	 * @since 1.0
	 */
	public String getNextWorkDay(Map<String, Object> parameters) {
		if (dateCalService != null) {
			Date tradeDate = DateUtil.parse((String) parameters.get("tradeDate"));
			String flag = (String) parameters.get("flag");
			List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31),
					(String) parameters.get("calendarId"));
			if (!holiday.contains((String) parameters.get("tradeDate"))) {
				return (String) parameters.get("tradeDate");
			} else {
				Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, flag);
				return DateUtil.format(tradingDay);
			}
		}
		return null;
	}
	/* 添加交割日与期限联动规则 gaopeng 2019/10/08 end */

	@Override
	public void doBack(TrdForwexInvestTradeDto entity) {
		//切面处理风险接口退回
	    //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}
	
	@Override
    public void doRevoke(TrdForwexInvestTradeDto entity) {
	    //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
	}
}
