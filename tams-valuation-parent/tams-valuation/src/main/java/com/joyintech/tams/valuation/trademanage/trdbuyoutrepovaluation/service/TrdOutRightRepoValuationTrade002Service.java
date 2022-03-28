/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.repdeal.RepurchaseDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSec;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrdOutRightRepoValuationTrade002Service extends BaseService<TrdOutRightRepoValuationTrade001Dto> implements BaseProcessService<TrdOutRightRepoValuationTrade001Dto> {


    @Autowired
    private RepurchaseDealTemplateRecService repurchaseDealTemplateRecService;

    /**
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/findPortfolioId",
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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/findFinprodId",
                params, SelectOptionDto.class);
    }


    /**
     * 根据交易编号查询详情
     */
    public TrdOutRightRepoValuationTrade001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/queryTradeInfoByTradeId",
                tradeId, TrdOutRightRepoValuationTrade001Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOutRightRepoValuationTrade001/createPlanTradeId",
                null, String.class);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdOutRightRepoValuationTrade001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdOutRightRepoValuationTradeResult trdOutRightRepoValuationTradeResult = new TrdOutRightRepoValuationTradeResult();
        trdOutRightRepoValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOutRightRepoValuationTrade001/" + action, trdOutRightRepoValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdOutRightRepoValuationTradeResult generateEntity(TrdOutRightRepoValuationTrade001Dto dto){
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValRepDealSec> listValBotAstTrade=new ArrayList<>();
        ValRepDealSec entity = new ValRepDealSec();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值

        entity.setProdId(dto.getPortfolioId()); //产品代码
        entity.setfTradeId(dto.getOutTradeId()); //外部交易编号
        entity.setDataActionSign("02");//数据动作标志
        entity.setTradeId(null);  //交易编号
//        entity.setTermKind("R007"); //期限品种
        //DictCodeList.回购期限品种_R007.getValue()
        entity.setTradeMarket("01"); //交易市场 银行间
        entity.setPs(dto.getPs()); //交易类型
//        entity.setBusiType(dto.getPs()); //交易类型
        entity.setTrader("yc"); //交易员
        entity.setTradeDate(dto.getTradeDate());//交易日
        entity.setSettleDate(dto.getTradeDate()); //交割日
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setFirstAmt(dto.getFprice()); //首次结算金额
        entity.setEndAmt(dto.getMfprice()); //到期结算金额 卖出、到期时：到期结算金额字段为必填项
        entity.setIntAmt(dto.getIntAmt());//利息金额
        entity.setCcy(dto.getCcy());//币种
        entity.setCapiProfFeature("01"); //本金收益特征 01保本
        entity.setCounterId("JYDS000000000424");//交易对手
        entity.setCounterType("01"); //交易对手类型 01 内部
        entity.setRegistOrg("02"); //登记托管机构
        entity.setIsTradeFee("N");
        entity.setIsStampDuty("N");
        entity.setIsBrokerage("N");
        entity.setIsCertfFee("N");
        entity.setIsStlAmt("N");
        entity.setIsCommission("N");
        entity.setDealFeeAmt(dto.getDealFeeAmt()); //交易手续费
        entity.setIsDealFeeAmt("N");
        entity.setStlFeeAmt(dto.getSettFeeAmt()); //结算手续费
        entity.setIsStlFeeAmt("N");
        entity.setIsChlFeeAmt("N");//通道费是否随交易支付
        entity.setpTradeId(dto.getFinprodMarketId()); //原交易编号
        entity.setIsClean("N");//有无清算指令
        entity.setPaydate(new Date()); //交收日
        entity.setMyAcctNo(dto.getTrdAcctId()); //付款账户
        entity.setCounterAcctNo(null);//收款账户
        entity.setFundSettleDate(null);//资金交收日
        entity.setBusiType("05"); //业务类型

        listValBotAstTrade.add(entity);

        Map<String, Object> map = repurchaseDealTemplateRecService.handleValBotAstTrade(listValBotAstTrade,new Date(),params,null);

        // 定义返回变量
        RepurchaseDealInfoValCommonResult result = new RepurchaseDealInfoValCommonResult();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrders(listValOrders);
        result.setListValOrdersExt(listValOrdersExt);

        TrdOutRightRepoValuationTradeResult rst = new TrdOutRightRepoValuationTradeResult(result);
        rst.setTrdOutRightRepoValuationTrade001Dto(dto);

        return rst;
    }
}
