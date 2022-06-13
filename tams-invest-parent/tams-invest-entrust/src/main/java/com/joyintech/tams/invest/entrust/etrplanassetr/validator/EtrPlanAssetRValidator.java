/*
 * 文件名：EtrBrowDepoInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年9月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrplanassetr.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrplanassetr.service.EtrPlanAssetRService;

/**
 * 计划类利率委托校验 〈功能详细描述〉
 *
 * @author zhangjun
 * @version 1.0 2018年9月25日
 * @see EtrPlanAssetRValidator
 * @since 1.0
 */
@Component
public class EtrPlanAssetRValidator implements EtrCommonValidator {

    @Autowired
    private EtrPlanAssetRService etrPlanAssetService;
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrPlanAssetRDto dto = (EtrPlanAssetRDto)value;
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<EtrPlanAssetRDto> listDto = dto.getSelectedItem();
            for (EtrPlanAssetRDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_删除.getValue());
                errorList.addAll(etrPlanAssetService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "close")) {
            List<EtrPlanAssetRDto> listDto = dto.getSelectedItem();
            for (EtrPlanAssetRDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_关闭.getValue());
                errorList.addAll(etrPlanAssetService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "recall")) {
            List<EtrPlanAssetRDto> listDto = dto.getSelectedItem();
            for (EtrPlanAssetRDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_撤回.getValue());
                errorList.addAll(etrPlanAssetService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "edit")) {
            if (!(DictCodeList.委托状态_已撤回.getValue().equals(dto.getEntrustStatus())
                  || DictCodeList.委托状态_已退回.getValue().equals(dto.getEntrustStatus()))) {
                errorList.add(new ValidMessage(
                    // “新建”、“已撤回”、“已退回”
                    this.getMessage("ETR_E_0009", this.getMessage("ETR_LEND_001"))));
            }
        }
        else {
        	//----modify by 裴家俊  2019/03/20----------
           /* String riskmathswitch = CacheUtil.getSysParam(BussinessConstant.RISK_MATH_SWITCH);
            if(StringUtil.equals(riskmathswitch, "01")){
                if(dto.getRiskTrialRetDto() == null || dto.getRiskTrialRetDto().size() == 0){
                    errorList.add(new ValidMessage(this.getMessage("ETR_X_013")));
                }
            }*/
            BigDecimal entrustAmt = dto.getEntrustAmt();
            BigDecimal entrustNum = dto.getEntrustNum();
            BigDecimal proAvailableCash = dto.getProAvailableCash();
            BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
            BigDecimal zero = new BigDecimal(0);
            if (entrustAmt == null && entrustNum == null) {
                errorList.add(new ValidMessage(this.getMessage("ETR_PLANR_001")));
            }
            if (entrustAmt != null) {
                if (entrustAmt.compareTo(zero) <= 0) {
                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_008",this.getMessage("EtrPlanAssetR001.entrustAmt"))));
                }
                // delete by renping 改为共通处理 2019-08-22 start
//                String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
//                if(StringUtil.equals(etrlimit_switch, "01")){
//                    BigDecimal limitAmt = etrPlanAssetService.selectLimitAmt(dto);
//                    if(limitAmt != null && entrustAmt.compareTo(limitAmt)==1){
//                        errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_009",this.getMessage("EtrPlanAssetR001.entrustAmt"))+limitAmt));
//                    }
//                }
                // delete by renping 改为共通处理 2019-08-22 end
                FinProduct finProduct = etrPlanAssetService.getFinProductInfo(dto.getFinprodId());
                if (finProduct != null && finProduct.getIssueAmt() != null
                		&& entrustAmt.compareTo(finProduct.getIssueAmt()) > 0) {
                	errorList.add(new ValidMessage("委托金额超过了计划的融资金额！"));
                }
            }
//            if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
//                if(!StringUtil.isNotEmpty(dto.getBusinessModel())){
//                    errorList.add(new ValidMessage("businessModel", "请输入业务模式"));
//                }
//            }else{
//                if(!StringUtil.isNotEmpty(dto.getInvAim())){
//                    errorList.add(new ValidMessage("businessModel", "请输入投资目的"));
//                }
//            }

            if (entrustNum != null) {
                if (entrustNum.compareTo(zero) <= 0) {
                    errorList.add(new ValidMessage("entrustNum", this.getMessage("ETR_X_008",this.getMessage("EtrPlanAssetR002.entrustNum"))));
                }
                // delete by renping 改为共通处理 2019-08-22 start
//                String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
//                if(StringUtil.equals(etrlimit_switch, "01")){
//                    BigDecimal limitAmt = etrPlanAssetService.selectLimitAmt(dto);
//                    if(limitAmt != null && entrustNum.compareTo(limitAmt)==1){
//                        errorList.add(new ValidMessage("entrustNum", this.getMessage("ETR_X_009",this.getMessage("EtrPlanAssetR002.entrustNum"))+limitAmt));
//                    }
//                }
                // delete by renping 改为共通处理 2019-08-22 end
            }
            Date vdate = dto.getVdate();
            Date mdate = dto.getMdate();
            Date finmdate = dto.getFinmdate();
            Date finvdate = dto.getFinvdate();
            if (vdate != null && mdate != null) {
                if (vdate.after(mdate)) {
                    errorList.add(new ValidMessage("vdate", this.getMessage("ETR_X_005")));
                }
                if(finvdate != null && vdate.before(finvdate)){
                    errorList.add(new ValidMessage("vdate",this.getMessage("ETR_X_006")+DateUtil.format(finvdate)));
                }
                if(finmdate != null && mdate.after(finmdate)){
                    errorList.add(new ValidMessage("mdate",this.getMessage("ETR_X_007")+DateUtil.format(finmdate)));
                }
            }
            /*if (!StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getCounterId())) {
                errorList.add(new ValidMessage("etrInvestPersonInfo.counterId","请输入交易对手。"));
            }*/
//            if (entrustAmt != null && proAvailableCash != null
//                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
//                if (entrustAmt.compareTo(proAvailableCash) == 1) {
//                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_010",this.getMessage("EtrPlanAssetR002.entrustAmt"))));
//                }
//            }
            if (entrustNum != null && assetAvailablePosition != null
             // 一体化对应etliu start
//                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
             // 一体化对应etliu end
                /*if (entrustNum.compareTo(assetAvailablePosition) == 1) {
                    errorList.add(new ValidMessage("entrustNum", this.getMessage("ETR_X_011",this.getMessage("EtrPlanAssetR002.entrustNum"))));
                }*/
            }
            String ps = dto.getPs();
            if (entrustNum != null && StringUtil.isNotEmpty(ps)) {
                if (StringUtil.equals(
                        CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                        DictCodeListExt1.是或否_否.getValue())
                        && StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())
                        && assetAvailablePosition != null) {
                    if (entrustNum.compareTo(assetAvailablePosition) > 0){
                        errorList.add(
                                new ValidMessage("entrustNum", this.getMessage("ETR_X_019")));
                    }
                } else if (StringUtil.equals(
                        CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                        DictCodeListExt1.是或否_否.getValue())
                        && StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())
                        && proAvailableCash != null) {
                    if (dto.getEntrustNum().compareTo(proAvailableCash) == 1) {
                        errorList.add(
                                new ValidMessage("entrustAmt", "委托金额不能大于可用现金。"));
                    }
                }
            }
            // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y:需要测试
            if(DictCodeListExt1.是否_是.getValue().equals(CacheUtil.getSysParam("IS_NEED_SPPI_CHECK"))){
                //投资目的
                if(StringUtil.isNotEmpty(dto.getInvAim())) {
                    if(StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue())
                            || StringUtil.equals(dto.getInvAim(), DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue())) {
                        FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                        if(StringUtil.isNotEmpty(finProduct)) {
                            if(StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试不通过.getValue())) {
                                errorList.add(new ValidMessage("invAim",
                                        getMessage("ETR_ERROR_0059")));
                            }
                            else if(StringUtil.isEmpty(finProduct.getSppiTestStatus())
                                ||StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_未测试.getValue())
                                    || StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_测试中.getValue())
                                    //|| StringUtil.equals(finProduct.getSppiTestStatus(), DictCodeListExt3.SPPI测试状态_已退回.getValue())
                            ) {
                                errorList.add(new ValidMessage("invAim",
                                        getMessage("选择此投资目的时，对应的资产必须先进行SPPI测试！")));
                            }
                        }
                    }
                }
            }
            errorList.addAll(etrPlanAssetService.check(dto));
        }
        return errorList;
    }

}
