package com.joyintech.tams.exchange.service.trade.processor.portfolio;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.investment.PortfolioTradeMessage;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.enums.exchange.PortfolioDetailBusiTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.response.PortfolioCalculateResponse;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.PortfolioDetailByManualMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.PortfolioDetailMapper;
import com.joyintech.tams.exchange.datacommon.db.service.TradeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lifangpeng
 * @date 2022/5/13 10:54
 */
@Component
public class PortfolioTradeMessageHelper {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(PortfolioTradeMessageHelper.class);

    @Autowired
    private PortfolioAssignHelper portfolioAssignHelper;
    @Autowired
    private PortfolioDetailByManualMapper portfolioDetailByManualMapper;
    @Autowired
    private PortfolioDetailMapper portfolioDetailMapper;
    @Autowired
    private TradeService tradeService;

    /**
     * 组装成交组合分配
     */
    public List<PortfolioTradeMessage> buildPortfolioTradeMessage(EntrustInterface entrust, Trade trade, Instruction instruction) {
        List<PortfolioTradeMessage> portfolioTradeMessages = Lists.newArrayList();

        BigDecimal tradedPrice = trade.getTradedPrice();
        if (InvestAssetTypeEnum.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            tradedPrice = trade.getFullPrice();
        }

        //1、调用通用分配接口，构建成交组合分配
        logger.info("调用通用分配接口，构建成交组合分配");
        Map<String, PortfolioDetail> portfolioDetailMap = Maps.newHashMap();
        ResultBean resultBean = portfolioAssignHelper.queryPortfolioAssign(instruction.getEntrustId(), PortfolioDetailBusiTypeEnum.TRADE, BigDecimal.valueOf(trade.getTradedVolume()), tradedPrice, trade.getEntrustProp());
        if (ErrorCode.SUCCESS.value() == resultBean.getErrorCode()) {
            portfolioDetailMap.putAll((Map<String, PortfolioDetail>) resultBean.getData());
        } else {
            logger.warn("构建成交组合分配失败：【instructionId={}】,【brokerTradeId={}】,【entrustId={}】", instruction.getEntrustId(), trade.getBrokerTradeId(), entrust.getId());
            return portfolioTradeMessages;
        }

        //2、保存 成交组合分配
        logger.info("保存成交组合分配");
        for (Map.Entry<String, PortfolioDetail> entry : portfolioDetailMap.entrySet()) {
            PortfolioDetail value = entry.getValue();
            value.setBusiId(trade.getId());
            portfolioDetailMapper.insert(value);
        }

        //3、查询成交对应委托下单的组合分配
        logger.info("查询成交对应委托下单的组合分配");
        List<PortfolioDetail> portfolioDetails = portfolioDetailByManualMapper.selectListByBusiIdAndBusiType(entrust.getId(), PortfolioDetailBusiTypeEnum.ORDER.getCode());
        Map<String, PortfolioDetail> detailMap = portfolioDetails.stream().collect(Collectors.toMap(PortfolioDetail::getPortfolioId, o -> o));

        //4、组装成交组合分配
        for (Map.Entry<String, PortfolioDetail> entry : portfolioDetailMap.entrySet()) {
            PortfolioDetail detail = entry.getValue();
            PortfolioTradeMessage portfolioTradeMessage = new PortfolioTradeMessage();
            portfolioTradeMessage.setBusiId(trade.getId());
            portfolioTradeMessage.setPortfolioId(detail.getPortfolioId());
            portfolioTradeMessage.setPortfolioName(detail.getPortfolioName());
            portfolioTradeMessage.setTradeNum(detail.getTargetNum());
            //信用保护凭证数量转成面额
            if (InvestAssetTypeEnum.isCreditProtectionCertificate(instruction.getAssetType())) {
                portfolioTradeMessage.setTradeNum(detail.getTargetNum().multiply(BigDecimal.valueOf(100)));
            }
            portfolioTradeMessage.setTradeAmt(detail.getTargetAmt());
            portfolioTradeMessage.setTargetType(detail.getTargetType());

            PortfolioDetail portfolioDetail = detailMap.get(detail.getPortfolioId());
            if (Objects.nonNull(portfolioDetail)) {
                portfolioTradeMessage.setEntrustNum(portfolioDetail.getTargetNum());
                portfolioTradeMessage.setEntrustAmt(portfolioDetail.getTargetAmt());
            } else {
                logger.error("下单的组合分配明细不存在：【instructionId={}】,【entrustId={}】", instruction.getEntrustId(), entrust.getId());
            }

            portfolioTradeMessages.add(portfolioTradeMessage);
        }

        return portfolioTradeMessages;
    }

    /**
     * 组装下单组合分配
     */
    public List<PortfolioCalculateResponse> buildPortfolioCalculateResponse(EntrustInterface entrust, PositionInstructionRequest instructionClone, PortfolioDetailBusiTypeEnum busiType) {
        logger.info("构建下单组合分配：【instructionId={}】,【entrustId={}】", instructionClone.getEntrustId(), entrust.getId());

        List<PortfolioCalculateResponse> calculateResponses = Lists.newArrayList();
        BigDecimal tradedPrice = entrust.getEntrustPrice();
        if (InvestAssetTypeEnum.BOND.codeEquals(instructionClone.getAssetType())
                && EnumUtil.in(instructionClone.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            tradedPrice = entrust.getFullPrice();
        }
        //1、调用通用分配接口，构建组合分配
        ResultBean resultBean = portfolioAssignHelper.queryPortfolioAssign(instructionClone.getEntrustId(), busiType, BigDecimal.valueOf(instructionClone.getEntrustNum()), tradedPrice, entrust.getEntrustProp());

        Map<String, PortfolioDetail> portfolioDetailMap = Maps.newHashMap();
        if (ErrorCode.SUCCESS.value() == resultBean.getErrorCode()) {
            portfolioDetailMap.putAll((Map<String, PortfolioDetail>) resultBean.getData());
        } else {
            logger.warn("构建下单组合分配失败：【instructionId={}】,【entrustId={}】", instructionClone.getEntrustId(), entrust.getId());
            return calculateResponses;
        }

        //2、保存 委托组合分配
        logger.info("构建下单组合分配：保存 委托组合分配，【instructionId={}】,【entrustId={}】", instructionClone.getEntrustId(), entrust.getId());
        for (Map.Entry<String, PortfolioDetail> entry : portfolioDetailMap.entrySet()) {
            PortfolioDetail value = entry.getValue();
            value.setBusiId(entrust.getId());
            portfolioDetailMapper.insert(entry.getValue());
        }

        //3、组装委托组合分配
        logger.info("构建下单组合分配：组装委托组合分配，【instructionId={}】,【entrustId={}】", instructionClone.getEntrustId(), entrust.getId());
        PortfolioCalculateResponse calculateResponse = null;
        for (Map.Entry<String, PortfolioDetail> entry : portfolioDetailMap.entrySet()) {
            calculateResponse = new PortfolioCalculateResponse();

            PortfolioDetail portfolioDetail = entry.getValue();

            calculateResponse.setPortfolioId(portfolioDetail.getPortfolioId());
            calculateResponse.setPortfolioName(portfolioDetail.getPortfolioName());
            calculateResponse.setFinprodMarketId(instructionClone.getFinprodMarketId());
            calculateResponse.setFinprodName(instructionClone.getFinprodName());
            if (Objects.nonNull(entrust.getFullPrice())) {
                calculateResponse.setFullPrice(entrust.getFullPrice());
            }
            calculateResponse.setPriceMode(instructionClone.getPriceMode());
            calculateResponse.setAcctNo(instructionClone.getAcctNo());

            calculateResponse.setEntrustNum(portfolioDetail.getTargetNum().longValue());
            calculateResponse.setEntrustAmt(portfolioDetail.getTargetAmt());
            if (ObjectUtil.isNotNull(calculateResponse.getEntrustNum()) && calculateResponse.getEntrustNum() != 0) {
                calculateResponse.setEntrustPrice(NumberUtil.div(calculateResponse.getEntrustAmt(), calculateResponse.getEntrustNum()));
            }

            calculateResponse.setEtrSecAmount(portfolioDetail.getEtrSecAmount());

            calculateResponses.add(calculateResponse);
        }
        logger.info("构建下单组合分配结果：{}", JsonUtils.getJsonFromObject(calculateResponses));

        return calculateResponses;
    }

    /**
     * 组装撤单组合分配
     */
    public List<PortfolioCalculateResponse> buildCancelPortfolioCalculateResponse(EntrustInterface entrust, PositionInstructionRequest instructionClone) {
        String entrustId = instructionClone.getEntrustId();
        logger.info("构建撤单组合分配：【instructionId={}】,【entrustId={}】", entrustId, entrust.getId());

        List<PortfolioCalculateResponse> calculateResponses = Lists.newArrayList();

        //1、查询下单委托组合分配
        logger.info("查询下单委托组合分配：【instructionId={}】,【entrustId={}】", entrustId, entrust.getId());
        List<PortfolioDetail> orderDetails = portfolioDetailByManualMapper.selectListByInstructionAndBusiType(entrustId, PortfolioDetailBusiTypeEnum.ORDER.getCode())
                .stream().filter(o -> StringUtil.equals(o.getBusiId(), entrust.getId())).collect(Collectors.toList());

        //获取成交编号
        Set<String> tradeIdSet = tradeService.queryTradeByInstructionId(entrustId)
                .stream().filter(o -> StringUtil.equals(o.getEntrustId(), entrust.getId()))
                .map(Trade::getId).collect(Collectors.toSet());

        //2、查询成交组合分配
        logger.info("查询成交组合分配：【instructionId={}】,【entrustId={}】", entrustId, entrust.getId());
        Map<String, List<PortfolioDetail>> tradeDetailsMap = portfolioDetailByManualMapper.selectListByInstructionAndBusiType(entrustId, PortfolioDetailBusiTypeEnum.TRADE.getCode())
                .stream().filter(o -> tradeIdSet.contains(o.getBusiId())).collect(Collectors.groupingBy(PortfolioDetail::getPortfolioId));

        List<PortfolioDetail> cancelDetails = Lists.newArrayList();

        //3、保存 撤销组合分配
        logger.info("构建撤单组合分配：保存 委托组合分配，【instructionId={}】,【entrustId={}】", entrustId, entrust.getId());
        orderDetails.forEach(o -> {
            o.setBusiId(entrust.getId());
            o.setInstructionId(entrustId);

            List<PortfolioDetail> list = tradeDetailsMap.get(o.getPortfolioId());
            if (CollectionUtils.isNotEmpty(list)) {
                BigDecimal tradeNum = list.stream().map(PortfolioDetail::getTargetNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal tradeAmt = list.stream().map(PortfolioDetail::getTargetAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                o.setTargetNum(o.getTargetNum().subtract(tradeNum));
                o.setTargetAmt(o.getTargetAmt().subtract(tradeAmt));
            }

            portfolioDetailMapper.insert(o);

            cancelDetails.add(o);
        });

        //4、组装委托组合分配
        logger.info("构建撤单组合分配：组装委托组合分配，【instructionId={}】,【entrustId={}】", entrustId, entrust.getId());
        PortfolioCalculateResponse calculateResponse = null;
        for (PortfolioDetail portfolioDetail : cancelDetails) {
            calculateResponse = new PortfolioCalculateResponse();

            calculateResponse.setPortfolioId(portfolioDetail.getPortfolioId());
            calculateResponse.setPortfolioName(portfolioDetail.getPortfolioName());
            calculateResponse.setFinprodMarketId(instructionClone.getFinprodMarketId());
            calculateResponse.setFinprodName(instructionClone.getFinprodName());
            if (Objects.nonNull(entrust.getFullPrice())) {
                calculateResponse.setFullPrice(entrust.getFullPrice());
            }
            calculateResponse.setPriceMode(instructionClone.getPriceMode());
            calculateResponse.setAcctNo(instructionClone.getAcctNo());

            calculateResponse.setEntrustNum(portfolioDetail.getTargetNum().longValue());
            calculateResponse.setEntrustAmt(portfolioDetail.getTargetAmt());
            if (ObjectUtil.isNotNull(calculateResponse.getEntrustNum()) && calculateResponse.getEntrustNum() != 0) {
                calculateResponse.setEntrustPrice(NumberUtil.div(calculateResponse.getEntrustAmt(), calculateResponse.getEntrustNum()));
            }

            calculateResponse.setEtrSecAmount(portfolioDetail.getEtrSecAmount());

            calculateResponses.add(calculateResponse);
        }
        logger.info("构建撤单组合分配结果：{}", JsonUtils.getJsonFromObject(calculateResponses));

        return calculateResponses;
    }


}
