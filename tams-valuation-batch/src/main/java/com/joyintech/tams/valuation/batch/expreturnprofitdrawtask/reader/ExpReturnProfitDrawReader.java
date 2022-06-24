/*
 * 文件名：YelidPrdProfitDrawReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.expreturnprofitdrawtask.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;

/**
 * 预期收益型子产品TA客户收益计提-账务明细处理Reader
 * 
 * @author dongjun
 * @version 1.0 2018年9月29日
 * @see ExpReturnProfitDrawReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpReturnProfitDrawReader extends BaseReader<StayDrawSubprdEntity> {

    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<StayDrawSubprdEntity> createList() {

        Map<String,Object> params = this.getParams();
        String profitType = (String)params.get("profitType");
        List<String> cashList = (List<String>)params.get("cashList");
        List<StayDrawSubprdEntity> result = accountingCommonService.getPrdInfoOrTAInfo(params, profitType, cashList);

        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<StayDrawSubprdEntity>();
        }

        for (StayDrawSubprdEntity entity : result) {
            entity.setProdId(entity.getFinprodId());
        }

        // 获取分录信息
        SysBatchTask batchTask = this.getBatchTask();
        Set<String> bookSetIds = new HashSet<String>();
        result.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());
        // 循环设计会计分录
        result.forEach(item -> {
            item.setBookSetTreeNode((TreeNode)booksetTreeNodeMap.get(item.getBooksetId())); // 设置会计分录
            item.setTaskPri(batchTask.getTaskPri()); // 设置任务优先级
        });

        return result;
    }

}
