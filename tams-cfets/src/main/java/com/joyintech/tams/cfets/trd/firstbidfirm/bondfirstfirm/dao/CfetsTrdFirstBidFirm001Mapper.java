package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirsBidFirmBidOrderDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirsBidFirmDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmProductDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmEntrustDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 一级投标确认mapper
 * @Author: kchen
 * @Date: 2022/5/14 11:15
 */
@Repository
public interface CfetsTrdFirstBidFirm001Mapper {
    /**
     * 指令查询
     * @param params 入参条件
     * @return
     */
    List<CfetsTrdFirstBidFirmEntrustDto> listEntrust(Map<String, Object> params);

    /**
     * 根据指令代码查询指令对应的交易流程中数据
     * @param params
     * @return
     */
    int checkDataStatus(Map<String, Object> params);

    /**
     * 查询指令详细信息
     * @param param
     * @return
     */
    CfetsTrdBondFirsBidFirmDetailDto getEntrustInfo(Map<String, Object> param);

    /**
     * 查询投标产品列表
     * @param param
     * @return
     */
    List<CfetsTrdBondFirstBidFirmProductDto> getEntrustProductInfo(Map<String, Object> param);

    /**
     * 查询投标确认执行明细
     * @param param
     * @return
     */
    List<CfetsTrdBondFirsBidFirmBidOrderDetailDto> getBidOrderTransactionDetails(Map<String, Object> param);
}
