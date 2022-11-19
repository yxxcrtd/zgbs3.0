package com.joyintech.tams.settle.batch.datagather.processor.bond;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 债券一级投标
 *
 * @author fuyongsheng
 * @date 2022年05月12日
 * @see StlDataGatherBond8Processor
 * @since 1.0
 */
@Service
@Lazy
public class StlDataGatherBond8Processor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {

    @Autowired
    private StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private SettleCommonService settleCommonService;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Override
    public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {
        InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();
        InvestTradeReaderDto item = (InvestTradeReaderDto) operateEntity;
        // 根据InvestTradeReaderDto中的成交数据明细数据列表，构建一个InvestTradeWriteDto
        List<SettleTransDataTotal> settleTransDataTotalDtos = item.getSettleTransDataTotalList();
        if (CollectionUtils.isNotEmpty(settleTransDataTotalDtos)) {
            // 组合交易
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            // 金融产品交易明细
            List<TrdProductDealDto> productDealList = new ArrayList<>();
            // 投管交易关系
            List<SettleTransTgMapping> settleTransTgMappingList = new ArrayList<>();
            // 金融产品交易汇总表
            List<TotalTrdProductDealDto> totalProductDealList = new ArrayList<>();
            // 金融产品汇总附属交易
            List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
            // TrdBankManualSettleInfo
            List<TrdBankManualSettleInfo> trdBankManualSettleInfoList = new ArrayList<>();
            // TrdBankManualSettleFlow
            List<TrdBankManualSettleFlow> trdBankManualSettleFlowList = new ArrayList<>();
            // 金融产品交易费用明细
            List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();
            // 划款指令明细
            List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
            // 循环成交明细汇总
            for (SettleTransDataTotal settleTransDataTotal : settleTransDataTotalDtos) {
                // 查询投管交易编号(汇总交易编号)
                Map<String, Object> paraMap = new HashMap<>(4);
                paraMap.put("applyTradeId", settleTransDataTotal.getApplyTradeId());
                paraMap.put("tradeDate", settleTransDataTotal.getTradeDate());
                paraMap.put("secAcctId", settleTransDataTotal.getSecAcctId());
                paraMap.put("seatNumber", settleTransDataTotal.getSeatNumber());
//                List<String> tradeIdTgList = stlDataGatherMapper.selectTradeIdTgs(paraMap);
                // 根据汇总交易编号查询所有通知的汇总交易
                List<TrdFinProductDealTotal> notifyDealTotalList = stlDataGatherMapper.selectTotalTradesBytotleTradeId(paraMap);
                // 根据通知的汇总交易生成结果的汇总交易
                Map<String, TotalTrdProductDealDto> resultDealTotalMap = this.createResultTrdFinProductDealTotalMap(settleTransDataTotal, notifyDealTotalList);
                // 根据汇总的成交明细查询出原成交明细列表
                paraMap.put("dealDate", settleTransDataTotal.getDealDate());
                List<SettleTransDataDto> stdDetailList = stlDataGatherMapper.selectSettleTransDatas(paraMap);

                // 循环生成的结果汇总交易
                for (TrdFinProductDealTotal notifyDealTotal : notifyDealTotalList) {
                    // 根据汇总交易编号查询所有交易
                    List<TrdProductDeal> notifyTrdProductDealList = stlDataGatherMapper.selectTradesByTotleTradeId(notifyDealTotal.getTotleTradeId());
                    if(CollectionUtils.isEmpty(notifyTrdProductDealList)){
                        continue;
                    }
                    // 金融产品汇总交易DTO
                    TotalTrdProductDealDto resultDealTotal = resultDealTotalMap.get(notifyDealTotal.getTotleTradeId());
                    // 循环原成交明细列表
                    for (SettleTransDataDto settleTransDataDetail : stdDetailList) {
                        // 投管交易关系
                        settleTransTgMappingList.add(tgTradeInfo(settleTransDataDetail, resultDealTotal));
                    }
                    // 构造金融交易汇总表数据
                    totalProductDealList.add(resultDealTotal);
                    // 按份额从小到大排序
                    List<TrdProductDeal> sortedNotifyDealList = notifyTrdProductDealList.stream()
                            .sorted(Comparator.comparing(TrdProductDeal::getShareAmt))
                            .collect(Collectors.toList());
                    // 处理尾插用
                    Map<String, BigDecimal> amtInfoMap = new HashMap<>();
                    BigDecimal shareAmtTotal = sortedNotifyDealList.stream().map(TrdProductDeal::getShareAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    for(int i = 0; i < sortedNotifyDealList.size(); i++){
                        // 最后一条数据
                        Boolean lastItem = i == sortedNotifyDealList.size() - 1;
                        TrdProductDeal notifyTrdProductDeal = sortedNotifyDealList.get(0);
                        // 生成金融产品交易表
                        TrdProductDealDto resultTrdProductDeal = createTrdProductDeal(notifyTrdProductDeal, resultDealTotal, amtInfoMap, lastItem, shareAmtTotal);
                        productDealList.add(resultTrdProductDeal);
                        // 生成金融产品交易附属表
                        TrdProductDealAdd trdProductDealAdd = this.createTrdProductDealAdd(resultTrdProductDeal, notifyTrdProductDeal);
                        trdProductDealAddList.add(trdProductDealAdd);
                        // 金融产品交易费用明细
                        trdTradeFeeDetailList.addAll(genTrdTradeFeeDetailList(settleTransDataTotal, resultTrdProductDeal, resultDealTotal, amtInfoMap, lastItem));
                        // 生成划款指令
                        stlPayOrdersList.add(generateStlPayOrdersList(settleTransDataTotal, resultTrdProductDeal));
                        // 组合交易
                        PtlTrade ptlTrade = new PtlTrade();
                        ptlTrade.setPortfolioId(trdProductDealAdd.getPortfolioId());
                        ptlTradeList.add(createPtlTrade(ptlTrade, resultTrdProductDeal));
                    }
                    // TrdBankManualSettleInfo
                    trdBankManualSettleInfoList.add(trdBankManualSettleInfo(settleTransDataTotal, resultDealTotal));
                    // TrdBankManualSettleFlow
                    trdBankManualSettleFlowList.add(trdBankManualSettleFlow(resultDealTotal));
                }
                // 组合交易
                investTradeWriteDto.setPtlTradeList(ptlTradeList);
                // 金融产品交易明细
                investTradeWriteDto.setProductDealList(productDealList);
                // 金融产品交易汇总表
                investTradeWriteDto.setFinProductDealTotalList(totalProductDealList);
                // 投管交易关系
                investTradeWriteDto.setSettleTransTgMappingList(settleTransTgMappingList);
                // 金融产品汇总附属交易
                investTradeWriteDto.setTrdProductDealAddList(trdProductDealAddList);
                // 金融产品交易费用明细
                investTradeWriteDto.setTradeFeeDetailList(trdTradeFeeDetailList);
                // 划款指令明细
                investTradeWriteDto.setStlPayOrdersList(stlPayOrdersList);
                investTradeWriteDto.setSecType(DictCodeListExt1.证券品种_债券.getValue());
                // trdBankManualSettleInfoList
                investTradeWriteDto.setTrdBankManualSettleInfoList(trdBankManualSettleInfoList);
                // trdBankManualSettleFlowList
                investTradeWriteDto.setTrdBankManualSettleFlowList(trdBankManualSettleFlowList);
            }
        }
        return investTradeWriteDto;
    }

    /**
     * 根据通知的汇总交易生成结果的汇总交易
     * @param settleTransDataTotal
     * @param notifyTrdFinProductDealTotalList
     * @return
     */
    private Map<String, TotalTrdProductDealDto> createResultTrdFinProductDealTotalMap(SettleTransDataTotal settleTransDataTotal,
                                List<TrdFinProductDealTotal> notifyTrdFinProductDealTotalList){
        Map<String, TotalTrdProductDealDto> resultDealTotalMap = new HashMap<>();
        if(CollectionUtils.isEmpty(notifyTrdFinProductDealTotalList)){
            return resultDealTotalMap;
        }
        // 按份额从小到大排序
        List<TrdFinProductDealTotal> sortedNotifyDealTotalList = notifyTrdFinProductDealTotalList
                .stream()
                .sorted(Comparator.comparing(TrdFinProductDealTotal::getShareAmt))
                .collect(Collectors.toList());
        Map<String, BigDecimal> amtInfoMap = new HashMap<>();
        // 汇总交易的交易份额
        BigDecimal notifyShareAmtTotal = notifyTrdFinProductDealTotalList.stream()
                .map(TrdFinProductDealTotal::getShareAmt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for(int i=0; i<sortedNotifyDealTotalList.size(); i++){
            TrdFinProductDealTotal notifyDealTotal = sortedNotifyDealTotalList.get(i);
            TotalTrdProductDealDto resultDealTotal = new TotalTrdProductDealDto();
            BeanUtil.copyPropertiesIgnoreNull(resultDealTotal, notifyDealTotal);
            // 汇总交易编号
            resultDealTotal.setTotleTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
            // 前置交易编号
            resultDealTotal.setbTradeId(notifyDealTotal.getTotleTradeId());
            resultDealTotal.setSettleDate(settleTransDataTotal.getSettleDate());
            // 尾差分配在最后一个(份额最大)上
            if (i == sortedNotifyDealTotalList.size()-1) {
                // 交易份额
                resultDealTotal.setShareAmt(ArithmeticUtil.subtract(settleTransDataTotal.getShareAmt(), amtInfoMap.get("shareAmt")));
                // 净价总额
                resultDealTotal.setCpriceAmt(ArithmeticUtil.subtract(settleTransDataTotal.getCpriceAmt(), amtInfoMap.get("cpriceAmt")));
                // 交易本金
                resultDealTotal.setPrinAmt(ArithmeticUtil.subtract(settleTransDataTotal.getPrinAmt(), amtInfoMap.get("prinAmt")));
                // 全价总额
                resultDealTotal.setFpriceAmt(ArithmeticUtil.subtract(settleTransDataTotal.getPrinAmt(), amtInfoMap.get("fpriceAmt")));
                // 交易金额
                resultDealTotal.setTradeAmt(ArithmeticUtil.subtract(settleTransDataTotal.getPrinAmt(), amtInfoMap.get("tradeAmt")));
                // 过户费
                resultDealTotal.setGhfFee(ArithmeticUtil.subtract(settleTransDataTotal.getGhfAmt(), amtInfoMap.get("ghfAmt")));
                // 证管费
                resultDealTotal.setZgfFee(ArithmeticUtil.subtract(settleTransDataTotal.getZgfAmt(), amtInfoMap.get("zgfAmt")));
                // 印花税
                resultDealTotal.setStampFee(ArithmeticUtil.subtract(settleTransDataTotal.getStampFee(), amtInfoMap.get("stampAmt")));
                // 经手费
                resultDealTotal.setJsfFee(ArithmeticUtil.subtract(settleTransDataTotal.getJsfAmt(), amtInfoMap.get("jsfAmt")));
                // 手续费
                resultDealTotal.setSxfFee(ArithmeticUtil.subtract(settleTransDataTotal.getSxfAmt(), amtInfoMap.get("sxfAmt")));
                // 结算费
                resultDealTotal.setSettleFee(ArithmeticUtil.subtract(settleTransDataTotal.getSettleFee(), amtInfoMap.get("settleAmt")));
                // 佣金
                resultDealTotal.setCommissionAmt(ArithmeticUtil.subtract(settleTransDataTotal.getCommissionAmt(), amtInfoMap.get("commissionAmt")));
            } else {
                // 交易份额
                resultDealTotal.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataTotal.getShareAmt(), notifyDealTotal.getShareAmt()), notifyShareAmtTotal, 2));
                amtInfoMap.put("shareAmt", ArithmeticUtil.doAdd(amtInfoMap.get("shareAmt"), resultDealTotal.getShareAmt()));
                // 净价总额
                resultDealTotal.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataTotal.getCpriceAmt(), notifyDealTotal.getShareAmt()), notifyShareAmtTotal, 2));
                amtInfoMap.put("cpriceAmt", ArithmeticUtil.doAdd(amtInfoMap.get("cpriceAmt"), resultDealTotal.getCpriceAmt()));
                // 交易本金
                resultDealTotal.setPrinAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataTotal.getPrinAmt(), notifyDealTotal.getShareAmt()), notifyShareAmtTotal, 2));
                amtInfoMap.put("prinAmt", ArithmeticUtil.doAdd(amtInfoMap.get("prinAmt"), resultDealTotal.getPrinAmt()));
                // 全价总额
                resultDealTotal.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataTotal.getPrinAmt(), notifyDealTotal.getShareAmt()), notifyShareAmtTotal, 2));
                amtInfoMap.put("fpriceAmt", ArithmeticUtil.doAdd(amtInfoMap.get("fpriceAmt"), resultDealTotal.getFpriceAmt()));
                // 交易金额
                resultDealTotal.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataTotal.getPrinAmt(), notifyDealTotal.getShareAmt()), notifyShareAmtTotal, 2));
                amtInfoMap.put("tradeAmt", ArithmeticUtil.doAdd(amtInfoMap.get("tradeAmt"), resultDealTotal.getTradeAmt()));
                // 过户费
                resultDealTotal.setGhfFee(ArithmeticUtil.subtract(settleTransDataTotal.getGhfAmt(), amtInfoMap.get("ghfAmt")));
                amtInfoMap.put("ghfAmt", ArithmeticUtil.doAdd(amtInfoMap.get("ghfAmt"), resultDealTotal.getGhfFee()));
                // 证管费
                resultDealTotal.setZgfFee(ArithmeticUtil.subtract(settleTransDataTotal.getZgfAmt(), amtInfoMap.get("zgfAmt")));
                amtInfoMap.put("zgfAmt", ArithmeticUtil.doAdd(amtInfoMap.get("zgfAmt"), resultDealTotal.getZgfFee()));
                // 印花税
                resultDealTotal.setStampFee(ArithmeticUtil.subtract(settleTransDataTotal.getStampFee(), amtInfoMap.get("stampAmt")));
                amtInfoMap.put("stampAmt", ArithmeticUtil.doAdd(amtInfoMap.get("stampAmt"), resultDealTotal.getStampFee()));
                // 经手费
                resultDealTotal.setJsfFee(ArithmeticUtil.subtract(settleTransDataTotal.getJsfAmt(), amtInfoMap.get("jsfAmt")));
                amtInfoMap.put("jsfAmt", ArithmeticUtil.doAdd(amtInfoMap.get("jsfAmt"), resultDealTotal.getJsfFee()));
                // 手续费
                resultDealTotal.setSxfFee(ArithmeticUtil.subtract(settleTransDataTotal.getSxfAmt(), amtInfoMap.get("sxfAmt")));
                amtInfoMap.put("sxfAmt", ArithmeticUtil.doAdd(amtInfoMap.get("sxfAmt"), resultDealTotal.getSxfFee()));
                // 结算费
                resultDealTotal.setSettleFee(ArithmeticUtil.subtract(settleTransDataTotal.getSettleFee(), amtInfoMap.get("settleAmt")));
                amtInfoMap.put("settleAmt", ArithmeticUtil.doAdd(amtInfoMap.get("settleAmt"), resultDealTotal.getSettleFee()));
                // 佣金
                resultDealTotal.setCommissionAmt(ArithmeticUtil.subtract(settleTransDataTotal.getCommissionAmt(), amtInfoMap.get("commissionAmt")));
                amtInfoMap.put("commissionAmt", ArithmeticUtil.doAdd(amtInfoMap.get("commissionAmt"), resultDealTotal.getCommissionAmt()));
            }
            resultDealTotalMap.put(notifyDealTotal.getTotleTradeId(), resultDealTotal);
        }
        return resultDealTotalMap;
    }

    /**
     * 生成金融产品交易附属表
     */
    private TrdProductDealAdd createTrdProductDealAdd(TrdProductDealDto trdProductDeal, TrdProductDeal notifyTrdProductDeal) {
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        TrdProductDealAdd notifyDealAdd = trdProductDealAddMapper.selectByPrimaryKey(notifyTrdProductDeal.getTradeId());
        // 交易编号
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        // 投资组合代码
        trdProductDealAdd.setPortfolioId(notifyDealAdd.getPortfolioId());
        // 生成代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
        return trdProductDealAdd;
    }

    /**
     * 处理尾插用
     */
    private void amtInfoDealMap(Map<String, BigDecimal> amtInfoMap) {
        // 交易份额
        amtInfoMap.put("shareAmt", BigDecimal.ZERO);
        // 净价总额
        amtInfoMap.put("cpriceAmt", BigDecimal.ZERO);
        // 交易本金
        amtInfoMap.put("prinAmt", BigDecimal.ZERO);
        // 全价总额
        amtInfoMap.put("fpriceAmt", BigDecimal.ZERO);
        // 交易金额
        amtInfoMap.put("tradeAmt", BigDecimal.ZERO);

        amtInfoMap.put("stampFee", BigDecimal.ZERO);
        amtInfoMap.put("jsfFee", BigDecimal.ZERO);
        amtInfoMap.put("ghfFee", BigDecimal.ZERO);
        amtInfoMap.put("zgfFee", BigDecimal.ZERO);
        amtInfoMap.put("sxfFee", BigDecimal.ZERO);
        amtInfoMap.put("settleFee", BigDecimal.ZERO);
        amtInfoMap.put("commissionAmt", BigDecimal.ZERO);
    }

    /**
     * TrdBankManualSettleFlow
     */
    private TrdBankManualSettleFlow trdBankManualSettleFlow(TotalTrdProductDealDto totalTrdProductDeal) {
        TrdBankManualSettleFlow trdBankManualSettleFlow = new TrdBankManualSettleFlow();
        // 交收流水号
        trdBankManualSettleFlow.setSeqNo(IDGenerator.generate());//自动生成
        // 交收编号
        trdBankManualSettleFlow.setSettleSeqNo(totalTrdProductDeal.getTotleTradeId());
        // 交收时间
        trdBankManualSettleFlow.setSettleTime(totalTrdProductDeal.getTradeDate());
        // 金融产品代码
        trdBankManualSettleFlow.setFinprodId(totalTrdProductDeal.getFinprodId());
        // 交割数量
        trdBankManualSettleFlow.setSettleNum(totalTrdProductDeal.getShareAmt());
        // 交割金额
        trdBankManualSettleFlow.setSettleAmt(totalTrdProductDeal.getFpriceAmt());
        return trdBankManualSettleFlow;
    }

    /**
     * TrdBankManualSettleInfo
     */
    private TrdBankManualSettleInfo trdBankManualSettleInfo(SettleTransDataTotal settleTransDataTotal, TotalTrdProductDealDto totalTrdProductDeal) {
        TrdBankManualSettleInfo trdBankManualSettleInfo = new TrdBankManualSettleInfo();
        // 交收编号
        trdBankManualSettleInfo.setSettleSeqNo(totalTrdProductDeal.getTotleTradeId());
        // 交收状态
        trdBankManualSettleInfo.setSettleStatus(settleTransDataTotal.getDeliveryResult());
        // 原业务编号
        trdBankManualSettleInfo.setTradeId(totalTrdProductDeal.getbTradeId());
        // 交收日期
        trdBankManualSettleInfo.setSettleDate(totalTrdProductDeal.getTradeDate());
        // 金融产品代码
        trdBankManualSettleInfo.setFinprodId(totalTrdProductDeal.getFinprodId());
        // 交割数量
        trdBankManualSettleInfo.setSettleNum(totalTrdProductDeal.getShareAmt());
        // 交割金额
        trdBankManualSettleInfo.setSettleAmt(totalTrdProductDeal.getFpriceAmt());
        // 业务类别
        trdBankManualSettleInfo.setBusiType("TRD002");
        return trdBankManualSettleInfo;
    }

    /**
     * 投管交易关系
     */
    private SettleTransTgMapping tgTradeInfo(SettleTransDataDto settleTransDataDetail, TotalTrdProductDealDto totalTrdProductDeal) {
        SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
        // 成交编号-流水号
        settleTransTgMapping.setTradeId(settleTransDataDetail.getTradeId());
        // 投管交易编号
        settleTransTgMapping.setTradeIdTg(totalTrdProductDeal.getTotleTradeId());
        // 投管交易数据状态
        settleTransTgMapping.setTgDataStatus(DictCodeList.处理状态_已处理.getValue());
        //  执行日期
        settleTransTgMapping.setDealDate(settleTransDataDetail.getDealDate());
        return settleTransTgMapping;
    }

    /**
     * 组合交易
     */
    private PtlTrade createPtlTrade(PtlTrade ptlTrade, TrdProductDealDto trdProductDealDto) {
        // 业务编号
        ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
        // 业务类型
        ptlTrade.setBusiType(trdProductDealDto.getBusiType());
        // 投资目的
        ptlTrade.setInvAim(trdProductDealDto.getInvAim());
        // 证券托管户代码
        ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
        // 实际发生日
        ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
        return ptlTrade;
    }

    /**
     * 划款指令明细list
     */
    public StlPayOrders generateStlPayOrdersList(SettleTransDataTotal settleTransDataTotal, TrdProductDeal trdProductDeal) {
        return createStlPayOrders(settleTransDataTotal, trdProductDeal);
    }

    /**
     * 根据交易明细创建指令
     *
     * @param settleTransDataTotal
     * @param trdProductDeal
     * @return
     */
    private StlPayOrders createStlPayOrders(SettleTransDataTotal settleTransDataTotal, TrdProductDeal trdProductDeal) {
        // 指令
        StlPayOrders stlPayOrders = new StlPayOrders();
        // 指令编号
        stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
        // 结算日期
        stlPayOrders.setSettleDate(trdProductDeal.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(settleTransDataTotal.getAcctNo());
        // 结算金额
        stlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(trdProductDeal.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(trdProductDeal.getTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(trdProductDeal.getPortfolioId());
        // 理财产品代码
        stlPayOrders.setProdId(trdProductDeal.getFinprodId());
        // 备注
        stlPayOrders.setRemark("匹配交收处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        if (StringUtil.equals(settleTransDataTotal.getPs(), DictCodeList.交易方向_买入.getValue())) {
            stlPayOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
        } else {
            stlPayOrders.setPs(DictCodeList.收付方向_收入.getValue());
        }
        return stlPayOrders;
    }

    /**
     * 金融产品交易费用明细list
     * @param settleTransDataTotal
     * @param resultDeal
     * @param resultDealTotal
     * @param amtInfoMap
     * @param lastItem
     * @return
     */
    public List<TrdTradeFeeDetail> genTrdTradeFeeDetailList(SettleTransDataTotal settleTransDataTotal, TrdProductDeal resultDeal,
                                                            TotalTrdProductDealDto resultDealTotal, Map<String, BigDecimal> amtInfoMap, Boolean lastItem) {
        BigDecimal shareAmtTotal = resultDealTotal.getShareAmt();
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();
        TrdTradeFeeDetail stampFee = new TrdTradeFeeDetail();
        stampFee.setTradeId(resultDeal.getTradeId());
        stampFee.setFeeType(DictCodeListExt1.一次性费用类型_印花税.getValue());
        stampFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        stampFee.setRegistOrg(resultDeal.getRegistOrg());
        TrdTradeFeeDetail jsfFee = new TrdTradeFeeDetail();
        jsfFee.setTradeId(resultDeal.getTradeId());
        jsfFee.setFeeType(DictCodeListExt1.一次性费用类型_经手费.getValue());
        jsfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        jsfFee.setRegistOrg(resultDeal.getRegistOrg());

        TrdTradeFeeDetail ghfFee = new TrdTradeFeeDetail();
        ghfFee.setTradeId(resultDeal.getTradeId());
        ghfFee.setFeeType(DictCodeListExt1.一次性费用类型_过户费.getValue());
        ghfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        ghfFee.setRegistOrg(resultDeal.getRegistOrg());

        TrdTradeFeeDetail zgfFee = new TrdTradeFeeDetail();
        zgfFee.setTradeId(resultDeal.getTradeId());
        zgfFee.setFeeType(DictCodeListExt1.一次性费用类型_证管费.getValue());
        zgfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        zgfFee.setRegistOrg(resultDeal.getRegistOrg());

        TrdTradeFeeDetail sxfFee = new TrdTradeFeeDetail();
        sxfFee.setTradeId(resultDeal.getTradeId());
        sxfFee.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
        sxfFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        sxfFee.setRegistOrg(resultDeal.getRegistOrg());

        TrdTradeFeeDetail settleFee = new TrdTradeFeeDetail();
        settleFee.setTradeId(resultDeal.getTradeId());
        settleFee.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
        settleFee.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        settleFee.setRegistOrg(resultDeal.getRegistOrg());

        TrdTradeFeeDetail commissionAmt = new TrdTradeFeeDetail();
        commissionAmt.setTradeId(resultDeal.getTradeId());
        commissionAmt.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
        commissionAmt.setIsPayWithTrade(settleTransDataTotal.getIsPayWithTrade());
        commissionAmt.setRegistOrg(resultDeal.getRegistOrg());
        // 处理尾插-是最后一条数据
        if (lastItem) {
            stampFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getStampFee(), amtInfoMap.get("stampFee")));
            jsfFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getJsfFee(), amtInfoMap.get("jsfFee")));
            ghfFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getGhfFee(), amtInfoMap.get("ghfFee")));
            zgfFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getZgfFee(), amtInfoMap.get("zgfFee")));
            sxfFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getSxfFee(), amtInfoMap.get("sxfFee")));
            settleFee.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getSettleFee(), amtInfoMap.get("settleFee")));
            commissionAmt.setFeeAmt(ArithmeticUtil.subtract(resultDealTotal.getCommissionAmt(), amtInfoMap.get("commissionAmt")));
            // 不是最后一条数据
        } else {
            stampFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getStampFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("stampFee", ArithmeticUtil.doAdd(amtInfoMap.get("stampFee"), stampFee.getFeeAmt()));
            jsfFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getJsfFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("jsfFee", ArithmeticUtil.doAdd(amtInfoMap.get("jsfFee"), jsfFee.getFeeAmt()));
            ghfFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getGhfFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("ghfFee", ArithmeticUtil.doAdd(amtInfoMap.get("ghfFee"), ghfFee.getFeeAmt()));
            zgfFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getZgfFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("zgfFee", ArithmeticUtil.doAdd(amtInfoMap.get("zgfFee"), zgfFee.getFeeAmt()));
            sxfFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getSxfFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("sxfFee", ArithmeticUtil.doAdd(amtInfoMap.get("sxfFee"), sxfFee.getFeeAmt()));
            settleFee.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getSettleFee(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("settleFee", ArithmeticUtil.doAdd(amtInfoMap.get("settleFee"), settleFee.getFeeAmt()));
            commissionAmt.setFeeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getCommissionAmt(), resultDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("commissionAmt", ArithmeticUtil.doAdd(amtInfoMap.get("commissionAmt"), commissionAmt.getFeeAmt()));
        }
        // 费用不为空且大于0再封装
        if(stampFee.getFeeAmt() != null && stampFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(stampFee);
        }
        if(jsfFee.getFeeAmt() != null && jsfFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(jsfFee);
        }
        if(ghfFee.getFeeAmt() != null && ghfFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(ghfFee);
        }
        if(zgfFee.getFeeAmt() != null && zgfFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(zgfFee);
        }
        if(sxfFee.getFeeAmt() != null && sxfFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(sxfFee);
        }
        if(settleFee.getFeeAmt() != null && settleFee.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(settleFee);
        }
        if(commissionAmt.getFeeAmt() != null && commissionAmt.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            trdTradeFeeDetailList.add(commissionAmt);
        }
        return trdTradeFeeDetailList;
    }

    /**
     * 构建金融产品交易明细
     * @param notifyTrdProductDeal
     * @param resultDealTotal
     * @param amtInfoMap
     * @param lastItem
     * @return
     */
    private TrdProductDealDto createTrdProductDeal(TrdProductDeal notifyTrdProductDeal, TotalTrdProductDealDto resultDealTotal,
                                                   Map<String, BigDecimal> amtInfoMap, Boolean lastItem, BigDecimal shareAmtTotal) {
        TrdProductDealDto trdProductDeal = new TrdProductDealDto();
        // 交易编号
        trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_债券买入));
        // 业务类型
        trdProductDeal.setBusiType(DictCodeListExt3.业务类型_债券交易.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(resultDealTotal.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(resultDealTotal.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(resultDealTotal.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        trdProductDeal.setTradeDate(resultDealTotal.getSettleDate());
        // 起息日
        trdProductDeal.setVdate(null);
        // 到期日
        trdProductDeal.setMdate(null);
        // 交割日
        trdProductDeal.setSettleDate(resultDealTotal.getSettleDate());
        // 交易币种
        trdProductDeal.setCcy(resultDealTotal.getCcy());
        // 单位净价
        trdProductDeal.setUnitCprice(resultDealTotal.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(resultDealTotal.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(resultDealTotal.getUnitFprice());
        // 单位面值
        trdProductDeal.setParValue(resultDealTotal.getParValue());
        // 处理尾插-是最后一条数据
        if (lastItem) {
            // 交易份额
            trdProductDeal.setShareAmt(ArithmeticUtil.subtract(resultDealTotal.getShareAmt(), amtInfoMap.get("shareAmt")));
            // 净价总额
            trdProductDeal.setCpriceAmt(ArithmeticUtil.subtract(resultDealTotal.getCpriceAmt(), amtInfoMap.get("cpriceAmt")));
            // 交易本金
            trdProductDeal.setPrinAmt(ArithmeticUtil.subtract(resultDealTotal.getPrinAmt(), amtInfoMap.get("prinAmt")));
            // 全价总额
            trdProductDeal.setFpriceAmt(ArithmeticUtil.subtract(resultDealTotal.getFpriceAmt(), amtInfoMap.get("fpriceAmt")));
            // 交易金额
            trdProductDeal.setTradeAmt(ArithmeticUtil.subtract(resultDealTotal.getTradeAmt(), amtInfoMap.get("tradeAmt")));
            // 不是最后一条数据
        } else {
            // 交易份额
            trdProductDeal.setShareAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getShareAmt(), notifyTrdProductDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("shareAmt", ArithmeticUtil.doAdd(amtInfoMap.get("shareAmt"), trdProductDeal.getShareAmt()));
            // 净价总额
            trdProductDeal.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getCpriceAmt(), notifyTrdProductDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("cpriceAmt", ArithmeticUtil.doAdd(amtInfoMap.get("cpriceAmt"), trdProductDeal.getCpriceAmt()));
            // 交易本金
            trdProductDeal.setPrinAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getPrinAmt(), notifyTrdProductDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("prinAmt", ArithmeticUtil.doAdd(amtInfoMap.get("prinAmt"), trdProductDeal.getPrinAmt()));
            // 全价总额
            trdProductDeal.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getFpriceAmt(), notifyTrdProductDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("fpriceAmt", ArithmeticUtil.doAdd(amtInfoMap.get("fpriceAmt"), trdProductDeal.getFpriceAmt()));
            // 交易金额
            trdProductDeal.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(resultDealTotal.getTradeAmt(), notifyTrdProductDeal.getShareAmt()), shareAmtTotal, 2));
            amtInfoMap.put("tradeAmt", ArithmeticUtil.doAdd(amtInfoMap.get("tradeAmt"), trdProductDeal.getTradeAmt()));
        }
        // 利息总额
        trdProductDeal.setIntAmt(BigDecimal.ZERO);
        // 交易方向
        trdProductDeal.setPs(resultDealTotal.getPs());
        // 投资目的
        trdProductDeal.setInvAim(resultDealTotal.getInvAim());
        // 结算方式 券款兑付
        trdProductDeal.setSettMode(null);
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 组合代码
        trdProductDeal.setPortfolioId(resultDealTotal.getPortfolioId());
        // 交易场所
        trdProductDeal.setTradeMarket(resultDealTotal.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(resultDealTotal.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(resultDealTotal.getTradeMarketMode());
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构-'中证登'
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中国证券登记结算有限责任公司.getValue());
        // 备注
        trdProductDeal.setRemark("数据清算自动处理");
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 投管业务类型
        trdProductDeal.setTgBusiType(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 席位号
        trdProductDeal.setSeatId(notifyTrdProductDeal.getSeatId());
        // 前置交易编号
        trdProductDeal.setbTradeId(notifyTrdProductDeal.getTradeId());
        // 汇总交易编号
        trdProductDeal.setTotleTradeId(resultDealTotal.getTotleTradeId());
        return trdProductDeal;
    }
}
