package com.joyintech.tams.invest.batch.risktrialrds.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 何阳阳
 * @date 2022/4/25
 */
@Service
@Scope("prototype")
public class RiskTrialDayEndProcessor extends RiskTrialDayEndBaseProcessor {

    public void initParams(String valDate) {
        Map<String, Object> params = getMapParams(valDate);
        setParams(params);
    }
}
