package com.joyintech.tams.invest.batch.assetdailyinfo.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.business.common.dto.SecInfoModelDto;
import com.joyintech.tams.common.base.dao.AssetDailyInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.batch.assetdailyinfo.dao.AssetDailyInfoGenerationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.busicommon.productRating.dto.CommonProductRatingDto;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.common.base.dao.AssetTargetDbMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dao.SecPosAutoGenerationMapper;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dto.AssetTargetDbDto;

/**
 * 资产每日信息生成批处理Service
 *
 * @author wangyang1
 * @version 1.0 2022年4月12日
 * @see AssetDailyInfoGenerationService
 * @since 1.0
 */
@Service
public class AssetDailyInfoGenerationService {

    /**
     * AssetDailyInfoGenerationMapper
     */
    @Autowired
    private AssetDailyInfoGenerationMapper assetDailyInfoGenerationMapper;

    @Autowired
    private AssetDailyInfoMapper assetDailyInfoMapper;


    /**
     * 资产每日信息生成
     *
     * @param batchDate 执行日
     * @see
     * @since 1.0
     */
    public void execute(Date batchDate) {
        // 删除当日资产每日信息数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("batchDate", batchDate);
        assetDailyInfoGenerationMapper.deleteTodayAssetDailyInfo(paramMap);

        List<String> assetInfoList = assetDailyInfoGenerationMapper.getAssetInfo(paramMap);
        for (String finprodId: assetInfoList){
            AssetDailyInfo dto = new AssetDailyInfo();
            dto.setFinprodId(finprodId);
            dto.setCdate(batchDate);
            //计算代偿期
            calPendingPeriod(dto, batchDate);
            assetDailyInfoMapper.insertSelective(dto);
        }
    }

    /**
     * 计算代偿期
     * @param assetDailyInfo
     * @param cDate
     */
    public void calPendingPeriod(AssetDailyInfo assetDailyInfo,Date cDate) {
            Map map = new HashMap();
        map.put("sysDate", cDate);
        map.put("finprodId",assetDailyInfo.getFinprodId());
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        map.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        map.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        map.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        map.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());
        SecInfoModelDto dto = assetDailyInfoGenerationMapper.getSecInfoModel(map);
        if (StringUtil.isNotEmpty(dto) && StringUtil.isNotEmpty(dto.getEndDate())
           && StringUtil.isNotEmpty(dto.getStartDate())) {
            // tDate默认取到期日
            Date tDate = dto.getEndDate();
            Date startDate = dto.getStartDate();
            // 计算结果
            List<Date[]> result = new ArrayList<>();
            // 下一行权日
            Date nextExeDate = dto.getNextExeDate();
            // 含权标识
            String isRight = dto.getIsRight();
            String pendingPeriodStr1 = "";
            String pendingPeriodStr2 = "";
            // 到期展示-- // Sonar Fix modify by huwei 220608
            if (cDate.compareTo(tDate) > 0) {
            } else {
                // 含权债TDTAE取值时，如当前计算日以后存在回售或赎回行权计划的且理论行权日不为到期日的，
                // 则TDATE取2个值计算2个结果，即TDATE分别为最近一个理论行权日、到期日，否则取到期日计算1个结果；
                if (DictCodeList.是否_是.getValue().equals(isRight) && null != nextExeDate
                        && DateUtil.compareDate(nextExeDate, tDate) != 0) {
                    result = calcDayInYear(startDate, cDate, nextExeDate, 1);
                    Map<String, Object> exPendingPeriodMap = getPendingPeriod(result, cDate, nextExeDate);
                    assetDailyInfo.setExPendingPeriod((BigDecimal) exPendingPeriodMap.get("pendingPeriod"));
                    assetDailyInfo.setExPendingPeriodUnit((String) exPendingPeriodMap.get("pendingPeriodUnit"));
                }
                result = calcDayInYear(startDate, cDate, tDate, 1);
                Map<String, Object> mPendingPeriodMap = getPendingPeriod(result, cDate, tDate);
                assetDailyInfo.setMPendingPeriod((BigDecimal) mPendingPeriodMap.get("pendingPeriod"));
                assetDailyInfo.setMPendingPeriodUnit((String) mPendingPeriodMap.get("pendingPeriodUnit"));
            }
        }
    }

    private Map<String,Object> getPendingPeriod(List<Date[]> result,Date cDate, Date tDate){
        Map<String,Object> map = new HashMap<>();
        Date yearStartDate = null;
        Date yearEndDate = null;
        BigDecimal pendingPeriod = BigDecimal.ZERO;
        if((int)DateUtil.getBetweenDayNumber(cDate,tDate)<365){
            pendingPeriod=BigDecimal.valueOf(DateUtil.getBetweenDayNumber(cDate,tDate));
            map.put("pendingPeriod",pendingPeriod);
            map.put("pendingPeriodUnit","D");
            return map;
        }else{
            for(int i = 0; i < result.size(); i++ ){
                Date maxStartDate = null;
                Date minEndDate = null;
                // 判断计息年度是否计算成功
                if(null != result && result.get(i).length == 2) {
                    yearStartDate=result.get(i)[0];
                    yearEndDate=result.get(i)[1];
                }
                // 比较计算日和计息年度开始日，取最大
                if(DateUtil.compareDate(cDate, yearStartDate)>=0){
                    maxStartDate = cDate;
                }else{
                    maxStartDate = yearStartDate;
                }
                // 比较计算日和计息年度截止日日，取最小
                if(DateUtil.compareDate(tDate, yearEndDate)==-1){
                    minEndDate = tDate;
                }else{
                    minEndDate = yearEndDate;
                }
                pendingPeriod = ArithmeticUtil.doAdd(pendingPeriod,
                        ArithmeticUtil.divide(BigDecimal.valueOf(DateUtil.getBetweenDayNumber(maxStartDate,minEndDate)),
                                BigDecimal.valueOf(DateUtil.getBetweenDayNumber(yearStartDate,yearEndDate)), 2));
            }
        }
        map.put("pendingPeriod",pendingPeriod);
        map.put("pendingPeriodUnit","Y");
        return map;
    }

    /**
     * 计算calDate所在的计息年度结束日
     * @param startDate 开始日
     * @param calDate 计算日
     * @param flag 向前向后推算标识（1:向后，-1：向前）
     * @return 计息年度结束日
     */
    private List<Date[]> calcDayInYear(Date startDate, Date calDate, Date endDate,int flag) {
        List<Date[]> dateList = new ArrayList<>();
        // 计息年度开始日
        Date start = null;
        // 计息年度结束日
        Date end = startDate;
        int index = 0;
        do {
            index ++;
            start = end;
            if(null != startDate) {
                end = DateUtil.addYear(startDate,flag * index);
            }
            if(flag > 0 ){
                if(start != null && (DateUtil.compareDate(end,calDate)>=0)) {
                    dateList.addAll(Collections.singleton(new Date[]{start,end}));
                }
            }else{
                if(start != null && (DateUtil.compareDate(calDate,start)>=0)) {
                    dateList.addAll(Collections.singleton(new Date[]{start,end}));
                }
            }
        } while((flag > 0 && (DateUtil.compareDate(endDate,end)>=0))
                || (flag < 0 && (DateUtil.compareDate(end,endDate)>=0)));
        return dateList;
    }

}
