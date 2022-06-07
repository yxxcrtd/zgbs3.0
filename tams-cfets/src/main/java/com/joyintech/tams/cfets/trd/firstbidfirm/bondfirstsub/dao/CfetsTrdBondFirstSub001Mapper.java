package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 一级申购MAPPER
 *
 * @author jp
 * @date 2022/04/18
 */
@Repository
public interface CfetsTrdBondFirstSub001Mapper {

    /**
     * 指令查询
     *
     * @param param 入参 页面查询条件
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/19 11:24
     */
    List<CfetsTrdBondFirstSubEntrustDto> listEntrust(Map<String, Object> param);

    /**
     * 根据指令代码查询指令对应的交易流程中数据
     *
     * @param param 入参 指令代码等
     * @return int
     * @author jiangpeng
     * @date 2022/4/20 14:16
     */
    int checkDataStatus(Map<String, Object> param);

    /**
     * 根据指令代码查询指令详细信息
     *
     * @param param 入参 指令代码等
     * @return int
     * @author jiangpeng
     * @date 2022/4/20 14:16
     */
    List<CfetsTrdBondFirstSubEntrustDetailDto> getEntrustInfo(Map<String, Object> param);

    /**
     * 查询当日申购
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstDaySubDto>
     * @author jiangpeng
     * @date 2022/4/26 19:34
     */
    List<CfetsTrdBondFirstDaySubDto> listDaySub(Map<String, Object> param);

    /**
     * 根据资产+证券账户维度查询申购明细
     *
     * @param param 资产 证券账户 等
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto>
     * @author jiangpeng
     * @date 2022/4/26 19:34
     */
    List<CfetsTrdBondFirstSubDealDetailDto> listDaySubDetail(Map<String, Object> param);

    /**
     * 申购执行明细
     *
     * @param param 查询条件
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubDealDetailDto>
     * @author jiangpeng
     * @date 2022/4/26 19:36
     */
    List<CfetsTrdBondFirstSubDealDetailDto> listDaySubDealDetail(Map<String, Object> param);
}
