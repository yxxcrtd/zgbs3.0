/*
 * 文件名：ValSchemeBatchDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ruanhongming
 * 修改时间：2018年6月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valscheme.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.base.spring.annotation.BaseEntityClassName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 科目余额Batch
 * 【weipeng-note: 暂时没有科目余额实体，先用SubjectBalance代替】
 * @author weipeng
 * @version 2.0 2018年5月29日
 * @see ValSchemeBatchDto
 * @since 1.0
 */
/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzhiang
 * @version 1.0 2018年7月10日
 * @see ValSchemeBatchDto
 * @since 1.0
 */
/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzhiang
 * @version 1.0 2018年7月10日
 * @see ValSchemeBatchDto
 * @since 1.0
 */
@BaseEntityClassName(value = ValSchemeBatchDto.class)
public class ValSchemeBatchDto extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 账套名称
     */
    private String booksetName;

    /**
     * 入账日
     */
    private Date bookDate;

    /**
     * 科目号
     */
    private String subjectNo;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 原币
     */
    private String oCcy;

    /**
     * 数量余额
     */
    private BigDecimal numAmt;

    /**
     * 原币余额
     */
    private BigDecimal oAmt;

    /**
     * 余额方向
     */
    private String balFlag;

    /**
     * 科目类型
     */
    private String subjectType;

    /**
     * 科目级别
     */
    private Long subjectLevel;

    /**
     * 基础科目性质
     */
    private String baseSubjectNature;

    /**
     * 上级科目号
     */
    private String fsubjectNo;

    /**
     * 四级科目号尾号
     */
    private String detailSubjectNoTail;

    /**
     * 本位币
     */
    private String bCcy;

    /**
     * 本位币余额
     */
    private BigDecimal bAmt;

    /**
     * 科目余额期待方向
     */
    private String expectBalFlag;

    /**
     * 估值方法
     */
    private String valuationMethod;


    /**
     * 获取 booksetId
     * 
     * @return booksetId.
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 booksetId
     * 
     * @param booksetId booksetId
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 获取 booksetName
     * 
     * @return booksetName.
     */
    public String getBooksetName() {
        return booksetName;
    }

    /**
     * 设置 booksetName
     * 
     * @param booksetName booksetName
     */
    public void setBooksetName(String booksetName) {
        this.booksetName = booksetName;
    }

    /**
     * 获取 bookDate
     * 
     * @return bookDate.
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * 设置 bookDate
     * 
     * @param bookDate bookDate
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * 获取 subjectNo
     * 
     * @return subjectNo.
     */
    public String getSubjectNo() {
        return subjectNo;
    }

    /**
     * 设置 subjectNo
     * 
     * @param subjectNo subjectNo
     */
    public void setSubjectNo(String subjectNo) {
        this.subjectNo = subjectNo;
    }

    /**
     * 获取 subjectName
     * 
     * @return subjectName.
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * 设置 subjectName
     * 
     * @param subjectName subjectName
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * 获取 oCcy
     * 
     * @return oCcy.
     */
    public String getoCcy() {
        return oCcy;
    }

    /**
     * 设置 oCcy
     * 
     * @param oCcy oCcy
     */
    public void setoCcy(String oCcy) {
        this.oCcy = oCcy;
    }

    /**
     * 获取 numAmt
     * 
     * @return numAmt.
     */
    public BigDecimal getNumAmt() {
        return numAmt;
    }

    /**
     * 设置 numAmt
     * 
     * @param numAmt numAmt
     */
    public void setNumAmt(BigDecimal numAmt) {
        this.numAmt = numAmt;
    }

    /**
     * 获取 oAmt
     * 
     * @return oAmt.
     */
    public BigDecimal getoAmt() {
        return oAmt;
    }

    /**
     * 设置 oAmt
     * 
     * @param oAmt oAmt
     */
    public void setoAmt(BigDecimal oAmt) {
        this.oAmt = oAmt;
    }

    /**
     * 获取 balFlag
     * 
     * @return balFlag.
     */
    public String getBalFlag() {
        return balFlag;
    }

    /**
     * 设置 balFlag
     * 
     * @param balFlag balFlag
     */
    public void setBalFlag(String balFlag) {
        this.balFlag = balFlag;
    }

    /**
     * 获取 subjectType
     * 
     * @return subjectType.
     */
    public String getSubjectType() {
        return subjectType;
    }

    /**
     * 设置 subjectType
     * 
     * @param subjectType subjectType
     */
    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    /**
     * 获取 subjectLevel
     * 
     * @return subjectLevel.
     */
    public Long getSubjectLevel() {
        return subjectLevel;
    }

    /**
     * 设置 subjectLevel
     * 
     * @param subjectLevel subjectLevel
     */
    public void setSubjectLevel(Long subjectLevel) {
        this.subjectLevel = subjectLevel;
    }

    /**
     * 获取 baseSubjectNature
     * 
     * @return baseSubjectNature.
     */
    public String getBaseSubjectNature() {
        return baseSubjectNature;
    }

    /**
     * 设置 baseSubjectNature
     * 
     * @param baseSubjectNature baseSubjectNature
     */
    public void setBaseSubjectNature(String baseSubjectNature) {
        this.baseSubjectNature = baseSubjectNature;
    }

    /**
     * 获取 fsubjectNo
     * 
     * @return fsubjectNo.
     */
    public String getFsubjectNo() {
        return fsubjectNo;
    }

    /**
     * 设置 fsubjectNo
     * 
     * @param fsubjectNo fsubjectNo
     */
    public void setFsubjectNo(String fsubjectNo) {
        this.fsubjectNo = fsubjectNo;
    }

    /**
     * 获取 detailSubjectNoTail
     * 
     * @return detailSubjectNoTail.
     */
    public String getDetailSubjectNoTail() {
        return detailSubjectNoTail;
    }

    /**
     * 设置 detailSubjectNoTail
     * 
     * @param detailSubjectNoTail detailSubjectNoTail
     */
    public void setDetailSubjectNoTail(String detailSubjectNoTail) {
        this.detailSubjectNoTail = detailSubjectNoTail;
    }


    public String getbCcy() {
        return bCcy;
    }

    public void setbCcy(String bCcy) {
        this.bCcy = bCcy;
    }

    public BigDecimal getbAmt() {
        return bAmt;
    }

    public void setbAmt(BigDecimal bAmt) {
        this.bAmt = bAmt;
    }

    public String getExpectBalFlag() {
        return expectBalFlag;
    }

    public void setExpectBalFlag(String expectBalFlag) {
        this.expectBalFlag = expectBalFlag;
    }

    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end
    /**
     * 获取 估值方法
     * @return 估值方法
     */
    public String getValuationMethod() {
        return valuationMethod;
    }

    /**
     * 设置 估值方法
     * @param valuationMethod 估值方法
     */
    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    /**
     * 是否使用摊余成本法
     * @return 是否使用摊余成本法
     */
    public boolean getIsAcMethod(){
        if(StringUtil.equals(DictCodeListExt1.估值方法_摊余成本法.getValue(), this.getValuationMethod())){
            return true;
        }
        else{
            return false;
        }
    }
}
