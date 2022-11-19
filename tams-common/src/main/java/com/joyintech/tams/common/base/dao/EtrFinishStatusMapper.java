package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrFinishStatusKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface EtrFinishStatusMapper extends BaseBatchMapper<EtrFinishStatus, EtrFinishStatusKey> {
    int deleteByPrimaryKey(EtrFinishStatusKey key);

    int insert(EtrFinishStatus record);

    int insertSelective(EtrFinishStatus record);

    int insertBasicByBasicInfo(EtrFinishStatus record);

    EtrFinishStatus selectByPrimaryKey(EtrFinishStatusKey key);

    int updateByPrimaryKeySelective(EtrFinishStatus record);

    int updateByPrimaryKey(EtrFinishStatus record);
}