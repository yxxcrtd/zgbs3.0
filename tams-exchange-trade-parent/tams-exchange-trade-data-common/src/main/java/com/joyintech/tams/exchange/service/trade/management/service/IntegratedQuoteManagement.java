package com.joyintech.tams.exchange.service.trade.management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteQueryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteSendBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.request.EntrustQueryRequest;
import com.joyintech.tams.exchange.common.utils.TickConverter;
import com.joyintech.tams.exchange.datacommon.db.service.FuturesInstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.scheduled.quote.QuoteJob;

/**
 * @ClassName IntegratedQuoteManagement
 * @Description 综合交易行情查询
 * @Author liujianping
 * @Date 2020/9/21
 * @Version
 **/
@Component
public class IntegratedQuoteManagement {
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
        return new ResultBean(ErrorCode.SUCCESS, terminalHelper.queryNegotiateQuoteReceive(negotiateQuoteQueryBean));
    }
    
    public ResultBean queryNegotiateQuoteSend(EntrustQueryRequest request) {
    	ResultBean resultBean = entrustManagement.queryIntegratedEntrust(request.getUserId(), request.getInstructionId(), request.getAccountId(), request.getOrderId());
    	if(null!=resultBean && null!=resultBean.getData()) {
    		List<NegotiateQuoteSendBean> retList = new ArrayList<NegotiateQuoteSendBean>();
    		List<IntegratedEntrust> entrustList = (List<IntegratedEntrust>) resultBean.getData();
    		for(IntegratedEntrust integratedEntrust:entrustList) {
    			if(integratedEntrust.getEntrustStatus()==EntrustStatus.REPORTED.value
    					||integratedEntrust.getEntrustStatus()==EntrustStatus.REPORTED_TOBE_CANCEL.value) {
    				NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
    				negotiateQuoteSendBean.setRequestStatus(1);//请求状态(1：待确认  3：已撤销 4：已成交 )
    				setNegotiateQuoteSendBean(negotiateQuoteSendBean,integratedEntrust);
    				retList.add(negotiateQuoteSendBean);
    			}else if(integratedEntrust.getEntrustStatus()==EntrustStatus.CANCELED.value) {
    				NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
    				negotiateQuoteSendBean.setRequestStatus(3);//请求状态(1：待确认  3：已撤销 4：已成交 )
    				setNegotiateQuoteSendBean(negotiateQuoteSendBean,integratedEntrust);
    				retList.add(negotiateQuoteSendBean);
    			}else if(integratedEntrust.getEntrustStatus()==EntrustStatus.PART_TRADED_TOBE_CANCELED.value
    					||integratedEntrust.getEntrustStatus()==EntrustStatus.PART_SUCCESS.value
    					||integratedEntrust.getEntrustStatus()==EntrustStatus.PART_TRADED_CANCELED.value
    					||integratedEntrust.getEntrustStatus()==EntrustStatus.SUCCEEDED.value) {
    				NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
    				negotiateQuoteSendBean.setRequestStatus(4);//请求状态(1：待确认  3：已撤销 4：已成交 )
    				setNegotiateQuoteSendBean(negotiateQuoteSendBean,integratedEntrust);
    				retList.add(negotiateQuoteSendBean);
    			}
    		}
    		return new ResultBean(ErrorCode.SUCCESS, retList);
    	}
    	return resultBean;
    }
    private void setNegotiateQuoteSendBean(NegotiateQuoteSendBean negotiateQuoteSendBean,IntegratedEntrust integratedEntrust) {
    	negotiateQuoteSendBean.setRequestNo(integratedEntrust.getReportNo());
    	negotiateQuoteSendBean.setRequestTime(integratedEntrust.getReportTime());
    	negotiateQuoteSendBean.setStockCode(integratedEntrust.getStockCode());
    	negotiateQuoteSendBean.setStockName(integratedEntrust.getStockName());
    	negotiateQuoteSendBean.setBs(integratedEntrust.getEntrustBs());
    	negotiateQuoteSendBean.setAmount(integratedEntrust.getEntrustVolume());
    	negotiateQuoteSendBean.setFullPrice(integratedEntrust.getFullPrice());
    	negotiateQuoteSendBean.setEntrustPrice(integratedEntrust.getEntrustPrice());
    	negotiateQuoteSendBean.setEntrustBalance(integratedEntrust.getEntrustBalance());
    	negotiateQuoteSendBean.setMemberId(integratedEntrust.getSellerNo());
    	negotiateQuoteSendBean.setTraderCode(integratedEntrust.getSellerNo());
    	negotiateQuoteSendBean.setInvestorId(integratedEntrust.getInvestorId());
    	negotiateQuoteSendBean.setInvestorType(integratedEntrust.getOwnerType());
    	negotiateQuoteSendBean.setCpMemberId(integratedEntrust.getPropSeatName());
    	negotiateQuoteSendBean.setCpTraderCode(integratedEntrust.getLiaisonTel());
    	negotiateQuoteSendBean.setCpInvestorId(integratedEntrust.getPropInvestorId());
    	negotiateQuoteSendBean.setCpInvestorType(integratedEntrust.getPropInvestorName());
    	negotiateQuoteSendBean.setConfirmId(integratedEntrust.getContractId());
    	negotiateQuoteSendBean.setPositionStr(integratedEntrust.getPositionStr());
    	negotiateQuoteSendBean.setRemark(integratedEntrust.getRemark());
    }
}


