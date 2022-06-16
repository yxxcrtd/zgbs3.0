package com.joyintech.tams.business.asset.protect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  信用保护凭证标的
 * 〈主页查询〉
 * @author wangshegnzhi
 * @version 1.0 2022年5月09日
 * @see CreditProtectionCertificateDto
 * @since 1.0
 */
@ApiModel(value="信用保护凭证标的",description="信用保护凭证标的")
public class CreditProtectionCertificateDto extends BaseEntity implements ProcessEntity {

    private String finprodId;//	资产代码
    private String finprodMarketId; //资产代码
    private String finprodName;//		资产名称
    private String finprodAbbr;//		资产简称
    private String tradeMarket;
    private String tradeMarketStr;//		交易场所
    private String issueRshort;
    private String issueRshortStr;//		创设机构
    private BigDecimal issueAmt;//		名义本金总金额（万元）
    private String protectObject;//		保护对象
    private String protectObjectStr;//		保护对象
    private String refEntityId;
    private String refEntity;//		参考实体
    private String refSec;//		参考债项
    private String refSecFeatures;//		参考债项特征
    private Date vDate;//		起始日
    private String gracePeriod;//		宽限期（天）
    private Date mDate;//		到期日
    private Date pledgeDeadlineDate;//		簿记建档日
    private Date payDate;//		缴款日
    private Date listingDate;//		上市流通日
    private String calendarId;//		交易日历
    private BigDecimal issuePrice;//		创设价格（元）
    private String ratebasic;//		计费年度天数
    private String settleMethod;
    private String settleMethodStr;//		结算方式
    private String isAllowOverwrite;
    private String isOrNoStr;//		是否允许wind覆盖/**
    @ApiModelProperty(name="operateType",value="操作类型：NEW,MODIFY,DELETE,REVOKE")
    private String operateType;


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return tradeMarketStr;
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getIssueRshort() {
        return issueRshort;
    }

    public void setIssueRshort(String issueRshort) {
        this.issueRshort = issueRshort;
    }

    public String getRefEntityId() {
        return refEntityId;
    }

    public void setRefEntityId(String refEntityId) {
        this.refEntityId = refEntityId;
    }

    public String getIssueRshortStr() {
        return issueRshortStr;
    }

    public void setIssueRshortStr(String issueRshortStr) {
        this.issueRshortStr = issueRshortStr;
    }

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getProtectObject() {
        return protectObject;
    }

    public void setProtectObject(String protectObject) {
        this.protectObject = protectObject;
    }

    public String getProtectObjectStr() {
        return protectObjectStr;
    }

    public void setProtectObjectStr(String protectObjectStr) {
        this.protectObjectStr = protectObjectStr;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(String refEntity) {
        this.refEntity = refEntity;
    }

    public String getRefSec() {
        return refSec;
    }

    public void setRefSec(String refSec) {
        this.refSec = refSec;
    }

    public String getRefSecFeatures() {
        return refSecFeatures;
    }

    public void setRefSecFeatures(String refSecFeatures) {
        this.refSecFeatures = refSecFeatures;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getvDate() {
        return vDate;
    }

    public void setvDate(Date vDate) {
        this.vDate = vDate;
    }

    public String getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getPledgeDeadlineDate() {
        return pledgeDeadlineDate;
    }

    public void setPledgeDeadlineDate(Date pledgeDeadlineDate) {
        this.pledgeDeadlineDate = pledgeDeadlineDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getCalendarId() {
        if(StringUtil.equals(this.calendarId, "04")) {
            return "上交所";
        }else if(StringUtil.equals(this.calendarId, "05")) {
            return "深交所";
        }
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getRatebasic() {
        return ratebasic;
    }

    public void setRatebasic(String ratebasic) {
        this.ratebasic = ratebasic;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getSettleMethodStr() {
        return settleMethodStr;
    }

    public void setSettleMethodStr(String settleMethodStr) {
        this.settleMethodStr = settleMethodStr;
    }

    public String getIsAllowOverwrite() {
        return isAllowOverwrite;
    }

    public void setIsAllowOverwrite(String isAllowOverwrite) {
        this.isAllowOverwrite = isAllowOverwrite;
    }

    public String getIsOrNoStr() {
        return isOrNoStr;
    }

    public void setIsOrNoStr(String isOrNoStr) {
        this.isOrNoStr = isOrNoStr;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
