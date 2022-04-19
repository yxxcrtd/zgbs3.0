//package com.joyintech.tams.business.common.controller;
//
///**
// * 共通查询tab页
// * @author msk
// * @version 1.0 2021年12月27日
// * @see FieldEtrDispatcherController
// * @since 1.0
// */
//
//
//import com.joyintech.framework.base.BaseController;
//import com.joyintech.framework.grid.Page;
//import com.joyintech.tams.business.common.dto.*;
//import com.joyintech.tams.business.common.pojo.BaseMap;
//import com.joyintech.tams.business.common.pojo.Message;
//import com.joyintech.tams.business.common.service.EtrCommonTableService;
//import com.joyintech.tams.business.common.service.EtrTradeCommonervice;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/common/EtrTradeCommon")
//public class EtrTradeCommonController extends BaseController {
//    private static final Logger logger = LoggerFactory.getLogger(EtrTradeCommonController.class);
//
//    @Autowired
//    private EtrTradeCommonervice etrTradeCommonervice;
//
//    /**
//     *
//     * @Description // 头寸查询汇总
//     * @Param [params]
//     * @return java.util.Map<java.lang.String,java.lang.Object>
//     * @version 1.0
//     * @see
//     **/
//    @PostMapping("queryPositionInfo")
//    @ResponseBody
//    public List<BaseMap> queryPositionInfo(@RequestBody PositionCashSecRequest requestDto){
//        return etrTradeCommonervice.queryPositionInfo(requestDto);
//    }
//
//    /**
//     *
//     * @Description // 头寸查询汇总
//     * @Param [params]
//     * @return java.util.Map<java.lang.String,java.lang.Object>
//     * @version 1.0
//     * @see
//     **/
//    @PostMapping("queryPositionInfoSingle")
//    @ResponseBody
//    public List<BaseMap> queryPositionInfoSingle(@RequestBody PositionCashSecRequest requestDto){
//        return etrTradeCommonervice.queryPositionInfoSingle(requestDto);
//    }
//}
