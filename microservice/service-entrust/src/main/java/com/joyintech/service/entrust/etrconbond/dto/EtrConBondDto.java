/*
 * 文件名：EtrBondFirstLBidDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etrconbond.dto;

import com.joyintech.service.entrust.etrbondfirstlbidding.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 可转债委托dto 〈功能详细描述〉
 *
 * @author gejiaao
 * @version 1.0 2019年10月24日
 * @see EtrConBondDto
 * @since 1.0
 */
public class EtrConBondDto extends EtrEntrust {


    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -5106366412674252223L;

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
     * 指定交易员
     */
    private String trader;

    /**
     * 委托关闭方式
     */
    private String CloseWay;

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
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 申购日期
     */
    private Date purchDate;
    /**
     * 申购方式
     */
    private String purchModle;
    /**
     * 申购类型
     */
    private String purchType;
    /**
     * 申购券面总额
     */
    private BigDecimal purchBondTotal;
    /**
     * 中签券面总额
     */
    private BigDecimal winlotteryAmt;
    /**
     * 债券一级分类
     */
    private String assetType1;
    /**
     * 证券账户
     */
    private String secAcctId;
    /**
     * 通道
     */
    private String chlId;
    /**
     * 交易所资金账户可用现金
     */
    private BigDecimal tradeMarketCash;
    /**
     * 通道可用现金
     */
    private BigDecimal channelAvailableCash;

    /**
     * 软控是否继续？Y：继续
     */
    private String softSkipMode;
    /**
     * 头寸使用序列号
     */
    private Long positionSeqno;

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

    public Date getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    public String getPurchModle() {
        return purchModle;
    }

    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle;
    }

    public String getPurchType() {
        return purchType;
    }

    public void setPurchType(String purchType) {
        this.purchType = purchType;
    }

    public BigDecimal getPurchBondTotal() {
        return purchBondTotal;
    }

    public void setPurchBondTotal(BigDecimal purchBondTotal) {
        this.purchBondTotal = purchBondTotal;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    @Override
    public String getChlId() {
        return chlId;
    }

    @Override
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public BigDecimal getWinlotteryAmt() {
        return winlotteryAmt;
    }

    public void setWinlotteryAmt(BigDecimal winlotteryAmt) {
        this.winlotteryAmt = winlotteryAmt;
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

    public Long getPositionSeqno() {
        return positionSeqno;
    }

    public void setPositionSeqno(Long positionSeqno) {
        this.positionSeqno = positionSeqno;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }
}
