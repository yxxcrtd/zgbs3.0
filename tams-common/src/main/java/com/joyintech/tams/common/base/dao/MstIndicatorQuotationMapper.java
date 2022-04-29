package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.MstIndicatorQuotation;
import com.joyintech.tams.common.base.entity.MstIndicatorQuotationKey;
import org.springframework.stereotype.Repository;

@Repository
public interface MstIndicatorQuotationMapper {
    int deleteByPrimaryKey(MstIndicatorQuotationKey key);

    int insert(MstIndicatorQuotation record);

    int insertSelective(MstIndicatorQuotation record);

    int insertBasicByBasicInfo(MstIndicatorQuotation record);

    MstIndicatorQuotation selectByPrimaryKey(MstIndicatorQuotationKey key);

    int updateByPrimaryKeySelective(MstIndicatorQuotation record);

    int updateByPrimaryKey(MstIndicatorQuotation record);
}