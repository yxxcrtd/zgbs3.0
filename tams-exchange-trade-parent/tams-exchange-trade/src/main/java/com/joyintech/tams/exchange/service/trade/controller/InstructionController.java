package com.joyintech.tams.exchange.service.trade.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.CalcPriceAndYieldRequest;
import com.joyintech.tams.exchange.common.bean.CalcPriceAndYieldResponse;
import com.joyintech.tams.exchange.common.bean.ExpireIncomeRateBean;
import com.joyintech.tams.exchange.common.bean.ExpireIncomeRateRet;
import com.joyintech.tams.exchange.common.bean.InstructionOpBean;
import com.joyintech.tams.exchange.common.bean.InstructionPage;
import com.joyintech.tams.exchange.common.bean.investment.EntrustOpeBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.ExchangeConstant;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.InstructionExportVo;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.request.InstructionExportRequest;
import com.joyintech.tams.exchange.common.request.InstructionQueryRequest;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.util.MasterUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/trade/joyin")
@Api(tags = "场内交易-指令操作接口")
public class InstructionController {
    private final Logger logger = LoggerFactory.getLogger(InstructionController.class);
    @Autowired(required = false)
    @Qualifier("loadBalancedRestTemplate")
    protected JoyinRestTemplate restTemplate;
    @Autowired
    private InvestmentManagement investmentManagement;

    @ApiOperation(value = "查询指令列表", notes = "查询指令列表")
    @RequestMapping(value = "/instruction/users", method = RequestMethod.POST)
    @CrossOrigin
    public Message<InstructionPage<InstructionExportVo, InstructionPledge, InstructionDetail>> queryAllInstruction(@RequestBody InstructionQueryRequest instructionQuery) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(instructionQuery);
    	}
        String fairCode = CacheUtil.getSysParam(ExchangeConstant.FAIR_CODE_IN_PARA);
        String platDate = DateUtil.getSysDate();
        String approveCode = CacheUtil.getSysParam(ExchangeConstant.RISK_APPROVAL_IN_PARA);
        String tradeTimeSwitch = CacheUtil.getSysParam(ExchangeConstant.ETR_TRADE_TIME_SWITCH);
        String fairModeEntrustPriceBestThanInstruction = CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION_IN_PARA);
        String fairModeWithUnacceptInstruction = CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_WITH_UNACCEPT_INSTRUCTION_IN_PARA);

        instructionQuery.setFairCode(fairCode);
        instructionQuery.setPlatDate(platDate);
        instructionQuery.setApproveCode(approveCode);
        instructionQuery.setTradeTimeSwitch(tradeTimeSwitch);
        instructionQuery.setFairModeEntrustPriceBestThanInstruction(fairModeEntrustPriceBestThanInstruction);
        instructionQuery.setFairModeWithUnacceptInstruction(fairModeWithUnacceptInstruction);

        Map<String, Object> pageParam = JsonUtils.getObjectFromJson(JsonUtils.getJsonFromObject(instructionQuery), Map.class);
        pageParam.remove("entrustStatus");
        return new Message<>(investmentManagement.queryInstructions(pageParam));
    }

    @PostMapping(value = "/instruction/export")
    @ResponseBody
    @ApiOperation(value = "导出指令列表", notes = "导出指令列表")
    public void exportInstruction(@RequestBody InstructionExportRequest request, HttpServletResponse response) {
        if (Objects.nonNull(request.getData())) {
            List<InstructionExportVo> instructionList = request.getData().stream().map(item -> BeanUtil.toBean(item, InstructionExportVo.class)).collect(Collectors.toList());
            List<InstructionExportVo> instructionExportVoList = investmentManagement.convertFieldValue(instructionList);

            List<BaseMap> newList = Lists.newArrayList();
            newList.addAll(instructionExportVoList.stream().map(item -> BaseMap.newMap(BeanUtil.beanToMap(item))).collect(Collectors.toList()));

            request.setData(newList);
        }
        ExcelUitl.doExport(request, response, "指令列表", () -> investmentManagement.queryExportPage(request.getDto(InstructionQueryRequest.class)));
    }

    @ApiOperation(value = "指令接收|放弃接口", notes = "指令接收|放弃接口")
    @RequestMapping(value = "/instruction/confirm", method = RequestMethod.POST)
    @CrossOrigin
    public Message<List<EntrustOpeBean>> acceptOrRefuse(@RequestBody @Validated List<InstructionOpBean> instructionOpBeans) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(instructionOpBeans);
    	}
        if (Objects.isNull(instructionOpBeans) || instructionOpBeans.size() == 0) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "参数不可以为空");
        }
        List<EntrustOpeBean> entrustList = new ArrayList<>();
        for (InstructionOpBean instructionOpBean : instructionOpBeans) {
//            if (Strings.isNullOrEmpty(instructionOpBean.getInstructionId())) {
//                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "指令ID不可以为空");
//            }
//            if (Strings.isNullOrEmpty(instructionOpBean.getRecStatus())) {
//                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "接收状态不可以为空");
//            }
//            if (Strings.isNullOrEmpty(instructionOpBean.getTrader())) {
//                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "交易员不可以为空");
//            }
            EntrustOpeBean entrustOpeBean = new EntrustOpeBean();
            entrustOpeBean.setEntrustId(instructionOpBean.getInstructionId());
            entrustOpeBean.setRecStatus(instructionOpBean.getRecStatus());
            entrustOpeBean.setTrader(instructionOpBean.getTrader());
            entrustList.add(entrustOpeBean);
        }
        return new Message<List<EntrustOpeBean>>(investmentManagement.acceptOrRefuse(entrustList));
    }

    @ApiOperation(value = "从投管重新刷入指令数据", notes = "从投管重新刷入指令数据", hidden = true)
    @RequestMapping(value = "/instruction/flush", method = RequestMethod.POST)
    public Message<Object> flushInstructionFromInvest(@RequestParam(value = "userId") String userId) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObject(userId);
    	}
        return new Message<Object>(investmentManagement.flushInstructionFromInvest());
    }

    /**
     * {"calculateField":"01","finprodId":"F01_113052.SH","fprice":"","price":"113.01","prinAmt":"100","ps":"T01","tradeMarket":"03","yield":""}
     */
    @ApiOperation(value = "净价、全价、到期收益率计算三合一", notes = "净价、全价、到期收益率计算三合一")
    @PostMapping(value = "/instruction/calcPriceAndYield")
    public Message<CalcPriceAndYieldResponse> calcPriceAndYield(@RequestBody @Validated CalcPriceAndYieldRequest request) {
        String url = TradeServiceURL.calcPriceAndYield;
        url = ServiceApplication.INSTRUCTION_SERVICE + url;
        Message response = restTemplate.postForObject(url, request, Message.class);
        logger.info("调用投管接口 【净价、全价、到期收益率计算三合一】 calcPriceAndYield() called with url ==> 【{}】, request = 【{}】, response => 【{}】", url, JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
        return response;
    }

    @Deprecated
    @ApiOperation(value = "到期收益率计算", notes = "到期收益率计算")
    @RequestMapping(value = "/instruction/expireIncomeRate", method = RequestMethod.POST)
    @CrossOrigin
    public Message<ExpireIncomeRateRet> expireIncomeRate(@RequestBody ExpireIncomeRateBean expireIncomeRateBean) {
        if (Objects.isNull(expireIncomeRateBean)) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "参数不可以为空");
        }
        int calculateType = expireIncomeRateBean.getCalculateType();
        if (calculateType != 1 && calculateType != 2 && calculateType != 3) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "计算类型取值范围：[1,2,3]");
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getFinprodId())) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "资产代码不可以为空");
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getTradeMarket())) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "交易场所不可以为空");
        }
        if (!expireIncomeRateBean.getFinprodId().contains(".")) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "资产代码格式错误");
        }
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(expireIncomeRateBean.getFinprodId());
        if (marketAndStockCode[1].equals(Market.SZSE.value)) {
            if (expireIncomeRateBean.getTradeMarket().equals(DictCodeListExt2.交易流通场所_中债_上海证券交易所.getValue())) {
                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "资产代码与交易场所不匹配");
            }
        }
        if (marketAndStockCode[1].equals(Market.SHSE.value)) {
            if (expireIncomeRateBean.getTradeMarket().equals(DictCodeListExt2.交易流通场所_中债_深圳证券交易所.getValue())) {
                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "资产代码与交易场所不匹配");
            }
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getSettleDate())) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "交割日不可以为空");
        }
//        if (Strings.isNullOrEmpty(expireIncomeRateBean.getInvAim())) {
//            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "投资目的不可以为空");
//        }
//        if (Strings.isNullOrEmpty(expireIncomeRateBean.getTradeMarketMode())) {
//            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "交易市场不可以为空");
//        }
        if (Objects.isNull(expireIncomeRateBean.getTradeAmt())) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "券面总额不可以为空");
        }
        /*if ( expireIncomeRateBean.getTradeAmt() <= 0) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "券面总额不可以小于等于0");
        }*/
        if (Objects.isNull(expireIncomeRateBean.getUnitCprice())) {
            expireIncomeRateBean.setUnitCprice(0d);
//            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "净价不可以为空");
        }
        if (Objects.isNull(expireIncomeRateBean.getFpriceAmt())) {
            expireIncomeRateBean.setFpriceAmt(0d);
        }
        if (Objects.isNull(expireIncomeRateBean.getYield())) {
            expireIncomeRateBean.setYield(0d);
        }
        /*if (  expireIncomeRateBean.getUnitCprice() <= 0) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "净价不可以小于等于0");
        }*/

        Map<String, Object> pageParam = new HashMap<String, Object>();
        pageParam.put("finprodId", expireIncomeRateBean.getFinprodId());
        pageParam.put("tradeMarket", expireIncomeRateBean.getTradeMarket());
        pageParam.put("settleDate", expireIncomeRateBean.getSettleDate());
        pageParam.put("invAim", expireIncomeRateBean.getInvAim());
        pageParam.put("tradeAmt", expireIncomeRateBean.getTradeAmt());
        pageParam.put("fpriceAmt", expireIncomeRateBean.getFpriceAmt());
        pageParam.put("yield", expireIncomeRateBean.getYield());
        pageParam.put("unitCprice", expireIncomeRateBean.getUnitCprice());
        pageParam.put("tradeMarketMode", "02");

        String url = TradeServiceURL.expireIncomeRate;
        //净价
        if (calculateType == 1) {
            url = TradeServiceURL.unitCprice;
        } else if (calculateType == 2) {
            url = TradeServiceURL.fpriceAmt;
        } else if (calculateType == 3) {
            url = TradeServiceURL.yield;
        }
        url = ServiceApplication.TRADE_SERVICE_NAME + url;
        logger.debug("调用投管接口：url:{}, param:{}", url, JSONUtil.toJsonStr(pageParam));
        ExpireIncomeRateRet expireIncomeRateRet = restTemplate.postForObject(url, pageParam, ExpireIncomeRateRet.class);
        logger.debug("ExpireIncomeRateRet:{}", JSONObject.toJSONString(expireIncomeRateRet));
        return new Message<ExpireIncomeRateRet>(expireIncomeRateRet);
    }
}