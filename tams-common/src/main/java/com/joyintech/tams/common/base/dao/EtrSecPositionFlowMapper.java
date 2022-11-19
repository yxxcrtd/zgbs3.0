package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.EtrSecPositionFlow;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface EtrSecPositionFlowMapper extends BaseBatchMapper<EtrSecPositionFlow, String> {
    int deleteByPrimaryKey(String seqno);

    int insert(EtrSecPositionFlow record);

    int insertSelective(EtrSecPositionFlow record);

    int insertBasicByBasicInfo(EtrSecPositionFlow record);

    EtrSecPositionFlow selectByPrimaryKey(String seqno);

    int updateByPrimaryKeySelective(EtrSecPositionFlow record);

    int updateByPrimaryKey(EtrSecPositionFlow record);
}