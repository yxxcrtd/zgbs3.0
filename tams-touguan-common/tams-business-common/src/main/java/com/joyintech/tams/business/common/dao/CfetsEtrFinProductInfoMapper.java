package com.joyintech.tams.business.common.dao;


import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoResponse;
import org.springframework.stereotype.Repository;

/**
 * 债券信息mapper
 */
@Repository
public interface CfetsEtrFinProductInfoMapper {


    /**
     * 银行间债券信息查询
     *
     * @param request
     * @return
     */
    CfetsEtrFinProductInfoResponse findFinProductInfo(CfetsEtrFinProductInfoRequest request);
}
