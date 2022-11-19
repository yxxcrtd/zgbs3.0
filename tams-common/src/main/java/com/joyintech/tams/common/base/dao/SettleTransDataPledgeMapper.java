package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.SettleTransDataPledge;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SettleTransDataPledgeMapper extends BaseBatchMapper<SettleTransDataPledge, String> {
    int deleteByPrimaryKey(String tradeId);

    int insert(SettleTransDataPledge record);

    int insertSelective(SettleTransDataPledge record);

    int insertBasicByBasicInfo(SettleTransDataPledge record);

    SettleTransDataPledge selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(SettleTransDataPledge record);

    int updateByPrimaryKey(SettleTransDataPledge record);
}