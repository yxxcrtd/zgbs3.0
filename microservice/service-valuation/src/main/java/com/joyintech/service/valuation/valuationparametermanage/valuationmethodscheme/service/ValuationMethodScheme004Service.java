/*
 * 文件名：ValuationMethodScheme004Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.dao.ValuationMethodScheme004Mapper;
import com.joyintech.service.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme004Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 
 * 估值方法方案 - 资产分类估值
 * @author zhangwei
 * @version 1.0 2018年8月28日
 * @see ValuationMethodScheme004Service
 * @since 1.0
 */
@Service(value="serviceValuationMethodScheme004Service")
@Transactional(rollbackFor = Exception.class)
public class ValuationMethodScheme004Service extends CommonExportService<ValuationMethodScheme004Dto> implements BaseProcessService<ValuationMethodScheme004Dto>{

	@Autowired
	private ValuationMethodScheme004Mapper valuationMethodScheme004Mapper;
	
	@Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @DataSource(DataSourceType.OTHER)
	public void doComplete(ValuationMethodScheme004Dto entity, OperationType type) {
	    if(!OperationType.DELETE.equals(type)) {
    	    //界面显示市场代码，保存时保存finprodid
    	    Map<String,Object> pageParam = new HashMap<String, Object>();
            pageParam.put("finprodId", entity.getFinprodId());
            ValuationMethodScheme004Dto valuationMethodScheme004Dto =  valuationMethodScheme004Mapper.getFinprodInfoByMarketId(pageParam);
            if(valuationMethodScheme004Dto != null) {
                entity.setFinprodId(valuationMethodScheme004Dto.getOriginalFinprodId());
            }
	    }
	    if(OperationType.NEW.equals(type)){
            valuationMethodScheme004Mapper.insert(entity);
        }else if(OperationType.MODIFY.equals(type)){
            valuationMethodScheme004Mapper.updateByPrimaryKeySelective(entity);
        }else if(OperationType.DELETE.equals(type)) {
            valuationMethodScheme004Mapper.deleteByPrimaryKey(entity.getSchemeDetailId());
        }
	}
	
	/**
	 * 通过方案详情代码查询 
	 * @param schemeId
	 * @return
	 */
	public ValuationMethodScheme004Dto findBySchemeDetailId(String schemeDetailId) {
		return valuationMethodScheme004Mapper.selectByPrimaryKey(schemeDetailId);
	}

	/**
     * 获取产品代码 - 下拉框
     * @return
     * @throws Exception
     */
	public List<SelectOptionDto> findFinprod(Map<String, Object> parameters) {
		List<ValuationMethodScheme004Dto>  resultList = valuationMethodScheme004Mapper.selectFinprod(parameters);
		List<SelectOptionDto> selectOptionList = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(resultList)) {
			for (ValuationMethodScheme004Dto single : resultList) {
				SelectOptionDto  selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(single.getFinprodId());
				selectOptionDto.setText(single.getFinprodId() + "(" + single.getFinprodName() + ")");
				selectOptionDto.setObject(single.getOriginalFinprodId());//add by yanya on 20191031 原资产代码
				selectOptionList.add(selectOptionDto);
			}
		}
		return selectOptionList;
    }

	/**
	 * 删除：支持批量删除
	 * @param schemeDetailId
	 * @return
	 */
	public int deleteByBatch(String schemeDetailId) {
		return valuationMethodScheme004Mapper.deleteByBatch(schemeDetailId);
	}

	/**
	 * 新增
	 * @param dto
	 */
	public void insert(ValuationMethodScheme004Dto dto) {
		valuationMethodScheme004Mapper.insert(dto);
	}

	/**
	 * 修改
	 * @param dto
	 */
	public void updateByPrimaryKeySelective(ValuationMethodScheme004Dto dto) {
		valuationMethodScheme004Mapper.updateByPrimaryKeySelective(dto);
	}

	/**
	 * 通过产品代码，获取产品信息
	 * @param pageParam
	 * @return
	 */
	public ValuationMethodScheme004Dto getFinprodInfo(Map<String, Object> pageParam) {
		return valuationMethodScheme004Mapper.getFinprodInfo(pageParam);
	}

	/**
	 * @Description:根据资产代码获取证券品种
	 * @auther: yuzj
	 * @date: 2021/9/22
	 * @param:
	 * @return:
	 */
	public String  getSecTypeByFinprodId(String finprodId) {
		return valuationMethodScheme004Mapper.getSecTypeByFinprodId(finprodId);
	}
}
