package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.BaseBussinessDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

/**
 * 〈功能详细描述〉
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月17日
 * @since 1.0
 */
@Service
public class PositionRequestServiceCreditProtectionCertificatePeriod {

    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void sendCreditProtectionCertificatePeriod(CreditProtectionCertificatePeriodSaveDto entity, String decisionAct, String systemAct, String flowStatus) {
        log.info("sendCreditProtectionCertificatePeriod() called with parameters => 【entity = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        resultMap.put("flowStatus", flowStatus);
        period001(entity, resultMap);
        PositionHttpRequest.send(resultMap);
    }

    public void sendRevokeCreditProtectionCertificatePeriod(CreditProtectionCertificatePeriodSaveDto entity) {
        log.info("sendRevokeCreditProtectionCertificatePeriod() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
        String ps = "T100";
        if (StringUtil.equals(entity.getPaBussType(), DictCodeListExt1.平安业务类型_到期.getValue())) {
            ps = DictCodeList.交易方向_到期.getValue();
        }
        PositionHttpRequest.dorevoke(entity.getBusiCode(), DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_信用保护凭证交易.getValue(), ps);
    }

    private void period001(CreditProtectionCertificatePeriodSaveDto entity, Map<String, Object> resultMap) {

        resultMap.put(INTERFACE_TYPE, "CPC001");
        resultMap.put("userId", LoginUserUtil.getUserName());
//        resultMap.put("computerId","");
        resultMap.put(TOTAL_TRADE_ID, entity.getBusiCode());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());

        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }

        resultMap.put("balanceCheck", balanceCheck);
        resultMap.put(BUSI_TYPE, DictCodeListExt3.头寸业务类型_信用保护凭证存续期.getValue());
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_交易登记.getValue());
        if (StringUtil.equals(entity.getPaBussType(), DictCodeListExt1.平安业务类型_到期.getValue())) {
            resultMap.put(PS, DictCodeList.交易方向_到期.getValue());
        } else {
            resultMap.put(PS, "T100");
        }

        resultMap.put(TRADE_PLACE, entity.getTradeMarket());

        resultMap.put(WORK_FLOW_ID, entity.getWorkflowId());
        resultMap.put("ceSettleType", entity.getSettleMethod());
        resultMap.put("cdate", DateUtil.format(entity.getTradeDate()));
        resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());

        List<Map<String, Object>> ptlList = new ArrayList<>();
        List<Map<String, Object>> ptlSubList = new ArrayList<>();
        List<BaseBussinessDto> baseBussinessDtos = entity.getBaseBussinessDtos();
        for (BaseBussinessDto baseBussinessDto : baseBussinessDtos) {
            Map<String, Object> ptl = new HashMap<>();
            ptl.put("portfolioId", baseBussinessDto.getTrdProductDeal().getPortfolioId());
            ptl.put(FINPROD_ID, baseBussinessDto.getTrdProductDeal().getFinprodId());
            ptl.put("tradeId", baseBussinessDto.getTrdProductDeal().getTradeId());
//            ptl.put("cashAcctId",baseBussinessDto.getTrdProductDeal())
            ptl.put("secAcctId", baseBussinessDto.getTrdProductDeal().getSecManageAcctId());
            ptl.put("secAmount", baseBussinessDto.getTrdProductDeal().getShareAmt());
            ptl.put("cashAmount", baseBussinessDto.getTrdProductDeal().getTradeAmt());
            ptl.put("cashAcctId", baseBussinessDto.getBillingDetail().getAcctNo());
            ptlList.add(ptl);
            if (StringUtil.equals(entity.getSettleMethod(), DictCodeListExt4.结算方式_标的_实物结算.getValue())) {
                Map<String, Object> ptlSub = new HashMap<>();
                ptlSub.put("portfolioId", baseBussinessDto.getTrdProductDeal().getPortfolioId());
                ptlSub.put(FINPROD_ID, baseBussinessDto.getTrdProductDeal().getFinprodId());
                ptlSub.put("tradeId", baseBussinessDto.getTrdProductDeal().getTradeId());
                ptlSub.put("secAcctId", baseBussinessDto.getTrdProductDeal().getSecManageAcctId());
                ptlSub.put("secAmount", baseBussinessDto.getTrdProductDeal().getShareAmt());
                ptlSubList.add(ptlSub);
            }
        }
        resultMap.put(PTL_LIST, ptlList);
        resultMap.put(PTL_SUB_LIST, ptlSubList);
    }
}
