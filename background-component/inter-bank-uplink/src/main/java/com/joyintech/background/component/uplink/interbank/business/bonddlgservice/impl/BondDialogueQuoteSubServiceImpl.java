/*
 * 文件名：BondDialogueQuoteSubServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.business.bonddlgservice.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joyintech.background.component.common.pack.common.base.BaseService;
import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.IDGenerator;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.uplink.interbank.business.bonddlgservice.BondDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.dto.local.BondQuoteHisSearchDto;
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

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 现券对话报价服务实现类
 * @author Jiaoxujin
 * @version 1.0 2020年5月7日
 * @see BondDialogueQuoteSubServiceImpl
 * @since 1.0
 */
@Service
class BondDialogueQuoteSubServiceImpl extends BaseService<IbuQuoteBond> implements BondDialogueQuoteSubService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IbuQuoteBondMapper quoteBondMapper;
    
    @Autowired
    private IbuQuoteBondDetMapper quoteBondDetMapper;
    
    @Autowired
    private IbuQuoteBondHisMapper quoteBondHisMapper;
    
    @Autowired
    private IbuQuoteBondDetHisMapper quoteBondDetHisMapper;

    @Autowired
    private IbuQuoteMapper ibuQuoteMapper;
    
    @Autowired
    WflIbuQuoteBond2Mapper wflIbuQuoteBond2Mapper;
    
    @Autowired
    WflIbuQuoteBondDetMapper wflIbuQuoteBondDetMapper;

    @Autowired
    private IbuQuoteEtrReMapper ibuQuoteEtrReMapper;
    
    /** Key为交易状态，发送状态；Value为允许的操作类型：List<银行间上行操作类型代码> */
    private static Table<String, String, List<String>> statusTable = HashBasedTable.create();
    
    static {
        statusTable.put(StringUtils.EMPTY, 
                                DictCodeList.银行间上行发送状态_待发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                                DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价新增.getValue()));
        
        // add by etliu 20210125 start
        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                DictCodeList.银行间上行操作类型_本方报价新增.getValue()));
        // add by etliu 20210125 end

        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                DictCodeList.银行间上行发送状态_已发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方新增发送成功.getValue(),
                                DictCodeList.银行间上行操作类型_本方新增发送失败.getValue()));

        statusTable.put(DictCodeList.银行间上行报价状态_已新增.getValue(), 
                                DictCodeList.银行间上行发送状态_成功.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                                DictCodeList.银行间上行操作类型_本方修改发送.getValue()));

        statusTable.put(DictCodeList.银行间上行报价状态_新增中.getValue(), 
                                DictCodeList.银行间上行发送状态_失败.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价回退.getValue()));

        statusTable.put(DictCodeList.银行间上行报价状态_修改中.getValue(), 
                                DictCodeList.银行间上行发送状态_已发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方修改发送成功.getValue(),
                                DictCodeList.银行间上行操作类型_本方修改发送失败.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_修改中.getValue(), 
                                DictCodeList.银行间上行发送状态_失败.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_已修改.getValue(), 
                                DictCodeList.银行间上行发送状态_成功.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                                DictCodeList.银行间上行操作类型_本方修改发送.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                DictCodeList.银行间上行发送状态_已发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价撤回成功.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价撤回失败.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_撤回中.getValue(), 
                                DictCodeList.银行间上行发送状态_失败.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                                DictCodeList.银行间上行操作类型_本方报价回退.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_已撤回.getValue(), 
                                DictCodeList.银行间上行发送状态_成功.getValue(),
            Arrays.asList());
        
        statusTable.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                                StringUtils.EMPTY,
            Arrays.asList(DictCodeList.银行间上行操作类型_撤销.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_已终止.getValue(), 
                                StringUtils.EMPTY,
            Arrays.asList());
        
        statusTable.put(DictCodeList.银行间上行报价状态_待回复.getValue(), 
                                StringUtils.EMPTY,
            Arrays.asList(DictCodeList.银行间上行操作类型_本方回复成交.getValue(),
                                DictCodeList.银行间上行操作类型_本方回复拒绝.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_已撤回.getValue(), 
                                StringUtils.EMPTY,
            Arrays.asList());
        
        statusTable.put(DictCodeList.银行间上行报价状态_成交中.getValue(), 
                                DictCodeList.银行间上行发送状态_已发送.getValue(),
            Arrays.asList(DictCodeList.银行间上行操作类型_本方回复成交成功.getValue(),
                                DictCodeList.银行间上行操作类型_本方回复成交失败.getValue()));
        
        statusTable.put(DictCodeList.银行间上行报价状态_已成交.getValue(), 
                                DictCodeList.银行间上行发送状态_成功.getValue(),
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
        
        statusTable.put(DictCodeList.银行间上行报价状态_已撤销.getValue(), 
                                StringUtils.EMPTY,
            Arrays.asList());
    }

    @PostConstruct
    public void beforeInit() {
        log.debug("SubBondDialogueQuoteServiceImpl Constructed.");
    }

    @Override
    public BondDialogueQuoteDto getBondDialogueQuote(String quoteId) {
        BondDialogueQuoteDto result = new BondDialogueQuoteDto();

        result.setIbuQuoteBondDto(ibuQuoteMapper.selectQuoteBondByKey(quoteId));
        // edit by etliu 20210114 start
//        result.setIbuQuoteBondDetDto(ibuQuoteMapper.selectByQuoteId(quoteId));
        List<IbuQuoteBondDetDto> tempList = ibuQuoteMapper.selectByQuoteListId(quoteId);
        if (!StringUtil.isEmptyList(tempList)) {
            result.setIbuQuoteBondDetDto(tempList.get(0));
        }
        // edit by etliu 20210114 end

        // add by etliu 20210309 银行间上下行多组合 start
        // 银行间报价指令关系业务表
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId));
        // add by etliu 20210309 银行间上下行多组合 end

        return result;
    }
    
    @Override
    public BondDialogueQuoteDto getWflBondDialogueQuote(String quoteId) {
        BondDialogueQuoteDto result = new BondDialogueQuoteDto();

        result.setIbuQuoteBondDto(wflIbuQuoteBond2Mapper.selectQuoteBondByKey(quoteId));
        result.setIbuQuoteBondDetDto(wflIbuQuoteBond2Mapper.selectByQuoteId(quoteId));
        
        // add by etliu 20210309 银行间上下行多组合 start
        // 银行间报价指令关系业务表
        result.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectWflQuoteEtrReDataByQuoteId(quoteId));
        // add by etliu 20210309 银行间上下行多组合 end

        return result;
    }

    @Override
    public ValidMessageDto validateRequiredItems(BondDialogueQuoteDto inputBean) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 对话报价进行操作时，需校验目前对话报价的报价状态和发送状态是否允许该项操作。
     */
    @Override
    public ValidMessageDto validateBondDialogueQuoteStatus(
                                                           BondDialogueQuoteDto inputBean, boolean needSearch) {
        if (inputBean == null) return null;
        ValidMessageDto validMessage = new ValidMessageDto();

        IbuQuoteBondDto ibuQuoteBond = inputBean.getIbuQuoteBondDto();
        IbuQuoteBondDto curDto = ibuQuoteBond;
        if (needSearch && ibuQuoteBond != null) {
            curDto = ibuQuoteMapper.selectQuoteBondByKey(ibuQuoteBond.getQuoteId());
        }

        if (curDto != null) {
            String message = validateStatus(inputBean.getExeTypeIBU(), curDto.getTradeStatus(), curDto.getSendStatus());
            if (message != null) {
                validMessage.setMessage(message);
                validMessage.setQuoteId(ibuQuoteBond.getQuoteId());
                return validMessage;
            }
        }
        // 新增时，默认为新增中状态，不需要验证状态
        return null;
    }

    @Override
    public List<ValidMessageDto> validateBondDialogueQuoteStatus(List<BondDialogueQuoteDto> inputList, boolean needSearch) {
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

    @Override
    public List<ValidMessageDto> batchValidateRequiredItems(List<BondDialogueQuoteDto> inputList) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ValidMessageDto> batchValidateBondDialogueQuoteStatus(List<BondDialogueQuoteDto> inputList) {
        return validateBondDialogueQuoteStatus(inputList, true);
    }

    @Override
	public QuoteResultDto insertBondDialogueQuote(BondDialogueQuoteDto inputBean, boolean isNew) {
		setDefaultStatus(inputBean, isNew);
		return excuteAndReturnQuoteResult(inputBean, null, x -> {
			Long version = inputBean.getIbuQuoteBondDto().getVersion();
			Long bVersion = inputBean.getIbuQuoteBondDto().getbVersion();
			if (!isNew)
				inputBean.setVersion(version, bVersion);
			int affectRows = 0;
			// 向历史表写入数据
/*			if (DictCodeList.银行间上行操作类型_本方新增发送.getValue().equals(inputBean.getExeTypeIBU())) {*/
				affectRows = insertHistoryData(inputBean);
			/*}*/
			// 向“在线表”插入数据
			affectRows += quoteBondMapper.insertSelective(inputBean.getIbuQuoteBondDto());
			affectRows += quoteBondDetMapper.insertSelective(inputBean.getIbuQuoteBondDetDto());
			//多组合
	        if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
	            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
	        }
			return affectRows;
		});
	}
    
    @Override
	public QuoteResultDto updateBondDialogueQuote(BondDialogueQuoteDto inputBean, boolean isNew) {
		setDefaultStatus(inputBean, isNew);
		return excuteAndReturnQuoteResult(inputBean, null, x -> {
			Long version = inputBean.getIbuQuoteBondDto().getVersion();
			Long bVersion = inputBean.getIbuQuoteBondDto().getbVersion();
			if (!isNew)
				inputBean.setVersion(version, bVersion);
			int affectRows = 0;
			affectRows += quoteBondMapper.insertSelective(inputBean.getIbuQuoteBondDto());
			affectRows += quoteBondDetMapper.insertSelective(inputBean.getIbuQuoteBondDetDto());
			
			// add by etliu 20210316 银行间上下行多组合 start
            if (CollectionUtils.isNotEmpty(inputBean.getIbuQuoteEtrReDtoList())) {
                affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrRe(inputBean.getIbuQuoteEtrReDtoList());
            }
            // add by etliu 20210316 银行间上下行多组合 end
			
			return affectRows;
		});
	}
    
    @Override
    public QuoteResultDto insertWflBondDialogueQuote(BondDialogueQuoteDto inputBean, 
                                                  boolean isNew) {
    	setWflDefaultStatus(inputBean, isNew);
		Long version = inputBean.getWflIbuQuoteBond2Dto().getVersion();
		Long bVersion = inputBean.getWflIbuQuoteBond2Dto().getbVersion();
		if (!isNew){
			inputBean.setVersion(version, bVersion);
		}
		int affectRows = 0;
		// 向“在线表”插入数据
		affectRows += wflIbuQuoteBondDetMapper.insertSelective(inputBean.getWflIbuQuoteBondDetDto());
		affectRows += wflIbuQuoteBond2Mapper.insertSelective(inputBean.getWflIbuQuoteBond2Dto());
		//多组合
        if (CollectionUtils.isNotEmpty(inputBean.getWflIbuQuoteEtrReDtoList())) {
            affectRows += ibuQuoteMapper.batchInsetWflIbuQuoteEtrRe(inputBean.getWflIbuQuoteEtrReDtoList());
        }
		log.info("affectRows is {}", affectRows);
		QuoteResultDto result = new QuoteResultDto();
        result.setBondDialogueQuote(inputBean);
        result.setStatus(true);
        return result;
    }
    
    /**
     * 设置默认的交易状态和发送状态（可用于流程中）
     * @param inputBean BondDialogueQuoteDto
     * @param isNew boolean 是否新增
     */
    private void setWflDefaultStatus(BondDialogueQuoteDto inputBean, boolean isNew) {
        String quoteId = IDGenerator.generate();
        if (inputBean.getWflIbuQuoteBond2Dto() != null) {
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBond2Dto().getTradeStatus())) {
                inputBean.getWflIbuQuoteBond2Dto().setTradeStatus(StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBond2Dto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getWflIbuQuoteBond2Dto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getWflIbuQuoteBond2Dto().setSendStatus(
                        DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else {
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
            if (inputBean.getWflIbuQuoteBond2Dto().getWorkflowStatus() == null) {
            	inputBean.getWflIbuQuoteBond2Dto().setWorkflowStatus(DictCodeList.银行间上行数据状态_流程中.getValue());
            }
        }
        if (inputBean.getWflIbuQuoteBondDetDto() != null) {
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBondDetDto().getTradeStatus())) {
                inputBean.getWflIbuQuoteBondDetDto().setTradeStatus(StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBondDetDto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getWflIbuQuoteBondDetDto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getWflIbuQuoteBondDetDto().setSendStatus(
                        DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else {
                    inputBean.getWflIbuQuoteBondDetDto().setSendStatus(StringUtils.EMPTY);
                }
            }
            if (inputBean.getWflIbuQuoteBondDetDto().getVersion() == null) {
                inputBean.getWflIbuQuoteBondDetDto().setVersion(inputBean.getWflIbuQuoteBondDetDto().getVersion());
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBondDetDto().getQuoteId())) {
                inputBean.getWflIbuQuoteBondDetDto().setQuoteId(quoteId);
            }
            if (StringUtils.isBlank(inputBean.getWflIbuQuoteBondDetDto().getDetId())) {
                inputBean.getWflIbuQuoteBondDetDto().setDetId(IDGenerator.generate());
            }
            if (inputBean.getWflIbuQuoteBondDetDto().getWorkflowStatus() == null) {
            	inputBean.getWflIbuQuoteBondDetDto().setWorkflowStatus(DictCodeList.银行间上行数据状态_流程中.getValue());
            }
            
        }
    }

    /**
     * 设置默认的交易状态和发送状态（可用于新增）
     * @param inputBean BondDialogueQuoteDto
     * @param isNew boolean 是否新增
     */
    private void setDefaultStatus(BondDialogueQuoteDto inputBean, boolean isNew) {
        String quoteId = IDGenerator.generate();
        if (inputBean.getIbuQuoteBondDto() != null) {
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDto().getTradeStatus())) {
                inputBean.getIbuQuoteBondDto().setTradeStatus(StringUtils.EMPTY);
                if(DictCodeList.银行间上行操作类型_本方报价修改.getValue().equals(
                    inputBean.getExeTypeIBU())){
                	 inputBean.getIbuQuoteBondDto().setTradeStatus(DictCodeList.银行间上行报价状态_修改中.getValue());
                }
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getIbuQuoteBondDto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getIbuQuoteBondDto().setSendStatus(
                        DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else {
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
        if (inputBean.getIbuQuoteBondDetDto() != null) {
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDetDto().getTradeStatus())) {
                inputBean.getIbuQuoteBondDetDto().setTradeStatus(StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDetDto().getSendStatus())) {
                if (DictCodeList.银行间上行操作类型_对手方报价新增.getValue().equals(
                    inputBean.getExeTypeIBU())) {
                    // 已收到对手方的消息了，不需要再发送
                    inputBean.getIbuQuoteBondDetDto().setSendStatus(StringUtils.EMPTY);
                }
                else if (isNew) {
                    inputBean.getIbuQuoteBondDetDto().setSendStatus(
                        DictCodeList.银行间上行发送状态_待发送.getValue());
                }
                else {
                    inputBean.getIbuQuoteBondDetDto().setSendStatus(StringUtils.EMPTY);
                }
            }
            if (inputBean.getIbuQuoteBondDetDto().getVersion() == null) {
                inputBean.getIbuQuoteBondDetDto().setVersion(inputBean.getIbuQuoteBondDto().getVersion());
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDetDto().getQuoteId())) {
                inputBean.getIbuQuoteBondDetDto().setQuoteId(quoteId);
            }
            if (StringUtils.isBlank(inputBean.getIbuQuoteBondDetDto().getDetId())) {
                inputBean.getIbuQuoteBondDetDto().setDetId(IDGenerator.generate());
            }
        }
    }

    @Override
    public QuoteResultDto restoreBondDialogueQuoteByKey(BondDialogueQuoteDto inputBean) {
        QuoteResultDto result = new QuoteResultDto(true);
        IbuQuoteBond quoteBond = inputBean.getIbuQuoteBondDto();
        IbuQuoteBondDet quoteBondDet = inputBean.getIbuQuoteBondDetDto();
        String quoteId = quoteBond.getQuoteId();
        IbuQuoteBondDto ibuQuoteBondDto = ibuQuoteMapper.selectQuoteBondByKey(quoteId);
        Long bVersion = ibuQuoteBondDto.getbVersion();
        
        if (bVersion != null) {
            // 历史对话报价查询
            List<BondDialogueQuoteDto> bdqList = ibuQuoteMapper.selectBondDialogueQuoteHisList(
                new BondQuoteHisSearchDto(quoteId, bVersion));

            if (CollectionUtils.isNotEmpty(bdqList)) {
                // "在线表"删除当前版本的记录
                if (quoteBondDet != null) {
                    quoteBondDetMapper.deleteByPrimaryKey(quoteBondDet.getDetId());
                }
                ibuQuoteMapper.deleteQuoteEtrReByQuoteId(quoteId);
                quoteBondMapper.deleteByPrimaryKey(quoteId);

                // 再将历史数据插入到“在线表”中，这里循环其实就走一次，因为只有一条数据
                for (BondDialogueQuoteDto hisDto : bdqList) {
                    // add by曹国 新增发送失败回退的特殊处理
                    if (DictCodeList.银行间上行报价状态_新增中.getValue().equals(
                        hisDto.getIbuQuoteBondDto().getTradeStatus())
                        && DictCodeList.银行间上行发送状态_已发送.getValue().equals(
                            hisDto.getIbuQuoteBondDto().getSendStatus())) {
                        hisDto.getIbuQuoteBondDto().setTradeStatus(StringUtils.EMPTY);
                        hisDto.getIbuQuoteBondDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                    }
                    
                    hisDto.setExeTypeIBU(inputBean.getExeTypeIBU());
                    quoteBondMapper.insert(hisDto.getIbuQuoteBondDto());
                    if (hisDto.getIbuQuoteBondDetDto() != null) {
                        // add by曹国 新增发送失败回退的特殊处理
                        if (DictCodeList.银行间上行报价状态_新增中.getValue().equals(
                            hisDto.getIbuQuoteBondDto().getTradeStatus())
                            && DictCodeList.银行间上行发送状态_已发送.getValue().equals(
                                hisDto.getIbuQuoteBondDto().getSendStatus())) {
                            hisDto.getIbuQuoteBondDetDto().setTradeStatus(StringUtils.EMPTY);
                            hisDto.getIbuQuoteBondDetDto().setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
                        }
                        quoteBondDetMapper.insert(hisDto.getIbuQuoteBondDetDto());
                        inputBean.setIbuQuoteBondDetDto(hisDto.getIbuQuoteBondDetDto());
                    }
                    //多组合
                    if(null!=hisDto.getIbuQuoteEtrReDtoList()&&hisDto.getIbuQuoteEtrReDtoList().size()>0){
                        ibuQuoteMapper.batchInsetIbuQuoteEtrRe(hisDto.getIbuQuoteEtrReDtoList());
                        inputBean.setIbuQuoteEtrReDtoList(hisDto.getIbuQuoteEtrReDtoList());
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

        result.setBondDialogueQuote(inputBean);
        return result;
    }

    @Override
    public QuoteResultDto updateBondDialogueQuoteByKey(BondDialogueQuoteDto inputBean) {
        setDefaultStatus(inputBean, false);
        return excuteAndReturnQuoteResult(inputBean, null,
            x -> {
                //String quoteId = inputBean.getIbuQuoteBondDto().getQuoteId();

                // 向“在线表”更新数据（含前置版本号）
                int affectRows = quoteBondMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDto());
                affectRows += quoteBondDetMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDetDto());
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

    @Override
    public QuoteResultDto deleteBondDialogueQuoteByKey(String quoteId, boolean deleteHis) {
        return excuteAndReturnQuoteResult(null, quoteId,
            x -> {
                // 明细数据删除
                int affectRows = ibuQuoteMapper.deleteQuoteDetByQuoteId(x);

                if (deleteHis) {
                    // 删除历史表Version=-1的数据
                    affectRows += ibuQuoteMapper.deleteQuoteDetHisByQuoteId(x);
                }

                // 报价主信息数据删除
                affectRows += quoteBondMapper.deleteByPrimaryKey(x);

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
    
    @Override
    public QuoteResultDto deleteWflBondDialogueQuoteByKey(String quoteId, boolean deleteHis) {
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
    public void updateBondDialogueQuoteStatus(BondDialogueQuoteDto inputBean) {
        if (inputBean == null) return;

        Map<String, String> statusMap = CommonUtils.getBondDialogueQuoteStatus(inputBean.getExeTypeIBU());
        String tradeStatus = statusMap.getOrDefault(IbuDictCode.银行间上行报价状态.getValue(),
            StringUtils.EMPTY);
        String sendStatus = statusMap.getOrDefault(IbuDictCode.银行间上行发送状态.getValue(),
            DictCodeList.银行间上行发送状态_待发送.getValue());

        if (inputBean.getIbuQuoteBondDto() != null) {
            if(!DictCodeList.银行间上行操作类型_撤销.getValue().equals(inputBean.getExeTypeIBU())){
                inputBean.getIbuQuoteBondDto().setSendStatus(sendStatus);
            }
            inputBean.getIbuQuoteBondDto().setTradeStatus(tradeStatus);
        }

        if (inputBean.getIbuQuoteBondDetDto() != null) {
            if(!DictCodeList.银行间上行操作类型_撤销.getValue().equals(inputBean.getExeTypeIBU())){
                inputBean.getIbuQuoteBondDetDto().setSendStatus(sendStatus);
            }
            inputBean.getIbuQuoteBondDetDto().setTradeStatus(tradeStatus);
        }
    }

    /**
     * 根据接口反馈的对话报价信息以及操作类型，针对更新相应的的字段。<br />
     * 同时调用【对话报价状态更新】方法，更新对话报价信息中的发送状态和交易状态。
     */
    @Override
    public void updateBondDialogueQuoteInterf(BondDialogueQuoteDto inputBean) {
        // 更新对话报价信息中的发送状态和交易状态
        updateBondDialogueQuoteStatus(inputBean);
        quoteBondMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDto());
        quoteBondDetMapper.updateByPrimaryKeySelective(inputBean.getIbuQuoteBondDetDto());
    }

    @Override
    public List<BondDialogueQuoteDto> getBondDialogueQuoteHisList(BondQuoteHisSearchDto inputBean) {
        return ibuQuoteMapper.selectBondDialogueQuoteHisList(inputBean);
    }

    @Override
    public Page<BondDialogueQuoteDto> getBondDialogueQuoteHisPageList(int pageNum, int pageSize,
                                                                   BondQuoteHisSearchDto inputBean) {
        Page<BondDialogueQuoteDto> pageData = new Page<>();
        com.github.pagehelper.Page<BondDialogueQuoteDto> pageList = PageHelper.startPage(pageNum,
            pageSize).doSelectPage(() -> ibuQuoteMapper.selectBondDialogueQuoteHisList(inputBean));
        pageData = new Page<>(pageList);
        return pageData;
    }

    @Override
    public QuoteResultBatDto batchInsertBondDialogueQuote(List<BondDialogueQuoteDto> inputList) {
        QuoteResultBatDto resultDto = new QuoteResultBatDto();

        inputList.forEach(p -> {
            setDefaultStatus(p, true);
            p.setVersion(-1L, -1L);
        });

        // 向历史表插入数据
        ibuQuoteMapper.batchInsertBondDialogueQuote(inputList, "_HIS");

        // 向“在线表”插入数据
        ibuQuoteMapper.batchInsertBondDialogueQuote(inputList, "");

        List<QuoteResultDto> quoteResultList = new ArrayList<>();
        for (BondDialogueQuoteDto bdqDto : inputList) {
            QuoteResultDto item = new QuoteResultDto();
            item.setBondDialogueQuote(bdqDto);
            item.setStatus(true);
            quoteResultList.add(item);
        }

        resultDto.setQuoteResultList(quoteResultList);
        resultDto.setSuccessAccout(inputList.size());
        resultDto.setFailAccout(0);
        return resultDto;
    }

    @Override
    public QuoteResultDto updateBondDialogueQuoteVersion(GateWayReturnMsgDto inputBean) {
        QuoteResultDto result = new QuoteResultDto(true);
        BondDialogueQuoteDto bondDialogueQuote = new BondDialogueQuoteDto();

        // 查询数据库中的现存数据
        IbuQuoteBondDto oldQuoteBond = ibuQuoteMapper.selectQuoteBondByKey(inputBean.getQuoteId());

        if (oldQuoteBond != null) {
            // 主表版本号要小于当前传过来的版本号
            //if (oldQuoteBond.getVersion() < inputBean.getVersion()) {
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
                bondDialogueQuote.setIbuQuoteBondDtoByHis(quoteBondHis);

                // 更新明细
                IbuQuoteBondDet quoteBondDet = new IbuQuoteBondDet();
                quoteBondDet.setQuoteId(inputBean.getQuoteId());
                
                GateWayReqMsgDTO detMsgDto = null;
                if (CollectionUtils.isNotEmpty(inputBean.getReqMsgDto())) {
                    // 对话报价一对一关系，取第一条
                    detMsgDto = inputBean.getReqMsgDto().get(0);
                }

                if (inputBean.isStatus()) {
                    if (inputBean.getVersion() != null) {
                        quoteBondDet.setVersion(inputBean.getVersion());
                    }
                    if (detMsgDto != null) {
                        quoteBondDet.setReqId(detMsgDto.getReqId());
                        quoteBondDet.setCpPartyId(detMsgDto.getCpPartyId());
                    }
                }
                else {
                    // 修改报价发送状态
                    quoteBondDet.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());
                    ibuQuoteMapper.updateDialogueDetVersionByQuoteId(quoteBondDet);
                }
                
                // 历史明细表先按Version=-1条件删除
                //ibuQuoteMapper.deleteQuoteDetHisByQuoteId(inputBean.getQuoteId());

                // 查询最新的报价明细信息
                IbuQuoteBondDet newQuoteDet = ibuQuoteMapper.selectByQuoteId(inputBean.getQuoteId());

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
                }
                // 向历史明细表中插入新版本号的数据
                //quoteBondDetHisMapper.insertSelective(quoteBondDetHis);

                bondDialogueQuote.setIbuQuoteBondDetDtoByHis(quoteBondDetHis);
                
                // add by etliu 20210316 银行间上下行多组合 start
                // 查询银行间报价指令关系业务表数据
                List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(
                    inputBean.getQuoteId());
                if (!StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                    bondDialogueQuote.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
                }
                // add by etliu 20210316 银行间上下行多组合 end
                
                result.setBondDialogueQuote(bondDialogueQuote);
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
     * 执行方法并返回“处理结果”对象
     * @param inputBean BondDialogueQuoteDto
     * @param quoteId String
     * @param func Function<String, Integer>
     * @return QuoteResultDto
     */
    private QuoteResultDto excuteAndReturnQuoteResult(BondDialogueQuoteDto inputBean,
                                                      String quoteId,
                                                      Function<String, Integer> func) {
        QuoteResultDto result = new QuoteResultDto();

        if (StringUtils.isNotBlank(quoteId) && inputBean == null) {
            IbuQuoteBondDto quoteBond = ibuQuoteMapper.selectQuoteBondByKey(quoteId);
            IbuQuoteBondDetDto quoteBondDet = ibuQuoteMapper.selectByQuoteId(quoteId);
			inputBean = new BondDialogueQuoteDto(quoteBond, quoteBondDet);
			
	         // add by etliu 20210313 银行间上下行多组合 start
			inputBean.setIbuQuoteEtrReDtoList(ibuQuoteMapper.selectQuoteEtrReDataByQuoteId(quoteId));
	         // add by etliu 20210313 银行间上下行多组合 end
        }

        int affectRows = func.apply(quoteId);
        log.info("affectRows is {}", affectRows);

        result.setBondDialogueQuote(inputBean);
        result.setStatus(true);
        return result;
    }

    /**
     * 向报价历史表中插入数据
     * @param inputBean BondDialogueQuoteDto
     * @return int
     */
    private int insertHistoryData(BondDialogueQuoteDto inputBean) {
        IbuQuoteBondHis quoteBondHis = new IbuQuoteBondHis();
        IbuQuoteBondDetHis quoteBondDetHis = new IbuQuoteBondDetHis();
        //银行间报价指令关系
        List<IbuQuoteEtrReHis> ibuQuoteEtrReHisList = new ArrayList<>();
        try {
            BeanUtils.copyProperties(quoteBondHis, inputBean.getIbuQuoteBondDto());
            BeanUtils.copyProperties(quoteBondDetHis, inputBean.getIbuQuoteBondDetDto());
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

        if (StringUtils.isNotBlank(quoteBondDetHis.getDetId()) && quoteBondDetHis.getVersion() != null) {
            affectRows += quoteBondDetHisMapper.insertSelective(quoteBondDetHis);
        }
        if(ibuQuoteEtrReHisList.size()>0){
            affectRows += ibuQuoteMapper.batchInsetIbuQuoteEtrReHis(ibuQuoteEtrReHisList);
        }

        return affectRows;
    }

    /**
     * 验证状态变更操作是否合理
     * @param exeTypeIBU String
     * @param curTradeStatus String
     * @param curSendStatus String
     * @return String 验证消息
     */
    protected String validateStatus(String exeTypeIBU, String curTradeStatus, String curSendStatus) {
        String msg = null;

        List<String> allowExeTypeList = BondDialogueQuoteSubServiceImpl.statusTable.get(
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
    
    /**
     * 根据外部报价编号查询报价编号
     * @param pquoteId String
     * @return String 报价编号
     */
    @Override
    public String getQuoteIdByPquoteId(String pquoteId){
        return ibuQuoteMapper.getQuoteIdByPquoteId(pquoteId);
    }

}
