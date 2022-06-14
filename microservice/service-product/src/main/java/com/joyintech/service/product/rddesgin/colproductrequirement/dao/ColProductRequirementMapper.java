package com.joyintech.service.product.rddesgin.colproductrequirement.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.service.product.rddesgin.colproductrequirement.dto.ColProductRequirement001Dto;

@Repository
public interface ColProductRequirementMapper {
	
	/**
	 * 产品收集查询页
	 * @param param
	 * @return
	 */
	List<ColProductRequirement001Dto> selectForPage(Map<String, Object> param);
	
	/**
	 * 根据Id查询
	 * @param requirementId
	 * @return
	 */
	ColProductRequirement001Dto queryRequirementByRequireId(String requirementId);
	
	/**
	 * 新建插入
	 * @param colProductRequirement001Dto
	 */
	void insertColProductRequirement(ColProductRequirement001Dto colProductRequirement001Dto);
	
	/**
	 * 根据Id删除
	 * @param requirementId
	 * @return
	 */
	void deleteColProductRequirement(String requirementId);
	
	/**
	 * 更新需求
	 * @param colProductRequirement001Dto
	 */
	void updateColProductRequirement(ColProductRequirement001Dto colProductRequirement001Dto);
	
	void deleteByRequirementId(ColProductRequirement001Dto colProductRequirement001Dto);
	
	/**
	 * 发起初评
	 * @param requirementId
	 */
	void reviewStart(ColProductRequirement001Dto colProductRequirement001Dto); 
}
