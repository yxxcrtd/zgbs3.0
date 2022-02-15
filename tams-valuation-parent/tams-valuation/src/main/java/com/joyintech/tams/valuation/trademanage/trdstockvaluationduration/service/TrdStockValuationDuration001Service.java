package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdStockValuationDuration001Service extends CommonExportService<TrdStockValuationDuration001Dto> implements BaseProcessService<TrdStockValuationDuration001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdStockValuationDuration001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockValuationDuration001/findPage",
                pageParam,TrdStockValuationDuration001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdStockValuationDuration001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockValuationDuration001/queryPageList", params,
                TrdStockValuationDuration001Dto.class);
    }

    @Override
    public void doComplete(TrdStockValuationDuration001Dto trdStockValuationDuration001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdStockValuationDuration001/delete", trdStockValuationDuration001Dto, Integer.class);
        }
    }
}
