package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinBondMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery004Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 债券市场信息查询服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinBondMarketInfoQuery004Service
 * @since 1.0
 */
@Service
public class FinBondMarketInfoQuery004Service extends BaseService {

    @Resource
    private FinBondMarketInfoQueryMapper finTenderBoundAssetMapper;
    @Resource
    private IPlatformDate iPlatformDate;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    public Page<FinBondMarketInfoQuery004Dto> queryForPage(FinBondMarketInfoQueryRequest request) {
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<FinBondMarketInfoQuery004Dto> bondMarketInfoQuery004Dtos = finTenderBoundAssetMapper.queryForPage(request);
        return new Page<>(PageInfo.of(bondMarketInfoQuery004Dtos));
    }

}
