package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

/**
 * 一级投标指令
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service
public class CfetsEtrBondFirstBid002Service extends CommonExportService<CfetsEtrBondFirstBid002Dto> implements BaseProcessService<CfetsEtrBondFirstBid002Dto> {

    @Override
    public void doComplete(CfetsEtrBondFirstBid002Dto cfetsEtrBondFirstBid002Dto, OperationType operationType) {

    }

    /**
     * 保存提交时，后台数据转换
     * @param cfetsDto
     * @return
     */
    public CfetsEtrBondFirstBid002Dto packageEquity(CfetsEtrBondFirstBidSaveRequest cfetsDto) {
        return  null;
    }
}
