package com.joyintech.tams.exchange.service.trade.controller;

import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.ResponseResultBody;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.request.PortfolioInfoRequest;
import com.joyintech.tams.exchange.common.request.PortfolioPositionRequest;
import com.joyintech.tams.exchange.common.response.PortfolioAllocationResponse;
import com.joyintech.tams.exchange.common.response.PortfolioCalculateResponse;
import com.joyintech.tams.exchange.common.response.PortfolioInfoResponse;
import com.joyintech.tams.exchange.service.trade.management.service.PortfolioManager;
import com.joyintech.tams.exchange.util.MasterUtil;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author swwan
 * @date 2021/11/22 - 16:52
 */
@RestController
@RequestMapping("/trade/joyin/portfolio")
@ResponseResultBody
@Api(tags = "场内交易-组合查询接口")
public class PortfolioController {

    @Autowired
    private PortfolioManager portfolioManager;

    @PostMapping("/info")
    @ApiOperation(value = "查询组合信息", notes = "查询组合信息")
    public Message<List<BaseMap>> queryPortfolioInfo(@RequestBody PositionCashSecRequest bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(bean);
        }
        return new Message<>(portfolioManager.queryPortfolioInfo(bean));
    }

    @Deprecated
    @PostMapping
    @ApiOperation(value = "查询组合信息", notes = "查询组合信息")
    public Message<Map<String, PortfolioInfoResponse>> queryPortfolioInfo(@RequestBody @Validated PortfolioInfoRequest bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(bean);
        }
        return new Message<>(portfolioManager.queryPortfolioInfo(bean));
    }

    @PostMapping("/cash")
    @ApiOperation(value = "查询组合现金信息", notes = "查询组合现金信息")
    @ApiImplicitParams({
            @ApiImplicitParam(required = false, name = "portfolioId", value = "组合编号", paramType = "query"),
            @ApiImplicitParam(required = false, name = "acctNo", value = "资金账户编号", paramType = "query")
    })
    public Message<Map<String, PortfolioInfoResponse>> queryPortfolioCash(@RequestParam @NotBlank(message = "组合编号不可为空") String portfolioId,
                                                                          @RequestParam @NotBlank(message = "资金账户编号不可为空") String acctNo) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(null);
        }
        return new Message<>(portfolioManager.queryPortfolioCash(portfolioId, acctNo));
    }

    @PostMapping("/position")
    @ApiOperation(value = "查询组合持仓信息", notes = "查询组合持仓信息")
    public Message<Map<String, PortfolioInfoResponse>> queryPortfolioPosition(@RequestBody @Validated PortfolioPositionRequest request) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(request);
        }
        return new Message<>(portfolioManager.queryPortfolioPosition(request.getPortfolioId(), request.getFinprodId(), request.getSecAcctId(), request.getTradeMarket()));
    }

    @PostMapping(value = "/calculate")
    @ApiOperation(value = "查询组合计算信息", notes = "查询组合计算信息")
    public Message<List<PortfolioCalculateResponse>> queryPortfolioCalculate(@RequestBody @Validated OrderPlaceBean bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(bean);
        }
        try {
            return new Message<>(portfolioManager.queryPortfolioCalculate(bean));
        } catch (Exception e) {
            return new Message<List<PortfolioCalculateResponse>>().fail().setMessage(e.getMessage());
        }
    }

    @PostMapping(value = "/fairList")
    @ApiOperation(value = "查询公平指令信息", notes = "查询公平指令信息")
    public Message<PortfolioAllocationResponse> queryPortfolioCalculateWithFair(@RequestBody @Validated OrderPlaceBean bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(bean);
        }
        try {
            return new Message<>(portfolioManager.queryPortfolioCalculateWithFair(bean));
        } catch (Exception e) {
            return new Message<PortfolioAllocationResponse>().fail().setMessage(e.getMessage());
        }
    }
}