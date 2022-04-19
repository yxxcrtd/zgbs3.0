package com.joyintech.tams.cfets.trd.repo.dao;

import com.joyintech.tams.common.base.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 本币交易-质押式回购交易 Mapper
 *
 * @author jiangpeng
 * @date 2021/12/4 15:39
 */
@Repository
public interface CfetsIbuQuoteRepo004Mapper {
    String findTraderByPartyId(@Param("partyId") String partyId);

    String findTraderByCounterId(@Param("counterId") String counterId);

    int getIsExistCounterId(@Param("entrustIdList") List<String> entrustIdList);

    List<EtrSecPositionFlow> selectEtrSecPositionFlow(Map<String,Object> param);

    List<PtlCashPositionFlow> selectPtlCashPositionFlow(Map<String,Object> param);

    int updatePosition(EtrSecPosition etrSecPosition);


    int updatePtlCashPosition(PtlCashPosition ptlCashPosition);

    List<String> selectOldTradeId(@Param("entrustIdList") List<String> entrustIdList);
}
