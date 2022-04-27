package com.joyintech.tams.business.common.dto;

import java.util.List;

/**
 * 银行间债券信息查询参数
 *
 * @author zhuys
 * @version 2022年04月14日
 * @see CfetsEtrFinProductInfoRequest
 * @since 1.0
 */
public class CfetsEtrFinProductInfoRequest {

    /**
     * 资产代码
     */
    public String finprodId;

    /**
     * 投管类型
     */
    private String finprodType2;

    /**
     * 投管类型
     */
    private String finprodType;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 交易场所
     */
    private List<String> tradeMarketList;

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

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public List<String> getTradeMarketList() {
        return tradeMarketList;
    }

    public void setTradeMarketList(List<String> tradeMarketList) {
        this.tradeMarketList = tradeMarketList;
    }
}
