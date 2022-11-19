package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProductQuto;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface FinProductQutoMapper extends BaseBatchMapper<FinProductQuto, String> {
    int deleteByPrimaryKey(String seqNo);

    int insert(FinProductQuto record);

    int insertSelective(FinProductQuto record);

    int insertBasicByBasicInfo(FinProductQuto record);

    FinProductQuto selectByPrimaryKey(String seqNo);

    int updateByPrimaryKeySelective(FinProductQuto record);

    int updateByPrimaryKey(FinProductQuto record);
}