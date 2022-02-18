package com.joyintech.tams.valuation.trademanage.bondlend.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl.SecblTranDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTrade002Dto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTrade003Dto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdBondLendTrade002Service extends CommonExportService<TrdBondLendTrade002Dto> implements BaseProcessService<TrdBondLendTrade002Dto> {

    @Autowired
    private SecblTranDealTemplateRecService secblTranDealTemplateRecService;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondLendTrade002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryTradeInfoByTradeId", tradeId,
                TrdBondLendTrade002Dto.class);
    }

    /**
     * 通过交易编号查询交易信息（到期）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondLendTrade003Dto queryInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryInfoByTradeId", tradeId,
                TrdBondLendTrade003Dto.class);
    }

    /**
     * 资金账户下拉框
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTrdAcctId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/getTrdAcctId" , map,
                SelectOptionDto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondLendTrade002/querySequences", null,Integer.class);
    }

    @Override
    public void doComplete(TrdBondLendTrade002Dto TrdBondLendTrade002Dto, OperationType operationType) {
        TrdBondLendTradeResult trdBondLendTradeResult = generateEntity(TrdBondLendTrade002Dto);
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
    public TrdBondLendTradeResult generateEntity(TrdBondLendTrade002Dto dto){
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
        entity.setTradeId(dto.getTradeId());
        entity.setProdId(dto.getProdId());
        //金融产品类型
        if("01".equals(dto.getPs())){
            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
        }else if("02".equals(dto.getPs())){
            entity.setFinprodType(DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
        }else if("03".equals(dto.getPs())){
            entity.setPs(DictCodeList.交易方向_到期.getValue());
        }
        entity.setTradeDate(dto.getTradeDate());//交易日
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setTerm(dto.getTerm());//借贷期限
        entity.setFeeRate(dto.getFeeRate());//借贷费率
        entity.setFee(dto.getFee());//借贷费用
        entity.setCcy(dto.getCcy());//币种
        entity.setSecId("F01_"+dto.getSecId());//标的债券代码
        entity.setPrinAmt(ArithmeticUtil.multiply(dto.getPrinAmt(), new BigDecimal(10000)));//券面总额
        entity.setStlFeeAmt(dto.getStlFeeAmt());//结算服务费
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setFinprodId("F01_"+ dto.getSecId());//金融产品代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setPs("T01");//交易方向
        entity.setBasis("A/365");//计息基础
        entity.setCounterId("JYDS000000000424");//交易对手
        entity.setTradeMarket("01");//交易场所
        entity.setmPrinAmt(dto.getPrinAmt());//质押券面总额
        entity.setIsClean("Y");//有无清算指令
        entity.setRegistAgency("01");//登记结算机构
        entity.setIsStlFeeAmt("Y");//结算手续费是否随交易支付
        entity.setMyAcctNo("201907300001:jixu0001");
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
        rst.setTrdBondLendTrade002Dto(dto);

        return rst;

    }

}
