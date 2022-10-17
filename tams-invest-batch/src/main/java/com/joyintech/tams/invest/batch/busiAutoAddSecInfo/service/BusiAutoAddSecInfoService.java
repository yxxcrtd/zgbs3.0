/*
 * 文件名：SecPosAutoGenerationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.busiAutoAddSecInfo.service;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.busiAutoAddSecInfo.dao.BusiAutoAddSecInfoMapper;
import com.joyintech.tams.invest.batch.busiAutoAddSecInfo.dto.BusiAutoAddFinProductBaseInfoDto;
import com.joyintech.tams.invest.batch.busiAutoAddSecInfo.dto.BusiAutoAddTradeProductDealDto;
import com.joyintech.tams.invest.batch.busiAutoAddSecInfo.dto.BusiAutoAddTradeProductDealRedisDto;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.FinalString;
import com.joyintech.tams.invest.common.constant.ValDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.valuationusemerge.TargetSearchDto;
import com.joyintech.tams.invest.common.redisinit.dao.RskRedisInitMapper;
import com.joyintech.tams.invest.common.redisinit.dto.ImplAsset;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券自动上市流通批处理
 *
 * @author sjj
 * @version 1.0 2021年04月07日
 * @see BusiAutoAddSecInfoService
 * @since 1.0
 */
@Service
public class BusiAutoAddSecInfoService {

    @Autowired
    private BusiAutoAddSecInfoMapper busiAutoAddSecInfoMapper;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
//    @Autowired
//    private FinProductCommonMapper finProductCommonMapper;
    @Autowired
    private FinCashPayRuleMapper finCashPayRuleMapper;
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;
    @Autowired
    private FinRateInfoMapper finRateInfoMapper;
    @Autowired
    private FinCashPlanMapper finCashPlanMapper;
    @Autowired
    private FinExercisePlanMapper finExercisePlanMapper;
    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;
    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;
    @Autowired
    private MstBondCreditRatingMapper mstBondCreditRatingMapper;
    @Autowired
    private RepAssetBondMapper repAssetBondMapper;
    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;
    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;
    @Autowired
    private RepAssetEquityMapper repAssetEquityMapper;
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;
    @Autowired
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private TrdFinProductBarterMapper trdFinProductBarterMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    
//    @Autowired
//    private IPlatformDate iPlatformDate;
    
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;
    
    @Autowired
    private RskRedisInitMapper rskRedisInitMapper;

    /**
     *  债券自动上市流通批处理
     *
     * @param batchDate 执行日
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor=Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void autoGeneration(Date batchDate,Date sysdate) {
        //1、获取未上市债券信息
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("FINPRODTYPE2F01",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        paramMap.put("YESORNO_Y",DictCodeListExt1.是否_是.getValue());
        List<FinProduct> listFinProduct = busiAutoAddSecInfoMapper.selectUnlistedSecList(paramMap);

        //2、获取接口表债券信息，按照简称完全匹配
        List<BusiAutoAddFinProductBaseInfoDto> baseFinProductBaseInfolist = new ArrayList<BusiAutoAddFinProductBaseInfoDto>();

        List<BusiAutoAddTradeProductDealDto> busiAutoAddTradeProductDealDtoList = new ArrayList<BusiAutoAddTradeProductDealDto>();

        for(FinProduct finProduct : listFinProduct){
            //查询接口表债券代码、全称
            InterfPayhsrcsecinfo interfPayhsrcsecinfo = busiAutoAddSecInfoMapper.getSecId(finProduct);
            if(interfPayhsrcsecinfo == null){
                continue;
            }
            //资产上市代码
            String secId = interfPayhsrcsecinfo.getSecid();

            BusiAutoAddFinProductBaseInfoDto baseFinProductBaseInfoDto = new BusiAutoAddFinProductBaseInfoDto();
            //2.1、复制未上市债券信息，并修改金融产品代码为上市金融产品代码
            //标的类金融产品表
            FinProduct finProductNew = new FinProduct();
            BeanUtil.copyPropertiesIgnoreNull(finProductNew, finProduct);
            finProductNew.setFinprodId("F01_"+secId);
            finProductNew.setFinprodName(interfPayhsrcsecinfo.getSecfullname());
            finProductNew.setFinprodMarketId(secId);
            baseFinProductBaseInfoDto.setFinProduct(finProductNew);

            //List<现金流信息Entity>
            // 查询条件
            List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

            //金融产品附属表
            TargetSearchDto searchAddinfo = new TargetSearchDto();
            searchAddinfo.setQueryType("FINPROD_ADD");
            queryCriteria.add(searchAddinfo);

            //金融产品分类表
            TargetSearchDto searchTypeinfo = new TargetSearchDto();
            searchTypeinfo.setQueryType("FINPROD_TYPE");
            queryCriteria.add(searchTypeinfo);

            //金融产品分类表
            TargetSearchDto searchCashinfo = new TargetSearchDto();
            searchCashinfo.setQueryType("FINCASH_INFO");
            queryCriteria.add(searchCashinfo);

            // 现金流计算规则表
            TargetSearchDto searchCashCalcRuleinfo = new TargetSearchDto();
            searchCashCalcRuleinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            queryCriteria.add(searchCashCalcRuleinfo);

            // 现金流利率信息表
            TargetSearchDto searchFinRateinfo = new TargetSearchDto();
            searchFinRateinfo.setQueryType("FINRATE_INFO");
            queryCriteria.add(searchFinRateinfo);

            // 现金流支付规则条件
            TargetSearchDto searchPayinfo = new TargetSearchDto();
            searchPayinfo.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
            queryCriteria.add(searchPayinfo);

            // 还本计划条件
            TargetSearchDto searchRedinfo = new TargetSearchDto();
            searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            queryCriteria.add(searchRedinfo);

            // 行权计划条件
            TargetSearchDto searchfinExercisePlan = new TargetSearchDto();
            searchfinExercisePlan.setQueryType("FINEXERCISE_PLAN");
            queryCriteria.add(searchfinExercisePlan);

            // 评级信息
            TargetSearchDto searchMarketRating = new TargetSearchDto();
            searchMarketRating.setQueryType("Market_Rating");
            queryCriteria.add(searchMarketRating);

            Map<TargetSearchDto, Object> targetSearchMap = this.getTargetInfo(
                    finProduct.getFinprodId(), queryCriteria);

            //标的类金融产品附属表
            FinProductAdd finProductAddNew = new FinProductAdd();
            BeanUtil.copyPropertiesIgnoreNull(finProductAddNew, targetSearchMap.get(searchAddinfo));
            finProductAddNew.setFinprodId("F01_"+secId);
            finProductAddNew.setWindId(secId);
            finProductAddNew.setIsAutoUpdate(DictCodeList.是否_是.getValue());
            finProductAddNew.setIsIssueAsset(DictCodeList.是否_否.getValue());
            baseFinProductBaseInfoDto.setFinProductAdd(finProductAddNew);

            //金融产品分类表
            FinProductType finProductTypeNew = new FinProductType();
            BeanUtil.copyPropertiesIgnoreNull(finProductTypeNew, targetSearchMap.get(searchTypeinfo));
            finProductTypeNew.setFinprodId("F01_"+secId);
            baseFinProductBaseInfoDto.setFinProductType(finProductTypeNew);

            //现金流信息表
            // TODO
            List<FinCashInfo> finCashInfolist = new ArrayList<FinCashInfo>();
            Map<String,String> paraMap =new HashMap<String,String>();
            String cashIdB = CommonUtil.generateCashFlowCode(finProductNew.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L, DictCodeListExt1.现金流二级类型_本金.getValue());
            String cashIdL = CommonUtil.generateCashFlowCode(finProductNew.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L, DictCodeListExt1.现金流二级类型_利息.getValue());

            for(FinCashInfo finCashInfo: (List<FinCashInfo>)targetSearchMap.get(searchCashinfo)){
                if(StringUtil.equals(DictCodeListExt1.现金流二级类型_本金.getValue(),finCashInfo.getCashType2())){
                    if(StringUtil.isBlank(paraMap.get(finCashInfo.getCashId()))){
                        paraMap.put(finCashInfo.getCashId(),cashIdB);
                    }
                    finCashInfo.setCashId(cashIdB);
                    finCashInfo.setFinprodId(finProductNew.getFinprodId());
                }else if(StringUtil.equals(DictCodeListExt1.现金流二级类型_利息.getValue(),finCashInfo.getCashType2())){
                    if(StringUtil.isBlank(paraMap.get(finCashInfo.getCashId()))){
                        paraMap.put(finCashInfo.getCashId(),cashIdL);
                    }
                    finCashInfo.setCashId(cashIdL);
                    finCashInfo.setFinprodId(finProductNew.getFinprodId());
                }
                finCashInfolist.add(finCashInfo);
            }
            baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfolist);

            //现金流计算规则表
            List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<FinCashCalcRule>();
            for(FinCashCalcRule finCashCalcRule: (List<FinCashCalcRule>)targetSearchMap.get(searchCashCalcRuleinfo)){
                if(paraMap.containsKey(finCashCalcRule.getCashId())){
                    finCashCalcRule.setCashId(paraMap.get(finCashCalcRule.getCashId()));
                    finCashCalcRule.setFinprodId(finProductNew.getFinprodId());
                    finCashCalcRuleList.add(finCashCalcRule);
                }
            }
            baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);

            //现金流利率信息表
            List<FinRateInfo> finRateInfoList = new ArrayList<FinRateInfo>();
            for(FinRateInfo finRateInfo: (List<FinRateInfo>)targetSearchMap.get(searchFinRateinfo)){
                if(paraMap.containsKey(finRateInfo.getCashId())){
                    finRateInfo.setCashId(paraMap.get(finRateInfo.getCashId()));
                    finRateInfo.setFinprodId(finProductNew.getFinprodId());
                    finRateInfoList.add(finRateInfo);
                }
            }
            baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);

            //现金流支付规则表
            List<FinCashPayRule> finCashPayRuleList = new ArrayList<FinCashPayRule>();
            for(FinCashPayRule finCashPayRule: (List<FinCashPayRule>)targetSearchMap.get(searchPayinfo)){
                if(paraMap.containsKey(finCashPayRule.getCashId())){
                    finCashPayRule.setCashId(paraMap.get(finCashPayRule.getCashId()));
                    finCashPayRule.setFinprodId(finProductNew.getFinprodId());
                    finCashPayRuleList.add(finCashPayRule);
                }
            }
            baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);

            //现金流计划
            List<FinCashPlan> finCashPlanList = new ArrayList<FinCashPlan>();
            for(FinCashPlan finCashPlan: (List<FinCashPlan>)targetSearchMap.get(searchRedinfo)){
                if(paraMap.containsKey(finCashPlan.getCashId())){
                    finCashPlan.setCashId(paraMap.get(finCashPlan.getCashId()));
                    finCashPlan.setFinprodId(finProductNew.getFinprodId());
                    finCashPlanList.add(finCashPlan);
                }
            }
            baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);

            //行权计划表
            List<FinExercisePlan> finExercisePlanList = new ArrayList<FinExercisePlan>();
            for(FinExercisePlan finExercisePlan: (List<FinExercisePlan>)targetSearchMap.get(searchfinExercisePlan)){
                if(paraMap.containsKey(finExercisePlan.getCashId())){
                    finExercisePlan.setCashId(paraMap.get(finExercisePlan.getCashId()));
                    finExercisePlan.setFinprodId(finProductNew.getFinprodId());
                    finExercisePlanList.add(finExercisePlan);
                }
            }
            baseFinProductBaseInfoDto.setFinExercisePlanList(finExercisePlanList);

            //评级信息表
            List<MstBondCreditRating> mstBondCreditRatingList = new ArrayList<MstBondCreditRating>();
            for(MstBondCreditRating mstBondCreditRating: (List<MstBondCreditRating>)targetSearchMap.get(searchMarketRating)){
                mstBondCreditRating.setoCode(secId);
                mstBondCreditRatingList.add(mstBondCreditRating);
            }
            baseFinProductBaseInfoDto.setMstBondCreditRatingList(mstBondCreditRatingList);

            //债券资产报送信息
            RepAssetBond repAssetBondNew = new RepAssetBond();
            RepAssetBond repAssetBond = repAssetBondMapper.selectByPrimaryKey(finProduct.getFinprodId());
            if(repAssetBond !=null){
                BeanUtil.copyPropertiesIgnoreNull(repAssetBondNew, repAssetBond);
                repAssetBondNew.setRepFinprodId("F01_"+secId);
                repAssetBondNew.setSecId(secId);
                repAssetBondNew.setCreateTime(new Date());
                repAssetBondNew.setUpdateTime(new Date());
                baseFinProductBaseInfoDto.setRepAssetBond(repAssetBondNew);
            }

            //资产负债报送信息
            RepAssetDept repAssetDeptNew = new RepAssetDept();
            RepAssetDept repAssetDept = repAssetDeptMapper.selectByPrimaryKey(finProduct.getFinprodId());
            if(repAssetDept !=null){
                BeanUtil.copyPropertiesIgnoreNull(repAssetDeptNew, repAssetDept);
                repAssetDeptNew.setRepFinprodId("F01_"+secId);
                repAssetDeptNew.setFinprodMarketId(secId);
                repAssetDeptNew.setUpdateTime(new Date());
                repAssetDeptNew.setCreateTime(new Date());
                baseFinProductBaseInfoDto.setRepAssetDept(repAssetDeptNew);
            }

            //中债资产其他要素
            RepAssetOther repAssetOtherNew = new RepAssetOther();
            RepAssetOther repAssetOther = repAssetOtherMapper.selectByPrimaryKey(finProduct.getFinprodId());
            if(repAssetOther !=null){
                BeanUtil.copyPropertiesIgnoreNull(repAssetOtherNew, repAssetOther);
                repAssetOtherNew.setRepFinprodId("F01_"+secId);
                repAssetOtherNew.setUpdateTime(new Date());
                repAssetOtherNew.setCreateTime(new Date());
                baseFinProductBaseInfoDto.setRepAssetOther(repAssetOtherNew);
            }

            //权益类资产报送信息
            RepAssetEquity repAssetEquityNew = new RepAssetEquity();
            RepAssetEquity repAssetEquity = repAssetEquityMapper.selectByPrimaryKey(finProduct.getFinprodId());
            if(repAssetEquity !=null){
                BeanUtil.copyPropertiesIgnoreNull(repAssetEquityNew, repAssetEquity);
                repAssetEquityNew.setRepFinprodId("F01_"+secId);
                repAssetEquityNew.setRepSharesId(secId);
                repAssetEquityNew.setUpdateTime(new Date());
                repAssetEquityNew.setCreateTime(new Date());
                baseFinProductBaseInfoDto.setRepAssetEquity(repAssetEquityNew);
            }

            //2.2 根据组合持仓重组各组合上市流通交易
            //2.2.1 处理各组合指令持仓数据
            Map<String,String> tempMap =new HashMap<String,String>();
            tempMap.put("finprodId",finProduct.getFinprodId());
            tempMap.put("DealDate", DateUtil.addDay(DateUtil.format(batchDate),-1));
            tempMap.put("HODING_TYPE_E01",DictCodeListExt1.持仓类型_委托指令可用.getValue());
            List<EtrSecPosition> etrSecPositionList = busiAutoAddSecInfoMapper.getEtrSecPositionList(tempMap);
            List<EtrSecPositionFlow> etrSecPositionflowlist =new ArrayList<EtrSecPositionFlow>();
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            String tradeid =  autoPeriodCommonService.buildBussinessCode(parameter);
            for(EtrSecPosition etrSecPosition : etrSecPositionList){
                EtrSecPositionFlow orgetrSecPositionFlow =new EtrSecPositionFlow();
                EtrSecPositionFlow targetrSecPositionFlow =new EtrSecPositionFlow();
                //发行代码对应流水
                BeanUtil.copyPropertiesIgnoreNull(orgetrSecPositionFlow, etrSecPosition);
                orgetrSecPositionFlow.setSeqno(IDGenerator.generate());
                orgetrSecPositionFlow.setAmount(ArithmeticUtil.multiply(etrSecPosition.getAmount(),new BigDecimal("-1")));
                orgetrSecPositionFlow.setTradeId(tradeid);
                orgetrSecPositionFlow.setCdate(batchDate);
                etrSecPositionflowlist.add(orgetrSecPositionFlow);

                //上市流通代码对应持仓
                BeanUtil.copyPropertiesIgnoreNull(targetrSecPositionFlow, orgetrSecPositionFlow);
                targetrSecPositionFlow.setSeqno(IDGenerator.generate());
                targetrSecPositionFlow.setAmount(ArithmeticUtil.multiply(targetrSecPositionFlow.getAmount(),new BigDecimal("-1")));
                targetrSecPositionFlow.setFinprodId("F01_"+secId);
                targetrSecPositionFlow.setTradeId(tradeid);
                targetrSecPositionFlow.setCdate(batchDate);
                etrSecPositionflowlist.add(targetrSecPositionFlow);
            }
            baseFinProductBaseInfoDto.setEtrSecPositionFlowlist(etrSecPositionflowlist);
            //2.2.2 处理各组合持仓数据
            tempMap.put("HODING_TYPE_03",DictCodeList.持仓类型_实际.getValue());
            List<PtlSecPosition> ptlSecPositionList = busiAutoAddSecInfoMapper.getPtlSecPositionList(tempMap);
            List<PtlSecPositionFlow> ptlSecPositionflowList = new ArrayList<PtlSecPositionFlow>();
            List<PtlSecPositionFlow> ptlSecPositionflowTempList = new ArrayList<PtlSecPositionFlow>();
            for(PtlSecPosition ptlSecPosition : ptlSecPositionList){
                String ptlsectradeid =  autoPeriodCommonService.buildBussinessCode(parameter);
                PtlSecPositionFlow orgptlSecPositionFlow =new PtlSecPositionFlow();
                PtlSecPositionFlow targptlSecPositionFlow =new PtlSecPositionFlow();
                BeanUtil.copyPropertiesIgnoreNull(orgptlSecPositionFlow, ptlSecPosition);
                //发行代码对应流水
                orgptlSecPositionFlow.setSeqno(IDGenerator.generate());
                orgptlSecPositionFlow.setAmount(ArithmeticUtil.multiply(ptlSecPosition.getAmount(),new BigDecimal("-1")));
                orgptlSecPositionFlow.setTradeId(ptlsectradeid);
                orgptlSecPositionFlow.setCdate(batchDate);
                ptlSecPositionflowList.add(orgptlSecPositionFlow);

                //上市流通代码对应持仓
                BeanUtil.copyPropertiesIgnoreNull(targptlSecPositionFlow, orgptlSecPositionFlow);
                targptlSecPositionFlow.setSeqno(IDGenerator.generate());
                targptlSecPositionFlow.setAmount(ArithmeticUtil.multiply(targptlSecPositionFlow.getAmount(),new BigDecimal("-1")));
                targptlSecPositionFlow.setFinprodId("F01_"+secId);
                targptlSecPositionFlow.setTradeId(ptlsectradeid);
                targptlSecPositionFlow.setCdate(batchDate);
                ptlSecPositionflowList.add(targptlSecPositionFlow);
                ptlSecPositionflowTempList.add(targptlSecPositionFlow);
            }
            baseFinProductBaseInfoDto.setPtlSecPositionFlowlist(ptlSecPositionflowList);

            baseFinProductBaseInfolist.add(baseFinProductBaseInfoDto);

            //2.3 生成上市流通交易
            BusiAutoAddTradeProductDealDto busiAutoAddTradeProductDealDto =new BusiAutoAddTradeProductDealDto();
            if(baseFinProductBaseInfolist != null && baseFinProductBaseInfolist.size() > 0){
                busiAutoAddTradeProductDealDto = this.generationDeal(ptlSecPositionflowTempList,finProduct,secId,batchDate,tradeid);
                busiAutoAddTradeProductDealDtoList.add(busiAutoAddTradeProductDealDto);
            }

            //2.4 更新原有标的上市代码
            this.updateFinProdAdd(finProduct,secId);
        }
        //入库加券信息
        if(baseFinProductBaseInfolist != null && baseFinProductBaseInfolist.size() > 0){
            this.insert(baseFinProductBaseInfolist);
            //加券后资产信息载入redis
            if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
            	if(redisRriter!=null) {
                    ImplCond cond = new ImplCond();
                    cond.setDel(false);
                    cond.setPlateDate(sysdate);
                    for (BusiAutoAddFinProductBaseInfoDto dto : baseFinProductBaseInfolist) {
                    	if(dto.getFinProduct()!=null&&StringUtil.isNotEmpty(dto.getFinProduct().getFinprodId())) {
                    		cond.setAssetIdArray(dto.getFinProduct().getFinprodId());
                            redisRriter.dealAssetData(cond);
                    	}
                    }	
            	}
            }
        }
        //入库交易信息
        if(busiAutoAddTradeProductDealDtoList !=null && busiAutoAddTradeProductDealDtoList.size() > 0){
            this.insertDeal(busiAutoAddTradeProductDealDtoList);
        }
    }

    //更新原有标的上市代码
    private void updateFinProdAdd(FinProduct finProduct,String secId) {
        FinProductAdd finProductAdd = new FinProductAdd();
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        finProductAdd.setAppearMarketId(secId);
        busiAutoAddSecInfoMapper.updateByPrimaryKey(finProductAdd);
    }

    //生成上市流通交易
    private BusiAutoAddTradeProductDealDto generationDeal(List<PtlSecPositionFlow> ptlSecPositionflowTempList,FinProduct finProduct,String secId,Date batchDate,String totalTradeId) {
        BusiAutoAddTradeProductDealDto busiAutoAddTradeProductDealDto =new BusiAutoAddTradeProductDealDto();
        //redis处理
        BusiAutoAddTradeProductDealRedisDto busiAutoAddTradeProductDealRedis =new BusiAutoAddTradeProductDealRedisDto();
        List<BaseTradeInfoDto> baseTradeInfoList =new ArrayList<BaseTradeInfoDto>();

        List<TrdProductDeal> deals = new ArrayList<>();
        List<TrdProductDealAdd> dealAdds = new ArrayList<>();
        List<TrdFinProductBarter> dealBarters = new ArrayList<>();
        List<PtlTrade> dealPtlTrade = new ArrayList<>();
        busiAutoAddTradeProductDealDto.setTrdProductDeals(deals);
        busiAutoAddTradeProductDealDto.setTrdProductDealAdds(dealAdds);
        busiAutoAddTradeProductDealDto.setTrdFinProductBarterList(dealBarters);
        busiAutoAddTradeProductDealDto.setPtlTrades(dealPtlTrade);

        //金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal =new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(totalTradeId);
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        trdFinProductDealTotal.setBusiType(DictCodeList.业务类型_债券交易.getValue());
        trdFinProductDealTotal.setFinprodId("F01_"+secId);
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_上市流通.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setTradeDate(batchDate);
        trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdFinProductDealTotal.setBranch(0L);
        busiAutoAddTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);

        //金融产品实物汇总交易
        TrdFinProductBarterTotal trdFinProductBarterTotal =new TrdFinProductBarterTotal();
        trdFinProductBarterTotal.setTradeId(totalTradeId);
        trdFinProductBarterTotal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        trdFinProductBarterTotal.setFinprodId(finProduct.getFinprodId());
        trdFinProductBarterTotal.setBranch(0L);
        busiAutoAddTradeProductDealDto.setTrdFinProductBarterTotal(trdFinProductBarterTotal);

        for(PtlSecPositionFlow ptlSecPositionFlow:ptlSecPositionflowTempList){
                //金融产品交易表
                TrdProductDeal trdProductDeal =new TrdProductDeal();
                trdProductDeal.setTradeId(ptlSecPositionFlow.getTradeId());
                trdProductDeal.setInvAim(ptlSecPositionFlow.getInvAim());
                trdProductDeal.setTotleTradeId(totalTradeId);
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                trdProductDeal.setBusiType(DictCodeList.业务类型_债券交易.getValue());
                trdProductDeal.setFinprodId("F01_"+secId);
                trdProductDeal.setTradeDate(batchDate);
                trdProductDeal.setPs(DictCodeList.交易方向_上市流通.getValue());
                trdProductDeal.setBranch(0L);
                trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                trdProductDeal.setSecManageAcctId(ptlSecPositionFlow.getSecAcctId());
                deals.add(trdProductDeal);
                //金融产品交易扩展表
                TrdProductDealAdd trdProductDealAdd =new TrdProductDealAdd();
                trdProductDealAdd.setTradeId(ptlSecPositionFlow.getTradeId());
                trdProductDealAdd.setPortfolioId(ptlSecPositionFlow.getPortfolioId());
                //如果是通过投资，则组合换成通道的账套
                FinProduct finProductTemp = finProductMapper.selectByPrimaryKey(ptlSecPositionFlow.getSecAcctId());
                if(finProductTemp !=null && StringUtil.equals(finProductTemp.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_通道.getValue())){
                    String TempPortfolioId = DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+ FinalString.LAYERING_SPLIT+ptlSecPositionFlow.getPortfolioId();
                    trdProductDealAdd.setPortfolioId(TempPortfolioId + FinalString.LAYERING_SPLIT+ptlSecPositionFlow.getSecAcctId());
                }
                trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
                dealAdds.add(trdProductDealAdd);
                //金融产品交易明细表
                TrdFinProductBarter trdFinProductBarter =new TrdFinProductBarter();
                trdFinProductBarter.setTradeId(ptlSecPositionFlow.getTradeId());
                trdFinProductBarter.setFinprodId(finProduct.getFinprodId());
                trdFinProductBarter.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                trdFinProductBarter.setInvAim(ptlSecPositionFlow.getInvAim());
                trdFinProductBarter.setBranch(0L);
                dealBarters.add(trdFinProductBarter);

                //生成组合交易表(PTL_TRADE)
                PtlTrade ptlTrade = new PtlTrade();
                ptlTrade.setPortfolioId(ptlSecPositionFlow.getPortfolioId());
                ptlTrade.setBusiNo(ptlSecPositionFlow.getTradeId());
                ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
                ptlTrade.setBusiId("F01_"+secId);
                ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
                ptlTrade.setInvAim(ptlSecPositionFlow.getInvAim());
                ptlTrade.setSecManageAcctId(ptlSecPositionFlow.getSecAcctId());
                ptlTrade.setSettleDate(batchDate);
                dealPtlTrade.add(ptlTrade);

            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
//            inputDto.setPageId("TrdBondInvestTrade002");
            inputDto.setProdid(trdProductDealAdd.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            if(inputDto.getProdid().contains("F28_")){
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
            }
            inputDto.setTrdProductDeal(trdProductDeal);
            inputDto.setTrdProductDealAdd(trdProductDealAdd);
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);

            //组装redis需要的信息
            busiAutoAddTradeProductDealRedis.setPs(DictCodeList.交易方向_上市流通.getValue());
            busiAutoAddTradeProductDealRedis.setFinprodId(finProduct.getFinprodId());
            busiAutoAddTradeProductDealRedis.setAppearMarketId(secId);
            busiAutoAddTradeProductDealRedis.setTradeDate(batchDate);

            BaseTradeInfoDto baseTradeInfoDto =new BaseTradeInfoDto();
            baseTradeInfoDto.setTrdProductDeal(trdProductDeal);
            baseTradeInfoDto.setTrdProductDealAdd(trdProductDealAdd);
            baseTradeInfoList.add(baseTradeInfoDto);

            Map<String,Object> paraMap = new HashMap<>();
            paraMap.put("finprodId",finProduct.getFinprodId());
            paraMap.put("settleDate",DateUtil.format(batchDate));
            //获取债券面值
            BigDecimal UnitPrice= busiAutoAddSecInfoMapper.getUnitPrice(paraMap);
            baseTradeInfoDto.getTrdProductDeal().setShareAmt(ptlSecPositionFlow.getAmount().multiply(UnitPrice));
            //获取交易前一天组合持有的发行标的资产市值
            paraMap.put("tradeDate",DateUtil.format(DateUtil.addDay(batchDate,-1)));
            List<Map<String,Object>> saveList = busiAutoAddSecInfoMapper.getSaveParam(paraMap);
            //循环list根据组合赋值
            if (saveList != null && saveList.size() > 0){
                for (Map<String, Object> stringObjectMap : saveList) {
                    if (StringUtil.equals(stringObjectMap.get("PORTFOLIOID"),ptlSecPositionFlow.getPortfolioId())
                            && StringUtil.equals(stringObjectMap.get("INVAIM"),ptlSecPositionFlow.getInvAim())){
                        if (stringObjectMap.get("PRICE") != null){
                            baseTradeInfoDto.getTrdProductDeal().setTradeAmt(new BigDecimal(stringObjectMap.get("PRICE").toString()));
                        }
                    }
                }
            }
            //TODO
//            if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
//                AjaxResult ar = interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
//                        BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue(), DictCodeList.平安资产类型_债券.getValue(),
//                        (en) -> interfRiskRdsService.sendRiskRdsData(dto, en));
//                if (StringUtil.isNotEmpty(ar)) {
//                    return null;
//                }
//            }
        }
        busiAutoAddTradeProductDealRedis.setBaseTradeInfoDtos(baseTradeInfoList);
        //接入风险限额
        setForRiskRedis(busiAutoAddTradeProductDealRedis);
        return busiAutoAddTradeProductDealDto;
    }
    
    private void setForRiskRedis(BusiAutoAddTradeProductDealRedisDto busiAutoAddTradeProductDealRedis) {
        //由于无法直接调用实占接口，这里直接插入实占信息表，后续跑redis初始化批会对这里插入的实占信息进行计算。
        if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
        	//1. 获取资产相关信息
        	//1.1 资产标的
        	String reFinprodId = busiAutoAddTradeProductDealRedis.getFinprodId();
        	String newFinprodId = DictCodeListExt1.金融产品类型_投管用_债券.getValue()+"_"+busiAutoAddTradeProductDealRedis.getAppearMarketId();
        	//1.2 平安资产二级分类 - 通过redis初始化sql中获取
        	String reType2 = null,newType2 = null;
        	Map<String,Object> mapPass = new HashMap<>();
        	mapPass.put("valDate", DateUtil.format(busiAutoAddTradeProductDealRedis.getTradeDate()));
        	List<String> paramIdList = new ArrayList<>();
        	paramIdList.add(reFinprodId);
        	paramIdList.add(newFinprodId);
        	List<ImplAsset> implAssetList = rskRedisInitMapper.getPtlAssetData(mapPass);
        	for(ImplAsset asset:implAssetList) {
        		if(StringUtil.equals(asset.getAssetCode(), reFinprodId)) {
        			reType2 = asset.getAssetType2();
        		}
        		if(StringUtil.equals(asset.getAssetCode(), newFinprodId)) {
        			newType2 = asset.getAssetType2();
        		}
        	}
        	
        	for(BaseTradeInfoDto dto:busiAutoAddTradeProductDealRedis.getBaseTradeInfoDtos()) {
        		Map<String,Object> map = new HashMap<>();
        		map.put("tradeDate", DateUtil.format(busiAutoAddTradeProductDealRedis.getTradeDate()));
        		String tradeId =null;
        		if(dto.getTrdProductDeal()!=null) {
        			map.put("amount", dto.getTrdProductDeal().getTradeAmt());
        			map.put("number", dto.getTrdProductDeal().getShareAmt());
        			tradeId = dto.getTrdProductDeal().getTradeId();
        		}
        		if(dto.getTrdProductDealAdd()!=null) {
        			map.put("prodId",dto.getTrdProductDealAdd().getPortfolioId());
        		}
        		if(StringUtil.isNotEmpty(tradeId)) {
        			//新标的，买入
        			map.put("uuid",  String.valueOf(System.currentTimeMillis()));
        			map.put("eventId", tradeId+DictCodeList.交易方向_买入.getValue());
        			map.put("ps",DictCodeList.交易方向_买入.getValue());
        			map.put("assetCode", newFinprodId);
        			map.put("type1", DictCodeList.平安资产类型_债券.getValue());
        			map.put("type2", reType2);
        			busiAutoAddSecInfoMapper.insertRskOccupyInfo(map);
        			//旧标的，卖出
        			map.put("uuid",  String.valueOf(System.currentTimeMillis()));
        			map.put("eventId", tradeId+DictCodeList.交易方向_卖出.getValue());
        			map.put("ps",DictCodeList.交易方向_卖出.getValue());
        			map.put("assetCode", reFinprodId);
        			map.put("type1", DictCodeList.平安资产类型_债券.getValue());
        			map.put("type2", newType2);
        			busiAutoAddSecInfoMapper.insertRskOccupyInfo(map);
        		}
        	}
        }
    }

    //插入上市流通交易表
    private void insertDeal(List<BusiAutoAddTradeProductDealDto> busiAutoAddTradeProductDealDtoList) {
        BusiAutoAddTradeProductDealDto busiAutoAddTradeProductDealDto = busiAutoAddTradeProductDealDtoList.get(0);
        if(busiAutoAddTradeProductDealDto !=null){
            if(busiAutoAddTradeProductDealDto.getTrdFinProductDealTotal()!= null){
                trdFinProductDealTotalMapper.insert(busiAutoAddTradeProductDealDto.getTrdFinProductDealTotal());
            }
            if(busiAutoAddTradeProductDealDto.getTrdFinProductBarterTotal()!= null){
                trdFinProductBarterTotalMapper.insert(busiAutoAddTradeProductDealDto.getTrdFinProductBarterTotal());
            }
            if (!StringUtil.isEmptyList(busiAutoAddTradeProductDealDto.getTrdProductDeals())) {
                for (TrdProductDeal trdProductDeal : busiAutoAddTradeProductDealDto.getTrdProductDeals()) {
                    trdProductDealMapper.insert(trdProductDeal);
                }
            }
            if (!StringUtil.isEmptyList(busiAutoAddTradeProductDealDto.getTrdProductDealAdds())) {
                for (TrdProductDealAdd trdProductDealAdd : busiAutoAddTradeProductDealDto.getTrdProductDealAdds()) {
                    trdProductDealAddMapper.insert(trdProductDealAdd);
                }
            }
            if (!StringUtil.isEmptyList(busiAutoAddTradeProductDealDto.getTrdFinProductBarterList())) {
                for (TrdFinProductBarter trdFinProductBarter : busiAutoAddTradeProductDealDto.getTrdFinProductBarterList()) {
                    trdFinProductBarterMapper.insert(trdFinProductBarter);
                }
            }
            if (!StringUtil.isEmptyList(busiAutoAddTradeProductDealDto.getPtlTrades())) {
                for (PtlTrade ptlTrade : busiAutoAddTradeProductDealDto.getPtlTrades()) {
                    ptlTradeMapper.insert(ptlTrade);
                }
            }
        }

    }

    /**
     * 公用方法：标的类共通方法 - 标的信息查询
     * @param finprodId 标的代码【必输项】
     * @param queryCriteria 查询类型【必输项】
     * @return Map<TargetSearchDto, Object> 返回值获取，直接用查询实体类作为key值获取。
     */
    public Map<TargetSearchDto, Object> getTargetInfo(String finprodId,
                                                      List<TargetSearchDto> queryCriteria) {
        // 定义返回值
        Map<TargetSearchDto, Object> returnMap = new HashMap<TargetSearchDto, Object>();

        // 循环查询类型list,进行获取数据
        for (TargetSearchDto svo : queryCriteria) {
            // 获取当前查询类型
            String queryTypeNow = svo.getQueryType();

            // 设置sql查询参数map
            Map<String, Object> paMap = new HashMap<String, Object>();
            paMap.put("finprodId", finprodId);// 标的代码
            paMap.put("branch", new Long(0));
            // 根据传递的查询类型，获取不同的标的数据
            if(StringUtil.equals("FINPROD_ADD",queryTypeNow)){
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
                returnMap.put(svo, finProductAdd);
            }
            else if(StringUtil.equals("FINPROD_TYPE",queryTypeNow)){
                FinProductTypeKey key =new FinProductTypeKey();
                key.setFinprodId(finprodId);
                key.setBranch(0L);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
                returnMap.put(svo, finProductType);
            }
            else if(StringUtil.equals("FINCASH_INFO",queryTypeNow)){
                List<FinCashInfo> finCashInfolist = busiAutoAddSecInfoMapper.selectFinCashInfo(finprodId);
                returnMap.put(svo, finCashInfolist);
            }
            else if(StringUtil.equals(ValDictCodeList.标的_现金流计算规则.getValue(),queryTypeNow)){
                List<FinCashCalcRule> finCashCalcRuleList = busiAutoAddSecInfoMapper.getTargetCashCalcRuleList(paMap);
                returnMap.put(svo, finCashCalcRuleList);
            }
            else if(StringUtil.equals("FINRATE_INFO",queryTypeNow)){
                List<FinRateInfo> finRateInfoList = busiAutoAddSecInfoMapper.getFinRateInfoList(paMap);
                returnMap.put(svo, finRateInfoList);
            }
            else if (ValDictCodeList.标的_现金流支付规则.getValue().equals(queryTypeNow)) {
                // 查询标的现金流支付规则
                List<FinCashPayRule> finCashPayRuleList = busiAutoAddSecInfoMapper.getFinCashPayRuleList(paMap);
                // 查询结果塞入map中
                returnMap.put(svo, finCashPayRuleList);
            }
            else if (ValDictCodeList.标的_现金流计划.getValue().equals(queryTypeNow)) {
                // 查询标的现金流计划list
                List<FinCashPlan> finCashPlanList = busiAutoAddSecInfoMapper.getFinCashPlanList(paMap);
                // 查询结果塞入map中
                returnMap.put(svo, finCashPlanList);

            }
            else if ("FINEXERCISE_PLAN".equals(queryTypeNow)) {
                // 查询标的现金流计划list
                List<FinExercisePlan> finExerciseList = busiAutoAddSecInfoMapper.getFinExercisePlanList(paMap);
                // 查询结果塞入map中
                returnMap.put(svo, finExerciseList);

            }
            else if("Market_Rating".equals(queryTypeNow)){
                // 查询评级信息list
                List<MstBondCreditRating> mstBondCreditRatingList = busiAutoAddSecInfoMapper.selectMstBondCreditRating(finprodId.replace("F01_",""));
                mstBondCreditRatingList.addAll(busiAutoAddSecInfoMapper.selectMstinnercreditrating(finprodId.replace("F01_","")));
                // 查询结果塞入map中
                returnMap.put(svo, mstBondCreditRatingList);
            }
//            else if("REP_ASSET_BOND".equals(queryTypeNow)){
//                // 查询评级信息list
//                List<MstBondCreditRating> mstBondCreditRatingList = busiAutoAddSecInfoMapper.selectMstBondCreditRating(finprodId.replace("F01_",""));
//                mstBondCreditRatingList.addAll(busiAutoAddSecInfoMapper.selectMstinnercreditrating(finprodId.replace("F01_","")));
//                // 查询结果塞入map中
//                returnMap.put(svo, mstBondCreditRatingList);
//            }
//            else if("REP_ASSET_EQUITY".equals(queryTypeNow)){
//                // 查询评级信息list
//                List<BussRepEquityInfoDto> mstBondCreditRatingList = bussRepEquityInfoMapper.getBussRepEquityInfoByProductId(finprodId);
//                mstBondCreditRatingList.addAll(busiAutoAddSecInfoMapper.selectMstinnercreditrating(finprodId.replace("F01_","")));
//                // 查询结果塞入map中
//                returnMap.put(svo, mstBondCreditRatingList);
//            }
//            else if (ValDictCodeList.标的_现金流计算规则.getValue().equals(queryTypeNow)) {
//                // 查询标的现金流计算规则list
//                List<TargetCashCalcRule> targetCashCalcRuleList = finProductCommonMapper.getTargetCashCalcRuleList(paMap);
//                // 查询结果塞入map中
//                returnMap.put(svo, targetCashCalcRuleList);
//
//            }
//            else if (ValDictCodeList.标的_业绩报酬计提计划.getValue().equals(queryTypeNow)) {
//                // 查询标的业绩报酬计提计划list
//                List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = finProductCommonMapper.getTargetPerfRewardAccrscheList(paMap);
//                // 查询结果塞入map中
//                returnMap.put(svo, targetPerfRewardAccrscheList);
//
//            }
//            else if (ValDictCodeList.标的_业绩报酬计提规则.getValue().equals(queryTypeNow)) {
//                // 查询标的业绩报酬规则 list
//                List<TargetPerfRewardRule> targetPerfRewardRuleList = finProductCommonMapper.getTargetPerfRewardRuleList(paMap);
//
//                // 查询结果塞入map中
//                returnMap.put(svo, targetPerfRewardRuleList);
//            }
//            else if (ValDictCodeList.标的_业绩报酬计算规则.getValue().equals(queryTypeNow)) {
//                // 查询标的业绩报酬规则 list
//                List<TargetPerfRewardCalRule> targetPerfRewardCalRule = finProductCommonMapper.getTargetPerfRewardCalRuleList(paMap);
//
//                // 循环业绩报酬规则 list，获取每个对应的标的业绩报酬规则明细
//                for (TargetPerfRewardCalRule dto : targetPerfRewardCalRule) {
//                    // 获取对应的明细List
//                    List<TargetPerfRewardCalRuleDetail> details = finProductCommonMapper.getTargetPerfRewardRuleDetailList(
//                            dto.getCashId(), dto.getEffDate());
//                    // 将明显塞入实体类中
//                    dto.setTargetPerfRewardCalRuleDetailList(details);
//                }
//
//                // 查询结果塞入map中
//                returnMap.put(svo, targetPerfRewardCalRule);
//            }
//            /** modify by dongjun on 20190219 remark:标的_业绩回补计算规则 start**/
//            else if (ValDictCodeList.标的_业绩回补计算规则.getValue().equals(queryTypeNow)) {
//                // 查询标的_业绩报酬计算规则 list
//                List<TargetFinClawbackCalcRule> targetFinCawlbackCalcRuleList = finProductCommonMapper.getTargetFinCLawbackList(paMap);
//                // 循环标的_业绩回补计算规则 list，获取每个对应的标的业绩报酬规则明细
//                for (TargetFinClawbackCalcRule dto : targetFinCawlbackCalcRuleList) {
//                    // 获取对应的明细List
//                    List<TargetPerfRewardCalRuleDetail> details = finProductCommonMapper.getTargetPerfRewardRuleDetailList(
//                            dto.getCashId(), dto.getEffDate());
//                    // 将明显塞入实体类中
//                    dto.setTargetPerfRewardCalRuleDetailList(details);
//                }
//
//                // 查询结果塞入map中
//                returnMap.put(svo, targetFinCawlbackCalcRuleList);
//            }
            /** modify by dongjun on 20190219 remark:标的_业绩回补计算规则 end**/

        }

        return returnMap;
    }
    /**
     * 标的类金融产品基本信息
     */
    private void insert(List<BusiAutoAddFinProductBaseInfoDto> list) {
        if (list != null && list.size() > 0) {
            // 循环遍历list里的dto
            for (BusiAutoAddFinProductBaseInfoDto dto : list) {
                // 插入标的类金融产品表
                if (dto.getFinProduct() != null) {
                    // 一体化对应etliu start
                    ValItemValueGenUtil.genFinprodTypeForFinProduct(dto.getFinProduct(), dto.getFinProductType());
                    // 一体化对应etliu end
                    finProductMapper.insert(dto.getFinProduct());
                }
                // 插入标的类金融产品附属表
                if (dto.getFinProductAdd() != null) {
                    finProductAddMapper.insert(dto.getFinProductAdd());
                }
                // 插入金融产品分类表
                if (dto.getFinProductType() != null) {
                    ValItemValueGenUtil.genTypeForFinProductType(dto.getFinProduct(), dto.getFinProductType());
                    finProductTypeMapper.insert(dto.getFinProductType());
                }
                //现金流信息
                if (!StringUtil.isEmptyList(dto.getFinCashInfoList())) {
                    for (FinCashInfo finCashInfo : dto.getFinCashInfoList()) {
                        finCashInfoMapper.insert(finCashInfo);
                    }
                }
                //插入金融产品现金流计算规则表
                if (dto.getFinCashCalcRuleList()!=null && !dto.getFinCashCalcRuleList().isEmpty()) {
                    for (FinCashCalcRule finCashCalcRule : dto.getFinCashCalcRuleList()) {
                        finCashCalcRuleMapper.insert(finCashCalcRule);
                    }
                }
                //插入金融产品现金流利率信息表
                if (!StringUtil.isEmptyList(dto.getFinRateInfoList())) {
                    for (FinRateInfo finRateInfo : dto.getFinRateInfoList()) {
                        finRateInfoMapper.insert(finRateInfo);
                    }
                }
                //插入金融产品现金流支付规则表
                if (!StringUtil.isEmptyList(dto.getFinCashPayRuleList())) {
                    for (FinCashPayRule finCashPayRule : dto.getFinCashPayRuleList()) {
                        finCashPayRuleMapper.insert(finCashPayRule);
                    }
                }
                //插入金融产品现金流计划表
                if (!StringUtil.isEmptyList(dto.getFinCashPlanList())) {
                    for (FinCashPlan finCashPlan : dto.getFinCashPlanList()) {
                        finCashPlanMapper.insert(finCashPlan);
                    }
                }

                //插入金融产品行权计划表
                if (!StringUtil.isEmptyList(dto.getFinExercisePlanList())) {
                    for (FinExercisePlan finExercisePlan : dto.getFinExercisePlanList()) {
                        finExercisePlanMapper.insert(finExercisePlan);
                    }
                }
                //插入持仓表
                if (!StringUtil.isEmptyList(dto.getPtlSecPositionFlowlist())) {
                    for (PtlSecPositionFlow ptlSecPositionFlow : dto.getPtlSecPositionFlowlist()) {
                        ptlSecPositionFlowMapper.insert(ptlSecPositionFlow);
                    }
                }
                //插入指令持仓表
                if (!StringUtil.isEmptyList(dto.getEtrSecPositionFlowlist())) {
                    for (EtrSecPositionFlow etrSecPositionFlow : dto.getEtrSecPositionFlowlist()) {
                        etrSecPositionFlowMapper.insert(etrSecPositionFlow);
                    }
                }
                //插入评级表
                if (!StringUtil.isEmptyList(dto.getMstBondCreditRatingList())) {
                    for (MstBondCreditRating mstBondCreditRating : dto.getMstBondCreditRatingList()) {
                       mstBondCreditRatingMapper.insert(mstBondCreditRating);
                    }
                }
                // 插入标的类金融产品表
                if (dto.getRepAssetBond() != null) {
                    repAssetBondMapper.insert(dto.getRepAssetBond());
                }
                // 插入标的类金融产品表
                if (dto.getRepAssetDept() != null) {
                    repAssetDeptMapper.insert(dto.getRepAssetDept());
                }
                // 插入债券资产报送信息表
                if (dto.getRepAssetOther() != null) {
                    repAssetOtherMapper.insert(dto.getRepAssetOther());
                }
                // 插入权益类资产报送信息表
                if (dto.getRepAssetEquity() != null) {
                    repAssetEquityMapper.insert(dto.getRepAssetEquity());
                }
            }
        }
    }
}
