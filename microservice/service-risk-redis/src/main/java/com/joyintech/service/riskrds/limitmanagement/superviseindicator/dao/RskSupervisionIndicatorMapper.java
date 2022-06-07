/*
 * 文件名：RskSupervisionIndicatorMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年10月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.riskrds.limitmanagement.superviseindicator.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.service.riskrds.limitmanagement.rsksupervisionindicatorparam.dto.RskSupervisionIndicatorParamDto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskPortfolioIndicatorDto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskPortfolioPackageDto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.service.riskrds.limitmanagement.superviseindicator.dto.RskSupervisionIndicatorDto;

/**
 * 
 * @author 裴家俊
 * @version 1.0 2018年10月24日
 * @see RskSupervisionIndicatorMapper
 * @since 1.0
 */
@Repository
public interface RskSupervisionIndicatorMapper {

    List<RskSupervisionIndicator002Dto> selectForPage(Map<String, Object> param);
    
    List<SelectOptionDto> getIndicatorParamList();
    //----add by jjpei 20190811---start-----
    @Select("${sql}")
    List<SelectOptionDto> getForbidEnumInfo(@Param("sql") String sql);
    //----add by jjpei 20190811---end-----
    List<TreeNode<RskSupervisionIndicatorDto>> getAllPortfolioByProfit(@Param(value = "indId") String indId,@Param(value = "portfLevel") String portfLevel,@Param(value = "portfName") String portfName,@Param(value = "profitType") String profitType,@Param(value = "issueMode") String issueMode,@Param(value = "raiseType") String raiseType,@Param(value = "investType") String investType,@Param(value = "platDate") String platDate,@Param(value = "isFaceSingleInvestor") String isFaceSingleInvestor);
    List<TreeNode<RskSupervisionIndicatorDto>> getAllPortfolioByOther(@Param(value = "fields") String fields,@Param(value = "portfLevel") String portfLevel,@Param(value = "indId") String indId,@Param(value = "portfName") String portfName,@Param(value = "profitType") String profitType,@Param(value = "issueMode") String issueMode,@Param(value = "raiseType") String raiseType,@Param(value = "investType") String investType,@Param(value = "platDate") String platDate,@Param(value = "isFaceSingleInvestor") String isFaceSingleInvestor);
    
    List<TreeNode<RskPortfolioPackageDto>> getAllPortfolioBag(@Param(value = "bagName") String bagName,@Param(value = "indId") String indId,@Param(value = "portfLevel") String portfLevel);
    int insertSupervisionIndicator(RskSupervisionIndicatorDto rskSupervisionIndicatorDto);
    int insertPortIndicator(RskPortfolioIndicatorDto rskPortfolioIndicatorDto);
    
    void updateIsEnabled(RskSupervisionIndicator002Dto dto);
    
    RskSupervisionIndicator002Dto querySuperIndicatorInfoByIndId(Map<String, Object> param);

    RskSupervisionIndicator002Dto querySuperIndicatorInfoByIndId_RULE(Map<String, Object> param);

    List<RskPortfolioIndicatorDto> queryPortfIndicatorInfoByIndId(Map<String, Object> param);
    
    RskSupervisionIndicatorParamDto searchAssetTypeByParam(@Param("indParamId") String indParamId);
    
    int searchRepeatByIndName(@Param("indName") String indName,@Param("indId") String indId);
    
    int  searchReleteCountByIndId(@Param("indId") String indId);

    int  searchReleteCountByRuleId(@Param("indId") String indId);

	int isConfigure(String indId);

	Integer deleteCondition(String indId);

	Integer deleteAllUnite(String indId);

	Integer deleteAllAssetDel(String indId);

	Integer deleteAllSpecialStage(String indId);

	List<String> getAllPortfolioId(String indId);
}
