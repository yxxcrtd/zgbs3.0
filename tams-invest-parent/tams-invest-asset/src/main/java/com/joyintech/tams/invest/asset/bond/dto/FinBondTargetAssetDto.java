/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.*;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.bond.dto.BusiFinBondRatingInfoDto;
import com.joyintech.tams.component.business.bond.dto.BusiFinExercisePlanDto;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepEquityInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.bond.validator.FinBondTargetAssetValidator;
import com.joyintech.tams.invest.asset.common.dto.*;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 债券标的信息DTO
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see FinBondTargetAssetDto
 * @since 1.0
 */
@PageId("FinBondTargetAsset002")
@ValidLogic(targetClass = FinBondTargetAssetValidator.class , groups = {ValidateGroupEdit.class,ValidateGroupDelete.class})
public class FinBondTargetAssetDto extends FinProduct implements ITableRow,ProcessEntity{

    /**
     * 操作类型
     */
    private String operateType;
    
    private String pageModel;

    /**
     * 选择数据
     */
    private List<FinBondTargetAssetDto> selectedItem;

    /**
     * 要素id
     */
    private String elementId;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 基础组件-标的金融产品
     */
    private List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList;
    
    /**
     * 债券资产报送信息
     */
    private BaseRepBondAssetInfoDto baseRepBondAssetInfoDto;

    /**
     * 权益类资产中债报送
     */
    private BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto;
    
    /**
     * 负债信息
     */
    private BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto;

    /**
     * 中债报送新增
     */
    private BaseRepOtherDto baseRepOtherDto;
    
    /**
     * 附件
     */
    private MstFileInputParameter mstFileInputParameter;

    /**
     * 分类4（wind一级类型）
     */
    private String type4;

    /**
     * 分类5（wind二级类型）
     */
    private String type5;
    
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
    private String isPPBStr;
    
    /**
     * 是否净价交易
     */
    private String isNetprice;
    private String isNetpriceStr;
    

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

	/*@NotEmpty(fieldName = "BussRepEquityInfo.repAssetType", groups = {ValidateGroupEdit.class})*/
    public String getRepAssetType() {
        return repAssetType;
    }

    public void setRepAssetType(String repAssetType) {
        this.repAssetType = repAssetType;
    }

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
     * 是否非担保
     */
    private String isNoGuard;

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

    public String getIsNoGuard() {
        return isNoGuard;
    }

    public void setIsNoGuard(String isNoGuard) {
        this.isNoGuard = isNoGuard;
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
     * 计息要素DTO
     */
    @Valid
    private FinInterestFactorDto finInterestFactorDto;
    
    /**
     * 付息信息DTO
     */
    @Valid
    private FinPayInterestStdInfoDto finPayInterestStdInfoDto;
    
    /**
     * 还本信息DTO
     */
    @Valid
    private BussFinRepaymentDto bussFinRepaymentDto;
    /**
     * 行权计划DTO
     */
    @Valid
    private BusiFinExercisePlanDto busiFinExercisePlanDto;
    
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
     * 转债信息
     */
    private FinProductConvertible finProductConvertible;

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


    
    
    
    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }


    
    /**
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }


    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<FinBondTargetAssetDto> getSelectedItem() {
        return selectedItem;
    }



    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinBondTargetAssetDto> selectedItem) {
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
     * 获取 type4
     * @return type4.
     */
    public String getType4() {
        return type4;
    }

    
    /**
     * 设置 type4
     * @param type4 type4
     */
    public void setType4(String type4) {
        this.type4 = type4;
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


    /**
     * 获取债券一级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType4Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND一级分类_含债券一级分类等, this.getType4());
    }
    
    /**
     * 获取 type5
     * @return type5.
     */
    public String getType5() {
        return type5;
    }

    
    /**
     * 设置 type5
     * @param type5 type5
     */
    public void setType5(String type5) {
        this.type5 = type5;
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
     * 设置 type12
     * @param type12
     */
	public void setType12(String type12) {
		this.type12 = type12;
	}
	
    /**
     * 原金融产品代码
     * {@inheritDoc}
     */
    @Override
    @PrimaryKey(Field.PK_2)
    public String getoFinprodId(){
        return super.getoFinprodId();
    }
	/**added by wangguiqi on 2019/7/19 -start**/
    
	/**
     * 获取债券二级类型name
     * @return
     * @see
     * @since 1.0
     */
    public String getType5Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND二级分类_含债券二级分类等, this.getType5());
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
     * 金融产品代码
     */
    @Override
    public String getFinprodId(){
        return super.getFinprodId();
    }
    /**
     * 市场代码
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName="FinBondTargetAsset002.finprodId",groups = {ValidateGroupEdit.class})
//    @Code(fieldName = "FinBondTargetAsset002.finprodId", length = 32, groups = {ValidateGroupEdit.class})
    @Pattern(fieldName = "FinBondTargetAsset002.finprodId",regx = "^[A-Za-z0-9]+.[A-Za-z0-9]+$",message = "FW_E_0002",groups={ValidateGroupEdit.class})
    @PrimaryKey(Field.PK_1)
    public String getFinprodMarketId(){
        return super.getFinprodMarketId();
    }
    
    /**
     * 资产名称
     * {@inheritDoc}
     */
    //ZGBSSE-571 【中债直连】债券标的新建页面，资产名称文本框可输入长度控制不正确
    @Override
    @Length(fieldName = "FinBondTargetAsset002.finprodName", max = 100, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "FinBondTargetAsset002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodName(){
        return super.getFinprodName();
    }
    
    /**
     * 资产名称
     * {@inheritDoc}
     */
    @Override
    @Length(fieldName = "FinBondTargetAsset002.finprodAbbr", max = 100, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "FinBondTargetAsset002.finprodAbbr", groups = {ValidateGroupEdit.class})
    public String getFinprodAbbr(){
        return super.getFinprodAbbr();
    }
    
    /**
     * 交易场所
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.tradeMarket", groups = {ValidateGroupEdit.class})
    public String getTradeMarket(){
        return super.getTradeMarket();
    }
    
    /**
     * 获取交易场所name
     * @return
     * @see
     * @since 1.0
     */
    public String getTradeMarketName() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }
    /**
     * 发行价
     * {@inheritDoc}
     */
    @Override
    @Amount(fieldName = "FinBondTargetAsset002.issuePrice", maxIntBit = 16, maxDecBit = 5, groups = {
        ValidateGroupEdit.class})
    @NotEmpty(fieldName = "FinBondTargetAsset002.issuePrice", groups = {ValidateGroupEdit.class})
    public BigDecimal getIssuePrice(){
        return super.getIssuePrice();
    }
    
    @Override
    @Length(fieldName = "FinBondTargetAsset002.remark", max = 1000, groups = {ValidateGroupEdit.class})
    public String getRemark(){
        return super.getRemark();
    }
    
    /**
     * 发行日
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.idate", groups = {ValidateGroupEdit.class})
    public Date getIdate(){
        return super.getIdate();
    }
    
    /**
     * 起息日
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate(){
        return super.getVdate();
    }
    /**
     * 到期日
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate(){
        return super.getMdate();
    }
    
    /**
     * 发行金额
     * {@inheritDoc}
     */
    @Override
    @Amount(fieldName = "FinBondTargetAsset002.issueAmt", maxIntBit = 30, groups = {
        ValidateGroupEdit.class})
    @NotEmpty(fieldName = "FinBondTargetAsset002.issueAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getIssueAmt(){
        return super.getIssueAmt();
    }
    
    /**
     * 发行人
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.issuer", groups = {ValidateGroupEdit.class})
    public String getIssuer(){
        return super.getIssuer();
    }
    
    /**
     * 发行方式
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.issueType", groups = {ValidateGroupEdit.class})
    public String getIssueType(){
        return super.getIssueType();
    }
    
    /**
     * 币种
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy(){
        return super.getCcy();
    }
    
    public String getCcyName(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }
    
    /**
     * 币种
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.tradeCalendarCode", groups = {ValidateGroupEdit.class})
    public String getCalendarId(){
        return super.getCalendarId();
    }


    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.blnArea", groups = {ValidateGroupEdit.class})
    public String getBlnArea(){
        return super.getBlnArea();
    }


    @Override
    @NotEmpty(fieldName = "FinBondTargetAsset002.capiIncomeFeature", groups = {ValidateGroupEdit.class})
    public String getCapiIncomeFeature(){
        return super.getCapiIncomeFeature();
    }



    /**
     * 获取 finInterestFactorDto
     * @return finInterestFactorDto.
     */
    public FinInterestFactorDto getFinInterestFactorDto() {
        return finInterestFactorDto;
    }







    
    /**
     * 设置 finInterestFactorDto
     * @param finInterestFactorDto finInterestFactorDto
     */
    public void setFinInterestFactorDto(FinInterestFactorDto finInterestFactorDto) {
        this.finInterestFactorDto = finInterestFactorDto;
    }


    
    /**
     * 获取 finPayInterestStdInfoDto
     * @return finPayInterestStdInfoDto.
     */
    public FinPayInterestStdInfoDto getFinPayInterestStdInfoDto() {
        return finPayInterestStdInfoDto;
    }


    
    /**
     * 设置 finPayInterestStdInfoDto
     * @param finPayInterestStdInfoDto finPayInterestStdInfoDto
     */
    public void setFinPayInterestStdInfoDto(FinPayInterestStdInfoDto finPayInterestStdInfoDto) {
        this.finPayInterestStdInfoDto = finPayInterestStdInfoDto;
    }

    
    
    
    
    /**
     * 获取 bussFinRepaymentDto
     * @return bussFinRepaymentDto.
     */
    public BussFinRepaymentDto getBussFinRepaymentDto() {
        return bussFinRepaymentDto;
    }


    
    /**
     * 设置 bussFinRepaymentDto
     * @param bussFinRepaymentDto bussFinRepaymentDto
     */
    public void setBussFinRepaymentDto(BussFinRepaymentDto bussFinRepaymentDto) {
        this.bussFinRepaymentDto = bussFinRepaymentDto;
    }


    /**
     * 获取 busiFinExercisePlanDto
     * @return busiFinExercisePlanDto.
     */
    public BusiFinExercisePlanDto getBusiFinExercisePlanDto() {
        return busiFinExercisePlanDto;
    }


    
    /**
     * 设置 busiFinExercisePlanDto
     * @param busiFinExercisePlanDto busiFinExercisePlanDto
     */
    public void setBusiFinExercisePlanDto(BusiFinExercisePlanDto busiFinExercisePlanDto) {
        this.busiFinExercisePlanDto = busiFinExercisePlanDto;
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
     * 获取 baseRepBondAssetInfoDto
     * @return baseRepBondAssetInfoDto.
     */
    public BaseRepBondAssetInfoDto getBaseRepBondAssetInfoDto() {
        return baseRepBondAssetInfoDto;
    }


    
    /**
     * 设置 baseRepBondAssetInfoDto
     * @param baseRepBondAssetInfoDto baseRepBondAssetInfoDto
     */
    public void setBaseRepBondAssetInfoDto(BaseRepBondAssetInfoDto baseRepBondAssetInfoDto) {
        this.baseRepBondAssetInfoDto = baseRepBondAssetInfoDto;
    }


    
    /**
     * 获取 baseRepAssetDeptInfoDto
     * @return baseRepAssetDeptInfoDto.
     */
    public BaseRepAssetDeptInfoDto getBaseRepAssetDeptInfoDto() {
        return baseRepAssetDeptInfoDto;
    }


    
    /**
     * 设置 baseRepAssetDeptInfoDto
     * @param baseRepAssetDeptInfoDto baseRepAssetDeptInfoDto
     */
    public void setBaseRepAssetDeptInfoDto(BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto) {
        this.baseRepAssetDeptInfoDto = baseRepAssetDeptInfoDto;
    }


    /**
     * 息票品种name
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getCouponSpeciesName(){
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, this.getCouponSpecies());
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
     * 取得 登记托管机构
     * @return 登记托管机构
     */
    @Override
    @NotEmpty(fieldName="FinBondTargetAsset002.registOrg",groups = {ValidateGroupEdit.class})
    public String getRegistOrg() {
        return super.getRegistOrg();
    }
    
    /**
     * 登记托管机构名称
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getRegistOrgName(){
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.getRegistOrg());
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
        /*if(StringUtil.isNotEmpty(this.getBasis())){
           String[] arr =  this.getBasis().split(",");
           if(arr.length>1){
               return SysDataUtil.getNameFromDataDict(DictCode.计息基础, arr[0])+","
                       + SysDataUtil.getNameFromDataDict(DictCode.利息分配方式, arr[1]);
           }
        }
        return null;*/
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
     * 企业性质
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getPartyType2Name(){
        return SysDataUtil.getNameFromDataDict(DictCode.发行机构类型_按经济类型划分, this.getPartyType2());
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
    
    public String getvdateStr(){
        return DateUtil.format(this.getVdate());
    }

    public String getMdateStr(){
        return DateUtil.format(this.getMdate());
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
    public String getIdateStr(){
        return DateUtil.format(super.getIdate());
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
    
    /**
     * 获取 IsSusStr
     * @return IsSusStr.
     */
    public String getIsSusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsSus());
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

    /**
     * 获取 IsRightStr
     * @return IsRightStr.
     */
    public String getIsRightStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsRight());
    }
    public String getIsCityBondStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsCityBond());
    }

    public String getIsIssueAssetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsIssueAsset());
    }

    public String getIsNoGuardStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsNoGuard());
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

    public FinProductConvertible getFinProductConvertible() {
        return finProductConvertible;
    }

    public void setFinProductConvertible(FinProductConvertible finProductConvertible) {
        this.finProductConvertible = finProductConvertible;
    }

    public String getConFinprodMarketId() {
        return conFinprodMarketId;
    }

    public void setConFinprodMarketId(String conFinprodMarketId) {
        this.conFinprodMarketId = conFinprodMarketId;
    }

    public BaseRepAssetEquityInfoDto getBaseRepAssetEquityInfoDto() {
        return baseRepAssetEquityInfoDto;
    }

    public void setBaseRepAssetEquityInfoDto(BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto) {
        this.baseRepAssetEquityInfoDto = baseRepAssetEquityInfoDto;
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

    public BaseRepOtherDto getBaseRepOtherDto() {
        return baseRepOtherDto;
    }

    public void setBaseRepOtherDto(BaseRepOtherDto baseRepOtherDto) {
        this.baseRepOtherDto = baseRepOtherDto;
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
}
