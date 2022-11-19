package com.joyintech.tams.common.base.dao;


import com.joyintech.tams.common.base.entity.ShareBonusReg;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareBonusRegMapper extends BaseBatchMapper<ShareBonusReg, String> {

    int insertSelective(ShareBonusReg entity);



}
