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

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValStockCirculate;
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
 * @see StockCirculateTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class StockCirculateTemplateRecService {

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
        List<ValStockCirculate> listValStockCirculate = new ArrayList<ValStockCirculate>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);
        
        
        listValStockCirculate = returnResult.getT(ValStockCirculate.class);
        //获取数据数量
        result.setDataNum(listValStockCirculate.size());
        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/


        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValStockCirculate(listValStockCirculate,acctEntity.getResovleDate(),params,returnResult.getValInterflog());

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
        
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        // 返回处理结果数据
        return result;
    }

    /**
     * 01-买卖交易
     * 
     * @param listValStockCirculate 净值型产品存续期交易
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List + 交易费用明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> handleValStockCirculate(List<ValStockCirculate> listValStockCirculate,Date resovleDate,Map<String,Object> params,
			List<ValInterflog> valInterflog) {
        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
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
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();

        // 账务明细代码表List
        List<TrdTradeNewStockLocked> listTrdTradeNewStockLocked = new ArrayList<TrdTradeNewStockLocked>();
        
        List<ValInterflog> valInterflogList =  new ArrayList<>();

        // 查询交易类型和交易方向映射配置表
        List<FinProductTypeConfig> configList = interfCommonService.getConfigListByType("");

        for (ValStockCirculate dto : listValStockCirculate) {
        	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}
        	
            dto.setCreateTime(resovleDate);//add by chufengjie on 20180720 remark:用于去重复数据
            // 金融产品交易
            TrdProductDeal valFinProductDeal = this.getValFinProductDeal(dto);
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
            //结算指令
            Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,null,
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

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }
    
    /**
     * T1:金融产品交易表 接收
     * 
     * @param dto 01-买卖交易
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValStockCirculate dto) {
        // 金融产品交易
        TrdProductDeal valFinProductDeal = new TrdProductDeal();

        // 交易方向 = 上市流通
        valFinProductDeal.setPs(DictCodeList.交易方向_上市流通.getValue());
        //定义编号
        /**由于股票交易不自动生成结算指令，需要根据上游交易编号来关联指令，交易编号直接取上游交易编号 update by yinya on 20211207**/
        String tradeId=null;
        if(dto.getDataActionSign().equals(DictCodeListExt1.交易状态_初始.getValue())){
            tradeId=dto.getTradeId();
        }else{
            tradeId=interfCommonService.getTradeid(valFinProductDeal.getPs(),dto.getDealDate(),"A");
        }

        // 交易编号 = 共通生成
        valFinProductDeal.setTradeId(tradeId);
        // 交易场所为银行间则业务类型为股票交易，反之为交易场所股票
	    valFinProductDeal.setBusiType(DictCodeList.业务类型_股票交易.getValue());
        // 金融产品代码 = A.资产代码
        valFinProductDeal.setFinprodId(dto.getAssetId());
        // 金融产品类型 = F10 股票
        valFinProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
        // 分支序号 = 取对应预计上市流通日
        valFinProductDeal.setBranch(new Long(DateUtil.format(dto.getExpCirculateDate(), "YYYYMMDD")));
        // 交易日 = A.交易日
        valFinProductDeal.setTradeDate(dto.getDealDate());
        // 交割日 = A.交割日
        valFinProductDeal.setSettleDate(dto.getSettdateDate());
        // 交易币种 = A.币种
        valFinProductDeal.setCcy(dto.getCcy());
        // 单位净价 = A.成交价格
        valFinProductDeal.setUnitCprice(dto.getUnitCprice());
        // 交易份额 = A.成交数量/100
        valFinProductDeal.setShareAmt(ArithmeticUtil.divide(dto.getDealNum(), new BigDecimal(100),2));
        // 净价总额 = A.中签金额
        valFinProductDeal.setCpriceAmt(ArithmeticUtil.multiply(dto.getDealNum(), dto.getUnitCprice()));
        // 交易金额 = 净价总额
        valFinProductDeal.setDealAmt(valFinProductDeal.getCpriceAmt());
        // 交易状态 = A.数据动作标志
        valFinProductDeal.setTradeStatus(dto.getDataActionSign());
        // 是否结清 = 固定值：设置默认值
        valFinProductDeal.setIsClean(InterfCommonConstant.是否结清_默认);
        // 交易场所 = A.交易场所
        valFinProductDeal.setTradeMarket(dto.getTradeMarket());
        /*modify by sunshuaishuai 交易管理 20220325 start*/
        // 关联系统交易编号 = A.成交编号
        valFinProductDeal.setfTradeId(dto.getTradeId());
        /*modify by sunshuaishuai 交易管理 20220325 end*/
        //是否生成结算指令 = 否
        valFinProductDeal.setIsOrders(DictCodeList.是否_否.getValue());
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
     * @param dto 01-买卖交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValStockCirculate dto,
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

}
