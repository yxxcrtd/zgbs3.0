package com.joyintech.tams.invest.common.entrust.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;

/**
 * 委托共通dto
 */
public class EntrustCommonDto {
    /**
     * 委托信息
     */
    private EtrEntrust etrEntrust;
    
    /**
     * 批处理执行时传入
     */
    private Date executeDate;
    
    /**
     * 现金交割日
     */
    private Date settleDate;
    /**
     * 委托附加信息
     */
    private EtrEntrustAdd etrEntrustAdd;

    private BussRepoSecDto bussRepoSecDto;

    private EtrEntrustWw etrEntrustWw;

    private EtrEntrustWwDetail etrEntrustWwDetail;

    private List<EtrEntrustDetail> etrEntrustDetailList;

    private String isExistNew;

    private BigDecimal forcedFinishAmt;

    /**
     * 是否交收失败
     */
    private String isSettleFail;

    private TrdProductDeal deal;

    /**
     * 是否关闭指令
     */
    private boolean isCloseEtr;

    public BussRepoSecDto getBussRepoSecDto() {
        return bussRepoSecDto;
    }

    public void setBussRepoSecDto(BussRepoSecDto bussRepoSecDto) {
        this.bussRepoSecDto = bussRepoSecDto;
    }

    public EtrEntrust getEtrEntrust() {
        return etrEntrust;
    }

    public void setEtrEntrust(EtrEntrust etrEntrust) {
        this.etrEntrust = etrEntrust;
    }

    public EtrEntrustAdd getEtrEntrustAdd() {
        return etrEntrustAdd;
    }

    public void setEtrEntrustAdd(EtrEntrustAdd etrEntrustAdd) {
        this.etrEntrustAdd = etrEntrustAdd;
    }

	public Date getExecuteDate() {
		return executeDate;
	}

	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

    public EtrEntrustWw getEtrEntrustWw() {
        return etrEntrustWw;
    }

    public void setEtrEntrustWw(EtrEntrustWw etrEntrustWw) {
        this.etrEntrustWw = etrEntrustWw;
    }

    public EtrEntrustWwDetail getEtrEntrustWwDetail() {
        return etrEntrustWwDetail;
    }

    public void setEtrEntrustWwDetail(EtrEntrustWwDetail etrEntrustWwDetail) {
        this.etrEntrustWwDetail = etrEntrustWwDetail;
    }

    public List<EtrEntrustDetail> getEtrEntrustDetailList() {
        return etrEntrustDetailList;
    }

    public void setEtrEntrustDetailList(List<EtrEntrustDetail> etrEntrustDetailList) {
        this.etrEntrustDetailList = etrEntrustDetailList;
    }

    public String getIsExistNew() {
        return isExistNew;
    }

    public void setIsExistNew(String isExistNew) {
        this.isExistNew = isExistNew;
    }

    public BigDecimal getForcedFinishAmt() {
        return forcedFinishAmt;
    }

    public void setForcedFinishAmt(BigDecimal forcedFinishAmt) {
        this.forcedFinishAmt = forcedFinishAmt;
    }

    /**
     * 是否交收失败
     */
    public String getIsSettleFail() {
        return isSettleFail;
    }

    /**
     * 是否交收失败
     */
    public void setIsSettleFail(String isSettleFail) {
        this.isSettleFail = isSettleFail;
    }

    public TrdProductDeal getDeal() {
        return deal;
    }

    public void setDeal(TrdProductDeal deal) {
        this.deal = deal;
    }

    /**
     * 是否关闭指令
     */
    public boolean isCloseEtr() {
        return isCloseEtr;
    }

    /**
     * 是否关闭指令
     */
    public void setCloseEtr(boolean closeEtr) {
        isCloseEtr = closeEtr;
    }
}
