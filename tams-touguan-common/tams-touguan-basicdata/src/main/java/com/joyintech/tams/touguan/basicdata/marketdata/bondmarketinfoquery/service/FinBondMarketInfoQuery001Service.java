package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dao.FinBondMarketInfoQueryMapper;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketCashPlanDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondPayHSrcSecOptionDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    private CommonAutoCouponService commonAutoCouponService;
    @Resource
    private FinBondMarketInfoQueryMapper finBondMarketInfoQueryMapper;
    @Resource
    private FinProductMapper finProductMapper;
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
        request.setPlatformDate(iPlatformDate.getSysDateStr());
        //查询债券市场信息
        List<FinBondMarketInfoQuery001Dto> bondMarketInfoQuery004Dtos = finBondMarketInfoQueryMapper.queryForPage(request);
        if (CollectionUtils.isEmpty(bondMarketInfoQuery004Dtos)) {
            logger.info("detailInfo >> 债券市场详情信息查询为空! secId:【{}】", secId);
            return null;
        }
        FinBondMarketInfoDetailQueryDto detailQueryDto = new FinBondMarketInfoDetailQueryDto();
        FinBondMarketInfoQuery001Dto finBondMarketInfoQuery001Dto = bondMarketInfoQuery004Dtos.get(0);
        BeanUtil.copyPropertiesIgnoreNull(detailQueryDto, finBondMarketInfoQuery001Dto);
        //付息计划信息
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("secId", secId);
        List<FinBondMarketCashPlanDto> finBondMarketCashPlanDtos = finBondMarketInfoQueryMapper.queryFinBondMarketCashPlan(map);
        detailQueryDto.setBondMarketCashPlanDtos(finBondMarketCashPlanDtos);
        //行权计划信息
        List<FinBondPayHSrcSecOptionDto> finBondPayHSrcSecOptionDtos = finBondMarketInfoQueryMapper.queryFinBondPayHSrcSecOption(map);
        detailQueryDto.setBondPayHSrcSecOptionDtos(finBondPayHSrcSecOptionDtos);
        return detailQueryDto;
    }

    /**
     * 加券
     *
     * @param secId
     * @return
     */
    public List<ValidMessage> addCoupons(String secId) {
        List<ValidMessage> validMessages = new ArrayList<>();
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(secId);
        if (finProduct != null) {
            validMessages.add(new ValidMessage("该债券信息在系统中已存在，无法加券！"));
            return validMessages;
        }
        /****************自动加券****start****************/
        //自动加券错误信息
        List<String> errorInfoList = new ArrayList<>();
        //调用债券自动加券方法
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = new InterfPayhsrcsecinfoDto();
        List<String> secIdList = new ArrayList<>();
        secIdList.add(secId);
        interfPayhsrcsecinfoDto.setSecidList(secIdList);
        interfPayhsrcsecinfoDto = commonAutoCouponService.autoBondCoupon(interfPayhsrcsecinfoDto);
        if (null != interfPayhsrcsecinfoDto && null != interfPayhsrcsecinfoDto.getErrorBondList()) {
            errorInfoList.addAll(interfPayhsrcsecinfoDto.getErrorBondList());
        }
        /****************自动加券****end****************/
        if (CollectionUtils.isNotEmpty(errorInfoList)) {
            validMessages = errorInfoList.stream().map(errorInfo -> new ValidMessage(errorInfo)).collect(Collectors.toList());
        }
        return validMessages;
    }


}
