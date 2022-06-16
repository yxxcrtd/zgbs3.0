package com.joyintech.tams.exchange.service.trade.processor.invest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.OthCodeBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.request.DeviationDegreeRequest;
import com.joyintech.tams.exchange.common.bean.request.EtrDispatcherInfoRequest;
import com.joyintech.tams.exchange.common.bean.request.TradeInfoProRequest;
import com.joyintech.tams.exchange.common.bean.response.TradeInfoProResponse;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.request.BidPriceConInfoRequest;
import com.joyintech.tams.exchange.common.response.BidPriceConInfoResponse;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.EntrustProp.*;
import static com.joyintech.tams.exchange.common.constant.ErrorCode.REQ_PARAMETER_ERROR;
import static com.joyintech.tams.exchange.common.constant.OthCodeType.*;

/**
 * 投管接口
 */
@SuppressWarnings("rawtypes")
@Component
public class InvestInterfaceHelper extends BaseService {
    private static final String DECLARATION_TYPE = "othCodeType";
    private static final String FINPROD_ID = "finprodId";
    private static final String TRADE_CALENDAR_TYPE = "tradeCalendarType";
    private static final String TRADE_DAY_FLAG = "tradeDayFlag";
    private static final String TRADE_DATE = "tradeDate";
    private static final String CDATE = "CDATE";
    private static final String NDAY = "NDay";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";

    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(InvestInterfaceHelper.class);
    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;
    @Autowired(required = false)
    @Qualifier("loadBalancedRestTemplate")
    protected JoyinRestTemplate restTemplate;
    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    public List<TradeInfoProResponse> getCounterDealerTraderInfo(TradeInfoProRequest dto) {
        dto.setIsOur(null);
        List<TradeInfoProResponse> list = getDealerTraderInfo(dto);

        return list.stream().filter(o -> StringUtil.isEmpty(o.getIsOur()) || StringUtil.equals(o.getIsOur(), "N")).collect(Collectors.toList());
    }

    public List<TradeInfoProResponse> getDealerTraderInfo(TradeInfoProRequest request) {
        List<TradeInfoProResponse> list = ListUtil.list(false);
        if (ObjectUtil.isNull(request)) {
            return list;
        }

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getDealerTraderInfo;
        return postForList(url, BeanUtil.object2Map(request), TradeInfoProResponse.class);
    }

    /**
     * SPPI检查-债券现券买入指令提交校验时对于使权不重大的购入价条件判断
     */
    public ResultBean getBidPriceConInfo(BidPriceConInfoRequest request) {
        if (ObjectUtil.isNull(request)) {
            return ResultBean.error(REQ_PARAMETER_ERROR);
        }

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getBidPriceConInfo;
        BidPriceConInfoResponse result = postForObject(url, BeanUtil.object2Map(request), BidPriceConInfoResponse.class);
        if (ObjectUtil.isNull(result)) {
            return ResultBean.error(ErrorCode.API_ERROR);
        }

        return ResultBean.success(result);
    }

    /**
     * 查询合约数据
     *
     * @param map 查询入参
     * @return 合约数据
     */
    public List<Instrument> getStockInfo(Map<String, Object> map) {

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectStockInfo;

        ResultBean result = postForObject(url, map, ResultBean.class);
        if (ObjectUtil.isNull(result) || !ErrorCode.isSuccess(result.getErrorCode())) {
            return ListUtil.empty();
        }
        return JSONArray.parseArray(JSONArray.toJSONString(result.getData()), Instrument.class);
    }

    /**
     * 查询最小价差数据
     *
     * @return 最小价差数据
     */
    public List<MinPrice> getMinPrice() {

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinPrice;

        ResultBean result = postForObject(url, MapUtil.newHashMap(), ResultBean.class);
        if (ObjectUtil.isNull(result) || !ErrorCode.isSuccess(result.getErrorCode())) {
            return ListUtil.empty();
        }
        return JSONArray.parseArray(JSONArray.toJSONString(result.getData()), MinPrice.class);
    }

    /**
     * 查询交易属性
     *
     * @return 交易属性
     */
    public List<MinTrdUnitLimit> getMinTrdUnitLimit() {

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.getMinTrdUnitLimit;

        ResultBean result = postForObject(url, MapUtil.newHashMap(), ResultBean.class);
        if (ObjectUtil.isNull(result) || !ErrorCode.isSuccess(result.getErrorCode())) {
            return ListUtil.empty();
        }
        return JSONArray.parseArray(JSONArray.toJSONString(result.getData()), MinTrdUnitLimit.class);
    }

    /**
     * 查询指令信息
     *
     * @param etrDispatcherInfoRequest 指令信息入参对象
     * @return 指令信息
     */
    public Map<String, Object> getEtrDispatcherInfo(EtrDispatcherInfoRequest etrDispatcherInfoRequest) {
        if (ObjectUtil.isNull(etrDispatcherInfoRequest)) {
            return null;
        }

        String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getEtrDispatcherInfo + etrDispatcherInfoRequest.getBusiTypePosition();
        String result = postForObject(url, BeanUtil.object2Map(etrDispatcherInfoRequest), String.class);
        if (StrUtil.isEmpty(result)) {
            return null;
        }
        Message<String> message = JSONObject.parseObject(result, new TypeReference<Message<String>>() {
        });
        if (StrUtil.isEmpty(message.getData())) {
            return null;
        }
        return JSONObject.parseObject(message.getData(), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 查询偏离度
     *
     * @param deviationDegreeRequest 偏离度入参对象
     * @return 偏离度
     */
    @SuppressWarnings("all")
    public String getDeviationDegree(DeviationDegreeRequest deviationDegreeRequest) {
        if (ObjectUtil.isNull(deviationDegreeRequest)) {
            return null;
        }

        String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getDeviationDegree;
        String result = postForObject(url, BeanUtil.object2Map(deviationDegreeRequest), String.class);
        if (StrUtil.isEmpty(result)) {
            return null;
        }
        Message<String> message = JSONObject.parseObject(result, new TypeReference<Message<String>>() {
        });
        return message.getData();
    }


    /**
     * 查询第N个交易日
     *
     * @param tradeCalendarType 交易日类型        3-上交所 4-深交所
     * @param cDate             日期              格式：yyyyMMdd，如20200512
     * @param nDay              第N个交易日       可以传入正数、负数、0
     * @return 第N个交易日
     */
    public String getTradeDate(String tradeCalendarType, String cDate, int nDay) {
        if (StrUtil.isEmpty(tradeCalendarType) || StrUtil.isEmpty(cDate)) {
            return null;
        }

        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryTradeDate;
        MapBuilder<String, Object> builder = MapUtil.builder();
        Map<String, Object> map = builder.put(TRADE_CALENDAR_TYPE, tradeCalendarType)
                .put(CDATE, cDate)
                .put(NDAY, nDay)
                .build();

        String result = postForObject(url, map, String.class);
        if (StrUtil.isEmpty(result)) {
            return null;
        }
        return JSON.parseObject(result).getString(TRADE_DATE);
    }

    /**
     * 查询输入日期是否交易日
     *
     * @param tradeCalendarType 交易日历类型  3-上交所; 4-深交所
     * @param cDate             日期 格式：yyyyMMdd，如20200512
     * @return 是否交易日 0-是；1-否
     */
    public String checkTradeDate(String tradeCalendarType, String cDate) {
        if (StrUtil.isEmpty(tradeCalendarType) || StrUtil.isEmpty(cDate)) {
            return null;
        }

        MapBuilder<String, Object> builder = MapUtil.builder();
        Map<String, Object> map = builder.put(TRADE_CALENDAR_TYPE, tradeCalendarType)
                .put(CDATE, cDate)
                .build();
        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.checkTradeDate;

        String result = postForObject(url, map, String.class);
        if (StrUtil.isEmpty(result)) {
            return null;
        }
        return JSON.parseObject(result).getString(TRADE_DAY_FLAG);
    }

    /**
     * 查询带交易市场标识后缀的申报代码
     *
     * @param finprodId   资产代码
     * @param entrustProp 委托属性
     * @return 申报代码
     */
    public String queryDeclarationCodeWithMarketSuffix(String finprodId, String entrustProp, String finprodMarketId) {
        String declarationCode = queryDeclarationCode(finprodId, entrustProp);
        if (StrUtil.isEmpty(declarationCode) || StrUtil.isEmpty(finprodMarketId)) {
            return StrUtil.EMPTY;
        }
        return StockCodeUtils.formatStockCode(declarationCode.trim(), StockCodeUtils.getMarket(finprodMarketId));

    }

    /**
     * 查询申报代码
     *
     * @param finprodId   资产代码
     * @param entrustProp 委托属性
     * @return 申报代码
     */
    public String queryDeclarationCode(String finprodId, String entrustProp) {
        String declarationType = convertEntrustProp2DeclarationType(entrustProp);
        if (StrUtil.isEmpty(finprodId) || StrUtil.isEmpty(declarationType)) {
            return StrUtil.EMPTY;
        }

        MapBuilder<String, Object> builder = MapUtil.builder();
        Map<String, Object> map = builder.put(FINPROD_ID, finprodId)
                .put(DECLARATION_TYPE, declarationType)
                .build();
        String url = ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectForInterf;

        List<OthCodeBean> list = postForList(url, map, OthCodeBean.class);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0).getOthCode();
        }
        return StrUtil.EMPTY;
    }

    private String convertEntrustProp2DeclarationType(String entrustProp) {
        if (StrUtil.isEmpty(entrustProp)) {
            return StrUtil.EMPTY;
        }
        String declarationType = StrUtil.EMPTY;
        if (HS.value.equals(entrustProp)) {
            declarationType = HSDM.value;
        } else if (ZYCRK.value.equals(entrustProp)) {
            declarationType = ZYDM.value;
        } else if (CHANGE_TO_STOCK.value.equals(entrustProp)) {
            declarationType = KZZZGDM.value;
        } else if (PS.value.equals(entrustProp)) {
            declarationType = PZDM.value;
        } else if (ALLOTMENT.value.equals(entrustProp)) {
            declarationType = PGDM.value;
        }
        return declarationType;
    }

    private <T> T postForObject(String url, Map<String, Object> paramMap, Class<T> beanClass) {
        T t;
        try {
            if (isSingle) {
                t = simpleRestTemplate.postForObject(url, paramMap, beanClass);
            } else {
                t = restTemplate.postForObject(url, paramMap, beanClass);
            }
            logger.debug("调用投管接口成功 => 【url = {}】, 【paramMap = {}】, 【result = {}】", url, paramMap, t);
            return t;
        } catch (Exception e) {
            logger.error("调用投管接口失败 => 【url = {}】, 【paramMap = {}】, 【exception = {}】", url, paramMap, ExceptionUtil.getMessage(e));
            return null;
        }
    }

    private <T> List<T> postForList(String url, Map<String, Object> paramMap, Class<T> beanClass) {
        List<T> list = ListUtil.empty();
        try {
            logger.debug("调用投管接口 => 【url = {}】, 【paramMap = {}】, 【beanClass = {}】", url, paramMap, beanClass);
            if (isSingle) {
                list.addAll(simpleRestTemplate.postForList(url, paramMap, beanClass));
            } else {
                list.addAll(restTemplate.postForList(url, paramMap, beanClass));
            }
            logger.debug("调用投管接口成功 => 【url = {}】, 【paramMap = {}】, 【result = {}】", url, paramMap, JSONUtil.toJsonStr(list));
            return list;
        } catch (Exception e) {
            logger.error("调用投管接口失败 => 【url = {}】, 【paramMap = {}】, 【exception = {}】", url, paramMap, ExceptionUtil.getMessage(e));
            return ListUtil.empty();
        }
    }

}
