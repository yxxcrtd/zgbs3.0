package com.joyintech.tams.invest.batch.tenderbond.dao;

import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.invest.batch.tenderbond.dto.FinTenderBondAssetDto;
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
    List<FinProduct> selectProduct(Map<String,Object> param);
}
