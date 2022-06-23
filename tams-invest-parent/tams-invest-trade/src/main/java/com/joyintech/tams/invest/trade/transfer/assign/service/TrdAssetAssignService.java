package com.joyintech.tams.invest.trade.transfer.assign.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.dto.PtlReceInfo;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.trade.transfer.assign.dao.TrdAssetAssignDao;
import com.joyintech.tams.invest.trade.transfer.assign.dto.TrdAssetAssignDto;
import com.joyintech.tams.invest.trade.transfer.assign.dto.TrdSecOverdueDuration003Dto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDtos;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto.TrdAssetsProject001Dto;
import com.joyintech.tams.systemtools.dao.TamsBaseMapper;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.BasePtlReceInfoService;

@Service
public class TrdAssetAssignService {

	@Autowired
	private TrdAssetAssignDao trdAssetAssignDao;

	@Resource(name="trdSecOverdueDuration003ServiceEx")
	private TrdSecOverdueDuration003Service trdSecOverdueDuration003Service;

	@Autowired
	private AutoPeriodCommonService autoPeriodCommonService;

	@Autowired
	private BaseGetBussinessId baseGetBusinessId;

	@Autowired
	private TamsBaseMapper tamsBaseMapper;

	public TrdAssetAssignDto calcAssignAmt(Map<String, String> params) {

		TrdAssetAssignDto result = new TrdAssetAssignDto();
		String isAutoAssign = tamsBaseMapper.selectSysParamValue("sys", "IS_AUTO_ASSIGN");
		if (!DictCodeList.是否_是.getValue().equals(isAutoAssign)) {
			return result;
		}

		String portfolioId = params.get("portfolioId");
		String finprodId = params.get("finprodId");
		Date payDate = DateUtil.parse(params.get("payDate"));

		int counts = trdAssetAssignDao.getMaxPayDate(finprodId, payDate);
		if (counts <= 0) {
			return result;
		}

		TrdAssetAssignDto tradeInfo = trdAssetAssignDao.getInterestTradeInfo(portfolioId, finprodId, payDate);
		//理论待支付利息
		BigDecimal unPaidIntamt = BigDecimal.ZERO;
		if (tradeInfo != null && DictCodeList.是否_否.getValue().equals(tradeInfo.getIsClean())
				&& tradeInfo.getUnPaidIntamt().compareTo(BigDecimal.ZERO) > 0) {
			unPaidIntamt = tradeInfo.getUnPaidIntamt();
			result.setIsNeedAssign(DictCodeList.是否_是.getValue());
			result.setLiBranch(tradeInfo.getLiBranch());
		} else {
			return result;
		}

		//result.setAssignPrinamt(BigDecimal.ZERO);
		result.setAssignIntamt(unPaidIntamt);
		return result;
	}

	/**
	 * 付息日转仓生成对应利息转让交易
	 * 
	 * @param total
	 * @param detail
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void genAssignTrade(String finprodType2, String type8, String bTradeId, String isNeedAssign, Object... deal) {

		if (DictCodeList.是否_否.getValue().equals(isNeedAssign)) {
			return;
		}

		TrdSecOverdueDuration003Dto dto = new TrdSecOverdueDuration003Dto();
		dto.setType8(type8);
		dto.setFinprodType2(finprodType2);
		dto.setSecAcctId("999999");
		TrdInvestPersonInfo002Dto counter = new TrdInvestPersonInfo002Dto();
		counter.setTrader(LoginUserUtil.getUserName());
		counter.setTraderDept(LoginUserUtil.getDeptCode());
		counter.setIsOutTrade(DictCodeList.是否_否.getValue());
		dto.setTrdInvestPersonInfo(counter);
		BussTrdTransferOrderDto outOrder = this.genBussTrdTransferOrder();
		List<BussTrdTransferOrderDto> lstOrders = new ArrayList<>();
		lstOrders.add(outOrder);
		dto.setBussTrdTransferOrder(lstOrders);
		if (DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(finprodType2)) {
			TrdAssetsProject001Dto total = (TrdAssetsProject001Dto) deal[0];
			TrdAssetsProject001Dto detail = (TrdAssetsProject001Dto) deal[1];
			dto.setLiBranch(total.getLiBranch());
			dto.setPortfolioId(total.getInvTransPortf());
			dto.setIntAmt(detail.getAssignIntamt());
			dto.setTradeAmt(detail.getAssignIntamt());
			dto.setFinprodId(detail.getFinprodId());
			//dto.setFinprodName(total.getFinprodName());
			dto.setTheoryInt(detail.getAssignIntamt());
			dto.setTransferDate(total.getSettleDate());
			dto.setOperateType(total.getOperateType());
			dto.setInvAim(detail.getInvAim());
			counter.setCounterId(detail.getPortfolioId());
			outOrder.setPortfolioId(total.getInvTransPortf());
			outOrder.setPayAcctNo(detail.getBussTrdTransferOrder().get(0).getRecAcctNo());
			//outOrder.setPayAcctName(detail.getBussTrdTransferOrder().get(0).getRecAcctName());
			outOrder.setRecAcctNo(detail.getBussTrdTransferOrder().get(0).getPayAcctNo());
			//outOrder.setRecAcctName(detail.getBussTrdTransferOrder().get(0).getPayAcctName());
			outOrder.setSettleDate(total.getSettleDate());
			outOrder.setSettleAmt(detail.getAssignIntamt());
		} else if (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(finprodType2)) {
			PlanTransferDto planDto = (PlanTransferDto) deal[0];
			PlanTransferDtos detailDto = (PlanTransferDtos) deal[1];
			dto.setLiBranch(planDto.getLiBranch());
			dto.setPortfolioId(planDto.getActPortfolioId());
			dto.setIntAmt(detailDto.getAssignIntamt());
			dto.setTradeAmt(detailDto.getAssignIntamt());
			dto.setFinprodId(planDto.getFinprodId());
			//dto.setFinprodName(planDto.getFinprodAbbr());
			dto.setTheoryInt(detailDto.getAssignIntamt());
			dto.setTransferDate(planDto.getSettleDate());
			dto.setOperateType(planDto.getOperateType());
			dto.setInvAim("999999");
			counter.setCounterId(planDto.getPlanTransferDtos().get(0).getActPortfolioId());
			StlPayOrdersDto stlDto = planDto.getPlanTransferDtos().get(0).getFinProductTradeDto().getBaseTradeProductDealDtos().get(1)
					.getStlPayOrdersList().get(0);
			outOrder.setPortfolioId(planDto.getActPortfolioId());
			outOrder.setPayAcctNo(stlDto.getMyAcctNo());
			//outOrder.setPayAcctName(stlDto.getPayAcctName());
			outOrder.setRecAcctNo(stlDto.getCounterAcctNo());
			//outOrder.setRecAcctName(stlDto.getRecAcctName());
			outOrder.setSettleDate(planDto.getSettleDate());
			outOrder.setSettleAmt(detailDto.getAssignIntamt());
		} else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)) {
			SecTransfer001Dto totalDto = (SecTransfer001Dto) deal[0];
			SecTransferDto detailDto = (SecTransferDto) deal[1];
			dto.setLiBranch(totalDto.getLiBranch());
			dto.setPortfolioId(totalDto.getInvestPortfolioId());
			dto.setIntAmt(detailDto.getAssignIntamt());
			dto.setTradeAmt(detailDto.getAssignIntamt());
			dto.setFinprodId(totalDto.getFinprodId());
			//dto.setFinprodName(totalDto.getFinprodAbbr());
			dto.setTheoryInt(detailDto.getAssignIntamt());
			dto.setTransferDate(totalDto.getSettleDate());
			dto.setOperateType(totalDto.getOperateType());
			dto.setInvAim(detailDto.getInvAim());
			dto.setSecAcctId(totalDto.getSecManageAcctId());
			counter.setCounterId(detailDto.getPortfolioId());
			StlPayOrdersDto stlDto = detailDto.getStlPayOrdersList().get(0);
			outOrder.setPortfolioId(totalDto.getInvestPortfolioId());
			outOrder.setPayAcctNo(stlDto.getCounterAcctNo());
			//outOrder.setPayAcctName(stlDto.getPayAcctName());
			outOrder.setRecAcctNo(stlDto.getMyAcctNo());
			//outOrder.setRecAcctName(stlDto.getRecAcctName());
			outOrder.setSettleDate(totalDto.getSettleDate());
			outOrder.setSettleAmt(detailDto.getAssignIntamt());
		}
		dto.setTotleTradeId(trdSecOverdueDuration003Service.genBusiCode());
		dto.setActPortfolioId(dto.getPortfolioId());
		//dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));

		BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
		/*Map<String, Object> mapReceInfo = new HashMap<>();
		mapReceInfo.put("branch", dto.getLiBranch());
		mapReceInfo.put("finprodId", dto.getFinprodId());
		PtlReceInfo 应收款标的dto = basePtlReceInfoService.genPtlReceInfo(mapReceInfo);*/
		PtlReceInfo 应收款标的dto = new PtlReceInfo();
		应收款标的dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		应收款标的dto.setMdate(dto.getTransferDate());
		dto.setMdate(应收款标的dto.getMdate());

		List<BaseTradeInfoDto> baseTotalTradeInfoDtoList = new ArrayList<>();
		result.setBaseTradeInfoDtoList(baseTotalTradeInfoDtoList);
		//转出汇总
		TrdFinProductDealTotal total转出 = trdSecOverdueDuration003Service.gen汇总交易信息(dto, 应收款标的dto);
		result.setTrdFinProductDealTotal(total转出);
		//转出汇总扩展
		PaTrdFinProductDealTotal paTotal转出 = trdSecOverdueDuration003Service.gen汇总扩展交易(dto);
		result.setPaTrdFinProductDealTotal(paTotal转出);
		//转出汇总附属
		TrdDealTotalAdd addTotal转出 = trdSecOverdueDuration003Service.gen汇总扩展附属(dto);
		result.setTrdDealTotalAdd(addTotal转出);
		//转入
		BaseTradeInfoDto baseTrade = new BaseTradeInfoDto();
		baseTotalTradeInfoDtoList.add(baseTrade);
		//金融产品交易汇总Entity_转入
		//		交易方向									买入
		//		交易对手									画面.组合代码
		//		其他同转出
		//金融产品交易汇总附属表Entity_转出
		//		同转出
		//金融产品交易汇总扩展Entity_转出
		//		同转出
		TrdFinProductDealTotal total转入 = new TrdFinProductDealTotal();
		BeanUtil.copyPropertiesIgnoreNull(total转入, total转出);
		baseTrade.setTrdFinProductDealTotal(total转入);
		String 转入汇总交易编号 = trdSecOverdueDuration003Service.genBusiCode();
		total转入.setTotleTradeId(转入汇总交易编号);
		total转入.setpTotleTradeId(total转出.getTotleTradeId());
		total转入.setPs(DictCodeList.交易方向_买入.getValue());
		total转入.setCounterId(dto.getActPortfolioId());
		total转入.setPortfolioId(total转出.getCounterId());
		//转入汇总扩展
		PaTrdFinProductDealTotal paTotal转入 = new PaTrdFinProductDealTotal();
		BeanUtil.copyPropertiesIgnoreNull(paTotal转入, paTotal转出);
		paTotal转入.setTotleTradeId(转入汇总交易编号);
		baseTrade.setPaTrdFinProductDealTotal(paTotal转入);
		//转入汇总附属
		TrdDealTotalAdd addTotal转入 = new TrdDealTotalAdd();
		BeanUtil.copyPropertiesIgnoreNull(addTotal转入, addTotal转出);
		addTotal转入.setTotleTradeId(转入汇总交易编号);
		baseTrade.setTrdDealTotalAdd(addTotal转入);
		//划款指令
		List<BussTrdTransferOrderDto> stlPayOrdersList = autoPeriodCommonService.save(dto.getBussTrdTransferOrder());
		StlPayOrders 转出stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, total转出, dto.getWorkflowId());
		StlPayOrders 转入stl = trdSecOverdueDuration003Service.gen转入划款指令(转出stl, total转出.getCounterId(), total转入.getTotleTradeId());
		result.setStlPayOrders(转出stl);
		baseTrade.setStlPayOrders(转入stl);

		/*mapReceInfo.put("portfolioId", dto.getActPortfolioId());
		mapReceInfo.put("secAcctId", dto.getSecAcctId());*/
		List<BaseTradeInfoDto> 利息转出BaseTradeInfo = new ArrayList<>();
		//利息转出entity
		if (dto.getIntAmt() != null && BigDecimal.ZERO.compareTo(dto.getIntAmt()) != 0) {
			//mapReceInfo.put("branch", dto.getLiBranch());
			BaseTradeInfoDto 交易个体利息 = trdSecOverdueDuration003Service.生成交易个体利息(dto, total转出, dto.getIntAmt(), DictCodeList.币种_人民币_CNY.getValue(),
					dto.getInvAim(), total转出.getSecManageAcctId());
			交易个体利息.getTrdProductDeal().setBranch(dto.getLiBranch());
			//转让的交易添加对应前置转仓转入交易的交易编号, 撤销转仓时根据这个撤掉对应的转让交易
			交易个体利息.getTrdProductDeal().setbTradeId(bTradeId);
			交易个体利息.getTrdProductDeal().setStatus("02");
			利息转出BaseTradeInfo.add(交易个体利息);
		}
		BigDecimal 已转让罚息 = BigDecimal.ZERO;
		List<BaseTradeInfoDto> 利息罚息转出BaseTradeInfo = new ArrayList<>();
		if (dto.getTheoryPenaltyIntLx() != null && dto.getPenaltyInt() != null && BigDecimal.ZERO.compareTo(dto.getTheoryPenaltyIntLx()) != 0
				&& BigDecimal.ZERO.compareTo(dto.getPenaltyInt()) != 0) {
			// 处理罚息金额 设计书理解为多少条本金交易 多少条本金罚息 交易 罚息按照本金所占总金额占比分配罚息金额
			if (CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)) {
				for (BaseTradeInfoDto 利息TradeInfo : 利息转出BaseTradeInfo) {
					String 利息罚息交易Id = trdSecOverdueDuration003Service.genBusiCode();
					//业务类型			罚息交易
					//分支序号			利息应收款标的dto.分支序号
					//交易本金
					//利息总额			画面.转让罚息*(金融产品交易及相关-利息转出对应.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)																														最后一笔倒减
					//全价总额
					//交易方向			卖出
					//随交易支付费用
					//交易金额			画面.转让罚息*(金融产品交易及相关-利息转出对应.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)																														最后一笔倒减
					//未随交易支付费用
					//总交易费用
					//其他字段同本金转出
					BaseTradeInfoDto 利息罚息TradeInfo = trdSecOverdueDuration003Service.genDumpBaseTradeInfoDto(利息TradeInfo, 利息罚息交易Id, null);
					利息罚息TradeInfo.getTrdProductDeal().setBusiType(DictCodeList.业务类型_逾期交易.getValue());
					BigDecimal 罚息 = ArithmeticUtil.multiply(dto.getPenaltyInt(), ArithmeticUtil.divide(利息罚息TradeInfo.getTrdProductDeal().getIntAmt(),
							ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt()), 4));
					if (利息转出BaseTradeInfo.indexOf(利息TradeInfo) == 利息转出BaseTradeInfo.size() - 1) {
						罚息 = ArithmeticUtil.subtract(dto.getPenaltyInt(), 已转让罚息);
					}
					已转让罚息 = ArithmeticUtil.doAdd(已转让罚息, 罚息);
					利息罚息TradeInfo.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
					利息罚息TradeInfo.getTrdProductDeal().setIntAmt(罚息);
					利息罚息TradeInfo.getTrdProductDeal().setTradeAmt(罚息);
					利息罚息TradeInfo.getTrdProductDeal().setFpriceAmt(BigDecimal.ZERO);
					利息罚息TradeInfo.getTrdProductDeal().setPs(DictCodeList.交易方向_卖出.getValue());
					利息罚息转出BaseTradeInfo.add(利息罚息TradeInfo);
				}
			}
		}
		//转入entity相关生成 所有的交易与转出有关，所有的待入库交易考虑根据转出生成
		List<BaseTradeInfoDto> 利息转入BaseTradeInfo = new ArrayList<>();
		List<BaseTradeInfoDto> 利息罚息转入BaseTradeInfo = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)) {
			for (BaseTradeInfoDto temp : 利息转出BaseTradeInfo) {
				BaseTradeInfoDto 转入Temp = trdSecOverdueDuration003Service.genDumpBaseTradeInfoDto(temp, null, total转出.getCounterId());
				转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_应收款项转入.getValue());
				转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
				利息转入BaseTradeInfo.add(转入Temp);
			}
		}
		if (CollectionUtils.isNotEmpty(利息罚息转出BaseTradeInfo)) {
			for (BaseTradeInfoDto temp : 利息罚息转出BaseTradeInfo) {
				BaseTradeInfoDto 转入Temp = trdSecOverdueDuration003Service.genDumpBaseTradeInfoDto(temp, null, total转出.getCounterId());
				转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_买入.getValue());
				转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
				利息罚息转入BaseTradeInfo.add(转入Temp);
			}
		}
		List<BaseTradeInfoDto> 交易list = new ArrayList<>();
		List<BaseTradeInfoDto> 罚息list = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(利息转入BaseTradeInfo)) {
			交易list.addAll(利息转入BaseTradeInfo);
		}
		if (CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)) {
			交易list.addAll(利息转出BaseTradeInfo);
		}
		if (CollectionUtils.isNotEmpty(利息罚息转出BaseTradeInfo)) {
			罚息list.addAll(利息罚息转出BaseTradeInfo);
		}
		if (CollectionUtils.isNotEmpty(利息罚息转入BaseTradeInfo)) {
			罚息list.addAll(利息罚息转入BaseTradeInfo);
		}
		baseTrade.setBaseTradeInfoDtoList(交易list);
		baseTrade.setPenaltyIntBaseTradeInfoDtoList(罚息list);
		dto.setBaseTotalTradeInfoDto(result);

		//完成前插入留痕表, 撤销时候使用
		ProcessMark processMark = new ProcessMark();
		processMark.setWorkflowId(WorkFlowUtil.getWorkflowId());
		processMark.setPageId("TrdSecOverdueDuration003");
		processMark.setBusinessDataPk1(dto.getTotleTradeId());
		processMark.setBusinessDataPk2(dto.getActPortfolioId());
		processMark.setBusinessDataPk3(dto.getFinprodId());
		processMark.setBusinessDataPk4(dto.getSecAcctId());
		//processMark.setBusinessDataPk5(trade.getActPortfolioId());
		processMark.setBusinessDataPk6(dto.getLiBranchStr());
		//processMark.setHisNo(2);
		processMark.setBusinessData(JsonUtils.object2Json(dto));
		processMark.setStatus("N");
		WorkflowContext.getProcessMarkService().insertIntoProcessMark(processMark);
		trdSecOverdueDuration003Service.doComplete(dto, OperationType.NEW);
	}

	/**
	 * 转出划款指令
	 * 
	 * @return
	 */
	private BussTrdTransferOrderDto genBussTrdTransferOrder() {
		BussTrdTransferOrderDto order = new BussTrdTransferOrderDto();
		BussinessCodeParameter parameter = new BussinessCodeParameter(DictCodeList.共通业务类型_DVP清算指令管理);
		order.setOrderId(baseGetBusinessId.buildBussinessCode(parameter));
		order.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		order.setInOutBank("行外");
		order.setIsNeedSend(DictCodeList.是否_否.getValue());
		order.setIsTotalTrade(DictCodeList.是否_否.getValue());
		//order.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
		order.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
		order.setPs(DictCodeListExt1.收支方向_支出.getValue());
		order.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
		return order;
	}

}
