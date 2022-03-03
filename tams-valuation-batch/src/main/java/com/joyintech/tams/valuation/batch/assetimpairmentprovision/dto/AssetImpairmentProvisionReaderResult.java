/*
 * 文件名：AssetIntdateCarryoverReaderResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年7月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.TrdAssetLoss;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;

import java.util.Date;
import java.util.List;


/**
 * reader返回值
 * @author chufengjie
 * @version 1.0 2020年10月13日
 * @see AssetImpairmentProvisionReaderResult
 * @since 1.0
 */
public class AssetImpairmentProvisionReaderResult extends NeedOperateEntity{
    
    /**
     * 账套代码
     */
    private String booksetId;
   
    /**
     * 发生日
     */
    private Date happenDate;
    
    /**
     * 入账日
     */
    private Date bookDate;
    
    /**
     * 账套日
     */
    private Date booksetDate;
    
    /**
     * 账务明细代码
     */
    private String bokDetailId;
    
    /**
     * 业务明细代码
     */
    private String busiId;
    /**
     * 本位币
     */
    private String standardCcy;
    
    /**
     * 减值行情列表
     */
    private List<TrdAssetLoss> trdAssetLossList;
    /**
     * 投资目的
     */
    private String invAim;
    /**
     * 会计场景
     */
    private TreeNode bookSetTreeNode;

	// add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 start
	/**
	 *  辅助核算项配置信息
	 */
	private SubjectMappingConfig subjectMappingConfig;
	// add by weipeng on 20220301 会计引擎升级改造，增加辅助核算项查询 end

	public String getBooksetId() {
		return booksetId;
	}

	public void setBooksetId(String booksetId) {
		this.booksetId = booksetId;
	}

	public Date getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(Date happenDate) {
		this.happenDate = happenDate;
	}

	public Date getBookDate() {
		return bookDate;
	}

	public void setBookDate(Date bookDate) {
		this.bookDate = bookDate;
	}

	public Date getBooksetDate() {
		return booksetDate;
	}

	public void setBooksetDate(Date booksetDate) {
		this.booksetDate = booksetDate;
	}

	public String getBokDetailId() {
		return bokDetailId;
	}

	public void setBokDetailId(String bokDetailId) {
		this.bokDetailId = bokDetailId;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}


	public String getStandardCcy() {
		return standardCcy;
	}

	public void setStandardCcy(String standardCcy) {
		this.standardCcy = standardCcy;
	}

	public String getInvAim() {
		return invAim;
	}

	public void setInvAim(String invAim) {
		this.invAim = invAim;
	}

    public List<TrdAssetLoss> getTrdAssetLossList() {
        return trdAssetLossList;
    }

    public void setTrdAssetLossList(List<TrdAssetLoss> trdAssetLossList) {
        this.trdAssetLossList = trdAssetLossList;
    }

    public TreeNode getBookSetTreeNode() {
        return bookSetTreeNode;
    }

    public void setBookSetTreeNode(TreeNode bookSetTreeNode) {
        this.bookSetTreeNode = bookSetTreeNode;
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
}
