package com.joyintech.tams.invest.period.trdseclendduration.dto;

import java.math.BigDecimal;
import java.util.List;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto;
import com.joyintech.tams.invest.period.trdseclendduration.validator.TrdSecLendDuration002DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import com.joyintech.tams.invest.period.trdrepoduration.validator.TrdRepoDuration002DtoValidator;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * @program: tams-invest-parent
 * @description: 详情页面dto
 * @author: 裴家俊
 * @create: 2019-07-04
 **/
@PageId("TrdSecLendDuration002")
@ValidLogic(targetClass = TrdSecLendDuration002DtoValidator.class)
public class TrdSecLendDuration002Dto extends TrdSecLendDurationDto implements ProcessEntity {

    /**
     * 获取 busiType
     *
     * @return busiType
     */
    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getBusiType() {
        return DictCodeListExt2.共通业务类型_债券借贷存续期.getValue();
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodId() {
        return super.getFinprodId();
    }

    /**
     * 划款指令信息
     */
    private List<BussTrdTransferOrderDto> bussTrdTransferOrder;

    /**
     * 质押券信息
     */
    private List<RepoInfo> secLendInfo;



    private String workflowId;

    private String elementId;

    private String deliveryTypeString;


    private String balanceCheck;//是否校验余额
    /**
     * 获取 elementId
     *
     * @return elementId
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     *
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 bussTrdTransferOrder
     *
     * @return bussTrdTransferOrder
     */
    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        return bussTrdTransferOrder;
    }

    /**
     * 设置 bussTrdTransferOrder
     *
     * @param bussTrdTransferOrder bussTrdTransferOrder
     */
    public void setBussTrdTransferOrder(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        this.bussTrdTransferOrder = bussTrdTransferOrder;
    }

    /**
     * 获取 workflowId
     *
     * @return workflowId
     */
    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 设置 workflowId
     *
     * @param workflowId workflowId
     */
    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 获取 finprodType2String
     *
     * @return finprodType2String
     */
    public String getFinprodType2String() {
        String result = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用,
            getFinprodType2());
        return result == null ? getFinprodType2() : result;
    }

    /**
     * 获取 statusValidString
     *
     * @return statusValidString
     */
    public String getStatusValidString() {
        String result = SysDataUtil.getNameFromDataDict(DictCode.画面数据状态,
            getStatusValid());
        return result == null ? getStatusValid() : result;
    }

    /**
     * 获取 type6String
     *
     * @return type6String
     */
    public String getTradeMarketString() {
        String result = SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
        return result == null ? getTradeMarket() : result;
    }

    public String getDeliveryTypeString() {
        deliveryTypeString = SysDataUtil.getNameFromDataDict(DictCode.结算方式_债券借贷, getDeliveryType());
        return deliveryTypeString == null ? getDeliveryType() : deliveryTypeString;
    }

    public BigDecimal getShareAmtString() {
        if(null!=getShareAmt()) {
            BigDecimal result = ArithmeticUtil.divide(getShareAmt(), new BigDecimal(10000));
            return result;
        }
        return null;
    }

    public List<RepoInfo> getSecLendInfo() {
        return secLendInfo;
    }

    public void setSecLendInfo(List<RepoInfo> secLendInfo) {
        this.secLendInfo = secLendInfo;
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }
}
