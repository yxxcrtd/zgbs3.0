/*
 * 文件名：EtrFundDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrfund.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.validation.annotation.Amount;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrfund.validator.EtrFundValidator;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 基金委托Dto
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年10月8日
 * @see EtrFundDto
 * @since 1.0
 */
@PageId("EtrFund002")
@ValidLogic(targetClass = EtrFundValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class EtrFundDto extends EtrEntrust implements ProcessEntity{

    /**
     * 页面提交模式
     */
    private String pageModel;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;

    /**
     * 选中的数据
     */
    private transient List<EtrFundDto> selectedItem;

    /**
     * 委托关闭方式
     */
    private String CloseWay;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 委托操作类型
     */
    private String entrustOperationType;

    /**
     * 投资人员Dto
     */
    @Valid
    private BussEtrInvestPersonInfoDto bussEtrInvestPersonInfoDto;

    /**
     *
     */
    private List<SysUploadFileInfo> fileArray;

    //资产代码
    private String finprodMarketId;

    //基金类型
    private String assetType1;

    /**新增个性分类三、四	王贵奇	2019/7/29	开始 **/
    //个性分类三
    private String assetType10;

    //个性分类四
    private String assetType11;
    /**新增个性分类三、四	王贵奇	2019/7/29	结束 **/

    //资产名称
    private String finprodName;

    //组合名称
    private String portfolioName;

    //已完成额度
    @JsonSerialize(using=BigDecimalSerializer.class)
    @Amount(fieldName = "EtrFund001.finishAmt", groups = {ValidateGroupEdit.class})
    private BigDecimal finishAmt;

    //交易完成度
    private long transactioncom;
    /**
     * 多对多导入
     */
    private boolean importFlow;
    //资产可用持仓
    private BigDecimal assetAvailablePosition;
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 交易对手类型
     */
    private String counterType;

    //add by lishuping 20191029 start
    /**
     * 资产简称
     */
    private String finprodAbbr;
    /**
     * 证券账户
     */
    private String secAcctId;
    /**
     * 交易所基金简称
     */
    private String trmFundAbbr;
    /**
     * 交易所基金类型
     */
    private String trmFundType;
    // 添加委托列表所需字段 by 赵东东 20190109 start
    /**
     * 序号
     */
    private String indexId;
    /**
     * 交易所资金账户可用现金
     */
    private BigDecimal tradeMarketCash;
    /**
     * 通道可用现金
     */
    private BigDecimal chlIdCash;


    /**
     * 组合T+1可用现金,用于质押式回购指令还是买断式回购指令
     */

    private BigDecimal appProAvailableCash1;


    /**
     * 父页面的parentPageId，用于区分字典值控制字段
     */

    private String parentPageId;

    public String getParentPageId() {
        return parentPageId;
    }

    public void setParentPageId(String parentPageId) {
        this.parentPageId = parentPageId;
    }

    /**
     *
     * 获取交易对手名称
     * @return
     * @see
     * @since 1.0
     */
    public String getCounterIdName() {
        return counterIdName;
    }
    /**
     *
     * 设置交易对手名称
     * <功能详细描述>
     * @param counterIdName
     * @see
     * @since 1.0
     */
    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }
    /**
     *
     * 获取证券账户名称
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getSecAcctIdName() {
        return secAcctIdName;
    }
    /**
     *
     * 设置证券账户名称
     * <功能详细描述>
     * @param secAcctIdName
     * @see
     * @since 1.0
     */
    public void setSecAcctIdName(String secAcctIdName) {
        this.secAcctIdName = secAcctIdName;
    }

    /**
     * 转入金融产品代码
     */
    private String rFinprodId;

    /**
     * 转入资产代码
     */
    private String changeToFinprodMarketId;

    /**
     * 转入资产名称
     */
    private String changeToFinprodName;

    /**
     * 通道名称
     */
    private String chlIdName;
    /**
     * 交易对手名称
     */
    private String counterIdName;
    /**
     * 证券账户名称
     */
    private String secAcctIdName;
    /**
     *
     * 获取通道名称
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getChlIdName() {
        return chlIdName;
    }
    /**
     *
     * 设置通道名称
     * <功能详细描述>
     * @param chlIdName
     * @see
     * @since 1.0
     */
    public void setChlIdName(String chlIdName) {
        this.chlIdName = chlIdName;
    }
    /**
     *
     * 获取通道可用现金的值
     * <功能详细描述>
     * @return chlIdCash
     * @see
     * @since 1.0
     */
    public BigDecimal getChlIdCash() {
        return chlIdCash;
    }
    /**
     *
     * 设置通道可用现金的值
     * <功能详细描述>
     * @param chlIdCash
     * @see
     * @since 1.0
     */
    public void setChlIdCash(BigDecimal chlIdCash) {
        this.chlIdCash = chlIdCash;
    }
    /**
     *
     * 获取交易所资金账户可用现金
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getTradeMarketCash() {
        return tradeMarketCash;
    }
    /**
     *
     * 设置交易所资金账户可用现金
     * <功能详细描述>
     * @param tradeMarketCash
     * @see
     * @since 1.0
     */
    public void setTradeMarketCash(BigDecimal tradeMarketCash) {
        this.tradeMarketCash = tradeMarketCash;
    }
    /**
     *
     * 获取序号
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getIndexId() {
        return indexId;
    }
    /**
     *
     * 设置序号
     * <功能详细描述>
     * @param indexId
     * @see
     * @since 1.0
     */

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }
    // 添加委托列表所需字段 by 赵东东 20190109 end
    /**
     * finprodAbbr
     * @return finprodAbbr
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * finprodAbbr
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
    //add by lishuping 20191029 end

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

    @PrimaryKey(value=Field.PK_1)
    public String getEntrustId(){
        return super.getEntrustId();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    @Length(fieldName = "EtrFund001.ps", max = 30, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "EtrFund001.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return super.getPs();
    }

    @NotEmpty(fieldName = "EtrFund001.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId(){
        return super.getPortfolioId();
    }

    public String getFinprodId(){
        return super.getFinprodId();
    }

    public String getEntrustAmtStr(){
        if(StringUtil.isNotEmpty(super.getEntrustAmt())){
            return super.getEntrustAmt().toString();
        }
        else{
            return null;
        }
    }
    public String getEntrustAmtOrNumStr(){
        if(StringUtil.isNotEmpty(super.getEntrustAmt())){
            return super.getEntrustAmt().toString();
        }
        else if(StringUtil.isNotEmpty(super.getEntrustNum())){
            return super.getEntrustNum().toString();
        }
        else{
            return null;
        }
    }
    public String getEntrustNumStr(){
        if(StringUtil.isNotEmpty(super.getEntrustNum())){
            return super.getEntrustNum().toString();
        }
        else{
            return null;
        }
    }
    //@NotEmpty(fieldName = "EtrFund001.entrustAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustAmt(){
        return super.getEntrustAmt();
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    @NotEmpty(fieldName = "EtrFund001.entrustMDate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return super.getMdate();
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    @NotEmpty(fieldName = "EtrFund001.entrustVDate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * <功能详细描述>
     * @return
     */
    public String getVdateStr(){
        return DateUtil.format(super.getVdate());
    }

    /**
     * <功能详细描述>
     * @return
     */
    public String getMdateStr(){
        return DateUtil.format(super.getMdate());
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

    public String getAssetType1Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.基金类资产类型, this.getAssetType1());
    }

    public String getTrmFundTypeStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易所基金类型, this.getTrmFundType());
    }

    public String getTradeMarketStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }

    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }


    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
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
     * 获取 finishAmt
     * @return finishAmt.
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }


    /**
     * 设置 finishAmt
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(BigDecimal finishAmt) {
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
    public List<EtrFundDto> getSelectedItem() {
        return selectedItem;
    }



    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<EtrFundDto> selectedItem) {
        this.selectedItem = selectedItem;
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
     * 获取 entrustOperationType
     * @return entrustOperationType.
     */
    public String getEntrustOperationType() {
        return entrustOperationType;
    }



    /**
     * 设置 entrustOperationType
     * @param entrustOperationType entrustOperationType
     */
    public void setEntrustOperationType(String entrustOperationType) {
        this.entrustOperationType = entrustOperationType;
    }


    /**
     * 获取 assetType1
     * @return assetType1.
     */
    @NotEmpty(fieldName = "EtrFund001.finprodType2", groups = {ValidateGroupEdit.class})
    public String getAssetType1() {
        return assetType1;
    }


    /**
     * 设置 assetType1
     * @param assetType1 assetType1
     */
    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }


    /**新增个性分类三、四	王贵奇	2019/7/29	开始**/
    public String getAssetType1Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, getAssetType1());
    }

    /**
     * 获取 assetType10
     * @return assetType10.
     */
    @NotEmpty(fieldName = "EtrInvestBondInfo001.type10", groups = {ValidateGroupEdit.class})
    public String getAssetType10() {
        return assetType10;
    }

    public String getAssetType10Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, getAssetType10());
    }

    /**
     * 设置 assetType10
     * @param assetType10 assetType10
     */
    public void setAssetType10(String assetType10) {
        this.assetType10 = assetType10;
    }
    /**
     * 获取 assetType11
     * @return assetType11.
     */
    //@NotEmpty(fieldName = "EtrInvestBondInfo001.type11", groups = {ValidateGroupEdit.class})
    public String getAssetType11() {
        return assetType11;
    }
    public String getAssetType11Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getAssetType11());
    }

    /**
     * 设置 assetType11
     * @param assetType11 assetType11
     */
    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }
    /**新增个性分类三、四	王贵奇	2019/7/29	结束**/

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
    @NotEmpty(fieldName = "EtrFund001.finprodId", groups = {ValidateGroupEdit.class})
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

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getTrmFundAbbr() {
        return trmFundAbbr;
    }

    public void setTrmFundAbbr(String trmFundAbbr) {
        this.trmFundAbbr = trmFundAbbr;
    }

    public String getTrmFundType() {
        return trmFundType;
    }

    public void setTrmFundType(String trmFundType) {
        this.trmFundType = trmFundType;
    }

    /**
     * 获取 changeToFinprodMarketId
     *
     * @return changeToFinprodMarketId
     */
    public String getChangeToFinprodMarketId() {
        return changeToFinprodMarketId;
    }

    /**
     * 设置 changeToFinprodMarketId
     *
     * @param changeToFinprodMarketId changeToFinprodMarketId
     */
    public void setChangeToFinprodMarketId(String changeToFinprodMarketId) {
        this.changeToFinprodMarketId = changeToFinprodMarketId;
    }

    /**
     * 获取 changeToFinprodName
     *
     * @return changeToFinprodName
     */
    public String getChangeToFinprodName() {
        return changeToFinprodName;
    }

    /**
     * 设置 changeToFinprodName
     *
     * @param changeToFinprodName changeToFinprodName
     */
    public void setChangeToFinprodName(String changeToFinprodName) {
        this.changeToFinprodName = changeToFinprodName;
    }

    /**
     * 获取 rFinprodId
     *
     * @return rFinprodId
     */
    public String getrFinprodId() {
        return rFinprodId;
    }

    /**
     * 设置 rFinprodId
     *
     * @param rFinprodId rFinprodId
     */
    public void setrFinprodId(String rFinprodId) {
        this.rFinprodId = rFinprodId;
    }
    /**
     * 获取 importFlow
     *
     * @return importFlow
     */
    public boolean isImportFlow() {
        return importFlow;
    }

    /**
     * 设置 importFlow
     *
     * @param importFlow importFlow
     */
    public void setImportFlow(boolean importFlow) {
        this.importFlow = importFlow;
    }

    public BigDecimal getAppProAvailableCash1() {
        return appProAvailableCash1;
    }
    public void setAppProAvailableCash1(BigDecimal appProAvailableCash1) {
        this.appProAvailableCash1 = appProAvailableCash1;
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
