package com.joyintech.tams.valuation.trademanage.option.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.option.dto.TrdOptionValuationTrade001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdOptionValuationTrade001Service extends CommonExportService<TrdOptionValuationTrade001Dto> implements BaseProcessService<TrdOptionValuationTrade001Dto> {

    /**
     * 检索场外期权信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdOptionValuationTrade001Dto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade001/findPage",
                pageParam, TrdOptionValuationTrade001Dto.class);
    }

    /**
     *
     * 合约标的下拉框
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> selectTargetIdAbbr(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade001/selectTargetIdAbbr",
                param, List.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdOptionValuationTrade001Dto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade001/queryPageList", params,
                TrdOptionValuationTrade001Dto.class);
    }

    @Override
    public void doComplete(TrdOptionValuationTrade001Dto trdOptionValuationTrade001Dto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOptionValuationTrade001/delete", trdOptionValuationTrade001Dto, Integer.class);
        }
    }
}
