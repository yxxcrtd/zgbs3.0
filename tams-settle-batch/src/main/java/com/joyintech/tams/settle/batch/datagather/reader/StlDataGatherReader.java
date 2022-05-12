package com.joyintech.tams.settle.batch.datagather.reader;


import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealService;
import com.joyintech.tams.settle.batch.datagather.constant.StlDataGatherConstant;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeReaderDto;
import com.joyintech.tams.settle.batch.datagather.dto.SettleTransDataDto;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see StlDataGatherReader
 * @since 1.0
 */
public abstract class StlDataGatherReader extends BaseReader<InvestTradeReaderDto> {

    @Autowired
    protected StlDownDataDealService stlDownDataDealService;

    @Autowired
    protected StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Override
    public List<InvestTradeReaderDto> createList() {
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 根据是否生成交易区分
        if(!isCreateTrade()){
            Map<String, Object> searchParam = new HashMap<>(4);
            searchParam.put("dealDate", execInfo.getExecuteDate());
            setSearchParam(searchParam);
            List<SettleTransDataDto> settleTransDataDtos = stlDataGatherMapper.selectSettleTransData(searchParam);
            Map<String, List<SettleTransDataDto>> tempMap = settleTransDataDtos.stream().collect(JoyinCollectors.groupingBy(SettleTransDataDto::getOrderId));
            return tempMap.values().stream().map(InvestTradeReaderDto::new).collect(Collectors.toList());
        }else{
            //存续期处理 特殊各自read
            Map<String, Object> searchParam = new HashMap<>(4);
            setSearchParam(searchParam);
            searchParam.put("dealDate", execInfo.getExecuteDate());
            searchParam.put("DELIVERY_RESULT_01", DictCodeListExt4.交收结果_正常交收.getValue());
            searchParam.put("DELIVERY_RESULT_02", DictCodeListExt4.交收结果_部分交收.getValue());
            // 债券存续期
            if(searchParam.containsKey("readerType") && StringUtil.equals(searchParam.get("readerType"),
                    StlDataGatherConstant.BOND_DURATION)){
                return this.getBondDurationSettleTransData(searchParam);
            }
            // 竞价回购存续期
            if(searchParam.containsKey("readerType") && StringUtil.equals(searchParam.get("readerType"),
                    StlDataGatherConstant.REPO_DURATION)){
                return this.getRepoDurationSettleTransData(searchParam);
            }
            // 协议回购逆回购到期
            if(searchParam.containsKey("readerType") && StringUtil.equals(searchParam.get("readerType"),
                    StlDataGatherConstant.AGRE_ANTI_REPO_EXPIRED)){
                return this.getAgreAntiRepoExpiredData(searchParam);
            }
            // 可转债申购确认
            if(searchParam.containsKey("readerType") && StringUtil.equals(searchParam.get("readerType"),
                    StlDataGatherConstant.BOND_PURCHASE_CONFIRM)){
                return this.getBondPurConfirSettleTransData(searchParam);
            }
            // 债券股票上市流通
            if(searchParam.containsKey("readerType") && StringUtil.equals(searchParam.get("readerType"),
                    StlDataGatherConstant.BOND_SHARES_MARKET_TRADE)){
                return this.getBondSharesMarketTradeData(searchParam);
            }
        }
        return new ArrayList<InvestTradeReaderDto>();
    }

    /**
     * 查询债券存续期成交明细数据
     * @param searchParam
     * @return
     */
    private List<InvestTradeReaderDto> getBondDurationSettleTransData(Map<String, Object> searchParam){
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        // 业务类型
        searchParam.put("finprodTypes", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue()));
        // 交易方向列表
        searchParam.put("psList", Arrays.asList(DictCodeList.交易方向_行权_赎回.getValue(), DictCodeList.交易方向_到期.getValue(), DictCodeList.交易方向_还本.getValue(), DictCodeList.交易方向_收息.getValue()));
        List<SettleTransDataDto> settleTransDataDtos = stlDataGatherService.listBondSettleTransData(searchParam);
        List<SettleTransDataDto> settleTransDataResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(settleTransDataDtos)) {
            //需要汇总的成交明细
            Map<String, List<SettleTransDataDto>> settleTransDataNeedTotalMap = new HashMap<>();
            List<SettleTransDataDto> settleTransDataSingle = settleTransDataDtos.stream().filter(item -> StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || (StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()) && StringUtil.equals(item.getPs(), DictCodeList.交易方向_行权_赎回.getValue()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(settleTransDataSingle)) {
                settleTransDataSingle.stream().forEach(item -> item.setTradeIdList(Arrays.asList(item.getTradeId())));
                settleTransDataResult.addAll(settleTransDataSingle);
            }
            settleTransDataDtos.stream().filter(item -> !(StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || (StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()) && StringUtil.equals(item.getPs(), DictCodeList.交易方向_行权_赎回.getValue())))).collect(Collectors.toList())
                    .stream().collect(Collectors.groupingBy(SettleTransDataDto::getBusiKey, Collectors.toList())).forEach(settleTransDataNeedTotalMap::put);
            for (Map.Entry<String, List<SettleTransDataDto>> parammap : settleTransDataNeedTotalMap.entrySet()) {
                SettleTransDataDto settleTransDataDto = new SettleTransDataDto();
                List<SettleTransDataDto> value = parammap.getValue();
                if (CollectionUtils.isNotEmpty(value)) {
                    SettleTransDataDto settleTransDataDtoInit = value.get(0);
                    BeanUtil.copyPropertiesIgnoreNull(settleTransDataDto, settleTransDataDtoInit);
                    settleTransDataDto.setCpriceAmt(settleTransDataDtos.stream().map(item -> item.getCpriceAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                    settleTransDataDto.setShareAmt(settleTransDataDtos.stream().map(item -> item.getShareAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                    settleTransDataDto.setIntAmt(settleTransDataDtos.stream().map(item -> item.getIntAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                    settleTransDataDto.setFpriceAmt(settleTransDataDtos.stream().map(item -> item.getFpriceAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                    settleTransDataDto.setTradeAmt(settleTransDataDtos.stream().map(item -> item.getTradeAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                    if ((null != settleTransDataDto.getIntAmt() || settleTransDataDto.getIntAmt().compareTo(BigDecimal.ZERO) != 0)
                            && settleTransDataDto.getIntAmt().compareTo(settleTransDataDto.getFpriceAmt()) != 0) {
                        if (DateUtil.compareDate(settleTransDataDtoInit.getMdate(), settleTransDataDtoInit.getMdate2()) == 0) {
                            settleTransDataDto.setPs(DictCodeList.交易方向_到期.getValue());
                        }
                        if (DateUtil.compareDate(settleTransDataDtoInit.getMdate(), settleTransDataDtoInit.getMdate2()) != 0) {
                            settleTransDataDto.setPs(DictCodeList.交易方向_还本.getValue());
                        }
                        // modify by etliu 20220227 start
                        // 广农商二阶段 GNS-901:【广农商场内清算】场内债券存续期-涉及到还本的交易，“利息明细信息”中未登录还本金额
                        if (settleTransDataDto.getPrinAmt() == null || settleTransDataDto.getPrinAmt().compareTo(BigDecimal.ZERO) <= 0) {
                            settleTransDataDto.setPrinAmt(value.get(1).getPrinAmt());
                        }
                        // modify by etliu 20220227 end
                    } else {
                        settleTransDataDto.setPs(DictCodeList.交易方向_收息.getValue());
                    }
                    settleTransDataDto.setTradeIdList(value.stream().map(e -> e.getTradeId()).collect(Collectors.toList()));
                }
                settleTransDataResult.add(settleTransDataDto);
            }
        }
        investTradeReaderDto.setSettleTransDataList(settleTransDataResult);
        List<InvestTradeReaderDto> result = new ArrayList<>();
        result.add(investTradeReaderDto);
        return result;
    }

    /**
     * 查询回购存续期成交明细数据
     * @param searchParam
     * @return
     */
    private List<InvestTradeReaderDto> getRepoDurationSettleTransData(Map<String, Object> searchParam){
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        List<SettleTransDataDto> settleTransDataDtos = new ArrayList<>();
        if(StringUtil.equals(searchParam.get("readerRepoType"), DictCodeList.回购类型_质押式竞价回购.getValue())){
            // 资产类型
            searchParam.put("finprodTypes", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()
                    ,DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()));
            // 交易方向列表
            searchParam.put("psList",  Arrays.asList(DictCodeList.交易方向_到期.getValue()));
            // ZGZGST-4214:增加业务场景类型条件 fuyongsheng 20211214 add start
            // 业务场景分类
            searchParam.put("busiSceneCalssList",  Arrays.asList(DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_到期.getValue()
                    ,DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_到期.getValue()));
            // ZGZGST-4214:增加业务场景类型条件 fuyongsheng 20211214 add end
            // 查询竞价回购存续期成交明细数据
            settleTransDataDtos = stlDataGatherService.listRepoSettleTransData(searchParam);
        }
        investTradeReaderDto.setSettleTransDataList(settleTransDataDtos);
        List<InvestTradeReaderDto> result = new ArrayList<>();
        result.add(investTradeReaderDto);
        return result;
    }

    /**
     * 查询协议回购逆回购到期成交明细数据
     * @param searchParam
     * @return
     */
    List<InvestTradeReaderDto> getAgreAntiRepoExpiredData(Map<String, Object> searchParam){
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        List<SettleTransDataDto> settleTransDataDtos = new ArrayList<>();
        if(StringUtil.equals(searchParam.get("readerRepoType"), DictCodeList.回购类型_质押式协议回购.getValue())){
            // 对账参数
            String isRecon = CacheUtil.getSysParam("IS_RECON");
            //（ YWBS IN （4030，债券质押购回交易）or 摘要标识 IN （220134，债券回购购回收取））
            searchParam.put("dzcjList",  Arrays.asList("债券质押购回交易","4030"));
            searchParam.put("zybsList",  Arrays.asList("债券回购购回收取","220134"));
            int dzcjCount =  stlDataGatherMapper.selectDzcjList(searchParam);
            if (StringUtil.equals(isRecon,"02") && dzcjCount > 0){
                searchParam.put("checkResultList",  Arrays.asList(DictCodeListExt3.对账状态结果_一致.getValue(),
                        DictCodeListExt3.对账状态结果_手工完成.getValue()));
            }
            searchParam.put("dqPs",  DictCodeList.交易方向_到期.getValue());
            searchParam.put("busiSceneCalssDqList", Collections.singletonList(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue()));
            settleTransDataDtos = stlDataGatherService.listRepoH01MaturitySettleTransData(searchParam);
        }
        investTradeReaderDto.setSettleTransDataList(settleTransDataDtos);
        List<InvestTradeReaderDto> result = new ArrayList<>();
        result.add(investTradeReaderDto);
        return result;
    }

    /**
     * 查询可转债申购确认成交明细数据
     * @param searchParam
     * @return
     */
    private List<InvestTradeReaderDto> getBondPurConfirSettleTransData(Map<String, Object> searchParam){
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        List<SettleTransDataDto> settleTransDataResult = new ArrayList<>();
        // 业务类型
        searchParam.put("finprodTypes", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券.getValue()));
        searchParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        searchParam.put("IS_CANCEL_N", DictCodeList.是否_否.getValue());
        searchParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        searchParam.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        List<SettleTransDataDto> settlePurConfirTransDataDtos = stlDataGatherService.listBondPurConfirSettleTransData(searchParam);
        if(CollectionUtils.isNotEmpty(settlePurConfirTransDataDtos)){
            settleTransDataResult.addAll(settlePurConfirTransDataDtos);
        }
        investTradeReaderDto.setSettleTransDataList(settleTransDataResult);
        List<InvestTradeReaderDto> result = new ArrayList<>();
        result.add(investTradeReaderDto);
        return result;
    }

    /**
     * 查询可转债申购确认成交明细数据
     * @param searchParam
     * @return
     */
    private List<InvestTradeReaderDto> getBondSharesMarketTradeData(Map<String, Object> searchParam){
        InvestTradeReaderDto investTradeReaderDto = new InvestTradeReaderDto();
        if (searchParam.containsKey("finprodOthCodeType") && StringUtil.equals(searchParam.get("finprodOthCodeType"),
                DictCodeListExt3.金融产品其他代码类型_上市流通.getValue())) {
            searchParam.put("OTH_TYPE_15", DictCodeListExt3.金融产品其他代码类型_上市流通.getValue());
            List<SettleTransDataDto> settleTransDataDtos = stlDataGatherService.listBoundSharesMarketTradeData(searchParam);
            investTradeReaderDto.setSettleTransDataList(settleTransDataDtos);
        }
        List<InvestTradeReaderDto> result = new ArrayList<>();
        result.add(investTradeReaderDto);
        return result;
    }

    /**
     * 设置查询参数
     * @param searchParam
     */
    protected abstract void setSearchParam(Map<String, Object> searchParam);

    /**
     * 是否为可处理的证券品种
     * @param secType 证券品种
     * @return
     */
    protected abstract boolean isSecType(String secType);

    /**
     * 是否需要生成交易相关数据 债券买卖等成交明细不需要在批处理生成交易数据，调用共通生成，存续期需要在批处理生成交易数据
     * @return
     */
    protected abstract boolean isCreateTrade();

}