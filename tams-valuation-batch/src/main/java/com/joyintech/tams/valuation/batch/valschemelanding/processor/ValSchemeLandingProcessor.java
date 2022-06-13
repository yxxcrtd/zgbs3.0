/*
 * 文件名：PlanCurrencyProAccountingProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.valuation.batch.deltrightcurcalamort.service.DeltRightCurCalAmortProcessorService;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingProceesorDto;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingReaderDto;
import com.joyintech.tams.valuation.batch.valschemelanding.service.ValSchemeLandingProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 估值方案落地批处理-processor
 * 
 * @author weipeng
 * @version 1.0 2022年03月25日
 * @see ValSchemeLandingProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValSchemeLandingProcessor extends BaseProcessor<NeedOperateEntity, ValSchemeLandingProceesorDto>{

    /**
     * 传入参数
     */
    private Map<String, Object> param = new HashMap<String, Object>();

    /**
     * 设置 param
     * 
     * @param param param
     */
    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
    
    @Autowired
    private DeltRightCurCalAmortProcessorService deltRightCurCalAmortProcessorService;

    @Autowired
    private ValSchemeLandingProcessorService valSchemeLandingProcessorService;

    @Override
    public ValSchemeLandingProceesorDto process(NeedOperateEntity items) throws Exception {
        //获取reader方法的返回值
        ValSchemeLandingReaderDto acctEntity = (ValSchemeLandingReaderDto) items;
        
        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        //调用service方法进行逻辑逻辑
        ValSchemeLandingProceesorDto result = valSchemeLandingProcessorService.execute(thisParams);
        // modify by weipeng on 20220303 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end
        //当为null时候，给予初始化
        if(result == null){
            result = new ValSchemeLandingProceesorDto();
        }
        result.setProdId(acctEntity.getBooksetId());

        return result;
    }
}
