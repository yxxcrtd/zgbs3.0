package com.joyintech.tams.exchange.datacommon.db.service;

import com.joyintech.tams.exchange.common.db.domain.PortfolioDetailExample;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.datacommon.cache.AsyncService;

import java.util.List;

/**
 * @author swwan
 * @date 2022/5/10 - 22:29
 */
public interface PortfolioDetailService extends AsyncService<String, PortfolioDetail> {


    long countByExample(PortfolioDetailExample example);

    int deleteByExample(PortfolioDetailExample example);

    int deleteByPrimaryKey(String id);

    int insert(PortfolioDetail record);

    int insertSelective(PortfolioDetail record);

    List<PortfolioDetail> selectByExample(PortfolioDetailExample example);

    PortfolioDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(PortfolioDetail record, PortfolioDetailExample example);

    int updateByExample(PortfolioDetail record, PortfolioDetailExample example);

    int updateByPrimaryKeySelective(PortfolioDetail record);

    int updateByPrimaryKey(PortfolioDetail record);

    int updateBatch(List<PortfolioDetail> list);

}
