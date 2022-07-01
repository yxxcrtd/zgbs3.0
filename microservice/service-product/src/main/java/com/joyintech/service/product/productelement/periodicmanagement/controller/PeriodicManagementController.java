package com.joyintech.service.product.productelement.periodicmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinBaseRuleDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.FinProductFloatFeeDto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.service.product.productelement.periodicmanagement.dto.PeriodicManagementDto;
import com.joyintech.service.product.productelement.periodicmanagement.service.PeriodicManagementService;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRule;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;

@RestController("backPeriodicManagementController")
@RequestMapping("/PeriodicManagement")
public class PeriodicManagementController {

    @Autowired
    private PeriodicManagementService periodicManagementService;

    @PostMapping("getFinprodIds/{page}/{length}")
    public Page<SelectOptionDto> getFinprodIds(@RequestBody Map<String, Object> param,
                                               @PathVariable("page") int page, @PathVariable("length") int length) {
        return periodicManagementService.getFinprodIds(page, length, param);
    }

    /**
     * 画面查询方法
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("selectForPage/{page}/{length}")
    public Page<PeriodicManagement001Dto> selectForPage(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length) {
        return periodicManagementService.selectForPage(page, length, param);
    }

    @PostMapping("getInvestCycle/{page}/{length}")
    public Page<SelectOptionDto> getInvestCycle(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page, @PathVariable("length") int length) {
        return periodicManagementService.getInvestCycle(page, length, param);
    }

    /**
     * 查询基本信息
     *
     * @param param
     * @return
     */
    @PostMapping("selectBaseInfoList")
    public List<PeriodicManagementDto> selectBaseInfoList(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectBaseInfoList(param);
    }

    /**
     * 查询业绩基准信息
     *
     * @param param
     * @return
     */
    @PostMapping("selectBenchMarkInfo")
    public FinBaseRuleDto selectBenchMarkInfo(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectBenchMarkInfo(param);
    }

    /**
     * 查询浮动管理费
     *
     * @param param
     * @return
     */
    @PostMapping("selectFloatFeeInfo")
    public FinProductFloatFeeDto selectFloatFeeInfo(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectFloatFeeInfo(param);
    }

    /**
     * 查询浮动管理费列表
     *
     * @param param
     * @return
     */
    @PostMapping("selectFloatFeeList")
    public List<FinFloatCalcRuleDetail> selectFloatFeeList(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectFloatFeeList(param);
    }

    /**
     * 查询期限产品代码
     *
     * @param param
     * @return
     */
    @PostMapping("selectPeriodProdId")
    public PeriodicManagementDto selectPeriodProdId(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectPeriodProdId(param);
    }

    /**
     * 查询现金流支付规则
     *
     * @param param
     * @return
     */
    @PostMapping("selectCashPayRule")
    public FinCashPayRule selectCashPayRule(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectCashPayRule(param);
    }

    /**
     * 查询现金流计算规则
     *
     * @param param
     * @return
     */
    @PostMapping("selectCashCalcRule")
    public FinCashCalcRule selectCashCalcRule(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectCashCalcRule(param);
    }

    /**
     * 查询业绩报酬计算规则
     *
     * @param param
     * @return
     */
    @PostMapping("selectFinFloatCalcRule")
    public FinFloatCalcRule selectFinFloatCalcRule(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectFinFloatCalcRule(param);
    }


    /**
     * 查询业绩报酬计算规则明细
     *
     * @param param
     * @return
     */
    @PostMapping("selectFinFloatCalcRuleDetail")
    public List<FinFloatCalcRuleDetail> selectFinFloatCalcRuleDetail(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectFinFloatCalcRuleDetail(param);
    }

    @PostMapping("selectFinCashInfo")
    public List<FinCashInfo> selectFinCashInfo(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectFinCashInfo(param);
    }

    /**
     * 删除流程生效
     *
     * @param dto
     */
    @PostMapping("doCompeleteDelete")
    @Transactional
    @DTXTransaction
    public void doCompeleteDelete(@RequestBody PeriodicManagement001Dto dto) {
        periodicManagementService.delete(dto);
    }

    /**
     * 根据期限产品代码 ，分支序号查询是否存在交易
     *
     * @param param
     * @return
     */
    @PostMapping("selectIsExistTrade")
    public int selectIsExistTrade(@RequestBody Map<String, Object> param) {
        return periodicManagementService.selectIsExistTrade(param);
    }

    /**
     * 查询期次代码是否存在
     *
     * @param param
     * @return
     */
    @PostMapping("checkPeriodId")
    public int checkPeriodId(@RequestBody Map<String, Object> param) {
        return periodicManagementService.checkPeriodId(param);
    }

    @PostMapping("queryFinBaseRuleInfo")
    public FinBaseRuleInfo queryFinBaseRuleInfo(@RequestBody Map<String, Object> param) {
        return periodicManagementService.queryFinBaseRuleInfo(param);
    }

    @PostMapping("getTrdProductDealByPeriodId")
    public int getTrdProductDealByPeriodId(@RequestBody Map<String, Object> map) {
        return periodicManagementService.getTrdProductDealByPeriodId(map);
    }

    /**
     * 查询相同开始日和结束日是否存在
     */
    @PostMapping("checkPeriodTradeDate")
    public int checkPeriodTradeDate(@RequestBody Map<String, Object> param) {
        return periodicManagementService.checkPeriodTradeDate(param);
    }

    /**
     * 新建期次管理数据在流程中是否已存在
     */
    @PostMapping("getInWflProcessCount")
    public long getInWflProcessCount(@RequestBody Map<String, Object> param) {
        return periodicManagementService.getInWflProcessCount(param);
    }
}
