package com.joyintech.tams.invest.trade.transfer.planratetransfer.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.entrust.dao.EtrTrdCommonMapper;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDtos;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeList.交易方向_卖出;
import static com.joyintech.tams.invest.common.constant.BussinessDictCodeList.币种_人民币;

/**
 * @package com.joyintech.tams.trade.resolute.planratetransfer.service
 * @author: Xu Zhihao
 * @date: 2019/12/17 16:50
 * @version: V1.0
 * @description:
 */
@Service
public class TrdPlanRateInvestTradeTransfer001Service extends CommonExportService<PlanTransferDto> implements BaseProcessService<PlanTransferDto> {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(TrdPlanRateInvestTradeTransfer001Service.class);

	/**
	 * 通用Mapper共同代理方法
	 */
	@Autowired(required = false)
	private BussCommonMapperDelegateService bussCommonMapperDelegateService;

	/**
	 * 风险试算组件Service
	 */
	@Autowired(required = false)
	private BasRiskMathCommService basRiskMathCommService;

	/**
	 * 金融产品交易实体类
	 */
	@Autowired(required = false)
	private BaseTradeProductDealCommService baseTradeProductDealCommService;

	@Autowired
	private RepAssetDeptMapper repAssetDeptMapper;
	@Autowired
	private CommonBusinessService commonBusinessService;
	@Autowired
	private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
	@Autowired
	private WflTrdProductDealMapper wflTrdProductDealMapper;
	@Autowired
	private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
	/*@Autowired
	private RepTradeMapper repTradeMapper;*/
	@Autowired
	private WflStlPayOrdersMapper wflStlPayOrdersMapper;
	@Autowired
	private BaseGetBusinessId baseGetBusinessId;

	@Autowired
	private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

	@Autowired
	private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

	@Autowired
	private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

	@Autowired
	private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

	@Autowired
	private TrdDealTotalAddMapper trdDealTotalAddMapper;

	@Autowired
	private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
	/**
	 * 划款指令生成
	 */
	@Autowired(required = false)
	private BussTrdTransferOrderService bussTrdTransferOrderService;

	@Autowired
	private EtrTrdCommonMapper etrTrdCommonMapper;

	@Autowired
	private com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService trdAssetAssignService;

	/**
	 * 查询组合代码
	 *
	 * @param pageParam
	 * @return
	 */
	public List<SelectOptionDto> selectPortoId(Map<String, Object> pageParam) {
		pageParam.put("userId", LoginUserUtil.getUserName());
		return simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanRateInvestTradeTransfer/queryProdIdInfo", pageParam, SelectOptionDto.class);
	}

	/**
	 * 查询利息信息
	 *
	 * @param param
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	public PlanTransferDto queryAssetInt(Map<String, Object> param) {
		return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanRateInvestTradeTransfer/queryAssetInt", param, PlanTransferDto.class);
	}

	/**
	 * 插入业务流程表
	 *
	 * @param entity
	 *            计划类实体类
	 * @see
	 * @since 1.0
	 */
	public void beforeWorkFlow(PlanTransferDto entity) {
		if (!StringUtil.isEmptyList(entity.getPlanTransferDtos())) {
			//BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
			entity.getPlanTransferDtos().forEach(planTransferDtos -> {
				//                // 调用授信共同 sjj start
				//                TrdProductDeal TrdProductDeal = new TrdProductDeal();
				FinProductTradeDto finProductTradeDto = planTransferDtos.getFinProductTradeDto();
				if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {

                        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, baseTradeProductDealDto.getTrdFinProductDealTotal());
                        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());

                        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, baseTradeProductDealDto.getPaTrdFinProductDealTotal());
                        wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                        wflPaTrdFinDealTotal.setWorkflowId(entity.getWorkflowId());
                        if (wflTrdFinProductDealTotalMapper != null) {
                            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);
                        }
                        if (wflPaTrdFinDealTotalMapper != null) {
                            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
                        }

						TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
						AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
						trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
						trdProductDeal.setInvAim(entity.getInvAim());
						inputDto.setTrdProductDeal(trdProductDeal);
						AllTradeCommonUtil.handleBeforeTradeComplete(inputDto, false);
						// 调用授信共同 sjj end

						finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

						WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
						BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
						wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
						wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
						if (wflTrdProductDeal != null) {
							wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
						}
						WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
						BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseTradeProductDealDto.getTrdProductDealAdd());
						wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
						wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
						wflTrdProductDealAdd.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
						if (wflTrdProductDealAdd != null) {
							wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
						}
						List<StlPayOrdersDto> list = baseTradeProductDealDto.getStlPayOrdersList();
						if (list != null && list.size() > 0) {
							//List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
							for (StlPayOrdersDto stlPayOrdersDto : list) {
								WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
								BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
								wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易状态_初始.getValue());
								wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
								if (wflStlPayOrders.getOrderId() != null) {
									//                          bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,wflStlPayOrders);
									wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
								}
							}
						}
					});
				}
				WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
				List<PaTrdProductDeal> paTrdProductDeals = planTransferDtos.getPaTrdProductDeals();
				paTrdProductDeals.forEach(paTrdProductDeal -> {
					BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, paTrdProductDeal);
					wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
					wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
					if (wflPaTrdProductDeal != null) {
						wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
					}
				});

				//              bussCommonMapperDelegateService.insertBatch(WflTrdProductDealMapper.class,wflTrdProductDeal,WflTrdProductDealAddMapper.class,
				//                  wflTrdProductDealAdd,WflPaTrdProductDealMapper.class,wflPaTrdProductDeal);
				//                TrdCommonWflParam trdCommonWflParam = new TrdCommonWflParam();
				//                trdCommonWflParam.setWflTrdProductDeals(Arrays.asList(wflTrdProductDeal));
				//                trdCommonWflParam.setWflDealAdds(Arrays.asList(wflTrdProductDealAdd));
				//                trdCommonWflParam.setWflPaDeals(Arrays.asList(wflPaTrdProductDeal));
				//                List<StlPayOrdersDto> list = planTransferDtos.getFinProductTradeDto().getBaseTradeProductDealDtos().get(0).getStlPayOrdersList();
				//                if (list != null && list.size() > 0) {
				//                    List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
				//                    for (StlPayOrdersDto stlPayOrdersDto : list) {
				//                        WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
				//                        BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stlPayOrdersDto);
				//                        wflStlPayOrders.setWorkflowStatus(DictCodeListExt1.交易状态_初始.getValue());
				//                        wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
				//                        if (wflStlPayOrders.getOrderId() != null) {
				//                            //                          bussCommonMapperDelegateService.insertBatch(WflStlPayOrdersMapper.class,wflStlPayOrders);
				//                            stlPayOrdersDtos.add(wflStlPayOrders);
				//                        }
				//                    }
				//                    trdCommonWflParam.setWflStlPayOrdersDtos(stlPayOrdersDtos);

				//                    // 插入Entity对应的业务流程DB中
				//                    simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/insertAutoClassifyBatchWfl",
				//                            trdCommonWflParam, String.class);
				entity.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
				//        委托代码不为空处理委托代码
				finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
					String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId()
							: item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
					if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
						item.setEntrustId(entrustId);
					}
				});
				baseTradeProductDealCommService.execute(planTransferDtos.getFinProductTradeDto());
				//若带转出产品组合是子组合，插入对应持仓
				if (StringUtil.isNotEmpty(entity.getPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(交易方向_卖出.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
						}
					});
					baseTradeProductDealCommService.insertPosition(finProductTradeDto);
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(交易方向_卖出.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
						}
					});
				}
				//若带转入产品组合是子组合，插入对应持仓
				if (StringUtil.isNotEmpty(planTransferDtos.getPortfolioId()) && !planTransferDtos.getActPortfolioId().equals(planTransferDtos.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(planTransferDtos.getActPortfolioId());
						}
					});
					baseTradeProductDealCommService.insertPosition(finProductTradeDto);
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(planTransferDtos.getPortfolioId());
						}
					});
				}
			});

		}
	}

	@Override
	public void doComplete(PlanTransferDto entity, OperationType type) {
		//删除流程中数据
		String workflowId = entity.getWorkflowId();
		baseTradeProductDealCommService.deleteWflData(workflowId);
		if (!StringUtil.isEmptyList(entity.getPlanTransferDtos())) {
			entity.getPlanTransferDtos().forEach(planTransferDtos -> {
				//                planTransferDtos.setActPortfolioId(planTransferDtos.getPortfolioId());
				//                planTransferDtos.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(planTransferDtos.getPortfolioId()));
				FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
				planTransferDtos.getFinProductTradeDto().setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
				finProductTradeDto = planTransferDtos.getFinProductTradeDto();
				basRiskMathCommService.insert(planTransferDtos.getRiskTrialRetDto(), planTransferDtos.getTradeId());

				//        委托代码不为空处理委托代码
				finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
					String entrustId = StringUtil.isNotEmpty(entity.getEntrustId()) ? entity.getEntrustId()
							: item.getTrdProductDealAdd() != null ? item.getTrdProductDealAdd().getEntrustId() : null;
					if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
						item.setEntrustId(entrustId);
					}
				});

				entity.setOperateType(type.toString());
				simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanRateInvestTradeTransfer/doComplete", planTransferDtos, String.class);
				entity.setTradeAmt(planTransferDtos.getTransferTradeAmt());
				// 所有交易生效后调用的共通方法
				if (!StringUtil.isEmptyList(finProductTradeDto.getBaseTradeProductDealDtos())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {

                        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, baseTradeProductDealDto.getTrdFinProductDealTotal());

                        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
                        BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, baseTradeProductDealDto.getPaTrdFinProductDealTotal());

                        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
                        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, baseTradeProductDealDto.getTrdDealTotalAdd());

//                        if (trdFinProductDealTotalMapper != null) {
//                            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
//                        }
                        if (paTrdFinProductDealTotalMapper != null) {
                            paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);
                        }
                        if (trdDealTotalAddMapper != null) {
                            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                        }


						TrdProductDeal trdProductDeal = baseTradeProductDealDto.getTrdProductDeal();
						TrdProductDealAdd trdProductDealAdd = baseTradeProductDealDto.getTrdProductDealAdd();
						AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
						inputDto.setProdid(trdProductDealAdd.getPortfolioId());
						inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
						inputDto.setTrdProductDeal(trdProductDeal);
						inputDto.setTrdProductDealAdd(trdProductDealAdd);
						inputDto.setTrdTradeFeeDetail(null);
						AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

						afterWorkFlow(trdProductDeal, trdProductDealAdd);
						if (DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs()) && DictCodeList.是否_否.getValue().equals(trdProductDeal.getIsOutTrade())) {
							trdAssetAssignService.genAssignTrade(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), DictCodeList.平安资产类型_计划类资产_利率型.getValue(),
									trdProductDeal.getTradeId(), entity.getIsNeedAssign(), entity, planTransferDtos);
							//throw new RuntimeException();
						}
					});
				}

				baseTradeProductDealCommService.execute(finProductTradeDto);
				//若带转出产品组合是子组合，插入对应持仓
				if (StringUtil.isNotEmpty(entity.getPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(交易方向_卖出.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
						}
					});
					baseTradeProductDealCommService.insertPosition(finProductTradeDto);
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(交易方向_卖出.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId());
						}
					});
				}
				//若带转入产品组合是子组合，插入对应持仓
				if (StringUtil.isNotEmpty(planTransferDtos.getPortfolioId()) && !planTransferDtos.getActPortfolioId().equals(planTransferDtos.getPortfolioId())) {
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(planTransferDtos.getActPortfolioId());
						}
					});
					baseTradeProductDealCommService.insertPosition(finProductTradeDto);
					finProductTradeDto.getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto1 -> {
						if (baseTradeProductDealDto1.getTrdProductDeal().getPs().equals(DictCodeList.交易方向_买入.getValue())) {
							baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(planTransferDtos.getPortfolioId());
						}
					});
				}
			});
		}
	}

	@Override
	public void doInsert(PlanTransferDto entity, OperationType type) {
		if (type.toString().equals("NEW") && entity.getOperateType().equals("submit")) {
			beforeWorkFlow(entity);
		}
	}

	@Override
	public void doUpdate(PlanTransferDto entity) {
		if (entity.getOperateType().equals("submit")) {
			doDelete(entity.getWorkflowId());
			FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
					FinProductMapper.class, entity.getFinprodId(), FinProduct.class);
			BussinessCodeParameter bussinessCodeParameterIn = new BussinessCodeParameter();
			BussinessCodeParameter bussinessCodeParameterOut = new BussinessCodeParameter();
			if (!StringUtil.isEmptyList(entity.getPlanTransferDtos())) {
				entity.getPlanTransferDtos().forEach(planTransferDtos -> {
					if (StringUtil.isEmpty(planTransferDtos.getTotleTradeId())) {
						bussinessCodeParameterIn.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
						if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProduct.getFinprodType2())){
							bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
						}else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProduct.getFinprodType2())){
							bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_信托产品);
						}else{
							bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
						}
						planTransferDtos.setTradeId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());
						planTransferDtos.setTotleTradeId(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());

						bussinessCodeParameterOut.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产赎回);
						if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProduct.getFinprodType2())){
							bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
						}else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProduct.getFinprodType2())){
							bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_信托产品);
						}else{
							bussinessCodeParameterOut.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
						}
						planTransferDtos.setTradeIdOut(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterOut) : IDGenerator.generate());
						planTransferDtos.setTotleTradeIdOut(baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate());
					}
					this.setEntity(entity, planTransferDtos);
				});
			}
			ProcessMark newEntity;
			try {
				newEntity = WorkFlowUtil.getBusinessObjectFromRequest(PlanTransferDto.class, true);
				// 工作流ID
				newEntity.setWorkflowId(entity.getWorkflowId());
				newEntity.setStatus(entity.getStatus());
				newEntity.setBusinessData(JsonUtils.object2Json(entity));
				// 更新业务表数据
				// 操作留痕表
				WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			beforeWorkFlow(entity);
		}
	}

	@Override
	public void doDelete(String workflowId) {
		baseTradeProductDealCommService.delete(workflowId);
	}

	/**
	 * 合成各实体类
	 *
	 * @param dto
	 *            计划类（利率型）投资交易拆分实体类
	 * @return 计划类（利率型）投资交易拆分实体类
	 * @see
	 * @since 1.0
	 */
	public void setEntity(PlanTransferDto dto, PlanTransferDtos planTransferDtos) {
		dto.setActPortfolioId(dto.getTransferPortfolioId());
		dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getTransferPortfolioId()));
		planTransferDtos.setActPortfolioId(planTransferDtos.getPortfolioId());
		planTransferDtos.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(planTransferDtos.getPortfolioId()));
		List<BaseTradeProductDealDto> list = new ArrayList<BaseTradeProductDealDto>();
		FinProductTradeDto finDto = new FinProductTradeDto();
		//转入交易
		BaseTradeProductDealDto baseDtoIn = new BaseTradeProductDealDto();
        TrdFinProductDealTotal trdFinProductDealTotal = getTrdTotalDeal(dto,planTransferDtos);
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_买入.getValue());
		trdFinProductDealTotal.setRemark("计划类利率型转入");
        baseDtoIn.setTrdFinProductDealTotal(trdFinProductDealTotal);//汇总交易表
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = setPaTrdProductDealTotal(dto, planTransferDtos);
        baseDtoIn.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        TrdDealTotalAdd trdDealTotalAdd = setTrdDealTotalAdd(dto, planTransferDtos);
        baseDtoIn.setTrdDealTotalAdd(trdDealTotalAdd);
		TrdProductDealAdd trdProductDealAdd = this.setTrdProductDealAdd(dto, planTransferDtos);
        baseDtoIn.setTrdProductDealAdd(trdProductDealAdd);
		TrdProductDeal trdProductDeal = this.getEntity(dto, planTransferDtos);
		trdProductDeal.setIsSwapTransaction(DictCodeList.是否_是.getValue());
		trdProductDeal.setRemark("计划类利率型转入");
		trdProductDeal.setInvAim(dto.getInvAim());
		baseDtoIn.setTrdProductDeal(trdProductDeal);
		//        if (dto.getStlPayOrdersList() != null) {
		//            for (StlPayOrders sDto : dto.getStlPayOrdersList()) {
		//                sDto.setTradeId(dto.getTradeId());
		//            }
		//        }
		//        baseDto.setStlPayOrdersList(dto.getStlPayOrdersList());
		//        baseDto.setTrdTradeFeeDetailList(setTrdTradeFeeDetails(dto, planTransferDtos));
		//转出交易
		BaseTradeProductDealDto baseDtoOut = new BaseTradeProductDealDto();
		TrdProductDealAdd trdProductDealAddOut = new TrdProductDealAdd();
		TrdProductDeal trdProductDealOut = new TrdProductDeal();
        TrdFinProductDealTotal trdFinProductDealTotalOut = new TrdFinProductDealTotal();
        PaTrdFinProductDealTotal paTrdFinProductDealTotalOut = new PaTrdFinProductDealTotal();
        TrdDealTotalAdd trdDealTotalAddOut = new TrdDealTotalAdd();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddOut, trdProductDealAdd);
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealOut, trdProductDeal);
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotalOut, trdFinProductDealTotal);
		trdProductDealAddOut.setPortfolioId(dto.getTransferPortfolioId());
		trdProductDealAddOut.setTradeId(planTransferDtos.getTradeIdOut());
		trdProductDealOut.setrTradeId(trdProductDeal.getTradeId());
		trdProductDealOut.setSplitTradeId(trdProductDeal.getTradeId());
		trdProductDealOut.setIsSwapTransaction(DictCodeList.是否_是.getValue());
		trdProductDealOut.setTradeId(planTransferDtos.getTradeIdOut());
        trdProductDealOut.setTotleTradeId(planTransferDtos.getTotleTradeIdOut());
        trdProductDealOut.setRemark("计划类利率型转出");
		trdProductDealOut.setPs(交易方向_卖出.getValue());
		trdProductDealOut.setCounterId(trdProductDealAdd.getPortfolioId());
        trdFinProductDealTotalOut.setTotleTradeId(planTransferDtos.getTotleTradeIdOut());
        trdFinProductDealTotalOut.setPortfolioId(dto.getTransferPortfolioId());
        trdFinProductDealTotalOut.setRemark("计划类利率型转出");
        trdFinProductDealTotalOut.setPs(交易方向_卖出.getValue());
        trdFinProductDealTotalOut.setCounterId(trdProductDealAdd.getPortfolioId());
        paTrdFinProductDealTotalOut.setTotleTradeId(planTransferDtos.getTotleTradeIdOut());
        paTrdFinProductDealTotalOut.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());

        trdDealTotalAddOut.setTotleTradeId(planTransferDtos.getTotleTradeIdOut());
		baseDtoOut.setTrdProductDeal(trdProductDealOut);
		baseDtoOut.setTrdProductDealAdd(trdProductDealAddOut);
        baseDtoOut.setTrdFinProductDealTotal(trdFinProductDealTotalOut);//汇总交易表
        baseDtoOut.setPaTrdFinProductDealTotal(paTrdFinProductDealTotalOut);
        baseDtoOut.setTrdDealTotalAdd(trdDealTotalAddOut);

        //划款指令(买入)
		gen划款指令_BY_交易(baseDtoIn, DictCodeListExt1.指令状态_新建.getValue(), planTransferDtos.getTotleTradeId());
		//划款指令（卖出）
		gen划款指令_BY_交易(baseDtoOut, DictCodeListExt1.指令状态_新建.getValue(), planTransferDtos.getTotleTradeIdOut());
		//生成划款指令
		//        List<StlPayOrdersDto> list1 = new ArrayList<>();
		//        StlPayOrdersDto stlPayOrdersDto = convertToSPOEntity(trdProductDeal);
		//        StlPayOrdersDto stlPayOrdersDtoOut = convertToSPOEntity(trdProductDealOut);
		//        list1.add(stlPayOrdersDto);
		//        list1.add(stlPayOrdersDtoOut);
		//        list.add(baseDtoIn);
		//        list.add(baseDtoOut);
		//        baseDtoIn.setStlPayOrdersList(list1);
		list.add(baseDtoIn);
		list.add(baseDtoOut);
		finDto.setBaseTradeProductDealDtos(list);
		planTransferDtos.setFinProductTradeDto(finDto);
		//扩展表
		List<PaTrdProductDeal> paTrdProductDealList = new ArrayList<>();
		PaTrdProductDeal paTrdProductDealIn = this.setPaTrdProductDeal(dto, planTransferDtos, trdProductDeal);
		PaTrdProductDeal paTrdProductDealOut = this.setPaTrdProductDeal(dto, planTransferDtos, trdProductDealOut);
		paTrdProductDealList.add(paTrdProductDealIn);
		paTrdProductDealList.add(paTrdProductDealOut);
		planTransferDtos.setPaTrdProductDeals(paTrdProductDealList);
		//组合交易表
		List<PtlTrade> ptlTrades = genPtlTrades(planTransferDtos, dto, trdProductDeal);
		ptlTrades.addAll(genPtlTrades(planTransferDtos, dto, trdProductDealOut));
		planTransferDtos.setPtlTrade(ptlTrades);
		//风险试算
		if (!StringUtil.isEmptyList(dto.getRiskTrialRetDto())) {
			for (RiskTrialRetDto riskTrialRetDto : dto.getRiskTrialRetDto()) {
				if (StringUtil.equals(riskTrialRetDto.getPortfolioId(), planTransferDtos.getPortfolioId())) {
					planTransferDtos.setRiskTrialRetDto(null);
					List<RiskTrialRetDto> riskTrialRetDtos = new ArrayList<>();
					riskTrialRetDtos.add(riskTrialRetDto);
					planTransferDtos.setRiskTrialRetDto(riskTrialRetDtos);
				}
			}
		}
	}

	/**
	 * 获取组合交易表list
	 * 
	 * @return 组合交易表list
	 * @see
	 * @since 1.0
	 */
	public List<PtlTrade> genPtlTrades(PlanTransferDtos dto, PlanTransferDto entity, TrdProductDeal trdProductDeal) {
		List<PtlTrade> list = new ArrayList<>();
		if (trdProductDeal.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
			PtlTrade ptlTrade = genPtlTrade(dto.getPortfolioId(), entity, trdProductDeal);
			list.add(ptlTrade);
			if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
				PtlTrade actPtlTrade = genPtlTrade(dto.getActPortfolioId(), entity, trdProductDeal);
				list.add(actPtlTrade);
			}
		} else {
			PtlTrade ptlTrade = genPtlTrade(entity.getPortfolioId(), entity, trdProductDeal);
			list.add(ptlTrade);
			if (!entity.getPortfolioId().equals(entity.getActPortfolioId())) {
				PtlTrade actPtlTrade = genPtlTrade(entity.getActPortfolioId(), entity, trdProductDeal);
				list.add(actPtlTrade);
			}
		}
		return list;
	}

	/**
	 * 获取组合交易表dto
	 * 
	 * @return 组合交易表dto
	 * @see
	 * @since 1.0
	 */
	public PtlTrade genPtlTrade(String portfolioId, PlanTransferDto entity, TrdProductDeal trdProductDeal) {
		PtlTrade ptlTrade = new PtlTrade();
		ptlTrade.setPortfolioId(portfolioId);
		ptlTrade.setBusiNo(trdProductDeal.getTradeId());
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		ptlTrade.setBusiId(entity.getFinprodId());
		ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
		ptlTrade.setSettleDate(DateUtil.parse(DateUtil.format(entity.getTradeDate())));
		return ptlTrade;
	}

	/**
	 * 获取划款指令list
	 * 
	 * @return 划款指令list
	 * @see
	 * @since 1.0
	 */
	public void gen划款指令_BY_交易(BaseTradeProductDealDto baseTradeProductDealDto, String mode, String totleTradeId) {
		BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
		param.setIsTotalTrade(DictCodeList.是否_否.getValue());
		param.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
		param.setDeal(baseTradeProductDealDto.getTrdProductDeal());
		List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = Arrays.asList(bussTrdTransferOrderService.generateOrder(param));
		if (bussTrdTransferOrderDtos != null) {
			for (BussTrdTransferOrderDto ret : bussTrdTransferOrderDtos) {
				if (StringUtil.equals(OperationType.REVOKE.toString(), mode)) {
					ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
				}
				if (StringUtil.equals(ret.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
					ret.setMyAcctNo(ret.getPayAcctNo());
					ret.setCounterAcctNo(ret.getRecAcctNo());
				} else {
					ret.setMyAcctNo(ret.getRecAcctNo());
					ret.setCounterAcctNo(ret.getPayAcctNo());
				}
				if (StringUtil.isNotEmpty(ret.getIsNeedSend())) {
					if (DictCodeList.是否_是.getCodeName().equals(ret.getIsNeedSend())) {
						ret.setIsNeedSend(DictCodeList.是否_是.getValue());
					} else if (DictCodeList.是否_否.getCodeName().equals(ret.getIsNeedSend())) {
						ret.setIsNeedSend(DictCodeList.是否_否.getValue());
					}
				}
			}
			StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, baseTradeProductDealDto.getTrdProductDealAdd(), null);
			if (!StringUtil.isNotEmpty(stl.getSettleDate())) {
				stl.setSettleDate(baseTradeProductDealDto.getTrdProductDeal().getSettleDate());
			}
            stl.setTradeId(totleTradeId);
			baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stl));
		}
	}

	/**
	 * 生成划款指令（根据划款指令列表分配划款指令）
	 * <功能详细描述>
	 *
	 * @param stlPayOrdersList
	 *            划款指令list
	 * @param dealAdd
	 *            交易附属
	 * @param workflowId
	 *            工作流代码
	 * @return 生成结果
	 * @see
	 * @since 1.0
	 */
	public StlPayOrdersDto gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdProductDealAdd dealAdd, String workflowId) {
		StlPayOrdersDto stlPayOrders = null;
		for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
			if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())) {
				stlPayOrders = dto;
				stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
				stlPayOrders.setTradeId(dealAdd.getTradeId());
				stlPayOrders.setWorkflowId(workflowId);
			}
		}
		return stlPayOrders;
	}

	/**
	 * 转换成 划款指令表实体
	 */
	public StlPayOrdersDto convertToSPOEntity(TrdProductDeal trdProductDeal) {
		StlPayOrdersDto spoEntity = new StlPayOrdersDto();
		spoEntity.setTradeId(trdProductDeal.getTradeId());
		String orderId = null;
		if (baseGetBusinessId != null) {
			BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
			bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
			orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
		} else {
			orderId = IDGenerator.generate();
		}
		spoEntity.setSettleDate(trdProductDeal.getSettleDate());
		spoEntity.setOrderId(orderId);
		spoEntity.setMyAcctNo(trdProductDeal.getCounterId());
		spoEntity.setCcy(trdProductDeal.getCcy());
		spoEntity.setSettleAmt(trdProductDeal.getTradeAmt());
		return spoEntity;
	}

	/**
	 * 组合交易表
	 *
	 * @param dto
	 *            计划类（利率型）投资交易实体类
	 * @return 组合交易实体类
	 * @see
	 * @since 1.0
	 */
	public List<PtlTrade> setPtlTrade(PlanTransferDto dto, PlanTransferDtos planTransferDtos) {
		List<PtlTrade> ptlTrades = new ArrayList<>();
		PtlTrade ptlTrade = new PtlTrade();
		PtlTrade ptlTradeOut = new PtlTrade();
		ptlTrade.setPortfolioId(planTransferDtos.getPortfolioId());
		ptlTrade.setBusiNo(planTransferDtos.getTradeId());
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		ptlTrade.setBusiId(dto.getFinprodId());
		ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
		ptlTrade.setSettleDate(dto.getTradeDate());
		ptlTrades.add(ptlTrade);
		BeanUtil.copyPropertiesIgnoreNull(ptlTradeOut, ptlTrade);
		ptlTradeOut.setBusiNo(planTransferDtos.getTradeIdOut());
		ptlTradeOut.setPortfolioId(dto.getTransferPortfolioId());
		ptlTrades.add(ptlTradeOut);
		if (!dto.getTransferPortfolioId().equals(dto.getActPortfolioId()) && StringUtil.isNotEmpty(dto.getActPortfolioId())) {
			PtlTrade ptlTrade1 = new PtlTrade();
			PtlTrade ptlTrade1Out = new PtlTrade();
			ptlTrade1.setPortfolioId(dto.getActPortfolioId());
			ptlTrade1.setBusiNo(planTransferDtos.getTradeId());
			ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
			ptlTrade1.setBusiId(dto.getFinprodId());
			ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
			ptlTrade1.setSettleDate(dto.getTradeDate());
			ptlTrades.add(ptlTrade1);
			BeanUtil.copyPropertiesIgnoreNull(ptlTrade1Out, ptlTrade1);
			ptlTrade1Out.setBusiNo(planTransferDtos.getTradeIdOut());
			ptlTrade1Out.setPortfolioId(planTransferDtos.getPortfolioId());
			ptlTrades.add(ptlTrade1Out);
		}
		return ptlTrades;
	}

	//    private List<TrdTradeFeeDetail> setTrdTradeFeeDetails(PlanTransferDto dto, PlanTransferDtos planTransferDtos) {
	//        List<TrdTradeFeeDetail> result = new ArrayList<>();
	//        TrdTradeFeeDetail trdTradeFeeDetail1 = new TrdTradeFeeDetail();
	////        trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
	////        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
	////            trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
	////        }else{
	////            if(StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
	////                trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_是.getValue());
	////            }else{
	////                trdTradeFeeDetail1.setIsPayWithTrade(DictCodeList.是否_否.getValue());
	////            }
	////        }
	//        trdTradeFeeDetail1.setFeeAmt(planTransferDtos.getFeeAmt());
	//        trdTradeFeeDetail1.setTradeId(planTransferDtos.getTradeId());
	//        trdTradeFeeDetail1.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue()); // 一次性费用类型
	////        result.add(trdTradeFeeDetail1);
	//        if (StringUtil.isNotEmpty(trdTradeFeeDetail1.getFeeAmt())) {
	//            result.add(trdTradeFeeDetail1);
	//        }
	//        return result;
	//    }

	/**
	 * 转入产品生成金融产品附属表
	 *
	 * @param dto
	 *            计划类（利率型）投资交易实体类
	 * @return 金融产品附属类
	 * @see
	 * @since 1.0
	 */
	public TrdProductDealAdd setTrdProductDealAdd(PlanTransferDto dto, PlanTransferDtos planTransferDtos) {
		TrdProductDealAdd addDto = new TrdProductDealAdd();
		addDto.setTradeId(planTransferDtos.getTradeId());
		addDto.setPortfolioId(planTransferDtos.getPortfolioId());
		//        addDto.setEntrustId(dto.getEntrustId());
		//addDto.setProdId(dto.getFinprodId());
		addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		return addDto;
	}

	/**
	 * 金融产品实体类生成(转入产品)
	 *
	 * @param dto
	 *            计划类（利率型）投资交易拆分实体类
	 * @return 金融产品实体类
	 * @see
	 * @since 1.0
	 */
	public TrdProductDeal getEntity(PlanTransferDto dto, PlanTransferDtos planTransferDtos) {
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		trdProductDeal.setTradeId(planTransferDtos.getTradeId());
        trdProductDeal.setTotleTradeId(planTransferDtos.getTotleTradeId());
		trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
		trdProductDeal.setFinprodId(dto.getFinprodId());
		trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		trdProductDeal.setTradeDate(dto.getTradeDate());
		trdProductDeal.setVdate(dto.getVdate());
		trdProductDeal.setMdate(dto.getMdate());
		trdProductDeal.setSettleDate(dto.getSettleDate());
		trdProductDeal.setSettleSpeed(Long.parseLong("0"));
		trdProductDeal.setCcy(dto.getCcy());
		trdProductDeal.setCpriceAmt(planTransferDtos.getPrinAmt());
		trdProductDeal.setShareAmt(planTransferDtos.getPrinAmt());
		trdProductDeal.setPrinAmt(planTransferDtos.getPrinAmt());
		trdProductDeal.setIntAmt(planTransferDtos.getIntAmt());
		trdProductDeal.setFpriceAmt(planTransferDtos.getPrinAmt());
		trdProductDeal.setTradeAmt(planTransferDtos.getTransferTradeAmt());
		trdProductDeal.setCounterId(dto.getTransferPortfolioId());
		trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
		trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		trdProductDeal.setIsOutTrade(DictCodeList.是否_否.getValue());
		trdProductDeal.setRemark("计划类利率型转入");
		trdProductDeal.setCreateUser(dto.getCreateUser());
		trdProductDeal.setCreateTime(new Date());
		trdProductDeal.setUpdateUser(dto.getUpdateUser());
		trdProductDeal.setUpdateTime(new Date());
		if (StringUtil.isNotBlank(dto.getChlId())) {
			trdProductDeal.setChlId(dto.getChlId());
		}
		//add by liuwei for huaxing 20200330 start
		trdProductDeal.setTrader(LoginUserUtil.getUserName());
		//add by liuwei for huaxing 20200330 end
		//        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
		//        if (dto.getSettleSpeed() != null) {
		//            trdProductDeal.setSettleSpeed(dto.getSettleSpeed() - 1);
		//        }
		//        trdProductDeal.setUnitCprice(new BigDecimal("1"));
		//        if (dto.getIntAmt() != null && dto.getTradeAmt() != null) {
		//            trdProductDeal.setUnitInt(ArithmeticUtil.divide(planTransferDtos.getIntAmt(), planTransferDtos.getTransferTradeAmt(), 8));
		//        }
		//        trdProductDeal.setUnitFprice(new BigDecimal("1"));
		//        trdProductDeal.setParValue(new BigDecimal("1"));
		//        trdProductDeal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
		//        trdProductDeal.setFinprodType("");
		//        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
		//        trdProductDeal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/*Long.valueOf(0)*/);
		//        trdProductDeal.setFpriceAmt(planTransferDtos.getTransferTradeAmt());
		//        trdProductDeal.setIntAmt(planTransferDtos.getIntAmt());
		////           trdProductDeal.setSettleDate(dto.getSettleDate());
		//        trdProductDeal.setCpriceAmt(planTransferDtos.getPrinAmt());
		//        trdProductDeal.setShareAmt(planTransferDtos.getPrinAmt());
		//        trdProductDeal.setTradeAmt(planTransferDtos.getTransferTradeAmt());
		//        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
		//        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		//        trdProductDeal.setCounterId(dto.getTrdInvestPersonInfo002Dto().getCounterId());
		//        trdProductDeal.setTrader(dto.getTrdInvestPersonInfo002Dto().getTrader());
		//        trdProductDeal.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
		//        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
		//        trdProductDeal.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
		//        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
		//            dto.setIsOutTrade(dto.getTrdInvestPersonInfo002Dto().getIsOutTrade());
		//        }else{
		//            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
		//        }
		//        trdProductDeal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
		//        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		//        trdProductDeal.setFeeAmt(dto.getChannelFee());
		//        if(StringUtil.equals(DictCodeList.是否_否.getValue(),dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())){
		//            trdProductDeal.setFeeAmtPay(dto.getChannelFee());
		//        }else{
		//            if(StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())){
		//                trdProductDeal.setFeeAmtPay(dto.getChannelFee());
		//            }else{
		//                trdProductDeal.setFeeAmtUnpay(dto.getChannelFee());
		//            }
		//        }

		return trdProductDeal;
	}

	/**
	 * 金融产品交易扩展
	 *
	 * @param dto
	 *            计划类（利率型）投资交易实体类
	 * @return 金融产品交易附属表实体类
	 * @see
	 * @since 1.0
	 */
	public PaTrdProductDeal setPaTrdProductDeal(PlanTransferDto dto, PlanTransferDtos planTransferDtos, TrdProductDeal trdProductDeal) {
		PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
		paTrdProductDeal.setTradeId(trdProductDeal.getTradeId());
		if (StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
			paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
		} else {
			paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
		}
		return paTrdProductDeal;
	}

	/**
	 * 流程结束数据处理
	 *
	 * @see
	 * @since 1.0
	 */
	public void afterWorkFlow(TrdProductDeal trdProductDeal, TrdProductDealAdd trdProductDealAdd) {
		String prodRegistCode = this.getProdRegistCode(trdProductDealAdd.getPortfolioId());
		if (prodRegistCode != null && !prodRegistCode.isEmpty()) {
			RepTrade repTrade = this.setRepTrade(trdProductDeal, this.getProdRegistCode(trdProductDealAdd.getPortfolioId()));
			// 调用基础组件.【交易报送组件】将交易报送信息Entity插入交易报送信息表
			simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanRateInvestTradeTransfer/insertRepTrade", Arrays.asList(repTrade), Boolean.class);
		}
		RepAssetDept repAssetDept = new RepAssetDept();
		repAssetDept.setRepFinprodId(trdProductDeal.getFinprodId());
		repAssetDept.setRepSendStatus(DictCodeList.是否_是.getValue());
		repAssetDeptMapper.updateByPrimaryKeySelective(repAssetDept);
	}

	/**
	 * 获取登记编号
	 *
	 * @param portfolioId
	 *            组合代码
	 * @return 登记编号
	 * @see
	 * @since 1.0
	 */
	public String getProdRegistCode(String portfolioId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("portfolioId", portfolioId);
		map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
		return simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanRateInvestTradeTransfer/getProdRegistCode", map, String.class);
	}

	/**
	 * 设置报送信息
	 *
	 * @param dto
	 *            存款实体类
	 * @param prodRegistCode
	 *            登记编号
	 * @return 报送实体类
	 * @see
	 * @since 1.0
	 */
	public RepTrade setRepTrade(TrdProductDeal dto, String prodRegistCode) {
		RepTrade repTrade = new RepTrade();
		repTrade.setRepFinprodRegId(prodRegistCode);
		repTrade.setRepBankTradeCode(dto.getTradeId());
		if (bussCommonMapperDelegateService != null) {
			FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class, dto.getFinprodId(), FinProduct.class);
			if (finProduct != null) {
				repTrade.setRepBankAssetCode(finProduct.getFinprodMarketId());
			}
		} else {
			repTrade.setRepBankAssetCode(dto.getFinprodId());
		}
		repTrade.setRepCcy(dto.getCcy());
		repTrade.setRepOccurMoney(dto.getTradeAmt());
		repTrade.setRepMoneyCny(dto.getTradeAmt().multiply(commonBusinessService.selectClosePrice(dto.getCcy(), DateUtil.format(dto.getTradeDate()))));
		// 一体化对应etliu start
		//          if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
		if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())) {
			// 一体化对应etliu end
			repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产买入.getValue());
		} else {
			repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
		}
		repTrade.setRepMoneyFlowTypeExp("无");
		repTrade.setRepCount(dto.getPrinAmt());
		repTrade.setRepTradeDate(dto.getTradeDate());
		repTrade.setRepTradeComponent(StringUtil.isEmpty(dto.getCounterTrader()) ? "" : dto.getCounterTrader());
		repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
		repTrade.setRepStatusDate(dto.getTradeDate());
		repTrade.setRepRemark(dto.getRemark());
		repTrade.setRepDelCode(dto.getTradeId());
		if (StringUtil.isNotEmpty(repTrade.getRepMoneyCny()) && StringUtil.isNotEmpty(repTrade.getRepCount())) {
			//              ArithmeticUtil.di
			BigDecimal temp = ArithmeticUtil.multiply(ArithmeticUtil.divide(repTrade.getRepMoneyCny(), repTrade.getRepCount(), 4), new BigDecimal("100"));
			repTrade.setRepUnitPriceFull(temp);
		}
		repTrade.setRepUnitPriceNet(new BigDecimal("100"));
		return repTrade;
	}

	@Override
	public void doBack(PlanTransferDto entity) {
		if (!StringUtil.isEmptyList(entity.getPlanTransferDtos())) {
			entity.getPlanTransferDtos().forEach(planTransferDtos -> {
				if (!StringUtil.isEmptyList(planTransferDtos.getFinProductTradeDto().getBaseTradeProductDealDtos())) {
					planTransferDtos.getFinProductTradeDto().getBaseTradeProductDealDtos().forEach(baseTradeProductDealDto -> {
						AllTradeCommonUtil.deletePosition("", baseTradeProductDealDto.getTrdProductDeal().getTradeId());
					});
				}
			});
		}
	}

    /**
     * 金融产品total实体类生成
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品实体类
     * @see
     * @since 1.0
     */
    public TrdFinProductDealTotal getTrdTotalDeal(PlanTransferDto dto1,PlanTransferDtos dto) {
        TrdFinProductDealTotal trdProductDealTotal = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealTotal, dto);
//        BigDecimal channeFee=dto.getChannelFee();
//        if(channeFee==null){
//            channeFee=BigDecimal.ZERO;
//        }
        trdProductDealTotal.setSettleSpeed(0L);

        trdProductDealTotal.setUnitCprice(new BigDecimal("1"));
        if (dto.getIntAmt() != null && dto.getTransferTradeAmt() != null) {
            trdProductDealTotal.setUnitInt(ArithmeticUtil.divide(dto.getIntAmt(), dto.getTransferTradeAmt(), 8));
        }
		trdProductDealTotal.setChlId(dto1.getChlId());
        trdProductDealTotal.setCcy(dto1.getCcy());
        trdProductDealTotal.setVdate(dto1.getVdate());//起息日
        trdProductDealTotal.setMdate(dto1.getMdate());//到期日
        trdProductDealTotal.setUnitFprice(new BigDecimal("1"));
        trdProductDealTotal.setParValue(new BigDecimal("1"));
        trdProductDealTotal.setBusiType(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue());
        trdProductDealTotal.setFinprodType("");
        trdProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        trdProductDealTotal.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue"))/*Long.valueOf(0)*/);
        trdProductDealTotal.setFpriceAmt(dto.getPrinAmt());//全价总额不包含通道费
        trdProductDealTotal.setIntAmt(dto.getIntAmt());
        trdProductDealTotal.setCpriceAmt(dto.getPrinAmt());
        trdProductDealTotal.setShareAmt(dto.getPrinAmt());
        trdProductDealTotal.setTradeAmt(dto.getTransferTradeAmt());
        trdProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDealTotal.setCounterId(dto1.getTransferPortfolioId());
        trdProductDealTotal.setTrader(LoginUserUtil.getUserName());
        trdProductDealTotal.setPs(dto1.getPs());
//        trdProductDealTotal.setCounterContact(dto.getTrdInvestPersonInfo002Dto().getContactInfo());
//        trdProductDealTotal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdProductDealTotal.setIsOutTrade("Y");

//        trdProductDealTotal.setCounterTrader(dto.getTrdInvestPersonInfo002Dto().getCounterTrader());
        trdProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
//        trdProductDealTotal.setFeeAmt(dto.getChannelFee());
//        if (StringUtil.equals(DictCodeList.是否_否.getValue(), dto.getTrdInvestPersonInfo002Dto().getIsOutTrade())) {
//            trdProductDealTotal.setFeeAmtPay(dto.getChannelFee());
//        } else {
//            if (StringUtil.equals(dto.getCollectType(), DictCodeListExt2.收取方式_回款前扣除.getValue())) {
//                trdProductDealTotal.setFeeAmtPay(dto.getChannelFee());
//            } else {
//                trdProductDealTotal.setFeeAmtUnpay(dto.getChannelFee());
//            }
//        }
        //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
//        trdProductDealTotal.setFeeAmtUnpay(dto.getChannelFee());
        trdProductDealTotal.setPayUnpaidintAmt(new BigDecimal("0"));//支付未支付利息
        trdProductDealTotal.setIspayUnpaidint("N");// 是否转让还本未支付利息

        trdProductDealTotal.setPortfolioId(dto.getActPortfolioId());//存子组合
        return trdProductDealTotal;
    }

    /**
     * 金融产品交易汇总扩展
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品交易汇总附属表实体类
     * @see
     * @since 1.0
     */
    public PaTrdFinProductDealTotal setPaTrdProductDealTotal(PlanTransferDto dto1,PlanTransferDtos dto) {
        PaTrdFinProductDealTotal paTrdProductDeal = new PaTrdFinProductDealTotal();
        paTrdProductDeal.setTotleTradeId(dto.getTotleTradeId());
        paTrdProductDeal.setTheoryInt(dto.getIntAmt());//
        if (dto1.getPs() != null && !dto1.getPs().isEmpty()) {
            if (dto1.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
            } else if (dto1.getPs().equals(交易方向_卖出.getValue())) {
                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
            }
        }
        paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());
        return paTrdProductDeal;
    }
    
    public String getTradeId(String finprodId) {        
    	FinProduct finProduct = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
            FinProductMapper.class, finprodId, FinProduct.class);
    	BussinessCodeParameter bussinessCodeParameterIn = new BussinessCodeParameter();
        bussinessCodeParameterIn.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
        if(StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, finProduct.getFinprodType2())){
            bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
        }else if(StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, finProduct.getFinprodType2())){
            bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_信托产品);
        }else{
            bussinessCodeParameterIn.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
        }
        return baseGetBusinessId != null ? baseGetBusinessId.buildBussinessCode(bussinessCodeParameterIn) : IDGenerator.generate();
    }

    /**
     * 生成金融产品交易汇总附属表
     *
     * @param dto 计划类（利率型）投资交易实体类
     * @return 金融产品附属类
     * @see
     * @since 1.0
     */
    public TrdDealTotalAdd setTrdDealTotalAdd(PlanTransferDto dto1, PlanTransferDtos dto) {
        TrdDealTotalAdd addDto = new TrdDealTotalAdd();
        addDto.setTotleTradeId(dto.getTotleTradeId());
        addDto.setEntrustId(dto1.getEntrustId());
        addDto.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        return addDto;
    }

	public TrdProductDeal getOrder(PlanTransferDtos dto) {
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, dto);
		trdProductDeal.setTradeId(dto.getTradeId());
		trdProductDeal.setCounterId(dto.getPortfolioId());
		trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
		trdProductDeal.setIsOutTrade("N");
		return trdProductDeal;
	}

	public StlPayOrdersDto setStlPayOrdersDto(PlanTransferDtos dto) {
		StlPayOrdersDto entity = new StlPayOrdersDto();
		/*// 结算日期 画面.交易日
		entity.setSettleDate(dto.getTradeDate());
		// 结算金额 画面.调配金额.abs()
		//entity.setSettleAmt(dto.getTradeAmtfp().abs());
		// 投资组合代码 画面.子组合代码
		entity.setPortfolioId(dto.getPortfolioId());
		// 交易编号 金融产品交易Entity.交易编号
		entity.setTradeId(dto.getTradeId());
		// 币种
		entity.setCcy(dto.getCcy());
		// 指令编号
		entity.setOrderId(dto.getTradeId());
		// 本方资金帐户（调用划款指令共通组件获取）
		if (bussTrdTransferOrderService != null) {
			// 调用划款指令共通组件
			BussSettleOrderParameterDto orderParameterDto = new BussSettleOrderParameterDto();
			orderParameterDto.setIsTotalTrade(DictCodeList.是否_否.getValue());
			orderParameterDto.setPortfolioId(dto.getPortfolioId());
			orderParameterDto.setDeal(dto.getTrdProductDeal());
			BussTrdTransferOrderDto resultOrderDto = bussTrdTransferOrderService.generateOrder(orderParameterDto);
			if (resultOrderDto != null) {
				// 获取共通取的托管户
				if (StringUtil.equals(DictCodeList.交易方向_产品申购.getValue(), dto.getTrdProductDeal().getPs())) {
					entity.setMyAcctNo(resultOrderDto.getRecAcctNo());
				} else {
					entity.setMyAcctNo(resultOrderDto.getPayAcctNo());
				}
			}
		}*/
		return entity;
	}

	public List<TrdProductDeal> generateOrdersNQs(PlanTransferDto transferOutDto, List<PlanTransferDtos> transferInDtoList) {
		logger.debug("generateOrdersNQs() called with parameters => 【transferOutDto = {}】, 【transferInDtoList = {}】",
				JsonUtils.object2Json(transferOutDto), JsonUtils.object2Json(transferInDtoList));

		List<TrdProductDeal> trdProductDealList = new ArrayList<>();

		//托管户查询
		String portfolioId = transferOutDto.getPortfolioId();
		List<PtlCapacctInfo> ptlCapacctList = etrTrdCommonMapper.getPtlAcctInfo(portfolioId, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

		for (PlanTransferDtos dto : transferInDtoList) {
			String portfolioIdIn = dto.getPortfolioId();
			List<PtlCapacctInfo> ptlCapacctListIn = etrTrdCommonMapper.getPtlAcctInfo(portfolioIdIn, Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()));

			TrdProductDeal trdProductDeal = getOrder(dto);

			if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1 && ptlCapacctList.get(0).equals(ptlCapacctListIn.get(0))) {
				//※划款指令共通页面调用【业务组件.划款指令共通组件.指令生成】，页面不展示
				trdProductDeal.setStlPayOrderShow("N");
			} else if (ptlCapacctList.size() == 1 && ptlCapacctListIn.size() == 1 &&  ptlCapacctList.get(0).getAcctNo() != ptlCapacctListIn.get(0).getAcctNo()) {
				//页面展示，不做任何处理
			} else {
				//页面展示,但是划款账号设置为空
				trdProductDeal.setStlPayOrderShow("Y");
			}
			trdProductDealList.add(trdProductDeal);
		}
		logger.debug("generateOrdersNQs() called with result => 【trdProductDealList = {}】",
				JsonUtils.object2Json(trdProductDealList));
		return trdProductDealList;
	}
}
