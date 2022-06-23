/*
 * 文件名：FixedFeeCalBookReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.fixedfeecalbook.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * TA固定费用计提入账批处理Reader
 * @author weipeng
 * @version 1.0 2018年6月4日
 * @see FixedFeeCalBookReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FixedFeeCalBookReader extends BaseReader<StayDrawSubprdEntity>{
    
    /**
     * 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;
    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StayDrawSubprdEntity> createList() {
        //获取task传递参数
        Map<String, Object> params = this.getParams();
        if(MapUtils.isEmpty(params)){
            return null;
        }
        
        SysBatchTask batchTask = this.getBatchTask(); 
        
        //调用公用方法【待计提金融产品现金流查询】，获取待计提金融产品现金流。
        params.put("profitType", null);
        //是否参与计算
        params.put("isCalc", DictCodeList.是否_是.getValue());
        List<StayDrawSubprdEntity> listPrdCashEntity = accountingCommonService.getStayDrawSubprdInfo(params);
        
        if (CollectionUtils.isEmpty(listPrdCashEntity)) {
            return listPrdCashEntity;
        }
        
        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listPrdCashEntity.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());
        
        //循环设计会计分录
        listPrdCashEntity.forEach(item ->{
            item.setBookSetTreeNode((TreeNode) booksetTreeNodeMap.get(item.getBooksetId())); //设置会计分录
            item.setTaskPri(batchTask.getTaskPri()); //设置任务优先级
        });
        
        return listPrdCashEntity;
    }

}
