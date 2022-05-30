package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.BidResultDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface BidResultDetailMapper {
    int deleteByPrimaryKey(String totalWinId);

    int insert(BidResultDetail record);

    int insertSelective(BidResultDetail record);

    int insertBasicByBasicInfo(BidResultDetail record);

    BidResultDetail selectByPrimaryKey(String totalWinId);

    int updateByPrimaryKeySelective(BidResultDetail record);

    int updateByPrimaryKey(BidResultDetail record);
}