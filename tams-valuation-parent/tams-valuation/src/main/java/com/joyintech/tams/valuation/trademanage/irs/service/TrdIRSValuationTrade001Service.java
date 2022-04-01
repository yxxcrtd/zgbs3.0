package com.joyintech.tams.valuation.trademanage.irs.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.irs.dto.TrdIRSValuationTrade001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdIRSValuationTrade001Service extends CommonExportService<TrdIRSValuationTrade001Dto> implements BaseProcessService<TrdIRSValuationTrade001Dto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdIRSValuationTrade001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIRSValuationTrade001/findPage",
                pageParam,TrdIRSValuationTrade001Dto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdIRSValuationTrade001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIRSValuationTrade001/queryPageList", params,
                TrdIRSValuationTrade001Dto.class);
    }

    @Override
    public void doComplete(TrdIRSValuationTrade001Dto trdIRSValuationTrade001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdIRSValuationTrade001/delete", trdIRSValuationTrade001Dto, Integer.class);
        }
    }
}
