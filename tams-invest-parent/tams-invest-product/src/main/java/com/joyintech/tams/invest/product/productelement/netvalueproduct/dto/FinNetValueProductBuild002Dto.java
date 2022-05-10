/*
 * 文件名：FinProductBuild002Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.product.productelement.netvalueproduct.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.Rate;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkLayeredInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductBonusPlanDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFeeInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFloatFeeDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFundAccountInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductGoalInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductInvestDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductInvestPeriodDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductLayeredInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductPayPlanDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductRevalDocumentDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductSaleParamDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductValuationDto;
import com.joyintech.tams.component.business.report.prod.dto.BussRepFinProductInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.product.productelement.anaplerotic.dto.FinProductAnapleroticInfoDto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.validator.FinNetValueProductBuild002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;


/**
 * 产品新建管理信息dto
 * 
 * @author zhangliang
 * @version 1.0 2018年10月13日
 * @see FinNetValueProductBuild002Dto
 * @since 1.0
 */
@ValidLogic(targetClass = FinNetValueProductBuild002Validator.class)
public class FinNetValueProductBuild002Dto extends FinProduct implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -8543840737635969481L;
    
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 费用新建缓存key值
     */
    private String feeNewAddId;
    
    /**
     * 确认标识
     */
    private String confirmFlag;
    
    /**
     * 元素Id
     */
    private String elementId;
    
    /**
     * 分支序号
     */
    private Long branch;
    
    /**
     * 画面Id
     */
    private String pageId;

    /**
     * 模板代码
     */
    private String tmplId;
    
    /**
     * 组合代码
     */
    private String ptlId;
    
    /**
     * 处理模式
     */
    private String dealMode;
    
    /**
     * 利率类型
     */
    private String calcType;
    
    /**
     * 是否周期型
     */
    private String isCycle;
    
    /**
     * 是否分层
     */
    private String isLay;
    
    /**
     * 分层类型
     */
    private String layType;
    
    /**
     * 产品收益标识
     */
    private String profitFlag;
    
    /**
     * 预期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;
    
    /**
     * 募集开始日
     */
    private Date firstSaleVdate;
    
    /**
     * 募集结束日
     */
    private Date firstSaleMdate;
    
    /**
     * 封闭期限
     */
    private Integer closeDays;
    
    /**
     * 计息基础
     */
    private String basis;
    
    /**
     * 销售部门
     */
    private String saleDepartment;
    
    /**
     * 申赎确认天数
     */
    private Integer purSpeed;
    
    /**
     * 赎回确认天数
     */
    private Integer redSpeed;
    
    /**
     * 投资性质
     */
    private String investNature;
    
    /**
     * 销售模式
     */
    private String saleMode;
    
    /**
     * 有效状态
     */
    private String effectStatus;
    
    private String period;
    private Integer investDays;
    
    /**
     * 发行状态
     */
    private String issueStatus;
    
    /********* add by zhangweiqi 主产品需求添加 begin**************/
    /**
     * 产品系列
     */
    private String prodSeries;
    
    
    
    public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getInvestDays() {
		return investDays;
	}

	public void setInvestDays(Integer investDays) {
		this.investDays = investDays;
	}

	/**
     * 产品分类
     */
    private String prodClass;
    /**
     * 产品特点
     */
    
    private String prodFeature;
    
    /**
     * 托管机构
     * 
     */
    private String trusteeId;
    
   /**
     * 专属产品
     * 
     */
    private String exclusiveProd;
    
    /**
     * 理财经理
     */
    private String  prodManager;
    
    /**
     * 销售类型
     */
    private String saleType;
    
    /**
     * 投资周期
     */
    private String investmentCycle;
    
    /**
     * 是否续投 continue_Flag
     */
    private String continueFlag;
    
    /**
     * 同业机构
     */
    private String sameOrg;
    
    /**
     * 是否支持代销
     */
    private String consignmentFlag;
    
    /**
     * 是否允许提前终止
     * BEFOREEND_FLAG
     */
    private String beforeEndFlag;
    
    /**
     * 是否允许客户赎回
     */
    private String redFlag;
    /**
     * 是否可违约赎回 DEFAULTRED_FLAG
     */
    private String defaultRedFlag;
    /**
     * 是否可提前成立 beforeestablish_FLAG
     */
    private String beforeEstablishFlag;
    /**
     * 是否收益权转让产品 add dgf 20191230
     */
//    private String repFiduciaryDuty;
    
   /**
    * 实际到期日
    */
    private Date actMdate;
    
    /**
     * 分红方式
     */
    
    private String payType;
    
    /**
     * 是否永续
     */
    private String isSus;
    
    /**
     * 产品登记编码
     */
    private String prodRegistCode;
    
    /**
     * 目标客户一级分类
     */
    private String targetCustomer1;
    /**
     * 目标客户二级分类
     */
    private String targetCustomer2;
    
    /**
     * 数据来源
     */
    private String dataSource;
    
    /********* add by zhangweiqi 主产品需求添加 end**************/
    private String termType;

    /**
     *
     * 说明书是否披露风险标志
     */
    private String directDisclosureRiskFlag;

    @NotEmpty(fieldName = "FinProductBuild002.directDisclosureRiskFlag")
    public String getDirectDisclosureRiskFlag() {
        return directDisclosureRiskFlag;
    }

    public void setDirectDisclosureRiskFlag(String directDisclosureRiskFlag) {
        this.directDisclosureRiskFlag = directDisclosureRiskFlag;
    }
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductPayPlanDto bussFinProductPayPlanDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanDto;
    /**
     * add by zhangweiqi 
     */
    @Valid
    private BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductBonusPlanDto bussFinProductBonusPlanDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductFeeInfoDto bussFinProductFeeInfoDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussBenchMarkInfoDto bussBenchMarkInfoDto;
    @Valid
    private BussBenchMarkInfoDto bussBenchMarkFloatInfoDto;
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductFloatFeeDto bussFinProductFloatFeeDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductSaleParamDto bussFinProductSaleParamDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductValuationDto bussFinProductValuationDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductGoalInfoDto bussFinProductGoalInfoDto;
    
    /**
     * {@linkplain}
     */
    @Valid
    private BussRepFinProductInfoDto bussRepFinProductInfoDto;
    
    /**
     * 分层款型业绩基准
     */
    @Valid
    private BussBenchMarkLayeredInfoDto bussBenchMarkLayeredInfoDto;
    /**
     * {@linkplain}
     */
    @Valid
    private BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto;
    
    @Valid
    private List<BussFinProductFundAccountInfoDto> bussFinProductFundAccountInfoDto;
    
    /**
     * add by yanya 20190823 产品投向
     */
    @Valid
    private BussFinProductInvestDto bussFinProductInvestDto;
    
    /**
     * add by wm 期限类别
     */
    @Valid
    private BussFinProductInvestPeriodDto bussFinProductInvestPeriodDto;
    
    
    public BussFinProductInvestDto getBussFinProductInvestDto() {
		return bussFinProductInvestDto;
	}

	public void setBussFinProductInvestDto(BussFinProductInvestDto bussFinProductInvestDto) {
		this.bussFinProductInvestDto = bussFinProductInvestDto;
	}
	
    //add by yejiejie start 分层信息
    @Valid
    private List<BussFinProductLayeredInfoDto> bussFinProductLayeredInfoDto;
    
    /**
     * 是否现金管理类
     */
    private String isCashManage;
    
    public String getIsCashManage() {
		return isCashManage;
	}

	public void setIsCashManage(String isCashManage) {
		this.isCashManage = isCashManage;
	}
    
	//add by niuqianyu 2019-07-18 start
	/**
     * 附件临时Id
     */
    private String fjId;
	/**
     * 文件上传输入参数
     */
    private MstFileInputParameter mstFileInputParameter;
    /**
     * 附件信息DTO
     */
    private List<FileCommonDto> fileCommonList;
    /**
     * 行内标识码
     */
    private String repFinprodInsId;
    
    /**
     * 产品设计系列编号 yzg 20191107
     */
    private String prdDesignId;
    /**
     * 产品设计系列名称 yzg 20191107
     */
    private String prdDesignName;
    
    /**
     * 是否合规新产品
     */
    private String isFhxgcp;
    /**
     * 是否养老理财产品
     */
    private String isYllccp;
    
	public String getFjId() {
		return fjId;
	}


	public void setFjId(String fjId) {
		this.fjId = fjId;
	}


	public MstFileInputParameter getMstFileInputParameter() {
		return mstFileInputParameter;
	}


	public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
		this.mstFileInputParameter = mstFileInputParameter;
	}


	public List<FileCommonDto> getFileCommonList() {
		return fileCommonList;
	}


	public void setFileCommonList(List<FileCommonDto> fileCommonList) {
		this.fileCommonList = fileCommonList;
	}
	//add by niuqianyu 2019-07-18 end	
    /**
     * 获取 pageId
     * @return pageId.
     */
    public String getPageId() {
        return pageId;
    }
    
    /**
     * 设置 pageId
     * @param pageId pageId
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }


    public String getTmplId() {
        return tmplId;
    }

    
    public void setTmplId(String tmplId) {
        this.tmplId = tmplId;
    }

    
    public String getPtlId() {
        return ptlId;
    }

    
    public void setPtlId(String ptlId) {
        this.ptlId = ptlId;
    }

    @NotEmpty(fieldName = "FinProductBuild002.dealMode")
    public String getDealMode() {
        return dealMode;
    }

    
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.operationType")
    public String getOperationType() {
        return super.getOperationType();
    }

    
    public String getCalcType() {
        return calcType;
    }

    
    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    public String getIsCycle() {
        return isCycle;
    }

    
    public void setIsCycle(String isCycle) {
        this.isCycle = isCycle;
    }

    @NotEmpty(fieldName = "FinProductBuild002.isLay")
    public String getIsLay() {
        return isLay;
    }

    
    public void setIsLay(String isLay) {
        this.isLay = isLay;
    }

    
    public String getLayType() {
        return layType;
    }

    
    public void setLayType(String layType) {
        this.layType = layType;
    }

    @NotEmpty(fieldName = "FinProductBuild002.profitFlag")
    public String getProfitFlag() {
        return profitFlag;
    }

    
    public void setProfitFlag(String profitFlag) {
        this.profitFlag = profitFlag;
    }

    
    @Rate(fieldName = "FinProductBuild002.yield", maxDecBit = 4)
    public BigDecimal getYield() {
        return yield;
    }

    
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    @NotEmpty(fieldName = "FinProductBuild002.firstSaleVdate")
    public Date getFirstSaleVdate() {
        return firstSaleVdate;
    }

    
    public void setFirstSaleVdate(Date firstSaleVdate) {
        this.firstSaleVdate = firstSaleVdate;
    }

    @NotEmpty(fieldName = "FinProductBuild002.firstSaleMdate")
    public Date getFirstSaleMdate() {
        return firstSaleMdate;
    }

    
    public void setFirstSaleMdate(Date firstSaleMdate) {
        this.firstSaleMdate = firstSaleMdate;
    }

    public Integer getCloseDays() {
        return closeDays;
    }

    
    public void setCloseDays(Integer closeDays) {
        this.closeDays = closeDays;
    }

    @NotEmpty(fieldName = "FinProductBuild002.basis")
    public String getBasis() {
        return basis;
    }

    
    public void setBasis(String basis) {
        this.basis = basis;
    }

   // @NotEmpty(fieldName = "FinProductBuild002.saleDepartment")
    public String getSaleDepartment() {
        return saleDepartment;
    }

    
    public void setSaleDepartment(String saleDepartment) {
        this.saleDepartment = saleDepartment;
    }

    
    public Integer getPurSpeed() {
        return purSpeed;
    }

    
    public void setPurSpeed(Integer purSpeed) {
        this.purSpeed = purSpeed;
    }

    @NotEmpty(fieldName = "FinProductBuild002.investNature")
    public String getInvestNature() {
        return investNature;
    }

    
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }

    //@NotEmpty(fieldName = "FinProductBuild002.saleMode")
    public String getSaleMode() {
        return saleMode;
    }

    
    public void setSaleMode(String saleMode) {
        this.saleMode = saleMode;
    }

    
    public BussRepFinProductInfoDto getBussRepFinProductInfoDto() {
        return bussRepFinProductInfoDto;
    }

    
    public void setBussRepFinProductInfoDto(BussRepFinProductInfoDto bussRepFinProductInfoDto) {
        this.bussRepFinProductInfoDto = bussRepFinProductInfoDto;
    }
    
    @Override
    public String getFinprodId() {
        return super.getFinprodId();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.finprodMarketId")
    @Length(fieldName = "FinProductBuild002.finprodMarketId", max = 20)
    @PrimaryKey(Field.PK_2)
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.finprodName")
    @Length(fieldName = "FinProductBuild002.finprodName", max = 200)
    public String getFinprodName() {
        return super.getFinprodName();
    }
    
    @PrimaryKey(Field.PK_3)
    public String getFinprodNameMd5() {
        return EncryptUtil.getInstance().md5(super.getFinprodName());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.finprodAbbr")
    @Length(fieldName = "FinProductBuild002.finprodAbbr", max = 200)
    public String getFinprodAbbr() {
        return super.getFinprodAbbr();
    }
    
    @PrimaryKey(Field.PK_4)
    public String getFinprodAbbrMd5() {
        return EncryptUtil.getInstance().md5(super.getFinprodAbbr());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.profitType")
    public String getProfitType() {
        return super.getProfitType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
   // @NotEmpty(fieldName = "FinProductBuild002.entrustType")
    public String getEntrustType() {
        return super.getEntrustType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Length(fieldName = "FinProductBuild002.entruster", max = 2000)
    public String getEntruster() {
        return super.getEntruster();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.vdate")
    public Date getVdate() {
        return super.getVdate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.mdate")
    public Date getMdate() {
        return super.getMdate();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
   // @NotEmpty(fieldName = "FinProductBuild002.termDays")
    public Long getTermDays() {
        return super.getTermDays();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.calendarId")
    public String getCalendarId() {
        return super.getCalendarId();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.ccy")
    public String getCcy() {
        return super.getCcy();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinProductBuild002.issueType")
    public String getIssueType() {
        return super.getIssueType();
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
     * 获取 bussFinProductSaleParamDto
     * @return bussFinProductSaleParamDto.
     */
    public BussFinProductSaleParamDto getBussFinProductSaleParamDto() {
        return bussFinProductSaleParamDto;
    }


    
    
    /**
     * 获取 bussBenchMarkInfoDto
     * @return bussBenchMarkInfoDto.
     */
    public BussBenchMarkInfoDto getBussBenchMarkInfoDto() {
        return bussBenchMarkInfoDto;
    }


    
    /**
     * 设置 bussBenchMarkInfoDto
     * @param bussBenchMarkInfoDto bussBenchMarkInfoDto
     */
    public void setBussBenchMarkInfoDto(BussBenchMarkInfoDto bussBenchMarkInfoDto) {
        this.bussBenchMarkInfoDto = bussBenchMarkInfoDto;
    }


    
    public BussBenchMarkInfoDto getBussBenchMarkFloatInfoDto() {
		return bussBenchMarkFloatInfoDto;
	}

	public void setBussBenchMarkFloatInfoDto(BussBenchMarkInfoDto bussBenchMarkFloatInfoDto) {
		this.bussBenchMarkFloatInfoDto = bussBenchMarkFloatInfoDto;
	}

	/**
     * 获取 bussFinProductFloatFeeDto
     * @return bussFinProductFloatFeeDto.
     */
    public BussFinProductFloatFeeDto getBussFinProductFloatFeeDto() {
        return bussFinProductFloatFeeDto;
    }


    
    /**
     * 设置 bussFinProductFloatFeeDto
     * @param bussFinProductFloatFeeDto bussFinProductFloatFeeDto
     */
    public void setBussFinProductFloatFeeDto(BussFinProductFloatFeeDto bussFinProductFloatFeeDto) {
        this.bussFinProductFloatFeeDto = bussFinProductFloatFeeDto;
    }


    /**
     * 设置 bussFinProductSaleParamDto
     * @param bussFinProductSaleParamDto bussFinProductSaleParamDto
     */
    public void setBussFinProductSaleParamDto(BussFinProductSaleParamDto bussFinProductSaleParamDto) {
        this.bussFinProductSaleParamDto = bussFinProductSaleParamDto;
    }


    
    /**
     * 获取 bussFinProductValuationDto
     * @return bussFinProductValuationDto.
     */
    public BussFinProductValuationDto getBussFinProductValuationDto() {
        return bussFinProductValuationDto;
    }


    
    /**
     * 设置 bussFinProductValuationDto
     * @param bussFinProductValuationDto bussFinProductValuationDto
     */
    public void setBussFinProductValuationDto(BussFinProductValuationDto bussFinProductValuationDto) {
        this.bussFinProductValuationDto = bussFinProductValuationDto;
    }


    
    /**
     * 获取 bussFinProductGoalInfoDto
     * @return bussFinProductGoalInfoDto.
     */
    public BussFinProductGoalInfoDto getBussFinProductGoalInfoDto() {
        return bussFinProductGoalInfoDto;
    }

    /**
     * 设置 bussFinProductGoalInfoDto
     * @param bussFinProductGoalInfoDto bussFinProductGoalInfoDto
     */
    public void setBussFinProductGoalInfoDto(BussFinProductGoalInfoDto bussFinProductGoalInfoDto) {
        this.bussFinProductGoalInfoDto = bussFinProductGoalInfoDto;
    }

    
    /**
     * 获取 bussFinProductFeeInfoDto
     * @return bussFinProductFeeInfoDto.
     */
    public BussFinProductFeeInfoDto getBussFinProductFeeInfoDto() {
        return bussFinProductFeeInfoDto;
    }


    /**
     * 设置 bussFinProductFeeInfoDto
     * @param bussFinProductFeeInfoDto bussFinProductFeeInfoDto
     */
    public void setBussFinProductFeeInfoDto(BussFinProductFeeInfoDto bussFinProductFeeInfoDto) {
        this.bussFinProductFeeInfoDto = bussFinProductFeeInfoDto;
    }

    
    /**
     * 获取 bussFinProductBonusPlanDto
     * @return bussFinProductBonusPlanDto.
     */
    public BussFinProductBonusPlanDto getBussFinProductBonusPlanDto() {
        return bussFinProductBonusPlanDto;
    }

    /**
     * 设置 bussFinProductBonusPlanDto
     * @param bussFinProductBonusPlanDto bussFinProductBonusPlanDto
     */
    public void setBussFinProductBonusPlanDto(BussFinProductBonusPlanDto bussFinProductBonusPlanDto) {
        this.bussFinProductBonusPlanDto = bussFinProductBonusPlanDto;
    }
    
    /**
     * 获取 bussFinProductOpenPlanDto
     * @return bussFinProductOpenPlanDto.
     */
    public BussFinProductOpenPlanDto getBussFinProductOpenPlanDto() {
        return bussFinProductOpenPlanDto;
    }

    /**
     * 设置 bussFinProductOpenPlanDto
     * @param bussFinProductOpenPlanDto bussFinProductOpenPlanDto
     */
    public void setBussFinProductOpenPlanDto(BussFinProductOpenPlanDto bussFinProductOpenPlanDto) {
        this.bussFinProductOpenPlanDto = bussFinProductOpenPlanDto;
    }


    
    /**
     * 获取 bussFinProductPayPlanDto
     * @return bussFinProductPayPlanDto.
     */
    public BussFinProductPayPlanDto getBussFinProductPayPlanDto() {
        return bussFinProductPayPlanDto;
    }


    
    /**
     * 设置 bussFinProductPayPlanDto
     * @param bussFinProductPayPlanDto bussFinProductPayPlanDto
     */
    public void setBussFinProductPayPlanDto(BussFinProductPayPlanDto bussFinProductPayPlanDto) {
        this.bussFinProductPayPlanDto = bussFinProductPayPlanDto;
    }
    
    /**
     * 获取 bussFinProductRevalDocumentDto
     * @return bussFinProductRevalDocumentDto.
     */
    public BussFinProductRevalDocumentDto getBussFinProductRevalDocumentDto() {
        return bussFinProductRevalDocumentDto;
    }
    
    /**
     * 设置 bussFinProductRevalDocumentDto
     * @param bussFinProductRevalDocumentDto bussFinProductRevalDocumentDto
     */
    public void setBussFinProductRevalDocumentDto(BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto) {
        this.bussFinProductRevalDocumentDto = bussFinProductRevalDocumentDto;
    }


    /**
     * 获取 branch
     * @return branch.
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 branch
     * @param branch branch
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    private transient List<FinNetValueProductBuild002Dto> selectedItem;


    
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<FinNetValueProductBuild002Dto> getSelectedItem() {
        return selectedItem;
    }

    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinNetValueProductBuild002Dto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    
    /**
     * 获取 confirmFlag
     * @return confirmFlag.
     */
    public String getConfirmFlag() {
        return confirmFlag;
    }

    
    /**
     * 设置 confirmFlag
     * @param confirmFlag confirmFlag
     */
    public void setConfirmFlag(String confirmFlag) {
        this.confirmFlag = confirmFlag;
    }

    
    /**
     * 获取 effectStatus
     * @return effectStatus.
     */
    public String getEffectStatus() {
        return effectStatus;
    }

    
    /**
     * 设置 effectStatus
     * @param effectStatus effectStatus
     */
    public void setEffectStatus(String effectStatus) {
        this.effectStatus = effectStatus;
    }

    
    /**
     * 获取 issueStatus
     * @return issueStatus.
     */
    public String getIssueStatus() {
        return issueStatus;
    }

    
    /**
     * 设置 issueStatus
     * @param issueStatus issueStatus
     */
    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }
    
    /**
     * 获取 feeNewAddId
     * @return feeNewAddId.
     */
    public String getFeeNewAddId() {
        return feeNewAddId;
    }

    
    /**
     * 设置 feeNewAddId
     * @param feeNewAddId feeNewAddId
     */
    public void setFeeNewAddId(String feeNewAddId) {
        this.feeNewAddId = feeNewAddId;
    }

	public String getProdSeries() {
		return prodSeries;
	}

	public void setProdSeries(String prodSeries) {
		this.prodSeries = prodSeries;
	}

	public String getProdClass() {
		return prodClass;
	}

	public void setProdClass(String prodClass) {
		this.prodClass = prodClass;
	}

	public String getProdFeature() {
		return prodFeature;
	}

	public void setProdFeature(String prodFeature) {
		this.prodFeature = prodFeature;
	}

	
	@NotEmpty(fieldName = "FinProductBuild002.truOrg")
	public String getTrusteeId() {
		return trusteeId;
	}

	public void setTrusteeId(String trusteeId) {
		this.trusteeId = trusteeId;
	}
	
	@NotEmpty(fieldName = "FinProductBuild002.exclusiveProd")
	public String getExclusiveProd() {
		return exclusiveProd;
	}

	public void setExclusiveProd(String exclusiveProd) {
		this.exclusiveProd = exclusiveProd;
	}

	

	public String getSaleType() {
		return saleType;
	}

	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}
	@NotEmpty(fieldName = "FinProductBuild002.consignmentFlag")
	public String getConsignmentFlag() {
		return consignmentFlag;
	}
	
	public void setConsignmentFlag(String consignmentFlag) {
		this.consignmentFlag = consignmentFlag;
	}
	@NotEmpty(fieldName = "FinProductBuild002.beforeEndFlag")
	public String getBeforeEndFlag() {
		return beforeEndFlag;
	}

	public void setBeforeEndFlag(String beforeEndFlag) {
		this.beforeEndFlag = beforeEndFlag;
	}
	 
	@NotEmpty(fieldName = "FinProductBuild002.redFlag")
	public String getRedFlag() {
		return redFlag;
	}

	public void setRedFlag(String redFlag) {
		this.redFlag = redFlag;
	}

	@NotEmpty(fieldName = "FinProductBuild002.defaultRedFlag")
	public String getDefaultRedFlag() {
		return defaultRedFlag;
	}

	public void setDefaultRedFlag(String defaultRedFlag) {
		this.defaultRedFlag = defaultRedFlag;
	}
	@NotEmpty(fieldName = "FinProductBuild002.beforeEstablishFlag")
	public String getBeforeEstablishFlag() {
		return beforeEstablishFlag;
	}
//    @NotEmpty(fieldName = "FinProductBuild002.repFiduciaryDuty")
//    public String getRepFiduciaryDuty() {
//        return repFiduciaryDuty;
//    }
//    public void setRepFiduciaryDuty(String repFiduciaryDuty) {
//        this.repFiduciaryDuty = repFiduciaryDuty;
//    }

    public void setBeforeEstablishFlag(String beforeEstablishFlag) {
		this.beforeEstablishFlag = beforeEstablishFlag;
	}

	public String getInvestmentCycle() {
		return investmentCycle;
	}

	public void setInvestmentCycle(String investmentCycle) {
		this.investmentCycle = investmentCycle;
	}

	public String getContinueFlag() {
		return continueFlag;
	}

	public void setContinueFlag(String continueFlag) {
		this.continueFlag = continueFlag;
	}


	public BussFinProductOpenPlanDto getBussFinProductOpenPlanRedDto() {
		return bussFinProductOpenPlanRedDto;
	}

	public void setBussFinProductOpenPlanRedDto(BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto) {
		this.bussFinProductOpenPlanRedDto = bussFinProductOpenPlanRedDto;
	}

	public List<BussFinProductFundAccountInfoDto> getBussFinProductFundAccountInfoDto() {
		return bussFinProductFundAccountInfoDto;
	}
	
	

	public String getProdManager() {
		return prodManager;
	}

	public void setProdManager(String prodManager) {
		this.prodManager = prodManager;
	}

	public void setBussFinProductFundAccountInfoDto(
			List<BussFinProductFundAccountInfoDto> bussFinProductFundAccountInfoDto) {
		this.bussFinProductFundAccountInfoDto = bussFinProductFundAccountInfoDto;
	}

	@Length(fieldName = "BussFinProductSaleParam.sameOrg" , max = 1000)
	public String getSameOrg() {
		return sameOrg;
	}

	public void setSameOrg(String sameOrg) {
		this.sameOrg = sameOrg;
	}

	public Date getActMdate() {
		return actMdate;
	}

	public void setActMdate(Date actMdate) {
		this.actMdate = actMdate;
	}
	//add by zhangweiqi 2019-3-14 净值型改成非必填
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getIsSus() {
		return isSus;
	}

	public void setIsSus(String isSus) {
		this.isSus = isSus;
	}

	public String getProdRegistCode() {
		return prodRegistCode;
	}

	public void setProdRegistCode(String prodRegistCode) {
		this.prodRegistCode = prodRegistCode;
	}

	public Integer getRedSpeed() {
		return redSpeed;
	}

	public void setRedSpeed(Integer redSpeed) {
		this.redSpeed = redSpeed;
	}
//	@NotEmpty(fieldName = "FinProductBuild002.targetCustomer1")
	public String getTargetCustomer1() {
		return targetCustomer1;
	}

	public void setTargetCustomer1(String targetCustomer1) {
		this.targetCustomer1 = targetCustomer1;
	}
	//add by gcl 2019/3/3 
//	@NotEmpty(fieldName = "FinProductBuild002.targetCustomer2")
	public String getTargetCustomer2() {
		return targetCustomer2;
	}

	public void setTargetCustomer2(String targetCustomer2) {
		this.targetCustomer2 = targetCustomer2;
	}

	//add by yejiejie 2019-3-8
	//产品期次
	//@NotEmpty(fieldName = "FinProductBuild002.repFinprodStage")
	private Integer repFinprodStage;

	public Integer getRepFinprodStage() {
		return repFinprodStage;
	}

	public void setRepFinprodStage(Integer repFinprodStage) {
		this.repFinprodStage = repFinprodStage;
	}
	//产品期次
    
	// add by tuqirong 2019-03-27 start
		//产品风险等级
		@NotEmpty(fieldName = "FinProductBuild002.riskLevel")
		private String riskLevel;
		public String getRiskLevel() {
			return riskLevel;
		}


		public void setRiskLevel(String riskLevel) {
			this.riskLevel = riskLevel;
		}
    //add by tuqirong 2019-03-27 end

        
        /**
         * 获取 bussFinProductLayeredInfoDto
         * @return bussFinProductLayeredInfoDto.
         */
        public List<BussFinProductLayeredInfoDto> getBussFinProductLayeredInfoDto() {
            return bussFinProductLayeredInfoDto;
        }

        
        /**
         * 设置 bussFinProductLayeredInfoDto
         * @param bussFinProductLayeredInfoDto bussFinProductLayeredInfoDto
         */
        public void setBussFinProductLayeredInfoDto(List<BussFinProductLayeredInfoDto> bussFinProductLayeredInfoDto) {
            this.bussFinProductLayeredInfoDto = bussFinProductLayeredInfoDto;
        }

        
        /**
         * 获取 bussBenchMarkLayeredInfoDto
         * @return bussBenchMarkLayeredInfoDto.
         */
        public BussBenchMarkLayeredInfoDto getBussBenchMarkLayeredInfoDto() {
            return bussBenchMarkLayeredInfoDto;
        }

        
        /**
         * 设置 bussBenchMarkLayeredInfoDto
         * @param bussBenchMarkLayeredInfoDto bussBenchMarkLayeredInfoDto
         */
        public void setBussBenchMarkLayeredInfoDto(BussBenchMarkLayeredInfoDto bussBenchMarkLayeredInfoDto) {
            this.bussBenchMarkLayeredInfoDto = bussBenchMarkLayeredInfoDto;
        }

    /** add by 发送接口状态   lizhi 2019-6-19 start**/
    // 发送状态
    private String sendStatus ;
    // 发送状态备注
    private String sendRemark ;

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendRemark() {
        return sendRemark;
    }

    public void setSendRemark(String sendRemark) {
        this.sendRemark = sendRemark;
    }
    /** add by 发送接口状态   lizhi 2019-6-19 end**/

    //ZGBUG-280产品端查询和新建页面有邮储银行的字段，希望可以删掉 guyunfei 20200310
    //@NotEmpty(fieldName = "FinProductBuild002.dataSource")
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public BussFinProductInvestPeriodDto getBussFinProductInvestPeriodDto() {
		return bussFinProductInvestPeriodDto;
	}

	public void setBussFinProductInvestPeriodDto(BussFinProductInvestPeriodDto bussFinProductInvestPeriodDto) {
		this.bussFinProductInvestPeriodDto = bussFinProductInvestPeriodDto;
	}

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}

	public String getRepFinprodInsId() {
		return repFinprodInsId;
	}

	public void setRepFinprodInsId(String repFinprodInsId) {
		this.repFinprodInsId = repFinprodInsId;
	}
      
	private List<FinProductAnapleroticInfoDto> finProductAnapleroticTabInfoDtoList;
	/**
	 *   是否回补
	 */
	private String isAnaplerotic;

	public List<FinProductAnapleroticInfoDto> getFinProductAnapleroticTabInfoDtoList() {
		return finProductAnapleroticTabInfoDtoList;
	}

	public void setFinProductAnapleroticTabInfoDtoList(
			List<FinProductAnapleroticInfoDto> finProductAnapleroticTabInfoDtoList) {
		this.finProductAnapleroticTabInfoDtoList = finProductAnapleroticTabInfoDtoList;
	}

	public String getIsAnaplerotic() {
		return isAnaplerotic;
	}

	public void setIsAnaplerotic(String isAnaplerotic) {
		this.isAnaplerotic = isAnaplerotic;
	}

	public String getPrdDesignId() {
		return prdDesignId;
	}

	public void setPrdDesignId(String prdDesignId) {
		this.prdDesignId = prdDesignId;
	}

	public String getPrdDesignName() {
		return prdDesignName;
	}

	public void setPrdDesignName(String prdDesignName) {
		this.prdDesignName = prdDesignName;
	}

	public String getIsFhxgcp() {
		return isFhxgcp;
	}

	public void setIsFhxgcp(String isFhxgcp) {
		this.isFhxgcp = isFhxgcp;
	}

	public String getIsYllccp() {
		return isYllccp;
	}

	public void setIsYllccp(String isYllccp) {
		this.isYllccp = isYllccp;
	}

    /**
     * add by jiangpeng on 20210401
     * 是否是产品编辑 TISHKBS-1952 用于处理产品编辑时覆盖 BOK_BOOKSET_CAPACCT_INFO(账套清算账户表)的问题
     */
    private boolean isProductEdit = false;

    public boolean isProductEdit() {
        return isProductEdit;
    }

    public void setProductEdit(boolean productEdit) {
        isProductEdit = productEdit;
    }
	
}
