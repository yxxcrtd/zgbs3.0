package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.MstStockLiquidDiscountInfo;
import com.joyintech.tams.common.base.entity.MstStockLiquidDiscountInfoKey;
import org.springframework.stereotype.Repository;

@Repository
public interface MstStockLiquidDiscountInfoMapper {
    int deleteByPrimaryKey(MstStockLiquidDiscountInfoKey key);

    int insert(MstStockLiquidDiscountInfo record);

    int insertSelective(MstStockLiquidDiscountInfo record);

    int insertBasicByBasicInfo(MstStockLiquidDiscountInfo record);

    MstStockLiquidDiscountInfo selectByPrimaryKey(MstStockLiquidDiscountInfoKey key);

    int updateByPrimaryKeySelective(MstStockLiquidDiscountInfo record);

    int updateByPrimaryKey(MstStockLiquidDiscountInfo record);
}