/*
 * 文件名：ResetBaseRuleInfoReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.cfetssettle.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.PositionRulePtlData;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.batch.cfetssettle.dao.CfetsSettleMapper;
import com.joyintech.tams.invest.batch.cfetssettle.dto.*;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

/**
 * reader相关业务处理
 * 〈功能详细描述〉
 *
 * @author sjj
 * @version 1.0 2021年11月19日
 * @see CfetsSettleService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class CfetsSettleService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(CfetsSettleService.class);

    @Autowired(required = false)
    private CfetsSettleMapper cfetsSettleMapper;

    @Autowired(required = false)
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired(required = false)
    private TrdBankManualSettleFlowMapper trdBankManualSettleFlowMapper;

    @Autowired(required = false)
    private TrdBankManualSetDetailMapper trdBankManualSetDetailMapper;

    @Autowired(required = false)
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired(required = false)
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired(required = false)
    private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;

    @Autowired(required = false)
    private TrdProductTempDealMapper trdProductTempDealMapper;

    @Autowired(required = false)
    private StlPayTempOrdersMapper stlPayTempOrdersMapper;

    @Autowired(required = false)
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired(required = false)
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired(required = false)
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    /**
     * 业务编号取得共通
     */
    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessIdimpl;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    private JoyinRestTemplate restTemplate;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    @Value("${application.micorservice.contextpath.tamsServer:http://tams-server}")
    private String tamsSerever;


    /**
     * 银行间手工交收流水信息Entity
     *
     * @param cfetsSettleAllInfoDto
     * @param executeDate           跑批日
     * @return
     */
    private TrdBankManualSettleFlow createTrdBankManualSettleFlow(CfetsSettleAllInfoDto cfetsSettleAllInfoDto, TrdBankManualSettleInfo trdBankManualSettleInfo, Date executeDate) {
        TrdBankManualSettleFlow trdBankManualSettleFlow = new TrdBankManualSettleFlow();
        //交收流水编号=调用【共通】生成
        trdBankManualSettleFlow.setSeqNo(IDGenerator.generate());
        //交收编号=银行间手工交收信息Entiy.交收编号
        trdBankManualSettleFlow.setSettleSeqNo(trdBankManualSettleInfo.getSettleSeqNo());
        //交收时间=当前时间
        trdBankManualSettleFlow.setSettleTime(new Date());
        //交收数量=画面.交收数量
        //交收数量
        if (StringUtil.equals(cfetsSettleAllInfoDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券存续期交易.getValue())
                && (StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_收息.getValue()) || StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_还本.getValue()))) {
        } else {
            trdBankManualSettleFlow.setSettleNum(cfetsSettleAllInfoDto.getSettleNumT());
        }
        //交收金额=画面.交收金额
        if (StringUtil.equals(cfetsSettleAllInfoDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_现券交易.getValue()) && StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
        } else {
            trdBankManualSettleFlow.setSettleAmt(cfetsSettleAllInfoDto.getSettleAmtT());
        }
        //金融产品代码=画面.资产代码
        trdBankManualSettleFlow.setFinprodId(cfetsSettleAllInfoDto.getFinprodId());
        //金融产品代码=画面.资产简称
        trdBankManualSettleFlow.setFinprodAbbr(cfetsSettleAllInfoDto.getFinprodAbbr());
        //操作类型=交收
        trdBankManualSettleFlow.setOperType(DictCodeListExt3.交收操作类型_交收.getValue());
        trdBankManualSettleFlow.setCreateUser("SYSTEM");
        trdBankManualSettleFlow.setUpdateUser("SYSTEM");
        return trdBankManualSettleFlow;
    }

    /**
     * 构建银行间手工交收信息Entity
     *
     * @param cfetsSettleAllInfoDto * @param executeDate 跑批日
     * @return
     */
    private TrdBankManualSettleInfo createTrdBankManualSettleInfo(CfetsSettleAllInfoDto cfetsSettleAllInfoDto, Date executeDate) {
        TrdBankManualSettleInfo trdBankManualSettleInfo = new TrdBankManualSettleInfo();

        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeListExt3.共通业务类型_手工交收);
        String settleSeqNo = autoPeriodCommonService.buildBussinessCode(parameter);
        //交收编号
        trdBankManualSettleInfo.setSettleSeqNo(cfetsSettleAllInfoDto.getSettleSeqNo() == null ? settleSeqNo : cfetsSettleAllInfoDto.getSettleSeqNo());
        //原业务编号
        trdBankManualSettleInfo.setTradeId(cfetsSettleAllInfoDto.getTotalTradeId());
        //金融产品代码
        trdBankManualSettleInfo.setFinprodId(cfetsSettleAllInfoDto.getFinprodId());
        //交收时间=平台日
        trdBankManualSettleInfo.setSettleDate(executeDate);
        //交收数量
        if (StringUtil.equals(cfetsSettleAllInfoDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券存续期交易.getValue())
                && (StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_收息.getValue()) || StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_还本.getValue()))) {
        } else {
            trdBankManualSettleInfo.setSettleNum(cfetsSettleAllInfoDto.getSettleNumT());
        }
        //交收金额
        if (StringUtil.equals(cfetsSettleAllInfoDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_现券交易.getValue()) && StringUtil.equals(cfetsSettleAllInfoDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
        } else {
            trdBankManualSettleInfo.setSettleAmt(cfetsSettleAllInfoDto.getSettleAmtT());
        }
        //业务类别
        trdBankManualSettleInfo.setBusiType(cfetsSettleAllInfoDto.getTgBusiType());
        //交收状态
        trdBankManualSettleInfo.setSettleStatus(DictCodeListExt3.交收状态_已交收.getValue());

        trdBankManualSettleInfo.setFinprodType2(cfetsSettleAllInfoDto.getFinprodType2());
        trdBankManualSettleInfo.setCounterId(cfetsSettleAllInfoDto.getCounterId());

        trdBankManualSettleInfo.setCreateUser("SYSTEM");
        trdBankManualSettleInfo.setUpdateUser("SYSTEM");
        return trdBankManualSettleInfo;
    }

    /**
     * 交收自动生成
     *
     * @param batchDate     执行日
     * @param productIdList
     * @see
     * @since 1.0
     */
    public void autoSettle(Date batchDate, List<String> productIdList) {
//        if (!StringUtil.equals(CacheUtil.getSysParam("auto_settle_flag"), "Y")) {
//            logger.info("日终自动交收系统参数未开启");
//            return;
//        }
//        //删除当日手工交收数据
//        //删除交收流水表
//        cfetsSettleMapper.deleteTodayCfetsSettleFlow(paramMap);
//        //删除交收明细表
//        cfetsSettleMapper.deleteTodayCfetsSettleDetail(paramMap);
//        //删除交收信息表
//        cfetsSettleMapper.deleteTodayCfetsSettleInfo(paramMap);

        //生成当日手工交收数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("batchDate", batchDate);
        paramMap.put("createrUser", "SYSTEM");
        List<CfetsSettleReaderResult> insertDtoLists = this.createTodayTodoSettleData(paramMap);
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            //插入银行间手工交收相关表
            for (CfetsSettleReaderResult cfetsManualSettleInsertDto : insertDtoLists) {
                //存在交收编号，说明是做过交收后又回溯，则做更新动作
                cfetsSettleMapper.mergeInfo(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo());
                //新增交收流水
                trdBankManualSettleFlowMapper.insertSelective(cfetsManualSettleInsertDto.getTrdBankManualSettleFlow());
                //新增交收明细
                for (TrdBankManualSetDetail trdBankManualSetDetail : cfetsManualSettleInsertDto.getListTrdBnakSetDetails()) {
                    trdBankManualSetDetailMapper.insertSelective(trdBankManualSetDetail);
                }
                //处理交易数据从temp表转移到交易表
                TrdFinProductDealTotal productDealTotalTemp = trdProductTempDealTotalMapper.selectByPrimaryKey(cfetsManualSettleInsertDto.getTotalTradeId());
                if (Objects.nonNull(productDealTotalTemp)) {
                    TrdFinProductDealTotal productDealTotal = trdFinProductDealTotalMapper.selectByPrimaryKey(cfetsManualSettleInsertDto.getTotalTradeId());
                    if (Objects.isNull(productDealTotal)) {//没有回溯过的记录直接copy
                        autoPeriodCommonService.insertDealDataFromTemp(cfetsManualSettleInsertDto.getTotalTradeId());
                    } else {//回溯之后的记录直接更新撤销状态
                        autoPeriodCommonService.updateDealDataCancel(cfetsManualSettleInsertDto.getTotalTradeId(), DictCodeList.是否_否.getValue());
                    }
                    // 划款指令从temp转到实际表
                    List<TrdProductTempDeal> list = trdProductTempDealMapper.selectByTotalTradeId(cfetsManualSettleInsertDto.getTotalTradeId());
                    if (CollectionUtils.isNotEmpty(list)) {
                        List<String> tradeIds = list.stream().map(TrdProductTempDeal::getTradeId).collect(Collectors.toList());
                        stlPayTempOrdersMapper.insertStlPayOrders(tradeIds);
                    }
                }
                //更新风险占用
                updateOccupy(cfetsManualSettleInsertDto);
                logger.info("日终自动交收数据:{}", JsonUtils.object2Json(cfetsManualSettleInsertDto));
                //对债券数据发送头寸
                if (StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())) {
                    mapList.add(this.sendPositionMethod(cfetsManualSettleInsertDto, batchDate));
                    this.insertOldPosition(cfetsManualSettleInsertDto, DictCodeListExt3.交收操作类型_交收.getValue(), false);
                }
                //对回购数据发送头寸
                if (StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                        || StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                    mapList.add(this.sendRepoPositionMethod(cfetsManualSettleInsertDto, batchDate));
                    this.insertOldRepoPosition(cfetsManualSettleInsertDto, DictCodeListExt3.交收操作类型_交收.getValue(), cfetsManualSettleInsertDto.getFinprodType2(),
                            false, cfetsManualSettleInsertDto.getTgBusiType(), false);
                }
                if (StringUtil.equals(cfetsManualSettleInsertDto.getBusiType(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
                    // 一级申购发送头寸报文
                    mapList.add(this.sendBidPositionMethod(cfetsManualSettleInsertDto, batchDate));
                    this.insertOldBidPosition(cfetsManualSettleInsertDto, DictCodeListExt3.交收操作类型_交收.getValue(), false);
                }
                if (StringUtil.equals(cfetsManualSettleInsertDto.getBusiType(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                    // 一级投标发送头寸报文
                    mapList.add(this.sendBidFirmPositionMethod(cfetsManualSettleInsertDto, batchDate));
                    this.insertOldBidPosition(cfetsManualSettleInsertDto, DictCodeListExt3.交收操作类型_交收.getValue(), false);
                }
            }
        } catch (PositionException e) {
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

    private Map<String, Object> sendPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
        List<PositionRulePtlData> ptlList = new ArrayList<>();
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        for (CfetsSettleDealInfo dto : listTemp) {
            PositionRulePtlData positionRulePtlData = new PositionRulePtlData();
            positionRulePtlData.setPortfolioId(dto.getPortfolioId());
            positionRulePtlData.setTradeId(dto.getTradeId());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                map.put("ccy", dto.getCcy());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                }
            } else {
                logger.error("汇总编号{}不存在组合", dto.getTotleTradeId());
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
            } else {
                logger.error("汇总编号" + dto.getTotleTradeId() + "不存在组合" + dto.getPortfolioId() + "或资产" + dto.getFinprodId());
            }
            positionRulePtlData.setSecAmount(dto.getShareAmt());
            positionRulePtlData.setCashAmount(dto.getTradeAmt());
            positionRulePtlData.setInvAim(dto.getInvAim());
            positionRulePtlData.setOwnSecAmount(dto.getOurShareAmt() == null ? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getOurShareAmt(), new BigDecimal(100)));
            positionRulePtlData.setIntegrateSecAmount(dto.getLendinShareAmt() == null ? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getLendinShareAmt(), new BigDecimal(100)));
            ptlList.add(positionRulePtlData);
        }

        CfetsSettlePositionDto req = new CfetsSettlePositionDto();
        req.setInterfaceType("Bond001");
        //指令或交易标识	ETR:指令,TRD:交易
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        //用户名	当前操作用户
        req.setUserId("SYSTEM");
        //汇总交易编号	单交易时传交易编号
        req.setbTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        //汇总交易编号	单交易时传交易编号
        req.setTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getSettleSeqNo());
        //流程状态	流程节点 ，提交 审批通过
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        //余额校验	Y：需校验   N：不需校验
        String balanceCheck = "N";
        req.setBalanceCheck(balanceCheck);
        // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        req.setBusiType(DictCodeListExt3.头寸业务类型_现券交易.getValue());
        if (StringUtil.isNotEmpty(cfetsManualSettleInsertDto.getPs())) {
            if (StringUtil.equals(cfetsManualSettleInsertDto.getPs(), BussinessDictCodeList.交易方向_行权赎回.getValue())
                    || StringUtil.equals(cfetsManualSettleInsertDto.getPs(), BussinessDictCodeList.交易方向_行权回售.getValue())) {
                req.setBusiType(DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
            } else if (StringUtil.equals(cfetsManualSettleInsertDto.getPs(), BussinessDictCodeList.交易方向_到期.getValue())
                    || StringUtil.equals(cfetsManualSettleInsertDto.getPs(), BussinessDictCodeList.交易方向_还本.getValue())
                    || StringUtil.equals(cfetsManualSettleInsertDto.getPs(), BussinessDictCodeList.交易方向_收息.getValue())) {
                req.setBusiType(DictCodeListExt3.头寸业务类型_债券存续期交易.getValue());
            }
        }
        //交易场所
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        //清算速度
        if (!StringUtil.isEmptyList(listTemp) && StringUtil.isNotEmpty(listTemp.get(0).getSettleSpeed())) {
            req.setSettleSpeed(listTemp.get(0).getSettleSpeed().toString());
            if (listTemp.get(0).getSettleSpeed().compareTo(new Long(0)) == 0) {
                req.setSettleSpeed("01");
            } else if (listTemp.get(0).getSettleSpeed().compareTo(new Long(1)) == 0) {
                req.setSettleSpeed("02");
            } else if (listTemp.get(0).getSettleSpeed().compareTo(new Long(2)) == 0) {
                req.setSettleSpeed("03");
            }
        } else {
            req.setSettleSpeed("01");
        }
        //业务动作BUSI_ACT 09:交收确认 10:交收失败
        req.setBusiAct("09");
        //交易方向
        req.setPs(cfetsManualSettleInsertDto.getPs());
        //新建 修改 关闭
        req.setSystemAct("01");
        //决策树
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(batchDate);
        req.setCcy(cfetsManualSettleInsertDto.getCcy());
        req.setFinprodId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getFinprodId());
        req.setPtlList(ptlList);
        logger.info("现券发送到头寸的报文:{}", JsonUtils.object2Json(req));
        return PositionHttpRequest.send(req);
    }

    private Map<String, Object>  sendRepoPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
        //组装组合金额信息
        List<CfetsEtrIbRepoPosPortfolioCashRequest> ptlCashList = new ArrayList<>();
        List<CfetsEtrIbRepoPosPortfolioSecRequest> ptlSecList = new ArrayList<>();
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        for (CfetsSettleDealInfo dto : listTemp) {
            CfetsEtrIbRepoPosPortfolioCashRequest positionRulePtlData = new CfetsEtrIbRepoPosPortfolioCashRequest();
            positionRulePtlData.setPortfolioId(dto.getPortfolioId());
            positionRulePtlData.setTradeId(dto.getTradeId());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                map.put("ccy", dto.getCcy());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                }
            }
            positionRulePtlData.setCashAmount(dto.getTradeAmt());
            ptlCashList.add(positionRulePtlData);
            List<PledgeBondInfoDto> pledgeBondList = cfetsSettleMapper.getPledgeListByTradeId(dto.getFinprodId());
            if (StringUtil.isNotEmpty(pledgeBondList)) {
                for (PledgeBondInfoDto regFinPledge : pledgeBondList) {
                    CfetsEtrIbRepoPosPortfolioSecRequest secport = new CfetsEtrIbRepoPosPortfolioSecRequest();
                    secport.setPortfolioId(dto.getPortfolioId());
                    secport.setEtrId(dto.getEntrustId());
                    secport.setTradeId(dto.getTradeId());
                    secport.setFinprodId(regFinPledge.getFinprodId());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", regFinPledge.getFinprodId());
                    String secAcctId = this.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        secport.setSecAcctId(secAcctId); // 证券账户
                    }
                    secport.setSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgePrin(), new BigDecimal("100")));
                    secport.setOwnSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgePrin(), new BigDecimal("100")));
                    secport.setIntegrateSecAmount(BigDecimal.ZERO);
                    ptlSecList.add(secport);
                }
            }

        }
        CfetsEtrIbRepoPositionRequest req = new CfetsEtrIbRepoPositionRequest();
        req.setInterfaceType("REPO001");
        //用户名	当前操作用户
        req.setUserId("SYSTEM");
        //指令或交易标识	ETR:指令,TRD:交易
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        //汇总交易编号	单交易时传交易编号
        req.setbTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        //汇总交易编号	单交易时传交易编号
        req.setTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getSettleSeqNo());
        //流程状态	流程节点 ，提交 审批通过
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        //余额校验	Y：需校验   N：不需校验
        String balanceCheck = "N";
        req.setBalanceCheck(balanceCheck);
        req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
        // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
        if (StringUtil.isNotEmpty(cfetsManualSettleInsertDto.getBusiType())) {
            req.setBusiType(cfetsManualSettleInsertDto.getBusiType());
        } else {
            req.setBusiType(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());
        }
        //交易场所
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        //清算速度
        if (!StringUtil.isEmptyList(listTemp) && StringUtil.isNotEmpty(listTemp.get(0).getSettleSpeed())) {
            req.setSettleSpeed(listTemp.get(0).getSettleSpeed().toString());
            if (listTemp.get(0).getSettleSpeed().compareTo(new Long(0)) == 0) {
                req.setSettleSpeed("01");
            } else if (listTemp.get(0).getSettleSpeed().compareTo(new Long(1)) == 0) {
                req.setSettleSpeed("02");
            } else if (listTemp.get(0).getSettleSpeed().compareTo(new Long(2)) == 0) {
                req.setSettleSpeed("03");
            }
        } else {
            req.setSettleSpeed("01");
        }
        //业务动作BUSI_ACT 09:交收确认 10:交收失败
        req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
        //交易方向
        String finprodType2 = listTemp.get(0).getFinprodType2();
        if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
            req.setPs(DictCodeList.回购拆借方向_正回购.getValue());
        } else if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
            req.setPs(DictCodeList.回购拆借方向_逆回购.getValue());
        }
        //新建 修改 关闭
        req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        //决策树
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(batchDate);
        req.setCcy(cfetsManualSettleInsertDto.getCcy());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        logger.info("回购发送到头寸的报文:{}", JsonUtils.object2Json(req));
        return PositionHttpRequest.send(req);
    }

    private List<CfetsSettleReaderResult> createTodayTodoSettleData(Map<String, Object> pageParam) {
        List<CfetsSettleReaderResult> listTemp = new ArrayList<>();
        Date batchDate = (Date) pageParam.get("batchDate");
        //平安业务类型
        pageParam.put("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_35", DictCodeListExt1.平安业务类型_债券中间收息.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_36", DictCodeListExt1.平安业务类型_债券中间还本.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_37", DictCodeListExt1.平安业务类型_债券到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_51", DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_52", DictCodeListExt1.平安业务类型_正回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_76", DictCodeListExt1.平安业务类型_买断式正回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_77", DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_105", DictCodeListExt2.平安业务类型_债券借贷融入.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_106", DictCodeListExt2.平安业务类型_债券借贷融出.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_107", DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_108", DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_180", DictCodeListExt4.平安业务类型_一级申购.getValue());
        pageParam.put("PINAN_BUSINESS_TYPE_151", DictCodeListExt4.平安业务类型_中标结果维护.getValue());
        pageParam.put("tgBusiTypes", Arrays.asList(DictCodeListExt1.平安业务类型_债券类资产买入.getValue(), DictCodeListExt1.平安业务类型_债券类资产卖出.getValue()
                , DictCodeListExt1.平安业务类型_债券类资产行权.getValue(), DictCodeListExt1.平安业务类型_债券中间收息.getValue(), DictCodeListExt1.平安业务类型_债券中间还本.getValue()
                , DictCodeListExt1.平安业务类型_债券到期.getValue(), DictCodeListExt1.平安业务类型_正回购.getValue(), DictCodeListExt1.平安业务类型_逆回购到期.getValue()
                , DictCodeListExt1.平安业务类型_正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式正回购.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购.getValue()
                , DictCodeListExt1.平安业务类型_买断式正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入.getValue()
                , DictCodeListExt2.平安业务类型_债券借贷融出.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue()
                , DictCodeListExt4.平安业务类型_一级申购.getValue(), DictCodeListExt4.平安业务类型_中标结果维护.getValue(), DictCodeListExt4.平安业务类型_债券分销买入确认.getValue()));
        //收付方向
        pageParam.put("feePs02", DictCodeListExt1.收付方向_支出.getValue());
        //结算状态
        pageParam.put("settlementStatus01", DictCodeList.结算状态_已结算.getValue());
        pageParam.put("settlementStatus02", DictCodeList.结算状态_已结算.getValue());
        //指令处理状态
        pageParam.put("commandDealStatus01", DictCodeList.指令处理状态_处理成功.getValue());
        pageParam.put("commandDealStatus02", DictCodeList.指令处理状态_处理失败.getValue());
        //指令处理状态
        pageParam.put("cleanStatus01", DictCodeListExt3.清算状态_清算成功.getValue());
        pageParam.put("cleanStatus02", DictCodeListExt3.清算状态_清算失败.getValue());
        pageParam.put("cleanStatus03", DictCodeListExt3.清算状态_清算中.getValue());
        //交收状态
        pageParam.put("DELIVERY_STATUS_01", DictCodeListExt3.交收状态_未交收.getValue());
        pageParam.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        pageParam.put("isCalcN", BussinessDictCodeList.是否_否.getValue());
        pageParam.put("batchDate", batchDate);
        pageParam.put("pageId", "CfetsManualSettle");
        pageParam.put("pageId01", "CfetsManualSettleUpdate");
        pageParam.put("tradePlat_01", BussinessDictCodeList.交易场所_银行间.getValue());
        pageParam.put("tradePlat_02", BussinessDictCodeList.交易场所_商业银行柜台市场.getValue());
        //交易状态
        pageParam.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        pageParam.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        pageParam.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        pageParam.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        pageParam.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());

        pageParam.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("notAssetType", Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue()));

        //查询待交收数据List
        List<CfetsSettleAllInfoDto> cfetsSettleDtoList = cfetsSettleMapper.selectAllSettleToDoList(pageParam);
        if (!StringUtil.isEmptyList(cfetsSettleDtoList)) {
            for (CfetsSettleAllInfoDto cfetsSettleAllInfoDto : cfetsSettleDtoList) {
                CfetsSettleReaderResult cfetsSettleReaderResult = new CfetsSettleReaderResult();
                //银行间手工交收信息Entity
                TrdBankManualSettleInfo trdBankManualSettleInfo = this.createTrdBankManualSettleInfo(cfetsSettleAllInfoDto, batchDate);
                //银行间手工交收流水信息Entity
                TrdBankManualSettleFlow trdBankManualSettleFlow = this.createTrdBankManualSettleFlow(cfetsSettleAllInfoDto, trdBankManualSettleInfo, batchDate);
                //银行间手工交收明细信息Entity
                List<TrdBankManualSetDetail> listTrdBnakSetDetails = new ArrayList<>();
                for (CfetsSettleDetailInfoDto CfetsSettleDetailInfoDto : cfetsSettleAllInfoDto.getCfetsSettleDetailInfoDtoList()) {
                    TrdBankManualSetDetail trdBankManualSetDetail = new TrdBankManualSetDetail();
                    //交收流水序号=调用【共通】生成
                    trdBankManualSetDetail.setSeqNo(IDGenerator.generate());
                    //交收编号=银行间手工交收信息Entiy.交收编号
                    trdBankManualSetDetail.setSettleSeqNo(trdBankManualSettleInfo.getSettleSeqNo());
                    //交收日期=平台日
                    trdBankManualSetDetail.setSettleDate(batchDate);
                    //原业务编号=成交确认明细.交易编号
                    trdBankManualSetDetail.setTradeId(CfetsSettleDetailInfoDto.getTradeId());
                    //金融产品代码=画面.资产代码
                    trdBankManualSetDetail.setFinprodId(cfetsSettleAllInfoDto.getFinprodId());
                    //金融产品简称=画面.资产简称
                    trdBankManualSetDetail.setFinprodAbbr(cfetsSettleAllInfoDto.getFinprodAbbr());
                    //组合代码=成交确认明细.投资组合代码
                    trdBankManualSetDetail.setPortfolioId(CfetsSettleDetailInfoDto.getPortfolioId());
                    //交割数量=成交确认明细.交易份额
                    trdBankManualSetDetail.setSettleNum(CfetsSettleDetailInfoDto.getSettleNum());
                    //交割金额=成交确认明细.交易金额
                    trdBankManualSetDetail.setSettleAmt(CfetsSettleDetailInfoDto.getSettleAmt());
                    //业务类别=画面.投管业务类型
                    trdBankManualSetDetail.setBusiType(cfetsSettleAllInfoDto.getTgBusiType());
                    //交收状态=数据字典.交收状态.已交收
                    trdBankManualSetDetail.setSettleStatus(DictCodeListExt3.交收状态_已交收.getValue());
                    trdBankManualSetDetail.setCreateUser("SYSTEM");
                    trdBankManualSetDetail.setUpdateUser("SYSTEM");
                    listTrdBnakSetDetails.add(trdBankManualSetDetail);
                }
                cfetsSettleReaderResult.setTrdBankManualSettleInfo(trdBankManualSettleInfo);
                cfetsSettleReaderResult.setTrdBankManualSettleFlow(trdBankManualSettleFlow);
                cfetsSettleReaderResult.setListTrdBnakSetDetails(listTrdBnakSetDetails);
                cfetsSettleReaderResult.setTgBusiType(cfetsSettleAllInfoDto.getTgBusiType());
                cfetsSettleReaderResult.setPs(cfetsSettleAllInfoDto.getPs());
                cfetsSettleReaderResult.setCcy(cfetsSettleAllInfoDto.getCcy());
                cfetsSettleReaderResult.setFinprodType2(cfetsSettleAllInfoDto.getFinprodType2());
                cfetsSettleReaderResult.setBusiType(cfetsSettleAllInfoDto.getBusiType());
                cfetsSettleReaderResult.setTotalTradeId(cfetsSettleAllInfoDto.getTotalTradeId());
                cfetsSettleReaderResult.setFinprodId(cfetsSettleAllInfoDto.getFinprodId());
                cfetsSettleReaderResult.setCounterId(cfetsSettleAllInfoDto.getCounterId());
                cfetsSettleReaderResult.setSettleAmt(cfetsSettleAllInfoDto.getSettleAmtT());
                listTemp.add(cfetsSettleReaderResult);
            }
        }
        return listTemp;
    }

    /**
     * 查询证券账户
     *
     * @param param
     * @return
     */
    public String qryPortfolioSecAcctId(Map<String, Object> param) {
        return cfetsSettleMapper.qryPortfolioSecAcctId(param);
    }

    /**
     * 查询托管户
     *
     * @param param
     * @return
     */
    public String qryPortfolioCashAcctId(Map<String, Object> param) {
        return cfetsSettleMapper.qryPortfolioCashAcctId(param);
    }

    /**
     * 更新占用
     *
     * @param cfetsManualSettleInsertDto
     */
    private void updateOccupy(CfetsSettleReaderResult cfetsManualSettleInsertDto) {
        try {
            cfetsManualSettleInsertDto.setButtonType(交收操作类型_交收.getValue());
            cfetsManualSettleInsertDto.setTradeId(cfetsManualSettleInsertDto.getTotalTradeId());
            Message<AjaxResult> message;
            String url = String.format("/cfets/riskTrial/doOccupy/%s/%s/%s", "CfetsManualSettle", 系统动作_新建.getValue(), 流程节点_审批生效.getValue());
            if (isSingle) {
                message = simpleRestTemplate.postForObject(tamsSerever + url, cfetsManualSettleInsertDto, Message.class);
            } else {
                message = restTemplate.postForObject(tamsSerever + url, cfetsManualSettleInsertDto, Message.class);
            }
            logger.info("风险占用更新结果：{}", JsonUtils.object2Json(message));
            if (Objects.nonNull(message) && !"000".equals(message.getCode())) {
                logger.error("风险占用更新异常：totalTradeId {}", cfetsManualSettleInsertDto.getTotalTradeId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建一级申购发送头寸信息
     *
     * @param cfetsManualSettleInsertDto
     * @return
     */
    private Map<String, Object>  sendBidPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
        //组装组合金额信息
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        for (CfetsSettleDealInfo dto : listTemp) {
            CfetsBondPortfolioCashRequest positionRulePtlData = new CfetsBondPortfolioCashRequest();
            positionRulePtlData.setPortfolioId(dto.getPortfolioId());
            positionRulePtlData.setTradeId(dto.getTradeId());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                map.put("ccy", dto.getCcy());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                }
            } else {
                logger.error("汇总编号" + dto.getTotleTradeId() + "不存在组合");
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
            } else {
                logger.error("汇总编号" + dto.getTotleTradeId() + "不存在组合" + dto.getPortfolioId() + "或资产" + dto.getFinprodId());
            }
            positionRulePtlData.setSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(), new BigDecimal(100)));
            positionRulePtlData.setCashAmount(dto.getTradeAmt());
            positionRulePtlData.setOwnSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(), new BigDecimal(100)).toPlainString());
            positionRulePtlData.setInvAim(dto.getInvAim());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            ptlList.add(positionRulePtlData);
        }

        CfetsBondPositionRequest req = new CfetsBondPositionRequest();
        req.setInterfaceType("Bond001");
        //指令或交易标识	ETR:指令,TRD:交易
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        //用户名	当前操作用户
        req.setUserId("SYSTEM");
        //汇总交易编号	单交易时传交易编号
        req.setbTotalTradeId(cfetsManualSettleInsertDto.getTradeId());
        //汇总交易编号
        req.setTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getSettleSeqNo());
        req.setWorkFlowId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getWorkflowId());
        //流程状态	流程节点 ，提交 审批通过
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        //余额校验	Y：需校验   N：不需校验
        String balanceCheck = "N";
        req.setBalanceCheck(balanceCheck);
        //  必填,参数字典 BUSI_TYPE_POSITION
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级申购.getValue());
        //交易场所
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        //业务动作 BUSI_ACT 09:交收确认 10:交收失败
        req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
        //交易方向
        req.setPs(cfetsManualSettleInsertDto.getPs());
        // 系统动作 新建 修改 关闭
        req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(batchDate);
        req.setCcy(cfetsManualSettleInsertDto.getCcy());
        req.setFinprodId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getFinprodId());
        req.setPtlList(ptlList);
        logger.info("一级申购发送的报文:{}", JsonUtils.object2Json(req));
        return PositionHttpRequest.send(req);
    }

    /**
     * 构建一级投标确认发送头寸信息
     *
     * @param cfetsManualSettleInsertDto
     * @return
     */
    private Map<String, Object>  sendBidFirmPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
        //组装组合金额信息
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
        String tgBusiType = null;
        for (CfetsSettleDealInfo dto : listTemp) {
            CfetsBondPortfolioCashRequest positionRulePtlData = new CfetsBondPortfolioCashRequest();
            positionRulePtlData.setPortfolioId(dto.getPortfolioId());
            positionRulePtlData.setTradeId(dto.getTradeId());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                map.put("ccy", dto.getCcy());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    positionRulePtlData.setCashAcctId(cashAcctId); // 托管户
                }
            } else {
                logger.error("汇总编号" + dto.getTotleTradeId() + "不存在组合");
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
            } else {
                logger.error("汇总编号" + dto.getTotleTradeId() + "不存在组合" + dto.getPortfolioId() + "或资产" + dto.getFinprodId());
            }
            positionRulePtlData.setSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(), new BigDecimal(100)));
            positionRulePtlData.setCashAmount(dto.getTradeAmt());
            positionRulePtlData.setOwnSecAmount(ArithmeticUtil.multiply(dto.getShareAmt(), new BigDecimal(100)).toPlainString());
            positionRulePtlData.setInvAim(dto.getInvAim());
            positionRulePtlData.setEtrId(dto.getEntrustId());
            ptlList.add(positionRulePtlData);
            tgBusiType = dto.getTgBusiType();
        }

        CfetsBondPositionRequest req = new CfetsBondPositionRequest();
        req.setInterfaceType("Bond001");
        //指令或交易标识	ETR:指令,TRD:交易
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        //用户名	当前操作用户
        req.setUserId("SYSTEM");
        //汇总交易编号	单交易时传交易编号
        req.setbTotalTradeId(cfetsManualSettleInsertDto.getTradeId());
        //汇总交易编号
        req.setTotalTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getSettleSeqNo());
        req.setWorkFlowId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getWorkflowId());
        //流程状态	流程节点 ，提交 审批通过
        req.setFlowStatus(DictCodeListExt3.流程节点_审批生效.getValue());
        //余额校验	Y：需校验   N：不需校验
        String balanceCheck = "N";
        req.setBalanceCheck(balanceCheck);
        //  必填,参数字典 BUSI_TYPE_POSITION
        req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
        //交易场所
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        //业务动作 BUSI_ACT 09:交收确认 10:交收失败
        req.setBusiAct(DictCodeListExt3.业务动作_交收确认.getValue());
        req.setPurchModle(DictCodeListExt4.头寸申购方式_自主投标.getValue());
        if (StringUtil.equals(tgBusiType, DictCodeListExt4.平安业务类型_债券分销买入确认.getValue())) {
            req.setPurchModle(DictCodeListExt4.头寸申购方式_分销买入.getValue());
        }
        //交易方向
        req.setPs(cfetsManualSettleInsertDto.getPs());
        // 系统动作 新建 修改 关闭
        req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(batchDate);
        req.setCcy(cfetsManualSettleInsertDto.getCcy());
        req.setFinprodId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getFinprodId());
        req.setPtlList(ptlList);
        logger.info("一级投标确认发送的报文:{}", JsonUtils.object2Json(req));
        return PositionHttpRequest.send(req);
    }

    /**
     * 交收确认时,重新组装插入旧头寸
     *
     * @param cfetsManualSettleRequest
     * @param buttonType
     */
    public void insertOldPosition(CfetsSettleReaderResult cfetsManualSettleRequest, String buttonType, boolean isFlowStart) {
        // 设置登记时的成交编号
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTrdBankManualSettleInfo().getTradeId());
        //  交易表未查询到数据,查询临时表(存续期数据)
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        }

        //交易份额 处理
        listTemp.forEach(item -> {
            if (item.getShareAmt() != null) {
                item.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(), new BigDecimal("100"), 0));
            }
        });
        //  处理组合可用持仓、可用现金
        if ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) &&
                (StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_卖出.getValue())
                        || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
        ) {

            //交收失败 生效 处理 可用
            List<String> orderIds = new ArrayList<>();
            //登记
            listTemp.forEach(item -> {
                String orderId = cfetsSettleMapper.selectRegTradeIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIds.add(orderId);
                }
            });
            //对话，请求
            listTemp.forEach(item -> {
                String orderId = cfetsSettleMapper.selectIbTradeIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIds.add(orderId);
                }
            });

            if (CollectionUtils.isNotEmpty(orderIds)) {
                orderIds.forEach(orderId -> {
                    AllTradeCommonUtil.deleteRegPosition(null, orderId);
                });
            }
            return;
        }

        if (!isFlowStart) {
            if (StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_到期.getValue())
                    || StringUtil.equals(cfetsManualSettleRequest.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
            ) {
                //登记、对话，请求 交收、交收回溯处理可用
                this.updatePtlPosition(listTemp, buttonType);
            }

        }

        TrdBankManualSettleInfo trdBankManualSettleInfo = cfetsManualSettleRequest.getTrdBankManualSettleInfo();
        Map<String, StlPayOrders> orderDtoMap = new HashMap<>();
        if (trdBankManualSettleInfo != null) {
            List<StlPayOrders> list = stlPayOrdersMapper.selectByTradeIds(listTemp.stream().map(CfetsSettleDealInfo::getTradeId).collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(list)) {
                List<StlPayTempOrders> tempOrdersList
                        = stlPayTempOrdersMapper.selectByTradeIds(listTemp.stream().map(CfetsSettleDealInfo::getTradeId).collect(Collectors.toList()));
                tempOrdersList.forEach(tempOrders -> {
                    StlPayOrders stlPayOrders = new StlPayOrders();
                    BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, tempOrders);
                    list.add(stlPayOrders);
                });
            }
            // 划款指令map
            orderDtoMap = list.stream()
                    .collect(Collectors.toMap(StlPayOrders::getTradeId, orderDto -> orderDto));
            logger.info("查询出的划款指令列表:{}", JsonUtils.object2Json(orderDtoMap));
            if(orderDtoMap.isEmpty()){
                logger.debug("未查到划款指令,交易数据:{}",JsonUtils.object2Json(listTemp));
                return;
            }
        }
        //按照交易金额排序
        listTemp = listTemp.stream().sorted(Comparator.comparing(CfetsSettleDealInfo::getTradeAmt).reversed()).collect(Collectors.toList());
        for (CfetsSettleDealInfo item : listTemp) {
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            }

            finProductTradeDto.setNeedSum(true);
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            // 金融产品交易
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
            trdProductDeal.setPayDate(item.getSettleDate());
            if (StringUtil.isNotBlank(trdProductDeal.getfTradeId())) {
                baseTradeProductDealDto.setfTradeId(trdProductDeal.getfTradeId());
            }
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            // 金融产品附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, item);
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);
            baseTradeProductDealDto.setEntrustId(item.getEntrustId());
            //划款指令
            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
            if (trdBankManualSettleInfo != null) {
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, orderDtoMap.get(item.getTradeId()));
                stlPayOrders.add(stlPayOrder);
                logger.info("债券交收处理头寸,首期划款指令:" + JsonUtils.object2Json(stlPayOrders));
            } else {
                // 到期会按交易编号生成单独的划款指令
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, orderDtoMap.get(item.getTradeId()));
                stlPayOrders.add(stlPayOrder);
                logger.info("债券交收处理头寸,到期划款指令:" + JsonUtils.object2Json(stlPayOrders));
            }
            if (CollectionUtils.isNotEmpty(stlPayOrders)
                    && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
            }

            finProductTradeDto.setBaseTradeProductDealDtos(Collections.singletonList(baseTradeProductDealDto));
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
        }
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
            List<String> orderIds = new ArrayList<>();
            for (StlPayOrders order : orderDtoMap.values()) {
                orderIds.add(order.getOrderId());
                orderIds.add(order.getOrderId() + "R");
            }
            logger.info("债券交收回溯,删除流水:" + JsonUtils.object2Json(orderIds));
            baseAssetCashPositionCommMapper.deleteCashPosition03ByOrderIds(orderIds);
        }
    }


    /**
     * 登记、对话，请求 交收、交收回溯处理可用
     *
     * @param listTemp
     * @param buttonType
     */
    private void updatePtlPosition(List<CfetsSettleDealInfo> listTemp, String buttonType) {
        List<String> orderIdList = new ArrayList<>();

        //登记
        listTemp.forEach(item -> {
            String tradeId = cfetsSettleMapper.selectRegTradeIdBytradeId(item.getTradeId());
            if (StringUtil.isNotEmpty(tradeId)) {
                orderIdList.add(tradeId);
            }
        });

        //对话，请求
        listTemp.forEach(item -> {
            String ibTradeId = cfetsSettleMapper.selectIbTradeIdBytradeId(item.getTradeId());
            if (StringUtil.isNotEmpty(ibTradeId)) {
                orderIdList.add(ibTradeId);
            }
        });

        List<String> orderIds = orderIdList.stream().distinct().collect(Collectors.toList());

        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue())
                && CollectionUtils.isNotEmpty(orderIds)) {
            orderIds.forEach(orderId -> {
                        AllTradeCommonUtil.deleteRegPosition(null, orderId);
                    }
            );
        }
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
            if (CollectionUtils.isNotEmpty(orderIds)) {
                List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
                List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();

                //报价
                orderIds.forEach(orderId -> {
                    //查询报价信息 报价多组合会有多条数据
                    List<PtlFlowsDto> dealList = cfetsSettleMapper.selectIbQuoteBytradeId(orderId);
                    dealList.forEach(quoteDeal -> {
                        PtlFlowsDto deal = new PtlFlowsDto();
                        if (quoteDeal != null) {
                            BeanUtil.copyPropertiesIgnoreNull(deal, quoteDeal);
                        }
                        if (StringUtil.isNotEmpty(deal.getTradeId())) {
                            String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                            //证券管理户代码
                            String secManageAcctId = "";
                            if (listTemp.size() > 0) {
                                secManageAcctId = listTemp.get(0).getSecManageAcctId();
                            }
                            if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                                    || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                            ) {
                                ptlSecPositionFlows.add(positionDeal(deal, secManageAcctId));
                            }
                            if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                                ptlCashPositionFlows.add(cashDeal(deal, acctId));
                            }
                        }
                    });

                });

                //登记
                orderIds.forEach(orderId -> {
                    PtlFlowsDto deal = new PtlFlowsDto();
                    //查询登记信息
                    RegFinProductDeal regDeal = cfetsSettleMapper.selectRegFinProductDealByPrimaryKey(orderId);
                    if (regDeal != null) {
                        BeanUtil.copyPropertiesIgnoreNull(deal, regDeal);
                    }

                    if (StringUtil.isNotEmpty(deal.getTradeId())) {
                        String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        //证券管理户代码
                        String secManageAcctId = "";
                        if (listTemp.size() > 0) {
                            secManageAcctId = listTemp.get(0).getSecManageAcctId();
                        }
                        if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_卖出.getValue())
                                || StringUtil.equals(deal.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                        ) {
                            ptlSecPositionFlows.add(positionDeal(deal, secManageAcctId));
                        }
                        if (StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                            ptlCashPositionFlows.add(cashDeal(deal, acctId));
                        }
                    }

                });
                updatePtlFlows(ptlSecPositionFlows, ptlCashPositionFlows);
            }
        }
    }

    /**
     * 组装组合现金流水
     *
     * @param deal
     * @param acctId
     * @return
     */
    private PtlCashPositionFlowDto cashDeal(PtlFlowsDto deal, String acctId) {
        PtlCashPositionFlowDto resultDto = new PtlCashPositionFlowDto();
        // 输入参数.组合代码
        resultDto.setPortfolioId(deal.getPortfolioId());
        // 划款指令Entity.本方账户
        resultDto.setAcctId(StringUtil.isEmpty(acctId) ? "999999" : acctId);
        // 输入参数.持仓类型
        resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        // 划款指令Entity.币种
        resultDto.setCcy(deal.getCcy());
        // 划款指令Entity.指令编号
        resultDto.setOrderId(deal.getTradeId());
        // 流程id
        resultDto.setEntrustId(deal.getEntrustId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        resultDto.setCdate(deal.getSettleDate());
        resultDto.setAmount(ArithmeticUtil.multiply(deal.getTradeAmt(), new BigDecimal(-1)));
        resultDto.setIsExistNew(DictCodeList.是否_是.getValue());

        return resultDto;

    }

    /**
     * 组装组合持仓流水
     *
     * @param deal
     * @param acctId
     * @return
     */
    private PtlSecPositionFlowDto positionDeal(PtlFlowsDto deal, String acctId) {
        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
        resultDto.setPortfolioId(deal.getPortfolioId());

        resultDto.setSecAcctId(StringUtil.isEmpty(acctId) ? "999999" : acctId);
        resultDto.setFinprodId(deal.getFinprodId());
        resultDto.setBranch(0L);
        resultDto.setInvAim(StringUtil.isEmpty(deal.getInvAim()) ? "999999" : deal.getInvAim());
        resultDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
        resultDto.setCcy(deal.getCcy());
        resultDto.setTradeId(deal.getTradeId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        resultDto.setCdate(deal.getSettleDate());
        resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
        resultDto.setIsExistNew(DictCodeList.是否_是.getValue());
        return resultDto;
    }

    /**
     * 更新组合流水
     *
     * @param ptlSecPositionFlows
     * @param ptlCashPositionFlows
     */
    private void updatePtlFlows(List<PtlSecPositionFlowDto> ptlSecPositionFlows, List<PtlCashPositionFlowDto> ptlCashPositionFlows) {
        if (CollectionUtils.isNotEmpty(ptlSecPositionFlows)) {
            ptlSecPositionFlows.forEach(x -> {
                //调用业务编号共通组件获取流水号
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
                x.setSeqno(seqNo);
                ptlSecPositionFlowMapper.insert(x);
            });
        }

        if (CollectionUtils.isNotEmpty(ptlCashPositionFlows)) {
            ptlCashPositionFlows.forEach(x -> {
                //调用业务编号共通组件获取流水号
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setSeqNo("9999");
                String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
                x.setSeqno(seqNo);
                ptlCashPositionFlowMapper.insert(x);
            });
        }
    }

    /**
     * 交收确认时,重新组装质押式回购插入旧头寸
     *
     * @param cfetsManualSettleRequest
     * @param buttonType
     * @param finprodType2             资产类型
     * @param isFlowStart              是否流程发起
     * @author jiangpeng
     * @date 2022/3/14 10:23
     */
    public void insertOldRepoPosition(CfetsSettleReaderResult cfetsManualSettleRequest,
                                      String buttonType, String finprodType2,
                                      boolean isFlowStart, String busiType, boolean isBack) {
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            logger.info("是否处理旧头寸标识为[否]不处理旧头寸");
            return;
        }
        if (isFlowStart) {
            logger.info("批处理不处理流程发起时的头寸");
            return;
        }
        logger.info("cfetsManualSettleRequest.getTradeId():" + cfetsManualSettleRequest.getTradeId());
        //设置登记时的成交编号
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        if (CollectionUtils.isEmpty(listTemp)) {
            listTemp = cfetsSettleMapper.getTempDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        }
        // 持仓回溯
        if (this.isNeedRevokePosition(finprodType2, isFlowStart, isBack, buttonType, busiType)) {
            this.settleFailPosition(listTemp, busiType, StringUtil.equals(DictCodeListExt3.交收操作类型_回溯.getValue(), buttonType));
        }
        if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                && StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            return;
        }
        List<StlPayOrders> list = stlPayOrdersMapper.selectByTradeIds(listTemp.stream().map(CfetsSettleDealInfo::getTradeId).collect(Collectors.toList()));
        // 存续期数据 划款指令存在临时表中
        if (CollectionUtils.isEmpty(list)) {
            List<StlPayTempOrders> ordersListTemp = stlPayTempOrdersMapper.selectByTradeIds(listTemp.stream()
                    .map(CfetsSettleDealInfo::getTradeId).collect(Collectors.toList()));
            ordersListTemp.forEach(ordersTemp -> {
                StlPayOrders orders = new StlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(orders, ordersTemp);
                list.add(orders);
            });
        }
        // 划款指令map
        Map<String, StlPayOrders> orderDtoMap = list.stream()
                .collect(Collectors.toMap(StlPayOrders::getTradeId, orderDto -> orderDto));
        logger.info("查询出的指令列表:{}", JsonUtils.object2Json(orderDtoMap));
        if (orderDtoMap.isEmpty()) {
            logger.debug("未查到划款指令,交易数据:{}", JsonUtils.object2Json(listTemp));
            return;
        }
        listTemp = listTemp.stream().sorted((x, y) -> y.getTradeAmt().compareTo(x.getTradeAmt())).collect(Collectors.toList());
        List<String> orderIdList = new ArrayList<>();
        for (CfetsSettleDealInfo item : listTemp) {
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && !isFlowStart) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            }
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue()) && (isFlowStart || isBack)) {
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            }
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            //表格质押券data
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("busiCode", item.getFinprodId());
            pageParam.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            pageParam.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            pageParam.put("platDate", DateUtil.format(trdProductDeal.getTradeDate()));
            List<CfetsPledgeInfoResponse> cfetsPledgeInfoList = cfetsSettleMapper.findRepoPledgeInfoList(pageParam);
            List<PledgeBondInfoDto> pledgeBondInfoDtoList = new ArrayList<>();
            cfetsPledgeInfoList.forEach(info -> {
                PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                pledgeBondInfoDto.setBondCode(info.getFinprodMarketId());
                // 质押券面已按比例折算,且已万元转化 此处做还原操作
                pledgeBondInfoDto.setPledgePrin(ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(new BigDecimal(1000000), info.getPledgePrin()), info.getPledgeRate(), 4));
                pledgeBondInfoDto.setFinprodId(info.getFinprodId());
                pledgeBondInfoDtoList.add(pledgeBondInfoDto);
            });
            BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
            bussRepoSecDto.setPledgeBondList(pledgeBondInfoDtoList);
            //划款指令
            baseTradeProductDealDto.setBussRepoSec(bussRepoSecDto);
            List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
            if (StringUtil.equals(busiType, DictCodeListExt1.平安业务类型_逆回购到期.getValue())
                    || StringUtil.equals(busiType, DictCodeListExt1.平安业务类型_正回购到期.getValue())) {
                // 到期会按交易编号生成单独的划款指令
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, orderDtoMap.get(item.getTradeId()));
                stlPayOrders.add(stlPayOrder);
                logger.info("交收处理头寸,到期划款指令:" + JsonUtils.object2Json(stlPayOrders));
            } else {
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, orderDtoMap.get(item.getTradeId()));
                String orderId = cfetsSettleMapper.selectRegTradeIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIdList.add(orderId);
                }
                orderId = cfetsSettleMapper.selectQuoteIdBytradeId(item.getTradeId());
                if (StringUtil.isNotEmpty(orderId)) {
                    orderIdList.add(orderId);
                }
                stlPayOrders.add(stlPayOrder);
                logger.info("交收处理头寸,首期期划款指令:" + JsonUtils.object2Json(stlPayOrders));
            }
            baseTradeProductDealDto.setEntrustId(item.getEntrustId());
            if (StringUtil.isEmpty(baseTradeProductDealDto.getEntrustId())) {
                baseTradeProductDealDto.setEntrustId(cfetsSettleMapper.qryEntrustId(item.getFinprodId()));
            }
            // shareAmt中为质押券面总额,换为交易金额
            trdProductDeal.setShareAmt(trdProductDeal.getPrinAmt());
            if (isBack) {
                trdProductDeal.setPrinAmt(trdProductDeal.getPrinAmt() == null ? null : trdProductDeal.getPrinAmt().negate());
                trdProductDeal.setShareAmt(trdProductDeal.getShareAmt() == null ? null : trdProductDeal.getShareAmt().negate());
                trdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt() == null ? null : trdProductDeal.getTradeAmt().negate());
                stlPayOrders.forEach(ordersDto ->
                        ordersDto.setSettleAmt(ordersDto.getSettleAmt().negate())
                );
            }
            // 划款指令
            baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
            // 金融产品交易
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
            // 金融产品附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, item);
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

            baseTradeProductDealDto.setStage("03");

            finProductTradeDto.setBaseTradeProductDealDtos(Collections.singletonList(baseTradeProductDealDto));
            finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
            finProductTradeDto.setAssetType2(DictCodeList.回购类型_质押式回购.getValue());
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.insertPosition(finProductTradeDto);
        }
        if (CollectionUtils.isNotEmpty(orderIdList) && !isBack && isFlowStart) {
            baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(orderIdList);
        }
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
            List<String> orderIds = new ArrayList<>();
            for (StlPayOrders order : orderDtoMap.values()) {
                orderIds.add(order.getOrderId());
                orderIds.add(order.getOrderId() + "R");
            }
            logger.info("债券交收回溯,删除流水:" + JsonUtils.object2Json(orderIds));
            baseAssetCashPositionCommMapper.deleteCashPosition03ByOrderIds(orderIds);
        }
    }

    public void insertOldBidPosition(CfetsSettleReaderResult cfetsManualSettleRequest,
                                     String buttonType,
                                     boolean isFlowStart) {
        if (StringUtil.equals(DictCodeList.是否_否.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            return;
        }
//        Set<String> tradeIdsSet = new HashSet<>();
        //设置登记时的成交编号
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleRequest.getTradeId());
        // 根据交易编号查询划款指令
        List<StlPayOrders> list = stlPayOrdersMapper.selectByTradeIds(listTemp.stream().map(CfetsSettleDealInfo::getTradeId).collect(Collectors.toList()));
        // 划款指令map
        Map<String, StlPayOrders> orderDtoMap = list.stream()
                .collect(Collectors.toMap(StlPayOrders::getTradeId, orderDto -> orderDto));
        logger.info("查询出的划款指令列表:{}", JsonUtils.object2Json(orderDtoMap));
        if(orderDtoMap.isEmpty()){
            logger.debug("未查到划款指令,交易数据:{}",JsonUtils.object2Json(listTemp));
            return;
        }
        for (CfetsSettleDealInfo deal : listTemp) {
            if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())) {
//                tradeIdsSet.add(deal.getTradeId());
            } else {
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue())) {
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                } else {
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                }
                finProductTradeDto.setAssetType2("ISSUE_CLASS_02");
                finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                baseTradeProductDealDto.setEntrustId(deal.getEntrustId());
                baseTradeProductDealDto.setStage("03");
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, deal);
                trdProductDeal.setFinprodId(deal.getFinprodId());
                trdProductDeal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                trdProductDeal.setSettleDate(deal.getTradeDate());
                trdProductDeal.setPrinAmt(deal.getShareAmt());
                trdProductDeal.setFpriceAmt(deal.getTradeAmt());
                trdProductDeal.setTradeDate(deal.getTradeDate());
                trdProductDeal.setSettleDate(deal.getSettleDate());
                trdProductDeal.setSecManageAcctId(deal.getSecManageAcctId());
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                TrdProductDealAdd add = new TrdProductDealAdd();
                add.setPortfolioId(deal.getPortfolioId());
                add.setTradeId(deal.getTradeId());
                add.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                add.setEntrustId(deal.getEntrustId());
                baseTradeProductDealDto.setTrdProductDealAdd(add);
                //处理指令完成情况
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(stlPayOrder, orderDtoMap.get(trdProductDeal.getTradeId()));
                baseTradeProductDealDto.setStlPayOrdersList(Collections.singletonList(stlPayOrder));
                finProductTradeDto.setBaseTradeProductDealDtos(Collections.singletonList(baseTradeProductDealDto));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            }
        }
        if (StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_回溯.getValue()) && !isFlowStart) {
            List<String> orderIds = new ArrayList<>();
            for (StlPayOrders order : orderDtoMap.values()) {
                orderIds.add(order.getOrderId());
                orderIds.add(order.getOrderId() + "R");
            }
            logger.info("债券交收回溯,删除流水:" + JsonUtils.object2Json(orderIds));
            baseAssetCashPositionCommMapper.deleteCashPosition03ByOrderIds(orderIds);
        }

    }

    /**
     * 交收失败时处理头寸信息
     *
     * @param listTemp
     * @author jiangpeng
     * @date 2022/3/11 15:51
     */
    private void settleFailPosition(List<CfetsSettleDealInfo> listTemp, String busiType, boolean isRevoke) {
        listTemp.forEach(deal -> {
                    // 登记编号查询
                    String regId = cfetsSettleMapper.selectRegTradeIdBytradeId(deal.getFinprodId());
                    if (StringUtil.isEmpty(regId)) {
                        // 报价编号查询
                        regId = cfetsSettleMapper.selectQuoteIdBytradeId(deal.getFinprodId());
                    }
                    //更新质押position持仓
                    this.updatePledgeBondForRevoke(regId, DictCodeList.持仓类型_质押.getValue(), deal.getTradeId(), isRevoke, deal.getSettleDate());
                }
        );
    }

    /**
     * 更新质押position持仓
     *
     * @param bizCode
     * @param holdingType
     * @param tradeId
     * @return
     */
    public long updatePledgeBondForRevoke(String bizCode, String holdingType, String tradeId, boolean isRevoke, Date settleDate) {
        if (StringUtil.isEmpty(tradeId)) {
            tradeId = bizCode;
        }
        Map<String, Object> param = this.setParams(bizCode, holdingType);
        List<PtlSecPositionFlow> ptlSecPositionFlows;
        ptlSecPositionFlows = this.更新持仓(param, tradeId, isRevoke, settleDate);
        return ptlSecPositionFlows.size();
    }

    private static Map<String, Object> setParams(String bizCode, String holdingType) {
        Map<String, Object> param = new HashMap<>();
        param.put("bizCode", bizCode);
        if (StringUtil.isNotEmpty(holdingType) && !"null".equals(holdingType)) {
            param.put("POSITION_TYPE_ROLLIN", holdingType);
            param.put("POSITION_TYPE_ROLLOUT", holdingType);
        } else {
            param.put("POSITION_TYPE_ROLLIN", DictCodeList.持仓类型_融入.getValue());
            param.put("POSITION_TYPE_ROLLOUT", DictCodeList.持仓类型_融出.getValue());
        }
        return param;
    }

    private List<PtlSecPositionFlow> 更新持仓(Map<String, Object> param, String tradeId, boolean isRevoke, Date settleDate) {
        List<PtlSecPositionFlow> ptlSecPositionFlows = cfetsSettleMapper.selectPtlSecPositionFlow(param);
        for (PtlSecPositionFlow flow : ptlSecPositionFlows) {
            PtlSecPosition position = new PtlSecPosition();
            if (StringUtil.isNotEmpty(settleDate)) {
                flow.setCdate(settleDate);
            }
            BeanUtil.copyPropertiesIgnoreNull(position, flow);
            if (!isRevoke) {
                flow.setAmount(flow.getAmount().negate());
            }
            flow.setSeqno(IDGenerator.generate());
            flow.setTradeId(tradeId);
            position.setAmount(flow.getAmount().negate());
            flow.setCreateTime(new Date());
            flow.setUpdateTime(new Date());
            flow.setCreateUser(LoginUserUtil.getUserName());
            flow.setUpdateUser(LoginUserUtil.getUserName());
            ptlSecPositionFlowMapper.insert(flow);
            cfetsSettleMapper.updatePosition(position);
        }
        return ptlSecPositionFlows;
    }

    /**
     * 判断是否需要撤回质押持仓
     * <p>正回购 交收失败 指令质押券可用持仓增加 组合质押券质押持仓减少
     * <p>正回购到期 交收 指令质押券可用持仓增加 组合质押券质押持仓减少 回溯反向处理
     *
     * @param finprodType2 业务类型
     * @param isFlowStart  是否流程发起
     * @param isBack       是否退回
     * @param buttonType   提交类型 交收/回溯/交收失败
     * @param busiType     业务类型
     * @return boolean 返回结果
     * @author jiangpeng
     * @date 2022/7/25 11:37
     */
    private boolean isNeedRevokePosition(String finprodType2, boolean isFlowStart, boolean isBack, String buttonType, String busiType) {
        boolean resultFlag = false;
        // 非流程生效
        if (isFlowStart || isBack) {
            return resultFlag;
        }
        // 非正回购
        if (!StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
            return resultFlag;
        }
        // 正回购 交收失败
        if ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收失败.getValue())
                && StringUtil.equals(busiType, DictCodeListExt1.平安业务类型_正回购.getValue()))) {
            resultFlag = true;
        }
        // 正回购到期 交收
        if ((StringUtil.equals(buttonType, DictCodeListExt3.交收操作类型_交收.getValue())
                || StringUtil.equals(DictCodeListExt3.交收操作类型_回溯.getValue(), buttonType))
                && StringUtil.equals(busiType, DictCodeListExt1.平安业务类型_正回购到期.getValue())) {
            resultFlag = true;
        }
        return resultFlag;
    }
}
