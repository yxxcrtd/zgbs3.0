package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDurationDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdStockValuationDuration001Service extends CommonExportService<TrdStockValuationDurationDto> implements BaseProcessService<TrdStockValuationDurationDto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdStockValuationDurationDto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockValuationDuration001/findPage",
                pageParam,TrdStockValuationDurationDto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdStockValuationDurationDto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockValuationDuration001/queryPageList", params,
                TrdStockValuationDurationDto.class);
    }

    @Override
    public void doComplete(TrdStockValuationDurationDto trdStockValuationDurationDto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdStockValuationDuration001/delete", trdStockValuationDurationDto, Integer.class);
        }
    }
}
