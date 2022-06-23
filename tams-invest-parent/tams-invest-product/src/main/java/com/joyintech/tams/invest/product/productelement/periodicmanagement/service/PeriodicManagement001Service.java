package com.joyintech.tams.invest.product.productelement.periodicmanagement.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFloatFeeDto;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

/**
 * 期次管理
 *
 * @author zhaojintao
 */
@Service("PeriodicManagement001Service")
public class PeriodicManagement001Service extends CommonExportService<PeriodicManagement001Dto>
        implements BaseProcessService<PeriodicManagement001Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PeriodicManagement001Service.class);

    @Autowired
    private PeriodicManagement002Service periodicManagement002Service;

    /**
     * 产品代码联想下拉框
     *
     * @param param String
     * @return List<SelectOptionDto>
     */
    public Page<SelectOptionDto> getFinprodIds(Map<String, Object> param) {
        param.put("dealMode10", BussinessDictCodeList.处理模式_周期型.getValue());
        param.put("userId", SecurityUtil.getLoginUser().getUserName());
        return restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME
                + "/PeriodicManagement/getFinprodIds", param, SelectOptionDto.class);
    }

    /**
     * 根据条件分页查询
     *
     * @param params
     * @return
     * @throws Exception
     */
    public Page<PeriodicManagement001Dto> selectForPage(Map<String, Object> params) {
        if (StringUtil.isNotEmpty(params.get("finprodId"))) {
            params.put("finprodId", StringUtil.splitToList(null, params.get("finprodId").toString()));
        }
        //统一 权限过滤 add by wangcheng 20191107
        params.put("userId", SecurityUtil.getLoginUser().getUserName());
        Page<PeriodicManagement001Dto> page = restTemplate.postForPage(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectForPage", params,
                PeriodicManagement001Dto.class);
        for (PeriodicManagement001Dto dto : page.getRoot()) {
//		    PeriodicManagement001Dto dtoTwo = periodicManagement002Service.selectAllInfo(dto.getPeriodProdId(),dto.getBranch(),dto.getTermType());
//		    BeanUtil.copyPropertiesIgnoreNull(dto, dtoTwo); 
            BussBenchMarkInfoDto bussBenchMarkInfoDto = new BussBenchMarkInfoDto();
            BussFinProductFloatFeeDto bussFinProductFloatFeeDto = new BussFinProductFloatFeeDto();
            FinBaseRuleInfo finBaseRuleInfo产品披露 = periodicManagement002Service.queryFinBaseRuleInfo(dto, DictCodeListExt1.业绩基准类型_产品披露.getValue());
            FinBaseRuleInfo finBaseRuleInfo浮动管理费 = periodicManagement002Service.queryFinBaseRuleInfo(dto, DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
            if (StringUtil.isNotEmpty(finBaseRuleInfo产品披露)) {
                BeanUtil.copyPropertiesIgnoreNull(bussBenchMarkInfoDto, finBaseRuleInfo产品披露);

                //添加产品披露的利率类型
                dto.setRateType(finBaseRuleInfo产品披露.getRateType());
            }
            if (StringUtil.isNotEmpty(finBaseRuleInfo浮动管理费)) {
                BeanUtil.copyPropertiesIgnoreNull(bussFinProductFloatFeeDto, finBaseRuleInfo浮动管理费);

                //增加一层校验，如果此时产品披露已经有利率类型的话就不用再进行覆盖添加了
                if (StringUtil.isEmpty(dto.getRateType())) {
                    dto.setRateType(finBaseRuleInfo浮动管理费.getRateType());
                }
            }
            dto.setBussBenchMarkInfoDto(bussBenchMarkInfoDto);
            dto.setBussFinProductFloatFeeDto(bussFinProductFloatFeeDto);
            Map<String, Object> map = new HashMap<>();
            map.put("periodProdId", dto.getPeriodProdId());
            map.put("periodId", dto.getPeriodId());
            List<FinFloatCalcRuleDetail> calcRuleDetail = periodicManagement002Service.selectFloatFeeList(map);
            dto.setFinFloatCalcRuleDetailList(calcRuleDetail);

            //处理产品代码
            String finprodId = dto.getFinprodId();
            if (StringUtil.isNotEmpty(finprodId) && StringUtil.contains(finprodId, "_")) {
                dto.setFinprodMarketId(finprodId.substring(finprodId.indexOf("_") + 1));
            } else {
                dto.setFinprodMarketId(finprodId);
            }
        }
        return page;
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        exportParam.config("/com/joyintech/tams/invest/product/productelement/periodicmanagement/conf/export_periodicmanagement001.xml").dataSource(
                DataSourceType.MASERT);
    }

    @Override
    public void doComplete(PeriodicManagement001Dto entity, OperationType type) {
        restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/doCompeleteDelete", entity,
                Integer.class);

    }

    public boolean isTrade(PeriodicManagement001Dto dto) {
        boolean flag = false;
        Map<String, Object> params = new HashMap<>();
        params.put("periodProdId", dto.getPeriodProdId());
        params.put("branch", Long.valueOf(dto.getBranch()));
        Integer num = restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/selectIsExistTrade", params,
                Integer.class);
        if (num > 0) {
            flag = true;
        }
        return flag;
    }

    public boolean checkPeriodId(String periodId) {
        boolean flag = false;
        Map<String, Object> params = new HashMap<>();
        params.put("periodId", periodId);
        Integer num = restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/checkPeriodId", params,
                Integer.class);
        if (num > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PeriodicManagement001Dto> selectPageData(Map<String, Object> selectParam) {
        try {
            return selectForPage(selectParam);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean getTrdProductDealByPeriodId(String periodId) {
        boolean flag = false;
        Map<String, Object> params = new HashMap<>();
        params.put("periodId", periodId);
        Integer num = restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/PeriodicManagement/getTrdProductDealByPeriodId", params,
                Integer.class);
        if (num > 0) {
            flag = true;
        }
        return flag;
    }
}
