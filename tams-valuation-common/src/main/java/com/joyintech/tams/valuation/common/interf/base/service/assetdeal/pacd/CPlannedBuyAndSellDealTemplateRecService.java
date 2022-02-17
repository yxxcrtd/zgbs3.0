/*
 * 文件名：PACDBuyAndSellDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2019年1月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
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
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;

/**
 * 计划类资产货币型买入卖出交易
 * 
 * @author HuangJian
 * @version 1.0 2019年1月23日
 * @see PACDBuyAndSellDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPlannedBuyAndSellDealTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param templateSourceFlgDTO
     * @param acctEntity
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PlanAssetCurrencyDealCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                     TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
        PlanAssetCurrencyDealCommonResult result = new PlanAssetCurrencyDealCommonResult();
        List<ValMngMnfTade> listValValMngMnfTade = new ArrayList<ValMngMnfTade>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValValMngMnfTade = returnResult.getT(ValMngMnfTade.class);
        result.setDatanum(listValValMngMnfTade == null ? 0 : listValValMngMnfTade.size());
        if (listValValMngMnfTade == null) {
            listValValMngMnfTade = new ArrayList<ValMngMnfTade>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValMngMnfTade(listValValMngMnfTade,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        if (map != null) {
            // 金融产品交易
            List<TrdProductDeal> listTrdProductDeal = (List<TrdProductDeal>)map.get(
                "TrdProductDeal");
            result.setListTrdProductDeal(listTrdProductDeal);
            // 估值核算交易扩展表
            List<TrdProductDealAdd> listTrdProductDealExt = (List<TrdProductDealAdd>)map.get(
                "TrdProductDealExt");
            result.setListTrdProductDealExt(listTrdProductDealExt);
            // 入账信息表
            List<BokDealAccounting> listBokDealAccounting = (List<BokDealAccounting>)map.get(
                "BokDealAccounting");
            result.setListBokDealAccounting(listBokDealAccounting);
            // 账务明细表
            List<BokDealAccountingDetail> listBokDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                "BokDealAccountingDetail");
            result.setListBokDealAccountingDetail(listBokDealAccountingDetail);
            // 账务明细代码表
            List<BokDetailId> listBokDetailId = (List<BokDetailId>)map.get("BokDetailId");
            result.setListBokDetailId(listBokDetailId);
            // 结算指令表
            List<StlOrders> listStlOrders = (List<StlOrders>)map.get("StlOrders");
            result.setListStlOrders(listStlOrders);
            // 结算指令扩展表
            List<StlOrdersExt> listStlOrdersExt = (List<StlOrdersExt>)map.get("StlOrdersExt");
            result.setListStlOrdersExt(listStlOrdersExt);
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        // 返回处理结果数据
        return result;

    }

    
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleValMngMnfTade(List<ValMngMnfTade> listValValMngMnfTade,Date resovleDate,
    		Map<String,Object> params, List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        // 金融产品交易
        List<TrdProductDeal> listTrdProductDeal = new ArrayList<TrdProductDeal>();
        // 估值核算交易扩展表
        List<TrdProductDealAdd> listTrdProductDealExt = new ArrayList<TrdProductDealAdd>();
        // 入账信息表
        List<BokDealAccounting> listBokDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表
        List<BokDealAccountingDetail> listBokDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();
        // 结算指令表
        List<StlOrders> listStlOrders = new ArrayList<StlOrders>();
        // 结算指令扩展表
        List<StlOrdersExt> listStlOrdersExt = new ArrayList<StlOrdersExt>();

        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        
    	List<ValInterflog> valInterflogList =  new ArrayList<>();
    	
        for (ValMngMnfTade dto : listValValMngMnfTade) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}

            TrdProductDeal trdProductDeal = this.getTrdProductDeal(dto);
            trdProductDeal.setCreateTime(resovleDate);
            listTrdProductDeal.add(trdProductDeal);
            InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
                trdProductDeal, dto.getProdId());
            listBokDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
            listBokDealAccountingDetail.add(
                interfProductDealBookCommonDto.getBokDealAccountingDetail());

            /**add by wujie on 20190620 start**/
            //四级科目尾号
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubjectNoTail(dto.getAssetId());
            //四级科目尾号名称
            interfProductDealBookCommonDto.getBokDetailId().setDetailSubnameTail(dto.getAssetName());
            /**add by wujie on 20190620 end**/

            listBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
            TrdProductDealAdd trdProductDealExt = this.getTrdProductDealExt(dto,
                interfProductDealBookCommonDto.getAcctSubjectType(),trdProductDeal.getTradeId());
            trdProductDealExt.setCreateTime(resovleDate);
            listTrdProductDealExt.add(trdProductDealExt);
            Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDeal,
                dto.getProdId(),interfProductDealBookCommonDto.getAcctSubjectType(), null);
            if (map.get("ORDERS") != null) {
                listStlOrders.addAll((List<StlOrders>)map.get("ORDERS"));
            }
            if (map.get("ORDERS_EXT") != null) {
                listStlOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
            }
            if (map.get("ACCOUNTING") != null) {
                listBokDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
            }
            if (map.get("ACCOUNTING_DETAI") != null) {
                listBokDealAccountingDetail.addAll(
                    (List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
            }
            if (map.get("BOK_DETAIL_ID") != null) {
                listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }

            /**add by wujie on 20190322 start reamrk:通道投资费用交易生成**/
            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, interfProductDealBookCommonDto.getAcctSubjectType());
            if(channelFeeInfoDeal != null){
                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(trdProductDeal, channelFeeInfoDeal);
                
                if(feedealMap.get("TrdProductDeal")!=null){
                    listTrdProductDeal.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
                }
                
                if(feedealMap.get("TrdProductDealExt")!=null){
                    listTrdProductDealExt.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
                }
                
                if(feedealMap.get("BokDealAccounting_List")!=null){
                    listBokDealAccounting.addAll((List<BokDealAccounting>)feedealMap.get("BokDealAccounting_List"));
                }
                
                if(feedealMap.get("BokDealAccountingDetail_List")!=null){
                    listBokDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)feedealMap.get("BokDealAccountingDetail_List"));
                }
                
                if(feedealMap.get("BokDetailId_list")!=null){
                    listBokDetailId.addAll((List<BokDetailId>)feedealMap.get("BokDetailId_list"));
                }
                
                if(feedealMap.get("StlOrders_List")!=null){
                    listStlOrders.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
                }
                if(feedealMap.get("StlOrdersExt_List")!=null){
                    listStlOrdersExt.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
                }
            }
            /**add by wujie on 20190322 end reamrk:通道投资费用交易生成**/
        }
        resultMap.put("TrdProductDeal", listTrdProductDeal);
        resultMap.put("TrdProductDealExt", listTrdProductDealExt);
        resultMap.put("BokDealAccounting", listBokDealAccounting);
        resultMap.put("BokDealAccountingDetail", listBokDealAccountingDetail);
        resultMap.put("BokDetailId", listBokDetailId);
        resultMap.put("StlOrders", listStlOrders);
        resultMap.put("StlOrdersExt", listStlOrdersExt);
        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        
        return resultMap;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValMngMnfTade dto,String acctSubjectType){
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
    
    private TrdProductDeal getTrdProductDeal(ValMngMnfTade dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 业务类型 = 数据字典：金融产品类型.货币基金交易
        result.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        // 金融产品代码=A.资产代码
        result.setFinprodId(dto.getAssetId());
        // 金融产品类型 =数据字典：金融产品类型.计划类资产
        result.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        // 分支序号=设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日 = A.交易日
        result.setTradeDate(dto.getTradeDate());
        // 交割日 = A.交易日
        result.setSettleDate(dto.getTradeDate());
        // 交易币种= A.币种
        result.setCcy(dto.getCcy());
        // 单位净价 =F.单位净价
        result.setUnitCprice(dto.getUnitCprice());
        // 净价总额 =F.交易本金
        result.setCpriceAmt(dto.getPrinAmt());
        // 交易份额 =交易份额
        result.setShareAmt(dto.getShareAmt());
        // 交易本金 =F.交易本金
        result.setPrinAmt(dto.getPrinAmt());
        // 利息总额 =F.应收红利
        result.setIntAmt(dto.getIntAmt());
        // 全价总额 =F.交易金额
        result.setFpriceAmt(dto.getTradeAmt());
        // 交易金额=F.交易金额
        result.setTradeAmt(dto.getTradeAmt());
        // 交易方向 =F.交易方向
        result.setPs(dto.getPs());
        // 交易编号 = 调用编号生成共通方法 TODO
        result.setTradeId(interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A"));
        // 交易状态 =A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());

        // 是否结清 =数据字典：是否.是
        result.setIsClean(DictCodeList.是否_是.getValue());
        // 交易对手 = A.交易对手
        result.setCounterId(dto.getCounterId());
        // 交易对手类型= A.交易对手类型
//        result.setCounterType(dto.getCounterType());
        // 交易场所 = A.交易场所
        result.setTradeMarket(dto.getTradeMarket());
        // 关联系统交易编号= A.成交编号
        result.setfTradeId(dto.getTradeId());
        // 原交易编号 =交易状态为撤销时：
        // 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET
        // 交易状态为非撤销时：
        // 原交易编号为空 TODO
        if (DictCodeListExt1.交易状态_撤销.getValue().equals(dto.getDataActionSign())) {

            result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        else {
            result.setpTradeId(null);
        }
      //通道代码 = a.通道代码
        result.setChlId(dto.getChannelId());
        return result;
    }

    private TrdProductDealAdd getTrdProductDealExt(ValMngMnfTade dto, String finprodType,String tradeId) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(tradeId);

        // 投资组合代码 =调用入账信息共通，返回核算主体类型
        // 1.核算主体类型 eq 数据字典：金融产品类型.投资组合
        // 投资组合代码 = A.产品代码
        // 2.核算主体类型 ne 数据字典：金融产品类型.投资组合
        // 理财产品代码 = A.产品代码 TODO
//        if (DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)) {
//            result.setPortfolioId(dto.getProdId());
//        }
//        else {
////            result.setProdId(dto.getProdId());
//        }
        result.setPortfolioId(dto.getProdId());
        // 是否有结算指令 = A.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 生成方式 InterfCommonConstant.接入方式
        result.setGenType(InterfCommonConstant.接入方式);
        return result;
    }

}
