
package com.joyintech.service.period.trdstockbonus.dao;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdstockbonus.TrdStockBonusDto;


/**
 * 股票存续期
 * @author panbin
 * @version 2019年6月19日
 * @see TrdStockBonusMapper
 */
@Repository
public interface TrdStockBonusMapper {

    List<TrdStockBonusDto> findPage(Map<String, Object> param);

    List<SelectOptionDto> getFinprodMarketId(Map<String, Object> param);

	List<SelectOptionDto> getSecManageAcctId(Map<String, Object> param);

	List<SelectOptionDto> getBusiCode(Map<String, Object> param);

	int revokeCheck(Map<String, Object> param);

	List<TrdProductDeal> finPrdDealInfo(String busiCode);

	PtlTrade findPtlTradeInfo(String tradeId);

	TrdProductDealAdd findPrdDealAddInfo(String tradeId);

	List<StlPayOrders> findStlPayOrdInfo(String tradeId);

	List<FinAssetIntInfo> findDividDetailInfo(String busiCode);

	/**
	 * 查询有效分红明细(新头寸)
	 * @param busiCode
	 * @return
	 */
	List<FinAssetIntInfo> findNewPositionDividDetailInfo(String busiCode);

	List<FinAssetIntInfo> findividInfo(Map<String, Object> param);

	TrdStockBonusDto findEffectiveInfo(Map<String, Object> param);

	TrdStockBonusDto findProcessInfo(Map<String, Object> param);


    List<FinAssetIntInfo> findProcessDividInfo(String busiCode);

	/**
	 * 查询流程中分红明细(新头寸)
	 * @param busiCode
	 * @return
	 */
	List<FinAssetIntInfo> findNewPositionProcessDividInfo(String busiCode);

    List<String> findTradeId(Map<String, Object> bizCode);

    List<TrdStockBonusDto> getRTrdProductDeals(Map<String, Object> param);

    List<TrdProductDeal> findEffectiveT13Deal(Map<String, Object> param);
}
