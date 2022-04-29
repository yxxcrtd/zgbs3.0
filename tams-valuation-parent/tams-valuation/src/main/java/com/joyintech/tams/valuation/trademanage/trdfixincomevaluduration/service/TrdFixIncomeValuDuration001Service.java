/*
 * 文件名：TrdRepoTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrdFixIncomeValuDuration001Service extends CommonExportService<TrdFixIncomeValuDuration001Dto> implements BaseProcessService<TrdFixIncomeValuDuration001Dto> {
	/**
	 * 查询
	 * @param pageParam Map
	 * @return Page类
	 * @throws Exception 异常信息
	 */
	public Page<TrdFixIncomeValuDuration001Dto> findPage(Map<String, Object> pageParam){
		return restTemplate.postForPage(
				ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuDuration001/findPage",
				pageParam, TrdFixIncomeValuDuration001Dto.class);
	}

	@Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.config("/com/joyintech/tams/valuation/trademanage/trdfixincomevaluduration/conf/export_conf.xml")
				.dataSource(DataSourceType.MASERT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<TrdFixIncomeValuDuration001Dto> selectPageData(Map<String, Object> selectParam) {
		return findPage(selectParam);
	}

	/**
	 * 工作流（留痕处理进行的操作）
	 */
	@Override
	public void doComplete(TrdFixIncomeValuDuration001Dto entity, OperationType type) {
		String action = "delete";
		restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFixIncomeValuDuration001/" + action, entity, Integer.class);
	}


}
