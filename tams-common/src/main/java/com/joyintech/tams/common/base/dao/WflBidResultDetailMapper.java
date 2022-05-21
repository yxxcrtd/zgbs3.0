package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflBidResultDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface WflBidResultDetailMapper {
    int deleteByPrimaryKey(String detailWinId);

    int insert(WflBidResultDetail record);

    int insertSelective(WflBidResultDetail record);

    int insertBasicByBasicInfo(WflBidResultDetail record);

    WflBidResultDetail selectByPrimaryKey(String detailWinId);

    int updateByPrimaryKeySelective(WflBidResultDetail record);

    int updateByPrimaryKey(WflBidResultDetail record);

    int deleteByWorkflowId(String workflowId);

    int deleteByTotalWinId(String totalWinId);
}