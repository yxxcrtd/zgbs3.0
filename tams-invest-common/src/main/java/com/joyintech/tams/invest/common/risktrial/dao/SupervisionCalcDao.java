package com.joyintech.tams.invest.common.risktrial.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.invest.common.risktrial.dto.IsupLogDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDetailDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrial.dto.SupervisionParameterDto;


@Repository
public interface SupervisionCalcDao {

	@Select("${sql}")
	List<Map<String, Object>> selectOne(@Param("sql") String sql);
	
	
	@Select("select m.PARA_CODE,m.PARA_GRANULARITY,m.PARA_DISPLAY_TYPE,m.CONTENT,n.IS_MATH,"
			+ "  n.PARA_ASSET_TYPE,n.PARA_ASSET_SEC_TYPE,n.PARA_ASSET_THR_TYPE,n.INVEST_PROPORTY from RSK_ISUP_INDICATOR_PARA_DTL m "
			+ " left join RSK_ISUP_INDICATOR_PARA n on m.PARA_CODE=n.PARA_CODE "
			+ " where m.PARA_CODE=#{code} and m.PARA_GRANULARITY=#{granularity}")
	SupervisionParameterDto getIndicatorParam(@Param("code") String code, @Param("granularity") String granularity);
	
	@Select("select type from ISUP_INDICATOR_PARAM where code=#{code}")
	String getIndicatorParamType(@Param("code") String code);
	
	@Delete(
			"delete from RSK_ISUP_POINT_VAL "
			+ " where PORTFOLIO_ID=#{portfolioId} "
			+ " and  IND_ID= #{indId} "
			+ " and SUP_DATE=#{supDate}"
	)
	void deletePointSupervisionValue(RiskTrialRetDto value);
	
	@Delete(
			"delete from RSK_ISUP_POINT_DET_VAL "
			+ " where PORTFOLIO_ID=#{portfolioId, jdbcType=VARCHAR} "
			+ " and  IND_ID= #{indId, jdbcType=VARCHAR} "
			+ " and SUP_DATE=#{supDate, jdbcType=DATE}"
			+ " and ASSET_CODE=#{assetCode, jdbcType=VARCHAR}"
	)
	void deleteMxPointSupervisionValue(RiskTrialRetDetailDto value);
	
	@Insert(
			"insert into RSK_ISUP_POINT_VAL ("
			+ "  PORTFOLIO_ID"
			+ "  , IND_ID"
			+ "  , SUP_DATE"
			+ "  , SUP_VAL"
			+ "  , SUP_STAT"
			+ "  , WARN_COND"
			+ "  , FOUL_COND"
			+ "  , CREATE_USER"
			+ "  , CREATE_DEPT" 
            + "  , CREATE_TIME" 
            + "  , UPDATE_USER" 
            + "  , UPDATE_TIME" 
			+ ") values ("
			+ "    #{portfolioId, jdbcType=VARCHAR}"
			+ "  , #{indId, jdbcType=INTEGER}"
			+ "  , #{supDate, jdbcType=DATE}"
			+ "  , #{supVal, jdbcType=VARCHAR}"
			+ "  , #{supStat, jdbcType=VARCHAR}"
			+ "  , #{warnCond, jdbcType=VARCHAR}"
			+ "  , #{foulCond, jdbcType=VARCHAR}"
			+ "  , #{createUser, jdbcType=VARCHAR}" 
			+ "  , #{createDept, jdbcType=VARCHAR}" 
            + "  , #{createTime, jdbcType=TIMESTAMP}"
            + "  , #{updateUser, jdbcType=VARCHAR}" 
            + "  , #{updateTime, jdbcType=TIMESTAMP}"
			+ ")"
	)
	void addPointSupervisionValue(RiskTrialRetDto value);
	
	@Insert(
			"insert into RSK_ISUP_POINT_DET_VAL ("
			+ "  PORTFOLIO_ID"
			+ "  , IND_ID"
			+ "  , SUP_DATE"
			+ "  , ASSET_CODE"
			+ "  , ASSET_NAME"
			+ "  , SUP_VAL"
			+ "  , SUP_STAT"
			+ "  , CREATE_USER"
			+ "  , CREATE_DEPT" 
            + "  , CREATE_TIME" 
            + "  , UPDATE_USER" 
            + "  , UPDATE_TIME" 
			+ ") values ("
			+ "    #{portfolioId, jdbcType=VARCHAR}"
			+ "  , #{indId, jdbcType=INTEGER}"
			+ "  , #{supDate, jdbcType=DATE}"
			+ "  , #{assetCode, jdbcType=VARCHAR}"
			+ "  , #{assetName, jdbcType=VARCHAR}"
			+ "  , #{supVal, jdbcType=VARCHAR}"
			+ "  , #{supStat, jdbcType=VARCHAR}"
			+ "  , #{createUser, jdbcType=VARCHAR}" 
			+ "  , #{createDept, jdbcType=VARCHAR}" 
            + "  , #{createTime, jdbcType=TIMESTAMP}"
            + "  , #{updateUser, jdbcType=VARCHAR}" 
            + "  , #{updateTime, jdbcType=TIMESTAMP}"
			+ ")"
	)
	void addMxPointSupervisionValue(RiskTrialRetDetailDto value);
	
	@Insert(
			"insert into RSK_ISUP_LOG ("
			+ "  LOG_ID"
			+ "  , PORTFOLIO_ID"
			+ "  , IND_ID"
			+ "  , LOG_LVL"
			+ "  , MESSAGE"
			+ "  , LOG_TIME"
			+ "  , CREATE_USER"
			+ "  , CREATE_DEPT" 
            + "  , CREATE_TIME" 
            + "  , UPDATE_USER" 
            + "  , UPDATE_TIME" 
			+ ") values ("
			+ "    #{logId, jdbcType=VARCHAR}"
			+ "  , #{portfolioId, jdbcType=VARCHAR}"
			+ "  , #{indId, jdbcType=INTEGER}"
			+ "  , #{logLvl, jdbcType=VARCHAR}"
			+ "  , #{message, jdbcType=VARCHAR}"
			+ "  , #{logTime, jdbcType=DATE}"
			+ "  , #{createUser, jdbcType=VARCHAR}" 
			+ "  , #{createDept, jdbcType=VARCHAR}" 
            + "  , #{createTime, jdbcType=TIMESTAMP}"
            + "  , #{updateUser, jdbcType=VARCHAR}" 
            + "  , #{updateTime, jdbcType=TIMESTAMP}"
            + ")"
	)
	void writeLog(IsupLogDto value);
	
}
