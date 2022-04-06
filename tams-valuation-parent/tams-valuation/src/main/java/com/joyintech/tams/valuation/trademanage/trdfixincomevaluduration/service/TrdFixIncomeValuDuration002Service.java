/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.PlannedAssetDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.mngasset.PlannedPeriodTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngPeriodTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.component.common.dto.FinCashPlanDto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluduration.dto.TrdFixIncomeValuDurationResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class TrdFixIncomeValuDuration002Service extends BaseService<TrdFixIncomeValuDuration001Dto> implements BaseProcessService<TrdFixIncomeValuDuration001Dto> {

    @Autowired
    private PlannedPeriodTradeTemplateRecService plannedPeriodTradeTemplateRecService;

    /**
     * 根据交易编号查询详情
     */
    public TrdFixIncomeValuDuration001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuDuration001/queryTradeInfoByTradeId",
                tradeId, TrdFixIncomeValuDuration001Dto.class);
    }


    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuDuration001/createPlanTradeId",
                null, String.class);

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdFixIncomeValuDuration001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdFixIncomeValuDurationResult trdFixIncomeValuDurationResult = new TrdFixIncomeValuDurationResult();
        trdFixIncomeValuDurationResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFixIncomeValuDuration001/" + action, trdFixIncomeValuDurationResult, Integer.class);
    }

    //封装实体类
    public TrdFixIncomeValuDurationResult generateEntity(TrdFixIncomeValuDuration001Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValMngPeriodTrade> listValMngPeriodTrade = new ArrayList<>();
        ValMngPeriodTrade entity = new ValMngPeriodTrade();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
//        entity.setFundSettleDate(DateUtil.parse(dto.getFundSettleDate()));
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setProdId(dto.getPortfolioId());
        entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setAssetId(dto.getFinprodMarketId());  //资产代码
        entity.setAssetName(dto.getFinprodName()); //资产名称
        entity.setDataActionSign("01");//数据动作标志
        //交易方向转换
        if(DictCodeList.交易方向_收息到账.getValue().equals(dto.getPs())){
            dto.setPs(DictCodeList.交易方向_收息.getValue());
        }else{
            dto.setPs(DictCodeList.交易方向_还本.getValue());
        }
        entity.setDealType(dto.getPs());
        if(DictCodeList.交易方向_还本.getValue().equals(dto.getPs()) && dto.getActInt().compareTo(new BigDecimal(0))==1 ){
            entity.setIsInt("Y");
        }else{
            entity.setIsInt("N");//是否还本带息 交易方向为还本，应收利息是否为0，不为0，IsInt为Y
        }
        //查询债券的划
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("finprodId",entity.getAssetId());
        List<FinCashPlanDto> list = getFinCashPlanByProductId(hashMap);
        if(!StringUtil.isEmptyList(list)){
            entity.setVdate(list.get(0).getVdate());//计息开始日 根据标的信息查询
            entity.setMdate(list.get(0).getMdate());//计息结束日 根据标的信息查询
        }else{
            entity.setVdate(DateUtil.parse(DateUtil.getSysDate()));//计息开始日 根据标的信息查询
            entity.setMdate(DateUtil.parse(DateUtil.getSysDate()));//计息结束日 根据标的信息查询
        }
//        entity.setVdate(new Date()); //计息开始日
//        entity.setMdate(new Date()); //计息结束日
//        entity.setPlanPayDate(dto.getTradeDate()); //计划
        entity.setPayDate(dto.getTradeDate()); //实际支付日  (取交易日)
        entity.setActInt(dto.getActInt()); //实收利息
        entity.setActCapi(dto.getActCapi()); //实收本金
        entity.setDealAmt(dto.getActInt().add(dto.getActCapi())); //交易金额=实收本金+实收利息
        entity.setInvAim(dto.getInvAim()); //投资目的=会计分类
        entity.setParValue(new BigDecimal(0)); //单位面值
        entity.setSettleDate(dto.getSettleDate()); //交割日 =settleDate
        entity.setIsClean2(dto.getIsClean2());//是否结清
        entity.setCcy(dto.getCcy());//币种
        entity.setTradeId(null);  //交易编号
        entity.setIsClean("Y");//有无清算指令
        entity.setMyAcctNo(dto.getTrdAcctId()); //资金账户

        listValMngPeriodTrade.add(entity);
        Map<String, Object> map = plannedPeriodTradeTemplateRecService.handleListValMngPeriodTrade(listValMngPeriodTrade,new Date(),params,null);
        // 定义返回变量
        PlannedAssetDealCommonResult result = new PlannedAssetDealCommonResult();
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");

        List<TrdTradeFeeDetail> valTradeFeeDetailList = (List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail");
        List<StlOrders> lsitValOrders = (List<StlOrders>)map.get("ValOrders");
        List<StlOrdersExt> lsitValOrdersExt = (List<StlOrdersExt>)map.get("ValOrdersExt");

        result.setListValDealAccounting((List<BokDealAccounting>)map.get("ValDealAccounting"));
        result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail"));

        result.setListValBokDetailId(listValBokDetailId);

        result.setListValFinProductDeal(listValFinProductDeal);
        result.setListValFinProductDealExt(listValFinProductDealExt);
        result.setValTradeFeeDetailList(valTradeFeeDetailList);
        result.setValOrdersList(lsitValOrders);
        result.setValOrdersExtList(lsitValOrdersExt);
        TrdFixIncomeValuDurationResult rst = new TrdFixIncomeValuDurationResult(result);
        rst.setTrdFixIncomeValuDuration001Dto(dto);
        return rst;
    }


    /**
     * 查询计划类资产付息计划
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlanByProductId(Map<String,Object> param){
        return restTemplate.postForList(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFixIncomeValuDuration001/getFinCashPlanByProductId",param,FinCashPlanDto.class);
    }
}
