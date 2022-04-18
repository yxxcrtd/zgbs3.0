package com.joyintech.tams.invest.batch.tenderbound.dao;

import com.joyintech.tams.invest.batch.tenderbound.dto.FinTenderBoundAssetDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自动上市流通
 */
@Repository
public interface FinTenderBoundAssetMapper {
    List<FinTenderBoundAssetDto> selectListing(Map<String,Object> param);
}
