package com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service;

import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;

import java.util.Date;
import java.util.List;

/**
 * @author 何阳阳
 * @date 2022/3/8
 */
public interface DrawBaseInfoReaderParameterGenerator {

    List<TargetSearchDto> createSearchParam(Date happenDate);
}
