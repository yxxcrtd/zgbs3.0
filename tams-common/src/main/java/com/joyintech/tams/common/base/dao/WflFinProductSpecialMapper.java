package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.WflFinProductSpecial;
import org.springframework.stereotype.Repository;

@Repository
public interface WflFinProductSpecialMapper {
    int insert(WflFinProductSpecial record);

    int insertSelective(WflFinProductSpecial record);
}