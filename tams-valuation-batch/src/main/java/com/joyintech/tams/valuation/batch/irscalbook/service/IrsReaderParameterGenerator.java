package com.joyintech.tams.valuation.batch.irscalbook.service;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service.DrawBaseInfoReaderParameterGenerator;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 利率互换reder预处理
 * @author 殷亚
 * @date 2022/4/27 15:14
 * @see
 * @version 1.0
 */
@Service(IrsReaderParameterGenerator.BEAN_NAME)
public class IrsReaderParameterGenerator implements DrawBaseInfoReaderParameterGenerator {

    public static final String BEAN_NAME = "irsReaderParameterGenerator";

    @Override
    public List<TargetSearchDto> createSearchParam(Date happenDate) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);
        // 调用共通设计-> 标的类共通：标的信息查询（现金流计算规则）
        TargetSearchDto targetSearchDto2 = new TargetSearchDto();
        targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        targetSearchDto2.setCashType2(DictCodeListExt1.现金流二级类型_收取.getValue());
        targetSearchDtoList.add(targetSearchDto2);

        TargetSearchDto targetSearchDto3 = new TargetSearchDto();
        targetSearchDto3.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        targetSearchDto3.setCashType2(DictCodeListExt1.现金流二级类型_支付.getValue());
        targetSearchDtoList.add(targetSearchDto3);

        return targetSearchDtoList;
    }
}
