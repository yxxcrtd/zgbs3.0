package com.joyintech.tams.settle.batch.datagather.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonParamDto;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.dao.StlDataGatherMapper;
import com.joyintech.tams.settle.common.dto.FundInfoDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see StlDataGatherService
 * @since 1.0
 */
@Service
public class StlDataGatherService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(StlDataGatherService.class);
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
    private StlDataGatherMapper stlDataGatherMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    
    @Autowired
    private TrdFinProductBarterTotalMapper trdBarterTotalMapper;
    
    @Autowired
    private TrdFinProductBarterMapper trdBarterMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;
    
    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;
    
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;

    @Autowired
    private SettleCommonService settleCommonService;
    
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;
    
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    
    @Autowired
    private SettleTransTgMappingMapper settleTransTgMappingMapper;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    // modify by etliu 20220220 start
    // 广农商二阶段 GNS-880:【广农商场内清算】上交所配股上市流通前一日清算解析ZQBD文件后，交易和头寸未处理:组合持仓流水和指令持仓流水登录的都是对的，ETR_SEC_POSITION和PTL_SEC_POSITION未更新
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
    // modify by etliu 20220220 start

	/**
     * 获取除权交易明细
     * @param paraMap 查询参数
     * @return 除权交易明细
     */
    public List<SettleDelDealDetailDto> selectSettleDelDealDetail(Map<String, Object> paraMap) {
    	return stlDataGatherMapper.selectSettleDelDealDetail(paraMap);
    }

    public int deleteParam(Date dealDate) {
        return stlDataGatherMapper.deleteParam(dealDate);
    }

    /**
     * 根据申请编号查询投管交易编号
     * @param applyTradeId 申请编号
     * @return 投管交易编号
     */
    public String selectBTradeId(String applyTradeId, String ps) {
        Map<String, String> paraMap = new HashMap<>(2);
        paraMap.put("ps", ps);
        paraMap.put("tradeId", applyTradeId);
        return stlDataGatherMapper.selectBTradeId(paraMap);
    }

    /**
     * 计算债券到期收益率和行权收益率
     * @param productDeal
     */
    public void calc(TrdProductDeal productDeal) {
        // 设置计算参数
        BondTradeDealParam dealParam = new BondTradeDealParam();
        dealParam.setFinprodId(productDeal.getFinprodId());
        dealParam.setTradeAmt(productDeal.getShareAmt().multiply(BigDecimal.valueOf(100)));
        dealParam.setSettleDate(productDeal.getSettleDate());
        dealParam.setVdate(productDeal.getVdate());
        dealParam.setTradeMarket(productDeal.getTradeMarket());
        dealParam.setTradeMarketMode(productDeal.getTradeMarketMode());
        dealParam.setPs(productDeal.getPs());
        dealParam.setUnitInt(productDeal.getUnitInt());
        dealParam.setUnitFprice(productDeal.getUnitFprice());

        // 设置计算类型为全价总额（根据全价总额计算到期收益率和行权收益率）
        dealParam.setCalculateField("fpriceAmt");
        dealParam.setFpriceAmt(productDeal.getFpriceAmt());
        dealParam.setIntAmt(productDeal.getIntAmt());
        dealParam.setInvAim(productDeal.getInvAim());

        // 调用债券交易计算逻辑
        // 调用债券交易计算逻辑
        Map ajaxResult = new HashMap();
        if (isSingle) {
            ajaxResult = simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/bond/TrdBondInvestTrade002/calculateData", dealParam, Map.class);
        } else {
            ajaxResult = restTemplate.postForObject(tamsSerever + "/invest/trade/bond/TrdBondInvestTrade002/calculateData", dealParam, Map.class);
        }
        if (ajaxResult != null && ajaxResult.containsKey("data")) {
	        Object result = ajaxResult.get("data");
	        if(result instanceof Map) {
	            Map<String, Object> calcResult = (Map)result;
	            Object yield = calcResult.get("yield");
	            Object exerYield = calcResult.get("exerYield");
	            if(yield instanceof BigDecimal) {
	                productDeal.setYield((BigDecimal)yield);
	            }
	            else if (yield != null) {
	                productDeal.setYield(new BigDecimal(yield.toString()));
	            }
	            if(exerYield instanceof BigDecimal) {
	                productDeal.setExerYield((BigDecimal)exerYield);
	            }
	            else if (exerYield != null) {
	                productDeal.setExerYield(new BigDecimal(exerYield.toString()));
	            }
	            if (productDeal.getYield() != null){
	                productDeal.setYield(productDeal.getYield().divide(BigDecimal.valueOf(100)));
	            }
                if (productDeal.getExerYield() != null){
                    productDeal.setExerYield(productDeal.getExerYield().divide(BigDecimal.valueOf(100)));
                }
	        }
        }
    }

    public void excutePosition(Map<String, Object> param) {
        // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 start
        param.put("isNotWorkflow", "true");
        // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 end
        // 调用债券交易计算逻辑
        if(isSingle) {
            simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/api/execute", param, Map.class);
        }
        else {
            restTemplate.postForObject(tamsSerever + "/invest/trade/api/execute", param, Map.class);
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeData(InvestTradeWriteDto tradeWriteDto) throws InvocationTargetException, IllegalAccessException {
        /**
         * 金融产品汇总交易
         */
        List<TotalTrdProductDealDto> finProductDealTotalList = tradeWriteDto.getFinProductDealTotalList();

        /**
         * 金融产品交易明细
         */
        List<TrdProductDealDto> productDealList = tradeWriteDto.getProductDealList();

        /**
         * 金融产品交易附属表
         */
        List<TrdProductDealAdd> trdProductDealAddList = tradeWriteDto.getTrdProductDealAddList();

        /**
         * 组合交易DTO
         */
        List<PtlTrade> ptlTradeList = tradeWriteDto.getPtlTradeList();

        /**
         * 金融产品交易费用明细
         */
        List<TrdTradeFeeDetail> tradeFeeDetailList = tradeWriteDto.getTradeFeeDetailList();

        /**
         * 划款指令明细
         */
        List<StlPayOrders> stlPayOrdersList = tradeWriteDto.getStlPayOrdersList();

        /**
         * 交易类金融信息
         */
        List<FinTradeProductDto> tradeProductList = tradeWriteDto.getTradeProductList();

        /**
         * 回购质押品
         */
        List<FinDealPledge> dealPledgeList = tradeWriteDto.getDealPledgeList();
        
        /**
         *  汇总交易附属信息
         */
        List<TrdDealTotalAdd> trdDealTotalAddList = tradeWriteDto.getTrdDealTotalAddList();
        
        /**
         *  实物交易汇总表
         */
        List<TrdFinProductBarterTotal> trdBarterTotalList = tradeWriteDto.getTrdBarterTotalList();
        
        /**
         *  实物交易表
         */
        List<TrdFinProductBarter> trdBarterList = tradeWriteDto.getTrdBarterList();
        
        /**
         *  成交编号业务编号关联表（只针对除权登记数据）
         */
        List<SettleTransTgMapping> settleTransTgMappingList = tradeWriteDto.getSettleTransTgMappingList();
        
        /**
         * 日间委托完成度表有一笔交易编号等于自身委托编号的记录，这条记录每次正向跑批时需要实时更新
         */
        List<EtrFinishStatus> etrFinishStatusList = tradeWriteDto.getEtrFinishStatusList();

        /**
         *  指令持仓流水
         */
        List<EtrSecPositionFlow> etrSecPositionFlowList = tradeWriteDto.getEtrSecPositionFlowList();

        /**
         * 组合持仓流水
         */
        List<PtlSecPositionFlow> ptlSecPositionFlowList = tradeWriteDto.getPtlSecPositionFlowList();

        // 全量基金信息获取
        Map<String, FundInfoDto> fundInfoDtoMap = settleCommonService.getAllFundInfoDto();
        //
        Map<String, FinTradeProductDto> finTradeProductMap = new HashMap<>();
        if (!StringUtil.isEmptyList(tradeProductList)) {
        	finTradeProductMap = tradeProductList.stream().collect(Collectors.toMap(FinTradeProductDto::getFinprodId, item -> item));
        }

        if(CollectionUtils.isNotEmpty(finProductDealTotalList)) {
        	Map<String, List<TrdDealTotalAdd>> totleAddMap = new HashMap<String, List<TrdDealTotalAdd>>();
            if (CollectionUtils.isNotEmpty(trdDealTotalAddList)){
                totleAddMap = trdDealTotalAddList.stream().collect(Collectors.groupingBy(TrdDealTotalAdd::getTotleTradeId));
            }
            for (TotalTrdProductDealDto totalTrdProductDealDto : finProductDealTotalList) {
                // 汇总交易表
                trdFinProductDealTotalMapper.insertSelective(totalTrdProductDealDto);
                
                if (!CollectionUtils.isNotEmpty(totleAddMap.get(totalTrdProductDealDto.getTotleTradeId()))){
                    TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
                    BeanUtil.copyProperties(trdDealTotalAdd, totalTrdProductDealDto);
                    // 汇总交易扩展表
                    trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
                }

                PaTrdFinProductDealTotal paProductDealTotal = new PaTrdFinProductDealTotal();
                paProductDealTotal.setTotleTradeId(totalTrdProductDealDto.getTotleTradeId());
                paProductDealTotal.setPaBussType(getPaBussType(totalTrdProductDealDto,tradeWriteDto.getSecType(), fundInfoDtoMap));
                paProductDealTotal.setRegDate(totalTrdProductDealDto.getRegDate());
                if (StringUtil.isNotEmpty(totalTrdProductDealDto.getPayType())) {
                	paProductDealTotal.setPayType(totalTrdProductDealDto.getPayType());
                }
                if (StringUtil.isNotEmpty(totalTrdProductDealDto.getBonusCfmDate())) {
                	paProductDealTotal.setBonusCfmDate(totalTrdProductDealDto.getBonusCfmDate());
                }
                // ZGZGST-4220:解决存续期详情画面字段为空问题 fuyongsheng add start
                // modify by etliu 20220227 start
                // 广农商二阶段 GNS-901：【广农商场内清算】场内债券存续期-涉及到还本的交易，“利息明细信息”中未登录还本金额
                if(DictCodeList.共通业务类型_质押式回购存续期.getValue().equals(totalTrdProductDealDto.getBusiType())
                        || DictCodeList.共通业务类型_债券类资产存续期.getValue().equals(totalTrdProductDealDto.getBusiType())){
                    // modify by etliu 20220227 end
                    paProductDealTotal.setTheoryInt(totalTrdProductDealDto.getTheoryInt());
                    paProductDealTotal.setTheoryPrin(totalTrdProductDealDto.getTheoryprinAmt());
                }
                // ZGZGST-4220:解决存续期详情画面字段为空问题 fuyongsheng add end
                paTrdFinProductDealTotalMapper.insertSelective(paProductDealTotal);
            }
        }
        Map<String, TrdProductDealAdd> trdProductDealAddMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(trdProductDealAddList)) {
            for(TrdProductDealAdd trdProductDealAdd : trdProductDealAddList){
                trdProductDealAddMap.put(trdProductDealAdd.getTradeId(), trdProductDealAdd);
            }
        }
        Map<String, List<TrdProductDealDto>> dealMap = new HashMap<String, List<TrdProductDealDto>>();
        if(CollectionUtils.isNotEmpty(productDealList)) {
            dealMap = productDealList.stream().collect(Collectors.groupingBy(TrdProductDealDto::getTradeId));
            for (TrdProductDealDto productDealDto : productDealList) {
                // 金融产品交易表
                trdProductDealMapper.insertSelective(productDealDto);
                TrdProductDealAdd trdProductDealAdd = trdProductDealAddMap.get(productDealDto.getTradeId());
                if(trdProductDealAdd == null){
                    // 金融产品交易扩展表
                    trdProductDealAdd = new TrdProductDealAdd();
                    BeanUtil.copyProperties(trdProductDealAdd, productDealDto);
                }
                // 申购模式
                if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(productDealDto.getFinprodType2()))
                        && DictCodeList.交易方向_申购确认.getValue().equals(productDealDto.getPs())) {
                	trdProductDealAdd.setPurchModle(DictCodeListExt2.申购方式_网上申购.getValue());
                }
                // 股票的现金分红交易，金融产品交易附属表的  REG_DATE  也要登值，估值那边说需要
                if ((DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(productDealDto.getFinprodType2()))
                        && DictCodeList.交易方向_现金分红.getValue().equals(productDealDto.getPs())) {
                	trdProductDealAdd.setRegDate(productDealDto.getRegDate());
                }
                if ((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(productDealDto.getFinprodType2()))&&productDealDto.getBonusCfmDate()!=null) {
                    trdProductDealAdd.setBonusCfmDate(productDealDto.getBonusCfmDate());
                }
                trdProductDealAdd.setGenType("03");
                trdProductDealAddMapper.insertSelective(trdProductDealAdd);
                // 回购时登录PA_TRD_PRODUCT_DEAL表
                if (StringUtil.equals(productDealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) ||
                	StringUtil.equals(productDealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
                	PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                	paTrdProductDeal.setTradeId(productDealDto.getTradeId());
                	if (StringUtil.equals(productDealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                		paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_正回购.getValue());
                		paTrdProductDeal.setBackLendDirection(DictCodeList.回购拆借方向_正回购.getValue());
                	}
                	else {
                		paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_逆回购.getValue());
                		paTrdProductDeal.setBackLendDirection(DictCodeList.回购拆借方向_逆回购.getValue());
                	}
                	paTrdProductDealMapper.insert(paTrdProductDeal);
					
                    // add 处理现金流信息 20210825 start
                    // 写入现金流相关信息表
                    //存续期不用登录
                    if(null!=finTradeProductMap.get(productDealDto.getTradeId())){
                        this.cashFlowLogic(productDealDto, finTradeProductMap.get(productDealDto.getTradeId()));
                    }
                    // add end
                }
                // 成交数据与投管交易关系
                if (StringUtil.isNotEmpty(productDealDto.getDealId())) {
                    List<String> tradeIdList = StringUtil.splitToList(",", productDealDto.getDealId());
                    Map<String, Object> paraMap = new HashMap<String, Object>();
                    paraMap.put("tradeIdList", tradeIdList);
                    paraMap.put("tradeIdTg", productDealDto.getTradeId());
                    paraMap.put("tgDataStatus", DictCodeListStl.投管交易数据状态_处理结束.getValue());
                    stlDataGatherMapper.updateSettleTransTgMapping(paraMap);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(ptlTradeList)) {
            for (PtlTrade ptlTrade : ptlTradeList) {
                // 组合交易表
                ptlTradeMapper.insertSelective(ptlTrade);
            }
        }

        if(CollectionUtils.isNotEmpty(tradeFeeDetailList)) {
            for (TrdTradeFeeDetail feeDetail : tradeFeeDetailList) {
                // 金融产品交易费用明细
                trdTradeFeeDetailMapper.insertSelective(feeDetail);

                // 费用交易汇总
                if (CollectionUtils.isNotEmpty(dealMap.get(feeDetail.getTradeId())) && CollectionUtils.isNotEmpty(finProductDealTotalList)){
                    TrdTradeFeeTotal trdTradeFeeTotal = new TrdTradeFeeTotal();
                    BeanUtil.copyProperties(trdTradeFeeTotal, feeDetail);
                    trdTradeFeeTotal.setTotleTradeId(feeDetail.getTradeId());
                    // 基金股票没有汇总交易
                    String totleTradeId = dealMap.get(feeDetail.getTradeId()).get(0).getTotleTradeId();
                    if (totleTradeId != null){
                        trdTradeFeeTotal.setTotleTradeId(dealMap.get(feeDetail.getTradeId()).get(0).getTotleTradeId());
                    }
                    trdTradeFeeTotalMapper.insertSelective(trdTradeFeeTotal);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(stlPayOrdersList)) {
            for (StlPayOrders stlPayOrders : stlPayOrdersList) {
                // 划款指令
                stlPayOrdersMapper.insertSelective(stlPayOrders);
            }
        }

        if(CollectionUtils.isNotEmpty(tradeProductList)) {
            for (FinTradeProductDto tradeProductDto : tradeProductList) {
                // 交易类金融产品表
                finTradeProductMapper.insertSelective(tradeProductDto);
                // 交易类金融产品附属表
                FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
                BeanUtil.copyProperties(finTradeProductAdd, tradeProductDto);
                finTradeProductAdd.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
                finTradeProductAddMapper.insert(finTradeProductAdd);
                // FIN_PRODUCT_TYPE(固定设置质押式竞价回购)
                FinProductType finProductType = new FinProductType();
                finProductType.setFinprodId(tradeProductDto.getFinprodId());
                finProductType.setBranch(0L);
                finProductType.setType1("02");
                finProductType.setType2("02");
                finProductType.setType3("G03");
                finProductType.setType6("H02");
                finProductType.setType7("04");
                finProductType.setType8("F07");
                finProductType.setType9("06");
                finProductType.setType10("44");
                finProductType.setType11("092");
                finProductType.setType12("094");
                // add bug: GNS-363 正回购清算生成交易后，组合实时资产头寸查询结果显示为逆回购 20210827 start
                List<TrdProductDealDto> dealList = dealMap.get(tradeProductDto.getFinprodId());
                if (!StringUtil.isEmptyList(dealList) && dealList.get(0) != null) {
                    TrdProductDealDto trdProductDealDto = dealList.get(0);
                    if (StringUtil.equals(trdProductDealDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                        finProductType.setType7("03");
                        finProductType.setType9("12");
                        finProductType.setType10("45");
                        finProductType.setType11("091");
                        finProductType.setType12("093");
                    }
                }
                // add end
                finProductTypeMapper.insert(finProductType);
            }
        }

        if(CollectionUtils.isNotEmpty(dealPledgeList)) {
            // 回购质押品
            for (FinDealPledge finDealPledge : dealPledgeList) {
                finDealPledgeMapper.insertSelective(finDealPledge);
            }
        }

        if(CollectionUtils.isNotEmpty(trdDealTotalAddList)) {
            // 汇总附属
            for (TrdDealTotalAdd trdDealTotalAdd : trdDealTotalAddList) {
                trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
            }
        }

        if(CollectionUtils.isNotEmpty(trdBarterTotalList)) {
            // 实物汇总
            for (TrdFinProductBarterTotal trdBarterTotal : trdBarterTotalList) {
                trdBarterTotalMapper.insertSelective(trdBarterTotal);
            }
        }

        if(CollectionUtils.isNotEmpty(trdBarterList)) {
            // 实物
            for (TrdFinProductBarter trdFinProductBarter : trdBarterList) {
                trdBarterMapper.insertSelective(trdFinProductBarter);
            }
        }

        // 日间委托完成度表有一笔交易编号等于自身委托编号的记录，这条记录每次正向跑批时需要实时更新
        if (CollectionUtils.isNotEmpty(etrFinishStatusList)){
            for (EtrFinishStatus etrFinishStatus : etrFinishStatusList){
                etrFinishStatus.setCreateTime(null);
                etrFinishStatus.setCreateTime(null);
                etrFinishStatus.setCreateDept(null);
                etrFinishStatusMapper.updateByPrimaryKeySelective(etrFinishStatus);
            }
        }

        // 成交编号业务编号关联表（只针对除权登记数据）
        if (CollectionUtils.isNotEmpty(settleTransTgMappingList)){
            for (SettleTransTgMapping settleTransTgMapping : settleTransTgMappingList){
                settleTransTgMappingMapper.insert(settleTransTgMapping);
            }
        }
        // 组合持仓流水
        if (CollectionUtils.isNotEmpty(ptlSecPositionFlowList)){
            for (PtlSecPositionFlow ptlSecPositionFlow : ptlSecPositionFlowList){
                // modify by etliu 20220308 start
                // 场内的头寸处理，全部已经对接了头寸引擎，需要标记下
                ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                // modify by etliu 20220308 end
                // modify by etliu 20220220 start
                // 广农商二阶段 GNS-880:【广农商场内清算】上交所配股上市流通前一日清算解析ZQBD文件后，交易和头寸未处理:组合持仓流水和指令持仓流水登录的都是对的，ETR_SEC_POSITION和PTL_SEC_POSITION未更新
//                ptlSecPositionFlowMapper.insert(ptlSecPositionFlow);
                PtlSecPositionFlowDto newPtlSecPositionFlow = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(newPtlSecPositionFlow, ptlSecPositionFlow);
                basePtlSecPositionInfoService.inertSecPositionFlow(newPtlSecPositionFlow);
                // modify by etliu 20220220 end
            }
        }
        // 指令持仓流水
        if (CollectionUtils.isNotEmpty(etrSecPositionFlowList)){
            // modify by etliu 20220220 start
            // 广农商二阶段 GNS-880:【广农商场内清算】上交所配股上市流通前一日清算解析ZQBD文件后，交易和头寸未处理:组合持仓流水和指令持仓流水登录的都是对的，ETR_SEC_POSITION和PTL_SEC_POSITION未更新
            List<EtrSecPositionFlowDto> newEtrSecPositionFlowList = new ArrayList<>();
            for (EtrSecPositionFlow etrSecPositionFlow : etrSecPositionFlowList){
                // modify by etliu 20220308 start
                // 场内的头寸处理，全部已经对接了头寸引擎，需要标记下
                etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                // modify by etliu 20220308 end
                etrSecPositionFlowMapper.insert(etrSecPositionFlow);

                EtrSecPositionFlowDto newEtrSecPositionFlow = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(newEtrSecPositionFlow, etrSecPositionFlow);
                newEtrSecPositionFlowList.add(newEtrSecPositionFlow);
            }

            EtrCommonCashPositionDealService.dealCommandPositionList(newEtrSecPositionFlowList);
            // modify by etliu 20220220 end
        }
    }

    private String getPaBussType(TotalTrdProductDealDto totalTrdProductDealDto, String secType,Map<String, FundInfoDto> fundInfoDtoMap) {
        String paBussType = null;
        String ps = totalTrdProductDealDto.getPs();
        String finprodId = totalTrdProductDealDto.getFinprodId();
        if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), secType)) {
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), ps)) {
                paBussType = DictCodeListExt1.平安业务类型_债券类资产买入.getValue();
            }
            else if (StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), ps)) {
                paBussType = DictCodeListExt1.平安业务类型_债券类资产卖出.getValue();
            }
            else if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                    || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps)) {
                paBussType = DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
            }
        }
        else if(StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), secType)) {
            if (DictCodeList.回购拆借方向_正回购.getValue().equals(ps)) {
                paBussType = DictCodeListExt1.平安业务类型_正回购.getValue();
            // ZGZGST-4263:回购存续期业务类型赋值有误 fuyongsheng 20211221 modify start
            // } else {
            //      paBussType = DictCodeListExt1.平安业务类型_逆回购.getValue();
            // }
            } else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(ps)) {
                paBussType = DictCodeListExt1.平安业务类型_逆回购.getValue();
            } else if(DictCodeList.交易方向_到期.getValue().equals(ps)){
                if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(totalTrdProductDealDto.getFinprodType2())){
                    paBussType = DictCodeListExt1.平安业务类型_正回购到期.getValue();
                }else{
                    paBussType = DictCodeListExt1.平安业务类型_逆回购到期.getValue();
                }
            }
            // ZGZGST-4263:回购存续期业务类型赋值有误 fuyongsheng 20211221 modify end
        }
        else if(StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), secType)) {
            FundInfoDto fundInfoDto = fundInfoDtoMap.get(finprodId);
            if(null != fundInfoDto) {
                String fundType = fundInfoDto.getType1();
                if (StringUtil.equals(fundType, DictCodeList.基金类型_货币基金.getValue())) {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_货币基金申购申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_货币基金买入.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_货币基金卖出.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeListExt2.交易方向_认购.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_货币基金认购申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeListExt2.交易方向_转换申请.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_货币基金赎回申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_红利再投.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_货币基金红利再投.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_现金分红.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_货币基金现金分红.getValue();
                    }
                }
                else {
                    if (StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金申购申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_非货币基金买入.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_赎回.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_非货币基金卖出.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeListExt2.交易方向_认购.getValue())) {
                        paBussType = DictCodeListExt3.平安业务类型_非货币基金认购申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeListExt2.交易方向_转换申请.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金赎回申请.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_现金分红.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金现金分红.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_红利再投.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金红利再投.getValue();
                    }
                    else if (StringUtil.equals(ps, DictCodeList.交易方向_除权登记.getValue())) {
                        paBussType = DictCodeListExt1.平安业务类型_非货币基金分红除权.getValue();
                    }
                }
            }
        }
        else if(StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), secType)) {
            // modify by etliu 20220219 start
            // GNS-875: 【广农商场内清算】股票现金分红，权益登记日日终上交所解析JSMX03文件；深交所解析SJSJG文件后，成交数据明细表、各交易表和头寸表数据登录不正确
            if(StringUtil.equals(totalTrdProductDealDto.getPayType(), DictCodeList.股票分红类型_送转.getValue())) {
                paBussType = DictCodeListExt2.平安业务类型_送转.getValue();
            }
            else if (StringUtil.equals(ps, DictCodeList.交易方向_现金分红.getValue())
                    || StringUtil.equals(ps, DictCodeList.交易方向_除权登记.getValue())) {
                paBussType = DictCodeListExt2.平安业务类型_现金分红.getValue();
            }else {
                paBussType = DictCodeListExt2.平安业务类型_股票交易.getValue();
            }
            // modify by etliu 20220219 end
        }
        return paBussType;
    }
    
    /**
     * 检索非货基现金分红对应的除权交易
     * @param paraMap Map<String, Object>
     * @return 非货基现金分红对应的除权交易
     */
    public List<TotalTrdProductDealDto> selectNhfTotalTrdProductDealDto(Map<String, Object> paraMap) {
    	return stlDataGatherMapper.selectNhfTotalTrdProductDealDto(paraMap);
    }
    
    /**
     * 根据交易编号查询汇总附属信息
     * 
     * @param tradeId 交易编号
     * @return 汇总附属信息
     * @see
     * @since 1.0
     */
    public TrdDealTotalAdd selectTotleDealByTotleId(String tradeId){
        return stlDataGatherMapper.queryTotalAddByTradeId(tradeId);
    }
    
    /**
     * 根据委托代码查询委托基本信息
     * 
     * @param entrustId 委托代码
     * @return 委托基本信息
     * @see
     * @since 1.0
     */
    public EtrEntrustAdd selectEtrEntrustByEntrustId(String entrustId){
    	return etrEntrustAddMapper.selectByPrimaryKey(entrustId);
    }
	
    /**
     * 写入现金流相关信息表
     *
     * @param input 画面参数Dto
     * @param finTradeProductDto 
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdProductDealDto input, FinTradeProductDto finTradeProductDto) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息Entity-本金
        FinCashInfo finCashInfoPrin = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashId(this.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_本金.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.本金
        finCashInfoPrin.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.本金
        finCashInfoPrin.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        // 币种 = 画面.币种
        finCashInfoPrin.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoPrin.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoPrin.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoPrin.setBranch(branchLong);
        // 工作流代码
        finCashInfoPrin.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoPrin);

        // 现金流信息Entity-利息
        FinCashInfo finCashInfoInt = new FinCashInfo();
        // 现金流代码 = 调用【各种业务共通组件.现金流代码生成】生成现金流代码。输入参数如下
        // 业务代码 = 金融产品交易Entity.交易编号
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashId(this.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                DictCodeListExt1.现金流二级类型_利息.getValue()));
        // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
        finCashInfoInt.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
        finCashInfoInt.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        // 币种 = 画面.币种
        finCashInfoInt.setCcy(input.getCcy());
        // 业务代码 = 金融产品交易Entity.交易编号
        finCashInfoInt.setFinprodId(input.getTradeId());
        // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
        finCashInfoInt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashInfoInt.setBranch(branchLong);
        // 工作流代码
        finCashInfoInt.setWorkflowId(input.getWorkflowId());
        finCashInfoList.add(finCashInfoInt);

        inputDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashPayRule.setEffDate(input.getVdate());
        // 支付频率 = 数据字典缓存.支付频率(付息频率).到期支付

        // 一体化对应etliu start
//        finCashPayRule.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
        // 一体化对应etliu end

        // 一体化对应etliu
//        finCashPayRule.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
        finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());

        // 首次支付日 = 画面.到期日
        finCashPayRule.setFirstPayDate(input.getMdate());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPayRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPayRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPayRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashPayRule.setBranch(branchLong);
        // 工作流代码
        finCashPayRule.setWorkflowId(input.getWorkflowId());
        finCashPayRuleList.add(finCashPayRule);

        inputDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity>
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();

        // 现金流计算规则表Entity-利息
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashCalcRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashCalcRule.setEffDate(input.getVdate());
        // 公布日期 = 画面.起息日
        // 一体化对应etliu
//        finCashCalcRule.setRelaeseDate(dto.getVdate()); // 公布日期
        // 计算类型 = 数据字典缓存.利率类型.固定利率
        finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
        // 基数类型 = 数据字典缓存.基数类型.本金
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        // 基数日期类型 = 数据字典缓存.基数日期类型.当日
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        // 计算基础 = 画面.计息基础
        finCashCalcRule.setBasis(finTradeProductDto.getBasis());
        // 生效利率 = 画面.回购利率/100
        finCashCalcRule.setYield(finTradeProductDto.getIntRate());
        // 是否初始利率 = 数据字典缓存.是否.是
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashCalcRule.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashCalcRule.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashCalcRule.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finCashCalcRule.setBranch(branchLong);
        // 工作流代码
        finCashCalcRule.setWorkflowId(input.getWorkflowId());
        finCashCalcRuleList.add(finCashCalcRule);

        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // 现金流利率信息表Entity-利息
        FinRateInfo finRateInfo = new FinRateInfo();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finRateInfo.setCashId(finCashInfoInt.getCashId());
        // 生效日 = 画面.起息日
        finRateInfo.setEffDate(input.getVdate());
        // 利率类型 = 数据字典缓存.利率类型.固定利率
        // 一体化对应etliu
//        finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
        finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
        // 计算基础 = 画面.计息基础
        finRateInfo.setBasis(finTradeProductDto.getBasis());
        // 初始利率 = 画面.回购利率/100
        finRateInfo.setRate(finCashCalcRule.getYield());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finRateInfo.setFinprodId(input.getTradeId());
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finRateInfo.setBranch(branchLong);
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finRateInfo.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finRateInfo.setFinprodType2(input.getFinprodType2());
        // 工作流代码
        finRateInfo.setWorkflowId(input.getWorkflowId());
        finRateInfoList.add(finRateInfo);

        inputDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();

        // 现金流计划表Entity 到期还本
        FinCashPlan finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-本金.现金流代码
        finCashPlan.setCashId(finCashInfoPrin.getCashId());
        // 现金流序号 = 1
        finCashPlan.setCashNum(1L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期还本
        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算结束日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 画面.本金金额
        finCashPlan.setPrinAmt(input.getPrinAmt());
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getPrinAmt());
        // 计划支付利息 = 0
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        // 是否支付利息 = 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 画面.本金金额
        finCashPlan.setCashAmt(input.getPrinAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        // 现金流计划表Entity 到期付息
        finCashPlan = new FinCashPlan();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPlan.setCashId(finCashInfoInt.getCashId());
        // 现金流序号 = 2
        finCashPlan.setCashNum(2L);
        // 现金流类型 = 数据字典缓存.现金流类型.到期付息
        finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
        // 未调整的计算开始日 = 画面.起息日
        finCashPlan.setVdateUnadjust(input.getVdate());
        // 未调整的计算开始日 = 画面.到期日
        finCashPlan.setMdateUnadjust(input.getMdate());
        // 未调整的计划支付日 = 画面.到期日
        finCashPlan.setPayDateUnadjust(input.getMdate());
        // 调整后的计算开始日 = 画面.起息日
        finCashPlan.setVdate(input.getVdate());
        // 调整后的计算结束日 = 画面.到期日
        finCashPlan.setMdate(input.getMdate());
        // 调整后的计划支付日 = 画面.到期日
        finCashPlan.setPayDate(input.getMdate());
        // 计算周期天数 = 画面.到期日-画面.起息日
        finCashPlan.setTermdays(DateUtil.getBetweenDayNumber(input.getVdate(), input.getMdate()));
        // 计划支付本金 = 0
        finCashPlan.setPrinAmt(BigDecimal.ZERO);
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(input.getPrinAmt());
        // 计划支付利息 = 回购利息 ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(this.calc投资交易计算回购利息(input.getPrinAmt(),finTradeProductDto.getIntRate(),
                input.getVdate(), input.getMdate(), finTradeProductDto.getBasis()));
        // 是否支付利息 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 回购利息
        finCashPlan.setCashAmt(finCashPlan.getIntAmt());
        // 现金流单位 = 数据字典缓存.现金流单位.总额
        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finCashPlan.setFinprodId(input.getTradeId());
        // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
        finCashPlan.setFinprodType(input.getFinprodType());
        // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finCashPlan.setFinprodType2(input.getFinprodType2());
        // 分支序号 = 固定值，系统参数
        finCashPlan.setBranch(branchLong);
        // 支付方式 = 数据字典缓存.分红方式.现金分红
        finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
        // 工作流代码
        finCashPlan.setWorkflowId(input.getWorkflowId());
        finCashPlanList.add(finCashPlan);

        inputDto.setFinCashPlanList(finCashPlanList);

        if (isSingle) {
            simpleRestTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                            + "/common/trdcommonbase/insertFinProductCashData",
                    inputDto, Boolean.class);
        } else {
            restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                            + "/common/trdcommonbase/insertFinProductCashData",
                    inputDto, Boolean.class); }
    }
    
    /**
     * 现金流代码生成
     *
     * @param busiNo 业务代码
     * @param busiType 业务类型
     * @param branch 分支序号
     * @param busiType2 现金流二级类型
     * @return 现金流代码
     */
    public String generateCashFlowCode(String busiNo, String busiType, long branch, String busiType2) {
        return CommonUtil.generateCashFlowCode(busiNo, busiType, branch, busiType2);
    }
    
    /**
     * 投资交易计算回购利息
     * 回购利息=本金金额*回购利率*回购期限*计息基础
     *
     * @param 本金金额
     * @param 回购利率
     * @param 起息日
     * @param 到期日
     * @param 计息基础
     * @return 回购利息
     * @see
     * @since 1.0
     */
    public BigDecimal calc投资交易计算回购利息(BigDecimal 本金金额, BigDecimal 回购利率, Date 起息日, Date 到期日,
                                     String 计息基础) {
        BigDecimal 回购利息 = null;

        int num_计息基础 = 365;
        if (StringUtil.equals(DictCodeList.计息基础_A_360.getValue(), 计息基础)) {
            num_计息基础 = 360;
        }

        long 回购期限 = DateUtil.getBetweenDayNumber(起息日, 到期日);

        // 回购利息=本金金额*回购利率*回购期限*计息基础
        // 小数点保留2位
        回购利息 = ArithmeticUtil.multiply(本金金额, 回购利率, BigDecimal.valueOf(回购期限));
        回购利息 = ArithmeticUtil.divide(回购利息, BigDecimal.valueOf(num_计息基础), 2);

        return 回购利息;
    }

    public void transactionMacthBatch(List<ExchangeTransMatchCommonDto> dtoList,
                                      List<String> needUpdateStatusEntrustIdList) {
        ExchangeTransMatchCommonParamDto paramDto = new ExchangeTransMatchCommonParamDto();
        paramDto.setParamDataList(dtoList);
        paramDto.setNeedUpdateStatusEntrustIdList(needUpdateStatusEntrustIdList);
        logger.debug("调用成交匹配:" + JsonUtils.object2Json(paramDto));
        if(CollectionUtils.isEmpty(dtoList)){
            return;
        }
        // 调用债券交易计算逻辑
        if(isSingle) {
            simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/exchangetransmatch/ExchangeTransMatchCommon/transactionMacthBatch", paramDto, Map.class);
        }
        else {
            restTemplate.postForObject(tamsSerever + "/invest/trade/exchangetransmatch/ExchangeTransMatchCommon/transactionMacthBatch", paramDto, Map.class);
        }

    }

    public List<SettleTransDataDto> listBondSettleTransData(Map<String, Object> paraMap){
        return stlDataGatherMapper.listBondSettleTransData(paraMap);
    }

    /**
     * 查询竞价回购存续期明细数据
     * @param paraMap
     * @return
     */
    public List<SettleTransDataDto> listRepoSettleTransData(Map<String, Object> paraMap){
        return stlDataGatherMapper.listRepoSettleTransData(paraMap);
    }

    /**
     * 查询协议回购到期明细数据
     * @param paraMap
     * @return
     */
    public List<SettleTransDataDto> listRepoH01MaturitySettleTransData(Map<String, Object> paraMap){
        return stlDataGatherMapper.listRepoH01MaturitySettleTransData(paraMap);
    }

    /**
     * 查询债券确认列表
     * @param paraMap
     * @return
     */
    public List<SettleTransDataDto> listBondPurConfirSettleTransData(Map<String, Object> paraMap){
        return stlDataGatherMapper.listBondPurConfirSettleTransData(paraMap);
    }

    public List<SettleTransDataDto> listBoundSharesMarketTradeData(Map<String, Object> paraMap) {
        return stlDataGatherMapper.listBoundSharesMarketTradeData(paraMap);
    }
}
