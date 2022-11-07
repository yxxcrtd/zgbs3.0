package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfTradSecdealMapper {
    int deleteByPrimaryKey(String dealno);

    int insert(InterfTradSecdeal record);

    int insertSelective(InterfTradSecdeal record);

    int insertBasicByBasicInfo(InterfTradSecdeal record);

    InterfTradSecdeal selectByPrimaryKey(String dealno);

    InterfTradSecdeal selectByFedealno(String fedealno);

    int updateByPrimaryKeySelective(InterfTradSecdeal record);

    int updateByPrimaryKey(InterfTradSecdeal record);
}