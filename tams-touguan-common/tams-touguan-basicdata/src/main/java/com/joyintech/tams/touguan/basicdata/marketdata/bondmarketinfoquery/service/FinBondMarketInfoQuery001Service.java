package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinBondMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        logger.info("queryForPage >> 债券市场信息分页查询参数：{}", JSON.toJSONString(request));
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        PageHelper.startPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize());
        List<FinBondMarketInfoQuery001Dto> bondMarketInfoQuery004Dtos = finBondMarketInfoQueryMapper.queryForPage(request);
        return new Page<>(PageInfo.of(bondMarketInfoQuery004Dtos));
    }

    /**
     * 债券市场详情信息查询
     *
     * @param secId
     * @return
     */
    public FinBondMarketInfoDetailQueryDto detailInfo(String secId) {
        logger.info("detailInfo >> 债券市场详情信息查询参数：secId：【{}】", secId);
        FinBondMarketInfoQueryRequest request = new FinBondMarketInfoQueryRequest();
        request.setSecId(secId);
        //查询债券市场信息
        List<FinBondMarketInfoQuery001Dto> bondMarketInfoQuery004Dtos = finBondMarketInfoQueryMapper.queryForPage(request);
        if (CollectionUtils.isEmpty(bondMarketInfoQuery004Dtos)) {
            logger.info("detailInfo >> 债券市场详情信息查询为空! secId:【{}】", secId);
            return null;
        }
        FinBondMarketInfoDetailQueryDto detailQueryDto = new FinBondMarketInfoDetailQueryDto();
        FinBondMarketInfoQuery001Dto finBondMarketInfoQuery001Dto = bondMarketInfoQuery004Dtos.get(0);
        BeanUtil.copyPropertiesIgnoreNull(detailQueryDto, finBondMarketInfoQuery001Dto);

        return detailQueryDto;
    }

}
