package com.joyintech.tams.invest.batch.tenderbond.dao;

import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.InterfPayhsrcsecinfo;
import com.joyintech.tams.invest.batch.tenderbond.dto.FinTenderBondAssetDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 自动上市流通
 */
@Repository
public interface FinTenderBondAssetMapper {
    /**
     * 查询待上市流通的新债
     * @param param
     * @return
     */
    List<FinTenderBondAssetDto> selectListing(Map<String,Object> param);

    /**
     * 查询标的
     * @param param
     * @return
     */
    FinProduct selectProduct(Map<String,Object> param);

    /**
     * 查询债券接口表债券数据
     *
     * @param finprodMarketId
     * @return
     */
    List<InterfPayhsrcsecinfo> getBySecId(@Param("finprodMarketId") String finprodMarketId);

    /**
     * 查询债券接口表债券数据
     *
     * @param dto
     * @return
     */
    List<InterfPayhsrcsecinfo> getByParam(FinTenderBondAssetDto dto);
}
