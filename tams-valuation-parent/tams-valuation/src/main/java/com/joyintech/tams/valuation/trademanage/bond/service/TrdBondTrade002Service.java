package com.joyintech.tams.valuation.trademanage.bond.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestAssetDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestAssetDeal;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeDto;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdBondTrade002Service extends CommonExportService<TrdBondTradeDto> implements BaseProcessService<TrdBondTradeDto> {

    @Autowired
    private SecVestAssetDealTemplateRecService secVestAssetDealTemplateRecService;

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public FinProduct selectInfoBySecId(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondTrade002/selectInfoBySecId", map,
                FinProduct.class);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondTrade002/queryTradeInfoByTradeId", tradeId,
                TrdBondTradeDto.class);
    }

    /**
     * 查询证券托管户：联想框文本框
     *
     * @param parameters 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> parameters) {
         return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondTrade002/getSecManageAcctId", parameters, SelectOptionDto.class);
    }

    public Page<SelectOptionDto> getChannelId(Map<String, Object> pageParam) {
        return restTemplate.postForPage(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondTrade002/getChannelId",
                pageParam, SelectOptionDto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondTrade002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdBondTradeDto trdBondTradeDto, OperationType operationType) {
        TrdBondTradeResult trdBondTradeResult = generateEntity(trdBondTradeDto);
        String action = StringUtil.EMPTY;
         if (OperationType.NEW.equals(operationType)) {
            //insert(secDealInfoProcessorResult);
             action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondTrade002/" + action, trdBondTradeResult,
                Integer.class);

    }

    //封装实体类
    public TrdBondTradeResult generateEntity(TrdBondTradeDto trdBondTradeDto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdBondTradeDto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValSecVestAssetDeal> listValSecVestAssetDeal = new ArrayList<ValSecVestAssetDeal>();
        ValSecVestAssetDeal valSecVestAssetDeal = new ValSecVestAssetDeal();
        BeanUtil.copyPropertiesIgnoreNull(valSecVestAssetDeal, trdBondTradeDto);
        valSecVestAssetDeal.setDealDate(trdBondTradeDto.getTradeDate());
        valSecVestAssetDeal.setInvestAim(trdBondTradeDto.getInvAim());//投资目的
        //比较界面和接口字段差异，并手动给接口字段赋值
        valSecVestAssetDeal.setAssetId(trdBondTradeDto.getSecId());//资产代码
        valSecVestAssetDeal.setDataActionSign("01");//数据动作标志
        valSecVestAssetDeal.setFpriceAmt(trdBondTradeDto.getTradeAmt());//全价
        valSecVestAssetDeal.setCpriceAmt(ArithmeticUtil.subtract(trdBondTradeDto.getTradeAmt(),trdBondTradeDto.getIntAmt()));//净价总额
        valSecVestAssetDeal.setTradeAmt(trdBondTradeDto.getSettleAmt());//交易金额

        valSecVestAssetDeal.setStlForm("03");//结算方式
        valSecVestAssetDeal.setParValue(new BigDecimal(0));//单位面值
        valSecVestAssetDeal.setFeeAmtUnpay(new BigDecimal(0));//未随交易支付费用
        valSecVestAssetDeal.setYield(new BigDecimal(0));//到期收益率
        valSecVestAssetDeal.setCounterId(null);//交易对手
        valSecVestAssetDeal.setCounterType(null);//交易对手类型
        valSecVestAssetDeal.setTrader(null);//交易员
        valSecVestAssetDeal.setUnitInt(new BigDecimal(0));//应收利息
        valSecVestAssetDeal.setIsStampDuty("Y");//印花税是否随交易支付
        valSecVestAssetDeal.setIsBrokerage("Y");//经手费是否随交易支付
        valSecVestAssetDeal.setIsCertfFee("Y");//证管费是否随交易支付
        valSecVestAssetDeal.setIsStlAmt("Y");//结算费是否随交易支付
        valSecVestAssetDeal.setIsCommission("N");//佣金是否随交易支付
        valSecVestAssetDeal.setIsDealFeeAmt("N");//交易手续费是否随交易支付
        valSecVestAssetDeal.setIsStlFeeAmt("N");//结算手续费是否随交易支付
        valSecVestAssetDeal.setIsChlFeeAmt("Y");//通道费是否随交易支付
        valSecVestAssetDeal.setRegistAgency(null);//登记结算机构
        valSecVestAssetDeal.setIsClean("Y");//有无清算指令
        valSecVestAssetDeal.setExeYield(new BigDecimal(0));//行权收益率
        valSecVestAssetDeal.setSharesTradeMarket("02");//流通市场
        //valSecVestAssetDeal.setIsOtherAmt("N");
        if(DictCodeList.交易方向_申购.getValue().equals(trdBondTradeDto.getPs())){
            valSecVestAssetDeal.setMyAcctNo(trdBondTradeDto.getMyAcctNo_sq());
        }else if(DictCodeList.交易方向_申购确认.getValue().equals(trdBondTradeDto.getPs())){
            valSecVestAssetDeal.setMyAcctNo(trdBondTradeDto.getMyAcctNo_qr());
        }else{
            valSecVestAssetDeal.setMyAcctNo(trdBondTradeDto.getMyAcctNo());
        }
//        valSecVestAssetDeal.setCounterAcctNo(valSecVestAssetDeal.getMyAcctNo());

        listValSecVestAssetDeal.add(valSecVestAssetDeal);

        //调用接口处理方法
        Map<String, Object> map = secVestAssetDealTemplateRecService.handleValSecVestAssetDeal(listValSecVestAssetDeal,new Date(),params,null);

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //金融产品实物交易
        List<TrdFinProductBarter> listTrdFinProductBarter = (List<TrdFinProductBarter>)map.get(
                "ValFinProductBarter");
        result.setListValFinProductBarter(listTrdFinProductBarter);
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

        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                "ValTradeFeeDetail");
        result.setListValTradeFeeDetail(listValTradeFeeDetail);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));

        TrdBondTradeResult entity = new TrdBondTradeResult(result);
        entity.setTrdBondTradeDto(trdBondTradeDto);
        return entity;
    }

}
