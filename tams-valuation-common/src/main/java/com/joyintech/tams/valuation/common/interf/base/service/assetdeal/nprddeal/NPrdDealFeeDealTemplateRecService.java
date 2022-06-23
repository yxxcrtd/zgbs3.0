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
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.NProdDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdBonusDealN;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdFeeDealN;
import com.joyintech.framework.util.StringUtil;

/**
 * 净值型产品费用交易表 模板处理Service(VAL_PRD_FEE_DEAL_N)
 * 
 * @author 魏鹏
 * @version 1.0 2018年6月25日
 * @see NPrdDealFeeDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NPrdDealFeeDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    @Autowired
    private  InterfCommonService interfCommonService;

    /**
     * 净值型产品交易费用交易 接口模板处理
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
        List<ValPrdFeeDealN> listValPrdFeeDealN = new ArrayList<ValPrdFeeDealN>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdFeeDealN = returnResult.getT(ValPrdBonusDealN.class);
        result.setDatanum(listValPrdFeeDealN==null?0:listValPrdFeeDealN.size());
        if (listValPrdFeeDealN == null) {
        	listValPrdFeeDealN = new ArrayList<ValPrdFeeDealN>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValPrdBonusDealN(listValPrdFeeDealN,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
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
            
            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                "listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                "listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            
            /**add by wujie on 20190129 start**/
            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
            result.setListValTradeFeeDetail(listValTradeFeeDetail);
            /**add by wujie on 20190129 end**/
            
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
         }
        // 返回处理结果数据
        return result;
    }

    /**
     * 02-净值型产品分红
     * 
     * @param listValPrdFeeDealN 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    private Map<String, Object> handleValPrdBonusDealN(List<ValPrdFeeDealN> listValPrdFeeDealN,Date resovleDate, Map<String, Object> params,
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
        
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
		
        /**add by wujie on 20190129 start**/
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        /**add by wujie on 20190129 end**/
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();
		
        for (ValPrdFeeDealN dto : listValPrdFeeDealN) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	dto.setCreateTime(resovleDate);
        	
        	/**modify by wujie on 20190122 remark:重复导入删除逻辑调整
        	cqFtradeIds.add(dto.getTradeId());
        	**/
        	
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);
            
            /**add by wujie on 20190129 start**/
            // 交易费用明细表
            TrdTradeFeeDetail tgTradeFeeDetail = this.getValTradeFeeDetail(dto, valFinProductDeal);
            listValTradeFeeDetail.add(tgTradeFeeDetail);
            /**add by wujie on 20190129 end**/
            
            // 入账信息表   账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(valFinProductDeal, 
            		dto.getProdId(),dto.getProdVdate(),dto.getFinprodType());
            
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
            //结算指令
            /**modify by wujie on 20190122 start
            Map<String, Object> map = this.getvalOrders(valFinProductDeal,valFinProductDealExt.getProdId(),dealAccounting.getValBooksetInfoDtoList());
            StlOrders ValOrders=(StlOrders)map.get("ORDERS");
            StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
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
        
        /**add by wujie on 20190129 start**/
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);
        /**add by wujie on 20190129 end**/
        
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
		
        
        return resultMap;
    }

    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 02-净值型产品分红
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdFeeDealN dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        valFinProductDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_产品付费.getValue(), dto.getPayDate(), "A"));
        // 业务类型 = 固定值: 18 费用交易
        valFinProductDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
        
        String cashId = null;
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            cashId = InterfHelper.getMd5CashId_(dto.getProdId() + new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()),
                dto.getFeeId());
        } else {
            cashId = InterfHelper.getMd5CashId_(dto.getProdId() , dto.getFeeId());
        } 
        //金融产品代码 = 现金流代码
        valFinProductDeal.setFinprodId(cashId);
        
        /**add by wujie on 20190422 start**/
	     //金融产品代码 = A.产品代码
        valFinProductDeal.setFinprodId(dto.getProdId());
	    /**add by wujie on 20190422 end**/
        
        //金融产品类型 = 调用共通查询现金流信息.现金流二级类型
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("cashId", cashId);
        FinCashInfo finCashInfo = interfCommonService.queryCashInfo(paramMap);
        if(finCashInfo!=null){
        	valFinProductDeal.setFinprodType(finCashInfo.getCashType2());
        }
        else{
            if(DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(dto.getFeeId())){
                valFinProductDeal.setFinprodType(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            }
        }
        
        //交割日 = C.支付日
        valFinProductDeal.setSettleDate(dto.getPayDate());
        // 分支序号 = 设置全局变量(默认为0）
        // 分支序号 = 设置全局变量(默认为0）
        Long branch = null; 
        if(StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品.getValue())){
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
        } else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        valFinProductDeal.setBranch(branch);
        // 交易日 = C.支付日
        valFinProductDeal.setTradeDate(dto.getPayDate());
        // 起息日 = C.计提起始日
        valFinProductDeal.setVdate(dto.getCalStartDay());
        // 到期日 = C.计提结束日
        valFinProductDeal.setMdate(dto.getCalEndDay());
        // 交易币种 = C.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 交易金额 = C.实付费用
        valFinProductDeal.setTradeAmt(dto.getActPayAmt());
        // 交易方向 = 固定值：C11 产品付费
        valFinProductDeal.setPs(DictCodeList.交易方向_产品付费.getValue());
        // 交易状态 = C.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        /**MODIFY BY WUJIE ON 20210501 START
        // 是否结清 = 固定值: ---收息付费时用
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
         **/
        if(StringUtil.isNotEmpty(dto.getIsClean2())){
            valFinProductDeal.setIsClean(dto.getIsClean2());
        }else{
            valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        }

        /**MODIFY BY WUJIE ON 20210501 END**/
        // 关联系统交易编号 = C.成交编号
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
     * @param dto 02-净值型产品分红
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValPrdFeeDealN dto,
                                                         TrdProductDeal t1) {
        // 估值核算交易扩展表
    	TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = B.产品代码
//        valFinProductDealExt.setProdId(dto.getProdId());
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        // 是否有结算指令 = C.有无清算指令
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
     * @param dto 01-买卖交易
     * @param valFinProductDeal 
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private TrdTradeFeeDetail getValTradeFeeDetail(ValPrdFeeDealN dto, TrdProductDeal valFinProductDeal) {
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
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param valFinProductDeal
//     * @return
//     * @see
//     * @since 1.0
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String finprodId,List<ValBooksetInfoDto> valBooksetInfoDtoList) {
//        //交易指令 调用共通 
//        ProductDealDto productDealDto=new ProductDealDto();
//        productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//        productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//        productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//        productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//        productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//        productDealDto.setFinprodId(finprodId);//valFinProductDeal.getFinprodId()是经过MD5处理过的
//        /** modfiy by wujie on 20181022 end remark:支持投资组合投资资产的情况
//        productDealDto.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue()); // * 金融产品类型
//        **/
//        productDealDto.setFinprodType(interfCommonService.queryProductType(finprodId)); 
//        /**modify by wujie on 20181022 end**/
//        productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//        productDealDto.setProdId(finprodId);//valFinProductDeal.getFinprodId()是经过MD5处理过的
//        productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//        productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
////        productDealDto.setBookNo(bookSetId);
//        productDealDto.setValBooksetInfoDtoList(valBooksetInfoDtoList);
//        productDealDto.setCreateTime(valFinProductDeal.getCreateTime());
//        /**add by chendijun  2018/11/15 start**/
//        //原交易编号
//        productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//        /**add by chendijun  2018/11/15 end**/
//        return interfCommonService.settlementInstructionGeneration_Layering(productDealDto, 
//        		DictCodeListExt1.指令类型_产品其他费用_支付.getValue(),DictCodeList.是否_是.getValue());
//    }

}
