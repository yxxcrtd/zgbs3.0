/*
 * 文件名：AccountEntryConfigEntity.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights
 * Reserved. 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.entity;

import com.joyintech.tams.common.base.entity.BokSubject;

import java.util.List;

/**
 * 会计分录配置信息
 * 
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see AccountEntry
 * @since 1.0
 */
public class AccountEntry {

    /**
     * 场景分支节点
     */
    private String sceneNodeId;

    /**
     * 科目代码
     */

    private String subjectId;

    /**
     * 科目名称
     */

    private String subjectName;

    
    /**
     * 借贷方向
     */

    private String cdFlag;

    /**
     * 金额元素
     */

    private String formElement;

    /**
     * 上级科目号
     */
    private String fsubjectId;
    
    /**
     * 基础科目性质
     */
    private String baseSubjectNature;

    /**
     * 科目类型
     */
    private String subjectType;

    /**
     * 是否核算数量
     */
    private String accQuaFlag;

    /**
     * 是否计息
     */
    private String accIntFlag;

    /**
     * 是否允许透支
     */
    private String overdrawnFlag;

    /**
     * 是否生成业务明细科目
     */
    private String genDetsubFlag;
    
    /**
     * 余额方向
     */
    private String balFlag;
    
    /**
     * 账务明细代码
     */
    private String bokDetailId;
    
    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 元素数据来源
     */
    private String dictSource;

    /**
     * 科目信息列表
     */
    private List<BokSubject> bokSubjectList;

    /**
     * 获取 sceneNodeId
     * 
     * @return sceneNodeId.
     */
    public String getSceneNodeId() {
        return sceneNodeId;
    }

    /**
     * 设置 sceneNodeId
     * 
     * @param sceneNodeId sceneNodeId
     */
    public void setSceneNodeId(String sceneNodeId) {
        this.sceneNodeId = sceneNodeId;
    }

    /**
     * 获取 subjectId
     * 
     * @return subjectId.
     */
    public String getSubjectId() {
        return subjectId;
    }

    /**
     * 设置 subjectId
     * 
     * @param subjectId subjectId
     */
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    
    /**
     * 获取 subjectName
     * @return subjectName.
     */
    public String getSubjectName() {
        return subjectName;
    }

    
    /**
     * 设置 subjectName
     * @param subjectName subjectName
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * 获取 cdFlag
     * 
     * @return cdFlag.
     */
    public String getCdFlag() {
        return cdFlag;
    }

    /**
     * 设置 cdFlag
     * 
     * @param cdFlag cdFlag
     */
    public void setCdFlag(String cdFlag) {
        this.cdFlag = cdFlag;
    }

    /**
     * 获取 formElement
     * 
     * @return formElement.
     */
    public String getFormElement() {
        return formElement;
    }

    /**
     * 设置 formElement
     * 
     * @param formElement formElement
     */
    public void setFormElement(String formElement) {
        this.formElement = formElement;
    }

    
    /**
     * 获取 fsubjectId
     * @return fsubjectId.
     */
    public String getFsubjectId() {
        return fsubjectId;
    }

    
    /**
     * 设置 fsubjectId
     * @param fsubjectId fsubjectId
     */
    public void setFsubjectId(String fsubjectId) {
        this.fsubjectId = fsubjectId;
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
     * 获取 accQuaFlag
     * 
     * @return accQuaFlag.
     */
    public String getAccQuaFlag() {
        return accQuaFlag;
    }

    /**
     * 设置 accQuaFlag
     * 
     * @param accQuaFlag accQuaFlag
     */
    public void setAccQuaFlag(String accQuaFlag) {
        this.accQuaFlag = accQuaFlag;
    }

    /**
     * 获取 accIntFlag
     * 
     * @return accIntFlag.
     */
    public String getAccIntFlag() {
        return accIntFlag;
    }

    /**
     * 设置 accIntFlag
     * 
     * @param accIntFlag accIntFlag
     */
    public void setAccIntFlag(String accIntFlag) {
        this.accIntFlag = accIntFlag;
    }

    /**
     * 获取 overdrawnFlag
     * 
     * @return overdrawnFlag.
     */
    public String getOverdrawnFlag() {
        return overdrawnFlag;
    }

    /**
     * 设置 overdrawnFlag
     * 
     * @param overdrawnFlag overdrawnFlag
     */
    public void setOverdrawnFlag(String overdrawnFlag) {
        this.overdrawnFlag = overdrawnFlag;
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

    public String getBokDetailId() {
        return bokDetailId;
    }

    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    
    /**
     * 获取 booksetId
     * @return booksetId.
     */
    public String getBooksetId() {
        return booksetId;
    }

    
    /**
     * 设置 booksetId
     * @param booksetId booksetId
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    
    /**
     * 获取 genDetsubFlag
     * @return genDetsubFlag.
     */
    public String getGenDetsubFlag() {
        return genDetsubFlag;
    }

    
    /**
     * 设置 genDetsubFlag
     * @param genDetsubFlag genDetsubFlag
     */
    public void setGenDetsubFlag(String genDetsubFlag) {
        this.genDetsubFlag = genDetsubFlag;
    }

    /**
     * 获取 科目信息列表
     * @return 科目信息列表
     */
    public List<BokSubject> getBokSubjectList() {
        return bokSubjectList;
    }

    /**
     * 设置 科目信息列表
     * @param bokSubjectList 科目信息列表
     */
    public void setBokSubjectList(List<BokSubject> bokSubjectList) {
        this.bokSubjectList = bokSubjectList;
    }

    /**
     * 获取 元素数据来源
     * @return 元素数据来源
     */
    public String getDictSource() {
        return dictSource;
    }

    /**
     * 设置 元素数据来源
     * @param dictSource 元素数据来源
     */
    public void setDictSource(String dictSource) {
        this.dictSource = dictSource;
    }
}
