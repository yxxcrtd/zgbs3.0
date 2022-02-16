package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券借贷交易 模板处理Service(VAL_SECLEND_DEAL)
 * 
 * @author dongxiangyang
 * @version 1.0 2018年10月29日
 * @see SecblTranDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecblTranDealTemplateRecService {

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
     * 债券借贷交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券买卖交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
            TemplateOrderInfoReaderResult acctEntity) {
    	// 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSeclendDeal> listValSeclendDeal=new ArrayList<>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        listValSeclendDeal=returnResult.getT(ValSeclendDeal.class);
        
        //获取数据数量
        result.setDataNum(listValSeclendDeal.size());

        /**add by wujie on 20210502 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210502 end**/
        
        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSeclendDeal(listValSeclendDeal,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
   	    // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal=(List<TrdProductDeal>)map.get("listValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);

        // 费用明细表
        List<TrdTradeFeeDetail> listValTrdTradeFeeDetail=(List<TrdTradeFeeDetail>) map.get("listValTrdTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTrdTradeFeeDetail);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt=(List<TrdProductDealAdd>)map.get("listValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting=(List<BokDealAccounting>)map.get("listValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        //账务明细list
        List<BokDealAccountingDetail> listValDealAccountingDetail=(List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=(List<BokDetailId>)map.get("listValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        //List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        //List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(valOrdersExtList);
        return result;
    }
    /**
     * 01-债券借贷交易
     * 
     * @param listValSeclendDeal 
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List 
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public  Map<String, Object> handleValSeclendDeal(List<ValSeclendDeal> listValSeclendDeal,Date resovleDate,Map<String,Object> params,
			List<ValInterflog> valInterflog){
     List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
     String valModel = (String)params.get(InterfCommonConstant.接口模式);
   	 Map<String, Object> resultMap=new HashMap<>();
   	 // 金融产品交易List
     List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
     // 费用明细表list
      List<TrdTradeFeeDetail> listValTrdTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
     // 估值核算交易扩展表List
     List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
     // 入账信息表List
     List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
     // 账务明细表List
     List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
     //账务明细代码表
     List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
     //结算指令
     List<StlOrders> listValOrders = new ArrayList<StlOrders>();
     List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
     List<ValInterflog> valInterflogList =  new ArrayList<>();
     for(ValSeclendDeal dto:listValSeclendDeal){
     	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
     		continue;
     	}
    	 dto.setCreateTime(resovleDate);
    	 //金融产品交易
    	 TrdProductDeal valFinProductDeal=getValFinProductDeal(dto);
    	 listValFinProductDeal.add(valFinProductDeal);
    	 /** add by yanya on 20200122 增加费用明细表 start**/
         TrdTradeFeeDetail valTrdTradeFeeDetail = getValTrdTradeFeeDetail(dto, valFinProductDeal);
         listValTrdTradeFeeDetail.add(valTrdTradeFeeDetail);
         /** add by yanya on 20200122 增加费用明细表 end**/
    	
    	 // 入账信息表   账务明细表、账务明细代码表调用共通
         InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
         listValDealAccounting.add(dealAccounting.getBokDealAccounting());
         listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
         
         // 估值核算交易扩展表
         TrdProductDealAdd TrdProductDealExt=getProductDealExt(dto,valFinProductDeal,dealAccounting.getAcctSubjectType());
    	 listValFinProductDealExt.add(TrdProductDealExt);
         
         BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
         listValBokDetailId.add(valBokDetailId);
         
         //结算指令
         /**modify by wujie on 20190118 start
         BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
         Map<String, Object> map = this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
         if(map.get("ORDERS")!=null){
        	 listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
         }
         if(map.get("ORDERS_EXT") != null){
        	 listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
         }
         **/
         StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
         Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders, 
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
     
     resultMap.put("listValFinProductDeal", listValFinProductDeal);
     //费用明细表list
     resultMap.put("listValTrdTradeFeeDetail", listValTrdTradeFeeDetail);
     resultMap.put("listValFinProductDealExt", listValFinProductDealExt);
     resultMap.put("listValDealAccounting", listValDealAccounting);
     resultMap.put("listValDealAccountingDetail", listValDealAccountingDetail);
     resultMap.put("listValBokDetailId", listValBokDetailId);
     resultMap.put("listValOrders", listValOrders);
     resultMap.put("listValOrdersExt", listValOrdersExt);
     valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
 	 resultMap.put("valInterflogList", valInterflogList);
     
   	return resultMap;
   }
    
    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSeclendDeal dto,String acctSubjectType){
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
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSeclendDeal dto, TrdProductDeal valFinProductDeal){
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
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-借贷交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private  TrdProductDeal getValFinProductDeal(ValSeclendDeal dto){
    	TrdProductDeal trdProductDeal=new TrdProductDeal();
    	//业务类型=数据字典：业务类型.债券借贷交易
    	trdProductDeal.setBusiType(DictCodeList.业务类型_债券借贷交易.getValue());
    	//交易编号=调用共通
    	String tradeId=interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A");
    	trdProductDeal.setTradeId(tradeId);
    	//金融产品代码=金融产品代码
    	trdProductDeal.setFinprodId(dto.getFinprodId());
    	//金融产品类型=A.金融产品类型
    	trdProductDeal.setFinprodType(dto.getFinprodType());
    	// 分支序号 = 设置全局变量(默认为0）
    	trdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
    	//交易日=A.交易日
    	trdProductDeal.setTradeDate(dto.getTradeDate());
    	//起息日=A.起息日
    	trdProductDeal.setVdate(dto.getVdate());
    	//到期日=A.到期日
    	trdProductDeal.setMdate(dto.getMdate());
    	//交割日=A.交易日
    	trdProductDeal.setSettleDate(dto.getTradeDate());
    	//交易币种=A.币种
    	trdProductDeal.setCcy(dto.getCcy());
    	
    	/**
		 * 债券借贷融入
			买入：
			    交易本金:债券借贷标的债券券面总额
			    交易金额:0
			到期：
			    交易本金:债券借贷标的债券券面总额
			    交易份额:债券借贷标的债券券面总额
			    利息总额:交易费用总额
			    交易金额:交易费用总额
		 */
        if(DictCodeListExt1.金融产品类型_债券借贷融入.getValue().equals(dto.getFinprodType())){
            //交易本金=A.券面总额
            trdProductDeal.setPrinAmt(dto.getPrinAmt());

            //交易份额 = A.标的债券券面总额
            trdProductDeal.setShareAmt(dto.getPrinAmt());

            if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())){
                //交易金额= 0
                trdProductDeal.setTradeAmt(BigDecimal.ZERO);
            }else{

                //交易金额=A.借贷费用
                trdProductDeal.setTradeAmt(dto.getFee());
                //利息总额 = A.借贷费用
                trdProductDeal.setIntAmt(dto.getFee());
            }
        }
    	/**
		 * 债券借贷融出:
			买入：
			   交易本金:债券借贷标的债券券面总额
			   交易份额:债券借贷标的债券券面总额
			   交易金额:0
			到期：
			   交易本金:债券借贷标的债券券面总额
			   交易份额:债券借贷标的债券券面总额
			   利息总额:交易费用总额
			   交易金额:交易费用总额
		 */
    	else if(DictCodeListExt1.金融产品类型_债券借贷融出.getValue().equals(dto.getFinprodType())){
    		//交易本金=A.券面总额
	    	trdProductDeal.setPrinAmt(dto.getPrinAmt());
	    	//交易份额 = A.标的债券券面总额
	    	trdProductDeal.setShareAmt(dto.getPrinAmt());
    		
    		if(DictCodeList.交易方向_买入.getValue().equals(dto.getPs())){
    	    	//交易金额= 0
    	    	trdProductDeal.setTradeAmt(BigDecimal.ZERO);
    		}else{
    			//交易金额=A.借贷费用
            	trdProductDeal.setTradeAmt(dto.getFee());
            	//利息总额 = A.借贷费用
            	trdProductDeal.setIntAmt(dto.getFee());
    		}
    		
    	}
    	//交易方向=A.交易方向
    	trdProductDeal.setPs(dto.getPs());
    	//到期收益率=A.费率
    	trdProductDeal.setYield(dto.getFeeRate());
    	//交易状态=A.数据动作标志
    	trdProductDeal.setTradeStatus(dto.getDataActionSign());
    	//交易对手=A.交易对手
    	trdProductDeal.setCounterId(dto.getCounterId());
    	//交易对手类型=A.交易对手类型
//    	trdProductDeal.setCounterType(dto.getCounterType());
    	//交易场所=A.交易场所
    	trdProductDeal.setTradeMarket(dto.getTradeMarket());
    	//关联系统交易编号=A.成交编号
    	trdProductDeal.setfTradeId(dto.getTradeId());
    	
    	trdProductDeal.setCreateTime(dto.getCreateTime());
    	
        //原交易编号
        if(trdProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            //调用共通
            trdProductDeal.setpTradeId(interfCommonService.getFTradeId(trdProductDeal));
        }else{
            trdProductDeal.setpTradeId(null);
        }
        //通道代码 = a.通道代码
        trdProductDeal.setChlId(dto.getChannelId());

        /** add by yanya on 20200122 增加总交易费用、未随交易支付费用、登记托管机构 start**/
        //总交易费用 =  A.结算手续费
        trdProductDeal.setFeeAmt(dto.getStlFeeAmt());
        //未随交易支付费用 "IF A.结算手续费是否随交易支付 eq “否” A.结算手续费 END"
        if(DictCodeList.是否_否.getValue().equals(dto.getIsStlFeeAmt()))  {
            trdProductDeal.setFeeAmtUnpay(dto.getStlFeeAmt());
        }
        else {
            //随交易支付费用 "IF A.结算手续费是否随交易支付 eq “是” A.结算手续费 END"
            trdProductDeal.setFeeAmtPay(dto.getStlFeeAmt());
        }
        //登记托管机构 = A.登记托管机构
        trdProductDeal.setRegistOrg(dto.getRegistAgency());
        /** add by yanya on 20200122 增加总交易费用、未随交易支付费用、登记托管机构 end**/

    	return trdProductDeal;
    }

    /**
     * 根据接口数据生成结算手续费 费用明细
     * @param dto
     * @param trdProductDeal
     * @return
     */
    private TrdTradeFeeDetail getValTrdTradeFeeDetail(ValSeclendDeal dto, TrdProductDeal trdProductDeal) {
        //定义交易费用明细
        TrdTradeFeeDetail  trdTradeFeeDetail = new TrdTradeFeeDetail();
        //交易编号	=	T1.交易编号
        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
        //一次性费用类型=	B08 结算手续费
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_结算手续费.getValue());
        //是否随交易支付 = A.结算手续费是否随交易支付
        trdTradeFeeDetail.setIsPayWithTrade(dto.getIsStlFeeAmt());
        //费用金额	= A.结算手续费
        trdTradeFeeDetail.setFeeAmt(dto.getStlFeeAmt());
        //费用代码	=	B08 结算手续费
        trdTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_结算手续费.getValue());
        return trdTradeFeeDetail;
    }
    
    
    /**
     * T2:估值核算交易扩展表 接收
     * 
     * @param dto 01-债券借贷交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getProductDealExt(ValSeclendDeal dto,TrdProductDeal valFinProductDeal,String finprodType){
    	TrdProductDealAdd trdProductDealExt=new TrdProductDealAdd();
    	//交易编号=T1.交易编号
    	trdProductDealExt.setTradeId(valFinProductDeal.getTradeId());
    	//理财产品代码=A.产品代码
    	/** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        trdProductDealExt.setProdId(dto.getProdId());
        **/
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	trdProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
////        	trdProductDealExt.setProdId(dto.getProdId());
//        }
        trdProductDealExt.setPortfolioId(dto.getProdId());
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
    	//是否有结算指令
//    	trdProductDealExt.setHasOrder(dto.getIsClean());
    	//生成方式=固定值：接口接入
    	trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
    	
    	
    	return trdProductDealExt;
    }
//    /**
//     * 结算指令的生成
//     * @param valFinProductDeal
//     * @param valBooksetInfoDtoList
//     * @return
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String booksetId,String acctSubjectType) {
//    	 Map<String, Object> map = new HashMap<String,Object>();
//    	 List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//         List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//    	
//    	 ProductDealDto productDealDto=new ProductDealDto();
//    	 productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//         productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//         productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//         productDealDto.setEffDate(valFinProductDeal.getSettleDate()); //  * 交割日
//         productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//         productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//         productDealDto.setFinprodId(prodId); // * 金融产品代码
//         /**modify by wujie on 20190107 start
//         productDealDto.setFinprodType(interfCommonService.queryProductType(prodId));
//         **/
//         productDealDto.setFinprodType(acctSubjectType);
//         /**modify by wujie on 20190107 end**/
//         productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//         productDealDto.setProdId(prodId); //  * 理财产品代码
//         productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//         productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//         productDealDto.setBookNo(booksetId);
//         productDealDto.setCreateTime(valFinProductDeal.getCreateTime());
//         productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//         
//         if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_到期.getValue())
//    			 || valFinProductDeal.getPs().equals(DictCodeList.交易方向_卖出.getValue())){
//        	 String insType = "";
//        	 if(valFinProductDeal.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券借贷融出.getValue())){
//        		 insType=DictCodeListExt1.指令类型_债券借贷_融出到期.getValue();
//        	 }else{
//        		 insType=DictCodeListExt1.指令类型_债券借贷_融入到期.getValue();
//        	 }
//        	 map = interfCommonService.settlementInstructionGeneration(productDealDto, 
//        			 insType,DictCodeList.是否_是.getValue());
//        	 
//        	 StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//             StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//             listValOrders.add(ValOrders);
//             listValOrdersExt.add(ValOrdersExt);
//             
//             map.put("ORDERS", listValOrders);
//             map.put("ORDERS_EXT", listValOrdersExt);
//         }
//        
//         return map;
//    }
}
