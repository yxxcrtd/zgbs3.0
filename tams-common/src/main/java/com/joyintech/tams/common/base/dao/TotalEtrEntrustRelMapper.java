package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TotalEtrEntrustRel;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalEtrEntrustRelMapper {
    int deleteByPrimaryKey(String totleId);

    int insert(TotalEtrEntrustRel record);

    int insertSelective(TotalEtrEntrustRel record);

    TotalEtrEntrustRel selectByPrimaryKey(String totleId);

    int updateByPrimaryKeySelective(TotalEtrEntrustRel record);

    int updateByPrimaryKey(TotalEtrEntrustRel record);
}