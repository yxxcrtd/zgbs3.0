package com.joyintech.tams.invest.settle.dvporder.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.settle.dvporder.DvpOrdersManagementDto;
import com.joyintech.tams.invest.settle.dvporder.validator.DvpOrdersManagement001DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;

/**
 * @program: tams-invest-parent
 * @description:
 * @author: 强成西
 * @create: 2019-01-03 10:56
 **/
public class DvpOrdersManagement001Dto extends DvpOrdersManagementDto implements ProcessEntity {


    /**
     * 交易场所
     */
    private String tradeMarket;
    /**
     * 划款类型
     * 
     * @return
     */
    public String getPayTypeString() {
    	// 一体化对应etliu start
//        return SysDataUtil.getNameFromDataDict(DictCode.划款类型, super.getPayType());
    	return SysDataUtil.getNameFromDataDict(DictCode.平安划款类型, super.getPayType());
//        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, getPayType());
     // 一体化对应etliu end
    }

    /**
     * 是否发送
     * 
     * @return
     */
    public String getIsNeedSendString() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, super.getIsNeedSend());
    }

    /**
     * 币种
     * 
     * @return
     */
    public String getCcyString() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    /**
     * 登记
     *
     * @return
     */
    public String getRegistOrgString() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, super.getRegistOrg());
    }

    /**
     * 平安业务类型
     * 
     * @return
     */
    public String getPaBussTypeString() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, super.getPaBussType());
    }

    /**
     * 处理状态
     *
     * @return
     */
    public String getDealStatusString() {
        return SysDataUtil.getNameFromDataDict(DictCode.处理状态, super.getDealStatus());
    }

    /**
     * 发送状态
     *
     * @return
     */
    public String getSendStatusString() {
        return SysDataUtil.getNameFromDataDict(DictCode.成功失败标志, super.getSendStatus());
    }


    public String getRemark() {
        return super.getRemark();
    }

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String getTradeMarket() {
        return tradeMarket;
    }

    @Override
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
