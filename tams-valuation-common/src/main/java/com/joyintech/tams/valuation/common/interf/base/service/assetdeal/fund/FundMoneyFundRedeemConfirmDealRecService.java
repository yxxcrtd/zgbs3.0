/*
 * 文件名：FundMoneyFundRedeemConfirmDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundMnfRedAffirm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 03货币基金赎回确认交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see FundMoneyFundRedeemConfirmDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundMoneyFundRedeemConfirmDealRecService {

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
        List<ValFundMnfRedAffirm> listValFundMnfRedAffirm = new ArrayList<ValFundMnfRedAffirm>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValFundMnfRedAffirm = returnResult.getT(ValFundMnfRedAffirm.class);
        // 获取数据数量
        result.setDataNum(listValFundMnfRedAffirm.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValFundMnfRedAffirm(listValFundMnfRedAffirm,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            
            result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
            
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
     * @param listValFundMnfRedAffirm List<ValFundMnfRedAffirm>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValFundMnfRedAffirm(List<ValFundMnfRedAffirm> listValFundMnfRedAffirm,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValFundMnfRedAffirm item : listValFundMnfRedAffirm) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	item.setCreateTime(resovleDate);
        	TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);
            
            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                valFinProductDeal, item.getProdId());
            
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
            		interfProductDealBookCommonDto.getAcctSubjectType());
            listValFinProductDealExt.add(valFinProductDealExt);
            
            //四级科目尾号
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(item.getAssetId());
            //四级科目尾号名称
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(item.getAssetName());
            listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());
            listValBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
            
            
            List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(item,valFinProductDeal);
            listValTradeFeeDetail.addAll(listfeeDetail);
            
            //结算指令
            Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, 
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
            
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(item, interfProductDealBookCommonDto.getAcctSubjectType());
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
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        
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
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValFundMnfRedAffirm dto,String acctSubjectType){
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
     * 接收 金融产品交易 T1
     * 
     * @param dto ValFundMnfRedAffirm
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValFundMnfRedAffirm dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_赎回确认.getValue(),dto.getTradeDate(),"A");
        result.setTradeId(tradeId);
        
        // 业务类型 = 06 货币基金交易
        result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        // 金融产品代码 = C.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
        result.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = C.交易日
        result.setTradeDate(dto.getTradeDate());
        // 交割日 = C.交易日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种 = C.币种
        result.setCcy(dto.getCcy());
        // 交易份额 = C.赎回份额
        result.setShareAmt(dto.getRedShare());
        // 交易本金 =C.赎回份额
        result.setPrinAmt(dto.getRedShare());
        // 利息总额 = C.实收红利
        result.setIntAmt(dto.getActrulBonus());
        
        //随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        
        //不随交易支付费用
        BigDecimal unFeeAmtPay = BigDecimal.ZERO;
        
        //赎回费
        feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay,dto.getFee());
        if(DictCodeList.是否_是.getValue().equals(dto.getIsCommission())){
        	feeAmtPay = ArithmeticUtil.doAdd(feeAmtPay,dto.getCommission());
        }else{
        	unFeeAmtPay = ArithmeticUtil.doAdd(unFeeAmtPay,dto.getCommission());
        }
        // 随交易支付费用 = C.费用
        result.setFeeAmtPay(feeAmtPay);
        
        // 不随交易支付费用
        result.setFeeAmtUnpay(unFeeAmtPay);
        
        // 总交易费用 = 随交易支付费用 + 不随交易支付费用
        result.setFeeAmt(ArithmeticUtil.doAdd(feeAmtPay,unFeeAmtPay));
        
        //交易金额 = B.确认金额（不包括费用)
        result.setTradeAmt(ArithmeticUtil.subtract(dto.getRedAmt(), BigDecimal.ZERO));
        
        
        // 交易方向 = T19 赎回确认
        result.setPs(DictCodeList.交易方向_赎回确认.getValue());
        // 交易状态 = C.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所 = C.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        /*modify by sunshuaishuai 交易管理 20220324 start*/
        // 关联系统交易编号= C.成交编号
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

        /*add by sunshuaishuai 交易管理 20220324 start*/
        //资金账户
        result.setMyAcctNo(dto.getMyAcctNo());
        //单位净价
        result.setUnitCprice(dto.getUnitCprice());
        /*add by sunshuaishuai 20220324 end*/
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValFundMnfRedAffirm
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValFundMnfRedAffirm dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = C.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        result.setProdId(dto.getProdId());
        **/
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	result.setPortfolioId(dto.getProdId());
//        }else{
//        	result.setProdId(dto.getProdId());
//        }
        result.setPortfolioId(dto.getProdId());
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = C.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //生成方式
        result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }
    /**
     * 接收 交易费用明细表 T5
     * 
     * @param dto ValFundMnfRedAffirm
     * @return ValTradeFeeDetail
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValFundMnfRedAffirm dto,TrdProductDeal deal) {
    	List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
        Map<String,String> mapIsPay = new HashMap<String,String>();
        //佣金
        map.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getCommission());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getIsCommission());
         
    	map.put(DictCodeListExt1.一次性费用类型_赎回费.getValue(), dto.getFee());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_赎回费.getValue(), DictCodeList.是否_是.getValue());
        
        for (String key : map.keySet()) {
        	BigDecimal amt = map.get(key);
            if(amt==null || BigDecimal.ZERO.compareTo(amt)==0){
        		continue;
        	}
        	TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(deal.getTradeId());
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
