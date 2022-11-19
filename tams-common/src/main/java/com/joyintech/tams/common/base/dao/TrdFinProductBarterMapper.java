package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.TrdFinProductBarter;
import com.joyintech.tams.common.base.entity.TrdFinProductBarterKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface TrdFinProductBarterMapper extends BaseBatchMapper<TrdFinProductBarter, TrdFinProductBarterKey> {
    int deleteByPrimaryKey(TrdFinProductBarterKey key);

    int insert(TrdFinProductBarter record);

    int insertSelective(TrdFinProductBarter record);

    int insertBasicByBasicInfo(TrdFinProductBarter record);

    TrdFinProductBarter selectByPrimaryKey(TrdFinProductBarterKey key);

    int updateByPrimaryKeySelective(TrdFinProductBarter record);

    int updateByPrimaryKey(TrdFinProductBarter record);
}