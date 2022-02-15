package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdIrsValuationDuration001Service extends CommonExportService<TrdIrsValuationDuration001Dto> implements BaseProcessService<TrdIrsValuationDuration001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdIrsValuationDuration001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIrsValuationDuration001/findPage",
                pageParam,TrdIrsValuationDuration001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdIrsValuationDuration001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIrsValuationDuration001/queryPageList", params,
                TrdIrsValuationDuration001Dto.class);
    }

    @Override
    public void doComplete(TrdIrsValuationDuration001Dto trdIrsValuationDuration001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdIrsValuationDuration001/delete", trdIrsValuationDuration001Dto, Integer.class);
        }
    }
}
