package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.EtrPledgeKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EtrPledgeMapper {
    int deleteByPrimaryKey(EtrPledgeKey key);

    int insert(EtrPledge record);

    int insertSelective(EtrPledge record);

    int insertBasicByBasicInfo(EtrPledge record);

    EtrPledge selectByPrimaryKey(EtrPledgeKey key);

    int updateByPrimaryKeySelective(EtrPledge record);

    int updateByPrimaryKey(EtrPledge record);

    int deleteByEntrustId(@Param("entrustId") String entrustId);
}