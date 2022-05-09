/*
 * 文件名：FinFundTargetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.fund.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.framework.json.BigDecimalSerializer;

/**
 * 基金类标的信息
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年9月7日
 * @see FinFundTargetDto
 * @since 1.0
 */

public class FinFundTargetDto extends FinProduct {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 基金类型
     */
    private String fundType;
    /**
     * 二级类型
     */
    private String secondType;
    /* add by zhangpuyu 2019/10/24 start */
    /**
	 * 投后管理机构
	 */
	private String postOrg;
	public String getPostOrg() {
		return postOrg;
	}

	public void setPostOrg(String postOrg) {
		this.postOrg = postOrg;
	}
	/* add by zhangpuyu 2019/10/24 end */
    /**added by wangguiqi on 2019/7/22 -start**/
    /**
     * 分类9（资产二级分类）
     */
    private String type9;

    /**
     * 分类10（资产三级分类）
     */
    private String type10;

    /**
     * 分类11（资产四级分类）
     */
    private String type11;

    /**
     * 分类12（资产五级分类）
     */
    private String type12;
    /**added by wangguiqi on 2019/7/22 -end**/

    /**
     * 申购确认速度
     */
    private String purSpeed;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 客户代码
     */
    //private String customerId;
    /**
     * 支付方式
     */
    private String divideMethod;
    /**
     * 支付频率
     */
    private String carryoverFreq;
    /**
     * 是否按日复利
     */
    private String isCompoundInt;
    /**
     * 是否存在交易
     */
    private String isTransInAsset;
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 授信额度代码
     */
    private String seqno;

    /**
     * 授信配置代码
     */
    private String creditMstId;

    /**
     * 授信客户编号（机构授信）/金融产品代码（资产授信）/资产系列代码（资产系列授信）
     */
    private String customerId;

    /**
     * 批复流水号
     */
    private String pflsh;
    /**
     * 已占用额度，初始值为0
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal hapCreditAmt;

    /**
     * 实际授信主体
     */
    private String actualCreditProvid;
    /**
     * 是否占用主体授信
     */
    private String creditFlag;

    /**
     * 投资性质
     */
    private String investNature;

    /**
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;

    /**
     * 赎回确认速度
     */
    private String redSpeed;

    /**
     * 法审编号
     */
    private String trialNo;
    
    /**
     * 获取 creditFlag
     * @return creditFlag.
     */
    public String getCreditFlag() {
        return creditFlag;
    }

    
    /**
     * 设置 creditFlag
     * @param creditFlag creditFlag
     */
    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }
    /**
     * 选择数据
     */
    private List<FinFundTargetDto> selectedItem;
    /**
     * 要素id
     */
    private String elementId;
    /**
     * 分红计划组件dto
     */
    private BussFinIntPayDto bussFinIntPayDto;
    /**
     * 基金中债报送信息dto
     */
    private BussRepFundInfoDto bussRepFundInfoDto;
    /**
     * 
     */
    private List<SysUploadFileInfo> fileArray;

    /**
     * 目的和意义以及可能会用到的地方
     */
    private String pageModel;

    /**
     * 认购起始日
     */
    private Date toffStrdate;

    /**
     * 认购终止日
     */
    private Date toffEnddate;

    /**
     * 交易所基金简称
     */
    private String trmFundAbbr;

    /**
     * 交易所基金类型
     */
    private String trmFundType;

    /**
     * 分级基金
     */
    private String graFundType;

    /**
     * 申购赎回代码
     */
    private String purRedId;

    /**
     * 申购赎回简称
     */
    private String purRedAbbr;

    /**
     * 单笔申购金额下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal purStartLow;

    /**
     * 单笔赎回份额下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal redStartLow;

    /**
     * ETF类型
     */
    private String etfType;

    /**
     * ETF最小申购赎回单位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal etfMinPrunit;

    /**
     * 面值，债券单位面值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal faceValue;

    /**
     * 母金融产品代码
     */
    private String pMarketId;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;

    /**
     * 是否7日可变现资产
     */
    private String isSevenDayCashable;

    /**
     * 开放类型
     */
    private String openType;

    public String getFundType() {
        return fundType;
    }
    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    
    public String getSecondType() {
        return secondType;
    }
    
    public void setSecondType(String secondType) {
        this.secondType = secondType;
    }

    
    public String getPurSpeed() {
        return purSpeed;
    }

    
    public void setPurSpeed(String purSpeed) {
        this.purSpeed = purSpeed;
    }

    
    public String getCustomerName() {
        return customerName;
    }

    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    
    public String getCustomerId() {
        return customerId;
    }

    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    
    public String getDivideMethod() {
        return divideMethod;
    }

    
    public void setDivideMethod(String divideMethod) {
        this.divideMethod = divideMethod;
    }

    
    public String getCarryoverFreq() {
        return carryoverFreq;
    }
    
    public void setCarryoverFreq(String carryoverFreq) {
        this.carryoverFreq = carryoverFreq;
    }

    public String getIsCompoundInt() {
        return isCompoundInt;
    }
    
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt;
    }
    public String getOperateType() {
        return operateType;
    }

    
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    
    public List<FinFundTargetDto> getSelectedItem() {
        return selectedItem;
    }

    public String getIsTransInAsset() {
        return isTransInAsset;
    }
    
    public void setIsTransInAsset(String isTransInAsset) {
        this.isTransInAsset = isTransInAsset;
    }
    public void setSelectedItem(List<FinFundTargetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    
    public String getElementId() {
        return elementId;
    }

    
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    
    public String getPageModel() {
        return pageModel;
    }

    
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }
    
    public BussFinIntPayDto getBussFinIntPayDto() {
        return bussFinIntPayDto;
    }
    
    public void setBussFinIntPayDto(BussFinIntPayDto bussFinIntPayDto) {
        this.bussFinIntPayDto = bussFinIntPayDto;
    }
    
    public BussRepFundInfoDto getBussRepFundInfoDto() {
        return bussRepFundInfoDto;
    }
    
    public void setBussRepFundInfoDto(BussRepFundInfoDto bussRepFundInfoDto) {
        this.bussRepFundInfoDto = bussRepFundInfoDto;
    }
    
    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }
    
    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }
    
    /**
     * 获取 seqno
     * @return seqno.
     */
    public String getSeqno() {
        return seqno;
    }
    
    /**
     * 设置 seqno
     * @param seqno seqno
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }
    
    /**
     * 获取 creditMstId
     * @return creditMstId.
     */
    public String getCreditMstId() {
        return creditMstId;
    }
    
    /**
     * 设置 creditMstId
     * @param creditMstId creditMstId
     */
    public void setCreditMstId(String creditMstId) {
        this.creditMstId = creditMstId;
    }
    
    /**
     * 获取 pflsh
     * @return pflsh.
     */
    public String getPflsh() {
        return pflsh;
    }
    
    /**
     * 设置 pflsh
     * @param pflsh pflsh
     */
    public void setPflsh(String pflsh) {
        this.pflsh = pflsh;
    }
    
    /**
     * 获取 hapCreditAmt
     * @return hapCreditAmt.
     */
    public BigDecimal getHapCreditAmt() {
        return hapCreditAmt;
    }
    
    /**
     * 设置 hapCreditAmt
     * @param hapCreditAmt hapCreditAmt
     */
    public void setHapCreditAmt(BigDecimal hapCreditAmt) {
        this.hapCreditAmt = hapCreditAmt;
    }
    
    /**
     * 获取 actualCreditProvid
     * @return actualCreditProvid.
     */
    public String getActualCreditProvid() {
        return actualCreditProvid;
    }
    
    /**
     * 设置 actualCreditProvid
     * @param actualCreditProvid actualCreditProvid
     */
    public void setActualCreditProvid(String actualCreditProvid) {
        this.actualCreditProvid = actualCreditProvid;
    }


	public PtlPortfolio getPtlPortfolio() {
		return ptlPortfolio;
	}


	public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
		this.ptlPortfolio = ptlPortfolio;
	}

    /**added by wangguiqi on 2019/7/19 -start**/
    /**
     * 获取 type9
     * @return type9.
     */
    public String getType9() {
		return type9;
	}


    /**
     * 设置 type9
     * @param type9
     */
	public void setType9(String type9) {
		this.type9 = type9;
	}

	/**
     * 获取 type10
     * @return type10.
     */
	public String getType10() {
		return type10;
	}


    /**
     * 设置 type10
     * @param type10
     */
	public void setType10(String type10) {
		this.type10 = type10;
	}

	/**
     * 获取 type11
     * @return type11.
     */
	public String getType11() {
		return type11;
	}


    /**
     * 设置 type11
     * @param type11
     */
	public void setType11(String type11) {
		this.type11 = type11;
	}


    /**
     * 获取 type12
     * @return type12.
     */
	public String getType12() {
		return type12;
	}

	/**
     * 设置 type12
     * @param type12
     */
	public void setType12(String type12) {
		this.type12 = type12;
	}
    /**added by wangguiqi on 2019/7/22 -end**/



    /**
     * 获取 investNature
     *
     * @return investNature
     */
    public String getInvestNature() {
        return investNature;
    }

    /**
     * 设置 investNature
     *
     * @param investNature investNature
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }

    public String getRedSpeed() {
        return redSpeed;
    }

    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }

    public String getTrialNo() {
        return trialNo;
    }

    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }

    public Date getToffStrdate() {
        return toffStrdate;
    }

    public void setToffStrdate(Date toffStrdate) {
        this.toffStrdate = toffStrdate;
    }

    public Date getToffEnddate() {
        return toffEnddate;
    }

    public void setToffEnddate(Date toffEnddate) {
        this.toffEnddate = toffEnddate;
    }

    public String getTrmFundAbbr() {
        return trmFundAbbr;
    }

    public void setTrmFundAbbr(String trmFundAbbr) {
        this.trmFundAbbr = trmFundAbbr;
    }

    public String getTrmFundType() {
        return trmFundType;
    }

    public void setTrmFundType(String trmFundType) {
        this.trmFundType = trmFundType;
    }

    public String getGraFundType() {
        return graFundType;
    }

    public void setGraFundType(String graFundType) {
        this.graFundType = graFundType;
    }

    public String getPurRedId() {
        return purRedId;
    }

    public void setPurRedId(String purRedId) {
        this.purRedId = purRedId;
    }

    public String getPurRedAbbr() {
        return purRedAbbr;
    }

    public void setPurRedAbbr(String purRedAbbr) {
        this.purRedAbbr = purRedAbbr;
    }

    public BigDecimal getPurStartLow() {
        return purStartLow;
    }

    public void setPurStartLow(BigDecimal purStartLow) {
        this.purStartLow = purStartLow;
    }

    public BigDecimal getRedStartLow() {
        return redStartLow;
    }

    public void setRedStartLow(BigDecimal redStartLow) {
        this.redStartLow = redStartLow;
    }

    public String getEtfType() {
        return etfType;
    }

    public void setEtfType(String etfType) {
        this.etfType = etfType;
    }

    public BigDecimal getEtfMinPrunit() {
        return etfMinPrunit;
    }

    public void setEtfMinPrunit(BigDecimal etfMinPrunit) {
        this.etfMinPrunit = etfMinPrunit;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getpMarketId() {
        return pMarketId;
    }

    public void setpMarketId(String pMarketId) {
        this.pMarketId = pMarketId;
    }

    public String getFundMangerName() {
        return fundMangerName;
    }

    public void setFundMangerName(String fundMangerName) {
        this.fundMangerName = fundMangerName;
    }

    public String getIsSevenDayCashable() {
        return isSevenDayCashable;
    }

    public void setIsSevenDayCashable(String isSevenDayCashable) {
        this.isSevenDayCashable = isSevenDayCashable;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }
}
