package com.joyintech.service.basicdata.mstmarketmanagement.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.mstmarketmanagement.dto.MstMarketManageDto;

@Repository 
public interface MstMarketManagementMapper {

    List<MstMarketManageDto> queryForPage(Map<String,Object> pageParam);

 
	List<SuggestDto> selectFinprodName(Map<String, Object> param);

 
    List<SelectOptionDto> getGradeResult(Map<String, Object> param);


    List<SelectOptionDto> getCodeName(Map<String, Object> param);
}
