/*
 * 文件名：TaxBalanceQuery001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 固收类存续期管理
 * @see TrdFixIncomeValuDuration001Mapper
 * @since 1.0
 */
@Repository(value="serviceTrdFixIncomeValuDuration001Mapper")
public interface TrdFixIncomeValuDuration001Mapper {

	/**
	 * 产品代码下拉框
	 */
	List<SelectOptionDto> findPortfolioId(Map<String, Object> params);


	/**
	 * 资产代码下拉框
	 */
	List<SelectOptionDto> findFinprodId(Map<String, Object> params);





    Integer  selectCountByTradeId(@Param("tradeId") String tradeId);
//

    /**
     * 获取自增序列值
     * @param sequenceName 序列名
     * @return
     */
    String getNextId(@Param("sequenceName") String sequenceName);



	/**
	 * 检索交易列表
	 */
	List<TrdFixIncomeValuDuration001Dto> selectDeal(Map<String, Object> paraMap);

	/**
	 * 根据交易编号查询数据
	 */
	TrdFixIncomeValuDuration001Dto queryTradeInfoByTradeId(String tradeId);


}
