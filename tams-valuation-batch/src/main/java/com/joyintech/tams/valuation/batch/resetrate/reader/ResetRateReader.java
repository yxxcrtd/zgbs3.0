/*
 * 文件名：ResetRateReader.java
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
import com.joyintech.tams.valuation.batch.resetrate.service.ResetRateReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 读入Reader
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetRateReader extends BaseReader<ResetRateReaderResult>{

    @Autowired
    private ResetRateReaderService readerService;
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResetRateReaderResult> createList() {
        return readerService.execute(this.getParams());
    }
    
}
