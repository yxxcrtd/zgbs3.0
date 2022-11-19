package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.FinTradeProductAddKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface FinTradeProductAddMapper extends BaseBatchMapper<FinTradeProductAdd, FinTradeProductAddKey> {
    int deleteByPrimaryKey(FinTradeProductAddKey key);

    int insert(FinTradeProductAdd record);

    int insertSelective(FinTradeProductAdd record);

    int insertBasicByBasicInfo(FinTradeProductAdd record);

    FinTradeProductAdd selectByPrimaryKey(FinTradeProductAddKey key);

    int updateByPrimaryKeySelective(FinTradeProductAdd record);

    int updateByPrimaryKey(FinTradeProductAdd record);
}