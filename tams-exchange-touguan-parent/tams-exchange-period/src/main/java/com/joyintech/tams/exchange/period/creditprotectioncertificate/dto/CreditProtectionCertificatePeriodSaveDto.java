package com.joyintech.tams.exchange.period.creditprotectioncertificate.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.validator.CreditProtectionCertificatePeriodSaveDtoValidator;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PageId("CreditProtectionCertificate002")
@ValidLogic(targetClass = CreditProtectionCertificatePeriodSaveDtoValidator.class , groups = {ValidateGroupEdit.class})
@ApiModel(value="信用凭证保护存续期",description="信用凭证保护存续期")
public class CreditProtectionCertificatePeriodSaveDto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;

    @ApiModelProperty(name="elementId",value="按钮id",example="EtrExchangeTradeJj_btn_submit/EtrExchangeTradeJj_btn_save")
    private String elementId;

    @ApiModelProperty(name="pageId",value="d",example="EtrExchangeTradeJj")
    private String pageId;

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
    @NotEmpty(fieldName = "资产代码", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "finprodMarketId",value = "资产代码")
    private String finprodMarketId;

    /**
     * 资产名称
     */
    @NotEmpty(fieldName = "资产名称", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "finprodMarketName",value = "资产名称")
    private String finprodMarketName;

    /**
     * 产品起息日
     */
    @NotEmpty(fieldName = "产品起息日", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "vDateWhole",value = "产品起息日")
    private Date vDate;

    /**
     * 产品到期日
     */
    @NotEmpty(fieldName = "产品到期日", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "mDateWhole",value = "产品到期日")
    private Date mDate;

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
//    @NotEmpty(fieldName = "交付债券", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "deliverBond",value = "交付债券")
    private String deliverBond;

    /**
     * 债券交付数量
     */
    @ApiModelProperty(name = "deliverBondAmount",value = "债券交付数量")
    private BigDecimal deliverBondAmount;

    /**
     * 交付时间
     */
    @NotEmpty(fieldName = "交付时间", groups = {ValidateGroupEdit.class})
    @ApiModelProperty(name = "deliverDate",value = "交付时间")
    private Date deliverDate;

    /**
     * 结算明细
     */
    @ApiModelProperty(name = "billingDetails",value = "结算明细")
    private List<BillingDetailsDto> billingDetails;

    /**
     * 证券托管户
     */
    @ApiModelProperty(name = "secManageAcctId",value = "证券托管户")
    private String secManageAcctId;

    /**
     * 记录操作方式
     */
    @ApiModelProperty(name = "optType",value = "记录操作方式")
    private String optType;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;


    /**
     * 汇总数据
     */
    @ApiModelProperty(name="baseBussinessDto",value="汇总数据")
    private BaseBussinessDto baseBussinessDto;

    /**
     * 明细数据
     */
    @ApiModelProperty(name="baseBussinessDtos",value="明细数据")
    private List<BaseBussinessDto> baseBussinessDtos;

    @ApiModelProperty(name="tradeDate",value="交易日期")
    private Date tradeDate;


    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BaseBussinessDto getBaseBussinessDto() {
        return baseBussinessDto;
    }

    public void setBaseBussinessDto(BaseBussinessDto baseBussinessDto) {
        this.baseBussinessDto = baseBussinessDto;
    }

    public List<BaseBussinessDto> getBaseBussinessDtos() {
        return baseBussinessDtos;
    }

    public void setBaseBussinessDtos(List<BaseBussinessDto> baseBussinessDtos) {
        this.baseBussinessDtos = baseBussinessDtos;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

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

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
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

    public List<BillingDetailsDto> getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(List<BillingDetailsDto> billingDetails) {
        this.billingDetails = billingDetails;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Date getvDate() {
        return vDate;
    }

    public void setvDate(Date vDate) {
        this.vDate = vDate;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public BigDecimal getDeliverBondAmount() {
        return deliverBondAmount;
    }

    public void setDeliverBondAmount(BigDecimal deliverBondAmount) {
        this.deliverBondAmount = deliverBondAmount;
    }
}

