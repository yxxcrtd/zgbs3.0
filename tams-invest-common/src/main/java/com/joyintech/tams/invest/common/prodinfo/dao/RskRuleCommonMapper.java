package com.joyintech.tams.invest.common.prodinfo.dao;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.batchList.dao.BatchListMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.joyintech.tams.invest.common.risktrialrds.dto.IsupLogDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDetailDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.dto.SupervisionParameterDto;
import com.joyintech.tams.invest.common.prodinfo.dto.IRskRulePoolConfig;


@Repository
public interface RskRuleCommonMapper extends BatchListMapper<RskPortfolioIndicator> {

	List<IRskRulePoolConfig> getRulePoolConfig(@Param("code") String code);

	/**
	 * 查询所有的规则包
	 * @return
	 */
	List<IRskRulePoolConfig> selectPtlRuleAll();

	List<String> getMatchPortData(@Param("sqlWhere") String sqlWhere,@Param("portfolioId") String portfolioId, @Param("valDate") String valDate);

	/**
	 * 获取规则关联指标
	 * @param list 各个维度及维度对应的值
	 * @return 规则代码
	 * @see
	 * @since 1.0
	 */
	List<String> selectRuleRelInd(List<String> list);

	/**
	 * 获取规则关联组合
	 * @param list 各个维度及维度对应的值
	 * @return 规则代码
	 * @see
	 * @since 1.0
	 */
	List<String> selectRuleRelPort(List<String> list);

	/**
	 * 删除指标与组合关联关系
	 * @param list
	 */
	void deletePortIndicator(List<RskPortfolioIndicator> list);

	/**
	 * 删除规则与组合关联关系
	 * @param ruleId
	 */
	int deleteRulePort(@Param("ruleId") String ruleId,@Param("list") List<String> list);

	/**
	 * 删除与指定规则之外的规则与组合关联关系
	 * @param ruleList
	 * @param portList
	 */
	int deleteRulePortReverse(@Param("ruleList") List<String> ruleList,@Param("portList") List<String> portList);

	/**
	 * 插入组合与指标关联操作
	 * @param list
	 */
	@Override
	void insertBatchList(List<RskPortfolioIndicator> list);

	/**
	 * 插入规则包与组合关系
	 * @param list
	 */
	void insertRskRulePortBatchList(List<RskRulePortfolio> list);
}
