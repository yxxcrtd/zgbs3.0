package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.MstCustomerInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MstCustomerInfoMapper {
    int deleteByPrimaryKey(String customerId);

    int insert(MstCustomerInfo record);

    int insertSelective(MstCustomerInfo record);

    int insertBasicByBasicInfo(MstCustomerInfo record);

    MstCustomerInfo selectByPrimaryKey(String customerId);

    int updateByPrimaryKeySelective(MstCustomerInfo record);

    int updateByPrimaryKey(MstCustomerInfo record);

    List<MstCustomerInfo> getAllCustomerName(@Param("customerIds") List<String> customerIds);
}