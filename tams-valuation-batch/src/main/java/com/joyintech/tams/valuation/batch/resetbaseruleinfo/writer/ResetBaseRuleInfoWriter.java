/*
 * 文件名：ResetBaseRuleInfoWriter.java
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
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.MstIntRateInfoMapper;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.MstIntRateInfoDto;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  writer
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetBaseRuleInfoWriter extends BaseWriter<NeedToWriteEntity>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(ResetBaseRuleInfoWriter.class);

    @Autowired
    private MstIntRateInfoMapper mstIntRateInfoMapper;
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        @SuppressWarnings("unchecked")
        List<ResetBaseRuleInfoResult> resultList = (List<ResetBaseRuleInfoResult>)items;
        if (!StringUtil.isEmptyList(resultList)) {
            for (ResetBaseRuleInfoResult resetBaseRuleInfoResult : resultList) {
                MstIntRateInfoDto  mstIntRateInfoDto=  resetBaseRuleInfoResult.getMstIntRateInfoDto();
                if (mstIntRateInfoDto == null) {
                    continue;
                }
                try{
                    mstIntRateInfoMapper.insert(mstIntRateInfoDto);
                }catch(Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }

}
