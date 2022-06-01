/*
 * 文件名：CfetsBondDealQuotedPrice002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.repo.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.repodlgservice.RepoDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.routeconfigservice.RouteConfigSubService;
import com.joyintech.background.component.uplink.interbank.business.routeintfservice.RouteIntfSubService;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteMapper;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuIntfScheme;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.ibu.dto.RepoQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.service.bankuplink.repo.dao.CfetsRepoDealQuotedPrice001Mapper;
import com.joyintech.service.bankuplink.repo.service.CfetsRepoDealQuotedPrice001Service;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepo003Mapper;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepoMapper;
import com.joyintech.tams.cfets.trd.repo.dto.*;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioCashRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioSecRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPositionRequest;
import com.joyintech.tams.common.base.entity.RegFinPledge;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.invest.bankuplink.repo.service.CfetsRepoDealQuotedPrice003Service;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoDealQuotedPriceDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoPledgeBondDto;
import com.joyintech.tams.invest.common.dto.cstp.IbuQuoteEntCrepPledgeCommonDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 质押式回购-对话报价
 */
@Service("vueCfetsRepoDealQuotedPrice002Service")
public class CfetsRepoDealQuotedPrice002Service extends CommonExportService<CfetsRepoDealQuotedPrice001Dto>
        implements BaseProcessService<CfetsRepoDealQuotedPrice001Dto> {

    private static final Logger logger = LoggerFactory.getLogger(CfetsRepoDealQuotedPrice002Service.class);
    /**
     * 利率保留位数
     */
    private static final int yieldScale = 4;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private CfetsRepoDealQuotedPrice003Service cfetsRepoDealQuotedPrice003Service;

    @Autowired
    private CfetsRepoDealQuotedPrice001Service cfetsRepoDealQuotedPrice001Service;

    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonServie;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;

    @Autowired
    private IbuMainService ibuMainService;

    @Autowired
    private RepoDialogueQuoteSubService repoDialogueQuoteSubService;

    @Autowired
    private RouteIntfSubService routeIntfSubService;

    @Autowired
    private RouteConfigSubService routeConfigSubService;

    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    @Autowired
    private CstpCommonService cstpCommonService;

    @Autowired
    private CfetsIbuQuoteRepoMapper cfetsIbuQuoteRepoMapper;

    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired
    private CfetsRepoDealQuotedPrice001Mapper cfetsRepoDealQuotedPrice001Mapper;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private CfetsIbuQuoteRepo003Mapper ibuQuoteRepo003Mapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 组合可用现金和实际现金查询更新
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    /**
     * 组合可用持仓和实际持仓查询更新
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired
    private CfetsRepoDealQuotedPrice001Service oldCfetsRepoDealQuotedPrice001Service;

    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;

    @Autowired
    IbuQuoteMapper ibuQuoteMapper;

    @Autowired
    private com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService cfetsQuotedCommonServicevue;

    /**
     * 报价初始化校验
     *
     * @param param
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2021/12/30 11:37
     */
    public AjaxResult checkEditList(QuotedPriceRequest request) {
        AjaxResult ar = new AjaxResult();
        List<QuotedPriceRequest> dataList = request.getRequests();
        for (QuotedPriceRequest data : dataList) {
            //多组合标识或者本方询价方为回复方 均跳转多组合界面
            String quoteRoleType = data.getQuoteRoleType();
            if (StringUtil.equals(quoteRoleType, BussinessDictCodeList.银行间上行本方角色_回复方.getValue())) {
                data.setEntrustId(null);
            }
            ar = this.checkEdit(data);
            if (!StringUtil.isEmptyList(ar.getErrorList())) {
                return ar;
            }
        }
        return ar;
    }

    /**
     * 报价初始化校验
     *
     * @param param
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2021/12/30 11:37
     */
    public AjaxResult checkEdit(QuotedPriceRequest request) {
        AjaxResult ar = new AjaxResult();
        // 验证信息
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        String entrustId = request.getEntrustId();
        String operation = request.getOperatorType();
        String quoteId = request.getQuoteId();
        String validStatus = request.getValidStatus();
        String quotaPageModel = request.getQuotaPageModel();
        CfetsRepoDealQuotedPrice001Dto dto = null;
        if (StringUtil.isNotEmpty(quoteId)) {
            //报价权限(是否属于该交易员)
            if (StringUtil.equals(validStatus, BussinessDictCodeList.有效状态_有效.getValue())) {
                Map<String, Object> mapOpr = new HashMap<>();
                mapOpr.put("quoteIdTyep", "rep");
                mapOpr.put("quoteId", quoteId);
                boolean opeQuotedPrice = cfetsQuotedCommonService.isOpeQuotedPrice(mapOpr);
                if (!opeQuotedPrice) {
                    ar.appendMessage(new ValidMessage("交易员无权限操作报价,报价编号:" + quoteId));
                    return ar;
                }
            }
            validList.addAll(isInWflProcess(quoteId, request.getPageId()));
            if (validList.size() > 0) {
                ar.setErrorList(validList);
                return ar;
            }
        }
        if (StringUtil.isNotEmpty(entrustId)) {
            dto = this.queryEntrustInfoByEntrustId(entrustId, quoteId);
        }

        if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(operation, "revoke")) {//删除
            // 删除操作校验
            CfetsRepoDealQuotedPrice001Dto dayOffer = StringUtil.isNotEmpty(quoteId)
                    ? this.queryEntrustInfoByQuoteId(quoteId) : null;
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            String sendStatus = request.getSendStatus();
            if (!StringUtil.isNotEmpty(sendStatus) || !StringUtil.equals(sendStatus, dayOffer.getSendStatus())) {
                // 发送状态与当前状态不符
                validList.add(new ValidMessage("发送状态已变更，请重新检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            if (!StringUtil.equals(BussinessDictCodeList.银行间上行发送状态_待发送.getValue(), dayOffer.getSendStatus())) {
                // 发送状态不是待发送
                validList.add(new ValidMessage("报价不满足【发送状态为待发送】的条件，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
        } else if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(operation, "restore")) {//回退
            // 回退操作校验
            CfetsRepoDealQuotedPrice001Dto dayOffer = StringUtil.isNotEmpty(quoteId)
                    ? this.queryEntrustInfoByQuoteId(quoteId)
                    : null;
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 校验交易状态
            String tradeStatus = request.getTradeStatus();
            if (!StringUtil.isNotEmpty(tradeStatus) || !StringUtil.equals(tradeStatus, dayOffer.getTradeStatus())) {
                validList.add(new ValidMessage("交易状态已变更，请重新检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 发送状态
            String sendStatus = request.getSendStatus();
            if (!StringUtil.isNotEmpty(sendStatus) || !StringUtil.equals(sendStatus, dayOffer.getSendStatus())) {
                validList.add(new ValidMessage("发送状态已变更，请重新检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 发送状态
            if (!StringUtil.equals(BussinessDictCodeList.银行间上行发送状态_失败.getValue(), dayOffer.getSendStatus())) {
                validList.add(new ValidMessage("报价不满足【交易状态为新增中或修改中或交谈中或成交中或终止中或撤回中、发送状态为失败】的条件，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            } else {
                if (StringUtil.equals(BussinessDictCodeList.银行间上行本方角色_询价方.getValue(), dayOffer.getQuoteRoleType())) {
                    // 校验交易状态
                    if (!StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_新增中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_成交中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_终止中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_修改中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue(), dayOffer.getTradeStatus())) {
                        validList.add(new ValidMessage("报价不满足【交易状态为新增中或修改中或交谈中或成交中或终止中或撤回中、发送状态为失败】的条件，请检查数据状态！"));
                        ar.setErrorList(validList);
                        return ar;
                    }
                    /*if (!StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_新增中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_成交中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_终止中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue(), dayOffer.getTradeStatus())) {
                        validList.add(new ValidMessage("报价不满足【交易状态为新增中或修改中或交谈中或成交中或终止中或撤回中、发送状态为失败】的条件，请检查数据状态！"));
                    }*/
                } else if (StringUtil.equals(BussinessDictCodeList.银行间上行本方角色_回复方.getValue(), dayOffer.getQuoteRoleType())) {
                    if (!StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_新增中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_成交中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_终止中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue(), dayOffer.getTradeStatus())
                            && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue(), dayOffer.getTradeStatus())) {
                        validList.add(new ValidMessage("报价不满足【交易状态为新增中或修改中或交谈中或成交中或终止中或撤回中、发送状态为失败】的条件，请检查数据状态！"));
                        ar.setErrorList(validList);
                        return ar;
                    }
                }
            }
        } else if (StringUtil.isNotEmpty(quoteId) && StringUtil.isNotEmpty(operation)
                && StringUtil.equals(operation, "revert")) {//撤回
            // 校验撤回
            CfetsRepoDealQuotedPrice001Dto dayOffer = this.queryEntrustInfoByQuoteId(quoteId);
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 发送状态
            String sendStatus = request.getSendStatus();
            // 交易状态
            String tradeStatus = request.getTradeStatus();
            boolean flag = false;
            if (!StringUtil.equals(sendStatus, dayOffer.getSendStatus())
                    || !StringUtil.equals(tradeStatus, dayOffer.getTradeStatus())) {
//				validList.add(new ValidMessage("状态已变更，请重新检查数据状态！"));
                flag = true;
            }
            // 校验本方角色
            if (StringUtil.equals(BussinessDictCodeList.银行间上行本方角色_询价方.getValue(), dayOffer.getQuoteRoleType())) {
                if (!((StringUtil.equals(dayOffer.getSendStatus(), BussinessDictCodeList.银行间上行发送状态_成功.getValue())
                        && (StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已新增.getValue())
                        || StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已修改.getValue())
                        || StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已交谈.getValue()))))) {
//                    validList.add(new ValidMessage("报价不满足【交易状态为已新增、已修改、已交谈、发送状态为成功】的条件，请检查数据状态！"));
                    flag = true;
                }
                //validList.add(new ValidMessage("只能撤回本方角色为询价方的报价数据！"));
            } else if (StringUtil.equals(BussinessDictCodeList.银行间上行本方角色_回复方.getValue(), dayOffer.getQuoteRoleType())) {
                if (!(StringUtil.equals(dayOffer.getSendStatus(), BussinessDictCodeList.银行间上行发送状态_成功.getValue())
                        && StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已交谈.getValue()))) {
//                    validList.add(new ValidMessage("报价不满足【交易状态为已交谈、发送状态为成功】的条件，请检查数据状态！"));
                    flag = true;
                }
            }
            if (flag) {
                validList.add(new ValidMessage("交易状态和发送状态分别为已新增/成功，已修改/成功，已交谈/成功的对话报价，才能执行撤回操作！"));
                ar.setErrorList(validList);
                return ar;
            }
        } else if (StringUtil.isNotEmpty(quoteId) && StringUtil.isNotEmpty(operation)
                && StringUtil.equals(quotaPageModel, "dialogueResponse")
                && StringUtil.equals(operation, "done")) {//成交
            // 当日报价回复校验
            CfetsRepoDealQuotedPrice001Dto dayOffer = this.queryEntrustInfoByQuoteId(quoteId);
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 交易状态
            String tradeStatus = request.getTradeStatus();
            if (!StringUtil.isNotEmpty(tradeStatus) || !StringUtil.equals(tradeStatus, dayOffer.getTradeStatus())
                    || !StringUtil.equals(tradeStatus, BussinessDictCodeList.银行间上行报价状态_待回复.getValue())) {
                validList.add(new ValidMessage("交易状态已变更，请重新检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            // 发送状态
            String sendStatus = request.getSendStatus();
            if (StringUtil.isNotEmpty(sendStatus) || StringUtil.isNotEmpty(dayOffer.getSendStatus())) {
                validList.add(new ValidMessage("发送状态已变更，请重新检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
        } else if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(quotaPageModel, "dialogueResponse")
                && StringUtil.equals(operation, "reject")) {//拒绝
            // 回复拒绝操作校验
            if (StringUtil.isNotEmpty(quoteId)) {
                // 当日报价双击校验
                CfetsRepoDealQuotedPrice001Dto dayOffer = this.queryEntrustInfoByQuoteId(quoteId);
                if (dayOffer == null) {
                    validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                    ar.setErrorList(validList);
                    return ar;
                }
                // 交易状态
                String tradeStatus = request.getTradeStatus();
                if (!StringUtil.isNotEmpty(tradeStatus) || !StringUtil.equals(tradeStatus, dayOffer.getTradeStatus())
                        || !StringUtil.equals(tradeStatus, BussinessDictCodeList.银行间上行报价状态_待回复.getValue())) {
                    validList.add(new ValidMessage("报价不满足【交易状态为待回复】的条件，请检查数据状态！"));
                    ar.setErrorList(validList);
                    return ar;
                }
            }
        } else if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(quotaPageModel, "edit")) {
            // 当日报价双击校验
            CfetsRepoDealQuotedPrice001Dto dayOffer = this.queryEntrustInfoByQuoteId(quoteId);
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            String sendStatus = request.getSendStatus();
            if ("".equals(sendStatus)) {
                sendStatus = null;
            }
            boolean flag = false;
            // 发送状态
            if (!StringUtil.equals(sendStatus, dayOffer.getSendStatus())) {
//				validList.add(new ValidMessage("状态已变更，请重新检查数据状态！"));
                flag = true;
            }
            if (!((StringUtil.equals(dayOffer.getSendStatus(), BussinessDictCodeList.银行间上行发送状态_成功.getValue())
                    && (StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已新增.getValue()))
                    || StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已修改.getValue())
                    || StringUtil.equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已交谈.getValue()))
                    || (null == dayOffer.getSendStatus() && StringUtil
                    .equals(dayOffer.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_待回复.getValue())))) {
                // 银行间上行报价状态_已新增)
//				validList.add(new ValidMessage("当前数据状态不符合修改要求！"));
                flag = true;
            }
            if (flag) {
                validList.add(new ValidMessage("交易状态和发送状态分别为已新增/成功，已修改/成功，已交谈/成功，待回复/空的对话报价，才能执行修改操作！"));
                ar.setErrorList(validList);
                return ar;
            }
        } else if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(quotaPageModel, "new")) {
            // 对话报价新增校验
            // 判断是否选择委托
            if (StringUtil.isNotEmpty(entrustId)) {
                // 对话报价校验
                if (dto == null) {
                    validList.add(new ValidMessage("指令代码不存在"));
                    ar.setErrorList(validList);
                    return ar;
                }
                if (!StringUtil.equals(dto.getEntrustStatus(), BussinessDictCodeList.委托状态_已接收.getValue())) {
                    validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_024")));
                    ar.setErrorList(validList);
                    return ar;
                }
                if (StringUtil.equals(dto.getEntrustStatus(), BussinessDictCodeList.委托状态_已接收.getValue())
                        && dto.getTransactioncom().compareTo(new BigDecimal(100)) >= 0) {
                    validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_023")));
                    ar.setErrorList(validList);
                    return ar;
                }
            }
        } else if (StringUtil.isNotEmpty(operation)
                && StringUtil.equals(quotaPageModel, "copy")) {
            // 当日报价复制校验
            CfetsRepoDealQuotedPrice001Dto dayOffer = this.queryEntrustInfoByQuoteId(quoteId);
            if (dayOffer == null) {
                validList.add(new ValidMessage("报价不存在，请检查数据状态！"));
                ar.setErrorList(validList);
                return ar;
            }
            if (dayOffer != null) {
                boolean flag = false;
                // 校验本方角色
                if (!StringUtil.equals(BussinessDictCodeList.银行间上行本方角色_询价方.getValue(), dayOffer.getQuoteRoleType())) {
//					validList.add(new ValidMessage("只能复制本方角色为询价方的报价数据！"));
                    flag = true;
                }
                // 交易状态
                String tradeStatus = request.getTradeStatus();
                String sendStatus = request.getSendStatus();
                if (!StringUtil.equals(tradeStatus, dayOffer.getTradeStatus())
                        || !StringUtil.equals(sendStatus, dayOffer.getSendStatus())) {
//					validList.add(new ValidMessage("状态已变更，请重新检查数据状态！"));
                    flag = true;
                }
                // 校验交易状态
                if (!StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已新增.getValue(), dayOffer.getTradeStatus())
                        && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(), dayOffer.getTradeStatus())
                        && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(), dayOffer.getTradeStatus())
                        && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(), dayOffer.getTradeStatus())
                        && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已修改.getValue(), dayOffer.getTradeStatus())
                        && !StringUtil.equals(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue(), dayOffer.getTradeStatus())) {
//					validList.add(new ValidMessage("只能复制交易状态为已新增/已成交/已终止/已撤回/已修改/已交谈的报价数据！"));
                    flag = true;
                }
                if (flag) {
                    validList.add(new ValidMessage("本方角色为询价方，交易状态和发送状态分别为已新增/成功，已修改/成功，已交谈/成功，已撤回/成功，已成交/成功，已终止 /成功的对话报价，才能执行复制操作！"));
                    ar.setErrorList(validList);
                    return ar;
                }
            }
        } else {
            ar.setData(this.getMessage("COMMON_C_0001", this.getMessage("SysCommon.save")));
        }
        ar.setErrorList(validList);
        return ar;
    }

    /**
     * 提交保存check 必填项
     *
     * @param entity 提交数据
     * @return 校验返回信息
     */
    public AjaxResult checkSave(CfetsRepoDealQuoteCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //模式
        String ps = entity.getPs();
        this.checkEntityEmpty(errorList, entity);
        String massge = null;
        // 记录指令代码
        List<String> entrustIdList = new ArrayList<>();
        entity.getPtlAllocations().forEach(item ->
                // 记录指令代码
                entrustIdList.add(item.getEntrustId()));
        String partyId = null;
        /*************************************************************组合分配********************************************/
        String repo_pledge_model = CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL);
        if (CollectionUtils.isEmpty(entity.getPtlAllocations())) {
            errorList.add(new ValidMessage("组合分配不可为空"));
        } else {
            // 校验质押券总额(该笔交易,流程中及已生效交易)不能超过指令的质押券总额
            // 交易金额汇总
            BigDecimal tradeAmtTotal = BigDecimal.ZERO;
            // 到期结算金额汇总
            BigDecimal mtradeAmtTotal = BigDecimal.ZERO;
            // 回购利息汇总
            BigDecimal mIntAmtTotal = BigDecimal.ZERO;
            // 交易手续费汇总
            BigDecimal dealFeeAmtTotal = BigDecimal.ZERO;
            // 结算手续费汇总
            BigDecimal settFeeamtTotal = BigDecimal.ZERO;
            List<RegFinPledge> list = new ArrayList<>();
            // 所有限价指令的回购利率中的最小值或最大值
            BigDecimal minOrMaxYield = null;
            // 指定价格的回购利率
            BigDecimal yieldPRM01 = null;
            // 回购利率是否相等
            boolean yieldFlag = true;
            for (RepoPtlAllocationDto ptlAllocatio : entity.getPtlAllocations()) {
                List<ValidMessage> validMessageList = this.checkPtlEmpty(ptlAllocatio, ps);
                if (validMessageList != null && !validMessageList.isEmpty()) {
                    errorList.addAll(validMessageList);
//                    break;
                }
                AjaxResult ajaxResultPartyId = cfetsQuotedCommonServicevue.checkpartyId(ptlAllocatio.getEntrustId(),
                        ptlAllocatio.getPortfolioId(), DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getValue());
                if(StringUtil.isNotEmpty(ajaxResultPartyId.getMessage())){
                    errorList.add(new ValidMessage(ajaxResultPartyId.getMessage()));
                }
                if (cfetsRepoQuotedPrice004Service.ACompareToB(ptlAllocatio.getTradeAmt(), ptlAllocatio.getConvertAmt(), 6) > 0
                        && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                    errorList.add(new ValidMessage("组合分配中交易金额要小于等于质押券折算总额"));
                }
                // 所有限价指令的回购利率中的最小值 初始化
                if (minOrMaxYield == null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 交易方式为正回购时,查找限价指令的回购利率中最小值
                if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())
                        && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = minOrMaxYield.compareTo(com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP)) <= 0
                            ? minOrMaxYield : com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 易方式为逆回购时,查找限价指令的回购利率中最大值
                else if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())
                        && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = minOrMaxYield.compareTo(com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP)) >= 0
                            ? minOrMaxYield : com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 指定价格回购利率 初始化
                if (yieldPRM01 == null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())) {
                    yieldPRM01 = com.joyintech.calculation.util.ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                if (yieldPRM01 != null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())
                        && yieldPRM01.compareTo(ptlAllocatio.getPrice()) != 0) {
                    yieldFlag = false;
                }

                tradeAmtTotal = ArithmeticUtil.doAdd(tradeAmtTotal, ptlAllocatio.getTradeAmt());
                mtradeAmtTotal = ArithmeticUtil.doAdd(mtradeAmtTotal, ptlAllocatio.getMtradeAmt());
                mIntAmtTotal = ArithmeticUtil.doAdd(mIntAmtTotal, ptlAllocatio.getmIntAmt());
                dealFeeAmtTotal = ArithmeticUtil.doAdd(dealFeeAmtTotal, ptlAllocatio.getDealFeeAmt());
                settFeeamtTotal = ArithmeticUtil.doAdd(settFeeamtTotal, ptlAllocatio.getSettFeeAmt());
                // 组合分配中交易金额要小于等于（剩余指令额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价-流程中交易登记额度）
                // 已实现: 剩余指令额度 = 指令额度-生效的交易登记额度-流程中交易登记额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价
                CfetsRepoDealEntrustOrderDto dto = cfetsIbuQuoteRepoMapper.findEtrLastNumByEtrId(ptlAllocatio.getEntrustId(), entity.getQuoteId());
                if (cfetsRepoQuotedPrice004Service.ACompareToB(ptlAllocatio.getTradeAmt(), dto.getAvaNum(), 12) > 0) {
                    errorList.add(new ValidMessage("组合分配中交易金额要小于等于[" + ptlAllocatio.getEntrustId() + "]剩余的指令额度"));
                    break;
                }
                if (StringUtil.isEmpty(partyId)) {
                    partyId = dto.getPartyId();
                }
                if (StringUtil.isNotEmpty(partyId) && !StringUtil.equals(partyId, dto.getPartyId())) {
                    errorList.add(new ValidMessage("所选指令交易账户不同"));
                    break;
                }
                if (entity.getRepoTerm() != dto.getRepoTerm()) {
                    errorList.add(new ValidMessage("指令期限与交易不同"));
                    break;
                }
                String settleSpreedCode = StringUtil.equals(dto.getSettleSpreed(), "0") ?
                        BussinessDictCodeList.清算速度_T加0.getValue() : BussinessDictCodeList.清算速度_T加1.getValue();
                if (!StringUtil.equals(entity.getSettleSpreedCode(), settleSpreedCode)) {
                    errorList.add(new ValidMessage("指令清算速度与交易不同"));
                    break;
                }
            }

            // 交易金额   到期结算金额  回购利息 交易手续费 结算手续费 总和需要相等
            if (StringUtil.isNotEmpty(entity.getTradeAmt())
                    && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getTradeAmt(), tradeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配交易金额总和需要等于交易金额"));
            }
            if (StringUtil.isNotEmpty(entity.getMtradeAmt())
                    && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getMtradeAmt(), mtradeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配到期结算金额总和需要等于到期结算金额"));
            }
            if (StringUtil.isNotEmpty(entity.getmIntAmt())
                    && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getmIntAmt(), mIntAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配回购利息总和需要等于回购利息"));
            }
            if (StringUtil.isNotEmpty(entity.getDealFeeAmt())
                    && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getDealFeeAmt(), dealFeeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配交易手续费总和需要等于交易手续费"));
            }
            if (StringUtil.isNotEmpty(entity.getSettFeeAmt())
                    && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getSettFeeAmt(), settFeeamtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配结算服务费总和需要等于结算服务费"));
            }
            //勾选指令的价格模式只存在指定价格时，回购利率默认等于指定价格指令中的回购利率，可修改，修
            // 改后与默认值不一致时，提交时，弹出提示“ 价格模式为指定价格，交易需要按照指令指定的回购利率进行交易！”
            if (StringUtil.isNotEmpty(yieldPRM01) && !StringUtil.isNotEmpty(minOrMaxYield)
                    && cfetsRepoQuotedPrice004Service.ACompareToB(yieldPRM01, entity.getPrice(), yieldScale) != 0) {
                errorList.add(new ValidMessage("价格模式为指定价格，交易需要按照指令指定的回购利率进行交易！"));
            }
            //交易方向为正回购
            //勾选指令的价格模式只存在限价指令时，回购利率默认为空，
            // 填入值后需不大于限价指令中回购利率的最小值，不满足时提交弹出提示“价格模式为限价指令，交易回购利率不能大于指令指定的回购利率进行交易”
            if (!StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && cfetsRepoQuotedPrice004Service.ACompareToB(minOrMaxYield, entity.getPrice(), yieldScale) < 0 && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                errorList.add(new ValidMessage("价格模式为限价指令，交易回购利率不能大于指令指定的回购利率进行交易！"));
            }
            //交易方向为逆回购
            //勾选指令的价格模式只存在限价指令时，回购利率默认为空，填入值后需不小于限价指令中回购利率的最大值，
            // 不满足时提交弹出提示“价格模式为限价指令，交易回购利率不能小于指令指定的回购利率进行交易”
            if (!StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && cfetsRepoQuotedPrice004Service.ACompareToB(minOrMaxYield, entity.getPrice(), yieldScale) > 0 && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())) {
                errorList.add(new ValidMessage("价格模式为限价指令，交易回购利率不能小于指令指定的回购利率进行交易！"));
            }
            //勾选指令的价格模式同时存在指定价格和限价指令时，回购利率默认为空，填入值后需判断回购利率是否等于指定价格中的回购利率，
            // 不满足提交时，弹出提示“价格模式存在指定价格，交易需要按照指令指定的回购利率进行交易！”
            if (StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && cfetsRepoQuotedPrice004Service.ACompareToB(yieldPRM01, entity.getPrice(), yieldScale) != 0) {
                errorList.add(new ValidMessage("价格模式存在指定价格，交易需要按照指令指定的回购利率进行交易！"));
            }
            //检查指令是否是已接收状态
            Map<String, Object> paramFail = new HashMap<>();
            paramFail.put("entrustIdList", entrustIdList);
            paramFail.put("entrustStatus", BussinessDictCodeList.委托状态_已接收.getValue());
            if (cfetsIbuQuoteBondEtrMapper.countFailEtr(paramFail) > 0) {
                errorList.add(new ValidMessage("指令非已接收状态，不可提交!"));
            }
        }
        /**************************************************************组合分配********************************************/
        /*************************************************************质押券信息*******************************************/
        if (CollectionUtils.isEmpty(entity.getRepoEntrustPledgeDtos())
                && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())) {
            errorList.add(new ValidMessage("质押券不可为空"));
        }
        if (!CollectionUtils.isEmpty(entity.getRepoEntrustPledgeDtos())) {
            // 同个质押券的汇总质押券面总额必须为十万的整数倍，且不超过100亿
//            Map<String, BigDecimal> secPledgePrinMap = new HashMap<>();
            for (RepoEntrustPledgeDto repoEntrustPledgeDto : entity.getRepoEntrustPledgeDtos()) {
                List<ValidMessage> validMessageList = checkPledgeEmpty(repoEntrustPledgeDto, ps);
                if (validMessageList != null && !validMessageList.isEmpty()) {
                    errorList.addAll(validMessageList);
                    break;
                }
//                // 按质押券维度 汇总质押券面总额
//                if (StringUtil.isNotEmpty(secPledgePrinMap.get(repoEntrustPledgeDto.getBondCode()))) {
//                    secPledgePrinMap.put(repoEntrustPledgeDto.getBondCode(),
//                            com.joyintech.calculation.util.ArithmeticUtil.doAdd(secPledgePrinMap.get(repoEntrustPledgeDto.getBondCode()), repoEntrustPledgeDto.getPledgePrin()));
//                } else {
//                    secPledgePrinMap.put(repoEntrustPledgeDto.getBondCode(), repoEntrustPledgeDto.getPledgePrin());
//                }
            }
            // 同个质押券的汇总质押券面总额必须为十万的整数倍，且不超过100亿
//            checkSecPledgePrin(secPledgePrinMap, errorList);
            massge = checkMatchCountPledgeBondList(errorList, entity);
        }
        /*************************************************************质押券信息*******************************************/
        if (CollectionUtils.isEmpty(errorList)) {
            ajaxResult.setMessage(massge, MessageType.确认消息);
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * entity 空值校验
     *
     * @param errorList
     * @author jiangpeng
     * @date 2021/12/17 10:47
     */
    public void checkEntityEmpty(List<ValidMessage> errorList, CfetsRepoDealQuoteCommonDto entity) {
//        if (!StringUtil.isNotEmpty(entity.getQuoteStyle())) {
//            errorList.add(new ValidMessage("请选择报价方式"));
//        }
        /**************************************************************基本信息********************************************/
        if (!StringUtil.isNotEmpty(entity.getPs())) {
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if (!StringUtil.isNotEmpty(entity.getMySideTrader())) {
            errorList.add(new ValidMessage("请输入本方交易员"));
        }
        if (!StringUtil.isNotEmpty(entity.getCounterPartyId())) {
            errorList.add(new ValidMessage("请输入对手方"));
        }
        if (StringUtil.isNotEmpty(entity.getMySide())
                && StringUtil.isNotEmpty(entity.getMySideTrader())) {
            String userId = joyinbusinessCommonService.findUserByMySideTrader(entity.getMySideTrader(), entity.getMySide());
            if (!StringUtil.isNotEmpty(userId)) {
                errorList.add(new ValidMessage("选择的本方交易员未与系统用户关联，请先去【用户管理】关联"));
            }
        }
        // 交易对手未绑定21位码,手方交易员非必填
//        CfetsSearchInfoRequest cfetsSearchInfoRequest = new CfetsSearchInfoRequest();
//        cfetsSearchInfoRequest.setDealType("02");
//        cfetsSearchInfoRequest.setInitValue(entity.getCounterId());
//        List<SelectOptionDto> list = joyinbusinessCommonService.queryMySideOrCount(cfetsSearchInfoRequest);
        if (/*StringUtil.isNotEmpty(list.get(0).getObject())
                &&*/ StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "01")
                && !StringUtil.isNotEmpty(entity.getCounterTrader())
                && StringUtil.equals(entity.getQuoteStyle(), BussinessDictCodeList.报价方式_对话报价.getValue())) {
            errorList.add(new ValidMessage("请输入对手方交易员"));
        }
        if (!StringUtil.isNotEmpty(entity.getRepoMethod())) {
            errorList.add(new ValidMessage("请输入回购方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettleSpreedCode())) {
            errorList.add(new ValidMessage("请输入清算速度"));
        }
        if (!StringUtil.isNotEmpty(entity.getCcy())) {
            errorList.add(new ValidMessage("请输入债券币种"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettCcy())) {
            errorList.add(new ValidMessage("请输入结算币种"));
        }
        if (!StringUtil.isNotEmpty(entity.getRepoTerm())) {
            errorList.add(new ValidMessage("请输入回购期限"));
        }
        if (!StringUtil.isNotEmpty(entity.getPrice())
                || BigDecimal.ZERO.compareTo(entity.getPrice()) == 0) {
            errorList.add(new ValidMessage("请输入回购利率"));
        }
        if (!StringUtil.isNotEmpty(entity.getTradeAmt())
                || BigDecimal.ZERO.compareTo(entity.getTradeAmt()) == 0) {
            errorList.add(new ValidMessage("请输入交易金额"));
        }
        if (!StringUtil.isNotEmpty(entity.getShareAmt())) {
            errorList.add(new ValidMessage("请输入券面总额合计"));
        }
        if (!StringUtil.isNotEmpty(entity.getConvertAmt())) {
            errorList.add(new ValidMessage("请输入质押券折算总额"));
        }
        if (StringUtil.isNotEmpty(entity.getTradeAmt()) && StringUtil.isNotEmpty(entity.getConvertAmt())
                && cfetsRepoQuotedPrice004Service.ACompareToB(entity.getTradeAmt(), entity.getConvertAmt(), 6) > 0) {
            errorList.add(new ValidMessage("交易金额需小于等于质押券折算总额"));
        }
        if (!StringUtil.isNotEmpty(entity.getDeliverytype())) {
            errorList.add(new ValidMessage("请输入首次结算方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getmDeliveryType())) {
            errorList.add(new ValidMessage("请输入到期结算方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettleType())) {
            errorList.add(new ValidMessage("请输入清算类型"));
        }
//        if (!StringUtil.isNotEmpty(entity.getDealFeeAmt())) {
//            errorList.add(new ValidMessage("请输入交易手续费"));
//        }
//        if (!StringUtil.isNotEmpty(entity.getSettFeeAmt())) {
//            errorList.add(new ValidMessage("请输入结算服务费"));
//        }
        /**************************************************************基本信息********************************************/
        if (StringUtil.isNotEmpty(entity.getCounterPartyId())) {
            String trader = cfetsRepoQuotedPrice004Service.findTraderByPartyId(entity.getCounterPartyId());
            if (!StringUtil.isNotEmpty(trader)) {
                errorList.add(new ValidMessage("选择的对手方未与系统中交易对手关联，请先去关联"));
            }
        }
        // 修改时 提交时校验：首次结算方式、到期结算方式必须为券款对付，清算类型必须为全额清算
        if (!StringUtil.equals("edit", entity.getQuotaPageModel())) {
            return;
        }
        if (!StringUtil.equals(entity.getDeliverytype(), BussinessDictCodeList.结算方式_券款对付.getValue())) {
            errorList.add(new ValidMessage("首次结算方式必须为券款对付"));
        }
        if (!StringUtil.equals(entity.getmDeliveryType(), BussinessDictCodeList.结算方式_券款对付.getValue())) {
            errorList.add(new ValidMessage("到期结算方式必须为券款对付"));
        }
        if (!StringUtil.equals(entity.getSettleType(), BussinessDictCodeList.清算类型_全额清算.getValue())) {
            errorList.add(new ValidMessage("清算类型必须为全额清算"));
        }

    }

    /**
     * 组合分配 空值校验
     *
     * @param ptlAllocatio
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2021/12/17 11:07
     */
    public List<ValidMessage> checkPtlEmpty(RepoPtlAllocationDto ptlAllocatio, String ps) {
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(ptlAllocatio.getEntrustId())) {
            errorList.add(new ValidMessage("请输入组合分配指令代码"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPortfolioName())) {
            errorList.add(new ValidMessage("请输入组合分配组合名称"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getAvaNum())) {
            errorList.add(new ValidMessage("请输入组合分配剩余指令额度"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getTradeAmt())) {
            errorList.add(new ValidMessage("请输入组合分配交易金额"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getConvertAmt())
                && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购)) {
            errorList.add(new ValidMessage("请输入组合分配质押券折算总额"));
        }
//        if (!StringUtil.isNotEmpty(ptlAllocatio.getDealFeeAmt())) {
//            errorList.add(new ValidMessage("请输入组合分配交易手续费"));
//        }
//        if (!StringUtil.isNotEmpty(ptlAllocatio.getSettFeeAmt())) {
//            errorList.add(new ValidMessage("请输入组合分配结算服务费"));
//        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getmIntAmt())) {
            errorList.add(new ValidMessage("请输入组合分配回购利息"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getMtradeAmt())) {
            errorList.add(new ValidMessage("请输入组合分配到期结算金额"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPriceModel())) {
            errorList.add(new ValidMessage("请输入组合分配价格模式"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPrice())) {
            errorList.add(new ValidMessage("请输入组合分配指令回购利率"));
        }
        return errorList;
    }

    /**
     * 质押券信息 空值校验
     *
     * @param repoEntrustPledgeDto
     * @param ps
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2021/12/17 11:48
     */
    public List<ValidMessage> checkPledgeEmpty(RepoEntrustPledgeDto repoEntrustPledgeDto, String ps) {
        List<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgePrinStr())) {
            repoEntrustPledgeDto.setPledgePrin(com.joyintech.calculation.util.ArithmeticUtil.multiply(new BigDecimal(repoEntrustPledgeDto.getPledgePrinStr()), new BigDecimal(10000)));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getBondCode())) {
            errorList.add(new ValidMessage("请输入质押券信息债券代码"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getBondAbbrName())) {
            errorList.add(new ValidMessage("请输入质押券信息债券简称"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgePrin())) {
            errorList.add(new ValidMessage("请输入质押券信息质押券面总额"));
        }
        if (BigDecimal.ZERO.compareTo(repoEntrustPledgeDto.getPledgePrin()) >= 0) {
            errorList.add(new ValidMessage("质押券信息质押券面总额需大于零"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgeRatio())) {
            errorList.add(new ValidMessage("请输入质押券信息折算比例"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getRepRegistOrg())) {
            errorList.add(new ValidMessage("请输入质押券信息登记托管机构"));
        }
        if (StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPortfolioId())) {
                errorList.add(new ValidMessage("请输入质押券信息组合代码"));
            }
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPortfolioName())) {
                errorList.add(new ValidMessage("请输入质押券信息组合名称"));
            }
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getAvaPledgePrin())) {
                errorList.add(new ValidMessage("请输入质押券信息可质押券面总额"));
            }
        }
        return errorList;
    }

    /**
     * 校验是否匹配交易对手债券
     *
     * @param secPledgePrinMap
     * @param errorList
     * @author jiangpeng
     * @date 2022/1/5 15:32
     */
    private String checkMatchCountPledgeBondList(List<ValidMessage> errorList, CfetsRepoDealQuoteCommonDto entity) {
        String massege = null;
        if (CollectionUtils.isEmpty(entity.getCountPledgeBondList())) {
            return massege;
        }
        boolean flag = false;
        boolean isBondIdMatch = true;
        for (RepoEntrustPledgeDto dto : entity.getCountPledgeBondList()) {
            BigDecimal pledgePrin = BigDecimal.ZERO;
            BigDecimal pledgeRatio = dto.getPledgeRatio();
            for (RepoEntrustPledgeDto pledgeDto : entity.getRepoEntrustPledgeDtos()) {
                if (!StringUtil.equals(pledgeDto.getFinprodId(), dto.getFinprodId())) {
                    continue;
                }
                if (StringUtil.equals(pledgeDto.getFinprodId(), dto.getFinprodId())) {
                    isBondIdMatch = false;
                }
                if (cfetsRepoQuotedPrice004Service.ACompareToB(pledgeRatio, pledgeDto.getPledgeRatio(), 4) != 0) {
                    flag = true;
                }
                pledgePrin = ArithmeticUtil.doAdd(pledgePrin, pledgeDto.getPledgePrin());
            }
            if (cfetsRepoQuotedPrice004Service.ACompareToB(dto.getPledgePrin(), pledgePrin, 12) != 0) {
                flag = true;
            }
            if (isBondIdMatch) {
                flag = true;
            }
        }
        if (flag) {
            massege = "对手方发送质押券信息和已添加质押券不匹配，是否继续？";
        }
        return massege;
    }

    /**
     * 提交保存check 必填项
     *
     * @param entity 提交数据
     * @return 校验返回信息
     */
    public AjaxResult checkQuotePriceSave(CfetsRepoDealQuoteCommonDto entity) {

        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //模式
        String ps = entity.getPs();
        checkEntityEmpty(errorList, entity);
        // 记录指令代码
        List<String> entrustIdList = new ArrayList<>();
        entity.getPtlAllocations().forEach(item ->
                // 记录指令代码
                entrustIdList.add(item.getEntrustId()));

        return ajaxResult;
    }

    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsRepoDealQuotedPrice001Dto entity, OperationType type) {
        //历史报价页面撤销  不生成业务流程数据  同交易
        if ("save".equals(entity.getOperateType()) || StringUtil.equals(entity.getOperateType(), "revoke")) {
            return;
        }
        this.insertWflRepoDealQuote(entity);
        // 发头寸
        String systemAct = this.handSystemAct(entity);
        this.sendPositionMethod(entity, DictCodeListExt3.流程节点_流程提交.getValue(), systemAct, systemAct, null);
        if (this.isUpdatePosition(entity)) {
            com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto
                    = this.investDto2ServiceDto(entity);
            dto.setIsExistNew(DictCodeList.是否_是.getValue());
            oldCfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(dto);
        }
    }

    /**
     * 判断是否要做持仓变更
     *
     * @param entity
     * @return boolean
     * @author jiangpeng
     * @date 2022/3/25 10:38
     */
    private boolean isUpdatePosition(CfetsRepoDealQuotedPrice001Dto entity) {
        boolean result = this.isUpdatePosition(entity.getQuotaPageModel(),entity.getOperatorType());
        if (!StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            result = false;
        }
        return result;
    }

    /**
     * 判断是否要做持仓变更
     *
     * @param entity
     * @return boolean
     * @author jiangpeng
     * @date 2022/3/25 10:38
     */
    private boolean isUpdatePosition(String quotaPageModel,String operatorType) {
        boolean result = false;
        if (StringUtil.equals(quotaPageModel, "new")) {
            result = true;
        }
        if (StringUtil.equals(quotaPageModel, "edit")) {
            result = true;
        }
        if (StringUtil.equals(quotaPageModel, "dialogueResponse") && StringUtil.equals(operatorType, "done")) {
            result = true;
        }
        return result;
    }


    /**
     * 更新操作
     */
    @Override
    public void doUpdate(CfetsRepoDealQuotedPrice001Dto entity) {
        if (StringUtil.equals("save", entity.getOperatorType())){
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if (!StringUtil.equals("revoke", entity.getOperatorType())
                && !StringUtil.equals("delete", entity.getOperatorType())) {
            // 重新insert
            insertWflRepoDealQuote(entity);
            String confirmOk = RequestUtil.getParam("confirmOk");
            try {
                // 发头寸
                if (StringUtil.isNotEmpty(entity.getWorkflowId())) {
                    SysWflBusinessList sysWflBusinessList = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                    String systemAct = DictCodeListExt3.系统动作_新建.getValue();
                    if (StringUtil.equals(sysWflBusinessList.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                        systemAct = DictCodeListExt3.系统动作_修改.getValue();
                        sendPositionMethod(entity, DictCodeListExt3.流程节点_流程提交.getValue(), systemAct, systemAct, confirmOk);
                    } else {
                        sendPositionMethod(entity, DictCodeListExt3.流程节点_流程提交.getValue(), systemAct, systemAct, confirmOk);
                    }
                }
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                if(StringUtil.isNotEmpty(bm)) {
                    String code = (String) bm.get("code");
                    String messageStr = JsonUtils.json2Object((String) bm.get("message").toString(), String.class);
                    if (StringUtil.isEmpty(confirmOk)
                            && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                        WorkFlowUtil.showMessage(messageStr, MessageType.确认消息);
                    }
                    if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                        WorkFlowUtil.showMessage(messageStr, MessageType.错误消息);
                    }
                    throw new PositionException(messageStr);
                }else {
                    WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                }
            }
            if (this.isUpdatePosition(entity)) {
                com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto
                        = this.investDto2ServiceDto(entity);
                dto.setIsExistNew(DictCodeList.是否_是.getValue());
                oldCfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(dto);
            }
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsRepoDealQuotedPrice001Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("更新失败");
            }
        }
    }

    @Override
    public void doDelete(String workflowId) {
        //作废流程需要处理现金流水
        doDeleteWflDataForRepo(workflowId);
        // 查询数据
        CfetsRepoDealQuotedPrice001Dto entity = getBussinessEntityByWorkflowId(workflowId);
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        //发送头寸
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), DictCodeListExt1.子系统代码_交易.getValue(),
                    DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), entity.getPs());
        }
    }

    /**
     * 退回
     *
     * @param entity
     */
    @Override
    public void doBack(CfetsRepoDealQuotedPrice001Dto entity) {
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            //回退释放头寸
            Map<String, Object> param = new HashMap<>();
            param.put("workflowId", entity.getWorkflowId());
            param.put("pageWflType", "REPO");
            cfetsBondDealQuotedPriceService.doDeletePtlPositionHold(param);
            //退回操作重新占用该条报价单之前的
            if (StringUtil.isNotEmpty(entity.getQuoteId())
                    && this.isUpdatePosition(entity)) {
                CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
                cfetsRepoDealQuotedPrice001Dto.setQuoteId(entity.getQuoteId());
                cfetsRepoDealQuotedPrice001Dto.setIsDzh(DictCodeList.是否_是.getValue());
                cfetsRepoDealQuotedPrice001Dto.setIsKtg(DictCodeList.是否_否.getValue());
                com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto
                        = this.investDto2ServiceDto(cfetsRepoDealQuotedPrice001Dto);
                dto.setIsExistNew(DictCodeList.是否_是.getValue());
                oldCfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(dto);
            }
        }
    }

    /**
     * 处理系统动作
     *
     * @param entity
     * @return
     */
    public String handSystemAct(CfetsRepoDealQuotedPrice001Dto entity) {
        String systemAct = StringUtil.EMPTY;
        if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
            systemAct = DictCodeListExt3.系统动作_新建.getValue();
        }
        if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
            systemAct = DictCodeListExt3.系统动作_修改.getValue();
        }
        if (StringUtil.equals(entity.getQuotaPageModel(), "dialogueResponse") && StringUtil.equals(entity.getOperatorType(), "done")) {
            systemAct = DictCodeListExt3.系统动作_成交.getValue();
        }
        return systemAct;
    }

    /**
     * 校验回复成交的质押品数据
     *
     * @param dto
     * @param dayOffer
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/28 20:43
     */
    public String checkPledgeBondList(CfetsRepoDealQuotedPrice001Dto dto, CfetsRepoDealQuotedPrice001Dto dayOffer) {
        // 验证信息
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        if (!StringUtil.isEmptyList(dayOffer.getPledgeBondList())) {
            if (StringUtil.equals(dayOffer.getPs(), BussinessDictCodeList.回购拆借方向_正回购.getValue())) {
                // 查询债券相关信息
                List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = null;
                List<String> bondCodes = dayOffer.getPledgeBondList().stream().map(obj -> obj.getBondCode())
                        .collect(Collectors.toList());
                Map<String, Object> param = new HashMap<>();
                param.put("portfolioId", dto == null ? dayOffer.getPortfolioId() : dto.getPortfolioId());
                param.put("secAcctId", dto == null ? "" : dto.getSecAcctId());
                param.put("finprodMarketIds", bondCodes);
                // 正回购
                pledgeBondList = cfetsRepoDealQuotedPrice003Service.queryPledgeBondList(param).getRoot();
                if (!StringUtil.isEmptyList(pledgeBondList)) {
                    pledgeBondList.forEach(x -> {
                        dayOffer.getPledgeBondList().forEach(obj -> {
                            if (obj.getBondCode().equals(x.getBondCode())) {
                                // 校验本次质押是否大于剩余可质押面额
                                if (x.getAvaPledgeNumber().compareTo(
                                        ArithmeticUtil.multiply(obj.getPledgePrin(), new BigDecimal(10000))) < 0) {
                                    validList.add(
                                            new ValidMessage(this.getMessage("CFETS_COMMON_R_025", obj.getBondCode())));
                                }
                            }
                        });
                    });
                }
            }
        }
        String result = "";
        if (!StringUtil.isEmptyList(validList)) {
            for (ValidMessage validMessage : validList) {
                result += validMessage.getMessage();
            }
        }
        return result;
    }

    /**
     * 撤销报价
     *
     * @param param
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:43
     */
    public boolean revert(QuotedPriceRequest request) {
        for (QuotedPriceRequest data : request.getRequests()) {
            String operation = data.getOperatorType();
            String quoteId = data.getQuoteId();
            String quotaPageModel = data.getQuotaPageModel();

            CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = this.queryRepoInfoByQuoteId(quoteId, operation, quotaPageModel);
            // 画面模式
            if (StringUtil.isNotEmpty(quotaPageModel)) {
                cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel(quotaPageModel);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("quoteId", quoteId);
            //组合分配列表初始化
            cfetsRepoDealQuotedPrice001Dto.setPtlAllocationList(this.selectPortfolioInit(map));
            Map<String, Object> pageParam = BeanUtil.object2Map(cfetsRepoDealQuotedPrice001Dto);
            pageParam.put("userName", LoginUserUtil.getUserName());
            pageParam.put("deptCode", LoginUserUtil.getDeptCode());
            ibuComponentCommonService.revert(pageParam);
        }
        return true;
    }

    /**
     * 根据报价代码查询报价信息
     *
     * @param quoteId   报价代码
     * @param operation 是否复制操作
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/30 17:39
     */
    public CfetsRepoDealQuotedPrice001Dto queryRepoInfoByQuoteId(String quoteId, String operation, String quotaPageModel) {
        // 根据报价id查询数据
        CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = this.queryEntrustInfoByQuoteId(quoteId);
        //逆回购  质押券列表  可质押以万元为单位
        if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue()) && !StringUtil.isEmptyList(cfetsRepoDealQuotedPrice001Dto.getPledgeBondList())) {
            cfetsRepoDealQuotedPrice001Dto.getPledgeBondList().forEach(item -> {
                item.setPledgePrin(ArithmeticUtil.divide(item.getPledgePrin(), new BigDecimal(10000), 2));
            });
        }
        if (StringUtil.equals(operation, "copy")
                && StringUtil.isNotEmpty(quoteId)) {
            cfetsRepoDealQuotedPrice001Dto.setQuoteId(null);
        }
//        if (cfetsRepoDealQuotedPrice001Dto.getQuoteId() != null) {
//            // 交谈记录
//            cfetsRepoDealQuotedPrice001Dto.setDialogueRecord(this.selectDialogueRecord(cfetsRepoDealQuotedPrice001Dto).getDialogueRecord());
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("quoteId", quoteId);
        //多组合质押券列表单独处理
        map.put("platDate", iPlatformDate.getSysDateStr());
        map.put("ps", cfetsRepoDealQuotedPrice001Dto.getPs());
        BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
        List<CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrice003s = this.selectQuoteCrepPledgeForDzh(map);
        if (CollectionUtils.isNotEmpty(cfetsRepoDealQuotedPrice003s)) {
            cfetsRepoDealQuotedPrice001Dto.setPledgeBondList(cfetsRepoDealQuotedPrice003s);
        }
        return cfetsRepoDealQuotedPrice001Dto;
    }

    /**
     * 成交
     *
     * @param param
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:43
     */
    public boolean deal(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto) {
        long startTime = System.currentTimeMillis();    //获取开始时间
        RepoDialogueQuoteDto inputBean = ibuComponentCommonService.convertRepoDialogueQuoteDto(dto);
        com.joyintech.background.component.common.pack.common.security.User userInfo = new com.joyintech.background.component.common.pack.common.security.User();
        userInfo.setUserName(LoginUserUtil.getUserName());
        userInfo.setDeptCode(LoginUserUtil.getDeptCode());

        QuoteResultDto quoteResultDto = this.sendRepoDialogueQuote(com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方回复成交.getValue(),
                inputBean, null);
        // 组件调用共通处理
        ibuComponentCommonService.resCommonHandle(quoteResultDto, userInfo, true);

        long endTime = System.currentTimeMillis();
        Date startDate = new Date();
        startDate.setTime(startTime);
        Date endDate = new Date();
        endDate.setTime(endTime);
        logger.info("deal程序运行开始时间：" + new SimpleDateFormat().format(startDate));
        logger.info("deal程序运行结束时间：" + new SimpleDateFormat().format(endDate)); //程序运行结束时间
        logger.info("deal程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        return true;
    }

    /**
     * 发送质押式回购报价反馈
     */
    private QuoteResultDto sendRepoDialogueQuote(String exeTypeIBU, RepoDialogueQuoteDto inputbean,
                                                 Map<String, String> attrMap) {
        QuoteResultDto result = new QuoteResultDto(true);
        IbuQuoteCrepDto ibuQuoteCrepDto = inputbean.getIbuQuoteCrepDto();
        Long version = ibuQuoteCrepDto.getVersion();
        Long bVersion = ibuQuoteCrepDto.getbVersion();
        inputbean.setVersion(version, bVersion);

        // 对话报价查询
        boolean flag = StringUtil.equals(exeTypeIBU, com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方回复成交.getValue())
                ? true : false;
        if (inputbean.getIbuQuoteCrepDto() != null) {
            inputbean.setExeTypeIBU(exeTypeIBU);
            // 现券对话报价必须项校验
            ResultBaseDto validation = routeIntfSubService.inputRepoDlgCheck(inputbean);

            // 校验不通过
            if (validation != null && !validation.isStatus()) {
                result.setStatus(false);
                result.setErrCode(validation.getErrCode());
                result.setErrMsg(validation.getErrMsg());
            } else {
                // 获取路由配置方案
                IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(attrMap);

                // 对话报价状态检验
                ValidMessageDto validResult =
                        repoDialogueQuoteSubService.validateRepoDialogueQuoteStatus(inputbean, false);

                //更新bondDialogueQuote对象中的交易状态、操作类型和发送状态
                repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(inputbean);

                if (com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(exeTypeIBU)
                        || com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方回复拒绝.getValue().equals(exeTypeIBU)) {
                    //更新bondDialogueQuote对象中的组合代码和委托代码
                    if (ibuQuoteCrepDto.getPortfolioId() != null) {
                        inputbean.getIbuQuoteCrepDto().setPortfolioId(ibuQuoteCrepDto.getPortfolioId());
                    }
                    if (ibuQuoteCrepDto.getEntrustId() != null) {
                        inputbean.getIbuQuoteCrepDto().setEntrustId(ibuQuoteCrepDto.getEntrustId());
                    }
                }

                // 校验通过
                if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
                    // 如果是回退操作，则需要回退历史数据
                    if (com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方报价回退.getValue().equals(exeTypeIBU)) {
                        result = repoDialogueQuoteSubService.restoreRepoDialogueQuoteByKey(inputbean);
                    } else if (flag) {
                        result = repoDialogueQuoteSubService.updateRepoDialogueQuoteByKeyDeal(inputbean);
                    } else {
                        result = repoDialogueQuoteSubService.updateRepoDialogueQuoteByKey(inputbean);
                    }

                    if (intfScheme != null) {
                        result.setSendType(intfScheme.getSendType());
                    }
                } else {
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(validResult.getMessageWithId());
                }
            }
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00018");
            result.setErrMsg(getMessage("BC_IBUPLINK_00018", ibuQuoteCrepDto.getQuoteId()));
        }

        return result;
    }

    /**
     * 修改
     *
     * @param dto
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:43
     */
    public boolean modify(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto) {
        ibuComponentCommonService.modify(dto);
        return true;
    }

    /**
     * 拒绝
     *
     * @param param
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:42
     */
    public boolean reject(CfetsRepoDealQuotedPrice001Dto entity) {
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("quoteId", entity.getQuoteId());
        pageParam.put("isDzh", DictCodeList.是否_是.getValue());
        pageParam.put("userName", LoginUserUtil.getUserName());
        pageParam.put("deptCode", LoginUserUtil.getDeptCode());
        ibuComponentCommonService.reject(pageParam);
        CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
        cfetsOprHisInfoRequest.setEntrustId(entity.getQuoteId());
        cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_拒绝.getValue());
        joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
        return true;
    }

    /**
     * 回退
     *
     * @param dto
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:42
     */
    public boolean restoreDayOff(QuotedPriceRequest request) {
        String operation = request.getOperatorType();
        String quoteId = request.getQuoteId();
        String quotaPageModel = request.getQuotaPageModel();

        CfetsRepoDealQuotedPrice001Dto dto = new CfetsRepoDealQuotedPrice001Dto();
        dto = this.queryRepoInfoByQuoteId(quoteId, operation, quotaPageModel);
        // 画面模式
        if (StringUtil.isNotEmpty(quotaPageModel)) {
            dto.setQuotaPageModel(quotaPageModel);
        }
        //多组合
        Map<String, Object> map = new HashMap<>();
        map.put("quoteId", quoteId);
        //组合分配列表初始化
        dto.setPtlAllocationList(this.selectPortfolioInit(map));
        Map<String, Object> param = BeanUtil.object2Map(dto);
        param.put("userName", LoginUserUtil.getUserName());
        param.put("deptCode", LoginUserUtil.getDeptCode());
        ibuComponentCommonService.restoreDayOff(param);
        com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto =
                JSON.parseObject(JSON.toJSONString(param), com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto.class);
        if (this.isUpdatePosition(quotaPageModel, operation)) {
            cfetsRepoDealQuotedPrice001Dto.setIsExistNew(DictCodeList.是否_是.getValue());
            oldCfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(cfetsRepoDealQuotedPrice001Dto);
        }
        return true;
    }

    /**
     * 删除
     *
     * @param dto
     * @return boolean
     * @author jiangpeng
     * @date 2021/12/28 20:46
     */
    public boolean deleteDayOff(QuotedPriceRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("userName", LoginUserUtil.getUserName());
        param.put("deptCode", LoginUserUtil.getDeptCode());
        ibuComponentCommonService.deleteDayOff(param);
        if (StringUtil.isNotEmpty(request.getQuoteId())
                && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            cfetsRepoDealQuotedPrice001Service.doDeletePtlPositionHoldByQuoteId(request.getQuoteId());
        }
        return true;
    }

    @Override
    public void doComplete(CfetsRepoDealQuotedPrice001Dto entity, OperationType type) {
        com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto
                = this.investDto2ServiceDto(entity);
        Map<String, Object> pageParam = BeanUtil.object2Map(entity);
        CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
        cfetsOprHisInfoRequest.setEntrustId(entity.getQuoteId());
        cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        if (StringUtil.equals(entity.getQuotaPageModel(), "dialogueResponse")) {
            // 对话回应
            if (StringUtil.equals(entity.getOperatorType(), "done")) {
                // 成交
                this.deal(dto);
                cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_成交.getValue());
            } else if (StringUtil.equals(entity.getOperatorType(), "refuse")) {
                // 拒绝
                this.reject(entity);
            }
        } else if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
            // 新增
            if (StringUtil.equals(entity.getOperatorType(), "save")) {
                // 保存
                this.insertRepoDealQuote(dto);
            } else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
                // 提交
                this.sendNewRepoDealQuote(dto);
            }
        } else if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
            // 修改
            if (StringUtil.equals(entity.getOperatorType(), "save")) {
                // 保存
                this.insertRepoDealQuote(dto);
            } else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
                // 提交
                this.modify(dto);
            }
            cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        } else {
            //撤销
            if (StringUtil.equals(entity.getOperatorType(), "revoke")) {
                this.doHisRevoke(dto);
                cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
            }
        }
        doDeleteWflDataForRepo(entity.getWorkflowId());
        //头寸
        if (type.equals(OperationType.REVOKE) || type.equals(OperationType.DELETE)) {
            PositionHttpRequest.dorevoke(entity.getQuoteId(), DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), entity.getPs());
        }
        joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
    }

    /**
     * 处理指令完成度
     *
     * @param cfetsRepoDealDbDto 入库dto
     * @param operationType      操作类型
     */
    public void insertEtrFinishStatus(CfetsRepoDealQuotedPrice001Dto entity, OperationType operationType) {
        List<PtlAllocationDto> ptlAllocationDtos = entity.getPtlAllocationList();
        if (CollectionUtils.isNotEmpty(ptlAllocationDtos)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            for (PtlAllocationDto ptlAllocationDto : ptlAllocationDtos) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                status.setEntrustId(ptlAllocationDto.getEntrustId());
                status.setTradeId(entity.getQuoteId());
                status.setCstpTradeId(entity.getQuoteId());
                status.setFinishDate(entity.getTradeDate());
                status.setEntFinishAmt(ptlAllocationDto.getFpriceAmt());
                status.setTrader(entity.getMySideTrader());
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    baseEtrInvestInfoServiceImpl.cancelCompleteDelegate(status.getEntrustId(), status.getTradeId());
                }
                etrFinishStatusDtos.add(status);
            }
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                baseEtrInvestInfoServiceImpl.completeDelegate(etrFinishStatusDtos);
            }
        }
    }

    /**
     * 质押券对话报价新增<br />
     * 对话报价信息入库
     *
     * @param cfetsRepoDealQuotedPrice001Dto 质押式回购报价信息
     * @author jiangpeng
     * @date 2021/12/28 20:46
     */
    @Transactional
    public void insertRepoDealQuote(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        // 调用银行间上行组件处理
        ibuComponentCommonService.insertRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);

    }

    /**
     * 质押券对话报价新增<br />
     * 对话报价信息入库
     *
     * @param cfetsRepoDealQuotedPrice001Dto 质押式回购报价信息
     * @author jiangpeng
     * @date 2021/12/28 20:47
     */
    @Transactional
    public void sendNewRepoDealQuote(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        // 调用银行间上行组件处理
        ibuComponentCommonService.sendNewRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);

    }

    /**
     * 历史报价撤销
     *
     * @param cfetsRepoDealQuotedPrice001Dto 质押式回购报价信息
     * @author jiangpeng
     * @date 2021/12/28 20:48
     */
    public void doHisRevoke(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        QuoteResultDto quoteResultDto = ibuComponentCommonService.sendRepoDialogueQuoteCancel(
                cfetsRepoDealQuotedPrice001Dto.getQuoteId(), cfetsRepoDealQuotedPrice001Dto);
        //撤销删除流水
    }

    /**
     * 根据实际指令类型来生成流程信息
     *
     * @param dto
     * @return com.joyintech.workflow.engine.dto.WorkFlowParameter<com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto>
     * @author jiangpeng
     * @date 2021/12/28 20:48
     */
    public WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto> getWorkFlowParameter(CfetsRepoDealQuotedPrice001Dto dto) {
        WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto> parameter = new WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto>();
        return parameter;
    }


    /**
     * 插入流程表数据
     *
     * @param dto
     * @return java.lang.Boolean
     * @author jiangpeng
     * @date 2021/12/28 20:48
     */
    public Boolean insertWflRepoDealQuote(CfetsRepoDealQuotedPrice001Dto dto) {
        com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto
                = new com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice001Dto, dto);
        // 质押券转换
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> pledgeBondLists = new ArrayList<>();
        for (CfetsRepoDealQuotedPrice003Dto pledgeDto : dto.getPledgeBondList()) {
            com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto pledgeBondDto =
                    new com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto();
            BeanUtil.copyPropertiesIgnoreNull(pledgeBondDto, pledgeDto);
            pledgeBondLists.add(pledgeBondDto);
        }
        cfetsRepoDealQuotedPrice001Dto.setPledgeBondList(pledgeBondLists);
        // 调用银行间上行组件处理
        ibuComponentCommonService.insertWflRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);
        return true;
    }

    /**
     * @param workflowId
     */
    private void doDeleteWflDataForRepo(String workflowId) {
        if (workflowId != null) {
            ibuComponentCommonService.deleteWflDataForRepo(workflowId);
        }
    }

    /**
     * 功能描述   check是否存在流程中
     *
     * @param * @param quoteId
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author zhangnian
     * @date 2021/1/15 0015
     */
    public List<ValidMessage> isInWflProcess(String quoteId, String pageId) {
        //check流程中
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
        wflValidInputParameter.setPageId(StringUtil.isNotEmpty(pageId) ? pageId : "CfetsQuoteRepo003");
        //设置报价编号
        wflValidInputParameter.setBusinessDataPk1(quoteId);
        if (StringUtil.isNotEmpty(mstWorkflowCommonServie) && mstWorkflowCommonServie.isInWflProcess(wflValidInputParameter)) {
            validList.add(new ValidMessage(getMessage("BUSS_COM_E_0023")));
        }
        return validList;
    }

    /**
     * 判断报价是否流程中
     *
     * @param quoteId
     * @return boolean
     * @author jiangpeng
     * @date 2022/1/4 11:36
     */
    public boolean isInWflProcess(String quoteId) {
        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
        wflValidInputParameter.setPageId("CfetsQuoteRepo003");
        //设置报价编号
        wflValidInputParameter.setBusinessDataPk1(quoteId);
        if (StringUtil.isNotEmpty(mstWorkflowCommonServie) && mstWorkflowCommonServie.isInWflProcess(wflValidInputParameter)) {
            return true;
        }
        return false;
    }


    /**
     * 流程中查询报价对话历史
     *
     * @param dto
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/30 17:34
     */
    public CfetsRepoDealQuotedPrice001Dto selectDialogueRecord(CfetsRepoDealQuotedPrice001Dto dto) {
        //质押式回购报价历史
        Map<String, Object> map = new HashMap<>();
        map.put("quoteId", dto.getQuoteId());
        map.put("EXE_TYPE_IBU_03", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue());
        map.put("EXE_TYPE_IBU_07", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue());
        map.put("EXE_TYPE_IBU_67", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue());
        map.put("EXE_TYPE_IBU_63", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue());
        map.put("EXE_TYPE_IBU_25", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈修改.getValue());
        map.put("EXE_TYPE_IBU_24", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈新增.getValue());
        map.put("EXE_TYPE_IBU_19", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价新增.getValue());
        map.put("EXE_TYPE_IBU_20", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价修改.getValue());
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> list = cfetsRepoDealQuotedPrice001Mapper.queryCfetsRepoHis(map);
        String msg = getDialogueRecord(list);
        dto.setDialogueRecord(msg);
        return dto;
    }

    public String getDialogueRecord(List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> list) {
        if (!StringUtil.isEmptyList(list)) {
            //交谈记录
            String msg = "";
            if (StringUtil.isNotEmpty(list) && list.size() > 1) {
                for (int j = list.size() - 1; j > 0; j--) {
                    String rowMsg = "";
                    //质押券提示记录
                    String pledgeBondMsg = "";
                    int i = j - 1;
                    //质押券对比
                    if (!StringUtil.isEmptyList(list.get(i).getPledgeBondList())) {
                        Map<String, com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> map_i = list.get(i).getPledgeBondList().stream().
                                collect(Collectors.toMap(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto::getFinprodId, e -> e));
                        Map<String, com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> map_j = list.get(j).getPledgeBondList().stream().
                                collect(Collectors.toMap(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto::getFinprodId, e -> e));
                        if (StringUtil.isEmptyList(list.get(j).getPledgeBondList())) {
                            for (com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(i).getPledgeBondList()) {
                                if ("".equals(pledgeBondMsg)) {
                                    pledgeBondMsg += "修改" + handleCss("质押券明细");
                                }
                                pledgeBondMsg += "删除债券 " + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName()) + ";";
                            }
                        } else {
                            for (com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(j).getPledgeBondList()) {
                                com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto temp = map_i.get(cfetsRepoDealQuotedPrice003Dto.getFinprodId());
                                if (temp == null) {
                                    if ("".equals(pledgeBondMsg)) {
                                        pledgeBondMsg += "修改" + handleCss("质押券明细");
                                    }
                                    pledgeBondMsg += "增加债券 " + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName())
                                            + "券面总额(万元) " + handleCss(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)).toString()) + ","
                                            + "折算比例(%) " + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString()) + ";";
                                } else {
                                    //修改质押券字段对比
                                    Boolean flag = true;
                                    if (temp.getPledgeRate().compareTo(cfetsRepoDealQuotedPrice003Dto.getPledgeRate()) != 0) {
                                        if ("".equals(pledgeBondMsg)) {
                                            pledgeBondMsg += "修改" + handleCss("质押券明细");
                                        }
                                        if (flag) {
                                            pledgeBondMsg += "修改债券" + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName());
                                            flag = false;
                                        }
                                        pledgeBondMsg += ",将" + handleCss("折算比例(%)") + "从" + handleCss(temp.getPledgeRate().toString()) + "修改为" + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString());
                                    }
                                    if (temp.getPledgePrin().compareTo(cfetsRepoDealQuotedPrice003Dto.getPledgePrin()) != 0) {
                                        if ("".equals(pledgeBondMsg)) {
                                            pledgeBondMsg += "修改" + handleCss("质押券明细");
                                        }
                                        if (flag) {
                                            pledgeBondMsg += "修改债券" + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName());
                                        }
                                        pledgeBondMsg += ",将" + handleCss("券面总额(万元)") + "从" + handleCss(temp.getPledgePrin().divide(new BigDecimal(10000)).toString()) + "修改为" + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgePrin().divide(new BigDecimal(10000)).toString()) + ";";
                                    }
                                    if (!"".equals(pledgeBondMsg)) {
                                        pledgeBondMsg += ";";
                                    }
                                }
                            }
                            for (com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(i).getPledgeBondList()) {
                                com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto temp = map_j.get(cfetsRepoDealQuotedPrice003Dto.getFinprodId());
                                if (temp == null) {
                                    if ("".equals(pledgeBondMsg)) {
                                        pledgeBondMsg += "修改" + handleCss("质押券明细");
                                    }
                                    pledgeBondMsg += "删除债券 " + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName()) + ";";
                                }
                            }
                        }
                    } else {
                        if (!StringUtil.isEmptyList(list.get(j).getPledgeBondList())) {
                            for (com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(j).getPledgeBondList()) {
                                if ("".equals(pledgeBondMsg)) {
                                    pledgeBondMsg += "修改" + handleCss("质押券明细");
                                }
                                pledgeBondMsg += "增加债券" + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName()) + "</span>"
                                        + "券面总额(万元) " + handleCss(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)).toString()) + ","
                                        + "折算比例(%) " + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString());
                            }
                        }
                    }
                    if ("".equals(rowMsg) && !"".equals(pledgeBondMsg)) {
                        rowMsg = handleMsg(rowMsg, list.get(j));
                    }
                    rowMsg += pledgeBondMsg;
                    //组合代码
        			/*if(!StringUtil.equals(list.get(i).getPortfolioId(), list.get(j).getPortfolioId())) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("组合")+"从"+handleCss(list.get(i).getPortfolioId()) + "修改为"+handleCss(list.get(j).getPortfolioId()) + ";";
        			}*/
                    //回购期限
                    if (!StringUtil.equals(String.valueOf(list.get(i).getTerm()), String.valueOf(list.get(j).getTerm()))) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("回购期限") + "从" + handleCss(String.valueOf(list.get(i).getTerm())) + "修改为" + handleCss(String.valueOf(list.get(j).getTerm())) + ";";
                    }
                    //回购利率
                    if (list.get(i).getPrice().compareTo(list.get(j).getPrice()) != 0) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("回购利率(%)") + "从" + handleCss(String.valueOf(list.get(i).getPrice())) + "修改为" + handleCss(String.valueOf(list.get(j).getPrice())) + ";";
                    }
                    //清算速度
                    String spreed_i = list.get(i).getSettleSpreed();
                    if (StringUtil.equals(list.get(i).getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())
                            || StringUtil.equals(list.get(i).getSettleSpreed(), "0")) {
                        spreed_i = BussinessDictCodeList.清算速度_T加0.getCodeName();
                    } else if (StringUtil.equals(list.get(i).getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())
                            || StringUtil.equals(list.get(i).getSettleSpreed(), "1")) {
                        spreed_i = BussinessDictCodeList.清算速度_T加1.getCodeName();
                    }
                    String spreed_j = list.get(j).getSettleSpreed();
                    if (StringUtil.equals(list.get(j).getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())
                            || StringUtil.equals(list.get(j).getSettleSpreed(), "0")) {
                        spreed_j = BussinessDictCodeList.清算速度_T加0.getCodeName();
                    } else if (StringUtil.equals(list.get(j).getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())
                            || StringUtil.equals(list.get(j).getSettleSpreed(), "1")) {
                        spreed_j = BussinessDictCodeList.清算速度_T加1.getCodeName();
                    }
                    if (!StringUtil.equals(spreed_i, spreed_j)) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("清算速度") + "从" + handleCss(spreed_i) + "修改为" + handleCss(spreed_j) + ";";
                    }
                    //首次结算方式
                    if (!StringUtil.equals(list.get(i).getvDeliveryType(), list.get(j).getvDeliveryType())) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("首次结算方式") + "从" + handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, list.get(i).getvDeliveryType())) + "修改为" + handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, list.get(j).getvDeliveryType())) + ";";
                    }
                    //到期结算方式
                    if (!StringUtil.equals(list.get(i).getmDeliveryType(), list.get(j).getmDeliveryType())) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("到期结算方式") + "从" + handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, list.get(i).getmDeliveryType())) + "修改为" + handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, list.get(j).getmDeliveryType())) + ";";
                    }
                    //交易金额
                    if (!StringUtil.equals(list.get(i).getDealAmt().toString(), list.get(j).getDealAmt().toString())) {
                        if ("".equals(rowMsg)) {
                            rowMsg = handleMsg(rowMsg, list.get(j));
                        }
                        rowMsg += "将" + handleCss("交易金额") + "从" + handleCss(String.valueOf(list.get(i).getDealAmt())) + "修改为" + handleCss(String.valueOf(list.get(j).getDealAmt())) + ";";
                    }
                    //券面总额合计
                    if (list.get(i).getPledgePrinTotal() != null && list.get(j).getPledgePrinTotal() != null) {
                        if (list.get(i).getPledgePrinTotal().compareTo(list.get(j).getPledgePrinTotal()) != 0) {
                            if ("".equals(rowMsg)) {
                                rowMsg = handleMsg(rowMsg, list.get(j));
                            }
                            rowMsg += "将" + handleCss("券面总额合计(万元)") + "从" + handleCss(String.valueOf(list.get(i).getPledgePrinTotal().divide(new BigDecimal(10000)))) + "修改为" + handleCss(String.valueOf(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)))) + ";";
                        }
                    }
                    if (!"".equals(rowMsg)) {
                        rowMsg += "</br>";
                    }
                    msg += rowMsg;
                }
            }
//            msg = HtmlUtils.htmlEscape(msg);
            return msg;
        }
        return "";

    }

    /**
     * css字体加红
     *
     * @param msg
     * @return
     */
    public String handleCss(String msg) {
        return "<span style='color:red'>" + msg + "</span>";
    }

    /**
     * 信息拼接
     *
     * @param msg
     * @param dto
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/28 16:51
     */
    public String handleMsg(String msg, com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto) {
        String date = DateUtil.format(dto.getUpdateTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
        msg += date + " ";
        if (StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue())
                || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue())
                || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue())
                || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue())) {
            msg += dto.getMySideTraderName() + " ";
        } else {
            msg += dto.getCounterTraderName() + " ";
        }
        return msg;
    }

    /**
     * 单独处理页面质押券列表
     *
     * @param cfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/28 11:36
     */
    public void 单独处理页面质押券列表(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
        if (null == cfetsRepoDealQuotedPrice001Dto.getBussRepoSec()) {
            if (!StringUtil.isEmptyList(cfetsRepoDealQuotedPrice001Dto.getPledgeBondList())) {
                List<PledgeBondInfoDto> pledgeBondList = new ArrayList<>();
                cfetsRepoDealQuotedPrice001Dto.getPledgeBondList().forEach(item -> {
                    PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                    BeanUtil.copyPropertiesIgnoreNull(pledgeBondInfoDto, item);
                    pledgeBondInfoDto.setPledgeRatio(item.getPledgeRate());
                    pledgeBondInfoDto.setFirstNetPrice(item.getNetPrice());
                    pledgeBondList.add(pledgeBondInfoDto);
                });
                bussRepoSecDto.setPledgeBondList(pledgeBondList);
                if (null != cfetsRepoDealQuotedPrice001Dto.getPortfolioId()) {
                    bussRepoSecDto.setPortfolioId(cfetsRepoDealQuotedPrice001Dto.getPortfolioId());
                }
                if (null != cfetsRepoDealQuotedPrice001Dto.getPs()) {
                    bussRepoSecDto.setPs(cfetsRepoDealQuotedPrice001Dto.getPs());
                }
            }
            cfetsRepoDealQuotedPrice001Dto.setBussRepoSec(bussRepoSecDto);
        }
    }

    /**
     * 前端dto转后端dto
     *
     * @param pageDto 前端传入dto
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/24 9:40
     */
    public CfetsRepoDealQuotedPrice001Dto page2BackDto(CfetsRepoDealQuoteCommonDto pageDto) {
        CfetsRepoDealQuotedPrice001Dto backDto = new CfetsRepoDealQuotedPrice001Dto();
        // 证券账户
        String secAcctId = StringUtil.EMPTY;
        if (CollectionUtils.isNotEmpty(pageDto.getRepoEntrustPledgeDtos()) && CollectionUtils.isNotEmpty(pageDto.getPtlAllocations())) {
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", pageDto.getPtlAllocations().get(0).getPortfolioId());
            map.put("finprodId", pageDto.getRepoEntrustPledgeDtos().get(0).getFinprodId());
            secAcctId = this.qryPortfolioSecAcctId(map);
        }
        pageDto.setSecManageAcctId(secAcctId);
        BeanUtil.copyPropertiesIgnoreNull(backDto, pageDto);
        // 卷面总额合计(万) 转换
        backDto.setPledgePrinTotal(pageDto.getShareAmt());
        // 交易金额 转换
        backDto.setDealAmt(pageDto.getTradeAmt());
        // 到期结算金额 转换
        backDto.setmTradeAmt(pageDto.getMtradeAmt());
        // 计息基础
        backDto.setBasis(DictCodeList.计息基础_A_365.getValue());
        // 是否多组合
        backDto.setIsDzh(DictCodeList.是否_是.getValue());
        // 回购方式 双边回购(1)
        backDto.setRepoMethod(pageDto.getRepoMethod());
        // 首次结算方式
        backDto.setvDeliveryType(pageDto.getDeliverytype());
        // 清算速度
        backDto.setSettleSpreed(pageDto.getSettleSpreedCode());
        // 结算币种
        backDto.setSettleCcy(pageDto.getSettCcy());
        // 首次结算日
        backDto.setvSettleDate(pageDto.getSettlementStartDate());
        // 到期结算日
        backDto.setmSettleDate(pageDto.getSettlementEndDate());
        // 实际占款天数
        backDto.setActDays(pageDto.getActCapDays());
        // 回购期限
        backDto.setTerm(pageDto.getRepoTerm());
        // 质押券折算总金额
        backDto.setShareCtAmt(pageDto.getConvertAmt());
        // 分配方式
        backDto.setDipType(pageDto.getDistribution());
        // 证券账户
        backDto.setSecAcctId(pageDto.getSecManageAcctId());
        // 交易对手 21位码
        if (StringUtil.isNotEmpty(pageDto.getCounterPartyId())) {
            backDto.setCounterId(pageDto.getCounterPartyId());
        }
        List<CfetsRepoDealQuotedPrice003Dto> pledgeBondLists = new ArrayList<>();
        for (RepoEntrustPledgeDto pledgeDto : pageDto.getRepoEntrustPledgeDtos()) {
            CfetsRepoDealQuotedPrice003Dto pledgeBondDto = new CfetsRepoDealQuotedPrice003Dto();
            BeanUtil.copyPropertiesIgnoreNull(pledgeBondDto, pledgeDto);
            // 折算比例
            pledgeBondDto.setPledgeRate(pledgeDto.getPledgeRatio());
            pledgeBondDto.setQuoteId(pageDto.getQuoteId());
            pledgeBondLists.add(pledgeBondDto);
        }
        backDto.setPledgeBondList(pledgeBondLists);
        List<PtlAllocationDto> ptlAllocationList = new ArrayList<>();
        for (RepoPtlAllocationDto ptlAllocationDto : pageDto.getPtlAllocations()) {
            PtlAllocationDto ptlDto = new PtlAllocationDto();
            BeanUtil.copyPropertiesIgnoreNull(ptlDto, ptlAllocationDto);
            ptlDto.setQuoteId(pageDto.getQuoteId());
            // 交易金额
            ptlDto.setFpriceAmt(ptlAllocationDto.getTradeAmt());
            // 回购期限
            ptlDto.setTermDays((long) pageDto.getRepoTerm());
            //折算券面总额
            ptlDto.setShareCtAmt(ptlAllocationDto.getConvertAmt());
            //券面总额
            ptlDto.setShareAmt(ptlAllocationDto.getShareAmt());
            //回购利息
            ptlDto.setIntAmt(ptlAllocationDto.getmIntAmt());
            ptlAllocationList.add(ptlDto);
        }
        backDto.setPtlAllocationList(ptlAllocationList);
        return backDto;
    }

    /**
     * 后端dto转前端dto
     *
     * @param backDto 后端传入dto
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/24 9:40
     */
    public CfetsRepoDealQuoteCommonDto back2PageDto(CfetsRepoDealQuotedPrice001Dto backDto) {
        CfetsRepoDealQuoteCommonDto pageDto = new CfetsRepoDealQuoteCommonDto();
        BeanUtil.copyPropertiesIgnoreNull(pageDto, backDto);
        // 卷面总额合计(万) 转换 元->万元
        pageDto.setShareAmt(backDto.getPledgePrinTotal());
        // 交易金额 转换
        pageDto.setTradeAmt(backDto.getDealAmt());
        // 到期结算金额 转换
        pageDto.setMtradeAmt(backDto.getmTradeAmt());
        // 回购方式 双边回购(1)
        pageDto.setRepoMethod(StringUtil.isNotEmpty(backDto.getRepoMethod()) ? backDto.getRepoMethod() : DictCodeListExt3.回购方式_双边回购.getValue());
        // 首次结算方式
        pageDto.setDeliverytype(backDto.getvDeliveryType());
        // 清算速度
        pageDto.setSettleSpreedCode(backDto.getSettleSpreed());
        // 结算币种
        pageDto.setSettCcy(backDto.getSettleCcy());
        // 首次结算日
        pageDto.setSettlementStartDate(backDto.getvSettleDate());
        // 到期结算日
        pageDto.setSettlementEndDate(backDto.getmSettleDate());
        // 实际占款天数
        pageDto.setActCapDays(backDto.getActDays());
        // 回购期限
        pageDto.setRepoTerm((int) backDto.getTerm());
        // 质押券折算总金额
        pageDto.setConvertAmt(backDto.getShareCtAmt());
        // 证券账户
        pageDto.setSecManageAcctId(backDto.getSecAcctId());
        String trader = cfetsRepoQuotedPrice004Service.findTraderByCounterId(backDto.getCounterId());
        // 对手方
        if (StringUtil.isEmpty(trader)) {
            pageDto.setCounterPartyId(backDto.getCounterId());
        } else {
            pageDto.setCounterPartyId(trader);
        }
        // 分配方式
        pageDto.setDistribution(backDto.getDipType());
        List<RepoEntrustPledgeDto> repoEntrustPledgeDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(backDto.getPledgeBondList())) {
            for (CfetsRepoDealQuotedPrice003Dto pledgeBondDto : backDto.getPledgeBondList()) {
                RepoEntrustPledgeDto pledgeDto = new RepoEntrustPledgeDto();
                BeanUtil.copyPropertiesIgnoreNull(pledgeDto, pledgeBondDto);
                // 折算比例
                pledgeDto.setPledgeRatio(pledgeBondDto.getPledgeRate());
                // 可质押券总额
                pledgeDto.setAvaPledgePrin(pledgeBondDto.getAvaPledgePrin());
                // 质押券总额 元->万元
                pledgeDto.setPledgePrin(ArithmeticUtil.multiply(pledgeBondDto.getPledgePrin(), new BigDecimal(10000)));
                if(StringUtil.isEmpty(pageDto.getQuoteId())) {
                    pageDto.setQuoteId(pledgeBondDto.getQuoteId());
                }
                repoEntrustPledgeDtos.add(pledgeDto);
            }
        }
        pageDto.setRepoEntrustPledgeDtos(repoEntrustPledgeDtos);
        List<RepoPtlAllocationDto> ptlAllocationList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(backDto.getPtlAllocationList())) {
            for (PtlAllocationDto ptlDto : backDto.getPtlAllocationList()) {
                RepoPtlAllocationDto ptlAllocationDto = new RepoPtlAllocationDto();
                BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto, ptlDto);
                // 交易金额
                ptlAllocationDto.setTradeAmt(ptlDto.getFpriceAmt());
                //券面总额
                ptlAllocationDto.setShareAmt(ptlDto.getShareAmt());
                //折算券面总额
                ptlAllocationDto.setConvertAmt(ptlDto.getShareCtAmt());
                // 到期结算金额
                ptlAllocationDto.setMtradeAmt(ptlDto.getMtradeAmt());
                // 利息
                ptlAllocationDto.setmIntAmt(ptlDto.getIntAmt());
                // 回购期限
                ptlAllocationList.add(ptlAllocationDto);
            }
        }
        pageDto.setPtlAllocations(ptlAllocationList);
        pageDto.setTradeDate(iPlatformDate.getSysDate());
        return pageDto;
    }

    /**
     * 头寸处理
     *
     * @param entity   质押式回购对话信息
     * @param isSubmit 是否提交
     * @author jiangpeng
     * @date 2021/12/28 20:49
     */
    private void sendPositionMethod(CfetsRepoDealQuotedPrice001Dto entity, String flowStatus, String decisionAct, String systemAct, String confirmOk) {
        List<CfetsEtrIbRepoPosPortfolioCashRequest> ptlCashList = new ArrayList<>();
        List<CfetsEtrIbRepoPosPortfolioSecRequest> ptlSecList = new ArrayList<>();

        for (PtlAllocationDto ptlDto : entity.getPtlAllocationList()) {
            //调用头寸
            CfetsEtrIbRepoPosPortfolioCashRequest posport = new CfetsEtrIbRepoPosPortfolioCashRequest();
            posport.setPortfolioId(ptlDto.getPortfolioId());
            posport.setEtrId(ptlDto.getEntrustId());
            posport.setTradeId(entity.getQuoteId());
            if (StringUtil.isNotEmpty(ptlDto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", ptlDto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            posport.setCashAmount(ptlDto.getFpriceAmt());
            ptlCashList.add(posport);
        }
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> pledgeBondList = this.质押券拆分(this.investDto2ServiceDto(entity));
        if (StringUtil.isNotEmpty(pledgeBondList)) {
            for (com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto pledgeDto : pledgeBondList) {
                CfetsEtrIbRepoPosPortfolioSecRequest secport = new CfetsEtrIbRepoPosPortfolioSecRequest();
                secport.setPortfolioId(pledgeDto.getPortfolioId());
                secport.setEtrId(pledgeDto.getEntrustId());
                secport.setFinprodId(pledgeDto.getFinprodId());
                secport.setTradeId(entity.getQuoteId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", pledgeDto.getPortfolioId());
                map.put("finprodId", pledgeDto.getFinprodId());
                String secAcctId = this.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    secport.setSecAcctId(secAcctId); // 证券账户
                }
                secport.setSecAmount(ArithmeticUtil.multiply(pledgeDto.getPledgeNumber(), new BigDecimal(100)));
                secport.setOwnSecAmount(ArithmeticUtil.multiply(pledgeDto.getPledgeNumber(), new BigDecimal(100)));
                secport.setIntegrateSecAmount(BigDecimal.ZERO);
                ptlSecList.add(secport);
            }
        }
        //调用头寸
        CfetsEtrIbRepoPositionRequest req = new CfetsEtrIbRepoPositionRequest();
        req.setInterfaceType("REPO001");
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(entity.getQuoteId());
        req.setFlowStatus(flowStatus);
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        req.setWorkFlowId(entity.getWorkflowId());
        req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
        String balanceCheck = DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        req.setBalanceCheck(balanceCheck);
        req.setBusiType(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setSettleSpeed(entity.getSettleSpreed());
        req.setBusiAct(DictCodeListExt3.业务动作_对话报价.getValue());
        req.setPs(entity.getPs());
        req.setDecisionAct(decisionAct);
        req.setSystemAct(systemAct);
        req.setbTotalTradeId(entity.getQuoteId());
        req.setCdate(entity.getTradeDate());
        req.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        PositionHttpRequest.send(req);
    }

    /**
     * 质押券列表 按委托+组合的交易金额比例拆分
     *
     * @param cfetsRepoDealQuotedPrice001Dto
     * @return java.util.List<com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEntCrepPledgeDto>
     * @author jiangpeng
     * @date 2021/12/28 20:19
     */
    public List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> 质押券拆分(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrice003Dtos = new ArrayList<>();
        CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto = new CfetsRepoDealQuotedPriceDto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPriceDto, cfetsRepoDealQuotedPrice001Dto);
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> pledgeBondLists = cfetsRepoDealQuotedPrice001Dto.getPledgeBondList();
        String isDzh = cfetsRepoDealQuotedPrice001Dto.getIsDzh();
        if (StringUtil.equals(isDzh, "Y")) {
            cfetsRepoDealQuotedPriceDto.setPtlAllocationList(cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList());
            List<CfetsRepoPledgeBondDto> cfetsRepoPledgeBonds = new ArrayList<>();
            if (!StringUtil.isEmptyList(pledgeBondLists)) {
                pledgeBondLists.forEach(item -> {
                    CfetsRepoPledgeBondDto cfetsRepoPledgeBondDto = new CfetsRepoPledgeBondDto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoPledgeBondDto, item);
                    cfetsRepoPledgeBonds.add(cfetsRepoPledgeBondDto);
                });
                cfetsRepoDealQuotedPriceDto.setPledgeBondList(cfetsRepoPledgeBonds);
            }
            List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledgeCommons = cstpCommonService.质押券拆分(cfetsRepoDealQuotedPriceDto);
            if (!StringUtil.isEmptyList(ibuQuoteEntCrepPledgeCommons)) {
                ibuQuoteEntCrepPledgeCommons.forEach(item -> {
                    com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto
                            = new com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, item);
                    cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                    cfetsRepoDealQuotedPrice003Dtos.add(cfetsRepoDealQuotedPrice003Dto);
                });
            }
        }
        return cfetsRepoDealQuotedPrice003Dtos;
    }

    /**
     * 查询产品质押债券 证券账户
     *
     * @param map
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:09
     */
    private String qryPortfolioSecAcctId(Map<String, Object> map) {
        return cfetsIbuQuoteRepoMapper.qryPortfolioSecAcctId(map);
    }

    /**
     * 查询产品资金账户
     *
     * @param map
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:09
     */
    private String qryPortfolioCashAcctId(Map<String, Object> map) {
        return cfetsIbuQuoteRepoMapper.qryPortfolioCashAcctId(map);
    }

    /**
     * 查询当日报价
     *
     * @param dto
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDayPriceDto>
     * @author jiangpeng
     * @date 2021/12/28 11:36
     */
    public List<CfetsRepoDayPriceDto> listDayPrice(RepoEntrustRequest dto) {
        List<CfetsRepoDayPriceDto> result = new ArrayList<>();
        List<RepoQuoteInfoDto> dayOfferTableList = this.getDayOfferTableList(this.repoEntrustRequest2Map(dto));
        if (CollectionUtils.isNotEmpty(dayOfferTableList)) {
            dayOfferTableList.forEach(item -> {
                CfetsRepoDayPriceDto cfetsRepoDayPriceDto = new CfetsRepoDayPriceDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDayPriceDto, item);
//                cfetsRepoDayPriceDto.setOuPartyName(item.getPartyAbbr());
                cfetsRepoDayPriceDto.setCpPartyName(item.getCpPartyName());//对手方机构名
                cfetsRepoDayPriceDto.setTradeAmt(item.getFpriceAmt());//交易金额
                cfetsRepoDayPriceDto.setvSettleDate(DateUtil.format(item.getvSettleDate()));//首次结算日
                cfetsRepoDayPriceDto.setmSettleDate(DateUtil.format(item.getmSettleDate()));//到期结算日
                cfetsRepoDayPriceDto.setExecid(item.getfTradeId());
                cfetsRepoDayPriceDto.setRepoTerm(item.getTerm());//回购期限
                if (StringUtil.equals(item.getSendStatus(), BussinessDictCodeList.银行间上行发送状态_失败.getValue())) {
                    String errorMsg = this.selectErrlog(item.getQuoteId());
                    cfetsRepoDayPriceDto.setErrorMsg(errorMsg);
                }
                result.add(cfetsRepoDayPriceDto);
            });
        }
        return result;
    }

    /**
     * 查询当日报价
     *
     * @param dto
     * @return java.util.List<com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDayPriceDto>
     * @author jiangpeng
     * @date 2021/12/28 11:36
     */
    public com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto> listDayPricePage(RepoEntrustRequest dto) {
        List<CfetsRepoDayPriceDto> result = new ArrayList<>();
        com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteInfoDto> page = this.getDayOfferTablePage(this.repoEntrustRequest2Map(dto));
        List<RepoQuoteInfoDto> dayOfferTableList = page.getRoot();
        if (CollectionUtils.isNotEmpty(dayOfferTableList)) {
            dayOfferTableList.forEach(item -> {
                CfetsRepoDayPriceDto cfetsRepoDayPriceDto = new CfetsRepoDayPriceDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDayPriceDto, item);
//                cfetsRepoDayPriceDto.setOuPartyName(item.getPartyAbbr());
                cfetsRepoDayPriceDto.setCpPartyName(item.getCpPartyName());//对手方机构名
                cfetsRepoDayPriceDto.setTradeAmt(item.getFpriceAmt());//交易金额
                cfetsRepoDayPriceDto.setvSettleDate(DateUtil.format(item.getvSettleDate()));//首次结算日
                cfetsRepoDayPriceDto.setmSettleDate(DateUtil.format(item.getmSettleDate()));//到期结算日
                cfetsRepoDayPriceDto.setExecid(item.getfTradeId());
                cfetsRepoDayPriceDto.setRepoTerm(item.getTerm());//回购期限
                if (StringUtil.equals(item.getSendStatus(), BussinessDictCodeList.银行间上行发送状态_失败.getValue())) {
                    String errorMsg = this.selectErrlog(item.getQuoteId());
                    cfetsRepoDayPriceDto.setErrorMsg(errorMsg);
                }
                result.add(cfetsRepoDayPriceDto);
            });
        }

        // 查询条件转换
        com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto> repoQuoteExtDtoPage
                = new com.joyintech.background.component.common.pack.common.grid.Page<>();
        BeanUtil.copyPropertiesIgnoreNull(repoQuoteExtDtoPage, page);
        repoQuoteExtDtoPage.setRoot(result);
        return repoQuoteExtDtoPage;
    }

    /**
     * 前台查询条件转化为后台MAP
     *
     * @param dto
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2021/12/30 16:21
     */
    private Map<String, Object> repoEntrustRequest2Map(RepoEntrustRequest dto) {
        Map<String, Object> stringObjectMap = new HashMap<>();
        // 本方交易员
        if (CollectionUtils.isNotEmpty(dto.getOuTraderNameList())) {
            StringBuilder mySideTrader = new StringBuilder();
            dto.getOuTraderNameList().forEach(
                    ouTraderName ->
                            mySideTrader.append(GlobalConstant.COMMA + ouTraderName)
            );
            stringObjectMap.put("mySideTrader", mySideTrader.toString());
        }
        if (CollectionUtils.isNotEmpty(dto.getCpPartyNameList())) {
            // 对手方
            StringBuilder counterId = new StringBuilder();
            dto.getCpPartyNameList().forEach(
                    cpPartyName ->
                            counterId.append(GlobalConstant.COMMA + cpPartyName)
            );
            stringObjectMap.put("counterId", counterId.toString());
        }
        if (CollectionUtils.isNotEmpty(dto.getCpTraderNameList())) {
            // 对手方交易员
            StringBuilder counterTrader = new StringBuilder();
            dto.getCpTraderNameList().forEach(
                    cpTraderName ->
                            counterTrader.append(GlobalConstant.COMMA + cpTraderName)
            );
            stringObjectMap.put("counterTrader", counterTrader.toString());
        }
        stringObjectMap.put("term", dto.getRepoTerm());//回购期限
        stringObjectMap.put("quoteId", dto.getQuoteId());//报价编号
        stringObjectMap.put("dayOfferStatus", dto.getDayOfferStatus());//成交状态
        stringObjectMap.put("keywords", dto.getSearch());//关键字
        stringObjectMap.put("psList", dto.getPsList());
        stringObjectMap.put("cfetsQuoteId", dto.getCfetsQuoteId());
        // 排序处理
        if(StringUtil.isNotEmpty(dto.getPageMode())){
            if(StringUtil.isNotEmpty(dto.getPageMode().getDirection())){
                stringObjectMap.put("direction", dto.getPageMode().getDirection());
            }
            if(StringUtil.isNotEmpty(dto.getPageMode().getSort())){
                stringObjectMap.put("sort", dto.getPageMode().getSort());
            }
            if (StringUtil.isNotEmpty(dto.getPageMode().getPageNo())) {
                stringObjectMap.put("pageNo", dto.getPageMode().getPageNo());
            }
            if (StringUtil.isNotEmpty(dto.getPageMode().getPageSize())) {
                stringObjectMap.put("pageSize", dto.getPageMode().getPageSize());
            }
        }
        return stringObjectMap;
    }

    /**
     * 获取当日报价数据列表
     *
     * @param param
     * @return java.util.List<com.joyintech.service.bankuplink.ibu.dto.RepoQuoteInfoDto>
     * @author jiangpeng
     * @date 2021/12/28 11:37
     */
    public List<RepoQuoteInfoDto> getDayOfferTableList(Map<String, Object> param) {
        List<RepoQuoteInfoDto> result = new ArrayList<>();
        User user = SecurityUtil.getLoginUser();
        if (user == null) {
            return Collections.emptyList();
        }
        param.put("loginUserId", LoginUserUtil.getUserName());
        param.put("loginTraderId", LoginUserUtil.getUserName());
        //add by zznn 20210730 start
        //岗位信息 框架没有封装  单独处理
        //交易主管(交易处长03)、超级管理员能够查询出所有的报价信息
        Map<String, Object> paramUser = new HashMap<>();
        paramUser.put("userId", LoginUserUtil.getUserName());
        paramUser.put("rolePos", "03");
        boolean traDirector = commonBusinessService.isTraDirector(paramUser);
        param.put("isAdmin", user.isAdmin() || traDirector);
        // 待回复报价是否所有交易员可见（01：不允许，02：允许）
        param.put("unAnswerFlag", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));
        result = cfetsRepoDealQuotedPrice001Service.getDayOfferTableList(param);
        return result;
    }

    /**
     * 获取当日报价数据列表
     *
     * @param param
     * @return java.util.List<com.joyintech.service.bankuplink.ibu.dto.RepoQuoteInfoDto>
     * @author jiangpeng
     * @date 2021/12/28 11:37
     */
    public com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteInfoDto> getDayOfferTablePage(Map<String, Object> param) {
        List<RepoQuoteInfoDto> result = new ArrayList<>();
        User user = SecurityUtil.getLoginUser();
        if (user == null) {
            return new com.joyintech.background.component.common.pack.common.grid.Page<>();
        }
        param.put("loginUserId", LoginUserUtil.getUserName());
        param.put("loginTraderId", LoginUserUtil.getUserName());
        //add by zznn 20210730 start
        //岗位信息 框架没有封装  单独处理
        //交易主管(交易处长03)、超级管理员能够查询出所有的报价信息
        Map<String, Object> paramUser = new HashMap<>();
        paramUser.put("userId", LoginUserUtil.getUserName());
        paramUser.put("rolePos", "03");
        boolean traDirector = commonBusinessService.isTraDirector(paramUser);
        param.put("isAdmin", user.isAdmin() || traDirector);
        // 待回复报价是否所有交易员可见（01：不允许，02：允许）
        param.put("unAnswerFlag", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));
        return cfetsRepoDealQuotedPrice001Service.getRepoDayOfferTablePage(param);
    }

    /**
     * 生成流水号获取
     *
     * @return
     */
    public static String getSeqNo() {
        return IDGenerator.generate();
    }


    private PtlCashPositionFlowDto 逆回购构造现金流水(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto, Date vSettleDate, BigDecimal factor) {
        PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = new PtlCashPositionFlowDto();// 组合现金流水DTO
        ptlCashPositionFlowDtoUnit.setSeqno(getSeqNo());
        ptlCashPositionFlowDtoUnit.setPortfolioId(dto.getPortfolioId());// 组合代码
        ptlCashPositionFlowDtoUnit.setAcctId(EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(),
                dto.getCcy() == null ? DictCodeList.币种_人民币_CNY.getValue() : dto.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue()));
        ptlCashPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
        ptlCashPositionFlowDtoUnit.setCdate(vSettleDate);// 日期
        ptlCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
        // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
        //add by zn 20200108 现金流水管控交易金额
        if (null != dto.getDealAmt()) {
            ptlCashPositionFlowDtoUnit.setAmount(dto.getDealAmt().multiply(factor));
        } else {
            //回退的金额为空处理
            if (null != dto.getFpriceAmt()) {
                ptlCashPositionFlowDtoUnit.setAmount(dto.getFpriceAmt().multiply(factor));
            }
        }
        ptlCashPositionFlowDtoUnit.setOrderId(dto.getQuoteId());// 报价编号
        ptlCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
        ptlCashPositionFlowDtoUnit.setRemark("银行间上下行");
        ptlCashPositionFlowDtoUnit.setEntrustId(dto.getEntrustId());
        /*************************************************************************/
        return ptlCashPositionFlowDtoUnit;
    }

    /**
     * 根据委托代码查询委托基本信息
     *
     * @param entrustId   指令代码
     * @param currQuoteId 报价交易编号
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/28 20:50
     */
    public CfetsRepoDealQuotedPrice001Dto queryEntrustInfoByEntrustId(String entrustId, String currQuoteId) {
        CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
        Map<String, Object> param = new HashMap<String, Object>();
        // param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        param.put("entrustId", entrustId);
        if (StringUtil.isNotBlank(currQuoteId)) {
            param.put("currQuoteId", currQuoteId);
        }
        List<CfetsRepoDealQuotedPrice001Dto> pageList = this.queryEntrustOrderList(param);

        if (pageList != null && !StringUtil.isEmptyList(pageList)) {
            BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice001Dto, pageList.get(0));
        }

        // 计算回购期限 add by zhangliang 2020-07-18
        /*
         * 这个 回购期限的默认值（根据委托的起息日和到期日）的逻辑，再修改下： 1.如果委托到期日的前N日是连续的节假日：回购期限=到期日-起息日-N个节假日
         * N为>=1的数值 2.如果委托到期日的前N日不是连续节假日，回购期限=委托到期日-委托起息日
         */
        if (StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getRepoMdate()) && StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getRepoVdate())
                && cfetsRepoDealQuotedPrice001Dto.getRepoVdate().compareTo(cfetsRepoDealQuotedPrice001Dto.getRepoMdate()) != 0) {
            Date nextWorkDay = cfetsQuotedCommonService.getNextWorkDay(cfetsRepoDealQuotedPrice001Dto.getRepoVdate());
            if (nextWorkDay.compareTo(cfetsRepoDealQuotedPrice001Dto.getRepoMdate()) == 0) {
                cfetsRepoDealQuotedPrice001Dto.setTerm(1L);
            }
        }

        // 清算速度
        if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(), "0")) {
            cfetsRepoDealQuotedPrice001Dto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
        } else if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(), "1")) {
            cfetsRepoDealQuotedPrice001Dto.setSettleSpreed(BussinessDictCodeList.清算速度_T加1.getValue());
        }
        // 交割日期
        Date platDate = iPlatformDate.getSysDate();
        if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(),
                BussinessDictCodeList.清算速度_T加1.getValue())) {
            platDate = cfetsQuotedCommonService.getNextWorkDay(platDate);
        }
        cfetsRepoDealQuotedPrice001Dto.setSettleDate(platDate);
        // 结算方式
        cfetsRepoDealQuotedPrice001Dto.setSettleMethod(BussinessDictCodeList.结算方式_券款对付.getValue());
        // 清算类型
        cfetsRepoDealQuotedPrice001Dto.setSettleType(BussinessDictCodeList.清算类型_全额清算.getValue());
        // 结算币种
        cfetsRepoDealQuotedPrice001Dto.setSettleCcy(BussinessDictCodeList.币种_人民币.getValue());

        if (cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal() != null) {
            cfetsRepoDealQuotedPrice001Dto.setPledgePrinTotal(
                    cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal().divide(new BigDecimal(10000)));// 券面总额合计处理
        }
        return cfetsRepoDealQuotedPrice001Dto;
    }

    /**
     * 委托指令查询
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto>
     * @author jiangpeng
     * @date 2021/12/28 20:50
     */
    public List<CfetsRepoDealQuotedPrice001Dto> queryEntrustOrderList(Map<String, Object> param) {
        param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
        param.put("FINPROD_TYPE2_Z", BussinessDictCodeList.金融产品类型_债券质押式正回购.getValue());
        param.put("FINPROD_TYPE2_N", BussinessDictCodeList.金融产品类型_债券质押式逆回购.getValue());
        param.put("ps03", BussinessDictCodeList.回购拆借方向_正回购.getValue());
        param.put("ps04", BussinessDictCodeList.回购拆借方向_逆回购.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("secAcctSource01", BussinessDictCodeList.账户来源_理财.getValue());
        param.put("secAcctSource02", BussinessDictCodeList.账户来源_自营.getValue());
        param.put("calendarId", CacheUtil.getSysParam("ibuVar_calendarId"));
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> list = cfetsRepoDealQuotedPrice001Mapper.queryEntrustOrderList(param);
        return this.serviceDtoList2InvestDtoList(list);
    }

    /**
     * 根据quoteId查询对话报价信息
     *
     * @param quoteId
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/28 20:51
     */
    public CfetsRepoDealQuotedPrice001Dto queryEntrustInfoByQuoteId(String quoteId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("quoteId", quoteId);
        param.put("platDate", iPlatformDate.getSysDateStr());
        CfetsRepoDealQuotedPrice001Dto dto = this.queryEntrustInfoByQuoteId(param);
        return dto;
    }

    /**
     * 组合分配列表
     *
     * @param map
     * @return java.util.List<com.joyintech.tams.invest.common.dto.common.PtlAllocationDto>
     * @author jiangpeng
     * @date 2021/12/30 9:39
     */
    public List<PtlAllocationDto> selectPortfolioInit(Map<String, Object> map) {
        if (map.containsKey("quoteId") && StringUtil.isNotEmpty(map.get("quoteId"))) {
            if (this.isInWflProcess((String) map.get("quoteId"))) {
                return cfetsRepoDealQuotedPrice001Mapper.selectWflPortfolioByQuoteIdInit(map);
            } else {
                return cfetsRepoDealQuotedPrice001Mapper.selectPortfolioByQuoteIdInit(map);
            }
        }
        return cfetsRepoDealQuotedPrice001Mapper.selectPortfolioInit(map);
    }

    /**
     * 委托指令查询
     *
     * @param map
     * @return java.util.List<com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto>
     * @author jiangpeng
     * @date 2021/12/30 9:38
     */
    public List<CfetsRepoDealQuotedPrice003Dto> selectQuoteCrepPledgeForDzh(Map<String, Object> map) {
        map.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        map.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
        map.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        // 流程中数据
        List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto> list = ibuQuoteRepo003Mapper.selectQuoteCrepPledgeForDzh(map);
        if (CollectionUtils.isEmpty(list)) {
            list = ibuQuoteRepo003Mapper.selectWflQuoteCrepPledgeForDzh(map);
        }
        List<CfetsRepoDealQuotedPrice003Dto> results = null;
        try {
            results = JsonUtils.json2List(JsonUtils.object2Json(list), CfetsRepoDealQuotedPrice003Dto.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("委托指令查询时,转换dto错误");
        }
        return results;
    }

    /**
     * 根据quoteId查询对话报价信息
     *
     * @param param
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto
     * @author jiangpeng
     * @date 2021/12/28 20:50
     */
    public CfetsRepoDealQuotedPrice001Dto queryEntrustInfoByQuoteId(Map<String, Object> param) {
        String quoteId = (String) param.get("quoteId");
        com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto = null;
        if (StringUtil.isNotEmpty(quoteId)) {
            if (this.isInWflProcess(quoteId)) {
                dto = this.queryRepoDialogueQuoteByQuoteId(quoteId);
            } else {
                dto = ibuComponentCommonService.queryRepoDialogueQuoteByQuoteId(quoteId);
            }
            //质押式回购报价历史
            dto.setMySideTraderName(cfetsRepoDealQuotedPrice001Mapper.getTraderName(dto.getMySideTrader()));
            dto.setCounterTraderName(cfetsRepoDealQuotedPrice001Mapper.getTraderName(dto.getCounterTrader()));
            Map<String, Object> map = new HashMap<>();
            map.put("quoteId", (String) param.get("quoteId"));
            map.put("EXE_TYPE_IBU_03", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue());
            map.put("EXE_TYPE_IBU_07", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue());
            map.put("EXE_TYPE_IBU_67", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue());
            map.put("EXE_TYPE_IBU_63", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue());
            map.put("EXE_TYPE_IBU_25", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈修改.getValue());
            map.put("EXE_TYPE_IBU_24", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈新增.getValue());
            map.put("EXE_TYPE_IBU_19", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价新增.getValue());
            map.put("EXE_TYPE_IBU_20", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价修改.getValue());
            List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> list = cfetsRepoDealQuotedPrice001Mapper.queryCfetsRepoHis(map);
            if (StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈修改.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈新增.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价新增.getValue())
                    || StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价修改.getValue())) {
                list.add(dto);
            }
            String msg = getDialogueRecord(list);
            dto.setDialogueRecord(msg);
        }
        return this.serviceDto2InvestDto(dto);
    }

    /**
     * 根据报价编号查询对话报价信息(流程中)
     *
     * @param quoteId
     * @return
     * @see
     * @since 1.0
     */
    public com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto queryRepoDialogueQuoteByQuoteId(String quoteId) {
        RepoDialogueQuoteDto repoDialogueQuoteDto = new RepoDialogueQuoteDto();
        repoDialogueQuoteDto.setIbuQuoteCrepDto(ibuQuoteRepo003Mapper.selectWflQuoteCrepByKey(quoteId));
        repoDialogueQuoteDto.setIbuQuoteCrepPledgeDtoList(ibuQuoteRepo003Mapper.selectWflQuoteCrepPledgeByKey(quoteId));
        repoDialogueQuoteDto.setIbuQuoteEtrReDtoList(ibuQuoteRepo003Mapper.selectWflQuoteEtrReDataByQuoteId(quoteId));
        repoDialogueQuoteDto.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteRepo003Mapper.selectWflQuoteEntCrepPledgeDataByQuoteId(quoteId));
        return repoDialogueQuoteDto == null ? null : ibuComponentCommonService.convertRepoDialogueQuoteDto(repoDialogueQuoteDto);
    }

    /**
     * 根据报价编号查询错误信息
     *
     * @param quoteId String 报价代码
     * @return IbuQuoteErrlogDto 错误信息
     * @see
     * @since 1.0
     */
    public String selectErrlog(String quoteId) {
        IbuQuoteErrlogDto ibuQuoteErrlogDto = ibuComponentCommonService.selectErrlog(quoteId);
        return ibuQuoteErrlogDto == null ? "未查询到错误信息" : (ibuQuoteErrlogDto.getErrCode() != null ? "错误码： "
                + ibuQuoteErrlogDto.getErrCode() : "")
                + (ibuQuoteErrlogDto.getErrMsg() != null ? "<br/>错误信息： "
                + ibuQuoteErrlogDto.getErrMsg() : "");
    }

    /**
     * 微服务SERVICE的DTOLIST转前台DTOLIST
     *
     * @param list SERVICE的DTOLIST
     * @return java.util.List<com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> 前台DTOLIST
     * @author jiangpeng
     * @date 2021/12/28 17:38
     */
    public List<CfetsRepoDealQuotedPrice001Dto> serviceDtoList2InvestDtoList(List<com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto> list) {
        List<CfetsRepoDealQuotedPrice001Dto> results = null;
        try {
            String listJson = JsonUtils.object2Json(list);
            results = JsonUtils.json2List(listJson, CfetsRepoDealQuotedPrice001Dto.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("委托指令查询时,转换dto错误");
        }
        return results;
    }

    /**
     * 微服务SERVICE的DTO转前台DTO
     *
     * @param dto SERVICE的DTO
     * @return com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto 前台DTO
     * @author jiangpeng
     * @date 2021/12/28 17:37
     */
    public CfetsRepoDealQuotedPrice001Dto serviceDto2InvestDto(com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto dto) {
        CfetsRepoDealQuotedPrice001Dto result
                = JsonUtils.json2Object(JsonUtils.object2Json(dto), CfetsRepoDealQuotedPrice001Dto.class);
        result.setDialogueRecord(dto.getDialogueRecord());
        return result;
    }

    /**
     * 前台DTO转微服务SERVICE的DTO
     *
     * @param dto 前台DTO
     * @return com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto SERVICE的DTO
     * @author jiangpeng
     * @date 2021/12/28 17:37
     */
    public com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto investDto2ServiceDto(CfetsRepoDealQuotedPrice001Dto dto) {
        com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto result
                = JsonUtils.json2Object(JsonUtils.object2Json(dto), com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto.class);
        return result;
    }

    @Override
    public String generateTitle(CfetsRepoDealQuotedPrice001Dto dto, OperationType type) {
        String str = "对话报价编号" + "(" + dto.getQuoteId() + ")-"
                + "业务类型" + "(" + DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getCodeName() + ")-" + "平台日" + "("
                + DateUtil.format(iPlatformDate.getSysDate()) + ")";
        return str;
    }
}
