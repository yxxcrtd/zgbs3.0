/*
 * 文件名：MstBondQuotationInfoController.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：wangzehui
 * 修改时间：2018年7月17日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.dayendaccount.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.dayendaccount.dto.AccountResult;
import com.joyintech.service.basicdata.dayendaccount.dto.DayEndAccount002Dto;
import com.joyintech.service.basicdata.dayendaccount.dto.DayEndAccountDto;
import com.joyintech.service.basicdata.dayendaccount.service.DayEndAccountService;
import com.joyintech.service.basicdata.mstbondquotationinfo.dto.MstBondQuotationInfoDto;
import com.joyintech.service.basicdata.mstbondquotationinfo.service.MstBondQuotationInfoImportDataService;
import com.joyintech.service.basicdata.mstbondquotationinfo.service.MstBondQuotationInfoService;
import com.joyintech.service.basicdata.mstcustomernature.dto.MstCustomerNatureDto;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 日终核对
 * 
 * @author chengzhong
 * @version 1.0 2021年4月20日
 * @see DayEndAccountController
 * @since 1.0
 */
@RestController("DayEndAccountCtr_dtx")
@RequestMapping("/DayEndAccount")
public class DayEndAccountController {

    /**
     * 
     */
    @Autowired
    private DayEndAccountService dayEndAccountService;

    @RequestMapping("selectForPage/{page}/{length}")
    public Page<DayEndAccountDto> selectForPage(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page,
                                                @PathVariable("length") int length) throws Exception {
        return dayEndAccountService.selectForPage(page, length, param);
    }

    @RequestMapping("queryAssetCode/{page}/{length}")
    public Page<SelectOptionDto> queryAssetCode(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page,
                                                @PathVariable("length") int length) {
        return dayEndAccountService.queryAssetCode(page, length, param);
    }

    @RequestMapping("qryAccount")
    public AccountResult qryAccount(@RequestBody List<DayEndAccountDto> dtoList) {
        return dayEndAccountService.qryAccount(dtoList);
    }
    @RequestMapping("getReconDealInfo")
    public DayEndAccount002Dto getReconDealInfo(@RequestBody Map<String, Object> param) {
        return dayEndAccountService.getReconDealInfo(param);
    }

//    @RequestMapping("getDealDetailInfo")
//    public DayEndAccount002Dto getDealDetailInfo(@RequestBody Map<String, Object> param) {
//        return dayEndAccountService.getDealDetailInfo(param);
//    }

    /**
     *
     * 工作流完成操作
     * <功能详细描述>
     * @param entity MstCustomerNatureDto
     * @param type 类型
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @RequestMapping("/doComplete/{type}")
    public void doComplete(@RequestBody DayEndAccount002Dto entity, @PathVariable("type") String type) {
        dayEndAccountService.doComplete(entity, type);
    }

    @RequestMapping("selectCustomerId/{page}/{length}")
    public Page<SelectOptionDto> selectCustomerId(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page,
                                                 @PathVariable("length") int length) {
        return dayEndAccountService.selectCustomerId(page,length,param);
    }

    @RequestMapping("queryBusiAcctNo/{page}/{length}")
    public Page<SelectOptionDto> queryBusiAcctNo(@RequestBody Map<String, Object> param,
                                                  @PathVariable("page") int page,
                                                  @PathVariable("length") int length) {
        return dayEndAccountService.queryBusiAcctNo(page,length,param);
    }

    @RequestMapping("selectPage/{page}/{length}")
    public Page<DayEndAccountDto> selectPage(@RequestBody Map<String, Object> param,
                                                @PathVariable("page") int page,
                                                @PathVariable("length") int length) throws Exception {
        return dayEndAccountService.selectPage(page, length, param);
    }

    @RequestMapping("selectSecAcctId/{page}/{length}")
    public Page<SelectOptionDto> selectSecAcctId(@RequestBody Map<String, Object> param,
                                                 @PathVariable("page") int page,
                                                 @PathVariable("length") int length) {
        return dayEndAccountService.selectSecAcctId(page,length,param);
    }
}
