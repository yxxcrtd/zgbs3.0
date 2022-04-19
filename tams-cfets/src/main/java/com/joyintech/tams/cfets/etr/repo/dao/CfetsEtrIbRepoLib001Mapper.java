package com.joyintech.tams.cfets.etr.repo.dao;

import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoLibAvaPledgeBondResponse;
import com.joyintech.tams.common.base.entity.EtrEntrustPledge;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CfetsEtrIbRepoLib001Mapper {


    /**
     * 质押券入库画面，查询可质押债券列表
     *
     * @param pageParam pageParam
     * @return List<TrdStandardBondInDto>
     */
    List<CfetsEtrIbRepoLibAvaPledgeBondResponse> selectAvaPledgeBondList(Map<String, Object> pageParam);
    
    /**
     * 质押券入库画面，查询已入库债券列表
     *
     * @param pageParam pageParam
     * @return List<TrdStandardBondInDto>
     */
    List<CfetsEtrIbRepoLibAvaPledgeBondResponse> selectPledgeBondList(Map<String, Object> pageParam);
    
    
    EtrEntrustPledge selectEtrEntrustPledge(EtrEntrustPledge etrEntrustPledge);
    
    int deleteEtrEntrustPledge(EtrEntrustPledge etrEntrustPledge); 
}
