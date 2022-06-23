/*
 * 文件名：BondRequestQuoteSubServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.business.bondreqservice.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.joyintech.background.component.uplink.interbank.database.dao.*;
import com.joyintech.background.component.uplink.interbank.database.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.IDGenerator;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.uplink.interbank.business.bondreqservice.BondRequestQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.dto.BondRequestQuoteDto;
import com.joyintech.background.component.uplink.interbank.business.dto.GateWayReqMsgDTO;
import com.joyintech.background.component.uplink.interbank.business.dto.GateWayReturnMsgDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteBondDetDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteBondDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEtrReDto;
import com.joyintech.background.component.uplink.interbank.business.dto.QuoteResultBatDto;
import com.joyintech.background.component.uplink.interbank.business.dto.QuoteResultDto;
import com.joyintech.background.component.uplink.interbank.business.dto.ValidMessageDto;
import com.joyintech.background.component.uplink.interbank.business.dto.WflIbuQuoteBondDetDto;
import com.joyintech.background.component.uplink.interbank.business.dto.local.BondQuoteHisSearchDto;
import com.joyintech.background.component.uplink.interbank.common.CommonUtils;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.common.IbuDictCode;

/**
 * 现券请求报价服务
 * @author Jiaoxujin
 * @version 1.0 2020年5月9日
 * @see BondRequestQuoteSubServiceImpl
 * @since 1.0
 */
@Service
public class BondRequestQuoteSubServiceImpl extends BaseService<IbuQuoteBond> implements BondRequestQuoteSubService {

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private IbuQuoteMapper ibuQuoteMapper;
    
    @Autowired
    private IbuQuoteBondMapper quoteBondMapper;

    @Autowired
    private IbuQuoteBondHisMapper quoteBondHisMapper;
    
    @Autowired
    WflIbuQuoteBond2Mapper wflIbuQuoteBond2Mapper;
    
    @Autowired
    WflIbuQuoteBondDetMapper wflIbuQuoteBondDetMapper;

    @Autowired
    IbuQuoteEtrReMapper ibuQuoteEtrReMapper;

    @Autowired
    IbuQuoteBondDetHisMapper ibuQuoteBondDetHisMapper;
    
    /** 非做市方；Key为交易状态，发送状态；Value为允许的操作类型：List<银行间上行操作类型代码> */
    private static Table<String, String, List<String>> statusTable03 = HashBasedTable.create();
    
    /** 做市方；Key为交易状态，发送状态；Value为允许的操作类型：List<银行间上行操作类型代码> */
    private static Table<String, String, List<String>> statusTable04 = HashBasedTable.create();
    
    static{
        statusTable03.put(StringUtils.EMPTY, 
                                    DictCodeList.银行间上行发送状态_待发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue(),
                                    DictCodeList.银行间上行操作类型_本方请求报价新增.getValue()));
        
        // add by etliu 20210125 start
        statusTable03.put(DictCodeList.银行间上行报价状态_新增中.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue(),
                DictCodeList.银行间上行操作类型_本方请求报价新增.getValue()));
        // add by etliu 20210125 end
        
        statusTable03.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方新增请求报价发送成功.getValue(),
                                    DictCodeList.银行间上行操作类型_本方新增请求报价发送失败.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_待回复.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方请求报价撤回.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方请求报价撤回成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方请求报价撤回失败.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已撤回.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList());
        statusTable03.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已回复.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方请求回复成交.getValue(),
                                            DictCodeList.银行间上行操作类型_本方请求报价撤回.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已终止.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable03.put(DictCodeList.银行间上行报价状态_成交中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方请求回复成交成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方请求回复成交失败.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已成交.getValue(),
                StringUtils.EMPTY,
                Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_成交中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable03.put(DictCodeList.银行间上行报价状态_已过期.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable03.put(DictCodeList.银行间上行报价状态_已撤销.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());

        statusTable04.put(DictCodeList.银行间上行报价状态_待回复.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方拒绝请求报价.getValue(),
                            DictCodeList.银行间上行操作类型_本方新增请求回复.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已撤回.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable04.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已过期.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable04.put(DictCodeList.银行间上行报价状态_拒绝中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方拒绝请求报价成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方拒绝请求报价失败.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已拒绝.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable04.put(DictCodeList.银行间上行报价状态_拒绝中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_回复中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方新增请求回复成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方新增请求回复失败.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已回复.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方撤销请求回复.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_回复中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_修改中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方修改请求回复成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方修改请求回复失败.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已修改.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable04.put(DictCodeList.银行间上行报价状态_修改中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList());
        statusTable04.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方撤销请求回复成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方撤销请求回复失败.getValue()));
       /* statusTable04.put(DictCodeList.银行间上行报价状态_待回复.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方撤销请求回复成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方撤销请求回复失败.getValue()));*/
        statusTable04.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable04.put(DictCodeList.银行间上行报价状态_已撤销.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
    }
    
    @PostConstruct
    public void beforeInit() {
        log.debug("SubBondRequestQuoteSubServiceImpl Constructed.");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BondRequestQuoteDto getBondRequestQuote(String quoteId) {
        BondRequestQuoteDto result = new BondRequestQuoteDto();
        result.setIbuQuoteBondDto(ibuQuoteMapper.selectQuoteBondByKey(quoteId));
        result.setIbuQuoteBondDetDtoList(ibuQuoteMapper.selectByQuoteListId(quoteId));
        
        // add by etliu 20210309 银行间上下行多组合 start
        // 银行间报价指令关系业务表
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId));
        // add by etliu 20210309 银行间上下行多组合 end
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BondRequestQuoteDto> getBondRequesteQuoteHisList(BondQuoteHisSearchDto inputBean) {
        return ibuQuoteMapper.selectBondRequestQuoteHisList(inputBean);
    }
    
    /**
     * 
     * 根据外部请求编号查询请求报价明细信息
     * @param reqId String
     * @return IbuQuoteBondDetDto
     * @see
     * @since 1.0
     */
    @Override
    public  IbuQuoteBondDetDto selectQuoteDetByReqId(String reqId){
        return ibuQuoteMapper.selectQuoteDetByReqId(reqId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<BondRequestQuoteDto> getBondRequestQuoteHisPageList(int pageNum, int pageSize,
                                                                 BondQuoteHisSearchDto inputBean) {
        Page<BondRequestQuoteDto> pageData = new Page<>();
        com.github.pagehelper.Page<BondRequestQuoteDto> pageList = PageHelper.startPage(pageNum,
            pageSize).doSelectPage(() -> ibuQuoteMapper.selectBondRequestQuoteHisList(inputBean));
        pageData = new Page<>(pageList);
        return pageData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidMessageDto validateRequiredItems(BondRequestQuoteDto inputBean) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 请求报价进行操作时，需校验目前请求报价的报价状态和发送状态是否允许该项操作。
     */
    @Override
    public ValidMessageDto validateBondRequestQuoteStatus(BondRequestQuoteDto inputBean, boolean needSearch) {
        if (inputBean == null) return null;
        ValidMessageDto validMessage = new ValidMessageDto();
        
        IbuQuoteBondDto ibuQuoteBond = inputBean.getIbuQuoteBondDto();
        IbuQuoteBondDto curDto = ibuQuoteBond;
        if (needSearch && ibuQuoteBond != null) {
            curDto = ibuQuoteMapper.selectQuoteBondByKey(ibuQuoteBond.getQuoteId());
        }
        if (curDto != null) {
            String message = validateStatus(curDto.getQuoteRoleType(),
                inputBean.getExeTypeIBU(), curDto.getTradeStatus(), curDto.getSendStatus());
            if (message != null) {
                validMessage.setMessage(message);
                validMessage.setQuoteId(ibuQuoteBond.getQuoteId());
                return validMessage;
            }
        }
        return null;
    }
    
    @Override
    public List<ValidMessageDto> validateBondRequestQuoteStatus(List<BondRequestQuoteDto> inputList, boolean needSearch) {
        List<ValidMessageDto> validList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(inputList)) {
            String exeType = StringUtil.EMPTY;
            Optional<String> exeTypeOpt = inputList.stream().map(p -> p.getExeTypeIBU()).findFirst();
            if (exeTypeOpt.isPresent()) {
                exeType = exeTypeOpt.get();
            }

            List<IbuQuoteBondDto> quoteDtoList = inputList.stream().map(
                p -> p.getIbuQuoteBondDto()).collect(Collectors.toList());
            List<String> quoteIdList = quoteDtoList.stream().map(p -> p.getQuoteId()).collect(
                Collectors.toList());
            List<IbuQuoteBondDto> curDtoList = quoteDtoList;
            if (needSearch && CollectionUtils.isNotEmpty(quoteIdList)) {
                curDtoList = ibuQuoteMapper.selectQuoteBondByKeys(quoteIdList);
            }

            if (CollectionUtils.isNotEmpty(curDtoList)) {
                for (IbuQuoteBondDto curDto : curDtoList) {
                    String message = validateStatus(
                        curDto.getQuoteRoleType(), exeType, curDto.getTradeStatus(), curDto.getSendStatus());
                    if (message != null) {
                        ValidMessageDto validMessage = new ValidMessageDto();
                        validMessage.setMessage(message);
                        validMessage.setQuoteId(curDto.getQuoteId());
                        validList.add(validMessage);
                    }
                }
            }
        }

        // 新增时，默认为新增中状态，不需要验证状态
        return validList;
    }
    
    /**
     * 验证状态变更操作是否合理
     * @param tradeStatus String
     * @param sendStatus String
     * @param curTradeStatus String
     * @param curSendStatus String
     * @return String 验证消息
     */
    protected String validateStatus(String quoteRoleType, String exeTypeIBU, String curTradeStatus,
                                       String curSendStatus){
        String msg = null;

        List<String> allowExeTypeList03 = BondRequestQuoteSubServiceImpl.statusTable03.get(
            StringUtil.nullToEmpty(curTradeStatus), StringUtil.nullToEmpty(curSendStatus));
        List<String> allowExeTypeList04 = BondRequestQuoteSubServiceImpl.statusTable04.get(
            StringUtil.nullToEmpty(curTradeStatus), StringUtil.nullToEmpty(curSendStatus));
        
        if(DictCodeList.银行间上行本方角色_非做市方.getValue().equals(quoteRoleType)){
            if (allowExeTypeList03 == null) {
                msg = getMessage("BC_IBUPLINK_00004",
                    StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                    StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName());
            }
            else if (!allowExeTypeList03.contains(exeTypeIBU)) {
                msg = getMessage("BC_IBUPLINK_00003",
                    StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                    StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName(),
                    StringUtils.isBlank(exeTypeIBU) ? StringUtil.EMPTY : DictCodeList.from(IbuDictCode.银行间上行操作类型, exeTypeIBU).getCodeName());
            }
        }else{
            if (allowExeTypeList04 == null) {
                msg = getMessage("BC_IBUPLINK_00004",
                    StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(
                        IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                    StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(
                        IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName());
            }
            else if (!allowExeTypeList04.contains(exeTypeIBU)) {
                msg = getMessage("BC_IBUPLINK_00003",
                    StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(
                        IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                    StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(
                        IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName(),
                    StringUtils.isBlank(exeTypeIBU) ? StringUtil.EMPTY : DictCodeList.from(
                        IbuDictCode.银行间上行操作类型, exeTypeIBU).getCodeName());
            }
        }
        return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessageDto> batchValidateRequiredItems(List<BondRequestQuoteDto> inputList) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 请求报价状态检验_批量
     */
    @Override
    public List<ValidMessageDto> batchValidateBondRequestQuoteStatus(List<BondRequestQuoteDto> inputList) {
        return validateBondRequestQuoteStatus(inputList, true);
    }

    /**
     * 请求报价新增
     */
    @Override
    public QuoteResultDto insertBondRequestQuote(BondRequestQuoteDto inputBean, 
                                                 boolean isNew, boolean isDuishouRespByJet) {
        return insertBondRequestQuote(inputBean, isNew, isDuishouRespByJet, true);
    }

    /**
     * 请求报价新增
     */
    @Override
    public QuoteResultDto insertBondRequestQuote(BondRequestQuoteDto inputBean, 
                                                 boolean isNew, boolean isDuishouRespByJet, boolean insertHis) {
        setDefaultStatus(inputBean,isNew);

        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                Long version = inputBean.getIbuQuoteBondDto().getVersion();
                Long bVersion = inputBean.getIbuQuoteBondDto().getbVersion();
                if (!isNew) inputBean.setVersion(version, bVersion);
                int affectRows = 0;

                // 向历史表写入数据
                if (insertHis) {
                    affectRows += insertHistoryData(inputBean);
                }

                affectRows += ibuQuoteMapper.batchInsetIbuQuoteBondDetDto(inputBean.getIbuQuoteBondDetDtoList());
                
                //多组合
    	        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                     for (IbuQuoteEtrReDto IbuQuoteEtrReDto :inputBean.getIbuQuoteEtrReDtoList()) {
                         IbuQuoteEtrReKey ibuQuoteEtrReKey = new IbuQuoteEtrReKey();
                         ibuQuoteEtrReKey.setEntrustId(IbuQuoteEtrReDto.getEntrustId());
                         ibuQuoteEtrReKey.setQuoteId(IbuQuoteEtrReDto.getQuoteId());
                         ibuQuoteEtrReKey.setVersion(IbuQuoteEtrReDto.getVersion());
                         ibuQuoteEtrReMapper.deleteByPrimaryKey(ibuQuoteEtrReKey);
                     }
    	            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
    	        }

                // 向“在线表”插入数据
                if (isDuishouRespByJet) {
                    // modify by etliu 20210123 start
//                    Map<String, String> detStatusMap = inputBean.getIbuQuoteBondDetDtoList().stream().collect(Collectors.toMap(IbuQuoteBondDetDto::getTradeStatus, IbuQuoteBondDetDto::getTradeStatus));
                    Map<String, String> detStatusMap = new HashMap<>();
                    for (IbuQuoteBondDetDto ibuQuoteBondDetDto : inputBean.getIbuQuoteBondDetDtoList()) {
                        detStatusMap.put(ibuQuoteBondDetDto.getTradeStatus(), ibuQuoteBondDetDto.getTradeStatus());
                    }
                    if ((detStatusMap != null && detStatusMap.size() == 1
                         && (detStatusMap.containsKey(DictCodeList.银行间上行报价状态_已拒绝.getValue())
                    /* || detStatusMap.containsKey(DictCodeList.银行间上行报价状态_已过期.getValue()) */))
                        || StringUtil.equals(DictCodeList.银行间上行报价状态_已过期.getValue(),
                            inputBean.getIbuQuoteBondDto().getTradeStatus())) {
                        affectRows += quoteBondMapper.updateByPrimaryKeySelective(
                            inputBean.getIbuQuoteBondDto());
                    }
                    else {
                        //add by zznn 20210726 start
                        //针对做市方撤销  当所有报价全部撤回后  主报价终态应该为待回复 成功
                        //非做市方接收已过期  当所有报价全部已过期  主报价终态应该为待回复 成功
                        if(detStatusMap != null && detStatusMap.size() == 1&&(StringUtil.equals(inputBean.getExeTypeIBU(),DictCodeList.银行间上行操作类型_本方请求报价撤回成功.getValue())
                                ||StringUtil.equals(inputBean.getExeTypeIBU(),DictCodeList.银行间上行操作类型_接收已过期.getValue()))){
                            inputBean.getIbuQuoteBondDto().setTradeStatus(DictCodeList.银行间上行报价状态_待回复.getValue());
                            inputBean.getIbuQuoteBondDto().setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());
                            affectRows +=ibuQuoteMapper.updateBondQuoteFinalStatusForRevoke(inputBean.getIbuQuoteBondDto());
                        }
                        //add by zznn 20210726 end
                        else{
                            //查询明细表中是否存在至少一条（已回复）？更新主表交易状态，true: 已回复， false:待回复
                            affectRows += ibuQuoteMapper.updateBondQuoteTradeStatus(inputBean.getIbuQuoteBondDto());
                        }
                    }
                    // modify by etliu 20210123 end
                }
                else {
                    affectRows += quoteBondMapper.insertSelective(inputBean.getIbuQuoteBondDto());
                }

                return affectRows;
            }
        );
    }

    /**
     * 向报价历史表中插入数据
     * @param inputBean BondDialogueQuoteDto
     * @return int
     */
    private int insertHistoryData(BondRequestQuoteDto inputBean) {
        IbuQuoteBondHis quoteBondHis = new IbuQuoteBondHis();
        List<IbuQuoteBondDetHis> quoteBondDetHisList = new ArrayList<>();
        //银行间报价指令关系
        List<IbuQuoteEtrReHis> ibuQuoteEtrReHisList = new ArrayList<>();
        try {
            BeanUtils.copyProperties(quoteBondHis, inputBean.getIbuQuoteBondDto());
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteBondDetDtoList())) {
                for (int i = 0; i < inputBean.getIbuQuoteBondDetDtoList().size(); i++ ) {
                    IbuQuoteBondDetHis ibuQuoteBondDetHis = new IbuQuoteBondDetHis();
                    BeanUtils.copyProperties(ibuQuoteBondDetHis,
                        inputBean.getIbuQuoteBondDetDtoList().get(i));
                    quoteBondDetHisList.add(ibuQuoteBondDetHis);
                }
            }
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                inputBean.getIbuQuoteEtrReDtoList().stream().forEach(item -> {
                    IbuQuoteEtrReHis ibuQuoteEtrReHis = new IbuQuoteEtrReHis();
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteEtrReHis,item);
                    ibuQuoteEtrReHisList.add(ibuQuoteEtrReHis);
                });
            }
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }

        int affectRows = quoteBondHisMapper.insertSelective(quoteBondHis);
        
        if (CollectionUtils.isNotEmpty(quoteBondDetHisList)) {
            for (IbuQuoteBondDetHis ibuQuoteBondDetHis :quoteBondDetHisList) {
                IbuQuoteBondDetHisKey ibuQuoteBondDetHisKey = new IbuQuoteBondDetHisKey();
                ibuQuoteBondDetHisKey.setDetId(ibuQuoteBondDetHis.getDetId());
                ibuQuoteBondDetHisKey.setVersion(ibuQuoteBondDetHis.getVersion());
                ibuQuoteBondDetHisMapper.deleteByPrimaryKey(ibuQuoteBondDetHisKey);
            }
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteBondDetHis(quoteBondDetHisList);
        }
        
        if(ibuQuoteEtrReHisList.size()>0){
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrReHis(ibuQuoteEtrReHisList);
        }

        return affectRows;
    }
    
    /**
     * 执行方法并返回“处理结果”对象
     * @param inputBean BondRequestQuoteDto
     * @param quoteId String
     * @param func Function<String, Integer>
     * @return QuoteResultDto
     */
    private QuoteResultDto excuteAndReturnQuoteResult(BondRequestQuoteDto inputBean,
                                                      String quoteId,
                                                      Function<String, Integer> func){
        QuoteResultDto result = new QuoteResultDto();

        if (StringUtils.isNotBlank(quoteId) && inputBean == null) {
            IbuQuoteBondDto quoteBond = ibuQuoteMapper.selectQuoteBondByKey(quoteId);
            List<IbuQuoteBondDetDto> quoteBondDetList = ibuQuoteMapper.selectByQuoteListId(quoteId);
            inputBean = new BondRequestQuoteDto(quoteBond, quoteBondDetList);
            
            // add by etliu 20210313 银行间上下行多组合 start
           inputBean.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId));
            // add by etliu 20210313 银行间上下行多组合 end
        }

        int affectRows = func.apply(quoteId);
        log.info("affectRows is {}", affectRows);

        result.setBondRequestQuote(inputBean);
        result.setStatus(true);
            
        return result;
    }
    
    /**
     * 设置默认的交易状态和发送状态（可用于新增）
     * @param inputBean BondRequestQuoteDto
     * @param isNew boolean
     * @see
     * @since 1.0
     */
    private void setDefaultStatus(BondRequestQuoteDto inputBean, boolean isNew){
        String quoteId = IDGenerator.generate();
        if(inputBean.getIbuQuoteBondDto() != null){
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDto().getTradeStatus())) {
                inputBean.getIbuQuoteBondDto().setTradeStatus(StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getIbuQuoteBondDto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getIbuQuoteBondDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else{
                    inputBean.getIbuQuoteBondDto().setSendStatus(StringUtils.EMPTY);
                }
            }
            if (inputBean.getIbuQuoteBondDto().getVersion() == null) {
                inputBean.getIbuQuoteBondDto().setVersion(-1L);
            }
            if (inputBean.getIbuQuoteBondDto().getbVersion() == null) {
                inputBean.getIbuQuoteBondDto().setbVersion(-1L);
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDto().getQuoteId())) {
                inputBean.getIbuQuoteBondDto().setQuoteId(quoteId);
            }
        }
        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteBondDetDtoList())) {
            for(IbuQuoteBondDetDto ibuQuoteBondDet : inputBean.getIbuQuoteBondDetDtoList()){
                if (StringUtils.isBlank(ibuQuoteBondDet.getTradeStatus())) {
                    ibuQuoteBondDet.setTradeStatus(DictCodeList.银行间上行报价状态_新增中.getValue());
                }
                if (StringUtils.isBlank(ibuQuoteBondDet.getSendStatus())) {
                    if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                        inputBean.getExeTypeIBU())) {
                        // 已收到对手方的消息了，不需要再发送
                        ibuQuoteBondDet.setSendStatus(StringUtils.EMPTY);
                    }
                    else if (isNew) {
                        ibuQuoteBondDet.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                    }
                    else{
                        ibuQuoteBondDet.setSendStatus(StringUtils.EMPTY);
                    }
                }
                if (ibuQuoteBondDet.getVersion() == null) {
                    ibuQuoteBondDet.setVersion(inputBean.getIbuQuoteBondDto().getVersion());
                }
                if (StringUtils.isBlank(ibuQuoteBondDet.getDetId())) {
                    ibuQuoteBondDet.setDetId(IDGenerator.generate());
                }
                if (StringUtils.isBlank(ibuQuoteBondDet.getQuoteId())) {
                    ibuQuoteBondDet.setQuoteId(quoteId);
                }
            }
        }
    }
    
    /**
     * 设置默认的交易状态和发送状态（可用于新增）
     * @param inputBean BondRequestQuoteDto
     * @param isNew boolean
     * @see
     * @since 1.0
     */
    private void setWflDefaultStatus(BondRequestQuoteDto inputBean, boolean isNew){
        String quoteId = IDGenerator.generate();
        if(inputBean.getWflIbuQuoteBond2Dto() != null){
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBond2Dto().getTradeStatus())) {
                inputBean.getWflIbuQuoteBond2Dto().setTradeStatus(StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBond2Dto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getWflIbuQuoteBond2Dto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getWflIbuQuoteBond2Dto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else{
                    inputBean.getWflIbuQuoteBond2Dto().setSendStatus(StringUtils.EMPTY);
                }
            }
            if (inputBean.getWflIbuQuoteBond2Dto().getVersion() == null) {
                inputBean.getWflIbuQuoteBond2Dto().setVersion(-1L);
            }
            if (inputBean.getWflIbuQuoteBond2Dto().getbVersion() == null) {
                inputBean.getWflIbuQuoteBond2Dto().setbVersion(-1L);
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBond2Dto().getQuoteId())) {
                inputBean.getWflIbuQuoteBond2Dto().setQuoteId(quoteId);
            }
        }
        if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteBondDetDtoList())) {
            for(WflIbuQuoteBondDetDto wflIbuQuoteBondDet : inputBean.getWflIbuQuoteBondDetDtoList()){
                if (StringUtils.isBlank(wflIbuQuoteBondDet.getTradeStatus())) {
                	wflIbuQuoteBondDet.setTradeStatus(DictCodeList.银行间上行报价状态_新增中.getValue());
                }
                if (StringUtils.isBlank(wflIbuQuoteBondDet.getSendStatus())) {
                    if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                        inputBean.getExeTypeIBU())) {
                        // 已收到对手方的消息了，不需要再发送
                    	wflIbuQuoteBondDet.setSendStatus(StringUtils.EMPTY);
                    }
                    else if (isNew) {
                    	wflIbuQuoteBondDet.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                    }
                    else{
                    	wflIbuQuoteBondDet.setSendStatus(StringUtils.EMPTY);
                    }
                }
                if (wflIbuQuoteBondDet.getVersion() == null) {
                	wflIbuQuoteBondDet.setVersion(inputBean.getWflIbuQuoteBond2Dto().getVersion());
                }
                if (StringUtils.isBlank(wflIbuQuoteBondDet.getDetId())) {
                	wflIbuQuoteBondDet.setDetId(IDGenerator.generate());
                }
                if (StringUtils.isBlank(wflIbuQuoteBondDet.getQuoteId())) {
                	wflIbuQuoteBondDet.setQuoteId(quoteId);
                }
            }
        }
    }

    public QuoteResultDto restoreBondRequestQuoteByKey(BondRequestQuoteDto inputBean) {
        QuoteResultDto result = new QuoteResultDto(true);

        IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
        List<IbuQuoteBondDetDto> quoteBondDetList = inputBean.getIbuQuoteBondDetDtoList();
        String quoteId = quoteBond.getQuoteId();
        Long bVersion = quoteBond.getbVersion();

        if (bVersion != null) {
            // 历史请求报价查询
            List<BondRequestQuoteDto> bdqList = ibuQuoteMapper.selectBondRequestQuoteHisList(
                new BondQuoteHisSearchDto(quoteId, bVersion));

            if (CollectionUtils.isNotEmpty(bdqList)) {
                // "在线表"删除当前版本的记录
                if(!StringUtil.isEmptyList(quoteBondDetList)){
                    List<String> detIdList = quoteBondDetList.stream().map(
                        p -> p.getDetId()).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(detIdList)) {
                        ibuQuoteMapper.deleteQuoteDetByDetIds(detIdList);
                    }
                }
                ibuQuoteMapper.deleteQuoteEtrReByQuoteId(quoteId);
                quoteBondMapper.deleteByPrimaryKey(quoteId);

                // 再将历史数据插入到“在线表”中，本循环只执行一次
                for (BondRequestQuoteDto hisDto : bdqList) {
                    //region add by曹国 新增发送失败回退的特殊处理
                    if (DictCodeList.银行间上行报价状态_新增中.getValue().equals(
                        hisDto.getIbuQuoteBondDto().getTradeStatus())
                        && DictCodeList.银行间上行发送状态_已发送.getValue().equals(
                            hisDto.getIbuQuoteBondDto().getSendStatus())) {
                        hisDto.getIbuQuoteBondDto().setTradeStatus(StringUtils.EMPTY);
                        hisDto.getIbuQuoteBondDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                    }
                    //endRegion add end 

                    hisDto.setExeTypeIBU(inputBean.getExeTypeIBU());
                    quoteBondMapper.insert(hisDto.getIbuQuoteBondDto());

                    if(!StringUtil.isEmptyList(hisDto.getIbuQuoteBondDetDtoList())){
                        //region add by曹国 新增发送失败回退的特殊处理
                        if (DictCodeList.银行间上行报价状态_新增中.getValue().equals(
                            hisDto.getIbuQuoteBondDto().getTradeStatus())
                            && DictCodeList.银行间上行发送状态_已发送.getValue().equals(
                                hisDto.getIbuQuoteBondDto().getSendStatus())) {
                            for (IbuQuoteBondDetDto detDto : hisDto.getIbuQuoteBondDetDtoList()) {

                                detDto.setTradeStatus(StringUtils.EMPTY);
                                detDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                            }
                        }
                        //endRegion add end
                        ibuQuoteMapper.batchInsetIbuQuoteBondDetDto(hisDto.getIbuQuoteBondDetDtoList());
                        //多组合
                        if(!StringUtil.isEmptyList(hisDto.getIbuQuoteEtrReDtoList())){
                            ibuQuoteMapper.batchInsetIbuQuoteEtrRe(hisDto.getIbuQuoteEtrReDtoList());
                            inputBean.setIbuQuoteEtrReDtoList(hisDto.getIbuQuoteEtrReDtoList());
                        }
                        inputBean.setIbuQuoteBondDetDtoList(hisDto.getIbuQuoteBondDetDtoList());
                    }

                    inputBean.setIbuQuoteBondDto(hisDto.getIbuQuoteBondDto());
                }
            }
            else {
                result.setErrCode("BC_IBUPLINK_00005");
                result.setErrMsg(getMessage("BC_IBUPLINK_00005", quoteId, quoteBond.getVersion(), bVersion));
                result.setStatus(false);
            }
        }
        else {
            result.setErrCode("BC_IBUPLINK_00006");
            result.setErrMsg(getMessage("BC_IBUPLINK_00006", quoteId, quoteBond.getVersion()));
            result.setStatus(false);
        }

        result.setBondRequestQuote(inputBean);

        return result;
    }

    /**
     * 请求报价新增_批量
     */
    @Override
    public QuoteResultBatDto batchInsertBondRequestQuote(List<BondRequestQuoteDto> inputList) {
        QuoteResultBatDto resultDto = new QuoteResultBatDto();

        inputList.forEach(p -> {
            setDefaultStatus(p, true);
            p.setVersion(-1L, -1L);
        });

        // 向历史表插入数据
        ibuQuoteMapper.batchInsertBondRequestQuote(inputList, "_HIS");
        
        // 向“在线表”插入数据
        ibuQuoteMapper.batchInsertBondRequestQuote(inputList,"");
        List<QuoteResultDto> quoteResultList = new ArrayList<>();
        for(BondRequestQuoteDto brqdto : inputList){
            QuoteResultDto item = new QuoteResultDto();
            item.setBondRequestQuote(brqdto);
            item.setStatus(true);
            quoteResultList.add(item);
        }
        resultDto.setQuoteResultList(quoteResultList);
        resultDto.setSuccessAccout(inputList.size());
        resultDto.setFailAccout(0);
        return resultDto;
    }

    /**
     * 请求报价更新_主键
     */
    @Override
    public QuoteResultDto updateBondRequestQuoteByKey(BondRequestQuoteDto inputBean) {
        setDefaultStatus(inputBean,false);
        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                //String quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();

                // 向“在线表”更新数据（含前置版本号）
                int affectRows = quoteBondMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDto());
                affectRows += ibuQuoteMapper.updateQuoteBondDetList(inputBean.getIbuQuoteBondDetDtoList());
                //针对多组合  先删除后插入
                if(!StringUtil.isEmptyList(inputBean.getIbuQuoteEtrReDtoList())){
                    affectRows += ibuQuoteMapper.deleteQuoteEtrReByQuoteId(inputBean.getIbuQuoteBondDto().getQuoteId());
                    affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
                }
                // 删除历史表Version=-1的数据
                //affectRows += ibuQuoteMapper.deleteQuoteDetHisByQuoteId(quoteId);
                //affectRows += deleteQuoteBondHisData(quoteId);

                // 再向历史表插入数据 Version=-1
                //inputBean.setVersion(-1L, inputBean.getIbuQuoteBondDto().getVersion());
                //affectRows += insertHistoryData(inputBean);
                return affectRows;
            }
        );
    }

    /**
     * 删除报价信息历史表Version=-1的数据
     * @param quoteId String
     * @param affectRows int
     * @return int
     */
    private int deleteQuoteBondHisData(String quoteId) {
        // ADD BY ZHANGLIANG 20200724 页面回退到-1版本号时继续新增发送，jet那边给过来的版本号会存在与已有数据重复的可能，报主键冲突，此处回退后新增发送把His数据全部删除处理
        // IbuQuoteBondHisKey quoteHisKey = new IbuQuoteBondHisKey();
        // quoteHisKey.setQuoteId(quoteId);
        // quoteHisKey.setVersion(-1L);
        // int affectRows = quoteBondHisMapper.deleteByPrimaryKey(quoteHisKey);
        int affectRows = ibuQuoteMapper.deleteQuoteHisByQuoteId(quoteId);
        return affectRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuoteResultDto deleteBondRequestQuoteByKey(
                                                      BondRequestQuoteDto inputBean,
                                                      boolean deleteHis, boolean isDuishouRespByJet) {
        return excuteAndReturnQuoteResult(inputBean, inputBean.getIbuQuoteBondDto().getQuoteId(),
            x -> {
                //明细数据删除
                int affectRows = 0;
                if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteBondDetDtoList())) {
                    boolean hasAnyDetId = inputBean.getIbuQuoteBondDetDtoList().stream().anyMatch(
                        p -> StringUtils.isNotBlank(p.getDetId()));
                    if (hasAnyDetId) {
                        List<String> detIdList = inputBean.getIbuQuoteBondDetDtoList().stream().map(
                            p -> p.getDetId()).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(detIdList)) {
                            ibuQuoteMapper.deleteQuoteDetByDetIds(detIdList);
                            if (deleteHis) {
                                // 删除历史表Version=-1的数据
                                affectRows += ibuQuoteMapper.deleteQuoteDetHisByDetIds(detIdList);
                            }
                        }
                    }
                    else {
                        affectRows += ibuQuoteMapper.deleteQuoteDetByQuoteId(x);
                        if (deleteHis) {
                            // 删除历史表Version=-1的数据
                            affectRows += ibuQuoteMapper.deleteQuoteDetHisByQuoteId(x);
                        }
                    }
                }

                if (!isDuishouRespByJet) {
                    //报价主信息数据删除
                    affectRows += quoteBondMapper.deleteByPrimaryKey(x);
                }

                if (deleteHis) {
                    affectRows += deleteQuoteBondHisData(x);
                }
                
                // 关系表数据删除
                affectRows += ibuQuoteMapper.deleteQuoteEtrReByQuoteId(x);
                if(deleteHis){
                    affectRows += ibuQuoteMapper.deleteQuoteEtrReHisByQuoteId(x);
                }

                return affectRows;
            }
        );
    }

    /**
     * 请求报价状态更新
     */
    @Override
    public void updateBondRequestQuoteStatus(
                  BondRequestQuoteDto inputBean,List<String> detIdList, boolean isDuishouResp) {
        if (inputBean == null) return;
        
        Map<String, String> statusMap = CommonUtils.getBondRequestQuoteStatus(inputBean.getExeTypeIBU());
        String tradeStatus = statusMap.getOrDefault(IbuDictCode.银行间上行报价状态.getValue(),
            StringUtils.EMPTY);
        String sendStatus = statusMap.getOrDefault(IbuDictCode.银行间上行发送状态.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue());
        
        // add by etliu 20210121 start
        // 26-接收已过期 场景，不做交易状态重置处理
        if (StringUtil.equals(DictCodeList.银行间上行报价状态_已过期.getValue(), tradeStatus)) {
            return;
        }
        // add by etliu 20210121 end

        if (inputBean.getIbuQuoteBondDto() != null) {
            if (!DictCodeList.银行间上行操作类型_对手方拒绝请求报价.getValue().equals(inputBean.getExeTypeIBU())) {
                if(!DictCodeList.银行间上行操作类型_撤销.getValue().equals(inputBean.getExeTypeIBU())){
                    inputBean.getIbuQuoteBondDto().setSendStatus(sendStatus);
                }
                inputBean.getIbuQuoteBondDto().setTradeStatus(tradeStatus);
            }

            if (inputBean.getIbuQuoteBondDetDtoList() != null) {
                // 本方决定成交
                if (CollectionUtils.isNotEmpty(detIdList)) {
                    if (DictCodeList.银行间上行操作类型_本方请求回复成交.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType()) || 
                        DictCodeList.银行间上行操作类型_本方请求回复成交成功.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType()) || 
                        DictCodeList.银行间上行操作类型_本方请求回复成交失败.getValue().equals(inputBean.getIbuQuoteBondDto().getExeType())) {
                        inputBean.getIbuQuoteBondDetDtoList().stream().filter(p -> detIdList.contains(p.getDetId())).forEach(q -> {
                            q.setTradeStatus(tradeStatus);
                            q.setSendStatus(sendStatus);
                        });
                        return;
                    }
                }

                for (IbuQuoteBondDetDto ibuQuoteBondDet : inputBean.getIbuQuoteBondDetDtoList()) {
                    if (!(isDuishouResp && StringUtils.isNotBlank(ibuQuoteBondDet.getTradeStatus()))) {
                        ibuQuoteBondDet.setSendStatus(sendStatus);
                        ibuQuoteBondDet.setTradeStatus(tradeStatus);
                    }
                }
            }
        }
    }

    /**
     * 请求报价接口更新
     */
    @Override
    public void updateBondRequestQuoteInterf(BondRequestQuoteDto inputBean) {
        // 更新对话报价信息中的发送状态和交易状态
        updateBondRequestQuoteStatus(inputBean,null, false);
        quoteBondMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDto());
        ibuQuoteMapper.updateQuoteBondDetList(inputBean.getIbuQuoteBondDetDtoList());
    }

    @Override
    public QuoteResultDto restoreBondRequesteQuoteByKey(BondRequestQuoteDto inputBean) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QuoteResultDto updateBondRequestQuoteVersion(GateWayReturnMsgDto inputBean) {
        QuoteResultDto result = new QuoteResultDto(true);
        BondRequestQuoteDto bondRequestQuoteDto = new BondRequestQuoteDto();

        // 查询数据库中的现存数据
        IbuQuoteBondDto oldQuoteBond = ibuQuoteMapper.selectQuoteBondByKey(inputBean.getQuoteId());
        
        if (oldQuoteBond != null) {
            // 主表版本号要小于当前传过来的版本号
//            if (oldQuoteBond.getVersion() < inputBean.getVersion()) {
                // 用最新的信息更新“在线表”
                IbuQuoteBond quoteBond = new IbuQuoteBond();
                quoteBond.setQuoteId(inputBean.getQuoteId());

                if (inputBean.isStatus()) {
                    if (inputBean.getVersion() != null) {
                        // 用最新的信息更新“在线表”
                        quoteBond.setVersion(inputBean.getVersion());
                        // 设置前置版本
                        if(oldQuoteBond.getbVersion() != null && oldQuoteBond.getbVersion() == -1L){
                            quoteBond.setbVersion(inputBean.getVersion());
                        }
                    }
                    quoteBond.setpQuoteId(inputBean.getpQuoteId());
                }
                else {
                    // 修改报价发送状态
//                    quoteBond.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());
                    quoteBond.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                }

                quoteBondMapper.updateByPrimaryKeySelective(quoteBond);

                // 历史表先按Version=-1条件删除
                //deleteQuoteBondHisData(inputBean.getQuoteId());

                IbuQuoteBondHis quoteBondHis = new IbuQuoteBondHis();
                try {
                    BeanUtil.copyPropertiesIgnoreNull(quoteBondHis, oldQuoteBond);
                    BeanUtil.copyPropertiesIgnoreNull(quoteBondHis, quoteBond);
                    // 拦截器一定要设置userInfo中的信息及最新的操作时间
                }
                catch (BeansException e1) {
                    log.error(e1.getMessage());
                    result.setErrCode("BeansException");
                    result.setErrMsg(e1.getMessage());
                    result.setStatus(false);
                }
                // 向历史表中插入新版本号的数据
                //quoteBondHisMapper.insertSelective(quoteBondHis);
                bondRequestQuoteDto.setIbuQuoteBondDtoByHis(quoteBondHis);

                // 更新明细
                List<IbuQuoteBondDetHis> quoteBondDetHisList = new ArrayList<>();
                List<IbuQuoteBondDetDto> detList = new ArrayList<>();
                List<String> detIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(inputBean.getReqMsgDto())) {
                    for (GateWayReqMsgDTO dto : inputBean.getReqMsgDto()) {
                        IbuQuoteBondDetDto quoteBondDet = new IbuQuoteBondDetDto();
                        quoteBondDet.setQuoteId(inputBean.getQuoteId());
                        quoteBondDet.setDetId(dto.getDetId());
                        
                        if (inputBean.isStatus()) {
                            quoteBondDet.setVersion(inputBean.getVersion());
                            quoteBondDet.setReqId(dto.getReqId());
                            quoteBondDet.setCpPartyId(dto.getCpPartyId());
                        }
                        else {
                            // 修改报价发送状态
                            quoteBondDet.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());
                        }

                        detList.add(quoteBondDet);
                        if (StringUtils.isNotBlank(dto.getDetId())) {
                            detIdList.add(dto.getDetId());
                        }
                    }

                    ibuQuoteMapper.updateQuoteBondDetList(detList);

                    if (CollectionUtils.isNotEmpty(detIdList)) {
                        // 查询最新的报价明细信息
                        List<IbuQuoteBondDet> newQuoteDetList = ibuQuoteMapper.selectQuoteBondDetList(detIdList);

                        if (CollectionUtils.isNotEmpty(newQuoteDetList)) {
                            for (IbuQuoteBondDet newQuoteDet : newQuoteDetList) {
                                // 历史明细表先按Version=-1条件删除
                                //IbuQuoteBondDetHisKey detHisKey = new IbuQuoteBondDetHisKey();
                                //detHisKey.setDetId(newQuoteDet.getDetId());
                                //detHisKey.setVersion(-1L);
                                //quoteBondDetHisMapper.deleteByPrimaryKey(detHisKey);

                                // 再将最新的数据插入历史表
                                IbuQuoteBondDetHis quoteBondDetHis = new IbuQuoteBondDetHis();
                                try {
                                    BeanUtils.copyProperties(quoteBondDetHis, newQuoteDet);
                                }
                                catch (IllegalAccessException | InvocationTargetException e) {
                                    log.error(e.getMessage());
                                    if (e instanceof IllegalAccessException) {
                                        result.setErrCode("IllegalAccessException");
                                    }
                                    else {
                                        result.setErrCode("InvocationTargetException");
                                    }
                                    result.setErrMsg(e.getMessage());
                                    result.setStatus(false);
                                    break;
                                }

                                // 向历史明细表中插入新版本号的数据
                                //quoteBondDetHisMapper.insertSelective(quoteBondDetHis);
                                quoteBondDetHisList.add(quoteBondDetHis);
                            }
                        }
                    }
                }

                bondRequestQuoteDto.setIbuQuoteBondDetDtoByHis(quoteBondDetHisList);
                
                // add by etliu 20210316 银行间上下行多组合 start
                // 查询银行间报价指令关系业务表数据
                List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(
                    inputBean.getQuoteId());
                if (!StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                    bondRequestQuoteDto.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
                }
                // add by etliu 20210316 银行间上下行多组合 end
                
                result.setBondRequestQuote(bondRequestQuoteDto);
/*            }
            else {
                result.setErrCode("BC_IBUPLINK_00009");
                result.setErrMsg(getMessage("BC_IBUPLINK_00009", inputBean.getVersion(),
                    inputBean.getQuoteId(), oldQuoteBond.getVersion()));
                result.setStatus(false);
            }*/
        }
        else {
            result.setErrCode("BC_IBUPLINK_00008");
            result.setErrMsg(getMessage("BC_IBUPLINK_00008", inputBean.getQuoteId()));
            result.setStatus(false);
        }

        return result;
    }

    /**
     * 根据外部报价编号查询报价编号
     * @param pquoteId String
     * @return String 报价编号
     */
    @Override
    public String getQuoteIdByPquoteId(String pquoteId){
        return ibuQuoteMapper.getQuoteIdByPquoteId(pquoteId);
    }
	@Override
	public QuoteResultDto insertWflBondRequestQuote(BondRequestQuoteDto inputBean, boolean isNew,
			boolean isDuishouRespByJet) {
		setWflDefaultStatus(inputBean, isNew);

		return excuteAndReturnQuoteResult(inputBean, null, x -> {
			Long version = inputBean.getWflIbuQuoteBond2Dto().getVersion();
			Long bVersion = inputBean.getWflIbuQuoteBond2Dto().getbVersion();
			if (!isNew)
				inputBean.setVersion(version, bVersion);

			int affectRows = 0;

			for (WflIbuQuoteBondDetDto dto : inputBean.getWflIbuQuoteBondDetDtoList()) {
				affectRows += wflIbuQuoteBondDetMapper.insertSelective(dto);
			}
			affectRows += wflIbuQuoteBond2Mapper.insertSelective(inputBean.getWflIbuQuoteBond2Dto());
			
			//多组合
	        if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteEtrReDtoList())) {
	            affectRows += ibuQuoteMapper.batchInsetWflIbuQuoteEtrRe(inputBean.getWflIbuQuoteEtrReDtoList());
	        }
			
			return affectRows;
		});
	}
	
	@Override
	public QuoteResultDto deleteWflBondRequestQuoteByKey(String quoteId, boolean deleteHis) {
		return excuteAndReturnQuoteResult(null, quoteId,
	            x -> {
	                // 明细数据删除
	                int affectRows = wflIbuQuoteBond2Mapper.deleteWflQuoteDetByQuoteId(x);
	                // 报价主信息数据删除
	                affectRows += wflIbuQuoteBond2Mapper.deleteByPrimaryKey(x);
	                
	                //关系表删除
	                affectRows += ibuQuoteMapper.deleteWflIbuQuoteEtrReByQuoteId(x);
	                
	                return affectRows;
	            }
	        );
	}
	
	
	@Override
	public BondRequestQuoteDto getWflBondRequestQuote(String quoteId) {
		// TODO Auto-generated method stub
		BondRequestQuoteDto result = new BondRequestQuoteDto();
		result.setIbuQuoteBondDto(wflIbuQuoteBond2Mapper.selectQuoteBondByKey(quoteId));
		result.setIbuQuoteBondDetDtoList(wflIbuQuoteBond2Mapper.selectByQuoteListId(quoteId));
		
        // add by etliu 20210309 银行间上下行多组合 start
        // 银行间报价指令关系业务表
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectWflQuoteEtrReDataByQuoteId(quoteId));
        // add by etliu 20210309 银行间上下行多组合 end
		
		return result;
	}
    
}
