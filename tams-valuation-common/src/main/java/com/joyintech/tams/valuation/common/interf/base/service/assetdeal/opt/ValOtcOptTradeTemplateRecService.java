/*
 * 文件名：ValOtcOptTradeTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年01月10日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.opt;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValOtcOptTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 场外期权交易 模板处理Service
 * 
 * @author tanxuan
 * @version 1.0 2020年01月10日
 * @see ValOtcOptTradeTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValOtcOptTradeTemplateRecService {

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
     * 场外期权交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 场外期权交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ValOtcOptTradeCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
    	ValOtcOptTradeCommonResult result = new ValOtcOptTradeCommonResult();
        List<ValOtcOptTrade> listValOtcOptTrade = new ArrayList<ValOtcOptTrade>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValOtcOptTrade = returnResult.getT(ValOtcOptTrade.class);
        //获取数据数量
        result.setDataNum(listValOtcOptTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态, DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValOtcOptTrade(listValOtcOptTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

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
     * 03-场外期权交易
     * 
     * @param listValOtcOptTrade 
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List +账务明细代码表List+结算指令表List+结算指令拓展表list
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValOtcOptTrade(List<ValOtcOptTrade> listValOtcOptTrade,Date resovleDate,Map<String,Object> params,
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

        Map<String,TargetFinProduct> assetMap = new HashMap<String,TargetFinProduct>();
        
        for (ValOtcOptTrade dto : listValOtcOptTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            
            String acctSubjectType = null;
            
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            dto.setTradeId(valFinProductDeal.getTradeId());
                
            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(
                valFinProductDeal, dto.getProdId());
            
            TrdProductDealAdd valtrdProductDealExt = this.getValFinProductDealExt(dto,valFinProductDeal);
            listValFinProductDealExt.add(valtrdProductDealExt);
            
            //入账信息表List
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            // 账务明细表List
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            
            //四级科目尾号
            dealAccounting.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
            
            TargetFinProduct item = assetMap.get(dto.getAssetId());
            if(item==null){
            	item = interfCommonService.getTargetFinProduct(dto.getAssetId());
            	//四级科目尾号名称
            	if(item!=null){
                    dealAccounting.getBokDetailId().setDetailSubnameTail(item.getFinprodName());
                    assetMap.put(dto.getAssetId(), item);
                }
            }
            
            // 账务明细代码表
            listValBokDetailId.add(dealAccounting.getBokDetailId()) ;
            
            StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
            Map<String, Object> maps = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
            		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
            if(maps.get("ORDERS")!=null){
            	valOrdersList.addAll((List<StlOrders>)maps.get("ORDERS"));
            }
            if(maps.get("ORDERS_EXT")!=null){
            	valOrdersExtList.addAll((List<StlOrdersExt>)maps.get("ORDERS_EXT"));
            }
            if(maps.get("ACCOUNTING")!=null){
            	 listValDealAccounting.addAll((List<BokDealAccounting>)maps.get("ACCOUNTING"));
            }
            if(maps.get("ACCOUNTING_DETAI")!=null){
            	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)maps.get("ACCOUNTING_DETAI"));
            }
            if(maps.get("BOK_DETAIL_ID")!=null){
            	listValBokDetailId.addAll((List<BokDetailId>)maps.get("BOK_DETAIL_ID"));
            }
            
            acctSubjectType = dealAccounting.getAcctSubjectType();
    
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, acctSubjectType);
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
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
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
    private StlOrders getStlOrders(ValOtcOptTrade dto, TrdProductDeal valFinProductDeal){
    	StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getSettleDate());
    	result.setSettleAmt(valFinProductDeal.getTradeAmt());
    	return result;
    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 03-应收款转让交易
     * @param dto
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValOtcOptTrade dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //交易编号 =共通生成
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        valFinProductDeal.setTradeId(tradeId);
        // 业务类型 = 38 场外期权
        valFinProductDeal.setBusiType(DictCodeListExt3.业务类型_场外期权.getValue());
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型（投管用） = F35 金融产品类型（投管用）（期权）
        valFinProductDeal.setFinprodType(DictCodeListExt3.金融产品类型_投管用_期权.getValue());
        // 分支序号 = A.分支序号
        valFinProductDeal.setBranch(dto.getBranch());
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        //交割日 = A.交割日
        valFinProductDeal.setSettleDate(dto.getEffDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
    	// 交易份额 = A.交易份额
    	valFinProductDeal.setShareAmt(dto.getDealNum());
        // 全价总额 = A.全价总额
    	valFinProductDeal.setFpriceAmt(dto.getFpriceAmt());
        // 交易金额 = A.交易金额
    	valFinProductDeal.setTradeAmt(dto.getTradeAmt());
        // 未随交易支付费用 = IF 数据字典：是否.否 eq A.通道费是否随交易支付  A.通道费
    	if(dto.getIsChlFeeAmt().equals(DictCodeListExt1.是否_否.getValue())){
    		valFinProductDeal.setFeeAmtUnpay(dto.getChlFeeAmt());
    	}
        // 总交易费用费用 = IF 数据字典：是否.否 eq A.通道费是否随交易支付  A.通道费
    	if(dto.getIsChlFeeAmt().equals(DictCodeListExt1.是否_否.getValue())){
    		valFinProductDeal.setFeeAmt(dto.getChlFeeAmt());
    	}
        //交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
        //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        //通道代码 = C.通道代码
        valFinProductDeal.setChlId(dto.getChannelId());
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        valFinProductDeal.setCreateDept(null);
        
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 03 应收款转让交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValOtcOptTrade dto,TrdProductDeal t1) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 投资组合代码
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        return valFinProductDealExt;
    }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValOtcOptTrade dto,String acctSubjectType){
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
