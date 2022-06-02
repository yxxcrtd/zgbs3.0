package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.RiskNodeConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskNodeConfigMapper {

    int deleteByPrimaryKey(@Param("nodeId") String nodeId);

    int insert(RiskNodeConfig record);

    int insertSelective(RiskNodeConfig record);

    RiskNodeConfig selectByPrimaryKey(@Param("nodeId") String nodeId);

    List<RiskNodeConfig> selectByRootId(@Param("nodeId") String nodeId);

    int updateByPrimaryKeySelective(RiskNodeConfig record);

    int updateByPrimaryKey(RiskNodeConfig record);
}