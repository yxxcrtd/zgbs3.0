package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SettleTransDataMapper extends BaseBatchMapper<SettleTransData, String> {
    int deleteByPrimaryKey(String tradeId);

    int insert(SettleTransData record);

    int insertSelective(SettleTransData record);

    int insertBasicByBasicInfo(SettleTransData record);

    SettleTransData selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(SettleTransData record);

    int updateByPrimaryKey(SettleTransData record);
}