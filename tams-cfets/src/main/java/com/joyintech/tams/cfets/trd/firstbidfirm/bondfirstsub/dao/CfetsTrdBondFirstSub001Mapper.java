package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CfetsTrdBondFirstSub001Mapper {

    /**
     * 指令查询
     *
     * @param param
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/19 11:24
     */
    List<CfetsTrdBondFirstSubEntrustDto> listEntrust(Map<String, Object> param);
}
