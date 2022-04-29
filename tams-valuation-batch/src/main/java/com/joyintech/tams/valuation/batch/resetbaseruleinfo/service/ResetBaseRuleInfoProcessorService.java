/*
 * 文件名：ResetBaseRuleInfoProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dao.ValResetBaseRuleInfoMapper;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.*;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * processor相关业务处理
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoProcessorService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class ResetBaseRuleInfoProcessorService extends BaseService{

    /*@Autowired(required = false)
    private PinganBatchPlatformDateImpl platformDateService;*/
    
    @Autowired
    private ValResetBaseRuleInfoMapper valResetBaseRuleInfoMapper;
    
    /**
     * process 数据处理
     * <功能详细描述>
     * @param param1
     * @return
     * @see
     * @since 1.0
     */
    public ResetBaseRuleInfoResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        ResetBaseRuleInfoReaderResult readerResult = (ResetBaseRuleInfoReaderResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (readerResult == null) {
            return null;
        }
      // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get(GlobalConstant.BATCH_INFO);
        
        if(batchExecuteInfo == null){
            return null;
        }
        // 待处理业绩基准数据参数
        ResetBaseRuleInfoDto resetBaseRuleInfoDto = readerResult.getResetBaseRuleInfoDto();
        BigDecimal intRate = BigDecimal.ZERO;
        MstIntRateInfoDto mstIntRateInfoDto = new MstIntRateInfoDto();
        mstIntRateInfoDto.setIntRateId(resetBaseRuleInfoDto.getBaseRuleId());
        mstIntRateInfoDto.setEffDate(batchExecuteInfo.getExecuteDate());
        mstIntRateInfoDto.setRelaeseDate(batchExecuteInfo.getExecuteDate());
        if(null==resetBaseRuleInfoDto.getCoefficient()){
            resetBaseRuleInfoDto.setCoefficient(new BigDecimal(1));
        }
        if(null==resetBaseRuleInfoDto.getSpreadRate()){
            resetBaseRuleInfoDto.setSpreadRate(BigDecimal.ZERO);
        }
        if(StringUtil.equals(resetBaseRuleInfoDto.getIndexSource(), DictCodeListExt1.基准类型_2_复合.getValue())){
            intRate = handleRate(resetBaseRuleInfoDto,batchExecuteInfo);
            //复合利率  复合利率 * NVL(业绩基准信息.系数,1) + NVL(业绩基准信息.利差,0)
            intRate = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(intRate,resetBaseRuleInfoDto.getCoefficient()),resetBaseRuleInfoDto.getSpreadRate());
        }else{
            if(StringUtil.equals(resetBaseRuleInfoDto.getRateType(), DictCodeList.利率类型_固定利率.getValue())){
                intRate = resetBaseRuleInfoDto.getRate();
            }else{
                intRate = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(resetBaseRuleInfoDto.getIntRate(),resetBaseRuleInfoDto.getCoefficient()),resetBaseRuleInfoDto.getSpreadRate());
            }
        }
        mstIntRateInfoDto.setIntRate(intRate);
        mstIntRateInfoDto.setInputType(DictCodeListExt1.录入方式_批处理生成.getValue());
        mstIntRateInfoDto.setIntRateType(DictCodeListExt1.基准类型_2_业绩基准.getValue());
        ResetBaseRuleInfoResult resultDto = new ResetBaseRuleInfoResult(); 
        resultDto.setMstIntRateInfoDto(mstIntRateInfoDto);
        resultDto.setProdId("业绩基准重置处理");
        return resultDto;
    }
    
    /**
     * 处理复合利率
     * <功能详细描述>
     * @param resetBaseRuleInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private BigDecimal handleRate(ResetBaseRuleInfoDto resetBaseRuleInfoDto,SysBatchExecInfo batchExecuteInfo){
        BigDecimal intRate = BigDecimal.ZERO;BigDecimal R = BigDecimal.ZERO;
          //查询基准成分
            if(StringUtil.isNotEmpty(resetBaseRuleInfoDto.getIndexId())){
                Map<String,Object> mstIndexComponentParam = new HashMap<String,Object>();
                mstIndexComponentParam.put("indexId", resetBaseRuleInfoDto.getIndexId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                List<MstIndexComponentDto> mstIndexComponentList= valResetBaseRuleInfoMapper.selectMstIndexComponentInfo(mstIndexComponentParam);
                if(null!=mstIndexComponentList&&!mstIndexComponentList.isEmpty()){
                    for(MstIndexComponentDto mstIndexComponentDto : mstIndexComponentList){
                        if(StringUtil.equals(mstIndexComponentDto.getComponentType(), DictCodeListExt1.基准类型_2_利率.getValue())
                            ||StringUtil.equals(mstIndexComponentDto.getComponentType(), DictCodeListExt1.基准类型_2_收益率曲线.getValue())){
                            Map<String,Object> mstIntRateInfoParam = new HashMap<String,Object>();
                            mstIntRateInfoParam.put("intRateId", mstIndexComponentDto.getComponentId());
                            mstIntRateInfoParam.put("sysDate", sdf.format(batchExecuteInfo.getExecuteDate()));
                            MstIntRateInfoDto mstIntRateInfoDto = valResetBaseRuleInfoMapper.getMstIntRateInfo(mstIntRateInfoParam);
                            if(null!=mstIntRateInfoDto){
                                //最新利率
                                R =  mstIntRateInfoDto.getIntRate();
                            }
                        }else if(StringUtil.equals(mstIndexComponentDto.getComponentType(), DictCodeListExt1.基准类型_2_指数.getValue())){
                            //查询指数行情表，根据基准成分信息.行情代码以及平台日期,R = 获取的对应的最新利率（指数行情.收盘价）
                            Map<String,Object> getClosePriceParam = new HashMap<String,Object>();
                            getClosePriceParam.put("componentId", mstIndexComponentDto.getComponentId());
                            getClosePriceParam.put("effDate", sdf.format(batchExecuteInfo.getExecuteDate()));
                            R = valResetBaseRuleInfoMapper.getClostPrice(getClosePriceParam);
                            R = StringUtil.isNotEmpty(R)?R:BigDecimal.ZERO;
//                            BigDecimal X1 = BigDecimal.ZERO;BigDecimal X2 = BigDecimal.ZERO;
//                            Map<String,Object> getdateParam = new HashMap<String,Object>();
//                            getdateParam.put("bastRuleId", resetBaseRuleInfoDto.getBaseRuleId());
//                            getdateParam.put("sysDate", sdf.format(batchExecuteInfo.getExecuteDate()));
//                            getdateParam.put("effDate",sdf.format(resetBaseRuleInfoDto.getEffDate()));
//                            Date D = resetBaseRuleInfoMapper.getMaxOpenVdate(getdateParam);
//                            if(null == D){
//                                D = resetBaseRuleInfoMapper.getVdate(getdateParam);
//                            }
//                            Map<String,Object> getClosePriceParam = new HashMap<String,Object>();
//                            getClosePriceParam.put("componentId", mstIndexComponentDto.getComponentId());
//                            getClosePriceParam.put("effDate", sdf.format(batchExecuteInfo.getExecuteDate()));
//                            X1 = resetBaseRuleInfoMapper.getClostPrice(getClosePriceParam);
//                            getClosePriceParam.put("effDate", sdf.format(D));
//                            X2 = resetBaseRuleInfoMapper.getPClostPrice(getClosePriceParam);
//                            //计算利率 R = (X1 - X2)*365/X2/(平台日期 - D)
//                            if(null!=X2&&null!=X1&&null!=D){
//                                if(X1.compareTo(X2)==0){
//                                    R = BigDecimal.ZERO;
//                                }else{
//                                    long termdays  = DateUtil.getBetweenDayNumber(DateUtil.format(D),DateUtil.format(batchExecuteInfo.getExecuteDate()));
//                                    BigDecimal termClosrPrice = ArithmeticUtil.doSub(X1,X2);
//                                    R = ArithmeticUtil.divide(ArithmeticUtil.divide(ArithmeticUtil.multiply(termClosrPrice,new BigDecimal(365)),X2),new BigDecimal(termdays));
//                                }
//                            }
                        }
                        intRate =ArithmeticUtil.doAdd(intRate,ArithmeticUtil.multiply(R,mstIndexComponentDto.getWeight()));
                    }
                }
            }
        return intRate;
    }
}
