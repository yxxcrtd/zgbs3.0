package com.joyintech.tams.invest.batch.risktrialrds.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskIndividUnite;
import com.joyintech.tams.common.base.entity.RskIndividUniteKey;
import com.joyintech.tams.invest.batch.risktrialrds.dto.RiskTrialDayTimeResult;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDetailDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.service.SupervisionCalcSvc;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2022/4/25
 */
public abstract class RiskTrialDayEndBaseWriter extends BaseWriter<NeedToWriteEntity> {

    static final String RSK_INDIVID_UNITE_LIST = "rskIndividUniteList";

    static final String RSK_TRIAL_VIOLATION_LIST = "rskTrialViolationList";

    private final List<String> CACHE = Collections.synchronizedList(new ArrayList<>());

    @Autowired(required = false)
    private RiskTrialCommMapper riskTrialCommMapper;

    @Autowired
    private SupervisionCalcSvc supervisionCalcSvc;

    private Date minDate;

    @Override
    public void write(List<? extends NeedToWriteEntity> list) throws Exception {
        List<RiskTrialDayTimeResult> resultList = (List<RiskTrialDayTimeResult>)list;

        for (RiskTrialDayTimeResult resetRateResult : resultList) {
            if (!StringUtil.isNotEmpty(resetRateResult)) {
                continue;
            }
            RiskTrialRetDto dealDto = resetRateResult.getRiskTrialRetDto();
            if (!StringUtil.isNotEmpty(dealDto) || StringUtil.isEmpty(dealDto.getIndId()) || StringUtil.isEmpty(dealDto.getPortfolioId()) || StringUtil.isEmpty(dealDto.getSupStat())) {
                continue;
            }
            //查询个性化联合设置
            List<RskIndividUnite> rskIndividUniteList = getList(RSK_INDIVID_UNITE_LIST);
            try {
                //处理个性化联合指标开始
                Set<String> rskFilterList = rskIndividUniteList.stream().filter(item -> item.getPrtBagId().equals(dealDto.getPortfolioId()) && item.getIndId().equals(dealDto.getIndId())).map(RskIndividUniteKey::getUniteId).collect(Collectors.toSet());
                if (!rskFilterList.isEmpty()) {
                    Set<String> indIdSet = rskIndividUniteList.stream().filter(item -> rskFilterList.contains(item.getUniteId())).map(RskIndividUniteKey::getIndId).collect(Collectors.toSet());
                    if (!indIdSet.isEmpty()) {
                        List<String> supStatList = resultList.stream().filter(item -> StringUtil.isNotEmpty(item.getRiskTrialRetDto()) && indIdSet.contains(item.getRiskTrialRetDto().getIndId())&&StringUtil.equals(dealDto.getPortfolioId(),item.getRiskTrialRetDto().getPortfolioId())).map(entry -> entry.getRiskTrialRetDto().getSupStat()).collect(Collectors.toList());
                        dealDto.setSupUniteStat(RiskUtil.getMinState(supStatList));
                        if (StringUtil.isNotEmpty(dealDto.getMathProcess())) {
                            Map<String, Object> mapProcess = JsonUtils.json2Object(dealDto.getMathProcess(), Map.class);
                            mapProcess.put(Const.UNITE, String.join(",", supStatList));
                            dealDto.setMathProcess(JsonUtils.object2Json(mapProcess));
                        }
                    }
                }
                //处理个性化联合指标结束

                //处理连续违规天数开始
                if (dealDto.getSupStat().equals(BussinessDictCodeListRds.监督状态_触发超限.getValue())) {
                    //查询当天前一天所有指标的连续违规天数
                    List<RiskTrialRetDto> curRiskTrialList = getList(RSK_TRIAL_VIOLATION_LIST);
                    dealDto.setFoulDays(calFoulDays(curRiskTrialList, dealDto.getSupDate(), dealDto.getPortfolioId(), dealDto.getIndId()));
                }
                //处理连续违规天数结束

                //处理监控指标DB
                //DB明细操作
                if (!StringUtil.isEmptyList(dealDto.getRiskTrialRetDetailDtoList())) {
                    List<RiskTrialRetDetailDto> tmpList = duplicateRemoval(dealDto.getRiskTrialRetDetailDtoList());

                    List<RiskTrialRetDetailDto> dataList = new ArrayList<>();
                    // modify by zhangrui1 start
                    for (RiskTrialRetDetailDto info : tmpList) {
                        // modify by zhangrui1 end
                        dataList.add(info);
                        if (dataList.size() % 500 == 0) {
                            riskTrialCommMapper.insertMxPointSupervisionBatch(dataList);
                            dataList.clear();
                        }
                    }
                    if(!dataList.isEmpty()) {
                        riskTrialCommMapper.insertMxPointSupervisionBatch(dataList);
                    }
                }
                //DB操作
                String key = dealDto.getPortfolioId() + GlobalConstant.COLON
                        + dealDto.getIndId() + GlobalConstant.COLON
                        + DateFormatUtils.format(dealDto.getSupDate(),DateUtil.DEFAULT_DATE_FORMAT_EN) + GlobalConstant.COLON
                        + dealDto.getSupFrequency();
                //modify by yjp HFZG-396【风险指标】投资规模类指标，月末控制指标，应仅监控月末日期 20211018 start
                String[] paramYMZBArr=StringUtil.isNotEmpty(CacheUtil.getSysParam("param_risk_ymzb"))?CacheUtil.getSysParam("param_risk_ymzb").split(com.joyintech.framework.constant.GlobalConstant.COMMA):null;
                boolean YMZBFlag=StringUtil.isNotEmpty(paramYMZBArr)? Arrays.asList(paramYMZBArr).contains(dealDto.getIndId())?true:false:false;
                if(YMZBFlag){
                    if(dealDto.getSupDate() != null && DateUtil.isLastDayOfMonth(dealDto.getSupDate()))
                    {
                        if(!CACHE.contains(key)) {
                            supervisionCalcSvc.addPointSupervisionValue(dealDto);
                            CACHE.add(key);
                        }
                    }
                }else{
                    if(!CACHE.contains(key)) {
                        supervisionCalcSvc.addPointSupervisionValue(dealDto);
                        CACHE.add(key);
                    }
                }
                //modify by yjp HFZG-396【风险指标】投资规模类指标，月末控制指标，应仅监控月末日期 20211018 end
            } catch (Exception e){
                throw new Exception("组合代码："+dealDto.getPortfolioId()+ GlobalConstant.COMMA+"指标代码："+dealDto.getIndId()+GlobalConstant.SEMICOLON+e.getMessage(),e);
            }
        }
    }

    protected abstract <T> List<T> getList(String listName);

    protected void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    private long calFoulDays(List<RiskTrialRetDto> curRiskTrialList, Date supDate, String portfolioId, String indId) {
        long foulDays = curRiskTrialList
                .stream()
                .filter(item -> item.getPortfolioId().equals(portfolioId) && item.getIndId().equals(indId))
                .map(item-> null != item.getFoulDays() ? item.getFoulDays() : 0L)
                .findFirst()
                .orElse(0L);
        return foulDays + (this.minDate == null ? 1 : DateUtil.getBetweenDayNumber(this.minDate, supDate));
    }

    // add by zhangrui1 批量去重 start
    /**
     * 数据去重<br>
     *
     * @param srcList 原始列表数据
     * @return 去重后数据
     */
    private List<RiskTrialRetDetailDto> duplicateRemoval(List<RiskTrialRetDetailDto> srcList) {
        if (StringUtil.isEmptyList(srcList)) {
            return srcList;
        }
        List<RiskTrialRetDetailDto> resList = new ArrayList<>();
        // 按主键过滤
        Map<String, List<RiskTrialRetDetailDto>> map = srcList.stream().collect(
                Collectors.groupingBy(
                        dto -> dto.getPortfolioId() + dto.getIndId() + DateUtil.format(dto.getSupDate())
                                + dto.getSupFrequency() + dto.getAssetCode()));
        // 去重
        map.entrySet().forEach(en -> {
            List<RiskTrialRetDetailDto> valList = en.getValue();
            RiskTrialRetDetailDto item = valList.stream().sorted(
                    (dto1, dto2) -> DateUtil.compareDate(dto2.getUpdateTime(),
                            dto1.getUpdateTime())).findFirst().get();
                String key = item.getPortfolioId() + GlobalConstant.COLON
                        + item.getIndId() + GlobalConstant.COLON
                        + DateFormatUtils.format(item.getSupDate(),DateUtil.DEFAULT_DATE_FORMAT_EN) + GlobalConstant.COLON
                        + item.getSupFrequency() + GlobalConstant.COLON
                        + item.getAssetCode();
                if(!CACHE.contains(key)) {
                    CACHE.add(key);
                    resList.add(item);
                }
        });
        return resList;
    }
    // add by zhangrui1 批量去重 end

}
