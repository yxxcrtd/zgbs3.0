/*
 * 文件名：EtrBondFirstLBidDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrconbond.validator;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesDealCommon;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrconbond.service.EtrConBondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  可转债验证类
 * 〈功能详细描述〉
 * @author gejiaao
 * @version 1.0 2019年10月23日
 * @see EtrConBondDtoValidator
 * @since 1.0
 */
@Component
public class EtrConBondDtoValidator implements EtrCommonValidator {

    @Autowired
    private EtrConBondService service;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> bussValid(Object value) {

        List<ValidMessage> errorList = new ArrayList<>();
        EtrConBondDto dto = (EtrConBondDto)value;
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<EtrConBondDto> listDto = dto.getSelectedItem();
            for (EtrConBondDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_删除.getValue());
                errorList.addAll(service.check(entity));
            }
        } else if (StringUtil.equals(dto.getOperateType(), "close")) {
            List<EtrConBondDto> listDto = dto.getSelectedItem();
            for (EtrConBondDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_关闭.getValue());
                errorList.addAll(service.check(entity));
            }
        } else if (StringUtil.equals(dto.getOperateType(), "recall")) {
            List<EtrConBondDto> listDto = dto.getSelectedItem();
            for (EtrConBondDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_撤回.getValue());
                errorList.addAll(service.check(entity));
            }
        } else if (StringUtil.equals(dto.getOperateType(), "edit")) {
            dto.setOperateType(BussinessDictCodeList.委托操作类型_修改.getValue());
            errorList.addAll(service.check(dto));
        }else {
            errorList.addAll(service.check(dto));
            //验证证券账户和通道
            if (StringUtil.isEmpty(dto.getChlId()) && StringUtil.isEmpty(dto.getSecAcctId())){
                errorList.add(new ValidMessage( this.getMessage("ETR_CB_001",getMessage("invest.channelId"))));
            }
            //如子组合申购后，同一个产品下的其他子组合不可再申购，通过通道申购的除外
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isEmpty(dto.getChlId())
                    && service.checkChildPortfolio(dto)){
                errorList.add(new ValidMessage( this.getMessage("ETR_CB_002")));
            }
            //若不允许提交，当申购类型为优先配售且组合可用现金不足，无法提交委托；
            if (StringUtil.isNotEmpty(dto.getProAvailableCash())
                    && StringUtil.isNotEmpty(dto.getPurchBondTotal())
                    && dto.getProAvailableCash().compareTo(dto.getPurchBondTotal()) < 0){
                if (StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstant.CHECK_CASH_SWITCH))
                        && StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.CHECK_CASH_SWITCH), DictCodeListExt1.是或否_否.getValue())
                        && StringUtil.equals(dto.getPurchType(), DictCodeListExt2.申购类型_优先配售.getValue())){
                    errorList.add(new ValidMessage(this.getMessage("ETR_CB_003")));
                }
            }
        }
        return errorList;
    }

    public AjaxResult checkConfirmInfo(EtrConBondDto dto) {
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        //如果头寸不足时
        /*if (dto.getProAvailableCash().compareTo(dto.getPurchBondTotal()) < 0){
            confirmMsg = "组合可用现金不足,当前最多可用金额为:<br/>" +
                    dto.getProAvailableCash() + "，请确认是否继续？";
        }
        if(StringUtil.isNotEmpty(dto.getChlId()) && dto.getChannelAvailableCash().compareTo(dto.getPurchBondTotal()) < 0){
            if (StringUtil.isNotBlank(confirmMsg)){
                confirmMsg += changeLineMark;
            }
            confirmMsg +="通道可用现金不足,当前最多可用金额为:<br/>" +
                    dto.getChannelAvailableCash() + "，请确认是否继续？";
        }*/
        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMsg);
            return wr;
        }
        return null;
    }

}
