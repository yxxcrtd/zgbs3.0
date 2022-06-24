/*
 * 文件名：SubjectBalanceCollectWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.dao.SubjectBalanceAgainCollectMapper;
import com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.dto.BokBalanceAgainCollectLogDto;
import com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.dto.BooksetTodoDto;
import com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.dto.SubjectBalanceBatch;
import com.joyintech.tams.valuation.batch.subjectbalanceagaincollect.dto.SubjectBalanceGenerateResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author lby
 * @version 1.0 2020年12月29日
 * @see SubjectBalanceAgainCollectWriter
 * @since 1.0
 */
@Service
public class SubjectBalanceAgainCollectWriter extends BaseWriter<NeedToWriteEntity>{
    
	private static final Logger logger = LoggerFactory.getLogger(SubjectBalanceAgainCollectWriter.class);
	
    /**
     * 科目余额汇总mapper
     */
    @Autowired
    private SubjectBalanceAgainCollectMapper subjectBalanceAgainCollectMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        //获取process返回组合的list值 - 记账凭证list
        List<SubjectBalanceGenerateResult> resultList = (List<SubjectBalanceGenerateResult>) items;
        List<SubjectBalanceBatch> listBatches = new ArrayList<SubjectBalanceBatch>();
        BokBalanceAgainCollectLogDto logDto = new BokBalanceAgainCollectLogDto();
        //循环科目余额list，对每个记账凭证进行操作
        for(SubjectBalanceGenerateResult result : resultList){
            //入库科目余额重汇数据
            if(CollectionUtils.isNotEmpty(result.getSubjectBalanceBatchs())){
            	listBatches.addAll(result.getSubjectBalanceBatchs());
            }
            //科目余额重汇信息记录DTO
            if (StringUtil.isNotEmpty(result.getBokBalanceAgainCollectLogDto())){
                logDto = result.getBokBalanceAgainCollectLogDto();
            }
        }
        List<List<SubjectBalanceBatch>> list = DataHandleUtil.splitList(listBatches);
        for (List<SubjectBalanceBatch> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                //科目余额			 入库
                subjectBalanceAgainCollectMapper.insertBatch(listInsert);
            	logger.debug("科目重汇余额批量插入成功");
            }
        }
        if (StringUtil.isNotEmpty(logDto)){
            Map<String,Object> map = new HashMap<>();
            map.put("booksetId",logDto.getBooksetId());
            //科目余额重汇信息记录表	 先删在入库
            subjectBalanceAgainCollectMapper.deleteLog(map);
            logger.debug("账套代码:"+logDto.getBooksetId()+"科目余额重汇信息记录删除成功");
            subjectBalanceAgainCollectMapper.insertLog(logDto);
            logger.debug("账套代码:"+logDto.getBooksetId()+"科目余额重汇信息记录插入成功");
        }
    }

}
