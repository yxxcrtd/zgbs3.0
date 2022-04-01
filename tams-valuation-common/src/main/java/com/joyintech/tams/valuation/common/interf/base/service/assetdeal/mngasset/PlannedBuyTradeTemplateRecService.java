/*
 * 文件名：PlannedBuyTradeTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ruanhongming
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.mngasset;

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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngBuyTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 计划类资产（利率型）买卖交易模板处理 〈功能详细描述〉
 * 
 * @author ruanhongming
 * @version 1.0 2018年6月25日
 * @see PlannedBuyTradeTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PlannedBuyTradeTemplateRecService {

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
        List<ValMngBuyTrade> listValMngBuyTrade = new ArrayList<ValMngBuyTrade>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValMngBuyTrade = returnResult.getT(ValMngBuyTrade.class);
        
        //获取数据数量
        result.setDataNum(listValMngBuyTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map  = handleListValMngBuyTrade(listValMngBuyTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        // 判断返回结果不为空
        if (map != null) {
            result.setListValBokDetailId((List<BokDetailId>)map.get("ValBokDetailId"));
            result.setListValFinProductDeal((List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt((List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            result.setValTradeFeeDetailList((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
            result.setValOrdersList((List<StlOrders>)map.get("ValOrders"));
            result.setValOrdersExtList((List<StlOrdersExt>)map.get("ValOrdersExt"));
            result.setListValDealAccounting((List<BokDealAccounting>)map.get("ValDealAccounting"));
            result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail"));
         // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * @param handleListValMngBuyTrade 计划类资产（利率型）基础信息表
     * @return 金融产品交易 +估值核算交易扩展表+入账信息表+账务明细表
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleListValMngBuyTrade(List<ValMngBuyTrade> listValMngBuyTrade,Date resovleDate,Map<String,Object> params,
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
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        
        for (ValMngBuyTrade dto : listValMngBuyTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);
            // 金融产品交易 处理
            TrdProductDeal valFinProductDeal = getValFinProductDeal(dto);
            valFinProductDealList.add(valFinProductDeal);
            
            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(
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
            
            TrdTradeFeeDetail valTradeFeeDetail = getValTradeFeeDetail(dto,valFinProductDeal);
            valTradeFeeDetailList.add(valTradeFeeDetail);
            
            /**modify by wujie on 20190118 start
            ProductDealDto productDealDto = getProductDealDto(dto,valFinProductDeal,dealAccounting.getBokDetailId(),dealAccounting.getAcctSubjectType());
            if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())){
               insType = DictCodeListExt1.指令类型_摊余成本类计划资产资产买入.getValue();
               Map<String,Object> map1 = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
               StlOrders valOrders1 = (StlOrders)map1.get("ORDERS");
               listValOrders.add(valOrders1);
               StlOrdersExt valOrdersExt1 = (StlOrdersExt)map1.get("ORDERS_EXT");
               listValOrdersExt.add(valOrdersExt1);
               
               insType = DictCodeListExt1.指令类型_资金交收_计划类资产买入_资金交收日.getValue();
               Map<String,Object> map2 = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
               StlOrders valOrders2 = (StlOrders)map2.get("ORDERS");
               listValOrders.add(valOrders2);
               StlOrdersExt valOrdersExt2 = (StlOrdersExt)map2.get("ORDERS_EXT");
               listValOrdersExt.add(valOrdersExt2);
               if(map2.get("ACCOUNTING")!=null){
                   listValDealAccounting.add((BokDealAccounting)map2.get("ACCOUNTING"));
               }
               if(map2.get("ACCOUNTING_DETAI")!=null){
                   listValDealAccountingDetail.add((BokDealAccountingDetail)map2.get("ACCOUNTING_DETAI"));
               }
               if(map2.get("BOK_DETAIL_ID")!=null){
                   listValBokDetailId.add((BokDetailId)map2.get("BOK_DETAIL_ID"));
               }
               
            }else if(DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())){
              
	            insType = DictCodeListExt1.指令类型_摊余成本类计划资产资产卖出.getValue();
	            Map<String,Object> map1 = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
	            StlOrders valOrders1 = (StlOrders)map1.get("ORDERS");
	            listValOrders.add(valOrders1);
	            StlOrdersExt valOrdersExt1 = (StlOrdersExt)map1.get("ORDERS_EXT");
	            listValOrdersExt.add(valOrdersExt1);
              	
              	insType = DictCodeListExt1.指令类型_资金交收_计划类资产卖出_资金到账.getValue();
                Map<String,Object> map2 = interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
                StlOrders valOrders2 = (StlOrders)map2.get("ORDERS");
                listValOrders.add(valOrders2);
                StlOrdersExt valOrdersExt2 = (StlOrdersExt)map2.get("ORDERS_EXT");
                listValOrdersExt.add(valOrdersExt2);
                if(map2.get("ACCOUNTING")!=null){
                    listValDealAccounting.add((BokDealAccounting)map2.get("ACCOUNTING"));
                }
                if(map2.get("ACCOUNTING_DETAI")!=null){
                    listValDealAccountingDetail.add((BokDealAccountingDetail)map2.get("ACCOUNTING_DETAI"));
                }
                if(map2.get("BOK_DETAIL_ID")!=null){
                    listValBokDetailId.add((BokDetailId)map2.get("BOK_DETAIL_ID"));
                }
                
            }
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
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("ValTradeFeeDetail", valTradeFeeDetailList);
        resultMap.put("ValOrders", listValOrders);
        resultMap.put("ValOrdersExt", listValOrdersExt);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValMngBuyTrade dto,String acctSubjectType){
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
     * 金融产品交易表 接收 T1
     * 
     * @param dto 01-买卖交易信息
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValMngBuyTrade dto) {

        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 = A.成交编号
        valFinProductDeal.setTradeId(
            interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A"));
        // 业务类型 = 09:计划类资产交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
        // 金融产品代码= A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型= F17:债权类资产
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        // 分支序号= 设置全局变量(默认为0）
        valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= A.交易日
        valFinProductDeal.setTradeDate(dto.getTradeDate());
        // 交割日= A.交割日
        valFinProductDeal.setSettleDate(dto.getEffDate());
        // 交易币种= A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价= A.净价/100
        valFinProductDeal.setUnitCprice(
            ArithmeticUtil.divide(dto.getCprice(), new BigDecimal(100), 2));
        // 单位利息= A.应收利息/100
        valFinProductDeal.setUnitInt(
            ArithmeticUtil.divide(dto.getUnitInt(), new BigDecimal(100), 2));
        // 单位全价= A.全价/100
        valFinProductDeal.setUnitFprice(
            ArithmeticUtil.divide(dto.getFprice(), new BigDecimal(100), 2));
        // 单位面值 = A.单位面值
        valFinProductDeal.setParValue(dto.getParValue());
        // 净价总额= A.净价总额
        valFinProductDeal.setCpriceAmt(dto.getCpriceAmt());
//        // 交易份额 = A.数量
//        valFinProductDeal.setShareAmt(dto.getNums());
        //modify jinmaodong 自定义资产交易份额修改为本金金额
        valFinProductDeal.setShareAmt(dto.getCapital());
        // 交易本金= A.本金金额
        valFinProductDeal.setPrinAmt(dto.getCapital());
        // 利息总额= A.应收利息总额
        valFinProductDeal.setIntAmt(dto.getIntAmt());
        // 全价总额= A.全价总额
        valFinProductDeal.setFpriceAmt(dto.getFpriceAmt());
        // 随交易支付费用 = A.费用
        valFinProductDeal.setFeeAmtPay(dto.getFeeAmtUnpay());
        // 未随交易支付费用 = A.未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(dto.getFeeAmtUnpay());
        // 总交易费用 = A.费用 + A.未随交易支付费用
        valFinProductDeal.setFeeAmt(
            ArithmeticUtil.doAdd(2, null, dto.getFeeAmtUnpay(), dto.getFeeAmtUnpay()));
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 到期收益率 = A.到期收益率
        valFinProductDeal.setYield(dto.getYield());
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
        // 交易金额取全价总额
        valFinProductDeal.setTradeAmt(dto.getFpriceAmt());
        // 是否结清 = A.是否结清
        valFinProductDeal.setIsClean(dto.getIsClean());
        // 通道代码 = A.通道代码
        valFinProductDeal.setChlId(dto.getChlId());
        // 交易对手 = A.交易对手
        valFinProductDeal.setCounterId(dto.getCounterId());
        // 交易对手类型 = A.交易对手类型
//        valFinProductDeal.setCounterType(dto.getCounterType());
        // 交易员 = A.交易员
        valFinProductDeal.setTrader(dto.getTrader());
        /*add by sunshuaishuai 交易管理 20220325 start*/
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getOutTradeId());
        /*add by sunshuaishuai 20220325 end*/
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        /** 投资目的  add by libingyan on 20210125 start */
        valFinProductDeal.setInvAim(dto.getInvAim());
        /** 投资目的  add by libingyan on 20210125 end  */

        /*add by sunshuaishuai 交易管理 20220325 start*/
        //资金账户
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220325 end*/
        return valFinProductDeal;
    }

    /**
     * 估值核算交易扩展表 接收 T2
     * 
     * @param dto 01-存续交易信息
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValMngBuyTrade dto,TrdProductDeal T1,String finprodType) {

    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = A.成交编号
        valFinProductDealExt.setTradeId(T1.getTradeId());
        // 理财产品代码 = A.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        valFinProductDealExt.setProdId(dto.getProdId());
        **/
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
//        	valFinProductDealExt.setProdId(dto.getProdId());
//        }
        
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = A.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 生成方式 = 接口接入
        valFinProductDealExt.setGenType(InterfCommonConstant.接入方式);
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
     * @param valFinProductDeal 
     * @return
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTradeFeeDetail(ValMngBuyTrade dto, TrdProductDeal valFinProductDeal) {
    	TrdTradeFeeDetail result = new TrdTradeFeeDetail();
        // 交易编号 = T1.交易编号
        //result.setTradeId(interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A"));
        result.setTradeId(valFinProductDeal.getTradeId());
        // 一次性费用类型 = B99 其他费用
        result.setFeeType(DictCodeListExt1.一次性费用类型_其他费用.getValue());
        // 是否随交易支付 = Y 是
        result.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        // 费用金额 = A.费用
        // result.setFeeAmt(dto.getFee());
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
//    private ProductDealDto getProductDealDto(ValMngBuyTrade dto, TrdProductDeal valFinProductDeal, BokDetailId valBokDetailId,String acctSubjectType) {
//        ProductDealDto result = new ProductDealDto();
//        //交易状态
//        result.setTradeStatus(valFinProductDeal.getTradeStatus());
//        // 交易编号
//        result.setTradeId(valFinProductDeal.getTradeId());
//        // 交易日
//        result.setTradeDate(valFinProductDeal.getTradeDate());
//        // 交易方向
//        result.setPs(valFinProductDeal.getPs());
//        // 交易金额
//        result.setTardeAmt(valFinProductDeal.getTradeAmt());
//        // 金融产品代码
//        result.setFinprodId(dto.getProdId());
//        // 金融产品类型
//        /**modify by wujie on 20190107 start
//        result.setFinprodType(interfCommonService.queryProductType(dto.getProdId())); 
//        **/
//        result.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        
//        // 业务类型
//        result.setBusiType(DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue());
//        // 交易对手
//        result.setCounterId(valFinProductDeal.getCounterId());
//        // 交易对手类型
//        result.setCounterType(valFinProductDeal.getCounterType());
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
