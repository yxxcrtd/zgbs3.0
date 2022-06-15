package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveQryRequest;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveRequest;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.*;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.common.enums.exchange.QuoteStatusEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepoBsEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepurchaseTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.PledgeDisPlaceDirEnum;
import com.joyintech.tams.exchange.common.enums.invest.PsEnum;
import com.joyintech.tams.exchange.common.request.EntrustQueryRequest;
import com.joyintech.tams.exchange.common.request.RepurchaseQuoteSendRequest;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TickConverter;
import com.joyintech.tams.exchange.common.vo.IntegratedEntrustVo;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionPledgeService;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.scheduled.quote.QuoteJob;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.joyintech.tams.exchange.common.constant.EntrustStatus.*;

/**
 * @ClassName IntegratedQuoteManagement
 * @Description 综合交易行情查询
 * @Author liujianping
 * @Date 2020/9/21
 * @Version
 **/
@Component
public class IntegratedQuoteManagement {
    public static final String ROOT = "root";
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(IntegratedQuoteManagement.class);
    @Autowired
    private TerminalHelper terminalHelper;
    @Autowired
    private QuoteJob quoteJob;

    @Autowired
    private QuoteTickService quoteTickService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private EntrustManagement entrustManagement;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private InstructionPledgeService instructionPledgeService;

    public ResultBean getTick(String stockCode) {
        if ("*".equals(stockCode)) {
            return new ResultBean(ErrorCode.SUCCESS, quoteTickService.getAll().values()
                    .stream().map(TickConverter::convertTick).collect(Collectors.toList()));
        }
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByStockCode(stockCode);
        if (Objects.nonNull(futuresInstrument)) {
            stockCode = futuresInstrument.getInnerCode();
        }
        return new ResultBean(ErrorCode.SUCCESS, TickConverter.convertTick(quoteTickService.findFromRedisById(stockCode)));
    }

    public ResultBean queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) {
        List<IntegratedBulkQuoteBean> integratedBulkQuotes = terminalHelper.queryIntegratedBulkQuote(integratedBulkQuoteQryBean);
        return new ResultBean(ErrorCode.SUCCESS, integratedBulkQuotes);
    }

    public ResultBean queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) {
        return new ResultBean(ErrorCode.SUCCESS, terminalHelper.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean));
    }

    public ResultBean queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) {
        String entrustProp = integratedRepurchaseQuoteQryBean.getEntrustProp();
        if (!"".equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_EXPIRE.value.equals(entrustProp) &&
                !EntrustProp.SH_REPURCHASE_INTENTION.value.equals(entrustProp)) {
            return null;
        }
        return new ResultBean(ErrorCode.SUCCESS, terminalHelper.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean));
    }

    public ResultBean queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) {
        return new ResultBean(ErrorCode.SUCCESS, terminalHelper.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean));
    }

    public ResultBean queryIntegratedQuote(String userId, int quoteType, String stockCode, String accountId, String oppositeLiaisonTel, String liaisonTel, int pageSize, String posStr) {
        List<IntegratedQuote> integratedQuotes = terminalHelper.queryIntegratedQuote(quoteType, stockCode, accountId, oppositeLiaisonTel, liaisonTel, pageSize, posStr);
        return new ResultBean(ErrorCode.SUCCESS, integratedQuotes);
    }

    public ResultBean addIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteBean repurchaseQuoteBean) {
        if (Objects.nonNull(repurchaseQuoteBean)) {
            quoteJob.addIntegratedRepurchaseQuote(repurchaseQuoteBean);
            return new ResultBean(ErrorCode.SUCCESS);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(ROOT, terminalHelper.queryNegotiateQuoteReceive(negotiateQuoteQueryBean));
        return new ResultBean(ErrorCode.SUCCESS, map);
    }

    public ResultBean queryNegotiateQuoteSend(EntrustQueryRequest request) {
        ResultBean resultBean = entrustManagement.queryIntegratedEntrust(request.getUserId(), request.getInstructionId(), request.getAccountId(), request.getOrderId());
        if (null != resultBean && null != resultBean.getData()) {
            List<NegotiateQuoteSendBean> retList = new ArrayList<>();
            List<IntegratedEntrustVo> entrustList = (List<IntegratedEntrustVo>) resultBean.getData();
            for (IntegratedEntrustVo integratedEntrust : entrustList) {
                if (integratedEntrust.getEntrustStatus() == EntrustStatus.REPORTED.value
                        || integratedEntrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value) {
                    NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
                    negotiateQuoteSendBean.setRequestStatus(1);//请求状态(1：待确认  3：已撤销 4：已成交 )
                    setNegotiateQuoteSendBean(negotiateQuoteSendBean, integratedEntrust);
                    retList.add(negotiateQuoteSendBean);
                } else if (integratedEntrust.getEntrustStatus() == EntrustStatus.CANCELED.value) {
                    NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
                    negotiateQuoteSendBean.setRequestStatus(3);//请求状态(1：待确认  3：已撤销 4：已成交 )
                    setNegotiateQuoteSendBean(negotiateQuoteSendBean, integratedEntrust);
                    retList.add(negotiateQuoteSendBean);
                } else if (integratedEntrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value
                        || integratedEntrust.getEntrustStatus() == EntrustStatus.PART_SUCCESS.value
                        || integratedEntrust.getEntrustStatus() == EntrustStatus.PART_TRADED_CANCELED.value
                        || integratedEntrust.getEntrustStatus() == EntrustStatus.SUCCEEDED.value) {
                    NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
                    negotiateQuoteSendBean.setRequestStatus(4);//请求状态(1：待确认  3：已撤销 4：已成交 )
                    setNegotiateQuoteSendBean(negotiateQuoteSendBean, integratedEntrust);
                    retList.add(negotiateQuoteSendBean);
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put(ROOT, retList);
            return new ResultBean(ErrorCode.SUCCESS, map);
        }
        return resultBean;
    }

    private void setNegotiateQuoteSendBean(NegotiateQuoteSendBean negotiateQuoteSendBean, IntegratedEntrustVo integratedEntrust) {
        negotiateQuoteSendBean.setRequestNo(integratedEntrust.getId());
        negotiateQuoteSendBean.setRequestTime(DateUtil.formatDateTime(integratedEntrust.getCreateTime()));
        negotiateQuoteSendBean.setStockCode(integratedEntrust.getStockCode());
        negotiateQuoteSendBean.setStockName(integratedEntrust.getStockName());
        negotiateQuoteSendBean.setBs(integratedEntrust.getEntrustBs());
        negotiateQuoteSendBean.setAmount(integratedEntrust.getEntrustVolumeView());
        negotiateQuoteSendBean.setFullPrice(integratedEntrust.getFullPrice());
        negotiateQuoteSendBean.setEntrustPrice(integratedEntrust.getEntrustPrice());
        negotiateQuoteSendBean.setEntrustBalance(integratedEntrust.getEntrustBalance());
        negotiateQuoteSendBean.setMemberId(integratedEntrust.getDealerName());
        negotiateQuoteSendBean.setTraderCode(integratedEntrust.getTradeName());
        negotiateQuoteSendBean.setInvestorId(integratedEntrust.getInvestorName());
        negotiateQuoteSendBean.setInvestorType(integratedEntrust.getOwnerType());
        negotiateQuoteSendBean.setCpMemberId(integratedEntrust.getPropDealerName());
        negotiateQuoteSendBean.setCpTraderCode(integratedEntrust.getPropTradeName());
        negotiateQuoteSendBean.setCpInvestorId(integratedEntrust.getPropInvestorName());
        negotiateQuoteSendBean.setCpInvestorType(integratedEntrust.getPropOwnerType());
        negotiateQuoteSendBean.setConfirmId(integratedEntrust.getContractId());
        negotiateQuoteSendBean.setPositionStr(integratedEntrust.getPositionStr());
        negotiateQuoteSendBean.setRemark(integratedEntrust.getRemark());
        String requestStatusName = EnumUtil.getDescriptionByCode(RequestStatus.class, negotiateQuoteSendBean.getRequestStatus());
        if (RequestStatus.WAIT_CONFIRM.getCode() == negotiateQuoteSendBean.getRequestStatus()) {
            requestStatusName = "待对手方确认";
        }
        negotiateQuoteSendBean.setRequestStatusStr(requestStatusName);
        negotiateQuoteSendBean.setBsStr(EntrustBs.isBuy(negotiateQuoteSendBean.getBs()) ? EntrustBs.BUY.name : EntrustBs.SELL.name);
        String investorTypeName = EnumUtil.getDescriptionByCode(InvestorType.class, negotiateQuoteSendBean.getInvestorType());
        negotiateQuoteSendBean.setInvestorTypeStr(investorTypeName);
        String cpInvestorTypeName = EnumUtil.getDescriptionByCode(InvestorType.class, negotiateQuoteSendBean.getCpInvestorType());
        negotiateQuoteSendBean.setCpInvestorTypeStr(cpInvestorTypeName);
    }

    public ResultBean addIntegratedRepurchaseQuoteReceive(RepurchaseQuoteReceiveResponse bean) {
        if (ObjectUtil.isNull(bean)) {
            return ResultBean.error(ErrorCode.REQ_PARAMETER_ERROR);
        }

        quoteJob.addIntegratedRepurchaseQuoteReceive(bean);
        return ResultBean.success();
    }

    /**
     * 我收到的协议回购行情
     *
     * @param request 查询条件对象
     * @return 我收到的协议回购行情
     */
    public ResultBean queryRepurchaseQuoteReceive(RepurchaseQuoteReceiveRequest request) {
        List<RepurchaseQuoteReceiveResponse> list = Lists.newArrayList();

        RepurchaseQuoteReceiveQryRequest quoteQryBean = new RepurchaseQuoteReceiveQryRequest();
//        Instruction instruction = instructionService.findById(request.getInstructionId());
//        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, quoteQryBean.getEntrustProp());
//        quoteQryBean.setEntrustProp(entrustProp);
//        quoteQryBean.setAccountId(instruction.getAcctNo());

        List<RepurchaseQuoteReceiveResponse> beanList = terminalHelper.queryRepurchaseQuoteReceive(quoteQryBean);
        if (CollectionUtils.isNotEmpty(beanList)) {
//            RepurchaseQuoteReceiveResponse response = null;
//            for (RepurchaseQuoteReceiveResponse bean : beanList) {
//                response = new RepurchaseQuoteReceiveResponse();
//                filterRepurchaseQuoteReceive(response, bean);
//
//                list.add(response);
//            }
            list.addAll(beanList);
            filterRepurchaseQuoteReceive(list, request);
        }

        return ResultBean.success(MapUtil.builder().put(ROOT, list).build());
    }

    private void filterRepurchaseQuoteReceive(RepurchaseQuoteReceiveResponse response, IntegratedRepurchaseQuoteBean bean) {
        response.setRequestNo(bean.getEntrustNo() + "");
        response.setRequestType(bean.getExchangeType());
        response.setBs(bean.getEntrustBs());
        response.setTradeMarket(null);
        response.setStockCode(bean.getStockCode());
        response.setStockName(bean.getStockName());
        response.setPledgeNumber(BigDecimal.valueOf(bean.getImpawnAmount()));
        response.setRepoAmount(null);
        response.setRepoRate(null);
        response.setDiscount(BigDecimal.valueOf(bean.getFunderRatio()));
        response.setFirstSettleDate(bean.getFirstExchdate() + "");
        response.setCompactTerm(bean.getRepoTerm());
        response.setBackDate(bean.getSettleEndDate() + "");
        response.setRepoMdate(bean.getRepurchaseDate() + "");
        response.setCapDays(bean.getFundUsedDays());
        response.setSwapIntoBonds(bean.getStockCode());
        response.setSwapIntoBondsName(bean.getStockName());
        response.setSwapIntoBondsVolume(null);
        response.setSwapOutBonds(null);
        response.setSwapOutBondsName(null);
        response.setSwapOutBondsVolume(null);
        response.setOrigBonds(bean.getOldImpawnCode());
        response.setOrigBondsName(null);
        response.setOrigBondsVolume(null);
        response.setOrigRepoAmount(BigDecimal.valueOf(bean.getPrevImpawnBalance()));
        response.setTransCode("");
        response.setmIntAmt(BigDecimal.valueOf(bean.getProfit()));
        response.setBackBalance(BigDecimal.valueOf(bean.getSettleBalance()));

        response.setTraderCode(bean.getTraderId());
        response.setMemberId(bean.getAgencyNo());
        response.setInvestorId(null);
        response.setInvestorType(null);

        response.setCpTraderCode(bean.getOppoTraderId());
        response.setCpMemberId(bean.getOppoAgency());
        response.setCpInvestorId(null);
        response.setCpInvestorType(null);

        response.setCpClientName(null);
        response.setOrigSerialNo(null);
        response.setRemark(bean.getRemark());
        response.setAccount(bean.getFundAccount());
        response.setAccountName(null);
    }


    private void filterRepurchaseQuoteReceive(List<RepurchaseQuoteReceiveResponse> list, RepurchaseQuoteReceiveRequest request) {
        log.info("filterRepurchaseQuoteReceive() called with parameters => 【list = {}】, 【request = {}】", list, request);
        if (ObjectUtil.isEmpty(request)) {
            return;
        }
        Stream<RepurchaseQuoteReceiveResponse> stream = list.stream();

        String requestNo = request.getRequestNo();
        if (ObjectUtil.isNotEmpty(requestNo)) {
            stream = stream.filter(item -> StrUtil.equals(item.getRequestNo(), requestNo));
        }

        String origSerialNo = request.getOrigSerialNo();
        if (ObjectUtil.isNotEmpty(origSerialNo)) {
            stream = stream.filter(item -> StrUtil.equals(item.getOrigSerialNo(), origSerialNo));
        }

        String account = request.getAccount();
        if (ObjectUtil.isNotEmpty(account)) {
            stream = stream.filter(item -> (ObjectUtil.isNotEmpty(item.getAccount()) && item.getAccount().contains(account))
                    || (ObjectUtil.isNotEmpty(item.getAccountName()) && item.getAccountName().contains(account)));
        }

        String requestType = request.getRequestType();
        if (ObjectUtil.isNotEmpty(requestType)) {
            stream = stream.filter(item -> StrUtil.equals(item.getRequestType(), requestType));
        }

        String stockCode = request.getStockCode();
        if (ObjectUtil.isNotEmpty(stockCode)) {
            stream = stream.filter(item -> (ObjectUtil.isNotEmpty(item.getStockCode()) && item.getStockCode().contains(stockCode))
                    || (ObjectUtil.isNotEmpty(item.getStockName()) && item.getStockName().contains(stockCode)));
        }

        BigDecimal pledgeNumber = request.getPledgeNumber();
        if (ObjectUtil.isNotEmpty(pledgeNumber)) {
            stream = stream.filter(item -> NumberUtil.equals(NumberUtil.round(NumberUtil.div(item.getPledgeNumber(), 100), 2), pledgeNumber));
        }

        BigDecimal repoAmount = request.getRepoAmount();
        if (ObjectUtil.isNotEmpty(repoAmount)) {
            stream = stream.filter(item -> NumberUtil.equals(NumberUtil.round(item.getRepoAmount(), 2), repoAmount));
        }

        Integer compactTerm = request.getCompactTerm();
        if (ObjectUtil.isNotEmpty(compactTerm)) {
            stream = stream.filter(item -> NumberUtil.equals(item.getCompactTerm(), compactTerm));
        }

        BigDecimal repoRate = request.getRepoRate();
        if (ObjectUtil.isNotEmpty(repoRate)) {
            stream = stream.filter(item -> NumberUtil.equals(NumberUtil.round(item.getRepoRate(), 3), repoRate));
        }

        BigDecimal settleAmount = request.getSettleAmount();
        if (ObjectUtil.isNotEmpty(settleAmount)) {
            stream = stream.filter(item -> NumberUtil.equals(NumberUtil.round(item.getBackBalance(), 2), settleAmount));
        }

        String firstSettleDate = request.getFirstSettleDate();
        if (ObjectUtil.isNotEmpty(firstSettleDate)) {
            stream = stream.filter(item -> StrUtil.equals(item.getFirstSettleDate(), firstSettleDate));
        }

        String remark = request.getRemark();
        if (ObjectUtil.isNotEmpty(remark)) {
            stream = stream.filter(item -> StrUtil.equals(item.getRemark(), remark));
        }

        list = stream.collect(Collectors.toList());
    }

    /**
     * 我发起的协议回购行情
     * 查询本地委托信息，只查询委托状态为 待确认/已成交/已回复/已撤销 的数据
     *
     * @param request 查询条件对象
     * @return 我发起的协议回购行情
     */
    public ResultBean queryRepurchaseQuoteSend(RepurchaseQuoteSendRequest request) {
        ResultBean resultBean = entrustManagement.queryIntegratedEntrust(request.getUserId(),
                request.getInstructionId(), request.getAccountId(), request.getOrderId());
        if (ErrorCode.isFail(resultBean.getErrorCode()) || ObjectUtil.isEmpty(resultBean.getData())) {
            return resultBean;
        }

        List<BaseRepurchaseQuoteSendResponse> retList = Lists.newArrayList();
        List<IntegratedEntrustVo> entrustList = (List<IntegratedEntrustVo>) resultBean.getData();

        BaseRepurchaseQuoteSendResponse sendBean;
        for (IntegratedEntrustVo integratedEntrust : entrustList) {
            int entrustStatus = integratedEntrust.getEntrustStatus();
            sendBean = createSendQuoteByIntegratedEntrust(integratedEntrust);

            if (EnumUtil.in(entrustStatus, REPORTED, REPORTED_TOBE_CANCEL)) {
                sendBean.setRequestStatus(QuoteStatusEnum.TO_BE_CONFIRMED.getCode());
            } else if (CANCELED.codeEquals(entrustStatus)) {
                sendBean.setRequestStatus(QuoteStatusEnum.REVOKED.getCode());
            } else if (EnumUtil.in(entrustStatus, PART_TRADED_TOBE_CANCELED, PART_SUCCESS, PART_TRADED_CANCELED, SUCCEEDED)) {
                sendBean.setRequestStatus(QuoteStatusEnum.DEAL_DONE.getCode());
            }

            fillRepurchaseQuoteSendResponseDescription(sendBean);

            if (EnumUtil.in(entrustStatus, REPORTED, REPORTED_TOBE_CANCEL, CANCELED, PART_TRADED_CANCELED, PART_SUCCESS, PART_TRADED_CANCELED, SUCCEEDED)) {
                retList.add(sendBean);
            }
        }

        List<Integer> requestStatusList = request.getRequestStatus();
        if (CollUtil.isNotEmpty(requestStatusList)) {
            retList = retList.stream().filter(item -> requestStatusList.contains(item.getRequestStatus())).collect(Collectors.toList());
        }

        return ResultBean.success(MapUtil.builder().put(ROOT, retList).build());

    }

    private void fillRepurchaseQuoteSendResponseDescription(BaseRepurchaseQuoteSendResponse sendBean) {
        sendBean.setRequestStatusStr(EnumUtil.getDescriptionByCode(QuoteStatusEnum.class, sendBean.getRequestStatus()));
        sendBean.setRequestTypeStr(EnumUtil.getDescriptionByCode(RepurchaseTypeEnum.class, sendBean.getRequestType()));
        sendBean.setBsStr(EnumUtil.getDescriptionByCode(RepoBsEnum.class, sendBean.getBs()));
//        sendBean.setPledgeNumberStr();
//        sendBean.setInvestorTypeStr();
//        sendBean.setCpInvestorTypeStr();
    }

    private BaseRepurchaseQuoteSendResponse createSendQuoteByIntegratedEntrust(IntegratedEntrustVo entrust) {

        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        if (ObjectUtil.isNull(instruction)) {
            log.warn("查询指令失败 => 【entrustId = {}】，【instructionId = {}】", entrust.getId(), entrust.getInstructionId());
        }

        BaseRepurchaseQuoteSendResponse baseRepurchaseQuoteSendResponse ;
        String ps = entrust.getPs();
        // 正回购
        if (PsEnum.POSITIVE_REPO.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new SQRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.首期.getCode());
            baseRepurchaseQuoteSendResponse.setBs(PsEnum.POSITIVE_REPO.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse, instruction);
        }

        // 逆回购
        else if (PsEnum.REVERSED_REPO.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new SQRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.首期.getCode());
            baseRepurchaseQuoteSendResponse.setBs(PsEnum.REVERSED_REPO.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse, instruction);
        }

        // 中途换券
        else if (PsEnum.REPO_EXCHANGE_COUPONS_MIDWAY.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new ZTHQRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.中途换券.getCode());
            ((NonFirstBaseRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse).setOrigSerialNo(entrust.getOrigSerialNo());

            ZTHQRepurchaseQuoteSendResponse sendResponse = (ZTHQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setDiscount(entrust.getDiscount());
            if ("31".equals(instruction.getEntrustBussType())) {
                sendResponse.setExchangeAmount(instruction.getEntrustAmt());
            } else{
                List<InstructionPledge> pledges = instructionPledgeService.queryInstructionPledgesByInstructionId(instruction.getEntrustId());
                InstructionPledge swapInPledge = pledges.stream().filter(item -> PledgeDisPlaceDirEnum.SWAP_IN.codeEquals(item.getDisplaceDir())).findFirst().orElse(null);
                if (ObjectUtil.isNotNull(swapInPledge)) {
                    sendResponse.setNewDiscount(swapInPledge.getPledgeRatio());
                    sendResponse.setSwapIntoBonds(swapInPledge.getFinprodMarketId());
                    sendResponse.setSwapIntoBondsVolume(swapInPledge.getPledgeNumber());
                }
                InstructionPledge swapOutPledge = pledges.stream().filter(item -> PledgeDisPlaceDirEnum.SWAP_OUT.codeEquals(item.getDisplaceDir())).findFirst().orElse(null);
                if (ObjectUtil.isNotNull(swapOutPledge)) {
                    sendResponse.setSwapOutBonds(swapOutPledge.getFinprodMarketId());
                    sendResponse.setSwapOutBondsVolume(swapOutPledge.getPledgeNumber());
                }
            }
        }

        // 提前购回
        else if (PsEnum.REPO_ADVANCE.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new TQGHRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.提前购回.getCode());
            ((NonFirstBaseRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse).setOrigSerialNo(entrust.getOrigSerialNo());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse, instruction);

            TQGHRepurchaseQuoteSendResponse sendResponse = (TQGHRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;
            sendResponse.setRepoAdvanceDate(DateUtil.getSysDate());
        }

        // 到期购回
/*        else if (PsEnum.REPO_MATURITY.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_MATURITY.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);

            DQGHRepurchaseQuoteSendResponse sendResponse = (DQGHRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setCompactTerm(entrust.getCompactTerm());
        }*/

        // 到期续做
        else if (PsEnum.REPO_RENEW.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new DQXZRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.到期续做.getCode());
            ((NonFirstBaseRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse).setOrigSerialNo(entrust.getOrigSerialNo());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse, instruction);

            DQXZRepurchaseQuoteSendResponse sendResponse = (DQXZRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setCompactTerm(entrust.getCompactTerm());
        }

        // 解除质押
        else if (PsEnum.REPO_UNSTAKE.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse = new JCZYRepurchaseQuoteSendResponse();
            baseRepurchaseQuoteSendResponse.setRequestType(RepurchaseTypeEnum.解除质押.getCode());
            ((NonFirstBaseRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse).setOrigSerialNo(entrust.getOrigSerialNo());

            JCZYRepurchaseQuoteSendResponse sendResponse = (JCZYRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;
            sendResponse.setSettleAmount(instruction.getEntrustAmt());
        } else {
            baseRepurchaseQuoteSendResponse = new BaseRepurchaseQuoteSendResponse();
        }

        baseRepurchaseQuoteSendResponse.setStockCode(entrust.getStockCode());
        baseRepurchaseQuoteSendResponse.setStockName(entrust.getStockName());
        baseRepurchaseQuoteSendResponse.setPledgeNumber(BigDecimal.valueOf(entrust.getEntrustVolume()));
        baseRepurchaseQuoteSendResponse.setCounterId(entrust.getCounterId());
        baseRepurchaseQuoteSendResponse.setTraderCode(entrust.getTradeName());
        baseRepurchaseQuoteSendResponse.setMemberId(entrust.getDealerName());
        baseRepurchaseQuoteSendResponse.setInvestorType(entrust.getOwnerType());
        baseRepurchaseQuoteSendResponse.setCpTraderCode(entrust.getPropTradeName());
        baseRepurchaseQuoteSendResponse.setCpMemberId(entrust.getPropDealerName());
        baseRepurchaseQuoteSendResponse.setCpInvestorId(entrust.getPropInvestorName());
        baseRepurchaseQuoteSendResponse.setInvestorId(entrust.getInvestorId());
        baseRepurchaseQuoteSendResponse.setCpInvestorType(entrust.getPropOwnerType());
        baseRepurchaseQuoteSendResponse.setCpClientName(entrust.getPortfolioName());
        baseRepurchaseQuoteSendResponse.setRemark(entrust.getRemark());

        if (ObjectUtil.isEmpty(baseRepurchaseQuoteSendResponse.getBs())) {
            baseRepurchaseQuoteSendResponse.setBs(instruction.getFirstRepoPs());
        }

        return baseRepurchaseQuoteSendResponse;
    }

    private void fillSQQuoteSendResponse(IntegratedEntrustVo entrust, SQRepurchaseQuoteSendResponse baseRepurchaseQuoteSendResponse, Instruction instruction) {
        baseRepurchaseQuoteSendResponse.setBackBalance(entrust.getBackBalance());
        baseRepurchaseQuoteSendResponse.setBackDate(entrust.getBackDate());
        baseRepurchaseQuoteSendResponse.setCapDays(instruction.getCapDays().intValue());
        baseRepurchaseQuoteSendResponse.setDiscount(entrust.getDiscount());
        baseRepurchaseQuoteSendResponse.setmIntAmt(NumberUtil.div(entrust.getBackBalance(), entrust.getEntrustBalance()));
        baseRepurchaseQuoteSendResponse.setRepoAmount(entrust.getEntrustBalance());
        baseRepurchaseQuoteSendResponse.setRepoRate(entrust.getEntrustPrice());
        baseRepurchaseQuoteSendResponse.setRepoMdate(DateUtil.format(instruction.getRepoMdate()));
        baseRepurchaseQuoteSendResponse.setFirstSettleDate(entrust.getFirstSettleDate());
        baseRepurchaseQuoteSendResponse.setBackDate(entrust.getBackDate());
    }
}


