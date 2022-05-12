/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service;

import com.joyintech.background.component.order.exchange.business.dto.EtrSecRepoRepoDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustBackupMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustDetailMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.position.service.PositionRequestServiceEtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dao.EtrExchangeTradeSecRepoMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSecRepoSaveRepoRequest;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

/**
 * 交易所指令交易（现券+通用质押式回购）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeRepo001Service
 * @since 1.0
 */
@Service("etrExchangeTradeRepo001Service")
public class EtrExchangeTradeRepo001Service extends CommonExportService<EtrExchangeTradeSecRepoSaveRepoRequest> implements BaseProcessService<EtrExchangeTradeSecRepoSaveRepoRequest> {

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
    EtrExchangeTradeSecRepoMapper etrExchangeTradeSecRepoMapper;
    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    /**
     * 投资委托基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 指令分发接口
     */
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;
    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    @Autowired
    private PositionRequestServiceEtrExchangeTradeSaveSecRepoRequest positionRequestService;

    public static final String ENTRUST_RECORD_MODIFY = "modify";

    public static final String ENTRUST_RECORD_NEW = "new";

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;

    @Autowired
    private EtrExchangeTradeSecRepo001Service etrExchangeTradeSecRepo001Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrExchangeTradeSecRepoSaveRepoRequest dto, OperationType arg1) {
        // TODO Auto-generated method stub
        calcAmt(dto);
        Map<String, Object> map = new HashMap<>();
        // 新增交易对手席位
        if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getSeatId())) {
            map.put("counterId", dto.getCounterId());
            map.put("counterSeatId", dto.getSeatId());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
                    Object.class);
        }
        // 新增交易对手交易员
        if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getCounterTrader())) {
            map.put("counterId", dto.getCounterId());
            map.put("counterTraderId", dto.getCounterTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
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
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        }

        //设置清算速度 硬编码
        if (StringUtil.isNotEmpty(dto.getSettleSpeed())) {
            if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_0.getValue())) {
                dto.setSettleSpeed("0");
            } else if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_1.getValue())) {
                dto.setSettleSpeed("1");
            }
        }

        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 start
        String isPushMsg = DictCodeList.是否_否.getValue();
        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 end

        // 构造投资指令DTO
        EtrBaseDto baseDto = etrExchangeTradeSecRepo001Service.getEtrBaseDto(dto);

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = etrExchangeTradeSecRepo001Service.getEtrSinglePortfolio(dto);

        //待分发逻辑处理
        BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
        BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);

        etrEntrust.setBusinessModel(dto.getBusinessModel());
        //根据投资目的与业务模式的对应关系入库时赋值
        if (StringUtil.equals("AC", dto.getBusinessModel())) {
            dto.setInvAim("AC");
            etrEntrust.setInvAim("AC");
        } else if (StringUtil.equals("FVTPL", dto.getBusinessModel())) {
            dto.setInvAim("FVTPL");
            etrEntrust.setInvAim("FVTPL");
        }
        // 自动分发
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        if (busiAutoEtrDispService != null) {
            // 检测是否设置交易员
            if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                // 跳出自动分发
                etrEntrust.setEntrustType(etrSinglePortfolioDto.getEntrustType());
                entity.setEtrDistributeList(etrDistributeList);
                isPushMsg = DictCodeList.是否_是.getValue();
            } else {
                // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                // 设置指令日期 add by yufei 20200808 start
                if (etrEntrust.getEntrustDate() == null) {
                    etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
                }
                // 设置指令日期 add by yufei 20200808 end
                if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                    // 自动分发
                    etrEntrust.setEntrustType(etrSinglePortfolioDto.getEntrustType());
                    entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
            }
        }
        if (StringUtil.isEmptyList(entity.getEtrDistributeList())) {//无分发人员
            baseDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
            etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        }

        // 通用式质押回购指令
        EtrSecRepoRepoDto etrSecRepoRepoDto = new EtrSecRepoRepoDto();
        if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
            etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(), new BigDecimal(100), 6));
            BeanUtil.copyPropertiesIgnoreNull(etrSecRepoRepoDto, etrSinglePortfolioDto);
            etrSecRepoRepoDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrSecRepoRepoDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
            etrSecRepoRepoDto.setBusiTypePosition(DictCodeListExt3.投管业务类型_通用式质押回购.getValue());
            etrSecRepoRepoDto.setDealMode(DictCodeListExt3.指定交易方式_匹配成交.getValue());
        }

        EtrParameterDto paraDto = new EtrParameterDto();
        etrExchangeTradeSecRepo001Service.setUser(paraDto);

        if (null != dto.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(dto.getEntrustId())) {
            dto.setOptType(ENTRUST_RECORD_MODIFY);
        } else {
            dto.setOptType(ENTRUST_RECORD_NEW);
        }
        if (StringUtil.equals(dto.getOptType(), ENTRUST_RECORD_NEW)) {//
            if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrSecRepoRepoDto.class).updateSettings(paraDto);
                //质押式回购 设置 AssetType1->平安资产类型_质押式回购
                etrSecRepoRepoDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
                EtrComponentFactory.getComponentService(EtrSecRepoRepoDto.class).entrustNew(etrSecRepoRepoDto);
            }
        } else {
            if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrSecRepoRepoDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrSecRepoRepoDto.class).entrustEdit(etrSecRepoRepoDto);
            }
        }
        etrExchangeTradeSecRepo001Service.doDeleteFlow(dto.getWorkflowId());
        //插入指令明细表
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = etrExchangeTradeSecRepo001Service.buildMultiEtrFlow(dto);
        if (!StringUtil.isEmptyList(baseEtrInvestInfoDto.getEtrEntrustDetailList())) {
            etrEntrustDetailMapper.deleteByEntrustId(baseEtrInvestInfoDto.getEtrEntrustDetailList().get(0).getEntrustId());
            for (EtrEntrustDetail etrEntrustDetail : baseEtrInvestInfoDto.getEtrEntrustDetailList()) {
                etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
            }
        }
        // 自动分发数据入库 add by yufei 20200808 start
        if (!StringUtil.isEmptyList(entity.getEtrDistributeList())) {
            baseEtrInvestInfoService.distributeDelegate(entity.getEtrDistributeList());
        }
        // 自动分发数据入库 add by yufei 20200808 end
        // add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
        //处理履历
        etrExchangeTradeSecRepo001Service.updateCfetsOprHis(dto.getEntrustId(), dto.getOptType());
    }

    @Override
    public void doBack(EtrExchangeTradeSecRepoSaveRepoRequest entity) {
        etrExchangeTradeSecRepo001Service.doDeleteEtrFlowMethod(entity.getEntrustId());
        //从废弃表还原
        etrExchangeTradeSecRepo001Service.backReRoll(entity.getEntrustId());
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
        AllTradeCommonUtil.deletePosition(null, entity.getEntrustId());
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
    }

    @Override
    public void doUpdate(EtrExchangeTradeSecRepoSaveRepoRequest entity) {

        if(StringUtil.isEmpty(entity.getPortfolioId()) && !CollectionUtils.isEmpty(entity.getPortfolioList())){
            entity.setPortfolioId(entity.getPortfolioList().get(0).getPortfolioId());
        }

        // nothing to do just for aspect
        Map<String, Object> map = new HashMap<>();
        calcAmt(entity);
        // 新增交易对手席位
        if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getSeatId())) {
            map.put("counterId", entity.getCounterId());
            map.put("counterSeatId", entity.getSeatId());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
                    Object.class);
        }
        // 新增交易对手交易员
        if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getCounterTrader())) {
            map.put("counterId", entity.getCounterId());
            map.put("counterTraderId", entity.getCounterTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            //退回的移到备份表
            etrExchangeTradeSecRepo001Service.doInsertBackupQuery(entity);
            // 先删除流程中数据
            etrExchangeTradeSecRepo001Service.doDeleteFlow(entity.getWorkflowId());
            // 重新insert
            etrExchangeTradeSecRepo001Service.doInsertFlow(entity);
            //add by zznn 20210824 start
            //保存不需要处理头寸
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
                etrExchangeTradeSecRepo001Service.doInsetOrUpdateForEtrPositionHoldMulti(entity);
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
            }
            //add by zznn 20210824 start
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrExchangeTradeSecRepoSaveRepoRequest.class, true);
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
                if (StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
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

    public void calcAmt(EtrExchangeTradeSecRepoSaveRepoRequest entity) {
        entity.setPrice(entity.getRepoRate());
        entity.setEntrustAmt(ArithmeticUtil.multiply(entity.getRepoAmt(),new BigDecimal(10000)));
        entity.setEntrustNum(ArithmeticUtil.divide(entity.getEntrustAmt(),new BigDecimal(100),2, RoundingMode.HALF_UP));
        for (EtrExchangeTradePortfolioSecRepoRequest item : entity.getPortfolioList()) {
            item.setPrice(item.getRepoRate());
            item.setEntrustAmt(ArithmeticUtil.multiply(item.getRepoAmt(),new BigDecimal(10000)));
            item.setEntrustNum(ArithmeticUtil.divide(item.getEntrustAmt(),new BigDecimal(100),2, RoundingMode.HALF_UP));
        }
    }

    /**
     * 流程判断
     */
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrExchangeTradeSecRepoSaveRepoRequest dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        String 平安资产类型 = null;
        String finprodId = dto.getFinprodId();
        平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
        finprodId = null;

        if (taskId != null) {
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
                        平安资产类型, "", dto.getTradeMarket(), dto.getPs()
                        , finprodId, entity.getWorkflowId()
                );
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批_委托用(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        return null;
    }

    @Override
    public void doInsert(EtrExchangeTradeSecRepoSaveRepoRequest entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        calcAmt(entity);
        //退回的移到备份表
        etrExchangeTradeSecRepo001Service.doInsertBackupQuery(entity);

        etrExchangeTradeSecRepo001Service.doInsertFlow(entity);
        //插入和修改时，指令持仓流水 、现金流水处理,支持多组合
        etrExchangeTradeSecRepo001Service.doInsetOrUpdateForEtrPositionHoldMulti(entity);
        //增加调用头寸引擎代码start
        String decisionAct = 系统动作_修改.getValue();
        String systemAct = 系统动作_修改.getValue();
        if (ENTRUST_RECORD_NEW.equals(entity.getOptType())) {
            systemAct = 系统动作_新建.getValue();
            decisionAct = 系统动作_新建.getValue();
        }
        positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct);
        //增加调用头寸引擎代码end
    }

    @Override
    public void doDelete(String workflowId) {
        etrExchangeTradeSecRepo001Service.doDeleteFlow(workflowId);
        //增加调用头寸引擎代码start
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            EtrExchangeTradeSecRepoSaveRepoRequest entity = getBussinessEntityByWorkflowId(workflowId);
            positionRequestService.sendDelete(entity);
        }
        //增加调用头寸引擎代码end
    }

    public EtrExchangeTradeSecRepoSaveRepoRequest genEtrExchangeTradeSecRepoSaveRepoRequest(EtrExchangeTradeSecRepoSaveRepoRequest dto, EtrExchangeTradePortfolioSecRepoRequest multiPtl001Dto) {
        EtrExchangeTradeSecRepoSaveRepoRequest etrExchangeTrade001Dto = new EtrExchangeTradeSecRepoSaveRepoRequest();
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

}
