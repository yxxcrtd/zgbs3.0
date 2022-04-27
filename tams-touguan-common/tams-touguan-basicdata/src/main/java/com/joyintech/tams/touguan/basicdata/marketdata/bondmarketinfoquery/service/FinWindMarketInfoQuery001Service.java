package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinWindMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinWindMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 待上市债券市场信息查询服务类
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinWindMarketInfoQuery001Service
 * @since 1.0
 */
@Service
public class FinWindMarketInfoQuery001Service extends BaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FinWindMarketInfoQueryMapper finWindMarketInfoQueryMapper;
    @Resource
    private IPlatformDate iPlatformDate;

    /**
     * 待上市债券市场信息分页查询
     *
     * @param request
     * @return
     */
    public Page<FinWindMarketInfoQuery001Dto> queryForPage(FinBondMarketInfoQueryRequest request) {
        logger.info("queryForPage >> 待上市债券市场信息分页查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<FinWindMarketInfoQuery001Dto> finWindMarketInfoQuery001Dtos = finWindMarketInfoQueryMapper.queryForPage(request);
        return new Page<>(PageInfo.of(finWindMarketInfoQuery001Dtos));
    }

    /**
     * 待上市加券市场信息查询
     *
     * @param param
     * @return
     */
    public FinWindMarketInfoQuery001Dto queryFinWindMarketForCoupons(Map<String, Object> param) {
        logger.info("queryFinBondMarketForCoupons >> 待上市加券市场信息查询参数：{}", JSON.toJSONString(param));
        param.put("platformDate", iPlatformDate.getSysDateStr());
        FinWindMarketInfoQuery001Dto finWindMarketInfoQuery001Dto = finWindMarketInfoQueryMapper.queryFinWindMarketForCoupons(param);
        return finWindMarketInfoQuery001Dto;
    }

}
