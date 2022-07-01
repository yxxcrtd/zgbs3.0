/*
 * 文件名：SubjectBalanceCollectWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.psbc.bankpositioncollect.writer;

import java.util.List;

import com.joyintech.tams.valuation.batch.psbc.bankcalbook.service.BokBankPositionService;
import com.joyintech.tams.valuation.batch.psbc.bankpositioncollect.dto.BankPositionGenerateResult;
import com.joyintech.tams.valuation.batch.psbc.bankpositioncollect.dto.BankSubjectBalanceBatch;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2019年8月28日
 * @see BankPositionCollectWriter
 * @since 1.0
 */
@Service
public class BankPositionCollectWriter extends BaseWriter<NeedToWriteEntity>{
    
    /**
     * 科目余额汇总mapper
     */
    @Autowired
    private BokBankPositionService bokBankPositionService;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        //获取process返回组合的list值 - 记账凭证list
        List<BankPositionGenerateResult> resultList = (List<BankPositionGenerateResult>) items;
        //循环科目余额list，对每个记账凭证进行操作
        for(BankPositionGenerateResult result : resultList){
            //入库科目余额数据 
            List<BankSubjectBalanceBatch> listBatches = result.getSubjectBalanceBatchs();
            if(CollectionUtils.isNotEmpty(listBatches)){
                listBatches.forEach(item ->{
                    //循环录入库中
                    bokBankPositionService.insertSelective(item);
                });
            }
        }
    }
}
