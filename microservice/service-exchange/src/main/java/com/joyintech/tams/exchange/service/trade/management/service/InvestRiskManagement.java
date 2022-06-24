package com.joyintech.tams.exchange.service.trade.management.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.exchange.common.bean.investment.*;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.InstructionPs;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.constant.joyin.BussinessDictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt1;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.OrderInterface;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.service.trade.processor.others.SplitOrderHelper;
import com.joyintech.tams.exchange.service.trade.thead.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class InvestRiskManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(InvestRiskManagement.class);

    @Autowired
    private InstructionService instructionService;
    @Autowired
    private CommonManagement commonManagement;
    @Autowired
    private ThreadPool threadPool;
    @Autowired
    private SplitOrderHelper splitOrderHelper;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public RiskOccupyBean riskCheck(Collection<EntrustInterface> entrusts) {
        return riskCheck(entrusts, null);
    }

    /**
     * 风险实占
     *
     * @param entrusts
     */
    public RiskOccupyBean riskCheck(Collection<EntrustInterface> entrusts, List<ReqRiskTrigger> parameterList) {
        for (OrderInterface order : entrusts) {
            if (EntrustProp.isSkipRisk(order.getEntrustProp())
                    || InvestmentAssetType.FUTURES.value.equals(order.getAssetType())) {//期货跳过风控
                logger.info(order.getEntrustProp() + "，跳过风控");
                RiskOccupyBean repRiskManagement = new RiskOccupyBean();
                repRiskManagement.setRskStatus("0");
                return repRiskManagement;
            }
        }
        if (EntrustUtils.isRiskApprovalSwitchOpen(commonManagement.getRiskApprovalCode())) {
            List<ReqRiskOccupy> reqList = new ArrayList<>(entrusts.size());
            entrusts.forEach(entrust -> {
                Instruction instruction = instructionService.findById(entrust.getInstructionId());
                if (Objects.nonNull(parameterList))
                    parameterList.add(createReqRiskExamine(entrust, instruction));
                reqList.add(createReqRiskOccupy(entrust, instruction));
            });
            RiskOccupyParam riskOccupyParam = new RiskOccupyParam();
            if (reqList.size() > 0) {
                riskOccupyParam.setAssetType(reqList.get(0).getAssetType());
            }
            riskOccupyParam.setEventType("1");
            riskOccupyParam.setRskList(reqList);
            logger.info("riskCheck request:{}", JSONObject.toJSONString(riskOccupyParam));
            RiskOccupyBean riskOccupyBean = null;
            if (isSingle) {
                riskOccupyBean = simpleRestTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqRiskControl, riskOccupyParam, RiskOccupyBean.class);
            } else {
                riskOccupyBean = restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqRiskControl, riskOccupyParam, RiskOccupyBean.class);
            }
            logger.info("riskCheck riskOccupyBean:{}", JSONObject.toJSONString(riskOccupyBean));
            return riskOccupyBean;
        } else {
            logger.info("风控开关关闭，跳过风控");
            RiskOccupyBean repRiskManagement = new RiskOccupyBean();
            repRiskManagement.setRskStatus("0");
            return repRiskManagement;
        }
    }

    private ReqRiskTrigger createReqRiskExamine(EntrustInterface entrust, Instruction instruction) {
        ReqRiskTrigger reqRiskExamine = new ReqRiskTrigger();
        reqRiskExamine.setEntrustCode(instruction.getId());
        String eventDate = convertDateFormat(entrust.getTradingDay());
        reqRiskExamine.setEventDate(eventDate);
        reqRiskExamine.setEventId(entrust.getId());
        reqRiskExamine.setPortfolioCode(entrust.getPortfolioId());
        reqRiskExamine.setTradePlace(instruction.getTradeMarket());

        reqRiskExamine.setAssetType(instruction.getAssetType());
        reqRiskExamine.setAssetName(entrust.getStockName());
        reqRiskExamine.setPs(instruction.getPs());
        if (DictCodeListExt1.交易方向_债转股.getValue().equals(instruction.getPs())) {
            reqRiskExamine.setConShareId(instruction.getConShareId());
            reqRiskExamine.setConPrice(instruction.getConPrice());
            reqRiskExamine.setConNum(instruction.getConNum());
        }
        if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())
                && EntrustUtils.isEntrustPropFullPrice(entrust.getEntrustProp())) {
            reqRiskExamine.setAmount(TradeUtils.calculateBalanceForRisk(entrust.getBsFlag(), entrust.getFullPrice(), entrust.getEntrustVolume(), entrust.getAssetType()));
            reqRiskExamine.setPriceAmt(entrust.getFullPrice());
        } else {
            if (InstructionPs.BOND2STOCK.value.equals(instruction.getPs())) {
                reqRiskExamine.setAmount(new BigDecimal(entrust.getEntrustVolume() * 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                reqRiskExamine.setPriceAmt(entrust.getEntrustPrice());
            } else {
                reqRiskExamine.setAmount(TradeUtils.calculateBalanceForRisk(entrust.getBsFlag(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType()));
                reqRiskExamine.setPriceAmt(entrust.getEntrustPrice());
            }
        }
        reqRiskExamine.setTradeNum(entrust.getEntrustVolume());
        if (!EntrustUtils.isRepurchase(entrust.getEntrustProp(), entrust.getAssetType())) {
            reqRiskExamine.setAssetCode(instruction.getFinprodMarketId());
        } else {
            if (InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1())) {//竞价回购
                reqRiskExamine.setAssetType2(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                reqRiskExamine.setRepurchaseType(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                //投管要求竞价回购这里平台日
                reqRiskExamine.setVdate(formatter.format(commonManagement.getPlatDate()));
                //投管要求质押式回购这里传入期限品种
                reqRiskExamine.setVarietyCode(entrust.getStockName());
            } else if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {//协议回购
                // 非首期填入成交编号
                if (!EntrustProp.isRepurchase_First(entrust.getEntrustProp())) {
                    reqRiskExamine.setFirstIssueDealNo(((IntegratedEntrust) entrust).getOrigSerialNo());
                }
                reqRiskExamine.setAssetType2(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                reqRiskExamine.setRepurchaseType(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                reqRiskExamine.setAmount(((IntegratedEntrust) entrust).getEntrustBalance());
                //投管要求协议回购这里传入起息日,到期日
                try {
                    reqRiskExamine.setVdate(formatter.format(dateFormat.parse(((IntegratedEntrust) entrust).getFirstSettleDate())));
                    reqRiskExamine.setMdate(formatter.format(dateFormat.parse(((IntegratedEntrust) entrust).getBackDate())));
                } catch (Exception e) {
                    logger.info("风险审核调用发生异常，回购日期转换错误: {}", e);
                }
            }
            reqRiskExamine.setAssetRate(entrust.getEntrustPrice());
        }
        if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())) {
            reqRiskExamine.setNetPrice(entrust.getEntrustPrice());
            reqRiskExamine.setAllPrice(entrust.getFullPrice());
        }
        reqRiskExamine.setEventType("1");
        reqRiskExamine.setTrader(instruction.getTradeId());
        reqRiskExamine.setManager(instruction.getTradeId());
        reqRiskExamine.setCreateUser(instruction.getTradeId());
        reqRiskExamine.setApproveUser(instruction.getTradeId());
        reqRiskExamine.setCcy("CNY");
        if (Objects.nonNull(instruction.getSettleSpeed())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entrust.getCreateTime());
            calendar.add(Calendar.DATE, Integer.parseInt(instruction.getSettleSpeed().toString()));
            reqRiskExamine.setDeliveryDate(formatter.format(calendar.getTime()));
        } else {
            reqRiskExamine.setDeliveryDate(reqRiskExamine.getEventDate());
        }
        return reqRiskExamine;
    }

    private ReqRiskOccupy createReqRiskOccupy(OrderInterface entrust, Instruction instruction) {
        ReqRiskOccupy reqRiskOccupy = new ReqRiskOccupy();
        reqRiskOccupy.setEntrustCode(instruction.getId());
        String eventDate = convertDateFormat(entrust.getTradingDay());
        reqRiskOccupy.setEventDate(eventDate);
        reqRiskOccupy.setEventId(entrust.getId());
        reqRiskOccupy.setPortfolioCode(entrust.getPortfolioId());
        reqRiskOccupy.setPs(instruction.getPs());
        if (DictCodeListExt1.交易方向_债转股.getValue().equals(instruction.getPs())) {
            reqRiskOccupy.setConShareId(instruction.getConShareId());
            reqRiskOccupy.setConPrice(instruction.getConPrice());
            reqRiskOccupy.setConNum(instruction.getConNum());
        }

        reqRiskOccupy.setAssetType(instruction.getAssetType());
        if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())
                && EntrustUtils.isEntrustPropFullPrice(entrust.getEntrustProp())) {
            reqRiskOccupy.setAmount(TradeUtils.calculateBalanceForRisk(entrust.getBsFlag(), entrust.getFullPrice(), entrust.getEntrustVolume(), entrust.getAssetType()));
            reqRiskOccupy.setPriceAmt(entrust.getFullPrice());
        } else {
            if (InstructionPs.BOND2STOCK.value.equals(instruction.getPs())) {
                reqRiskOccupy.setAmount(new BigDecimal(entrust.getEntrustVolume() * 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                reqRiskOccupy.setPriceAmt(entrust.getEntrustPrice());
            } else {
                reqRiskOccupy.setAmount(TradeUtils.calculateBalanceForRisk(entrust.getBsFlag(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType()));
                reqRiskOccupy.setPriceAmt(entrust.getEntrustPrice());
            }
        }
        reqRiskOccupy.setTradeNum(entrust.getEntrustVolume());
        if (!EntrustUtils.isRepurchase(entrust.getEntrustProp(), entrust.getAssetType())) {
            reqRiskOccupy.setAssetCode(instruction.getFinprodMarketId());
        } else {
            try {
                if (InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1())) {//竞价回购
                    reqRiskOccupy.setAssetType2(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                    reqRiskOccupy.setRepurchaseType(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                    //投管要求竞价回购这里平台日
                    reqRiskOccupy.setVdate(formatter.format(commonManagement.getPlatDate()));
                    //投管要求质押式回购这里传入期限品种
                    reqRiskOccupy.setVarietyCode(entrust.getStockName());
                } else if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {//协议回购
                    // 非首期填入成交编号
                    if (!EntrustProp.isRepurchase_First(entrust.getEntrustProp())) {
                        reqRiskOccupy.setFirstIssueDealNo(((IntegratedEntrust) entrust).getOrigSerialNo());
                    }
                    reqRiskOccupy.setAssetType2(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                    reqRiskOccupy.setRepurchaseType(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                    reqRiskOccupy.setAmount(((IntegratedEntrust) entrust).getEntrustBalance());
                    //要求填入抵押券代码
                    List<String> pledgeAssetCodeList = new ArrayList<>();
                    pledgeAssetCodeList.add(entrust.getStockCode());
                    reqRiskOccupy.setPledgeAssetCodeList(pledgeAssetCodeList);

                    //投管要求协议回购这里传入起息日,到期日
                    reqRiskOccupy.setVdate(formatter.format(dateFormat.parse(((IntegratedEntrust) entrust).getFirstSettleDate())));
                    reqRiskOccupy.setMdate(formatter.format(dateFormat.parse(((IntegratedEntrust) entrust).getBackDate())));
                }
            } catch (Exception e) {
                logger.info("风险三合一调用发生异常，回购日期转换错误: {}", e);
            }
            reqRiskOccupy.setAssetRate(entrust.getEntrustPrice());
        }
        if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())) {
            reqRiskOccupy.setNetPrice(entrust.getEntrustPrice());
            reqRiskOccupy.setAllPrice(entrust.getFullPrice());
        }
        reqRiskOccupy.setEventType("1");
        reqRiskOccupy.setIsWarnNeedConfirm("Y");
        reqRiskOccupy.setIsApprNeedConfirm("Y");
        reqRiskOccupy.setCcy("CNY");
        String counterId = instruction.getCounterId();
        if (Strings.isNullOrEmpty(counterId) && InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
            counterId = ((IntegratedEntrust) entrust).getLiaisonName();
        }
        reqRiskOccupy.setCounterId(counterId);
        reqRiskOccupy.setTradeId(entrust.getId());
        return reqRiskOccupy;
    }

    //   yyyyMMdd转yyyy-MM-dd
    private String convertDateFormat(String date) {
        try {
            Date format1 = new SimpleDateFormat("yyyyMMdd").parse(date);
            String dateAfterConvert = new SimpleDateFormat("yyyy-MM-dd").format(format1);
            return dateAfterConvert;
        } catch (ParseException e) {
            logger.error("convertDateFormat error: {}", e.getMessage());
            return null;
        }
    }


    public void riskRelease(EntrustInterface entrust) {
        List<OrderInterface> orderInterfaces = new ArrayList<>();
        orderInterfaces.add(entrust);
        riskRelease(orderInterfaces, instructionService.findById(entrust.getInstructionId()));
    }

    /**
     * 风险实占释放
     *
     * @param instruction
     * @param orders
     * @param instruction
     */
    public void riskRelease(Collection<OrderInterface> orders, Instruction instruction) {
        for (OrderInterface order : orders) {
            if (EntrustProp.isSkipRisk(order.getEntrustProp())) {
                logger.info(order.getEntrustProp() + "，跳过风控释放");
                return;
            }
        }
        if (EntrustUtils.isRiskApprovalSwitchOpen(commonManagement.getRiskApprovalCode())) {
            List<ReqRiskOccupy> reqList = new ArrayList<>(orders.size());
            orders.forEach(order -> {
                ReqRiskOccupy reqRiskOccupy = new ReqRiskOccupy();
                reqRiskOccupy.setEntrustCode(instruction.getId());
                String eventDate = convertDateFormat(order.getTradingDay());
                reqRiskOccupy.setEventDate(eventDate);
                String orderId = order.getId();
                if (EntrustUtils.isSplitOrder(order.getEntrustProp(), order.getRemark())) {
                    logger.info("释放拆单订单风控：委托编号(entrustId): {}, 原委托编号信息(remark): {}", order.getId(), order.getRemark());
                    orderId = EntrustUtils.convertRemarkToEntrustId(order.getRemark());
                    reqRiskOccupy.setEventId(orderId);
                    reqRiskOccupy.setTradeId(orderId);
                    reqRiskOccupy.setSeqNo(orderId);
                    splitOrderHelper.handleRiskReleaseBySplitOrder(order, reqRiskOccupy);
                } else {
                    reqRiskOccupy.setTradeId(orderId);
                    reqRiskOccupy.setSeqNo(orderId);
                    reqRiskOccupy.setEventId(orderId);
                    if (order.getCancelVolume() == order.getEntrustVolume()) {
                        reqRiskOccupy.setRevokeFlg("Y");
                    } else {
                        reqRiskOccupy.setRevokeFlg("N");
                    }
                }
                reqRiskOccupy.setPortfolioCode(order.getPortfolioId());
                if (!EntrustUtils.isRepurchase(order.getEntrustProp(), order.getAssetType())) {
                    reqRiskOccupy.setAssetCode(order.getStockCode());
                } else {
                    if (InvestmentAssetType1.BIDDING_REPO.value.equals(instruction.getAssetType1())) {//竞价回购
                        reqRiskOccupy.setAssetType2(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                        reqRiskOccupy.setRepurchaseType(BussinessDictCodeList.回购类型_质押式竞价回购.getValue());
                        //投管要求竞价回购这里平台日
                        reqRiskOccupy.setVdate(formatter.format(commonManagement.getPlatDate()));
                        //投管要求质押式回购这里传入期限品种
                        reqRiskOccupy.setVarietyCode(order.getStockName());
                    } else if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {//协议回购
                        // 非首期填入成交编号
                        if (!EntrustProp.isRepurchase_First(order.getEntrustProp())) {
                            reqRiskOccupy.setFirstIssueDealNo(((IntegratedEntrust) order).getOrigSerialNo());
                        }
                        reqRiskOccupy.setAssetType2(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                        reqRiskOccupy.setRepurchaseType(BussinessDictCodeList.回购类型_质押式协议回购.getValue());
                        //要求填入抵押券代码
                        List<String> pledgeAssetCodeList = new ArrayList<>();
                        pledgeAssetCodeList.add(order.getStockCode());
                        reqRiskOccupy.setPledgeAssetCodeList(pledgeAssetCodeList);
                        //投管要求协议回购这里传入起息日,到期日
                        try {
                            reqRiskOccupy.setVdate(formatter.format(dateFormat.parse(((IntegratedEntrust) order).getFirstSettleDate())));
                            reqRiskOccupy.setMdate(formatter.format(dateFormat.parse(((IntegratedEntrust) order).getBackDate())));
                        } catch (Exception e) {
                            logger.info("风险释放调用发生异常，回购日期转换错误: {}", e);
                        }
                    }
                }
                reqRiskOccupy.setAssetType(instruction.getAssetType());
                reqRiskOccupy.setPs(instruction.getPs());
                if (DictCodeListExt1.交易方向_债转股.getValue().equals(instruction.getPs())) {
                    reqRiskOccupy.setConShareId(instruction.getConShareId());
                    reqRiskOccupy.setConPrice(instruction.getConPrice());
                    reqRiskOccupy.setConNum(instruction.getConNum());
                }
                if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())
                        && EntrustUtils.isEntrustPropFullPrice(order.getEntrustProp())) {
                    reqRiskOccupy.setAmount(TradeUtils.calculateBalanceForRisk(order.getBsFlag(), order.getFullPrice(), order.getCancelVolume(), order.getAssetType()));
                    reqRiskOccupy.setPriceAmt(order.getFullPrice());
                } else {
                    if (InstructionPs.BOND2STOCK.value.equals(instruction.getPs())) {
                        reqRiskOccupy.setAmount(new BigDecimal(order.getCancelVolume() * 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                    } else if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                        reqRiskOccupy.setAmount(((IntegratedEntrust) order).getEntrustBalance());
                    } else {
                        reqRiskOccupy.setAmount(TradeUtils.calculateBalanceForRisk(order.getBsFlag(), order.getEntrustPrice(), order.getCancelVolume(), order.getAssetType()));
                    }
                    reqRiskOccupy.setPriceAmt(order.getEntrustPrice());
                }
                reqRiskOccupy.setTradeNum(order.getCancelVolume());
                if (InvestmentAssetType.BOND.value.equals(instruction.getAssetType())) {
                    reqRiskOccupy.setNetPrice(order.getEntrustPrice());
                    reqRiskOccupy.setAllPrice(order.getFullPrice());
                }
                reqRiskOccupy.setEventType("1");
                reqRiskOccupy.setCcy("CNY");
                String counterId = instruction.getCounterId();
                if (Strings.isNullOrEmpty(counterId) && InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                    counterId = ((IntegratedEntrust) order).getLiaisonName();
                }
                reqRiskOccupy.setCounterId(counterId);
                reqList.add(reqRiskOccupy);
            });
            RiskOccupyParam riskOccupyParam = new RiskOccupyParam();
            riskOccupyParam.setAssetType(reqList.get(0).getAssetType());
            riskOccupyParam.setEventType("1");
            riskOccupyParam.setRskList(reqList);
            logger.info("请求释放风控，报文: {}", JSONObject.toJSONString(riskOccupyParam));
            RiskOccupyBean riskOccupyBean = null;
            if (isSingle) {
                riskOccupyBean = simpleRestTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqRiskControlRelease, riskOccupyParam, RiskOccupyBean.class);
            } else {
                riskOccupyBean = restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqRiskControlRelease, riskOccupyParam, RiskOccupyBean.class);
            }
            logger.info("风控释放结果报文:{}", JSONObject.toJSONString(riskOccupyBean));
        } else {
            logger.info("风控开关关闭，跳过风控");
        }
    }

    /**
     * 风险实占释放异步方法
     *
     * @param instruction
     * @param orders
     * @param instruction
     */
    public void riskControlRelease(Collection<OrderInterface> orders, Instruction instruction) {
        for (OrderInterface order : orders) {
            if (EntrustProp.ZYCRK.value.equals(order.getEntrustProp())) {
                logger.info("质押券出入库, skip riskControlRelease.");
                return;
            }
        }
        threadPool.execute(() -> riskRelease(orders, instruction));
    }


    /**
     * 风控审批
     *
     * @param orders
     * @param instruction
     */
    public String triggerRiskExamine(Collection<EntrustInterface> orders, Instruction instruction) {
        List<ReqRiskTrigger> reqList = new ArrayList<ReqRiskTrigger>(orders.size());
        orders.forEach(order -> reqList.add(createReqRiskExamine(order, instruction)));
        return triggerRiskExamine(null, reqList);
    }

    public String triggerRiskExamine(String orderId, List<ReqRiskTrigger> reqList) {
        ReqRiskTriggerBean reqRiskTriggerBean = new ReqRiskTriggerBean();
        if (!Strings.isNullOrEmpty(orderId)) {//公平委托
            reqRiskTriggerBean.setOrderID(orderId);
        } else {
            reqRiskTriggerBean.setOrderID(reqList.get(0).getEventId());
        }
        reqRiskTriggerBean.setRskList(reqList);
        if (EntrustUtils.isRiskApprovalSwitchOpen(commonManagement.getRiskApprovalCode())) {
            logger.info("triggerRisk reqRiskTriggerBean:{}", JSONObject.toJSONString(reqRiskTriggerBean));
            String result = null;
            if (isSingle) {
                result = simpleRestTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqTriggerRisk, reqRiskTriggerBean, String.class);
            } else {
                result = restTemplate.postForObject(ServiceApplication.CUSTOMER_INTERFACE_NAME + TradeServiceURL.reqTriggerRisk, reqRiskTriggerBean, String.class);
            }
            logger.info("triggerRisk result:{}", result);
            return result;
        } else {
            logger.info("风控开关关闭，跳过风控");
            return "1";
        }
    }

}
