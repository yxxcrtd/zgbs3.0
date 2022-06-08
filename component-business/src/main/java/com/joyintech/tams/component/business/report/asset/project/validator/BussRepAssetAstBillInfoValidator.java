/*
 * 文件名：BussRepAssetAstBillValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年11月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.report.asset.project.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeListExt2;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepAssetAstBillInfoDto;
import com.joyintech.tams.constant.DictCodeList;

/**
 * 非标资产(票据或信用)报送信息
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年11月2日
 * @since 1.0
 */
@Component
public class BussRepAssetAstBillInfoValidator implements BeanValidator{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        BussRepAssetAstBillInfoDto dto = (BussRepAssetAstBillInfoDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_票据类.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信用证.getValue())){
                if(StringUtil.isNotEmpty(dto.getRepAssetType())){
                    if(StringUtil.isEmpty(dto.getRepAstBillType())){
                        resultList.add(new ValidMessage("repAstBillType",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repAstBillType"))));
                    }
                    if(StringUtil.isEmpty(dto.getRepTradeMarket())){
                        resultList.add(new ValidMessage("repTradeMarket",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repTradeMarket"))));
                    }
                    if(null==dto.getRepNum()){
                        resultList.add(new ValidMessage("repNum",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repNum"))));
                    }
                    if(null==dto.getRepRemainTerm()){
                        resultList.add(new ValidMessage("repRemainTerm",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repRemainTerm"))));
                    }
                    if(null==dto.getRepMaxRemainTerm()){
                        resultList.add(new ValidMessage("repMaxRemainTerm",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repMaxRemainTerm"))));
                    }
                    if(null==dto.getRepMinRemainTerm()){
                        resultList.add(new ValidMessage("repMinRemainTerm",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repMinRemainTerm"))));
                    }
                    if(null==dto.getRepDiscRate()){
                        resultList.add(new ValidMessage("repDiscRate",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repDiscRate"))));
                    }
                /*if(StringUtil.isEmpty(dto.getRepAstBillType())){
                    resultList.add(new ValidMessage("repAstBillType",
                        getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repAstBillType"))));
                }*/
                    if(StringUtil.equals(dto.getRepTradeMarket(), DictCodeList.交易流通场所_其他交易流通场所.getValue())){
                        if(StringUtil.isEmpty(dto.getRepRemark())){
                            resultList.add(new ValidMessage("repRemark",
                                    getMessage("COMMON_E_0057", getMessage("BussRepAssetAstBillInfo.repRemark"))));
                        }
                    }
                    // Sonar Fix modify by huwei 220608
                    if(null!=dto.getRepMaxRemainTerm()&&null!=dto.getRepMinRemainTerm()&&null!=dto.getRepRemainTerm()){
                        if(dto.getRepMinRemainTerm().compareTo(dto.getRepMaxRemainTerm()) > 0){
                            resultList.add(new ValidMessage("",
                                    getMessage("BUSS_COM_E_0003",
                                            getMessage("BussRepAssetAstBillInfo.repMinRemainTerm"),
                                            getMessage("BussRepAssetAstBillInfo.repMaxRemainTerm"))));
                        }
                        if(dto.getRepRemainTerm().compareTo(new BigDecimal(dto.getRepMaxRemainTerm())) > 0){
                            resultList.add(new ValidMessage("",
                                    getMessage("BUSS_COM_E_0003",
                                            getMessage("BussRepAssetAstBillInfo.repRemainTerm"),
                                            getMessage("BussRepAssetAstBillInfo.repMaxRemainTerm"))));
                        }
                        if(new BigDecimal(dto.getRepMinRemainTerm()).compareTo(dto.getRepRemainTerm()) > 0){
                            resultList.add(new ValidMessage("",
                                    getMessage("BUSS_COM_E_0003",
                                            getMessage("BussRepAssetAstBillInfo.repMinRemainTerm"),
                                            getMessage("BussRepAssetAstBillInfo.repRemainTerm"))));
                        }
                    }
                }
        }
        return resultList;
    }

}
