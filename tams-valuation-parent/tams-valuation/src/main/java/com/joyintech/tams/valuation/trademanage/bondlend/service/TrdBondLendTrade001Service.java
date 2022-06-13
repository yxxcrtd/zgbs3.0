package com.joyintech.tams.valuation.trademanage.bondlend.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdBondLendTrade001Service extends CommonExportService<TrdBondLendTradeDto> implements BaseProcessService<TrdBondLendTradeDto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdBondLendTradeDto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade001/findPage",
                pageParam,TrdBondLendTradeDto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdBondLendTradeDto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade001/queryPageList", params,
                TrdBondLendTradeDto.class);
    }

    @Override
    public void doComplete(TrdBondLendTradeDto trdBondLendTradeDto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondLendTrade001/delete", trdBondLendTradeDto, Integer.class);
        }
    }
}
