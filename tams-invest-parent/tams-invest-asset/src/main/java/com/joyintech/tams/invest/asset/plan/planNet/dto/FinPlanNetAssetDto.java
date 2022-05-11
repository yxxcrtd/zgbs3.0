/*
 * 文件名：FinPlanAssetNetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.planNet.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Amount;
import com.joyintech.framework.validation.annotation.Code;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.BussFinRecommentDto;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.report.asset.plan.dto.BussRepAssetMngProdInfo;
import com.joyintech.tams.component.business.report.asset.trust.dto.BussRepOutInvestAgrInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.asset.plan.planNet.validator.FinPlanNetAssetValidator;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/*
 * 文件名：FinPlanAssetNet001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author liuwei
 * @version 1.0 2018年9月10日
 * @see FinPlanNetAssetDto
 * @since 1.0
 */
@PageId("FinPlanNetAsset002")
@ValidLogic(targetClass = FinPlanNetAssetValidator.class,groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class FinPlanNetAssetDto extends FinProduct implements ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 398124932926546323L;
    /**
     * 标的类金融产品entity
     */
    private FinProduct finProduct;
    /**
     * 金融产品附属表entity
     */
    private FinProductAdd finProductAdd;
    //增加投后信息，标的类金融产品扩展表by赵东东2019.9.29start
    /**
     * 投后信息表
     */
    private FinPostInvestInfo finPostInvestInfo;
    /**
     * 标的类金融产品扩展表
     */
    private PaFinProduct paFinProduct;
    //增加投后信息，标的类金融产品扩展表by赵东东2019.9.29start
    /**
     * 金融产品分类表entity
     */
    private FinProductType finProductType;
    /**
     * 金融产品现金流信息表dtolist
     */
    private List<FinCashInfo> finCashInfoList;
    /**
     *
     * 现金流利率信息表dtolist
     */
    private List<FinRateInfo> finRateInfoList;
    /**
     * 现金流计算规则dtolist
     */
    private List<FinCashCalcRule> finCashCalcRuleList;
    /**
     * 组合entity
     */
    private PtlPortfolio ptlPortfolio;
    /**
     * 资产管理计划报送信息输入组件
     */
    @Valid
    private BussRepAssetMngProdInfo bussRepAssetMngProdInfo;
    /**
     * 委外投资—协议方式中债报送信息输入组件
     */
    @Valid
    private BussRepOutInvestAgrInfo bussRepOutInvestAgrInfo;
    /**
     * 参与方信息
     */
    @Valid
    private BussParticipantInfoDto bussParticipantInfoDto;
    /**
     * 资产负债报送信息entity
     */
    private RepAssetDept repAssetDept;
    /**
     * 资产管理计划报送信息entity
     */
    private RepAssetMngPlan repAssetMngPlan;
    /**
     * 委外投资—协议方式报送信息entity
     */
    private RepTrustInvestMode repTrustInvestMode;
    // 增加中债资产其它要素entity by赵东东 2019.10.10 start
    /**
     * 中债资产其它要素entity
     */
    private RepAssetOther repAssetOther;
    /**
     * 获取 repAssetOther
     * @return repAssetOther.
     */
    public RepAssetOther getRepAssetOther() {
		return repAssetOther;
	}
    /**
     * 设置 repAssetOther
     * @param 
     */
	public void setRepAssetOther(RepAssetOther repAssetOther) {
		this.repAssetOther = repAssetOther;
	}
	// 增加中债资产其它要素entity by赵东东 2019.10.10 end
	/**
     * 文件上传输入参数
     */
    private MstFileInputParameter mstFileInputParameter;
    /**
     * 附件信息DTO
     */
    private List<FileCommonDto> fileCommonList;
    /**
     * 操作类型
     */
    private String operateType;    
    /**
     * 界面类型
     */
    private String pageModel; 
    /**
     * 选中的数据列表
     */
    private transient List<FinPlanNetAssetDto> selectedItem;
    /**
     * 画面元素
     */
    private String elementId;
    /**
     * 起息日开始日期
     */
    private String startVDate;
    /**
     * 起息日结束日期
     */
    private String endVDate;
    /**
     * 到息日开始日期
     */
    private String startMDate;
    /**
     * 到息日结束日期
     */
    private String endMDate;
    /**
     * 一级分类
     */
    private String category;
    /**
     * 二级分类
     */
    private String type;
    //行内五级分类  zhangpuyu 2019-7-23 start
    /**
     * 行内二级分类
     */
    private String type9;
    /**
     * 行内三级分类
     */
    private String type10;
    /**
     * 行内四级分类
     */
    private String type11;
    /**
     * 行内五级分类
     */
    private String type12;

    /**
     * 申购确认速度，如果资产只有一个确认速度，则存该字段
     */
    private String purSpeed;

    /**
     * 赎回确认速度
     */
    private String redSpeed;

    /**
     * 获取 type9
     * @return type9.
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.type9",groups={ValidateGroupEdit.class})
    public String getType9() {
		return type9;
	}

    /**
     * 设置 type9
     * @param 
     */
	public void setType9(String type9) {
		this.type9 = type9;
	}
	/**
     * 获取 type10
     * @return type10.
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.type10",groups={ValidateGroupEdit.class})
	public String getType10() {
		return type10;
	}

	    /**
	     * 设置 type10
	     * @param 
	     */
	public void setType10(String type10) {
		this.type10 = type10;
	}
	
	/**
     * 获取 type11
     * @return type11.
     */
//    @NotEmpty(fieldName = "FinPlanNetAsset002.type11",groups={ValidateGroupEdit.class})
	public String getType11() {
		return type11;
	}

    /**
     * 设置 type11
     * @param 
     */
	public void setType11(String type11) {
		this.type11 = type11;
	}

	/**
     * 获取 type12
     * @return type12.
     */
//    @NotEmpty(fieldName = "FinPlanNetAsset002.type12",groups={ValidateGroupEdit.class})
	public String getType12() {
		return type12;
	}

    /**
     * 设置 type12
     * @param 
     */
	public void setType12(String type12) {
		this.type12 = type12;
	}
	//行内五级分类  zhangpuyu 2019-7-23 end
	/**
     * 交易方式
     */
    private String transMode;
    /**
     * 出账编号
     *//*
    private String cmsId;
    *//**
     * BECIF号
     *//*
    private String cmsBecif;
    *//**
     * 关联合同编号
     *//*
    private String cmsContractId;*/
    /**
     * 发行人名称
     */
    private String issuerName;
    /**
     * 托管人名称
     */
    private String trusteeIdName;
    /**
     * 附件临时Id
     */
    private String fjId;
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
     * 投资性质_固定收益类
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.investmentAttributes",groups={ValidateGroupEdit.class})
    private String investmentAttributes;
    
    // 重写合同号 by 赵东东 2019.10.15 start
    /**
     * 合同号
     */
    private String contractNo;
    /**
     * 获取合同号
     * @return contractNo.
     */
   // @NotEmpty(fieldName = "FinPlanNetAsset002.contractNo",groups={ValidateGroupEdit.class})
    public String getContractNo() {
		return contractNo;
	}
    /**
    * 设置 contractNo
    * @param contractNo contractNo
    */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	// 重写合同号 by 赵东东 2019.10.15 end
	//增加预警线,止损线，法审编号，业绩基准，超额分成方式by赵东东 2019.9.27 start
    /**
     * 预警线
     */
    private String warnLine;
    /**
     * 止损线
     */
    private String flatLine;
    /**
     * 法审编号
     */
    private String trialNo;
    /**
     * 业绩基准
     */
    private String baseRule;
    /**
     * 超额分成方式
     */
    private String overallocation;

    /**
     * 资产/负债类别
     */
    private String repAssetType;

    /**
     * 是否每日开放申赎
     */
    private String isOpenEveryDay;

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
     * 增信措施
     */
    private String creditEnhanceMeasure;

    /**
     * 基金经理姓名
     */
    private String fundMangerName;

    /**
     * 获取止损线
     * @return flatLine.
     */
   //@NotEmpty(fieldName = "FinPlanNetAsset002.flatLine",groups={ValidateGroupEdit.class})
   public String getFlatLine() {
		return flatLine;
	}
   /**
    * 设置止损线
    * @return flatLine.
    */
	public void setFlatLine(String flatLine) {
		this.flatLine = flatLine;
	}
	// 法审编号选填 by赵东东 2019.10.15 start
	/**
     * 获取法审编号
     * @return trialNo.
     */
	//@NotEmpty(fieldName = "FinPlanNetAsset002.trialNo",groups={ValidateGroupEdit.class})
	public String getTrialNo() {
		return trialNo;
	}
	// 法审编号选填 by赵东东 2019.10.15 end
	/**
     * 设置法审编号
     * @return trialNo.
     */
	public void setTrialNo(String trialNo) {
		this.trialNo = trialNo;
	}
	/**
     * 获取业绩基准
     * @return baseRule.
     */
	//@NotEmpty(fieldName = "FinPlanNetAsset002.baseRule",groups={ValidateGroupEdit.class})
	public String getBaseRule() {
		return baseRule;
	}
	/**
     * 设置业绩基准
     * @return baseRule.
     */
	public void setBaseRule(String baseRule) {
		this.baseRule = baseRule;
	}
	/**
     * 获取超额分成方式
     * @return overallocation.
     */
	//@NotEmpty(fieldName = "FinPlanNetAsset002.overallocation",groups={ValidateGroupEdit.class})
	public String getOverallocation() {
		return overallocation;
	}
	/**
     * 设置超额分成方式
     * @return overallocation.
     */
	public void setOverallocation(String overallocation) {
		this.overallocation = overallocation;
	}
	/**
     * 获取预警线
     * @return warnLine.
     */
    //@NotEmpty(fieldName = "FinPlanNetAsset002.warnLine",groups={ValidateGroupEdit.class})
	public String getWarnLine() {
		return warnLine;
	}
	/**
     * 设置预警线
     * @return 
     */
	public void setWarnLine(String warnLine) {
		this.warnLine = warnLine;
	}
	//增加预警线,止损线，法审编号，业绩基准，超额分成方式by赵东东 2019.9.27 end
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
     * 取得 市场代码，可以按规则生成，或者手输，不校验唯一。对于理财产品分层，同母金融产品代码对应的市场代码。
     * @return 市场代码，可以按规则生成，或者手输，不校验唯一。对于理财产品分层，同母金融产品代码对应的市场代码。
     */
    @Override
    //YUCHUBS-564 资产代码的格式去掉校验 guyunfei 2019/09/04 start
    //@Pattern(fieldName = "FinPlanNetAsset002.finprodMarketId",regx = "^(?=.*[a-zA-Z]+)(?=.*[0-9]+)[a-zA-Z0-9]+$",message = "FW_E_0002",groups={ValidateGroupEdit.class})
    //YUCHUBS-564 资产代码的格式去掉校验 guyunfei 2019/09/04 end
//    @NotEmpty(fieldName = "FinPlanNetAsset002.finprodMarketId", groups = {ValidateGroupEdit.class})
    @Code(fieldName = "FinPlanNetAsset002.finprodMarketId", length = 32, groups = {ValidateGroupEdit.class})
    @PrimaryKey(Field.PK_1)
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }
    /**
     * 取得 本金收益特征，保本、非保本
     * @return 本金收益特征，保本、非保本
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.capiIncomeFeature",groups={ValidateGroupEdit.class})
    public String getCapiIncomeFeature() {
        return super.getCapiIncomeFeature();
    }

    /**
     * 取得 起息日
     * @return 起息日
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.vDate",groups={ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * 取得 到期日
     * @return 到期日
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.mDate",groups={ValidateGroupEdit.class})
    public Date getMdate() {
        return super.getMdate();
    }

    /**
     * 取得 交易日历
     * @return 交易日历
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.calendarId",groups={ValidateGroupEdit.class})
    public String getCalendarId() {
        return super.getCalendarId();
    }

    /**
     * 取得 币种（本金币种、募集币种）
     * @return 币种（本金币种、募集币种）
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.ccy",groups={ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 取得 金融产品全称
     * @return 金融产品全称
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.finprodName",groups={ValidateGroupEdit.class})
    public String getFinprodName() {
        return super.getFinprodName();
    }

    /**
     * 取得 托管人
     * @return 托管人
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.trusteeId",groups={ValidateGroupEdit.class})
    public String getTrusteeId() {
        return super.getTrusteeId();
    }

    /**
     * 取得 发行人，存债券发行人、基金管理人、通道管理人等
     * @return 发行人，存债券发行人、基金管理人、通道管理人等
     */
    @NotEmpty(fieldName = "FinPlanNetAsset002.manager",groups={ValidateGroupEdit.class})
    public String getManager() {
        return super.getManager();
    }

    /**
     * 取得 发行规模
     * @return 发行规模
     */
    //@NotEmpty(fieldName = "FinPlanNetAsset002.issueAmt",groups={ValidateGroupEdit.class})
    @Amount(fieldName = "FinPlanNetAsset002.issueAmt", maxIntBit = 12, maxDecBit = 2,groups={ValidateGroupEdit.class})
    public BigDecimal getIssueAmt() {
        return super.getIssueAmt();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    @Override
    @PrimaryKey(Field.PK_2)
    public String getFinprodType2() {
        return super.getFinprodType2();
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
    public List<FinPlanNetAssetDto> getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinPlanNetAssetDto> selectedItem) {
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
     * 获取 startVDate
     * @return startVDate.
     */
    public String getStartVDate() {
        return startVDate;
    }
    
    /**
     * 设置 startVDate
     * @param startVDate startVDate
     */
    public void setStartVDate(String startVDate) {
        this.startVDate = startVDate;
    }
    
    /**
     * 获取 endVDate
     * @return endVDate.
     */
    public String getEndVDate() {
        return endVDate;
    }
    
    /**
     * 设置 endVDate
     * @param endVDate endVDate
     */
    public void setEndVDate(String endVDate) {
        this.endVDate = endVDate;
    }
    
    /**
     * 获取 startMDate
     * @return startMDate.
     */
    public String getStartMDate() {
        return startMDate;
    }
    
    /**
     * 设置 startMDate
     * @param startMDate startMDate
     */
    public void setStartMDate(String startMDate) {
        this.startMDate = startMDate;
    }
    
    /**
     * 获取 endMDate
     * @return endMDate.
     */
    public String getEndMDate() {
        return endMDate;
    }
    
    /**
     * 设置 endMDate
     * @param endMDate endMDate
     */
    public void setEndMDate(String endMDate) {
        this.endMDate = endMDate;
    }
    
    /**
     * 获取 category
     * @return category.
     */
    //@NotEmpty(fieldName = "FinPlanNetAsset002.category",groups={ValidateGroupEdit.class})
    public String getCategory() {
        return category;
    }
    
    /**
     * 设置 category
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * 获取 type
     * @return type.
     */
   //@NotEmpty(fieldName = "FinPlanNetAsset002.type",groups={ValidateGroupEdit.class})
    public String getType() {
        return type;
    }
    
    /**
     * 设置 type
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * 获取 transMode
     * @return transMode.
     */
   /* @NotEmpty(fieldName = "FinPlanNetAsset002.transMode",groups={ValidateGroupEdit.class})*/
    public String getTransMode() {
        return transMode;
    }
    
    /**
     * 设置 transMode
     * @param transMode transMode
     */
    public void setTransMode(String transMode) {
        this.transMode = transMode;
    }
    
    /**
     * 获取 cmsBECIF
     * @return cmsBECIF.
     *//*
    public String getCmsBecif() {
        return cmsBecif;
    }
    
    *//**
     * 设置 cmsBECIF
     * @param cmsBecif cmsBECIF
     *//*
    public void setCmsBecif(String cmsBecif) {
        this.cmsBecif = cmsBecif;
    }
    
    *//**
     * 获取 cmsContractId
     * @return cmsContractId.
     *//*
    public String getCmsContractId() {
        return cmsContractId;
    }
    
    *//**
     * 设置 cmsContractId
     * @param cmsContractId cmsContractId
     *//*
    public void setCmsContractId(String cmsContractId) {
        this.cmsContractId = cmsContractId;
    }
    */
    /**
     * 将一级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getCategoryAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.计划类资产一级分类, this.getCategory());
    }
    
    /**
     * 将二级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getTypeAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.计划类资产二级分类, this.getType());
    }
    //行内五级分类 2019-7-23 zhangpuyu star
    /**
     * 将行内五级分类对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getType9Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.getType9());
    }
    
    public String getType10Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.getType10());
    }
    
    public String getType11Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getType11());
    }
    
    public String getType12Abbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产五级分类, this.getType12());
    }
  //行内五级分类 2019-7-23 zhangpuyu end
    /**
     * 将交易方式对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getTransModeAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getTradeMarket());
    }
    
    /**
     * 将币种对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getCcyAbbr(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    /**
     * 获取 finProduct
     *
     * @return finProduct
     */
    public FinProduct getFinProduct() {
        return finProduct;
    }

    /**
     * 设置 finProduct
     *
     * @param finProduct finProduct
     */
    public void setFinProduct(FinProduct finProduct) {
        this.finProduct = finProduct;
    }

    /**
     * 获取 finProductAdd
     *
     * @return finProductAdd
     */
    public FinProductAdd getFinProductAdd() {
        return finProductAdd;
    }

    /**
     * 设置 finProductAdd
     *
     * @param finProductAdd finProductAdd
     */
    public void setFinProductAdd(FinProductAdd finProductAdd) {
        this.finProductAdd = finProductAdd;
    }
    
    //增加投后信息表by赵东东2019.9.29 start
    /**
     * 获取 finPostInvestInfo
     *
     * @return finPostInvestInfo
     */
    public FinPostInvestInfo getFinPostInvestInfo() {
		return finPostInvestInfo;
	}
    /**
     * 设置 finPostInvestInfo
     *
     * @param finPostInvestInfo finPostInvestInfo
     */
	public void setFinPostInvestInfo(FinPostInvestInfo finPostInvestInfo) {
		this.finPostInvestInfo = finPostInvestInfo;
	}
	//增加投后信息表by赵东东2019.9.29 end
	//增加标的类金融产品扩展表by赵东东2019.9.29 start
	/**
     * 获取 paFinProduct
     *
     * @return paFinProduct
     */
    public PaFinProduct getPaFinProduct() {
		return paFinProduct;
	}
    /**
     * 设置paFinProduct
     *
     * @return 
     */
	public void setPaFinProduct(PaFinProduct paFinProduct) {
		this.paFinProduct = paFinProduct;
	}
	//增加标的类金融产品扩展表by赵东东2019.9.29 end
    /**
     * 获取 finProductType
     *
     * @return finProductType
     */
    public FinProductType getFinProductType() {
        return finProductType;
    }

    /**
     * 设置 finProductType
     *
     * @param finProductType finProductType
     */
    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }

    /**
     * 获取 finCashInfoList
     *
     * @return finCashInfoList
     */
    public List<FinCashInfo> getFinCashInfoList() {
        return finCashInfoList;
    }

    /**
     * 设置 finCashInfoList
     *
     * @param finCashInfoList finCashInfoList
     */
    public void setFinCashInfoList(List<FinCashInfo> finCashInfoList) {
        this.finCashInfoList = finCashInfoList;
    }

    /**
     * 获取 finRateInfoList
     *
     * @return finRateInfoList
     */
    public List<FinRateInfo> getFinRateInfoList() {
        return finRateInfoList;
    }

    /**
     * 设置 finRateInfoList
     *
     * @param finRateInfoList finRateInfoList
     */
    public void setFinRateInfoList(List<FinRateInfo> finRateInfoList) {
        this.finRateInfoList = finRateInfoList;
    }

    /**
     * 获取 finCashCalcRuleList
     *
     * @return finCashCalcRuleList
     */
    public List<FinCashCalcRule> getFinCashCalcRuleList() {
        return finCashCalcRuleList;
    }

    /**
     * 设置 finCashCalcRuleList
     *
     * @param finCashCalcRuleList finCashCalcRuleList
     */
    public void setFinCashCalcRuleList(List<FinCashCalcRule> finCashCalcRuleList) {
        this.finCashCalcRuleList = finCashCalcRuleList;
    }

    /**
     * 获取 bussRepAssetMngProdInfo
     *
     * @return bussRepAssetMngProdInfo
     */
    public BussRepAssetMngProdInfo getBussRepAssetMngProdInfo() {
        return bussRepAssetMngProdInfo;
    }

    /**
     * 设置 bussRepAssetMngProdInfo
     *
     * @param bussRepAssetMngProdInfo bussRepAssetMngProdInfo
     */
    public void setBussRepAssetMngProdInfo(BussRepAssetMngProdInfo bussRepAssetMngProdInfo) {
        this.bussRepAssetMngProdInfo = bussRepAssetMngProdInfo;
    }

    /**
     * 获取 bussRepOutInvestAgrInfo
     *
     * @return bussRepOutInvestAgrInfo
     */
    public BussRepOutInvestAgrInfo getBussRepOutInvestAgrInfo() {
        return bussRepOutInvestAgrInfo;
    }

    /**
     * 设置 bussRepOutInvestAgrInfo
     *
     * @param bussRepOutInvestAgrInfo bussRepOutInvestAgrInfo
     */
    public void setBussRepOutInvestAgrInfo(BussRepOutInvestAgrInfo bussRepOutInvestAgrInfo) {
        this.bussRepOutInvestAgrInfo = bussRepOutInvestAgrInfo;
    }

    /**
     * 获取 repAssetDept
     *
     * @return repAssetDept
     */
    public RepAssetDept getRepAssetDept() {
        return repAssetDept;
    }

    /**
     * 设置 repAssetDept
     *
     * @param repAssetDept repAssetDept
     */
    public void setRepAssetDept(RepAssetDept repAssetDept) {
        this.repAssetDept = repAssetDept;
    }

    /**
     * 获取 repAssetMngPlan
     *
     * @return repAssetMngPlan
     */
    public RepAssetMngPlan getRepAssetMngPlan() {
        return repAssetMngPlan;
    }

    /**
     * 设置 repAssetMngPlan
     *
     * @param repAssetMngPlan repAssetMngPlan
     */
    public void setRepAssetMngPlan(RepAssetMngPlan repAssetMngPlan) {
        this.repAssetMngPlan = repAssetMngPlan;
    }

    /**
     * 获取 repTrustInvestMode
     *
     * @return repTrustInvestMode
     */
    public RepTrustInvestMode getRepTrustInvestMode() {
        return repTrustInvestMode;
    }

    /**
     * 设置 repTrustInvestMode
     *
     * @param repTrustInvestMode repTrustInvestMode
     */
    public void setRepTrustInvestMode(RepTrustInvestMode repTrustInvestMode) {
        this.repTrustInvestMode = repTrustInvestMode;
    }

    /**
     * 获取 ptlPortfolio
     *
     * @return ptlPortfolio
     */
    public PtlPortfolio getPtlPortfolio() {
        return ptlPortfolio;
    }

    /**
     * 设置 ptlPortfolio
     *
     * @param ptlPortfolio ptlPortfolio
     */
    public void setPtlPortfolio(PtlPortfolio ptlPortfolio) {
        this.ptlPortfolio = ptlPortfolio;
    }

    /**
     * 获取 mstFileInputParameter
     *
     * @return mstFileInputParameter
     */
    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    /**
     * 设置 mstFileInputParameter
     *
     * @param mstFileInputParameter mstFileInputParameter
     */
    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }

    /**
     * 获取 fileCommonList
     *
     * @return fileCommonList
     */
    public List<FileCommonDto> getFileCommonList() {
        return fileCommonList;
    }

    /**
     * 设置 fileCommonList
     *
     * @param fileCommonList fileCommonList
     */
    public void setFileCommonList(List<FileCommonDto> fileCommonList) {
        this.fileCommonList = fileCommonList;
    }

    /**
     * 获取 cmsId
     *
     * @return cmsId
     */
    //@NotEmpty(fieldName = "FinPlanNetAsset002.cmsId",groups={ValidateGroupEdit.class})
//    @Pattern(fieldName = "FinPlanNetAsset002.cmsId",regx = "^(?=.*[a-zA-Z]+)(?=.*[0-9]+)[a-zA-Z0-9]+$",message = "{0}格式应为字母加数字")
    /*public String getCmsId() {
        return cmsId;
    }*/

    /**
     * 设置 cmsId
     *
     * @param cmsId cmsId
     *//*
    public void setCmsId(String cmsId) {
        this.cmsId = cmsId;
    }
*/
    /**
     * 获取 issuerName
     *
     * @return issuerName
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * 设置 issuerName
     *
     * @param issuerName issuerName
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * 获取 trusteeIdName
     *
     * @return trusteeIdName
     */
    public String getTrusteeIdName() {
        return trusteeIdName;
    }

    /**
     * 设置 trusteeIdName
     *
     * @param trusteeIdName trusteeIdName
     */
    public void setTrusteeIdName(String trusteeIdName) {
        this.trusteeIdName = trusteeIdName;
    }

    /**
     * 获取 fjId
     *
     * @return fjId
     */
    public String getFjId() {
        return fjId;
    }

    /**
     * 设置 fjId
     *
     * @param fjId fjId
     */
    public void setFjId(String fjId) {
        this.fjId = fjId;
    }

    /**
     * 获取 bussParticipantInfoDto
     *
     * @return bussParticipantInfoDto
     */
    public BussParticipantInfoDto getBussParticipantInfoDto() {
        return bussParticipantInfoDto;
    }

    /**
     * 设置 bussParticipantInfoDto
     *
     * @param bussParticipantInfoDto bussParticipantInfoDto
     */
    public void setBussParticipantInfoDto(BussParticipantInfoDto bussParticipantInfoDto) {
        this.bussParticipantInfoDto = bussParticipantInfoDto;
    }
    public String getVdateStr(){
        return DateUtil.format(this.getVdate());
    }
    
    public String getMdateStr(){
        return DateUtil.format(this.getMdate());
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
     * 获取 investmentAttributes
     * @return investmentAttributes.
     */
    public String getInvestmentAttributes() {
        return investmentAttributes;
    }

    /**
     * 设置 investmentAttributes
     * @param investmentAttributes investmentAttributes
     */
    public void setInvestmentAttributes(String investmentAttributes) {
        this.investmentAttributes = investmentAttributes;
    }
    /**
     * 实际管理人
     */
    private String actualManager;

    public String getActualManager() {
        return actualManager;
    }

    public void setActualManager(String actualManager) {
        this.actualManager = actualManager;
    }

    @NotEmpty(fieldName = "FinPlanNetAsset002.purSpeed",groups={ValidateGroupEdit.class})
    public String getPurSpeed() {
        return purSpeed;
    }

    public void setPurSpeed(String purSpeed) {
        this.purSpeed = purSpeed;
    }

    @NotEmpty(fieldName = "FinPlanNetAsset002.redSpeed",groups={ValidateGroupEdit.class})
    public String getRedSpeed() {
        return redSpeed;
    }

    public void setRedSpeed(String redSpeed) {
        this.redSpeed = redSpeed;
    }
    // 增加推荐方与推荐费率 add by renping 20191014 start
    /**
     * 推荐方
     */
    private String recommender;

    /**
     * 取得 推荐方
     * @return 推荐方
     */
    public String getRecommender() {
        return recommender;
    }

    /**
     * 设置 推荐方
     * @param recommender 推荐方
     */
    public void setRecommender(String recommender) {
        this.recommender = recommender == null ? null : recommender.trim();
    }
    /**
     * 资产推荐方名称
     */
    private String recommenderName;

    /**
     * 取得 资产推荐方名称
     * @return 资产推荐方名称
     */
    public String getRecommenderName() {
        return recommenderName;
    }

    /**
     * 设置 资产推荐方名称
     * @param recommenderName 资产推荐方名称
     */
    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommender == null ? null : recommenderName.trim();
    }

    /**
     * 资产推荐方及费率信息
     */
    @Valid
    private BussFinRecommentDto bussFinRecommentDto;

    /**
     * 获取 bussFinRecommentDto
     * 
     * @return bussFinRecommentDto.
     */
    public BussFinRecommentDto getBussFinRecommentDto() {
        return bussFinRecommentDto;
    }

    /**
     * 设置 bussFinRecommentDto
     * 
     * @param bussFinRecommentDto bussFinRecommentDto
     */
    public void setBussFinRecommentDto(BussFinRecommentDto bussFinRecommentDto) {
        this.bussFinRecommentDto = bussFinRecommentDto;
    }
    // 增加推荐方与推荐费率 add by renping 20191014 end


   /* @NotEmpty(fieldName = "BussRepEquityInfo.repAssetType", groups = {ValidateGroupEdit.class})*/
    public String getRepAssetType() {
        return repAssetType;
    }

    public void setRepAssetType(String repAssetType) {
        this.repAssetType = repAssetType;
    }

    /**
     * 取得 增信措施
     * @return 增信措施
     */
    public String getCreditEnhanceMeasure() {
        return creditEnhanceMeasure;
    }

    /**
     * 设置 增信措施
     * @param creditEnhanceMeasure 增信措施
     */
    public void setCreditEnhanceMeasure(String creditEnhanceMeasure) {
        this.creditEnhanceMeasure = creditEnhanceMeasure;
    }

    /**
     * 取得 基金经理姓名
     * @return 基金经理姓名
     */
    public String getFundMangerName() {
        return fundMangerName;
    }

    /**
     * 设置 基金经理姓名
     * @param fundMangerName 基金经理姓名
     */
    public void setFundMangerName(String fundMangerName) {
        this.fundMangerName = fundMangerName;
    }

    @NotEmpty(fieldName = "FinPlanNetAsset002.isOpenEveryDay",groups={ValidateGroupEdit.class})
    public String getIsOpenEveryDay() {
        return isOpenEveryDay;
    }

    public void setIsOpenEveryDay(String isOpenEveryDay) {
        this.isOpenEveryDay = isOpenEveryDay;
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

    public BussFinProductOpenPlanDto getBussFinProductOpenPlanRedDto() {
        return bussFinProductOpenPlanRedDto;
    }

    public void setBussFinProductOpenPlanRedDto(BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto) {
        this.bussFinProductOpenPlanRedDto = bussFinProductOpenPlanRedDto;
    }
}