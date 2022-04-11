package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InterfCfetsNewbondIssue;
import org.springframework.stereotype.Repository;


@Repository
public interface InterfCfetsNewbondIssueMapper {
    int deleteByPrimaryKey(String marketId);

    int insert(InterfCfetsNewbondIssue record);

    int insertSelective(InterfCfetsNewbondIssue record);

    InterfCfetsNewbondIssue selectByPrimaryKey(String marketId);

    int updateByPrimaryKeySelective(InterfCfetsNewbondIssue record);

    int updateByPrimaryKey(InterfCfetsNewbondIssue record);
}