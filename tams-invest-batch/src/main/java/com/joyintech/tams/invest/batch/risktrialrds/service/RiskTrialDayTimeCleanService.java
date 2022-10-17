package com.joyintech.tams.invest.batch.risktrialrds.service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeReaderResult;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2022/4/27
 */
@Service
public class RiskTrialDayTimeCleanService {

    private final Logger logger = LoggerFactory.getLogger(RiskTrialDayTimeCleanService.class);

    @Autowired
    private RiskTrialCommMapper riskTrialCommMapper;

//    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeDailyData(Date supDate, List<RiskTrialDayTimeReaderResult> results) {
        //获取需要清理的数据
        if (CollectionUtils.isNotEmpty(results)) {
            String supFrequency = BussinessDictCodeListRds.监督频率_日终.getValue();
            List<String> portfolioIdList = results
                    .stream()
                    .filter(result -> result.getTaskQueue() != null)
                    .map(result -> result.getTaskQueue().getPoint().getPortfolioId())
                    .distinct()
                    .collect(Collectors.toList());
            removeDailyData(supDate, supFrequency, portfolioIdList);
        }
    }

//    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeDailyDataNew(Date supDate) {
        String supFrequency = BussinessDictCodeListRds.监督频率_日终.getValue();
        removeDailyData(supDate, supFrequency, null);
    }

    private void removeDailyData(Date supDate, String supFrequency, List<String> portfolioIdList) {
        String dateStr = DateUtil.format(supDate);
        logger.info("清除为日期[{}]的指标计算结果数据开始", dateStr);
        int pointValCount = riskTrialCommMapper.deleteRiskValBatchDataNew(supDate, supFrequency, portfolioIdList);
        int pointDetValCount = riskTrialCommMapper.deleteRiskValDetBatchDataNew(supDate, supFrequency, portfolioIdList);
        logger.info("清除为日期[{}]的指标计算结果数据: RSK_ISUP_POINT_VAL -> {}条，RSK_ISUP_POINT_DET_VAL -> {}条", dateStr, pointValCount, pointDetValCount);
    }

}
