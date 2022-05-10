package com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.RskIndividConditionCustom;
import com.joyintech.tams.common.base.entity.RskIndividConditionLine;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividAssetDelDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividConditionDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialAssetDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialStageDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividUniteDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskSupervisionIndividuationDto;

@Repository
public interface RskSupervisionIndividuationMapper {

	List<RskSupervisionIndividuationDto> selectForPage(Map<String, Object> param);

	int deleteAllCondition(RskSupervisionIndividuationDto dto);

	List<SelectOptionDto> queryUniteIndIdList(Map<String, Object> param);

	List<SelectOptionDto> queryUniteIndIdList_RULE(Map<String, Object> param);

	List<RskIndividConditionDto> selectIndividuationForPage(Map<String, Object> param);

	List<RskIndividUniteDto> selectUniteForPage(Map<String, Object> param);

	int deleteAllUnite(RskSupervisionIndividuationDto dto);

	int deleteAllAssetDel(RskSupervisionIndividuationDto dto);

	int deleteAllSpecialStage(RskSupervisionIndividuationDto dto);

	List<SelectOptionDto> queryFinprodIdList(Map<String, Object> param);

	List<RskIndividAssetDelDto> findPageForAssetDel(Map<String, Object> param);

	List<RskIndividSpecialStageDto> findPageForSpecial(Map<String, Object> param);

	List<RskIndividSpecialAssetDto> findPageForSpecialAsset(Map<String, Object> param);

	String getIndType(String indId);

	List<SelectOptionDto> getFinprodId(Map<String, Object> param);

	RskIndividSpecialStageDto queryForSpecial(String uniteId);

	RskIndividSpecialAssetDto queryForAsset(String uniteId);

	String getIndName(String indId);

	String getVdate(String portfolioId);

	List<RskIndividConditionCustom> findAll(Map<String, Object> param);

    int checkExistsUsing(Map<String, Object> param);

	List<SelectOptionDto> queryConditionList(Map<String, Object> param);

    List<RskIndividConditionLine> findAllCustomizedItem(Map<String, Object> param);

	void deleteAllConditionLines(RskSupervisionIndividuationDto dto);

    List<String> findPortfolioByPackId(String prtBagId);
}
