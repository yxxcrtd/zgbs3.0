package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.*;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.enums.exchange.QuoteStatusEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepoBsEnum;
import com.joyintech.tams.exchange.common.enums.exchange.RepoTypeEnum;
import com.joyintech.tams.exchange.common.enums.invest.PsEnum;
import com.joyintech.tams.exchange.common.request.EntrustQueryRequest;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TickConverter;
import com.joyintech.tams.exchange.common.vo.IntegratedEntrustVo;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.scheduled.quote.QuoteJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            List<NegotiateQuoteSendBean> retList = new ArrayList<NegotiateQuoteSendBean>();
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

    /**
     * 我发起的协议回购行情
     * 查询本地委托信息，只查询委托状态为 待确认/已成交/已回复/已撤销 的数据
     * @param request   查询条件对象
     * @return 我发起的协议回购行情
     */
    public ResultBean queryRepurchaseQuoteSend(EntrustQueryRequest request) {
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

            if (EnumUtil.in(entrustStatus, REPORTED, REPORTED_TOBE_CANCEL, CANCELED, PART_TRADED_CANCELED, PART_SUCCESS, PART_TRADED_CANCELED, SUCCEEDED)) {
                retList.add(sendBean);
            }
        }

        return ResultBean.success(MapUtil.builder().put(ROOT, retList).build());

    }

    private BaseRepurchaseQuoteSendResponse createSendQuoteByIntegratedEntrust(IntegratedEntrustVo entrust) {
        BaseRepurchaseQuoteSendResponse baseRepurchaseQuoteSendResponse = new BaseRepurchaseQuoteSendResponse();
//        baseRepurchaseQuoteSendResponse.setRequestType();
//        baseRepurchaseQuoteSendResponse.setBs();
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
        baseRepurchaseQuoteSendResponse.setCpClientName(null);
        baseRepurchaseQuoteSendResponse.setRemark(entrust.getRemark());
//        baseRepurchaseQuoteSendResponse.setRequestTypeStr();
//        baseRepurchaseQuoteSendResponse.setBsStr();
//        baseRepurchaseQuoteSendResponse.setPledgeNumberStr();
//        baseRepurchaseQuoteSendResponse.setInvestorTypeStr();
//        baseRepurchaseQuoteSendResponse.setCpInvestorTypeStr();
//        baseRepurchaseQuoteSendResponse.setRequestStatus();
//        baseRepurchaseQuoteSendResponse.setRequestStatusStr();

        String ps = entrust.getPs();
        // 正回购
        if (PsEnum.POSITIVE_REPO.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.FIRST_PHASE.getCode());
            baseRepurchaseQuoteSendResponse.setBs(RepoBsEnum.REPURCHASE.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);
        }

        // 逆回购
        else if (PsEnum.REVERSED_REPO.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.FIRST_PHASE.getCode());
            baseRepurchaseQuoteSendResponse.setBs(RepoBsEnum.REVERSE_REPURCHASE.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);
        }

        if (EnumUtil.notIn(ps, PsEnum.POSITIVE_REPO, PsEnum.REVERSED_REPO)) {
            // 需要查询首期的数据
            NonFirstBaseRepurchaseQuoteSendResponse nonFirstBaseRepurchaseQuoteSendResponse = (NonFirstBaseRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse ;
            nonFirstBaseRepurchaseQuoteSendResponse.setOrigSerialNo(null);
            nonFirstBaseRepurchaseQuoteSendResponse.setBs(null);
        }

        // 中途换券
        else if (PsEnum.REPO_EXCHANGE_COUPONS_MIDWAY.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_EXCHANGE_COUPONS_MIDWAY.getCode());

            ZTHQRepurchaseQuoteSendResponse sendResponse = (ZTHQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setDiscount(entrust.getDiscount());
            sendResponse.setNewDiscount(null);
            sendResponse.setExchangeAmount(null);
            sendResponse.setSwapIntoBonds(null);
            sendResponse.setSwapIntoBondsVolume(null);
            sendResponse.setSwapOutBonds(null);
            sendResponse.setSwapOutBondsVolume(null);
        }

        // 提前购回
        else if (PsEnum.REPO_ADVANCE.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_ADVANCE.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);

            TQGHRepurchaseQuoteSendResponse sendResponse = (TQGHRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;
            sendResponse.setRepoAdvanceDate(null);
        }

        // 到期购回
        else if (PsEnum.REPO_MATURITY.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_MATURITY.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);

            DQGHRepurchaseQuoteSendResponse sendResponse = (DQGHRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setCompactTerm(entrust.getCompactTerm());
        }

        // 到期续做
        else if (PsEnum.REPO_RENEW.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_RENEW.getCode());

            fillSQQuoteSendResponse(entrust, (SQRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse);

            DQXZRepurchaseQuoteSendResponse sendResponse = (DQXZRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setCompactTerm(entrust.getCompactTerm());
        }

        // 解除质押
        else if (PsEnum.REPO_UNSTAKE.codeEquals(ps)) {
            baseRepurchaseQuoteSendResponse.setRequestType(RepoTypeEnum.REPO_UNSTAKE.getCode());

            JCZYRepurchaseQuoteSendResponse sendResponse = (JCZYRepurchaseQuoteSendResponse) baseRepurchaseQuoteSendResponse;

            sendResponse.setSettleAmount(null);
        }

        return baseRepurchaseQuoteSendResponse;
    }

    private void fillSQQuoteSendResponse(IntegratedEntrustVo entrust, SQRepurchaseQuoteSendResponse baseRepurchaseQuoteSendResponse) {
        baseRepurchaseQuoteSendResponse.setBackBalance(entrust.getBackBalance());
        baseRepurchaseQuoteSendResponse.setBackDate(entrust.getBackDate());
        baseRepurchaseQuoteSendResponse.setCapDays(null);
        baseRepurchaseQuoteSendResponse.setDiscount(entrust.getDiscount());
        baseRepurchaseQuoteSendResponse.setmIntAmt(NumberUtil.div(entrust.getBackBalance(), entrust.getEntrustBalance()));
        baseRepurchaseQuoteSendResponse.setRepoAmount(entrust.getEntrustBalance());
        baseRepurchaseQuoteSendResponse.setRepoRate(entrust.getEntrustPrice());
        baseRepurchaseQuoteSendResponse.setRepoMdate(null);
        baseRepurchaseQuoteSendResponse.setFirstSettleDate(entrust.getFirstSettleDate());
        baseRepurchaseQuoteSendResponse.setBackDate(entrust.getBackDate());
    }
}


