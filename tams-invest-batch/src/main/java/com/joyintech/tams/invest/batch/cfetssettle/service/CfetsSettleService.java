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
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.PositionRulePtlData;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.TrdBankManualSetDetailMapper;
import com.joyintech.tams.common.base.dao.TrdBankManualSettleFlowMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductTempDealTotalMapper;
import com.joyintech.tams.common.base.entity.TrdBankManualSetDetail;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleFlow;
import com.joyintech.tams.common.base.entity.TrdBankManualSettleInfo;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.batch.cfetssettle.dao.CfetsSettleMapper;
import com.joyintech.tams.invest.batch.cfetssettle.dto.*;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.流程节点_流程提交;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_关闭;

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
    private TrdBankManualSettleFlowMapper trdBankManualSettleFlowMapper;

    @Autowired(required = false)
    private TrdBankManualSetDetailMapper trdBankManualSetDetailMapper;

    @Autowired(required = false)
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired(required = false)
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired(required = false)
    private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;

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
        logger.info("日终自动交收开启");
//        //删除当日手工交收数据
//        //删除交收流水表
//        cfetsSettleMapper.deleteTodayCfetsSettleFlow(paramMap);
//        //删除交收明细表
//        cfetsSettleMapper.deleteTodayCfetsSettleDetail(paramMap);
//        //删除交收信息表
//        cfetsSettleMapper.deleteTodayCfetsSettleInfo(paramMap);

        //生成当日手工交收数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("batchDate", batchDate);
        paramMap.put("createrUser", "SYSTEM");
        List<CfetsSettleReaderResult> insertDtoLists = this.createTodayTodoSettleData(paramMap);
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
            }
            //更新风险占用
            updateOccupy(cfetsManualSettleInsertDto);
            logger.info("日终自动交收数据:{}", JsonUtils.object2Json(cfetsManualSettleInsertDto));
            //对债券数据发送头寸
            if (StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券.getValue())) {
                sendPositionMethod(cfetsManualSettleInsertDto, batchDate);
            }
            //对回购数据发送头寸
            if (StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
                    || StringUtil.equals(cfetsManualSettleInsertDto.getFinprodType2(), DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                sendRepoPositionMethod(cfetsManualSettleInsertDto, batchDate);
            }
            if (StringUtil.equals(cfetsManualSettleInsertDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())) {
                // 一级申购发送头寸报文
                this.sendBidPositionMethod(cfetsManualSettleInsertDto, batchDate);
            }
            if (StringUtil.equals(cfetsManualSettleInsertDto.getTgBusiType(), DictCodeListExt3.头寸业务类型_债券一级投标.getValue())) {
                // 一级申购发送头寸报文
                this.sendBidFirmPositionMethod(cfetsManualSettleInsertDto, batchDate);
            }

        }

    }

    private void sendPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
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
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
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
        PositionHttpRequest.send(req);
    }

    private void sendRepoPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
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
        req.setPs(cfetsManualSettleInsertDto.getPs());
        //新建 修改 关闭
        req.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        //决策树
        req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
        req.setCdate(batchDate);
        req.setCcy(cfetsManualSettleInsertDto.getCcy());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        logger.info("回购发送到头寸的报文:{}", JsonUtils.object2Json(req));
        PositionHttpRequest.send(req);
    }

    private List<CfetsSettleReaderResult> createTodayTodoSettleData(Map<String, Object> param) {
        List<CfetsSettleReaderResult> listTemp = new ArrayList<CfetsSettleReaderResult>();
        Date batchDate = (Date) param.get("batchDate");
        //平安业务类型
        param.put("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        param.put("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        param.put("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        param.put("PINAN_BUSINESS_TYPE_35", DictCodeListExt1.平安业务类型_债券中间收息.getValue());
        param.put("PINAN_BUSINESS_TYPE_36", DictCodeListExt1.平安业务类型_债券中间还本.getValue());
        param.put("PINAN_BUSINESS_TYPE_37", DictCodeListExt1.平安业务类型_债券到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        param.put("PINAN_BUSINESS_TYPE_51", DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_52", DictCodeListExt1.平安业务类型_正回购到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        param.put("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        param.put("PINAN_BUSINESS_TYPE_76", DictCodeListExt1.平安业务类型_买断式正回购到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_77", DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_105", DictCodeListExt2.平安业务类型_债券借贷融入.getValue());
        param.put("PINAN_BUSINESS_TYPE_106", DictCodeListExt2.平安业务类型_债券借贷融出.getValue());
        param.put("PINAN_BUSINESS_TYPE_107", DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_108", DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        param.put("PINAN_BUSINESS_TYPE_180", DictCodeListExt4.平安业务类型_一级申购.getValue());
        param.put("PINAN_BUSINESS_TYPE_151", DictCodeListExt4.平安业务类型_中标结果维护.getValue());
        param.put("tgBusiTypes", Arrays.asList(DictCodeListExt1.平安业务类型_债券类资产买入.getValue(), DictCodeListExt1.平安业务类型_债券类资产卖出.getValue()
                , DictCodeListExt1.平安业务类型_债券类资产行权.getValue(), DictCodeListExt1.平安业务类型_债券中间收息.getValue(), DictCodeListExt1.平安业务类型_债券中间还本.getValue()
                , DictCodeListExt1.平安业务类型_债券到期.getValue(), DictCodeListExt1.平安业务类型_正回购.getValue(), DictCodeListExt1.平安业务类型_逆回购到期.getValue()
                , DictCodeListExt1.平安业务类型_正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式正回购.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购.getValue()
                , DictCodeListExt1.平安业务类型_买断式正回购到期.getValue(), DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入.getValue()
                , DictCodeListExt2.平安业务类型_债券借贷融出.getValue(), DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue(), DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue()
                , DictCodeListExt4.平安业务类型_一级申购.getValue(), DictCodeListExt4.平安业务类型_中标结果维护.getValue()));
        //收付方向
        param.put("feePs02", DictCodeListExt1.收付方向_支出.getValue());
        //结算状态
        param.put("settlementStatus01", DictCodeList.结算状态_已结算.getValue());
        param.put("settlementStatus02", DictCodeList.结算状态_已结算.getValue());
        //指令处理状态
        param.put("commandDealStatus01", DictCodeList.指令处理状态_处理成功.getValue());
        param.put("commandDealStatus02", DictCodeList.指令处理状态_处理失败.getValue());
        //指令处理状态
        param.put("cleanStatus01", DictCodeListExt3.清算状态_清算成功.getValue());
        param.put("cleanStatus02", DictCodeListExt3.清算状态_清算失败.getValue());
        param.put("cleanStatus03", DictCodeListExt3.清算状态_清算中.getValue());
        //交收状态
        param.put("DELIVERY_STATUS_01", DictCodeListExt3.交收状态_未交收.getValue());
        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("isCalcN", BussinessDictCodeList.是否_否.getValue());
        param.put("batchDate", batchDate);
        param.put("pageId", "CfetsManualSettle");
        param.put("pageId01", "CfetsManualSettleUpdate");
        param.put("tradePlat_01", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradePlat_02", BussinessDictCodeList.交易场所_商业银行柜台市场.getValue());

        //查询待交收数据List
        List<CfetsSettleAllInfoDto> cfetsSettleDtoList = cfetsSettleMapper.selectAllSettleToDoList(param);
        if (!StringUtil.isEmptyList(cfetsSettleDtoList)) {
            for (CfetsSettleAllInfoDto cfetsSettleAllInfoDto : cfetsSettleDtoList) {
                CfetsSettleReaderResult cfetsSettleReaderResult = new CfetsSettleReaderResult();
                //银行间手工交收信息Entity
                TrdBankManualSettleInfo trdBankManualSettleInfo = this.createTrdBankManualSettleInfo(cfetsSettleAllInfoDto, batchDate);
                //银行间手工交收流水信息Entity
                TrdBankManualSettleFlow trdBankManualSettleFlow = this.createTrdBankManualSettleFlow(cfetsSettleAllInfoDto, trdBankManualSettleInfo, batchDate);
                //银行间手工交收明细信息Entity
                List<TrdBankManualSetDetail> listTrdBnakSetDetails = new ArrayList<TrdBankManualSetDetail>();
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
        String url;
        Message<AjaxResult> message;
        url = String.format("/riskTrial/doOccupy/%s/%s/%s", "CfetsManualSettle", 系统动作_关闭.getValue(), 流程节点_流程提交.getValue());
        if (isSingle) {
            message = simpleRestTemplate.postForObject(tamsSerever + url, cfetsManualSettleInsertDto, Message.class);
        } else {
            message = restTemplate.postForObject(tamsSerever + url, cfetsManualSettleInsertDto, Message.class);
        }
        logger.info("风险占用更新结果：{}", JsonUtils.object2Json(message));
        if (Objects.nonNull(message) && !"000".equals(message.getCode())) {
            logger.error("风险占用更新异常：totalTradeId {}", cfetsManualSettleInsertDto.getTotalTradeId());
        }
    }

    /**
     * 构建一级申购发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendBidPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
        //组装组合金额信息
        List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
        List<CfetsSettleDealInfo> listTemp = cfetsSettleMapper.getDealDetailsByTradeId(cfetsManualSettleInsertDto.getTrdBankManualSettleInfo().getTradeId());
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
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
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
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
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
        PositionHttpRequest.send(req);
    }
    /**
     * 构建一级投标确认发送头寸信息
     * @param cfetsManualSettleRequest
     * @return
     */
    private void sendBidFirmPositionMethod(CfetsSettleReaderResult cfetsManualSettleInsertDto, Date batchDate) {
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
            }
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    positionRulePtlData.setSecAcctId(secAcctId); // 证券账户
                }
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
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
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
        PositionHttpRequest.send(req);
    }
}
