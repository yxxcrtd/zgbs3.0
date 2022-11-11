package com.joyintech.tams.invest.trade.transfer.common.service;

import com.google.common.collect.Maps;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.tams.invest.trade.transfer.common.dto.TransferCommon001Dto;
import com.joyintech.tams.invest.trade.transfer.common.dto.TransferInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TransferCommon001Service
 */
@Service
public class TransferCommon001Service extends BaseService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TransferCommon001Service.class);

    /**
     * 查询待转仓组合信息
     *
     * @param pageParam 参数
     * @return list
     */
    public List<TransferCommon001Dto> findAvaPortfolioInfo(Map<String, Object> pageParam) {
        List<TransferCommon001Dto> resultList = simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/trade/transfer/common/TransferCommon/findAvaPortfolioInfo", pageParam, TransferCommon001Dto.class);
        //画面已转仓列表
        List<TransferInfoDto> listInfoList = new ArrayList<>();
        try {
            if (StringUtil.isNotEmpty(pageParam.get("listInfoList"))) {
                listInfoList = JsonUtils.json2List((String) pageParam.get("listInfoList"), TransferInfoDto.class);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //减去画面已转仓交易金额
        if (StringUtil.isNotEmpty(listInfoList) && StringUtil.isNotEmpty(resultList)) {
            for (TransferInfoDto info : listInfoList) {
                for (TransferCommon001Dto dto : resultList) {
                    if (StringUtil.equals(info.getPortfolioId(), dto.getPortfolioId())) {
                        dto.setAvailableCash(ArithmeticUtil.doSub(dto.getAvailableCash(), info.getTradeAmt()));
                    }
                }
            }
        }
//        Iterator<TransferCommon001Dto> it = resultList.iterator();
//        while (it.hasNext()) {
//            if (BigDecimal.ZERO.compareTo(it.next().getAvailableCash()) >= 0) {
//                it.remove();
//            }
//        }
        return resultList;
    }

    public PortfolioValuationDto getPtlPortfolioValuation(String portfolioId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("portfolioId", portfolioId);
        try {
            List<PortfolioValuationDto> resultList = simpleRestTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
                    + "/trade/transfer/common/TransferCommon/getPtlPortfolioValuation", paramMap, PortfolioValuationDto.class);
            if (CollectionUtils.isNotEmpty(resultList)) {
                return resultList.get(0);
            }
        } catch (Exception e) {
            logger.error("查询组合估值状态信息失败！{}", e.getMessage(), e);
        }

        return new PortfolioValuationDto();
    }

}
