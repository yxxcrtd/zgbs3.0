package com.joyintech.tams.cfets.etrib.bond.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.CfgDataTableService;
import com.joyintech.tams.cfets.etrib.bond.dto.CfetsEtrFinProductInfoResponse;
import com.joyintech.tams.cfets.etrib.bond.service.CfetsEtrFinProductInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 银行间债券信息
 *
 * @author zhuys
 * @version 1.0 2022年04月14日
 * @see CfetsEtrIbBondProductInfoController
 * @since 1.0
 */
@Controller
@PageId("CfetsEtrBondProductInfo")
@RequestMapping("/cfets/etrib/cfetsEtrBondProductInfo")
@Api(tags = "银行间债券信息")
public class CfetsEtrIbBondProductInfoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrIbBondProductInfoController.class);

    @Autowired
    private CfetsEtrFinProductInfoService cfetsEtrFinProductInfoService;

    @PostMapping("/getEtrFinProductInfo/{pageCode}/{dtCode}/{secId:.+}")
    @ApiOperation(value = "债券信息查询功能", notes = "债券信息查询功能")
    @ResponseBody
    public Message<CfetsEtrFinProductInfoResponse> getEtrFinProductInfo(@PathVariable("pageCode") String pageCode, @PathVariable("dtCode") String dtCode, @PathVariable String secId) {
        try {
            //查询债券信息
            CfetsEtrFinProductInfoResponse finProductInfo = cfetsEtrFinProductInfoService.findFinProductInfo(pageCode, dtCode, secId);
            return new Message<>(finProductInfo).success().setData(finProductInfo);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<CfetsEtrFinProductInfoResponse>().fail().setMessage("查询异常");
        }
    }

}