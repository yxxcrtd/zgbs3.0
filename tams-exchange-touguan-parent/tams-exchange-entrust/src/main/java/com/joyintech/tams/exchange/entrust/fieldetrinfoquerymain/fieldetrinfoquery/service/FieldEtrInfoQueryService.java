/*
 * 文件名：FieldEtrInfoQueryService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：msk
 * 修改时间：2021年12月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dao.FieldEtrInfoQueryMapper;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.EntrustQueryBean;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrBondFirstExeListInfoResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrInfoQueryListRequest;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrInfoQueryListResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FinProdResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.TradeQueryBean;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 场内指令查询
 *
 * @author sjj
 * @version 1.0 2021年11月26日
 * @see FieldEtrInfoQueryService
 * @since 1.0
 */
@Service("FieldEtrInfoQueryService")
public class FieldEtrInfoQueryService extends BaseService {

    @Autowired
    private FieldEtrInfoQueryMapper fieldEtrInfoQueryMapper;
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 指令列表分页查询
     *
     * @param dto 入数
     * @return Page<FieldEtrInfoQueryListResponse>
     */
    public Page<FieldEtrInfoQueryListResponse> findAllData(FieldEtrInfoQueryListRequest dto) {

        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("entrustAwait", DictCodeList.委托状态_待接收.getValue());
        pageParam.put("entrustDistribute", DictCodeListExt3.委托状态_待分发.getValue());
        pageParam.put("entrustReceive", DictCodeList.委托状态_已接收.getValue());
        pageParam.put("entrustUntread", DictCodeList.委托状态_已完成.getValue());
        pageParam.put("entrustClose", DictCodeList.委托状态_已关闭.getValue());
        pageParam.put("entrustReturn", DictCodeList.委托状态_已退回.getValue());
        pageParam.put("entrustRecall", DictCodeList.委托状态_已撤回.getValue());
        pageParam.put("entrustInwfl", DictCodeListExt3.委托状态_流程中.getValue());

        pageParam.put("busiTypePosition02", DictCodeListExt3.头寸业务类型_现券交易.getValue());
        pageParam.put("busiTypePosition03", DictCodeListExt3.头寸业务类型_债券行权交易.getValue());

        List<String> listTemp = Arrays.asList(dto.getEntrustStatus());
        if (!listTemp.contains("all")) {
            pageParam.put("entrustStatusList", listTemp);
        }
        String portfolioId = (String) pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(pageParam.get("portfolioId"))) {
            List<String> portfolioIdList = StringUtil.splitToList(",", (String) pageParam.get("portfolioId"));
            pageParam.put("portfolioIdList", portfolioIdList);
        }

        String finprodMarketId = (String) pageParam.get("finprodMarketId");
        if (StringUtil.isNotEmpty(pageParam.get("finprodMarketId"))) {
            List<String> finprodMarketIdList = StringUtil.splitToList(",", (String) pageParam.get("finprodMarketId"));
            pageParam.put("finprodMarketIdList", finprodMarketIdList);
        }

        String secAcctId = (String) pageParam.get("secAcctId");
        if (StringUtil.isNotEmpty(pageParam.get("secAcctId"))) {
            List<String> secAcctIdList = StringUtil.splitToList(",", (String) pageParam.get("secAcctId"));
            pageParam.put("secAcctIdList", secAcctIdList);
        }

        String invManagerId = (String) pageParam.get("invManagerId");
        if (StringUtil.isNotEmpty(pageParam.get("invManagerId"))) {
            List<String> invManagerIdList = StringUtil.splitToList(",", (String) pageParam.get("invManagerId"));
            pageParam.put("invManagerIdList", invManagerIdList);
        }

        String finprodType2Old = (String) pageParam.get("finprodType2");
        if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(pageParam.get("ps"))) {
            if (StringUtil.isNotEmpty((String) pageParam.get("ps"))) {
                pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
            }
        }
        pageParam.put("ASSET_TYPE_07", DictCodeList.平安资产类型_拆借.getValue());
        pageParam.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        pageParam.put("ASSET_TYPE_10", DictCodeList.平安资产类型_买断式回购.getValue());
        pageParam.put("ASSET_TYPE_12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        pageParam.put("ASSET_TYPE_16", DictCodeListExt2.平安资产类型_利率互换.getValue());
        pageParam.put("BACK_LEND_DIRECTION", DictCode.回购拆借方向.getValue());
        pageParam.put("PS", DictCode.交易方向.getValue());
        pageParam.put("effectFlag", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("effectFlag04", DictCodeListExt1.有效状态_流程中.getValue());
        pageParam.put("finType_zq", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("ps_49", DictCodeListExt3.交易方向_债转股.getValue());
        pageParam.put("search_isNumberFlag", BaseMap.isNumeric(dto.getSearch()));

        pageParam.put("PRICE_CLASS_01", DictCodeListExt3.计价方式_净价.getValue());
        pageParam.put("PRICE_CLASS_02", DictCodeListExt3.计价方式_全价.getValue());
        pageParam.put("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());


        if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_0.getValue())) {
            pageParam.put("settleSpeed", "0");
        } else if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_1.getValue())) {
            pageParam.put("settleSpeed", "1");
        } else if (StringUtil.equals(dto.getSettleSpeed(),
                DictCodeList.清算速度_T_2.getValue())) {
            pageParam.put("settleSpeed", "2");
        }
        pageParam.put("sort", dto.getPageMode().getSort());
        pageParam.put("direction", dto.getPageMode().getDirection());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        pageParam.put("year", year);

        //价格模式
        if (StringUtils.isNotEmpty((String) pageParam.get("search"))) {
            List<String> declaTypeList = new ArrayList<>();
            List<? extends DataDictItem> list = SysDataUtil.getDataDictItem(DictCode.价格模式.getValue());
            if (!list.isEmpty() && list.size() > 0) {
                for (DataDictItem t :
                        list) {
                    String typeValue = t.getItemValue();
                    if (typeValue.indexOf((String) pageParam.get("search")) != -1) {
                        declaTypeList.add(t.getItemCode());
                    }
                }
                pageParam.put("declaTypeList", declaTypeList);
            }
        } else {
            pageParam.put("declaTypeList", null);
        }
        //根据组合权限表添加检索权限
        Page<FieldEtrInfoQueryListResponse> pageLimit = new Page<FieldEtrInfoQueryListResponse>();
        if (StringUtil.isNotEmpty(SecurityUtil.getLoginUser())) {
            //查询登录用户是否是领导、交易处长岗位
            Map<String, Object> map = new HashMap<>();
            map.put("rolepos01", DictCodeListExt2.角色岗位_领导.getValue());
            map.put("rolepos03", DictCodeListExt2.角色岗位_交易处长.getValue());
            map.put("userName", SecurityUtil.getLoginUser().getUserName());
            int count = fieldEtrInfoQueryMapper.queryRolePosByLoginUser(map);
            if (count == 0) {
                pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
            }
            pageLimit = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                    () -> fieldEtrInfoQueryMapper.findAllData(pageParam));
        } else {
            return pageLimit;
        }

        List<FieldEtrInfoQueryListResponse> list = pageLimit.getRoot();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {

            for (FieldEtrInfoQueryListResponse response : list) {
                //一级投标债券 重新计算执行进度和成交进度
                if (StringUtil.equals(response.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue()) ||
                        StringUtil.equals(response.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
                    FieldEtrInfoQueryListResponse dto2 = fieldEtrInfoQueryMapper.getEtrFirstBidInfo(response.getEntrustId());
                    if (dto2 != null) {
                        response.setExecFinishAmt(dto2.getExecFinishAmt());
                        response.setExecRemainderAmt(dto2.getExecRemainderAmt());
                        response.setExeProgress(dto2.getExeProgress());
                        response.setTransFinishAmt(dto2.getTransFinishAmt());
                        response.setTransRemainderAmt(dto2.getTransRemainderAmt());
                        response.setDealProgress(dto2.getDealProgress());
                    }
                }
            }

        }

        pageLimit.setRoot(list);

        return pageLimit;
    }

    public Page<FinProdResponse> findFinProdMarketIdList(FieldEtrInfoQueryListRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrInfoQueryMapper.findFinProdMarketIdList(pageParam));
    }


    /**
     * 一级债券指令执行查询
     * @param dto
     * @return
     */
    public Page<FieldEtrBondFirstExeListInfoResponse> getEtrBondFirstExeListInfo(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new Page<FieldEtrBondFirstExeListInfoResponse>();
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrInfoQueryMapper.getEtrBondFirstExeListInfo(pageParam));
    }

    public Page<EntrustQueryBean> getEtrDispatcherExeListInfo(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        pageParam.put("quoteType", BussinessDictCodeList.报价方式_登记报价.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new Page<EntrustQueryBean>();
        }
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrInfoQueryMapper.getEtrDispatcherExeListInfo(pageParam));
    }

    public Page<EntrustQueryBean> getEtrDispatcherExeListInfoByExe(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("instructionId", dto.getEntrustId());
        Page<EntrustQueryBean> page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrInfoQueryMapper.queryEntrust2Touguan(pageParam));
        if (!CollectionUtils.isEmpty(page.getRoot())) {
            for (EntrustQueryBean response : page.getRoot()) {
                if (StringUtils.equals(response.getEntrustBs(), DictCodeListExt3.交易方向_质押券出库.getValue())
                        || StringUtils.equals(response.getEntrustBs(), DictCodeListExt3.交易方向_质押券入库.getValue())) {
                    response.setEntrustPrice(StringUtil.isNotEmpty(response.getEntrustPrice()) ? ArithmeticUtil.multiply(response.getEntrustPrice(), BigDecimal.valueOf(100)) : null);
                    response.setTradedPrice(StringUtil.isNotEmpty(response.getTradedPrice()) ? ArithmeticUtil.multiply(response.getTradedPrice(), BigDecimal.valueOf(100)) : null);
                }
                response.setTradingDay(StringUtil.isNotEmpty(response.getTradingDay()) ? DateUtil.format(DateUtil.parse(response.getTradingDay()), DateUtil.DEFAULT_DATE_FORMAT_EN) : null);
            }
        }
        return page;
    }


    public Page<TradeQueryBean> getEtrDispatcherDealListInfo(CommonSearchInfoRequest dto) {
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("feeType06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        pageParam.put("feeType08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        pageParam.put("TRADE_FLOW_STATUS01", DictCodeListExt1.交易流程状态_流程中.getValue());
        pageParam.put("TRADE_FLOW_STATUS02", DictCodeListExt1.交易流程状态_有效.getValue());
        pageParam.put("TRADE_FLOW_STATUS03", DictCodeListExt1.交易流程状态_撤销.getValue());
        List<String> entrustIdList = StringUtil.splitToList(",", (String) pageParam.get("entrustId"));
        if (StringUtil.isBlank(dto.getEntrustId()) || entrustIdList.size() > 1) {
            return new Page<TradeQueryBean>();
        }
        Page<TradeQueryBean>
                page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> fieldEtrInfoQueryMapper.getEtrDispatcherDealListInfo(pageParam));
        return page;
    }

    public Page<TradeQueryBean> getEtrDispatcherDealListInfoByExe(CommonSearchInfoRequest dto) {
        String busiTypePosition = dto.getBusiTypePosition();
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("instructionId", dto.getEntrustId());
        pageParam.put("sysDate", iPlatformDate.getSysDateStr());
        Page<TradeQueryBean> page = super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(),
                () -> fieldEtrInfoQueryMapper.queryTrade2Touguan(pageParam));
        if (!CollectionUtils.isEmpty(page.getRoot())) {
            for (TradeQueryBean response : page.getRoot()) {
                if (StringUtils.equals(dto.getBusiTypePosition(), DictCodeListExt3.投管业务类型_现券交易.getValue())) {
                    response.setTradedVolume(ArithmeticUtil.divide(response.getTradedVolume(), new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                }
                if (StringUtils.equals(response.getEntrustBs(), DictCodeListExt3.交易方向_质押券出库.getValue())
                        || StringUtils.equals(response.getEntrustBs(), DictCodeListExt3.交易方向_质押券入库.getValue())) {
                    response.setTradedPrice(StringUtil.isNotEmpty(response.getTradedPrice()) ? ArithmeticUtil.multiply(response.getTradedPrice(), BigDecimal.valueOf(100)) : null);
                }
                response.setTradingDay(StringUtil.isNotEmpty(response.getTradingDay()) ? DateUtil.format(DateUtil.parse(response.getTradingDay()), DateUtil.DEFAULT_DATE_FORMAT_EN) : null);
                setSuspendParams(response, busiTypePosition);
            }
        }
        return page;
    }

    private void setSuspendParams(TradeQueryBean response, String busiTypePosition) {
        if (busiTypePosition.equals(DictCodeListExt3.投管业务类型_质押券出入库.getValue())) {
            response.setTradedVolumeStr(response.getTradedVolume() + "(万元)");
            response.setTradedPriceStr("");
            response.setTradedBalanceStr("");
        }
        if (busiTypePosition.equals(DictCodeListExt3.投管业务类型_现券交易.getValue())) {
            response.setTradedVolumeStr(response.getTradedVolume() + "(万元)");
            response.setTradedPriceStr(response.getTradedPrice() + "(元)");
            response.setTradedBalanceStr(response.getClearBalance() + "(元)");
        }
        if (busiTypePosition.equals(DictCodeListExt3.投管业务类型_通用式质押回购.getValue())) {
            response.setTradedVolumeStr("");
            response.setTradedPriceStr(response.getTradedPrice() + "%");
            response.setTradedBalanceStr(ArithmeticUtil.divide(response.getClearBalance(), new BigDecimal(100), 2) + "(万元)");
        }
    }

}
