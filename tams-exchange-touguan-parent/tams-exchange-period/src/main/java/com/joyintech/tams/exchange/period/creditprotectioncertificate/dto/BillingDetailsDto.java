package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;

import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 〈结算明细信息）〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月16日
 * @since 1.0
 */
public class BillingDetailsDto {

    /**
     * 业务编号
     */
    @ApiModelProperty(name = "busiCode",value = "业务编号")
    private String busiCode;

    /**
     * 序号
     */
    @ApiModelProperty(name = "seqNo", value = "序号")
    private String seqNo;
    /**
     * 组合代码
     */
    @NotEmpty(fieldName = "组合代码", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    private String portfolioId;
    /**
     * 组合名称
     */
    @NotEmpty(fieldName = "组合名称", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    /**
     * 资金账户
     */
    @NotEmpty(fieldName = "资金账户", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "acctNo", value = "资金账户")
    private String acctNo;

    /**
     * 名义本金
     */
    @NotEmpty(fieldName = "名义本金", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "repNomCapi", value = "名义本金")
    private BigDecimal repNomCapi;

    /**
     * 债券交付数量
     */
//    @NotEmpty(fieldName = "债券交付数量", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "deliverBondAmount", value = "债券交付数量")
    private BigDecimal deliverBondAmount;

    /**
     * 交付金额
     */
    @NotEmpty(fieldName = "交付金额", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "交付金额", value = "交付金额")
    private BigDecimal deliveryAmount;

    /**
     * 备注
     */
    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    /**
     * 证券托管户
     */
    @ApiModelProperty(name = "secManageAcctId",value = "证券托管户")
    private String secManageAcctId;

    /**
     * 证券管理账户/通道代码，无通道无证券管理账户的存999999
     */
    @ApiModelProperty(name = "secAcctId",value = "证券管理账户")
    private String secAcctId;

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public BigDecimal getRepNomCapi() {
        return repNomCapi;
    }

    public void setRepNomCapi(BigDecimal repNomCapi) {
        this.repNomCapi = repNomCapi;
    }

    public BigDecimal getDeliverBondAmount() {
        return deliverBondAmount;
    }

    public void setDeliverBondAmount(BigDecimal deliverBondAmount) {
        this.deliverBondAmount = deliverBondAmount;
    }

    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }
}
