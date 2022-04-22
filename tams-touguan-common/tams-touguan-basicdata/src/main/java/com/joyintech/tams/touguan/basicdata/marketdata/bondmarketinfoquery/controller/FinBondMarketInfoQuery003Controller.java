package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service.FinBondMarketInfoQuery001Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@PageId("FinBondMarketInfoQuery001")
@RequestMapping("/asset/finBondMarketInfoQuery003")
@Api(tags = "债券市场信息加券")
public class FinBondMarketInfoQuery003Controller extends BaseController {

    @Autowired
    private FinBondMarketInfoQuery001Service finBondMarketInfoQuery001Service;

    @ResponseBody
    @PostMapping("/addCoupons")
    @ApiOperation(value = "加券按钮", notes = "加券按钮")
    public Message<AjaxResult> addCoupons(@RequestParam("secId") String secId) {
        List<ValidMessage> validMessageList = finBondMarketInfoQuery001Service.addCoupons(secId);
        if (CollectionUtils.isNotEmpty(validMessageList)) {
            return Message.fail("加券失败", validMessageList);
        } else {
            return Message.success("加券成功", null);
        }
    }
}
