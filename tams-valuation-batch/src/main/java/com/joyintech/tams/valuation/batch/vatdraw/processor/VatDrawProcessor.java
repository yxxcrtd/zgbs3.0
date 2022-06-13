/*
 * 文件名：ValAddedTaxDealProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.batch.vatdraw.dto.VatBatchResult;
import com.joyintech.tams.valuation.batch.vatdraw.service.VatDrawProcessorBNewService;
import com.joyintech.tams.valuation.batch.vatdraw.service.VatDrawProcessorBService;
import com.joyintech.tams.valuation.batch.vatdraw.service.VatDrawProcessorNewService;
import com.joyintech.tams.valuation.batch.vatdraw.service.VatDrawProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 增值税计提批处理Processor
 * 
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDrawProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class VatDrawProcessor extends BaseProcessor<NeedOperateEntity, VatBatchResult> {

    /**
     * processor方法的 Service
     */
    @Autowired
    private VatDrawProcessorService vatDrawProcessorService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private VatDrawProcessorBService vatDrawProcessorBService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private VatDrawProcessorNewService vatDrawProcessorNewService;

    /**
     * processor方法的 Service
     */
    @Autowired
    private VatDrawProcessorBNewService vatDrawProcessorBNewService;

    @Override
    public VatBatchResult process(NeedOperateEntity items)
        throws Exception {
        // 获取reader方法的返回值
        PrdCashDto acctEntity = (PrdCashDto)items;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);
        VatBatchResult result = null;

        // modify by weipeng on 20220311 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 调用service方法进行逻辑逻辑
        if ("02".equals(CacheUtil.getSysParam("TAX_CALC_TASK"))) {
            // 若按照内部科目编码分录，则需要映射分录对应科目信息.
            //调用service方法进行逻辑逻辑
            if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                result = vatDrawProcessorBNewService.execute(thisParams);
            }
            else{
                result = vatDrawProcessorBService.execute(thisParams);
            }
        }
        else {
            // 若按照内部科目编码分录，则需要映射分录对应科目信息.
            //调用service方法进行逻辑逻辑
            if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
                result = vatDrawProcessorNewService.execute(thisParams);
            }
            else{
                result = vatDrawProcessorService.execute(thisParams);
            }
        }
        // modify by weipeng on 20220311 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        // 当为null时候，给予初始化
        if (result == null) {
            result = new VatBatchResult();
        }

        result.setProdId(
            acctEntity.getBooksetId() + GlobalConstant.SPLIT + acctEntity.getBokDetailId());

        return result;
    }

}
