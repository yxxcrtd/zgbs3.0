/*
 * 文件名：CstpTradeMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨静敏
 * 修改时间：2018年11月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.common.dao;

import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstCounterParty;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.invest.common.dto.cstp.EtrEntrustExt;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradRepoasstDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 回购交易下行自动录单
 * 
 * @author 叶洋
 * @version 1.0 2020年05月27日
 * @see CstpAutoTradeMapper
 * @since 1.0
 */
@Repository
public interface CstpAutoTradeMapper {

	/**
	 * 查询抵押品信息
	 * 
	 * @param oritradeId
	 * @return
	 */
	List<InterfTradRepoasstDto> queryRepoAsst(@Param("oritradeId") String oritradeId);

	/**
	 * 根据交易对手名查询交易对手信息
	 * 
	 * @param counterName
	 * @return
	 */
	MstCounterParty queryCountPartyByName(@Param("counterName") String counterName);

	/**
	 * 修改接口表中的数据来源,用于撤销
	 * 
	 * @param execId
	 * @param businessType
	 */
	void updateInterfCstpTrade(@Param("execId") String execId, @Param("businessType") String businessType);

	/**
	 * 根据成交单编号查询回购委托信息
	 * 
	 * @param oritradeId
	 * @return
	 */
	EtrEntrustExt queryRepoEntrustInfo(@Param("oritradeId") String oritradeId);

	/**
	 * 根据成交单编号查询债券委托信息
	 * 
	 * @param oritradeId
	 * @return
	 */
	EtrEntrustExt queryBondEntrustInfo(@Param("oritradeId") String oritradeId);

	/**
	 * 根据成交编号判断是否已经生成了交易
	 * @param fTradeId
	 * @return
	 */
	TrdProductDeal isExistsTrade(@Param("fTradeId") String fTradeId);

	/**
	 * 根据市场代码查询债券信息
	 * @param finprodMarketId
	 * @return
	 */
	FinProduct selectSecByFinprodMarketId(@Param("finprodMarketId") String finprodMarketId);


}
