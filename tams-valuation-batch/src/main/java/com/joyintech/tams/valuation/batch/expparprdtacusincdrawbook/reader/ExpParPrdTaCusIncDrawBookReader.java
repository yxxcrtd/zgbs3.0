/*
 * 文件名：ExpParPrdTaCusIncDrawBookReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：lihuadong
 * 修改时间：2018年9月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
 * 预期收益型母产品TA客户收益计提入账 Reader
 * 
 * @author lihuadong
 * @version 1.0 2018年9月30日
 * @see ExpParPrdTaCusIncDrawBookReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpParPrdTaCusIncDrawBookReader extends BaseReader<StayDrawSubprdEntity> {

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
	 * 
	 * {@inheritDoc}
	 */
    @Override
	public List<StayDrawSubprdEntity> createList() {
        // 获取 task 传递的参数
        Map<String, Object> params = this.getParams();
        if (MapUtils.isEmpty(params)) {
            return null;
        }
        
        SysBatchTask batchTask = this.getBatchTask();
        //是否参与计算
        params.put("isCalc", DictCodeList.是否_是.getValue());
        // 调用共通查询：待计提金融产品现金流查询
        List<StayDrawSubprdEntity> stayPrdCashEntityList = accountingCommonService.getStayDrawSubprdInfo(params);
        
        if (CollectionUtils.isEmpty(stayPrdCashEntityList)) {
            return stayPrdCashEntityList;
        }
        
        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        stayPrdCashEntityList.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds,
        		batchTask.getTaskId());
        
        // 循环设计会计分录
        stayPrdCashEntityList.forEach(item -> {
            item.setBookSetTreeNode((TreeNode) booksetTreeNodeMap.get(item.getBooksetId())); // 设置会计分录
            item.setTaskPri(batchTask.getTaskPri()); // 设置任务优
        });
        
        return stayPrdCashEntityList;
    }

}
