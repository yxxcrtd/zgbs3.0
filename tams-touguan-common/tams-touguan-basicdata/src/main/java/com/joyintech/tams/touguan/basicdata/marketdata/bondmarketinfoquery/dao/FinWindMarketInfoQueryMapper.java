package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao;

import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinWindMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinWindMarketInfoQueryMapper {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    List<FinWindMarketInfoQuery001Dto> queryForPage(FinBondMarketInfoQueryRequest request);
}
