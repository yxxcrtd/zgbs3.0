/*
 * 文件名：EtrProject001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrproject.validator;

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
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrproject.service.EtrProjectService;

/**
 * 项目类委托校验类 〈功能详细描述〉
 *
 * @author xiexinting
 * @version 1.0 2018年10月11日
 * @see EtrProjectValidator
 * @since 1.0
 */
@Component
public class EtrProjectValidator implements EtrCommonValidator {

    @Autowired
    private EtrProjectService etrProjectService;
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {
        List<ValidMessage> errorList = new ArrayList<>();
        EtrProjectDto dto = (EtrProjectDto)value;
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<EtrProjectDto> listDto = dto.getSelectedItem();
            for (EtrProjectDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_删除.getValue());
                errorList.addAll(etrProjectService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "close")) {
            List<EtrProjectDto> listDto = dto.getSelectedItem();
            for (EtrProjectDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_关闭.getValue());
                errorList.addAll(etrProjectService.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "recall")) {
            List<EtrProjectDto> listDto = dto.getSelectedItem();
            for (EtrProjectDto entity : listDto) {
                entity.setOperateType(DictCodeListExt1.委托操作类型_撤回.getValue());
                errorList.addAll(etrProjectService.check(entity));
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
            BigDecimal entrustAmt = dto.getEntrustAmt();
            BigDecimal proAvailableCash = dto.getProAvailableCash();
            BigDecimal assetAvailablePosition = dto.getAssetAvailablePosition();
            BigDecimal zero = new BigDecimal(0);
            BigDecimal issueAmt = etrProjectService.searchIssueAmt(dto.getFinprodId());
            if(entrustAmt != null){
                if(entrustAmt.compareTo(zero) != 1){
                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_008",this.getMessage("EtrProject001.entrustAmt"))));
                }
//                if(proAvailableCash != null && entrustAmt.compareTo(proAvailableCash) == 1){
//                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_010",this.getMessage("EtrProject001.entrustAmt"))));
//                }
                /*可用现金与持仓Check  add by zhuyongkang 2019/1/20 start*/
                // 可用现金校验
                if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                        DictCodeListExt1.是或否_否.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                        && dto.getProAvailableCash() != null && dto.getEntrustAmt() != null
                        && dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_018",this.getMessage("EtrProject001.entrustAmt"))));
                }
                // 可用持仓校验
                else if (StringUtil.equals(
                        CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                        DictCodeListExt1.是或否_否.getValue())
                        && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                        && dto.getAssetAvailablePosition() != null && dto.getEntrustAmt() != null
                        && dto.getEntrustAmt().compareTo(dto.getAssetAvailablePosition()) > 0) {
                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_011",this.getMessage("EtrProject001.entrustAmt"))));
                }
                // 委托金额不能大于融资金额
                else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()) &&
                		issueAmt != null && entrustAmt.compareTo(issueAmt)>0) {
                	errorList.add(new ValidMessage("entrustAmt", "委托金额不能大于融资金额"));
                }
              /*  if(assetAvailablePosition != null && entrustAmt.compareTo(assetAvailablePosition) == 1){
                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_011",this.getMessage("EtrProject001.entrustAmt"))));
                }*/
                /*可用现金与持仓Check  add by zhuyongkang 2019/1/20 end*/
                // delete by renping 改为共通处理 2019-08-22 start
//                String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
//                if(StringUtil.equals(etrlimit_switch, "01")){
//                    BigDecimal limitAmt = etrProjectService.selectLimitAmt(dto);
//                    if(limitAmt != null && entrustAmt.compareTo(limitAmt)==1){
//                        errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_X_009",this.getMessage("EtrProject001.entrustAmt"))+limitAmt));
//                    }
//                }
                // delete by renping 改为共通处理 2019-08-22 end
            }
            if (dto.getVdate()!=null && dto.getMdate()!=null) {
                if (dto.getMdate().before(dto.getVdate())) {
                    errorList.add(new ValidMessage("vdate", this.getMessage("ETR_X_005")));
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


            // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y需要测试
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
            errorList.addAll(etrProjectService.check(dto));
        }
        return errorList;
    }

}
