/*
 * 文件名：ProfitDistributionBookNetProcessor.java
 * 版权：Copyright 2020-2021 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.1
 * 修改人：WUJIE
 * 修改时间：2020年4月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.1
 */
package com.joyintech.tams.valuation.batch.profitdistributionnet.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.profitdistributionnet.dto.ProfitDistributionNetReaderDto;
import com.joyintech.tams.valuation.batch.profitdistributionnet.dto.ProfitDistributionNetResult;
import com.joyintech.tams.valuation.batch.profitdistributionnet.service.ProfitDistributionBookNetProcessorNewService;
import com.joyintech.tams.valuation.batch.profitdistributionnet.service.ProfitDistributionBookNetProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 净值产品利润分配批处理 -- 处理Processor
 * 
 * @author WUJIE
 * @version 1.0 2020年4月1日
 * @see ProfitDistributionBookNetProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ProfitDistributionBookNetProcessor extends BaseProcessor<NeedOperateEntity, ProfitDistributionNetResult> {

    /**
     * taFloadFeeAccountsService
     */
    @Autowired
    private ProfitDistributionBookNetProcessorService processorService;
    /**
     * taFloadFeeAccountsService
     */
    @Autowired
    private ProfitDistributionBookNetProcessorNewService processorNewService;

    /**
     * 返回记账凭证生成结果
     */

    @Override
    public ProfitDistributionNetResult process(NeedOperateEntity items)
        throws Exception {
        
        // 获取reader方法的返回值
        ProfitDistributionNetReaderDto acctEntity = (ProfitDistributionNetReaderDto)items;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

        // modify by weipeng on 20220304 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
        ProfitDistributionNetResult result = new ProfitDistributionNetResult();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            result = processorNewService.execute(thisParams);
        }
        else{
            result = processorService.execute(thisParams);
        }
        // modify by weipeng on 20220304 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

        //当为null时候，给予初始化
        if(result == null){
            result = new ProfitDistributionNetResult();
        }
        
        result.setProdId(acctEntity.getBooksetId());

        return result;
    }

}
