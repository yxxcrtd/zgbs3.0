package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.InfContractPortfolioDetail;

public interface InfContractPortfolioDetailMapper {
    int deleteByPrimaryKey(String seqno);

    int insert(InfContractPortfolioDetail record);

    int insertSelective(InfContractPortfolioDetail record);

    InfContractPortfolioDetail selectByPrimaryKey(String seqno);

    int updateByPrimaryKeySelective(InfContractPortfolioDetail record);

    int updateByPrimaryKey(InfContractPortfolioDetail record);
}