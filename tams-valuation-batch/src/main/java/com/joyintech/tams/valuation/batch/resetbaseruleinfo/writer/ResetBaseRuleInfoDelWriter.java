/*
 * 文件名：ResetBaseRuleInfoDelWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dao.ValResetBaseRuleInfoMapper;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DelWriter
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoDelWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetBaseRuleInfoDelWriter extends BaseWriter<NeedToWriteEntity> {

    @Autowired
    private ValResetBaseRuleInfoMapper valResetBaseRuleInfoMapper;
    
    /*@Autowired(required = false)
    private PinganBatchPlatformDateImpl platformDateService;*/
    
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)this.getParams().get(GlobalConstant.BATCH_INFO);
        if(null!=batchExecuteInfo){
            paramMap.put("effDate",DateUtil.format(batchExecuteInfo.getExecuteDate()));
            paramMap.put("intRateType", DictCodeListExt1.基准类型_2_业绩基准.getValue());
            valResetBaseRuleInfoMapper.deleteMstRateInfo(paramMap);
        }
    }
    
}
