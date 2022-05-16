package com.joyintech.tams.exchange.service.trade.controller;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.exchange.common.ResponseResultBody;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.EntrustBsProp;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.request.EntrustExportRequest;
import com.joyintech.tams.exchange.common.request.EntrustQueryRequest;
import com.joyintech.tams.exchange.common.request.PageEntrustInfoRequest;
import com.joyintech.tams.exchange.common.vo.IntegratedEntrustVo;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.service.trade.management.service.EntrustManagement;
import com.joyintech.tams.exchange.util.MasterUtil;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/trade/joyin")
@ResponseResultBody
@Api(tags = "场内交易-委托操作接口")
public class EntrustController {

    @Autowired
    private EntrustManagement entrustManagement;
    @Autowired
    private InstructionService instructionService;

    @ApiOperation(value = "查询委托信息", notes = "查询委托信息")
    @PostMapping(value = {"/entrust"})
    public Message<OrderAndEntrustQryResult> queryAll(@RequestBody @Validated EntrustQueryRequest entrustQueryRequest) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(entrustQueryRequest);
    	}
        return new Message<>(entrustManagement.queryEntrust(entrustQueryRequest.getUserId(), entrustQueryRequest.getInstructionId(), entrustQueryRequest.getAccountId(), entrustQueryRequest.getOrderId()));
    }

    @ApiOperation(value = "查询综合业务信息", notes = "查询综合业务信息")
    @PostMapping(value = {"/entrust/integrated"})
    public Message<List<IntegratedEntrustVo>> queryIntegratedEntrustAll(@RequestBody @Validated EntrustQueryRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return new Message<>(entrustManagement.queryIntegratedEntrust(request.getUserId(), request.getInstructionId(), request.getAccountId(), request.getOrderId()));
    }

    /**
     * 撤销user下对应entrust
     *
     * @param userId    用户Id
     * @param entrustId 委托单号
     * @return Message
     */
    @ApiOperation(value = "撤销委托", notes = "撤销委托")
    @PostMapping(value = "/entrust/cancel")
    public Message<Object> cancelEntrust(@RequestParam String userId,
                                         @RequestParam String entrustId) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(null);
    	}
        return new Message<>(entrustManagement.cancelEntrust(userId, entrustId));
    }

    /**
     * 撤销user下对应entrust  //撤销审批，对应revokecontroller,指令中有是否需要复核参数，决定是否走这个revoke
     *
     * @param userId 用户Id
     * @param dto    委托单号
     * @return ResultBean
     */
    @PostMapping(value = "/entrust/cancel/revoke")
    public ResultBean cancelEntrustWithRevoke(String userId, @RequestBody RevokeDto dto) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObjectResultBean(dto);
    	}
        return entrustManagement.cancelEntrustWithRevoke(userId, dto);
    }

//    /**
//     * 最大交易数量查询
//     *
//     * @param userId        用户Id
//     * @param instructionId 指令编号
//     * @param stockCode     市场.证券代码
//     * @param entrustProp   交易类型
//     * @param entrustBs     交易方向
//     * @param entrustPrice  委托价格
//     * @param accountId     账户Id
//     * @return ResultBean
//     */
//    @PostMapping(value = "/entrust/enable", method = RequestMethod.GET)
//    public ResultBean queryEnable(@RequestParam(value = "user_id") String userId,
//                                  @RequestParam(value = "stock_code") String stockCode,
//                                  @RequestParam(value = "entrust_prop") String entrustProp,
//                                  @RequestParam(value = "entrust_bs") String entrustBs,
//                                  @RequestParam(value = "entrust_price") double entrustPrice,
//                                  @RequestParam(value = "account_id") String accountId) {
//        return entrustManagement.queryEnable(userId, stockCode, entrustProp, entrustBs, entrustPrice, accountId);
//    }

    /**
     * 最大交易数量查询
     *
     * @param enableQryBean 查询参数
     * @return ResultBean
     */
    @PostMapping(value = "/entrust/enable")
    public ResultBean queryEnable(@RequestBody @Validated EnableQryBean enableQryBean) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObjectResultBean(enableQryBean);
    	}
        Instruction instruction = instructionService.findById(enableQryBean.getInstructionId());
        enableQryBean.setEntrustProp(EntrustPropUtils.getEntrustProp(instruction, enableQryBean.getEntrustProp()));
        enableQryBean.setEntrustBs(EntrustBsProp.getEntrustBs(instruction.getPs()));
        if (StringUtils.isNotEmpty(enableQryBean.getStockCode())) {
            enableQryBean.setStockCode(instruction.getFinprodMarketId());
        }
        if (StringUtils.isNotEmpty(enableQryBean.getAccountId())) {
            enableQryBean.setAccountId(instruction.getAcctNo());
        }
        return entrustManagement.queryEnable(enableQryBean);
    }

    /*** 
     * @Description: 申报代码获取
     * @Param: [finprodId, othCodeType]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/6/5 
     */
    @PostMapping(value = "/entrust/getOthCode")
    public ResultBean getOthCode(@RequestParam(value = "finprod_id", required = false) String finprodId,
                                 @RequestParam(value = "oth_code_type", required = false) String othCodeType) {
        return entrustManagement.getOthCode(finprodId, othCodeType);
    }

    @ApiOperation(value = "查询分页委托信息", notes = "查询分页委托信息")
    @PostMapping(value = "/entrust/page")
    public Message<PageInfo<EntrustQueryBean>> queryAllEntrustForPage(@RequestBody @Validated PageEntrustInfoRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return entrustManagement.queryAllEntrustForPage(request);
    }

    @ApiOperation(value = "查询分页综合业务委托信息", notes = "查询分页综合业务委托信息")
    @PostMapping(value = "/entrust/integrated/page")
    public Message<PageInfo<EntrustQueryBean>> queryIntegratedEntrustForPage(@RequestBody @Validated PageEntrustInfoRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return entrustManagement.queryIntegratedEntrustForPage(request);
    }

    @ApiOperation(value = "导出委托信息列表", notes = "导出委托信息列表")
    @PostMapping(value = "/entrust/export")
    public void exportEntrust(@RequestBody EntrustExportRequest request, HttpServletResponse response) {
        ExcelUitl.doExport(request, response, "委托列表", () -> entrustManagement.queryAllEntrustForPage(request.getDto(PageEntrustInfoRequest.class)).getData().getList());
    }

    @ApiOperation(value = "查询异常委托", notes = "查询异常委托")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "userId", value = "用户编号", paramType = "query"),
            @ApiImplicitParam(required = false, name = "entrustStatusList", value = "委托状态列表", paramType = "body")
    })
    @PostMapping(value = "/entrust/exception")
    public Message<List<EntrustInterface>> queryExceptionEntrust(@RequestParam @NotBlank(message = "用户编号不可为空") String userId,
                                                                 @RequestBody(required = false) List<String> entrustStatusList) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(entrustStatusList);
    	}
        String entrustStatus = "1,3,4";
        if (CollUtil.isNotEmpty(entrustStatusList)) {
            entrustStatus = CollUtil.join(entrustStatusList, GlobalConstant.COMMA);
        }
        return new Message<>(entrustManagement.queryEntrustByStatus(userId, entrustStatus));
    }

    @ApiOperation(value = "撤销异常委托", notes = "撤销异常委托")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "userId", value = "用户编号", paramType = "query"),
            @ApiImplicitParam(required = true, name = "userName", value = "用户名称", paramType = "query"),
            @ApiImplicitParam(required = true, name = "entrustId", value = "委托编号", paramType = "query"),
    })
    @PostMapping(value = "/entrust/exception/cancel")
    public Message<Object> cancelExceptionEntrust(@RequestParam @NotBlank(message = "用户编号不可为空") String userId,
                                                  @RequestParam @NotBlank(message = "用户名称不可为空") String userName,
                                                  @RequestParam @NotBlank(message = "委托编号不可为空") String entrustId) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(null);
    	}
        return new Message<>(entrustManagement.cancelExceptionEntrust(userId, userName, entrustId));
    }

//    @ApiOperation(value = "导出异常委托信息列表", notes = "导出异常委托信息列表")
//    @PostMapping(value = "/entrust/exception/export")
//    public void exportExceptionEntrust(@RequestBody EntrustExportRequest request, HttpServletResponse response) {
//        ExcelUitl.doExport(request, response, "异常委托列表", () -> entrustManagement.queryEntrustByStatus(request.getDto(PageEntrustInfoRequest.class)).getData().getList());
//    }
}
