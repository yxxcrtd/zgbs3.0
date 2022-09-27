package com.joyintech.service.riskrds.limitmanagement.rsksupervisionriskanalyze.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.riskrds.limitmanagement.rsksupervisionriskanalyze.dto.RskSupervisionRiskAnalyzeDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 指标参数管理
 *
 * @author 刘伟
 * @version 1.0 2018年10月23日
 * @see RskSupervisionRiskAnalyzeMapper
 * @since 1.0
 */
@Repository
public interface RskSupervisionRiskAnalyzeMapper {

    /**
     * 查询界面查询事件
     *
     * @param pageParam 页面参数
     * @see
     * @since 1.0
     */
    List<RskSupervisionRiskAnalyzeDto> select(Map<String, Object> pageParam);
    
    List<RskSupervisionRiskAnalyzeDto> selectMx(Map<String, Object> pageParam);
    
    List<RskSupervisionRiskAnalyzeDto> selectNx(Map<String, Object> pageParam);
    
    List<RskSupervisionRiskAnalyzeDto> selectFu(Map<String, Object> pageParam);
    /**
     * 进入界面初始化查询
     */
    List<RskSupervisionRiskAnalyzeDto> selectInit(Map<String, Object> pageParam);

    List<SelectOptionDto> selectByUserName(Map<String, Object> param);

    List<SelectOptionDto> selectIndName(Map<String, Object> params);

    List<SelectOptionDto> selectGroupName(Map<String, Object> params);
}
