/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.*;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;

/**
 * 基金交易新建
 *
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdFundValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFundValuationTrade002Service extends CommonExportService<TrdFundValuationTradeDto>
        implements BaseProcessService<TrdFundValuationTradeDto> {

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private FundBuyAndSellDealRecService fundBuyAndSellDealRecService;
    @Autowired
    private FundSubscribeApplyDealRecService fundSubscribeApplyDealRecService;
    @Autowired
    private FundSubscribeConfirmDealRecService fundSubscribeConfirmDealRecService;
    @Autowired
    private FundMoneyFundRedeemConfirmDealRecService fundMoneyFundRedeemConfirmDealRecService;
    @Autowired
    private FundRedeemArrivalAccountDealRecService fundRedeemArrivalAccountDealRecService;
    @Autowired
    private InterfCommonService interfCommonService;
    @Autowired
    private FundNMoneyFundRedeemConfirmDealRecService fundNMoneyFundRedeemConfirmDealRecService;

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createFundTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/createFundTradeId",
                null, String.class);
    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdFundValuationTradeDto queryTradeInfoByTradeId(Map<String,Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/queryTradeInfoByTradeId",
                param, TrdFundValuationTradeDto.class);
    }

    /**
     * 赎回到账详情查询
     */
    public TrdFundValuationTradeDto queryRedInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/queryRedInfoByTradeId",
                tradeId, TrdFundValuationTradeDto.class);
    }

    /**
     * 赎回到账双击明细页面
     */
    public TrdFundValuationTradeDto queryRedInfoByOutTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/queryRedInfoByOutTradeId",
                tradeId, TrdFundValuationTradeDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdFundValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdFundTradeResult trdFundTradeResult = new TrdFundTradeResult();
        trdFundTradeResult = generateEntity(entity,"A");
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFundValuationTrade/" + action, trdFundTradeResult, Integer.class);
    }

    //封装实体类(type:A代表基金，B代表计划类净值型资产)
    public TrdFundTradeResult generateEntity(TrdFundValuationTradeDto trdFundValuationTradeDto,String type){
        TrdFundTradeResult trdFundTradeResult = new TrdFundTradeResult();
        if (StringUtil.isNotEmpty(trdFundValuationTradeDto.getPs())) {
            if (trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_买入.getValue())
                    || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                //买入、卖出 ==>对应接口【基金&计划类买入卖出交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValFundTrade> listValFundTrade = new ArrayList<ValFundTrade>();
                ValFundTrade valFundTrade = new ValFundTrade();
                //实体类字段映射处理
                valFundTrade = dealBean(valFundTrade,trdFundValuationTradeDto,type);
                listValFundTrade.add(valFundTrade);
                Map<String, Object>  map = fundBuyAndSellDealRecService.handleValFundTrade(listValFundTrade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
                    List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                            "ValDealAccounting");
                    result.setListValDealAccounting(listValDealAccounting);

                    List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail");
                    result.setListValDealAccountingDetail(listValDealAccountingDetail);

                    List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                            "ValBokDetailId");
                    result.setListValBokDetailId(listValBokDetailId);

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

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
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                rst.setTrdFundValuationTradeDto(trdFundValuationTradeDto);
                return rst;
            } else if (trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购.getValue())
                || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购.getValue())) {
                //申购、认购 ==>对应接口【基金&计划类认申购申请交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValApplyPurTade> listValApplyPurTade = new ArrayList<ValApplyPurTade>();
                ValApplyPurTade valApplyPurTade = new ValApplyPurTade();
                //实体类字段映射处理
                valApplyPurTade = dealBean(valApplyPurTade,trdFundValuationTradeDto,type);

                listValApplyPurTade.add(valApplyPurTade);
                Map<String, Object>  map = fundSubscribeApplyDealRecService.handleValApplyPurTade(listValApplyPurTade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
                    List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                            "ValDealAccounting");
                    result.setListValDealAccounting(listValDealAccounting);

                    List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail");
                    result.setListValDealAccountingDetail(listValDealAccountingDetail);

                    List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                            "ValBokDetailId");
                    result.setListValBokDetailId(listValBokDetailId);

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

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
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                rst.setTrdFundValuationTradeDto(trdFundValuationTradeDto);
                return rst;
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购确认.getValue())
                    || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购确认.getValue()) ) {
                //申购确认、认购确认 ==>对应接口【基金&计划类认申购确认交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValAffirmPurTrade> listValAffirmPurTrade = new ArrayList<ValAffirmPurTrade>();
                ValAffirmPurTrade valAffirmPurTrade = new ValAffirmPurTrade();
                //实体类字段映射处理
                valAffirmPurTrade = dealBean(valAffirmPurTrade,trdFundValuationTradeDto,type);

                listValAffirmPurTrade.add(valAffirmPurTrade);
                Map<String, Object>  map = fundSubscribeConfirmDealRecService.handleValApplyPurTade(listValAffirmPurTrade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));

                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

                    result.setListValOrders( (List<StlOrders>)map.get(
                            "listValOrders"));

                    result.setListValOrdersExt((List<StlOrdersExt>)map.get(
                            "listValOrdersExt"));

                    result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));

                    result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail"));

                    result.setListValBokDetailId((List<BokDetailId>)map.get(
                            "ValBokDetailId"));

                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                rst.setTrdFundValuationTradeDto(trdFundValuationTradeDto);
                return rst;
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回确认.getValue()) ) {
                //赎回确认 ==>对应接口【货币基金赎回确认交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                TargetFinProduct item = interfCommonService.getTargetFinProduct(trdFundValuationTradeDto.getAssetId());
                String finprodType = "";
                if(item!=null){
                    finprodType = item.getFinprodType();
                }

                Map<String, Object> map = new HashMap<>();
                if(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(finprodType)){
                    List<ValFundNRedAffirmN> listValFundNRedAffirmN = new ArrayList<ValFundNRedAffirmN>();
                    ValFundNRedAffirmN valFundNRedAffirmN = new ValFundNRedAffirmN();
                    valFundNRedAffirmN = dealBean(valFundNRedAffirmN,trdFundValuationTradeDto,type);
                    listValFundNRedAffirmN.add(valFundNRedAffirmN);
                    map = fundNMoneyFundRedeemConfirmDealRecService.handleValFundNRedAffirmN(listValFundNRedAffirmN,new Date(),params,null);
                }else{
                    List<ValFundMnfRedAffirm> listValFundMnfRedAffirm = new ArrayList<ValFundMnfRedAffirm>();
                    ValFundMnfRedAffirm valFundMnfRedAffirm = new ValFundMnfRedAffirm();
                    //实体类字段映射处理
                    valFundMnfRedAffirm = dealBean(valFundMnfRedAffirm,trdFundValuationTradeDto,type);

                    listValFundMnfRedAffirm.add(valFundMnfRedAffirm);
                    map = fundMoneyFundRedeemConfirmDealRecService.handleValFundMnfRedAffirm(listValFundMnfRedAffirm,new Date(),params,null);
                }

                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

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

                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                rst.setTrdFundValuationTradeDto(trdFundValuationTradeDto);
                return rst;
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回到账.getValue()) ) {
                //赎回到账 ==>对应接口【基金&计划类赎回到账交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getProdId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValRedAccoutTrade> listValRedAccoutTrade = new ArrayList<ValRedAccoutTrade>();
                ValRedAccoutTrade valRedAccoutTrade = new ValRedAccoutTrade();
                //实体类字段映射处理
                valRedAccoutTrade = dealBean(valRedAccoutTrade,trdFundValuationTradeDto,type);

                listValRedAccoutTrade.add(valRedAccoutTrade);
                Map<String, Object>  map = fundRedeemArrivalAccountDealRecService.handleValRedAccoutTrade(listValRedAccoutTrade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
                    // 入账信息表List
                    List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                            "ValDealAccounting");
                    result.setListValDealAccounting(listValDealAccounting);

                    // 入账明细信息表List
                    List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail");
                    result.setListValDealAccountingDetail(listValDealAccountingDetail);

                    // 账务明细代码表List
                    List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                            "ValBokDetailId");
                    result.setListValBokDetailId(listValBokDetailId);

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
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                rst.setTrdFundValuationTradeDto(trdFundValuationTradeDto);
                return rst;
            }else{
                //返回初始化
                return trdFundTradeResult;
            }
        }else{
            //返回初始化
            return trdFundTradeResult;
        }
    }

    /**
     * @auther: 属性转换处理-买入、卖出
     * @date: 2022/3/1 16:01
     * @param: 
     * @return: 
     */
    public ValFundTrade dealBean(ValFundTrade valFundTrade,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valFundTrade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
        //实体类字段映射处理
        if("A".equals(type) && "01".equals(dto.getFundType())){
            valFundTrade.setBusiType("06");//业务类型
        }else{
            valFundTrade.setBusiType("07");//业务类型
        }
        valFundTrade.setAssetName(dto.getAssetAbbr());//资产名称
        valFundTrade.setDataActionSign("01");//数据动作标志
        valFundTrade.setPrinAmt(dto.getTradeAmt());//交易本金
        valFundTrade.setShareAmt(dto.getDealNum());//交易份额
        valFundTrade.setUnitCprice(ArithmeticUtil.divide(dto.getTradeAmt(),dto.getDealNum(),4, RoundingMode.HALF_UP));
        valFundTrade.setMyAcctNo(dto.getTrdMyAcctNo());//付款账户-资金账户
        valFundTrade.setTradeAmt(dto.getSettleAmt());//交易金额
        valFundTrade.setIsClean("N"); //有无清算指令
        valFundTrade.setIsCommission("N");

        return valFundTrade;
    }

    /**
     * @auther: 属性转换处理-申购、认购
     * @date: 2022/3/1 16:01
     * @param:
     * @return:
     */
    public ValApplyPurTade dealBean(ValApplyPurTade valApplyPurTade,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valApplyPurTade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        //比较界面和接口字段差异，并手动给接口字段赋值
        if("A".equals(type) && "01".equals(dto.getFundType())){
            valApplyPurTade.setBusiType("06");//业务类型
        }else {
            valApplyPurTade.setBusiType("07");//业务类型
        }

        valApplyPurTade.setAssetName(dto.getAssetAbbr());//资产名称
        valApplyPurTade.setDataActionSign("01");//数据动作标志
        valApplyPurTade.setIsClean("N"); //有无清算指令
        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_认购.getValue()) ){
                valApplyPurTade.setTradeDate(dto.getApplyDate());//交易日
                valApplyPurTade.setPurAmt(dto.getApplyAmt());//申购金额
                valApplyPurTade.setMyAcctNo(dto.getSubMyAcctNo());//付款账户
                valApplyPurTade.setSettleDate(dto.getSubSettleDate());//资金交收日
            }else if(dto.getPs().equals(DictCodeList.交易方向_申购.getValue()) ){
                valApplyPurTade.setTradeDate(dto.getPurApplyDate());//交易日
                valApplyPurTade.setPurAmt(dto.getPurApplyAmt());//认购金额
                valApplyPurTade.setMyAcctNo(dto.getPurMyAcctNo());//付款账户
                valApplyPurTade.setSettleDate(dto.getPurSettleDate());//资金交收日
            }
        }
        return valApplyPurTade;
    }

    /**
     * @auther: 属性转换处理-申购确认、认购确认
     * @date: 2022/3/1 16:01
     * @param:
     * @return:
     */
    public ValAffirmPurTrade dealBean(ValAffirmPurTrade valAffirmPurTrade,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valAffirmPurTrade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        //比较界面和接口字段差异，并手动给接口字段赋值
        valAffirmPurTrade.setAssetName(dto.getAssetAbbr());//资产名称
        valAffirmPurTrade.setDataActionSign("01");//数据动作标志
        valAffirmPurTrade.setIsClean("N"); //有无清算指令
        //valAffirmPurTrade.setFundType();//基金类型
        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_申购确认.getValue()) ){
                valAffirmPurTrade.setTradeDate(dto.getPurConfirmDate());//交易日
                valAffirmPurTrade.setCfmAmt(dto.getPurConfirmAmt());//确认金额
                valAffirmPurTrade.setCfmShare(dto.getPurConfirmNum());//确认份额
                //valAffirmPurTrade.setRefundAmt(dto.getInterestIncome());//利息金额
                valAffirmPurTrade.setCfmUnitCpr(dto.getPurUnitValue());//单位净值
                valAffirmPurTrade.setFee(dto.getPurFee());//费用
                valAffirmPurTrade.setMyAcctNo(dto.getPurConfirmMyAcctNo());//资金账户
            }else if(dto.getPs().equals(DictCodeList.交易方向_认购确认.getValue()) ){
                valAffirmPurTrade.setTradeDate(dto.getSubConfirmDate());//交易日
                valAffirmPurTrade.setCfmAmt(dto.getSubConfirmAmt());//确认金额
                valAffirmPurTrade.setCfmShare(dto.getSubConfirmNum());//确认份额
                valAffirmPurTrade.setRefundAmt(dto.getRaiseInterest());//利息金额
                valAffirmPurTrade.setFee(dto.getSubFee());//费用
                valAffirmPurTrade.setMyAcctNo(dto.getSubConfirmMyAcctNo());//资金账户
            }
        }
        return valAffirmPurTrade;
    }

    /**
     * @auther: 属性转换处理-赎回确认
     * @date: 2022/3/1 16:01
     * @param:
     * @return:
     */
    public ValFundMnfRedAffirm dealBean(ValFundMnfRedAffirm valFundMnfRedAffirm,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valFundMnfRedAffirm,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        //比较界面和接口字段差异，并手动给接口字段赋值

        valFundMnfRedAffirm.setAssetName(dto.getAssetAbbr());//资产名称
        valFundMnfRedAffirm.setTradeDate(dto.getRedConfirmDate());//交易日
//        valFundMnfRedAffirm.setPreDate(dto.getArriveDate());//预计到账日
        valFundMnfRedAffirm.setRedShare(dto.getRedConfirmNum());//确认数量
        valFundMnfRedAffirm.setRedAmt(dto.getRedConfirmAmt());//赎回金额
        valFundMnfRedAffirm.setFee(dto.getRedFee());//赎回费

        valFundMnfRedAffirm.setMyAcctNo(dto.getRedMyAcctNo());//资金账户
        valFundMnfRedAffirm.setUnitCprice(ArithmeticUtil.divide(dto.getRedConfirmAmt(),dto.getRedConfirmNum(),4, RoundingMode.HALF_UP));

        valFundMnfRedAffirm.setDataActionSign("01");//数据动作标志
        valFundMnfRedAffirm.setIsClean("N"); //有无清算指令
        return valFundMnfRedAffirm;
    }

    /**
     * @auther: 属性转换处理-赎回确认(净值型)
     * @date: 2022/3/1 16:01
     * @param:
     * @return:
     */
    public ValFundNRedAffirmN dealBean(ValFundNRedAffirmN valFundNRedAffirmN,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valFundNRedAffirmN,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        //比较界面和接口字段差异，并手动给接口字段赋值

        valFundNRedAffirmN.setAssetName(dto.getAssetAbbr());//资产名称
        valFundNRedAffirmN.setTradeDate(dto.getRedConfirmDate());//交易日
        valFundNRedAffirmN.setPreDate(dto.getArriveDate());//预计到账日
        valFundNRedAffirmN.setRedShare(dto.getRedConfirmNum());//确认数量
        valFundNRedAffirmN.setRedAmt(dto.getRedConfirmAmt());//赎回金额
        valFundNRedAffirmN.setFee(dto.getRedFee());

        valFundNRedAffirmN.setDataActionSign("01");//数据动作标志
        valFundNRedAffirmN.setIsClean("N"); //有无清算指令
        return valFundNRedAffirmN;
    }


    /**
     * @auther: 属性转换处理-赎回到账
     * @date: 2022/3/1 16:01
     * @param:
     * @return:
     */
    public ValRedAccoutTrade dealBean(ValRedAccoutTrade valRedAccoutTrade,TrdFundValuationTradeDto dto,String type){
        BeanUtil.copyPropertiesIgnoreNull(valRedAccoutTrade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        //比较界面和接口字段差异，并手动给接口字段赋值
        if("A".equals(type) && "01".equals(dto.getFundType())){
            valRedAccoutTrade.setTradeType("06");//业务类型
        }else {
            valRedAccoutTrade.setTradeType("07");//业务类型
        }

        valRedAccoutTrade.setTradeDate(dto.getArriveDate());//交易日
        valRedAccoutTrade.setTradeAmt(dto.getTradeAmtArrive());//赎回费
        valRedAccoutTrade.setMyAcctNo(dto.getRedMyAcctNo());//资金账户

        valRedAccoutTrade.setSettleDate(dto.getArriveDate());
        valRedAccoutTrade.setDataActionSign("01");//数据动作标志
        valRedAccoutTrade.setIsClean("N"); //有无清算指令
        return valRedAccoutTrade;
    }

}
