package com.joyintech.tams.business.asset.tenderbond.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 新债标的新增/修改/查看公用DTO
 */
@PageId("FinTenderBondAsset002")
@ApiModel(value="FinTenderBondAsset002Dto",description="新债标的(输入输出共用)")
public class FinTenderBondAsset002Dto extends BaseEntity implements ProcessEntity {
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
     * 收益类型
     */
    @ApiModelProperty(name="profitType",value="收益类型")
    private String profitType;

    /**
     * 息票品种
     */
    @ApiModelProperty(name="couponSpecies",value="息票品种",required = true)
    private String couponSpecies;

    /**
     * 通道代码
     */
    @ApiModelProperty(name="chlFinprodId",value="通道代码")
    private String chlFinprodId;

    /**
     * 市场代码
     */
    @ApiModelProperty(name="finprodMarketId",value="资产代码",required = true)
    private String finprodMarketId;

    /**
     * 发行认购代码
     */
    @ApiModelProperty(name="issueId",value="发行认购代码")
    private String issueId;

    /**
     * 发行价
     */
    @ApiModelProperty(name="issuePrice",value="发行价")
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    /**
     * 发行规模
     */
    @ApiModelProperty(name="issueAmt",value="发行规模")
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal issueAmt;

    /**
     * 币种
     */
    @ApiModelProperty(name="ccy",value="币种",required = true)
    private String ccy;

    /**
     * 境内外
     */
    @ApiModelProperty(name="blnArea",value="境内外")
    private String blnArea;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarket",value="交易场所",required = true)
    private String tradeMarket;

    /**
     * 交易场所
     */
    @ApiModelProperty(name="tradeMarketValue",value="交易场所字典值")
    private String tradeMarketValue;

    /**
     * 交易日历
     */
    @ApiModelProperty(name="calendarId",value="交易日历")
    private String calendarId;

    /**
     * 发行方式/募集方式
     */
    @ApiModelProperty(name="issueType",value="募集方式",required = true)
    private String issueType;

    /**
     * 运作模式
     */
    @ApiModelProperty(name="operationType",value="运作模式")
    private String operationType;

    /**
     * 委托方式
     */
    @ApiModelProperty(name="entrustType",value="委托方式")
    private String entrustType;

    /**
     * 委托方
     */
    @ApiModelProperty(name="entruster",value="委托方")
    private String entruster;

    /**
     * 托管人
     */
    @ApiModelProperty(name="trusteeId",value="托管人")
    private String trusteeId;

    /**
     * 发行人
     */
    @ApiModelProperty(name="issuer",value="发行人",required = true)
    private String issuer;

    /**
     * 管理人
     */
    @ApiModelProperty(name="manager",value="管理人")
    private String manager;

    /**
     * 融资人
     */
    @ApiModelProperty(name="financier",value="融资人")
    private String financier;

    /**
     * 发行日
     */
    @ApiModelProperty(name="idate",value="发行日")
    private Date idate;

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
     * 清盘日
     */
    @ApiModelProperty(name="liquidationDate",value="清盘日")
    private Date liquidationDate;

    /**
     * 是否通道
     */
    @ApiModelProperty(name="isChl",value="是否通道")
    private String isChl;

    /**
     * 是否永续
     */
    @ApiModelProperty(name="isSus",value="是否永续")
    private String isSus;

    /**
     * 永续条款
     */
    @ApiModelProperty(name="sustainableRemark",value="永续条款")
    private String sustainableRemark;

    /**
     * 是否含权
     */
    @ApiModelProperty(name="isRight",value="是否含权")
    private String isRight;

    /**
     * 本金收益特征
     */
    @ApiModelProperty(name="capiIncomeFeature",value="本金收益特征")
    private String capiIncomeFeature;

    /**
     * 母金融产品代码
     */
    @ApiModelProperty(name="pFinprodId",value="母金融产品代码")
    private String pFinprodId;

    /**
     * 原金融产品代码
     */
    @ApiModelProperty(name="oFinprodId",value="原金融产品代码")
    private String oFinprodId;

    /**
     * 登记托管机构
     */
    @ApiModelProperty(name="registOrg",value="登记托管机构",required = true)
    private String registOrg;

    @ApiModelProperty(name="registOrg",value="登记托管机构字典值")
    private String registOrgValue;

    /**
     * 含权债摊销日
     */
    @ApiModelProperty(name="exeDate",value="含权债摊销日")
    private String exeDate;

    /**
     * 备注
     */
    @ApiModelProperty(name="remark",value="备注")
    private String remark;

    /**
     * 合同编号
     */
    @ApiModelProperty(name="contractNo",value="合同编号")
    private String contractNo;

    /**
     * 是否金融机构发行
     */
    @ApiModelProperty(name="isFinOrg",value="是否金融机构发行")
    private String isFinOrg;

    /**
     * 担保人
     */
    @ApiModelProperty(name="sponsor",value="担保人")
    private String sponsor;

    /**
     * 投资顾问
     */
    @ApiModelProperty(name="investAdviser",value="投资顾问")
    private String investAdviser;

    /**
     * 是否轧差清算
     */
    @ApiModelProperty(name="liquidationYesno",value="是否轧差清算")
    private String liquidationYesno;

    /**
     * 认购起始日
     */
    @ApiModelProperty(name="toffStrdate",value="认购起始日")
    private Date toffStrdate;

    /**
     * 认购终止日
     */
    @ApiModelProperty(name="toffEnddate",value="认购终止日")
    private Date toffEnddate;

    /**
     * SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过
     */
    @ApiModelProperty(name="sppiTestStatus",value="SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过")
    private String sppiTestStatus;

    /**
     * 上市流通代码
     */
    @ApiModelProperty(name="circulationCode",value="上市流通代码")
    private String circulationCode;

    @ApiModelProperty(name = "type9", value = "资产一级分类",required = true)
    private String type9;

    @ApiModelProperty(name = "type10", value = "资产二级分类",required = true)
    private String type10;

    @ApiModelProperty(name = "type11", value = "资产三级分类",required = true)
    private String type11;

    /**
     * 上市流通日
     */
    @ApiModelProperty(name = "circulationDate", value = "上市流通日")
    private Date circulationDate;

    /**
     * 标的扩展表字段
     */
    @ApiModelProperty(name="finProductAdd",value="扩展字段")
    private FinProductAddDto productAddDto;

    /**
     * 发行信息
     */
    @ApiModelProperty(name="finProductIssue",value="发行信息")
    private FinProductIssueDto issueDto;

    /**
     * 债券评级信息
     */
    @ApiModelProperty(name="ratingDto",value="债券评级信息,展示时使用")
    private List<MstBondCreditRatingDto> ratingList;

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

    @ApiModelProperty(name = "calModel", value = "参考收益率:rate,发行价格:price")
    private String calModel;

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型（估值核算）
     * @return 金融产品类型（估值核算）
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型（估值核算）
     * @param finprodType 金融产品类型（估值核算）
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 金融产品类型（投管分类）
     * @return 金融产品类型（投管分类）
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类）
     * @param finprodType2 金融产品类型（投管分类）
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 金融产品简称
     * @return 金融产品简称
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * 设置 金融产品简称
     * @param finprodAbbr 金融产品简称
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr == null ? null : finprodAbbr.trim();
    }

    /**
     * 取得 金融产品全称
     * @return 金融产品全称
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 金融产品全称
     * @param finprodName 金融产品全称
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName == null ? null : finprodName.trim();
    }

    /**
     * 取得 收益类型
     * @return 收益类型
     */
    public String getProfitType() {
        return profitType;
    }

    /**
     * 设置 收益类型
     * @param profitType 收益类型
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType == null ? null : profitType.trim();
    }

    /**
     * 取得 息票品种
     * @return 息票品种
     */
    public String getCouponSpecies() {
        return couponSpecies;
    }

    /**
     * 设置 息票品种
     * @param couponSpecies 息票品种
     */
    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies == null ? null : couponSpecies.trim();
    }

    /**
     * 取得 通道代码
     * @return 通道代码
     */
    public String getChlFinprodId() {
        return chlFinprodId;
    }

    /**
     * 设置 通道代码
     * @param chlFinprodId 通道代码
     */
    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId == null ? null : chlFinprodId.trim();
    }

    /**
     * 设置 市场代码
     * @param finprodMarketId 市场代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId == null ? null : finprodMarketId.trim();
    }

    /**
     * 取得 发行认购代码
     * @return 发行认购代码
     */
    public String getIssueId() {
        return issueId;
    }

    /**
     * 设置 发行认购代码
     * @param issueId 发行认购代码
     */
    public void setIssueId(String issueId) {
        this.issueId = issueId == null ? null : issueId.trim();
    }

    /**
     * 取得 发行价
     * @return 发行价
     */
    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    /**
     * 设置 发行价
     * @param issuePrice 发行价
     */
    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * 取得 发行规模
     * @return 发行规模
     */
    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    /**
     * 设置 发行规模
     * @param issueAmt 发行规模
     */
    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 境内外
     * @return 境内外
     */
    public String getBlnArea() {
        return blnArea;
    }

    /**
     * 设置 境内外
     * @param blnArea 境内外
     */
    public void setBlnArea(String blnArea) {
        this.blnArea = blnArea == null ? null : blnArea.trim();
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易日历
     * @return 交易日历
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 交易日历
     * @param calendarId 交易日历
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId == null ? null : calendarId.trim();
    }

    /**
     * 取得 发行方式/募集方式
     * @return 发行方式/募集方式
     */
    public String getIssueType() {
        return issueType;
    }

    /**
     * 设置 发行方式/募集方式
     * @param issueType 发行方式/募集方式
     */
    public void setIssueType(String issueType) {
        this.issueType = issueType == null ? null : issueType.trim();
    }

    /**
     * 取得 委托方式
     * @return 委托方式
     */
    public String getEntrustType() {
        return entrustType;
    }

    /**
     * 设置 委托方式
     * @param entrustType 委托方式
     */
    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType == null ? null : entrustType.trim();
    }

    /**
     * 取得 委托方
     * @return 委托方
     */
    public String getEntruster() {
        return entruster;
    }

    /**
     * 设置 委托方
     * @param entruster 委托方
     */
    public void setEntruster(String entruster) {
        this.entruster = entruster == null ? null : entruster.trim();
    }

    /**
     * 取得 托管人
     * @return 托管人
     */
    public String getTrusteeId() {
        return trusteeId;
    }

    /**
     * 设置 托管人
     * @param trusteeId 托管人
     */
    public void setTrusteeId(String trusteeId) {
        this.trusteeId = trusteeId == null ? null : trusteeId.trim();
    }

    /**
     * 取得 发行人
     * @return 发行人
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 设置 发行人
     * @param issuer 发行人
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer == null ? null : issuer.trim();
    }

    /**
     * 取得 管理人
     * @return 管理人
     */
    public String getManager() {
        return manager;
    }

    /**
     * 设置 管理人
     * @param manager 管理人
     */
    public void setManager(String manager) {
        this.manager = manager == null ? null : manager.trim();
    }

    /**
     * 取得 融资人
     * @return 融资人
     */
    public String getFinancier() {
        return financier;
    }

    /**
     * 设置 融资人
     * @param financier 融资人
     */
    public void setFinancier(String financier) {
        this.financier = financier == null ? null : financier.trim();
    }

    /**
     * 取得 发行日
     * @return 发行日
     */
    public Date getIdate() {
        return idate;
    }

    /**
     * 设置 发行日
     * @param idate 发行日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setIdate(Date idate) {
        this.idate = idate;
    }

    /**
     * 取得 起息日
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     * @param vdate 起息日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     * @return 到期日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期日
     * @param mdate 到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 期限天数
     * @return 期限天数
     */
    public Long getTermDays() {
        return termDays;
    }

    /**
     * 设置 期限天数
     * @param termDays 期限天数
     */
    public void setTermDays(Long termDays) {
        this.termDays = termDays;
    }

    /**
     * 取得 实际到期日
     * @return 实际到期日
     */
    public Date getActmdate() {
        return actmdate;
    }

    /**
     * 设置 实际到期日
     * @param actmdate 实际到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setActmdate(Date actmdate) {
        this.actmdate = actmdate;
    }

    /**
     * 取得 清盘日
     * @return 清盘日
     */
    public Date getLiquidationDate() {
        return liquidationDate;
    }

    /**
     * 设置 清盘日
     * @param liquidationDate 清盘日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setLiquidationDate(Date liquidationDate) {
        this.liquidationDate = liquidationDate;
    }

    /**
     * 取得 是否通道
     * @return 是否通道
     */
    public String getIsChl() {
        return isChl;
    }

    /**
     * 设置 是否通道
     * @param isChl 是否通道
     */
    public void setIsChl(String isChl) {
        this.isChl = isChl == null ? null : isChl.trim();
    }

    /**
     * 取得 是否永续
     * @return 是否永续
     */
    public String getIsSus() {
        return isSus;
    }

    /**
     * 设置 是否永续
     * @param isSus 是否永续
     */
    public void setIsSus(String isSus) {
        this.isSus = isSus == null ? null : isSus.trim();
    }

    /**
     * 取得 永续条款
     * @return 永续条款
     */
    public String getSustainableRemark() {
        return sustainableRemark;
    }

    /**
     * 设置 永续条款
     * @param sustainableRemark 永续条款
     */
    public void setSustainableRemark(String sustainableRemark) {
        this.sustainableRemark = sustainableRemark == null ? null : sustainableRemark.trim();
    }

    /**
     * 取得 是否含权
     * @return 是否含权
     */
    public String getIsRight() {
        return isRight;
    }

    /**
     * 设置 是否含权
     * @param isRight 是否含权
     */
    public void setIsRight(String isRight) {
        this.isRight = isRight == null ? null : isRight.trim();
    }

    /**
     * 取得 本金收益特征
     * @return 本金收益特征
     */
    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    /**
     * 设置 本金收益特征
     * @param capiIncomeFeature 本金收益特征
     */
    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature == null ? null : capiIncomeFeature.trim();
    }

    /**
     * 取得 母金融产品代码
     * @return 母金融产品代码
     */
    public String getpFinprodId() {
        return pFinprodId;
    }

    /**
     * 设置 母金融产品代码
     * @param pFinprodId 母金融产品代码
     */
    public void setpFinprodId(String pFinprodId) {
        this.pFinprodId = pFinprodId == null ? null : pFinprodId.trim();
    }

    /**
     * 取得 原金融产品代码
     * @return 原金融产品代码
     */
    public String getoFinprodId() {
        return oFinprodId;
    }

    /**
     * 设置 原金融产品代码
     * @param oFinprodId 原金融产品代码
     */
    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId == null ? null : oFinprodId.trim();
    }

    /**
     * 取得 登记托管机构
     * @return 登记托管机构
     */
    public String getRegistOrg() {
        return registOrg;
    }

    /**
     * 设置 登记托管机构
     * @param registOrg 登记托管机构
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg == null ? null : registOrg.trim();
    }

    /**
     * 取得 含权债摊销日
     * @return 含权债摊销日
     */
    public String getExeDate() {
        return exeDate;
    }

    /**
     * 设置 含权债摊销日
     * @param exeDate 含权债摊销日
     */
    public void setExeDate(String exeDate) {
        this.exeDate = exeDate == null ? null : exeDate.trim();
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 合同编号
     * @return 合同编号
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * 设置 合同编号
     * @param contractNo 合同编号
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    /**
     * 取得 是否金融机构发行
     * @return 是否金融机构发行
     */
    public String getIsFinOrg() {
        return isFinOrg;
    }

    /**
     * 设置 是否金融机构发行
     * @param isFinOrg 是否金融机构发行
     */
    public void setIsFinOrg(String isFinOrg) {
        this.isFinOrg = isFinOrg == null ? null : isFinOrg.trim();
    }

    /**
     * 取得 担保人
     * @return 担保人
     */
    public String getSponsor() {
        return sponsor;
    }

    /**
     * 设置 担保人
     * @param sponsor 担保人
     */
    public void setSponsor(String sponsor) {
        this.sponsor = sponsor == null ? null : sponsor.trim();
    }

    /**
     * 取得 投资顾问
     * @return 投资顾问
     */
    public String getInvestAdviser() {
        return investAdviser;
    }

    /**
     * 设置 投资顾问
     * @param investAdviser 投资顾问
     */
    public void setInvestAdviser(String investAdviser) {
        this.investAdviser = investAdviser == null ? null : investAdviser.trim();
    }

    /**
     * 取得 是否轧差清算
     * @return 是否轧差清算
     */
    public String getLiquidationYesno() {
        return liquidationYesno;
    }

    /**
     * 设置 是否轧差清算
     * @param liquidationYesno 是否轧差清算
     */
    public void setLiquidationYesno(String liquidationYesno) {
        this.liquidationYesno = liquidationYesno == null ? null : liquidationYesno.trim();
    }
    /**
     * 认购起始日
     * @return 认购起始日
     */
    public Date getToffStrdate() {
        return toffStrdate;
    }

    /**
     * 认购起始日
     */
    public void setToffStrdate(Date toffStrdate) {
        this.toffStrdate = toffStrdate;
    }

    /**
     * 认购终止日
     * @return 认购终止日
     */
    public Date getToffEnddate() {
        return toffEnddate;
    }

    /**
     * 认购终止日
     */
    public void setToffEnddate(Date toffEnddate) {
        this.toffEnddate = toffEnddate;
    }

    /**
     * 取得 SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过
     * @return SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过
     */
    public String getSppiTestStatus() {
        return sppiTestStatus;
    }

    /**
     * 设置 SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过
     * @param sppiTestStatus SPPI测试状态：0未测试、1测试中、2测试通过、3测试不通过
     */
    public void setSppiTestStatus(String sppiTestStatus) {
        this.sppiTestStatus = sppiTestStatus == null ? null : sppiTestStatus.trim();
    }

    /**
     * 取得 上市流通代码
     * @return 上市流通代码
     */
    public String getCirculationCode() {
        return circulationCode;
    }

    /**
     * 设置 上市流通代码
     * @param circulationCode 上市流通代码
     */
    public void setCirculationCode(String circulationCode) {
        this.circulationCode = circulationCode == null ? null : circulationCode.trim();
    }

    /**
     * 市场代码
     * {@inheritDoc}
     */
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId(){
        return this.finprodMarketId;
    }

    public FinProductAddDto getProductAddDto() {
        return productAddDto;
    }

    public void setProductAddDto(FinProductAddDto productAddDto) {
        this.productAddDto = productAddDto;
    }

    public FinProductIssueDto getIssueDto() {
        return issueDto;
    }

    public void setIssueDto(FinProductIssueDto issueDto) {
        this.issueDto = issueDto;
    }

    public List<MstBondCreditRatingDto> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<MstBondCreditRatingDto> ratingList) {
        this.ratingList = ratingList;
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

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getType10() {
        return type10;
    }

    public void setType10(String type10) {
        this.type10 = type10;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getTradeMarketValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.tradeMarket);
    }

    public void setTradeMarketValue(String tradeMarketValue) {
        this.tradeMarketValue = tradeMarketValue;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getRegistOrgValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.registOrg);
    }

    public void setRegistOrgValue(String registOrgValue) {
        this.registOrgValue = registOrgValue;
    }

    public String getCalModel() {
        return calModel;
    }

    public void setCalModel(String calModel) {
        this.calModel = calModel;
    }

    public Date getCirculationDate() {
        return circulationDate;
    }

    public void setCirculationDate(Date circulationDate) {
        this.circulationDate = circulationDate;
    }
}
