/*
 * 文件名：TaxBalanceQuery001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dao;

import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 基金交易
 * @author yuzj
 * @version 1.0 2021年01月04日
 * @see TrdFundValuationTradeMapper
 * @since 1.0
 */
@Repository(value="serviceTrdFundValuationTradeMapper")
public interface TrdFundValuationTradeMapper {

    List<TrdFundValuationTradeDto> select(Map<String, Object> key);

    Integer  selectCountByTradeId(@Param("tradeId") String tradeId);


    /**
     * 获取自增序列值
     * @param sequenceName 序列名
     * @return
     */
    String getNextId(@Param("sequenceName") String sequenceName);


	TrdFundValuationTradeDto queryTradeInfoByTradeId(Map<String, Object> param);

	TrdFundValuationTradeDto queryRedInfoByTradeId(@Param("tradeId") String tradeId);

	TrdFundValuationTradeDto queryRedInfoByOutTradeId(@Param("tradeId") String tradeId);

	/**
	 * 检索债券交易列表
	 *
	 * @param paraMap 参数
	 * @return 债券交易列表
	 * @see
	 * @since 1.0
	 */
	List<TrdFundValuationTradeDto> selectDeal(Map<String, Object> paraMap);


}
