/*
 * 文件名：TablePartitionReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.tablepartition.reader;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.cache.service.ValuationBatchCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.tams.common.base.entity.BokMatchRlat;
import com.joyintech.tams.valuation.batch.common.accountingmethod.dao.AccountingMethodMapper;
import com.joyintech.tams.valuation.batch.tablepartition.dto.TablePartitionDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 表分区reader
 * @author 王波
 * @version 1.0 2018年10月16日
 * @see TablePartitionReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class TablePartitionReader extends BaseReader<TablePartitionDto> {

    @Autowired
    private ValuationBatchCacheService valuationBatchCacheService;

    @Override
    public List<TablePartitionDto> createList() {
        List<TablePartitionDto> res = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        TablePartitionDto dto = new TablePartitionDto();
        
        List<String> matchTypeList = new ArrayList<>();
        matchTypeList.add(DictCodeListExt1.匹配类型_任务代码_表分区场景.getValue());
//        List<BokMatchRlat> valMatchRlats = accountingMethodMapper.selectValMatchRlatBySource(
//            matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);
        List<BokMatchRlat> valMatchRlats = valuationBatchCacheService.getBokMatchRlatFromCache(matchTypeList, this.getBatchExecuteInfo().getTaskId(), null);
        valMatchRlats.forEach(x->{
            tables.add(x.getTarget());
        });
        // 01 -- 按照账套ID分区
        dto.setPartitionType("01");
        dto.setTableName(tables);
        res.add(dto);
        return res;
    }

}
