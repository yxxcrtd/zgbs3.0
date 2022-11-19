package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.SettleTransDataAdd;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SettleTransDataAddMapper extends BaseBatchMapper<SettleTransDataAdd, String> {
    int deleteByPrimaryKey(String tradeId);

    int insert(SettleTransDataAdd record);

    int insertSelective(SettleTransDataAdd record);

    int insertBasicByBasicInfo(SettleTransDataAdd record);

    SettleTransDataAdd selectByPrimaryKey(String tradeId);

    int updateByPrimaryKeySelective(SettleTransDataAdd record);

    int updateByPrimaryKey(SettleTransDataAdd record);
}