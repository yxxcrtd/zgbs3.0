/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.depositasset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.joyintech.framework.util.StringUtil;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotAstTrade;

/**
 * 存款类资产-存款交易 模板处理Service(VAL_BOT_AST_INFO)
 * 
 * @author weipeng
 * @version 1.0 2018年6月27日
 * @see DepositeAssetAstDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DepositeAssetAstDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private  InterfCommonService interfCommonService;
    /**
     * 存款类资产-存款交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 存款类资产交易信息 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        Map<String,Object> params = acctEntity.getParams();
        List<ValBotAstTrade> listValBotAstTrade = new ArrayList<ValBotAstTrade>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValBotAstTrade = returnResult.getT(ValBotAstTrade.class);
        //获取数据数量
        result.setDataNum(listValBotAstTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValBotAstTrade(listValBotAstTrade,acctEntity,params,returnResult.getValInterflog());

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
        //账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        
        //结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get(
            "listValOrders");
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
            "listValOrdersExt");
        result.setListValOrders(listValOrders);
        result.setListValOrdersExt(listValOrdersExt);
        
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }
    /**
     * 01-存款交易
     * 
     * @param listValBotAstTrade 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> handleValBotAstTrade(List<ValBotAstTrade> listValBotAstTrade,TemplateOrderInfoReaderResult acctEntity,Map<String,Object> params,
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
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
        
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValBotAstTrade dto : listValBotAstTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(acctEntity.getResovleDate());
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            // 入账信息表   账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            
            String finprodType = dealAccounting.getAcctSubjectType();
            
            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal,finprodType);
            valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            if(StringUtil.isEmpty(acctEntity.getFourLevelSubjectKg())
            		|| DictCodeList.是否_是.getValue().equals(acctEntity.getFourLevelSubjectKg())){
            	//四级科目号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(valFinProductDeal.getFinprodId());
                TargetFinProduct targetFinProduct = interfCommonService.getTargetFinProduct(valFinProductDeal.getFinprodId());
                //四级科目尾号名称 
                dealAccounting.getBokDetailId().setDetailSubnameTail(targetFinProduct==null?null:targetFinProduct.getFinprodName());
            }
            
            listValBokDetailId.add(dealAccounting.getBokDetailId());
            
            // 交易费用明细表
            dto.setTradeId(valFinProductDeal.getTradeId());
            TrdTradeFeeDetail valTradeFeeDetail = this.getValTradeFeeDetail(dto,valFinProductDeal);
            listValTradeFeeDetail.add(valTradeFeeDetail);
            
            //结算指令
            /** modify by wujie on 20190118 start
            BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
            Map<String, Object> map = this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
            listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
            listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
            
            List<BokDealAccounting> bokDealAccountinglist = (List<BokDealAccounting>)map.get("ValDealAccounting");
            if(bokDealAccountinglist!=null && bokDealAccountinglist.size()>0){
            	listValDealAccounting.addAll(bokDealAccountinglist);
            }
            
            List<BokDealAccountingDetail> bokDealAccountingDetaillist = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
            if(bokDealAccountingDetaillist != null && bokDealAccountingDetaillist.size()>0){
            	listValDealAccountingDetail.addAll(bokDealAccountingDetaillist);
            }
            
            List<BokDetailId> bokDetailIdlist = (List<BokDetailId>)map.get("ValBokDetailId");
            if(bokDetailIdlist != null && bokDetailIdlist.size()>0){
            	listValBokDetailId.addAll(bokDetailIdlist);
            }
            **/
            Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
            		dto.getProdId(), finprodType,null);
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
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValBotAstTrade dto,String acctSubjectType){
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
    
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String bookSetId,String acctSubjectType) {
//    	Map<String, Object> map = new HashMap<>();
//    	List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//    	List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//    	List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
//    	// 入账信息表List
//    	List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
//    	// 账务明细表List
//    	List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
//          
//    	//交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态 
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setEffDate(valFinProductDeal.getSettleDate()); //  交割日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(prodId); // * 金融产品代码
//        
//        /**modify by wujie on 20190107 start
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId)); 
//        **/
//        productDealDto.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(prodId); //  * 理财产品代码
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//        productDealDto.setBookNo(bookSetId);
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        String insType = "";
//        if(DictCodeList.交易方向_买入.getValue().equals(valFinProductDeal.getPs())){
//        	insType=  DictCodeListExt1.指令类型_存放同业_一般款项_买入.getValue();
//        	map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//        	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//            insType=  DictCodeListExt1.指令类型_资金收款_存放同业_起息.getValue();
//            map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//        	 ValOrders=(StlOrders)map.get("ORDERS");
//             ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//            if(map.get("ACCOUNTING")!=null){
//                listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//            }
//            if(map.get("ACCOUNTING_DETAI")!=null){
//                listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//            }
//            if(map.get("BOK_DETAIL_ID")!=null){
//                listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//            }
//            
//        }else if(DictCodeList.交易方向_卖出.getValue().equals(valFinProductDeal.getPs())){
//        	insType=  DictCodeListExt1.指令类型_存放同业_一般款项_到期卖出.getValue();
//        	map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//        	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//            insType=  DictCodeListExt1.指令类型_资金收款_存放同业_卖出.getValue();
//            map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//            ValOrders=(StlOrders)map.get("ORDERS");
//            ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//            if(map.get("ACCOUNTING")!=null){
//                listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//            }
//            if(map.get("ACCOUNTING_DETAI")!=null){
//                listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//            }
//            if(map.get("BOK_DETAIL_ID")!=null){
//                listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//            }
//            
//        }
//        map.put("ORDERS", listValOrders);
//        map.put("ORDERS_EXT", listValOrdersExt);
//        map.put("ValDealAccounting", listValDealAccounting);
//        map.put("ValDealAccountingDetail", listValDealAccountingDetail);
//        map.put("ValBokDetailId", listValBokDetailId);
//        
//        return map;
//    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-存款交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValBotAstTrade dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //交易编号 =共通生成
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        valFinProductDeal.setTradeId(tradeId);
        // 业务类型 = 03 存款类资产交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_存款类资产交易.getValue());
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F07 存款
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_存款.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        /** modify by dongjun on 20181105 start **/
        // 交割日 = A.交割日 == null? A.交易日 : A.交割日
        valFinProductDeal.setSettleDate(dto.getSettDate() == null ? dto.getDealDate() : dto.getSettDate());
        /** modify by dongjun on 20181105 end **/
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        
        //净价总额 = A.交易本金
        valFinProductDeal.setCpriceAmt(dto.getTradeCapi());
        //交易份额 = A.交易本金
        valFinProductDeal.setShareAmt(dto.getTradeCapi());
        //全价总额 = A.交易本金+A.利息金额
        valFinProductDeal.setFpriceAmt(dto.getTradeCapi().add(dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt()));
        
        // 交易本金 = A.交易本金
        valFinProductDeal.setPrinAmt(dto.getTradeCapi());
        // 利息总额 = A.应收利息
        valFinProductDeal.setIntAmt(dto.getIntAmt());
        // 随交易支付费用 = A.交易费用
        valFinProductDeal.setFeeAmtPay(dto.getTrdFee());
        // 交易金额 = A.交易金额
        valFinProductDeal.setTradeAmt(dto.getDealAmt());
        // 总交易费用 = A.交易费用
        valFinProductDeal.setFeeAmt(dto.getTrdFee());
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易对手 = A.交易对手
        valFinProductDeal.setCounterId(dto.getCounterId());
        //接口添加 资金账户=A.资金账户  yangchen 20220324
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());


//        // 交易对手类型 = 根据交易对手到客户信息表中查询获取
//        MstCustomerInfo valCustomerInfo = valCustomerInfoMapper.selectByPrimaryKey(
//            dto.getCounterId());
//        if (valCustomerInfo != null) {
//            valFinProductDeal.setCounterType(valCustomerInfo.getCustomerType());
//        }
//        else {
//            valFinProductDeal.setCounterType(null);
//        }
//        // 关联系统交易编号 = A.成交编号
////        valFinProductDeal.setfTradeId(dto.getTradeId());

        //接口修改 外部交易编号=A.外部交易编号  yangchen 20220324 start
        valFinProductDeal.setfTradeId(dto.getfTradeId());
        //接口修改 外部交易编号=A.外部交易编号  yangchen 20220324 end

        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
      //通道代码 = a.通道代码
        valFinProductDeal.setChlId(dto.getChannelId());
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 01-存款交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValBotAstTrade dto,
                                                         TrdProductDeal t1,String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        valFinProductDealExt.setProdId(dto.getProdId());
        **/
        valFinProductDealExt.setPortfolioId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
////        	valFinProductDealExt.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = A.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        return valFinProductDealExt;
    }
    /**
     * T5: 交易费用明细表 接收
     * 
     * @param dto 01-存款交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTradeFeeDetail(ValBotAstTrade dto, TrdProductDeal valFinProductDeal) {
    	TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
        // 交易编号 = T1.交易编号
        valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
        // 一次性费用类型 = 固定值：99 其他费用
        valTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_其他费用.getValue());
        // 是否随交易支付 = 固定值：Y 是 默认随交易支付
        valTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        // 费用金额 = A.交易费用
        valTradeFeeDetail.setFeeAmt(dto.getTrdFee());
        // 费用代码 = 固定值：99 其他费用
        valTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_其他费用.getValue());
        
        valTradeFeeDetail.setCreateTime(dto.getCreateTime());
        return valTradeFeeDetail;
    }

}
