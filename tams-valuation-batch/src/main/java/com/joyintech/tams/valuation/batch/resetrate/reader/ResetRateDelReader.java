/*
 * 文件名：ResetRateDelReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateReaderResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除处理Reader
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateDelReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetRateDelReader extends BaseReader<ResetRateReaderResult>{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResetRateReaderResult> createList() {
        ResetRateReaderResult readerResult = new ResetRateReaderResult();
        List<ResetRateReaderResult> resultList = new ArrayList<ResetRateReaderResult>();
        resultList.add(readerResult);
        return resultList;
    }
}
