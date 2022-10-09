package com.joyintech.tams.invest.batch.risktrialrds.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskIndividAssetDel;
import com.joyintech.tams.common.base.entity.RskIndividSpecialStage;
import com.joyintech.tams.common.base.entity.RskPortfolioPackageDtl;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeReaderResult;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeResult;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.service.RiskIndConditionLineService;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndividLimit;
import com.joyintech.tams.invest.common.risktrialrds.RiskTrialComm;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionParameterDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionTargetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.Task;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 何阳阳
 * @date 2022/4/25
 */
public class RiskTrialDayEndBaseProcessor extends BaseProcessor<RiskTrialDayTimeReaderResult, RiskTrialDayTimeResult> {

    private static final Logger logger = LoggerFactory.getLogger(RiskTrialDayEndBaseProcessor.class);

    @Autowired
    protected RiskTrialComm riskTrialComm;

    @Autowired(required = false)
    protected RiskTrialCommMapper riskTrialCommMapper;

    // add by zhangrui1 自定义条件阈值 start
    @Autowired
    private RiskIndConditionLineService lineService;
    // add by zhangrui1 自定义条件阈值 end

    @Override
    public RiskTrialDayTimeResult process(RiskTrialDayTimeReaderResult result) throws Exception {
        RiskTrialDayTimeResult resultDto = null;
        Map<String,Object> mapOther = super.getParams();
        RiskTrialRetDto retEntry = executeOne(result, mapOther);
        if(null != retEntry) {
            resultDto = new RiskTrialDayTimeResult();
            resultDto.setRiskTrialRetDto(retEntry);
        }
        return resultDto;
    }

    protected RiskTrialRetDto executeOne(RiskTrialDayTimeReaderResult result, Map<String,Object> mapOther) {
        try{
            Task task = result.getTaskQueue();
            RiskTrialRetDto retEntry = riskTrialComm.afterRiskTrialDeal(mapOther, task, BussinessDictCodeListRds.监督频率_日终.getValue());
            if(StringUtil.isNotEmpty(retEntry)&&!StringUtil.isEmpty(retEntry.getIndId())&&!StringUtil.isEmpty(retEntry.getPortfolioId())&&!StringUtil.isEmpty(retEntry.getSupStat())){
                return retEntry;
            }
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected synchronized Map<String, Object> getMapParams(String valDate) {
        List<SupervisionParameterDto> supervisionParameterDtoList = riskTrialCommMapper.getIndicatorParamAll(valDate, null, null);
        //查询个性化设置相关信息start--
        List<RskIndividSpecialStage> rskIndividSpecialStageList=riskTrialCommMapper.getRiskIndividSpecialStage(valDate, BussinessDictCodeListRds.阶段类型_特殊阶段.getValue());
        List<RskIndividSpecialStage> rskIndividSpecialStageAstList=riskTrialCommMapper.getRiskIndividSpecialStage(valDate, BussinessDictCodeListRds.阶段类型_特殊资产阶段.getValue());
        List<RskIndividAssetDel> rskIndividAssetDelList=riskTrialCommMapper.getRiskIndividAstDel(valDate);
        // add by zhangrui1 自定义条件阈值 start
        Map<String, Map<String, IIndividLimit>> limitMap = lineService.getRiskIndConditionLine(DateUtil.parse(valDate), null);
        List<RskIndividSpecialStage> temp = new ArrayList<>();
        if (!CollectionUtils.isEmpty(limitMap)) {
            limitMap.forEach((key, value) -> {
                if (!CollectionUtils.isEmpty(value)) {
                    value.entrySet().forEach(en -> {
                        IIndividLimit limit = en.getValue();
                        if (null != limit) {
                            RskIndividSpecialStage stage = new RskIndividSpecialStage();
                            stage.setApproveCond(limit.getApproveCond());
                            stage.setWarnCond(limit.getWarnCond());
                            stage.setFoulCond(limit.getFoulCond());
                            stage.setIndId(key);
                            stage.setPrtBagId(en.getKey());
                            temp.add(stage);
                        }
                    });
                }
            });
        }
        if (StringUtil.isEmptyList(rskIndividSpecialStageList)) {
            rskIndividSpecialStageList = temp;
        }
        else if (!StringUtil.isEmptyList(temp)) {
            for (RskIndividSpecialStage stage : rskIndividSpecialStageList) {
                Iterator<RskIndividSpecialStage> it = temp.iterator();
                while (it.hasNext()) {
                    RskIndividSpecialStage item = it.next();
                    if (null == item
                            || (StringUtil.equals(item.getIndId(), stage.getIndId())
                            && StringUtil.equals(item.getPrtBagId(), stage.getPrtBagId()))) {
                        it.remove();
                    }
                }
            }
            if (!StringUtil.isEmptyList(temp)) {
                rskIndividSpecialStageList.addAll(temp);
            }
        }
        // add by zhangrui1 自定义条件阈值 end
        Map<String, Object> params = new HashMap<>(4);
        params.put(Const.SUP_PARAM,supervisionParameterDtoList);
        params.put(Const.IND_SPE_STAGE,rskIndividSpecialStageList);
        params.put(Const.IND_SPE_STAGE_AST,rskIndividSpecialStageAstList);
        params.put(Const.IND_AST_DEL,rskIndividAssetDelList);
        return params;
    }
}
