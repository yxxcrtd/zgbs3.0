package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsBondPortfolioCashRequest;
import com.joyintech.tams.business.common.dto.CfetsBondPositionRequest;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoResponse;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.EtrCommonFinProductInfoService;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.business.common.util.NumericUtil;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bondfirstbid.dao.CfetsEtrBondFirstBid002Mapper;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsBiddingProductDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.EtrSinglePortfolioDto;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustBackupMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustBidPriceMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustDetailMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.WflEtrEntrust;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service
public class CfetsEtrBondFirstBid002Service extends CommonExportService<CfetsEtrBondFirstBid002Dto> implements BaseProcessService<CfetsEtrBondFirstBid002Dto> {

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    @Autowired
    private CfetsEtrBondFirstBid002Mapper cfetsEtrBondFirstBid002Mapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private EtrEntrustBidPriceMapper etrEntrustBidPriceMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired(required = false)
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired
    private BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;

    @Autowired
    private EtrCommonFinProductInfoService etrCommonFinProductInfoService;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    @Autowired
    private CfetsEtrBondFirstBid001Service cfetsEtrBondFirstBid001Service;

    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    @Override
    public void doInsert(CfetsEtrBondFirstBid002Dto entity, OperationType operationType) {
        if ("submit".equals(entity.getOperateType())) {
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondFirstBidDtos())) {

                    for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                        WFlowCommonService.insertEtrEntrustRe(dto.getEntrustId(), entity.getTotalEtrustId());
                        dto.setWorkflowId(entity.getWorkflowId());
                        BaseEtrInvestInfoDto baseEtrDto = initBaseEtrInfoFlowDto(dto);
                        doInsertFlowMethod(entity.getOperateType(), baseEtrDto);
                        // 老头寸处理
                        this.dealOldPosition(dto, false);
                    }
                    // 发送头寸
                    this.sendPositionMethod(entity, StringUtil.equals(entity.getSystemAct(), DictCodeListExt3.系统动作_新建.getValue()), null);
                }

            }
        }

        // 操作履历由切面实现
    }

    /**
     * 老头寸处理
     *
     * @param entity
     * @param isBack
     */
    private void dealOldPosition(CfetsEtrBondFirstBid002Dto entity, boolean isBack) {
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))
                && StringUtil.equals(entity.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
            if (isBack) {
                //插入旧头寸
                AllTradeCommonUtil.deletePosition(null, entity.getEntrustId());
            } else {
                doInsetOrUpdateForEtrPositionHold(entity);
            }
        }
    }

    /**
     * 插入和修改时，指令现金流水处理
     * @param entity
     */
    private void doInsetOrUpdateForEtrPositionHold(CfetsEtrBondFirstBid002Dto entity) {
        // 构造单组合单资产DTO
        List<EtrSinglePortfolioDto> etrSinglePortfolioDtoList = getEtrSinglePortfolio(entity);
        // 指令持仓流水 、现金流水处理
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();// 组合持仓流水、组合现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();// 现金流水

        if (DictCodeList.交易方向_买入.getValue().equals(entity.getPs())) {

            for (EtrSinglePortfolioDto etrSinglePortfolioDto:etrSinglePortfolioDtoList) {
                EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合现金流水DTO
                etrCashPositionFlowDtoUnit.setSeqno(getSeqNo());
                etrCashPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
                etrCashPositionFlowDtoUnit.setAcctId(etrSinglePortfolioDto.getDealCurAcct());// 资金账户
                etrCashPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
                etrCashPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
                etrCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
                etrCashPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().negate());
                etrCashPositionFlowDtoUnit.setOrderId(etrSinglePortfolioDto.getEntrustId());// 业务编号
                etrCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
                etrCashPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());// 冲正标识
                etrCashPositionFlowDtoUnit.setOffsetId(etrSinglePortfolioDto.getEntrustId());// 冲销业务编号

                etrCashPositionFlows.add(etrCashPositionFlowDtoUnit);
            }
        }
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setDealEtr(true);
        etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> {
            etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache());
            etrCashPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
        });
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
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
     * 构造单组合单资产DTO
     * @param entity
     * @return
     */
    private List<EtrSinglePortfolioDto> getEtrSinglePortfolio(CfetsEtrBondFirstBid002Dto entity) {
        List<EtrSinglePortfolioDto> singlePortfolioDtoList = new ArrayList<>();
        for (CfetsBiddingProductDto product : entity.getBiddingProductList()) {
            EtrSinglePortfolioDto singlePortfolioDto = new EtrSinglePortfolioDto();
            singlePortfolioDto.setPortfolioId(product.getPortfolioId());
            singlePortfolioDto.setEntrustId(entity.getEntrustId());
            singlePortfolioDto.setEntrustAmt(product.getBidPrinAmt());
            String dealCurAcct = EtrCommonCashPositionDealService.select组合托管户(product.getPortfolioId(), entity.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
            singlePortfolioDto.setDealCurAcct(dealCurAcct);

            singlePortfolioDtoList.add(singlePortfolioDto);
        }

        return singlePortfolioDtoList;
    }

    /**
     * 发送头寸
     *
     * @param dto
     * @param isSubmit
     * @param confirmOk
     */
    private void sendPositionMethod(CfetsEtrBondFirstBid002Dto dto, boolean isSubmit, String confirmOk) {

        for (CfetsEtrBondFirstBid002Dto entity : dto.getEtrInvestBondFirstBidDtos()) {
            List<CfetsBondPortfolioCashRequest> ptlList = new ArrayList<>();
            for (CfetsBiddingProductDto productDto : entity.getBiddingProductList()) {
                //调用头寸
                CfetsBondPortfolioCashRequest posport = new CfetsBondPortfolioCashRequest();
                posport.setPortfolioId(productDto.getPortfolioId());
                posport.setEtrId(entity.getEntrustId());
                posport.setTradeId(entity.getEntrustId());
                //posport.setCashAcctId(productDto.getSecAcctId());
                if (StringUtil.isNotEmpty(productDto.getSecAcctId())) {
                    posport.setSecAcctId(productDto.getSecAcctId()); // 证券户
                }
                if (StringUtil.isNotEmpty(productDto.getAcctNo())) {
                    posport.setCashAcctId(productDto.getAcctNo());
                    posport.setSecAcctId(productDto.getSecAcctId()); // 证券户
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("acctNo",productDto.getAcctNo());
//                    map.put("portfolioId",productDto.getPortfolioId());
//                    map.put("tradeMarket",entity.getTradeMarket());
//                    List<SelectOptionDto> tempList= cfetsEtrBondFirstBid001Service.getPtlSecAcctInfo(map);
//                    SelectOptionDto temp = tempList.stream().filter(x -> Objects.equals(x.getId(),productDto.getAcctNo())).findFirst().orElse(null);
//                    ArrayList secMap = (ArrayList) temp.getObject();
//                    posport.setSecAcctId(((HashMap) secMap.get(0)).get("secAcctId").toString()); // 证券户
                } else {
                    if (StringUtil.isNotEmpty(productDto.getPortfolioId())) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("portfolioId", productDto.getPortfolioId());
                        map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                        String cashAcctId = joyinbusinessCommonService.qryPortfolioCashAcctId(map);
                        if (StringUtil.isNotEmpty(cashAcctId)) {
                            posport.setCashAcctId(cashAcctId); // 托管户
                        }
                    }
                }


                posport.setSecAmount(productDto.getBidPrinAmt());
                posport.setCashAmount(productDto.getBidPrinAmt());
                posport.setIntegrateSecAmount(BigDecimal.ZERO.toString());
                posport.setOwnSecAmount(productDto.getBidPrinAmt().toString());
                ptlList.add(posport);
            }
            //调用头寸
            CfetsBondPositionRequest req = new CfetsBondPositionRequest();
            req.setInterfaceType("Bond001");//报文类型
            req.setUserId(SecurityUtil.getLoginUser().getUserName());
            req.setTotalTradeId(dto.getTotalEtrustId());
            req.setFlowStatus(DictCodeListExt3.流程节点_流程提交.getValue());
            req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
            req.setWorkFlowId(entity.getWorkflowId());
            String balanceCheck = DictCodeList.是否_是.getValue();
            if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                    && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                    || StringUtil.isNotEmpty(confirmOk)) {
                balanceCheck = DictCodeList.是否_否.getValue();
            }
            req.setBalanceCheck(balanceCheck);
            if (StringUtil.equals(entity.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级申购.getValue());
            } else {
                req.setBusiType(DictCodeListExt3.头寸业务类型_债券一级投标.getValue());
            }

            //兼容场内场外
            req.setTradePlace(entity.getTradeMarket());
            req.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());
            req.setBusiAct(DictCodeListExt3.业务动作_投资指令.getValue());
            req.setPs(DictCodeList.交易方向_买入.getValue());
            if (isSubmit) {
                req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
                req.setSystemAct(entity.getSystemAct());
            } else {
                req.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
                req.setSystemAct(entity.getSystemAct());
                req.setbTotalTradeId(dto.getTotalEtrustId());
            }
            req.setCdate(entity.getEntrustDate());
            req.setFinprodId(entity.getFinprodId());
            req.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            req.setPtlList(ptlList);
            PositionHttpRequest.send(req);
        }


    }

    @Override
    public void doUpdate(CfetsEtrBondFirstBid002Dto entity) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        //代办提交，头寸校验，点击确认传值参数
        String confirmOk = RequestUtil.getParam("confirmOk");
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            boolean isSubmit = true;
            SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
            if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                isSubmit = false;
            }
            this.packageEquityForSubmit(entity);
            if ("submit".equals(entity.getOperateType()) && isSubmit) {
                List<CfetsEtrBondFirstBid002Dto> dataList = entity.getEtrInvestBondFirstBidDtos().stream()
                        .filter(dto -> StringUtil.isNotEmpty(dto.getEntrustId())).collect(Collectors.toList());
                try {
                    AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(dataList, DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue(), BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(), true, null);
                    if (StringUtil.isNotEmpty(ar)) {
                        throw new BusinessException(ar.getMessage());
                    }
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                }
                EntrustRdsCommonAop.riskListToChildren(entity.getRiskTrialRetDto(), entity.getEtrInvestBondFirstBidDtos(), (dto, list) -> dto.setRiskTrialRetDto(list));
            }

            WFlowCommonService.deleteFlow(entity.getWorkflowId());
            for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                dto.setWorkflowId(entity.getWorkflowId());
                BaseEtrInvestInfoDto base = initBaseEtrInfoFlowDto(dto);
                WFlowCommonService.insertFlow(base);

                // 老头寸处理
                this.dealOldPosition(dto, false);
            }
            //发送头寸
            try {
                sendPositionMethod(entity, isSubmit, confirmOk);
            } catch (Exception e) {
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

    /**
     * 保存提交时，后台数据转换
     * @param dto
     */
    private CfetsEtrBondFirstBid002Dto packageEquityForSubmit(CfetsEtrBondFirstBid002Dto dto) {

        //查询资产信息
        CfetsEtrFinProductInfoResponse finProductInfo = new CfetsEtrFinProductInfoResponse();
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            CfetsEtrFinProductInfoRequest productInfoRequest = new CfetsEtrFinProductInfoRequest();
            productInfoRequest.setFinprodId(dto.getFinprodId());
            finProductInfo = etrCommonFinProductInfoService.findFinProductInfo(null, null, productInfoRequest);
        }

        if (finProductInfo != null) {
            dto.setTradeMarket(finProductInfo.getTradeMarket());
            //dto.setCalendarId(finProductInfo.getCalendarId());
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            dto.setFinprodAbbr(finProductInfo.getFinprodAbbr());
            dto.setFinprodMarketId(finProductInfo.getFinprodMarketId());
            //审批时 流程数据初始化
            dto.setIssueClass(finProductInfo.getIssueClass());
            if (StringUtil.equals(finProductInfo.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                dto.setIssueDate(finProductInfo.getIssueDate());
                dto.setPayDate(finProductInfo.getPayDate());
                dto.setIssuePrice(new BigDecimal(finProductInfo.getIssuePrice()));
                dto.setReferYield(finProductInfo.getReferYield());

            } else {
                //查询所有的对手方
                List<SelectOptionDto> counterDtos = joyinbusinessCommonMapper.queryCounterPartyList(null);
                Map<String, String> counterMap = counterDtos.stream()
                        .collect(Collectors.toMap(SelectOptionDto::getId, SelectOptionDto::getText));

                dto.setTenderTarget(finProductInfo.getTenderTarget());
                dto.setTenderSdate(finProductInfo.getTenderSdate());
                dto.setTenderEdate(finProductInfo.getTenderEdate());
                dto.setIssueDate(finProductInfo.getIssueDate());
                dto.setPayEdate(finProductInfo.getPayEdate());
                dto.setCounterName(counterMap.get(dto.getCounterId()));
                dto.setTenderTarget(finProductInfo.getTenderTarget());
            }
        }
        dto.setEntrustId(dto.getEtrInvestBondFirstBidDtos().get(0).getEntrustId());
        //流程中数据对象重复  取值有问题 这个处理为空
        dto.setEtrInvestBondFirstBidDtos(null);

        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        //dto.setInvManagerId("li02");
        dto.setEntrustNum(dto.getTotalBiddingNum());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        dto.setEntrustDate(dto.getEntrustDate() != null ? dto.getEntrustDate() : DateUtil.platDateFromCache());

        List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
        List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidList = new ArrayList<>();
        //场外
        if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
            //报价发行时，按照证券账户维度拆分为多条指令信息
            if (DictCodeListExt4.新债_发行方式_报价发行.getValue().equals(dto.getIssueClass())) {
                Map<String, List<CfetsBiddingProductDto>> secAcctIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getSecAcctId));
                for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : secAcctIdMap.entrySet()) {
                    CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
                    BeanUtil.copyPropertiesIgnoreNull(dd, dto);
                    dd.setBiddingProductList(null);
                    dd.setBiddingProductList(entry.getValue());
                    dd.setSecAcctId(entry.getKey());
                    //汇总投标券面总额 重新计算
                    BigDecimal totalNum = dd.getBiddingProductList().stream().map(CfetsBiddingProductDto::getBidPrinAmt)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    dd.setTotalBiddingNum(totalNum);
                    //针对退回指令，查询指令下达时间
                    if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                        //根据指令id查询出该资产是退回指令，且该指令的下达时间
                        CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(dto.getEntrustId());
                        if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                            dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                        }
                        dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                    } else {
                        dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                    }

                    //投标日前下的指令，指令结束日为投标日;投标日后下的指令，指令结束日为指令日期
                    if (DateUtil.compareDate(dto.getEntrustDate(), dto.getIssueDate()) >= 0) {
                        dd.setMdate(dto.getIssueDate());
                    } else {
                        dd.setMdate(dto.getEntrustDate());
                    }

                    etrInvestBondFirstBidList.add(dd);
                }

            } else {
                CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
                BeanUtil.copyPropertiesIgnoreNull(dd, dto);
                //针对退回指令，查询指令下达时间
                if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                    //根据指令id查询出该资产是退回指令，且该指令的下达时间
                    CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(dto.getEntrustId());
                    if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                        dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                    }
                    dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                } else {
                    dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                }

                etrInvestBondFirstBidList.add(dd);
            }

        }


        //场内
        if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {

            CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd, dto);
            //针对退回指令，查询指令下达时间
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                //根据指令id查询出该资产是退回指令，且该指令的下达时间
                CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(dto.getEntrustId());
                if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                    dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                }
                dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
            } else {
                dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            }

            etrInvestBondFirstBidList.add(dd);
        }

        //风险
        CfetsEtribdoRiskcalResponse riskDto = dto.getCfetsEtribdoRiskcalReturn();
        List<RiskTrialRetDto> riskTrialRetDto = new ArrayList<>();
        if (StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())) {
            for (CfetsEtribdoRiskcalResponse it : riskDto.getRiskcalReturnList()) {
                RiskTrialRetDto t = new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t, it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        if (StringUtil.isNotEmpty(dto.getEntrustId())) {
            dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);
        dto.setEtrInvestBondFirstBidDtos(etrInvestBondFirstBidList);
        return dto;
    }

    @Override
    public void doComplete(CfetsEtrBondFirstBid002Dto entity, OperationType operationType) {
        entity.setOperateType(operationType.toString());
        entity.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }
        //删除流程中数据
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
            try {
                doCompleteMethod(dto, entity.getOperateType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("新建指令失败", e);
            }
        }
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
    }

    /**
     * 退回
     * @param entity
     */
    @Override
    public void doBack(CfetsEtrBondFirstBid002Dto entity) {
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondFirstBidDtos())) {
            for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                this.dealOldPosition(dto, true);
            }
        }
    }

    /**
     * 流程中数据作废
     *
     * @param workflowId
     */
    private void doDeleteFlow(String workflowId) {
        CfetsEtrBondFirstBid002Dto entity = getBussinessEntityByWorkflowId(workflowId);
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondFirstBidDtos())) {
            for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                    Map<String, Object> param = new HashMap<>();
                    //删除流程中数据
                    WFlowCommonService.doCompleteFlow(workflowId);
                    //从废弃表中获取，转移到指令表
                    etrEntrustBackupMapper.insertEtrFromQuery(dto.getEntrustId());
                    etrEntrustBackupMapper.insertEtrAddFromQuery(dto.getEntrustId());
                    etrEntrustBackupMapper.insertEtrBidPriceFromQuery(dto.getEntrustId());
                    etrEntrustBackupMapper.insertEtrDetailFromQuery(dto.getEntrustId());
                    // 删除废弃表
                    etrEntrustBackupMapper.deleteByPrimaryKey(dto.getEntrustId());
                    etrEntrustBackupMapper.deleteAddByPrimaryKey(dto.getEntrustId());
                    etrEntrustBackupMapper.deleteEntrustBidPriceByEntrustId(dto.getEntrustId());
                    etrEntrustBackupMapper.deleteEntrustDetailByEntrustId(dto.getEntrustId());
                }

            }
        }
        //发送头寸
        String busiType = "";
        if (StringUtil.equals(entity.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
            busiType=DictCodeListExt3.头寸业务类型_债券一级申购.getValue();
        } else {
            busiType=DictCodeListExt3.头寸业务类型_债券一级投标.getValue();
        }
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            PositionHttpRequest.doDeleteByWorkFlowId(workflowId,DictCodeListExt1.子系统代码_委托.getValue(),busiType,DictCodeList.交易方向_买入.getValue());
        }
    }

    /**
     * 流程结束后操作
     *
     * @param dto
     * @param operateType
     */
    private void doCompleteMethod(CfetsEtrBondFirstBid002Dto dto, String operateType) {
        BaseEtrInvestInfoDto entity = initBaseEtrInfoDto(dto);
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(operateType, "NEW")) {
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(entity.getEtrEntrust())) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(entity.getEtrEntrust(), entity.getEtrEntrustAdd()));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoService.newDelegate(entity);
        } else {
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo())
                    && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                        dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
            }

            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(entity.getEtrEntrust())) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(entity.getEtrEntrust(), entity.getEtrEntrustAdd()));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }

            baseEtrInvestInfoService.updateDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }

            if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", dto.getEntrustId());
                etrCommandDispatcherService.pushCommandMsg(param);
            }
        }
    }

    /**
     * 后台数据转换投资委托信息流程数据
     *
     * @param dto
     * @return
     */
    public BaseEtrInvestInfoDto initBaseEtrInfoFlowDto(CfetsEtrBondFirstBid002Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust = new EtrEntrust();

            dto.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getTotalBiddingNum().divide(new BigDecimal(100)));
            etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
            etrEntrust.setEntrustClass(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
            //委托附属表
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
            }
            etrEntrustAdd.setEntrustId(dto.getEntrustId());
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            entity.setEtrEntrustAdd(etrEntrustAdd);

            //委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
            if (!StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                for (CfetsBiddingProductDto productDto : biddingProductList) {
                    EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                    bidPrice.setEntrustId(dto.getEntrustId());
                    bidPrice.setBidPrice(productDto.getBidPrice());
                    bidPrice.setPortfolioId(productDto.getPortfolioId());
                    bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                    bidPriceList.add(bidPrice);
                }
            }

            //委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                    entrustDetail.setAcctNo(entry.getValue().get(0).getAcctNo());
                    if (StringUtil.isEmpty(entry.getValue().get(0).getSecAcctId())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("acctNo", entrustDetail.getAcctNo());
                        map.put("portfolioId", entrustDetail.getPortfolioId());
                        map.put("tradeMarket", dto.getTradeMarket());
                        List<SelectOptionDto> tempList = cfetsEtrBondFirstBid001Service.getPtlSecAcctInfo(map);
                        SelectOptionDto temp = tempList.stream().filter(x -> Objects.equals(x.getId(), entrustDetail.getAcctNo())).findFirst().orElse(null);
                        ArrayList secMap = (ArrayList) temp.getObject();
                        entrustDetail.setSecAcctId(((HashMap) secMap.get(0)).get("secAcctId").toString()); // 证券户
                    }

                }
                if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                    entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getBidPrinAmt(), 100, 4));
                } else {
                    entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                }

                entrustDetail.setInvAim(entry.getValue().get(0).getBusinessModel());
                etrEntrustDetailList.add(entrustDetail);
            }
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());

            entity.setEtrEntrust(etrEntrust);
            entity.setBidPriceList(bidPriceList);
            entity.setEtrEntrustDetailList(etrEntrustDetailList);
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            return entity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 后台数据转换投资委托信息数据
     *
     * @param dto
     * @return
     */
    public BaseEtrInvestInfoDto initBaseEtrInfoDto(CfetsEtrBondFirstBid002Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust = new EtrEntrust();

            dto.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustNum(dto.getTotalBiddingNum().divide(new BigDecimal(100)));
            etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
            etrEntrust.setEntrustClass(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
            if (etrEntrust.getEntrustDate() == null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }

            //委托附属表
            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
            }
            etrEntrustAdd.setEntrustId(dto.getEntrustId());
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            entity.setEtrEntrustAdd(etrEntrustAdd);
            //指令下达时间
            if (StringUtil.isNotEmpty(dto.getTransmitTime())) {
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            } else {
                etrEntrustAdd.setTransmitTime(new Date());
            }

            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();

            //委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            if (!StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                for (CfetsBiddingProductDto productDto : biddingProductList) {
                    EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                    bidPrice.setEntrustId(dto.getEntrustId());
                    bidPrice.setBidPrice(productDto.getBidPrice());
                    bidPrice.setPortfolioId(productDto.getPortfolioId());
                    bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                    bidPriceList.add(bidPrice);
                }
            }

            //委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                entrustDetail.setInvAim(entry.getValue().get(0).getBusinessModel());
                if (StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        || StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                    entrustDetail.setAcctNo(entry.getValue().get(0).getAcctNo());
                    if (StringUtil.isEmpty(entry.getValue().get(0).getSecAcctId())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("acctNo", entrustDetail.getAcctNo());
                        map.put("portfolioId", entrustDetail.getPortfolioId());
                        map.put("tradeMarket", dto.getTradeMarket());
                        List<SelectOptionDto> tempList = cfetsEtrBondFirstBid001Service.getPtlSecAcctInfo(map);
                        SelectOptionDto temp = tempList.stream().filter(x -> Objects.equals(x.getId(), entrustDetail.getAcctNo())).findFirst().orElse(null);
                        ArrayList secMap = (ArrayList) temp.getObject();
                        entrustDetail.setSecAcctId(((HashMap) secMap.get(0)).get("secAcctId").toString()); // 证券户
                    } else {
                        entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                    }
                }
                if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                    entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getBidPrinAmt(), 100, 4));
                } else {
                    entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                }

                etrEntrustDetailList.add(entrustDetail);
            }
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());

            entity.setEtrEntrust(etrEntrust);
            entity.setBidPriceList(bidPriceList);
            entity.setEtrEntrustDetailList(etrEntrustDetailList);
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            return entity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 操作类型为submit时执行新增流程数据
     *
     * @param operateType 操作类型
     * @param dto
     */
    private void doInsertFlowMethod(String operateType, BaseEtrInvestInfoDto dto) {
        if ("submit".equals(operateType)) {
            if (StringUtil.isEmpty(dto.getWorkFlowId())) {
                throw new RuntimeException("WorkFlowId初始化不能为空!");
            }
            WFlowCommonService.insertFlow(dto);
        }
    }

    /**
     * 保存提交时，后台数据转换
     *
     * @param cfetsDto
     * @return
     */
    public CfetsEtrBondFirstBid002Dto packageEquity(CfetsEtrBondFirstBidSaveRequest cfetsDto) {

        //查询资产信息
        CfetsEtrFinProductInfoResponse finProductInfo = new CfetsEtrFinProductInfoResponse();
        if (StringUtil.isNotEmpty(cfetsDto.getFinprodId())) {
            CfetsEtrFinProductInfoRequest productInfoRequest = new CfetsEtrFinProductInfoRequest();
            productInfoRequest.setFinprodId(cfetsDto.getFinprodId());
            finProductInfo = etrCommonFinProductInfoService.findFinProductInfo(null, null, productInfoRequest);
        }


        CfetsEtrBondFirstBid002Dto dto = new CfetsEtrBondFirstBid002Dto();
        BeanUtil.copyPropertiesIgnoreNull(dto, cfetsDto);
        if (finProductInfo != null) {
            dto.setTradeMarket(finProductInfo.getTradeMarket());
            //dto.setCalendarId(finProductInfo.getCalendarId());
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            dto.setFinprodAbbr(finProductInfo.getFinprodAbbr());
            dto.setFinprodMarketId(finProductInfo.getFinprodMarketId());
            //审批时 流程数据初始化
            dto.setIssueClass(finProductInfo.getIssueClass());
            if (StringUtil.equals(finProductInfo.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                dto.setIssueDate(finProductInfo.getIssueDate());
                dto.setPayDate(finProductInfo.getPayDate());
                dto.setIssuePrice(new BigDecimal(finProductInfo.getIssuePrice()));
                dto.setReferYield(finProductInfo.getReferYield());

            } else {
                //查询所有的对手方
                List<SelectOptionDto> counterDtos = joyinbusinessCommonMapper.queryCounterPartyList(null);
                Map<String, String> counterMap = counterDtos.stream()
                        .collect(Collectors.toMap(SelectOptionDto::getId, SelectOptionDto::getText));

                dto.setTenderTarget(finProductInfo.getTenderTarget());
                dto.setTenderSdate(finProductInfo.getTenderSdate());
                dto.setTenderEdate(finProductInfo.getTenderEdate());
                dto.setIssueDate(finProductInfo.getIssueDate());
                dto.setPayEdate(finProductInfo.getPayEdate());
                dto.setCounterId(cfetsDto.getCounterId());
                dto.setCounterName(counterMap.get(cfetsDto.getCounterId()));
                dto.setTenderTarget(finProductInfo.getTenderTarget());
            }
        }

        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        //dto.setInvManagerId("li02");
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        dto.setEntrustNum(cfetsDto.getTotalBiddingNum());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        dto.setElementId(cfetsDto.getElementId());
        dto.setEntrustDate(cfetsDto.getEtrDate() != null ? cfetsDto.getEtrDate() : DateUtil.platDateFromCache());
        dto.setVdate(cfetsDto.getEtrDate());

        List<CfetsBiddingProductDto> biddingProductList = cfetsDto.getBiddingProductList();
        List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidList = new ArrayList<>();
        //场外
        if (StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                || StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
            //报价发行时，按照证券账户维度拆分为多条指令信息
            if (DictCodeListExt4.新债_发行方式_报价发行.getValue().equals(cfetsDto.getIssueClass())) {
                Map<String, List<CfetsBiddingProductDto>> secAcctIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getSecAcctId));
                for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : secAcctIdMap.entrySet()) {
                    CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
                    BeanUtil.copyPropertiesIgnoreNull(dd, dto);
                    dd.setBiddingProductList(null);
                    dd.setBiddingProductList(entry.getValue());
                    dd.setSecAcctId(entry.getKey());
                    //汇总投标券面总额 重新计算
                    BigDecimal totalNum = dd.getBiddingProductList().stream().map(CfetsBiddingProductDto::getBidPrinAmt)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    dd.setTotalBiddingNum(totalNum);
                    //针对退回指令，查询指令下达时间
                    if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())) {
                        //根据指令id查询出该资产是退回指令，且该指令的下达时间
                        CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(cfetsDto.getEntrustId());
                        if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                            dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                        }
                        dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                    } else {
                        dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                    }

                    //投标日前下的指令，指令结束日为投标日;投标日后下的指令，指令结束日为指令日期
                    if (DateUtil.compareDate(cfetsDto.getEtrDate(), cfetsDto.getIssueDate()) >= 0) {
                        dd.setMdate(cfetsDto.getIssueDate());
                    } else {
                        dd.setMdate(cfetsDto.getEtrDate());
                    }

                    etrInvestBondFirstBidList.add(dd);
                }

            } else {
                CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
                BeanUtil.copyPropertiesIgnoreNull(dd, dto);
                //针对退回指令，查询指令下达时间
                if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())) {
                    //根据指令id查询出该资产是退回指令，且该指令的下达时间
                    CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(cfetsDto.getEntrustId());
                    if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                        dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                    }
                    dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                } else {
                    dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                }

                dd.setMdate(cfetsDto.getTenderDate());
                etrInvestBondFirstBidList.add(dd);
            }

        }


        //场内
        if (StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                || StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {

            CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd, dto);
            //针对退回指令，查询指令下达时间
            if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())) {
                //根据指令id查询出该资产是退回指令，且该指令的下达时间
                CfetsEtribBondInfo003Dto transmitTimeDto = cfetsetrIbBondinfoMapper.selectTransmitTimeByInfo(cfetsDto.getEntrustId());
                if (null != transmitTimeDto && StringUtil.isNotEmpty(transmitTimeDto.getTransmitTime())) {
                    dd.setTransmitTime(transmitTimeDto.getTransmitTime());
                }
                dd.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
            } else {
                dd.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            }

            dd.setMdate(cfetsDto.getTenderDate());
            etrInvestBondFirstBidList.add(dd);
        }

        //风险
        CfetsEtribdoRiskcalResponse riskDto = cfetsDto.getCfetsEtribdoRiskcalReturn();
        List<RiskTrialRetDto> riskTrialRetDto = new ArrayList<>();
        if (StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())) {
            for (CfetsEtribdoRiskcalResponse it : riskDto.getRiskcalReturnList()) {
                RiskTrialRetDto t = new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t, it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())) {
            dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);
        dto.setEtrInvestBondFirstBidDtos(etrInvestBondFirstBidList);
        return dto;
    }

    /**
     * 保存提交时check检查
     *
     * @param allDto
     * @return
     */
    public AjaxResult check(CfetsEtrBondFirstBidSaveRequest allDto) {

        List<ValidMessage> messages = new ArrayList<>();
        if (!StringUtil.isNotEmpty(allDto.getFinprodId())) {
            messages.add(new ValidMessage("请输入资产代码。"));
        }

        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        return null;
    }

    /**
     * 保存修改后直接通过的指令数据
     *
     * @param allDto
     */
    public void saveModifyPassInfo(CfetsEtrBondFirstBid002Dto allDto) {
        for (CfetsEtrBondFirstBid002Dto dto : allDto.getEtrInvestBondFirstBidDtos()) {
            //全删委托投标标位表数据
            baseEtrInvestInfoMapper.deleteBidPrice(dto.getEntrustId());
            //全删委托指令明细数据
            baseEtrInvestInfoMapper.deleteEntrustDetail(dto.getEntrustId());
            //更新委托表
            EtrEntrust etrEntrust = new EtrEntrust();
            etrEntrust.setEntrustId(dto.getEntrustId());
            etrEntrust.setEntrustNum(ArithmeticUtil.divide(dto.getTotalBiddingNum(), 100, 4));
            etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
            //更新委托附属表
            etrEntrustAddMapper.updateByPrimaryKeySelective(null);

            //保存委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
            for (CfetsBiddingProductDto productDto : biddingProductList) {
                EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                bidPrice.setEntrustId(dto.getEntrustId());
                bidPrice.setBidPrice(productDto.getBidPrice());
                bidPrice.setPortfolioId(productDto.getPortfolioId());
                bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                bidPriceList.add(bidPrice);
            }
            if (CollectionUtils.isNotEmpty(bidPriceList)) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : bidPriceList) {
                    etrEntrustBidPriceMapper.insertSelective(etrEntrustBidPrice);
                }
            }

            //保存委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                etrEntrustDetailList.add(entrustDetail);
            }
            if (CollectionUtils.isNotEmpty(etrEntrustDetailList)) {
                for (EtrEntrustDetail etrEntrustDetail : etrEntrustDetailList) {
                    etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
                }
            }
        }
    }

    /**
     * 保存、修改提交校验
     *
     * @param cfetsDto
     * @return
     */
    public AjaxResult checkSave(CfetsEtrBondFirstBidSaveRequest cfetsDto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> messages = new ArrayList<>();

        if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())) {
            WflEtrEntrust wflEtrEntrust =  wflEtrEntrustMapper.selectByEntrustId(cfetsDto.getEntrustId());
            if (wflEtrEntrust != null) {
                messages.add(new ValidMessage("正在流程中，不允许修改！"));
                ajaxResult.setErrorList(messages);
                return ajaxResult;
            }
        }
        if (StringUtil.isEmpty(cfetsDto.getFinprodId())) {
            messages.add(new ValidMessage("请输入资产代码！"));
        }
        if (cfetsDto.getTotalBiddingNum() == null) {
            messages.add(new ValidMessage("汇总投标券面总额不能为空！"));
        }
        //校验字符串是否是  数字 小数 不为负数
        if (!NumericUtil.isNumeric(cfetsDto.getTotalBiddingNum().toString())) {
            messages.add(new ValidMessage("汇总投标券面总额必须是不为负值的数字！"));
        }

        if (CollectionUtils.isNotEmpty(cfetsDto.getBiddingProductList())) {

            for (CfetsBiddingProductDto bidDto : cfetsDto.getBiddingProductList()) {
                if (StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_银行间.getValue())
                        || StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
                    //证券账户不能为空
                    if (StringUtil.isEmpty(bidDto.getSecAcctId())) {
                        messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],证券账户不能为空！"));
                    }
                }

                if (StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        || StringUtil.equals(cfetsDto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                    //资金账户不能为空
                    if (StringUtil.isEmpty(bidDto.getAcctNo())) {
                        messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],资金账户不能为空！"));
                    }
                    //资金账户对应的证券账户
                    if (StringUtil.isEmpty(bidDto.getSecAcctId())) {
                        messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],证券账户不能为空！"));
                    }
                }

                if (bidDto.getBidPrinAmt() == null) {
                    messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],投标券面总额不能为空！"));
                }
                //校验字符串是否是  数字 小数 不为负数
                if (!NumericUtil.isNumeric(bidDto.getBidPrinAmt().toString())) {
                    messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],投标券面总额必须是不为负值的数字！"));
                }
                //大于0.1(万元)
                if (bidDto.getBidPrinAmt() != null) {
                    if (bidDto.getBidPrinAmt().compareTo(BigDecimal.valueOf(1000)) < 0) {
                        messages.add(new ValidMessage("组合[" + bidDto.getPortfolioId() + "],投标券面总额不能小于【0.1(万元)】！"));
                    }

                }
            }

            //汇总投标券面总额 等于 投标券面总额之和
            BigDecimal bidPrinAmtAdd = cfetsDto.getBiddingProductList().stream()
                    .map(CfetsBiddingProductDto::getBidPrinAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!(cfetsDto.getTotalBiddingNum().compareTo(bidPrinAmtAdd) == 0)) {
                messages.add(new ValidMessage("汇总投标券面总额不等于投标券面总额之和！"));
            }

            //报价发行时，同一组合在列表中只能存在一条数据
            if (StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                List<String> portfolioIdList = cfetsDto.getBiddingProductList().stream().
                        map(CfetsBiddingProductDto::getPortfolioId).distinct().collect(Collectors.toList());
                if (portfolioIdList.size() != cfetsDto.getBiddingProductList().size()) {
                    messages.add(new ValidMessage("报价发行：同一组合在列表中只能存在一条数据！"));
                }
            }

            if (StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_招标发行.getValue()) ||
                    StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_簿记建档.getValue())) {
                //招标发行/簿记建档时，同一组合同一标位在列表中只能存在一条数据
                Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = cfetsDto.getBiddingProductList().stream()
                        .collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
                for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                    for (CfetsBiddingProductDto productDto : entry.getValue()) {
                        if (productDto.getBidPrice() == null) {
                            messages.add(new ValidMessage("投标标位不能为空！"));
                            break;
                        }
                        if (productDto.getBidPrice().compareTo(BigDecimal.ZERO) < 0) {
                            messages.add(new ValidMessage("投标标位必须大于0！"));
                            break;
                        }
                        //校验字符串是否是  数字 小数 不为负数
                        if (!NumericUtil.isNumeric(productDto.getBidPrice().toString())) {
                            messages.add(new ValidMessage("投标标位必须是不为负值的数字！"));
                            break;
                        }
                        if (productDto.getPortfolioTotalBidPrinAmt().compareTo(BigDecimal.ZERO) < 0) {
                            messages.add(new ValidMessage("组合汇总投标券面总额必须大于0！"));
                            break;
                        }
                        //校验字符串是否是  数字 小数 不为负数
                        if (!NumericUtil.isNumeric(productDto.getPortfolioTotalBidPrinAmt().toString())) {
                            messages.add(new ValidMessage("组合汇总投标券面总额必须是不为负值的数字！"));
                            break;
                        }
                    }
                    List<BigDecimal> bidPriceList = entry.getValue().stream().map(CfetsBiddingProductDto::getBidPrice).distinct().collect(Collectors.toList());
                    if (bidPriceList.size() != entry.getValue().size()) {
                        messages.add(new ValidMessage("招标发行/簿记建档:同一组合同一标位在列表中只能存在一条数据！"));
                    }

                    List<String> businessModelList = entry.getValue().stream().map(CfetsBiddingProductDto::getBusinessModel).distinct().collect(Collectors.toList());
                    if (businessModelList.size() != 1) {
                        messages.add(new ValidMessage("同一组合业务模式不一致！"));
                    }
                }
            }
            if (StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_招标发行.getValue()) ||
                    StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_簿记建档.getValue())) {
                //校验标位信息
                if (StringUtil.isNotEmpty(cfetsDto.getFinprodId())) {
                    //查询债券信息
                    CfetsEtrFinProductInfoRequest dto = new CfetsEtrFinProductInfoRequest();
                    dto.setFinprodId(cfetsDto.getFinprodId());
                    CfetsEtrFinProductInfoResponse finProductInfo = etrCommonFinProductInfoService.findFinProductInfo(null, null, dto);

                    Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = cfetsDto.getBiddingProductList().stream()
                            .collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
                    List<String> errorMessageList = new ArrayList<>();
                    for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                        List<BigDecimal> bidPriceSort = entry.getValue().stream().map(CfetsBiddingProductDto::getBidPrice).sorted().collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(bidPriceSort)) {
                            //根据标的信息校验标位
                            //最低标位 最低标位的大于等于该字段值
                            if (finProductInfo.getMinTamt() != null) {
                                if (bidPriceSort.get(0).compareTo(finProductInfo.getMinTamt()) == -1) {
                                   // messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求，请重新填写！"));
                                    errorMessageList.add("最低标位");
                                }
                            }
                            //最高标位 最高标位的小于等于该字段值
                            if (finProductInfo.getMaxTamt() != null) {
                                if (finProductInfo.getMaxTamt().compareTo(bidPriceSort.get(bidPriceSort.size() - 1)) == -1) {
                                   // messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求，请重新填写！"));
                                    errorMessageList.add("最高标位");
                                }
                            }

                            //最大投标标位数 标位个数小于等于该字段值
                            if (finProductInfo.getMaxTenderUnit() != null) {
                                if (finProductInfo.getMaxTenderUnit().compareTo(BigDecimal.valueOf(bidPriceSort.size())) == -1) {
                                    //messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求，请重新填写！"));
                                    errorMessageList.add("最大投标标位数");
                                }

                            }

                            //最大投标标位差 最高标位减去最低标位的差的绝对值小于等于该字段值
                            if (finProductInfo.getMaxTenderPlus() != null) {
                                BigDecimal subNum = bidPriceSort.get(bidPriceSort.size() - 1).subtract(bidPriceSort.get(0));
                                if (finProductInfo.getMaxTenderPlus().compareTo(subNum) == -1) {
                                   // messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求，请重新填写！"));
                                    errorMessageList.add("最大投标标位差");
                                }
                            }

                            //投标步长  标位需要时投标步长的整数倍
                            if (finProductInfo.getTenderUnit() != null && BigDecimal.ZERO.compareTo(finProductInfo.getTenderUnit()) != 0) {
                                for (BigDecimal bidPrice : bidPriceSort) {
                                    if (!BigDecimalUtil.isIntegerMultiples(bidPrice, finProductInfo.getTenderUnit())) {
                                       // messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求，请重新填写！"));
                                        errorMessageList.add("投标步长");
                                    }
                                }

                            }

                            //连续投标限制
                            if (StringUtil.isNotEmpty(finProductInfo.getSeriYN()) && DictCodeList.是否_是.getValue().equals(finProductInfo.getSeriYN())) {

                                if (finProductInfo.getTenderUnit() != null) {
                                    List<BigDecimal> bidPriceSortnew = new ArrayList<>();
                                    for (int i = 0; i < bidPriceSort.size() - 1; i++) {
                                        bidPriceSortnew.add(bidPriceSort.get(i + 1).subtract(bidPriceSort.get(i)));
                                    }
                                    if (CollectionUtils.isNotEmpty(bidPriceSortnew)) {
                                        if (bidPriceSortnew.contains(finProductInfo.getTenderUnit())) {
                                            errorMessageList.add("连续投标限制");
                                        }
                                    }
                                }

                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(errorMessageList)) {
                        String str = StringUtil.join(errorMessageList.stream().distinct().collect(Collectors.toList()), "、");
                        messages.add(new ValidMessage("投标券面总额/投标标位不满足该资产发行要求："+str+"，请重新填写！"));
                    }

                }
            }

        }


        ajaxResult.setErrorList(messages);
        return ajaxResult;
    }

    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsEtrBondFirstBid002Dto entity, OperationType type) {
        return "指令日期" + ":" + (entity.getEntrustDate() == null ? DateUtil.format(DateUtil.platDateFromCache()) : DateUtil.format(entity.getEntrustDate()) )+ "-" +
                "资产简称" + ":" + (entity.getFinprodAbbr() == null ? "" : entity.getFinprodAbbr()) + "-" +
                "发行方式" + ":" + (SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, entity.getIssueClass()) == null ? "" : SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, entity.getIssueClass())) + "-" +
                "汇总投标券面总额" + ":" + ArithmeticUtil.divide(entity.getTotalBiddingNum(), new BigDecimal(10000), 2) + "万元";
    }


    /**
     * 查询留痕表数据
     * @param workflowId
     * @return
     */
    public ProcessMark getFirstBidBusinessInfo(String workflowId) {
        return cfetsEtrBondFirstBid002Mapper.getFirstBidBusinessInfo(workflowId);
    }
}
