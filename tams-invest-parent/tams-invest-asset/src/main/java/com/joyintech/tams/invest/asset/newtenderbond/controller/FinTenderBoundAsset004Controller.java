package com.joyintech.tams.invest.asset.newtenderbond.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.tams.invest.asset.newtenderbond.dto.FinTenderBoundAsset004Dto;
import com.joyintech.tams.invest.asset.newtenderbond.service.FinTenderBoundAsset004Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@PageId("FinTenderBoundAsset004")
@RequestMapping("/invest/asset/tenderBoundAsset004")
@Api(tags = "债券市场信息查询")
public class FinTenderBoundAsset004Controller extends BaseController {

    @Autowired
    private FinTenderBoundAsset004Service finTenderBoundAsset004Service;

    @ResponseBody
    @PostMapping("/getBoundMarketInfo")
    @ApiOperation(value = "债券市场信息查询", notes = "债券市场信息查询")
    public FinTenderBoundAsset004Dto getBoundMarketInfo(@RequestBody Map<String, Object> search) {
        return finTenderBoundAsset004Service.getBoundMarketInfo(search);
    }
}
