/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.validation.annotation.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.bond.dto.BusiFinBondRatingInfoDto;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepEquityInfoDto;
import com.joyintech.tams.component.business.riskrds.dto.PageModel;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 债券标的信息DTO
 * @author zhangnian
 * @version 1.0 2022年4月14日
 * @see FinBondAssetSaveRequest
 * @since 1.0
 */
@PageId("FinBondAsset002")
@ApiModel(value="债券新建画面表单",description="债券新建画面表单")
public class FinBondAssetSaveRequest extends BaseEntity implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateType",value="操作类型",example="submit/save", required = true)
    private String operateType;

    /**
     * 操作类型
     */
    @ApiModelProperty(name="operateStatus",value="操作状态",required = true)
    private String operateStatus;

    @ApiModelProperty(name="elementId",value="按钮id",example="FinBondAsset002_btn_submit/FinBondAsset002_btn_save")
    private String elementId;

    @ApiModelProperty(name="pageId",value="画面ID",example="FinBondAsset002")
    private String pageId;

    @ApiModelProperty(name="pageModel",value="分页对象")
    private PageModel pageModel;

    @ApiModelProperty(name="finprodId",value="金融产品代码")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",required=true)
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarketId",value="债券代码后缀",required=true)
    private String tradeMarketId;

    @ApiModelProperty(name="finprodName",value="资产名称",required=true)
    private String finprodName;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",required=true)
    private String finprodAbbr;

    @ApiModelProperty(name="tradeMarket",value="交易场所",required=true)
    private String tradeMarket;

    @ApiModelProperty(name="registOrg",value="登记托管机构",required=true)
    private String registOrg;

    @ApiModelProperty(name="issuePrice",value="发行价格",required=true)
    private BigDecimal issuePrice;

    @ApiModelProperty(name="idate",value="发行日",required=true)
    private Date idate;

    @ApiModelProperty(name="vdate",value="起息日",required=true)
    private Date vdate;

    @ApiModelProperty(name="mdate",value="到期日",required=true)
    private Date mdate;

    @ApiModelProperty(name="delistingDate",value="摘牌日",required=true)
    private Date delistingDate;

    @ApiModelProperty(name="issueAmt",value="发行金额(亿元)",required=true)
    private BigDecimal issueAmt;

    @ApiModelProperty(name="issuer",value="发行人",required=true)
    private String issuer;

    private String type9;

    private String type10;

    private String type11;

    @ApiModelProperty(name="issueType",value="募集方式",required=true)
    private String issueType;

    @ApiModelProperty(name="isSus",value="是否永续",required=true)
    private String isSus;

    @ApiModelProperty(name="isRight",value="是否含权债",required=true)
    private String isRight;

    @ApiModelProperty(name="isAllowOverwrite",value="是否允许资讯数据覆盖",required=true)
    private String isAllowOverwrite;

    @ApiModelProperty(name="isNetprice",value="是否净价交易(条件必填)")
    private String isNetprice;

    @ApiModelProperty(name="isNoGuard",value="是否非担保(条件必填)")
   private String isNoGuard;

    @ApiModelProperty(name="ccy",value="币种",required=true)
    private String ccy;

    @ApiModelProperty(name="capiIncomeFeature",value="保本标识")
    private String capiIncomeFeature;

    @ApiModelProperty(name="conceptType",value="概念标识")
    private String conceptType;

    @ApiModelProperty(name="crossMarketCode",value="跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name="sustainableRemark",value="特殊条款(条件必填)")
    private String sustainableRemark;

    /**
     * 转债信息
     */
    @ApiModelProperty(name="finProductConvertible",value="转债信息")
    private BondFinProductConvertibleDto finProductConvertible;

    /**
     * 计息要素
     */
    @ApiModelProperty(name="finInterestFactorDto",value="计息要素")
    private BondFinInterestFactorDto finInterestFactorDto;

    /**
     * 付息信息DTO
     */
    @ApiModelProperty(name="finPayInterestStdInfoDto",value="付息信息")
    private BondFinPayInterestStdInfoDto finPayInterestStdInfoDto;

    /**
     * 还本信息DTO
     */
    @ApiModelProperty(name="invRepayList",value="还本信息")
    private List<BondFinCashPlanDto>  invRepayList;

    /**
     * 行权计划DTO List<FinExercisePlan> finExercisePlanList;
     */
    @ApiModelProperty(name="finExercisePlanList",value="行权计划")
    private List<BondFinExercisePlanDto>  finExercisePlanList;

    /**
     * 附件
     */
    @ApiModelProperty(name="mstFileInputParameter",value="附件")
    private MstFileInputParameter mstFileInputParameter;
    /**
     * 备注
     */
    @ApiModelProperty(name="remark",value="备注")
    private String remark;
    /**
     * 发行信息
     */
    @ApiModelProperty(name="finProductIssueList",value="发行信息")
    private List<BondFinProductIssueDto> finProductIssueList;

    @ApiModelProperty(name="typeTwo",value="资产一级分类" ,required=true)
    private String typeTwo;

    @ApiModelProperty(name="typeThree",value="资产二级分类",required=true)
    private String typeThree;

    @ApiModelProperty(name="typeFour",value="资产三级分类",required=true)
    private String typeFour;

    /**
     * 选择数据
     */
    private List<FinBondAssetSaveRequest> selectedItem;

    /**
     * 基础组件-标的金融产品
     */
    private List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList;

    /**
     * 资产/负债类别
     */
    private String repAssetType;


    private String industry;

    private String industry2;
    
    private String isNetSett;
    
    private String tradePlat;
    
    /**
     * 是否私募债
     */
    private String isPPB;

    public String getIsNetSett() {
		return isNetSett;
	}

	public void setIsNetSett(String isNetSett) {
		this.isNetSett = isNetSett;
	}

	public String getTradePlat() {
		return tradePlat;
	}

	public void setTradePlat(String tradePlat) {
		this.tradePlat = tradePlat;
	}

	@NotEmpty(fieldName = "FinBondTargetAsset002.isPpb", groups = {ValidateGroupEdit.class})
	public String getIsPPB() {
		return isPPB;
	}
	
	public String getIsPPBStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsPPB());
    }

	public void setIsPPB(String isPPB) {
		this.isPPB = isPPB;
	}

	public String getIsNetprice() {
		return isNetprice;
	}

	public void setIsNetprice(String isNetprice) {
		this.isNetprice = isNetprice;
	}
	
	public String getIsNetpriceStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsNetprice());
    }

    public String getRepAssetType() {
        return repAssetType;
    }

    public void setRepAssetType(String repAssetType) {
        this.repAssetType = repAssetType;
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
     * 是否次级债
     */
    private String isSecBond;
    /**
     * 支付频率
     */
    private String payCycle;
    
    /**
     * 承销商
     */
    private String underWriterId;

    /**
     * 是否允许覆盖
     */
    private String isAutoUpdate;
    
    /**
     * 发行人名称
     */
    private String issuerName;
    
    /**
     * 承销商名称
     */
    private String underWriterIdName;
    
    /**
     * 交易日历名称
     */
    private String calendarName;
    
    /**
     * 利率类型
     */
    private String rateType;
    
    /**
     * 票面利率
     */
    private BigDecimal rate;
    
    /**
     * 计息基础
     */
    private String basis;
    
    /***
     * 系数
     */
    private String coefficient;
    
    /**
     * 利差
     */
    private String spreadRate;
    
    /**
     * 机构类型(企业性质)
     */
    private String partyType2;
    
    /**
     * 基准编号
     */
    private String indexId;
    
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
     * 主体评级结果
     */
    private String ztGradeResult;
    /**
     * 主体评级机构
     */
    private String ztGradeOrgId;
    /**
     * 债项评级结果
     */
    private String zxGradeResult;
    /**
     * 债项评级机构
     */
    private String zxGradeOrgId;
    /**
     * 行权日
     */
    private Date exerciseDate;

    /**
     * 债券评级信息list add by shenchen 2019-04-25
     */
    private List<BusiFinProductRatingDto> mstBondCreditRatingList;

    /**
     * 债券中`5yer5lyjo0x  债报送 机构类别add by shenchen
     */
    private RepAssetBond repAssetBond;

    /**
     * 权益类中债报送
     */
    private RepAssetEquity repAssetEquity;
    /**
     * 债券中债报送 资产负债类别
     *
     */
    private RepAssetDept repAssetDept;

    /**
     * 其他报送信息
     */
    private RepAssetOther repAssetOther;

    /**
     * 是否城投债
     */
    private String isCityBond;

    /**
     * 城投债级别
     */
    private String cityBondLev;

    /**
     * 现金流计算规则dtolist
     */
    private List<FinCashCalcRule> finCashCalcRuleList;

    /**
     * 是否发行标的
     */
    public String isIssueAsset;

    /**
     * 上市代码
     */
    public String appearMarketId;

    /**
     * 获取上市代码
     */
    public String getAppearMarketId() {
        return appearMarketId;
    }

    /**
     * 设置上市代码
     */
    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    /**
     * 获取 isIssueAsset
     * @return isIssueAsset.
     */
    public String getIsIssueAsset() {
        return isIssueAsset;
    }

    /**
     * 设置 isIssueAsset
     * @param isIssueAsset isIssueAsset
     */
    public void setIsIssueAsset(String isIssueAsset) {
        this.isIssueAsset = isIssueAsset;
    }

    /**
     * 获取 ztGradeResult
     * @return ztGradeResult.
     */
    public String getZtGradeResult() {
        return ztGradeResult;
    }
    public String getZtGradeResultStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getZtGradeResult());
    }

    /**
     * 设置 ztGradeResult
     * @param ztGradeResult ztGradeResult
     */
    public void setZtGradeResult(String ztGradeResult) {
        this.ztGradeResult = ztGradeResult;
    }

    /**
     * 获取 ztGradeOrgId
     * @return ztGradeOrgId.
     */
    public String getZtGradeOrgId() {
        return ztGradeOrgId;
    }
    public String getZtGradeOrgIdStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级机构, this.getZtGradeOrgId());
    }

    /**
     * 设置 ztGradeOrgId
     * @param ztGradeOrgId ztGradeOrgId
     */
    public void setZtGradeOrgId(String ztGradeOrgId) {
        this.ztGradeOrgId = ztGradeOrgId;
    }

    /**
     * 获取 zxGradeResult
     * @return zxGradeResult.
     */
    public String getZxGradeResult() {
        return zxGradeResult;
    }
    public String getZxGradeResultStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getZxGradeResult());
    }

    /**
     * 设置 zxGradeResult
     * @param zxGradeResult zxGradeResult
     */
    public void setZxGradeResult(String zxGradeResult) {
        this.zxGradeResult = zxGradeResult;
    }

    /**
     * 获取 zxGradeOrgId
     * @return zxGradeOrgId.
     */
    public String getZxGradeOrgId() {
        return zxGradeOrgId;
    }
    public String getZxGradeOrgIdStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级机构, this.getZxGradeOrgId());
    }

    /**
     * 设置 zxGradeOrgId
     * @param zxGradeOrgId zxGradeOrgId
     */
    public void setZxGradeOrgId(String zxGradeOrgId) {
        this.zxGradeOrgId = zxGradeOrgId;
    }
    
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

    // 一体化对应etliu
    /**
     * 面值
     */
    @NotEmpty(fieldName = "FinBondTargetAsset002.faceValue" ,groups = {ValidateGroupEdit.class})
    private BigDecimal faceValue;

    /**
     * 获取 faceValue
     * @return faceValue.
     */
    public BigDecimal getFaceValue() {
        return faceValue;
    }
    
    /**
     * 设置 faceValue
     * @param faceValue faceValue
     */
    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    /**
     * 债券评级信息DTO
     */
    @Valid
    private BusiFinBondRatingInfoDto busiFinBondRatingInfoDto;
    
    /**
     * 中债报送DTO
     */
    @Valid
    private BussFinBondRepInfoDto bussFinBondRepInfoDto;

    /**
     * 业务组件--权益类中债报送
     */
    @Valid
    private BussRepEquityInfoDto bussRepEquityInfoDto;
    
    /**
     * 附件信息DTO
     */
    private List<FileCommonDto> fileCommonList;
    
    /**
     * 附件信息DTO
     */
    private List<UploadFile> fileArray;


    /**
     * 正股市场代码
     */
    private String conFinprodMarketId;
    
    /**
     * 获取 underWriterIdName
     * @return underWriterIdName.
     */
    public String getUnderWriterIdName() {
        return underWriterIdName;
    }

    /**
     * 设置 underWriterIdName
     * @param underWriterIdName underWriterIdName
     */
    public void setUnderWriterIdName(String underWriterIdName) {
        this.underWriterIdName = underWriterIdName;
    }

    /**
     * 获取 calendarName
     * @return calendarName.
     */
    public String getCalendarName() {
        return calendarName;
    }


    
    /**
     * 设置 calendarName
     * @param calendarName calendarName
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    
    /**
     * 获取 issuerName
     * @return issuerName.
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * 设置 issuerName
     * @param issuerName issuerName
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * 获取 baseFinProductBaseInfoList
     * @return baseFinProductBaseInfoList.
     */
    public List<BaseFinProductBaseInfoDto> getBaseFinProductBaseInfoList() {
        return baseFinProductBaseInfoList;
    }

    /**
     * 设置 baseFinProductBaseInfoList
     * @param baseFinProductBaseInfoList baseFinProductBaseInfoList
     */
    public void setBaseFinProductBaseInfoList(List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList) {
        this.baseFinProductBaseInfoList = baseFinProductBaseInfoList;
    }

    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }

    public List<FinBondAssetSaveRequest> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<FinBondAssetSaveRequest> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 获取 isAutoUpdate
     * @return isAutoUpdate.
     */
    public String getIsAutoUpdate() {
        return isAutoUpdate;
    }

    /**
     * 设置 isAutoUpdate
     * @param isAutoUpdate isAutoUpdate
     */
    public void setIsAutoUpdate(String isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
    }

    /**
     * 获取 indexId
     * @return indexId.
     */
    public String getIndexId() {
        return indexId;
    }

    /**
     * 设置 indexId
     * @param indexId indexId
     */
    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    /**added by wangguiqi on 2019/7/19 -start**/
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
     * 获取 isSecBond
     * @return isSecBond.
     */
    public String getIsSecBond() {
        return isSecBond;
    }

    
    /**
     * 设置 isSecBond
     * @param isSecBond isSecBond
     */
    public void setIsSecBond(String isSecBond) {
        this.isSecBond = isSecBond;
    }
    /**
     * 获取 underWriterId
     * @return underWriterId.
     */
    public String getUnderWriterId() {
        return underWriterId;
    }

    
    /**
     * 设置 underWriterId
     * @param underWriterId underWriterId
     */
    public void setUnderWriterId(String underWriterId) {
        this.underWriterId = underWriterId;
    }


    /**
     * 获取 busiFinBondRatingInfoDto
     * @return busiFinBondRatingInfoDto.
     */
    public BusiFinBondRatingInfoDto getBusiFinBondRatingInfoDto() {
        return busiFinBondRatingInfoDto;
    }

    /**
     * 设置 busiFinBondRatingInfoDto
     * @param busiFinBondRatingInfoDto busiFinBondRatingInfoDto
     */
    public void setBusiFinBondRatingInfoDto(BusiFinBondRatingInfoDto busiFinBondRatingInfoDto) {
        this.busiFinBondRatingInfoDto = busiFinBondRatingInfoDto;
    }

    /**
     * 获取 mstFileInputParameter
     * @return mstFileInputParameter.
     */
    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    /**
     * 设置 mstFileInputParameter
     * @param mstFileInputParameter mstFileInputParameter
     */
    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }

    /**
     * 获取 fileCommonList
     * @return fileCommonList.
     */
    public List<FileCommonDto> getFileCommonList() {
        return fileCommonList;
    }

    /**
     * 设置 fileCommonList
     * @param fileCommonList fileCommonList
     */
    public void setFileCommonList(List<FileCommonDto> fileCommonList) {
        this.fileCommonList = fileCommonList;
    }

    /**
     * 获取 rateType
     * @return rateType.
     */
    public String getRateType() {
        return rateType;
    }

    /**
     * 设置 rateType
     * @param rateType rateType
     */
    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    /**
     * 获取 rate
     * @return rate.
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置 rate
     * @param rate rate
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * 获取 basis
     * @return basis.
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 basis
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }

    /**
     * 获取 coefficient
     * @return coefficient.
     */
    public String getCoefficient() {
        return coefficient;
    }

    /**
     * 设置 coefficient
     * @param coefficient coefficient
     */
    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }


    
    /**
     * 获取 spreadRate
     * @return spreadRate.
     */
    public String getSpreadRate() {
        return spreadRate;
    }

    /**
     * 设置 spreadRate
     * @param spreadRate spreadRate
     */
    public void setSpreadRate(String spreadRate) {
        this.spreadRate = spreadRate;
    }

    /**
     * 获取 partyType2
     * @return partyType2.
     */
    public String getPartyType2() {
        return partyType2;
    }

    /**
     * 设置 partyType2
     * @param partyType2 partyType2
     */
    public void setPartyType2(String partyType2) {
        this.partyType2 = partyType2;
    }

    
    /**
     * 获取 bussFinBondRepInfoDto
     * @return bussFinBondRepInfoDto.
     */
    public BussFinBondRepInfoDto getBussFinBondRepInfoDto() {
        return bussFinBondRepInfoDto;
    }


    
    /**
     * 设置 bussFinBondRepInfoDto
     * @param bussFinBondRepInfoDto bussFinBondRepInfoDto
     */
    public void setBussFinBondRepInfoDto(BussFinBondRepInfoDto bussFinBondRepInfoDto) {
        this.bussFinBondRepInfoDto = bussFinBondRepInfoDto;
    }
 
    /**
     * 利率类型
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getRateTypeName(){
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.getRateType());
    }

    /**
     * 计息基础
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBasisName() {
        return SysDataUtil.getNameFromDataDict(DictCode.计息基础, this.getBasis());
    }
    
    /**
     * 是否次级债名称
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getIsSecBondName(){
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsSecBond());
    }

    /**
     *  基准名称(挂钩利率 )
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getIndexIdName(){
        return SysDataUtil.getNameFromDataDict(DictCode.利率行情, this.getIndexId());
    }

    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<UploadFile> getFileArray() {
        return fileArray;
    }

    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<UploadFile> fileArray) {
        this.fileArray = fileArray;
    }

    /**
     * 获取 payCycle
     * @return payCycle.
     */
    public String getPayCycle() {
        return payCycle;
    }
    public String getPayCycleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.频率, this.getPayCycle());
    }

    /**
     * 设置 payCycle
     * @param payCycle payCycle
     */
    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
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
     * 获取 customerId
     * @return customerId.
     */
    public String getCustomerId() {
        return customerId;
    }


    
    /**
     * 设置 customerId
     * @param customerId customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
     * 获取 exerciseDate
     * @return exerciseDate.
     */
    public Date getExerciseDate() {
        return exerciseDate;
    }
    
    /**
     * 设置 exerciseDate
     * @param exerciseDate exerciseDate
     */
    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }
    public String getExerciseDateStr() {
        return DateUtil.format(this.getExerciseDate());
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIndustry2() {
        return industry2;
    }

    public void setIndustry2(String industry2) {
        this.industry2 = industry2;
    }

    public String getIsCityBondStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsCityBond());
    }

    public String getIsIssueAssetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsIssueAsset());
    }
    
    public String getIndustryStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.机构所属行业一级分类, this.getIndustry());
    }
    public String getIndustry2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.机构所属行业二级分类, this.getIndustry2());
    }

    public List<BusiFinProductRatingDto> getMstBondCreditRatingList() {
        return mstBondCreditRatingList;
    }

    public void setMstBondCreditRatingList(List<BusiFinProductRatingDto> mstBondCreditRatingList) {
        this.mstBondCreditRatingList = mstBondCreditRatingList;
    }

    public RepAssetBond getRepAssetBond() {
        return repAssetBond;
    }

    public void setRepAssetBond(RepAssetBond repAssetBond) {
        this.repAssetBond = repAssetBond;
    }

    public RepAssetDept getRepAssetDept() {
        return repAssetDept;
    }

    public void setRepAssetDept(RepAssetDept repAssetDept) {
        this.repAssetDept = repAssetDept;
    }

    public String getIsCityBond() {
        return isCityBond;
    }

    public void setIsCityBond(String isCityBond) {
        this.isCityBond = isCityBond;
    }

    public String getCityBondLev() {
        return cityBondLev;
    }

    public void setCityBondLev(String cityBondLev) {
        this.cityBondLev = cityBondLev;
    }

    public String getConFinprodMarketId() {
        return conFinprodMarketId;
    }

    public void setConFinprodMarketId(String conFinprodMarketId) {
        this.conFinprodMarketId = conFinprodMarketId;
    }

    public BussRepEquityInfoDto getBussRepEquityInfoDto() {
        return bussRepEquityInfoDto;
    }

    public void setBussRepEquityInfoDto(BussRepEquityInfoDto bussRepEquityInfoDto) {
        this.bussRepEquityInfoDto = bussRepEquityInfoDto;
    }

    public RepAssetEquity getRepAssetEquity() {
        return repAssetEquity;
    }

    public void setRepAssetEquity(RepAssetEquity repAssetEquity) {
        this.repAssetEquity = repAssetEquity;
    }

    public RepAssetOther getRepAssetOther() {
        return repAssetOther;
    }

    public void setRepAssetOther(RepAssetOther repAssetOther) {
        this.repAssetOther = repAssetOther;
    }

    /**
     * 获取 现金流计算规则dtolist
     * @return finCashCalcRuleList.
     */
    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }

    /**
     * 设置 现金流计算规则dtolist
     * @param finCashCalcRuleList finCashCalcRuleList
     */
    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public Date getIdate() {
        return idate;
    }

    public void setIdate(Date idate) {
        this.idate = idate;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Date getDelistingDate() {
        return delistingDate;
    }

    public void setDelistingDate(Date delistingDate) {
        this.delistingDate = delistingDate;
    }

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getIsSus() {
        return isSus;
    }

    public void setIsSus(String isSus) {
        this.isSus = isSus;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getIsNoGuard() {
        return isNoGuard;
    }

    public void setIsNoGuard(String isNoGuard) {
        this.isNoGuard = isNoGuard;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getIsAllowOverwrite() {
        return isAllowOverwrite;
    }

    public void setIsAllowOverwrite(String isAllowOverwrite) {
        this.isAllowOverwrite = isAllowOverwrite;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getSustainableRemark() {
        return sustainableRemark;
    }

    public void setSustainableRemark(String sustainableRemark) {
        this.sustainableRemark = sustainableRemark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BondFinProductConvertibleDto getFinProductConvertible() {
        return finProductConvertible;
    }

    public void setFinProductConvertible(BondFinProductConvertibleDto finProductConvertible) {
        this.finProductConvertible = finProductConvertible;
    }

    public BondFinInterestFactorDto getFinInterestFactorDto() {
        return finInterestFactorDto;
    }

    public void setFinInterestFactorDto(BondFinInterestFactorDto finInterestFactorDto) {
        this.finInterestFactorDto = finInterestFactorDto;
    }

    public BondFinPayInterestStdInfoDto getFinPayInterestStdInfoDto() {
        return finPayInterestStdInfoDto;
    }

    public void setFinPayInterestStdInfoDto(BondFinPayInterestStdInfoDto finPayInterestStdInfoDto) {
        this.finPayInterestStdInfoDto = finPayInterestStdInfoDto;
    }

    public void setInvRepayList(List<BondFinCashPlanDto> invRepayList) {
        this.invRepayList = invRepayList;
    }

    public void setFinExercisePlanList(List<BondFinExercisePlanDto> finExercisePlanList) {
        this.finExercisePlanList = finExercisePlanList;
    }

    private String oFinprodId;

    public String getoFinprodId() {
        return oFinprodId;
    }

    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId;
    }

    public String getvdateStr() {
        return DateUtil.format(this.getVdate());
    }

    private String couponSpecies;

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    private String chlFinprodId;

    public String getChlFinprodId() {
        return chlFinprodId;
    }

    public void setChlFinprodId(String chlFinprodId) {
        this.chlFinprodId = chlFinprodId;
    }

    private String finprodType2;

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public List<BondFinCashPlanDto> getInvRepayList() {
        return invRepayList;
    }

    public List<BondFinExercisePlanDto> getFinExercisePlanList() {
        return finExercisePlanList;
    }

    public List<BondFinProductIssueDto> getFinProductIssueList() {
        return finProductIssueList;
    }

    public void setFinProductIssueList(List<BondFinProductIssueDto> finProductIssueList) {
        this.finProductIssueList = finProductIssueList;
    }

    /**
     * 正股代码
     */
    private String conFinprodId;

    /**
     * 正股名称
     */
    private String conFinprodName;

    /**
     * 转股起始日
     */
    private Date conVdate;

    /**
     * 转股价格
     */
    private BigDecimal conPrice;

    /**
     * 转股比例
     */
    private BigDecimal conProportion;

    /**
     * 回售触发价
     */
    private BigDecimal resalePrice;

    /**
     * 赎回触发价
     */
    private BigDecimal redeemPrice;



    /**
     * 利率类型
     */
    private String calcType;

    /**
     * 参考收益率/初始利率
     */
    private BigDecimal yield;

    /**
     * 基准利率精度
     */
    private BigDecimal benchmarkIdAccuracy;

    /**
     * 基准利率
     */
    private BigDecimal basicRate;

    /**
     * 利率上限
     */
    private BigDecimal highestRate;

    /**
     * 利率下限
     */
    private BigDecimal lowestRate;

    /**
     * 基准利率调整规则
     */
    private String resetType;

    /**
     * 延后时间
     */
    private BigDecimal observeBefDay;

    /**
     * 延后时间单位
     */
    private String observeBefUnit;

    /**
     * 首次利率确定日
     */
    private Date firstConfirmDate;

    /**
     * 交易市场计息基础
     */
    private String marketBasis;

    /**
     * 利息分配方式
     */
    private String intpayRule;

    /**
     * 利息分配方式
     */
    private String interestCashId;

    /**
     * 利息分配方式
     */
    private String principalCashId;

    private String calcRule;

    private String payTdayRule;

    /**
     * 上市日
     */
    @ApiModelProperty(name="listingDate",value="上市日")
    private Date listingDate;

    public String getConFinprodId() {
        return conFinprodId;
    }

    public void setConFinprodId(String conFinprodId) {
        this.conFinprodId = conFinprodId;
    }

    public String getConFinprodName() {
        return conFinprodName;
    }

    public void setConFinprodName(String conFinprodName) {
        this.conFinprodName = conFinprodName;
    }

    public Date getConVdate() {
        return conVdate;
    }

    public void setConVdate(Date conVdate) {
        this.conVdate = conVdate;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public BigDecimal getConProportion() {
        return conProportion;
    }

    public void setConProportion(BigDecimal conProportion) {
        this.conProportion = conProportion;
    }

    public BigDecimal getResalePrice() {
        return resalePrice;
    }

    public void setResalePrice(BigDecimal resalePrice) {
        this.resalePrice = resalePrice;
    }

    public BigDecimal getRedeemPrice() {
        return redeemPrice;
    }

    public void setRedeemPrice(BigDecimal redeemPrice) {
        this.redeemPrice = redeemPrice;
    }

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public BigDecimal getBenchmarkIdAccuracy() {
        return benchmarkIdAccuracy;
    }

    public void setBenchmarkIdAccuracy(BigDecimal benchmarkIdAccuracy) {
        this.benchmarkIdAccuracy = benchmarkIdAccuracy;
    }

    public BigDecimal getBasicRate() {
        return basicRate;
    }

    public void setBasicRate(BigDecimal basicRate) {
        this.basicRate = basicRate;
    }

    public BigDecimal getHighestRate() {
        return highestRate;
    }

    public void setHighestRate(BigDecimal highestRate) {
        this.highestRate = highestRate;
    }

    public BigDecimal getLowestRate() {
        return lowestRate;
    }

    public void setLowestRate(BigDecimal lowestRate) {
        this.lowestRate = lowestRate;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public BigDecimal getObserveBefDay() {
        return observeBefDay;
    }

    public void setObserveBefDay(BigDecimal observeBefDay) {
        this.observeBefDay = observeBefDay;
    }

    public String getObserveBefUnit() {
        return observeBefUnit;
    }

    public void setObserveBefUnit(String observeBefUnit) {
        this.observeBefUnit = observeBefUnit;
    }

    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public String getMarketBasis() {
        return marketBasis;
    }

    public void setMarketBasis(String marketBasis) {
        this.marketBasis = marketBasis;
    }

    public String getIntpayRule() {
        return intpayRule;
    }

    public void setIntpayRule(String intpayRule) {
        this.intpayRule = intpayRule;
    }

    public String getInterestCashId() {
        return interestCashId;
    }

    public void setInterestCashId(String interestCashId) {
        this.interestCashId = interestCashId;
    }

    public String getPrincipalCashId() {
        return principalCashId;
    }

    public void setPrincipalCashId(String principalCashId) {
        this.principalCashId = principalCashId;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    private Long branch;

    private FinProductConvertible productConvertible;

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public FinProductConvertible getProductConvertible() {
        return productConvertible;
    }

    public void setProductConvertible(FinProductConvertible productConvertible) {
        this.productConvertible = productConvertible;
    }

    public String getTypeTwo() {
        return typeTwo;
    }

    public void setTypeTwo(String typeTwo) {
        this.typeTwo = typeTwo;
    }

    public String getTypeThree() {
        return typeThree;
    }

    public void setTypeThree(String typeThree) {
        this.typeThree = typeThree;
    }

    public String getTypeFour() {
        return typeFour;
    }

    public void setTypeFour(String typeFour) {
        this.typeFour = typeFour;
    }

    public String getTradeMarketId() {
        return tradeMarketId;
    }

    public void setTradeMarketId(String tradeMarketId) {
        this.tradeMarketId = tradeMarketId;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getCalcRule() {
        return calcRule;
    }

    public void setCalcRule(String calcRule) {
        this.calcRule = calcRule;
    }

    public String getPayTdayRule() {
        return payTdayRule;
    }

    public void setPayTdayRule(String payTdayRule) {
        this.payTdayRule = payTdayRule;
    }
}
