/*
 * 文件名：ResetBaseRuleInfoReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dao.ValResetBaseRuleInfoMapper;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoDto;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoReaderResult;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * reader相关业务处理
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoReaderService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class ResetBaseRuleInfoReaderService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(ResetBaseRuleInfoReaderService.class);
    @Autowired
    private ValResetBaseRuleInfoMapper valResetBaseRuleInfoMapper;
    
    /*@Autowired(required = false)
    private PinganBatchPlatformDateImpl platformDateService;*/
    /**
     * 获取待处理数据
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<ResetBaseRuleInfoReaderResult> execute(Map<String, Object> param) {
        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get(GlobalConstant.BATCH_INFO);
        if(null==batchExecuteInfo){
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        paramMap.put("openCycle02", DictCodeList.开放模式_高频开放.getValue());
        paramMap.put("rateType01", DictCodeList.利率类型_固定利率.getValue());
        paramMap.put("rateType02", DictCodeList.利率类型_浮动利率.getValue());
        // 2019年9月20日 并且 业绩基准信息.业绩基准类型 = ‘浮动管理费’ by yangxiaofeng start
        paramMap.put("rateType03", DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
        // 2019年9月20日 并且 业绩基准信息.业绩基准类型 = ‘浮动管理费’ by yangxiaofeng end

        /**增加业绩基准类型_业绩回补  add by chendijun 20191219**/
        paramMap.put("BASE_RULE_TYPE_03", DictCodeListExt1.业绩基准类型_业绩回补.getValue());
        paramMap.put("sysDate", DateUtil.format(batchExecuteInfo.getExecuteDate()));
        paramMap.put("cashTypeC01", DictCodeListExt1.现金流二级类型_本金.getValue());
        paramMap.put("cashTypeC04", DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        paramMap.put("resetType01", DictCodeListExt1.重置规则_期初.getValue());
        paramMap.put("resetType02", DictCodeListExt1.重置规则_期末.getValue());
        paramMap.put("resetType03", DictCodeListExt1.重置规则_当期生效.getValue());
        paramMap.put("benchMarkType05", DictCodeListExt1.基准类型_2_复合.getValue());
        List<ResetBaseRuleInfoReaderResult> resultList = new ArrayList<ResetBaseRuleInfoReaderResult>();
        List<ResetBaseRuleInfoDto> resetBaseRuleInfoDtoList = valResetBaseRuleInfoMapper.selectAllBaseRuleInfo(paramMap);
        if (!StringUtil.isEmptyList(resetBaseRuleInfoDtoList)) {
            for (ResetBaseRuleInfoDto resetBaseRuleInfoDto : resetBaseRuleInfoDtoList) {
//                System.out.println(resetBaseRuleInfoDto.getBaseRuleId());
                logger.info(resetBaseRuleInfoDto.getBaseRuleId());
                ResetBaseRuleInfoReaderResult resultDto = new ResetBaseRuleInfoReaderResult();
                resultDto.setResetBaseRuleInfoDto(resetBaseRuleInfoDto);
                resultList.add(resultDto);
            }
        }
        return resultList;
    }
    
}
