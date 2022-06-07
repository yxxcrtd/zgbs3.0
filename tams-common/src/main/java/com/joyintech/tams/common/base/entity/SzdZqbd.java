package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.util.Date;

public class SzdZqbd extends BaseEntity {
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
     * 市场代码
     */
    private String scdm;

    /**
     * 托管会员的清算编号，对于QFII账户，填写托管银行的清算编号
     */
    private String qsbh;

    /**
     * 每个产品在交易所有唯一的身份ID即股东代码，也称证券账户。需转换成系统的组合代码代码
     */
    private String zqzh;

    /**
     * 交易单元
     */
    private String xwh;

    /**
     * 证券代码
     */
    private String zqdm;

    /**
     * 证券类别
     */
    private String zqlb;

    /**
     * 流通类型
     */
    private String ltlx;

    /**
     * 权益类别
     */
    private String qylb;

    /**
     * 挂牌年份
     */
    private String gpnf;

    /**
     * 变动数量
     */
    private String bdsl;

    /**
     * 变动类型
     */
    private String bdlx;

    /**
     * 变动日期
     */
    private String bdrq;

    /**
     * 数量(保留字段)
     */
    private String sl;

    /**
     * 编号(保留字段)
     */
    private String bh;

    /**
     * 备用
     */
    private String by1;

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
     * 取得 市场代码
     * @return 市场代码
     */
    public String getScdm() {
        return scdm;
    }

    /**
     * 设置 市场代码
     * @param scdm 市场代码
     */
    public void setScdm(String scdm) {
        this.scdm = scdm == null ? null : scdm.trim();
    }

    /**
     * 取得 托管会员的清算编号，对于QFII账户，填写托管银行的清算编号
     * @return 托管会员的清算编号，对于QFII账户，填写托管银行的清算编号
     */
    public String getQsbh() {
        return qsbh;
    }

    /**
     * 设置 托管会员的清算编号，对于QFII账户，填写托管银行的清算编号
     * @param qsbh 托管会员的清算编号，对于QFII账户，填写托管银行的清算编号
     */
    public void setQsbh(String qsbh) {
        this.qsbh = qsbh == null ? null : qsbh.trim();
    }

    /**
     * 取得 每个产品在交易所有唯一的身份ID即股东代码，也称证券账户。需转换成系统的组合代码代码
     * @return 每个产品在交易所有唯一的身份ID即股东代码，也称证券账户。需转换成系统的组合代码代码
     */
    public String getZqzh() {
        return zqzh;
    }

    /**
     * 设置 每个产品在交易所有唯一的身份ID即股东代码，也称证券账户。需转换成系统的组合代码代码
     * @param zqzh 每个产品在交易所有唯一的身份ID即股东代码，也称证券账户。需转换成系统的组合代码代码
     */
    public void setZqzh(String zqzh) {
        this.zqzh = zqzh == null ? null : zqzh.trim();
    }

    /**
     * 取得 交易单元
     * @return 交易单元
     */
    public String getXwh() {
        return xwh;
    }

    /**
     * 设置 交易单元
     * @param xwh 交易单元
     */
    public void setXwh(String xwh) {
        this.xwh = xwh == null ? null : xwh.trim();
    }

    /**
     * 取得 证券代码
     * @return 证券代码
     */
    public String getZqdm() {
        return zqdm;
    }

    /**
     * 设置 证券代码
     * @param zqdm 证券代码
     */
    public void setZqdm(String zqdm) {
        this.zqdm = zqdm == null ? null : zqdm.trim();
    }

    /**
     * 取得 证券类别
     * @return 证券类别
     */
    public String getZqlb() {
        return zqlb;
    }

    /**
     * 设置 证券类别
     * @param zqlb 证券类别
     */
    public void setZqlb(String zqlb) {
        this.zqlb = zqlb == null ? null : zqlb.trim();
    }

    /**
     * 取得 流通类型
     * @return 流通类型
     */
    public String getLtlx() {
        return ltlx;
    }

    /**
     * 设置 流通类型
     * @param ltlx 流通类型
     */
    public void setLtlx(String ltlx) {
        this.ltlx = ltlx == null ? null : ltlx.trim();
    }

    /**
     * 取得 权益类别
     * @return 权益类别
     */
    public String getQylb() {
        return qylb;
    }

    /**
     * 设置 权益类别
     * @param qylb 权益类别
     */
    public void setQylb(String qylb) {
        this.qylb = qylb == null ? null : qylb.trim();
    }

    /**
     * 取得 挂牌年份
     * @return 挂牌年份
     */
    public String getGpnf() {
        return gpnf;
    }

    /**
     * 设置 挂牌年份
     * @param gpnf 挂牌年份
     */
    public void setGpnf(String gpnf) {
        this.gpnf = gpnf == null ? null : gpnf.trim();
    }

    /**
     * 取得 变动数量
     * @return 变动数量
     */
    public String getBdsl() {
        return bdsl;
    }

    /**
     * 设置 变动数量
     * @param bdsl 变动数量
     */
    public void setBdsl(String bdsl) {
        this.bdsl = bdsl == null ? null : bdsl.trim();
    }

    /**
     * 取得 变动类型
     * @return 变动类型
     */
    public String getBdlx() {
        return bdlx;
    }

    /**
     * 设置 变动类型
     * @param bdlx 变动类型
     */
    public void setBdlx(String bdlx) {
        this.bdlx = bdlx == null ? null : bdlx.trim();
    }

    /**
     * 取得 变动日期
     * @return 变动日期
     */
    public String getBdrq() {
        return bdrq;
    }

    /**
     * 设置 变动日期
     * @param bdrq 变动日期
     */
    public void setBdrq(String bdrq) {
        this.bdrq = bdrq == null ? null : bdrq.trim();
    }

    /**
     * 取得 数量(保留字段)
     * @return 数量(保留字段)
     */
    public String getSl() {
        return sl;
    }

    /**
     * 设置 数量(保留字段)
     * @param sl 数量(保留字段)
     */
    public void setSl(String sl) {
        this.sl = sl == null ? null : sl.trim();
    }

    /**
     * 取得 编号(保留字段)
     * @return 编号(保留字段)
     */
    public String getBh() {
        return bh;
    }

    /**
     * 设置 编号(保留字段)
     * @param bh 编号(保留字段)
     */
    public void setBh(String bh) {
        this.bh = bh == null ? null : bh.trim();
    }

    /**
     * 取得 备用
     * @return 备用
     */
    public String getBy1() {
        return by1;
    }

    /**
     * 设置 备用
     * @param by1 备用
     */
    public void setBy1(String by1) {
        this.by1 = by1 == null ? null : by1.trim();
    }

    public String getJgdm() {
        return jgdm;
    }

    public void setJgdm(String jgdm) {
        this.jgdm = jgdm;
    }
}