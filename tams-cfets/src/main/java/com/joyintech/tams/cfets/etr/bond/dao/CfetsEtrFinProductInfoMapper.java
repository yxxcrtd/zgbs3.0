package com.joyintech.tams.cfets.etr.bond.dao;


import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrFinProductInfoResponse;
import org.springframework.stereotype.Repository;

/**
 * 债券信息mapper
 */
@Repository
public interface CfetsEtrFinProductInfoMapper {


    /**
     * 银行间债券信息查询
     *
     * @param finprodId
     * @return
     */
    CfetsEtrFinProductInfoResponse findFinProductInfo(String finprodId);
}
