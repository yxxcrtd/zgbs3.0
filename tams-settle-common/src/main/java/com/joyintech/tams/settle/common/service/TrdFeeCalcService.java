/*
 * 文件名：ExchangeFeeCalc.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */
package com.joyintech.tams.settle.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.common.base.entity.TrdFeeConfig;
import com.joyintech.tams.common.base.entity.TrdFeeConfigDetail;
import com.joyintech.tams.settle.common.dao.TrdFeeCalcMapper;
import com.joyintech.tams.settle.common.dto.CalcParamDto;
import com.joyintech.tams.settle.common.dto.CommisionDetail;
import com.joyintech.tams.settle.common.dto.CommissionCalcDetail;
import com.joyintech.tams.settle.common.dto.CommissionCalcDto;
import com.joyintech.tams.settle.common.dto.DetailAmount;
import com.joyintech.tams.settle.common.dto.FeeInfo;
import com.joyintech.tams.settle.common.dto.FeePlan;
import com.joyintech.tams.settle.common.dto.PlanSearchInfo;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import com.joyintech.tams.settle.common.dto.TradeFeatureInformation;
import com.joyintech.tams.settle.common.dto.TrdDetailDto;
import com.joyintech.tams.settle.common.dto.TrdTradeFeeDetailExtDto;

/**
 * 交易所费用计算
 *
 * @author 王波
 * @date 2020/7/8
 * @see TrdFeeCalcService
 * @since 1.0
 */
@Service
public class TrdFeeCalcService {

    @Autowired
    private TrdFeeCalcMapper trdFeeCalcMapper;

    /**
     * 证券账户表
     */
    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    /**
     * 金融产品分类表
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private SettleCommonService settleCommonService;
    
    /**
     * 金融产品附属表
     */
    @Autowired
    private FinProductAddMapper finProductAddMapper;

    /**
     * 一级费用计算
     *
     * @param trdDetail      交易明细
     * @param feeInfos       费用列表
     * @param planSearchInfo 方案查询信息
     * @param tradeType      交易品种
     * @param retentionBit   小数位数
     * @return 费用列表
     */
    public List<FeeInfo> firstFeeCalc(TrdDetailDto trdDetail
            , List<FeeInfo> feeInfos
            , PlanSearchInfo planSearchInfo
            , String tradeType
            , Integer retentionBit) {
        if (retentionBit == null) {
            retentionBit = 2;
        }
        //变量.证券账户信息 = 根据主键【交易明细.证券账户代码】查询证券账户表
//		StlSecAcctInfo stlSecAcctInfo = stlSecAcctInfoMapper.selectByPrimaryKey(trdDetail.getSecManageAcctId());
        StlDownDataDealAccountInfoDto stlDownDataDealAccountInfoDto = new StlDownDataDealAccountInfoDto();
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setBranch(trdDetail.getBranch());
        finProductTypeKey.setFinprodId(trdDetail.getFinprodId());
        // 变量.标的分类信息 = 根据主键【交易明细.金融产品代码+分支序号】查询金融产品分类表
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);

        // 数据清算附属信息（核算信息）全量获取
        Map<String, List<StlDownDataDealAccountInfoDto>> allStlDownDataDealAccountInfoDtoList
                = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(false);
        List<StlDownDataDealAccountInfoDto> stlDownDataDealAccountInfoDtos = allStlDownDataDealAccountInfoDtoList.get(trdDetail.getSecManageAcctId());
        if (stlDownDataDealAccountInfoDtos != null && stlDownDataDealAccountInfoDtos.size() > 0) {
            stlDownDataDealAccountInfoDto = stlDownDataDealAccountInfoDtos.get(0);
        }

        for (FeeInfo feeInfo : feeInfos) {
            // 1.费用方案查询
            PlanSearchInfo localPlanSearchInfo = null;
            //if 【入参】方案查询信息 = null
            if (planSearchInfo == null) {
                localPlanSearchInfo = new PlanSearchInfo();
                // 费用类型
    			// 交易场所
    			if (StringUtil.isNotEmpty(trdDetail.getTradeMarket())) {
    				localPlanSearchInfo.setTradeMarket(trdDetail.getTradeMarket());
    			}
    			// 交易平台
    			if (StringUtil.isNotEmpty(trdDetail.getTradePlat())) {
    				localPlanSearchInfo.setTradePlatform(trdDetail.getTradePlat());
    			}
    			// 费用类型
    			if (StringUtil.isNotEmpty(feeInfo.getFeeType())) {
    				localPlanSearchInfo.setFeeType(feeInfo.getFeeType());
    			}
    			// 登记托管机构
    			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getRegistOrg())) {
    				localPlanSearchInfo.setRegistOrg(stlDownDataDealAccountInfoDto.getRegistOrg());
    			}
    			// 交易品种
    			if (StringUtil.isNotEmpty(tradeType)) {
    				localPlanSearchInfo.setTradeType(tradeType);
    			}
    			// 交易方向
    			if (StringUtil.isNotEmpty(trdDetail.getPs())) {
    				localPlanSearchInfo.setPs(trdDetail.getPs());
    			}
    			// 期限品种
    			if (StringUtil.isNotEmpty(trdDetail.getTermType())) {
    				localPlanSearchInfo.setTermType(trdDetail.getTermType());
    			}
    			// 证券账户
    			if (StringUtil.isNotEmpty(trdDetail.getSecManageAcctId())) {
    				localPlanSearchInfo.setSecAcctId(trdDetail.getSecManageAcctId());
    			}
    			// 券商代码
    			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getBrokerId())) {
    				localPlanSearchInfo.setBrokerageNo(stlDownDataDealAccountInfoDto.getBrokerId());
    			}
    			// 证券分类1
    			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType6())) {
    				localPlanSearchInfo.setAssetClassify1(finProductType.getType6());
    			}
    			// 证券分类2
    			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType7())) {
    				localPlanSearchInfo.setAssetClassify2(finProductType.getType7());
    			}
    			// 组合代码
    			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getPortfolioId())) {
    				localPlanSearchInfo.setPortfolioId(stlDownDataDealAccountInfoDto.getPortfolioId());
    			}
    			// 资金账户
				if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getAcctNo())) {
					localPlanSearchInfo.setCapitalAccountNo(stlDownDataDealAccountInfoDto.getAcctNo());
				}
            } 
            else {
                // 变量.方案查询信息 = 【入参】方案查询信息
                localPlanSearchInfo = planSearchInfo;
                // 变量.方案查询信息.费用类型 = 变量.费用信息.费用类型
                localPlanSearchInfo.setFeeType(feeInfo.getFeeType());
            }

            // 1.2 查询费用方案
            FeePlan feePlan = getFeePlan(localPlanSearchInfo, trdDetail.getTradeDate());
            
            if (feePlan != null){
                // 变量.费用信息.是否随交易支付 = 变量.费用方案.是否随交易支付
                feeInfo.setIsPayWithTrade(feePlan.getTrdFeeConfig().getIsPayWithTrade());

                // 2.费用计算
                // 2.1 计算信息设置
                // 定义 变量.计算基数
                BigDecimal calcBasis = BigDecimal.ZERO;
                if (DictCodeListExt1.计费基数_成交金额.getValue().equals(feePlan.getTrdFeeConfig().getChargeBasis())) {
                    // 变量.计算基数 = 变量.交易明细.全价总额
                    calcBasis = trdDetail.getFpriceAmt();
                } else if (DictCodeListExt1.计费基数_券面总额.getValue().equals(feePlan.getTrdFeeConfig().getChargeBasis())) {
                    // 变量.计算基数 = 变量.交易明细.交易份额
                    calcBasis = trdDetail.getShareAmt();
                    if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), tradeType)) {
                        calcBasis = trdDetail.getShareAmt().multiply(BigDecimal.valueOf(100));
                    }
                }
                // 调用共通方法【计算信息设置】
                // 变量.计算参数列表
                List<CalcParamDto> calcParamDtos
                        = calcInfoSet(feePlan, calcBasis, feePlan.getTrdFeeConfig().getRetentionBit().intValue());

                feeInfo.setFeeAmount(BigDecimal.ZERO);
                // 2.2 费用计算
                for (CalcParamDto calcParamDto : calcParamDtos) {
                    // 费用金额计算
                    BigDecimal amount = feeAmountCalc(calcParamDto);
                    // 限额处理
                    BigDecimal limitAmount = limitResolve(calcParamDto, amount).setScale(retentionBit, RoundingMode.HALF_UP);
                    // 变量.费用信息.费用金额 = 变量.费用信息.费用金额 + 上述返回金额
                    feeInfo.setFeeAmount(ArithmeticUtil.doAdd(feeInfo.getFeeAmount(), limitAmount));
                }
            }

        }

        return feeInfos;
    }

    /**
     * 佣金计算（通用）
     *
     * @param detailDtos              交易明细列表
     * @param tradeFeatureInformation 交易特征信息
     * @param retentionBit            小数位数
     * @param batch                   批次号
     * @param isAlloc                 是否分配明细
     * @param tradeType               交易品种
     * @return 佣金计算DTO
     */
    public CommissionCalcDto calcWayCommon(List<TrdDetailDto> detailDtos
            , TradeFeatureInformation tradeFeatureInformation
            , Integer retentionBit, String batch, String isAlloc, String tradeType) {
        // 出参 - 佣金金额
        BigDecimal commfeeAmount = BigDecimal.ZERO;
        // 出参- 佣金计算过程明细列表
        List<CommissionCalcDetail> commissionCalcDetailList = new ArrayList<>();

        // 佣金明细列表
        List<CommisionDetail> commisionDetailList = new ArrayList<>();
        if (retentionBit == null) {
			retentionBit = 2;
		}

		if (isAlloc == null) {
			isAlloc = DictCodeList.是否_否.getValue();
		}
		// 交易日期
    	Date dealDate = null;
    	
    	// 交易品种转换（和曹国商量了一下，该处暂时强转，等曹国和金强商量完结果再说）
    	if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), tradeType)) {
    		tradeType = DictCodeList.平安资产类型_债券.getValue();
    	}
    	if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), tradeType)) {
    		tradeType = DictCodeList.平安资产类型_质押式回购.getValue();
    	}
    	if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), tradeType)) {
    		tradeType = DictCodeList.平安资产类型_基金.getValue();
    	}
    	if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), tradeType)) {
    		tradeType = DictCodeListExt2.平安资产类型_股票.getValue();
    	}
		// ZGCPCN-1541:费用计算增加信用保护凭证 modify by fuyongsheng 20220620 start
		if (StringUtil.equals(DictCodeListStl.证券品种_信用保护凭证.getValue(), tradeType)) {
			tradeType = DictCodeListExt2.平安资产类型_信用保护凭证.getValue();
		}
		// ZGCPCN-1541:费用计算增加信用保护凭证 modify by fuyongsheng 20220620 end

    	// 方案查询
		PlanSearchInfo localPlanSearchInfo = new PlanSearchInfo();
		StlDownDataDealAccountInfoDto stlDownDataDealAccountInfoDto = new StlDownDataDealAccountInfoDto();
		// 数据清算附属信息（核算信息）全量获取
		Map<String, List<StlDownDataDealAccountInfoDto>> allStlDownDataDealAccountInfoDtoList
				= settleCommonService.getAllStlDownDataDealAccountInfoDtoList(false);

        if (tradeFeatureInformation == null) {
            // if（【入参】交易特征信息 == null）
			// 变量.交易明细 = 【入参】交易明细列表get(0)
            TrdDetailDto trdDetail = detailDtos.get(0);
			// 变量.交易日期 = 变量.交易明细.交易日期
			dealDate = trdDetail.getTradeDate();
			FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
			finProductTypeKey.setBranch(0L);
			finProductTypeKey.setFinprodId(trdDetail.getFinprodId());
			// 变量.标的分类信息 = 根据主键【交易明细.金融产品代码+分支序号】查询金融产品分类表
			FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
			
			List<StlDownDataDealAccountInfoDto> stlDownDataDealAccountInfoDtos = allStlDownDataDealAccountInfoDtoList.get(trdDetail.getSecManageAcctId());
			if (stlDownDataDealAccountInfoDtos != null && stlDownDataDealAccountInfoDtos.size() > 0) {
				stlDownDataDealAccountInfoDto = stlDownDataDealAccountInfoDtos.get(0);
			}

			// 交易场所
			// 交易场所
			if (StringUtil.isNotEmpty(trdDetail.getTradeMarket())) {
				localPlanSearchInfo.setTradeMarket(trdDetail.getTradeMarket());
			}
			// 交易平台
			if (StringUtil.isNotEmpty(trdDetail.getTradePlat())) {
				localPlanSearchInfo.setTradePlatform(trdDetail.getTradePlat());
			}
			// 费用类型
			localPlanSearchInfo.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
			// 登记托管机构
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getRegistOrg())) {
				localPlanSearchInfo.setRegistOrg(stlDownDataDealAccountInfoDto.getRegistOrg());
			}
			// 交易品种
			if (StringUtil.isNotEmpty(tradeType)) {
				localPlanSearchInfo.setTradeType(tradeType);
			}
			// 交易方向
			if (StringUtil.isNotEmpty(trdDetail.getPs())) {
				localPlanSearchInfo.setPs(trdDetail.getPs());
			}
			// 期限品种
			if (StringUtil.isNotEmpty(trdDetail.getTermType())) {
				localPlanSearchInfo.setTermType(trdDetail.getTermType());
			}
			// 证券账户
			if (StringUtil.isNotEmpty(trdDetail.getSecManageAcctId())) {
				localPlanSearchInfo.setSecAcctId(trdDetail.getSecManageAcctId());
			}
			// 券商代码
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getBrokerId())) {
				localPlanSearchInfo.setBrokerageNo(stlDownDataDealAccountInfoDto.getBrokerId());
			}
			// 证券分类1
			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType6())) {
				localPlanSearchInfo.setAssetClassify1(finProductType.getType6());
			}
			// 证券分类2
			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType7())) {
				localPlanSearchInfo.setAssetClassify2(finProductType.getType7());
			}
			// 组合代码
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getPortfolioId())) {
				localPlanSearchInfo.setPortfolioId(stlDownDataDealAccountInfoDto.getPortfolioId());
			}
			// 资金账户
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getAcctNo())) {
				localPlanSearchInfo.setCapitalAccountNo(stlDownDataDealAccountInfoDto.getAcctNo());
			}
		}
        else {
        	// 变量.交易日期 = 【入参】交易特征信息.交易日期
			dealDate = tradeFeatureInformation.getDealDate();
			FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
			finProductTypeKey.setBranch(0L);
			finProductTypeKey.setFinprodId(tradeFeatureInformation.getFinprodId());
			// 变量.标的分类信息 = 根据主键【交易明细.金融产品代码+分支序号】查询金融产品分类表
			FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);

			List<StlDownDataDealAccountInfoDto> stlDownDataDealAccountInfoDtos
					= allStlDownDataDealAccountInfoDtoList.get(tradeFeatureInformation.getSecAcctId());
			if (stlDownDataDealAccountInfoDtos != null && stlDownDataDealAccountInfoDtos.size() > 0) {
				stlDownDataDealAccountInfoDto = stlDownDataDealAccountInfoDtos.get(0);
			}

			// 交易场所
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getTradeMarket())) {
				localPlanSearchInfo.setTradeMarket(tradeFeatureInformation.getTradeMarket());
			}
			// 交易平台
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getTradePlat())) {
				localPlanSearchInfo.setTradePlatform(tradeFeatureInformation.getTradePlat());
			}
			// 费用类型
			localPlanSearchInfo.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
			// 登记托管机构
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getRegistOrg())) {
				localPlanSearchInfo.setRegistOrg(stlDownDataDealAccountInfoDto.getRegistOrg());
			}
			// 交易品种
			if (StringUtil.isNotEmpty(tradeType)) {
				localPlanSearchInfo.setTradeType(tradeType);
			}
			// 交易方向
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getPs())) {
				localPlanSearchInfo.setPs(tradeFeatureInformation.getPs());
			}
			// 期限品种
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getTermType())) {
				localPlanSearchInfo.setTermType(tradeFeatureInformation.getTermType());
			}
			// 证券账户
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getSecAcctId())) {
				localPlanSearchInfo.setSecAcctId(tradeFeatureInformation.getSecAcctId());
			}
			// 券商代码
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getBrokerId())) {
				localPlanSearchInfo.setBrokerageNo(stlDownDataDealAccountInfoDto.getBrokerId());
			}
			// 证券分类1
			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType6())) {
				localPlanSearchInfo.setAssetClassify1(finProductType.getType6());
			}
			// 证券分类2
			if (finProductType != null && StringUtil.isNotEmpty(finProductType.getType7())) {
				localPlanSearchInfo.setAssetClassify2(finProductType.getType7());
			}
			// 结算数据下行相关处理（针对上农商只有竞价回购）
			// modify by etliu 20220104 start
			// GZYHTZJY-868: 日终清算，协议回购的佣金没有计算出来
//			if (StringUtil.equals(tradeType, DictCodeList.平安资产类型_质押式回购.getValue())) {
			if (StringUtil.equals(tradeType, DictCodeList.平安资产类型_质押式回购.getValue())
					&& !StringUtil.equals(DictCodeList.回购类型_质押式协议回购.getValue(), tradeFeatureInformation.getRepoType())) {
				// 质押式回购需要单独设置成竞价回购
				localPlanSearchInfo.setAssetClassify1(DictCodeList.回购类型_质押式竞价回购.getValue());
				localPlanSearchInfo.setAssetClassify2(DictCodeList.回购类型_质押式竞价回购.getValue());
				// 交易方向重新设置
				if (StringUtil.equals(tradeFeatureInformation.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
					localPlanSearchInfo.setPs(DictCodeList.回购拆借方向_正回购.getValue());
				}
				if (StringUtil.equals(tradeFeatureInformation.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
					localPlanSearchInfo.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
				}
			}
			else if (StringUtil.equals(tradeType, DictCodeList.平安资产类型_质押式回购.getValue())
					&& StringUtil.equals(DictCodeList.回购类型_质押式协议回购.getValue(), tradeFeatureInformation.getRepoType())) {
				localPlanSearchInfo.setAssetClassify1(DictCodeList.回购类型_质押式协议回购.getValue());
				localPlanSearchInfo.setAssetClassify2(DictCodeList.回购类型_质押式协议回购.getValue());
				// modify by etliu 20220117 start
				// GZYHTZJY-1080:【场内对账清结算1231】深交所协议回购 逆回购首期佣金没有计算出来，正回购到期续作佣金计算有误【解决日期：2022-01-12】
				if (!StringUtil.isEmptyList(detailDtos)) {
//					localPlanSearchInfo.setPs(detailDtos.get(0).getPs());
					if ((!StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), detailDtos.get(0).getPs())
							&& !StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getValue(), detailDtos.get(0).getPs()))
							|| StringUtil.equals(DictCodeList.交易方向_买入.getValue(), detailDtos.get(0).getPs())) {
						// 交易方向重新设置
						if (StringUtil.equals(tradeFeatureInformation.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
							localPlanSearchInfo.setPs(DictCodeList.回购拆借方向_正回购.getValue());
						}
						else if (StringUtil.equals(tradeFeatureInformation.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
							localPlanSearchInfo.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
						}
					}
					else {
						localPlanSearchInfo.setPs(detailDtos.get(0).getPs());
					}
				}
				// modify by etliu 20220117 end
			}
			// modify by etliu 20220104 end
			
			if (StringUtil.equals(tradeType, DictCodeList.平安资产类型_基金.getValue())){
			    FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(tradeFeatureInformation.getFinprodId());
			    if (finProductAdd != null){
			        localPlanSearchInfo.setAssetClassify2(finProductAdd.getTrmFundType());
			    }
			}
			// 组合代码
			if (StringUtil.isNotEmpty(tradeFeatureInformation.getPortfolioId())) {
				localPlanSearchInfo.setPortfolioId(tradeFeatureInformation.getPortfolioId());
			}
			// 资金账户
			if (StringUtil.isNotEmpty(stlDownDataDealAccountInfoDto.getAcctNo())) {
				localPlanSearchInfo.setCapitalAccountNo(stlDownDataDealAccountInfoDto.getAcctNo());
			}
		}

		// add by etliu 20220126 start
		// TISHKS-780: 交易所费用设置了费用，清算时无法计算出费用
		if (StringUtil.equals(tradeType, DictCodeList.平安资产类型_债券.getValue())){
			if (StringUtil.isNotBlank(localPlanSearchInfo.getAssetClassify2())
					&& !StringUtil.startsWith(localPlanSearchInfo.getAssetClassify2(), "B0")) {
				localPlanSearchInfo.setAssetClassify2("B0" + localPlanSearchInfo.getAssetClassify2());
			}
		}
		else if (StringUtil.equals(tradeType, DictCodeListExt2.平安资产类型_股票.getValue())){
			localPlanSearchInfo.setAssetClassify1(DictCodeListExt2.平安资产类型_股票.getValue());
			localPlanSearchInfo.setAssetClassify2(DictCodeListExt2.平安资产类型_股票.getValue());
		}
		// ZGCPCN-1541:费用计算增加信用保护凭证 modify by fuyongsheng 20220620 start
		else if (StringUtil.equals(tradeType, DictCodeListExt2.平安资产类型_信用保护凭证.getValue())){
			localPlanSearchInfo.setAssetClassify1(DictCodeListExt2.平安资产类型_信用保护凭证.getValue());
			localPlanSearchInfo.setAssetClassify2(DictCodeListExt2.平安资产类型_信用保护凭证.getValue());
		}
		// ZGCPCN-1541:费用计算增加信用保护凭证 modify by fuyongsheng 20220620 end
		// add by etliu 20220126 end

        // 查询费用方案
		FeePlan feePlan = getFeePlan(localPlanSearchInfo, dealDate);

		if (feePlan != null){

	        // 2.佣金包含费用处理
	        Map<String, List<TrdDetailDto>> mapTrdDetail = new HashMap<String,  List<TrdDetailDto>>();
	        List<FeeInfo> feeInfos = new ArrayList<>();

			if (StringUtil.isNotEmpty(feePlan.getTrdFeeConfig().getCommCalMethod())) {
				if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按申请编号汇总.getValue())) {
					mapTrdDetail = detailDtos.stream().collect(Collectors.groupingBy(TrdDetailDto::getApplyTradeId));
				}
			}

	        // 1.3 佣金承担费用方案查询
	        // 变量.佣金需处理费用列表 = 变量.费用方案.费用由券商承担 按照逗号分隔
	        if (StringUtil.isNotEmpty(feePlan.getTrdFeeConfig().getBrokerBaerFee())) {
	            List<String> feeTypes = Arrays.asList(feePlan.getTrdFeeConfig().getBrokerBaerFee().split(GlobalConstant.COMMA));
	            for (String feeType : feeTypes) {
	                // 费用信息
	                FeeInfo feeInfo = new FeeInfo();
	                feeInfo.setIsPayWithTrade(feePlan.getTrdFeeConfig().getIsPayWithTrade());
	                feeInfo.setFeeType(feeType);
	                feeInfos.add(feeInfo);
	            }
	            if (StringUtil.isNotEmpty(feePlan.getTrdFeeConfig().getCommCalMethod())) {
	                if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按申请编号汇总.getValue())) {
	                    mapTrdDetail = detailDtos.stream().collect(Collectors.groupingBy(TrdDetailDto::getApplyTradeId));
	                }
	            }
	            for (TrdDetailDto detailDto : detailDtos) {
	                Integer retentionBitTemp = null;
	                if (feePlan.getTrdFeeConfig().getRetentionBit() != null) {
	                    retentionBitTemp = feePlan.getTrdFeeConfig().getRetentionBit().intValue();
	                }
	                List<FeeInfo> commFeeInfos = firstFeeCalc(detailDto, feeInfos, localPlanSearchInfo, tradeType, retentionBitTemp);
	                // 循环 返回值费用列表
	                List<TrdTradeFeeDetailExtDto> listTrdTradeFeeDetailExtDto = new ArrayList<TrdTradeFeeDetailExtDto>();
	                for (FeeInfo commFeeInfo : commFeeInfos) {
	                    TrdTradeFeeDetailExtDto trdTradeFeeDetailExtDto = new TrdTradeFeeDetailExtDto();
	                    trdTradeFeeDetailExtDto.setFeeType(commFeeInfo.getFeeType());
	                    trdTradeFeeDetailExtDto.setFeeAmount(commFeeInfo.getFeeAmount());
	                    listTrdTradeFeeDetailExtDto.add(trdTradeFeeDetailExtDto);
	                    /*// 循环 变量.交易明细.交易费用明细表列表
	                    for (TrdTradeFeeDetailExtDto trdTradeFeeDetailExtDto : detailDto.getListTrdTradeFeeDetailExtDto()) {
	                        if (trdTradeFeeDetailExtDto.getFeeType().equals(commFeeInfo.getFeeType())) {
	                            // 变量.交易费用明细表.佣金计算过程中费用金额 = 变量.佣金计算过程中费用信息.金额
	                            trdTradeFeeDetailExtDto.setFeeAmount(commFeeInfo.getFeeAmount());
	                        }
	                    }*/
	                    CommisionDetail commisionDetail = new CommisionDetail();
	                    //交易编号
	                    commisionDetail.setTradeId(detailDto.getTradeId());
	                    // 费用类型
	                    commisionDetail.setFeeType(commFeeInfo.getFeeType());
	                    // 是否随交易支付
	                    commisionDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
	                    // 费用金额
	                    commisionDetail.setFeeAmt(commFeeInfo.getFeeAmount());
	                    commisionDetailList.add(commisionDetail);
	                }
	                detailDto.setListTrdTradeFeeDetailExtDto(listTrdTradeFeeDetailExtDto);
	            }
	        }
	        
	        // 3.费用计算
	        //变量.交易汇总列表
	        /*List<TrdDetailDto> trdDetailDtos = null;
	        if (detailDtos.size() > 1 && !StringUtil.equals(DictCodeListExt3.佣金计算方式_按成交编号计算.getValue()
	                , feePlan.getTrdFeeConfig().getCommCalMethod())) {
	            trdDetailDtos = trdAmtAll(detailDtos, feePlan.getTrdFeeConfig().getCommCalMethod(), String.valueOf(batch));
	        }
	        else {
	            trdDetailDtos = detailDtos;
	        }*/
	        List<TrdDetailDto> trdDetailDtos = trdAmtAll(detailDtos, feePlan.getTrdFeeConfig().getCommCalMethod(), String.valueOf(batch));
	        
	        // 按证券代码计算时佣金费用金额
            BigDecimal commFeeAmountFinId =  BigDecimal.ZERO;
            // 按证券代码计算时调用共通方法【计算信息设置】
            BigDecimal jsjeFinId =  BigDecimal.ZERO;

	        //循环 变量.交易汇总列表
	        for (TrdDetailDto trdDetailDto : trdDetailDtos) {
	            // 计算基数
	            BigDecimal calcBasis = BigDecimal.ZERO;
	            // if 变量.费用方案.计费基础 = “金额”
	            if (feePlan.getTrdFeeConfig().getChargeBasis().equals(DictCodeListExt1.计费基数_成交金额.getValue())) {
	                calcBasis = trdDetailDto.getFpriceAmt();
	            } else if(StringUtil.equals(feePlan.getTrdFeeConfig().getChargeBasis(), DictCodeListExt1.计费基数_券面总额.getValue())) {
	                calcBasis = trdDetailDto.getShareAmt();
	                if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), tradeType)) {
	                    calcBasis = trdDetailDto.getShareAmt().multiply(BigDecimal.valueOf(100));
	                }
	            }
	            // 调用共通方法【计算信息设置】
	            List<CalcParamDto> calcParamDtos = calcInfoSet(feePlan, calcBasis, feePlan.getTrdFeeConfig().getRetentionBit().intValue());
	            // 佣金包含经手费金额
	            BigDecimal includeJsfAmt = BigDecimal.ZERO;
	            // 佣金包含结算费金额
	            BigDecimal includeSettleFee = BigDecimal.ZERO;

	            // 佣金包含证管费金额
	            BigDecimal includeZgfAmt = BigDecimal.ZERO ;

	            // 佣金包含过户费金额
	            BigDecimal includeGhfAmt = BigDecimal.ZERO;

	            // 佣金包含印花税金额
	            BigDecimal includeStampFee = BigDecimal.ZERO;

	            // 佣金费用金额
	            BigDecimal commFeeAmount =  BigDecimal.ZERO;


	            for (CalcParamDto calcParamDto : calcParamDtos) {
	                // 调用共通方法【费用金额计算】
	                BigDecimal amount = feeAmountCalc(calcParamDto);

	                // 限额处理
	                amount = limitResolve(calcParamDto, amount).setScale(retentionBit, RoundingMode.HALF_UP);
	                
	                // 变量.佣金费用金额 = 变量.佣金费用金额 + 上述返回金额
	                commFeeAmount = commFeeAmount.add(amount);
	            }
	            
	            if (trdDetailDto.getListTrdTradeFeeDetailExtDto() != null && trdDetailDto.getListTrdTradeFeeDetailExtDto().size() > 0) {
	                for (TrdTradeFeeDetailExtDto trdTradeFeeDetailExtDto : trdDetailDto.getListTrdTradeFeeDetailExtDto()) {
	                    if (StringUtil.equals(trdTradeFeeDetailExtDto.getFeeType(), DictCodeListExt1.一次性费用类型_经手费.getValue())) {
	                        includeJsfAmt = trdTradeFeeDetailExtDto.getFeeAmount();
	                    }
	                    if (StringUtil.equals(trdTradeFeeDetailExtDto.getFeeType(), DictCodeListExt1.一次性费用类型_结算费.getValue())) {
	                        includeSettleFee = trdTradeFeeDetailExtDto.getFeeAmount();
	                    }
	                    if (StringUtil.equals(trdTradeFeeDetailExtDto.getFeeType(), DictCodeListExt1.一次性费用类型_证管费.getValue())) {
	                        includeZgfAmt = trdTradeFeeDetailExtDto.getFeeAmount();
	                    }
	                    if (StringUtil.equals(trdTradeFeeDetailExtDto.getFeeType(), DictCodeListExt1.一次性费用类型_过户费.getValue())) {
	                        includeGhfAmt = trdTradeFeeDetailExtDto.getFeeAmount();
	                    }
	                    if (StringUtil.equals(trdTradeFeeDetailExtDto.getFeeType(), DictCodeListExt1.一次性费用类型_印花税.getValue())) {
	                        includeStampFee = trdTradeFeeDetailExtDto.getFeeAmount();
	                    }
	    
	                }
	            }
	            // 变量.佣金费用金额 = 变量.佣金费用金额 - 变量.汇总交易.佣金计算过程中费用金额
	            if (trdDetailDto.getFeeAmount() != null) {
	                commFeeAmount = commFeeAmount.subtract(trdDetailDto.getFeeAmount());
	            }
	            /*if (commFeeAmount != null) {
	                commFeeAmount = limitResolve(calcParamDtos.get(0), commFeeAmount).setScale(2, RoundingMode.HALF_UP);
	            }*/
	            // 【出参】佣金金额 =+ 变量.佣金费用金额
	            commfeeAmount = commfeeAmount.add(commFeeAmount);

	            if (StringUtil.equals(DictCodeList.是否_是.getValue(), isAlloc)) {
	                CommissionCalcDetail commissionCalcDetail = new CommissionCalcDetail();
	                // 佣金计算方式
	                commissionCalcDetail.setCommCalMethod(feePlan.getTrdFeeConfig().getCommCalMethod());

	                // 业务编号
	                commissionCalcDetail.setBussinessId(trdDetailDto.getTradeId());

	                // 计算基数
	                commissionCalcDetail.setCalcBase(calcParamDtos.get(0).getJsje() != null ? calcParamDtos.get(0).getJsje().setScale(2, RoundingMode.HALF_UP) : null);

	                // 费率
	                commissionCalcDetail.setFeeRate(calcParamDtos.get(0).getCalcRate());

	                // 佣金包含经手费金额
	                commissionCalcDetail.setIncludeJsfAmt(includeJsfAmt);

	                //佣金包含结算费金额
	                commissionCalcDetail.setIncludeSettleFee(includeSettleFee);

	                // 佣金包含证管费金额
	                commissionCalcDetail.setIncludeZgfAmt(includeZgfAmt);

	                // 佣金包含过户费金额
	                commissionCalcDetail.setIncludeGhfAmt(includeGhfAmt);

	                // 佣金包含印花税金额
	                commissionCalcDetail.setIncludeStampFee(includeStampFee);

	                // 限额最大值
	                commissionCalcDetail.setMaxQuotaAmt(calcParamDtos.get(0).getFeeMaxi());

	                // 限额最小值
	                commissionCalcDetail.setMinQuotaAmt(calcParamDtos.get(0).getFeeMini());

	               //佣金金额
	                commissionCalcDetail.setCommissionAmt(commFeeAmount);

	                // 批次号
	                commissionCalcDetail.setBatch(batch);

	                commissionCalcDetailList.add(commissionCalcDetail);
	            }

	            if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按成交编号计算.getValue())) {
	                CommisionDetail commisionDetail = new CommisionDetail();
	                //交易编号
	                commisionDetail.setTradeId(trdDetailDto.getTradeId());
	                // 费用类型
	                commisionDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
	                // 是否随交易支付
	                commisionDetail.setIsPayWithTrade(feePlan.getTrdFeeConfig().getIsPayWithTrade());
	                // 费用金额
	                commisionDetail.setFeeAmt(commFeeAmount);
	                commisionDetailList.add(commisionDetail);
	            }
	            else if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按申请编号汇总.getValue())) {
	                // 变量.交易明细列表 = 变量.申请编号MAP.get(变量.汇总交易.交易编号)
	                List<TrdDetailDto> trdDetailDtos1 = mapTrdDetail.get(trdDetailDto.getTradeId());
	                List<DetailAmount> amounts = new ArrayList<>();
	                if (!StringUtil.isEmptyList(trdDetailDtos1)) {
						for (TrdDetailDto detailDto : trdDetailDtos1) {
							DetailAmount detailAmount = new DetailAmount();
							if (feePlan.getTrdFeeConfig().getChargeBasis().equals(DictCodeListExt1.计费基数_成交金额.getValue())) {
								detailAmount.setMixAmount(detailDto.getFpriceAmt());
							} else if (feePlan.getTrdFeeConfig().getChargeBasis().equals(DictCodeListExt1.计费基数_券面总额.getValue())) {
								detailAmount.setMixAmount(detailDto.getShareAmt());
							} else {
								detailAmount.setMixAmount(detailDto.getCpriceAmt());
							}
							detailAmount.setDetailNo(detailDto.getTradeId());
							amounts.add(detailAmount);
						}
					}

	                List<BigDecimal> allAllotAmounts = new ArrayList<>();
	                allAllotAmounts.add(commFeeAmount);
	                // 调用共通方法【金额平均分配】
	                List<DetailAmount> detailAmounts
	                        = amountEvenlyDistributed(amounts, calcParamDtos.get(0).getJsje(), allAllotAmounts, retentionBit);
	                if (!StringUtil.isEmptyList(detailAmounts)) {
						for (DetailAmount detailAmount : detailAmounts) {
							CommisionDetail commisionDetail = new CommisionDetail();
							//交易编号
							commisionDetail.setTradeId(detailAmount.getDetailNo());
							// 费用类型
							commisionDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
							// 是否随交易支付
							commisionDetail.setIsPayWithTrade(feePlan.getTrdFeeConfig().getIsPayWithTrade());
							// 费用金额
							commisionDetail.setFeeAmt(detailAmount.getAllotAmountList()[0]);
							commisionDetailList.add(commisionDetail);
						}
					}

	            }
	            else if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按证券代码汇总.getValue())) {
	                commFeeAmountFinId = commFeeAmount;
	                jsjeFinId = calcParamDtos.get(0).getJsje();
	            }
	        }
	        if (feePlan.getTrdFeeConfig().getCommCalMethod().equals(DictCodeListExt3.佣金计算方式_按证券代码汇总.getValue())) {
	            List<DetailAmount> amounts = new ArrayList<>();
	            List<BigDecimal> allAllotAmounts = new ArrayList<>();
	            for (TrdDetailDto trdDetailDto : detailDtos){
	                DetailAmount detailAmount = new DetailAmount();
	                if (feePlan.getTrdFeeConfig().getChargeBasis().equals(DictCodeListExt1.计费基数_成交金额.getValue())) {
	                    detailAmount.setMixAmount(trdDetailDto.getFpriceAmt());
	                } else if (feePlan.getTrdFeeConfig().getChargeBasis().equals(DictCodeListExt1.计费基数_券面总额.getValue())) {
	                    detailAmount.setMixAmount(trdDetailDto.getShareAmt());
	                } else {
	                    detailAmount.setMixAmount(trdDetailDto.getCpriceAmt());
	                }
	                detailAmount.setDetailNo(trdDetailDto.getTradeId());
	                amounts.add(detailAmount);
	                allAllotAmounts.add(commFeeAmountFinId);
	            }
	            // 调用共通方法【金额平均分配】
                List<DetailAmount> detailAmounts = amountEvenlyDistributed(amounts, jsjeFinId, allAllotAmounts, retentionBit);
                for (DetailAmount detailAmount1 : detailAmounts) {
                    CommisionDetail commisionDetail = new CommisionDetail();
                    //交易编号
                    commisionDetail.setTradeId(detailAmount1.getDetailNo());
                    // 费用类型
                    commisionDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
                    // 是否随交易支付
                    commisionDetail.setIsPayWithTrade(feePlan.getTrdFeeConfig().getIsPayWithTrade());
                    // 费用金额
                    commisionDetail.setFeeAmt(detailAmount1.getAllotAmountList()[0]);
                    commisionDetailList.add(commisionDetail);
                }
            }
		}
		
        

        CommissionCalcDto res = new CommissionCalcDto();
		res.setCommisionAmount(commfeeAmount.setScale(retentionBit, RoundingMode.HALF_UP));
		res.setCommisionDetailList(commisionDetailList);
        res.setCommissionCalcDetailList(commissionCalcDetailList);
		return res;
    }

    /**
     * 佣金计算（画面用）
     * @param trdDetailDto  交易明细
     * @param retentionBit 小数位数
     * @param tradeType 交易品种
     * @return
     */
    BigDecimal calcWayPage(TrdDetailDto trdDetailDto , Integer retentionBit, String tradeType) {
        List<TrdDetailDto> trdDetailDtos = new ArrayList<>();
        trdDetailDtos.add(trdDetailDto);

        CommissionCalcDto commissionCalcDto = calcWayCommon(trdDetailDtos, null, retentionBit, null, null, tradeType);

        return commissionCalcDto.getCommisionAmount();
    }

    /**
     * 获取费用方案
     *
     * @return
     */
    private FeePlan getFeePlan(PlanSearchInfo planSearchInfo, Date feeDate) {
        FeePlan feePlan = null;
        Map<String, Object> searchParam = new HashMap<>();
        // 1.1 交易费用配置查询
        searchParam.put("planInfo", planSearchInfo);
        searchParam.put("feeDate", feeDate);
        List<TrdFeeConfig> trdFeeConfigs = trdFeeCalcMapper.getTrdFeeConfig(searchParam);
        if (trdFeeConfigs != null && !trdFeeConfigs.isEmpty()) {
            feePlan = new FeePlan();
			// 取第一条数据
			feePlan.setTrdFeeConfig(trdFeeConfigs.get(0));
			// 1.2 交易费用配置明细
			List<TrdFeeConfigDetail> trdFeeConfigDetails = trdFeeCalcMapper
					.getTrdFeeConfigDetail(trdFeeConfigs.get(0).getSeqNo());
            feePlan.setTrdFeeConfigDetails(trdFeeConfigDetails);
        }
        return feePlan;
    }

    /**
     * 计算信息设置
     *
     * @param feePlan      费用方案
     * @param value        金额
     * @param retentionBit 保留位数
     * @return 计算参数列表
     * @see
     * @since 1.0
     */
    private List<CalcParamDto> calcInfoSet(FeePlan feePlan, BigDecimal value, int retentionBit) {
        // 计算参数列表
        List<CalcParamDto> res = new ArrayList<CalcParamDto>();
        // 【入参】费用方案.是否分档 = "是"
        if (DictCodeList.是否_是.getValue().equals(feePlan.getTrdFeeConfig().getIsGrade())) {
            // 剩余金额 = 入参.金额
            BigDecimal remainBal = value;
            // 逐档金额 = 0
            BigDecimal gradeBal = BigDecimal.ZERO;
            
            // 循环 【入参】费用方案.方案明细列表
            for (int i = 0 ; i < feePlan.getTrdFeeConfigDetails().size(); i ++ ){
                TrdFeeConfigDetail detail = feePlan.getTrdFeeConfigDetails().get(i);
            // for (TrdFeeConfigDetail detail : feePlan.getTrdFeeConfigDetails()) {
                // 费用方案.分档模式 = "逐档计算汇总"
                if (DictCodeListExt3.分档模式_逐档计算汇总.getValue().equals(feePlan.getTrdFeeConfig().getGradeMode())) {
                    CalcParamDto calcParamDto = new CalcParamDto();
                    // if 变量.方案明细.分档条件 = "小于等于"
                    if (DictCodeListExt3.分档条件_小于等于.getValue().equals(detail.getGradeCondition())) {
                        // if 入参.金额 <= 变量.方案明细.分档金额
                        if (value.compareTo(detail.getGradeAmt()) <= 0) {
                            // 计费类型
                            setCalcParamDto(feePlan, retentionBit, remainBal, detail, calcParamDto);
                            res.add(calcParamDto);
                            return res;
                        } else {
                            // 计费类型
                            setCalcParamDto(feePlan, retentionBit, detail.getGradeAmt().subtract(gradeBal), detail, calcParamDto);
                            res.add(calcParamDto);
                            // 变量.剩余金额 = 变量.剩余金额 - （变量.方案明细.分档金额 - 变量.逐档金额）
                            remainBal = remainBal.subtract(detail.getGradeAmt().subtract(gradeBal));
                            // 变量.逐档金额 = 变量.方案明细.分档金额
                            gradeBal = detail.getGradeAmt();
                        }
                    } else {
                        // 计费类型
                        setCalcParamDto(feePlan, retentionBit, remainBal, detail, calcParamDto);
                        res.add(calcParamDto);
                        return res;
                    }
                } else {
                    CalcParamDto calcParamDto = new CalcParamDto();
                    if (DictCodeListExt3.分档条件_小于等于.getValue().equals(detail.getGradeCondition())) {
                        // if 入参.金额 <= 变量.方案明细.分档金额
                        if (value.compareTo(detail.getGradeAmt()) <= 0) {
                            // 计费类型
                            setCalcParamDto(feePlan, retentionBit, remainBal, detail, calcParamDto);
                            res.add(calcParamDto);
                            return res;
                        } else {
                            // 计费类型
                            setCalcParamDto(feePlan, retentionBit, detail.getGradeAmt().subtract(gradeBal), detail, calcParamDto);
                            res.add(calcParamDto);
                            // 变量.剩余金额 = 变量.剩余金额 - 变量.方案明细.分档金额
                            remainBal = remainBal.subtract(detail.getGradeAmt().subtract(gradeBal));
                            // 变量.逐档金额 = 变量.方案明细.分档金额
                            gradeBal = detail.getGradeAmt();
                        }
                    } else {
                        if (i == feePlan.getTrdFeeConfigDetails().size() - 1){
                            // 计费类型
                            setCalcParamDto(feePlan, retentionBit, remainBal, detail, calcParamDto);
                            res.add(calcParamDto);
                            return res;
                        }
                        continue;
                    }
                }
            }
        } else {
            CalcParamDto calcParamDto = new CalcParamDto();
            TrdFeeConfigDetail detail = feePlan.getTrdFeeConfigDetails().get(0);
            // 计费类型
            setCalcParamDto(feePlan, retentionBit, value, detail, calcParamDto);
            res.add(calcParamDto);
        }
        return res;
    }

    /**
     * 设置计算参数
     *
     * @param feePlan
     * @param retentionBit
     * @param remainBal
     * @param detail
     * @param calcParamDto
     */
    private void setCalcParamDto(FeePlan feePlan, int retentionBit, BigDecimal remainBal, TrdFeeConfigDetail detail, CalcParamDto calcParamDto) {
        // 计费类型
        calcParamDto.setChargeKind(feePlan.getTrdFeeConfig().getChargeType());
        // 计算金额
        calcParamDto.setJsje(remainBal);
        // 计算利率
        calcParamDto.setCalcRate(detail.getChargeRate());
        // 保留位数
        calcParamDto.setRetentionBit(retentionBit);
        // 上限
        calcParamDto.setFeeMaxi(detail.getFeeMaxi());
        // 下限
        calcParamDto.setFeeMini(detail.getFeeMini());
        // 固定费用
        calcParamDto.setExpenseAmount(detail.getChargeValue());
    }

    /**
     * 费用金额计算
     *
     * @param calcParam 计算参数
     * @return 金额
     */
    private BigDecimal feeAmountCalc(CalcParamDto calcParam) {
        // if 【入参】计算参数.计费类型 = "固定费用"
        if (DictCodeListExt1.计费类型_固定金额.getValue().equals(calcParam.getChargeKind())) {
            return calcParam.getExpenseAmount();
        }
		BigDecimal temp = ArithmeticUtil.multiply(calcParam.getJsje(), calcParam.getCalcRate());
        if(null != temp){
			return temp.setScale(calcParam.getRetentionBit(), RoundingMode.HALF_UP);
		}
        return new BigDecimal(0);
    }

    /**
     * 限额处理
     *
     * @param calcParam 计算参数
     * @param value     金额
     * @return 金额
     */
    private BigDecimal limitResolve(CalcParamDto calcParam, BigDecimal value) {
        BigDecimal res = value;
        if (calcParam.getFeeMaxi() != null) {
            // 【出参】金额 = min(【入参】计算参数.上限,【入参】金额)
            if (calcParam.getFeeMaxi().compareTo(value) <= 0){
                return calcParam.getFeeMaxi();
            }
        }
        if (calcParam.getFeeMini() != null) {
            // 【出参】金额 = max(【入参】计算参数.下限,【入参】金额)
            if (calcParam.getFeeMini().compareTo(value) >= 0){
                return calcParam.getFeeMini();
            }
        }
        return res;
    }

    /**
     * 交易金额汇总
     *
     * @param trdDetailDtos 交易明细列表
     * @param commCalMethod 佣金计算方式
     * @param batchId       批次号
     * @return 交易汇总列表
     * @see
     * @since 1.0
     */
    private List<TrdDetailDto> trdAmtAll(List<TrdDetailDto> trdDetailDtos, String commCalMethod, String batchId) {
        final List<TrdDetailDto> res = new ArrayList<>();
        // 佣金计算方式 = "按证券代码汇总"
        if (DictCodeListExt3.佣金计算方式_按证券代码汇总.getValue().equals(commCalMethod)) {
            // 定义汇总交易
            TrdDetailDto allDetail = new TrdDetailDto();
            // 变量.汇总交易.交易编号 = 【入参】批次号
            allDetail.setTradeId(batchId);
            allDetail = setTrdAmtAll(trdDetailDtos, allDetail);
            res.add(allDetail);
        }
        if (DictCodeListExt3.佣金计算方式_按申请编号汇总.getValue().equals(commCalMethod)) {
            // 按照申请编号分组，循环分组信息
            Map<String, List<TrdDetailDto>> listMap = trdDetailDtos.stream()
                    .collect(Collectors.groupingBy(TrdDetailDto::getApplyTradeId));
            listMap.forEach((x, y) -> {
                // 定义汇总交易
                TrdDetailDto allDetail = new TrdDetailDto();
                // 变量.汇总交易.交易编号 = 第一条数据的 申请编号
                allDetail.setTradeId(x);
                allDetail = setTrdAmtAll(listMap.get(x), allDetail);
                res.add(allDetail);
            });
        }
        if (DictCodeListExt3.佣金计算方式_按成交编号计算.getValue().equals(commCalMethod)) {
            return trdDetailDtos;
        }
        return res;
    }

    /**
     * 交易汇总字段设置
     *
     * @param trdDetailDtos 交易明细
     * @param allDetail     汇总交易
     * @return 设置后的汇总交易
     */
    private TrdDetailDto setTrdAmtAll(List<TrdDetailDto> trdDetailDtos, TrdDetailDto allDetail) {
        // 净价总额
        BigDecimal cpriceAmt = BigDecimal.ZERO;

        // 交易份额
        BigDecimal shareAmt = BigDecimal.ZERO;

        // 交易本金
        BigDecimal prinAmt = BigDecimal.ZERO;

        // 利息总额
        BigDecimal intAmt = BigDecimal.ZERO;

        // 全价总额
        BigDecimal fpriceAmt = BigDecimal.ZERO;

        for (TrdDetailDto trdDetailDto : trdDetailDtos){
            // 净价总额 相加
            cpriceAmt = ArithmeticUtil.doAdd(cpriceAmt, trdDetailDto.getCpriceAmt());
            // 交易份额 相加
            shareAmt = ArithmeticUtil.doAdd(shareAmt, trdDetailDto.getShareAmt());
            // 交易本金 相加
            prinAmt = ArithmeticUtil.doAdd(prinAmt, trdDetailDto.getPrinAmt());
            // 利息总额 相加
            intAmt = ArithmeticUtil.doAdd(intAmt, trdDetailDto.getIntAmt());
            // 全价总额 相加
            fpriceAmt = ArithmeticUtil.doAdd(fpriceAmt, trdDetailDto.getFpriceAmt());
        }
        /*trdDetailDtos.forEach(x -> {
            // 净价总额 相加
            cpriceAmt.add(x.getCpriceAmt());
            // 交易份额 相加
            shareAmt.add(x.getShareAmt());
            // 交易本金 相加
            prinAmt.add(x.getPrinAmt());
            // 利息总额 相加
            intAmt.add(x.getIntAmt());
            // 全价总额 相加
            fpriceAmt.add(x.getFpriceAmt());
        });*/

        allDetail.setCpriceAmt(cpriceAmt);
        allDetail.setShareAmt(shareAmt);
        allDetail.setPrinAmt(prinAmt);
        allDetail.setIntAmt(intAmt);
        allDetail.setFpriceAmt(fpriceAmt);

        // 明细列表
        List<TrdTradeFeeDetailExtDto> trdTradeFeeDetailExtDtos = new ArrayList<>();
        for (TrdDetailDto dto : trdDetailDtos) {
        	if (!StringUtil.isEmptyList(dto.getListTrdTradeFeeDetailExtDto())) {
				trdTradeFeeDetailExtDtos.addAll(dto.getListTrdTradeFeeDetailExtDto());
			}
        }

        // 分组
        Map<String, List<TrdTradeFeeDetailExtDto>> listMap = trdTradeFeeDetailExtDtos.stream()
                .collect(Collectors.groupingBy(TrdTradeFeeDetailExtDto::getFeeType));
        List<TrdTradeFeeDetailExtDto> resList = new ArrayList<>();

        listMap.forEach((x, y) -> {
            TrdTradeFeeDetailExtDto dto = new TrdTradeFeeDetailExtDto();
            // 费用类型
            dto.setFeeType(x);
            // 费用金额
            BigDecimal feeAmt = BigDecimal.ZERO;

            // 佣金计算过程中费用金额
            BigDecimal feeAmount = BigDecimal.ZERO;
            for (TrdTradeFeeDetailExtDto trdTradeFeeDetailExtDto : y){
                feeAmt = ArithmeticUtil.doAdd(feeAmt, trdTradeFeeDetailExtDto.getFeeAmt());
                feeAmount = ArithmeticUtil.doAdd(feeAmount, trdTradeFeeDetailExtDto.getFeeAmount());
            }
            /*y.forEach(trdTradeFeeDetailExtDto -> {
                feeAmt.add(trdTradeFeeDetailExtDto.getFeeAmt());
                feeAmount.add(trdTradeFeeDetailExtDto.getFeeAmount());
            });*/
            dto.setFeeAmt(feeAmt);
            dto.setFeeAmount(feeAmount);
            resList.add(dto);
        });

        // 佣金计算过程中费用金额
        BigDecimal feeAmount = BigDecimal.ZERO;

        allDetail.setListTrdTradeFeeDetailExtDto(resList);
        for (TrdTradeFeeDetailExtDto feeDetail : resList){
            feeAmount = feeAmount.add(feeDetail.getFeeAmount());
        }

        allDetail.setFeeAmount(feeAmount);

        return allDetail;
    }

    /**
     * 金额平均分配
     *
     * @param amounts        明细金额列表
     * @param allMixAmount   占比总金额
     * @param retentionBit   小数位数 - 默认为2
     * @return 明细金额列表
     * @see
     * @since 1.0
     */
    private List<DetailAmount> amountEvenlyDistributed(List<DetailAmount> amounts, BigDecimal allMixAmount,
                                                       List<BigDecimal> allAllotAmounts, Integer retentionBit) {
        if (retentionBit == null) {
            retentionBit = 2;
        }
        // 将明细金额列表以占比金额从小到大排序
        amounts.sort((a, b) -> a.getMixAmount().compareTo(b.getMixAmount()));
        for (int i = 0; i < allAllotAmounts.size(); i++) {
            BigDecimal allAllotAmount = allAllotAmounts.get(i);
            // 已分配金额
            BigDecimal hasAllotAmount = BigDecimal.ZERO;
            for (int j = 0; j < amounts.size(); j++) {

                DetailAmount amount = amounts.get(j);
                if (amount.getAllotAmountList() == null) {
                    BigDecimal[] allotAmountArray = new BigDecimal[allAllotAmounts.size()];
                    amount.setAllotAmountList(allotAmountArray);
                }
                if (j == amounts.size() - 1) {
                    // 最后一条数据
                    // 变量.明细金额.分配金额【i】 = 分配总金额 - 变量.已分配金额
                    amount.getAllotAmountList()[i] = allAllotAmount.subtract(hasAllotAmount);
                } else {
                    // 变量.明细金额.分配金额【i】 = 分配总金额*占比金额/占比总金额
                    amount.getAllotAmountList()[i] = allAllotAmount.multiply(amount.getMixAmount()).divide(allMixAmount, retentionBit, RoundingMode.HALF_UP);
                    //变量.已分配金额 += 变量.明细金额.分配金额【i】
                    hasAllotAmount = hasAllotAmount.add(amount.getAllotAmountList()[i]);
                }

            }

        }
        return amounts;
    }
}
