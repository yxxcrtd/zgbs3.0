/*
 * 文件名：ResetRateReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.batch.resetrate.dao.ValResetRateMapper;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateReaderResult;
import com.joyintech.tams.valuation.common.asset.dto.FinRateInfoDto;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
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
 * @version 1.0 2019年2月21日
 * @see ResetRateReaderService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class ResetRateReaderService extends BaseService{

    @Autowired
    private ValResetRateMapper valResetRateMapper;
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
    public List<ResetRateReaderResult> execute(Map<String, Object> param) {
     // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get(GlobalConstant.BATCH_INFO);
        if(null==batchExecuteInfo){
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("rateType02", DictCodeList.利率类型_浮动利率.getValue());
        paramMap.put("sysDate",DateUtil.format(batchExecuteInfo.getExecuteDate()));
        paramMap.put("productIdList",(List<String>)param.get("productIdList"));

        List<ResetRateReaderResult> resultList = new ArrayList<ResetRateReaderResult>();
        List<FinRateInfoDto> finRateInfoDtoList = valResetRateMapper.selectAllRateInfo(paramMap);
        if (!StringUtil.isEmptyList(finRateInfoDtoList)) {
            for (FinRateInfoDto finRateInfoDto : finRateInfoDtoList) {
                ResetRateReaderResult resultDto = new ResetRateReaderResult();
                resultDto.setFinRateInfoDto(finRateInfoDto);
                resultList.add(resultDto);
            }
        }
        return resultList;
    }
}
