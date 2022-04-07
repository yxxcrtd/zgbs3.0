/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.repdeal.RepurchaseDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSec;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade003Dto;
import com.joyintech.tams.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class TrdRepoValuationTrade003Service extends BaseService<TrdRepoValuationTrade003Dto> implements BaseProcessService<TrdRepoValuationTrade003Dto> {

    @Autowired
    private RepurchaseDealTemplateRecService repurchaseDealTemplateRecService;

    @Autowired
    private TrdRepoValuationTrade002Service trdRepoValuationTrade002Service;

    /**
     * 根据交易编号查询详情
     */
    public TrdRepoValuationTrade003Dto queryTradeInfoByTradeId01(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdRepoValuationTrade002/queryTradeInfoByTradeId01",
                tradeId, TrdRepoValuationTrade003Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdRepoValuationTrade002/createPlanTradeId",
                null, String.class);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdRepoValuationTrade003Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdRepoValuationTradeResult trdRepoValuationTradeResult = new TrdRepoValuationTradeResult();
        trdRepoValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdRepoValuationTrade001/" + action, trdRepoValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdRepoValuationTradeResult generateEntity(TrdRepoValuationTrade003Dto dto3){

        TrdRepoValuationTrade001Dto dto = trdRepoValuationTrade002Service.queryTradeInfoByTradeId(dto3.getTradeId());

        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto3.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        List<ValRepDealSec> listValBotAstTrade=new ArrayList<>();
        ValRepDealSec entity = new ValRepDealSec();
//        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setpTradeId(dto.getTradeId()); //原交易编号
        entity.setfTradeId(dto.getOutTradeId()); //外部交易编号
        entity.setProdId(dto3.getPortfolioId()); //产品代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setTradeId(createPlanTradeId());  //交易编号
//        entity.setTradeId(dto.getTradeId());
//        entity.setTermKind("R007"); //期限品种
        //DictCodeList.回购期限品种_R007.getValue()
        entity.setTradeMarket(dto3.getTradeMarket()); //交易市场

        entity.setPs(dto3.getPs()); //交易类型
        entity.setTrader("yc"); //交易员
        entity.setTradeDate(dto3.getTradeDate());//交易日
        entity.setSettleDate(dto3.getSettleDate()); //交割日
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setFirstAmt(new BigDecimal(0)); //首次结算金额
        entity.setEndAmt(new BigDecimal(0)); //到期结算金额 卖出、到期时：到期结算金额字段为必填项
        entity.setIntAmt(dto3.getIntAmt());//利息金额
        entity.setCcy(dto3.getCcy());//币种
        entity.setCapiProfFeature("01"); //本金收益特征 01保本
        entity.setCounterId(dto.getCounterId());//交易对手
        entity.setCounterType("01"); //交易对手类型 01 内部
        entity.setRegistOrg("02"); //登记托管机构
        entity.setIsTradeFee("N");
        entity.setIsStampDuty("N");
        entity.setBrokerage(dto.getBrokerage()); //经手费
        entity.setIsBrokerage("Y");
        entity.setIsCertfFee("N"); //证管费是否随交易支付
        entity.setStlAmt(dto.getStlAmt()); //结算费
        entity.setIsStlAmt("Y"); //结算费是否随交易支付
        entity.setCommission(dto.getCommission()); //佣金
        entity.setIsCommission("N"); //佣金是否随交易支付
        entity.setDealFeeAmt(dto.getDealFeeAmt()); //交易手续费
        entity.setIsDealFeeAmt("Y");//交易手续费是否随交易支付
        entity.setStlFeeAmt(dto.getSettFeeAmt()); //结算手续费
        entity.setIsStlFeeAmt("N"); //结算手续费是否随交易支付

        entity.setIsChlFeeAmt("N");//通道费是否随交易支付
        entity.setIsClean("N");//有无清算指令
        entity.setPaydate(new Date()); //交收日
        entity.setMyAcctNo(dto3.getTrdAcctId()); //付款账户
        entity.setCounterAcctNo(null);//收款账户
        entity.setFundSettleDate(null);//资金交收日
        entity.setBusiType("04"); //业务类型

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

        TrdRepoValuationTradeResult rst = new TrdRepoValuationTradeResult(result);
        rst.setTrdRepoValuationTrade001Dto(dto);
        return rst;
    }

}
