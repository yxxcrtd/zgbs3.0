/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
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
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfundbonusvaluationtrade.dto.TrdFundBonusValuationTradeDto;
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
 * @see TrdFundBonusValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFundBonusValuationTrade002Service extends CommonExportService<TrdFundBonusValuationTradeDto>
        implements BaseProcessService<TrdFundBonusValuationTradeDto> {

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private FundCashDividendDealRecService fundCashDividendDealRecService;

    @Autowired
    private FundBonusReinvestmentDealRecService fundBonusReinvestmentDealRecService;

    @Autowired
    private FundShareConvertDealRecService fundShareConvertDealRecService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdFundBonusValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdFundBonusTradeResult trdFundBonusTradeResult = new TrdFundBonusTradeResult();
        trdFundBonusTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFundBonusValuationTrade/" + action, trdFundBonusTradeResult, Integer.class);
    }


    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createFundBonusTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundBonusValuationTrade/createFundBonusTradeId",
                null, String.class);

    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdFundBonusValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundBonusValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdFundBonusValuationTradeDto.class);
    }

    //封装实体类
    public TrdFundBonusTradeResult generateEntity(TrdFundBonusValuationTradeDto trdFundBonusValuationTradeDto){
        TrdFundBonusTradeResult trdFundBonusTradeResult =  new TrdFundBonusTradeResult();
        //买入、卖出==>对应接口【基金&计划类现金分红交易】
        if (StringUtil.isNotEmpty(trdFundBonusValuationTradeDto.getPs())) {
            if (trdFundBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_现金分红.getValue()) ) {
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValCashBonusTrade> listValCashBonusTrade = new ArrayList<ValCashBonusTrade>();
                ValCashBonusTrade valCashBonusTrade = new ValCashBonusTrade();
                valCashBonusTrade = dealBean(valCashBonusTrade,trdFundBonusValuationTradeDto);

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
                TrdFundBonusTradeResult rst = new TrdFundBonusTradeResult(result);
                return rst;
            } else if (trdFundBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_红利再投.getValue()) ) {
                //申购、认购 ==>对应接口【基金&计划类红利再投交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValBonusResTrade> listValBonusResTrade = new ArrayList<ValBonusResTrade>();
                ValBonusResTrade valBonusResTrade = new ValBonusResTrade();
                valBonusResTrade = dealBean(valBonusResTrade,trdFundBonusValuationTradeDto);

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
                TrdFundBonusTradeResult rst = new TrdFundBonusTradeResult(result);
                return rst;
            } else if (trdFundBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_份额强增.getValue())
                    || trdFundBonusValuationTradeDto.getPs().equals(DictCodeList.交易方向_份额强减.getValue()) ) {
                //申购、认购 ==>对应接口【基金份额折算交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundBonusValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValFundShareConvert> listValFundTrade = new ArrayList<ValFundShareConvert>();
                ValFundShareConvert valFundShareConvert = new ValFundShareConvert();
                valFundShareConvert = dealBean(valFundShareConvert,trdFundBonusValuationTradeDto);

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
                TrdFundBonusTradeResult rst = new TrdFundBonusTradeResult(result);
                return rst;
            }else{
                //返回初始化
                return trdFundBonusTradeResult;
            }
        }else{
            //返回初始化
            return trdFundBonusTradeResult;
        }
    }


    /**
     * @auther: 属性转换处理-现金分红
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValCashBonusTrade dealBean(ValCashBonusTrade valCashBonusTrade,TrdFundBonusValuationTradeDto dto){
        //相同字段名： 成交编号、交易方向、交易场所、币种
        BeanUtil.copyPropertiesIgnoreNull(valCashBonusTrade, dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        valCashBonusTrade.setAssetId("F06_" + dto.getAssetId());//资产代码
        valCashBonusTrade.setAssetName(dto.getAssetAbbr());//资产名称
        valCashBonusTrade.setBonusDate(dto.getTradeDate());//分红日
        valCashBonusTrade.setActBounsAmt(dto.getBonusAmt());//实际分红金额
        valCashBonusTrade.setDataActionSign("01");//数据动作标志
        valCashBonusTrade.setIsClean("N");//有无清算指令
        //valCashBonusTrade.setMdate();//分红截止日

        return valCashBonusTrade;
    }

    /**
     * @auther: 属性转换处理-红利再投
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValBonusResTrade dealBean(ValBonusResTrade valBonusResTrade,TrdFundBonusValuationTradeDto dto){
        //相同字段名： 成交编号、交易方向、交易场所、币种
        BeanUtil.copyPropertiesIgnoreNull(valBonusResTrade, dto);
        valBonusResTrade.setAssetId("F06_" + dto.getAssetId());//资产代码
        valBonusResTrade.setAssetName(dto.getAssetAbbr());//资产名称
        valBonusResTrade.setBonusAmt(dto.getBonusAmt_hlzt());//红利再投金额
        valBonusResTrade.setBonusDate(dto.getTradeDate_hlzt());//红利再投日
        valBonusResTrade.setBonusNet(dto.getUnitBonusUnitValue());//红利再投净值
        valBonusResTrade.setBonusShare(dto.getDealNum_hlzt());//红利再投份额

        valBonusResTrade.setDataActionSign("01");
        valBonusResTrade.setIsClean("N");

        return valBonusResTrade;
    }

    /**
     * @auther: 属性转换处理-份额强增、份额强减
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValFundShareConvert dealBean(ValFundShareConvert valFundShareConvert,TrdFundBonusValuationTradeDto dto){
        //相同字段名： 成交编号、交易方向、交易场所、币种
        BeanUtil.copyPropertiesIgnoreNull(valFundShareConvert, dto);
        valFundShareConvert.setAssetId("F06_" + dto.getAssetId());//资产代码
        valFundShareConvert.setAssetName(dto.getAssetAbbr());//资产名称
        valFundShareConvert.setTradeDate(dto.getTradeDate_share());//交易日期
        valFundShareConvert.setCfmShare(dto.getDealNum_share());//交易份额
        valFundShareConvert.setBusiType("06");//业务类型
        valFundShareConvert.setDataActionSign("01");
        valFundShareConvert.setIsClean("N");//有无清算指令

        return valFundShareConvert;
    }




}
