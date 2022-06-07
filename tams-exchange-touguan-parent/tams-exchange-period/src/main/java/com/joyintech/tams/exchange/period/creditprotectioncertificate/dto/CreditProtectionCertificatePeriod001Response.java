package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;


import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CreditProtectionCertificatePeriod001Response extends BaseEntity {


    /**
     * 有效状态
     */
    @ApiModelProperty(name = "validStatus",value = "有效状态")
    private String validStatus;

    /**
     * 有效状态
     */
    @ApiModelProperty(name = "validStatusStr",value = "有效状态")
    private String validStatusStr;

    /**
     * 业务编号
     */
    @ApiModelProperty(name = "busiCode",value = "业务编号")
    private String busiCode;

    /**
     * 平安业务类型
     */
    @ApiModelProperty(name = "paBussType",value = "平安业务类型")
    private String paBussType;

    /**
     * 平安业务类型
     */
    @ApiModelProperty(name = "paBussType",value = "平安业务类型")
    private String paBussTypeStr;

    /**
     * 资产代码
     */
    @ApiModelProperty(name = "finprodMarketId",value = "资产代码")
    private String finprodMarketId;

    /**
     * 资产名称
     */
    @ApiModelProperty(name = "finprodMarketName",value = "资产名称")
    private String finprodMarketName;

    /**
     * 产品起息日
     */
    @ApiModelProperty(name = "vDateWhole",value = "产品起息日")
    private String vDateWhole;

    /**
     * 产品到期日
     */
    @ApiModelProperty(name = "mDateWhole",value = "产品到期日")
    private String mDateWhole;

    /**
     * 名义本金
     */
    @ApiModelProperty(name = "BigDecimal",value = "名义本金")
    private BigDecimal repNomCapi;

    /**
     * 参考实体
     */
    @ApiModelProperty(name = "refEntity",value = "参考实体")
    private String refEntity;

    /**
     * 信用事件
     */
    @ApiModelProperty(name = "creditEvent",value = "信用事件")
    private String creditEvent;

    /**
     * 信用事件
     */
    @ApiModelProperty(name = "creditEvent",value = "信用事件")
    private String creditEventStr;

    /**
     * 信用事件发生时间
     */
    @ApiModelProperty(name = "creditEventHappenedDate",value = "信用事件发生时间")
    private Date creditEventHappenedDate;

    /**
     * 信用事件决定日
     */
    @ApiModelProperty(name = "creditEventResolveDate",value = "信用事件决定日")
    private Date creditEventResolveDate;

    /**
     * 结算方式
     */
    @ApiModelProperty(name = "settleMethod",value = "结算方式")
    private String settleMethod;

    /**
     * 交付债券
     */
    @ApiModelProperty(name = "deliverBond",value = "交付债券")
    private String deliverBond;

    /**
     * 交付时间
     */
    @ApiModelProperty(name = "deliverDate",value = "交付时间")
    private String deliverDate;

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getPaBussType() {
        return paBussType;
    }

    public void setPaBussType(String paBussType) {
        this.paBussType = paBussType;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodMarketName() {
        return finprodMarketName;
    }

    public void setFinprodMarketName(String finprodMarketName) {
        this.finprodMarketName = finprodMarketName;
    }

    public String getvDateWhole() {
        return vDateWhole;
    }

    public void setvDateWhole(String vDateWhole) {
        this.vDateWhole = vDateWhole;
    }

    public String getmDateWhole() {
        return mDateWhole;
    }

    public void setmDateWhole(String mDateWhole) {
        this.mDateWhole = mDateWhole;
    }

    public BigDecimal getRepNomCapi() {
        return repNomCapi;
    }

    public void setRepNomCapi(BigDecimal repNomCapi) {
        this.repNomCapi = repNomCapi;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(String refEntity) {
        this.refEntity = refEntity;
    }

    public String getCreditEvent() {
        return creditEvent;
    }

    public void setCreditEvent(String creditEvent) {
        this.creditEvent = creditEvent;
    }

    public Date getCreditEventHappenedDate() {
        return creditEventHappenedDate;
    }

    public void setCreditEventHappenedDate(Date creditEventHappenedDate) {
        this.creditEventHappenedDate = creditEventHappenedDate;
    }

    public Date getCreditEventResolveDate() {
        return creditEventResolveDate;
    }

    public void setCreditEventResolveDate(Date creditEventResolveDate) {
        this.creditEventResolveDate = creditEventResolveDate;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getDeliverBond() {
        return deliverBond;
    }

    public void setDeliverBond(String deliverBond) {
        this.deliverBond = deliverBond;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
    }

    public String getPaBussTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, this.paBussType);
    }

    public void setPaBussTypeStr(String paBussTypeStr) {
        this.paBussTypeStr = paBussTypeStr;
    }

    public String getValidStatusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, this.validStatus);
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }

    public String getCreditEventStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, this.creditEvent);
    }

    public void setCreditEventStr(String creditEventStr) {
        this.creditEventStr = creditEventStr;
    }
}
