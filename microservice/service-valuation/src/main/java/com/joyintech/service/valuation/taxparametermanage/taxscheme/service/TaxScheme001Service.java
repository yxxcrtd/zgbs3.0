/*
 * 文件名：TaxScheme001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.taxparametermanage.taxscheme.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dao.TaxScheme001Mapper;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dao.TaxScheme005Mapper;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dto.TaxScheme001Dto;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dto.TaxScheme005Dto;

/**
 * 税务方案 - 主界面公用 
 * @author zhangwei
 * @version 1.0 2018年10月11日
 * @see TaxScheme001Service
 * @since 1.0
 */
@Service(value="serviceTaxScheme001Service")
@Transactional(rollbackFor = Exception.class)
public class TaxScheme001Service extends CommonExportService<TaxScheme001Dto>{
	
	@Autowired 
	private TaxScheme001Mapper taxScheme001Mapper;
	
	@Autowired 
    private TaxScheme005Mapper taxScheme005Mapper;
	
	/**
     * 获取税务方案 - 分页
     * @return
     * @throws Exception
     */
	public Page<TaxScheme001Dto> findSchemeByPage(int page, int length, Map<String, Object> parameters) {
		return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectScheme(parameters));
    }

	/**
     * 获取增值税则表 - 分页
     * @return
     * @throws Exception
     */
	public Page<TaxScheme001Dto> findVatTaxByPage(int page, int length, Map<String, Object> parameters) {
		return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectVatTax(parameters));
	}
	
	/**
     * 获取增值税则表 - 分页
     * @return
     * @throws Exception
     */
	public List<TaxScheme001Dto> findVatTax(Map<String, Object> parameters) {
		return taxScheme001Mapper.selectVatTax(parameters);
	}

    /**
     * 获取附加税则表 - 分页
     * @return
     * @throws Exception
     */
    public Page<TaxScheme001Dto> findAdtTaxByPage(int page, int length, Map<String, Object> parameters) {

        if(MultilevelDataAuthorityUtil.isEnabled()){

            return super.queryForPage(page, length, ()-> taxScheme001Mapper.findAdtTaxEJFR(parameters));
        }else {
            return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectAdtTax(parameters));
        }
    }

    /**
     * 获取附加税则表
     * @return
     * @throws Exception
     */
    public List<TaxScheme001Dto> findAdtTax(Map<String, Object> parameters){
        if(MultilevelDataAuthorityUtil.isEnabled()){

            List<String> treeInstCode = LoginUserUtil.getTreeInstCode();
            parameters.put("instCode",treeInstCode);
            return  taxScheme001Mapper.findAdtTaxEJFR(parameters);
        }else {
            return taxScheme001Mapper.selectAdtTax(parameters);
        }
    }


    /**
     * 获取资产应税表 - 分页
     * @param pageParam
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<TaxScheme001Dto> findTaxableByPage(int page, int length, Map<String, Object> pageParam) {
        return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectTaxable(pageParam));
    }
    
    /**
     * 获取资产应税表
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public List<TaxScheme001Dto> findTaxable(Map<String, Object> pageParam) {
        return taxScheme001Mapper.selectTaxable(pageParam);
    }

    
    /**
     * 获取资产代码 - 下拉框
     * 
     * @return
     * @throws Exception
     */
    public List<SelectOptionDto> findAsset(Map<String, Object> pageParam) {
        pageParam.put("InstCode",LoginUserUtil.getInstCode());
        List<TaxScheme005Dto> resultList = taxScheme005Mapper.selectAsset(pageParam);
        List<SelectOptionDto> selectOptionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (TaxScheme005Dto item : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(item.getAssetId() ); 
                selectOptionDto.setText(item.getAssetId() + "(" + item.getAssetName() + ")");
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }
    
    /**
     * 查询参数设置 - 分页
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<TaxScheme001Dto> findConfigByPage(int page, int length, Map<String, Object> pageParam) {
        return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectConfig(pageParam));
    }
    
    /**
     * 查询参数设置
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<TaxScheme001Dto> findConfig(Map<String, Object> pageParam) {
        return taxScheme001Mapper.selectConfig(pageParam);
    }

    /**
     * 查询会计场景计税配置 - 分页
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<TaxScheme001Dto> findTaxSchaBokConfigByPage(int page, int length, Map<String, Object> pageParam) {
        return super.queryForPage(page, length, ()-> taxScheme001Mapper.selectTaxSchaBokConfig(pageParam));
    }

    /**
     * 查询会计场景计税配置
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<TaxScheme001Dto> findTaxSchaBokConfig(Map<String, Object> pageParam){
        return taxScheme001Mapper.selectTaxSchaBokConfig(pageParam);
    }
}
