/*
 * 文件名：EtrIbBondinfo002Dto.java
*/
 
package com.joyintech.tams.invest.entrust.etribbondinfo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Amount;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.Rate;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.trade.dto.BussEtrInvestPersonInfoDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.validator.EtrIbBondinfo002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

/**
 * 债券投资委托dto
 */
@PageId("EtrIbBondinfo002")
@ValidLogic(targetClass = EtrIbBondinfo002Validator.class , groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class EtrIbBondinfo002Dto extends EtrEntrust implements ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -8503717126985211652L;
    
    /**
          * 全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;
    
    /**
     * 资产起息日
     */
    private Date finvdate;
    /**
     * 资产到期日
     */
    private Date finmdate;
    
    /**
     * 计算方式
     */
    private String calculateField;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 组合名称
     */
    private String portfolioName;
    
    /**
     * 金融产品全称
     */
    private String finprodName;

    /**
     * 按钮id
     */
    private String elementId;
    
    /**
     * 行权收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exerYield;
    
    /**
     * 到期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal matureYield;

    /**
     * 清算速度
     */
    private long settleSpeed;

    /**
     * 清算速度
     */
    private String settleSpreed;
    
    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 通道名称
     */
    private String chlIdName;
    
    /**
     * 资产可用持仓
     */
    private BigDecimal assetAvailablePosition;
    
    /**
     * 流程状态
     */
    private String recStatus;
    
    /**
     * 交割日期
     */
    private Date settleDate;
    
    /**
     * 计算标志
     */
    private String calflag;
    
    /**
     * 单位利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitInt;
    
    /**
     * 利息总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 单位全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;
    
    /**
     * 多指令导入
     */
    private boolean importFlow;
    
    /**
     * 资产二级分类（原资产一级分类，委托页面的债券一级分类，修改：王贵奇，2019/9/27）
     */
    private String assetType1;

    /**
     * 委托关闭方式
     */
    private String CloseWay;
    
    /**
     * 证券托管户
    */
    private String secAcctId;
    
    /**
     * 画面
     */
    private String pageModel;
    
	/**
	 * 记录操作类型
	 */
	private OperationType operType;

    /**
     * 交易对手类型
     */
    private String counterType;


    /**
     * 关联附件
     */
    private String businessId;
    
    /**
     * 文件上传dto
     */
    private List<SysUploadFileInfo> fileArray;
    
    /**
     * 风险试算实体类
     */
    private List<RiskTrialRetDto> riskTrialRetDto;

    /**
     * 本方交易员
     */
    private String trader;

    /**
     * 净价偏离度
     */
    private BigDecimal deviationDegree;

    /**
     * 获取 chlIdName
     * 
     * @return chlIdName.
     */
    public String getChlIdName() {
        return chlIdName;
    }

    /**
     * 设置 chlIdName
     * 
     * @param chlIdName chlIdName
     */
    public void setChlIdName(String chlIdName) {
        this.chlIdName = chlIdName;
    }

    /**
     * finprodAbbr
     * @return finprodAbbr
     */
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    /**
     * finprodAbbr
     * @param finprodAbbr finprodAbbr
     */
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    /**
     * 获取 fpriceAmt
     * @return fpriceAmt.
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 设置 fpriceAmt
     * @param fpriceAmt fpriceAmt
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }
  
    /**
     * 获取 riskTrialRetDto
     * @return riskTrialRetDto.
     */
    public List<RiskTrialRetDto> getRiskTrialRetDto() {
        return riskTrialRetDto;
    }
   
    /**
     * 设置 riskTrialRetDto
     * @param riskTrialRetDto riskTrialRetDto
     */
    public void setRiskTrialRetDto(List<RiskTrialRetDto> riskTrialRetDto) {
        this.riskTrialRetDto = riskTrialRetDto;
    }


    /**
     * 获取 finvdate
     * @return finvdate.
     */
    public Date getFinvdate() {
        return finvdate;
    }
  
    /**
     * 设置 finvdate
     * @param finvdate finvdate
     */
    public void setFinvdate(Date finvdate) {
        this.finvdate = finvdate;
    }
   
    /**
     * 获取 finmdate
     * @return finmdate.
     */
    public Date getFinmdate() {
        return finmdate;
    }
   
    /**
     * 设置 finmdate
     * @param finmdate finmdate
     */
    public void setFinmdate(Date finmdate) {
        this.finmdate = finmdate;
    }

    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<SysUploadFileInfo> getFileArray() {
        return fileArray;
    }

    
    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<SysUploadFileInfo> fileArray) {
        this.fileArray = fileArray;
    }



    /**
     * 获取 finprodMarketId
     * @return finprodMarketId.
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 设置 finprodMarketId
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
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
     * 获取 portfolioName
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    
    /**
     * 获取 finprodName
     * @return finprodName.
     */
    public String getFinprodName() {
        return finprodName;
    }

    /**
     * 设置 finprodName
     * @param finprodName finprodName
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * 获取 exerYield
     * @return exerYield.
     */
    @Rate(fieldName = "EtrIbBondinfo002.exerYield", groups = {ValidateGroupEdit.class})
    public BigDecimal getExerYield() {
        return exerYield;
    }

    
    /**
     * 设置 exerYield
     * @param exerYield exerYield
     */
    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    /**
     * 获取 matureYield
     * @return matureYield.
     */
    public BigDecimal getMatureYield() {
        return matureYield;
    }

    /**
     * 设置 matureYield
     * @param matureYield matureYield
     */
    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }


    /**
     * 数值型清算速度
     */
    public long getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    /**
     * 获取 settleSpreed
     * @return settleSpreed.
     */
    @NotEmpty(fieldName = "EtrIbBondinfo002.settleSpreed", groups = {ValidateGroupEdit.class})
    public String getSettleSpreed() {
        return settleSpreed;
    }

    /**
     * 设置 settleSpreed
     * @param settleSpreed settleSpreed
     */
    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }
    
    /**
     * 
     * 取得组合代码
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.portfolioId", groups = {ValidateGroupEdit.class})
    public String getPortfolioId() {
        return super.getPortfolioId();
    }
    
    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }
    
    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.mdate", groups = {ValidateGroupEdit.class})
    public Date getMdate() {
        return super.getMdate();
    }
    
    /**
     * 取得 币种
     * @return 币种
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }
    
    /**
     * 取得 交易方向
     * @return 交易方向
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.ps", groups = {ValidateGroupEdit.class})
    public String getPs() {
        return super.getPs();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodId() {
        return super.getFinprodId();
    }
    

    /**
     * 取得 委托额度，交易金额或者数量
     * @return 委托额度，交易金额或者数量
     */
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.entrustAmt", groups = {ValidateGroupEdit.class})
    @Amount(fieldName = "EtrIbBondinfo002.entrustAmt", groups = {ValidateGroupEdit.class})
    public BigDecimal getEntrustNum() {
        return super.getEntrustNum();
    }

  
    @Override
    @Rate(fieldName = "EtrIbBondinfo002.price", maxValue=100000000, groups = {ValidateGroupEdit.class})
    public BigDecimal getPrice(){
        return super.getPrice();
    }
    
    @Override
    @NotEmpty(fieldName = "EtrIbBondinfo002.priceModel", groups = {ValidateGroupEdit.class})
    public String getPriceModel() {
        return super.getPriceModel();
    }
    
    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = Field.PK_1)
    public String getEntrustId() {
        return super.getEntrustId();
    }
    
    /**
     * 获取 assetAvailablePosition
     * @return assetAvailablePosition.
     */
    public BigDecimal getAssetAvailablePosition() {
        return assetAvailablePosition;
    }
   
    /**
     * 设置 assetAvailablePosition
     * @param assetAvailablePosition assetAvailablePosition
     */
    public void setAssetAvailablePosition(BigDecimal assetAvailablePosition) {
        this.assetAvailablePosition = assetAvailablePosition;
    }
    
    
	 /**
     * 获取 recStatus
     * @return recStatus.
     */
    public String getRecStatus() {
		return recStatus;
	}

	 /**
     * 获取 recStatus
     * @return recStatus.
     */
	public void setRecStatus(String recStatus) {
		this.recStatus = recStatus;
	}
	
	 /**
     * 获取 settleDate
     * @return settleDate.
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     * @param settleDate settleDate
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }
    
	/**
     * 获取 calflag
     * @return calflag.
     */
    public String getCalflag() {
        return calflag;
    }
    
    /**
     * 设置 calflag
     * @param calflag calflag
     */
    public void setCalflag(String calflag) {
        this.calflag = calflag;
    }
    
    /**
     * 获取 unitInt
     * @return unitInt.
     */
    public BigDecimal getUnitInt() {
        return unitInt;
    }
   
    /**
     * 设置 unitInt
     * @param unitInt unitInt
     */
    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }
   
    /**
     * 获取 unitFprice
     * @return unitFprice.
     */
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }
  
    /**
     * 设置 unitFprice
     * @param unitFprice unitFprice
     */
    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }
    
    /**
     * 获取 intAmt
     * @return intAmt.
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }
  
    /**
     * 设置 intAmt
     * @param intAmt intAmt
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }
    
    /**
     * 获取 importFlow
     *
     * @return importFlow
     */
    public boolean isImportFlow() {
        return importFlow;
    }

    /**
     * 设置 importFlow
     *
     * @param importFlow importFlow
     */
    public void setImportFlow(boolean importFlow) {
        this.importFlow = importFlow;
    }
    
    
	public String getAssetType1() {
		return assetType1;
	}

	public void setAssetType1(String assetType1) {
		this.assetType1 = assetType1;
	}

    /**
     * 获取 closeWay
     * @return closeWay.
     */
    public String getCloseWay() {
        return CloseWay;
    }

    /**
     * 设置 closeWay
     * @param closeWay closeWay
     */
    public void setCloseWay(String closeWay) {
        CloseWay = closeWay;
    }
    
    

	public String getSecAcctId() {
		return secAcctId;
	}

	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}
	
	

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}
	
	
	public OperationType getOperType() {
		return operType;
	}

	public void setOperType(OperationType operType) {
		this.operType = operType;
	}

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 获取 counterType
     *
     * @return counterType
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 counterType
     *
     * @param counterType counterType
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

	/**
     * 
     * 设置委托状态数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusEntrustStatus(){
        return SysDataUtil.getNameFromDataDict(DictCode.委托状态, super.getEntrustStatus());
    }
    
    /**
     * 
     * 设置币种数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusCcy(){
        return SysDataUtil.getNameFromDataDict(DictCode.币种, super.getCcy());

    }
    
    /**
     * 
     * 设置交易方向数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getBusPs(){
        return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
    }


    /**
     * 
          * 设置价格模式数据字典
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String getPriceModelStr(){
        if (StringUtil.isEmpty(super.getPriceModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.价格模式, super.getPriceModel());
    }

    private String parentPortfolioId;


    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }
    
    /**
     * 选中的数据
     */
    private transient List<EtrIbBondinfo002Dto> selectedItem;
    
    /**
     * 资产本金（授信用）
     */
    private BigDecimal prinAmt;
    
    /**
     * 序号
     */
    private String indexId;
    
    /**
     * 新增个性分类三、四。王贵奇，2019/9/27 开始
     * 个性分类三，对应资产三级分类
     */
    private String assetType10;
    
    /**
     * 交易对手名称
     */
    private String counterIdName;
    
    /**
     * 委托开始日Text
     */
    private String vdateText;
    
    /**
     * 委托截止日Text
     */
    private String mdateText;
    
    /**
     * 投资人员信息DTO
     */
    @Valid
    private BussEtrInvestPersonInfoDto etrInvestPersonInfo;
    
    /**
     * 通道可用现金
     */
    private BigDecimal channelAvailableCash;
    
    /**
     * 本方券券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustOwnNum;
    
    /**
     * 融入券/卖空购回券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustIntegrateNum;

    /**
     * 证券账户名称
     */
    private String secManageAcctIdName;

	public List<EtrIbBondinfo002Dto> getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(List<EtrIbBondinfo002Dto> selectedItem) {
		this.selectedItem = selectedItem;
	}

	public BigDecimal getPrinAmt() {
		return prinAmt;
	}

	public void setPrinAmt(BigDecimal prinAmt) {
		this.prinAmt = prinAmt;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getAssetType10() {
		return assetType10;
	}

	public void setAssetType10(String assetType10) {
		this.assetType10 = assetType10;
	}

	public String getCounterIdName() {
		return counterIdName;
	}

	public void setCounterIdName(String counterIdName) {
		this.counterIdName = counterIdName;
	}
	
	/**
     * 获取 vdateText
     *
     * @rturn vdateText
     */
    public String getVdateText() {
        vdateText = DateUtil.format(this.getVdate());
        return this.vdateText;
    }
    
    /**
     * 获取 mdateText
     *
     * @rturn mdateText
     */
    public String getMdateText() {
        mdateText = DateUtil.format(this.getMdate());
        return this.mdateText;
    }

	public BussEtrInvestPersonInfoDto getEtrInvestPersonInfo() {
		return etrInvestPersonInfo;
	}

	public void setEtrInvestPersonInfo(BussEtrInvestPersonInfoDto etrInvestPersonInfo) {
		this.etrInvestPersonInfo = etrInvestPersonInfo;
	}

	public BigDecimal getChannelAvailableCash() {
		return channelAvailableCash;
	}

	public void setChannelAvailableCash(BigDecimal channelAvailableCash) {
		this.channelAvailableCash = channelAvailableCash;
	}

	public BigDecimal getEntrustOwnNum() {
		return entrustOwnNum;
	}

	public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
		this.entrustOwnNum = entrustOwnNum;
	}

	public BigDecimal getEntrustIntegrateNum() {
		return entrustIntegrateNum;
	}

	public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
		this.entrustIntegrateNum = entrustIntegrateNum;
	}
	
	/**
     * 获取 settleSpreedName
     * 
     * @return settleSpreedName.
     */
    public String getSettleSpreedName() {
        // modify by dubaojun 2020-07-03 缺陷SNSTZJY-72对应：导入文件中的清算速度为【01:T+0】时，待办中的清算速度显示成【T+01】，应该显示成【T+0】 start
        return SysDataUtil.getNameFromDataDict(DictCode.清算速度, this.getSettleSpreed());
        //return "T+" + this.getSettleSpreed();
        // modify by dubaojun 2020-07-03 缺陷SNSTZJY-72对应：导入文件中的清算速度为【01:T+0】时，待办中的清算速度显示成【T+01】，应该显示成【T+0】 end
    }
    
    /**
     * 获取 tradeMarketName
     * 
     * @return tradeMarketName.
     */
    public String getTradeMarketName() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradeMarket());
    }
	
	@Override
	@NotEmpty(fieldName = "EtrIbBondinfo002.invAim", groups = {ValidateGroupEdit.class})
	public String getInvAim() {
		// TODO Auto-generated method stub
		return super.getInvAim();
	}

	public String getCalculateField() {
		return calculateField;
	}

	public void setCalculateField(String calculateField) {
		this.calculateField = calculateField;
	}

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public BigDecimal getDeviationDegree() {
        return deviationDegree;
    }

    public void setDeviationDegree(BigDecimal deviationDegree) {
        this.deviationDegree = deviationDegree;
    }

    /**
     * 获取 tradeMarketName
     *
     * @return tradeMarketName.
     */
    public String getInvAimName() {
        return SysDataUtil.getNameFromDataDict(DictCode.投资目的, super.getInvAim());
    }

    public String getSecManageAcctIdName() {
        return secManageAcctIdName;
    }

    public void setSecManageAcctIdName(String secManageAcctIdName) {
        this.secManageAcctIdName = secManageAcctIdName;
    }
}
