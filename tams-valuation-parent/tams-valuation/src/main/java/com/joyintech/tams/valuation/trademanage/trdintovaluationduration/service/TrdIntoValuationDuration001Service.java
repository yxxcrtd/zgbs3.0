/*
 * 文件名：TrdRepoTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdintovaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;

import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdIntoValuationDuration001Service extends CommonExportService<TrdIntoValuationDuration001Dto> implements BaseProcessService<TrdIntoValuationDuration001Dto> {

	/**
	 * 查询
	 * @param pageParam Map
	 * @return Page类
	 * @throws Exception 异常信息
	 */
	public Page<TrdIntoValuationDuration001Dto> findPage(Map<String, Object> pageParam){
		return restTemplate.postForPage(
				ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIntoValuationDuration001/findPage",
				pageParam, TrdIntoValuationDuration001Dto.class);
	}

	/**
	 * 不分页  导出
	 *
	 * @param params
	 * @return
	 */
	public List<TrdIntoValuationDuration001Dto> queryPageList(Map<String, Object> params) {
		return restTemplate.postForList(
				ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIntoValuationDuration001/queryPageList", params,
                TrdIntoValuationDuration001Dto.class);
	}


	/**
	 * 工作流（留痕处理进行的操作）
	 */
	@Override
	public void doComplete(TrdIntoValuationDuration001Dto entity, OperationType type) {
		String action = "delete";
		restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdIntoValuationDuration001/" + action, entity, Integer.class);
	}

}
