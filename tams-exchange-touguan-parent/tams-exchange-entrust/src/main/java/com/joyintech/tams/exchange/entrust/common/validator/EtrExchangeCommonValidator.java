/*
 * 文件名：EtrCommonValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年7月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.common.validator;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.PtlCapacctInfo;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.dao.CommonBusinessMapper;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskNamListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesDealCommon;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrbondtostock.dto.EtrBondToStockDto;
import com.joyintech.tams.invest.entrust.etrbondtostock.service.EtrBondToStockService;
import com.joyintech.tams.invest.entrust.etrboutrepo.dto.EtrBoutRepoDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrconbond.service.EtrConBondService;
import com.joyintech.tams.invest.entrust.etrforwex.dto.EtrForwexDto;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrirs.dto.EtrIRSDto;
import com.joyintech.tams.invest.entrust.etrlendspecinfo.dto.EtrLendSpecInfoDto;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrrepo.dto.EtrRepoDto;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.invest.entrust.etrstock.dto.EtrStockDto;
import com.joyintech.tams.invest.entrust.etrwtfxswap.dto.EtrWtFxswapDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrWtSpotexDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 委托共通Validator
 *
 * @author 裴家俊
 * @version 1.0 2019年1月30日
 * @see EtrExchangeCommonValidator
 * @since 1.0
 */
public interface EtrExchangeCommonValidator extends BeanValidator {

    /**
     * 日志
     */
    public static Logger logger = LoggerFactory.getLogger(EtrCommonValidator.class);

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
        //名单管理  isNameSoftControl true:软控  false:硬控
        boolean isNameSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_NAME_SOFTCONTROL"));
        boolean isCheckOrg = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CHECK_ORG"));
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        isAuthorizationSoftcontrol = StringUtil.isNotEmpty(isAuthorizationSoftcontrol) && isAuthorizationSoftcontrol;
        RiskNamListCommonValidatorService riskNamListCommonValidatorService = SpringContextUtil.getBean(
                RiskNamListCommonValidatorService.class);
        // add by qianngchengxi 2019-08-06 start
        if (StringUtil.isEmptyList(validMessageList)) {
            Field counterTypeField = ReflectionUtils.findField(value.getClass(), "counterType");
            if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL))) {
                // check 属性交易对手时内部交易对手，只能选择卖出
                Field psField = ReflectionUtils.findField(value.getClass(), "ps");
                if (counterTypeField != null && psField != null) {
                    ReflectionUtils.makeAccessible(counterTypeField);
                    ReflectionUtils.makeAccessible(psField);
                    Object counterType = ReflectionUtils.getField(counterTypeField, value);
                    Object ps = ReflectionUtils.getField(psField, value);
                    if (StringUtil.isNotEmpty(counterType) && StringUtil.isNotEmpty(ps)) {
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
//                if (StringUtil.isNotEmpty(counterType) && StringUtil.isNotEmpty(chlId) && "N".equals(counterType)) {
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


            Field tradeMarketField = ReflectionUtils.findField(value.getClass(), "tradeMarket");
            Field ccyField = ReflectionUtils.findField(value.getClass(), "ccy");
            if (portfolioIdField != null && tradeMarketField != null && ccyField != null) {
                ReflectionUtils.makeAccessible(portfolioIdField);
                Object portfolioId = ReflectionUtils.getField(portfolioIdField, value);

                ReflectionUtils.makeAccessible(tradeMarketField);
                Object tradeMarket = ReflectionUtils.getField(tradeMarketField, value);

                ReflectionUtils.makeAccessible(ccyField);
                Object ccy = ReflectionUtils.getField(ccyField, value);
                //获取组合的资金账户
                if (StringUtil.isNotEmpty(portfolioId) && StringUtil.isNotEmpty(ccy)) {
                    String acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
                    if (StringUtil.equals(tradeMarket, DictCodeListExt1.交易场所_上海证券交易所.getValue()) ||
                            StringUtil.equals(tradeMarket, DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                        acctType = DictCodeListExt1.资金账户类型_券商资金账户.getValue();
                    } else {
                        acctType = DictCodeListExt1.资金账户类型_产品托管户.getValue();
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", portfolioId);
                    map.put("ccy", ccy);
                    map.put("acctType", acctType);
                    CommonBusinessMapper mapper = SpringContextUtil.getBean(CommonBusinessMapper.class);
                    List<PtlCapacctInfo> ptlCapacctInfos = mapper.selectPtlCapacctInfo(map);
                    if (StringUtil.isEmptyList(ptlCapacctInfos)) {
                        validMessageList.add(new ValidMessage("portfolioId", "组合尚未绑定资金账户，无法下达指令"));
                    }
                }
            }
            // check 属性bussTrdTransferOrder
            Field field = ReflectionUtils.findField(value.getClass(), "bussTrdTransferOrder");
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                Object orders = ReflectionUtils.getField(field, value);
                if (orders == null) {
                    checkOrder(value, validMessageList);
                } else if (orders instanceof List) {
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
            } else {
                checkOrder(value, validMessageList);
            }
            // 委托开始日不得小于平台日
            Field vdate = ReflectionUtils.findField(value.getClass(), "vdate");
            if (vdate != null) {
                ReflectionUtils.makeAccessible(vdate);
                Object vdateObject = ReflectionUtils.getField(vdate, value);
                if (StringUtil.isNotEmpty(vdateObject)) {
                    Date sysDate = DateUtil.platDateFromCache();
                    Date vd;
                    if (vdateObject instanceof String) {
                        vd = DateUtil.parse((String) vdateObject);
                    } else {
                        vd = (Date) vdateObject;
                    }

                    if (DateUtil.compareDate(DateUtil.format(vd), DateUtil.format(sysDate)) < 0) {
                        validMessageList.add(new ValidMessage("vdate", "委托开始日不得小于平台日"));
                    }

                }
            }
        }
        // add by qianngchengxi 2019-08-06 start

        //资产与名单资产是否匹配
        String etrlimit_switch = CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH);
        // 当业务check没有问题，则执行风险相关交易共通check
        if (StringUtil.isEmptyList(validMessageList)) {
            try {
                TrdProductDeal trdProductDeal = null;
                String repType = null;

                // 副总裁/总裁助理授权额度检查相关处理
                String counterId = null;
                BigDecimal prinAmt = null;
                String 平安资产类型 = null;
                String windtypeone = null;
                String tradeMarket = null;
                String ps = null;
                // 资产类型改为授权类型 add by renping 2019-08-06  start
                String finprodId = null;
                // 资产类型改为授权类型 add by renping 2019-08-06  end
                // 资产类型改为授权类型 add by zhangnian 2020-03-04  start
                String 无标的投管资产类型映射行内资产分类 = null;
                // 资产类型改为授权类型 add by zhangnian 2019-03-04  end
                // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2020-03-04  start
                String inputValue1 = null;
                String inputValue2 = null;
                String inputValue3 = null;
                // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2019-03-04  end
                // 债券投资交易
                if (value instanceof EtrInvestBondInfoDto) {
                    EtrInvestBondInfoDto inputDto = (EtrInvestBondInfoDto) value;
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    // 修改债券获取金额by赵东东 2019.9.4 start
                    prinAmt = inputDto.getEntrustNum();
                    // 修改债券获取金额by赵东东 2019.9.4 end
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                // 拆借投资交易
                else if (value instanceof EtrLendSpecInfoDto) {
                    EtrLendSpecInfoDto inputDto = (EtrLendSpecInfoDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_拆借.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    inputValue1 = DictCodeList.平安资产类型_拆借.getValue();
                    inputValue2 = ps;
                }
                // 存款类资产投资交易
                else if (value instanceof EtrBrowDepoInfoDto) {
                    EtrBrowDepoInfoDto inputDto = (EtrBrowDepoInfoDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_存款.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                // 基金类投资交易
                else if (value instanceof EtrFundDto) {
                    EtrFundDto inputDto = (EtrFundDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                // 计划类资产（净值型）交易
                else if (value instanceof EtrPlanAssetNDto) {
                    // 修改强转类型by赵东东2019.9.4 start
                    /* EtrFundDto inputDto = (EtrFundDto)value; */
                    EtrPlanAssetNDto inputDto = (EtrPlanAssetNDto) value;
                    // 修改强转类型by赵东东2019.9.4 start
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_净值型.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                // 计划类资产（利率型）交易
                else if (value instanceof EtrPlanAssetRDto) {
                    EtrPlanAssetRDto inputDto = (EtrPlanAssetRDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                        prinAmt = inputDto.getEntrustNum();
                    }
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_利率型.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                // 回购资产交易
                else if (value instanceof EtrRepoDto) {
                    EtrRepoDto inputDto = (EtrRepoDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    repType = inputDto.getAssetType1();
                    inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                    inputValue2 = repType;
                    inputValue3 = ps;
                }
                // 项目类资产交易
                else if (value instanceof EtrProjectDto) {
                    EtrProjectDto inputDto = (EtrProjectDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_项目类资产.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                //  计划类资产（货币型）交易
                else if (value instanceof EtrPlanAssetCDto) {
                    EtrPlanAssetCDto inputDto = (EtrPlanAssetCDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_计划类资产_货币型.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                //  买断式回购
                else if (value instanceof EtrBoutRepoDto) {
                    EtrBoutRepoDto inputDto = (EtrBoutRepoDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeList.平安资产类型_买断式回购.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    inputValue1 = DictCodeList.平安资产类型_买断式回购.getValue();
                    inputValue2 = ps;
                }
                // 债券借贷
                else if (value instanceof EtrSecLendDto) {
                    EtrSecLendDto inputDto = (EtrSecLendDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    // 修改获取金额by赵东东 2019.8.23 start
                    prinAmt = inputDto.getTotalAmt();
                    // 修改获取金额by赵东东 2019.8.23 end
                    平安资产类型 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    // 修改获取借贷方向by赵东东 2019.8.23 start
                    ps = inputDto.getBusiPs();
                    // 修改获取借贷方向by赵东东 2019.8.23 end
                    finprodId = "";//inputDto.getFinprodId();
                    inputValue1 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                }
                // 股票
                else if (value instanceof EtrStockDto) {
                    EtrStockDto inputDto = (EtrStockDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    // 将ps不等于T02的改成T01 赵东东 2019.8.26 start
                    if (!StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                        // 将代码中的字符串改掉by赵东东 2019.8.26 start
                        ps = DictCodeList.交易方向_买入.getValue();
                        // 将代码中的字符串改掉by赵东东 2019.8.26 end
                    }
                    // 将ps不等于T02的改成T01 赵东东 2019.8.26 end
                    finprodId = inputDto.getFinprodId();
                }
                // 外汇即期
                else if (value instanceof EtrWtSpotexDto) {
                    EtrWtSpotexDto inputDto = (EtrWtSpotexDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
                }
                // 外汇远期
                else if (value instanceof EtrForwexDto) {
                    EtrForwexDto inputDto = (EtrForwexDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
                }
                // 外汇掉期
                else if (value instanceof EtrWtFxswapDto) {
                    EtrWtFxswapDto inputDto = (EtrWtFxswapDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    // 修改获取交易金额by赵东东 2019.8.23 start
                    prinAmt = inputDto.getTradeAmt();
                    // 修改获取交易金额by赵东东 2019.8.23 end
                    平安资产类型 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    // 修改获取交易方向by赵东东 2019.8.23 start
                    ps = inputDto.getTradePs();
                    // 修改获取交易方向by赵东东 2019.8.23 start
                    inputValue1 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
                }
                // 利率互换
                else if (value instanceof EtrIRSDto) {
                    EtrIRSDto inputDto = (EtrIRSDto) value;

                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    // 修改金额的大小by赵东东 2019.8.23 start
                    prinAmt = inputDto.getEntrustAmt().multiply(new BigDecimal(10000));
                    // 修改金额的大小by赵东东 2019.8.23 end
                    平安资产类型 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    // 修改交易方向by赵东东 2019.8.23 start
                    ps = inputDto.getBusiPs();
                    // 修改交易方向by赵东东 2019.8.23 start
                    inputValue1 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                }
                // 可转债 add by gejiaao
                else if (value instanceof EtrConBondDto) {
                    EtrConBondDto inputDto = (EtrConBondDto) value;
                    EtrConBondService etrConBondService = SpringContextUtil.getBean(EtrConBondService.class);
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                    prinAmt = inputDto.getPurchBondTotal();
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = etrConBondService.getTrdProductDeal(inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }

                // 可转债转股 add by gaopeng  20191123
                else if (value instanceof EtrBondToStockDto) {
                    EtrBondToStockDto inputDto = (EtrBondToStockDto) value;
                    EtrBondToStockService etrBondToStockService = SpringContextUtil.getBean(EtrBondToStockService.class);
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                    prinAmt = inputDto.getStocketrPriceTotal();
                    平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = etrBondToStockService.getTrdProductDeal(inputDto);
                    ps = inputDto.getPs();
                    trdProductDeal.setFinprodId(inputDto.getShareFinprodId());
                    finprodId = inputDto.getShareFinprodId();
                } else if (value instanceof EtrExchangeTradeSaveSecRepoRequest) {
                    EtrExchangeTradeSaveSecRepoRequest inputDto = (EtrExchangeTradeSaveSecRepoRequest) value;
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    平安资产类型 = inputDto.getFinprodType2();
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }

                //场内交易指令 add by yzg 20201214 start
                else if (value instanceof EtrExchangeTradeSaveRequest) {
                    EtrExchangeTradeSaveRequest inputDto = (EtrExchangeTradeSaveRequest) value;
                    // 副总裁/总裁助理授权额度检查相关处理
                    counterId = inputDto.getCounterId();
                    prinAmt = inputDto.getEntrustAmt();
                    if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                        平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                    } else if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                        平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                    } else if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                        平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                    } else {
                        平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                    }
                    windtypeone = inputDto.getFinprodType2();
                    tradeMarket = inputDto.getTradeMarket();
                    trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                    ps = inputDto.getPs();
                    finprodId = inputDto.getFinprodId();
                }
                //场内交易指令 add by yzg 20201214 end
                //交易所质押式协议回购 add by chendijun 20210311
                else if (value instanceof EtrExchangeAgreRepTrade001Dto) {
                    EtrExchangeAgreRepTrade001Dto inputDto = (EtrExchangeAgreRepTrade001Dto) value;
                    counterId = inputDto.getCounterId();
                    //prinAmt = inputDto.getPrintAmt();
                    //回购金额和方向判断 add by yzg 20210325 start
                    EtrExchangeAgreRepTrade001Service etrAgreRepTrade001Service = SpringContextUtil.getBean(EtrExchangeAgreRepTrade001Service.class);
                    //逆回购和正回购按正常交易金额和交易方向授权
                    if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
                            || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
                        ps = inputDto.getPs();
                        prinAmt = inputDto.getTradeAmt();
                    }
                    //提前购回、到期购回、中途换券、解除质押根据原始回购的交易方向和回购金额进行授权判断
                    else if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_提前购回.getValue())
                            || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_到期购回.getValue())
                            || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
                            || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
                        //获得初始活够金额和交易方向
                        Map<String, Object> map = new HashMap<>();
                        // 上交所、深交所取值来源不一致
                        map.put("tradeId", (inputDto.getIntiTradeId() == null || inputDto.getIntiTradeId().equals("")) ? inputDto.getTradeId() : inputDto.getIntiTradeId());
                        EtrExchangeAgreRepTrade001Dto result = etrAgreRepTrade001Service.getEntrustInfoByTradeId(map);
                        if (StringUtil.isNotEmpty(result)) {
                            ps = result.getPs();
                            prinAmt = result.getTradeAmt();
                        }
                    }
                    //到期续作使用原始的回购金额和续做的回购金额中的较大值进行判断
                    else if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())) {
                        //获得初始回购金额和交易方向
                        Map<String, Object> map = new HashMap<>();
                        map.put("tradeId", (inputDto.getIntiTradeId() == null || inputDto.getIntiTradeId().equals("")) ? inputDto.getTradeId() : inputDto.getIntiTradeId());
                        EtrExchangeAgreRepTrade001Dto result = etrAgreRepTrade001Service.getEntrustInfoByTradeId(map);
                        if (StringUtil.isNotEmpty(result)) {
                            ps = result.getPs();
                            prinAmt = result.getTradeAmt();
                            if (inputDto.getTradeAmt().compareTo(prinAmt) > 0) {
                                prinAmt = inputDto.getTradeAmt();
                            }
                        } else {
                            ps = DictCodeListExt3.交易方向_逆回购.getValue();
                            prinAmt = inputDto.getTradeAmt();
                        }
                    }
                    //回购金额和方向判断 add by yzg 20210325 end
                    平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                    inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                    repType = DictCodeList.回购类型_质押式协议回购.getValue();
                    inputValue2 = repType;
                    if (StringUtil.equals(inputDto.getRepPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                        windtypeone = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
                        inputValue3 = DictCodeListExt3.行内资产三级分类_正回购.getValue();
                    } else {
                        windtypeone = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
                        inputValue3 = DictCodeListExt3.行内资产三级分类_逆回购.getValue();
                    }
                    tradeMarket = inputDto.getTradeMarket();
                    //ps = inputDto.getPs();
                    //finprodId = inputDto.getFinprodId();
                    trdProductDeal = new TrdProductDeal();
                    trdProductDeal.setTradeDate(DateUtil.parse(inputDto.getVdate()));
                    trdProductDeal.setFinprodId(inputDto.getFinprodId());
                }


                // delete by renping 2019-08-06 start
//                if (StringUtil.isEmptyList(validMessageList) && prinAmt != null) {
//                    CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
//                    if (commonBusinessService != null && commonBusinessService.是否超过交易员额度(counterId, prinAmt, 平安资产类型, windtypeone, tradeMarket, ps)) {
//                        // 交易本金金额超过当前用户授权额度，无法发起交易!
//                        ValidMessage validMessage = new ValidMessage(getMessage("TRD_E_0016", "当前用户"));
//                        validMessageList.add(validMessage);
//                    }
//                }
                // delete by renping 2019-08-06 end
                //add by gaopeng 20191126 增加授信check start
                if ((StringUtil.equals(平安资产类型, DictCodeList.平安资产类型_债券.getValue()) || StringUtil.equals(平安资产类型, DictCodeListExt2.平安资产类型_股票.getValue()))
                        && (StringUtil.equals(ps, DictCodeListExt3.交易方向_债转股.getValue())
                        || StringUtil.equals(ps, DictCodeList.交易方向_申购.getValue()))) {
                    if (trdProductDeal != null) {
                        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
                        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                        inputDto.setTrdProductDeal(trdProductDeal);
                        inputDto.setRepoType(repType);
                        List<CreditLinesDealCommon> list = ceLinesUpdate.isNeedOccupyCre(inputDto, true, false, false);
                        if (list != null && list.size() > 0) {
                            ValidMessage validMessage = new ValidMessage(list.get(0).getErrMessage());
                            validMessageList.add(validMessage);
                        }
                    }
                }
                //add by gaopeng 20191126 end
                // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息） 且不等于赎回
                boolean etrlimitSwitch = StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH), "01");
                if (etrlimitSwitch && StringUtil.isEmptyList(validMessageList) && prinAmt != null
                        && (!StringUtil.equals(ps == null ? "" : ps, DictCodeList.交易方向_赎回.getValue()))) {
                    if (!isAuthorizationSoftcontrol) {
                        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
                        Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),
                                平安资产类型, windtypeone, tradeMarket, ps, finprodId, null);
                        if (commonBusinessService != null && commonBusinessService.是否超过副总额额度_委托用(params)) {
                            // 交易本金金额超过副总裁/总裁助理授权额度，无法发起交易!
                            ValidMessage validMessage = new ValidMessage(
                                    // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 start
                                    getMessage("TRD_E_0016", "领导"));
                            // 修改超过副总裁的授权额度时报出的message 赵东东 2019.8.27 end
                            validMessageList.add(validMessage);
                        }
                    }
                }
                //add by 张年  2019/08.02 start
                //资产与名单资产是否匹配(包括有标的、无标的)
                if (trdProductDeal != null) {
                    // TISHKBS-411
                    if (!isNameSoftControl) {
                        if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                            Map<String, Object> param = new HashMap<>();
                            if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                                param.put("finprodId", trdProductDeal.getFinprodId());
                            } else {
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
                            } else if (!Boolean.parseBoolean(param.get("isAssetMatch").toString()) && Boolean.parseBoolean(param.get("isAssetWhiteList").toString())) {
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
                }
                //add by 张年  2019/08.02 end
            } catch (Exception e) {
                logger.info("执行风险相关交易共通check出现异常，异常信息如下：");
                logger.error(e.getMessage(), e);
            }
        }

        return validMessageList;
    }

    // add by qianngchengxi 2019-08-06 start

    /**
     * check 属性stlPayOrders
     *
     * @param value
     * @param validMessageList
     */
    default void checkOrder(Object value, List<ValidMessage> validMessageList) {
        Field field1 = ReflectionUtils.findField(value.getClass(), "stlPayOrders");
        if (field1 != null) {
            ReflectionUtils.makeAccessible(field1);
            Object orders1 = ReflectionUtils.getField(field1, value);
            if (orders1 instanceof StlPayOrders) {
                StlPayOrders order1 = (StlPayOrders) orders1;
                if ((DictCodeList.是否_是.getValue().equals(order1.getIsNeedSend()) || DictCodeList.是否_是.getCodeName().equals(order1.getIsNeedSend())) && (StringUtil.isEmpty(order1.getMyAcctNo()) || StringUtil.isEmpty(order1.getCounterAcctNo()))) {
                    validMessageList.add(new ValidMessage("是否发送指令是：是，收付款账户都不能为空。"));
                }
            }
        }
    }
    // add by qianngchengxi 2019-08-06 start

    /**
     * 验证方法
     *
     * @param value 需要被验证的业务对象
     * @return 验证消息，如果为空则表示验证通过
     * @since 1.0
     */
    List<ValidMessage> bussValid(Object value);

    default String isNameSoftControl(Object value) {
        StringBuilder confirmMessage = new StringBuilder();
        boolean isNameSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_NAME_SOFTCONTROL"));
        if (!isNameSoftControl) {
            return confirmMessage.toString();
        }
        try {
            RiskNamListCommonValidatorService riskNamListCommonValidatorService = SpringContextUtil.getBean(
                    RiskNamListCommonValidatorService.class);
            TrdProductDeal trdProductDeal = null;
            String repType = null;

            // 副总裁/总裁助理授权额度检查相关处理
            String counterId = null;
            BigDecimal prinAmt = null;
            String 平安资产类型 = null;
            String windtypeone = null;
            String tradeMarket = null;
            String ps = null;
            // 资产类型改为授权类型 add by renping 2019-08-06  start
            String finprodId = null;
            // 资产类型改为授权类型 add by renping 2019-08-06  end
            // 资产类型改为授权类型 add by zhangnian 2020-03-04  start
            String 无标的投管资产类型映射行内资产分类 = null;
            // 资产类型改为授权类型 add by zhangnian 2019-03-04  end
            // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2020-03-04  start
            String inputValue1 = null;
            String inputValue2 = null;
            String inputValue3 = null;
            // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2019-03-04  end
            // 债券投资交易
            if (value instanceof EtrInvestBondInfoDto) {
                EtrInvestBondInfoDto inputDto = (EtrInvestBondInfoDto) value;
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改债券获取金额by赵东东 2019.9.4 start
                prinAmt = inputDto.getEntrustNum();
                // 修改债券获取金额by赵东东 2019.9.4 end
                平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 拆借投资交易
            else if (value instanceof EtrLendSpecInfoDto) {
                EtrLendSpecInfoDto inputDto = (EtrLendSpecInfoDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_拆借.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeList.平安资产类型_拆借.getValue();
                inputValue2 = ps;
            }
            // 存款类资产投资交易
            else if (value instanceof EtrBrowDepoInfoDto) {
                EtrBrowDepoInfoDto inputDto = (EtrBrowDepoInfoDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_存款.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 基金类投资交易
            else if (value instanceof EtrFundDto) {
                EtrFundDto inputDto = (EtrFundDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 计划类资产（净值型）交易
            else if (value instanceof EtrPlanAssetNDto) {
                // 修改强转类型by赵东东2019.9.4 start
                /* EtrFundDto inputDto = (EtrFundDto)value; */
                EtrPlanAssetNDto inputDto = (EtrPlanAssetNDto) value;
                // 修改强转类型by赵东东2019.9.4 start
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_净值型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 计划类资产（利率型）交易
            else if (value instanceof EtrPlanAssetRDto) {
                EtrPlanAssetRDto inputDto = (EtrPlanAssetRDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    prinAmt = inputDto.getEntrustNum();
                }
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_利率型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 回购资产交易
            else if (value instanceof EtrRepoDto) {
                EtrRepoDto inputDto = (EtrRepoDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                repType = inputDto.getAssetType1();
                inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                inputValue2 = repType;
                inputValue3 = ps;
            }
            // 项目类资产交易
            else if (value instanceof EtrProjectDto) {
                EtrProjectDto inputDto = (EtrProjectDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_项目类资产.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            //  计划类资产（货币型）交易
            else if (value instanceof EtrPlanAssetCDto) {
                EtrPlanAssetCDto inputDto = (EtrPlanAssetCDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_货币型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            //  买断式回购
            else if (value instanceof EtrBoutRepoDto) {
                EtrBoutRepoDto inputDto = (EtrBoutRepoDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_买断式回购.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeList.平安资产类型_买断式回购.getValue();
                inputValue2 = ps;
            }
            // 债券借贷
            else if (value instanceof EtrSecLendDto) {
                EtrSecLendDto inputDto = (EtrSecLendDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改获取金额by赵东东 2019.8.23 start
                prinAmt = inputDto.getTotalAmt();
                // 修改获取金额by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改获取借贷方向by赵东东 2019.8.23 start
                ps = inputDto.getBusiPs();
                // 修改获取借贷方向by赵东东 2019.8.23 end
                finprodId = "";//inputDto.getFinprodId();
                inputValue1 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
            }
            // 股票
            else if (value instanceof EtrStockDto) {
                EtrStockDto inputDto = (EtrStockDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                // 将ps不等于T02的改成T01 赵东东 2019.8.26 start
                if (!StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                    // 将代码中的字符串改掉by赵东东 2019.8.26 start
                    ps = DictCodeList.交易方向_买入.getValue();
                    // 将代码中的字符串改掉by赵东东 2019.8.26 end
                }
                // 将ps不等于T02的改成T01 赵东东 2019.8.26 end
                finprodId = inputDto.getFinprodId();
            }
            // 外汇即期
            else if (value instanceof EtrWtSpotexDto) {
                EtrWtSpotexDto inputDto = (EtrWtSpotexDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
            }
            // 外汇远期
            else if (value instanceof EtrForwexDto) {
                EtrForwexDto inputDto = (EtrForwexDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
            }
            // 外汇掉期
            else if (value instanceof EtrWtFxswapDto) {
                EtrWtFxswapDto inputDto = (EtrWtFxswapDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改获取交易金额by赵东东 2019.8.23 start
                prinAmt = inputDto.getTradeAmt();
                // 修改获取交易金额by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改获取交易方向by赵东东 2019.8.23 start
                ps = inputDto.getTradePs();
                // 修改获取交易方向by赵东东 2019.8.23 start
                inputValue1 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
            }
            // 利率互换
            else if (value instanceof EtrIRSDto) {
                EtrIRSDto inputDto = (EtrIRSDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改金额的大小by赵东东 2019.8.23 start
                prinAmt = inputDto.getEntrustAmt().multiply(new BigDecimal(10000));
                // 修改金额的大小by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改交易方向by赵东东 2019.8.23 start
                ps = inputDto.getBusiPs();
                // 修改交易方向by赵东东 2019.8.23 start
                inputValue1 = DictCodeListExt2.平安资产类型_利率互换.getValue();
            }
            // 可转债 add by gejiaao
            else if (value instanceof EtrConBondDto) {
                EtrConBondDto inputDto = (EtrConBondDto) value;
                EtrConBondService etrConBondService = SpringContextUtil.getBean(EtrConBondService.class);
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                prinAmt = inputDto.getPurchBondTotal();
                平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = etrConBondService.getTrdProductDeal(inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }

            // 可转债转股 add by gaopeng  20191123
            else if (value instanceof EtrBondToStockDto) {
                EtrBondToStockDto inputDto = (EtrBondToStockDto) value;
                EtrBondToStockService etrBondToStockService = SpringContextUtil.getBean(EtrBondToStockService.class);
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                prinAmt = inputDto.getStocketrPriceTotal();
                平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = etrBondToStockService.getTrdProductDeal(inputDto);
                ps = inputDto.getPs();
                trdProductDeal.setFinprodId(inputDto.getShareFinprodId());
                finprodId = inputDto.getShareFinprodId();
            } else if (value instanceof EtrExchangeTradeSaveSecRepoRequest) {
                EtrExchangeTradeSaveSecRepoRequest inputDto = (EtrExchangeTradeSaveSecRepoRequest) value;
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }

            //场内交易指令 add by yzg 20201214 start
            else if (value instanceof EtrExchangeTradeSaveRequest) {
                EtrExchangeTradeSaveRequest inputDto = (EtrExchangeTradeSaveRequest) value;
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                } else if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                } else if (StringUtil.equals(inputDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                } else {
                    平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                }
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            //场内交易指令 add by yzg 20201214 end
            //交易所质押式协议回购 add by chendijun 20210311
            else if (value instanceof EtrExchangeAgreRepTrade001Dto) {
                EtrExchangeAgreRepTrade001Dto inputDto = (EtrExchangeAgreRepTrade001Dto) value;
                counterId = inputDto.getCounterId();
                //prinAmt = inputDto.getPrintAmt();
                //回购金额和方向判断 add by yzg 20210325 start
                EtrExchangeAgreRepTrade001Service etrAgreRepTrade001Service = SpringContextUtil.getBean(EtrExchangeAgreRepTrade001Service.class);
                //逆回购和正回购按正常交易金额和交易方向授权
                if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
                        || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
                    ps = inputDto.getPs();
                    prinAmt = inputDto.getTradeAmt();
                }
                //提前购回、到期购回、中途换券、解除质押根据原始回购的交易方向和回购金额进行授权判断
                else if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_提前购回.getValue())
                        || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_到期购回.getValue())
                        || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
                        || StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
                    //获得初始活够金额和交易方向
                    Map<String, Object> map = new HashMap<>();
                    // 上交所、深交所取值来源不一致
                    map.put("tradeId", (inputDto.getIntiTradeId() == null || inputDto.getIntiTradeId().equals("")) ? inputDto.getTradeId() : inputDto.getIntiTradeId());
                    EtrExchangeAgreRepTrade001Dto result = etrAgreRepTrade001Service.getEntrustInfoByTradeId(map);
                    if (StringUtil.isNotEmpty(result)) {
                        ps = result.getPs();
                        prinAmt = result.getTradeAmt();
                    }
                }
                //到期续作使用原始的回购金额和续做的回购金额中的较大值进行判断
                else if (StringUtil.equals(inputDto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())) {
                    //获得初始回购金额和交易方向
                    Map<String, Object> map = new HashMap<>();
                    map.put("tradeId", (inputDto.getIntiTradeId() == null || inputDto.getIntiTradeId().equals("")) ? inputDto.getTradeId() : inputDto.getIntiTradeId());
                    EtrExchangeAgreRepTrade001Dto result = etrAgreRepTrade001Service.getEntrustInfoByTradeId(map);
                    if (StringUtil.isNotEmpty(result)) {
                        ps = result.getPs();
                        prinAmt = result.getTradeAmt();
                        if (inputDto.getTradeAmt().compareTo(prinAmt) > 0) {
                            prinAmt = inputDto.getTradeAmt();
                        }
                    } else {
                        ps = DictCodeListExt3.交易方向_逆回购.getValue();
                        prinAmt = inputDto.getTradeAmt();
                    }
                }
                //回购金额和方向判断 add by yzg 20210325 end
                平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                repType = DictCodeList.回购类型_质押式协议回购.getValue();
                inputValue2 = repType;
                if (StringUtil.equals(inputDto.getRepPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                    windtypeone = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
                    inputValue3 = DictCodeListExt3.行内资产三级分类_正回购.getValue();
                } else {
                    windtypeone = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
                    inputValue3 = DictCodeListExt3.行内资产三级分类_逆回购.getValue();
                }
                tradeMarket = inputDto.getTradeMarket();

                trdProductDeal = new TrdProductDeal();
                trdProductDeal.setTradeDate(DateUtil.parse(inputDto.getVdate()));
                trdProductDeal.setFinprodId(inputDto.getFinprodId());
            }

            if (trdProductDeal != null) {
                if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                    Map<String, Object> param = new HashMap<>();
                    if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                        param.put("finprodId", trdProductDeal.getFinprodId());
                    } else {
                        param.put("inputValue1", inputValue1);
                        param.put("inputValue2", inputValue2);
                        param.put("inputValue3", inputValue3);
                    }
                    param = riskNamListCommonValidatorService.名单是否匹配(param);
                    if (Boolean.parseBoolean(param.get("isAssetMatch").toString())) {
                        if (param.containsKey("listTypeAsset")) {
                            confirmMessage.append("资产在黑名单内，禁止投资!");
                        }
                    } else if (!Boolean.parseBoolean(param.get("isAssetMatch").toString()) && Boolean.parseBoolean(param.get("isAssetWhiteList").toString())) {
                        confirmMessage.append("该资产不在白名单内，禁止投资!");
                    }
                    if (!Boolean.parseBoolean(param.get("isOrgBlackMatch").toString())) {
                        if (param.containsKey("listTypeOrg")) {
                            confirmMessage.append("资产所属机构（或交易对手）在黑名单内，禁止投资!");
                        }
                    } else if (!Boolean.parseBoolean(param.get("isOrgWhiteMatch").toString())) {
                        confirmMessage.append("该资产所属机构（或交易对手）不在白名单内，禁止投资!");
                    }
                }
            }
            //add by 张年  2019/08.02 end
        } catch (Exception e) {
            logger.info("执行风险相关交易共通check出现异常，异常信息如下：");
            logger.error(e.getMessage(), e);
        }
        return confirmMessage.toString();
    }
}
