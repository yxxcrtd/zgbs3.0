/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etragrereptrade.service;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade002Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTrade003Dto;
import com.joyintech.service.entrust.etragrereptrade.dto.OnWayRep002Dto;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinDealPledgeMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepAdvBuyDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepExpireDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepExpireUnBuyDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepFristIssueDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepHwayChgSecDto;
import com.joyintech.background.component.order.exchange.business.dto.AgreRepOpenSecDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBlockTradeBondDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBlockTradeFundDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBlockTradePledgeRepoDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBlockTradeStockDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondBiddingDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondFxdIncmPlatSHDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondRepoBiddingDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondRepurchaseDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondRightDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrBondToStockDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrConvBondApplyDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrFundBiddingDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrStandardBondInOutDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrStockBiddingDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrStockNewDto;
import com.joyintech.background.component.order.exchange.business.dto.EtrStockRightDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.entrust.etragrereptrade.dao.EtrAgreRepTradeMapper;
import com.joyintech.service.entrust.etragrereptrade.dto.EtrAgreRepTradeDto;



/**
 * 场内交易委托 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年5月6日
 * @see EtrAgreRepTradeService
 * @since 1.0
 */
@Service
public class EtrAgreRepTradeService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(EtrAgreRepTradeService.class);

    @Autowired
    private EtrAgreRepTradeMapper etrAgreRepTradeMapper;
    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;
    /**
     * 投资委托基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;
    /**
     * 指令分发接口
     */
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;
    
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     *回购质押品
     */
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;
    
    public static final String ENTRUST_RECORD_MODIFY = "modify";
    
    public static final String ENTRUST_RECORD_NEW = "new";

    /**
     * 对手方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterTrader(Map<String, String> map) {
        List<SuggestDto> suggestDtos = etrAgreRepTradeMapper.selectCounterTrader(map);
        return suggestDtos;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectTrader(Map<String, String> map) {
        List<SuggestDto> suggestDtos = etrAgreRepTradeMapper.selectTrader(map);
        return suggestDtos;
    }
    /**
     * 查询原成交信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<EtrAgreRepTradeDto> getEntrustInfoByTradeId(Map<String, String> map) {
    	return etrAgreRepTradeMapper.getEntrustInfoByTradeId(map);
    }
    /**
     * 获取指令信息
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<EtrAgreRepTradeDto> getEntrustInfoByEntrustId(Map<String, String> map) {
    	return etrAgreRepTradeMapper.getEntrustInfoByEntrustId(map);
    }
    /**
     * 持仓信息查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrAgreRepTrade002Dto> findPositionsPage(int page, int length,
                                                          Map<String, Object> pageParam) {
        String portfolioId = (String)pageParam.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        pageParam.put("sysDate", iPlatformDate.getSysDate());
        pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
        pageParam.put("VALUATION_SOURCE_CSD", BussinessDictCodeList.估值来源_中证登.getValue());
        pageParam.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        pageParam.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        pageParam.put("PS_T01", BussinessDictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", BussinessDictCodeList.交易方向_卖出.getValue());
        pageParam.put("PS_T03", BussinessDictCodeList.交易方向_行权回售.getValue());
        pageParam.put("PS_T05", BussinessDictCodeList.交易方向_申购确认.getValue());
        pageParam.put("PS_T19", BussinessDictCodeList.交易方向_赎回确认.getValue());
        pageParam.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        pageParam.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        pageParam.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        pageParam.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        pageParam.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        pageParam.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        // 浮动盈亏计算修改 modify by yufei 20200806 start
        Page<EtrAgreRepTrade002Dto> result = super.queryForPage(page, length, () -> etrAgreRepTradeMapper.findPositionsPage(pageParam));
        // 迁移至SQL中计算，方便排序
//        if(!StringUtil.isEmptyList(result.getRoot())){
//            // 计算浮动盈亏
//            for(EtrAgreRepTrade002Dto dto : result.getRoot()){
//                // 最新净价*组合持仓.数量 - 持仓成本 as 浮动盈亏
//                BigDecimal floatPL = ArithmeticUtil.multiply(dto.getPrice(),dto.getPtlPositionsNum());
//                floatPL = ArithmeticUtil.subtract(floatPL,dto.getPositionsAmt());
//                dto.setFloatingPL(floatPL);
//            }
//        }
        return result;
        // 浮动盈亏计算修改 modify by yufei 20200806 end
    }

    /**
     * 在途回购查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<OnWayRep002Dto> findOnWayRepPage(int page, int length,
                                                 Map<String, Object> pageParam) {

        Page<OnWayRep002Dto> result = super.queryForPage(page, length, () -> etrAgreRepTradeMapper.findOnWayRepPage(pageParam));
        return result;
    }
    
    /**
     * 构建流程表数据
     * @param
     * @return
     */
    public BaseEtrInvestInfoDto buildWflEntity(EtrAgreRepTradeDto entity) throws IllegalAccessException, InvocationTargetException {
    	BaseEtrInvestInfoDto dto = new BaseEtrInvestInfoDto();
    	EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        List<EtrPledge> list = new ArrayList<>();
        EtrPledge etrPledge = new EtrPledge();
        etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
        //协议回购首期指令
    	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
    	//协议回购提前购回指令
    	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
    	//协议回购到期购回指令
    	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
    	//协议回购到期续作指令
    	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
    	//协议回购中途换券指令
    	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
    	//协议回购解除质押指令
    	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            
            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H04");
            }
            
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepFristIssueDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepFristIssueDto);
            if(StringUtil.isNotEmpty(agreRepFristIssueDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepFristIssueDto);
            	list.add(etrPledge);
            }
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepAdvBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepAdvBuyDto);
            if(StringUtil.isNotEmpty(agreRepAdvBuyDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepAdvBuyDto);
            	list.add(etrPledge);
            }
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireDto);
            if(StringUtil.isNotEmpty(agreRepExpireDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepExpireDto);
            	list.add(etrPledge);
            }
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireUnBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireUnBuyDto);
            if(StringUtil.isNotEmpty(agreRepExpireUnBuyDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepExpireUnBuyDto);
            	list.add(etrPledge);
            }
            etrEntrustAdd.setRedoType(entity.getRedoType());
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            //质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));
            
            // 质押数量
            agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
  
            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());
   
            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getNewRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepHwayChgSecDto.getTradeId());
            List<EtrAgreRepTradeDto> repFristList = etrAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepHwayChgSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepHwayChgSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepHwayChgSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepHwayChgSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepHwayChgSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepHwayChgSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }
                });
            }

            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepHwayChgSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepHwayChgSecDto);
            if(StringUtil.isNotEmpty(agreRepHwayChgSecDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepHwayChgSecDto);
            	list.add(etrPledge);
            }
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());
            
            // 质押数量
            agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
  
            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepOpenSecDto.getTradeId());
            List<EtrAgreRepTradeDto> repFristList = etrAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepOpenSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepOpenSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepOpenSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepOpenSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepOpenSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepOpenSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }

                    //质押率
                    if(item.getOrigConRatio()!=null){
                        agreRepOpenSecDto.setPledgeRatio(ArithmeticUtil.divide(item.getOrigConRatio(),new BigDecimal(100),10));
                    }

                    //面值 = 首期的面值
                    FinDealPledgeKey key = new FinDealPledgeKey();
                    key.setFinprodId(item.getTradeId());
                    key.setBranch(0L);
                    key.setPledgeFinprodId(agreRepOpenSecDto.getFinprodId());
                    FinDealPledge finDealPledge = finDealPledgeMapper.selectByPrimaryKey(key);
                    if(finDealPledge != null) {
                        agreRepOpenSecDto.setPledgeFaceValue(finDealPledge.getPledgeFaceValue());
                        agreRepOpenSecDto.setPledgeAmt(finDealPledge.getPledgeAmt());
                    }
                });
            }


            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepOpenSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepOpenSecDto);
            if(StringUtil.isNotEmpty(agreRepOpenSecDto.getFinprodId())) {
            	BeanUtil.copyPropertiesIgnoreNull(etrPledge, agreRepOpenSecDto);
            	list.add(etrPledge);
            }
        }
        dto.setEtrEntrust(etrEntrust);
        dto.setEtrEntrustAdd(etrEntrustAdd);
        dto.setEtrPledgeList(list);
        return dto;
    }
    /**
     * 工作流完成方法
     * @param entity
     * @param operationType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void doComplete(EtrAgreRepTradeDto entity, String operationType) throws IllegalAccessException, InvocationTargetException {
        //BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
       // EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
     //   entrustCommonDto.setEtrEntrust(etrEntrust);
       // entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
//        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
//            FinProductTypeKey key = new FinProductTypeKey();
//            key.setFinprodId(dto.getFinprodId());
//            key.setBranch(new Long(0));
//            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
//            if (null != finProductType) {
//                etrEntrustAdd.setAssetType(finProductType.getType8());
//            }else {
//                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
//            }
//        } else {
           
//        }

        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 start
        String isPushMsg = DictCodeList.是否_否.getValue();
        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 end

        // 构造投资指令DTO
        //EtrBaseDto baseDto = getEtrBaseDto(entity);

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
        
        //待分发逻辑处理
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        if (busiAutoEtrDispService != null) {
            // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
            // 设置指令日期 add by yufei 20200808 start
            if(etrEntrust.getEntrustDate() == null) {
                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
            }
            // 设置指令日期 add by yufei 20200808 end
            if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                // 自动分发
            	etrDistributeList = busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd);
                isPushMsg = DictCodeList.是否_是.getValue();
            }
            
        }
        if(StringUtil.isEmptyList(etrDistributeList)){//无分发人员
        	etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        }
        
        //协议回购首期指令
    	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
    	//协议回购提前购回指令
    	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
    	//协议回购到期购回指令
    	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
    	//协议回购到期续作指令
    	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
    	//协议回购中途换券指令
    	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
    	//协议回购解除质押指令
    	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            
            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepFristIssueDto.setAssetType10("H04");
            }
            
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepFristIssueDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepFristIssueDto);

        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepAdvBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepAdvBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepAdvBuyDto);
    
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireDto);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepExpireUnBuyDto.setAssetType10("H04");
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepExpireUnBuyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepExpireUnBuyDto);
            etrEntrustAdd.setRedoType(entity.getRedoType());
            agreRepExpireUnBuyDto.setRedoType(entity.getRedoType());
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            //质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));
            
            // 质押数量
            agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
  
            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());
   
            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getNewRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepHwayChgSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepHwayChgSecDto.getTradeId());
            List<EtrAgreRepTradeDto> repFristList = etrAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepHwayChgSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepHwayChgSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepHwayChgSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepHwayChgSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepHwayChgSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepHwayChgSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }
                });
            }

            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepHwayChgSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepHwayChgSecDto);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	
            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());
            
            // 质押数量
            agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
  
            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());
            // 目标类型
            agreRepOpenSecDto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H03");
            }else if(DictCodeList.回购拆借方向_逆回购.getValue().equals(entity.getRepPs())){
            	agreRepOpenSecDto.setAssetType10("H04");
            }

            //无法从页面带回购首期的部分数据，需要重新查一次
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("tradeId",agreRepOpenSecDto.getTradeId());
            List<EtrAgreRepTradeDto> repFristList = etrAgreRepTradeMapper.getEntrustInfoByTradeId(queryMap);
            if(!StringUtil.isEmptyList(repFristList)) {
                repFristList.forEach(item->{
                    // 到期应计利息
                    agreRepOpenSecDto.setmIntAmt(item.getIntAmt());

                    //到期结算金额
                    agreRepOpenSecDto.setmTradeAmt(item.getExpireSettleAmt());

                    // 首次交收日
                    if(StringUtil.isNotEmpty(item.getFristSettleDate())){
                        agreRepOpenSecDto.setVpayDate(DateUtil.parse(item.getFristSettleDate()));
                    }
                    if(StringUtil.isNotEmpty(item.getExpireSettleDate())){
                        // 到期交收日
                        agreRepOpenSecDto.setMpayDate(DateUtil.parse(item.getExpireSettleDate()));
                    }

                    //占款天数
                    if(item.getOccupyDays()!=null){
                        agreRepOpenSecDto.setCapDays(BigDecimal.valueOf(item.getOccupyDays()));
                    }

                    // 期限天数，到期日-起息日
                    if(item.getTerm()!=null){
                        agreRepOpenSecDto.setTermDays(Long.valueOf(item.getTerm()));
                    }

                    //质押率
                    if(item.getOrigConRatio()!=null){
                        agreRepOpenSecDto.setPledgeRatio(ArithmeticUtil.divide(item.getOrigConRatio(),new BigDecimal(100),10));
                    }

                    //面值 = 首期的面值
                    FinDealPledgeKey key = new FinDealPledgeKey();
                    key.setFinprodId(item.getTradeId());
                    key.setBranch(0L);
                    key.setPledgeFinprodId(agreRepOpenSecDto.getFinprodId());
                    FinDealPledge finDealPledge = finDealPledgeMapper.selectByPrimaryKey(key);
                    if(finDealPledge != null) {
                        agreRepOpenSecDto.setPledgeFaceValue(finDealPledge.getPledgeFaceValue());
                        agreRepOpenSecDto.setPledgeAmt(finDealPledge.getPledgeAmt());
                    }
                });
            }


            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, agreRepOpenSecDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, agreRepOpenSecDto);
        }

        String optType = ENTRUST_RECORD_NEW;
        
		if (null != entity.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId())) {
			optType = ENTRUST_RECORD_MODIFY;
		}
		EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        if (StringUtil.equals(optType, ENTRUST_RECORD_NEW)) {//
        	if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        		 EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
                 EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustNew(agreRepFristIssueDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            	EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustNew(agreRepAdvBuyDto);
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            	EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustNew(agreRepExpireDto);
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            	EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustNew(agreRepExpireUnBuyDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            	EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustNew(agreRepHwayChgSecDto);         
            }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            	EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
            	EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustNew(agreRepOpenSecDto);
            }
        } else {
        	if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                //退回后改变质押券后需要更新指令质押券信息，这里设置旧的质押券金融产品代码
        	    EtrEntrust oldEtrEntrust = etrEntrustMapper.selectByPrimaryKey(agreRepFristIssueDto.getEntrustId());
                if(StringUtil.isNotEmpty(oldEtrEntrust)) {
                    agreRepFristIssueDto.setOldFinprodId(oldEtrEntrust.getFinprodId());
                }

       		    EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustEdit(agreRepFristIssueDto);
           } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
           	    EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustEdit(agreRepAdvBuyDto);
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
           	    EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustEdit(agreRepExpireDto);
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
           	    EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustEdit(agreRepExpireUnBuyDto);
           } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                //退回后改变质押券后需要更新指令质押券信息，这里设置旧的质押券金融产品代码
                EtrEntrust oldEtrEntrust = etrEntrustMapper.selectByPrimaryKey(agreRepHwayChgSecDto.getEntrustId());
                if(StringUtil.isNotEmpty(oldEtrEntrust)) {
                    agreRepHwayChgSecDto.setOldFinprodId(oldEtrEntrust.getFinprodId());
                }
                EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustEdit(agreRepHwayChgSecDto);         
           }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
           	    EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
           	    EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustEdit(agreRepOpenSecDto);
           }
        }
       
        // 自动分发
//        List<EtrDistribute> etrDistributeList = new ArrayList<>();
//        if (busiAutoEtrDispService != null) {
//            // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
//            // 设置指令日期 add by yufei 20200808 start
//            if(etrEntrust.getEntrustDate() == null) {
//                etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
//            }
//            // 设置指令日期 add by yufei 20200808 end
//            if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
//                // 自动分发
//            	etrDistributeList = busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd);
//                isPushMsg = DictCodeList.是否_是.getValue();
//            }
//            
//        }
        // 自动分发数据入库 add by yufei 20200808 start
        if(!StringUtil.isEmptyList(etrDistributeList)){
            baseEtrInvestInfoService.distributeDelegate(etrDistributeList);
        }
        // 自动分发数据入库 add by yufei 20200808 end
        // add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", entity.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
        // 自动分发自动接收 ，杭银投资poc add by yufei 20200905 start
        if(!StringUtil.isEmptyList(etrDistributeList) && etrDistributeList.size() == 1){
            baseEtrInvestInfoService.receptionDelegate(etrDistributeList);
            // 上交所和深交所推送接收信息到交易系统
            if (StringUtil.equals(entity.getTradeMarket(),
                DictCodeListExt1.交易场所_上海证券交易所.getValue()) || StringUtil.equals(
                		entity.getTradeMarket(),
                DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                // TODO 调用交易接口做接收操作
            }
        }
        // 自动分发自动接收 ，杭银投资poc add by yufei 20200905 end
    }
    /**
     * 配置用户
     */
    public void setUser(EtrParameterDto paraDto){
        User user = new User();
        user.setUserName(LoginUserUtil.getUserName());
        user.setDeptCode(LoginUserUtil.getDeptCode());
        paraDto.setUser(user);
    }
    /**
     * 保存check
     * @param entity
     * @return
     */
    public BindingResult checkSave(@RequestBody EtrAgreRepTradeDto entity, BindingResult result)
        throws IllegalAccessException, InvocationTargetException {

    

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
        etrSinglePortfolioDto.setStockId("11");
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);

        if (DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //协议回购首期指令
        	AgreRepFristIssueDto agreRepFristIssueDto = new AgreRepFristIssueDto();
            BeanUtil.copyProperties(agreRepFristIssueDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepFristIssueDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
                agreRepFristIssueDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepFristIssueDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepFristIssueDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepFristIssueDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepFristIssueDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepFristIssueDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepFristIssueDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            if(StringUtil.isNotEmpty(entity.getConRatio())) {
                agreRepFristIssueDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            }
            // 质押金额
            agreRepFristIssueDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
                agreRepFristIssueDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepFristIssueDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepFristIssueDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepFristIssueDto.setTrader(entity.getTrader());
            // 约定号
            agreRepFristIssueDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepFristIssueDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepFristIssueDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepFristIssueDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepFristIssueDto.setRepSecId(entity.getRepSecId());
            //回购利率
            if(StringUtil.isNotEmpty(entity.getYield())) {
                agreRepFristIssueDto.setYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),10));
            }
            EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepFristIssueDto.class).entrustCheck(agreRepFristIssueDto, result);
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购提前购回指令
        	AgreRepAdvBuyDto agreRepAdvBuyDto = new AgreRepAdvBuyDto();
            BeanUtil.copyProperties(agreRepAdvBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepAdvBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepAdvBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepAdvBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepAdvBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepAdvBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepAdvBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepAdvBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepAdvBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepAdvBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepAdvBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepAdvBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepAdvBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepAdvBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepAdvBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepAdvBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepAdvBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepAdvBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepAdvBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepAdvBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
                agreRepAdvBuyDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepAdvBuyDto.class).entrustCheck(agreRepAdvBuyDto, result);
    
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购到期购回指令
        	AgreRepExpireDto agreRepExpireDto = new AgreRepExpireDto();
            BeanUtil.copyProperties(agreRepExpireDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepExpireDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepExpireDto.class).entrustCheck(agreRepExpireDto, result);
    
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购到期续作指令
        	AgreRepExpireUnBuyDto agreRepExpireUnBuyDto = new AgreRepExpireUnBuyDto();
            BeanUtil.copyProperties(agreRepExpireUnBuyDto, etrSinglePortfolioDto);
            // 首次交收日
            if(StringUtil.isNotEmpty(entity.getFristSettleDate())){
            	agreRepExpireUnBuyDto.setVpayDate(DateUtil.parse(entity.getFristSettleDate()));
            }
            if(StringUtil.isNotEmpty(entity.getExpireSettleDate())){
            	// 到期交收日
            	agreRepExpireUnBuyDto.setMpayDate(DateUtil.parse(entity.getExpireSettleDate()));
            }
            // 到期应计利息
            agreRepExpireUnBuyDto.setmIntAmt(entity.getIntAmt());
            //到期结算金额
            agreRepExpireUnBuyDto.setmTradeAmt(entity.getExpireSettleAmt());
            //占款天数
            if(entity.getOccupyDays()!=null){
            	agreRepExpireUnBuyDto.setCapDays(BigDecimal.valueOf(entity.getOccupyDays()));
            }
            
            // 请求号
            agreRepExpireUnBuyDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepExpireUnBuyDto.setPledgeFaceValue(entity.getPledgeFaceValue());
            
            // 质押数量
            agreRepExpireUnBuyDto.setPledgeNumber(entity.getRepSecNum());
            // 质押率
            agreRepExpireUnBuyDto.setPledgeRatio(ArithmeticUtil.divide(entity.getConRatio(),new BigDecimal(100),10));
            // 质押金额
            agreRepExpireUnBuyDto.setPledgeAmt(entity.getTradeAmt());
            // 期限天数，到期日-起息日
            if(entity.getTerm()!=null){
            	agreRepExpireUnBuyDto.setTermDays(Long.valueOf(entity.getTerm()));
            }      
            // 交易对手
            agreRepExpireUnBuyDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepExpireUnBuyDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepExpireUnBuyDto.setTrader(entity.getTrader());
            // 约定号
            agreRepExpireUnBuyDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepExpireUnBuyDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepExpireUnBuyDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepExpireUnBuyDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepExpireUnBuyDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepExpireUnBuyDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepExpireUnBuyDto.class).entrustCheck(agreRepExpireUnBuyDto, result);
        } else if (DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购中途换券指令
        	AgreRepHwayChgSecDto agreRepHwayChgSecDto = new AgreRepHwayChgSecDto();
            BeanUtil.copyProperties(agreRepHwayChgSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepHwayChgSecDto.setRequestId(entity.getReqOnId());
            // 质押面值
            agreRepHwayChgSecDto.setPledgeFaceValue(entity.getNewpledgeFaceValue());
            // 质押率
            agreRepHwayChgSecDto.setPledgeRatio(ArithmeticUtil.divide(entity.getNewConRatio(),new BigDecimal(100),10));
            // 质押数量
            agreRepHwayChgSecDto.setPledgeNumber(entity.getNewRepSecNum());
  
            // 质押金额
            agreRepHwayChgSecDto.setPledgeAmt(entity.getTradeAmt());

            // 交易对手
            agreRepHwayChgSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepHwayChgSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepHwayChgSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepHwayChgSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepHwayChgSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepHwayChgSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepHwayChgSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepHwayChgSecDto.setRepSecId(entity.getRepSecId());
            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepHwayChgSecDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepHwayChgSecDto.class).entrustCheck(agreRepHwayChgSecDto, result);
        }else if (DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
        	//协议回购解除质押指令
        	AgreRepOpenSecDto agreRepOpenSecDto = new AgreRepOpenSecDto();
            BeanUtil.copyProperties(agreRepOpenSecDto, etrSinglePortfolioDto);  
            
            // 请求号
            agreRepOpenSecDto.setRequestId(entity.getReqOnId());
            
            // 质押数量
            agreRepOpenSecDto.setPledgeNumber(entity.getRepSecNum());
  
            // 交易对手
            agreRepOpenSecDto.setCounterId(entity.getCounterId());
            // 对手方交易员
            agreRepOpenSecDto.setCounterTrader(entity.getCounterTrader());

            // 本方交易员
            agreRepOpenSecDto.setTrader(entity.getTrader());
            // 约定号
            agreRepOpenSecDto.setCounterConNo(entity.getAgreeOnId());
            // 席位号
            agreRepOpenSecDto.setCounterSeat(entity.getSeatId());
            //备注
            agreRepOpenSecDto.setRemark(entity.getAddCla());
            //回购方向
            agreRepOpenSecDto.setBusiPs(entity.getRepPs());
            // 质押券代码
            agreRepOpenSecDto.setRepSecId(entity.getRepSecId());

            if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	 //成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getIntiTradeId());
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            	//成交编号/初始交易编号
            	agreRepOpenSecDto.setTradeId(entity.getTradeId());
            }
            EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(AgreRepOpenSecDto.class).entrustCheck(agreRepOpenSecDto, result);
        }
       
        return result;
    }
    /**
     * 构造单组合单资产DTO
     * @param dto
     * @return
     */
    public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrAgreRepTradeDto dto) {
        //构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        // 构造单组合单资产DTO start
        EtrSinglePortfolioDto etrSinglePortfolioDto = new EtrSinglePortfolioDto();
        BeanUtil.copyPropertiesIgnoreNull(etrSinglePortfolioDto, baseDto);
        etrSinglePortfolioDto.setPortfolioId(dto.getPortfolioId());// 组合代码
        if(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue().equals(dto.getEntrustBussType())){
        	etrSinglePortfolioDto.setStockId(dto.getNewRepSecId());// 证券代码
            etrSinglePortfolioDto.setFinprodId(dto.getNewfinprodId());// 证券代码对应的金融产品代码
        }else{
        	etrSinglePortfolioDto.setStockId(dto.getRepSecId());// 证券代码
            etrSinglePortfolioDto.setFinprodId(dto.getFinprodId());// 证券代码对应的金融产品代码
        }
        etrSinglePortfolioDto.setSecAcctId(dto.getSecAcctId());// 证券账户
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        etrSinglePortfolioDto.setPs(dto.getPs());// 委托方向
        etrSinglePortfolioDto.setPriceModel(DictCodeListExt2.价格模式_限价指令.getValue());// 价格模式
        etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100),10));// 指令价格
        etrSinglePortfolioDto.setEntrustNum(dto.getTradeAmt());// 指令数量
        etrSinglePortfolioDto.setEntrustAmt(dto.getTradeAmt());// 指令金额
        etrSinglePortfolioDto.setInvAim(null);// 投资目的
        etrSinglePortfolioDto.setTradeMarket(dto.getTradeMarket());// 交易场所
        //etrSinglePortfolioDto.setTradePlat(dto.getTradePlat());// 交易平台
        /*
         * if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.深圳证券交易所
         * 交易平台 = 数据字典.交易平台.综合协议平台
         * else if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.上海证券交易所
         * 交易平台 = 数据字典.交易平台.大宗交易平台 end
         */
        if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_综合协议交易平台.getValue());

        } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_固定收益平台.getValue());
        }

        /***********************************************************************/
        
        if(DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getRepPs())){
        	etrSinglePortfolioDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());// 金融产品分类
        }else{
        	etrSinglePortfolioDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());// 金融产品分类
        }
        etrSinglePortfolioDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        etrSinglePortfolioDto.setAssetType1("H01");
//        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
//        /***********************************************************************/
//        etrSinglePortfolioDto.setDealMode(dto.getDeclaType());//申报类型-db 交易方式
        // 构造单组合单资产DTO end
        return etrSinglePortfolioDto;
    }
    /**
     * 构造投资指令DTO
     * @param dto
     * @return
     */
    public EtrBaseDto getEtrBaseDto(EtrAgreRepTradeDto dto) {
        // 构造投资指令DTO start
        EtrBaseDto baseDto = new EtrBaseDto();
        baseDto.setMdate(DateUtil.parse(dto.getMdate()));// 指令起始日
        baseDto.setVdate(DateUtil.parse(dto.getVdate()));// 指令结束日
        baseDto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());// 指令状态
        // 指令接收日期 NULL
        // 指令关闭日期 NULL
        baseDto.setInvManagerId(dto.getInvManagerId());// 登录用户.用户ID
        baseDto.setCreateUser(dto.getCreateUser());// 创建用户
        baseDto.setCreateDept(dto.getCreateDept());// 创建部门
        baseDto.setCreateTime(dto.getCreateTime());// 创建时间
        baseDto.setUpdateUser(LoginUserUtil.getUserName());// 更新用户
        baseDto.setUpdateTime(new Date());// 更新时间
        baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        baseDto.setEntrustType(dto.getEntrustBussType());// 指令业务类型
        baseDto.setEntrustId(dto.getEntrustId());// 指令编号
        if (iPlatformDate != null) {
            baseDto.setEntrustDate(iPlatformDate.getSysDate());// 委托日期
        }
        baseDto.setTargetType(DictCodeListExt3.目标类型_金额.getValue());// 目标类型
        // 构造投资指令DTO end
        return baseDto;
    }
    public Page<EtrAgreRepTrade003Dto> selectEtrEntrust(int page, int length, Map<String,Object> param){
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        return super.queryForPage(page, length, () -> etrAgreRepTradeMapper.selectEtrEntrust(param));
    }

    public List<SelectOptionDto> getptlAcctNo(Map<String, String> map) {
        return etrAgreRepTradeMapper.getptlAcctNo(map);
    }

    public BigDecimal queryOtherSecPosition(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        param.put("sysDate", DateUtil.format(sysDate));
        // 查询最大日期
        param.put("cdate",sysDate);
        param.put("hodingType",DictCodeListExt1.持仓类型_委托指令可用.getValue());
        Date maxCdate = etrAgreRepTradeMapper.selectSecPositionMaxCdate(param);
        param.put("cdate",maxCdate);
        if (maxCdate == null) {
            return BigDecimal.ZERO;
        }
        return etrAgreRepTradeMapper.selectAvailablePositionAmount(param);
    }

    /**
     * 查询首期的到期结算金额
     *
     * @param map 检索参数
     * @return 到期结算金额
     */
    public BigDecimal getFirstTradeAmtByTradeId(Map<String, String> map) {
        return etrAgreRepTradeMapper.getFirstTradeAmtByTradeId(map);
    }
}
