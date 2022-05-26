package com.joyintech.tams.invest.common.component.business.common.dto;

import java.math.BigDecimal;
import java.util.List;

import com.joyintech.tams.common.base.entity.TrdFinProductBarter;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;

/**
 * 组合持仓流水输入DTO
 */
public class PortfolioPositionsFlowInputDto {
    /**
     * 持仓类型
     */
    private String positionType;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 分红方式
     */
    private String bonusMode;

    /**
     * 资产二级类型
     */
    private String assetType2;

    /**
     * 业务阶段 01 登记 02 成交 03 交收 04 交收失败
     */
    private String stage;

    /**
     * 买断式回购特殊处理(债券数量)
     */
    private BigDecimal secAmount;

    /**
     * 金融产品交易Entity
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品实物交易表
     */
    private List<TrdFinProductBarter> trdFinProductBarterList;

    /**
     * 金融产品交易Entity
     */
    private TrdProductDealAdd trdProductDealAdd;

    /**
     * 是否走工作流（结算下行过来的数据没有流程中的记录）
     */
    private boolean isNotWorkflow;

    /**
     * 获取 positionType
     *
     * @return positionType
     */
    public String getPositionType() {
        return positionType;
    }

    /**
     * 设置 positionType
     *
     * @param positionType positionType
     */
    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    /**
     * 获取 trdProductDeal
     *
     * @return trdProductDeal
     */
    public TrdProductDeal getTrdProductDeal() {
        return trdProductDeal;
    }

    /**
     * 设置 trdProductDeal
     *
     * @param trdProductDeal trdProductDeal
     */
    public void setTrdProductDeal(TrdProductDeal trdProductDeal) {
        this.trdProductDeal = trdProductDeal;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getBonusMode() {
        return bonusMode;
    }

    public void setBonusMode(String bonusMode) {
        this.bonusMode = bonusMode;
    }

    /**
     * 获取 assetType2
     *
     * @rturn assetType2
     */
    public String getAssetType2() {
        return this.assetType2;
    }

    /**
     * 设置 assetType2
     *
     * @param assetType2 assetType2
     */
    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    /**
     * 获取 金融产品实物交易表
     *
     * @return trdFinProductBarterList 金融产品实物交易表
     */
    public List<TrdFinProductBarter> getTrdFinProductBarterList() {
        return this.trdFinProductBarterList;
    }

    /**
     * 设置 金融产品实物交易表
     *
     * @param trdFinProductBarterList 金融产品实物交易表
     */
    public void setTrdFinProductBarterList(List<TrdFinProductBarter> trdFinProductBarterList) {
        this.trdFinProductBarterList = trdFinProductBarterList;
    }
	public TrdProductDealAdd getTrdProductDealAdd() {
		return trdProductDealAdd;
	}

	public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
		this.trdProductDealAdd = trdProductDealAdd;
	}

    public BigDecimal getSecAmount() {
        return secAmount;
    }

    public void setSecAmount(BigDecimal secAmount) {
        this.secAmount = secAmount;
    }

    public boolean isNotWorkflow() {
        return isNotWorkflow;
    }

    public void setNotWorkflow(boolean notWorkflow) {
        isNotWorkflow = notWorkflow;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
