/*
 * 文件名：AccountsAdjustAccountEntry004Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.service.valuation.accounadjustmanage.accountentry.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;

/**
 * 会计分录详情Dto
 * @author weipeng
 * @version 1.0 2018年7月18日
 * @see ValAccountsAdjustAccountEntry004Dto
 * @since 1.0
 */
public class ValAccountsAdjustAccountEntry004Dto extends BaseEntity{
    
    private static final long serialVersionUID = 1L;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 场景分支节点
     */
    private String sceneNodeId;

    /**
     * 科目号
     */
    private String subjectNo;
    
    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 余额方向
     */
    private String cdFlag;
    
    /**
     * 金额元素
     */
    private String formEle;

    /**
     * 账务明细元素
     */
    private String bokDetailId;

    /**
     * 记账网点
     */
    private String accountNet;
    
    private String dictName;

    private String dictCode;
    
    private String fsceneNodeid;

    /**
     * @author  殷亚
     * @description  元素数据来源
     * @date 2022-2-17 15:54
     */
    private String dictSource;

    public String getDictSource() {
        return dictSource;
    }

    public void setDictSource(String dictSource) {
        this.dictSource = dictSource;
    }

    /**
     * 取得 账套代码
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId == null ? null : booksetId.trim();
    }

    /**
     * 取得 场景分支节点
     * @return 场景分支节点
     */
    public String getSceneNodeId() {
        return sceneNodeId;
    }

    /**
     * 设置 场景分支节点
     * @param sceneNodeId 场景分支节点
     */
    public void setSceneNodeId(String sceneNodeId) {
        this.sceneNodeId = sceneNodeId == null ? null : sceneNodeId.trim();
    }

    /**
     * 取得 科目号
     * @return 科目号
     */
    public String getSubjectNo() {
        return subjectNo;
    }

    /**
     * 设置 科目号
     * @param subjectNo 科目号
     */
    public void setSubjectNo(String subjectNo) {
        this.subjectNo = subjectNo == null ? null : subjectNo.trim();
    }

    /**
     * 取得 余额方向
     * @return 余额方向
     */
    public String getCdFlag() {
        return this.cdFlag;
    }

    /**
     * 设置 余额方向
     * @param cdFlag 余额方向
     */
    public void setCdFlag(String cdFlag) {
        this.cdFlag = cdFlag == null ? null : cdFlag.trim();
    }
    
    /**
     * 取得 金额元素
     * @return 金额元素
     */
    public String getFormEle() {
        return formEle;
    }

    /**
     * 设置 金额元素
     * @param formEle 金额元素
     */
    public void setFormEle(String formEle) {
        this.formEle = formEle == null ? null : formEle.trim();
    }

    /**
     * 取得 账务明细元素
     * @return 账务明细元素
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 设置 账务明细元素
     * @param bokDetailId 账务明细元素
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId == null ? null : bokDetailId.trim();
    }

    /**
     * 取得 记账网点
     * @return 记账网点
     */
    public String getAccountNet() {
        return accountNet;
    }

    /**
     * 设置 记账网点
     * @param accountNet 记账网点
     */
    public void setAccountNet(String accountNet) {
        this.accountNet = accountNet == null ? null : accountNet.trim();
    }

    
    /**
     * 获取  科目名称
     * @return  科目名称
     */
    public String getSubjectName() {
        return subjectName;
    }

    
    /**
     * 设置 科目名称
     * @param subjectName  科目名称
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    
    /**
     * 获取 dictName
     * @return dictName.
     */
    public String getDictName() {
        return dictName;
    }

    
    /**
     * 设置 dictName
     * @param dictName dictName
     */
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    /**
     * 获取 fsceneNodeid
     * @return fsceneNodeid.
     */
    public String getFsceneNodeid() {
        return fsceneNodeid;
    }

    
    /**
     * 设置 fsceneNodeid
     * @param fsceneNodeid fsceneNodeid
     */
    public void setFsceneNodeid(String fsceneNodeid) {
        this.fsceneNodeid = fsceneNodeid;
    }
    
    
}
