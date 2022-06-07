/*
 * 文件名：PaActiveRemind001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.remind.dto;
 
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionriskanalyze.dto.RskSupervisionRiskAnalyzeDto;

import java.util.Date;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年8月21日
 * @see PaActiveRemind001Dto
 * @since 1.0
 */

public class PaActiveRemind001Dto extends RskSupervisionRiskAnalyzeDto  {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 4335909839362986036L;
    /**
     * 菜单ID
     */
    private String menuId;
    /**
     * 提醒类型
     */
    private String remType;
    /**
     * 产品代码
     */
    private String finprodId;
    /**
     * 产品代码
     */
    private String prodId;
    /**
     * 产品名称
     */
    private String finprodName;
    /**
     * 费用名称
     */
    private String feeId;
    /**
     * 资产代码
     */
    private String assetCode;
    /**
     * 资产代码
     */
    private String assetId;
    /**
     * 资产名称
     */
    private String assetName;
    /**
     * 提醒说明
     */
    private String reminder;
    /**
     * 操作
     */
    private String operating;
    /**
     * 配置天数
     */
    private Integer days;
    /**
     * 配置URL
     */
    private String url;
    /**
     * 标题
     */
    private String lable;
    /**
     * 操作按钮
     */
    private String dealF;
    /**
     * 关闭按钮
     */
    private String closF;

    /**
     * 配置关闭日期
     */
    private Date mdate;
    
    /**
     * 评级日期
     */
    private Date odate;
    /**
     * 现金流类型，中间还本、中间收息、到期还本、到期收息、费用支付等
     */
    private String cashType;
    /**
     *机构代码
     */
    private String orgId;
    /**
     * 机构名称
     */
    private String orgName;
    
    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 组合名称
     */
    private String portfolioName;
    
    /**
     * 指标名称
     */
    private String indName;
   
    /**
     * 指标结果
     */
    private String supTrend;


    /**
     * 资金账户
     *
     */
    private String trdAcctId;

    /**
     * 资金账户名称
     *
     */
    private String trdAcctName;

    /**
     * 交易场所
     *
     */
    private String tradePlace;


    /**
     * 结果日期
     * @return
     */
    private Date cdate;

    public Date getCdate() {
        return cdate;
    }

    public String getCdateStr() {
        return DateUtil.format(cdate);
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }


    public String getTrdAcctId() {
        return trdAcctId;
    }

    public void setTrdAcctId(String trdAcctId) {
        this.trdAcctId = trdAcctId;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public String getTrdAcctName() {
        return trdAcctName;
    }

    public void setTrdAcctName(String trdAcctName) {
        this.trdAcctName = trdAcctName;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }


// add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 start
    /**
     * 计息截止日
     */
    private Date dDate;

    private String oneAttr;

    public String getOneAttr() {
        return oneAttr;
    }

    public void setOneAttr(String oneAttr) {
        this.oneAttr = oneAttr;
    }

    /**
     * 上市代码
     */
    private String appearMarketId;

    /**
     * 获取上市代码
     */
    public String getAppearMarketId() {
        return appearMarketId;
    }

    /**
     * 设置上市代码
     */
    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }
    /**
     * 计息截止日
     * @return 计息截止日
     */
    public Date getdDate() {
        return dDate;
    }

    /**
     * 计息截止日
     * @param dDate 计息截止日
     */
    public void setdDate(Date dDate) {
        this.dDate = dDate;
    }

    public String getdDateStr() {
        return DateUtil.format(dDate);
    }
    // add by jiangpeng on 20200728 债券存续期 计息截止日与支付日不同时 交易查询用 end

    /**
     * 获取 orgId
     * @return orgId.
     */
    public String getOrgId() {
        return orgId;
    }


    
    /**
     * 设置 orgId
     * @param orgId orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }


    
    /**
     * 获取 orgName
     * @return orgName.
     */
    public String getOrgName() {
        return orgName;
    }


    
    /**
     * 设置 orgName
     * @param orgName orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public String getMenuId() {
        return menuId;
    }


    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }


    public Date getOdate() {
		return odate;
	}


	public void setOdate(Date odate) {
		this.odate = odate;
	}


	/**
     * 获取 操作
     * @return operating.
     */
    public String getOperating() {
        return operating;
    }


    /**
     * 设置 操作
     * @param operating operating
     */
    public void setOperating(String operating) {
        this.operating = operating;
    }


    public String getRemType() {
        return remType;
    }


    public String getRemTypeStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.提醒类型_小分类, remType);
    }


    public void setRemType(String remType) {
        this.remType = remType;
    }


    /**
     * 获取 资产代码
     * @return assetCode.
     */
    public String getAssetCode() {
        return assetCode;
    }

    /**
     * 设置 资产代码
     * @param assetCode assetCode
     */
    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetId() {
        return assetId;
    }


    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }


    /**
     * 获取 资产名称
     * @return assetName.
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * 设置 资产名称
     * @param assetName assetName
     */
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getFinprodId() {
        return finprodId;
    }


    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }


    public String getProdId() {
        return prodId;
    }


    public void setProdId(String prodId) {
        this.prodId = prodId;
    }


    public String getFinprodName() {
        return finprodName;
    }


    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }


    public String getFeeId() {
        return feeId;
    }


    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }


    /**
     * 获取 提醒说明
     * @return reminder.
     */
    public String getReminder() {
        return reminder;
    }

    /**
     * 设置 提醒说明
     * @param reminder reminder
     */
    public void setReminder(String reminder) {
        this.reminder = reminder;
    }


    public Integer getDays() {
        return days;
    }


    public void setDays(Integer days) {
        this.days = days;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getDealF() {
        return dealF;
    }


    public void setDealF(String dealF) {
        this.dealF = dealF;
    }


    public String getClosF() {
        return closF;
    }


    public void setClosF(String closF) {
        this.closF = closF;
    }


    public String getLable() {
        return lable;
    }


    public void setLable(String lable) {
        this.lable = lable;
    }


    public Date getMdate() {
        return mdate;
    }

    public String getOdateStr() {
        return DateUtil.format(odate);
    }

    public String getMdateStr() {
        return DateUtil.format(mdate);
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getCashType() {
        return cashType;
    }

    public void setCashType(String cashType) {
        this.cashType = cashType;
    }



    
    /**
     * 获取 portfolioId
     * @return portfolioId.
     */
    public String getPortfolioId() {
        return portfolioId;
    }



    
    /**
     * 设置 portfolioId
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }



    
    /**
     * 获取 portfolioName
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }



    
    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }



    
    /**
     * 获取 indName
     * @return indName.
     */
    public String getIndName() {
        return indName;
    }



    
    /**
     * 设置 indName
     * @param indName indName
     */
    public void setIndName(String indName) {
        this.indName = indName;
    }



    
    /**
     * 获取 supTrend
     * @return supTrend.
     */
    public String getSupTrend() {
        return supTrend;
    }



    
    /**
     * 设置 supTrend
     * @param supTrend supTrend
     */
    public void setSupTrend(String supTrend) {
        this.supTrend = supTrend;
    }
    
    
}
