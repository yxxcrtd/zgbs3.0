/*
 * 文件名：BussFinProductSaleParamValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.prod.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.prod.dto.BussFinProductSaleParamDto;
import com.joyintech.tams.constant.DictCodeList;

/**
 * 销售参数业务组件Validator
 * 
 * @author zhangliang
 * @version 1.0 2018年10月17日
 * @see BussFinProductSaleParamValidator
 * @since 1.0
 */
@Component
public class BussFinProductSaleParamValidator implements BeanValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        BussFinProductSaleParamDto bussFinProductSaleParamDto = (BussFinProductSaleParamDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if (StringUtil.equals(bussFinProductSaleParamDto.getPageId(), "FinProductBuild002") || 
        		StringUtil.equals(bussFinProductSaleParamDto.getPageId(), "FinNetValueProductBuild002")) {
        	//delete by wm 界面无该输入项 start 2019-05-10
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getPrdBand())) {
//                resultList.add(new ValidMessage("prdBand", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.prdBand"))));
//            }
//            
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getCyyType())) {
//                resultList.add(new ValidMessage("cyyType", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.cyyType"))));
//            }
            //delete by wm 界面无该输入项 end 2019-05-10
        	//delete by wm 移动 到外面check start 2019-05-10
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleChannel())) {
//                resultList.add(new ValidMessage("saleChannel", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.saleChannel"))));
//            }
          //delete by wm 移动 到外面check end 2019-05-10
            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleArea())) {
                resultList.add(new ValidMessage("saleArea", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("BussFinProductSaleParam.saleArea"))));
            }
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleMax())) {
//                resultList.add(new ValidMessage("saleMax", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.saleMax"))));
//            }
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleMin())) {
//                resultList.add(new ValidMessage("saleMin", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.saleMin"))));
//            }
            if (StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleMax())
                && StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleMin())) {
                if (bussFinProductSaleParamDto.getSaleMin().compareTo(
                    bussFinProductSaleParamDto.getSaleMax()) > 0) {
                    resultList.add(new ValidMessage("saleMin",
                        this.getMessage("FIN_PROD_E_0017",
                            this.getMessage("BussFinProductSaleParam.saleMin"),
                            this.getMessage("BussFinProductSaleParam.saleMax"))));
                }
            }
            //20200319 注释 同业机构报错框描红问题，判断移至外层 FinNetValueProductBuild002Validator
            /*if (StringUtil.isNotEmpty(bussFinProductSaleParamDto.getInvestorType()) &&
                    bussFinProductSaleParamDto.getInvestorType().indexOf( DictCodeList.目标客户类型_金融同业专属.getValue())!=-1
            		&& !StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSameOrg())) {
                resultList.add(new ValidMessage("sameOrg", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("BussFinProductSaleParam.sameOrg"))));
            }*/
            //20200319 taoran 注释  
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getToffStart())) {
//                resultList.add(new ValidMessage("toffStart", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.toffStart"))));
//            }
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getLowestAmt())) {
//                resultList.add(new ValidMessage("lowestAmt", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.lowestAmt"))));
//            }
            //delete by wm 界面无该输入项 start 2019-05-10
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getHugeRedRatio())) {
//                resultList.add(new ValidMessage("hugeRedRatio", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.hugeRedRatio"))));
//            }
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getMinPaperQty())) {
//                resultList.add(new ValidMessage("minPaperQty", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.minPaperQty"))));
//            }
//            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getMinRedmQty())) {
//                resultList.add(new ValidMessage("minRedmQty", this.getMessage("BUSS_COM_E_0038",
//                    this.getMessage("BussFinProductSaleParam.minRedmQty"))));
//            }
          //delete by wm 界面无该输入项 end 2019-05-10
            if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getCanPlge())) {
                resultList.add(new ValidMessage("canPlge", this.getMessage("BUSS_COM_E_0038",
                    this.getMessage("BussFinProductSaleParam.canPlge"))));
            }
            else {
                if (StringUtil.equals(bussFinProductSaleParamDto.getCanPlge(),
                    DictCodeList.是否_是.getValue())) {
                    if (!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getMaxPlgeRate())) {
                        resultList.add(new ValidMessage("maxPlgeRate",
                            this.getMessage("BUSS_COM_E_0008",
                                this.getMessage("BussFinProductSaleParam.canPlge"),
                                DictCodeList.是否_是.getCodeName(),
                                this.getMessage("BussFinProductSaleParam.maxPlgeRate"))));
                    }
                }
            }
            //计划募集金额
            if(null == bussFinProductSaleParamDto.getRaiseAmtPlan() || BigDecimal.ZERO.compareTo(bussFinProductSaleParamDto.getRaiseAmtPlan()) >= 0){
                if(!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getRaiseAmtPlan())) {
                    resultList.add(new ValidMessage("raiseAmtPlan", getMessage("FIN_PROD_E_0091",
                            getMessage("BussFinProductSaleParam.repRaiseAmtPlan"))));
                } 
            }

            //单一投资者持有上限
            if(null == bussFinProductSaleParamDto.getSingleInvestorholdingMax()){
                resultList.add(new ValidMessage("singleInvestorholdingMax", "请输入单一投资者持有上限"));
            }else{
                if(bussFinProductSaleParamDto.getSingleInvestorholdingMax().compareTo(BigDecimal.ZERO)<0
                        ||bussFinProductSaleParamDto.getSingleInvestorholdingMax().compareTo(BigDecimal.valueOf(100))>0){
                    resultList.add(new ValidMessage("singleInvestorholdingMax", "请输入0-100间数值"));
                }
            }
//            //净值型和货币型的限制
//           if(StringUtil.equals(bussFinProductSaleParamDto.getPageId(), "FinNetValueProductBuild002")){
//                //销售渠道
//                if(!StringUtil.isNotEmpty(bussFinProductSaleParamDto.getSaleChannel())) {
//                    resultList.add(new ValidMessage("saleChannel", getMessage("BUSS_COM_E_0038",
//                            getMessage("BussFinProductSaleParam.saleChannel"))));
//                }
//                
//            }
        } 

        return resultList;
    }

}
