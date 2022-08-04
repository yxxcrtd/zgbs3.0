package com.joyintech.service.trade.manualsettle.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.service.trade.entrustmsgbymq.dao.EntrustMsgbyMQMapper;
import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;
import com.joyintech.service.trade.entrustmsgbymq.service.TradePreClearingService;
import com.joyintech.service.trade.entrustmsgbymq.service.TradePreCommonService;
import com.joyintech.service.trade.exchange.dto.Entrust;
import com.joyintech.service.trade.manualsettle.dao.ManualSettleMapper;
import com.joyintech.service.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.service.trade.manualsettle.dto.ManualSettleFlowDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.common.base.pojo.PledgeListDto;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.position.dto.PostionParameter;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhangnian
 * @ClassName ManualSettleService
 * @description TODO
 * @date 2020/10/22 0022 11:12
 **/
@Service("backManualSettleService")
public class ManualSettleService extends BaseService<ManualSettleDto> {

    private final static Logger logger = LoggerFactory.getLogger(ManualSettleService.class);

    // 上交所交易日历ID
    public static final String STL_DOWNDATA_DEAL_SHJYS_CALID = "STL_DOWNDATA_DEAL_SHJYS_CALID";
    // 深交所交易日历ID
    public static final String STL_DOWNDATA_DEAL_SZJYS_CALID = "STL_DOWNDATA_DEAL_SZJYS_CALID";

    @Autowired
    private ManualSettleMapper manualSettleMapper;

    @Autowired
    private TrdManualSettleFlowMapper trdManualSettleFlowMapper;
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    @Autowired
    private TrdProductTempDealMapper trdProductTempDealMapper;

    @Autowired
    private TrdProductTempDealAddMapper trdProductTempDealAddMapper;

    @Autowired
    private StlPayTempOrdersMapper stlPayTempOrdersMapper;

    @Autowired
    private EntrustMsgbyMQMapper entrustMsgbyMQMapper;

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private TradePreClearingService tradePreClearingService;

    @Autowired
    private TradePreCommonService tradePreCommonService;

    @Autowired
    private TrdManualSettleInfoMapper trdManualSettleInfoMapper;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private CommonPositionService commonPositionService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private DateCalService dateCalService;

    public Page<ManualSettleDto> selectForPage(int page, int length, Map<String, Object> pageParam)
            throws Exception {
        Page<ManualSettleDto> pageDto = super.queryForPage(page, length, () -> manualSettleMapper.queryForPage(pageParam));
        return pageDto;
    }


    public List<ManualSettleFlowDto>  queryForFlowPage(Map<String,Object> param){
        return manualSettleMapper.queryForFlowPage(param);
    }

    public void settleCash(ManualSettleDto dto){
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getEntrustNo());
        //临时交易表
        List<TrdProductTempDeal> trdProductTempDeals = new ArrayList<>();
        if(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue().equals(etrEntrust.getEntrustClass())){ // 多组合指令
            trdProductTempDeals.addAll(manualSettleMapper.selectTrdProductTempDeals(dto.getTradeId()));
        }else{
            trdProductTempDeals.add(trdProductTempDealMapper.selectByPrimaryKey(dto.getTradeId()));
        }
        if(CollectionUtils.isEmpty(trdProductTempDeals)){
            return;
        }
        for(TrdProductTempDeal trdProductTempDeal : trdProductTempDeals){
            //交收、回溯处头寸逻辑
            if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收.getValue())||
                    StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_回溯.getValue())){
                // 处理交收或者回溯数据
                this.handleSettleSuccessOrRecall(dto, trdProductTempDeal);
            }
            else if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收失败.getValue())){
                this.handleSettleFailed(dto, trdProductTempDeal);
            }
        }
    }

    /**
     * 处理交收或者回溯数据
     * @param dto
     */
    private void handleSettleSuccessOrRecall(ManualSettleDto dto, TrdProductTempDeal trdProductTempDeal){
            //临时交易表
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, trdProductTempDeal);
            //临时交易附属表
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            TrdProductTempDealAdd trdProductTempDealAdd = trdProductTempDealAddMapper.selectByPrimaryKey(trdProductTempDeal.getTradeId());
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, trdProductTempDealAdd);
            StlPayOrdersDto stlPayOrdersDto = new StlPayOrdersDto();
            //划款指令表
            StlPayTempOrders stlPayTempOrders = manualSettleMapper.selectStlPayOrders(trdProductTempDeal.getTradeId());
            if (null!=stlPayTempOrders) {
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDto, stlPayTempOrders);
            }
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收.getValue())){
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            }else if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_回溯.getValue())){
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            // 是否来自场内交易下单成交标识
            baseTradeProductDealDto.setIsFromExchange(DictCodeList.是否_是.getValue());
            // 划款指令
            if (stlPayOrdersDto != null) {
                baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(stlPayOrdersDto));
            }
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
            finProductTradeDto.setNeedSum(true);
            String assetType2 = entrustMsgbyMQMapper.queryAssetType1(dto.getEntrustNo());
            finProductTradeDto.setAssetType2(assetType2);
            // 委托代码不为空处理委托代码
            finProductTradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                String entrustId = dto.getEntrustNo();
                if (StringUtil.isEmpty(item.getEntrustId()) && StringUtil.isNotEmpty(entrustId)) {
                    item.setEntrustId(entrustId);
                }
            });

            //质押式回购 持仓流水
            if(!dto.getBusiType().equals("01") // 上交所固定收益平台债券买入
                    &&!dto.getBusiType().equals("02") // 上交所固定收益平台债券卖出
                    &&!dto.getBusiType().equals("03") // 深交所综合协议平台债券买入
                    &&!dto.getBusiType().equals("04") // 深交所综合协议平台债券卖出
                    // ZGCPCN-1001:新增场内现券买入卖出 modify by fuyongsheng 20220531 start
                    &&!dto.getBusiType().equals("25") // 场内现券买入
                    &&!dto.getBusiType().equals("26") // 场内现券卖出
                    &&!dto.getBusiType().equals("27") // 信用保护凭证买入
                    &&!dto.getBusiType().equals("28") // 信用保护凭证卖出
                    // ZGCPCN-1001:新增场内现券买入卖出 modify by fuyongsheng 20220531 end
            ){
                if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收.getValue())) {
                    InfContractDetailfile infContractDetailfile = manualSettleMapper.selectInfContractDetailfile(dto.getEntrustNo(), dto.getDealNo());
                    TrdTempDto trdTempDto = new TrdTempDto();
                    trdTempDto.setTradeId(trdProductTempDeal.getTradeId());
                    if (stlPayOrdersDto != null) {
                        trdTempDto.setOrderId(stlPayOrdersDto.getOrderId());
                    }

                    // modify by etliu 20220725 start
                    // 3.0.6协议回购+风险升级 XYHGFXSJ-368
                    //【实时资产头寸查询】协议回购上交所正回购数据日间交收后，质押券的指令可用持仓、交易可用持仓、质押持仓显示错误
//                    infContractDetailfile.setPortfolioId(trdProductTempDealAdd.getPortfolioId());
//                    infContractDetailfile.setShareAmt(trdProductTempDeal.getShareAmt());
//                    infContractDetailfile.setTradeAmt(trdProductTempDeal.getTradeAmt());
                    // modify by etliu 20220725 end

                    // XYHGFXSJ-8:构建质押品信息list modify by fuyongsheng 20220718 start
                    if(DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())){
                        // XYHGFXSJ-724 【实时资产头寸查询】协议回购上交所正回购中途换券数据日间交收后，实时资产里的质押券没有变更
                        infContractDetailfile.setPledgeList(manualSettleMapper.selectFinDealPledgeByTradeId2(trdProductTempDeal.getFinprodId()));
                        // XYHGFXSJ-724 【实时资产头寸查询】协议回购上交所正回购中途换券数据日间交收后，实时资产里的质押券没有变更
                    }else {
                        infContractDetailfile.setPledgeList(this.genPledgeDtoList(trdProductTempDeal.getFinprodId()));
                    }
                    // XYHGFXSJ-8:构建质押品信息list modify by fuyongsheng 20220718 end
                    if (DictCodeList.回购拆借方向_正回购.getValue().equals(infContractDetailfile.getPs())
                            || DictCodeList.回购拆借方向_逆回购.getValue().equals(infContractDetailfile.getPs())) {
                        // 指令信息处理
                        tradePreClearingService.buildPositionBaseData(trdTempDto, infContractDetailfile);
                        // modify by etliu 20220720 start
                        // 3.0.6协议回购+风险升级 XYHGFXSJ-143
                        //【现金流缺口分析】协议式上交所正回购数据进行手工交收后，现金流缺口分析里没有对应的值，组合资金账户头寸查询的明细里没有值
                        if (StringUtil.isNotBlank(stlPayOrdersDto.getOrderId())) {
                            trdTempDto.setOrderId(stlPayOrdersDto.getOrderId());
                        }
                        // modify by etliu 20220720 end
                        // 临时交易及其他数据处理
                        tradePreClearingService.trdRepo(trdTempDto, infContractDetailfile, infContractDetailfile.getPs());
                    }
                    else if (DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())
                            || DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())
                            || DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())
                            || DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())) {
                        // 指令信息处理
                        tradePreClearingService.buildPositionBaseData(trdTempDto, infContractDetailfile);
                        // modify by etliu 20220720 start
                        // 3.0.6协议回购+风险升级 XYHGFXSJ-143
                        //【现金流缺口分析】协议式上交所正回购数据进行手工交收后，现金流缺口分析里没有对应的值，组合资金账户头寸查询的明细里没有值
                        if (StringUtil.isNotBlank(stlPayOrdersDto.getOrderId())) {
                            trdTempDto.setOrderId(stlPayOrdersDto.getOrderId());
                        }
                        // modify by etliu 20220720 end
                        // 临时交易及其他数据处理
                        tradePreClearingService.trdRepoPeriod(trdTempDto, infContractDetailfile);

                        // modify by etliu 20220727 start
                        // 3.0.6协议回购+风险升级 XYHGFXSJ-461
                        //【现金流缺口分析】协议式上交所正回购提前购回数据，日间交收后，现金流缺口分析、组合未来头寸分析、实时资产头寸查询报表显示错误
                        if (DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())
                                || DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())
                                || DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())) {
                            List<ManualSettleDto> tepmPledgeInfoList = manualSettleMapper.selectPledgeInfo(infContractDetailfile.getFinprodId());
                            if (!StringUtil.isEmptyList(tepmPledgeInfoList)) {
                                ((InfContractDetailfile) trdTempDto.getData()).setFinprodId(tepmPledgeInfoList.get(0).getPledgeId());
                                trdTempDto.setTradeAmt(tepmPledgeInfoList.get(0).getPrin());
                            }
                        }
                        // modify by etliu 20220727 end
                    }
                    else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(infContractDetailfile.getPs())){
                        // 指令信息处理
                        tradePreClearingService.buildPositionBaseData(trdTempDto, infContractDetailfile);
                        // 临时交易及其他数据处理
                        tradePreClearingService.trdRepoPeriodT56(trdTempDto, infContractDetailfile, true);

                        // modify by etliu 20220727 start
                        // 3.0.6协议回购+风险升级 XYHGFXSJ-461
                        //【现金流缺口分析】协议式上交所正回购提前购回数据，日间交收后，现金流缺口分析、组合未来头寸分析、实时资产头寸查询报表显示错误
                        List<ManualSettleDto> tepmPledgeInfoList = manualSettleMapper.selectPledgeInfo(infContractDetailfile.getFinprodId());
                        if (!StringUtil.isEmptyList(tepmPledgeInfoList)) {
                            ((InfContractDetailfile) trdTempDto.getData()).setFinprodId(tepmPledgeInfoList.get(0).getPledgeId());
                            trdTempDto.setTradeAmt(tepmPledgeInfoList.get(0).getPrin());
                        }
                        // modify by etliu 20220727 end
                    }

                    // modify by etliu 20220227 start
                    // 广农商二阶段 GNS-887:【广农商场内清算】深交所协议回购正回购逆回购首期日终结算生成交易后，前台实时资产头寸查不到数据
                    if (StringUtil.isBlank(trdTempDto.getSecManageAcctId())) {
                        trdTempDto.setSecManageAcctId(trdProductDeal.getSecManageAcctId());
                    }
                    // modify by etliu 20220227 end

                    // 场景参数设置
                    PostionParameter parameter = new PostionParameter();
                    parameter.setPs(trdTempDto.getPs());
                    parameter.setTradeMarket(trdTempDto.getTradeMarket());
                    parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                    parameter.setOperationType("NEW");
                    parameter.setTradeType("02");
                    trdTempDto.setFinprodType2(etrEntrustMapper.selectByPrimaryKey(trdTempDto.getEntrustId()).getFinprodType2());
                    parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue().concat(trdTempDto.getFinprodType2()));
                    // 数据来源设置
                    if (StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
    //                    InfContractDetailfile infContractDetailfile = (InfContractDetailfile)trdTempDto.getData();
                        BigDecimal faceValue = trdCommonBaseService.getFaceValue(infContractDetailfile.getFinprodId(), infContractDetailfile.getTradeDate());
                        trdTempDto.setTradeAmt((faceValue == null ? BigDecimal.valueOf(100) : faceValue).multiply(infContractDetailfile.getShareAmt()));
                    }
                    trdTempDto.setIsFinish(DictCodeList.是否_是.getValue());
                    trdTempDto.setTradeDate(DateUtil.platDateFromCache());
                    if (StringUtil.equals(trdTempDto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue()) && StringUtil.isNotEmpty(trdTempDto.getRedoType())) {
                        parameter.setPs(parameter.getPs() + trdTempDto.getRedoType());
                    }

                    // 中途换券换钱特殊处理
                    if (StringUtil.equals(trdTempDto.getPs(),DictCodeListExt3.交易方向_中途换券.getValue())
                            // 中途换券的换钱tradeAmt不为空
                            && StringUtil.isNotEmpty(trdTempDto.getTradeAmt()) &&
                            StringUtil.equals(infContractDetailfile.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                        parameter.setPs(parameter.getPs() + "_" + DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue());
                    }

                    // add by etliu 20220116 start
                    // GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
                    if (StringUtil.isNotBlank(trdTempDto.getFinprodId())) {
                        trdTempDto.setPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinprodId()));
                    }
                    if (trdTempDto.getFinDealPledge() != null && StringUtil.isNotBlank(trdTempDto.getFinDealPledge().getPledgeFinprodId())) {
                        trdTempDto.setFinDealPledgePledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinDealPledge().getPledgeFinprodId()));
                    }
                    if (trdTempDto.getData() != null && StringUtil.isNotBlank(((InfContractDetailfile) trdTempDto.getData()).getFinprodId())) {
                        trdTempDto.setDataPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), ((InfContractDetailfile) trdTempDto.getData()).getFinprodId()));
                    }
                    if (!StringUtil.isEmptyList(trdTempDto.getFinPledgeDispDetailTempList())
                            && StringUtil.isNotBlank(trdTempDto.getFinPledgeDispDetailTempList().get(0).getFinprodId())) {
                        trdTempDto.setFinPledgeDispDetailPledgeInvAim(commonPositionService.selectHoldInvAim(trdTempDto.getPortfolioId(), trdTempDto.getFinPledgeDispDetailTempList().get(0).getFinprodId()));
                    }
                    // add by etliu 20220116 end

                    parameter.setCustom(trdTempDto);
//                    try {
                        logger.info("手工交收处理请求报文:" + JsonUtils.object2Json(parameter));
                        commonPositionService.process(parameter);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        logger.error("手工交收处理请求失败:", e);
//                    }
//                    // 3.调用基础组件.【投资委托组件.完成委托】，写入委托完成状况信息(投资委托场合才执行此步)
//                    if (!finProductTradeDto.isNeedHandleFinishEntrust()) {
//                        baseTradeProductDealCommService.finishEntrust(finProductTradeDto.getBaseTradeProductDealDtos(), false);
//                    }
                }else{
                    List<PtlCashPosition> ptlCashPositions = manualSettleMapper.listPtlCashPositionFlow(stlPayOrdersDto.getOrderId());
                    List<PtlSecPosition> ptlSecPositions = manualSettleMapper.listPtlSecPositionFlow(trdProductDeal.getTradeId());
                    List<EtrCashPosition> etrCashPositions = manualSettleMapper.listEtrCashPositionFlow(stlPayOrdersDto.getOrderId());
                    List<EtrSecPosition> etrSecPositions = manualSettleMapper.listEtrSecPositionFlow(trdProductDeal.getTradeId());

                    // 删除日间委托完成度
                    manualSettleMapper.deleteEtrFinishStatus(trdProductDeal.getTradeId());
                    //更新余额
                    ptlCashPositions.stream().forEach(item ->{
                        if(null!=item.getAmount()){
                            item.setAmount(item.getAmount().negate());
                            manualSettleMapper.updatePtlCashPosition(item);
                        }
                    });
                    ptlSecPositions.stream().forEach(item ->{
                        if(null!=item.getAmount()){
                            item.setAmount(item.getAmount().negate());
                            manualSettleMapper.updatePtlSecPosition(item);
                        }
                    });
                    etrCashPositions.stream().forEach(item ->{
                        if(null!=item.getAmount()){
                            item.setAmount(item.getAmount().negate());
                            manualSettleMapper.updateEtrCashPosition(item);
                        }
                    });
                    etrSecPositions.stream().forEach(item ->{
                        if(null!=item.getAmount()){
                            item.setAmount(item.getAmount().negate());
                            manualSettleMapper.updateEtrSecPosition(item);
                        }
                    });
                }

            }else {
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
                finProductTradeDto.setNeedHandleFinishEntrust(true);
                // ZGCPCN-198:手工交收处理头寸系列户多组合指令需拆分 modify by fuyongsheng 20220425 start
                // baseTradeProductDealCommService.execute(finProductTradeDto);
                TrdManualSettleInfo trdManualSettleInfo = trdManualSettleInfoMapper.selectByPrimaryKey(dto.getSettleSeqNo());
                tradePreCommonService.executePosition(finProductTradeDto, trdManualSettleInfo.getAcctNo());
                // ZGCPCN-198:手工交收处理头寸系列户多组合指令需拆分 modify by fuyongsheng 20220425 end
            }
            //针对回溯  删除交收及回溯的流水
            if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_回溯.getValue())){
                if (stlPayOrdersDto != null&&StringUtil.isNotEmpty(stlPayOrdersDto.getOrderId())) {
                    manualSettleMapper.deletePtlCashFlow(stlPayOrdersDto.getOrderId());
                    manualSettleMapper.deleteEtrCashFlow(stlPayOrdersDto.getOrderId());
                }
                if(StringUtil.isNotEmpty(trdProductDeal.getTradeId())){
                    manualSettleMapper.deleteEtrSecFlow(trdProductDeal.getTradeId());
                    manualSettleMapper.deletePtlSecFlow(trdProductDeal.getTradeId());
                    // modify by etliu 20220226 start
                    // 广农商二阶段 GNS-970:【广农商场内清算】手工交收回溯的头寸，在日终清结算撤销批时未删除
                    manualSettleMapper.deletePtlCashFlow(trdProductDeal.getTradeId());
                    manualSettleMapper.deleteEtrCashFlow(trdProductDeal.getTradeId());
                    // modify by etliu 20220226 end
                }
            }
    }

    // XYHGFXSJ-8:构建质押品信息list modify by fuyongsheng 20220718 start
    /**
     * 构建质押品信息list
     * @param tradeId
     * @return
     */
    private List<PledgeListDto> genPledgeDtoList(String tradeId){
        List<PledgeListDto> pledgeListDtos = new ArrayList<>();
        List<FinDealPledge> finDealPledgeList = manualSettleMapper.selectFinDealPledgeByTradeId(tradeId);
        if(!CollectionUtils.isEmpty(finDealPledgeList)){
            for(FinDealPledge finDealPledge : finDealPledgeList){
                PledgeListDto pledgeListDto = new PledgeListDto();
                pledgeListDto.setFinprodMarketId(finDealPledge.getPledgeFinprodId());
                pledgeListDto.setPledgeNumber(finDealPledge.getPledgeNumber());
                pledgeListDtos.add(pledgeListDto);
            }
        }
        return pledgeListDtos;
    }
    // XYHGFXSJ-8:构建质押品信息list modify by fuyongsheng 20220718 end

    /**
     * 处理交收失败数据
     * @param dto
     */
    private void handleSettleFailed(ManualSettleDto dto, TrdProductTempDeal trdProductTempDeal){
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            //查询指令预占现金数据
            List<EtrCashPositionFlowDto> etrCashPositionFlows =manualSettleMapper.selectEtrCashPositionFlow(dto.getEntrustNo());
            //查询指令预占头寸数据
            List<EtrSecPositionFlowDto> etrSecPositionFlows = manualSettleMapper.selectEtrSecPositionFlow(dto.getEntrustNo());
            //update by yzg 20210528 start 头寸只会生成一条
            EtrSecPositionFlowDto etrSecPositionFlowDto = new EtrSecPositionFlowDto();
            EtrCashPositionFlowDto etrCashPositionFlowDto = new EtrCashPositionFlowDto();
            if(!StringUtil.isEmptyList(etrSecPositionFlows)) {
            	etrSecPositionFlowDto = etrSecPositionFlows.get(0);
            }
            if(!StringUtil.isEmptyList(etrCashPositionFlows)) {
            	etrCashPositionFlowDto = etrCashPositionFlows.get(0);
            }
            List<EtrCashPositionFlowDto> etrCashPositionFlowDtos = new ArrayList<EtrCashPositionFlowDto>();
            List<EtrSecPositionFlowDto> etrSecPositionFlowDtos = new ArrayList<EtrSecPositionFlowDto>();

            // modify by etliu 20220228 start
            // 广农商二阶段 GNS-973:【广农商场内清算】深交所协议回购 正/逆回购 提前/到期购回，点击交收失败后，头寸更新有误
            // modify by etliu 20220228 end
            if(etrCashPositionFlows!=null&&!etrCashPositionFlows.isEmpty()){
                etrCashPositionFlowDto.setSeqno(IDGenerator.generate());
                etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache());
                //划款指令表
                StlPayTempOrders stlPayTempOrders = manualSettleMapper.selectStlPayOrders(trdProductTempDeal.getTradeId());
                etrCashPositionFlowDto.setOrderId(stlPayTempOrders.getOrderId());
                // modify by etliu 20220228 start
                // 广农商二阶段 GNS-973:【广农商场内清算】深交所协议回购 正/逆回购 提前/到期购回，点击交收失败后，头寸更新有误
                if (trdProductTempDeal != null &&
                        !StringUtil.equals(DictCodeListExt3.交易方向_提前购回.getValue(), trdProductTempDeal.getPs())
                        && !StringUtil.equals(DictCodeListExt3.交易方向_到期购回.getValue(), trdProductTempDeal.getPs())
                        //XYHGFXSJ-821 【实时资产头寸查询】深交所正回购首期，场内日间交收失败，债券指令可用持仓回冲的流水错误
                        && !StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), trdProductTempDeal.getPs()
                        //XYHGFXSJ-821 【实时资产头寸查询】深交所正回购首期，场内日间交收失败，债券指令可用持仓回冲的流水错误
                )) {
                    etrCashPositionFlowDto.setAmount(trdProductTempDeal.getTradeAmt());
                    if (etrCashPositionFlowDto.getAmount() == null) {
                        etrCashPositionFlowDto.setAmount(trdProductTempDeal.getPrinAmt());
                    }
                } else {
                    etrCashPositionFlowDto.setAmount(etrCashPositionFlowDto.getAmount().negate());
                }
                // modify by etliu 20220228 end
            	etrCashPositionFlowDtos.add(etrCashPositionFlowDto);
            }
            if(etrSecPositionFlows!=null&&!etrSecPositionFlows.isEmpty()){
                etrSecPositionFlowDto.setSeqno(IDGenerator.generate());
                etrSecPositionFlowDto.setCdate(DateUtil.platDateFromCache());
                etrSecPositionFlowDto.setTradeId(trdProductTempDeal.getTradeId());

                // modify by etliu 20220228 start
                // 广农商二阶段 GNS-973:【广农商场内清算】深交所协议回购 正/逆回购 提前/到期购回，点击交收失败后，头寸更新有误
                if (trdProductTempDeal != null &&
                        !StringUtil.equals(DictCodeListExt3.交易方向_提前购回.getValue(), trdProductTempDeal.getPs())
                        && !StringUtil.equals(DictCodeListExt3.交易方向_到期购回.getValue(), trdProductTempDeal.getPs())
                        //XYHGFXSJ-821 【实时资产头寸查询】深交所正回购首期，场内日间交收失败，债券指令可用持仓回冲的流水错误
                        && !StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), trdProductTempDeal.getPs()
                        //XYHGFXSJ-821 【实时资产头寸查询】深交所正回购首期，场内日间交收失败，债券指令可用持仓回冲的流水错误
                )) {
                    etrSecPositionFlowDto.setAmount(trdProductTempDeal.getShareAmt());
                    if (etrSecPositionFlowDto.getAmount() == null) {
                        etrSecPositionFlowDto.setAmount(dto.getPledgeNumber());
                    }
                } else {
                    etrSecPositionFlowDto.setAmount(etrSecPositionFlowDto.getAmount().negate());
                }
            	// modify by etliu 20220228 end
            	etrSecPositionFlowDtos.add(etrSecPositionFlowDto);
            }
           //update by yzg 20210528 end

            etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlowDtos);
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlowDtos);
            etrPositionHoldDto.setDealEtr(true);
            EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
    }

    public boolean recallDelegate(ManualSettleDto dto)
            throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        BeanUtil.copyProperties(etrEntrust, dto);
        return baseEtrInvestInfoService.recallDelegate(entity);
    }

    public void settle(ManualSettleDto dto){
        //处理流水
        try{
            //DateFormat df = DateFormat.getTimeInstance();//只显示出时分秒
            //String hms = df.format(new Date());
            TrdManualSettleFlow flow = new TrdManualSettleFlow();
            flow.setSeqNo(UUID.randomUUID().toString().replace("-", ""));
            flow.setSettleSeqNo(dto.getSettleSeqNo());
            //时间拼接
            //String[] partsDate = DateUtil.format(dto.getSettleDate()).split("-");
            //String[] partsTime = hms.split(":");
            //GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(partsDate[0]),Integer.parseInt(partsDate[1]),Integer.parseInt(partsDate[2]),Integer.parseInt(partsTime[0]),Integer.parseInt(partsTime[1]),Integer.parseInt(partsTime[2]));
            // ZGCPCN-86:交收日期需要使用平台日的准确时间,通过平台日的日期拼接当前时间的时分秒 modify by fuyongsheng 20220420 start
            // flow.setSettleTime(new Date());
            Calendar now = Calendar.getInstance();
            Calendar plat = Calendar.getInstance();
            Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
            plat.setTime(sysDate);
            plat.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            plat.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            plat.set(Calendar.SECOND, now.get(Calendar.SECOND));
            flow.setSettleTime(plat.getTime());
            // ZGCPCN-86:交收日期需要使用平台日的准确时间,通过平台日的日期拼接当前时间的时分秒 modify by fuyongsheng 20220420 end
            flow.setFinprodId(dto.getFinprodId());
            flow.setSettleNum(dto.getSettleNum());
            flow.setSettleAmt(dto.getSettleAmt());
            flow.setOperType(dto.getPageModel());
            flow.setUpdateUser(LoginUserUtil.getUserName());
            trdManualSettleFlowMapper.insert(flow);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        Map<String,Object> param = new HashMap<>();
        param.put("settleSeqNo",dto.getSettleSeqNo());
        // ZGTFCP-1417:交收延期顺延至下一个工作日 modify by fuyongsheng 20220321 start
        String calendarId;
        if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())){
            calendarId = CacheUtil.getSysParam(STL_DOWNDATA_DEAL_SHJYS_CALID);
        }else{
            calendarId = CacheUtil.getSysParam(STL_DOWNDATA_DEAL_SZJYS_CALID);
        }
        param.put("settleDelayDate",DateUtil.parse(this.getNextWorkDay(dto.getSettleDate(),calendarId)));
        // ZGTFCP-1417:交收延期顺延至下一个工作日 modify by fuyongsheng 20220321 end
        if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收.getValue())){
            param.put("settleStatus",DictCodeListExt3.交收状态_已交收.getValue());
        }else if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_回溯.getValue())){
            param.put("settleStatus",DictCodeListExt3.交收状态_未交收.getValue());
        }else if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收延期.getValue())){
            param.put("settleStatus",DictCodeListExt3.交收状态_交收延期.getValue());
        }else if(StringUtil.equals(dto.getPageModel(),DictCodeListExt3.交收操作类型_交收失败.getValue())){
            param.put("settleStatus",DictCodeListExt3.交收状态_交收失败.getValue());
        }
        manualSettleMapper.updateSettleInfo(param);
    }

    public Page<ManualSettleDto> findPageExport(int page, int length, Map<String, Object> pageParam)
            throws Exception {
        Page<ManualSettleDto> pageDto = super.queryForPage(page, length, () -> manualSettleMapper.findPageExport(pageParam));
        return pageDto;
    }

    /**
     * 获取下一个工作日
     *
     * @param tradeDate， calendarId
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public String getNextWorkDay(Date tradeDate,String calendarId) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate,
                    DateUtil.addDays(tradeDate, 31), calendarId);
            Date tradingDay = dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
            return DateUtil.format(tradingDay);
        }
        return null;
    }

}

