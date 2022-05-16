package com.joyintech.tams.exchange.service.trade.controller;

import com.github.pagehelper.PageInfo;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.exchange.common.ResponseResultBody;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.TradeQueryBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.request.*;
import com.joyintech.tams.exchange.common.vo.TradeVo;
import com.joyintech.tams.exchange.service.trade.management.service.TradeManagement;
import com.joyintech.tams.exchange.util.MasterUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController("TradeController_dtx")
@RequestMapping("/trade/joyin")
@ResponseResultBody
@Api(tags = "场内交易-成交查询接口")
public class TradeController {

    @Autowired
    private TradeManagement tradeManagement;

    @ApiOperation(value = "查询成交信息", notes = "查询成交信息")
    @RequestMapping(value = "/trade", method = RequestMethod.POST)
    public Message<List<Trade>> queryTradeAll(@RequestBody @Validated TradeInfoRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return tradeManagement.queryTrade(request);
    }

    @ApiOperation(value = "查询历史成交信息", notes = "查询历史成交信息")
    @RequestMapping(value = "/trade/history", method = RequestMethod.POST)
    public Message<List<HistoryTrade>> queryHistoryTrade(@RequestBody @Validated HistoryTradeInfoRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return tradeManagement.queryHistoryTrade(request);
    }

    @ApiOperation(value = "查询分页成交信息", notes = "查询分页成交信息")
    @RequestMapping(value = "/trade/page", method = RequestMethod.POST)
    public Message<PageInfo<TradeQueryBean>> queryAllForPage(@RequestBody @Validated PageTradeInfoRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return tradeManagement.queryAllForPage(request);
    }

    @PostMapping(value = "/trade/export")
    @ResponseBody
    @ApiOperation(value = "导出成交信息列表", notes = "导出成交信息列表")
    public void exportTrade(@RequestBody TradeExportRequest request, HttpServletResponse response) {
        ExcelUitl.doExport(request, response, "成交列表", () -> tradeManagement.queryAllForPage(request.getDto(PageTradeInfoRequest.class)).getData().getList());
    }

    @ApiOperation(value = "根据委托编号查询成交信息", notes = "根据委托编号查询成交信息")
    @RequestMapping(value = "/tradeByEntrustId", method = RequestMethod.POST)
    public Message<List<Trade>> queryTradeByEntrustId(@RequestBody @Validated TradeInfoByIdRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return new Message<>(tradeManagement.queryTradeByEntrustId(request.getUserId(), request.getId()));
    }

    @ApiOperation(value = "根据订单编号查询成交信息", notes = "根据订单编号查询成交信息")
    @RequestMapping(value = "/tradeByOrderId", method = RequestMethod.POST)
    public Message<List<Trade>> queryTradeByOrderId(@RequestBody @Validated TradeInfoByIdRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return new Message<>(tradeManagement.queryTradeByOrderId(request.getUserId(), request.getId()));
    }

    @ApiOperation(value = "根据指令编号查询成交信息", notes = "根据指令编号查询成交信息")
    @RequestMapping(value = "/tradeByInstructionId", method = RequestMethod.POST)
    public Message<List<TradeVo>> queryTradeByInstructionId(@RequestBody @Validated TradeInfoByIdRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return new Message<>(tradeManagement.queryTradeByInstructionId(request.getUserId(), request.getId()));
    }

    @ApiOperation(value = "根据资金账户编号查询成交信息", notes = "根据资金账户编号查询成交信息")
    @RequestMapping(value = "/tradeByAccountId", method = RequestMethod.POST)
    public Message<List<Trade>> queryTradeByAccountId(@RequestBody @Validated TradeInfoByIdRequest request) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(request);
    	}
        return new Message<>(tradeManagement.queryTradeByAccountId(request.getUserId(), request.getId()));
    }
}
