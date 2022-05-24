package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.InterfCommonConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.CommentFunction;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

@Service
public class CreditProtectionCertificatePeriod002Service extends BaseService<CreditProtectionCertificatePeriodSaveDto> implements BaseProcessService<CreditProtectionCertificatePeriodSaveDto> {

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private PositionRequestServiceCreditProtectionCertificatePeriod positionRequestService;

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;


    @Override
    public void doComplete(CreditProtectionCertificatePeriodSaveDto entity, OperationType operationType) {
        BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
        deleteFlow(entity.getWorkflowId());
        insertDate(baseBussinessDto, entity);
        if (!StringUtil.equals("save", entity.getOperateType())) {
            doOldPositionConfirm(entity, false);
            String decisionAct = 系统动作_新建.getValue();
            String systemAct = 系统动作_新建.getValue();
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct);
        }
    }

    @Override
    public void doInsert(CreditProtectionCertificatePeriodSaveDto entity, OperationType type) {
        BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
        deleteFlow(entity.getWorkflowId());
        insertFlow(baseBussinessDto, entity);
        if (!StringUtil.equals("save", entity.getOperateType())) {

            doOldPositionSubmit(entity, true);

            String decisionAct = 系统动作_新建.getValue();
            String systemAct = 系统动作_新建.getValue();
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct);
        }
    }

    @Override
    public void doUpdate(CreditProtectionCertificatePeriodSaveDto entity) {
        BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
        deleteFlow(entity.getWorkflowId());
        insertFlow(baseBussinessDto, entity);
        if (!StringUtil.equals("save", entity.getOperateType())) {
            String decisionAct = 系统动作_新建.getValue();
            String systemAct = 系统动作_新建.getValue();
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct);
        }
    }
//
//    @Override
//    public void doDelete(String workflowId) {
//        baseTradeProductDealCommServiceImpl.delete(workflowId);
//    }

    @Override
    public void doBack(CreditProtectionCertificatePeriodSaveDto entity) {
        doOldPositionSubmit(entity, true);
        AllTradeCommonUtil.deletePositionReal(entity.getBusiCode());
    }

    @Override
    public void doRevoke(CreditProtectionCertificatePeriodSaveDto entity) {
        AllTradeCommonUtil.deletePositionReal(entity.getBusiCode());
        CreditProtectionCertificatePeriodSaveDto dto = getBussinessEntityByWorkflowId(entity.getWorkflowId());
        doOldPositionSubmit(dto, true);
        doOldPositionConfirm(dto, true);
        positionRequestService.sendRevokeCreditProtectionCertificatePeriod(entity);
    }

    private void insertFlow(BaseBussinessDto baseBussinessDto, CreditProtectionCertificatePeriodSaveDto entity) {
        //汇总数据

        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtils.copyProperties(baseBussinessDto.getTrdFinProductDealTotal(), wflTrdFinProductDealTotal);
        wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);


        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtils.copyProperties(baseBussinessDto.getTrdDealTotalAdd(), wflTrdDealTotalAdd);
        wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
        wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

        //处理明细数据
        insertFlowDetails(entity);
    }

    private void insertDate(BaseBussinessDto baseBussinessDto, CreditProtectionCertificatePeriodSaveDto entity) {
        //汇总数据

        TrdFinProductDealTotal trdFinProductDealTotal = baseBussinessDto.getTrdFinProductDealTotal();
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);


        TrdDealTotalAdd trdDealTotalAdd = baseBussinessDto.getTrdDealTotalAdd();
        trdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
        trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

        //处理明细数据
        insertDetails(entity);
    }

    private void insertDetails(CreditProtectionCertificatePeriodSaveDto entity) {
        List<BillingDetailsDto> billingDetails = entity.getBillingDetails();
        for (BillingDetailsDto billingDetail : billingDetails) {
            BaseBussinessDto baseBussinessDto1 = genBaseBussinessDto2(entity, billingDetail);

            //金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = baseBussinessDto1.getTrdFinProductDealTotal();
            trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
            trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

            //金融产品交易汇总附属表
            TrdDealTotalAdd trdDealTotalAdd = baseBussinessDto1.getTrdDealTotalAdd();
            trdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
            trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);
            //金融产品交易
            TrdProductDeal trdProductDeal = baseBussinessDto1.getTrdProductDeal();
            trdProductDeal.setWorkflowId(entity.getWorkflowId());
            trdProductDealMapper.insert(trdProductDeal);
            //金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd = baseBussinessDto1.getTrdProductDealAdd();
            trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            trdProductDealAddMapper.insertSelective(trdProductDealAdd);
            //划款指令表
            StlPayOrders stlPayOrders = baseBussinessDto1.getStlPayOrders();
            stlPayOrders.setWorkflowId(entity.getWorkflowId());
            stlPayOrdersMapper.insertSelective(stlPayOrders);
            //组合交易表
            if (baseBussinessDto1.getPtlTrade() != null) {
                PtlTrade ptlTrade = baseBussinessDto1.getPtlTrade();
                ptlTradeMapper.insertSelective(ptlTrade);
            }
        }
    }

    private void insertFlowDetails(CreditProtectionCertificatePeriodSaveDto entity) {
        List<BillingDetailsDto> billingDetails = entity.getBillingDetails();
        entity.setBaseBussinessDtos(new ArrayList<>());
        for (BillingDetailsDto billingDetail : billingDetails) {
            BaseBussinessDto baseBussinessDto1 = genBaseBussinessDto2(entity, billingDetail);
            entity.getBaseBussinessDtos().add(baseBussinessDto1);
            //金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = baseBussinessDto1.getTrdFinProductDealTotal();
            WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
            BeanUtils.copyProperties(trdFinProductDealTotal, wflTrdFinProductDealTotal);
            wflTrdFinProductDealTotal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
            wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

            //金融产品交易汇总附属表
            TrdDealTotalAdd trdDealTotalAdd = baseBussinessDto1.getTrdDealTotalAdd();
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtils.copyProperties(trdDealTotalAdd, wflTrdDealTotalAdd);
            wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
            //金融产品交易
            TrdProductDeal trdProductDeal = baseBussinessDto1.getTrdProductDeal();
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtils.copyProperties(trdProductDeal, wflTrdProductDeal);
            wflTrdProductDeal.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealMapper.insert(wflTrdProductDeal);
            //金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd = baseBussinessDto1.getTrdProductDealAdd();
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtils.copyProperties(trdProductDealAdd, wflTrdProductDealAdd);
            wflTrdProductDealAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            //划款指令表
            StlPayOrders stlPayOrders = baseBussinessDto1.getStlPayOrders();
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtils.copyProperties(stlPayOrders, wflStlPayOrders);
            wflStlPayOrders.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
            wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
            //组合交易表
            if (baseBussinessDto1.getPtlTrade() != null) {
                PtlTrade ptlTrade = baseBussinessDto1.getPtlTrade();
                ptlTradeMapper.insertSelective(ptlTrade);
            }
        }
    }

    private void deleteFlow(String workflowId) {
        if (StringUtil.isNotBlank(workflowId)) {
            wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
            wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
            wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
            wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
            wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
        }
    }

    public String generateBusiCode(CreditProtectionCertificatePeriodSaveDto dto) {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_信用保护凭证存续期);
        return baseGetBusinessIdImpl.buildBussinessCode(parameter);
    }

    public CommentFunction<CreditProtectionCertificatePeriodSaveDto, String> generateComment(CreditProtectionCertificatePeriodSaveDto dto) {
        return comment -> "业务编号:(" + dto.getBusiCode() + ")-业务类型(" + dto.getPaBussTypeStr() + ")-平台日(" + DateUtil.format(iPlatformDate.getSysDate()) + ")";
    }

    private BaseBussinessDto genBaseBussinessDto(CreditProtectionCertificatePeriodSaveDto dto) {
        BaseBussinessDto result = new BaseBussinessDto();

        TrdFinProductDealTotal trdFinProductDealTotal = genTrdProductDealTotal(dto);
        result.setTrdFinProductDealTotal(trdFinProductDealTotal);

        TrdDealTotalAdd trdDealTotalAdd = genTrdDealTotalAdd(dto);
        result.setTrdDealTotalAdd(trdDealTotalAdd);

        dto.setBaseBussinessDto(result);

        return result;
    }

    //处理明细
    private BaseBussinessDto genBaseBussinessDto2(CreditProtectionCertificatePeriodSaveDto dto, BillingDetailsDto billingDetail) {
        BaseBussinessDto result = new BaseBussinessDto();
        String totleTradeId = generateBusiCode(dto);

        //平台日
        Date platDate = iPlatformDate.getSysDate();

        String calendarId;

        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
        } else {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
        }
        // 下一个交易日
        Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);

        //金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdFinProductDealTotal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        trdFinProductDealTotal.setTradeDate(platDate);
        trdFinProductDealTotal.setVdate(dto.getvDate());
        trdFinProductDealTotal.setMdate(dto.getmDate());
        //交割日期
        trdFinProductDealTotal.setSettleDate(nextWorkDate);

        trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        BigDecimal tradeAmt = billingDetail.getDeliveryAmount().multiply(new BigDecimal("10000"));
        BigDecimal shareAmt = billingDetail.getRepNomCapi().multiply(new BigDecimal("10000"));
        BigDecimal deliverBondAmount = billingDetail.getDeliverBondAmount() == null ? BigDecimal.ZERO : billingDetail.getDeliverBondAmount().multiply(new BigDecimal("10000"));
        trdFinProductDealTotal.setTradeAmt(tradeAmt);
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期.getValue());

        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());

        //现金流代码

        //证券托管户
        trdFinProductDealTotal.setSecManageAcctId(dto.getSecManageAcctId());
        trdFinProductDealTotal.setCreateUser(LoginUserUtil.getUserName());
        trdFinProductDealTotal.setCreateDept(LoginUserUtil.getDeptCode());
        trdFinProductDealTotal.setUpdateUser(LoginUserUtil.getUserName());
        trdFinProductDealTotal.setCreateTime(iPlatformDate.getSysDate());
        trdFinProductDealTotal.setUpdateTime(DateUtil.now());
        trdFinProductDealTotal.setTgBusiType(dto.getCreditEvent());

        trdFinProductDealTotal.setShareAmt(shareAmt);

        trdFinProductDealTotal.setCreditEventHappenedDate(dto.getCreditEventHappenedDate());
        trdFinProductDealTotal.setCreditEventResolveDate(dto.getCreditEventResolveDate());
        trdFinProductDealTotal.setSettleMethod(dto.getSettleMethod());
        trdFinProductDealTotal.setDeliverBond(dto.getDeliverBond());
        trdFinProductDealTotal.setDeliverBondAmount(deliverBondAmount);

        trdFinProductDealTotal.setSecManageAcctId(billingDetail.getSecManageAcctId());
        trdFinProductDealTotal.setpTotleTradeId(dto.getBusiCode());

        trdFinProductDealTotal.setTotleTradeId(totleTradeId);

        result.setTrdFinProductDealTotal(trdFinProductDealTotal);

        //金融产品交易汇总附属表
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();

        trdDealTotalAdd.setTotleTradeId(totleTradeId);
        trdDealTotalAdd.setCreateUser(LoginUserUtil.getUserName());
        trdDealTotalAdd.setCreateDept(LoginUserUtil.getDeptCode());
        trdDealTotalAdd.setUpdateUser(LoginUserUtil.getUserName());
        trdDealTotalAdd.setCreateTime(iPlatformDate.getSysDate());
        trdDealTotalAdd.setUpdateTime(DateUtil.now());
        result.setTrdDealTotalAdd(trdDealTotalAdd);

        //金融产品交易
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(totleTradeId);
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdProductDeal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        trdProductDeal.setBranch(0L);
        trdProductDeal.setTradeDate(iPlatformDate.getSysDate());
        trdProductDeal.setVdate(dto.getvDate());
        trdProductDeal.setMdate(dto.getmDate());
        trdProductDeal.setSettleDate(nextWorkDate);
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdProductDeal.setShareAmt(billingDetail.getRepNomCapi().multiply(new BigDecimal("10000")));
        trdProductDeal.setTradeAmt(billingDetail.getDeliveryAmount().multiply(new BigDecimal("10000")));
        trdProductDeal.setPs(DictCodeList.交易方向_到期.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeListExt1.是否_否.getValue());
        trdProductDeal.setTotleTradeId(dto.getBusiCode());

        trdProductDeal.setCreateUser(LoginUserUtil.getUserName());
        trdProductDeal.setCreateDept(LoginUserUtil.getDeptCode());
        trdProductDeal.setUpdateUser(LoginUserUtil.getUserName());
        trdProductDeal.setCreateTime(iPlatformDate.getSysDate());
        trdProductDeal.setUpdateTime(DateUtil.now());

        trdProductDeal.setTgBusiType(dto.getCreditEvent());
        result.setTrdProductDeal(trdProductDeal);

        //金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(totleTradeId);
        trdProductDealAdd.setCreateUser(LoginUserUtil.getUserName());
        trdProductDealAdd.setCreateDept(LoginUserUtil.getDeptCode());
        trdProductDealAdd.setUpdateUser(LoginUserUtil.getUserName());
        trdProductDealAdd.setCreateTime(iPlatformDate.getSysDate());
        trdProductDealAdd.setUpdateTime(DateUtil.now());

        result.setTrdProductDealAdd(trdProductDealAdd);

        StlPayOrders stlPayOrders = new StlPayOrders();
        stlPayOrders.setOrderId(totleTradeId);
        stlPayOrders.setOrderType("01");
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        stlPayOrders.setMyAcctNo(billingDetail.getAcctNo());
        stlPayOrders.setSettleAmt(tradeAmt);
        stlPayOrders.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        stlPayOrders.setTradeId(totleTradeId);
        stlPayOrders.setPortfolioId(billingDetail.getPortfolioId());
        stlPayOrders.setSettleDate(nextWorkDate);

        stlPayOrders.setCreateUser(LoginUserUtil.getUserName());
        stlPayOrders.setCreateDept(LoginUserUtil.getDeptCode());
        stlPayOrders.setUpdateUser(LoginUserUtil.getUserName());
        stlPayOrders.setCreateTime(iPlatformDate.getSysDate());
        stlPayOrders.setUpdateTime(DateUtil.now());

        result.setStlPayOrders(stlPayOrders);

        if (StringUtil.equals(dto.getOperateType(), BussinessConstant.SUBMIT)) {
            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setPortfolioId(billingDetail.getPortfolioId());
            ptlTrade.setBusiId(totleTradeId);
            ptlTrade.setBusiType(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            ptlTrade.setSecManageAcctId(billingDetail.getSecManageAcctId());
            ptlTrade.setSettleDate(nextWorkDate);
            ptlTrade.setCreateUser(LoginUserUtil.getUserName());
            ptlTrade.setCreateDept(LoginUserUtil.getDeptCode());
            ptlTrade.setUpdateUser(LoginUserUtil.getUserName());
            ptlTrade.setCreateTime(iPlatformDate.getSysDate());
            ptlTrade.setUpdateTime(DateUtil.now());
            result.setPtlTrade(ptlTrade);
        }
        return result;
    }

    private TrdFinProductDealTotal genTrdProductDealTotal(CreditProtectionCertificatePeriodSaveDto dto) {

        //平台日
        Date platDate = iPlatformDate.getSysDate();

        String calendarId;

        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
        } else {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
        }
        // 下一个交易日
        Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);

        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdFinProductDealTotal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());

        trdFinProductDealTotal.setTradeDate(platDate);
        trdFinProductDealTotal.setVdate(dto.getvDate());
        trdFinProductDealTotal.setMdate(dto.getmDate());
        //交割日期
        trdFinProductDealTotal.setSettleDate(nextWorkDate);

        trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        BigDecimal tradeAmt = BigDecimal.ZERO;
        BigDecimal shareAmt = BigDecimal.ZERO;
        BigDecimal deliverBondAmount = BigDecimal.ZERO;
        for (BillingDetailsDto billingDetail : dto.getBillingDetails()) {
            tradeAmt = tradeAmt.add(billingDetail.getDeliveryAmount());
            shareAmt = shareAmt.add(billingDetail.getRepNomCapi());
            if (billingDetail.getDeliverBondAmount() != null) {
                deliverBondAmount = shareAmt.add(billingDetail.getDeliverBondAmount());
            }
        }
        tradeAmt = tradeAmt.multiply(new BigDecimal("10000"));
        shareAmt = shareAmt.multiply(new BigDecimal("10000"));
        deliverBondAmount = deliverBondAmount.multiply(new BigDecimal("10000"));
        trdFinProductDealTotal.setTradeAmt(tradeAmt);
        trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期.getValue());

        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());

        //现金流代码

        //证券托管户
        trdFinProductDealTotal.setSecManageAcctId(dto.getSecManageAcctId());
        trdFinProductDealTotal.setCreateUser(LoginUserUtil.getUserName());
        trdFinProductDealTotal.setCreateDept(LoginUserUtil.getDeptCode());
        trdFinProductDealTotal.setUpdateUser(LoginUserUtil.getUserName());
        trdFinProductDealTotal.setCreateTime(platDate);
        trdFinProductDealTotal.setUpdateTime(DateUtil.now());
        trdFinProductDealTotal.setTgBusiType(dto.getCreditEvent());

        trdFinProductDealTotal.setShareAmt(shareAmt);

        trdFinProductDealTotal.setCreditEventHappenedDate(dto.getCreditEventHappenedDate());
        trdFinProductDealTotal.setCreditEventResolveDate(dto.getCreditEventResolveDate());
        trdFinProductDealTotal.setSettleMethod(dto.getSettleMethod());
        trdFinProductDealTotal.setDeliverBond(dto.getDeliverBond());
        trdFinProductDealTotal.setDeliverBondAmount(deliverBondAmount);

        return trdFinProductDealTotal;
    }

    private TrdDealTotalAdd genTrdDealTotalAdd(CreditProtectionCertificatePeriodSaveDto dto) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();

        trdDealTotalAdd.setTotleTradeId(dto.getBusiCode());
        trdDealTotalAdd.setCreateUser(LoginUserUtil.getUserName());
        trdDealTotalAdd.setCreateDept(LoginUserUtil.getDeptCode());
        trdDealTotalAdd.setUpdateUser(LoginUserUtil.getUserName());
        trdDealTotalAdd.setCreateTime(iPlatformDate.getSysDate());
        trdDealTotalAdd.setUpdateTime(DateUtil.now());
        return trdDealTotalAdd;
    }

    public FinprodLinkDto finprodIdLink(CreditProtectionCertificatePeriod001Request params) {
        //选择资产代码后，联动查询展示：资产名称、起始日、到期日、参考实体
        params.setTradeDate(iPlatformDate.getSysDate());
        FinprodLinkDto finprodLinkDto = creditProtectionCertificatePeriodMapper.findFinprodDetail(params);

        String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");

        List<BillingDetailsDto> details;
        // 开关配置N查老头存
        if (DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
            details = creditProtectionCertificatePeriodMapper.getBillingDetailsByFinprodMarketIdOld(params);
        } else { // 默认查新头寸
            details = creditProtectionCertificatePeriodMapper.getBillingDetailsByFinprodMarketId(params);
        }

        finprodLinkDto.setBillingDetails(details);

        List<String> refSecs = new ArrayList<>();

        if (StringUtil.isEmpty(finprodLinkDto.getRefSec())) {
            if (StringUtil.isNotEmpty(finprodLinkDto.getRefEntity())) {
                refSecs = creditProtectionCertificatePeriodMapper.getRefSecsByRefEntity(finprodLinkDto.getRefEntity());
            }
        } else {
            refSecs.add(finprodLinkDto.getRefSec());
        }

        finprodLinkDto.setRefSecs(refSecs);
        return finprodLinkDto;
    }

    public CreditProtectionCertificatePeriodSaveDto getEntityByBusinessCode(String businessCode) {
        CreditProtectionCertificatePeriodSaveDto entity = creditProtectionCertificatePeriodMapper.getEntityByBusinessCode(businessCode);
        entity.setBillingDetails(creditProtectionCertificatePeriodMapper.getBillingDetailByBusinessCode(businessCode));
        return entity;
    }

    /**
     * 获取下一个工作日
     */
    public Date getNextWorkDay(Date tradeDate, String calendarId) {
        if (dateCalService != null) {
            List<String> holiday = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDays(tradeDate, 31), calendarId);
            return dateCalService.calculationTradingDay(tradeDate, 1, holiday, "1");
        }
        return null;
    }

    /**
     * 老头寸处理 新建提交
     *
     * @param entity
     * @param isRevoke
     */
    private void doOldPositionSubmit(CreditProtectionCertificatePeriodSaveDto entity, boolean isRevoke) {
        List<BillingDetailsDto> billingDetails = entity.getBillingDetails();
        //指令持仓流水&指令持仓
        List<EtrSecPositionFlow> etrSecPositionFlows = new ArrayList<>();
        //交易持仓流水&交易持仓
        List<PtlSecPositionFlow> ptlSecPositionFlows = new ArrayList<>();

        if (StringUtil.equals(DictCodeListExt4.结算方式_标的_现金结算.getValue(), entity.getSettleMethod())) {
            for (BillingDetailsDto billingDetail : billingDetails) {
                EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                etrSecPositionFlow.setSeqno(IDGenerator.generate());
                etrSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                etrSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                etrSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                etrSecPositionFlow.setInvAim(null);
                etrSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                etrSecPositionFlow.setCdate(entity.getTradeDate());
                etrSecPositionFlow.setAmount(billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)));
                etrSecPositionFlow.setpFinprodId(null);
                etrSecPositionFlow.setTradeId(entity.getBusiCode());
                etrSecPositionFlow.setOffsetId(entity.getBusiCode());
                etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrSecPositionFlow.setUpdateTime(DateUtil.now());
                etrSecPositionFlows.add(etrSecPositionFlow);

                PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
                ptlSecPositionFlow.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                ptlSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                ptlSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                ptlSecPositionFlow.setInvAim(null);
                ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                ptlSecPositionFlow.setCdate(entity.getTradeDate());
                ptlSecPositionFlow.setAmount(billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)));
                ptlSecPositionFlow.setpFinprodId(null);
                ptlSecPositionFlow.setTradeId(entity.getBusiCode());
                ptlSecPositionFlow.setOffsetId(entity.getBusiCode());
                ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                ptlSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                ptlSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                ptlSecPositionFlow.setUpdateTime(DateUtil.now());
                ptlSecPositionFlows.add(ptlSecPositionFlow);
            }
        } else {
            for (BillingDetailsDto billingDetail : billingDetails) {
                EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                etrSecPositionFlow.setSeqno(IDGenerator.generate());
                etrSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                etrSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                etrSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                etrSecPositionFlow.setInvAim(null);
                etrSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                etrSecPositionFlow.setCdate(entity.getTradeDate());
                etrSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)).negate() : billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)));
                etrSecPositionFlow.setpFinprodId(null);
                etrSecPositionFlow.setTradeId(entity.getBusiCode());
                etrSecPositionFlow.setOffsetId(entity.getBusiCode());
                etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrSecPositionFlow.setUpdateTime(DateUtil.now());
                etrSecPositionFlows.add(etrSecPositionFlow);

                EtrSecPositionFlow etrSecPositionFlow2 = new EtrSecPositionFlow();
                BeanUtils.copyProperties(etrSecPositionFlow, etrSecPositionFlow2);
                etrSecPositionFlow2.setAmount(isRevoke ? billingDetail.getDeliverBondAmount().multiply(BigDecimal.valueOf(10000)).negate() : billingDetail.getDeliverBondAmount().multiply(BigDecimal.valueOf(10000)));
                etrSecPositionFlows.add(etrSecPositionFlow2);

                PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
                ptlSecPositionFlow.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                ptlSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                ptlSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                ptlSecPositionFlow.setInvAim(null);
                ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                ptlSecPositionFlow.setCdate(entity.getTradeDate());
                ptlSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)).negate() : billingDetail.getRepNomCapi().multiply(BigDecimal.valueOf(10000)));
                ptlSecPositionFlow.setpFinprodId(null);
                ptlSecPositionFlow.setTradeId(entity.getBusiCode());
                ptlSecPositionFlow.setOffsetId(entity.getBusiCode());
                ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                ptlSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                ptlSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                ptlSecPositionFlow.setUpdateTime(DateUtil.now());
                ptlSecPositionFlows.add(ptlSecPositionFlow);
                PtlSecPositionFlow ptlSecPositionFlow2 = new PtlSecPositionFlow();
                BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow2);
                ptlSecPositionFlow2.setAmount(isRevoke ? billingDetail.getDeliverBondAmount().multiply(BigDecimal.valueOf(10000)).negate() : billingDetail.getDeliverBondAmount().multiply(BigDecimal.valueOf(10000)));
                ptlSecPositionFlows.add(ptlSecPositionFlow2);
            }
        }
        for (EtrSecPositionFlow etrSecPositionFlow : etrSecPositionFlows) {
            etrSecPositionFlowMapper.insertSelective(etrSecPositionFlow);
        }
        for (PtlSecPositionFlow ptlSecPositionFlow : ptlSecPositionFlows) {
            ptlSecPositionFlowMapper.insertSelective(ptlSecPositionFlow);
        }
    }


    /**
     * 老头寸处理 审批提交
     *
     * @param entity
     * @param isRevoke
     */
    private void doOldPositionConfirm(CreditProtectionCertificatePeriodSaveDto entity, boolean isRevoke) {
        List<EtrCashPositionFlow> etrCashPositionFlows = new ArrayList<>();
        List<PtlCashPositionFlow> ptlCashPositionFlows = new ArrayList<>();
        List<BillingDetailsDto> billingDetails = entity.getBillingDetails();
        String calendarId;
        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
        } else {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
        }
        // 下一个交易日
        Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);
        for (BillingDetailsDto billingDetail : billingDetails) {
            EtrCashPositionFlow etrCashPositionFlow = new EtrCashPositionFlow();
            etrCashPositionFlow.setSeqno(IDGenerator.generate());
            etrCashPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
            etrCashPositionFlow.setAcctId(billingDetail.getSecAcctId());
            etrCashPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
            etrCashPositionFlow.setCdate(nextWorkDate);
            etrCashPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            etrCashPositionFlow.setAmount(isRevoke ? billingDetail.getDeliveryAmount().multiply(BigDecimal.valueOf(10000)).negate() : billingDetail.getDeliveryAmount().multiply(BigDecimal.valueOf(10000)));
            etrCashPositionFlow.setOrderId(entity.getBusiCode());
            etrCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            etrCashPositionFlow.setCreateUser(LoginUserUtil.getUserName());
            etrCashPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
            etrCashPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
            etrCashPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            etrCashPositionFlow.setUpdateTime(DateUtil.now());
            etrCashPositionFlows.add(etrCashPositionFlow);

            PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
            BeanUtils.copyProperties(etrCashPositionFlow, ptlCashPositionFlow);
            ptlCashPositionFlows.add(ptlCashPositionFlow);
        }
        for (EtrCashPositionFlow etrCashPositionFlow : etrCashPositionFlows) {
            etrCashPositionFlowMapper.insertSelective(etrCashPositionFlow);
        }
        for (PtlCashPositionFlow ptlCashPositionFlow : ptlCashPositionFlows) {
            ptlCashPositionFlowMapper.insertSelective(ptlCashPositionFlow);
        }
    }
}
