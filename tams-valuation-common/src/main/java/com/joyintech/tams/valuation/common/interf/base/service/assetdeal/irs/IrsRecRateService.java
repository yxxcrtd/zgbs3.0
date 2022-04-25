/*
 * 文件名：LendRateDealService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.irs;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlRateDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拆借收息Service 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author lishicheng
 * @version 1.0 2018年8月24日
 * @see IrsRecRateService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class IrsRecRateService {

	@Autowired
	private InterfCommonService interfCommonService;

	/**
	 * TemplateAnalysisForXMLService
	 */
	@Autowired
	private TemplateAnalysisForXMLService templateAnalysisForXMLService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LendDealCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
			TemplateOrderInfoReaderResult acctEntity) {
		LendDealCommonResult result = new LendDealCommonResult();
		List<ValDlRateDeal> listValDlRateDeal = new ArrayList<ValDlRateDeal>();
		Map<String,Object> params = acctEntity.getParams();
		// 解析模板后的list以及日志list
		TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
		// 1、文件读取解析
	    returnResult = templateAnalysisForXMLService.execute(acctEntity);
	    
		// 2、进行数据有效性的check,并生出进入日志表的数据结构
		listValDlRateDeal = returnResult.getT(ValDlRateDeal.class);
		//获取数据数量
        result.setDataNum(listValDlRateDeal.size());

		/**add by wujie on 20210506 start**/
		if(returnResult.isReadFile()){
			result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
		}
		/**add by wujie on 20210506 end**/

		// 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValDlRateDeal(listValDlRateDeal, acctEntity,params,returnResult.getValInterflog());
		if (map != null) {
			result.setListValFinProductDeal((List<TrdProductDeal>) map.get("ValFinProductDeal"));
			result.setListValFinProductDealExt((List<TrdProductDealAdd>) map.get("ValFinProductDealExt"));
			result.setBokDealAccountingList((List<BokDealAccounting>)map.get(
		                    "ValDealAccounting"));
		    result.setBokDealAccountingDetailList((List<BokDealAccountingDetail>)map.get(
		            "ValDealAccountingDetail"));
		    result.setBokDetailIdList((List<BokDetailId>)map.get(
		            "ValBokDetailId"));
			result.setListValOrders((List<StlOrders>)map.get(
	                            "valOrdersList"));
            result.setListValOrdersExt((List<StlOrdersExt>)map.get(
	                            "valOrdersExtList"));
    		// 日志
    		result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
		}
		return result;
	}

	/**
	 * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构 <一句话功能简述> <功能详细描述>
	 * 
	 * @param listValDlRateDeal
	 * @return Map<String, Object>
	 * @see
	 * @since 1.0
	 */
	@SuppressWarnings({"unchecked"})
	private Map<String, Object> handleValDlRateDeal(List<ValDlRateDeal> listValDlRateDeal, TemplateOrderInfoReaderResult acctEntity,Map<String,Object> params,
			List<ValInterflog> valInterflog) {
		Map<String, Object> resultMap = new HashMap<>();
		// 金融产品交易list
		List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
		// 估值核算交易扩展list
		List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
		// 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
		//结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
		
        List<ValInterflog> valInterflogList =  new ArrayList<>();
		
        //同业借款：四级科目类型 01：资产代码  02：期限品种
        Map<String,BokBooksetExt> bokBooksetExtMap = new HashMap<String,BokBooksetExt>();
        
		for (ValDlRateDeal dto : listValDlRateDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
			dto.setCreateTime(acctEntity.getResovleDate());
			TargetFinProduct targetFinProduct = interfCommonService.queryTargetFinProduct(dto.getDlId());
			String finProductType = targetFinProduct==null?null:targetFinProduct.getFinprodType();
			// 金融产品交易
			TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto,finProductType);
			Long maxBranch = interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收利息.getValue());
			valFinProductDeal.setBranch(maxBranch);
			listValFinProductDeal.add(valFinProductDeal);
			// 交易入账
			InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
			
			//同业借款交易的四级科目尾号、四级科目尾号名称改为资产的金融产品代码、金融产品简称
			String detailSubjectNoTail = null;
			String detailSubnameTail = null;
			if(StringUtil.isEmpty(acctEntity.getFourLevelSubjectKg())
					|| DictCodeList.是否_是.getValue().equals(acctEntity.getFourLevelSubjectKg())){
				if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finProductType)) {
					BokBooksetExt bokBooksetExt = bokBooksetExtMap.get(dealAccounting.getBokDetailId().getBooksetId());
	            	if(bokBooksetExt==null){
	            		bokBooksetExt = interfCommonService.queryBokBooksetExtbyId(ValDictCodeList.账套配置表参数项_同业借款四级科目类型.getValue(),dealAccounting.getBokDetailId().getBooksetId());
	            		bokBooksetExtMap.put(dealAccounting.getBokDetailId().getBooksetId(),bokBooksetExt);
	            	}
	            	if(bokBooksetExt==null || "01".equals(bokBooksetExt.getParamValue())){
	            		detailSubjectNoTail  = targetFinProduct==null?null:targetFinProduct.getFinprodId();
						detailSubnameTail = targetFinProduct==null?null:targetFinProduct.getFinprodAbbr();
	            	}
	            	else{
	            		detailSubjectNoTail  = targetFinProduct==null?null:targetFinProduct.getTermType();
						detailSubnameTail = targetFinProduct == null ? null
								: InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_内部拆借.getValue(),
								targetFinProduct.getTermType());
	            	}
					
				} else {
					detailSubjectNoTail  = targetFinProduct==null?null:targetFinProduct.getTermType();
					detailSubnameTail = targetFinProduct == null ? null
							: InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_内部拆借.getValue(),
							targetFinProduct.getTermType());
				}
			}
			// 估值核算交易扩展
			TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto, valFinProductDeal,dealAccounting.getAcctSubjectType());
			listValFinProductDealExt.add(valFinProductDealExt);

			//四级科目尾号
			dealAccounting.getBokDetailId().setDetailSubjectNoTail(detailSubjectNoTail);
			//四级科目尾号名称
			dealAccounting.getBokDetailId().setDetailSubnameTail(detailSubnameTail);


			listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            listValBokDetailId.add(dealAccounting.getBokDetailId());
			
			//结算指令-同业借款中间收息
            Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
            		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
            if(map.get("ORDERS")!=null){
            	listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
            }
            if(map.get("ORDERS_EXT")!=null){
            	listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
            }
            if(map.get("ACCOUNTING")!=null){
           	 	listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
            }
            if(map.get("ACCOUNTING_DETAI")!=null){
            	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
            }
            if(map.get("BOK_DETAIL_ID")!=null){
            	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }
            
            /**add by wujie on 20190322 start reamrk:通道投资费用交易生成**/
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, dealAccounting.getAcctSubjectType());
            if(channelFeeInfoDeal != null){
                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(valFinProductDeal, channelFeeInfoDeal);
                
                if(feedealMap.get("TrdProductDeal")!=null){
                    listValFinProductDeal.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
                }
                
                if(feedealMap.get("TrdProductDealExt")!=null){
                    listValFinProductDealExt.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
                }
                
                if(feedealMap.get("BokDealAccounting_List")!=null){
                    listValDealAccounting.addAll((List<BokDealAccounting>)feedealMap.get("BokDealAccounting_List"));
                }
                
                if(feedealMap.get("BokDealAccountingDetail_List")!=null){
                    listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)feedealMap.get("BokDealAccountingDetail_List"));
                }
                
                if(feedealMap.get("BokDetailId_list")!=null){
                    listValBokDetailId.addAll((List<BokDetailId>)feedealMap.get("BokDetailId_list"));
                }
                
                if(feedealMap.get("StlOrders_List")!=null){
                    listValOrders.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                }
                if(feedealMap.get("StlOrdersExt_List")!=null){
                    listValOrdersExt.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                }
            }
            /**add by wujie on 20190322 end reamrk:通道投资费用交易生成**/
		}
		resultMap.put("ValFinProductDeal", listValFinProductDeal);
		resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
		resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
		resultMap.put("valOrdersList", listValOrders);
        resultMap.put("valOrdersExtList", listValOrdersExt); 
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
		return resultMap;
	}

	 /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValDlRateDeal dto,String acctSubjectType){
        //接口.通道代码为空
        if(dto.getChannelId() == null){
            return null;
        }
        //接口.通道费用空
        if(dto.getChlFeeAmt()==null || dto.getChlFeeAmt().compareTo(BigDecimal.ZERO)==0){
            return null;
        }
        ChannelFeeInfoDeal deal = new ChannelFeeInfoDeal();
        //通道费用 = 接口.通道费用
        deal.setChlFeeAmt(dto.getChlFeeAmt());
        //是否随交易支付 = 接口.通道费是否随交易支付
        deal.setIsPayWithTrade(dto.getIsChlFeeAmt());
        //通道代码 = 接口.通道代码
        deal.setChannelId(dto.getChannelId());
        //产品代码 = 接口.产品代码
        deal.setProdId(dto.getProdId());
        
        deal.setAcctSubjectId(dto.getProdId() + FinalString.LAYERING_SPLIT + dto.getChannelId());
        
        deal.setAcctSubjectType(acctSubjectType);
        
        return deal;
    }
	
	/**
	 * 接收 金融产品交易 T1 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @return
	 */
	private TrdProductDeal getValFinProductDeal(ValDlRateDeal dto,String finprodType) {
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		// 交易编号
		String tradeId = interfCommonService.getTradeid(DictCodeList.交易方向_收息到账.getValue(), dto.getDealDate(), "B");
		trdProductDeal.setTradeId(tradeId);
		// 业务类型 = 数据字典：业务类型.拆借交易
		trdProductDeal.setBusiType(DictCodeList.业务类型_拆借交易.getValue());
		// 金融产品代码 = B.拆借标的代码
		trdProductDeal.setFinprodId(dto.getDlId());
		/**add by wujie on 20180825 start**/
		// 金融产品类型  =
		trdProductDeal.setFinprodType(finprodType);
        /**add by wujie on 20180825 end**/
		// 分支序号 = 设置全局变量(默认为0）
		trdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
		// 交易日 = B.交易日
		trdProductDeal.setTradeDate(dto.getDealDate());
		// 起息日 = B.计息开始日
		trdProductDeal.setVdate(dto.getVdate());
		// 到期日 = B.计息结束日
		trdProductDeal.setMdate(dto.getMdate());
		// 交割日 = B.交割日
		trdProductDeal.setSettleDate(dto.getSettleDate());
		// 交易币种 = B.币种
		trdProductDeal.setCcy(dto.getCcy());
		// 利息总额 = B.实收利息
		trdProductDeal.setIntAmt(dto.getActInt());
		// 交易金额 = B.交易金额
		trdProductDeal.setTradeAmt(dto.getDealAmt());
		// 交易方向 = 
		trdProductDeal.setPs(DictCodeList.交易方向_收息到账.getValue());
		// 交易状态 = B.数据动作标志
		trdProductDeal.setTradeStatus(dto.getDataActionSign());
		// 是否结清 = B.是否结清
//		trdProductDeal.setIsClean(dto.getIsClean());
		trdProductDeal.setIsClean(dto.getIsClean2());
		// 关联系统交易编号 = B.成交编号
		trdProductDeal.setfTradeId(dto.getTradeId());
	      // 设置系统时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
		//20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(trdProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	trdProductDeal.setpTradeId(interfCommonService.getFTradeId(trdProductDeal));
        }
        //通道代码 = a.通道代码
        trdProductDeal.setChlId(dto.getChannelId());
		return trdProductDeal;
	}

	/**
	 * 估值核算交易扩展表T2 接收 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @param valFinProductDeal
	 * @return
	 */
	private TrdProductDealAdd getValFinProductDealExt(ValDlRateDeal dto, TrdProductDeal valFinProductDeal,String finprodType) {
		TrdProductDealAdd result = new TrdProductDealAdd();
		// 交易编号 = T1.交易编号
		result.setTradeId(valFinProductDeal.getTradeId());
		// 理财产品代码 = A.产品代码
		/** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        result.setProdId(dto.getProdId());
        **/
		result.setPortfolioId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	result.setPortfolioId(dto.getProdId());
//        }else{
//        	result.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
		// 是否有结算指令 = A.有无清算指令
//		result.setHasOrder(dto.getIsClean());
		// 生成方式
		result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
		return result;
	}
	
//	/**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     * add by wujie on 20180828
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String bookSetId,String acctSubjectType) {
//        //交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setEffDate(valFinProductDeal.getSettleDate());//交割日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(prodId); // * 金融产品代码
//        /**modify by wujie on 20190107 start
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId));
//        **/
//        productDealDto.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(prodId); //  * 理财产品代码
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//        productDealDto.setBookNo(bookSetId);
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        
//        /** modify by wujie on 20181229 start
//        String insType = DictCodeListExt1.指令类型_拆分同业_中间收息.getValue();
//        **/
//        String insType = DictCodeListExt1.指令类型_资金收款_同业借款_中间收息.getValue();
//        /**modify by wujie on 20181229 end**/
//        return interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//    }

}
