package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InfContractPortfolioDetail;
import com.joyintech.tams.common.base.entity.InfContractPortfolioDetailKey;
import org.springframework.stereotype.Repository;

@Repository
public interface InfContractPortfolioDetailMapper {
    int deleteByPrimaryKey(InfContractPortfolioDetailKey key);

    int insert(InfContractPortfolioDetail record);

    int insertSelective(InfContractPortfolioDetail record);

    InfContractPortfolioDetail selectByPrimaryKey(InfContractPortfolioDetailKey key);

    int updateByPrimaryKeySelective(InfContractPortfolioDetail record);

    int updateByPrimaryKey(InfContractPortfolioDetail record);
}