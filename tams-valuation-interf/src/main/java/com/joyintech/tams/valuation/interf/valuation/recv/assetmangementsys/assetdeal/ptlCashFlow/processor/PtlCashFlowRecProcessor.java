/*
 * 文件名：PtlCashFlowRecProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.processor;

import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.interf.core.InterfBaseProcessor;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.dto.PtlCashFlowProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.service.PtlCashFlowRecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * 自定义现金流交易信息接口 Processor
 * @author tanxuan
 * @version 1.0 2020年4月3日
 * @see PtlCashFlowRecProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PtlCashFlowRecProcessor extends InterfBaseProcessor<InterfNeedOperateEntity, PtlCashFlowProcessorResult> {

    @Autowired
    private PtlCashFlowRecService recService;
    
    @Override
    public PtlCashFlowProcessorResult process(InterfNeedOperateEntity item)
        throws Exception {
                
        // 获取reader方法的返回值(processor并发数据结构)
        TemplateOrderInfoReaderResult acctEntity = (TemplateOrderInfoReaderResult)item;
        
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        acctEntity.setResovleDate(interfExecuteInfo.getInterfDate());
        acctEntity.setParams(this.getParams());
        
        //调用债券交易信息处理公用service
        PtlCashFlowProcessorResult  result = recService.execute(acctEntity); 
        
        return result;
    }

}
