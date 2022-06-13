package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.service.FinListedBondInfoQuery001Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PageId("FinListedBondInfoQuery003")
@RequestMapping("/asset/finListedBondInfoQuery003")
@Api(tags = "待上市债券信息加券")
public class FinListedBondInfoQuery003Controller extends BaseController {

    @Autowired
    private FinListedBondInfoQuery001Service finListedBondInfoQuery001Service;

    @ResponseBody
    @PostMapping("/addCoupons")
    @ApiOperation(value = "加券按钮", notes = "加券按钮")
    public Message<AjaxResult> addCoupons(@RequestParam("secId") String secId) {
        AjaxResult ajaxResult = finListedBondInfoQuery001Service.addCoupons(secId);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

}
