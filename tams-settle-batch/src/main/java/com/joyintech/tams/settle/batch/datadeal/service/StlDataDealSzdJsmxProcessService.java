package com.joyintech.tams.settle.batch.datadeal.service;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.settle.common.dto.FundInfoDto;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import com.joyintech.tams.settle.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据清算step2 processor Service - 处理上中登JSMX
 * 
 * @author 陈卓
 * @date 2020/7/9
 * @see StlDataDealSzdJsmxProcessService
 * @since 1.0
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StlDataDealSzdJsmxProcessService {

    @Autowired(required = false)
    private StlDownDataDealService stlDownDataDealService;
	
	@Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

	@Autowired
	private SettleCommonService settleCommonService;

	@Autowired
	private TrdCommonBaseService trdCommonBaseService;

	@Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;

	@Autowired
	protected FinProductTypeMapper finProductTypeMapper;

	private Logger logger = LoggerFactory.getLogger(StlDataDealSzdJsmxProcessService.class);

	/*@Autowired
	protected ICalendar iCalendar;*/
	
	// jsmx02-上中登jsmx的债券
	public static final String[] szdJsmx02BondYwlxArray1 = new String[] {StlDataDealProcessConstant.YWLX_003, StlDataDealProcessConstant.YWLX_004, StlDataDealProcessConstant.YWLX_601, 
			StlDataDealProcessConstant.YWLX_605, StlDataDealProcessConstant.YWLX_611, StlDataDealProcessConstant.YWLX_012};
	
	// jsmx03-上中登jsmx的债券
	public static final String[] szdJsmx03BondYwlxArray1 = new String[] {StlDataDealProcessConstant.YWLX_003, StlDataDealProcessConstant.YWLX_004, StlDataDealProcessConstant.YWLX_601, 
			StlDataDealProcessConstant.YWLX_605, StlDataDealProcessConstant.YWLX_611, StlDataDealProcessConstant.YWLX_032};
	/**
	 * 生成上中登JSMX数据清算批处理step2 processor的结果Dto
	 * 
	 * @param stlDownDataDealOperatorDto StlDownDataDealOperatorDto
	 */
	public StlDownDataDealWriteDto generatorStlDownDataDealSzdJsmxWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
		// 核算信息全量数据
		Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
				// 债券数据全量获取
		List<String> bondFinprodIdList = stlDownDataDealMapper.selectAllBondFinprodId();
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
		    stlDownDataDealWriteDto = secTypeIsBondDeal(stlDownDataDealOperatorDto, map, bondFinprodIdList);
	        // 佣金计算
	        stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
		}
		if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
		    stlDownDataDealWriteDto = secTypeIsRepoDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
            // SETTLE_TRANS_DATA表数据的TRADE_AMT和TRADE_AMT_RAW数据重新设值
            if (stlDownDataDealWriteDto != null && stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList() != null && stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().size() > 0) {
            	for (int index = 0; index < stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().size(); index++) {
            		String tradeId = stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().getTradeId();
            		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {
            			if (StringUtil.equals(szdJsmx.getCjbh(), tradeId)) {
            				// modify by etliu 20211225 start
							// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//            				stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().setTradeAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
							stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().setTradeAmtRaw(new BigDecimal(szdJsmx.getSjsf()).abs());
							// modify by etliu 20211225 end
            			}
            		}
            	}
            }
		}
		if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
		    stlDownDataDealWriteDto = secTypeIsFundDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
		}
		if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
		    stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map, bondFinprodIdList);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
		}

		if (StringUtil.equals(DictCodeListStl.证券品种_信用保护凭证.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
			stlDownDataDealWriteDto = secTypeIsCertificateOfCreditProtectionDeal(stlDownDataDealOperatorDto, map, bondFinprodIdList);
			// 佣金计算
			stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
		}
		
		return stlDownDataDealWriteDto;
	}

	/**
	 * 债券类证券品种处理
	 * @param stlDownDataDealOperatorDto StlDownDataDealOperatorDto
	 * @param map Map<String, List<StlDownDataDealAccountInfoDto>>
	 * @param bondFinprodIdList List<String>
	 * @return StlDownDataDealWriteDto
	 */
	private StlDownDataDealWriteDto secTypeIsBondDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto,
                                                      Map<String, List<StlDownDataDealAccountInfoDto>> map, List<String> bondFinprodIdList) {
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {
			// 035－网上发行业务时候，债券和股票一样的，需要特殊处理
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_035, szdJsmx.getYwlx())) {
				if (!bondFinprodIdList.contains(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH")) {
					continue;
				}
			}
			
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_012, szdJsmx.getYwlx())
			    || StringUtil.equals(StlDataDealProcessConstant.YWLX_032, szdJsmx.getYwlx())) {
			    if (!StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())){
			        continue;
			    }
			}
			
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_301, szdJsmx.getYwlx())) {
                if (!StringUtil.equals(StlDataDealProcessConstant.QSBZ_280, szdJsmx.getQsbz())){
                    continue;
                }
            }
			
			// 以清算数据Entity.证券账号 + 清算数据Entity.业务单元1为KEY检索核算信息全量数据，不存在就不处理
			if (!map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh1()) && !map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh2())) {
				continue;
			}
			// 组合代码不在输入的组合代码List里面不处理
			List<StlDownDataDealAccountInfoDto> accountInfoDtolist = StringUtil.isEmptyList(map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1())) ? map.get(szdJsmx.getZqzh()+szdJsmx.getXwh2()) : map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1());
			if (accountInfoDtolist != null && accountInfoDtolist.size() > 0) {
				if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
					continue;
				}
			}
			else {
				continue;
			}
			// 清算数据DTO
			StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
			// 债券兑付和债券收息场景单独处理
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)
			&& (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_352)
					|| StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_353))){
				if ((StringUtil.equals(StlDataDealProcessConstant.QSBZ_231,szdJsmx.getQsbz())
						||StringUtil.equals(StlDataDealProcessConstant.QSBZ_232,szdJsmx.getQsbz()))
						&& StringUtil.equals(StlDataDealProcessConstant.JGDM_0000,szdJsmx.getJgdm())) {
					// 债券兑付和债券收息场景
					stlDownDataDealWriteBaseDto = dealBondCashAndInterestCollection(szdJsmx, stlDownDataDealOperatorDto,
							accountInfoDtolist);
				}
			}else{
				// 定义变量.成交数据明细DTO
				SettleTransData settleTransData = new SettleTransData();
				String generate = IDGenerator.generate();
				settleTransData.setTradeId(generate);
				settleTransData.setCjbh(szdJsmx.getCjbh());
				// 定义变量.成交数据明细附属表DTO
				SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
				// 金融产品代码
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
				// 金融产品类型(投管分类)
				settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
				// 金融产品类型(估值核算)
				settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
				// 单位全价
				settleTransData.setUnitFprice(new BigDecimal(szdJsmx.getJg1()));
				// 清算速度
				settleTransData.setSettleSpeed(0L);
				if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
					settleTransData.setSettleSpeed(1L);
				}
				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 交易份额
//				settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).divide(new BigDecimal(100)).doubleValue())));
				settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs().divide(new BigDecimal(100)));
				// 全价总额
//				settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// modify by etliu 20211225 end

				// 单位净价
				if (StringUtil.isNotEmpty(szdJsmx.getJg2())) {
					settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg2()));// 配债此字段未空，需要判断
				}
				// 交易币种
				settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdJsmx.getBz()));
				// 交收方式
				settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
				settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdJsmx.getJgdm()));
				// 交易方向
				settleTransData.setPs(getSzdJsmxBondPs(stlDownDataDealOperatorDto.getDataFile(), szdJsmx.getMmbz(), szdJsmx.getYwlx(),
						szdJsmx.getZqlb(), szdJsmx.getJllx(), szdJsmx.getQsbz(), szdJsmx.getGhlx(),szdJsmx.getJgdm()));
				// 针对债券-回售特殊处理为竞价平台
				if (StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_行权_回售.getValue())){
					settleTransData.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
				}
				// 交易份额
				if (StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_行权_回售.getValue())
						|| StringUtil.equals(settleTransData.getPs(), DictCodeListExt1.交易方向_债转股.getValue())){
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//					settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).divide(new BigDecimal(100)).doubleValue())));
					settleTransData.setShareAmt(new BigDecimal(szdJsmx.getSl()).abs().divide(new BigDecimal(100)));
					// modify by etliu 20211225 end
				}
				//交易日期
				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
				// 金融产品代码
				if (StringUtil.equals(settleTransData.getPs(), DictCodeListExt3.交易方向_配债.getValue())){
					settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm2() + ".SH");
					settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
				}
				// 业务场景明细分类
				settleTransData.setBusiSceneCalss(getSzdJsmxBondBusiSceneClass(stlDownDataDealOperatorDto.getDataFile(), szdJsmx.getMmbz(), szdJsmx.getYwlx(),
						szdJsmx.getZqlb(), szdJsmx.getJllx(), szdJsmx.getQsbz(), szdJsmx.getGhlx(),szdJsmx.getJgdm()));
				//执行日期
				settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
				// 分支序号
				settleTransData.setBranch(0L);
				//申请编号
				settleTransData.setApplyTradeId(szdJsmx.getSqbh());
				//业务类型
				settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
				// 证券品种
				settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
				// 交割日期
				settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
				//债券-可转债申购
				if (DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())
				  || DictCodeList.交易方向_申购确认.getValue().equals(settleTransData.getPs())){
					// GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 start
					settleTransData.setFinprodId(stlDownDataDealMapper.getLastFinproductIdByApprovalNo(szdJsmx.getZqdm1()));
					// GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 end
					szdJsmx.setCjbh(szdJsmx.getJsbh());
				}
				// 交易平台
				String finProductTypeType6 = getFinProductTypeType6(settleTransData);
				settleTransData.setTradePlat(null);
				if ("B14".equals(finProductTypeType6)) {
					settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdJsmx.getJyfs()));
				}
				// 债可交换债换股业务
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)) {
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_653)) {
						if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_PT, szdJsmx.getZqlb())) {
							// 金融产品代码
							settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
							// 单位全价
							settleTransData.setUnitFprice(new BigDecimal(0));
							// 单位净价
							settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg1()));
						}
						//settleTransDataAdd.setTradeId(szdJsmx.getCjbh());
						settleTransDataAdd.setTradeId(generate);
						settleTransDataAdd.setrFinprodId(szdJsmx.getZqdm2());
					}
				}
				// 国债场内分销
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02)){
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_022)) {
						// 交易方向
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						// 业务场景明细分类
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_分销_结果.getValue());
						// 金融产品代码
						settleTransData.setFinprodId(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue()+szdJsmx.getZqdm1()+".SH");
						// 金融产品类型(估值核算)
						settleTransData.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
						// 金融产品类型(投管分类)
						settleTransData.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());

					}
				}
				// 公司债场内分销
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)){
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_612)
							&&(StringUtil.equals(StlDataDealProcessConstant.JLLX_002, szdJsmx.getJllx())
							||StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx()))) {
						handleSzdJsmx(settleTransData,szdJsmx);
					}
				}
				if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_011)) {
					if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_PT, szdJsmx.getZqlb())) {
						// 金融产品代码
						settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
						// 金融产品类型(投管分类)
						settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
						// 金融产品类型(估值核算)
						settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
						//settleTransDataAdd.setTradeId(szdJsmx.getCjbh());
						settleTransDataAdd.setTradeId(generate);
						//债转股代码
						//settleTransDataAdd.setrFinprodId(szdJsmx.getZqdm2());
						//变量份额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getSl()).abs());
						// modify by etliu 20211225 end
					}
					else if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, szdJsmx.getZqlb())) {
						//settleTransDataAdd.setTradeId(szdJsmx.getCjbh());
						settleTransDataAdd.setTradeId(generate);
						//settleTransDataAdd.setrFinprodId(szdJsmx.getZqdm2());
					}
					else {
						// 金融产品代码
						//将清算数据Entity.证券代码1作为转股代码去债券标的中取对应的股票代码，再取该股对应的金融产品代码
						List<String> zzgFinprodIdList = stlDownDataDealMapper.getZzgFinprodId(szdJsmx.getZqdm1());
 						settleTransData.setFinprodId(CollectionUtils.isEmpty(zzgFinprodIdList)?null:zzgFinprodIdList.get(0));
						// 金融产品类型(投管分类)
						//settleTransData.setFinprodType2(null);
						settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
						// 金融产品类型(估值核算)
						//settleTransData.setFinprodType(null);
						settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
						// 单位全价
						settleTransData.setUnitFprice(new BigDecimal(0));
						// 单位净价
						settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg1()));
						//交易份额
						settleTransData.setShareAmt(new BigDecimal(0));
						//全价总额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// modify by etliu 20211225 end
						//settleTransDataAdd.setTradeId(szdJsmx.getCjbh());
						settleTransDataAdd.setTradeId(generate);
						//settleTransDataAdd.setrFinprodId(szdJsmx.getZqdm1());
					}
				}
				// 成交编号
				/*if (StringUtil.isNotEmpty(szdJsmx.getCjbh())) {
					settleTransData.setTradeId(szdJsmx.getCjbh());
				}
				else {
					settleTransData.setTradeId(IDGenerator.generate());
				}*/

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 印花税
				if (StringUtil.isNotEmpty(szdJsmx.getYhs())){
//					settleTransData.setStampFee(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
					settleTransData.setStampFee(new BigDecimal(szdJsmx.getYhs()).abs());
					// 印花税_原始
//					settleTransData.setStampFeeRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
					settleTransData.setStampFeeRaw(new BigDecimal(szdJsmx.getYhs()).abs());
				}
				// 经手费
				if (StringUtil.isNotEmpty(szdJsmx.getJsf())){
//					settleTransData.setJsfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
					settleTransData.setJsfAmt(new BigDecimal(szdJsmx.getJsf()).abs());
					// 经手费_原始
//					settleTransData.setJsfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
					settleTransData.setJsfAmtRaw(new BigDecimal(szdJsmx.getJsf()).abs());
				}
				// 过户费
				if (StringUtil.isNotEmpty(szdJsmx.getGhf())){
//					settleTransData.setGhfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
					settleTransData.setGhfAmt(new BigDecimal(szdJsmx.getGhf()).abs());
					// 过户费_原始
//					settleTransData.setGhfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
					settleTransData.setGhfAmtRaw(new BigDecimal(szdJsmx.getGhf()).abs());
				}
				// 证管费
				if (StringUtil.isNotEmpty(szdJsmx.getZgf())){
//					settleTransData.setZgfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
					settleTransData.setZgfAmt(new BigDecimal(szdJsmx.getZgf()).abs());
					// 证管费_原始
//					settleTransData.setZgfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
					settleTransData.setZgfAmtRaw(new BigDecimal(szdJsmx.getZgf()).abs());
				}
				// 手续费
				if (StringUtil.isNotEmpty(szdJsmx.getSxf())){
//					settleTransData.setSxfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
					settleTransData.setSxfAmt(new BigDecimal(szdJsmx.getSxf()).abs());
					// 手续费_原始
//					settleTransData.setSxfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
					settleTransData.setSxfAmtRaw(new BigDecimal(szdJsmx.getSxf()).abs());
				}
				// 交易金额
//				settleTransData.setTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// 交易金额_原始
//				settleTransData.setTradeAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
				settleTransData.setTradeAmtRaw(new BigDecimal(szdJsmx.getSjsf()).abs());
				// modify by etliu 20211225 end

				// 交易场所
				settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
				// 交易市场
				settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
				// 席位号
				settleTransData.setSeatNumber(StringUtil.isNotEmpty(szdJsmx.getXwh1()) ? szdJsmx.getXwh1() : szdJsmx.getXwh2());
				// 组合代码
				settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
				// 证券账户代码
				settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				// 资金账户
				settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
				// 临时表流水号
				settleTransData.setSeqNo(szdJsmx.getSeqno());
				// 是否优先配售
				settleTransData.setIsPurchTypeYx(DictCodeList.是否_否.getValue());
				// 清算速度:申购是T+2
				if(DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())){
					settleTransData.setSettleSpeed(2L);
				}
				if(DictCodeList.交易方向_申购确认.getValue().equals(settleTransData.getPs())){
					// 交割日期
					settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
					// 交易日期
					settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJsrq()));
					// 交易本金
					// GZYHTZJY-1107:modify by fuyongsheng 20220113 start
					settleTransData.setPrinAmt(settleTransData.getFpriceAmt());
					// GZYHTZJY-1107:modify by fuyongsheng 20220113 end
				}
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02)) {
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_012)) {
						settleTransData.setIsPurchTypeYx(DictCodeList.是否_是.getValue());
					}
				}
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)) {
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_032)) {
						settleTransData.setIsPurchTypeYx(DictCodeList.是否_是.getValue());
					}
				}
				if(DictCodeList.交易方向_买入.getValue().equals(settleTransData.getPs())
						|| DictCodeList.交易方向_卖出.getValue().equals(settleTransData.getPs())
						|| DictCodeList.交易方向_行权_回售.getValue().equals(settleTransData.getPs())){
					// 交割日期
					if(StringUtil.equals(DictCodeListStl.交收方式_非担保.getValue(), settleTransData.getSettleMode())){
						settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
					}else{
						settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
					}
				}
				// 日终过来的文件结算价格就是全价的，需要反算净价总额、应计利息总额
				if (bondFinprodIdList.contains(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH")
						|| bondFinprodIdList.contains(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm2() + ".SH")) {
					// GNS-879:配债不重新计算净价总额和应计利息总额 modify by fuyongsheng 20220217 start
					if(!DictCodeListExt3.交易方向_配债.getValue().equals(settleTransData.getPs())){
						stlDownDataDealService.calc(settleTransData);
					}
					// GNS-879:配债不重新计算净价总额和应计利息总额 modify by fuyongsheng 20220217 end
				}
				stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
				stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
				// 成交数据与投管交易关系
				SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
				settleTransTgMapping.setTradeId(settleTransData.getTradeId());
				stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
			}
			stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
		}
		
		return stlDownDataDealWriteDto;
	}

	// 公司债场内分销
	private void handleSzdJsmx(SettleTransData settleTransData, SzdJsmx szdJsmx) {
		// 交易方向
		settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
		// 非担保交收通知
		if(StringUtil.equals(StlDataDealProcessConstant.JLLX_002, szdJsmx.getJllx())){
			// 业务场景明细分类
			settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_分销_通知.getValue());
			// 交收结果
			settleTransData.setDeliveryResult(DictCodeListExt4.交收结果_交收通知.getValue());
			//非担保交收结果
		}else  if(StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx())){
			// 业务场景明细分类
			settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_分销_结果.getValue());
		}
		// 金融产品代码
		settleTransData.setFinprodId(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue()+szdJsmx.getZqdm1()+".SH");
		// 金融产品类型(估值核算)
		settleTransData.setFinprodType(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
		// 金融产品类型(投管分类)
		settleTransData.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
	}

	// 回购类证券品种处理
	private StlDownDataDealWriteDto secTypeIsRepoDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) {
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {
			// 以清算数据Entity.证券账号 + 清算数据Entity.业务单元1为KEY检索核算信息全量数据，不存在就不处理
			if (!map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh1()) && !map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh2())) {
				continue;
			}
			// 组合代码不在输入的组合代码List里面不处理
			List<StlDownDataDealAccountInfoDto> accountInfoDtolist = StringUtil.isEmptyList(map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1())) ? map.get(szdJsmx.getZqzh()+szdJsmx.getXwh2()) : map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1());
//			if (accountInfoDtolist != null && accountInfoDtolist.size() > 0) {
//				if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
//					continue;
//				}
//			}
//			else {
//				continue;
//			}
			// 定义变量.成交数据明细DTO
			SettleTransData settleTransData = new SettleTransData();
			// 流水号 = UUID
			settleTransData.setTradeId(IDGenerator.generate());
			// 成交编号
			settleTransData.setCjbh(szdJsmx.getCjbh());
			// 定义变量.成交数据明细附属表DTO
			SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
			// 定义变量.成交数据明细质押品信息表
			SettleTransDataPledge settleTransDataPledge = new SettleTransDataPledge();
			// 交易平台
			settleTransData.setTradePlat(null);
			// 交收方式
			settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
			// 交易币种
			settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
			// 交易日期(默认是交易日期,后面根据不同场景分别赋值)
			settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
			// 交割日期(默认是清算日期,后面根据不同场景分别赋值)
			settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
			// 交易场所
			settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
			// 交易市场
			settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
			// 席位号
			settleTransData.setSeatNumber(StringUtil.isNotEmpty(szdJsmx.getXwh1()) ? szdJsmx.getXwh1() : szdJsmx.getXwh2());
			// 组合代码
			settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
			// 证券账户代码
			settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
			// 资金账户
			settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
			// 计息基础
			settleTransData.setBasis(DictCodeList.计息基础_A_365.getValue());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
			// 临时表流水号
			settleTransData.setSeqNo(szdJsmx.getSeqno());
			// 申请编号
			settleTransData.setApplyTradeId(szdJsmx.getSqbh());
			// 不同交易类型数据单独处理
			// 竞价回购交易
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_023, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_602, szdJsmx.getYwlx())) {
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx())) {
					if (StringUtil.equals(StlDataDealProcessConstant.QSBZ_02A, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_00B, szdJsmx.getQsbz())) {
						if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
						}
						else {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_023, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_首期_买入.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_023, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_首期_买入.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_602, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_隔夜正回购_首期_买入.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_602, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_隔夜逆回购_首期_买入.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						// 回购类型
						settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式竞价回购.getValue());
						// 业务类型
						settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
						// 交易方向
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						// 金融产品代码
						settleTransData.setFinprodId(szdJsmx.getZqdm1());
						// 交易份额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
						// modify by etliu 20211225 end
						// 期限品种
						settleTransData.setTermType(getTermType(szdJsmx.getZqdm1()));
						// 起息日
						settleTransData.setVdate(DateUtil.parse(szdJsmx.getJyrq()));
						// 到期日
						settleTransData.setMdate(DateUtil.parse(szdJsmx.getQtrq()));
						// 首期交收日
						settleTransData.setVpayDate(DateUtil.parse(szdJsmx.getJsrq()));
						// 到期交收日
						String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
						settleTransData.setMpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getQtrq()), calendarId));
						// 交割日
						settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
						// 本金金额
						// BigDecimal bjje = new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue()));
						// 本金金额 = 成交数量 fuyongsheng 20211101 edit start
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						BigDecimal bjje = new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue()));
						BigDecimal bjje = new BigDecimal(szdJsmx.getCjsl()).abs();
						// modify by etliu 20211225 end
						// 本金金额 = 成交数量 fuyongsheng 20211101 edit end
						// 回购利息
						BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.multiply(bjje, settleTransData.getIntRate()),
								ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(), settleTransData.getMpayDate())), 365, 14));
						settleTransData.setmIntAmt(ArithmeticUtil.round(hglx, 2, RoundingMode.HALF_UP));
						settleTransData.setIntAmt(settleTransData.getmIntAmt());
						// 交易本金
						settleTransData.setPrinAmt(bjje);
						// 到期本金
						settleTransData.setmPrinAmt(bjje);
						// add 计算费用时，计算基础为成交金额
						settleTransData.setFpriceAmt(bjje);
						// 到期金额
						settleTransData.setmTradeAmt(ArithmeticUtil.doAdd(bjje, settleTransData.getmIntAmt()));
						// 数据字典：金融产品类型(估值).质押式正回购 eq 变量.金融产品类型(估值)时特殊处理
						if (StringUtil.equals(settleTransData.getFinprodType(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
							// 流水号=成交数据明细流水号
							settleTransDataPledge.setTradeId(settleTransData.getTradeId());
							settleTransDataPledge.setBranch(0L);
							settleTransDataPledge.setPledgeFinprodId(szdJsmx.getZqdm2());
							// settleTransDataPledge.setPledgeNumber(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
							// modify by etliu 20211225 start
							// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//							settleTransDataPledge.setPledgeNumber(szdJsmx.getSl() == null ? BigDecimal.ZERO : new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
							settleTransDataPledge.setPledgeNumber(szdJsmx.getSl() == null ? BigDecimal.ZERO : new BigDecimal(szdJsmx.getSl()).abs());
							// modify by etliu 20211225 end
						}
						// 单位净价
						// settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg1()));
						settleTransData.setUnitCprice(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
						//全价金额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// modify by etliu 20211225 end

					}
				}
			}
			// 账户式质押式债券回购的购回("JSMX02" eq 输入.清算数据源文件)
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02) && (StringUtil.equals(StlDataDealProcessConstant.YWLX_024, szdJsmx.getYwlx()) ||
					StringUtil.equals(StlDataDealProcessConstant.YWLX_603, szdJsmx.getYwlx()))) {
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx())) {
					if (StringUtil.equals(StlDataDealProcessConstant.QSBZ_22A, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_00C, szdJsmx.getQsbz())) {
						if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
						}
						else {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
						}
						// 业务场景分类和回购利率
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_024, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_到期.getValue());
							// 回购利率
							// settleTransData.setIntRate(new BigDecimal(szdJsmx.getJg2()));
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_024, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_到期.getValue());
							// 回购利率
							// settleTransData.setIntRate(new BigDecimal(szdJsmx.getJg2()));
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_603, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_隔夜正回购_到期.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_603, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
							// 业务场景分类
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_隔夜逆回购_到期.getValue());
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
						}
						// 回购类型
						settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式竞价回购.getValue());
						// 业务类型
						settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
						// 交易方向
						settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
						// 金融产品代码
						settleTransData.setFinprodId(szdJsmx.getZqdm1());
						// 交易份额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
						// modify by etliu 20211225 end
						// 期限品种
						settleTransData.setTermType(getTermType(szdJsmx.getZqdm1()));
						// 起息日
						settleTransData.setVdate(DateUtil.parse(szdJsmx.getJyrq()));
						// 到期日
						settleTransData.setMdate(DateUtil.parse(szdJsmx.getQtrq()));
						// 交易日期
						settleTransData.setTradeDate(settleTransData.getMdate());
						// 交割日期
						settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
						// 首期交收日
						String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
						settleTransData.setVpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getJyrq()), calendarId));
						// 到期交收日
						settleTransData.setMpayDate(DateUtil.parse(szdJsmx.getJsrq()));
						// 本金金额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						BigDecimal bjje = new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue()));
						BigDecimal bjje = new BigDecimal(szdJsmx.getCjsl()).abs();
						// modify by etliu 20211225 end

						// 到期金额
						settleTransData.setmTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// 计算回购利息 fuyongsheng 20211215 add start
						// 回购利息
//						BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.multiply(bjje, settleTransData.getIntRate()),
//								ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(), settleTransData.getMpayDate())), 365, 14));
//						settleTransData.setmIntAmt(ArithmeticUtil.round(hglx, 2, RoundingMode.HALF_UP));
						settleTransData.setmIntAmt(ArithmeticUtil.subtract(settleTransData.getmTradeAmt(), bjje));
						settleTransData.setIntAmt(settleTransData.getmIntAmt());
						// 计算回购利息 fuyongsheng 20211215 add end
						// 交易本金
						settleTransData.setPrinAmt(bjje);
						// 到期本金
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						settleTransData.setmPrinAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						settleTransData.setmPrinAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
						// modify by etliu 20211225 end
						// 单位净价
						settleTransData.setUnitCprice(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg2()), new BigDecimal(100), 8));
						// 竞价回购到期不登录applyTradeId
						settleTransData.setApplyTradeId(null);
					}
				}
			}
			// 债券质押式协议回购业务、报价回购-回购业务
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_619, szdJsmx.getYwlx()) ||
					/*StringUtil.equals(StlDataDealProcessConstant.YWLX_117, szdJsmx.getYwlx()) ||*/
					StringUtil.equals(StlDataDealProcessConstant.YWLX_615, szdJsmx.getYwlx())) {

				settleTransData.setPrinAmt(abs(szdJsmx.getQsje()));
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx())) {
					if (StringUtil.equals(StlDataDealProcessConstant.JYFS_106, szdJsmx.getJyfs())) {
//						 if (StringUtil.equals(StlDataDealProcessConstant.QSBZ_02C, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_02B, szdJsmx.getQsbz())) {
							// 回购类型
							settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式协议回购.getValue());
							// 业务类型
							settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
							if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								// 金融产品类型(估值核算)
								settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
								// 金融产品类型(投管分类)
								settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
							}
							else {
								// 金融产品类型(估值核算)
								settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
								// 金融产品类型(投管分类)
								settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
							}
							// 业务场景分类和交易方向
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_619, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue());
								settleTransData.setApplyTradeId(settleTransData.getCjbh());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_619, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue());
								settleTransData.setApplyTradeId(settleTransData.getCjbh());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_615, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期续做.getValue());
//								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期续作.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue());
								settleTransData.setApplyTradeId(settleTransData.getCjbh());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_615, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期续做.getValue());
//								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue());
								settleTransData.setApplyTradeId(settleTransData.getCjbh());
							}
							// GZYHTZJY-1086: modify by fuyongsheng 20220111 start
							settleTransData.setfCjbh(settleTransData.getCjbh());
							// GZYHTZJY-1086: modify by fuyongsheng 20220111 end
							/*if (StringUtil.equals(StlDataDealProcessConstant.YWLX_117, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价正回购_首期_买入.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_117, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价逆回购_首期_买入.getValue());
							}*/
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
							// 金融产品代码
							settleTransData.setFinprodId(szdJsmx.getZqdm1());
							// 期限品种
					    	settleTransData.setTermType(getTermType(szdJsmx.getZqdm1()));
							// 交易份额
//							settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//						    settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// modify by etliu 20211225 end
					    	// 起息日
							settleTransData.setVdate(DateUtil.parse(szdJsmx.getJyrq()));
							// 到期日
							settleTransData.setMdate(DateUtil.parse(szdJsmx.getQtrq()));
							// 首期交收日
							settleTransData.setVpayDate(DateUtil.parse(szdJsmx.getJsrq()));
							// 到期交收日
							String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
							// settleTransData.setMpayDate(stlDataDealProcessCommonService.getNextWorkDay(com.joyintech.framework.util.DateUtil.parse(szdJsmx.getQtrq()), calendarId));
							// 0：工作日
							String isHoliday = stlDataDealProcessCommonService.getIsHoliday(DateUtil.parse(szdJsmx.getQtrq()), calendarId);
							if("0".equals(isHoliday)){
								settleTransData.setMpayDate(DateUtil.parse(szdJsmx.getQtrq()));
							}else{
								settleTransData.setMpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getQtrq()), calendarId));
							}
							// 本金金额
//							BigDecimal bjje = new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue()));
							// 回购利息
//							BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(bjje, settleTransData.getIntRate()), 100, 2),
//									ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getMpayDate(), settleTransData.getVpayDate())), 365, 14));
//							settleTransData.setmIntAmt(ArithmeticUtil.round(hglx, 2, RoundingMode.HALF_UP));
//							settleTransData.setIntAmt(settleTransData.getmIntAmt());
							// 到期本金
//							settleTransData.setmPrinAmt(bjje);
//							settleTransData.setFpriceAmt(bjje);
						    settleTransData.setmPrinAmt(BigDecimal.ZERO);
							//全价金额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//							settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// modify by etliu 20211225 end
							// 到期金额
//							settleTransData.setmTradeAmt(ArithmeticUtil.doAdd(bjje, settleTransData.getmIntAmt()));
						    settleTransData.setmTradeAmt(BigDecimal.ZERO);
						    //清算数据Entity.申请编号 = 清算数据Entity.成交编号
                            szdJsmx.setSqbh(szdJsmx.getCjbh());
						    //清算数据Entity.交易日期 = 清算数据Entity.清算日期
						    szdJsmx.setJyrq(szdJsmx.getQsrq());
						    // 质押品信息
							// settleTransDataPledge.setTradeId(szdJsmx.getCjbh());
							// 流水号 = 成交数据明细流水号
							settleTransDataPledge.setTradeId(settleTransData.getTradeId());
							settleTransDataPledge.setBranch(0L);
							settleTransDataPledge.setPledgeFinprodId("F01_" + szdJsmx.getZqdm2() + ".SH");
//							settleTransDataPledge.setPledgeNumber(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));

						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
						    //质押数量
//						    settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())),BigDecimal.valueOf(100),2));
						settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getCjsl()).abs(), BigDecimal.valueOf(100),2));
							//质押率
//						    settleTransDataPledge.setPledgeRatio(ArithmeticUtil.divide(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())),new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())),2));
						settleTransDataPledge.setPledgeRatio(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getQsje()).abs(), new BigDecimal(szdJsmx.getCjsl()).abs(),2));
						// modify by etliu 20211225 end
//						}
					}
				}
			}
			// 债券质押式协议回购业务、报价回购-回购购回业务
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_613, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_614, szdJsmx.getYwlx()) ||
					/*StringUtil.equals(StlDataDealProcessConstant.YWLX_118, szdJsmx.getYwlx()) ||*/
					StringUtil.equals(StlDataDealProcessConstant.YWLX_617, szdJsmx.getYwlx())) {
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx())) {
					if (StringUtil.equals(StlDataDealProcessConstant.JYFS_106, szdJsmx.getJyfs())) {
//						if (StringUtil.equals(StlDataDealProcessConstant.QSBZ_22C, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_22B, szdJsmx.getQsbz())) {
							// 回购类型
							settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式协议回购.getValue());
							// 业务类型
							settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
							if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								// 金融产品类型(估值核算)
								settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
								// 金融产品类型(投管分类)
								settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
							}
							else {
								// 金融产品类型(估值核算)
								settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
								// 金融产品类型(投管分类)
								settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
							}
							// 业务场景分类和交易方向
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_613, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_613, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_614, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
//								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setPs(DictCodeList.交易方向_到期续做.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_614, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
//								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setPs(DictCodeList.交易方向_到期续做.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_617, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeListExt3.交易方向_投资端_提前终止.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_提取终止.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_617, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeListExt3.交易方向_投资端_提前终止.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_提取终止.getValue());
							}
							/*if (StringUtil.equals(StlDataDealProcessConstant.YWLX_118, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价正回购_到期.getValue());
							}
							if (StringUtil.equals(StlDataDealProcessConstant.YWLX_118, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
								settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价逆回购_到期.getValue());
							}*/
							// 金融产品代码
							settleTransData.setFinprodId(szdJsmx.getZqdm1());
							// 交易份额
						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//							settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
						// modify by etliu 20211225 end
							// GZYHTZJY-1062:赋值交易本金 modify by fuyongsheng 20220106 start
							settleTransData.setPrinAmt(settleTransData.getShareAmt());
							// GZYHTZJY-1062:赋值交易本金 modify by fuyongsheng 20220106 end
							// 期限品种
							settleTransData.setTermType(getTermType(szdJsmx.getZqdm1()));
							// 回购利率
							settleTransData.setIntRate(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getJg1()), new BigDecimal(100), 8));
							//单位净价
							settleTransData.setUnitCprice(settleTransData.getIntRate());
							// 起息日
							settleTransData.setVdate(DateUtil.parse(szdJsmx.getQtrq()));
							// 到期日
							settleTransData.setMdate(DateUtil.parse(szdJsmx.getJyrq()));
							// 首期交收日
							String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
							// settleTransData.setVpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getQtrq()), calendarId));
							String isHoliday = stlDataDealProcessCommonService.getIsHoliday(DateUtil.parse(szdJsmx.getQtrq()), calendarId);
							if("0".equals(isHoliday)){
								settleTransData.setVpayDate(DateUtil.parse(szdJsmx.getQtrq()));
							}else{
								settleTransData.setVpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getQtrq()), calendarId));
							}
							// 到期交收日
							settleTransData.setMpayDate(DateUtil.parse(szdJsmx.getJsrq()));

							// 查询首期清算数据
							//首期_成交编号
							settleTransData.setfCjbh(szdJsmx.getSqbh());
					        // 回购利息
							// settleTransData.setmIntAmt(new BigDecimal(szdJsmx.getQsje()).abs().subtract(new BigDecimal(szdJsmx.getCjsl()).abs()));
							// 查询首期清算数据
							List<SettleTransData> sqSettleTransDatas = trdCommonBaseService.selectSqSettleTransDatas(
									settleTransData.getfCjbh(), settleTransData.getSeatNumber(), settleTransData.getSecAcctId(),
									settleTransData.getVdate());
							if(!CollectionUtils.isEmpty(sqSettleTransDatas)){
								SettleTransData sqSettleTransData = sqSettleTransDatas.get(0);
								settleTransData.setmIntAmt(new BigDecimal(szdJsmx.getQsje()).abs().subtract(sqSettleTransData.getPrinAmt()));
							}
							// 利息总额
							// GZYHTZJY-1062:赋值利息总额 fuyongsheng modify 20220106 start
							settleTransData.setIntAmt(settleTransData.getmIntAmt());
							// GZYHTZJY-1062:赋值利息总额 fuyongsheng modify 20220106 end
							// settleTransData.setUnitInt(ArithmeticUtil.subtract(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())),new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue()))));

						// modify by etliu 20211225 start
						// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
							//到期本金
//					        settleTransData.setmPrinAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						settleTransData.setmPrinAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
                            // 到期金额
//							settleTransData.setmTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setmTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
							// 交易日期
							settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
							//全价总额
//						    settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
						// modify by etliu 20211225 end

						    //申请编号 = 成交编号
							szdJsmx.setSqbh(szdJsmx.getCjbh());
							//交易日期 = 清算日期
						    szdJsmx.setJyrq(szdJsmx.getQsrq());
							// 质押品信息
							// settleTransDataPledge.setTradeId(szdJsmx.getCjbh());
							// 流水号 = 成交数据明细流水号
							/*settleTransDataPledge.setTradeId(settleTransData.getTradeId());
							settleTransDataPledge.setBranch(0L);
							settleTransDataPledge.setPledgeFinprodId(szdJsmx.getZqdm2());
							settleTransDataPledge.setPledgeNumber(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));*/
//						}
					}
				}
			}
			// 债券质押式协议回购业务、报价回购-换券、解除质押
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_616, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_618, szdJsmx.getYwlx())) {
				if (StringUtil.equals(StlDataDealProcessConstant.JYFS_106, szdJsmx.getJyfs())) {
//					if (StringUtil.equals(StlDataDealProcessConstant.QSBZ_22C, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_22B, szdJsmx.getQsbz())) {
						// 回购类型
						settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式协议回购.getValue());
						// 业务类型
						settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
						if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
						}
						else {
							// 金融产品类型(估值核算)
							settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
							// 金融产品类型(投管分类)
							settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
						}
						// 业务场景分类和交易方向
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_616, szdJsmx.getYwlx())
								&& StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())
						) {
							settleTransData.setPs(DictCodeListExt3.交易方向_中途换券.getValue());
							if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00C, szdJsmx.getGhlx())){
								 settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换入.getValue());
								 settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换入.getValue());
							}else if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00D, szdJsmx.getGhlx())){
								 settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换出.getValue());
								 settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
							}
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_616, szdJsmx.getYwlx())
								&& StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
//							settleTransData.setPs(DictCodeListStl.交易方向_换券.getValue());
							settleTransData.setPs(DictCodeListExt3.交易方向_中途换券.getValue());
                            if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00C, szdJsmx.getGhlx())){
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_换券_换入.getValue());
								settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换入.getValue());
							}else if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00D, szdJsmx.getGhlx())){
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_换券_换出.getValue());
								settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
							}
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_618, szdJsmx.getYwlx())
								&& StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							// GZYHTZJY-1008:交易方向应为解除质押 fuyongsheng 20211230 modify start
							// settleTransData.setPs(DictCodeListExt3.交易方向_中途换券.getValue());
							settleTransData.setPs(DictCodeListExt3.交易方向_解除质押.getValue());
							// GZYHTZJY-1008:交易方向应为解除质押 fuyongsheng 20211230 modify end
							if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00D, szdJsmx.getGhlx())){
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_解除质押.getValue());
								settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
							}
//							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_逾期返售解除质押.getValue());
						}
						if (StringUtil.equals(StlDataDealProcessConstant.YWLX_618, szdJsmx.getYwlx())
								&& StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
//							settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
							// GZYHTZJY-1008:交易方向应为解除质押 fuyongsheng 20211230 modify start
							// settleTransData.setPs(DictCodeListExt3.交易方向_中途换券.getValue());
							settleTransData.setPs(DictCodeListExt3.交易方向_解除质押.getValue());
							// GZYHTZJY-1008:交易方向应为解除质押 fuyongsheng 20211230 modify end
							if (StringUtil.equals(StlDataDealProcessConstant.GHLX_00D, szdJsmx.getGhlx())){
								settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_解除质押.getValue());
								settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
							}
//							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_逾期返售解除质押.getValue());
						}
						// 金融产品代码
						settleTransData.setFinprodId(szdJsmx.getZqdm1());
						// 交易份额
//						settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
						// GZYHTZJY-1008:modify by fuyongsheng 20211230 start
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//					    settleTransData.setShareAmt(ArithmeticUtil.divide(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())),BigDecimal.valueOf(100),2));
					 	settleTransData.setShareAmt(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getCjsl()).abs(), BigDecimal.valueOf(100),2));
//						settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
					// modify by etliu 20211225 end
						// GZYHTZJY-1008:modify by fuyongsheng 20211230 end
					    // 期限品种
						settleTransData.setTermType(getTermType(szdJsmx.getZqdm1()));
						// 回购利率
//						settleTransData.setIntRate(new BigDecimal(szdJsmx.getJg1()));
						// 起息日
						settleTransData.setVdate(DateUtil.parse(szdJsmx.getQtrq()));
						// 到期日
//						settleTransData.setMdate(DateUtil.parse(szdJsmx.getJyrq()));
						// 首期交收日
//						String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
//						settleTransData.setVpayDate(stlDataDealProcessCommonService.getNextWorkDay(DateUtil.parse(szdJsmx.getQtrq()), calendarId));
						// 到期交收日
//						settleTransData.setMpayDate(DateUtil.parse(szdJsmx.getJsrq()));
						// 到期金额
//						settleTransData.setmTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
						//变量.首期_成交编号 = 清算数据Entity.申请编号
						settleTransData.setfCjbh(szdJsmx.getSqbh());
				        //清算数据Entity.申请编号 = 清算数据Entity.成交编号
					    szdJsmx.setSqbh(szdJsmx.getCjbh());
						//清算数据Entity.交易日期 = 清算数据Entity.清算日期
					    szdJsmx.setJyrq(szdJsmx.getQsrq());
					    //清算数据Entity.清算日期 = 清算数据Entity.交收日期
					    szdJsmx.setQsrq(szdJsmx.getJsrq());
					    // 质押品信息
						// settleTransDataPledge.setTradeId(szdJsmx.getCjbh());
						// 流水号 = 成交数据明细流水号
						settleTransDataPledge.setTradeId(settleTransData.getTradeId());
						settleTransDataPledge.setBranch(0L);
						settleTransDataPledge.setPledgeFinprodId("F01_" + szdJsmx.getZqdm2() + ".SH");
						//质押数量
//						settleTransDataPledge.setPledgeNumber(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
					    // settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(settleTransData.getShareAmt(),BigDecimal.valueOf(100),2));
						settleTransDataPledge.setPledgeNumber(settleTransData.getShareAmt());
						// 换出折算率=首期质押券折算率,换入折算率=首期成交明细本金金额/成交数量 modify by fuyongsheng 20220104 start
						if(DictCodeListExt3.质押券置换_置换方向_换出.getValue().equals(settleTransData.getTradeType())){
							// 查询首期质押券信息
							List<SettleTransDataPledge> sqSettleTransDataPledges = stlDownDataDealMapper.selectSqSettleTransDataPledges(
									settleTransData.getfCjbh(), settleTransData.getSeatNumber(), settleTransData.getSecAcctId(),
									settleTransData.getVdate());
							if(org.apache.commons.collections.CollectionUtils.isNotEmpty(sqSettleTransDataPledges)
							&& StringUtil.isNotEmpty(sqSettleTransDataPledges.get(0).getPledgeRatio())){
								settleTransDataPledge.setPledgeRatio(sqSettleTransDataPledges.get(0).getPledgeRatio());
							}
						}else if(DictCodeListExt3.质押券置换_置换方向_换入.getValue().equals(settleTransData.getTradeType())){
							// 查询首期清算数据
							List<SettleTransData> sqSettleTransDatas = trdCommonBaseService.selectSqSettleTransDatas(
									settleTransData.getfCjbh(), settleTransData.getSeatNumber(), settleTransData.getSecAcctId(),
									settleTransData.getVdate());
							if(!CollectionUtils.isEmpty(sqSettleTransDatas)){
								settleTransDataPledge.setPledgeRatio(ArithmeticUtil.divide(sqSettleTransDatas.get(0).getPrinAmt().abs(),
										new BigDecimal(szdJsmx.getCjsl()).abs(),2));
							}
						}
						// 换出折算率=首期质押券折算率,换入折算率=首期成交明细本金金额/成交数量 modify by fuyongsheng 20220104 end
						//置换方向
					    settleTransDataPledge.setDisplaceDirection(settleTransData.getTradeType());
//					}
				}
			}
			// 债券质押式协议回购业务、报价回购-质押券出库
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_025, szdJsmx.getYwlx())
				|| StringUtil.equals(StlDataDealProcessConstant.YWLX_026, szdJsmx.getYwlx())) {
				if (StringUtil.equals(StlDataDealProcessConstant.JYFS_104, szdJsmx.getJyfs())
				   || StringUtil.equals(StlDataDealProcessConstant.JYFS_003, szdJsmx.getJyfs())
				   || StringUtil.equals(StlDataDealProcessConstant.JYFS_004, szdJsmx.getJyfs())) {
					// GNS-905:jgdm字段取错 modify by fuyongsheng 20220219 start
//					if (StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, szdJsmx.getJsbh())
//							|| StringUtil.equals(StlDataDealProcessConstant.JGDM_0100, szdJsmx.getJsbh())) {
					if (StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, szdJsmx.getJgdm())
							|| StringUtil.equals(StlDataDealProcessConstant.JGDM_0100, szdJsmx.getJgdm())) {
						// GNS-905:jgdm字段取错 modify by fuyongsheng 20220219 end
					   if (!StringUtil.equals(StlDataDealProcessConstant.BZJDM_888880, szdJsmx.getZqdm2())) {
						   // GNS-905:修改质押券出入库的回购类型和业务类型 modify by fuyongsheng 20220219 start
						   //证券品种
						   settleTransData.setSecType(DictCodeListExt1.证券品种_债券.getValue());
						   // 回购类型
						   // settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式协议回购.getValue());
						   // 业务类型
						   // settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
						   settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
						   // GNS-905:修改质押券出入库的回购类型和业务类型 modify by fuyongsheng 20220219 end
						   // 金融产品类型(估值核算)
						   settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
						   // 金融产品类型(投管分类)
						   settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
						   // 业务场景分类和交易方向
						   if (StringUtil.equals(StlDataDealProcessConstant.YWLX_026, szdJsmx.getYwlx())
								   && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
							   settleTransData.setPs(DictCodeListExt3.交易方向_质押券出库.getValue());
							   settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_质押券出库.getValue());
						   }
						   if (StringUtil.equals(StlDataDealProcessConstant.YWLX_025, szdJsmx.getYwlx())
								   && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
//							settleTransData.setPs(DictCodeListStl.交易方向_换券.getValue());
							   settleTransData.setPs(DictCodeListExt3.交易方向_质押券入库.getValue());
							   settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_质押券入库.getValue());
						   }
						   // 金融产品代码
						   // GNS-905:金融产品代码入错 modify by fuyongsheng 20220219 start
						   // settleTransData.setFinprodId(szdJsmx.getZqdm2());
						   settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm2() + ".SH");
						   // GNS-905:金融产品代码入错 modify by fuyongsheng 20220219 end
						   // 交易份额
						   // modify by etliu 20211225 start
						   // 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
// 						   settleTransData.setShareAmt(ArithmeticUtil.divide(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())), BigDecimal.valueOf(100), 2));
						   settleTransData.setShareAmt(ArithmeticUtil.divide(new BigDecimal(szdJsmx.getCjsl()).abs(), BigDecimal.valueOf(100), 2));
						   // modify by etliu 20211225 end
						   //清算数据Entity.交易日期 = 清算数据Entity.清算日期
						   szdJsmx.setJyrq(szdJsmx.getQsrq());
						   //清算数据Entity.清算日期 = 清算数据Entity.交收日期
						   szdJsmx.setQsrq(szdJsmx.getJsrq());
						   // 质押品信息
						   // settleTransDataPledge.setTradeId(szdJsmx.getCjbh());
						   // 流水号 = 成交数据明细流水号
						   settleTransDataPledge.setTradeId(settleTransData.getTradeId());
						   settleTransDataPledge.setBranch(0L);
						   settleTransDataPledge.setPledgeFinprodId(szdJsmx.getZqdm2());
						   //质押数量
 						   settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(settleTransData.getShareAmt(), BigDecimal.valueOf(100), 2));

					   }
					   // GNS-905:标准券不处理，不入成交明细表 modify by fuyongsheng 20220219 start
					   else{
						   continue;
					   }
						// GNS-905:标准券不处理，不入成交明细表 modify by fuyongsheng 20220219 end
				   }
				}
			}

//			// 成交编号
//			if (StringUtil.isNotEmpty(szdJsmx.getCjbh())) {
//				settleTransData.setTradeId(szdJsmx.getCjbh());
//			}
//			else {
//				settleTransData.setTradeId(IDGenerator.generate());
//			}
//			// 债券质押式协议回购到期续做（合约新开）特殊处理
//			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_615, szdJsmx.getYwlx())) {
//				settleTransData.setTradeId(szdJsmx.getCjbh() + "X");
//				settleTransData.setTradeIdRaw(szdJsmx.getCjbh());
//			}
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 分支序号
			settleTransData.setBranch(0L);
			// 申请编号
			settleTransData.setApplyTradeId(szdJsmx.getSqbh());
			// modify by etliu 20211225 start
			// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
			// 印花税
			if (StringUtil.isNotEmpty(szdJsmx.getYhs())){
//				settleTransData.setStampFee(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
				settleTransData.setStampFee(new BigDecimal(szdJsmx.getYhs()).abs());
				// 印花税_原始
//				settleTransData.setStampFeeRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
				settleTransData.setStampFeeRaw(new BigDecimal(szdJsmx.getYhs()).abs());
			}
			// 经手费
			if (StringUtil.isNotEmpty(szdJsmx.getJsf())){
//				settleTransData.setJsfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
				settleTransData.setJsfAmt(new BigDecimal(szdJsmx.getJsf()).abs());
				// 经手费_原始
//				settleTransData.setJsfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
				settleTransData.setJsfAmtRaw(new BigDecimal(szdJsmx.getJsf()).abs());
			}
			// 过户费
			if (StringUtil.isNotEmpty(szdJsmx.getGhf())){
//				settleTransData.setGhfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
				settleTransData.setGhfAmt(new BigDecimal(szdJsmx.getGhf()).abs());
				// 过户费_原始
//				settleTransData.setGhfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
				settleTransData.setGhfAmtRaw(new BigDecimal(szdJsmx.getGhf()).abs());
			}
			// 证管费
			if (StringUtil.isNotEmpty(szdJsmx.getZgf())){
//				settleTransData.setZgfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
				settleTransData.setZgfAmt(new BigDecimal(szdJsmx.getZgf()).abs());
				// 证管费_原始
//				settleTransData.setZgfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
				settleTransData.setZgfAmtRaw(new BigDecimal(szdJsmx.getZgf()).abs());
			}
			// 手续费
			if (StringUtil.isNotEmpty(szdJsmx.getSxf())){
//				settleTransData.setSxfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
				settleTransData.setSxfAmt(new BigDecimal(szdJsmx.getSxf()).abs());
				// 手续费_原始
//				settleTransData.setSxfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
				settleTransData.setSxfAmtRaw(new BigDecimal(szdJsmx.getSxf()).abs());
			}
			// 交易金额
//			settleTransData.setTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
			if(StringUtil.isNotEmpty(szdJsmx.getQsje())){
				settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// 交易金额_原始
//			settleTransData.setTradeAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setTradeAmtRaw(new BigDecimal(szdJsmx.getQsje()).abs());
				// modify by etliu 20211225 end
			}

			StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
			stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
			// settleTransDataAdd.setTradeId(szdJsmx.getCjbh());
			// 成交编号= 成交数据明细流水号
			settleTransDataAdd.setTradeId(settleTransData.getTradeId());
			settleTransDataAdd.setSeRepDayType(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_SE_REP_DAY_TYPE));
			stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
			// 成交数据明细质押品信息表
			if(null != settleTransDataPledge && StringUtils.isNotEmpty(settleTransDataPledge.getTradeId())){
				stlDownDataDealWriteBaseDto.setSettleTransDataPledgeList(Arrays.asList(settleTransDataPledge));
			}
			// 成交数据与投管交易关系
			SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
			settleTransTgMapping.setTradeId(settleTransData.getTradeId());
			stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
			stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
		}
		return stlDownDataDealWriteDto;
	}

	// 基金类证券品种处理
	private StlDownDataDealWriteDto secTypeIsFundDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) {
		// 全量基金信息获取
		Map<String, FundInfoDto> fundInfoDtoMap = settleCommonService.getAllFundInfoDto();
		
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {
			// 以清算数据Entity.证券账号 + 清算数据Entity.业务单元1为KEY检索核算信息全量数据，不存在就不处理
			if (!map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh1()) && !map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh2())) {
				continue;
			}
			// 组合代码不在输入的组合代码List里面不处理
			List<StlDownDataDealAccountInfoDto> accountInfoDtolist = StringUtil.isEmptyList(map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1())) ? map.get(szdJsmx.getZqzh()+szdJsmx.getXwh2()) : map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1());
			if (accountInfoDtolist != null && accountInfoDtolist.size() > 0) {
				if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
					continue;
				}
			}
			else {
				continue;
			}
			// 基金Map不存在则不处理
			if (!fundInfoDtoMap.containsKey(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH")) {
				continue;
			}
			// 定义变量.成交数据明细DTO
			SettleTransData settleTransData = new SettleTransData();
			// 金融产品代码
			settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
			// 金融产品类型(投管分类)
			settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
			// 金融产品类型(估值核算)
			settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
			FundInfoDto fundInfoDto = fundInfoDtoMap.get(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
			if (fundInfoDto != null) {
				// 业务类型
				if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
					settleTransData.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
				}
				else {
					settleTransData.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
				}
				// 交易币种
				settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdJsmx.getBz()));
				// 交易平台
				settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdJsmx.getJyfs()));
				// 交收方式
				settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
				settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdJsmx.getJgdm()));
				// 业务场景分类和交易方向
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02) && (StringUtil.equals(StlDataDealProcessConstant.YWLX_002, szdJsmx.getYwlx()) || 
						StringUtil.equals(StlDataDealProcessConstant.YWLX_034, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_104, szdJsmx.getYwlx()))) {
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_002, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_买入.getValue());
					}
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_002, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_卖出.getValue());
					}
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_034, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_分级LOF_买入.getValue());
					}
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_034, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_分级LOF_卖出.getValue());
					}
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_104, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_买入.getValue());
					}
					if (StringUtil.equals(StlDataDealProcessConstant.YWLX_104, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())) {
						settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_卖出.getValue());
					}
				}
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) && StringUtil.equals(StlDataDealProcessConstant.YWLX_351, szdJsmx.getYwlx())
						&& StringUtil.equals(StlDataDealProcessConstant.ZQLB_JJ, szdJsmx.getZqlb()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_230, szdJsmx.getQsbz())) {
					settleTransData.setPs(DictCodeList.交易方向_现金分红.getValue());
					if (StringUtil.isNotEmpty(fundInfoDto.getType7())) {
						if (StringUtil.equals(DictCodeListExt2.交易所基金类型_ETF.getValue(), fundInfoDto.getType7())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeListExt2.交易所基金类型_LOF.getValue(), fundInfoDto.getType7())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeListExt2.交易所基金类型_分级基金.getValue(), fundInfoDto.getType7())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_分级LOF_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeListExt2.交易所基金类型_封闭式基金.getValue(), fundInfoDto.getType7())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue(), fundInfoDto.getType7())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_实时申赎货币基金_分红_现金分红.getValue());
						}
					}
					else {
						if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_货币基金_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeList.基金类型_债券基金.getValue(), fundInfoDto.getType1()) || StringUtil.equals(DictCodeList.基金类型_股票基金.getValue(), fundInfoDto.getType1()) ||
								StringUtil.equals(DictCodeList.基金类型_混合基金.getValue(), fundInfoDto.getType1()) || StringUtil.equals(DictCodeList.基金类型_私募基金.getValue(), fundInfoDto.getType1()) ||
								StringUtil.equals(DictCodeList.基金类型_其他公募基金.getValue(), fundInfoDto.getType1())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_非货币基金_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeList.基金类型_ETF.getValue(), fundInfoDto.getType1())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_分红_现金分红.getValue());
						}
						if (StringUtil.equals(DictCodeList.基金类型_LOF.getValue(), fundInfoDto.getType1())) {
							settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_分红_现金分红.getValue());
						}
					}
				}
//				// 成交编号
//				if (StringUtil.isNotEmpty(szdJsmx.getCjbh())) {
//					settleTransData.setTradeId(szdJsmx.getCjbh());
//				}
//				else {
//					settleTransData.setTradeId(IDGenerator.generate());
//				}
				// 流水号
				settleTransData.setTradeId(IDGenerator.generate());
				// 成交编号
				settleTransData.setCjbh(szdJsmx.getCjbh());
				// 执行日期
				settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
				// 证券品种
				settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
				// 申请编号
				settleTransData.setApplyTradeId(szdJsmx.getSqbh());
				// 分支序号
				settleTransData.setBranch(0L);
				// 交易日期
				// 交割日期
				if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), settleTransData.getPs()) || StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), settleTransData.getPs())) {
					settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
					settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
				}
				else {
					settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJsrq()));
					settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
				}
				if (StringUtil.equals(settleTransData.getPs(), DictCodeList.交易方向_现金分红.getValue())){
					// 单位净价
					if (StringUtil.isNotEmpty(szdJsmx.getJg1())){
						settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg1()));
					}
					// 单位全价
					settleTransData.setUnitFprice(null);
					// GNS-924:现金分红应取清算日期 modify by fuyongsheng 20220220 start
					settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
					// GNS-924:现金分红应取清算日期 modify by fuyongsheng 20220220 end
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
					//交易本金
//					settleTransData.setPrinAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
					settleTransData.setPrinAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
				    //交易份额
					settleTransData.setShareAmt(null);
					//利息总额
//					settleTransData.setIntAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
					settleTransData.setIntAmt(new BigDecimal(szdJsmx.getQsje()).abs());
					// modify by etliu 20211225 end
				}else{
					// 单位净价
					if (StringUtil.isNotEmpty(szdJsmx.getJg2())){
						settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg2()));
					}
					// 单位全价
					settleTransData.setUnitFprice(new BigDecimal(szdJsmx.getJg1()));
					//交易本金
					settleTransData.setPrinAmt(null);
					// 交易份额
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//					settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
					settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
					// modify by etliu 20211225 end
					//利息总额
					settleTransData.setIntAmt(null);
				}
				// 单位面值
				settleTransData.setParValue(fundInfoDto.getFaceValue());

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 全价总额
//				settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// 印花税
				if (StringUtil.isNotEmpty(szdJsmx.getYhs())){
//				    settleTransData.setStampFee(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
					settleTransData.setStampFee(new BigDecimal(szdJsmx.getYhs()).abs());
				    // 印花税_原始
//	                settleTransData.setStampFeeRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
					settleTransData.setStampFeeRaw(new BigDecimal(szdJsmx.getYhs()).abs());
				}
				// 经手费
				if (StringUtil.isNotEmpty(szdJsmx.getJsf())){
//				    settleTransData.setJsfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
					settleTransData.setJsfAmt(new BigDecimal(szdJsmx.getJsf()).abs());
				    // 经手费_原始
//	                settleTransData.setJsfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
					settleTransData.setJsfAmtRaw(new BigDecimal(szdJsmx.getJsf()).abs());
				}
				// 过户费
				if (StringUtil.isNotEmpty(szdJsmx.getGhf())){
//				    settleTransData.setGhfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
					settleTransData.setGhfAmt(new BigDecimal(szdJsmx.getGhf()).abs());
				    // 过户费_原始
//	                settleTransData.setGhfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
					settleTransData.setGhfAmtRaw(new BigDecimal(szdJsmx.getGhf()).abs());
				}
				// 证管费
				if (StringUtil.isNotEmpty(szdJsmx.getZgf())){
//				    settleTransData.setZgfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
					settleTransData.setZgfAmt(new BigDecimal(szdJsmx.getZgf()).abs());
				    // 证管费_原始
//	                settleTransData.setZgfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
					settleTransData.setZgfAmtRaw(new BigDecimal(szdJsmx.getZgf()).abs());
				}
				// 手续费
				if (StringUtil.isNotEmpty(szdJsmx.getSxf())){
//				    settleTransData.setSxfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
					settleTransData.setSxfAmt(new BigDecimal(szdJsmx.getSxf()).abs());
				    // 手续费_原始
//	                settleTransData.setSxfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
					settleTransData.setSxfAmtRaw(new BigDecimal(szdJsmx.getSxf()).abs());
				}
				// 交易金额
//				settleTransData.setTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// 交易金额_原始
//				settleTransData.setTradeAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
				settleTransData.setTradeAmtRaw(new BigDecimal(szdJsmx.getSjsf()).abs());
				// modify by etliu 20211225 end

				// 交易场所
				settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
				// 交易市场
				settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
				// 席位号
				settleTransData.setSeatNumber(StringUtil.isNotEmpty(szdJsmx.getXwh1()) ? szdJsmx.getXwh1() : szdJsmx.getXwh2());
				// 组合代码
				settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
				// 证券账户代码
				settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				// 资金账户
				settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
				// 临时表流水号
				settleTransData.setSeqNo(szdJsmx.getSeqno());
				
				StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
				stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
				// 成交数据与投管交易关系
				SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
				settleTransTgMapping.setTradeId(settleTransData.getTradeId());
				stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
				stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
			}
		}
		
		return stlDownDataDealWriteDto;
	}
	
	
	// 股票类证券品种处理
	private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, 
			List<StlDownDataDealAccountInfoDto>> map, List<String> bondFinprodIdList) {
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		String[] zqlbArray = new String[] {StlDataDealProcessConstant.ZQLB_PT, StlDataDealProcessConstant.ZQLB_PG, StlDataDealProcessConstant.ZQLB_PS, 
				StlDataDealProcessConstant.ZQLB_GJ, StlDataDealProcessConstant.ZQLB_GF, StlDataDealProcessConstant.ZQLB_JN, StlDataDealProcessConstant.ZQLB_JW,
				StlDataDealProcessConstant.ZQLB_SF, StlDataDealProcessConstant.ZQLB_XL, StlDataDealProcessConstant.ZQLB_YX, StlDataDealProcessConstant.ZQLB_ZG};
		String[] jgdmArray = new String[] {StlDataDealProcessConstant.JGDM_0000, StlDataDealProcessConstant.JGDM_0102};
		String[] ghlxArray = new String[] {StlDataDealProcessConstant.GHLX_00O, StlDataDealProcessConstant.GHLX_00K, StlDataDealProcessConstant.GHLX_00V};
		String[] jyfsArray = new String[] {StlDataDealProcessConstant.JYFS_001, StlDataDealProcessConstant.JYFS_002, StlDataDealProcessConstant.JYFS_014};
		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {
			// 035－网上发行业务时候，债券和股票一样的，需要特殊处理
			if (StringUtil.equals(StlDataDealProcessConstant.YWLX_035, szdJsmx.getYwlx())) {
				if (bondFinprodIdList.contains(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH")) {
					continue;
				}
			}
			// "固定收益类"  eq 清算数据Entity.证券类别 OR "基金" eq 清算数据Entity.证券类别，不处理
			if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_JJ, szdJsmx.getZqzh()) || StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, szdJsmx.getZqzh())) {
				continue;
			}
			// 以清算数据Entity.证券账号 + 清算数据Entity.业务单元1为KEY检索核算信息全量数据，不存在就不处理
			if (!map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh1()) && !map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh2())) {
				continue;
			}
			// 组合代码不在输入的组合代码List里面不处理
			List<StlDownDataDealAccountInfoDto> accountInfoDtolist = StringUtil.isEmptyList(map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1())) ? map.get(szdJsmx.getZqzh()+szdJsmx.getXwh2()) : map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1());
			if (accountInfoDtolist != null && accountInfoDtolist.size() > 0) {
				if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
					continue;
				}
			}
			else {
				continue;
			}
			// 定义变量.成交数据明细DTO
			SettleTransData settleTransData = new SettleTransData();
			// 金融产品代码
			if (StringUtil.isNotEmpty(szdJsmx.getZqdm2())) {
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm2() + ".SH");
			}
			else {
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
			}
			// 金融产品类型(投管分类)
			settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
			// 金融产品类型(估值核算)
			settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
			// 交易币种
			settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdJsmx.getBz()));
			// 交易平台
			settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdJsmx.getJyfs()));
			// 交收方式
			settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
			settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdJsmx.getJgdm()));
			// 单位净价
			settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg1()));
			// 清算速度
			settleTransData.setSettleSpeed(0L);
			if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
				settleTransData.setSettleSpeed(1L);
			}
			// modify by etliu 20211225 start
			// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
			// 交易份额
//			settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
			settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
			// 全价总额
//			settleTransData.setFpriceAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
			settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
			// modify by etliu 20211225 end

			// 交易日期
			settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
			// 交割日期
			settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
			
			// 新股中签
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_002, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_001, szdJsmx.getJyfs()) &&
					StringUtil.equals(StlDataDealProcessConstant.YWLX_035, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_73A, szdJsmx.getQsbz()) &&
				/*	StringUtil.equals(DictCodeListStl.交收方式_非担保.getValue(), settleTransData.getSettleMode())*/
					StringUtil.equals(StlDataDealProcessConstant.JSFS_002, szdJsmx.getJsfs())
					&& StringUtil.equals(StlDataDealProcessConstant.GHLX_00T, szdJsmx.getGhlx())
					&& StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())
					&& Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb())
					&& Arrays.asList(jgdmArray).contains(szdJsmx.getJgdm())) {
				//交易日期
				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
				//交割日期
				settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
				//交易方向
				settleTransData.setPs(DictCodeList.交易方向_申购.getValue());
                //交易份额
				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//				settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
				settleTransData.setShareAmt(new BigDecimal(szdJsmx.getSl()).abs());
				// modify by etliu 20211225 end
				// 成交编号 = 交收编号
				szdJsmx.setCjbh(szdJsmx.getJsbh());
				//业务场景明细分类
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_新股中签_通知.getValue());
			}
			// 新股中签
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_001, szdJsmx.getJyfs()) &&
					StringUtil.equals(StlDataDealProcessConstant.YWLX_035, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_73A, szdJsmx.getQsbz()) &&
					/*	StringUtil.equals(DictCodeListStl.交收方式_非担保.getValue(), settleTransData.getSettleMode())*/
					StringUtil.equals(StlDataDealProcessConstant.JSFS_002, szdJsmx.getJsfs())
					&& StringUtil.equals(StlDataDealProcessConstant.GHLX_00T, szdJsmx.getGhlx())
					&& StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())
					&& Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb())
					&& Arrays.asList(jgdmArray).contains(szdJsmx.getJgdm())) {
				//交易方向
				settleTransData.setPs(DictCodeList.交易方向_申购确认.getValue());
				//交易份额
				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//				settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
				settleTransData.setShareAmt(new BigDecimal(szdJsmx.getSl()).abs());
				// modify by etliu 20211225 end
				//交易日期
				// settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJsrq()));
				//交割日期
				settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
				// 成交编号 = 交收编号
				szdJsmx.setCjbh(szdJsmx.getJsbh());
				//业务场景明细分类
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_新股中签_确认.getValue());
				// 净价总额
				settleTransData.setCpriceAmt(settleTransData.getUnitCprice().multiply(settleTransData.getShareAmt()));
			}
			// GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 start
			if(DictCodeList.交易方向_申购确认.getValue().equals(settleTransData.getPs())
					|| DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())){
				settleTransData.setFinprodId(stlDownDataDealMapper.getLastFinproductIdByApprovalNo(szdJsmx.getZqdm1()));
			}
			// GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 end
			// 存续期股票—配售
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_001, szdJsmx.getJyfs()) &&
					StringUtil.equals(StlDataDealProcessConstant.YWLX_351, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_230, szdJsmx.getQsbz()) &&
					StringUtil.equals(DictCodeListStl.交收方式_担保.getValue(), settleTransData.getSettleMode()) && StringUtil.equals(StlDataDealProcessConstant.GHLX_00T, szdJsmx.getGhlx()) &&
					Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb()) && Arrays.asList(jgdmArray).contains(szdJsmx.getJgdm())) {
				settleTransData.setPs(DictCodeListExt2.交易方向_增发.getValue());
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_公开增发申购_增发配售.getValue());
			}
			// 红利清算
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_003, szdJsmx.getJyfs()) &&
					StringUtil.equals(StlDataDealProcessConstant.YWLX_351, szdJsmx.getYwlx()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_230, szdJsmx.getQsbz()) &&
					StringUtil.equals(DictCodeListStl.交收方式_担保.getValue(), settleTransData.getSettleMode()) && StringUtil.equals(StlDataDealProcessConstant.QYLB_HL, szdJsmx.getQylb()) &&
					Arrays.asList(ghlxArray).contains(szdJsmx.getGhlx()) && Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb()) && Arrays.asList(jgdmArray).contains(szdJsmx.getJgdm())) {
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
				//				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJsrq()));
				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
				settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
				settleTransData.setPs(DictCodeList.交易方向_现金分红.getValue());
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_分红_现金分红.getValue());
				//单位净价
				settleTransData.setUnitCprice(BigDecimal.ZERO);
				//单位利息
				settleTransData.setUnitInt(new BigDecimal(szdJsmx.getJg1()));
			}
			// 要约收购及债券回售
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_003, szdJsmx.getJyfs()) &&
					StringUtil.equals(StlDataDealProcessConstant.YWLX_301, szdJsmx.getYwlx()) && StringUtil.equals(DictCodeListStl.交收方式_担保.getValue(), settleTransData.getSettleMode()) &&
					StringUtil.equals(StlDataDealProcessConstant.GHLX_00T, szdJsmx.getGhlx()) && Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb())) {
				settleTransData.setMdate(DateUtil.parse(szdJsmx.getQtrq()));
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
					settleTransData.setPs(DictCodeListStl.交易方向_收购方过入.getValue());
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//					settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSl()).doubleValue())));
					settleTransData.setShareAmt(new BigDecimal(szdJsmx.getSl()).abs());
					// modify by etliu 20211225 end
				}
				else {
					settleTransData.setPs(DictCodeListStl.交易方向_预受方过出.getValue());
					// modify by etliu 20211225 start
					// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//					settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).doubleValue())));
					settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
					// modify by etliu 20211225 end
				}
			}
			// 股票大宗交易系统专场
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_03) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_003, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_102, szdJsmx.getJyfs()) &&
					StringUtil.equals(DictCodeListStl.交收方式_非担保.getValue(), settleTransData.getSettleMode()) && StringUtil.equals(StlDataDealProcessConstant.YWLX_621, szdJsmx.getYwlx()) && 
					StringUtil.equals(StlDataDealProcessConstant.QSBZ_060, szdJsmx.getQsbz()) && StringUtil.equals(StlDataDealProcessConstant.GHLX_00T, szdJsmx.getGhlx()) && 
					Arrays.asList(zqlbArray).contains(szdJsmx.getZqlb()) && StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, szdJsmx.getJgdm())) {
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
					settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
					settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_买入.getValue());
				}
				else {
					settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
					settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_卖出.getValue());
				}
			}
			// 股票买卖
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx()) && 
					(StringUtil.equals(StlDataDealProcessConstant.YWLX_001, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_037, szdJsmx.getYwlx())) &&
					Arrays.asList(jyfsArray).contains(szdJsmx.getJyfs()) && 
					(StringUtil.equals(StlDataDealProcessConstant.QSBZ_060, szdJsmx.getQsbz()) || StringUtil.equals(StlDataDealProcessConstant.QSBZ_061, szdJsmx.getQsbz())) &&
					StringUtil.equals(StlDataDealProcessConstant.GHLX_00A, szdJsmx.getGhlx()) && StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, szdJsmx.getJgdm())) {
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())) {
					settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
					settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_买入.getValue());
				}
				else {
					settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
					settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_卖出.getValue());
				}
			}
			// 存续期股票—配股缴款
			if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02) &&
					StringUtil.equals(StlDataDealProcessConstant.JLLX_001, szdJsmx.getJllx()) && StringUtil.equals(StlDataDealProcessConstant.JYFS_001, szdJsmx.getJyfs()) && 
					(StringUtil.equals(StlDataDealProcessConstant.YWLX_012, szdJsmx.getYwlx()) || StringUtil.equals(StlDataDealProcessConstant.YWLX_013, szdJsmx.getYwlx())) &&
					StringUtil.equals(DictCodeListStl.交收方式_担保.getValue(), settleTransData.getSettleMode()) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_070, szdJsmx.getQsbz()) && 
					!StringUtil.isNotEmpty(szdJsmx.getGhlx()) && Arrays.asList(jgdmArray).contains(szdJsmx.getJgdm())) {
				settleTransData.setPs(DictCodeListExt2.交易方向_配股.getValue());
				settleTransData.setTradeDate(com.joyintech.framework.util.DateUtil.parse(szdJsmx.getQsrq()));
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_配股认购_配股.getValue());
			}
			// 成交编号
//			if (StringUtil.isNotEmpty(szdJsmx.getCjbh())) {
//				settleTransData.setTradeId(szdJsmx.getCjbh());
//			}
//			else {
//				settleTransData.setTradeId(IDGenerator.generate());
//			}
			// 流水号
			settleTransData.setTradeId(IDGenerator.generate());
			// 成交编号
			settleTransData.setCjbh(szdJsmx.getCjbh());
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
			// 申请编号
			settleTransData.setApplyTradeId(szdJsmx.getSqbh());
			// 分支序号
			settleTransData.setBranch(0L);
			// 业务类型
			settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());

			// modify by etliu 20211225 start
			// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
			// 印花税
			if (StringUtil.isNotEmpty(szdJsmx.getYhs())){
//			    settleTransData.setStampFee(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
				settleTransData.setStampFee(new BigDecimal(szdJsmx.getYhs()).abs());
			    // 印花税_原始
//	            settleTransData.setStampFeeRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getYhs()).doubleValue())));
				settleTransData.setStampFeeRaw(new BigDecimal(szdJsmx.getYhs()).abs());
			}
			// 经手费
			if (StringUtil.isNotEmpty(szdJsmx.getJsf())){
//			    settleTransData.setJsfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
				settleTransData.setJsfAmt(new BigDecimal(szdJsmx.getJsf()).abs());
			    // 经手费_原始
//	            settleTransData.setJsfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getJsf()).doubleValue())));
				settleTransData.setJsfAmtRaw(new BigDecimal(szdJsmx.getJsf()).abs());
			}
			// 过户费
			if (StringUtil.isNotEmpty(szdJsmx.getGhf())){
//			    settleTransData.setGhfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
				settleTransData.setGhfAmt(new BigDecimal(szdJsmx.getGhf()).abs());
			    // 过户费_原始
//	            settleTransData.setGhfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getGhf()).doubleValue())));
				settleTransData.setGhfAmtRaw(new BigDecimal(szdJsmx.getGhf()).abs());
			}
			// 证管费
			if (StringUtil.isNotEmpty(szdJsmx.getZgf())){
//			    settleTransData.setZgfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
				settleTransData.setZgfAmt(new BigDecimal(szdJsmx.getZgf()).abs());
			    // 证管费_原始
//	            settleTransData.setZgfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getZgf()).doubleValue())));
				settleTransData.setZgfAmtRaw(new BigDecimal(szdJsmx.getZgf()).abs());
			}
			// 手续费
			if (StringUtil.isNotEmpty(szdJsmx.getSxf())){
//			    settleTransData.setSxfAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
				settleTransData.setSxfAmt(new BigDecimal(szdJsmx.getSxf()).abs());
			    // 手续费_原始
//	            settleTransData.setSxfAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSxf()).doubleValue())));
				settleTransData.setSxfAmtRaw(new BigDecimal(szdJsmx.getSxf()).abs());
			}
			// 交易金额
//			settleTransData.setTradeAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
			settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
			// 交易金额_原始
//			settleTransData.setTradeAmtRaw(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
			settleTransData.setTradeAmtRaw(new BigDecimal(szdJsmx.getSjsf()).abs());
			// modify by etliu 20211225 end

			// 交易场所
			settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
			// 交易市场
			settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
			// 席位号
			settleTransData.setSeatNumber(StringUtil.isNotEmpty(szdJsmx.getXwh1()) ? szdJsmx.getXwh1() : szdJsmx.getXwh2());
			// 组合代码
			settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
			// 证券账户代码
			settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
			// 资金账户
			settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
			// 临时表流水号
			settleTransData.setSeqNo(szdJsmx.getSeqno());
			// 交易方向是配股的时候，需要重新设置资产代码
			if (StringUtils.equals(settleTransData.getPs(), DictCodeListExt2.交易方向_配股.getValue())) {
				String approvalNo = StringUtils.isNotEmpty(szdJsmx.getZqdm1()) ? szdJsmx.getZqdm1() : szdJsmx.getZqdm2();
				List<String> finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue(), approvalNo);
				if (finprodIdList != null && finprodIdList.size() > 0) {
					settleTransData.setFinprodId(finprodIdList.get(0));
				}
			}
			
			StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
			stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
			// 成交数据与投管交易关系
			SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
			settleTransTgMapping.setTradeId(settleTransData.getTradeId());
			stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
			stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
		}
		
		return stlDownDataDealWriteDto;
	}

	// 信用保护凭证类证券品种处理
	private StlDownDataDealWriteDto secTypeIsCertificateOfCreditProtectionDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map, List<String> bondFinprodIdList) {
		StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
		for (SzdJsmx szdJsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getJsmxList()) {

			// 以清算数据Entity.证券账号 + 清算数据Entity.业务单元1为KEY检索核算信息全量数据，不存在就不处理
			if (!map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh1()) && !map.containsKey(szdJsmx.getZqzh()+szdJsmx.getXwh2())) {
				continue;
			}
			// 组合代码不在输入的组合代码List里面不处理
			List<StlDownDataDealAccountInfoDto> accountInfoDtolist = StringUtil.isEmptyList(map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1())) ? map.get(szdJsmx.getZqzh()+szdJsmx.getXwh2()) : map.get(szdJsmx.getZqzh()+szdJsmx.getXwh1());
			if (accountInfoDtolist != null && accountInfoDtolist.size() > 0) {
				if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
					continue;
				}
			}
			else {
				continue;
			}
			// 清算数据DTO
			StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();

				// 定义变量.成交数据明细DTO
				SettleTransData settleTransData = new SettleTransData();
				String generate = IDGenerator.generate();
				settleTransData.setTradeId(generate);
				settleTransData.setCjbh(szdJsmx.getCjbh());
				// 定义变量.成交数据明细附属表DTO
				SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
				// 金融产品代码
				settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_信用保护凭证.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
				// 金融产品类型(投管分类)
				settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
				// 金融产品类型(估值核算)
				settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
				// 单位全价
				settleTransData.setUnitFprice(new BigDecimal(szdJsmx.getJg1()));
				// 清算速度
				settleTransData.setSettleSpeed(0L);
				if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
					settleTransData.setSettleSpeed(1L);
				}

				// 交易份额
				settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs());
				// 全价总额
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());

				// 单位净价
				if (StringUtil.isNotEmpty(szdJsmx.getJg2())) {
					settleTransData.setUnitCprice(new BigDecimal(szdJsmx.getJg2()));// 配债此字段未空，需要判断
				}
				// 交易币种
				settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdJsmx.getBz()));
				// 交收方式
				settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
				settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdJsmx.getJgdm()));
				//交易日期
				settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
				//执行日期
				settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
				// 分支序号
				settleTransData.setBranch(0L);
				//申请编号
				settleTransData.setApplyTradeId(szdJsmx.getSqbh());
				//业务类型
				settleTransData.setBusiType(DictCodeListStl.共通业务类型_信用保护凭证交易.getValue());
				// 证券品种
				settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
				// 交割日期
				settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getQsrq()));
				// 交易平台
				settleTransData.setTradePlat(null);
				//普通二级市场交易业务
				if (StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SZD_JSMX_02)){
					// 交易方向
					if(StringUtil.equals(StlDataDealProcessConstant.MMBS_B, szdJsmx.getMmbz())){
						settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
						// 业务场景明细分类
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_信用保护凭证_买入.getValue());
					} else if(StringUtil.equals(StlDataDealProcessConstant.MMBS_S, szdJsmx.getMmbz())){
						settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
						// 业务场景明细分类
						settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_信用保护凭证_卖出.getValue());
					}
					if (StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_690)) {
						settleTransData.setApplyTradeId(szdJsmx.getCjbh());
					}
				}

				// 印花税
				if (StringUtil.isNotEmpty(szdJsmx.getYhs())){
					settleTransData.setStampFee(new BigDecimal(szdJsmx.getYhs()).abs());
					// 印花税_原始
					settleTransData.setStampFeeRaw(new BigDecimal(szdJsmx.getYhs()).abs());
				}
				// 经手费
				if (StringUtil.isNotEmpty(szdJsmx.getJsf())){
					settleTransData.setJsfAmt(new BigDecimal(szdJsmx.getJsf()).abs());
					// 经手费_原始
					settleTransData.setJsfAmtRaw(new BigDecimal(szdJsmx.getJsf()).abs());
				}
				// 过户费
				if (StringUtil.isNotEmpty(szdJsmx.getGhf())){
					settleTransData.setGhfAmt(new BigDecimal(szdJsmx.getGhf()).abs());
					// 过户费_原始
					settleTransData.setGhfAmtRaw(new BigDecimal(szdJsmx.getGhf()).abs());
				}
				// 证管费
				if (StringUtil.isNotEmpty(szdJsmx.getZgf())){
					settleTransData.setZgfAmt(new BigDecimal(szdJsmx.getZgf()).abs());
					// 证管费_原始
					settleTransData.setZgfAmtRaw(new BigDecimal(szdJsmx.getZgf()).abs());
				}
				// 手续费
				if (StringUtil.isNotEmpty(szdJsmx.getSxf())){
					settleTransData.setSxfAmt(new BigDecimal(szdJsmx.getSxf()).abs());
					// 手续费_原始
					settleTransData.setSxfAmtRaw(new BigDecimal(szdJsmx.getSxf()).abs());
				}
				// 交易金额
				settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// 交易金额_原始
				settleTransData.setTradeAmtRaw(new BigDecimal(szdJsmx.getSjsf()).abs());
				// 交易场所
				settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
				// 交易市场
				settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
				// 席位号
				settleTransData.setSeatNumber(szdJsmx.getXwh1());
				// 组合代码
				settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
				// 证券账户代码
				settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				// 资金账户
				settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
				// 临时表流水号
				settleTransData.setSeqNo(szdJsmx.getSeqno());

				stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
				stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
				// 成交数据与投管交易关系
				SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
				settleTransTgMapping.setTradeId(settleTransData.getTradeId());
				stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);

			stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
		}

		return stlDownDataDealWriteDto;
	}


	// 交易方向取得-上中登jsmx的债券
	private String getSzdJsmxBondPs(String dataFile, String mmbs, String ywlx, String zqlb, String jllx, String qsbz, String ghlx,String jgdm) {
		// jsmx02
		if (StringUtil.lowerCase(dataFile).startsWith(StlDataDealProcessConstant.SZD_JSMX_02)) {
			if (Arrays.asList(szdJsmx02BondYwlxArray1).contains(ywlx)) {
			    if (StringUtil.equals(StlDataDealProcessConstant.YWLX_012, ywlx)){
			        return DictCodeListExt3.交易方向_配债.getValue();
			    }
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
					return DictCodeList.交易方向_买入.getValue();
				}
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
					return DictCodeList.交易方向_卖出.getValue();
				}
			}
		}
		// jsmx03
		if (StringUtil.lowerCase(dataFile).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)) {
			if (Arrays.asList(szdJsmx03BondYwlxArray1).contains(ywlx)
			    && StlDataDealProcessConstant.JGDM_0000.contains(jgdm)) {
			    if (StringUtil.equals(StlDataDealProcessConstant.YWLX_032, ywlx)){
                    return DictCodeListExt3.交易方向_配债.getValue();
                }
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
					return DictCodeList.交易方向_买入.getValue();
				}
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
					return DictCodeList.交易方向_卖出.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_305)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb)) {
					return DictCodeList.交易方向_行权_回售.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_020)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb)
				        && (StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, jgdm) ||
						StringUtil.equals(StlDataDealProcessConstant.JGDM_0102, jgdm))) {
					if (StringUtil.equals(StlDataDealProcessConstant.JLLX_002, jllx)) {
						return DictCodeList.交易方向_申购.getValue();
					}else if (StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx)){
						return DictCodeList.交易方向_申购确认.getValue();
					}
				}
			}
			/*if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_035)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) && StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx)) {
					return DictCodeList.交易方向_申购确认.getValue();
				}
			}*/
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_653)) {
				if ((StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) || StringUtil.equals(StlDataDealProcessConstant.ZQLB_PT, zqlb)) 
						&& StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_178, qsbz)) {
					return DictCodeListStl.交易方向_换股.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_012)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) && StringUtil.equals(StlDataDealProcessConstant.GHLX_00L, ghlx)) {
					if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
						return DictCodeList.交易方向_买入.getValue();
					}
					if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
						return DictCodeList.交易方向_卖出.getValue();
					}
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_011)) {
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_001, jllx)) {
					return DictCodeListExt1.交易方向_债转股.getValue();
				}
			}
		}
		return null;
	}
	
	// 业务场景明细分类取得-上中登jsmx的债券
	private String getSzdJsmxBondBusiSceneClass(String dataFile, String mmbs, String ywlx, String zqlb, String jllx, String qsbz, String ghlx,String jgdm) {
		// jsmx02
		if (StringUtil.lowerCase(dataFile).startsWith(StlDataDealProcessConstant.SZD_JSMX_02)) {
			if (Arrays.asList(szdJsmx02BondYwlxArray1).contains(ywlx)) {
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
					return DictCodeListStl.业务场景分类明细_债券_债券买入.getValue();
				}
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
					return DictCodeListStl.业务场景分类明细_债券_债券卖出.getValue();
				}
			}
		}
		// jsmx03
		if (StringUtil.lowerCase(dataFile).startsWith(StlDataDealProcessConstant.SZD_JSMX_03)) {
			if (Arrays.asList(szdJsmx03BondYwlxArray1).contains(ywlx)
			&& StlDataDealProcessConstant.JGDM_0000.equals(jgdm)) {
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
					return DictCodeListStl.业务场景分类明细_债券_债券买入.getValue();
				}
				if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
					return DictCodeListStl.业务场景分类明细_债券_债券卖出.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_301)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb)) {
					return DictCodeListStl.业务场景分类明细_债券_回售_行权回售.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_020)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) &&
						(StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, jgdm)||
						StringUtil.equals(StlDataDealProcessConstant.JGDM_0102, jgdm))) {
					if(StringUtil.equals(StlDataDealProcessConstant.JLLX_002, jllx)) {
						return DictCodeListStl.业务场景分类明细_债券_网上申购_申购.getValue();
					}else if(StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx)){
						return DictCodeListStl.业务场景分类明细_债券_网上申购_申购确认.getValue();
					}
				}
			}
//			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_020)) {
//				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb)
//					&& StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx)
//						&& (StringUtil.equals(StlDataDealProcessConstant.JGDM_0000, jgdm)
//						StringUtil.equals(StlDataDealProcessConstant.JGDM_0102, jgdm))) {
//					return DictCodeListStl.业务场景分类明细_债券_网上申购_申购确认.getValue();
//				}
//			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_653)) {
				if ((StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) || StringUtil.equals(StlDataDealProcessConstant.ZQLB_PT, zqlb)) 
						&& StringUtil.equals(StlDataDealProcessConstant.JLLX_003, jllx) && StringUtil.equals(StlDataDealProcessConstant.QSBZ_178, qsbz)) {
					return DictCodeListStl.业务场景分类明细_债券_换股.getValue();
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_012)) {
				if (StringUtil.equals(StlDataDealProcessConstant.ZQLB_GZ, zqlb) && StringUtil.equals(StlDataDealProcessConstant.GHLX_00L, ghlx)) {
					if (StringUtil.equals(StlDataDealProcessConstant.MMBS_B, mmbs)) {
						return DictCodeListStl.业务场景分类明细_债券_债券买入.getValue();
					}
					if (StringUtil.equals(StlDataDealProcessConstant.MMBS_S, mmbs)) {
						return DictCodeListStl.业务场景分类明细_债券_债券卖出.getValue();
					}
				}
			}
			if (StringUtil.equals(ywlx, StlDataDealProcessConstant.YWLX_011)) {
				if (StringUtil.equals(StlDataDealProcessConstant.JLLX_001, jllx)) {
					return DictCodeListStl.业务场景分类明细_债券_转股_债转股.getValue();
				}
			}
		}
		return null;
	}
	
	// 期限品种取得-上中登jsmx的回购
	private String getTermType(String zqdm1) {
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206001, zqdm1)) {
			return DictCodeList.回购期限品种_GC001.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206002, zqdm1)) {
			return DictCodeList.回购期限品种_GC002.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206003, zqdm1)) {
			return DictCodeList.回购期限品种_GC003.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206004, zqdm1)) {
			return DictCodeList.回购期限品种_GC004.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206007, zqdm1)) {
			return DictCodeList.回购期限品种_GC007.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206014, zqdm1)) {
			return DictCodeList.回购期限品种_GC014.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206028, zqdm1)) {
			return DictCodeList.回购期限品种_GC028.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206091, zqdm1)) {
			return DictCodeList.回购期限品种_GC091.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_206182, zqdm1)) {
			return DictCodeList.回购期限品种_GC182.getValue();
		}
		// GNS-847: 解决竞价回购termType为空 modify by fuyongsheng 20220214 start
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204001, zqdm1)) {
			return DictCodeList.回购期限品种_GC001.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204002, zqdm1)) {
			return DictCodeList.回购期限品种_GC002.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204003, zqdm1)) {
			return DictCodeList.回购期限品种_GC003.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204004, zqdm1)) {
			return DictCodeList.回购期限品种_GC004.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204007, zqdm1)) {
			return DictCodeList.回购期限品种_GC007.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204014, zqdm1)) {
			return DictCodeList.回购期限品种_GC014.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204028, zqdm1)) {
			return DictCodeList.回购期限品种_GC028.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204091, zqdm1)) {
			return DictCodeList.回购期限品种_GC091.getValue();
		}
		if (StringUtil.equals(StlDataDealProcessConstant.ZQDM1_204182, zqdm1)) {
			return DictCodeList.回购期限品种_GC182.getValue();
		}
		// GNS-847: 解决竞价回购termType为空 modify by fuyongsheng 20220214 end
		return null;
	}

	/**
	 * 处理债券兑付和债券收息场景类型交收数据
	 * @param szdJsmx
	 * @param stlDownDataDealOperatorDto
	 * @param accountInfoDtolist
	 * @return
	 */
	private StlDownDataDealWriteBaseDto dealBondCashAndInterestCollection(SzdJsmx szdJsmx,
                                                                          StlDownDataDealOperatorDto stlDownDataDealOperatorDto, List<StlDownDataDealAccountInfoDto> accountInfoDtolist){
		 StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
		// 定义变量.成交数据明细DTO
		SettleTransData settleTransData = new SettleTransData();
		// 定义变量.成交数据明细附属表DTO
		SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
		String generate = IDGenerator.generate();
		// 流水号
		settleTransData.setTradeId(generate);
		// 成交编号
		settleTransData.setCjbh(szdJsmx.getCjbh());
		// 申请编号
		settleTransData.setApplyTradeId(szdJsmx.getSqbh());
		// 金融产品代码
		settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdJsmx.getZqdm1() + ".SH");
		// 金融产品类型(投管分类)
		settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		// 金融产品类型(估值核算)
		settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
		// 分支序号
		settleTransData.setBranch(0L);
		// 业务类型
		settleTransData.setBusiType(DictCodeListExt3.业务类型_债券交易.getValue());
		// 交易日期
		// modify by fuyongsheng 20211217 交易日期无需+1自然日 start
		// ImplCalendar implCalendar = new ImplCalendar();
		// settleTransData.setTradeDate(DateUtil.parse(implCalendar.calcDate(szdJsmx.getJyrq(), 1,
		//		BussinessDictCodeListRds.操作符_加.getValue(), BussinessDictCodeListRds.特征值偏移单位_自然日.getValue())));
		// settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getJyrq()));
		// modify by fuyongsheng 20211218 交易日期取清算日期 start
		settleTransData.setTradeDate(DateUtil.parse(szdJsmx.getQsrq()));
		// modify by fuyongsheng 20211218 交易日期取清算日期 end
		// modify by fuyongsheng 20211217 交易日期无需+1自然日 end
		// 交割日期
		settleTransData.setSettleDate(DateUtil.parse(szdJsmx.getJsrq()));
		// 交易币种
		settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdJsmx.getBz()));
		// 交易平台
		String finProductTypeType6 = getFinProductTypeType6(settleTransData);
		settleTransData.setTradePlat(null);
		if ("B14".equals(finProductTypeType6)) {
			settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdJsmx.getJyfs()));
		}
		// 交收方式
		settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdJsmx.getJsfs()));
		// 交收结果
		settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdJsmx.getJgdm()));
		// 执行日期
		settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
		// 证券品种
		settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
		// 清算速度
		settleTransData.setSettleSpeed(0L);
		if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
			settleTransData.setSettleSpeed(1L);
		}
		// 交易场所
		settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
		// 交易市场
		settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
		// 席位号
		settleTransData.setSeatNumber(szdJsmx.getXwh2());
		// 组合代码
		settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
		// 证券账户代码
		settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
		// 资金账户
		settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
		// 临时表流水号
		settleTransData.setSeqNo(szdJsmx.getSeqno());
		// 资金交收日
		settleTransData.setVpayDate(DateUtil.parse(szdJsmx.getQsrq()));

		// modify by etliu 20211225 start
		// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
		// 交易份额  清算数据Entity.成交数量/100
//		settleTransData.setShareAmt(new BigDecimal(Math.abs(new BigDecimal(szdJsmx.getCjsl()).divide(new BigDecimal(100)).doubleValue())));
		settleTransData.setShareAmt(new BigDecimal(szdJsmx.getCjsl()).abs().divide(new BigDecimal(100)));
		// 单位面值 调用【当前面值】
		settleTransData.setParValue(stlDataDealProcessCommonService.getUnitPrice(settleTransData.getFinprodId(),
				settleTransData.getTradeDate()));
		// 交易金额 ABS(清算数据Entity.实际收付)
//		settleTransData.setTradeAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
		settleTransData.setTradeAmt(new BigDecimal(szdJsmx.getSjsf()).abs());
		// modify by etliu 20211225 end

		if(StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_352)){ // 债券兑付
			FinProduct finProduct = settleCommonService.selectFinProduct(settleTransData.getFinprodId());
			// 清算数据的交易日期 == 标的信息的到期日
			String jyrq = StringUtil.isNotEmpty(szdJsmx.getJyrq()) ? szdJsmx.getJyrq() : szdJsmx.getQsrq();
			if(DateUtil.parse(jyrq).equals(finProduct.getMdate())){
				// 交易方向
				settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
				// 业务场景细类
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_到期_到期.getValue());
				// 单位净价
				settleTransData.setUnitCprice(new BigDecimal(0));
				// 单位全价
				settleTransData.setUnitFprice(new BigDecimal(0));
				// 单位利息
				settleTransData.setUnitInt(new BigDecimal(0));
				// 交易本金(清算数据价格2*交易份额)
				settleTransData.setPrinAmt(new BigDecimal(szdJsmx.getJg2()).multiply(settleTransData.getShareAmt()));
				// 净价总额
				settleTransData.setCpriceAmt(new BigDecimal(0));

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 利息总额 abs(清算数据Entity.实付金额)-变量.交易本金
//				settleTransData.setIntAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue()))
//						.subtract(settleTransData.getPrinAmt()));
				settleTransData.setIntAmt(new BigDecimal(szdJsmx.getSjsf()).abs()
						.subtract(settleTransData.getPrinAmt()));
				// 全价总额 abs(清算数据Entity.清算金额)
//				settleTransData.setFpriceAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// modify by etliu 20211225 end
			}else{
				settleTransData.setPs(DictCodeList.交易方向_行权_赎回.getValue());
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_赎回.getValue());
				// 单位全价 = 清算数据价格1
				settleTransData.setUnitFprice(new BigDecimal(szdJsmx.getJg1()));
				// 单位利息(此公共方法计算多种参数,仅使用单位利息)
				stlDownDataDealService.calc(settleTransData);
				// 单位净价(变量.单位全价 - 变量.单位利息)
				settleTransData.setUnitCprice(ArithmeticUtil.subtract(settleTransData.getUnitFprice(), settleTransData.getUnitInt()));
				// 交易本金(单位面值*交易份额)
				settleTransData.setPrinAmt(ArithmeticUtil.multiply(settleTransData.getParValue(), settleTransData.getShareAmt()));
				// 净价总额(变量.单位净价* 变量.交易份额)
				settleTransData.setCpriceAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 利息总额 abs(清算数据Entity.清算金额)-变量.净价总额
//				settleTransData.setIntAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue()))
//						.subtract(settleTransData.getCpriceAmt()));
				settleTransData.setIntAmt(ArithmeticUtil.subtract(new BigDecimal(szdJsmx.getQsje()).abs(), settleTransData.getCpriceAmt()));
				// 全价总额 abs(清算数据Entity.清算金额)
//				settleTransData.setFpriceAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// modify by etliu 20211225 end
			}
		}else if(StringUtil.equals(szdJsmx.getYwlx(), StlDataDealProcessConstant.YWLX_353)){ // 债券兑息
			// 清算数据价格2不为空且不等于0
			if(StringUtil.isNotEmpty(szdJsmx.getJg2()) && new BigDecimal(szdJsmx.getJg2()).compareTo(BigDecimal.ZERO) != 0){
				settleTransData.setPs( DictCodeList.交易方向_还本.getValue());
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_还本收息.getValue());
				// 单位净价
				settleTransData.setUnitCprice(new BigDecimal(0));
				// 单位全价
				settleTransData.setUnitFprice(new BigDecimal(0));
				// 单位利息
				settleTransData.setUnitInt(new BigDecimal(0));
				// 交易本金(清算数据价格2*交易份额)
				settleTransData.setPrinAmt(new BigDecimal(szdJsmx.getJg2()).multiply(settleTransData.getShareAmt()));
				// 净价总额
				settleTransData.setCpriceAmt(new BigDecimal(0));

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 利息总额 abs(清算数据Entity.实际收付)-变量.交易本金
//				settleTransData.setIntAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue()))
//						.subtract(settleTransData.getPrinAmt()));
				settleTransData.setIntAmt(new BigDecimal(szdJsmx.getSjsf()).abs()
						.subtract(settleTransData.getPrinAmt()));
				// 全价总额 abs(清算数据Entity.清算金额)
//				settleTransData.setFpriceAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getQsje()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getQsje()).abs());
				// modify by etliu 20211225 end
			}else{
				settleTransData.setPs(DictCodeList.交易方向_收息.getValue());
				settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_收息.getValue());
				// 单位净价
				settleTransData.setUnitCprice(new BigDecimal(0));
				// 单位全价
				settleTransData.setUnitFprice(new BigDecimal(0));
				// 单位利息
				settleTransData.setUnitInt(new BigDecimal(0));
				// 交易本金(单位面值*交易份额)
				settleTransData.setPrinAmt(settleTransData.getParValue().multiply(settleTransData.getShareAmt()));
				// 净价总额
				settleTransData.setCpriceAmt(new BigDecimal(0));

				// modify by etliu 20211225 start
				// 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
				// 利息总额 abs(清算数据.实付金额)
//				settleTransData.setIntAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
				settleTransData.setIntAmt(new BigDecimal(szdJsmx.getSjsf()).abs());
				// 全价总额 abs(清算数据.实付金额)
//				settleTransData.setFpriceAmt(BigDecimal.valueOf(Math.abs(new BigDecimal(szdJsmx.getSjsf()).doubleValue())));
				settleTransData.setFpriceAmt(new BigDecimal(szdJsmx.getSjsf()).abs());
				// modify by etliu 20211225 end
			}
		}
		// 印花税
		settleTransData.setStampFee(BigDecimal.ZERO);
		// 经手费
		settleTransData.setJsfAmt(BigDecimal.ZERO);
		// 过户费
		settleTransData.setGhfAmt(BigDecimal.ZERO);
		// 证管费
		settleTransData.setZgfAmt(BigDecimal.ZERO);
		// 手续费
		settleTransData.setSxfAmt(BigDecimal.ZERO);
		// 佣金
		settleTransData.setCommissionAmt(BigDecimal.ZERO);
		// 佣金_原始
		settleTransData.setCommissionAmtRaw(BigDecimal.ZERO);
		// 印花税_原始
		settleTransData.setStampFeeRaw(this.abs(szdJsmx.getYhs()));
		// 经手费_原始
		settleTransData.setJsfAmtRaw(this.abs(szdJsmx.getJsf()));
		// 过户费_原始
		settleTransData.setGhfAmtRaw(this.abs(szdJsmx.getGhf()));
		// 证管费_原始
		settleTransData.setZgfAmtRaw(this.abs(szdJsmx.getZgf()));
		// 手续费_原始
		settleTransData.setSxfAmtRaw(this.abs(szdJsmx.getSxf()));
		// 是否优先配售
		settleTransData.setIsPurchTypeYx(DictCodeList.是否_否.getValue());
		stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
		// 成交编号= 成交数据明细流水号
		settleTransDataAdd.setTradeId(settleTransData.getTradeId());
		settleTransDataAdd.setSeRepDayType(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_SE_REP_DAY_TYPE));
		stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
		// 成交数据与投管交易关系
		SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
		settleTransTgMapping.setTradeId(settleTransData.getTradeId());
		stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
		return stlDownDataDealWriteBaseDto;
	}

	/**
	 * 根据金融产品代码查询FIN_PRODUCT_TYPE.TYPE_6 -- 投管一级类型
	 */
	private String getFinProductTypeType6(SettleTransData settleTransData) {
		FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
		finProductTypeKey.setBranch(0L);
		finProductTypeKey.setFinprodId(settleTransData.getFinprodId());

		FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
		if (null == finProductType) {
			return "";
		}
		return finProductType.getType6();
	}

	/**
	 * 对字符串类型的数字取绝对值
	 * @param value
	 * @return
	 */
	private BigDecimal abs(String value) {
		return null != value ? new BigDecimal(value).abs() : null;
	}
}
