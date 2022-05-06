package com.joyintech.service.period.trdstockbonus.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.service.period.trdstockbonus.dao.TrdStockBonusMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrade;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdstockbonus.TrdStockBonusDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 
 * @author panbin
 * @version 2019年6月19日
 * @see TrdStockBonusService
 */
@Service
public class TrdStockBonusService extends BaseService<TrdStockBonusDto>{

	@Autowired
	private TrdStockBonusMapper trdStockBonusMapper;
	
	@Autowired
	private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
	@Autowired
	private TrdProductDealMapper trdProductDealMapper;
	@Autowired
	private TrdProductDealAddMapper trdProductDealAddMapper;
	@Autowired
	private StlPayOrdersMapper stlPayOrdersMapper;
	@Autowired
	private PtlTradeMapper ptlTradeMapper;
	@Autowired
	private PaTrdProductDealMapper paTrdProductDealMapper;
	
	@Autowired
	private InvestPeriodCommonService investPeriodCommonService;
	@Autowired
    private PeriodCommonMapper periodCommonMapper;
	@Autowired
    private RepTradeMapper repTradeMapper;
	
	@Autowired
    private IPlatformDate platformDateImpl;

	@Autowired
	private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;
	@Autowired
	private WflTrdProductDealMapper wflTrdProductDealMapper;
	@Autowired
	private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
	@Autowired
	private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
	@Autowired
	private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
	@Autowired
	private TradeProductDealCommService baseTradeProductDealCommService;
	
	public Page<TrdStockBonusDto> findPage(int page, int length, Map<String, Object> param) {
		setParams(param);
		setParamsList(param);
        return queryForPageForSelectReturn(page, length, () -> trdStockBonusMapper.findPage(param));
	}

	private void setParamsList(Map<String, Object> param) {

//        if(StringUtil.isNotEmpty((String)param.get("busiCode"))){
//            param.replace("busiCode", Arrays.asList(param.get("busiCode").toString().split(",")));
//        }else{
//            param.replace("busiCode", null);
//        }
        if(StringUtil.isNotEmpty((String)param.get("secManageAcctId"))){
        	param.replace("secManageAcctId", Arrays.asList(param.get("secManageAcctId").toString().split(",")));
        }else{
        	param.replace("secManageAcctId", null);
        }
        if(StringUtil.isNotEmpty((String)param.get("finprodMarketId"))){
        	param.replace("finprodMarketId", Arrays.asList(param.get("finprodMarketId").toString().split(",")));
        }else{
        	param.replace("finprodMarketId", null);
        }
        if(StringUtil.isNotEmpty((String)param.get("chlId"))){
            param.replace("chlId", Arrays.asList(param.get("chlId").toString().split(",")));
        }else{
            param.replace("chlId", null);
        }
        if(StringUtil.isNotEmpty((String)param.get("bonusMode"))){
        	param.replace("bonusMode", Arrays.asList(param.get("bonusMode").toString().split(",")));
        }else{
        	param.replace("bonusMode", null);
        }
	}
	
	private void setParams(Map<String, Object> param) {
        param.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        param.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        param.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        param.put("F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
		param.put("PS_T11", DictCodeList.交易方向_现金分红.getValue());
		param.put("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        param.put("PS_T13", DictCodeList.交易方向_除权登记.getValue());
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        param.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
		// 多级法人改造 add by caiqun 20200615
		if(MultilevelDataAuthorityUtil.isEnabled()) {
			//当前用户所属机构代码
			String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
			param.put("institutionCode",createInst);
		}
		// 多级法人改造 add by caiqun 20200615
    }


	/**
	 * 
	 * TODO 业务编码
	 * @return Page<SelectOptionDto>
	 * @param page
	 * @param length
	 * @param param
	 * @return
	 */
	public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdStockBonusMapper.getBusiCode(param));
    }
	
	/**
	 * 市场代码
	 *
	 * @author panbin
	 * @date 2019年6月21日
	 * @param page
	 * @param length
	 * @param param
	 * @return Page<SelectOptionDto>
	 */
	public Page<SelectOptionDto> getFinprodMarketId(int page, int length, Map<String, Object> param) {
		 return queryForPageForSelectReturn(page, length, () -> trdStockBonusMapper.getFinprodMarketId(param));
	}


	/**
	 * 证券账户
	 *
	 * @author panbin
	 * @date 2019年6月21日
	 * @param page
	 * @param length
	 * @param param
	 * @return Page<SelectOptionDto>
	 */
	public Page<SelectOptionDto> getSecManageAcctId(int page, int length, Map<String, Object> param) {
		return queryForPageForSelectReturn(page, length, () -> trdStockBonusMapper.getSecManageAcctId(param));
	}


	public int revokeCheck(Map<String, Object> param) {
		param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销);
		return trdStockBonusMapper.revokeCheck(param);
	}


	/**
	 * 
	 * TODO 有效、撤销
	 * @return TrdRepoDurationDto
	 * @param param
	 * @return
	 */
	public TrdStockBonusDto findEffectiveInfo(Map<String, Object> param) {
		setParams(param);
		return trdStockBonusMapper.findEffectiveInfo(param);
	}

	/**
	 * 
	 * TODO 流程中
	 * @return TrdRepoDurationDto
	 * @param param
	 * @return
	 */
	public TrdStockBonusDto findProcessInfo(Map<String, Object> param) {
		setParams(param);
		return trdStockBonusMapper.findProcessInfo(param);
	}
	
	
	/**
	 * 
	 * TODO 金融产品交易
	 * @return List<TrdProductDeal>
	 * @param busiCode
	 * @return
	 */
	public List<TrdProductDeal> finPrdDealInfo(String busiCode) {
		return trdStockBonusMapper.finPrdDealInfo(busiCode);
	}

	/**
	 * 
	 * TODO 组合交易
	 * @return PtlTrade
	 * @param tradeId
	 * @return
	 */
	public PtlTrade findPtlTradeInfo(String tradeId) {
		return trdStockBonusMapper.findPtlTradeInfo(tradeId);
	}


	/**
	 * 
	 * TODO 金融产品交易附属
	 * @return TrdProductDealAdd
	 * @param tradeId
	 * @return
	 */
	public TrdProductDealAdd findPrdDealAddInfo(String tradeId) {
		return trdStockBonusMapper.findPrdDealAddInfo(tradeId);
	}


	/**
	 * 
	 * TODO 划款指令
	 * @return List<StlPayOrders>
	 * @param tradeId
	 * @return
	 */
	public List<StlPayOrders> findStlPayOrdInfo(String tradeId) {
		return trdStockBonusMapper.findStlPayOrdInfo(tradeId);
	}


	/**
	 * 
	 * TODO 分红明细
	 * @return List<DividDetailInfo>
	 * @param busiCode
	 * @return
	 */
	public List<FinAssetIntInfo> findDividDetailInfo(String busiCode) {
		// ZGCPCN-232:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220426 start
		// 新老头寸开关(Y:新头寸,N:老头寸)
		String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
		// 开关配置N查老头存
		if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
			return trdStockBonusMapper.findDividDetailInfo(busiCode);
		}else{ // 默认查新头寸
			return trdStockBonusMapper.findNewPositionDividDetailInfo(busiCode);
		}
		// ZGCPCN-232:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220426 end
	}

	/**
	 *  分红明细-流程中
	 * @param busiCode
	 * @return
	 */
	public List<FinAssetIntInfo> findProcessDividInfo(String busiCode) {
		// ZGCPCN-232:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220426 start
		// 新老头寸开关(Y:新头寸,N:老头寸)
		String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
		// 开关配置N查老头存
		if(DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)){
			return trdStockBonusMapper.findProcessDividInfo(busiCode);
		}else{ // 默认查新头寸
			return trdStockBonusMapper.findNewPositionProcessDividInfo(busiCode);
		}
		// ZGCPCN-232:通过开关控制查询新老头寸,默认查询新头寸 modify by fuyongsheng 20220426 end
	}

	/**
	 * 
	 * TODO 
	 * @return List<PtlSecPosition>
	 * @param param
	 * @return
	 */
	public List<FinAssetIntInfo> findividInfo(Map<String, Object> param) {
		return trdStockBonusMapper.findividInfo(param);
	}

	/**
	 * 
	 * TODO 撤销
	 * @return String
	 * @param dto
	 * @return
	 */
	public String doRevoke(TrdStockBonusDto dto) {
		 Map<String, Object> params = new HashMap<>();
         params.put("bizCode", dto.getpTradeId());
         params.put("workflowId", dto.getWorkflowId());
         params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
         params.put("REVOKE_TRADE_STATUS", DictCodeListExt1.交易状态_撤销.getValue());
         params.put("user", dto.getCreateUser());
         params.put("dept", StringUtil.isEmpty(dto.getCreateDept()) ? "dept" : dto.getCreateDept());
         params.put("time", dto.getCreateTime());
         params.put("revokeReason", dto.getRevokeReason());
         this.insertWflTable(params);
         return Boolean.TRUE.toString();
    }

	private void insertWflTable(Map<String, Object> params) {
		params.put("PST11",DictCodeList.交易方向_现金分红.getValue());
//		List<String> subBizCodes = trdStockBonusMapper.findTradeId(params);
//		if (subBizCodes != null && !subBizCodes.isEmpty()) {
//			for (String subBizCode : subBizCodes) {
//				Map<String, Object> params1 = new HashMap<>();
//				params1.putAll(params);
//				params1.put("bizCode", subBizCode);
//				doRevokeDeal(params1);
//			}
//		}
		doRevokeDeal(params);
	}


	private void doRevokeDeal(Map<String, Object> params) {
		// 汇总交易表
		periodCommonMapper.insertWflTotle(params);
		// 汇总扩展表
		periodCommonMapper.insertWflPaTotal(params);

		params.put("ORDER_STATUS_03", DictCodeListExt1.指令状态_撤销.getValue());
		// 划款指令表
		periodCommonMapper.insertWflPayOrders(params);
		// 修改参数
		params.put("totleTradeId", params.get("bizCode"));
		List<TrdProductDeal> deals = periodCommonMapper.findDealByTotalId(params);
		if (!deals.isEmpty()) {
			params.put("deals", deals);
			// 交易表
			periodCommonMapper.insertWflTrade(params);
			// 交易扩展表
			periodCommonMapper.insertWflPaTrade(params);
			// 交易附属表
			periodCommonMapper.insertWflTradeAdd(params);
		}
	}
	
	/**
     * 工作流结束操作
     * 
     * @param dto 数据entity
     * @return 处理结果
     */
    public String doComplete(TrdStockBonusDto dto) {
             String updateUser = dto.getUpdateUser();
             Date updateTime = new Date();
             if (BussinessConstant.REVOKE.equals(dto.getOperateType())) {
                      TrdFinProductDealTotal trdFinProductDealTotal = this
                                        .revoke汇总和交易相关表(dto.getpTradeId(), updateUser, dto.getRevokeReason(), null);
                      // 删除其他表
                      investPeriodCommonService.deleteByWorkflowId(dto.getWorkflowId());
                      
                      return Boolean.TRUE.toString();
             }
			investPeriodCommonService.doInsertAndDelete(dto.getBaseTotalTradeInfoDto(), updateUser, updateTime,
                               dto.getWorkflowId());

             List<BaseTradeInfoDto> baseTradeInfoDtos = dto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
             if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
                      baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                               String portfolioId = baseTradeInfoDto.getTrdProductDealAdd() != null
                                                 ? baseTradeInfoDto.getTrdProductDealAdd().getPortfolioId()
                                                 : baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd().getPortfolioId();

                               String 产品登记编码 = periodCommonMapper.selectProdRegistCode(portfolioId,
                                                 DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                               if (StringUtil.isNotEmpty(产品登记编码)) {
                                        if (baseTradeInfoDto.getTrdProductDeal() != null) {
                                                 insertRep(dto, updateUser, updateTime, baseTradeInfoDto.getTrdProductDeal(), 产品登记编码);
                                        }
                                        if (baseTradeInfoDto.getBaseTrade() != null
                                                          && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                                                 insertRep(dto, updateUser, updateTime,
                                                                   baseTradeInfoDto.getBaseTrade().getTrdProductDeal(), 产品登记编码);
                                        }
                               }

                      });
             }
             return Boolean.TRUE.toString();
    }

	public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, String revokeReason, String type6) {
		Map<String, Object> params = new HashMap<>();
		params.put("bizCode", bizCode);
		params.put("PST11",DictCodeList.交易方向_现金分红.getValue());
    	List<String> subBizCodes = trdStockBonusMapper.findTradeId(params);
		if (subBizCodes != null && !subBizCodes.isEmpty()) {
			for (String subBizCode : subBizCodes) {
				this.revoke汇总和交易相关表(subBizCode, updateUser, false, revokeReason,type6);
			}
		}
		return this.revoke汇总和交易相关表(bizCode, updateUser, true, revokeReason,type6);
	}


	public TrdFinProductDealTotal revoke汇总和交易相关表(String bizCode, String updateUser, boolean isTotal, String revokeReason, String type6) {
		// 用于获取关键信息的map
		Map<String, Object> map = new HashMap<>();
		// 是否汇总的参数区分
		if (isTotal) {
			map.put("totleTradeId", bizCode);
		}
		else {
			map.put("tradeId", bizCode);
		}
		Date updateTime = new Date();
		TrdFinProductDealTotal trdFinProductDealTotal = null;
		if (isTotal) {
			// 金融产品交易汇总
			trdFinProductDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
			// 原数据 是否撤销 => 是
			trdFinProductDealTotal.setIsCancel(DictCodeList.是否_是.getValue());

			trdFinProductDealTotal.setUpdateTime(updateTime);
			trdFinProductDealTotal.setUpdateUser(updateUser);
			trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);
			// 撤销交易状态为撤销
			trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
			String newTotalTradeId = trdFinProductDealTotal.getTotleTradeId() + "R";
			trdFinProductDealTotal.setpTradeId(trdFinProductDealTotal.getTotleTradeId());
			trdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
			if (StringUtil.isNotEmpty(trdFinProductDealTotal.getpTotleTradeId())) {
				trdFinProductDealTotal.setpTotleTradeId(trdFinProductDealTotal.getpTotleTradeId() + "R");
			}
			// 是否撤销入否
			trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
			trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

			PaTrdFinProductDealTotal paTrdFinProductDealTotal = paTrdFinProductDealTotalMapper.selectByPrimaryKey(bizCode);
			paTrdFinProductDealTotal.setTotleTradeId(newTotalTradeId);
			paTrdFinProductDealTotal.setUpdateTime(updateTime);
			paTrdFinProductDealTotal.setUpdateUser(updateUser);
			paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

		}
		// 划款指令
		List<StlPayOrders> ordersTotal = periodCommonMapper.findOrdersByOrderId(bizCode);
		revokeStlPayOrders(ordersTotal);

		// 费用明细
		periodCommonMapper.inertRevokeFeeDetailByTradeId(bizCode, updateUser);

		// 金融产品交易Entity
		// 增加无汇总交易的场景 无汇总交易使用交易编号关联
		List<TrdProductDeal> trdProductDeals = new ArrayList<>();
		if (isTotal) {
			trdProductDeals = periodCommonMapper.findDealByTotalId(map);
		}
		// 一体化对应etliu start
		Map<String, String> portfolioIdMap = new HashMap<String, String>();
		// 一体化对应etliu end


		trdProductDeals.forEach(trdProductDeal1 -> {
			TrdProductDeal trdProductDeal = new TrdProductDeal();
			BeanUtils.copyProperties(trdProductDeal1, trdProductDeal);
			trdProductDeal.setIsCancel(DictCodeList.是否_是.getValue());
			// 原数据 是否撤销 => 是
			trdProductDealMapper.updateByPrimaryKey(trdProductDeal);
			// 交易状态为撤销
			trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
			String oldTradeId = trdProductDeal.getTradeId();
			String newTradeId = trdProductDeal.getTradeId() + "R";
			trdProductDeal.setpTradeId(oldTradeId);
			trdProductDeal.setTradeId(newTradeId);
			trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId()+"R");
			trdProductDeal.setCancelRemark(revokeReason);
			trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
			trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
			trdProductDealMapper.insertSelective(trdProductDeal);


			periodCommonMapper.insertRevokePaTrdProductDeal(oldTradeId, updateUser);
			periodCommonMapper.insertRevokeTrdProductDealAdd(oldTradeId, updateUser);

			TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(oldTradeId);
			// 组合交易
			if (trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getPortfolioId())) {
				PtlTrade deal = new PtlTrade();
				deal.setBusiNo(oldTradeId);
				deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
				deal.setPortfolioId(trdProductDealAdd.getPortfolioId());
				periodCommonMapper.insertRevokePtlTrade(deal, updateUser);

				// 一体化对应etliu start
				portfolioIdMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
				// 一体化对应etliu end
			}

			// 费用明细
			periodCommonMapper.inertRevokeFeeDetailByTradeId(oldTradeId, updateUser);

			// 删除交易报送
			periodCommonMapper.deleteRepTradeByTradeId(oldTradeId);

		});

		// 一体化对应etliu start
		if (!StringUtil.isEmptyList(trdProductDeals)) {
			List<TrdProductDealAdd> trdProductDealAdds = periodCommonMapper.findDealAddByTradeIds(trdProductDeals);
			List<PtlTrade> ptlTradeList = periodCommonMapper.findPtlTradeByTradeIds(trdProductDeals);

			// 所有交易生效后调用的共通方法
			for (TrdProductDeal baseDto : trdProductDeals) {
				AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
				inputDto.setProdid(portfolioIdMap.get("portfolioId"));
				inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
				inputDto.setTrdProductDeal(baseDto);
				inputDto.setTrdProductDealAdd(getAdd(trdProductDealAdds, baseDto.getTradeId()));
				inputDto.setRepoType(type6);
				List<PtlTrade> ptlTradeList1 = new ArrayList<>();
				ptlTradeList.forEach(ptlTrade -> {
					if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
						ptlTradeList1.add(ptlTrade);
					}
				});
				inputDto.setPtlTradeList(ptlTradeList1);
				AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
			}
		}
		// 一体化对应etliu end

		return trdFinProductDealTotal;
	}

	private TrdProductDealAdd getAdd(List<TrdProductDealAdd> trdProductDealAdds, String tradeId) {
		for (TrdProductDealAdd dealAdd : trdProductDealAdds) {
			if (tradeId.equals(dealAdd.getTradeId())) {
				return dealAdd;
			}
		}
		return null;
	}

	private void revokeStlPayOrders(List<StlPayOrders> orders) {
		if (orders != null) {
			orders.forEach(order -> {
					order.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
					order.setpTradeId(order.getOrderId());
					order.setTradeId(order.getTradeId() + "R");
					order.setOrderId(order.getOrderId() + "R");
					stlPayOrdersMapper.insertSelective(order);
				}
			);
		}
	}

    /**
     *
     * @param baseTotalTradeInfoDto
     * @param updateUser
     * @param updateTime
     * @param workflowId
     */
    private void doInsertAndDelete(BaseTotalTradeInfoDto baseTotalTradeInfoDto, String updateUser, Date updateTime,
                                  String workflowId) {
        baseTotalTradeInfoDto.getTrdFinProductDealTotal().setUpdateUser(updateUser);
        baseTotalTradeInfoDto.getTrdFinProductDealTotal().setUpdateTime(updateTime);
        trdFinProductDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getTrdFinProductDealTotal());

        if(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal() != null){
            baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateUser(updateUser);
            baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateTime(updateTime);
            paTrdFinProductDealTotalMapper.insertSelective(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal());
        }

//        if(baseTotalTradeInfoDto.getTrdTradeFeeDetail()!=null) {
//            baseTotalTradeInfoDto.getTrdTradeFeeDetail().setUpdateUser(updateUser);
//            baseTotalTradeInfoDto.getTrdTradeFeeDetail().setUpdateTime(updateTime);
//            trdTradeFeeDetailMapper.insertSelective(baseTotalTradeInfoDto.getTrdTradeFeeDetail());
//        }
        List<BaseTradeInfoDto> baseTradeInfoDtos = baseTotalTradeInfoDto.getBaseTradeInfoDtoList();
        baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
            if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
                baseTradeInfoDto.getTrdFinProductDealTotal().setUpdateUser(updateUser);
                baseTradeInfoDto.getTrdFinProductDealTotal().setUpdateTime(updateTime);
                trdFinProductDealTotalMapper.insertSelective(baseTradeInfoDto.getTrdFinProductDealTotal());
            }
            if (baseTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
                baseTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateUser(updateUser);
                baseTradeInfoDto.getPaTrdFinProductDealTotal().setUpdateTime(updateTime);
                paTrdFinProductDealTotalMapper.insertSelective(baseTradeInfoDto.getPaTrdFinProductDealTotal());
            }

            if (baseTradeInfoDto.getTrdProductDeal() != null) {
                baseTradeInfoDto.getTrdProductDeal().setUpdateUser(updateUser);
                baseTradeInfoDto.getTrdProductDeal().setUpdateTime(updateTime);
                trdProductDealMapper.insertSelective(baseTradeInfoDto.getTrdProductDeal());
            }

            if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
                baseTradeInfoDto.getTrdProductDealAdd().setUpdateUser(updateUser);
                baseTradeInfoDto.getTrdProductDealAdd().setUpdateTime(updateTime);
                trdProductDealAddMapper.insertSelective(baseTradeInfoDto.getTrdProductDealAdd());
            }

            if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
                baseTradeInfoDto.getPaTrdProductDeal().setUpdateUser(updateUser);
                baseTradeInfoDto.getPaTrdProductDeal().setUpdateTime(updateTime);
                paTrdProductDealMapper.insertSelective(baseTradeInfoDto.getPaTrdProductDeal());
            }

//            if (baseTradeInfoDto.getTrdTradeFeeDetail() != null) {
//                baseTradeInfoDto.getTrdTradeFeeDetail().setUpdateUser(updateUser);
//                baseTradeInfoDto.getTrdTradeFeeDetail().setUpdateTime(updateTime);
//                trdTradeFeeDetailMapper.insertSelective(baseTradeInfoDto.getTrdTradeFeeDetail());
//            }

            if (baseTotalTradeInfoDto.isNeedStlPayOrders()&& baseTradeInfoDto.getStlPayOrders() != null) {
                baseTradeInfoDto.getStlPayOrders().setUpdateUser(updateUser);
                baseTradeInfoDto.getStlPayOrders().setUpdateTime(updateTime);
                stlPayOrdersMapper.insertSelective(baseTradeInfoDto.getStlPayOrders());
            }

			if (CollectionUtils.isNotEmpty(baseTradeInfoDto.getPtlTrade())) {
				baseTradeInfoDto.getPtlTrade().forEach(ptlTrade -> {
					ptlTrade.setUpdateUser(updateUser);
					ptlTrade.setUpdateTime(updateTime);
					ptlTradeMapper.insertSelective(ptlTrade);
				});
			}

            BaseTrade baseTrade = baseTradeInfoDto.getBaseTrade();
            if (baseTrade != null) {
                if (baseTrade.getTrdProductDeal() != null) {
                    baseTrade.getTrdProductDeal().setUpdateUser(updateUser);
                    baseTrade.getTrdProductDeal().setUpdateTime(updateTime);
                    trdProductDealMapper.insertSelective(baseTrade.getTrdProductDeal());
                }
//                if (baseTrade.getPaTrdProductDeal() != null) {
//                    baseTrade.getPaTrdProductDeal().setUpdateUser(updateUser);
//                    baseTrade.getPaTrdProductDeal().setUpdateTime(updateTime);
//                    paTrdProductDealMapper.insertSelective(baseTrade.getPaTrdProductDeal());
//                }
                if (baseTrade.getTrdProductDealAdd() != null) {
                    baseTrade.getTrdProductDealAdd().setUpdateUser(updateUser);
                    baseTrade.getTrdProductDealAdd().setUpdateTime(updateTime);
                    trdProductDealAddMapper.insertSelective(baseTrade.getTrdProductDealAdd());
                }
				if (CollectionUtils.isNotEmpty(baseTrade.getPtlTrade())) {
					baseTrade.getPtlTrade().forEach(ptlTrade -> {
						ptlTrade.setUpdateUser(updateUser);
						ptlTrade.setUpdateTime(updateTime);
						ptlTradeMapper.insertSelective(ptlTrade);
					});
				}
            }
        });
        investPeriodCommonService.deleteByWorkflowId(workflowId);
    }
    
    private void insertRep(TrdStockBonusDto dto, String updateUser, Date updateTime, TrdProductDeal deal,
            String 产品登记编码) {
	   List<RepTrade> repTrades = gen交易报送信息(dto, deal, 产品登记编码);
	   repTrades.forEach(repTrade -> {
	            repTrade.setCreateUser(dto.getCreateUser());
	            repTrade.setCreateTime(dto.getCreateTime());
	            repTrade.setCreateDept(dto.getCreateDept());
	            repTrade.setUpdateUser(updateUser);
	            repTrade.setUpdateTime(updateTime);
	            repTradeMapper.insertSelective(repTrade);
	   });
	}
    
    
    /**
     * 处理交易报送的entity
     * 
     * @param entity             数据entity
     * @param deal 				交易entity
     * @param 产品登记编码             产品登记编码
     * @return 报送数据
     */
    private List<RepTrade> gen交易报送信息(TrdStockBonusDto entity, TrdProductDeal deal, String 产品登记编码) {
             // 折算人民币金额（元）
             BigDecimal 最低收盘价 = BigDecimal.ONE;
             if (!DictCodeList.币种_人民币_CNY.getValue().equals(entity.getCcy())) {
                      最低收盘价 = investPeriodCommonService.find最低收盘价(entity.getCcy(), DateUtil.format(entity.getSettleDate()));
             }
             BigDecimal 最低收盘价F = 最低收盘价;
             List<RepTrade> repTrades = new ArrayList<>();
             RepTrade repTrade = new RepTrade();
             repTrade.setRepFinprodRegId(产品登记编码);
             //   发行机构代码
             repTrade.setRepIssuePartyId(entity.getRepIssuePartyId());
             //   行内交易编码
             //   repTrade.setRepBankTradeCode(entity.getTradeId());
             //   行内资产/负债编码
             repTrade.setRepBankAssetCode(entity.getFinprodMarketId());
             //   币种
             repTrade.setRepCcy(entity.getCcy());
             // 对手方 隐藏发行人
             repTrade.setRepTradeComponent("无");
             // 合同号
             repTrade.setRepDelCode(deal.getTradeId());
             //   备注
             repTrade.setRepRemark(entity.getRemark());
             //   报送状态
             repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
             //   数据日期
             repTrade.setRepStatusDate(platformDateImpl.getSysDate());
             //   所有场景生成第一种报送数据||现金分红后续交易不需要生成
             if (!StringUtil.equals(entity.getType(), "04")&&!StringUtil.equals(entity.getType(),"03")) {
	            	 //   第一笔
	            	 //   数量
                      repTrade.setRepCount(deal.getPrinAmt());
                      //   交易日
                      repTrade.setRepTradeDate(deal.getTradeDate());
                      //   折算人民币
                      repTrade.setRepMoneyCny(ArithmeticUtil.multiply(deal.getIntAmt(), 最低收盘价F));
                      //   发生金额
                      repTrade.setRepOccurMoney(deal.getIntAmt());
                      //   资金流动类型
                      repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
                      repTrades.add(repTrade);
             }
             // 货币基金红利再投及非货币红利再投时，生成第二种报送数据
             if (StringUtil.equals(entity.getType(), "01") || StringUtil.equals(entity.getType(), "04")) {
                      RepTrade repTrade2 = new RepTrade();
                      BeanUtil.copyPropertiesIgnoreNull(repTrade2, repTrade);
                      //   第二笔
                      //   数量
                      repTrade2.setRepCount(deal.getShareAmt());
                      //   交易日
                      repTrade2.setRepTradeDate(deal.getSettleDate());
                      //   折算人民币
                      repTrade2.setRepMoneyCny(ArithmeticUtil.multiply(deal.getIntAmt(), 最低收盘价F));
                      //   发生金额
                      repTrade2.setRepOccurMoney(deal.getIntAmt());
                      repTrade2.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_资产买入.getValue());
                      repTrades.add(repTrade2);
             }
             return repTrades;
    }

	/**
	 *
	 * @param baseTotalTradeInfoDto
	 * @return
	 */
	public String doInsert(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
		//periodCommonService.doInsert(baseTotalTradeInfoDto);
		if(StringUtil.isNotEmpty(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId())){
			baseTradeProductDealCommService.deleteWflData(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId());
		}
		WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
		BeanUtils.copyProperties(baseTotalTradeInfoDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal);
		wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
		wflTrdFinProductDealTotalMapper.insert(wflTrdFinProductDealTotal);

		baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
			if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
				WflTrdFinProductDealTotal wflTrdFinProductDealTotal1 = new WflTrdFinProductDealTotal();
				BeanUtils.copyProperties(baseTradeInfoDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal1);
				wflTrdFinProductDealTotal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflTrdFinProductDealTotalMapper.insert(wflTrdFinProductDealTotal1);
			}


			if (baseTradeInfoDto.getTrdProductDeal() != null) {
				WflTrdProductDeal deal = new WflTrdProductDeal();
				BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDeal(), deal);
				deal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflTrdProductDealMapper.insert(deal);
			}

			if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
				WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
				BeanUtils.copyProperties(baseTradeInfoDto.getPaTrdProductDeal(), wflPaTrdProductDeal);
				wflPaTrdProductDeal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflPaTrdProductDealMapper.insert(wflPaTrdProductDeal);
			}

			if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
				WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
				BeanUtils.copyProperties(baseTradeInfoDto.getTrdProductDealAdd(), wflTrdProductDealAdd);
				wflTrdProductDealAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
			}

			if (baseTotalTradeInfoDto.isNeedStlPayOrders() && baseTradeInfoDto.getStlPayOrders() != null) {
				WflStlPayOrders orders = new WflStlPayOrders();
				BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
				orders.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflStlPayOrdersMapper.insert(orders);
			}

			if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
				WflTrdProductDeal deal1 = new WflTrdProductDeal();
				BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getTrdProductDeal(), deal1);
				deal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflTrdProductDealMapper.insert(deal1);
			}

			if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
				WflPaTrdProductDeal deal1 = new WflPaTrdProductDeal();
				BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal(), deal1);
				deal1.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflPaTrdProductDealMapper.insert(deal1);
			}

			if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
				WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
				BeanUtils.copyProperties(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), wflTrdProductDealAdd);
				wflTrdProductDealAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
				wflTrdProductDealAddMapper.insert(wflTrdProductDealAdd);
			}
		});
		return DictCodeListExt1.交易流程状态_流程中.getValue();
	}


	@Autowired
	private PaTrdFinProductDealTotalMapper paTrdFinDealTotalMapper;

	private void do生成有效数据(BaseTotalTradeInfoDto baseTotalTradeInfoDto) {
		if (baseTotalTradeInfoDto.getTrdFinProductDealTotal() != null) {
			trdFinProductDealTotalMapper.insert(baseTotalTradeInfoDto.getTrdFinProductDealTotal());
		}

		if (baseTotalTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
			paTrdFinDealTotalMapper.insert(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal());
		}

		if (baseTotalTradeInfoDto.getBaseTradeInfoDtoList() != null && !baseTotalTradeInfoDto.getBaseTradeInfoDtoList().isEmpty()) {
			baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
				if (baseTradeInfoDto.getTrdFinProductDealTotal() != null) {
					trdFinProductDealTotalMapper.insert(baseTradeInfoDto.getTrdFinProductDealTotal());
				}
				if (baseTradeInfoDto.getPaTrdFinProductDealTotal() != null) {
					paTrdFinDealTotalMapper.insert(baseTradeInfoDto.getPaTrdFinProductDealTotal());
				}

				if (baseTradeInfoDto.getTrdProductDeal() != null) {
					trdProductDealMapper.insert(baseTradeInfoDto.getTrdProductDeal());
				}

//				if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
//					paTrdProductDealMapper.insert(baseTradeInfoDto.getPaTrdProductDeal());
//				}

				if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
					trdProductDealAddMapper.insert(baseTradeInfoDto.getTrdProductDealAdd());
				}

				if (baseTotalTradeInfoDto.isNeedStlPayOrders() && baseTradeInfoDto.getStlPayOrders() != null) {
					stlPayOrdersMapper.insert(baseTradeInfoDto.getStlPayOrders());
				}

				if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
					trdProductDealMapper.insert(baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
				}

//				if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
//					paTrdProductDealMapper.insert(baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal());
//				}

				if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
					trdProductDealAddMapper.insert(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
				}
//                添加没有维护组合交易表的情况
				if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getPtlTrade() != null) {
					baseTradeInfoDto.getBaseTrade().getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade)
					);
				}
			});
		}
	}

	public List<TrdStockBonusDto> getRTrdProductDeals(Map<String, Object> param) {
		return trdStockBonusMapper.getRTrdProductDeals(param);
	}

	public List<BaseTradeProductDealDto> findEffectiveT13Deal(Map<String, Object> param) {
		List<TrdProductDeal> deals = trdStockBonusMapper.findEffectiveT13Deal(param);
		List<BaseTradeProductDealDto> result = new ArrayList<>();
		if (deals != null && !deals.isEmpty()) {
			deals.forEach(trdProductDeal -> {
				BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
				dto.setTrdProductDeal(trdProductDeal);
				dto.setTrdProductDealAdd(trdProductDealAddMapper.selectByPrimaryKey(trdProductDeal.getTradeId()));
				List<StlPayOrders> stlPayOrders = trdStockBonusMapper.findStlPayOrdInfo((String) param.get("tradeId"));
				if (stlPayOrders != null && !stlPayOrders.isEmpty()) {
					StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
					BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, stlPayOrders.get(0));
					dto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
				}
			});
		}
		return result;
	}
}
