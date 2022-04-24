package com.joyintech.tams.settle.batch.datagather.processor;

import com.alibaba.fastjson.JSON;
import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see StlDataGatherProcessor
 * @since 1.0
 */
public abstract class StlDataGatherProcessor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {
    private static Logger logger = LoggerFactory.getLogger(StlDataGatherProcessor.class);
    @Autowired
    protected StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Override
    public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {
        InvestTradeReaderDto item = (InvestTradeReaderDto)operateEntity;
        Date executeDate = this.getBatchExecuteInfo().getExecuteDate();
        // 根据InvestTradeReaderDto中的成交数据明细数据列表，构建一个InvestTradeWriteDto
        List<SettleTransDataDto> settleTransDataDtos = item.getSettleTransDataList();
        if(CollectionUtils.isNotEmpty(settleTransDataDtos)) {
            /*if(item.isCreateTrade()){
                //存续期
                InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();
                // 金融产品交易明细
                List<TrdProductDealDto> dealList = generateProductDeal(settleTransDataDtos);
                // 金融产品交易汇总信息
                List<TotalTrdProductDealDto> dealTotalList = generateDealTotal(dealList);
                // 生成汇总交易
                investTradeWriteDto.setFinProductDealTotalList(dealTotalList);
                // 金融产品交易汇总附属信息
                List<TrdDealTotalAdd> totalAddList = generateDealTotalAdd(dealTotalList);
                // 金融产品交易实物汇总信息
                List<TrdFinProductBarterTotal> trdBarterTotalList = generateDealBarterTotal(dealTotalList);
                // 金融产品交易实物信息
                List<TrdFinProductBarter> trdBarterList = generateDealBarterList(dealList);
                // 组合交易
                List<PtlTrade> ptlTradeList = generatePtlTrade(dealList);
                // 金融产品交易费用明细
                List<TrdTradeFeeDetail> tradeFeeDetailList = generateTradeFeeDetail(dealList);
                // 划款指令明细
                List<StlPayOrders> stlPayOrdersList = generateStlPayOrdersList(dealTotalList, dealList, settleTransDataDtos);
                // 交易类金融信息
                List<FinTradeProductDto> tradeProductList = generateFinTradeProductList(dealList, settleTransDataDtos);
                // 回购质押品
                List<FinDealPledge> dealPledgeList = new ArrayList<FinDealPledge>();
                if (item.getSettleTransDataPledgeList() != null && item.getSettleTransDataPledgeList().size() > 0) {
                    dealPledgeList = generateFinDealPledgeList(dealList, item.getSettleTransDataPledgeList(), settleTransDataDtos);
                }
                // 委托完成度
                List<EtrFinishStatus> etrFinishStatusList = generateEtrFinishStatusList(dealTotalList, dealList);

                if(null != totalAddList) {
                    investTradeWriteDto.setTrdDealTotalAddList(totalAddList);
                }
                if(null != trdBarterTotalList) {
                    investTradeWriteDto.setTrdBarterTotalList(trdBarterTotalList);
                }
                if(null != trdBarterList) {
                    investTradeWriteDto.setTrdBarterList(trdBarterList);
                }
                if(null != dealList) {
                    investTradeWriteDto.setProductDealList(dealList);
                }
                if (null != ptlTradeList) {
                    investTradeWriteDto.setPtlTradeList(ptlTradeList);
                }
                if(null != tradeFeeDetailList) {
                    investTradeWriteDto.setTradeFeeDetailList(tradeFeeDetailList);
                }
                if(null != stlPayOrdersList) {
                    investTradeWriteDto.setStlPayOrdersList(stlPayOrdersList);
                }
                if(null != tradeProductList) {
                    investTradeWriteDto.setTradeProductList(tradeProductList);
                }
                if(null != dealPledgeList && !dealPledgeList.isEmpty()) {
                    investTradeWriteDto.setDealPledgeList(dealPledgeList);
                }
                if(null != etrFinishStatusList) {
                    investTradeWriteDto.setEtrFinishStatusList(etrFinishStatusList);
                }
                investTradeWriteDto.setSecType(getSecType());
                return investTradeWriteDto;
            }else{*/
                //交易数据  调用共通
                generateSettleTransData(settleTransDataDtos);
            /*}*/
        }
        return null;
    }

    protected abstract String getSecType();

    protected abstract List<String> getFinprodType();

    /**
     *  汇总成交数据
     * @param settleTransDataList
     * @return
     */
    protected void generateSettleTransData(List<SettleTransDataDto> settleTransDataList) {
        logger.debug("汇总成交匹配数据:" + JSON.toJSONString(settleTransDataList));
        // 对成交明细汇总数据进行重新排序,匹配到上行委托的数据优先
        List<SettleTransDataDto> reRankSettleTransDataList = this.reRankSettleTransDataList(settleTransDataList);
        // modify by etliu 20211227 start
        // GZYHTZJY-777:回售交易当日无法得到成交的清算数据，导致无法匹配到对应的上行委托或指令
        // 筛选普通处理的成交明细汇总数据(除债回售、可转债申购、新股申购、配债、配股之外的数据)
        List<SettleTransDataDto> normalSettleTransDataList = filterNormalSettleTransDataList(reRankSettleTransDataList);
        // 筛选需特殊处理的成交明细汇总数据(债回售、可转债申购、新股申购、配债、配股的数据)
        List<SettleTransDataDto> specailSettleTransDataList = filterSpecialSettleTransDataList(reRankSettleTransDataList);

        // 需要更新状态的指令
        List <String> needUpdateStatusEntrustIdList = new ArrayList<>();
        // modify by etliu 20211227 end

        List<ExchangeTransMatchCommonDto> exchangeTransMatchCommons = new ArrayList<>();

        // 处理除债回售、可转债申购、新股申购、配债、配股之外的数据
        this.handleNormalTrade(normalSettleTransDataList, exchangeTransMatchCommons, needUpdateStatusEntrustIdList);

        // modify by etliu 20211227 start
        // GZYHTZJY-777:回售交易当日无法得到成交的清算数据，导致无法匹配到对应的上行委托或指令
        // 债回售、可转债申购、新股申购、配债、配股需要特殊处理
        this.handleSpecailTrade(specailSettleTransDataList, exchangeTransMatchCommons, needUpdateStatusEntrustIdList);
        // modify by etliu 20211227 end

        // 调用画面端成交匹配逻辑
        stlDataGatherService.transactionMacthBatch(exchangeTransMatchCommons, needUpdateStatusEntrustIdList);
    }

    /**
     * 对成交明细汇总数据进行重新排序,匹配到上行委托的数据优先
     * @param settleTransDataList
     * @return
     */
    private List<SettleTransDataDto> reRankSettleTransDataList(List<SettleTransDataDto> settleTransDataList){
        // modify by etliu 20211204 start
        // GZYHTZJY-711: 自动匹配应优先处理能够匹配到上行委托的数据，再根据业务字段匹配指令
        List<SettleTransDataDto> reRankSettleTransDataList = new ArrayList<>();
        reRankSettleTransDataList.addAll(settleTransDataList.stream().filter(
                x -> StringUtil.isNotBlank(x.getEntrustId())).collect(Collectors.toList()));
        reRankSettleTransDataList.addAll(settleTransDataList.stream().filter(
                x -> StringUtil.isBlank(x.getEntrustId())).collect(Collectors.toList()));
        // modify by etliu 20211204 end
        return reRankSettleTransDataList;
    }

    /**
     * 筛选普通处理的成交明细汇总数据(除债回售、可转债申购、新股申购、配债、配股之外的数据)
     * @param settleTransDataDtos
     * @return
     */
    private List<SettleTransDataDto> filterNormalSettleTransDataList(List<SettleTransDataDto> settleTransDataDtos){
        return  settleTransDataDtos.stream().filter(
                x -> !(StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), x.getPs())
                        || (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
                        || (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
                        || StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), x.getPs())
                        || StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(), x.getPs())
                )
        ).collect(Collectors.toList());
    }

    /**
     * 筛选需特殊处理的成交明细汇总数据(债回售、可转债申购、新股申购、配债、配股的数据)
     * @param settleTransDataDtos
     * @return
     */
    private List<SettleTransDataDto> filterSpecialSettleTransDataList(List<SettleTransDataDto> settleTransDataDtos){
        return  settleTransDataDtos.stream().filter(
                x -> (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), x.getPs())
                        || (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
                        || (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
                        || StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), x.getPs())
                        || StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(), x.getPs())
                )
        ).collect(Collectors.toList());
    }

    /**
     * 除债回售、可转债申购、新股申购、配债、配股之外的普通数据处理
     * @param normalSettleTransDataList
     * @param exchangeTransMatchCommons
     * @param needUpdateStatusEntrustIdList
     */
    private void handleNormalTrade(List<SettleTransDataDto> normalSettleTransDataList,
                                     List<ExchangeTransMatchCommonDto>  exchangeTransMatchCommons,
                                     List<String> needUpdateStatusEntrustIdList) {
        List<EtrEntrustDto> etrEntrustDtos = stlDataGatherMapper.listEtrEntrust(getFinprodType(),this.getBatchExecuteInfo().getExecuteDate());
        logger.debug("指令匹配数据:" + JSON.toJSONString(etrEntrustDtos));
        Map<String, List<EtrEntrustDto>> etrEntrustMap = etrEntrustDtos.stream().collect(Collectors.groupingBy(EtrEntrustDto::getBusiKey, Collectors.toList()));
        if(StringUtil.isEmptyList(etrEntrustDtos)){
            return;
        }
        // modify by etliu 20211204 start
        // GZYHTZJY-711: 自动匹配应优先处理能够匹配到上行委托的数据，再根据业务字段匹配指令
        // for (SettleTransDataDto settleTransDataDto : settleTransDataList) {
        for (SettleTransDataDto settleTransDataDto : normalSettleTransDataList) {
            // modify by etliu 20211204 end
            ExchangeTransMatchCommonDto exchangeTransMatchCommonDto = new ExchangeTransMatchCommonDto();
            BeanUtil.copyPropertiesIgnoreNull(exchangeTransMatchCommonDto,settleTransDataDto);
            exchangeTransMatchCommonDto.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());

            exchangeTransMatchCommonDto.setRemainNum(settleTransDataDto.getShareAmt());
            exchangeTransMatchCommonDto.setRemainAmount(settleTransDataDto.getFpriceAmt());
            exchangeTransMatchCommonDto.setSeqId(settleTransDataDto.getTradeId());

            if(StringUtil.isNotEmpty(settleTransDataDto.getEntrustId())){
                // modify by etliu 20211210 start
                // ZGZGST-4158: 重新跑清结算的批处理后，结算数据与当天全部的场内指令进行了自动匹配
//                    exchangeTransMatchCommonDto.setEntrustIdList(Arrays.asList(etrEntrustDtos.get(0).getEntrustId()));
                exchangeTransMatchCommonDto.setEntrustIdList(Collections.singletonList(settleTransDataDto.getEntrustId()));

                needUpdateStatusEntrustIdList.add(settleTransDataDto.getEntrustId());
                // modify by etliu 20211210 end
            }else{
                if(DictCodeList.回购类型_质押式协议回购.getValue().equals(settleTransDataDto.getRepoType())){
                    // 处理协议回购数据
                    this.handleAgreRepData(etrEntrustDtos, settleTransDataDto, exchangeTransMatchCommonDto, needUpdateStatusEntrustIdList);
                }else{
                    String busiKey = getSettleTransDataBusiKey(settleTransDataDto);
                    if(!etrEntrustMap.containsKey(busiKey)){
                        continue;
                    }
                    List<EtrEntrustDto> currEtrEntrustList = etrEntrustMap.get(busiKey);
                    if(CollectionUtils.isEmpty(currEtrEntrustList)){
                        continue;
                    }
                    if(currEtrEntrustList.size()==1){
                        exchangeTransMatchCommonDto.setEntrustIdList(Collections.singletonList(currEtrEntrustList.get(0).getEntrustId()));
                        needUpdateStatusEntrustIdList.add(currEtrEntrustList.get(0).getEntrustId());
                    }
                    if(currEtrEntrustList.size()>1){
                        List<String> entrustIds = currEtrEntrustList.stream().map(EtrEntrust::getEntrustId).collect(Collectors.toList());
                        needUpdateStatusEntrustIdList.addAll(entrustIds);

                        //委托数量汇总
                        BigDecimal entrustNumTotal = currEtrEntrustList.stream().map(EtrEntrustDto::getEntrustNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                        // GNS-847:竞价回购匹配时,shareAmt需除以100, modify by fuyongsheng 20220222 start
                        BigDecimal shareAmt = settleTransDataDto.getShareAmt();
                        if(null != shareAmt && DictCodeList.回购类型_质押式竞价回购.getValue().equals(settleTransDataDto.getRepoType())){
                            shareAmt = ArithmeticUtil.divide(shareAmt, new BigDecimal(100), 4, RoundingMode.HALF_UP);
                        }
                        // if(null!=settleTransDataDto.getShareAmt()&&settleTransDataDto.getShareAmt().compareTo(entrustNumTotal)>=0){
                        if(null != shareAmt && shareAmt.compareTo(entrustNumTotal)>=0){
                            // GNS-847:竞价回购匹配时,shareAmt需除以100, modify by fuyongsheng 20220222 end
                            exchangeTransMatchCommonDto.setEntrustIdList(entrustIds);
                        }else{
                            // 按金额汇总匹配,筛选指令金额不为空数据
                            List<EtrEntrustDto> filterEtrEntrustList = currEtrEntrustList.stream().filter(item->item.getEntrustAmt()!=null).collect(Collectors.toList());
                            //委托金额汇总
                            if(CollectionUtils.isNotEmpty(filterEtrEntrustList)){
                                BigDecimal entrustAmtTotal = filterEtrEntrustList.stream().map(EtrEntrustDto::getEntrustAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                                if(null!=settleTransDataDto.getTradeAmt()&&settleTransDataDto.getTradeAmt().compareTo(entrustAmtTotal)>=0){
                                    exchangeTransMatchCommonDto.setEntrustIdList(entrustIds);
                                }
                            }
                        }
                    }
                }
            }
            if(null!=exchangeTransMatchCommonDto&&!StringUtil.isEmptyList(exchangeTransMatchCommonDto.getEntrustIdList())){
                exchangeTransMatchCommons.add(exchangeTransMatchCommonDto);
            }
        }
    }

    /**
     * 处理协议回购数据
     * @param etrEntrustDtos
     * @param settleTransDataDto
     * @param exchangeTransMatchCommonDto
     * @param needUpdateStatusEntrustIdList
     */
    private void handleAgreRepData(List<EtrEntrustDto> etrEntrustDtos, SettleTransDataDto settleTransDataDto,
                                   ExchangeTransMatchCommonDto exchangeTransMatchCommonDto,
                                   List<String> needUpdateStatusEntrustIdList){
        // 协议回购特殊处理,筛选未完成匹配的(协议回购没有部分匹配)
        List<EtrEntrustDto> filterEtrEntrustDtos = etrEntrustDtos.stream()
                .filter(entry-> !DictCodeListExt3.下行交易匹配状态_匹配完成.getValue().equals(entry.getMatchClass()))
                .collect(Collectors.toList());
        // 获取质押式协议回购匹配指令列表
        List<EtrEntrustDto> currEtrEntrustList = this.getRepoH01MatchEntrustList(settleTransDataDto, filterEtrEntrustDtos);
        if(CollectionUtils.isEmpty(currEtrEntrustList)){
            return;
        }
        List<String> entrustIds = Lists.newArrayList();
        for(EtrEntrustDto currEtrEntrust : currEtrEntrustList){
            // 检查协议回购指令金额数量
            this.checkAgreRepEntrustAmt(settleTransDataDto, currEtrEntrust, entrustIds);
        }
        // 协议回购当且仅有一条指令满足条件时,自动匹配
        if(entrustIds.size() == 1){
            exchangeTransMatchCommonDto.setEntrustIdList(entrustIds);
            needUpdateStatusEntrustIdList.addAll(entrustIds);
        }
    }

    /**
     * 检查协议回购指令金额数量
     * @param settleTransDataDto
     * @param currEtrEntrust
     * @param entrustIds
     */
    private void checkAgreRepEntrustAmt(SettleTransDataDto settleTransDataDto, EtrEntrustDto currEtrEntrust,
                                        List<String> entrustIds){
        if(DictCodeListExt3.交易方向_中途换券.getValue().equals(settleTransDataDto.getPs())
                || DictCodeListExt3.交易方向_解除质押.getValue().equals(settleTransDataDto.getPs())){
            // 查询指令质押券信息
            List<EtrPledge> etrPledges = stlDataGatherMapper.selectEtrPledges(
                    currEtrEntrust.getEntrustId(), currEtrEntrust.getFinprodId());
            if(CollectionUtils.isNotEmpty(etrPledges)){
                if(null!=settleTransDataDto.getShareAmt()&&settleTransDataDto.getShareAmt().compareTo(etrPledges.get(0).getPledgeNumber())<=0){
                    entrustIds.add(currEtrEntrust.getEntrustId());
                }
            }
        }else{
            if(null!=settleTransDataDto.getShareAmt()&&settleTransDataDto.getShareAmt().compareTo(currEtrEntrust.getEntrustAmt())<=0){
                entrustIds.add(currEtrEntrust.getEntrustId());
            }
        }
    }

    // modify by etliu 20211227 start
    /**
     * 债回售、可转债申购、新股申购、配债、配股需要特殊处理
     * @param specailSettleTransDataList
     * @param exchangeTransMatchCommons
     * @param needUpdateStatusEntrustIdList
     */
    private void handleSpecailTrade (List<SettleTransDataDto> specailSettleTransDataList,
                                     List<ExchangeTransMatchCommonDto>  exchangeTransMatchCommons,
                                     List<String> needUpdateStatusEntrustIdList) {
        if (StringUtil.isEmptyList(specailSettleTransDataList)) {
            return;
        }

        // 查询债回售、可转债申购、新股申购、配债、配股对应指令
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dataList", specailSettleTransDataList);
        paramMap.put("finprodTypeList", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), DictCodeListExt1.金融产品类型_投管用_股票.getValue()));
        paramMap.put("psList", Arrays.asList(DictCodeList.交易方向_行权_回售.getValue(), DictCodeList.交易方向_申购.getValue(),
                DictCodeListExt3.交易方向_配债.getValue(), DictCodeListExt2.交易方向_配股.getValue()));

        List<EtrEntrustDto> etrEntrustDtoList = stlDataGatherMapper.selectSpecialEtrEntrustData(paramMap);
        if (StringUtil.isEmptyList(etrEntrustDtoList)) {
            return;
        }
        Map<String, List<EtrEntrustDto>> etrEntrustMap = etrEntrustDtoList.stream().collect(Collectors.groupingBy(EtrEntrustDto::getBusiKeyWithoutDate, Collectors.toList()));

        // 查询回售对应指令
        // 债回售：取资产的回售区间，取回售申请结束日<=平台日的最近一条，然后取区间内指令
        List<SettleTransDataDto> zhsSettleTransDataList = specailSettleTransDataList.stream().filter(
                x -> StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), x.getPs())
            ).collect(Collectors.toList());
        if (!StringUtil.isEmptyList(zhsSettleTransDataList)) {
            // 查询行权-回售计划
            paramMap.put("dataList", zhsSettleTransDataList);
            paramMap.put("dealDate", this.getBatchExecuteInfo().getExecuteDate());
            List<FinExercisePlan> finExercisePlanList = stlDataGatherMapper.selectFinExercisePlanData(paramMap);
            Map<String, List<FinExercisePlan>> finExercisePlanMap = finExercisePlanList.stream().collect(Collectors.groupingBy(FinExercisePlan::getFinprodId, Collectors.toList()));

            for (SettleTransDataDto settleTransDataDto: zhsSettleTransDataList)
            {
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 start
                // String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getTradePlat(),settleTransDataDto.getPs()),"_");
                String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getPs()),"_");
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 end
                if (etrEntrustMap.containsKey(busiKey) && finExercisePlanMap.containsKey(settleTransDataDto.getFinprodId())) {
                    List<EtrEntrustDto> currEntrustList = etrEntrustMap.get(busiKey);
                    List<FinExercisePlan> currFinExercisePlanList = finExercisePlanMap.get(settleTransDataDto.getFinprodId());
                    FinExercisePlan currFinExercisePlan = currFinExercisePlanList.stream().sorted(Comparator.comparing(FinExercisePlan::getVdate).reversed()).collect(Collectors.toList()).get(0);
                    List<EtrEntrustDto> resulstEntrustList = currEntrustList.stream().filter(x -> DateUtil.compareTo(x.getEntrustDate(), currFinExercisePlan.getVdate()) >= 0
                        && DateUtil.compareTo(x.getEntrustDate(), currFinExercisePlan.getMdate()) <= 0).collect(Collectors.toList());

                    this.handleMatch(settleTransDataDto, resulstEntrustList, exchangeTransMatchCommons, needUpdateStatusEntrustIdList);
                }
            }
        }

        // 查询可转债申购、新股申购对应指令
        List<SettleTransDataDto> sgSettleTransDataList = specailSettleTransDataList.stream().filter(
                x ->  (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
                        || (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), x.getFinprodType2())
                        && StringUtil.equals(DictCodeList.交易方向_申购.getValue(), x.getPs())
                )
            ).collect(Collectors.toList());
        if (!StringUtil.isEmptyList(sgSettleTransDataList)) {
            for (SettleTransDataDto settleTransDataDto: sgSettleTransDataList)
            {
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 start
                // String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getTradePlat(),settleTransDataDto.getPs()),"_");
                String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getPs()),"_");
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 end
                if (etrEntrustMap.containsKey(busiKey)) {
                    List<EtrEntrustDto> currEntrustList = etrEntrustMap.get(busiKey);
                    this.handleMatch(settleTransDataDto, currEntrustList, exchangeTransMatchCommons, needUpdateStatusEntrustIdList);
                }
            }
        }

        // 查询配债、配股对应指令
        List<SettleTransDataDto> pgpzSettleTransDataList = specailSettleTransDataList.stream().filter(
                x -> StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(), x.getPs())
                        || StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(), x.getPs())
            ).collect(Collectors.toList());
        if (!StringUtil.isEmptyList(pgpzSettleTransDataList)) {
            // 查询配股、配债对应申报信息
            paramMap.put("dataList", pgpzSettleTransDataList);
            paramMap.put("dealDate", this.getBatchExecuteInfo().getExecuteDate());
            List<FinProductOthCode> finProductOthCodeList = stlDataGatherMapper.selectFinProductOthCodeData(paramMap);
            Map<String, List<FinProductOthCode>> finProductOthCodeMap = finProductOthCodeList.stream().collect(Collectors.groupingBy(FinProductOthCode::getFinprodId, Collectors.toList()));

            for (SettleTransDataDto settleTransDataDto: pgpzSettleTransDataList)
            {
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 start
                // String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getTradePlat(),settleTransDataDto.getPs()),"_");
                String busiKey = StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(),settleTransDataDto.getPs()),"_");
                // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 end
                if (etrEntrustMap.containsKey(busiKey) && finProductOthCodeMap.containsKey(settleTransDataDto.getFinprodId())) {
                    List<EtrEntrustDto> currEntrustList = etrEntrustMap.get(busiKey);
                    List<FinProductOthCode> currfinProductOthCodeList = finProductOthCodeMap.get(settleTransDataDto.getFinprodId());
                    FinProductOthCode currFinProductOthCode = currfinProductOthCodeList.stream().sorted(Comparator.comparing(FinProductOthCode::getDeclaredStartDate).reversed()).collect(Collectors.toList()).get(0);
                    List<EtrEntrustDto> resulstEntrustList = currEntrustList.stream().filter(x -> DateUtil.compareTo(x.getEntrustDate(), currFinProductOthCode.getDeclaredStartDate()) >= 0
                            && DateUtil.compareTo(x.getEntrustDate(), currFinProductOthCode.getDeclaredEndDate()) <= 0).collect(Collectors.toList());

                    this.handleMatch(settleTransDataDto, resulstEntrustList, exchangeTransMatchCommons, needUpdateStatusEntrustIdList);
                }
            }
        }
    }

    /**
     * 处理匹配逻辑
     * @param settleTransDataDto 结算数据
     * @param currEtrEntrustList 指令数据
     * @param exchangeTransMatchCommons 匹配结果列表
     * @param needUpdateStatusEntrustIdList
     */
    private void handleMatch(SettleTransDataDto settleTransDataDto,
                             List<EtrEntrustDto> currEtrEntrustList,
                             List<ExchangeTransMatchCommonDto> exchangeTransMatchCommons,
                             List<String> needUpdateStatusEntrustIdList) {
        if (settleTransDataDto == null || StringUtil.isEmptyList(currEtrEntrustList)) {
            return;
        }
        ExchangeTransMatchCommonDto exchangeTransMatchCommonDto = new ExchangeTransMatchCommonDto();
        BeanUtil.copyPropertiesIgnoreNull(exchangeTransMatchCommonDto, settleTransDataDto);
        exchangeTransMatchCommonDto.setMatchType(DictCodeListExt3.下行交易匹配类型_自动匹配.getValue());

        exchangeTransMatchCommonDto.setRemainNum(settleTransDataDto.getShareAmt());
        exchangeTransMatchCommonDto.setRemainAmount(settleTransDataDto.getFpriceAmt());
        exchangeTransMatchCommonDto.setSeqId(settleTransDataDto.getTradeId());

        if(currEtrEntrustList.size() == 1){
            exchangeTransMatchCommonDto.setEntrustIdList(Arrays.asList(currEtrEntrustList.get(0).getEntrustId()));
            needUpdateStatusEntrustIdList.add(currEtrEntrustList.get(0).getEntrustId());
        }
        if(currEtrEntrustList.size() > 1){
            List<String> entrustIds = currEtrEntrustList.stream().map(e -> e.getEntrustId()).collect(Collectors.toList());

            needUpdateStatusEntrustIdList.addAll(entrustIds);

            //委托数量汇总
            BigDecimal entrustNumTotal = currEtrEntrustList.stream().map(EtrEntrustDto::getEntrustNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            if(null!=settleTransDataDto.getShareAmt()&&settleTransDataDto.getShareAmt().compareTo(entrustNumTotal)>=0){
                exchangeTransMatchCommonDto.setEntrustIdList(entrustIds);
            }else{
                //委托金额汇总
                BigDecimal entrustAmtTotal = currEtrEntrustList.stream().map(EtrEntrustDto::getEntrustAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(null!=settleTransDataDto.getTradeAmt()&&settleTransDataDto.getTradeAmt().compareTo(entrustAmtTotal)>=0){
                    exchangeTransMatchCommonDto.setEntrustIdList(entrustIds);
                }
            }
        }

        if(null != exchangeTransMatchCommonDto && !StringUtil.isEmptyList(exchangeTransMatchCommonDto.getEntrustIdList())){
            exchangeTransMatchCommons.add(exchangeTransMatchCommonDto);
        }
    }

    // modify by etliu 20211227 end

    /**
     * 获取质押式协议回购匹配指令
     * @param settleTransDataDto
     * @param etrEntrustDtos
     * @return
     */
    private List<EtrEntrustDto> getRepoH01MatchEntrustList(SettleTransDataDto settleTransDataDto, List<EtrEntrustDto> etrEntrustDtos){
        List<EtrEntrustDto> matchEtrEntrustList = Lists.newArrayList();
        List<SettleTransDataTotalPledge> pledges = stlDataGatherMapper.selectSettleTransDataTotalPledge(settleTransDataDto.getTradeId());
        String settlePs = settleTransDataDto.getPs();
        for(EtrEntrustDto etrEntrustDto : etrEntrustDtos){
            if( !settleTransDataDto.getSecAcctId().equals(etrEntrustDto.getSecAcctId())
                ||!settleTransDataDto.getAcctNo().equals(etrEntrustDto.getDealCurAcct())
                || !settleTransDataDto.getTradeDate().equals(etrEntrustDto.getEntrustDate())
                // modify by fuyongsheng 匹配指令忽略交易平台 start
                // || !settleTransDataDto.getTradePlat().equals(etrEntrustDto.getTradePlat())
                // modify by fuyongsheng 匹配指令忽略交易平台 end
            ){
                continue;
            }
            if(DictCodeList.交易方向_买入.getValue().equals(settlePs)){ // 首期
                if(
                    (
                        (
                            DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(settleTransDataDto.getFinprodType2())
                            &&
                            DictCodeListExt3.交易方向_正回购.getValue().equals(etrEntrustDto.getPs())
                        )
                        ||
                        (
                            DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(settleTransDataDto.getFinprodType2())
                            &&
                            DictCodeListExt3.交易方向_逆回购.getValue().equals(etrEntrustDto.getPs())
                        )
                    )
                    && CollectionUtils.isNotEmpty(pledges)
                    && pledges.get(0).getPledgeFinprodId().equals(etrEntrustDto.getFinprodId())){
                    matchEtrEntrustList.add(etrEntrustDto);
                }
            }else if((DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(settlePs)
                    && DictCodeListExt3.交易方向_提前购回.getValue().equals(etrEntrustDto.getPs())) // 清算的提前终止 = 指令的提前购回
                    ||
                    (DictCodeList.交易方向_到期.getValue().equals(settlePs))
                    && DictCodeListExt3.交易方向_到期购回.getValue().equals(etrEntrustDto.getPs())){ // 清算的到期 = 指令的到期购回
                if(etrEntrustDto.getTradeId().equals(settleTransDataDto.getfCjbh())
                    && etrEntrustDto.getVpayDate().equals(settleTransDataDto.getVdate())){
                    matchEtrEntrustList.add(etrEntrustDto);
                }
            }else if(DictCodeList.交易方向_到期续做.getValue().equals(settlePs)){
                if(settlePs.equals(etrEntrustDto.getPs())
                        // add by etlliu 20220220 start
                        // GNS-833:【广农商场内清算】协议回购-上交所中途换券和解除质押，匹配结果不对
                        && StringUtil.equals(settleTransDataDto.getFinprodType2(), etrEntrustDto.getFinprodType2())
                        && etrEntrustDto.getTradeId().equals(settleTransDataDto.getfCjbh())
                        // add by etlliu 20220220 end
                        && CollectionUtils.isNotEmpty(pledges)
                        && pledges.get(0).getPledgeFinprodId().equals(etrEntrustDto.getFinprodId())){
                    matchEtrEntrustList.add(etrEntrustDto);
                }
            }else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(settlePs)
                    || DictCodeListExt3.交易方向_解除质押.getValue().equals(settlePs)){
                if(DictCodeListExt3.交易方向_中途换券.getValue().equals(settlePs)){
                    pledges = pledges.stream().filter(pledge ->
                            DictCodeListExt3.质押券置换_置换方向_换入.getValue().equals(pledge.getDisplaceDirection()))
                            .collect(Collectors.toList());
                }
                if(settlePs.equals(etrEntrustDto.getPs()) && CollectionUtils.isNotEmpty(pledges)
                    && pledges.get(0).getPledgeFinprodId().equals(etrEntrustDto.getFinprodId())
                    && etrEntrustDto.getTradeId().equals(settleTransDataDto.getfCjbh())
                    && etrEntrustDto.getVpayDate().equals(settleTransDataDto.getVdate())){
                    matchEtrEntrustList.add(etrEntrustDto);
                }
            }
        }
        return matchEtrEntrustList;
    }

    /**
     * 组合交易
     * @param dealList 交易明细
     * @return 组合交易
     */
    protected List<PtlTrade> generatePtlTrade(List<TrdProductDealDto> dealList) {
    	return null;
    }

    /**
     * 汇总交易生成
     * @param dealList 交易明细
     * @return
     */
    protected List<TotalTrdProductDealDto> generateDealTotal(List<TrdProductDealDto> dealList) {
        return null;
    }
    
    /**
     * 汇总交易附属生成
     * @param totleList 汇总交易
     * @return
     */
    protected List<TrdDealTotalAdd> generateDealTotalAdd(List<TotalTrdProductDealDto> totleList) {
        return null;
    }
    
    /**
     * 实物汇总交易生成
     * @param totleList 实物汇总交易
     * @return
     */
    protected List<TrdFinProductBarterTotal> generateDealBarterTotal(List<TotalTrdProductDealDto> totleList) {
        return null;
    }
    
    /**
     * 实物交易生成
     * @param dealList 实物交易
     * @return
     */
    protected List<TrdFinProductBarter> generateDealBarterList(List<TrdProductDealDto> dealList) {
        return null;
    }
    

    /**
     * 金融产品交易明细
     * @param settleTransDataDtos 成交数据明细
     * @return
     */
    protected List<TrdProductDealDto> generateProductDeal(List<SettleTransDataDto> settleTransDataDtos) {
    	return null;
    }

    /**
     * 金融产品交易费用明细
     * @param dealList 交易明细
     * @return
     */
    protected List<TrdTradeFeeDetail> generateTradeFeeDetail(List<TrdProductDealDto> dealList) {
    	return null;
    }

    /**
     * 划款指令明细
     */
    protected List<StlPayOrders> generateStlPayOrdersList(List<TotalTrdProductDealDto> totalTrdProductDealDtoList, List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
    	return null;
    }

    protected List<FinTradeProductDto> generateFinTradeProductList(List<TrdProductDealDto> dealList, List<SettleTransDataDto> settleTransDataDtos) {
        return null;
    }

    protected List<FinDealPledge> generateFinDealPledgeList(List<TrdProductDealDto> dealList, List<SettleTransDataPledge> settleTransDataPledgeList, List<SettleTransDataDto> settleTransDataDtos) {
        return null;
    }
    
    protected List<EtrFinishStatus> generateEtrFinishStatusList(List<TotalTrdProductDealDto> totalTrdProductDealDtoList, List<TrdProductDealDto> dealList){
    	return null;
    }

    private String getSettleTransDataBusiKey(SettleTransDataDto settleTransDataDto){
        if(DictCodeList.回购类型_质押式竞价回购.getValue().equals(settleTransDataDto.getRepoType())){
            String ps;
            if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(settleTransDataDto.getFinprodType2())){
                ps = DictCodeListExt3.交易方向_正回购.getValue();
            }else{
                ps = DictCodeListExt3.交易方向_逆回购.getValue();
            }
            String finProdId = settleTransDataDto.getTermType();
            // modify by fuyongsheng 匹配指令忽略交易平台 start
            // return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),finProdId, DateUtil.format(settleTransDataDto.getTradeDate()),settleTransDataDto.getTradePlat(),ps),"_");
            return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),finProdId, DateUtil.format(settleTransDataDto.getTradeDate()),ps),"_");
            // modify by fuyongsheng 匹配指令忽略交易平台 end
        }
        // modify by fuyongsheng 匹配指令忽略交易平台 start
        // GNS-922:质押券出入库不看交易平台 modify by fuyongsheng 20220219 start
//        else if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(settleTransDataDto.getPs()) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(settleTransDataDto.getPs())){
//            return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(), DateUtil.format(settleTransDataDto.getTradeDate()),settleTransDataDto.getPs()),"_");
//        }
//        // GNS-922:质押券出入库不看交易平台 modify by fuyongsheng 20220219 end
//        else if(DictCodeListExt3.交易方向_债转股.getValue().equals(settleTransDataDto.getPs())){
//            return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(), DateUtil.format(settleTransDataDto.getTradeDate()),settleTransDataDto.getPs()),"_");
//        }
//        else{
//            return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(), DateUtil.format(settleTransDataDto.getTradeDate()),settleTransDataDto.getTradePlat(),settleTransDataDto.getPs()),"_");
//        }
        return StringUtil.joinString(Arrays.asList(settleTransDataDto.getSecAcctId(),settleTransDataDto.getAcctNo(),settleTransDataDto.getFinprodId(), DateUtil.format(settleTransDataDto.getTradeDate()),settleTransDataDto.getPs()),"_");
        // modify by fuyongsheng 匹配指令忽略交易平台 end
    }
}
