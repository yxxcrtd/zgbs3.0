/*
 * 文件名：FinProductBuild002Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.product.common.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.service.product.productelement.scheduling.dto.BussRepFinProductInfoDto;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.PrdMdateAdjust;
import com.joyintech.tams.common.base.entity.PrdSaleParam;
import com.joyintech.tams.common.base.entity.PrdValParam;
import com.joyintech.tams.invest.common.valuationinterf.dto.BussFinProductFundAccountInfoDto;

/**
 * 产品新建管理信息dto
 * 
 * @author zhangliang
 * @version 1.0 2018年10月13日
 * @see FinProductCommonInfoDto
 * @since 1.0
 */
public class FinProductCommonInfoDto extends FinProduct{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -8543840737635969481L;
    
    /**
     * 画面Id
     */
    private String pageId;
    
    /**
     * 分支序号
     */
    private Long branch;

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
     * 募集方式
     */
    
    private String issueType;
    
    /********* add by zhangweiqi 主产品需求添加 begin**************/
    /**
     * 产品系列
     */
    private String prodSeries;
    
    
    
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
     * 分红方式
     */
    
    private String payType;
    
    /**
     * 实际到期日
     */
    private String actMdate;
    
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
    
    /********* add by zhangweiqi 主产品需求添加 end**************/
    /**
     * 周期型产品的期限品种
     */
    private String period;
    /**
     * 是否现金管理类
     */
    private String isCashManage;
    /**
     * 是否合规新产品
     */
    private String isFhxgcp;
    /**
     * 是否养老理财产品
     */
    private String isYllccp;
    /**
     * 行内标识码
     */
    private String repFinprodInsId;
    public String getIsCashManage() {
		return isCashManage;
	}


	public void setIsCashManage(String isCashManage) {
		this.isCashManage = isCashManage;
	}
	
	/**
     * 数据来源
     */
    private String dataSource;
    /**
     * 期限品种
     */
    private String termType;
    /**
     * 产品设计系列编号
     */
    private String prdDesignId;
    /**
     * 产品设计系列名称
     */
    private String prdDesignName;

    /**
     *
     * 说明书是否披露风险标志
     */
    private String directDisclosureRiskFlag;

    public String getDirectDisclosureRiskFlag() {
        return directDisclosureRiskFlag;
    }

    public void setDirectDisclosureRiskFlag(String directDisclosureRiskFlag) {
        this.directDisclosureRiskFlag = directDisclosureRiskFlag;
    }

    /**
     * add by wm 期限类别
     */
    private BussFinProductInvestPeriodDto bussFinProductInvestPeriodDto;

	public String getDataSource() {
		return dataSource;
	}


	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
     * {@linkplain}
     */
    private BussFinProductPayPlanDto bussFinProductPayPlanDto;
    
    /**
     * {@linkplain}
     */
    private BussFinProductOpenPlanDto bussFinProductOpenPlanDto;
    
    /**
     * add by zhangweiqi 
     */
  
    private BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto;
    
    /**
     * {@linkplain}
     */
    private BussFinProductBonusPlanDto bussFinProductBonusPlanDto;
    
    /**
     * {@linkplain}
     */
    private BussBenchMarkInfoDto bussBenchMarkInfoDto;
    
    /**
     * {@linkplain}
     */
    private FinProductFloatFeeDto bussFinProductFloatFeeDto;
    
    /**
     * {@linkplain}
     */
    private BussBenchMarkInfoDto bussBenchMarkFloatInfoDto;
    
    /**
     * {@linkplain}
     */
    private PrdSaleParam bussFinProductSaleParamDto;
    
    /**
     * {@linkplain}
     */
    private PrdValParam bussFinProductValuationDto;
    
    /**
     * {@linkplain} 
     */
    private BussFinProductInvestInfoDto bussFinProductInvestDto;
    
    /**
     * {@linkplain}
     */
    private FinProductGoalInfoDto bussFinProductGoalInfoDto;
    
    /**
     * {@linkplain}
     */
    private BussRepFinProductInfoDto bussRepFinProductInfoDto;
    
    /**
     * {@linkplain}
     */
    private BussFinProductFeeInfoDto bussFinProductFeeInfoDto;
    
    /**
     * {@linkplain}
     */
    private BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto;
    
    private List<BussFinProductFundAccountInfoDto> bussFinProductFundAccountInfoDto;
    
    private List<BussFinProductLayeredInfoDto> bussFinProductLayeredInfoDto;
    
    /**
     * 分层款型业绩基准
     */
    private BussBenchMarkLayeredInfoDto bussBenchMarkLayeredInfoDto;
    /**
     * add by hujinjin 到期日调整使用
     */
    private PrdMdateAdjust prdMdateAdjust;
    

	public PrdMdateAdjust getPrdMdateAdjust() {
		return prdMdateAdjust;
	}


	public void setPrdMdateAdjust(PrdMdateAdjust prdMdateAdjust) {
		this.prdMdateAdjust = prdMdateAdjust;
	}


	public String getPageId() {
        return pageId;
    }

    
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

    public String getDealMode() {
        return dealMode;
    }

    
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
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

    public String getProfitFlag() {
        return profitFlag;
    }

    
    public void setProfitFlag(String profitFlag) {
        this.profitFlag = profitFlag;
    }

    
    public BigDecimal getYield() {
        return yield;
    }

    
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public Date getFirstSaleVdate() {
        return firstSaleVdate;
    }

    
    public void setFirstSaleVdate(Date firstSaleVdate) {
        this.firstSaleVdate = firstSaleVdate;
    }

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

    public String getBasis() {
        return basis;
    }

    
    public void setBasis(String basis) {
        this.basis = basis;
    }

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

    public String getInvestNature() {
        return investNature;
    }

    
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }

    public String getSaleMode() {
        return saleMode;
    }
    
    public void setSaleMode(String saleMode) {
        this.saleMode = saleMode;
    }
    
    public BussBenchMarkInfoDto getBussBenchMarkInfoDto() {
        return bussBenchMarkInfoDto;
    }

    public void setBussBenchMarkInfoDto(BussBenchMarkInfoDto bussBenchMarkInfoDto) {
        this.bussBenchMarkInfoDto = bussBenchMarkInfoDto;
    }
    
    public FinProductFloatFeeDto getBussFinProductFloatFeeDto() {
        return bussFinProductFloatFeeDto;
    }

    public void setBussFinProductFloatFeeDto(FinProductFloatFeeDto bussFinProductFloatFeeDto) {
        this.bussFinProductFloatFeeDto = bussFinProductFloatFeeDto;
    }

    public PrdSaleParam getBussFinProductSaleParamDto() {
        return bussFinProductSaleParamDto;
    }
    
    public void setBussFinProductSaleParamDto(PrdSaleParam bussFinProductSaleParamDto) {
        this.bussFinProductSaleParamDto = bussFinProductSaleParamDto;
    }

    
    public BussBenchMarkInfoDto getBussBenchMarkFloatInfoDto() {
		return bussBenchMarkFloatInfoDto;
	}


	public void setBussBenchMarkFloatInfoDto(BussBenchMarkInfoDto bussBenchMarkFloatInfoDto) {
		this.bussBenchMarkFloatInfoDto = bussBenchMarkFloatInfoDto;
	}


	public PrdValParam getBussFinProductValuationDto() {
        return bussFinProductValuationDto;
    }

    public void setBussFinProductValuationDto(PrdValParam bussFinProductValuationDto) {
        this.bussFinProductValuationDto = bussFinProductValuationDto;
    }
    
    
    public BussFinProductInvestInfoDto getBussFinProductInvestDto() {
		return bussFinProductInvestDto;
	}


	public void setBussFinProductInvestDto(BussFinProductInvestInfoDto bussFinProductInvestDto) {
		this.bussFinProductInvestDto = bussFinProductInvestDto;
	}


	public FinProductGoalInfoDto getBussFinProductGoalInfoDto() {
        return bussFinProductGoalInfoDto;
    }
    
    public void setBussFinProductGoalInfoDto(FinProductGoalInfoDto bussFinProductGoalInfoDto) {
        this.bussFinProductGoalInfoDto = bussFinProductGoalInfoDto;
    }

    public BussRepFinProductInfoDto getBussRepFinProductInfoDto() {
        return bussRepFinProductInfoDto;
    }

    public void setBussRepFinProductInfoDto(BussRepFinProductInfoDto bussRepFinProductInfoDto) {
        this.bussRepFinProductInfoDto = bussRepFinProductInfoDto;
    }
    
    public BussFinProductBonusPlanDto getBussFinProductBonusPlanDto() {
        return bussFinProductBonusPlanDto;
    }
    
    public void setBussFinProductBonusPlanDto(BussFinProductBonusPlanDto bussFinProductBonusPlanDto) {
        this.bussFinProductBonusPlanDto = bussFinProductBonusPlanDto;
    }

    public BussFinProductOpenPlanDto getBussFinProductOpenPlanDto() {
        return bussFinProductOpenPlanDto;
    }

    public void setBussFinProductOpenPlanDto(BussFinProductOpenPlanDto bussFinProductOpenPlanDto) {
        this.bussFinProductOpenPlanDto = bussFinProductOpenPlanDto;
    }
    
    public BussFinProductPayPlanDto getBussFinProductPayPlanDto() {
        return bussFinProductPayPlanDto;
    }
    
    public void setBussFinProductPayPlanDto(BussFinProductPayPlanDto bussFinProductPayPlanDto) {
        this.bussFinProductPayPlanDto = bussFinProductPayPlanDto;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

	public BussFinProductFeeInfoDto getBussFinProductFeeInfoDto() {
		return bussFinProductFeeInfoDto;
	}

	public void setBussFinProductFeeInfoDto(BussFinProductFeeInfoDto bussFinProductFeeInfoDto) {
		this.bussFinProductFeeInfoDto = bussFinProductFeeInfoDto;
	}

	public BussFinProductRevalDocumentDto getBussFinProductRevalDocumentDto() {
		return bussFinProductRevalDocumentDto;
	}

	public void setBussFinProductRevalDocumentDto(BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto) {
		this.bussFinProductRevalDocumentDto = bussFinProductRevalDocumentDto;
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


	


	public String getTrusteeId() {
		return trusteeId;
	}


	public void setTrusteeId(String trusteeId) {
		this.trusteeId = trusteeId;
	}


	public String getExclusiveProd() {
		return exclusiveProd;
	}


	public void setExclusiveProd(String exclusiveProd) {
		this.exclusiveProd = exclusiveProd;
	}


	public String getProdManager() {
		return prodManager;
	}


	public void setProdManager(String prodManager) {
		this.prodManager = prodManager;
	}


	public String getSaleType() {
		return saleType;
	}


	public void setSaleType(String saleType) {
		this.saleType = saleType;
	}


	public String getConsignmentFlag() {
		return consignmentFlag;
	}


	public void setConsignmentFlag(String consignmentFlag) {
		this.consignmentFlag = consignmentFlag;
	}


	public String getBeforeEndFlag() {
		return beforeEndFlag;
	}


	public void setBeforeEndFlag(String beforeEndFlag) {
		this.beforeEndFlag = beforeEndFlag;
	}


	public String getRedFlag() {
		return redFlag;
	}


	public void setRedFlag(String redFlag) {
		this.redFlag = redFlag;
	}


	public String getDefaultRedFlag() {
		return defaultRedFlag;
	}


	public void setDefaultRedFlag(String defaultRedFlag) {
		this.defaultRedFlag = defaultRedFlag;
	}


	public String getBeforeEstablishFlag() {
		return beforeEstablishFlag;
	}


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


	public void setBussFinProductFundAccountInfoDto(
			List<BussFinProductFundAccountInfoDto> bussFinProductFundAccountInfoDto) {
		this.bussFinProductFundAccountInfoDto = bussFinProductFundAccountInfoDto;
	}


	public String getSameOrg() {
		return sameOrg;
	}


	public void setSameOrg(String sameOrg) {
		this.sameOrg = sameOrg;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}


	public String getActMdate() {
		return actMdate;
	}


	public void setActMdate(String actMdate) {
		this.actMdate = actMdate;
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


	public String getIssueType() {
		return issueType;
	}


	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}


	public List<BussFinProductLayeredInfoDto> getBussFinProductLayeredInfoDto() {
		return bussFinProductLayeredInfoDto;
	}


	public void setBussFinProductLayeredInfoDto(List<BussFinProductLayeredInfoDto> bussFinProductLayeredInfoDto) {
		this.bussFinProductLayeredInfoDto = bussFinProductLayeredInfoDto;
	}


	public Integer getRedSpeed() {
		return redSpeed;
	}


	public void setRedSpeed(Integer redSpeed) {
		this.redSpeed = redSpeed;
	}


	public String getTargetCustomer1() {
		return targetCustomer1;
	}


	public void setTargetCustomer1(String targetCustomer1) {
		this.targetCustomer1 = targetCustomer1;
	}


	public String getTargetCustomer2() {
		return targetCustomer2;
	}


	public void setTargetCustomer2(String targetCustomer2) {
		this.targetCustomer2 = targetCustomer2;
	}
	
	/**
     * 本次付息起始日  中间付息新建使用
     */
    private Date payintStartDate;


    
    /**
     * 获取 payintStartDate
     * @return payintStartDate.
     */
    public Date getPayintStartDate() {
        return payintStartDate;
    }


    
    /**
     * 设置 payintStartDate
     * @param payintStartDate payintStartDate
     */
    public void setPayintStartDate(Date payintStartDate) {
        this.payintStartDate = payintStartDate;
    }
    //add by zlp 2019/3/7
    /**
     * 发行状态
     */
    private String issueStatus;
    
	public String getIssueStatus() {
		return issueStatus;
	}


	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}

    
    //add by wangcheng 2019/3/7
    /**
     * 交易方向
     */
    private String ps;

	public String getPs() {
		return ps;
	}
	
	public void setPs(String ps) {
		this.ps = ps;
	}
	//add by yejiejie 2019-3-8
	//产品期次
	private Integer repFinprodStage;
	//产品期次（快录）
	private Integer repFinprodStageC;
	//托管机构（快录）
	private String truOrg;
	
   public String getTruOrg() {
		return truOrg;
	}


	public void setTruOrg(String truOrg) {
		this.truOrg = truOrg;
	}


public Integer getRepFinprodStageC() {
		return repFinprodStageC;
	}


	public void setRepFinprodStageC(Integer repFinprodStageC) {
		this.repFinprodStageC = repFinprodStageC;
	}


	public Integer getRepFinprodStage() {
		return repFinprodStage;
	}

	public void setRepFinprodStage(Integer repFinprodStage) {
		this.repFinprodStage = repFinprodStage;
	}
	//产品期次
	
	//add by tuqirong 2019-03-26 start
	//产品风险等级
	private String riskLevel;


	public String getRiskLevel() {
		return riskLevel;
	}


	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	//add by tuqirong 2019-03-26 end 
	//add by gcl 2019-03-296 start
	//到期客户赎回金额
	private String mCusRevAmt;


	public String getmCusRevAmt() {
		return mCusRevAmt;
	}


	public void setmCusRevAmt(String mCusRevAmt) {
		this.mCusRevAmt = mCusRevAmt;
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


	public String getTermType() {
		return termType;
	}


	public void setTermType(String termType) {
		this.termType = termType;
	}


	public BussFinProductInvestPeriodDto getBussFinProductInvestPeriodDto() {
		return bussFinProductInvestPeriodDto;
	}


	public void setBussFinProductInvestPeriodDto(BussFinProductInvestPeriodDto bussFinProductInvestPeriodDto) {
		this.bussFinProductInvestPeriodDto = bussFinProductInvestPeriodDto;
	}


	public String getPeriod() {
		return period;
	}


	public void setPeriod(String period) {
		this.period = period;
	}


	public String getRepFinprodInsId() {
		return repFinprodInsId;
	}


	public void setRepFinprodInsId(String repFinprodInsId) {
		this.repFinprodInsId = repFinprodInsId;
	}
	
	/**
     * 回补信息集合
     */
    private List<FinProductAnapleroticInfoDto> finProductAnapleroticTabInfoDtoList;
    
    /**
     * 是否回补
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
