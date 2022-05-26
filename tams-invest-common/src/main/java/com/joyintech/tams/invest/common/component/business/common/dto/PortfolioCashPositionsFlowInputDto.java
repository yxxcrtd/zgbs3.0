package com.joyintech.tams.invest.common.component.business.common.dto;

import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;

import java.util.List;

/**
 * 组合现金流水输入DTO
 */
public class PortfolioCashPositionsFlowInputDto {
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
     * 金融产品交易Entity
     */
    private TrdProductDeal trdProductDeal;

    /**
     * 金融产品交易附属Entity
     */
    private TrdProductDealAdd trdProductDealAdd;

    /**
     * 划款指令list
     */
    private List<StlPayOrdersDto> stlPayOrders;
    /**
     * 是否撤销交易
     */
    private Boolean needNegative;

    /**
     * 是否走工作流（结算下行过来的数据没有流程中的记录）
     */
    private boolean isNotWorkflow;

    /**
     * 收支方向
     */
    private String ieFlag;

    /**
     * 业务阶段 01 登记 02 成交 03 交收 04 交收失败
     */
    private String stage;

    /**
     * 获取 ieFlag
     *
     * @return ieFlag
     */
    public String getIeFlag() {
        return ieFlag;
    }

    /**
     * 设置 ieFlag
     *
     * @param ieFlag ieFlag
     */
    public void setIeFlag(String ieFlag) {
        this.ieFlag = ieFlag;
    }

    /**
     * 获取 needNegative
     *
     * @return needNegative
     */
    public Boolean getNeedNegative() {
        return needNegative;
    }

    /**
     * 设置 needNegative
     *
     * @param needNegative needNegative
     */
    public void setNeedNegative(Boolean needNegative) {
        this.needNegative = needNegative;
    }

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

    /**
     * 获取 trdProductDealAdd
     *
     * @return trdProductDealAdd
     */
    public TrdProductDealAdd getTrdProductDealAdd() {
        return trdProductDealAdd;
    }

    /**
     * 设置 trdProductDealAdd
     *
     * @param trdProductDealAdd trdProductDealAdd
     */
    public void setTrdProductDealAdd(TrdProductDealAdd trdProductDealAdd) {
        this.trdProductDealAdd = trdProductDealAdd;
    }

    /**
     * 获取 stlPayOrders
     *
     * @return stlPayOrders
     */
    public List<StlPayOrdersDto> getStlPayOrders() {
        return stlPayOrders;
    }

    /**
     * 设置 stlPayOrders
     *
     * @param stlPayOrders stlPayOrders
     */
    public void setStlPayOrders(List<StlPayOrdersDto> stlPayOrders) {
        this.stlPayOrders = stlPayOrders;
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
     * 获取 isNotWorkflow
     * @return isNotWorkflow.
     */
    public boolean isNotWorkflow() {
        return isNotWorkflow;
    }

    /**
     * 设置 isNotWorkflow
     * @param isNotWorkflow isNotWorkflow
     */
    public void setNotWorkflow(boolean isNotWorkflow) {
        this.isNotWorkflow = isNotWorkflow;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
