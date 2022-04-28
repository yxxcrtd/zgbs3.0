/*
 * 文件名：CfetsQuotedCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.common.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteMapper;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBond;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.common.dao.CfetsQuotedCommonMapper;
import com.joyintech.service.bankuplink.common.dto.CfetsIbRepoPosPortfolioCashRequest;
import com.joyintech.service.bankuplink.common.dto.CfetsIbRepoPosPortfolioSecRequest;
import com.joyintech.service.bankuplink.common.dto.CfetsIbRepoPositionRequest;
import com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.dto.RepoQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.service.BankUplinkPublishService;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.service.bankuplink.repo.dao.CfetsRepoDealQuotedPrice001Mapper;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.service.bankuplink.repo.service.CfetsRepoDealQuotedPrice001Service;
import com.joyintech.system.base.entity.SysUserAdd;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoDealQuotedPriceDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoPledgeBondDto;
import com.joyintech.tams.invest.common.dto.cstp.IbuQuoteEntCrepPledgeCommonDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 银行间上行共通Service
 * 
 * @author zhangliang
 * @version 1.0 2020年5月12日
 * @see CfetsQuotedCommonService
 * @since 1.0
 */
@Service("backCfetsQuotedCommonService")
public class CfetsQuotedCommonService extends BaseService {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * {@linkplain}
     */
    @Autowired
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * {@linkplain}
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private EtrDistributeMapper etrDistributeMapper;

    /**
     * 投资委托信息Mapper
     */
    @Autowired
    private BaseEtrInvestInfoMapper etrInvestInfoMapper;

    /**
     * 报价完成状况Mapper
     */
    @Autowired
    private EtrQuoteFinishStatusMapper etrQuoteFinishStatusMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonMapper cfetsQuotedCommonMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsRepoDealQuotedPrice001Service cfetsRepoDealQuotedPrice001Service;
    
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    
    
    @Autowired
    private EtrDistributeInterfTmpMapper etrDistributeInterfTmpMapper;
    
    /**
     * IbuMainService
     */
    @Autowired
    private IbuMainService ibuMainService;

    @Autowired
    private IbuQuoteMapper ibuQuoteMapper;

    @Autowired
    private CstpCommonService cstpCommonService;
    

    /**
     * mq信息推送service {@linkplain}
     */
    @Autowired(required = false)
    private BankUplinkPublishService messageSenderService;
    
    @Value("${application.rocketmq.topic.bond:trcms_pfms_tbank_bond}")
    private String bondTopic;
    
    @Value("${application.rocketmq.topic.repo:trcms_pfms_tbank_repo}")
    private String repoTopic;

    @Autowired
    private CfetsRepoDealQuotedPrice001Mapper cfetsRepoDealQuotedPrice001Mapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 委托放弃处理
     * 
     * @param entrustId 委托代码
     * @return 执行是否成功 "1" 已接收且完成度为0指令,才能执行放弃操作！ "2" 存在流程中的报价，不可放弃！ "3"成功
     * @see
     * @since 1.0
     * 
     */
    @Transactional
    public String entrustGiveUpHandle(String entrustId) {
        if (StringUtil.isEmpty(entrustId)) {
            return "1";
        }

        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust == null) {
            return "1";
        }

        // 已接收且完成度为0的指令可以选择【放弃】接收
        if (!StringUtil.equals(etrEntrust.getEntrustStatus(),
            BussinessDictCodeList.委托状态_已接收.getValue())) {
            return "1";
        }

        EtrFinishStatusKey etrFinishStatusKey = new EtrFinishStatusKey();
        etrFinishStatusKey.setEntrustId(entrustId);
        List<EtrFinishStatus> etrFinishStatusList = etrInvestInfoMapper.selectEtrFinishStatusByPrimaryKey(
            etrFinishStatusKey);
        if (!StringUtil.isEmptyList(etrFinishStatusList)) {
            return "1";
        }
        Map<String,Object> param= new HashMap<>();
        param.put("entrustId", entrustId);
        List<EtrQuoteFinishStatus> etrQuoteFinishStatusList =  cfetsQuotedCommonMapper.selectEtrQuoteFinishStatusList(param);  
        if (!StringUtil.isEmptyList(etrQuoteFinishStatusList)) {
            return "1";
        }
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        if (etrEntrustAdd == null) {
            return "1";
        }

        //是否存在流程中或者有效且数据状态不为 已撤回 已终止
        
        int i =cfetsQuotedCommonMapper.checkDataStatus(param);
        if(i>0){
        	return "2";
        }
        
        
//        List<EtrDistribute> etrDistributeList = busiAutoEtrDispService.selectTrader(etrEntrust,
//            etrEntrustAdd);
//        if (!StringUtil.isEmptyList(etrDistributeList)) {
//            // 删除委托指令分发
//            EtrDistribute etrDistributeDel = new EtrDistribute();
//            etrDistributeDel.setEntrustId(entrustId);
//            etrInvestInfoMapper.deleteEtrDistributeByPrimaryKey(etrDistributeDel);
//            for (EtrDistribute etrDistribute : etrDistributeList) {
//                etrDistributeMapper.insertSelective(etrDistribute);
//            }
//        }

        boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId,com.joyintech.tams.constant.DictCodeList.委托状态_待接收.getValue());
		//2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
		/*List<EtrDistributeInterfTmp> distmpList = cfetsQuotedCommonMapper.selectEtrDistributeInterfTmpList(param);

		if(!StringUtil.isEmptyList(distmpList)) {
			boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId,com.joyintech.tams.constant.DictCodeList.委托状态_待接收.getValue());
			
			List<EtrDistribute> disList = new ArrayList<EtrDistribute>();
			
			for(EtrDistributeInterfTmp entity : distmpList) {
				 EtrDistribute dis = new EtrDistribute();
                 dis.setEntrustId(entity.getEntrustId());
                 dis.setTrader(entity.getTrader());
                 dis.setRecStatus(com.joyintech.tams.constant.DictCodeList.委托状态_待接收.getValue());
                 dis.setTradingHeader(entity.getTradingHeader());
                 disList.add(dis);
			}
			
			this.etrInvestInfoServiceImpl.distributeDelegate(disList);
		}else{
			boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId,com.joyintech.tams.constant.DictCodeListExt3.委托状态_待分发.getValue());
		}*/

//        EtrEntrust etrEntrustUpdate = new EtrEntrust();
//        etrEntrustUpdate.setEntrustId(entrustId);
//        etrEntrustUpdate.setEntrustStatus(BussinessDictCodeList.委托状态_待接收.getValue());
//        etrEntrustMapper.updateByPrimaryKeySelective(etrEntrustUpdate);

        return "3";
    }

    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @return 交易员列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryMySideTraderPage(int page, int length,
                                                       Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsQuotedCommonMapper.queryMySideTraderList(param));
    }
    

    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @return 交易员列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryCounterSideTraderPage(int page, int length,
                                                       Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsQuotedCommonMapper.queryCounterSideTraderList(param));
    }
    
    /**
     * 查询本方交易员列表
     * 
     * @param param 入参
     * @return 交易员列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryMySideTraderList(Map<String, Object> param) {
        return cfetsQuotedCommonMapper.queryMySideTraderList(param);
    }
    
    
    /**
     * 查询对手方交易员列表
     * 
     * @param param 入参
     * @return 交易员列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryCounterSideTraderList(Map<String, Object> param) {
        return cfetsQuotedCommonMapper.queryCounterSideTraderList(param);
    }

    /**
     * 根据交易员Id查询交易员名称
     * 
     * @param traderId 交易员Id
     * @return 交易员名称
     * @see
     * @since 1.0
     */
    public String queryTraderNameByTraderId(String traderId) {
        Map<String, Object> param = new HashMap<>();
        param.put("initValue", traderId);
        List<SelectOptionDto> list = cfetsQuotedCommonMapper.queryMySideTraderList(param);
        if (StringUtil.isEmptyList(list)){
            return null;
        }
        return list.get(0).getText();
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
        return ibuQuoteErrlogDto==null ? "未查询到错误信息":(ibuQuoteErrlogDto.getErrCode() != null ? "错误码： "
                                                         + ibuQuoteErrlogDto.getErrCode() : "")
               + (ibuQuoteErrlogDto.getErrMsg() != null ? "<br/>错误信息： "
                                                          + ibuQuoteErrlogDto.getErrMsg() : "");
    }
    
    /**
     * 根据市场代码查询金融产品代码
     * 
     * @param finprodMarketId 市场代码
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    public String queryFinprodIdByFinprodMarketId(String finprodMarketId){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodMarketId", finprodMarketId);
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        return cfetsQuotedCommonMapper.queryFinprodIdByFinprodMarketId(param);
    }
    
    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryFinprodIdPage(int page, int length,Map<String, Object> param) {
        param.put("assestType", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        return this.queryForPageForSelectReturn(page, length,()->cfetsQuotedCommonMapper.queryFinprodIdList(param));
    }
    
    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryFinprodIdList(Map<String, Object> param) {
        param.put("assestType", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        return cfetsQuotedCommonMapper.queryFinprodIdList(param);
    }

    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryMySidePage(int page, int length, Map<String, Object> param) {
    	param.put("ACCOUNT_SOURCE_01",BussinessDictCodeList.账户来源_理财.getValue());
        param.put("userName", LoginUserUtil.getUserName());
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsQuotedCommonMapper.queryMySideList(param));
    }
    
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryCounterSidePage(int page, int length, Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsQuotedCommonMapper.queryCounterSideList(param));
    }
    
    /**
     * 查询市场代码列表
     * 
     * @return 市场代码列表
     * @see
     * @since 1.0
     */
    public List<String> queryFinprodMarketIdList() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("assestType", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        return cfetsQuotedCommonMapper.queryFinprodMarketIdList(param);
    }

    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryMySideList(Map<String, Object> param) {
        return cfetsQuotedCommonMapper.queryMySideList(param);
    }
    
    /**
     * 查询本方机构列表
     * 
     * @param param 入参
     * @return 本方机构列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryCounterSideList(Map<String, Object> param) {
        return cfetsQuotedCommonMapper.queryCounterSideList(param);
    }

    /** 对话报价集合，Key为角色类型，Value为交易状态：发送状态：操作类型(True为更新，False为删除) */
    private static Map<String, Table<String, String, Boolean>> _dialogueMap;

    /** 请求报价集合，Key为角色类型，Value为交易状态：发送状态：操作类型(True为更新，False为删除) */
    private static Map<String, Table<String, String, Boolean>> _requestMap;

    /** 质押回购报价集合，Key为角色类型，Value为交易状态：发送状态：操作类型(True为更新，False为删除) */
    private static Map<String, Table<String, String, Boolean>> _repoDlgMap;

    static {
        Table<String, String, Boolean> dialogueXjfTable = HashBasedTable.create();
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已新增.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已修改.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(), StringUtil.EMPTY,
            false);
        /*历史报价撤销*/
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        dialogueXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(), StringUtil.EMPTY,
                false);

        Table<String, String, Boolean> dialogueHffTable = HashBasedTable.create();
        dialogueHffTable.put(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        /*历史报价撤销*/
        dialogueHffTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        dialogueHffTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(), StringUtil.EMPTY,
                false);

        _dialogueMap = new ImmutableMap.Builder<String, Table<String, String, Boolean>>().put(
            BussinessDictCodeList.银行间上行本方角色_询价方.getValue(), dialogueXjfTable).put(
                BussinessDictCodeList.银行间上行本方角色_回复方.getValue(), dialogueHffTable).build();

        Table<String, String, Boolean> requestFzsfTable = HashBasedTable.create();
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_待回复.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(), StringUtil.EMPTY,
            false);
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已过期.getValue(), StringUtil.EMPTY,
            false);
        /*历史报价撤销*/
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        requestFzsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(), StringUtil.EMPTY,
            false);

        Table<String, String, Boolean> requestZsfTable = HashBasedTable.create();
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已回复.getValue(), StringUtil.EMPTY,
            true);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已修改.getValue(), StringUtil.EMPTY,
            true);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(), StringUtil.EMPTY,
    		true);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(), StringUtil.EMPTY,
            false);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已过期.getValue(), StringUtil.EMPTY,
            false);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_待回复.getValue(), StringUtil.EMPTY,
            false);
        /*历史报价撤销*/
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        requestZsfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(), StringUtil.EMPTY,
            false);

        _requestMap = new ImmutableMap.Builder<String, Table<String, String, Boolean>>().put(
            BussinessDictCodeList.银行间上行本方角色_非做市方.getValue(), requestFzsfTable).put(
                BussinessDictCodeList.银行间上行本方角色_做市方.getValue(), requestZsfTable).build();

        Table<String, String, Boolean> repoDlgXjfTable = HashBasedTable.create();
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已新增.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已修改.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(),
            StringUtil.EMPTY, false);
        /*历史报价撤销*/
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        repoDlgXjfTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		StringUtil.EMPTY, false);

        Table<String, String, Boolean> repoDlgHffTable = HashBasedTable.create();
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), true);
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已终止.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(),
            BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        /*历史报价撤销*/
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		BussinessDictCodeList.银行间上行发送状态_成功.getValue(), false);
        repoDlgHffTable.put(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue(),
    		StringUtil.EMPTY, false);

        _repoDlgMap = new ImmutableMap.Builder<String, Table<String, String, Boolean>>().put(
            BussinessDictCodeList.银行间上行本方角色_询价方.getValue(), repoDlgXjfTable).put(
                BussinessDictCodeList.银行间上行本方角色_回复方.getValue(), repoDlgHffTable).build();
    }

    /**
     * 报价完成度更新
     * 
     * @param dto QuoteResultDto
     * @return int 影响的行数
     */
    @Transactional
    public int updateQuoteCompletion(QuoteResultDto dto) {
        int result = -1;
        Table<String, String, Boolean> operationTbl = null;
        Boolean operateType = null;

        BaseQuoteDto baseDto = dto.getBaseQuoteDto();
        if (baseDto != null) {
            // Added by JIAOXUJIN on 2020-06-17 无委托模式不进行完成度的更新
         // modify by etliu 20210315 银行间上下行多组合 start
//            if (StringUtils.isBlank(baseDto.getEntrustId())) {
            if (StringUtils.isBlank(baseDto.getEntrustId())
                && StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
         // modify by etliu 20210315 银行间上下行多组合 end
                return result;
            }

            String quoteType = dto.getQuoteType();
            String quoteRoleType = baseDto.getQuoteRoleType();
            String tradeStatus = StringUtils.isNotBlank(
                baseDto.getTradeStatus()) ? baseDto.getTradeStatus() : StringUtil.EMPTY;
            String sendStatus = StringUtils.isNotBlank(
                baseDto.getSendStatus()) ? baseDto.getSendStatus() : StringUtil.EMPTY;

            if (DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(quoteType)) {
                operationTbl = _dialogueMap.get(quoteRoleType);

                if (operationTbl != null) {
                    operateType = operationTbl.get(tradeStatus, sendStatus);
                    // 删除和插入
                    if (operateType != null && operateType) {
                        // modify by etliu 20210315 银行间上下行多组合 start
                        if (StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
                            // 单组合报价处理
                            // 明细 交易份额*100
                            BigDecimal shareAmt = getDecimalDefaultZero(
                                baseDto.getShareAmt()).multiply(new BigDecimal(100.0d));
                            deleteFinishStatus(baseDto);
                            insertFinishStatus(dto, shareAmt, null);
                        }
                        else {
                            // 多组合报价处理
                            for (IbuQuoteEtrReDto ibuQuoteEtrReDto : dto.getIbuQuoteEtrReDtoList()) {
                                BaseQuoteDto tempBaseDto = dto.getBaseQuoteDto();
                                BeanUtil.copyPropertiesIgnoreNull(tempBaseDto, baseDto);
                                tempBaseDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());

                                // 明细 交易份额*100
                                BigDecimal shareAmt = getDecimalDefaultZero(
                                    ibuQuoteEtrReDto.getShareAmt()).multiply(new BigDecimal(100.0d));
                                deleteFinishStatus(tempBaseDto);
                                
                                insertFinishStatus(dto, shareAmt, tempBaseDto);
                            }
                        }
                        // modify by etliu 20210315 银行间上下行多组合 end
                    }
                    // 删除
                    else if (operateType != null && !operateType) {
                     // modify by etliu 20210315 银行间上下行多组合 start
                        if (StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
                            // 单组合报价处理
                            deleteFinishStatus(baseDto);
                        }
                        else {
                            // 多组合报价处理
                            for (IbuQuoteEtrReDto ibuQuoteEtrReDto : dto.getIbuQuoteEtrReDtoList()) {
                                BaseQuoteDto tempBaseDto = dto.getBaseQuoteDto();
                                BeanUtil.copyPropertiesIgnoreNull(tempBaseDto, baseDto);
                                tempBaseDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
                                deleteFinishStatus(tempBaseDto);
                            }
                        }
                     // modify by etliu 20210315 银行间上下行多组合 end
                    }
                }
            }
            else if (DictCodeList.报价系统区分类型_现券请求报价.getValue().equals(quoteType)) {
                operationTbl = _requestMap.get(quoteRoleType);

                if (operationTbl != null) {
                    operateType = operationTbl.get(tradeStatus, sendStatus);
                    // 删除和插入
                    if (operateType != null && operateType) {
                     // modify by etliu 20210316 银行间上下行多组合 start
                        if (StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
                            // 单组合报价处理
                            BigDecimal shareAmt = null;
                            // modify by zhangliang 2020-07-20 做市回复成交发送状态是空，此处特殊判断支持场景银行间上行操作类型_接收请求回复成交成功
                            if ((BussinessDictCodeList.银行间上行报价状态_已成交.getValue().equals(tradeStatus)
                                && BussinessDictCodeList.银行间上行发送状态_成功.getValue().equals(sendStatus))
                                || (StringUtil.equals(baseDto.getQuoteRoleType(), DictCodeList.银行间上行本方角色_做市方.getValue())
                                    && BussinessDictCodeList.银行间上行报价状态_已成交.getValue().equals(tradeStatus))) {
                                List<IbuQuoteBondDetDto> detList = dto.getBondRequestQuote()
                                    .getIbuQuoteBondDetDtoList().stream().filter(
                                        p -> StringUtils.equals(p.getTradeStatus(), tradeStatus)
                                        && StringUtils.equals(p.getSendStatus(), sendStatus)).collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(detList)) {
                                    // 明细 sum(交易份额*100)
                                    shareAmt = detList.stream().map(
                                        p -> getDecimalDefaultZero(p.getShareAmt()).multiply(
                                            new BigDecimal(100.0d))).reduce(new BigDecimal("0"),
                                                BigDecimal::add);
                                }
                            }
                            else {
                                // 主表 交易份额*100
                                shareAmt = getDecimalDefaultZero(
                                    dto.getBondRequestQuote().getIbuQuoteBondDto().getShareAmt()).multiply(
                                        new BigDecimal(100.0d));
                            }
                            
                            deleteFinishStatus(baseDto);
                            insertFinishStatus(dto, shareAmt, null);
                        }
                        else {
                            // 多组合报价处理
                            for (IbuQuoteEtrReDto ibuQuoteEtrReDto : dto.getIbuQuoteEtrReDtoList()) {
                                BaseQuoteDto tempBaseDto = dto.getBaseQuoteDto();
                                BeanUtil.copyPropertiesIgnoreNull(tempBaseDto, baseDto);
                                tempBaseDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());

                                // 明细 交易份额*100
                                BigDecimal tempShareAmt = getDecimalDefaultZero(
                                    ibuQuoteEtrReDto.getShareAmt()).multiply(new BigDecimal(100.0d));
                                deleteFinishStatus(tempBaseDto);
                                
                                insertFinishStatus(dto, tempShareAmt, tempBaseDto);
                            }
                        }
                     // modify by etliu 20210316 银行间上下行多组合 end
                    }
                    // 删除
                    else if (operateType != null && !operateType) {
                     // modify by etliu 20210316 银行间上下行多组合 start
                        if (StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
                            // 单组合报价处理
                            deleteFinishStatus(baseDto);
                        }
                        else {
                            // 多组合报价处理
                            for (IbuQuoteEtrReDto ibuQuoteEtrReDto : dto.getIbuQuoteEtrReDtoList()) {
                                BaseQuoteDto tempBaseDto = dto.getBaseQuoteDto();
                                BeanUtil.copyPropertiesIgnoreNull(tempBaseDto, baseDto);
                                tempBaseDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
                                deleteFinishStatus(tempBaseDto);
                            }
                        }
                     // modify by etliu 20210316 银行间上下行多组合 end
                    }
                }
            }
            else if (DictCodeList.报价系统区分类型_质押式回购对话报价.getValue().equals(quoteType)) {
                operationTbl = _repoDlgMap.get(quoteRoleType);

                if (operationTbl != null) {
                    operateType = operationTbl.get(tradeStatus, sendStatus);
                 // modify by etliu 20210316 银行间上下行多组合 start
                    // 删除和插入
                    if (operateType != null && operateType) {
                        RepoDialogueQuoteDto repoDialogueQuote = dto.getRepoDialogueQuote();
                        // 主表 全价总额
                        if(!StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())){
                            // 多组合报价处理
                            deleteByQuoteId(baseDto);
                            List<IbuQuoteEtrReDto>  ibuQuoteEtrRes = repoDialogueQuote.getIbuQuoteEtrReDtoList();
                            ibuQuoteEtrRes.stream().forEach(item -> {
                                BaseQuoteDto baseQuoteDto = new BaseQuoteDto();
                                baseQuoteDto.setQuoteId(baseDto.getQuoteId());
                                baseQuoteDto.setEntrustId(item.getEntrustId());
                                BigDecimal priceAmt = item.getFpriceAmt();
                                QuoteResultDto quoteResultDto = new QuoteResultDto();
                                BeanUtil.copyPropertiesIgnoreNull(quoteResultDto,dto);
                                quoteResultDto.getBaseQuoteDto().setEntrustId(item.getEntrustId());
                                insertFinishStatus(quoteResultDto, priceAmt, baseQuoteDto);
                            });
                        }else{
                            // 单组合报价处理
                            BigDecimal priceAmt = repoDialogueQuote.getIbuQuoteCrepDto().getFpriceAmt();
                            deleteFinishStatus(baseDto);
                            insertFinishStatus(dto, priceAmt, null);
                        }
                    }
                    // 删除
                    else if (operateType != null && !operateType) {
                        if (StringUtil.isEmptyList(dto.getIbuQuoteEtrReDtoList())) {
                            // 单组合报价处理
                            deleteFinishStatus(baseDto);
                        }
                        else {
                            // 多组合报价处理
//                            for (IbuQuoteEtrReDto ibuQuoteEtrReDto : dto.getIbuQuoteEtrReDtoList()) {
//                                BeanUtil.copyPropertiesIgnoreNull(tempBaseDto, baseDto);
//                                tempBaseDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
                                deleteByQuoteId(baseDto);
//                            }
                        }
                    }
                 // modify by etliu 20210316 银行间上下行多组合 end
                }
            }
            else {
                log.warn("报价方式为空或无法识别。");
            }

            if (operationTbl == null) {
                log.warn("通过报价类型[{}]和角色类型[{}]没有找到匹配的配置数据。", quoteType, quoteRoleType);
            }
            if (operateType == null) {
                log.warn("通过交易状态[{}]和发送状态[{}]没有找到匹配的操作类型。", tradeStatus, sendStatus);
            }
        }
        else {
            log.warn("BaseQuoteDto为空，无法进行操作。");
        }

        return result;
    }

    /**
     * 删除报价完成状况表中的一条数据
     * 
     * @param baseDto
     * @see
     * @since 1.0
     */
    private void deleteFinishStatus(BaseQuoteDto baseDto) {
        log.info("删除报价指令完成度 QuoteId:" + baseDto.getQuoteId() + ",EntrustId:" + baseDto.getEntrustId());
        EtrQuoteFinishStatusKey key = new EtrQuoteFinishStatusKey();
        key.setEntrustId(baseDto.getEntrustId());
        key.setQuoteId(baseDto.getQuoteId());
        etrQuoteFinishStatusMapper.deleteByPrimaryKey(key);
    }

    /**
     * 删除报价完成状况表中的一条数据
     *
     * @param baseDto
     * @see
     * @since 1.0
     */
    public void deleteByQuoteId(BaseQuoteDto baseDto) {
        log.info("删除报价指令完成度 QuoteId:" + baseDto.getQuoteId());
        EtrQuoteFinishStatusKey key = new EtrQuoteFinishStatusKey();
        key.setQuoteId(baseDto.getQuoteId());
        etrQuoteFinishStatusMapper.deleteByQuoteId(key);
    }
    /**
     * 向报价完成状况表插入一条数据
     * 
     * @param dto
     * @param shareAmt
     * @param temoBaseDto 多组合使用dto
     * @see
     * @since 1.0
     */
    private void insertFinishStatus(QuoteResultDto dto, BigDecimal shareAmt, BaseQuoteDto tempBaseDto) {
        BaseQuoteDto baseDto = dto.getBaseQuoteDto();
        EtrQuoteFinishStatus record = new EtrQuoteFinishStatus();
        record.setEntrustId(baseDto.getEntrustId());
        
     // add by etliu 20210315 银行间上下行多组合 start
        if (StringUtil.isBlank(record.getEntrustId())
            && tempBaseDto != null) {
            record.setEntrustId(tempBaseDto.getEntrustId());
        }
     // add by etliu 20210315 银行间上下行多组合 end
        
        record.setQuoteId(baseDto.getQuoteId());
        record.setFinishDate(baseDto.getQuoteDate());
        record.setFinishAmt(shareAmt);
        
        if (DictCodeList.报价系统区分类型_现券对话报价.getValue().equals(dto.getQuoteType())) {
            record.setTrader(dto.getBondDialogueQuote().getIbuQuoteBondDto().getCreateUser());
            record.setCreateUser(dto.getBondDialogueQuote().getIbuQuoteBondDto().getCreateUser());
            record.setCreateDept(dto.getBondDialogueQuote().getIbuQuoteBondDto().getCreateDept());
            record.setUpdateUser(dto.getBondDialogueQuote().getIbuQuoteBondDto().getUpdateUser());
        }
        else if (DictCodeList.报价系统区分类型_现券请求报价.getValue().equals(dto.getQuoteType())) {
            record.setTrader(dto.getBondRequestQuote().getIbuQuoteBondDto().getCreateUser());
            record.setCreateUser(dto.getBondRequestQuote().getIbuQuoteBondDto().getCreateUser());
            record.setCreateDept(dto.getBondRequestQuote().getIbuQuoteBondDto().getCreateDept());
            record.setUpdateUser(dto.getBondRequestQuote().getIbuQuoteBondDto().getUpdateUser());
        }
        else if (DictCodeList.报价系统区分类型_质押式回购对话报价.getValue().equals(dto.getQuoteType())) {
            record.setTrader(dto.getRepoDialogueQuote().getIbuQuoteCrepDto().getCreateUser());
            record.setCreateUser(dto.getRepoDialogueQuote().getIbuQuoteCrepDto().getCreateUser());
            record.setCreateDept(dto.getRepoDialogueQuote().getIbuQuoteCrepDto().getCreateDept());
            record.setUpdateUser(dto.getRepoDialogueQuote().getIbuQuoteCrepDto().getUpdateUser());
        }

        User user = SecurityUtil.getLoginUser();
        if (user != null) {
            record.setCreateUser(user.getUserName());
            record.setCreateDept(user.getDeptCode());
            record.setUpdateUser(user.getUserName());
        }

        record.setUpdateTime(new Date());
        record.setCreateTime(new Date());
        etrQuoteFinishStatusMapper.insertSelective(record);
    }

    private BigDecimal getDecimalDefaultZero(BigDecimal decimal) {
        if (decimal == null) {
            return new BigDecimal(0);
        }
        return decimal;
    }

    /**
     * 组合代码查询（无权限）
     * <功能详细描述>
     * @param page 页数
     * @param length 条数
     * @param param 参数
     * @return 分页集合
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> selectPortfolioId(int page, int length,
                                                   Map<String, Object> param) {
        String initValue =(String)param.get("initValue");
        if(StringUtil.isNotEmpty(initValue)){
            List<String> initValueList = StringUtil.splitToList(",", initValue);
            param.put("initValueList", initValueList);
        }
        Page<SelectOptionDto> pageList = this.queryForPageForSelectReturn(page, length,
            () -> cfetsQuotedCommonMapper.selectPortfolioId(param));
        return pageList;
    }
    
    /**
     *  发布MQ共通处理
     * 
     * @param quoteResultDto 组件返回对象
     * @param isReceive 是否接收
     * @return 发布结果
     * @see
     * @since 1.0
     */
    public boolean publishMQCommonHandle(QuoteResultDto quoteResultDto, boolean isReceive){
        // 返回值
        boolean flag = false;
        // 定义发布MQ对象
        Map<String, Object> mqMap = new HashMap<String, Object>();
        
        if (quoteResultDto != null){
            if (StringUtil.equals(quoteResultDto.getQuoteType(), DictCodeList.报价系统区分类型_现券对话报价.getValue())){
                if (quoteResultDto.getBondDialogueQuote() != null && quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto() != null){
                    IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto();
                    // 接收网关异步返回的报文
                    if (isReceive){
                        // 刷新当日指令列表（广播）
                        if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), DictCodeList.银行间上行报价状态_待回复.getValue())){
                            Map<String, Object> param = new HashMap<String, Object>();
                            param.put("quoteId", ibuQuoteBondDto.getQuoteId());
                            List<BondQuoteInfoDto> bondQuoteInfoDtoList = cfetsBondDealQuotedPriceService.getDayOfferTableList(param);
                            if (!StringUtil.isEmptyList(bondQuoteInfoDtoList)) {
                                mqMap = JsonUtils.object2Map(bondQuoteInfoDtoList.get(0));
                            }
                            /*mqMap.put("sendType", WebSocketSendType.广播.getValue());
                            flag = bankUplinkPublishService.sendMessage(bondTopic, "newBondDialogueQuoteAll", JsonUtils.object2Json(mqMap));*/
                        }
                        else if (StringUtil.equals(ibuQuoteBondDto.getExeType(), DictCodeList.银行间上行操作类型_对手方报价撤回.getValue())){
                            this.publishMQDayHandle();
                        }
                        else {
                            // 指定交易员
                            /*mqMap.put("sendType", WebSocketSendType.指定单用户.getValue());
                            mqMap.put("toUser", ibuQuoteBondDto.getUpdateUser());
                            bankUplinkPublishService.sendMsg(bondTopic, "refreshDayOfferTableLoad", JsonUtils.object2Json(mqMap));*/
                        }
                    }
                }
            }
            else if (StringUtil.equals(quoteResultDto.getQuoteType(), DictCodeList.报价系统区分类型_现券请求报价.getValue())){
                if (quoteResultDto.getBondRequestQuote() != null && quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto() != null){
                    IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto();
                    // 接收网关异步返回的报文
                    if (isReceive){
                        // 刷新做市报价列表（广播）
                        if (StringUtil.equals(ibuQuoteBondDto.getQuoteRoleType(), DictCodeList.银行间上行本方角色_做市方.getValue())
                            && StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), DictCodeList.银行间上行报价状态_待回复.getValue())){
                            // 根据报价代码查询做市报价信息
                            Map<String, Object> param = new HashMap<String, Object>();
                            param.put("quoteId", ibuQuoteBondDto.getQuoteId());
                            List<BondQuoteInfoDto> bondQuoteInfoDtoList = cfetsBondDealQuotedPriceService.getMarketOfferTableList(param);
                            
                            // 发布MQ
                            if (!StringUtil.isEmptyList(bondQuoteInfoDtoList)) {
                                mqMap = JsonUtils.object2Map(bondQuoteInfoDtoList.get(0));
                            }
                           /* mqMap.put("sendType", WebSocketSendType.广播.getValue());
                            flag = bankUplinkPublishService.sendMessage(bondTopic, "newBondMarketOfferQuoteAll", JsonUtils.object2Json(mqMap));
                            if (StringUtil.equals(ibuQuoteBondDto.getExeType(), DictCodeList.银行间上行操作类型_本方撤销请求回复成功.getValue())){
                                mqMap.put("sendType", WebSocketSendType.指定单用户.getValue());
                                mqMap.put("toUser", ibuQuoteBondDto.getUpdateUser());
                                bankUplinkPublishService.sendMsg(bondTopic,
                                    "refreshDayOfferTableLoad", JsonUtils.object2Json(mqMap));
                            }*/
                        }
                        // 针对的是对手方撤销请求报价，需要把做市报价广播刷新
                        else if (StringUtil.equals(ibuQuoteBondDto.getQuoteRoleType(), DictCodeList.银行间上行本方角色_做市方.getValue())
                            && StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), DictCodeList.银行间上行报价状态_已撤回.getValue())
                            && StringUtil.equals(ibuQuoteBondDto.getSendStatus(), StringUtils.EMPTY)){
                            this.publishMQMarketHandle();
                        }
                        // 刷新当日指令报价列表 （指定交易员）
                        else{
                           /* mqMap.put("sendType", WebSocketSendType.指定单用户.getValue());
                            mqMap.put("toUser", ibuQuoteBondDto.getUpdateUser());
                            bankUplinkPublishService.sendMsg(bondTopic,
                                "refreshDayOfferTableLoad", JsonUtils.object2Json(mqMap));*/
                        }
                    }
                }
            }
            else if (StringUtil.equals(quoteResultDto.getQuoteType(), DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())){
                if (quoteResultDto.getRepoDialogueQuote() != null && quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto() != null){
                    IbuQuoteCrepDto ibuQuoteCrepDto = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto();
                    // 接收网关异步返回的报文
                    if (isReceive){
                        // 刷新做市报价列表（广播）
                        if (StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(), DictCodeList.银行间上行报价状态_待回复.getValue())){
                            // 根据报价代码查询做市报价信息
                            Map<String, Object> param = new HashMap<String, Object>();
                            param.put("quoteId", ibuQuoteCrepDto.getQuoteId());
                            List<RepoQuoteInfoDto> repoQuoteInfoDtoList = cfetsRepoDealQuotedPrice001Service.getDayOfferTableList(param);
                            
                            // 发布MQ
                            if (!StringUtil.isEmptyList(repoQuoteInfoDtoList)) {
                                mqMap = JsonUtils.object2Map(repoQuoteInfoDtoList.get(0));
                            }
                            //mqMap.put("sendType", WebSocketSendType.广播.getValue());
                            messageSenderService.pushCommandMsgToMQ(JsonUtils.object2Json(mqMap));
                        }
                        // 刷新当日指令报价列表 （指定交易员）
                        else{
                        	messageSenderService.pushCommandMsgToMQ(JsonUtils.object2Json(mqMap));
                           /* mqMap.put("sendType", WebSocketSendType.指定单用户.getValue());
                            mqMap.put("toUser", ibuQuoteCrepDto.getUpdateUser());
                            bankUplinkPublishService.sendMsg(repoTopic,
                                "refreshDayOfferTableLoad", JsonUtils.object2Json(mqMap));*/
                        }
                    }
                }
            }
        }
        
        if (!flag){
            log.info("MQ发布失败！");
        }
        return flag;
    }
    
    /**
     * 发布MQ刷新做市报价列表
     * 
     * @return
     * @see
     * @since 1.0
     */
    public boolean publishMQMarketHandle(){
        Map<String, Object> mqMap = new HashMap<String, Object>();
        /*mqMap.put("sendType", WebSocketSendType.广播.getValue());
        boolean flag = bankUplinkPublishService.sendMessage(bondTopic, "refreshMarketOfferTableLoad", JsonUtils.object2Json(mqMap));
        if (!flag) {
            log.info("MQ发布失败！");
        }
        
        return flag;*/
        return true;
    }
    
    /**
     * 发布MQ刷新当日指令列表
     * 
     * @return
     * @see
     * @since 1.0
     */
    public boolean publishMQDayHandle(){
        Map<String, Object> mqMap = new HashMap<String, Object>();
       /* mqMap.put("sendType", WebSocketSendType.广播.getValue());
        boolean flag = bankUplinkPublishService.sendMessage(bondTopic, "refreshDayOfferTableLoad", JsonUtils.object2Json(mqMap));
        if (!flag) {
            log.info("MQ发布失败！");
        }
        
        return flag;*/
        return true;
    }
    
    /**
     * 发布MQ刷新当日指令列表
     * 
     * @return
     * @see
     * @since 1.0
     */
    public boolean publishMQDayHandleRepo(){
        Map<String, Object> mqMap = new HashMap<String, Object>();
        //mqMap.put("sendType", WebSocketSendType.广播.getValue());
        messageSenderService.pushCommandMsgToMQ(JsonUtils.object2Json(mqMap));
        boolean flag = true;
        if (!flag) {
            log.info("MQ发布失败！");
        }
        
        return flag;
    }

    /**
     * 根据当前登录用户查询对应的交易员id<br/>
     * <功能详细描述>
     * @param param 参数
     * @return 对应的交易员id
     * @see
     * @since 1.0
     */
    public List<SysUserAdd> queryLoginTraderId(Map<String, Object> param) {
        return cfetsQuotedCommonMapper.queryLoginTraderId(param);
    }
    
    /**
     * 根据组合代码和本方机构查询账户来源
     * 
     * @param param 入参
     * @return 账户来源
     */
    public String querySecAcctSourceByPtlIdPartyId(Map<String, Object> param) {
    	return cfetsQuotedCommonMapper.querySecAcctSourceByPtlIdPartyId(param);
    }
    
    /**
     * 委托已报额度查询
     * 
     * @param param 入参
     * @return 账户来源
     */
    public BigDecimal queryUsedShareAmt(Map<String, Object> param) {
    	return cfetsQuotedCommonMapper.queryUsedShareAmt(param);
    }

    /**
     *  查找组合对应的证券账户
     * @author zhangnian
     * @date 2021/1/13 0013
     * @return
    */
    public String qryPortfolioSecAcctId(Map<String,Object> map){
        return cfetsQuotedCommonMapper.qryPortfolioSecAcctId(map);
    }
    
    /**
     * 根据查询查询符合条件的指令编号
     * 
     * @param map 入参
     * @return 报价编号
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryCfetsEntrustIdList(int page, int length, Map<String, Object> param){
        return this.queryForPageForSelectReturn(page, length,()->cfetsQuotedCommonMapper.queryCfetsEntrustIdList(param));
    }
    
    // add by etliu 20210122 start
    /**
     * 根据对手银行间机构，21位编号查询系统交易对手id
     * @param cpPartyId 对手银行间机构，21位编号
     * @return 系统交易对手id
     */
    public String selectCounterIdByCpPartyId(String cpPartyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("cpPartyId", cpPartyId);
        return cfetsQuotedCommonMapper.selectCounterIdByCpPartyId(map);
    }
    // add by etliu 20210122 end
    
    /**
     * 交易撤销时，恢复报价可用头寸
     * @param map 参数:成交编号
     * @throws Exception
     */
    public void recoveryQuotePositionData(Map<String, Object> map) throws Exception {
        String execId = (String) map.get("execId");
        if (StringUtil.isBlank(execId)) {
            return;
        }
        
        // 根据成交编号查询报价信息
        List<IbuQuoteBond> ibuQuoteBondList = cfetsQuotedCommonMapper.selectQuoteInfoByExecId(map);
        if (StringUtil.isEmptyList(ibuQuoteBondList)) {
            return;
        }
        
        IbuQuoteBond ibuQuoteBond = ibuQuoteBondList.get(0);
        String quoteId = ibuQuoteBond.getQuoteId();
        
        if (StringUtil.equals(DictCodeList.报价系统区分类型_现券对话报价.getValue(),
            ibuQuoteBond.getQuoteType())) {
            BondDialogueQuoteDto bondDialogueQuoteDto = ibuMainService.getBondDialogueQuote(quoteId);
            IbuQuoteBondDto ibuQuoteBondDto = bondDialogueQuoteDto.getIbuQuoteBondDto();
            
            CfetsBondDealDialogueQuoteDto dto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
            dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
            dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
            dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
            dto.setFpriceAmt(ibuQuoteBondDto.getFpriceAmt());
            //券面总额【单位：万】
            dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
            dto.setPs(ibuQuoteBondDto.getPs());
            cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
        }
        else if (StringUtil.equals(DictCodeList.报价系统区分类型_现券请求报价.getValue(),
            ibuQuoteBond.getQuoteType())) {
            BondRequestQuoteDto bondRequestQuoteDto = ibuMainService.getBondRequestQuote(quoteId);
            IbuQuoteBondDto ibuQuoteBondDto = bondRequestQuoteDto.getIbuQuoteBondDto();
            
            CfetsBondDealDialogueQuoteDto dto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
            dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
            dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
            dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
            dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)));
            dto.setPs(ibuQuoteBondDto.getPs());
            cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
        }
        else {
            RepoDialogueQuoteDto repoDialogueQuoteDto = ibuMainService.getRepoDialogueQuote(quoteId);
            List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList();
            IbuQuoteCrepDto ibuQuoteCrepDto = repoDialogueQuoteDto.getIbuQuoteCrepDto();
            
            CfetsRepoDealQuotedPrice001Dto dto = new CfetsRepoDealQuotedPrice001Dto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteCrepDto);
            dto.setQuoteId(ibuQuoteCrepDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteCrepDto.getTradeStatus());
            dto.setTradeStatus(dto.getTradeStatus());
            dto.setPs(dto.getPs());
            if (null != ibuQuoteCrepPledgeDtoList && ibuQuoteCrepPledgeDtoList.size() > 0) {
                List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
                for (IbuQuoteCrepPledgeDto IbuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                    CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, IbuQuoteCrepPledgeDto);
                    cfetsRepoDealQuotedPrice003Dto.setBondCode(IbuQuoteCrepPledgeDto.getFinprodMarketId());
                    cfetsRepoDealQuotedPrice003Dto.setPledgePrin(IbuQuoteCrepPledgeDto.getPledgeAmt());
                    pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                }
                dto.setPledgeBondList(pledgeBondList);
            }
            cfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(dto);
        }
    }
    
    /**
     * 删除报价可用头寸
     * @param map 参数:成交编号
     * @throws Exception
     */
    public void deleteQuotePositionData(Map<String, Object> map) throws Exception {
        String execId = (String) map.get("execId");
        if (StringUtil.isBlank(execId)) {
            return;
        }

        // 根据成交编号查询报价信息
        List<IbuQuoteBond> ibuQuoteBondList = cfetsQuotedCommonMapper.selectQuoteInfoByExecId(map);
        if (StringUtil.isEmptyList(ibuQuoteBondList)) {
            return;
        }

        IbuQuoteBond ibuQuoteBond = ibuQuoteBondList.get(0);
        String quoteId = ibuQuoteBond.getQuoteId();
        
        cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(quoteId);
    }
    
 // add by etliu 20210129 start
    /**
     * 检查画面选择的成交编号是否存在对应的报价
     * 目前债券二级交易和回购交易画面会用到
     * 
     * @return 是否存在对应的报价
     * @see
     * @since 1.0
     */
    public String checkExecIdHasQuote(String execId) {
        // 根据成交编号查询报价信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("execId", execId);
        List<IbuQuoteBond> ibuQuoteBondList = cfetsQuotedCommonMapper.selectQuoteInfoByExecId(paramMap);
        if (StringUtil.isEmptyList(ibuQuoteBondList)) {
            return DictCodeList.是否_否.getValue();
        }

        return DictCodeList.是否_是.getValue();
    }
 // add by etliu 20210129 end

    public Page<SelectOptionDto> selectEntrustId(int page, int length,
                                                   Map<String, Object> param) {
        Page<SelectOptionDto> pageList = this.queryForPageForSelectReturn(page, length,
                () -> cfetsQuotedCommonMapper.selectEntrustId(param));
        return pageList;
    }

    /**
     * 是否可以操作报价
     * @param param
     * @return
     */
    public boolean isOpeQuotedPrice(Map<String,Object> param){
        return ibuQuoteMapper.isOpeQuotedPrice(param)>0?true:false;
    }

    public List<CfetsRepoDealQuotedPrice003Dto> listIbuQuoteEntCrepPledge(String quoteId){
        return cfetsRepoDealQuotedPrice001Mapper.listIbuQuoteEntCrepPledge(quoteId);
    }


    /**
     * 处理头寸组装(对话)
     * @param entity
     * @param flowStatus
     * @return
     */
    public Map<String,Object> prepareDialoguePosition(CfetsBondDealDialogueQuoteDto entity,String flowStatus,String decisionAct,String systemAct,String confirmOk){
        final Date settleDate =iPlatformDate.getSysDate();
        Map<String,Object> result = new HashMap<>();
        String ps = entity.getPs();
        result.put("interfaceType","Bond001");
        result.put("userId", null!=SecurityUtil.getLoginUser()?SecurityUtil.getLoginUser().getUserName():"system");
        result.put("totalTradeId",entity.getQuoteId());
        result.put("flowStatus",flowStatus);
        result.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        String balanceCheck = com.joyintech.tams.constant.DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        result.put("balanceCheck",balanceCheck);
        if(StringUtil.equals(ps,BussinessDictCodeList.交易方向_行权回售.getValue())){
            result.put("busiType", DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
        }else{
            result.put("busiType", DictCodeListExt3.头寸业务类型_现券交易.getValue());
        }
        result.put("busiAct","11");
        result.put("ps",ps);
        result.put("settleSpeed",entity.getSettleSpreed());
        result.put("tradePlace",BussinessDictCodeList.交易场所_银行间.getValue());
        result.put("systemAct",systemAct);
        result.put("decisionAct",decisionAct);
        result.put("cdate", DateUtil.format(settleDate));
        result.put("ccy",entity.getCcy());
        result.put("finprodId",entity.getFinprodId());
        result.put("bTotalTradeId",entity.getQuoteId());
        if(CollectionUtils.isNotEmpty(entity.getCfetsBondDealDialogueQuoteDtoList())){
            if(StringUtil.isEmpty(entity.getTradeMarket())){
                entity.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            }
            List<Map<String,Object>> ptlList = new ArrayList<>();
            for (com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto : entity.getCfetsBondDealDialogueQuoteDtoList()) {
                Map<String,Object> pltMap = new HashMap<>();
                String portfolioId = cfetsBondDealDialogueQuoteDto.getPortfolioId();
                pltMap.put("portfolioId", portfolioId);
                pltMap.put("tradeId",entity.getQuoteId());
                pltMap.put("etrId",cfetsBondDealDialogueQuoteDto.getEntrustId());
                pltMap.put("cashAcctId",cfetsQuotedCommonMapper.getACCTNO(portfolioId,BussinessDictCodeList.资金账户类型_托管账户.getValue()));
                pltMap.put("secAcctId",cfetsQuotedCommonMapper.getSecAcctId(portfolioId,entity.getFinprodId(),entity.getTradeMarket()));
                pltMap.put("secAmount", ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000))); //券面总额
                pltMap.put("cashAmount",cfetsBondDealDialogueQuoteDto.getFpriceAmt().setScale(4, RoundingMode.DOWN));
                if(StringUtil.equals(ps,BussinessDictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps,BussinessDictCodeList.交易方向_行权回售.getValue())){
                    pltMap.put("integrateSecAmount",cfetsBondDealDialogueQuoteDto.getLendinShareAmt());
                    pltMap.put("ownSecAmount",cfetsBondDealDialogueQuoteDto.getOurShareAmt());
                }
                pltMap.put("etrSecAmount",ArithmeticUtil.multiply(etrEntrustMapper.selectByPrimaryKey(cfetsBondDealDialogueQuoteDto.getEntrustId()).getEntrustNum(),new BigDecimal(100)));
                ptlList.add(pltMap);
            }
            result.put("ptlList",ptlList);
        }
        return result;
    }

    /**
     * 处理头寸组装
     * @param entity
     * @param flowStatus
     * @return
     */
    public Map<String,Object> prepareRequestPosition(CfetsBondDealRequestQuoteDto entity, String flowStatus, String decisionAct, String systemAct,String confirmOk){
        final Date settleDate =iPlatformDate.getSysDate();
        Map<String,Object> result = new HashMap<>();
        String ps = entity.getPs();
        result.put("interfaceType","Bond001");
        result.put("userId", null!=SecurityUtil.getLoginUser()?SecurityUtil.getLoginUser().getUserName():"system");
        result.put("totalTradeId",entity.getQuoteId());
        result.put("flowStatus",flowStatus);
        result.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        String balanceCheck = com.joyintech.tams.constant.DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        result.put("balanceCheck",balanceCheck);
        if(StringUtil.equals(ps,BussinessDictCodeList.交易方向_行权回售.getValue())){
            result.put("busiType",DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
        }else{
            result.put("busiType", DictCodeListExt3.头寸业务类型_现券交易.getValue());
        }
        result.put("busiAct","12");
        result.put("ps",ps);
        result.put("settleSpeed",entity.getSettleSpreed());
        result.put("tradePlace",BussinessDictCodeList.交易场所_银行间.getValue());
        result.put("systemAct",systemAct);
        result.put("decisionAct",decisionAct);
        result.put("cdate",DateUtil.format(settleDate));
        result.put("ccy",entity.getCcy());
        result.put("finprodId",entity.getFinprodId());
        result.put("bTotalTradeId",entity.getQuoteId());
        if(CollectionUtils.isNotEmpty(entity.getCfetsBondDealRequestQuoteDtoList())){
            if(StringUtil.isEmpty(entity.getTradeMarket())){
                entity.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            }
            //成交按回复方的价格计算现金
            if(CollectionUtils.isNotEmpty(entity.getCounterInfoList())){
                com.joyintech.service.bankuplink.bond.dto.CounterInfoDto counterInfoDto = entity.getCounterInfoList().get(0);
                if(null!=counterInfoDto.getUnitFprice()&&counterInfoDto.getUnitFprice().compareTo(BigDecimal.ZERO)!=0){
                    entity.setUnitFprice(counterInfoDto.getUnitFprice());
                }
            }
            List<Map<String,Object>> ptlList = new ArrayList<>();
            for (com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto cfetsBondDealDialogueQuoteDto : entity.getCfetsBondDealRequestQuoteDtoList()) {
                Map<String,Object> pltMap = new HashMap<>();
                String portfolioId = cfetsBondDealDialogueQuoteDto.getPortfolioId();
                pltMap.put("portfolioId", portfolioId);
                pltMap.put("tradeId",entity.getQuoteId());
                pltMap.put("etrId",cfetsBondDealDialogueQuoteDto.getEntrustId());
                pltMap.put("cashAcctId",cfetsQuotedCommonMapper.getACCTNO(portfolioId,BussinessDictCodeList.资金账户类型_托管账户.getValue()));
                pltMap.put("secAcctId",cfetsQuotedCommonMapper.getSecAcctId(portfolioId,entity.getFinprodId(),entity.getTradeMarket()));
                pltMap.put("secAmount",ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000))); //券面总额
                if(null!=cfetsBondDealDialogueQuoteDto.getFpriceAmt()&&BigDecimal.ZERO.compareTo(cfetsBondDealDialogueQuoteDto.getFpriceAmt())!=0){
                    pltMap.put("cashAmount",cfetsBondDealDialogueQuoteDto.getFpriceAmt().setScale(4,RoundingMode.DOWN));//全价总额
                }else{
                    if(null!=entity.getUnitFprice()&&BigDecimal.ZERO.compareTo(entity.getUnitFprice())!=0){
                        pltMap.put("cashAmount",ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),entity.getUnitFprice(),new BigDecimal(100)).setScale(4,RoundingMode.DOWN));
                    }else{
                        pltMap.put("cashAmount",ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000)).setScale(4,RoundingMode.DOWN));
                    }
                }
                if(StringUtil.equals(ps,BussinessDictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps,BussinessDictCodeList.交易方向_行权回售.getValue())){
                    pltMap.put("integrateSecAmount",cfetsBondDealDialogueQuoteDto.getLendinShareAmt());
                    pltMap.put("ownSecAmount",cfetsBondDealDialogueQuoteDto.getOurShareAmt());
                }
                pltMap.put("etrSecAmount",ArithmeticUtil.multiply(etrEntrustMapper.selectByPrimaryKey(cfetsBondDealDialogueQuoteDto.getEntrustId()).getEntrustNum(),new BigDecimal(100)));
                ptlList.add(pltMap);
            }
            result.put("ptlList",ptlList);
        }
        return result;
    }

    /**
     * 质押券列表 按委托+组合的交易金额比例拆分
     *
     * @param cfetsRepoDealQuotedPrice001Dto
     * @return java.util.List<com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEntCrepPledgeDto>
     * @author jiangpeng
     * @date 2021/12/28 20:19
     */
    public List<CfetsRepoDealQuotedPrice003Dto> 质押券拆分(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        List<CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrice003Dtos = new ArrayList<>();
        CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto = new CfetsRepoDealQuotedPriceDto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPriceDto, cfetsRepoDealQuotedPrice001Dto);
        List<CfetsRepoDealQuotedPrice003Dto> pledgeBondLists = cfetsRepoDealQuotedPrice001Dto.getPledgeBondList();
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
                    CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, item);
                    cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                    cfetsRepoDealQuotedPrice003Dtos.add(cfetsRepoDealQuotedPrice003Dto);
                });
            }
        }
        return cfetsRepoDealQuotedPrice003Dtos;
    }

    /**
     * 头寸处理
     *
     * @param entity   质押式回购对话信息
     * @param isSubmit 是否提交
     * @author jiangpeng
     * @date 2021/12/28 20:49
     */
    public void sendPositionMethod(CfetsRepoDealQuotedPrice001Dto entity, String flowStatus, String decisionAct, String systemAct, String confirmOk) {
        List<CfetsIbRepoPosPortfolioCashRequest> ptlCashList = new ArrayList<>();
        List<CfetsIbRepoPosPortfolioSecRequest> ptlSecList = new ArrayList<>();

        for (PtlAllocationDto ptlDto : entity.getPtlAllocationList()) {
            //调用头寸
            CfetsIbRepoPosPortfolioCashRequest posport = new CfetsIbRepoPosPortfolioCashRequest();
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
        List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = this.质押券拆分(entity);
        if (StringUtil.isNotEmpty(pledgeBondList)) {
            for (CfetsRepoDealQuotedPrice003Dto pledgeDto : pledgeBondList) {
                CfetsIbRepoPosPortfolioSecRequest secport = new CfetsIbRepoPosPortfolioSecRequest();
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
        CfetsIbRepoPositionRequest req = new CfetsIbRepoPositionRequest();
        req.setInterfaceType("REPO001");
        req.setUserId(null != SecurityUtil.getLoginUser() ? SecurityUtil.getLoginUser().getUserName() : "system");
        req.setTotalTradeId(entity.getQuoteId());
        req.setFlowStatus(flowStatus);
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        req.setWorkFlowId(entity.getWorkflowId());
        req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
        String balanceCheck = com.joyintech.tams.constant.DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), com.joyintech.tams.constant.DictCodeList.是否_是.getValue()))
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
        req.setCdate(DateUtil.format(entity.getTradeDate()));
        req.setCcy(com.joyintech.tams.constant.DictCodeList.币种_人民币_CNY.getValue());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        PositionHttpRequest.send(req);
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
        return cfetsQuotedCommonMapper.qryPortfolioCashAcctId(map);
    }

}
