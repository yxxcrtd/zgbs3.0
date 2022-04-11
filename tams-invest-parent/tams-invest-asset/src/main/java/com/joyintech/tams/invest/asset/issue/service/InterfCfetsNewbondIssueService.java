package com.joyintech.tams.invest.asset.issue.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.tams.invest.asset.issue.dto.FinProductIssueDto;
import com.joyintech.tams.invest.asset.issue.dto.InterfCfetsNewbondIssueDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

/**
 * CFETS新债标的发行信息service
 * 〈功能详细描述〉
 *
 * @author zhuys
 * @version 1.0 2022年04月10日
 * @see InterfCfetsNewbondIssueService
 * @since 1.0
 */
@Service
public class InterfCfetsNewbondIssueService extends CommonExportService<InterfCfetsNewbondIssueDto> implements BaseProcessService<FinProductIssueDto> {

    @Override
    public void doComplete(FinProductIssueDto entity, OperationType type) {

    }
}
