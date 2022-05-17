package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public void sendCreditProtectionCertificatePeriod(CreditProtectionCertificatePeriodSaveDto entity, String decisionAct, String systemAct) {
        log.info("sendCreditProtectionCertificatePeriod() called with parameters => 【entity = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        period001(entity,resultMap);
        PositionHttpRequest.send(resultMap);
    }

    private void period001(CreditProtectionCertificatePeriodSaveDto entity, Map<String, Object> resultMap) {
//        resultMap.put("interfaceType","");
        resultMap.put("userId", LoginUserUtil.getUserName());
//        resultMap.put("computerId","");
        resultMap.put("totalTradeId",entity.getBusiCode());
        resultMap.put("flowStatus",DictCodeListExt3.流程节点_审批生效.getValue());
        resultMap.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());

        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }

        resultMap.put("balanceCheck",balanceCheck);


    }
}
