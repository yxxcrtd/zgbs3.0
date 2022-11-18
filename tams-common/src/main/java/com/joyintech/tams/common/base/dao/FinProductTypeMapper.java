package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface FinProductTypeMapper extends BaseBatchMapper<FinProductType, FinProductTypeKey> {
    int deleteByPrimaryKey(FinProductTypeKey key);

    int insert(FinProductType record);

    int insertSelective(FinProductType record);

    int insertBasicByBasicInfo(FinProductType record);

    FinProductType selectByPrimaryKey(FinProductTypeKey key);

    int updateByPrimaryKeySelective(FinProductType record);

    int updateByPrimaryKey(FinProductType record);
}