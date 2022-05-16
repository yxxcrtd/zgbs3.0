package com.joyintech.tams.exchange.entrust.etrexchange.position.service;

import static com.joyintech.tams.constant.DictCodeList.*;
import static com.joyintech.tams.constant.DictCodeListExt2.交易方向_配股;
import static com.joyintech.tams.constant.DictCodeListExt2.申购方式_网上申购;
import static com.joyintech.tams.constant.DictCodeListExt3.业务动作_投资指令;
import static com.joyintech.tams.constant.DictCodeListExt3.交易方向_配债;
import static com.joyintech.tams.constant.DictCodeListExt3.金融产品其他代码类型_新股申购代码;
import static com.joyintech.tams.constant.DictCodeListExt3.金融产品其他代码类型_配债代码;
import static com.joyintech.tams.constant.DictCodeListExt3.金融产品其他代码类型_配股代码;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.BID_RESULT;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.BUSI_ACT;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.B_TOTAL_TRADE_ID;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.CCY;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.DELIVERY_TYPE;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.ETFREDEMPTION_METHOD;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.FINPROD_ID;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTEGRATE_SEC_AMOUNT;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERFACE_TYPE;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_BOND002;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_BOND003;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_FUND002;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_REPO003;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INTERF_TYPE_STOCK001;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.INV_AIM;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.ORD_ENTRUST_ID;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.OWN_SEC_AMOUNT;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.POSITION_ETR_PAGE;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.POSITION_TYPE_ETR;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PS;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PTL_CASH_LIST;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PTL_LIST;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PTL_RO_LIST;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PTL_SEC_LIST;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.PURCH_MODLE;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.SAME_DAY_TURN;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.SETTLE_SPEED;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.SHARES_SEC_LIST;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.SHARES_TRADE_PLATFORM;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.STANDARD_SEC_AMOUNT;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.STANDARD_SEC_ID;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.TRADE_PLACE;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.WORK_FLOW_ID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeOtherCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeOperateRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePortfolioRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.FinProductOthCodeDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.position.bean.PositionRequestBaseBean;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.TrdMoreConvrateDto;

@Service
public class PositionRequestServiceEtrExchangeTradeSaveRequest {

    public static final String STANDAR_SEC_ID = "XN_PRODID999";
    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CommonPositionEngineService commonPositionEngineService;
    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;
    @Autowired
    private EtrTradeOtherCommonService etrTradeOtherCommonService;

    /**
     * EtrExchangeTradeSaveRequest请求对象使用
     *
     * @param entity
     */
    public void sendEtrExchangeTradeSaveRequest(EtrExchangeTradeSaveRequest entity, String decisionAct, String systemAct) {
        log.info("sendEtrExchangeTradeSaveRequest() called with parameters => 【entity = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        this.setCommonField(entity, resultMap,decisionAct);//设置公共字段，个性化的需要在具体接口中覆盖
        String finprodType = entity.getFinprodType2();
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType) 
        		&& !DictCodeListExt3.交易方向_质押券入库.getValue().equals(entity.getPs())
        		&& !DictCodeListExt3.交易方向_质押券出库.getValue().equals(entity.getPs())) {
            String type6 = commonPositionEngineService.getType6(entity.getFinprodId());
            if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)) {
                finprodType += "-" + type6;
            }
        }
        String interType = commonPositionEngineService.getPositionInterType(POSITION_TYPE_ETR, POSITION_ETR_PAGE, finprodType, entity.getPs(), entity.getOptType(), entity.getClass().getName());
        log.info("匹配头寸接口：[{}]", interType);
        if (INTERF_TYPE_BOND002.equals(interType)) {
            this.bond002(entity, resultMap);
        } else if (INTERF_TYPE_BOND003.equals(interType)) {
            this.bond003(entity, resultMap);
        } else if (INTERF_TYPE_FUND002.equals(interType)) {
            this.fund002(entity, resultMap);
        } else if (INTERF_TYPE_REPO003.equals(interType)) {
            this.repo003(entity, resultMap);
        } else if (INTERF_TYPE_STOCK001.equals(interType)) {
            this.stock001(entity, resultMap);
        } else {
            log.warn("未匹配到头寸接口");
            //TODO 根据条件判断调用其他接口
            return;
        }
        PositionHttpRequest.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param entity
     */
    public void sendDelete(EtrExchangeTradeSaveRequest entity) {
        log.info("sendDelete() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), entity.getAssetType2());
        PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), POSITION_TYPE_ETR, busiType, entity.getPs());
    }

    /**
     * 撤销时使用---撤销指令-共通接口
     *
     * @param entity
     */
    public void sendRevoke(EtrExchangeTradeOperateRequest entity) {
        // 撤回不调用头寸
        return;
//        log.info("sendRevoke() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
//        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), null);
//        PositionHttpRequest.dorevoke(entity.getEntrustId(), POSITION_TYPE_TRD, busiType, entity.getPs());
    }

    /**
     * 关闭时使用---关闭-共通接口
     *
     * @param entity
     */
    public void sendClose(EtrExchangeTradeOperateRequest entity) {
        log.info("sendClose() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), null);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        // 关闭指令时，无流程ID，故生成UUID传入
        resultMap.put("totalTradeId", IDGenerator.generate(32));
        resultMap.put("modelType", POSITION_TYPE_ETR);

        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("busiType", busiType);
        map.put(PS, entity.getPs());
        map.put(BUSI_ACT, DictCodeListExt3.业务动作_指令手动关闭.getValue());
        map.put("systemAct", DictCodeListExt3.系统动作_关闭.getValue());
        map.put("closeId", entity.getEntrustId());
        list.add(map);
        resultMap.put("data", list);

        PositionHttpRequest.batchClose(resultMap);
    }

    /**
     * 场内现券-指令    bond002
     *
     * @param entity
     * @param resultMap
     */
    private void bond002(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND002);
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        resultMap.put(DELIVERY_TYPE, commonPositionEngineService.getDeliveryType(entity.getFinprodId()));
        resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed((String) resultMap.get(DELIVERY_TYPE)));
        resultMap.put(BID_RESULT, DictCodeList.是否_否.getValue());
        resultMap.put(PURCH_MODLE, null);
        resultMap.put(FINPROD_ID, entity.getFinprodId());
        resultMap.put(SHARES_TRADE_PLATFORM, entity.getTradePlat());

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;

            List<Map<String, Object>> ptlList = new ArrayList<>(portfolioListSize);
            fillAttributes(entity);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
                if (交易方向_行权_回售.getValue().equals(entity.getPs())) {
                    bean.setCashAmount(item.getEntrustAmt());
                } else {
                    BigDecimal fprice = Objects.isNull(entity.getFprice()) ? new BigDecimal(0) : entity.getFprice();
                    bean.setCashAmount(fprice.multiply(item.getEntrustNum()));
                }
                BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                bean.setSecAmount(secAmount);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                map.put(ORD_ENTRUST_ID, null);
                map.put(INV_AIM, entity.getInvAim());
                map.put(INTEGRATE_SEC_AMOUNT, 0);
                map.put(OWN_SEC_AMOUNT, secAmount);

                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);
        }
    }

    private void fillAttributes(EtrExchangeTradeSaveRequest entity) {
        for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
            item.setEntrustId(entity.getEntrustId());
            item.setFinprodId(entity.getFinprodId());
        }
    }

    /**
     * 可转债-指令    bond003
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND003);
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        resultMap.put(DELIVERY_TYPE, commonPositionEngineService.getDeliveryType(entity.getFinprodId()));
        /*if ((DictCodeListExt3.结算方式_头寸引擎_RTGS.getValue()).equals(resultMap.get(DELIVERY_TYPE))&&(DictCodeListExt1.交易场所_深圳证券交易所.getValue()).equals(resultMap.get(TRADE_PLACE))) {//头寸引擎深交所RTGS都是大宗交易
            resultMap.put(SHARES_TRADE_PLATFORM, DictCodeList.交易平台_大宗交易平台.getValue());
        }*/
        resultMap.put(SHARES_TRADE_PLATFORM, entity.getTradePlat());
        resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed((String) resultMap.get(DELIVERY_TYPE)));

        String ps = entity.getPs();
        if (交易方向_申购.getValue().equals(entity.getPs())) {
            resultMap.put(PURCH_MODLE, 申购方式_网上申购.getValue());
        }

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;
            fillAttributes(entity);
            // 组合现金数据集
            List<Map<String, Object>> ptlCashList = new ArrayList<>(portfolioListSize);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
                bean.setFinprodId(null);
                bean.setSecAcctId(null);
                if (交易方向_买入.getValue().equals(ps)
                        || 交易方向_卖出.getValue().equals(ps)) {
                    BigDecimal fprice = Objects.isNull(entity.getFprice()) ? new BigDecimal(0) : entity.getFprice();
                    bean.setCashAmount(fprice.multiply(item.getEntrustNum()));
                } else if (交易方向_申购.getValue().equals(ps)) {
                    // 系统参数.预估中签比例*指令金额
//                    bean.setCashAmount(item.getEntrustAmt().multiply(CacheUtil.getSysParam(BussinessConstant.PREDICT_WIN_PERCENT)));
                } else if (DictCodeListExt3.交易方向_配债.getValue().equals(ps)
                        || 交易方向_行权_回售.getValue().equals(ps)) {
                    bean.setCashAmount(item.getEntrustAmt());
                }
                bean.setSecAmount(BigDecimal.ZERO);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                ptlCashList.add(map);
            }
            resultMap.put(PTL_CASH_LIST, ptlCashList);

            if (DictCodeListExt3.交易方向_债转股.getValue().equals(ps)) {
                // 股票持仓数据集
                List<Map<String, Object>> sharesSecList = new ArrayList<>(portfolioListSize);
                for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                    bean = new PositionRequestBaseBean(item);
                    bean.setSecAmount(item.getToShareAvailableNum());
                    bean.setCashAmount(null);

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    sharesSecList.add(map);
                }
                resultMap.put(SHARES_SEC_LIST, sharesSecList);
            }

            // 可转债数据集
            List<Map<String, Object>> bondSecList = new ArrayList<>(portfolioListSize);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
                BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                bean.setSecAmount(secAmount);
                bean.setCashAmount(BigDecimal.ZERO);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                //融入券券面总额
                map.put(INTEGRATE_SEC_AMOUNT, 0);
                //本方券券面总额
//                if (交易方向_行权_回售.getValue().equals(ps)) {
                    map.put(OWN_SEC_AMOUNT, secAmount);
//                } else {
//                    map.put(OWN_SEC_AMOUNT, 0);
//                }
                bondSecList.add(map);
            }
            resultMap.put("bondSecList", bondSecList);

            if (DictCodeListExt3.交易方向_配债.getValue().equals(ps) || 交易方向_申购.getValue().equals(ps)) {
                // 配债申购数据集
                List<Map<String, Object>> applyBondSecList = new ArrayList<>(portfolioListSize);
                for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                    bean = new PositionRequestBaseBean(item);
                    bean.setFinprodId(this.getOthCode(ps, entity.getFinprodId()));
                    bean.setSecAmount(commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum()));
                    bean.setCashAmount(null);

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    applyBondSecList.add(map);
                }
                resultMap.put("applyBondSecList", applyBondSecList);
            }
        }
    }

    /**
     * 基金-指令    fund002
     *
     * @param entity
     * @param resultMap
     */
    private void fund002(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_FUND002);
        // 业务动作
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        resultMap.put(SETTLE_SPEED, null);
        resultMap.put(DELIVERY_TYPE, null);
        // 当日回转
        resultMap.put(SAME_DAY_TURN, DictCodeList.是否_否.getValue());
        // ETF申赎方式
        resultMap.put(ETFREDEMPTION_METHOD, null);

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;
            fillAttributes(entity);
            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(portfolioListSize);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
                bean.setSecAmount(item.getEntrustNum());
                bean.setCashAmount(item.getEntrustAmt());

                Map<String, Object> map = BeanUtil.object2Map(bean);
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            // 组合附属数据集 空
            resultMap.put("ptlSubList", Lists.newArrayList());
        }
    }

    /**
     * 质押式通用回购-指令    repo003
     *
     * @param entity
     * @param resultMap
     */
    private void repo003(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_REPO003);
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        resultMap.put(SETTLE_SPEED, null);
        resultMap.put(DELIVERY_TYPE, null);

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;
            fillAttributes(entity);
            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(portfolioListSize);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);
//                bean.setFinprodId(null);
                bean.setSecAmount(null);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                // 标准券代码
                map.put(STANDARD_SEC_ID, STANDAR_SEC_ID);

                // 再折算率	reConversionRate "调用【再折算率查询】参数：资金账户 = 指令资金账户
                ImmutableMap<String, Object> param = ImmutableMap.of("acctNo", entity.getAcctNo());
                TrdMoreConvrateDto moreConvrate = etrExchangeTradeCommon001Service.getMoreConvrate(param);
                if (Objects.nonNull(moreConvrate)) {
                    map.put("reConversionRate", moreConvrate.getMoreConvrate());
                }
                if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(entity.getPs())
                		|| DictCodeListExt3.交易方向_质押券出库.getValue().equals(entity.getPs())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("finprodId",entity.getFinprodId());
                    BigDecimal faceValue = etrTradeOtherCommonService.selectFaceValue(params);
                    // 标准券总额 =质押券数量*折算率*最新面值
                    BigDecimal standardSecAmount = item.getEntrustNum().multiply(entity.getEntrustPrice()).divide(BigDecimal.valueOf(10000)).multiply(faceValue);
                    map.put(STANDARD_SEC_AMOUNT, standardSecAmount.setScale( 0, BigDecimal.ROUND_DOWN ).longValue()*100);//向下取整
                	map.put("cashAmount", null);//出入库时，可以为空
                }else {
                    // 标准券总额 = 标准券数量*100
                    map.put(STANDARD_SEC_AMOUNT, item.getEntrustNum().multiply(BigDecimal.valueOf(100)));
                }
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            String ps = entity.getPs();
            // 质押券数据集	ptlSecList
            if (DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps)) {
                List<Map<String, Object>> ptlSecList = new ArrayList<>(portfolioListSize);
                for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                    bean = new PositionRequestBaseBean(item);
                    // 券面总额 指令.指令数量*100
                    bean.setCashAmount(null);
                    BigDecimal secAmount = item.getEntrustNum().multiply(BigDecimal.valueOf(100));
                    bean.setSecAmount(secAmount);

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    map.put(INTEGRATE_SEC_AMOUNT, 0);
                    map.put(OWN_SEC_AMOUNT, secAmount);

                    ptlSecList.add(map);
                }
                resultMap.put(PTL_SEC_LIST, ptlSecList);
            }
        }
    }

    /**
     * 股票-指令    stock001
     *
     * @param entity
     * @param resultMap
     */
    private void stock001(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_STOCK001);
        resultMap.put(BUSI_ACT, 业务动作_投资指令.getValue());
        resultMap.put(SETTLE_SPEED, null);
        resultMap.put(DELIVERY_TYPE, null);

        if (CollectionUtils.isNotEmpty(entity.getPortfolioList())) {
            int portfolioListSize = entity.getPortfolioList().size();
            PositionRequestBaseBean bean;
            fillAttributes(entity);
            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(portfolioListSize);
            for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                bean = new PositionRequestBaseBean(item);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                map.put(ORD_ENTRUST_ID, null);
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            String ps = entity.getPs();
            // 组合配股/新股申购数据集	ptlRoList
            if (交易方向_配股.getValue().equals(ps) || 交易方向_申购.getValue().equals(ps)) {
                List<Map<String, Object>> ptlRoList = new ArrayList<>(portfolioListSize);
                for (EtrExchangeTradePortfolioRequest item : entity.getPortfolioList()) {
                    bean = new PositionRequestBaseBean(item);
                    // 配股/申购代码
                    bean.setFinprodId(this.getOthCode(ps, entity.getFinprodId()));

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    map.put(ORD_ENTRUST_ID, null);
                    ptlRoList.add(map);
                }
                resultMap.put(PTL_RO_LIST, ptlRoList);
            }
        }
    }

    /**
     * 获取申购、配股配债等代码
     *
     * @param ps        交易方向
     * @param finprodId 金融产品代码
     * @return 申购等代码
     */
    private String getOthCode(String ps, String finprodId) {
        List<String> othTypeList = Lists.newArrayList();
        if (交易方向_配股.getValue().equals(ps)) {
            othTypeList.add(金融产品其他代码类型_配股代码.getValue());
        } else if (交易方向_申购.getValue().equals(ps)) {
            othTypeList.add(金融产品其他代码类型_新股申购代码.getValue());
        } else if (交易方向_配债.getValue().equals(ps)) {
            othTypeList.add(金融产品其他代码类型_配债代码.getValue());
        }
        ImmutableMap<String, Object> map = ImmutableMap.of("othTypeList", othTypeList, "finprodId", finprodId);

        List<FinProductOthCodeDto> codeDtos = etrExchangeTradeCommon001Service.getFinprodOthCodeInfo(map);

        if (CollectionUtils.isNotEmpty(codeDtos)) {
            return codeDtos.get(0).getOthCode();
        }
        return finprodId;
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(EtrExchangeTradeSaveRequest entity, Map<String, Object> resultMap,String decisionAct) {
        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getEntrustId());
        resultMap.put("balanceCheck", balanceCheck);
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_流程提交.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_委托.getValue());

        //设置头寸业务类型
        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), entity.getAssetType2());
        resultMap.put("busiType", busiType);
        // 交易方向
        resultMap.put(PS, entity.getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put(TRADE_PLACE, entity.getTradeMarket());
        // 交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, commonPositionEngineService.getSharesTradePlatform(entity.getTradePlat(), entity.getPs(), entity.getDeclaType(), ""));
        // 流程编号	指令的工作流ID
        resultMap.put(WORK_FLOW_ID, entity.getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getVdate()));
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put(CCY, entity.getCcy());
        } else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        if (DictCodeListExt3.系统动作_修改.getValue().equals(decisionAct)) {
            resultMap.put(B_TOTAL_TRADE_ID, entity.getEntrustId());
        } else {
            resultMap.put(B_TOTAL_TRADE_ID, null);
        }
    }



}
