/*
 * 文件名：PrdCashEntity.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年6月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.accounting.entity;

import java.util.Date;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;

/**
 * 待计提金融产品现金流- 实体类
 * 
 * @author chengzhong
 * @version 1.0 2018年6月2日
 * @see PrdCashEntity
 * @since 1.0
 */
public class PrdCashEntity extends NeedOperateEntity {
    /* @Author 殷亚
     * @Description 增加 BokDetailId实体类属性
     * @Date  2022-3-11 14:26
     * @Version 1.0
     * @Param
     * @Return
     **/
    private BokDetailId bokDetailIdEntity;

    public BokDetailId getBokDetailIdEntity() {
        return bokDetailIdEntity;
    }

    public void setBokDetailIdEntity(BokDetailId bokDetailIdEntity) {
        this.bokDetailIdEntity = bokDetailIdEntity;
    }
    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 账务明细代码
     */
    private String bokDetailId;

    /**
     * 起息日
     */
    private Date vdate;

//    /**
//     * 交易场所
//     */
//    private String tradeMarket;

    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 实际到期日
     */
    private Date actmdate;

    /**
     * 现金流代码
     */
    private String cashId;

    /**
     * 现金流一级类型
     */
    private String cashType1;

    /**
     * 现金流二级类型
     */
    private String cashType2;

    /**
     * 帐套代码
     */
    private String booksetId;

    /**
     * 帐套日期
     */
    private Date booksetDate;

    /**
     * 入账日期
     */
    private Date bookDate;

    /**
     * 账套名称
     */
    private String booksetName;

    /**
     * 账套类别
     */
    private String booksetType;

    /**
     * 核算主体代码
     */
    private String acctSubjectId;

    /**
     * 核算主体类别
     */
    private String acctSubjectType;

    /**
     * 本位币
     */
    private String standardCcy;

    /**
     * 模板账套代码
     */
    private String booksetIdTmpl;

    /**
     * 会计准则
     */
    private String acctStandard;

    /**
     * 账套开始日
     */
    private Date startDate;

    /**
     * 账套结束日
     */
    private Date endDate;

    /**
     * 会计场景
     */
    private TreeNode bookSetTreeNode;

    /**
     * 任务优先级
     */
    private Long taskPri;
    
    /**add by dongjun on 20190220 Remark:核算方式 start **/
    /**
     * 核算方式
     */
    private String acctMode;
    
    /**
     * 
     * @return 核算方式
     */
    public String getAcctMode() {
		return acctMode;
	}

    /**
     * 
     * @param acctMode 核算方式
     */
	public void setAcctMode(String acctMode) {
		this.acctMode = acctMode;
	}
	/**add by dongjun on 20190220 Remark:核算方式 end **/
	

    /**
     * 取得 金融产品代码
     * 
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * 
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 取得 金融产品类型
     * 
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * 
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    /**
     * 取得 起息日
     * 
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     * 
     * @param vdate 起息日
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

//    /**
//     * 获取 交易场所
//     * 
//     * @return 交易场所
//     */
//    public String getTradeMarket() {
//        return tradeMarket;
//    }
//
//    /**
//     * 设置 交易场所
//     * 
//     * @param tradeMarket 交易场所
//     */
//    public void setTradeMarket(String tradeMarket) {
//        this.tradeMarket = tradeMarket;
//    }

    /**
     * 获取 交易日历
     * 
     * @return 交易日历
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 交易日历
     * 
     * @param calendarId 交易日历
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 取得 实际到期日
     * 
     * @return 实际到期日
     */
    public Date getActmdate() {
        return actmdate;
    }

    /**
     * 设置 实际到期日
     * 
     * @param actmdate 实际到期日
     */
    public void setActmdate(Date actmdate) {
        this.actmdate = actmdate;
    }

    /**
     * 取得 现金流代码
     * 
     * @return 现金流代码
     */
    public String getCashId() {
        return cashId;
    }

    /**
     * 设置 现金流代码
     * 
     * @param cashId 现金流代码
     */
    public void setCashId(String cashId) {
        this.cashId = cashId;
    }

    /**
     * 取得 现金流一级类型
     * 
     * @return 现金流一级类型
     */
    public String getCashType1() {
        return cashType1;
    }

    /**
     * 设置 现金流一级类型
     * 
     * @param cashType1 现金流一级类型
     */
    public void setCashType1(String cashType1) {
        this.cashType1 = cashType1;
    }

    /**
     * 取得 现金流二级类型
     * 
     * @return 现金流二级类型
     */
    public String getCashType2() {
        return cashType2;
    }

    /**
     * 设置 现金流二级类型
     * 
     * @param cashType2 现金流二级类型
     */
    public void setCashType2(String cashType2) {
        this.cashType2 = cashType2;
    }

    /**
     * 取得 帐套代码
     * 
     * @return 帐套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 帐套代码
     * 
     * @param booksetId 帐套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    /**
     * 取得 帐套日期
     * 
     * @return 帐套日期
     */
    public Date getBooksetDate() {
        return booksetDate;
    }

    /**
     * 设置 帐套日期
     * 
     * @param booksetDate 帐套日期
     */
    public void setBooksetDate(Date booksetDate) {
        this.booksetDate = booksetDate;
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
     * 取得 账套名称
     * 
     * @return 账套名称
     */
    public String getBooksetName() {
        return booksetName;
    }

    /**
     * 设置 账套名称
     * 
     * @param booksetName 账套名称
     */
    public void setBooksetName(String booksetName) {
        this.booksetName = booksetName;
    }

    /**
     * 取得 账套类别
     * 
     * @return 账套类别
     */
    public String getBooksetType() {
        return booksetType;
    }

    /**
     * 设置 账套类别
     * 
     * @param booksetType 账套类别
     */
    public void setBooksetType(String booksetType) {
        this.booksetType = booksetType;
    }

    /**
     * 取得 核算主体代码
     * 
     * @return 核算主体代码
     */
    public String getAcctSubjectId() {
        return acctSubjectId;
    }

    /**
     * 设置 核算主体代码
     * 
     * @param acctSubjectId 核算主体代码
     */
    public void setAcctSubjectId(String acctSubjectId) {
        this.acctSubjectId = acctSubjectId;
    }

    /**
     * 取得 核算主体类别
     * 
     * @return 核算主体类别
     */
    public String getAcctSubjectType() {
        return acctSubjectType;
    }

    /**
     * 设置 核算主体类别
     * 
     * @param acctSubjectType 核算主体类别
     */
    public void setAcctSubjectType(String acctSubjectType) {
        this.acctSubjectType = acctSubjectType;
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
     * 取得 模板账套代码
     * 
     * @return 模板账套代码
     */
    public String getBooksetIdTmpl() {
        return booksetIdTmpl;
    }

    /**
     * 设置 模板账套代码
     * 
     * @param booksetIdTmpl 模板账套代码
     */
    public void setBooksetIdTmpl(String booksetIdTmpl) {
        this.booksetIdTmpl = booksetIdTmpl;
    }

    /**
     * 取得 会计准则
     * 
     * @return 会计准则
     */
    public String getAcctStandard() {
        return acctStandard;
    }

    /**
     * 设置 会计准则
     * 
     * @param acctStandard 会计准则
     */
    public void setAcctStandard(String acctStandard) {
        this.acctStandard = acctStandard;
    }

    /**
     * 取得 账套开始日
     * 
     * @return 账套开始日
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置 账套开始日
     * 
     * @param startDate 账套开始日
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 取得 账套结束日
     * 
     * @return 账套结束日
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 设置 账套结束日
     * 
     * @param endDate 账套结束日
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 设置 账务明细代码
     * 
     * @param bokDetailId 账务明细代码
     */
    public String getBokDetailId() {
        return bokDetailId;
    }

    /**
     * 取得 账务明细代码
     * 
     * @param bokDetailId 账务明细代码
     */
    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    /**
     * 获取 会计场景
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

}