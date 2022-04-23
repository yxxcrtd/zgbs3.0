/*
 * 文件名：FundNMoneyFundRedeemConfirmDealRecService.java
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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotAstTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundNRedAffirmN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 04净值型基金赎回确认交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see FundNMoneyFundRedeemConfirmDealRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundNMoneyFundRedeemConfirmDealRecService {

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
        List<ValFundNRedAffirmN> listValFundNRedAffirmN = new ArrayList<ValFundNRedAffirmN>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValFundNRedAffirmN = returnResult.getT(ValFundNRedAffirmN.class);
        
        // 获取数据数量
        result.setDataNum(listValFundNRedAffirmN.size());
        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构

        Map<String, Object> map = this.handleValFundNRedAffirmN(listValFundNRedAffirmN,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
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
     * @param listValFundNRedAffirmN List<ValFundNRedAffirmN>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    public Map<String, Object> handleValFundNRedAffirmN(List<ValFundNRedAffirmN> listValFundNRedAffirmN,Date resovleDate,Map<String,Object> params,
    		List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValFundNRedAffirmN item : listValFundNRedAffirmN) {
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

            /*modify by sunshuaishuai 20220415 交易管理：录入界面的资金账户 start*/
            //结算指令
            StlOrders stlOrders = this.getStlOrders(item,valFinProductDeal);
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders,
                    item.getProdId(), interfProductDealBookCommonDto.getAcctSubjectType(),null);
            /*modify by sunshuaishuai 20220415 交易管理：录入界面的资金账户 end*/
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
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValFundNRedAffirmN dto,String acctSubjectType){
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
    
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String booksetId,String acctSubjectType) {
//        //交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(prodId); // * 金融产品代码
//        /**modify by wujie on 20190107 start
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId));
//        **/
//        productDealDto.setFinprodType(acctSubjectType);
//        /**modify by wujie on 20190107 end**/
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(prodId); //  * 理财产品代码
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//        productDealDto.setBookNo(booksetId);
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        String insType=  DictCodeListExt1.指令类型_非货币基金及净值型资产_赎回确认.getValue();
//        return interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_是.getValue());
//    }
    
//    /************************add by wujie on 20180731 start**************************************************/
//    private Map<String, Object> getvalOrdersApplay(TrdProductDeal valFinProductDeal,String prodId,String bookSetId) {
//    	//交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(prodId); // * 金融产品代码
//        productDealDto.setFinprodType(interfCommonService.queryProductType(prodId)); 
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(prodId); //  * 理财产品代码
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_否.getValue());   //  * 是否随交易出账
//        productDealDto.setBookNo(bookSetId);
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        String insType=  DictCodeListExt1.指令类型_非货币基金_赎回到账.getValue();
//        //查询资产类型是否为计划类净值型资产
//        /** modify by wujie on 20181203 start
//    	TargetFinProduct tfp = interfCommonService.getTargetFinProduct(valFinProductDeal.getFinprodId());
//    	if(tfp != null){
//    		if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(tfp.getFinprodType())
//    				&& DictCodeList.收益类型_净值型.getValue().equals(tfp.getProfitType())){
//    			insType=  DictCodeListExt1.指令类型_计划类净值型资产_赎回到账.getValue();
//    		}
//    	}
//    	**/
//        if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(valFinProductDeal.getFinprodType())){
//        	insType=  DictCodeListExt1.指令类型_计划类净值型资产_赎回到账.getValue();
//    	}
//        /**modify by wujie on 20181203 end**/
//        return interfCommonService.settlementInstructionGeneration(productDealDto, insType,DictCodeList.是否_否.getValue());
//    }
//    /************************add by wujie on 20180731 end****************************************************/

    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValFundNRedAffirmN
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValFundNRedAffirmN dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号 
        String tradeId=interfCommonService.getTradeid(DictCodeList.交易方向_赎回确认.getValue(),dto.getTradeDate(),"A");
        result.setTradeId(tradeId);
        
        // 业务类型 = 07 非货币基金交易
        result.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        // 金融产品代码 = D.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F06 基金
	  	String finprodType = DictCodeListExt1.金融产品类型_基金.getValue();
	  	TargetFinProduct item = interfCommonService.getTargetFinProduct(dto.getAssetId());
	  	if(item!=null){
	  		finprodType = item.getFinprodType();
	  	}
	  	result.setFinprodType(finprodType);
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= D.交易日
        result.setTradeDate(dto.getTradeDate());
        // 交割日 = D.交易日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种= D.币种
        result.setCcy(dto.getCcy());
        // 净值日期 =D.净值日
        result.setCpriceDate(dto.getNetDate());
        // 单位净价= D.赎回净值
        result.setUnitCprice(dto.getRedNet());
        // 交易份额= D.赎回份额
        result.setShareAmt(dto.getRedShare());
        //全价总额 = D.赎回份额*D.赎回净值
         BigDecimal fpriceAmt = dto.getRedShare()==null?BigDecimal.ZERO:dto.getRedShare();
         fpriceAmt = fpriceAmt.multiply(dto.getRedNet()==null?BigDecimal.ZERO:dto.getRedNet());
         result.setFpriceAmt(fpriceAmt);
        
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
        
        // 交易金额 = D.赎回金额（赎回份额*赎回净值-赎回费用）
        result.setTradeAmt(ArithmeticUtil.subtract(dto.getRedAmt(), BigDecimal.ZERO));
        
        
        
        
        
        // 交易方向 =T19 赎回确认
        result.setPs(DictCodeList.交易方向_赎回确认.getValue());
        // 交易状态 = D.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 设置默认值
        result.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所 =D.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        /*modify by sunshuaishuai 交易管理 20220330 start*/
        // 关联系统交易编号 = D.成交编号
        result.setfTradeId(dto.getOutTradeId());
        /*modify by sunshuaishuai 交易管理 20220330 end*/
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	result.setpTradeId(interfCommonService.getFTradeId(result));
        }
      //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());

        /*add by sunshuaishuai 交易管理 20220330 start*/
        //资金账户
        result.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220330 end*/
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValFundNRedAffirmN
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValFundNRedAffirmN dto,TrdProductDeal deal,String finprodType) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = D.产品代码
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
        // 是否有结算指令 =D.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        /*modify by sunshuaishuai 20220414 判断生成方式是接口接入还是画面录入 start*/
        //生成方式
        if(StringUtil.isNotEmpty(dto.getDataSource())){
            result.setGenType(dto.getDataSource());
        }else {
            result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        }
        /*modify by sunshuaishuai 20220414 判断生成方式是接口接入还是画面录入 end*/
        return result;
    }
    /**
     * 接收 交易费用明细表 T5
     * 
     * @param dto ValFundNRedAffirmN
     * @return ValTradeFeeDetail
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValFundNRedAffirmN dto, TrdProductDeal deal) {
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

    /*add by sunshuaishuai 20220415 交易管理：录入界面的资金账户 start*/
    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValFundNRedAffirmN dto, TrdProductDeal valFinProductDeal){
        StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) ){
            return null;
        }
        result.setMyAcctNo(dto.getMyAcctNo());
        //result.setCounterAcctNo(dto.getCounterAcctNo());
        result.setSettleDate(valFinProductDeal.getSettleDate());
        result.setSettleAmt(valFinProductDeal.getTradeAmt());
        return result;
    }
    /*add by sunshuaishuai 20220415 交易管理：录入界面的资金账户 end*/

}
