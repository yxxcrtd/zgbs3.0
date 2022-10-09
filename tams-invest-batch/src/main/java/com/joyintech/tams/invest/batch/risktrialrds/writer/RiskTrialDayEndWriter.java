package com.joyintech.tams.invest.batch.risktrialrds.writer;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.RskIndividUnite;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2022/4/25
 */
@Service
@Scope("prototype")
public class RiskTrialDayEndWriter extends RiskTrialDayEndBaseWriter {
    @Autowired(required = false)
    private RiskTrialCommMapper riskTrialCommMapper;
    public void initParams(Date platDate) {
        String supFrequency = BussinessDictCodeListRds.监督频率_日终.getValue();
        String preDate = DateUtil.addDay(DateUtil.format(platDate),-1);
        //查询个性化联合设置
        List<RskIndividUnite> rskIndividUniteList = riskTrialCommMapper.getRiskIndividUnite();
        //查询当天前一天所有指标的连续违规天数
        List<RiskTrialRetDto> rskTrialViolationList = riskTrialCommMapper.getRiskTrialRetInfo(supFrequency, preDate);

        Map<String, Object> params = new HashMap<>(2);
        params.put(RSK_INDIVID_UNITE_LIST, rskIndividUniteList);
        params.put(RSK_TRIAL_VIOLATION_LIST, rskTrialViolationList);
        setParams(params);
    }

    @Override
    protected <T> List<T> getList(String listName) {
        if(StringUtils.equals(listName, RSK_TRIAL_VIOLATION_LIST)) {
            return (List<T>)getParams().get(RSK_TRIAL_VIOLATION_LIST);
        }
        return (List<T>)getParams().get(RSK_INDIVID_UNITE_LIST);
    }
}
