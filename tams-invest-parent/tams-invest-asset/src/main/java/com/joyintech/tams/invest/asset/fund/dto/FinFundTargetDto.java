/*
 * 文件名：FinFundTargetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.fund.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.*;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.RepAssetDept;
import com.joyintech.tams.common.base.entity.RepFund;
import com.joyintech.tams.component.business.asset.dto.BussFinIntPayDto;
import com.joyintech.tams.component.business.report.asset.fund.dto.BussRepFundInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.fund.valid.FinFundTargetValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 基金类标的信息 〈功能详细描述〉
 * 
 * @author wangzehui
 * @version 1.0 2018年9月7日
 * @see FinFundTargetDto
 * @since 1.0
 */

@PageId("FinFundTarget002")
@ValidLogic(targetClass = FinFundTargetValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class FinFundTargetDto extends FinProduct implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 基金中债报送组件
     */
    @Valid
    private BussRepFundInfoDto bussRepFundInfoDto;
    /**
     * 分红计划组件
     */
    @Valid
    private BussFinIntPayDto bussFinIntPayDto;
    /**
     * 附件上传
     */
    private List<SysUploadFileInfo> fileArray;
    /**
     * 基金类型
     */
    private String fundType;

    /**
     * 二级类型
     */
    private String secondType;

    /**added by wangguiqi on 2019/7/19 -start**/
    /**
     * 分类9（资产二级类型 ）
     */
    private String type9;
    
    /**
     * 分类10（资产三级类型）
     */
    private String type10;
    
    /**
     * 分类11（资产四级类型）
     */
    private String type11;
    
    /**
     * 分类12（资产五级类型）
     */
    private String type12;
    /* add by zhangpuyu 2019/10/24 start */
    /**
	 * 交易对手类型---分行、通道
	 */
	private String counterType;
	public String getCounterType() {
		return counterType;
	}

	public void setCounterType(String counterType) {
		this.counterType = counterType;
	}
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
    /**added by wangguiqi on 2019/7/19 -end**/
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
     * 选择数据
     */
    private List<FinFundTargetDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * 目的和意义以及可能会用到的地方
     */
    private String pageModel;
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
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;

    /**
     * 投资性质
     */
    @NotEmpty(fieldName = "FinBondTargetAsset002.investNature",groups={ValidateGroupEdit.class})
    private String investNature;

    
    /**
     * 基金中债报送 资产负债类别
     *
     */
    private RepAssetDept repAssetDept;
    /**
     * 基金报送信息 add by shenchen
     */
    private RepFund repfund;


    /**
     * 赎回确认速度
     */
    private String redSpeed;

    /**
     * 法审编号
     */
    private String trialNo;

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
    
    @Override
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @PrimaryKey(value = PrimaryKey.Field.PK_2)
    public String getFinprodType2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.金融产品类型_投管用, super.getFinprodType2());
    }

    @Override
    @NotEmpty(fieldName = "FinFundTarget001.finprodMarketId", groups = {ValidateGroupEdit.class})
    @Pattern(fieldName = "FinFundTarget001.finprodMarketId",regx = "^[A-Za-z0-9]+.[A-Za-z0-9]+$",message = "FW_E_0002",groups={ValidateGroupEdit.class})
//    @Code(fieldName = "FinFundTarget001.finprodMarketId", length = 32, groups = {ValidateGroupEdit.class})
//    @Length(fieldName = "FinFundTarget001.finprodMarketId", max = 18, groups = {
//        ValidateGroupEdit.class})
    @PrimaryKey(value = PrimaryKey.Field.PK_1)
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }

    @Override
    @NotEmpty(fieldName = "FinFundTarget001.finprodAbbr", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "FinFundTarget001.finprodAbbr", max = 100, groups = {
        ValidateGroupEdit.class})
    public String getFinprodAbbr() {
        return super.getFinprodAbbr();
    }

    @Override
    @NotEmpty(fieldName = "FinFundTarget001.finprodName", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "FinFundTarget001.finprodName", max = 100, groups = {
        ValidateGroupEdit.class})
    public String getFinprodName() {
        return super.getFinprodName();
    }

    /**
     * 规范日期格式 <一句话功能简述> <功能详细描述>
     * 
     * @return
     * @see
     * @since 1.0
     */
    @NotEmpty(fieldName = "FinFundTarget002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }
    
    public String getVdateStr() {
        return DateUtil.format(super.getVdate());
    }
    @NotEmpty(fieldName = "FinFundTarget002.idate", groups = {ValidateGroupEdit.class})
    public Date getIdate() {
        return super.getIdate();
    }

    //@Length(fieldName = "FinFundTarget001.fundType", max = 32, groups = {ValidateGroupEdit.class})
    public String getFundType() {
        return fundType;
    }

    @NotEmpty(fieldName = "FinFundTarget001.tradeMarket", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "FinFundTarget001.tradeMarket", max = 32, groups = {
        ValidateGroupEdit.class})
    public String getTradeMarket() {
        return super.getTradeMarket();
    }

    @NotEmpty(fieldName = "FinFundTarget001.manager", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "FinFundTarget001.manager", max = 32, groups = {ValidateGroupEdit.class})
    public String getManager() {
        return super.getManager();
    }

    @NotEmpty(fieldName = "FinFundTarget001.ccy", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "FinFundTarget001.ccy", max = 32, groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }
    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());
    }
    public String getFundTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.基金类资产类型, fundType);
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public String getSecondType() {
        return secondType;
    }

    public String getSecondTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.基金类资产二级类型, secondType);
    }

    public void setSecondType(String secondType) {
        this.secondType = secondType;
    }
    @NotEmpty(fieldName = "FinFundTarget002.purSpeed", groups = {ValidateGroupEdit.class})
    public String getPurSpeed() {
        return purSpeed;
    }
    public String getPurSpeedStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, purSpeed);
    }
    public BussRepFundInfoDto getBussRepFundInfoDto() {
        return bussRepFundInfoDto;
    }

    
    public void setBussRepFundInfoDto(BussRepFundInfoDto bussRepFundInfoDto) {
        this.bussRepFundInfoDto = bussRepFundInfoDto;
    }

    
    public BussFinIntPayDto getBussFinIntPayDto() {
        return bussFinIntPayDto;
    }

    
    public void setBussFinIntPayDto(BussFinIntPayDto bussFinIntPayDto) {
        this.bussFinIntPayDto = bussFinIntPayDto;
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

    //@NotEmpty(fieldName = "FinFundTarget001.divideMethod", groups = {ValidateGroupEdit.class})
    //@Length(fieldName = "FinFundTarget001.divideMethod", max = 32, groups = {
      //  ValidateGroupEdit.class})
    public String getDivideMethod() {
        return divideMethod;
    }

    public String getDivideMethodStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.分红方式, divideMethod);
    }

    public void setDivideMethod(String divideMethod) {
        this.divideMethod = divideMethod;
    }

    //@NotEmpty(fieldName = "FinFundTarget001.carryoverFreq", groups = {ValidateGroupEdit.class})
    //@Length(fieldName = "FinFundTarget001.carryoverFreq", max = 32, groups = {
    //    ValidateGroupEdit.class})
    public String getCarryoverFreq() {
        return carryoverFreq;
    }

    public String getCarryoverFreqStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.收益结转频率, carryoverFreq);
    }

    public void setCarryoverFreq(String carryoverFreq) {
        this.carryoverFreq = carryoverFreq;
    }

    @NotEmpty(fieldName = "FinFundTarget002.calendarId", groups = {ValidateGroupEdit.class})
    //@Length(fieldName = "FinFundTarget002.calendarId", max = 32, groups = {
    //    ValidateGroupEdit.class})
    public String getCalendarId() {
        return super.getCalendarId();
    }
    
    public String getIsCompoundInt() {
        return isCompoundInt;
    }
    
    public void setIsCompoundInt(String isCompoundInt) {
        this.isCompoundInt = isCompoundInt;
    }
    
    public String getIsTransInAsset() {
        return isTransInAsset;
    }

    
    public void setIsTransInAsset(String isTransInAsset) {
        this.isTransInAsset = isTransInAsset;
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

    /**
     * 获取 investNature
     *
     * @return investNature
     */
    @NotEmpty(fieldName = "资产投资性质" ,groups = {ValidateGroupEdit.class})
    public String getInvestNature() {
        return investNature;
    }

	public PtlPortfolio getPtlPortfolio() {
		return ptlPortfolio;
	}
    /**
     * 设置 investNature
     *
     * @param investNature investNature
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }        


	public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
		this.ptlPortfolio = ptlPortfolio;
	}
    public RepAssetDept getRepAssetDept() {
        return repAssetDept;
    }
    
	/**
     * 获取 type9
     * @return type9.
     */
    @NotEmpty(fieldName = "FinBondTargetAsset002.type9" ,groups = {ValidateGroupEdit.class})
    public String getType9() {
		return type9;
	}    

    /**
     * 获取资产二级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType9Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.getType9());
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
	@NotEmpty(fieldName = "FinBondTargetAsset002.type10" ,groups = {ValidateGroupEdit.class})
    public String getType10() {
		return type10;
	}  
    
    /**
     * 获取资产三级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType10Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.getType10());
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
//	@NotEmpty(fieldName = "FinBondTargetAsset002.type11" ,groups = {ValidateGroupEdit.class})
    public String getType11() {
		return type11;
	}  
    
    /**
     * 获取资产四级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType11Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getType11());
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
//	@NotEmpty(fieldName = "FinBondTargetAsset002.type12" ,groups = {ValidateGroupEdit.class})
    public String getType12() {
		return type12;
	}  
    
    /**
     * 获取资产五级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType12Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产五级分类, this.getType12());
    }
    
    /**
     * 设置 type5
     * @param type12
     */
	public void setType12(String type12) {
		this.type12 = type12;
	}
    



    public void setRepAssetDept(RepAssetDept repAssetDept) {
        this.repAssetDept = repAssetDept;
    }

    public RepFund getRepfund() {
        return repfund;
    }

    public void setRepfund(RepFund repfund) {
        this.repfund = repfund;
    }

    @NotEmpty(fieldName = "FinPlanNetAsset002.redSpeed",groups={ValidateGroupEdit.class})
    public String getRedSpeed() {
        return redSpeed;
    }

    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }

    @Override
    @Length(fieldName = "FinDepositTarget002.contractNo", max = 400, groups = {ValidateGroupEdit.class})
    /*@NotEmpty(fieldName = "FinDepositTarget002.contractNo", groups = {ValidateGroupEdit.class})*/
    public String getContractNo(){
        return super.getContractNo();
    }

    @Length(fieldName = "FinDepositTarget002.trialNo", max = 100, groups = {ValidateGroupEdit.class})
    public String getTrialNo() {
        return trialNo;
    }

    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }

    @NotEmpty(fieldName = "FinFundTarget002.toffStrdate", groups = {ValidateGroupEdit.class})
    public Date getToffStrdate() {
        return toffStrdate;
    }

    public void setToffStrdate(Date toffStrdate) {
        this.toffStrdate = toffStrdate;
    }

    @NotEmpty(fieldName = "FinFundTarget002.toffEnddate", groups = {ValidateGroupEdit.class})
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

    public String getTrmFundTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易所基金类型, this.getTrmFundType());
    }

    private String oldFinprodMarketId;

    public String getOldFinprodMarketId() {
        return oldFinprodMarketId;
    }

    public void setOldFinprodMarketId(String oldFinprodMarketId) {
        this.oldFinprodMarketId = oldFinprodMarketId;
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
