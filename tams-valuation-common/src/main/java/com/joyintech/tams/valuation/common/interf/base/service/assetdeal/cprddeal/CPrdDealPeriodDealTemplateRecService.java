/*
 * 文件名：VPrdDealPeriodDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.cprddeal;

import java.math.BigDecimal;
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
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdPeriodDealC;
import com.joyintech.framework.util.StringUtil;

/**
 * 货币型产品存续期交易表 模板处理Service(VAL_PRD_PERIOD_DEAL_C)
 * 
 * @author chendijun
 * @version 1.0 2018年8月13日
 * @see CPrdDealPeriodDealTemplateRecService
 * @since 1.0
 */

@Service
@Scope("prototype")
public class CPrdDealPeriodDealTemplateRecService {

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
        CProdDealInfoValCommonResult result = new CProdDealInfoValCommonResult();
        Map<String,Object> params = acctEntity.getParams();//modify by dongjun on 20190104
        List<ValPrdPeriodDealC> listValPrdPeriodDealC = new ArrayList<ValPrdPeriodDealC>();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult<>();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdPeriodDealC = returnResult.getT(ValPrdPeriodDealC.class);
        result.setDatanum(listValPrdPeriodDealC==null?0:listValPrdPeriodDealC.size());
        if (listValPrdPeriodDealC == null) {
        	listValPrdPeriodDealC = new ArrayList<ValPrdPeriodDealC>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、根据有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValPrdPeriodDealC(listValPrdPeriodDealC,
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

            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
            result.setValTradeFeeDetailList(listValTradeFeeDetail);

            // 结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));//add by dongjun on 20190225
        }
        // 返回结果
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> handleValPrdPeriodDealC(List<ValPrdPeriodDealC> listValPrdPeriodDealC,
            Date resovleDate,Map<String,Object> params,List<ValInterflog> valInterflog) {
		List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
		String valModel = (String)params.get(InterfCommonConstant.接口模式);
        // 初始化返回结果
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
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
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValPrdPeriodDealC dto : listValPrdPeriodDealC) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);
            // 金融产品交易
            TrdProductDeal trdProductDeal = null;
            /**
             * add by wujie on 20180906 start
             * 产品赎回交易
             */
            if(DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
            		|| DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())){
            	BigDecimal intamt = dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt();
            	if(intamt.compareTo(BigDecimal.ZERO)==0){
            		trdProductDeal = this.getValFinProductDeal(dto);
                    listValFinProductDeal.add(trdProductDeal);
            	}else{
            		String ps = null;
            		if(DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())){
            			ps = DictCodeList.交易方向_赎回收益确认.getValue();
            		}else{
            			ps = DictCodeList.交易方向_产品到期分红.getValue();
            		}
            		trdProductDeal = this.getValFinProductDeal(dto,dto.getDealType());
                    listValFinProductDeal.add(trdProductDeal);
                    
            		/**拆分出的现金分红交易处理**/
                    TrdProductDeal trdProductDealFH = this.getValFinProductDeal(dto,ps);
                    listValFinProductDeal.add(trdProductDealFH);
                    
                    // 现金分红-估值核算交易扩展表
                    TrdProductDealAdd trdProductDealExt = this.getValFinProductDealExt(dto,
                        trdProductDealFH);
                    listValFinProductDealExt.add(trdProductDealExt);
                    
                    // 现金分红-入账信息表 账务明细表调用共通
                    InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                        trdProductDealFH, dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
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
                    
                    /** modify by wujie on 20190121 start
                    // 现金分红-结算指令表
                    // 现金分红-结算指令扩展表
                    Map<String, Object> map = this.getvalOrders(trdProductDealFH, 
                    		dealAccounting.getValBooksetInfoDtoList());
                    listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
                    listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
                    **/
                    Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDealFH, dto.getProdId(),dto.getFinprodType(),dto.getProdVdate());
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
                    /**modify by wujie on 20190121**/
            		
            	}
            }
            else{
            	trdProductDeal = this.getValFinProductDeal(dto);
                listValFinProductDeal.add(trdProductDeal);
            }
            /**add by wujie on 20180906 end**/
            
            // 估值核算交易扩展表
            TrdProductDealAdd trdProductDealExt = this.getValFinProductDealExt(dto,
                trdProductDeal);
            listValFinProductDealExt.add(trdProductDealExt);
            // 交易费用明细表
            TrdTradeFeeDetail tradeFeeDetail = this.getValTgTradeFeeDetail(dto, trdProductDeal);
            if (tradeFeeDetail != null) {
                listValTradeFeeDetail.add(tradeFeeDetail);
            }
            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                trdProductDeal, dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
            /**modify by wujie on 20181023 start remark:改为调用分层入账信息共通
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
            listValBokDetailId.add(dealAccounting.getBokDetailId());
            **/
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
            /**modify by wujie on 20181023 end remark:改为调用分层入账信息共通**/

            /**modify by wujie on 20190121 start
            // 结算指令表
            // 结算指令扩展表
            Map<String, Object> map = this.getvalOrders(trdProductDeal, dealAccounting.getValBooksetInfoDtoList());
            listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
            listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
            
            if(map.get("ACCOUNTING")!=null){
            	listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
            }
            
            if(map.get("ACCOUNTING_DETAI")!=null){
            	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
            }
            
            if(map.get("BOK_DETAIL_ID")!=null){
            	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }
            **/
            Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDeal, dto.getProdId(), dto.getFinprodType(),dto.getProdVdate());
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
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);

        resultMap.put("listValOrders", listValOrders);
        resultMap.put("listValOrdersExt", listValOrdersExt);
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }

    /**
     * T1: 金融产品交易
     * 赎回交易特殊处理 add by wujie on 20180906 
     * PS:只处理赎回交易
     */
    private TrdProductDeal getValFinProductDeal(ValPrdPeriodDealC dto,String ps) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(interfCommonService.getTradeid(ps, dto.getBusiDate(), "A"));
        // 业务类型 = 数据字典：业务类型.货币型TA交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        // 金融产品代码 = A.产品代码
        trdProductDeal.setFinprodId(dto.getProdId());
        /**modify by niuqianyu on 20190118 start**/
//      // 金融产品类型 = 数据字典:金融产品类型.理财产品
//      trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
      
      // 金融产品类型 = A.金融产品类型
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
      /**modify by niuqianyu on 20190118 end**/
        // 交易日 = A.交易日
        trdProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.业务日期
        trdProductDeal.setSettleDate(dto.getBusiDate());
        // 交易币种 = A.币种
        trdProductDeal.setCcy(dto.getCcy());
        if(DictCodeList.交易方向_产品现金分红.getValue().equals(ps) 
        		|| DictCodeList.交易方向_赎回收益确认.getValue().equals(ps)
        		|| DictCodeList.交易方向_产品到期分红.getValue().equals(ps)){
        	// 单位净价 = A.单位净值
            trdProductDeal.setUnitCprice(InterfCommonConstant.单位净价_默认);
            //利息总额 = A.赎回收益
            trdProductDeal.setIntAmt(dto.getIntAmt());
            //交易金额 = A.赎回收益
            trdProductDeal.setTradeAmt(dto.getIntAmt());
            
            /**add by wujie on 20190109 start**/
            if(DictCodeList.交易方向_赎回收益确认.getValue().equals(ps)){
            	//交易份额 = A.赎回本金
            	trdProductDeal.setShareAmt(dto.getTradeQty());
            }
            /**add by wujie on 20190109 end**/
            
            /**add by wujie on 20190116 start**/
            //到期日 = A.业务日期
            trdProductDeal.setMdate(dto.getBusiDate());
            /**add by wujie on 20190116 end**/
            
        }
        else if(DictCodeList.交易方向_产品赎回.getValue().equals(ps)
        		|| DictCodeList.交易方向_产品到期.getValue().equals(ps)){
        	// 净值日期 = A.净值日
            trdProductDeal.setCpriceDate(dto.getNetDate());
            // 单位净价 = A.单位净值
            trdProductDeal.setUnitCprice(dto.getUnitValue());
            // 交易份额 = A.交易份额
            trdProductDeal.setShareAmt(dto.getTradeQty());
            // 交易本金 = A.交易份额
            trdProductDeal.setPrinAmt(dto.getTradeQty());
            //全价总额 = A.交易份额 
            trdProductDeal.setFpriceAmt(dto.getTradeQty());
            // 随交易支付费用 = A.费用
            trdProductDeal.setFeeAmtPay(dto.getFee());
            //交易金额 = A.交易金额 - A.费用 - A.赎回收益
            BigDecimal tradeAmt = dto.getDealAmt()==null?BigDecimal.ZERO:dto.getDealAmt();
            tradeAmt = tradeAmt.subtract(dto.getFee()==null?BigDecimal.ZERO:dto.getFee());
            tradeAmt = tradeAmt.subtract(dto.getIntAmt() == null?BigDecimal.ZERO:dto.getIntAmt());
            trdProductDeal.setTradeAmt(tradeAmt);
            // 总交易费用 = A.费用
            trdProductDeal.setFeeAmt(dto.getFee());
        }
        // 交易方向 =
        trdProductDeal.setPs(ps);
        // 交易状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值 (非收息付费时设置默认值)
        trdProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
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
     * T1: 金融产品交易
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdPeriodDealC dto) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(
            interfCommonService.getTradeid(dto.getDealType(), dto.getBusiDate(), "A"));
        // 业务类型 = 数据字典：业务类型.货币型TA交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        // 金融产品代码 = A.产品代码
        trdProductDeal.setFinprodId(dto.getProdId());
        /**modify by niuqianyu on 20190118 start**/
//      // 金融产品类型 = 数据字典:金融产品类型.理财产品
//      trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
      
      // 金融产品类型 = A.金融产品类型
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
      /**modify by niuqianyu on 20190118 end**/
        // 交易日 = A.交易日
        trdProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.业务日期
        trdProductDeal.setSettleDate(dto.getBusiDate());
        // 交易币种 = A.币种
        trdProductDeal.setCcy(dto.getCcy());
        // 净值日期 = A.净值日
        trdProductDeal.setCpriceDate(dto.getNetDate());
        // 单位净价 = A.单位净值
        trdProductDeal.setUnitCprice(dto.getUnitValue());
        // 交易份额 = A.交易份额
        trdProductDeal.setShareAmt(dto.getTradeQty());
        // 交易本金 = A.交易份额
        trdProductDeal.setPrinAmt(dto.getTradeQty());
        // 随交易支付费用 = A.费用
        trdProductDeal.setFeeAmtPay(dto.getFee());
        /**modify by wujie on 20180827 start**/
        // 交易金额 = A.交易金额+A.费用
//        trdProductDeal.setTradeAmt(dto.getDealAmt().add(dto.getFee()));
        BigDecimal tradeAmt = dto.getDealAmt()==null?BigDecimal.ZERO:dto.getDealAmt();
        if(DictCodeList.交易方向_产品认购.getValue().equals(dto.getDealType())
        		|| DictCodeList.交易方向_产品申购.getValue().equals(dto.getDealType())
        		){
        	tradeAmt = tradeAmt.add(dto.getFee()==null?BigDecimal.ZERO:dto.getFee());
        }else{
        	tradeAmt = tradeAmt.subtract(dto.getFee()==null?BigDecimal.ZERO:dto.getFee());
        }
        trdProductDeal.setTradeAmt(tradeAmt);
        /**modify by wujie on 20180827 end**/
        // 总交易费用 = A.费用
        trdProductDeal.setFeeAmt(dto.getFee());
        
        /**add by wujie on 20180828 start**/
        //利息总额 = A.赎回收益
        trdProductDeal.setIntAmt(dto.getIntAmt());
        //全价总额 = A.交易份额 + A.赎回收益
        BigDecimal fpriceAmt = dto.getTradeQty()==null?BigDecimal.ZERO:dto.getTradeQty();
        fpriceAmt = fpriceAmt.add(dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt());
        trdProductDeal.setFpriceAmt(fpriceAmt);
        /**add by wujie on 20180828 end**/
        // 交易方向 = A.交易类型
        trdProductDeal.setPs(dto.getDealType());
        // 交易状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值 (非收息付费时设置默认值)
        trdProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 关联系统交易编号 = A.成交编号
        trdProductDeal.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
        // 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
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
    private TrdProductDealAdd getValFinProductDealExt(ValPrdPeriodDealC dto, TrdProductDeal deal) {
    	TrdProductDealAdd trdProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        trdProductDealExt.setTradeId(deal.getTradeId());
        /**modify by niuqianyu on 20190118 start**/
//      trdProductDealExt.setProdId(dto.getProdId());
      
        trdProductDealExt.setPortfolioId(dto.getProdId());
//      if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//          trdProductDealExt.setPortfolioId(dto.getProdId());
//      }else{
//          trdProductDealExt.setProdId(dto.getProdId());
//      }
      /**modify by niuqianyu on 20190118 end**/
        // 是否有结算指令 = A.有无清算指令
//        trdProductDealExt.setHasOrder(dto.getIsClean());
        // 生成方式 = 固定值 接口接入
        trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 固定值 系统时间
        trdProductDealExt.setCreateTime(dto.getCreateTime());
        return trdProductDealExt;

    }

    /**
     * T5: 交易费用明细表
     * 
     * @param dto
     * @param deal T1
     * @return
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTgTradeFeeDetail(ValPrdPeriodDealC dto, TrdProductDeal deal) {
        if (dto != null && dto.getFee() != null && dto.getFee().compareTo(BigDecimal.ZERO) != 0) {
        	TrdTradeFeeDetail tradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            tradeFeeDetail.setTradeId(deal.getTradeId());
            // 一次性费用类型 = 固定值:99 其他费用
            tradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_其他费用.getValue());
            // 是否随交易支付 = 固定值:Y 是 默认随交易支付
            tradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            // 费用金额 = A.费用
            tradeFeeDetail.setFeeAmt(dto.getFee());
            // 费用代码 = 固定值: 99 其他费用
            tradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_其他费用.getValue());
            // 创建时间 = 系统时间
            tradeFeeDetail.setCreateTime(dto.getCreateTime());
            return tradeFeeDetail;
        }
        else {
            return null;
        }
    }

//    /**
//     * 结算指令 <功能详细描述>
//     * 
//     * @param valFinProductDeal
//     * @param bookSetId
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @SuppressWarnings("unchecked")
//	private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal, List<ValBooksetInfoDto> valBooksetInfoDtoList) {
//        Map<String, Object> map = new HashMap<>();
//        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//        
//        // 入账信息表
//        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
//        // 账务明细表
//        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
//        // 账务明细代码表
//        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
//
//        // 结算指令
//        ProductDealDto dto = new ProductDealDto();
//        //交易状态
//        dto.setTradeStatus(valFinProductDeal.getTradeStatus());
//        // 交易编号
//        dto.setTradeId(valFinProductDeal.getTradeId());
//        // 交易日
//        dto.setTradeDate(valFinProductDeal.getTradeDate());
//        // 交割日
//        dto.setEffDate(valFinProductDeal.getSettleDate());
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
//        
//        // 交割日
//        dto.setEffDate(valFinProductDeal.getSettleDate());
//        
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        dto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        
//        String insType = "";
//        if(DictCodeList.交易方向_产品认购.getValue().equals(valFinProductDeal.getPs())
//            || DictCodeList.交易方向_产品申购.getValue().equals(valFinProductDeal.getPs())) {
//            /**
//             * 1.币型产品-产品认购申购-收取
//             */
//            insType = DictCodeListExt1.指令类型_货币型产品_产品认购申购_收取.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//        }else if(DictCodeList.交易方向_产品赎回.getValue().equals(valFinProductDeal.getPs())
//        		|| DictCodeList.交易方向_产品到期.getValue().equals(valFinProductDeal.getPs())
//        		) {
//        	
//        	//随交易生成的指令
//        	insType = DictCodeListExt1.指令类型_货币型产品_产品赎回到期_本金确认.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//            
//            //资金交收指令
//            insType = DictCodeListExt1.指令类型_资金划款_货币型产品赎回到期.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_否.getValue());
//            
//            ValOrders1=(StlOrders)map.get("ORDERS");
//            ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//            
//            if(map.get("ACCOUNTING_LIST")!=null){
//            	listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING_LIST"));
//            }
//            
//            if(map.get("ACCOUNTING_DETAI_LIST")!=null){
//            	listValDealAccountingDetail.addAll((List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI_LIST"));
//            }
//            
//            if(map.get("BOK_DETAIL_ID_LIST")!=null){
//            	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST"));
//            }
//        }else if(DictCodeList.交易方向_产品到期分红.getValue().equals(valFinProductDeal.getPs())) {
//        
//        	//随交易生成的指令
//        	insType = DictCodeListExt1.指令类型_货币型产品_产品到期分红.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//        }
//        else if(DictCodeList.交易方向_产品现金分红.getValue().equals(valFinProductDeal.getPs())) {
//        	//随交易生成的指令
//        	insType = DictCodeListExt1.指令类型_货币型产品_产品分红.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//        }
//        else if(DictCodeList.交易方向_赎回收益确认.getValue().equals(valFinProductDeal.getPs())) {
//        	//随交易生成的指令
//        	insType = DictCodeListExt1.指令类型_货币型产品_产品分红.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//        }
//        map.put("ORDERS", listValOrders);
//        map.put("ORDERS_EXT", listValOrdersExt);
//        map.put("ACCOUNTING", listValDealAccounting);
//        map.put("ACCOUNTING_DETAI", listValDealAccountingDetail);
//        map.put("BOK_DETAIL_ID", listValBokDetailId);
//        return map;
//    }
}
