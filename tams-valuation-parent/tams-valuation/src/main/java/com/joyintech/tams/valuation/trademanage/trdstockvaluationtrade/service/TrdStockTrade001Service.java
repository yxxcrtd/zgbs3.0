package com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdStockTrade001Service extends CommonExportService<TrdStockTradeDto> implements BaseProcessService<TrdStockTradeDto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdStockTradeDto> findPage(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdStockTrade001/findPage",
                pageParam,TrdStockTradeDto.class);
    }

    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/valuation/trademanage/trdstockvaluationtrade/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdStockTradeDto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }

    @Override
    public void doComplete(TrdStockTradeDto trdStockTradeDto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdStockTrade001/delete", trdStockTradeDto, Integer.class);
        }
    }
}
