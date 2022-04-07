/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestAssetDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券买卖交易 模板处理Service(VAL_SEC_VEST_ASSET_DEAL)
 * 
 * @author weipeng
 * @version 1.0 2018年6月27日
 * @see SecVestAssetDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecVestAssetDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    /**
     * 接口共通Service
     */
    @Autowired
    private InterfCommonService interfCommonService;
    
    /**
     * 债券买卖交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券买卖交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSecVestAssetDeal> listValSecVestAssetDeal = new ArrayList<ValSecVestAssetDeal>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        
        listValSecVestAssetDeal = returnResult.getT(ValSecVestAssetDeal.class);
        //获取数据数量
        result.setDataNum(listValSecVestAssetDeal.size());
        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSecVestAssetDeal(listValSecVestAssetDeal,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
            "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
            "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //金融产品实物交易
        List<TrdFinProductBarter> listTrdFinProductBarter = (List<TrdFinProductBarter>)map.get(
                "ValFinProductBarter");
        result.setListValFinProductBarter(listTrdFinProductBarter);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
            "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
            "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        /**add by wujie on 20180712 start**/
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        /**add by wujie on 20180712 end**/
        
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
            "ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-买卖交易
     * 
     * @param listValSecVestAssetDeal 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> handleValSecVestAssetDeal(List<ValSecVestAssetDeal> listValSecVestAssetDeal,Date resovleDate,Map<String,Object> params,
			List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 金融产品实物交易List
        List<TrdFinProductBarter> listValFinProductBarter = new ArrayList<TrdFinProductBarter>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
         //结算指令list
        List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
        //结算指令拓展list
        List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValSecVestAssetDeal dto : listValSecVestAssetDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            dto.setTradeId(valFinProductDeal.getTradeId());
           
            // 入账信息表   账务明细表、账务明细代码表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            
            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal,dealAccounting.getAcctSubjectType());
            listValFinProductDealExt.add(valFinProductDealExt);

            //交易方向为“债转股”时才入数据
            if(DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())){
                // 金融产品实物交易
                TrdFinProductBarter trdFinProductBarter = this.getTrdFinProductBarter(dto, valFinProductDeal);
                listValFinProductBarter.add(trdFinProductBarter);
                
                
                //股票的账务明细代码生成
                BokDetailId valBokDetailId_dsf = new BokDetailId();
           	    valBokDetailId_dsf.setBooksetId(dealAccounting.getBokDetailId().getBooksetId());
	           	valBokDetailId_dsf.setBusiId(trdFinProductBarter.getFinprodId());
	           	valBokDetailId_dsf.setBusiDetailType(trdFinProductBarter.getFinprodType());
	           	valBokDetailId_dsf.setTradeId(valFinProductDeal.getTradeId());
	           	valBokDetailId_dsf.setCcy(valFinProductDeal.getCcy());
	           	valBokDetailId_dsf.setBokDetailId(interfCommonService.genMd5BokDetailId(valBokDetailId_dsf));
	           	 
	            BokDetailId dsf = interfCommonService.settingDetailSubjectNoTaiL(trdFinProductBarter.getFinprodId(), trdFinProductBarter.getFinprodType(), null, valFinProductDeal.getBusiType(),valBokDetailId_dsf.getBooksetId());
	       	    valBokDetailId_dsf.setDetailSubnameTail(dsf.getDetailSubnameTail());
	       		valBokDetailId_dsf.setDetailSubjectNoTail(dsf.getDetailSubjectNoTail()==null?dsf.getDetailSubnameTail():dsf.getDetailSubjectNoTail());
	       		listValBokDetailId.add(valBokDetailId_dsf);
                
            }

            BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
            
            //四级科目尾号
            valBokDetailId.setDetailSubjectNoTail(dto.getAssetId());
            
            //四级科目尾号名称
            valBokDetailId.setDetailSubnameTail(dto.getSecAbbr());
            
            listValBokDetailId.add(valBokDetailId);
            
            // 交易费用明细表
            List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(dto,valFinProductDeal);
            listValTradeFeeDetail.addAll(listfeeDetail);
            
            //结算指令
            StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
            		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
            if(map.get("ORDERS")!=null){
            	valOrdersList.addAll((List<StlOrders>)map.get("ORDERS"));
            }
            if(map.get("ORDERS_EXT")!=null){
            	valOrdersExtList.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
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
                    valOrdersList.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                }
                if(feedealMap.get("StlOrdersExt_List")!=null){
                    valOrdersExtList.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                }
            }
        }
        resultMap.put("valOrdersList", valOrdersList);
        resultMap.put("valOrdersExtList", valOrdersExtList);
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValFinProductBarter", listValFinProductBarter);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }
    
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSecVestAssetDeal dto, TrdProductDeal valFinProductDeal){
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
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSecVestAssetDeal dto,String acctSubjectType){
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
     * @param dto 01-买卖交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValSecVestAssetDeal dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //定义编号
        //String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        /**由于债券交易不自动生成结算指令，需要根据上游交易编号来关联指令，交易编号直接取上游交易编号 update by yinya on 20211207**/
        String tradeId=null;
        if(dto.getDataActionSign().equals(DictCodeListExt1.交易状态_初始.getValue())){
            tradeId=dto.getTradeId();
        }else{
            tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        }

        // 交易编号 = 共通生成
        valFinProductDeal.setTradeId(tradeId);
        // 交易场所为银行间则业务类型为债券交易，反之为交易场所债券
	    valFinProductDeal.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F01 债券
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.交割日
        valFinProductDeal.setSettleDate(dto.getSettdateDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价 = A.净价
        valFinProductDeal.setUnitCprice(dto.getCprice());
        // 单位利息 = A.应收利息
        valFinProductDeal.setUnitInt(dto.getUnitInt());
        // 单位全价 = A.全价
        valFinProductDeal.setUnitFprice(dto.getFprice());
        // 净价总额 = A.净价总额
        valFinProductDeal.setCpriceAmt(dto.getCpriceAmt());
        //全价总额 = A.全价总额
        valFinProductDeal.setFpriceAmt(dto.getFpriceAmt());
        //未随交易支付费用 = A.未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(dto.getFeeAmtUnpay());
        //单位面值 = A.单位面值
        valFinProductDeal.setParValue(dto.getParValue());
        //到期收益率 = A.到期收益率
        valFinProductDeal.setYield(dto.getYield());
        //行权收益率 = A.行权收益率
        valFinProductDeal.setExerYield(dto.getExeYield());
        //通道代码 = A.通道代码
        valFinProductDeal.setChlId(dto.getChlId());
        //交易对手 = A.交易对手
        valFinProductDeal.setCounterId(dto.getCounterId());
        //交易员 = A.交易员
        valFinProductDeal.setTrader(dto.getTrader());
        //证券托管户 = A.证券托管户
        valFinProductDeal.setSecManageAcctId(dto.getSecManageAcctId());
        // 交易份额 = A.成交数量/100
        valFinProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getDealNum(), new BigDecimal(100),2));
        // 交易本金 = A.净价总额
        valFinProductDeal.setPrinAmt(dto.getCpriceAmt());
        // 利息总额 = A.应收利息总额
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
        
        if (dto.getChlId() == null) {
            // 通道费
            if (DictCodeList.是否_是.getValue().equals(dto.getIsChlFeeAmt())) {
                feeAmtPay = feeAmtPay.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
            }
            else {
                feeAmtUnpay = feeAmtUnpay.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
            }
            feeAmt = feeAmt.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
        }
        
        //随交易支付费用
        valFinProductDeal.setFeeAmtPay(feeAmtPay);
        
        //未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(feeAmtUnpay);
        
        //总交易费用
        valFinProductDeal.setFeeAmt(feeAmt);
        
        if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())
        		|| DictCodeList.交易方向_申购.getValue().equals(dto.getPs())){
        	valFinProductDeal.setTradeAmt(ArithmeticUtil.doAdd(2, null,dto.getFpriceAmt(),feeAmtPay));
        }else if(DictCodeList.交易方向_申购确认.getValue().equals(dto.getPs())){
        	BigDecimal tradeAmt = dto.getTradeAmt()==null?BigDecimal.ZERO:dto.getTradeAmt();
        	valFinProductDeal.setTradeAmt(ArithmeticUtil.doAdd(2, null,tradeAmt,feeAmtPay));
        }else if(DictCodeListExt1.交易方向_债转股.getValue().equals(dto.getPs())){
        	valFinProductDeal.setTradeAmt(ArithmeticUtil.subtract(dto.getTradeAmt(),feeAmtPay));
        }
        else{
        	valFinProductDeal.setTradeAmt(ArithmeticUtil.subtract(dto.getFpriceAmt(),feeAmtPay));
        }
        
        /**add by wujie on 20180925 end**/
        
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 投资目的 = A.投资目的
        valFinProductDeal.setInvAim(dto.getInvestAim());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所 = A.交易场所
        valFinProductDeal.setTradeMarket(dto.getTradeMarket());
        // 交易市场 = A.流通市场
        valFinProductDeal.setTradeMarketMode(dto.getSharesTradeMarket());
        /*modify by sunshuaishuai 交易管理 20220325 start*/
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getOutTradeId());
        /*modify by sunshuaishuai 交易管理 20220325 end*/
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        //是否生成结算指令 add by yinya on 20211201
        valFinProductDeal.setIsOrders(dto.getIsClean());

        /*add by sunshuaishuai 交易管理 20220406 start*/
        //资金账户
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220406 end*/
        return valFinProductDeal;
    }

    /** 
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 01-买卖交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValSecVestAssetDeal dto,
                                                         TrdProductDeal t1,String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        // 保证金 = A.保证金
        valFinProductDealExt.setDepositAmt(dto.getBondAmt());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }
    /**
     * T10:金融产品实物交易 接收
     *
     * @param dto 01-买卖交易
     * @return 金融产品实物交易表
     * @see
     * @since 1.0
     */
    private TrdFinProductBarter getTrdFinProductBarter(ValSecVestAssetDeal dto, TrdProductDeal t1) {
        // 金融产品实物交易表
        TrdFinProductBarter trdFinProductBarter = new TrdFinProductBarter();
        // 交易编号 = T1.交易编号
        trdFinProductBarter.setTradeId(t1.getTradeId());
        // 金融产品代码 = A.正股代码
        trdFinProductBarter.setFinprodId(dto.getSharesCode());
        //金融产品类型 = A.正股类型
        trdFinProductBarter.setFinprodType(dto.getSharesType());
        //分支序号    = 设置全局变量(默认为0）
        trdFinProductBarter.setBranch(0L);
        //交易份额 =  A.转股数
        trdFinProductBarter.setShareAmt(dto.getSharesNum());
        //交易本金 = A.正股交易本金
        trdFinProductBarter.setPrinAmt(dto.getExpConValue());
        // 创建时间 = 系统时间
        trdFinProductBarter.setCreateTime(dto.getCreateTime());
        return trdFinProductBarter;
    }

    /**
     * T5: 交易费用明细表 接收
     * 
     * @param dto 01-买卖交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValSecVestAssetDeal dto, TrdProductDeal valFinProductDeal) {
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
        
        /** add by wujie on 20190322 start **/
        if (dto.getChlId() == null) {
            /** add by wujie on 20190322 end **/
            // 通道费
            map.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getChlFeeAmt());
            mapIsPay.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getIsChlFeeAmt());
            /** add by wujie on 20190322 start **/
        }
        /** add by wujie on 20190322 end **/
        
        
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

}
