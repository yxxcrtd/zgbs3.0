package com.joyintech.tams.component.business.riskall.dto;

import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 风险升级 4合一 接口入参
 *
 * @author 代志伟
 * @version 1.0 2022年5月30日
 * @see RiskCommonService
 * @since 1.0
 */

public class RiskCommonInputDto extends RiskNodeConfigDtl {

    /**
     * 指标试算入参信息
     */
    private List<InterfRiskRdsDto> riskRdsDtoList;

    /**
     * 授信试算入参信息
     */
    private List<CreditLinesInputDto> creditInputList;

    /**
     * 名单试算入参
     */
    private List<RiskNameListDto> riskNameList;

    /*授权相关试算入参信息*/

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易金额
     */
    private BigDecimal prinAmt;

    /**
     * 平安资产类型
     */
    private String assetType;

    /**
     * 资产一级分类
     */
    private String assetSecType;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易方向
     */
    private String ps;


    public List<InterfRiskRdsDto> getRiskRdsDtoList() {
        return riskRdsDtoList;
    }

    public void setRiskRdsDtoList(List<InterfRiskRdsDto> riskRdsDtoList) {
        this.riskRdsDtoList = riskRdsDtoList;
    }

    public List<CreditLinesInputDto> getCreditInputList() {
        return creditInputList;
    }

    public void setCreditInputList(List<CreditLinesInputDto> creditInputList) {
        this.creditInputList = creditInputList;
    }

    public List<RiskNameListDto> getRiskNameList() {
        return riskNameList;
    }

    public void setRiskNameList(List<RiskNameListDto> riskNameList) {
        this.riskNameList = riskNameList;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetSecType() {
        return assetSecType;
    }

    public void setAssetSecType(String assetSecType) {
        this.assetSecType = assetSecType;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}
