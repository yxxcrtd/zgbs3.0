package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketCashPlanDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondPayHSrcSecOptionDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.apache.ibatis.annotations.Param;
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
     * @param params
     * @return
     */
    List<FinBondMarketCashPlanDto> queryFinBondMarketCashPlan(Map<String, Object> params);

    /**
     * 债券行权信息
     *
     * @param secId
     * @return
     */
    List<FinBondPayHSrcSecOptionDto> queryFinBondPayHSrcSecOption(@Param("secId") String secId);

    /**
     * 债券信息查询（接口表）
     *
     * @param param
     * @return
     */
    FinBondMarketInfoDetailQueryDto queryFinBondMarketForCoupons(Map<String, Object> param);

    /**
     * 债券信息查询
     *
     * @param param
     * @return
     */
    FinBondMarketInfoDetailQueryDto queryFinProductForCoupons(Map<String, Object> param);

    /**
     * 债券信息下拉框查询
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getAssetId(Map<String, Object> param);

    /**
     * 查询付息计划信息
     *
     * @param map
     * @return
     */
    List<FinBondMarketCashPlanDto> selectInvScheduleList(Map<String, Object> map);

    /**
     * 行权计划信息
     *
     * @param map
     * @return
     */
    List<FinBondPayHSrcSecOptionDto> selectFinExercisePlanList(Map<String, Object> map);

}
