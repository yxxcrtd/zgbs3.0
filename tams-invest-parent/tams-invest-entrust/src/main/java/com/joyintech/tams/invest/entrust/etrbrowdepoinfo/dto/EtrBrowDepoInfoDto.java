/*
 * 文件名：EtrBrowDepoInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Amount;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.Rate;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.validator.EtrBrowDepoInfoValidator;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 存款委托DTO
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年9月13日
 * @see EtrBrowDepoInfoDto
 * @since 1.0
 */
@PageId("EtrBrowDepoInfo002")
@ValidLogic(targetClass = EtrBrowDepoInfoValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class EtrBrowDepoInfoDto extends EtrEntrust implements ProcessEntity{

    /**
     * 序号
     */
    private String indexId;

    /**
     * 存款资产本金
     */
    private BigDecimal iusseAmt;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 页面提交模式
     */
    private String pageModel;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1164891656947671931L;

    /**
     * 选中的数据
     */
    private transient List<EtrBrowDepoInfoDto> selectedItem;

    /**
     * 委托关闭方式
     */
    private String CloseWay;

    /**
     * 期限天数，到期日-起息日
     */
    private Long termDays;

    //组合名称
    private String portfolioName;

    //资产类型
    private String finprodType2;

    //资产代码
    private String finprodMarketId;

    //资产名称
    private String finprodName;

    //付息频率
    private  String payFreq;

    /**
     * 交易对手类型
     */
    private  String counterType;

    //已完成额度
    private long finishAmt;

    //交易完成度
    private long transactioncom;

    //起息日
    private Date dateStart;

    //到期日
    private Date dateEnd;

    //资产可用持仓
    private BigDecimal assetAvailablePosition;

    //投资人员信息
    @Valid
    private BussEtrInvestPersonInfoDto bussEtrInvestPersonInfoDto;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**新增资产二、三、四级分类	王贵奇	2019/7/30 开始*/

    /**
     * 资产二级类型
     */
    private String assetType1;

    /**
     * 资产三级类型
     */
    private String assetType10;

    /**
     * 通道名称
     */
    private String chlIdName;

    /**
     * 资产四级类型
     */
    private String assetType11;

    /**
     * 是否终止协议
     */
    private String isAgreementEnd;


    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType10() {
        return assetType10;
    }

    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }

    public String getAssetType1Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, getAssetType1());
    }

    public String getAssetType10Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, getAssetType10());
    }

    public String getAssetType11Str(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getAssetType11());
    }
    /**新增资产二、三、四级分类	王贵奇	2019/7/30 结束*/

    public BigDecimal getIusseAmt() {
        return iusseAmt;
    }

    public void setIusseAmt(BigDecimal iusseAmt) {
        this.iusseAmt = iusseAmt;
    }

    /**
     * 获取 riskTrialRetDto
     * @return riskTrialRetDto.
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }


    /**
     * 设置 riskTrialRetDto
     * @param riskTrialRetDto riskTrialRetDto
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }


    /**
     *
     */
    private List<SysUploadFileInfo> fileArray;

    @PrimaryKey(value=Field.PK_1)
    public String getEntrustId(){
        return super.getEntrustId();
    }

    public String getEntrustStatusStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }

    public String getCcyStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }

    public String getPsStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }

    public String getPayFreqStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.频率, getPayFreq());
    }

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    public String getVdateStr() {
        return DateUtil.format(super.getVdate());
    }

    public String getDateStartStr() {
        return DateUtil.format(this.getDateStart());
    }

    public String getDateEndStr() {
        return DateUtil.format(this.getDateEnd());
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return super.getMdate();
    }

    public String getMdateStr() {
        return DateUtil.format(super.getMdate());
    }

    /**
     * 取得 币种
     * @return 币种
     */
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    @Length(fieldName = "EtrBrowDepoInfo001.ps", max = 30, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return super.getPs();
    }

    /**
     * 取得 委托额度，交易金额
     * @return 委托额度，交易金额
     */
    @Amount(fieldName = "EtrBrowDepoInfo001.entrustAmt", groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.entrustAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustAmt() {
        return super.getEntrustAmt();
    }

    public String getEntrustAmtStr(){
        return super.getEntrustAmt() != null ? super.getEntrustAmt().toString():null;
    }

    /**
     * 取得 委托价格，净价、利率等
     * @return 委托价格，净价、利率等
     */
    public BigDecimal getPrice() {
        return super.getPrice();
    }

    /**
     * 获取 portfolioName
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }


    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }


    /**
     * 获取 finprodType2
     * @return finprodType2.
     */
    @Length(fieldName = "EtrBrowDepoInfo001.finprodType2", max = 30, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "EtrBrowDepoInfo001.finprodType2", groups = {ValidateGroupEdit.class})
    public String getFinprodType2() {
        return finprodType2;
    }


    public String getFinprodType2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.存款类资产类型, this.getFinprodType2());
    }

    /**
     * 设置 finprodType2资产类型
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }


    /**
     * 获取 finprodId资产代码
     * @return finprodId.
     */
    @Length(fieldName = "EtrBrowDepoInfo001.finprodId", max = 30, groups = {ValidateGroupEdit.class})
    //@NotEmpty(fieldName = "EtrBrowDepoInfo001.finprodId", groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }



    /**
     * 获取 finprodName
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }


    /**
     * 设置 finprodName
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }


    /**
     * 获取 payFreq
     * @return payFreq.
     */
    public String getPayFreq() {
        return payFreq;
    }


    /**
     * 设置 payFreq
     * @param payFreq payFreq
     */
    public void setPayFreq(String payFreq) {
        this.payFreq = payFreq;
    }


    /**
     * 获取 finishAmt
     * @return finishAmt.
     */
    public long getFinishAmt() {
        return finishAmt;
    }


    /**
     * 设置 finishAmt
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(long finishAmt) {
        this.finishAmt = finishAmt;
    }


    /**
     * 获取 transactioncom
     * @return transactioncom.
     */
    public long getTransactioncom() {
        return transactioncom;
    }


    /**
     * 设置 transactioncom
     * @param transactioncom transactioncom
     */
    public void setTransactioncom(long transactioncom) {
        this.transactioncom = transactioncom;
    }


    /**
     * 获取 serialversionuid
     * @return serialversionuid.
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }



    /**
     * 获取 operationType
     * @return operationType.
     */
    public String getOperateType() {
        return operateType;
    }



    /**
     * 设置 operationType
     * @param operationType operationType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }


    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }


    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }


    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<EtrBrowDepoInfoDto> getSelectedItem() {
        return selectedItem;
    }


    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<EtrBrowDepoInfoDto> selectedItem) {
        this.selectedItem = selectedItem;
    }


    /**
     * 获取 dateStart
     * @return dateStart.
     */
    //@NotEmpty(fieldName = "EtrBrowDepoInfo001.vdate", groups = {ValidateGroupEdit.class})
    public Date getDateStart() {
        return dateStart;
    }


    /**
     * 设置 dateStart
     * @param dateStart dateStart
     */
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }


    /**
     * 获取 dateEnd
     * @return dateEnd.
     */
    //@NotEmpty(fieldName = "EtrBrowDepoInfo001.mdate", groups = {ValidateGroupEdit.class})
    public Date getDateEnd() {
        return dateEnd;
    }


    /**
     * 设置 dateEnd
     * @param dateEnd dateEnd
     */
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }


    /**
     * 获取 closeWay
     * @return closeWay.
     */
    public String getCloseWay() {
        return CloseWay;
    }

    /**
     * 设置 closeWay
     * @param closeWay closeWay
     */
    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }


    /**
     * 获取 termDays
     * @return termDays.
     */
    //@NotEmpty(fieldName = "EtrBrowDepoInfo002.termDays", groups = {ValidateGroupEdit.class})
    public Long getTermDays() {
        return termDays;
    }


    /**
     * 设置 termDays
     * @param termDays termDays
     */
    public void setTermDays(Long termDays) {
        this.termDays = termDays;
    }


    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }


    /**
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }


    /**
     * 获取 bussEtrInvestPersonInfoDto
     * @return bussEtrInvestPersonInfoDto.
     */
    public BussEtrInvestPersonInfoDto getBussEtrInvestPersonInfoDto() {
        return bussEtrInvestPersonInfoDto;
    }


    /**
     * 设置 bussEtrInvestPersonInfoDto
     * @param bussEtrInvestPersonInfoDto bussEtrInvestPersonInfoDto
     */
    public void setBussEtrInvestPersonInfoDto(BussEtrInvestPersonInfoDto bussEtrInvestPersonInfoDto) {
        this.bussEtrInvestPersonInfoDto = bussEtrInvestPersonInfoDto;
    }


    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }


    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }


    /**
     * 获取 finprodMarketId
     * @return finprodMarketId.
     */
    @NotEmpty(fieldName = "EtrBrowDepoInfo001.finprodId", groups = {ValidateGroupEdit.class})
    public String getFinprodMarketId() {
        return finprodMarketId;
    }


    /**
     * 设置 finprodMarketId
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }


    /**
     * 获取 assetAvailablePosition
     * @return assetAvailablePosition.
     */
    public BigDecimal getAssetAvailablePosition() {
        return assetAvailablePosition;
    }


    /**
     * 设置 assetAvailablePosition
     * @param assetAvailablePosition assetAvailablePosition
     */
    public void setAssetAvailablePosition(BigDecimal assetAvailablePosition) {
        this.assetAvailablePosition = assetAvailablePosition;
    }

    /**
     * 获取 counterType
     *
     * @return counterType
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 counterType
     *
     * @param counterType counterType
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getIsAgreementEnd() {
        return isAgreementEnd;
    }

    public void setIsAgreementEnd(String isAgreementEnd) {
        this.isAgreementEnd = isAgreementEnd;
    }

    /**
     * 获取 indexId
     * @return indexId.
     */
    public String getIndexId() {
        return indexId;
    }

    /**
     * 设置 indexId
     * @param indexId indexId
     */
    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    /**
     * 获取 chlIdName
     * @return chlIdName.
     */
    public String getChlIdName() {
        return chlIdName;
    }

    /**
     * 设置 chlIdName
     * @param chlIdName chlIdName
     */
    public void setChlIdName(String chlIdName) {
        this.chlIdName = chlIdName;
    }

    /**
     * 获取 finprodAbbr
     * @return finprodAbbr.
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * 设置 finprodAbbr
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    /**
     * 当前处理人
     */
    private String nextUser;

    public String getNextUser() {
        return nextUser;
    }

    public void setNextUser(String nextUser) {
        this.nextUser = nextUser;
    }
}
