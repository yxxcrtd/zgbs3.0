package com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdBondChangeTrade001Service extends CommonExportService<TrdBondChangeTradeDto> implements BaseProcessService<TrdBondChangeTradeDto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdBondChangeTradeDto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade001/findPage",
                pageParam,TrdBondChangeTradeDto.class);
    }

    /**
     * 不分页
     *
     * @param params
     * @return
     */
    public List<TrdBondChangeTradeDto> queryPageList(Map<String, Object> params) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade001/queryPageList", params,
                TrdBondChangeTradeDto.class);
    }

    /**
     *
     * 获取转出证券名称联想下拉框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondChangeTrade001/getAssetId" , map,
                SelectOptionDto.class);
    }


    /**
     *
     * 获取正股代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getStockId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                        + "/TrdBondChangeTrade001/getStockId" , map,
                SelectOptionDto.class);
    }

    @Override
    public void doComplete(TrdBondChangeTradeDto trdBondChangeTradeDto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondChangeTrade001/delete", trdBondChangeTradeDto, Integer.class);
        }
    }
}
