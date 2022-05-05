package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dao;

import com.joyintech.tams.common.base.entity.FinProductTypeConfig;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto.FinListedBondInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.req.FinListedBondInfoQueryRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinListedBondInfoQueryMapper {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    List<FinListedBondInfoQuery001Dto> queryForPage(FinListedBondInfoQueryRequest request);

    /**
     * 债券市场信息查询
     *
     * @param request
     * @return
     */
    FinListedBondInfoQuery001Dto queryFinWindMarketForCoupons(FinListedBondInfoQueryRequest request);

    /**
     * 根据行内三级分类获取wind分类
     *
     * @param param
     * @return
     */
    FinProductTypeConfig getWindType(FinProductTypeConfig param);
}
