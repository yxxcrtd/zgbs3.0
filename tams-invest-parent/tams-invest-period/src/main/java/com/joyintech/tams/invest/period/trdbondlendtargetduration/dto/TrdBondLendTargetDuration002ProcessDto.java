package com.joyintech.tams.invest.period.trdbondlendtargetduration.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdbondlendtargetduration.InvestTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdbondlendtargetduration.TrdBondLendTargetDurationDto;
import com.joyintech.tams.invest.period.trdbondlendtargetduration.validator.TrdBondLendTargetDuration002DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.dto
 * @Description: 页面查询参数
 * @date Date : 2018年09月25日 9:42
 */
@PageId("TrdBondLendTargetDuration002")
@ValidLogic(targetClass = TrdBondLendTargetDuration002DtoValidator.class, groups = {ValidateGroupDelete.class,
    ValidateGroupEdit.class})
public class TrdBondLendTargetDuration002ProcessDto extends TrdBondLendTargetDurationDto implements ProcessEntity {

    /**
     * 页面元素
     */
    private String elementId;

    /**
     * 交易信息
     */
    private InvestTradeInfoDto tradeInfoDto;

    /**
     * 附件上传
     */
    private List<UploadFile> uploadFileList;

    private String balanceCheck;//是否校验余额
    /**
     * 获取 statusValidString
     *
     * @return statusValidString
     */
    public String getStatusValidString() {
        String result = SysDataUtil.getNameFromDataDict(DictCode.画面数据状态, getStatusValid());
        return result == null ? getStatusValid() : result;
    }


    public String getTradeTypeString() {
        String result = SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, getTradeType());
        return result == null ? getTradeType() : result;
    }

    /**
     * 获取 busiCode
     *
     * @return busiCode.
     */
    @Override
    @PrimaryKey(Field.PK_1)
    public String getMpFinprodId() {
        return super.getMpFinprodId();
    }

    @Override
    @NotEmpty(fieldName = "实收利息总额", groups = {ValidateGroupEdit.class})
    public BigDecimal getIntAmt() {
        return super.getIntAmt();
    }

    @Override
    @NotEmpty(fieldName = "实收本息总额", groups = {ValidateGroupEdit.class})
    public BigDecimal getTradeAmt() {
        return super.getTradeAmt();
    }

    @Override
    @NotEmpty(fieldName = "资金到账日", groups = {ValidateGroupEdit.class})
    public String getTradeDate() {
        return super.getTradeDate();
    }

    /**
     * 获取 bussTrdTransferOrder
     *
     * @return bussTrdTransferOrder
     */
    @NotEmpty(fieldName = "划款指令", groups = {ValidateGroupEdit.class})
    public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
        if (super.getBussTrdTransferOrder() == null || super.getBussTrdTransferOrder().isEmpty()) {
            return null;
        }
        return super.getBussTrdTransferOrder();
    }

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
     * 获取 tradeInfoDto
     *
     * @return tradeInfoDto
     */
    public InvestTradeInfoDto getTradeInfoDto() {
        return tradeInfoDto;
    }

    /**
     * 设置 tradeInfoDto
     *
     * @param tradeInfoDto tradeInfoDto
     */
    public void setTradeInfoDto(InvestTradeInfoDto tradeInfoDto) {
        this.tradeInfoDto = tradeInfoDto;
    }

    /**
     * 获取 uploadFileList
     *
     * @return uploadFileList
     */
    public List<UploadFile> getUploadFileList() {
        return uploadFileList;
    }

    /**
     * 设置 uploadFileList
     *
     * @param uploadFileList uploadFileList
     */
    public void setUploadFileList(List<UploadFile> uploadFileList) {
        this.uploadFileList = uploadFileList;
    }


    public String getTheoryAmtString() {
        if (super.getTheoryAmt() != null) {
            DecimalFormat df2 = new DecimalFormat("###,###,###,###,###,###,##0.00");
            return df2.format(super.getTheoryAmt());
        }
        return "";
    }

    public String getBalanceCheck() {
        return balanceCheck;
    }

    public void setBalanceCheck(String balanceCheck) {
        this.balanceCheck = balanceCheck;
    }
}
