package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProductSpecial;
import org.springframework.stereotype.Repository;

@Repository
public interface FinProductSpecialMapper {
    int insert(FinProductSpecial record);

    int insertSelective(FinProductSpecial record);
}