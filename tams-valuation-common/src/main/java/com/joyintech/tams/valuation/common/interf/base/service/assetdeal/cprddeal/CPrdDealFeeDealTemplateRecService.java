/*
 * 文件名：CPrdDealFeeDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.cprddeal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.CProdDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdFeeDealC;
import com.joyintech.framework.util.StringUtil;

/**
 * 货币型产品费用交易 模板处理Service(ValPrdFeeDealC)
 * 
 * @author chendijun
 * @version 1.0 2018年8月14日
 * @see CPrdDealFeeDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPrdDealFeeDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    /**
     * 接口共通方法
     */
    @Autowired
    private InterfCommonService interfCommonService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CProdDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 初始化返回货币型对象
        CProdDealInfoValCommonResult result = new CProdDealInfoValCommonResult();
        Map<String,Object> params = acctEntity.getParams();//modify by dongjun on 20190104
        List<ValPrdFeeDealC> listValPrdFeeDealC = new ArrayList<ValPrdFeeDealC>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdFeeDealC = returnResult.getT(ValPrdFeeDealC.class);
        result.setDatanum(listValPrdFeeDealC==null?0:listValPrdFeeDealC.size());
        if (listValPrdFeeDealC == null) {
        	listValPrdFeeDealC = new ArrayList<ValPrdFeeDealC>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValPrdBonusDealC(listValPrdFeeDealC,
            acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if(map != null){
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
            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);
            
            // 结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            
            /**modify by wujie on 20190122 remark:重复导入删除逻辑调整
            if(map.get("cqFtradeIds")!=null){
            	Map<String,Object> paramMap = new HashMap<String,Object>();
            	paramMap.put("cqFtradeIds", map.get("cqFtradeIds"));
            	result.setParamMap(paramMap);
            }**/
            
            /**add by wujie on 20190129 start**/
            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
            result.setValTradeFeeDetailList(listValTradeFeeDetail);
            /**add by wujie on 20190129 end**/
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据

        return result;

    }

    @SuppressWarnings("unchecked")
	private Map<String, Object> handleValPrdBonusDealC(List<ValPrdFeeDealC> listValPrdFeeDealC,
            Date resovleDate,Map<String,Object> params,List<ValInterflog> valInterflog) {
		List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
		String valModel = (String)params.get(InterfCommonConstant.接口模式);
		// 初始化返回map	
        Map<String, Object> resultMap = new HashMap<>();

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();

        // 结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        
        /**add by wujie on 20190129 start**/
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        /**add by wujie on 20190129 end**/
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();

        for (ValPrdFeeDealC dto : listValPrdFeeDealC) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);

            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);
            
            /**modify by wujie on 20190122 remark:重复导入删除逻辑调整
            cqFtradeIds.add(valFinProductDeal.getfTradeId());
            **/

            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);
            
            /**add by wujie on 20190129 start**/
            // 交易费用明细表
            TrdTradeFeeDetail tgTradeFeeDetail = this.getValTradeFeeDetail(dto, valFinProductDeal);
            listValTradeFeeDetail.add(tgTradeFeeDetail);
            /**add by wujie on 20190129 end**/

            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                valFinProductDeal, dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
           
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
            	listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
            }
            
            BokDetailId bokDetailId = new BokDetailId();
            bokDetailId.setBusiId(dto.getProdId());
            bokDetailId.setBusiDetailType(dto.getFinprodType());
            Long  branch = null;
            String detailSubjectNoTail = dto.getProdId();
            if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
            	branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
            	detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
            }
            bokDetailId.setBranch(branch);
            String detailId =  interfCommonService.genMd5BokDetailId(bokDetailId);
            
            List<BokDealAccountingDetail> bokDealAccountingDetaillist = dealAccounting.getBokDealAccountingDetailList();
            if (CollectionUtils.isNotEmpty(bokDealAccountingDetaillist)) {
            	for(BokDealAccountingDetail obj :bokDealAccountingDetaillist){
            		obj.setBokDetailId(detailId);
            	}
            	listValDealAccountingDetail.addAll(bokDealAccountingDetaillist);
            }
            /**add by wujie on 20190103 start**/
            detailSubjectNoTail = detailSubjectNoTail+FinalString.LAYERING_SPLIT+dto.getFinprodType();
            /**add by wujie on 20190103 end**/
            List<BokDetailId> bokDetailIdlist = dealAccounting.getBokDetailIdList();
            if (CollectionUtils.isNotEmpty(bokDetailIdlist)) {
            	for(BokDetailId obj:bokDetailIdlist){
            		obj.setBusiId(dto.getProdId());
            		obj.setBusiDetailType(dto.getFinprodType());
            		obj.setDetailSubjectNoTail(detailSubjectNoTail);
            		obj.setDetailSubnameTail(dto.getProdName());
            		obj.setBranch(branch);
            		obj.setBokDetailId(detailId);
                    listValBokDetailId.add(obj);
            	}
            }
            // 结算指令
            /**modify by wujie on 20190121 start
            ProductDealDto productDealDto = getProductDealDto(valFinProductDeal,
            		dealAccounting.getValBooksetInfoDtoList(),dto.getProdId());
            String insType = DictCodeListExt1.指令类型_产品其他费用_支付.getValue();
            Map<String, Object> map = interfCommonService.settlementInstructionGeneration_Layering(
                productDealDto, insType, DictCodeList.是否_是.getValue());
            StlOrders ValOrders = (StlOrders)map.get("ORDERS");
            StlOrdersExt ValOrdersExt = (StlOrdersExt)map.get("ORDERS_EXT");
            listValOrders.add(ValOrders);
            listValOrdersExt.add(ValOrdersExt);
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
            /**modify by wujie on 20190121 end**/
            
            
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
        /**add by wujie on 20190129 start**/
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        /**add by wujie on 20190129 end**/
        return resultMap;

    }

    /**
     * T1: 金融产品交易
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdFeeDealC dto) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_产品付费.getValue(),
            dto.getPayDate(), "A"));
        // 业务类型 = 数据字典：业务类型.货币型TA交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
        
        /**modify by niuqianyu on 20190118 start**/
	      String cashId = null;
	      if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
	        cashId = InterfHelper.getMd5CashId_(dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()),dto.getFeeId());   
	      } else {
	        cashId = InterfHelper.getMd5CashId_(dto.getProdId(),dto.getFeeId());   
	      }
	      trdProductDeal.setFinprodId(cashId);
         /**modify by niuqianyu on 20190118 end**/
	      
	     /**add by wujie on 20190422 start**/
	     //金融产品代码 = A.产品代码
	      trdProductDeal.setFinprodId(dto.getProdId());
	     /**add by wujie on 20190422 end**/
	      
        //金融产品类型 = 调用共通查询现金流信息.现金流二级类型
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cashId", cashId);
        FinCashInfo finCashInfo = interfCommonService.queryCashInfo(paramMap);
        if(finCashInfo!=null){
    	  trdProductDeal.setFinprodType(finCashInfo.getCashType2());
        }else{
            if(DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(dto.getFeeId())){
                trdProductDeal.setFinprodType(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            }
        }
          
        /**modify by niuqianyu on 20190118 start**/ 
        // 分支序号 = 设置全局变量(默认为0）
//        trdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        
          Long branch = null; 
          if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
              branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
          } else {
              branch = InterfCommonConstant.分支序号_默认;
          }
          trdProductDeal.setBranch(branch);
        /**modify by niuqianyu on 20190118 end**/
        // 交易日 = C.支付日
        trdProductDeal.setTradeDate(dto.getPayDate());
        // 起息日 = C.计提起始日
        trdProductDeal.setVdate(dto.getCalStartDay());
        // 到期日 = C.计提结束日
        trdProductDeal.setMdate(dto.getCalEndDay());
        // 交割日 = C.支付日
        trdProductDeal.setSettleDate(dto.getPayDate());
        // 交易币种 = C.币种
        trdProductDeal.setCcy(dto.getCcy());

        // 交易金额 = C.实付费用
        trdProductDeal.setTradeAmt(dto.getActPayAmt());

        // 交易方向 = 固定值--> 数据字典:交易方向.产品付费
        trdProductDeal.setPs(DictCodeList.交易方向_产品付费.getValue());
        // 交易状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        /**MODIFY BY WUJIE ON 20210501 START
        // 是否结清 = 固定值 (非收息付费时设置默认值)
        trdProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        **/
        if(StringUtil.isNotEmpty(dto.getIsClean2())){
            trdProductDeal.setIsClean(dto.getIsClean2());
        }else{
            trdProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        }

        /**MODIFY BY WUJIE ON 20210501 END**/


        // 关联系统交易编号 = A.成交编号
        trdProductDeal.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
        //20181012 leipeng 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(trdProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            trdProductDeal.setpTradeId(interfCommonService.getFTradeId(trdProductDeal));
        }
        return trdProductDeal;
    }

    /**
     * T2: 估值核算交易扩展表
     * 
     * @param dto
     * @param deal T1
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValPrdFeeDealC dto, TrdProductDeal deal) {
    	TrdProductDealAdd trdProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        trdProductDealExt.setTradeId(deal.getTradeId());
        /**modify by niuqianyu on 20190118 start**/
//      // 理财产品代码 = C.产品代码
//      trdProductDealExt.setProdId(dto.getProdId());
      
//      if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//          trdProductDealExt.setPortfolioId(dto.getProdId());
//      }else{
//          trdProductDealExt.setProdId(dto.getProdId());
//      }
      trdProductDealExt.setPortfolioId(dto.getProdId());
      /**modify by niuqianyu on 20190118 end**/
        // 是否有结算指令 = C.有无清算指令
//        trdProductDealExt.setHasOrder(dto.getIsClean());
        // 生成方式 = 固定值 接口接入
        trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 固定值 系统时间
        trdProductDealExt.setCreateTime(dto.getCreateTime());
        return trdProductDealExt;

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
    private TrdTradeFeeDetail getValTradeFeeDetail(ValPrdFeeDealC dto, TrdProductDeal valFinProductDeal) {
    	TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
        // 交易编号 = T1.交易编号
        valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
        String cashId = null;
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            cashId = InterfHelper.getMd5CashId_(dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()),
                dto.getFeeId());
        } else {
            cashId = InterfHelper.getMd5CashId_(dto.getProdId() , dto.getFeeId());
        } 
        //金融产品类型 = 调用共通查询现金流信息.现金流二级类型
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cashId", cashId);
        FinCashInfo finCashInfo = interfCommonService.queryCashInfo(paramMap);
        if(finCashInfo!=null){
        	valTradeFeeDetail.setFeeType(finCashInfo.getCashType2());
        }
        else{
            if(DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(dto.getFeeId())){
                valTradeFeeDetail.setFeeType(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            }
        }
        // 是否随交易支付 = 固定值：Y 是 默认随交易支付
        valTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        // 对应金额
        valTradeFeeDetail.setFeeAmt(dto.getActPayAmt());
        valTradeFeeDetail.setFeeId(dto.getFeeId());
        
        // 创建时间 = 系统时间
        valTradeFeeDetail.setCreateTime(dto.getCreateTime());
        
        return valTradeFeeDetail;
    }
    
//    private ProductDealDto getProductDealDto(TrdProductDeal valFinProductDeal, List<ValBooksetInfoDto> valBooksetInfoDtoList,String prodId) {
//
//        // 结算指令
//        ProductDealDto dto = new ProductDealDto();
//        //交易状态
//        dto.setTradeStatus(valFinProductDeal.getTradeStatus());
//        // 交易编号
//        dto.setTradeId(valFinProductDeal.getTradeId());
//        // 交易日
//        dto.setTradeDate(valFinProductDeal.getTradeDate());
//        // 交易方向
//        dto.setPs(valFinProductDeal.getPs());
//        // 交易金额
//        dto.setTardeAmt(valFinProductDeal.getTradeAmt());
//        // 金融产品代码
//        dto.setFinprodId(prodId);
//        // 金融产品类型
//        /** modfiy by wujie on 20181022 end remark:支持投资组合投资资产的情况
//        dto.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
//        **/
//        dto.setFinprodType(interfCommonService.queryProductType(prodId)); 
//        /**modify by wujie on 20181022 end**/
//        // 业务类型
//        dto.setBusiType(valFinProductDeal.getBusiType());
//        // 理财产品代码
//        dto.setProdId(valFinProductDeal.getFinprodId());
//        // 币种
//        dto.setCcy(valFinProductDeal.getCcy());
//        // 是否随交易出账
//        dto.setIsTrade(DictCodeList.是否_是.getValue());
//        // 账套代码
//        dto.setValBooksetInfoDtoList(valBooksetInfoDtoList);
//        // 交割日
//        dto.setEffDate(valFinProductDeal.getSettleDate());
//        
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        dto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//
//        return dto;
//    }
}
