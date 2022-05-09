package com.joyintech.tams.component.business.riskrds.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;

import java.math.BigDecimal;

/**
 * 委托基础实体(委托符合EtrEntrust实体的那些类)
 * @author 裴家俊
 *
 */
public class EntrustBaseDto extends EtrEntrust {

	private static final long serialVersionUID = 1806018338501643163L;

	/**
	 * 子组合代码
	 */
	private String actPortfolioId;

	/**
	 * 回购/拆借方向，正回购、逆回购、拆入、拆出
	 */
	private String busiPs;

	/**
	 * 名义本金(拆借专用)
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal prin;

	/**
	 * 首次结算金额(买断式专用)
	 */
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal fSettleAmt;

	public String getActPortfolioId() {
		return actPortfolioId;
	}

	public void setActPortfolioId(String actPortfolioId) {
		this.actPortfolioId = actPortfolioId;
	}

	public String getBusiPs() {
		return busiPs;
	}

	public void setBusiPs(String busiPs) {
		this.busiPs = busiPs;
	}

	public BigDecimal getPrin() {
		return prin;
	}

	public void setPrin(BigDecimal prin) {
		this.prin = prin;
	}

	public BigDecimal getfSettleAmt() {
		return fSettleAmt;
	}

	public void setfSettleAmt(BigDecimal fSettleAmt) {
		this.fSettleAmt = fSettleAmt;
	}

	private BussRepoSecDto bussRepoSec;

	public BussRepoSecDto getBussRepoSec() {
		return bussRepoSec;
	}

	public void setBussRepoSec(BussRepoSecDto bussRepoSec) {
		this.bussRepoSec = bussRepoSec;
	}
}
