/*
 * 文件名：RepoDialogueQuoteSubServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.business.repodlgservice.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.IDGenerator;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.dto.local.BondQuoteHisSearchDto;
import com.joyintech.background.component.uplink.interbank.business.repodlgservice.RepoDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.common.CommonUtils;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.common.IbuDictCode;
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 质押式回购报价服务
 * @author Jiaoxujin
 * @version 1.0 2020年5月9日
 * @see RepoDialogueQuoteSubServiceImpl
 * @since 1.0
 */
@Service
public class RepoDialogueQuoteSubServiceImpl extends BaseService<IbuQuoteCrep> implements RepoDialogueQuoteSubService {
    private Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    IbuQuoteCrepMapper ibuQuoteCrepMapper;
    
    @Autowired
    IbuQuoteCrepPledgeMapper ibuQuoteCrepPledgeMapper;
    
    @Autowired
    WflIbuQuoteCrepMapper wflIbuQuoteCrepMapper;
    
    @Autowired
    WflIbuQuoteCrepPledgeMapper wflIbuQuoteCrepPledgeMapper;
    
    @Autowired
    IbuQuoteCrepHisMapper ibuQuoteCrepHisMapper;
    
    @Autowired
    IbuQuoteCrepPledgeHisMapper ibuQuoteCrepPledgeHisMapper;
    
    @Autowired
    IbuQuoteMapper ibuQuoteMapper;
    /** Key为交易状态，发送状态；Value为允许的操作类型：List<银行间上行操作类型代码> */
    private static Table<String, String, List<String>> statusTable = HashBasedTable.create();
    
    static{
        statusTable.put(StringUtils.EMPTY, 
                                    DictCodeList.银行间上行发送状态_待发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                        		DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                            DictCodeList.银行间上行操作类型_本方报价新增.getValue()));
        
        // add by etliu 20210125 start
        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                DictCodeList.银行间上行操作类型_本方报价新增.getValue()));
        // add by etliu 20210125 end
        
        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方新增发送失败.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已新增.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                            DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                            DictCodeList.银行间上行操作类型_本方报价撤回.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_待回复.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方回复成交.getValue(),
                            DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(),
                            DictCodeList.银行间上行操作类型_本方交谈修改.getValue()
                        ,DictCodeList.银行间上行操作类型_本方交谈发送.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_待回复.getValue(),
                DictCodeList.银行间上行发送状态_失败.getValue(),
                Arrays.asList(DictCodeList.银行间上行操作类型_本方交谈修改.getValue()
                        ,DictCodeList.银行间上行操作类型_本方交谈发送.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_成交中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方回复成交成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方回复成交失败.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue(), DictCodeList.银行间上行操作类型_本方报价撤回.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                StringUtils.EMPTY,
                		Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_成交中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_终止中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方回复拒绝成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方回复拒绝失败.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已终止.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList());
        statusTable.put(DictCodeList.银行间上行报价状态_终止中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价撤回成功.getValue(),
                            DictCodeList.银行间上行操作类型_本方报价撤回失败.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已撤回.getValue(), 
                                    DictCodeList.银行间上行发送状态_成功.getValue(),
                        Arrays.asList());
        statusTable.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                    DictCodeList.银行间上行发送状态_失败.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        statusTable.put(DictCodeList.银行间上行报价状态_已撤销.getValue(), 
                                    StringUtils.EMPTY,
                        Arrays.asList());
        statusTable.put(DictCodeList.银行间上行报价状态_修改中.getValue(), 
                DictCodeList.银行间上行发送状态_待发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方修改发送.getValue()));
        //add by zhangnian 20210114 start
        statusTable.put(DictCodeList.银行间上行报价状态_修改中.getValue(),
                DictCodeList.银行间上行发送状态_失败.getValue(),
                Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        //add by zhangnian 20210114  end
		statusTable.put(DictCodeList.银行间上行报价状态_已修改.getValue(), DictCodeList.银行间上行发送状态_成功.getValue(),
				Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(), DictCodeList.银行间上行操作类型_本方报价撤回.getValue()
                ,DictCodeList.银行间上行操作类型_本方修改发送.getValue())
        );
//		statusTable.put(DictCodeList.银行间上行报价状态_已交谈.getValue(), DictCodeList.银行间上行发送状态_成功.getValue(),
//				Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue()));
		statusTable.put(DictCodeList.银行间上行报价状态_已修改.getValue(), 
                DictCodeList.银行间上行发送状态_已发送.getValue(),
                        Arrays.asList(DictCodeList.银行间上行操作类型_本方修改发送成功.getValue(),
                        		DictCodeList.银行间上行操作类型_本方报价修改.getValue()));
//		statusTable.put(DictCodeList.银行间上行报价状态_已交谈.getValue(), DictCodeList.银行间上行发送状态_成功.getValue(),
//				Arrays.asList(DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue(),
//						DictCodeList.银行间上行操作类型_本方交谈修改.getValue()));
//		statusTable.put(DictCodeList.银行间上行报价状态_已交谈.getValue(), DictCodeList.银行间上行发送状态_成功.getValue(),
//				Arrays.asList(DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue(),
//						DictCodeList.银行间上行操作类型_本方交谈发送.getValue()));
		// add by etliu 20210107 start
		statusTable.put(DictCodeList.银行间上行报价状态_已交谈.getValue(), DictCodeList.银行间上行发送状态_成功.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(), 
                DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue(),
                DictCodeList.银行间上行操作类型_本方交谈修改.getValue(),
                DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue(),
                DictCodeList.银行间上行操作类型_本方交谈发送.getValue(),
                DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue(),
            DictCodeList.银行间上行操作类型_本方报价撤回.getValue()));
		// add by etliu 20210107 end
        //add by zhangnian 20210115 start
        statusTable.put(DictCodeList.银行间上行报价状态_交谈中.getValue(), DictCodeList.银行间上行发送状态_失败.getValue(),
                Arrays.asList(DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        //add by zhangnian 20210115 end
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RepoDialogueQuoteDto getRepoDialogueQuote(String quoteId) {
        RepoDialogueQuoteDto result = new RepoDialogueQuoteDto();
        result.setIbuQuoteCrepDto(ibuQuoteMapper.selectQuoteCrepByKey(quoteId));
        result.setIbuQuoteCrepPledgeDtoList(ibuQuoteMapper.selectQuoteCrepPledgeByKey(quoteId));
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId));
        result.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteMapper.selectQuoteEntCrepPledgeDataByQuoteId(quoteId));
        return result;
    }

    @Override
    public RepoDialogueQuoteDto getWflRepoDialogueQuote(String quoteId) {
        RepoDialogueQuoteDto result = new RepoDialogueQuoteDto();
        result.setIbuQuoteCrepDto(ibuQuoteMapper.selectWflQuoteCrepByKey(quoteId));
        result.setIbuQuoteCrepPledgeDtoList(ibuQuoteMapper.selectWflQuoteCrepPledgeByKey(quoteId));
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectWflQuoteEtrReDataByQuoteId(quoteId));
        result.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteMapper.selectWflQuoteEntCrepPledgeDataByQuoteId(quoteId));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RepoDialogueQuoteDto> getRepoDialogueQuoteHisList(BondQuoteHisSearchDto inputBean) {
        return ibuQuoteMapper.selectRepoDialogueQuoteHisList(inputBean);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RepoDialogueQuoteDto> getRepoDialogueQuoteHisPageList(int pageNum, int pageSize,
                                                                      BondQuoteHisSearchDto inputBean) {
        Page<RepoDialogueQuoteDto> pageData = new Page<>();
        com.github.pagehelper.Page<RepoDialogueQuoteDto> pageList = PageHelper.startPage(pageNum,
            pageSize).doSelectPage(() -> ibuQuoteMapper.selectRepoDialogueQuoteHisList(inputBean));
        pageData = new Page<>(pageList);
        return pageData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidMessageDto validateRequiredItems(RepoDialogueQuoteDto inputBean) {
        return null;
    }

    /**
     * 质押式回购报价进行操作时，需校验目前质押式回购报价的报价状态和发送状态是否允许该项操作。
     */
    @Override
    public ValidMessageDto validateRepoDialogueQuoteStatus(RepoDialogueQuoteDto inputBean, boolean needSearch) {
        if (inputBean == null) return null;
        ValidMessageDto validMessage = new ValidMessageDto();
        IbuQuoteCrepDto ibuQuoteCrepDto = inputBean.getIbuQuoteCrepDto();
        IbuQuoteCrepDto curDto = ibuQuoteCrepDto;
        if (needSearch && ibuQuoteCrepDto != null) {
            curDto = ibuQuoteMapper.selectQuoteCrepByKey(ibuQuoteCrepDto.getQuoteId());
        }
        if (curDto != null) {
            String message = validateStatus(inputBean.getExeTypeIBU(), curDto.getTradeStatus(), curDto.getSendStatus());
            if (message != null) {
                validMessage.setMessage(message);
                validMessage.setQuoteId(ibuQuoteCrepDto.getQuoteId());
                return validMessage;
            }
        }
        // 新增时，默认为新增中状态，不需要验证状态
        return null;
    }

    /**
     * 质押式回购报价进行操作时，需校验目前质押式回购报价的报价状态和发送状态是否允许该项操作。
     */
    @Override
    public List<ValidMessageDto> validateRepoDialogueQuoteStatus(List<RepoDialogueQuoteDto> inputList, boolean needSearch) {
        List<ValidMessageDto> validList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(inputList)) {
            String exeType = StringUtil.EMPTY;
            Optional<String> exeTypeOpt = inputList.stream().map(p -> p.getExeTypeIBU()).findFirst();
            if (exeTypeOpt.isPresent()) {
                exeType = exeTypeOpt.get();
            }

            List<IbuQuoteCrepDto> crepDtoList = inputList.stream().map(
                p -> p.getIbuQuoteCrepDto()).collect(Collectors.toList());
            List<String> quoteIdList = crepDtoList.stream().map(p -> p.getQuoteId()).collect(
                Collectors.toList());
            List<IbuQuoteCrepDto> curDtoList = crepDtoList;
            if (needSearch && CollectionUtils.isNotEmpty(quoteIdList)) {
                curDtoList = ibuQuoteMapper.selectQuoteCrepByKeys(quoteIdList);
            }

            if (CollectionUtils.isNotEmpty(curDtoList)) {
                for (IbuQuoteCrepDto curDto : curDtoList) {
                    String message = validateStatus(exeType, curDto.getTradeStatus(), curDto.getSendStatus());
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
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessageDto> batchValidateRequiredItems(List<RepoDialogueQuoteDto> inputList) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 质押式回购报价状态检验_批量
     */
    @Override
    public List<ValidMessageDto> batchValidateRepoDialogueQuoteStatus(List<RepoDialogueQuoteDto> inputList) {
        return validateRepoDialogueQuoteStatus(inputList, true);
    }

    /**
     * 质押式回购报价信息入库
     */
    @Override
    public QuoteResultDto insertRepoDialogueQuote(RepoDialogueQuoteDto inputBean, 
                                                  boolean isNew) {
        setDefaultStatus(inputBean, isNew);
        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                Long version = inputBean.getIbuQuoteCrepDto().getVersion();
                Long bVersion = inputBean.getIbuQuoteCrepDto().getbVersion();
                if (!isNew) inputBean.setVersion(version, bVersion);
                // 向历史表写入数据
                int affectRows = insertHistoryData(inputBean);
                String quoteId = inputBean.getIbuQuoteCrepDto().getQuoteId();
                if(StringUtils.isNotEmpty(quoteId)){
                	affectRows += wflIbuQuoteCrepMapper.deleteByPrimaryKey(quoteId);
                	affectRows += ibuQuoteMapper.deleteWflPledgeListByKey(quoteId);
                	//多组合
                    affectRows += ibuQuoteMapper.deleteWflIbuQuoteEtrReByQuoteId(quoteId);
                    affectRows += ibuQuoteMapper.deleteWflIbuQuoteEntCrepPledgeByQuoteId(quoteId);
                    //多组合
                }
                //向“在线表”插入数据
                affectRows += ibuQuoteCrepMapper.insert(inputBean.getIbuQuoteCrepDto());
                if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteCrepPledgeDtoList())) {
                    affectRows += ibuQuoteMapper.insertPledgeList(inputBean.getIbuQuoteCrepPledgeDtoList());
                }
                //多组合
                if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                    affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
                }
                if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEntCrepPledgeDtoList())) {
                    affectRows += ibuQuoteMapper.batchInsetIbuQuoteEntCrepPledge(inputBean.getIbuQuoteEntCrepPledgeDtoList());
                }
                //多组合
                return affectRows;
            }
        );
    }
    
    /**
     * 向质押式回购历史表中插入数据
     * @param inputBean BondDialogueQuoteDto
     * @return int
     */
    @Override
    public int insertHistoryData(RepoDialogueQuoteDto inputBean) {
        IbuQuoteCrepHis quoteBondHis = new IbuQuoteCrepHis();
        List<IbuQuoteCrepPledgeHis> quoteBondDetHisList = new ArrayList<IbuQuoteCrepPledgeHis>();
        //银行间报价指令关系
        List<IbuQuoteEtrReHis> ibuQuoteEtrReHisList = new ArrayList<>();
        //回购报价指令质押品
        List<IbuQuoteEntCrepPledgeHis> ibuQuoteEntCrepPledgeHisList = new ArrayList<>();
        int affectRows = 0;
        //add by zhangnian  20210114 start
        if(!CommonUtils.isOperationHis(inputBean)){
            return affectRows;
        }
        //add by zhangnian  20210114 end
        try {
            BeanUtils.copyProperties(quoteBondHis, inputBean.getIbuQuoteCrepDto());
            //新增报价入历史表  状态特殊处理  用于回退
            if (StringUtils.equals(inputBean.getExeTypeIBU(), DictCodeList.银行间上行操作类型_本方新增发送.getValue())
                    && StringUtils.equals(inputBean.getIbuQuoteCrepDto().getTradeStatus(), DictCodeList.银行间上行报价状态_新增中.getValue())
                    && StringUtils.equals(inputBean.getIbuQuoteCrepDto().getSendStatus(), DictCodeList.银行间上行发送状态_已发送.getValue())) {
                quoteBondHis.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                quoteBondHis.setTradeStatus(StringUtils.EMPTY);
            }
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteCrepPledgeDtoList())) {
                List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDts = inputBean.getIbuQuoteCrepPledgeDtoList();
                for (int i = 0; i < ibuQuoteCrepPledgeDts.size(); i++ ) {
                    IbuQuoteCrepPledgeHis ibuQuoteCrepPledgeHis = new IbuQuoteCrepPledgeHis();
                    BeanUtils.copyProperties(ibuQuoteCrepPledgeHis,ibuQuoteCrepPledgeDts.get(i));
                    quoteBondDetHisList.add(ibuQuoteCrepPledgeHis);
                }
            }
            //add by zznn 20210303 start
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                    inputBean.getIbuQuoteEtrReDtoList().stream().forEach(item -> {
                        IbuQuoteEtrReHis ibuQuoteEtrReHis = new IbuQuoteEtrReHis();
                        BeanUtil.copyPropertiesIgnoreNull(ibuQuoteEtrReHis,item);
                        ibuQuoteEtrReHisList.add(ibuQuoteEtrReHis);
                    });
            }
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEntCrepPledgeDtoList())) {
                inputBean.getIbuQuoteEntCrepPledgeDtoList().stream().forEach(item -> {
                    IbuQuoteEntCrepPledgeHis ibuQuoteEntCrepPledgeHis = new IbuQuoteEntCrepPledgeHis();
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteEntCrepPledgeHis,item);
                    ibuQuoteEntCrepPledgeHisList.add(ibuQuoteEntCrepPledgeHis);
                });
            }
            //add by zznn 20210303 end
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        affectRows = ibuQuoteCrepHisMapper.insertSelective(quoteBondHis);
        if(quoteBondDetHisList.size() > 0){
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteCrepPledgeHis(quoteBondDetHisList);
        }
        if(ibuQuoteEtrReHisList.size()>0){
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrReHis(ibuQuoteEtrReHisList);
        }
        if(ibuQuoteEntCrepPledgeHisList.size()>0){
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEntCrepPledgeHis(ibuQuoteEntCrepPledgeHisList);
        }
        return affectRows;
    }
    
    /**
     * 设置默认的交易状态和发送状态（可用于新增）
     * @param inputBean
     * @param isNew boolean 是否新增
     */
    private void setDefaultStatus(RepoDialogueQuoteDto inputBean, boolean isNew) {
        String quoteId = IDGenerator.generate();
		if (inputBean.getWflIbuQuoteCrepDto() != null) {
			inputBean.getWflIbuQuoteCrepDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
		}
        if (inputBean.getIbuQuoteCrepDto() != null) {
            if (StringUtils.isBlank(inputBean.getIbuQuoteCrepDto().getTradeStatus())) {
                inputBean.getIbuQuoteCrepDto().setTradeStatus(StringUtils.EMPTY);
                if(inputBean.getIbuQuoteCrepDto().getExeType().equals(DictCodeList.银行间上行操作类型_本方报价修改.getValue())){
                	inputBean.getIbuQuoteCrepDto().setTradeStatus(DictCodeList.银行间上行报价状态_修改中.getValue());
                }
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteCrepDto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getIbuQuoteCrepDto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getIbuQuoteCrepDto().setSendStatus(
                        DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else {
                    inputBean.getIbuQuoteCrepDto().setSendStatus(StringUtils.EMPTY);
                }
            }
            if (inputBean.getIbuQuoteCrepDto().getVersion() == null) {
                inputBean.getIbuQuoteCrepDto().setVersion(-1L);
            }
            if (inputBean.getIbuQuoteCrepDto().getbVersion() == null) {
                inputBean.getIbuQuoteCrepDto().setbVersion(-1L);
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteCrepDto().getQuoteId())) {
                inputBean.getIbuQuoteCrepDto().setQuoteId(quoteId);
            }
        }
        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteCrepPledgeDtoList())){
            for(IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : inputBean.getIbuQuoteCrepPledgeDtoList()){
                if (ibuQuoteCrepPledgeDto.getVersion() == null) {
                    ibuQuoteCrepPledgeDto.setVersion(inputBean.getIbuQuoteCrepDto().getVersion());
                }
                if (StringUtils.isBlank(ibuQuoteCrepPledgeDto.getQuoteId())) {
                    ibuQuoteCrepPledgeDto.setQuoteId(quoteId);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())){
            inputBean.getIbuQuoteEtrReDtoList().forEach(item -> {
                if (item.getVersion() == null) {
                    item.setVersion(inputBean.getIbuQuoteCrepDto().getVersion());
                }
                if (StringUtils.isBlank(item.getQuoteId())) {
                    item.setQuoteId(quoteId);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEntCrepPledgeDtoList())){
            inputBean.getIbuQuoteEntCrepPledgeDtoList().forEach(item -> {
                if (item.getVersion() == null) {
                    item.setVersion(inputBean.getIbuQuoteCrepDto().getVersion());
                }
                if (StringUtils.isBlank(item.getQuoteId())) {
                    item.setQuoteId(quoteId);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuoteResultBatDto batchInsertRepoDialogueQuote(List<RepoDialogueQuoteDto> inputList) {
        QuoteResultBatDto resultDto = new QuoteResultBatDto();

        inputList.forEach(p -> {
            setDefaultStatus(p, true);
            p.setVersion(-1L, -1L);
        });

        // 向历史表插入数据
        ibuQuoteMapper.batchInsertRepoDialogueQuote(inputList, "_HIS");
        
        // 向“在线表”插入数据
        ibuQuoteMapper.batchInsertRepoDialogueQuote(inputList, "");
        
        List<QuoteResultDto> quoteResultList = new ArrayList<>();
        for (RepoDialogueQuoteDto bdqDto : inputList) {
            QuoteResultDto item = new QuoteResultDto();
            item.setRepoDialogueQuote(bdqDto);
            item.setStatus(true);
            quoteResultList.add(item);
        }

        resultDto.setQuoteResultList(quoteResultList);
        resultDto.setSuccessAccout(inputList.size());
        resultDto.setFailAccout(0);
        return resultDto;
    }

    /**
     * 根据指定条件将更待更新信息更新入库
     */
    @Override
    public QuoteResultDto updateRepoDialogueQuoteByKey(RepoDialogueQuoteDto inputBean) {
        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                String quoteId = inputBean.getIbuQuoteCrepDto().getQuoteId();

                // 向“在线表”更新数据（含前置版本号）
                int affectRows = ibuQuoteCrepMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteCrepDto());

                /*if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteCrepPledgeDtoList())) {
                    ibuQuoteMapper.deletePledgeByQuoteId(quoteId);
                    affectRows += ibuQuoteMapper.insertPledgeList(inputBean.getIbuQuoteCrepPledgeDtoList());
                }*/

                // 删除历史表Version=-1的数据
                //affectRows += ibuQuoteMapper.deleteCrepPledgeHisByQuoteId(quoteId);
                //affectRows += deleteQuoteRepoHisData(quoteId);

                // 再向历史表插入数据 Version=-1
                //inputBean.setVersion(-1L, inputBean.getIbuQuoteCrepDto().getVersion());
                //affectRows += insertHistoryData(inputBean);

                return affectRows;
            }
        );
    }

    /**
     * 根据指定条件将更待更新信息更新入库
     */
    @Override
    public QuoteResultDto updateRepoDialogueQuoteByKeyDeal(RepoDialogueQuoteDto inputBean) {
        String quoteId = inputBean.getIbuQuoteCrepDto().getQuoteId();
        ibuQuoteMapper.deleteQuoteEtrReByQuoteId(quoteId);
        ibuQuoteMapper.deleteQuoteEntPledgeByQuoteId(quoteId);
        return excuteAndReturnQuoteResult(inputBean, null,
                x -> {
                    // 向历史表写入数据
                    int affectRows = insertHistoryData(inputBean);
                    if(StringUtils.isNotEmpty(quoteId)){
                        affectRows += wflIbuQuoteCrepMapper.deleteByPrimaryKey(quoteId);
                        affectRows += ibuQuoteMapper.deleteWflPledgeListByKey(quoteId);
                        //多组合
                        affectRows += ibuQuoteMapper.deleteWflIbuQuoteEtrReByQuoteId(quoteId);
                        affectRows += ibuQuoteMapper.deleteWflIbuQuoteEntCrepPledgeByQuoteId(quoteId);
                        //多组合
                    }
                    // 向“在线表”更新数据（含前置版本号）
                    affectRows += ibuQuoteCrepMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteCrepDto());
                    //多组合
                    if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                        affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
                    }
                    if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEntCrepPledgeDtoList())) {
                        affectRows += ibuQuoteMapper.batchInsetIbuQuoteEntCrepPledge(inputBean.getIbuQuoteEntCrepPledgeDtoList());
                    }

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
/*    private int deleteQuoteRepoHisData(String quoteId) {
        IbuQuoteCrepHisKey quoteHisKey = new IbuQuoteCrepHisKey();
        quoteHisKey.setQuoteId(quoteId);
        quoteHisKey.setVersion(-1L);
        int affectRows = ibuQuoteCrepHisMapper.deleteByPrimaryKey(quoteHisKey);
        return affectRows;
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public QuoteResultDto deleteRepoDialogueQuoteByKey(String quoteId, boolean deleteHis) {
        return excuteAndReturnQuoteResult(null, quoteId,
            x -> {
                // 质押品数据删除
                int affectRows = ibuQuoteMapper.deletePledgeByQuoteId(x);
                
                if (deleteHis) {
                    // 删除历史表Version=-1的数据
                    affectRows += ibuQuoteMapper.deleteCrepPledgeHisByQuoteId(x);
                }
                //多组合改造
                //add by zhangnian 20210303 start
                affectRows += ibuQuoteMapper.deleteQuoteEtrReByQuoteId(x);
                if(deleteHis){
                    affectRows += ibuQuoteMapper.deleteQuoteEtrReHisByQuoteId(x);
                }
                affectRows += ibuQuoteMapper.deleteQuoteEntPledgeByQuoteId(x);
                if(deleteHis){
                    affectRows += ibuQuoteMapper.deleteQuoteEntPledgeHisByQuoteId(x);
                }
                //add by zhangnian 20210303 end
                //质押式回购报价数据删除
                affectRows += ibuQuoteCrepMapper.deleteByPrimaryKey(x);
                if (deleteHis) {
                    affectRows +=  ibuQuoteMapper.deleteCrepHisByQuoteId(x);
                }
                return affectRows;
            }
        );
    }

    /**
     * 质押式回购报价状态更新
     */
    @Override
    public void updateRepoDialogueQuoteStatus(RepoDialogueQuoteDto inputBean) {
        if (inputBean == null) return;
        // modify by etliu 20210106 start
//        Map<String, String> statusMap = CommonUtils.getBondDialogueQuoteStatus(inputBean.getExeTypeIBU());
        Map<String, String> statusMap = CommonUtils.getRepoDialogueQuoteStatus(inputBean.getExeTypeIBU());
        // modify by etliu 20210106 end
        String tradeStatus = statusMap.getOrDefault(IbuDictCode.银行间上行报价状态.getValue(),
            DictCodeList.银行间上行报价状态_新增中.getValue());
        String sendStatus = statusMap.getOrDefault(IbuDictCode.银行间上行发送状态.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue());
        if (inputBean.getIbuQuoteCrepDto() != null) {
            if(!DictCodeList.银行间上行操作类型_撤销.getValue().equals(inputBean.getExeTypeIBU())){
                inputBean.getIbuQuoteCrepDto().setSendStatus(sendStatus);
            }
            inputBean.getIbuQuoteCrepDto().setTradeStatus(tradeStatus);
        }
    }

    /**
     * 质押式回购报价接口更新
     */
    @Override
    public void updateRepoDialogueQuoteInterf(RepoDialogueQuoteDto inputBean) {
        updateRepoDialogueQuoteStatus(inputBean);
        // 更新质押式回购报价信息中的发送状态和交易状态
        ibuQuoteCrepMapper.updateByPrimaryKey(inputBean.getIbuQuoteCrepDto());
    }
    
    /**
     * 执行方法并返回“处理结果”对象
     * @param inputBean BondDialogueQuoteDto
     * @param quoteId String
     * @param func Function<String, Integer>
     * @return QuoteResultDto
     */
    private QuoteResultDto excuteAndReturnQuoteResult(RepoDialogueQuoteDto inputBean,
                                                      String quoteId,
                                                      Function<String, Integer> func) {
        QuoteResultDto result = new QuoteResultDto();

        if (StringUtils.isNotBlank(quoteId)) {
            IbuQuoteCrepDto ibuQuoteCrepDto   = ibuQuoteMapper.selectQuoteCrepByKey(quoteId);
            List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = ibuQuoteMapper.selectQuoteCrepPledgeByKey(quoteId);
            //多组合改造
            List<IbuQuoteEtrReDto>  ibuQuoteEtrReList = ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId);
            List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledgeList = ibuQuoteMapper.selectQuoteEntCrepPledgeDataByQuoteId(quoteId);
            inputBean = new RepoDialogueQuoteDto(ibuQuoteCrepDto, ibuQuoteCrepPledgeDtoList,ibuQuoteEtrReList,ibuQuoteEntCrepPledgeList);
            //多组合改造
        }

        int affectRows = func.apply(quoteId);
        log.info("affectRows is {}", affectRows);

        result.setRepoDialogueQuote(inputBean);
        result.setStatus(true);

        return result;
    }
    
    /**
     * 验证状态变更操作是否合理
     * @param curTradeStatus String
     * @param curSendStatus String
     * @return String 验证消息
     */
    protected String validateStatus(String exeTypeIBU, String curTradeStatus,
                                       String curSendStatus){
        String msg = null;

        List<String> allowExeTypeList = RepoDialogueQuoteSubServiceImpl.statusTable.get(
            StringUtil.nullToEmpty(curTradeStatus), StringUtil.nullToEmpty(curSendStatus));
        if (allowExeTypeList == null) {
            msg = getMessage("BC_IBUPLINK_00004",
                StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(
                    IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(
                    IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName());
        }
        else if (!allowExeTypeList.contains(exeTypeIBU)) {
            msg = getMessage("BC_IBUPLINK_00003",
                StringUtils.isBlank(curTradeStatus) ? StringUtil.EMPTY : DictCodeList.from(
                    IbuDictCode.银行间上行报价状态, curTradeStatus).getCodeName(),
                StringUtils.isBlank(curSendStatus) ? StringUtil.EMPTY : DictCodeList.from(
                    IbuDictCode.银行间上行发送状态, curSendStatus).getCodeName(),
                StringUtils.isBlank(exeTypeIBU) ? StringUtil.EMPTY : DictCodeList.from(
                    IbuDictCode.银行间上行操作类型, exeTypeIBU).getCodeName());
        }
        return msg;
    }
    
    @Override
    public QuoteResultDto restoreRepoDialogueQuoteByKey(RepoDialogueQuoteDto inputBean){
        QuoteResultDto result = new QuoteResultDto(true);

        IbuQuoteCrepDto ibuQuoteCrepDto = inputBean.getIbuQuoteCrepDto();
        List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = inputBean.getIbuQuoteCrepPledgeDtoList();
        String quoteId = ibuQuoteCrepDto.getQuoteId();
        Long bVersion = ibuQuoteCrepDto.getbVersion();
        
        if (bVersion != null) {
            // 历史质押式回购报价查询
            List<RepoDialogueQuoteDto> bdqList = ibuQuoteMapper.selectRepoDialogueQuoteHisList(
                new BondQuoteHisSearchDto(quoteId, bVersion));

            if (CollectionUtils.isNotEmpty(bdqList)) {
                // "在线表"删除当前版本的记录
                if (ibuQuoteCrepPledgeDtoList != null) {
                    List<IbuQuoteCrepPledgeKey> keyList = new ArrayList<>();
                    for (IbuQuoteCrepPledge ibuQuoteCrepPledge : ibuQuoteCrepPledgeDtoList) {
                        IbuQuoteCrepPledgeKey ibuQuoteCrepPledgeKey = new IbuQuoteCrepPledgeKey();
                        try {
                            BeanUtils.copyProperties(ibuQuoteCrepPledgeKey, ibuQuoteCrepPledge);
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                        keyList.add(ibuQuoteCrepPledgeKey);
                    }
                    
                    if (CollectionUtils.isNotEmpty(keyList)) {
                        ibuQuoteMapper.deleteQuoteCrepPledges(keyList);
                    }
                }
                ibuQuoteMapper.deleteQuoteEtrReByQuoteId(quoteId);
                ibuQuoteMapper.deleteQuoteEntPledgeByQuoteId(quoteId);
                ibuQuoteCrepMapper.deleteByPrimaryKey(quoteId);

                // 再将历史数据插入到“在线表”中，这里循环其实就走一次，因为只有一条数据
                for (RepoDialogueQuoteDto hisDto : bdqList) {
                    // add by曹国 新增发送失败回退的特殊处理
                    if (DictCodeList.银行间上行报价状态_新增中.getValue().equals(
                        hisDto.getIbuQuoteCrepDto().getTradeStatus())
                        && DictCodeList.银行间上行发送状态_已发送.getValue().equals(
                            hisDto.getIbuQuoteCrepDto().getSendStatus())) {
                        hisDto.getIbuQuoteCrepDto().setTradeStatus(StringUtils.EMPTY);
                        hisDto.getIbuQuoteCrepDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                    }
                    // add end
                    hisDto.setExeTypeIBU(inputBean.getExeTypeIBU());
                    ibuQuoteCrepMapper.insert(hisDto.getIbuQuoteCrepDto());
                    if (CollectionUtils.isNotEmpty(hisDto.getIbuQuoteCrepPledgeDtoList())) {
                        ibuQuoteMapper.insertPledgeList(hisDto.getIbuQuoteCrepPledgeDtoList());
                        inputBean.setIbuQuoteCrepPledgeDtoList(hisDto.getIbuQuoteCrepPledgeDtoList());
                    }
                    //多组合改造
                    if (CollectionUtils.isNotEmpty(hisDto.getIbuQuoteEtrReDtoList())) {
                        ibuQuoteMapper.batchInsetIbuQuoteEtrRe(hisDto.getIbuQuoteEtrReDtoList());
                        inputBean.setIbuQuoteEtrReDtoList(hisDto.getIbuQuoteEtrReDtoList());
                    }
                    if (CollectionUtils.isNotEmpty(hisDto.getIbuQuoteEntCrepPledgeDtoList())) {
                        ibuQuoteMapper.batchInsetIbuQuoteEntCrepPledge(hisDto.getIbuQuoteEntCrepPledgeDtoList());
                        inputBean.setIbuQuoteEntCrepPledgeDtoList(hisDto.getIbuQuoteEntCrepPledgeDtoList());
                    }
                    //多组合改造
                    inputBean.setIbuQuoteCrepDto(hisDto.getIbuQuoteCrepDto());
                }
            }
            else {
                result.setErrCode("BC_IBUPLINK_00005");
                result.setErrMsg(getMessage("BC_IBUPLINK_00005", quoteId, ibuQuoteCrepDto.getVersion(), bVersion));
                result.setStatus(false);
            }
        }
        else {
            result.setErrCode("BC_IBUPLINK_00006");
            result.setErrMsg(getMessage("BC_IBUPLINK_00006", quoteId, ibuQuoteCrepDto.getVersion()));
            result.setStatus(false);
        }

        result.setRepoDialogueQuote(inputBean);

        return result;
    }

    @Override
    public QuoteResultDto updateRepoDialogueQuoteVersion(GateWayReturnMsgDto inputBean) {
        QuoteResultDto result = new QuoteResultDto(true);
        RepoDialogueQuoteDto repoDialogueQuoteDto = new RepoDialogueQuoteDto();

        // 查询数据库中的现存数据
        IbuQuoteCrepDto oldIbuQuoteCrepDto = ibuQuoteMapper.selectQuoteCrepByKey(inputBean.getQuoteId());

        if (oldIbuQuoteCrepDto != null) {
            // 主表版本号要小于当前传过来的版本号
            //if (oldIbuQuoteCrepDto.getVersion() < inputBean.getVersion()) {
                // 用最新的信息更新“在线表”
                IbuQuoteCrep ibuQuoteCrep = new IbuQuoteCrep();
                ibuQuoteCrep.setQuoteId(inputBean.getQuoteId());

                if (inputBean.isStatus()) {
                    if (inputBean.getVersion() != null) {
                        // 用最新的信息更新“在线表”
                        ibuQuoteCrep.setVersion(inputBean.getVersion());
                        // 设置前置版本
                        if(oldIbuQuoteCrepDto.getbVersion() != null && oldIbuQuoteCrepDto.getbVersion() == -1L){
                            ibuQuoteCrep.setbVersion(inputBean.getVersion());
                        }
                    }
                    ibuQuoteCrep.setpQuoteId(inputBean.getpQuoteId());
                }
                else {
                    // 修改报价发送状态
//                    ibuQuoteCrep.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());
                    ibuQuoteCrep.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                }

                ibuQuoteCrepMapper.updateByPrimaryKeySelective(ibuQuoteCrep);

                // 历史表先按Version=-1条件删除
                //deleteQuoteRepoHisData(inputBean.getQuoteId());

                IbuQuoteCrepHis ibuQuoteCrepHis = new IbuQuoteCrepHis();
                try {
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepHis, oldIbuQuoteCrepDto);
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepHis, ibuQuoteCrep);
                    // 拦截器一定要设置userInfo中的信息及最新的操作时间
                }
                catch (BeansException e1) {
                    log.error(e1.getMessage());
                    result.setErrCode("BeansException");
                    result.setErrMsg(e1.getMessage());
                    result.setStatus(false);
                }
                // 向历史表中插入新版本号的数据
                //ibuQuoteCrepHisMapper.insertSelective(ibuQuoteCrepHis);
                repoDialogueQuoteDto.setIbuQuoteRepoDtoByHis(ibuQuoteCrepHis);

                // 更新明细
                IbuQuoteCrepPledge ibuQuoteCrepPledge = new IbuQuoteCrepPledge();
                ibuQuoteCrepPledge.setQuoteId(inputBean.getQuoteId());

                if (inputBean.isStatus()) {
                    if (inputBean.getVersion() != null) {
                        ibuQuoteCrepPledge.setVersion(inputBean.getVersion());
                        ibuQuoteMapper.updateCrepVersionByQuoteId(ibuQuoteCrepPledge);
                    }
                }

                // 历史明细表先按Version=-1条件删除
                //ibuQuoteMapper.deleteCrepPledgeHisByQuoteId(inputBean.getQuoteId());

                // 查询最新的质押品信息
                List<IbuQuoteCrepPledge> newCrepPledgeList = 
                    ibuQuoteMapper.selectPledgeByQuoteId(inputBean.getQuoteId());

                // 再将最新的数据插入历史表
                List<IbuQuoteCrepPledgeHis> ibuPledgeHisList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(newCrepPledgeList)) {
                    for (IbuQuoteCrepPledge newIbuQuoteCrepPledge : newCrepPledgeList) {
                        IbuQuoteCrepPledgeHis ibuQuoteCrepPledgeHis = new IbuQuoteCrepPledgeHis();
                        try {
                            BeanUtils.copyProperties(ibuQuoteCrepPledgeHis, newIbuQuoteCrepPledge);
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
                        }
                        // 向历史明细表中插入新版本号的数据
                        //ibuQuoteCrepPledgeHisMapper.insertSelective(ibuQuoteCrepPledgeHis);
                        ibuPledgeHisList.add(ibuQuoteCrepPledgeHis);
                    }
                }

                repoDialogueQuoteDto.setIbuQuoteCrepPledgeDtoByHis(ibuPledgeHisList);
                
                // add by etliu 20210316 银行间上下行多组合 start
                // 查询银行间报价指令关系业务表数据
                List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(
                    inputBean.getQuoteId());
                if (!StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                    repoDialogueQuoteDto.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
                }
                // add by etliu 20210316 银行间上下行多组合 end
                
                result.setRepoDialogueQuote(repoDialogueQuoteDto);
/*            }
            else {
                result.setErrCode("BC_IBUPLINK_00009");
                result.setErrMsg(getMessage("BC_IBUPLINK_00009", inputBean.getVersion(),
                    inputBean.getQuoteId(), oldIbuQuoteCrepDto.getVersion()));
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
     * 质押式回购根据外部报价编号查询报价编号
     * @param pquoteId String
     * @return String 报价编号
     */
    @Override
    public String getRepoQuoteIdByPquoteId(String pquoteId){
        return ibuQuoteMapper.getRepoQuoteIdByPquoteId(pquoteId);
    }

	@Override
	public QuoteResultDto insertWflRepoDialogueQuote(RepoDialogueQuoteDto inputBean, boolean isNew) {
        setDefaultStatus(inputBean, isNew);
        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                Long version = inputBean.getWflIbuQuoteCrepDto().getVersion();
                Long bVersion = inputBean.getWflIbuQuoteCrepDto().getbVersion();
                if (!isNew) inputBean.setVersion(version, bVersion);
                int affectRows = 0;
                //向“流程表”插入数据
                affectRows += wflIbuQuoteCrepMapper.insert(inputBean.getWflIbuQuoteCrepDto());
                if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteCrepPledgeDtoList())) {
                    affectRows += ibuQuoteMapper.insertWflPledgeList(inputBean.getWflIbuQuoteCrepPledgeDtoList());
                }
                //多组合
                if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteEtrReDtoList())) {
                    affectRows += ibuQuoteMapper.batchInsetWflIbuQuoteEtrRe(inputBean.getWflIbuQuoteEtrReDtoList());
                }
                if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteEntCrepPledgeDtoList())) {
                    affectRows += ibuQuoteMapper.batchInsetWflIbuQuoteEntCrepPledge(inputBean.getWflIbuQuoteEntCrepPledgeDtoList());
                }
                //多组合
                return affectRows;
            }
        );
	}

	@Override
	public QuoteResultDto deleteWflRepoDialogueQuoteByKey(String quoteId) {
		return excuteAndReturnQuoteResult(null, quoteId,
	            x -> {
	                // 质押品流程数据删除
	                int affectRows = ibuQuoteMapper.deleteWflPledgeListByKey(x);
	                //多组合
                    affectRows += ibuQuoteMapper.deleteWflIbuQuoteEtrReByQuoteId(x);
                    affectRows += ibuQuoteMapper.deleteWflIbuQuoteEntCrepPledgeByQuoteId(x);
                    //多组合
	                // 报价主信息流程数据删除
	                affectRows += wflIbuQuoteCrepMapper.deleteByPrimaryKey(x);
	                
	                return affectRows;
	            }
	        );
	}
}
