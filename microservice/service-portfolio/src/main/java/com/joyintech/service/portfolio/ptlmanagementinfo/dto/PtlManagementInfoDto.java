/*
 * 文件名：PtlManagementInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlmanagementinfo.dto;
import java.math.BigDecimal;
import java.util.List;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.PtlValParam;

/**
 * 组合管理实体类
 * @author fanshuanghu
 * @version 1.0 2018年9月13日
 * @see PtlManagementInfoDto
 * @since 1.0
 */
public class PtlManagementInfoDto extends PtlPortfolio {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 8559576346600595510L;
    /**
     * 是否保本
     */
    private String isGuarantee;
    /**
     * 开放类型，开放式、封闭式
     */
    private String openType;
    /**
     * 募集方式，公募、私募
     */
    private String raiseType;
    /**
     * 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    private String investNature;
    /**
     * 收益类型字符串
     */
    private String profitTypeStr;
    /**
     * 组合类型字符串
     */
    private String portfolioTypeStr;
    /**
     * 选择的行
     */
    private List<PtlManagementInfoDto> selectedItem;
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;
    /**
     * 用户ID
     */
    private List<String> userId;
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;
    /**
     * 资金账户信息实体类
     */
    private List<FundAccountInfoDto> fundAccountInfoDto;
    /**
     * 证券账户实体类
     */
    private List<SecuritiesAccountInfoDto> securitiesAccountInfoDto;
    
 // 一体化对应etliu start
    /**
     * 证券账户实体类
     */
    private PtlValParam ptlValParam;

    /**
     * 投资策略代码
     */
    private String strategyId;

    /**
     * 投资策略名称
     */
    private String strategyName;

    /**
     * 继承比例
     */
    private BigDecimal disPercent;

    private String childPortfolioId;

    private String childPortfolioName;

    /**
     * 组合系列
     */
    private String portfolioSeries;


    /**
     * 获取 ptlValParam
     * @return ptlValParam.
     */
    public PtlValParam getPtlValParam() {
        return ptlValParam;
    }

    /**
     * 设置 ptlValParam
     * @param ptlValParam ptlValParam
     */
    public void setPtlValParam(PtlValParam ptlValParam) {
        this.ptlValParam = ptlValParam;
    }
 // 一体化对应etliu end
    
    /**
     * 获取 userId
     * @return userId.
     */
    public List<String> getUserId() {
        return userId;
    }
    
    /**
     * 设置 userId
     * @param userId userId
     */
    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
    /**
     * 获取 fundAccountInfoDto
     * @return fundAccountInfoDto.
     */
    public List<FundAccountInfoDto> getFundAccountInfoDto() {
        return fundAccountInfoDto;
    }
    /**
     * 获取 openType
     * @return openType.
     */
    public String getOpenType() {
        return openType;
    }
    /**
     * 设置 openType
     * @param openType openType
     */
    public void setOpenType(String openType) {
        this.openType = openType;
    }
    /**
     * 获取 raiseType
     * @return raiseType.
     */
    public String getRaiseType() {
        return raiseType;
    }
    /**
     * 设置 raiseType
     * @param raiseType raiseType
     */
    public void setRaiseType(String raiseType) {
        this.raiseType = raiseType;
    }
    /**
     * 获取 investNature
     * @return investNature.
     */
    public String getInvestNature() {
        return investNature;
    }
    /**
     * 设置 investNature
     * @param investNature investNature
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature;
    }


    /**
     * 设置 fundAccountInfoDto
     * @param fundAccountInfoDto fundAccountInfoDto
     */
    public void setFundAccountInfoDto(List<FundAccountInfoDto> fundAccountInfoDto) {
        this.fundAccountInfoDto = fundAccountInfoDto;
    }

    
  /*  *//**
     * 获取 ptlValParam
     * @return ptlValParam.
     *//*
    public List<PtlValParam> getPtlValParam() {
        return ptlValParam;
    }

    
    *//**
     * 设置 ptlValParam
     * @param ptlValParam ptlValParam
     *//*
    public void setPtlValParam(List<PtlValParam> ptlValParam) {
        this.ptlValParam = ptlValParam;
    }*/

    
    /**
     * 获取 securitiesAccountInfoDto
     * @return securitiesAccountInfoDto.
     */
    public List<SecuritiesAccountInfoDto> getSecuritiesAccountInfoDto() {
        return securitiesAccountInfoDto;
    }


    
    /**
     * 设置 securitiesAccountInfoDto
     * @param securitiesAccountInfoDto securitiesAccountInfoDto
     */
    public void setSecuritiesAccountInfoDto(List<SecuritiesAccountInfoDto> securitiesAccountInfoDto) {
        this.securitiesAccountInfoDto = securitiesAccountInfoDto;
    }


    /**
     * 获取 profitTypeStr
     * @return profitTypeStr.
     */
    public String getProfitTypeStr() {
        return profitTypeStr;
    }
    
    /**
     * 设置 profitTypeStr
     * @param profitTypeStr profitTypeStr
     */
    public void setProfitTypeStr(String profitTypeStr) {
        this.profitTypeStr = profitTypeStr;
    }
    
    /**
     * 获取 portfolioTypeStr
     * @return portfolioTypeStr.
     */
    public String getPortfolioTypeStr() {
        return portfolioTypeStr;
    }
    
    /**
     * 设置 portfolioTypeStr
     * @param portfolioTypeStr portfolioTypeStr
     */
    public void setPortfolioTypeStr(String portfolioTypeStr) {
        this.portfolioTypeStr = portfolioTypeStr;
    }
    /**
     * 获取 isGuarantee
     * @return isGuarantee.
     */
    public String getIsGuarantee() {
        return isGuarantee;
    }
    /**
     * 设置 isGuarantee
     * @param isGuarantee isGuarantee
     */
    public void setIsGuarantee(String isGuarantee) {
        this.isGuarantee = isGuarantee;
    }
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<PtlManagementInfoDto> getSelectedItem() {
        return selectedItem;
    }
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<PtlManagementInfoDto> selectedItem) {
        this.selectedItem = selectedItem;
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

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public BigDecimal getDisPercent() {
        return disPercent;
    }

    public void setDisPercent(BigDecimal disPercent) {
        this.disPercent = disPercent;
    }

    public String getChildPortfolioId() {
        return childPortfolioId;
    }

    public void setChildPortfolioId(String childPortfolioId) {
        this.childPortfolioId = childPortfolioId;
    }

    public String getChildPortfolioName() {
        return childPortfolioName;
    }

    public void setChildPortfolioName(String childPortfolioName) {
        this.childPortfolioName = childPortfolioName;
    }

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

}
