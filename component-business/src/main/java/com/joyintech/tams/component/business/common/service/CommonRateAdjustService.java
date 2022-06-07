/*
 * 文件名：CommonRateAdjust.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.joyintech.tams.component.business.common.dao.CommonBusinessMapper;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.common.dto.AssetCommonInvRateAdjustDto;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.RateResetCal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

/**
 * 利率报价共通
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月20日
 * @see CommonRateAdjust
 * @since 1.0
 */
@Service
public class CommonRateAdjustService extends BaseService<FinPayInterestStdInfoDto>{

    @Autowired
    private CommonBusinessMapper commonBusinessMapper;
    /**
     * 处理资产报价信息
     * 用于计算支付计划
     * <功能详细描述>
     * @param scai
     * @param is
     * @param scheList
     * @param holidayList
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetCommonInvRateAdjustDto> escapRateAdjust(AssetCommonInfoDto scai,FinCashPayRule is,List<FinCashPlanDto> scheList,List<String> holidayList){
        List<AssetCommonInvRateAdjustDto> rateList = new ArrayList<AssetCommonInvRateAdjustDto>();
        //固定利率
     // 一体化对应etliu start
//        if(StringUtil.equals(scai.getRateType(), DictCodeList.利率类型_固定利率.getValue())){
        if(StringUtil.equals(scai.getIntType(), DictCodeList.利率类型_固定利率.getValue())){
     // 一体化对应etliu end
            List<AssetCommonInvRateAdjustDto> findRateList = getRateAdjust(scai);
            rateList.addAll(findRateList);
            AssetCommonInvRateAdjustDto invRateQuo = new AssetCommonInvRateAdjustDto();
            invRateQuo.setEffDate(scai.getVdate());
            invRateQuo.setYield(scai.getRate());
            invRateQuo.setBasis(scai.getBasis());
            rateList.add(invRateQuo);
        }else{
            //浮动利率
            //取基准行情信息 
            //利差(%),上限(%),下限(%)
            if(null!=scai.getFinRateInfo()){
                BigDecimal spreadRate = ArithmeticUtil.divide(scai.getFinRateInfo().getSpreadRate(), new BigDecimal(100), 14);
                // 一体化对应etliu
//                BigDecimal rateMax = ArithmeticUtil.divide(scai.getFinRateInfo().getRateMax(), new BigDecimal(100), 14);
//                BigDecimal rateMin = ArithmeticUtil.divide(scai.getFinRateInfo().getRateMin(), new BigDecimal(100), 14);
                //update by zhangnian 20191030 start
                //上限、下限为空时，不处理，否则后续计算报价信息会取0
                if(null!=scai.getFinRateInfo().getHighestRate()){
                    BigDecimal rateMax = ArithmeticUtil.divide(scai.getFinRateInfo().getHighestRate(), new BigDecimal(100), 14);
                    scai.getFinRateInfo().setHighestRate(rateMax);
                }
                if(null!=scai.getFinRateInfo().getLowestRate()){
                    BigDecimal rateMin = ArithmeticUtil.divide(scai.getFinRateInfo().getLowestRate(), new BigDecimal(100), 14);
                    scai.getFinRateInfo().setLowestRate(rateMin);
                }
                //update by zhangnian 20191030 end
                scai.getFinRateInfo().setSpreadRate(spreadRate);
                // 一体化对应etliu
//                scai.getFinRateInfo().setRateMax(rateMax);
//                scai.getFinRateInfo().setRateMin(rateMin);

            }
            // 一体化对应etliu
//            rateList = getMstRateInfo(scai.getFinRateInfo().getIndexId());
            rateList = getMstRateInfo(scai.getFinRateInfo().getBenchmarkId());
            rateList = RateResetCal.cal(scheList, is, scai.getFinRateInfo(), rateList,
                scai, 0, holidayList, AssetCommonInvRateAdjustDto.class);
        }
        // 按生效日从小到大排序
        Collections.sort(rateList, new Comparator<AssetCommonInvRateAdjustDto>() {
            @Override
            public int compare(AssetCommonInvRateAdjustDto o1, AssetCommonInvRateAdjustDto o2) {
                return o1.getEffDate().compareTo(o2.getEffDate());
            }
        });
        return rateList;
    }
    /**
     * 获取利率调整信息
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetCommonInvRateAdjustDto> getRateAdjust(AssetCommonInfoDto scai){
        return restTemplate.getForList(ServiceApplication.ASSET_SERVICE_NAME
            + "/FinAssetRateAdj/getFinAssetRateAdjByCashId?finprodId="
            + scai.getFinprodId()+"&finprodType="+DictCodeListExt1.现金流业务类型_金融产品.getValue()
            +"&cashType="+ DictCodeListExt1.现金流二级类型_利息.getValue()
            +"&isInitial="+DictCodeList.是否_否.getValue()
            +"&cashId="+scai.getCashId(),
            AssetCommonInvRateAdjustDto.class);
    }
    
    /**
     * 获取利率行情信息
     * <功能详细描述>
     * @param indexId    基准编号
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetCommonInvRateAdjustDto> getMstRateInfo(String indexId){
        return commonBusinessMapper.getMstRateInfo(indexId);

//                restTemplate.getForList(ServiceApplication.ASSET_SERVICE_NAME
//            + "/FinAssetRateAdj/getMstRateInfo?indexId="
//            + indexId,
//            AssetCommonInvRateAdjustDto.class);
    }
}
