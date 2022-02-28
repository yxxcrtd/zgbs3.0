/*
 * 文件名：FundCashDividendDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValCashBonusTrade;

/**
 * 05现金分红交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see FundCashDividendDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundCashDividendDealRecService {

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
        List<ValCashBonusTrade> listValCashBonusTrade = new ArrayList<ValCashBonusTrade>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValCashBonusTrade = returnResult.getT(ValCashBonusTrade.class);
        
        // 获取数据数量
        result.setDataNum(listValCashBonusTrade.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构

        Map<String, Object> map = this.handleValCashBonusTrade(listValCashBonusTrade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
           
            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                "listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                "listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            
            result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                    "ValDealAccounting"));
            
            result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail"));
            
            result.setListValBokDetailId((List<BokDetailId>)map.get(
                    "ValBokDetailId"));
            
         // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 
     * @param listValCashBonusTrade List<ValCashBonusTrade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValCashBonusTrade(List<ValCashBonusTrade> listValCashBonusTrade,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        
        for (ValCashBonusTrade item : listValCashBonusTrade) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	
        	item.setCreateTime(resovleDate);
        	TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);
            
            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                valFinProductDeal, item.getProdId());
            
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,interfProductDealBookCommonDto.getAcctSubjectType());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            
            
            //四级科目尾号
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(item.getAssetId());
            //四级科目尾号名称
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(item.getAssetName());
            
            listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
            listValBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
           
            
            //结算指令
            StlOrders stlOrders = this.getStlOrders(item);
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
                		item.getProdId(), interfProductDealBookCommonDto.getAcctSubjectType(),null);
            
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
            
            /**杭州银行专属：非货币基金现金分红时，生成一笔除权登记交易
             * add by wujie on 20180731 start
             */
            if(!DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue().equals(valFinProductDeal.getBusiType())){
            	valFinProductDeal = this.getValFinProductDealCQ(item,valFinProductDeal.getFinprodType());
            	
                listValFinProductDeal.add(valFinProductDeal);
                
                interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
                
                valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
                		interfProductDealBookCommonDto.getAcctSubjectType());
                listValFinProductDealExt.add(valFinProductDealExt);
                
                //四级科目尾号
                interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(item.getAssetId());
                //四级科目尾号名称
                interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(item.getAssetName());
                
                listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
                listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
                listValBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
            }
            
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(item, interfProductDealBookCommonDto.getAcctSubjectType());
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
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        
        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
        return resultMap;
    }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValCashBonusTrade dto,String acctSubjectType){
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
    
    /********************************add by wujie on 20180731 start***********************************/
    /**
     * 红利除权交易
     */
    private TrdProductDeal getValFinProductDealCQ(ValCashBonusTrade dto,String finprodType) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_除权登记.getValue(),dto.getBonusDate(),"A");
        result.setTradeId(tradeId);
        // 业务类型 
        result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        // 金融产品代码 = E.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
        result.setFinprodType(finprodType);
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = E.分红日
        result.setTradeDate(dto.getBonusDate());
        // 交割日  = E.分红日
        result.setSettleDate(dto.getBonusDate());
        //到期日 =分红截止日
        result.setMdate(dto.getMdate());
        // 交易币种 = E.币种
        result.setCcy(dto.getCcy());
        // 交易方向 = T11 现金分红
        result.setPs(DictCodeList.交易方向_除权登记.getValue());
        // 交易状态 = E.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = E.成交编号
        result.setfTradeId(dto.getTradeId());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //利息总额=实际分红金额
        result.setIntAmt(dto.getActBounsAmt());
        //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        return result;
    }
    /********************************add by wujie on 20180731 end***********************************/

    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValCashBonusTrade dto){
    	StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getSettleDate());
    	result.setSettleAmt(dto.getActBounsAmt());
    	return result;
    }

    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValCashBonusTrade
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValCashBonusTrade dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_现金分红.getValue(),dto.getBonusDate(),"A");
        result.setTradeId(tradeId);
        
        /** modify by wujie on 20181203
        // 业务类型 
        if(DictCodeList.基金类型_货币基金.getValue().equals(dto.getFundType())){
        	result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        }else{
        	result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        }
        **/
        
        // 金融产品代码 = E.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
        /**add by wujie on 20181203 start remark:根据资产代码获取资产对应的金融产品类型，默认为基金**/
        //result.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
	  	String finprodType = DictCodeListExt1.金融产品类型_基金.getValue();
	  	String profitType = DictCodeList.收益类型_货币型.getValue();
	  	result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
	  	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
	  	if(item!=null){
	  		finprodType = item.getFinprodType();
	  		profitType = item.getProfitType();
	  	}
	  	if(DictCodeListExt1.金融产品类型_基金.getValue().equals(finprodType)
	  			&& DictCodeList.收益类型_货币型.getValue().equals(profitType)){
	  		result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
	  	}else{
	  		result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
	  	}
	  	result.setFinprodType(finprodType);
	  	/**add by wujie on 20181203 end**/
        
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = E.分红日
        result.setTradeDate(dto.getBonusDate());
        //到期日 =分红截止日
        result.setMdate(dto.getMdate());
        // 交割日  = E.分红日
        result.setSettleDate(dto.getBonusDate());
        // 交易币种 = E.币种
        result.setCcy(dto.getCcy());
        // 交易金额 = E.实际分红金额
        result.setTradeAmt(dto.getActBounsAmt());
        // 交易方向 = T11 现金分红
        result.setPs(DictCodeList.交易方向_现金分红.getValue());
        // 交易状态 = E.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = E.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //利息总额=实际分红金额
        result.setIntAmt(dto.getActBounsAmt());
        /**add by wujie on 20210507 start**/
        result.setTradeMarket(dto.getTradeMarket());
        /**add by wujie on 20210507 end**/

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
     * @param dto ValCashBonusTrade
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValCashBonusTrade dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = E.成交编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = E.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        valFinProductDealExt.setProdId(dto.getProdId());
        **/
        result.setPortfolioId(dto.getProdId());
        
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	result.setPortfolioId(dto.getProdId());
//        }else{
//        	result.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = E.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //生成方式
        result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }
}
