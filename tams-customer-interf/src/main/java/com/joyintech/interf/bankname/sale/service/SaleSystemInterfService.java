package com.joyintech.interf.bankname.sale.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.interf.bankname.sale.constant.SaleCodeList;
import com.joyintech.interf.bankname.sale.dao.SaleInterfMapper;
import com.joyintech.interf.bankname.sale.dto.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.product.payint.dto.FinProductPayInt003Dto;
import com.joyintech.tams.invest.product.productduration.establish.dto.FinProductEstablish002Dto;
import com.joyintech.tams.invest.product.productduration.expire.dto.FinProductExpire002Dto;
import com.joyintech.tams.invest.product.productduration.netproductbonusscheme.dto.FinNetProductBonusSchemeDto;
import com.joyintech.tams.invest.product.productduration.netproductduration.dto.FinNetProductDurationDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 销售接口业务处理
 * 〈功能详细描述〉
 * @author 吴鼎
 * @version 1.0 2019年7月4日
 * @see SaleSystemInterfService
 * @since 1.0
 */
@Service
public class SaleSystemInterfService {
	
	@Autowired
	private SaleInterfMapper saleSystemInterfMapper;
	
	@Autowired
	private SaleToBusinessDtoService saleToBusinessDtoService;
	
	@Autowired
	private ProductDealWFLService productDealWFLService;
	
	@Autowired
	private NetValueProductDealWFLService netValueProductDealWFLService;
	
	@Autowired
	private ToSaleService toSaleService;
	
	
	/**
     * 页面调用，按产品代码发送
     * @return
     */
	public void sendProductInfoToSale(String finprodId) {
		
		//适配器信息
		int adapterID = SaleCodeList.销售接口_产品信息接口_按产品代码;
		
		Map<String, Object> res = new HashMap<>();
		// 根据产品代码查询产品要素信息
        ProdBaseInfoDto prodBaseInfoDto = saleSystemInterfMapper.queryProdInfoByFinprodId(finprodId);
    	 
    	// 将产品要素插入临时表
        saleSystemInterfMapper.insertProductInfoToTemp(prodBaseInfoDto) ;
        //调用适配器生成文件
        res = toSaleService.sendToSaleByFinprodId(adapterID,finprodId);
        if(res.get("flag").toString().equals("true")){
          //更新状态
          updateSendStatus(finprodId);
        }
    }
	

	/**
     * 适配器或者定时任务调动，按日期发送
     * @return
     */
	public Map<String, Object> sendProductInfoToSaleByDate(String saledate) {
		//适配器信息
		int adapterID = SaleCodeList.销售接口_产品信息接口_按日期;
		
		// 根据产品代码查询产品要素信息
        List<ProdBaseInfoDto> lstProdBaseInfoDto = saleSystemInterfMapper.queryProdInfoByDate(saledate);
    	 
        for(ProdBaseInfoDto prodBaseInfoDto: lstProdBaseInfoDto){
        	// 将产品要素插入临时表
            saleSystemInterfMapper.insertProductInfoToTemp(prodBaseInfoDto);
            
//            if(res.get("flag").toString().equals("true")){
//              //更新状态
//	          updateSendStatus(prodBaseInfoDto.getFinprodid());
//	        }
        }
        //调用适配器生成文件
        return toSaleService.sendToSaleByDate(adapterID, saledate);
    }

	
	public void sendBonusPlanToSale(String finprodId) {
		
		//适配器信息
		int adapterID = SaleCodeList.销售接口_分红方案接口_按产品代码;
		Map<String, Object> res = new HashMap<>();
		
		// 根据产品代码查询分红方案
        List<BonusPlanDto> lstBonusPlan = saleSystemInterfMapper.queryBonusPlanByFinprodId(finprodId);
    	
    	// 将分红方案插入临时表
        for(BonusPlanDto bonusPlanDto : lstBonusPlan){
        	saleSystemInterfMapper.insertBonusPlanToTemp(bonusPlanDto);
        }
        //调用适配器生成文件
        res = toSaleService.sendToSaleByFinprodId(adapterID,finprodId);
        
	}
	
	public Map<String, Object> sendBonusPlanToSaleByDate(String date) {
		
		//适配器信息
		int adapterID = SaleCodeList.销售接口_分红方案接口_按日期;

		// 根据产品代码查询分红方案
        List<BonusPlanDto> lstBonusPlan = saleSystemInterfMapper.queryBonusPlanByDate(date);
    	
    	// 将分红方案插入临时表
        for(BonusPlanDto bonusPlanDto : lstBonusPlan){
        	saleSystemInterfMapper.insertBonusPlanToTemp(bonusPlanDto);
        }
        //调用适配器生成文件
        return toSaleService.sendToSaleByDate(adapterID, date);
        
	}

	
	public Map<String, Object> sendMarketIssueToSale(String date) {
		
		//适配器信息
		int adapterID = SaleCodeList.销售接口_行情下发口_按日期;

		List<MarketIssueDto> lstMarketIssue = saleSystemInterfMapper.queryMarketIssueByDate(date);
		
		// 将行情信息插入临时表
		for(MarketIssueDto marketIssueDto : lstMarketIssue){
        	saleSystemInterfMapper.insertMarketIssueToTemp(marketIssueDto);
        }
		//调用适配器生成文件
		Map<String, Object> res = toSaleService.sendToSaleByDate(adapterID, date);
		return res;
	}


	/**
     * 获取销售系统认申购数据
     * @return
	 * @throws Exception 
     */
	public Map<String, String> receiveAlloFromSale(String date) {
		
		Map<String, String> res = new HashMap<>();
		//从临时表获取产品认购数据
		List<SaleAlloDto> lstSaleAlloDto = saleSystemInterfMapper.queryAlloByDate(date);
		
		for(SaleAlloDto saleAlloDto : lstSaleAlloDto){
			
			// 根据产品代码查询是否存在此产品
			String finprodId = saleSystemInterfMapper.queryProdidByfinprodCode(saleAlloDto.getFinprodCode());
			
			if(!StringUtil.isEmpty(finprodId)){
				// 调用业务逻辑处理数据
				res = doSubAndPurToBusiness(saleAlloDto);
			}
			
		}
		
		//调用业务逻辑结果处理
		return res;
	}


	/**
     * 处理销售系统认申购数据
     * @return
	 * @throws Exception 
     */
	private Map<String, String> doSubAndPurToBusiness(SaleAlloDto saleAlloDto) {
		
		Map<String, String> res = new HashMap<>();
		// 获取产品收益类型
		String profitType = saleSystemInterfMapper.getProdProfitType(saleAlloDto.getFinprodCode());
		saleAlloDto.setProfitType(profitType);
		// 区分产品是收益性还是净值型产品
		if(DictCodeList.收益类型_收益型.getValue().equals(profitType)){
			
  	    	// 完善收益型交易model的属性内容
			FinProductEstablish002Dto finProductEstablish002Dto = saleToBusinessDtoService.rebuildProductModelAttribute(saleAlloDto);
			
			// 调用净值交易流程方法
			res = productDealWFLService.saveProEstablishInfo(finProductEstablish002Dto);
			
		}else if(DictCodeList.收益类型_净值型.getValue().equals(profitType)
  	    		||DictCodeList.收益类型_货币型.getValue().equals(profitType)){

  	    	// 完善交易model的属性内容
  	    	FinNetProductDurationDto finNetProductDurationDto = saleToBusinessDtoService.dealModelAttributeAllo(saleAlloDto) ;
  	    	// 调用净值交易流程方法
  	    	res = netValueProductDealWFLService.save(finNetProductDurationDto);
		
		}
		
		return res;
			
	}

	/**
     * 获取销售系统赎回到期数据
     * @return
	 * @throws Exception 
     */
	public Map<String, String> receiveRedeemFromSale(String date) {
		
		Map<String, String> res = new HashMap<>();
		//从临时表获取产品到期数据
		List<SaleRedeemDto> lstSaleRedeemDto = saleSystemInterfMapper.queryRedeemByDate(date);
		
		for(SaleRedeemDto saleRedeemDto : lstSaleRedeemDto){
			// 根据产品代码查询是否存在此产品
			String finprodId = saleSystemInterfMapper.queryProdidByfinprodCode(saleRedeemDto.getFinprodCode());
			
			if(!StringUtil.isEmpty(finprodId)){
				//调用业务逻辑处理数据
				res = doRedeemToBusiness(saleRedeemDto);
			}
			
		}
		
		//调用业务逻辑结果处理
        return res;
		
	}


	/**
     * 处理销售系统赎回到期数据
     * @return
	 * @throws Exception 
     */
	private Map<String, String> doRedeemToBusiness(SaleRedeemDto saleRedeemDto) {
		
		Map<String, String> res = new HashMap<>();
		// 获取产品收益类型
		String profitType = saleSystemInterfMapper.getProdProfitType(saleRedeemDto.getFinprodCode());
		saleRedeemDto.setProfitType(profitType);
		// 区分产品是收益性还是净值型产品
		if(DictCodeList.收益类型_收益型.getValue().equals(profitType)){
    		//将产品兑付文件交易数据构建成model对象
//    		FinProductExpire002Dto finProductExpire002Dto = productDealMDLService.anylysisStrArr2ExpireModel(attrData);
    		// 完善产品赎回以及到期交易model的属性内容
    		FinProductExpire002Dto finProductExpire002Dto = saleToBusinessDtoService.rebuildProductInfo(saleRedeemDto);
    		// 调用产品赎回以及到期交易流程方法
    		res =  productDealWFLService.saveExpireInfo(finProductExpire002Dto);
    	}
    	if(DictCodeList.收益类型_净值型.getValue().equals(profitType)
  	    		||DictCodeList.收益类型_货币型.getValue().equals(profitType)){ //净值型产品
    		// 将文件交易数据构建成model对象
//  	    	FinNetProductDurationDto finNetProductDurationDto= netValueProductDealMDLService.redExpStrArrToModel(attrData) ;
  	    	// 完善交易model的属性内容
  	    	FinNetProductDurationDto finNetProductDurationDto = saleToBusinessDtoService.dealModelAttributeRedeem(saleRedeemDto) ;
  	    	// 调用净值交易流程方法
  	    	res = netValueProductDealWFLService.save(finNetProductDurationDto);
    	}
		
    	return res;
	}
	
	
	/**
     * 获取销售系统份额强增强减数据
     * @return
     */
	public Map<String, String> receiveAddReduceSale(String date) {
		
		Map<String, String> res = new HashMap<>();
		//从临时表获取产品认购数据
		List<SaleAddReduceDto> lstSaleAddReduceDto = saleSystemInterfMapper.queryAddReduceByDate(date);
		
		for(SaleAddReduceDto saleAddReduceDto : lstSaleAddReduceDto){
			
			// 根据产品代码查询是否存在此产品
			String finprodId = saleSystemInterfMapper.queryProdidByfinprodCode(saleAddReduceDto.getFinprodCode());
			
			if(!StringUtil.isEmpty(finprodId)){
				//调用业务逻辑处理数据
				res = doAddReduceToBusiness(saleAddReduceDto);
			}
			
		}
		
		//调用业务逻辑结果处理
        return res;
		
	}


	/**
     * 处理销售系统份额强增强减数据
     * @return
     */
	private Map<String, String> doAddReduceToBusiness(SaleAddReduceDto saleAddReduceDto) {
		
		Map<String, String> res = new HashMap<>();
		// 完善交易model的属性内容
    	FinNetProductDurationDto finNetProductDurationDto = saleToBusinessDtoService.dealModelAttributeAddReduce(saleAddReduceDto) ;
    	// 调用净值交易流程方法
    	res = netValueProductDealWFLService.save(finNetProductDurationDto);
		
		return res;
	}
	
	
	/**
     * 获取销售系统分红汇总数据
     * @return
	 * @throws Exception 
     */
	public Map<String, String> receiveBonusCollectSale(String date) {
		
		Map<String, String> res = new HashMap<>();
		//从临时表获取产品认购数据
		List<SaleBonusCollectDto> lstSaleBonusCollectDto = saleSystemInterfMapper.queryBonusCollectByDate(date);
		
		for(SaleBonusCollectDto saleBonusCollectDto : lstSaleBonusCollectDto){
			
			// 根据产品代码查询是否存在此产品
			String finprodId = saleSystemInterfMapper.queryProdidByfinprodCode(saleBonusCollectDto.getFinprodCode());
			
			if(!StringUtil.isEmpty(finprodId)){
				//调用业务逻辑处理数据
				res = doBonusCollectToBusiness(saleBonusCollectDto);
			}
			
		}
		
		//调用业务逻辑结果处理
        return res;
		
	}


	/**
     * 处理销售系统分红汇总数据
     * @return
	 * @throws Exception 
     */
	private Map<String, String> doBonusCollectToBusiness(SaleBonusCollectDto saleBonusCollectDto) {
		
		Map<String, String> res = new HashMap<>();
		// 获取产品收益类型
		String profitType = saleSystemInterfMapper.getProdProfitType(saleBonusCollectDto.getFinprodCode());
		saleBonusCollectDto.setProfitType(profitType);
		// 区分产品是收益性还是净值型产品
		if(DictCodeList.收益类型_收益型.getValue().equals(profitType)){
    		//将产品兑付文件交易数据构建成model对象
//    		FinProductExpire002Dto finProductExpire002Dto = productDealMDLService.anylysisStrArr2ExpireModel(attrData);
    		// 完善产品赎回以及到期交易model的属性内容
			FinProductPayInt003Dto finProductPayInt003Dto = saleToBusinessDtoService.rebuildProductPayIntInfo(saleBonusCollectDto);
    		// 调用产品赎回以及到期交易流程方法
			res = productDealWFLService.save(finProductPayInt003Dto);
    	}
    	if(DictCodeList.收益类型_净值型.getValue().equals(profitType)
  	    		||DictCodeList.收益类型_货币型.getValue().equals(profitType)){ //净值型产品
    		
    		FinNetProductBonusSchemeDto finNetProductBonusSchemeDto = new FinNetProductBonusSchemeDto();
    		if(saleBonusCollectDto.getBonusamt().compareTo(BigDecimal.ZERO) >0){ // 现金红利下发
    			// 完善分红现金下发model的属性内容
    			finNetProductBonusSchemeDto = saleToBusinessDtoService.rebuildProductBonusCashDetail(saleBonusCollectDto);
    		}
    		if(saleBonusCollectDto.getRebonusamt().compareTo(BigDecimal.ZERO) > 0){ // 再投份额确认
    			// 完善再投份额确认model的属性内容
    			finNetProductBonusSchemeDto = saleToBusinessDtoService.rebuildProductBonusShareDetail(saleBonusCollectDto);
    		}
    		// 调用分红方案总金额交易流程方法
    		res = netValueProductDealWFLService.saveBonusSchemeInfo(finNetProductBonusSchemeDto);
    	}
		
		
		return res;
	}
	
	
	/**
     * 获取销售系统产品日计提利息信息数据
     * @return
     */
	public Map<String, String> receiveProdAccrueSale(String date) {
		
		Map<String, String> res = new HashMap<>();
		//从临时表获取产品认购数据
		List<SaleProdAccrueDto> lstSaleProdAccrueDto = saleSystemInterfMapper.queryProdAccrueByDate(date);
		
		for(SaleProdAccrueDto saleProdAccrueDto : lstSaleProdAccrueDto){
			// 根据产品代码查询是否存在此产品
			String finprodId = saleSystemInterfMapper.queryProdidByfinprodCode(saleProdAccrueDto.getFinprodCode());
			
			if(!StringUtil.isEmpty(finprodId)){
				//调用业务逻辑处理数据
				res = doProdAccrueToBusiness(saleProdAccrueDto);
			}
			
		}
		
		//调用业务逻辑结果处理
        return res;
		
	}


	/**
     * 处理销售系统产品日计提利息信息数据
     * @return
     */
	private Map<String, String> doProdAccrueToBusiness(SaleProdAccrueDto saleProdAccrueDto) {
		
		// 处理数据
		return null;
	}
	
	
	//更改发送状态
	private void updateSendStatus(String finprodId) {
		
		FinProdSendInfoDto sendInfoModel = new FinProdSendInfoDto();
		
		sendInfoModel.setFinprodid(finprodId);
		sendInfoModel.setSendstatus(DictCodeList.发送状态_发送成功.getValue());
		sendInfoModel.setSendremark("销售接口发送产品"+finprodId+"成功");
		sendInfoModel.setCreateuser("SALEINTERF");
		sendInfoModel.setCreatedept("");
		sendInfoModel.setUpdateuser("SALEINTERF");
		sendInfoModel.setContractno("");
		
		saleSystemInterfMapper.insertFinProdSendInfo(sendInfoModel);
	}
	
}
