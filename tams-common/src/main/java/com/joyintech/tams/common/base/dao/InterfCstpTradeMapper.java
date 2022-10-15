package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InterfCstpTrade;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfCstpTradeMapper {
    int insert(InterfCstpTrade record);

    int insertSelective(InterfCstpTrade record);

    int insertBasicByBasicInfo(InterfCstpTrade record);

    InterfCstpTrade selectByPrimaryKey(@Param("execid") String execid);
}