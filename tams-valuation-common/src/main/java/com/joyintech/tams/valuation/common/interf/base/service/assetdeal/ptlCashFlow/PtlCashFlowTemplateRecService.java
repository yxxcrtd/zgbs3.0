/*
 * 文件名：PtlCashFlowTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.ptlCashFlow;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValCashflowTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 自定义现金流交易 模板处理Service(VAL_PTL_CASH_FLOW)
 * 
 * @author tanxuan
 * @version 1.0 2020年4月3日
 * @see PtlCashFlowTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PtlCashFlowTemplateRecService {

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
     * 自定义现金流交易 接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 自定义现金流交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PtlCashFlowCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                           TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
    	PtlCashFlowCommonResult result = new PtlCashFlowCommonResult();
        List<ValCashflowTrade> listPtlCashFlow = new ArrayList<ValCashflowTrade>();
        Map<String,Object> params = acctEntity.getParams();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

        // 1、文件读取解析
        if (DictCodeInterfList.接口文件类型_XML.getValue().equals(acctEntity.getTmpType())) {
            // 模板类型为XML时候，调用XML解析功能，并返回解析后的数据结构
            returnResult = templateAnalysisForXMLService.execute(acctEntity);
        }
        else if (DictCodeInterfList.接口文件类型_TXT.getValue().equals(acctEntity.getTmpType())) {

        }
        else if (DictCodeInterfList.接口文件类型_EXCEL.getValue().equals(acctEntity.getTmpType())) {

        }
        listPtlCashFlow = returnResult.getT(ValCashflowTrade.class);
        //获取数据数量
        result.setDataNum(listPtlCashFlow.size());

        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValCashflowTrade(listPtlCashFlow,acctEntity.getResovleDate(),params);

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
            "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
            "ValFinProductDealExt");
        result.setListValFinProductDealAdd(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
            "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
            "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
            "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 交易费用明细表List
        List<TgTradeFeeDetail> listValTradeFeeDetail = (List<TgTradeFeeDetail>)map.get(
            "ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-自定义现金流交易
     * 
     * @param listPtlCashFlow 自定义现金流交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> handleValCashflowTrade(List<ValCashflowTrade> listPtlCashFlow,Date resovleDate,Map<String,Object> params) {
        List<String> bookSetIds = (List)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
    	
    	Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
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
        
        for (ValCashflowTrade dto : listPtlCashFlow) {
        	
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
            listValFinProductDeal.add(valFinProductDeal);

            dto.setTradeId(valFinProductDeal.getTradeId());
            // 估值核算交易扩展表
            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(dto,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);
           
            // 入账信息表   账务明细表、账务明细代码表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
            listValDealAccounting.add(dealAccounting.getBokDealAccounting());
            listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());

            BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
            //四级科目尾号
            valBokDetailId.setDetailSubjectNoTail(dto.getProdId());
            //四级科目尾号名称
            valBokDetailId.setDetailSubnameTail(dto.getProdName());
            
            listValBokDetailId.add(valBokDetailId);

            //结算指令
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
        }
        resultMap.put("valOrdersList", valOrdersList);
        resultMap.put("valOrdersExtList", valOrdersExtList);
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        
        return resultMap;
    }


    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-自定义现金流交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValCashflowTrade dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();
        // 关联系统交易编号 = B.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
        //定义编号
        String tradeId=interfCommonService.getTradeid(dto.getPs(),dto.getTradeDate(),"A");
        // 交易编号 = 共通生成
        valFinProductDeal.setTradeId(tradeId);
        valFinProductDeal.setBusiType(DictCodeListExt3.业务类型_自定义现金流交易.getValue());
        // 金融产品代码 = A.产品代码
        valFinProductDeal.setFinprodId(dto.getProdId());
        // 金融产品类型 = A.金融产品类型
        valFinProductDeal.setFinprodType(dto.getFinprodType());
        // IF A.金融产品类型 IN (投资组合,理财产品) 分支序号 = 0 ELSE 分支序号 = 转换数字(A.产品起息日) END IF
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())
        		|| DictCodeListExt1.金融产品类型_理财产品.getValue().equals(dto.getFinprodType())){
        	valFinProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
        }else{
        	String sProdVdate = DateUtil.format(dto.getProdVdate());
        	if(StringUtil.isNotEmpty(sProdVdate)){
        		valFinProductDeal.setBranch(Long.valueOf(sProdVdate));
        	}
        }
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getTradeDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 交易金额 = A.交易金额
        valFinProductDeal.setTradeAmt(dto.getTradeAmt());
        // 交易方向 = A.交易方向
        valFinProductDeal.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if(valFinProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
        	valFinProductDeal.setpTradeId(interfCommonService.getFTradeId(valFinProductDeal));
        }
        // 创建时间 = 系统时间
        valFinProductDeal.setCreateTime(dto.getCreateTime());
        valFinProductDeal.setCreateDept(null);
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
    private TrdProductDealAdd getValFinProductDealExt(ValCashflowTrade dto,
                                                         TrdProductDeal t1) {
        // 估值核算交易扩展表
        TrdProductDealAdd valFinProductDealExt = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        valFinProductDealExt.setTradeId(t1.getTradeId());
        // 理财产品代码 = A.产品代码（投资组合投资时应赋值[表.投资组合代码]字段）
        String finprodType = interfCommonService.queryProductType(dto.getProdId());
        valFinProductDealExt.setPortfolioId(dto.getProdId());
        //生成方式 = 接口接入
        valFinProductDealExt.setGenType(InterfCommonConstant.接入方式);
        // 创建时间 = 系统时间
        valFinProductDealExt.setCreateTime(dto.getCreateTime());
        valFinProductDealExt.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return valFinProductDealExt;
    }

    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValCashflowTrade dto, TrdProductDeal valFinProductDeal){
        StlOrders result = new StlOrders();
        //modify by yanya on 20210204 判定为空包含空字符
        //if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
        if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
            return null;
        }
        result.setMyAcctNo(dto.getMyAcctNo());
        result.setCounterAcctNo(dto.getCounterAcctNo());
        result.setSettleDate(dto.getTradeDate());
        result.setSettleAmt(valFinProductDeal.getTradeAmt());
        return result;
    }
}
