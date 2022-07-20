/*
 * 文件名：CfetsBondDealQuotedPriceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.bond.service;

import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.bond.dao.CfetsBondDealQuotedPriceMapper;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CounterInfoDto;
import com.joyintech.service.bankuplink.calc.dto.SecInfoModelDto;
import com.joyintech.service.bankuplink.calc.service.SecCalRequestQuoteService;
import com.joyintech.service.bankuplink.common.dao.CfetsQuotedCommonMapper;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.service.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月9日
 * @see CfetsBondDealQuotedPriceService
 * @since 1.0
 */
@Service
public class CfetsBondDealQuotedPriceService extends BaseService{
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPriceMapper cfetsBondDealQuotedPriceMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonMapper cfetsQuotedCommonMapper;
    
    
    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductMapper finProductMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private SecCalRequestQuoteService secCalRequestQuoteService;


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
    private IbuMainService ibuMainService;

    /**
     * {@linkplain}
     */
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    
    /**
     * 委托指令查询
     * 
     * @param param 入参
     * @return 委托指令
     * @see
     * @since 1.0
     */
    public List<CfetsBondDealEntrustOrderDto> queryEntrustOrderList(Map<String, Object> param){
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradeMarketMode", BussinessDictCodeList.交易市场_二级市场.getValue());
        param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
        param.put("secAcctSource", BussinessDictCodeList.账户来源_自营.getValue());
        param.put("psT01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
//        param.put("platDate", "2020-08-13");
        //*****************************调试****************************/
        //messageSenderService.pushCommandMsgToMQ("金融产品类型_债券");
        return cfetsBondDealQuotedPriceMapper.queryEntrustOrderList(param);
    }
    
    /**
     * 根据查询查询符合条件的委托列表分页
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<CfetsBondDealEntrustOrderDto> queryEntrustOrderPage(int page, int length, Map<String, Object> param){
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradeMarketMode", BussinessDictCodeList.交易市场_二级市场.getValue());
        param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
        param.put("psT01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
        return this.queryForPageForSelectReturn(page, length,()->cfetsBondDealQuotedPriceMapper.queryEntrustOrderList(param));
    }
    
    /**
     * 根据查询查询符合条件的委托列表
     * 
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryEntrustInfoList(int page, int length, Map<String, Object> param){
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradeMarketMode", BussinessDictCodeList.交易市场_二级市场.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("secAcctSource", BussinessDictCodeList.账户来源_自营.getValue());
        param.put("secAcctSource_01", BussinessDictCodeList.账户来源_理财.getValue());
        param.put("psT01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
        return this.queryForPageForSelectReturn(page, length,()->cfetsBondDealQuotedPriceMapper.queryEntrustInfoList(param));
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
    public Page<SuggestDto> queryHisEntrustInfoList(int page, int length, Map<String, Object> param){
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("tradeMarket", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradeMarketMode", BussinessDictCodeList.交易市场_二级市场.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("secAcctSource", BussinessDictCodeList.账户来源_自营.getValue());
        param.put("secAcctSource_01", BussinessDictCodeList.账户来源_理财.getValue());
        param.put("psT01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
        return this.queryForPageForSelectReturn(page, length,()->cfetsBondDealQuotedPriceMapper.queryHisEntrustInfoList(param));
    }
    // ZGZGST-1936:CFETS历史报价查询-指令代码使用文本框 fuyongsheng 20210904 add end
    
    /**
     * 根据查询查询符合条件的报价编号
     * 
     * @param param 入参
     * @return 报价编号
     * @see
     * @since 1.0
     */
    public Page<SuggestDto> queryCfetsQuoteIdList(int page, int length, Map<String, Object> param){
        return this.queryForPageForSelectReturn(page, length,()->cfetsBondDealQuotedPriceMapper.queryCfetsQuoteIdList(param));
    }
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入流程表
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertWflBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.insertWflBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.insertBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 现券对话报价新增发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendNewBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return ibuComponentCommonService.sendNewBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 现券对话报价更新发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendUpdateBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        return ibuComponentCommonService.sendUpdateBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
    }
    
    /**
     * 本方撤销请求回复；<br />
     * 做市方回复非做市方请求报价
     * 操作类型：本方新增请求回复
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuoteNewReply(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.sendBondRequestQuoteNewReply(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入流程表
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertWflBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        ibuComponentCommonService.insertWflBondRequestQuote(cfetsBondDealRequestQuoteDto);
        return true;
    }
    
    // add by wangzhuangzhuang 新增请求画面 20200520 start
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean insertBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        // 调用银行间上行组件处理
        ibuComponentCommonService.insertBondRequestQuote(cfetsBondDealRequestQuoteDto);
        
        return true;
    }
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        // 调用银行间上行组件处理
        return ibuComponentCommonService.sendBondRequestQuote(cfetsBondDealRequestQuoteDto);
    }
    /**
     * 现券请求报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuoteDeal(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        // 调用银行间上行组件处理
        ibuComponentCommonService.sendBondRequestQuoteDeal(cfetsBondDealRequestQuoteDto);
        
        return true;
    }
    // add by wangzhuangzhuang 新增请求画面 20200520 end
    
    /**
     * 现券请求报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuoteModify(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        // 调用银行间上行组件处理
        ibuComponentCommonService.sendBondRequestQuoteModify(cfetsBondDealRequestQuoteDto);
        
        return true;
    }
    
    /**
     * 现券对话报价查询<br />
     * 根据主键查询对话报价信息
     * 
     * @param param String 报价编号
     * @return BondDialogueQuoteDto
     */
    public CfetsBondDealDialogueQuoteDto getBondDialogueQuote(Map<String, Object> param){
        if (!StringUtil.isNotEmpty(param.get("quoteId"))){
            return null;
        }
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
		if (StringUtil.isNotEmpty(param.get("statusValid"))) {
			cfetsBondDealDialogueQuoteDto = ibuComponentCommonService.getBondDialogueQuote(param.get("quoteId").toString(), param.get("statusValid").toString());
		} else {
			cfetsBondDealDialogueQuoteDto = ibuComponentCommonService.getBondDialogueQuote(param.get("quoteId").toString());
		}
        if (cfetsBondDealDialogueQuoteDto != null){
            // 从委托获取一些必要信息
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())){
                param.put("loginUserId", param.get("loginUserId").toString());
                param.put("entrustId", cfetsBondDealDialogueQuoteDto.getEntrustId());
                List<CfetsBondDealEntrustOrderDto> list = this.queryEntrustOrderList(param);
                if (!StringUtil.isEmptyList(list)){
                    CfetsBondDealEntrustOrderDto entrustInfo = list.get(0);
                    cfetsBondDealDialogueQuoteDto.setEntrustNum(entrustInfo.getEntrustNum());
                    cfetsBondDealDialogueQuoteDto.setSecManageAcctId(entrustInfo.getSecManageAcctId());
                    cfetsBondDealDialogueQuoteDto.setPortfolioSeries(entrustInfo.getPortfolioSeries());
                }
            }
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(cfetsBondDealDialogueQuoteDto.getFinprodId());
            if (finProduct != null){
                cfetsBondDealDialogueQuoteDto.setFinprodMarketId(finProduct.getFinprodMarketId());
                cfetsBondDealDialogueQuoteDto.setIsRight(finProduct.getIsRight());
                cfetsBondDealDialogueQuoteDto.setTradeMarket(finProduct.getTradeMarket());
            }
            FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
            finProductTypeKey.setFinprodId(cfetsBondDealDialogueQuoteDto.getFinprodId());
            finProductTypeKey.setBranch(0L);
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
            if (finProductType != null){
                cfetsBondDealDialogueQuoteDto.setAssetTypeOne(finProductType.getType4());
                cfetsBondDealDialogueQuoteDto.setType11(finProductType.getType11());
            }
        }
        
        return cfetsBondDealDialogueQuoteDto;
    }
    
    // add by wangzhuangzhuang 报价双击查看详情 20200521 start
    /**
     * 现券请求报价查询<br />
     * 根据主键查询请求报价信息
     * 
     * @param param String 报价编号
     * @return BondRequestQuoteDto
     */
    public CfetsBondDealRequestQuoteDto getBondRequestQuote(Map<String, Object> param){
        if (!StringUtil.isNotEmpty(param.get("quoteId"))){
            return null;
        }
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
		if (StringUtil.isNotEmpty(param.get("statusValid"))) {
			cfetsBondDealRequestQuoteDto = ibuComponentCommonService
					.getBondRequestQuote(param.get("quoteId").toString(), param.get("statusValid").toString());
		} else {
			cfetsBondDealRequestQuoteDto = ibuComponentCommonService
					.getBondRequestQuote(param.get("quoteId").toString());
		}
        if (cfetsBondDealRequestQuoteDto != null){
            // 从委托获取一些必要信息
            if (StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getEntrustId())){
                param.put("loginUserId", param.get("loginUserId").toString());
                param.put("entrustId", cfetsBondDealRequestQuoteDto.getEntrustId());
                List<CfetsBondDealEntrustOrderDto> list = this.queryEntrustOrderList(param);
                if (!StringUtil.isEmptyList(list)){
                    CfetsBondDealEntrustOrderDto entrustInfo = list.get(0);
                    cfetsBondDealRequestQuoteDto.setEntrustNum(entrustInfo.getEntrustNum());
                    cfetsBondDealRequestQuoteDto.setSecManageAcctId(entrustInfo.getSecManageAcctId());
                    cfetsBondDealRequestQuoteDto.setPortfolioSeries(entrustInfo.getPortfolioSeries());
                }
            }
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(cfetsBondDealRequestQuoteDto.getFinprodId());
            if (finProduct != null){
                cfetsBondDealRequestQuoteDto.setFinprodMarketId(finProduct.getFinprodMarketId());
                cfetsBondDealRequestQuoteDto.setFinprodAbbr(finProduct.getFinprodAbbr());
                cfetsBondDealRequestQuoteDto.setTradeMarket(finProduct.getTradeMarket());
            }
        }
        if(!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDto.getCounterInfoList())){
            // 实际业务场景选择的机构列表数据量不多，此处循环处理不考虑性能问题
            for (CounterInfoDto counterInfoDto : cfetsBondDealRequestQuoteDto.getCounterInfoList()) {
                if (StringUtil.isNotEmpty(counterInfoDto.getCpPartyId())) {
                    param.put("counterId", counterInfoDto.getCpPartyId());
                    CounterInfoDto dto = cfetsBondDealQuotedPriceMapper.selectCounterInfo(param);
                    if (dto != null){
                        counterInfoDto.setCounterValue(dto.getCounterValue());
                    }
                }
                counterInfoDto.setShareAmt(ArithmeticUtil.divide(counterInfoDto.getShareAmt(), new BigDecimal(100),6));
                counterInfoDto.setCounterId(counterInfoDto.getCpPartyId());
                if (StringUtil.isNotEmpty(counterInfoDto.getCpTrader())){
                    String traderName = cfetsQuotedCommonService.queryTraderNameByTraderId(counterInfoDto.getCpTrader());
                    if (traderName != null){
                        counterInfoDto.setCpTrader(traderName);
                    }
                }
            }
        }
        
        return cfetsBondDealRequestQuoteDto;
    }
    // add by wangzhuangzhuang 报价双击查看详情 20200521 end
    
    /**
     * 实体类属性转换
     * 
     * @param bondQuoteExtDto 组件返回dto
     * @return 画面显示dto
     * @see
     * @since 1.0
     */
    private BondQuoteInfoDto dtoConvert(BondQuoteExtDto bondQuoteExtDto){
        // add by etliu 20210329 银行间上下行多组合 start
        if (bondQuoteExtDto.getIbuQuoteBondDet() == null && !StringUtil.isEmptyList(bondQuoteExtDto.getIbuQuoteBondDetList())) {
            bondQuoteExtDto.setIbuQuoteBondDet(bondQuoteExtDto.getIbuQuoteBondDetList().get(0));
        }
        // add by etliu 20210329 银行间上下行多组合 end

        BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
        bondQuoteInfoDto.setQuoteId(bondQuoteExtDto.getQuoteId());
        bondQuoteInfoDto.setQuoteRoleType(bondQuoteExtDto.getQuoteRoleType());
        bondQuoteInfoDto.setPs(bondQuoteExtDto.getPs());
        bondQuoteInfoDto.setFinprodId(bondQuoteExtDto.getFinprodId());
        bondQuoteInfoDto.setFinprodMarketId(bondQuoteExtDto.getFinprodMarketId());
        bondQuoteInfoDto.setFinprodAbbr(bondQuoteExtDto.getFinprodAbbr());
        bondQuoteInfoDto.setQuoteType(bondQuoteExtDto.getQuoteType());
        bondQuoteInfoDto.setTradeStatus(bondQuoteExtDto.getTradeStatus());
        bondQuoteInfoDto.setSendStatus(bondQuoteExtDto.getSendStatus());
        bondQuoteInfoDto.setOuPartyId(bondQuoteExtDto.getOuPartyId());
        bondQuoteInfoDto.setPartyAbbr(bondQuoteExtDto.getPartyAbbr());
        bondQuoteInfoDto.setOuTraderId(bondQuoteExtDto.getOuTraderId());
        bondQuoteInfoDto.setOuTraderName(bondQuoteExtDto.getOuTraderName());
        bondQuoteInfoDto.setCpPartyId(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getCpPartyId() : null);
        bondQuoteInfoDto.setCpPartyName(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getCpPartyName() : null);
        bondQuoteInfoDto.setCpTrader(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getCpTrader() : null);
        bondQuoteInfoDto.setCpTraderName(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getCpTraderName() : null);
        if (StringUtil.equals(
            com.joyintech.background.component.uplink.interbank.common.DictCodeList.报价系统区分类型_现券请求报价.getValue(),
            bondQuoteExtDto.getQuoteType())) {
            bondQuoteInfoDto.setReqEffTime(bondQuoteExtDto.getEffTime() != null ? DateUtil.format(
                bondQuoteExtDto.getEffTime(), "HH:mm") : null);
        }
        bondQuoteInfoDto.setResEffTime(bondQuoteExtDto.getIbuQuoteBondDet() != null ? DateUtil.format(bondQuoteExtDto.getIbuQuoteBondDet().getReEffTime(), "HH:mm") : null);
        bondQuoteInfoDto.setSettleSpeed(bondQuoteExtDto.getSettleSpeed());
        bondQuoteInfoDto.setUnitCprice(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getUnitCprice() : null);
        bondQuoteInfoDto.setYield(bondQuoteExtDto.getIbuQuoteBondDet() != null ? ArithmeticUtil.multiply(bondQuoteExtDto.getIbuQuoteBondDet().getYield(), new BigDecimal(100)) : null);
        bondQuoteInfoDto.setExerYield(bondQuoteExtDto.getIbuQuoteBondDet() != null ? ArithmeticUtil.multiply(bondQuoteExtDto.getIbuQuoteBondDet().getExerYield(), new BigDecimal(100)) : null);
        bondQuoteInfoDto.setAssetTypeOne(bondQuoteExtDto.getType6());
        bondQuoteInfoDto.setCfetsQuoteId(bondQuoteExtDto.getCfetsQuoteId());
        bondQuoteInfoDto.setfTradeId(bondQuoteExtDto.getIbuQuoteBondDet() != null ? bondQuoteExtDto.getIbuQuoteBondDet().getExecid() : null);
        // 券面总额
        bondQuoteInfoDto.setPrinAmt(bondQuoteExtDto.getShareAmt() != null ? ArithmeticUtil.multiply(bondQuoteExtDto.getShareAmt(), new BigDecimal(100)) : null);
        // 回复量
        bondQuoteInfoDto.setShareAmt(bondQuoteExtDto.getIbuQuoteBondDet() != null ? ArithmeticUtil.multiply(bondQuoteExtDto.getIbuQuoteBondDet().getShareAmt(), new BigDecimal(100)) : null);
        bondQuoteInfoDto.setValidStatus(bondQuoteExtDto.getValidStatus());
        //组合代码
        bondQuoteInfoDto.setPortfolioId(bondQuoteExtDto.getPortfolioId());
        //是否多组合
        bondQuoteInfoDto.setIsDzh(bondQuoteExtDto.getIsDzh());
        //待偿期 评级 备注
        bondQuoteInfoDto.setPendingPeriod(bondQuoteExtDto.getPendingPeriod());
        bondQuoteInfoDto.setGrade(bondQuoteExtDto.getGrade());
        bondQuoteInfoDto.setRemark(bondQuoteExtDto.getRemark());
        bondQuoteInfoDto.setDeliveryType(bondQuoteExtDto.getDeliveryType());
        bondQuoteInfoDto.setSettType(bondQuoteExtDto.getSettType());
        return bondQuoteInfoDto;
    }
    
    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param param BondQuoteSearchDto 现券报价查询条件
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteInfoDto> getDayOfferTableList(Map<String, Object> param){
        List<BondQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        List<BondQuoteExtDto> bondQuoteExtDtoList = ibuComponentCommonService.getDayOfferTableList(param);
        List<BondQuoteExtDto> list = handleManyCounter(bondQuoteExtDtoList);
        list.forEach(x -> {
            bondQuoteInfoDtoList.add(this.dtoConvert(x));
        });
        return bondQuoteInfoDtoList;
    }

    /**
     * 多机构处理
     * @param list
     * @return
     */
    public List<BondQuoteExtDto> handleManyCounter(List<BondQuoteExtDto> list){
    	List<BondQuoteExtDto> bondQuoteExtDtos = new ArrayList<>();
    	if(!StringUtil.isEmptyList(list)) {
    		for(BondQuoteExtDto dto:list) {
    			//单机构
    			if(dto.getIbuQuoteBondDetList() != null && dto.getIbuQuoteBondDetList().size()==1) {
    				dto.setIbuQuoteBondDet(dto.getIbuQuoteBondDetList().get(0));
    				bondQuoteExtDtos.add(dto);
    			}
    			//多机构
    			else if(dto.getIbuQuoteBondDetList() != null && dto.getIbuQuoteBondDetList().size()>1) {
    					//终态：已撤回、已终止、已成交、已过期只展示一条,新增中初始态只展示一条
                        //add by zznn 20210726 已新增、成功 请求报价合并
    					if(StringUtil.equals(dto.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已撤回.getValue())
    							|| StringUtil.equals(dto.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已终止.getValue())
    							|| StringUtil.equals(dto.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已过期.getValue())
    							|| StringUtil.equals(dto.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_新增中.getValue())
    							||(StringUtil.equals(dto.getTradeStatus(),com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_待回复.getValue())&&StringUtil.equals(dto.getSendStatus(),com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行发送状态_成功.getValue()))
                                || dto.getTradeStatus() == null) {
    						IbuQuoteBondDetDto newDto = new IbuQuoteBondDetDto();
    						BeanUtil.copyPropertiesIgnoreNull(newDto, dto.getIbuQuoteBondDetList().get(0));
    						for (int i = 0; i < dto.getIbuQuoteBondDetList().size(); i++) {
    							//机构ID拼接
								if(i != 0 && StringUtil.isNotEmpty(dto.getIbuQuoteBondDetList().get(i).getCpPartyId())) {
									newDto.setCpPartyId(newDto.getCpPartyId()+","+dto.getIbuQuoteBondDetList().get(i).getCpPartyId());
								}
								//机构name拼接
								if(i != 0 && StringUtil.isNotEmpty(dto.getIbuQuoteBondDetList().get(i).getCpPartyName())) {
									newDto.setCpPartyName(newDto.getCpPartyName()+","+dto.getIbuQuoteBondDetList().get(i).getCpPartyName());
								}
								//交易员ID拼接
								if(i != 0 && StringUtil.isNotEmpty(dto.getIbuQuoteBondDetList().get(i).getCpTrader())) {
									newDto.setCpTrader(newDto.getCpTrader()+","+dto.getIbuQuoteBondDetList().get(i).getCpTrader());
								}
								//交易员name拼接
								if(i != 0 && StringUtil.isNotEmpty(dto.getIbuQuoteBondDetList().get(i).getCpTraderName())) {
									newDto.setCpTraderName(newDto.getCpTraderName()+","+dto.getIbuQuoteBondDetList().get(i).getCpTraderName());
								}
								//depID拼接
								if(i != 0 && StringUtil.isNotEmpty(dto.getIbuQuoteBondDetList().get(i).getDetId())) {
									newDto.setDetId(newDto.getDetId()+","+dto.getIbuQuoteBondDetList().get(i).getDetId());
								}
							}
    						dto.setIbuQuoteBondDet(newDto);
    						bondQuoteExtDtos.add(dto);
    					}
    					else if(StringUtil.equals(dto.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已成交.getValue())) {
    						for(IbuQuoteBondDetDto ibuQuoteBondDet:dto.getIbuQuoteBondDetList()) {
	    						if(ibuQuoteBondDet.getTradeStatus().equals(com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_已成交.getValue())){
	    							dto.setIbuQuoteBondDet(ibuQuoteBondDet);
	    							bondQuoteExtDtos.add(dto);
	    							break;
	    						}
    						}
    					}
    					//非中台展示多条，待回复合并
    					else {
    						//待回复详情合并用
    						IbuQuoteBondDetDto newDetDto = new IbuQuoteBondDetDto();
    						boolean flag = true;
    						for(IbuQuoteBondDetDto ibuQuoteBondDet:dto.getIbuQuoteBondDetList()) {
    							BondQuoteExtDto newDto = new BondQuoteExtDto();
    							if(StringUtil.equals(ibuQuoteBondDet.getTradeStatus(), com.joyintech.background.component.uplink.interbank.common.DictCodeList.银行间上行报价状态_待回复.getValue())) {
    								if(flag) {
    									BeanUtil.copyPropertiesIgnoreNull(newDetDto, ibuQuoteBondDet);
    									flag = false;
    								}else {
    									//机构ID拼接
    									if(StringUtil.isNotEmpty(ibuQuoteBondDet.getCpPartyId())) {
    										newDetDto.setCpPartyId(newDetDto.getCpPartyId()+","+ibuQuoteBondDet.getCpPartyId());
    									}
    									//机构name拼接
    									if(StringUtil.isNotEmpty(ibuQuoteBondDet.getCpPartyName())) {
    										newDetDto.setCpPartyName(newDetDto.getCpPartyName()+","+ibuQuoteBondDet.getCpPartyName());
    									}
    									//交易员ID拼接
    									if(StringUtil.isNotEmpty(ibuQuoteBondDet.getCpTrader())) {
    										newDetDto.setCpTrader(newDetDto.getCpTrader()+","+ibuQuoteBondDet.getCpTrader());
    									}
    									//交易员name拼接
    									if(StringUtil.isNotEmpty(ibuQuoteBondDet.getCpTraderName())) {
    										newDetDto.setCpTraderName(newDetDto.getCpTraderName()+","+ibuQuoteBondDet.getCpTraderName());
    									}
    									//depID拼接
    									if(StringUtil.isNotEmpty(ibuQuoteBondDet.getDetId())) {
    										newDetDto.setDetId(newDetDto.getDetId()+","+ibuQuoteBondDet.getDetId());
    									}
    								}
    							}else {
    								BeanUtil.copyPropertiesIgnoreNull(newDto, dto);
    								newDto.setTradeStatus(ibuQuoteBondDet.getTradeStatus());
    								newDto.setSendStatus(ibuQuoteBondDet.getSendStatus());
    								newDto.setIbuQuoteBondDet(ibuQuoteBondDet);
    								bondQuoteExtDtos.add(newDto);
    							}
    						}
    						if(!flag) {
    							dto.setTradeStatus(newDetDto.getTradeStatus());
    							dto.setSendStatus(newDetDto.getSendStatus());
    							dto.setIbuQuoteBondDet(newDetDto);
    							bondQuoteExtDtos.add(dto);
    						}
    					}
    				}
    			}
    		}
    	return bondQuoteExtDtos;
    }
    
    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param param BondQuoteSearchDto 现券报价查询条件
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteInfoDto> getOrderOfferTableList(Map<String, Object> param){
        List<BondQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        List<BondQuoteExtDto> bondQuoteExtDtoList = ibuComponentCommonService.getOrderOfferTableList(param);
        bondQuoteExtDtoList.forEach(x -> {
            bondQuoteInfoDtoList.add(this.dtoConvert(x));
        });
        
        return bondQuoteInfoDtoList;
    }
    
    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param param BondQuoteSearchDto 现券报价查询条件
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteInfoDto> getMarketOfferTableList(Map<String, Object> param){
        List<BondQuoteInfoDto> bondQuoteInfoDtoList = new ArrayList<>();
        // 查询条件转换
        List<BondQuoteExtDto> bondQuoteExtDtoList = ibuComponentCommonService.getMarketOfferTableList(param);
        bondQuoteExtDtoList.forEach(x -> {
            BondQuoteInfoDto bondQuoteInfoDto = this.dtoConvert(x);
            bondQuoteInfoDto.setOuTraderId(null);
            bondQuoteInfoDtoList.add(bondQuoteInfoDto);
        });
        
        return bondQuoteInfoDtoList;
    }
    
    /**
     * 调用算法计算相关金额元素
     * 
     * @param cfetsBondDealDialogueQuoteDto 画面对象
     * @return 处理后的画面对象
     * @see
     * @since 1.0
     */
    public CfetsBondDealDialogueQuoteDto calcAmt(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
    	SecInfoModelDto secInfoModelDto = new SecInfoModelDto();
        secInfoModelDto.setFinprodId(cfetsBondDealDialogueQuoteDto.getFinprodId());
        secInfoModelDto.setPrinAmt(cfetsBondDealDialogueQuoteDto.getAvaNum());
        secInfoModelDto.setSettleDate(cfetsBondDealDialogueQuoteDto.getSettleDate());
        secInfoModelDto.setCalculateField(cfetsBondDealDialogueQuoteDto.getCalculateField());
        secInfoModelDto.setCleanprice(cfetsBondDealDialogueQuoteDto.getPrice());
        secInfoModelDto.setDpriceamt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        //不改变共通  收益率跟原报价画面一样 乘以100参与计算
        secInfoModelDto.setMatureyield(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getMatureYield(),new BigDecimal(100)));
        secInfoModelDto.setExerciseyield(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getExerYield(),new BigDecimal(100)));
        // 根据债券交易的录入信息计算各项指标
        secCalRequestQuoteService.calSecDeal(secInfoModelDto);
        // 返回结果处理
        if (secInfoModelDto.getCleanprice() != null){
            cfetsBondDealDialogueQuoteDto.setPrice(secInfoModelDto.getCleanprice().setScale(4, RoundingMode.HALF_UP));
        }
        if (secInfoModelDto.getMatureyield() != null){
            cfetsBondDealDialogueQuoteDto.setMatureYield(secInfoModelDto.getMatureyield());
        }
        if (secInfoModelDto.getExerciseyield() != null){
            cfetsBondDealDialogueQuoteDto.setExerYield(secInfoModelDto.getExerciseyield());
        }
        if (secInfoModelDto.getAccuir() != null){
            cfetsBondDealDialogueQuoteDto.setUnitInt(secInfoModelDto.getAccuir().setScale(5, RoundingMode.HALF_UP));
        }
        if (secInfoModelDto.getDirtyprice() != null){
            cfetsBondDealDialogueQuoteDto.setUnitFprice(secInfoModelDto.getDirtyprice().setScale(4, RoundingMode.HALF_UP));
        }
        
        cfetsBondDealDialogueQuoteDto.setCpriceAmt(secInfoModelDto.getCpriceamt());
        cfetsBondDealDialogueQuoteDto.setIntAmt(secInfoModelDto.getAccuiramt());
        cfetsBondDealDialogueQuoteDto.setFpriceAmt(secInfoModelDto.getDpriceamt());
        // 每百元金额(元) = 面值
        cfetsBondDealDialogueQuoteDto.setParValue(secInfoModelDto.getAbsPrinAmtHun());
        // 本金额(元) = 面值 * 券面总额  / 100
        cfetsBondDealDialogueQuoteDto.setParValueAmt(secInfoModelDto.getAbsPrinAmt());

        //计算结果到期收益率和行权收益率除以100
        cfetsBondDealDialogueQuoteDto.setMatureYield(ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getMatureYield(),new BigDecimal(100),6));
        cfetsBondDealDialogueQuoteDto.setExerYield(ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getExerYield(),new BigDecimal(100),6));
        return cfetsBondDealDialogueQuoteDto;
    	/*cfetsBondDealDialogueQuoteDto.setUnitInt(new BigDecimal(10).setScale(5, RoundingMode.HALF_UP));
        cfetsBondDealDialogueQuoteDto.setUnitFprice(new BigDecimal(11).setScale(4, RoundingMode.HALF_UP));
        cfetsBondDealDialogueQuoteDto.setCpriceAmt(new BigDecimal(10));
        cfetsBondDealDialogueQuoteDto.setIntAmt(new BigDecimal(102));
        cfetsBondDealDialogueQuoteDto.setFpriceAmt(new BigDecimal(11000));
        // 每百元金额(元) = 面值
        cfetsBondDealDialogueQuoteDto.setParValue(new BigDecimal(100));
        // 本金额(元) = 面值 * 券面总额  / 100
        cfetsBondDealDialogueQuoteDto.setParValueAmt(new BigDecimal(10000));
        return cfetsBondDealDialogueQuoteDto;*/
    }
    

    /**
     * 现券对话报价批量新增发送<br />
     * @param inputList List<BondDialogueQuoteDto>
     * @param userInfo User 登录用户信息
     */
    public QuoteResultBatDto batchNewBondDialogueQuote(List<BondDialogueQuoteDto> inputList, User userInfo) {
        return ibuComponentCommonService.batchNewBondDialogueQuote(inputList, userInfo);
    }

    /**
     * 现券请求报价批量新增发送<br />
     * 操作类型 ：本方新增请求报价发送
     * @param inputList List<BondRequestQuoteDto>
     * @param userInfo User 登录用户信息
     */
    public QuoteResultBatDto batchNewBondRequestQuote(List<BondRequestQuoteDto> inputList, User userInfo) {
        return ibuComponentCommonService.batchNewBondRequestQuote(inputList, userInfo);
    }

    /**
     * 查询已经存在quoteId列表
     * @param quoteIdList List<String>
     * @return List<String>
     */
    public List<String> selectQuoteIdsByIdList(List<String> quoteIdList) {
        return ibuComponentCommonService.selectQuoteIdsByIdList(quoteIdList);
    }

    /**
     * 查询已经存在detId列表
     * @param detIdList List<String>
     * @return List<String>
     */
    public List<String> selectDetIdsByIdList(List<String> detIdList) {
        return ibuComponentCommonService.selectDetIdsByIdList(detIdList);
    }

    public String getMessage(String code, Object... args) {
        return super.getMessage(code, args);
    }

    /**
     * 查询委托已完成额度
     * 
     * @param map 入参
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal queryEntrustDealedAmt(Map<String, Object> map){
        return cfetsBondDealQuotedPriceMapper.queryEntrustDealedAmt(map);
    }
    
    /**
     * 现券对话报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    public boolean sendBondDialogueQuoteRevoke(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.sendBondDialogueQuoteRevoke(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 对话报价对象
     */
    @Transactional
    public boolean sendBondRequestQuoteRevoke(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        ibuComponentCommonService.sendBondRequestQuoteRevoke(cfetsBondDealRequestQuoteDto);
        return true;
    }
    
    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 对话报价对象
     */
    @Transactional
    public boolean sendBondRequestQuoteRevokeReply(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        ibuComponentCommonService.sendBondRequestQuoteRevokeReply(cfetsBondDealRequestQuoteDto);
        return true;
    }
    
    /**
     * 现券对话报价回退；把失败状态还原成待处理<br />
     * 报价回退发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto String 报价编号
     * @return QuoteResultDto
     */
    @Transactional
    public boolean bondDialogueQuoteRestore(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.bondDialogueQuoteRestore(cfetsBondDealDialogueQuoteDto);
        cfetsBondDealDialogueQuoteDto.setOperateType("Restore");
        doInsetOrUpdateForPtlPositionHold(cfetsBondDealDialogueQuoteDto);//头寸处理
        return true;
    }
    
    /**
     * 现券请求报价回退；把失败状态还原成待处理<br />
     * 报价回退发送处理，更新接口返回数据
     * 操作类型：本方报价回退
     * @param cfetsBondDealRequestQuoteDto String 报价编号
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuoteRestore(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        ibuComponentCommonService.sendBondRequestQuoteRestore(cfetsBondDealRequestQuoteDto);
        cfetsBondDealRequestQuoteDto.setOperateType("Restore");
        doInsetOrUpdateForPtlPositionHold(cfetsBondDealRequestQuoteDto);//头寸处理
        return true;
    }
    
    /**
     * 现券对话报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto BondDialogueQuoteDto
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondDialogueQuoteDeal(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.sendBondDialogueQuoteDeal(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 现券对话报价回复拒绝<br />
     * 报价回复拒绝发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto BondDialogueQuoteDto
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondDialogueQuoteRefuse(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        ibuComponentCommonService.sendBondDialogueQuoteRefuse(cfetsBondDealDialogueQuoteDto);
        return true;
    }
    
    /**
     * 
     * 删除请求报价的主表，主表历史，明细，明细历史数据
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional
    public boolean deleteBondRequestQuote(String quoteId){
        return ibuComponentCommonService.deleteBondRequestQuote(quoteId);
    }
    
    /**
     * 
     * 删除对话报价的主表，主表历史，明细，明细历史数据
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    @Transactional
    public boolean deleteBondDialogueQuote(String quoteId){
        return ibuComponentCommonService.deleteBondDialogueQuote(quoteId);
    }
    
    /**
     * 发送网关处理
     * 
     * @param param 入参
     * @return 
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public boolean sendGateWayHandle(Map<String, Object> param){
        boolean flag = false;
        if (StringUtil.isNotEmpty(param.get("quoteResultDtoJson")) && StringUtil.isNotEmpty(param.get("userInfo"))){
            QuoteResultDto quoteResultDto = JsonUtils.json2Object(param.get("quoteResultDtoJson").toString(), QuoteResultDto.class);
            
            Map<String, Object> userMap = (Map<String, Object>)param.get("userInfo");
            User userInfo = new User();
            userInfo.setUserName(userMap.get("userName").toString());
            userInfo.setDeptCode(userMap.get("deptCode").toString());
            
            flag = ibuComponentCommonService.resCommonHandle(quoteResultDto, userInfo, true);
        }
        
        return flag;
    }
    
    /**
     * 查询债券最新估值
     * 
     * @param param 入参
     * @return 最新估值
     * @see
     * @since 1.0
     */
    public MstBondValuationInfo queryLastBondValuation(Map<String, Object> param){
        param.put("valueSource", BussinessDictCodeList.估值来源_中债登.getValue());
        return cfetsBondDealQuotedPriceMapper.queryLastBondValuation(param);
    }
    
    /**
     * 历史报价撤销
     * 
     * @param bondQuoteInfoDto 报价代码
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public boolean doHisRevoke(BondQuoteInfoDto bondQuoteInfoDto){
        QuoteResultDto quoteResultDto = new QuoteResultDto();
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(), BussinessDictCodeList.报价方式_对话报价.getValue())){
            quoteResultDto = ibuComponentCommonService.sendBondDialogueQuoteCancel(bondQuoteInfoDto.getQuoteId(), bondQuoteInfoDto);
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(), BussinessDictCodeList.报价方式_请求报价.getValue())){
            quoteResultDto = ibuComponentCommonService.sendBondRequestQuoteCancel(bondQuoteInfoDto.getQuoteId(), bondQuoteInfoDto);
        }
        return quoteResultDto.isStatus();
    }

    /**
     * 获取下一个工作日
     *
     * @param tradeDate 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    public Date getNextWorkDay(Date tradeDate) {
        List<String> holiday = this.getDealCalendarHoliday(tradeDate,
            DateUtil.addDays(tradeDate, 31), CacheUtil.getSysParam("ibuVar_calendarId"));
        return this.calculationTradingDay(tradeDate, 1, holiday, "1");
    }
    
    /**
     * 交易日历的节假日取得
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dealCalId 交易日历代码
     * @return 节假日列表
     */
    private List<String> getDealCalendarHoliday(Date startDate, Date endDate, String dealCalId) {
        Map<String, Object> paraMap = new HashMap<>();
        int days = StringUtil.toInt(CacheUtil.getSysParam(BussinessConstant.HOLIDAY_POSTPONE_DAYS));
        paraMap.put("startDate", DateUtil.addDay(startDate, -days));
        paraMap.put("endDate", DateUtil.addDay(endDate, days));
        paraMap.put("dealCalId", dealCalId);
        return cfetsBondDealQuotedPriceMapper.getDealCalendarHoliday(paraMap);
    }
    
    
	/**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by zhangpei 
	 * 20201128
	 * @param dto
	 */
	public void doInsetOrUpdateForPtlPositionHold(CfetsBondDealDialogueQuoteDto dto) {
	    // T+1情况，可用持仓登在T日，可用现金登在T+1日
	    Date platDate = iPlatformDate.getSysDate();
        final Date settleDate = dto.getSettleDate() == null ? platDate : dto.getSettleDate(); 
	    
		// 指令持仓流水 、现金流水处理
		int affectRows = 0;
		//已撤回状态 不删除原流水 成交中回退 不删除原流水
		if (!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
			affectRows = doDeletePtlPositionHoldByQuoteId(dto.getQuoteId());
		}
		PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();// 组合持仓流水、组合现金流水
		List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();// 现金流水
		List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();// 持仓流水
		BondDialogueQuoteDto bondDialogueQuoteDto = ibuMainService.getBondDialogueQuote(dto.getQuoteId());
		//撤回、按照当前表的份额、全价金额登录组合持仓流水、组合现金流水
		if(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue()) ||
				StringUtil.equals(dto.getOperateType(), "Restore")){
			//如果原来没有头寸处理、非成交中的回退也不进行头寸操作
			if ((StringUtil.equals(dto.getOperateType(), "Restore") && affectRows == 0) || (StringUtil
					.equals(dto.getOperateType(), "Restore")
					&& StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_成交中.getValue()))) {
				return;
			}
			if(null != bondDialogueQuoteDto && null != bondDialogueQuoteDto.getIbuQuoteBondDto()){
				BeanUtil.copyPropertiesIgnoreNull(dto,bondDialogueQuoteDto.getIbuQuoteBondDto());
			}
		}
		BigDecimal factor = new BigDecimal(-1);
		//撤回登陆反向流水
		if (StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
				|| (StringUtil.equals(dto.getOperateType(), "Restore")
						&& StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_成交中.getValue()))) {
			factor = new BigDecimal(1);
		}
		
		// add by etliu 20210309 银行间上下行多组合 start
		// 银行间报价指令关系业务表
		List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = new ArrayList<>();
		// 画面传入数据，如画面有传，以画面数据为准
		if (!StringUtil.isEmptyList(dto.getCfetsBondDealDialogueQuoteDtoList())) {
            for (CfetsBondDealDialogueQuoteDto tempDataDto : dto.getCfetsBondDealDialogueQuoteDtoList()) {
                IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                ibuQuoteEtrReDto.setQuoteId(dto.getQuoteId());
                ibuQuoteEtrReDto.setPortfolioId(tempDataDto.getPortfolioId());
                ibuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(tempDataDto.getAvaNum(), BigDecimal.valueOf(100))); // 画面上券面总额为万元单位
                ibuQuoteEtrReDto.setFpriceAmt(tempDataDto.getFpriceAmt());
                if (ibuQuoteEtrReDto.getFpriceAmt() == null) {
                    ibuQuoteEtrReDto.setFpriceAmt(ArithmeticUtil.multiply(
                        ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100)));
                }
                ibuQuoteEtrReDto.setEntrustId(tempDataDto.getEntrustId());
                ibuQuoteEtrReDtoList.add(ibuQuoteEtrReDto);
            }
		}
		// db中数据
		else if (bondDialogueQuoteDto != null) {
            ibuQuoteEtrReDtoList = bondDialogueQuoteDto.getIbuQuoteEtrReDtoList();
        }
		// add by etliu 20210309 银行间上下行多组合 end
		
		if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())|| DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())) {
			/********************** 持仓流水DTO构造 *************************************/
            if (StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                getAllPtlSecPositionFlowDto(dto, platDate, ptlSecPositionFlows,
                    bondDialogueQuoteDto, factor, null);
            }
            else {// 多组合
                for (IbuQuoteEtrReDto ibuQuoteEtrReDto : ibuQuoteEtrReDtoList) {
                    dto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
                    getAllPtlSecPositionFlowDto(dto, platDate, ptlSecPositionFlows,
                        bondDialogueQuoteDto, factor, ibuQuoteEtrReDto);
                }
            }
			
//			// T+1场景，可用持仓登录从平台日到交割日的流水
//			if (DateUtil.compareDate(platDate, settleDate) < 0) {
//			    Date tempDate = DateUtil.addDay(platDate, 1);
//			    while (DateUtil.compareDate(tempDate, settleDate) <= 0) {
//			        PtlSecPositionFlowDto ptlSecPositionFlowDtoTemp = new PtlSecPositionFlowDto();
//			        BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTemp, ptlSecPositionFlowDtoUnit);
//			        ptlSecPositionFlowDtoTemp.setCdate(tempDate);
//			        ptlSecPositionFlows.add(ptlSecPositionFlowDtoTemp);
//
//			        tempDate = DateUtil.addDay(tempDate, 1);
//			    }
//			}
			/*************************************************************************/
		} else if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())) {
			if(StringUtil.isEmptyList(ibuQuoteEtrReDtoList)){
				getAllPtlCashpositionFlowDto(dto, settleDate, ptlCashPositionFlows, bondDialogueQuoteDto, factor, null);
			}else{//多组合
			    for (IbuQuoteEtrReDto ibuQuoteEtrReDto : ibuQuoteEtrReDtoList) {
					/********************** 持仓流水DTO构造 *************************************/
					dto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
					getAllPtlCashpositionFlowDto(dto, settleDate, ptlCashPositionFlows, bondDialogueQuoteDto, factor,
					    ibuQuoteEtrReDto);
				}
			}
		}

		ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
		ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
		ptlPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setCdate(settleDate));
		ptlPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setCdate(platDate));
		// 数据是否已入新头寸
		if(StringUtil.isNotEmpty(dto.getIsExistNew())){
            ptlPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setIsExistNew(dto.getIsExistNew()));
            ptlPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setIsExistNew(dto.getIsExistNew()));
        }


		// 插入持仓流水
		if (ptlPositionHoldDto.getPtlSecPositionFlows().size() > 0) {
			if (!(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
					|| (StringUtil.equals(dto.getOperateType(), "Restore") && StringUtil.equals(dto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_成交中.getValue())))) {
				baseAssetCashPositionCommMapper.deletePositionByTradeId(dto.getQuoteId());
			}
			for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
				basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
			}
		}

		// 插入现金流水
		if (ptlPositionHoldDto.getPtlCashPositionFlows().size() > 0) {
			List<String> quoteIds = new ArrayList<>();
			quoteIds.add(dto.getQuoteId());
			if (!(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
					|| (StringUtil.equals(dto.getOperateType(), "Restore") && StringUtil.equals(dto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_成交中.getValue())))) {
				baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(quoteIds);
			}
			for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
				basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
			}
		}
	}

	private void getAllPtlSecPositionFlowDto(CfetsBondDealDialogueQuoteDto dto, Date platDate,
			List<PtlSecPositionFlowDto> ptlSecPositionFlows, BondDialogueQuoteDto bondDialogueQuoteDto,
			BigDecimal factor, IbuQuoteEtrReDto ibuQuoteEtrReDto) {
	    String portfolioId = null;
	    BigDecimal shareAmt = null;
	    
	    if (ibuQuoteEtrReDto != null) {
	        portfolioId = ibuQuoteEtrReDto.getPortfolioId();
	        shareAmt = ibuQuoteEtrReDto.getShareAmt();
	    }
	    else {
	        portfolioId = dto.getPortfolioId();
	        //券面总额【单位：万】 * 10000 = 券面总额[单位：元]
	        //持仓数量  = 券面总额[单位：元]/100
	        shareAmt = dto.getAvaNum() == null ? bondDialogueQuoteDto.getIbuQuoteBondDto().getShareAmt() : dto.getAvaNum().multiply(new BigDecimal(100));
	    }
	    
		PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
		ptlSecPositionFlowDtoUnit.setSeqno(getSeqNo());
		ptlSecPositionFlowDtoUnit.setPortfolioId(portfolioId);// 组合代码
		ptlSecPositionFlowDtoUnit.setSecAcctId("999999"); // 证券账户
		ptlSecPositionFlowDtoUnit.setFinprodId(dto.getFinprodId());// 金融产品代码
		ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
		ptlSecPositionFlowDtoUnit.setInvAim(AllTradeCommonUtil.getInvAim(portfolioId));
		ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
		ptlSecPositionFlowDtoUnit.setCdate(platDate);// 日期
		ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//		//券面总额【单位：万】 * 10000 = 券面总额[单位：元]
//		//持仓数量  = 券面总额[单位：元]/100
//		BigDecimal shareAmt = dto.getAvaNum() == null ? bondDialogueQuoteDto.getIbuQuoteBondDto().getShareAmt() : dto.getAvaNum().multiply(new BigDecimal(100));
		ptlSecPositionFlowDtoUnit.setAmount(shareAmt.multiply(factor));// 数量
		ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
		ptlSecPositionFlowDtoUnit.setTradeId(dto.getQuoteId());// 成交编号
		if (StringUtils.isNotEmpty(portfolioId) && StringUtils.isNotEmpty(dto.getFinprodId())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("portfolioId", portfolioId);
			map.put("finprodId", dto.getFinprodId());
			String secAcctId = cfetsQuotedCommonMapper.qryPortfolioSecAcctId(map);
			if (StringUtils.isNotEmpty(secAcctId)) {
				ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId); // 证券账户
			}
		}
        ptlSecPositionFlowDtoUnit.setEntrustId(dto.getEntrustId());
		ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);
	}

	private void getAllPtlCashpositionFlowDto(CfetsBondDealDialogueQuoteDto dto, final Date settleDate,
			List<PtlCashPositionFlowDto> ptlCashPositionFlows, BondDialogueQuoteDto bondDialogueQuoteDto,
			BigDecimal factor, IbuQuoteEtrReDto ibuQuoteEtrReDto) {
        String portfolioId = null;
        BigDecimal fpriceAmt = null;

        if (ibuQuoteEtrReDto != null) {
            portfolioId = ibuQuoteEtrReDto.getPortfolioId();
            if (ibuQuoteEtrReDto.getFpriceAmt() != null) {
                fpriceAmt = ibuQuoteEtrReDto.getFpriceAmt().multiply(factor);
            }
            else {
                fpriceAmt = ArithmeticUtil.multiply(ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100), factor);
            }
        }
        else {
            portfolioId = dto.getPortfolioId();
            // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
            if (null != dto.getFpriceAmt()) {
                fpriceAmt = dto.getFpriceAmt().multiply(factor);
            } else {
                if (null != bondDialogueQuoteDto && null != bondDialogueQuoteDto.getIbuQuoteBondDto()) {
                    fpriceAmt = bondDialogueQuoteDto.getIbuQuoteBondDto().getFpriceAmt();
                }
            }
            
            if (fpriceAmt == null && StringUtil.equals(
                dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
                fpriceAmt = ArithmeticUtil.multiply(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getShareAmt(),
                    BigDecimal.valueOf(100));
            }
        }
	    
		PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = new PtlCashPositionFlowDto();// 组合现金流水DTO
		ptlCashPositionFlowDtoUnit.setSeqno(getSeqNo());
		ptlCashPositionFlowDtoUnit.setPortfolioId(portfolioId);// 组合代码
		if (StringUtil.isNotEmpty(portfolioId)) {
			ptlCashPositionFlowDtoUnit.setAcctId(EtrCommonCashPositionDealService.select组合托管户(portfolioId,
					dto.getCcy() == null ? DictCodeList.币种_人民币_CNY.getValue() : dto.getCcy(),
					DictCodeListExt2.资金账户类型_托管户.getValue()));
		} else {
			ptlCashPositionFlowDtoUnit.setAcctId("999999");
		}
		ptlCashPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
		ptlCashPositionFlowDtoUnit.setCdate(settleDate);// 日期
		ptlCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
		// if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
//		if (null != dto.getFpriceAmt()) {
//			ptlCashPositionFlowDtoUnit.setAmount(dto.getFpriceAmt().multiply(factor));
//		} else {
//			if (null != bondDialogueQuoteDto && null != bondDialogueQuoteDto.getIbuQuoteBondDto()) {
//				ptlCashPositionFlowDtoUnit.setAmount(bondDialogueQuoteDto.getIbuQuoteBondDto().getFpriceAmt());
//			}
//		}
//		
//		if (ptlCashPositionFlowDtoUnit.getAmount() == null && StringUtil.equals(
//		    dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
//		    ptlCashPositionFlowDtoUnit.setAmount(ArithmeticUtil.multiply(
//		        bondDialogueQuoteDto.getIbuQuoteBondDto().getShareAmt(),
//		        BigDecimal.valueOf(100)));
//		}
		ptlCashPositionFlowDtoUnit.setAmount(fpriceAmt);
		
		ptlCashPositionFlowDtoUnit.setOrderId(dto.getQuoteId());// 报价编号
		ptlCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
        ptlCashPositionFlowDtoUnit.setEntrustId(dto.getEntrustId());
		/*************************************************************************/
		ptlCashPositionFlows.add(ptlCashPositionFlowDtoUnit);
	}
	
	/**
	 * 根据报价编号删除
	 * 
	 * @param quoteId
	 * @return
	 */
	public int doDeletePtlPositionHoldByQuoteId(String quoteId) {
		int affectRows = 0;
		if (null != quoteId) {
			List<String> quoteIds = new ArrayList<String>();
			quoteIds.add(quoteId);
			affectRows += baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(quoteIds);
			affectRows += baseAssetCashPositionCommMapper.deletePositionByTradeId(quoteId);
		}
		return affectRows;
	}
	
	/**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by amyli 
	 * 20201128
	 * @param dto
	 */
	public void doInsetOrUpdateForPtlPositionHold(CfetsBondDealRequestQuoteDto dto) {
		if(StringUtils.isNotEmpty(dto.getQuoteId())){
			//已撤回状态 不删除原流水
			if(!StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())){
				doDeletePtlPositionHoldByQuoteId(dto.getQuoteId());
			}
		}

        // T+1情况，可用持仓登在T日，可用现金登在T+1日
		Date platDate = iPlatformDate.getSysDate();
        final Date settleDate = dto.getSettleDate() == null ? platDate : dto.getSettleDate(); 
        
		// 指令持仓流水 、现金流水处理
		PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();// 组合持仓流水、组合现金流水
		List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();// 现金流水
		List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();// 持仓流水
//		PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
//		PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = new PtlCashPositionFlowDto();// 组合现金流水DTO
		BondRequestQuoteDto bondRequestQuoteDto = ibuMainService.getBondRequestQuote(dto.getQuoteId());
		if(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue()) ||
				StringUtil.equals(dto.getOperateType(), "Restore")){
			if(null != bondRequestQuoteDto && null != bondRequestQuoteDto.getIbuQuoteBondDto()){
				BeanUtil.copyPropertiesIgnoreNull(dto,bondRequestQuoteDto.getIbuQuoteBondDto());
			}
		}
		BigDecimal factor = new BigDecimal(-1);
		if(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())){
			factor = new BigDecimal(1);
		}
		
	    // add by etliu 20210309 银行间上下行多组合 start
        // 银行间报价指令关系业务表
        List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = new ArrayList<>();
        // 画面传入数据，如画面有传，以画面数据为准
        if (!StringUtil.isEmptyList(dto.getCfetsBondDealRequestQuoteDtoList())) {
            for (CfetsBondDealRequestQuoteDto tempDataDto : dto.getCfetsBondDealRequestQuoteDtoList()) {
                IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                ibuQuoteEtrReDto.setQuoteId(dto.getQuoteId());
                ibuQuoteEtrReDto.setPortfolioId(tempDataDto.getPortfolioId());
                ibuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(tempDataDto.getAvaNum(), BigDecimal.valueOf(100))); // 画面上券面总额为万元单位
                if(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_成交中.getValue())
                    || "done".equals(dto.getOperateType()) ){
                    if(StringUtil.equals(dto.getOperateType(), DictCodeListExt3.流程节点_流程退回.getValue())){
                        ibuQuoteEtrReDto.setFpriceAmt(ArithmeticUtil.multiply(
                                ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100)));
                    }else{
                        ibuQuoteEtrReDto.setFpriceAmt(tempDataDto.getFpriceAmt());
                    }
                }else{
                    if (ibuQuoteEtrReDto.getFpriceAmt() == null) {
                        ibuQuoteEtrReDto.setFpriceAmt(ArithmeticUtil.multiply(
                                ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100)));
                    }
                }
                ibuQuoteEtrReDto.setEntrustId(tempDataDto.getEntrustId());
                ibuQuoteEtrReDtoList.add(ibuQuoteEtrReDto);
            }
        }
        // db中数据
        else if (bondRequestQuoteDto != null) {
            ibuQuoteEtrReDtoList = bondRequestQuoteDto.getIbuQuoteEtrReDtoList();
        }
        // add by etliu 20210309 银行间上下行多组合 end
		
		if (DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())|| DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())) {
			/********************** 持仓流水DTO构造 *************************************/
			
            if (StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                getAllPtlSecPositionFlowDto(dto, platDate, ptlSecPositionFlows,
                    bondRequestQuoteDto, factor, null);
            }
            else {// 多组合
                for (IbuQuoteEtrReDto ibuQuoteEtrReDto : ibuQuoteEtrReDtoList) {
                    getAllPtlSecPositionFlowDto(dto, platDate, ptlSecPositionFlows,
                        bondRequestQuoteDto, factor, ibuQuoteEtrReDto);
                }
            }
			
//			ptlSecPositionFlowDtoUnit.setSeqno(getSeqNo());
//			ptlSecPositionFlowDtoUnit.setPortfolioId(dto.getPortfolioId());// 组合代码
//			ptlSecPositionFlowDtoUnit.setSecAcctId("999999"); // 证券账户
//			ptlSecPositionFlowDtoUnit.setFinprodId(dto.getFinprodId());// 金融产品代码
//			ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
////			ptlSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(dto.getInvAim()) ? dto.getInvAim() : "999999");
//			ptlSecPositionFlowDtoUnit.setInvAim(AllTradeCommonUtil.getInvAim(dto.getPortfolioId()));
//			ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
//			ptlSecPositionFlowDtoUnit.setCdate(platDate);// 日期
//			ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//	         //券面总额【单位：万】 * 10000 = 券面总额[单位：元]
//            //持仓数量  = 券面总额[单位：元]/100
//			BigDecimal prinAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(100));
//			ptlSecPositionFlowDtoUnit.setAmount(prinAmt.multiply(factor));// 数量
//			ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
//			ptlSecPositionFlowDtoUnit.setTradeId(dto.getQuoteId());// 成交编号
//			if (StringUtils.isNotEmpty(dto.getPortfolioId()) && StringUtils.isNotEmpty(dto.getFinprodId())) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("portfolioId", dto.getPortfolioId());
//				map.put("finprodId", dto.getFinprodId());
//				String secAcctId = cfetsQuotedCommonMapper.qryPortfolioSecAcctId(map);
//				if (StringUtils.isNotEmpty(secAcctId)) {
//					ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId); // 证券账户
//				}
//			}
//			ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);
			
//	         // T+1场景，可用持仓登录从平台日到交割日的流水
//            if (DateUtil.compareDate(platDate, settleDate) < 0) {
//                Date tempDate = DateUtil.addDay(platDate, 1);
//                while (DateUtil.compareDate(tempDate, settleDate) <= 0) {
//                    PtlSecPositionFlowDto ptlSecPositionFlowDtoTemp = new PtlSecPositionFlowDto();
//                    BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDtoTemp, ptlSecPositionFlowDtoUnit);
//                    ptlSecPositionFlowDtoTemp.setCdate(tempDate);
//                    ptlSecPositionFlows.add(ptlSecPositionFlowDtoTemp);
//
//                    tempDate = DateUtil.addDay(tempDate, 1);
//                }
//            }
			/*************************************************************************/
		} else if (DictCodeList.交易方向_买入.getValue().equals(dto.getPs())) {
			/********************** 持仓流水DTO构造 *************************************/
			
            if (StringUtil.isEmptyList(ibuQuoteEtrReDtoList)) {
                getAllPtlCashpositionFlowDto(dto, settleDate, ptlCashPositionFlows,
                    bondRequestQuoteDto, factor, null);
            }
            else {// 多组合
                for (IbuQuoteEtrReDto ibuQuoteEtrReDto : ibuQuoteEtrReDtoList) {
                    /********************** 持仓流水DTO构造 *************************************/
                    getAllPtlCashpositionFlowDto(dto, settleDate, ptlCashPositionFlows,
                        bondRequestQuoteDto, factor, ibuQuoteEtrReDto);
                }
            }
			
//			ptlCashPositionFlowDtoUnit.setSeqno(getSeqNo());
//			ptlCashPositionFlowDtoUnit.setPortfolioId(dto.getPortfolioId());// 组合代码
//			if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
//				ptlCashPositionFlowDtoUnit.setAcctId(EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(),
//						dto.getCcy() == null ? DictCodeList.币种_人民币_CNY.getValue() : dto.getCcy(),
//						DictCodeListExt2.资金账户类型_托管户.getValue()));
//			} else {
//				ptlCashPositionFlowDtoUnit.setAcctId("999999");
//			}
//			ptlCashPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
//			ptlCashPositionFlowDtoUnit.setCdate(settleDate);// 日期
//			ptlCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//			// if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
//			BigDecimal prinAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(10000));
//			if (null != prinAmt) {
//				ptlCashPositionFlowDtoUnit.setAmount(prinAmt.multiply(factor));
//			}
//			ptlCashPositionFlowDtoUnit.setOrderId(dto.getQuoteId());// 报价编号
//			ptlCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
//			/*************************************************************************/
//			ptlCashPositionFlows.add(ptlCashPositionFlowDtoUnit);
		}

		ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
		ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
		BigDecimal unitFprice = BigDecimal.ONE;
        if(CollectionUtils.isNotEmpty(ibuQuoteEtrReDtoList)) {
            EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(ibuQuoteEtrReDtoList.get(0).getEntrustId());
            unitFprice = unitFprice.multiply(etrEntrustAdd.getFullPrice());
        }
        BigDecimal finalUnitFprice = unitFprice;
        ptlPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> {
            etrCashPositionFlowDto.setCdate(settleDate);
            etrCashPositionFlowDto.setAmount(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(etrCashPositionFlowDto.getAmount(), finalUnitFprice), BigDecimal.valueOf(100), 2, RoundingMode.DOWN));
        });
		ptlPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setCdate(platDate));
        // 数据是否已入新头寸
        if(StringUtil.isNotEmpty(dto.getIsExistNew())){
            ptlPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setIsExistNew(dto.getIsExistNew()));
            ptlPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setIsExistNew(dto.getIsExistNew()));
        }

		// 插入持仓流水
		if(ptlPositionHoldDto.getPtlSecPositionFlows().size() > 0){
			if (!(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
					|| (StringUtil.equals(dto.getOperateType(), "Restore") && StringUtil.equals(dto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_成交中.getValue())))) {
				baseAssetCashPositionCommMapper.deletePositionByTradeId(dto.getQuoteId());
			}
			for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
				basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
			}
		}
		
		// 插入现金流水
		if (ptlPositionHoldDto.getPtlCashPositionFlows().size() > 0) {
			List<String> quoteIds = new ArrayList<>();
			quoteIds.add(dto.getQuoteId());
			if (!(StringUtil.equals(dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())
					|| (StringUtil.equals(dto.getOperateType(), "Restore") && StringUtil.equals(dto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_成交中.getValue())))) {
				baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(quoteIds);
			}
			for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
				basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlow);
			}
		}
	}
	
	
	private void getAllPtlSecPositionFlowDto(CfetsBondDealRequestQuoteDto dto, Date platDate,
			List<PtlSecPositionFlowDto> ptlSecPositionFlows, BondRequestQuoteDto bondRequestQuoteDto,
			BigDecimal factor, IbuQuoteEtrReDto ibuQuoteEtrReDto) {
        String portfolioId = null;
        BigDecimal shareAmt = null;

        if (ibuQuoteEtrReDto != null) {
            portfolioId = ibuQuoteEtrReDto.getPortfolioId();
            shareAmt = ibuQuoteEtrReDto.getShareAmt();
        }
        else {
            portfolioId = dto.getPortfolioId();
            //券面总额【单位：万】 * 10000 = 券面总额[单位：元]
            //持仓数量  = 券面总额[单位：元]/100
            shareAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(100));
        }

		PtlSecPositionFlowDto ptlSecPositionFlowDtoUnit = new PtlSecPositionFlowDto();// 组合持仓流水DTO
		ptlSecPositionFlowDtoUnit.setSeqno(getSeqNo());
		ptlSecPositionFlowDtoUnit.setPortfolioId(portfolioId);// 组合代码
		ptlSecPositionFlowDtoUnit.setSecAcctId("999999"); // 证券账户
		ptlSecPositionFlowDtoUnit.setFinprodId(dto.getFinprodId());// 金融产品代码
		ptlSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
//		ptlSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(dto.getInvAim()) ? dto.getInvAim() : "999999");
		ptlSecPositionFlowDtoUnit.setInvAim(AllTradeCommonUtil.getInvAim(portfolioId));
		ptlSecPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
		ptlSecPositionFlowDtoUnit.setCdate(platDate);// 日期
		ptlSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//         //券面总额【单位：万】 * 10000 = 券面总额[单位：元]
//        //持仓数量  = 券面总额[单位：元]/100
//		BigDecimal prinAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(100));
		ptlSecPositionFlowDtoUnit.setAmount(shareAmt.multiply(factor));// 数量
		ptlSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
		ptlSecPositionFlowDtoUnit.setTradeId(dto.getQuoteId());// 成交编号
		if (StringUtils.isNotEmpty(portfolioId) && StringUtils.isNotEmpty(dto.getFinprodId())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("portfolioId", portfolioId);
			map.put("finprodId", dto.getFinprodId());
			String secAcctId = cfetsQuotedCommonMapper.qryPortfolioSecAcctId(map);
			if (StringUtils.isNotEmpty(secAcctId)) {
				ptlSecPositionFlowDtoUnit.setSecAcctId(secAcctId); // 证券账户
			}
		}
        //委托代码需要登录  下行成交数据回来生成交易数据 需要反冲报价时生成的流水
        if(StringUtil.isNotEmpty(dto.getEntrustId())){
            ptlSecPositionFlowDtoUnit.setEntrustId(dto.getEntrustId());
        }else{
            //多组合委托编号从分配列表取
            ptlSecPositionFlowDtoUnit.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
        }
		ptlSecPositionFlows.add(ptlSecPositionFlowDtoUnit);
	}

	private void getAllPtlCashpositionFlowDto(CfetsBondDealRequestQuoteDto dto, final Date settleDate,
			List<PtlCashPositionFlowDto> ptlCashPositionFlows, BondRequestQuoteDto bondRequestQuoteDto,
			BigDecimal factor, IbuQuoteEtrReDto ibuQuoteEtrReDto) {
        String portfolioId = null;
        BigDecimal fpriceAmt = null;

        if (ibuQuoteEtrReDto != null) {
            portfolioId = ibuQuoteEtrReDto.getPortfolioId();
            if (ibuQuoteEtrReDto.getFpriceAmt() != null) {
                fpriceAmt = ibuQuoteEtrReDto.getFpriceAmt();
            }
            else {
                fpriceAmt = ArithmeticUtil.multiply(ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100));
            }
        }
        else {
            portfolioId = dto.getPortfolioId();
            // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
            fpriceAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(10000));
        }
	    
		PtlCashPositionFlowDto ptlCashPositionFlowDtoUnit = new PtlCashPositionFlowDto();// 组合现金流水DTO
		ptlCashPositionFlowDtoUnit.setSeqno(getSeqNo());
		ptlCashPositionFlowDtoUnit.setPortfolioId(portfolioId);// 组合代码
		if (StringUtil.isNotEmpty(portfolioId)) {
			ptlCashPositionFlowDtoUnit.setAcctId(EtrCommonCashPositionDealService.select组合托管户(portfolioId,
					dto.getCcy() == null ? DictCodeList.币种_人民币_CNY.getValue() : dto.getCcy(),
					DictCodeListExt2.资金账户类型_托管户.getValue()));
		} else {
			ptlCashPositionFlowDtoUnit.setAcctId("999999");
		}
		ptlCashPositionFlowDtoUnit.setHodingType(DictCodeList.持仓类型_可用.getValue());// 持仓类型
		ptlCashPositionFlowDtoUnit.setCdate(settleDate);// 日期
		ptlCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
//		// if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
//		BigDecimal prinAmt = dto.getAvaNum() == null ? dto.getPrinAmt() : dto.getAvaNum().multiply(new BigDecimal(10000));
//		if (null != prinAmt) {
//			ptlCashPositionFlowDtoUnit.setAmount(prinAmt.multiply(factor));
//		}
		ptlCashPositionFlowDtoUnit.setAmount(fpriceAmt.multiply(factor));
		ptlCashPositionFlowDtoUnit.setOrderId(dto.getQuoteId());// 报价编号
		ptlCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
        //委托代码需要登录  下行成交数据回来生成交易数据 需要反冲报价时生成的流水
        if(StringUtil.isNotEmpty(dto.getEntrustId())){
            ptlCashPositionFlowDtoUnit.setEntrustId(dto.getEntrustId());
        }else{
            //多组合委托编号从分配列表取
            ptlCashPositionFlowDtoUnit.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
        }
		/*************************************************************************/
		ptlCashPositionFlows.add(ptlCashPositionFlowDtoUnit);
	}
	
	
	/**
	 * 删除工作流数据
	 */
	public boolean doDeleteWflDataForBond(Map<String, Object> param){
		String workflowId = (String)param.get("workflowId");
        if (workflowId != null) {
        	ibuComponentCommonService.deleteWflDataForBond(workflowId);
            return true;
        }
        return false;
	}
	
	public boolean doDeletePtlPositionHold(Map<String, Object> param){
		String workflowId = (String)param.get("workflowId");
		String pageWflType = (String)param.get("pageWflType");
        if (null != workflowId && null != pageWflType) {
        	String quoteId = ibuComponentCommonService.selectWflQuoteIdByWorkflowId(pageWflType, workflowId);
        	doDeletePtlPositionHoldByQuoteId(quoteId);
        }
        return false;
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
     * 交易日推算
     *
     * @param beforeAdjustDate 调整前日期
     * @param days 调整天数
     * @param holidayList 节假日列表
     * @param flag 是否跨月 1：是、2：否
     * @return 调整后日期
     * @exception Exception 异常
     */
    public Date calculationTradingDay(Date beforeAdjustDate, int days,
                                        List<String> holidayList, String flag) {
        // 交易日计数
        int dayCount = 0;
        // 调整后非跨月日期
        Date noCrossDate = null;
        // 调整后日期
        Date afterAdjustDate = beforeAdjustDate;

        while (dayCount < days) {
            // 中间参数.调整后日期 = 临时变量.调整后日期 + 1日
            Date tempAfterAdjustDate = DateUtil.addDay(afterAdjustDate, 1);
            while (holidayList.indexOf(DateUtil.format(tempAfterAdjustDate)) > -1) {
                tempAfterAdjustDate = DateUtil.addDay(tempAfterAdjustDate, 1);
            }

            if (StringUtil.equals(BussinessDictCodeList.是否_否.getValue(), flag) && !StringUtil.equals(
                DateUtil.format(tempAfterAdjustDate, "yyyyMM"),
                DateUtil.format(beforeAdjustDate, "yyyyMM"))) {
                noCrossDate = afterAdjustDate;
                return noCrossDate;
            }
            else {
                afterAdjustDate = tempAfterAdjustDate;
                dayCount++ ;
            }
        }
        return afterAdjustDate;
    }

	public List<CfetsBondDealDialogueQuoteDto> getCfetsBondDealDialogueQuoteDtoList(Map<String, Object> param) {
		if(param.containsKey("quoteId")&&StringUtil.isNotEmpty(param.get("quoteId"))){
            return cfetsBondDealQuotedPriceMapper.getCfetsBondDealDialogueQuoteDtoList(param);
        }
		return null;
	}
	
	public List<CfetsBondDealRequestQuoteDto> getCfetsBondDealRequestQuoteDtoList(Map<String, Object> param) {
		if(param.containsKey("quoteId")&&StringUtil.isNotEmpty(param.get("quoteId"))){
            return cfetsBondDealQuotedPriceMapper.getCfetsBondDealRequestQuoteDtoList(param);
        }
		return null;
	}


    /**
     * 获取新建界面的资产代码联想下拉框框 <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(int page, int length,Map<String, Object> param) {
        param.put("assestType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_05",DictCodeListExt1.交易场所_机构间私募系统.getValue());
        param.put("TRADE_PLACE_09",DictCodeListExt1.交易场所_OTC场外.getValue());
        param.put("TRADE_PLACE_10",DictCodeListExt1.交易场所_境外交易场所.getValue());
//        param.put("TRADE_PLACE_11", DictCodeListExt3.交易场所_北京金融资产交易所.getValue());
        return this.queryForPageForSelectReturn(page, length,()->cfetsBondDealQuotedPriceMapper.getfinprodIdForAdd(param));
    }

    /**
     * 分页查询
     * @param map 请求参数
     * @return 分页查询结果
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<CstpSelectDto> getCstpQryTableList(Map<String,Object> map) throws Exception{
        if(StringUtil.isNotEmpty((String)map.get("finprodType"))){
            switch ((String)map.get("finprodType")){
                case "T01": map.replace("finprodType", "BOND");
                    break;
                case "T02": {
                    map.put("marketindicator", "9");
                    map.remove("finprodType");
                };
                break;
                case "T03":
                {
                    map.put("marketindicator", "10");
                    map.remove("finprodType");
                }
                ;
                break;
                case "T04":
                {
                    map.put("marketindicator", "8");
                    map.remove("finprodType");
                }
                ;
                break;
                case "T05":
                {
                    map.put("marketindicator", "2");
                    map.remove("finprodType");
                }
                ;
                break;
            }
        }
        if(StringUtil.isNotEmpty((String)map.get("benfangSide")) && StringUtil.equals((String)map.get("benfangSide"),"01")){
            map.put("benfangSide","");
        }
        map.put("TRADE_STATUS", BussinessDictCodeList.交易状态_初始.getValue());
        map.put("PARTMATCH", BussinessDictCodeList.匹配状态_部分匹配.getValue());
        map.put("acctType",BussinessDictCodeList.资金账户类型_托管账户.getValue());
        return cfetsBondDealQuotedPriceMapper.getCstpQryTableList(map);
    }

    public BigDecimal getWflEntrustDealedAmt(Map<String, Object> map){
        return cfetsBondDealQuotedPriceMapper.getWflEntrustDealedAmt(map);
    }

    public List<CstpSelectDto> selectExportPdf(Map<String, Object> map) {
        if (StringUtil.isNotEmpty(map.get("selectedItem"))){
            List<String> selectedItem = StringUtil.splitToList(",",(String)map.get("selectedItem"));
            if(selectedItem.size() > 0){
                map.put("execidList",selectedItem);
            }
        }
        return cfetsBondDealQuotedPriceMapper.selectExportPdf(map);
    }

    public BigDecimal queryEntrustDealedAmt2(Map<String, Object> map){
        return cfetsBondDealQuotedPriceMapper.queryEntrustDealedAmt2(map);
    }
}
