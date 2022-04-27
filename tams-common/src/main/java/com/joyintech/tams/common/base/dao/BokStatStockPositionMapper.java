package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.BokStatStockPosition;
import com.joyintech.tams.common.base.entity.BokStatStockPositionKey;
import org.springframework.stereotype.Repository;

@Repository
public interface BokStatStockPositionMapper {
    int deleteByPrimaryKey(BokStatStockPositionKey key);

    int insert(BokStatStockPosition record);

    int insertSelective(BokStatStockPosition record);

    int insertBasicByBasicInfo(BokStatStockPosition record);

    BokStatStockPosition selectByPrimaryKey(BokStatStockPositionKey key);

    int updateByPrimaryKeySelective(BokStatStockPosition record);

    int updateByPrimaryKey(BokStatStockPosition record);
}