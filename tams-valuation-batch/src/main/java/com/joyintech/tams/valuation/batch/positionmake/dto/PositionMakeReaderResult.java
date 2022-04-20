package com.joyintech.tams.valuation.batch.positionmake.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.common.base.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 头寸生成批处理 - reader返回值DTO
 * 
 * @author Administrator
 *
 */
public class PositionMakeReaderResult extends NeedOperateEntity  {

	private String booksetId;

	/**
	 * 组合资产估值List
	 */
	private List<PtlSecValution> ptlSecValutionList = new ArrayList<PtlSecValution>();

	/**
	 * 产品费用头寸List
	 */
	private List<BokPositionPrdfee> bokPositionPrdfeeList = new ArrayList<BokPositionPrdfee>();

	/**
	 * 产品头寸List
	 */
	private List<BokPositionPrd> bokPositionPrdList = new ArrayList<BokPositionPrd>();

	/**
	 * 组合费用头寸
	 */
	private List<PtlFeePosition> ptlFeePositionList = new ArrayList<PtlFeePosition>();

	/**
	 * 组合估值头寸List
	 */
	private List<PtlPortfolioValuation> ptlPortfolioValuationList = new ArrayList<PtlPortfolioValuation>();

	/**
	 * 组合资产应收款头寸List
	 */
	private List<PtlSecReceivables> ptlSecReceivablesList = new ArrayList<PtlSecReceivables>();
	

	public List<PtlSecValution> getPtlSecValutionList() {
		return ptlSecValutionList;
	}

	public void setPtlSecValutionList(List<PtlSecValution> ptlSecValutionList) {
		this.ptlSecValutionList = ptlSecValutionList;
	}

	public List<BokPositionPrdfee> getBokPositionPrdfeeList() {
		return bokPositionPrdfeeList;
	}

	public void setBokPositionPrdfeeList(List<BokPositionPrdfee> bokPositionPrdfeeList) {
		this.bokPositionPrdfeeList = bokPositionPrdfeeList;
	}

	public List<BokPositionPrd> getBokPositionPrdList() {
		return bokPositionPrdList;
	}

	public void setBokPositionPrdList(List<BokPositionPrd> bokPositionPrdList) {
		this.bokPositionPrdList = bokPositionPrdList;
	}

	public List<PtlFeePosition> getPtlFeePositionList() {
		return ptlFeePositionList;
	}

	public void setPtlFeePositionList(List<PtlFeePosition> ptlFeePositionList) {
		this.ptlFeePositionList = ptlFeePositionList;
	}

	public List<PtlPortfolioValuation> getPtlPortfolioValuationList() {
		return ptlPortfolioValuationList;
	}

	public void setPtlPortfolioValuationList(List<PtlPortfolioValuation> ptlPortfolioValuationList) {
		this.ptlPortfolioValuationList = ptlPortfolioValuationList;
	}

	public List<PtlSecReceivables> getPtlSecReceivablesList() {
		return ptlSecReceivablesList;
	}

	public void setPtlSecReceivablesList(List<PtlSecReceivables> ptlSecReceivablesList) {
		this.ptlSecReceivablesList = ptlSecReceivablesList;
	}

	public String getBooksetId() {
		return booksetId;
	}

	public void setBooksetId(String booksetId) {
		this.booksetId = booksetId;
	}
}
