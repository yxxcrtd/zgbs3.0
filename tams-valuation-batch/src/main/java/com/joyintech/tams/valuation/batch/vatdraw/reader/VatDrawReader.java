/*
 * 文件名：ValAddedTaxDrawReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.vatdraw.dao.VatDrawMapper;
import com.joyintech.tams.valuation.batch.vatdraw.dto.PrdCashDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 增值税计提批处理Reader
 * 
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDrawReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class VatDrawReader extends BaseReader<PrdCashDto> {

    /**
     * 余额表生成处理mapper
     */
    @Autowired
    private VatDrawMapper vatDrawMapper;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrdCashDto> createList() {
        // 发生日
        Date happenDate = (Date)this.getParams().get(BookConstant.HAPPEN_DATE);
        // 任务代码
        String taskId = (String)this.getParams().get("taskId");
        SysBatchTask batchTask = this.getBatchTask();
        // 现金流类型集合
        List<String> cashTypeList = new ArrayList<String>();
        cashTypeList.add(DictCodeListExt1.现金流二级类型_税额.getValue());

        this.getParams().put("cashTypeList", cashTypeList);
        this.getParams().put("accSubjectType", DictCodeListExt1.金融产品类型_理财产品.getValue());
        // 查询待计提金融产品现金流
        List<PrdCashDto> listDto = Lists.newArrayList();
            
        List<PrdCashDto> listDtoTwo = vatDrawMapper.queryPrdCashDto(this.getParams());
        
        if (CollectionUtils.isEmpty(listDtoTwo)) {
            return listDtoTwo;
        }
        for (PrdCashDto prdCashDto : listDtoTwo) {
            listDto.add(prdCashDto);
        }
        if (CollectionUtils.isEmpty(listDto)) {
            return listDto;
        }
        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listDto.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());
        
        // 循环设计会计分录
        listDto.forEach(item -> {
            // 设置发生日期
            item.setHappenDate(happenDate);
            // 设置任务代码
            item.setTaskId(taskId);
            // 设置会计分录
            item.setBookSetTreeNode((TreeNode)booksetTreeNodeMap.get(item.getBooksetId()));
            // 设置任务优先级
            item.setTaskPri(batchTask.getTaskPri());
        });
        return listDto;
    }
}
