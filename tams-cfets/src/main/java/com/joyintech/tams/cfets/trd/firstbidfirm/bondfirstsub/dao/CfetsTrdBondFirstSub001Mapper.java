package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
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
}
