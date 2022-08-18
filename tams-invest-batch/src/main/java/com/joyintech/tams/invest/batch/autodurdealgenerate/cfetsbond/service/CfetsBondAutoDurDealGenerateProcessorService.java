/*
 * 文件名：BondAutoDurDealGenerateProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.entity.PaTrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.RegFinProductDeal;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.WflStlPayOrders;
import com.joyintech.tams.common.base.entity.WflTrdProductDeal;
import com.joyintech.tams.common.base.entity.WflTrdProductDealAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dao.CfetsBondAutoDurDealGenerateMapper;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsEtrIbPosPortfolioRequest;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsEtrIbPositionRequest;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsManualSettleDealInfo;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.dto.CfetsTrdConfirmMatchDto;
import com.joyintech.tams.invest.batch.common.dto.BatchBussSettleOrderParameterDto;
import com.joyintech.tams.invest.batch.common.service.PinganBatchCommonService;
import com.joyintech.tams.invest.common.autoperiod.bond.AutoPeriodBondCommonService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurDbInertDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * processor相关业务处理
 *
 * @author etliu
 * @version 1.0 2018年12月11日
 * @see CfetsBondAutoDurDealGenerateProcessorService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class CfetsBondAutoDurDealGenerateProcessorService extends BaseService {

    @Autowired
    private PinganBatchCommonService pinganBatchCommonService;

    @Autowired
    private AutoPeriodBondCommonService autoPeriodBondCommonService;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private CfetsBondAutoDurDealGenerateMapper cfetsBondAutoDurDealGenerateMapper;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;

    /**
     * 递增维度(元)
     */
    private static final BigDecimal faceDimension = new BigDecimal("0.01");

    /**
     * process 数据处理
     * @param batchDate 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public void execute(Date batchDate) {
        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("busiType", DictCodeList.共通业务类型_债券类资产存续期.getValue());
        paramMap1.put("finProdType2", Collections.singletonList(DictCodeListExt1.金融产品类型_投管用_债券.getValue()));
        paramMap1.put("updateUser", "system");
        paramMap1.put("batchDate", batchDate);
        //此处增加银行间限制，因为删除时增加了银行间限制
        paramMap1.put("tradeMarket",DictCodeListExt1.交易场所_银行间.getValue());
        //查询当日未交收的数据
        List<TrdFinProductDealTotal> totalTradeIds = cfetsBondAutoDurDealGenerateMapper.selectTrdFinProductDealTotalData(paramMap1);
        for(TrdFinProductDealTotal item : totalTradeIds){
            PositionHttpRequest.dorevoke(item.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),DictCodeListExt3.头寸业务类型_债券存续期交易.getValue(),item.getPs());
        }
        paramMap1.put("psT39",DictCodeList.交易方向_行权_赎回.getValue());
        //查询当日赎回未交收的数据
        List<TrdFinProductDealTotal> totalTradeId1s = cfetsBondAutoDurDealGenerateMapper.selectTrdFinProductDealTotalData(paramMap1);
        for(TrdFinProductDealTotal item : totalTradeId1s){
            PositionHttpRequest.dorevoke(item.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),DictCodeListExt3.头寸业务类型_债券行权交易.getValue(),item.getPs());
        }
        //删除当日数据
        autoPeriodCommonService.deleteActTempData(DictCodeList.共通业务类型_债券类资产存续期.getValue(),DictCodeListExt1.金融产品类型_投管用_债券.getValue(),batchDate,DictCodeListExt1.交易场所_银行间.getValue());
        // 待处理交易数据参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("statusValid", Collections.singletonList(DictCodeListExt1.画面数据状态_待处理.getValue()));
        paramMap.put("userName", LoginUserUtil.getUserName());
        paramMap.put("PLATEFORM_DATE", batchDate);
        // 查询待处理交易详细数据
        this.setParams(paramMap);
        List<TrdBondDurationDto> tradeDataList = cfetsBondAutoDurDealGenerateMapper.findAllData(paramMap);
        //查询行权赎回待处理数据
        paramMap.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());
        paramMap.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        paramMap.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        paramMap.put("YESNO_Y", DictCodeList.是否_是.getValue());
        paramMap.put("YESNO_N", DictCodeList.是否_否.getValue());
        List<RegFinProductDealTotal> regFinProductDealTotalList = cfetsBondAutoDurDealGenerateMapper.findShData(paramMap);

        List<TrdBondDurationDto> trdBondDurationDtoInsertList = new ArrayList<>();
        paramMap.put("CASH_TYPE_1", DictCodeListExt1.现金流一级类型_本金.getValue());
        paramMap.put("CASH_TYPE_2", DictCodeListExt1.现金流二级类型_本金.getValue());
        // cashId存储map,避免出现重复情况的数据库访问次数
        Map<String, String> cashIdMap = new HashMap<>();
        for (TrdBondDurationDto trdBondDurationDto : tradeDataList) {
            //重新查询单条记录数据
            paramMap.put("finprodId", trdBondDurationDto.getFinprodId());
            paramMap.put("finprodMarketId", trdBondDurationDto.getFinprodMarketId());
            //调整后的计算结束日
            paramMap.put("mDate", trdBondDurationDto.getMDate());
            paramMap.put("vDate", trdBondDurationDto.getVDate());
            paramMap.put("secAcctId", trdBondDurationDto.getSecManageAcctId());
            paramMap.put("adjustMDate", DateUtil.parse(trdBondDurationDto.getMDate()));
            TrdBondDurationDto pendingTradeData = cfetsBondAutoDurDealGenerateMapper.findEffectiveData(paramMap);

            //paramMap.put("busiCode", trdBondDurationDto.getBusiCode());
            paramMap.put("yield", pendingTradeData.getYield());
            List<FinAssetIntInfo> finAssetIntInfos = cfetsBondAutoDurDealGenerateMapper.findAssetIntInfo(paramMap);

            // 实收本息总额: 设置为 全价总额 理论本息总额
            pendingTradeData.setTradeAmt(pendingTradeData.getFpriceAmt());
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
            pendingTradeData.setBusiCode(autoPeriodCommonService.buildBussinessCode(parameter));
            pendingTradeData.setFinAssetIntInfoList(finAssetIntInfos);
            // 构造将要登录的业务流程表数据
            genDataForInsertDto(pendingTradeData);
            //业务模式
            String invAim = cfetsBondAutoDurDealGenerateMapper.findInvAimData(paramMap);
            if (StringUtil.isNotEmpty(invAim)) {
                if (pendingTradeData.getTrdBondDurDbInertDto() != null &&
                        CollectionUtils.isNotEmpty(pendingTradeData.getTrdBondDurDbInertDto().getTrdProductDealList())) {
                    pendingTradeData.getTrdBondDurDbInertDto().getTrdProductDealList().forEach(x -> {
                        x.setInvAim(invAim);
                        // 查找cashId
                        if (cashIdMap.get(x.getFinprodId()) == null) {
                            String cashId = cfetsBondAutoDurDealGenerateMapper.findCashIdByFinprodId(paramMap);
                            cashIdMap.put(x.getFinprodId(), cashId);
                        }
                        x.setCashId(cashIdMap.get(x.getFinprodId()));
                    });
                }

            }
            trdBondDurationDtoInsertList.add(pendingTradeData);
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            for (RegFinProductDealTotal regFinProductDealTotal : regFinProductDealTotalList) {
                // 构造将要登录的业务流程表数据
                TrdBondDurationDto TrdBondDurationDto = genDataForShInsertDto(regFinProductDealTotal, batchDate);
                autoPeriodCommonService.insertCfetsShTempDealData(TrdBondDurationDto.getTrdBondDurDbInertDto());
                //插入匹配关系表，且发送头寸
                // try {
                mapList.add(genMatchAndSendPosMethod(TrdBondDurationDto.getTrdBondDurDbInertDto(), regFinProductDealTotal));
                //}catch (Exception e){}
            }
            //插入数据
            for (TrdBondDurationDto trdBondDurationDto : trdBondDurationDtoInsertList) {
                autoPeriodCommonService.insertCfetsTempDealData(trdBondDurationDto.getTrdBondDurDbInertDto());
                mapList.add(sendPositionMethod(trdBondDurationDto.getTrdBondDurDbInertDto().getTrdFinProductDealTotal()));
            }
        } catch ( PositionException e) {
            if (CollectionUtils.isNotEmpty(mapList)) {
                mapList.forEach(item -> {
                    String revokeId = String.valueOf(item.get("seqno"));
                    String modelType = (String) item.get("modelType");
                    PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
                });
            }
            throw e;
        }
    }

    /**
     * 构建发送头寸信息
     * @param trdFinProductDealTotal
     * @return
     */
    private Map<String, Object> sendPositionMethod(TrdFinProductDealTotal trdFinProductDealTotal) {
        //组装组合金额信息
        List<CfetsEtrIbPosPortfolioRequest> ptlList=new ArrayList<>();
        List<CfetsManualSettleDealInfo> listTemp = cfetsBondAutoDurDealGenerateMapper.getDealDetailsByTradeId(trdFinProductDealTotal.getTotleTradeId());
        for(CfetsManualSettleDealInfo dto : listTemp){
            CfetsEtrIbPosPortfolioRequest positionRulePtlData = new CfetsEtrIbPosPortfolioRequest();
            positionRulePtlData.setPortfolioId(dto.getPortfolioId());
            positionRulePtlData.setTradeId(dto.getTradeId());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                map.put("ccy", dto.getCcy());
                String cashAcctId = cfetsBondAutoDurDealGenerateMapper.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                }
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = cfetsBondAutoDurDealGenerateMapper.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
            }
            positionRulePtlData.setSecAmount(dto.getShareAmt() == null? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getShareAmt(),new BigDecimal(100)));
            positionRulePtlData.setCashAmount(dto.getTradeAmt());
            positionRulePtlData.setInvAim(dto.getInvAim());
            positionRulePtlData.setOwnSecAmount(dto.getOurShareAmt() == null? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getOurShareAmt(),new BigDecimal(100)));
            positionRulePtlData.setIntegrateSecAmount(dto.getLendinShareAmt() == null? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getLendinShareAmt(),new BigDecimal(100)));
            ptlList.add(positionRulePtlData);
        }

        CfetsEtrIbPositionRequest req = new CfetsEtrIbPositionRequest();
        req.setInterfaceType("Bond001");
        //指令或交易标识	ETR:指令,TRD:交易
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        //用户名	当前操作用户
        req.setUserId("SYSTEM");
        //汇总交易编号	单交易时传交易编号
        req.setbTotalTradeId(trdFinProductDealTotal.getTotleTradeId());
        //汇总交易编号
        req.setTotalTradeId(trdFinProductDealTotal.getTotleTradeId());
        //流程状态	流程节点 ，提交 审批通过
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        //余额校验	Y：需校验   N：不需校验
        String balanceCheck="N";
        req.setBalanceCheck(balanceCheck);
        // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券存续期交易.getValue());
        //交易场所
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        //清算速度
        if(!StringUtil.isEmptyList(listTemp) && StringUtil.isNotEmpty(listTemp.get(0).getSettleSpeed())){
            req.setSettleSpeed(listTemp.get(0).getSettleSpeed().toString());
            if(listTemp.get(0).getSettleSpeed().compareTo(new Long(0))==0){
                req.setSettleSpeed("01");
            }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(1))==0){
                req.setSettleSpeed("02");
            }else if(listTemp.get(0).getSettleSpeed().compareTo(new Long(2))==0){
                req.setSettleSpeed("03");
            }
        }else{
            req.setSettleSpeed("01");
        }
        //业务动作BUSI_ACT 22 待交收
        req.setBusiAct("22");
        //交易方向
        req.setPs(trdFinProductDealTotal.getPs());
        //新建 修改 关闭
        req.setSystemAct("01");
        req.setDecisionAct("01");
        req.setCdate(trdFinProductDealTotal.getTradeDate());
        req.setCcy(trdFinProductDealTotal.getCcy());
        req.setFinprodId(trdFinProductDealTotal.getFinprodId());
        req.setPtlList(ptlList);
        return PositionHttpRequest.send(req);
    }


    private TrdBondDurationDto genDataForShInsertDto(RegFinProductDealTotal regFinProductDealTotal,Date batchDate) {
        TrdBondDurDbInertDto trdBondDurDbInertDto = new TrdBondDurDbInertDto();
        TrdBondDurationDto trdBondDurationDto =new  TrdBondDurationDto();
        //查询登记明细相关表
        Map<String, Object> map = new HashMap<String, Object>();
        //根据头寸查询当天需要进行赎回的明细数据
        map.put("finprodId",regFinProductDealTotal.getFinprodId());
        map.put("cdate",regFinProductDealTotal.getSettleDate());//理论行权日
        map.put("A_001", DictCodeListExt3.资产类型_资产账户持仓.getValue());
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("SEC_ACCT_ID",regFinProductDealTotal.getSecManageAcctId());//证券账户
        map.put("ACCT_NO",regFinProductDealTotal.getAcctId());//资金账户
        List<RegFinProductDeal> regFinProductDealList =cfetsBondAutoDurDealGenerateMapper.queryRegDealInfo(map);

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        String busiCode = autoPeriodCommonService.buildBussinessCode(parameter);

        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        List<TrdProductDealAdd> trdProductDealaddList = new ArrayList<>();
        List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        //净价总额计算,净价总额=账户持仓券面总额（万元）*10000*赎回比例%*行权净价/100
        BigDecimal cpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(regFinProductDealTotal.getFaceValue(),regFinProductDealTotal.getRedeemRate(),regFinProductDealTotal.getExercisePrice()),new BigDecimal(100),2) ;
        //应计利息计算
        BigDecimal intAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(regFinProductDealTotal.getFaceValue(),regFinProductDealTotal.getRedeemRate(),regFinProductDealTotal.getIntAmt()),new BigDecimal(100),2) ;
        //赎回金额计算：默认等于净价总额+应计利息总额；
        BigDecimal tradeAmt = ArithmeticUtil.doAdd(cpriceAmt,intAmt);
        //乘赎回比例
        BigDecimal shareAmt = ArithmeticUtil.multiply(regFinProductDealTotal.getShareAmt(),regFinProductDealTotal.getRedeemRate());

        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, regFinProductDealTotal);
        trdFinProductDealTotal.setTotleTradeId(busiCode);
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(regFinProductDealTotal.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        trdFinProductDealTotal.setSecManageAcctId(regFinProductDealTotal.getSecManageAcctId());
        trdFinProductDealTotal.setCcy(regFinProductDealTotal.getCcy());
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_行权_赎回.getValue());
        trdFinProductDealTotal.setBranch(0L);
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(DateUtil.format(batchDate)));
      //  trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getvDate()));
      //  trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getmDate()));
        //trdFinProductDealTotal.setSettleDate(DateUtil.parse(DateUtil.format(batchDate)));
        trdFinProductDealTotal.setSettleDate(regFinProductDealTotal.getActExerciseDate());
        trdFinProductDealTotal.setShareAmt(shareAmt);
       // trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCpriceAmt(cpriceAmt);
        trdFinProductDealTotal.setPrinAmt(cpriceAmt);
        trdFinProductDealTotal.setIntAmt(intAmt);
        //trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
       // trdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setTgBusiType(AutoPeriodCommonService.change交易方向To投管类型(trdFinProductDealTotal.getPs()));
        trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        trdFinProductDealTotal.setFpriceAmt(tradeAmt);
        trdFinProductDealTotal.setTradeMarket(regFinProductDealTotal.getTradeMarket());
        trdFinProductDealTotal.setTradeMarketMode(BussinessDictCodeList.交易市场_二级市场.getValue());
        trdFinProductDealTotal.setTradeAmt(tradeAmt);
      //  trdFinProductDealTotal.setCounterId(entity.getIssuer());
        BeanUtil.copyPropertiesIgnoreNull(trdDealTotalAdd, regFinProductDealTotal);
        trdDealTotalAdd.setTotleTradeId(busiCode);
        trdDealTotalAdd.setOurShareAmt(trdFinProductDealTotal.getShareAmt());
        //组合分配，按照账户持仓占比进行分配
        //降序
        regFinProductDealList.sort((p1,p2) -> p1.getFaceValue().compareTo(p2.getFaceValue()));
        //账户持仓汇总值
        BigDecimal avaNumTotal = regFinProductDealTotal.getFaceValue();
        //尾差
        BigDecimal tailDifferenceCprice = cpriceAmt;
        BigDecimal tailDifferenceShAmt = tradeAmt;

        for (RegFinProductDeal regFinProductDeal : regFinProductDealList) {
            regFinProductDeal.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(cpriceAmt,regFinProductDeal.getFaceValue()),avaNumTotal,2,RoundingMode.DOWN));
            regFinProductDeal.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(tradeAmt,regFinProductDeal.getFaceValue()),avaNumTotal,2,RoundingMode.DOWN));

            tailDifferenceCprice = ArithmeticUtil.subtract(tailDifferenceCprice,regFinProductDeal.getCpriceAmt());
            tailDifferenceShAmt=ArithmeticUtil.subtract(tailDifferenceShAmt,regFinProductDeal.getTradeAmt());
        }
        handleTailDifference(regFinProductDealList,tailDifferenceCprice,"01");
        handleTailDifference(regFinProductDealList,tailDifferenceShAmt,"02");
        regFinProductDealList.forEach(item->{item.setIntAmt(ArithmeticUtil.subtract(item.getTradeAmt(),item.getCpriceAmt()));});
        for(RegFinProductDeal regFinProductDeal : regFinProductDealList) {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,regFinProductDeal);
            trdProductDeal.setTradeDate(trdFinProductDealTotal.getTradeDate());
            trdProductDeal.setSettleDate(trdFinProductDealTotal.getSettleDate());
            trdProductDeal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            trdProductDeal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            trdProductDeal.setFinprodId(regFinProductDeal.getFinprodId());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            trdProductDeal.setBranch(0L);
          //  trdProductDeal.setVdate(DateUtil.parse(entity.getvDate()));
           // trdProductDeal.setMdate(DateUtil.parse(entity.getmDate()));
            trdProductDeal.setCcy(regFinProductDealTotal.getCcy());
            trdProductDeal.setShareAmt(ArithmeticUtil.multiply(regFinProductDeal.getShareAmt(),regFinProductDealTotal.getRedeemRate()));
            trdProductDeal.setIntAmt(regFinProductDeal.getIntAmt());
            trdProductDeal.setPrinAmt(regFinProductDeal.getCpriceAmt());
            trdProductDeal.setFpriceAmt(regFinProductDeal.getTradeAmt());
            trdProductDeal.setTradeAmt(regFinProductDeal.getTradeAmt());
            trdProductDeal.setPs(DictCodeList.交易方向_行权_赎回.getValue());
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
            trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
            trdProductDeal.setTotleTradeId(busiCode);
            trdProductDeal.setTradeMarket(regFinProductDealTotal.getTradeMarket());
            trdProductDeal.setSecManageAcctId(regFinProductDealTotal.getSecManageAcctId());
            trdProductDeal.setAccrNum(regFinProductDeal.getShareAmt());
            trdProductDealList.add(trdProductDeal);

            TrdProductDealAdd trdProductDealAdd=new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd,regFinProductDeal);
            trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
            trdProductDealAdd.setPortfolioId(regFinProductDeal.getPortfolioId());
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_批处理生成.getValue());
            //trdProductDealAdd.setWorkflowId(deal.getWorkflowId());
            trdProductDealAdd.setOurShareAmt(trdProductDeal.getShareAmt());
            trdProductDealAdd.setStatus(DictCodeList.数据状态_新建.getValue());
            trdProductDealaddList.add(trdProductDealAdd);
            //组装组合交易表
            PtlTrade ptlTrade2 = new PtlTrade();
            ptlTrade2.setPortfolioId(regFinProductDeal.getPortfolioId());
            ptlTrade2.setBusiNo(trdProductDeal.getTradeId());
            ptlTrade2.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade2.setInvAim(trdProductDeal.getInvAim());
            ptlTrade2.setBusiId(trdProductDeal.getFinprodId());
            ptlTrade2.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
            ptlTrade2.setSecManageAcctId(trdProductDeal.getSecManageAcctId());
            ptlTrade2.setSettleDate(trdProductDeal.getTradeDate());
            ptlTradeList.add(ptlTrade2);
            //组装划款指令表
            StlPayOrders stlPayOrders = new StlPayOrders();
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
            bussinessCodeParameter.setSeqNo("9998");
            String orderId;
            if(baseGetBussinessId != null){
                orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
            }else{
                orderId = IDGenerator.generate();
            }
            stlPayOrders.setOrderId(orderId);
            stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
//            String acctNo = autoPeriodCommonService.selectDealCapacctInfo(regFinProductDealTotal.getSecManageAcctId(), trdFinProductDealTotal.getCcy());
//            stlPayOrders.setMyAcctNo(acctNo);
            String myAcctNo = EtrCommonCashPositionDealService.select组合托管户(trdProductDealAdd.getPortfolioId(),
                    trdProductDeal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
            String counterAcctNo = null;
            stlPayOrders.setMyAcctNo(myAcctNo);
            stlPayOrders.setCounterAcctNo(counterAcctNo);
            stlPayOrders.setSettleDate(DateUtil.parse(DateUtil.format(batchDate)));
            stlPayOrders.setPs(DictCodeListExt1.收支方向_收入.getValue());
            stlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
            stlPayOrders.setCcy(regFinProductDealTotal.getCcy());
            stlPayOrders.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
            stlPayOrders.setIsNeedSend(DictCodeListExt1.是否_否.getValue());
            stlPayOrders.setIsTotalTrade(DictCodeListExt1.是否_是.getValue());
            stlPayOrders.setTradeId(trdProductDeal.getTradeId());
            stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
            stlPayOrders.setPortfolioId(trdProductDealAdd.getPortfolioId());
            stlPayOrdersList.add(stlPayOrders);
        }

        trdBondDurDbInertDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        trdBondDurDbInertDto.setTrdDealTotalAdd(trdDealTotalAdd);
        trdBondDurDbInertDto.setTrdProductDealList(trdProductDealList);
        trdBondDurDbInertDto.setTrdProductDealAdds(trdProductDealaddList);
        trdBondDurDbInertDto.setStlPayOrdersList(stlPayOrdersList);
        trdBondDurDbInertDto.setPtlTrades(ptlTradeList);
        trdBondDurationDto.setTrdBondDurDbInertDto(trdBondDurDbInertDto);
        return trdBondDurationDto;
    }
    private Map<String, Object> genMatchAndSendPosMethod (TrdBondDurDbInertDto trdBondDurDbInertDto,RegFinProductDealTotal regFinProductDealTotal) {
        TrdFinProductDealTotal trdTotle = trdBondDurDbInertDto.getTrdFinProductDealTotal();
        CfetsTrdConfirmMatchDto matchDto=new CfetsTrdConfirmMatchDto(); //匹配关系表
        matchDto.setSeqno(IDGenerator.generate());
        matchDto.setPreTradeId(regFinProductDealTotal.getTotleTradeId());
        matchDto.setTrdConfStatus(DictCodeList.确认状态_已确认.getValue());
        matchDto.setCreateUser("SYSTEM");
        matchDto.setCreateDept(CommonUtil.getDeptCode());
        matchDto.setUpdateUser("SYSTEM");
        matchDto.setTrdId(trdTotle.getTotleTradeId());
        //插入匹配关系表
        cfetsBondAutoDurDealGenerateMapper.insertTrdQutoMatchInfo(matchDto);
        //发送头寸
        List<CfetsEtrIbPosPortfolioRequest> ptlList = new ArrayList<>();
        for (TrdProductDeal dto : trdBondDurDbInertDto.getTrdProductDealList()) {
            //调用头寸
            CfetsEtrIbPosPortfolioRequest posport=new CfetsEtrIbPosPortfolioRequest();
            posport.setPortfolioId(dto.getPortfolioId());
            posport.setTradeId(dto.getTradeId());
            if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId = cfetsBondAutoDurDealGenerateMapper.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            posport.setSecAcctId(dto.getSecManageAcctId());
            posport.setSecAmount(ArithmeticUtil.multiply(dto.getShareAmt()==null?BigDecimal.ZERO:dto.getShareAmt(),new BigDecimal(100)));
            posport.setCashAmount(dto.getTradeAmt()==null?BigDecimal.ZERO:dto.getTradeAmt());
            trdBondDurDbInertDto.getTrdProductDealAdds().forEach(trdProductDealAdd -> {
                if (dto.getTradeId() != null && dto.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                    posport.setEtrId(trdProductDealAdd.getEntrustId());
                    posport.setIntegrateSecAmount(ArithmeticUtil.multiply(trdProductDealAdd.getLendinShareAmt()==null?BigDecimal.ZERO:trdProductDealAdd.getLendinShareAmt(),new BigDecimal(100)));
                    posport.setOwnSecAmount(ArithmeticUtil.multiply(trdProductDealAdd.getOurShareAmt()==null?BigDecimal.ZERO:trdProductDealAdd.getOurShareAmt(),new BigDecimal(100)));
                }
            });
            ptlList.add(posport);
        }
        //调用头寸
        CfetsEtrIbPositionRequest req=new CfetsEtrIbPositionRequest();
        req.setInterfaceType("Bond001");
        req.setUserId("SYSTEM");
        req.setTotalTradeId(trdTotle.getTotleTradeId());
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券行权交易.getValue()); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setSettleSpeed("01");
        req.setBalanceCheck("Y");
        req.setBusiAct(DictCodeListExt3.业务动作_待交收.getValue());
        req.setPs(trdTotle.getPs());
        req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(trdTotle.getTradeDate());
        req.setCcy(trdTotle.getCcy());
        req.setFinprodId(trdTotle.getFinprodId());
        req.setPtlList(ptlList);
       // req.setbTotalTradeId(regFinProductDealTotal.getTotleTradeId());
        return PositionHttpRequest.send(req);
    }

    /**
     * 构造将要登录的业务流程表数据
     *
     * @param entity 参数dto
     * @see
     * @since 1.0
     */
    private void genDataForInsertDto(TrdBondDurationDto entity) {
        autoPeriodBondCommonService.genBondDataForInsertDtoNew(entity);
        TrdBondDurDbInertDto trdBondDurDbInertDto = entity.getTrdBondDurDbInertDto();

        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        PaTrdFinProductDealTotal paTrdFinProductDealTotal =new PaTrdFinProductDealTotal();
        List<TrdProductDeal> trdProductDealList = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> trdProductDealAddList =new ArrayList<TrdProductDealAdd>();
        List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trdBondDurDbInertDto.getWflTrdFinProductDealTotal());
        BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trdBondDurDbInertDto.getWflPaTrdFinDealTotal());

        for(WflTrdProductDeal wflTrdProductDeal : trdBondDurDbInertDto.getWflTrdProductDealList()){
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,wflTrdProductDeal);
            trdProductDealList.add(trdProductDeal);
        }
        for(WflTrdProductDealAdd wflTrdProductDealAdd : trdBondDurDbInertDto.getWflTrdProductDealAdds()){
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd,wflTrdProductDealAdd);
            trdProductDealAddList.add(trdProductDealAdd);
        }
        for(WflStlPayOrders wflStlPayOrders : trdBondDurDbInertDto.getWflStlPayOrdersList()){
            StlPayOrders stlPayOrders = new StlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,wflStlPayOrders);
            stlPayOrdersList.add(stlPayOrders);
        }
        trdBondDurDbInertDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        trdBondDurDbInertDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        trdBondDurDbInertDto.setTrdProductDealList(trdProductDealList);
        trdBondDurDbInertDto.setTrdProductDealAdds(trdProductDealAddList);
        trdBondDurDbInertDto.setStlPayOrdersList(stlPayOrdersList);
        entity.setTrdBondDurDbInertDto(trdBondDurDbInertDto);
    }

    /**
     * 生成划款指令
     *
     * @param entity 交易数据
     * @return 划款指令
     * @see
     * @since 1.0
     */
    private List<WflStlPayOrders> gen划款指令流程(TrdBondDurationDto entity) {
        // 自动生成划款指令用deal
        TrdProductDeal trdProductDeal = AutoPeriodBondCommonService.getTrdProductDeal(entity);
        BatchBussSettleOrderParameterDto parameterDto = new BatchBussSettleOrderParameterDto();
        parameterDto.setIsTotalTrade(DictCodeList.是否_是.getValue());
        parameterDto.setBankLendingType(null);
        parameterDto.setPortfolioId(entity.getPortfolioId());
        parameterDto.setDeal(trdProductDeal);
        return pinganBatchCommonService.generateSettleOrder(parameterDto);
    }

    private void setParams(Map<String, Object> param) {
        param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("PAGE_DATE_STATUS01", DictCodeListExt1.画面数据状态_待处理.getValue());
        param.put("PAGE_DATE_STATUS03", DictCodeListExt1.画面数据状态_流程中.getValue());
        param.put("PAGE_DATE_STATUS05", DictCodeListExt1.画面数据状态_有效.getValue());
        param.put("PAGE_DATE_STATUS06", DictCodeListExt1.画面数据状态_撤销.getValue());
        param.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
        param.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
        param.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
        param.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
        // 数据字典缓存.交易状态.初始
        param.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YESNO_N", DictCodeList.是否_否.getValue());
        param.put("YESNO_Y", DictCodeList.是否_是.getValue());
        // 数据字典缓存.持仓类型.实际持仓
        param.put("POSITION_TYPE03", DictCodeList.持仓类型_实际.getValue());
        // 数据字典缓存.金融产品类型 投管分类.债券
        param.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
        param.put("STATUS_N", DictCodeList.数据状态_新建.getValue());
        //String plateDate = platformDateImpl.getSysDateStr();
        //param.put("PLATEFORM_DATE", DateUtil.parse(plateDate));
    }
    /**
     *  尾差处理(券面)
     * @param
     * @param tailDifference
     */
    public void handleTailDifference(List<RegFinProductDeal>  regFinProductDealList,BigDecimal tailDifference,String type){
        //递增维度
        BigDecimal addDimension = faceDimension;
        int size = regFinProductDealList.size();
        //委托维度 已经完全分配的集合
        while(tailDifference.compareTo(BigDecimal.ZERO)>0&&size>0){
            if(tailDifference.compareTo(new BigDecimal("1"))>=0){
                addDimension = ArithmeticUtil.divide(tailDifference,new BigDecimal(size),2,RoundingMode.DOWN);
            }
            for (RegFinProductDeal item : regFinProductDealList) {
                if(tailDifference.compareTo(BigDecimal.ZERO)>0){
                    if(StringUtil.equals(type,"01")){
                        item.setCpriceAmt(ArithmeticUtil.doAdd(item.getCpriceAmt(),addDimension));
                    }
                    if(StringUtil.equals(type,"02")){
                        item.setTradeAmt(ArithmeticUtil.doAdd(item.getTradeAmt(),addDimension));
                    }
                    tailDifference = ArithmeticUtil.subtract(tailDifference,addDimension);
                }
            }
        }
    }
}
