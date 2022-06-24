/*
 * 文件名：FinProductOpenPlanService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.product.common.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.FinOpenRule;
import com.joyintech.tams.component.base.core.BaseFinProductOpenPlanService;
import com.joyintech.tams.component.base.core.BaseFinProductOpenRuleService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductOpenPlanDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductOpenRuleDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.product.common.dao.FinProductOpenPlanMapper;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import com.joyintech.service.product.common.dto.FinProductOpenPlanDto;

/**
 * 产品开放计划信息共通
 * @author 张亮
 * @version 1.0 2018年9月17日
 * @see FinProductOpenPlanService
 * @since 1.0
 */
@Service("backFinProductOpenPlanService")
public class FinProductOpenPlanService {
	
	
	/**
	 * {@linkplain}
	 */
	@Autowired(required=false)
	private BaseFinProductOpenRuleService baseFinProductOpenRuleService;
	
	/**
     * {@linkplain}
     */
    @Autowired(required=false)
	private FinProductOpenPlanMapper finProductOpenPlanMapper;
	
	/**
	 * {@linkplain}
	 */
	@Autowired(required=false)
	private BaseFinProductOpenPlanService baseFinProductOpenPlanService;
	
	
	/**
	 * 开放计划信息入库
	 * @param productOpenPlanDto 开放计划信息
	 */
	@Transactional
	public void insertFinProdPayPlan(FinProductOpenPlanDto productOpenPlanDto) {
		if(null != productOpenPlanDto) {
			// 金融产品现金流支付规则
			if(null != baseFinProductOpenRuleService) {
				baseFinProductOpenRuleService.insert(Arrays.asList(productOpenPlanDto.getFinOpenRule()));
			}
			if(null != baseFinProductOpenPlanService) {
				baseFinProductOpenPlanService.insert(productOpenPlanDto.getFinOpenPlanList());
			}
		}
	}
	
	/**
	 * 开放计划信息更新
	 * @param productOpenPlanDto 开放计划信息
	 */
	@Transactional
	public void updateFinProdPayPlan(FinProductOpenPlanDto productOpenPlanDto) {
		if(null != productOpenPlanDto) {
			FinOpenRule finOpenRule = productOpenPlanDto.getFinOpenRule();
			// 金融产品现金流支付规则
			if(null != baseFinProductOpenRuleService) {
				BaseFinProductOpenRuleDto dto = new BaseFinProductOpenRuleDto();
				dto.setCashId(finOpenRule.getCashId());
				dto.setFinOpenRuleList(Arrays.asList(finOpenRule));
				baseFinProductOpenRuleService.update(dto);
			}
			// 金融产品现金流计划
			if(null != baseFinProductOpenPlanService) {
				BaseFinProductOpenPlanDto payPlanDto = new BaseFinProductOpenPlanDto();
				payPlanDto.setCashId(finOpenRule.getCashId());
				payPlanDto.setFinOpenPlanList(productOpenPlanDto.getFinOpenPlanList());
				baseFinProductOpenPlanService.updataFinOpenPlan(payPlanDto);
			}
		}
	}
	
	/**
	 * @param cashId 现金流代码
	 */
	public FinProductOpenPlanDto selectFinProdPayPlan(String cashId) {
		FinProductOpenPlanDto result = new FinProductOpenPlanDto();
		// 金融产品现金流支付规则
		if(null != baseFinProductOpenRuleService) {
			BaseFinProductOpenRuleDto finProductOpenRuleDto = new BaseFinProductOpenRuleDto();
			finProductOpenRuleDto.setCashId(cashId);
			List<FinOpenRule> ruleList = baseFinProductOpenRuleService.search(finProductOpenRuleDto);
			if(CollectionUtils.isNotEmpty(ruleList)) {
				result.setFinOpenRule(ruleList.get(0));
			}
		}
		// 金融产品现金流计划
		if(null != baseFinProductOpenPlanService) {
			BaseFinProductOpenPlanDto baseFinProductOpenPlanDto = new BaseFinProductOpenPlanDto();
			baseFinProductOpenPlanDto.setCashId(cashId);
			result.setFinOpenPlanList(baseFinProductOpenPlanService.selectByFinOpenPlan(baseFinProductOpenPlanDto));
		}
		return result;
	}

	/**
	 * 根据现金流代码删除开放计划
	 * @param cashId 现金流代码
	 */
	@Transactional
	public void deleteFinProdPayPlan(String cashId) {
		// 金融产品现金流支付规则
		if(null != baseFinProductOpenRuleService) {
			BaseFinProductOpenRuleDto finProductOpenRuleDto = new BaseFinProductOpenRuleDto();
			finProductOpenRuleDto.setCashId(cashId);
			baseFinProductOpenRuleService.delete(finProductOpenRuleDto);
		}
		// 金融产品现金流计划
		if(null != baseFinProductOpenPlanService) {
			BaseFinProductOpenPlanDto finProductOpenPlanDto = new BaseFinProductOpenPlanDto();
			finProductOpenPlanDto.setCashId(cashId);
			baseFinProductOpenPlanService.deleteByFinOpenPlan(finProductOpenPlanDto);
		}
	}
	
	/**
	 * 根据金融产品代码查询开放计划信息
	 * @param finprodId 金融产品代码
	 * @return 开放计划信息
	 * @throws Exception 
	 */
	public List<BussFinProductOpenPlanDto> selectOpenPlanInfoByFinprodId(String finprodId) throws InvocationTargetException, IllegalAccessException {
	    
		List<BussFinProductOpenPlanDto> bussFinProductOpenPlanDtoList = new ArrayList<BussFinProductOpenPlanDto>();
	    Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        map.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        
        List<FinOpenRule> list = finProductOpenPlanMapper.queryOpenPlanInfoByFinprodId(map);
        if (list == null || list.size() == 0) {
        	return bussFinProductOpenPlanDtoList;
        }
        for(FinOpenRule finOpenRule:list){
        BussFinProductOpenPlanDto bussFinProductOpenPlanDto = new BussFinProductOpenPlanDto();
        if (finOpenRule != null){
            BeanUtil.copyProperties(bussFinProductOpenPlanDto, finOpenRule);
            if (StringUtil.equals(finOpenRule.getDayType(), DictCodeListExt1.日期类型_周几.getValue())){
                bussFinProductOpenPlanDto.setDayOfWeek(finOpenRule.getDayValue());
            }
            else if (StringUtil.equals(finOpenRule.getDayType(), DictCodeListExt1.日期类型_第几工作日.getValue())){
                bussFinProductOpenPlanDto.setWorkdayOfMonth(finOpenRule.getDayValue());
            }
            else if (StringUtil.equals(finOpenRule.getDayType(), DictCodeListExt1.日期类型_第几自然日.getValue())){
                bussFinProductOpenPlanDto.setDayOfMonth(finOpenRule.getDayValue());
            }
            else if (StringUtil.equals(finOpenRule.getDayType(), DictCodeListExt1.日期类型_第几周周几.getValue())){
                String dayValue = finOpenRule.getDayValue();
                if(StringUtil.isNotEmpty(dayValue) && dayValue.indexOf(";") > 0){
                    int index = dayValue.indexOf(";");
                    bussFinProductOpenPlanDto.setWeekOfMonth(dayValue.substring(0, index));
                    bussFinProductOpenPlanDto.setWorkdayOfWeek(dayValue.substring(index + 1));
                }
            }
        }
        bussFinProductOpenPlanDtoList.add(bussFinProductOpenPlanDto);
        }
        return bussFinProductOpenPlanDtoList;
	}
	/**
     * 根据金融产品代码查询开放计划信息
     * @param finprodId 金融产品代码
     * @return 开放计划信息
     */
	public List<FinOpenPlan> queryOpenPlanListByFinprodId(String finprodId){
	    Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        map.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        
        return finProductOpenPlanMapper.queryOpenPlanListByFinprodId(map);
	}
}
