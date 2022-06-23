/*
 * 文件名：PrdSupplementDealTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.prdclawback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.PrdSupplementDealRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValPrdSupplementDeal;

/**
 * 产品回补交易 模板处理Service(VAL_PRD_SUPPLEMENT_DEAL)
 * 
 * @author dongjun
 * @version 1.0 2019年2月21日
 * @see PrdSupplementDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PrdSupplementDealTemplateRecService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PrdSupplementDealRecCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                    TemplateOrderInfoReaderResult acctEntity) {

        PrdSupplementDealRecCommonResult result = new PrdSupplementDealRecCommonResult();
        List<ValPrdSupplementDeal> listValPrdSupplementDeal = new ArrayList<ValPrdSupplementDeal>();
        Map<String,Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValPrdSupplementDeal = returnResult.getT(ValPrdSupplementDeal.class);
        // 获取数据数量
        result.setDataNum(listValPrdSupplementDeal.size());

        /**add by wujie on 20210506 start**/
        if(returnResult.isReadFile()){
            result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
        }
        /**add by wujie on 20210506 end**/

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = this.handleValPrdSupplementDeal(listValPrdSupplementDeal,
                acctEntity, params,returnResult.getValInterflog());
        if (map != null) {
            result.setListValFinProductDeal((List<TrdProductDeal>)map.get("listValFinProductDeal"));
            result.setListValFinProductDealExt(
                    (List<TrdProductDealAdd>)map.get("listValFinProductDealExt"));
            result.setBokDealAccountingList((List<BokDealAccounting>)map.get("listValDealAccounting"));
            result.setBokDealAccountingDetailList(
                    (List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail"));
            result.setBokDetailIdList((List<BokDetailId>)map.get("listValBokDetailId"));
            // 添加日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }



        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     * 
     * @param listValPrdSupplementDeal List<ValPrdSupplementDeal>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> handleValPrdSupplementDeal(List<ValPrdSupplementDeal> listValPrdSupplementDeal,TemplateOrderInfoReaderResult acctEntity,
                                                   		Map<String,Object> params, List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();

        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();

        List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	String valModel = (String)params.get(InterfCommonConstant.接口模式);
        
    	List<ValInterflog> valInterflogList =  new ArrayList<>();
    	
        for (ValPrdSupplementDeal item : listValPrdSupplementDeal) {
        	if(StringUtil.equals(valModel, InterfCommonConstant.接口模式_指定账套) && !bookSetIds.contains(item.getProdId())){
        		continue;
        	}
            item.setCreateTime(acctEntity.getResovleDate());

            // 金融产品交易 处理
            TrdProductDeal valFinProductDeal = getValFinProductDeal(item);
            listValFinProductDeal.add(valFinProductDeal);

            // 金融产品交易扩展 处理
            TrdProductDealAdd valFinProductDealExt = getValFinProductDealExt(item,
                valFinProductDeal);
            listValFinProductDealExt.add(valFinProductDealExt);

            // 入账信息表 账务明细表调用共通
            InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting_Layering(valFinProductDeal,
                    item.getProdId(),item.getProdVdate(),item.getFinprodType());


            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingList())) {
                listValDealAccounting.addAll(dealAccounting.getBokDealAccountingList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDealAccountingDetailList())) {
                listValDealAccountingDetail.addAll(dealAccounting.getBokDealAccountingDetailList());
            }
            if (CollectionUtils.isNotEmpty(dealAccounting.getBokDetailIdList())) {
                //设置账务明细代码表的四级科目号
                String detailSubjectNoTail = item.getProdId();
                String detailSubnameTail = item.getProdName();
                if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(item.getFinprodType())){
                    detailSubjectNoTail += new SimpleDateFormat("yyyyMMdd").format(item.getProdVdate());
                    detailSubnameTail += new SimpleDateFormat("yyyyMMdd").format(item.getProdVdate());
                }
                detailSubjectNoTail = detailSubjectNoTail+ FinalString.LAYERING_SPLIT+item.getFinprodType();
                interfCommonService.setDetailSubjectNoTail(dealAccounting.getBokDetailIdList(),
                        detailSubjectNoTail, detailSubnameTail);
                listValBokDetailId.addAll(dealAccounting.getBokDetailIdList());
            }
        }

        resultMap.put("listValFinProductDeal", listValFinProductDeal);
        resultMap.put("listValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("listValDealAccounting", listValDealAccounting);
        resultMap.put("listValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("listValBokDetailId", listValBokDetailId);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
    	resultMap.put("valInterflogList", valInterflogList);
        return resultMap;

    }

    /**
     * 接收 金融产品交易 T1
     * 
     * @param dto ValPrdSupplementDeal
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValPrdSupplementDeal dto) {
        TrdProductDeal result = new TrdProductDeal();
        // 业务类型
        result.setBusiType(DictCodeList.业务类型_产品回补交易.getValue());
        // 交易编号
        String tradeId = interfCommonService.getTradeid(DictCodeList.交易方向_回补.getValue(),
            dto.getPayDate(), "A");
        result.setTradeId(tradeId);


        // 金融产品代码
        Long branch;
        if (StringUtil.equals(dto.getFinprodType(), DictCodeListExt1.金融产品类型_理财子产品)) {
            branch = Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(dto.getProdVdate()));
         }
        else {
            branch = InterfCommonConstant.分支序号_默认;
        }
        result.setFinprodId(dto.getProdId());
        // 金融产品类型
//        result.setFinprodType(DictCodeListExt1.现金流二级类型_固定管理费.getValue());
        String feeId = dto.getProdId()+DictCodeListExt1.现金流二级类型_浮动管理费.getValue();
        // 产品代码+现金流二级类型 _浮动管理费= 费用类型 or 现金流二级类型 _浮动管理费 = 费用类型
        if(feeId.equals(dto.getFeeId()) || DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(dto.getFeeId())){
            // 金融产品类型
            result.setFinprodType(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        }
        else{
            // 金融产品类型
            result.setFinprodType(DictCodeListExt1.现金流二级类型_固定管理费.getValue());
        }

        // 分支序号
        result.setBranch(branch);
        // 交易日
        result.setTradeDate(dto.getPayDate());
        // 起息日= A.回补周期起始日
        result.setVdate(dto.getCalStartDay());
        // 到期日 = A.回补周期结束日
        result.setMdate(dto.getCalEndDay());
        // 交割日 = A.支付日
        result.setSettleDate(dto.getPayDate());
        // 交易币种 = A.币种
        result.setCcy(dto.getCcy());
        // 利息总额 = A.实付费用
        result.setIntAmt(dto.getActPayAmt());
        // 交易方向 N 固定值 数据字典：交易方向.回补
        result.setPs(DictCodeList.交易方向_回补.getValue());
        // 交易状态
        result.setTradeStatus(dto.getDataActionSign());
        // 原交易编号
        if (result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())) {
            result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        // 是否结清 = A.是否结算
        result.setIsClean(dto.getIsClean());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        result.setUpdateTime(dto.getCreateTime());
        result.setCreateUser("interf");
        result.setUpdateUser("interf");
        return result;
    }

    /**
     * 接收 估值核算交易扩展表 T2
     * 
     * @param dto ValPrdSupplementDeal
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValPrdSupplementDeal dto,
                                                      TrdProductDeal deal) {
    	TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = A.产品代码
//        if (DictCodeListExt1.金融产品类型_投资组合.getValue().equals(dto.getFinprodType())) {
            result.setPortfolioId(dto.getProdId());
//        }
//        else {
//            result.setProdId(dto.getProdId());
//        }
        // 是否有结算指令 = A.有无清算指令
//        result.setHasOrder(dto.getIsClean());
        // 生成方式
        result.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());;
        result.setCreateTime(dto.getCreateTime());
        return result;
    }
}
