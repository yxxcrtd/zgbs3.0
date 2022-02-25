package com.joyintech.tams.valuation.trademanage.bondlend.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl.SecblTranDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendDeal;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTrade001Dto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeDto;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdBondLendTrade003Service extends CommonExportService<TrdBondLendTrade001Dto> implements BaseProcessService<TrdBondLendTrade001Dto> {

    @Autowired
    private SecblTranDealTemplateRecService secblTranDealTemplateRecService;

    @Autowired
    private TrdBondLendTrade002Service trdBondLendTrade002Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondLendTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryTradeInfoByTradeId", tradeId,
                TrdBondLendTradeDto.class);
    }

    /**
     * 通过交易编号查询交易信息（到期）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondLendTradeDto queryInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryInfoByTradeId", tradeId,
                TrdBondLendTradeDto.class);
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
    public TrdBondLendTradeResult generateEntity(TrdBondLendTrade001Dto trdBondLendTrade001Dto){
        TrdBondLendTradeDto dto = trdBondLendTrade002Service.queryTradeInfoByTradeId(trdBondLendTrade001Dto.getTradeId());

        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdBondLendTrade001Dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValSeclendDeal> listValSeclendDeal=new ArrayList<>();
        ValSeclendDeal entity = new ValSeclendDeal();
        entity.setTradeId(dto.getTradeId());
        entity.setProdId(trdBondLendTrade001Dto.getProdId());
        //金融产品类型
        entity.setPs(DictCodeList.交易方向_到期.getValue());
        entity.setTradeDate(trdBondLendTrade001Dto.getTradeDate());//交易日
        entity.setTerm(trdBondLendTrade001Dto.getTerm());//借贷期限
        entity.setFeeRate(trdBondLendTrade001Dto.getFeeRate());//借贷费率
        entity.setFee(trdBondLendTrade001Dto.getFee());//借贷费用
        entity.setCcy(trdBondLendTrade001Dto.getCcy());//币种
        entity.setSecId("F01_"+trdBondLendTrade001Dto.getSecId());//标的债券代码
        entity.setPrinAmt(ArithmeticUtil.multiply(trdBondLendTrade001Dto.getPrinAmt(), new BigDecimal(10000)));//券面总额
        entity.setMyAcctNo(trdBondLendTrade001Dto.getAcctNo());
        //日期。。。
        entity.setVdate(dto.getVdate());//起息日
        entity.setMdate(dto.getMdate());//到期日
        entity.setStlFeeAmt(dto.getStlFeeAmt());//结算服务费
        entity.setFinprodType(dto.getFinprodType());
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setFinprodId("F01_"+ dto.getSecId());//金融产品代码
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
        rst.setTrdBondLendTradeDto(dto);

        return rst;

    }

}
