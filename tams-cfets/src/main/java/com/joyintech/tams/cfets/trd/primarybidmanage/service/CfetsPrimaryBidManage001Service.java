package com.joyintech.tams.cfets.trd.primarybidmanage.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.trd.primarybidmanage.dao.CfetsPrimaryBidManageMapper;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 债券标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年5月6日
 * @see CfetsPrimaryBidManage001Service
 * @since 1.0
 */
@Service
public class CfetsPrimaryBidManage001Service extends BaseService{

    @Autowired(required = false)
    private CfetsPrimaryBidManageMapper cfetsPrimaryBidManageMapper;

    /**
     * 基础--中债报送(资产)
     */
    @Autowired(required = false)
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private IPlatformDate iplatformDate;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param request request
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<CfetsPrimaryBidManageCfmResponse> selectCfmForPage(CfetsPrimaryBidManageCfmRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        if (CollectionUtils.isNotEmpty(request.getBidDate())) {
            String bidDateStart = request.getBidDate().get(0);
            if (StringUtil.isNotEmpty(bidDateStart)){
                pageParam.put("bidDateStart",bidDateStart);
            }
            String bidDateEnd = request.getBidDate().get(1);
            if (StringUtil.isNotEmpty(bidDateEnd)){
                pageParam.put("bidDateEnd",bidDateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getBidCfmDate())) {
            String bidCfmDateStart = request.getBidCfmDate().get(0);
            if (StringUtil.isNotEmpty(bidCfmDateStart)){
                pageParam.put("bidCfmDateStart",bidCfmDateStart);
            }
            String bidCfmDateEnd = request.getBidCfmDate().get(1);
            if (StringUtil.isNotEmpty(bidCfmDateEnd)){
                pageParam.put("bidCfmDateEnd",bidCfmDateEnd);
            }
        }

        pageParam.put("finprodIdList", StringUtil.splitToList(null, request.getFinprodId()));
        pageParam.put("tradeMarketList", StringUtil.splitToList(null, request.getTradeMarket()));
        pageParam.put("tenderTargetList", StringUtil.splitToList(null, request.getTenderTarget()));
        pageParam.put("winTypeList", StringUtil.splitToList(null, request.getWinType()));
        pageParam.put("payTypeList", StringUtil.splitToList(null, request.getPayType()));
        pageParam.put("counterIdList", StringUtil.splitToList(null, request.getCounterId()));
        pageParam.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
        pageParam.put("otherEffectFlag_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("COMMON_BUSINESS_TYPE_TRD97", BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
        List<String> isWinList = StringUtil.splitToList(null, request.getIsWin());
        pageParam.put("isWinList", isWinList);
        if (StringUtil.isNotEmpty(request.getPageMode().getSort()) && StringUtil.equals(request.getPageMode().getSort(),"bidDate")){
            pageParam.put("sort","bidDateStart");
        }else{
            pageParam.put("sort",request.getPageMode().getSort());
        }
        pageParam.put("direction",request.getPageMode().getDirection());
        pageParam.put("userId",SecurityUtil.getLoginUser().getUserName());
        Page<CfetsPrimaryBidManageCfmResponse> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> cfetsPrimaryBidManageMapper.selectCfmForPage(pageParam));
        return page;
    }

    /**
     * 交易对手
     *
     * <功能详细描述>
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getCounterId(SelectOptionCommonRequest request) {
        Map<String, Object> map = BeanUtil.object2Map(request);
        map.put("IS_ISSUER", DictCodeList.是否_是.getValue());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> cfetsPrimaryBidManageMapper.getCounterId(map));
        return page;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param request 检索参数
     * @return 联想文本框
     */
    public Page<SelectOptionDto> selectTrader(SelectOptionCommonRequest request) {
        Map<String, Object> map = BeanUtil.object2Map(request);
        map.put("IS_ISSUER", DictCodeList.是否_是.getValue());
        Page<SelectOptionDto> page = super.queryForPage(request.getPageMode().getPageNo(),
                request.getPageMode().getPageSize(),
                () -> cfetsPrimaryBidManageMapper.selectTrader(map));
        return page;
    }
    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param request request
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<CfetsPrimaryBidManageResponse> selectForPage(CfetsPrimaryBidManageRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        if (CollectionUtils.isNotEmpty(request.getBidDate())) {
            String bidDateStart = request.getBidDate().get(0);
            if (StringUtil.isNotEmpty(bidDateStart)){
                pageParam.put("bidDateStart",bidDateStart);
            }
            String bidDateEnd = request.getBidDate().get(1);
            if (StringUtil.isNotEmpty(bidDateEnd)){
                pageParam.put("bidDateEnd",bidDateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getWinDate())) {
            String winDateStart = request.getWinDate().get(0);
            if (StringUtil.isNotEmpty(winDateStart)){
                pageParam.put("winDateStart",winDateStart);
            }
            String winDateEnd = request.getWinDate().get(1);
            if (StringUtil.isNotEmpty(winDateEnd)){
                pageParam.put("winDateEnd",winDateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getPayDate())) {
            String payDateStart = request.getPayDate().get(0);
            if (StringUtil.isNotEmpty(payDateStart)){
                pageParam.put("payDateStart",payDateStart);
            }
            String payDateEnd = request.getPayDate().get(1);
            if (StringUtil.isNotEmpty(payDateEnd)){
                pageParam.put("payDateEnd",payDateEnd);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getListingDate())) {
            String listingDateStart = request.getListingDate().get(0);
            if (StringUtil.isNotEmpty(listingDateStart)){
                pageParam.put("listingDateStart",listingDateStart);
            }
            String listingDateEnd = request.getListingDate().get(1);
            if (StringUtil.isNotEmpty(listingDateEnd)){
                pageParam.put("listingDateEnd",listingDateEnd);
            }
        }
        pageParam.put("finprodIdList", StringUtil.splitToList(null, request.getFinprodId()));
        pageParam.put("tradeMarketList", StringUtil.splitToList(null, request.getTradeMarket()));
        pageParam.put("tenderTargetList", StringUtil.splitToList(null, request.getTenderTarget()));
        pageParam.put("winTypeList", StringUtil.splitToList(null, request.getWinType()));
        pageParam.put("payTypeList", StringUtil.splitToList(null, request.getPayType()));
        pageParam.put("bidTypeList", StringUtil.splitToList(null, request.getBidType()));
        pageParam.put("isWinList", StringUtil.splitToList(null, request.getIsWin()));
        pageParam.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
        pageParam.put("sort",request.getPageMode().getSort());
        pageParam.put("direction",request.getPageMode().getDirection());
        Page<CfetsPrimaryBidManageResponse> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> cfetsPrimaryBidManageMapper.selectForPage(pageParam));
        return page;
    }

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param request request
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<CfetsPrimaryBidManageResponse> selectDetailForPage(CfetsPrimaryBidManageRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        Page<CfetsPrimaryBidManageResponse> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> cfetsPrimaryBidManageMapper.selectDetailForPage(pageParam));
        return page;
    }

}
