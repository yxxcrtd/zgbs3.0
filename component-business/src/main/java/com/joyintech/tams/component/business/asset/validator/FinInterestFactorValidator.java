/*
 * 文件名：FinInterestFactorValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.asset.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 计息要素--业务验证
 * @author zhangnian
 * @version 1.0 2018年9月25日
 * @see FinInterestFactorValidator
 * @since 1.0
 */
@Component
public class FinInterestFactorValidator implements BeanValidator{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        FinInterestFactorDto dto = (FinInterestFactorDto)value;
        List<ValidMessage> resultList = new ArrayList<>();
        //资产类型
        String finprodType = dto.getFinprodType2();
        //计息基础
        String basis =  dto.getBasis();
        //利息分配方式 
        String basisStyle = dto.getBasisStyle();
        //利率类型
        // 一体化对应etliu
//        String rateType = dto.getRateType();
        String rateType = dto.getIntType();
        String isCalLoadRate =  CacheUtil.getSysParam("IS_CAL_LOADRATE");
        //息票品种为贴现、零息时无需check
        if(!StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue())
            &&!StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())){
            if(StringUtil.isEmpty(basis)){
                resultList.add(new ValidMessage("basis",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.basis"))));
            }
            // 一体化对应etliu
//            if(StringUtil.isEmpty(dto.getRateType())){
            if(StringUtil.isEmpty(dto.getIntType())){
             // 一体化对应etliu start
//                resultList.add(new ValidMessage("rateType",
//                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.rateType"))));
                resultList.add(new ValidMessage("intType",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.rateType"))));
             // 一体化对应etliu end
            }
        }
        //债券
        if(StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            //债券类资产息票品种不可为空
            if(StringUtil.isEmpty(dto.getCouponSpecies())){
                resultList.add(new ValidMessage("couponSpecies",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.couponSpecies"))));
            }else{
                if(StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_附息.getValue())
                    ||StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())){
                    if(StringUtil.equals(basis, DictCodeList.计息基础_A_A.getValue())){
                        if(StringUtil.isEmpty(basisStyle)){
                            resultList.add(new ValidMessage("basisStyle",
                                getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.basis"),DictCodeList.计息基础_A_A.getValue(), getMessage("FinInterestFactor.basisStyle"))));
                        }
                    }
                }
            }
            if(null==dto.getRate()){
                resultList.add(new ValidMessage("rate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.rate"))));
            }
        }else if(StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
            //存款类
            if(null==dto.getLoadRate()){
                resultList.add(new ValidMessage("loadRate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.loadRate"))));
            }
        }else if(StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
            //计划类
            if(null==dto.getLoadRate()){
                resultList.add(new ValidMessage("loadRate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.loadRate"))));
            }
            if(null==dto.getInvestRate()){
                resultList.add(new ValidMessage("investRate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.investRate"))));
            }
        }else if(StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
            /*if(null==dto.getRate()){
                resultList.add(new ValidMessage("rate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.rate"))));
            }*/
            //项目类资产
            if(null==dto.getLoadRate()){
                resultList.add(new ValidMessage("loadRate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.loadRate"))));
            }
            if(null==dto.getInvestRate()){
                resultList.add(new ValidMessage("investRate",
                    getMessage("COMMON_E_0057", getMessage("FinInterestFactor.investRate"))));
            }
        }
      //类型类型为浮动利率
        if(StringUtil.equals(rateType, DictCodeList.利率类型_浮动利率.getValue())){
            //系数不可为空
            if(null== dto.getCoefficient()){
                resultList.add(new ValidMessage("coefficient",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.coefficient2"))));
            }
            //基准利率调整规则不可为空
            if(StringUtil.isEmpty(dto.getResetType())){
                resultList.add(new ValidMessage("resetType",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.resetType"))));
            }
            //利差不可为空
            if(null == dto.getSpreadRate()){
                resultList.add(new ValidMessage("spreadRate",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.spreadRate2"))));
            }
            //利率上限和利率下限均输入，利率下限>=利率上限时
            // 一体化对应etliu
            //去掉浮动利率的上下限必填限制 lishuping  20190918 start
//            if(null == dto.getRateMax()){
           /* if(null == dto.getHighestRate()){
                resultList.add(new ValidMessage("highestRate",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.rateMax"))));
            }*/
            // 一体化对应etliu
//            if(null == dto.getRateMin()){
            /*if(null == dto.getLowestRate()){
                resultList.add(new ValidMessage("lowestRate",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.rateMin"))));
            }*/
            //去掉浮动利率的上下限必填限制 lishuping  20190918 end
            // 一体化对应etliu
//            if(null!=dto.getRateMax()&&null!=dto.getRateMin()){
            if(null!=dto.getHighestRate()&&null!=dto.getLowestRate()){
            	// 一体化对应etliu
//                if(dto.getRateMax().compareTo(dto.getRateMin())!=1){
            	if(dto.getHighestRate().compareTo(dto.getLowestRate())!=1){
                    resultList.add(new ValidMessage(
                        getMessage("BUSS_COM_E_0035", getMessage("FinInterestFactor.rateMin"), getMessage("FinInterestFactor.rateMax"))));
                }
            }
            // 一体化对应etliu
//            if(StringUtil.isEmpty(dto.getIndexId())){
            if(StringUtil.isEmpty(dto.getBenchmarkId())){
                resultList.add(new ValidMessage("benchmarkId",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.rateType"),DictCodeList.利率类型_浮动利率.getCodeName(), getMessage("FinInterestFactor.indexId"))));
            }
        }
        if(StringUtil.equals(dto.getResetType(), DictCodeList.重置类型_基准利率调整规则_基准利率变动后固定间隔生效.getValue())){
            if(null == dto.getObserveBefDay()){
                resultList.add(new ValidMessage("observeBefDay",
                    getMessage("BUSS_COM_E_0008",
                        getMessage("FinInterestFactor.resetType"),
                        DictCodeList.重置类型_基准利率调整规则_基准利率变动后固定间隔生效.getCodeName(),
                        getMessage("FinInterestFactor.observeBefDay"))));
            }
            if(StringUtil.isEmpty((dto.getObserveBefUnit()))){
                resultList.add(new ValidMessage("observeBefUnit",
                    getMessage("BUSS_COM_E_0008", getMessage("FinInterestFactor.resetType"),DictCodeList.重置类型_基准利率调整规则_基准利率变动后固定间隔生效.getCodeName(), getMessage("FinInterestFactor.observeBefUnit"))));
            }
        }
        if((StringUtil.equals(isCalLoadRate,"02")&&StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_存款.getValue()))
        ||StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
        ||StringUtil.equals(finprodType, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
            if(StringUtil.isEmpty(dto.getCalRateType())){
                resultList.add(new ValidMessage("calRateType",
                        getMessage("COMMON_E_0057", getMessage("计息利率方式"))));
            }
        }
        return resultList;
    }

}
