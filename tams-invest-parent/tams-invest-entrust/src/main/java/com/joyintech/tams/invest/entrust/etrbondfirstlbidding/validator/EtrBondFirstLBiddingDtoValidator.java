/*
 * 文件名：EtrBondFirstLBidDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.entrust.etrbondfirstlbidding.validator;

import com.joyintech.framework.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.base.entrust.impl.dto.TrdCommandDispatcher001Dto;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.service.EtrBondFirstBiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author yangjm
 * @version 1.0 2019年5月6日
 * @see EtrBondFirstLBiddingDtoValidator
 * @since 1.0
 */
@Component
public class EtrBondFirstLBiddingDtoValidator implements BeanValidator{

    @Autowired
    private EtrBondFirstBiddingService service;
    @Autowired
    private FinProductMapper finProductMapper;


    List<ValidMessage> resultList = new ArrayList<ValidMessage>();

    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {

        List<ValidMessage> errorList = new ArrayList<>();
        EtrBondFirstLBiddingDto dto = (EtrBondFirstLBiddingDto)value;
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<EtrBondFirstLBiddingDto> listDto = dto.getSelectedItem();
            for (EtrBondFirstLBiddingDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_删除.getValue());
                errorList.addAll(service.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "close")) {
            List<EtrBondFirstLBiddingDto> listDto = dto.getSelectedItem();
            for (EtrBondFirstLBiddingDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_关闭.getValue());
                if("08".equals(entity.getEntrustStatus())){
                    errorList.add(new ValidMessage("存在指令在流程中，不可关闭！"));
                    return errorList;
                }
                errorList.addAll(service.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "recall")) {
            List<EtrBondFirstLBiddingDto> listDto = dto.getSelectedItem();
            for (EtrBondFirstLBiddingDto entity : listDto) {
                entity.setOperateType(BussinessDictCodeList.委托操作类型_撤回.getValue());
                if (StringUtil.isNotEmpty(entity.getWinPriceStr())) {
                    errorList.add(new ValidMessage("", entity.getWinPriceStr() + "中标结果已存在不能撤回！"));
                    return errorList;
                }
                if("08".equals(entity.getEntrustStatus())){
                    errorList.add(new ValidMessage("存在指令在流程中，不可撤销！"));
                    return errorList;
                }
                errorList.addAll(service.check(entity));
            }
        }
        else if (StringUtil.equals(dto.getOperateType(), "edit")) {
            dto.setOperateType(BussinessDictCodeList.委托操作类型_修改.getValue());
            if (StringUtil.equals(dto.getPs(), BussinessDictCodeList.交易方向_卖出.getValue())
                && !StringUtil.isNotEmpty(dto.getPortfolioId())) {
                errorList.add(new ValidMessage("finprodId", this.getMessage("ETR_BOND_001")));
            }
            if (StringUtil.isNotEmpty(dto.getWinPriceStr())) {
                errorList.add(new ValidMessage("", dto.getWinPriceStr() + "中标结果已存在不能修改！"));
                return errorList;
            }
//            存在系统变量是否有截标 为01 是时 增加截标check
            String isCloseBid = CacheUtil.getSysParam(BussinessConstant.IS_CLOSE_BID);
            if (StringUtil.equals(isCloseBid, DictCodeList.是否_是.getValue())&&service.checkIsBidClose(dto) > 0){
                errorList.add(new ValidMessage(getMessage("ETR_BOND_007")));
            }
            errorList.addAll(service.check(dto));
        }
        else {
            Date vdate = dto.getVdate();
            Date mdate = dto.getMdate();
            Date finmdate = dto.getFinmdate();
            Date finvdate = dto.getFinvdate();
            Date entrustDate = dto.getEntrustDate();
            if (vdate != null && mdate != null) {
                if (vdate.after(mdate)) {
                    errorList.add(new ValidMessage("vdate", this.getMessage("ETR_X_005")));
                }
//                if(finvdate != null && vdate.before(finvdate)){
//                    errorList.add(new ValidMessage("vdate",this.getMessage("ETR_X_006")+DateUtil.format(finvdate)));
//                }
                if(finmdate != null && mdate.after(finmdate)){
                    errorList.add(new ValidMessage("mdate",this.getMessage("ETR_X_007")+DateUtil.format(finmdate)));
                }
                if (null != entrustDate &&(entrustDate.before(vdate) || entrustDate.after(mdate))) {
                    errorList.add(new ValidMessage("entrustDate",this.getMessage("投标日要在委托有效期内，在委托开始日和委托结束日之间!")
                        + DateUtil.format(vdate) + GlobalConstant.COMMA + DateUtil.format(mdate)));
                }
            }
            // delete by renping 改为共通处理 2019-08-22 start
//            BigDecimal entrustNum = dto.getEntrustNum();
//            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01")){
//                BigDecimal limitAmt = service.selectLimitAmt(dto);
//                if(limitAmt != null && entrustNum.compareTo(limitAmt) > 0){
//                    errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_BOND_005")+limitAmt));
//                }
//            }
            // delete by renping 改为共通处理 2019-08-22 end
            if (StringUtil.isNotEmpty(dto) && !StringUtil.isNotEmpty(dto.getInvAim()) ) {
                errorList.add(new ValidMessage("invAim",  "请输入投资目的！"));

            }
            BussEtrInvestPersonInfoDto personDto =  dto.getEtrInvestPersonInfo();
            if (StringUtil.isNotEmpty(personDto) && !StringUtil.isNotEmpty(personDto.getCounterId()) ) {
                errorList.add(new ValidMessage("etrInvestPersonInfo.counterId",  "请输入交易对手！"));
            }
//            if (service.checkIsBidClose(dto) > 0){
//                errorList.add(new ValidMessage("", this.getMessage("ETR_BOND_007")));
//            }
            //            存在系统变量是否有截标 为01 是时 增加截标check
            String isCloseBid = CacheUtil.getSysParam(BussinessConstant.IS_CLOSE_BID);
            if (StringUtil.equals(isCloseBid, DictCodeList.是否_是.getValue())&&service.checkIsBidClose(dto) > 0){
                errorList.add(new ValidMessage(getMessage("ETR_BOND_007")));
            }
            errorList.addAll(service.check(dto));
            // ZGZGST-1232 fuyongsheng 20210820:增加SPPI测试 系统参数开关,Y:需要测试   继续增加开关 仅当页面展示投资目的时校验 add by yujinfa
            if(DictCodeListExt1.是否_是.getValue().equals(CacheUtil.getSysParam("IS_NEED_SPPI_CHECK"))&&"01".equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"))){
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
        }
        //是否展示资产四五级分类
        if(StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"),"01")) {
        	if(StringUtil.isEmpty(dto.getAssetType11())) {
        		resultList.add(new ValidMessage("type11",
                        getMessage("COMMON_E_0057", getMessage("EtrBondFirstLBidding002.type11"))));
        	}
        	 
        }

        // modify by etliu 20211211 start
        // ZGZGST-4157: 待接收的指令点撤回提示【系统异常】
        if (StringUtil.isNotBlank(dto.getFinprodMarketId()) && dto.getEtrInvestPersonInfo() != null
            && StringUtil.isNotBlank(dto.getEtrInvestPersonInfo().getTrader())) {
            //同一债券同一投标日，若已有分发记录，再次对同一标的同一投标日的委托做分发时，限制只能分发给同一交易员。否则系统给出提示信息并限制操作的check
            TrdCommandDispatcher001Dto dispatcher001Dto = new TrdCommandDispatcher001Dto();
            dispatcher001Dto.setBussBidDate(DateUtil.format(dto.getEntrustDate(),DateUtil.DEFAULT_DATE_FORMAT_EN));
            dispatcher001Dto.setFinprodMarketId(dto.getFinprodMarketId());
            List<TrdCommandDispatcher001Dto> listTraders=service.getTraders(dispatcher001Dto);
            if(!listTraders.isEmpty() && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo()) && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())){
                for (TrdCommandDispatcher001Dto list2 : listTraders) {
                    if(!StringUtil.equals(list2.getTrader(),dto.getEtrInvestPersonInfo().getTrader())&&StringUtil.isNotEmpty(list2)){
                        errorList.add(new ValidMessage(this.getMessage("TRD_I_0004")+list2.getTrader()));
                        resultList.add(new ValidMessage("type11",
                                getMessage("COMMON_E_0057", getMessage("EtrBondFirstLBidding002.type11"))));
                    }
                }
            }
        }
        // modify by etliu 20211211 end

        return errorList;
    }

}
