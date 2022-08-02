/*
 * 文件名：EtrBondFirstLBidDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrconbond.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrconbond.validator.EtrConBondDtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 可转债委托dto 〈功能详细描述〉
 *
 * @author gejiaao
 * @version 1.0 2019年10月23日
 * @see EtrConBondDto
 * @since 1.0
 */
@PageId("EtrConBond002")
@ValidLogic(targetClass = EtrConBondDtoValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class EtrConBondDto extends EtrEntrust implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -7300212795949457812L;

    /**
     * 按钮id
     */
    private String elementId;

    /**
     * 预计委托可用现金
     */
    private BigDecimal expectAvailableCash;

    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;
    /**
     * 市场代码
     */
    private String finprodMarketId;

    //add by gaopeng 20200102 start
    /**
     * 序号
     */
    private String indexId;

    /**
     * 证券托管户名称
     */
    private String secAcctName;

    /**
     * 通道名称
     */
    private String chlIdName;
    //add by gaopeng 20200102 end
    /**
     * 投资人员信息DTO
     */
    @Valid
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    /**
     * 接收状态
     */
    private String recStatus;

    /**
     * 中标状态
     */
    private String isWin;

    /**
     * 指定交易员
     */
    private String trader;

    /**
     * 委托关闭方式
     */
    private String CloseWay;
    /**
     * 是否多对多导入指令
     */
    private boolean importFlow;

    /**
     * 选中的数据
     */
    private transient List<EtrConBondDto> selectedItem;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 组合名称
     */
    private String portfolioName;


    /**
     * 金融产品全称
     */
    private String finprodName;


    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;
    /**
     * 缴款方式
     */
    private String etrPayType;

    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 资产起息日
     */
    private Date finvdate;

    /**
     * 资产到期日
     */
    private Date finmdate;

    /**
     * 申购日期
     */
    private Date purchDate;

    /**
     * 申购日期Text
     */
    private String purchDateText;

    /**
     * 申购方式
     */
    private String purchModle;
    /**
     * 申购类型
     */
    private String purchType;

    /**
     * 申购方式
     */
    private String purchModleStr;
    /**
     * 申购类型
     */
    private String purchTypeStr;
    /**
     * 申购券面总额
     */
    private BigDecimal purchBondTotal;
    /**
     * 中签券面总额
     */
    private BigDecimal winlotteryAmt;
    /**
     * 交易所资金账户可用现金
     */
    private BigDecimal tradeMarketCash;
    /**
     * 通道
     */
    private String chlId;
    /**
     * 证券托管户
     */
    private String secAcctId;
    /**
     * 通道可用现金
     */
    private BigDecimal channelAvailableCash;
    /**
     * 债券一级类型
     */
    private String assetType1;

    private String busiTypePosition;


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
    @NotEmpty(fieldName = "EtrInvestAllInfo001.finprodId", groups = {ValidateGroupEdit.class})
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
     * 获取 etrInvestPersonInfo
     * @return etrInvestPersonInfo.
     */
    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    /**
     * 设置 etrInvestPersonInfo
     * @param etrInvestPersonInfo etrInvestPersonInfo
     */
    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }

    /**
     * 获取 recStatus
     * @return recStatus.
     */
    public String getRecStatus() {
        return recStatus;
    }

    /**
     * 设置 recStatus
     * @param recStatus recStatus
     */
    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    /**
     * 获取 trader
     * @return trader.
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 trader
     * @param trader trader
     */
    public void setTrader(String trader) {
        this.trader = trader;
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
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<EtrConBondDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<EtrConBondDto> selectedItem) {
        this.selectedItem = selectedItem;
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
     * 获取 finprodType2
     * @return finprodType2.
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 finprodType2
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }
    /**
     * 获取 bidDate
     * @return bidDate.
     */
    @Override
    @NotEmpty(fieldName = "EtrConBond001.purchDate", groups = {ValidateGroupEdit.class})
    public Date getEntrustDate() {
        return super.getEntrustDate();
    }

    /**
     * 获取 etrPayType
     * @return etrPayType.
     */
    public String getEtrPayType() {
        return etrPayType;
    }
    /**
     * 设置 etrPayType
     * @param etrPayType etrPayType
     */
    public void setEtrPayType(String etrPayType) {
        this.etrPayType = etrPayType;
    }
    /**
     * 获取 expectAvailableCash
     * @return expectAvailableCash.
     */
    public BigDecimal getExpectAvailableCash() {
        return expectAvailableCash;
    }
    /**
     * 设置 expectAvailableCash
     * @param expectAvailableCash expectAvailableCash
     */
    public void setExpectAvailableCash(BigDecimal expectAvailableCash) {
        this.expectAvailableCash = expectAvailableCash;
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
     * 获取 finvdate
     * @return finvdate.
     */
    public Date getFinvdate() {
        return finvdate;
    }
    /**
     * 设置 finvdate
     * @param finvdate finvdate
     */
    public void setFinvdate(Date finvdate) {
        this.finvdate = finvdate;
    }
    /**
     * 获取 finmdate
     * @return finmdate.
     */
    public Date getFinmdate() {
        return finmdate;
    }
    /**
     * 设置 finmdate
     * @param finmdate finmdate
     */
    public void setFinmdate(Date finmdate) {
        this.finmdate = finmdate;
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }
    /**
     *
     * 取得组合代码
     */
    @Override
    @NotEmpty(fieldName = "EtrInvestBondInfo002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    @Override
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    @Override
    public Date getMdate() {
        return super.getMdate();
    }

    /**
     * 取得 币种
     * @return 币种
     */
    @Override
    @NotEmpty(fieldName = "EtrInvestBondInfo002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 取得 投资目的
     * @return 投资目的
     */
    @Override
    @NotEmpty(fieldName = "EtrInvestBondInfo002.invAim", groups = {ValidateGroupEdit.class})
    public String getInvAim() {
        return super.getInvAim();
    }
    /**
     * 取得 交易场所
     * @return 交易场所
     */
    @Override
    @NotEmpty(fieldName = "EtrInvestAllInfo001.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket() {
        return super.getTradeMarket();
    }

    /**
     *
     * 设置委托状态数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusEntrustStatus(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态, super.getEntrustStatus());
    }
    /**
     *
     * 设置币种数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusCcy(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, super.getCcy());

    }
    /**
     *
     * 设置交易场所数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusTradeMarket(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, super.getTradeMarket());
    }
    /**
     *
     * 设置交易平台数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusTradePlat(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易平台, super.getTradePlat());
    }

    /**
     * 设置中标状态数据字典
     *
     * @return
     * @see
     * @since 1.0
     */
    public String getBusIsWin() {
        if (!StringUtil.isEmpty(this.getIsWin())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.中标状态, this.getIsWin());
        }
        return null;
    }

    /**
     * 获取 isWin
     * @return isWin.
     */
    public String getIsWin() {
        return isWin;
    }

    /**
     * 设置 isWin
     * @param isWin isWin
     */
    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }

    /**
     * 缴款方式数据字典
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusEtrPayType() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.缴款方式, this.getEtrPayType());
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
    public Date getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }
    /* @NotEmpty(fieldName = "EtrConBond001.purchModle", groups = {ValidateGroupEdit.class})*/
    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }
    @NotEmpty(fieldName = "EtrConBond001.purchType", groups = {ValidateGroupEdit.class})
    public String getPurchType() {
        return purchType;
    }

    public void setPurchType(String purchType) {
        this.purchType = purchType;
    }

    public String getPurchModleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.申购方式, this.getPurchModle());
    }

    public void setPurchModleStr(String purchModleStr) {
        this.purchModleStr = purchModleStr;
    }

    public String getPurchTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.申购类型, this.getPurchType());
    }

    public void setPurchTypeStr(String purchTypeStr) {
        this.purchTypeStr = purchTypeStr;
    }
    @NotEmpty(fieldName = "EtrConBond001.purchBondTotal", groups = {ValidateGroupEdit.class})
    public BigDecimal getPurchBondTotal() {
        return purchBondTotal;
    }

    public void setPurchBondTotal(BigDecimal purchBondTotal) {
        this.purchBondTotal = purchBondTotal;
    }

    public BigDecimal getTradeMarketCash() {
        return tradeMarketCash;
    }

    public void setTradeMarketCash(BigDecimal tradeMarketCash) {
        this.tradeMarketCash = tradeMarketCash;
    }

    public BigDecimal getChannelAvailableCash() {
        return channelAvailableCash;
    }

    public void setChannelAvailableCash(BigDecimal channelAvailableCash) {
        this.channelAvailableCash = channelAvailableCash;
    }

    @Override
    public String getChlId() {
        return chlId;
    }

    @Override
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public BigDecimal getWinlotteryAmt() {
        return winlotteryAmt;
    }

    public void setWinlotteryAmt(BigDecimal winlotteryAmt) {
        this.winlotteryAmt = winlotteryAmt;
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
     * 获取 secAcctName
     * @return secAcctName.
     */
    public String getSecAcctName() {
        return secAcctName;
    }

    /**
     * 设置 secAcctName
     * @param secAcctName secAcctName
     */
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
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
     * 获取 purchDateText
     * @return purchDateText.
     */
    public String getPurchDateText() {
        purchDateText = DateUtil.format(this.getPurchDate());
        return this.purchDateText;
    }

    /**
     * 获取 tradeMarketName
     *
     * @return tradeMarketName.
     */
    public String getTradeMarketName() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    /**
     * 获取 投资目的名称
     *
     * @return invAimName 投资目的名称
     */
    public String getInvAimName() {
        return SysDataUtil.getNameFromDataDict(DictCode.投资目的, getInvAim());
    }

    /**
     * 获取 币种名称
     *
     * @return ccyName 币种名称
     */
    public String getCcyName() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    /**
     * 获取 申购方式名称
     *
     * @return purchModleName 申购方式名称
     */
    public String getPurchModleName() {
        return SysDataUtil.getNameFromDataDict(DictCode.申购方式, getPurchModle());
    }

    /**
     * 获取 申购类型名称
     *
     * @return purchTypeName 申购类型名称
     */
    public String getPurchTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.申购类型, getPurchType());
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
