/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.repdeal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotAstTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSec;


/**
 * 交易信息-回购交易 模板处理Service
 * 
 * @author chufengjie
 * @version 1.0 2018年8月7日
 * @see RepurchaseDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepurchaseDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 交易信息-回购交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 交易信息-回购交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RepurchaseDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                     TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        RepurchaseDealInfoValCommonResult result = new RepurchaseDealInfoValCommonResult();
        List<ValRepDealSec> listValBotAstTrade = new ArrayList<ValRepDealSec>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValBotAstTrade = returnResult.getT(ValBotAstTrade.class);
        result.setDatanum(listValBotAstTrade==null?0:listValBotAstTrade.size());
        if (listValBotAstTrade == null) {
        	listValBotAstTrade = new ArrayList<ValRepDealSec>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValBotAstTrade(listValBotAstTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

        if(map != null){
        	// 金融产品交易List
            List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
            result.setListValFinProductDeal(listValFinProductDeal);
            // 估值核算交易扩展表List
            List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
            result.setListValFinProductDealExt(listValFinProductDealExt);
            // 入账信息表List
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                "ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);
            // 账务明细表List
            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                "ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);
            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
            result.setListValTradeFeeDetail(listValTradeFeeDetail);
            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);

            // 结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-回购交易
     * 
     * @param listValBotAstTrade 回购交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValBotAstTrade(List<ValRepDealSec> listValBotAstTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();

        // 结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();

        List<ValInterflog> valInterflogList =  new ArrayList<>();

        for (ValRepDealSec dto : listValBotAstTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);
            FinTradeProduct finTradeProduct = interfCommonService.queryFinTradeProduct(dto.getpTradeId());
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto,finTradeProduct);
            listValFinProductDeal.add(valFinProductDeal);
            // 交易费用明细表
            List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(dto,valFinProductDeal);
            listValTradeFeeDetail.addAll(listfeeDetail);

            // 入账信息表 账务明细表 账务明细代码表 调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(
                valFinProductDeal, dto.getProdId());
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            
            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal,dealAccounting.getAcctSubjectType());
            valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            //四级科目尾号
            dealAccounting.getBokDetailId().setDetailSubjectNoTail(finTradeProduct==null?null:finTradeProduct.getTermType());
            //四级科目尾号名称 
			dealAccounting.getBokDetailId()
					.setDetailSubnameTail(finTradeProduct == null ? null
							/**modify by wujie on 20210506 start
                             : InterfHelper.getDictColdeList_ItemName(DictCode.期限品种_回购.getValue(),**/
							: InterfHelper.getDictColdeList_ItemName(DictCode.回购期限品种.getValue(),
                            /**modify by wujie on 20210506 end**/
									finTradeProduct.getTermType()));
            listValBokDetailId.add(dealAccounting.getBokDetailId());
            
            StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
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
            /**modify by wujie on 20190118 end**/
            
            
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
                
                if(feedealMap.get("TgTradeFeeDetail")!=null){
                	listValTradeFeeDetail.add((TrdTradeFeeDetail)feedealMap.get("TgTradeFeeDetail"));
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
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }
    
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValRepDealSec dto, TrdProductDeal valFinProductDeal){
    	StlOrders result = new StlOrders();
    	//modify by yanya on 20210204 判定为空包含空字符
    	//if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getFundSettleDate());
    	result.setSettleAmt(valFinProductDeal.getTradeAmt());
    	return result;
    }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValRepDealSec dto,String acctSubjectType){
    	//接口.通道代码为空
    	if(dto.getChlId() == null){
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
    	deal.setChannelId(dto.getChlId());
    	//产品代码 = 接口.产品代码
    	deal.setProdId(dto.getProdId());
    	
    	deal.setAcctSubjectId(dto.getProdId() + FinalString.LAYERING_SPLIT + dto.getChlId());
    	
    	deal.setAcctSubjectType(acctSubjectType);
    	
    	return deal;
    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-回购交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValRepDealSec dto,FinTradeProduct finTradeProduct) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 = 自动生成
        //String tradeId = interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(), "A");
        /**由于债券交易不自动生成结算指令，需要根据上游交易编号来关联指令，交易编号直接取上游交易编号 update by yinya on 20211207**/
        String tradeId=null;
        if(dto.getDataActionSign().equals(DictCodeListExt1.交易状态_初始.getValue())){
            tradeId=dto.getTradeId();
        }else{
            tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        }
        valFinProductDeal.setTradeId(tradeId);
        // 业务类型 = A.业务类型
        valFinProductDeal.setBusiType(dto.getBusiType());
        // 金融产品代码 = A.原交易编号
        valFinProductDeal.setFinprodId(dto.getpTradeId());
        // 金融产品类型e
        valFinProductDeal.setFinprodType(finTradeProduct.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getTradeDate());
        // 起息日 = A.起息日
        valFinProductDeal.setVdate(dto.getVdate());
        // 到期日 = A.到期日
        valFinProductDeal.setMdate(dto.getMdate());
        // 交割日 = A.交割日
        valFinProductDeal.setSettleDate(dto.getSettleDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());

        // 接口添加 资金账户 = A.资金账户 yangchen 20220325 start
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());
        // 接口添加 资金账户 = A.资金账户 yangchen 20220325 end
        // 单位净价
        if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
            && (DictCodeList.业务类型_债券买断式回购交易.getValue().equals(dto.getBusiType()))) {
            valFinProductDeal.setUnitCprice(dto.getCprice());
        }
        else if ((DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                  || DictCodeList.交易方向_到期.getValue().equals(dto.getPs())
                  || DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(dto.getPs())
                  || DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(dto.getPs())
                  || DictCodeList.交易方向_到期续做.getValue().equals(dto.getPs()))
                 && (DictCodeList.业务类型_债券买断式回购交易.getValue().equals(dto.getBusiType()))) {
            valFinProductDeal.setUnitCprice(dto.getMcprice());
        }
        // 单位全价
        if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
            && (DictCodeList.业务类型_债券买断式回购交易.getValue().equals(dto.getBusiType()))) {
            valFinProductDeal.setUnitFprice(dto.getFprice());
        }
        else if ((DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
                || DictCodeList.交易方向_到期.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(dto.getPs())
                || DictCodeList.交易方向_到期续做.getValue().equals(dto.getPs()))
                 && (DictCodeList.业务类型_债券买断式回购交易.getValue().equals(dto.getBusiType()))) {
            valFinProductDeal.setUnitFprice(dto.getMfprice());
        }
        //净价总额 = A.首次结算金额
        valFinProductDeal.setCpriceAmt(dto.getFirstAmt());
        //交易份额 = A.首次结算金额
        valFinProductDeal.setShareAmt(dto.getFirstAmt());
        //全价总额 = A.首次结算金额+A.利息金额
        valFinProductDeal.setFpriceAmt(dto.getFirstAmt().add(dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt()));
        
        // 交易本金 = A.首次结算金额
        valFinProductDeal.setPrinAmt(dto.getFirstAmt());
        // 利息总额 = A.利息金额
        valFinProductDeal.setIntAmt(dto.getIntAmt());
        
        //随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        
        //未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        
        //总交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        
        //印花税
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStampDuty())){
        	feeAmtPay = feeAmtPay.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        }
        feeAmt = feeAmt.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        
        //经手费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsBrokerage())){
        	feeAmtPay = feeAmtPay.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        }
        feeAmt = feeAmt.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        
        //证管费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsCertfFee())){
        	feeAmtPay = feeAmtPay.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        }
        feeAmt = feeAmt.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        
        //结算费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStlAmt())){
        	feeAmtPay = feeAmtPay.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        }
        feeAmt = feeAmt.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        
        //佣金
        if(DictCodeList.是否_是.getValue().equals(dto.getIsCommission())){
        	feeAmtPay = feeAmtPay.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        }
        feeAmt = feeAmt.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        
        //交易手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
        	feeAmtPay = feeAmtPay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }
        feeAmt = feeAmt.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        
        //结算手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStlFeeAmt())){
        	feeAmtPay = feeAmtPay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }
        feeAmt = feeAmt.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        
        /**add by wujie on 20190322 start**/
        if(dto.getChlId() == null){
        /**add by wujie on 20190322 end**/
        	//通道费
            if(DictCodeList.是否_是.getValue().equals(dto.getIsChlFeeAmt())){
            	feeAmtPay = feeAmtPay.add(dto.getChlFeeAmt()==null?BigDecimal.ZERO:dto.getChlFeeAmt());
            }else{
            	feeAmtUnpay = feeAmtUnpay.add(dto.getChlFeeAmt()==null?BigDecimal.ZERO:dto.getChlFeeAmt());
            }
            feeAmt = feeAmt.add(dto.getChlFeeAmt()==null?BigDecimal.ZERO:dto.getChlFeeAmt());
        /**add by wujie on 20190322 start**/
        }
        /**add by wujie on 20190322 end**/
        
        //交易费用
        if(DictCodeList.是否_是.getValue().equals(dto.getIsTradeFee())){
        	feeAmtPay = feeAmtPay.add(dto.getTradeFee()==null?BigDecimal.ZERO:dto.getTradeFee());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getTradeFee()==null?BigDecimal.ZERO:dto.getTradeFee());
        }
        feeAmt = feeAmt.add(dto.getTradeFee()==null?BigDecimal.ZERO:dto.getTradeFee());
        
        //随交易支付费用
        valFinProductDeal.setFeeAmtPay(feeAmtPay);
        
        //未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(feeAmtUnpay);
        
        //总交易费用
        valFinProductDeal.setFeeAmt(feeAmt);
        
	      /***
	      * 正回购 买入时：交易金额 = A.首次结算金额  + A.利息金额- 随交易支付费用
	      * 逆回购买入时： 交易金额 = A.首次结算金额 + A.利息金额+ 随交易支付费用
	      * 
	      * 正回购 卖出、到期时：交易金额 = A.到期结算金额 + 随交易支付费用
	      * 逆回购 卖出、到期时：交易金额 = A.到期结算金额 - 随交易支付费用
	      */
	     if(DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
	             || DictCodeList.交易方向_到期.getValue().equals(dto.getPs())
                 /**add by yanya on 20191112 增加提前终止、逾期返售 start**/
                 || DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(dto.getPs())
                 || DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(dto.getPs())
                 /**add by yanya on 20191112 增加提前终止、逾期返售 end**/
                 || DictCodeList.交易方向_到期续做.getValue().equals(dto.getPs())
         ){
	     	BigDecimal cashAmt = dto.getEndAmt()==null?BigDecimal.ZERO:dto.getEndAmt();
	     	if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(valFinProductDeal.getFinprodType())
	         		|| DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(valFinProductDeal.getFinprodType())){
	     		cashAmt = cashAmt.add(feeAmtPay);	
	     	}else{
	     		cashAmt = cashAmt.subtract(feeAmtPay);
	     	}       	
	     	valFinProductDeal.setTradeAmt(cashAmt);
	     }else{
	     	BigDecimal cashAmt = dto.getFirstAmt()==null? BigDecimal.ZERO:dto.getFirstAmt();
	     	cashAmt = cashAmt.add(dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt());
	     	if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(valFinProductDeal.getFinprodType())
	     		|| DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(valFinProductDeal.getFinprodType())){
	     		cashAmt = cashAmt.subtract(feeAmtPay);
	     	}else{
	     		cashAmt = cashAmt.add(feeAmtPay);
	     	}
	     	valFinProductDeal.setTradeAmt(cashAmt);
	     }
        /**add by wujie on 20180925 end**/
        
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 通道代码 =A.通道代码
        valFinProductDeal.setChlId(dto.getChlId());
        // 交易对手 = A.交易对手
        valFinProductDeal.setCounterId(dto.getCounterId());
        // 交易对手类型 = A.交易对手类型
//        valFinProductDeal.setCounterType(dto.getCounterType());
        // 交易场所 = A.交易场所
        valFinProductDeal.setTradeMarket(dto.getTradeMarket());
        // 交易员 = A.交易员
        valFinProductDeal.setTrader(dto.getTrader());
        // 关联系统交易编号 = A.成交编号
//        valFinProductDeal.setfTradeId(dto.getTradeId());
        // 接口添加 关联系统交易编号 = A.外部交易编号 yangchen 20220325 start
        valFinProductDeal.setfTradeId(dto.getfTradeId());
        // 接口添加 关联系统交易编号 = A.外部交易编号 yangchen 20220325 end
        //交易市场 = 数据字典：流通市场.二级市场
        valFinProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        /**add by wujie on 20190118 start remark:登记托管机构 = A.登记托管机构**/
        valFinProductDeal.setRegistOrg(dto.getRegistOrg());
        /**add by wujie on 20190118 end**/

        /**add by wujie on 20190420 start**/
        //交付日市场 = A.交收日
        valFinProductDeal.setPayDate(dto.getPaydate());
        /**add by wujie on 20190420 end**/


        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        //是否生成结算指令 add by yinya on 20211201
        valFinProductDeal.setIsOrders(dto.getIsClean());
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 01-回购交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValRepDealSec dto, TrdProductDeal t1,String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        valFinProductDealExt.setProdId(dto.getProdId());
        **/
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
////        	valFinProductDealExt.setProdId(dto.getProdId());
//        }
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = A.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 创建时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        // 生成方式
        valFinProductDealExt.setGenType(
            com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }

    /**
     * T3: 交易费用明细表 接收
     * 
     * @param dto 01-回购交易
     * @param valFinProductDeal
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValRepDealSec dto,TrdProductDeal valFinProductDeal) {
    	List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
        Map<String,String> mapIsPay = new HashMap<String,String>();
        //经手费
        map.put(DictCodeListExt1.一次性费用类型_经手费.getValue(), dto.getBrokerage());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_经手费.getValue(), dto.getIsBrokerage());
        
        //印花税
        map.put(DictCodeListExt1.一次性费用类型_印花税.getValue(), dto.getStampDuty());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_印花税.getValue(), dto.getIsStampDuty());
        
        //证管费
        map.put(DictCodeListExt1.一次性费用类型_证管费.getValue(), dto.getCertfFee());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_证管费.getValue(), dto.getIsCertfFee());
        
        //结算费
        map.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getStlAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getIsStlAmt());
        
        //佣金
        map.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getCommission());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getIsCommission());
        
        //交易手续费
        map.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getDealFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getIsDealFeeAmt());
        
        //结算手续费
        map.put(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), dto.getStlFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), dto.getIsStlFeeAmt());
        
        /**add by wujie on 20190322 start**/
        if(dto.getChlId() ==null){
        /**add by wujie on 20190322 end**/
	        //通道费
	        map.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getChlFeeAmt());
	        mapIsPay.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getIsChlFeeAmt());
	    /**add by wujie on 20190322 start**/
        }
        /**add by wujie on 20190322 end**/
        
        //交易费用
        map.put(DictCodeListExt1.一次性费用类型_其他费用.getValue(), dto.getTradeFee());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_其他费用.getValue(), dto.getIsTradeFee());
               
        for (String key : map.keySet()) {
        	BigDecimal amt = map.get(key);
            if(amt==null || BigDecimal.ZERO.compareTo(amt)==0){
        		continue;
        	}
        	TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
            valTradeFeeDetail.setFeeType(key);
            // 是否随交易支付 = 固定值：Y 是 默认随交易支付
            valTradeFeeDetail.setIsPayWithTrade(mapIsPay.get(key));
            // 对应金额
            valTradeFeeDetail.setFeeAmt(amt);
            valTradeFeeDetail.setFeeId(key);
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            listValTradeFeeDetail.add(valTradeFeeDetail);
        }
        
        return listValTradeFeeDetail;
    }

//    /**
//     * 结算指令 <一句话功能简述> <功能详细描述>
//     * 
//     * @param dto
//     * @param valFinProductDeal
//     * @param valBokDetailId
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private ProductDealDto getProductDealDto(ValRepDealSec dto, TrdProductDeal valFinProductDeal,
//                                             BokDetailId valBokDetailId,String acctSubjectType) {
//        ProductDealDto result = new ProductDealDto();
//        //交易状态
//        result.setTradeStatus(valFinProductDeal.getTradeStatus());
//
//        //交易状态
//        result.setTradeStatus(valFinProductDeal.getTradeStatus());
//        // 交易编号
//        result.setTradeId(valFinProductDeal.getTradeId());
//        // 交易日
//        result.setTradeDate(valFinProductDeal.getSettleDate());
//        // 交易方向
//        result.setPs(valFinProductDeal.getPs());
//        // 交易金额
//        result.setTardeAmt(valFinProductDeal.getTradeAmt());
//        // 金融产品代码
//        result.setFinprodId(dto.getProdId());
//        // 金融产品类型
//        /**modify by wujie on 20190107 start
//        result.setFinprodType(interfCommonService.queryProductType(prodId));
//        **/
//        result.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        result.setBusiType(dto.getBusiType());
//        // 理财产品代码
//        result.setProdId(dto.getProdId());
//        // 币种
//        result.setCcy(valFinProductDeal.getCcy());
//        // 是否随交易出账
//        result.setIsTrade(DictCodeList.是否_是.getValue());
//        // 账套代码
//        result.setBookNo(valBokDetailId.getBooksetId());
//        // 交割日
//        result.setEffDate(valFinProductDeal.getSettleDate());
//        // 创建时间
//        result.setCreateTime(dto.getCreateTime());
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        result.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        
//        /**add by chendijun  2018/11/20 start**/
//        //交易场所
//        result.setTradeMarket(valFinProductDeal.getTradeMarket());
//        //交易市场
//        result.setTradeMarketMode(valFinProductDeal.getTradeMarketMode());
//        /**add by chendijun  2018/11/20 end**/
//        return result;
//    }
}
