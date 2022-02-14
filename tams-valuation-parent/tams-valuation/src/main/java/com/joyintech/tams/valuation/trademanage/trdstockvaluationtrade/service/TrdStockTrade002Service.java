package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTrade002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

@Service
public class TrdStockTrade002Service extends CommonExportService<TrdStockTrade002Dto> implements BaseProcessService<TrdStockTrade002Dto> {

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdStockTrade002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockTrade002/queryTradeInfoByTradeId", tradeId,
                TrdStockTrade002Dto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdStockTrade002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdStockTrade002Dto trdStockTrade002Dto, OperationType operationType) {
        String action = StringUtil.EMPTY;
         if (OperationType.NEW.equals(operationType)) {
             action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockTrade002/" + action, trdStockTrade002Dto,
                Integer.class);

    }

}
