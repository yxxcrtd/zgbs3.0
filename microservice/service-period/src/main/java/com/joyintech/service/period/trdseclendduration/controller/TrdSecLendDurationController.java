package com.joyintech.service.period.trdseclendduration.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.period.trdseclendduration.service.TrdSecLendDurationService;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonWflParam;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto;

/**
 * 债券借贷存续期
 *
 * @author : 裴家俊
 * @version V1.0
 * @Project: microservice
 * @date 2019年7月1日
 */
@RestController("TrdSecLendDurationCtr_dtx")
@RequestMapping("/TrdSecLendDuration")
public class TrdSecLendDurationController {

    @Autowired
    private TrdSecLendDurationService trdSecLendDurationService;

    /**
     * 查询有效、撤销或待处理的存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<TrdSecLendDurationDto> findPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                             @PathVariable("length") int length) {
        return trdSecLendDurationService.findPage(page, length, param);
    }

    @PostMapping("getShowHandNum")
    public Map<String,Object> getShowHandNum(@RequestBody Map<String, Object> param) {
        return trdSecLendDurationService.getShowHandNum(param);
    }


    /**
     * 查询有效、撤销或待处理的存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("getBusiCode/{page}/{length}")
    public Page<SelectOptionDto> getBusiCode(@RequestBody Map<String, Object> param, @PathVariable("page") int page,
                                             @PathVariable("length") int length) {
        return trdSecLendDurationService.getBusiCode(page, length, param);
    }

    /**
     * 查询有效、撤销或待处理的存续期交易数据(不分页)
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("selectAllData")
    public List<TrdSecLendDurationDto> selectAllData(@RequestBody Map<String, Object> param) {
        return trdSecLendDurationService.selectAllData(param);
    }

    /**
     * 查询存续期交易数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findEffectiveData")
    public TrdSecLendDurationDto findEffectiveData(@RequestBody Map<String, Object> params) {
        return trdSecLendDurationService.findEffectiveData(params);
    }

    /**
     * 查询质押券信息
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findRepoInfo")
    public List<RepoInfo> findRepoInfo(@RequestBody Map<String, Object> params) {
        return trdSecLendDurationService.findRepoInfo(params);
    }

    /**
     * 查询现金流信息信息
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findFinCashInfo")
    public List<FinCashPlan> findFinCashInfo(@RequestBody String finprodId) {
        return trdSecLendDurationService.findFinCashInfo(finprodId);
    }

    /**
     * 插入数据，并删除流程表数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public String doComplete(@RequestBody TrdSecLendDurationDto trdRepoDurationDto) {
        return trdSecLendDurationService.doComplete(trdRepoDurationDto);
    }

    /**
     * 作废流程， 并删除流程表数据
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doDelete")
    public String doDelete(@RequestBody TrdCommonDeleteDto trdCommonDeleteDto) {
        return trdSecLendDurationService.doDelete(trdCommonDeleteDto);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("insertAutoClassifyBatch")
    public String insertAutoClassifyBatch(@RequestBody TrdCommonWflParam trdCommonWflParam) {
        return trdSecLendDurationService.insertAutoClassifyBatch(trdCommonWflParam);
    }
}
