/*
 * 文件名：DeltRightCurCalAmortReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.batch.valschemelanding.dao.ValSchemeLandingMapper;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingReaderDto;
import org.apache.commons.collections.CollectionUtils;
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
 * 估值方案落地批处理-reader
 * 
 * @author weipeng
 * @version 1.0 2022年03月25日
 * @see ValSchemeLandingReader
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValSchemeLandingReader extends BaseReader<ValSchemeLandingReaderDto> {

    @Autowired
    private ValSchemeLandingMapper valSchemeLandingMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValSchemeLandingReaderDto> createList() {
        // 变量定义
        Map<String, Object> params = this.getParams();
        if (this.getParams() != null) {
            this.getParams().putAll(params);
        }
        // 重分类交易方向
        params.put("reclsPs", DictCodeList.交易方向_资产重分类.getValue());
        params.put("booksetType", DictCodeList.账套类别_理财账套.getValue());
        Map<String, ValSchemeLandingReaderDto> resultMap = new HashMap<>();
        List<ValSchemeLandingReaderDto> result = new ArrayList<>();
        // 查询当日重分类交易
        List<ValSchemeLandingReaderDto> reclsDeal = valSchemeLandingMapper.selectReclsDeal(params);
        if(CollectionUtils.isNotEmpty(reclsDeal)){
            for(ValSchemeLandingReaderDto item : reclsDeal){
                // 主键为账套代码 + 金融产品代码
                if(resultMap.get(item.getBooksetId()+item.getFinprodId()) == null){
                    // 只有map中不存在的数据，存入map中
                    resultMap.put(item.getBooksetId() + item.getFinprodId(), item);
                    // 更新返回结果
                    result.add(item);
                }
            }
        }
        // 查询持仓资产信息
        List<ValSchemeLandingReaderDto> assetList = valSchemeLandingMapper.selectValSchemeTaskTodo(params);
        if(CollectionUtils.isNotEmpty(assetList)){
            for(ValSchemeLandingReaderDto item : assetList){
                // 主键为账套代码 + 金融产品代码,若重分类已存在，以重分类为准，且不考虑一个资产多个投资目的共存情况
                if(resultMap.get(item.getBooksetId()+item.getFinprodId()) == null){
                    // 只有map中不存在的数据，存入map中
                    resultMap.put(item.getBooksetId() + item.getFinprodId(), item);
                    // 更新返回结果
                    result.add(item);
                }
            }
        }
        // 返回处理结果
        return result;
    }

}
