package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InterfCstpBjfxjg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfCstpBjfxjgMapper {
    InterfCstpBjfxjg selectByPrimaryKey(@Param("trdId") String trdId);

    int insert(InterfCstpBjfxjg record);

    int insertSelective(InterfCstpBjfxjg record);
}