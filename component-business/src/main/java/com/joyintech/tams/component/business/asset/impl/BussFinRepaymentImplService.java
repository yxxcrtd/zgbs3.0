/*
 * 文件名：BussFinRepaymentImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.asset.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.asset.BussFinRepaymentService;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.common.dto.AssetCommonInvRateAdjustDto;
import com.joyintech.tams.component.business.common.service.CommonRateAdjustService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.calculator.AssetInterestCal;
import com.joyintech.tams.invest.common.calculator.RepayScheduleCal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月14日
 * @see BussFinRepaymentImplService
 * @since 1.0
 */
@Service
public class BussFinRepaymentImplService extends BaseService<BussFinRepaymentDto> implements BussFinRepaymentService{

    @Autowired
    private DateCalService dateCalService;
    
   /* @Autowired
    private IPlatformDate iPlatformDate;*/
    
    @Autowired
    private CommonRateAdjustService commonRateAdjustService;
    
    /**
     * FinProductMapper
     */
    @Autowired
    private FinProductMapper finProductMapper;
	
    /**
     * 初始化还本计划
     * {@inheritDoc}
     */
    @Override
    public List<FinCashPlanDto> getFinCashPlanByProductId(String finprodId,String cashType,String finprodType,String finprodType2){
        return restTemplate.getForList(ServiceApplication.ASSET_SERVICE_NAME
            + "/BussFinRepayment/getFinCashPlanByProductId?finprodId="
            + finprodId+"&cashType="+cashType
            + "&finprodType="+finprodType
            +"&finprodType2="+finprodType2,
            FinCashPlanDto.class);
    }
    
    /**
     * 生成还本计划
     * {@inheritDoc}
     */
    public List<FinCashPlanDto> createAssetRePaymentPlan(BussFinRepaymentDto dto) throws ParseException {
        //还本计划(return)
        List<FinCashPlanDto> finCashPlanList = new ArrayList<FinCashPlanDto>();
        //付息计划
        List<FinCashPlanDto> scheList = new ArrayList<FinCashPlanDto>();
        //还本计划(cal)
        List<FinCashPlanDto> invRepayList = new ArrayList<FinCashPlanDto>();
        //资产公共DTO
        AssetCommonInfoDto scai = new AssetCommonInfoDto();
        BeanUtil.copyPropertiesIgnoreNull(scai, dto); //复制资产信息
        scai.setFinRateInfo(dto.getFinInterestFactorDto());
        //付息规则
        FinCashPayRule is = new FinCashPayRule();
        BeanUtil.copyPropertiesIgnoreNull(is, dto.getFinPayInterestStdInfoDto()); //复制付息规则
        //到期日+15天
        Date  mdate = DateUtil.parse(DateUtil.plusDays(DateUtil.format(scai.getMdate()), 15));
        //获取节假日
        List<String> holidayList = dateCalService.getDealCalendarHoliday(scai.getVdate(),mdate,scai.getCalendarId());
        //债券取初始利率、项目类资产取净投资收益率
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getRate(), new BigDecimal(100), 14));
            scai.setAmount(new BigDecimal(100));
        }
        else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
            if(StringUtil.equals(dto.getFinInterestFactorDto().getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getBuyOutRate(), new BigDecimal(100), 14));
            }
            else{
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getInvestRate(), new BigDecimal(100), 14));
            }
            scai.setAmount(dto.getIssueAmt());
            is.setPayCycle(dto.getPayCycle());//还本周期
        }
        else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
            if(StringUtil.equals(dto.getFinInterestFactorDto().getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getBuyOutRate(), new BigDecimal(100), 14));
            }
            else{
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getInvestRate(), new BigDecimal(100), 14));
            }
            scai.setAmount(dto.getIssueAmt());
            is.setPayCycle(dto.getPayCycle());//还本周期
        }
        scai.setIntType(dto.getFinInterestFactorDto().getIntType());
        if(StringUtil.isEmpty(is.getCalcRule())){
        	// 一体化对应etliu
//            is.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        	is.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
        }
        //计息基础
        scai.setBasis(dto.getFinInterestFactorDto().getBasis());
        if(StringUtil.isEmpty(dto.getFinInterestFactorDto().getBasisStyle())){
            scai.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
        }else{
            scai.setIntpayRule(dto.getFinInterestFactorDto().getBasisStyle()); 
        }
        // 平台日期
        //Date platDate = iPlatformDate.getSysDate();
        //生成付息区间
        //前台传入，已有还本、付息计划，重新计算利息
        if(Boolean.parseBoolean(dto.getIsAgainCalInt())){
            scheList = dto.getInvScheduleList();
            invRepayList = dto.getInvRepayList();
            //计算支付日
            for(FinCashPlanDto finCashPlanDto : scheList){
                if (!StringUtil.isNotEmpty(finCashPlanDto.getPayDate())) {
                    String payDate = AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finCashPlanDto.getMdate()), is.getPayTdayRule(), holidayList);
                    finCashPlanDto.setPayDate(DateUtil.parse(payDate));
                }
            }
            for(FinCashPlanDto finCashPlanDto2:invRepayList){
                if(dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){//是还本不带息的场景
                    finCashPlanDto2.setIsPayInt(DictCodeList.是否_否.getValue());//还本不带息
                }else {
                    finCashPlanDto2.setIsPayInt(DictCodeList.是否_是.getValue());//还本带息
                }
                if (!StringUtil.isNotEmpty(finCashPlanDto2.getPayDate())) {
                    String payDate = AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finCashPlanDto2.getMdate()), is.getPayTdayRule(), holidayList);
                    finCashPlanDto2.setPayDate(DateUtil.parse(payDate));
                }
                for(FinCashPlanDto finCashPlanDto3:scheList){
                    if(DateUtil.compareDate(finCashPlanDto2.getMdate(), finCashPlanDto3.getVdate())==1
                        &&DateUtil.compareDate(finCashPlanDto2.getMdate(), finCashPlanDto3.getMdate())==-1){
                        finCashPlanDto2.setVdate(finCashPlanDto3.getVdate());
                        finCashPlanDto2.setVdateTerm(finCashPlanDto3.getVdate());
                        finCashPlanDto2.setMdateTerm(finCashPlanDto2.getMdate());
                        break;
                    }else if(DateUtil.compareDate(finCashPlanDto2.getMdate(), finCashPlanDto3.getMdate())==0){
                        finCashPlanDto2.setVdate(finCashPlanDto2.getMdate());
                        finCashPlanDto2.setVdateTerm(finCashPlanDto2.getMdate());
                        finCashPlanDto2.setMdateTerm(finCashPlanDto2.getMdate());
                    }
                }
            }
        }else{
            scheList = AssetInterestCal.cal(is, scai, holidayList, false,FinCashPlanDto.class);
            invRepayList = createInvRepayList(dto,scai,is,holidayList,scheList);
        }
        // 获取资产报价信息并处理
        List<AssetCommonInvRateAdjustDto> rateList = commonRateAdjustService.escapRateAdjust(scai,is,invRepayList,holidayList);
        finCashPlanList = RepayScheduleCal.cal(scai, invRepayList, is, rateList, scai.getAmount(), FinCashPlanDto.class, FinCashPlanDetailDto.class);
        Collections.sort(finCashPlanList, new Comparator<FinCashPlanDto>() {
            @Override
            public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                return o1.getMdate().compareTo(o2.getMdate());
            }
        });
        return finCashPlanList;
    }
    
    /**
     * 生成还本区间
     * isAgainCalInt=false    默认生成:债券类资产构建一条到期还本计划，项目类资产按照还本周期生成还本区间
     * isAgainCalInt=ture     确认:前台传入还本计划
     * @return
     * @see
     * @since 1.0
     */
    
    private List<FinCashPlanDto> createInvRepayList(BussFinRepaymentDto dto,AssetCommonInfoDto scai,FinCashPayRule is,List<String> holidayList,List<FinCashPlanDto> scheList){
        List<FinCashPlanDto> invRepayList = new ArrayList<FinCashPlanDto>();
        //计息营业日规则
        String endTdayRule = dto.getFinPayInterestStdInfoDto().getEndTdayRule();
        //付息营业日规则
        String payTdayRule = dto.getFinPayInterestStdInfoDto().getPayTdayRule();
        if(Boolean.parseBoolean(dto.getIsAgainCalInt())){
            //支付日调整
              for(FinCashPlan entity:dto.getInvRepayList()){
                  FinCashPlanDto newFinCashPlan = new FinCashPlanDto();
                  newFinCashPlan.setVdateUnadjust(dto.getVdate());
                  newFinCashPlan.setMdateUnadjust(entity.getMdate());
                  newFinCashPlan.setVdate(dto.getMdate());
                  newFinCashPlan.setMdate(entity.getMdate());
                  newFinCashPlan.setPrinAmt(entity.getPrinAmt());
                  if(dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){//是还本不带息的场景
                      newFinCashPlan.setIsPayInt(DictCodeList.是否_否.getValue());//还本不带息
                  }
                  /*newFinCashPlan.setIntPrinAmt(new BigDecimal(100));*/
                  newFinCashPlan.setCashBaseamt(new BigDecimal(100));
                  if(!DateUtil.isSameDate(entity.getMdate(), dto.getMdate())){
                     String payDate =  AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(entity.getMdate()), payTdayRule, holidayList);
                     newFinCashPlan.setPayDate(DateUtil.parse(payDate));
                  }else{
                      newFinCashPlan.setPayDate(entity.getMdate());
                  }
                  invRepayList.add(newFinCashPlan);
              }
          }else{
              if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                  FinCashPlanDto newFinCashPlan = new FinCashPlanDto();
                  newFinCashPlan.setVdateUnadjust(dto.getVdate());
                  newFinCashPlan.setMdateUnadjust(dto.getMdate());
                  newFinCashPlan.setVdate(dto.getMdate());
                  newFinCashPlan.setMdate(dto.getMdate());
                  newFinCashPlan.setPrinAmt(scai.getAmount());
                  newFinCashPlan.setIntPrinAmt(new BigDecimal(100));
                  newFinCashPlan.setCashBaseamt(new BigDecimal(100));
                  String payDate =  AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(dto.getMdate()), payTdayRule, holidayList);
                  newFinCashPlan.setPayDate(DateUtil.parse(payDate));
                  invRepayList.add(newFinCashPlan); 
              }else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
              ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
                  if(StringUtil.isNotEmpty(endTdayRule)){
                      is.setEndTdayRule(endTdayRule);
                  }else{
                      is.setEndTdayRule(DictCodeList.营业日准则_不调整.getValue());
                  }
                  if(StringUtil.isNotEmpty(payTdayRule)){
                      is.setPayTdayRule(payTdayRule);
                  }else{
                      is.setPayTdayRule(DictCodeList.营业日准则_不调整.getValue());
                  }
                  
               // 一体化对应etliu
//                  is.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
                  is.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
                  
                  //invRepayList = AveInterestCal.cal(is, scai, holidayList, null, platDate, FinCashPlanDto.class);
                  invRepayList = AssetInterestCal.cal(is, scai, holidayList, false,FinCashPlanDto.class);
                  //还本计划计息截止日所在付息计划区间内,付息计划的开始日作为还本计划的计息开始日
                  // 总期数 = 还本计划列表的条数
                  if(null!=invRepayList&&!invRepayList.isEmpty()){
                      int termTotal = invRepayList.size();
                      // 计算本金 = 【入参】初始本金
                      BigDecimal amtcal = scai.getAmount();
                      // 当期本金 = 初始本金
                      BigDecimal amtnow = scai.getAmount();
                      BigDecimal aveAmt = ArithmeticUtil.divide(amtcal, termTotal, 2);
                      for(FinCashPlanDto finCashPlanDto:invRepayList){
                          if(dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){//是还本不带息的场景
                              finCashPlanDto.setIsPayInt(DictCodeList.是否_否.getValue());//还本不带息
                          }
                          termTotal--;
                          //还本金额
                          if(termTotal==0){
                              finCashPlanDto.setPrinAmt(amtnow);
                          }else{
                              amtnow = ArithmeticUtil.subtract(amtnow, aveAmt);
                              finCashPlanDto.setPrinAmt(aveAmt);
                          }
                          for(FinCashPlanDto finCashPlanDto2:scheList){
                              if(DateUtil.compareDate(finCashPlanDto.getMdate(), finCashPlanDto2.getVdate())==1
                                  &&DateUtil.compareDate(finCashPlanDto.getMdate(), finCashPlanDto2.getMdate())==-1){
                                  finCashPlanDto.setVdate(finCashPlanDto2.getVdate());
                                  break;
                              }else if(DateUtil.compareDate(finCashPlanDto.getMdate(), finCashPlanDto2.getMdate())==0){
                                  finCashPlanDto.setVdate(finCashPlanDto.getMdate());
                              }
                          }
                      }
                  }
              }
          }
        //等额本息
        //invRepayList = AveInterestScheduleCal.interestScheduleCal(scai, invRepayList, rateList, is, holidayList, scai.getAmount(), platDate, FinCashPlanDto.class);
        return invRepayList;
    }

    /**
     * 根据新的计息截止日获取还款日（支付日）
     *
     * @return 还款日（支付日）
     * @see
     * @since 1.0
     */
    @Override
    public String genPayDate(Map<String, Object> paramMap) {
        String mdate = (String) paramMap.get("mdate");
        String finprodId = (String) paramMap.get("finprodId");

        if (StringUtil.isBlank(mdate) || StringUtil.isBlank(finprodId)
            || !DateUtil.isDateStr(mdate)) {
            return null;
        }
        
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct == null) {
            return null;
        }
        
        Date vdate = finProduct.getVdate(); // 起息日
        String calendarId = finProduct.getCalendarId(); // 交易日历
        Date tempMdate = DateUtil.parse(mdate);

        // 付息营业日准则
        Map<String, Object> paramSelectMap = new HashMap<String, Object>();
        paramSelectMap.put("finprodId", finprodId);
        String payTdayRule = restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/BussFinRepayment/selectAssetPayTdayRule",paramSelectMap,
            String.class);
        
        //获取节假日
        List<String> holidayList = dateCalService.getDealCalendarHoliday(vdate, tempMdate, calendarId);
        // 计算支付日
        String payDate = AdjustedTradeDayCal.getAdjustedTradeDay(mdate, payTdayRule, holidayList);
        
        return payDate;
    }
}
