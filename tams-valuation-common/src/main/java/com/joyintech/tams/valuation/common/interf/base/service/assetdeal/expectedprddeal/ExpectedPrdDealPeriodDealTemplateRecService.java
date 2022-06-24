/*
 * 文件名：ExpectedPrdDealPeriodDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangqidong
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.expectedprddeal;

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
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.ExpectedProdDealValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdPeriodDeal;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdPeriodDealC;
import com.joyintech.framework.util.StringUtil;

/**
 * 预期型产品存续期交易表 模板处理Service(VAL_PRD_PERIOD_DEAL)
 * @author wangqidong
 * @version 1.0 2018年10月9日
 * @see ExpectedPrdDealPeriodDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpectedPrdDealPeriodDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private InterfCommonService InterfCommonService;

    /**
     * 接口共通方法
     */
    @Autowired
    private InterfCommonService interfCommonService;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ExpectedProdDealValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                TemplateOrderInfoReaderResult acctEntity) {
        // 初始化返回预期型对象
        ExpectedProdDealValCommonResult result = new ExpectedProdDealValCommonResult();
        List<ValPrdPeriodDeal> listValPrdPeriodDeal = new ArrayList<ValPrdPeriodDeal>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdPeriodDeal = returnResult.getT(ValPrdPeriodDealC.class);
        result.setDatanum(listValPrdPeriodDeal==null?0:listValPrdPeriodDeal.size());
        if (listValPrdPeriodDeal == null) {
        	listValPrdPeriodDeal = new ArrayList<ValPrdPeriodDeal>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、根据有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValPrdPeriodDealExp(listValPrdPeriodDeal,
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
            
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据
        // 返回结果
        
        return result;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param listValPrdPeriodDeal
     * @param resovleDate
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> handleValPrdPeriodDealExp(List<ValPrdPeriodDeal> listValPrdPeriodDeal,
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
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
        for (ValPrdPeriodDeal dto : listValPrdPeriodDeal) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
            dto.setCreateTime(resovleDate);
            // 金融产品交易
            TrdProductDeal trdProductDeal = null;
            /**
             * 产品赎回交易
             */
            if(DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())
                || DictCodeList.交易方向_产品到期.getValue().equals(dto.getDealType())){
                BigDecimal intamt = dto.getIntAmt()==null?BigDecimal.ZERO:dto.getIntAmt();
                if(intamt.compareTo(BigDecimal.ZERO)==0){
                    trdProductDeal = this.getValFinProductDeal(dto);
                    listValFinProductDeal.add(trdProductDeal);
                }else{
                    trdProductDeal = this.getValFinProductDeal(dto,dto.getDealType());
                    listValFinProductDeal.add(trdProductDeal);
                    
                    /**拆分出的付息交易处理**/
                    TrdProductDeal trdProductDealFX = null;
                    if(DictCodeList.交易方向_产品赎回.getValue().equals(dto.getDealType())) {
                        trdProductDealFX = this.getValFinProductDeal(dto,DictCodeList.交易方向_赎回收益确认.getValue());
                    } else {
                        trdProductDealFX = this.getValFinProductDeal(dto,DictCodeList.交易方向_中间付息.getValue()); 
                    }  
                  
                    listValFinProductDeal.add(trdProductDealFX);
                    
                    // 付息-估值核算交易扩展表
                    TrdProductDealAdd trdProductDealExt = this.getValFinProductDealExt(dto,
                        trdProductDealFX);
                    listValFinProductDealExt.add(trdProductDealExt);
                    
                    // 付息-入账信息表 账务明细表调用共通
                    InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                        trdProductDealFX, dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
                    
                    if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
                    	listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
                    }
                    if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingDetailList())) {
                    	listValDealAccountingDetail.addAll(dealAccounting.getBokDealAccountingDetailList());
                    }
                    if (CollectionUtils.isNotEmpty(dealAccounting.getBokDetailIdList())) {
                    	/**理财子产品账务明细代码四级科目尾号设置 add by wujie on 20181029 start**/
                    	String detailSubjectNoTail = dto.getProdId();
                    	String detailSubnameTail = dto.getProdName();
                    	if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
                    		detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                    		detailSubnameTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
                    	}
                    	/**理财子产品账务明细代码四级科目尾号设置 add by wujie on 20181029 end**/
                    	/**add by wujie on 20190103 start**/
                    	detailSubjectNoTail = detailSubjectNoTail+
                    			FinalString.LAYERING_SPLIT+
                    			dto.getFinprodType();
                    	/**add by wujie on 20190103 end**/
                        interfCommonService.setDetailSubjectNoTail(dealAccounting.getBokDetailIdList(), 
                        		detailSubjectNoTail, detailSubnameTail);
                    	listValBokDetailId.addAll(dealAccounting.getBokDetailIdList());
                    }
                    
                    /** modify by wujie on 20190118 start
                    // 付息-结算指令表
                    // 付息-结算指令扩展表
                    List<ValBooksetInfoDto> valBooksetInfoDtoList = dealAccounting.getValBooksetInfoDtoList();
                    Map<String, Object> map = this.getvalOrders(trdProductDealFX, valBooksetInfoDtoList);
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
                    Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDealFX, dto.getProdId(), dto.getFinprodType(),dto.getProdVdate());
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
            }
            else{
                trdProductDeal = this.getValFinProductDeal(dto);
                listValFinProductDeal.add(trdProductDeal);
            }
            
            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                trdProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);

            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(
                trdProductDeal, dto.getProdId(),dto.getProdVdate(),
                dto.getFinprodType());

            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
            	listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingDetailList())) {
            	listValDealAccountingDetail.addAll(dealAccounting.getBokDealAccountingDetailList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDetailIdList())) {
            	/**理财子产品账务明细代码四级科目尾号设置 add by wujie on 20181029 start**/
            	String detailSubjectNoTail = dto.getProdId();
            	String detailSubnameTail = dto.getProdName();
            	if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(dto.getFinprodType())){
            		detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
            		detailSubnameTail += new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate());
            	}
            	/**理财子产品账务明细代码四级科目尾号设置 add by wujie on 20181029 end**/
            	//设置账务明细代码表的四级科目号
            	/**add by wujie on 20190103 start**/
            	detailSubjectNoTail = detailSubjectNoTail+
            			FinalString.LAYERING_SPLIT+
            			dto.getFinprodType();
            	/**add by wujie on 20190103 end**/
                interfCommonService.setDetailSubjectNoTail(dealAccounting.getBokDetailIdList(), 
                		detailSubjectNoTail, detailSubnameTail);
            	listValBokDetailId.addAll(dealAccounting.getBokDetailIdList());
            }
            
            /** modify by wujie on 20190118 start
            // 结算指令表
            // 结算指令扩展表
            List<ValBooksetInfoDto> valBooksetInfoDtoList = dealAccounting.getValBooksetInfoDtoList();
            Map<String, Object> map = this.getvalOrders(trdProductDeal, valBooksetInfoDtoList);
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
            /**modify by wujie on 20190118 end**/
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
        return resultMap;
    }
    
    /**
     * T1: 金融产品交易
     * 赎回交易特殊处理 
     * PS:只处理赎回交易
     */
    private TrdProductDeal getValFinProductDeal(ValPrdPeriodDeal dto,String ps) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(interfCommonService.getTradeid(dto.getDealType(),
            dto.getBusiDate(), "A"));
        // 业务类型 = 数据字典：业务类型.费用交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_预期收益型TA交易.getValue()); 
        // 金融产品代码 = A.产品代码
        trdProductDeal.setFinprodId(dto.getProdId());
        // 金融产品类型 = A.金融产品类型
        trdProductDeal.setFinprodType(dto.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        /**add by wujie on 20190107 start**/
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        trdProductDeal.setBranch(branch);
        /**add by wujie on 20190107 end**/
        
        // 交易日 = A.交易日
        trdProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.业务日期
        trdProductDeal.setSettleDate(dto.getBusiDate());
        // 到期日 = A.付息截止日
        trdProductDeal.setMdate(dto.getCalEndDay());
        // 交易币种 = A.币种
        trdProductDeal.setCcy(dto.getCcy());
        // 交易份额 = A.本金金额
        trdProductDeal.setShareAmt(dto.getCapiAmt());
        if(DictCodeList.交易方向_产品赎回.getValue().equals(ps)
            || DictCodeList.交易方向_产品到期.getValue().equals(ps)){
             // 交易本金 = A.本金金额
             trdProductDeal.setPrinAmt(dto.getCapiAmt());
             // 利息总额 = 0
             trdProductDeal.setIntAmt(BigDecimal.ZERO);
             // 交易金额 = A.交易本金
             trdProductDeal.setTradeAmt(dto.getCapiAmt());
             // 交易方向 = 产品赎回
             trdProductDeal.setPs(ps);
        }
        else if(DictCodeList.交易方向_中间付息.getValue().equals(ps)
            || DictCodeList.交易方向_赎回收益确认.getValue().equals(ps)){
            // 交易本金 = 0
            trdProductDeal.setPrinAmt(BigDecimal.ZERO);
            // 利息总额 = A.利息金额
            trdProductDeal.setIntAmt(dto.getIntAmt());
            // 交易金额 = A.利息金额
            trdProductDeal.setTradeAmt(dto.getIntAmt());
            // 交易方向 = 中间付息
            trdProductDeal.setPs(ps);
            
            /**add by wujie on 20190109 start**/
            if(DictCodeList.交易方向_赎回收益确认.getValue().equals(ps)){
            	//交易份额 = A.赎回本金
            	trdProductDeal.setShareAmt(dto.getCapiAmt());
            }
            /**add by wujie on 20190109 end**/
        }
        // 交易状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = A.是否结清 
        trdProductDeal.setIsClean(dto.getIsClean()); 
        // 关联系统交易编号 = A.成交编号
        trdProductDeal.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
        // 起息日 = 产品起息日
        trdProductDeal.setVdate(dto.getProdVdate());//add by dongjun on 20181106
        
        /**add by chendijun  2018/11/09 start**/
        // 原交易编号 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET
        String fTradeId = StringUtil.EMPTY;
        if(StringUtil.equals(trdProductDeal.getTradeStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
            fTradeId = InterfCommonService.getFTradeId(trdProductDeal);
        } 
        trdProductDeal.setpTradeId(fTradeId);
        /**add by chendijun  2018/11/09 end**/
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
    private TrdProductDeal getValFinProductDeal(ValPrdPeriodDeal dto) {
        // 初始化 金融产品交易 返回值
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        // 交易编号 = 调用共通方法生成
        trdProductDeal.setTradeId(interfCommonService.getTradeid(dto.getDealType(),
            dto.getBusiDate(), "A"));
        // 业务类型 = 数据字典：业务类型.费用交易
        trdProductDeal.setBusiType(DictCodeList.业务类型_预期收益型TA交易.getValue()); 
        // 金融产品代码 = A.产品代码
        trdProductDeal.setFinprodId(dto.getProdId());
        trdProductDeal.setFinprodType(dto.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        /** modify by dongjun on 20181029 start **/
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        trdProductDeal.setBranch(branch);
        /** modify by dongjun on 20181029 end **/
        // 交易日 = A.交易日
        trdProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.业务日期
        trdProductDeal.setSettleDate(dto.getBusiDate());
        // 到期日 = A.付息截止日
        trdProductDeal.setMdate(dto.getCalEndDay());
        // 交易币种 = A.币种
        trdProductDeal.setCcy(dto.getCcy());
        // 交易份额 = A.本金金额
        trdProductDeal.setShareAmt(dto.getCapiAmt());
        // 交易本金 = A.本金金额
        trdProductDeal.setPrinAmt(dto.getCapiAmt());
        // 利息总额 = A.利息金额
        trdProductDeal.setIntAmt(dto.getIntAmt());
        // 交易金额 = A.交易金额
        trdProductDeal.setTradeAmt(dto.getDealAmt());
        // 交易方向 = A.交易类型
        trdProductDeal.setPs(dto.getDealType());
        // 交易状态 = A.数据动作标志 新增 --> 01:初始 撤销 --> 03:撤销
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = A.是否结清
        trdProductDeal.setIsClean(dto.getIsClean());       
        // 关联系统交易编号 = A.成交编号
        trdProductDeal.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
        // 起息日 = 产品起息日
        trdProductDeal.setVdate(dto.getProdVdate());//add by dongjun on 20181106
        /** modify by dongjun on 20181029 start**/
        // 原交易编号 = 调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET
        String fTradeId = StringUtil.EMPTY;
        if(StringUtil.equals(trdProductDeal.getTradeStatus(),DictCodeListExt1.交易状态_撤销.getValue())){
            fTradeId = InterfCommonService.getFTradeId(trdProductDeal);
        } 
        trdProductDeal.setpTradeId(fTradeId);
        /** modify by dongjun on 20181029 end**/

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
    private TrdProductDealAdd getValFinProductDealExt(ValPrdPeriodDeal dto, TrdProductDeal deal) {
    	TrdProductDealAdd trdProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        trdProductDealExt.setTradeId(deal.getTradeId()); 
        //投资组合代码=A.投资组合代码
        trdProductDealExt.setPortfolioId(dto.getPortfolioId());
        //理财产品代码=A.产品代码
        /** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        trdProductDealExt.setProdId(dto.getProdId());
        **/
        trdProductDealExt.setPortfolioId(dto.getProdId());
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())){
//        	trdProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
//        	trdProductDealExt.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
        // 是否有结算指令 = A.有无清算指令
//        trdProductDealExt.setHasOrder(dto.getIsSettle());
        // 生成方式 = 固定值 接口接入
        trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 创建时间 = 固定值 系统时间
        trdProductDealExt.setCreateTime(dto.getCreateTime());
        return trdProductDealExt;

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
//        if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(valFinProductDeal.getFinprodType())){
//        	// 金融产品类型
//            dto.setFinprodType(interfCommonService.queryProductType(valFinProductDeal.getFinprodId()));
//        }else{
//        	// 金融产品类型
//            dto.setFinprodType(valFinProductDeal.getFinprodType());
//        }
//        // 业务类型
//        dto.setBusiType(valFinProductDeal.getBusiType());
//        // 理财产品代码
//        dto.setProdId(valFinProductDeal.getFinprodId());
//        // 币种
//        dto.setCcy(valFinProductDeal.getCcy());
//        // 是否随交易出账
//        dto.setIsTrade(DictCodeList.是否_是.getValue());
//        // 账套信息List
//        dto.setValBooksetInfoDtoList(valBooksetInfoDtoList);
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
//             * 1.预期型产品-产品认购申购-收取
//             */
//            insType = DictCodeListExt1.指令类型_预期收益型产品_产品认购申购_收取.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders);
//            listValOrdersExt.add(ValOrdersExt);
//            
//        }else if(DictCodeList.交易方向_产品赎回.getValue().equals(valFinProductDeal.getPs())
//                || DictCodeList.交易方向_产品到期.getValue().equals(valFinProductDeal.getPs())
//                ) {
//            
//            //随交易生成的指令
//            insType = DictCodeListExt1.指令类型_预期收益型产品_产品赎回到期.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//            
//            //资金交收指令
//            insType = DictCodeListExt1.指令类型_预期收益型产品_产品赎回到期_产品本金交易_支付.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_否.getValue());
//            
//            ValOrders1=(StlOrders)map.get("ORDERS");
//            ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//            
//            
//            List<BokDealAccounting> bokDealAccountingList = new ArrayList<BokDealAccounting>();
//            if(map.get("ACCOUNTING_LIST")!=null){
//            	bokDealAccountingList = (List<BokDealAccounting>)map.get("ACCOUNTING_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDealAccountingList)){
//            		listValDealAccounting.addAll(bokDealAccountingList);
//            	}
//            }
//            List<BokDealAccountingDetail> bokDealAccountingDetailList = new ArrayList<BokDealAccountingDetail>();
//            if(map.get("ACCOUNTING_DETAI_LIST")!=null){
//            	bokDealAccountingDetailList = (List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDealAccountingDetailList)){
//            		listValDealAccountingDetail.addAll(bokDealAccountingDetailList);
//            	}
//            }
//            List<BokDetailId> bokDetailIdList = new ArrayList<BokDetailId>();
//            if(map.get("BOK_DETAIL_ID_LIST")!=null){
//            	bokDetailIdList = (List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDetailIdList)){
//            		 listValBokDetailId.addAll(bokDetailIdList);
//            	}
//            }
//        }else if(DictCodeList.交易方向_中间付息.getValue().equals(valFinProductDeal.getPs())
//            || DictCodeList.交易方向_赎回收益确认.getValue().equals(valFinProductDeal.getPs())) {
//        
//            //随交易生成的指令
//            insType = DictCodeListExt1.指令类型_预期收益型产品_兑付客户收益.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_是.getValue());
//            
//            StlOrders ValOrders1=(StlOrders)map.get("ORDERS");
//            StlOrdersExt ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//          
//            //资金交收指令
//            insType = DictCodeListExt1.指令类型_预期收益型产品_支付客户收益.getValue();
//            map = interfCommonService.settlementInstructionGeneration_Layering(dto, insType,
//                DictCodeList.是否_否.getValue());
//            
//            ValOrders1=(StlOrders)map.get("ORDERS");
//            ValOrdersExt1=(StlOrdersExt)map.get("ORDERS_EXT");
//            listValOrders.add(ValOrders1);
//            listValOrdersExt.add(ValOrdersExt1);
//            
//            /**add by wujie on 20181107 start remark:中间付息、赎回收益确认**/
//            List<BokDealAccounting> bokDealAccountingList = new ArrayList<BokDealAccounting>();
//            if(map.get("ACCOUNTING_LIST")!=null){
//            	bokDealAccountingList = (List<BokDealAccounting>)map.get("ACCOUNTING_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDealAccountingList)){
//            		listValDealAccounting.addAll(bokDealAccountingList);
//            	}
//            }
//            List<BokDealAccountingDetail> bokDealAccountingDetailList = new ArrayList<BokDealAccountingDetail>();
//            if(map.get("ACCOUNTING_DETAI_LIST")!=null){
//            	bokDealAccountingDetailList = (List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDealAccountingDetailList)){
//            		listValDealAccountingDetail.addAll(bokDealAccountingDetailList);
//            	}
//            }
//            List<BokDetailId> bokDetailIdList = new ArrayList<BokDetailId>();
//            if(map.get("BOK_DETAIL_ID_LIST")!=null){
//            	bokDetailIdList = (List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST");
//            	if (CollectionUtils.isNotEmpty(bokDetailIdList)){
//            		 listValBokDetailId.addAll(bokDetailIdList);
//            	}
//            }
//            /**add by wujie on 20181107 end**/
//        }
//        
//        map.put("ORDERS", listValOrders);
//        map.put("ORDERS_EXT", listValOrdersExt);
//        map.put("ACCOUNTING", listValDealAccounting);
//        map.put("ACCOUNTING_DETAI", listValDealAccountingDetail);
//        map.put("BOK_DETAIL_ID", listValBokDetailId);
//        return map;
//    }
}
