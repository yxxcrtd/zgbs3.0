package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.RiskSceneConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskSceneConfigMapper {
    int deleteByPrimaryKey(String seqNo);

    int insert(RiskSceneConfig record);

    int insertSelective(RiskSceneConfig record);

    int insertBasicByBasicInfo(RiskSceneConfig record);

    RiskSceneConfig selectByPrimaryKey(String seqNo);

    int updateByPrimaryKeySelective(RiskSceneConfig record);

    int updateByPrimaryKey(RiskSceneConfig record);
}