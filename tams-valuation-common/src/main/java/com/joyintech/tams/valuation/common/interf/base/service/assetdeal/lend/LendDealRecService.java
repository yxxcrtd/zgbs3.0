/*
 * 文件名：FundBonusReinvestmentDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.lend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joyintech.framework.util.StringUtil;

import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.common.interf.base.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlDeal;

/**
 *拆借交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see LendDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class LendDealRecService {

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    /**
     * FundDealInfoForInterfRecCommonResult
     * 
     * @param templateSourceFlgDTO TemplateSourceFlgDTO
     * @param acctEntity TemplateOrderInfoReaderResult
     * @return FundDealInfoForInterfRecCommonResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public LendDealCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                        TemplateOrderInfoReaderResult acctEntity) {
        LendDealCommonResult result = new LendDealCommonResult();
        List<ValDlDeal> listValDlDeal = new ArrayList<ValDlDeal>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValDlDeal = returnResult.getT(ValDlDeal.class);
        //获取数据数量
        result.setDataNum(listValDlDeal.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构

        Map<String, Object> map = this.handleValDlDeal(listValDlDeal,acctEntity,params,returnResult.getValInterflog());
        if (map!=null) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            result.setBokDealAccountingList((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));
            result.setBokDealAccountingDetailList((List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail"));
            result.setBokDetailIdList((List<BokDetailId>)map.get(
                    "ValBokDetailId"));
            result.setListValOrders(
                    (List<StlOrders>)map.get(
                            "valOrdersList"));
            result.setListValOrdersExt(
                    (List<StlOrdersExt>)map.get(
                            "valOrdersExtList"));
            
            // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 
     * @param listValBonusResTrade List<ValBonusResTrade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleValDlDeal(List<ValDlDeal> listValDlDeal,TemplateOrderInfoReaderResult acctEntity,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
        // 入账信息表 账务明细表 账务明细代码表 调用共通
        InterfProductDealBookCommonDto dealAccounting = null;
        // 金融产品交易
        TrdProductDeal valFinProductDeal = null;
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        //同业借款：四级科目类型 01：资产代码  02：期限品种
        Map<String,BokBooksetExt> bokBooksetExtMap = new HashMap<String,BokBooksetExt>();
        
        for (ValDlDeal item : listValDlDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	TargetFinProduct targetFinProduct = interfCommonService.queryTargetFinProduct(item.getpTradeId());
            String finProductType = targetFinProduct==null?null:targetFinProduct.getFinprodType();

            item.setCreateTime(acctEntity.getResovleDate());
            //同业借款到期交易，拆成还本到账、收息到账2笔交易
            if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finProductType) &&(
                    DictCodeList.交易方向_还本.getValue().equals(item.getDealType())
                            || DictCodeList.交易方向_到期.getValue().equals(item.getDealType()))
            ){
        		//生成还本到账交易
                valFinProductDeal = getValFinProductDeal(item,finProductType,DictCodeList.交易方向_还本到账.getValue());
            	Long maxBranch = interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收本金.getValue());
                valFinProductDeal.setBranch(maxBranch);
                
            	listValFinProductDeal.add(valFinProductDeal);
                
                // 入账信息表 账务明细表调用共通
                dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
                
                // 估值核算交易扩展 处理
                TrdProductDealAdd valFinProductDealExt = getValFinProductDealExt(item,valFinProductDeal,dealAccounting.getAcctSubjectType());
                valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                
                //同业借款交易的四级科目尾号、四级科目尾号名称改为资产的金融产品代码、金融产品简称
                String detailSubjectNoTail = null;
                String detailSubnameTail = null;
                
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
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(detailSubjectNoTail);
                //四级科目尾号名称
                dealAccounting.getBokDetailId().setDetailSubnameTail(detailSubnameTail);


                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                listValBokDetailId.add(dealAccounting.getBokDetailId());
                
                Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
                		item.getProdId(), dealAccounting.getAcctSubjectType(),null);
                
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
                
                /**还本、到期带利息-生成收息到账交易**/
                if(item.getActInt()!=null && BigDecimal.ZERO.compareTo(item.getActInt())<0){
                	valFinProductDeal = getValFinProductDeal(item,finProductType
                    		,DictCodeList.交易方向_收息到账.getValue());
                	maxBranch = interfCommonService.getBranch(valFinProductDeal.getMdate(), DictCodeList.分支类型_应收利息.getValue());
                	valFinProductDeal.setBranch(maxBranch);
                	
                	listValFinProductDeal.add(valFinProductDeal);
                    
                    // 入账信息表 账务明细表调用共通
                    dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, item.getProdId());
                    // 估值核算交易扩展 处理
                    valFinProductDealExt = getValFinProductDealExt(item,valFinProductDeal,dealAccounting.getAcctSubjectType());
                    valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
                    listValFinProductDealExt.add(valFinProductDealExt);

                    //四级科目尾号
                    dealAccounting.getBokDetailId().setDetailSubjectNoTail(detailSubjectNoTail);
                    //四级科目尾号名称
                    dealAccounting.getBokDetailId().setDetailSubnameTail(detailSubnameTail);

                    listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                    listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                    listValBokDetailId.add(dealAccounting.getBokDetailId());
                    
                    map = interfCommonService.instructionGenerate(valFinProductDeal, 
                    		item.getProdId(), dealAccounting.getAcctSubjectType(),null);
                    
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
                }
        	}else{
                valFinProductDeal = this.getValFinProductDeal(item,targetFinProduct);
                listValFinProductDeal.add(valFinProductDeal);
                
                dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
                
                TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,dealAccounting.getAcctSubjectType());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                //同业借款交易的四级科目尾号、四级科目尾号名称改为资产的金融产品代码、金融产品简称
                String detailSubjectNoTail = null;
                String detailSubnameTail = null;
                if(StringUtil.isEmpty(acctEntity.getFourLevelSubjectKg())
                        || DictCodeList.是否_是.getValue().equals(acctEntity.getFourLevelSubjectKg())){
                	
                    if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finProductType)) {
                    	//查询账套参数【TYJK_FORSUBJECT】:01:资产代码 02：期限品种
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
                
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(detailSubjectNoTail);
                //四级科目尾号名称
                dealAccounting.getBokDetailId().setDetailSubnameTail(detailSubnameTail);

                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                listValBokDetailId.add(dealAccounting.getBokDetailId());

                //结算指令
                Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
                		item.getProdId(), dealAccounting.getAcctSubjectType(),null);
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
        	}
        	
        	/**add by wujie on 20190322 start reamrk:通道投资费用交易生成**/
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(item, dealAccounting.getAcctSubjectType());
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
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValDlDeal dto,String acctSubjectType){
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
    private TrdProductDeal getValFinProductDeal(ValDlDeal dto,String finProductType, String bookType) {
    	TrdProductDeal result = new TrdProductDeal();
        // 业务类型 = 01 拆借交易
        result.setBusiType(DictCodeList.业务类型_拆借交易.getValue());
        // 金融产品代码 = A.原交易编号
        result.setFinprodId(dto.getpTradeId());
        // 金融产品类型  = 
        result.setFinprodType(finProductType);
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= A.交易日
        result.setTradeDate(dto.getDealDate());
        //起息日 = A.起息日
        result.setVdate(dto.getVdate());
        //到期日 = A.到期日
        result.setMdate(dto.getMdate());
        //交割日 = A.交割日
        result.setSettleDate(dto.getSettleDate());
        //交易币种 = A.币种
        result.setCcy(dto.getCcy());
        //到期收益率 = A.拆借利率
        result.setYield(dto.getRate());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 =设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        //交易对手 = A.交易对手
        result.setCounterId(dto.getCounterId());
        //交易对手类型 = A.交易对手类型
//        result.setCounterType(dto.getCounterType());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());

        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //交易状态
        result.setTradeStatus(dto.getDataActionSign());
        if (DictCodeList.交易方向_还本到账.getValue().equals(bookType)) {
            // 交易份额 = B.实收本金
        	result.setShareAmt(dto.getCapital());
            // 交易本金 = B.实收本金
        	result.setPrinAmt(dto.getCapital());
            // 交易金额 = B.实收本金
        	result.setTradeAmt(dto.getCapital());
            // 全价总额 = B.实收本金
        	result.setFpriceAmt(dto.getCapital());
            //交易方向
        	result.setPs(DictCodeList.交易方向_还本到账.getValue());
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_还本到账.getValue(),dto.getDealDate(),"A");
            result.setTradeId(tradeId);
        }
        // 收息到账
        if (DictCodeList.交易方向_收息到账.getValue().equals(bookType)) {
            // 利息总额 = B.实收利息
        	result.setIntAmt(dto.getActInt());
            // 交易金额 = B.实收利息
        	result.setTradeAmt(dto.getActInt());
            // 全价总额 = B.实收利息
        	result.setFpriceAmt(dto.getActInt());
            //交易方向
        	result.setPs(DictCodeList.交易方向_收息到账.getValue());
            String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_收息到账.getValue(),dto.getDealDate(),"A");
            result.setTradeId(tradeId);
        }
        
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
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
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String bookSetId,String acctSubjectType) {
//    	/**add by wujie on 20181211 start**/
//    	Map<String, Object> paramsMap = new HashMap<>();
//    	List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//    	List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//    	List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
//    	List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
//    	List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
//    	/**add by wujie on 20181211 end**/
//    	
//    	//交易指令 调用共通 
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
//        String insType = null;
//        if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(valFinProductDeal.getFinprodType())){
//        	if(DictCodeList.交易方向_买入.getValue().equals(valFinProductDeal.getPs())){
//        		/**
//        		 * modify by wujie on 20190111 start
//        		insType = DictCodeListExt1.指令类型_拆放同业_同业借款_买入.getValue();
//        		**/
//        		insType = DictCodeListExt1.指令类型_拆入_起息.getValue();
//        		/**modify by wujie on 20190111 end**/
//        		
//        		/**add by wujie on 20181211 start**/
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//        		
//                insType=  DictCodeListExt1.指令类型_资金收款_拆入起息.getValue();
//                map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//            	 ValOrders=(StlOrders)map.get("ORDERS");
//                 ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                if(map.get("ACCOUNTING")!=null){
//                    listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//                }
//                if(map.get("ACCOUNTING_DETAI")!=null){
//                    listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//                }
//                if(map.get("BOK_DETAIL_ID")!=null){
//                    listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//                }
//                /**add by wujie on 20181211 end**/
//        		
//        	}
//        	else{
//        		insType = DictCodeListExt1.指令类型_拆放同业_拆入到期.getValue();
//        		
//        		/**modify by wujie on 20181211 start**/
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//
//                insType=  DictCodeListExt1.指令类型_资金划款_拆放同业_拆入到期.getValue();
//                map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//            	 ValOrders=(StlOrders)map.get("ORDERS");
//                 ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                if(map.get("ACCOUNTING")!=null){
//                    listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//                }
//                if(map.get("ACCOUNTING_DETAI")!=null){
//                    listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//                }
//                if(map.get("BOK_DETAIL_ID")!=null){
//                    listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//                }
//                
//                /**modify by wujie on 20181211 end**/
//        		
//        	}
//        }else{
//        	//拆出、同业借款
//        	if(DictCodeList.交易方向_买入.getValue().equals(valFinProductDeal.getPs())){
//        		insType = DictCodeListExt1.指令类型_拆放同业_同业借款_买入.getValue();
//        		
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                insType=  DictCodeListExt1.指令类型_资金收款_同业借款_买入.getValue();
//                map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//            	 ValOrders=(StlOrders)map.get("ORDERS");
//                 ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                if(map.get("ACCOUNTING")!=null){
//                    listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//                }
//                if(map.get("ACCOUNTING_DETAI")!=null){
//                    listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//                }
//                if(map.get("BOK_DETAIL_ID")!=null){
//                    listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//                }
//        	}
//        	else if(DictCodeList.交易方向_卖出.getValue().equals(valFinProductDeal.getPs())){
//        		insType = DictCodeListExt1.指令类型_拆放同业_转仓卖出.getValue();
//        		
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                
//                insType=  DictCodeListExt1.指令类型_资金收款_同业借款_卖出.getValue();
//                map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//            	 ValOrders=(StlOrders)map.get("ORDERS");
//                 ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//                
//                if(map.get("ACCOUNTING")!=null){
//                    listValDealAccounting.add((BokDealAccounting)map.get("ACCOUNTING"));
//                }
//                if(map.get("ACCOUNTING_DETAI")!=null){
//                    listValDealAccountingDetail.add((BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"));
//                }
//                if(map.get("BOK_DETAIL_ID")!=null){
//                    listValBokDetailId.add((BokDetailId)map.get("BOK_DETAIL_ID"));
//                }
//        		
//        	}
//        	else if(DictCodeList.交易方向_还本到账.getValue().equals(valFinProductDeal.getPs())){
//        		/** modify by wujie on 20181229 start
//        		insType = DictCodeListExt1.指令类型_拆放同业_到期.getValue();
//        		**/
//        		insType = DictCodeListExt1.指令类型_资金收款_同业借款_还本到期.getValue();
//        		
//        		/**modify by wujie on 20181229 end**/
//        		
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//        		
//        	}
//        	else if(DictCodeList.交易方向_收息到账.getValue().equals(valFinProductDeal.getPs())){
//        		/** modify by wujie on 20181229 start
//        		insType = DictCodeListExt1.指令类型_拆分同业_中间收息.getValue();
//        		**/
//        		insType = DictCodeListExt1.指令类型_资金收款_同业借款_中间收息.getValue();
//        		/**add by wujie on 20181229 end**/
//        		
//        		Map<String, Object> map=interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//            	StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//                listValOrders.add(ValOrders);
//                listValOrdersExt.add(ValOrdersExt);
//        	}
//        }
//        /**
//         * modify by wujie on 20181211 start
//        return interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//        */
//        paramsMap.put("ORDERS", listValOrders);
//        paramsMap.put("ORDERS_EXT", listValOrdersExt);
//        paramsMap.put("ValDealAccounting", listValDealAccounting);
//        paramsMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
//        paramsMap.put("ValBokDetailId", listValBokDetailId);
//        return paramsMap;
//        /**modify by wujie on 20181211 end**/
//    }
    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValDlDeal
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValDlDeal dto,TargetFinProduct targetFinProduct) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号
        String tradeId=interfCommonService.getTradeid(dto.getDealType(),dto.getDealDate(),"A");
        result.setTradeId(tradeId);
        // 业务类型 = 01 拆借交易
        result.setBusiType(DictCodeList.业务类型_拆借交易.getValue());
        // 金融产品代码 = A.原交易编号
        result.setFinprodId(dto.getpTradeId());
        /**add by wujie on 20180825 start**/
        // 金融产品类型  =
        result.setFinprodType(targetFinProduct==null?null:targetFinProduct.getFinprodType());
        /**add by wujie on 20180825 end**/
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= A.交易日
        result.setTradeDate(dto.getDealDate());
        //起息日 = A.起息日
        result.setVdate(dto.getVdate());
        //到期日 = A.到期日
        result.setMdate(dto.getMdate());
        //交割日 = A.交割日
        result.setSettleDate(dto.getSettleDate());
        //交易币种 = A.币种
        result.setCcy(dto.getCcy());
        
        //净价总额 = A.本金金额
        result.setCpriceAmt(dto.getCapital());
        //交易份额 = A.本金金额
        result.setShareAmt(dto.getCapital());
        //全价总额 = A.本金金额+A.实收利息
        result.setFpriceAmt(dto.getCapital().add(dto.getActInt()==null?BigDecimal.ZERO:dto.getActInt()));
        //交易本金 = A.本金金额
        result.setPrinAmt(dto.getCapital());
        //利息总额  = A.实收利息
        result.setIntAmt(dto.getActInt());
        //交易金额 = A.交易金额
        result.setTradeAmt(dto.getDealAmt());
        // 交易方向 =A.交易类型
        result.setPs(dto.getDealType());
        //到期收益率 = A.拆借利率
        result.setYield(dto.getRate());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 =设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        //交易对手 = A.交易对手
        result.setCounterId(dto.getCounterId());
        //交易对手类型 = A.交易对手类型
//        result.setCounterType(dto.getCounterType());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        // 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }

      //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValDlDeal
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValDlDeal dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
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
//        result.setHasOrder(dto.getIsClean());
        //生成方式
        result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }

}
