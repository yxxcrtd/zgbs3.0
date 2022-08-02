/*
 * 文件名：TradeCommonValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2019年1月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.common.validator;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.dao.CommonBusinessMapper;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.trade.dto.BussTrdTransfenpeiDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskNamListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesDealCommon;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.bondlend.dto.TrdBondLendInvestTradeDto;
import com.joyintech.tams.invest.trade.bondlend.service.TrdBondLendInvestTrade002Service;
import com.joyintech.tams.invest.trade.bondtostock.dto.TrdBondToStock001Dto;
import com.joyintech.tams.invest.trade.bondtostock.service.TrdBondToStock002Service;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTrade002Dto;
import com.joyintech.tams.invest.trade.buyoutrepo.dto.TrdOutRightRepoTradePtl;
import com.joyintech.tams.invest.trade.buyoutrepo.service.TrdOutRightRepoTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.conbond.dto.TrdConBondDto;
import com.joyintech.tams.invest.trade.conbond.service.TrdConBond002Service;
import com.joyintech.tams.invest.trade.forwex.dto.TrdForwexInvestTradeDto;
import com.joyintech.tams.invest.trade.forwex.service.TrdForwexInvestTrade002Service;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.fund.service.TrdFundInvestTrade002Service;
import com.joyintech.tams.invest.trade.irs.dto.TrdIRSTradeDto;
import com.joyintech.tams.invest.trade.irs.service.TrdIRSTrade002Service;
import com.joyintech.tams.invest.trade.lend.dto.TrdLendInvestTradeDto;
import com.joyintech.tams.invest.trade.lend.service.TrdLendInvestTrade002Service;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.tams.invest.trade.planccy.service.TrdPlanCcyInvestTrade002Service;
import com.joyintech.tams.invest.trade.plannet.controller.TrdPlanNetInvestTrade002Controller;
import com.joyintech.tams.invest.trade.plannet.dto.TrdPlanNetInvestTradeDto;
import com.joyintech.tams.invest.trade.project.dto.TrdProjectInvestTrade002Dto;
import com.joyintech.tams.invest.trade.project.service.TrdProjectInvestTrade002Service;
import com.joyintech.tams.invest.trade.spotex.dto.TrdSpotexInvestTradeDto;
import com.joyintech.tams.invest.trade.spotex.service.TrdSpotexInvestTrade002Service;
import com.joyintech.tams.invest.trade.stockdeal.dto.TrdStockDto;
import com.joyintech.tams.invest.trade.stockdeal.service.TrdStock002Service;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.dto.TrdDepositInvestTrade001Dto;
import com.joyintech.tams.invest.trade.trddepositinvesttrade.service.TrdDepositInvestTrade002Service;
import com.joyintech.tams.invest.trade.trdfxswapinvesttrade.dto.TrdFxswapInvestTrade001Dto;
import com.joyintech.tams.invest.trade.trdfxswapinvesttrade.service.TrdFxswapInvestTrade002Service;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.dto.TrdPlanRateInvestTradeDto;
import com.joyintech.tams.invest.trade.trdplanrateinvesttrade.service.TrdPlanRateInvestTrade002Service;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 交易共通Validator
 *
 * @author etliu
 * @version 1.0 2019年1月30日
 * @see TradeCommonValidator
 * @since 1.0
 */
public interface TradeCommonValidator extends BeanValidator {

    /**
     * 日志
     */
    Logger logger = LoggerFactory.getLogger(TradeCommonValidator.class);


    /**
     * 验证方法
     *
     * @param value 需要被验证的业务对象
     * @return 验证消息，如果为空则表示验证通过
     * @since 1.0
     */
    @Override
    default List<ValidMessage> valid(Object value) {
        // 先执行各个画面的业务check
        List<ValidMessage> validMessageList = this.bussValid(value);
        //收息,还本,到期日当天禁止转仓 S
        Map<String, Object> map = new HashMap<String, Object>();
        int count =0;
        TradeCommonService tradeCommonService = SpringContextUtil.getBean(TradeCommonService.class);
        boolean isCreditSync = false;
        String isOutTrade = "";
        // 债券投资交易
        /*if (value instanceof TrdBondInvestTradeDto) {
            TrdBondInvestTradeDto inputDto = (TrdBondInvestTradeDto)value;
            if(DictCodeList.交易方向_卖出.getValue().equals(inputDto.getPs())) {
                map.put("payDate", inputDto.getSettleDate());
                map.put("finprodId", inputDto.getFinprodId());
                count = tradeCommonService.getPayTrad(map);
            } else if (DictCodeList.交易方向_买入.getValue().equals(inputDto.getPs())) {
                isCreditSync = true;
                isOutTrade = inputDto.getTrdInvestPersonInfo().getIsOutTrade();
            }
        }// 存款类资产投资交易
        else if (value instanceof TrdDepositInvestTrade001Dto) {
            TrdDepositInvestTrade001Dto inputDto = (TrdDepositInvestTrade001Dto)value;
            if(DictCodeList.交易方向_卖出.getValue().equals(inputDto.getPs())) {
                map.put("payDate", inputDto.getSettleDate());
                map.put("finprodId", inputDto.getFinprodId());
                count = tradeCommonService.getPayTrad(map);
            }
        }// 计划类资产（利率型）交易
        else if (value instanceof TrdPlanRateInvestTradeDto) {
            TrdPlanRateInvestTradeDto inputDto = (TrdPlanRateInvestTradeDto)value;
            if(DictCodeList.交易方向_卖出.getValue().equals(inputDto.getPs())) {
                map.put("payDate", inputDto.getSettleDate());
                map.put("finprodId", inputDto.getFinprodId());
                count = tradeCommonService.getPayTrad(map);
            } else if (DictCodeList.交易方向_买入.getValue().equals(inputDto.getPs())) {
                isCreditSync = true;
                isOutTrade = inputDto.getTrdInvestPersonInfo002Dto().getIsOutTrade();
            }

        }/// 项目类资产交易
        else if (value instanceof TrdProjectInvestTrade002Dto) {
            TrdProjectInvestTrade002Dto inputDto = (TrdProjectInvestTrade002Dto)value;
            if(DictCodeList.交易方向_卖出.getValue().equals(inputDto.getPs())) {
                map.put("payDate", DateUtil.parse(inputDto.getSettleDate()));
                map.put("finprodId", inputDto.getFinprodId());
                count = tradeCommonService.getPayTrad(map);
            } else if (DictCodeList.交易方向_买入.getValue().equals(inputDto.getPs())) {
                isCreditSync = true;
                isOutTrade = inputDto.getTrdInvestPersonInfo().getIsOutTrade();
            }
        }
        if(count > 0) {
            ValidMessage validMessage = new ValidMessage("收息,还本,到期日当天禁止卖出!");
            validMessageList.add(validMessage);
            return validMessageList;
        }*/

        //收息,还本,到期日当天禁止转仓 E

        //名单管理  isNameSoftControl true:软控  false:硬控
        boolean isNameSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_NAME_SOFTCONTROL"));
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        //授信管理  isCreditSoftControl true:软控  false:硬控 
        boolean isCreditSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CREDIT_SOFTCONTROL")); 
        isAuthorizationSoftcontrol = StringUtil.isNotEmpty(isAuthorizationSoftcontrol)&&isAuthorizationSoftcontrol;
        RiskNamListCommonValidatorService riskNamListCommonValidatorService = SpringContextUtil.getBean(
                RiskNamListCommonValidatorService.class);
        //add by lpf 产品到期不能做交易 start 20220712
        Field portfolioIdField1 = ReflectionUtils.findField(value.getClass(), "portfolioId");//交易对手类型
        Field tradeDateField = ReflectionUtils.findField(value.getClass(), "tradeDate");//交易方向
        if (portfolioIdField1 != null && tradeDateField != null) {
            ReflectionUtils.makeAccessible(portfolioIdField1);
            ReflectionUtils.makeAccessible(tradeDateField);
            Object portfolioId = ReflectionUtils.getField(portfolioIdField1, value);
            Object tradeDate = ReflectionUtils.getField(tradeDateField, value);
            if (portfolioId != null && tradeDate != null) {
                PtlPortfolioMapper ptlPortfolioMapper = SpringContextUtil.getBean(PtlPortfolioMapper.class);
                PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey((String) portfolioId);
                String tradeDateStr = "";
                if(tradeDate instanceof java.util.Date){
                    tradeDateStr = DateUtil.format((Date)tradeDate);
                }
                else if(tradeDate instanceof java.lang.String){
                    tradeDateStr = (String) tradeDate;
                }
                if(StringUtil.isNotEmpty(portfolio) && StringUtil.isNotEmpty(tradeDateStr) &&DateUtil.compareDate(DateUtil.format(portfolio.getMdate()), tradeDateStr)<0){
                    validMessageList.add(new ValidMessage("portfolioId", "产品已到期，不可做交易！"));
                }
            }
        }
        //add by lpf 产品到期不能做交易 end
        // 当业务check没有问题，则执行风险相关交易共通check
        if (StringUtil.isEmptyList(validMessageList)) {
            try {
                TrdProductDeal trdProductDeal = null;
                //授信用
                TrdProductDeal creditTrdProductDeal = null;
                TrdProductDeal othTrdProductDeal = null;
                String repType = null;

                // 副总裁/总裁助理授权额度检查相关处理
                String counterId = null;
                BigDecimal prinAmt = null;
                String 平安资产类型 = null;
                String oth平安资产类型 = null;
                String windtypeone = null;
                String tradeMarket = null;
                String ps = null;
                String portfolioId = null;
                String chlId = null;
                // 资产类型改为授权类型 add by renping 2019-08-06  start
                String finprodId = null;
                // 资产类型改为授权类型 add by renping 2019-08-06  end
                // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2020-03-04  start
                String inputValue1 = null;String inputValue2 = null;String inputValue3 = null;
                // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2019-03-04  end
                
                boolean isNumCheck = true;
                // 债券投资交易
                if (value instanceof TrdBondInvestTradeDto) {
                    TrdBondInvestTradeDto inputDto = (TrdBondInvestTradeDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
//                    prinAmt = inputDto.getFpriceAmt();
                    prinAmt = inputDto.getTradeAmt();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    windtypeone = inputDto.getWindtypeone();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdBondInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdBondInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.setDealDataForValid(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                }
                // 拆借投资交易
                else if (value instanceof TrdLendInvestTradeDto) {
                    TrdLendInvestTradeDto inputDto = (TrdLendInvestTradeDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getFinTradeProduct().getPrin(); 
                    平安资产类型 = BussinessDictCodeList.平安资产类型_拆借.getValue();
                    windtypeone = inputDto.getPaBussType();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getBusiPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();

                    TrdLendInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdLendInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.prepareEntity(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                    inputValue1 = DictCodeList.平安资产类型_拆借.getValue();
                    inputValue2 = ps;
                }
                // 存款类资产投资交易
                else if (value instanceof TrdDepositInvestTrade001Dto) {
                    TrdDepositInvestTrade001Dto inputDto = (TrdDepositInvestTrade001Dto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
//                    prinAmt = inputDto.getPrinAmt();
                    //交易金额
                    prinAmt = inputDto.getTradeAmtAndFee();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmtAndFee).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_存款.getValue();
                    windtypeone = inputDto.getFinprodType();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdDepositInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdDepositInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = pageService.setTrdProductDeal(inputDto);
                    }
                }
                // 基金类投资交易
                else if (value instanceof TrdFundInvestTradeDto) {
                    TrdFundInvestTradeDto inputDto = (TrdFundInvestTradeDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getTradeAmt(); 
                    if (prinAmt == null || prinAmt.compareTo(BigDecimal.ZERO) <= 0) {
                        prinAmt = inputDto.getShareAmt();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                    windtypeone = inputDto.getType6();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdFundInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdFundInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.prepareEntity(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                    //赎回时不对份额进行授权校验
                    if (DictCodeList.交易方向_赎回.getValue().equals(ps)){
                        isNumCheck = false;
                    }
                }
                // 计划类资产（净值型）交易
                else if (value instanceof TrdPlanNetInvestTradeDto) {
                    TrdPlanNetInvestTradeDto inputDto = (TrdPlanNetInvestTradeDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    // 修改交易金额获取值by赵东东 2019.9.4 start
                    prinAmt = inputDto.getTradeAmt();
                    if (prinAmt == null || prinAmt.compareTo(BigDecimal.ZERO) <= 0) {
                        prinAmt = inputDto.getShareAmt();
                    }
                    //多组合时用最大的券面总额
                    if (null != inputDto.getTradeAmt() && 
                        null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    // 修改交易金额获取值by赵东东 2019.9.4 end
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_净值型.getValue();
                    windtypeone = inputDto.getType6();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdPlanNetInvestTrade002Controller pageService = SpringContextUtil.getBean(
                            TrdPlanNetInvestTrade002Controller.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.prepareEntity(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                    
                    //赎回时不对份额进行授权校验
                    if (DictCodeList.交易方向_赎回.getValue().equals(ps) && (inputDto.getTradeAmt() == null || inputDto.getTradeAmt().compareTo(BigDecimal.ZERO) <= 0)){
                        isNumCheck = false;
                    }
                }
                // 计划类资产（利率型）交易
                else if (value instanceof TrdPlanRateInvestTradeDto) {
                    TrdPlanRateInvestTradeDto inputDto = (TrdPlanRateInvestTradeDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
//                    prinAmt = inputDto.getPrinAmt();
                    prinAmt = inputDto.getTradeAmt();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_利率型.getValue();
                    windtypeone = inputDto.getFinprodType();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdPlanRateInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdPlanRateInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = pageService.getEntity(inputDto);
                    }
                }
                // 回购资产交易
                else if (value instanceof TrdRepoTrade002Dto) {
                    TrdRepoTrade002Dto inputDto = (TrdRepoTrade002Dto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getPrinAmt();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getMaxTradeamt()){
                        prinAmt = inputDto.getMaxTradeamt();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                    windtypeone = inputDto.getRepoType();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    TrdRepoTrade002Service pageService = SpringContextUtil.getBean(
                            TrdRepoTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.prepareEntity(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        repType = inputDto.getRepoType();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                inputDto.getTrdProductDeal());
                    }
                    inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                    inputValue2 = repType;
                    inputValue3 = ps;
                    if (trdProductDeal != null){
                        trdProductDeal.setInputValue1(inputValue1);
                        trdProductDeal.setInputValue2(inputValue2);
                        trdProductDeal.setInputValue3(inputValue3);
                    }
                }
                // 项目类资产交易
                else if (value instanceof TrdProjectInvestTrade002Dto) {
                    TrdProjectInvestTrade002Dto inputDto = (TrdProjectInvestTrade002Dto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
//                    prinAmt = inputDto.getPrinAmt();
                    prinAmt = inputDto.getTradeAmt();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_项目类资产.getValue();
                    windtypeone = inputDto.getProjectType();
                    tradeMarket = null;
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdProjectInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdProjectInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        BaseTradeInfoDto baseTradeInfoDto = pageService.genFinProductTradeDto(
                                inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                baseTradeInfoDto.getTrdProductDeal());
                    }
                }
                //  计划类资产（货币型）交易
                else if (value instanceof TrdPlanCcyInvestTradeDto) {
                    TrdPlanCcyInvestTradeDto inputDto = (TrdPlanCcyInvestTradeDto) value;
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getPrinAmt();
                    if (prinAmt == null || prinAmt.compareTo(BigDecimal.ZERO) <= 0) {
                        prinAmt = inputDto.getShareAmt();
                    }
                    //多组合时用最大的券面总额
                    if (null != inputDto.getTradeAmt() && null != inputDto.getBussTrdTransfenpeiDto() && inputDto.getBussTrdTransfenpeiDto().size() > 0){
                        prinAmt = inputDto.getBussTrdTransfenpeiDto().stream().map(BussTrdTransfenpeiDto::getTradeAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_货币型.getValue();
                    // 修改交易场所by赵东东  2019.8.23  start
                    tradeMarket = inputDto.getTradeMarket();
                    // 修改交易场所by赵东东  2019.8.23  end
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    TrdPlanCcyInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdPlanCcyInvestTrade002Service.class);
                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        pageService.prepareEntity(inputDto);

                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                    
                    //赎回时不对份额进行授权校验
                    if (DictCodeList.交易方向_赎回.getValue().equals(ps) && (inputDto.getPrinAmt() == null || inputDto.getPrinAmt().compareTo(BigDecimal.ZERO) <= 0)){
                        isNumCheck = false;
                    }
                }
                //  买断式回购
                else if (value instanceof TrdOutRightRepoTrade002Dto) {
                    TrdOutRightRepoTrade002Dto inputDto = (TrdOutRightRepoTrade002Dto) value;
                    TrdOutRightRepoTrade002Service pageService = SpringContextUtil
                            .getBean(TrdOutRightRepoTrade002Service.class);
                    if (inputDto.getTrdOutRightRepoPersonInfo() != null) {
                        counterId = inputDto.getTrdOutRightRepoPersonInfo().getCounterId();
                    }
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    // 金额修改为券面总额by赵东东2019.9.4 start
//                    prinAmt = inputDto.getSecTotalPrin();
                    prinAmt = inputDto.getfSettleAmt();
                    //多组合时用最大的券面总额
                    if (null != inputDto.getTrdOutRightRepoTradePtl() && inputDto.getTrdOutRightRepoTradePtl().size() > 0){
                        prinAmt = inputDto.getTrdOutRightRepoTradePtl().stream().map(TrdOutRightRepoTradePtl::getFirstSettleAmt).max((x1, x2) -> x1.compareTo(x2)).get();
                    }
                    // 金额修改为券面总额by赵东东2019.9.4 end
                    ps = inputDto.getPs();
                    平安资产类型 = DictCodeList.平安资产类型_买断式回购.getValue();
                    ps = inputDto.getPs();
                    // 修改交易场所by赵东东  2019.8.23  start
                    tradeMarket = inputDto.getTradeMarket();
                    // 修改交易场所by赵东东  2019.8.23  end
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                pageService.genTrdProductDeal(inputDto));
                        trdProductDeal.setPrinAmt(inputDto.getSecTotalPrin());
                    }
                    inputValue1 = DictCodeList.平安资产类型_买断式回购.getValue();
                    inputValue2 = ps;
                }
                // 资产类型改为授权类型 add by renping 2019-08-06  start
                // 债券借贷
                else if (value instanceof TrdBondLendInvestTradeDto) {
                    TrdBondLendInvestTradeDto inputDto = (TrdBondLendInvestTradeDto) value;
                    TrdBondLendInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdBondLendInvestTrade002Service.class);
                    String creFinprodId = inputDto.getFinprodId();
                    // 修改债券借贷授权不check by 赵东东 2019.8.21 start
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                    pageService.prepareEntity(inputDto);
//                    prinAmt = inputDto.getTrdProductDeal().getPrinAmt();
                    prinAmt = inputDto.getTrdProductDeal().getShareAmt();
                    counterId = inputDto.getTrdProductDeal().getCounterId();
                    // 修改交易场所by赵东东  2019.8.23  start
                    tradeMarket = inputDto.getTradeMarket();
                    // 修改交易场所by赵东东  2019.8.23  end
                    // 修改交易方向by赵东东 2019.8.26 start
                    ps = inputDto.getPs();
                    // 修改交易方向by赵东东 2019.8.26 start
//                    finprodId = inputDto.getFinprodId();
                    finprodId = "";
                    // 修改债券借贷授权不check by 赵东东 2019.8.21 end

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        creditTrdProductDeal = new TrdProductDeal();
                        pageService.prepareEntity(inputDto);
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                inputDto.getTrdProductDeal());
                        BeanUtil.copyPropertiesIgnoreNull(creditTrdProductDeal,
                            inputDto.getTrdProductDeal());
                        //根据标的检查授信
                        creditTrdProductDeal.setFinprodId(creFinprodId);
                    }
                    inputValue1 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                }
                // 股票
                else if (value instanceof TrdStockDto) {
                    TrdStockDto inputDto = (TrdStockDto) value;
                    TrdStock002Service pageService = SpringContextUtil.getBean(
                            TrdStock002Service.class);
                    // 修改股票授权不check by 赵东东 2019.8.21 start
                    /* counterId = inputDto.getCounterId(); */
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                    /* pageService.prepareEntity(inputDto); */
                    prinAmt = inputDto.getTradeAmt();
                    counterId = inputDto.getCounterId();
                    tradeMarket = inputDto.getTradeMarket();
                    ps = inputDto.getPs();
                    // 将ps不等于T02的改成T01 赵东东 2019.8.26 start
                    if (!StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                        // 去掉代码中的字符串by 赵东东 2019.8.26 start
                        ps = DictCodeList.交易方向_买入.getValue();
                        // 去掉代码中的字符串by 赵东东 2019.8.26 end
                    }
                    // 将ps不等于T02的改成T01 赵东东 2019.8.26 end
                    finprodId = inputDto.getFinprodId();
                    // 修改股票授权不check by 赵东东 2019.8.21 end

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        pageService.prepareEntity(inputDto);
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                }
                // 外汇即期
                else if (value instanceof TrdSpotexInvestTradeDto) {
                    TrdSpotexInvestTradeDto inputDto = (TrdSpotexInvestTradeDto) value;
                    TrdSpotexInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdSpotexInvestTrade002Service.class);
                    counterId = inputDto.getCounterId();
                    if (inputDto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
                        if (StringUtil.equals(inputDto.getDealCur(),
                                DictCodeList.币种_人民币_CNY.getValue())) {
                            prinAmt = inputDto.getPrinAmt();
                        } else {
                            prinAmt = inputDto.getTradeAmt();
                        }
                    } else {
                        CommonBusinessService commonBusinessService = SpringContextUtil
                                .getBean(CommonBusinessService.class);
                        if (commonBusinessService != null) {
                            prinAmt = commonBusinessService.calExchangeAmt(inputDto.getPrinAmt(),
                                    DateUtil.addDay(inputDto.getTradeDate(), -1), inputDto.getDealCur(),
                                    DictCodeList.币种_人民币_CNY.getValue());
                        }
                    }
                    tradeMarket = null;
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇即期.getValue();

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        pageService.prepareEntity(inputDto);
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    }
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
                }
                // 外汇远期
                else if (value instanceof TrdForwexInvestTradeDto) {
                    TrdForwexInvestTradeDto inputDto = (TrdForwexInvestTradeDto) value;
                    TrdForwexInvestTrade002Service pageService = SpringContextUtil.getBean(
                            TrdForwexInvestTrade002Service.class);
                    counterId = inputDto.getCounterId();
                    if (inputDto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
                        if (StringUtil.equals(inputDto.getCcy(),
                                DictCodeList.币种_人民币_CNY.getValue())) {
                            prinAmt = inputDto.getPrinAmt();
                        } else {
                            prinAmt = inputDto.getFpriceAmt();
                        }
                    } else {
                        CommonBusinessService commonBusinessService = SpringContextUtil
                                .getBean(CommonBusinessService.class);
                        if (commonBusinessService != null) {
                            prinAmt = commonBusinessService.calExchangeAmt(inputDto.getPrinAmt(),
                                    DateUtil.addDay(inputDto.getTradeDate(), -1), inputDto.getCcy(),
                                    DictCodeList.币种_人民币_CNY.getValue());
                        }
                    }
                    tradeMarket = null;
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇远期.getValue();

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        pageService.prepareEntity(inputDto);
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto.getTrdProductDeal());
                    }
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
                }
                // 外汇掉期
                else if (value instanceof TrdFxswapInvestTrade001Dto) {
                    TrdFxswapInvestTrade001Dto inputDto = (TrdFxswapInvestTrade001Dto) value;
                    TrdFxswapInvestTrade002Service pageService = SpringContextUtil
                            .getBean(TrdFxswapInvestTrade002Service.class);
                    counterId = inputDto.getCounterId();
                    if (inputDto.getCurPair().contains(DictCodeList.币种_人民币_CNY.getValue())) {
                        if (StringUtil.equals(inputDto.getDealCur(),
                                DictCodeList.币种_人民币_CNY.getValue())) {
                            prinAmt = inputDto.getnDealCurAmt();
                        } else {
                            prinAmt = inputDto.getnCorCurAmt();
                        }
                    } else {
                        CommonBusinessService commonBusinessService = SpringContextUtil
                                .getBean(CommonBusinessService.class);
                        if (commonBusinessService != null) {
                            prinAmt = commonBusinessService.calExchangeAmt(inputDto.getnDealCurAmt(),
                                    DateUtil.addDay(inputDto.getTradeDate(), -1), inputDto.getDealCur(),
                                    DictCodeList.币种_人民币_CNY.getValue());
                        }
                    }
                    tradeMarket = null;
                    ps = inputDto.getPs();
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                pageService.setTrdProductDeal(inputDto));
                    }
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
                }
                // 利率互换
                else if (value instanceof TrdIRSTradeDto) {
                    TrdIRSTradeDto inputDto = (TrdIRSTradeDto) value;
                    TrdIRSTrade002Service pageService = SpringContextUtil
                            .getBean(TrdIRSTrade002Service.class);
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                    pageService.prepareEntity(inputDto);
                    prinAmt = inputDto.getTrdProductDeal().getPrinAmt(); 
                    counterId = inputDto.getTrdProductDeal().getCounterId();
                    tradeMarket = null;
                    ps = inputDto.getIrsType();

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                inputDto.getTrdProductDeal());
                    }
                    inputValue1 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                }
                // 可转债
                else if (value instanceof TrdConBondDto) {
                    TrdConBondDto inputDto = (TrdConBondDto) value;
                    TrdConBond002Service pageService = SpringContextUtil
                            .getBean(TrdConBond002Service.class);
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    TrdFinProductDealTotal trdFinProductDealTotal = pageService.getTrdFinProductDealTotal(inputDto);
                    prinAmt = trdFinProductDealTotal.getPrinAmt();
                    counterId = trdFinProductDealTotal.getCounterId();
                    tradeMarket = null;
//                    ps = inputDto.getTrdProductDeal().getPs();
                    ps = DictCodeList.交易方向_买入.getValue();

                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                trdFinProductDealTotal);
                        trdProductDeal.setPs(ps);
                    }
                }
                // 债转股
                else if (value instanceof TrdBondToStock001Dto) {
                    TrdBondToStock001Dto inputDto = (TrdBondToStock001Dto) value;
                    TrdBondToStock002Service pageService = SpringContextUtil
                            .getBean(TrdBondToStock002Service.class);
                    portfolioId = inputDto.getPortfolioId();
                    chlId = inputDto.getChlId();
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    oth平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                    TrdFinProductDealTotal trdFinProductDealTotal = pageService.gen汇总信息(inputDto);
                    prinAmt = ArithmeticUtil.multiply(trdFinProductDealTotal.getShareAmt(), new BigDecimal(100));
                    counterId = trdFinProductDealTotal.getCounterId();
                    tradeMarket = null;
                    ps = DictCodeList.交易方向_卖出.getValue();
                    // 资产类型改为授权类型 add by renping 2019-08-06  start
                    finprodId = inputDto.getFinprodId();
                    // 资产类型改为授权类型 add by renping 2019-08-06  end

                    if (pageService != null
                            && (StringUtil.equals("submit", inputDto.getOperateType())
                            || StringUtil.equals("save", inputDto.getOperateType()))) {
                        trdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,
                                trdFinProductDealTotal);
                        trdProductDeal.setPs(DictCodeList.交易方向_卖出.getValue());
                        othTrdProductDeal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(othTrdProductDeal,
                                trdProductDeal);
                        othTrdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                        othTrdProductDeal.setFinprodId(inputDto.getShareFinprodId());
                        othTrdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                        othTrdProductDeal.setShareAmt(inputDto.getConNum());
                        othTrdProductDeal.setPrinAmt(ArithmeticUtil.multiply(inputDto.getFirstUnitCprice3(), inputDto.getConNum()));
                        othTrdProductDeal.setTradeAmt(ArithmeticUtil.multiply(inputDto.getFirstUnitCprice3(), inputDto.getConNum()));
                    }
                }
                // 资产类型改为授权类型 add by renping 2019-08-06  end
//                if (StringUtil.isNotEmpty(chlId) && StringUtil.isNotEmpty(portfolioId)) {
//                    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper = SpringContextUtil.getBean(
//                        BaseAssetCashPositionCommMapper.class);
//                    String accountType = baseAssetCashPositionCommMapper.selectAccountTypeByPortAndChl(portfolioId,
//                        chlId);
//                    if (StringUtil.isEmpty(accountType)) {
//                        validMessageList.add(new ValidMessage("portfolioId", "该组合没有关联所选择通道的资金账户。"));
//                    }
//                }
                //add by 张年  2019/08.02 start
              //资产与名单资产是否匹配(包括有标的、无标的)
                if (trdProductDeal != null) {
                    if (!isNameSoftControl) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("listDate", trdProductDeal.getTradeDate());
                        param.put("connterId",counterId);
                        if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                            param.put("finprodId", trdProductDeal.getFinprodId());
                        }else{
                            param.put("inputValue1", inputValue1);
                            param.put("inputValue2", inputValue2);
                            param.put("inputValue3", inputValue3);
                        }
                        param = riskNamListCommonValidatorService.名单是否匹配(param);
                        if (Boolean.parseBoolean(param.get("isAssetMatch").toString())) {
                            if (param.containsKey("listTypeAsset")) {
                                ValidMessage validMessage = new ValidMessage("资产在黑名单内，禁止投资!");
                                validMessageList.add(validMessage);
                                return validMessageList;
                            }
                        } else if(!Boolean.parseBoolean(param.get("isAssetMatch").toString()) && Boolean.parseBoolean(param.get("isAssetWhiteList").toString())) { 
                            ValidMessage validMessage = new ValidMessage("该资产不在白名单内，禁止投资!");
                            validMessageList.add(validMessage);
                            return validMessageList;
                        }
                        if (!Boolean.parseBoolean(param.get("isOrgBlackMatch").toString())) {    
                            if (param.containsKey("listTypeOrg")) {
                                ValidMessage validMessage = new ValidMessage("资产所属机构（或交易对手）在黑名单内，禁止投资!");
                                validMessageList.add(validMessage);
                                return validMessageList;
                            }
                        } else if (!Boolean.parseBoolean(param.get("isOrgWhiteMatch").toString())) {
                            ValidMessage validMessage = new ValidMessage("该资产所属机构（或交易对手）不在白名单内，禁止投资!");
                            validMessageList.add(validMessage);
                            return validMessageList;
                        }
                    }
                }
//                其他交易信息（例:债转股）
                if(othTrdProductDeal!=null){
                    if(StringUtil.isNotEmpty(othTrdProductDeal.getFinprodId())){
                        if (!isNameSoftControl) {
                            if (trdProductDeal != null && StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                                Map<String, Object> param = new HashMap<>();
                                param.put("finprodId", trdProductDeal.getFinprodId());
                                param.put("listDate", trdProductDeal.getTradeDate());
                                param = riskNamListCommonValidatorService.名单是否匹配(param);
                                if (Boolean.parseBoolean(param.get("isAssetMatch").toString())) {
                                    if (param.containsKey("listTypeAsset")) {
                                        ValidMessage validMessage = new ValidMessage("资产在黑名单内，禁止投资!");
                                        validMessageList.add(validMessage);
                                        return validMessageList;
                                    }
                                } else if(!Boolean.parseBoolean(param.get("isAssetMatch").toString()) && Boolean.parseBoolean(param.get("isAssetWhiteList").toString())) { 
                                    ValidMessage validMessage = new ValidMessage("该资产不在白名单内，禁止投资!");
                                    validMessageList.add(validMessage);
                                    return validMessageList;
                                }
                                if (!Boolean.parseBoolean(param.get("isOrgBlackMatch").toString())) {    
                                    if (param.containsKey("listTypeOrg")) {
                                        ValidMessage validMessage = new ValidMessage("资产所属机构（或交易对手）在黑名单内，禁止投资!");
                                        validMessageList.add(validMessage);
                                        return validMessageList;
                                    }
                                } else if (!Boolean.parseBoolean(param.get("isOrgWhiteMatch").toString())) {
                                    ValidMessage validMessage = new ValidMessage("该资产所属机构（或交易对手）不在白名单内，禁止投资!");
                                    validMessageList.add(validMessage);
                                    return validMessageList;
                                }
                            }
                        }
                        /*if(!riskNamListCommonValidatorService.名单是否匹配(param)){
                            ValidMessage validMessage = new ValidMessage("名单内未匹配到资产或机构,禁止投资!");
                            validMessageList.add(validMessage);
                            return validMessageList;
                        }*/
                    }
                }
                //add by 张年  2019/08.02 end
                // 调用风险相关交易共通check
                if (trdProductDeal != null && !isCreditSoftControl) {
                    if (value instanceof TrdBondLendInvestTradeDto && null != creditTrdProductDeal) {
                        trdProductDeal = creditTrdProductDeal;
                    }
                	CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
                	AllTradeCommonInputDto inputDto =new AllTradeCommonInputDto();
                	inputDto.setTrdProductDeal(trdProductDeal);
                	inputDto.setRepoType(repType);
                	List<CreditLinesDealCommon> list = ceLinesUpdate.isNeedOccupyCre(inputDto, true,false,false);
                	if(list !=null && list.size()>0){
                		ValidMessage validMessage = new ValidMessage(list.get(0).getErrMessage());
                		validMessageList.add(validMessage);
                	}
                }
//                其他交易信息（例:债转股）
                if (othTrdProductDeal != null && !isCreditSoftControl) {
                    CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
                    AllTradeCommonInputDto inputDto =new AllTradeCommonInputDto();
                    inputDto.setTrdProductDeal(othTrdProductDeal);
                    inputDto.setRepoType(repType);
                    List<CreditLinesDealCommon> list = ceLinesUpdate.isNeedOccupyCre(inputDto, true,false,false);
                    if(list !=null && list.size()>0){
                        ValidMessage validMessage = new ValidMessage(list.get(0).getErrMessage());
                        validMessageList.add(validMessage);
                    }
                }
                //----add by jjpei 20190710----授权开关控制----start----
                /*if (StringUtil.isEmptyList(validMessageList) && prinAmt != null) {
                    CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                    if (commonBusinessService != null && commonBusinessService.是否超过交易员额度(counterId, prinAmt, 平安资产类型, windtypeone, tradeMarket, ps)) {
                        // 交易本金金额超过当前用户授权额度，无法发起交易!
                        ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_0016", "当前用户"));
                        validMessageList.add(validMessage);
                    }
                }*/
                //add by liupengfei for 多级法人改造 S
                /*如果没有授权，提示不给提交交易，但是有授权超过授权额度允许提交*/
                if (!isAuthorizationSoftcontrol && StringUtil.isEmptyList(validMessageList) && prinAmt != null) {
                    CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                    if (commonBusinessService != null && commonBusinessService.是否交易员授权(counterId, prinAmt, 平安资产类型, windtypeone, tradeMarket, ps,finprodId)) {
                        // 交易本金金额超过当前用户授权额度，无法发起交易!
                        ValidMessage validMessage = new ValidMessage(getMessage("当前用户未授权，无法发起交易！"));
                        validMessageList.add(validMessage);
                    }
                }
                //add by liupengfei for 多级法人改造 E
                // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息）
                if (!isAuthorizationSoftcontrol && StringUtil.isEmptyList(validMessageList) && prinAmt != null && isNumCheck) {
                    CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                    Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),平安资产类型, windtypeone, tradeMarket, ps
                            // 资产类型改为授权类型 add by renping 2019-08-06  start
                            , finprodId , null);
                    if (commonBusinessService != null && commonBusinessService.是否超过副总额额度(params)) {
                        // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
                        ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_0016", "领导"));
                        validMessageList.add(validMessage);
                    }
                }
                //                其他交易信息（例:债转股）
                if (!isAuthorizationSoftcontrol && StringUtil.isEmptyList(validMessageList) && othTrdProductDeal!=null) {
                    CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                    Map<String, Object> params = CommonBusinessService.setParams(counterId, othTrdProductDeal.getPrinAmt(), LoginUserUtil.getUserName(),
                            oth平安资产类型, windtypeone, tradeMarket, othTrdProductDeal.getPs()
                            // 资产类型改为授权类型 add by renping 2019-08-06  start
                            , othTrdProductDeal.getFinprodId(), null);
                    if (commonBusinessService != null && commonBusinessService.是否超过副总额额度(params)) {
                        // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
                        ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_0016", "领导"));
                        validMessageList.add(validMessage);
                    }
                }
                //----add by jjpei 20190710----授权开头控制----end----

            }
            catch (Exception e) {
                logger.info("执行风险相关交易共通check出现异常，异常信息如下：");
                logger.error(e.getMessage(),e);
            }

            Field counterTypeField = ReflectionUtils.findField(value.getClass(), "counterType");
            if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))) {
                // check 属性交易对手时内部交易对手，只能选择卖出
                Field psField = ReflectionUtils.findField(value.getClass(), "ps");
                if (counterTypeField != null && psField != null) {
                    ReflectionUtils.makeAccessible(counterTypeField);
                    ReflectionUtils.makeAccessible(psField);
                    Object counterType = ReflectionUtils.getField(counterTypeField, value);
                    Object ps = ReflectionUtils.getField(psField, value);
                    if (counterType != null && ps != null) {
                        if ("N".equals(counterType) && !DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                            validMessageList.add(new ValidMessage("ps", "选择内部交易对手时，交易方向只能选择卖出"));
                        }
                    }
                }
            }

            // check通道时，不能选择内部交易对手
            Field chlIdField = ReflectionUtils.findField(value.getClass(), "chlId");
//            if (counterTypeField != null && chlIdField != null) {
//                ReflectionUtils.makeAccessible(counterTypeField);
//                ReflectionUtils.makeAccessible(chlIdField);
//                Object counterType = ReflectionUtils.getField(counterTypeField, value);
//                Object chlId = ReflectionUtils.getField(chlIdField, value);
//                if (counterType != null && StringUtil.isNotEmpty(chlId) && "N".equals(counterType)) {
//                    validMessageList.add(new ValidMessage("chlId", "选择内部交易对手时，不能选择通道"));
//                }
//            }

            // check通道时，通道与组合不能挂同一个账户
            Field portfolioIdField = ReflectionUtils.findField(value.getClass(), "portfolioId");
            if (portfolioIdField != null && chlIdField != null) {
                ReflectionUtils.makeAccessible(portfolioIdField);
                ReflectionUtils.makeAccessible(chlIdField);
                Object portfolioId = ReflectionUtils.getField(portfolioIdField, value);
                Object chlId = ReflectionUtils.getField(chlIdField, value);
                if (StringUtil.isNotEmpty(portfolioId) && StringUtil.isNotEmpty(chlId)) {
                    CommonBusinessMapper mapper = SpringContextUtil.getBean(CommonBusinessMapper.class);
                    Boolean isSame = mapper.checkPortChlAcctNoSame((String) portfolioId, (String) chlId);
                    if (isSame != null && isSame) {
                        validMessageList.add(new ValidMessage("chlId", "所选择的通道与组合有相同的账户"));
                    }
                }
            }

            Field field = ReflectionUtils.findField(value.getClass(), "bussTrdTransferOrder");
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                Object orders = ReflectionUtils.getField(field, value);
                if (orders instanceof List) {
                    List<Object> orderList = (ArrayList) orders;
                    for (Object order : orderList) {
                        if (order instanceof BussTrdTransferOrderDto) {
                            BussTrdTransferOrderDto dto = (BussTrdTransferOrderDto) order;
                            if ((DictCodeList.是否_是.getValue().equals(dto.getIsNeedSend()) || DictCodeList.是否_是.getCodeName().equals(dto.getIsNeedSend())) && (StringUtil.isEmpty(dto.getRecAcctNo()) || StringUtil.isEmpty(dto.getPayAcctNo()))) {
                                validMessageList.add(new ValidMessage("是否发送指令是：是，收付款账户都不能为空。"));
                            }
                        }
                    }
                }
            }
        }

        return validMessageList;
    }

    /**
     * 验证方法
     *
     * @param value 需要被验证的业务对象
     * @return 验证消息，如果为空则表示验证通过
     * @since 1.0
     */
    List<ValidMessage> bussValid(Object value);
}
