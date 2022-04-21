package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class SzdSjsfx extends BaseEntity {
    /**
     * 流水号
     */
    private String seqno;

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
     * 结算账号
     */
    private String fxjszh;

    /**
     * 托管单元
     */
    private String fxtgdy;

    /**
     * 证券代码
     */
    private String fxzqdm;

    /**
     * 证券账户
     */
    private String fxzqzh;

    /**
     * 业务类别
     */
    private String fxywlb;

    /**
     * 客户订单编号
     */
    private String fxddbh;

    /**
     * 委托股数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fxwtgs;

    /**
     * 确认股数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fxqrgs;

    /**
     * 资金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fxzjje;

    /**
     * 错误代号
     */
    private String fxcwdh;

    /**
     * 发送日期
     */
    private String fxfsrq;

    /**
     * 备用标志
     */
    private String fxbybz;

    /**
     * 交收结果
     */
    private String jgdm;

    /**
     * 取得 流水号
     * @return 流水号
     */
    public String getSeqno() {
        return seqno;
    }

    /**
     * 设置 流水号
     * @param seqno 流水号
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }

    /**
     * 取得 导入日期
     * @return 导入日期
     */
    public Date getImportDate() {
        return importDate;
    }

    /**
     * 设置 导入日期
     * @param importDate 导入日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    /**
     * 取得 数据文件
     * @return 数据文件
     */
    public String getDataFile() {
        return dataFile;
    }

    /**
     * 设置 数据文件
     * @param dataFile 数据文件
     */
    public void setDataFile(String dataFile) {
        this.dataFile = dataFile == null ? null : dataFile.trim();
    }

    /**
     * 取得 是否本次数据清算对象
     * @return 是否本次数据清算对象
     */
    public String getIsSettleTarget() {
        return isSettleTarget;
    }

    /**
     * 设置 是否本次数据清算对象
     * @param isSettleTarget 是否本次数据清算对象
     */
    public void setIsSettleTarget(String isSettleTarget) {
        this.isSettleTarget = isSettleTarget == null ? null : isSettleTarget.trim();
    }

    /**
     * 取得 结算账号
     * @return 结算账号
     */
    public String getFxjszh() {
        return fxjszh;
    }

    /**
     * 设置 结算账号
     * @param fxjszh 结算账号
     */
    public void setFxjszh(String fxjszh) {
        this.fxjszh = fxjszh == null ? null : fxjszh.trim();
    }

    /**
     * 取得 托管单元
     * @return 托管单元
     */
    public String getFxtgdy() {
        return fxtgdy;
    }

    /**
     * 设置 托管单元
     * @param fxtgdy 托管单元
     */
    public void setFxtgdy(String fxtgdy) {
        this.fxtgdy = fxtgdy == null ? null : fxtgdy.trim();
    }

    /**
     * 取得 证券代码
     * @return 证券代码
     */
    public String getFxzqdm() {
        return fxzqdm;
    }

    /**
     * 设置 证券代码
     * @param fxzqdm 证券代码
     */
    public void setFxzqdm(String fxzqdm) {
        this.fxzqdm = fxzqdm == null ? null : fxzqdm.trim();
    }

    /**
     * 取得 证券账户
     * @return 证券账户
     */
    public String getFxzqzh() {
        return fxzqzh;
    }

    /**
     * 设置 证券账户
     * @param fxzqzh 证券账户
     */
    public void setFxzqzh(String fxzqzh) {
        this.fxzqzh = fxzqzh == null ? null : fxzqzh.trim();
    }

    /**
     * 取得 业务类别
     * @return 业务类别
     */
    public String getFxywlb() {
        return fxywlb;
    }

    /**
     * 设置 业务类别
     * @param fxywlb 业务类别
     */
    public void setFxywlb(String fxywlb) {
        this.fxywlb = fxywlb == null ? null : fxywlb.trim();
    }

    /**
     * 取得 客户订单编号
     * @return 客户订单编号
     */
    public String getFxddbh() {
        return fxddbh;
    }

    /**
     * 设置 客户订单编号
     * @param fxddbh 客户订单编号
     */
    public void setFxddbh(String fxddbh) {
        this.fxddbh = fxddbh == null ? null : fxddbh.trim();
    }

    /**
     * 取得 委托股数
     * @return 委托股数
     */
    public BigDecimal getFxwtgs() {
        return fxwtgs;
    }

    /**
     * 设置 委托股数
     * @param fxwtgs 委托股数
     */
    public void setFxwtgs(BigDecimal fxwtgs) {
        this.fxwtgs = fxwtgs;
    }

    /**
     * 取得 确认股数
     * @return 确认股数
     */
    public BigDecimal getFxqrgs() {
        return fxqrgs;
    }

    /**
     * 设置 确认股数
     * @param fxqrgs 确认股数
     */
    public void setFxqrgs(BigDecimal fxqrgs) {
        this.fxqrgs = fxqrgs;
    }

    /**
     * 取得 资金金额
     * @return 资金金额
     */
    public BigDecimal getFxzjje() {
        return fxzjje;
    }

    /**
     * 设置 资金金额
     * @param fxzjje 资金金额
     */
    public void setFxzjje(BigDecimal fxzjje) {
        this.fxzjje = fxzjje;
    }

    /**
     * 取得 错误代号
     * @return 错误代号
     */
    public String getFxcwdh() {
        return fxcwdh;
    }

    /**
     * 设置 错误代号
     * @param fxcwdh 错误代号
     */
    public void setFxcwdh(String fxcwdh) {
        this.fxcwdh = fxcwdh == null ? null : fxcwdh.trim();
    }

    /**
     * 取得 发送日期
     * @return 发送日期
     */
    public String getFxfsrq() {
        return fxfsrq;
    }

    /**
     * 设置 发送日期
     * @param fxfsrq 发送日期
     */
    public void setFxfsrq(String fxfsrq) {
        this.fxfsrq = fxfsrq == null ? null : fxfsrq.trim();
    }

    /**
     * 取得 备用标志
     * @return 备用标志
     */
    public String getFxbybz() {
        return fxbybz;
    }

    /**
     * 设置 备用标志
     * @param fxbybz 备用标志
     */
    public void setFxbybz(String fxbybz) {
        this.fxbybz = fxbybz == null ? null : fxbybz.trim();
    }

    public String getJgdm() {
        return jgdm;
    }

    public void setJgdm(String jgdm) {
        this.jgdm = jgdm;
    }
}