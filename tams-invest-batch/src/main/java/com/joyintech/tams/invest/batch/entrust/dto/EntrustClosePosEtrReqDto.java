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
}
