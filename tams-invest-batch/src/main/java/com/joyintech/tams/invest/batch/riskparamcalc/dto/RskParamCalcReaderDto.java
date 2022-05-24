/*
 * 文件名：RskParamReCalcReaderDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import org.apache.shiro.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 批处理-指标参数重新更新-ReaderDto
 * 
 * @author 裴家俊
 * @author zhangrui1
 * @version 1.0 2020年4月17日
 * @see RskParamCalcReaderDto
 * @since 1.0
 */
public class RskParamCalcReaderDto extends NeedOperateEntity {

    /**
     * 预占编号
     */
    private String eventId;

    /**
     * 报文内容
     */
    private List<RskInterfDataDto> dataList;

    public RskParamCalcReaderDto() {

    }

    /**
     * @param eventId
     * @param dataList
     */
    public RskParamCalcReaderDto(String eventId, List<RskInterfDataDto> dataList) {
        setEventId(eventId);
        setDataList(dataList);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * 获取 报文内容
     * 
     * @return 报文内容.
     */
    public List<RskInterfDataDto> getDataList() {
        return dataList;
    }

    /**
     * 设置 报文内容
     * 
     * @param dataList 报文内容
     */
    public void setDataList(List<RskInterfDataDto> dataList) {
        this.dataList = CollectionUtils.isEmpty(dataList) ? dataList : dataList.stream().sorted(
            Comparator.comparing(RskInterfDataDto::getOrderNo)).collect(Collectors.toList());
    }
}
