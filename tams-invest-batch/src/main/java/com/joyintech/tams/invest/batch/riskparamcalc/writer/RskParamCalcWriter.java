/*
 * 文件名：RskParamReCalcWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskParamReCalcWriterDto;
import com.joyintech.tams.invest.batch.riskparamcalc.service.RskParamReCalcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理-指标参数重新更新-Writer
 * 
 * @author 裴家俊
 * @version 1.0 2020年4月17日
 * @see RskParamCalcWriter
 * @since 1.0
 */
@Service
@Scope(value = "prototype")
public class RskParamCalcWriter extends BaseWriter<NeedToWriteEntity> {

    @Autowired
    private RskParamReCalcService service;

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends NeedToWriteEntity> writeList)
        throws Exception {
        if (StringUtil.isEmptyList(writeList)) {
            return;
        }
        List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        writeList.forEach(entity -> {
            if (entity == null) {
                return;
            }
            RskParamReCalcWriterDto dto = (RskParamReCalcWriterDto)entity;
            details.addAll(dto.getParaCalList());
        });
        // 参数计算流水
        if (!StringUtil.isEmptyList(details)) {
            service.saveDetails(details);
        }
    }

}
