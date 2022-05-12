package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetailExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author swwan
 * @date 2022/5/11 - 9:44
 */
public interface PortfolioDetailMapper extends IMybatisMapper<PortfolioDetail, String, PortfolioDetailExample> {
    long countByExample(PortfolioDetailExample example);

    int deleteByExample(PortfolioDetailExample example);

    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(String id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(PortfolioDetail record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(PortfolioDetail record);

    List<PortfolioDetail> selectByExample(PortfolioDetailExample example);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    PortfolioDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") PortfolioDetail record, @Param("example") PortfolioDetailExample example);

    int updateByExample(@Param("record") PortfolioDetail record, @Param("example") PortfolioDetailExample example);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(PortfolioDetail record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(PortfolioDetail record);

    int updateBatch(List<PortfolioDetail> list);
}