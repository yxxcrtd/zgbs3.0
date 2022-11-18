package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetailKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdTradeFeeDetailMapper extends BaseBatchMapper<TrdTradeFeeDetail, TrdTradeFeeDetailKey> {
    int deleteByPrimaryKey(TrdTradeFeeDetailKey key);

    int insert(TrdTradeFeeDetail record);

    int insertSelective(TrdTradeFeeDetail record);

    int insertBasicByBasicInfo(TrdTradeFeeDetail record);

    TrdTradeFeeDetail selectByPrimaryKey(TrdTradeFeeDetailKey key);

    int updateByPrimaryKeySelective(TrdTradeFeeDetail record);

    int updateByPrimaryKey(TrdTradeFeeDetail record);
}