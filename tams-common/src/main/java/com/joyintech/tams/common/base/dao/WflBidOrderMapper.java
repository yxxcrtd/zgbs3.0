package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflBidOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface WflBidOrderMapper {
    int deleteByPrimaryKey(String bidId);

    int insert(WflBidOrder record);

    int insertSelective(WflBidOrder record);

    WflBidOrder selectByPrimaryKey(String bidId);

    int updateByPrimaryKeySelective(WflBidOrder record);

    int updateByPrimaryKey(WflBidOrder record);
}