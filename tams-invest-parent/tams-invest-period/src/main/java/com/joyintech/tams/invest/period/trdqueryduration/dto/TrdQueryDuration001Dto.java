/*
 * 文件名：TrdInvestDealQuery001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdqueryduration.dto;

import com.google.common.base.Objects;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易查询dto
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2021年2月1日
 * @see TrdQueryDuration001Dto
 * @since 1.0
 */
public class TrdQueryDuration001Dto extends TrdQueryDurationDto {

	/**
	 * 页面元素
	 */
	private String elementId;

	/**
	 * code Name
	 */
	private String statusValidString;

	/**
	 * 平安业务类型
	 */
	private String paBussTypeString;

	/***update zhangweiqi 2019-10-14 存续期添加通道费 begin**/
	/**
	 * 通道费
	 */
	private BigDecimal channelFee;

	/**
	 * 通道性质
	 */
	private String payType;

	/**
	 * 通道收取方式
	 */
	private String collectType;

	/**
	 * 实收本息总额
	 */

	private BigDecimal tradeAmtReal;

	private String finprodType2String;

	private String tradeAmt2String;

	private String ccyVal;
	/**
	 * 获取 statusValidString
	 *
	 * @return statusValidString
	 */
	public String getStatusValidString() {
		String result = SysDataUtil.getNameFromDataDict(DictCode.画面数据状态, getStatusValid());
		return result == null ? getStatusValid() : result;
	}

	/**
	 * 设置 statusValidString
	 *
	 * @param statusValidString statusValidString
	 */
	public void setStatusValidString(String statusValidString) {
		this.statusValidString = statusValidString;
	}

	/**
	 * 获取 PaBussTypeString
	 *
	 * @return PaBussTypeString
	 */
	public String getPaBussTypeString() {
		String paBussTypeName = null;
		if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(getStatusValid())) {
			if (getCashType() != null && getCashType().contains(DictCodeList.现金流类型_中间还本.getValue())) {
				paBussTypeName = DictCodeListExt1.平安业务类型_债券中间还本.getCodeName();
			}
			else if (getCashType() != null && (getCashType().contains(DictCodeList.现金流类型_到期还本.getValue())
					|| getCashType().contains(DictCodeList.现金流类型_到期收息.getValue()))) {
				paBussTypeName = DictCodeListExt1.平安业务类型_债券到期.getCodeName();
			}
			else {
				paBussTypeName =  DictCodeListExt1.平安业务类型_债券中间收息.getCodeName();
			}
		}
		if (StringUtils.isEmpty(paBussTypeName)||Objects.equal(paBussTypeName,getPaBussType())){
			paBussTypeName = SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, getPaBussType());
		}
		if(StringUtils.isEmpty(paBussTypeName)||Objects.equal(paBussTypeName,getPaBussType())){
			paBussTypeName = SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, getPaBussType());
		}

		return paBussTypeName;
	}

	@Override
	public String getPaBussType() {
		if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(getStatusValid())) {
			if (getCashType() != null && getCashType().contains(DictCodeList.现金流类型_中间还本.getValue())) {
				return DictCodeListExt1.平安业务类型_债券中间还本.getValue();
			}
			else if (getCashType() != null && (getCashType().contains(DictCodeList.现金流类型_到期还本.getValue())
					|| getCashType().contains(DictCodeList.现金流类型_到期收息.getValue()))) {
				return DictCodeListExt1.平安业务类型_债券到期.getValue();
			}
			else {
				return DictCodeListExt1.平安业务类型_债券中间收息.getValue();
			}
		}
		else {
			return super.getPaBussType();
		}
	}

	/**
	 * 交易编号
	 *
	 * @return
	 */
	@Override
	@PrimaryKey(PrimaryKey.Field.PK_1)
	public String getFinprodId() {
		return super.getFinprodId();
	}

	/**
	 * 获取 busiType
	 *
	 * @return busiType
	 */
	@PrimaryKey(PrimaryKey.Field.PK_2)
	public String getBusiType() {
		return DictCodeList.共通业务类型_债券类资产存续期.getValue();
	}

	@Override
	@PrimaryKey(PrimaryKey.Field.PK_3)
	public String getPortfolioId() {
		return super.getPortfolioId();
	}

	@Override
	@PrimaryKey(PrimaryKey.Field.PK_4)
	public String getSecManageAcctId() {
		return super.getSecManageAcctId();
	}

	/**
	 * 获取 busiCode
	 *
	 * @return busiCode.
	 */
	@Override
	@PrimaryKey(Field.PK_5)
	public String getBusiCode() {
		return super.getBusiCode();
	}

	@Override
	@NotEmpty(fieldName = "实收利息总额", groups = {ValidateGroupEdit.class})
	public BigDecimal getIntAmt() {
		return super.getIntAmt();
	}

	@Override
	@NotEmpty(fieldName = "到账金额", groups = {ValidateGroupEdit.class})
	public BigDecimal getTradeAmt() {
		return super.getTradeAmt();
	}

	@Override
	@NotEmpty(fieldName = "资金到账日", groups = {ValidateGroupEdit.class})
	public String getTradeDate() {
		return super.getTradeDate();
	}

	/**
	 * 获取 bussTrdTransferOrder
	 *
	 * @return bussTrdTransferOrder
	 */
	@NotEmpty(fieldName = "划款指令", groups = {ValidateGroupEdit.class})
	public List<BussTrdTransferOrderDto> getBussTrdTransferOrder() {
		if (super.getBussTrdTransferOrder() == null || super.getBussTrdTransferOrder().isEmpty()) {
			return null;
		}
		return super.getBussTrdTransferOrder();
	}
	/**
	 * 获取 isClean
	 *
	 * @return isClean
	 */
	@Override
	@NotEmpty(fieldName = "TrdSecOverdueDuration002.isClean", groups = {ValidateGroupEdit.class})
	public String getIsClean() {
		return super.getIsClean();
	}
	/**
	 * 获取 elementId
	 *
	 * @return elementId
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * 设置 elementId
	 *
	 * @param elementId elementId
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * 设置 paBussTypeString
	 *
	 * @param paBussTypeString paBussTypeString
	 */
	public void setPaBussTypeString(String paBussTypeString) {
		this.paBussTypeString = paBussTypeString;
	}

	public BigDecimal getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(BigDecimal channelFee) {
		this.channelFee = channelFee;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCollectType() {
		return collectType;
	}

	public void setCollectType(String collectType) {
		this.collectType = collectType;
	}
	@Override
	@NotEmpty(fieldName = "实收本息总额", groups = {ValidateGroupEdit.class})
	public BigDecimal getTradeAmtReal() {
		return tradeAmtReal;
	}

	public void setTradeAmtReal(BigDecimal tradeAmtReal) {
		this.tradeAmtReal = tradeAmtReal;
	}

	public String getFinprodType2String() {
		String result = SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, getFinprodType2());
		return result == null ? getFinprodType2() : result;
	}

	public void setFinprodType2String(String finprodType2String) {
		this.finprodType2String = finprodType2String;
	}

	public String getCcyVal() {
		String result = SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
		return result == null ? getCcy() : result;
	}

	public void setCcyVal(String ccyVal) {
		this.ccyVal = ccyVal;
	}


	public String getTradeAmt2String() {
		if (StringUtil.isNotEmpty(getTradeAmt()) && StringUtil.isNotEmpty(getTradeAmt2())){
			return getTradeAmt().setScale(2, BigDecimal.ROUND_HALF_UP).toString()+";"
					+getTradeAmt2().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		}else if(!StringUtil.isNotEmpty(getTradeAmt()) && StringUtil.isNotEmpty(getTradeAmt2())){
			return getTradeAmt2().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		}
		else if(StringUtil.isNotEmpty(getTradeAmt())){
			return getTradeAmt().setScale(2, BigDecimal.ROUND_HALF_UP).toString() ;
		}
		return "";
	}

	public void setTradeAmt2String(String tradeAmt2String) {
		this.tradeAmt2String = tradeAmt2String;
	}
}
