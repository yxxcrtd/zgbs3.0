/*
 * 文件名：TaxBalanceQuery001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade003Dto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 信用拆借交易
 * @see TrdRepoValuationTrade001Mapper
 * @since 1.0
 */
@Repository(value="serviceTrdRepoValuationTrade001Mapper")
public interface TrdRepoValuationTrade001Mapper {

	/**
	 * 产品代码下拉框
	 */
	List<SelectOptionDto> findPortfolioId(Map<String, Object> params);


	/**
	 * 资产代码下拉框
	 */
//	List<SelectOptionDto> findFinprodId(Map<String, Object> params);


//    List<TrdLendValuationTradeDto> select(Map<String, Object> key);

    Integer  selectCountByTradeId(@Param("tradeId") String tradeId);


    /**
     * 获取自增序列值
     * @param sequenceName 序列名
     * @return
     */
    String getNextId(@Param("sequenceName") String sequenceName);



	/**
	 * 检索交易列表
	 *
	 * @param paraMap 参数
	 * @return 交易列表
	 * @see
	 * @since 1.0
	 */
	List<TrdRepoValuationTrade001Dto> selectDeal(Map<String, Object> paraMap);


	//根据交易编号查询数据  修改
	TrdRepoValuationTrade001Dto queryTradeInfoByTradeId(Map<String, Object> paraMap);


	//根据交易编号查询数据  存续期交易
	TrdRepoValuationTrade003Dto queryTradeInfoByTradeId01(Map<String, Object> paraMap);


}
