package com.joyintech.tams.exchange.service.trade.controller;

import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveRequest;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.request.EntrustQueryRequest;
import com.joyintech.tams.exchange.common.request.QueryTickRequest;
import com.joyintech.tams.exchange.service.trade.management.service.IntegratedQuoteManagement;
import com.joyintech.tams.exchange.util.MasterUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/joyin")
@Api(tags = "场内交易-指令行情查询接口")
public class InstructionQuoteController {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(InstructionQuoteController.class);

    @Autowired
    private QuoteService quoteService;
    @Autowired
    private IntegratedQuoteManagement integratedQuoteManagement;

    @ApiOperation(value = "竞价行情查询接口", notes = "竞价行情查询接口")
    @PostMapping(value = {"/quote/tick"})
    public ResultBean queryTick(@RequestBody @Validated QueryTickRequest queryTickRequest) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(queryTickRequest);
        }
        return quoteService.getTick(queryTickRequest.getStockCode());
    }

    @ApiOperation(value = "大宗交易行情查询接口", notes = "大宗交易行情查询接口")
    @GetMapping(value = "/entrust/integrated/bulk_quote")
    public ResultBean queryIntegratedBulkQuote(@RequestParam(value = "stock_code") String stockCode,
                                               @RequestParam(value = "entrust_prop") String entrustProp,
                                               @RequestParam(value = "entrust_bs") String entrustBs,
                                               @RequestParam(value = "account_id") String accountId,
                                               @RequestParam(value = "page_size", required = false, defaultValue = "0") int pageSize) {
        IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean = new IntegratedBulkQuoteQryBean(stockCode, entrustProp, entrustBs, accountId, pageSize);
        if (!MasterUtil.isMaster()) {
            return MasterUtil.getForObjectResultBean();
        }
        return integratedQuoteManagement.queryIntegratedBulkQuote(integratedBulkQuoteQryBean);
    }

//    @ApiOperation(value = "大宗交易行情查询接口", notes = "大宗交易行情查询接口")
//    @GetMapping(value = "/entrust/integrated/bulk_quote")
//    public ResultBean queryIntegratedBulkQuote(@RequestBody @Validated IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) {
//        return integratedQuoteManagement.queryIntegratedBulkQuote(integratedBulkQuoteQryBean);
//    }

    @ApiOperation(value = "盘后大宗行情查询", notes = "盘后大宗行情查询")
    @GetMapping(value = "/entrust/integrated/bulk_close_quote")
    public ResultBean queryIntegratedBulkCloseQuote(@RequestParam(value = "stock_code") String stockCode,
                                                    @RequestParam(value = "exchange_type") String exchangeType,
                                                    @RequestParam(value = "account_id") String accountId,
                                                    @RequestParam(value = "page_size", required = false, defaultValue = "0") int pageSize) {
        IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean = new IntegratedBulkCloseQuoteQryBean(stockCode, exchangeType, accountId, pageSize);
        if (!MasterUtil.isMaster()) {
            return MasterUtil.getForObjectResultBean();
        }
        return integratedQuoteManagement.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean);
    }

//    @ApiOperation(value = "盘后大宗行情查询", notes = "盘后大宗行情查询")
//    @GetMapping(value = "/entrust/integrated/bulk_close_quote")
//    public ResultBean queryIntegratedBulkCloseQuote(@RequestBody @Validated IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) {
//        return integratedQuoteManagement.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean);
//    }

    @ApiOperation(value = "质押式协议回购行情查询", notes = "质押式协议回购行情查询")
    @GetMapping(value = "/entrust/integrated/repurchase_quote")
    public ResultBean queryIntegratedRepurchaseQuote(@RequestParam(value = "action_in") int actionIn,
                                                     @RequestParam(value = "entrust_prop", required = false, defaultValue = "") String entrustProp,
                                                     @RequestParam(value = "account_id", required = false) String accountId,
                                                     @RequestParam(value = "page_size", required = false, defaultValue = "1000") int pageSize) {
        IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean = new IntegratedRepurchaseQuoteQryBean(actionIn, entrustProp, accountId, pageSize);
        if (!MasterUtil.isMaster()) {
            return MasterUtil.getForObjectResultBean();
        }
        return integratedQuoteManagement.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean);
    }

//    @ApiOperation(value = "质押式协议回购行情查询", notes = "质押式协议回购行情查询")
//    @GetMapping(value = "/entrust/integrated/repurchase_quote")
//    public ResultBean queryIntegratedRepurchaseQuote(@RequestBody @Validated IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) {
//        return integratedQuoteManagement.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean);
//    }

    @ApiOperation(value = "上海固收行情查询", notes = "上海固收行情查询")
    @GetMapping(value = "/entrust/integrated/fixed_income_quote")
    public ResultBean queryIntegratedFixedIncomeQuote(@RequestParam(value = "stock_code") String stockCode,
                                                      @RequestParam(value = "exchange_type") String exchangeType,
                                                      @RequestParam(value = "account_id") String accountId,
                                                      @RequestParam(value = "page_size", required = false, defaultValue = "0") int pageSize) {
        IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean = new IntegratedFixedIncomeQuoteQryBean(stockCode, exchangeType, accountId, pageSize);
        if (!MasterUtil.isMaster()) {
            return MasterUtil.getForObjectResultBean();
        }
        return integratedQuoteManagement.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean);
    }


//    @ApiOperation(value = "上海固收行情查询", notes = "上海固收行情查询")
//    @GetMapping(value = "/entrust/integrated/fixed_income_quote")
//    public Message<List<IntegratedFixedIncomeQuoteBean>> queryIntegratedFixedIncomeQuote(@RequestBody @Validated IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) {
//        return new Message<List<IntegratedFixedIncomeQuoteBean>>(integratedQuoteManagement.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean));
//    }

    @ApiOperation(value = "综合业务行情查询接口", notes = "综合业务行情查询接口")
    @GetMapping(value = "/entrust/integrated/quote")
    public ResultBean queryIntegratedQuote(@RequestParam(value = "user_id", required = false) String userId,
                                           @RequestParam(value = "quote_type") int quoteType,
                                           @RequestParam(value = "account_id", required = false) String accountId,
                                           @RequestParam(value = "stock_code", required = false) String stockCode,
                                           @RequestParam(value = "opposite_liaisonTel", required = false) String oppositeLiaisonTel,
                                           @RequestParam(value = "liaisonTel", required = false) String liaisonTel,
                                           @RequestParam(value = "page_size", required = false, defaultValue = "0") int pageSize,
                                           @RequestParam(value = "posStr", required = false, defaultValue = "") String posStr) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.getForObjectResultBean();
        }
        return integratedQuoteManagement.queryIntegratedQuote(userId, quoteType, stockCode, accountId, oppositeLiaisonTel, liaisonTel, pageSize, posStr);
    }

    /**
     * 手动增加协议回购mock行情
     */
    @ApiOperation(value = "手动增加协议回购mock行情", notes = "手动增加协议回购mock行情", hidden = true)
    @PostMapping(value = "/add/integrated/repurchase_quote")
    public ResultBean addIntegratedRepurchaseQuote(@RequestBody IntegratedRepurchaseQuoteBean repurchaseQuoteBean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return integratedQuoteManagement.addIntegratedRepurchaseQuote(repurchaseQuoteBean);
    }

    @ApiOperation(value = "我收到的协商成交行情接口(深交所)", notes = "我收到的协商成交行情接口(深交所)")
    @PostMapping(value = "/entrust/integrated/negotiateQuoteReceive")
    public Message queryNegotiateQuoteReceive(@RequestBody @Validated NegotiateQuoteQueryBean negotiateQuoteQueryBean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(negotiateQuoteQueryBean);
        }
        return new Message(integratedQuoteManagement.queryNegotiateQuoteReceive(negotiateQuoteQueryBean));
    }

    @ApiOperation(value = "我发起的协商成交行情接口", notes = "我发起的协商成交行情接口")
    @PostMapping(value = "/entrust/integrated/negotiateQuoteSend")
    public Message queryNegotiateQuoteSend(@RequestBody @Validated EntrustQueryRequest request) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(request);
        }
        return new Message(integratedQuoteManagement.queryNegotiateQuoteSend(request));
    }

    @ApiOperation(value = "我收到的协议回购行情接口", notes = "我收到的协议回购行情接口")
    @PostMapping(value = "/entrust/integrated/repurchaseQuoteReceive")
    public Message queryRepurchaseQuoteReceive(@RequestBody @Validated RepurchaseQuoteReceiveRequest bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(bean);
        }
        return new Message(integratedQuoteManagement.queryRepurchaseQuoteReceive(bean));
    }

    /**
     * 手动增加我收到的协议回购mock行情
     */
    @ApiOperation(value = "手动增加我收到的协议回购mock行情", notes = "手动增加我收到的协议回购mock行情", hidden = true)
    @PostMapping(value = "/add/integrated/repurchaseQuoteReceive")
    public ResultBean addIntegratedRepurchaseQuoteReceive(@RequestBody RepurchaseQuoteReceiveResponse bean) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObjectResultBean(null);
        }
        return integratedQuoteManagement.addIntegratedRepurchaseQuoteReceive(bean);
    }

    @ApiOperation(value = "我发起的协议回购行情接口", notes = "我发起的协议回购行情接口")
    @PostMapping(value = "/entrust/integrated/repurchaseQuoteSend")
    public Message queryRepurchaseQuoteSend(@RequestBody @Validated EntrustQueryRequest request) {
        if (!MasterUtil.isMaster()) {
            return MasterUtil.postForObject(request);
        }
        return new Message(integratedQuoteManagement.queryRepurchaseQuoteSend(request));
    }
}
