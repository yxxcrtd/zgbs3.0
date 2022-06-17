package com.joyintech.background.component.order.exchange.database.entity;

import java.math.BigDecimal;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;

public class EtrPledgeComponent  extends BaseEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = -487538125526493753L;

	/**
     * 委托代码
     */
    private String entrustId;

    /**
     * 金融产品代码
     */
    private String finprodId;
    /**
     * 质押面值
     */

    private BigDecimal pledgeFaceValue;

    /**
     * 质押数量
     */

    private BigDecimal pledgeNumber;

    /**
     * 质押率
     */

    private BigDecimal pledgeRatio;

    /**
     * 质押金额
     */

    private BigDecimal pledgeAmt;

    /**
     * 取得 质押面值
     * @return 质押面值
     */
    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    /**
     * 设置 质押面值
     * @param pledgeFaceValue 质押面值
     */
    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    /**
     * 取得 质押数量
     * @return 质押数量
     */
    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    /**
     * 设置 质押数量
     * @param pledgeNumber 质押数量
     */
    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    /**
     * 取得 质押率
     * @return 质押率
     */
    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    /**
     * 设置 质押率
     * @param pledgeRatio 质押率
     */
    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    /**
     * 取得 质押金额
     * @return 质押金额
     */
    public BigDecimal getPledgeAmt() {
        return pledgeAmt;
    }

    /**
     * 设置 质押金额
     * @param pledgeAmt 质押金额
     */
    public void setPledgeAmt(BigDecimal pledgeAmt) {
        this.pledgeAmt = pledgeAmt;
    }

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	private String displaceDir;

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }
}
