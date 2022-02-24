/*
 * 文件名：TrdDepositInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：fanshuanghu
 * 修改时间：2018年10月18日 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;

import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfNeedOperateEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.depositasset.DepositeAssetAstDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotAstTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.constant.ProcessType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 存款类新建Service
 * 
 * @author fanshuanghu
 * @version 1.0 2018年10月18日
 * @see TrdDepositValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdDepositValuationTrade002Service extends CommonExportService<TrdDepositValuationTrade001Dto> implements BaseProcessService<TrdDepositValuationTrade001Dto> {



    @Autowired
    private DepositeAssetAstDealTemplateRecService depositeAssetAstDealTemplateRecService;

    @Autowired
    private TemplateOrderInfoReaderResult acctEntity;


    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 存款类资产实体类
     * @see
     * @since 1.0
     */
    public TrdDepositValuationTrade001Dto getInitEntity(String tradeId) {
        TrdDepositValuationTrade001Dto entity = new TrdDepositValuationTrade001Dto();
        TrdProductDeal trdProductDeal = null;
        TrdProductDealAdd trdProductDealAdd = null;
        String finprodId = null;
        FinProduct finProduct = null;
        return entity;
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/createPlanTradeId",
                null, String.class);

    }


    /**
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/findPortfolioId",
                params, SelectOptionDto.class);
    }

    /**
     * 资产代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findFinprodId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/findFinprodId",
                params, SelectOptionDto.class);
    }

    //交易对手
    public Page<SelectOptionDto> getCounterId(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME
                + "/TrdDepositValuationTrade001/getCounterId" , param,SelectOptionDto.class);
    }

    /**
     * 根据交易编号查询详情
     */
    public TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepositValuationTrade001/queryTradeInfoByTradeId",
                tradeId, TrdDepositValuationTrade001Dto.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdDepositValuationTrade001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdDepositValuationTradeResult trdDepositValuationTradeResult = new TrdDepositValuationTradeResult();
        trdDepositValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdDepositValuationTrade001/" + action, trdDepositValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdDepositValuationTradeResult generateEntity(TrdDepositValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValBotAstTrade> listValBotAstTrade=new ArrayList<>();
        ValBotAstTrade entity = new ValBotAstTrade();
        entity.setTradeId(dto.getTradeId());
        entity.setProdId(dto.getPortfolioId());
        entity.setAssetId(dto.getFinprodMarketId());
//        //金融产品类型
//        if("01".equals(dto.getPs())){
//            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
//        }else if("02".equals(dto.getPs())){
//            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
//        }else if("03".equals(dto.getPs())){
//            entity.setPs(DictCodeList.交易方向_到期.getValue());
//        }
        entity.setDealDate(dto.getTradeDate());//交易日
        entity.setSettDate(dto.getSettDate()); //交割日
        entity.setTradeCapi(dto.getPrinAmt()); //交易本金
        entity.setIntAmt(dto.getIntAmt()); //应收利息
        entity.setDealAmt(dto.getDealAmt());//交易金额
        entity.setCcy(dto.getCcy());//币种
//        entity.setVdate(DateUtil.parse(dto.getVdate()));//起息日
//        entity.setMdate(DateUtil.parse(dto.getMdate()));//到期日
//        entity.setTerm(dto.getTerm());//借贷期限
//        entity.setFeeRate(dto.getFeeRate());//借贷费率
//        entity.setFee(dto.getFee());//借贷费用

//        entity.setSecId("F01_"+dto.getSecId());//标的债券代码
//        entity.setPrinAmt(dto.getPrinAmt());//券面总额
//        entity.setStlFeeAmt(dto.getStlFeeAmt());//结算服务费
        //比较界面和接口字段差异，并手动给接口字段赋值
//        entity.setFinprodId("F01_"+ dto.getSecId());//金融产品代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setPs("T01");//交易方向

        entity.setCounterId("JYDS000000000424");//交易对手


        entity.setIsClean("Y");//有无清算指令
//        entity.setRegistAgency("01");//登记结算机构
//        entity.setIsStlFeeAmt("Y");//结算手续费是否随交易支付
//        entity.setMyAcctNo("201907300001:jixu0001");
        listValBotAstTrade.add(entity);
        TemplateOrderInfoReaderResult acctEntity= new TemplateOrderInfoReaderResult();
        Date date = new Date();
        acctEntity.setResovleDate(date);
        acctEntity.setFourLevelSubjectKg("Y");
        Map<String, Object> map = depositeAssetAstDealTemplateRecService.handleValBotAstTrade(listValBotAstTrade,acctEntity,params,null);
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal=(List<TrdProductDeal>)map.get("listValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);

        // 费用明细表
//        List<TrdTradeFeeDetail> listValTrdTradeFeeDetail=(List<TrdTradeFeeDetail>) map.get("listValTrdTradeFeeDetail");
//        result.setListValTradeFeeDetail(listValTrdTradeFeeDetail);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt=(List<TrdProductDealAdd>)map.get("listValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting=(List<BokDealAccounting>)map.get("listValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        //账务明细list
        List<BokDealAccountingDetail> listValDealAccountingDetail=(List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail =(List<TrdTradeFeeDetail>)map.get("listValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=(List<BokDetailId>)map.get("listValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        //结算指令
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(valOrdersExtList);

        TrdDepositValuationTradeResult rst = new TrdDepositValuationTradeResult(result);
        rst.setTrdDepositValuationTrade001Dto(dto);

        return rst;
    }

}
