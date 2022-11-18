package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.PtlTradeKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface PtlTradeMapper extends BaseBatchMapper<PtlTrade, PtlTradeKey> {
    int deleteByPrimaryKey(PtlTradeKey key);

    int insert(PtlTrade record);

    int insertSelective(PtlTrade record);

    int insertBasicByBasicInfo(PtlTrade record);

    PtlTrade selectByPrimaryKey(PtlTradeKey key);

    int updateByPrimaryKeySelective(PtlTrade record);

    int updateByPrimaryKey(PtlTrade record);
}