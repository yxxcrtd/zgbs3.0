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
import com.joyintech.tams.constant.DictCodeListExt1;
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
    public TrdFixIncomeValuDuration001Dto queryTradeInfoByTradeId(Map<String,Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuDuration001/queryTradeInfoByTradeId",
                param, TrdFixIncomeValuDuration001Dto.class);
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
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValMngPeriodTrade> listValMngPeriodTrade = new ArrayList<>();
        ValMngPeriodTrade entity = new ValMngPeriodTrade();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setDataActionSign("01");//数据动作标志
        entity.setMdate(dto.getTradeDate()); //实际支付日  (取交易日)
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
        //查询债券的付息计划
        Map<String,Object> hashMap = new HashMap<>();
        hashMap.put("finprodId",entity.getAssetId());
        hashMap.put("mdate",DateUtil.format(entity.getMdate()));
        List<FinCashPlanDto> list = getFinCashPlanByProductId(hashMap);
        if(!StringUtil.isEmptyList(list)){
            entity.setVdate(list.get(0).getVdate());//计息开始日 根据标的信息查询
        }else{
            entity.setVdate(DateUtil.parse(DateUtil.getSysDate()));//计息开始日 根据标的信息查询
        }

        entity.setDealAmt(dto.getActInt().add(dto.getActCapi())); //交易金额=实收本金+实收利息
        entity.setPayDate(dto.getSettleDate());
        entity.setParValue(new BigDecimal(100)); //单位面值
        entity.setIsClean("N");//有无清算指令

        //设置数据来源：画面录入
        entity.setDataSource(DictCodeListExt1.生成方式_画面录入.getValue());

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
