/*
 * 文件名：NPrdBaseinfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：qiangchengxi
 * 修改时间：2018年6月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.nprddeal;

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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdPeriodDealN;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 净值型产品存续期交易表 模板处理Service(VAL_PRD_PERIOD_DEAL) 〈功能详细描述〉
 * 
 * @author 魏鹏
 * @version 1.0 2018年6月25日
 * @see NPrdDealPeriodDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NPrdDealPeriodDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private  InterfCommonService interfCommonService;


    /**
     * 净值型产品存续期交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 净值型产品交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public NProdDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        NProdDealInfoValCommonResult result = new NProdDealInfoValCommonResult();
        List<ValPrdPeriodDealN> listValPrdPeriodDealN = new ArrayList<ValPrdPeriodDealN>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdPeriodDealN = returnResult.getT(ValPrdPeriodDealN.class);
        
        result.setDatanum(listValPrdPeriodDealN==null?0:listValPrdPeriodDealN.size());
        if (listValPrdPeriodDealN == null) {
        	listValPrdPeriodDealN = new ArrayList<ValPrdPeriodDealN>();
        }
        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValPrdPeriodDealN(listValPrdPeriodDealN,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if(map!=null){
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
            //账务明细代码表
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            
            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
            result.setListValTradeFeeDetail(listValTradeFeeDetail);
            
            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                "listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                "listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-存续期交易
     * 
     * @param listValPrdPeriodDealN 净值型产品存续期交易
     * @param resovleDate 数据处理日期
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	private Map<String, Object> handleValPrdPeriodDealN(List<ValPrdPeriodDealN> listValPrdPeriodDealN,Date resovleDate, Map<String, Object> params,
			List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        
    	List<ValInterflog> valInterflogList =  new ArrayList<>();
    	
        for (ValPrdPeriodDealN dto : listValPrdPeriodDealN) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	dto.setCreateTime(resovleDate);//add by wujie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);

            // 入账信息表   账务明细表调用共通 
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(valFinProductDeal, 
            		dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
            /**
             * modify by wujie on 20181023 start remark:改为调用分层入账信息共通
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            listValBokDetailId.add(dealAccounting.getBokDetailId());
            */
            
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
            	listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingDetailList())) {
            	listValDealAccountingDetail.addAll(dealAccounting.getBokDealAccountingDetailList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDetailIdList())) {
            	//设置账务明细代码表的四级科目号
            	String detailSubjectNoTail = dto.getProdId();
                String detailSubnameTail = dto.getProdName();
                if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                    detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                    detailSubnameTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                }
            	/**add by wujie on 20190103 start**/
            	detailSubjectNoTail = detailSubjectNoTail+FinalString.LAYERING_SPLIT+dto.getFinprodType();
            	/**add by wujie on 20190103 end**/
                interfCommonService.setDetailSubjectNoTail(dealAccounting.getBokDetailIdList(), 
                		detailSubjectNoTail, detailSubnameTail);
            	listValBokDetailId.addAll(dealAccounting.getBokDetailIdList());
            }
            /**
             * modify by wujie on 20181023 end remark:改为调用分层入账信息共通
             */

            // 交易费用明细表
            TrdTradeFeeDetail valTradeFeeDetail = this.getValTradeFeeDetail(dto,valFinProductDeal);
            if(valTradeFeeDetail!=null){
            	listValTradeFeeDetail.add(valTradeFeeDetail);
            }
            
            //结算指令
            /**modify by wujie on 20190122 start
            Map<String, Object> map = this.getvalOrders(valFinProductDeal,dealAccounting.getValBooksetInfoDtoList());
			List<StlOrders> ValOrders=(List<StlOrders>)map.get("ORDERS");
            List<StlOrdersExt> ValOrdersExt=(List<StlOrdersExt>)map.get("ORDERS_EXT");
            listValOrders.addAll(ValOrders);
            listValOrdersExt.addAll(ValOrdersExt);
            if(map.get("ACCOUNTING")!=null){
            	listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
            }
            if(map.get("ACCOUNTING_DETAI")!=null){
            	listValDealAccountingDetail.addAll((List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
            }
            if(map.get("BOK_DETAIL_ID")!=null){
            	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }
            **/
            Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal, dto.getProdId(),dto.getFinprodType(),dto.getProdVdate());
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
            /**modify by wujie on 20190122 end**/
            
        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        
        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
        return resultMap;
    }
    
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @SuppressWarnings("unchecked")
//	private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,List<ValBooksetInfoDto> valBooksetInfoDtoList) {
//    	Map<String, Object> map = new HashMap<String,Object>();
//    	List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//    	
//        //交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setEffDate(valFinProductDeal.getSettleDate());//* 交割日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(valFinProductDeal.getFinprodId()); // * 金融产品代码
//        /** modfiy by wujie on 20181022 end remark:支持投资组合投资资产的情况
//        productDealDto.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue()); // * 金融产品类型
//        **/
//        productDealDto.setFinprodType(interfCommonService.queryProductType(valFinProductDeal.getFinprodId())); 
//        /**modify by wujie on 20181022 end**/
//        
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(valFinProductDeal.getFinprodId()); //  * 理财产品代码
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//        productDealDto.setEffDate(valFinProductDeal.getSettleDate());
////        productDealDto.setBookNo(bookSetId);
//        productDealDto.setValBooksetInfoDtoList(valBooksetInfoDtoList);
//        productDealDto.setCreateTime(valFinProductDeal.getCreateTime());
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        String insType = "";
//        //产品认购、申购
//        if(DictCodeList.交易方向_产品认购.getValue().equals(valFinProductDeal.getPs()) 
//              || DictCodeList.交易方向_产品申购.getValue().equals(valFinProductDeal.getPs())
//              ){
//        	insType= DictCodeListExt1.指令类型_净值型产品_产品认购申购_收取.getValue();
//        	
//        }else if(DictCodeList.交易方向_产品赎回.getValue().equals(valFinProductDeal.getPs()) 
//              || DictCodeList.交易方向_产品到期.getValue().equals(valFinProductDeal.getPs())
//              ){
//        	insType= DictCodeListExt1.指令类型_净值型产品_产品赎回到期.getValue();
//        }
//        map = interfCommonService.settlementInstructionGeneration_Layering(productDealDto, insType,
//        		DictCodeList.是否_是.getValue());
//        StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//        StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//        listValOrders.add(ValOrders);
//        listValOrdersExt.add(ValOrdersExt);
//        
//        //产品赎回到期
//        if(DictCodeListExt1.指令类型_净值型产品_产品赎回到期.getValue().equals(insType)){
//        	 map = interfCommonService.settlementInstructionGeneration_Layering(productDealDto, 
//        			 DictCodeListExt1.指令类型_净值型产品_产品赎回到期_支付.getValue(),DictCodeList.是否_否.getValue());
//             ValOrders=(StlOrders)map.get("ORDERS");
//             ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//             listValOrders.add(ValOrders);
//             listValOrdersExt.add(ValOrdersExt);
//             
//             /** modify by wujie on 20181023 start remark:改为调用分层入账信息共通**/
////             BokDealAccounting valDealAccounting = (BokDealAccounting)map.get("ACCOUNTING"); 
////             BokDealAccountingDetail valDealAccountingDetail = (BokDealAccountingDetail)map.get("ACCOUNTING_DETAI"); 
////             BokDetailId valBokDetailId = (BokDetailId)map.get("BOK_DETAIL_ID"); 
//             
//             // 入账信息表
//             List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
//             // 账务明细表
//             List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
//             // 账务明细代码表
//             List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
//             
//             if(map.get("ACCOUNTING_LIST")!=null){
//             	listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING_LIST"));
//             }
//             
//             if(map.get("ACCOUNTING_DETAI_LIST")!=null){
//             	listValDealAccountingDetail.addAll((List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI_LIST"));
//             }
//             
//             if(map.get("BOK_DETAIL_ID_LIST")!=null){
//             	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST"));
//             }
//             /** modify by wujie on 20181023 end remark:改为调用分层入账信息共通**/
//             
//             map = new HashMap<String,Object>();
//             map.put("ORDERS", listValOrders);
//             map.put("ORDERS_EXT", listValOrdersExt);
//             map.put("ACCOUNTING", listValDealAccounting);
//             map.put("ACCOUNTING_DETAI", listValDealAccountingDetail);
//             map.put("BOK_DETAIL_ID", listValBokDetailId);
//        }else{
//        	 map = new HashMap<String,Object>();
//             map.put("ORDERS", listValOrders);
//             map.put("ORDERS_EXT", listValOrdersExt);
//        }
//        return map;
//    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-存续期交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdPeriodDealN dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 交易编号 = A.成交编号
        valFinProductDeal.setTradeId(interfCommonService.getTradeid(dto.getDealType(), dto.getBusiDate(), "A"));
        
        // 业务类型 = 17 TA交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_净值型TA交易.getValue());
        // 金融产品代码 = A.产品代码
        valFinProductDeal.setFinprodId(dto.getProdId());
        // 金融产品类型 = F16 理财产品
        valFinProductDeal.setFinprodType(dto.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        valFinProductDeal.setBranch(branch);
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        //交割日 = A.业务日期
        valFinProductDeal.setSettleDate(dto.getBusiDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 净值日期 = A.净值日
        valFinProductDeal.setCpriceDate(dto.getNetDate());
        // 单位净价 = A.单位净值
        valFinProductDeal.setUnitCprice(dto.getUnitValue());
        // 交易份额 = A.交易份额
        valFinProductDeal.setShareAmt(dto.getTradeQty());
        // 随交易支付费用 = A.费用
        valFinProductDeal.setFeeAmtPay(dto.getFee());
        /**modify by wujie on 20180827 start**/
//        //交易本金       A.交易金额
//        valFinProductDeal.setPrinAmt(dto.getDealAmt());
        //全价总额 = A.单位净值*A.交易份额
        BigDecimal fpriceAmt = dto.getUnitValue() == null?BigDecimal.ZERO:dto.getUnitValue();
        fpriceAmt = fpriceAmt.multiply(dto.getTradeQty()==null?BigDecimal.ZERO:dto.getTradeQty());
        valFinProductDeal.setFpriceAmt(fpriceAmt);
        /**modify by wujie on 20180827 end**/
        /** modify by yanya on 20210316 增加费用留存 start**/
        //// 总交易费用 = A.费用
        //valFinProductDeal.setFeeAmt(dto.getFee());
        // 未随交易支付费用 = A.费用留存收入
        valFinProductDeal.setFeeAmtUnpay(dto.getRetainFee());
        // 总交易费用 = A.费用+A.费用留存收入
        valFinProductDeal.setFeeAmt(ArithmeticUtil.doAdd(dto.getFee(),dto.getRetainFee()));
        /** modify by yanya on 20210316 增加费用留存 end**/
        
        /**modify by wujie on 20180827 start**/
        // 交易金额 = A.交易金额+A.费用
        BigDecimal tradeAmt = dto.getDealAmt()==null?BigDecimal.ZERO:dto.getDealAmt();
        if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())
        		|| DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())
        		){
        	tradeAmt = tradeAmt.add(dto.getFee()==null?BigDecimal.ZERO:dto.getFee());
        }else{
        	tradeAmt = tradeAmt.subtract(dto.getFee()==null?BigDecimal.ZERO:dto.getFee());
        }
        valFinProductDeal.setTradeAmt(tradeAmt);
        /**modify by wujie on 20180827 end**/
        
        // 交易方向 = A.交易类型
        valFinProductDeal.setPs(dto.getDealType());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
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
     * @param dto 01-存续期交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValPrdPeriodDealN dto,TrdProductDeal t1) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = B.产品代码
//        valFinProductDealExt.setProdId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//            valFinProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
////            valFinProductDealExt.setProdId(dto.getProdId());
//        }
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 是否有结算指令 = A.有无清算指令
//        valFinProductDealExt.setHasOrder(dto.getIsClean());
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        //生成方式
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }
    /**
     * T5: 交易费用明细表 接收
     * 
     * @param dto 01-存续期交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTradeFeeDetail(ValPrdPeriodDealN dto, TrdProductDeal valFinProductDeal) {
    	if(dto!=null&&dto.getFee()!=null&&dto.getFee().compareTo(BigDecimal.ZERO)!=0){
    		TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
            // 一次性费用类型 = 固定值：99 其他费用
            valTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_其他费用.getValue());
            // 是否随交易支付 = 固定值：Y 是 默认随交易支付
            valTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额 = A.费用
            valTradeFeeDetail.setFeeAmt(dto.getFee());
            // 费用代码 = 固定值：99 其他费用
            valTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_其他费用.getValue());
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            return valTradeFeeDetail;
    	}
        else if(dto != null&&dto.getRetainFee() != null && dto.getRetainFee().compareTo(BigDecimal.ZERO) > 0){
            TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
            // 一次性费用类型 = 赎回费（产品留存）
            valTradeFeeDetail.setFeeType(DictCodeListExt3.一次性费用类型_赎回费_产品留存.getValue());
            // 是否随交易支付 = 固定值：否
            valTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            // 费用金额 = A.费用留存收入
            valTradeFeeDetail.setFeeAmt(dto.getRetainFee());
            // 费用代码 = 赎回费（产品留存）
            valTradeFeeDetail.setFeeId(DictCodeListExt3.一次性费用类型_赎回费_产品留存.getValue());
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            return valTradeFeeDetail;
        }
    	else{
    		//如果费用未空不生成
    		return null;
    	}

    }

}
