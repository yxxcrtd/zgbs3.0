package com.joyintech.tams.invest.batch.overduesecpositionforward.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.tams.common.base.dao.PtlRecePositionFlowMapper;
import com.joyintech.tams.invest.batch.overduesecpositionforward.dto.OverdueSecPositionForwardResultDto;
import com.joyintech.tams.invest.batch.overduesecpositionforward.service.OverdueSecPositionForwardService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OverdueSecPositionForwardWriter extends BaseWriter<OverdueSecPositionForwardResultDto> {
    /**
     * service
     */
    @Autowired
    private OverdueSecPositionForwardService overdueSecPositionForwardService;
    @Override
    public void write(List<? extends OverdueSecPositionForwardResultDto> items) throws Exception {
        overdueSecPositionForwardService.holdResultDto(items);
    }
}
