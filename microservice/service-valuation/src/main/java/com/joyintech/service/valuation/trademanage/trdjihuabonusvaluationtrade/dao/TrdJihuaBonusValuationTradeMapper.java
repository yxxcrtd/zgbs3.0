/*
 * 文件名：TaxBalanceQuery001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.dao;

import com.joyintech.service.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 基金存续期
 * @author yuzj
 * @version 1.0 2021年01月22日
 * @see TrdJihuaBonusValuationTradeMapper
 * @since 1.0
 */
@Repository(value="serviceTrdJihuaBonusValuationTradeMapper")
public interface TrdJihuaBonusValuationTradeMapper {

    Integer  selectCountByTradeId(@Param("tradeId") String tradeId);

    /**
     * 获取自增序列值
     * @param sequenceName 序列名
     * @return
     */
    String getNextId(@Param("sequenceName") String sequenceName);


	TrdJihuaBonusValuationTradeDto queryTradeInfoByTradeId(Map<String, Object> params);

	/**
	 * 检索债券交易列表
	 *
	 * @param paraMap 参数
	 * @return 债券交易列表
	 * @see
	 * @since 1.0
	 */
	List<TrdJihuaBonusValuationTradeDto> selectDeal(Map<String, Object> paraMap);


}
