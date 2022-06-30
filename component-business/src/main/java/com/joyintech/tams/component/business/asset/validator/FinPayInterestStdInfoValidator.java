/*
 * 文件名：FinPayInterestStdInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.asset.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.*;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 付息信息--业务验证
 * @author zhangnian
 * @version 1.0 2018年9月28日
 * @see FinPayInterestStdInfoValidator
 * @since 1.0
 */
@Component
public class FinPayInterestStdInfoValidator implements BeanValidator{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        FinPayInterestStdInfoDto dto = (FinPayInterestStdInfoDto)value;
        List<ValidMessage> resultList = new ArrayList<>();
        if((StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
            &&StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_附息.getValue()))
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
            ){
                if(StringUtil.isEmpty(dto.getPayTdayRule())){
                    resultList.add(new ValidMessage("payTdayRule",
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payTdayRule"))));
                }
                if(StringUtil.isEmpty(dto.getPayCycle())){
                    resultList.add(new ValidMessage("payCycle",
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payCycle"))));
                }
                if(!StringUtil.equals(dto.getPayCycle(), DictCodeList.频率_不规则.getValue())){
                    if(StringUtil.isEmpty(dto.getCalcRule())){
                        resultList.add(new ValidMessage("calcRule",
                            getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.calcRule"))));
                    }
                }
            }else if(StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())){
                if(StringUtil.isEmpty(dto.getPayTdayRule())){
                    resultList.add(new ValidMessage("payTdayRule",
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payTdayRule"))));
                }
            }
        //项目类资产--计息营业日准则必填
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
            if(StringUtil.isEmpty(dto.getEndTdayRule())){
                resultList.add(new ValidMessage("endTdayRule",
                    getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.endTdayRule"))));
            }
        }
        //当付息频率改为自定义时，画面.自定义说明改为必填。否则，画面.自定义说明为非必填
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
            ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
         // 一体化对应etliu start
//            if(StringUtil.equals(dto.getPayCycle(), DictCodeList.支付频率_付息频率_不规则.getValue())){
            if(StringUtil.equals(dto.getPayCycle(), DictCodeList.频率_不规则.getValue())){
         // 一体化对应etliu end
                if(StringUtil.isEmpty(dto.getRemark())){
                    resultList.add(new ValidMessage(
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.remark"))));
                }
            }
        }
     // 一体化对应etliu
//        if(StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方法_首次付息日.getValue())){
        if(StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方式_首次支付日.getValue())){
            if(null == dto.getFirstPayDate()){
            	// 一体化对应etliu
//                resultList.add(new ValidMessage("firstPayDate",
//                    getMessage("BUSS_COM_E_0008",getMessage("FinPayInterestStdInfo.calcRule"),DictCodeList.推算方法_首次付息日.getCodeName(), getMessage("FinPayInterestStdInfo.firstPayDate"))));
            	resultList.add(new ValidMessage("firstPayDate",
                        getMessage("BUSS_COM_E_0008",getMessage("FinPayInterestStdInfo.calcRule"),DictCodeList.推算方式_首次支付日.getCodeName(), getMessage("FinPayInterestStdInfo.firstPayDate"))));
            }
        }
        //活期存放
        if(StringUtil.equals(dto.getType6(), DictCodeListExt3.存款类资产类型_活期存放.getValue())){
            if(StringUtil.equals(dto.getPayCycle(), DictCodeList.频率_年.getValue())
            ||StringUtil.equals(dto.getPayCycle(), DictCodeList.频率_季.getValue())){
                if(StringUtil.isEmpty(dto.getPayMonth())){
                    resultList.add(new ValidMessage(
                            getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payMonth"))));
                }
            }
            if(StringUtil.isEmpty(dto.getDayValue())){
                resultList.add(new ValidMessage(
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.dayValue"))));
            }
        }
        //支付计划
        //保存
        if(StringUtil.equals(dto.getOperateType(),"save")){
            if(null == dto.getFinCashPlanList()){
                resultList.add(new ValidMessage(getMessage("FIN_ASSET_E_0008")));
            }else{
                dto.getFinCashPlanList().forEach(entity ->{
                    if(null==entity.getVdateUnadjust()||null==entity.getMdateUnadjust()
                        ||null==entity.getPayDate()||null == entity.getIntAmt()){
                        resultList.add(new ValidMessage("",getMessage("COMMON_E_0057"),getMessage("FinPayInterestStdInfo.vdateUnadjust")
                            +"、"+getMessage("FinPayInterestStdInfo.mdateUnadjust")+"、"+getMessage("FinPayInterestStdInfo.payDate")+"、"+getMessage("FinPayInterestStdInfo.intAmt")));
                    }
                });
            }
        }
        return resultList;
    }

}
