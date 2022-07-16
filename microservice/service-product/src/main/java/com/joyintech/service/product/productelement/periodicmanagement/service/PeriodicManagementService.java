package com.joyintech.service.product.productelement.periodicmanagement.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.product.productelement.periodicmanagement.dao.PeriodicManagementMapper;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinBaseRuleDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinProductFloatFeeDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagementDto;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("backPeriodicManagementService")
public class PeriodicManagementService extends BaseService<PeriodicManagement001Dto> {

    @Autowired
    private PeriodicManagementMapper periodicManagementMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;
    /**
     * 平台日基础组件
     */
    @Autowired(required = false)
    private IPlatformDate platformDate;

    public Page<SelectOptionDto> getFinprodIds(int page, int length, Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length, () -> periodicManagementMapper.getFinprodIds(param));
    }

    public Page<PeriodicManagement001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) {
        pageParam.put("VALID_STATUS_01", DictCodeListExt1.有效状态_有效.getValue());
        pageParam.put("VALID_STATUS_02", DictCodeListExt1.有效状态_待处理.getValue());
        pageParam.put("platDate", DateUtil.format(platformDate.getSysDate()));
        pageParam.put("F26", DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue());
        pageParam.put("baseRuleType01", DictCodeListExt1.业绩基准类型_产品披露.getValue());
        pageParam.put("publishType01", DictCodeListExt1.披露类型_区间披露.getValue());
        pageParam.put("calcType01", DictCodeListExt1.计算类型_固定利率.getValue());
        pageParam.put("calcType02", DictCodeListExt1.计算类型_浮动利率.getValue());
        pageParam.put("dealMode10", DictCodeList.处理模式_周期型.getValue());
        pageParam.put("termType03", DictCodeListExt2.期限类型_无固定期限.getValue());
        Page<PeriodicManagement001Dto> dto = super.queryForPage(page, length, () -> periodicManagementMapper.selectForPage(pageParam));
        return dto;
    }

    public Page<SelectOptionDto> getInvestCycle(int page, int length, Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length, () -> periodicManagementMapper.getInvestCycle(param));
    }

    /**
     * 查询基本信息
     *
     * @param param
     * @return
     */
    public List<PeriodicManagementDto> selectBaseInfoList(Map<String, Object> param) {
        return periodicManagementMapper.selectBaseInfoList(param);
    }

    /**
     * 查询业绩基准
     *
     * @param param
     * @return
     */
    public FinBaseRuleDto selectBenchMarkInfo(Map<String, Object> param) {
        return periodicManagementMapper.selectBenchMarkInfo(param);
    }

    /**
     * 查询浮动管理费信息
     *
     * @param param
     * @return
     */
    public FinProductFloatFeeDto selectFloatFeeInfo(Map<String, Object> param) {
        List<FinProductFloatFeeDto> feeDtoList = periodicManagementMapper.selectFloatFeeInfo(param);
        return StringUtil.isEmptyList(feeDtoList) ? null : feeDtoList.get(0);
    }

    public List<FinFloatCalcRuleDetail> selectFloatFeeList(Map<String, Object> param) {
        return periodicManagementMapper.selectFloatFeeList(param);
    }

    public PeriodicManagementDto selectPeriodProdId(Map<String, Object> param) {
        List<PeriodicManagementDto> list = periodicManagementMapper.selectPeriodProdId(param);
        if (!StringUtil.isEmptyList(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询现金流支付规则
     *
     * @param param
     * @return
     */
    public FinCashPayRule selectCashPayRule(Map<String, Object> param) {
        return periodicManagementMapper.selectCashPayRule(param);
    }

    /**
     * 查询现金流字段规则
     *
     * @param param
     * @return
     */
    public FinCashCalcRule selectCashCalcRule(Map<String, Object> param) {
        return periodicManagementMapper.selectCashCalcRule(param);
    }

    /**
     * 查询业绩报酬计算规则
     *
     * @param param
     * @return
     */
    public FinFloatCalcRule selectFinFloatCalcRule(Map<String, Object> param) {
        return periodicManagementMapper.selectFinFloatCalcRule(param);
    }

    /**
     * 查询业绩报酬计算规则明细
     *
     * @param param
     * @return
     */
    public List<FinFloatCalcRuleDetail> selectFinFloatCalcRuleDetail(Map<String, Object> param) {
        return periodicManagementMapper.selectFinFloatCalcRuleDetail(param);
    }

    public List<FinCashInfo> selectFinCashInfo(Map<String, Object> param) {
        return periodicManagementMapper.selectFinCashInfo(param);
    }

    /**
     * 删除数据
     *
     * @param dto
     */
    public void delete(PeriodicManagement001Dto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("branch", dto.getBranch());
        map.put("periodId", dto.getPeriodId());
        String prodId = periodicManagementMapper.getProdIdByPeriodId(map);
        FinTradeProduct finTradeProduct = new FinTradeProduct();
        finTradeProduct.setFinprodId(prodId);
        finTradeProduct.setBranch(Long.valueOf(dto.getBranch()));
        finTradeProductMapper.deleteByPrimaryKey(finTradeProduct);

        FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
        finTradeProductAdd.setFinprodId(prodId);
        finTradeProductAdd.setBranch(Long.valueOf(dto.getBranch()));
        finTradeProductAddMapper.deleteByPrimaryKey(finTradeProductAdd);

        Map<String, Object> params = new HashMap<>();
        params.put("periodProdId", prodId);
        params.put("branch", Long.valueOf(dto.getBranch()));
        params.put("cashType1", DictCodeListExt1.现金流一级类型_费用.getValue());
        params.put("cashType2", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        List<FinCashInfo> finCashInfoList = periodicManagementMapper.selectFinCashInfo(params);
        if (!StringUtil.isEmptyList(finCashInfoList)) {
            String cashId = finCashInfoList.get(0).getCashId();
            periodicManagementMapper.deleteFinCashInfo(cashId);
            periodicManagementMapper.deleteFinCashPayRule(cashId);
            periodicManagementMapper.deleteFinCashCalcRule(cashId);
            periodicManagementMapper.deleteFloatCashCalcRule(cashId);
            periodicManagementMapper.deleteFloatCashCalcRuleDetail(cashId);
        }
        params.put("baseRuleType", DictCodeListExt1.业绩基准类型_产品披露.getValue());
        periodicManagementMapper.deleteFinBaseRuleInfo(params);
        params.put("baseRuleType", DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
        periodicManagementMapper.deleteFinBaseRuleInfo(params);
    }

    /**
     * 根据期限产品代码 ，分支序号查询是否存在交易
     *
     * @param param
     * @return
     */
    public int selectIsExistTrade(Map<String, Object> param) {
        return periodicManagementMapper.selectIsExistTrade(param);
    }

    public int checkPeriodId(Map<String, Object> param) {
        return periodicManagementMapper.checkPeriodId(param);
    }

    public FinBaseRuleInfo queryFinBaseRuleInfo(Map<String, Object> param) {
        return periodicManagementMapper.queryFinBaseRuleInfo(param);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public int getTrdProductDealByPeriodId(Map<String, Object> map) {
        return periodicManagementMapper.getTrdProductDealByPeriodId(map);
    }


    /**
     * 查询相同开始日和结束日是否存在
     */
    public int checkPeriodTradeDate(Map<String, Object> param) {
        return periodicManagementMapper.checkPeriodTradeDate(param);
    }

    /**
     * 检查流程中是否已存在：资产代码、开始日、结束日
     */
    public long getInWflProcessCount(Map<String, Object> param) {
        List<String> list = periodicManagementMapper.selectProcessingFinTradeProduct(param);
        long count = list.stream().filter(o -> isInWflProcess(param, o)).count();

        return count;
    }

    private boolean isInWflProcess(Map<String, Object> param, String businessData) {
        final String periodId = (String) param.get("periodId");
        PeriodicManagement001Dto dto = JsonUtils.json2Object(businessData, PeriodicManagement001Dto.class);
        if (StringUtil.isNotEmpty(periodId) && StringUtil.equals(periodId, dto.getPeriodId())) {
            return true;
        }
        final String startDate = (String) param.get("startDate");
        final String endDate = (String) param.get("endDate");
        if (StringUtil.equals(startDate, DateUtil.format(dto.getStartDate(), "yyyy-MM-dd"))
                && StringUtil.equals(endDate, DateUtil.format(dto.getEndDate(), "yyyy-MM-dd"))) {
            return true;
        }

        return false;
    }
}
