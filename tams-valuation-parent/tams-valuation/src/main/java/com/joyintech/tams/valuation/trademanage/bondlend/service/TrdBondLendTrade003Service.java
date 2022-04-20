package com.joyintech.tams.valuation.trademanage.bondlend.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl.SecblTranDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTrade001Dto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TrdBondLendTrade003Service extends CommonExportService<TrdBondLendTrade001Dto> implements BaseProcessService<TrdBondLendTrade001Dto> {

    @Autowired
    private SecblTranDealTemplateRecService secblTranDealTemplateRecService;

    @Autowired
    private TrdBondLendTrade002Service trdBondLendTrade002Service;

    @Override
    public void doComplete(TrdBondLendTrade001Dto trdBondLendTrade001Dto, OperationType operationType) {
        TrdBondLendTradeResult trdBondLendTradeResult = generateEntity(trdBondLendTrade001Dto);
        String action = StringUtil.EMPTY;
         if (OperationType.NEW.equals(operationType)) {
             action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/" + action, trdBondLendTradeResult,
                Integer.class);

    }

    //封装实体类
    public TrdBondLendTradeResult generateEntity(TrdBondLendTrade001Dto dto){

        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValSeclendDeal> listValSeclendDeal=new ArrayList<>();
        ValSeclendDeal entity = new ValSeclendDeal();
        BeanUtil.copyPropertiesIgnoreNull(entity, dto);
        //金融产品类型
        entity.setPs(DictCodeList.交易方向_到期.getValue());
        entity.setFee(dto.getSettleAmt());
        entity.setPrinAmt(ArithmeticUtil.multiply(dto.getPrinAmt(), new BigDecimal(10000)));//券面总额
        entity.setFeeRate(ArithmeticUtil.divide(dto.getFeeRate(),new BigDecimal(100),4, RoundingMode.HALF_UP));
        entity.setMyAcctNo(dto.getAcctNo());
        entity.setFundSettleDate(dto.getTradeDate());//资金交收日
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setFinprodId(dto.getSecId());//金融产品代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setBasis(null);//计息基础  接口没用到
        entity.setCounterId(null);//交易对手
        entity.setTradeMarket("01");//交易场所  默认银行间
        entity.setmPrinAmt(new BigDecimal(0));//质押券面总额
        entity.setIsClean("N");//有无清算指令 默认：无
        entity.setRegistAgency(null);//登记结算机构
        entity.setIsStlFeeAmt("N");//结算手续费是否随交易支付 默认：否
        listValSeclendDeal.add(entity);

        Map<String, Object> map = secblTranDealTemplateRecService.handleValSeclendDeal(listValSeclendDeal,new Date(),params,null);
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal=(List<TrdProductDeal>)map.get("listValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);

        // 费用明细表
        List<TrdTradeFeeDetail> listValTrdTradeFeeDetail=(List<TrdTradeFeeDetail>) map.get("listValTrdTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTrdTradeFeeDetail);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt=(List<TrdProductDealAdd>)map.get("listValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting=(List<BokDealAccounting>)map.get("listValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        //账务明细list
        List<BokDealAccountingDetail> listValDealAccountingDetail=(List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=(List<BokDetailId>)map.get("listValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);

        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(valOrdersExtList);

        TrdBondLendTradeResult rst = new TrdBondLendTradeResult(result);
        rst.setTrdBondLendTrade001Dto(dto);

        return rst;

    }

}
