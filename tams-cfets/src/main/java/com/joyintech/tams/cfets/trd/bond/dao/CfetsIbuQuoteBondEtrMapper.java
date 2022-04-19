package com.joyintech.tams.cfets.trd.bond.dao;

import com.joyintech.tams.cfets.trd.bond.dto.EntrustAvaShareAmtDto;
import com.joyintech.tams.common.base.entity.EtrDistributeInterfTmp;
import com.joyintech.tams.common.base.entity.EtrQuoteFinishStatus;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 指令相关操作
 */
@Repository
public interface CfetsIbuQuoteBondEtrMapper {

    int checkDataStatus(Map<String, Object> map);



    List<EtrDistributeInterfTmp> listEtrDistributeInterfTmp(Map<String, Object> map);



    List<EtrQuoteFinishStatus> listEtrQuoteFinishStatus(Map<String, Object> param);

    int countWflEtr(Map<String, Object> param);

    FinExercisePlan getFinExercisePlan(Map<String, Object> param);

    int countFailEtr(Map<String, Object> param);

    List<EntrustAvaShareAmtDto> listAvaShareAmt(Map<String, Object> param);

    String getAccNo(Map<String,String> param);
}
