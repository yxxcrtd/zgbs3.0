/*
 * 文件名：ImplPortf.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年10月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.redisinit.dto;

import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdInfo;

import java.math.BigDecimal;

/**
 * 组合信息实现类
 * @author 裴家俊
 * @version 1.0 2019年10月2日
 * @see ImplPortf
 * @since 1.0
 */
public class ImplPortf extends PtlPortfolio implements IProdInfo {

    private String openMode;//开放频率，高频

    private BigDecimal pcbAmout;//业绩比较基准

    private String investMgr;//投资经理

    private String calendarId;//交易日历

    private String investNature;//投资性质

    /**
     * 募集方式
     */
    private String raiseType;

    private String openType;//开放类型，开放封闭

    @Override
    public String getProdId() {
        return this.getPortfolioId();//组合代码
    }

    @Override
    public String getProdName() {
        return this.getPortfolioName();//组合名称
    }

    @Override
    public String getInvestType() {
        return this.getInvestNature();//投资性质
    }

    @Override
    public String getOpenType() {
        return this.openMode;//开放频率
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    @Override
    public String getIssueMode() {
        return this.openType;//开放类型
    }

    @Override
    public String getProdType() {
        return this.getProfitType();//收益类型
    }

    @Override
    public BigDecimal getPcbAmt() {
        return pcbAmout;
    }

    @Override
    public String getCalendarCode() {
        return calendarId;
    }

    @Override
    public String getInvestMgr() {
        return investMgr;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public BigDecimal getPcbAmout() {
        return pcbAmout;
    }

    public void setPcbAmout(BigDecimal pcbAmout) {
        this.pcbAmout = pcbAmout;
    }

    public void setInvestMgr(String investMgr) {
        this.investMgr = investMgr;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getInvestNature() {
        return investNature;
    }

    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }

    @Override
    public String getRaiseType() {
        return raiseType;
    }

    public void setRaiseType(String raiseType) {
        this.raiseType = raiseType;
    }

    /**
     * 建仓期
     */
    private Long buildPeriod;

    /**
     * 取得 建仓期
     * @return 建仓期
     */
    public Long getBuildPeriod() {
        return buildPeriod;
    }

    /**
     * 设置 建仓期
     * @param buildPeriod 建仓期
     */
    public void setBuildPeriod(Long buildPeriod) {
        this.buildPeriod = buildPeriod;
    }
}
