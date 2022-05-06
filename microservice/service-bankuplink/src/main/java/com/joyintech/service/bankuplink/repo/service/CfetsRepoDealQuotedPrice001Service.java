/*
 * 文件名：CfetsRepoDealQuotedPriceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yufei
 * 修改时间：2020年5月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.repo.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.common.dao.CfetsQuotedCommonMapper;
import com.joyintech.service.bankuplink.ibu.dto.RepoQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.service.bankuplink.repo.dao.CfetsRepoDealQuotedPrice001Mapper;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.common.util.QuotaPageModelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author yufei
 * @version 1.0 2020年5月14日
 * @see CfetsRepoDealQuotedPriceService
 * @since 1.0
 */
@Service("backCfetsRepoDealQuotedPrice001Service")
public class CfetsRepoDealQuotedPrice001Service extends BaseService {

    /**
     * {@linkplain}
     */
    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsRepoDealQuotedPrice001Mapper cfetsRepoDealQuotedPrice001Mapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonMapper cfetsQuotedCommonMapper;
    
    
    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    
    
	/**
	 * 组合可用持仓和实际持仓查询更新
	 */
	@Autowired(required = false)
	private BasePtlSecPositionInfoService basePtlSecPositionInfoService;
	
	
	/**
	 * 组合可用现金和实际现金查询更新
	 */
	@Autowired(required = false)
	private BasePtlCashPositionInfoService basePtlCashPositionInfoService;
    
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuMainService ibuMainService;
    
    /**
     * 委托指令查询
     * 
     * @param pageParam 入参
     * @return 委托指令
     * @see
     * @since 1.0
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
        return cfetsRepoDealQuotedPrice001Mapper.queryEntrustOrderList(param);
    }

    /**
     * 正回购可质押查询
     * 
     * @param length 条数
     * @param page 页数
     * @param pageParam 入参
     * @return 可质押列表
     * @see
     * @since 1.0
     */
    public Page<CfetsRepoDealQuotedPrice003Dto> queryPledgeBondList(int page, int length,
                                                                    Map<String, Object> param) {
        param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
        param.put("finprodTypeTgF01", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        param.put("positionType01", BussinessDictCodeList.持仓类型_可用.getValue());
        param.put("positionType02", BussinessDictCodeList.持仓类型_在途.getValue());
        param.put("positionType03", BussinessDictCodeList.持仓类型_实际.getValue());
        param.put("positionType04", BussinessDictCodeList.持仓类型_质押.getValue());
        param.put("positionType05", BussinessDictCodeList.持仓类型_融入.getValue());
        param.put("positionType06", BussinessDictCodeList.持仓类型_融出.getValue());
        param.put("positionType07", BussinessDictCodeList.持仓类型_卖空.getValue());
        param.put("ps03", BussinessDictCodeList.回购拆借方向_正回购.getValue());
        param.put("ps04", BussinessDictCodeList.回购拆借方向_逆回购.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
        param.put("tradeStatus15", BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
        param.put("tradeStatus06", BussinessDictCodeList.银行间上行报价状态_已撤回.getValue());
        param.put("tradeStatus08", BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
        param.put("tradeStatus13", BussinessDictCodeList.银行间上行报价状态_已过期.getValue());
        param.put("valueSourceCBC", BussinessDictCodeList.估值来源_中债登.getValue());
        Page<CfetsRepoDealQuotedPrice003Dto> pageList = super.queryForPage(page, length,
            () -> cfetsRepoDealQuotedPrice001Mapper.queryPledgeBondList(param));
        return pageList;
    }
    
    /**
     * 质押券明细列表查询
     * 
     * @param length 条数
     * @param page 页数
     * @param pageParam 入参
     * @return 质押券明细列表
     * @see
     * @since 1.0
     */
    public Page<CfetsRepoDealQuotedPrice003Dto> getPledgeBondList(int page, int length,
                                                                    Map<String, Object> param) {
        Page<CfetsRepoDealQuotedPrice003Dto> pageList = super.queryForPage(page, length,
            () -> cfetsRepoDealQuotedPrice001Mapper.getPledgeBondList(param));
        return pageList;
    }

    /**
     * 逆回购可质押查询
     * 
     * @param length 条数
     * @param page 页数
     * @param pageParam 入参
     * @return 可质押列表
     * @see
     * @since 1.0
     */
    public Page<CfetsRepoDealQuotedPrice003Dto> queryDefyPledgeBondList(int page, int length,
                                                                        Map<String, Object> param) {
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("FINPROD_TYPE_TG_F01", BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        param.put("valueSourceCBC", BussinessDictCodeList.估值来源_中债登.getValue());
        Page<CfetsRepoDealQuotedPrice003Dto> pageList = super.queryForPage(page, length,
            () -> cfetsRepoDealQuotedPrice001Mapper.queryDefyPledgeBondList(param));
        return pageList;
    }

    /**
     * 根据查询查询符合条件的委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryEntrustInfoList(int page, int length,
                                                      Map<String, Object> param) {
        param.put("FINPROD_TYPE2_Z", BussinessDictCodeList.金融产品类型_债券质押式正回购.getValue());
        param.put("FINPROD_TYPE2_N", BussinessDictCodeList.金融产品类型_债券质押式逆回购.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsRepoDealQuotedPrice001Mapper.queryEntrustInfoList(param));
    }

    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add start
    /**
     * 根据查询查询符合条件的历史委托列表
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryHisEntrustInfoList(int page, int length,
                                                      Map<String, Object> param) {
        param.put("FINPROD_TYPE2_Z", BussinessDictCodeList.金融产品类型_债券质押式正回购.getValue());
        param.put("FINPROD_TYPE2_N", BussinessDictCodeList.金融产品类型_债券质押式逆回购.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        return this.queryForPageForSelectReturn(page, length,
                () -> cfetsRepoDealQuotedPrice001Mapper.queryHisEntrustInfoList(param));
    }
    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add end

    /**
     * 质押券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        // 调用银行间上行组件处理
        ibuComponentCommonService.insertRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);

        return true;
    }
    
    /**
     * 质押券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertWflRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        // 调用银行间上行组件处理
        ibuComponentCommonService.insertWflRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);
        return true;
    }

    /**
     * 质押券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendNewRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        // 调用银行间上行组件处理
        ibuComponentCommonService.sendNewRepoDealQuote(cfetsRepoDealQuotedPrice001Dto);
        //add by chenjiaxing 证券账户入库 20210901 start

        //add by chenjiaxing 证券账户入库 20210901 end

        return true;
    }

    /**
     * 质押券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<RepoQuoteInfoDto> getDayOfferTableList(Map<String, Object> param) {
        List<RepoQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        List<RepoQuoteExtDto> bondQuoteExtDtoList = ibuComponentCommonService.getRepoDayOfferTableList(
            param);
        bondQuoteExtDtoList.forEach(x -> {
            bondQuoteInfoDtoList.add(this.dtoConvert(x));
        });

        return bondQuoteInfoDtoList;
    }

    /**
     * 质押券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     *
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteInfoDto> getRepoDayOfferTablePage(Map<String, Object> param) {
        com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteInfoDto> repoQuoteInfoDtoPage
                = new com.joyintech.background.component.common.pack.common.grid.Page<>();
        List<RepoQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteExtDto> page = ibuComponentCommonService.getRepoDayOfferTablePage(
                param);
        List<RepoQuoteExtDto> bondQuoteExtDtoList = page.getRoot();
        bondQuoteExtDtoList.forEach(x -> {
            bondQuoteInfoDtoList.add(this.dtoConvert(x));
        });
        BeanUtil.copyPropertiesIgnoreNull(repoQuoteInfoDtoPage,page);
        repoQuoteInfoDtoPage.setRoot(bondQuoteInfoDtoList);

        return repoQuoteInfoDtoPage;
    }

    /**
     * 实体类属性转换
     * 
     * @param bondQuoteExtDto 组件返回dto
     * @return 画面显示dto
     * @see
     * @since 1.0
     */
    private RepoQuoteInfoDto dtoConvert(RepoQuoteExtDto bondQuoteExtDto) {
        RepoQuoteInfoDto bondQuoteInfoDto = new RepoQuoteInfoDto();
        bondQuoteInfoDto.setEntrustId(bondQuoteExtDto.getEntrustId());
        bondQuoteInfoDto.setQuoteId(bondQuoteExtDto.getQuoteId());
        bondQuoteInfoDto.setQuoteRoleType(bondQuoteExtDto.getQuoteRoleType());
        bondQuoteInfoDto.setPs(bondQuoteExtDto.getRepoPs());
        bondQuoteInfoDto.setQuoteType(bondQuoteExtDto.getQuoteType());
        bondQuoteInfoDto.setTradeStatus(bondQuoteExtDto.getTradeStatus());
        bondQuoteInfoDto.setSendStatus(bondQuoteExtDto.getSendStatus());
        bondQuoteInfoDto.setOuPartyId(bondQuoteExtDto.getOuPartyId());
        bondQuoteInfoDto.setOuTraderId(bondQuoteExtDto.getOuTraderId());
        bondQuoteInfoDto.setCpPartyId(
            bondQuoteExtDto.getCpPartyId() != null ? bondQuoteExtDto.getCpPartyId() : null);
        bondQuoteInfoDto.setCpTrader(
            bondQuoteExtDto.getCpTraderId() != null ? bondQuoteExtDto.getCpTraderId() : null);
        // 本方交易员姓名，对手方交易员姓名,本方机构名称,对手方机构名称
        bondQuoteInfoDto.setOuTraderName(bondQuoteExtDto.getOuTraderName());
        bondQuoteInfoDto.setOuPartyName(bondQuoteExtDto.getOuPartyName());
        bondQuoteInfoDto.setCpTraderName(bondQuoteExtDto.getCpTraderName());
        bondQuoteInfoDto.setCpPartyName(bondQuoteExtDto.getCpPartyName());
        bondQuoteInfoDto.setRate(
            ArithmeticUtil.multiply(bondQuoteExtDto.getRate(), new BigDecimal(100)));
        bondQuoteInfoDto.setFpriceAmt(bondQuoteExtDto.getFpriceAmt());
        bondQuoteInfoDto.setShareAmt(bondQuoteExtDto.getShareAmt());
        bondQuoteInfoDto.setTerm(
            bondQuoteExtDto.getTerm() == null ? 0L : bondQuoteExtDto.getTerm());
        bondQuoteInfoDto.setvSettleDate(bondQuoteExtDto.getvSettleDate());
        bondQuoteInfoDto.setmSettleDate(bondQuoteExtDto.getmSettleDate());
        bondQuoteInfoDto.setTermType(bondQuoteExtDto.getTermType());// 期限品种
        bondQuoteInfoDto.setSettleSpeed(bondQuoteExtDto.getSettleSpeed());
        bondQuoteInfoDto.setCfetsQuoteId(bondQuoteExtDto.getCfetsQuoteId());
        bondQuoteInfoDto.setfTradeId(bondQuoteExtDto.getExecid());
        bondQuoteInfoDto.setValidStatus(bondQuoteExtDto.getValidStatus());
        bondQuoteInfoDto.setWorkflowId(bondQuoteExtDto.getWorkflowId());
        bondQuoteInfoDto.setIsDzh(bondQuoteExtDto.getIsDzh());
        bondQuoteInfoDto.setIsDzh(bondQuoteExtDto.getCfetsQuoteId());
        return bondQuoteInfoDto;
    }

    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<RepoQuoteInfoDto> getOrderOfferTableList(Map<String, Object> param) {
        List<RepoQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        List<RepoQuoteExtDto> bondQuoteExtDtoList = ibuComponentCommonService.getRepoOrderOfferTableList(
            param);
        bondQuoteExtDtoList.forEach(x -> {
            bondQuoteInfoDtoList.add(this.dtoConvert(x));
        });

        return bondQuoteInfoDtoList;
    }

    /**
     * 根据quoteId查询对话报价信息<br/>
     * <功能详细描述>
     * 
     * @param param 查询参数
     * @return 对话报价信息
     * @see
     * @since 1.0
     */
    public CfetsRepoDealQuotedPrice001Dto queryEntrustInfoByQuoteId(Map<String, Object> param) {
        String quoteId = (String)param.get("quoteId");
        CfetsRepoDealQuotedPrice001Dto dto = null;
        if (StringUtil.isNotEmpty(quoteId)) {
            dto = ibuComponentCommonService.queryRepoDialogueQuoteByQuoteId(quoteId);
            //质押式回购报价历史
            dto.setMySideTraderName(cfetsRepoDealQuotedPrice001Mapper.getTraderName(dto.getMySideTrader()));
            dto.setCounterTraderName(cfetsRepoDealQuotedPrice001Mapper.getTraderName(dto.getCounterTrader()));
            Map<String, Object> map = new HashMap<>();
            map.put("quoteId", (String)param.get("quoteId"));
            map.put("EXE_TYPE_IBU_03", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue());
            map.put("EXE_TYPE_IBU_07", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue());
            map.put("EXE_TYPE_IBU_67", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue());
            map.put("EXE_TYPE_IBU_63", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue());
            map.put("EXE_TYPE_IBU_25", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈修改.getValue());
            map.put("EXE_TYPE_IBU_24", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方交谈新增.getValue());
            map.put("EXE_TYPE_IBU_19", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价新增.getValue());
            map.put("EXE_TYPE_IBU_20", com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_对手方报价修改.getValue());
            List<CfetsRepoDealQuotedPrice001Dto> list = cfetsRepoDealQuotedPrice001Mapper.queryCfetsRepoHis(map);
            if(StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue())
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
        return dto;
    }
    
    /**
     * 流程中查询报价对话历史
     * @param dto
     * @return
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
        List<CfetsRepoDealQuotedPrice001Dto> list = cfetsRepoDealQuotedPrice001Mapper.queryCfetsRepoHis(map);
        String msg = getDialogueRecord(list);
        dto.setDialogueRecord(msg);
        return dto;
    }
    
    public String getDialogueRecord(List<CfetsRepoDealQuotedPrice001Dto> list) {
    	if(!StringUtil.isEmptyList(list)) {
        	//交谈记录
        	String msg = "";
        	if(StringUtil.isNotEmpty(list) && list.size() > 1) {
        		for(int j=list.size()-1;j>0;j--) {
        			String rowMsg = "";
        			//质押券提示记录
                	String pledgeBondMsg = "";
        			int i = j - 1;
        			//质押券对比
        			if(!StringUtil.isEmptyList(list.get(i).getPledgeBondList())) {
        				Map<String, CfetsRepoDealQuotedPrice003Dto> map_i = list.get(i).getPledgeBondList().stream().
        						collect(Collectors.toMap(CfetsRepoDealQuotedPrice003Dto::getFinprodId, e -> e));
        				Map<String, CfetsRepoDealQuotedPrice003Dto> map_j = list.get(j).getPledgeBondList().stream().
        						collect(Collectors.toMap(CfetsRepoDealQuotedPrice003Dto::getFinprodId, e -> e));
        				if(StringUtil.isEmptyList(list.get(j).getPledgeBondList())) {
        					for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(i).getPledgeBondList()) {
        						if("".equals(pledgeBondMsg)) {
									pledgeBondMsg += "修改" + handleCss("质押券明细");
								}
        						pledgeBondMsg += "删除债券 "+handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName())+";";
        					}
        				}else {
        					for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(j).getPledgeBondList()) {
        						CfetsRepoDealQuotedPrice003Dto temp = map_i.get(cfetsRepoDealQuotedPrice003Dto.getFinprodId());
        						if(temp == null) {
        							if("".equals(pledgeBondMsg)) {
										pledgeBondMsg += "修改" + handleCss("质押券明细");
									}
        							pledgeBondMsg += "增加债券 "+handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName())
											+ "券面总额(万元) " + handleCss(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)).toString())+","
											+ "折算比例(%) " + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString())+";";
        						}else {
        							//修改质押券字段对比
        							Boolean flag = true;
        							if(temp.getPledgeRate().compareTo(cfetsRepoDealQuotedPrice003Dto.getPledgeRate()) != 0) {
        								if("".equals(pledgeBondMsg)) {
    										pledgeBondMsg += "修改" + handleCss("质押券明细");
    									}
        								if(flag) {
        									pledgeBondMsg += "修改债券" + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName());
        									flag = false;
        								}
        								pledgeBondMsg += ",将"+handleCss("折算比例(%)") + "从" + handleCss(temp.getPledgeRate().toString()) + "修改为" +handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString());
        							}
                                    if(temp.getPledgePrin().compareTo(cfetsRepoDealQuotedPrice003Dto.getPledgePrin()) != 0) {
                                        if("".equals(pledgeBondMsg)) {
                                            pledgeBondMsg += "修改" + handleCss("质押券明细");
                                        }
                                        if(flag) {
                                            pledgeBondMsg += "修改债券" + handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName());
                                        }
                                        pledgeBondMsg += ",将"+handleCss("券面总额(万元)") + "从" + handleCss(temp.getPledgePrin().divide(new BigDecimal(10000)).toString()) + "修改为" +handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgePrin().divide(new BigDecimal(10000)).toString())+";";
                                    }
        							if(!"".equals(pledgeBondMsg)) {
        								pledgeBondMsg += ";";
        							}
        						}
        					}
        					for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(i).getPledgeBondList()) {
        						CfetsRepoDealQuotedPrice003Dto temp = map_j.get(cfetsRepoDealQuotedPrice003Dto.getFinprodId());
        						if(temp == null) {
        							if("".equals(pledgeBondMsg)) {
										pledgeBondMsg += "修改" + handleCss("质押券明细");
									}
        							pledgeBondMsg += "删除债券 "+handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName())+";";
        						}
        					}
						}
        			}else {
						if(!StringUtil.isEmptyList(list.get(j).getPledgeBondList())) {
							for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : list.get(j).getPledgeBondList()) {
								if("".equals(pledgeBondMsg)) {
									pledgeBondMsg += "修改"+handleCss("质押券明细");
								}
								pledgeBondMsg += "增加债券"+handleCss(cfetsRepoDealQuotedPrice003Dto.getBondAbbrName())+"</span>"
										+ "券面总额(万元) " + handleCss(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)).toString())+","
										+ "折算比例(%) " + handleCss(cfetsRepoDealQuotedPrice003Dto.getPledgeRate().toString());
							}
						}
					}
        			if("".equals(rowMsg) && !"".equals(pledgeBondMsg)) {
        				rowMsg = handleMsg(rowMsg,list.get(j));
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
        			if(!StringUtil.equals(String.valueOf(list.get(i).getTerm()), String.valueOf(list.get(j).getTerm()))) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("回购期限")+"从"+handleCss(String.valueOf(list.get(i).getTerm())) + "修改为"+handleCss(String.valueOf(list.get(j).getTerm())) + ";";
        			}
        			//回购利率
        			if(list.get(i).getPrice().compareTo(list.get(j).getPrice()) != 0) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("回购利率(%)")+"从"+handleCss(String.valueOf(list.get(i).getPrice())) + "修改为"+handleCss(String.valueOf(list.get(j).getPrice())) + ";";
        			}
        			//清算速度
        			String spreed_i = list.get(i).getSettleSpreed();
    				if(StringUtil.equals(list.get(i).getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())
    						|| StringUtil.equals(list.get(i).getSettleSpreed(), "0")) {
    					spreed_i=BussinessDictCodeList.清算速度_T加0.getCodeName();
    				}else if(StringUtil.equals(list.get(i).getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())
    						|| StringUtil.equals(list.get(i).getSettleSpreed(), "1")) {
    					spreed_i=BussinessDictCodeList.清算速度_T加1.getCodeName();
    				}
    				String spreed_j = list.get(j).getSettleSpreed();
    				if(StringUtil.equals(list.get(j).getSettleSpreed(), BussinessDictCodeList.清算速度_T加0.getValue())
    						|| StringUtil.equals(list.get(j).getSettleSpreed(), "0")) {
    					spreed_j=BussinessDictCodeList.清算速度_T加0.getCodeName();
    				}else if(StringUtil.equals(list.get(j).getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())
    						||StringUtil.equals(list.get(j).getSettleSpreed(), "1")) {
    					spreed_j=BussinessDictCodeList.清算速度_T加1.getCodeName();
    				}
        			if(!StringUtil.equals(spreed_i, spreed_j)) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("清算速度")+"从"+handleCss(spreed_i) + "修改为"+handleCss(spreed_j) + ";";
        			}
        			//首次结算方式
        			if(!StringUtil.equals(list.get(i).getvDeliveryType(), list.get(j).getvDeliveryType())) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("首次结算方式")+"从"+handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式,list.get(i).getvDeliveryType())) + "修改为"+handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式,list.get(j).getvDeliveryType())) + ";";
        			}
        			//到期结算方式
        			if(!StringUtil.equals(list.get(i).getmDeliveryType(), list.get(j).getmDeliveryType())) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("到期结算方式")+"从"+handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式, list.get(i).getmDeliveryType())) + "修改为"+handleCss(SysDataUtil.getNameFromDataDict(BussinessDictCode.结算方式,list.get(j).getmDeliveryType())) + ";";
        			}
        			//交易金额
        			if(!StringUtil.equals(list.get(i).getDealAmt().toString(), list.get(j).getDealAmt().toString())) {
        				if("".equals(rowMsg)) {
        					rowMsg = handleMsg(rowMsg,list.get(j));
        				}
        				rowMsg += "将"+handleCss("交易金额")+"从"+handleCss(String.valueOf(list.get(i).getDealAmt())) + "修改为"+handleCss(String.valueOf(list.get(j).getDealAmt())) + ";";
        			}
        			//券面总额合计
        			if(list.get(i).getPledgePrinTotal() != null && list.get(j).getPledgePrinTotal() != null) {
	        			if(list.get(i).getPledgePrinTotal().compareTo(list.get(j).getPledgePrinTotal()) != 0) {
	        				if("".equals(rowMsg)) {
	        					rowMsg = handleMsg(rowMsg,list.get(j));
	        				}
	        				rowMsg += "将"+handleCss("券面总额合计(万元)")+"从"+handleCss(String.valueOf(list.get(i).getPledgePrinTotal().divide(new BigDecimal(10000)))) + "修改为"+handleCss(String.valueOf(list.get(j).getPledgePrinTotal().divide(new BigDecimal(10000)))) + ";";
	        			}
        			}
        			if(!"".equals(rowMsg)) {
        				rowMsg += "</br>";
    				}
        			msg += rowMsg;
        		}
        	}
        	msg = HtmlUtils.htmlEscape(msg);
        	return msg;
        }
    	return "";
    	
    }
    
    /**
     * css字体加红
     * @param msg
     * @return
     */
    public String handleCss(String msg) {
    	return "<span style='color:red'>"+msg+"</span>";
    }
    
    public String handleMsg(String msg,CfetsRepoDealQuotedPrice001Dto dto) {
    	String date = DateUtil.format(dto.getUpdateTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
		msg += date + " ";
		if(StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方新增发送成功.getValue())
				|| StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方修改发送成功.getValue())
				|| StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue())
				|| StringUtil.equals(dto.getExeType(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行操作类型_本方交谈发送成功.getValue())) {
			msg += dto.getMySideTraderName()+" ";
		}else {
			msg += dto.getCounterTraderName() + " ";
		}
		return msg;
    }

    /**
     * 撤销报价 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean revert(Map<String, Object> param) {
        ibuComponentCommonService.revert(param);
        return true;
    }

    /**
     * 成交 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean deal(Map<String, Object> param) {
        ibuComponentCommonService.deal(param);
        return true;
    }
    
    /**
     * 修改 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean modify(CfetsRepoDealQuotedPrice001Dto dto) {
        ibuComponentCommonService.modify(dto);
        return true;
    }

    /**
     * 拒绝 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean reject(Map<String, Object> param) {
        ibuComponentCommonService.reject(param);
        return true;
    }

    /**
     * 回退 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean restoreDayOff(Map<String, Object> param) {
        ibuComponentCommonService.restoreDayOff(param);
        CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = JSON.parseObject(JSON.toJSONString(param), CfetsRepoDealQuotedPrice001Dto.class);
        doInsetOrUpdateForPtlPositionHold(cfetsRepoDealQuotedPrice001Dto);
        return true;
    }

    /**
     * 删除 <功能详细描述>
     * 
     * @param param 参数
     * @return
     * @see
     * @since 1.0
     */
    public boolean deleteDayOff(Map<String, Object> param) {
        ibuComponentCommonService.deleteDayOff(param);
        //删除操作，根据句报价编号删除流水
        String quoteId = (String)param.get("quoteId");
        if(StringUtil.isNotEmpty(quoteId)){
            doDeletePtlPositionHoldByQuoteId(quoteId);
        }
        return true;
    }

    /**
     * 历史报价撤销
     * 
     * @param quoteId 报价代码
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public boolean doHisRevoke(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        QuoteResultDto quoteResultDto = ibuComponentCommonService.sendRepoDialogueQuoteCancel(
            cfetsRepoDealQuotedPrice001Dto.getQuoteId(), cfetsRepoDealQuotedPrice001Dto);
        //撤销删除流水
        doDeletePtlPositionHoldByQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
        return quoteResultDto.isStatus();
    }

    /**
     * 根据查询查询符合条件的报价编号
     * 
     * @param map 入参
     * @return 报价编号
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryCfetsQuoteIdList(int page, int length,
                                                  Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
            () -> cfetsRepoDealQuotedPrice001Mapper.queryCfetsQuoteIdList(param));
    }

    
    /**
	 * 根据报价编号删除
	 * @param quoteId
	 * @return
	 */
	public boolean doDeletePtlPositionHoldByQuoteId(String quoteId) {
		if (null != quoteId) {
			List<String> quoteIds = new ArrayList<String>();
			quoteIds.add(quoteId);
			baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(quoteIds);
			baseAssetCashPositionCommMapper.deletePositionByTradeId(quoteId);
		}
		return true;
	}
	
    
    /**
     * 
     * @param dto
     */
	public void doInsetOrUpdateForPtlPositionHold(CfetsRepoDealQuotedPrice001Dto dto) {
	    // T+1情况，可用持仓登在T+1日，可用现金登在T日
	    Date platDate = iPlatformDate.getSysDate();
	    final Date vSettleDate = dto.getvSettleDate() == null ? platDate : dto.getvSettleDate();
	    String ps = dto.getPs();
        String isDzh = dto.getIsDzh();
        String isMq = dto.getIsMq(); //是否mq  前台传过来 质押券没有拆分  mq推过来  质押券已经拆分 无需处理
		// 指令持仓流水 、现金流水处理
		if(StringUtils.isNotEmpty(dto.getQuoteId())){
            if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
			    doDeletePtlPositionHoldByQuoteId(dto.getQuoteId());
            }
			RepoDialogueQuoteDto repoDialogueQuoteDto = ibuMainService.getRepoDialogueQuote(dto.getQuoteId());
			if(null != repoDialogueQuoteDto && null != repoDialogueQuoteDto.getIbuQuoteCrepDto()){
                //组合代码为空默认为  回退到回复方初始化数据
                if(StringUtil.equals(dto.getQuotaPageModel(),QuotaPageModelUtil.MODEL_RESTOREDAYOFF)
                    &&StringUtil.isEmpty(repoDialogueQuoteDto.getIbuQuoteCrepDto().getPortfolioId())
                        &&!StringUtil.equals(isDzh,"Y")){
                    return;
                }
				BeanUtil.copyPropertiesIgnoreNull(dto,repoDialogueQuoteDto.getIbuQuoteCrepDto());
				//add by zn 20210112
				//退回时，回归持仓流水
				if(StringUtil.isEmpty(dto.getPs())){
				    dto.setPs(repoDialogueQuoteDto.getIbuQuoteCrepDto().getRepoPs());
                }
			}
            ps = dto.getPs();
			//多组合 正回购 时   质押券取拆分后的质押券 需要关联组合拆分
            List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = dto.getPledgeBondList();
            if(null == pledgeBondList){
                if(StringUtil.equals(isDzh,"Y")&&StringUtil.equals(ps,DictCodeListExt3.交易方向_正回购.getValue())){
                    if(null!=repoDialogueQuoteDto&&null!=repoDialogueQuoteDto.getIbuQuoteEntCrepPledgeDtoList()){
                        pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
                        for (IbuQuoteEntCrepPledgeDto ibuQuoteEntCrepPledgeDto : repoDialogueQuoteDto.getIbuQuoteEntCrepPledgeDtoList()) {
                            CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                            BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto,ibuQuoteEntCrepPledgeDto);
                            cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ArithmeticUtil.multiply(ibuQuoteEntCrepPledgeDto.getPledgeNumber(),new BigDecimal(100)));
                            cfetsRepoDealQuotedPrice003Dto.setFinprodId(ibuQuoteEntCrepPledgeDto.getPledgeFinprodId());
                            pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                        }
                        dto.setPledgeBondList(pledgeBondList);
                    }
                }else{
                    if(null != repoDialogueQuoteDto && null != repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList()){
                        pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
                        for(IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList()){
                            CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                            BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto,ibuQuoteCrepPledgeDto);
                            cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ibuQuoteCrepPledgeDto.getPledgeAmt());
                            //add by zn 20210112
                            //退回时，回归持仓流水
                            if(StringUtil.isEmpty(cfetsRepoDealQuotedPrice003Dto.getBondCode())){
                                cfetsRepoDealQuotedPrice003Dto.setBondCode(ibuQuoteCrepPledgeDto.getFinprodMarketId());
                            }
                            pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                            dto.setPledgeBondList(pledgeBondList);
                        }
                    }
                }
            }
			if(null!= repoDialogueQuoteDto && null!=repoDialogueQuoteDto.getIbuQuoteEtrReDtoList()){
                List<PtlAllocationDto> ptlAllocationList = dto.getPtlAllocationList();
                if(null == ptlAllocationList){
                    ptlAllocationList = new ArrayList<PtlAllocationDto>();
                    for (IbuQuoteEtrReDto ibuQuoteEtrReDto : repoDialogueQuoteDto.getIbuQuoteEtrReDtoList()) {
                        PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                        BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto,ibuQuoteEtrReDto);
                        ptlAllocationList.add(ptlAllocationDto);
                    }
                    dto.setPtlAllocationList(ptlAllocationList);
                }
            }
		}
		PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();// 组合持仓流水、组合现金流水
		List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();// 现金流水
		List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();// 持仓流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 指令持仓流水
        List<EtrCashPositionFlowDto> etrCashPositionFlowDtos = new ArrayList<>();// 指令现金流水
		BigDecimal factor = new BigDecimal(-1);
        if (!StringUtil.equals(dto.getQuotaPageModel(), QuotaPageModelUtil.MODEL_NEW)
                && StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
            factor = new BigDecimal(1);
        }
		if (DictCodeListExt3.交易方向_正回购.getValue().equals(ps)) {
			/********************** 持仓流水DTO构造 *************************************/
			List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = dto.getPledgeBondList();
            if(StringUtil.equals(isDzh,"Y")){
                if(StringUtil.equals(isMq,"Y")){
                    pledgeBondList = dto.getPledgeBondList();
                }else{
                    //是否mq  前台传过来 质押券没有拆分  mq推过来  质押券已经拆分 无需处理
                    List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = ibuComponentCommonService.质押券拆分(dto);
                    //重新构造 质押列表
                    List<CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrices = new ArrayList<>();
                    if(null!=ibuQuoteEntCrepPledges&&ibuQuoteEntCrepPledges.size()>0){
                        ibuQuoteEntCrepPledges.stream().forEach(item -> {
                            CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                            cfetsRepoDealQuotedPrice003Dto.setPortfolioId(item.getPortfolioId());
                            cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                            cfetsRepoDealQuotedPrice003Dto.setBondCode(item.getBondCode());
                            cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal(100)));
                            cfetsRepoDealQuotedPrice003Dto.setEntrustId(item.getEntrustId());
                            cfetsRepoDealQuotedPrices.add(cfetsRepoDealQuotedPrice003Dto);
                        });
                    }
                    pledgeBondList = cfetsRepoDealQuotedPrices;
                }
            }
			if(null != pledgeBondList && pledgeBondList.size() > 0){
                Map<String, List<PledgeBondInfoDto>> avaMap = new HashMap<>();
                //单组合外部处理
                if(!StringUtil.equals(isDzh,"Y")){
                    avaMap = handleInvAim(dto);
                }
				for(CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto : pledgeBondList){
                    if(StringUtil.equals(isDzh,"Y")){
                        dto.setPortfolioId(cfetsRepoDealQuotedPrice003Dto.getPortfolioId());
                        avaMap = handleInvAim(dto);
                    }
                    // 待分配投资目的的质押券面总额
                    BigDecimal pledgePrin = cfetsRepoDealQuotedPrice003Dto.getPledgePrin();
                    List<PledgeBondInfoDto> tempList = avaMap.get(cfetsRepoDealQuotedPrice003Dto.getBondCode());
                    if (!StringUtil.isEmptyList(tempList)) {
                        for (PledgeBondInfoDto avaBond : tempList) {
                            PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
                            // 当前投资目的可分配券面总额
                            BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                            // 当前投资目的决定分配的券面总额
                            BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;
                            ptlSecPositionFlowDtoUnit.setSeqno(getSeqNo());
                            ptlSecPositionFlowDtoUnit.setPortfolioId(dto.getPortfolioId());// 组合代码
                            ptlSecPositionFlowDtoUnit.setFinprodId(cfetsRepoDealQuotedPrice003Dto.getFinprodId());// 金融产品代码
                            ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
                            ptlSecPositionFlowDtoUnit.setSecAcctId("999999"); // 证券账户
                            //ptlSecPositionFlowDtoUnit.setInvAim(AllTradeCommonUtil.getInvAim(dto.getPortfolioId()));
                            // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                            ptlSecPositionFlowDtoUnit.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());
                            ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_质押.getValue());// 持仓类型
                            ptlSecPositionFlowDtoUnit.setCdate(platDate);// 日期
                            ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
                            ptlSecPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
                            
                            ptlSecPositionFlowDtoUnit.setAmount(ArithmeticUtil.divide(prin,new BigDecimal(100),2));// 数量
                            ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
                            ptlSecPositionFlowDtoUnit.setTradeId(dto.getQuoteId());// 成交编号
                            if (StringUtils.isNotEmpty(dto.getPortfolioId()) && StringUtils.isNotEmpty(cfetsRepoDealQuotedPrice003Dto.getFinprodId())) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("portfolioId", dto.getPortfolioId());
                                map.put("finprodId", cfetsRepoDealQuotedPrice003Dto.getFinprodId());
                                String secAcctId = cfetsQuotedCommonMapper.qryPortfolioSecAcctId(map);
                                if (StringUtils.isNotEmpty(secAcctId)) {
                                    ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId); // 证券账户
                                }
                            }
                            ptlSecPositionFlowDtoUnit.setEntrustId(cfetsRepoDealQuotedPrice003Dto.getEntrustId());
                            if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                                ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);
                            }
                            // 分配完毕后,停止可质押券信息（含投资目的）的循环
                            pledgePrin = pledgePrin.subtract(prin);
                            if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                                break;
                            }
                        }
                    }
				}
			}
			/*************************************************************************/
		} else if (DictCodeListExt3.交易方向_逆回购.getValue().equals(ps)) {
			/********************** 持仓流水DTO构造 *************************************/
			//多组合 逆回购 直接取分配列表
            if(StringUtil.equals(isDzh,"Y")){
                if(null!=dto.getPtlAllocationList()&&dto.getPtlAllocationList().size()>0){
                    for (PtlAllocationDto item :dto.getPtlAllocationList()){
                        CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
                        cfetsRepoDealQuotedPrice001Dto.setQuoteId(dto.getQuoteId());
                        cfetsRepoDealQuotedPrice001Dto.setPortfolioId(item.getPortfolioId());
                        cfetsRepoDealQuotedPrice001Dto.setCcy(dto.getCcy());
                        cfetsRepoDealQuotedPrice001Dto.setDealAmt(item.getFpriceAmt());
                        cfetsRepoDealQuotedPrice001Dto.setEntrustId(item.getEntrustId());
                        PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = 逆回购构造现金流水(cfetsRepoDealQuotedPrice001Dto,vSettleDate,factor);// 组合现金流水DTO
                        ptlCashPositionFlows.add(ptlCashPositionFlowDtoUnit);
                    }
                }
            }else{
                PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = 逆回购构造现金流水(dto,vSettleDate,factor);// 组合现金流水DTO
                ptlCashPositionFlows.add(ptlCashPositionFlowDtoUnit);
            }
		}
		ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
		ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);

        ptlPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> {
            etrCashPositionFlowDto.setCdate(vSettleDate);
            etrCashPositionFlowDto.setIsExistNew(dto.getIsExistNew());
        });
        ptlPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> {
            etrSecPositionFlowDto.setCdate(platDate);
            etrSecPositionFlowDto.setIsExistNew(dto.getIsExistNew());
            EtrSecPositionFlowDto secPositionFlowDto = new EtrSecPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(secPositionFlowDto, etrSecPositionFlowDto);
            etrSecPositionFlows.add(secPositionFlowDto);
        });

		// 插入持仓流水
        //撤回不删除流水  登录一比反向流水  add by zhangnian 202010115
		if(ptlPositionHoldDto.getPtlSecPositionFlows().size() > 0){
            List<PtlSecPositionFlow> positionFlows = baseAssetCashPositionCommMapper.selectAllPositionByOffsetFlag(dto.getQuoteId(), DictCodeList.冲正标识_正常交易.getValue());
            for (PtlSecPositionFlow ptlSecPositionFlow : positionFlows) {
                if (StringUtil.equals(ptlSecPositionFlow.getHodingType(), DictCodeList.持仓类型_质押.getValue())) {
                    PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(flowDto, ptlSecPositionFlow);
                    flowDto.setSeqno(IDGenerator.generate());
                    flowDto.setAmount(flowDto.getAmount().negate());
                    flowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                    flowDto.setOffsetId(ptlSecPositionFlow.getTradeId());
                    ptlPositionHoldDto.getPtlSecPositionFlows().add(flowDto);
                }
            }
            if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
                baseAssetCashPositionCommMapper.deletePositionByTradeId(dto.getQuoteId());
            }
            baseAssetCashPositionCommMapper.deletePositionByTradeId(dto.getQuoteId());
			for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
				basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
			}
		}
		
		// 插入现金流水
        //撤回不删除流水  登录一比反向流水 add by zhangnian 202010115
		if (ptlPositionHoldDto.getPtlCashPositionFlows().size() > 0) {
			List<String> quoteIds = new ArrayList<String>();
			quoteIds.add(dto.getQuoteId());
            if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
			    baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(quoteIds);
            }
			for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
				basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
			}
		}
        if (DictCodeListExt3.交易方向_正回购.getValue().equals(ps)) {
            // 查询正常流水,生成冲销流水,为了余额更新正确
            if (etrSecPositionFlows.size() > 0) {
                List<EtrSecPositionFlowDto> etrSecPositionFlowDtos = baseAssetCashPositionCommMapper.selectAllEtrPositionByOffsetFlag(dto.getQuoteId(), DictCodeList.冲正标识_正常交易.getValue());
                for (EtrSecPositionFlowDto flowDto : etrSecPositionFlowDtos) {
                    EtrSecPositionFlowDto temp = new EtrSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(temp, flowDto);
                    temp.setSeqno(IDGenerator.generate());
                    temp.setAmount(flowDto.getAmount().negate());
                    temp.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                    temp.setOffsetId(flowDto.getTradeId());
                    etrSecPositionFlows.add(temp);
                }
            }
            //删除历史流水
            baseAssetCashPositionCommMapper.deleteEtrPositionByTradeId(dto.getQuoteId());

            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlowDtos);
            // 插入新流水,并更新余额
            EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        }
	}

	private PtlCashPositionFlowDto 逆回购构造现金流水(CfetsRepoDealQuotedPrice001Dto dto,Date vSettleDate,BigDecimal factor){
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
        }else{
            //回退的金额为空处理
            if(null!=dto.getFpriceAmt()){
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
     * 生成流水号获取
     *
     * @return
     */
    public static String getSeqNo() {
        return IDGenerator.generate();
    }


    /**
     * 投资目的处理
     * @author zhangnian
     * @date 2021/1/11 0011
     * @param  * @param null
     * @return
     */
    public Map<String, List<PledgeBondInfoDto>> handleInvAim(CfetsRepoDealQuotedPrice001Dto input){
        // 可质押券信息（含投资目的）
        Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(input)
                .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
        avaMap.entrySet().stream().forEach(entry -> {
            List<PledgeBondInfoDto> newList = new ArrayList<>() ;
            if(CollectionUtils.isNotEmpty(entry.getValue())){
                entry.getValue().forEach(item->{
                    newList.add(item);
                });
                Collections.sort(newList, Comparator.comparing(PledgeBondInfoDto::getInvAimSort));
                entry.setValue(newList);
            }
        });
        return avaMap;
    }


    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(CfetsRepoDealQuotedPrice001Dto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("secAcctId", input.getSecAcctId());
        param.put("chlId", input.getChlId());
        param.put("folwtradeId", input.getQuoteId());
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                param, PledgeBondInfoDto.class);
    }


    public BigDecimal selectUsedAmt(Map<String, Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.selectUsedAmt(map);
    }


    /**
     * 组合分配列表
     * @param map
     * @return
     */
    public List<PtlAllocationDto>  selectPortfolioInit(Map<String,Object> map){
        if(map.containsKey("quoteId")&&StringUtil.isNotEmpty(map.get("quoteId"))){
            return cfetsRepoDealQuotedPrice001Mapper.selectPortfolioByQuoteIdInit(map);
        }
        return cfetsRepoDealQuotedPrice001Mapper.selectPortfolioInit(map);
    }

    /**
     * 关系数据
     * @param map
     * @return
     */
    public List<PtlAllocationDto>  selectEtrReByQuoteId(Map<String,Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.selectEtrReByQuoteId(map);
    }

    public List<CfetsRepoDealQuotedPrice003Dto> selectQuoteCrepPledgeForDzh(Map<String,Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.selectQuoteCrepPledgeForDzh(map);
    }

    public List<CfetsRepoDealQuotedPrice003Dto> selectQuoteCrepPledgeForDsf(Map<String,Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.selectQuoteCrepPledgeForDsf(map);
    }

    public String getIsShowDsfPledge(Map<String,Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.getIsShowDsfPledge(map);
    }

    public List<CfetsRepoDealQuotedPrice001Dto> getQualifiedByentrustIds(Map<String,Object> map){
        return cfetsRepoDealQuotedPrice001Mapper.getQualifiedByentrustIds(map);
    }

    public CfetsRepoDealQuotedPrice003Dto queryPledgeBondElementForImport(Map<String, Object> param){
        return cfetsRepoDealQuotedPrice001Mapper.queryPledgeBondElementForImport(param);
    }
}
