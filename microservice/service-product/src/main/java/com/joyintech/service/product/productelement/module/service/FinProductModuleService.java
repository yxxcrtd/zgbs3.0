/*
 * 文件名：FinProductModuleService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.product.productelement.module.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.PaFinProductMapper;
import com.joyintech.tams.common.base.dao.PrdTemplateMapper;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.product.messageadjust.bmquotation.dto.FinProductBmQuotationDto;
import com.joyintech.service.product.productelement.module.dao.FinProductModuleMapper;
import com.joyintech.service.product.productelement.module.dto.FinProductModuleInfoDto;
import com.joyintech.service.product.productelement.module.dto.FinProductPayPlanDto;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.BaseFinProductCashPayPlanService;
import com.joyintech.tams.component.base.core.BaseFinProductPayRuleService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductCashPayPlanDto;
import com.joyintech.tams.component.base.core.dto.BaseFinProductPayRuleDto;
import com.joyintech.tams.component.base.report.dto.BaseRepFinProductDto;
import com.joyintech.tams.component.base.report.prod.BaseRepFinProductInfoService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 产品模板
 * @author 何阳阳
 * @version 1.0 2018年9月17日
 * @see FinProductModuleService
 * @since 1.0
 */
@Service("backFinProductModuleService")
public class FinProductModuleService extends BaseService<FinProductModuleInfoDto> {
	
	/**
	 * 产品模板
	 */
	@Autowired
	private PrdTemplateMapper prdTemplateMapper;
    /**
     * 标的类金融产品扩展表
     */
    @Autowired
    private PaFinProductMapper paFinProductMapper;
	/**
	 * 金融产品现金流支付规则组件
	 */
	@Autowired(required = false)
	private BaseFinProductPayRuleService baseFinProductPayRuleService;

	/**
	 * 金融产品现金流计划组件
	 */
	@Autowired(required = false)
	private BaseFinProductCashPayPlanService baseFinProductCashPayPlanService;

	@Autowired
	private FinProductModuleMapper finProductModuleMapper;

	/**
	 * 标的类金融产品基本信息组件
	 */
	@Autowired(required = false)
	private BaseFinProductBaseInfoService baseFinProductBaseInfoService;
//
//    /**
//     * 理财产品文档模板
//     */
//    @Autowired(required = false)
//	private PrdFileMapper prdFileMapper;

    /**
     * 产品端中债报送信息
     */
    @Autowired(required = false)
	private BaseRepFinProductInfoService baseRepFinProductInfoService;

	/**
	 * 付息计划入库
	 * @param productPayPlan 付息计划
	 */
	public void insertFinProdPayPlan(FinProductPayPlanDto productPayPlan) {
		if(null != productPayPlan) {
			// 金融产品现金流支付规则
			if(null != baseFinProductPayRuleService) {
				baseFinProductPayRuleService.insert(Arrays.asList(productPayPlan.getCashPayRule()));
			}
			if(null != baseFinProductCashPayPlanService) {
				baseFinProductCashPayPlanService.insertList(productPayPlan.getCashPlanList());
			}
		}
	}

	/**
	 * 付息计划更新
	 * @param productPayPlan 付息计划
	 */
	public void updateFinProdPayPlan(FinProductPayPlanDto productPayPlan) {
		if(null != productPayPlan) {
			FinCashPayRule payRule = productPayPlan.getCashPayRule();
			// 金融产品现金流支付规则
			if(null != baseFinProductPayRuleService) {
				BaseFinProductPayRuleDto payRuleDto = new BaseFinProductPayRuleDto();
				payRuleDto.setCashFlowId(payRule.getCashId());
				payRuleDto.setEffectivDay(payRule.getEffDate());
				payRuleDto.setFinCashPayRuleList(Arrays.asList(payRule));
				baseFinProductPayRuleService.update(payRuleDto);
			}
			// 金融产品现金流计划
			if(null != baseFinProductCashPayPlanService) {
				BaseFinProductCashPayPlanDto payPlanDto = new BaseFinProductCashPayPlanDto();
				payPlanDto.setCashId(payRule.getCashId());
				payPlanDto.setFinCashPlanList(productPayPlan.getCashPlanList());
				baseFinProductCashPayPlanService.update(payPlanDto);
			}
		}
	}

	/**
	 * 付息计划更新
	 * @param cashId 现金流代码
	 * @param finprodId 金融产品代码
	 */
	public FinProductPayPlanDto selectFinProdPayPlan(String cashId, String finprodId) {
		FinProductPayPlanDto result = new FinProductPayPlanDto();
		// 金融产品现金流支付规则
		if(null != baseFinProductPayRuleService) {
			FinCashPayRule cashPayRule = new FinCashPayRule();
			cashPayRule.setFinprodId(finprodId);
			List<FinCashPayRule> ruleList = baseFinProductPayRuleService.select(cashPayRule);
			if(CollectionUtils.isNotEmpty(ruleList)) {
				result.setCashPayRule(ruleList.get(0));
			}
		}
		// 金融产品现金流计划
		if(null != baseFinProductCashPayPlanService) {
			FinCashPlan finCashPlan = new FinCashPlan();
			finCashPlan.setCashId(cashId);
			finCashPlan.setFinprodId(finprodId);
			result.setCashPlanList(baseFinProductCashPayPlanService.select(finCashPlan));
		}
		return result;
	}

	public void deleteFinProdPayPlan(String cashId, String finprodId) {
		// 金融产品现金流支付规则
		if(null != baseFinProductPayRuleService) {
			FinCashPayRule cashPayRule = new FinCashPayRule();
			cashPayRule.setFinprodId(finprodId);
			baseFinProductPayRuleService.delete(cashPayRule);
		}
		// 金融产品现金流计划
		if(null != baseFinProductCashPayPlanService) {
			FinCashPlan finCashPlan = new FinCashPlan();
			finCashPlan.setCashId(cashId);
			finCashPlan.setFinprodId(finprodId);
			baseFinProductCashPayPlanService.deleteByPrimaryKey(finCashPlan);
		}
	}

    /**
     * 模板名称的联想下拉框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTempletNameList(int page,int length,Map<String,Object> map) {
//        map.put("finprodName", param);
//        map.put("FINPROD_TYPE_TG_F19", DictCodeListExt1.金融产品类型_投管用_系列模板.getValue());
        return super.queryForPageForSelectReturn(page,length,() -> finProductModuleMapper.getTempletNameList(map));
    }

    /**
     * 画面查询方法
     * <功能详细描述>
     * @param page
     * @param length
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<FinProductModuleInfoDto> selectForPage(int page, int length,Map<String, Object> param){
        //param.put("FINPROD_TYPE_TG_F19", DictCodeListExt1.金融产品类型_投管用_系列模板.getValue());
        return super.queryForPage(page, length, () -> finProductModuleMapper.selectForPage(param));
    }

    /**
     * 删除操作
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int delete(FinProductBmQuotationDto dto) {
        List<String> finprodIdList = new ArrayList<String>();
        //删除金融产品扩展表
        paFinProductMapper.deleteByPrimaryKey(dto.getFinprodId());
        //调用标的类金融产品信息组件删除

        finprodIdList.add(dto.getFinprodId());
        baseFinProductBaseInfoService.delete(finprodIdList);
        //电泳产品端中债报送基本信息组件删除
        BaseRepFinProductDto baseRepFinProductDto = new BaseRepFinProductDto();
        baseRepFinProductDto.setRepFinprodIds(finprodIdList);
        baseRepFinProductInfoService.deleteRepFinProductInfo(baseRepFinProductDto);
        return 0;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public String getCashId(String finprodId) {
        return finProductModuleMapper.getCashId(finprodId);
    }


    /**
     * 以金融产品代码检索模板信息
     * @param finprodId 金融产品代码
     * @return
     * @see
     * @since 1.0
     */
    public FinProductModuleInfoDto queryModuleInfoByFinprodId(Map<String, Object> map) {
    	map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
    	map.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
        return finProductModuleMapper.queryModuleInfoByFinprodId(map);
    }


    /**
     * 以模板名称检索模板信息
     * @param finprodId 金融产品代码
     * @return
     * @see
     * @since 1.0
     */
    public FinProductModuleInfoDto queryModuleInfoByFinprodName(Map<String, Object> map) {
    	map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
    	map.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
        return finProductModuleMapper.queryModuleInfoByFinprodName(map);
    }
    
    /**
     * 根据模板ID查询模板
     * @param templateId
     * @return
     */
    public FinProductModuleInfoDto selectByTemplateId(String templateId) {
    	return finProductModuleMapper.selectByTemplateId(templateId);
    }
}
