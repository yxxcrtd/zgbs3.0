/*
 * 文件名：PACDApplyPurDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2019年1月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfApplyPurTade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 计划类资产货币型申购申请交易
 * 
 * @author HuangJian
 * @version 1.0 2019年1月23日
 * @see CPlannedApplyPurDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPlannedApplyPurDealTemplateRecService {

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
        List<ValMngMnfApplyPurTade> listValMngMnfApplyPurTade = new ArrayList<ValMngMnfApplyPurTade>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValMngMnfApplyPurTade = returnResult.getT(ValMngMnfApplyPurTade.class);
        result.setDatanum(
            listValMngMnfApplyPurTade == null ? 0 : listValMngMnfApplyPurTade.size());
        if (listValMngMnfApplyPurTade == null) {
            listValMngMnfApplyPurTade = new ArrayList<ValMngMnfApplyPurTade>();
        }

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValMngMnfApplyPurTade(listValMngMnfApplyPurTade,acctEntity.getResovleDate(), params,returnResult.getValInterflog());
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
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        // 返回处理结果数据
        return result;

    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> handleValMngMnfApplyPurTade(List<ValMngMnfApplyPurTade> listValMngMnfApplyPurTade,Date resovleDate,
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
    	
        for (ValMngMnfApplyPurTade dto : listValMngMnfApplyPurTade) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(dto.getProdId())){
        		continue;
        	}

            TrdProductDeal trdProductDeal = this.getTrdProductDeal(dto);
            trdProductDeal.setCreateTime(resovleDate);
            listTrdProductDeal.add(trdProductDeal);

            /**modify by wujie on 20190625 start
             InterfProductDealBookCommonDto interfProductDealBookCommonDto = interfCommonService.dealAccounting(
             trdProductDeal, dto.getProdId());
             listBokDealAccounting.add(interfProductDealBookCommonDto.getBokDealAccounting());
             listBokDealAccountingDetail.add(
             interfProductDealBookCommonDto.getBokDealAccountingDetail());
             listBokDetailId.add(interfProductDealBookCommonDto.getBokDetailId());
             **/
            String acctSubjectId = dto.getProdId();
            if(!StringUtil.isEmpty(dto.getChannelId())){
                acctSubjectId = acctSubjectId+ FinalString.LAYERING_SPLIT+dto.getChannelId();
            }
            ValBooksetInfoDto bookSetInfo = interfCommonService.getBooksetInfoByProdId(acctSubjectId);
            String finprodType = null;
            if(bookSetInfo != null && bookSetInfo.getBooksetId()!=null){
                finprodType = bookSetInfo.getAcctSubjectType();
            }
            /**modify by wujie on 20190625 end**/

            TrdProductDealAdd trdProductDealExt = this.getTrdProductDealExt(dto,finprodType,trdProductDeal.getTradeId());
            trdProductDealExt.setCreateTime(resovleDate);
            listTrdProductDealExt.add(trdProductDealExt);
            Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDeal,
                    dto.getProdId(),finprodType, null);

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

    private TrdProductDeal getTrdProductDeal(ValMngMnfApplyPurTade dto) {
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
        /*modify by sunshuaishuai 交易管理 20220325 start*/
        // 交割日 = A.交易日
        if(StringUtil.isNotEmpty(dto.getSettleDate())){
            result.setSettleDate(dto.getSettleDate());
        }else {
            result.setSettleDate(dto.getTradeDate());
        }
        /*modify by sunshuaishuai 交易管理 20220325 end*/
        // 交易币种= A.币种
        result.setCcy(dto.getCcy());
        // 交易金额= A.申购金额
        result.setTradeAmt(dto.getPurAmt());
        // 交易方向 =数据字典：交易方向.申购
        result.setPs(DictCodeList.交易方向_申购.getValue());
        // 交易编号 = 调用编号生成共通方法
        result.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_申购.getValue(),
            dto.getTradeDate(), "A"));
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
        /*modify by sunshuaishuai 交易管理 20220325 start*/
        // 关联系统交易编号= A.成交编号
        result.setfTradeId(dto.getOutTradeId());
        /*modify by sunshuaishuai 交易管理 20220325 end*/
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

        /*add by sunshuaishuai 交易管理 20220325 start*/
        //资金账户
        result.setMyAcctNo(dto.getMyAcctNo());
        /*add by sunshuaishuai 20220325 end*/
        return result;
    }

    private TrdProductDealAdd getTrdProductDealExt(ValMngMnfApplyPurTade dto,String finprodType,String tradeId) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(tradeId);

        // 投资组合代码 =调用入账信息共通，返回核算主体类型
        // 1.核算主体类型 eq 数据字典：金融产品类型.投资组合
        // 投资组合代码 = A.产品代码
        // 2.核算主体类型 ne 数据字典：金融产品类型.投资组合
        // 理财产品代码 = A.产品代码
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//            result.setPortfolioId(dto.getProdId());
//        }else{
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
