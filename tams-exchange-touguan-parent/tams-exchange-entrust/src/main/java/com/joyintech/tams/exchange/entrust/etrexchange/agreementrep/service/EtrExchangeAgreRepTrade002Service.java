/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service;


import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dao.EtrExchangeAgreRepTradeMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePositionsCommonRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeUnitCostDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationHqResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.service.EtrExchangeTradeJj001Service;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 场内交易委托 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年5月6日
 * @see EtrExchangeAgreRepTrade002Service
 * @since 1.0
 */
@Service
public class EtrExchangeAgreRepTrade002Service extends CommonExportService {

    private static final Logger logger = LoggerFactory.getLogger(EtrExchangeAgreRepTrade002Service.class);

    @Autowired
    private EtrExchangeAgreRepTradeMapper etrExchangeAgreRepTradeMapper;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;
    @Autowired
    private EtrExchangeTradeJj001Service exchangeTradeJj001Service;

    @Autowired
    private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;

    public static final String ENTRUST_RECORD_MODIFY = "modify";
    
    public static final String ENTRUST_RECORD_NEW = "new";

    public static String queryrRepurchaseQuote = "/joyin/entrust/integrated/repurchase_quote";

    public static String queryrRepurchaseQuoteNew = "/joyin/entrust/integrated/repurchaseQuoteReceive";

    public static String queryIntegratedTick = "/joyin/quote/tick";
    /**
     BPB	上海协议回购成交申报		    正回购
     BPC	上海协议回购到期续做申报		到期续做
     BPD	上海协议回购解除质押		    接触质押
     BPE	上海协议回购换券申报		    中途换券
     BPF	上海协议回购提前终止申报		提前购回
     */
    private static final String SB_SHXYHGCJ = "BPB";
    private static final String SB_SHXYHGDQXZ = "BPC";
    private static final String SB_SHXYHGJCZY = "BPD";
    private static final String SB_SHXYHGHJSB = "BPE";
    private static final String SB_SHXYHGTQZZSB = "BPF";
    private static final String SB_SZXYHGCSJY = "BPN";
    private static final String SB_SZXYHGGH = "BPR";
    private static final String SB_SZXYHGDQXZ = "BPT";

    /**
     * 持仓信息查询
     *
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrExchangeAgreRepTrade002Dto> findPositionsPage(EtrExchangeTradePositionsCommonRequest request) {
        Map<String,Object> pageParam = new HashMap<>();
        String platDate = iPlatformDate.getSysDateStr();
        if (StringUtil.isNotEmpty(request.getPortfolioIdList())) {
            pageParam.put("portfolioIdList",request.getPortfolioIdList());
        }
        if (StringUtil.isNotEmpty(request.getFinprodMarketId())) {
            pageParam.put("finprodMarketId",request.getFinprodMarketId());
        }
        if(StringUtil.isNotEmpty(request.getFinprodMarketIds())){
            pageParam.put("finprodMarketIds",StringUtil.splitToList(GlobalConstant.COMMA, request.getFinprodMarketIds()));
        }else{
            pageParam.put("finprodMarketIds", null);
        }
        if (StringUtil.isNotEmpty(request.getTradeMarket())) {
            pageParam.put("tradeMarket",request.getTradeMarket());
        }
        if (request.getPageMode() != null) {
            pageParam.put("pageMode", request.getPageMode());
        }

        pageParam.put("portfolioName", request.getPortfolioName());
        pageParam.put("finprodName", request.getFinprodName());
        pageParam.put("search",request.getSearch());
        if (SecurityUtil.getLoginUser() != null) {
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }

        pageParam.put("sysDate", iPlatformDate.getSysDate());
        pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
        pageParam.put("VALUATION_SOURCE_CSD", BussinessDictCodeList.估值来源_中证登.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        pageParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        pageParam.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
        pageParam.put("PS_T03", BussinessDictCodeList.交易方向_行权回售.getValue());
        pageParam.put("PS_T05", BussinessDictCodeList.交易方向_申购确认.getValue());
        pageParam.put("PS_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
        pageParam.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        pageParam.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        pageParam.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        pageParam.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        pageParam.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        pageParam.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        pageParam.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        //Page<EtrExchangeAgreRepTrade002Dto> result = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeAgreRepTradeMapper.findPositionsPage(pageParam));
        Page<EtrExchangeAgreRepTrade002Dto> result=new Page<>();
        List<EtrExchangeAgreRepTrade002Dto> list=etrExchangeAgreRepTradeMapper.findPositionsPage(pageParam);
        List<EtrExchangeAgreRepTrade002Dto> returnList=new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(EtrExchangeAgreRepTrade002Dto entity : list){
                //可用数量
                PositionCashSecDto secDto=new PositionCashSecDto();
                BeanUtil.copyPropertiesIgnoreNull(secDto,entity);
                secDto.setTradePlat(TempConstant.AGR_EREP_ETR); //临时变量
                secDto.setHoldingType(DictCodeListExt3.资产类型_质押式协议回购本方券非净额结算可用持仓.getValue());
                BigDecimal canRepoNum=etrTradeCommonervice.selectAvailableNum(secDto);
                entity.setSellNum(canRepoNum);
                //过滤掉实际持仓和可用持仓都等于0
                if(entity.getPositionsNum().compareTo(BigDecimal.ZERO)==0&&canRepoNum.compareTo(BigDecimal.ZERO)==0){
                    continue;
                }
                //最新价从行情取
                EtrExchangeTradeQuotationHqResponse hqResponse=exchangeTradeJj001Service.findQuotations(entity.getFinprodMarketId());
                if(hqResponse!=null){
                    BigDecimal lastPrice=hqResponse.getLastPrice()!=null?hqResponse.getLastPrice().setScale(2,BigDecimal.ROUND_HALF_UP):new BigDecimal(0.00);
                    entity.setPrice(lastPrice);
                }
                returnList.add(entity);
            }

        }
        // 根据查询结果查询估值、交易数据，用来加工单位成本等字段
        final List pList = new ArrayList<String>();
        final List fList = new ArrayList<String>();
        if(!StringUtil.isEmptyList(returnList)) {
            returnList.stream().forEach(c -> {
                if (!pList.contains(c.getPortfolioId())) {
                    pList.add(c.getPortfolioId());
                }
                if (!fList.contains(c.getFinprodId())) {
                    fList.add(c.getFinprodId());
                }
            });
        }
        result.setRoot(returnList);
        //return result;
         //查询单位成本数据
        Map<String, EtrExchangeUnitCostDto> unitCostMap = etrExchangeTradeCommon002Service.calcEachCostAmtMap(DateUtil.parse(platDate), pList, fList, "");
         //计算单位成本、计算持仓成本、最新净价、持仓市值、浮动盈亏
        return calculationAmount(unitCostMap, result, platDate);
    }

    public Page<EtrExchangeAgreRepTrade002Dto> calculationAmount(Map<String, EtrExchangeUnitCostDto> unitCostMap, Page<EtrExchangeAgreRepTrade002Dto> pages, String platDate) {
        if(pages != null && pages.getRoot()!= null && !pages.getRoot().isEmpty()){
            pages.getRoot().forEach( r->{
                EtrExchangeUnitCostDto dto = unitCostMap.get(r.getPortfolioId() + r.getFinprodId());
                if (dto != null){
                    // 1.持仓成本（元）
                    r.setPositionsAmt(dto.getBuyCostAmt());
                    // 2.持仓成本价（元） 取单位成本
                    //r.setUnitCost(dto.getUnitCost());
                }
                // 最新净价*组合持仓.数量 - 持仓成本 as 浮动盈亏
                BigDecimal floatFd = ArithmeticUtil.multiply(r.getPrice(),r.getPositionsNum());
                floatFd = ArithmeticUtil.subtract(floatFd,r.getPositionsAmt());
                r.setFloatingPL(floatFd);
                BigDecimal floatPL =ArithmeticUtil.divide(floatFd,ArithmeticUtil.multiply(r.getPrice(),r.getPositionsNum()),2);
                r.setFloatingPLPercent(floatPL);

            });
        }
        return pages;
    }

    /**
     * 在途回购查询
     *
     * @param request int
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrExchangeOnWayRep002Dto> findOnWayRepPage(EtrExchangeOnWayRepRequestDto request) {
        Map<String,Object> pageParam = new HashMap<>();
        pageParam.put("search", request.getSearch());
        if (SecurityUtil.getLoginUser() != null) {
            pageParam.put("userId", SecurityUtil.getLoginUser().getUserName());
        }

        pageParam.put("portfolioIdList", request.getPortfolioIdList());
        pageParam.put("tradeMarket", request.getTradeMarket());
        pageParam.put("tradeId", request.getTradeId());
        pageParam.put("repPs", request.getRepPs());
        pageParam.put("zyqList", request.getZyj());
        pageParam.put("sysdate", iPlatformDate.getSysDate());
        pageParam.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        pageParam.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        pageParam.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        pageParam.put("UNIT_01", DictCodeListExt3.交易单位_张.getValue());
        pageParam.put("UNIT_04", DictCodeListExt3.交易单位_手.getValue());
        pageParam.put("pageMode", request.getPageMode());

        Page<EtrExchangeOnWayRep002Dto> result = super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeAgreRepTradeMapper.findOnWayRepPage(pageParam));

//        if(result != null && !StringUtil.isEmptyList(result.getRoot())){
//            Date sysDate = iPlatformDate.getSysDate();
//            for(EtrExchangeOnWayRep002Dto entity : result.getRoot()){
//                // 计算剩余天数
//                // 剩余天数
//                entity.setLeftDay(DateUtil.getBetweenDayNumber(sysDate, entity.getExpireSettleDate()));
//            }
//        }

        return result;
    }

    private String mathEntrustProp(String prop){
        switch (prop) {
            case SB_SHXYHGCJ:
                return DictCodeListExt3.交易方向_正回购.getValue();
            case SB_SHXYHGDQXZ:
                return DictCodeListExt3.交易方向_到期续做.getValue();
            case SB_SHXYHGJCZY:
                return DictCodeListExt3.交易方向_解除质押.getValue();
            case SB_SHXYHGHJSB:
                return DictCodeListExt3.交易方向_中途换券.getValue();
            case SB_SHXYHGTQZZSB:
                return DictCodeListExt3.交易方向_提前购回.getValue();
            default:
                return prop;
        }
    }

    private String mathEntrustPropName(String prop){
        switch (prop) {
            case SB_SHXYHGCJ:
                return DictCodeListExt3.交易方向_正回购.getCodeName();
            case SB_SHXYHGDQXZ:
                return DictCodeListExt3.交易方向_到期续做.getCodeName();
            case SB_SHXYHGJCZY:
                return DictCodeListExt3.交易方向_解除质押.getCodeName();
            case SB_SHXYHGHJSB:
                return DictCodeListExt3.交易方向_中途换券.getCodeName();
            case SB_SHXYHGTQZZSB:
                return DictCodeListExt3.交易方向_提前购回.getCodeName();
            default:
                return prop;
        }
    }

    /**
     * 调用非公开行情接口
     * @param request
     * @return
     */
    public Page<EtrExchangeUnOpenQuota002Dto> findUnOpenQuotaPage(EtrExchangeUnOpenQuotaRequestDto request) {
        Page<EtrExchangeUnOpenQuota002Dto> page = new Page<>();
        List<EtrExchangeUnOpenQuota002Dto> list = new ArrayList<>();
        List<Map<String, Object>> repurchaseQuotes = new ArrayList<>();
        //接行情数据  start
        String param = "?action_in=1";
        //actionIn   0：公开行情查询 1:非公开行情查询
        // entrustProp 非必填
        Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryrRepurchaseQuote + param, Map.class);
        if (quoteMap != null && quoteMap.get("data") != null) {
            List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
            if (!StringUtil.isEmptyList(quoteList)) {
                repurchaseQuotes.addAll(quoteList);
            }
        }
        //接行情数据 end

        //关键字筛选
        if(StringUtil.isNotEmpty(request.getSearch())){
            //请求编号
            repurchaseQuotes = repurchaseQuotes
                    .stream()
                    .filter(item -> StringUtil.contains(
                            (item.get("cbpconferId") == null ? "" : item.get("cbpconferId").toString() + '|')
                                    + (item.get("entrustProp") == null ? "" : mathEntrustProp(item.get("entrustProp").toString()) + '|')
                                    + (item.get("entrustProp") == null ? "" : mathEntrustPropName(item.get("entrustProp").toString()) + '|')
                                    + (item.get("bprsrcStatus") == null ? "" : item.get("bprsrcStatus").toString() + '|')
                                    + (item.get("stockCode") == null ? "" : item.get("stockCode").toString() + '|')
                                    + (item.get("stockName") == null ? "" : item.get("stockName").toString() + '|')
                                    + (item.get("firstExchdate") == null ? "" : item.get("firstExchdate").toString() + '|')
                                    + (item.get("repurchaseDate") == null ? "" : item.get("repurchaseDate").toString() + '|')
                                    + (item.get("settleEndDate") == null ? "" : item.get("settleEndDate").toString() + '|')
                                    + (item.get("oldImpawnCode") == null ? "" : item.get("oldImpawnCode").toString() + '|')
                                    + (item.get("origCbpconferId") == null ? "" : item.get("origCbpconferId").toString() + '|')
                                    + (item.get("oppoTraderId") == null ? "" : item.get("oppoTraderId").toString() + '|')
                                    + (item.get("oppoAgency") == null ? "" : item.get("oppoAgency").toString() + '|')
                                    + (item.get("traderId") == null ? "" : item.get("traderId").toString() + '|')
                                    + (item.get("remark") == null ? "" : item.get("remark").toString() + '|')
                            , request.getSearch()))
                    .collect(Collectors.toList());
        }

        //条件筛选
        if(StringUtil.isNotEmpty(request.getRequestId())){
            repurchaseQuotes = repurchaseQuotes
                    .stream()
                    .filter(item -> StringUtil.contains(item.get("cbpconferId") == null ? null : item.get("cbpconferId").toString(), request.getRequestId()))
                    .collect(Collectors.toList());
        }
        if(StringUtil.isNotEmpty(request.getBussType())){
             repurchaseQuotes = repurchaseQuotes
                    .stream()
                    .filter(item -> StringUtil.equals(item.get("entrustProp") == null ? null : mathEntrustProp(item.get("entrustProp").toString()), request.getBussType()))
                    .collect(Collectors.toList());
        }
//        if(StringUtil.isNotEmpty(request.getFinprodName())){
//            repurchaseQuotes = repurchaseQuotes
//                    .stream()
//                    .filter(item -> StringUtil.equals(item.get("stockCode") == null ? null : item.get("stockCode").toString(), request.getFinprodName()))
//                    .collect(Collectors.toList());
//        }
        if (!StringUtil.isEmptyList(request.getZyj())) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("finprodIdList", request.getZyj());
            List<String> finprodMarketIdList = etrExchangeAgreRepTradeMapper.selectFinprodMarketIdData(paramMap);
            if (!StringUtil.isEmptyList(finprodMarketIdList)) {
                repurchaseQuotes = repurchaseQuotes
                        .stream()
                        .filter(item -> finprodMarketIdList.contains(item.get("stockCode") == null ? "" : item.get("stockCode").toString()))
                        .collect(Collectors.toList());
            }
        }

        //去重
        HashSet<String> hSet = new HashSet<>();
        List<Map<String, Object>> newRepurchaseQuotes = new ArrayList<>();
        List<String> stockCodes = new ArrayList<>();
        List<String> origPledgeList = new ArrayList<>();
        repurchaseQuotes.forEach(temp->{
            if(StringUtil.isNotEmpty(temp.get("cbpconferId"))&&hSet.add(temp.get("cbpconferId").toString())){
                newRepurchaseQuotes.add(temp);
                // 质押券代码
                if (StringUtil.isNotEmpty(temp.get("stockCode"))) {
                    stockCodes.add(temp.get("stockCode").toString());
                }
                // 原质押券代码
                if (StringUtil.isNotEmpty(temp.get("oldImpawnCode"))) {
                    origPledgeList.add(temp.get("oldImpawnCode").toString());
                }
            }
        });
        hSet.clear();repurchaseQuotes.clear();
        List<Map<String,Object>> faceValues = new ArrayList<>();
        //封装dto
        //处理标的和面值
        Map<String,Object> tempPara = new HashMap<String, Object>();
        tempPara.put("finprodMarketIdList",stockCodes);
        tempPara.put("tradeDate",DateUtil.getSysDate());
        if (stockCodes.size() > 0) {
            faceValues.addAll(restTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFaceValueList", tempPara,
                    List.class));
        }
        final Map<String, Object> faceValueMap =
                StringUtil.isEmptyList(faceValues) ? new HashMap<>() : faceValues.stream().collect(JoyinCollectors.toMap(x -> x.get("finprodMarketId").toString(), x -> x.get("faceValue")));

        // 原质押券资产信息
        List<FinProduct> origPledgeFinProductList = null;
        if (!StringUtil.isEmptyList(origPledgeList)) {
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodMarketIdList", origPledgeList);
            origPledgeFinProductList = etrExchangeAgreRepTradeMapper.selectFinProductData(paramMap);
        }
        final Map<String, String> origPledgeFinProductMap = StringUtil.isEmptyList(origPledgeFinProductList) ? new HashMap<>() :
                origPledgeFinProductList.stream().collect(JoyinCollectors.toMap(x -> x.getFinprodMarketId(), x -> x.getFinprodAbbr()));

        newRepurchaseQuotes.forEach(tmp -> {
            EtrExchangeUnOpenQuota002Dto dto = new EtrExchangeUnOpenQuota002Dto();
            //资金账号
            ///dto.setAcctNo(model.getId());
//            if(StringUtil.isNotEmpty(tmp.get("account_id").toString())){
//                dto.setAcctNo(tmp.get("account_id").toString());
//            }
            // 请求号
            if (StringUtil.isNotEmpty(tmp.get("cbpconferId"))) {
                dto.setReqOnId(tmp.get("cbpconferId").toString());
            }
            // 请求类型
            /**
             BPB	上海协议回购成交申报		    正回购
             BPC	上海协议回购到期续做申报		到期续做
             BPD	上海协议回购解除质押		    接触质押
             BPE	上海协议回购换券申报		    中途换券
             BPF	上海协议回购提前终止申报		提前购回
             */
            String entrustProp = tmp.get("entrustProp") == null ? null : tmp.get("entrustProp").toString();
            if (StringUtil.isNotEmpty(entrustProp)) {
                //BPB	上海协议回购成交申报		    正回购
                if (StringUtil.equals(SB_SHXYHGCJ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_正回购.getValue());
                }
                //BPC	上海协议回购到期续做申报		到期续做
                else if (StringUtil.equals(SB_SHXYHGDQXZ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_到期续做.getValue());
                }
                //BPD	上海协议回购解除质押		    接触质押
                else if (StringUtil.equals(SB_SHXYHGJCZY, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_解除质押.getValue());
                }
                //BPE	上海协议回购换券申报		    中途换券
                else if (StringUtil.equals(SB_SHXYHGHJSB, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_中途换券.getValue());
                }
                //BPF	上海协议回购提前终止申报		提前购回
                else if (StringUtil.equals(SB_SHXYHGTQZZSB, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_提前购回.getValue());
                }
                //BPF	深圳债券质押协议回购初始交易		正回购
                else if (StringUtil.equals(SB_SZXYHGCSJY, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_正回购.getValue());
                }
                //BPN	深圳债券质押协议回购购回交易		到期购回
                else if (StringUtil.equals(SB_SZXYHGGH, entrustProp)) {
                    // 回购到期日
                    if (StringUtil.isNotEmpty(tmp.get("repurchaseDate"))) {
                        String mdate = DateUtil.format(DateUtil.parse(tmp.get("repurchaseDate").toString()));
                        if (DateUtil.compareDate(mdate, DateUtil.getSysDate()) > 0) {
                            dto.setReqType(DictCodeListExt3.交易方向_提前购回.getValue());
                        } else {
                            dto.setReqType(DictCodeListExt3.交易方向_到期购回.getValue());
                        }
                    } else {
                        dto.setReqType(DictCodeListExt3.交易方向_到期购回.getValue());
                    }
                }
                //BPF	深圳债券质押协议回购到期续做		到期续做
                else if (StringUtil.equals(SB_SZXYHGDQXZ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_到期续做.getValue());
                }
                else{
                    return;
                }
            }
            // 协议回购质权人类型
            if (StringUtil.isNotEmpty(tmp.get("bprsrcStatus"))) {
                dto.setRedoType(tmp.get("bprsrcStatus").toString());
            }
            // 质押券代码
            if (StringUtil.isNotEmpty(tmp.get("stockCode"))) {
                dto.setRepSecId(tmp.get("stockCode").toString());

                if (StringUtil.endsWith(tmp.get("stockCode").toString(), "SH")) {
                    dto.setTradeMarket(DictCodeListExt1.交易场所_上海证券交易所.getValue());
                } else {
                    dto.setTradeMarket(DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                }
            }
            // 质押券名称
            if (StringUtil.isNotEmpty(tmp.get("stockName"))) {
                dto.setPledgeStampName(tmp.get("stockName").toString());
            }
            // 原质押代码  忽略 【属于 重复字段】
            // 质押数量
            if (StringUtil.isNotEmpty(tmp.get("impawnAmount"))) {
                dto.setRepSecNum(BigDecimal.valueOf(Double.valueOf(tmp.get("impawnAmount").toString())));
            }
            // 质押券面总额合计 = 质押数量 * 最新面值 [FinProductCommonBatchService.getFaceValue(String finprodId, Date happenDate)]
            if (StringUtil.isNotEmpty(dto.getRepSecNum())) {
//                faceValues.forEach(item ->{
//                    if(StringUtil.isNotEmpty(tmp.get("stockCode"))&&StringUtil.isNotEmpty(item.get("finprodMarketId"))&&StringUtil.isNotEmpty(item.get("faceValue"))
//                            &&StringUtil.equals(tmp.get("stockCode").toString(),item.get("finprodMarketId").toString())){
//                        dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(item.get("faceValue").toString())));
//                    }
//                });
                if (faceValueMap.containsKey(tmp.get("stockCode").toString())) {
                    dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(faceValueMap.get(tmp.get("stockCode").toString()).toString())));
                } else {
                    dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(100)));
                }
            }
            // 折算比例
            if (StringUtil.isNotEmpty(tmp.get("funderRatio"))) {
                dto.setConRatio(BigDecimal.valueOf(Double.valueOf(tmp.get("funderRatio").toString())));
            }
            // 回购金额 = 质押券面总额合计*折算比例
            if (StringUtil.isNotEmpty(tmp.get("impawnBalance"))) {
                dto.setTradeAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("impawnBalance").toString())));
            }
            else if (StringUtil.isNotEmpty(dto.getPledgeStampBalance()) && StringUtil.isNotEmpty(dto.getConRatio())) {
                dto.setTradeAmt(dto.getPledgeStampBalance().multiply(dto.getConRatio()).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
            // 回购利率
            if (StringUtil.isNotEmpty(tmp.get("expireYearRate"))) {
                dto.setYield(BigDecimal.valueOf(Double.valueOf(tmp.get("expireYearRate").toString())));
            }
            // 回购期限
            if (StringUtil.isNotEmpty(tmp.get("repoTerm"))) {
                dto.setTerm(Integer.valueOf(tmp.get("repoTerm").toString()));
            }
            // 实际占款天数
            if (StringUtil.isNotEmpty(tmp.get("fundUsedDays"))) {
                dto.setOccupyDays(Integer.valueOf(tmp.get("fundUsedDays").toString()));
            }
            // 首次结算日
            if (StringUtil.isNotEmpty(tmp.get("firstExchdate"))) {
                dto.setFristSettleDate(DateUtil.format(DateUtil.parse(tmp.get("firstExchdate").toString())));
            }
            // 回购到期日
            if (StringUtil.isNotEmpty(tmp.get("repurchaseDate"))) {
                dto.setBackDate(DateUtil.format(DateUtil.parse(tmp.get("repurchaseDate").toString())));
            }
            // 到期结算日
            if (StringUtil.isNotEmpty(tmp.get("settleEndDate"))) {
                dto.setExpireSettleDate(DateUtil.format(DateUtil.parse(tmp.get("settleEndDate").toString())));
            }
            // 原质押券代码
            if (StringUtil.isNotEmpty(tmp.get("oldImpawnCode"))) {
                dto.setOrigPledgeStampCode(tmp.get("oldImpawnCode").toString());
                // 原质押券名称
                if (origPledgeFinProductMap.containsKey(tmp.get("oldImpawnCode").toString())) {
                    dto.setOrigPledgeStampName(origPledgeFinProductMap.get(tmp.get("oldImpawnCode").toString()));
                }
            }
            // 回购利息
            if (StringUtil.isNotEmpty(tmp.get("profit"))) {
                dto.setIntAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("profit").toString())));
            }
            // 到期结算金额
            if (StringUtil.isNotEmpty(tmp.get("settleBalance"))) {
                dto.setExpireSettleAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("settleBalance").toString())));
            }
            // 成交编号
            if (StringUtil.isNotEmpty(tmp.get("origCbpconferId"))) {
                dto.setTradeId(tmp.get("origCbpconferId").toString());
            }
            // 发起方交易员代码
            if (StringUtil.isNotEmpty(tmp.get("oppoTraderId"))) {
                dto.setSponsorTraderCode(tmp.get("oppoTraderId").toString());
            }
            // 发起方交易商代码
            if (StringUtil.isNotEmpty(tmp.get("oppoAgency"))) {
                dto.setSponsorDealerCode(tmp.get("oppoAgency").toString());
            }
            // 本方交易员代码
            if (StringUtil.isNotEmpty(tmp.get("traderId"))) {
                dto.setTraderCode(tmp.get("traderId").toString());
            }
            // 补充协议
            if (StringUtil.isNotEmpty(tmp.get("remark"))) {
                dto.setAddCla(tmp.get("remark").toString());
            }

            // 回购利率和折算率应该乘以100
            if (dto.getYield() != null) {
                dto.setYield(dto.getYield());
            }
            if (dto.getConRatio() != null) {
                dto.setConRatio(ArithmeticUtil.multiply(dto.getConRatio(), BigDecimal.valueOf(100)));
            }

            list.add(dto);
        });

        page.setRoot(list);
        return page;
    }

    public Page<EtrExchangeAgreRepTrade003Dto> selectEtrEntrust(EtrExchangeAgreRepTrade003RequestDto request){
        Map<String,Object> param = new HashMap<>();
        param.put("entrustStatus", request.getEntrustStatus());
        param.put("search",request.getKeywords());
        param.put("entrustId", request.getEntrustId());
        param.put("sysDate",iPlatformDate.getSysDateStr());
        if (SecurityUtil.getLoginUser() != null) {
            param.put("userId", SecurityUtil.getLoginUser().getUserName());
        }
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("UNIT_01", DictCodeListExt3.交易单位_张.getValue());
        param.put("UNIT_04", DictCodeListExt3.交易单位_手.getValue());
        param.put("portfolioName", request.getPortfolioName());
        param.put("tradeMarket", request.getTradeMarket());
        param.put("finprodId", request.getFinprodId());
        param.put("bussType", request.getBussType());
        param.put("manager", request.getManager());
        param.put("startDate", request.getStartDate());
        param.put("endDate", request.getEndDate());
        param.put("pageMode", request.getPageMode());
        param.put("portfolioIdList", request.getPortfolioIdList());
        param.put("zyj", request.getZyj());
        param.put("search_isNumberFlag", BaseMap.isNumeric(request.getKeywords()));
        Page<EtrExchangeAgreRepTrade003Dto> page =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(), () -> etrExchangeAgreRepTradeMapper.selectEtrEntrust(param));

        List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
                param, SysUser.class);
        for(EtrExchangeAgreRepTrade003Dto dto:page.getRoot()){
            if(StringUtil.isNotEmpty(dto.getNextUser())&&StringUtil.isNotEmpty(userList)){
                String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
                        .map(SysUser::getRealName)
                        .collect(Collectors.joining(",","",""));
                dto.setNextUser(sysUsers);
            }
        }
        return page;
    }

    /**
     * 调用非公开行情接口
     * @param request
     * @return
     */
    public Page<EtrExchangeUnOpenQuota002Dto> findUnOpenQuotaPageNew(EtrExchangeUnOpenQuotaRequestDto request) {
        Page<EtrExchangeUnOpenQuota002Dto> page = new Page<>();
        List<EtrExchangeUnOpenQuota002Dto> list = new ArrayList<>();
        List<Map<String, Object>> repurchaseQuotes = new ArrayList<>();
        //接行情数据  start
        String param = "?action_in=1";
        //actionIn   0：公开行情查询 1:非公开行情查询
        // entrustProp 非必填
        Map quoteMap = restTemplate.postForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryrRepurchaseQuoteNew,request, Map.class);
        if (quoteMap != null && quoteMap.get("data") != null) {
            Map dataMap=(Map)quoteMap.get("data");
            if (dataMap != null && dataMap.get("root") != null) {
                List<Map<String, Object>> quoteList = (List<Map<String, Object>>) dataMap.get("root");
                if (!StringUtil.isEmptyList(quoteList)) {
                    repurchaseQuotes.addAll(quoteList);
                }
            }
        }
        //接行情数据 end


        //去重
        HashSet<String> hSet = new HashSet<>();
        List<Map<String, Object>> newRepurchaseQuotes = new ArrayList<>();
        List<String> stockCodes = new ArrayList<>();
        List<String> origPledgeList = new ArrayList<>();
        repurchaseQuotes.forEach(temp->{
            if(StringUtil.isNotEmpty(temp.get("requestNo"))&&hSet.add(temp.get("requestNo").toString())){
                newRepurchaseQuotes.add(temp);
                // 质押券代码
                if (StringUtil.isNotEmpty(temp.get("stockCode"))) {
                    stockCodes.add(temp.get("stockCode").toString());
                }
                // 原质押券代码
                if (StringUtil.isNotEmpty(temp.get("origBonds"))) {
                    origPledgeList.add(temp.get("origBonds").toString());
                }
            }
        });
        hSet.clear();repurchaseQuotes.clear();
        List<Map<String,Object>> faceValues = new ArrayList<>();
        //封装dto
        //处理标的和面值
        Map<String,Object> tempPara = new HashMap<String, Object>();
        tempPara.put("finprodMarketIdList",stockCodes);
        tempPara.put("tradeDate",DateUtil.getSysDate());
        if (stockCodes.size() > 0) {
            faceValues.addAll(restTemplate.postForObject(
                    ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFaceValueList", tempPara,
                    List.class));
        }
        final Map<String, Object> faceValueMap =
                StringUtil.isEmptyList(faceValues) ? new HashMap<>() : faceValues.stream().collect(JoyinCollectors.toMap(x -> x.get("finprodMarketId").toString(), x -> x.get("faceValue")));

        // 原质押券资产信息
        List<FinProduct> origPledgeFinProductList = null;
        if (!StringUtil.isEmptyList(origPledgeList)) {
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodMarketIdList", origPledgeList);
            origPledgeFinProductList = etrExchangeAgreRepTradeMapper.selectFinProductData(paramMap);
        }
        final Map<String, String> origPledgeFinProductMap = StringUtil.isEmptyList(origPledgeFinProductList) ? new HashMap<>() :
                origPledgeFinProductList.stream().collect(JoyinCollectors.toMap(x -> x.getFinprodMarketId(), x -> x.getFinprodAbbr()));

        newRepurchaseQuotes.forEach(tmp -> {
            EtrExchangeUnOpenQuota002Dto dto = new EtrExchangeUnOpenQuota002Dto();
            //资金账号
            ///dto.setAcctNo(model.getId());
//            if(StringUtil.isNotEmpty(tmp.get("account_id").toString())){
//                dto.setAcctNo(tmp.get("account_id").toString());
//            }
            // 请求号
            if (StringUtil.isNotEmpty(tmp.get("requestNo"))) {
                dto.setReqOnId(tmp.get("requestNo").toString());
            }
            // 请求类型
            /**
             BPB	上海协议回购成交申报		    正回购
             BPC	上海协议回购到期续做申报		到期续做
             BPD	上海协议回购解除质押		    接触质押
             BPE	上海协议回购换券申报		    中途换券
             BPF	上海协议回购提前终止申报		提前购回
             */
            String entrustProp = tmp.get("entrustProp") == null ? null : tmp.get("entrustProp").toString();
            if (StringUtil.isNotEmpty(entrustProp)) {
                //BPB	上海协议回购成交申报		    正回购
                if (StringUtil.equals(SB_SHXYHGCJ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_正回购.getValue());
                }
                //BPC	上海协议回购到期续做申报		到期续做
                else if (StringUtil.equals(SB_SHXYHGDQXZ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_到期续做.getValue());
                }
                //BPD	上海协议回购解除质押		    接触质押
                else if (StringUtil.equals(SB_SHXYHGJCZY, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_解除质押.getValue());
                }
                //BPE	上海协议回购换券申报		    中途换券
                else if (StringUtil.equals(SB_SHXYHGHJSB, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_中途换券.getValue());
                }
                //BPF	上海协议回购提前终止申报		提前购回
                else if (StringUtil.equals(SB_SHXYHGTQZZSB, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_提前购回.getValue());
                }
                //BPF	深圳债券质押协议回购初始交易		正回购
                else if (StringUtil.equals(SB_SZXYHGCSJY, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_正回购.getValue());
                }
                //BPN	深圳债券质押协议回购购回交易		到期购回
                else if (StringUtil.equals(SB_SZXYHGGH, entrustProp)) {
                    // 回购到期日
                    if (StringUtil.isNotEmpty(tmp.get("repurchaseDate"))) {
                        String mdate = DateUtil.format(DateUtil.parse(tmp.get("repurchaseDate").toString()));
                        if (DateUtil.compareDate(mdate, DateUtil.getSysDate()) > 0) {
                            dto.setReqType(DictCodeListExt3.交易方向_提前购回.getValue());
                        } else {
                            dto.setReqType(DictCodeListExt3.交易方向_到期购回.getValue());
                        }
                    } else {
                        dto.setReqType(DictCodeListExt3.交易方向_到期购回.getValue());
                    }
                }
                //BPF	深圳债券质押协议回购到期续做		到期续做
                else if (StringUtil.equals(SB_SZXYHGDQXZ, entrustProp)) {
                    dto.setReqType(DictCodeListExt3.交易方向_到期续做.getValue());
                }
                else{
                    return;
                }
            }
            // 协议回购质权人类型
            if (StringUtil.isNotEmpty(tmp.get("requestType"))) {
                dto.setReqType(tmp.get("requestType").toString());
            }
            // 交易场所
            if (StringUtil.isNotEmpty(tmp.get("tradeMarket"))) {
                dto.setRepSecId(tmp.get("tradeMarket").toString());
            }
            // 质押券代码
            if (StringUtil.isNotEmpty(tmp.get("stockCode"))) {
                dto.setRepSecId(tmp.get("stockCode").toString());
            }
            // 质押券名称
            if (StringUtil.isNotEmpty(tmp.get("stockName"))) {
                dto.setPledgeStampName(tmp.get("stockName").toString());
            }
            // 质押数量
            if (StringUtil.isNotEmpty(tmp.get("pledgeNumber"))) {
                dto.setRepSecNum(BigDecimal.valueOf(Double.valueOf(tmp.get("pledgeNumber").toString())));
            }
            // 质押券面总额合计 = 质押数量 * 最新面值 [FinProductCommonBatchService.getFaceValue(String finprodId, Date happenDate)]
            if (StringUtil.isNotEmpty(dto.getRepSecNum())) {
//                faceValues.forEach(item ->{
//                    if(StringUtil.isNotEmpty(tmp.get("stockCode"))&&StringUtil.isNotEmpty(item.get("finprodMarketId"))&&StringUtil.isNotEmpty(item.get("faceValue"))
//                            &&StringUtil.equals(tmp.get("stockCode").toString(),item.get("finprodMarketId").toString())){
//                        dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(item.get("faceValue").toString())));
//                    }
//                });
                if (faceValueMap.containsKey(tmp.get("stockCode").toString())) {
                    dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(faceValueMap.get(tmp.get("stockCode").toString()).toString())));
                } else {
                    dto.setPledgeStampBalance(dto.getRepSecNum().multiply(new BigDecimal(100)));
                }
            }
            // 折算比例
            if (StringUtil.isNotEmpty(tmp.get("discount"))) {
                dto.setConRatio(BigDecimal.valueOf(Double.valueOf(tmp.get("discount").toString())));
            }
            // 回购金额 = 质押券面总额合计*折算比例
            if (StringUtil.isNotEmpty(tmp.get("repoAmount"))) {
                dto.setTradeAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("repoAmount").toString())));
            }
            // 回购利率
            if (StringUtil.isNotEmpty(tmp.get("repoRate"))) {
                dto.setYield(BigDecimal.valueOf(Double.valueOf(tmp.get("repoRate").toString())));
            }
            // 回购期限
            if (StringUtil.isNotEmpty(tmp.get("compactTerm"))) {
                dto.setTerm(Integer.valueOf(tmp.get("compactTerm").toString()));
            }
            // 实际占款天数
            if (StringUtil.isNotEmpty(tmp.get("capDays"))) {
                dto.setOccupyDays(Integer.valueOf(tmp.get("capDays").toString()));
            }
            // 首次结算日
            if (StringUtil.isNotEmpty(tmp.get("firstSettleDate"))&&DateUtil.isDateStr(tmp.get("firstSettleDate").toString())) {
                dto.setFristSettleDate(DateUtil.format(DateUtil.parse(tmp.get("firstSettleDate").toString())));
            }
            // 回购到期日
            if (StringUtil.isNotEmpty(tmp.get("repoMdate"))&&DateUtil.isDateStr(tmp.get("repoMdate").toString())) {
                dto.setBackDate(DateUtil.format(DateUtil.parse(tmp.get("repoMdate").toString())));
            }
            // 到期结算日
            if (StringUtil.isNotEmpty(tmp.get("backDate"))&&DateUtil.isDateStr(tmp.get("backDate").toString())) {
                dto.setExpireSettleDate(DateUtil.format(DateUtil.parse(tmp.get("backDate").toString())));
            }
            // 原质押券代码
            if (StringUtil.isNotEmpty(tmp.get("origBonds"))) {
                dto.setOrigPledgeStampCode(tmp.get("origBonds").toString());
                // 原质押券名称
                if (origPledgeFinProductMap.containsKey(tmp.get("origBonds").toString())) {
                    dto.setOrigPledgeStampName(tmp.get("origBondsName").toString());
                }
            }
            // 回购利息
            if (StringUtil.isNotEmpty(tmp.get("mIntAmt"))) {
                dto.setIntAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("mIntAmt").toString())));
            }
            // 到期结算金额
            if (StringUtil.isNotEmpty(tmp.get("backBalance"))) {
                dto.setExpireSettleAmt(BigDecimal.valueOf(Double.valueOf(tmp.get("backBalance").toString())));
            }
            // 成交编号
            if (StringUtil.isNotEmpty(tmp.get("origSerialNo"))) {
                dto.setTradeId(tmp.get("origSerialNo").toString());
            }
            // 本方交易员
            if (StringUtil.isNotEmpty(tmp.get("traderCode"))) {
                dto.setCpTraderCode(tmp.get("traderCode").toString());
            }
            // 本方交易商代码
            if (StringUtil.isNotEmpty(tmp.get("memberId"))) {
                dto.setMemberId(tmp.get("memberId").toString());
            }
            // 本方交易主体（深交所展示，否则为空）
            if (StringUtil.isNotEmpty(tmp.get("investorId"))) {
                dto.setCpInvestorId(tmp.get("investorId").toString());
            }
            // 本方交易主体类型（深交所展示，否则为空）
            if (StringUtil.isNotEmpty(tmp.get("investorType"))) {
                dto.setCpInvestorType(tmp.get("investorType").toString());
            }
            //对手方交易员
            if (StringUtil.isNotEmpty(tmp.get("cpTraderCode"))) {
                dto.setCpTraderCode(tmp.get("cpTraderCode").toString());
            }
            //对手方交易商
            if (StringUtil.isNotEmpty(tmp.get("cpMemberId"))) {
                dto.setCpMemberId(tmp.get("cpMemberId").toString());
            }
            //对手方交易商
            if (StringUtil.isNotEmpty(tmp.get("cpInvestorId"))) {
                dto.setCpInvestorId(tmp.get("cpInvestorId").toString());
            }
            //对手方交易商
            if (StringUtil.isNotEmpty(tmp.get("cpInvestorType"))) {
                dto.setCpInvestorType(tmp.get("cpInvestorType").toString());
            }
            //对手方交易商
            if (StringUtil.isNotEmpty(tmp.get("cpClientName"))) {
                dto.setCpClientName(tmp.get("cpClientName").toString());
            }
            // 补充协议
            if (StringUtil.isNotEmpty(tmp.get("remark"))) {
                dto.setAddCla(tmp.get("remark").toString());
            }

            // 回购利率和折算率应该乘以100
            if (dto.getYield() != null) {
                dto.setYield(dto.getYield());
            }
            if (dto.getConRatio() != null) {
                dto.setConRatio(ArithmeticUtil.multiply(dto.getConRatio(), BigDecimal.valueOf(100)));
            }

            list.add(dto);
        });

        page.setRoot(list);
        return page;
    }

}
