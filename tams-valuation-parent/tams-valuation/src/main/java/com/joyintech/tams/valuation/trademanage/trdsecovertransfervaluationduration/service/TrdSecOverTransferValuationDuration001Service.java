package com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdSecOverTransferValuationDuration001Service extends CommonExportService<TrdSecOverTransferValuationDuration001Dto> implements BaseProcessService<TrdSecOverTransferValuationDuration001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdSecOverTransferValuationDuration001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdSecOverTransferValuationDuration001/findPage",
                pageParam,TrdSecOverTransferValuationDuration001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdSecOverTransferValuationDuration001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdSecOverTransferValuationDuration001/queryPageList", params,
                TrdSecOverTransferValuationDuration001Dto.class);
    }

    @Override
    public void doComplete(TrdSecOverTransferValuationDuration001Dto trdSecOverTransferValuationDuration001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdSecOverTransferValuationDuration001/delete", trdSecOverTransferValuationDuration001Dto, Integer.class);
        }
    }
}
