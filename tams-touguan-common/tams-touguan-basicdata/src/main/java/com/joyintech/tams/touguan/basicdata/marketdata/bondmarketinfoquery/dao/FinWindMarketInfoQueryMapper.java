package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao;

import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinWindMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinWindMarketInfoQueryRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FinWindMarketInfoQueryMapper {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    List<FinWindMarketInfoQuery001Dto> queryForPage(FinWindMarketInfoQueryRequest request);

    /**
     * 债券市场信息查询
     *
     * @param params
     * @return
     */
    FinWindMarketInfoQuery001Dto queryFinWindMarketForCoupons(Map<String, Object> params);
}
