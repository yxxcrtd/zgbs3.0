package com.joyintech.service.product.productelement.periodicmanagement.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinBaseRuleDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinProductFloatFeeDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagementDto;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;

@Repository
public interface PeriodicManagementMapper {

	List<SelectOptionDto> getFinprodIds(Map<String, Object> param);
	
	List<PeriodicManagement001Dto> selectForPage(Map<String, Object> param);

	List<SelectOptionDto> getInvestCycle(Map<String, Object> param);
	
	List<PeriodicManagementDto> selectBaseInfoList(Map<String, Object> param);

	FinBaseRuleDto selectBenchMarkInfo(Map<String, Object> param);

	List<FinProductFloatFeeDto> selectFloatFeeInfo(Map<String, Object> param);

	List<FinFloatCalcRuleDetail> selectFloatFeeList(Map<String, Object> param);
	
	List<PeriodicManagementDto> selectPeriodProdId(Map<String, Object> param);

	FinCashPayRule selectCashPayRule(Map<String, Object> param);

	FinCashCalcRule selectCashCalcRule(Map<String, Object> param);

	FinFloatCalcRule selectFinFloatCalcRule(Map<String, Object> param);

	List<FinFloatCalcRuleDetail> selectFinFloatCalcRuleDetail(Map<String, Object> param);

	void deleteFinCashPayRule(String cashId);

	void deleteFinCashCalcRule(String cashId);

	void deleteFloatCashCalcRule(String cashId);

	void deleteFloatCashCalcRuleDetail(String cashId);

	void deleteFinBaseRuleInfo(Map<String, Object> params);

	void deleteFinCashInfo(String cashId);

	List<FinCashInfo> selectFinCashInfo(Map<String, Object> param);
	
	int selectIsExistTrade(Map<String, Object> param);

	int checkPeriodId(Map<String, Object> param);

	FinBaseRuleInfo queryFinBaseRuleInfo(Map<String, Object> param);
	 
	String getProdIdByPeriodId(Map<String, Object> param);
	 /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    int getTrdProductDealByPeriodId(Map<String, Object> map);


	/**
	 * 查询相同开始日和结束日是否存在
	 */
    int checkPeriodTradeDate(Map<String, Object> param);
}
