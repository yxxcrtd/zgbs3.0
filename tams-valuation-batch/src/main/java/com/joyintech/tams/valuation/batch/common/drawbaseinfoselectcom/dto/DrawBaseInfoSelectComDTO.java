/*
 * 文件名：TodoDeal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 计提基础信息DTO 〈功能详细描述〉
 *
 * @author wudebao
 * @version 1.0 2018年5月23日
 * @see DrawBaseInfoSelectComDTO
 * @since 1.0
 */
public class DrawBaseInfoSelectComDTO {

    /**
     * 任务代码
     */
    private String taskId;

    /**
     * 任务优先级
     */
    private Long taskPri;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 账套日期
     */
    private Date booksetDate;

    /**
     * 业务明细代码(金融产品代码)
     */
    private String busiId;

    /**
     * 业务明细类型（金融产品类型）
     */
    private String busiDetailType;

    /**
     * 分支序号
     */
    private Long branch;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 原币
     */
    private String oCcy;

    /**
     * 余额方向（发生日）
     */
    private String balFlagHappenDate;

    /**
     * 科目余额
     */
    private BigDecimal oAmtHappenDate;

    /**
     * 数量余额
     */
    private BigDecimal numAmtHappenDate;

    /**
     * 发生日
     */
    private Date happenDate;

    /**
     * 基础科目性质
     */
    private String baseSubjectNature;

    /**
     * 本位币
     */
    private String standardCcy;

    /**
     * 结息日
     */
    private String mdate;

    /**
     * 是否倒起息
     */
    private String isBackValue;
    
    /**
     * 减值阶段3数量
     * add by caoguo on 20201031
     */
    private Long numP3;

    /**
     *  辅助核算项配置信息
     */
    private SubjectMappingConfig subjectMappingConfig;

    /**
     * 估值方法
     */
    private String valuationMethod;

    /**
     * 获取 taskId
     *
     * @return taskId.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 获取 taskPri
     *
     * @return taskPri.
     */
    public Long getTaskPri() {
        return taskPri;
    }

    /**
     * 设置 taskPri
     *
     * @param taskPri taskPri
     */
    public void setTaskPri(Long taskPri) {
        this.taskPri = taskPri;
    }

    /**
     * 获取 booksetId
     *
     * @return booksetId.
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 获取 booksetDate
     *
     * @return booksetDate.
     */
    public Date getBooksetDate() {
        return booksetDate;
    }

    /**
     * 获取 busiId
     *
     * @return busiId.
     */
    public String getBusiId() {
        return busiId;
    }

    /**
     * 获取 busiDetailType
     *
     * @return busiDetailType.
     */
    public String getBusiDetailType() {
        return busiDetailType;
    }

    /**
     * 获取 branch
     *
     * @return branch.
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 获取 bokDetailId
     *
     * @return bokDetailId.
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 获取 invAim
     *
     * @return invAim.
     */
    public String getInvAim() {
        return invAim;
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
     * 获取 balFlagHappenDate
     *
     * @return balFlagHappenDate.
     */
    public String getBalFlagHappenDate() {
        return balFlagHappenDate;
    }

    /**
     * 获取 oAmtHappenDate
     *
     * @return oAmtHappenDate.
     */
    public BigDecimal getoAmtHappenDate() {
        return oAmtHappenDate;
    }

    /**
     * 获取 numAmtHappenDate
     *
     * @return numAmtHappenDate.
     */
    public BigDecimal getNumAmtHappenDate() {
        return numAmtHappenDate;
    }

    /**
     * 获取 happenDate
     *
     * @return happenDate.
     */
    public Date getHappenDate() {
        return happenDate;
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
     * 获取 standardCcy
     *
     * @return standardCcy.
     */
    public String getStandardCcy() {
        return standardCcy;
    }

    /**
     * 设置 taskId
     *
     * @param taskId taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
     * 设置 booksetDate
     *
     * @param booksetDate booksetDate
     */
    public void setBooksetDate(Date booksetDate) {
        this.booksetDate = booksetDate;
    }

    /**
     * 设置 busiId
     *
     * @param busiId busiId
     */
    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    /**
     * 设置 busiDetailType
     *
     * @param busiDetailType busiDetailType
     */
    public void setBusiDetailType(String busiDetailType) {
        this.busiDetailType = busiDetailType;
    }

    /**
     * 设置 branch
     *
     * @param branch branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 设置 bokDetailId
     *
     * @param bokDetailId bokDetailId
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    /**
     * 设置 invAim
     *
     * @param invAim invAim
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
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
     * 设置 balFlagHappenDate
     *
     * @param balFlagHappenDate balFlagHappenDate
     */
    public void setBalFlagHappenDate(String balFlagHappenDate) {
        this.balFlagHappenDate = balFlagHappenDate;
    }

    /**
     * 设置 oAmtHappenDate
     *
     * @param oAmtHappenDate oAmtHappenDate
     */
    public void setoAmtHappenDate(BigDecimal oAmtHappenDate) {
        this.oAmtHappenDate = oAmtHappenDate;
    }

    /**
     * 设置 numAmtHappenDate
     *
     * @param numAmtHappenDate numAmtHappenDate
     */
    public void setNumAmtHappenDate(BigDecimal numAmtHappenDate) {
        this.numAmtHappenDate = numAmtHappenDate;
    }

    /**
     * 设置 happenDate
     *
     * @param happenDate happenDate
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
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
     * 设置 standardCcy
     *
     * @param standardCcy standardCcy
     */
    public void setStandardCcy(String standardCcy) {
        this.standardCcy = standardCcy;
    }

    /**
     * 获取 mdate
     * 
     * @return mdate.
     */
    public String getMdate() {
        return mdate;
    }

    /**
     * 获取 isBackValue
     * 
     * @return isBackValue.
     */
    public String getIsBackValue() {
        return isBackValue;
    }

    /**
     * 设置 mdate
     * 
     * @param mdate mdate
     */
    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    /**
     * 设置 isBackValue
     * 
     * @param isBackValue isBackValue
     */
    public void setIsBackValue(String isBackValue) {
        this.isBackValue = isBackValue;
    }

    
    /**
     * 获取 numP3
     * @return numP3.
     */
    public Long getNumP3() {
        return numP3;
    }

    
    /**
     * 设置 numP3
     * @param numP3 numP3
     */
    public void setNumP3(Long numP3) {
        this.numP3 = numP3;
    }

    // add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
    /**
     * 获取 辅助核算项配置信息
     * @return 辅助核算项配置信息.
     */
    public SubjectMappingConfig getSubjectMappingConfig() {
        return subjectMappingConfig;
    }

    /**
     * 设置 辅助核算项配置信息
     * @param subjectMappingConfig 辅助核算项配置信息
     */
    public void setSubjectMappingConfig(SubjectMappingConfig subjectMappingConfig) {
        this.subjectMappingConfig = subjectMappingConfig;
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
