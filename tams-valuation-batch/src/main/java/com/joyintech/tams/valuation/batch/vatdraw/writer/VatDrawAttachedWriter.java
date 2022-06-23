/*
 * 文件名：ValAddedTaxDrawWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.VatBatchResult;

/**
 * 增值税计提批处理Writer
 * 
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDrawAttachedWriter
 * @since 1.0
 */
@Service
public class VatDrawAttachedWriter extends BaseWriter<NeedToWriteEntity> {

    /**
     * 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogic bookAccountGenLogic;
    
    /**
     * 增值税批处理Mapper
     */
    @Autowired
    private VatDrawMapper vatDrawMapper;
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        // 获取process返回组合的list值 - 记账凭证list
        List<VatBatchResult> resultList = (List<VatBatchResult>)items;

        // 循环记账凭证list，对每个记账凭证进行操作
        for (VatBatchResult result : resultList) {
            // 插入记账凭证主表信息 和 插入记账凭证明细表信息
            if (result.getBookGenerateResult() != null) {
                bookAccountGenLogic.insertKeepAccount(result.getBookGenerateResult());
            }
            // 插入增值税账务明细余额汇总表
            if (CollectionUtils.isNotEmpty(result.getListTaxBokBalanceSum())) {
                vatDrawMapper.insertTaxBokBalanceSum(result.getListTaxBokBalanceSum());
            }
        }
    }

}
