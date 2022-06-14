package com.joyintech.service.product.resultscovering.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.product.resultscovering.dto.FinResultsCovering001Dto;
import com.joyintech.service.product.resultscovering.dto.FinResultsCovering002Dto;

@Repository
public interface FinResultsCoveringMapper {
	/**
	 * 查询回补信息
	 * @param map
	 * @return
	 */
	List<FinResultsCovering001Dto> selectForPage(Map<String, Object> map);
	FinResultsCovering002Dto selectBaseInfo(Map<String, Object> map);
	BigDecimal getSumAmt(FinResultsCovering002Dto dto);
	FinResultsCovering002Dto selectInfo(Map<String, Object> map);
	List<FinResultsCovering001Dto> findPtradeid(@Param("tradeId") String tradeId);
	
	List<FinResultsCovering002Dto> selectInfoCovering(Map<String, Object> map);
	
    /**
     * 产品代码下拉框
     * @param paramMap 参数
     * @return 下拉框列表
     * @throws Exception
     */
    List<SelectOptionDto> queryProdIdInfo(Map<String, Object> paramMap);
    /**
     * 判断是否为高频开放
     * @param map 条件
     * @return
     */
    Integer isOpenMode02(Map<String, Object> map);
}
