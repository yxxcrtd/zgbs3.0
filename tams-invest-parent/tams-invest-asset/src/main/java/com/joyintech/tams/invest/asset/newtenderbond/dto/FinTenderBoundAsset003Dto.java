package com.joyintech.tams.invest.asset.newtenderbond.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@PageId("FinTenderBoundAsset003")
@ApiModel(value="新债标的(输入输出共用)",description="新债标的(输入输出共用)")
public class FinTenderBoundAsset003Dto extends BaseEntity implements ProcessEntity {

    @ApiModelProperty(name = "issueStage", value = "发行阶段")
    private String issueStage;

    @ApiModelProperty(name = "issueStatus", value = "发行状态")
    private String issueStatus;

    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    /**
     * 金融产品类型（投管分类）
     */
    @ApiModelProperty(name="finprodType2",value="金融产品类型（投管分类）")
    private String finprodType2;

    /**
     * 金融产品简称
     */
    @ApiModelProperty(name="finprodAbbr",value="金融产品简称")
    private String finprodAbbr;

    /**
     * 金融产品全称
     */
    @ApiModelProperty(name="finprodName",value="金融产品全称")
    private String finprodName;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;

    /**
     * 登记托管机构
     */
    @ApiModelProperty(name="registOrg",value="登记托管机构")
    private String registOrg;

    /**
     * 增发批次,首发时为0
     */
    @ApiModelProperty(name = "addBatch", value = "增发批次")
    private Long addBatch;

    /**
     * 发行日
     */
    @ApiModelProperty(name="idate",value="发行日")
    private Date idate;

    /**
     * 起息日
     */
    @ApiModelProperty(name="vdate",value="起息日")
    private Date vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name="mdate",value="到期日")
    private Date mdate;

    /**
     * 上市代码
     */
    @ApiModelProperty(name = "appearMarketId", value = "上市代码")
    private String appearMarketId;

    /**
     * 上市简称
     */
    @ApiModelProperty(name = "appearMarketAbbr", value = "上市简称")
    private String appearMarketAbbr;

    /**
     * 上市日期
     */
    @ApiModelProperty(name = "listingDate", value = "上市日期")
    private Date listingDate;

    /**
     * 备注
     */
    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    /**
     * 操作类型：NEW,MODIFY,DELETE,REVOKE
     */
    @ApiModelProperty(name="operateType",value="操作类型：NEW,MODIFY,DELETE,REVOKE")
    private String operateType;

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


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public Long getAddBatch() {
        return addBatch;
    }

    public void setAddBatch(Long addBatch) {
        this.addBatch = addBatch;
    }

    public Date getIdate() {
        return idate;
    }

    public void setIdate(Date idate) {
        this.idate = idate;
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

    public String getAppearMarketId() {
        return appearMarketId;
    }

    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    public String getAppearMarketAbbr() {
        return appearMarketAbbr;
    }

    public void setAppearMarketAbbr(String appearMarketAbbr) {
        this.appearMarketAbbr = appearMarketAbbr;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
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

    public String getIssueStage() {
        return issueStage;
    }

    public void setIssueStage(String issueStage) {
        this.issueStage = issueStage;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 市场代码
     * {@inheritDoc}
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId(){
        return this.finprodMarketId;
    }
}
