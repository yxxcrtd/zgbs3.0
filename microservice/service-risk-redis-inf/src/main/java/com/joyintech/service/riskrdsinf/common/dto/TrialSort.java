package com.joyintech.service.riskrdsinf.common.dto;

import org.apache.shiro.crypto.hash.Hash;

import java.util.HashMap;
import java.util.Map;

/**
 * 风险试算结果排序
 */
public class TrialSort {

    /**
     * 组合代码
     */
    private String warningLine;

    private String kriName;

    private String approveLine;

    private String kriCode;

    private String kriTypeCn;

    private Integer groupId;

    private String kriType;

    private String kriValue;

    private String kriStatus;

    private String assetName;

    private String kriExpr;

    private String kriOldExpr;

    private String oldValue;

    private String forbiddenLn;

    private String portfolioName;

    private String kriTrend;

    private String portfolioCode;

    private Integer sort;

    private Integer portfolioCodeSort;

    private Map<String,Integer> map = new HashMap();

    public String getWarningLine() {
        return warningLine;
    }

    public void setWarningLine(String warningLine) {
        this.warningLine = warningLine;
    }

    public String getKriName() {
        return kriName;
    }

    public void setKriName(String kriName) {
        this.kriName = kriName;
    }

    public String getApproveLine() {
        return approveLine;
    }

    public void setApproveLine(String approveLine) {
        this.approveLine = approveLine;
    }

    public String getKriCode() {
        return kriCode;
    }

    public void setKriCode(String kriCode) {
        this.kriCode = kriCode;
    }

    public String getKriTypeCn() {
        return kriTypeCn;
    }

    public void setKriTypeCn(String kriTypeCn) {
        this.kriTypeCn = kriTypeCn;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getKriType() {
        return kriType;
    }

    public void setKriType(String kriType) {
        this.kriType = kriType;
    }

    public String getKriValue() {
        return kriValue;
    }

    public void setKriValue(String kriValue) {
        this.kriValue = kriValue;
    }

    public String getKriStatus() {
        return kriStatus;
    }

    public void setKriStatus(String kriStatus) {
        this.kriStatus = kriStatus;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getKriExpr() {
        return kriExpr;
    }

    public void setKriExpr(String kriExpr) {
        this.kriExpr = kriExpr;
    }

    public String getKriOldExpr() {
        return kriOldExpr;
    }

    public void setKriOldExpr(String kriOldExpr) {
        this.kriOldExpr = kriOldExpr;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getForbiddenLn() {
        return forbiddenLn;
    }

    public void setForbiddenLn(String forbiddenLn) {
        this.forbiddenLn = forbiddenLn;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getKriTrend() {
        return kriTrend;
    }

    public void setKriTrend(String kriTrend) {
        this.kriTrend = kriTrend;
    }

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

    public Integer getSort() {
        return sort;
    }
    //违约>审批>预警>异常>正常
    public void setSort(Integer sort) {
        if("3".equals(this.kriStatus)) {
            this.sort = 5;
        }
        if("2".equals(this.kriStatus)) {
            this.sort = 4;
        }
        if("1".equals(this.kriStatus)) {
            this.sort = 3;
        }
        if("4".equals(this.kriStatus)) {
            this.sort = 2;
        }
        if("0".equals(this.kriStatus)) {
            this.sort = 1;
        }
    }

    public Integer getPortfolioCodeSort() {
        return portfolioCodeSort;
    }

    public void setPortfolioCodeSort(Integer portfolioCodeSort) {
        if(this.portfolioCode !=null) {
            this.portfolioCodeSort = this.portfolioCode.hashCode();
        }else {
            this.portfolioCodeSort = 0;
        }
    }
}
