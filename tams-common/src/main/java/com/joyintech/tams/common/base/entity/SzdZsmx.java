package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class SzdZsmx extends BaseEntity {
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
     * 计税流水号
     */
    private String mxjsls;

    /**
     * 结算账户
     */
    private String mxjszh;

    /**
     * 结算备付金账号
     */
    private String mxbfzh;

    /**
     * 业务类别
     */
    private String mxywlb;

    /**
     * 证券代号
     */
    private String mxzqdh;

    /**
     * 托管单元
     */
    private String mxxwdh;

    /**
     * 证券账户
     */
    private String mxgddm;

    /**
     * 净减持股数
     */
    private Long mxjcgs;

    /**
     * 减持股份交割日期
     */
    private Date mxjcrq;

    /**
     * 计税日期
     */
    private Date mxjsrq;

    /**
     * 应缴税金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mxfsje;

    /**
     * 货币代号
     */
    private String mxhbdh;

    /**
     * 发送日期
     */
    private Date mxfsrq;

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
     * 取得 计税流水号
     * @return 计税流水号
     */
    public String getMxjsls() {
        return mxjsls;
    }

    /**
     * 设置 计税流水号
     * @param mxjsls 计税流水号
     */
    public void setMxjsls(String mxjsls) {
        this.mxjsls = mxjsls == null ? null : mxjsls.trim();
    }

    /**
     * 取得 结算账户
     * @return 结算账户
     */
    public String getMxjszh() {
        return mxjszh;
    }

    /**
     * 设置 结算账户
     * @param mxjszh 结算账户
     */
    public void setMxjszh(String mxjszh) {
        this.mxjszh = mxjszh == null ? null : mxjszh.trim();
    }

    /**
     * 取得 结算备付金账号
     * @return 结算备付金账号
     */
    public String getMxbfzh() {
        return mxbfzh;
    }

    /**
     * 设置 结算备付金账号
     * @param mxbfzh 结算备付金账号
     */
    public void setMxbfzh(String mxbfzh) {
        this.mxbfzh = mxbfzh == null ? null : mxbfzh.trim();
    }

    /**
     * 取得 业务类别
     * @return 业务类别
     */
    public String getMxywlb() {
        return mxywlb;
    }

    /**
     * 设置 业务类别
     * @param mxywlb 业务类别
     */
    public void setMxywlb(String mxywlb) {
        this.mxywlb = mxywlb == null ? null : mxywlb.trim();
    }

    /**
     * 取得 证券代号
     * @return 证券代号
     */
    public String getMxzqdh() {
        return mxzqdh;
    }

    /**
     * 设置 证券代号
     * @param mxzqdh 证券代号
     */
    public void setMxzqdh(String mxzqdh) {
        this.mxzqdh = mxzqdh == null ? null : mxzqdh.trim();
    }

    /**
     * 取得 托管单元
     * @return 托管单元
     */
    public String getMxxwdh() {
        return mxxwdh;
    }

    /**
     * 设置 托管单元
     * @param mxxwdh 托管单元
     */
    public void setMxxwdh(String mxxwdh) {
        this.mxxwdh = mxxwdh == null ? null : mxxwdh.trim();
    }

    /**
     * 取得 证券账户
     * @return 证券账户
     */
    public String getMxgddm() {
        return mxgddm;
    }

    /**
     * 设置 证券账户
     * @param mxgddm 证券账户
     */
    public void setMxgddm(String mxgddm) {
        this.mxgddm = mxgddm == null ? null : mxgddm.trim();
    }

    /**
     * 取得 净减持股数
     * @return 净减持股数
     */
    public Long getMxjcgs() {
        return mxjcgs;
    }

    /**
     * 设置 净减持股数
     * @param mxjcgs 净减持股数
     */
    public void setMxjcgs(Long mxjcgs) {
        this.mxjcgs = mxjcgs;
    }

    /**
     * 取得 减持股份交割日期
     * @return 减持股份交割日期
     */
    public Date getMxjcrq() {
        return mxjcrq;
    }

    /**
     * 设置 减持股份交割日期
     * @param mxjcrq 减持股份交割日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMxjcrq(Date mxjcrq) {
        this.mxjcrq = mxjcrq;
    }

    /**
     * 取得 计税日期
     * @return 计税日期
     */
    public Date getMxjsrq() {
        return mxjsrq;
    }

    /**
     * 设置 计税日期
     * @param mxjsrq 计税日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMxjsrq(Date mxjsrq) {
        this.mxjsrq = mxjsrq;
    }

    /**
     * 取得 应缴税金金额
     * @return 应缴税金金额
     */
    public BigDecimal getMxfsje() {
        return mxfsje;
    }

    /**
     * 设置 应缴税金金额
     * @param mxfsje 应缴税金金额
     */
    public void setMxfsje(BigDecimal mxfsje) {
        this.mxfsje = mxfsje;
    }

    /**
     * 取得 货币代号
     * @return 货币代号
     */
    public String getMxhbdh() {
        return mxhbdh;
    }

    /**
     * 设置 货币代号
     * @param mxhbdh 货币代号
     */
    public void setMxhbdh(String mxhbdh) {
        this.mxhbdh = mxhbdh == null ? null : mxhbdh.trim();
    }

    /**
     * 取得 发送日期
     * @return 发送日期
     */
    public Date getMxfsrq() {
        return mxfsrq;
    }

    /**
     * 设置 发送日期
     * @param mxfsrq 发送日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMxfsrq(Date mxfsrq) {
        this.mxfsrq = mxfsrq;
    }

    public String getJgdm() {
        return jgdm;
    }

    public void setJgdm(String jgdm) {
        this.jgdm = jgdm;
    }
}