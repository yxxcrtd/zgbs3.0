package com.joyintech.service.valuation.accounadjustmanage.subjectset.dto;

import com.joyintech.framework.base.BaseEntity;

/**
 * @author  殷亚
 * @description
 * @date 2022-1-12 16:17   
 */
public class ValSubjectHandDto extends BaseEntity {
	/**
	 * 账套代码
	 */
	private String booksetId;
	/**
	 * 科目代码
	 */
	private  String subjectNo;

	/**
	 * 内部科目编码
	 */
	private String subjectInternalCode;
	/**
	 * 辅助核算项编码
	 */
	private String handCode;
	/**
	 * 辅助核算项编码 值
	 */
	private String handCodeValue;

	/*add by sunshuaishuai 辅助核算项 20220406 start*/
	/**
	 * 辅助核算项字典名
	 */
	private String handName;
	/*add by sunshuaishuai 20220406 end*/


	public String getBooksetId() {
		return booksetId;
	}

	public void setBooksetId(String booksetId) {
		this.booksetId = booksetId;
	}

	public String getSubjectNo() {
		return subjectNo;
	}

	public void setSubjectNo(String subjectNo) {
		this.subjectNo = subjectNo;
	}

	public String getSubjectInternalCode() {
		return subjectInternalCode;
	}

	public void setSubjectInternalCode(String subjectInternalCode) {
		this.subjectInternalCode = subjectInternalCode;
	}

	public String getHandCode() {
		return handCode;
	}

	public void setHandCode(String handCode) {
		this.handCode = handCode;
	}

	public String getHandCodeValue() {
		return handCodeValue;
	}

	public void setHandCodeValue(String handCodeValue) {
		this.handCodeValue = handCodeValue;
	}

	public String getHandName() {
		return handName;
	}

	public void setHandName(String handName) {
		this.handName = handName;
	}
}
