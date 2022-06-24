/*
 * 文件名：CPrdDealBonusDealTemplate.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.cprddeal;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdBonusDealC;

/**
 * 货币型产品分红交易 模板处理Service(ValPrdBonusDealC)
 * 
 * @author chendijun
 * @version 1.0 2018年8月14日
 * @see CPrdDealBonusDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPrdDealBonusDealTemplateRecService {

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
        // 定义返回对象
        CProdDealInfoValCommonResult result = new CProdDealInfoValCommonResult();
        Map<String,Object> params = acctEntity.getParams();//modify by dongjun on 20190104
        List<ValPrdBonusDealC> listValPrdBonusDealC = new ArrayList<>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
            
        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdBonusDealC = returnResult.getT(ValPrdBonusDealC.class);
        result.setDatanum(listValPrdBonusDealC==null?0:listValPrdBonusDealC.size());
        if (listValPrdBonusDealC == null) {
        	listValPrdBonusDealC = new ArrayList<ValPrdBonusDealC>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、根据有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValPrdBonusDealC(listValPrdBonusDealC,
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
            result.setListValOrders(listValOrders);
            // 结算指令扩展表
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrdersExt(listValOrdersExt);
            
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));

        }
        // 返回处理结果数据

        return result;
    }

    /**
     * 02-货币型产品分红
     * 
     * @param listValPrdBonusDealC
     * @param resovleDate
     * @return resultMap
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	private Map<String, Object> handleValPrdBonusDealC(List<ValPrdBonusDealC> listValPrdBonusDealC,
            Date resovleDate,Map<String,Object> params,List<ValInterflog> valInterflog) {
		List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
		String valModel = (String)params.get(InterfCommonConstant.接口模式);
		// 初始化返回结果
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        

        for (ValPrdBonusDealC dto : listValPrdBonusDealC) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);// 用于去重操作

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

            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                valFinProductDeal, dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
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
            /**modify by wujie on 20181023 start remark:改为调用分层入账信息共通**/
            

            // 结算指令
            /**
             * modify by wujie on 20190121 start
            ProductDealDto productDealDto = getProductDealDto(valFinProductDeal,
            		dealAccounting.getValBooksetInfoDtoList());
            String insType = "";
            if (DictCodeList.交易方向_产品现金分红.getValue().equals(valFinProductDeal.getPs())) {
                insType = DictCodeListExt1.指令类型_货币型产品_产品分红.getValue();
                
                // 指令类型
                Map<String, Object> map = interfCommonService.settlementInstructionGeneration_Layering(
                    productDealDto, insType, DictCodeList.是否_是.getValue());
                StlOrders ValOrders=(StlOrders)map.get("ORDERS");
                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
                listValOrders.add(ValOrders);
                listValOrdersExt.add(ValOrdersExt);
                
            } 
            else if (DictCodeList.交易方向_产品到期分红.getValue().equals(valFinProductDeal.getPs())) {
                insType = DictCodeListExt1.指令类型_货币型产品_产品到期分红.getValue();
                
                // 指令类型
                Map<String, Object> map = interfCommonService.settlementInstructionGeneration_Layering(
                    productDealDto, insType, DictCodeList.是否_是.getValue());
                StlOrders ValOrders=(StlOrders)map.get("ORDERS");
                StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
                listValOrders.add(ValOrders);
                listValOrdersExt.add(ValOrdersExt);
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
            /**modify by wujie on 20190118 end**/
        }

        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);

        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        
        List<ValInterflog> valInterflogList = new ArrayList<>();
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
    	
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
    private TrdProductDeal getValFinProductDeal(ValPrdBonusDealC dto) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();

        // 交易方向
        // 1.B.分红方式 eq 数据字典：支付方式.现金 交易方向 = 数据字典：交易方向.产品现金分红
        // 2.B.分红方式 eq 数据字典：支付方式.红利再投 交易方向 = 数据字典：交易方向.产品红利转投
        if (DictCodeList.支付方式_现金.getValue().equals(dto.getBonusMode())) {
            trdProductDeal.setPs(DictCodeList.交易方向_产品现金分红.getValue());
            /**add by wujie on 20180827 start**/
            // 利息金额 = B.分红总金额
            trdProductDeal.setIntAmt(dto.getBonusTotalAmt());
            // 交易金额 = B.分红总金额
            trdProductDeal.setTradeAmt(dto.getBonusTotalAmt());
            /**add by wujie on 20180827 end**/
        }
        else if (DictCodeList.支付方式_红利再投.getValue().equals(dto.getBonusMode())) {
            trdProductDeal.setPs(DictCodeList.交易方向_产品红利转投.getValue());

            /**modify by wujie on 20210428 start
            // 交易份额 = B.红利再投份额
            trdProductDeal.setShareAmt(dto.getBonusTotalAmt());
            // 交易本金 =B.分红总金额
            trdProductDeal.setPrinAmt(dto.getBonusTotalAmt());
             **/
            //交易份额 = B.红利再投份额
            trdProductDeal.setShareAmt(dto.getUnitBonusAmt());
            //全价总额 = B.分红总金额
            trdProductDeal.setFpriceAmt(dto.getBonusTotalAmt());
            /**modify by wujie on 20210428 end**/
        }
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(
            interfCommonService.getTradeid(trdProductDeal.getPs(), dto.getSettDate(), "A"));
        // 业务类型 = 数据字典：业务类型.货币型TA交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        // 金融产品代码 = B.产品代码
        trdProductDeal.setFinprodId(dto.getProdId());
        /**modify by niuqianyu on 20190118 start**/
//      // 金融产品类型 = 数据字典:金融产品类型.理财产品
//      trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
      
      // 金融产品类型 = B.金融产品类型
      trdProductDeal.setFinprodType(dto.getFinprodType());
      
      // 分支序号 = 设置全局变量(默认为0）
//      trdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
      Long branch = null; 
      if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
          branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
      } else {
          branch = InterfCommonConstant.分支序号_默认;
      }
      trdProductDeal.setBranch(branch);
      
      //到期日 = B.分红截止日
      trdProductDeal.setMdate(dto.getEndDate());
      /**modify by niuqianyu on 20190118 end**/
        // 交易日 = B.分红日
        trdProductDeal.setTradeDate(dto.getSettDate());
        // 交割日 = B.分红日
        trdProductDeal.setSettleDate(dto.getSettDate());
        // 交易币种 = B.币种
        trdProductDeal.setCcy(dto.getCcy());
        
        /**modify by wujie on 20180827 start**/
        // 单位净价 = B.分红总金额/B.红利再投份额
//        trdProductDeal.setUnitCprice(
//            ArithmeticUtil.divide(dto.getBonusTotalAmt(), dto.getUnitBonusAmt(), 2));
        //单位净价 = 默认为1
        trdProductDeal.setUnitCprice(InterfCommonConstant.单位净价_默认);
        /**modify by wujie on 20180827 end**/

        // 交易状态 = B.数据动作标志 新增 --> 01：初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值 (收息付费时用)
        trdProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = B.成交编号
        trdProductDeal.setfTradeId(dto.getTradeNo());
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
    private TrdProductDealAdd getValFinProductDealExt(ValPrdBonusDealC dto, TrdProductDeal deal) {
    	TrdProductDealAdd trdProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        trdProductDealExt.setTradeId(deal.getTradeId());
        /**modify by niuqianyu on 20190118 start**/
//      // 理财产品代码 = B.产品代码
//      trdProductDealExt.setProdId(dto.getProdId());

//      if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//          trdProductDealExt.setPortfolioId(dto.getProdId());
//      }else{
//          trdProductDealExt.setProdId(dto.getProdId());
//      }
        
        trdProductDealExt.setPortfolioId(dto.getProdId());
      /**modify by niuqianyu on 20190118 end**/
        // 是否有结算指令 = B.有无清算指令
//        trdProductDealExt.setHasOrder(dto.getIsClean());
        // 生成方式 = 固定值 接口接入
        trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 固定值 系统时间
        trdProductDealExt.setCreateTime(dto.getCreateTime());
        return trdProductDealExt;

    }

//    private ProductDealDto getProductDealDto(TrdProductDeal valFinProductDeal, List<ValBooksetInfoDto> valBooksetInfoDtoList) {
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
//        dto.setFinprodId(valFinProductDeal.getFinprodId());
//        // 金融产品类型
//        /** modfiy by wujie on 20181022 end remark:支持投资组合投资资产的情况
//        dto.setFinprodType(valFinProductDeal.getFinprodType());
//        **/
//        dto.setFinprodType(interfCommonService.queryProductType(valFinProductDeal.getFinprodId())); 
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
//        return dto;
//    }
}
