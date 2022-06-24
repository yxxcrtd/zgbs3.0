/*
 * 文件名：ExpectIncomeSubProductFixedFeeCalBookReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.expectincomesubproductfixedfeecalbook.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;

/**
 * 预期收益型子产品 TA 固定费用计提入账 Reader
 * 
 * @author zhangwei
 * @version 1.0 2018年9月30日
 * @see ExpectIncomeSubProductFixedFeeCalBookReader
 * @since 1.0
 */
@Service
//@Scope("prototype")
public class ExpReturnSubProductFixedFeeCalBookReader extends BaseReader<StayDrawSubprdEntity> {

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
	@SuppressWarnings("unchecked")
	@Override
	public List<StayDrawSubprdEntity> createList() {
		// 获取 task 传递的参数
		Map<String, Object> params = this.getParams();
		if (MapUtils.isEmpty(params)) {
			return null;
		}

		SysBatchTask batchTask = this.getBatchTask();

		// 获取从 task 拿到的参数
		String profitType = (String) params.get("profitType");// 收益类型
		List<String> cashTypeList = (List<String>) params.get("cashTypeList");// 现金流类型集合
		// 调用共通查询：待计提子产品现金流查询
		List<StayDrawSubprdEntity> stayDrawSubprdEntitiyList = accountingCommonService.getPrdInfoOrTAInfo(params, profitType, cashTypeList);

		if (CollectionUtils.isEmpty(stayDrawSubprdEntitiyList)) {
			return stayDrawSubprdEntitiyList;
		}

		// 获取分录信息
		Set<String> bookSetIds = new HashSet<String>();
		stayDrawSubprdEntitiyList.forEach(item -> {
			if (StringUtil.isNotEmpty(item.getBooksetId())) {
				bookSetIds.add(item.getBooksetId());
			}
		});
		Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds,
				batchTask.getTaskId());

		// 循环设计会计分录
		stayDrawSubprdEntitiyList.forEach(item -> {
			item.setBookSetTreeNode((TreeNode) booksetTreeNodeMap.get(item.getBooksetId())); // 设置会计分录
			item.setTaskPri(batchTask.getTaskPri()); // 设置任务优先级
		});

		return stayDrawSubprdEntitiyList;
	}

}
