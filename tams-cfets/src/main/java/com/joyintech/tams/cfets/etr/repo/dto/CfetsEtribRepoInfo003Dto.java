/*
 * 文件名：EtribRepoInfo003Dto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月20日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 王波
 * @date 2021年01月20日
 * @see CfetsEtribRepoInfo003Dto
 * @since 1.0
 */
@ApiModel(value="质押式回购指令提交类",description="质押式回购指令提交类")
public class CfetsEtribRepoInfo003Dto extends EtrEntrust implements ProcessEntity {

    /**
     * 按钮id
     */
    @ApiModelProperty(name="elementId",value="按钮id")
    private String elementId;

    @ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateType",value="操作类型")
    private String operateType;

    /**
     * 劵面总额
     */
    @ApiModelProperty(name="tradeAmt",value="劵面总额")
    private BigDecimal tradeAmt;
    
    /**
     * 质押券库
     */
    @ApiModelProperty(name="zyqk",value="质押券库")
    private String zyqk;

    /**
     * 质押券库
     */
    @ApiModelProperty(name="zyqkStr",value="质押券库")
    private String zyqkStr;

    @ApiModelProperty(name="avaPledgePrin",value="可用质押券总额")
    private BigDecimal avaPledgePrin;
    /**
     * 质押券信息列表明细
     */
    @ApiModelProperty(name="pledgeBondTotalListDetailed",value="质押券信息列表明细")
    private List<CfetsPledgeBondInfoDto> pledgeBondTotalListDetailed;
    
    /**
     * 添加质押券信息
     */
    @ApiModelProperty(hidden = true)
    private BussRepoSecDto bussRepoSec;

    /**
     * 清算速度
     */
    @ApiModelProperty(name="settleSpreed",value="清算速度")
    private String settleSpreed;

    /**
     * 回购期限
     */
    @ApiModelProperty(name="repoTerm",value="回购期限")
    private BigDecimal repoTerm;

    /**
     * T日可用现金
     */
    @ApiModelProperty(name="availableCashT",value="T日可用现金")
    private BigDecimal availableCashT;

    /**
     * T1日可用现金
     */
    @ApiModelProperty(name="availableCashT1",value="T1日可用现金")
    private BigDecimal availableCashT1;

    /**
     * 账户现金
     */
    @ApiModelProperty(name="accountCash",value="账户现金")
    private BigDecimal accountCash;


    /**
     * 期限品种
     */
    @ApiModelProperty(name="termType",value="期限品种")
    private String termType;

    /**
     * 证券托管户
     */
    @ApiModelProperty(hidden = true)
    private String secAcctId;

    @ApiModelProperty(name="etrInvestRepoInfoDtos",value="组合分配")
    List<CfetsEtribRepoInfo003Dto> etrInvestRepoInfoDtos;
    
    /**
     * 本方券券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额")
    private BigDecimal entrustOwnNum;
    /**
     * 融入券/卖空购回券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    @ApiModelProperty(name="entrustIntegrateNum",value="融入券/卖空购回券面总额")
    private BigDecimal entrustIntegrateNum;

    /**
     * 风险试算实体类
     */
    @ApiModelProperty(name="riskTrialRetDto",value="风险试算实体类")
    private List<RiskTrialRetDto> riskTrialRetDto;

    private CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalResponse;

    /**
     * 多组合汇总指令ID
     */
    @ApiModelProperty(name="totalEtrustId",value="多组合汇总指令ID")
    private String totalEtrustId;


    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    private String softSkipMode;


    private String entrustDateStr;
    /**
     * 委托关闭方式
     */
    @ApiModelProperty(hidden = true)
    private String CloseWay;

    /**
     * 接收状态
     */
    @ApiModelProperty(hidden = true)
    private String recStatus;

    /**
     *
     */
    @ApiModelProperty(hidden = true)
    private String assetType1;


    /**
     *
     */
    private Date transmitTime;


//    /**
//     *
//     */
//    private Boolean submitFlag;


    /**
     *
     */
    private String systemAct;

    /**
     * 质押券折算总额(元)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal totalAmount;

    /**
     * 投资人员信息DTO
     */
    @ApiModelProperty(hidden = true)
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;

    private CfetsEtribRepoInfo003Dto cfetsEtribRepoInfo003Dto;

    /**
     * 本方交易员
     */
    @ApiModelProperty(hidden = true)
    private String trader;

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 repoTerm
     * @return repoTerm.
     */
    public BigDecimal getRepoTerm() {
        return repoTerm;
    }
    /**
     * 设置 repoTerm
     * @param repoTerm repoTerm
     */
    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
    }
    
    /**
     * 获取 termType
     * @return termType.
     */
    public String getTermType() {
        return termType;
    }
    
    /**
     * 设置 termType
     * @param termType termType
     */
    public void setTermType(String termType) {
        this.termType = termType;
    }

    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    public String getTotalEtrustId() {
        return totalEtrustId;
    }

    public void setTotalEtrustId(String totalEtrustId) {
        this.totalEtrustId = totalEtrustId;
    }

    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }

    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }

    public CfetsEtribdoRiskcalResponse getCfetsEtribdoRiskcalResponse() {
        return cfetsEtribdoRiskcalResponse;
    }

    public void setCfetsEtribdoRiskcalResponse(CfetsEtribdoRiskcalResponse cfetsEtribdoRiskcalResponse) {
        this.cfetsEtribdoRiskcalResponse = cfetsEtribdoRiskcalResponse;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public List<CfetsEtribRepoInfo003Dto> getEtrInvestRepoInfoDtos() {
        return etrInvestRepoInfoDtos;
    }

    public void setEtrInvestRepoInfoDtos(List<CfetsEtribRepoInfo003Dto> etrInvestRepoInfoDtos) {
        this.etrInvestRepoInfoDtos = etrInvestRepoInfoDtos;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
        return etrInvestPersonInfo;
    }

    public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
        this.etrInvestPersonInfo = etrInvestPersonInfo;
    }
    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    public String getCloseWay() {
        return CloseWay;
    }

    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }

    /**
     * 选中的数据
     */
    private transient List<CfetsEtribRepoInfo003Dto> selectedItem;

    public List<CfetsEtribRepoInfo003Dto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<CfetsEtribRepoInfo003Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    //add by chenjiaxing 多组合本方交易员字段未添加 20210914 start


    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }
    //add by chenjiaxing 多组合本方交易员字段未添加 20210914 end

	public String getSoftSkipMode() {
		return softSkipMode;
	}

	public void setSoftSkipMode(String softSkipMode) {
		this.softSkipMode = softSkipMode;
	}

	public List<CfetsPledgeBondInfoDto> getPledgeBondTotalListDetailed() {
		return pledgeBondTotalListDetailed;
	}

	public void setPledgeBondTotalListDetailed(List<CfetsPledgeBondInfoDto> pledgeBondTotalListDetailed) {
		this.pledgeBondTotalListDetailed = pledgeBondTotalListDetailed;
	}

	public BussRepoSecDto getBussRepoSec() {
		return bussRepoSec;
	}

	public void setBussRepoSec(BussRepoSecDto bussRepoSec) {
		this.bussRepoSec = bussRepoSec;
	}

	public String getZyqk() {
		return zyqk;
	}

	public void setZyqk(String zyqk) {
		this.zyqk = zyqk;
	}

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public CfetsEtribRepoInfo003Dto getCfetsEtribRepoInfo003Dto() {
        return cfetsEtribRepoInfo003Dto;
    }

    public void setCfetsEtribRepoInfo003Dto(CfetsEtribRepoInfo003Dto cfetsEtribRepoInfo003Dto) {
        this.cfetsEtribRepoInfo003Dto = cfetsEtribRepoInfo003Dto;
    }

    public String getEntrustDateStr() {
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public String getZyqkStr() {
        return zyqkStr;
    }

    public void setZyqkStr(String zyqkStr) {
        this.zyqkStr = zyqkStr;
    }

    public BigDecimal getAvailableCashT() {
        return availableCashT;
    }

    public void setAvailableCashT(BigDecimal availableCashT) {
        this.availableCashT = availableCashT;
    }

    public BigDecimal getAvailableCashT1() {
        return availableCashT1;
    }

    public void setAvailableCashT1(BigDecimal availableCashT1) {
        this.availableCashT1 = availableCashT1;
    }

    public BigDecimal getAccountCash() {
        return accountCash;
    }

    public void setAccountCash(BigDecimal accountCash) {
        this.accountCash = accountCash;
    }

    public Date getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(Date transmitTime) {
        this.transmitTime = transmitTime;
    }

//    public Boolean getSubmitFlag() {
//        return submitFlag;
//    }
//
//    public void setSubmitFlag(Boolean submitFlag) {
//        this.submitFlag = submitFlag;
//    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public BigDecimal getAvaPledgePrin() {
        return avaPledgePrin;
    }

    public void setAvaPledgePrin(BigDecimal avaPledgePrin) {
        this.avaPledgePrin = avaPledgePrin;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

}
