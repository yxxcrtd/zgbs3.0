package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinDealPledge;
import com.joyintech.tams.common.base.entity.FinDealPledgeKey;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinDealPledgeMapper extends BaseBatchMapper<FinDealPledge, FinDealPledgeKey> {
    int deleteByPrimaryKey(FinDealPledgeKey key);

    int insert(FinDealPledge record);

    int insertSelective(FinDealPledge record);

    int insertBasicByBasicInfo(FinDealPledge record);

    FinDealPledge selectByPrimaryKey(FinDealPledgeKey key);

    int updateByPrimaryKeySelective(FinDealPledge record);

    int updateByPrimaryKey(FinDealPledge record);
    /*
    * 根据金融产品代码查询所有的质押品信息
    * */
    List<FinDealPledge> selectByFinprodId (FinDealPledgeKey key);
}