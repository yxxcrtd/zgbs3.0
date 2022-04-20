package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinBondMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 债券市场信息查询服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinBondMarketInfoQuery001Service
 * @since 1.0
 */
@Service
public class FinBondMarketInfoQuery001Service extends BaseService {

    @Resource
    private FinBondMarketInfoQueryMapper finBondMarketInfoQueryMapper;
    @Resource
    private IPlatformDate iPlatformDate;

    /**
     * 债券市场信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinBondMarketInfoQuery001Dto> queryForPage(FinBondMarketInfoQueryRequest request) {
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<FinBondMarketInfoQuery001Dto> bondMarketInfoQuery004Dtos = finBondMarketInfoQueryMapper.queryForPage(request);
        return new Page<>(PageInfo.of(bondMarketInfoQuery004Dtos));
    }

}
