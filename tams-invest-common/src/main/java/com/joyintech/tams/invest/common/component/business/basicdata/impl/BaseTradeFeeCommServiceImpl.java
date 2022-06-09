package com.joyintech.tams.invest.common.component.business.basicdata.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joyintech.tams.invest.common.CommonUtil;
import org.jasypt.commons.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.component.business.basicdata.dao.BaseTradeFeeCommMapper;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.BasicTradeFeeInputDto;
import com.joyintech.tams.invest.common.component.business.basicdata.dto.MstCommissionSetDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;

/**
 * 费用计算实现类
 * @author 裴家俊
 * @version 1.0 2019年3月28日
 * @see BaseTradeFeeCommServiceImpl
 * @since 1.0
 */
@Service
public class BaseTradeFeeCommServiceImpl implements BaseTradeFeeCommService {

    /**
     * @see BaseTradeFeeCommMapper
     */
    @Autowired
    private BaseTradeFeeCommMapper baseTradeFeeCommMapper;

	private static final List<String> 上海_深圳 = Arrays.asList(BussinessDictCodeList.交易场所_上海证券交易所.getValue(),
            BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
    /**
     * 银行间费用计算
     */
    @Override
    public List<TrdTradeFeeDetail> innerBankFeeDeal(BasicTradeFeeInputDto inputDto) {
        List<TrdTradeFeeDetail>  feeConfigList=new ArrayList<TrdTradeFeeDetail>();
        if(!checkIsEmpty(inputDto)){
        	return feeConfigList;
        }
    	if(StringUtil.isNotEmpty(inputDto.getTradeMarket())&&StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), inputDto.getTradeMarket())){
    		for(String feeType:inputDto.getFeeTypeList()){
    			//调用交易费用组件.费用配置查询
    			BasicTradeFeeInputDto searchDto=new BasicTradeFeeInputDto();
    			BeanUtil.copyPropertiesIgnoreNull(searchDto, inputDto);
    			//交易手续费不需要传入登记托管机构
    			if(StringUtil.equals(BussinessDictCodeList.费用类型_交易手续费.getValue(), feeType)){
    				searchDto.setRegistOrg(null);
    			}
    			searchDto.setFeeType(feeType);
    			searchDto.setSecAcctId(null);
                if (StringUtil.equals(searchDto.getIsGrade(), "0")) {
                    continue;
                }                    
    			List<MstCommissionSetDto> feeDetailList=baseTradeFeeCommMapper.selectFeeConfig(searchDto);
    			if(feeDetailList.size()<=0){
    				continue;
    			}
    			MstCommissionSetDto retFeeDetailDto=feeDetailList.get(0);
    			
    			//调用费用计算
    			TrdTradeFeeDetail feeCofigDetail=feeMathMethod(feeType,retFeeDetailDto,inputDto);
    			feeCofigDetail.setIsPayWithTrade(BussinessDictCodeList.是否_否.getValue());
    			feeConfigList.add(feeCofigDetail);
    		}
    	}
    	
        return feeConfigList;
    }
    
    /**
     * 交易所佣金费用计算
     */
    @Override
    public List<TrdTradeFeeDetail> bourseFeeDeal(BasicTradeFeeInputDto inputDto) {
    	 List<TrdTradeFeeDetail>  feeConfigList=new ArrayList<TrdTradeFeeDetail>();
         if(!checkIsEmpty(inputDto)){
         	return feeConfigList;
         }
         //add by wangpeng 20190816 债券优先股
    	if(StringUtil.equals(inputDto.getFinprodType2(), BussinessDictCodeList.平安资产类型_债券.getValue())
    			&&StringUtil.equals(inputDto.getType6(), DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue())){
    		inputDto.setFinprodType2(BussinessDictCodeList.平安资产类型_股票.getValue());
    	}
         //add end
    	if(StringUtil.isNotEmpty(inputDto.getTradeMarket())&&上海_深圳.contains(inputDto.getTradeMarket())){
    		for(String feeType:inputDto.getFeeTypeList()){
//    			if(StringUtil.equals(feeType,BussinessDictCodeList.费用类型_佣金.getValue())&&StringUtil.isEmpty(inputDto.getSecAcctId()))continue;
    			//调用交易费用组件.费用配置查询
    			BasicTradeFeeInputDto searchDto=new BasicTradeFeeInputDto();
    			BeanUtil.copyPropertiesIgnoreNull(searchDto, inputDto);
				//如果查询的是结算费或经手费，置SEC_ACCT_ID为空

				if(StringUtil.equals(feeType,BussinessDictCodeList.费用类型_结算费.getValue())||
						StringUtil.equals(feeType,BussinessDictCodeList.费用类型_经手费.getValue())){
					searchDto.setSecAcctId(null);
				}
    			searchDto.setFeeType(feeType);
    			searchDto.setRegistOrg(null);
    			//查询出所有符合条件的费用规则（按照生效日逆序，资产分类一、资产分类二升序排序）
    			List<MstCommissionSetDto> feeDetailList=baseTradeFeeCommMapper.selectFeeConfig(searchDto);
				MstCommissionSetDto retFeeDetailDto=new MstCommissionSetDto();
				//当前交易的全价总额
				BigDecimal fpriceAmt=inputDto.getFpriceAmt();
				//当前交易的券面总额
				BigDecimal tradeAmt = inputDto.getTradeAmt();
				//遍历所有查询出的费用规则，找到最合适的一个费用规则
				for(MstCommissionSetDto dto:feeDetailList){
					/**判断是否能找到一个种类（若不分档，该种类只有一条费用规则，若分档，该种类有大于1条费用规则）的符合要求的费用规则
					 * 实现条件：该债券信息中资产分类一等于费用规则的资产分类一并且该债券信息中资产分类二等于费用规则的资产分类二
					 * 			或者债券信息中资产分类一等于费用规则的资产分类一并且费用规则的资产分类二为空
					 * 			或者费用规则的资产分类一为空并且费用规则的资产分类二为空
					 */
					if((CommonUtils.isNotEmpty(inputDto.getType6()) && inputDto.getType6().equals(dto.getAssetClassify1())) &&
							(CommonUtils.isNotEmpty(inputDto.getType7()) && inputDto.getType7().equals(dto.getAssetClassify2()))
							||(CommonUtils.isNotEmpty(inputDto.getType6()) && inputDto.getType6().equals(dto.getAssetClassify1()) && StringUtil.isEmpty(dto.getAssetClassify2()))
							||(StringUtil.isEmpty(dto.getAssetClassify1())&&StringUtil.isEmpty(dto.getAssetClassify2()))){
						/**满足条件，一定存在一条该种类下的费用规则满足要求
						 * 判断循环是否进入了下一个种类并且是否已经找到了满足条件的费用规则，若满足，跳出循环
						 * 实现条件：费用规则的费用规则中是否分档为“否”并且暂存的规则是否分档为“是”
						 * 			或者费用规则的资产分类二为空并且暂存的规则资产分类二不为空
						 * 			或者费用规则的资产分类一为空并且暂存的规则资产分类一不为空
						 * 		或者暂存的规则不为空并且暂存的规则生效日不等于循环中的规则生效日
						 */
 						if(("Y".equals(retFeeDetailDto.getIsGrade())&&!"Y".equals(dto.getIsGrade()))
								||(StringUtil.isNotEmpty(retFeeDetailDto.getAssetClassify2())&&StringUtil.isEmpty(dto.getAssetClassify2()))
								||(StringUtil.isNotEmpty(retFeeDetailDto.getAssetClassify1())&&StringUtil.isEmpty(dto.getAssetClassify1()))
								||(StringUtil.isNotEmpty(retFeeDetailDto.getBatchSeq())&&!retFeeDetailDto.getEffDate().equals(dto.getEffDate())))
							break;
						//判断该种类下的该费用规则是否分档
						if("Y".equals(dto.getIsGrade())){
							//获取分档时比较对象金额
							BigDecimal obj = new BigDecimal(0);
							if("01".equals(dto.getChargeType()) && "01".equals(dto.getChargeBasis())){
								obj = tradeAmt;
							}else{
								obj = fpriceAmt;
							}
							//若分档，判断该债券交易的（全价总额是否大于分档金额）或者（全价总额是否等于分档金额并且是否包含分档金额是否为“是”），若满足，暂时赋值该规则
							if(obj.compareTo(new BigDecimal(dto.getGradeAmount()))>0
									||obj.compareTo(new BigDecimal(dto.getGradeAmount()))==0&&"Y".equals(dto.getIsCludeStartamt()))
								retFeeDetailDto=dto;
						}else{
							//若不分档，赋值取到符合要求的费用规则，跳出循环
							retFeeDetailDto=dto;
							break;
						}
					}
				}
//				if(StringUtil.isEmpty(retFeeDetailDto.getBatchSeq()))continue;
    			//调用费用计算
    			TrdTradeFeeDetail feeCofigDetail=feeMathMethod(feeType,retFeeDetailDto,inputDto);
//    			feeCofigDetail.setIsPayWithTrade(BussinessDictCodeList.是否_是.getValue());
    			feeConfigList.add(feeCofigDetail);
    		}
    	}
    	
        return feeConfigList;
    }
    
    /**
     * 空值检查
     * @param inputDto
     * @return boolean
     */
    private boolean checkIsEmpty(BasicTradeFeeInputDto inputDto){
    	if(StringUtil.isBlank(inputDto.getTradeMarket())||StringUtil.isBlank(inputDto.getPs())||StringUtil.isEmptyList(inputDto.getFeeTypeList())){
    		return false;
    	}
//    	if(StringUtil.isNotEmpty(inputDto.getTradeMarket())&&StringUtil.equals(BussinessDictCodeList.交易场所_银行间.getValue(), inputDto.getTradeMarket())){
//    		if(StringUtil.isBlank(inputDto.getRegistOrg())){
//    			return false;
//    		}
//    	}
    	if(StringUtil.equals(BussinessDictCodeList.费用类型_佣金.getValue(), inputDto.getTradeMarket()) && StringUtil.isNotEmpty(inputDto.getFeeType())&&上海_深圳.contains(inputDto.getTradeMarket())){
    		if(StringUtil.isBlank(inputDto.getSecAcctId())){
    			return false;
    		}
    	}
    	return true;
    }
    /**
     * 费用计算
     * @param 
     * @return TrdTradeFeeDetail
     */
    private TrdTradeFeeDetail feeMathMethod(String feeType,MstCommissionSetDto retFeeDetailDto,BasicTradeFeeInputDto inputDto){
    	TrdTradeFeeDetail feeCofigDetail=new TrdTradeFeeDetail();
    	BigDecimal singleFee=null;
    	//费用计算
		if(BussinessDictCodeList.计费类型_固定金额.getValue().equals(retFeeDetailDto.getChargeType())){
			singleFee=retFeeDetailDto.getChargeValue();
		}
		if(BussinessDictCodeList.计费类型_固定费率.getValue().equals(retFeeDetailDto.getChargeType())){
			if(BussinessDictCodeList.计费基数_成交金额.getValue().equals(retFeeDetailDto.getChargeBasis())){
				if(StringUtil.isNotEmpty(inputDto.getFpriceAmt())){
					singleFee=ArithmeticUtil.round(ArithmeticUtil.multiply(inputDto.getFpriceAmt(),retFeeDetailDto.getChargeRate()),2, RoundingMode.HALF_UP);
				}
			}
			if(BussinessDictCodeList.计费基数_券面总额.getValue().equals(retFeeDetailDto.getChargeBasis())){
				if(StringUtil.isNotEmpty(inputDto.getPrinAmt())){
					singleFee=ArithmeticUtil.round(ArithmeticUtil.multiply(inputDto.getPrinAmt(),retFeeDetailDto.getChargeRate()),2,RoundingMode.HALF_UP);
				}
			}
			if(null !=singleFee){
				if(StringUtil.isNotEmpty(retFeeDetailDto.getFeeMini())&&singleFee.compareTo(retFeeDetailDto.getFeeMini())<=0){
					singleFee=retFeeDetailDto.getFeeMini();
				}
				if(StringUtil.isNotEmpty(retFeeDetailDto.getFeeMaxi())&&singleFee.compareTo(retFeeDetailDto.getFeeMaxi())>0){
					singleFee=retFeeDetailDto.getFeeMaxi();
				}
			}
		}
		feeCofigDetail.setFeeType(feeType);
		feeCofigDetail.setIsPayWithTrade(retFeeDetailDto.getIsPayWithTrade());
		feeCofigDetail.setFeeAmt(singleFee);
		return feeCofigDetail;
    }

}
