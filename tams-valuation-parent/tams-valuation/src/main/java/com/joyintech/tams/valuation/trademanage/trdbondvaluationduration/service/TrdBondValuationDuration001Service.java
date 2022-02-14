package com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdBondValuationDuration001Service extends CommonExportService<TrdBondValuationDuration001Dto> implements BaseProcessService<TrdBondValuationDuration001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdBondValuationDuration001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondValuationDuration001/findPage",
                pageParam,TrdBondValuationDuration001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdBondValuationDuration001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondValuationDuration001/queryPageList", params,
                TrdBondValuationDuration001Dto.class);
    }

    @Override
    public void doComplete(TrdBondValuationDuration001Dto trdBondValuationDuration001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondValuationDuration001/delete", trdBondValuationDuration001Dto, Integer.class);
        }
    }
}
