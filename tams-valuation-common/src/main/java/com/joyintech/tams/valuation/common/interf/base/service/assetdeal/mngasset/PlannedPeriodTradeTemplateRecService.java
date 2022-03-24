/*
 * 文件名：PlannedPeriodTradeTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ruanhongming
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.mngasset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.PlannedAssetDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngPeriodTrade;
import com.joyintech.framework.util.StringUtil;

/**
 * 计划类资产（利率型）存续期交易模板处理 〈功能详细描述〉
 * 
 * @author ruanhongming
 * @version 1.0 2018年6月25日
 * @see PlannedPeriodTradeTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PlannedPeriodTradeTemplateRecService {

    /**
     * 基本信息模板处理service <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @Autowired
    private InterfCommonService interfCommonService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PlannedAssetDealCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        PlannedAssetDealCommonResult result = new PlannedAssetDealCommonResult();
        List<ValMngPeriodTrade> listValMngPeriodTrade = new ArrayList<ValMngPeriodTrade>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValMngPeriodTrade = returnResult.getT(ValMngPeriodTrade.class);
        //获取数据数量
        result.setDataNum(listValMngPeriodTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleListValMngPeriodTrade(listValMngPeriodTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        // 判断返回结果不为空
        if (map != null) {
            List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
            List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
            
            List<TrdTradeFeeDetail> valTradeFeeDetailList = (List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail");
            List<StlOrders> lsitValOrders = (List<StlOrders>)map.get("ValOrders");
            List<StlOrdersExt> lsitValOrdersExt = (List<StlOrdersExt>)map.get("ValOrdersExt");
            
            result.setListValDealAccounting((List<BokDealAccounting>)map.get("ValDealAccounting"));
            result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail"));
            
            result.setListValBokDetailId(listValBokDetailId);

            result.setListValFinProductDeal(listValFinProductDeal);
            result.setListValFinProductDealExt(listValFinProductDealExt);
            result.setValTradeFeeDetailList(valTradeFeeDetailList);
            result.setValOrdersList(lsitValOrders);
            result.setValOrdersExtList(lsitValOrdersExt);
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * @param handleListValMngPeriodTrade 计划类资产（利率型）基础信息表
     * @return 金融产品交易 +估值核算交易扩展表+入账信息表+账务明细表
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleListValMngPeriodTrade(List<ValMngPeriodTrade> listValMngPeriodTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> valFinProductDealList = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> valFinProductDealExtList = new ArrayList<TrdProductDealAdd>();
        List<TrdTradeFeeDetail> valTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        
        // 入账信息表 账务明细表 账务明细代码表 调用共通
        InterfProductDealBookCommonDto dealAccounting = null;
        // 金融产品交易
        TrdProductDeal valFinProductDeal = null;
        // 获取基础信息
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        // 获取基础信息
        for (ValMngPeriodTrade dto : listValMngPeriodTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);
            if(DictCodeList.交易方向_还本.getValue().equals(dto.getDealType()) 
                || DictCodeList.交易方向_到期.getValue().equals(dto.getDealType())){
            	// 金融产品交易 处理
                valFinProductDeal = getValFinProductDeal(dto,DictCodeList.交易方向_还本到账.getValue());
            	valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收本金.getValue()));
                valFinProductDealList.add(valFinProductDeal);
                
                // 入账信息表 账务明细表调用共通
                dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, dto.getProdId());
                
                // 估值核算交易扩展 处理
                TrdProductDealAdd valFinProductDealExt = getValFinProductDealExt(dto,valFinProductDeal,dealAccounting.getAcctSubjectType());
                valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
                valFinProductDealExtList.add(valFinProductDealExt);
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
                //四级科目尾号名称
                dealAccounting.getBokDetailId().setDetailSubnameTail(dto.getAssetName());
                
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                listValBokDetailId.add(dealAccounting.getBokDetailId());
                
                if(StringUtil.isEmpty(dto.getChannelId())){
                     valTradeFeeDetailList.add(getValTradeFeeDetail(dto,valFinProductDeal));
                }
                /**modify by wujie on 20190118 start
                ProductDealDto productDealDto = getProductDealDto(dto,valFinProductDeal,dealAccounting.getBokDetailId(),dealAccounting.getAcctSubjectType());
                String insType = DictCodeListExt1.指令类型_摊余成本类计划资产资产_还本到账.getValue();
                Map<String,Object> map = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
                StlOrders valOrders = (StlOrders)map.get("ORDERS");
                listValOrders.add(valOrders);
                StlOrdersExt valOrdersExt = (StlOrdersExt)map.get("ORDERS_EXT");
                listValOrdersExt.add(valOrdersExt);
                **/
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
                /**modify by wujie on 20190118 end**/
                
                /**到期、还本带息时，生成收息到账交易**/
                if ("Y".equals(dto.getIsInt())) {
                    
	                // 金融产品交易 处理
	                valFinProductDeal = getValFinProductDeal(dto,DictCodeList.交易方向_收息到账.getValue());
	                valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收利息.getValue()));
	                valFinProductDealList.add(valFinProductDeal);
                    
                    // 入账信息表 账务明细表调用共通
                    dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, dto.getProdId());
                    
                    // 估值核算交易扩展 处理
                    valFinProductDealExt = getValFinProductDealExt(dto,valFinProductDeal,dealAccounting.getAcctSubjectType());
                    valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
                    valFinProductDealExtList.add(valFinProductDealExt);
                    
                    listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                    listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                    
                    //四级科目尾号
                    dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
                    //四级科目尾号名称
                    dealAccounting.getBokDetailId().setDetailSubnameTail(dto.getAssetName());
                    listValBokDetailId.add(dealAccounting.getBokDetailId());
                    
                    if(StringUtil.isEmpty(dto.getChannelId())){
	                    valTradeFeeDetailList.add(getValTradeFeeDetail(dto,valFinProductDeal));
                    }
                    
                    /**modify by wujie on 20190118 start
                     productDealDto = getProductDealDto(dto,valFinProductDeal,dealAccounting.getBokDetailId(),dealAccounting.getAcctSubjectType());
                     insType = DictCodeListExt1.指令类型_摊余成本类计划资产资产_收息到账.getValue();
                     map = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
                     valOrders = (StlOrders)map.get("ORDERS");
                     listValOrders.add(valOrders);
                     valOrdersExt = (StlOrdersExt)map.get("ORDERS_EXT");
                     listValOrdersExt.add(valOrdersExt);
                     **/
                    map = interfCommonService.instructionGenerate(valFinProductDeal, 
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
                }
            }else{
            	// 金融产品交易 处理
                valFinProductDeal = getValFinProductDeal(dto,DictCodeList.交易方向_收息到账.getValue());
            	valFinProductDeal.setBranch(interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收利息.getValue()));
                valFinProductDealList.add(valFinProductDeal);
                
                // 入账信息表 账务明细表调用共通
                dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, dto.getProdId());
                
                // 估值核算交易扩展 处理
                TrdProductDealAdd valFinProductDealExt = getValFinProductDealExt(dto,valFinProductDeal,dealAccounting.getAcctSubjectType());
                valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
                valFinProductDealExtList.add(valFinProductDealExt);
                
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
                //四级科目尾号名称
                dealAccounting.getBokDetailId().setDetailSubnameTail(dto.getAssetName());
                listValBokDetailId.add(dealAccounting.getBokDetailId());
                if(StringUtil.isEmpty(dto.getChannelId())){
	                valTradeFeeDetailList.add(getValTradeFeeDetail(dto,valFinProductDeal));
                }
                /**modify by wujie on 20190118 start
                ProductDealDto productDealDto = getProductDealDto(dto,valFinProductDeal,dealAccounting.getBokDetailId(),dealAccounting.getAcctSubjectType());
                String insType =  DictCodeListExt1.指令类型_摊余成本类计划资产资产_收息到账.getValue();
                Map<String,Object> map = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
                StlOrders valOrders = (StlOrders)map.get("ORDERS");
                listValOrders.add(valOrders);
                StlOrdersExt valOrdersExt = (StlOrdersExt)map.get("ORDERS_EXT");
                listValOrdersExt.add(valOrdersExt);
                **/
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
                /**modify by wujie on 20190118 end**/
            }
            
            /**add by wujie on 20190322 start reamrk:通道投资费用交易生成**/
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, dealAccounting.getAcctSubjectType());
            if(channelFeeInfoDeal != null){
                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(valFinProductDeal, channelFeeInfoDeal);
                
                if(feedealMap.get("TrdProductDeal")!=null){
                    valFinProductDealList.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
                }
                
                if(feedealMap.get("TrdProductDealExt")!=null){
                    valFinProductDealExtList.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
                }
                
                if(feedealMap.get("TgTradeFeeDetail")!=null){
                    valTradeFeeDetailList.add((TrdTradeFeeDetail)feedealMap.get("TgTradeFeeDetail"));
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
        resultMap.put("ValFinProductDeal", valFinProductDealList);
        resultMap.put("ValFinProductDealExt", valFinProductDealExtList);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("ValTradeFeeDetail", valTradeFeeDetailList);
        resultMap.put("ValOrders", listValOrders);
        resultMap.put("ValOrdersExt", listValOrdersExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValMngPeriodTrade dto,String acctSubjectType){
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
     * 金融产品交易表 接收 T1
     * 
     * @param dto 01-存续交易信息
     * @param bookType 
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValMngPeriodTrade dto, String bookType) {

        TrdProductDeal valFinProductDeal = new TrdProductDeal();
//        // 交易编号 = B.成交编号
//        valFinProductDeal.setTradeId(dto.getTradeId());
        // 业务类型 = 09:计划类资产交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
        // 金融产品代码= B.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型= F17:债权类资产
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        // 分支序号= 设置全局变量(默认为0）
        valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= B.实际支付日
        valFinProductDeal.setTradeDate(dto.getPayDate());
        // 起息日= B.计息起始日
        valFinProductDeal.setVdate(dto.getVdate());
        // 到期日= B.计息结束日
        valFinProductDeal.setMdate(dto.getMdate());
        // 交割日 = B.交割日
        valFinProductDeal.setSettleDate(dto.getSettleDate());
        // 交易币种= B.币种
        valFinProductDeal.setCcy(dto.getCcy());

        //接口添加 资金账户= B.资金账户  yangchen 20220324 start
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());
        /*add by yangchen 20220324 交易界面：添加资金账户 end*/
        
        if (DictCodeList.交易方向_还本到账.getValue().equals(bookType)) {
            // 净价总额 = B.实收本金
            //valFinProductDeal.setCpriceAmt(dto.getActCapi());
            // 交易份额 = B.实收本金
            valFinProductDeal.setShareAmt(dto.getActCapi());
            // 交易本金 = B.实收本金
            valFinProductDeal.setPrinAmt(dto.getActCapi());
            // 交易金额 = B.实收本金
            valFinProductDeal.setTradeAmt(dto.getActCapi());
            // 全价总额 = B.实收本金
            valFinProductDeal.setFpriceAmt(dto.getActCapi());
            //交易方向
            valFinProductDeal.setPs(DictCodeList.交易方向_还本到账.getValue());
            
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_还本到账.getValue(),dto.getPayDate(),"A");
            valFinProductDeal.setTradeId(tradeId);
        }
        // 收息到账
        if (DictCodeList.交易方向_收息到账.getValue().equals(bookType)) {
            // 利息总额 = B.实收利息
            valFinProductDeal.setIntAmt(dto.getActInt());
            // 交易金额 = B.实收利息
            valFinProductDeal.setTradeAmt(dto.getActInt());
            // 全价总额 = B.实收利息
            valFinProductDeal.setFpriceAmt(dto.getActInt());
            //交易方向
            valFinProductDeal.setPs(DictCodeList.交易方向_收息到账.getValue());
            
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_收息到账.getValue(),dto.getPayDate(),"A");
            valFinProductDeal.setTradeId(tradeId);
        }
        
        // 单位面值 = B.单位面值
        valFinProductDeal.setParValue(dto.getParValue());
        /**add by wujie on 20190329 start**/
        if(StringUtil.isEmpty(dto.getChannelId())){
        	BigDecimal chlFeeAmt = dto.getChlFeeAmt()==null?BigDecimal.ZERO:dto.getChlFeeAmt();
        	// 随交易支付费用 = B.通道费
            valFinProductDeal.setFeeAmtPay(chlFeeAmt);
            // 未随交易支付费用 =默认为0
            valFinProductDeal.setFeeAmtUnpay(BigDecimal.ZERO);
            
            BigDecimal fpriceAmt = valFinProductDeal.getFpriceAmt();
            fpriceAmt = fpriceAmt.add(chlFeeAmt);
            
            BigDecimal tradeAmt = valFinProductDeal.getTradeAmt();
            tradeAmt = tradeAmt.add(chlFeeAmt);
            
            // 交易金额 = B.实收利息
            valFinProductDeal.setTradeAmt(tradeAmt);
            // 全价总额 = B.实收利息
            valFinProductDeal.setFpriceAmt(fpriceAmt);
        }
        /**add by wujie on 20190329 end**/
        
        // 投资目的 = B.投资目的
        valFinProductDeal.setInvAim(dto.getInvAim());
        if (StringUtil.isNotEmpty(dto.getDataActionSign())) {
            // 新增
            if (dto.getDataActionSign().equals("01")) {
                valFinProductDeal.setTradeStatus("01");
            }
            // 撤销
            else if (dto.getDataActionSign().equals("03")) {
                valFinProductDeal.setTradeStatus("03");
            }
        }
        // 是否结清 = A.是否结清
//        valFinProductDeal.setIsClean(dto.getIsClean());
        valFinProductDeal.setIsClean(dto.getIsClean2());
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
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
     * 估值核算交易扩展表 T2 接收
     * 
     * @param dto 01-存续交易信息
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValMngPeriodTrade dto,TrdProductDeal deal,String finprodType) {

    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = B.成交编号
        valFinProductDealExt.setTradeId(deal.getTradeId());
        // 理财产品代码 = B.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        valFinProductDealExt.setProdId(dto.getProdId());
        **/
        valFinProductDealExt.setPortfolioId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
//        	valFinProductDealExt.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = B.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        // 生成方式
        valFinProductDealExt.setGenType(
            com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }

    /**
     * 交易费用明细表 接收 T5
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTradeFeeDetail(ValMngPeriodTrade dto,TrdProductDeal valFinProductDeal) {
    	TrdTradeFeeDetail result = new TrdTradeFeeDetail();
        // 交易编号 = T1.交易编号
        result.setTradeId(valFinProductDeal.getTradeId());
        // 一次性费用类型 
        result.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        // 是否随交易支付 = Y 是
        result.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        // 费用金额 = A.通道费
        result.setFeeAmt(dto.getChlFeeAmt());
        // 费用代码 = B99 其他费用
        result.setFeeId(DictCodeListExt1.一次性费用类型_其他费用.getValue());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        return result;
    }
//    /**
//     * 结算指令 <一句话功能简述> <功能详细描述>
//     * @param dto 
//     * 
//     * @param valFinProductDeal
//     * @param valBokDetailId 
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private ProductDealDto getProductDealDto(ValMngPeriodTrade dto, TrdProductDeal valFinProductDeal, BokDetailId valBokDetailId,String acctSubjectType) {
//        ProductDealDto result = new ProductDealDto();
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
//        // 业务类型
//        result.setBusiType(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
//        // 理财产品代码
//        result.setProdId(dto.getProdId());
//        // 币种
//        result.setCcy(valFinProductDeal.getCcy());
//        // 是否随交易出账
//        result.setIsTrade(DictCodeList.是否_是.getValue());
//        //账套代码
//        result.setBookNo(valBokDetailId.getBooksetId());
//        //交割日
//        result.setEffDate(valFinProductDeal.getSettleDate());
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        result.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        return result;
//    }
}
