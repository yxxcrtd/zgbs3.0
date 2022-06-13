package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.BokFinprodSchemeInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface BokFinprodSchemeInfoMapper {
    int deleteByPrimaryKey(String booksetId);

    int insert(BokFinprodSchemeInfo record);

    int insertSelective(BokFinprodSchemeInfo record);

    int insertBasicByBasicInfo(BokFinprodSchemeInfo record);

    BokFinprodSchemeInfo selectByPrimaryKey(String booksetId);

    int updateByPrimaryKeySelective(BokFinprodSchemeInfo record);

    int updateByPrimaryKey(BokFinprodSchemeInfo record);
}