package com.joyintech.tams.business.common.entity;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

/**
 * 描述:CFG_READONLY_FORM_SHOW表的实体类
 * @version
 * @author:  caiq
 * @创建时间: 2021-11-16
 */
@ApiModel(value = "画面只读表单展示配置实体类")
public class CfgReadonlyFormShow{
    /**
     * 页面编码
     */
    private String pagecode;

    /**
     * 页面区域编码
     */
    private String dtcode;

    /**
     * 扩展条件编码,默认999999
     */
    private String spcdcode;

    /**
     * 字段代码
     */
    private String colcode;

    /**
     * 字段名称
     */
    private String colname;

    /**
     * null
     */
    private String dictcode;

    /**
     * 顺序号
     */
    private BigDecimal seqno;

    /**
     * 扩张配置
     */
    private String extendcfg;

    /**
     * 可用状态(0:不可用,1:可用)
     */
    private BigDecimal status;

    /**
     * 备注1
     */
    private BigDecimal remark1;

    /**
     * 备注2
     */
    private String remark2;

    /**
     * 备注3
     */
    private String remark3;

    /**
     * 页面编码
     * @return PAGECODE 页面编码
     */
    public String getPagecode() {
        return pagecode;
    }

    /**
     * 页面编码
     * @param pagecode 页面编码
     */
    public void setPagecode(String pagecode) {
        this.pagecode = pagecode == null ? null : pagecode.trim();
    }

    /**
     * 页面区域编码
     * @return DTCODE 页面区域编码
     */
    public String getDtcode() {
        return dtcode;
    }

    /**
     * 页面区域编码
     * @param dtcode 页面区域编码
     */
    public void setDtcode(String dtcode) {
        this.dtcode = dtcode == null ? null : dtcode.trim();
    }

    /**
     * 扩展条件编码,默认999999
     * @return SPCDCODE 扩展条件编码,默认999999
     */
    public String getSpcdcode() {
        return spcdcode;
    }

    /**
     * 扩展条件编码,默认999999
     * @param spcdcode 扩展条件编码,默认999999
     */
    public void setSpcdcode(String spcdcode) {
        this.spcdcode = spcdcode == null ? null : spcdcode.trim();
    }

    /**
     * 字段代码
     * @return COLCODE 字段代码
     */
    public String getColcode() {
        return colcode;
    }

    /**
     * 字段代码
     * @param colcode 字段代码
     */
    public void setColcode(String colcode) {
        this.colcode = colcode == null ? null : colcode.trim();
    }

    /**
     * 字段名称
     * @return COLNAME 字段名称
     */
    public String getColname() {
        return colname;
    }

    /**
     * 字段名称
     * @param colname 字段名称
     */
    public void setColname(String colname) {
        this.colname = colname == null ? null : colname.trim();
    }

    /**
     * null
     * @return DICTCODE null
     */
    public String getDictcode() {
        return dictcode;
    }

    /**
     * null
     * @param dictcode null
     */
    public void setDictcode(String dictcode) {
        this.dictcode = dictcode == null ? null : dictcode.trim();
    }

    /**
     * 顺序号
     * @return SEQNO 顺序号
     */
    public BigDecimal getSeqno() {
        return seqno;
    }

    /**
     * 顺序号
     * @param seqno 顺序号
     */
    public void setSeqno(BigDecimal seqno) {
        this.seqno = seqno;
    }

    /**
     * 扩张配置
     * @return EXTENDCFG 扩张配置
     */
    public String getExtendcfg() {
        return extendcfg;
    }

    /**
     * 扩张配置
     * @param extendcfg 扩张配置
     */
    public void setExtendcfg(String extendcfg) {
        this.extendcfg = extendcfg == null ? null : extendcfg.trim();
    }

    /**
     * 可用状态(0:不可用,1:可用)
     * @return STATUS 可用状态(0:不可用,1:可用)
     */
    public BigDecimal getStatus() {
        return status;
    }

    /**
     * 可用状态(0:不可用,1:可用)
     * @param status 可用状态(0:不可用,1:可用)
     */
    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    /**
     * 备注1
     * @return REMARK1 备注1
     */
    public BigDecimal getRemark1() {
        return remark1;
    }

    /**
     * 备注1
     * @param remark1 备注1
     */
    public void setRemark1(BigDecimal remark1) {
        this.remark1 = remark1;
    }

    /**
     * 备注2
     * @return REMARK2 备注2
     */
    public String getRemark2() {
        return remark2;
    }

    /**
     * 备注2
     * @param remark2 备注2
     */
    public void setRemark2(String remark2) {
        this.remark2 = remark2 == null ? null : remark2.trim();
    }

    /**
     * 备注3
     * @return REMARK3 备注3
     */
    public String getRemark3() {
        return remark3;
    }

    /**
     * 备注3
     * @param remark3 备注3
     */
    public void setRemark3(String remark3) {
        this.remark3 = remark3 == null ? null : remark3.trim();
    }
}