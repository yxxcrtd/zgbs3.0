package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.SettleTransTgMapping;
import com.joyintech.tams.common.base.entity.SettleTransTgMappingKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SettleTransTgMappingMapper extends BaseBatchMapper<SettleTransTgMapping, SettleTransTgMappingKey> {
    int deleteByPrimaryKey(SettleTransTgMappingKey key);

    int insert(SettleTransTgMapping record);

    int insertSelective(SettleTransTgMapping record);

    int insertBasicByBasicInfo(SettleTransTgMapping record);

    SettleTransTgMapping selectByPrimaryKey(SettleTransTgMappingKey key);

    int updateByPrimaryKeySelective(SettleTransTgMapping record);

    int updateByPrimaryKey(SettleTransTgMapping record);
}