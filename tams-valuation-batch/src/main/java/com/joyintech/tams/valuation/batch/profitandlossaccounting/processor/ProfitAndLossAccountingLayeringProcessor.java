/*
 * 文件名：ProfitAndLossAccountingProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitandlossaccounting.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingResult;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.service.ProfitAndLossAccountingLayeringProcessorNewService;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.service.ProfitAndLossAccountingLayeringProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 权益分层处理Processor
 *
 * @author weipeng
 * @version 1.0 2019年1月17日
 * @see ProfitAndLossAccountingLayeringProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitAndLossAccountingLayeringProcessor extends BaseProcessor<NeedOperateEntity, ProfitAndLossAccountingResult> {

    @Autowired
    private ProfitAndLossAccountingLayeringProcessorService processorService;

    @Autowired
    private ProfitAndLossAccountingLayeringProcessorNewService processorNewService;

    @Override
    public ProfitAndLossAccountingResult process(NeedOperateEntity readerEntity)
            throws Exception {
        ProfitAndLossAccountingNeedOperateEntity acctEntity = (ProfitAndLossAccountingNeedOperateEntity)readerEntity;
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        // modify by weipeng on 20220304 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        ProfitAndLossAccountingResult result = new ProfitAndLossAccountingResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = processorNewService.execute(thisParams);
        }
        else{
            result = processorService.execute(thisParams);
        }
        // modify by weipeng on 20220304 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        result.setProdId(acctEntity.getBooksetId());
        return result;
    }

}
