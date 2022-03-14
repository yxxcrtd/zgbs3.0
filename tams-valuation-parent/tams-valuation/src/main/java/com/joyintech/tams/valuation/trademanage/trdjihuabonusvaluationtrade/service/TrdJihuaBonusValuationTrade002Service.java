/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundBonusReinvestmentDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundCashDividendDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundShareConvertDealRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBonusResTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValCashBonusTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundShareConvert;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusTradeResult;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 基金交易新建
 *
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdJihuaBonusValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdJihuaBonusValuationTrade002Service extends CommonExportService<TrdJihuaBonusValuationTradeDto>
        implements BaseProcessService<TrdJihuaBonusValuationTradeDto> {

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;


    @Autowired
    private FundBonusReinvestmentDealRecService fundBonusReinvestmentDealRecService;

    @Autowired
    private FundShareConvertDealRecService fundShareConvertDealRecService;

    @Autowired
    private FundCashDividendDealRecService fundCashDividendDealRecService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdJihuaBonusValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdJihuaBonusTradeResult trdJihuaBonusTradeResult = new TrdJihuaBonusTradeResult();
        trdJihuaBonusTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdJihuaBonusValuationTrade/" + action, trdJihuaBonusTradeResult, Integer.class);
    }

    public List<SelectOptionDto> getTrdAcctId(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdJihuaBonusValuationTrade/getTrdAcctId", param,
                SelectOptionDto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createJihuaBonusTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdJihuaBonusValuationTrade/createJihuaBonusTradeId",
                null, String.class);

    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdJihuaBonusValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdJihuaBonusValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdJihuaBonusValuationTradeDto.class);
    }

    //封装实体类
    public TrdJihuaBonusTradeResult generateEntity(TrdJihuaBonusValuationTradeDto trdJihuaBonusValuationTradeDto){
        TrdJihuaBonusTradeResult trdJihuaBonusTradeResult =  new TrdJihuaBonusTradeResult();
        //买入、卖出==>对应接口【基金&计划类现金分红交易】
        if (StringUtil.isNotEmpty(trdJihuaBonusValuationTradeDto.getPs())) {
            if (trdJihuaBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_现金分红.getValue()) ) {
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdJihuaBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValCashBonusTrade> listValCashBonusTrade = new ArrayList<ValCashBonusTrade>();
                ValCashBonusTrade valCashBonusTrade = new ValCashBonusTrade();
                BeanUtil.copyPropertiesIgnoreNull(valCashBonusTrade, trdJihuaBonusValuationTradeDto);
                //比较界面和接口字段差异，并手动给接口字段赋值
                valCashBonusTrade.setAssetId("F06_" + trdJihuaBonusValuationTradeDto.getAssetId());//资产代码  F06基金
                valCashBonusTrade.setDataActionSign("01");
                valCashBonusTrade.setIsChlFeeAmt("Y");
                valCashBonusTrade.setIsClean("N");

                listValCashBonusTrade.add(valCashBonusTrade);

                Map<String, Object> map = fundCashDividendDealRecService.handleValCashBonusTrade(listValCashBonusTrade, new Date(), params, null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    //结算指令
                    List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                            "listValOrders");
                    List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                            "listValOrdersExt");
                    result.setListValOrders(listValOrders);
                    result.setListValOrdersExt(listValOrdersExt);

                    result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));

                    result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail"));

                    result.setListValBokDetailId((List<BokDetailId>)map.get(
                            "ValBokDetailId"));

                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdJihuaBonusTradeResult rst = new TrdJihuaBonusTradeResult(result);
                return rst;
            } else if (trdJihuaBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_红利再投.getValue()) ) {
                //申购、认购 ==>对应接口【基金&计划类红利再投交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdJihuaBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValBonusResTrade> listValBonusResTrade = new ArrayList<ValBonusResTrade>();
                ValBonusResTrade valBonusResTrade = new ValBonusResTrade();
                BeanUtil.copyPropertiesIgnoreNull(valBonusResTrade, trdJihuaBonusValuationTradeDto);
                //比较界面和接口字段差异，并手动给接口字段赋值
                valBonusResTrade.setAssetId("F06_" + trdJihuaBonusValuationTradeDto.getAssetId());//资产代码  F06基金
                valBonusResTrade.setDataActionSign("01");
                valBonusResTrade.setIsClean("N");

                listValBonusResTrade.add(valBonusResTrade);

                Map<String, Object> map = fundBonusReinvestmentDealRecService.handleValBonusResTrade(listValBonusResTrade, new Date(), params, null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    //结算指令
                    List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                            "listValOrders");
                    List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                            "listValOrdersExt");
                    result.setListValOrders(listValOrders);
                    result.setListValOrdersExt(listValOrdersExt);

                    result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));

                    result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail"));

                    result.setListValBokDetailId((List<BokDetailId>)map.get(
                            "ValBokDetailId"));

                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdJihuaBonusTradeResult rst = new TrdJihuaBonusTradeResult(result);
                return rst;
            } else if (trdJihuaBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_份额强增.getValue())
                    || trdJihuaBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_份额强减.getValue()) ) {
                //申购、认购 ==>对应接口【基金份额折算交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdJihuaBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValFundShareConvert> listValFundTrade = new ArrayList<ValFundShareConvert>();
                ValFundShareConvert valFundShareConvert = new ValFundShareConvert();
                BeanUtil.copyPropertiesIgnoreNull(valFundShareConvert, trdJihuaBonusValuationTradeDto);
                //比较界面和接口字段差异，并手动给接口字段赋值
                valFundShareConvert.setAssetId("F06_" + trdJihuaBonusValuationTradeDto.getAssetId());//资产代码  F06基金
                valFundShareConvert.setDataActionSign("01");
                valFundShareConvert.setIsClean("N");

                listValFundTrade.add(valFundShareConvert);

                Map<String, Object> map = fundShareConvertDealRecService.handleValFundTrade(listValFundTrade, new Date(), params, null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
                    /**modify by wujie on 20190118 start
                     result.setListInterfProductDealBookCommonDto(
                     (List<InterfProductDealBookCommonDto>)map.get(
                     "listInterfProductDealBookCommonDto"));
                     **/
                    List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                            "ValDealAccounting");
                    result.setListValDealAccounting(listValDealAccounting);

                    List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail");
                    result.setListValDealAccountingDetail(listValDealAccountingDetail);

                    List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                            "ValBokDetailId");
                    result.setListValBokDetailId(listValBokDetailId);
                    /**modify by wujie on 20190118 end**/

                    //结算指令
                    List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                            "listValOrders");
                    List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                            "listValOrdersExt");
                    result.setListValOrders(listValOrders);
                    result.setListValOrdersExt(listValOrdersExt);
                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdJihuaBonusTradeResult rst = new TrdJihuaBonusTradeResult(result);
                return rst;
            }else{
                //返回初始化
                return trdJihuaBonusTradeResult;
            }
        }else{
            //返回初始化
            return trdJihuaBonusTradeResult;
        }

    }


}
