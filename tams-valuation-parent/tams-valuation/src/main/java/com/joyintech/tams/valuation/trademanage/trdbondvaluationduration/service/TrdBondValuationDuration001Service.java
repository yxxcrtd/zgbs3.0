package com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdBondValuationDuration001Service extends CommonExportService<TrdBondValuationDurationDto> implements BaseProcessService<TrdBondValuationDurationDto> {

    /**
     * 检索债券信息
     *
     * @param pageParam 查询参数
     * @return
     * @see
     * @since 1.0
     */
    public Page<TrdBondValuationDurationDto> findPage(Map<String, Object> pageParam) {

        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondValuationDuration001/findPage",
                pageParam,TrdBondValuationDurationDto.class);
    }

    @Override
    protected void buildExportParam(ExportParam exportParam) {
            exportParam.config("/com/joyintech/tams/valuation/trademanage/trdbondvaluationduration/conf/export_conf.xml")
                    .dataSource(DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdBondValuationDurationDto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }

    @Override
    public void doComplete(TrdBondValuationDurationDto trdBondValuationDurationDto, OperationType operationType) {
        // 删除操作
        if(OperationType.DELETE.equals(operationType)) {
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondValuationDuration001/delete", trdBondValuationDurationDto, Integer.class);
        }
    }
}
