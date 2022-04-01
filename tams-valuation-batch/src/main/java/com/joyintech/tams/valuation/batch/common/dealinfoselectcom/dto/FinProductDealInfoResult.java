/*
 * 文件名：FinProductDealInfoResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易信息结果DTO 〈功能详细描述〉
 *
 * @author wudebao
 * @version 1.0 2018年5月5日
 * @see FinProductDealInfoResult
 * @since 1.0
 */
public class FinProductDealInfoResult extends NeedOperateEntity {

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 账套日期
     */
    private Date booksetDate;

    /**
     * 发生日期
     */
    private Date happenDate;

    /**
     * 入账日期
     */
    private Date bookDate;

    /**
     * 本位币
     */
    private String standardCcy;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 会计场景
     */
    private TreeNode bookSetTreeNode;

    /**
     * 会计准则
     */
    private String acctStandard;

    /**
     * 现金管理类
     */
    private String isCashManage;

    /**
     * 交易基本信息列表
     */
    private List<FinProductDealInfoComDTO> listFinProductDealInfoComDTO = new ArrayList<FinProductDealInfoComDTO>();
    
    /**
     * 减值阶段
     * add by wujie on 20201017 remark:资产减值
     */
    private String stage;

    /**
     * 取得 账套代码
     *
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     *
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 获取 busiType
     *
     * @return busiType.
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 busiType
     *
     * @param busiType busiType
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    /**
     * 取得 账务明细代码
     *
     * @return 账务明细代码
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 设置 账务明细代码
     *
     * @param bokDetailId 账务明细代码
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    /**
     * 取得 账套日期
     *
     * @return 账套日期
     */
    public Date getBooksetDate() {
        return booksetDate;
    }

    /**
     * 设置 账套日期
     *
     * @param booksetDate 账套日期
     */
    public void setBooksetDate(Date booksetDate) {
        this.booksetDate = booksetDate;
    }

    /**
     * 取得 发生日期
     *
     * @return 发生日期
     */
    public Date getHappenDate() {
        return happenDate;
    }

    /**
     * 设置 发生日期
     *
     * @param happenDate 发生日期
     */
    public void setHappenDate(Date happenDate) {
        this.happenDate = happenDate;
    }

    /**
     * 取得 入账日期
     *
     * @return 入账日期
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * 设置 入账日期
     *
     * @param bookDate 入账日期
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * 取得 本位币
     *
     * @return 本位币
     */
    public String getStandardCcy() {
        return standardCcy;
    }

    /**
     * 设置 本位币
     *
     * @param standardCcy 本位币
     */
    public void setStandardCcy(String standardCcy) {
        this.standardCcy = standardCcy;
    }

    /**
     * 取得 投资目的
     *
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     *
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 取得 会计场景
     *
     * @return 会计场景
     */
    public TreeNode getBookSetTreeNode() {
        return bookSetTreeNode;
    }

    /**
     * 设置 会计场景
     *
     * @param bookSetTreeNode 会计场景
     */
    public void setBookSetTreeNode(TreeNode bookSetTreeNode) {
        this.bookSetTreeNode = bookSetTreeNode;
    }

    /**
     * 获取 acctStandard
     *
     * @return acctStandard.
     */
    public String getAcctStandard() {
        return acctStandard;
    }

    /**
     * 设置 acctStandard
     *
     * @param acctStandard acctStandard
     */
    public void setAcctStandard(String acctStandard) {
        this.acctStandard = acctStandard;
    }

    /**
     * 取得 交易基本信息列表
     *
     * @return 交易基本信息列表
     */
    public List<FinProductDealInfoComDTO> getListFinProductDealInfoComDTO() {
        return listFinProductDealInfoComDTO;
    }

    /**
     * 设置 交易基本信息列表
     *
     * @param listFinProductDealInfoComDTO 交易基本信息列表
     */
    public void setListFinProductDealInfoComDTO(List<FinProductDealInfoComDTO> listFinProductDealInfoComDTO) {
        this.listFinProductDealInfoComDTO = listFinProductDealInfoComDTO;
    }

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

    public String getIsCashManage() {
        return isCashManage;
    }

    public void setIsCashManage(String isCashManage) {
        this.isCashManage = isCashManage;
    }
}
