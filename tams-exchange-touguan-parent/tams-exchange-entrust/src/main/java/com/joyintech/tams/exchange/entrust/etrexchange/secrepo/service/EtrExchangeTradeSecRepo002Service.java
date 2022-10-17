/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.etrexchangetrade.dto.EtrExchangeTrade002Dto;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeUnitCostDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SelectOptionRequestDto;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationHqResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.service.EtrExchangeTradeJj001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dao.EtrExchangeTradeSecRepoMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeEtrExchangeSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeEtrExchangeSecRepoResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePositionsSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePositionsSecRepoResponse;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 交易所指令交易（现券+通用质押式回购）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeSecRepo002Service
 * @since 1.0
 */
@Service
public class EtrExchangeTradeSecRepo002Service extends BaseService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private EtrExchangeTradeSecRepoMapper etrExchangeTradeSecRepoMapper;
    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;
    @Autowired
    private EtrExchangeTradeJj001Service exchangeTradeJj001Service;
    @Autowired
    private EtrExchangeTradeSecRepo001Service etrExchangeTradeSecRepo001Service;

    /**
     * 分页查询持仓信息
     * <功能详细描述>
     *
     * @param dto 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    public Page<EtrExchangeTradePositionsSecRepoResponse> findPositionsPage(EtrExchangeTradePositionsSecRepoRequest dto) {
        String userId = SecurityUtil.getLoginUser().getUserName();
        if (StringUtils.isEmpty(userId)) {
            return new Page<>();
        }
        logger.info("请求用户【{}】", "userId = " + userId);
        try {
            // 构建持仓查询参数
            Map<String, Object> param = buildPositionSearchParam(dto);
            Page<EtrExchangeTradePositionsSecRepoResponse> result = new Page<>();
            // 查询持仓
            List<EtrExchangeTradePositionsSecRepoResponse> queryList = etrExchangeTradeSecRepoMapper.findPositionsPage(param);
            List<EtrExchangeTradePositionsSecRepoResponse> returnList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(queryList)) {
                // 填充可卖数量
                this.fillPositionsSecSellNum(queryList);
                // 填充最新价
                this.fillPositionsLastPrice(queryList);
                // 剔除持仓和可卖数量为0的数据
                for (EtrExchangeTradePositionsSecRepoResponse entity : queryList) {
                    if (entity.getPositionsNum().compareTo(BigDecimal.ZERO) == 0 && entity.getSellNum().compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }
                    returnList.add(entity);
                }
            }
            // 根据行情最新价和计价方式重新计算最新价
            this.recalculatePositionsLastPrice(returnList);
            result.setRoot(returnList);
            // 根据查询结果查询估值、交易数据，用来加工单位成本等字段
            final List pList = new ArrayList<String>();
            final List fList = new ArrayList<String>();
            if (!StringUtil.isEmptyList(returnList)) {
                result.getRoot().stream().forEach(c -> {
                    if (!pList.contains(c.getPortfolioId())) {
                        pList.add(c.getPortfolioId());
                    }
                    if (!fList.contains(c.getFinprodId())) {
                        fList.add(c.getFinprodId());
                    }
                });
            }
            // 查询单位成本数据
            String platDate = iPlatformDate.getSysDateStr();
            Map<String, EtrExchangeUnitCostDto> unitCostMap = calcEachCostAmtMap(DateUtil.parse(platDate), pList, fList, "");
            // 计算单位成本、计算持仓成本、最新净价、持仓市值、浮动盈亏
            return calculationAmount(unitCostMap, result, platDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        return null;
    }

    /**
     * 查询证券代码联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSecAcctId(SelectOptionRequestDto request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        Page<SelectOptionDto> tempList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeSecRepoMapper.getSecAcctId(param));
        return tempList;
    }

    /**
     * 查询上交所和深交所标准券数量
     *
     * @param request 页面参数
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    public Map<String, String> loadSecNum(SelectOptionRequestDto request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("secNum_sh", "0");
        resultMap.put("secNum_color_sh", "red");
        resultMap.put("secNum_sz", "0");
        resultMap.put("secNum_color_sz", "red");
        List<EtrExchangeTrade002Dto> dtoList = etrExchangeTradeSecRepoMapper.selectSecNum(pageParam);
        for (EtrExchangeTrade002Dto dto : dtoList) {
            if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sh", format.format(dto.getSecNum()));
                if (dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sh", "green");
                }
            } else if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sz", format.format(dto.getSecNum()));
                if (dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sz", "green");
                }
            }
        }
        return resultMap;
    }

    /**
     * 指令列表查询
     * @param request
     * @return
     */
    public Page<EtrExchangeTradeEtrExchangeSecRepoResponse> selectEtrExchange(EtrExchangeTradeEtrExchangeSecRepoRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        param.put("sysDate", iPlatformDate.getSysDateStr());
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("ENTRUST_BUSS_TYPE_28",DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue());
        param.put("ENTRUST_BUSS_TYPE_29",DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue());
        param.put("PRICE_MODEL_PRM03",DictCodeListExt2.价格模式_市价指令.getValue());
        param.put("rolePos05", DictCodeListExt2.角色岗位_投资经理.getValue());
        String portfolioId = (String) param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        if (CollectionUtils.isNotEmpty(request.getEntrustTypeList())) {
            param.put("entrustTypeList", request.getEntrustTypeList());
        }
        param.put("search_isNumberFlag", BaseMap.isNumeric(request.getSearch()));
        if (StringUtil.isNotEmpty((String) param.get("finprodMarketIds"))) {
            param.put("finprodMarketIds", StringUtil.splitToList(GlobalConstant.COMMA, param.get("finprodMarketIds").toString()));
        } else {
            param.put("finprodMarketIds", null);
        }
        return super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeSecRepoMapper.selectEtrExchange(param));
    }

    /**
     * 查询指令编号联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getId(SelectOptionRequestDto request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        Page<SelectOptionDto> tempList = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeTradeSecRepoMapper.getId(param));
        return tempList;
    }

    /**
     * 查询前一日持仓指标
     *
     * @param cdate           交易日期
     * @param portfolioIdList 组合代码
     * @param secType1        债券一级分类
     */
    public List<BaseMap> selectLastCostAmt(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("cdate", DateUtil.addDay(cdate, -1));

        if (portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if (finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList", finprodIdList);
        if (StringUtil.isNotEmpty(secType1)) reqMap.put("secType1", secType1);

        List<BaseMap> list = etrExchangeTradeSecRepoMapper.selectLastCostAmt(reqMap);
        /**
         * 返回结果
         * PORTFOLIO_ID、FINPROD_ID、INV_AIM、
         * BUY_COST_AMT 成本
         * SHARE_AMT    数量
         */
        return list;
    }

    /**
     * 当日交易数据查询
     *
     * @param cdate           交易日期
     * @param portfolioIdList 组合代码
     * @param secType1        债券一级分类
     */
    public List<BaseMap> selectCurrTrdData(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        BaseMap reqMap = BaseMap.newMap()
                .put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue())    // 债券
                .put("SVR_TRADE_MARKET_01", DictCodeListExt1.交易场所_银行间.getValue())           // 银行间
                .put("PS_T01", DictCodeList.交易方向_买入.getValue())
                .put("PS_T02", DictCodeList.交易方向_卖出.getValue())
                .put("PS_T03", DictCodeList.交易方向_行权_回售.getValue())
                .put("PS_T04", DictCodeList.交易方向_申购.getValue())
                .put("PS_T49", DictCodeListExt1.交易方向_债转股.getValue())
                .put("SVR_TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue())
                .put("SVR_TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue())
                .put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue())
                .put("YN_N", DictCodeList.是否_否.getValue())
                .put("cdate", cdate);

        if (portfolioIdList != null && !portfolioIdList.isEmpty()) reqMap.put("portfolioIdList", portfolioIdList);
        if (finprodIdList != null && !finprodIdList.isEmpty()) reqMap.put("finprodIdList", finprodIdList);
        if (StringUtil.isNotEmpty(secType1)) reqMap.put("secType1", secType1);

        List<BaseMap> list = etrExchangeTradeSecRepoMapper.selectCurrTrdData(reqMap);
        /**
         *  PORTFOLIO_ID、FINPROD_ID、INV_AIM、PS
         *  PRICE,  净价
         *  AMT     数量
         */
        return list;
    }

    /**
     * @return
     */
    public Map<String, EtrExchangeUnitCostDto> calcEachCostAmtMap(Date cdate, List<String> portfolioIdList, List<String> finprodIdList, String secType1) {
        /** 返回结果
         * KEY:组合+资产代码+投资目的
         * VALUE:持仓单位成本dto
         */
        Map<String, EtrExchangeUnitCostDto> resultMap = new HashMap<>();

        // 查询前一日持仓指标
        List<BaseMap> lastCostAmt = selectLastCostAmt(cdate, portfolioIdList, finprodIdList, "");

        if (lastCostAmt != null) {
            lastCostAmt.forEach(e -> {
                if (BigDecimal.ZERO.compareTo(e.getBigDecimal("SHARE_AMT")) != 0) {
                    // 数量不为0时生成单位净价
                    EtrExchangeUnitCostDto dto = new EtrExchangeUnitCostDto();

                    dto.setBuyCostAmt(e.getBigDecimal("BUY_COST_AMT"));
                    dto.setShareAmt(e.getBigDecimal("SHARE_AMT"));
                    // 单位成本=成本/数量， （EACH_COST = BUY_COST_AMT / SHARE_AMT）
                    dto.setUnitCost(ArithmeticUtil.divide(
                            e.getBigDecimal("BUY_COST_AMT"),
                            e.getBigDecimal("SHARE_AMT"),
                            4));
                    resultMap.put(getKey(e), dto);
                }
            });
        }

        // 当日交易数据查询
        List<BaseMap> currTrdData = selectCurrTrdData(cdate, portfolioIdList, finprodIdList, secType1);
        if (currTrdData != null) {
            currTrdData.forEach(i -> {
                String key = getKey(i);
                EtrExchangeUnitCostDto dto = resultMap.get(key);
                if (dto == null) {
                    dto = new EtrExchangeUnitCostDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    resultMap.put(key, dto);
                }
                if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_买入.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                } else if (StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_申购.getValue())) {
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .add(ArithmeticUtil.multiply(i.getBigDecimal("PRICE"), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                    // 数量
                    dto.setShareAmt(dto.getShareAmt().add(i.getBigDecimal("AMT")));
                    // 单位成本 = 成本/数量
                    dto.setUnitCost(ArithmeticUtil.divide(dto.getBuyCostAmt(), dto.getShareAmt(), 4));
                }

                resultMap.put(key, dto);
            });
            currTrdData.forEach(i -> {
                String key = getKey(i);
                EtrExchangeUnitCostDto dto = resultMap.get(key);
                if (dto == null) {
                    dto = new EtrExchangeUnitCostDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                    resultMap.put(key, dto);
                }
                if (!StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_买入.getValue()) &&
                        !StringUtil.equals(i.getString("PS"), DictCodeList.交易方向_申购.getValue())) { // 卖出或行权回售
                    // 成本
                    BigDecimal thisBuyCostAmt = dto.getBuyCostAmt()
                            .subtract(ArithmeticUtil.multiply(dto.getUnitCost(), i.getBigDecimal("AMT")));
                    dto.setBuyCostAmt(thisBuyCostAmt);
                }

                resultMap.put(key, dto);
            });
        }
        return resultMap;
    }

    public static String getKey(BaseMap bm) {
        return bm.getString("PORTFOLIO_ID") + bm.get("SECACCTID") + bm.get("FINPROD_ID");
    }

    private Page<EtrExchangeTradePositionsSecRepoResponse> calculationAmount(Map<String, EtrExchangeUnitCostDto> unitCostMap, Page<EtrExchangeTradePositionsSecRepoResponse> pages, String platDate) {
        if (pages != null && pages.getRoot() != null && !pages.getRoot().isEmpty()) {
            pages.getRoot().forEach(r -> {
                EtrExchangeUnitCostDto dto = unitCostMap.get(r.getPortfolioId() +r.getSecAcctId()+ r.getFinprodId());
                if (dto != null) {
                    // 1.持仓成本（元）
                    r.setPositionsAmt(dto.getBuyCostAmt());
                    // 2.持仓成本单位价（元） 取单位成本
                    r.setPositionUnitCost(dto.getUnitCost());
                }
                // 最新净价*组合持仓.数量 - 持仓成本 as 浮动盈亏
                BigDecimal floatFd = ArithmeticUtil.multiply(r.getPrice(),
                                    ArithmeticUtil.multiply(r.getPositionsNum(),new BigDecimal("100")));
                if(null != floatFd){
                    floatFd = ArithmeticUtil.subtract(floatFd, r.getPositionsAmt());
                    r.setFloating(floatFd);
//                BigDecimal floatPL = ArithmeticUtil.divide(floatFd, ArithmeticUtil.multiply(r.getPrice(),
//                                ArithmeticUtil.multiply(r.getPositionsNum(),new BigDecimal("100"))), 4).multiply(new BigDecimal("100"));
                    BigDecimal floatPL = ArithmeticUtil.divide(floatFd,r.getPositionsAmt(),4).multiply(new BigDecimal("100"));
                    r.setFloatingPL(floatPL);
                }

            });
        }
        return pages;
    }

    /**
     * 填充持仓可卖数量
     * @param positions
     */
    private void fillPositionsSecSellNum(List<EtrExchangeTradePositionsSecRepoResponse> positions){
        if(CollectionUtils.isEmpty(positions)){
            return;
        }
        List<PositionCashSecDto> positionCashSecDtos = new ArrayList<>(positions.size());
        for (EtrExchangeTradePositionsSecRepoResponse entity : positions) {
            if(StringUtil.isEmpty(entity.getFinprodId()) || StringUtil.isEmpty(entity.getSecAcctId())){
                continue;
            }
            PositionCashSecDto secDto = new PositionCashSecDto();
            BeanUtil.copyPropertiesIgnoreNull(secDto, entity);
            secDto.setAcctId(entity.getAcctNo());
            secDto.setTradePlat(DictCodeList.交易平台_新债平台.getValue());
            if (StringUtil.isEmpty(secDto.getCcy())) {
                secDto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            }
            positionCashSecDtos.add(secDto);
        }
        // 批量查询持仓可用数量
        List<PositionCashSecDto> positionResult = etrTradeCommonervice.selectAvailableNumBatch(positionCashSecDtos);
        if(CollectionUtils.isEmpty(positionResult)){
            return;
        }
        Map<String, PositionCashSecDto> map = positionResult
                .stream()
                .collect(Collectors.toMap(PositionCashSecDto::getBusiKey, position -> position));
        for(EtrExchangeTradePositionsSecRepoResponse position : positions){
            PositionCashSecDto positionCashSecDto = map.get(position.getBusiKey());
            if(null != positionCashSecDto){
                position.setSellNum(ArithmeticUtil.divide(positionCashSecDto.getAMOUNT(),new BigDecimal("100"),2));
            }else{
                position.setSellNum(BigDecimal.ZERO);
            }
        }
    }

    /**
     * 填充持仓最新价
     * @param positions
     */
    private void fillPositionsLastPrice(List<EtrExchangeTradePositionsSecRepoResponse> positions){
        if(CollectionUtils.isEmpty(positions)){
            return;
        }
        // 系统参数接行情的时候查行情
        if (!StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam("TRD_USE_QUOTE"))) {
            for(EtrExchangeTradePositionsSecRepoResponse position : positions){
                //最新价从行情取
                EtrExchangeTradeQuotationHqResponse hqResponse = exchangeTradeJj001Service.
                        findQuotations(position.getFinprodMarketId());
                if (hqResponse != null) {
                    BigDecimal lastPrice = hqResponse.getLastPrice() != null ?
                            hqResponse.getLastPrice().setScale(2, BigDecimal.ROUND_HALF_UP)
                            : BigDecimal.valueOf(0.00);
                    position.setPrice(lastPrice);
                }
            }
        }
    }

    /**
     * 重新计算持仓最新价
     * @param positions
     */
    private void recalculatePositionsLastPrice(List<EtrExchangeTradePositionsSecRepoResponse> positions){
        if(CollectionUtils.isEmpty(positions)){
            return;
        }
        //add 设置 ZGCPCN-1158 【场内债券指令】持仓栏中最新价，应根据计价方式转化成净价展示 start
        // 此方法是根据 行情最新价和计价方式 重新计算最新价，因为最新价是从行情取的，如果系统都没有接行情，则无需计算
        if (!StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam("TRD_USE_QUOTE"))) {
            positions.forEach(v->{
                if(v.getIsNetprice().equals("N") && v.getFinprodId() != null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("ps", "T02");
                    map.put("calculateField", "02");
                    map.put("finprodId", v.getFinprodId());
                    map.put("prinAmt", new BigDecimal(100));
                    map.put("fprice", v.getPrice());
                    map.put("tradeMarket", v.getTradeMarket());
                    Map<String, BigDecimal> returnMap=null;
                    try{
                        returnMap = etrExchangeTradeSecRepo001Service.calcPriceAndYield(map);
                    }catch (Exception e){
                    }
                    if(!Objects.isNull(returnMap) && !Objects.isNull(returnMap.get("cprice"))){
                        BigDecimal cPrice = v.getPrice();
                        v.setPrice(returnMap.get("cprice"));
                        v.setcPrice(cPrice);
                    }
                };
            });
        }
        //add 设置 ZGCPCN-1158 【场内债券指令】持仓栏中最新价，应根据计价方式转化成净价展示 end
    }

    /**
     * 构建持仓查询参数
     * @param dto
     * @return
     */
    private  Map<String, Object> buildPositionSearchParam(EtrExchangeTradePositionsSecRepoRequest dto){
        String platDate = iPlatformDate.getSysDateStr();
        PageMode pageMode = null;
        if (StringUtil.isNotEmpty(dto.getPageMode())
                && (dto.getPageMode().getSort().equals("floating") || dto.getPageMode().getSort().equals("floatingPL"))) {
            pageMode = dto.getPageMode();
            PageMode pageMode2 = new PageMode();
            pageMode2.setPageNo(pageMode.getPageNo());
            pageMode2.setPageSize(pageMode.getPageSize());
            pageMode2.setSort("");
            pageMode2.setDirection("");
            dto.setPageMode(pageMode2);
        }
        Map<String, Object> param = BeanUtil.object2Map(dto);
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        String portfolioId = (String) param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        param.put("sysDate", iPlatformDate.getSysDate());
        param.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        param.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        param.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
        param.put("VALUATION_SOURCE_CSD", BussinessDictCodeList.估值来源_中证登.getValue());
        param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
        param.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        param.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        param.put("type6_B14",DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        param.put("type6_B16",DictCodeList.WIND一级分类_含债券一级分类等_可分离转债存债.getValue());
//                param.put("CUSTOM_ASSET_TYPE_FOUR_013", DictCodeListExt3.行内资产四级分类_可转债.getValue()); // 可转债
        param.put("search_isNumberFlag", CommonUtil.isNumeric(dto.getSearch()));
        if (StringUtil.isNotEmpty((String) param.get("finprodMarketIds"))) {
            param.put("finprodMarketIds", StringUtil.splitToList(GlobalConstant.COMMA, param.get("finprodMarketIds").toString()));
        } else {
            param.put("finprodMarketIds", null);
        }
        param.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        param.put("NO_TRADE_BUSINESS_ETR", TempConstant.NO_TRADE_BUSINESS_ETR);
        return param;
    }
}
