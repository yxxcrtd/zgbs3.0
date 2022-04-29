/*
 * 文件名：ResetRateWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateResult;
import com.joyintech.tams.valuation.common.service.RateAdjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * writer
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetRateWriter extends BaseWriter<NeedToWriteEntity>{

    @Autowired
    private RateAdjService rateAdjService;
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        @SuppressWarnings("unchecked")
        List<ResetRateResult> resultList = (List<ResetRateResult>)items;
        if (!StringUtil.isEmptyList(resultList)) {
            for (ResetRateResult resetRateResult : resultList) {
                if(null!=resetRateResult.getRateAdjDto()){
                    rateAdjService.insert(resetRateResult.getRateAdjDto()); 
                }
            }
        }
    }
    
}
