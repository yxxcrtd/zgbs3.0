/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.stock;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValStockDeal;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 股票买卖交易 模板处理Service(VAL_STOCK_DEAL)
 * 
 * @author weipeng
 * @version 1.0 2018年6月27日
 * @see StockDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class StockDealTemplateRecService {

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
     * 股票买卖交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 股票买卖交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValStockDeal> listValStockDeal = new ArrayList<ValStockDeal>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        
        listValStockDeal = returnResult.getT(ValStockDeal.class);
        //获取数据数量
        result.setDataNum(listValStockDeal.size());
        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValStockDeal(listValStockDeal,acctEntity.getResovleDate(),params,returnResult.getValInterflog(), acctEntity);

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
        /**add by wujie on 20180712 start**/
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        /**add by wujie on 20180712 end**/
        
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
            "ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        //流通受限信息list
        List<TrdTradeNewStockLocked> listTrdTradeNewStockLocked=(List<TrdTradeNewStockLocked>)map.get("listTrdTradeNewStockLocked");
        result.setListTrdTradeNewStockLocked(listTrdTradeNewStockLocked);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-买卖交易
     * 
     * @param listValStockDeal 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> handleValStockDeal(List<ValStockDeal> listValStockDeal,Date resovleDate,Map<String,Object> params,
			List<ValInterflog> valInterflog, TemplateOrderInfoReaderResult acctEntity) {
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

        // 账务明细代码表List
        List<TrdTradeNewStockLocked> listTrdTradeNewStockLocked = new ArrayList<TrdTradeNewStockLocked>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();

        // 查询交易类型和交易方向映射配置表
        List<FinProductTypeConfig> configList = interfCommonService.getConfigListByType(DictCodeListExt4.配置类型_股票交易类型_TO_交易方向.getValue());
        Map<String, String> configMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(configList)){
            for(FinProductTypeConfig item : configList){
                configMap.put(item.getInputValue1(), item.getOutputValue1());
            }
        }
        else{
            return resultMap;
        }
        for (ValStockDeal dto : listValStockDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}

        	// 增加交易方向映射关系判断，若无映射关系，直接跳过
            String ps = configMap.get(dto.getStockTradeType());
            if(ps == null){
                // 若为空，则直接返回
                ValInterflog checkLog = new ValInterflog();
                checkLog.setTmpName(acctEntity.getTmpEName());// 模板名称
                checkLog.setFileName(acctEntity.getTmpCName());// 文件名称
                checkLog.setExDate(new Date());// 日期
                checkLog.setParentSeqno(acctEntity.getParentSeqno());// 主副日志表连接键
                checkLog.setDetail("交易类型未映射到对应交易方向，请添加对应匹配关系！");
                checkLog.setNum((long) listValStockDeal.indexOf(dto));
                checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                valInterflog.add(checkLog);
                continue;
            }
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto, ps);
            listValFinProductDeal.add(valFinProductDeal);

            dto.setTradeId(valFinProductDeal.getTradeId());
           
            // 入账信息表   账务明细表、账务明细代码表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
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
            valBokDetailId.setDetailSubnameTail(dto.getAssetAbbr());
            
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

            // 流通受限信息
            if(DictCodeList.是否_是.getValue().equals(dto.getIsLimited())){
                listTrdTradeNewStockLocked.add(this.getStockLockInfo(dto, valBokDetailId.getBooksetId()));
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
        resultMap.put("listTrdTradeNewStockLocked", listTrdTradeNewStockLocked);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }
    
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValStockDeal dto, TrdProductDeal valFinProductDeal){
    	StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo())){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setSettleAmt(valFinProductDeal.getTradeAmt());
    	return result;
    }
    
    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-买卖交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValStockDeal dto, String ps) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        //定义编号
        //String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getDealDate(),"A");
        /**由于股票交易不自动生成结算指令，需要根据上游交易编号来关联指令，交易编号直接取上游交易编号 update by yinya on 20211207**/
        String tradeId=null;
        if(dto.getDataActionSign().equals(DictCodeListExt1.交易状态_初始.getValue())){
            tradeId=dto.getTradeId();
        }else{
            tradeId=interfCommonService.getTradeid(ps, dto.getDealDate(),"A");
        }

        // 交易编号 = 共通生成
        valFinProductDeal.setTradeId(tradeId);
        // 交易场所为银行间则业务类型为股票交易，反之为交易场所股票
	    valFinProductDeal.setBusiType(DictCodeList.业务类型_股票交易.getValue());
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F10 股票
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
        // 分支序号 = 设置全局变量(默认为0）
        // 若流通受限不为空，则取对应预计上市流通日
        // 流通受限信息
        if(DictCodeList.是否_是.getValue().equals(dto.getIsLimited())){
            valFinProductDeal.setBranch(new Long(DateUtil.format(dto.getExpCirculateDate(), "YYYYMMDD")));
        }
        else{
            valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        }
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.交割日
        valFinProductDeal.setSettleDate(dto.getSettdateDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价 = A.成交价格
        valFinProductDeal.setUnitCprice(dto.getUnitCprice());
        //未随交易支付费用 = A.未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(dto.getFeeAmtUnpay());
        // 交易份额 = A.成交数量/100
        valFinProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getDealNum(), new BigDecimal(100),2));
        // 净价总额 = A.中签金额
        valFinProductDeal.setCpriceAmt(dto.getCprice());

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

        //过户费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsTransferAmt())){
            feeAmtPay = feeAmtPay.add(dto.getTransferAmt()==null?BigDecimal.ZERO:dto.getTransferAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getTransferAmt()==null?BigDecimal.ZERO:dto.getTransferAmt());
        }
        feeAmt = feeAmt.add(dto.getTransferAmt()==null?BigDecimal.ZERO:dto.getTransferAmt());

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
        if(DictCodeList.是否_是.getValue().equals(dto.getIsOtherFeeAmt())){
        	feeAmtPay = feeAmtPay.add(dto.getOtherFeeAmt() == null?BigDecimal.ZERO:dto.getOtherFeeAmt());
        }else{
        	feeAmtUnpay = feeAmtUnpay.add(dto.getOtherFeeAmt() == null?BigDecimal.ZERO:dto.getOtherFeeAmt());
        }
        feeAmt = feeAmt.add(dto.getOtherFeeAmt() == null?BigDecimal.ZERO:dto.getOtherFeeAmt());
        
        //随交易支付费用
        valFinProductDeal.setFeeAmtPay(feeAmtPay);
        
        //未随交易支付费用
        valFinProductDeal.setFeeAmtUnpay(feeAmtUnpay);
        
        //总交易费用
        valFinProductDeal.setFeeAmt(feeAmt);

        valFinProductDeal.setTradeAmt(dto.getTradeAmt());
        
        /**add by wujie on 20180925 end**/
        
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(ps);
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所 = A.交易场所
        valFinProductDeal.setTradeMarket(dto.getTradeMarket());
        // 关联系统交易编号 = A.外部成交编号
        valFinProductDeal.setfTradeId(dto.getOutTradeId());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        //是否生成结算指令 add by yinya on 20211201
        valFinProductDeal.setIsOrders(dto.getIsOrders());
        // 资金账户
        valFinProductDeal.setMyAcctNo(dto.getMyAcctNo());
        // 发行方式
        valFinProductDeal.setIssueType(dto.getDealIssueType());

        return valFinProductDeal;
    }

    /** 
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 01-买卖交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValStockDeal dto,
                                                         TrdProductDeal t1,String finprodType) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        /*modify by sunshuaishuai 20220413 判断生成方式是接口接入还是画面录入 start*/
        //生成方式
        if(StringUtil.isNotEmpty(dto.getDataSource())){
            valFinProductDealExt.setGenType(dto.getDataSource());
        }else {
            valFinProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        }
        /*modify by sunshuaishuai 20220413 判断生成方式是接口接入还是画面录入 end*/
        return valFinProductDealExt;
    }

    /**
     * T5: 交易费用明细表 接收
     * 
     * @param dto 01-买卖交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValStockDeal dto, TrdProductDeal valFinProductDeal) {
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

        //过户费
        map.put(DictCodeListExt1.一次性费用类型_过户费.getValue(), dto.getTransferAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_过户费.getValue(), dto.getIsTransferAmt());

        //佣金
        map.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getCommission());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_佣金.getValue(), dto.getIsCommission());
        
        //交易手续费
        map.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getDealFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getIsDealFeeAmt());
        
        //其他费用.
        map.put(DictCodeListExt1.一次性费用类型_其他费用.getValue(), dto.getOtherFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_其他费用.getValue(), dto.getIsOtherFeeAmt());

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
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            listValTradeFeeDetail.add(valTradeFeeDetail);
        }
        
        return listValTradeFeeDetail;
    }

    /**
     * 接收 股票流通受限信息
     * @param dto
     * @return
     */
    private TrdTradeNewStockLocked getStockLockInfo(ValStockDeal dto, String booksetId){
        TrdTradeNewStockLocked result = new TrdTradeNewStockLocked();
        result.setTradeId(interfCommonService.getTradeid(null, dto.getExpCirculateDate(),"A"));
        result.setBooksetId(booksetId);
        result.setFinprodId(dto.getAssetId());
        result.setCcy(dto.getCcy());
        result.setStartDate(dto.getLimitBeginDate());
        result.setEndDate(dto.getLimitEndDate());
        result.setShareAmt(dto.getDealNum());
        result.setUnitCprice(dto.getUnitCprice());
        result.setExpDate(dto.getExpCirculateDate());
        result.setTotalDays(dto.getLimitDays());
        if(StringUtil.isNotEmpty(dto.getDataSource())){
            result.setGenType(dto.getDataSource());
        }else {
            result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        }
        result.setmTradeId(dto.getTradeId());
        return result;
    }

}
