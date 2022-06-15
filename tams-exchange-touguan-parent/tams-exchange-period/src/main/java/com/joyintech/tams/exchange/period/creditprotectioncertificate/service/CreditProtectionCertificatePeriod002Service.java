package com.joyintech.tams.exchange.period.creditprotectioncertificate.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.*;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
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
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
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

    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;

    @Override
    public void doComplete(CreditProtectionCertificatePeriodSaveDto entity, OperationType operationType) {
        BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
        deleteFlow(entity.getWorkflowId());
        insertDate(baseBussinessDto, entity);
        if (!StringUtil.equalsIgnoreCase("save", entity.getOperateType())) {
            doOldPositionConfirm(entity, false);
            String decisionAct = 系统动作_修改.getValue();
            String systemAct = 系统动作_修改.getValue();
            if ("new".equalsIgnoreCase(entity.getOptType())) {
                systemAct = 系统动作_新建.getValue();
                decisionAct = 系统动作_新建.getValue();
            }
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct, DictCodeListExt3.流程节点_审批生效.getValue());
        }
    }

    @Override
    public void doInsert(CreditProtectionCertificatePeriodSaveDto entity, OperationType type) {
        if (!StringUtil.equalsIgnoreCase("save", entity.getOperateType())) {
            BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
            deleteFlow(entity.getWorkflowId());
            insertFlow(baseBussinessDto, entity);
            doOldPositionSubmit(entity, false);
            String decisionAct = 系统动作_修改.getValue();
            String systemAct = 系统动作_修改.getValue();
            if ("new".equalsIgnoreCase(entity.getOptType())) {
                systemAct = 系统动作_新建.getValue();
                decisionAct = 系统动作_新建.getValue();
            }
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct, DictCodeListExt3.流程节点_流程提交.getValue());
        }
    }

    public void doRevokeComplete(CreditProtectionCertificatePeriodSaveDto entity) {
        investPeriodCommonService.revokeReverseTradeTable(entity.getBusiCode(),
                LoginUserUtil.getUserName(), "", null, null, null);
        CreditProtectionCertificatePeriodSaveDto dto = getBussinessEntityByWorkflowId(entity.getWorkflowId());
        doOldPositionSubmit(dto, true);
        doOldPositionConfirm(dto, true);
        positionRequestService.sendRevokeCreditProtectionCertificatePeriod(entity);
    }

    @Override
    public void doUpdate(CreditProtectionCertificatePeriodSaveDto entity) {
        if (!StringUtil.equalsIgnoreCase("save", entity.getOperateType())) {
            BaseBussinessDto baseBussinessDto = genBaseBussinessDto(entity);
            deleteFlow(entity.getWorkflowId());
            insertFlow(baseBussinessDto, entity);
            doOldPositionSubmit(entity, false);
            String decisionAct = 系统动作_修改.getValue();
            String systemAct = 系统动作_修改.getValue();
            if ("new".equalsIgnoreCase(entity.getOptType())) {
                systemAct = 系统动作_新建.getValue();
                decisionAct = 系统动作_新建.getValue();
            }
            positionRequestService.sendCreditProtectionCertificatePeriod(entity, decisionAct, systemAct, DictCodeListExt3.流程节点_流程提交.getValue());
        }
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    @Override
    public void doBack(CreditProtectionCertificatePeriodSaveDto entity) {
        doOldPositionSubmit(entity, true);
        AllTradeCommonUtil.deletePositionReal(entity.getBusiCode());
    }

    @Override
    public void doRevoke(CreditProtectionCertificatePeriodSaveDto entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("bizCode", entity.getBusiCode());
        params.put("workflowId", entity.getWorkflowId());
        params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
        params.put("user", entity.getCreateUser());
        params.put("dept", entity.getCreateDept() == null ? "dept" : entity.getCreateDept());
        params.put("time", entity.getCreateTime());
        params.put("revokeReason", entity.getRevokeReason());
        investPeriodCommonService.insertWflTable(params);

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
        entity.setBaseBussinessDtos(new ArrayList<>());
        for (BillingDetailsDto billingDetail : billingDetails) {
            BaseBussinessDto baseBussinessDto1 = genBaseBussinessDto2(entity, billingDetail);
            entity.getBaseBussinessDtos().add(baseBussinessDto1);

            //金融产品交易
            TrdProductDeal trdProductDeal = baseBussinessDto1.getTrdProductDeal();
            trdProductDeal.setWorkflowId(entity.getWorkflowId());
            trdProductDealMapper.insert(trdProductDeal);
            //金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd = baseBussinessDto1.getTrdProductDealAdd();
            trdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            trdProductDealAddMapper.insertSelective(trdProductDealAdd);

            //债券 金融产品交易
            //金融产品交易
            if (null != baseBussinessDto1.getTrdProductDeal2()) {
                TrdProductDeal trdProductDeal2 = baseBussinessDto1.getTrdProductDeal2();
                trdProductDeal2.setWorkflowId(entity.getWorkflowId());
                trdProductDealMapper.insert(trdProductDeal2);
                //金融产品交易附属表
                TrdProductDealAdd trdProductDealAdd2 = baseBussinessDto1.getTrdProductDealAdd2();
                trdProductDealAdd2.setWorkflowId(entity.getWorkflowId());
                trdProductDealAddMapper.insertSelective(trdProductDealAdd2);
            }

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

            //债券 金融产品交易
            //金融产品交易
            if (null != baseBussinessDto1.getTrdProductDeal2()) {
                TrdProductDeal trdProductDeal2 = baseBussinessDto1.getTrdProductDeal2();
                WflTrdProductDeal wflTrdProductDeal2 = new WflTrdProductDeal();
                BeanUtils.copyProperties(trdProductDeal2, wflTrdProductDeal2);
                wflTrdProductDeal2.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
                wflTrdProductDeal2.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDealMapper.insert(wflTrdProductDeal2);
                //金融产品交易附属表
                TrdProductDealAdd trdProductDealAdd2 = baseBussinessDto1.getTrdProductDealAdd2();
                WflTrdProductDealAdd wflTrdProductDealAdd2 = new WflTrdProductDealAdd();
                BeanUtils.copyProperties(trdProductDealAdd2, wflTrdProductDealAdd2);
                wflTrdProductDealAdd2.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
                wflTrdProductDealAdd2.setWorkflowId(entity.getWorkflowId());
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd2);
            }

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
        return comment -> "业务编号:(" + dto.getBusiCode() + ")-业务类型(" + DictCodeList.共通业务类型_信用保护凭证存续期.getCodeName() + ")-平台日(" + DateUtil.format(iPlatformDate.getSysDate()) + ")";
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
        result.setBillingDetail(billingDetail);
        String totleTradeId;
        if (StringUtil.isEmpty(billingDetail.getBusiCode())) {
            totleTradeId = generateBusiCode(dto);
        } else {
            totleTradeId = billingDetail.getBusiCode();
        }

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

        //金融产品交易
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(totleTradeId);
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        trdProductDeal.setFinprodId(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue() + "_" + dto.getFinprodMarketId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_信用保护凭证.getValue());
        trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        trdProductDeal.setBranch(0L);
        trdProductDeal.setTradeDate(platDate);
        trdProductDeal.setVdate(dto.getvDate());
        trdProductDeal.setMdate(dto.getmDate());
        trdProductDeal.setSettleDate(nextWorkDate);
        trdProductDeal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        trdProductDeal.setShareAmt(billingDetail.getRepNomCapi());

        if (StringUtil.equals(dto.getSettleMethod(), DictCodeListExt4.结算方式_标的_现金结算.getValue())) {
            trdProductDeal.setTradeAmt(billingDetail.getDeliveryAmount());
        } else {
            trdProductDeal.setTradeAmt(BigDecimal.ZERO);
        }

        trdProductDeal.setPs(DictCodeList.交易方向_到期.getValue());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setIsCancel(DictCodeListExt1.是否_否.getValue());
        trdProductDeal.setTotleTradeId(dto.getBusiCode());

        trdProductDeal.setCreateUser(LoginUserUtil.getUserName());
        trdProductDeal.setCreateDept(LoginUserUtil.getDeptCode());
        trdProductDeal.setUpdateUser(LoginUserUtil.getUserName());
        trdProductDeal.setCreateTime(platDate);
        trdProductDeal.setUpdateTime(DateUtil.now());
        trdProductDeal.setSecManageAcctId(billingDetail.getSecAcctId());
        trdProductDeal.setPortfolioId(billingDetail.getPortfolioId());

        trdProductDeal.setTgBusiType(dto.getCreditEvent());
        trdProductDeal.setRemark(billingDetail.getRemark());
        result.setTrdProductDeal(trdProductDeal);

        //金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(totleTradeId);
        trdProductDealAdd.setPortfolioId(billingDetail.getPortfolioId());
        trdProductDealAdd.setCreateUser(LoginUserUtil.getUserName());
        trdProductDealAdd.setCreateDept(LoginUserUtil.getDeptCode());
        trdProductDealAdd.setUpdateUser(LoginUserUtil.getUserName());
        trdProductDealAdd.setCreateTime(platDate);
        trdProductDealAdd.setUpdateTime(DateUtil.now());

        result.setTrdProductDealAdd(trdProductDealAdd);

        if (StringUtil.equals(dto.getSettleMethod(), DictCodeListExt4.结算方式_标的_实物结算.getValue())) {
            //生成债券交易
            String totleTradeId2 = generateBusiCode(dto);
            TrdProductDeal trdProductDeal2 = new TrdProductDeal();
            trdProductDeal2.setTradeId(totleTradeId2);
            trdProductDeal2.setBusiType(DictCodeList.共通业务类型_债券卖出.getValue());
            trdProductDeal2.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + "_" + dto.getDeliverBond());
            trdProductDeal2.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            trdProductDeal2.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            trdProductDeal2.setBranch(0L);
            trdProductDeal2.setTradeDate(platDate);
            trdProductDeal2.setVdate(dto.getvDate());
            trdProductDeal2.setMdate(dto.getmDate());
            trdProductDeal2.setSettleDate(nextWorkDate);
            trdProductDeal2.setCcy(DictCodeList.币种_人民币_CNY.getValue());

            trdProductDeal2.setUnitCprice(ArithmeticUtil.divide(billingDetail.getDeliveryAmount(), billingDetail.getDeliverBondAmount(), 4, RoundingMode.HALF_UP));
            trdProductDeal2.setUnitInt(BigDecimal.ZERO);
            trdProductDeal2.setUnitFprice(trdProductDeal2.getUnitCprice());

            trdProductDeal2.setCpriceAmt(billingDetail.getDeliveryAmount());

            trdProductDeal2.setFpriceAmt(billingDetail.getDeliveryAmount());

            trdProductDeal2.setShareAmt(billingDetail.getRepNomCapi());

            trdProductDeal2.setTradeAmt(billingDetail.getDeliveryAmount());

            trdProductDeal2.setPs(DictCodeList.交易方向_卖出.getValue());
            trdProductDeal2.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdProductDeal2.setIsCancel(DictCodeListExt1.是否_否.getValue());
            trdProductDeal2.setTotleTradeId(dto.getBusiCode());


            trdProductDeal2.setCreateUser(LoginUserUtil.getUserName());
            trdProductDeal2.setCreateDept(LoginUserUtil.getDeptCode());
            trdProductDeal2.setUpdateUser(LoginUserUtil.getUserName());
            trdProductDeal2.setCreateTime(platDate);
            trdProductDeal2.setUpdateTime(DateUtil.now());
            trdProductDeal2.setSecManageAcctId(billingDetail.getSecAcctId());
            trdProductDeal2.setPortfolioId(billingDetail.getPortfolioId());

            trdProductDeal2.setTgBusiType(dto.getCreditEvent());
            trdProductDeal2.setRemark(billingDetail.getRemark());
            result.setTrdProductDeal2(trdProductDeal2);

            //金融产品交易附属表
            TrdProductDealAdd trdProductDealAdd2 = new TrdProductDealAdd();
            trdProductDealAdd2.setTradeId(totleTradeId2);
            trdProductDealAdd2.setPortfolioId(billingDetail.getPortfolioId());
            trdProductDealAdd2.setCreateUser(LoginUserUtil.getUserName());
            trdProductDealAdd2.setCreateDept(LoginUserUtil.getDeptCode());
            trdProductDealAdd2.setUpdateUser(LoginUserUtil.getUserName());
            trdProductDealAdd2.setCreateTime(platDate);
            trdProductDealAdd2.setUpdateTime(DateUtil.now());

            result.setTrdProductDealAdd2(trdProductDealAdd2);
        }

        StlPayOrders stlPayOrders = new StlPayOrders();
        stlPayOrders.setOrderId(totleTradeId);
        stlPayOrders.setOrderType("01");
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        stlPayOrders.setMyAcctNo(billingDetail.getAcctNo());
        stlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
        stlPayOrders.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        stlPayOrders.setTradeId(totleTradeId);
        stlPayOrders.setPortfolioId(billingDetail.getPortfolioId());
        stlPayOrders.setSettleDate(nextWorkDate);

        stlPayOrders.setCreateUser(LoginUserUtil.getUserName());
        stlPayOrders.setCreateDept(LoginUserUtil.getDeptCode());
        stlPayOrders.setUpdateUser(LoginUserUtil.getUserName());
        stlPayOrders.setCreateTime(platDate);
        stlPayOrders.setUpdateTime(DateUtil.now());

        result.setStlPayOrders(stlPayOrders);

        if (StringUtil.equals(dto.getOperateType(), BussinessConstant.SUBMIT)) {
            PtlTrade ptlTrade = new PtlTrade();
            ptlTrade.setBusiNo(totleTradeId);
            ptlTrade.setBusiTable("02");
            ptlTrade.setPortfolioId(billingDetail.getPortfolioId());
            ptlTrade.setBusiId(totleTradeId);
            ptlTrade.setBusiType(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            ptlTrade.setSecManageAcctId(billingDetail.getSecManageAcctId());
            ptlTrade.setSettleDate(nextWorkDate);
            ptlTrade.setCreateUser(LoginUserUtil.getUserName());
            ptlTrade.setCreateDept(LoginUserUtil.getDeptCode());
            ptlTrade.setUpdateUser(LoginUserUtil.getUserName());
            ptlTrade.setCreateTime(platDate);
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
        tradeAmt = tradeAmt;
        shareAmt = shareAmt;
        deliverBondAmount = deliverBondAmount;
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

        trdFinProductDealTotal.setmDeliveryType(dto.getSettleMethod());

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

        trdDealTotalAdd.setCreditEventResolveDate(dto.getCreditEventResolveDate());
        trdDealTotalAdd.setCreditEventHappenedDate(dto.getCreditEventHappenedDate());
        trdDealTotalAdd.setDeliverBond(dto.getDeliverBond());
        return trdDealTotalAdd;
    }

    public FinprodLinkDto finprodIdLink(CreditProtectionCertificatePeriod001Request params) {
        //选择资产代码后，联动查询展示：资产名称、起始日、到期日、参考实体
        params.setTradeDate(iPlatformDate.getSysDate());
        FinprodLinkDto finprodLinkDto = creditProtectionCertificatePeriodMapper.findFinprodDetail(params);

        if (null == finprodLinkDto) {
            throw new BusinessException("资产不存在");
        }

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
                String[] refEntityArray = StringUtil.split(finprodLinkDto.getRefEntity(), ",");
                List<String> refSecsList = creditProtectionCertificatePeriodMapper.getRefSecsByRefEntity(refEntityArray);
                refSecsList.forEach(refSecsD -> refSecs.addAll(Arrays.asList(StringUtil.split(refSecsD, ","))));
            }
        } else {
            refSecs.addAll(Arrays.asList(StringUtil.split(finprodLinkDto.getRefSec(), ",")));
        }
        List<SelectOptionDto> refSecsOption = new ArrayList<>();

        if (!CollectionUtils.isEmpty(refSecs)) {
            refSecsOption.addAll(creditProtectionCertificatePeriodMapper.queryOptionsForRefSecs(refSecs));
        }

        finprodLinkDto.setRefSecs(refSecsOption);
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
        //指令现金流水&指令现金
        List<EtrCashPositionFlow> etrCashPositionFlows = new ArrayList<>();

        String calendarId;
        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SHJYS_CALID");
        } else {
            calendarId = CacheUtil.getSysParam("STL_DOWNDATA_DEAL_SZJYS_CALID");
        }
        // 下一个交易日
        Date nextWorkDate = getNextWorkDay(iPlatformDate.getSysDate(), calendarId);

        if (StringUtil.equals(DictCodeListExt4.结算方式_标的_现金结算.getValue(), entity.getSettleMethod())) {
            for (BillingDetailsDto billingDetail : billingDetails) {
                //指令持仓流水
                EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                etrSecPositionFlow.setSeqno(IDGenerator.generate());
                etrSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                etrSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                etrSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                etrSecPositionFlow.setInvAim(null);
                etrSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                etrSecPositionFlow.setCdate(entity.getTradeDate());
                etrSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi() : billingDetail.getRepNomCapi().negate());
                etrSecPositionFlow.setpFinprodId(null);
                etrSecPositionFlow.setTradeId(entity.getBusiCode());
                etrSecPositionFlow.setOffsetId(entity.getBusiCode());
                etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrSecPositionFlow.setUpdateTime(DateUtil.now());
                etrSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                etrSecPositionFlows.add(etrSecPositionFlow);

                //交易持仓流水
                PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
                ptlSecPositionFlow.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                ptlSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                ptlSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                ptlSecPositionFlow.setInvAim(null);
                ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                ptlSecPositionFlow.setCdate(entity.getTradeDate());
                ptlSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi() : billingDetail.getRepNomCapi().negate());
                ptlSecPositionFlow.setpFinprodId(null);
                ptlSecPositionFlow.setTradeId(entity.getBusiCode());
                ptlSecPositionFlow.setOffsetId(entity.getBusiCode());
                ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                ptlSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                ptlSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                ptlSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                ptlSecPositionFlow.setUpdateTime(DateUtil.now());
                ptlSecPositionFlows.add(ptlSecPositionFlow);

                //指令现金流水
                EtrCashPositionFlow etrCashPositionFlow = new EtrCashPositionFlow();
                etrCashPositionFlow.setSeqno(IDGenerator.generate());
                etrCashPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrCashPositionFlow.setAcctId(billingDetail.getSecAcctId());
                etrCashPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                etrCashPositionFlow.setCdate(nextWorkDate);
                etrCashPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                etrCashPositionFlow.setAmount(isRevoke ? billingDetail.getDeliveryAmount().negate() : billingDetail.getDeliveryAmount());
                etrCashPositionFlow.setOrderId(entity.getBusiCode());
                etrCashPositionFlow.setOffsetId(entity.getBusiCode());
                etrCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrCashPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrCashPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrCashPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrCashPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrCashPositionFlow.setUpdateTime(DateUtil.now());
                etrCashPositionFlows.add(etrCashPositionFlow);
            }
        } else {
            for (BillingDetailsDto billingDetail : billingDetails) {
                //指令持仓流水&指令持仓：扣减可用T+0信用保护凭证'名义本金'
                EtrSecPositionFlow etrSecPositionFlow = new EtrSecPositionFlow();
                etrSecPositionFlow.setSeqno(IDGenerator.generate());
                etrSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                etrSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                etrSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                etrSecPositionFlow.setInvAim(null);
                etrSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                etrSecPositionFlow.setCdate(entity.getTradeDate());
                etrSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi() : billingDetail.getRepNomCapi().negate());
                etrSecPositionFlow.setpFinprodId(null);
                etrSecPositionFlow.setTradeId(entity.getBusiCode());
                etrSecPositionFlow.setOffsetId(entity.getBusiCode());
                etrSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrSecPositionFlow.setUpdateTime(DateUtil.now());
                etrSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                etrSecPositionFlows.add(etrSecPositionFlow);

                //指令持仓流水&指令持仓：扣减可用T+0交付债券'债券交付数量'
                EtrSecPositionFlow etrSecPositionFlow2 = new EtrSecPositionFlow();
                BeanUtils.copyProperties(etrSecPositionFlow, etrSecPositionFlow2);
                etrSecPositionFlow2.setSeqno(IDGenerator.generate());
                etrSecPositionFlow2.setAmount(isRevoke ? billingDetail.getDeliverBondAmount() : billingDetail.getDeliverBondAmount().negate());
                etrSecPositionFlows.add(etrSecPositionFlow2);

                //交易持仓流水&交易持仓：扣减可用T+0信用保护凭证'名义本金'
                PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
                ptlSecPositionFlow.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                ptlSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
                ptlSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
                ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
                ptlSecPositionFlow.setInvAim(null);
                ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
                ptlSecPositionFlow.setCdate(entity.getTradeDate());
                ptlSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi() : billingDetail.getRepNomCapi().negate());
                ptlSecPositionFlow.setpFinprodId(null);
                ptlSecPositionFlow.setTradeId(entity.getBusiCode());
                ptlSecPositionFlow.setOffsetId(entity.getBusiCode());
                ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                ptlSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                ptlSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                ptlSecPositionFlow.setUpdateTime(DateUtil.now());
                ptlSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                ptlSecPositionFlows.add(ptlSecPositionFlow);

                //交易持仓流水&交易持仓：扣减可用T+0交付债券'债券交付数量'
                PtlSecPositionFlow ptlSecPositionFlow2 = new PtlSecPositionFlow();
                BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow2);
                ptlSecPositionFlow2.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow2.setAmount(isRevoke ? billingDetail.getDeliverBondAmount() : billingDetail.getDeliverBondAmount().negate());
                ptlSecPositionFlows.add(ptlSecPositionFlow2);

                //指令现金流水&指令现金：增加预增T+1交付金额
                EtrCashPositionFlow etrCashPositionFlow = new EtrCashPositionFlow();
                etrCashPositionFlow.setSeqno(IDGenerator.generate());
                etrCashPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
                etrCashPositionFlow.setAcctId(billingDetail.getSecAcctId());
                etrCashPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                etrCashPositionFlow.setCdate(nextWorkDate);
                etrCashPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                etrCashPositionFlow.setAmount(isRevoke ? billingDetail.getDeliveryAmount().negate() : billingDetail.getDeliveryAmount());
                etrCashPositionFlow.setOrderId(entity.getBusiCode());
                etrCashPositionFlow.setOffsetId(entity.getBusiCode());
                etrCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
                etrCashPositionFlow.setCreateUser(LoginUserUtil.getUserName());
                etrCashPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
                etrCashPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
                etrCashPositionFlow.setCreateTime(iPlatformDate.getSysDate());
                etrCashPositionFlow.setUpdateTime(DateUtil.now());
                etrCashPositionFlows.add(etrCashPositionFlow);
            }
        }
        for (EtrSecPositionFlow etrSecPositionFlow : etrSecPositionFlows) {
            etrSecPositionFlowMapper.insertSelective(etrSecPositionFlow);
        }
        for (PtlSecPositionFlow ptlSecPositionFlow : ptlSecPositionFlows) {
            ptlSecPositionFlowMapper.insertSelective(ptlSecPositionFlow);
        }
        for (EtrCashPositionFlow etrCashPositionFlow : etrCashPositionFlows) {
            etrCashPositionFlowMapper.insertSelective(etrCashPositionFlow);
        }
    }


    /**
     * 老头寸处理 审批提交
     *
     * @param entity
     * @param isRevoke
     */
    private void doOldPositionConfirm(CreditProtectionCertificatePeriodSaveDto entity, boolean isRevoke) {
        //指令现金流水&指令现金
        List<EtrCashPositionFlow> etrCashPositionFlows = new ArrayList<>();
        //交易现金流水&交易现金
        List<PtlCashPositionFlow> ptlCashPositionFlows = new ArrayList<>();
        //交易持仓流水&交易持仓
        List<PtlSecPositionFlow> ptlSecPositionFlows = new ArrayList<>();

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
            //交易持仓流水&交易持仓：增加可用T+1信用保护凭证'名义本金'
            PtlSecPositionFlow ptlSecPositionFlow = new PtlSecPositionFlow();
            ptlSecPositionFlow.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
            ptlSecPositionFlow.setSecAcctId(billingDetail.getSecAcctId());
            ptlSecPositionFlow.setFinprodId(entity.getFinprodMarketId());
            ptlSecPositionFlow.setBranch(InterfCommonConstant.分支序号_默认);
            ptlSecPositionFlow.setInvAim(null);
            ptlSecPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
            ptlSecPositionFlow.setCdate(nextWorkDate);
            ptlSecPositionFlow.setAmount(isRevoke ? billingDetail.getRepNomCapi().negate() : billingDetail.getRepNomCapi());
            ptlSecPositionFlow.setpFinprodId(null);
            ptlSecPositionFlow.setTradeId(entity.getBusiCode());
            ptlSecPositionFlow.setOffsetId(entity.getBusiCode());
            ptlSecPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            ptlSecPositionFlow.setCreateUser(LoginUserUtil.getUserName());
            ptlSecPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
            ptlSecPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
            ptlSecPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            ptlSecPositionFlow.setUpdateTime(DateUtil.now());
            ptlSecPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlSecPositionFlows.add(ptlSecPositionFlow);

            //交易持仓流水&交易持仓：扣减实际T+1信用保护凭证'名义本金'
            PtlSecPositionFlow ptlSecPositionFlow2 = new PtlSecPositionFlow();
            BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow2);
            ptlSecPositionFlow2.setSeqno(IDGenerator.generate());
            ptlSecPositionFlow2.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlSecPositionFlow2.setAmount(isRevoke ? billingDetail.getRepNomCapi() : billingDetail.getRepNomCapi().negate());
            ptlSecPositionFlows.add(ptlSecPositionFlow2);

            if (StringUtil.equals(entity.getSettleMethod(), DictCodeListExt4.结算方式_标的_实物结算.getValue())) {
                //交易持仓流水&交易持仓：增加可用T+1交付债券'债券交付数量'
                PtlSecPositionFlow ptlSecPositionFlow3 = new PtlSecPositionFlow();
                BeanUtils.copyProperties(ptlSecPositionFlow, ptlSecPositionFlow3);
                ptlSecPositionFlow3.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow3.setAmount(isRevoke ? billingDetail.getDeliverBondAmount().negate() : billingDetail.getDeliverBondAmount());
                ptlSecPositionFlows.add(ptlSecPositionFlow3);
                //交易持仓流水&交易持仓：扣减实际T+1交付债券'债券交付数量'
                PtlSecPositionFlow ptlSecPositionFlow4 = new PtlSecPositionFlow();
                BeanUtils.copyProperties(ptlSecPositionFlow2, ptlSecPositionFlow4);
                ptlSecPositionFlow4.setSeqno(IDGenerator.generate());
                ptlSecPositionFlow4.setAmount(isRevoke ? billingDetail.getDeliverBondAmount() : billingDetail.getDeliverBondAmount().negate());
                ptlSecPositionFlows.add(ptlSecPositionFlow4);
            }

            //指令现金流水&指令现金：扣减预增T+1交付金额
            EtrCashPositionFlow etrCashPositionFlow = new EtrCashPositionFlow();
            etrCashPositionFlow.setSeqno(IDGenerator.generate());
            etrCashPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
            etrCashPositionFlow.setAcctId(billingDetail.getSecAcctId());
            etrCashPositionFlow.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
            etrCashPositionFlow.setCdate(nextWorkDate);
            etrCashPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            etrCashPositionFlow.setAmount(isRevoke ? billingDetail.getDeliveryAmount() : billingDetail.getDeliveryAmount().negate());
            etrCashPositionFlow.setOrderId(entity.getBusiCode());
            etrCashPositionFlow.setOffsetId(entity.getBusiCode());
            etrCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            etrCashPositionFlow.setCreateUser(LoginUserUtil.getUserName());
            etrCashPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
            etrCashPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
            etrCashPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            etrCashPositionFlow.setUpdateTime(DateUtil.now());
            etrCashPositionFlows.add(etrCashPositionFlow);
            //指令现金流水&指令现金：增加可用T+1交付金额
            EtrCashPositionFlow etrCashPositionFlow2 = new EtrCashPositionFlow();
            BeanUtils.copyProperties(etrCashPositionFlow, etrCashPositionFlow2);
            etrCashPositionFlow2.setSeqno(IDGenerator.generate());
            etrCashPositionFlow2.setHodingType(DictCodeList.持仓类型_可用.getValue());
            etrCashPositionFlow2.setAmount(isRevoke ? billingDetail.getDeliveryAmount().negate() : billingDetail.getDeliveryAmount());
            etrCashPositionFlows.add(etrCashPositionFlow2);
            //交易现金流水&交易现金：增加实际T+1交付金额
            PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
            ptlCashPositionFlow.setSeqno(IDGenerator.generate());
            ptlCashPositionFlow.setPortfolioId(billingDetail.getPortfolioId());
            ptlCashPositionFlow.setAcctId(billingDetail.getSecAcctId());
            ptlCashPositionFlow.setHodingType(DictCodeList.持仓类型_实际.getValue());
            ptlCashPositionFlow.setCdate(nextWorkDate);
            ptlCashPositionFlow.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            ptlCashPositionFlow.setAmount(isRevoke ? billingDetail.getDeliveryAmount().negate() : billingDetail.getDeliveryAmount());
            ptlCashPositionFlow.setOrderId(entity.getBusiCode());
            ptlCashPositionFlow.setOffsetId(entity.getBusiCode());
            ptlCashPositionFlow.setEntrustId(entity.getBusiCode());
            ptlCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
            ptlCashPositionFlow.setCreateUser(LoginUserUtil.getUserName());
            ptlCashPositionFlow.setCreateDept(LoginUserUtil.getDeptCode());
            ptlCashPositionFlow.setUpdateUser(LoginUserUtil.getUserName());
            ptlCashPositionFlow.setCreateTime(iPlatformDate.getSysDate());
            ptlCashPositionFlow.setUpdateTime(DateUtil.now());
            ptlCashPositionFlows.add(ptlCashPositionFlow);
        }
        for (EtrCashPositionFlow etrCashPositionFlow : etrCashPositionFlows) {
            etrCashPositionFlowMapper.insertSelective(etrCashPositionFlow);
        }
        for (PtlCashPositionFlow ptlCashPositionFlow : ptlCashPositionFlows) {
            ptlCashPositionFlowMapper.insertSelective(ptlCashPositionFlow);
        }
        for (PtlSecPositionFlow ptlSecPositionFlow : ptlSecPositionFlows) {
            ptlSecPositionFlowMapper.insertSelective(ptlSecPositionFlow);
        }
    }
}
