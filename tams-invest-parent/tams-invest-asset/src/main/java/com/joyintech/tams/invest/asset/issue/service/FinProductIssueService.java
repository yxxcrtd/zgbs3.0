package com.joyintech.tams.invest.asset.issue.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.tams.invest.asset.issue.dto.FinProductIssueDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

/**
 * 标的类金融产品发行信息service
 * 〈功能详细描述〉
 *
 * @author zhuys
 * @version 1.0 2022年04月10日
 * @see FinProductIssueService
 * @since 1.0
 */
@Service
public class FinProductIssueService extends CommonExportService<FinProductIssueDto> implements BaseProcessService<FinProductIssueDto> {

    @Override
    public void doComplete(FinProductIssueDto entity, OperationType type) {

    }
}
