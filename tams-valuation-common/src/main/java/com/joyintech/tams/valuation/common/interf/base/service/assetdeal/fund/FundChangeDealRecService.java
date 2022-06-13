/*
 * 文件名：FundChangeDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：tanxuan
 * 修改时间：2020年01月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund;

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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundChangeDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 09 基金转换交易
 * 
 * @author tanxuan
 * @version 1.0 2020年01月10日
 * @see FundChangeDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundChangeDealRecService {

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
        List<ValFundChangeDeal> listValFundChangeDeal = new ArrayList<ValFundChangeDeal>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValFundChangeDeal = returnResult.getT(ValFundChangeDeal.class);
        
        // 获取数据数量
        result.setDataNum(listValFundChangeDeal.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/
        
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValFundChangeDeal(listValFundChangeDeal,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (!map.isEmpty()) {
        	// 金融产品交易List
            result.setListValFinProductDeal( (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            // 估值核算交易扩展表List
            result.setListValFinProductDealExt((List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            // 入账信息表List
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);
            // 账务明细表List
            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);
            // 账务明细代码表List
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get( "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            //交易费用明细表
            result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
            // 添加日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 09 基金转行交易
     * 
     * @param listValFundChangeDeal List<ValFundChangeDeal>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValFundChangeDeal(List<ValFundChangeDeal> listValFundChangeDeal,Date resovleDate,Map<String,Object> params,
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
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        //交易费用明细表
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        // 添加日志
        List<ValInterflog> valInterflogList =  new ArrayList<>();

        for (ValFundChangeDeal item : listValFundChangeDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
        	item.setCreateTime(resovleDate);
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);

            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());
            listValDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listValDealAccountingDetail.add(interfProductDealBookCommonDto.getBokDealAccountingDetail());

            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item,valFinProductDeal,
            		interfProductDealBookCommonDto.getAcctSubjectType());
            valFinProductDealExt.setTradeId(valFinProductDeal.getTradeId());
            listValFinProductDealExt.add(valFinProductDealExt);

            BokDetailId valBokDetailId = interfProductDealBookCommonDto.getBokDetailId();
            //四级科目尾号
            valBokDetailId.setDetailSubjectNoTail(item.getAssetId());
            //四级科目尾号名称
            valBokDetailId.setDetailSubnameTail(item.getAssetName());
            listValBokDetailId.add(valBokDetailId);
            
            //转让资产的账务明细代码生成
            BokDetailId valBokDetailId_Change = new BokDetailId();
            valBokDetailId_Change.setBooksetId(interfProductDealBookCommonDto.getBokDetailId().getBooksetId());
            valBokDetailId_Change.setBusiId(item.getcAssetId());
            valBokDetailId_Change.setBusiDetailType(DictCodeListExt1.金融产品类型_基金.getValue());
            valBokDetailId_Change.setTradeId(valFinProductDeal.getTradeId());
            valBokDetailId_Change.setCcy(valFinProductDeal.getCcy());
            valBokDetailId_Change.setBokDetailId(interfCommonService.genMd5BokDetailId(valBokDetailId_Change));
            BokDetailId change = interfCommonService.settingDetailSubjectNoTaiL(item.getcAssetId(),DictCodeListExt1.金融产品类型_基金.getValue() , null, valFinProductDeal.getBusiType(),valBokDetailId_Change.getBooksetId());
            valBokDetailId_Change.setDetailSubnameTail(change.getDetailSubnameTail());
            valBokDetailId_Change.setDetailSubjectNoTail(change.getDetailSubjectNoTail()==null?change.getDetailSubnameTail():change.getDetailSubjectNoTail());
       		listValBokDetailId.add(valBokDetailId_Change);
            
            List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(item,valFinProductDeal);
            listValTradeFeeDetail.addAll(listfeeDetail);

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
                
                if(feedealMap.get("TgTradeFeeDetail")!=null){
                    listValTradeFeeDetail.add((TrdTradeFeeDetail)feedealMap.get("TgTradeFeeDetail"));
                }
            }
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);

        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);

        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);

        return resultMap;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValFundChangeDeal dto,String acctSubjectType){
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
     * @param dto ValFundChangeDeal
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValFundChangeDeal dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 = 调用编号生成共通方法
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        result.setTradeId(tradeId);
        // 业务类型 = 根据转换出基金代码查询基金标的信息
        //  IF 基金标的的收益类型 = 净值型 数据字典：业务类型.非货币基金交易 ELSE 数据字典：业务类型.货币基金交易
	  	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
        if(item!=null && DictCodeList.收益类型_净值型.getValue().equals(item.getProfitType())){
        	result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        }else{
        	result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        }
        // 金融产品代码 = I.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = 数据字典：金融产品类型.基金
	  	result.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = I.确认日
        result.setTradeDate(dto.getTradeDate());
        // 交割日= I.确认日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种 = A.币种
        result.setCcy(dto.getCcy());
        // 单位净价 = I.单位净值
        result.setUnitCprice(dto.getUnitCprice());
        // 交易份额 = I.交易份额
        result.setShareAmt(dto.getShareAmt());
        // 交易本金 = I.交易本金
        result.setPrinAmt(dto.getPrinAmt());

        /**add by wujie on 20210507 start**/
        result.setIntAmt(dto.getIntAmt());
        /**add by wujie on 20210507 end**/
        // 随交易支付费用 = IF  I.通道费是否随交易支付 =“是” I.交易费 + I.通道费  ELSE I.交易费
        BigDecimal chlFeeAmt = (dto.getChlFeeAmt() == null) ? BigDecimal.ZERO : dto.getChlFeeAmt();
        /** modify by yanya on 20210207 交易费为空时用0处理 start**/
        BigDecimal feeAmt = (dto.getFeeAmt() == null) ? BigDecimal.ZERO : dto.getFeeAmt();
        /** modify by yanya on 20210207 交易费为空时用0处理 end**/
        if(DictCodeList.是否_是.getValue().equals(dto.getIsChlFeeAmt())){
        	//result.setFeeAmtPay(dto.getFeeAmt().add(chlFeeAmt));
            result.setFeeAmtPay(feeAmt.add(chlFeeAmt));
        }else{
        	//result.setFeeAmtPay(dto.getFeeAmt());
            result.setFeeAmtPay(feeAmt);
        }
        // 交易金额 = I.交易金额
        result.setTradeAmt(dto.getTradeAmt());
        // 未随交易支付费用 = IF  I.通道费是否随交易支付 =“否” I.通道费  
        if(DictCodeList.是否_否.getValue().equals(dto.getIsChlFeeAmt())){
        	result.setFeeAmtPay(dto.getChlFeeAmt());
        }
        /** modify by yanya on 20210207 交易费为空时用0处理 start**/
        // 总交易费用 =  I.交易费 + I.通道费
        //result.setFeeAmt(dto.getFeeAmt().add(chlFeeAmt));
        result.setFeeAmt(feeAmt.add(chlFeeAmt));
        /** modify by yanya on 20210207 交易费为空时用0处理 end**/
        // 交易方向 = I.交易方向
        result.setPs(dto.getPs());
        // 交易状态 = I.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 关联系统交易编号 = I.成交编号
        result.setfTradeId(dto.getTradeId());
        // 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        //通道代码 = I.通道代码
        result.setChlId(dto.getChannelId());
        // 交易场所= I.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());

        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValFundChangeDeal
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValFundChangeDeal dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 投资组合代码 = I.产品代码
        result.setPortfolioId(dto.getProdId());
        //生成方式 = 接口接入
        result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        //转让资产代码 = I.转让资产代码
        result.setrFinprodId(dto.getcAssetId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        
        return result;
    }
    
    /**
     * 接收 交易费用明细表 T5
     * 
     * @param dto ValAffirmPurTrade
     * @return ValTradeFeeDetail
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValFundChangeDeal dto,TrdProductDeal deal) {
    	List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
        Map<String,String> mapIsPay = new HashMap<String,String>();
        //佣金
        map.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getCommission());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getIsCommission());
        
        if(DictCodeListExt3.交易方向_转换申购确认.getValue().equals(dto.getPs())){
        	 map.put(DictCodeListExt1.一次性费用类型_申购费.getValue(), dto.getFeeAmt());
             mapIsPay.put(DictCodeListExt1.一次性费用类型_申购费.getValue(), DictCodeList.是否_是.getValue());
        }
        else{
        	 map.put(DictCodeListExt1.一次性费用类型_赎回费.getValue(), dto.getFeeAmt());
             mapIsPay.put(DictCodeListExt1.一次性费用类型_赎回费.getValue(), DictCodeList.是否_是.getValue());
        }
        
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
