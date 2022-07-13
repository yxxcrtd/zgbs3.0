package com.joyintech.service.period.trdrepoduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.service.period.trdrepoduration.dao.TrdRepoDurationMapper;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.*;
import com.joyintech.tams.component.business.common.service.BussCoreCalculateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlSecPositionInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommonDeleteDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dao.EtrCommonCashPositionMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdRepoDurationService extends BaseService<TrdRepoDurationDto> {

    @Autowired
    private TrdRepoDurationMapper trdRepoDurationMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;

    @Autowired
    private RepTradeMapper repTradeMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;
    
    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;
    
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private  PaTrdProductDealMapper paTrdProductDealMapper;
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private BussCoreCalculateService bussCoreCalculateService;
    @Autowired
    private BaseFinProductCashInfoService baseFinProductCashInfoService;
    @Autowired
    private BaseFinProductPayRuleService baseFinProductPayRuleService;

    @Autowired
    private BaseFinProductCashCalcRuleService baseFinProductCashCalcRuleService;
    @Autowired
    private BaseFinProductCashRateRuleService baseFinProductCashRateRuleService;
    @Autowired
    private BaseFinProductCashPayPlanService baseFinProductCashPayPlanService;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    @Autowired
    private BaseGetBussinessId baseGetBussinessId;
    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate platformDateImpl;
    /**
     * 平台日期
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    /**
     * 汇总交易费用明细表
     */
    @Autowired
    private TrdTradeFeeTotalMapper trdTradeFeeTotalMapper;
    /**
     * 持仓头寸
     */
    @Autowired
    private PtlSecPositionMapper ptlSecPositionMapper;
    /**
     * 委托持仓头寸
     */
    @Autowired
    private  EtrSecPositionMapper etrSecPositionMapper;
    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired
    private BasePtlSecPositionInfoMapper basePtlSecPositionInfoMapper;
    @Autowired
    private EtrCommonCashPositionMapper etrCommonCashPositionMapper;
    @Autowired
    private PeriodCommonMapper periodCommonMapper;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    public Page<TrdRepoDurationDto> findPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        return queryForPageForSelectReturn(page, length, () -> trdRepoDurationMapper.findPage(param));
    }

    public List<TrdRepoDurationDto> selectAllData(Map<String, Object> param) {
        setParams(param);
        return trdRepoDurationMapper.selectAllData(param);
    }

    private void setParams(Map<String, Object> param) {
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        param.put("COMMON_BUSINESS_TYPETRD16", DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 一体化对应etliu start
//        param.put("COMMON_BUSINESS_TYPETRD08", DictCodeList.共通业务类型_质押式回购交易.getValue());
        param.put("COMMON_BUSINESS_TYPETRD08", DictCodeList.业务类型_债券质押式回购交易.getValue());
        // 一体化对应etliu end
        param.put("FINPROD_TYPE_TGF03", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        param.put("FINPROD_TYPE_TGF02", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("PS_T07", DictCodeList.交易方向_还本到账.getValue());
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        param.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        param.put("C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("PST07", DictCodeList.交易方向_到期.getValue());
        param.put("PST56", DictCodeList.交易方向_到期续做.getValue());
//        增加提前终止 与逾期贩售 start
        param.put("PS_TQZZ", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
        param.put("PS_YQFS", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
//        增加提前终止 与逾期贩售 end
        param.put("PST22", DictCodeList.交易方向_收息到账.getValue());
        param.put("PST23", DictCodeList.交易方向_还本到账.getValue());
        param.put("PST93", DictCodeListExt3.交易方向_解除质押.getValue());
        param.put("PST94", DictCodeListExt3.交易方向_中途换券.getValue());
        param.put("PST95", DictCodeListExt3.交易方向_到期购回.getValue());
        param.put("PST96", DictCodeListExt3.交易方向_提前购回.getValue());
        param.put("FEE_TYPE_JSF", DictCodeListExt1.一次性费用类型_结算费.getValue());
        param.put("TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
		param.put("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
        param.put("SysDate", platformDateImpl.getSysDate());
        //有回购类型查询条件是 不显示银行间的数据
        param.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
		
    }

    public TrdRepoDurationDto findEffectiveData(Map<String, Object> params) {
        setParams(params);
        return trdRepoDurationMapper.findEffectiveData(params);
    }

    /**
     * 流程结束时处理
     *
     * @param trdRepoDurationDto 参数
     * @return
     */
    public String doComplete(TrdRepoDurationDto trdRepoDurationDto) {
        String workflowId = trdRepoDurationDto.getWorkflowId();
        
        //查找回购到期授信交易TRADE_ID 撤销释放额度用
        String totleid = trdRepoDurationMapper.findCeTradeIdByTotleTradeId(
            trdRepoDurationDto.getBusiCode());
        
        // 金融产品交易Entity
        List<WflTrdProductDeal> wflTrdProductDeals = trdRepoDurationMapper.findDealByWorkflowId(workflowId);

        // 组合交易
        List<PtlTrade> ptlTrades = gen组合交易(trdRepoDurationDto, wflTrdProductDeals);
        String updateUser = trdRepoDurationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equalsIgnoreCase(trdRepoDurationDto.getOperateType())) {
            //查找授信交易TRADE_ID 撤销释放额度用
            List<String> tradeidList = trdRepoDurationMapper.findCeTradeIdListByTotleTradeId(trdRepoDurationDto.getBusiCode());
            //删除授信占用
            if (tradeidList != null && !tradeidList.isEmpty()){
                tradeidList.forEach(tradeid -> {
                    riskCreditListCommonValidatorService.deleteCredit(tradeid);
                });
            }
            
            TrdFinProductDealTotal trdFinProductDealTotal = investPeriodCommonService.revoke汇总和交易相关表(trdRepoDurationDto.getBusiCode(),
                    updateUser, trdRepoDurationDto.getRevokeReason(), trdRepoDurationDto.getType6(), null);

            // 删除质押券持仓
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtils.copyProperties(trdFinProductDealTotal, wflTrdFinProductDealTotal);
            //到期续做，删除首期交易的质押券及交易流水
            if (trdRepoDurationDto.getPstype()!=null && trdRepoDurationDto.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())){
                delete到期续做首期交易SecPositionFlow(wflTrdFinProductDealTotal,trdRepoDurationDto,updateUser);
            }
            deletePledgeSecPosition(wflTrdFinProductDealTotal);
            deleteWflByWorkflowId(workflowId);
            
            /***质押回购交易释放状态判断*chenlei1**add*start***/
      	   if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdRepoDurationDto.getFinprodType2())
      	            || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(trdRepoDurationDto.getFinprodType2()))
      	            && 
      	            DictCodeList.回购类型_质押式协议回购.getValue().equals(trdRepoDurationDto.getType6())
      	            && 
      	            (DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(trdRepoDurationDto.getPs())
      	            || DictCodeList.交易方向_到期 .getValue().equals(trdRepoDurationDto.getPs()))){
      		        Map<String, Object> statusMap = trdRepoDurationMapper.selectTradeReleaseStatus(wflTrdFinProductDealTotal.getFinprodId());
                     String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                     String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");        
                     if (DictCodeList.是否_否.getValue().equals(isManualRelease)) {//IF 是否手工释放 = 否
       	                trdRepoDurationMapper.updateReleaseStatusRevoke(trdRepoDurationDto.getFinprodId());
                     }
              }
            /***质押回购交易释放状态判断*chenlei1**add*end***/
      	   
      	   // 质押式到期交易撤销 额度重新占用 20.5.8 start
      	   if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(
                trdRepoDurationDto.getFinprodType2())
                && DictCodeList.交易方向_到期.getValue().equals(trdRepoDurationDto.getPs())) {
                List<TrdProductDeal> tempTrdProductDealList = new ArrayList<TrdProductDeal>();
                List<TrdProductDeal> trdProductDeals = new ArrayList<>();
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    TrdProductDeal deal = new TrdProductDeal();
                    BeanUtils.copyProperties(wflTrdProductDeal, deal);
                    deal.setUpdateTime(updateTime);
                    deal.setUpdateUser(updateUser);
                    trdProductDeals.add(deal);
                });

                trdProductDeals.forEach(trdProductDeal -> {
                    trdProductDeal.setUpdateTime(updateTime);
                    trdProductDeal.setUpdateUser(updateUser);
//                    trdProductDealMapper.insertSelective(trdProductDeal);
//                    if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
//                        && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(),
//                            trdRepoDurationDto.getActPortfolioId())) {
//                        String tradeId = trdProductDeal.getTradeId();
//                        trdProductDeal.setTradeId(trdProductDeal.getTradeId() + "S");
//                        trdProductDealMapper.insertSelective(trdProductDeal);
//                        trdProductDeal.setTradeId(tradeId);
//                    }
                    tempTrdProductDealList.add(trdProductDeal);
                });

                if (!StringUtil.isEmptyList(tempTrdProductDealList)) {
                    // 调用授信共通
                    CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);

                    for (TrdProductDeal trdProductDeal : tempTrdProductDealList) {
                        if (DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal.getPs())) {
                            AllTradeCommonInputDto input = new AllTradeCommonInputDto();
//                            String totleid = trdRepoDurationMapper.findCeTradeIdByTotleTradeId(
//                                trdProductDeal.getTotleTradeId());
                            trdProductDeal.setTradeId(totleid);
                            trdProductDeal.setTotleTradeId(totleid);
                            input.setTrdProductDeal(trdProductDeal);
                            input.setRepoType(trdRepoDurationDto.getType6());
                            if (!(DictCodeListExt1.交易状态_初始.getValue().equals(
                                trdProductDeal.getTradeStatus())
                                  && (DictCodeList.交易方向_买入.getValue().equals(
                                      trdProductDeal.getPs())
                                      || DictCodeList.交易方向_申购.getValue().equals(
                                          trdProductDeal.getPs())
                                      || DictCodeListExt2.交易方向_新股中签.getValue().equals(
                                          trdProductDeal.getPs())
                                      || DictCodeListExt2.交易方向_配股.getValue().equals(
                                          trdProductDeal.getPs())
                                      || DictCodeListExt2.交易方向_增发.getValue().equals(
                                          trdProductDeal.getPs())))) {

                                ceLinesUpdate.isNeedOccupyCre(input, true, true, false);
                            }
                        }
                    }
                }

            }
      	   // 质押式到期交易撤销 额度重新占用 20.5.8 end

            // add by mashukai 20210302 ZGBSS-633 start
            if(StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeList.交易方向_到期.getValue())
            ||StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
                Map<String, Object> params = new HashMap<>();
                params.put("finprodId", trdRepoDurationDto.getFinprodId());
                params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
                List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);
                if (flows != null && !flows.isEmpty()) {
                    flows.forEach(flow -> {
                        if (trdRepoDurationDto.getvDate().equals(DateUtil.format(flow.getCdate()))){
                            //撤销时金额取反
                            flow.setAmount(flow.getAmount().negate());
                            flow.setCdate(DateUtil.parse(trdRepoDurationDto.getSettleDate()));
                            trdRepoDurationMapper.insertSelect04Sec(flow);
                            params.put("seqNo", IDGenerator.generate());
                            params.put("mDate", DateUtil.parse(trdRepoDurationDto.getSettleDate()));
                            params.put("vDate", DateUtil.parse(trdRepoDurationDto.getvDate()));
                            params.put("finprodId", flow.getFinprodId());
                            params.put("tradeId", trdRepoDurationDto.getFinprodId());
                            params.put("newTradeId", trdRepoDurationDto.getBusiCode());
                            params.put("isRevoke", "true");
                            //生成持仓流水数据，如果没有生成流水，重复跑当天的批处理会导致持仓数据错误
                            trdRepoDurationMapper.insertSelect04SecFlowByDate(params);
                        }
                    });
                }
            }
            // add by mashukai 20210302 ZGBSS-633 end
            
            return Boolean.TRUE.toString();
        }

        // 金融产品交易汇总
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = trdRepoDurationMapper.findTotalByWorkflowId(workflowId);
        //后台构建交易首期-针对到期续做
        if (trdRepoDurationDto.getPstype()!=null && trdRepoDurationDto.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())){
            gen到期续做(trdRepoDurationDto,wflTrdFinProductDealTotal);
        }
        if(StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeList.交易方向_提前终止.getValue())){
            // 删除质押券持仓
            deletePledgeSecPosition(wflTrdFinProductDealTotal);
        }
        // add by yujiaping 20210205 ZGBSS-489质押式回购交易—银行间正回购交易已做到期交易，质押式回购新建交易添加质押券，查看该债券信息有误start
       if(StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeList.交易方向_到期.getValue())
       ||StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
    	   Map<String, Object> params = new HashMap<>();
           params.put("finprodId", trdRepoDurationDto.getFinprodId());
           params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
           List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);
           if (flows != null && !flows.isEmpty()) {
               flows.forEach(flow -> {
                   if (trdRepoDurationDto.getvDate().equals(DateUtil.format(flow.getCdate()))){
                       flow.setAmount(flow.getAmount());
                       flow.setCdate(DateUtil.parse(trdRepoDurationDto.getSettleDate()));
                       trdRepoDurationMapper.insertSelect04Sec(flow);
                       params.put("seqNo", IDGenerator.generate());
                       params.put("mDate", DateUtil.parse(trdRepoDurationDto.getSettleDate()));
                       params.put("vDate", DateUtil.parse(trdRepoDurationDto.getvDate()));
                       params.put("finprodId", flow.getFinprodId());
                       params.put("tradeId", trdRepoDurationDto.getFinprodId());
                       params.put("newTradeId", trdRepoDurationDto.getBusiCode());
                       //生成持仓流水数据，如果没有生成流水，重复跑当天的批处理会导致持仓数据错误
                       trdRepoDurationMapper.insertSelect04SecFlowByDate(params);
                   }
               });
           }
        }
        //add by yujiaping 20210205 ZGBSS-489质押式回购交易—银行间正回购交易已做到期交易，质押式回购新建交易添加质押券，查看该债券信息有误end
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        BeanUtils.copyProperties(wflTrdFinProductDealTotal, trdFinProductDealTotal);
        trdFinProductDealTotal.setUpdateTime(updateTime);
        trdFinProductDealTotal.setUpdateUser(updateUser);
        if (trdRepoDurationDto.getPstype()!=null && trdRepoDurationDto.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())) {
            trdFinProductDealTotal.setAccountingAmt(trdRepoDurationDto.getSetAmt());
        }
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
//        汇总附加表
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
//        罚息金额
        trdDealTotalAdd.setPenaltyIntamt(trdRepoDurationDto.getPenaltyIntamt());
//        提前终止利率
        if(trdRepoDurationDto.getTerminateRate()!= null){
            trdDealTotalAdd.setTerminateRate(ArithmeticUtil.divide(trdRepoDurationDto.getTerminateRate(),new BigDecimal(100),4));
        }
        if (trdRepoDurationDto.getPstype()!=null && trdRepoDurationDto.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())) {
            trdDealTotalAdd.setMpFinprodId(trdRepoDurationDto.getTrdProductDeal().getTradeId());//首期交易的金融产品代码
            trdDealTotalAdd.setMpBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        }
        trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
        List<TrdProductDeal> trdProductDeals = new ArrayList<>();
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtils.copyProperties(wflTrdProductDeal, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            trdProductDeals.add(deal);
        });

        // 划款指令
        List<WflStlPayOrders> stlPayOrdersDtos = trdRepoDurationMapper.findOrdersByWorkflowId(workflowId);
        stlPayOrdersDtos.forEach(order -> {
            StlPayOrders deal = new StlPayOrders();
            BeanUtils.copyProperties(order, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            stlPayOrdersMapper.insertSelective(deal);
        });

        // 一体化对应etliu start
        List<TrdProductDeal> tempTrdProductDealList = new ArrayList<TrdProductDeal>();
        // 一体化对应etliu end

        // 7.4 插入金融产品交易表
        trdProductDeals.forEach(trdProductDeal -> {
            trdProductDeal.setUpdateTime(updateTime);
            trdProductDeal.setUpdateUser(updateUser);
            trdProductDealMapper.insertSelective(trdProductDeal);
            if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                String tradeId = trdProductDeal.getTradeId();
                trdProductDeal.setTradeId(trdProductDeal.getTradeId() + "S");
                trdProductDealMapper.insertSelective(trdProductDeal);
                trdProductDeal.setTradeId(tradeId);
            }
            // 交易附属表添加 2019年3月15日14:50:18 start
            TrdProductDealAdd add = new TrdProductDealAdd();
            add.setTradeId(trdProductDeal.getTradeId());
            add.setPortfolioId(trdRepoDurationDto.getPortfolioId());
//            提前终止利率
            //        提前终止利率
            if(trdRepoDurationDto.getTerminateRate()!= null){
                add.setTerminateRate(ArithmeticUtil.divide(trdRepoDurationDto.getTerminateRate(),new BigDecimal(100),4));
            }
//            罚息金额
            add.setPenaltyIntamt(trdRepoDurationDto.getPenaltyIntamt());
            trdProductDealAddMapper.insertSelective(add);
            if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                add.setTradeId(trdProductDeal.getTradeId() + "S");
                add.setPortfolioId(trdRepoDurationDto.getActPortfolioId());
                trdProductDealAddMapper.insertSelective(add);
            }
            // 交易附属表添加 2019年3月15日14:50:18 end
            
            // 通道费入库费用明细 表 2019-10-15
            if(StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), trdProductDeal.getPs())
            		&&StringUtil.isNotEmpty(trdRepoDurationDto.getChannelFee())){
            	 
                 trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
                 // 交易费用明细表.费用类型 = “通道费”
                 trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                 // 交易费用明细表.是否随交易支付  根据收取方式判断
             	if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), trdRepoDurationDto.getCollectType())){
                 	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                 }else{
                 	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                 }
                 // 交易费用明细表.交易金额 = 交易.通道费
                 trdTradeFeeDetail.setFeeAmt(trdRepoDurationDto.getChannelFee());
                 trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
                if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                        && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                    String tradeId = trdTradeFeeDetail.getTradeId();
                    trdTradeFeeDetail.setTradeId(trdTradeFeeDetail.getTradeId() + "S");
                    trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
                    trdTradeFeeDetail.setTradeId(tradeId);
                }
            }

            // 通道费入库费用明细 表 2019-10-15 end
//        有交易结算费入费用明细表
            if(StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), trdProductDeal.getPs())&&
                    StringUtil.isNotEmpty(trdRepoDurationDto.getActTradeFee())){
                TrdTradeFeeDetail trdTradeFeeDetailAct = new TrdTradeFeeDetail();
                trdTradeFeeDetailAct.setTradeId(trdProductDeal.getTradeId());
                // 交易费用明细表.费用类型 = “结算费”
                trdTradeFeeDetailAct.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
                // 交易费用明细表.是否随交易支付  否
                trdTradeFeeDetailAct.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                // 交易费用明细表.交易金额 = 交易.通道费
                trdTradeFeeDetailAct.setFeeAmt(trdRepoDurationDto.getActTradeFee());
                trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetailAct);
                if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                        && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                    String tradeId = trdTradeFeeDetailAct.getTradeId();
                    trdTradeFeeDetailAct.setTradeId(trdTradeFeeDetailAct.getTradeId() + "S");
                    trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetailAct);
                    trdTradeFeeDetailAct.setTradeId(tradeId);
                }
            }
            // 一体化对应etliu start
            tempTrdProductDealList.add(trdProductDeal);
            // 一体化对应etliu end
        });
        // 金融产品交易汇总扩展Entity
        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = trdRepoDurationMapper.findPaTotalByWorkflowId(workflowId);
        if(StringUtil.isNotEmpty(trdRepoDurationDto.getChannelFee())){
            //                汇总交易费用明细表 通道费
            TrdTradeFeeTotal feeTotal = new TrdTradeFeeTotal();
            feeTotal.setTotleTradeId(wflPaTrdFinDealTotal.getTotleTradeId());
            feeTotal.setFeeAmt(trdRepoDurationDto.getChannelFee());
            // 交易费用明细表.费用类型 = “结算费”
            feeTotal.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            // 交易费用明细表.是否随交易支付  否
            // 交易费用明细表.是否随交易支付  根据收取方式判断
            if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), trdRepoDurationDto.getCollectType())){
                feeTotal.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }else{
                feeTotal.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
            trdTradeFeeTotalMapper.insertSelective(feeTotal);
        }
        if(StringUtil.isNotEmpty(trdRepoDurationDto.getActTradeFee())){
            //                汇总交易费用明细表 结算费
            TrdTradeFeeTotal feeTotal = new TrdTradeFeeTotal();
            feeTotal.setTotleTradeId(wflPaTrdFinDealTotal.getTotleTradeId());
            feeTotal.setFeeAmt(trdRepoDurationDto.getActTradeFee());
            // 交易费用明细表.费用类型 = “结算费”
            feeTotal.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
            // 交易费用明细表.是否随交易支付  否
            feeTotal.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            trdTradeFeeTotalMapper.insertSelective(feeTotal);
        }
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        BeanUtils.copyProperties(wflPaTrdFinDealTotal, paTrdFinProductDealTotal);
        paTrdFinProductDealTotal.setUpdateTime(updateTime);
        paTrdFinProductDealTotal.setUpdateUser(updateUser);
        paTrdFinProductDealTotalMapper.insertSelective(paTrdFinProductDealTotal);

        if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdRepoDurationDto.getFinprodType2())
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(trdRepoDurationDto.getType6())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", wflTrdFinProductDealTotal.getFinprodId());
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            if(StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())){//质押券提前终止是终止日，其他是资金到账日
                params.put("mDate", wflTrdFinProductDealTotal.getMdate());
            }
            else{
                params.put("mDate", wflTrdFinProductDealTotal.getSettleDate());
            }
            
            /**质押式正回购释放判断***chenlei1***add*start*/
		    Map<String, Object> statusMap = trdRepoDurationMapper.selectTradeReleaseStatus(wflTrdFinProductDealTotal.getFinprodId());
            String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
            String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
            if(!DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)){           
            /**质押式正回购释放判断***chenlei1***add*end*/
            List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);
            if (flows != null && !flows.isEmpty()) {
                flows.forEach(flow -> {
                    if(StringUtil.equals(trdRepoDurationDto.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())){
                        flow.setCdate(wflTrdFinProductDealTotal.getMdate());
                    }
                    else{
                        flow.setCdate(wflTrdFinProductDealTotal.getSettleDate());
                    }
                    flow.setCreateTime(updateTime);
                    flow.setCreateUser(updateUser);
                    flow.setUpdateTime(updateTime);
 //                   trdRepoDurationMapper.insertSelect04Sec(flow);
                    flow.setSeqno(IDGenerator.generate());
                    flow.setTradeId(wflTrdFinProductDealTotal.getFinprodId());
                    ptlSecPositionFlowMapper.insert(flow);
                    PtlSecPositionFlowDto secPositionFlowDto =new PtlSecPositionFlowDto();//汇总头寸表
                    PtlSecPosition secPosition= new PtlSecPosition();
                    BeanUtil.copyPropertiesIgnoreNull(secPositionFlowDto,flow);
                    BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                    BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlowDto);
                    if(amount==null) {//当天没余额
                        // 如果当天的持仓数据没有，取当天之前最近一天的数据
                        // 查询最近的那一天
                        Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(secPositionFlowDto);
                        if (maxCdate == null) {
                            ptlSecPositionMapper.insertSelective(secPosition);
                        } else {
                            PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                            BeanUtils.copyProperties(secPositionFlowDto, tempPositionFlowDto);
                            tempPositionFlowDto.setCdate(maxCdate);
                            BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPosition(tempPositionFlowDto);
                            secPosition.setAmount(ArithmeticUtil.doAdd(secPosition.getAmount(), tempAmount));
                            ptlSecPositionMapper.insertSelective(secPosition);
                        }
                    }
                    else{//当天有余额
                        trdRepoDurationMapper.insertSelect04Sec(flow);
                    }

                });
            }
            }
        }
        /***质押回购交易释放状态判断*chenlei1**add*start***/
 	   if ((DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdRepoDurationDto.getFinprodType2())
 	            || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(trdRepoDurationDto.getFinprodType2()))
 	            && 
 	            DictCodeList.回购类型_质押式协议回购.getValue().equals(trdRepoDurationDto.getType6())
 	            && 
 	            (DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(trdRepoDurationDto.getPs())
 	            || DictCodeList.交易方向_到期 .getValue().equals(trdRepoDurationDto.getPs()))){
 		        Map<String, Object> statusMap = trdRepoDurationMapper.selectTradeReleaseStatus(wflTrdFinProductDealTotal.getFinprodId());
                String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
                String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");        
                if(!DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)){//IF  未释放
 	                trdRepoDurationMapper.updateReleaseStatus(trdRepoDurationDto.getFinprodId());
 	            }	
         }
       /***质押回购交易释放状态判断*chenlei1**add*end***/
        // 7.3 插入组合交易
        ptlTrades.forEach(ptlTrade -> {
            PtlTrade deal = new PtlTrade();
            BeanUtils.copyProperties(ptlTrade, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            ptlTradeMapper.insertSelective(deal);
            if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                deal.setPortfolioId(trdRepoDurationDto.getActPortfolioId());
                deal.setBusiNo(deal.getBusiNo() + "S");
                ptlTradeMapper.insertSelective(deal);
            }
        });

        // 7.7 交易报送信息
        List<RepTrade> repTrades = gen交易报送信息(trdRepoDurationDto, wflTrdProductDeals);
        repTrades.forEach(repTrade -> {
            RepTrade deal = new RepTrade();
            BeanUtils.copyProperties(repTrade, deal);
            deal.setUpdateTime(updateTime);
            deal.setUpdateUser(updateUser);
            repTradeMapper.insertSelective(deal);
        });
        // 7.2 删除原业务流程表中的交易记录
        deleteWflByWorkflowId(workflowId);

        // 一体化对应etliu start
        if (!StringUtil.isEmptyList(tempTrdProductDealList)) {
            tempTrdProductDealList.forEach(item->{
                item.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
                item.setInputValue2(trdRepoDurationDto.getType6());
            });
            // 回购到期 调用lendMdateAdd估值接口
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(trdRepoDurationDto.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDealList(tempTrdProductDealList);
            inputDto.setRepoType(trdRepoDurationDto.getType6());
            inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                StlPayOrders stlPayOrders = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, stlPayOrdersDtos.get(0));
                inputDto.setStlPayOrders(stlPayOrders);
            }

            /**ADD BY WUJIE ON 20210129 START REMARK:回购交易方向：到期续作、逾期贩售**/
            inputDto.setPsType(trdRepoDurationDto.getPs());
            /**ADD BY WUJIE ON 20210129 END**/

            AllTradeCommonUtil.lendMdateAdd(inputDto);
            if (StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
                    && !StringUtil.equals(trdRepoDurationDto.getPortfolioId(), trdRepoDurationDto.getActPortfolioId())) {
                tempTrdProductDealList.forEach(item->{
                    item.setTradeId(item.getTradeId()+'S');
                });
                trdTradeFeeDetail.setTradeId(trdTradeFeeDetail.getTradeId()+'S');
                inputDto.setProdid(trdRepoDurationDto.getActPortfolioId());
                AllTradeCommonUtil.lendMdateAdd(inputDto);
            }
        }
        return Boolean.TRUE.toString();
    }

    private String deleteWflByWorkflowId(String workflowId) {
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflPaTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        return Boolean.TRUE.toString();
    }

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 删除原业务流程表中的交易记录
     *
     * @param trdCommonDeleteDto TrdCommonDeleteDto
     * @return true
     */
    public String doDelete(TrdCommonDeleteDto trdCommonDeleteDto) {
        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = trdCommonDeleteDto.getWflTrdFinProductDealTotal();
        deletePledgeSecPosition(wflTrdFinProductDealTotal);
        if(null!=trdCommonDeleteDto){
        	 deleteWflByWorkflowId(trdCommonDeleteDto.getWorkflowId());
        }
       
        return Boolean.TRUE.toString();
    }

    private void deletePledgeSecPosition(WflTrdFinProductDealTotal wflTrdFinProductDealTotal) {
        if (wflTrdFinProductDealTotal != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", wflTrdFinProductDealTotal.getTotleTradeId().substring(0, wflTrdFinProductDealTotal.getTotleTradeId().length() - 1));
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            if(StringUtil.equals(wflTrdFinProductDealTotal.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())){
                params.put("mDate", wflTrdFinProductDealTotal.getMdate());
            }
            else{
                params.put("mDate", wflTrdFinProductDealTotal.getSettleDate());
            }
            List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);
            if (flows != null && !flows.isEmpty()) {
                flows.forEach(flow -> {
                    flow.setAmount(flow.getAmount());
                    trdRepoDurationMapper.insertSelect04Sec(flow);
                    ptlSecPositionFlowMapper.deleteByPrimaryKey(flow.getSeqno());
                });
            }
            
            // START modify by wj 20200519 TISHKBS-383【实时资产头寸查询】撤销质押式回购到期续做，头寸统计错误
            // 最早的实际头寸 未返还。
            //modify by mashukai 20210317 	ZGBSS-794 质押式回购到期，撤销生效之后资产头寸没有随之变化 start
            if(DictCodeList.交易方向_到期续做.getValue().equals(wflTrdFinProductDealTotal.getPs())){
                params.put("holdingType", DictCodeList.持仓类型_实际.getValue());
                flows = trdRepoDurationMapper.selectSecFlow(params);
                if (flows != null && !flows.isEmpty()) {
                    flows.forEach(flow -> {
                        flow.setAmount(flow.getAmount());
                        trdRepoDurationMapper.insertSelect04Sec(flow);
                        ptlSecPositionFlowMapper.deleteByPrimaryKey(flow.getSeqno());
                    });
                }    
            }
            //modify by mashukai 20210317 	ZGBSS-794 质押式回购到期，撤销生效之后资产头寸没有随之变化 end

        }
    }

    private List<RepTrade> gen交易报送信息(TrdRepoDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals) {
        List<RepTrade> result = new ArrayList<>();
        // 产品登记编码
        Map<String, Object> params = new HashMap<>();
        params.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        params.put("portfolioId", entity.getPortfolioId());
        String 产品登记编码 = trdRepoDurationMapper.findPrdRegCode(params);
        if (StringUtil.isEmpty(产品登记编码)) {
            return result;
        }
        BigDecimal 最低收盘价 = BigDecimal.ONE;
        if (!DictCodeList.币种_人民币_CNY.getValue().equals(entity.getCcy())) {
            最低收盘价 = investPeriodCommonService.find最低收盘价(entity.getCcy(), entity.getSettleDate());
        }
        BigDecimal 最低收盘价F = 最低收盘价;
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            RepTrade repTrade = new RepTrade();
            repTrade.setRepIssuePartyId(entity.getIssuePartyId());
            repTrade.setRepFinprodRegId(产品登记编码);
            repTrade.setRepBankTradeCode(wflTrdProductDeal.getTradeId());
            repTrade.setRepBankAssetCode(wflTrdProductDeal.getFinprodId());
            repTrade.setRepCcy(entity.getCcy());
            repTrade.setRepCount(wflTrdProductDeal.getShareAmt());
            repTrade.setRepTradeDate(DateUtil.parse(entity.getSettleDate()));
            repTrade.setRepTradeComponent(entity.getCounterId());
            repTrade.setRepDelCode(wflTrdProductDeal.getTradeId());
            repTrade.setRepRemark(entity.getRemark());
            repTrade.setRepSendStatus(DictCodeList.是否_是.getValue());
            repTrade.setRepStatusDate(platformDateImpl.getSysDate());
            repTrade.setCreateUser(wflTrdProductDeal.getCreateUser());
            repTrade.setCreateDept(wflTrdProductDeal.getCreateDept());
            repTrade.setCreateTime(wflTrdProductDeal.getCreateTime());

            if (DictCodeList.交易方向_还本到账.getValue().equals(wflTrdProductDeal.getPs())) {
                repTrade.setRepOccurMoney(wflTrdProductDeal.getPrinAmt());
                // 折算人民币金额（元）
                repTrade.setRepMoneyCny(ArithmeticUtil.multiply(wflTrdProductDeal.getPrinAmt(), 最低收盘价F));
                if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())) {
                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_同业拆入和卖出回购到期.getValue());
                    repTrade.setRepMoneyFlowTypeExp("债券质押式正回购到期本金");
                } else {
                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_拆放同业和买入返售到期.getValue());
                    repTrade.setRepMoneyFlowTypeExp("债券质押式逆回购到期本金");
                }
            }
            else if (DictCodeList.交易方向_收息到账.getValue().equals(wflTrdProductDeal.getPs())) {
                repTrade.setRepOccurMoney(wflTrdProductDeal.getIntAmt());
                repTrade.setRepMoneyCny(ArithmeticUtil.multiply(wflTrdProductDeal.getIntAmt(), 最低收盘价F));
                if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())) {
                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产支出.getValue());
                    repTrade.setRepMoneyFlowTypeExp("债券质押式正回购到期利息支出");
                } else {
                    repTrade.setRepMoneyFlowType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
                    repTrade.setRepMoneyFlowTypeExp("债券质押式逆回购到期利息支出");
                }
            }

            result.add(repTrade);
        });
        return result;
    }

    private List<PtlTrade> gen组合交易(TrdRepoDurationDto entity, List<WflTrdProductDeal> wflTrdProductDeals) {
        List<PtlTrade> result = new ArrayList<>();
        wflTrdProductDeals.forEach(wflTrdProductDeal -> {
            PtlTrade ptlTrade2 = new PtlTrade();
            ptlTrade2.setPortfolioId(entity.getPortfolioId());
            ptlTrade2.setBusiNo(wflTrdProductDeal.getTradeId());
            ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade2.setBusiId(entity.getFinprodId());
            ptlTrade2.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
            ptlTrade2.setSecManageAcctId(entity.getSecManageAcctId());
            ptlTrade2.setSettleDate(DateUtil.parse(entity.getSettleDate()));
            result.add(ptlTrade2);
        });
        return result;
    }

    public List<RepoInfo> findRepoInfo(Map<String, Object> params) {
        return trdRepoDurationMapper.findRepoInfo(params);
    }

    public List<FinCashPlan> findFinCashInfo(String finprodId) {
        return trdRepoDurationMapper.findFinCashInfo(finprodId);
    }

    public Page<SelectOptionDto> getBusiCode(int page, int length, Map<String, Object> param) {
        param.put("FINPROD_TYPE_TGF03", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        param.put("FINPROD_TYPE_TGF02", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());

        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("IS_CANCEL_N", DictCodeList.是否_否.getValue());
        param.put("PS_T07", DictCodeList.交易方向_到期.getValue());

        param.put("PST56", DictCodeListExt3.交易方向_到期续做.getValue());
        param.put("PST93", DictCodeListExt3.交易方向_解除质押.getValue());
        param.put("PST94", DictCodeListExt3.交易方向_中途换券.getValue());
        param.put("PST95", DictCodeListExt3.交易方向_到期购回.getValue());
        param.put("PST96", DictCodeListExt3.交易方向_提前购回.getValue());

        param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        param.put("PST07", DictCodeList.交易方向_到期.getValue());
        param.put("PST56", DictCodeList.交易方向_到期续做.getValue());
//        增加提前终止 与逾期贩售 start
        param.put("PS_TQZZ", DictCodeListExt3.交易方向_投资端_提前终止.getValue());
        param.put("PS_YQFS", DictCodeListExt3.交易方向_投资端_逾期返售.getValue());
        param.put("TRADE_MARKET_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_MARKET_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());

        return queryForPageForSelectReturn(page, length, () -> trdRepoDurationMapper.getBusiCode(param));
    }

    /**
     * 新建新生成的标的信息
     *
     * @param  trdRepoDurationDto
     * @return true
     */
    public String doInsert(TrdRepoDurationDto trdRepoDurationDto) {
        FinTradeProduct finTradeProduct=trdRepoDurationDto.getFinTradeProduct();
        finTradeProductMapper.insertSelective(finTradeProduct);//新建插入标的交易表
        return Boolean.TRUE.toString();
    }
    /**
     * 到期续做校验
     *查询回购交易的市场机构代码和到期日
     * @param
     * @return true
     */
    public TrdRepoDurationDto findTrdMarketByFinprodId(Map<String, Object> params) {
        setParams(params);
        return trdRepoDurationMapper.findTrdMarketByFinprodId(params);
    }

    /**
     * 写入现金流相关信息表
     *
     * @param input 画面参数Dto
     * @see
     * @since 1.0
     */
    private void cashFlowLogic(TrdProductDeal input,String basis,BigDecimal intRate,BigDecimal setAmt) {
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
        finCashInfoPrin.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
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
        finCashInfoInt.setCashId(commonBusinessService.generateCashFlowCode(input.getTradeId(),
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
        baseFinProductCashInfoService.insertList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        // 现金流支付规则表Entity-利息
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        // 现金流代码 = 现金流信息Entity-利息.现金流代码
        finCashPayRule.setCashId(finCashInfoInt.getCashId());
        // 生效日期 = 画面.起息日
        finCashPayRule.setEffDate(input.getVdate());
        // 支付频率 = 数据字典缓存.支付频率(付息频率).到期支付
        finCashPayRule.setPayCycle(DictCodeList.频率_到期一次.getValue());
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
        baseFinProductPayRuleService.insert(finCashPayRuleList);

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
        finCashCalcRule.setBasis(basis);
        // 生效利率 = 画面.回购利率/100
        finCashCalcRule.setYield(ArithmeticUtil.divide(intRate, new BigDecimal("100"), 8));
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
        baseFinProductCashCalcRuleService.insert(finCashCalcRuleList);

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
        finRateInfo.setBasis(basis);
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
        baseFinProductCashRateRuleService.insert(finRateInfoList);

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
        // 计划支付本金 = 画面.结算金额
        finCashPlan.setPrinAmt(setAmt);
        // 计息本金 = 画面.本金金额
        finCashPlan.setIntPrinAmt(setAmt);
        // 计划支付利息 = 0
        finCashPlan.setIntAmt(BigDecimal.ZERO);
        // 是否支付利息 = 数据字典缓存.是否.是
        finCashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
        // 计划支付现金流 = 画面.本金金额
        finCashPlan.setCashAmt(setAmt);
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
        finCashPlan.setIntPrinAmt(setAmt);
        // 计划支付利息 = 回购利息 ※调用业务组件.【平安银行算法组件.投资交易回购利息】，拆借算法同回购一样，可参考回购利息算法
        finCashPlan.setIntAmt(bussCoreCalculateService.calc投资交易计算回购利息(setAmt,
                ArithmeticUtil.divide(intRate, new BigDecimal("100"), 8),
                input.getVdate(), input.getMdate(), basis));
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
        baseFinProductCashPayPlanService.insertList(finCashPlanList);
    }


    private void gen到期续做(TrdRepoDurationDto trdRepoDurationDto,WflTrdFinProductDealTotal wflTrdFinProductDealTotal){
        String orgFinprodId=trdRepoDurationDto.getFinprodId();//原始首期交易金融产品代码
        String updateUser = trdRepoDurationDto.getUpdateUser();//修改者
        Date updateTime = new Date();//插入时间
        //查出该交易的原始首期交易
        TrdProductDeal  trdProductDealOrg= trdProductDealMapper.selectByPrimaryKey(orgFinprodId);
        //查询交易标的表的金融产品代码
        WflTrdDealTotalAdd wflTrdDealTotalAdd=
                wflTrdDealTotalAddMapper.selectByPrimaryKey(wflTrdFinProductDealTotal.getTotleTradeId());
        String newFinprodId=wflTrdDealTotalAdd.getMpFinprodId();//新首期交易金融产品id
        trdProductDealOrg.setFinprodId(newFinprodId);//构建新的首期交易-交易标的金融产品代码
        trdProductDealOrg.setTradeId(newFinprodId);
        Date vDate=trdProductDealOrg.getSettleDate();//原始交易的交割日
        //获取界面的traddeal数据
        TrdProductDeal 界面deal=trdRepoDurationDto.getTrdProductDeal();
        Date tradeDate=界面deal.getTradeDate();
        trdProductDealOrg.setCounterContact(界面deal.getCounterContact());
        trdProductDealOrg.setVdate(界面deal.getVdate());
        trdProductDealOrg.setMdate(界面deal.getMdate());
        trdProductDealOrg.setTradeDate(界面deal.getVdate());//交易日
        trdProductDealOrg.setSettleDate(界面deal.getVdate());
        trdProductDealOrg.setShareAmt(界面deal.getShareAmt());
        trdProductDealOrg.setPrinAmt(界面deal.getPrinAmt());
        //trdProductDealOrg.setIntAmt(界面deal.getIntAmt());
        trdProductDealOrg.setIntAmt(null);
        trdProductDealOrg.setFeeAmt(null);//初始清空
        trdProductDealOrg.setFeeAmtUnpay(null);//初始清空
        trdProductDealOrg.setFeeAmtPay(null);//初始清空
        //交易金额 正回购时，交易金额 = 本金金额 - 佣金；逆回购时，交易金额 = 本金金额 + 佣金；
        /**moidfy by wujie on 20201229 start
        if(trdRepoDurationDto.getCommissionAmt()!=null) {
            if (trdProductDealOrg.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                trdProductDealOrg.setTradeAmt(界面deal.getPrinAmt().subtract(trdRepoDurationDto.getCommissionAmt()));
            }else{
                trdProductDealOrg.setTradeAmt(界面deal.getPrinAmt().add(trdRepoDurationDto.getCommissionAmt()));
            }
        }
        **/
        if (trdProductDealOrg.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
        	trdProductDealOrg.setTradeAmt(ArithmeticUtil.subtract(界面deal.getPrinAmt(), trdRepoDurationDto.getCommissionAmt()));
        }
        else{
        	trdProductDealOrg.setTradeAmt(ArithmeticUtil.doAdd(界面deal.getPrinAmt(), trdRepoDurationDto.getCommissionAmt()));
        }
        /**moidfy by wujie on 20201229 end**/
        
        
        
        //trdProductDealOrg.setFpriceAmt(trdRepoDurationDto.getTradeAmtex());//续做到期结算金额
        trdProductDealOrg.setCounterId(界面deal.getCounterId());
        trdProductDealOrg.setCounterTrader(界面deal.getCounterTrader());
        trdProductDealOrg.setCounterContact(界面deal.getCounterContact());//联系方式
        trdProductDealOrg.setTrader(界面deal.getTrader());
        trdProductDealOrg.setTotleTradeId(wflTrdFinProductDealTotal.getTotleTradeId());
        trdProductDealOrg.setCreateUser(updateUser);
        trdProductDealOrg.setUpdateTime(updateTime);
        trdProductDealOrg.setCreateTime(updateTime);
        trdRepoDurationDto.getTrdProductDeal().setTradeId(newFinprodId);//新的首期交易金融产品

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表,查询历史首期那笔扩展表信息
        TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(orgFinprodId);
        String portfolioId=trdProductDealAdd.getPortfolioId();//母组合代码
        // 交易编号 = 业务编号 ※2生成的交易编号
        trdProductDealAdd.setTradeId(newFinprodId);//用新的首期交易编号
        // 生成方式 = 数据字典缓存.生成方式.画面录入
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        trdProductDealAdd.setCreateTime(updateTime);
        trdProductDealAdd.setUpdateTime(updateTime);
        trdProductDealAdd.setCreateUser(updateUser);
        trdProductDealAddMapper.insertSelective(trdProductDealAdd);
        // 组合交易表Entity 构造开始-----------------------------------------------------------------------
        PtlTradeKey key = new PtlTradeKey();
        key.setBusiNo(orgFinprodId);
        key.setPortfolioId(portfolioId);//插入母组合的
        key.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        List<PtlTrade> ptlTradeList =new ArrayList<>();
        PtlTrade ptlTrade=ptlTradeMapper.selectByPrimaryKey(key);//获取历史首期交易的组合交易
        // 业务编号 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiNo(newFinprodId);
        // 业务表 = "金融产品交易"
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        // 业务明细代码 = 业务编号 ※2生成的交易编号
        ptlTrade.setBusiId(newFinprodId);
        // 实际发生日 = 画面.交易日
        ptlTrade.setSettleDate(trdProductDealOrg.getVdate());
        ptlTrade.setCreateTime(updateTime);
        ptlTrade.setCreateUser(updateUser);
        ptlTradeMapper.insertSelective(ptlTrade);
        ptlTradeList.add(ptlTrade);
        //确定一下是否有子组合
        if(trdRepoDurationDto.getActPortfolioId()!=null && StringUtil.isNotEmpty(trdRepoDurationDto.getActPortfolioId())
        && !trdRepoDurationDto.getActPortfolioId().equals(portfolioId)) {
            String fPortfolioId = trdRepoDurationDto.getActPortfolioId();//有子组合
                PtlTrade fPtlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(fPtlTrade, ptlTrade);
                fPtlTrade.setPortfolioId(fPortfolioId);//插入母组合交易
                ptlTradeMapper.insertSelective(fPtlTrade);
                ptlTradeList.add(fPtlTrade);
        }

        // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
        FinTradeProductAddKey addkey= new FinTradeProductAddKey();
        addkey.setFinprodId(orgFinprodId);
        addkey.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //查询原始的交易
        FinTradeProductAdd finTradeProductAdd = finTradeProductAddMapper.selectByPrimaryKey(addkey);
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProductAdd.setFinprodId(newFinprodId);
        finTradeProductAdd.setCreateTime(updateTime);
        finTradeProductAdd.setCreateUser(updateUser);
        finTradeProductAddMapper.insertSelective(finTradeProductAdd);
        // 交易类金融产品附属Entity 构造结束----------------------------------------------------------

        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductTypeKey typeKey= new FinProductTypeKey();
        typeKey.setFinprodId(orgFinprodId);
        typeKey.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        FinProductType finProductType =finProductTypeMapper.selectByPrimaryKey(typeKey);
        // 金融产品代码 = 金融产品交易Entity.交易编号
        finProductType.setFinprodId(newFinprodId);
        finProductType.setCreateTime(updateTime);
        finProductType.setUpdateTime(updateTime);
        finProductType.setCreateUser(updateUser);
        finProductTypeMapper.insertSelective(finProductType);
        // 金融产品分类Entity 构造结束----------------------------------------------------------

        // 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        //获取历史数据
        PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(orgFinprodId);
        // 交易编号 = 金融产品交易Entity.交易编号
        paTrdProductDeal.setTradeId(newFinprodId);
        paTrdProductDeal.setCreateTime(updateTime);
        paTrdProductDeal.setUpdateTime(updateTime);
        paTrdProductDeal.setCreateUser(updateUser);
        paTrdProductDealMapper.insertSelective(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------

        //写入现金流相关信息表
        cashFlowLogic(trdProductDealOrg,trdRepoDurationDto.getBasis(),trdRepoDurationDto.getIntRatex(),
                trdRepoDurationDto.getTheoryPrinex());//

        // 7.7写入回购质押品信息
        //用旧的查出旧的质押品信息
        FinDealPledgeKey pledgeKey= new FinDealPledgeKey();
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        pledgeKey.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        pledgeKey.setFinprodId(orgFinprodId);//金融产品代码
        List<FinDealPledge> dealPledgeList=finDealPledgeMapper.selectByFinprodId(pledgeKey);//得到原始交易的质押信息列表
        dealPledgeList.forEach(dealPledgel -> {
            dealPledgel.setFinprodId(newFinprodId);//组装成新的质押信息
            dealPledgel.setCreateUser(updateUser);
            dealPledgel.setCreateTime(updateTime);
            finDealPledgeMapper.insertSelective(dealPledgel);//插入新的质押品信息
        });

        if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdRepoDurationDto.getFinprodType2())
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(trdRepoDurationDto.getType6())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", orgFinprodId);
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            params.put("mDate", vDate);
            List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);//查询原来首期的质押券的信息流水
            if (flows != null && !flows.isEmpty()) {
                flows.forEach(flow -> {
                    flow.setCdate(trdProductDealOrg.getVdate());
                    flow.setCreateTime(updateTime);
                    flow.setUpdateTime(updateTime);
                    flow.setCreateUser(updateUser);
                    flow.setUpdateUser(updateUser);
                    flow.setAmount(ArithmeticUtil.multiply(flow.getAmount(), new BigDecimal(-1)));//原始交易是正值
                    //trdRepoDurationMapper.insertSelect04SecFlow(flow);//新首期的PTL_SEC_POSITION
                    flow.setSeqno(IDGenerator.generate());
                    flow.setTradeId(newFinprodId);
                    ptlSecPositionFlowMapper.insert(flow);//新首期质押的PTL_SEC_POSITION
                    PtlSecPositionFlowDto secPositionFlowDto =new PtlSecPositionFlowDto();
                    PtlSecPosition secPosition= new PtlSecPosition();
                    BeanUtil.copyPropertiesIgnoreNull(secPositionFlowDto,flow);
                    BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                    BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlowDto);
                    if(amount==null) {//当天没余额
                        // 如果当天的持仓数据没有，取当天之前最近一天的数据
                        // 查询最近的那一天
                        Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(secPositionFlowDto);
                        if (maxCdate == null) {
                            ptlSecPositionMapper.insertSelective(secPosition);
                        } else {
                            PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                            BeanUtils.copyProperties(secPositionFlowDto, tempPositionFlowDto);
                            tempPositionFlowDto.setCdate(maxCdate);

                            BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPosition(tempPositionFlowDto);
                            secPosition.setAmount(ArithmeticUtil.doAdd(secPosition.getAmount(), tempAmount));
                            ptlSecPositionMapper.insertSelective(secPosition);
                        }
                    }
                    else{//当天有余额
                        trdRepoDurationMapper.insertSelect04Sec(flow);
                    }
                    EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlow, flow);//etr流水
                    etrSecPositionFlow.setAmount(ArithmeticUtil.multiply(etrSecPositionFlow.getAmount(), new BigDecimal(-1)));
                    etrSecPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrSecPositionFlow.setSeqno(IDGenerator.generate());
                    etrSecPositionFlowMapper.insertSelective(etrSecPositionFlow);//委托持仓流水
                    EtrSecPositionFlowDto etrSecPositionFlowDto=new EtrSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto, etrSecPositionFlow);//etr流水
                    EtrSecPosition etrSecPosition = new EtrSecPosition();//委托首期交易持仓
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPosition, secPosition);
                    etrSecPosition.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    BigDecimal etramount = etrCommonCashPositionMapper.selectSecPosition(etrSecPositionFlowDto);
                    if (etramount == null) {
                        // 如果当天的持仓数据没有，取当天之前最近一天的数据
                        // 查询最近的那一天
                        Date maxCdate = etrCommonCashPositionMapper.selectSecPositionMaxCdate(etrSecPositionFlowDto);
                        if (maxCdate == null) {
                            etrSecPositionMapper.insertSelective(etrSecPosition);
                        }
                        else {
                            EtrSecPositionFlowDto tempPositionFlowDto = new EtrSecPositionFlowDto();
                            BeanUtils.copyProperties(etrSecPositionFlowDto, tempPositionFlowDto);
                            tempPositionFlowDto.setCdate(maxCdate);

                            BigDecimal tempAmount = etrCommonCashPositionMapper.selectSecPosition(tempPositionFlowDto);
                            etrSecPosition.setAmount(ArithmeticUtil.doAdd(etrSecPosition.getAmount(), tempAmount));
                            etrSecPositionMapper.insertSelective(etrSecPosition);
                        }
                    }
                    else {
                        etrSecPosition.setAmount(ArithmeticUtil.doAdd(etrSecPosition.getAmount(), etramount));
                        etrSecPositionMapper.updateByPrimaryKeySelective(etrSecPosition);
                    }
                    // 2.2.2 检索组合持仓的最大日期
                    String maxCdate = etrCommonCashPositionMapper.selectSecMaxCdate(etrSecPositionFlowDto);
                    String cDate = DateUtil.format(etrSecPositionFlowDto.getCdate());
                    // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
                    if (maxCdate != null && !maxCdate.equals(cDate)) {
                        String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                        String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                        Map<String, Object> etrParams = BeanUtil.object2Map(etrSecPositionFlowDto);
                        etrParams.put("minDate", minDate);
                        etrParams.put("maxDate", maxDate);
                        etrCommonCashPositionMapper.updateSecPosition(etrParams);
                    }
                });
            }
        }

        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        if (StringUtil.isNotEmpty(trdRepoDurationDto.getCommissionAmt())) {//佣金不为空,
            //有佣金入费用明细表
            trdTradeFeeDetail.setTradeId(newFinprodId);
            // 交易费用明细表.费用类型 = “佣金”
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_佣金.getValue());
            // 交易费用明细表.交易金额 = 交易.通道费
            trdTradeFeeDetail.setFeeAmt(trdRepoDurationDto.getCommissionAmt());
            trdProductDealOrg.setFeeAmt(trdRepoDurationDto.getCommissionAmt());//交易表费用总金额
            if(StringUtil.isNotEmpty(trdRepoDurationDto.getChlId())) {//通过通道投资，默认佣金随费用支付，出费用明细表
                // 交易费用明细表.是否随交易支付  根据收取方式判断
                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                //因佣金随费用支付，交易表feeAmt加上该笔费用
                trdProductDealOrg.setFeeAmtPay( trdRepoDurationDto.getCommissionAmt());
            }else{//通过证券户投资
                if(StringUtil.isNotEmpty(trdRepoDurationDto.getIsPayWithTrade()) &&
                        trdRepoDurationDto.getIsPayWithTrade().equals(DictCodeList.是否_否.getValue())){//是否随交易支付为否
                    // 交易费用明细表.是否随交易支付  根据收取方式判断
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
                    trdProductDealOrg.setFeeAmtUnpay(trdRepoDurationDto.getCommissionAmt());//交易表的未随交易支付
                }else {//费用品种中配置了随交易支付，费用品种没有配置默认佣金随交易支付
                    // 交易费用明细表.是否随交易支付  根据收取方式判断
                    trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                    //因佣金随费用支付，交易表feeAmt加上该笔费用
                    trdProductDealOrg.setFeeAmtPay(trdRepoDurationDto.getCommissionAmt());
                }
            }
            trdTradeFeeDetail.setCreateUser(updateUser);
            trdTradeFeeDetail.setCreateTime(updateTime);
            trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);
        }

        //插入交易表
        trdProductDealMapper.insertSelective(trdProductDealOrg);
        Map<String, Object> params = new HashMap<>();//该笔首期交易入持仓表
        params.put("finprodId", orgFinprodId);
        params.put("holdingType", DictCodeList.持仓类型_实际.getValue());
        params.put("mDate", vDate);
        List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);//查询原来首期的持仓的信息流水
        if (flows != null && !flows.isEmpty()) {
            flows.forEach(flow -> {
                flow.setCdate(trdProductDealOrg.getVdate());
                flow.setAmount(trdProductDealOrg.getPrinAmt());
                flow.setFinprodId(newFinprodId);
                flow.setCreateTime(updateTime);
                flow.setCreateUser(updateUser);
                flow.setUpdateUser(updateUser);
                flow.setUpdateTime(updateTime);
                flow.setSeqno(IDGenerator.generate());
                flow.setTradeId(newFinprodId);
                ptlSecPositionFlowMapper.insert(flow);//新首期交易的流水PTL_SEC_POSITION
                PtlSecPosition secPosition= new PtlSecPosition();
                BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                PtlSecPositionFlowDto secPositionFlow =new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(secPositionFlow,flow);
                BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlow);
                if(amount==null){
                    ptlSecPositionMapper.insertSelective(secPosition);//新增汇总
                }else{
                    trdRepoDurationMapper.insertSelect04Sec(flow);//汇总更新
                }
            });
        }

        // 划款指令
        List<WflStlPayOrders> stlPayOrdersDtos = trdRepoDurationMapper.findOrdersByWorkflowId(trdRepoDurationDto.getWorkflowId());
        // 所有交易生效后调用的共通方法-	调用估值的入账信息
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(portfolioId);
        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
        inputDto.setTrdProductDeal(trdProductDealOrg);
        // 通道交易 新增
        inputDto.setTrdProductDealAdd(trdProductDealAdd);
        //inputDto.setTrdTradeFeeDetail(trdTradeFeeDetail);
        // 通道交易 新增
        inputDto.setRepoType("H01");//质押式协议回购
        inputDto.setActPortfolioId(portfolioId);//子组合代码
        inputDto.setPtlTradeList(ptlTradeList);
        if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
            StlPayOrders stlPayOrders = new StlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, stlPayOrdersDtos.get(0));
            inputDto.setStlPayOrders(stlPayOrders);
        }
        AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

    }

    /*查询续做信息
    * */
    public TrdRepoDurationDto findTrdDealInfoByBusiCode(String busiCode) {
        Map<String, Object> params =new HashMap<>();
        params.put("FEE_TYPE_YJF", DictCodeListExt1.一次性费用类型_佣金.getValue());
        params.put("busiCode",busiCode);
        TrdRepoDurationDto repoDurationDto=trdRepoDurationMapper.findTrdDealInfoByBusiCode(params);
        return repoDurationDto;
    }

    /**
     * 获取质押式回购原始交易信息
     * @param finprodId
     * @return 交易信息
     */
    public TrdProductDeal queryTrdProductDealByFinprodId(String finprodId){
        return trdProductDealMapper.selectByPrimaryKey(finprodId);
    }

    private void delete到期续做首期交易SecPositionFlow(WflTrdFinProductDealTotal wflTrdFinProductDealTotal,TrdRepoDurationDto trdRepoDurationDto,
                                               String updateUser) {
        if (wflTrdFinProductDealTotal != null) {
            Date updateTime = new Date();//插入时间
            String finprodType=trdRepoDurationDto.getFinprodType2();
            String mDate=trdRepoDurationDto.getmDatex();
            //查出原始交易编号
            String finprodId = trdRepoDurationMapper.findFinprodIdByTotleTradeId(wflTrdFinProductDealTotal.getpTradeId());
            String newTradeId=finprodId+ "R";
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(finprodType)) {
                Map<String, Object> params = new HashMap<>();
                String totleTradId = wflTrdFinProductDealTotal.getpTradeId();//原始交易的汇总交易编号

                params.put("finprodId", finprodId);
                params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
                params.put("mDate", mDate);
                List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);
                if (flows != null && !flows.isEmpty()) {
                    flows.forEach(flow -> {
                        flow.setCdate(wflTrdFinProductDealTotal.getSettleDate());
                        flow.setCreateTime(updateTime);
                        //flow.setUpdateTime(updateTime);
                        flow.setCreateUser(updateUser);
                        flow.setUpdateUser(updateUser);
                        flow.setSeqno(IDGenerator.generate());
                        flow.setTradeId(newTradeId);
                        ptlSecPositionFlowMapper.insert(flow);//新首期质押的PTL_SEC_POSITION
                        PtlSecPositionFlowDto secPositionFlowDto =new PtlSecPositionFlowDto();
                        PtlSecPosition secPosition= new PtlSecPosition();
                        BeanUtil.copyPropertiesIgnoreNull(secPositionFlowDto,flow);
                        BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                        BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlowDto);
                        if(amount==null) {//当天没余额
                            // 如果当天的持仓数据没有，取当天之前最近一天的数据
                            // 查询最近的那一天
                            Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(secPositionFlowDto);
                            if (maxCdate == null) {
                                ptlSecPositionMapper.insertSelective(secPosition);
                            } else {
                                PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                                BeanUtils.copyProperties(secPositionFlowDto, tempPositionFlowDto);
                                tempPositionFlowDto.setCdate(maxCdate);

                                BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPosition(tempPositionFlowDto);
                                secPosition.setAmount(ArithmeticUtil.doAdd(secPosition.getAmount(), tempAmount));
                                ptlSecPositionMapper.insertSelective(secPosition);
                            }
                        }
                        else{//当天有余额
                            trdRepoDurationMapper.insertSelect04Sec(flow);
                        }
                        EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                        BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlow, flow);//etr流水
                        etrSecPositionFlow.setAmount(ArithmeticUtil.multiply(etrSecPositionFlow.getAmount(), new BigDecimal(-1)));
                        etrSecPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        etrSecPositionFlow.setSeqno(IDGenerator.generate());
                        etrSecPositionFlowMapper.insertSelective(etrSecPositionFlow);//委托持仓流水
                        EtrSecPositionFlowDto etrSecPositionFlowDto=new EtrSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto, etrSecPositionFlow);//etr流水
                        EtrSecPosition etrSecPosition = new EtrSecPosition();//委托首期交易持仓
                        BeanUtil.copyPropertiesIgnoreNull(etrSecPosition, secPosition);
                        etrSecPosition.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        BigDecimal etramount = etrCommonCashPositionMapper.selectSecPosition(etrSecPositionFlowDto);
                        if (etramount == null) {
                            // 如果当天的持仓数据没有，取当天之前最近一天的数据
                            // 查询最近的那一天
                            Date maxCdate = etrCommonCashPositionMapper.selectSecPositionMaxCdate(etrSecPositionFlowDto);
                            if (maxCdate == null) {
                                etrSecPositionMapper.insertSelective(etrSecPosition);
                            }
                            else {
                                EtrSecPositionFlowDto tempPositionFlowDto = new EtrSecPositionFlowDto();
                                BeanUtils.copyProperties(etrSecPositionFlowDto, tempPositionFlowDto);
                                tempPositionFlowDto.setCdate(maxCdate);

                                BigDecimal tempAmount = etrCommonCashPositionMapper.selectSecPosition(tempPositionFlowDto);
                                etrSecPosition.setAmount(ArithmeticUtil.doAdd(etrSecPosition.getAmount(), tempAmount));
                                etrSecPositionMapper.insertSelective(etrSecPosition);
                            }
                        }
                        else {
                            etrSecPosition.setAmount(ArithmeticUtil.doAdd(etrSecPosition.getAmount(), etramount));
                            etrSecPositionMapper.updateByPrimaryKeySelective(etrSecPosition);
                        }
                        // 2.2.2 检索组合持仓的最大日期
                        String maxCdate = etrCommonCashPositionMapper.selectSecMaxCdate(etrSecPositionFlowDto);
                        String cDate = DateUtil.format(etrSecPositionFlowDto.getCdate());
                        // 2.2.3 上面检索出的最大日期 <> 输入参数.日期，将输入参数.日期到2.2.2检索出来的最大日期的数据全部更新
                        if (maxCdate != null && !maxCdate.equals(cDate)) {
                            String minDate = maxCdate.compareTo(cDate) > 0 ? DateUtil.addDay(cDate, 1) : maxCdate;
                            String maxDate = maxCdate.compareTo(cDate) < 0 ? DateUtil.addDay(cDate, -1) : maxCdate;
                            Map<String, Object> etrParams = BeanUtil.object2Map(etrSecPositionFlowDto);
                            etrParams.put("minDate", minDate);
                            etrParams.put("maxDate", maxDate);
                            etrCommonCashPositionMapper.updateSecPosition(etrParams);
                        }
                    });
                }
            }


            Map<String, Object> params = new HashMap<>();//该笔首期交易入持仓表
            params.put("finprodId", finprodId);
            params.put("holdingType", DictCodeList.持仓类型_实际.getValue());
            params.put("mDate", mDate);
            List<PtlSecPositionFlow> flows = trdRepoDurationMapper.selectSecFlow(params);//查询原来首期的持仓的信息流水
            if (flows != null && !flows.isEmpty()) {
                flows.forEach(flow -> {
                    flow.setCdate(wflTrdFinProductDealTotal.getSettleDate());
                    flow.setCreateTime(updateTime);
                    flow.setCreateUser(updateUser);
                    flow.setUpdateUser(updateUser);
                    flow.setUpdateTime(updateTime);
                    flow.setSeqno(IDGenerator.generate());
                    flow.setTradeId(newTradeId);
                    ptlSecPositionFlowMapper.insert(flow);//新首期交易的流水PTL_SEC_POSITION
                    PtlSecPosition secPosition= new PtlSecPosition();
                    BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                    PtlSecPositionFlowDto secPositionFlow =new PtlSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(secPositionFlow,flow);
                    BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlow);
                    if(amount==null){
                        ptlSecPositionMapper.insertSelective(secPosition);//新增汇总
                    }else{
                        trdRepoDurationMapper.insertSelect04Sec(flow);//汇总更新
                    }
                });
            }
            //撤销首期交易的子组合
            if (!StringUtil.equals(trdRepoDurationDto.getActPortfolioId(),
                    trdRepoDurationDto.getPortfolioId())) {
                    PtlTrade deal = new PtlTrade();
                    deal.setBusiNo(finprodId);
                    deal.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                    deal.setPortfolioId(trdRepoDurationDto.getActPortfolioId());// 子组合
                    periodCommonMapper.insertRevokePtlTrade(deal, updateUser);
                }
            }
    }
    
    public String findCeTradeIdByTotleTradeId(String totleTradeId) {
        return trdRepoDurationMapper.findCeTradeIdByTotleTradeId(totleTradeId);
    }
}