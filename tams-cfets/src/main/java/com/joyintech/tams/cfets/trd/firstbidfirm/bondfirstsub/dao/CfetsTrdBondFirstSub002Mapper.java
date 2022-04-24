package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDetailDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 一级申购新建MAPPER
 *
 * @author jp
 * @date 2022/04/18
 */
@Repository
public interface CfetsTrdBondFirstSub002Mapper {

    /**
     * 根据指令代码查询申购产品列表
     *
     * @param param 入参 指令代码等
     * @return int
     * @author jiangpeng
     * @date 2022/4/20 14:16
     */
    //TODO sql
    List<CfetsTrdBondFirstSubInfoDto> selectSubProducts(Map<String, Object> param);
}
