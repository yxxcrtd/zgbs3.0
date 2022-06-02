package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.RiskNodeConfigDtl;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskNodeConfigDtlMapper {
    int deleteByPrimaryKey(String nodeId);

    int insert(RiskNodeConfigDtl record);

    int insertSelective(RiskNodeConfigDtl record);

    RiskNodeConfigDtl selectByPrimaryKey(String nodeId);

    int updateByPrimaryKeySelective(RiskNodeConfigDtl record);

    int updateByPrimaryKey(RiskNodeConfigDtl record);
}