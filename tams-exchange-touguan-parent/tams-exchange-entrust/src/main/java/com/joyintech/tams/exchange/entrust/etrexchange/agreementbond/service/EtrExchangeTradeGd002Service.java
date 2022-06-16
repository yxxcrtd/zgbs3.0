/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementbond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementbond.dto.EtrExchangeFixedIncomeResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeJjSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationRequest;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeGd002Service
 * @since 1.0
 */
@Service
public class EtrExchangeTradeGd002Service extends BaseService<EtrExchangeTradeJjSaveRequest> {

    public static String queryIntegratedQuote = "/joyin/entrust/integrated/quote";

    public static String queryIntegratedTick = "/joyin/quote/tick";

    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    FinProductAddMapper finProductAddMapper;

    @Autowired
    FinProductMapper finProductMapper;


    /**
     * 查询固定收益行情数据
     **/
    public Page<EtrExchangeFixedIncomeResponse> findFixedIncomePage(EtrExchangeTradeQuotationRequest request) {
        Page<EtrExchangeFixedIncomeResponse> page = new Page<EtrExchangeFixedIncomeResponse>();
        List<EtrExchangeFixedIncomeResponse> list = new ArrayList<EtrExchangeFixedIncomeResponse>();
        Map<String, Object> map = BeanUtil.object2Map(request);
        String secId = (String) map.get("secId");
        String param = "?quote_type=5";
        if (StringUtil.isNotEmpty(secId)) {
            param += "&stock_code=" + secId;
        }
        Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);
        if (quoteMap != null && quoteMap.get("data") != null) {
            List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
            if (!StringUtil.isEmptyList(quoteList)) {
                quoteList.forEach(tmp -> {
                    EtrExchangeFixedIncomeResponse dto = new EtrExchangeFixedIncomeResponse();
                    dto.setSecId((String) tmp.get("stockCode"));
                    dto.setSecName((String) tmp.get("stockName"));
                    dto.setMarket((String) tmp.get("market"));
                    //行情类型
                    //买卖方向
                    //买入
                    if (StringUtil.equals((String) tmp.get("entrustBs"), "1")) {
                        dto.setEntrustPs(DictCodeList.交易方向_买入.getValue());
                    } else if (StringUtil.equals((String) tmp.get("entrustBs"), "2")) {
                        //卖出
                        //定价申报
                        dto.setEntrustPs(DictCodeList.交易方向_卖出.getValue());
                    }
                    //数量
                    if (tmp.get("entrustVolume") != null) {
                        dto.setEntrustNum(new BigDecimal((Integer) tmp.get("entrustVolume")));
                    }
                    //价格（净价） / 回购利率
                    if (tmp.get("entrustPrice") != null) {
//                        dto.setnPrice(new BigDecimal((Double)tmp.get("entrustPrice")));
                        dto.setnPrice(ArithmeticUtil.round(BigDecimal.valueOf((Double) tmp.get("entrustPrice")), 4, null));
                    }
                    //全价
                    if (tmp.get("fullPrice") != null) {
//                        dto.setfPrice(new BigDecimal((Double)tmp.get("fullPrice")));
                        dto.setfPrice(ArithmeticUtil.round(BigDecimal.valueOf((Double) tmp.get("fullPrice")), 4, null));
                    }

                    //收益率
                    if (tmp.get("expireIncomeRate") != null) {
//                        dto.setYield(new BigDecimal((Double)tmp.get("expireIncomeRate")));
                        dto.setYield(ArithmeticUtil.round(BigDecimal.valueOf((Double) tmp.get("expireIncomeRate")), 4, null));
                    }

                    //订单编号
                    dto.setOrderId((String) tmp.get("contractId"));

                    list.add(dto);
                });
            }
        }
        page.setRoot(list);
        return page;
    }


}
