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
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecExecDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券行权交易 模板处理Service(VAL_SEC_EXEC_DEAL)
 * 
 * @author yanya
 * @version 1.0 2021年3月9日
 * @see SecExecDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecExecDealTemplateRecService {

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
     * 债券买卖交易 接口模板处理
     *
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券买卖交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSecExecDeal> listValSecExecDeal = new ArrayList<ValSecExecDeal>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        listValSecExecDeal = returnResult.getT(ValSecExecDeal.class);
        //获取数据数量
        result.setDataNum(listValSecExecDeal.size());

        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSecExecDeal(listValSecExecDeal, acctEntity.getResovleDate(), params, returnResult.getValInterflog());

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>) map.get("ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>) map.get("ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //金融产品实物交易
        List<TrdFinProductBarter> listTrdFinProductBarter = (List<TrdFinProductBarter>) map.get("ValFinProductBarter");
        result.setListValFinProductBarter(listTrdFinProductBarter);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>) map.get("ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>) map.get("ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>) map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);

        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>) map.get("ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>) map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>) map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        //行权申请更新list
        List<SecExecApplyDto> valSecExecApplyList=(List<SecExecApplyDto>) map.get("ValSecExecApply");
        result.setListSecExecApply(valSecExecApplyList);
        // 添加日志
        result.setValInterflog((List<ValInterflog>) map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }

    /**
     * 债券行权交易
     * @param listValSecExecDeal 债券行权交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	private Map<String, Object> handleValSecExecDeal(List<ValSecExecDeal> listValSecExecDeal, Date resovleDate, Map<String,Object> params,
			List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 金融产品实物交易List
        List<TrdFinProductBarter> listValFinProductBarter = new ArrayList<TrdFinProductBarter>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
         //结算指令list
        List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
        //结算指令拓展list
        List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        // 行权申请更新List
        List<SecExecApplyDto> listSecExecApply = new ArrayList<SecExecApplyDto>();

        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValSecExecDeal dto : listValSecExecDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            dto.setTradeId(valFinProductDeal.getTradeId());

            // 入账信息表   账务明细表、账务明细代码表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
            String booksetId = dto.getProdId();//账套代码
            Date bookDate = dto.getDealDate();//入账日
            if(dealAccounting.getBokDealAccounting() != null) {
                booksetId = dealAccounting.getBokDealAccounting().getBooksetId();
                bookDate = dealAccounting.getBokDealAccounting().getBookDate();
                //特殊处理回售申请的入账日，为：行权日
                if(StringUtil.equals(DictCodeListExt3.交易方向_行权回售申请.getValue(), dto.getPs())) {
                    bookDate = dto.getExecDate();
                    dealAccounting.getBokDealAccounting().setBookDate(bookDate);//行权日
                    dealAccounting.getBokDealAccounting().setBooksetDate(bookDate);//行权日
                    dealAccounting.getBokDealAccounting().setHappenDate(bookDate);//行权日
                }
                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            }
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            /** 行权申请更新dto**/
            SecExecApplyDto secExecApplyDto = this.getSecExecApplyDto(valFinProductDeal, booksetId, bookDate);
            listSecExecApply.add(secExecApplyDto);
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

            // 交易费用明细表
            List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(dto,valFinProductDeal);
            listValTradeFeeDetail.addAll(listfeeDetail);

            //结算指令
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
        resultMap.put("valOrdersList", valOrdersList);
        resultMap.put("valOrdersExtList", valOrdersExtList);
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValFinProductBarter", listValFinProductBarter);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("ValSecExecApply", listSecExecApply);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }

    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSecExecDeal dto, TrdProductDeal valFinProductDeal){
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

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSecExecDeal dto,String acctSubjectType){
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
     * T1:金融产品交易表 接收
     * @param dto 债券行权交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValSecExecDeal dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //定义编号
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        // 交易编号 = 共通生成
        valFinProductDeal.setTradeId(tradeId);
        // 业务类型：行权回售申为债券交易预处理，赎回为债券交易
	    valFinProductDeal.setBusiType(DictCodeList.业务类型_债券交易.getValue());
	    if(StringUtil.equals(DictCodeListExt3.交易方向_行权回售申请.getValue(), dto.getPs())) {
            valFinProductDeal.setBusiType(DictCodeList.业务类型_债券交易预处理.getValue());
        }
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F01 债券
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        valFinProductDeal.setBranch(interfCommonService.getBranch(dto.getExecDate(), DictCodeListExt3.分支类型_行权.getValue()));
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        // 起息日 = A.行权申请日
        valFinProductDeal.setVdate(dto.getVdate());
        // 到日期 = A.行权日
        valFinProductDeal.setMdate(dto.getExecDate());
        // 交割日 = A.预计/实际资金到账日
        valFinProductDeal.setSettleDate(dto.getSettleDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价 = A.净价
        valFinProductDeal.setUnitCprice(dto.getCprice());
        // 单位利息 = A.应收利息
        valFinProductDeal.setUnitInt(dto.getUnitInt());
        // 单位全价 = A.全价
        valFinProductDeal.setUnitFprice(dto.getFprice());
        //单位面值 = A.单位面值
        valFinProductDeal.setParValue(dto.getParValue());
        // 净价总额 = A.净价总额
        valFinProductDeal.setCpriceAmt(dto.getCpriceAmt());
        // 交易份额 = A.券面总额/100
        valFinProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getDealNum(), new BigDecimal(100),2));
        // 交易本金 = A.净价总额
        valFinProductDeal.setPrinAmt(dto.getCpriceAmt());
        // 利息总额 = A.应收利息总额
        valFinProductDeal.setIntAmt(dto.getIntAmt());
        //全价总额 = A.全价总额
        valFinProductDeal.setFpriceAmt(dto.getFpriceAmt());
        //随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        //未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        //总交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        //印花税
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStampDuty())){
            feeAmtPay = feeAmtPay.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        }
        feeAmt = feeAmt.add(dto.getStampDuty()==null?BigDecimal.ZERO:dto.getStampDuty());
        //经手费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsBrokerage())){
            feeAmtPay = feeAmtPay.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        }
        feeAmt = feeAmt.add(dto.getBrokerage()==null?BigDecimal.ZERO:dto.getBrokerage());
        //证管费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsCertfFee())){
            feeAmtPay = feeAmtPay.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        }
        feeAmt = feeAmt.add(dto.getCertfFee()==null?BigDecimal.ZERO:dto.getCertfFee());
        //结算费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStlAmt())){
            feeAmtPay = feeAmtPay.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        }
        feeAmt = feeAmt.add(dto.getStlAmt()==null?BigDecimal.ZERO:dto.getStlAmt());
        //佣金
        if(DictCodeList.是否_是.getValue().equals(dto.getIsCommission())){
            feeAmtPay = feeAmtPay.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        }
        feeAmt = feeAmt.add(dto.getCommission()==null?BigDecimal.ZERO:dto.getCommission());
        //交易手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }
        feeAmt = feeAmt.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        //结算手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsStlFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }
        feeAmt = feeAmt.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        if (dto.getChlId() == null) {
            // 通道费
            if (DictCodeList.是否_是.getValue().equals(dto.getIsChlFeeAmt())) {
                feeAmtPay = feeAmtPay.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
            }
            else {
                feeAmtUnpay = feeAmtUnpay.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
            }
            feeAmt = feeAmt.add(dto.getChlFeeAmt() == null ? BigDecimal.ZERO : dto.getChlFeeAmt());
        }
        //随交易支付费用 = 变量.随交易支付费用
        valFinProductDeal.setFeeAmtPay(feeAmtPay);
        //交易金额 = A.交易金额
        valFinProductDeal.setTradeAmt(dto.getTradeAmt());
        //未随交易支付费用 = 变量.未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(feeAmtUnpay);
        //总费用 = 变量.随交易支付费用 + 变量.未随交易支付费用
        valFinProductDeal.setFeeAmt(feeAmt);
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 投资目的 = A.投资目的
        valFinProductDeal.setInvAim(dto.getInvestAim());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        //通道代码 = A.通道代码
        valFinProductDeal.setChlId(dto.getChlId());
        //交易对手 = A.交易对手
        valFinProductDeal.setCounterId(dto.getCounterId());
        //交易员 = A.交易员
        valFinProductDeal.setTrader(dto.getTrader());
        //证券托管户 = A.证券托管户
        valFinProductDeal.setSecManageAcctId(dto.getSecManageAcctId());
        // 交易场所 = A.交易场所
        valFinProductDeal.setTradeMarket(dto.getTradeMarket());
        // 交易市场 = 数据字典。交易市场_二级市场
        valFinProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        return valFinProductDeal;
    }

    /**
     * T2:估值核算交易扩展表 接收
     *
     * @param dto 债券行权交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValSecExecDeal dto,
                                                         TrdProductDeal t1, String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        //生成方式
        valFinProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        return valFinProductDealExt;
    }

    /**
     * T5: 交易费用明细表 接收
     * 
     * @param dto 债券行权交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValSecExecDeal dto, TrdProductDeal valFinProductDeal) {
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
        Map<String,String> mapIsPay = new HashMap<String,String>();
        //经手费
        map.put(DictCodeListExt1.一次性费用类型_经手费.getValue(), dto.getBrokerage());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_经手费.getValue(), dto.getIsBrokerage());
        
        //印花税
        map.put(DictCodeListExt1.一次性费用类型_印花税.getValue(), dto.getStampDuty());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_印花税.getValue(), dto.getIsStampDuty());
        
        //证管费
        map.put(DictCodeListExt1.一次性费用类型_证管费.getValue(), dto.getCertfFee());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_证管费.getValue(), dto.getIsCertfFee());
        
        //结算费
        map.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getStlAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getIsStlAmt());
        
        //佣金
        map.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getCommission());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getIsCommission());
        
        //交易手续费
        map.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getDealFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getIsDealFeeAmt());
        
        //结算手续费
        map.put(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), dto.getStlFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), dto.getIsStlFeeAmt());
        
        /** add by wujie on 20190322 start **/
        if (dto.getChlId() == null) {
            /** add by wujie on 20190322 end **/
            // 通道费
            map.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getChlFeeAmt());
            mapIsPay.put(DictCodeListExt1.一次性费用类型_通道费.getValue(), dto.getIsChlFeeAmt());
            /** add by wujie on 20190322 start **/
        }
        /** add by wujie on 20190322 end **/
        
        
        for (String key : map.keySet()) {
        	BigDecimal amt = map.get(key);
            if(amt==null || BigDecimal.ZERO.compareTo(amt)==0){
        		continue;
        	}
        	TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
            valTradeFeeDetail.setFeeType(key);
            // 是否随交易支付 = 固定值：Y 是 默认随交易支付
            valTradeFeeDetail.setIsPayWithTrade(mapIsPay.get(key));
            // 对应金额
            valTradeFeeDetail.setFeeAmt(amt);
            valTradeFeeDetail.setFeeId(key);
            //是否作为待摊
            valTradeFeeDetail.setIsPrepaid(DictCodeList.是否_否.getValue());
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            listValTradeFeeDetail.add(valTradeFeeDetail);
        }
        
        return listValTradeFeeDetail;
    }

    /**
     * 构建债券行权申请更新dto
     * @param trdProductDeal
     * @param booksetId
     * @param bookDate
     * @return
     */
    private SecExecApplyDto getSecExecApplyDto(TrdProductDeal trdProductDeal, String booksetId, Date bookDate) {
        SecExecApplyDto secExecApplyDto = new SecExecApplyDto();
        /**金融产品代码**/
        secExecApplyDto.setFinprodId(trdProductDeal.getFinprodId());
        /**金融产品类型（估值核算）**/
        secExecApplyDto.setFinprodType(trdProductDeal.getFinprodType());
        /**投资目的**/
        secExecApplyDto.setInvAim(trdProductDeal.getInvAim());
        /**分支序号**/
        secExecApplyDto.setBranch(trdProductDeal.getBranch());
        /**业务类型**/
        secExecApplyDto.setBusiType(trdProductDeal.getBusiType());
        /**交易方向，买入、卖出、收息、还本、申购、赎回等**/
        secExecApplyDto.setPs(trdProductDeal.getPs());
        /**交易日期**/
        secExecApplyDto.setTradeDate(trdProductDeal.getTradeDate());
        /**账套代码**/
        secExecApplyDto.setBooksetId(booksetId);
        /**入账日**/
        secExecApplyDto.setBookDate(bookDate);
        return secExecApplyDto;
    }

}
