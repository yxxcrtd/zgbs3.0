package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflBidResultTotal;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WflBidResultTotalMapper {
    int deleteByPrimaryKey(String totalWinId);

    int insert(WflBidResultTotal record);

    int insertSelective(WflBidResultTotal record);

    int insertBasicByBasicInfo(WflBidResultTotal record);

    WflBidResultTotal selectByPrimaryKey(String totalWinId);

    int updateByPrimaryKeySelective(WflBidResultTotal record);

    int updateByPrimaryKey(WflBidResultTotal record);

    void deleteByWorkflowId(String workflowId);
}