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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券存续期交易 模板处理Service(VAL_BOT_AST_INFO)
 * 
 * @author weipeng
 * @version 1.0 2018年6月27日
 * @see SecVestPeriodTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecVestPeriodTemplateRecService {

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
     * 债券存续期交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券存续期交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSecVestPeriod> listValSecVestPeriod = new ArrayList<ValSecVestPeriod>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValSecVestPeriod = returnResult.getT(ValSecVestPeriod.class);
        //获取数据数量
        result.setDataNum(listValSecVestPeriod.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSecVestPeriod(listValSecVestPeriod,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

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
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
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
     * 02-存续期交易
     * 
     * @param listValSecVestPeriod 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValSecVestPeriod(List<ValSecVestPeriod> listValSecVestPeriod,Date resovleDate,Map<String,Object> params,
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
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        //结算指令list
        List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
        //结算指令拓展list
        List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
	    for (ValSecVestPeriod dto : listValSecVestPeriod) {
	       	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
	       		continue;
	       	}
            dto.setCreateTime(resovleDate);
            // 金融产品交易
            // 还本带息时接收交易时将拆分成还本到账、收息到账两笔交易
            TrdProductDeal valFinProductDeal = new TrdProductDeal();
            // 交易入帐信息公共处理Dto
            InterfProductDealBookCommonDto dealAccounting = null;//modify by dongjun on 20190401 通道变更
            if(DictCodeList.交易方向_还本.getValue().equals(dto.getPs()) 
            		|| DictCodeList.交易方向_到期.getValue().equals(dto.getPs())){
            	/**生成还本到账交易**/
            	valFinProductDeal = this.getValFinProductDeal(dto, DictCodeList.交易方向_还本到账.getValue());
            	valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(), 
            			DictCodeList.分支类型_应收本金.getValue()));
                //20200115 tanxuan 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
                if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
                	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
                }
            	
            	listValFinProductDeal.add(valFinProductDeal);
            	
                 // 入账信息表   账务明细表调用共通
                dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                
                // 估值核算交易扩展表
                TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                    valFinProductDeal,dealAccounting.getAcctSubjectType());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
                
                //四级科目尾号
                valBokDetailId.setDetailSubjectNoTail(dto.getAssetId());
                
                //四级科目尾号名称
                valBokDetailId.setDetailSubnameTail(dto.getSecAbbr());
                
                listValBokDetailId.add(valBokDetailId);
                
                /**modify by wujie on 20190118 start
                BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
                Map<String, Object> map=this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
                if(map.get("ORDERS")!=null){
                	StlOrders valOrders=(StlOrders)map.get("ORDERS");
                    valOrdersList.add(valOrders);
                }
                if(map.get("ORDERS_EXT") != null){
                	StlOrdersExt alOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
                    valOrdersExtList.add(alOrdersExt);
                }
                **/
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
                /**modify by wujie on 20190118 end**/
                
            	/**到期、还本带息时，生成收息到账交易**/
            	if ("Y".equals(dto.getIsAmt())) {
            		valFinProductDeal = this.getValFinProductDeal(dto, DictCodeList.交易方向_收息到账.getValue());
                	valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(),DictCodeList.分支类型_应收利息.getValue()));
                    //20200115 tanxuan 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
                    if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
                    	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
                    }
            		listValFinProductDeal.add(valFinProductDeal);
                	
                     // 入账信息表   账务明细表调用共通
                    dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
                    listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                    listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                    
                    // 估值核算交易扩展表
                    valFinProductDealExt = this.getValFinProductDealExt(dto,
                        valFinProductDeal,dealAccounting.getAcctSubjectType());
                    listValFinProductDealExt.add(valFinProductDealExt);
                    
                    valBokDetailId = dealAccounting.getBokDetailId();
                    
                    //四级科目尾号
                    valBokDetailId.setDetailSubjectNoTail(dto.getAssetId());
                    
                    //四级科目尾号名称
                    valBokDetailId.setDetailSubnameTail(dto.getSecAbbr());
                    
                    listValBokDetailId.add(valBokDetailId);
                    
                    /**modify by wujie on 20190118 start
                    map=this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
                    if(map.get("ORDERS")!=null){
                    	StlOrders valOrders=(StlOrders)map.get("ORDERS");
                        valOrdersList.add(valOrders);
                    }
                    if(map.get("ORDERS_EXT")!=null){
                    	StlOrdersExt alOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
                        valOrdersExtList.add(alOrdersExt);
                    }
                    **/
                    map = interfCommonService.instructionGenerate(valFinProductDeal, 
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
                    /**modify by wujie on 20190118 end**/
            	}
            }else{
            	valFinProductDeal = this.getValFinProductDeal(dto, DictCodeList.交易方向_收息到账.getValue());
            	valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收利息.getValue()));
                //20200115 tanxuan 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
                if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
                	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
                }
            	listValFinProductDeal.add(valFinProductDeal);
            	
                 // 入账信息表   账务明细表调用共通
                dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                
                // 估值核算交易扩展表
                TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                    valFinProductDeal,dealAccounting.getAcctSubjectType());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
                
                //四级科目尾号
                valBokDetailId.setDetailSubjectNoTail(dto.getAssetId());
                
                //四级科目尾号名称
                valBokDetailId.setDetailSubnameTail(dto.getSecAbbr());
                
                listValBokDetailId.add(valBokDetailId);
                
                /**modify by wujie on 20190118 start
                BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
                Map<String, Object> map=this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
                if(map.get("ORDERS") != null){
                	StlOrders valOrders=(StlOrders)map.get("ORDERS");
                    valOrdersList.add(valOrders);
                }
                if(map.get("ORDERS_EXT") != null){
                	 StlOrdersExt alOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
                     valOrdersExtList.add(alOrdersExt);
                }
                **/
                Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
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
                /**modify by wujie on 20190118 end**/
            }
            
            if(dealAccounting != null){
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
                        valOrdersList.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                    }
                    if(feedealMap.get("StlOrdersExt_List")!=null){
                        valOrdersExtList.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                    }
                }
            }
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("valOrdersList", valOrdersList);
        resultMap.put("valOrdersExtList", valOrdersExtList);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
        return resultMap;
    }
    
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSecVestPeriod dto, TrdProductDeal valFinProductDeal){
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
    
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal dto,String prodId,String bookSetId,String acctSubjectType) {
//    	//生成结算指令
//        ProductDealDto productDealDto=new ProductDealDto();
//        //* 交易状态
//        productDealDto.setTradeStatus(dto.getTradeStatus()); 
//        //交易编号=dto.交易编号
//        productDealDto.setTradeId(dto.getTradeId());
//        //交易日=dto.交易日
//        productDealDto.setTradeDate(dto.getTradeDate());
//        //交割日
//        productDealDto.setEffDate(dto.getSettleDate());
//        //交易方向
//        productDealDto.setPs(dto.getPs());
//        //交易金额
//        productDealDto.setTardeAmt(dto.getTradeAmt());
//        //金融产品代码=资产代码
//        productDealDto.setFinprodId(prodId);
//        //金融产品类型
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId)); 
//        /**modify by wujie on 20190107 start
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId));
//        **/
//        productDealDto.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        //业务类型
//        productDealDto.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
//        //理财产品代码
//        productDealDto.setProdId(prodId);
//        //币种
//        productDealDto.setCcy(dto.getCcy());
//        // 是否随交易出账
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());
//        
//        productDealDto.setBookNo(bookSetId);
//        
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(dto.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        /**add by chendijun  2018/11/20 start**/
//        //交易场所
//        productDealDto.setTradeMarket(dto.getTradeMarket());
//        //交易市场
//        productDealDto.setTradeMarketMode(dto.getTradeMarketMode());
//        /**add by chendijun  2018/11/20 end**/
//        //指令类型
//        String insType=null;
//        if(dto.getInvAim().equals(DictCodeListExt1.投资目的_摊余成本类.getValue())){
//        	//摊余成本 判断交易方向
//        	if(DictCodeList.交易方向_还本到账.getValue().equals(dto.getPs())){
//        		//还本到账
//        		insType=DictCodeListExt1.指令类型_摊余成本类债券资产_还本到账.getValue();
//        		
//        	}else if(dto.getPs().equals(DictCodeList.交易方向_收息到账.getValue())){
//        		//到息收账
//        		insType=DictCodeListExt1.指令类型_摊余成本类债券资产_收息到账.getValue();
//        	}
//        }else if(dto.getInvAim().equals(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())){
//        	//FVTPL 
//        	if(DictCodeList.交易方向_还本到账.getValue().equals(dto.getPs())){
//        		//还本到账
//        		insType=DictCodeListExt1.指令类型_公允价值损益类债券资产_还本到账.getValue();
//        	}else if(dto.getPs().equals(DictCodeList.交易方向_收息到账.getValue())){
//        		//收息到账
//        		insType=DictCodeListExt1.指令类型_公允价值损益类债券资产_收息到账.getValue();
//        	}
//        	
//        }else if(dto.getInvAim().equals(DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())){
//        	//FVCOI   
//        	if(DictCodeList.交易方向_还本到账.getValue().equals(dto.getPs())){
//        		//还本到账 
//        		insType=DictCodeListExt1.指令类型_综合收益类债券资产_还本到账.getValue();
//        	}else if(dto.getPs().equals(DictCodeList.交易方向_收息到账.getValue())){
//        		//收息到账
//        		insType=DictCodeListExt1.指令类型_综合收益类债券资产_收息到账.getValue();
//        	}
//        }
//        return interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//    }
    

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 02-存续期交易
     * @param bookType 定义到账类型：01：还本到账，02：收息到账
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValSecVestPeriod dto, String bookType) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 业务类型 = 02 债券交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
        // 金融产品代码 = B.产品代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F01 债券
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        // 交易日 = B.交易日
        valFinProductDeal.setTradeDate(dto.getTradeDate());
        // 交易币种 = B.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位面值 = B。单位面值
        valFinProductDeal.setParValue(dto.getParValue());
        /**add by wujie on 20180712 start**/
        //起息日= B.计息开始日
        valFinProductDeal.setVdate(dto.getVdate());
        //到期日= B.计息结束日
        valFinProductDeal.setMdate(dto.getMdate());
        //到期日= B.计息结束日
        valFinProductDeal.setInvAim(dto.getInvAim());
        //交割日 = B.交割日
        valFinProductDeal.setSettleDate(dto.getSettleDate());
        /**add by wujie on 20180712 end**/
        // 还本到账
        if (DictCodeList.交易方向_还本到账.getValue().equals(bookType)) {
            // 净价总额 = B.实收本金
            valFinProductDeal.setCpriceAmt(dto.getActCapi());
	        // 交易份额 = B.成交数量/100
	        valFinProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getDealNum(), new BigDecimal(100),2));
            // 交易本金 = B.实收本金
            valFinProductDeal.setPrinAmt(dto.getActCapi());
            // 交易金额 = B.实收本金
            valFinProductDeal.setTradeAmt(dto.getActCapi());
            // 全价总额 = B.实收本金
            valFinProductDeal.setFpriceAmt(dto.getActCapi());
            //交易编号 =共通生成
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_还本到账.getValue(),dto.getSettleDate(),"A");
            valFinProductDeal.setTradeId(tradeId);
            
            //交易方向
            valFinProductDeal.setPs(DictCodeList.交易方向_还本到账.getValue());
        }
        // 收息到账
        if (DictCodeList.交易方向_收息到账.getValue().equals(bookType)) {
            // 利息总额 = B.实收利息
            valFinProductDeal.setIntAmt(dto.getActInt());
            // 交易金额 = B.实收利息
            valFinProductDeal.setTradeAmt(dto.getActInt());
            // 全价总额 = B.实收利息
            valFinProductDeal.setFpriceAmt(dto.getActInt());
            //交易编号 =共通生成
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_收息到账.getValue(),dto.getSettleDate(),"A");
            valFinProductDeal.setTradeId(tradeId);
            //交易方向
            valFinProductDeal.setPs(DictCodeList.交易方向_收息到账.getValue());
        }
        //随交易支付费用 = B.通道费
        valFinProductDeal.setFeeAmtPay(dto.getChlFeeAmt());
        //未随交易支付费用 = 默认为0
        valFinProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
        // 总交易费用 = B.通道费
        valFinProductDeal.setFeeAmt(dto.getChlFeeAmt());
        
        // 投资目的 = B.投资目的
        valFinProductDeal.setInvAim(dto.getInvAim());
        // 交易状态 = B.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = B.是否结清
//        valFinProductDeal.setIsClean(dto.getIsClean());
        valFinProductDeal.setIsClean(dto.getIsClean2());
        /**add by chendijun 2018/11/19 start**/
        //交易场所 = 调用共通设计->【详细设计-标的类共通方法设计.xlsx】->标的类金融产品信息查询SHEET 交易场所 = 查询接口.交易场所
        TargetFinProduct targetFinProduct = interfCommonService.getTargetFinProduct(dto.getAssetId());
        valFinProductDeal.setTradeMarket(targetFinProduct.getTradeMarket());
        //交易市场 = 数据字典：流通市场.二级市场
        valFinProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        /**add by chendijun 2018/11/19 end**/
        // 关联系统交易编号 = B.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
//        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
//        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
//        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
//        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        valFinProductDeal.setCreateDept(null);
        //通道代码 = a.通道代码
        valFinProductDeal.setChlId(dto.getChannelId());
        //计提份额 = a.计提份额 add by yanya on 20210303
        valFinProductDeal.setAccrNum(dto.getAccrNum());
        /*add by sunshuaishuai 交易管理 20220325 start*/
        //证券账户
        valFinProductDeal.setSecManageAcctId(dto.getSecManageAcctId());
        /*add by sunshuaishuai 20220325 end*/
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 02-存续期交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValSecVestPeriod dto,
                                                         TrdProductDeal t1,String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = B.产品代码
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
        // 是否有结算指令 = B.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        /*modify by sunshuaishuai 20220413 判断生成方式是接口接入还是画面录入 start*/
        //生成方式
        if(StringUtil.isNotEmpty(dto.getDataSource())){
            valFinProductDealExt.setGenType(dto.getDataSource());
        }else {
            valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        }
        /*modify by sunshuaishuai 20220413 判断生成方式是接口接入还是画面录入 end*/
        return valFinProductDealExt;
    }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSecVestPeriod dto,String acctSubjectType){
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
    
}
