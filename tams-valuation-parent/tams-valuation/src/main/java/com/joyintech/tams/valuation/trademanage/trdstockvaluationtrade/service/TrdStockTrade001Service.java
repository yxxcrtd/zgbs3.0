package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTrade001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdStockTrade001Service extends CommonExportService<TrdStockTrade001Dto> implements BaseProcessService<TrdStockTrade001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdStockTrade001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockTrade001/findPage",
                pageParam,TrdStockTrade001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdStockTrade001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockTrade001/queryPageList", params,
                TrdStockTrade001Dto.class);
    }

    @Override
    public void doComplete(TrdStockTrade001Dto trdStockTrade001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdStockTrade001/delete", trdStockTrade001Dto, Integer.class);
        }
    }
}
