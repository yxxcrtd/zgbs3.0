package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.contoller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bondfirstbid.controller.CfetsEtrBondFirstBid001Controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 一级投标确认 初始化页面
 *
 * @Author: kchen
 * @Date: 2022/5/14 11:07
 */
@Controller
@PageId("CfetsTrdFirstBidFirm001")
@RequestMapping("/cfets/finbond/tenderBondSub/CfetsTrdFirstBidFirm001")
@Api(tags = "CFETS一级投标确认-常用查询类接口")
public class CfetsTrdFirstBidFirm001Controller extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrBondFirstBid001Controller.class);

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        //pageId
        initPageParams.put("pageId", this.getPageId());
        return new Message<Map<String, Object>>(initPageParams).success();

    }

}
