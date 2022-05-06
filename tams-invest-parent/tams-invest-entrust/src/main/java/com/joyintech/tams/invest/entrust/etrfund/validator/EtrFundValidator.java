/*
 * 文件名：EtrFundValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrfund.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etrfund.service.EtrFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 基金委托check
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年10月8日
 * @see EtrFundValidator
 * @since 1.0
 */
@Component
public class EtrFundValidator implements EtrCommonValidator {

    @Autowired
    private EtrFundService etrFundService;
    List<ValidMessage> resultList = new ArrayList<ValidMessage>();

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        EtrFundDto etrFundDto = (EtrFundDto)value;
//        if (WorkFlowUtil.isNotFromToDoList()) {//不走流程
            if (StringUtil.equals(etrFundDto.getOperateType(), "delete")) {//删除
                List<EtrFundDto> selectItem = etrFundDto.getSelectedItem();
                for(EtrFundDto temp:selectItem){
                   temp.setEntrustOperationType(DictCodeListExt1.委托操作类型_删除.getValue());
                   errorList.addAll(etrFundService.check(temp));
                }
            }
            if (StringUtil.equals(etrFundDto.getOperateType(), "recall")) {//撤销
                List<EtrFundDto> selectItem = etrFundDto.getSelectedItem();
                for(EtrFundDto temp:selectItem){
                    temp.setEntrustOperationType(DictCodeListExt1.委托操作类型_撤回.getValue());
                    errorList.addAll(etrFundService.check(temp));
                 }
            }
            if (StringUtil.equals(etrFundDto.getOperateType(), "close")) {//关闭
                List<EtrFundDto> selectItem = etrFundDto.getSelectedItem();
                for(EtrFundDto temp:selectItem){
                    temp.setEntrustOperationType(DictCodeListExt1.委托操作类型_关闭.getValue());
                    temp.setCloseWay(DictCodeListExt1.委托关闭方式_人工.getValue());
                    errorList.addAll(etrFundService.check(temp));
                 }

            }
//        }

        if(StringUtil.equals(etrFundDto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            // delete by renping 改为共通处理 2019-08-22 start
//            BigDecimal entrustAmt = etrFundDto.getEntrustAmt();
//            String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
//            if(StringUtil.equals(etrlimit_switch, "01")){
//                if(entrustAmt!=null){
//                    BigDecimal limitAmt = etrFundService.selectLimitAmt(etrFundDto);
//                    if(limitAmt != null && entrustAmt.compareTo(limitAmt)==1){
//                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_009",getMessage("EtrFund001.entrustAmt"))+limitAmt));
//                    }
//                }
//            }
        	//3)	提交时校验，委托日期区间如在标的的认购起始日和认购终止日之间，交易方向必须为认购，如不在，交易方向不可为认购；
        	if(StringUtil.isNotEmpty(etrFundDto.getFinprodId())&&StringUtil.isNotEmpty(etrFundDto.getPs())){
        		FinProductAdd finProductAdd = etrFundService.selectFinProductAdd(etrFundDto.getFinprodId());
        		if(StringUtil.isNotEmpty(finProductAdd)){
        			if((etrFundDto != null && etrFundDto.getMdate() != null && (DateUtil.compareDate(finProductAdd.getToffEnddate(), etrFundDto.getMdate())>=0))
        					&&(etrFundDto != null && etrFundDto.getVdate() != null && (DateUtil.compareDate(finProductAdd.getToffStrdate(), etrFundDto.getVdate())<=0))){
                    	//委托日期区间如在标的的认购起始日和认购终止日之间
        				if(!StringUtil.equals(etrFundDto.getPs(), DictCodeListExt2.交易方向_认购.getValue())){
        					errorList.add(new ValidMessage("ps","委托日期区间在标的的认购起始日和认购终止日之间，交易方向必须为认购!"));
        				}
                    }else{
                    	if(StringUtil.equals(etrFundDto.getPs(), DictCodeListExt2.交易方向_认购.getValue())){
        					errorList.add(new ValidMessage("ps","委托日期区间不在标的的认购起始日和认购终止日之间，交易方向不可为认购!"));
        				}
                    }
        			
        		}
        	}
        	//证券托管户交易场所为交易所时必填 需求变更为： 通道和证券账户二选一必填
        	if((StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), etrFundDto.getTradeMarket())||StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), etrFundDto.getTradeMarket()))
        			&&StringUtil.isEmpty(etrFundDto.getSecAcctId())&&StringUtil.isEmpty(etrFundDto.getChlId())){
        		errorList.add(new ValidMessage("secAcctId","交易场所为交易所时，合作资产和证券托管户二选一必填"));
        	}
        	
            // delete by renping 改为共通处理 2019-08-22 end
            if(StringUtil.isNotEmpty(etrFundDto.getVdate()) && StringUtil.isNotEmpty(etrFundDto.getMdate())){
                if(etrFundDto.getVdate().compareTo(etrFundDto.getMdate()) == 1){
                    errorList.add(new ValidMessage("vdate", getMessage("ETR_X_005")));
                }
            }
//            if(!StringUtil.isNotEmpty(etrFundDto.getBussEtrInvestPersonInfoDto().getCounterId())){
//                errorList.add(new ValidMessage("bussEtrInvestPersonInfoDto.counterId", "请输入交易对手"));
//            }
            /*if(!StringUtil.isNotEmpty(etrFundDto.getCcy())){
                errorList.add(new ValidMessage("ccy", "请输入币种"));
            }*/

            if(etrFundDto.getPs().equals(DictCodeList.交易方向_申购.getValue())){
                if(etrFundDto.getAssetType1().equals(DictCodeList.基金类资产类型_非货币基金.getValue())
                    && StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_赎回.getValue())){

                if(etrFundDto.getAssetType1().equals(DictCodeList.基金类资产类型_非货币基金.getValue())
                    && StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustNum())){
                    errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_012", getMessage("EtrFund001.entrustNum"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustNum()) && StringUtil.isNotEmpty(etrFundDto.getAssetAvailablePosition())){
                    if(etrFundDto.getEntrustNum().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_008",getMessage("EtrFund001.entrustNum"))));
                    }
                    else if(StringUtil.isNotEmpty(etrFundDto.getFinprodId())
                    		/*&& !etrFundDto.getAssetType1().equals(DictCodeList.基金类资产类型_货币基金.getValue())*/){
                        int result = etrFundDto.getEntrustNum().compareTo(etrFundDto.getAssetAvailablePosition());
                        if(result==1&&StringUtil.equals(
                            CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                            DictCodeListExt1.是或否_否.getValue())){
                            errorList.add(new ValidMessage("","委托份额必须小于等于资产可用持仓"));
                        }
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_买入.getValue())){
            	if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
            		errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            	}
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustNum())){
                    errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_012", getMessage("EtrFund001.entrustNum"))));
                }
                //委托价格，当交易方向为买入/卖出，且交易场所和交易对手都是交易所时显示，且必填
                if(!StringUtil.isNotEmpty(etrFundDto.getPrice())){
                	if(!StringUtil.equals(etrFundDto.getCounterType(), DictCodeList.是否_否.getValue())){
                		errorList.add(new ValidMessage("price", getMessage("ETR_X_012", getMessage("EtrFund002.price"))));
                	}
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt()) && StringUtil.isNotEmpty(etrFundDto.getProAvailableCash())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeListExt2.交易方向_认购.getValue())){
            	//1)	交易方向选择认购时，资产代码必填；
            	//2)	认购时只可委托金额；
            	
            	//4)	认购时交易对手默认为管理人；

            	if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
            		errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            	}
            	if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())){
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else{
            	if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
            		errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            	}
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustNum())){
                    errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_012", getMessage("EtrFund001.entrustNum"))));
                }
              //委托价格，当交易方向为买入/卖出，且交易场所和交易对手都是交易所时显示，且必填
                if(!StringUtil.isNotEmpty(etrFundDto.getPrice())){
                	if(!StringUtil.equals(etrFundDto.getCounterType(), DictCodeList.是否_否.getValue())&&!etrFundDto.getPs().equals(DictCodeListExt2.交易方向_转换申请.getValue())){
                		errorList.add(new ValidMessage("price", getMessage("ETR_X_012", getMessage("EtrFund002.price"))));
                	}
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustNum()) && StringUtil.isNotEmpty(etrFundDto.getAssetAvailablePosition())){
                    if(etrFundDto.getEntrustNum().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_008",getMessage("EtrFund001.entrustNum"))));
                    }
                    else {
                        int result = etrFundDto.getEntrustNum().compareTo(etrFundDto.getAssetAvailablePosition());
                        if(result==1&&StringUtil.equals(
                            CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                            DictCodeListExt1.是或否_否.getValue())){
                            errorList.add(new ValidMessage("entrustNum","委托份额必须小于等于资产可用持仓"));
                        }
                    }
                }
            }

            //TISHKBS-688 【UAT】投资交易-公募基金指令，提交校验问题 add by lizh 20200711 start
            if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            }
            //TISHKBS-688 【UAT】投资交易-公募基金指令，提交校验问题 add by lizh 20200711 end
        }
        if(StringUtil.equals(etrFundDto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
            BigDecimal entrustAmt = etrFundDto.getEntrustAmt();
            String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
            if(StringUtil.equals(etrlimit_switch, "01")){
                if(entrustAmt!=null){
                    BigDecimal limitAmt = etrFundService.selectLimitAmt(etrFundDto);
                    if(limitAmt != null && entrustAmt.compareTo(limitAmt)==1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_009",getMessage("EtrFund001.entrustAmt"))+limitAmt));
                    }
                }
            }
            if(StringUtil.isNotEmpty(etrFundDto.getVdate()) && StringUtil.isNotEmpty(etrFundDto.getMdate())){
                if(etrFundDto.getVdate().compareTo(etrFundDto.getMdate()) == 1){
                    errorList.add(new ValidMessage("vdate", getMessage("ETR_X_005")));
                }
            }
//            if(!StringUtil.isNotEmpty(etrFundDto.getBussEtrInvestPersonInfoDto().getCounterId())){
//                errorList.add(new ValidMessage("bussEtrInvestPersonInfoDto.counterId", "请输入交易对手"));
//            }
            if(!StringUtil.isNotEmpty(etrFundDto.getCcy())){
                errorList.add(new ValidMessage("ccy", getMessage("ETR_X_012", getMessage("EtrFund001.ccy"))));
            }
          //证券托管户交易场所为交易所时必填 需求变更为： 通道和证券账户二选一必填
        	if((StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), etrFundDto.getTradeMarket())||StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), etrFundDto.getTradeMarket()))
        			&&StringUtil.isEmpty(etrFundDto.getSecAcctId())&&StringUtil.isEmpty(etrFundDto.getChlId())){
        		errorList.add(new ValidMessage("secAcctId","交易场所为交易所时，合作资产和证券托管户二选一必填"));
        	}
            
          //3)	提交时校验，委托日期区间如在标的的认购起始日和认购终止日之间，交易方向必须为认购，如不在，交易方向不可为认购；
        	if(StringUtil.isNotEmpty(etrFundDto.getFinprodId())&&StringUtil.isNotEmpty(etrFundDto.getPs())){
        		FinProductAdd finProductAdd = etrFundService.selectFinProductAdd(etrFundDto.getFinprodId());
        		if(StringUtil.isNotEmpty(finProductAdd)){
        			if((etrFundDto != null && etrFundDto.getMdate() != null && (DateUtil.compareDate(finProductAdd.getToffEnddate(), etrFundDto.getMdate())>=0))
        					&&(etrFundDto != null && etrFundDto.getVdate() != null && (DateUtil.compareDate(finProductAdd.getToffStrdate(), etrFundDto.getVdate())<=0))){
                    	//委托日期区间如在标的的认购起始日和认购终止日之间
        				if(!StringUtil.equals(etrFundDto.getPs(), DictCodeListExt2.交易方向_认购.getValue())){
        					errorList.add(new ValidMessage("ps","委托日期区间在标的的认购起始日和认购终止日之间，交易方向必须为认购!"));
        				}
                    }else{
                    	if(StringUtil.equals(etrFundDto.getPs(), DictCodeListExt2.交易方向_认购.getValue())){
        					errorList.add(new ValidMessage("ps","委托日期区间不在标的的认购起始日和认购终止日之间，交易方向不可为认购!"));
        				}
                    }
        			
        		}
        	}
            
            if(etrFundDto.getPs().equals(DictCodeList.交易方向_申购.getValue())){
                
                if(etrFundDto.getAssetType1().equals(DictCodeList.基金类资产类型_非货币基金.getValue())
                    && StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_赎回.getValue())){
                if(etrFundDto.getAssetType1().equals(DictCodeList.基金类资产类型_非货币基金.getValue())
                    && StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustNum())){
                    errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_012", getMessage("EtrFund001.entrustNum"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustNum()) && StringUtil.isNotEmpty(etrFundDto.getAssetAvailablePosition())){
                    if(etrFundDto.getEntrustNum().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_008",getMessage("EtrFund001.entrustNum"))));
                    }
                    else if(StringUtil.isNotEmpty(etrFundDto.getFinprodId())){
                        int result = etrFundDto.getEntrustNum().compareTo(etrFundDto.getAssetAvailablePosition());
                        if(result==1&&StringUtil.equals(
                            CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                            DictCodeListExt1.是或否_否.getValue())){
                            errorList.add(new ValidMessage("entrustNum","委托份额必须小于等于资产可用持仓")); 
                        }
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_买入.getValue())){
            	if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
            		errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            	}
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
              //委托价格，当交易方向为买入/卖出，且交易场所和交易对手都是交易所时显示，且必填
                if(!StringUtil.isNotEmpty(etrFundDto.getPrice())){
                	if(!StringUtil.equals(etrFundDto.getCounterType(), DictCodeList.是否_否.getValue())){
                		errorList.add(new ValidMessage("price", getMessage("ETR_X_012", getMessage("EtrFund002.price"))));
                	}
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt()) && StringUtil.isNotEmpty(etrFundDto.getProAvailableCash())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                    /*else{
                        int result = etrFundDto.getEntrustNum().compareTo(etrFundDto.getAssetAvailablePosition());
                        if(result==1){
                            errorList.add(new ValidMessage("entrustNum","委托份额必须小于等于资产可用持仓")); 
                        }
                    }*/
                }
            }else if(etrFundDto.getPs().equals(DictCodeListExt2.交易方向_认购.getValue())){
                //1)	交易方向选择认购时，资产代码必填；
                //2)	认购时只可委托金额；

                //4)	认购时交易对手默认为管理人；

                if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
                    errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
                }
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else if(etrFundDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())){
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_012", getMessage("EtrFund001.entrustAmt"))));
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
                    if(etrFundDto.getEntrustAmt().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustAmt", getMessage("ETR_X_008",getMessage("EtrFund001.entrustAmt"))));
                    }
                }
            }
            else{
            	if(StringUtil.isEmpty(etrFundDto.getFinprodMarketId())){
            		errorList.add(new ValidMessage("finprodMarketId", getMessage("ETR_X_012", getMessage("EtrFund001.finprodId"))));
            	}
                if(!StringUtil.isNotEmpty(etrFundDto.getEntrustNum()) && !etrFundDto.getPs().equals(DictCodeList.交易方向_认购.getValue())){
                    errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_012", getMessage("EtrFund001.entrustNum"))));
                }
              //委托价格，当交易方向为买入/卖出，且交易场所和交易对手都是交易所时显示，且必填
                if(!StringUtil.isNotEmpty(etrFundDto.getPrice()) &&!etrFundDto.getPs().equals(DictCodeListExt2.交易方向_转换申请.getValue())){
                	if(!StringUtil.equals(etrFundDto.getCounterType(), DictCodeList.是否_否.getValue())){
                		errorList.add(new ValidMessage("price", getMessage("ETR_X_012", getMessage("EtrFund002.price"))));
                	}
                }
                if(StringUtil.isNotEmpty(etrFundDto.getEntrustNum()) && StringUtil.isNotEmpty(etrFundDto.getAssetAvailablePosition())){
                    if(etrFundDto.getEntrustNum().compareTo(new BigDecimal(0)) != 1){
                        errorList.add(new ValidMessage("entrustNum", getMessage("ETR_X_008",getMessage("EtrFund001.entrustNum"))));
                    }
                    else{
                        int result = etrFundDto.getEntrustNum().compareTo(etrFundDto.getAssetAvailablePosition());
                        if(result==1&&StringUtil.equals(
                            CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                            DictCodeListExt1.是或否_否.getValue())){
                            errorList.add(new ValidMessage("entrustNum","委托份额必须小于等于资产可用持仓")); 
                        }
                    }
                }
            }
        }
        if(StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"),"01")&&  (StringUtil.equals(etrFundDto.getOperateType(), "save") ||  StringUtil.equals(etrFundDto.getOperateType(), "submit"))) {
        	if(StringUtil.isEmpty(etrFundDto.getAssetType11())) {
        		resultList.add(new ValidMessage("type10",
                        getMessage("COMMON_E_0057", getMessage("EtrInvestBondInfo001.type10"))));
        	}
        	 
        }
        // 增加可用现金的硬校验并增加开关 by 赵东东 20200121 start
        if(StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())&&StringUtil.isNotEmpty(etrFundDto.getProAvailableCash())&&
            (StringUtil.equals(etrFundDto.getPs(),DictCodeList.交易方向_买入.getValue())
//                ||StringUtil.equals(etrFundDto.getPs(),DictCodeList.交易方向_申购.getValue())
//                ||StringUtil.equals(etrFundDto.getPs(),DictCodeListExt2.交易方向_认购.getValue())
            )&&StringUtil.equals(
                    CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                    DictCodeListExt1.是或否_否.getValue())) {
            if(etrFundDto.getEntrustAmt().compareTo(etrFundDto.getProAvailableCash())>0) {
                errorList.add(new ValidMessage("entrustAmt","组合可用现金不足")); 
            }
            
        }
        /*if(etrFundDto.getPs().equals(DictCodeList.交易方向_赎回.getValue()) && StringUtil.isNotEmpty(etrFundDto.getEntrustAmt())){
            errorList.add(new ValidMessage("entrustAmt", "委托份额必须大于0"));
        }*/
        /*如果是五级类型，才判断四级分类是会否为空，若是二级库，则不需要判断*/
        if ((CacheUtil.getSysParam("IS_SHOW_TYPE") == null || 
                "01".equals(CacheUtil.getSysParam("IS_SHOW_TYPE")))
        && !StringUtil.equals(etrFundDto.getOperateType(), "close")    //关闭时候不要判断
        && StringUtil.isEmpty(etrFundDto.getAssetType11())){
            errorList.add(new ValidMessage("assetType11", getMessage("ETR_X_012", getMessage("EtrInvestBondInfo001.type11"))));
        }
        /*TISHKS-92 计划类货币型指令、计划类净值型指令与场外基金指令，交易方向选择买入时，提示框缺失 start*/
        if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), etrFundDto.getPs()) && StringUtil.isNotEmpty(etrFundDto.getBussEtrInvestPersonInfoDto())
                && StringUtil.isNotEmpty(etrFundDto.getBussEtrInvestPersonInfoDto().getCounterId())) {
            PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(etrFundDto.getBussEtrInvestPersonInfoDto().getCounterId());
            if (mcp != null) {
                errorList.add(new ValidMessage("bussEtrInvestPersonInfoDto.counterId", this.getMessage("TRD_COUNTER_TYPE_001")));
            }
        }
        /*TISHKS-92 计划类货币型指令、计划类净值型指令与场外基金指令，交易方向选择买入时，提示框缺失 end*/
        return errorList;
    }

}
