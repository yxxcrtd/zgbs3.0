package com.joyintech.service.bankuplink.common.service;

import com.joyintech.background.component.uplink.interbank.business.dto.ResultBaseDto;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.common.dao.CstpAutoTradeMapper;
import com.joyintech.service.bankuplink.common.dao.CstpTradeMapper;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.service.bankuplink.common.dto.TrdConfirmMatchDto;
import com.joyintech.service.bankuplink.common.util.BusinessTypeUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.dao.CstpCommonMatchMapper;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonMatchService;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.EtrEntrustExt;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradSecdealDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mchange.v2.log.MLog.getLogger;

/**
 * 银行间债券下行自动录单service
 */
@Service
public class CstpSecAutoTradeService extends BaseService<TrdBondInvestTradeDto> {

	/**
	 * 日志
	 */
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(CstpSecAutoTradeService.class);

	/**
	 * 接口处理cstp
	 */
	@Autowired
	private CstpTradeMapper cstpMapper;

	/**
	 * 平台日
	 */
	@Autowired
	private IPlatformDate iPlatformDate;

	/**
	 * 交易对手
	 */
	@Autowired
	private MstCounterPartyMapper mstCounterPartyMapper;

	@Autowired
	private CstpRepoAutoTradeService repoAutoTradeService;

	@Autowired
	private CstpAutoTradeMapper autoTradeMapper;

	@Autowired
	private FinProductMapper finProductMapper;

	@Autowired
	private StlSecAcctInfoMapper secAcctInfoMapper;

	@Autowired
	private TrdMatchInfoMapper trdMatchInfoMapper;

	@Autowired
	private InterfTradSecdealMapper secMapper;

	@Autowired
	private CstpCommonMatchMapper cstpCommonMatchMapper;

	@Autowired
    private CstpCommonMatchService cstpCommonMatchService;

	public void secAutoTrade(InterfCstpTrade interfCstpTrade,InterfTradSecdeal interfTradSecdeal, ResultBaseDto resultBaseDto) {
        String matchType = DictCodeListExt3.下行交易匹配类型_自动匹配.getValue();
	    /**
		 * 入接口表(INTERF_TRAD_SECDEAL)成功后，根据原始交易编号（INTERF_TRAD_SECDEAL.FEDEALNO）匹配现券银行间报价明细表(IBU_QUOTE_BOND_DET)中的委托代码，
		 * 匹配上继续生成交易数据，匹配不上不生成交易。再判断标的是否存在，存在则进行后续操作
		 */
		// 成交编号
		String oritradeId = interfCstpTrade.getExecid();
		// 匹配委托(根据成交单编号查询债券委托信息) IBU_QUOTE_BOND
		// 上行报价完之后往外汇发，外汇返回成交编号和下发成交回报数据给投组，这两个是同步进行，这中间存在一个时间差，导致下行这边无法匹配上行报价的委托编号,这里隔1s查一次
		int QueryCount = 1;
		EtrEntrustExt entrustInfo = null;
		while (QueryCount <= 10 && entrustInfo == null) {
			logger.info("下行自动匹配（债券）第" + QueryCount + "次查询上行报价指令信息！！！");
			entrustInfo = autoTradeMapper.queryBondEntrustInfo(oritradeId);
			if(null!=entrustInfo){
				String isDzh = cstpMapper.getIsDzh(entrustInfo.getQuoteId());
				if(StringUtil.equals(isDzh,"Y")||(StringUtil.equals(isDzh,"N")&&StringUtil.isNotEmpty(entrustInfo.getPortfolioId()))){
					break;
				}
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error("下行（债券）自动录单匹配指令信息失败！！！");
				}
				QueryCount++ ;
			}
		}
		// 判断债券标的是否存在
		InterfTradSecdealDto interfCstpSecdeal = cstpMapper.selectTradeSec(interfCstpTrade.getExecid(),
				DateUtil.format(iPlatformDate.getSysDate(), DateUtil.DEFAULT_OTHER_FORMAT),
				BusinessTypeUtil.BUSINESSTYPE_BOND);
		FinProduct finProduct = finProductMapper.selectByPrimaryKey(interfCstpSecdeal.getSecid());
		if (null==finProduct) {
			// 防止有些标的的finprodId带有前缀，因此这里进行第二次查询
			finProduct = finProductMapper.selectByPrimaryKey("F01_" + interfCstpSecdeal.getSecid());
		}
		if (StringUtil.isNotEmpty(entrustInfo)
				&& StringUtil.isNotEmpty(finProduct)) {
			// 判断是否已经生成了交易
			TrdProductDeal deal = autoTradeMapper.isExistsTrade(oritradeId);
			if (!StringUtil.isNotEmpty(deal)) {
				// 交易对手不存在 -> 新建
				/*if (StringUtil.isNotEmpty(interfCstpSecdeal.getCno())
						&& autoTradeMapper.queryCountPartyByName(interfCstpSecdeal.getCno()) == null) {
					MstCounterParty mstCounterParty = new MstCounterParty();
					// 生成交易对手编号
					String bussinessId = repoAutoTradeService.genBussinessId();
					mstCounterParty.setCounterId(bussinessId);
					mstCounterParty.setCounterName(interfCstpSecdeal.getCno());
					mstCounterParty.setCounterType(BussinessDictCodeList.交易对手类型_资管产品.getValue());
					// 资管产品管理人员默认为空
					mstCounterPartyMapper.insertSelective(mstCounterParty);
				}*/
				// 组装交易实体类
				TrdBondInvestTradeDto bondTradeDto = new TrdBondInvestTradeDto();
				if(StringUtil.isNotEmpty(entrustInfo.getCpPartyId())){
					bondTradeDto.setCounterId(cstpMapper.selectCounterId(entrustInfo.getCpPartyId()));
				}
				bondTradeDto.setCounterTrader(entrustInfo.getCpTrader());
				bondTradeDto.setTrader(entrustInfo.getOuTrader());
				bondTradeDto.setPs(entrustInfo.getPs());
				// 根据证券代码查询证券账户来源,判断是否是甲类户交易
				if (StringUtil.isNotEmpty(interfCstpSecdeal.getOurdepositaccountnumber())) {
					StlSecAcctInfo stlSecAcctInfo = secAcctInfoMapper.selectByPrimaryKey(interfCstpSecdeal.getOurdepositaccountnumber());
					if (StringUtil.isNotEmpty(stlSecAcctInfo)) {
						bondTradeDto.setSecAcctSource(stlSecAcctInfo.getSecAcctSource());
					}
				}
				bondTradeDto.setfTradeId(oritradeId);
				// 委托代码
				bondTradeDto.setEntrustId(entrustInfo.getEntrustId());
				// 组合
				bondTradeDto.setPortfolioId(entrustInfo.getPortfolioId());
				// 标的主键
				bondTradeDto.setFinprodId(finProduct.getFinprodId());
				// 起息日
				bondTradeDto.setVdate(finProduct.getVdate());
				// 到期日
				bondTradeDto.setMdate(finProduct.getMdate());
				// 资产名称
				bondTradeDto.setFinprodName(finProduct.getFinprodName());
				// 登记托管机构
				bondTradeDto.setRegistOrg(finProduct.getRegistOrg());
				//报价编号   处理头寸需要
				bondTradeDto.setQuoteId(entrustInfo.getQuoteId());
				bondTradeDto.setInterfTradSecdeal(interfCstpSecdeal);
				try{
					Map<String,Object> resultMap =  restTemplate.postForObject(
						ServiceApplication.PINGAN_SERVICE_NAME+ "/invest/trade/bond/TrdBondInvestTrade002/secAutoTrade",
						bondTradeDto, Map.class);
					//登录匹配情况 ptlAllocationDtos  add by zhangnian 20210316 start
					List<PtlAllocationDto> ptlAllocations = JsonUtils.json2List(JsonUtils.object2Json(resultMap.get("ptlAllocationDtos")),PtlAllocationDto.class);
					//List<PtlAllocationDto> ptlAllocations = (List<PtlAllocationDto>)resultMap.get("ptlAllocationDtos");
					if(!StringUtil.isEmptyList(ptlAllocations)){
						ptlAllocations.forEach(item -> {
							TrdMatchInfo trdMatchInfo = new TrdMatchInfo();
							trdMatchInfo.setSeqno(cstpCommonMatchMapper.selectSeqNo(DateUtil.getSysDate()));
							trdMatchInfo.setCstpTradeId(oritradeId);
							trdMatchInfo.setEntrustId(item.getEntrustId());
							//债券匹配额度 以百元券登录
							trdMatchInfo.setMatchNum(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal("100"),2));
							trdMatchInfo.setMatchType("0");
							trdMatchInfo.setTradeDate(DateUtil.parse(interfCstpTrade.getTradedate()));
							trdMatchInfoMapper.insert(trdMatchInfo);
						});
					}
					//登录匹配情况 ptlAllocationDtos  add by zhangnian 20210316 end
					//更新交易手续费和结算手续费
					InterfTradSecdeal repoRecord = new InterfTradSecdeal();
					repoRecord.setDealno(interfTradSecdeal.getDealno());
					repoRecord.setDealFeeamt(resultMap.containsKey("dealFeeamt")?new BigDecimal((String)resultMap.get("dealFeeamt")):null);
					repoRecord.setSettFeeamt(resultMap.containsKey("settFeeamt")?new BigDecimal((String)resultMap.get("settFeeamt")):null);
					secMapper.updateByPrimaryKeySelective(repoRecord);
					// 最后修改接口表中的数据来源,用于撤销
					autoTradeMapper.updateInterfCstpTrade(oritradeId, BusinessTypeUtil.BUSINESSTYPE_BOND);
				}
				catch (Exception e){
					resultBaseDto.setErrMsg(e.getMessage());
                    logger.info("下行交易处理失败,自动生成匹配信息开始");
                    Map<String,Object> map = new HashMap<>();
                    map.put("execId",oritradeId);
                    map.put("matchType", matchType);
					//删除已经匹配的数据
					cstpCommonMatchService.deleteMatchInfo(map);
                    cstpCommonMatchService.dealMatchEntrust(map);
                    logger.info("下行交易处理失败,自动生成匹配信息结束");
					logger.error(this.getClass()+"债券成交明细接口异常:"+e.getMessage());
				}
			} else {
				String msg = "成交单编号：" + oritradeId + "，已经存在有效交易！";
				resultBaseDto.setErrMsg(msg);
				logger.info(msg);
			}
		} else {
			String msg = "无报价的指令代码或债券代码不存在！";
			resultBaseDto.setErrMsg(msg);
			logger.info(msg);
		}
	}


	/**
	 * 前后端分离版本用
	 * @param interfCstpTrade
	 * @param interfTradSecdeal
	 * @param resultBaseDto
	 */
	public void secAutoTrade2(InterfCstpTrade interfCstpTrade,InterfTradSecdeal interfTradSecdeal, ResultBaseDto resultBaseDto) {
		String oritradeId = interfCstpTrade.getExecid();
		int QueryCount = 1;
		EtrEntrustExt entrustInfo = null;
		while (QueryCount <= 10 && entrustInfo == null) {
			logger.info("下行自动匹配（债券）第" + QueryCount + "次查询上行报价指令信息！！！");
			entrustInfo = autoTradeMapper.queryBondEntrustInfo(oritradeId);
			if(null!=entrustInfo){
				String isDzh = cstpMapper.getIsDzh(entrustInfo.getQuoteId());
				if(StringUtil.equals(isDzh,"Y")||(StringUtil.equals(isDzh,"N")&&StringUtil.isNotEmpty(entrustInfo.getPortfolioId()))){
					logger.info("查询到上行报价信息");
					break;
				}
			}
			else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error("下行（债券）自动录单匹配指令信息失败！！！");
				}
				QueryCount++ ;
			}
		}
		// 判断债券标的是否存在
		InterfTradSecdealDto interfCstpSecdeal = cstpMapper.selectTradeSec(interfCstpTrade.getExecid(),
				DateUtil.format(iPlatformDate.getSysDate(), DateUtil.DEFAULT_OTHER_FORMAT),
				BusinessTypeUtil.BUSINESSTYPE_BOND);
		FinProduct finProduct = finProductMapper.selectByPrimaryKey(interfCstpSecdeal.getSecid());
		if (null==finProduct) {
			// 防止有些标的的finprodId带有前缀，因此这里进行第二次查询
			finProduct = finProductMapper.selectByPrimaryKey("F01_" + interfCstpSecdeal.getSecid());
		}
	/*	if (StringUtil.isNotEmpty(entrustInfo)
				&& StringUtil.isNotEmpty(finProduct)) {*/
		if (StringUtil.isNotEmpty(finProduct)) {
			// 判断是否已经生成了交易
			TrdProductDeal deal = autoTradeMapper.isExistsTrade(oritradeId);
			//判断该下行数据，是否已经匹配成功了。
			TrdConfirmMatchDto matchDto=cstpMapper.selectMatchDtoByExecId(oritradeId);
			if (!StringUtil.isNotEmpty(deal) && !StringUtil.isNotEmpty(matchDto)) {
				try{
					Map<String,Object> param = new HashMap<>();
					param.put("interfCstpTrade",interfCstpTrade);
					param.put("interfTradSecdeal",interfTradSecdeal);
					restTemplate.postForObject(
							ServiceApplication.PINGAN_SERVICE_NAME+ "/cfets/common/CfetsIbuQuoteBatchCommon/cstpAutoTrade",
							param, Map.class);
				}
				catch (Exception e){
					resultBaseDto.setErrMsg(e.getMessage());
					logger.error(this.getClass()+"债券成交明细接口异常:"+e.getMessage());
				}
			} else {
				String msg = "成交单编号：" + oritradeId + "，已经存在有效交易！";
				resultBaseDto.setErrMsg(msg);
				logger.info(msg);
			}
		} else {
			String msg = "无债券代码不存在！";
			resultBaseDto.setErrMsg(msg);
			logger.info(msg);
		}
	}


	/**
	 * 前后端分离版本用 一级申购自动匹配
	 * @author jiangpeng
	 * @date 2022/5/6 17:31
	 * @param interfCstpBjfxjg 一级申购下行报文
	 * @param resultBaseDto 处理结果
	 */
	public void secAutoBidTrade(InterfCstpBjfxjg interfCstpBjfxjg, ResultBaseDto resultBaseDto) {
		String oritradeId = interfCstpBjfxjg.getTrdId();
		// 判断债券标的是否存在
		FinProduct finProduct = finProductMapper.selectByPrimaryKey(interfCstpBjfxjg.getInstrumentId());
		if (null == finProduct) {
			// 防止有些标的的finprodId没带前缀，因此这里进行第二次查询
			finProduct = finProductMapper.selectByPrimaryKey("F32_" + interfCstpBjfxjg.getInstrumentId());
		}
		if (StringUtil.isNotEmpty(finProduct)) {
			// 判断是否已经生成了交易
			TrdProductDeal deal = autoTradeMapper.isExistsTrade(oritradeId);
			//判断该下行数据，是否已经匹配成功了。
			TrdConfirmMatchDto matchDto = cstpMapper.selectMatchDtoByCstpTradeId(oritradeId);
			if (!StringUtil.isNotEmpty(deal) && !StringUtil.isNotEmpty(matchDto)) {
				try {
					Map<String, Object> param = new HashMap<>();
					param.put("interfCstpBjfxjg", interfCstpBjfxjg);
					restTemplate.postForObject(
							ServiceApplication.PINGAN_SERVICE_NAME + "/cfets/common/CfetsIbuQuoteBatchCommon/cstpAutoTrade",
							param, Map.class);
				} catch (Exception e) {
					resultBaseDto.setErrMsg(e.getMessage());
					logger.error(this.getClass() + "一级申购成交明细接口异常:" + e.getMessage());
				}
			} else {
				String msg = "成交单编号：" + oritradeId + "，已经存在有效交易！";
				resultBaseDto.setErrMsg(msg);
				logger.info(msg);
			}
		} else {
			String msg = "无债券代码不存在！";
			resultBaseDto.setErrMsg(msg);
			logger.info(msg);
		}
	}

}
