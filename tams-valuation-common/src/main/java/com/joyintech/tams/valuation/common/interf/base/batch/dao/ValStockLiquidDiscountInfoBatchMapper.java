/*
 * 文件名：ValBokBookdetailCurrentBatchMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年7月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.batch.dao;

import com.joyintech.tams.common.base.entity.MstSharePrice;
import com.joyintech.tams.common.base.entity.MstStockLiquidDiscountInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValStockLiquidDiscountInfoBatchMapper {

    int insertBatch(List<MstStockLiquidDiscountInfo> list);
    int deleteBatch(List<MstStockLiquidDiscountInfo> list);

}
