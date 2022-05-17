package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Strings;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.Algorithm;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.Order;
import com.joyintech.tams.exchange.common.exception.PositionExchangeException;
import com.joyintech.tams.exchange.common.request.BidPriceConInfoRequest;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.response.BidPriceConInfoResponse;
import com.joyintech.tams.exchange.common.utils.CheckParamUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.datacommon.db.service.OrderService;
import com.joyintech.tams.exchange.service.trade.position.service.PositionRequestServiceInstructionRequest;
import com.joyintech.tams.exchange.service.trade.processor.invest.InvestInterfaceHelper;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.algorithm.AlgorithmHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.common.CommonCheckHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.joyintech.tams.business.common.exception.PositionException;


import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(OrderManagement.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private AlgorithmHelper algorithmHelper;
    @Autowired
    private CommonCheckHelper commonCheckHelper;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private PositionRequestServiceInstructionRequest positionRequestServiceInstructionRequest;
    @Autowired
    private InvestInterfaceHelper investInterfaceHelper;

    /**
     * 根据userId查询Order表中数据
     */
    public ResultBean queryOrder(String userId, String instructionId, String accountId) {
        if (!Strings.isNullOrEmpty(instructionId)) {
            return new ResultBean(ErrorCode.SUCCESS, orderService.queryOrderByInstructionId(instructionId));
        }
        List<Order> orderList = orderService.queryOrder(userId);
        if (!Strings.isNullOrEmpty(accountId)) {
            orderList = orderList.stream().filter(entrust -> accountId.equals(entrust.getAccountId())).collect(Collectors.toList());
        }
        return new ResultBean(ErrorCode.SUCCESS, orderList);
    }

    public ResultBean sendOrder(String userId, OrderPlaceBean orderPlaceBean) {
        // 填充期货下单stockCode结构
        boolean hasInstruction = fillStockCodeForFutures(orderPlaceBean);
        if (!hasInstruction) {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST.value, ErrorCode.INSTRUCTIONS_NOT_EXIST.errorMsg, orderPlaceBean);
        }
        // 参数检查
        ErrorCode checkMsg = CheckParamUtils.checkOrderParam(orderPlaceBean);
        if (checkMsg.value != ErrorCode.SUCCESS.value)
            return new ResultBean(checkMsg, orderPlaceBean);
        String uniqueCheck = commonCheckHelper.checkApplyEntrust(orderPlaceBean.getEntrustProp(), orderPlaceBean.getAccountId(), orderPlaceBean.getStockCode(), orderPlaceBean.getInstructionId());
        if (Objects.nonNull(uniqueCheck))
            return new ResultBean(ErrorCode.ACCOUNT_APPLY_NOT_UNIQUE.value, ErrorCode.ACCOUNT_APPLY_NOT_UNIQUE.errorMsg + ":" + uniqueCheck, orderPlaceBean);
        // SPPI检查
        ResultBean sppiResult = checkSPPI(orderPlaceBean);
        if (sppiResult.isFail()) {
            return sppiResult;
        } else {
            BidPriceConInfoResponse response = (BidPriceConInfoResponse) sppiResult.getData();
            if (ObjectUtil.isNotNull(response) && response.getStatus() != 0) {
                return ResultBean.error(ErrorCode.CHECK_ERROR.value, "SPPI 校验失败：" + response.getMessage(), orderPlaceBean);
            }
        }

        try {
            checkPosition(orderPlaceBean);
            Order order = tradeHelper.handleOrder(orderPlaceBean);
            return new ResultBean(order.getErrorCode(), order.getErrorMsg());
        } catch (PositionException e) {
            logger.warn("场内交易下单头寸检测异常", e);
            Message message = PositionUtil.ajaxErrorReturn(e);
            return new ResultBean(Integer.parseInt(message.getCode()), message.getMessage(), message.getData());
        } catch (PositionExchangeException ex) {
            logger.warn("场内交易下单头寸检测异常", ex);
            return ResultBean.success(ex.getExceptionMessage());
        }
    }

    private ResultBean checkSPPI(OrderPlaceBean orderPlaceBean) {

        Instruction instruction = instructionService.findById(orderPlaceBean.getInstructionId());
        if (InvestAssetTypeEnum.isBond(instruction.getAssetType()) && InstructionPs.BUY.codeEquals(instruction.getPs()))
        {
            BidPriceConInfoRequest request = new BidPriceConInfoRequest();
            request.setPs(InstructionPs.BUY.getCode());
            request.setFinprodId(instruction.getFinprodId());
            request.setBusinessModel(instruction.getInvAim());
            request.setPrice(orderPlaceBean.getEntrustPrice());

            return investInterfaceHelper.getBidPriceConInfo(request);
        }
        return ResultBean.success();
    }

    //头寸引擎余额检查
    private void checkPosition(OrderPlaceBean orderPlaceBean) {
        Instruction instruction = instructionService.findById(orderPlaceBean.getInstructionId());
//        	PositionInstructionRequest instructionClone = (PositionInstructionRequest)instruction.cloneInstruction();
        PositionInstructionRequest instructionClone = new PositionInstructionRequest();
        BeanUtil.copyProperties(instruction, instructionClone);
        instructionClone.setSoftSkipMode(orderPlaceBean.getSoftSkipMode());
        //普通下单
        if (tradeHelper.isOrdinaryTransaction(orderPlaceBean, instruction)) {
            logger.info("普通交易头寸检查...");
            instructionClone.setEntrustAmt(orderPlaceBean.getEntrustPrice().multiply(BigDecimal.valueOf(orderPlaceBean.getEntrustVolume())));
            instructionClone.setEntrustNum(orderPlaceBean.getEntrustVolume());
            instructionClone.setEntrustPrice(orderPlaceBean.getEntrustPrice());
            instructionClone.setFullPrice(orderPlaceBean.getFullPrice());
        } else if (tradeHelper.isFairTransaction(orderPlaceBean, instructionClone)) { //公平交易
            return;
        } else {
            tradeHelper.fillEntrustPriceAndAmt(orderPlaceBean, instructionClone, instruction);
        }
        instructionClone.setEntrustNo(instruction.getEntrustId());
        instructionClone.setEntrustProp(orderPlaceBean.getEntrustProp());
        positionRequestServiceInstructionRequest.sendRequest(instructionClone, DictCodeListExt3.业务动作_交易下单.getValue(), DictCodeListExt3.系统动作_检查.getValue(), DictCodeListExt3.系统动作_新建.getValue());
    }

    private boolean fillStockCodeForFutures(OrderPlaceBean orderPlaceBean) {
        Instruction instruction = instructionService.findById(orderPlaceBean.getInstructionId());
        if (Objects.isNull(instruction)) {
            logger.info("未找到相关指令信息：assetTypeCheckForFutures instructionId [{}] ", orderPlaceBean.getInstructionId());
            return false;
        }
        if (InvestAssetTypeEnum.isFutures(instruction.getAssetType())) {
            String stockCode = orderPlaceBean.getStockCode();
            orderPlaceBean.setStockCode(instruction.getFinprodMarketId());
            logger.info("重新填充期货下单参数stockCode信息， old：{}   new： {}", stockCode, instruction.getFinprodMarketId());
        }
        return true;
    }

    public ResultBean sendIntegratedEntrust(String userId, IntegratedEntrust integratedEntrust) {
        // 参数检查
        ErrorCode checkMsg = CheckParamUtils.checkIntegratedEntrustParam(integratedEntrust);
        if (checkMsg.value != ErrorCode.SUCCESS.value) {
            return new ResultBean(checkMsg, integratedEntrust);
        }
        String uniqueCheck = commonCheckHelper.checkApplyEntrust(integratedEntrust.getEntrustProp(), integratedEntrust.getAccountId(), integratedEntrust.getStockCode(), integratedEntrust.getInstructionId());
        if (Objects.nonNull(uniqueCheck))
            return new ResultBean(ErrorCode.INSTRUCTION_APPLY_NOT_UNIQUE.value, ErrorCode.INSTRUCTION_APPLY_NOT_UNIQUE.errorMsg + ":" + uniqueCheck, integratedEntrust);
        //头寸引擎余额检查---start
        try {
            String instructionId = integratedEntrust.getInstructionId();
            Instruction instruction = instructionService.findById(instructionId);
            PositionInstructionRequest instructionClone = new PositionInstructionRequest();
            BeanUtil.copyProperties(instruction, instructionClone);
            instructionClone.setSoftSkipMode(integratedEntrust.getSoftSkipMode());
            if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                logger.info("场内协议回购交易下单头寸检查，指令编号：{}，回购金额：{}", instructionId, integratedEntrust.getEntrustBalance());
                instructionClone.setEntrustAmt(integratedEntrust.getEntrustBalance());
            } else {
                // 计算委托金额
                BigDecimal entrustBalance = TradeUtils.calculateBalance(integratedEntrust.getEntrustPrice(), integratedEntrust.getEntrustVolume(), integratedEntrust.getAssetType(), integratedEntrust);
                instructionClone.setEntrustAmt(entrustBalance);
            }
            instructionClone.setEntrustNum(integratedEntrust.getEntrustVolume());
            instructionClone.setEntrustPrice(integratedEntrust.getEntrustPrice());
            instructionClone.setFullPrice(integratedEntrust.getFullPrice());
            instructionClone.setEntrustNo(instruction.getEntrustId());
            instructionClone.setEntrustProp(integratedEntrust.getEntrustProp());
            positionRequestServiceInstructionRequest.sendRequest(instructionClone, DictCodeListExt3.业务动作_交易下单.getValue(), DictCodeListExt3.系统动作_检查.getValue(), DictCodeListExt3.系统动作_新建.getValue());
        } catch (PositionException e) {
            logger.warn("场内交易大宗下单头寸检测异常", e);
            Message message = PositionUtil.ajaxErrorReturn(e);
            return new ResultBean(Integer.parseInt(message.getCode()), message.getMessage(), message.getData());
        }
        //头寸引擎余额检查---end
        IntegratedEntrust returnEntrust = tradeHelper.handIntegratedEntrust(integratedEntrust);
        return new ResultBean(returnEntrust.getErrorCode(), returnEntrust.getErrorMsg());
    }

    public ResultBean cancelOrder(String userId, String orderId) {
        return new ResultBean(ErrorCode.SUCCESS, tradeHelper.handleCancelOrder(orderId));
    }

    public ResultBean sendOrderWithReview(String userId, ReviewDto dto) {
        // 填充期货下单stockCode结构
        boolean hasInstruction = fillStockCodeForFutures(dto);
        if (!hasInstruction) {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST.value, ErrorCode.INSTRUCTIONS_NOT_EXIST.errorMsg, dto);
        }
        // 参数检查
        ErrorCode checkMsg = CheckParamUtils.checkOrderParam(dto);
        if (checkMsg.value != ErrorCode.SUCCESS.value)
            return new ResultBean(checkMsg, dto);
        String uniqueCheck = commonCheckHelper.checkApplyEntrust(dto.getEntrustProp(), dto.getAccountId(), dto.getStockCode(), dto.getInstructionId());
        if (Objects.nonNull(uniqueCheck))
            return new ResultBean(ErrorCode.ACCOUNT_APPLY_NOT_UNIQUE.value, ErrorCode.ACCOUNT_APPLY_NOT_UNIQUE.errorMsg + ":" + uniqueCheck, dto);
        Order order = tradeHelper.handleOrderWithReview(dto);
        return new ResultBean(order.getErrorCode(), order.getErrorMsg());
    }

    public ResultBean sendIntegratedEntrustWithReview(String userId, TrdExchangeDto dto) {
        // 参数检查
        ErrorCode checkMsg = CheckParamUtils.checkIntegratedEntrustParam(dto);
        if (checkMsg.value != ErrorCode.SUCCESS.value)
            return new ResultBean(checkMsg, dto);
        String uniqueCheck = commonCheckHelper.checkApplyEntrust(dto.getEntrustProp(), dto.getAccountId(), dto.getStockCode(), dto.getInstructionId());
        if (Objects.nonNull(uniqueCheck))
            return new ResultBean(ErrorCode.INSTRUCTION_APPLY_NOT_UNIQUE.value, ErrorCode.INSTRUCTION_APPLY_NOT_UNIQUE.errorMsg + ":" + uniqueCheck, dto);
        IntegratedEntrust entrust = tradeHelper.handleIntegratedEntrustWithReview(dto);
        return new ResultBean(entrust.getErrorCode(), entrust.getErrorMsg());
    }

    public ResultBean createAlgorithm(String userId, AlgorithmPlaceBean algorithmPlaceBean) {
        ErrorCode checkMsg = CheckParamUtils.getErrorCode(userId, algorithmPlaceBean.getInstructionId(), algorithmPlaceBean.getEntrustBs(), algorithmPlaceBean.getStockCode(), EntrustProp.NORMAL.value,
                null, null, null, algorithmPlaceBean.getOffsetFlag(), null);
        Algorithm algorithm = algorithmPlaceBean.createAlgorithm();
        if (checkMsg.value != ErrorCode.SUCCESS.value)
            return new ResultBean(checkMsg, algorithm);
        algorithmHelper.handleAlgorithmCreate(userId, algorithm);
        return new ResultBean(algorithm.getErrorCode(), algorithm.getErrorMsg(), algorithm);
    }

    public ResultBean cancelAlgorithm(String algorithmId) {
        return algorithmHelper.cancelAlgorithm(algorithmId);
    }

//    public ResultBean cancelOrderWithRevoke(String userId, RevokeDto dto) {
//        return new ResultBean(ErrorCode.SUCCESS, tradeHelper.handleCancelOrderWithRevoke(dto));
//    }
}
