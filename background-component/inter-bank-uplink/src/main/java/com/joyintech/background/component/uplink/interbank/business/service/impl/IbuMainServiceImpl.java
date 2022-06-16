/*
 * 文件名：IbuMainServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.business.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.security.UserUtil;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.uplink.interbank.business.bonddlgservice.BondDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.bondreqservice.BondRequestQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.intferrmsgservice.IbuIntfErrMsgService;
import com.joyintech.background.component.uplink.interbank.business.quoteerrlog.IbuQuoteErrlogService;
import com.joyintech.background.component.uplink.interbank.business.repodlgservice.RepoDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.routeconfigservice.RouteConfigSubService;
import com.joyintech.background.component.uplink.interbank.business.routeintfservice.RouteIntfSubService;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.background.component.uplink.interbank.common.CommonUtils;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.common.IbuDictCode;
import com.joyintech.background.component.uplink.interbank.common.QuoteResultException;
import com.joyintech.background.component.uplink.interbank.database.dao.*;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuIntfScheme;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBond;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBondDet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 银行间上行组件服务统一实现类
 *
 * @author Jiaoxujin
 * @version 1.0 2020年5月7日
 * @see IbuMainServiceImpl
 * @since 1.0
 */
@Service
public class IbuMainServiceImpl extends BaseService<IbuQuoteBond> implements IbuMainService {
    private Logger log = LoggerFactory.getLogger(getClass());

    // 异步方法检查数据库中外部报价编号的时间间隔（毫秒）
    @Value("${application.quote.resp.checkInterval:100}")
    private int checkInterval;

    // 异步方法检查数据库中外部报价编号的最大等待时间（毫秒）
    @Value("${application.quote.resp.maxWaitTime:3000}")
    private int maxWaitTime;

    @Autowired
    private IbuMainMapper ibuMainMapper;

    @Autowired
    private IbuQuoteMapper ibuQuoteMapper;

    @Autowired
    private BondDialogueQuoteSubService bondDialogueQuoteService;

    @Autowired
    private RepoDialogueQuoteSubService repoDialogueQuoteSubService;

    @Autowired
    private BondRequestQuoteSubService bondRequestQuoteService;

    @Autowired
    private RouteIntfSubService routeIntfSubService;

    @Autowired
    private RouteConfigSubService routeConfigSubService;

    @Autowired
    private IbuIntfErrMsgService ibuIntfErrMsgService;

    @Autowired
    private IbuQuoteErrlogService ibuQuoteErrlogService;

    @Autowired
    private WflIbuQuoteBond2Mapper wflIbuQuoteBond2Mapper;

    @Autowired
    private WflIbuQuoteBondDetMapper wflIbuQuoteBondDetMapper;

    @Autowired
    private WflIbuQuoteCrepMapper wflIbuQuoteCrepMapper;

    @Autowired
    private WflIbuQuoteCrepPledgeMapper wflIbuQuoteCrepPledgeMapper;

    @Autowired
    private WflIbuQuoteEtrReMapper wflIbuQuoteEtrReMapper;

    //region 对话报价和请求报价综合查询

    /**
     * 现券报价根据成交编号获取委托编号
     *
     * @param execid
     * @return String
     */
    @Override
    public String getBondEntrustIdByExeid(String execid) {
        return ibuMainMapper.getBondEntrustIdByExeid(execid);
    }

    @Override
    public List<BondQuoteExtDto> getBondQuoteList(BondQuoteSearchDto inputBean, User userInfo) {
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        return ibuMainMapper.selectBondQuoteList(inputBean);
    }

    @Override
    public Page<BondQuoteExtDto> getBondQuotePageList(int pageNum, int pageSize,
                                                      BondQuoteSearchDto inputBean,
                                                      User userInfo) {
        Page<BondQuoteExtDto> pageData = new Page<>();
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        com.github.pagehelper.Page<BondQuoteExtDto> pageList = PageHelper.startPage(pageNum,
                pageSize).doSelectPage(() -> ibuMainMapper.selectBondQuoteList(inputBean));
        pageData = new Page<>(pageList);
        return pageData;
    }

    @Override
    public List<String> selectQuoteIdsByIdList(List<String> quoteIdList) {
        return ibuMainMapper.selectQuoteIdsByIdList(quoteIdList);
    }

    @Override
    public List<String> selectDetIdsByIdList(List<String> detIdList) {
        return ibuMainMapper.selectDetIdsByIdList(detIdList);
    }

    //endRegion

    @Override
    public List<String> queryMessgeIbu(String condition) {
        // TODO Auto-generated method stub
        return null;
    }

    //region=======================对话报价Start=========================

    @Override
    public BondDialogueQuoteDto getBondDialogueQuote(String quoteId) {
        return bondDialogueQuoteService.getBondDialogueQuote(quoteId);
    }

    @Override
    public BondDialogueQuoteDto getWflBondDialogueQuote(String quoteId) {
        return bondDialogueQuoteService.getWflBondDialogueQuote(quoteId);
    }

    /**
     * 删除对话报价的主表，主表历史，明细，明细历史数据
     *
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto deleteBondDialogueQuote(String quoteId) {
        return bondDialogueQuoteService.deleteBondDialogueQuoteByKey(quoteId, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto insertWflBondDialogueQuote(BondDialogueQuoteDto inputBean, User userInfo) {
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
        QuoteResultDto result = insertWflBondDialogueQuoteWithCheck(inputBean, true);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto insertBondDialogueQuote(BondDialogueQuoteDto inputBean, User userInfo) {
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
        QuoteResultDto result = insertBondDialogueQuoteWithCheck(inputBean, true);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultBatDto batchInsertBondDialogueQuote(List<BondDialogueQuoteDto> inputList,
                                                          User userInfo) {
        return batchInsertBondDialogueQuote(DictCodeList.银行间上行操作类型_本方报价新增.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    public QuoteResultBatDto batchNewBondDialogueQuote(List<BondDialogueQuoteDto> inputList,
                                                       User userInfo) {
        return batchInsertBondDialogueQuote(DictCodeList.银行间上行操作类型_本方新增发送.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Deprecated
    public QuoteResultDto updateBondDialogueQuoteByKey(BondDialogueQuoteDto inputBean,
                                                       User userInfo) {
        bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);
        return bondDialogueQuoteService.updateBondDialogueQuoteByKey(inputBean);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendNewBondDialogueQuote(BondDialogueQuoteDto inputBean, User userInfo) {
        return sendNewBondDialogueQuote(inputBean, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendUpdateBondDialogueQuote(BondDialogueQuoteDto inputBean, User userInfo) {
        return sendUpdateBondDialogueQuote(inputBean, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondDialogueQuote(String quoteId, User userInfo) {
        QuoteResultDto result = new QuoteResultDto(true);
        BondDialogueQuoteDto bondDialogueQuote =
                bondDialogueQuoteService.getBondDialogueQuote(quoteId);

        if (bondDialogueQuote != null && bondDialogueQuote.getIbuQuoteBondDto() != null) {
            result = sendNewBondDialogueQuote(bondDialogueQuote, false);
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00017");
            result.setErrMsg(getMessage("BC_IBUPLINK_00017", quoteId));
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondDialogueQuoteDeal(BondDialogueQuoteDto inputBean,
                                                    Map<String, String> attrMap, User userInfo) {
        return sendBondDialogueQuote(DictCodeList.银行间上行操作类型_本方回复成交.getValue(), inputBean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondDialogueQuoteRefuse(BondDialogueQuoteDto inputBean,
                                                      Map<String, String> attrMap, User userInfo) {
        return sendBondDialogueQuote(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(), inputBean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondDialogueQuoteRevoke(String quoteId,
                                                      Map<String, String> attrMap, User userInfo) {
        return sendBondDialogueQuote(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                setQuoteIdIntoBondDialogue(quoteId), attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto bondDialogueQuoteRestore(String quoteId,
                                                   Map<String, String> attrMap, User userInfo) {
        return sendBondDialogueQuote(DictCodeList.银行间上行操作类型_本方报价回退.getValue(),
                setQuoteIdIntoBondDialogue(quoteId), attrMap);
    }

    /**
     * 现券对话报价撤销<br />
     * 报价回复撤销，更新接口返回数据
     *
     * @param quoteId  String 报价编号
     * @param attrMap  Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondDialogueQuoteCancel(String quoteId, Map<String, String> attrMap, User userInfo) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(quoteId);
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        return sendBondDialogueQuote(DictCodeList.银行间上行操作类型_撤销.getValue(), inputBean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto updateBondDialogueQuoteInterfResp(ReceiveBondDialogueQuoteDto inputBean,
                                                            User userInfo) {
        if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(inputBean.getExeTypeIBU())) {
            return bondDialogueQuoteInterfResp(inputBean, userInfo);
        } else {
            int timeUsed = 0;
            LinkedBlockingQueue<String> quoteIdQueue = new LinkedBlockingQueue<>(1);
            String quoteId = null;
            String pQuoteId = null;
            IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
            if (quoteBond != null) {
                pQuoteId = quoteBond.getpQuoteId();
            }

            do {
                quoteId = bondDialogueQuoteService.getQuoteIdByPquoteId(pQuoteId);

                if (StringUtils.isNotBlank(quoteId)) {
                    quoteIdQueue.add(quoteId);
                }

                try {
                    quoteIdQueue.poll(checkInterval, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
                timeUsed += checkInterval;
            }
            while (StringUtils.isBlank(quoteId) && timeUsed < maxWaitTime);

            if (StringUtils.isNotBlank(quoteId)) {
                return bondDialogueQuoteInterfResp(inputBean, userInfo);
            }

            log.error(getMessage("BC_IBUPLINK_00019", maxWaitTime, pQuoteId));
            QuoteResultDto result = new QuoteResultDto(false);
            result.setErrCode("BC_IBUPLINK_00019");
            result.setErrMsg(getMessage("BC_IBUPLINK_00019", maxWaitTime, pQuoteId));
            return result;
        }
    }

    private QuoteResultDto bondDialogueQuoteInterfResp(ReceiveBondDialogueQuoteDto inputBean, User userInfo) {
        String quoteId = null;
        QuoteResultDto result = new QuoteResultDto(false);
        IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
        IbuQuoteBondDetDto quoteBondDet = inputBean.getIbuQuoteBondDetDto();
        IbuQuoteBondDto oldQuoteBond = new IbuQuoteBondDto();
        Long oldVersion = -2L;
        String oldUser = null, oldDept = null, exeType = null;

        if (quoteBond != null) {
            //region准备工作
            quoteId = quoteBond.getQuoteId();
            exeType = quoteBond.getExeType();

            // add by zhangliang 对手方新增报价时会出现我方接收先撤回后新增的场景，需要将此判断条件去除
            /*if (!DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(inputBean.getExeTypeIBU())
                && StringUtils.isBlank(quoteId)) {
                quoteId = bondDialogueQuoteService.getQuoteIdByPquoteId(quoteBond.getpQuoteId());
                quoteBond.setQuoteId(quoteId);
            }*/
            String dbQuoteId = bondDialogueQuoteService.getQuoteIdByPquoteId(quoteBond.getpQuoteId());
            if (StringUtils.isNotBlank(dbQuoteId)) {
                quoteId = dbQuoteId;
            }
            // 意思是若数据库中已经存在这个pQuoteId对应的数据，则使用数据库中那条记录的quoteId，不使用新传入的quoteId
            // quoteId的值有可能已经发生了改变，也可能没变，那么再设一次也无妨
            quoteBond.setQuoteId(quoteId);

            if (quoteBondDet != null) {
                quoteBondDet.setQuoteId(quoteId);
                if (quoteBondDet.getVersion() == null || quoteBondDet.getVersion() <= 0) {
                    quoteBondDet.setVersion(quoteBond.getVersion());
                }
            }
            //保存错误信息到日志表
            if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
                saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
            }

            //主表和明细表要先查再合并
            BondDialogueQuoteDto oldQuoteBondDto = bondDialogueQuoteService.getBondDialogueQuote(quoteId);

            if (oldQuoteBondDto != null) {
                try {
                    if (oldQuoteBondDto.getIbuQuoteBondDto() != null) {
                        oldQuoteBond = oldQuoteBondDto.getIbuQuoteBondDto();
                        oldVersion = oldQuoteBond.getVersion();
                        oldUser = oldQuoteBond.getUpdateUser();
                        oldDept = oldQuoteBond.getCreateDept();
                        BeanUtil.copyPropertiesIgnoreNull(oldQuoteBond, quoteBond);
                        BeanUtil.copyPropertiesIgnoreNull(quoteBond, oldQuoteBond);
                    }
                    if (oldQuoteBondDto.getIbuQuoteBondDetDto() != null) {
                        BeanUtil.copyPropertiesIgnoreNull(oldQuoteBondDto.getIbuQuoteBondDetDto(), quoteBondDet);
                        BeanUtil.copyPropertiesIgnoreNull(quoteBondDet, oldQuoteBondDto.getIbuQuoteBondDetDto());
                    }
                } catch (BeansException e) {
                    log.error(e.getMessage());
                    result.setStatus(false);
                    result.setErrCode("JavaError");
                    result.setErrMsg(e.getMessage());
                    return result;
                }
            }

            //设置用户信息
            if (userInfo == null) {
                userInfo = new User();
                if (UserUtil.DEFAULT_USER.equals(quoteBond.getUpdateUser()) && oldUser != null) {
                    userInfo.setUserName(oldUser);
                } else {
                    userInfo.setUserName(quoteBond.getUpdateUser());
                }
                if (UserUtil.DEFAULT_DEPT.equals(quoteBond.getCreateDept()) && oldDept != null) {
                    userInfo.setDeptCode(oldDept);
                } else {
                    userInfo.setDeptCode(quoteBond.getCreateDept());
                }
                UserUtil.bindUser(userInfo);
            }
            //endRegion
            try {
                // 主表版本号要小于当前传过来的版本号
                if (oldVersion < quoteBond.getVersion()) {
                    //"在线表"数据删除
                    result = bondDialogueQuoteService.deleteBondDialogueQuoteByKey(quoteBond.getQuoteId(), false);

                    if (result.isStatus()) {
                        //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
                        bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);

                        boolean isNew = DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                                inputBean.getExeTypeIBU()) ? true : false;

                        // 返回失败，不更新前置版本号
                        if (!isExecuteFail(exeType)) {
                            // 取Version，不要改成bVersion
                            inputBean.getIbuQuoteBondDto().setbVersion(quoteBond.getVersion());
                        }

                        //"在线表"及历史表数据插入，新版本号来自回复的参数
                        result = bondDialogueQuoteService.insertBondDialogueQuote(inputBean, isNew);
                    }
                } else {
                    result.setErrCode("BC_IBUPLINK_00009");
                    result.setErrMsg(getMessage("BC_IBUPLINK_00009", quoteBond.getVersion(),
                            quoteBond.getQuoteId(), oldVersion));
                }
            } finally {
                UserUtil.clear();
            }
        } else {
            result.setErrCode("BC_IBUPLINK_00007");
            result.setErrMsg(getMessage("BC_IBUPLINK_00007"));
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto receBondDlgQuoteGateWayMsg(GateWayReturnMsgDto inputBean, User userInfo) {
        String quoteId = inputBean.getQuoteId();
//        quoteId = bondDialogueQuoteService.getQuoteIdByPquoteId(inputBean.getpQuoteId());
//        inputBean.setQuoteId(quoteId);
        QuoteResultDto result = new QuoteResultDto(true);
        StringBuffer message = new StringBuffer();

        //保存错误信息到日志表
        if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
            saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
        }

        // 验证必须项
        if (StringUtils.isBlank(inputBean.getQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "quoteId") + "\n");
        }
/*        if (inputBean.getVersion() == null) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "version") + "\n");
        }*/
        if (StringUtils.isBlank(inputBean.getpQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "pQuoteId") + "\n");
        }

        if (message.length() > 0) {
            result.setErrCode("BC_IBUPLINK_00002");
            result.setErrMsg(message.toString());
            result.setStatus(false);
            return result;
        } else {
            log.warn(inputBean.getErrCode() + ": " + inputBean.getErrMsg());
            result = bondDialogueQuoteService.updateBondDialogueQuoteVersion(inputBean);
            return result;
        }
    }

    /**
     * 将报价编号存入BondDialogueQuoteDto对象
     *
     * @param quoteId
     * @return BondDialogueQuoteDto
     * @see
     * @since 1.0
     */
    private BondDialogueQuoteDto setQuoteIdIntoBondDialogue(String quoteId) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(quoteId);
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        return inputBean;
    }

    /**
     * 发送对话报价反馈
     */
    private QuoteResultDto sendBondDialogueQuote(String exeTypeIBU, BondDialogueQuoteDto inputBean,
                                                 Map<String, String> attrMap) {
        QuoteResultDto result = new QuoteResultDto(true);
        IbuQuoteBondDto ibuQuoteBondDto = inputBean.getIbuQuoteBondDto();
        String quoteId = ibuQuoteBondDto.getQuoteId();
        //多组合改造  成交的关系表数据  从前台带出
        List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
        if (StringUtils.equals(exeTypeIBU, DictCodeList.银行间上行操作类型_本方回复成交.getValue())) {
            ibuQuoteEtrRes = inputBean.getIbuQuoteEtrReDtoList();
        }
        // 对话报价查询
        BondDialogueQuoteDto bondDialogueQuote =
                bondDialogueQuoteService.getBondDialogueQuote(quoteId);

        if (bondDialogueQuote.getIbuQuoteBondDto() != null) {
            bondDialogueQuote.setExeTypeIBU(exeTypeIBU);
            //备注 add by lijie2 20200118
            bondDialogueQuote.getIbuQuoteBondDto().setRemark(ibuQuoteBondDto.getRemark());
            bondDialogueQuote.getIbuQuoteBondDetDto().setRemark(ibuQuoteBondDto.getRemark());
            //备注 add by lijie2 20200118
            //撤销发送状态特殊处理为空 需求要求
            if (StringUtils.equals(exeTypeIBU, DictCodeList.银行间上行操作类型_撤销.getValue())) {
                bondDialogueQuote.getIbuQuoteBondDto().setSendStatus(StringUtils.EMPTY);
                bondDialogueQuote.getIbuQuoteBondDetDto().setSendStatus(StringUtils.EMPTY);
            }
            // 现券对话报价必须项校验
            ResultBaseDto validation = routeIntfSubService.inputBondDlgCheck(bondDialogueQuote);

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
                        bondDialogueQuoteService.validateBondDialogueQuoteStatus(bondDialogueQuote, false);

                //更新bondDialogueQuote对象中的交易状态、操作类型和发送状态
                bondDialogueQuoteService.deleteWflBondDialogueQuoteByKey(quoteId, true);
                bondDialogueQuoteService.updateBondDialogueQuoteStatus(bondDialogueQuote);

                if (DictCodeList.银行间上行操作类型_本方回复拒绝.getValue().equals(exeTypeIBU)
                        || DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(exeTypeIBU)) {
                    //更新bondDialogueQuote对象中的组合代码和委托代码
                    if (ibuQuoteBondDto.getPortfolioId() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setPortfolioId(ibuQuoteBondDto.getPortfolioId());
                    }
                    if (ibuQuoteBondDto.getEntrustId() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setEntrustId(ibuQuoteBondDto.getEntrustId());
                    }
                    if (ibuQuoteBondDto.getSecManageAcctId() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setSecManageAcctId(ibuQuoteBondDto.getSecManageAcctId());
                    }
                    if (ibuQuoteBondDto.getDealFeeAmt() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setDealFeeAmt(ibuQuoteBondDto.getDealFeeAmt());
                    }
                    if (ibuQuoteBondDto.getSettFeeAmt() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setSettFeeAmt(ibuQuoteBondDto.getSettFeeAmt());
                    }
                    if (ibuQuoteBondDto.getDipType() != null) {
                        bondDialogueQuote.getIbuQuoteBondDto().setDipType(ibuQuoteBondDto.getDipType());
                    }
                    //多组合 关系表数据单独处理
                    if (null != ibuQuoteEtrRes && ibuQuoteEtrRes.size() > 0) {
                        //版本号取值主表
                        ibuQuoteEtrRes.stream().forEach(item -> {
                            item.setVersion(bondDialogueQuote.getIbuQuoteBondDto().getVersion());
                        });
                        bondDialogueQuote.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
                    }
                }
                // 校验通过
                if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
                    // 如果是回退操作，则需要回退历史数据
                    if (DictCodeList.银行间上行操作类型_本方报价回退.getValue().equals(exeTypeIBU)) {
                        result = bondDialogueQuoteService.restoreBondDialogueQuoteByKey(bondDialogueQuote);
                    } else {
                        result = bondDialogueQuoteService.updateBondDialogueQuoteByKey(bondDialogueQuote);
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
            result.setErrMsg(getMessage("BC_IBUPLINK_00018", quoteId));
        }

        return result;
    }

    /**
     * 现券对话报价新增发送
     */
    private QuoteResultDto sendNewBondDialogueQuote(
            BondDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方新增发送.getValue());

        // 现券对话报价必须项校验
        ResultBaseDto validation = routeIntfSubService.inputBondDlgCheck(inputBean);

        // 校验不通过
        if (validation != null && !validation.isStatus()) {
            result.setStatus(false);
            result.setErrCode(validation.getErrCode());
            result.setErrMsg(validation.getErrMsg());
        } else {
            // 获取路由配置方案
            IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());

            result = insertBondDialogueQuoteWithCheck(inputBean, needSearch);

            if (intfScheme != null) {
                result.setSendType(intfScheme.getSendType());
            }
        }

        return result;
    }

    /**
     * 现券对话报价新增发送
     */
    private QuoteResultDto sendUpdateBondDialogueQuote(
            BondDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方修改发送.getValue());

        // 现券对话报价必须项校验
        ResultBaseDto validation = routeIntfSubService.inputBondDlgCheck(inputBean);

        // 校验不通过
        if (validation != null && !validation.isStatus()) {
            result.setStatus(false);
            result.setErrCode(validation.getErrCode());
            result.setErrMsg(validation.getErrMsg());
        } else {
            // 获取路由配置方案
            IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());

            result = UpdateBondDialogueQuoteWithCheck(inputBean, needSearch);

            if (intfScheme != null) {
                result.setSendType(intfScheme.getSendType());
            }
        }

        return result;
    }

    /**
     * 对话报价流程表验证插入
     */
    private QuoteResultDto insertWflBondDialogueQuoteWithCheck(
            BondDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getWflIbuQuoteBond2Dto() != null) {
            quoteId = inputBean.getWflIbuQuoteBond2Dto().getQuoteId();
        }

        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof BondDialogueQuoteDto) {
                bondDialogueQuoteService.deleteWflBondDialogueQuoteByKey(quoteId, true);
                result = bondDialogueQuoteService.insertWflBondDialogueQuote(
                        (BondDialogueQuoteDto) validResult, true);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            result = bondDialogueQuoteService.insertWflBondDialogueQuote(inputBean, true);
        }

        return result;
    }

    /**
     * 对话报价验证插入
     */
    private QuoteResultDto insertBondDialogueQuoteWithCheck(
            BondDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getIbuQuoteBondDto() != null) {
            quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();
        }

        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof BondDialogueQuoteDto) {
                bondDialogueQuoteService.deleteWflBondDialogueQuoteByKey(quoteId, true);
                bondDialogueQuoteService.deleteBondDialogueQuoteByKey(quoteId, true);
                result = bondDialogueQuoteService.insertBondDialogueQuote((BondDialogueQuoteDto) validResult, true);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            //更新inputBean对象中的交易状态、操作类型和发送状态
            bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);
            result = bondDialogueQuoteService.insertBondDialogueQuote(inputBean, true);
        }

        return result;
    }

    /**
     * 对话报价验证插入
     */
    private QuoteResultDto UpdateBondDialogueQuoteWithCheck(
            BondDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getIbuQuoteBondDto() != null) {
            quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();
        }

        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof BondDialogueQuoteDto) {
                bondDialogueQuoteService.deleteWflBondDialogueQuoteByKey(quoteId, true);
                bondDialogueQuoteService.deleteBondDialogueQuoteByKey(quoteId, false);
                result = bondDialogueQuoteService.updateBondDialogueQuote((BondDialogueQuoteDto) validResult, true);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            //更新inputBean对象中的交易状态、操作类型和发送状态
            bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);
            result = bondDialogueQuoteService.insertBondDialogueQuote(inputBean, true);
        }

        return result;
    }

    /**
     * 验证对话报价是否已经存在于数据库中，若存在则进行DTO中的状态更新和验证<br />
     *
     * @param inputBean  BondDialogueQuoteDto
     * @param needSearch boolean 是否需要从数据库中查询DTO
     * @return Object 如果校验通过，返回BondDialogueQuoteDto，不通过返回ValidMessageDto
     * @throws QuoteResultException
     */
    private Object validateExists(BondDialogueQuoteDto inputBean,
                                  boolean needSearch) throws QuoteResultException {
        //更新bondDialogueQuote对象中的交易状态、操作类型和发送状态
        bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);

        // 对话报价状态检验
        ValidMessageDto validResult =
                bondDialogueQuoteService.validateBondDialogueQuoteStatus(inputBean, needSearch);

        // 校验通过
        if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
            return inputBean;
        } else {
            return validResult;
        }
    }

    /**
     * 批量新增和批量新增发送的共通方法
     */
    private QuoteResultBatDto batchInsertBondDialogueQuote(String exeTypeIBU,
                                                           List<BondDialogueQuoteDto> inputList) {
        QuoteResultBatDto batchResult = new QuoteResultBatDto();
        QuoteResultDto result;
        List<String> quoteIdList = new ArrayList<>();    // 删除用
        List<String> detIdList = new ArrayList<>();        // 删除用
        Map<String, String> sendTypeMap = new HashMap<>();

        //region 遍历验证和更新状态
        for (BondDialogueQuoteDto inputBean : inputList) {
            inputBean.setExeTypeIBU(exeTypeIBU);
            String quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();

            //region 银行间上行操作类型_本方新增发送
            if (DictCodeList.银行间上行操作类型_本方新增发送.getValue().equals(exeTypeIBU)) {
                // 现券对话报价发送必须项校验
                ResultBaseDto validation = routeIntfSubService.inputBondDlgCheck(inputBean);

                // 校验不通过
                if (validation != null && !validation.isStatus()) {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode(validation.getErrCode());
                    result.setErrMsg(validation.getErrMsg());
                    batchResult.addQuoteResult(result);
                } else {
                    // 获取路由配置方案
                    IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());
                    if (intfScheme != null) {
                        sendTypeMap.put(quoteId, intfScheme.getSendType());
                    }
                }
            }
            //endRegion

            // 报价ID已存在
            if (StringUtils.isNotBlank(quoteId)) {
                Object validResult = null;
                try {
                    validResult = validateExists(inputBean, true);
                } catch (QuoteResultException e) {
                    batchResult.addQuoteResult(e.getQuoteResult());
                    continue;
                }

                // 验证通过
                if (validResult instanceof BondDialogueQuoteDto) {
                    if (((BondDialogueQuoteDto) validResult).getIbuQuoteBondDto() != null) {
                        quoteIdList.add(((BondDialogueQuoteDto) validResult).getIbuQuoteBondDto().getQuoteId());
                    }
                    if (((BondDialogueQuoteDto) validResult).getIbuQuoteBondDetDto() != null) {
                        detIdList.add(((BondDialogueQuoteDto) validResult).getIbuQuoteBondDetDto().getDetId());
                    }
                    inputBean = (BondDialogueQuoteDto) validResult;
                } else {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
                    batchResult.addQuoteResult(result);
                }
            } else {
                //更新inputBean对象中的交易状态、操作类型和发送状态
                bondDialogueQuoteService.updateBondDialogueQuoteStatus(inputBean);
            }
        }
        //endRegion

        if (batchResult.getQuoteResultListSize() > 0) {
            batchResult.setSuccessAccout(0);
            batchResult.setFailAccout(inputList.size());
            batchResult.setErrMsgList(
                    batchResult.getQuoteResultList().stream().map(p -> p.getErrMsg()).collect(
                            Collectors.toList()));
            return batchResult;
        } else {
            // 批量删除
            if (CollectionUtils.isNotEmpty(detIdList)) {
                ibuMainMapper.deleteQuoteDetByIds(detIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteDetHisByIds(detIdList);
            }
            if (CollectionUtils.isNotEmpty(quoteIdList)) {
                ibuMainMapper.deleteQuoteByIds(quoteIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteHisByIds(quoteIdList);
            }

            // 批量插入
            batchResult = bondDialogueQuoteService.batchInsertBondDialogueQuote(inputList);
            batchResult.getQuoteResultList().forEach(
                    p -> p.setSendType(sendTypeMap.get(p.getBaseQuoteDto().getQuoteId())));

            return batchResult;
        }
    }
    //endRegion=======================对话报价End=========================

    //region=======================请求报价Start=========================

    @Override
    public BondRequestQuoteDto getBondRequestQuote(String quoteId) {
        return bondRequestQuoteService.getBondRequestQuote(quoteId);
    }

    /**
     * 流程中请求报价
     */
    @Override
    public BondRequestQuoteDto getWflBondRequestQuote(String quoteId) {
        return bondRequestQuoteService.getWflBondRequestQuote(quoteId);
    }

    /**
     * 删除对话报价的主表，主表历史，明细，明细历史数据
     *
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto deleteBondRequestQuote(String quoteId) {
        BondRequestQuoteDto inputBean = bondRequestQuoteService.getBondRequestQuote(quoteId);
        return bondRequestQuoteService.deleteBondRequestQuoteByKey(inputBean, true, false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto insertBondRequestQuote(BondRequestQuoteDto inputBean, User userInfo) {
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方请求报价新增.getValue());
        QuoteResultDto result = insertBondRequestQuoteWithCheck(inputBean, true);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultBatDto batchInsertBondRequestQuote(List<BondRequestQuoteDto> inputList,
                                                         User userInfo) {
        return batchInsertBondRequestQuote(DictCodeList.银行间上行操作类型_本方请求报价新增.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultBatDto batchNewBondRequestQuote(List<BondRequestQuoteDto> inputList,
                                                      User userInfo) {
        return batchInsertBondRequestQuote(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto updateBondRequestQuoteByKey(BondRequestQuoteDto inputBean,
                                                      User userInfo) {
        return bondRequestQuoteService.updateBondRequestQuoteByKey(inputBean);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendNewBondRequestQuote(BondRequestQuoteDto inputBean, User userInfo) {
        return sendNewBondRequestQuote(inputBean, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendUpdatedBondRequestQuote(BondRequestQuoteDto inputBean,
                                                      User userInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuote(String quoteId, User userInfo) {
        QuoteResultDto result = new QuoteResultDto();
        BondRequestQuoteDto bondRequestQuoteDto = bondRequestQuoteService.getBondRequestQuote(quoteId);

        if (bondRequestQuoteDto != null && bondRequestQuoteDto.getIbuQuoteBondDto() != null) {
            result = sendNewBondRequestQuote(bondRequestQuoteDto, false);
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00017");
            result.setErrMsg(getMessage("BC_IBUPLINK_00017", quoteId));
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteDeal(BondRequestQuoteDto inputBean, List<String> detIdList, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方请求回复成交.getValue(),
                inputBean, attrMap, detIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteModify(BondRequestQuoteDto inputBean, List<String> detIdList, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                inputBean, attrMap, detIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteRefuse(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方拒绝请求报价.getValue(),
                setQuoteIdIntoBondRequest(quoteId), attrMap, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteRevoke(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方请求报价撤回.getValue(),
                setQuoteIdIntoBondRequest(quoteId), attrMap, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteRestore(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方报价回退.getValue(),
                setQuoteIdIntoBondRequest(quoteId), attrMap, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteNewReply(BondRequestQuoteDto bondRequestQuote, Map<String, String> attrMap,
                                                       User userInfo) {
        return sendBondRequestQuoteNewReply(DictCodeList.银行间上行操作类型_本方新增请求回复.getValue(),
                bondRequestQuote, attrMap);
    }

    /**
     * 现券请求报价撤销<br />
     * 报价回复撤销，更新接口返回数据
     *
     * @param quoteId  String 报价编号
     * @param attrMap  Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteCancel(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_撤销.getValue(),
                setQuoteIdIntoBondRequest(quoteId), attrMap, null);
    }

    /**
     * 将报价编号存入BondRequestQuoteDto对象中
     *
     * @param quoteId
     * @return BondRequestQuoteDto
     * @see
     * @since 1.0
     */
    private BondRequestQuoteDto setQuoteIdIntoBondRequest(String quoteId) {
        BondRequestQuoteDto inputBean = new BondRequestQuoteDto();
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(quoteId);
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        return inputBean;
    }

    /**
     * 本方新增请求回复报价反馈
     */
    private QuoteResultDto sendBondRequestQuoteNewReply(String exeTypeIBU, BondRequestQuoteDto bondRequestQuote,
                                                        Map<String, String> attrMap) {
        QuoteResultDto result = new QuoteResultDto();
        String quoteId = bondRequestQuote.getIbuQuoteBondDto().getQuoteId();
        // 请求报价查询
        BondRequestQuoteDto bondRequestQuoteDto = bondRequestQuoteService.getBondRequestQuote(quoteId);

        if (bondRequestQuoteDto != null && bondRequestQuoteDto.getIbuQuoteBondDto() != null) {
            bondRequestQuoteDto.setExeTypeIBU(exeTypeIBU);
            // 现券请求报价必须项校验
            ResultBaseDto validation = routeIntfSubService.inputBondReqCheck(bondRequestQuoteDto);
            // 校验不通过
            if (validation != null && !validation.isStatus()) {
                result.setStatus(false);
                result.setErrCode(validation.getErrCode());
                result.setErrMsg(validation.getErrMsg());
            } else {
                // 获取路由配置方案
                IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(attrMap);
                if (intfScheme != null) {
                    result.setSendType(intfScheme.getSendType());
                }

                //获取更新参数
                bondRequestQuoteDto.getIbuQuoteBondDto().setQuoteId(bondRequestQuote.getIbuQuoteBondDto().getQuoteId());
                updateParam(bondRequestQuoteDto, bondRequestQuote);

                //更新bondDialogueQuote对象中的组合代码和委托代码
                bondRequestQuoteDto.getIbuQuoteBondDto().setPortfolioId(bondRequestQuote.getIbuQuoteBondDto().getPortfolioId());
                bondRequestQuoteDto.getIbuQuoteBondDto().setEntrustId(bondRequestQuote.getIbuQuoteBondDto().getEntrustId());

                // 请求报价状态检验
                ValidMessageDto validResult =
                        bondRequestQuoteService.validateBondRequestQuoteStatus(bondRequestQuoteDto, false);

                //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
                bondRequestQuoteService.updateBondRequestQuoteStatus(bondRequestQuoteDto, null, false);

                // 校验通过
                if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
                    // Modified by JIAOXUJIN on 2020-06-12 for 历史表不更新
                    bondRequestQuoteService.deleteBondRequestQuoteByKey(bondRequestQuote, false, false);
                    // Added by JIAOXUJIN on 2020-06-08
                    //bondRequestQuoteDto.setVersion(-1L, bondRequestQuoteDto.getIbuQuoteBondDto().getVersion());
                    // Modified by JIAOXUJIN on 2020-06-12 for 历史表不更新
                    result = bondRequestQuoteService.insertBondRequestQuote(bondRequestQuoteDto, true, false, false);
                } else {
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(validResult.getMessageWithId());
                    // return result;
                }
            }
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00018");
            result.setErrMsg(getMessage("BC_IBUPLINK_00018", quoteId));
        }
        return result;
    }

    private void updateParam(BondRequestQuoteDto bondRequestQuoteDto, BondRequestQuoteDto newBondRequestQuoteDto) {
        IbuQuoteBondDto ibuQuoteBondDto = bondRequestQuoteDto.getIbuQuoteBondDto();
        IbuQuoteBondDto newIbuQuoteBondDto = newBondRequestQuoteDto.getIbuQuoteBondDto();
        IbuQuoteBondDetDto newIbuQuoteBondDetDto = newBondRequestQuoteDto.getIbuQuoteBondDetDtoList().get(0);

        BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, newIbuQuoteBondDto);

        // 本循环只执行一次
        for (IbuQuoteBondDetDto ibuQuoteBondDetDto : bondRequestQuoteDto.getIbuQuoteBondDetDtoList()) {
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, newIbuQuoteBondDetDto);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteUpdateReply(BondRequestQuoteDto inputBean,
                                                          Map<String, String> attrMap,
                                                          User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方修改请求回复.getValue(),
                inputBean, attrMap, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendBondRequestQuoteRevokeReply(String quoteId,
                                                          Map<String, String> attrMap,
                                                          User userInfo) {
        return sendBondRequestQuote(DictCodeList.银行间上行操作类型_本方撤销请求回复.getValue(),
                setQuoteIdIntoBondRequest(quoteId), attrMap, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto updateBondRequestQuoteInterfResp(ReceiveBondRequestQuoteDto inputBean,
                                                           User userInfo) {
        if (DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(inputBean.getExeTypeIBU())) {
            return bondRequestQuoteInterfResp(inputBean, userInfo);
        } else {
            int timeUsed = 0;
            LinkedBlockingQueue<String> quoteIdQueue = new LinkedBlockingQueue<>(1);
            String quoteId = null;
            String pQuoteId = null;
            IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
            if (quoteBond != null) {
                pQuoteId = quoteBond.getpQuoteId();
            }

            do {
                quoteId = bondRequestQuoteService.getQuoteIdByPquoteId(pQuoteId);

                if (StringUtils.isNotBlank(quoteId)) {
                    quoteIdQueue.add(quoteId);
                }

                try {
                    quoteIdQueue.poll(checkInterval, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
                timeUsed += checkInterval;
            }
            while (StringUtils.isBlank(quoteId) && timeUsed < maxWaitTime);

            if (StringUtils.isNotBlank(quoteId)) {
                return bondRequestQuoteInterfResp(inputBean, userInfo);
            }

            log.error(getMessage("BC_IBUPLINK_00020", maxWaitTime, pQuoteId));
            QuoteResultDto result = new QuoteResultDto(false);
            result.setErrCode("BC_IBUPLINK_00020");
            result.setErrMsg(getMessage("BC_IBUPLINK_00020", maxWaitTime, pQuoteId));
            return result;
        }
    }

    private QuoteResultDto bondRequestQuoteInterfResp(ReceiveBondRequestQuoteDto inputBean,
                                                      User userInfo) {
        String quoteId = null;
        QuoteResultDto result = new QuoteResultDto(false);
        IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
        List<IbuQuoteBondDetDto> quoteBondDetList = inputBean.getIbuQuoteBondDetDtoList();
        List<IbuQuoteBondDetDto> allDetList = new ArrayList<>();
        List<String> reqIdList = null, detIdList = null, cpPartIdList = null;
        IbuQuoteBondDto oldQuoteBond = new IbuQuoteBondDto();
        Long oldVersion = -2L;
        String oldUser = null, oldDept = null, exeType = null;
        boolean isDuishouResp = false;

        if (quoteBond != null) {
            //region准备工作
            quoteId = quoteBond.getQuoteId();
            exeType = quoteBond.getExeType();

            // add by zhangliang 对手方新增报价时会出现我方接收先撤回后新增的场景，需要将此判断条件去除
            /*if (!DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(inputBean.getExeTypeIBU())
                && StringUtils.isBlank(quoteId)) {
                quoteId = bondRequestQuoteService.getQuoteIdByPquoteId(quoteBond.getpQuoteId());
                quoteBond.setQuoteId(quoteId);
            }*/
            String dbQuoteId = bondRequestQuoteService.getQuoteIdByPquoteId(quoteBond.getpQuoteId());
            if (StringUtils.isNotBlank(dbQuoteId)) {
                quoteId = dbQuoteId;
            }
            quoteBond.setQuoteId(quoteId);

            if (CollectionUtils.isNotEmpty(quoteBondDetList)) {
                for (IbuQuoteBondDetDto det : quoteBondDetList) {
                    det.setQuoteId(quoteId);
                    if (det.getVersion() == null || det.getVersion() <= 0) {
                        det.setVersion(quoteBond.getVersion());
                    }
                }
                if (DictCodeList.银行间上行操作类型_本方请求回复成交.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType()) ||
                        DictCodeList.银行间上行操作类型_本方请求回复成交成功.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType()) ||
                        DictCodeList.银行间上行操作类型_本方请求回复成交失败.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType())) {
                    reqIdList = quoteBondDetList.stream().filter(
                            p -> StringUtils.isNotBlank(p.getReqId())).map(p -> p.getReqId()).collect(
                            Collectors.toList());
                    cpPartIdList = quoteBondDetList.stream().filter(
                            p -> StringUtils.isBlank(p.getReqId())
                                    && StringUtils.isNotBlank(p.getCpPartyId())).map(
                            p -> p.getCpPartyId()).collect(Collectors.toList());
                }
            }

            //保存错误信息到日志表
            if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
                saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
            }

            //主表和明细表要先查再合并
            BondRequestQuoteDto oldQuoteBondDto = bondRequestQuoteService.getBondRequestQuote(quoteId);

            if (oldQuoteBondDto != null) {
                try {
                    if (oldQuoteBondDto.getIbuQuoteBondDto() != null) {
                        oldQuoteBond = oldQuoteBondDto.getIbuQuoteBondDto();
                        oldVersion = oldQuoteBond.getVersion();
                        oldUser = oldQuoteBond.getUpdateUser();
                        oldDept = oldQuoteBond.getCreateDept();
                        BeanUtil.copyPropertiesIgnoreNull(oldQuoteBond, quoteBond);
                        BeanUtil.copyPropertiesIgnoreNull(quoteBond, oldQuoteBond);
                    }

                    if (CollectionUtils.isEmpty(quoteBondDetList)) {
                        quoteBondDetList = new ArrayList<>();
                    }

                    if (CollectionUtils.isNotEmpty(oldQuoteBondDto.getIbuQuoteBondDetDtoList())) {
                        List<IbuQuoteBondDetDto> quoteBondDetFinalList = new ArrayList<>(quoteBondDetList);
                        List<IbuQuoteBondDetDto> updatedDetList = oldQuoteBondDto.getIbuQuoteBondDetDtoList()
                                .stream().map(p -> {
                                    Optional<IbuQuoteBondDetDto> detOpt = quoteBondDetFinalList.stream()
                                            .filter(bondDetFilter(p)).findFirst();

                                    if (detOpt.isPresent()) {
                                        String newTradeStatus = detOpt.get().getTradeStatus();
                                        String newSendStatus = detOpt.get().getSendStatus();
                                        BeanUtil.copyPropertiesIgnoreNull(p, detOpt.get());

                                        // 使用自己传过来的状态
                                        if (isDuishouResp(inputBean)) {
                                            p.setTradeStatus(newTradeStatus);
                                            p.setSendStatus(newSendStatus);
                                        }
                                    }
                                    return p;
                                }).collect(Collectors.toList());

                        List<String> existsReqIds = updatedDetList.stream().filter(
                                p -> StringUtils.isNotBlank(p.getReqId())).map(
                                p -> p.getReqId()).collect(Collectors.toList());
                        List<String> existsCpPartIds = updatedDetList.stream().filter(
                                p -> StringUtils.isBlank(p.getReqId())
                                        && StringUtils.isNotBlank(p.getCpPartyId())).map(
                                p -> p.getCpPartyId()).collect(Collectors.toList());

                        allDetList.addAll(quoteBondDetFinalList.stream().filter(
                                p -> !existsReqIds.contains(p.getReqId())
                                        && !existsCpPartIds.contains(p.getCpPartyId())).collect(
                                Collectors.toList()));
                        allDetList.addAll(updatedDetList);
                    } else {
                        allDetList.addAll(quoteBondDetList);
                    }

                    // add by etliu 20210316 银行间上下行多组合 start
                    if (CollectionUtils.isNotEmpty(oldQuoteBondDto.getIbuQuoteEtrReDtoList())
                            && CollectionUtils.isEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                        List<IbuQuoteEtrReDto> resultIbuQuoteEtrReList = new ArrayList<>();
                        for (IbuQuoteEtrReDto ibuQuoteEtrReDto : oldQuoteBondDto.getIbuQuoteEtrReDtoList()) {
                            IbuQuoteEtrReDto resultIbuQuoteEtrReDto = new IbuQuoteEtrReDto();
                            BeanUtil.copyPropertiesIgnoreNull(resultIbuQuoteEtrReDto, ibuQuoteEtrReDto);
                            resultIbuQuoteEtrReDto.setVersion(quoteBond.getVersion());
                            resultIbuQuoteEtrReList.add(resultIbuQuoteEtrReDto);
                        }
                        inputBean.setIbuQuoteEtrReDtoList(resultIbuQuoteEtrReList);
                    }
                    // add by etliu 20210316 银行间上下行多组合 end
                } catch (BeansException e) {
                    log.error(e.getMessage());
                    result.setStatus(false);
                    result.setErrCode("JavaError");
                    result.setErrMsg(e.getMessage());
                    return result;
                } finally {
                    inputBean.setIbuQuoteBondDetDtoList(allDetList);
                    if (CollectionUtils.isNotEmpty(reqIdList) || CollectionUtils.isNotEmpty(cpPartIdList)) {
                        //allDetList 与 reqIdList/cpPartIdList join得到detIdList
                        detIdList = new ArrayList<>();
                        for (IbuQuoteBondDetDto detDto : allDetList) {
                            if (reqIdList.contains(detDto.getReqId()) || cpPartIdList.contains(detDto.getCpPartyId())) {
                                detIdList.add(detDto.getDetId());
                            }
                        }
                    }
                }
            }

            //设置用户信息
            if (userInfo == null) {
                userInfo = new User();
                if (UserUtil.DEFAULT_USER.equals(quoteBond.getUpdateUser()) && oldUser != null) {
                    userInfo.setUserName(oldUser);
                } else {
                    userInfo.setUserName(quoteBond.getUpdateUser());
                }
                if (UserUtil.DEFAULT_DEPT.equals(quoteBond.getCreateDept()) && oldDept != null) {
                    userInfo.setDeptCode(oldDept);
                } else {
                    userInfo.setDeptCode(quoteBond.getCreateDept());
                }
                UserUtil.bindUser(userInfo);
            }
            //endRegion

            try {
                // 主表版本号要小于当前传过来的版本号
                if (oldVersion < quoteBond.getVersion()) {
                    isDuishouResp = isDuishouResp(inputBean);

                    //"在线表"数据删除
                    result = bondRequestQuoteService.deleteBondRequestQuoteByKey(inputBean, false,
                            isDuishouResp);

                    if (result.isStatus()) {
                        //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
                        bondRequestQuoteService.updateBondRequestQuoteStatus(inputBean, detIdList, isDuishouResp);

                        boolean isNew = DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(
                                inputBean.getExeTypeIBU()) ? true : false;

                        // 返回失败，不更新前置版本号
                        if (!isExecuteFail(exeType)) {
                            // 取Version，不要改成bVersion
                            inputBean.getIbuQuoteBondDto().setbVersion(quoteBond.getVersion());
                        }

                        //"在线表"数据插入，新版本号来自回复的参数
                        result = bondRequestQuoteService.insertBondRequestQuote(inputBean, isNew, isDuishouResp);
                    }
                } else {
                    result.setErrCode("BC_IBUPLINK_00009");
                    result.setErrMsg(getMessage("BC_IBUPLINK_00009", quoteBond.getVersion(),
                            quoteBond.getQuoteId(), oldVersion));
                }
            } finally {
                UserUtil.clear();
            }
        } else {
            result.setErrCode("BC_IBUPLINK_00007");
            result.setErrMsg(getMessage("BC_IBUPLINK_00007"));
        }

        return result;
    }

    private Predicate<IbuQuoteBondDetDto> bondDetFilter(IbuQuoteBondDetDto p) {
        return q -> {
            boolean result = false;
            if (StringUtils.isBlank(q.getReqId()) || StringUtils.isBlank(p.getReqId())) {
                if (StringUtils.isBlank(p.getReqId())) {
                    result = StringUtils.equals(q.getCpPartyId(), p.getCpPartyId());
                }
            } else {
                result = StringUtils.equals(q.getReqId(), p.getReqId());
            }
            return result;
        };
    }

    private boolean isDuishouResp(ReceiveBondRequestQuoteDto inputBean) {
        boolean result = false;
        IbuQuoteBondDto bondDto = inputBean.getIbuQuoteBondDto();

        if (bondDto != null) {
            List<String> operateTypeList = Arrays.asList(
                    DictCodeList.银行间上行操作类型_对手方拒绝请求报价.getValue(),
                    DictCodeList.银行间上行操作类型_对手方新增请求回复.getValue(),
                    DictCodeList.银行间上行操作类型_对手方修改请求回复.getValue(),
                    DictCodeList.银行间上行操作类型_对手方撤销请求回复.getValue(),
                    DictCodeList.银行间上行操作类型_接收已过期.getValue());

            if (DictCodeList.银行间上行本方角色_非做市方.getValue().equals(bondDto.getQuoteRoleType()) &&
                    operateTypeList.contains(bondDto.getExeType())) {
                result = true;
            }
        }

        return result;
    }

    /**
     * 接收网关回复现券请求报价信息<br />
     * 接收网关回复现券请求报价信息
     *
     * @param inputBean GateWayReturnMsgDto 网关返回信息
     * @param userInfo  User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto receBondReqQuoteGateWayMsg(GateWayReturnMsgDto inputBean, User userInfo) {
        String quoteId = inputBean.getQuoteId();
//        quoteId = bondRequestQuoteService.getQuoteIdByPquoteId(inputBean.getpQuoteId());
//        inputBean.setQuoteId(quoteId);

        //保存错误信息到日志表
        if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
            saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
        }

        QuoteResultDto result = new QuoteResultDto(true);
        StringBuffer message = new StringBuffer();

        // 验证必须项
        if (StringUtils.isBlank(inputBean.getQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "quoteId") + "\n");
        }
/*        if (inputBean.getVersion() == null) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "version") + "\n");
        }*/
        if (StringUtils.isBlank(inputBean.getpQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "pQuoteId") + "\n");
        }

        if (message.length() > 0) {
            result.setErrCode("BC_IBUPLINK_00002");
            result.setErrMsg(message.toString());
            result.setStatus(false);
            return result;
        } else {
            log.warn(inputBean.getErrCode() + ": " + inputBean.getErrMsg());
            result = bondRequestQuoteService.updateBondRequestQuoteVersion(inputBean);
            return result;
        }
    }

    /**
     * 发送请求报价反馈
     */
    private QuoteResultDto sendBondRequestQuote(String exeTypeIBU, BondRequestQuoteDto inputBean,
                                                Map<String, String> attrMap, List<String> detIdList) {
        QuoteResultDto result = new QuoteResultDto();
        IbuQuoteBondDto ibuQuoteBondDto = inputBean.getIbuQuoteBondDto();
        String quoteId = ibuQuoteBondDto.getQuoteId();

        // 请求报价查询
        BondRequestQuoteDto bondRequestQuoteDto = bondRequestQuoteService.getBondRequestQuote(quoteId);

        if (bondRequestQuoteDto != null && bondRequestQuoteDto.getIbuQuoteBondDto() != null) {
            bondRequestQuoteDto.setExeTypeIBU(exeTypeIBU);
            //备注 add by lijie2 20200118
            bondRequestQuoteDto.getIbuQuoteBondDto().setRemark(ibuQuoteBondDto.getRemark());
            //备注 add by lijie2 20200118
            //撤销发送状态特殊处理为空 需求要求
            if (StringUtils.equals(exeTypeIBU, DictCodeList.银行间上行操作类型_撤销.getValue())) {
                bondRequestQuoteDto.getIbuQuoteBondDto().setSendStatus(StringUtils.EMPTY);
                if (null != bondRequestQuoteDto.getIbuQuoteBondDetDtoList() && bondRequestQuoteDto.getIbuQuoteBondDetDtoList().size() > 0) {
                    for (IbuQuoteBondDetDto ibuQuoteBondDetDto : bondRequestQuoteDto.getIbuQuoteBondDetDtoList()) {
                        ibuQuoteBondDetDto.setSendStatus(StringUtils.EMPTY);
                    }
                }
            }
            //多组合改造  成交的关系表数据  从前台带出
            List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
            if (StringUtils.equals(exeTypeIBU, DictCodeList.银行间上行操作类型_本方回复成交.getValue())
            ||StringUtils.equals(exeTypeIBU, DictCodeList.银行间上行操作类型_本方请求回复成交.getValue())) {
                ibuQuoteEtrRes = inputBean.getIbuQuoteEtrReDtoList();
            }
            // 现券请求报价必须项校验
            ResultBaseDto validation = routeIntfSubService.inputBondReqCheck(bondRequestQuoteDto);
            // 校验不通过
            if (validation != null && !validation.isStatus()) {
                result.setStatus(false);
                result.setErrCode(validation.getErrCode());
                result.setErrMsg(validation.getErrMsg());
            } else {
                // 获取路由配置方案
                IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(attrMap);

                // 请求报价状态检验
                ValidMessageDto validResult =
                        bondRequestQuoteService.validateBondRequestQuoteStatus(bondRequestQuoteDto, false);
                bondRequestQuoteService.deleteWflBondRequestQuoteByKey(quoteId, false);
                //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
                bondRequestQuoteService.updateBondRequestQuoteStatus(bondRequestQuoteDto, detIdList, false);

                if (DictCodeList.银行间上行操作类型_本方请求回复成交.getValue().equals(exeTypeIBU)
                        || DictCodeList.银行间上行操作类型_本方修改请求回复.getValue().equals(exeTypeIBU)
                        || DictCodeList.银行间上行操作类型_本方撤销请求回复.getValue().equals(exeTypeIBU)) {
                    //更新bondDialogueQuote对象中的组合代码和委托代码
                    if (ibuQuoteBondDto.getPortfolioId() != null) {
                        bondRequestQuoteDto.getIbuQuoteBondDto().setPortfolioId(ibuQuoteBondDto.getPortfolioId());
                    }
                    if (ibuQuoteBondDto.getEntrustId() != null) {
                        bondRequestQuoteDto.getIbuQuoteBondDto().setEntrustId(ibuQuoteBondDto.getEntrustId());
                    }
                    //多组合 关系表数据单独处理
                    if (null != ibuQuoteEtrRes && ibuQuoteEtrRes.size() > 0) {
                        //版本号取值主表
                        ibuQuoteEtrRes.stream().forEach(item -> {
                            item.setVersion(bondRequestQuoteDto.getIbuQuoteBondDto().getVersion());
                        });
                        bondRequestQuoteDto.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
                    }
                }

                // 校验通过
                if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
                    // 如果是回退操作，则需要回退历史数据
                    if (DictCodeList.银行间上行操作类型_本方报价回退.getValue().equals(exeTypeIBU)) {
                        result = bondRequestQuoteService.restoreBondRequestQuoteByKey(bondRequestQuoteDto);
                    } else {
                        result = bondRequestQuoteService.updateBondRequestQuoteByKey(bondRequestQuoteDto);
                    }

                    if (intfScheme != null) {
                        result.setSendType(intfScheme.getSendType());
                    }
                } else {
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(validResult.getMessageWithId());
                    return result;
                }
            }
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00018");
            result.setErrMsg(getMessage("BC_IBUPLINK_00018", quoteId));
        }
        return result;
    }

    /**
     * 现券请求报价新增发送
     */
    private QuoteResultDto sendNewBondRequestQuote(BondRequestQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue());

        // 现券请求报价必须项校验
        ResultBaseDto validation = routeIntfSubService.inputBondReqCheck(inputBean);
        // 校验不通过
        if (validation != null && !validation.isStatus()) {
            result.setStatus(false);
            result.setErrCode(validation.getErrCode());
            result.setErrMsg(validation.getErrMsg());
        } else {
            // 获取路由配置方案
            IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());

            result = insertBondRequestQuoteWithCheck(inputBean, needSearch);
            if (intfScheme != null) {
                result.setSendType(intfScheme.getSendType());
            }
        }
        return result;
    }

    /**
     * 请求报价验证插入
     */
    private QuoteResultDto insertBondRequestQuoteWithCheck(
            BondRequestQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto();
        String quoteId = null;
        if (inputBean.getIbuQuoteBondDto() != null) {
            quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();
        }
        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof BondRequestQuoteDto) {
                bondRequestQuoteService.deleteWflBondRequestQuoteByKey(quoteId, false);
                bondRequestQuoteService.deleteBondRequestQuoteByKey(inputBean, true, false);
                result = bondRequestQuoteService.insertBondRequestQuote((BondRequestQuoteDto) validResult, true, false);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            //更新inputBean对象中的交易状态、操作类型和发送状态
            bondRequestQuoteService.updateBondRequestQuoteStatus(inputBean, null, false);
            result = bondRequestQuoteService.insertBondRequestQuote(inputBean, true, false);
        }
        return result;
    }

    /**
     * 验证请求报价是否已经存在于数据库中，若存在则进行DTO中的状态更新和验证<br />
     *
     * @param inputBean  BondDialogueQuoteDto
     * @param needSearch boolean 是否需要从数据库中查询DTO
     * @return Object 如果校验通过，返回BondDialogueQuoteDto，不通过返回ValidMessageDto
     * @throws QuoteResultException
     */
    private Object validateExists(BondRequestQuoteDto inputBean,
                                  boolean needSearch) throws QuoteResultException {
        //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
        bondRequestQuoteService.updateBondRequestQuoteStatus(inputBean, null, false);

        // 请求报价状态检验
        ValidMessageDto validResult =
                bondRequestQuoteService.validateBondRequestQuoteStatus(inputBean, needSearch);

        // 校验通过
        if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
            return inputBean;
        } else {
            return validResult;
        }
    }

    /**
     * 批量新增和批量新增发送的共通方法
     */
    private QuoteResultBatDto batchInsertBondRequestQuote(String exeTypeIBU,
                                                          List<BondRequestQuoteDto> inputList) {
        QuoteResultBatDto batchResult = new QuoteResultBatDto();
        QuoteResultDto result;
        List<String> quoteIdList = new ArrayList<>();    // 删除用
        List<String> detIdList = new ArrayList<>();        // 删除用
        Map<String, String> sendTypeMap = new HashMap<>();
        //region 遍历验证和更新状态
        for (BondRequestQuoteDto inputBean : inputList) {
            inputBean.setExeTypeIBU(exeTypeIBU);
            String quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();

            //region 银行间上行操作类型_本方新增发送
            if (DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue().equals(exeTypeIBU)) {
                // 现券请求报价发送必须项校验
                ResultBaseDto validation = routeIntfSubService.inputBondReqCheck(inputBean);

                // 校验不通过
                if (validation != null && !validation.isStatus()) {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode(validation.getErrCode());
                    result.setErrMsg(validation.getErrMsg());
                    batchResult.addQuoteResult(result);
                } else {
                    // 获取路由配置方案
                    IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());
                    if (intfScheme != null) {
                        sendTypeMap.put(quoteId, intfScheme.getSendType());
                    }
                }
            }
            //endRegion

            // 报价ID已存在
            if (StringUtils.isNotBlank(quoteId)) {
                Object validResult = null;
                try {
                    validResult = validateExists(inputBean, true);
                } catch (QuoteResultException e) {
                    batchResult.addQuoteResult(e.getQuoteResult());
                    continue;
                }

                // 验证通过
                if (validResult instanceof BondRequestQuoteDto) {
                    if (((BondRequestQuoteDto) validResult).getIbuQuoteBondDto() != null) {
                        quoteIdList.add(((BondRequestQuoteDto) validResult).getIbuQuoteBondDto().getQuoteId());
                    }
                    if (((BondRequestQuoteDto) validResult).getIbuQuoteBondDetDtoList() != null) {
                        for (IbuQuoteBondDet ibuQuoteBondDet : ((BondRequestQuoteDto) validResult).getIbuQuoteBondDetDtoList()) {
                            detIdList.add(ibuQuoteBondDet.getDetId());
                        }
                    }
                    inputBean = (BondRequestQuoteDto) validResult;
                } else {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
                    batchResult.addQuoteResult(result);
                }
            } else {
                //更新inputBean对象中的交易状态、操作类型和发送状态
                bondRequestQuoteService.updateBondRequestQuoteStatus(inputBean, null, false);
            }
        }
        //endRegion

        if (batchResult.getQuoteResultListSize() > 0) {
            batchResult.setSuccessAccout(0);
            batchResult.setFailAccout(inputList.size());
            batchResult.setErrMsgList(
                    batchResult.getQuoteResultList().stream().map(p -> p.getErrMsg()).collect(
                            Collectors.toList()));
            return batchResult;
        } else {
            // 批量删除
            if (CollectionUtils.isNotEmpty(detIdList)) {
                ibuMainMapper.deleteQuoteDetByIds(detIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteDetHisByIds(detIdList);
            }
            if (CollectionUtils.isNotEmpty(quoteIdList)) {
                ibuMainMapper.deleteQuoteByIds(quoteIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteHisByIds(quoteIdList);
            }

            // 批量插入
            batchResult = bondRequestQuoteService.batchInsertBondRequestQuote(inputList);
            batchResult.getQuoteResultList().forEach(
                    p -> p.setSendType(sendTypeMap.get(p.getBaseQuoteDto().getQuoteId())));

            return batchResult;
        }
    }

    //endRegion=======================请求报价End=========================

    //region=======================质押式回购报价Start=========================

    /**
     * 质押式回购根据成交编号获取委托编号
     *
     * @param execid
     * @return String
     */
    @Override
    public String getRepoEntrustIdByExeid(String execid) {
        return ibuMainMapper.getRepoEntrustIdByExeid(execid);
    }

    @Override
    public List<RepoQuoteExtDto> getRepoDialogueQuoteList(RepoQuoteSearchDto inputBean,
                                                          User userInfo) {
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        return ibuMainMapper.selectRepoQuoteList(inputBean);
    }

    //add by chenjiaxing 质押式回购改造 20210819
    @Override
    public List<RepoQuoteExtDto> getRepoDialogueQuoteList2(RepoQuoteSearchDto inputBean,
                                                           User userInfo) {
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        return ibuMainMapper.selectRepoQuoteList2(inputBean);
    }
    //add by chenjiaixng 质押式回购改造 20210819

    @Override
    public com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteExtDto> getRepoDialogueQuoteList(int pageNum, int pageSize,
                                                                       RepoQuoteSearchDto inputBean,
                                                                       User userInfo) {
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        return this.queryForPageForSelectReturn(pageNum, pageSize,() -> ibuMainMapper.selectRepoQuoteList(inputBean));
    }

    //add by chenjiaxing 质押式回购改造 20210819
    @Override
    public com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteExtDto> getRepoDialogueQuoteList2(int pageNum, int pageSize,
                                                                        RepoQuoteSearchDto inputBean,
                                                                        User userInfo) {
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        return this.queryForPageForSelectReturn(pageNum, pageSize,() -> ibuMainMapper.selectRepoQuoteList2(inputBean));
    }

    /**
     * 删除质押式回购报价的主表，主表历史，明细，明细历史数据
     *
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto deleteRepoDialogueQuote(String quoteId) {
        return repoDialogueQuoteSubService.deleteRepoDialogueQuoteByKey(quoteId, true);
    }

    @Override
    public Page<RepoQuoteExtDto> getRepoDialogueQuotePageList(int pageNum, int pageSize,
                                                              RepoQuoteSearchDto inputBean,
                                                              User userInfo) {
        Page<RepoQuoteExtDto> pageData = new Page<>();
        CommonUtils.sortList(inputBean.getOrderField(), inputBean.getOrderDirection());
        com.github.pagehelper.Page<RepoQuoteExtDto> pageList = PageHelper.startPage(pageNum,
                pageSize).doSelectPage(() -> ibuMainMapper.selectRepoQuoteList(inputBean));
        pageData = new Page<>(pageList);
        return pageData;
    }

    @Override
    public RepoDialogueQuoteDto getRepoDialogueQuote(String quoteId) {
        return repoDialogueQuoteSubService.getRepoDialogueQuote(quoteId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto insertRepoDialogueQuote(RepoDialogueQuoteDto inputBean, User userInfo) {
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
        QuoteResultDto result = insertRepoDialogueQuoteWithCheck(inputBean, true);
        return result;
    }

    /**
     * 对话报价验证插入
     */
    private QuoteResultDto insertRepoDialogueQuoteWithCheck(
            RepoDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getIbuQuoteCrepDto() != null) {
            quoteId = inputBean.getIbuQuoteCrepDto().getQuoteId();
        }
        boolean deleteHis = true;
        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof RepoDialogueQuoteDto) {
                //add by zhangnian 20210114  start
                if (!CommonUtils.isOperationHis((RepoDialogueQuoteDto) validResult)) {
                    deleteHis = false;
                }
                //add by zhangnian 20210114 end
                repoDialogueQuoteSubService.deleteRepoDialogueQuoteByKey(quoteId, deleteHis);
                result = repoDialogueQuoteSubService.insertRepoDialogueQuote(
                        (RepoDialogueQuoteDto) inputBean, true);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            //更新inputBean对象中的交易状态、操作类型和发送状态
            repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(inputBean);
            result = repoDialogueQuoteSubService.insertRepoDialogueQuote(inputBean, deleteHis);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto insertWflRepoDialogueQuote(RepoDialogueQuoteDto inputBean, User userInfo) {
        //inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
        QuoteResultDto result = insertWflRepoDialogueQuoteWithCheck(inputBean, true);
        return result;
    }

    /**
     * 对话报价验证插入
     */
    private QuoteResultDto insertWflRepoDialogueQuoteWithCheck(
            RepoDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getWflIbuQuoteCrepDto() != null) {
            quoteId = inputBean.getWflIbuQuoteCrepDto().getQuoteId();
        }

        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof RepoDialogueQuoteDto) {
                repoDialogueQuoteSubService.deleteWflRepoDialogueQuoteByKey(quoteId);
                result = repoDialogueQuoteSubService.insertWflRepoDialogueQuote(
                        (RepoDialogueQuoteDto) validResult, true);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            result = repoDialogueQuoteSubService.insertWflRepoDialogueQuote(inputBean, true);
        }

        return result;
    }

    /**
     * 验证对话报价是否已经存在于数据库中，若存在则进行DTO中的状态更新和验证<br />
     *
     * @param inputBean  RepoDialogueQuoteDto
     * @param needSearch boolean 是否需要从数据库中查询DTO
     * @return Object 如果校验通过，返回RepoDialogueQuoteDto，不通过返回ValidMessageDto
     * @throws QuoteResultException
     */
    private Object validateExists(RepoDialogueQuoteDto inputBean,
                                  boolean needSearch) throws QuoteResultException {
        //更新bondDialogueQuote对象中的交易状态、操作类型和发送状态
        repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(inputBean);

        // 对话报价状态检验
        ValidMessageDto validResult =
                repoDialogueQuoteSubService.validateRepoDialogueQuoteStatus(inputBean, needSearch);

        // 校验通过
        if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
            return inputBean;
        } else {
            return validResult;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultBatDto batchInsertRepoDialogueQuote(List<RepoDialogueQuoteDto> inputList,
                                                          User userInfo) {
        return batchInsertRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方报价新增.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultBatDto batchNewRepoDialogueQuote(List<RepoDialogueQuoteDto> inputList,
                                                       User userInfo) {
        return batchInsertRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方新增发送.getValue(), inputList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto updateRepoDialogueQuoteByKey(RepoDialogueQuoteDto inputBean,
                                                       User userInfo) {
        return repoDialogueQuoteSubService.updateRepoDialogueQuoteByKey(inputBean);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendNewRepoDialogueQuote(RepoDialogueQuoteDto inputBean, User userInfo) {
        return sendNewRepoDialogueQuote(inputBean, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendUpdatedRepoDialogueQuote(RepoDialogueQuoteDto inputBean, Map<String, String> attrMap, User userInfo) {
        QuoteResultDto result = new QuoteResultDto(true);
        if (inputBean != null && inputBean.getIbuQuoteCrepDto() != null
                && inputBean.getIbuQuoteCrepDto().getQuoteId() != null) {
            IbuQuoteCrepDto curDto = ibuQuoteMapper.selectQuoteCrepByKey(inputBean.getIbuQuoteCrepDto().getQuoteId());
            if (null != curDto.getSendStatus() && curDto.getSendStatus().equals(DictCodeList.银行间上行发送状态_成功.getValue())) {
                DictCodeList tradeStatusDict = DictCodeList.from(IbuDictCode.银行间上行报价状态, curDto.getTradeStatus());
                switch (tradeStatusDict) {
                    case 银行间上行报价状态_已新增:
                    case 银行间上行报价状态_已修改:
                        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方修改发送.getValue());
                        break;
                    case 银行间上行报价状态_已交谈:
                        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue());
                        break;
                    case 银行间上行报价状态_待回复:
                        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方交谈发送.getValue());
                        break;
                }
            }
            //特殊处理:双加打开 修改
            if (StringUtils.equals(curDto.getSendStatus(), DictCodeList.银行间上行发送状态_待发送.getValue())
                    && StringUtils.isEmpty(curDto.getTradeStatus())) {
                inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方新增发送.getValue());
            }
            if (StringUtils.equals(curDto.getTradeStatus(), DictCodeList.银行间上行报价状态_待回复.getValue())) {
                inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方交谈发送.getValue());
            }
        }
        // 现券对话报价必须项校验
        ResultBaseDto validation = routeIntfSubService.inputRepoDlgCheck(inputBean);
        // 校验不通过
        if (validation != null && !validation.isStatus()) {
            result.setStatus(false);
            result.setErrCode(validation.getErrCode());
            result.setErrMsg(validation.getErrMsg());
        } else {
            // 获取路由配置方案
            if(null !=inputBean){
                IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());
                result = insertRepoDialogueQuoteWithCheck(inputBean, true);
                if (intfScheme != null) {
                    result.setSendType(intfScheme.getSendType());
                }
            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuote(String quoteId, User userInfo) {
        QuoteResultDto result = new QuoteResultDto(true);
        RepoDialogueQuoteDto repoDialogueQuoteDto =
                repoDialogueQuoteSubService.getRepoDialogueQuote(quoteId);

        if (repoDialogueQuoteDto != null && repoDialogueQuoteDto.getIbuQuoteCrepDto() != null) {
            result = sendNewRepoDialogueQuote(repoDialogueQuoteDto, false);
        } else {
            result.setStatus(false);
            result.setErrCode("BC_IBUPLINK_00017");
            result.setErrMsg(getMessage("BC_IBUPLINK_00017", quoteId));
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteDeal(RepoDialogueQuoteDto inputbean, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方回复成交.getValue(), inputbean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteModify(RepoDialogueQuoteDto inputbean, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方报价修改.getValue(), inputbean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteRefuse(RepoDialogueQuoteDto inputbean, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(), inputbean, attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteRevoke(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(), setQuoteIdIntoRepoDialogue(quoteId), attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteRestore(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_本方报价回退.getValue(), setQuoteIdIntoRepoDialogue(quoteId), attrMap);
    }

    /**
     * 将quoteId存入RepoDialogueQuoteDto对象中
     *
     * @param quoteId
     * @return RepoDialogueQuoteDto
     * @see
     * @since 1.0
     */
    private RepoDialogueQuoteDto setQuoteIdIntoRepoDialogue(String quoteId) {
        RepoDialogueQuoteDto inputbean = new RepoDialogueQuoteDto();
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        ibuQuoteCrepDto.setQuoteId(quoteId);
        inputbean.setIbuQuoteCrepDto(ibuQuoteCrepDto);
        return inputbean;
    }

    /**
     * 质押式回购报价撤销<br />
     * 报价回复撤销，更新接口返回数据
     *
     * @param quoteId  String 报价编号
     * @param attrMap  Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto sendRepoDialogueQuoteCancel(String quoteId, Map<String, String> attrMap, User userInfo) {
        return sendRepoDialogueQuote(DictCodeList.银行间上行操作类型_撤销.getValue(), setQuoteIdIntoRepoDialogue(quoteId), attrMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto updateRepoDialogueQuoteInterfResp(ReceiveRepoDialogueQuoteDto inputBean,
                                                            User userInfo) {
        if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(inputBean.getExeTypeIBU())) {
            return repoDialogueQuoteInterfResp(inputBean, userInfo);
        } else {
            int timeUsed = 0;
            LinkedBlockingQueue<String> quoteIdQueue = new LinkedBlockingQueue<>(1);
            String quoteId = null;
            String pQuoteId = null;
            IbuQuoteCrepDto ibuQuoteCrep = inputBean.getIbuQuoteCrepDto();
            if (ibuQuoteCrep != null) {
                pQuoteId = ibuQuoteCrep.getpQuoteId();
            }

            do {
                quoteId = repoDialogueQuoteSubService.getRepoQuoteIdByPquoteId(pQuoteId);

                if (StringUtils.isNotBlank(quoteId)) {
                    quoteIdQueue.add(quoteId);
                }

                try {
                    quoteIdQueue.poll(checkInterval, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
                timeUsed += checkInterval;
            }
            while (StringUtils.isBlank(quoteId) && timeUsed < maxWaitTime);

            if (StringUtils.isNotBlank(quoteId)) {
                return repoDialogueQuoteInterfResp(inputBean, userInfo);
            }

            log.error(getMessage("BC_IBUPLINK_00021", maxWaitTime, pQuoteId));
            QuoteResultDto result = new QuoteResultDto(false);
            result.setErrCode("BC_IBUPLINK_00021");
            result.setErrMsg(getMessage("BC_IBUPLINK_00021", maxWaitTime, pQuoteId));
            return result;
        }
    }

    private QuoteResultDto repoDialogueQuoteInterfResp(ReceiveRepoDialogueQuoteDto inputBean,
                                                       User userInfo) {
        String quoteId = null;
        QuoteResultDto result = new QuoteResultDto(false);
        IbuQuoteCrepDto ibuQuoteCrep = inputBean.getIbuQuoteCrepDto();
        List<IbuQuoteCrepPledgeDto> quoteCrepPledgeList = inputBean.getIbuQuoteCrepPledgeDtoList();
        List<IbuQuoteCrepPledgeDto> allCrepPledgeList = new ArrayList<>();
        IbuQuoteCrepDto oldQuoteCrep = new IbuQuoteCrepDto();
        Long oldVersion = -2L;
        String oldUser = null, oldDept = null, exeType = null;

        if (ibuQuoteCrep != null) {
            //region准备工作
            quoteId = ibuQuoteCrep.getQuoteId();
            exeType = ibuQuoteCrep.getExeType();

            // add by zhangliang 对手方新增报价时会出现我方接收先撤回后新增的场景，需要将此判断条件去除
            /*if (!DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(inputBean.getExeTypeIBU())
                && StringUtils.isBlank(quoteId)) {
                quoteId = repoDialogueQuoteSubService.getRepoQuoteIdByPquoteId(ibuQuoteCrep.getpQuoteId());
                ibuQuoteCrep.setQuoteId(quoteId);
            }*/
            String dbQuoteId = repoDialogueQuoteSubService.getRepoQuoteIdByPquoteId(ibuQuoteCrep.getpQuoteId());
            if (StringUtils.isNotBlank(dbQuoteId)) {
                quoteId = dbQuoteId;
            }
            ibuQuoteCrep.setQuoteId(quoteId);

            if (CollectionUtils.isNotEmpty(quoteCrepPledgeList)) {
                for (IbuQuoteCrepPledgeDto det : quoteCrepPledgeList) {
                    det.setQuoteId(quoteId);
                    if (det.getVersion() == null || det.getVersion() <= 0) {
                        det.setVersion(ibuQuoteCrep.getVersion());
                    }
                }
            }

            //保存错误信息到日志表
            if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
                saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
            }

            //主表和明细表要先查再合并
            RepoDialogueQuoteDto oldQuoteRepoDto = repoDialogueQuoteSubService.getRepoDialogueQuote(quoteId);

            if (oldQuoteRepoDto != null) {
                try {
                    if (oldQuoteRepoDto.getIbuQuoteCrepDto() != null) {
                        oldQuoteCrep = oldQuoteRepoDto.getIbuQuoteCrepDto();
                        oldVersion = oldQuoteCrep.getVersion();
                        oldUser = oldQuoteCrep.getUpdateUser();
                        oldDept = oldQuoteCrep.getCreateDept();
                        BeanUtil.copyPropertiesIgnoreNull(oldQuoteCrep, ibuQuoteCrep);
                        BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrep, oldQuoteCrep);
                    }

                    if (CollectionUtils.isEmpty(quoteCrepPledgeList)) {
                        quoteCrepPledgeList = new ArrayList<>();
                    }

                    if (CollectionUtils.isNotEmpty(oldQuoteRepoDto.getIbuQuoteCrepPledgeDtoList())) {
                        //AI报文调佣该逻辑 quoteCrepPledgeList为空
                        if (CollectionUtils.isNotEmpty(quoteCrepPledgeList)) {
                            List<IbuQuoteCrepPledgeDto> quoteCrepPledgeFinalList = new ArrayList<>(quoteCrepPledgeList);
                            // 与数据库中已存在的押品合并信息
                            List<IbuQuoteCrepPledgeDto> updatedCrepPledgeList = oldQuoteRepoDto.getIbuQuoteCrepPledgeDtoList()
                                    .stream().map(p -> {
                                        Optional<IbuQuoteCrepPledgeDto> detOpt = quoteCrepPledgeFinalList.stream()
                                                .filter(q -> StringUtils.equals(q.getQuoteId(), p.getQuoteId())
                                                        && Long.compare(q.getBranch(), p.getBranch()) == 0
                                                        && StringUtils.equals(q.getPledgeFinprodId(), p.getPledgeFinprodId()))
                                                .findFirst();

                                        if (detOpt.isPresent()) {
                                            BeanUtil.copyPropertiesIgnoreNull(p, detOpt.get());
                                            return p;
                                        } else {
                                            // 质押式报价押品全删全插
                                            return null;
                                        }
                                    }).filter(w -> w != null).collect(Collectors.toList());

                            if (CollectionUtils.isNotEmpty(updatedCrepPledgeList)) {
                                List<String> existsPledgeIds = updatedCrepPledgeList.stream().map(
                                        p -> p.getQuoteId() + "$" + p.getBranch() + "$"
                                                + p.getPledgeFinprodId()).collect(Collectors.toList());

                                // 添加数据库中不存在的押品信息
                                allCrepPledgeList.addAll(quoteCrepPledgeFinalList.stream().filter(
                                        p -> !existsPledgeIds.contains(
                                                p.getQuoteId() + "$" + p.getBranch() + "$" + p.getPledgeFinprodId())).collect(
                                        Collectors.toList()));

                                // 添加与数据库中合并过的押品信息
                                allCrepPledgeList.addAll(updatedCrepPledgeList);
                            } else {
                                allCrepPledgeList.addAll(quoteCrepPledgeList);
                            }
                        } else {
                            //保留数据库业务明细表数据
                            allCrepPledgeList.addAll(oldQuoteRepoDto.getIbuQuoteCrepPledgeDtoList());
                        }
                    } else {
                        allCrepPledgeList.addAll(quoteCrepPledgeList);
                    }
                    //AI报文特殊处理
                    if (CollectionUtils.isEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                        inputBean.setIbuQuoteEtrReDtoList(oldQuoteRepoDto.getIbuQuoteEtrReDtoList());
                    }
                    if (CollectionUtils.isEmpty(inputBean.getIbuQuoteEntCrepPledgeDtoList())) {
                        inputBean.setIbuQuoteEntCrepPledgeDtoList(oldQuoteRepoDto.getIbuQuoteEntCrepPledgeDtoList());
                    }
                } catch (BeansException e) {
                    log.error(e.getMessage());
                    result.setStatus(false);
                    result.setErrCode("JavaError");
                    result.setErrMsg(e.getMessage());
                    return result;
                } finally {
                    inputBean.setIbuQuoteCrepPledgeDtoList(allCrepPledgeList);
                }
            }

            if (userInfo == null) {
                userInfo = new User();
                if (UserUtil.DEFAULT_USER.equals(ibuQuoteCrep.getUpdateUser()) && oldUser != null) {
                    userInfo.setUserName(oldUser);
                } else {
                    userInfo.setUserName(ibuQuoteCrep.getUpdateUser());
                }
                if (UserUtil.DEFAULT_DEPT.equals(ibuQuoteCrep.getCreateDept()) && oldDept != null) {
                    userInfo.setDeptCode(oldDept);
                } else {
                    userInfo.setDeptCode(ibuQuoteCrep.getCreateDept());
                }
                UserUtil.bindUser(userInfo);
            }
            //endRegion

            try {
                // 主表版本号要小于当前传过来的版本号
                if (oldVersion < ibuQuoteCrep.getVersion()) {
                    //"在线表"数据删除
                    result = repoDialogueQuoteSubService.deleteRepoDialogueQuoteByKey(ibuQuoteCrep.getQuoteId(), false);

                    if (result.isStatus()) {
                        //更新bondRequestQuoteDto对象中的交易状态、操作类型和发送状态
                        repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(inputBean);

                        boolean isNew = DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                                inputBean.getExeTypeIBU()) ? true : false;

                        // 返回失败，不更新前置版本号
                        if (!isExecuteFail(exeType)) {
                            // 取Version，不要改成bVersion
                            inputBean.getIbuQuoteCrepDto().setbVersion(ibuQuoteCrep.getVersion());
                        }

                        //"在线表"及历史表数据插入，新版本号来自回复的参数
                        result = repoDialogueQuoteSubService.insertRepoDialogueQuote(inputBean, isNew);
                    }
                } else {
                    result.setErrCode("BC_IBUPLINK_00009");
                    result.setErrMsg(getMessage("BC_IBUPLINK_00009", ibuQuoteCrep.getVersion(),
                            ibuQuoteCrep.getQuoteId(), oldVersion));
                }
            } finally {
                UserUtil.clear();
            }
        } else {
            result.setErrCode("BC_IBUPLINK_00007");
            result.setErrMsg(getMessage("BC_IBUPLINK_00007"));
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuoteResultDto receRepoDlgQuoteGateWayMsg(GateWayReturnMsgDto inputBean, User userInfo) {
        String quoteId = inputBean.getQuoteId();
//        quoteId = repoDialogueQuoteSubService.getRepoQuoteIdByPquoteId(inputBean.getpQuoteId());
//        inputBean.setQuoteId(quoteId);
        QuoteResultDto result = new QuoteResultDto(true);
        StringBuffer message = new StringBuffer();

        //保存错误信息到日志表
        if (StringUtils.isNotBlank(inputBean.getErrMsg()) || StringUtils.isNotBlank(inputBean.getErrCode())) {
            saveIbuQuoteErrlog(quoteId, inputBean.getErrCode(), inputBean.getErrMsg());
        }

        // 验证必须项
        if (StringUtils.isBlank(inputBean.getQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "quoteId") + "\n");
        }
//        if (inputBean.getVersion() == null) {
//            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "version") + "\n");
//        }
        if (StringUtils.isBlank(inputBean.getpQuoteId())) {
            message.append(getMessage("BC_IBUPLINK_00002", "inputBean", "pQuoteId") + "\n");
        }

        if (message.length() > 0) {
            result.setErrCode("BC_IBUPLINK_00002");
            result.setErrMsg(message.toString());
            result.setStatus(false);
            return result;
        } else {
            log.warn(inputBean.getErrCode() + ": " + inputBean.getErrMsg());
            result = repoDialogueQuoteSubService.updateRepoDialogueQuoteVersion(inputBean);
            return result;
        }

    }

    /**
     * 发送质押式回购报价反馈
     */
    private QuoteResultDto sendRepoDialogueQuote(String exeTypeIBU, RepoDialogueQuoteDto inputbean,
                                                 Map<String, String> attrMap) {
        QuoteResultDto result = new QuoteResultDto(true);
        IbuQuoteCrepDto ibuQuoteCrepDto = inputbean.getIbuQuoteCrepDto();
        String quoteId = ibuQuoteCrepDto.getQuoteId();

        // 对话报价查询
        RepoDialogueQuoteDto repoDialogueQuoteDto =
                repoDialogueQuoteSubService.getRepoDialogueQuote(quoteId);
        boolean flag = false;
        // 对手方发送新增报文后 直接回复成交时,库中没有组合信息的数据,只有流程中的表中存在
        if (CollectionUtils.isEmpty(repoDialogueQuoteDto.getIbuQuoteEtrReDtoList())) {
            flag = true;
            RepoDialogueQuoteDto repoDialogueQuoteDtoTemp = repoDialogueQuoteSubService.getWflRepoDialogueQuote(quoteId);
            IbuQuoteCrepDto ibuQuoteCrepDto1 = repoDialogueQuoteDto.getIbuQuoteCrepDto();
            IbuQuoteCrepDto ibuQuoteCrepDtoTemp = repoDialogueQuoteDtoTemp.getIbuQuoteCrepDto();
            if (ibuQuoteCrepDto1 != null && ibuQuoteCrepDtoTemp != null) {
                ibuQuoteCrepDto1.setActDays(ibuQuoteCrepDtoTemp.getActDays());
                ibuQuoteCrepDto1.setDealFeeAmt(ibuQuoteCrepDtoTemp.getDealFeeAmt());
                ibuQuoteCrepDto1.setSettFeeAmt(ibuQuoteCrepDtoTemp.getSettFeeAmt());
                ibuQuoteCrepDto1.setDipType(ibuQuoteCrepDtoTemp.getDipType());
            }
            if (CollectionUtils.isNotEmpty(repoDialogueQuoteDtoTemp.getIbuQuoteEtrReDtoList())) {
                repoDialogueQuoteDto.setIbuQuoteEtrReDtoList(repoDialogueQuoteDtoTemp.getIbuQuoteEtrReDtoList());
            }
            if (CollectionUtils.isNotEmpty(repoDialogueQuoteDtoTemp.getIbuQuoteCrepPledgeDtoList())) {
                repoDialogueQuoteDto.setIbuQuoteCrepPledgeDtoList(repoDialogueQuoteDtoTemp.getIbuQuoteCrepPledgeDtoList());
            }
            if (CollectionUtils.isNotEmpty(repoDialogueQuoteDtoTemp.getIbuQuoteEntCrepPledgeDtoList())) {
                repoDialogueQuoteDto.setIbuQuoteEntCrepPledgeDtoList(repoDialogueQuoteDtoTemp.getIbuQuoteEntCrepPledgeDtoList());
            }
        }
        if (repoDialogueQuoteDto.getIbuQuoteCrepDto() != null) {
            repoDialogueQuoteDto.setExeTypeIBU(exeTypeIBU);
            // 现券对话报价必须项校验
            ResultBaseDto validation = routeIntfSubService.inputRepoDlgCheck(repoDialogueQuoteDto);

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
                        repoDialogueQuoteSubService.validateRepoDialogueQuoteStatus(repoDialogueQuoteDto, false);

                //更新bondDialogueQuote对象中的交易状态、操作类型和发送状态
                repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(repoDialogueQuoteDto);

                if (DictCodeList.银行间上行操作类型_本方回复成交.getValue().equals(exeTypeIBU)
                        || DictCodeList.银行间上行操作类型_本方回复拒绝.getValue().equals(exeTypeIBU)) {
                    //更新bondDialogueQuote对象中的组合代码和委托代码
                    if (ibuQuoteCrepDto.getPortfolioId() != null) {
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().setPortfolioId(ibuQuoteCrepDto.getPortfolioId());
                    }
                    if (ibuQuoteCrepDto.getEntrustId() != null) {
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().setEntrustId(ibuQuoteCrepDto.getEntrustId());
                    }
                }

                // 校验通过
                if (validResult == null || StringUtils.isBlank(validResult.getMessage())) {
                    // 如果是回退操作，则需要回退历史数据
                    if (DictCodeList.银行间上行操作类型_本方报价回退.getValue().equals(exeTypeIBU)) {
                        result = repoDialogueQuoteSubService.restoreRepoDialogueQuoteByKey(repoDialogueQuoteDto);
                    } else if (flag) {
                        result = repoDialogueQuoteSubService.updateRepoDialogueQuoteByKeyDeal(repoDialogueQuoteDto);
                    } else {
                        result = repoDialogueQuoteSubService.updateRepoDialogueQuoteByKey(repoDialogueQuoteDto);
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
            result.setErrMsg(getMessage("BC_IBUPLINK_00018", quoteId));
        }

        return result;
    }

    /**
     * 质押式回购报价新增发送
     */
    private QuoteResultDto sendNewRepoDialogueQuote(
            RepoDialogueQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方新增发送.getValue());

        // 现券对话报价必须项校验
        ResultBaseDto validation = routeIntfSubService.inputRepoDlgCheck(inputBean);

        // 校验不通过
        if (validation != null && !validation.isStatus()) {
            result.setStatus(false);
            result.setErrCode(validation.getErrCode());
            result.setErrMsg(validation.getErrMsg());
        } else {
            // 获取路由配置方案
            IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());

            result = insertRepoDialogueQuoteWithCheck(inputBean, needSearch);

            if (intfScheme != null) {
                result.setSendType(intfScheme.getSendType());
            }
        }

        return result;
    }

    /**
     * 批量新增和批量新增发送的共通方法
     */
    private QuoteResultBatDto batchInsertRepoDialogueQuote(String exeTypeIBU,
                                                           List<RepoDialogueQuoteDto> inputList) {
        QuoteResultBatDto batchResult = new QuoteResultBatDto();
        QuoteResultDto result;
        List<String> quoteIdList = new ArrayList<>();    // 删除用
//        List<String> detIdList = new ArrayList<>();        // 删除用
        Map<String, String> sendTypeMap = new HashMap<>();

        //region 遍历验证和更新状态
        for (RepoDialogueQuoteDto inputBean : inputList) {
            inputBean.setExeTypeIBU(exeTypeIBU);
            String quoteId = inputBean.getIbuQuoteCrepDto().getQuoteId();

            //region 银行间上行操作类型_本方新增发送
            if (DictCodeList.银行间上行操作类型_本方新增发送.getValue().equals(exeTypeIBU)) {
                // 现券对话报价发送必须项校验
                ResultBaseDto validation = routeIntfSubService.inputRepoDlgCheck(inputBean);

                // 校验不通过
                if (validation != null && !validation.isStatus()) {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode(validation.getErrCode());
                    result.setErrMsg(validation.getErrMsg());
                    batchResult.addQuoteResult(result);
                } else {
                    // 获取路由配置方案
                    IbuIntfScheme intfScheme = routeConfigSubService.getSchemeForAttr(inputBean.getAttrMap());
                    if (intfScheme != null) {
                        sendTypeMap.put(quoteId, intfScheme.getSendType());
                    }
                }
            }
            //endRegion

            // 报价ID已存在
            if (StringUtils.isNotBlank(quoteId)) {
                Object validResult = null;
                try {
                    validResult = validateExists(inputBean, true);
                } catch (QuoteResultException e) {
                    batchResult.addQuoteResult(e.getQuoteResult());
                    continue;
                }

                // 验证通过
                if (validResult instanceof RepoDialogueQuoteDto) {
                    if (((RepoDialogueQuoteDto) validResult).getIbuQuoteCrepDto() != null) {
                        quoteIdList.add(((RepoDialogueQuoteDto) validResult).getIbuQuoteCrepDto().getQuoteId());
                    }
                    /*if (((RepoDialogueQuoteDto)validResult).getIbuQuoteCrepPledgeDtoList() != null) {
                        for(){
                            
                        }
                        detIdList.add(((RepoDialogueQuoteDto)validResult).getIbuQuoteBondDetDto().getDetId());
                    }*/
                    inputBean = (RepoDialogueQuoteDto) validResult;
                } else {
                    result = new QuoteResultDto();
                    result.setStatus(false);
                    result.setErrCode("StatusValidationError");
                    result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
                    batchResult.addQuoteResult(result);
                }
            } else {
                //更新inputBean对象中的交易状态、操作类型和发送状态
                repoDialogueQuoteSubService.updateRepoDialogueQuoteStatus(inputBean);
            }
        }
        //endRegion

        if (batchResult.getQuoteResultListSize() > 0) {
            batchResult.setSuccessAccout(0);
            batchResult.setFailAccout(inputList.size());
            batchResult.setErrMsgList(
                    batchResult.getQuoteResultList().stream().map(p -> p.getErrMsg()).collect(
                            Collectors.toList()));
            return batchResult;
        } else {
            // 批量删除
            if (CollectionUtils.isNotEmpty(quoteIdList)) {
                ibuMainMapper.deleteQuoteCrepPledgeByIds(quoteIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteCrepPledgeHisByIds(quoteIdList);
            }
            if (CollectionUtils.isNotEmpty(quoteIdList)) {
                ibuMainMapper.deleteQuoteCrepByIds(quoteIdList);
                // 历史表数据删除:Version=-1
                ibuMainMapper.deleteQuoteCrepHisByIds(quoteIdList);
            }

            // 批量插入
            batchResult = repoDialogueQuoteSubService.batchInsertRepoDialogueQuote(inputList);
            batchResult.getQuoteResultList().forEach(
                    p -> p.setSendType(sendTypeMap.get(p.getBaseQuoteDto().getQuoteId())));

            return batchResult;
        }
    }

    /**
     * 获取质押数量<br />
     *
     * @param quoteId         String 报价编号
     * @param quoteDate       Date 报价日期
     * @param pledgeFinprodId String 质押金融产品代码
     * @return BigDecimal
     */
    @Override
    public BigDecimal getPledgeFaceValue(String quoteId, Date quoteDate, String pledgeFinprodId) {
        return ibuMainMapper.getPledgeFaceValue(quoteId, quoteDate, pledgeFinprodId);
    }
    //endRegion=======================质押式回购报价End=========================

    //region=======================路由配置方案Start=========================

    /**
     * {@inheritDoc}
     */
    @Override
    public IbuIntfScheme getSchemeForAttr(Map<String, String> attrMap) {
        return routeConfigSubService.getSchemeForAttr(attrMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IbuIntfScheme getDefaultScheme() {
        return routeConfigSubService.getDefaultScheme();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto insertScheme(IbuIntfScheme ibuIntfScheme, User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.insertScheme(ibuIntfScheme);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto updateScheme(String schemeId, IbuIntfScheme ibuIntfScheme,
                                      User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.updateScheme(schemeId, ibuIntfScheme);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto deleleScheme(String schemeId, User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.deleleScheme(schemeId);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto setDefaultScheme(String schemeId, User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.setDefaultScheme(schemeId);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBaseDto insertQuoteScheme(String schemeId, Map<String, String> attrMap,
                                           User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.insertQuoteScheme(schemeId, attrMap);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto updateQuoteScheme(String oldSchemeId, String newSchemeId,
                                           String arrtGrpId, Map<String, String> attrMap,
                                           User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.updateQuoteScheme(oldSchemeId, newSchemeId, arrtGrpId, attrMap);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBaseDto deleteQuoteScheme(String schemeId, String arrtGrpId, User userInfo) {
        ResultBaseDto result = new ResultBaseDto(true);
        routeConfigSubService.deleteQuoteScheme(schemeId, arrtGrpId);
        return result;
    }
    //endRegion=======================路由配置方案End=========================

    //region=====================银行间上行错误码Start======================

    /**
     * 银行间上行错误码分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @param pageNum                int 页码，从1开始
     * @param pageSize               int 分页大小
     * @param ibuInrfErrMsgSearchDto IbuInrfErrMsgSearchDto 银行间上行错误码查询条件
     * @return Page<IbuIntfErrMsgDto>
     */
    @Override
    public Page<IbuIntfErrMsgDto> selectForPage(int pageNum, int pageSize,
                                                IbuInrfErrMsgSearchDto ibuInrfErrMsgSearchDto) {
        return ibuIntfErrMsgService.selectForPage(pageNum, pageSize, ibuInrfErrMsgSearchDto);
    }

    /**
     * 银行间上行错误码查询<br />
     * 根据传入的不为空的变量进行查询，需支持数据权限
     *
     * @param ibuInrfErrMsgSearchDto IbuInrfErrMsgSearchDto 银行间上行错误码查询条件
     * @return List<IbuIntfErrMsgDto>
     */
    @Override
    public List<IbuIntfErrMsgDto> selectIbuIntfErrMsgList(IbuInrfErrMsgSearchDto ibuInrfErrMsgSearchDto) {
        return ibuIntfErrMsgService.selectIbuIntfErrMsgList(ibuInrfErrMsgSearchDto);
    }

    /**
     * 根据错误码和错误信息查询<br />
     *
     * @param errCode String 错误码
     * @return IbuIntfErrMsgDto
     */
    @Override
    public IbuIntfErrMsgDto selectByErrCode(String errCode) {
        return ibuIntfErrMsgService.selectByErrCode(errCode);
    }

    /**
     * 根据报价编号查询错误信息
     *
     * @param quoteId String
     * @return IbuQuoteErrlogDto
     * @see
     * @since 1.0
     */
    @Override
    public IbuQuoteErrlogDto selectErrlog(String quoteId) {
        return ibuQuoteErrlogService.selectErrlogByQuoteId(quoteId);
    }

    /**
     * 保存错误信息
     *
     * @param quoteId String
     * @param errCode String
     * @param errMsg  String
     * @see
     * @since 1.0
     */
    public void saveIbuQuoteErrlog(String quoteId, String errCode,
                                   String errMsg) {
        IbuQuoteErrlogDto ibuQuoteErrlogDto = new IbuQuoteErrlogDto();
        ibuQuoteErrlogDto.setQuoteId(quoteId);
        ibuQuoteErrlogDto.setErrCode(errCode);
        if (!StringUtil.isNotEmpty(errMsg)) {
            // Added by JIAOXUJIN on 2020-06-06
            IbuIntfErrMsgDto errMsgDto = ibuIntfErrMsgService.selectByErrCode(errCode);
            if (errMsgDto != null) {
                ibuQuoteErrlogDto.setErrMsg(errMsgDto.getErrMsg());
            }
        } else {
            ibuQuoteErrlogDto.setErrMsg(errMsg);
        }
        ibuQuoteErrlogDto.setVersion(ibuQuoteErrlogService.getVersion());
        ibuQuoteErrlogService.insertIbuQuoteErrlog(ibuQuoteErrlogDto);
    }

    //endRegion=====================银行间上行错误码End=======================

    //region=====================Private Common Start======================

    private boolean isExecuteFail(String exeType) {
        if (exeType == null) return false;
        boolean result = false;
        List<String> failCodeList = Arrays.asList(
                DictCodeList.银行间上行操作类型_本方新增发送失败.getValue(),
                DictCodeList.银行间上行操作类型_本方修改发送失败.getValue(),
                DictCodeList.银行间上行操作类型_本方回复成交失败.getValue(),
                DictCodeList.银行间上行操作类型_本方回复拒绝失败.getValue(),
                DictCodeList.银行间上行操作类型_本方报价撤回失败.getValue(),
                DictCodeList.银行间上行操作类型_本方新增请求报价发送失败.getValue(),
                DictCodeList.银行间上行操作类型_本方请求报价撤回失败.getValue(),
                DictCodeList.银行间上行操作类型_本方请求回复成交失败.getValue(),
                DictCodeList.银行间上行操作类型_本方拒绝请求报价失败.getValue(),
                DictCodeList.银行间上行操作类型_本方新增请求回复失败.getValue(),
                DictCodeList.银行间上行操作类型_本方修改请求回复失败.getValue(),
                DictCodeList.银行间上行操作类型_本方撤销请求回复失败.getValue());

        if (failCodeList.contains(exeType)) {
            result = true;
        }

        return result;
    }

    @Override
    public QuoteResultDto insertWflBondRequestQuote(BondRequestQuoteDto inputBean, User userInfo) {
        inputBean.setExeTypeIBU(DictCodeList.银行间上行操作类型_本方请求报价新增.getValue());
        QuoteResultDto result = insertWflBondRequestQuoteWithCheck(inputBean, true);
        return result;
    }

    /**
     * 请求报价流程表验证插入
     */
    private QuoteResultDto insertWflBondRequestQuoteWithCheck(BondRequestQuoteDto inputBean, boolean needSearch) {
        QuoteResultDto result = new QuoteResultDto(true);
        String quoteId = null;
        if (inputBean.getWflIbuQuoteBond2Dto() != null) {
            quoteId = inputBean.getWflIbuQuoteBond2Dto().getQuoteId();
        }
        // 报价ID已存在
        if (StringUtils.isNotBlank(quoteId)) {
            Object validResult;
            try {
                validResult = validateExists(inputBean, needSearch);
            } catch (QuoteResultException e) {
                return e.getQuoteResult();
            }

            if (validResult instanceof BondRequestQuoteDto) {
                bondRequestQuoteService.deleteWflBondRequestQuoteByKey(quoteId, true);
                result = bondRequestQuoteService.insertWflBondRequestQuote((BondRequestQuoteDto) validResult, true, false);
            } else {
                result.setStatus(false);
                result.setErrCode("StatusValidationError");
                result.setErrMsg(((ValidMessageDto) validResult).getMessageWithId());
            }
        } else {
            result = bondRequestQuoteService.insertWflBondRequestQuote(inputBean, true, false);
        }
        return result;
    }

    @Override
    public boolean deleteWflDataForBond(String workflowId) {
        // TODO Auto-generated method stub
        if (StringUtils.isNotEmpty(workflowId)) {
            wflIbuQuoteBond2Mapper.deleteByWorkflowId(workflowId);
            wflIbuQuoteBondDetMapper.deleteByWorkflowId(workflowId);
            wflIbuQuoteEtrReMapper.deleteByWorkflowId(workflowId);
        }
        return true;
    }

    @Override
    public boolean deleteWflDataForRepo(String workflowId) {
        // TODO Auto-generated method stub
        if (StringUtils.isNotEmpty(workflowId)) {
            wflIbuQuoteCrepMapper.deleteByWorkflowId(workflowId);
            wflIbuQuoteCrepPledgeMapper.deleteByWorkflowId(workflowId);
            wflIbuQuoteEtrReMapper.deleteByWorkflowId(workflowId);
        }
        return true;
    }

    @Override
    public String selectWflQuoteIdByWorkflowId(String pageWflType, String workflowId) {
        String quoteId = StringUtils.EMPTY;
        if (StringUtils.equals(pageWflType, "BOND")) {
            if (StringUtils.isNotEmpty(workflowId)) {
                quoteId = ibuMainMapper.selectBondQuoteIdByWorkflowId(workflowId);
            }
        } else if (StringUtils.equals(pageWflType, "REPO")) {
            if (StringUtils.isNotEmpty(workflowId)) {
                quoteId = ibuMainMapper.selectRepoQuoteIdByWorkflowId(workflowId);
            }
        }
        return quoteId;
    }

    // add by etliu 20210318 银行间上下行多组合 start

    /**
     * 根据报价编号，查询银行间报价指令关系业务表数据
     *
     * @param quoteId 报价编号
     * @return 银行间报价指令关系业务表数据
     */
    @Override
    public List<IbuQuoteEtrReDto> selectQuoteEtrReDataByQuoteId(String quoteId) {
        return ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId);
    }
    // add by etliu 20210318 银行间上下行多组合 end

    //endRegion=====================Private Common End=======================
}
