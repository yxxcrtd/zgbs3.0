/*
 * 文件名：EtrSecLendController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrseclend.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.service.entrust.etrseclend.service.EtrSecLendService;

/**
 * 债券投资委托微服务controller
 * 〈功能详细描述〉
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLendController
 * @since 1.0
 */
@RestController("EtrSecLendCtr_dtx")
@RequestMapping("/EtrSecLend")
public class EtrSecLendController {

    /**
     * 债券投资委托微服务service
     */
    @Autowired
    private EtrSecLendService etrSecLendService;
    
    /**
     * 
     * 债券投资委托分页查询
     * <功能详细描述>
     * @param param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<EtrSecLendDto> selectForPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page, @PathVariable("length") int length) {
        return etrSecLendService.selectForPage(page,length,param);
    }
    
    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length) {
        return etrSecLendService.getFinprodId(page, length, param);
    }
    /**
     * 查询证券托管户：联想框文本框
     *
     * @param param 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    public List<SuggestDto> getSecManageAcctId(@RequestBody Map<String, Object> param) {
        return etrSecLendService.getSecManageAcctId(param);
    }
    /**
     * 
     * 操作前校验
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("check")
    public List<ValidMessage> check(@RequestBody EtrSecLendDto dto){
        return etrSecLendService.check(dto);
    }
    /**
     * 撤回委托 
     * <功能详细描述>
     * @param dto 实体类
     * @return boolean
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("recall")
    public boolean recallDelegate(@RequestBody EtrSecLendDto dto) throws IllegalAccessException, InvocationTargetException{
        return etrSecLendService.recallDelegate(dto);
    }
    /**
     * 关闭委托 
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("close")
    public boolean closeDelegate(String entrustId,String closeWay) {
        return etrSecLendService.closeDelegate(entrustId, closeWay);
    }
    /**
     * 
     * 通过主键查询数据
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    @GetMapping("selectByKey")
    public EtrSecLendDto selectByKey(String entrustId,String finprodId,String portfolioId) {
        return etrSecLendService.selectByKey(entrustId,finprodId,portfolioId);
    }
    /**
     * 
     * 工作流完成方法
     * <功能详细描述>
     * @param map
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody EtrSecLendDto dto) throws IllegalAccessException, InvocationTargetException{
        etrSecLendService.doComplete(dto, dto.getOperateType());
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doInsert")
    public void doInsert(@RequestBody EtrSecLendDto dto){
        etrSecLendService.doInsert(dto, dto.getOperateType());
    }
    
    /**
     * 删除流程中数据和流水
     * @param dto
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doDeleteFlow")
    public void doDeleteFlow(@RequestBody EtrSecLendDto dto) {
    	etrSecLendService.doDeleteFlow(dto);
    }

    /**
     * 删除流程中数据和流水
     * @param dto
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doDeleteWfl")
    public void doDeleteWfl(@RequestBody EtrSecLendDto dto) {
        etrSecLendService.doDeleteWfl(dto);
    }


    /**
     * 
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("delete")
    public boolean deleteDelegate(String entrustId) {
        return etrSecLendService.deleteDelegate(entrustId);
    }
    /**
     * 
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getAssetAvailablePosition")
    public Map<String, Object> getAssetAvailablePosition(@RequestBody Map<String, Object> param) {
        return etrSecLendService.getAssetAvailablePosition(param);
    }


    /**
     * 退回更新流程,删除流水
     * @param dto
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doBackFlow")
    public void doBackFlow(@RequestBody EtrSecLendDto dto) {
        etrSecLendService.doBackFlow(dto);
    }
}
