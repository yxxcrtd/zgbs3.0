package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao;

import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketCashPlanDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FinBondMarketInfoQueryMapper {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    List<FinBondMarketInfoQuery001Dto> queryForPage(FinBondMarketInfoQueryRequest request);

    /**
     * 债券付息信息
     *
     * @param param
     * @return
     */
    List<FinBondMarketCashPlanDto> selectWindBondSchedule(Map<String, Object> param);
}
