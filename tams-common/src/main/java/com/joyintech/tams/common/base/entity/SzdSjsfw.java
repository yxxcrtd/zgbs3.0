package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 深中登SJSFW
 */
public class SzdSjsfw extends BaseEntity {
    /**
     * 流水号
     */
    private String seqNo;
    /**
     * 导入日期
     */
    private Date importDate;
    /**
     * 数据文件
     */
    private String dataFile;
    /**
     * 是否本次数据清算对象
     */
    private String isSettleTarget;
    /**
     * 数据类别
     */
    private String fwsjlb;
    /**
     * 结算账号
     */
    private String fwjszh;
    /**
     * 结算账号
     */
    private String fwbfzh;
    /**
     * 托管单元
     */
    private String fwtgdy;
    /**
     * 证券代码
     */
    private String fwzqdm;
    /**
     * 证券账户
     */
    private String fwzqzh;
    /**
     * 股份性质
     */
    private String fwgfxz;
    /**
     * 流通类型
     */
    private String fwltlx;
    /**
     * 买入金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fwmrje;
    /**
     * 卖出金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fwmcje;
    /**
     * 资金余额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fwzjye;
    /**
     * 数量1
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fwsl1;
    /**
     * 数量2
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal fwsl2;
    /**
     * 处理日期
     */
    private String fwclrq;
    /**
     * 发送日期
     */
    private String fwfsrq;
    /**
     * 摘要说明
     */
    private String fwzysm;
    /**
     * 备注说明
     */
    private String fwbzsm;
    /**
     * 备用标志
     */
    private String fwbybz;

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getIsSettleTarget() {
        return isSettleTarget;
    }

    public void setIsSettleTarget(String isSettleTarget) {
        this.isSettleTarget = isSettleTarget;
    }

    public String getFwsjlb() {
        return fwsjlb;
    }

    public void setFwsjlb(String fwsjlb) {
        this.fwsjlb = fwsjlb;
    }

    public String getFwjszh() {
        return fwjszh;
    }

    public void setFwjszh(String fwjszh) {
        this.fwjszh = fwjszh;
    }

    public String getFwbfzh() {
        return fwbfzh;
    }

    public void setFwbfzh(String fwbfzh) {
        this.fwbfzh = fwbfzh;
    }

    public String getFwtgdy() {
        return fwtgdy;
    }

    public void setFwtgdy(String fwtgdy) {
        this.fwtgdy = fwtgdy;
    }

    public String getFwzqdm() {
        return fwzqdm;
    }

    public void setFwzqdm(String fwzqdm) {
        this.fwzqdm = fwzqdm;
    }

    public String getFwzqzh() {
        return fwzqzh;
    }

    public void setFwzqzh(String fwzqzh) {
        this.fwzqzh = fwzqzh;
    }

    public String getFwgfxz() {
        return fwgfxz;
    }

    public void setFwgfxz(String fwgfxz) {
        this.fwgfxz = fwgfxz;
    }

    public String getFwltlx() {
        return fwltlx;
    }

    public void setFwltlx(String fwltlx) {
        this.fwltlx = fwltlx;
    }

    public BigDecimal getFwmrje() {
        return fwmrje;
    }

    public void setFwmrje(BigDecimal fwmrje) {
        this.fwmrje = fwmrje;
    }

    public BigDecimal getFwmcje() {
        return fwmcje;
    }

    public void setFwmcje(BigDecimal fwmcje) {
        this.fwmcje = fwmcje;
    }

    public BigDecimal getFwzjye() {
        return fwzjye;
    }

    public void setFwzjye(BigDecimal fwzjye) {
        this.fwzjye = fwzjye;
    }

    public BigDecimal getFwsl1() {
        return fwsl1;
    }

    public void setFwsl1(BigDecimal fwsl1) {
        this.fwsl1 = fwsl1;
    }

    public BigDecimal getFwsl2() {
        return fwsl2;
    }

    public void setFwsl2(BigDecimal fwsl2) {
        this.fwsl2 = fwsl2;
    }

    public String getFwclrq() {
        return fwclrq;
    }

    public void setFwclrq(String fwclrq) {
        this.fwclrq = fwclrq;
    }

    public String getFwfsrq() {
        return fwfsrq;
    }

    public void setFwfsrq(String fwfsrq) {
        this.fwfsrq = fwfsrq;
    }

    public String getFwzysm() {
        return fwzysm;
    }

    public void setFwzysm(String fwzysm) {
        this.fwzysm = fwzysm;
    }

    public String getFwbzsm() {
        return fwbzsm;
    }

    public void setFwbzsm(String fwbzsm) {
        this.fwbzsm = fwbzsm;
    }

    public String getFwbybz() {
        return fwbybz;
    }

    public void setFwbybz(String fwbybz) {
        this.fwbybz = fwbybz;
    }
}