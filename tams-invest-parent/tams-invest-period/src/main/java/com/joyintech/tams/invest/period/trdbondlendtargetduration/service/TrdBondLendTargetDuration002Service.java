package com.joyintech.tams.invest.period.trdbondlendtargetduration.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.trade.impl.BussTrdTransferOrderImplService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdbondlendtargetduration.InvestTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.period.trdbondlendtargetduration.dto.TrdBondLendTargetDuration002ProcessDto;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendCashPositionDto;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendDuration002Dto;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendPositionDto;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendSecPositionDto;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.CommentFunction;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdBondLendTargetDuration002Service extends BaseService<TrdBondLendTargetDuration002ProcessDto> implements BaseProcessService<TrdBondLendTargetDuration002ProcessDto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdBondLendTargetDuration002Service.class);

    @Autowired
    private TradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private BussTrdTransferOrderImplService bussTrdTransferOrderImplService;

    private static BussinessCodeParameter parameter = new BussinessCodeParameter();

    static {
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
    }

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired(required=false)
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    public TrdBondLendTargetDuration002ProcessDto findEffectiveData(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondLendTargetDuration/findEffectiveData",
            pageParam, TrdBondLendTargetDuration002ProcessDto.class);
    }

    @Override
    public void doComplete(TrdBondLendTargetDuration002ProcessDto entity, OperationType operationType) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        保存持仓(entity, operationType, false);
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        saveData(entity, operationType);
        baseTradeProductDealCommServiceImpl.deleteWflData(entity.getWorkflowId());
        if (OperationType.REVOKE == operationType) {
            PositionHttpRequest.dorevoke(entity.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),DictCodeListExt4.头寸业务类型_银行间债券借贷收付息还本.getValue(),getPsFromCashType(entity.getCashType(), entity.getTradeType()));
        }else{
            try{
                sendNewPosition(entity,DictCodeListExt3.系统动作_新建.getValue(),DictCodeListExt3.流程节点_审批生效.getValue());
            }catch (PositionException e){
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                if(bm==null){
                    WorkFlowUtil.showMessage( message.getMessage(), MessageType.错误消息);
                }
                String code = (String) bm.get("code");
                StringBuffer messageStr = (StringBuffer) bm.get("message");
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
                }
            }
        }
    }

    private void 保存持仓(TrdBondLendTargetDuration002ProcessDto entity, OperationType operationType, boolean 流程中) {
        if (!DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType()) && 流程中) {
            return;
        }
        List<PtlCashPositionFlowDto> ptlCashPositionFlowDtos = Lists.newArrayList();
        entity.getTradeInfoDto().getStlPayOrdersList().forEach(stlPayOrders -> {
            PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
            ptlCashPositionFlowDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
            ptlCashPositionFlowDto.setFinprodId(entity.getFinprodId());
            ptlCashPositionFlowDto.setSeqno(getSeqNo());
            ptlCashPositionFlowDto.setPortfolioId(entity.getActPortfolioId());
            ptlCashPositionFlowDto.setAcctId(stlPayOrders.getMyAcctNo());
            ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
            if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType()) && 流程中) {
                ptlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
            }
            ptlCashPositionFlowDto.setCdate(stlPayOrders.getSettleDate());
            ptlCashPositionFlowDto.setCcy(stlPayOrders.getCcy());
            ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt());
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                if (DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue().equals(entity.getTradeType())) {
                    ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
                }
            }
            else {
                if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType())) {
                    ptlCashPositionFlowDto.setAmount(stlPayOrders.getSettleAmt().negate());
                }
            }
            ptlCashPositionFlowDto.setOrderId(stlPayOrders.getOrderId());
            ptlCashPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
            ptlCashPositionFlowDtos.add(ptlCashPositionFlowDto);
        });

        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlowDtos);
        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
            for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                PtlCashPositionFlowDto ptlCashPositionFlow1 = new PtlCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(ptlCashPositionFlow1, ptlCashPositionFlow);
                ptlCashPositionFlow1.setPortfolioId(entity.getPortfolioId());
                if (StringUtil.isEmpty(ptlCashPositionFlow.getPortfolioId())) {
                    ptlCashPositionFlow.setPortfolioId(entity.getPortfolioId());
                }
                basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
                if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow1);
                }
            }
        }
        if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType()) && 流程中) {
            baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, true);
        }else if (!DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType())&&!流程中) {
            baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, true);
        }
    }

    private void saveData(TrdBondLendTargetDuration002ProcessDto entity, OperationType operationType) {
        InvestTradeInfoDto tradeInfoDto = entity.getTradeInfoDto();

        TrdFinProductDealTotal trdFinProductDealTotal = tradeInfoDto.getTrdFinProductDealTotal();
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
            trdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_是.getValue());
            trdFinProductDealTotalMapper.updateByPrimaryKeySelective(trdFinProductDealTotal);

            trdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + "R");
            trdFinProductDealTotal.setIsCancel(DictCodeListExt1.是否_否.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
        }
        trdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);

        PaTrdFinProductDealTotal paTrdFinDealTotal = tradeInfoDto.getPaTrdFinProductDealTotal();
        if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
            paTrdFinDealTotal.setTotleTradeId(paTrdFinDealTotal.getTotleTradeId() + "R");
        }
        paTrdFinDealTotalMapper.insertSelective(paTrdFinDealTotal);

        TrdDealTotalAdd trdDealTotalAdd = tradeInfoDto.getTrdDealTotalAdd();
        if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
            trdDealTotalAdd.setTotleTradeId(trdDealTotalAdd.getTotleTradeId() + "R");
        }
        trdDealTotalAddMapper.insertSelective(trdDealTotalAdd);

        tradeInfoDto.getTrdProductDealList().forEach(trdProductDeal -> {
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                trdProductDeal.setIsCancel(DictCodeListExt1.是否_是.getValue());
                trdProductDealMapper.updateByPrimaryKeySelective(trdProductDeal);

                trdProductDeal.setIsCancel(DictCodeListExt1.是否_否.getValue());
                trdProductDeal.setTotleTradeId(trdProductDeal.getTotleTradeId() + "R");
                trdProductDeal.setpTradeId(trdProductDeal.getTradeId());
                trdProductDeal.setTradeId(trdProductDeal.getTradeId() + "R");
                trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            }
            trdProductDealMapper.insertSelective(trdProductDeal);
        });

        tradeInfoDto.getTrdProductDealAddList().forEach(trdProductDealAdd -> {
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                trdProductDealAdd.setTradeId(trdProductDealAdd.getTradeId() + "R");
            }
            trdProductDealAddMapper.insertSelective(trdProductDealAdd);
        });

        tradeInfoDto.getPaTrdProductDealList().forEach(paTrdProductDeal -> {
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
            }
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
        });

        tradeInfoDto.getPtlTradeList().forEach(ptlTrade -> {
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                ptlTrade.setBusiNo(ptlTrade.getBusiNo() + "R");
            }
            ptlTradeMapper.insertSelective(ptlTrade);
        });

        tradeInfoDto.getStlPayOrdersList().forEach(stlPayOrders -> {
            if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                stlPayOrders.setTradeId(stlPayOrders.getTradeId() + "R");
                stlPayOrders.setOrderId(stlPayOrders.getOrderId() + "R");
            }
            stlPayOrdersMapper.insertSelective(stlPayOrders);
        });

        tradeInfoDto.getTrdProductDealAddList().forEach(trdProductDealAdd -> {
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(trdProductDealAdd.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            List<PtlTrade> ptlTrades = new ArrayList<>();
            tradeInfoDto.getPtlTradeList().forEach(ptlTrade -> {
                if (ptlTrade.getBusiNo().equals(trdProductDealAdd.getTradeId())) {
                    ptlTrades.add(ptlTrade);
                }
            });
            inputDto.setPtlTradeList(ptlTrades);
            tradeInfoDto.getTrdProductDealList().forEach(trdProductDeal -> {
                if (trdProductDeal.getTradeId().equals(trdProductDealAdd.getTradeId())) {
                    trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    if (operationType == OperationType.DELETE || operationType == OperationType.REVOKE) {
                        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    }
                    inputDto.setTrdProductDeal(trdProductDeal);
                    inputDto.setTrdProductDealAdd(trdProductDealAdd);
                    String actPortfolioId = null;
                    for (PtlTrade ptlTrade : ptlTrades) {
                        if (!ptlTrade.getPortfolioId().equals(trdProductDealAdd.getPortfolioId())) {
                            actPortfolioId = trdProductDealAdd.getPortfolioId();
                        }
                    }
                    if (actPortfolioId != null) {
                        inputDto.setActPortfolioId(actPortfolioId);
                    }
                    if (tradeInfoDto.getStlPayOrdersList() != null && !tradeInfoDto.getStlPayOrdersList().isEmpty()) {
                        StlPayOrders order = new StlPayOrders();
                        BeanUtil.copyPropertiesIgnoreNull(order, tradeInfoDto.getStlPayOrdersList().get(0));
                        inputDto.setStlPayOrders(order);
                    }
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                }
            });
        });
    }

    @Override
    public void doInsert(TrdBondLendTargetDuration002ProcessDto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && com.joyintech.framework.util.StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
        if (!BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            return;
        }
        entity.setCreateUser(SecurityUtil.getLoginUser().getUserName());
        entity.setCreateDept(SecurityUtil.getLoginUser().getDeptCode());
        Date date = new Date();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType())) {
            setWorkflowId(entity.getTradeInfoDto(), entity.getWorkflowId());
        }
        saveDataWhenStartWorkflow(entity, type);
        保存持仓(entity, type, true);
        try{
            String confirmOk = RequestUtil.getParam("confirmOk");
            if(StringUtil.isNotEmpty(confirmOk)){
                entity.setBalanceCheck("N");
            }
            sendNewPosition(entity,DictCodeListExt3.系统动作_新建.getValue(),DictCodeListExt3.流程节点_流程提交.getValue());
        }catch (PositionException e){
            Message message = PositionUtil.ajaxErrorReturn(e);
            BaseMap bm = (BaseMap) message.getData();
            if(bm==null){
                WorkFlowUtil.showMessage( message.getMessage(), MessageType.错误消息);
            }
            String code = (String) bm.get("code");
            StringBuffer messageStr = (StringBuffer) bm.get("message");
            if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
            }
            if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
            }
        }
    }

    private void sendNewPosition (TrdBondLendTargetDuration002ProcessDto dto, String decisionAct, String flowStatus){
        TrdSecLendPositionDto position = new TrdSecLendPositionDto();
        position.setWorkFlowId(dto.getWorkflowId());
        position.setBusiType(DictCodeListExt4.头寸业务类型_银行间债券借贷收付息还本.getValue());
        position.setBusiAct(DictCodeListExt3.业务动作_交易登记.getValue());
        position.setPs(getPsFromCashType(dto.getCashType(), dto.getTradeType()));
        position.setTradePlace(dto.getTradeMarket());
//        position.setSettleSpeed(dto.getSettleSpeed());
        position.setDecisionAct(decisionAct);
        position.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        position.setCdate(DateUtil.parse(dto.getTradeDate()));
        position.setCcy(dto.getCcy());
        position.setTotalTradeId(dto.getTotleTradeId());
        position.setFlowStatus(flowStatus);
        position.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        position.setBalanceCheck(dto.getBalanceCheck());
        position.setInterfaceType("Bond008");
        position.setUserId(SecurityUtil.getLoginUser().getUserName());
        if(StringUtil.equals(DictCodeListExt3.系统动作_修改.getValue(),decisionAct)){
            position.setbTotalTradeId(dto.getTotleTradeId());
        }
//        position.setIsCashSettle(dto.getIsCashPay());
        if(StringUtil.equals(dto.getTradeType(),DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue())){
            position.setIsOutCash(DictCodeList.是否_是.getValue());
        }else{
            position.setIsOutCash(DictCodeList.是否_否.getValue());
        }
        List<TrdSecLendCashPositionDto> cashList = new ArrayList<>();
        List<TrdSecLendSecPositionDto> targetList = new ArrayList<>();
        List<TrdSecLendSecPositionDto> secList = new ArrayList<>();

//        List<WflStlPayOrders> stlPayOrdersDtos = dto.getStlPayOrdersDtos();

        TrdSecLendCashPositionDto cash = new TrdSecLendCashPositionDto();
        cash.setPortfolioId(dto.getPortfolioId());
        String acct = EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(), dto.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
        cash.setCashAcctId(acct);
        cash.setTradeId(dto.getTotleTradeId());
        cash.setCashAmount(dto.getTradeAmt());
        cashList.add(cash);
        position.setPtlCashList(cashList);

//        TrdSecLendSecPositionDto target=new TrdSecLendSecPositionDto();
//        target.setPortfolioId(dto.getPortfolioId());
//        target.setTradeId(dto.getTotleTradeId());
//        target.setFinprodId( dto.getPledgeFinprodId());
//        target.setSecAcctId(dto.getSecManageAcctId()); // 证券账户
//        target.setSecAmount(dto.getShareAmt());
//        if(StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_融入.getValue())) {
//            target.setIntegrateSecAmount(dto.getShareAmt());
//            target.setOwnSecAmount(BigDecimal.ZERO);
//        }else{
//            target.setOwnSecAmount(dto.getShareAmt());
//            target.setIntegrateSecAmount(BigDecimal.ZERO);
//        }
//        targetList.add(target);
//        position.setPtlTargetList(targetList);
//        if(StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_融入.getValue())){
//            if(StringUtil.isNotEmpty(dto.getSecLendInfo())){
//                for(RepoInfo pledgeBondInfoDto:dto.getSecLendInfo()){
//                    TrdSecLendSecPositionDto secport=new TrdSecLendSecPositionDto();
//                    secport.setPortfolioId(dto.getPortfolioId());
//                    secport.setFinprodId( pledgeBondInfoDto.getPledgeFinprodId());
//                    secport.setSecAcctId(dto.getSecManageAcctId()); // 证券账户
//                    secport.setSecAmount(pledgeBondInfoDto.getQty());
//                    secport.setOwnSecAmount(pledgeBondInfoDto.getQty());
//                    secport.setIntegrateSecAmount(BigDecimal.ZERO);
//                    secport.setTradeId(dto.getBusiCode());
//                    secList.add(secport);
//                }
//            }
//            position.setPtlRelieveSecList(secList);
//        }
        PositionHttpRequest.send(position);
    }



    private void setWorkflowId(InvestTradeInfoDto tradeInfoDto, String workflowId) {
        tradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(workflowId);
        tradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);
        tradeInfoDto.getTrdDealTotalAdd().setWorkflowId(workflowId);
        tradeInfoDto.getTrdProductDealList().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(workflowId));
        tradeInfoDto.getTrdProductDealAddList().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(workflowId));
        tradeInfoDto.getPaTrdProductDealList().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(workflowId));
        tradeInfoDto.getPtlTradeList().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(workflowId));
        tradeInfoDto.getStlPayOrdersList().forEach(trdProductDeal -> trdProductDeal.setWorkflowId(workflowId));
    }

    @Override
    public void doUpdate(TrdBondLendTargetDuration002ProcessDto entity) {
        genFinProductTradeDto(entity);
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && com.joyintech.framework.util.StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(Collectors.toList()), entity.getTotleTradeId());
        }
        ProcessMark newEntity;
        try {
            newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdBondLendTargetDuration002ProcessDto.class, true);
            // 工作流ID
            newEntity.setWorkflowId(entity.getWorkflowId());
            newEntity.setStatus(entity.getStatus());
            newEntity.setBusinessData(JsonUtils.object2Json(entity));
            // 更新业务表数据
            // 操作留痕表
            WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        saveDataWhenStartWorkflow(entity, OperationType.MODIFY);
        保存持仓(entity, OperationType.MODIFY, true);
        String act = DictCodeListExt3.系统动作_新建.getValue();
        SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
        if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
            act = DictCodeListExt3.系统动作_修改.getValue();
        }
        String confirmOk = RequestUtil.getParam("confirmOk");
        if(StringUtil.isNotEmpty(confirmOk)){
            entity.setBalanceCheck("N");
        }
        try{
            sendNewPosition(entity,act,DictCodeListExt3.流程节点_流程提交.getValue());
        }catch (PositionException e){
            Message message = PositionUtil.ajaxErrorReturn(e);
            BaseMap bm = (BaseMap) message.getData();
            if(bm==null){
                WorkFlowUtil.showMessage( message.getMessage(), MessageType.错误消息);
            }
            String code = (String) bm.get("code");
            StringBuffer messageStr = (StringBuffer) bm.get("message");
            if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
            }
            if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
            }
        }
    }

    private void saveDataWhenStartWorkflow(TrdBondLendTargetDuration002ProcessDto entity, OperationType type) {
        if (type == OperationType.MODIFY) {
            baseTradeProductDealCommServiceImpl.deleteWflData(entity.getWorkflowId());
        }
        saveWflData(entity, type);
    }

    private void saveWflData(TrdBondLendTargetDuration002ProcessDto entity, OperationType type) {
        String 数据状态 = DictCodeList.数据状态_新建.getValue();
        if (type == OperationType.MODIFY) {
            数据状态 = DictCodeList.数据状态_修改.getValue();
        }
        else if (type == OperationType.DELETE) {
            数据状态 = DictCodeList.数据状态_删除.getValue();
        }
        else if (type == OperationType.REVOKE) {
            数据状态 = DictCodeList.数据状态_作废.getValue();
        }

        String 数据状态最终 = 数据状态;
        InvestTradeInfoDto tradeInfoDto = entity.getTradeInfoDto();

        WflTrdFinProductDealTotal wflTrdFinProductDealTotal = new WflTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdFinProductDealTotal, tradeInfoDto.getTrdFinProductDealTotal());
        wflTrdFinProductDealTotal.setWorkflowStatus(数据状态最终);
        wflTrdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId()); // 流程中设置成子组合
        wflTrdFinProductDealTotalMapper.insertSelective(wflTrdFinProductDealTotal);

        WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdFinDealTotal, tradeInfoDto.getPaTrdFinProductDealTotal());
        wflPaTrdFinDealTotal.setWorkflowStatus(数据状态最终);
        wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

        WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, tradeInfoDto.getTrdDealTotalAdd());
        wflTrdDealTotalAdd.setWorkflowStatus(数据状态最终);
        wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);

        tradeInfoDto.getTrdProductDealList().forEach(trdProductDeal -> {
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, trdProductDeal);
            wflTrdProductDeal.setWorkflowStatus(数据状态最终);
            wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        });

        tradeInfoDto.getTrdProductDealAddList().forEach(trdProductDeal -> {
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, trdProductDeal);
            wflTrdProductDealAdd.setWorkflowStatus(数据状态最终);
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId()); // 流程中设置成子组合
            wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        });

        tradeInfoDto.getPaTrdProductDealList().forEach(trdProductDeal -> {
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, trdProductDeal);
            wflPaTrdProductDeal.setWorkflowStatus(数据状态最终);
            wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        });

        tradeInfoDto.getStlPayOrdersList().forEach(trdProductDeal -> {
            WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, trdProductDeal);
            wflStlPayOrders.setWorkflowStatus(数据状态最终);
            wflStlPayOrdersMapper.insertSelective(wflStlPayOrders);
        });
    }

    /**
     * 2.0划款指令复制 <功能详细描述>
     * 
     * @param entity 业务实体
     * @return 转换的划款指令
     * @see
     * @since 1.0
     */
    private StlPayOrders gen划款指令流程(TrdBondLendTargetDuration002ProcessDto entity) {
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
        StlPayOrders stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            stlPayOrders = dto;
            stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
            stlPayOrders.setTradeId(entity.getTotleTradeId());
            stlPayOrders.setStatus(DictCodeList.数据状态_新建.getValue());
            stlPayOrders.setWorkflowId(entity.getWorkflowId());
        }
        return stlPayOrders;
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        TrdBondLendTargetDuration002ProcessDto entity = getBussinessEntityByWorkflowId(workflowId);
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            //发送头寸
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(),DictCodeListExt1.子系统代码_交易.getValue(),DictCodeListExt4.头寸业务类型_银行间债券借贷首期.getValue(),entity.getTradeInfoDto().getTrdFinProductDealTotal().getPs());
        }
    }

    public CommentFunction<TrdBondLendTargetDuration002ProcessDto, String> 生成业务说明() {
        return entity -> WorkFlowUtil.concatComment(
            this.getMessage("TrdBondDuration001.finprodName") + GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodAbbr()
                                                    + GlobalConstant.CLOSE_PARENTHESIS,
            "资产类型" + GlobalConstant.OPEN_PARENTHESIS + entity.getTradeTypeString() + GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage(
                "TrdBondDuration001.busiCode") + GlobalConstant.OPEN_PARENTHESIS + entity.getTotleTradeId() + GlobalConstant.CLOSE_PARENTHESIS);
    }

    public Map<String, Object> genParams(TrdBondLendTargetDuration002ProcessDto entity) {
        return new HashMap<String, Object>() {
            {
                // 调整后的计算结束日
                put("mDate", entity.getMdate());
                put("vDate", entity.getVdate());
                // 金融产品代码
                put("finprodId", entity.getFinprodId());
                put("statusValid", entity.getStatusValid());
                put("busiCode", entity.getTotleTradeId());
                put("secAcctId", entity.getSecManageAcctId());
            }
        };
    }

    public void genFinProductTradeDto(TrdBondLendTargetDuration002ProcessDto entity) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        InvestTradeInfoDto dto = new InvestTradeInfoDto();
        entity.setTradeInfoDto(dto);
        dto.setTrdFinProductDealTotal(生成汇总表数据(entity));
        dto.setPaTrdFinProductDealTotal(生成汇总扩展表数据(entity));
        dto.setTrdDealTotalAdd(生成汇总附属表数据(entity));
        dto.setTrdProductDealList(Lists.newArrayList());
        dto.setTrdProductDealAddList(Lists.newArrayList());
        dto.setPaTrdProductDealList(Lists.newArrayList());
        dto.setPtlTradeList(Lists.newArrayList());
        生成交易表数据(entity);
        dto.setStlPayOrdersList(Arrays.asList(gen划款指令流程(entity)));
    }

    private void 生成交易表数据(TrdBondLendTargetDuration002ProcessDto entity) {
        if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(entity.getTradeType())) {
            根据投资目的生成交易表(entity, DictCodeListExt1.投资目的_卖空.getValue(), BigDecimal.ONE, ArithmeticUtil.divide(entity.getAmount(), 100, 2));
        }
        else if (DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue().equals(entity.getTradeType())) {
            Map<String, BigDecimal> 融出券投资目的的持仓 = 检索融出券投资目的持仓(entity.getMpFinprodId(), entity.getActPortfolioId());
            if (融出券投资目的的持仓 != null) {
                BigDecimal 总持仓 = 融出券投资目的的持仓.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                String 持仓最大的投资目的 = null;
                BigDecimal 最大持仓 = BigDecimal.ZERO;
                for (Map.Entry<String, BigDecimal> entry : 融出券投资目的的持仓.entrySet()) {
                    String 投资目的 = entry.getKey();
                    BigDecimal 持仓 = entry.getValue();
                    if (持仓 != null && 持仓.compareTo(BigDecimal.ZERO) > 0) {
                        if (持仓.compareTo(最大持仓) > 0) {
                            最大持仓 = 持仓;
                            持仓最大的投资目的 = 投资目的;
                        }
                        根据投资目的生成交易表(entity, 投资目的, ArithmeticUtil.divide(持仓, 总持仓, 12), 持仓);
                    }
                }
                List<TrdProductDeal> trdProductDeallist = entity.getTradeInfoDto().getTrdProductDealList();

                BigDecimal totlePrinAmt = ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt());
                BigDecimal totleIntAmt = entity.getIntAmt();

                BigDecimal unlessMaxPrinAmt = BigDecimal.ZERO;
                BigDecimal unlessMaxIntAmt = BigDecimal.ZERO;
                String totlePs = getPsFromCashType(entity.getCashType(), entity.getTradeType());
                for (TrdProductDeal trdProductDeal : trdProductDeallist) {
                    if (!trdProductDeal.getInvAim().equals(持仓最大的投资目的)) {
                        unlessMaxPrinAmt = ArithmeticUtil.doAdd(unlessMaxPrinAmt, trdProductDeal.getPrinAmt());
                        unlessMaxIntAmt = ArithmeticUtil.doAdd(unlessMaxIntAmt, trdProductDeal.getIntAmt());
                    }
                }
                for (TrdProductDeal trdProductDeal : trdProductDeallist) {
                    if (trdProductDeal.getInvAim().equals(持仓最大的投资目的)) {
                        // 本金的那条数据
                        if (DictCodeList.交易方向_本金返还.getValue().equals(trdProductDeal.getPs())
                                || DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal.getPs())) {
                            trdProductDeal.setPrinAmt(ArithmeticUtil.subtract(totlePrinAmt, unlessMaxPrinAmt));
                            trdProductDeal.setTradeAmt(trdProductDeal.getPrinAmt());
                        }
                        else {
                            trdProductDeal.setIntAmt(ArithmeticUtil.subtract(totleIntAmt, unlessMaxIntAmt));
                            trdProductDeal.setTradeAmt(trdProductDeal.getIntAmt());
                        }
                        trdProductDeal.setFpriceAmt(ArithmeticUtil.doAdd(trdProductDeal.getPrinAmt(), trdProductDeal.getIntAmt()));
                    }
                }
            }
        }
    }

    public Map<String, BigDecimal> 检索融出券投资目的持仓(String mpFinprodId, String portfolioId) {
        List<PtlSecPositionFlow> finAssetIntInfos = restTemplate.getForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondLendTargetDuration/findAmountGroupByInv/" + mpFinprodId + "/" + portfolioId,
            PtlSecPositionFlow.class);
        if (finAssetIntInfos != null && !finAssetIntInfos.isEmpty()) {
            return finAssetIntInfos.stream().collect(
                JoyinCollectors.toMap(PtlSecPositionFlow::getInvAim, PtlSecPositionFlow::getAmount));
        }
        return null;
    }

    private List<TrdProductDeal> 根据投资目的生成交易表(TrdBondLendTargetDuration002ProcessDto entity, String 投资目的, BigDecimal 分配比例, BigDecimal 持仓) {
        List<TrdProductDeal> trdProductDeallist = entity.getTradeInfoDto().getTrdProductDealList();
        List<String> cashIds = StringUtil.splitToList(",", entity.getCashId());
        List<String> cashTypes = StringUtil.splitToList(",", entity.getCashType());
        String ps = getPsFromCashType(entity.getCashType(), entity.getTradeType());
        for (int i = 0; i < cashIds.size(); i++ ) {
            // 只收息的情况
            if (DictCodeList.交易方向_收息.getValue().equals(ps) || DictCodeList.交易方向_付息.getValue().equals(ps)) {
                TrdProductDeal deal = 生成收息交易(cashIds.get(i), cashTypes.get(i), entity, 投资目的, 分配比例);
                deal.setAccrNum(持仓);
                trdProductDeallist.add(deal);
            }
            // 还本或到期，还本可能存在收息
            else if (DictCodeList.交易方向_还本.getValue().equals(ps) || DictCodeList.交易方向_到期.getValue().equals(ps)) {
                if (DictCodeList.现金流类型_到期还本.getValue().equals(cashTypes.get(i)) || DictCodeList.现金流类型_中间还本.getValue().equals(cashTypes.get(i))) {
                    TrdProductDeal deal = 生成还本交易(cashIds.get(i), cashTypes.get(i), entity, 投资目的, 分配比例);
                    deal.setAccrNum(持仓);
                    deal.setShareAmt(持仓);
                    trdProductDeallist.add(deal);
                }
                else if ((DictCodeList.现金流类型_到期收息.getValue().equals(cashTypes.get(i)) || DictCodeList.现金流类型_中间收息.getValue().equals(cashTypes.get(i)))
                        && entity.getIntAmt() != null && entity.getIntAmt().compareTo(BigDecimal.ZERO) > 0) {
                    TrdProductDeal deal1 = 生成收息交易(cashIds.get(i), cashTypes.get(i), entity, 投资目的, 分配比例);
                    deal1.setAccrNum(持仓);
                    trdProductDeallist.add(deal1);
                }
            }
        }
        return trdProductDeallist;
    }

    private TrdProductDeal 生成还本交易(String 现金流代码, String 现金流类型, TrdBondLendTargetDuration002ProcessDto entity, String 投资目的,
                                  BigDecimal 分配比例) {
        TrdProductDeal deal = getTrdProductDeal(现金流代码, 现金流类型, entity, 投资目的, 分配比例);
        deal.setPrinAmt(按比例分配(ArithmeticUtil.subtract(entity.getTradeAmt(), entity.getIntAmt()), 分配比例));
        deal.setIntAmt(null);
        deal.setFpriceAmt(null);
        deal.setTradeAmt(deal.getPrinAmt());
        deal.setFpriceAmt(ArithmeticUtil.doAdd(deal.getPrinAmt(), deal.getIntAmt()));

        TrdProductDealAdd dealAdd = getTrdProductDealAdd(entity, deal);
        entity.getTradeInfoDto().getTrdProductDealAddList().add(dealAdd);

        PaTrdProductDeal paTrdProductDeal = getPaTrdProductDeal(entity, deal);
        entity.getTradeInfoDto().getPaTrdProductDealList().add(paTrdProductDeal);

        PtlTrade ptlTrade = getPtlTrade(entity, deal);
        entity.getTradeInfoDto().getPtlTradeList().add(ptlTrade);

        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            ptlTrade1.setPortfolioId(entity.getActPortfolioId());
            entity.getTradeInfoDto().getPtlTradeList().add(ptlTrade1);
        }
        return deal;
    }

    private TrdProductDeal 生成收息交易(String 现金流代码, String 现金流类型, TrdBondLendTargetDuration002ProcessDto entity, String 投资目的,
                                  BigDecimal 分配比例) {
        TrdProductDeal deal = getTrdProductDeal(现金流代码, 现金流类型, entity, 投资目的, 分配比例);
        deal.setFpriceAmt(ArithmeticUtil.doAdd(deal.getPrinAmt(), deal.getIntAmt()));

        TrdProductDealAdd dealAdd = getTrdProductDealAdd(entity, deal);
        entity.getTradeInfoDto().getTrdProductDealAddList().add(dealAdd);

        PaTrdProductDeal paTrdProductDeal = getPaTrdProductDeal(entity, deal);
        entity.getTradeInfoDto().getPaTrdProductDealList().add(paTrdProductDeal);

        PtlTrade ptlTrade = getPtlTrade(entity, deal);
        entity.getTradeInfoDto().getPtlTradeList().add(ptlTrade);

        if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(ptlTrade1, ptlTrade);
            ptlTrade1.setPortfolioId(entity.getActPortfolioId());
            entity.getTradeInfoDto().getPtlTradeList().add(ptlTrade1);
        }
        return deal;
    }

    private PtlTrade getPtlTrade(TrdBondLendTargetDuration002ProcessDto entity, TrdProductDeal deal) {
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(entity.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        ptlTrade.setInvAim(deal.getInvAim());
        ptlTrade.setSecManageAcctId(deal.getSecManageAcctId());
        ptlTrade.setSettleDate(deal.getTradeDate());
        ptlTrade.setPortfolioId(entity.getPortfolioId());
        ptlTrade.setBusiNo(deal.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        return ptlTrade;
    }

    private PaTrdProductDeal getPaTrdProductDeal(TrdBondLendTargetDuration002ProcessDto entity, TrdProductDeal deal) {
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(deal.getTradeId());
        paTrdProductDeal.setPaBussType(entity.getTradeType());
        paTrdProductDeal.setWorkflowId(entity.getWorkflowId());
        return paTrdProductDeal;
    }

    private TrdProductDealAdd getTrdProductDealAdd(TrdBondLendTargetDuration002ProcessDto entity, TrdProductDeal deal) {
        TrdProductDealAdd dealAdd = new TrdProductDealAdd();
        dealAdd.setTradeId(deal.getTradeId());
        dealAdd.setPortfolioId(entity.getActPortfolioId());
        dealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dealAdd.setMpFinprodId(entity.getMpFinprodId());
        dealAdd.setMpBranch(Long.parseLong(entity.getBranch()));
        return dealAdd;
    }

    private TrdProductDeal getTrdProductDeal(String 现金流代码, String 现金流类型, TrdBondLendTargetDuration002ProcessDto entity,
                                             String 投资目的, BigDecimal 分配比例) {
        TrdProductDeal deal = new TrdProductDeal();
        deal.setTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
        deal.setTotleTradeId(entity.getTotleTradeId());
        deal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        deal.setFinprodId(entity.getFinprodId());
        deal.setFinprodType2(DictCodeListExt1.金融产品类型_债券.getValue());
        deal.setCcy(entity.getCcy());
        deal.setPs(现金流类型To交易方向(现金流类型, entity.getTradeType()));
        deal.setBranch(生成分支序号(deal.getPs(), entity.getMdate()));
        deal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        deal.setVdate(DateUtil.parse(entity.getVdate()));
        deal.setMdate(DateUtil.parse(entity.getMdate()));
        deal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        deal.setIntAmt(按比例分配(entity.getIntAmt(), 分配比例));
        deal.setIsClean(DictCodeList.是否_是.getValue());
        deal.setTradeAmt(deal.getIntAmt());
        deal.setTradeMarket(entity.getTradeMarket());
        deal.setCashId(现金流代码);
        deal.setIsOutTrade(DictCodeList.是否_是.getValue());
        deal.setInvAim(投资目的);
        deal.setRegistOrg(entity.getRegistOrg());
        deal.setSecManageAcctId(entity.getSecManageAcctId());
        deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        deal.setIsCancel(DictCodeList.是否_否.getValue());
        deal.setCounterId(entity.getCounterId());
        deal.setTgBusiType(entity.getTradeType());
        deal.setParValue(entity.getUnitPrinAmt());
        return deal;
    }

    private BigDecimal 按比例分配(BigDecimal value, BigDecimal 分配比例) {
        return ArithmeticUtil.round(ArithmeticUtil.multiply(value, 分配比例), 2, RoundingMode.HALF_UP);
    }

    private Long 生成分支序号(String ps, String mdate) {
        String stringBranch = mdate.replaceAll("-", "");
        if (DictCodeList.交易方向_利息返还.getValue().equals(ps) || DictCodeList.交易方向_收息到账.getValue().equals(ps)) {
            stringBranch = stringBranch + "02";
        }
        else {
            stringBranch = stringBranch + "01";
        }
        return Long.parseLong(stringBranch);
    }

    private String 现金流类型To交易方向(String 现金流类型, String tradeType) {
        String totlePs = getPsFromCashType(现金流类型, tradeType);
        if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(tradeType)) {
            if (DictCodeList.交易方向_付息.getValue().equals(totlePs)) {
                // 类型为融入债券付息还本（付）时，汇总表交易方向为付息时，登录 数据字典.交易方向.融入债券利息返还
                return DictCodeList.交易方向_利息返还.getValue();
            }
            // 类型为融入债券付息还本（付）时，汇总表交易方向为还本/到期时，登录 数据字典.交易方向.融入债券本金返还
            else if (DictCodeList.交易方向_到期.getValue().equals(totlePs) || DictCodeList.交易方向_还本.getValue().equals(totlePs)) {
                return DictCodeList.交易方向_本金返还.getValue();
            }
        }
        else if (DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue().equals(tradeType)) {
            if (DictCodeList.交易方向_收息.getValue().equals(totlePs)) {
                // 类型为融出债券收息还本（收）时，汇总表交易方向为收息时，登录数据字典.交易方向.收息到账
                return DictCodeList.交易方向_收息到账.getValue();
            }
            // 类型为融出债券收息还本（收）时，汇总表交易方向为还本/到期时，登录数据字典.交易方向.还本到账
            else if (DictCodeList.交易方向_到期.getValue().equals(totlePs) || DictCodeList.交易方向_还本.getValue().equals(totlePs)) {
                return DictCodeList.交易方向_还本到账.getValue();
            }
        }
        return null;

    }

    private TrdDealTotalAdd 生成汇总附属表数据(TrdBondLendTargetDuration002ProcessDto entity) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(entity.getTotleTradeId());
        trdDealTotalAdd.setMpFinprodId(entity.getMpFinprodId());
        trdDealTotalAdd.setMpBranch(Long.parseLong(entity.getBranch()));
        trdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
        return trdDealTotalAdd;
    }

    private PaTrdFinProductDealTotal 生成汇总扩展表数据(TrdBondLendTargetDuration002ProcessDto entity) {
        PaTrdFinProductDealTotal paTrdFinProductDealTotal = new PaTrdFinProductDealTotal();
        paTrdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
        paTrdFinProductDealTotal.setBranch(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        paTrdFinProductDealTotal.setUnitInt(entity.getUnitIntAmt());
        paTrdFinProductDealTotal.setUnitPrin(entity.getUnitPrinAmt());
        paTrdFinProductDealTotal.setPrin(entity.getPrinAmt());
        paTrdFinProductDealTotal.setRate(ArithmeticUtil.divide(entity.getRangeYield(), new BigDecimal(100), 12));
        paTrdFinProductDealTotal.setTheoryAmt(entity.getTheoryAmt());
        paTrdFinProductDealTotal.setTheoryInt(entity.getTheoryInt());
        paTrdFinProductDealTotal.setTheoryPrin(entity.getTheoryPrinAmt());
        paTrdFinProductDealTotal.setTheoryPayDate(DateUtil.parse(entity.getPayDate()));
        paTrdFinProductDealTotal.setPaBussType(entity.getTradeType());
        paTrdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        return paTrdFinProductDealTotal;
    }

    private TrdFinProductDealTotal 生成汇总表数据(TrdBondLendTargetDuration002ProcessDto entity) {
        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getTotleTradeId());
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_债券.getValue());
        trdFinProductDealTotal.setTradeDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setVdate(DateUtil.parse(entity.getVdate()));
        trdFinProductDealTotal.setMdate(DateUtil.parse(entity.getMdate()));
        trdFinProductDealTotal.setSettleDate(DateUtil.parse(entity.getTradeDate()));
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setShareAmt(entity.getAmount());
        trdFinProductDealTotal.setPrinAmt(entity.getTheoryPrinAmt());
        trdFinProductDealTotal.setIntAmt(entity.getIntAmt());
        trdFinProductDealTotal.setFpriceAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());
        trdFinProductDealTotal.setPs(getPsFromCashType(entity.getCashType(), entity.getTradeType()));
        trdFinProductDealTotal.setBranch(生成汇总表分支序号(trdFinProductDealTotal.getPs(), entity.getMdate()));
        trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        trdFinProductDealTotal.setCounterId(entity.getCounterId());
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
//        trdFinProductDealTotal.setPayDate(DateUtil.parse(entity.getPayDate()));
        trdFinProductDealTotal.setTgBusiType(entity.getTradeType());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
        return trdFinProductDealTotal;
    }

    private Long 生成汇总表分支序号(String ps, String mdate) {
        String stringBranch = mdate.replaceAll("-", "");
        if (DictCodeList.交易方向_付息.getValue().equals(ps) || DictCodeList.交易方向_收息.getValue().equals(ps)) {
            stringBranch = stringBranch + "02";
        }
        else {
            stringBranch = stringBranch + "01";
        }
        return Long.parseLong(stringBranch);
    }

    private static String getPsFromCashType(String cashType, String tradeType) {
        if (DictCodeList.现金流类型_中间收息.getValue().equals(cashType)) {
            if (DictCodeListExt3.平安业务类型_融入债券付息还本_付.getValue().equals(tradeType)) {
                return DictCodeList.交易方向_付息.getValue();
            }
            else if (DictCodeListExt3.平安业务类型_融出债券付息还本_收.getValue().equals(tradeType)) {
                return DictCodeList.交易方向_收息.getValue();
            }
        }
        else if (cashType.contains(DictCodeList.现金流类型_中间还本.getValue())) {
            return DictCodeList.交易方向_还本.getValue();
        }
        else if (cashType.contains(DictCodeList.现金流类型_到期还本.getValue())) {
            return DictCodeList.交易方向_到期.getValue();
        }
        return null;
    }

    @Override
    public void doBack(TrdBondLendTargetDuration002ProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    @Override
    public void doRevoke(TrdBondLendTargetDuration002ProcessDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    public InvestTradeInfoDto findFinProductTradeDto(String totleTradeId) {
        return restTemplate.getForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondLendTargetDuration/findFinProductTradeDto/" + totleTradeId,
                InvestTradeInfoDto.class);
    }
}
