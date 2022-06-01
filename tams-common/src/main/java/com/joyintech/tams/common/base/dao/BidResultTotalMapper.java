package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.BidResultTotal;
import org.springframework.stereotype.Repository;

@Repository
public interface BidResultTotalMapper {
    int deleteByPrimaryKey(String totalWinId);

    int insert(BidResultTotal record);

    int insertSelective(BidResultTotal record);

    int insertBasicByBasicInfo(BidResultTotal record);

    BidResultTotal selectByPrimaryKey(String totalWinId);

    int updateByPrimaryKeySelective(BidResultTotal record);

    int updateByPrimaryKey(BidResultTotal record);
}