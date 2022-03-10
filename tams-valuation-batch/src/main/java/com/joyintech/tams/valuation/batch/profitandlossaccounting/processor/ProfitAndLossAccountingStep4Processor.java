/*
 * 文件名：ProfitAndLossAccountingProcessor.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0 修改人：杨智 修改时间：2018年5月23日
 * 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitandlossaccounting.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingResult;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.service.ProfitAndLossAccountingStep4ProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 未分配利润科目结转Processor
 *
 * @author weipeng
 * @version 1.0 2019年1月14日
 * @see ProfitAndLossAccountingStep4Processor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingStep4Processor extends BaseProcessor<NeedOperateEntity, ProfitAndLossAccountingResult> {

    @Autowired
    private ProfitAndLossAccountingStep4ProcessorService processorService;

    @Override
    public ProfitAndLossAccountingResult process(NeedOperateEntity readerEntity)
            throws Exception {
        ProfitAndLossAccountingNeedOperateEntity acctEntity = (ProfitAndLossAccountingNeedOperateEntity)readerEntity;
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);
        ProfitAndLossAccountingResult result = processorService.execute(thisParams);
        result.setProdId(acctEntity.getBooksetId());
        return result;
    }

}
