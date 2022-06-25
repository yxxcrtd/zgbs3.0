/*
 * 文件名：EtrIbRepoinfo003Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月29日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.repo.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.cfets.etr.repo.dto.*;
import com.joyintech.tams.common.base.dao.EtrEntrustBackupMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author 王波
 * @date 2021年01月29日
 * @see CfetsEtrIbRepoinfo003Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CfetsEtrIbRepoinfo003Service extends CommonExportService<CfetsEtribRepoInfo003Dto> implements BaseProcessService<CfetsEtribRepoInfo003Dto> {

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private CfetsEtribBondInfo003Service cfetsEtribBondInfo003Service;

    /**
     * 委托指令共同
     */
    @Autowired(required=false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired(required=false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired(required = false)
    private BussRepoSecService bussRepoSecService;

    @Autowired(required = false)
    private CfetsEtrIbBondinfoMapper cfetsEtrIbBondinfoMapper;

    @Autowired(required=false)
    private SysWflBusinessListMapper sysWflBusinessListMapper;
    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;
    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;
    @Autowired
    private CfetsEtrIbRepoinfo003Service cfetsEtrIbRepoinfo003Service;


    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsEtribRepoInfo003Dto entity, OperationType type) {
        if(Objects.isNull(entity.getEntrustAmt())) {
            entity.setEntrustAmt(BigDecimal.ZERO);
        }
        DecimalFormat df4 = new DecimalFormat("#,##0.00");
        return  "(指令日期"+ ":" + DateUtil.format(entity.getEntrustDate())+")-"+
                "(回购期限" + ":" + (entity.getRepoTerm() == null? "" : entity.getRepoTerm()) + "天)-"+
                "指令金额"+ ":" + df4.format(entity.getEntrustAmt()) + "元";
    }
    private Map<String, Object> getEtrLimitSwitch(String workflowId, CfetsEtribRepoInfo003Dto dto,
                                                  CommonBusinessService commonBusinessService) throws WorkFlowException {
        BigDecimal amount = dto.getEntrustNum();
        if (amount == null) {
            amount = dto.getEntrustAmt();
        }
        Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
                BussinessDictCodeList.平安资产类型_回购.getValue(), dto.getAssetType1(), dto.getTradeMarket(), dto.getPs()
                , dto.getFinprodId(), workflowId
        );
        // add by zhangrui1 风险审批修改 start
        params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
        // add by zhangrui1 风险审批修改 end
        return commonBusinessService.是否需要上级审批_委托用(params);
    }
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        CfetsEtribRepoInfo003Dto dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        if (taskId != null) {
            try {
                if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH_IS_TOTAL), "Y")){
                    return getEtrLimitSwitch(entity.getWorkflowId(), dto, commonBusinessService);
                }else{
//           		BussinessConstant.是否需要上级审批
                    List<CfetsEtribRepoInfo003Dto> etribRepoInfo003DtoList =dto.getEtrInvestRepoInfoDtos();
                    Map<String, Object> result = new HashMap<>();
                    for(CfetsEtribRepoInfo003Dto cfetsEtribRepoInfo003Dto:etribRepoInfo003DtoList){
                        result=getEtrLimitSwitch(entity.getWorkflowId(), cfetsEtribRepoInfo003Dto, commonBusinessService);
                        if((boolean)result.get(BussinessConstant.是否需要上级审批)==true){
                            return result;
                        }
                    }
                    return result;
                }
            }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }
    /**
     * 流程结束后操作
     *
     * @param type 操作类型
     */
    @Override
    public void doComplete(CfetsEtribRepoInfo003Dto entity, OperationType type) {
        entity.setOperateType(type.toString());
        if (StringUtil.equals(entity.getSettleSpreed(),
                DictCodeList.清算速度_T_0.getValue())) {
            entity.setSettleSpreed("0");
        }
        if (StringUtil.equals(entity.getSettleSpreed(),
                DictCodeList.清算速度_T_1.getValue())) {
            entity.setSettleSpreed("1");
        }
//        if(StringUtil.isNotEmpty(dto.getSettleSpreed())){
//            if(Integer.parseInt(dto.getSettleSpreed())!=0){
//                List<String> holiday = dateCalService.getDealCalendarHoliday(dto.getVdate(),
//                        DateUtil.addDays(dto.getVdate(), 31),dto.getCalendarId());
//                Date tradingDay = dateCalService.calculationTradingDay(dto.getVdate(), Integer.parseInt(dto.getSettleSpreed()), holiday, "1");
//                dto.setSettleDate(tradingDay);
//                // 每个组合设置交割日
//                if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
//                    for (int i = 0; i < entity.getEtrInvestRepoInfoDtos().size(); i++) {
//                        entity.getEtrInvestRepoInfoDtos().get(i).setSettleDate(tradingDay);
//                    }
//                }
//            }
//        }
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }
        for (CfetsEtribRepoInfo003Dto dto: entity.getEtrInvestRepoInfoDtos()) {
            try {
                doCompleteInsert(dto, type);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            if(bussRepoSecService != null){
                bussRepoSecService.insert(dto.getBussRepoSec(),dto.getEntrustId());
            }
        }
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
    }



	private void doCompleteInsert(CfetsEtribRepoInfo003Dto dto, OperationType type)
			throws IllegalAccessException, InvocationTargetException {
		BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        entrustCommonDto.setEtrEntrust(etrEntrust);
        entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        //add by tanxuan on 20200514 指令分发推送MQ  参数定义
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(type.toString(), "NEW")) {
            dto.setFinprodType2(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
//            由于dto.getEtrInvestPersonInfo()中InvManagerId为null，因此审批后的业务表中无项目经理，故隐藏掉这行代码
//            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(dto.getEntrustAmt());
            //做交易录单时交易日历丢失 交易日历默认银行间 add by libaisong 2021-03-01 start
            etrEntrust.setCalendarId("03");
            //做交易录单时交易日历丢失 交易日历默认银行间 add by libaisong 2021-03-01 end
            etrEntrust.setTradeMarket( DictCodeListExt1.交易场所_银行间.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            //指令接收查询交易方向丢失 add by libaisong 2021-03-01 start
            etrEntrustAdd.setBusiPs(dto.getPs());
            //指令接收查询交易方向丢失 add by libaisong 2021-03-01 end
            etrEntrustAdd.setVdate(dto.getVdate());
            etrEntrustAdd.setMdate(dto.getMdate());
            etrEntrustAdd.setPledgeStockId(dto.getZyqk());
            if(StringUtil.isNotEmpty(dto.getTransmitTime())){
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            }else{
                etrEntrustAdd.setTransmitTime(new Date());
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            if(StringUtil.isNotEmpty(dto.getTermType())){
                etrEntrustAdd.setTermType(dto.getTermType());
            }
            if(dto.getRepoTerm()!=null){
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
//            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
//
//                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
//                        dto.getEtrInvestPersonInfo().getTrader());
//                for (String trader : traderList) {
//                    EtrDistribute etrDistribute = new EtrDistribute();
//                    // 设置接收状态
//                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
//                    // 设置指定交易员指定交易员 = 指定交易员
//                    etrDistribute.setTrader(trader);
//                    // 设置委托代码
//                    etrDistribute.setEntrustId(dto.getEntrustId());
//                    //
//                    etrDistributeList.add(etrDistribute);
//                }
//                // entity.setEtrDistributeList(etrDistributeList);
//            }
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            entity.setEtrPledgeList(getEtrPledgeListByBussRepoSecDto(dto.getBussRepoSec(),dto.getEntrustId()));
            baseEtrInvestInfoService.newDelegate(entity);
        } else {
            dto.setFinprodType2(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
//            由于dto.getEtrInvestPersonInfo()中InvManagerId为null，因此审批后的业务表中无项目经理，故隐藏掉这行代码
//            BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(dto.getEntrustAmt());
            //做交易录单时交易日历丢失 交易日历默认银行间 add by libaisong 2021-03-01 start
            etrEntrust.setCalendarId("03");
            //做交易录单时交易日历丢失 交易日历默认银行间 add by libaisong 2021-03-01 end
            etrEntrust.setTradeMarket( DictCodeListExt1.交易场所_银行间.getValue());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            //指令接收查询交易方向丢失 add by libaisong 2021-03-01 start
            etrEntrustAdd.setBusiPs(dto.getPs());
            //指令接收查询交易方向丢失 add by libaisong 2021-03-01 end
            etrEntrustAdd.setVdate(dto.getVdate());
            etrEntrustAdd.setMdate(dto.getMdate());
            if(StringUtil.isNotEmpty(dto.getTransmitTime())){
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            }else{
                etrEntrustAdd.setTransmitTime(new Date());
            }
            etrEntrustAdd.setPledgeStockId(dto.getZyqk());
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            if(StringUtil.isNotEmpty(dto.getTermType())){
                etrEntrustAdd.setTermType(dto.getTermType());
            }
            if(dto.getRepoTerm()!=null){
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
//            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
//                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
//                        dto.getEtrInvestPersonInfo().getTrader());
//                for (String trader : traderList) {
//                    EtrDistribute etrDistribute = new EtrDistribute();
//                    // 设置接收状态
//                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
//                    // 设置指定交易员指定交易员 = 指定交易员
//                    etrDistribute.setTrader(trader);
//                    // 设置委托代码
//                    etrDistribute.setEntrustId(dto.getEntrustId());
//                    //
//                    etrDistributeList.add(etrDistribute);
//                }
//                // entity.setEtrDistributeList(etrDistributeList);
//            }
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            entity.setEtrPledgeList(getEtrPledgeListByBussRepoSecDto(dto.getBussRepoSec(),dto.getEntrustId()));
            baseEtrInvestInfoService.updateDelegate(entity);
        }
        //add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
	}

    @Override
    public void doDelete(String workflowId) {
        CfetsEtribRepoInfo003Dto entity = getBussinessEntityByWorkflowId(workflowId);
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
            for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                //从废弃表中获取，转移到指令表
                etrEntrustBackupMapper.insertEtrFromQuery(dto.getEntrustId());
                etrEntrustBackupMapper.insertEtrAddFromQuery(dto.getEntrustId());
                // 删除废弃表
                etrEntrustBackupMapper.deleteByPrimaryKey(dto.getEntrustId());
                etrEntrustBackupMapper.deleteAddByPrimaryKey(dto.getEntrustId());
            }
        }
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            //发送头寸
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(),DictCodeListExt1.子系统代码_委托.getValue(),DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(),entity.getPs());
        }
   }


    /**
     * 流程启动时为附件绑定工作流代码 <功能详细描述>
     *
     * @param entity
     * @param operateType
     * @see
     * @since 1.0
     */
    @Override
    public void doInsert(CfetsEtribRepoInfo003Dto entity, OperationType operateType) {
        if ("submit".equals(entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestRepoInfoDtos())) {
                for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                    WFlowCommonService.insertEtrEntrustRe(dto.getEntrustId(), entity.getTotalEtrustId());
                    dto.setWorkflowId(entity.getWorkflowId());
                    doInsertFlow(dto);
                }
                //发送头寸
                this.sendPositionMethod(entity, StringUtil.equals(entity.getSystemAct(), DictCodeListExt3.系统动作_新建.getValue()) ? true : false, null);
            }
            this.olePositionDeal(entity);
        }
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(dto.getEntrustId());
            if (StringUtil.equals(operateType.name(), "NEW")) {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
            } else {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
            }
            oprHisReq.setBusiDes(entity.getRemark());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        }
    }

    @Override
    public void doBack(CfetsEtribRepoInfo003Dto entity) {
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                AllTradeCommonUtil.deletePosition(null, dto.getEntrustId());
            }
        }
    }

    /**
     * 修改指令回退时,从废弃表将数据取出重新插入
     *
     * @param entrustId 指令代码
     * @author jiangpeng
     * @date 2022/3/24 9:01
     */
    public void backReRoll(String entrustId) {
        //从废弃表中获取，转移到指令表
        etrEntrustBackupMapper.insertEtrFromQuery(entrustId);
        etrEntrustBackupMapper.insertEtrAddFromQuery(entrustId);
        // 删除废弃表
        etrEntrustBackupMapper.deleteByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteAddByPrimaryKey(entrustId);
    }

    @Override
    public void doUpdate(CfetsEtribRepoInfo003Dto entity) {
        if("save".equals(entity.getOperateType())){
            return;
        }
        String confirmOk = RequestUtil.getParam("confirmOk");
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            boolean isSubmit = true;
           /* for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
                wflEtrEntrustKey.setEntrustId(dto.getEntrustId());
                wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                WflEtrEntrust wflEtrEntrust = wflEtrEntrustMapper.selectByPrimaryKey(wflEtrEntrustKey);
                if(wflEtrEntrust != null) {
                    isSubmit = false;
                }
            }*/
            // 代办中编辑后质押券面未修改
            for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL), "02")
                        || StringUtil.equals(entity.getPs(), DictCodeListExt1.回购方向_正回购.getValue())) {
                    dto.setBussRepoSec(new BussRepoSecDto());
                    dto.getBussRepoSec().setPageFrom("CfetsEtrIbRepoinfo003");
                    List<PledgeBondInfoDto> pledgeBondList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(entity.getPledgeBondTotalListDetailed())) {
                        for (CfetsPledgeBondInfoDto cfetsPledgeBondInfoDto : entity.getPledgeBondTotalListDetailed()) {
                            if (StringUtil.equals(dto.getPortfolioId(), cfetsPledgeBondInfoDto.getPortfolioId())) {
                                //组合代码相同
                                PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                                BeanUtil.copyPropertiesIgnoreNull(pledgeBondInfoDto, cfetsPledgeBondInfoDto);
                                pledgeBondInfoDto.setBondCode(cfetsPledgeBondInfoDto.getFinprodMarketId());
                                pledgeBondInfoDto.setPledgeRatio(cfetsPledgeBondInfoDto.getScale());
                                pledgeBondInfoDto.setPledgeRatio(cfetsPledgeBondInfoDto.getScale());
                                pledgeBondList.add(pledgeBondInfoDto);
                            }
                        }
                    }
                    dto.getBussRepoSec().setPledgeBondList(pledgeBondList);
                }
            }
            try {
                SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                    isSubmit = false;
                }
                if("submit".equals(entity.getOperateType()) && isSubmit) {
                    List<CfetsEtribRepoInfo003Dto> dataList = new ArrayList<>();
                    cfetsEtrIbRepoinfo003Service.packageEquity(entity,dataList);
                    EntrustRdsCommonAop.riskListToChildren(entity.getRiskTrialRetDto(), entity.getEtrInvestRepoInfoDtos(),(dto, list) -> dto.setRiskTrialRetDto(list));

                    if(!StringUtil.isNotEmpty(entity.getEntrustId())) {
                        BussinessCodeParameter param = new BussinessCodeParameter();
                        param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
                        String generatedId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                        entity.setTotalEtrustId(generatedId);
                    }else{
                        entity.setTotalEtrustId(entity.getEntrustId());
                    }
                }
                WFlowCommonService.deleteFlow(entity.getWorkflowId());
                for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
                    dto.setWorkflowId(entity.getWorkflowId());
                    doInsertFlow(dto);
                }
                //发送头寸
                sendPositionMethod(entity, isSubmit, confirmOk);
                olePositionDeal(entity);
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                String code = (String) bm.get("code");
                StringBuffer messageStr = (StringBuffer) bm.get("message");
                if (StringUtil.isEmpty(confirmOk) && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
                }
                throw new PositionException(messageStr.toString());
            }
        }
    }

    private  void sendPositionMethod(CfetsEtribRepoInfo003Dto entity,boolean isSubmit,String confirmOk){
        List<CfetsEtrIbRepoPosPortfolioCashRequest> ptlCashList=new ArrayList<>();
        List<CfetsEtrIbRepoPosPortfolioSecRequest> ptlSecList=new ArrayList<>();
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            //调用头寸
            CfetsEtrIbRepoPosPortfolioCashRequest posport=new CfetsEtrIbRepoPosPortfolioCashRequest();
            posport.setPortfolioId(dto.getPortfolioId());
            posport.setEtrId(dto.getEntrustId());
            if(StringUtil.isNotEmpty(dto.getPortfolioId())){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId =cfetsEtribBondInfo003Service.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            posport.setCashAmount(dto.getEntrustAmt());
            ptlCashList.add(posport);
            if (StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                for(PledgeBondInfoDto pledgeBondInfoDto:dto.getBussRepoSec().getPledgeBondList()){
                    CfetsEtrIbRepoPosPortfolioSecRequest secport=new CfetsEtrIbRepoPosPortfolioSecRequest();
                    secport.setPortfolioId(dto.getPortfolioId());
                    secport.setEtrId(dto.getEntrustId());
                    secport.setFinprodId( pledgeBondInfoDto.getFinprodId());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", pledgeBondInfoDto.getFinprodId());
                    String secAcctId = cfetsEtribBondInfo003Service.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        secport.setSecAcctId(secAcctId); // 证券账户
                    }
                    secport.setSecAmount(pledgeBondInfoDto.getPledgePrin());
                    secport.setOwnSecAmount(pledgeBondInfoDto.getPledgePrin());
                    secport.setIntegrateSecAmount(BigDecimal.ZERO);
                    ptlSecList.add(secport);
                }
            }
        }
        //调用头寸
        CfetsEtrIbRepoPositionRequest req=new CfetsEtrIbRepoPositionRequest();
        req.setInterfaceType("REPO001");
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(entity.getTotalEtrustId());
        req.setWorkFlowId(entity.getWorkflowId());
        req.setFlowStatus(DictCodeListExt3.流程节点_流程提交.getValue());
        req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
        String balanceCheck="Y";
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
        req.setBalanceCheck(balanceCheck);
        req.setBusiType(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setSettleSpeed(entity.getSettleSpreed());
        req.setBusiAct(DictCodeListExt3.业务动作_投资指令.getValue());
        req.setPs(entity.getPs());
        if(isSubmit){
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setSystemAct(entity.getSystemAct());
        }else{
            req.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
            req.setSystemAct(entity.getSystemAct());
            req.setbTotalTradeId(entity.getTotalEtrustId());
        }
        req.setCdate(entity.getEntrustDate());
        req.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        PositionHttpRequest.send(req);
    }

	public void doInsertFlow(CfetsEtribRepoInfo003Dto dto) {
    	try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            entrustCommonDto.setEtrEntrust(etrEntrust);
            entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
            entrustCommonDto.setBussRepoSecDto(dto.getBussRepoSec());
            dto.setFinprodType2(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())?DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue():DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            //BeanUtil.copyProperties(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            etrEntrust.setEntrustAmt(dto.getEntrustAmt());
            etrEntrust.setInvManagerId(dto.getInvManagerId());
            etrEntrust.setTradeMarket(dto.getTradeMarket());
            etrEntrust.setTradePlat(dto.getTradePlat());
            BeanUtil.copyProperties(etrEntrustAdd, dto);
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            etrEntrustAdd.setVdate(dto.getVdate());
            etrEntrustAdd.setMdate(dto.getMdate());
            etrEntrustAdd.setPledgeStockId(dto.getZyqk());
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(),
                    DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long)1);
            }
            if(StringUtil.isNotEmpty(dto.getTermType())){
                etrEntrustAdd.setTermType(dto.getTermType());
            }
            if(dto.getRepoTerm()!=null){
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            entity.setEtrEntrust(etrEntrust);
            entity.setEtrEntrustAdd(etrEntrustAdd);
            entity.setEtrPledgeList(getEtrPledgeListByBussRepoSecDto(dto.getBussRepoSec(),dto.getEntrustId()));
            entity.setWorkFlowId(dto.getWorkflowId());
            WFlowCommonService.insertFlow(entity);
//            if(baseEtrCashPositionCommService!=null){
//                baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
//            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

	private List<EtrPledge> getEtrPledgeListByBussRepoSecDto(BussRepoSecDto bussRepoSec,String bizCode){
	   	 if(StringUtil.isNotEmpty(bussRepoSec)){
	   		 List<EtrPledge> etrPledgeList = new ArrayList<>();
	   		 for (PledgeBondInfoDto pledgeBond : bussRepoSec.getPledgeBondList()) {
	                // 回购委托新建
	                EtrPledge etrPledge = new EtrPledge();
	                // 委托代码 = 委托代码
	                etrPledge.setEntrustId(bizCode);
	                // 金融产品代码 = 输出质押品DTO.金融产品代码
	                etrPledge.setFinprodId(pledgeBond.getFinprodId());
	                // 质押面值 = 输出质押品DTO.质押面值
	                etrPledge.setPledgeFaceValue(new BigDecimal("100"));
	                // 质押数量 = 输出质押品DTO.质押数量
	                etrPledge.setPledgeNumber(ArithmeticUtil.divide(pledgeBond.getPledgePrin(), new BigDecimal("100"), 4));
	                // 质押率 = 输出质押品DTO.质押率
	                etrPledge.setPledgeRatio(pledgeBond.getPledgeRatio());
	                // 质押金额 = 输出质押品DTO.质押金额
	                etrPledge.setPledgeAmt(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), etrPledge.getPledgeRatio()));
	                etrPledgeList.add(etrPledge);
	       	 }
             return etrPledgeList;
	   	 }
	   	 return null;
	}

    public CfetsEtribRepoInfo003Dto packageEquity(CfetsEtribRepoInfo003Dto RepoInfoDto,List<CfetsEtribRepoInfo003Dto> dataList) {
        //风险
        CfetsEtribdoRiskcalResponse riskDto = RepoInfoDto.getCfetsEtribdoRiskcalResponse();
        List<RiskTrialRetDto> riskTrialRetDto=new ArrayList<>();
        if(StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())){
            for(CfetsEtribdoRiskcalResponse it:riskDto.getRiskcalReturnList()){
                RiskTrialRetDto t=new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t,it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        RepoInfoDto.setRiskTrialRetDto(riskTrialRetDto);
        for(CfetsEtribRepoInfo003Dto dto: RepoInfoDto.getEtrInvestRepoInfoDtos()) {
            if (StringUtil.isEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(
                        param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
            dto.setSettleSpreed(RepoInfoDto.getSettleSpreed());
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            dto.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            dto.setInvAim("999999");
            dto.setVdate(RepoInfoDto.getEntrustDate());
            dto.setMdate(RepoInfoDto.getEntrustDate());
            dto.setPs(RepoInfoDto.getPs());
            dto.setPrice(RepoInfoDto.getPrice());
            dto.setRepoTerm(RepoInfoDto.getRepoTerm());
            dto.setPriceModel(RepoInfoDto.getPriceModel());
            dto.setTermType(RepoInfoDto.getTermType());
            dto.setCounterId(RepoInfoDto.getCounterId());
            dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
            dto.setRemark(RepoInfoDto.getRemark());
            // ----------add by zhangrui1 20200715 预占功能 start----------
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                dataList.add(dto);
            }
            // ----------add by zhangrui1 20200715 预占功能 end----------

            //针对退回指令，查询指令下达时间
            if(StringUtil.isNotEmpty(RepoInfoDto.getEntrustId())){
                //根据指令id查询出该资产是退回指令，且该指令的下达时间
                CfetsEtribBondInfo003Dto transmitTimeDto=cfetsEtrIbBondinfoMapper.selectTransmitTimeByInfo(RepoInfoDto.getEntrustId());
                if(null!=transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())){
                    RepoInfoDto.setTransmitTime(transmitTimeDto.getTransmitTime());
                    dto.setTransmitTime(transmitTimeDto.getTransmitTime());
                }
                RepoInfoDto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
            }else{
                RepoInfoDto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            }
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL), "02")
                    ||StringUtil.equals(RepoInfoDto.getPs(), DictCodeListExt1.回购方向_正回购.getValue())){
                dto.setBussRepoSec(new BussRepoSecDto());
                dto.getBussRepoSec().setPageFrom("CfetsEtrIbRepoinfo003");;
                List<PledgeBondInfoDto> pledgeBondList= new ArrayList<>();
                // ZGZGST-2152: 回购方向是逆回购时,质押券信息列表明细是null,增加非空判断 fuyongsheng 20210907 edit start
                if(CollectionUtils.isNotEmpty(RepoInfoDto.getPledgeBondTotalListDetailed())){
                    for(CfetsPledgeBondInfoDto cfetsPledgeBondInfoDto :RepoInfoDto.getPledgeBondTotalListDetailed()){
                        if(StringUtil.equals(dto.getPortfolioId(), cfetsPledgeBondInfoDto.getPortfolioId())){
                            //组合代码相同
                            PledgeBondInfoDto pledgeBondInfoDto =new PledgeBondInfoDto();
                            BeanUtil.copyPropertiesIgnoreNull(pledgeBondInfoDto, cfetsPledgeBondInfoDto);
                            pledgeBondInfoDto.setBondCode(cfetsPledgeBondInfoDto.getFinprodMarketId());
                            pledgeBondInfoDto.setPledgeRatio(cfetsPledgeBondInfoDto.getScale());
                            pledgeBondInfoDto.setPledgeRatio(cfetsPledgeBondInfoDto.getScale());
                            pledgeBondList.add(pledgeBondInfoDto);
                        }
                    }
                }
                // ZGZGST-2152: 回购方向是逆回购时,质押券信息列表明细是null,增加非空判断 fuyongsheng 20210907 edit end
                dto.getBussRepoSec().setPledgeBondList(pledgeBondList);
            }
        }

        return RepoInfoDto;
    }


    /**
     * 组装持仓入库数据
     * @author jiangpeng
     * @date 2022/2/21 10:50
     * @param entity
     * @return com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto
     */
    private List<EntrustCommonDto> getEtrExecuteEntity(CfetsEtribRepoInfo003Dto entity) {
        List<EntrustCommonDto> entrustCommonDtos = new ArrayList<>();
        for (CfetsEtribRepoInfo003Dto dto : entity.getEtrInvestRepoInfoDtos()) {
            EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
            entrustCommonDto.setIsExistNew(DictCodeList.是否_是.getValue());
            EtrEntrust etrEntrust = new EtrEntrust();
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
//            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto.getEtrInvestPersonInfo());
            etrEntrust.setFinprodType2(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                    ? DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue() : DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            etrEntrust.setIsOutTrade(DictCodeList.是否_否.getValue());
            etrEntrust.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            if (StringUtil.isEmpty(dto.getTradePlat())) {
                etrEntrust.setTradePlat(DictCodeList.交易平台_固定收益平台.getValue());
            }
            etrEntrustAdd.setEntrustId(dto.getEntrustId());
            if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_0.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 0);
            }
            if (StringUtil.equals(dto.getSettleSpreed(), DictCodeList.清算速度_T_1.getValue())) {
                etrEntrustAdd.setSettleSpreed((long) 1);
            }
            if (StringUtil.isNotEmpty(dto.getTermType())) {
                etrEntrustAdd.setTermType(dto.getTermType());
            }
            if (dto.getRepoTerm() != null) {
                etrEntrustAdd.setTermDays(dto.getRepoTerm().longValue());
            }
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            etrEntrustAdd.setAssetType1(DictCodeList.回购类型_质押式回购.getValue());
            etrEntrustAdd.setBusiPs(dto.getPs());
//            etrEntrustAdd.setMinAmt(dto.getMinAmt());
//            etrEntrustAdd.setVdate(dto.getVdateAdd());
//            etrEntrustAdd.setMdate(dto.getMdateAdd());
//            etrEntrustAdd.setCashUsedate(dto.getAvailableDay());
//            etrEntrustAdd.setSecAcctId(dto.getSecuritiesCust());
            // 有质押品
            if (StringUtil.isNotEmpty(dto.getBussRepoSec()) && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                Map<String, EntrustCommonDto> entrustCommonDtoMap = new HashMap<>();
                for (PledgeBondInfoDto pledgeBondInfoDto : dto.getBussRepoSec().getPledgeBondList()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", pledgeBondInfoDto.getFinprodId());
                    String secAcctId = cfetsEtribBondInfo003Service.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        EntrustCommonDto temp = entrustCommonDtoMap.get(secAcctId);
                        if (temp == null) {
                            temp = new EntrustCommonDto();
                            BeanUtil.copyPropertiesIgnoreNull(temp, entrustCommonDto);
                        }
                        BussRepoSecDto bussRepoSecDto = temp.getBussRepoSecDto();
                        if (bussRepoSecDto == null) {
                            bussRepoSecDto = new BussRepoSecDto();
                        }
                        List<PledgeBondInfoDto> pledgeBondInfoDtoList = bussRepoSecDto.getPledgeBondList();
                        if (pledgeBondInfoDtoList == null) {
                            pledgeBondInfoDtoList = new ArrayList<>();
                        }
                        pledgeBondInfoDtoList.add(pledgeBondInfoDto);
                        bussRepoSecDto.setPledgeBondList(pledgeBondInfoDtoList);
                        etrEntrustAdd.setSecAcctId(secAcctId);
                        temp.setEtrEntrust(etrEntrust);
                        temp.setEtrEntrustAdd(etrEntrustAdd);
                        temp.setBussRepoSecDto(bussRepoSecDto);
                        entrustCommonDtoMap.put(secAcctId, temp);
                    }
                }
                for (String key : entrustCommonDtoMap.keySet()) {
                    entrustCommonDtos.add(entrustCommonDtoMap.get(key));
                }
            } else {// 无质押品
                entrustCommonDto.setEtrEntrust(etrEntrust);
                entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
                entrustCommonDtos.add(entrustCommonDto);
            }
        }
        return entrustCommonDtos;
    }

    /**
     * 旧头寸调用
     *
     * @param entity
     * @author jiangpeng
     * @date 2022/2/24 9:46
     */
    private void olePositionDeal(CfetsEtribRepoInfo003Dto entity) {
        if (DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getPs())
                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            List<EntrustCommonDto> entrustCommonDtos = this.getEtrExecuteEntity(entity);
            for (EntrustCommonDto entrustCommonDto : entrustCommonDtos) {
                baseEtrCashPositionCommService.entrustStartExecute(entrustCommonDto);
            }
        }
    }

}
