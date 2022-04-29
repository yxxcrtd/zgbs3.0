/*
 * 文件名：IrsFairValeAlterationsProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5
 * 修改人：殷亚
 * 修改时间：2022年4月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.batch.irsfairvalealterations.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.deltrightfairvalealterations.service.DeltRightFairValeAlterationsProcessorNewService;
import com.joyintech.tams.valuation.batch.deltrightfairvalealterations.service.DeltRightFairValeAlterationsProcessorService;
import com.joyintech.tams.valuation.batch.irsfairvalealterations.service.IrsFairValeAlterationsProcessorNewService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 利率互换公允价值变动账务处理 Processor
 * @author 殷亚
 * @date 2022/4/28 11:28
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsFairValeAlterationsProcessor extends
        BaseProcessor<NeedOperateEntity, BookGenerateResult> {


    /**
     * processor方法的 Service
     */
    @Autowired
    private IrsFairValeAlterationsProcessorNewService processerNewService;

    @Override
    public BookGenerateResultWithBokSummary process(NeedOperateEntity items) throws Exception {

        DrawBaseInfoSelectComResult drawBaseInfoSelectComResult = (DrawBaseInfoSelectComResult) items;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult) items;
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, acctEntity);
        // 1.利率互换公允价值批处理的业务处理
        BookGenerateResultWithBokSummary serviceDealResult  = new BookGenerateResultWithBokSummary();
        // 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
        String accountEntryType =  CacheUtil.getSysParam(GlobalConstant.ACCOUNT_ENTRY_TYPE);
        // 若按照内部科目编码分录，则需要映射分录对应科目信息.
        //调用service方法进行逻辑逻辑
        if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
            serviceDealResult = processerNewService.execute(drawBaseInfoSelectComResult, thisParams);
        }
        //当为null时候，给予初始化
        if(serviceDealResult == null){
            serviceDealResult = new BookGenerateResultWithBokSummary();
        }
        
        serviceDealResult.setProdId(
            drawBaseInfoSelectComResult.getBooksetId() + GlobalConstant.SPLIT + drawBaseInfoSelectComResult.getBokDetailId());

        return serviceDealResult;
    }

}
