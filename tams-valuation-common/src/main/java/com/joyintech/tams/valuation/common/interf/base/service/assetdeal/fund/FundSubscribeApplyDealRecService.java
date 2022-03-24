/*
 * 文件名：FundSubscribeApplyDealService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValApplyPurTade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 01申购申请交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see FundSubscribeApplyDealService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundSubscribeApplyDealRecService {

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
    public FundDealInfoForInterfRecCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                        TemplateOrderInfoReaderResult acctEntity) {
        FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
        List<ValApplyPurTade> listValApplyPurTade = new ArrayList<ValApplyPurTade>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValApplyPurTade = returnResult.getT(ValApplyPurTade.class);
        
        // 获取数据数量
        result.setDataNum(listValApplyPurTade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态, DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/
        
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValApplyPurTade(listValApplyPurTade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                    "ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);
            
            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);
            
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
            // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 
     * @param listValApplyPurTade List<ValApplyPurTade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValApplyPurTade(List<ValApplyPurTade> listValApplyPurTade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        
        for (ValApplyPurTade item : listValApplyPurTade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	
        	item.setCreateTime(resovleDate);
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);
            
            //查询账套信息：
            String acctSubjectId = item.getProdId();
            if(!StringUtil.isEmpty(item.getChannelId())){
            	acctSubjectId = acctSubjectId+FinalString.LAYERING_SPLIT+item.getChannelId();
            }
            ValBooksetInfoDto bookSetInfo = interfCommonService.getBooksetInfoByProdId(acctSubjectId);
            
            String finprodType = null;
            if(bookSetInfo != null && bookSetInfo.getBooksetId()!=null){
            	finprodType = bookSetInfo.getAcctSubjectType();
            }
            
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,finprodType);
            valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            //结算指令
            StlOrders stlOrders = this.getStlOrders(item);
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
                		item.getProdId(), finprodType,null);
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
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
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
    private StlOrders getStlOrders(ValApplyPurTade dto){
    	StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getSettleDate());
    	result.setSettleAmt(dto.getPurAmt());
    	return result;
    }

    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValApplyPurTade
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValApplyPurTade dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 = A.成交编号
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        result.setTradeId(tradeId);
        
        // 业务类型 = A.业务类型
        result.setBusiType(dto.getBusiType());
        // 金融产品代码 = A.资产代码
        result.setFinprodId(dto.getAssetId());
	  	String finprodType = DictCodeListExt1.金融产品类型_基金.getValue();
	  	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
	  	if(item!=null){
	  		finprodType = item.getFinprodType();
	  	}
	  	result.setFinprodType(finprodType);
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        result.setTradeDate(dto.getTradeDate());
        //交割日= A.交易日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种 = A.币种
        result.setCcy(dto.getCcy());
        // 交易金额 = A.申购金额
        result.setTradeAmt(dto.getPurAmt());
        // 交易方向 = T04 申购
        result.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易对手= A.交易对手
        result.setCounterId(dto.getCounterId());
        // 交易场所=A.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        /*modify by sunshuaishuai 交易管理 20220324 start*/
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getOutTradeId());
        /*modify by sunshuaishuai 20220324 end*/
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        /*add by sunshuaishuai 添加交易份额 20210430 start*/
        result.setShareAmt(dto.getPurAmt());
        /*add by sunshuaishuai 添加交易份额 20210430 end*/
        /*add by sunshuaishuai 交易管理 20220324 start*/
        //资金账户
        result.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220324 end*/
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValApplyPurTade
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValApplyPurTade dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = A.产品代码
        result.setPortfolioId(dto.getProdId());
        //生成方式 = 接口接入
        result.setGenType(InterfCommonConstant.接入方式);
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }
}
