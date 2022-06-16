package com.joyintech.service.trade.entrustmsgbymq.service;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.entrustmsgbymq.dao.EntrustMsgbyMQMapper;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
/**
 * 
 * 基金预清算主推接口Service
 * 〈功能详细描述〉
 * @author amyli
 * @version 1.0 2020年5月27日
 * @see FundTradePreClearingService
 * @since 1.0
 */
@Service
public class FundTradePreClearingService extends BaseService<InfContractDetailfile> {
	/**
	 * 委托成交消息主推接口Mapper
	 */
	@Autowired
	private EntrustMsgbyMQMapper entrustMsgbyMQMapper;
	/**
	 * 金融产品信息主推接口Mapper
	 */
	@Autowired
	FinProductAddMapper finProductAddMapper;

	/**
	 * 金融产品临时交易表主推接口Mapper
	 */
	@Autowired
	TrdProductTempDealMapper trdProductTempDealMapper;

	/**
	 * 金融产品临时汇总交易表
	 */
	@Autowired
	private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;

	/**
	 * 金融产品临时交易附属表主推接口Mapper
	 */
	@Autowired
	TrdProductTempDealAddMapper trdProductTempDealAddMapper;

	/**
	 * 金融产品临时划款指令主推接口Mapper
	 */
	@Autowired
	StlPayTempOrdersMapper stlPayTempOrdersMapper;
	/**
	 * 业务共通组件.资产头寸现金持仓共通组件
	 */
	@Autowired
	private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;
	/**
	 * 组合可用持仓和实际持仓查询更新
	 */
	@Autowired(required = false)
	private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

	/**
	 * 组合可用现金和实际现金查询更新
	 */
	@Autowired(required = false)
	private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
	/**
	 * 投资委托
	 */
	@Autowired(required = false)
	private BaseEtrInvestInfoService baseEtrInvestInfoService;
    /**
     * 持仓现金执行共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    
    /**
     * 委托
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

	@Autowired
	private TradePreCommonService tradePreCommonService;

	@Autowired
	private PositionRequestTradeReportService positionRequestTradeReportService;
    
    public void trdFund(TrdTempDto trdTempDto, InfContractDetailfile infContractDetailfile) throws Exception {
		// 金融产品临时交易表Entity
		TrdProductTempDeal trdProductTempDeal = new TrdProductTempDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductTempDeal, infContractDetailfile);
		// ZGCPCN-1133:需登录交易状态 modify by fuyongsheng 20220606 start
		trdProductTempDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
		// ZGCPCN-1133:需登录交易状态 modify by fuyongsheng 20220606 end
		// 金融产品交易DTO.关联系统交易编号 = 成交明细DTO.成交编号
		if (StringUtils.isNotEmpty(infContractDetailfile.getDealNo())) {
			trdProductTempDeal.setfTradeId(infContractDetailfile.getDealNo());
		}
		// 金融产品交易DTO.分支序号<==0
		trdProductTempDeal.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		// 金融产品交易DTO.金融产品类型（投管分类）<=='基金类资产'
		trdProductTempDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		// 金融产品交易DTO.交易币种<==成交明细DTO.交易币种
		trdProductTempDeal.setCcy(infContractDetailfile.getCcy());
		// 金融产品交易DTO.交易日<==成交明细DTO.交易日
		trdProductTempDeal.setTradeDate(infContractDetailfile.getTradeDate());
		// 金融产品交易DTO.交割日<==成交明细DTO.交易日
		trdProductTempDeal.setSettleDate(infContractDetailfile.getTradeDate());
		// 金融产品交易DTO.单位净价<==成交明细DTO.成交价格
		trdProductTempDeal.setUnitCprice(infContractDetailfile.getUnitCprice());

		FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(infContractDetailfile.getFinprodId());
		// 金融产品交易DTO.单位面值<==根据成交明细DTO.金融产品代码，取标的类金融产品附属信息，获取单位面值
		if (finProductAdd != null) {
			trdProductTempDeal.setParValue(finProductAdd.getFaceValue());
		}
		// 金融产品交易DTO.交易场所<==成交明细DTO.交易场所
		trdProductTempDeal.setTradeMarket(infContractDetailfile.getTradeMarket());
		// 金融产品交易DTO.交易平台<==成交明细DTO.交易平台
		trdProductTempDeal.setTradePlat(infContractDetailfile.getTradePlat());
		// 金融产品交易DTO.交易份额<==成交明细DTO.交易数量
		trdProductTempDeal.setShareAmt(infContractDetailfile.getShareAmt());
		// 金融产品交易DTO.交易金额<==成交明细DTO.交易金额
		trdProductTempDeal.setTradeAmt(infContractDetailfile.getTradeAmt());
		
	    // add by etliu 20201212 SNSTZJY-2333 【金融产品交易临时交易表】交易所日间成交数据，股票、基金的全价总额没有登录
		trdProductTempDeal.setFpriceAmt(trdProductTempDeal.getTradeAmt());
		
		// 金融产品交易DTO.随交易支付费用<==本次不计算，为0
		trdProductTempDeal.setFeeAmtPay(new BigDecimal(0));
		// 金融产品交易DTO.总交易费用<==本次不计算，为0
		trdProductTempDeal.setFeeAmt(new BigDecimal(0));
		// 金融产品交易DTO.交易方向<==成交明细DTO.交易方向
		trdProductTempDeal.setPs(infContractDetailfile.getPs());
		// 金融产品交易DTO.证券账户代码<==根据成交明细DTO.资金账户代码，取证券账户表信息，获取证券账户代码
		// 更改为 根据成交明细DTO.资金账户代码取证券账户信息里的证券账户代码
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", infContractDetailfile.getAcctNo());
        secMap.put("portfolioId", infContractDetailfile.getPortfolioId());
        secMap.put("tradeMarket", infContractDetailfile.getTradeMarket());
		String secAcctId = entrustMsgbyMQMapper.queryByAcctNo(secMap);
		if(StringUtils.isNotEmpty(secAcctId)){
			trdProductTempDeal.setSecManageAcctId(secAcctId);	
		}
		// 金融产品交易DTO.交易编号<==自动生成  根据规则
		BussinessCodeParameter codeParameter = null;
        if (baseGetBusinessId != null) {
            codeParameter = new BussinessCodeParameter();
            if (StringUtil.equals(infContractDetailfile.getPs(), DictCodeListExt2.交易方向_认购.getValue())
                || StringUtil.equals(infContractDetailfile.getPs(), DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(infContractDetailfile.getPs(), DictCodeList.交易方向_买入.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金申购);
            }
            else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            }
            trdProductTempDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            // 多组合系列户指令须登录临时交易汇总表
            if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
					&& StringUtil.isEmpty(trdTempDto.getTotleTradeId())){
				trdTempDto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
			}
        }
        else {
        	trdProductTempDeal.setTradeId(IDGenerator.generate());
			// 多组合系列户指令须登录临时交易汇总表
			if(tradePreCommonService.isMultiPortFolioEntrust(infContractDetailfile.getEntrustId())
					&& StringUtil.isEmpty(trdTempDto.getTotleTradeId())){
				trdTempDto.setTotleTradeId(IDGenerator.generate());
			}
        }
		// 金融产品交易DTO.金融产品代码<==成交明细DTO.金融产品代码
		trdProductTempDeal.setFinprodId(infContractDetailfile.getFinprodId());
		
		// 金融产品交易DTO.交易本金<==成交明细DTO.交易金额
		trdProductTempDeal.setPrinAmt(infContractDetailfile.getTradeAmt());
				
		String paBussType = null;
		String type6 = entrustMsgbyMQMapper.queryType6ById(infContractDetailfile.getFinprodId(), 0L);
		if (StringUtil.isNotEmpty(type6)) {
			if (StringUtil.equals(type6, DictCodeList.基金类资产类型_货币基金.getValue())) {
				if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_申购.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_货币基金申购申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_货币基金买入.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_赎回.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_卖出.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_货币基金卖出.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_货币基金认购申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
				}
			} else if (StringUtil.equals(type6, DictCodeList.基金类资产类型_非货币基金.getValue())) {
				if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_申购.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_非货币基金申购申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_非货币基金买入.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_赎回.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeList.交易方向_卖出.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_非货币基金卖出.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeListExt2.交易方向_认购.getValue())) {
					paBussType = DictCodeListExt3.平安业务类型_非货币基金认购申请.getValue();
				} else if (StringUtil.equals(trdProductTempDeal.getPs(), DictCodeListExt2.交易方向_转换申请.getValue())) {
					paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
				}
			}
		}
		trdProductTempDeal.setTgBusiType(paBussType);
		trdProductTempDeal.setBusiType(null == paBussType ? "BUSI_TYPE" : paBussType);
		 // 本方交易员 根据委托代码获取委托指令分发信息的指定交易员
		String trader = entrustMsgbyMQMapper.queryTradeId(infContractDetailfile.getEntrustId());
		if (trader != null) {
			trdProductTempDeal.setTrader(trader);
		}
		// 交易对手 根据委托代码获取委托指令信息的交易对手
		EtrEntrust etrEntrust = entrustMsgbyMQMapper.queryEtrEntrust(infContractDetailfile.getEntrustId());
		if (etrEntrust != null) {
			trdProductTempDeal.setCounterId(etrEntrust.getCounterId());
		}
		trdProductTempDeal.setTotleTradeId(trdTempDto.getTotleTradeId());
        // 临时交易入库
		trdProductTempDealMapper.insertSelective(trdProductTempDeal);

		// 多组合系列户指令需要登录临时汇总交易表 add by fuyongsheng 20220511 start
		// 防止重复登录
		if(StringUtil.isNotEmpty(trdTempDto.getTotleTradeId()) &&
				null == trdProductTempDealTotalMapper.selectByPrimaryKey(trdTempDto.getTotleTradeId())){
			// 写入金融产品临时汇总交易表
			TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
			BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trdProductTempDeal);
			trdProductTempDealTotalMapper.insertSelective(trdFinProductDealTotal);
		}
		// 多组合系列户指令需要登录临时汇总交易表 add by fuyongsheng 20220511 end

		// 金融产品临时交易附属表Entity
		TrdProductTempDealAdd trdProductTempDealAdd = new TrdProductTempDealAdd();
		// 金融产品交易DTO.委托代码<==成交明细DTO.委托代码
		trdProductTempDealAdd.setEntrustId(infContractDetailfile.getEntrustId());
		// 金融产品交易DTO.交易编号<==自动生成
		trdProductTempDealAdd.setTradeId(trdProductTempDeal.getTradeId());
		// 金融产品交易DTO.组合代码<==成交明细DTO.组合代码
		trdProductTempDealAdd.setPortfolioId(infContractDetailfile.getPortfolioId());
		// 金融产品交易DTO.金融产品代码<==成交明细DTO.金融产品代码
		trdProductTempDealAdd.setrFinprodId(infContractDetailfile.getFinprodId());
		// 金融产品交易DTO.生成方式<==接口接入
		trdProductTempDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
		trdProductTempDealAddMapper.insertSelective(trdProductTempDealAdd);

		// 临时划款指令表Entity
		String orderId = null;
        if (baseGetBusinessId != null) {
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            orderId = baseGetBusinessId.buildBussinessCode(bussinessCodeParameter);
        } else {
        	orderId = IDGenerator.generate();
        }
		StlPayTempOrders stlPayTempOrders = new StlPayTempOrders();
		// 划款指令DTO.指令编号<==自动生成
		stlPayTempOrders.setOrderId(orderId);
		// 划款指令DTO.结算日期<==金融产品交易DTO.结交割日
		stlPayTempOrders.setSettleDate(trdProductTempDeal.getSettleDate());
		// 划款指令DTO.本方资金账户<==成交明细DTO.资金账户代码
		stlPayTempOrders.setMyAcctNo(infContractDetailfile.getAcctNo());
		// 划款指令DTO.收付方向
		// 如果金融产品交易DTO.交易方向=买入，则为‘付’
		// 如果金融产品交易DTO.交易方向=卖出，则为‘收’
		if (trdProductTempDeal.getPs().equals(DictCodeList.交易方向_买入.getValue())) {
			stlPayTempOrders.setPs(DictCodeListExt1.收支方向_支出.getValue());
		} else if (trdProductTempDeal.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
			stlPayTempOrders.setPs(DictCodeListExt1.收支方向_收入.getValue());
		}
		// 划款指令DTO.结算金额<==金融产品交易DTO.交易金额
		stlPayTempOrders.setSettleAmt(trdProductTempDeal.getTradeAmt());
		// 划款指令DTO.交易编号<==金融产品交易DTO.交易编号
		stlPayTempOrders.setTradeId(trdProductTempDeal.getTradeId());
		// 划款指令DTO.投资组合代码<==金融产品交易DTO.组合代码

		/************** 划款指令需要设置币种 **************************/
		stlPayTempOrders.setCcy(trdProductTempDeal.getCcy());
		/*****************************************************/

		stlPayTempOrders.setPortfolioId(trdProductTempDealAdd.getPortfolioId());
		stlPayTempOrdersMapper.insertSelective(stlPayTempOrders);

//		// 构建金融产品交易Entity
//		BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
//
//		TrdProductDeal trdProductDeal = new TrdProductDeal();
//		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductTempDeal);
//		baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
//
//		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
//		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdProductTempDealAdd);
//		baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
//		List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<StlPayOrdersDto>();
//		StlPayOrdersDto StlPayOrders = new StlPayOrdersDto();
//		BeanUtil.copyPropertiesIgnoreNull(StlPayOrders, stlPayTempOrders);
//		stlPayOrdersList.add(StlPayOrders);
//		baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersList);
//		baseTradeProductDealDto.setEntrustId(trdProductTempDealAdd.getEntrustId());
//		// 3.写入类型为[实际]的组合持仓流水与组合现金流水
//		this.insertFundPosition(baseTradeProductDealDto);

		// ZGTFCP-1499:重新封装基础对象，调用头寸公共方法 modify by fuyongsheng 20220325 start
		FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
		finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
		BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductTempDeal);
		baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易

		baseTradeProductDealDto.setEntrustId(trdProductTempDealAdd.getEntrustId());

		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdProductTempDealAdd);
		baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
		// 是否来自场内交易下单成交标识
		baseTradeProductDealDto.setIsFromExchange(DictCodeList.是否_是.getValue());

		// 划款指令
		StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
		BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, stlPayTempOrders);

		baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
		finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
		finProductTradeDto.setNeedSum(true);
		// ZGTFCP-1639:查询资产类型 modify by fuyongsheng 20220328 start
		String assetType2 = entrustMsgbyMQMapper.queryAssetType1(trdProductTempDealAdd.getEntrustId());
		// ZGTFCP-1639:查询资产类型 modify by fuyongsheng 20220328 end
		finProductTradeDto.setAssetType2(assetType2);
		// 委托代码不为空处理委托代码
		finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
			String entrustId = infContractDetailfile.getEntrustId();
			if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
				item.setEntrustId(entrustId);
			}
		});

		// 调用新头寸接口 add by fuyongsheng 20220307 start
		BeanUtil.copyPropertiesIgnoreNull(trdTempDto, infContractDetailfile);
		// 关联系统交易编号
		trdTempDto.setfTradeId(infContractDetailfile.getDealNo());
		trdTempDto.setTradeId(trdProductTempDeal.getTradeId());
		// 金融产品类型 = 基金
		trdTempDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		// 交割日
		trdTempDto.setSettleDate(infContractDetailfile.getTradeDate());
		trdTempDto.setTrdProductTempDeal(trdProductTempDeal);
		trdTempDto.setTrdProductTempDealAdd(trdProductTempDealAdd);

		// 处理头寸
		tradePreCommonService.executePosition(finProductTradeDto, infContractDetailfile.getAcctNo());
		// ZGTFCP-1499:重新封装基础对象，调用头寸公共方法 modify by fuyongsheng 20220325 end
	}

	/**
	 * @description: 2.写入类型为[实际]的组合持仓流水与组合现金流水
	 * @param: [baseTradeProductDealDto,
	 *             持仓类型]
	 * @return: void
	 * @date: 2018/9/29
	 */
	public Boolean insertFundPosition(BaseTradeProductDealDto baseTradeProductDealDto) {
		DictCodeList 持仓类型 = DictCodeList.持仓类型_实际;
		boolean needNegative = false;
		List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
		List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();

		PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
		inputDto.setPositionType(持仓类型.getValue());
		inputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
		// 增加附属表dto
		inputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
		List<PtlSecPositionFlowDto> ptlSecPositionFlowDtoss = assetPositionsCashCommServiceImpl.positionDeal(inputDto);
		ptlSecPositionFlows.addAll(ptlSecPositionFlowDtoss);

		if (baseTradeProductDealDto.getStlPayOrdersList() != null && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
			PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
			cashInputDto.setPositionType(持仓类型.getValue());
			cashInputDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
			cashInputDto.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
			cashInputDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList());
			List<PtlCashPositionFlowDto> resultList = assetPositionsCashCommServiceImpl.cashDeal(cashInputDto);
			ptlCashPositionFlows.addAll(resultList);
		}
		// 现金与持仓执行共同
		baseEtrCashPositionCommService.executeEtrCashPosition(baseTradeProductDealDto, 持仓类型, needNegative,DictCodeList.是否_否.getValue(), DictCodeList.是否_否.getValue());

		PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
		ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
		ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
		// 不是逾期交易，更新持仓流水

		// 插入持仓流水
		for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
			basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
		}
		// 插入现金流水
		for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
			basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
		}
		return Boolean.TRUE;
	}

	/**
	 * description: 2.写入委托完成度
	 * 
	 * @param [baseTradeProductDealDto,
	 *            持仓类型]
	 * @param isCancel
	 * @return boolean
	 */
	private Boolean finishEntrust(BaseTradeProductDealDto baseTradeProductDealDto, boolean isCancel) {
		List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
		EtrFinishStatusDto status = new EtrFinishStatusDto();
		status.setEntrustId(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
		status.setTradeId(baseTradeProductDealDto.getTrdProductDealAdd().getTradeId());
		status.setFinishDate(baseTradeProductDealDto.getTrdProductDeal().getTradeDate());
		EtrEntrust etr = etrEntrustMapper.selectByPrimaryKey(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
		String targetType = entrustMsgbyMQMapper.queryTargetType(baseTradeProductDealDto.getTrdProductDealAdd().getEntrustId());
    	//委托是金额 更新金额
    	if(StringUtil.isNotEmpty(etr) && DictCodeListExt3.目标类型_金额.getValue().equals(targetType) && StringUtil.isNotEmpty(etr.getEntrustAmt())){
    		status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getTradeAmt());
    	}
    	//委托是数量 更新数量
    	else if(StringUtil.isNotEmpty(etr) && DictCodeListExt3.目标类型_数量.getValue().equals(targetType) && StringUtil.isNotEmpty(etr.getEntrustAmt())){
    		status.setFinishAmt(baseTradeProductDealDto.getTrdProductDeal().getShareAmt());
    	}
		status.setTrader(baseTradeProductDealDto.getTrdProductDeal().getTrader());
		baseEtrInvestInfoDtos.add(status);

		if (!baseEtrInvestInfoDtos.isEmpty() && null != baseEtrInvestInfoService) {
			boolean result = true;
			if (isCancel) {
				for (EtrFinishStatus etrFinishStatusDto : baseEtrInvestInfoDtos) {
					result = result && baseEtrInvestInfoService.cancelCompleteDelegate(etrFinishStatusDto.getEntrustId(), etrFinishStatusDto.getTradeId());
				}
			} else {
				result = baseEtrInvestInfoService.completeDelegate(baseEtrInvestInfoDtos);
			}
			return result;
		}
		return Boolean.TRUE;
	}
}
