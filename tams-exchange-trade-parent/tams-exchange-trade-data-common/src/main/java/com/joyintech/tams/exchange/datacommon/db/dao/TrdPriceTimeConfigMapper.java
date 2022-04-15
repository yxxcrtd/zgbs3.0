package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.TrdPriceTimeConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author swwan
 * @date 2022/4/14 - 11:03
 */
@Repository
public interface TrdPriceTimeConfigMapper {
    /**
     * delete by primary key
     * @param configNo primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(BigDecimal configNo);

    /**
     * insert record to table
     * @param record the record
     * @return insert count
     */
    int insert(TrdPriceTimeConfig record);

    /**
     * insert record to table selective
     * @param record the record
     * @return insert count
     */
    int insertSelective(TrdPriceTimeConfig record);

    /**
     * select by primary key
     * @param configNo primary key
     * @return object by primary key
     */
    TrdPriceTimeConfig selectByPrimaryKey(BigDecimal configNo);

    /**
     * update record selective
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(TrdPriceTimeConfig record);

    /**
     * update record
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(TrdPriceTimeConfig record);

    int updateBatch(List<TrdPriceTimeConfig> list);

    int updateBatchSelective(List<TrdPriceTimeConfig> list);

    int batchInsert(@Param("list") List<TrdPriceTimeConfig> list);
}