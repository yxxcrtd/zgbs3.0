package com.joyintech.tams.exchange.service.trade.position.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.service.TamsBusinessCommonService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionPledge;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.response.PortfolioCalculateResponse;
import com.joyintech.tams.exchange.common.utils.TradePlatUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionPledgeService;
import com.joyintech.tams.exchange.service.trade.management.service.EntrustManagement;
import com.joyintech.tams.exchange.service.trade.management.service.PortfolioManager;
import com.joyintech.tams.exchange.service.trade.position.bean.PositionRequestBaseBean;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeOtherCommonService;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeList.交易方向_行权_回售;
import static com.joyintech.tams.constant.DictCodeList.是否_否;
import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeList.交易方向_申购;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestServiceInstructionRequest {

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(PositionRequestServiceInstructionRequest.class);
    @Autowired
    private CommonPositionEngineService commonPositionEngineService;
    @Autowired
    private PortfolioManager portfolioManager;
    @Autowired
    private InstructionPledgeService instructionPledgeService;
    @Autowired
    private EntrustManagement entrustManagement;
    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;
    @Autowired
    private EtrTradeOtherCommonService etrTradeOtherCommonService;
    @Autowired
    private TamsBusinessCommonService tamsBusinessCommonService;

    /**
     * Instruction 请求对象使用
     */
    public void sendRequest(PositionInstructionRequest entity, String busiAct, String decisionAct, String systemAct) {
        log.info("sendRequest() called with parameters => 【entity = {}】, 【busiAct = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), busiAct, decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(BUSI_ACT, busiAct);
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        //设置公共字段，个性化的需要在具体接口中覆盖
        this.setCommonField(entity, resultMap);
        String finprodType = entity.getFinprodType2();
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)
                && !交易方向_质押券入库.getValue().equals(entity.getPs())
                && !交易方向_质押券出库.getValue().equals(entity.getPs())) {
            String type6 = commonPositionEngineService.getType6(entity.getFinprodId());
            if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)) {
                finprodType += "-" + type6;
            }
        }
        if (DictCodeList.回购类型_质押式协议回购.getValue().equals(entity.getAssetType1())) {
            finprodType = entity.getAssetType() + "-" + entity.getAssetType1();
        }
        String interType = commonPositionEngineService.getPositionInterType(POSITION_TYPE_TRD, POSITION_TRD_PAGE, finprodType, entity.getPs(), busiAct, entity.getClass().getName());
        if (INTERF_TYPE_BOND002.equals(interType)) {
            this.bond002(entity, resultMap);
        } else if (INTERF_TYPE_BOND003.equals(interType)) {
            this.bond003(entity, resultMap);
        } else if (INTERF_TYPE_FUND002.equals(interType)) {
            this.fund002(entity, resultMap);
        } else if (INTERF_TYPE_REPO003.equals(interType)) {
            this.repo003(entity, resultMap);
        } else if (INTERF_TYPE_REPO002.equals(interType)) {
            this.repo002(entity, resultMap);
        } else if (INTERF_TYPE_STOCK001.equals(interType)) {
            this.stock001(entity, resultMap);
        } else {
            return;
        }
        PositionHttpRequest.send(resultMap);
    }

    /**
     * 场内现券-下单    bond002
     *
     * @param entity    下单信息
     * @param resultMap 参数集合
     */
    private void bond002(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND002);
        resultMap.put(DELIVERY_TYPE, parseDeliveryType(entity));
        resultMap.put(SETTLE_SPEED, parseSettleSpreed(resultMap));
        resultMap.put(BID_RESULT, 是否_否.getValue());
        resultMap.put(PURCH_MODLE, null);
        resultMap.put(FINPROD_ID, entity.getFinprodId());
        resultMap.put(B_TOTAL_TRADE_ID, null);
        resultMap.put(SHARES_TRADE_PLATFORM, TradePlatUtils.trd2EtrTradePlat(entity));

        List<PortfolioCalculateResponse> calculates = entity.getPortfolioCalculateResponses();
        if (CollectionUtils.isEmpty(calculates)) {
            calculates = getCalculateList(entity);
        }
        int calculateSize = calculates.size();

        if (CollectionUtils.isNotEmpty(calculates)) {
            PositionRequestBaseBean bean;
            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
                BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                bean.setSecAmount(secAmount);
                BigDecimal price = Objects.isNull(entity.getFullPrice()) ? BigDecimal.ZERO : entity.getFullPrice();
                if (交易方向_行权_回售.getValue().equals(entity.getPs())) {
                    price = entity.getEntrustPrice();
                }
                bean.setCashAmount(price.multiply(BigDecimal.valueOf(item.getEntrustNum())).setScale(2, RoundingMode.HALF_UP));

                Map<String, Object> map = BeanUtil.object2Map(bean);
                // 下单委托编号
                map.put(ORD_ENTRUST_ID, entity.getEntrustNo());
                // 下单编号，撤委托使用
                map.put(ORD_ID, entity.getEntrustNo());
                map.put(INV_AIM, entity.getInvAim());
                map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                map.put(OWN_SEC_AMOUNT, secAmount);
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);
        }
    }

    /**
     * 可转债-下单    bond003
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND003);
        resultMap.put(DELIVERY_TYPE, parseDeliveryType(entity));
        resultMap.put(SETTLE_SPEED, parseSettleSpreed(resultMap));
        resultMap.put(PURCH_MODLE, null);
        resultMap.put(SHARES_TRADE_PLATFORM, TradePlatUtils.trd2EtrTradePlat(entity));

        List<PortfolioCalculateResponse> calculates = entity.getPortfolioCalculateResponses();
        if (CollectionUtils.isEmpty(calculates)) {
            calculates = getCalculateList(entity);
        }
        int calculateSize = calculates.size();

        if (CollectionUtils.isNotEmpty(calculates)) {
            PositionRequestBaseBean bean;

            // 组合现金数据集
            List<Map<String, Object>> list = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
                bean.setFinprodId(null);
                BigDecimal price = Objects.isNull(entity.getFullPrice()) ? BigDecimal.ZERO : entity.getFullPrice();
                bean.setCashAmount(price.multiply(BigDecimal.valueOf(item.getEntrustNum())).setScale(2, RoundingMode.HALF_UP));

                Map<String, Object> map = BeanUtil.object2Map(bean);
                list.add(map);
            }
            resultMap.put(PTL_CASH_LIST, list);

            // 股票持仓数据集
            String ps = entity.getPs();
            if (交易方向_债转股.getValue().equals(ps)) {
                // 股票持仓数据集
                List<Map<String, Object>> sharesSecList = new ArrayList<>(calculateSize);
                for (PortfolioCalculateResponse item : calculates) {
                    bean = new PositionRequestBaseBean();
                    bean.setPortfolioId(item.getPortfolioId());
                    bean.setFinprodId(entity.getConShareId());
                    bean.setSecAmount(entity.getConNum());
                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    sharesSecList.add(map);
                }
                resultMap.put(SHARES_SEC_LIST, sharesSecList);
            }

            // 可转债数据集
            List<Map<String, Object>> bondSecList = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
                BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                bean.setSecAmount(secAmount);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                //融入券券面总额
                map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                //本方券券面总额
                map.put(OWN_SEC_AMOUNT, secAmount);
                bondSecList.add(map);
            }
            resultMap.put("bondSecList", bondSecList);

            if (交易方向_配债.getValue().equals(ps) || 交易方向_申购.getValue().equals(ps)) {
                // 配债申购数据集
                List<Map<String, Object>> applyBondSecList = new ArrayList<>(calculateSize);
                for (PortfolioCalculateResponse item : calculates) {
                    bean = new PositionRequestBaseBean(entity);
                    bean.setPortfolioId(item.getPortfolioId());
//                    bean.setFinprodId(entity.getConShareId());
                    bean.setFinprodId(entrustManagement.getOthCodeFromInvestment(entity.getEntrustProp(), entity));
                    BigDecimal secAmount = commonPositionEngineService.getSecAmountByBondUnit(item.getEntrustNum());
                    bean.setSecAmount(secAmount);
                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    //融入券券面总额
                    map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                    //本方券券面总额
                    map.put(OWN_SEC_AMOUNT, secAmount);
                    applyBondSecList.add(map);
                }
                resultMap.put(APPLY_BOND_SEC_LIST, applyBondSecList);
            }
        }
    }

    /**
     * 基金-下单    fund002
     *
     * @param entity
     * @param resultMap
     */
    private void fund002(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_FUND002);
        // 清算速度
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        // 当日回转
        resultMap.put(SAME_DAY_TURN, 是否_否);
        // ETF申赎方式
        resultMap.put(ETFREDEMPTION_METHOD, null);

        List<PortfolioCalculateResponse> calculates = entity.getPortfolioCalculateResponses();
        if (CollectionUtils.isEmpty(calculates)) {
            calculates = getCalculateList(entity);
        }
        int calculateSize = calculates.size();

        if (CollectionUtils.isNotEmpty(calculates)) {
            PositionRequestBaseBean bean;

            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
                bean.setCashAmount(item.getEntrustAmt().setScale(2, RoundingMode.HALF_UP));
                bean.setSecAmount(BigDecimal.valueOf(item.getEntrustNum()));

                Map<String, Object> map = BeanUtil.object2Map(bean);
                // 下单委托编号
                map.put(ORD_ENTRUST_ID, entity.getEntrustNo());
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            // 组合附属数据集 空
            resultMap.put("ptlSubList", Lists.newArrayList());
        }
    }

    /**
     * 质押式通用回购-下单    repo003
     *
     * @param entity    下单对象
     * @param resultMap 参数集合
     */
    private void repo003(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_REPO003);
        resultMap.put(SETTLE_SPEED, null);
        resultMap.put(SHARES_TRADE_PLATFORM, null);
        resultMap.put(DELIVERY_TYPE, null);
        resultMap.put(TOTAL_TRADE_ID, entity.getEntrustId());

        // 调用组合计算接口，获取分配信息
        List<PortfolioCalculateResponse> calculates = entity.getPortfolioCalculateResponses();
        if (CollectionUtils.isEmpty(calculates)) {
            calculates = getCalculateList(entity);
        }
        int calculateSize = calculates.size();

        if (CollectionUtils.isNotEmpty(calculates)) {
            PositionRequestBaseBean bean;

            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
//                bean.setFinprodId(null);
                bean.setCashAmount(item.getEntrustAmt());
                bean.setSecAmount(BigDecimal.ZERO);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                // 下单委托编号
                map.put(ORD_ENTRUST_ID, entity.getEntrustNo());
                // 标准券代码
                map.put(STANDARD_SEC_ID, STANDAR_SEC_ID);
                //指令券面总额
                map.put(ETR_SEC_AMOUNT, item.getEtrSecAmount());
                if (交易方向_质押券入库.getValue().equals(entity.getPs())
                        || 交易方向_质押券出库.getValue().equals(entity.getPs())) {
                    //出入库时，可以为空
                    map.put("cashAmount", null);
                    Map<String, Object> params = new HashMap<>();
                    params.put("finprodId", entity.getFinprodId());
                    BigDecimal faceValue = etrTradeOtherCommonService.selectFaceValue(params);
                    // 标准券总额=质押券数量*折算率*最新面值
                    BigDecimal standardSecAmount = item.getEntrustAmt().multiply(faceValue).divide(BigDecimal.valueOf(100));
                    //向下取整
                    map.put(STANDARD_SEC_AMOUNT, standardSecAmount.setScale(0, BigDecimal.ROUND_DOWN).longValue() * 100);
                } else {
                    // 标准券总额=标准券数量*100
                    map.put(STANDARD_SEC_AMOUNT, item.getEntrustNum() * 100);
                }
                // 再折算率	reConversionRate "调用【再折算率查询】参数：资金账户 = 指令资金账户 TODO
                ImmutableMap<String, Object> param = ImmutableMap.of("acctNo", entity.getAcctNo());
//                TrdMoreConvrateDto moreConvrate = instruction.getMoreConvrate(param);
//                if (Objects.nonNull(moreConvrate)) {
//                    map.put("reConversionRate", moreConvrate.getMoreConvrate());
//                }
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            String ps = entity.getPs();
            // 质押券数据集	ptlSecList
            if (交易方向_质押券入库.getValue().equals(ps) || 交易方向_质押券出库.getValue().equals(ps)) {
                List<Map<String, Object>> list = new ArrayList<>(calculateSize);
                for (PortfolioCalculateResponse item : calculates) {
                    bean = new PositionRequestBaseBean(entity);
                    bean.setPortfolioId(item.getPortfolioId());
                    bean.setFinprodId(entity.getFinprodId());
                    bean.setCashAmount(BigDecimal.ZERO);
                    // 券面总额 下单数量*100
                    BigDecimal secAmount = commonPositionEngineService.multiplyWithOneHundred(item.getEntrustNum());
                    bean.setSecAmount(secAmount);

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    //融入券券面总额
                    map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                    //本方券券面总额 指令对应组合.指令数量*100
                    map.put(OWN_SEC_AMOUNT, secAmount);

                    list.add(map);
                }
                resultMap.put(PTL_SEC_LIST, list);
            }
        }
    }

    /**
     * 质押式协议回购-下单    repo002
     *
     * @param entity
     * @param resultMap
     */
    private void repo002(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_REPO002);
        resultMap.put(SETTLE_SPEED, null);
        resultMap.put(SHARES_TRADE_PLATFORM, null);
        resultMap.put(DELIVERY_TYPE, null);

        // 协议回购不存在多组合，不需要循环多组合列表
        PositionRequestBaseBean bean;
        String ps = entity.getPs();
        String firstRepoPs = entity.getFirstRepoPs();
        resultMap.put(PS, firstRepoPs);

        //IF 下单.交易方向 = 中途换券
        //IF 下单对应的指令.指令业务类型 = 数据字典.指令业务类型.协议回购中途换券指令-换钱 THEN 数据字典.换券方式.02 换钱 ELSE 数据字典.换券方式.01 换券
        if (交易方向_中途换券.getValue().equals(ps)) {
            if (上农商_指令业务类型_协议回购中途换券指令_换钱.getValue().equals(entity.getEntrustBussType())) {
                resultMap.put(BOND_EXCHANGE_TYPE, "02");
            } else {
                resultMap.put(BOND_EXCHANGE_TYPE, "01");
            }
        } else {
            resultMap.put(BOND_EXCHANGE_TYPE, null);
        }

        // 首期组合现金数据集，IF 交易方向 = 正回购、逆回购、提前购回、到期购回、解除质押 传值 否则为空
        if (交易方向_正回购.getValue().equals(ps)
                || 交易方向_逆回购.getValue().equals(ps)
                || 交易方向_提前购回.getValue().equals(ps)
                || 交易方向_到期购回.getValue().equals(ps)
                || 交易方向_解除质押.getValue().equals(ps)
                || (交易方向_中途换券.getValue().equals(ps) && 上农商_指令业务类型_协议回购中途换券指令_换钱.getValue().equals(entity.getEntrustBussType()))) {
            List<Map<String, Object>> list = new ArrayList<>(1);
            bean = new PositionRequestBaseBean(entity);
//            bean.setCashAmount(NumberUtil.sub(entity.getmTradeAmt(), entity.getmIntAmt()));
            bean.setSecAmount(BigDecimal.ZERO);

            /*if (交易方向_解除质押.getValue().equals(ps)) {
                // 解除质押走线下划款，扣托管户现金
                bean.setCashAcctId(tamsBusinessCommonService.selectProdTghCapacctNo(entity.getPortfolioId()));
            } else {
                // 其他走券商资金账户
                bean.setCashAcctId(entity.getAcctNo());
            }*/
            bean.setCashAcctId(entity.getAcctNo());

            // 交易金额
            // 交易方向= 首期 取回购金额，其他取到期结算金额
            /*if (交易方向_提前购回.getValue().equals(ps)
                    || 交易方向_到期购回.getValue().equals(ps)
                    || 交易方向_解除质押.getValue().equals(ps)) {
                bean.setCashAmount(entity.getmTradeAmt());
            } else {
                bean.setCashAmount(entity.getEntrustAmt());
            }*/

            //if 交易方向= 首期 then 回购金额
            //else if 交易方向=解除质押 and 交易场所 = 深交所 then 下单.结算金额
            //else if 交易方向=解除质押 and 交易场所 = 上交所 then 0
            //else if 交易方向=中途换券 and and 指令业务类型 = 数据字典.指令业务类型.协议回购中途换券指令-换钱
            //else 到期结算金额
            //end if
            if (交易方向_提前购回.getValue().equals(ps)
                    || 交易方向_到期购回.getValue().equals(ps)
                    || 交易方向_解除质押.getValue().equals(ps)) {
                if (交易方向_解除质押.getValue().equals(ps) && 交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
                    bean.setCashAmount(BigDecimal.ZERO);
                } else {
                    bean.setCashAmount(entity.getmTradeAmt());
                }
            } else {
                bean.setCashAmount(entity.getEntrustAmt());
            }


            Map<String, Object> map = BeanUtil.object2Map(bean);
            list.add(map);
            resultMap.put(PTL_CASH_LIST, list);
        }

        // 轧差组合现金数据集
        // IF 交易方向 = 到期续作 传值 ELSE 空
        if (交易方向_到期续做.getValue().equals(ps)) {
            List<Map<String, Object>> list = new ArrayList<>(1);
            bean = new PositionRequestBaseBean(entity);
            bean.setSecAmount(BigDecimal.ZERO);
            // 交易金额
            // IF 回购类型 = 正回购 THEN 指令.到期续作金额 - 指令.首期的到期结算金额
            // IF 回购类型 = 逆回购  and 成交编号找不到对应的首期 THEN 指令.到期续作金额
            // IF 回购类型 = 逆回购  and 成交编号找到对应的首期 THEN  指令.到期续作金额 - 指令.首期的到期结算金额"
            BigDecimal firstTradeAmt = etrTradeCommonervice.getFirstTradeAmt(entity.getTrdMatchId());
            BigDecimal tradeAmt = entity.getEntrustAmt();
            log.info("轧差组合现金数据集:到期续作金额 tradeAmt={}，首期的到期结算金额 firstTradeAmt={}", tradeAmt, firstTradeAmt);
            bean.setCashAmount(tradeAmt.subtract(firstTradeAmt));

            Map<String, Object> map = BeanUtil.object2Map(bean);
            list.add(map);
            resultMap.put(PTL_OFFSET_CASH_LIST, list);
        }

        // 质押券数据集	ptlSecList
        // IF 交易方向 = 首期、中途换券换入and 回购类型 = 正回购 THEN 传值 ELSE 空
        if (交易方向_正回购.getValue().equals(firstRepoPs) ||
                (交易方向_中途换券.getValue().equals(ps) && 交易方向_正回购.getValue().equals(ps))) {
            List<Map<String, Object>> list = new ArrayList<>(1);
            // 查询质押券集合
            InstructionPledge instructionPledge = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(entity.getEntrustId(), entity.getFinprodMarketId());
            if (Objects.nonNull(instructionPledge)) {
                bean = new PositionRequestBaseBean();
                bean.setPortfolioId(entity.getPortfolioId());
                // 债券代码 指令.质押券的金融产品代码
                bean.setFinprodId(instructionPledge.getFinprodId());
                bean.setSecAcctId(entity.getSecAcctId());
                bean.setTradeId(entity.getEntrustNo());
                bean.setEtrId(entity.getEntrustId());
                BigDecimal secAmount = commonPositionEngineService.multiplyWithOneHundred(instructionPledge.getPledgeNumber());
                bean.setSecAmount(secAmount);

                Map<String, Object> map = BeanUtil.object2Map(bean);
                //融入券券面总额
                map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                //本方券券面总额 指令.质押券数量*100 乘以一百
                map.put(OWN_SEC_AMOUNT, secAmount);

                list.add(map);
            }
            resultMap.put(PTL_SEC_LIST, list);
        }

        // 解除质押券数据集	ptlRelieveSecList
        // IF 交易方向 = 交易方向 = 解除质押、到期购回、提前购回、中途换券换出、到期续作 and 回购类型 = 正回购  THEN 传值 ELSE 空 END IF
        if ((交易方向_解除质押.getValue().equals(ps)
                || 交易方向_到期购回.getValue().equals(ps)
                || 交易方向_提前购回.getValue().equals(ps)
                || 交易方向_到期续做.getValue().equals(ps)
                || 交易方向_中途换券.getValue().equals(ps))
            /*&& 交易方向_正回购.getValue().equals(firstRepoPs)*/) {
            List<Map<String, Object>> list = new ArrayList<>(4);
            // 查询质押券集合
            InstructionPledge item = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(entity.getEntrustId(), entity.getFinprodMarketId());
            //取单个质押券
            if (Objects.nonNull(item)) {
                bean = new PositionRequestBaseBean();
                bean.setPortfolioId(entity.getPortfolioId());
                // 债券代码 指令.质押券的金融产品代码
                bean.setFinprodId(item.getFinprodId());
                bean.setSecAcctId(entity.getSecAcctId());
                bean.setTradeId(entity.getEntrustNo());
                bean.setEtrId(entity.getEntrustId());

                //券面总额 IF 交易方向 = 解除质押 THEN 下单.质押券数量*100 ELSE 下单到期续作对应首期的成交回报.质押券数量
                if (交易方向_中途换券.getValue().equals(ps)) {
                    BigDecimal secAmount = commonPositionEngineService.multiplyWithOneHundred(item.getOldPledgeNumber());
                    bean.setSecAmount(secAmount);
                } else {
                    BigDecimal secAmount = commonPositionEngineService.multiplyWithOneHundred(item.getPledgeNumber());
                    bean.setSecAmount(secAmount);
                }


                Map<String, Object> map = BeanUtil.object2Map(bean);
                //融入券券面总额
                map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
                //本方券券面总额
                map.put(OWN_SEC_AMOUNT, bean.getSecAmount());
                list.add(map);
            }
            resultMap.put(PTL_RELIEVE_SEC_LIST, list);
        }
    }

    /**
     * 股票-下单    stock001
     *
     * @param entity
     * @param resultMap
     */
    private void stock001(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_STOCK001);
        // 清算速度
        resultMap.put(SETTLE_SPEED, null);

        // 调用组合计算接口，获取分配信息
        List<PortfolioCalculateResponse> calculates = entity.getPortfolioCalculateResponses();
        if (CollectionUtils.isEmpty(calculates)) {
            calculates = getCalculateList(entity);
        }
        int calculateSize = calculates.size();

        if (CollectionUtils.isNotEmpty(calculates)) {
            PositionRequestBaseBean bean;

            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>(calculateSize);
            for (PortfolioCalculateResponse item : calculates) {
                bean = new PositionRequestBaseBean(entity);
                bean.setPortfolioId(item.getPortfolioId());
                bean.setCashAmount(item.getEntrustAmt().setScale(2, RoundingMode.HALF_UP));
                bean.setSecAmount(BigDecimal.valueOf(item.getEntrustNum()));

                Map<String, Object> map = BeanUtil.object2Map(bean);
                // 下单委托编号
                map.put(ORD_ENTRUST_ID, entity.getEntrustNo());
                ptlList.add(map);
            }
            resultMap.put(PTL_LIST, ptlList);

            String ps = entity.getPs();
            // 组合配股/新股申购数据集	ptlRoList
            if (DictCodeListExt2.交易方向_配股.getValue().equals(ps) || 交易方向_申购.getValue().equals(ps)) {
                List<Map<String, Object>> ptlRoList = new ArrayList<>(calculateSize);
                for (PortfolioCalculateResponse item : calculates) {
                    bean = new PositionRequestBaseBean(entity);
                    bean.setPortfolioId(item.getPortfolioId());
                    // 配股/申购代码
                    bean.setFinprodId(entrustManagement.getOthCodeFromInvestment(entity.getEntrustProp(), entity));
                    bean.setCashAmount(item.getEntrustAmt());
                    bean.setSecAmount(item.getEntrustNum());

                    Map<String, Object> map = BeanUtil.object2Map(bean);
                    // 下单委托编号
                    map.put(ORD_ENTRUST_ID, entity.getEntrustNo());
                    ptlRoList.add(map);
                }
                resultMap.put(PTL_RO_LIST, ptlRoList);
            }
        }

    }

    /**
     * 解析清算速度
     * <p>
     * 清算速度	见数据字典：清算速度
     * "IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.净额结算 THEN 数据字典.清算速度.T+1
     * ELSE IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.RTGS THEN 数据字典.清算速度.T+0
     * ELSE 数据字典.清算速度.T+1
     *
     * @param resultMap 封装集合
     * @return 清算速度
     */
    private String parseSettleSpreed(Map<String, Object> resultMap) {
        return commonPositionEngineService.parseSettleSpreed((String) resultMap.get(DELIVERY_TYPE));
    }

    /**
     * 解析结算方式
     *
     * @param instruction 下单信息
     * @return 结算方式
     */
    private String parseDeliveryType(Instruction instruction) {
        return commonPositionEngineService.getDeliveryType(instruction.getFinprodId());
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(PositionInstructionRequest entity, Map<String, Object> resultMap) {
        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }
        resultMap.put("userId", entity.getTradeId());
        resultMap.put("totalTradeId", entity.getEntrustNo());
        resultMap.put("balanceCheck", balanceCheck);
        // modify by swwan ZGTFCP-1393 交易撤销：流程状态flowStatus 应该传流程生效 02 start
        if (业务动作_交易下单.getValue().equals(resultMap.get(BUSI_ACT))) {
            resultMap.put("flowStatus", 流程节点_流程提交.getValue());
        } else if (业务动作_撤委托.getValue().equals(resultMap.get(BUSI_ACT))) {
            resultMap.put("flowStatus", 流程节点_审批生效.getValue());
        }
        // modify by swwan ZGTFCP-1393 交易撤销：流程状态flowStatus 应该传流程生效 02 end
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());

        //设置头寸业务类型
        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), entity.getAssetType1());
        resultMap.put(BUSI_TYPE, busiType);

        resultMap.put(DELIVERY_TYPE, null);
        // 交易方向
        resultMap.put(PS, entity.getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put(TRADE_PLACE, entity.getTradeMarket());
        // 交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, commonPositionEngineService.getSharesTradePlatform(entity.getTradePlat(), entity.getPs(), entity.getDealMode(), entity.getEntrustProp()));
        // 流程编号	指令的工作流ID
        resultMap.put(WORK_FLOW_ID, null);
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getVdate()));
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put(CCY, entity.getCcy());
        } else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        resultMap.put(B_TOTAL_TRADE_ID, null);
    }

    /**
     * 获取组合计算信息
     *
     * @param entity 下单对象
     * @return 组合计算信息
     */
    private List<PortfolioCalculateResponse> getCalculateList(PositionInstructionRequest entity) {
        // 组装下单对象
        OrderPlaceBean orderPlaceBean = new OrderPlaceBean();
        orderPlaceBean.setEntrustPrice(entity.getEntrustPrice());
        orderPlaceBean.setEntrustVolume(entity.getEntrustNum());
        orderPlaceBean.setInstructionId(entity.getEntrustId());
        orderPlaceBean.setFullPrice(entity.getFullPrice());
        orderPlaceBean.setEntrustProp(entity.getEntrustProp());

        // 调用组合计算接口，得出各组合分配到的数量金额
        ResultBean resultBean = portfolioManager.queryPortfolioCalculate(orderPlaceBean, false);
        if (resultBean.getErrorCode() == 0) {
            return (List<PortfolioCalculateResponse>) resultBean.getData();
        }
        return Lists.newArrayList();
    }

    /**
     * 根据指令单位是张还是手来处理交易金额
     * 债券使用
     *
     * @param entrustNum 委托数量
     */
    private BigDecimal getSecAmountByBondUnit(BigDecimal entrustNum) {
        BigDecimal secAmount = BigDecimal.ZERO;
        if (Objects.isNull(entrustNum)) {
            return secAmount;
        }
        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
        if (交易单位_张.getValue().equals(bondTradedUnit)) {
            secAmount = entrustNum.multiply(BigDecimal.valueOf(100L));
        } else if (交易单位_份.getValue().equals(bondTradedUnit)) {
            secAmount = entrustNum.multiply(BigDecimal.valueOf(1000L));
        }
        return secAmount;
    }
}
