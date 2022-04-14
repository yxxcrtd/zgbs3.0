package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdPriceTimeConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdPriceTimeConfigMapper {
    int deleteByPrimaryKey(String configNo);

    int insert(TrdPriceTimeConfig record);

    int insertSelective(TrdPriceTimeConfig record);

    int insertBasicByBasicInfo(TrdPriceTimeConfig record);

    TrdPriceTimeConfig selectByPrimaryKey(String configNo);

    int updateByPrimaryKeySelective(TrdPriceTimeConfig record);

    int updateByPrimaryKey(TrdPriceTimeConfig record);
}