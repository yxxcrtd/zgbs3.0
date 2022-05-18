package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.CommentFunction;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

@Service
public class CreditProtectionCertificatePeriod002Service extends BaseService<CreditProtectionCertificatePeriodSaveDto> implements BaseProcessService<CreditProtectionCertificatePeriodSaveDto> {

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;

    @Autowired
    private CreditProtectionCertificatePeriod001Service creditProtectionCertificatePeriod001Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private TradeProductDealCommService tradeProductDealCommService;

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


    @Override
    public void doComplete(CreditProtectionCertificatePeriodSaveDto entity, OperationType operationType) {
        BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
        deleteFlow(entity.getWorkflowId());
        insertDate(baseBussinessDto, entity);
        if (!StringUtil.equals("save", entity.getOperateType())) {
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

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    @Override
    public void doBack(CreditProtectionCertificatePeriodSaveDto entity) {
        AllTradeCommonUtil.deletePositionReal(entity.getBusiCode());
    }

    @Override
    public void doRevoke(CreditProtectionCertificatePeriodSaveDto entity) {
        AllTradeCommonUtil.deletePositionReal(entity.getBusiCode());
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

    public CreditProtectionCertificatePeriodSaveDto getCheckEntity(CreditProtectionCertificatePeriod001Response dto) {
        return null;
    }

    public String generateBusiCode(CreditProtectionCertificatePeriodSaveDto dto) {
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_信用保护凭证存续期);
        return baseGetBusinessIdImpl.buildBussinessCode(parameter);
    }

    public CommentFunction<CreditProtectionCertificatePeriodSaveDto, String> generateComment(CreditProtectionCertificatePeriodSaveDto dto) {
        return null;
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

        //金融产品交易汇总表
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdFinProductDealTotal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        trdFinProductDealTotal.setTradeDate(iPlatformDate.getSysDate());
        trdFinProductDealTotal.setVdate(dto.getvDate());
        trdFinProductDealTotal.setMdate(dto.getmDate());
        //交割日期
        trdFinProductDealTotal.setSettleDate(dto.getDeliverDate());

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
        trdProductDeal.setSettleDate(iPlatformDate.getSysDate());
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
            ptlTrade.setSettleDate(iPlatformDate.getSysDate());
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
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(dto.getBusiCode());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdFinProductDealTotal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        trdFinProductDealTotal.setTradeDate(iPlatformDate.getSysDate());
        trdFinProductDealTotal.setVdate(dto.getvDate());
        trdFinProductDealTotal.setMdate(dto.getmDate());
        //交割日期
        trdFinProductDealTotal.setSettleDate(dto.getDeliverDate());

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
        trdFinProductDealTotal.setCreateTime(iPlatformDate.getSysDate());
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
        FinprodLinkDto finprodLinkDto = creditProtectionCertificatePeriodMapper.findFinprodDetail(params);
        return null;
    }

    public CreditProtectionCertificatePeriodSaveDto getEntityByBusinessCode(String businessCode) {
        CreditProtectionCertificatePeriodSaveDto entity = creditProtectionCertificatePeriodMapper.getEntityByBusinessCode(businessCode);
        entity.setBillingDetails(creditProtectionCertificatePeriodMapper.getBillingDetailByBusinessCode(businessCode));
        return entity;
    }
}
