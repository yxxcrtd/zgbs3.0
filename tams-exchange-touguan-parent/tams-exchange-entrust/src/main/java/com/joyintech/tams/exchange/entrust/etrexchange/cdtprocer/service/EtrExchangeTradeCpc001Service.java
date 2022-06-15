package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.EtrBlockTradeStockDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrSecRepoBondDto;
import com.joyintech.background.component.order.exchange.common.constant.UnitEnum;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dao.EtrExchangeTradeCpc002Mapper;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeMiniLimitPriceRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.position.service.PositionRequestServiceTradSaveCpcRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeOperateSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeQuotationHqSecRepoResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepo001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepo002Service;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

/**
 * 交易所指令交易（现券+通用质押式回购）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeSecRepo001Service
 * @since 1.0
 */
@Service("EtrExchangeTradeCpc001Service")
public class EtrExchangeTradeCpc001Service extends CommonExportService<EtrCreditProtectionCertificateApplyRequest>
        implements BaseProcessService<EtrCreditProtectionCertificateApplyRequest> {

    public static String queryIntegratedTick = "/joyin/quote/tick";
    @Autowired
    private EtrComponentCommService<EtrBlockTradeStockDto> componentService;
    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;
    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;
    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;
    @Autowired
    EtrExchangeTradeSecRepo002Service etrExchangeTrade002Service;
    @Autowired
    FinProductAddMapper finProductAddMapper;
    @Autowired
    FinProductMapper finProductMapper;
    @Autowired
    EtrExchangeTradeCpc002Mapper etrExchangeTradeCpc002Mapper;

    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private PositionRequestServiceTradSaveCpcRequest positionRequestService;


    public static final String ENTRUST_RECORD_MODIFY = "modify";
    public static final String ENTRUST_RECORD_NEW = "new";
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private BasRiskMathCommService basRiskMathCommService;

    public String setEntrustBussType(EtrCreditProtectionCertificateApplyRequest dto) {
        //业务类型
        dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue());
        String businessType = DictCodeListExt3.共通业务类型_信用保护凭证指令.getValue();
        return businessType;
    }

    public BussinessCodeParameter genBussinessCodeParameter(EtrCreditProtectionCertificateApplyRequest dto) {
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_信用保护凭证指令));
        return param;
    }
    /**
     * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> getWorkFlowParameter(EtrCreditProtectionCertificateApplyRequest dto) {
        WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> parameter = new WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest>();
        return parameter;
    }

    /**
     * 组合分配实体转换成
     *
     * @param dto
     * @param multiPtl001Dto
     * @return
     */
    public EtrCreditProtectionCertificateApplyRequest genEtrExchangeTradeSaveSecRepoRequest(EtrCreditProtectionCertificateApplyRequest dto, EtrExchangeTradePortfolioSecRepoRequest multiPtl001Dto) {
        EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = new EtrCreditProtectionCertificateApplyRequest();
        BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, dto);
        etrExchangeTrade001Dto.setPortfolioId(multiPtl001Dto.getPortfolioId());
        etrExchangeTrade001Dto.setAcctNo(multiPtl001Dto.getAcctNo());
        etrExchangeTrade001Dto.setSecAcctId(multiPtl001Dto.getSecAcctId());
        etrExchangeTrade001Dto.setEntrustAmt(multiPtl001Dto.getEntrustAmt());
        etrExchangeTrade001Dto.setEntrustNum(multiPtl001Dto.getEntrustNum());
        etrExchangeTrade001Dto.setAccAvailableCash(multiPtl001Dto.getAccAvailableCash());
        etrExchangeTrade001Dto.setAccAvailablePos(multiPtl001Dto.getAccAvailablePos());
        etrExchangeTrade001Dto.setTargetType(dto.getTargetType());
        etrExchangeTrade001Dto.setMdate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setVdate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setStockId(dto.getStockId());
        etrExchangeTrade001Dto.setEntrustDate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setOptType(dto.getOptType());

        return etrExchangeTrade001Dto;
    }

    @Override
    public void doInsert(EtrCreditProtectionCertificateApplyRequest entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        //退回的移到备份表
        doInsertBackupQuery(entity);
        doInsertFlow(entity);
        //插入和修改时，指令持仓流水 、现金流水处理,支持多组合
        doInsetOrUpdateForEtrPositionHoldMulti(entity);
        //增加调用头寸引擎代码start
        String decisionAct = 系统动作_修改.getValue();
        String systemAct = 系统动作_修改.getValue();
        if (ENTRUST_RECORD_NEW.equals(entity.getOptType())) {
            systemAct = 系统动作_新建.getValue();
            decisionAct = 系统动作_新建.getValue();
        }
//        try {
//            entity.setPositionSeqno(null);
//            positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct);
//        } catch (PositionException e) {
//            Message message = PositionUtil.ajaxErrorReturn(e);
//            if (StringUtil.isNotBlank(message.getMessage())) {
//                WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
//            } else {
//                if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
//                    BaseMap bm = (BaseMap) message.getData();
//                    StringBuffer messageStr = (StringBuffer) bm.get("message");
//                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
//                }
//            }
//        }
        positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct );

    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
        //增加调用头寸引擎代码start
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            EtrCreditProtectionCertificateApplyRequest entity = getBussinessEntityByWorkflowId(workflowId);
            positionRequestService.sendDelete(entity);
        }
        //增加调用头寸引擎代码end
    }

    @Override
    public void doUpdate(EtrCreditProtectionCertificateApplyRequest entity) {
        // nothing to do just for aspect
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            //退回的移到备份表
            doInsertBackupQuery(entity);
            // 先删除流程中数据
            doDeleteFlow(entity.getWorkflowId());
            // 重新insert
            doInsertFlow(entity);
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
                doInsetOrUpdateForEtrPositionHoldMulti(entity);
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
            }
            //add by zznn 20210824 start
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrExchangeTradeSaveSecRepoRequest.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //保存不需要处理头寸
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //增加调用头寸引擎代码start
                String decisionAct = 系统动作_修改.getValue();
                String systemAct = 系统动作_修改.getValue();
                EtrEntrust etrEntrust = etrEntrustBackupMapper.selectByPrimaryKey(entity.getEntrustId());
                SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                    if (etrEntrust == null) {
                        systemAct = 系统动作_新建.getValue();
                        decisionAct = 系统动作_修改.getValue();
                    }
                } else {
                    if (etrEntrust == null) {
                        systemAct = 系统动作_新建.getValue();
                        decisionAct = 系统动作_新建.getValue();
                    }
                }
                String confirmOk = RequestUtil.getParam("confirmOk");
                if (StringUtil.equals("1", confirmOk) || StringUtil.equals("1",entity.getConfirmOk())) {
                    entity.setSoftSkipMode("Y");
                }
                try {
                    entity.setPositionSeqno(null);
                    positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct);
                } catch (PositionException e) {
                    Message message = PositionUtil.ajaxErrorReturn(e);
                    if (StringUtil.isNotBlank(message.getMessage())) {
                        WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                    } else {
                        if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                            BaseMap bm = (BaseMap) message.getData();
                            StringBuffer messageStr = (StringBuffer) bm.get("message");
                            WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                        }
                    }
                }
                //增加调用头寸引擎代码end
            }
        }
    }

    @Override
    public void doComplete(EtrCreditProtectionCertificateApplyRequest dto, OperationType arg1) {
        BigDecimal cash = StringUtil.isNotEmpty(dto.getEntrustNum())? dto.getEntrustNum().multiply(new BigDecimal(10000)):BigDecimal.valueOf(0.00);
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        etrEntrust.setTradePlat(DictCodeList.交易平台_其它.getValue());
        entrustCommonDto.setEtrEntrust(etrEntrust);
        entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(dto.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {
                etrEntrustAdd.setAssetType(finProductType.getType8());
            } else {
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
        }

        // 构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        //待分发逻辑处理
        BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
        BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);

        etrEntrust.setBusinessModel(dto.getBusinessModel());
        //根据投资目的与业务模式的对应关系入库时赋值
        dto.setInvAim("FVTPL");
        etrEntrust.setInvAim("FVTPL");
        etrEntrust.setEntrustNum(cash);
        etrEntrust.setTradePlat(DictCodeList.交易平台_其它.getValue());
        baseDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态

        // 债券现券交易指令
        EtrSecRepoBondDto etrSecRepoBondDto = new EtrSecRepoBondDto();
        if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrSecRepoBondDto, etrSinglePortfolioDto);
            etrSecRepoBondDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100), 8)); // 到期收益率
            etrSecRepoBondDto.setFullPrice(dto.getFprice()); // 全价
            etrSecRepoBondDto.setPrice(dto.getPrice());
            etrSecRepoBondDto.setEntrustNum(cash);
            etrSecRepoBondDto.setEntrustAmt(dto.getEntrustAmt());
            etrSecRepoBondDto.setCalMode(dto.getCalculateField());// 计算方式
            etrSecRepoBondDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrSecRepoBondDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());// 资产大类
            etrSecRepoBondDto.setBusiTypePosition(DictCodeListExt3.投管业务类型_信用保护凭证.getValue());
        }

        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);

        if (null != dto.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(dto.getEntrustId())) {
            dto.setOptType(ENTRUST_RECORD_MODIFY);
        } else {
            dto.setOptType(ENTRUST_RECORD_NEW);
        }
        if (StringUtil.equals(dto.getOptType(), ENTRUST_RECORD_NEW)) {//
            if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrSecRepoBondDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrSecRepoBondDto.class).entrustNew(etrSecRepoBondDto);
            }
        }
        doDeleteFlow(dto.getWorkflowId());
        //插入指令明细表
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildMultiEtrFlow(dto);
        if (!StringUtil.isEmptyList(baseEtrInvestInfoDto.getEtrEntrustDetailList())) {
            etrEntrustDetailMapper.deleteByEntrustId(baseEtrInvestInfoDto.getEtrEntrustDetailList().get(0).getEntrustId());
            for (EtrEntrustDetail etrEntrustDetail : baseEtrInvestInfoDto.getEtrEntrustDetailList()) {
                etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
            }
        }
        //处理履历
        updateCfetsOprHis(dto.getEntrustId(), dto.getOptType());

        basRiskMathCommService.insert(dto.getRiskTrialRetDto(), dto.getEntrustId());

    }


    /**
     * 构造投资指令DTO
     *
     * @param dto
     * @return
     */
    public EtrBaseDto getEtrBaseDto(EtrCreditProtectionCertificateApplyRequest dto) {
        // 构造投资指令DTO start
        EtrBaseDto baseDto = new EtrBaseDto();
        baseDto.setMdate(dto.getMdate());// 指令起始日
        baseDto.setVdate(dto.getVdate());// 指令结束日
        baseDto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());// 指令状态
        // 指令接收日期 NULL
        // 指令关闭日期 NULL
        baseDto.setInvManagerId(dto.getInvManagerId());// 登录用户.用户ID
        baseDto.setCreateUser(dto.getCreateUser());// 创建用户
        baseDto.setCreateDept(dto.getCreateDept());// 创建部门
        baseDto.setCreateTime(dto.getCreateTime());// 创建时间
        baseDto.setUpdateUser(LoginUserUtil.getUserName());// 更新用户
        baseDto.setUpdateTime(new Date());// 更新时间
        if (StringUtil.equals(dto.getIsMulti(), DictCodeList.是否_是.getValue())) {
            baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        } else {
            baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        }
        baseDto.setEntrustType(dto.getEntrustBussType());// 指令业务类型
        baseDto.setEntrustId(dto.getEntrustId());// 指令编号
        if (iPlatformDate != null) {
            baseDto.setEntrustDate(iPlatformDate.getSysDate());// 委托日期
        }
        baseDto.setTargetType(dto.getTargetType());// 目标类型
        // 构造投资指令DTO end
        return baseDto;
    }

    /**
     * 构造单组合单资产DTO
     *
     * @param dto
     * @return
     */
    public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrCreditProtectionCertificateApplyRequest dto) {
        //构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        // 构造单组合单资产DTO start
        EtrSinglePortfolioDto etrSinglePortfolioDto = new EtrSinglePortfolioDto();
        BeanUtil.copyPropertiesIgnoreNull(etrSinglePortfolioDto, baseDto);
        etrSinglePortfolioDto.setPortfolioId(dto.getPortfolioId());// 组合代码
        etrSinglePortfolioDto.setStockId(dto.getStockId());// 证券代码
        etrSinglePortfolioDto.setFinprodId(dto.getFinprodId());// 证券代码对应的金融产品代码
        etrSinglePortfolioDto.setSecAcctId(dto.getSecAcctId());// 证券账户
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        etrSinglePortfolioDto.setPs(dto.getPs());// 委托方向
        etrSinglePortfolioDto.setPriceModel(dto.getPriceModel());// 价格模式
        etrSinglePortfolioDto.setPrice(dto.getEntrustPrice() != null ? dto.getEntrustPrice() : dto.getPrice());// 指令价格
        etrSinglePortfolioDto.setEntrustNum(dto.getEntrustNum());// 指令数量
        etrSinglePortfolioDto.setEntrustAmt(dto.getEntrustAmt());// 指令金额
        etrSinglePortfolioDto.setInvAim(dto.getInvAim());// 投资目的
        etrSinglePortfolioDto.setTradeMarket(dto.getTradeMarket());// 交易场所
        etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_其它.getValue());// 交易平台
        etrSinglePortfolioDto.setBusinessModel(dto.getBusinessModel());
        /***********************************************************************/
        etrSinglePortfolioDto.setFinprodType2(dto.getFinprodType2());// 金融产品分类
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        /***********************************************************************/
        etrSinglePortfolioDto.setDealMode(dto.getDealMode());//交易方式
        etrSinglePortfolioDto.setPriceType(dto.getPriceType());//计价方式

        // modify by yangxunpeng ZGCPCN-1341
        /**
        if(DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())){
            etrSinglePortfolioDto.setPrice(dto.getRepoRate());
            etrSinglePortfolioDto.setEntrustAmt(ArithmeticUtil.multiply(dto.getRepoAmt(),new BigDecimal(10000)));
            etrSinglePortfolioDto.setEntrustNum(ArithmeticUtil.divide(etrSinglePortfolioDto.getEntrustAmt(),new BigDecimal(100),2, RoundingMode.HALF_UP));
        }*/

        // 构造单组合单资产DTO end
        return etrSinglePortfolioDto;
    }
    /**
     * 配置用户
     */
    public void setUser(EtrParameterDto paraDto) {
        com.joyintech.background.component.common.pack.common.security.User user = new User();
        user.setUserName(LoginUserUtil.getUserName());
        user.setDeptCode(LoginUserUtil.getDeptCode());
        paraDto.setUser(user);
    }

    public void doInsertFlow(EtrCreditProtectionCertificateApplyRequest dto) {
        BaseEtrInvestInfoDto entity = buildMultiEtrFlow(dto);
        dto.setAssetType2(entity.getEtrEntrustAdd().getAssetType1());
        // 设置指令创建来源
        entity.getEtrEntrustAdd().setEntrustCreateSource(DictCodeListExt3.指令创建来源_场内实时.getValue());
        entity.getEtrEntrustAdd().setBusiTypePosition(DictCodeListExt3.投管业务类型_信用保护凭证.getValue());
        entity.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
        entity.getEtrEntrust().setTradePlat(DictCodeList.交易平台_其它.getValue());
        entity.setWorkFlowId(dto.getWorkflowId());
        //如果目标为空视之为按照数量
        if(StringUtil.isEmpty(entity.getEtrEntrustAdd().getTargetType())){
            entity.getEtrEntrustAdd().setTargetType("01");
        }
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                Boolean.class);
    }

    public void doDeleteFlow(String workflowId) {
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", workflowId);
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
                Boolean.class);
    }

    /**
     * 查询证券代码
     *
     * @return
     */
    public Page<SelectOptionDto> getFinprodMarketInfo(Map<String, Object> map) {
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfoCds", map,
                SelectOptionDto.class);
    }

    /**
     * 插入和修改时，指令持仓流水 、现金流水处理,支持多组合
     * add by zhangpei
     * 20201128
     *
     * @param dto
     */
    public void doInsetOrUpdateForEtrPositionHoldMulti(EtrCreditProtectionCertificateApplyRequest dto) {
        if (StringUtil.equals(dto.getIsMulti(), DictCodeList.是否_是.getValue())) {
            List<EtrCreditProtectionCertificateApplyRequest> EtrExchangeTrade001DtoList = new ArrayList<>();
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = this.genEtrExchangeTradeSaveSecRepoRequest(dto, item);
                doInsetOrUpdateForEtrPositionHold(etrExchangeTrade001Dto);
            }
        } else {
            doInsetOrUpdateForEtrPositionHold(dto);
        }

    }
    /**
     * 生成流水号获取
     *
     * @return
     */
    public static String getSeqNo() {
        return IDGenerator.generate();
    }

    /**
     * 插入和修改时，指令持仓流水 、现金流水处理
     * add by zhangpei
     * 20201128
     *
     * @param dto
     */
    public void doInsetOrUpdateForEtrPositionHold(EtrCreditProtectionCertificateApplyRequest dto) {
        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        // 指令持仓流水 、现金流水处理
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();// 组合持仓流水、组合现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();// 现金流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
        EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合现金流水DTO
        if (DictCodeList.交易方向_卖出.getValue().equals(etrSinglePortfolioDto.getPs())) {
            /********************** 持仓流水DTO构造 *************************************/
            etrSecPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrSecPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(etrSinglePortfolioDto.getSecAcctId());// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId(etrSinglePortfolioDto.getFinprodId());// 金融产品代码
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(etrSinglePortfolioDto.getInvAim()) ? etrSinglePortfolioDto.getInvAim() : "999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustNum().multiply(new BigDecimal(-1)));// 数量
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(etrSinglePortfolioDto.getEntrustId());// 指令编号
            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
            /*************************************************************************/
        } else if (DictCodeList.交易方向_买入.getValue().equals(etrSinglePortfolioDto.getPs()) || DictCodeList.交易方向_申购.getValue().equals(etrSinglePortfolioDto.getPs())) {
            /********************** 持仓流水DTO构造 *************************************/
            etrCashPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrCashPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrCashPositionFlowDtoUnit.setAcctId(etrSinglePortfolioDto.getDealCurAcct());// 资金账户
            etrCashPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrCashPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
            if (null != dto.getFprice()) {
                etrCashPositionFlowDtoUnit.setAmount(dto.getEntrustNum().multiply(dto.getPrice()).multiply(new BigDecimal(-1)));
            } else {
                if (StringUtil.isNotEmpty(etrSinglePortfolioDto.getEntrustAmt())) {
                    etrCashPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 数量
                }
            }
            etrCashPositionFlowDtoUnit.setOrderId(etrSinglePortfolioDto.getEntrustId());// 业务编号
            etrCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
            etrCashPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());// 冲正标识
            etrCashPositionFlowDtoUnit.setOffsetId(etrSinglePortfolioDto.getEntrustId());// 冲销业务编号
            /*************************************************************************/
            etrCashPositionFlows.add(etrCashPositionFlowDtoUnit);
        }
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setDealEtr(true);

        // modify by etliu 20220308 start
        etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> {
            etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache());
            etrCashPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
        });
        etrPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> {
            etrSecPositionFlowDto.setCdate(DateUtil.platDateFromCache());
            etrSecPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
        });
        // modify by etliu 20220308 end

        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
    }

    public BaseEtrInvestInfoDto buildMultiEtrFlow(EtrCreditProtectionCertificateApplyRequest dto) {

        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildEtrFlow(dto);

        EtrEntrust etrEntrust = baseEtrInvestInfoDto.getEtrEntrust();
        if (!CollectionUtils.isEmpty(dto.getPortfolioList())) {
            dto.setSecAcctId(dto.getPortfolioList().get(0).getSecAcctId());
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
                etrEntrustDetail.setPortfolioId(item.getPortfolioId());
                etrEntrustDetail.setEntrustId(etrEntrust.getEntrustId());
                etrEntrustDetail.setEntrustAmt(item.getEntrustAmt());
                BigDecimal cash = StringUtil.isNotEmpty(item.getEntrustNum())? item.getEntrustNum().multiply(new BigDecimal(10000)):BigDecimal.valueOf(0.00);
                etrEntrustDetail.setEntrustNum(cash);
                etrEntrustDetail.setDisRate(item.getDisRate());
                etrEntrustDetail.setTargetType(dto.getTargetType());
                etrEntrustDetailList.add(etrEntrustDetail);
            }
            baseEtrInvestInfoDto.setEtrEntrustDetailList(etrEntrustDetailList);
        }
        return baseEtrInvestInfoDto;
    }

    public BaseEtrInvestInfoDto buildEtrFlow(EtrCreditProtectionCertificateApplyRequest dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(dto.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {
                etrEntrustAdd.setAssetType(finProductType.getType8());
            } else {
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());
        }
        if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(baseDto.getEntrustType())) {
            EtrSecRepoBondDto etrSecRepoBondDto = new EtrSecRepoBondDto();
            BeanUtil.copyPropertiesIgnoreNull(etrSecRepoBondDto, etrSinglePortfolioDto);
            etrSecRepoBondDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100), 8)); // 到期收益率
            etrSecRepoBondDto.setFullPrice(dto.getFprice()); // 全价
            etrSecRepoBondDto.setPrice(dto.getPrice());
            BigDecimal cash = StringUtil.isNotEmpty(dto.getEntrustNum())? dto.getEntrustNum().multiply(new BigDecimal(10000)):BigDecimal.valueOf(0.00);
            etrSecRepoBondDto.setEntrustAmt(dto.getEntrustAmt());
            etrSecRepoBondDto.setEntrustNum(cash);
            etrSecRepoBondDto.setCalMode(dto.getCalculateField());// 计算方式
            etrSecRepoBondDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrSecRepoBondDto.setAssetType(DictCodeList.平安资产类型_信用保护凭证.getValue());// 资产大类
            etrSecRepoBondDto.setBusiTypePosition(DictCodeListExt3.投管业务类型_信用保护凭证.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrSecRepoBondDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrSecRepoBondDto);
        }
        entity.setEtrEntrust(etrEntrust);
        entity.setEtrEntrustAdd(etrEntrustAdd);
        return entity;
    }

    /**
     * 处理履历
     */
    public void updateCfetsOprHis(String entrustId, String optType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        /*if (ENTRUST_RECORD_NEW.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        } else*/ if (ENTRUST_RECORD_NEW.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        } else if ("close".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
        } else if ("revoke".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        } else if ("export".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_导出.getValue());
        }
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    public void doInsertBackupQuery(EtrCreditProtectionCertificateApplyRequest dto) {
        BaseEtrInvestInfoDto entity = buildMultiEtrFlow(dto);
        WFlowCommonService.inserBackuptWithQuery(entity);
    }


    //双击修改check
    public AjaxResult dbClickCheck(EtrCreditProtectionCertificateApplyRequest dto) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //双击修改check
        if (!StringUtil.equals(dto.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue()) && !StringUtil.equals(dto.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())) {
            String entrustStatusStr = SysDataUtil.getNameFromDataDict(DictCode.委托状态, dto.getEntrustStatus());
            errorList.add(new ValidMessage(entrustStatusStr + "的指令不允许修改!"));
        }else{
            //check流程中是否有数据
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setBusinessDataPk1(dto.getEntrustId());
            if (WorkFlowUtil.isNotFromToDoList() && mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errorList.add(new ValidMessage("所选对象存在流程中的数据不能修改"));
            }
        }
        if (!CollectionUtils.isEmpty(errorList)) {
            webResult.setErrorList(errorList);
        } else {
            return null;
        }
        return webResult;
    }


    /**
     * 判断指令是否分发
     *
     * @param entrustId 指令编码
     * @return 分发结果
     */
    public EtrDistribute getDistributeByEntrustId(String entrustId) {
        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", entrustId);
        return etrExchangeTradeCpc002Mapper.getDistributeByEntrustId(map);
    }

    /**
     * 保存check
     *
     * @param entity
     */
    public AjaxResult checkSave(EtrCreditProtectionCertificateApplyRequest entity, BindingResult result) {
        try {
            EtrParameterDto paraDto = new EtrParameterDto();
            setUser(paraDto);
            if (null != entity.getEntrustPrice()) {
                entity.setPrice(entity.getEntrustPrice());// 指令价格 或者净价
            }
            // 构造单组合单资产DTO
            EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
            //设置交易买卖默认参数
            setCheckParams(entity, paraDto);
            if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                // 债券竞价指令
                EtrSecRepoBondDto etrSecRepoBondDto = new EtrSecRepoBondDto();
                BeanUtil.copyPropertiesIgnoreNull(etrSecRepoBondDto, etrSinglePortfolioDto);
                etrSecRepoBondDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(), new BigDecimal(100), 8)); // 到期收益率
                etrSecRepoBondDto.setFullPrice(entity.getFprice()); // 全价
                etrSecRepoBondDto.setPrice(entity.getPrice());
                etrSecRepoBondDto.setEntrustAmt(entity.getEntrustAmt());
                etrSecRepoBondDto.setEntrustNum(entity.getEntrustNum());
                etrSecRepoBondDto.setCalMode(entity.getCalculateField());// 计算方式
                etrSecRepoBondDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
                etrSecRepoBondDto.setAssetType(DictCodeList.信用保护凭证类型_信用保护凭证.getValue());// 资产大类
                EtrComponentFactory.getComponentService(EtrSecRepoBondDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrSecRepoBondDto.class).entrustCheck(etrSecRepoBondDto, result);
            }
            AjaxResult ajax = new AjaxResult(result);
            Field[] fields = ReflectUtil.getDeclaredFields(ajax);
            for (Field field : fields) {
                if (StringUtil.equals(field.getName(), "messageType")) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, ajax, MessageType.错误消息.getCode());
                    break;
                }
            }
            return ajax;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCheckParams(EtrCreditProtectionCertificateApplyRequest entity, EtrParameterDto paraDto) {
        /********************************/
        //设置默认最小价差、最小交易单位；否则指令组件抛异常；
        paraDto.setMinDiffPrice(BigDecimal.valueOf(50).setScale(3, BigDecimal.ROUND_HALF_UP));
        paraDto.setMinDealUnit(new BigDecimal(1));

        Map<String, UnitEnum> numUnit = new HashMap<String, UnitEnum>();
        numUnit.put(entity.getTradeMarket(), UnitEnum.万元);
        paraDto.setNumUnit(numUnit);

    }
    /**
     * @param map
     * @return 获取最小价差
     */
    @SuppressWarnings("unchecked")
    public List<MinPirceDto> getInterfSysMinPriceAll(Map<String, Object> map) {
        List<MinPirceDto> minPirceList = new ArrayList<MinPirceDto>();
        Map<String, Object> res = restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinPrice", map, Map.class);
        if (res != null && res.get("data") != null) {
            List<MinPirceDto> list = (List<MinPirceDto>) res.get("data");
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    minPirceList.add(JSON.parseObject(JSON.toJSONString(list.get(i)), MinPirceDto.class));
                }
            }
        }
        return minPirceList;
    }

    /**
     * 有效价格范围check
     * @param entity
     * @param result
     * @return
     */
    public List<ValidMessage> checkSavePriceRange(EtrCreditProtectionCertificateApplyRequest entity, BindingResult result) {
        try {
            // 当前系统时间
            String nowDate = DateUtil.getDateTimestamp();
            // 业务品种
            String finprodType = null;
            // 标的信息
            FinProduct finproduct = new FinProduct();
            // 检查价格
            BigDecimal checkPrice = entity.getPrice();
            // 行情信息
            EtrExchangeTradeQuotationHqSecRepoResponse quotation = findQuotations(entity.getStockId());
            if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(entity.getEntrustBussType())) {
                // 计价方式  全价时,使用全价校验
                if(StringUtil.equals(entity.getPriceType(),DictCodeListExt3.计价方式_全价.getValue())){
                    checkPrice = entity.getFprice();
                }
                finproduct.setFinprodId(entity.getFinprodId());
                finprodType = DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue();
                finproduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
            }
            //
            List<ValidMessage> validMessageList = checkPriceRangeCommon(finprodType,finproduct,entity,checkPrice,nowDate,quotation);

            return validMessageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询竞价交易行情信息
     **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EtrExchangeTradeQuotationHqSecRepoResponse findQuotations(String secId) {
        String param = "?stock_code=" + secId;
        Map<String, Object> quoteList;
        Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick + param, Map.class);
        if (quoteMap != null && quoteMap.get("data") != null) {
            quoteList = (Map<String, Object>) quoteMap.get("data");
            return BeanUtil.mapToObject(quoteList, EtrExchangeTradeQuotationHqSecRepoResponse.class);
        }
        return new EtrExchangeTradeQuotationHqSecRepoResponse();
    }

    /**
     * 有效价格范围check  共通
     * @param finprodType 业务品种
     * @param finproduct 标的信息
     * @param entity 指令信息
     * @param checkPrice 检查价格
     * @param nowDate 当前时间
     * @param quotation 行情信息
     * @return
     */
    private List<ValidMessage> checkPriceRangeCommon(String finprodType, FinProduct finproduct, EtrCreditProtectionCertificateApplyRequest entity,
                                                     BigDecimal checkPrice, String nowDate, EtrExchangeTradeQuotationHqSecRepoResponse quotation) {
        List<ValidMessage> validMessageList = new ArrayList<>();

        return validMessageList;
    }

    /**
     * 修改/双击查看详情查询信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrCreditProtectionCertificateApplyRequest getEtrExchangeTradeInfo(String entrustId) {
        try {
            EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = new EtrCreditProtectionCertificateApplyRequest();
            //通过指令ID获取委托指令表的DTO,获取委托类型
            EtrEntrust etrEntrust = etrExchangeTradeCpc002Mapper.selectEntrustByKey(entrustId);
            if (StringUtil.isNotEmpty(etrEntrust)) {
                // 债券现券交易指令
                if (DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue().equals(etrEntrust.getEntrustType())) {
                    EtrSecRepoBondDto etrSecRepoBondDto = EtrComponentFactory.getComponentService(EtrSecRepoBondDto.class).entrustSelect(entrustId);
                    if (StringUtil.isNotEmpty(etrSecRepoBondDto)) {
                        BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrSecRepoBondDto);
                        etrExchangeTrade001Dto.setYield(etrSecRepoBondDto.getMatureYield().multiply(new BigDecimal(100))); // 到期收益率
                        etrExchangeTrade001Dto.setFprice(etrSecRepoBondDto.getFullPrice()); // 全价
                        etrExchangeTrade001Dto.setCalculateField(etrSecRepoBondDto.getCalMode());// 计算方式
                        etrExchangeTrade001Dto.setFinprodType2(etrSecRepoBondDto.getFinprodType2());// 金融产品类型（投管）
                    }
                }
                etrExchangeTrade001Dto.setEntrustBussType(etrEntrust.getEntrustType());
                EtrEntrustAdd etrEntrustAdd = etrExchangeTradeCpc002Mapper.selectEntrustAddByKey(etrEntrust.getEntrustId());
                if (null != etrEntrustAdd && StringUtils.isNotEmpty(etrEntrustAdd.getDealCurAcct())) {
                    etrExchangeTrade001Dto.setAcctNo(etrEntrustAdd.getDealCurAcct());
                }
            }
            return etrExchangeTrade001Dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 委托关闭
     *
     * @param entity
     */
    public void closeDelegate(EtrExchangeTradeOperateSecRepoRequest entity) {
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        componentService.entrustClose(etrBaseDto);
        // 处理指令持仓和头寸
        if (baseEtrCashPositionCommService != null
                && DictCodeList.委托状态_已接收.getValue().equals(entity.getEntrustStatus())) {
            baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId(), DictCodeList.是否_是.getValue());
        }
        //处理履历
        updateCfetsOprHis(entity.getEntrustId(), "close");
        positionRequestService.sendClose(entity);
    }

    /**
     * 委托撤销
     *
     * @param entity
     */
    public void recallDelegate(EtrExchangeTradeOperateSecRepoRequest entity) {
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        //关闭委托
        componentService.entrustRevoke(etrBaseDto);
        //处理指令持仓和头寸(仅有未被接收的指令才可以撤会,所以一般只可能存在下指令时生成的持仓和流水)
        if (baseEtrCashPositionCommService != null && (DictCodeList.委托状态_待接收.getValue().equals(entity.getEntrustStatus())
                || DictCodeListExt3.委托状态_待分发.getValue().equals(entity.getEntrustStatus()))) {
            baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId(), DictCodeList.是否_是.getValue());
        }
        //处理履历
        updateCfetsOprHis(entity.getEntrustId(), "revoke");
    }


    public MinTrdUnitLimitDto getMinTrdUnitLimitPrice(EtrExchangeMiniLimitPriceRequest request) {
        MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(request.getFinprodId(), StringUtil.isNotEmpty(request.getTradePlat())?request.getTradePlat():DictCodeListExt3.对手方类型_其他.getValue(), request.getTradeMarket(), request.getFinprodType2(), request.getPs());
        return minTrdUnitLimitDto;
    }

    public MinTrdUnitLimitDto getInterfSysMinTrdUnit(String finprodId, String tradePlat, String tradeMarket,
                                                     String assetType, String ps) {
        MinTrdUnitLimitDto minTrdUnitLimitDto = null;
        String PINANAssetType = DictCodeListExt1.金融产品类型_信用保护凭证.getValue();
        String tradePlats = Objects.isNull(tradePlat)? DictCodeListExt3.对手方类型_其他.getValue() : tradePlat;
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("tradeMarket", tradeMarket);
        map.put("tradePlat", tradePlats);
        map.put("assetType", PINANAssetType);
        map.put("ps", ps);
        Map<String, Object> res = restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinTrdUnitLimit", map, Map.class);
        if (res != null && res.get("data") != null) {
            List<MinTrdUnitLimitDto> list = (List<MinTrdUnitLimitDto>) res.get("data");
            if (list != null && list.size() > 0) {
                minTrdUnitLimitDto = JSON.parseObject(JSON.toJSONString(list.get(0)), MinTrdUnitLimitDto.class);
            }
        }
        return minTrdUnitLimitDto;
    }

    /**
     * 查询最小交易单元
     *
     * @param param
     * @return
     */
    public BigDecimal getMinNumStep(Map<String, Object> param) {
        String ps = !StringUtil.isNotEmpty(param.get("ps")) ? "T01" : param.get("ps").toString();
//        String currentTab = param.get("currentTab") == null ? null : param.get("currentTab").toString();
        String currentTab = StringUtil.isNotEmpty(param.get("currentTab")) ? param.get("currentTab").toString():DictCodeListExt3.对手方类型_其他.getValue() ;
        String finprodId = (String) param.get("finprodId");
        String finprodType2 = (String) param.get("finprodType2");
        String tradeMarket = (String) param.get("tradeMarket");
        BigDecimal minNum = new BigDecimal(1);
        MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(finprodId, currentTab, tradeMarket, finprodType2, ps);
        if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
            minNum = minTrdUnitLimitDto.getMinStep();
        }
        return minNum;
    }

}
