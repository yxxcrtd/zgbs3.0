package com.joyintech.tams.business.common.controller;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.tams.business.common.constant.Constant;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.CfgDataTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common/config/dataTable")
@Api(tags = "页面展示设置")
public class CfgDataTableController {

    private static final Logger logger = LoggerFactory.getLogger(CfgDataTableController.class);

    @Autowired
    private CfgDataTableService cfgDataTableService;

    @ApiOperation(value = "查询页面设置", notes = "查询页面设置")
    @PostMapping("/getAllDataTable/{pageCode}")
    public Message<Map<String, List<CfgDataTableShow>>> getAllDataTable(@PathVariable("pageCode") String pageCode) {
        try {
            if (StringUtil.isEmpty(pageCode)) {
                return new Message<Map<String, List<CfgDataTableShow>>>().setMessage(Constant.MESSAGE_PAGECODE_NULL);
            }
            return new Message<>(cfgDataTableService.getByPageCode(pageCode));
        } catch (Exception e) {
            logger.error("查询页面设置", e);
            return new Message<Map<String, List<CfgDataTableShow>>>().fail();
        }
    }

    @ApiOperation(value = "根据pageCode，dtCode和spcdCode查询页面设置", notes = "根据pageCode，dtCode和spcdCode查询页面设置")
    @PostMapping("/getByPageCodeAndDtCode/{pageCode}/{dtCode}/{spcdCode}")
    public Message<List<CfgDataTableShow>> getByPageCodeAndDtCode(@PathVariable("pageCode") String pageCode,
    		                                                      @PathVariable("dtCode") String dtCode,
                                                                  @PathVariable("spcdCode") String spcdCode) {
        try {
            if (StringUtil.isEmpty(pageCode) || StringUtil.isEmpty(dtCode)) {
                return new Message<List<CfgDataTableShow>>().setMessage(Constant.MESSAGE_PAGECODE_NULL);
            }
            return new Message<>(cfgDataTableService.getByPageCodeAndDtCode(pageCode,dtCode,spcdCode));
        } catch (Exception e) {
            logger.error("查询页面设置", e);
            return new Message<List<CfgDataTableShow>>().fail().setMessage(Constant.GET_DATATABLE_ERROR);
        }
    }

    @ApiOperation(value = "页面设置", notes = "页面设置")
    @PostMapping("/setDataTable")
    public Message<String> setDataTable(@RequestBody List<CfgDataTableShow> dataTableShowList) {
        try {
            cfgDataTableService.setDataTable(dataTableShowList);
            return new Message<String>().success();
        } catch (Exception e) {
            logger.error("页面设置失败", e);
            return new Message<String>().fail().setMessage(Constant.SET_DATATABLE_ERROR);
        }
    }

    @ApiOperation(value = "根据pageCode，dtCode和spcdCode列表 查询页面设置", notes = "根据pageCode，dtCode和spcdCode列表查询页面设置")
    @PostMapping("/getByPageCodeAndDtCodes/{pageCode}/{dtCode}/{spcdCodes}")
    public Message<List<CfgDataTableShow>> getByPageCodeAndDtCodes(@PathVariable("pageCode") String pageCode,
                                                                   @PathVariable("dtCode") String dtCode,
                                                                   @PathVariable("spcdCodes") String spcdCodes) {
        List<String> spcdCodeList = new ArrayList<>(Arrays.asList(spcdCodes.split(",")));
        try {
            if (StringUtil.isEmpty(pageCode) || StringUtil.isEmpty(dtCode)) {
                return new Message<List<CfgDataTableShow>>().setMessage(Constant.MESSAGE_PAGECODE_NULL);
            }
            return new Message<>(cfgDataTableService.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList));
        } catch (Exception e) {
            logger.error("查询页面设置", e);
            return new Message<List<CfgDataTableShow>>().fail().setMessage(Constant.GET_DATATABLE_ERROR);
        }
    }
}
