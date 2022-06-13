/*
 * 文件名：TrdDepositInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 存款类投资交易service
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class TrdDepositValuationTrade001Service extends CommonExportService<TrdDepositValuationTrade001Dto> implements BaseProcessService<TrdDepositValuationTrade001Dto> {

    /**
     * 查询
     * @param pageParam Map
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<TrdDepositValuationTrade001Dto> findPage(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/findPage",
                pageParam,TrdDepositValuationTrade001Dto.class);
    }

    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/valuation/trademanage/trddepositvaluationtrade/conf/export_conf.xml")
                .dataSource(DataSourceType.MASERT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TrdDepositValuationTrade001Dto> selectPageData(Map<String, Object> selectParam) {
        return findPage(selectParam);
    }

    /**
     * 工作流（留痕处理进行的操作）
     */
    @Override
    public void doComplete(TrdDepositValuationTrade001Dto entity, OperationType type) {
        String action = "delete";
        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdDepositValuationTrade001/" + action, entity, Integer.class);
    }
}
