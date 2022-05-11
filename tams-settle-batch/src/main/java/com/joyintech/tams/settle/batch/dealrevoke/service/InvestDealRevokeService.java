package com.joyintech.tams.settle.batch.dealrevoke.service;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.settle.batch.datadeal.service.StlDataDealProcessCommonService;
import com.joyintech.tams.settle.batch.dealrevoke.dao.InvestDealRevokeMapper;

/**
 * @author 何阳阳
 * @date 2020/8/3
 * @see InvestDealRevokeService
 * @since 1.0
 */
@Service
@Lazy
public class InvestDealRevokeService {
    private static final Logger logger = LoggerFactory.getLogger(InvestDealRevokeService.class);

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    private JoyinRestTemplate restTemplate;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    @Value("${application.micorservice.contextPath.tamsServer:http://tams-server}")
    private String tamsSerever;

    @Autowired
    private InvestDealRevokeMapper investDealRevokeMapper;

	@Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private TrdCommonBaseMapper trdCommonBaseMapper;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;


    private Map<String, Object> createCommonParam(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("dealDate", tradeDate);
        param.put("dealDateStr", DateUtil.format(tradeDate));
        param.put("portfolioIdList", portfolioIdList);
        return param;
    }

    /**
     * 查询汇总交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTotalTradeIdForBond(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        //param.put("busiType", DictCodeList.共通业务类型_债券类资产交易.getValue());
        List<String> busiTypeList = new ArrayList<String>();
        busiTypeList.add(DictCodeList.共通业务类型_债券类资产交易.getValue());
        busiTypeList.add(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        busiTypeList.add(DictCodeListExt2.共通业务类型_可转债申购交易.getValue());
        // GZYHTZJY-1142: 对债券债转股交易进行撤销 fuyongsheng add start
        busiTypeList.add(DictCodeListExt2.共通业务类型_可转债转股交易.getValue());
        // ZGTFCP-1507:质押券入库交易撤销 modify by fuyongsheng 20220321 start
        busiTypeList.add(DictCodeListExt3.共通业务类型_质押券出入库交易.getValue());
        // ZGTFCP-1507:质押券入库交易撤销 modify by fuyongsheng 20220321 end
        param.put("busiTypeList", busiTypeList);
        // GZYHTZJY-1142: 对债券债转股交易进行撤销 fuyongsheng add end
        //增加协议回购中途换券撤销、竞价回购的入库、出库
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        param.put("psList", Arrays.asList(DictCodeList.交易方向_买入.getValue()
                ,DictCodeList.交易方向_卖出.getValue(),DictCodeList.交易方向_行权_回售.getValue(),DictCodeList.交易方向_申购确认.getValue()
                ,DictCodeList.交易方向_申购.getValue(),DictCodeListExt3.交易方向_债转股.getValue(),DictCodeListExt3.交易方向_配债.getValue()
                // ZGZGST-4219: 对债券存续期交易进行撤销 fuyongsheng add start
                ,DictCodeList.交易方向_收息到账.getValue(),DictCodeList.交易方向_还本到账.getValue()
                // ZGZGST-4219: 对债券存续期交易进行撤销 fuyongsheng add end
                // GZYHTZJY-1142: 对债券债转股交易进行撤销 fuyongsheng add start
                ,DictCodeListExt3.交易方向_债转股.getValue()
                // GZYHTZJY-1142: 对债券债转股交易进行撤销 fuyongsheng add end
                // GNS-929:增加质押券出入库方向 modify by fuyongsheng 20220224 start
                ,DictCodeListExt3.交易方向_质押券入库.getValue()
                ,DictCodeListExt3.交易方向_质押券出库.getValue()
                // GNS-929:增加质押券出入库方向 modify by fuyongsheng 20220224 end
                // modify by etliu 20220228 start
                // 广农商二阶段 GNS-976:【广农商场内清算】场内债券存续期-行权赎回交易重复跑批时未对原交易数据进行撤销，并处理相关头寸
                ,DictCodeList.交易方向_行权_赎回.getValue()
                // modify by etliu 20220228 end
        ));
        param.put("isBond", DictCodeList.是否_是.getValue());
        param.put("PS_01", DictCodeList.交易方向_买入.getValue());
        param.put("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        param.put("psApplyList", Arrays.asList(DictCodeList.交易方向_申购.getValue(),DictCodeList.交易方向_申购确认.getValue()));
        param.put("PURCH_MODLE_02", DictCodeListExt3.申购方式_网下申购.getValue());
        List<String> totalTradeIdTgMs = investDealRevokeMapper.selectTotalTradeIdTgM(param);


        //删除交收结果表（交收成功的交易对应的交收结果删除）
        param.put("totalTradeIdList", totalTradeIdTgMs);
        param.put("DELIVERY_STATUS_02",DictCodeListExt3.交收状态_已交收.getValue());
        List<String>  manualSettleIdTgMs = investDealRevokeMapper.selectManualSettleIdTgM(param);
        investDealRevokeMapper.deleteManualSetDetailBySettleId(manualSettleIdTgMs);
        investDealRevokeMapper.deleteManualSettleInfoBySettleId(manualSettleIdTgMs);

        param.put("psList",  Arrays.asList(DictCodeList.交易方向_上市流通.getValue()));
        param.put("busiType", DictCodeList.共通业务类型_债券类资产交易.getValue());
        // GNS-957:上市流通日期取当天 modify by fuyongsheng 20220224 start
        // String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
        // Date shNextTradeDate = stlDataDealProcessCommonService.getNextWorkDay(tradeDate,calendarId);
        // param.put("dealDate", shNextTradeDate);
        // param.put("dealDateStr", DateUtil.format(shNextTradeDate));
        param.put("dealDate", tradeDate);
        param.put("dealDateStr", DateUtil.format(tradeDate));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue()));
        // 上交所上市流通
        List<String> shltTotalTradeIdTg03Ms = investDealRevokeMapper.selectSsltTotalTradeIdTgM(param);
        //        calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
//        Date szNextTradeDate = stlDataDealProcessCommonService.getNextWorkDay(tradeDate,calendarId);
//        param.put("dealDate", szNextTradeDate);
//        param.put("dealDateStr", DateUtil.format(szNextTradeDate));
        param.put("dealDate", tradeDate);
        param.put("dealDateStr", DateUtil.format(tradeDate));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        // 深交所上市流通
        List<String> shltTotalTradeIdTg04Ms = investDealRevokeMapper.selectSsltTotalTradeIdTgM(param);
        totalTradeIdTgMs.addAll(shltTotalTradeIdTg03Ms);
        totalTradeIdTgMs.addAll(shltTotalTradeIdTg04Ms);
        return totalTradeIdTgMs;
    }

    /**
     * 查询交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTradeIdForRepo(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        param.put("finprodType2List", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()
                ,DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        param.put("psList",Arrays.asList(DictCodeList.交易方向_买入.getValue()));
        return investDealRevokeMapper.selectTradeIdTgM(param);
    }

    /**
     * 查询汇总交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTotalTradeIdForRepo(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
//        param.put("busiType", DictCodeList.共通业务类型_质押式回购存续期.getValue());
        param.put("psDurList", Arrays.asList(DictCodeList.交易方向_到期续做.getValue(), DictCodeList.交易方向_到期.getValue()
                , DictCodeListExt3.交易方向_投资端_提前终止.getValue(),DictCodeListExt3.交易方向_中途换券.getValue(),DictCodeListExt3.交易方向_解除质押.getValue()
                ,DictCodeList.交易方向_收息到账.getValue(),DictCodeList.交易方向_还本到账.getValue()));
        param.put("tgBusiTypeList",Arrays.asList(DictCodeListExt3.平安业务类型_债券质押式逆回购.getValue(),DictCodeListExt3.平安业务类型_债券质押式正回购.getValue()
                ,DictCodeListExt1.平安业务类型_正回购到期.getValue(),DictCodeListExt1.平安业务类型_逆回购到期.getValue()));
        param.put("isRepoFund",DictCodeList.是否_是.getValue());
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        return investDealRevokeMapper.selectTotalTradeIdTgM(param);
    }

    /**
     * 查询交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTradeIdForShares(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        param.put("finprodType2List", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_股票.getValue()));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        //AND A.交易方向 IN (交易方向_买入，交易方向_卖出，交易方向_申购，交易方向_申购确认，交易方向_上市流通）
        param.put("psSharsList",Arrays.asList(DictCodeList.交易方向_买入.getValue(),DictCodeList.交易方向_卖出.getValue(),DictCodeList.交易方向_申购.getValue(),
                DictCodeList.交易方向_申购确认.getValue(), DictCodeListExt2.交易方向_配股.getValue()));
        param.put("psSharsTwoList",Arrays.asList(DictCodeList.交易方向_买入.getValue(),DictCodeList.交易方向_卖出.getValue(),DictCodeList.交易方向_申购.getValue(),
                DictCodeList.交易方向_申购确认.getValue(), DictCodeListExt2.交易方向_配股.getValue()));
        return investDealRevokeMapper.selectTradeIdTgM(param);
    }

    /**
     * 查询汇总交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTotalTradeIdForShares(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        param.put("busiType", DictCodeListExt2.共通业务类型_股票分红交易.getValue());
        /*param.put("psList", Arrays.asList(DictCodeList.股票分红类型_分红送转.getValue()));*/
        param.put("psList", Arrays.asList(DictCodeList.交易方向_现金分红.getValue(),DictCodeList.交易方向_红利再投.getValue()));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        List<String> totalTradeIdTgMs = investDealRevokeMapper.selectTotalTradeIdTgM(param);


        param.put("psList",  Arrays.asList(DictCodeList.交易方向_上市流通.getValue()));
        param.put("busiType", DictCodeListExt2.共通业务类型_股票投资交易.getValue());
        // GNS-957:上市流通日期取当天 modify by fuyongsheng 20220224 start
//        String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
//        Date shNextTradeDate = stlDataDealProcessCommonService.getNextWorkDay(tradeDate,calendarId);
//        param.put("dealDate", shNextTradeDate);
//        param.put("dealDateStr", DateUtil.format(shNextTradeDate));
        param.put("dealDate", tradeDate);
        param.put("dealDateStr", DateUtil.format(tradeDate));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue()));
        // 上交所上市流通
        List<String> shltTotalTradeIdTg03Ms = investDealRevokeMapper.selectSsltTotalTradeIdTgM(param);
        //        calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
//        Date szNextTradeDate = stlDataDealProcessCommonService.getNextWorkDay(tradeDate,calendarId);
//        param.put("dealDate", szNextTradeDate);
//        param.put("dealDateStr", DateUtil.format(szNextTradeDate));
        param.put("dealDate", tradeDate);
        param.put("dealDateStr", DateUtil.format(tradeDate));
        // GNS-957:上市流通日期取当天 modify by fuyongsheng 20220224 end
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        // 深交所上市流通
        List<String> shltTotalTradeIdTg04Ms = investDealRevokeMapper.selectSsltTotalTradeIdTgM(param);
        totalTradeIdTgMs.addAll(shltTotalTradeIdTg03Ms);
        totalTradeIdTgMs.addAll(shltTotalTradeIdTg04Ms);
        return totalTradeIdTgMs;
    }

    /**
     * 查询交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTradeIdForFund(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        param.put("finprodType2List", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_基金.getValue()));
        param.put("psList", Arrays.asList(DictCodeList.交易方向_买入.getValue(),DictCodeList.交易方向_卖出.getValue()));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        return investDealRevokeMapper.selectTradeIdTgM(param);
    }

    /**
     * 查询汇总交易编号
     * @param tradeDate 交易日
     * @param portfolioIdList 组合代码列表
     * @return 汇总交易编号列表
     */
    public List<String> selectTotalTradeIdForFund(Date tradeDate, List<String> portfolioIdList) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        param.put("busiType", DictCodeList.共通业务类型_基金类资产存续期.getValue());
        param.put("psList", Arrays.asList(DictCodeList.交易方向_除权登记.getValue(),DictCodeList.交易方向_现金分红.getValue(),DictCodeList.交易方向_红利再投.getValue()));
        param.put("tradeMarketList",Arrays.asList(DictCodeListExt1.交易场所_上海证券交易所.getValue(),DictCodeListExt1.交易场所_深圳证券交易所.getValue()));
        param.put("isRepoFund", DictCodeList.是否_是.getValue());
        return investDealRevokeMapper.selectTotalTradeIdTgM(param);
    }

    /**
     * 删除日间成交数据形成的头寸流水信息
     */
    public void deletePositionFlow(Date tradeDate, List<String> portfolioIdList, String busiType,List<String> orderIds) {
        Map<String, Object> param = createCommonParam(tradeDate, portfolioIdList);
        List<String> finprodType2List = new ArrayList<String>();
        
        if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), busiType)) {
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        }
        if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), busiType)) {
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        }
        if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), busiType)) {
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        }
        if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), busiType)) {
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        	finprodType2List.add(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        }
        param.put("finprodType2List", finprodType2List);
        //删除前,查出流水，以便更新余额
        param.put("trdIdList", orderIds);
        List<PtlCashPosition> ptlCashPositions = investDealRevokeMapper.listPtlCashPositionFlow(param);
        List<PtlSecPosition> ptlSecPositions = investDealRevokeMapper.listPtlSecPositionFlow(param);
        List<EtrCashPosition> etrCashPositions = investDealRevokeMapper.listEtrCashPositionFlow(param);
        List<EtrSecPosition> etrSecPositions = investDealRevokeMapper.listEtrSecPositionFlow(param);
        investDealRevokeMapper.deletePtlCashPositionFlow(param);
        investDealRevokeMapper.deletePtlSecPositionFlow(param);
        investDealRevokeMapper.deleteEtrCashPositionFlow(param);
        investDealRevokeMapper.deleteEtrSecPositionFlow(param);

        if(!CollectionUtils.isEmpty(orderIds)){
            //删除标准券入出库表
            investDealRevokeMapper.deleteTrdStasecFlow(param);
            //删除标准券入出库明细表
            investDealRevokeMapper.deleteTrdStasecFlowDetail(param);
        }

        // add 日间指令流水删除 20210817 start
        investDealRevokeMapper.deleteEtrCashPositionFlowByTradeId(param);
        // add end
        // 删除日间委托完成度
        investDealRevokeMapper.deleteEtrFinishStatus(param);
        //更新余额
        ptlCashPositions.stream().forEach(item ->{
             if(null!=item.getAmount()){
                 item.setAmount(item.getAmount().negate());
                 investDealRevokeMapper.updatePtlCashPosition(item);
             }
        });
        ptlSecPositions.stream().forEach(item ->{
            if(null!=item.getAmount()){
                item.setAmount(item.getAmount().negate());
                investDealRevokeMapper.updatePtlSecPosition(item);
            }
        });
        etrCashPositions.stream().forEach(item ->{
            if(null!=item.getAmount()){
                item.setAmount(item.getAmount().negate());
                investDealRevokeMapper.updateEtrCashPosition(item);
            }
        });
        etrSecPositions.stream().forEach(item ->{
            if(null!=item.getAmount()){
                item.setAmount(item.getAmount().negate());
                investDealRevokeMapper.updateEtrSecPosition(item);
            }
        });

        // 撤销日间成交交易调用新头寸接口 add by fuyongsheng 20220308 start
        positionHttpRequestBatchClose(param);
        // 撤销日间成交交易调用新头寸接口 add by fuyongsheng 20220308 end
    }

    /**
     * 撤销日间成交交易调用新头寸接口
     * @param param
     */
    private void positionHttpRequestBatchClose(Map<String, Object> param){
        // 撤销日间成交交易调用新头寸接口 add by fuyongsheng 20220308 start
        // 查询场内日间成交交易
        // add by fuyongsheng 20220426 start
        param.put("TRADE_MARKET_03", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        param.put("TRADE_MARKET_04", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        // add by fuyongsheng 20220426 end
        List<TrdProductTempDeal> trdProductTempDeals = investDealRevokeMapper.getTrdProductTempDeals(param);
        if(!CollectionUtils.isEmpty(trdProductTempDeals)){
            // 日间成交撤销
            Map<String, Object> batchRevoke = new HashMap<>();
            // 日间下单撤销
            Map<String, Object> orderBatchRevoke = new HashMap<>();

            // 日间成交撤销数据列表
            List<Map<String, Object>> dataList = new ArrayList<>();
            // 日间下单撤销数据列表
            List<Map<String, Object>> orderDataList = new ArrayList<>();
            // 交易编号去重
            List<TrdProductTempDeal> distinctDeals = trdProductTempDeals
                    .stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(TrdProductTempDeal::getTradeId))), ArrayList::new));
            Set<String> orderIdSet = new HashSet<>();
            for(TrdProductTempDeal deal : distinctDeals){
                // 日间成交撤销数据
                Map<String, Object> data = new HashMap<>();
                // 日间下单撤销数据
                Map<String, Object> orderData = new HashMap<>();

                // 查询下单明细数据
                InfContractDetailfile infContractDetailfile = trdCommonBaseMapper.selectInfContractDetailfile(deal.getfTradeId());
                if(null == infContractDetailfile){
                    continue;
                }
                EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(infContractDetailfile.getEntrustId());
                if(null == etrEntrust){
                    continue;
                }
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(infContractDetailfile.getEntrustId());
                if(null == etrEntrustAdd){
                    continue;
                }
                data.put("busiType", commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                        etrEntrustAdd.getAssetType1()));
                data.put("ps", deal.getPs());
                data.put("closeId", deal.getTradeId());
                data.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
                data.put("busiAct", DictCodeListExt3.业务动作_成交匹配.getValue());
                dataList.add(data);
                // ZGTFCP-1249:orderId防止重复 modify by fuyongsheng 20220318 start
                if(!orderIdSet.contains(infContractDetailfile.getOrderId())){
                    if(etrEntrust != null){
                        orderData.put("busiType", commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                                etrEntrustAdd.getAssetType1()));
                        orderData.put("ps", deal.getPs());
                        orderData.put("closeId", infContractDetailfile.getOrderId());
                        orderData.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
                        orderData.put("busiAct", DictCodeListExt3.业务动作_成交匹配.getValue());
                        orderDataList.add(orderData);
                    }
                    orderIdSet.add(infContractDetailfile.getOrderId());
                }
                // ZGTFCP-1249:orderId防止重复 modify by fuyongsheng 20220318 end
            }
            batchRevoke.put("data", dataList);
            batchRevoke.put("userId", LoginUserUtil.getUserName());
            batchRevoke.put("totalTradeId",IDGenerator.generate());
            batchRevoke.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            //ZGTFCP-1222:更换头寸批处理接口 modify by fuyongsheng  20220318 start
            // PositionHttpRequest.batchClose(batchRevoke);
            logger.debug("【批量撤销日间交易数据】batchRevoke:{}", JSON.toJSONString(batchRevoke));
            PositionHttpRequest.batchRevoke(batchRevoke);
            //ZGTFCP-1222:更换头寸批处理接口 modify by fuyongsheng  20220318 end


            orderBatchRevoke.put("data", orderDataList);
            orderBatchRevoke.put("userId", LoginUserUtil.getUserName());
            orderBatchRevoke.put("totalTradeId",IDGenerator.generate());
            orderBatchRevoke.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            //ZGTFCP-1222:更换头寸批处理接口 modify by fuyongsheng  20220318 start
            // PositionHttpRequest.batchClose(orderBatchRevoke);
            logger.debug("【批量撤销日间下单数据】orderBatchRevoke:{}", JSON.toJSONString(orderBatchRevoke));
            PositionHttpRequest.batchRevoke(orderBatchRevoke);
            //ZGTFCP-1222:更换头寸批处理接口 modify by fuyongsheng  20220318 end
        }
        // 撤销日间成交交易调用新头寸接口 add by fuyongsheng 20220308 end

        // ZGTFCP-1621:冲销成交回报的质押券出入库流水 add by fuyongsheng 20220322 tsart
        // 查询日间标准券流水表
        List<TrdTempStasecFlow> trdTempStasecFlows = investDealRevokeMapper.getTrdTempStasecFlows(param);
        if(!CollectionUtils.isEmpty(trdTempStasecFlows)){
            // 日间标准券撤销
            Map<String, Object> stasecBatchRevoke = new HashMap<>();
            // 日间下单撤销数据列表
            List<Map<String, Object>> stasecDataList = new ArrayList<>();
            for(TrdTempStasecFlow trdTempStasecFlow : trdTempStasecFlows){
                // 日间标准券撤销数据
                Map<String, Object> stasecData = new HashMap<>();
                stasecData.put("busiType", commonPositionEngineService.getPositionBusiType(
                        DictCodeListExt1.金融产品类型_投管用_债券.getValue(), trdTempStasecFlow.getPs(), null));
                stasecData.put("ps", trdTempStasecFlow.getPs());
                stasecData.put("closeId", trdTempStasecFlow.getSeqno());
                stasecData.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
                stasecData.put("busiAct", DictCodeListExt3.业务动作_成交匹配.getValue());
                stasecDataList.add(stasecData);
            }
            stasecBatchRevoke.put("data", stasecDataList);
            stasecBatchRevoke.put("userId", LoginUserUtil.getUserName());
            stasecBatchRevoke.put("totalTradeId",IDGenerator.generate());
            stasecBatchRevoke.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
            logger.debug("【批量撤销日间标准券出入库数据】stasecBatchRevoke:{}", JSON.toJSONString(stasecBatchRevoke));
            PositionHttpRequest.batchRevoke(stasecBatchRevoke);
            // ZGTFCP-1621:冲销成交回报的质押券出入库流水 add by fuyongsheng 20220322 end
        }
    }

    public void updateEtrStatusByTrdId(List<String> tradeIdList, List<String> totalTradeIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("ENTRUST_STATUS_03",DictCodeList.委托状态_已接收.getValue());
        if (!StringUtil.isEmptyList(tradeIdList)){
            param.put("trdIdList", tradeIdList);
            investDealRevokeMapper.updateEtrStatusByTrdId(param);
        }
        if (!StringUtil.isEmptyList(totalTradeIdList)){
            param.put("trdIdList", totalTradeIdList);
            investDealRevokeMapper.updateEtrStatusByTrdId(param);
        }


    }
    
    /**
     * 删除日终交易占用的委托完成度信息
     * 
     * @param tradeIdList 交易明细
     * @param totalTradeIdList 汇总交易明细
     * @see
     * @since 1.0
     */
    public void deleteEtrFinishStatusByTrdId(List<String> tradeIdList, List<String> totalTradeIdList){
    	Map<String, Object> param = new HashMap<>();
    	if (!StringUtil.isEmptyList(tradeIdList)){
    		param.put("trdIdList", tradeIdList);
    		investDealRevokeMapper.deleteEtrFinishStatusByTrdId(param);
    	}
    	if (!StringUtil.isEmptyList(totalTradeIdList)){
    		param.put("trdIdList", totalTradeIdList);
    		investDealRevokeMapper.deleteEtrFinishStatusByTrdId(param);
    	}
    }
    
    /**
     * 根据交易编号删除日间指令现金流水
     * 
     * @param tradeIdList 交易明细
     * @param totalTradeIdList 汇总交易明细
     * @see
     * @since 1.0
     */
    public void deleteEtrCashPositionFlowByTrdId(List<String> tradeIdList, List<String> totalTradeIdList){
    	Map<String, Object> param = new HashMap<>();
    	if (!StringUtil.isEmptyList(tradeIdList)){
    		param.put("trdIdList", tradeIdList);
    		investDealRevokeMapper.deleteEtrCashPositionFlowByTrdId(param);
    	}
    	if (!StringUtil.isEmptyList(totalTradeIdList)){
    		param.put("trdIdList", totalTradeIdList);
    		investDealRevokeMapper.deleteEtrCashPositionFlowByTrdId(param);
    	}
    }
    

    /**
     * 删除日间成交数据形成的头寸流水信息
     */
    public void revokeDeal(List<String> tradeIdList) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("tradeIdList", tradeIdList);
        param.put("updateUser", GlobalConstant.SYSTEM_USER);
        param.put("revokeReason", "数据清算批处理");
        // 调用债券交易撤销逻辑
        if(isSingle) {
            simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
        }
        else {
            restTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
        }

    }

    /**
     * 删除日间成交数据形成的头寸流水信息
     */
    public void revokeDealTotal(List<String> totalTradeIdList) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("totalTradeIdList", totalTradeIdList);
        param.put("updateUser", GlobalConstant.SYSTEM_USER);
        param.put("revokeReason", "数据清算批处理");
        // 调用债券交易撤销逻辑
        if(isSingle) {
            simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
        }
        else {
            restTemplate.postForObject(tamsSerever + "/invest/trade/api/revoke", param, String.class);
        }

    }

    public Map<String,String> getTradeIdPsMap(List<String> tradeIdList) {
        Map<String,Object> param = new HashMap<>();
        param.put("tradeIdList",tradeIdList);
        param.put("isTotal","02");
        if (!CollectionUtils.isEmpty(tradeIdList)){
            return getTradeIdPsMap(param);
        }
        return null;
    }

    public Map<String,String> getTotalTradeIdPsMap(List<String> totleTradeIdList) {
        Map<String,Object> param = new HashMap<>();
        param.put("totleTradeIdList",totleTradeIdList);
        param.put("isTotal","01");
        if (!CollectionUtils.isEmpty(totleTradeIdList)){
            return getTradeIdPsMap(param);
        }
        return null;

    }

    public Map<String,String> getTradeIdPsMap(Map<String,Object> param) {
        List<Map<String, String>> list = investDealRevokeMapper.selectPsById(param);
        return getStringStringMap(param, list);
    }

    public Map<String,String> getRepoTradeIdPsMap(List<String> totleTradeIdList) {
        Map<String,Object> param = new HashMap<>();
        param.put("isTotal","01");
        param.put("totleTradeIdList",totleTradeIdList);
        if (!CollectionUtils.isEmpty(totleTradeIdList)) {
            List<Map<String, String>> list = investDealRevokeMapper.selectRepoPsById(param);
            return getStringStringMap(param, list);
        }
        return null;
    }

    private Map<String, String> getStringStringMap(Map<String, Object> param, List<Map<String, String>> list) {
        Map<String,String> map = new HashMap<>();
        if (!StringUtil.isEmptyList(list)){
            list.forEach(item->{
               if (StringUtil.isNotEmpty(param.get("isTotal"))){
                  if( StringUtil.equals(param.get("isTotal"),"01")) {
                      String totalkey = null;
                      String psvalue = null;
                      for (Map.Entry<String,String> i:item.entrySet()) {
                         if ("TOTALKEY".equalsIgnoreCase(i.getKey())) {
                            totalkey = i.getValue();
                         }else if ("PSVALUE".equalsIgnoreCase(i.getKey())) {
                            psvalue = i.getValue();
                         }
                       }
                      if (totalkey != null) {
                          map.put(totalkey, psvalue);
                      }
                   }else if( StringUtil.equals(param.get("isTotal"),"02")){
                       String tradekey = null;
                       String psvalue = null;
                       for (Map.Entry<String,String> i:item.entrySet()) {
                         if ("TRADEKEY".equalsIgnoreCase(i.getKey())) {
                             tradekey = i.getValue();
                         }else if ("PSVALUE".equalsIgnoreCase(i.getKey())) {
                              psvalue = i.getValue();
                          }
                        }
                      if (tradekey != null) {
                          map.put(tradekey, psvalue);
                      }
                    }
                 }
            });
        }
        return map;
    }

    public List<String> searchTradeIdsByTotalTradeIds(List<String> totalTradeIds){
       return investDealRevokeMapper.searchTradeIdsByTotalTradeIds(totalTradeIds);
    }

    public void deletePtlSecPositionFlow(List<String> totalTradeIds){
        investDealRevokeMapper.deletePtlSecPositionFlowByTradeIds(totalTradeIds);
    }

    public void deleteEtrSecPositionFlow(List<String> totalTradeIds){
        investDealRevokeMapper.deleteEtrSecPositionFlowByTradeIds(totalTradeIds);
    }

    public void deleteEtrCashPositionFlow(List<String> totalTradeIds){
        investDealRevokeMapper.deleteEtrCashPositionFlowByTradeIds(totalTradeIds);
    }

    public void deletePtlCashPositionFlow(List<String> totalTradeIds){
        investDealRevokeMapper.deletePtlCashPositionFlowByTradeIds(totalTradeIds);
    }

    public List<PtlSecPositionFlowDto> getPtlSecPositionFlowsByTradeIds(List<String> tradeIds){
        return investDealRevokeMapper.getPtlSecPositionFlowsByTradeIds(tradeIds);
    }

    public List<EtrSecPositionFlowDto> getEtrSecPositionFlowsByTradeIds(List<String> tradeIds){
        return investDealRevokeMapper.getEtrSecPositionFlowsByTradeIds(tradeIds);
    }

    public void deleteZyTrdStasecFlows(List<String> tradeIds){
        if(CollectionUtils.isEmpty(tradeIds)){
            return;
        }
        investDealRevokeMapper.deleteZyTrdStasecFlows(tradeIds);
    };

    public void deleteZyTrdStasecFlowDetails(List<String> tradeIds){
        if(CollectionUtils.isEmpty(tradeIds)){
            return;
        }
        investDealRevokeMapper.deleteZyTrdStasecFlowDetails(tradeIds);
    };


    /**
     * tradeIds
     * @param tradeIds
     */
    public void executeSecPositionFlow(List<String> tradeIds){
        // 组合持仓
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = this.getPtlSecPositionFlowsByTradeIds(tradeIds);
        for(PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows){
            ptlSecPositionFlow.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow.setAmount(ptlSecPositionFlow.getAmount().negate());
            ptlSecPositionFlow.setTradeId(ptlSecPositionFlow.getTradeId() + "R");
            basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
        }
        // 指令持仓
        List<EtrSecPositionFlowDto> etrSecPositionFlows = this.getEtrSecPositionFlowsByTradeIds(tradeIds);
        for(EtrSecPositionFlowDto etrSecPositionFlow : etrSecPositionFlows){
            etrSecPositionFlow.setSeqno(IDGenerator.generate());
            etrSecPositionFlow.setAmount(etrSecPositionFlow.getAmount().negate());
            etrSecPositionFlow.setTradeId(etrSecPositionFlow.getTradeId() + "R");
        }
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setDealEtr(true);
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
    }

    // modify by etliu 20220227 start
    // 广农商二阶段 GNS-963:【广农商场内清算】协议回购上交所存续期（解除质押、到期购回、到期续做、提前购回）指令提交后，指令持仓更新不对
    /**
     * 删除日间生成的回购交易类金融产品数据
     * @param dealDate
     */
    public void deleteDayFinTradeProductData(Date dealDate) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("dealDate", dealDate);
        investDealRevokeMapper.deleteDayFinTradeProduct(paraMap);
        investDealRevokeMapper.deleteDayFinTradeProductAdd(paraMap);
        investDealRevokeMapper.deleteDayFinProductType(paraMap);
        investDealRevokeMapper.deleteDayFinDealPledge(paraMap);
    }
    // modify by etliu 20220227 end
}
