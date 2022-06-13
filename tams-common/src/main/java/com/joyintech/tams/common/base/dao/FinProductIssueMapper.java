package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProductIssue;
import org.springframework.stereotype.Repository;

@Repository
public interface FinProductIssueMapper {
    int deleteByPrimaryKey(String finprodId);

    int insert(FinProductIssue record);

    int insertSelective(FinProductIssue record);

    FinProductIssue selectByPrimaryKey(String finprodId);

    int updateByPrimaryKeySelective(FinProductIssue record);

    int updateByPrimaryKey(FinProductIssue record);
}