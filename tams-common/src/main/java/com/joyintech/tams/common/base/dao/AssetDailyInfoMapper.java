package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.AssetDailyInfo;
import com.joyintech.tams.common.base.entity.AssetDailyInfoKey;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetDailyInfoMapper {
    int deleteByPrimaryKey(AssetDailyInfoKey key);

    int insert(AssetDailyInfo record);

    int insertSelective(AssetDailyInfo record);

    int insertBasicByBasicInfo(AssetDailyInfo record);

    AssetDailyInfo selectByPrimaryKey(AssetDailyInfoKey key);

    int updateByPrimaryKeySelective(AssetDailyInfo record);

    int updateByPrimaryKey(AssetDailyInfo record);
}