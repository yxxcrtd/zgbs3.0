/*
 * 文件名：BookAccountGenerateDelWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年6月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.common.base.entity.BokFinprodSchemeInfo;
import com.joyintech.tams.valuation.batch.valschemelanding.dao.ValSchemeLandingMapper;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingProceesorDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class ValSchemeLandingWriter extends BaseWriter<NeedToWriteEntity> {

    @Autowired
    private ValSchemeLandingMapper valSchemeLandingMapper;

    private Integer maxSqlNum = 500;

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items)
        throws Exception {
        // 获取process返回组合的list值 - 记账凭证list
        List<ValSchemeLandingProceesorDto> resultList = (List<ValSchemeLandingProceesorDto>)items;
        if (CollectionUtils.isNotEmpty(resultList)) {
            // 定义一个固定大小插入存放集合
            List<BokFinprodSchemeInfo> listInsert = new ArrayList<BokFinprodSchemeInfo>();
            for (ValSchemeLandingProceesorDto e : resultList) {
                if(e.getBooksetId() == null){
                    continue;
                }
                BokFinprodSchemeInfo schemeInfo = new BokFinprodSchemeInfo();
                BeanUtil.copyPropertiesIgnoreNull(schemeInfo, e);
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(schemeInfo);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valSchemeLandingMapper.deleteBatch(listInsert);
                    valSchemeLandingMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(schemeInfo);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valSchemeLandingMapper.deleteBatch(listInsert);
                valSchemeLandingMapper.insertBatch(listInsert);
            }
        }
    }

}
