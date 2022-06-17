package com.joyintech.tams.business.asset.protect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 信用保护凭证标的新增/修改/查看公用DTO
 */
@PageId("CreditProtectionCertificate002")
@ApiModel(value="CreditProtectionCertificate002Dto",description="新债标的(输入输出共用)")
public class CreditProtectionCertificate002Dto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name="pageId",value="画面ID",example="CreditProtectionCertificate002")
    private String pageId;
    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;
    /**
     * 金融产品类型（估值核算）
     */
    @ApiModelProperty(name="finprodType",value="金融产品类型（估值核算）")
    private String finprodType;
    /**
     * 金融产品类型（投管分类）
     */
    @ApiModelProperty(name="finprodType2",value="金融产品类型（投管分类）")
    private String finprodType2;
    /**
     * 金融产品简称
     */
    @ApiModelProperty(name="finprodAbbr",value="资产简称",required = true)
    private String finprodAbbr;
    /**
     * 金融产品全称
     */
    @ApiModelProperty(name="finprodName",value="资产名称",required = true)
    private String finprodName;
    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所",required = true)
    private String tradeMarket;
    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarketId",value="资产代码后缀",required = true)
    private String tradeMarketId;
    /**
     * 发行人 - 创设机构
     */
    @ApiModelProperty(name="issuershort",value="创设机构",required = true)
    private String issuershort;
    /**
     * 名义本金总额
     */
    @ApiModelProperty(name="nomCapi",value="名义本金总额",required = true)
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal nomCapi;
    /**
     *保护对象
     */
    @ApiModelProperty(name="protectObject",value="保护对象",required = true)
    private String protectObject;
    /**
     * 参考实体
     */
    @ApiModelProperty(name="refEntity",value="参考实体")
    private String refEntity;
    /**
     * 参考债项
     */
    @ApiModelProperty(name="refSec",value="参考债项")
    private String refSec;
    /**
     * 参考债项特征
     */
    @ApiModelProperty(name="refSecFeatures",value="参考债项特征")
    private String refSecFeatures;
    /**
     * 起息日
     */
    @ApiModelProperty(name="vdate",value="起息日",required = true)
    private Date vdate;
    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日",required = true)
    private Date mdate;
    /**
     * 宽限期
     */
    @ApiModelProperty(name="gracePeriod",value="宽限期")
    private Long gracePeriod;
    /**
     * 簿记建档日
     */
    @ApiModelProperty(name="bookBuildingDate",value="簿记建档日")
    private Date bookBuildingDate;
    /**
     * 缴款日
     */
    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;
    /**
     * 上市日期
     */
    @ApiModelProperty(name="listingDate",value="上市日期")
    private Date listingDate;
    /**
     * 交易日历
     */
    @ApiModelProperty(name="calendarId",value="交易日历",required = true)
    private String calendarId;
    /**
     * 发行价 - 创设价格
     */
    @ApiModelProperty(name="issuePrice",value="创设价格",required = true)
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal issuePrice;
    /**
     * 计息基础 - 计费年度天数
     */
    @ApiModelProperty(name="basis",value="计费年度天数")
    private String basis;
    /**
     * 结算方式
     */
    @ApiModelProperty(name="settleMethod",value="结算方式",required = true)
    private String settleMethod;
    /**
     * 是否允许资讯数据覆盖
     */
    @ApiModelProperty(name = "isAllowOverwrite", value = "是否允许资讯数据覆盖",required = true)
    private String isAllowOverwrite;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;
    /**
     * 期限天数
     */
    @ApiModelProperty(name="termDays",value="期限天数")
    private Long termDays;
    /**
     * 实际到期日
     */
    @ApiModelProperty(name="actmdate",value="实际到期日")
    private Date actmdate;
    /**
     * 备注
     */
    @ApiModelProperty(name="remark",value="备注")
    private String remark;

    /**
     * 操作类型：NEW,MODIFY,DELETE,REVOKE
     */
    @ApiModelProperty(name="operateType",value="操作类型：NEW,MODIFY,DELETE,REVOKE")
    private String operateType;

    /**
     * 操作类型：加券操作
     */
    @ApiModelProperty(name="operateType2",value="操作类型：auto、cover")
    private String operateType2;

    /**
     * 操作类型：加券操作
     */
    @ApiModelProperty(name="operateStatus",value="操作类型",required = true)
    private String operateStatus;

    /**
     * 页面模式
     */
    @ApiModelProperty(name="pageModel",value="页面模式")
    private String pageModel;

    /**
     * elementId
     */
    @ApiModelProperty(name="elementId",value="要素id")
    private String elementId;

    @ApiModelProperty(name = "calModel", value = "参考收益率:rate,发行价格:price")
    private String calModel;

    /**
     * 附件信息DTO
     */
    private List<FileCommonDto> fileCommonList;

    /**
     * 附件信息DTO
     */
    private List<UploadFile> fileArray;

    /**
     * 附件
     */
    @ApiModelProperty(name="mstFileInputParameter",value="附件")
    private MstFileInputParameter mstFileInputParameter;

    private String isOrNoStr;
    private String settleMethodStr;
    private String refEntityStr;
    private String issuershortStr;
    private String tradeMarketStr;
    private String refSecList;

    private String refSecLists;

    public String getIsOrNoStr() {
        return isOrNoStr;
    }

    public void setIsOrNoStr(String isOrNoStr) {
        this.isOrNoStr = isOrNoStr;
    }

    public String getSettleMethodStr() {
        return settleMethodStr;
    }

    public void setSettleMethodStr(String settleMethodStr) {
        this.settleMethodStr = settleMethodStr;
    }

    public String getRefEntityStr() {
        return refEntityStr;
    }

    public void setRefEntityStr(String refEntityStr) {
        this.refEntityStr = refEntityStr;
    }

    public String getIssuershortStr() {
        return issuershortStr;
    }

    public void setIssuershortStr(String issuershortStr) {
        this.issuershortStr = issuershortStr;
    }

    public String getTradeMarketStr() {
        return tradeMarketStr;
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getRefSecList() {
        return refSecList;
    }

    public void setRefSecList(String refSecList) {
        this.refSecList = refSecList;
    }

    public String getRefSecLists() {
        return refSecLists;
    }

    public void setRefSecLists(String refSecLists) {
        this.refSecLists = refSecLists;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketId() {
        return tradeMarketId;
    }

    public void setTradeMarketId(String tradeMarketId) {
        this.tradeMarketId = tradeMarketId;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getIssuershort() {
        return issuershort;
    }

    public void setIssuershort(String issuershort) {
        this.issuershort = issuershort;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Long getTermDays() {
        return termDays;
    }

    public void setTermDays(Long termDays) {
        this.termDays = termDays;
    }

    public Date getActmdate() {
        return actmdate;
    }

    public void setActmdate(Date actmdate) {
        this.actmdate = actmdate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getProtectObject() {
        return protectObject;
    }

    public void setProtectObject(String protectObject) {
        this.protectObject = protectObject;
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

    public Long getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public Date getBookBuildingDate() {
        return bookBuildingDate;
    }

    public void setBookBuildingDate(Date bookBuildingDate) {
        this.bookBuildingDate = bookBuildingDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getNomCapi() {
        return nomCapi;
    }

    public void setNomCapi(BigDecimal nomCapi) {
        this.nomCapi = nomCapi;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getSettleMethod() {
        return settleMethod;
    }

    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    public String getIsAllowOverwrite() {
        return isAllowOverwrite;
    }

    public void setIsAllowOverwrite(String isAllowOverwrite) {
        this.isAllowOverwrite = isAllowOverwrite;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateType2() {
        return operateType2;
    }

    public void setOperateType2(String operateType2) {
        this.operateType2 = operateType2;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getCalModel() {
        return calModel;
    }

    public void setCalModel(String calModel) {
        this.calModel = calModel;
    }

    public List<FileCommonDto> getFileCommonList() {
        return fileCommonList;
    }

    public void setFileCommonList(List<FileCommonDto> fileCommonList) {
        this.fileCommonList = fileCommonList;
    }

    public List<UploadFile> getFileArray() {
        return fileArray;
    }

    public void setFileArray(List<UploadFile> fileArray) {
        this.fileArray = fileArray;
    }

    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }
}
