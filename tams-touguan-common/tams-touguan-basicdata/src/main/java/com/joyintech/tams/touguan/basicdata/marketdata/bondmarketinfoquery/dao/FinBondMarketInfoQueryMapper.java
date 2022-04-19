package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao;

import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery004Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQueryPayHSrcSecSchedulDto;
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
    List<FinBondMarketInfoQuery004Dto> queryForPage(FinBondMarketInfoQueryRequest request);

    /**
     * 平台日
     *
     * @param params{ iplatformDate:平台日
     *                }
     * @return
     */
    FinBondMarketInfoQuery004Dto getBoundMarketInfo(Map<String, Object> params);

    /**
     * 债券付息信息
     *
     * @param param
     * @return
     */
    List<FinBondMarketInfoQueryPayHSrcSecSchedulDto> selectWindBondSchedule(Map<String, Object> param);
}
