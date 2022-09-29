package com.joyintech.tams.invest.common.risk.dao;


import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import com.joyintech.tams.invest.common.risk.dto.RiskNamListCommonDto;
import com.joyintech.tams.invest.common.risk.dto.RiskNameListDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 名单公共校验
 */
@Repository
public interface RiskNamListCommonMapper {

    /**
     * 标的
     * @param param
     * @return
     */
    List<RiskNamListCommonDto> getAllInfoByFinprodId(Map<String, Object> param);
    /**
     *  无标的
     * @param param
     * @return
     */
    List<RiskNamListCommonDto> getAllInfoByNoFinprodId(Map<String, Object> param);
    List<RiskNamListCommonDto> selectAssetCount(Map<String, Object> param);/*edit by songyawei 20200701*/
    RiskNamListCommonDto selectCustomerInfo(@Param("customerId") String customerId);
    RiskNamListCommonDto selectCustomerInfo2(@Param("counterId") String counterId,@Param("counterType") String counterType);
    List<RiskNameListDto> getNameListByFinprodId(Map<String, Object> param);
    Map<String, Object> relateOrgFlag(@Param("corOrgid") String corOrgid);
    FinProduct queryFpById(@Param("finprodId") String finPriductId);
    int findByInstructionId(@Param("instructionId")String instructionId);

    List<RiskNameListDto>  getNameListByFinprodIds(Map<String, Object> param);

    MstCounterParty queryMstCP(@Param("counterId") String counterId);

    String queryInOrderId(@Param("tradeId") String tradeId);
}
