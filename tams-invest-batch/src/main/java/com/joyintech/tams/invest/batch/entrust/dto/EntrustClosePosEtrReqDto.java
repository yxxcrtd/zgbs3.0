package com.joyintech.tams.invest.batch.entrust.dto;

/**
 * 头寸信息查询，请求参数
 */
public class EntrustClosePosEtrReqDto {

    /**
     * 业务类型
     */
    private String busiType;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 关闭编号
     */
    private String closeId;
    /**
     * 系统动作
     */
    private String systemAct;
    /**
     * 业务动作
     */
    private String busiAct;

    /**
     * 金融产品类型
     *
     * @return
     */
    private String finprodType2;

    /**
     * 交易场所
     *
     * @return
     */
    private String tradeMarket;

    /**
     * 业务类型
     *
     * @return
     */
    private String busiTypePosition;

    /**
     * 发行方式
     */
    private String issueClass;

    private String finprodId;

    /**
     * 资产类型
     */
    private String assetType1;

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSystemAct() {
        return systemAct;
    }

    public void setSystemAct(String systemAct) {
        this.systemAct = systemAct;
    }

    public String getBusiAct() {
        return busiAct;
    }

    public void setBusiAct(String busiAct) {
        this.busiAct = busiAct;
    }

    public String getCloseId() {
        return closeId;
    }

    public void setCloseId(String closeId) {
        this.closeId = closeId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    private String entrustStatus;

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }
}
