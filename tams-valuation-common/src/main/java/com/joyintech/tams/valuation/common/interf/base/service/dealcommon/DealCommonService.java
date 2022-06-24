package com.joyintech.tams.valuation.common.interf.base.service.dealcommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.constant.DictCodeList;
import org.apache.commons.collections4.CollectionUtils;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductDealBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.ProductDealDto;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.val.dao.BokTradeBuildMapper;


/**
 * 投管估值一体化接口共通具体实现Service
 * 
 * @author wujie
 * @version 1.0 2019年2月15日
 * @see DealCommonService
 * @since 1.0
 */
@Service
public class DealCommonService {
	
	@Autowired
	private BokTradeBuildMapper bokTradeBuildMapper;
	
	@Autowired
    private InterfCommonService interfCommonService;
	
	@Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;
	

	/**
	 * 查询投资组合对应的产品信息
	 * @param portfolidId 投资组合代码
	 * @return
	 */
	public FinProduct queryFinProductByPortfolidId(String portfolidId){
		FinProduct finProduct = new FinProduct();
		/*
		//投资组合收益类型
		String profitType = bokTradeBuildMapper.queryProfitType(portfolidId);
		
		//2.1 查询投资组合的收益类型
		if(DictCodeList.收益类型_收益型.getValue().equals(profitType)){
			return finProduct;
		}*/
		//调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->查询组合信息SHEET
		PtlPortfolio  ptlPortfolio =interfCommonService.getPtlPortfolo(portfolidId);
		//IF 变量.组合信息Dto IS NOT NULL AND (变量.组合信息Dto.母投资组合代码 IS NOT NULL OR 变量.组合信息Dt.收益类型)
		if(ptlPortfolio != null 
				&& (DictCodeListExt1.组合类型_子组合.getValue().equals(ptlPortfolio.getPortfolioType()) 
						|| DictCodeList.收益类型_收益型.getValue().equals(ptlPortfolio.getProfitType()))){
			return finProduct;
		}
		//2.2 根据投资组合代码查询对应的理财产品信息
		finProduct = bokTradeBuildMapper.queryFinProductByPortfolidId(portfolidId); 
		
		return finProduct;
	}
	/**
	 * 结算指令生成_活期存款交易
	 */
	public Map<String,Object> settlementOrder_current(ProductDealDto dto,List<StlOrdersMapping> listStlOrdersMapping,String myAcctNo,String myAcctType){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<StlOrders> stlOrdersList = new ArrayList<StlOrders>();
		List<StlOrdersExt> stlOrdersExtList = new ArrayList<StlOrdersExt>();
		for(StlOrdersMapping obj :listStlOrdersMapping){
			// 定义 变量.收付方向
	        String ps = "";
	        // 定义 变量.对方资金账户
	        String counterAcctNo = "";
	        
	        // 2.1 期存款利息_结息、活期存款利息_卖出
	        if (DictCodeListExt1.指令类型_活期存款利息_结息.getValue().equals(obj.getOrderType())
	            || DictCodeListExt1.指令类型_活期存款利息_卖出_转仓.getValue().equals(obj.getOrderType())) {
	            counterAcctNo = null;
	            ps = DictCodeList.收付方向_收入.getValue();
	        }
	        // 2.2 活期存款利息_买入
	        else if (DictCodeListExt1.指令类型_活期存款利息_买入_转仓.getValue().equals(obj.getOrderType())) {
	            counterAcctNo = null;
	            ps = DictCodeListExt1.收付方向_支出.getValue();
	        }
	        
	        // 定义 变量.指令编号 = 自动生成
	        String fingerNo = interfCommonService.getTradeid(obj.getOrderType(), dto.getTradeDate(), "B");
	        // 入库
	        // 定义 变量.结算指令Dto
	        StlOrders valOrders = new StlOrders();
	        valOrders.setOrderId(fingerNo);
	        valOrders.setBusiType(dto.getBusiType());
	        valOrders.setOrderType(obj.getOrderType());
	        valOrders.setSettleDate(dto.getTradeDate());
	        valOrders.setTradeId(dto.getTradeId());
	        valOrders.setMyAcctNo(myAcctNo);
	        valOrders.setPs(ps);
	        valOrders.setCounterAcctNo(counterAcctNo);
	        valOrders.setSettleAmt(dto.getTardeAmt());
	        valOrders.setCcy(dto.getCcy());
	        valOrders.setSettleStatus(DictCodeList.成功失败标志_成功.getValue());
	        valOrders.setOrderStatus(dto.getTradeStatus());
	        stlOrdersList.add(valOrders);

	        // 定义 变量.结算指令扩展Dto
	        StlOrdersExt valOrdersExt = new StlOrdersExt();
	        valOrdersExt.setOrderId(fingerNo);
	        valOrdersExt.setIsTrade(obj.getIsBook());
	        stlOrdersExtList.add(valOrdersExt);
		}
		if(stlOrdersList.size()>0){
			resultMap.put("ORDERS_LIST", stlOrdersList);
		}
		if(stlOrdersExtList.size()>0){
			resultMap.put("ORDERS_EXT_LIST", stlOrdersExtList);
		}
		return resultMap;
	}
	
	
	public int insertBatchTrdProductDeal(List<TrdProductDeal> list){
		int result =0;
		// 入库
        if (CollectionUtils.isNotEmpty(list)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();
            
            for (TrdProductDeal e : list) {
                // 小于最大值
                if (listInsert.size() < 500) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= 500) {
                    valFinProductDealBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valFinProductDealBatchMapper.insertBatch(listInsert);
            }
        }
		return result;
	}
}
