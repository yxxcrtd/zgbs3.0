package com.joyintech.tams.exchange.controller;

import com.google.common.base.Strings;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.investment.EntrustOpeBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.ExchangeConstant;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.joyin.BussinessDictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt2;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.ExchangeInstructionService;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 兼容新UI后端
 */
@RestController
@RequestMapping("/joyin")
public class ExchangeInstructionController {
    private final Logger logger = LoggerFactory.getLogger(ExchangeInstructionController.class);

    @Autowired
    private ExchangeInstructionService instructionService;
    @Autowired
    private InvestmentManagement investmentManagement;

    /**
     * 查询指令列表
     * 当 dataType 不为null时， 为从投管查询
     *
     * @param userId 用户Id
     * @return ResultBean
     */
    @RequestMapping(value = "/instruction/users", method = RequestMethod.GET)
    @CrossOrigin
    public ResultBean queryAll(@RequestParam(value = "user_id") List<String> userId, @RequestParam(value = "instruction_id", required = false) List<String> instructionId, @RequestParam(value = "portfolio_id", required = false) String portfolioId, @RequestParam(value = "instruction_status", required = false) String entrustStatus, @RequestParam(value = "trade_market", required = false) String tradeMarket, @RequestParam(value = "trade_plat", required = false) String tradePlat, @RequestParam(value = "finprod_market_id", required = false) String finprodMarketId, @RequestParam(value = "ps", required = false) String ps, @RequestParam(value = "data_type", required = false) String dataType, @RequestParam(value = "amount", required = false, defaultValue = "0") int amount, @RequestParam(value = "page_no", required = false, defaultValue = "-1") int pageNo) {
        Map<String, Object> pageParam = new HashMap<String, Object>();

        pageParam.put("traderList", userId);
        pageParam.put("amount", amount);
        if (pageNo != -1) {
            pageParam.put("pageNo", pageNo);
        }
        if (Objects.nonNull(instructionId)) {
            pageParam.put("entrustId", instructionId);
        }
        if (!Strings.isNullOrEmpty(portfolioId)) {
            pageParam.put("portfolioId", portfolioId);
        }
        if (!Strings.isNullOrEmpty(entrustStatus)) {
            pageParam.put("entrustStatus", entrustStatus);
        }
        if (!Strings.isNullOrEmpty(tradeMarket)) {
            pageParam.put("tradeMarket", tradeMarket);
        }
        if (!Strings.isNullOrEmpty(tradePlat)) {
            pageParam.put("tradePlat", tradePlat);
        }
        if (!Strings.isNullOrEmpty(finprodMarketId)) {
            pageParam.put("finprodMarketId", finprodMarketId);
        }
        if (!Strings.isNullOrEmpty(ps)) {
            pageParam.put("ps", ps);
        }
        if (!Strings.isNullOrEmpty(dataType)) {
            pageParam.put("dataType", dataType);
        }
        String fairCode = CacheUtil.getSysParam(ExchangeConstant.FAIR_CODE_IN_PARA);
        String platDate = DateUtil.getSysDate();
        String approveCode = CacheUtil.getSysParam(ExchangeConstant.RISK_APPROVAL_IN_PARA);
        String tradeTimeSwitch = CacheUtil.getSysParam(ExchangeConstant.ETR_TRADE_TIME_SWITCH);
        String fairModeEntrustPriceBestThanInstruction = CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_ENTRUST_PRICE_BEST_THAN_INSTRUCTION_IN_PARA);
        String fairModeWithUnacceptInstruction = CacheUtil.getSysParam(ExchangeConstant.FAIR_MODE_WITH_UNACCEPT_INSTRUCTION_IN_PARA);

        pageParam.put("fairCode", fairCode);
        pageParam.put("platDate", platDate);
        pageParam.put("approveCode", approveCode);
        pageParam.put("tradeTimeSwitch", tradeTimeSwitch);
        pageParam.put("fairModeEntrustPriceBestThanInstruction", fairModeEntrustPriceBestThanInstruction);
        pageParam.put("fairModeWithUnacceptInstruction", fairModeWithUnacceptInstruction);

        logger.debug("设置系统参数：fairCode {} platDate {} approveCode {} tradeTimeSwitch {} fairModeEntrustPriceBestThanInstruction {} fairModeWithUnacceptInstruction {}", fairCode, platDate, approveCode, tradeTimeSwitch, fairModeEntrustPriceBestThanInstruction, fairModeWithUnacceptInstruction);
//        return instructionService.queryAll(pageParam);
//////////////////////////兼容新UI后端
        return new ResultBean(ErrorCode.SUCCESS, investmentManagement.queryInstructions(pageParam));
    }

    /***
     * @Description: 指令接收|放弃接口
     * @Param: [entrustOpeList]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/4/28
     */
    @RequestMapping(value = "/instruction/confirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResultBean acceptOrRefuse(@RequestBody List<InstructionOpBean> instructionOpBeans) {
//        return instructionService.acceptOrRefuse(instructionOpBeans);
//////////////////////////兼容新UI后端
        if (Objects.isNull(instructionOpBeans) || instructionOpBeans.size() == 0) {
            throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "参数不可以为空");
        }
        List<EntrustOpeBean> entrustList = new ArrayList<>();
        for (InstructionOpBean instructionOpBean : instructionOpBeans) {
            if (Strings.isNullOrEmpty(instructionOpBean.getInstructionId())) {
                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "指令ID不可以为空");
            }
            if (Strings.isNullOrEmpty(instructionOpBean.getRecStatus())) {
                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "接收状态不可以为空");
            }
            if (Strings.isNullOrEmpty(instructionOpBean.getTrader())) {
                throw BusinessResultException.create(ErrorCode.REQ_PARAMETER_ERROR, "交易员不可以为空");
            }
            EntrustOpeBean entrustOpeBean = new EntrustOpeBean();
            entrustOpeBean.setEntrustId(instructionOpBean.getInstructionId());
            entrustOpeBean.setRecStatus(instructionOpBean.getRecStatus());
            entrustOpeBean.setTrader(instructionOpBean.getTrader());
            entrustList.add(entrustOpeBean);
        }
        return investmentManagement.acceptOrRefuse(entrustList);
    }

    /***
     * @Description: 到期收益率
     * @Param: [finprodId, tradeMarket, ps, netPrice, deliverDate]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/5/8
     */
    @RequestMapping(value = "/instruction/expireIncomeRate", method = RequestMethod.POST)
    @CrossOrigin
    public ResultBean expireIncomeRate(@RequestBody ExpireIncomeRateBean expireIncomeRateBean, @RequestParam(value = "calculate_type", required = false, defaultValue = "3") int calculateType) {


        if (Objects.isNull(expireIncomeRateBean)) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "参数不可以为空");
        }
        if (calculateType != 1 && calculateType != 2 && calculateType != 3) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "计算类型取值范围：[1,2,3]");
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getFinprodId())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "资产代码不可以为空");
        }

        if (Strings.isNullOrEmpty(expireIncomeRateBean.getTradeMarket())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "交易场所不可以为空");
        }
        if (expireIncomeRateBean.getFinprodId().indexOf(".") < 0) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "资产代码格式错误");

        }
        String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(expireIncomeRateBean.getFinprodId());
        if (marketAndStockCode[1].equals(Market.SZSE.value)) {
            if (expireIncomeRateBean.getTradeMarket().equals(DictCodeListExt2.交易流通场所_中债_上海证券交易所.getValue())) {
                return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "资产代码与交易场所不匹配");
            }
        }
        if (marketAndStockCode[1].equals(Market.SHSE.value)) {
            if (expireIncomeRateBean.getTradeMarket().equals(DictCodeListExt2.交易流通场所_中债_深圳证券交易所.getValue())) {
                return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "资产代码与交易场所不匹配");
            }
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getSettleDate())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "交割日不可以为空");
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getInvAim())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "投资目的不可以为空");
        }
        if (Strings.isNullOrEmpty(expireIncomeRateBean.getTradeMarketMode())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "交易市场不可以为空");
        }
        if (!BussinessDictCodeList.交易市场_一级市场.getValue().equals(expireIncomeRateBean.getTradeMarketMode()) && !BussinessDictCodeList.交易市场_二级市场.getValue().equals(expireIncomeRateBean.getTradeMarketMode())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "交易市场不正确");
        }
        if (Objects.isNull(expireIncomeRateBean.getTradeAmt())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "券面总额不可以为空");
        }
        /*if ( expireIncomeRateBean.getTradeAmt() <= 0) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "券面总额不可以小于等于0");
        }*/
        if (Objects.isNull(expireIncomeRateBean.getUnitCprice())) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "净价不可以为空");
        }
        /*if (  expireIncomeRateBean.getUnitCprice() <= 0) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR.value, "净价不可以小于等于0");
        }*/

        Map<String, Object> pageParam = new HashMap<String, Object>();
        pageParam.put("finprodId", expireIncomeRateBean.getFinprodId());
        pageParam.put("tradeMarket", expireIncomeRateBean.getTradeMarket());
        pageParam.put("settleDate", expireIncomeRateBean.getSettleDate());
        pageParam.put("invAim", expireIncomeRateBean.getInvAim());
        pageParam.put("tradeAmt", expireIncomeRateBean.getTradeAmt());
        pageParam.put("fpriceAmt", expireIncomeRateBean.getFpriceAmt());
        pageParam.put("yield", expireIncomeRateBean.getYield());
        pageParam.put("tradeMarketMode", expireIncomeRateBean.getTradeMarketMode());
        pageParam.put("unitCprice", expireIncomeRateBean.getUnitCprice());
        return instructionService.expireIncomeRate(pageParam, calculateType);
    }

    /**
     * 从投管重新刷入指令数据
     *
     * @param userId 用户Id
     * @return resultBean
     */
    @RequestMapping(value = "/instruction/flush", method = RequestMethod.POST)
    public ResultBean flushInstructionFromInvest(@RequestParam(value = "user_id") String userId) {
        String param = "?user_id=" + userId;
//        return instructionService.flushInstructionFromInvest(param);
//////////////////////////兼容新UI后端
        return investmentManagement.flushInstructionFromInvest();
    }
}