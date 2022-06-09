/*
 * 文件名：SecPosAutoGenerationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autogenerationrule.commonservice;

import com.joyintech.background.component.common.pack.common.util.IDGenerator;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.busicommon.productRating.dto.CommonProductRatingDto;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.AssetTargetDbMapper;
import com.joyintech.tams.common.base.entity.AssetTargetDb;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dao.SecPosAutoGenerationMapper;
import com.joyintech.tams.invest.batch.autogeneration.secpos.dto.AssetTargetDbDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CommonRuleGenerationService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void autoGeneration(Date batchDate, List<String> productIdList,String batchType) {
        String batchSeqno = IDGenerator.generate();
        //发送请求
        sendBatchRequest(batchSeqno,batchType,DateUtil.format(batchDate));
        boolean flag = true;
        //获取结果
        String maxTimeString = CacheUtil.getSysParam("batch_position_max_time");
        if(StringUtil.isEmpty(maxTimeString)){
            maxTimeString = "600000";//十分钟
        }
        String sleepTimeString = CacheUtil.getSysParam("batch_position_sleep_time");
        if(StringUtil.isEmpty(sleepTimeString)){
            sleepTimeString = "1000";//一秒钟
        }
        Long maxTime =Long.valueOf(maxTimeString);
        Long sleepTime =Long.valueOf(sleepTimeString);
        Long startTime = System.currentTimeMillis();
        while (true){
            Map<String,Object> res = getBatchLog(batchSeqno);
            if(StringUtil.isNotEmpty(res.get("data"))){
                Map<String,Object> batchLog =(Map<String, Object>) res.get("data");
                if(StringUtil.isNotEmpty(batchLog.get("batchStatus"))){
                    String status = batchLog.get("batchStatus").toString();
                    if(status.equals(DictCodeList.批处理执行状态_执行中.getValue())){
                        Long now = System.currentTimeMillis();
                        Long l = now - startTime;
                        if(l>maxTime){
                            throw new RuntimeException("批处理超时");
                        }
                        //运行中
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage());
                            // Sonar Fix modify by huwei 220609
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                    String logMsg ="";
                    if(StringUtil.isNotEmpty(batchLog.get("logMsg"))){
                        logMsg = batchLog.get("logMsg").toString();
                    }
                    if(status.equals(DictCodeList.批处理执行状态_异常结束.getValue())){
                        if(StringUtil.isEmpty(logMsg)){
                            logMsg = "批处理执行异常";
                        }
                        throw new RuntimeException(logMsg);
                    }
                    if(status.equals(DictCodeList.批处理执行状态_正常结束.getValue())){
                        logger.info(logMsg);
                        break;
                    }
                }
            }
            throw new RuntimeException("返回结果在预期之外:"+res.toString());
        }
        logger.info("批处理正常结束");
    }

    private void sendBatchRequest(String batchSeqno,String batchType,String batchDate) {
        Map<String,Object> req = new HashMap<>();
        req.put("batchSeqno",batchSeqno);
        req.put("batchTypeList",Arrays.asList(batchType));
        req.put("batchDate",batchDate);
        req.put("user","system");
        // 发送请求
        PositionHttpRequest.sendBatch(req);
    }

    private Map<String,Object> getBatchLog(String batchSeqno) {
        Map<String,Object> req = new HashMap<>();
        req.put("batchSeqno",batchSeqno);
        // 获取结果
        Map<String,Object> res =PositionHttpRequest.getBatchLog(req);
        return res;
    }

}
