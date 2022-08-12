/*
 * 文件名：EtrBondFirstLBiddingService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.entrust.etrexchangetrade.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.service.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import com.joyintech.background.component.common.pack.common.security.User;
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
import com.joyintech.background.component.order.exchange.common.constant.UnitEnum;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.common.dao.EntrustCommonPersonSelectMapper;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.service.entrust.etrexchangetrade.dao.EtrExchangeTradeMapper;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;

import java.util.Date;

/**
 * 场内交易委托 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年5月6日
 * @see EtrExchangeTradeService
 * @since 1.0
 */
@Service
public class EtrExchangeTradeService extends BaseService{

    @Autowired
    private EtrExchangeTradeMapper etrExchangeTradeMapper;

    /**
     * 委托人员信息
     */
    @Autowired
    private EntrustCommonPersonSelectMapper entrustCommonPersonSelectMapper;
    /**
     * 指令分发接口
     */
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;
    /**
     * 投资委托基础组件
     */
    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private EtrComponentCommService<EtrBlockTradeStockDto> componentService;

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 委托指令附属表Mapper
     */
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    /**
     * 指令委托流水
     */
    @Autowired
    EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    /**
     * 指令持仓流水
     */
    @Autowired
    EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    /**
     * FinProductMapper
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    public static final String ENTRUST_RECORD_NEW = "new";
    
    public static final String ENTRUST_RECORD_MODIFY = "modify";


    /**
     * 工作流完成方法
     * @param dto
     * @param operationType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void doComplete(EtrExchangeTrade001Dto dto, String operationType) throws IllegalAccessException, InvocationTargetException {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EntrustCommonDto entrustCommonDto = new EntrustCommonDto();
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        entrustCommonDto.setEtrEntrust(etrEntrust);
        entrustCommonDto.setEtrEntrustAdd(etrEntrustAdd);
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(dto.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {
                etrEntrustAdd.setAssetType(finProductType.getType8());
            }else {
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        }
        
        //设置清算速度 硬编码
		if (StringUtil.isNotEmpty(dto.getSettleSpeed())) {
			if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_0.getValue())) {
				dto.setSettleSpeed("0");
			} else if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_1.getValue())) {
				dto.setSettleSpeed("1");
			}
		}

        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 start
        String isPushMsg = DictCodeList.是否_否.getValue();
        // add by tanxuan on 20200514 指令分发推送MQ 参数定义 end

        // 构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        
        //待分发逻辑处理
        BeanUtil.copyPropertiesIgnoreNull(etrEntrust, dto);
        BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, dto);

        etrEntrust.setBusinessModel(dto.getBusinessModel());
        //根据投资目的与业务模式的对应关系入库时赋值
        if(StringUtil.equals("Y1",dto.getBusinessModel())){
            dto.setInvAim("AC");
            etrEntrust.setInvAim("AC");
        }else if(StringUtil.equals("Y2",dto.getBusinessModel())){
            dto.setInvAim("FVTPL");
            etrEntrust.setInvAim("FVTPL");
        }
        // 自动分发
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        if (busiAutoEtrDispService != null) {
            // 检测是否设置交易员
            if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                // 跳出自动分发
                entity.setEtrDistributeList(etrDistributeList);
                isPushMsg = DictCodeList.是否_是.getValue();
            } else {
                // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                // 设置指令日期 add by yufei 20200808 start
                if(etrEntrust.getEntrustDate() == null) {
                    etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
                }
                // 设置指令日期 add by yufei 20200808 end
                if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                    // 自动分发
                    entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
            }
        }
        if(StringUtil.isEmptyList(entity.getEtrDistributeList())){//无分发人员
        	baseDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        	etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        }


        // 股票竞价指令dto
        EtrStockBiddingDto etrStockBiddingDto = new EtrStockBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockBiddingDto, etrSinglePortfolioDto);
            etrStockBiddingDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
        }

        // 基金竞价指令dto
        EtrFundBiddingDto etrFundBiddingDto = new EtrFundBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrFundBiddingDto, etrSinglePortfolioDto);
            etrFundBiddingDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());
        }

        // 大宗债券质押式回购指令dto
        EtrBlockTradePledgeRepoDto etrBlockTradePledgeRepoDto = new EtrBlockTradePledgeRepoDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradePledgeRepoDto, etrSinglePortfolioDto);
            etrBlockTradePledgeRepoDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradePledgeRepoDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradePledgeRepoDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradePledgeRepoDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            if (DictCodeListExt3.交易方向_正回购.getValue().equals(etrBlockTradePledgeRepoDto.getPs())) {
                etrBlockTradePledgeRepoDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            } else {
                etrBlockTradePledgeRepoDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            etrBlockTradePledgeRepoDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        }

        // 大宗股票指令dto
        EtrBlockTradeStockDto etrBlockTradeStockDto = new EtrBlockTradeStockDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeStockDto, etrSinglePortfolioDto);
            etrBlockTradeStockDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeStockDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradeStockDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeStockDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
        }

        // 大宗基金指令dto
        EtrBlockTradeFundDto etrBlockTradeFundDto = new EtrBlockTradeFundDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeFundDto, etrSinglePortfolioDto);
            etrBlockTradeFundDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeFundDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradeFundDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeFundDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());
        }

        // 大宗债券指令dto
        EtrBlockTradeBondDto etrBlockTradeBondDto = new EtrBlockTradeBondDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeBondDto, etrSinglePortfolioDto);
            etrBlockTradeBondDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8));// 收益率
            etrBlockTradeBondDto.setFullPrice(dto.getFprice());// 全价
            etrBlockTradeBondDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeBondDto.setCounterSeat(dto.getSeatId()); // 席位号
            etrBlockTradeBondDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeBondDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBlockTradeBondDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
        }

        // 债券竞价指令
        EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
            etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondBiddingDto.setFullPrice(dto.getFprice()); // 全价
            etrBondBiddingDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
        }

        // 上交所固定收益平台现券交易指令
        EtrBondFxdIncmPlatSHDto etrBondFxdIncmPlatSHDto = new EtrBondFxdIncmPlatSHDto();
        if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondFxdIncmPlatSHDto, etrSinglePortfolioDto);
            etrBondFxdIncmPlatSHDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondFxdIncmPlatSHDto.setFullPrice(dto.getFprice()); // 全价
            etrBondFxdIncmPlatSHDto.setCounterId(dto.getCounterId());// 交易对手
            etrBondFxdIncmPlatSHDto.setTrader(dto.getTrader());// 本方交易员
            etrBondFxdIncmPlatSHDto.setCounterTrader(dto.getCounterTrader());// 对手方交易员
            etrBondFxdIncmPlatSHDto.setCounterOrg(dto.getCounterOrg());// 对手方交易商
            etrBondFxdIncmPlatSHDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBondFxdIncmPlatSHDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondFxdIncmPlatSHDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondFxdIncmPlatSHDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            etrBondFxdIncmPlatSHDto.setSettleSpreed(Long.parseLong(dto.getSettleSpeed()));//清算速度
        }

        // 债券回售指令
        EtrBondRepurchaseDto etrBondRepurchaseDto = new EtrBondRepurchaseDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepurchaseDto, etrSinglePortfolioDto);
            etrBondRepurchaseDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRepurchaseDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
        }

        // 配股指令
        EtrStockRightDto etrStockRightDto = new EtrStockRightDto();
        if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockRightDto, etrSinglePortfolioDto);
            etrStockRightDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrStockRightDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类
        }

        // 新股申购指令
        EtrStockNewDto etrStockNewDto = new EtrStockNewDto();
        if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockNewDto, etrSinglePortfolioDto);
            etrStockNewDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrStockNewDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类
        }

        // 配债指令
        EtrBondRightDto etrBondRightDto = new EtrBondRightDto();
        if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondRightDto, etrSinglePortfolioDto);
            etrBondRightDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRightDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
        }

        // 可转债申购指令
        EtrConvBondApplyDto etrConvBondApplyDto = new EtrConvBondApplyDto();
        if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrConvBondApplyDto, etrSinglePortfolioDto);
            etrConvBondApplyDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrConvBondApplyDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
        }
        
     // 债转股指令
        EtrBondToStockDto etrBondToStockDto = new EtrBondToStockDto();
        if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondToStockDto, etrSinglePortfolioDto);
            etrBondToStockDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondToStockDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            etrBondToStockDto.setExpConNum(dto.getToShareAvailableNum());//可转股数量
        }
        
        // 标准券出入库指令
        EtrStandardBondInOutDto etrStandardBondInOutDto = new EtrStandardBondInOutDto();
        if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(baseDto.getEntrustType())) {
        	etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            BeanUtil.copyPropertiesIgnoreNull(etrStandardBondInOutDto, etrSinglePortfolioDto);
            //etrStandardBondInOutDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
			if (etrStandardBondInOutDto.getPs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())) {
				etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
			} else if (etrStandardBondInOutDto.getPs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())) {
				etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
			}
        }
        
        // 债券质押式回购竞价指令
        EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(baseDto.getEntrustType())) {
        	etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
            etrBondRepoBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
        }

        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        
		if (null != dto.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(dto.getEntrustId())) {
			dto.setOptType(ENTRUST_RECORD_MODIFY);
		}
		
        if (StringUtil.equals(dto.getOptType(), ENTRUST_RECORD_NEW)) {//
            if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).entrustNew(etrStockBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).entrustNew(etrFundBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).updateSettings(paraDto);
                //质押式回购 设置 AssetType1->平安资产类型_质押式回购
                etrBlockTradePledgeRepoDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
                EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).entrustNew(etrBlockTradePledgeRepoDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).entrustNew(etrBlockTradeStockDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).entrustNew(etrBlockTradeFundDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).entrustNew(etrBlockTradeBondDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustNew(etrBondBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).entrustNew(etrBondFxdIncmPlatSHDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).entrustNew(etrBondRepurchaseDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockRightDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockRightDto.class).entrustNew(etrStockRightDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockNewDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockNewDto.class).entrustNew(etrStockNewDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRightDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRightDto.class).entrustNew(etrBondRightDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).entrustNew(etrConvBondApplyDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondToStockDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondToStockDto.class).entrustNew(etrBondToStockDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).entrustNew(etrStandardBondInOutDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
              //质押式回购 设置 AssetType1->平安资产类型_质押式回购
                etrBondRepoBiddingDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustNew(etrBondRepoBiddingDto);
            }
        } else {
            if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).entrustEdit(etrStockBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).entrustEdit(etrFundBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).entrustEdit(etrBlockTradePledgeRepoDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).entrustEdit(etrBlockTradeStockDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).entrustEdit(etrBlockTradeFundDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).entrustEdit(etrBlockTradeBondDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustEdit(etrBondBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).entrustEdit(etrBondFxdIncmPlatSHDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).entrustEdit(etrBondRepurchaseDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockRightDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockRightDto.class).entrustEdit(etrStockRightDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStockNewDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStockNewDto.class).entrustEdit(etrStockNewDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRightDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRightDto.class).entrustEdit(etrBondRightDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).entrustEdit(etrConvBondApplyDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondToStockDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondToStockDto.class).entrustEdit(etrBondToStockDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).entrustEdit(etrStandardBondInOutDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustEdit(etrBondRepoBiddingDto);
            }
        }
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
        // 指令持仓流水 、现金流水处理
        /*EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();// 组合持仓流水、组合现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();// 现金流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
        EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合现金流水DTO
        if (DictCodeList.交易方向_卖出.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeList.交易方向_行权_回售.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt1.交易方向_债转股.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_质押券入库.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {*/

            /********************** 持仓流水DTO构造 *************************************/
            /*etrSecPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrSecPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(etrSinglePortfolioDto.getSecAcctId());// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId(etrSinglePortfolioDto.getFinprodId());// 金融产品代码
            if (DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs()) && (DictCodeList.交易平台_竞价撮合平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
            		|| DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
            		|| DictCodeList.交易平台_综合协议交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat()))) {
                etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");
            }else if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {
            	etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");
            }
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(etrSinglePortfolioDto.getInvAim()) ? etrSinglePortfolioDto.getInvAim() : "999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {
				etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 标准券数量
			} else {
				etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustNum().multiply(new BigDecimal(-1)));// 数量
			}
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(etrSinglePortfolioDto.getEntrustId());// 指令编号
            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);*/
            /*************************************************************************/
        /*} else if (DictCodeList.交易方向_买入.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt3.交易方向_逆回购.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt2.交易方向_配股.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeList.交易方向_申购.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt3.交易方向_配债.getValue().equals(etrSinglePortfolioDto.getPs())) {*/
            /********************** 持仓流水DTO构造 *************************************/
            /*etrCashPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrCashPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrCashPositionFlowDtoUnit.setAcctId(etrSinglePortfolioDto.getDealCurAcct());// 资金账户
            etrCashPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrCashPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
            if (null != dto.getFprice()) {
                etrCashPositionFlowDtoUnit.setAmount(dto.getEntrustNum().multiply(dto.getFprice()).multiply(new BigDecimal(-1)));
            } else {
                etrCashPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 数量
            }
            etrCashPositionFlowDtoUnit.setOrderId(etrSinglePortfolioDto.getEntrustId());// 业务编号
            etrCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
            etrCashPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());// 冲正标识
            etrCashPositionFlowDtoUnit.setOffsetId(etrSinglePortfolioDto.getEntrustId());// 冲销业务编号
            */
            /*************************************************************************/
           /* etrCashPositionFlows.add(etrCashPositionFlowDtoUnit);
        }

        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setDealEtr(true);
        etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache()));
        etrPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setCdate(DateUtil.platDateFromCache()));
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);*/
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end

        // 自动分发数据入库 add by yufei 20200808 start
        if(!StringUtil.isEmptyList(entity.getEtrDistributeList())){
            baseEtrInvestInfoService.distributeDelegate(entity.getEtrDistributeList());
        }
        // 自动分发数据入库 add by yufei 20200808 end
        // add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
    }


    public void doCompleteMulti(EtrExchangeTradeMulti001Dto dto, String operationType) throws IllegalAccessException, InvocationTargetException {
        EtrExchangeTrade001Dto etrExchangeTrade001Dto = new EtrExchangeTrade001Dto();
        BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto,dto);
        etrExchangeTrade001Dto.setPortfolioId(dto.getPortfolioTreeId());
        etrExchangeTrade001Dto.setAcctNo(dto.getTradeMultiPtl001Dtos().get(0).getAcctNo());
        etrExchangeTrade001Dto.setSecAcctId(dto.getTradeMultiPtl001Dtos().get(0).getSecAcctId());
        this.doComplete(etrExchangeTrade001Dto, operationType);

        //插入指令多组合明细表
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildMultiEtrFlow(dto);
        if(!StringUtil.isEmptyList(baseEtrInvestInfoDto.getEtrEntrustDetailList())) {
            for (EtrEntrustDetail etrEntrustDetail:baseEtrInvestInfoDto.getEtrEntrustDetailList()) {
                etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
            }
        }
    }

    /**
     * 构造投资指令DTO
     * @param dto
     * @return
     */
    public EtrBaseDto getEtrBaseDto(EtrExchangeTrade001Dto dto) {
        // 构造投资指令DTO start
        EtrBaseDto baseDto = new EtrBaseDto();
        baseDto.setMdate(dto.getMdate());// 指令起始日
        baseDto.setVdate(dto.getVdate());// 指令结束日
        baseDto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());// 指令状态
        // 指令接收日期 NULL
        // 指令关闭日期 NULL
        baseDto.setInvManagerId(dto.getInvManagerId());// 登录用户.用户ID
        baseDto.setCreateUser(dto.getCreateUser());// 创建用户
        baseDto.setCreateDept(dto.getCreateDept());// 创建部门
        baseDto.setCreateTime(dto.getCreateTime());// 创建时间
        baseDto.setUpdateUser(LoginUserUtil.getUserName());// 更新用户
        baseDto.setUpdateTime(new Date());// 更新时间
        if(StringUtil.equals(dto.getIsMulti(),DictCodeList.是否_是.getValue()) ) {
            baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        }
        else {
            baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        }

        baseDto.setEntrustType(dto.getEntrustBussType());// 指令业务类型
        baseDto.setEntrustId(dto.getEntrustId());// 指令编号
        if (iPlatformDate != null) {
            baseDto.setEntrustDate(iPlatformDate.getSysDate());// 委托日期
        }
        baseDto.setTargetType(dto.getTargetType());// 目标类型
        // 构造投资指令DTO end
        return baseDto;
    }

    /**
     * 构造单组合单资产DTO
     * @param dto
     * @return
     */
    public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrExchangeTrade001Dto dto) {
        //构造投资指令DTO
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        // 构造单组合单资产DTO start
        EtrSinglePortfolioDto etrSinglePortfolioDto = new EtrSinglePortfolioDto();
        BeanUtil.copyPropertiesIgnoreNull(etrSinglePortfolioDto, baseDto);
        etrSinglePortfolioDto.setPortfolioId(dto.getPortfolioId());// 组合代码
        etrSinglePortfolioDto.setStockId(dto.getStockId());// 证券代码
        etrSinglePortfolioDto.setFinprodId(dto.getFinprodId());// 证券代码对应的金融产品代码
        etrSinglePortfolioDto.setSecAcctId(dto.getSecAcctId());// 证券账户
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        etrSinglePortfolioDto.setPs(dto.getPs());// 委托方向
        etrSinglePortfolioDto.setPriceModel(dto.getPriceModel());// 价格模式
        etrSinglePortfolioDto.setPrice(dto.getEntrustPrice() != null ? dto.getEntrustPrice() : dto.getPrice());// 指令价格
        etrSinglePortfolioDto.setEntrustNum(dto.getEntrustNum());// 指令数量
        etrSinglePortfolioDto.setEntrustAmt(dto.getEntrustAmt());// 指令金额
        etrSinglePortfolioDto.setInvAim(dto.getInvAim());// 投资目的
        etrSinglePortfolioDto.setTradeMarket(dto.getTradeMarket());// 交易场所
        etrSinglePortfolioDto.setTradePlat(dto.getTradePlat());// 交易平台
        /*
         * if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.深圳证券交易所
         * 交易平台 = 数据字典.交易平台.综合协议平台
         * else if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.上海证券交易所
         * 交易平台 = 数据字典.交易平台.大宗交易平台 end
         */
        if (DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
            && DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_综合协议交易平台.getValue());

        } else if (DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
                   && DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
            etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_大宗交易平台.getValue());
        }

        /***********************************************************************/
        etrSinglePortfolioDto.setFinprodType2(dto.getFinprodType2());// 金融产品分类
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        /***********************************************************************/
        etrSinglePortfolioDto.setDealMode(dto.getDeclaType());//申报类型-db 交易方式
        // 构造单组合单资产DTO end
        return etrSinglePortfolioDto;
    }

    /**
     * 生成流水号获取
     *
     * @return
     */
    public static String getSeqNo() {
        return IDGenerator.generate();
    }


    public Page<EtrExchangeTrade003Dto> selectEtrExchange(int page, int length,Map<String,Object> param){
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        param.put("ENTRUST_CLASS_02", DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        String portfolioId = (String)param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        return super.queryForPage(page, length, () -> etrExchangeTradeMapper.selectEtrExchange(param));
    }
    //add by chenjiaxing 导出全部数据 202100902 start
    public List<EtrExchangeTrade003Dto> selectEtrExchange2(Map<String,Object> param){
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        String portfolioId = (String)param.get("portfolioId");
        if (StringUtil.isNotEmpty(portfolioId)) {
            param.put("portfolioIdList",
                    StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        return etrExchangeTradeMapper.selectEtrExchange(param);
    }
    //add by chenjiaxing 导出全部数据 202100902 end

    /**
     * 平台日期
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

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
    public Page<EtrExchangeTrade002Dto> findPositionsPage(int page, int length,
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
        Page<EtrExchangeTrade002Dto> result = super.queryForPage(page, length, () -> etrExchangeTradeMapper.findPositionsPage(pageParam));
        if(!StringUtil.isEmptyList(result.getRoot())){
            // 计算浮动盈亏
            for(EtrExchangeTrade002Dto dto : result.getRoot()){
                // 最新净价*组合持仓.数量 - 持仓成本 as 浮动盈亏
                BigDecimal floatPL = ArithmeticUtil.multiply(dto.getPrice(),dto.getPtlPositionsNum());
                floatPL = ArithmeticUtil.subtract(floatPL,dto.getPositionsAmt());
                dto.setFloatingPL(floatPL);
            }
        }
        return result;
        // 浮动盈亏计算修改 modify by yufei 20200806 end
    }

    /**
     * 债券质押持仓信息查询
     *
     * @param page int
     * @param length int
     * @param pageParam int
     * @return
     * @see
     * @since 1.0
     */
    public Page<EtrExchangeTrade002Dto> findZqzyPositionsPage(int page, int length,
            Map<String, Object> pageParam) {
        getZqzyPositionParam(pageParam);
        Page<EtrExchangeTrade002Dto> result = super.queryForPage(page, length, () -> etrExchangeTradeMapper.findZqzyPositionsPage(pageParam));
        return result;
    }

    /**
     *
     * @param pageParam
     * @return
     */
    public List<EtrExchangeTrade002Dto> findZqzyPositionsList(Map<String, Object> pageParam) {
        getZqzyPositionParam(pageParam);
        return etrExchangeTradeMapper.findZqzyPositionsPage(pageParam);
    }

    private void getZqzyPositionParam(Map<String, Object> pageParam) {
        pageParam.put("POSITION_TYPE_03", BussinessDictCodeList.持仓类型_实际.getValue());
        pageParam.put("POSITION_TYPE_04", BussinessDictCodeList.持仓类型_质押.getValue());
        pageParam.put("POSITION_TYPE_05", BussinessDictCodeList.持仓类型_融入.getValue());
        pageParam.put("POSITION_TYPE_06", BussinessDictCodeList.持仓类型_融出.getValue());
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
        pageParam.put("PS_01", DictCodeList.出入库方向_入库.getValue());
        pageParam.put("LIBRARY_DIRECTION_01", DictCodeListExt3.交易方向_质押券入库.getValue());
        pageParam.put("TRADE_PLACE_03", BussinessDictCodeList.交易场所_深圳证券交易所.getValue());
        pageParam.put("TRADE_PLACE_04", BussinessDictCodeList.交易场所_上海证券交易所.getValue());
        pageParam.put("MORE_CONVRATE", 0.9);
        if(StringUtil.isNotEmpty(pageParam.get("date"))) {
            pageParam.put("date", DateUtil.parse((String) pageParam.get("date")));
        }
        else {
            pageParam.put("date", iPlatformDate.getSysDate());
            //pageParam.put("date", DateUtil.parse("2020-08-13"));
        }
    }

    /**
     * 查询证券账户联想框文本框
     *
     * @param param 画面参数
     * @return 证券账户联想框文本框
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getSecAcctId(int page, int length,Map<String, Object> param) {
        return super.queryForPage(page, length, () -> etrExchangeTradeMapper.getSecAcctId(param));
    }

    /**
     * 质押券代码下拉框查询
     * @param page
     * @param length
     * @param param
     * @return
     */
    public Page<SelectOptionDto> selectZyq(int page, int length,Map<String, Object> param) {
        return super.queryForPage(page, length, () -> etrExchangeTradeMapper.selectZyq(param));
    }



    /**
     * 查询债券基本信息
     *
     * @param map 参数
     * @return 债券基本信息
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public SecInfoModelDto getSecInfoModel(Map<String, Object> map) {
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("sysDate", DateUtil.platDateFromCache());
        map.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        map.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        map.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        map.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        map.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());

        return etrExchangeTradeMapper.getSecInfoModel(map);
    }

    /**
     * 获取债券付息计划
     *
     * @param finprodId 金融产品代码
     * @return 债券付息计划
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlan(String finprodId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return etrExchangeTradeMapper.getFinCashPlan(map);
    }

    /**
     * a.当前债券面额取得（根据还本计划计算）
     *
     * @param param 参数
     * @return a.当前债券面额取得（根据还本计划计算）
     * @see
     * @since 1.0
     */
    public BigDecimal getCurSecAmt(Map<String, Object> param) {
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        String settleDate = (String) param.get("settleDate");
        param.put("settleDate", DateUtil.parse(settleDate));
        return etrExchangeTradeMapper.getCurSecAmt(param);
    }

    /**
     * 查询债券利率重置信息
     *
     * @param finprodId 金融产品代码
     * @return 债券利率重置信息
     * @see
     * @since 1.0
     */
    public List<FinCashCalcRule> selectBondsIntResetList(String finprodId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", finprodId);
        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return etrExchangeTradeMapper.selectBondsIntResetList(param);
    }

    /**
     * 关闭委托
     */
    public void entrustClose(EtrExchangeTrade003Dto entity){
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        componentService.entrustClose(etrBaseDto);
		// 处理指令持仓和头寸
		if (baseEtrCashPositionCommService != null
				&& DictCodeList.委托状态_已接收.getValue().equals(entity.getEntrustStatus())) {
			baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId());
		}
    }

    /**
     * 撤销委托
     *
     */
    public void entrustRevoke(EtrExchangeTrade003Dto entity){
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        //关闭委托
        componentService.entrustRevoke(etrBaseDto);
        //处理指令持仓和头寸(仅有未被接收的指令才可以撤会,所以一般只可能存在下指令时生成的持仓和流水)
        if (baseEtrCashPositionCommService != null && (DictCodeList.委托状态_待接收.getValue().equals(entity.getEntrustStatus())
        		||DictCodeListExt3.委托状态_待分发.getValue().equals(entity.getEntrustStatus()))) {
            baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId());
        }
        //		if(StringUtil.isNotEmpty(entity.getEntrustId())) {
        //            EtrPositionHoldDto etrPositionHoldDto = EtrCommonCashPositionDealService.revokeEtrPositionHoldDto(entity.getEntrustId());
        //		      if (DictCodeList.交易方向_卖出.getValue().equals(entity.getPs())
        //		            || DictCodeListExt3.交易方向_正回购.getValue().equals(entity.getPs())
        //		            || DictCodeList.交易方向_行权_回售.getValue().equals(entity.getPs())) {
        //		          // 持仓流水
        //	                List<EtrSecPositionFlowDto> etrSecPositionFlows = etrPositionHoldDto.getPtlSecPositionFlows();
        //	                if(!StringUtil.isEmptyList(etrSecPositionFlows)) {
        //	                    for(EtrSecPositionFlowDto dto:etrSecPositionFlows) {
        //	                        dto.setAmount(dto.getAmount().negate());
        //	                        etrSecPositionFlowMapper.deleteByPrimaryKey(dto.getSeqno());
        //	                        EtrCommonCashPositionDealService.dealCommandPosition(dto);
        //	                    }
        //	                }
        //
        //		        } else if(DictCodeList.交易方向_买入.getValue().equals(entity.getPs())
        //		            || DictCodeListExt3.交易方向_逆回购.getValue().equals(entity.getPs())
        //		            || DictCodeListExt2.交易方向_配股.getValue().equals(entity.getPs())
        //		            || DictCodeList.交易方向_申购.getValue().equals(entity.getPs())){
        //		            // 现金流水
        //	                List<EtrCashPositionFlowDto> etrCashPositionFlows = etrPositionHoldDto.getPtlCashPositionFlows();
        //	                if(!StringUtil.isEmptyList(etrCashPositionFlows)) {
        //	                    //将改指令涉及到的指令现金流水删除，并且更新指令可用现金
        //	                    for(EtrCashPositionFlowDto dto:etrCashPositionFlows) {
        //	                        dto.setAmount(dto.getAmount().negate());
        //	                        etrCashPositionFlowMapper.deleteByPrimaryKey(dto.getSeqno());
        //
        //	                        EtrCommonCashPositionDealService.dealCommandBalance(dto);
        //	                    }
        //
        //	                }
        //		        }
        //		}
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
     * //根据金融产品代码获取投管一级分类
     * @param entity
     * @return
     */
    public EtrExchangeTrade001Dto setAssetTypeById(EtrExchangeTrade001Dto entity) {
        // 根据资产获取资产类型
        if (StringUtil.isNotEmpty(entity.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(entity.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {//获取资产一级分类
                entity.setAssetsType(finProductType.getType6());
            }
        }
        return entity;
    }
    
	/**
	 * 
	 * @param finprodType2
	 * @return
	 */
	private String getPAAssetType(String finprodType2) {
		String assetType = "";
		if (StringUtils.isNotEmpty(finprodType2)) {
			if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
				assetType = DictCodeList.平安资产类型_债券.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
				assetType = DictCodeList.平安资产类型_基金.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
				assetType = DictCodeListExt2.平安资产类型_股票.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
					|| StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
					|| StringUtils.equals(finprodType2, "ZYSHG")) {
				assetType = DictCodeList.平安资产类型_质押式回购.getValue();
			}
		}
		return assetType;
	}
    
	public BigDecimal getInterfSysMinPrice(String finprodId, String tradePlat, String tradeMarket, String assetType,
			BigDecimal... price) {
		BigDecimal minPrice = new BigDecimal(0.001);
		String PINANAssetType = getPAAssetType(assetType);
		// 质押式回购 没有资产代码，虚拟的资产代码
		if (StringUtils.equals(PINANAssetType, DictCodeList.平安资产类型_质押式回购.getValue())) {
			finprodId = null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("finprodId", finprodId);
		map.put("tradePlat", tradePlat);
		map.put("tradeMarket", tradeMarket);
		map.put("assetType", PINANAssetType);
		if (price.length > 0 && null != price[0]) {
			map.put("currPrice", new BigDecimal(price[0].toString()));
		}
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinPrice", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) res.get("data");
			if (list != null && list.size() > 0) {
				Map<String, Object> minPriceMap = list.get(0);
				if (null != minPriceMap && null != minPriceMap.get("minPrice")) {
					minPrice = new BigDecimal(minPriceMap.get("minPrice").toString());
				}
			}
		}
		return minPrice;
	}
    
    /**
	 * 
	 * @param finprodId 金融产品代码
	 * @param tradePlat 交易平台
	 * @param assetType 资产大类
	 * @return 获取最小交易单元
	 */
	@SuppressWarnings({ "unchecked" })
	public BigDecimal getInterfSysMinTrdUnit(String finprodId, String tradePlat, String tradeMarket, String assetType,
			String ps) {
		BigDecimal minNum = new BigDecimal(1);
		String PINANAssetType = getPAAssetType(assetType);
		// 质押式回购 没有资产代码，虚拟的资产代码
		if (StringUtils.equals(PINANAssetType, DictCodeList.平安资产类型_质押式回购.getValue())) {
			finprodId = null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("finprodId", finprodId);
		map.put("tradeMarket", tradeMarket);
		map.put("tradePlat", tradePlat);
		map.put("assetType", PINANAssetType);
		map.put("ps", ps);
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinTrdUnitLimit", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) res.get("data");
			if (list != null && list.size() > 0) {
				Map<String, Object> minTrdUnit = list.get(0);
				if (minTrdUnit != null && minTrdUnit.get("minStep") != null) {
					minNum = new BigDecimal(minTrdUnit.get("minStep").toString());
				}
			}
		}
		return minNum;
	}


    public void setCheckParams(EtrExchangeTrade001Dto entity, EtrParameterDto paraDto) {

        /********************************/
        setAssetTypeById(entity);

        //设置默认最小价差、最小交易单位；否则指令组件抛异常；
        paraDto.setMinDiffPrice(new BigDecimal(0.001).setScale(3,BigDecimal.ROUND_HALF_UP));
        paraDto.setMinDealUnit(new BigDecimal(1));
/*        BigDecimal minPrice = getInterfSysMinPrice(entity.getFinprodId(), entity.getTradePlat(),
        		entity.getTradeMarket(), entity.getFinprodType2());
        if(null != minPrice){
        	paraDto.setMinDiffPrice(minPrice);
        }
		BigDecimal minNumStep = getInterfSysMinTrdUnit(entity.getFinprodId(), entity.getTradePlat(),
				entity.getTradeMarket(), entity.getFinprodType2(), entity.getPs());
		if (null != minNumStep) {
			if (StringUtils.equals(entity.getTradePlat(), DictCodeList.交易平台_竞价撮合平台.getValue())
					&& DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
				if (null != entity.getAvailableNum()
						&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else if (null != entity.getAvailableNum() && new BigDecimal(
						entity.getAvailableNum().subtract(entity.getEntrustNum()).divide(minNumStep).intValue())
								.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
										.divide(minNumStep)) == 0) {
					// 设置最小交易单位
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else {
					// 设置最小交易单位
					paraDto.setMinDealUnit(minNumStep);
				}
			} else {
				// 设置最小交易单位
				paraDto.setMinDealUnit(minNumStep);
			}
		}*/
		
		Map<String, UnitEnum> numUnit = new HashMap<String, UnitEnum>();
		if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
			numUnit.put(entity.getTradeMarket(), UnitEnum.份);
		} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
			numUnit.put(entity.getTradeMarket(), UnitEnum.股);
		} else {
			numUnit.put(entity.getTradeMarket(), UnitEnum.张);
		}
		paraDto.setNumUnit(numUnit);
        
        
       /* // 设置最小价差、设置最小买卖单位、设置指令数量单位
        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2()) || DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
                paraDto.setMinDiffPrice(new BigDecimal(0.001).setScale(6,BigDecimal.ROUND_HALF_UP));
                if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())) {
                    if (null != entity.getAssetsType() && DictCodeList.WIND一级分类_含债券一级分类等_国债.getValue().equals(entity.getAssetsType())) {
                    	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                    }
					
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.张);
                    paraDto.setNumUnit(map);
                } else {
					if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(100));
					}
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.股);
                    paraDto.setNumUnit(map);
                }
            } else if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                paraDto.setMinDealUnit(new BigDecimal(10));
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.张);
                paraDto.setNumUnit(map);
            } else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
            	if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())
						&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())
						&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
								.divide(new BigDecimal(100)).intValue())
										.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
												.divide(new BigDecimal(100))) == 0) {
					// 不设置最小交易单位
					paraDto.setMinDealUnit(entity.getEntrustNum());
				}
				else {
					paraDto.setMinDealUnit(new BigDecimal(100));
				}
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.份);
                paraDto.setNumUnit(map);
            }
        }
        if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                if (StringUtils.equals(entity.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
					paraDto.setMinDealUnit(new BigDecimal(1));
				} else {
					if (null != entity.getAvailableNum() && entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(10));
					}
				}
                if (null != entity.getAssetsType() && DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue().equals(entity.getAssetsType())) {
                    paraDto.setMinDealUnit(new BigDecimal(10000));
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.份);
                    paraDto.setNumUnit(map);
                }
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.张);
                paraDto.setNumUnit(map);
            } else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
                if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
                	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                } else {
                	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                }
                if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
                    paraDto.setMinDealUnit(new BigDecimal(100));
					if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(100));
					}
                    if(DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())){
                        Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                        map.put(entity.getTradeMarket(), UnitEnum.股);
                        paraDto.setNumUnit(map);
                    }else{
                        Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                        map.put(entity.getTradeMarket(), UnitEnum.份);
                        paraDto.setNumUnit(map);
                    }
                } else {
                    paraDto.setMinDealUnit(new BigDecimal(10));
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.张);
                    paraDto.setNumUnit(map);
                }
            }
        }*/



        // 竞价交易平台 设置涨跌停
        if (DictCodeList.交易平台_竞价撮合平台.getValue().equals(entity.getTradePlat())) {
            if (null != entity.getLimitUp()) {
                paraDto.setLimitUpAmt(entity.getLimitUp());
            }
            if (null != entity.getLimitDown()) {
                paraDto.setLimitDnAmt(entity.getLimitDown());
            }
            paraDto.setMinReportUnit(new BigDecimal(1));
        }


        // 大宗交易平台
        if (DictCodeList.交易平台_大宗交易平台.getValue().equals(entity.getTradePlat())) {
            /*
             * A股单笔交易不低于30W股，或者交易金额不低于200W人民币；
             * 基金单笔交易数量不低于200W份，或者交易金额不低于200W人民币；
             * 深交所债券买卖或者回购单笔不得低于5千张，或者就交易金额不低于50W人民币；上交所不低于1000手（10000张），
             * 或者交易金额不低于100W人民币； 股票、基金交易场所均为上交所,深交所不支持
             */
            if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
                paraDto.putLimitAmtNum(entity.getTradeMarket(), new BigDecimal(300000), null, UnitEnum.万股,
                    new BigDecimal(2000000), null);
            }
            if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
                paraDto.putLimitAmtNum(entity.getTradeMarket(), new BigDecimal(2000000), null, UnitEnum.万份,
                    new BigDecimal(2000000), null);
            }
            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())
                || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
                || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())) {
                if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
                    paraDto.putLimitAmtNum(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), new BigDecimal(5000), null,
                        UnitEnum.张, new BigDecimal(500000), null);
                } else {
                    paraDto.putLimitAmtNum(DictCodeListExt1.交易场所_上海证券交易所.getValue(), new BigDecimal(10000), null,
                        UnitEnum.张, new BigDecimal(1000000), null);
                }
            }
        }
    }


    /**
     * 保存check
     * @param entity
     * @return
     */
    public BindingResult checkSave(EtrExchangeTrade001Dto entity, BindingResult result)
        throws IllegalAccessException, InvocationTargetException {

        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        if(null != entity.getEntrustPrice()){
            entity.setPrice(entity.getEntrustPrice());// 指令价格 或者净价
        }

        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);

        //设置交易买卖默认参数
        setCheckParams(entity,paraDto);

        if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //股票竞价指令
            EtrStockBiddingDto etrStockBiddingDto = new EtrStockBiddingDto();
            BeanUtil.copyProperties(etrStockBiddingDto, etrSinglePortfolioDto);
            etrStockBiddingDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).entrustCheck(etrStockBiddingDto, result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //基金竞价指令
            EtrFundBiddingDto etrFundBiddingDto = new EtrFundBiddingDto();
            BeanUtil.copyProperties(etrFundBiddingDto, entity);
            etrFundBiddingDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).entrustCheck(etrFundBiddingDto, result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //大宗质押式回购指令
            EtrBlockTradePledgeRepoDto etrBlockTradePledgeRepoDto = new EtrBlockTradePledgeRepoDto();
            BeanUtil.copyProperties(etrBlockTradePledgeRepoDto, entity);
            etrBlockTradePledgeRepoDto.setCounterSeat(entity.getSeatId());
            etrBlockTradePledgeRepoDto.setCounterConNo(entity.getAgreeOnId());// 约定号
            etrBlockTradePledgeRepoDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).entrustCheck(etrBlockTradePledgeRepoDto, result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //大宗股票指令
            EtrBlockTradeStockDto etrBlockTradeStockDto = new EtrBlockTradeStockDto();
            BeanUtil.copyProperties(etrBlockTradeStockDto, entity);
            etrBlockTradeStockDto.setCounterSeat(entity.getSeatId());
            etrBlockTradeStockDto.setCounterConNo(entity.getAgreeOnId());// 约定号
            etrBlockTradeStockDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).entrustCheck(etrBlockTradeStockDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //大宗基金指令
            EtrBlockTradeFundDto etrBlockTradeFundDto = new EtrBlockTradeFundDto();
            BeanUtil.copyProperties(etrBlockTradeFundDto, entity);
            etrBlockTradeFundDto.setCounterSeat(entity.getSeatId());
            etrBlockTradeFundDto.setCounterConNo(entity.getAgreeOnId());// 约定号
            etrBlockTradeFundDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).entrustCheck(etrBlockTradeFundDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            //大宗债券指令
            EtrBlockTradeBondDto etrBlockTradeBondDto = new EtrBlockTradeBondDto();
            BeanUtil.copyProperties(etrBlockTradeBondDto, entity);
            etrBlockTradeBondDto.setCounterSeat(entity.getSeatId());
            etrBlockTradeBondDto.setCounterConNo(entity.getAgreeOnId());// 约定号
            etrBlockTradeBondDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).entrustCheck(etrBlockTradeBondDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 债券竞价指令
            EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
            BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
            etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondBiddingDto.setFullPrice(entity.getFprice()); // 全价
            etrBondBiddingDto.setCalMode(entity.getCalculateField());// 计算方式
            etrBondBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustCheck(etrBondBiddingDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 上交所固定收益平台现券交易指令
            EtrBondFxdIncmPlatSHDto etrBondFxdIncmPlatSHDto = new EtrBondFxdIncmPlatSHDto();
            BeanUtil.copyPropertiesIgnoreNull(etrBondFxdIncmPlatSHDto, etrSinglePortfolioDto);
            etrBondFxdIncmPlatSHDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondFxdIncmPlatSHDto.setFullPrice(entity.getFprice()); // 全价
            etrBondFxdIncmPlatSHDto.setCounterId(entity.getCounterId());// 交易对手
            etrBondFxdIncmPlatSHDto.setTrader(entity.getTrader());// 本方交易员
            etrBondFxdIncmPlatSHDto.setCounterTrader(entity.getCounterTrader());// 对手方交易员
            etrBondFxdIncmPlatSHDto.setCounterOrg(entity.getCounterOrg());// 对手方交易商
            etrBondFxdIncmPlatSHDto.setCounterConNo(entity.getAgreeOnId());// 约定号
            etrBondFxdIncmPlatSHDto.setCalMode(entity.getCalculateField());// 计算方式
            etrBondFxdIncmPlatSHDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrBondFxdIncmPlatSHDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            etrBondFxdIncmPlatSHDto.setSettleSpreed(Long.parseLong(entity.getSettleSpeed()));//清算速度

            EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).entrustCheck(etrBondFxdIncmPlatSHDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 债券回售指令
            EtrBondRepurchaseDto etrBondRepurchaseDto = new EtrBondRepurchaseDto();
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepurchaseDto, etrSinglePortfolioDto);
            etrBondRepurchaseDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrBondRepurchaseDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).entrustCheck(etrBondRepurchaseDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 配股指令
            EtrStockRightDto etrStockRightDto = new EtrStockRightDto();
            BeanUtil.copyPropertiesIgnoreNull(etrStockRightDto, etrSinglePortfolioDto);
            etrStockRightDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrStockRightDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrStockRightDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrStockRightDto.class).entrustCheck(etrStockRightDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 新股申购指令
            EtrStockNewDto etrStockNewDto = new EtrStockNewDto();
            BeanUtil.copyPropertiesIgnoreNull(etrStockNewDto, etrSinglePortfolioDto);
            etrStockNewDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrStockNewDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrStockNewDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrStockNewDto.class).entrustCheck(etrStockNewDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 配债指令
            EtrBondRightDto etrBondRightDto = new EtrBondRightDto();
            BeanUtil.copyPropertiesIgnoreNull(etrBondRightDto, etrSinglePortfolioDto);
            etrBondRightDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrBondRightDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrBondRightDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrBondRightDto.class).entrustCheck(etrBondRightDto,result);
        }
        if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
            // 可转债申购指令
            EtrConvBondApplyDto etrConvBondApplyDto = new EtrConvBondApplyDto();
            BeanUtil.copyPropertiesIgnoreNull(etrConvBondApplyDto, etrSinglePortfolioDto);
            etrConvBondApplyDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
            etrConvBondApplyDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

            EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).updateSettings(paraDto);
            EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).entrustCheck(etrConvBondApplyDto,result);

        }
		if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债转股指令
			EtrBondToStockDto etrBondToStockDto = new EtrBondToStockDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondToStockDto, etrSinglePortfolioDto);
			etrBondToStockDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondToStockDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
			etrBondToStockDto.setExpConNum(entity.getToShareAvailableNum());// 可转股数量
			
			EtrComponentFactory.getComponentService(EtrBondToStockDto.class).updateSettings(paraDto);
	        EtrComponentFactory.getComponentService(EtrBondToStockDto.class).entrustCheck(etrBondToStockDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 标准券出入库指令
			EtrStandardBondInOutDto etrStandardBondInOutDto = new EtrStandardBondInOutDto();
			BeanUtil.copyPropertiesIgnoreNull(etrStandardBondInOutDto, etrSinglePortfolioDto);
			// etrStandardBondInOutDto.setFinprodType2(dto.getFinprodType2()); //金融产品类型（投管）
			etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
			
			EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).updateSettings(paraDto);
	        EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).entrustCheck(etrStandardBondInOutDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债券质押式回购竞价指令
			EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
			etrBondRepoBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
			
			EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
	        EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustCheck(etrBondRepoBiddingDto, result);
		}
        return result;
    }

    /***
     * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
     * @return
     */
    public String getInvAim(){
        return this.etrExchangeTradeMapper.getInvAim();
    }

    public String linkInvAim(Map<String, Object> param) {
        String invAim = this.etrExchangeTradeMapper.linkInvAim(param);
        if(!StringUtil.isNotEmpty(invAim)) {
            invAim = this.etrExchangeTradeMapper.linkInvAimEtr(param);
        }
        return invAim;
    }

    /**
     * 查询指令信息
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrExchangeTrade001Dto getEtrExchangeTradeInfo(String entrustId) throws Exception{
        EtrExchangeTrade001Dto etrExchangeTrade001Dto = new EtrExchangeTrade001Dto();
        //通过指令ID获取委托指令表的DTO,获取委托类型
        EtrEntrust etrEntrust =  etrExchangeTradeMapper.selectEntrustByKey(entrustId);
        if(StringUtil.isNotEmpty(etrEntrust)) {
            //股票竞价指令
            if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue())) {
                EtrStockBiddingDto etrStockBiddingDto = EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrStockBiddingDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrStockBiddingDto);
                    etrExchangeTrade001Dto.setAcctNo(etrStockBiddingDto.getDealCurAcct());// 资金账户
                }
            }
            //基金竞价指令
            else if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue())) {
                EtrFundBiddingDto etrFundBiddingDto = EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrFundBiddingDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrFundBiddingDto);
                    etrExchangeTrade001Dto.setAcctNo(etrFundBiddingDto.getDealCurAcct());// 资金账户
                }
            }
            //大宗债券质押式回购指令
            else if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue())) {
                EtrBlockTradePledgeRepoDto etrBlockTradePledgeRepoDto =  EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrBlockTradePledgeRepoDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrBlockTradePledgeRepoDto);
                    etrExchangeTrade001Dto.setSeatId(etrBlockTradePledgeRepoDto.getCounterSeat());//席位号
                    etrExchangeTrade001Dto.setAgreeOnId(etrBlockTradePledgeRepoDto.getCounterConNo());//约定号
                    etrExchangeTrade001Dto.setAcctNo(etrBlockTradePledgeRepoDto.getDealCurAcct());// 资金账户
                    etrExchangeTrade001Dto.setPrice(ArithmeticUtil.multiply(etrBlockTradePledgeRepoDto.getPrice(),new BigDecimal(100)));
                }
            }
            //大宗股票指令
            else if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue())) {
                EtrBlockTradeStockDto etrBlockTradeStockDto =  EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrBlockTradeStockDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrBlockTradeStockDto);
                    etrExchangeTrade001Dto.setSeatId(etrBlockTradeStockDto.getCounterSeat());//席位号
                    etrExchangeTrade001Dto.setAgreeOnId(etrBlockTradeStockDto.getCounterConNo());//约定号
                    etrExchangeTrade001Dto.setAcctNo(etrBlockTradeStockDto.getDealCurAcct());// 资金账户
                }
            }
            //大宗基金指令
            else if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue())) {
                EtrBlockTradeFundDto etrBlockTradeFundDto =  EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrBlockTradeFundDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrBlockTradeFundDto);
                    etrExchangeTrade001Dto.setSeatId(etrBlockTradeFundDto.getCounterSeat());//席位号
                    etrExchangeTrade001Dto.setAgreeOnId(etrBlockTradeFundDto.getCounterConNo());//约定号
                    etrExchangeTrade001Dto.setAcctNo(etrBlockTradeFundDto.getDealCurAcct());// 资金账户
                }
            }

            //大宗债券指令
            else if(StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue())) {
                EtrBlockTradeBondDto etrBlockTradeBondDto = EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrBlockTradeBondDto)) {
                    BeanUtil.copyProperties(etrExchangeTrade001Dto, etrBlockTradeBondDto);
                    etrExchangeTrade001Dto.setSeatId(etrBlockTradeBondDto.getCounterSeat());//席位号
                    etrExchangeTrade001Dto.setAgreeOnId(etrBlockTradeBondDto.getCounterConNo());//约定号
                    etrExchangeTrade001Dto.setYield(etrBlockTradeBondDto.getMatureYield().multiply(new BigDecimal(100)));//收益率
                    etrExchangeTrade001Dto.setFprice(etrBlockTradeBondDto.getFullPrice());//全价
                    etrExchangeTrade001Dto.setCalculateField(etrBlockTradeBondDto.getCalMode());// 计算方式
                    etrExchangeTrade001Dto.setAcctNo(etrBlockTradeBondDto.getDealCurAcct());// 资金账户
                }

            }
            //债券竞价指令
            else if(DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrBondBiddingDto etrBondBiddingDto = EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustSelect(entrustId);
                if(StringUtil.isNotEmpty(etrBondBiddingDto)){
                    BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto,etrBondBiddingDto);
                    etrExchangeTrade001Dto.setYield(etrBondBiddingDto.getMatureYield().multiply(new BigDecimal(100))); // 到期收益率
                    etrExchangeTrade001Dto.setFprice(etrBondBiddingDto.getFullPrice()); // 全价
                    etrExchangeTrade001Dto.setCalculateField(etrBondBiddingDto.getCalMode());// 计算方式
                    etrExchangeTrade001Dto.setFinprodType2(etrBondBiddingDto.getFinprodType2());// 金融产品类型（投管）
                }
            }
            // 上交所固定收益平台现券交易指令
            else if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrBondFxdIncmPlatSHDto etrBondFxdIncmPlatSHDto = EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondFxdIncmPlatSHDto);
                etrExchangeTrade001Dto.setYield(etrBondFxdIncmPlatSHDto.getMatureYield().multiply(new BigDecimal(100))); // 到期收益率
                etrExchangeTrade001Dto.setFprice(etrBondFxdIncmPlatSHDto.getFullPrice()); // 全价
                etrExchangeTrade001Dto.setCounterId(etrBondFxdIncmPlatSHDto.getCounterId());// 交易对手
                etrExchangeTrade001Dto.setTrader(etrBondFxdIncmPlatSHDto.getTrader());// 本方交易员
                etrExchangeTrade001Dto.setCounterTrader(etrBondFxdIncmPlatSHDto.getCounterTrader());// 对手方交易员
                etrExchangeTrade001Dto.setCounterOrg(etrBondFxdIncmPlatSHDto.getCounterOrg());// 对手方交易商
                etrExchangeTrade001Dto.setAgreeOnId(etrBondFxdIncmPlatSHDto.getCounterConNo());//约定号
                etrExchangeTrade001Dto.setCalculateField(etrBondFxdIncmPlatSHDto.getCalMode());// 计算方式
                etrExchangeTrade001Dto.setFinprodType2(etrBondFxdIncmPlatSHDto.getFinprodType2());// 金融产品类型（投管）
                if (etrBondFxdIncmPlatSHDto.getSettleSpreed() != null) {
                    if (etrBondFxdIncmPlatSHDto.getSettleSpreed() == 0) {
                        etrExchangeTrade001Dto.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());// 清算速度
                    } else if (etrBondFxdIncmPlatSHDto.getSettleSpreed() == 1) {
                        etrExchangeTrade001Dto.setSettleSpeed(DictCodeList.清算速度_T_1.getValue());// 清算速度
                    }
                }
            }

            // 债券回售指令
            else if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrBondRepurchaseDto etrBondRepurchaseDto = EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto,etrBondRepurchaseDto);
                etrExchangeTrade001Dto.setFinprodType2(etrBondRepurchaseDto.getFinprodType2());// 金融产品类型（投管）
            }

            // 配股指令
            else if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrStockRightDto etrStockRightDto = EtrComponentFactory.getComponentService(EtrStockRightDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrStockRightDto);
                etrExchangeTrade001Dto.setFinprodType2(etrStockRightDto.getFinprodType2());// 金融产品类型（投管）
            }
            // 新股申购指令
            else if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrStockNewDto etrStockNewDto = EtrComponentFactory.getComponentService(EtrStockNewDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrStockNewDto);
                etrExchangeTrade001Dto.setFinprodType2(etrStockNewDto.getFinprodType2());// 金融产品类型（投管）
            }

            // 配债指令
            else if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrBondRightDto etrBondRightDto = EtrComponentFactory.getComponentService(EtrBondRightDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondRightDto);
                etrExchangeTrade001Dto.setFinprodType2(etrBondRightDto.getFinprodType2());// 金融产品类型（投管）
            }

            // 可转债申购指令
            else if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(etrEntrust.getEntrustType())) {
                EtrConvBondApplyDto etrConvBondApplyDto = EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrConvBondApplyDto);
                etrExchangeTrade001Dto.setFinprodType2(etrConvBondApplyDto.getFinprodType2());// 金融产品类型（投管）
            }
            
            // 债转股指令
            else if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(etrEntrust.getEntrustType())) {
            	EtrBondToStockDto etrBondToStockDto = EtrComponentFactory.getComponentService(EtrBondToStockDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondToStockDto);
                etrExchangeTrade001Dto.setFinprodType2(etrBondToStockDto.getFinprodType2());// 金融产品类型（投管）
                etrExchangeTrade001Dto.setToShareAvailableNum(etrBondToStockDto.getExpConNum());//债转股数量
            }
            
            // 标准券出入库指令
            else if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(etrEntrust.getEntrustType())) {
            	EtrStandardBondInOutDto etrStandardBondInOutDto = EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrStandardBondInOutDto);
                etrExchangeTrade001Dto.setPrice(etrStandardBondInOutDto.getPrice().multiply(new BigDecimal(100)));
                etrExchangeTrade001Dto.setFinprodType2(etrStandardBondInOutDto.getFinprodType2());// 金融产品类型（投管）
            }
            
            // 债券质押式回购竞价指令
            else if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(etrEntrust.getEntrustType())) {
            	EtrBondRepoBiddingDto etrBondRepoBiddingDto = EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustSelect(entrustId);
                BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondRepoBiddingDto);
                etrExchangeTrade001Dto.setPrice(etrBondRepoBiddingDto.getPrice().multiply(new BigDecimal(100)));
                etrExchangeTrade001Dto.setFinprodType2(etrBondRepoBiddingDto.getFinprodType2());// 金融产品类型（投管）
            }

            if(!StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue()) && !StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue()) && !StringUtil.equals(etrEntrust.getEntrustType(), DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue())) {
                etrExchangeTrade001Dto.setEntrustPrice(etrExchangeTrade001Dto.getPrice());//指令价格
                etrExchangeTrade001Dto.setPrice(null);//净价需要置空
            }

            etrExchangeTrade001Dto.setEntrustBussType(etrEntrust.getEntrustType());
            
            EtrEntrustAdd etrEntrustAdd = etrExchangeTradeMapper.selectEntrustAddByKey(etrEntrust.getEntrustId());
            if(null != etrEntrustAdd && StringUtils.isNotEmpty(etrEntrustAdd.getDealCurAcct())){
                etrExchangeTrade001Dto.setAcctNo(etrEntrustAdd.getDealCurAcct());
            }
            if (null != etrEntrustAdd && StringUtils.isNotEmpty(etrEntrustAdd.getDealMode())) {
                etrExchangeTrade001Dto.setDeclaType(etrEntrustAdd.getDealMode());
            }
        }
        return etrExchangeTrade001Dto;

    }

    /**
     * 对手方交易商联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterDealer(Map<String, String> map) {
        // 交易场所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        List<SuggestDto> suggestDtos = etrExchangeTradeMapper.selectCounterDealer(map);
        return suggestDtos;
    }

    /**
     * 对手方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterTrader(Map<String, String> map) {
        // 交易场所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        List<SuggestDto> suggestDtos = etrExchangeTradeMapper.selectCounterTrader(map);
        return suggestDtos;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectTrader(Map<String, String> map) {
        // 交易场所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        List<SuggestDto> suggestDtos = etrExchangeTradeMapper.selectTrader(map);
        return suggestDtos;
    }

    /**
     * 债券回售计划查询
     *
     * @param map
     *            检索参数
     * @return SelectOptionDto
     */
    public int selectBondExecPlan(Map<String, Object> map) {
        // 交易场所。上海证券交易所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        // 行权方式.回售
        map.put("EXEC_MODE_02", DictCodeList.行权方式_回售.getValue());
        // 现金流二级类型.本金
        map.put("CASH_TYPE_2_C01", DictCodeListExt1.现金流二级类型_本金.getValue());

        return etrExchangeTradeMapper.selectBondExecPlan(map);
    }

    /**
     * 资产自定义二级类型【页面二级类型】查询
     *
     * @param map
     *            检索参数
     * @return SelectOptionDto
     */
    public String getCustomerTypeTwo(Map<String, Object> map) {
        return etrExchangeTradeMapper.getCustomerTypeTwo(map);
    }

    /**
     * 查询上交所和深交所标准券数量
     *
     * @param pageParam 页面参数
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    public Map<String, String> loadSecNum(Map<String, Object> pageParam) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("secNum_sh", "0");
        resultMap.put("secNum_color_sh", "red");
        resultMap.put("secNum_sz", "0");
        resultMap.put("secNum_color_sz", "red");
        List<EtrExchangeTrade002Dto> dtoList = etrExchangeTradeMapper.selectSecNum(pageParam);
        for (EtrExchangeTrade002Dto dto : dtoList) {
            if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sh", format.format(dto.getSecNum()));
                if(dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sh", "green");
                }
            }
            else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                java.text.DecimalFormat format = new java.text.DecimalFormat("###,###,###");
                resultMap.put("secNum_sz", format.format(dto.getSecNum()));
                if(dto.getSecNum().compareTo(BigDecimal.ZERO) < 0) {
                    resultMap.put("secNum_color_sz", "green");
                }
            }
        }
        return resultMap;
    }

    public EtrDistribute getDistributeByEntrustId(Map<String, Object> map) {
		return etrExchangeTradeMapper.getDistributeByEntrustId(map);
	}
    
	/**
	 * 折算率查询
	 * 
	 * @param map
	 * @return
	 */
	public String selectStdsecConverseRatio(Map<String, Object> map) {
		map.put("date", iPlatformDate.getSysDate());
		return etrExchangeTradeMapper.selectStdsecConverseRatio(map);
	}

	/**
	 * 查询标准券可用持仓
	 * @param map
	 * @return
	 */
	public String queryStandandTicketCanUse(Map<String, Object> map) {
		map.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
		map.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
		map.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
		map.put("BROKER", DictCodeListExt3.所有_所有.getValue());
		return etrExchangeTradeMapper.queryStandandTicketCanUse(map);
	}

    public BaseEtrInvestInfoDto buildEtrFlow(EtrExchangeTrade001Dto dto) {
        BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
        EtrBaseDto baseDto = getEtrBaseDto(dto);
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        EtrEntrust etrEntrust = new EtrEntrust();
        EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
        if (StringUtil.isNotEmpty(dto.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(dto.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {
                etrEntrustAdd.setAssetType(finProductType.getType8());
            }else {
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        }
        // 股票竞价指令dto
        EtrStockBiddingDto etrStockBiddingDto = new EtrStockBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockBiddingDto, etrSinglePortfolioDto);
            etrStockBiddingDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrStockBiddingDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrStockBiddingDto);
        }

        // 基金竞价指令dto
        EtrFundBiddingDto etrFundBiddingDto = new EtrFundBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrFundBiddingDto, etrSinglePortfolioDto);
            etrFundBiddingDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrFundBiddingDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrFundBiddingDto);
        }

        // 大宗债券质押式回购指令dto
        EtrBlockTradePledgeRepoDto etrBlockTradePledgeRepoDto = new EtrBlockTradePledgeRepoDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradePledgeRepoDto, etrSinglePortfolioDto);
            etrBlockTradePledgeRepoDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradePledgeRepoDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradePledgeRepoDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradePledgeRepoDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            if (DictCodeListExt3.交易方向_正回购.getValue().equals(etrBlockTradePledgeRepoDto.getPs())) {
                etrBlockTradePledgeRepoDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
            } else {
                etrBlockTradePledgeRepoDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            }
            etrBlockTradePledgeRepoDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            //质押式回购 设置 AssetType1->平安资产类型_质押式回购
            etrBlockTradePledgeRepoDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBlockTradePledgeRepoDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBlockTradePledgeRepoDto);
        }

        // 大宗股票指令dto
        EtrBlockTradeStockDto etrBlockTradeStockDto = new EtrBlockTradeStockDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeStockDto, etrSinglePortfolioDto);
            etrBlockTradeStockDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeStockDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradeStockDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeStockDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBlockTradeStockDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBlockTradeStockDto);
        }

        // 大宗基金指令dto
        EtrBlockTradeFundDto etrBlockTradeFundDto = new EtrBlockTradeFundDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeFundDto, etrSinglePortfolioDto);
            etrBlockTradeFundDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeFundDto.setCounterSeat(dto.getSeatId());// 席位号
            etrBlockTradeFundDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeFundDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBlockTradeFundDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBlockTradeFundDto);
        }

        // 大宗债券指令dto
        EtrBlockTradeBondDto etrBlockTradeBondDto = new EtrBlockTradeBondDto();
        if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBlockTradeBondDto, etrSinglePortfolioDto);
            etrBlockTradeBondDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8));// 收益率
            etrBlockTradeBondDto.setFullPrice(dto.getFprice());// 全价
            etrBlockTradeBondDto.setCounterId(dto.getCounterId());// 交易对手
            etrBlockTradeBondDto.setCounterSeat(dto.getSeatId()); // 席位号
            etrBlockTradeBondDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBlockTradeBondDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBlockTradeBondDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBlockTradeBondDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBlockTradeBondDto);
        }

        // 债券竞价指令
        EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
            etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondBiddingDto.setFullPrice(dto.getFprice()); // 全价
            etrBondBiddingDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondBiddingDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondBiddingDto);
        }

        // 上交所固定收益平台现券交易指令
        EtrBondFxdIncmPlatSHDto etrBondFxdIncmPlatSHDto = new EtrBondFxdIncmPlatSHDto();
        if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondFxdIncmPlatSHDto, etrSinglePortfolioDto);
            etrBondFxdIncmPlatSHDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(),new BigDecimal(100),8)); // 到期收益率
            etrBondFxdIncmPlatSHDto.setFullPrice(dto.getFprice()); // 全价
            etrBondFxdIncmPlatSHDto.setCounterId(dto.getCounterId());// 交易对手
            etrBondFxdIncmPlatSHDto.setTrader(dto.getTrader());// 本方交易员
            etrBondFxdIncmPlatSHDto.setCounterTrader(dto.getCounterTrader());// 对手方交易员
            etrBondFxdIncmPlatSHDto.setCounterOrg(dto.getCounterOrg());// 对手方交易商
            etrBondFxdIncmPlatSHDto.setCounterConNo(dto.getAgreeOnId());// 约定号
            etrBondFxdIncmPlatSHDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondFxdIncmPlatSHDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondFxdIncmPlatSHDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            // TISHKS-1639:清算速度赋值有误 modify by fuyongsheng 20220810 start
            // etrBondFxdIncmPlatSHDto.setSettleSpreed(StringUtil.isNotEmpty(dto.getSettleSpeed())?Long.parseLong(dto.getSettleSpeed()):0L);//清算速度
            if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_0.getValue())) {
                etrBondFxdIncmPlatSHDto.setSettleSpreed(0L);
            } else if (StringUtil.equals(dto.getSettleSpeed(), DictCodeList.清算速度_T_1.getValue())) {
                etrBondFxdIncmPlatSHDto.setSettleSpreed(1L);
            }
            // TISHKS-1639:清算速度赋值有误 modify by fuyongsheng 20220810 end
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondFxdIncmPlatSHDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondFxdIncmPlatSHDto);
        }

        // 债券回售指令
        EtrBondRepurchaseDto etrBondRepurchaseDto = new EtrBondRepurchaseDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepurchaseDto, etrSinglePortfolioDto);
            etrBondRepurchaseDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRepurchaseDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondRepurchaseDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondRepurchaseDto);
        }

        // 配股指令
        EtrStockRightDto etrStockRightDto = new EtrStockRightDto();
        if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockRightDto, etrSinglePortfolioDto);
            etrStockRightDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrStockRightDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrStockRightDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrStockRightDto);
        }

        // 新股申购指令
        EtrStockNewDto etrStockNewDto = new EtrStockNewDto();
        if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrStockNewDto, etrSinglePortfolioDto);
            etrStockNewDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrStockNewDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrStockNewDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrStockNewDto);
        }

        // 配债指令
        EtrBondRightDto etrBondRightDto = new EtrBondRightDto();
        if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondRightDto, etrSinglePortfolioDto);
            etrBondRightDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRightDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondRightDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondRightDto);
        }

        // 可转债申购指令
        EtrConvBondApplyDto etrConvBondApplyDto = new EtrConvBondApplyDto();
        if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrConvBondApplyDto, etrSinglePortfolioDto);
            etrConvBondApplyDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrConvBondApplyDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrConvBondApplyDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrConvBondApplyDto);
        }

        // 债转股指令
        EtrBondToStockDto etrBondToStockDto = new EtrBondToStockDto();
        if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondToStockDto, etrSinglePortfolioDto);
            etrBondToStockDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondToStockDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            etrBondToStockDto.setExpConNum(dto.getToShareAvailableNum());//可转股数量
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondToStockDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondToStockDto);
        }

        // 标准券出入库指令
        EtrStandardBondInOutDto etrStandardBondInOutDto = new EtrStandardBondInOutDto();
        if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(baseDto.getEntrustType())) {
            etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            BeanUtil.copyPropertiesIgnoreNull(etrStandardBondInOutDto, etrSinglePortfolioDto);
            //etrStandardBondInOutDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            if (etrStandardBondInOutDto.getPs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())) {
                etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            } else if (etrStandardBondInOutDto.getPs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())) {
                etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
            }
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrStandardBondInOutDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrStandardBondInOutDto);
        }

        // 债券质押式回购竞价指令
        EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(baseDto.getEntrustType())) {
            etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(),new BigDecimal(100),6));
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
            etrBondRepoBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
            // 质押式回购 设置 AssetType1->平安资产类型_质押式回购
            etrBondRepoBiddingDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondRepoBiddingDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondRepoBiddingDto);
        }
        entity.setEtrEntrust(etrEntrust);
        entity.setEtrEntrustAdd(etrEntrustAdd);
        return entity;
    }

    public BaseEtrInvestInfoDto buildMultiEtrFlow(EtrExchangeTradeMulti001Dto dto) {
	    dto.setSecAcctId(dto.getTradeMultiPtl001Dtos().get(0).getSecAcctId());
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildEtrFlow(dto);

        EtrEntrust etrEntrust = baseEtrInvestInfoDto.getEtrEntrust();
        EtrEntrustAdd etrEntrustAdd = baseEtrInvestInfoDto.getEtrEntrustAdd();
        etrEntrustAdd.setWeighType(dto.getWeightType());
        etrEntrustAdd.setMulDistriClass(dto.getMulDistriClass());
        etrEntrustAdd.setMulTarget(dto.getTargetValue());
        etrEntrustAdd.setMulUnit(dto.getMulTargetUnit());

        List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
        baseEtrInvestInfoDto.setEtrEntrustDetailList(etrEntrustDetailList);
        for(EtrExchangeTradeMultiPtl001Dto item : dto.getTradeMultiPtl001Dtos()) {
            EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
            etrEntrustDetail.setPortfolioId(item.getPortfolioId());
            etrEntrustDetail.setEntrustId(etrEntrust.getEntrustId());
            etrEntrustDetail.setEntrustAmt(item.getEntrustAmt());
            etrEntrustDetail.setEntrustNum(item.getEntrustNum());
            etrEntrustDetail.setDisRate(ArithmeticUtil.divide(item.getProportion(),new BigDecimal(100),4));
            etrEntrustDetail.setTargetType(dto.getTargetType());
            etrEntrustDetailList.add(etrEntrustDetail);
        }
        return baseEtrInvestInfoDto;
    }

	/**
	 * 每日债券估值市场行情价格查询
	 * 
	 * @param map
	 * @return
	 */
	public String qryMstBondMarketPrice(Map<String, Object> map) {
		String mstBondMarketPrice = etrExchangeTradeMapper.qryMstBondMarketInfo(map);
		if (StringUtils.isEmpty(mstBondMarketPrice)) {
			mstBondMarketPrice = etrExchangeTradeMapper.qryInterfBondMarketInfo(map);
		}
		return mstBondMarketPrice;
	}
	
	/**
	 * 每日债券估值市场行情价格查询
	 * 
	 * @param map
	 * @return
	 */
	public String qryMstFundMarketPrice(Map<String, Object> map) {
		String mstBondMarketPrice = etrExchangeTradeMapper.qryMstFundMarketInfo(map);
		if (StringUtils.isEmpty(mstBondMarketPrice)) {
			mstBondMarketPrice = etrExchangeTradeMapper.qryInterfFundMarketInfo(map);
		}
		return mstBondMarketPrice;
	}
	
	/**
	 * 每日债券估值市场行情价格查询
	 * 
	 * @param map
	 * @return
	 */
	public String qryMstShareMarketPrice(Map<String, Object> map) {
		String mstBondMarketPrice = etrExchangeTradeMapper.qryMstShareMarketInfo(map);
		if (StringUtils.isEmpty(mstBondMarketPrice)) {
			mstBondMarketPrice = etrExchangeTradeMapper.qryInterfShareMarketInfo(map);
		}
		return mstBondMarketPrice;
	}
	/**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by zhangpei 
	 * 20201128
	 * @param dto
	 */
	public void doInsetOrUpdateForEtrPositionHold(EtrExchangeTrade001Dto dto){
		// 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
		// 指令持仓流水 、现金流水处理
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();// 组合持仓流水、组合现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();// 现金流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
        EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合现金流水DTO
        if (DictCodeList.交易方向_卖出.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeList.交易方向_行权_回售.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt1.交易方向_债转股.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_质押券入库.getValue().equals(etrSinglePortfolioDto.getPs())
            || DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {

            /********************** 持仓流水DTO构造 *************************************/
            etrSecPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrSecPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(etrSinglePortfolioDto.getSecAcctId());// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId(etrSinglePortfolioDto.getFinprodId());// 金融产品代码
            if (DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs()) && (DictCodeList.交易平台_竞价撮合平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
            		|| DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
            		|| DictCodeList.交易平台_综合协议交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat()))) {
                etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");
            }else if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {
            	etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");
            }
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(etrSinglePortfolioDto.getInvAim()) ? etrSinglePortfolioDto.getInvAim() : "999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
			if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrSinglePortfolioDto.getPs())) {
				etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 标准券数量
			} else {
				etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustNum().multiply(new BigDecimal(-1)));// 数量
			}
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(etrSinglePortfolioDto.getEntrustId());// 指令编号
            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
            /*************************************************************************/
        } else if (DictCodeList.交易方向_买入.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt3.交易方向_逆回购.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt2.交易方向_配股.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeList.交易方向_申购.getValue().equals(etrSinglePortfolioDto.getPs())
                   || DictCodeListExt3.交易方向_配债.getValue().equals(etrSinglePortfolioDto.getPs())) {
            /********************** 持仓流水DTO构造 *************************************/
            etrCashPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrCashPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrCashPositionFlowDtoUnit.setAcctId(etrSinglePortfolioDto.getDealCurAcct());// 资金账户
            etrCashPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrCashPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrCashPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            // if 画面.全价不为空，则数量 = 画面.全价*画面.数量 else 投资指令DTO.指令金额
            if (null != dto.getFprice()) {
                etrCashPositionFlowDtoUnit.setAmount(dto.getEntrustNum().multiply(dto.getFprice()).multiply(new BigDecimal(-1)));
            } else {
                etrCashPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 数量
            }
            etrCashPositionFlowDtoUnit.setOrderId(etrSinglePortfolioDto.getEntrustId());// 业务编号
            etrCashPositionFlowDtoUnit.setChlFinprodId(null);// 通道金融产品代码
            etrCashPositionFlowDtoUnit.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());// 冲正标识
            etrCashPositionFlowDtoUnit.setOffsetId(etrSinglePortfolioDto.getEntrustId());// 冲销业务编号
            /*************************************************************************/
            etrCashPositionFlows.add(etrCashPositionFlowDtoUnit);
        }

        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setDealEtr(true);
        etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache()));
        etrPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> etrSecPositionFlowDto.setCdate(DateUtil.platDateFromCache()));
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
	}

    /**
     *
     * @Description //金融产品及其他信息查询
     * @Param [map]
     * @return java.util.List<com.joyintech.service.entrust.etrexchangetrade.dto.FinProductOthCode>
     * @version 1.0
     * @see
     **/
	public List<FinProductOthCodeDto> getFinprodOthCodeInfo(Map<String,Object> map){
	    return etrExchangeTradeMapper.getFinprodOthCodeInfo(map);
    }

    /**
     *
     * @Description //权益额度查询
     * @Param [map]
     * @return java.util.List<com.joyintech.tams.common.base.entity.FinProductQuto>
     * @version 1.0
     * @see
     **/

    public List<FinProductQuto> getFinProductQutoInfo(Map<String,Object> map){
        return etrExchangeTradeMapper.getFinProductQutoInfo(map);
    }

    /**
     *
     * @Description //已认购权益
     * @Param [map]
     * @return java.util.List<com.joyintech.tams.common.base.entity.EtrEntrust>
     * @version 1.0
     * @see
    **/

    public List<SubEqutityDto> getsubEqutity(Map<String,Object> map){
        map.put("entrustStatusClose",DictCodeList.委托状态_已关闭.getValue());
        return etrExchangeTradeMapper.getsubEqutity(map);
    }

    public List<WflEtrEntrust> selectWflEntrust(Map<String, Object> map) {
        map.put("ps_T04",DictCodeList.交易方向_申购.getValue());
        return etrExchangeTradeMapper.selectWflEntrust(map);
    }

    public List<String> selectWflEntrustAddAndEtrEntrustAdd(Map<String, Object> map) {
        return etrExchangeTradeMapper.selectWflEntrustAddAndEtrEntrustAdd(map);
    }

    //add by chenjiaxing 债券竞价交易展示偏移度 start
    /**
     * 1.6计算偏离度
     *
     * @param
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public String calcDeviationDegree(Map<String, Object> param) {
        // 参数.资产代码(市场代码)
        String 市场代码 = (String) param.get("finprodMarketId");
        // 参数.净价
        String 净价 = (String) param.get("unitCprice");
        if (StringUtil.isBlank(市场代码) || StringUtil.isBlank(净价)) {
            return null;
        }

        String sysDate = (String) param.get("sysDate");
        param.put("sysDate", DateUtil.parse(sysDate));

        // 查询债券登记托管机构
        FinProduct finProduct = finProductMapper.selectByPrimaryKey((String) param.get("finprodId"));
        if (finProduct != null) {
            if (StringUtil.equals(DictCodeList.登记托管机构_中债_中央国债登记结算有限责任公司.getValue(),
                    finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
            }
            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_上海清算所.getValue(),
                    finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_上清所_SCH.getValue());
            }
            else if (StringUtil.equals(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue(),
                    finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中证登_CSD.getValue());
            }
            else {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
            }
        }
        else {
            param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
        }

        // 查询上一估值日的净价
        BigDecimal 上一估值日净价 = etrExchangeTradeMapper.getLastNetPrice(param);
        if (上一估值日净价 == null || BigDecimal.ZERO.compareTo(上一估值日净价) == 0) {
            return "0%";
        }

        try {
            BigDecimal 所输净价 = new BigDecimal(净价);

            // 计算公式：（所输净价-上一估值净价）/上一估值净价*100%
            BigDecimal result = ArithmeticUtil.subtract(所输净价, 上一估值日净价);
            result = ArithmeticUtil.multiply(result, new BigDecimal(100));
            result = ArithmeticUtil.divide(result, 上一估值日净价, 0);

            return String.valueOf(result) + "%";
        }
        catch (Exception e) {
        }

        return null;
    }
    //add by chenjiaxing 债券竞价交易展示偏离度 end

    // add by etliu 20211207 start
    // 交易所债券非交易类业务迁移
    /**
     *
     * @Description //查询行权回售数据
     * @Param [map]
     * @return 行权回售数据
     * @version 1.0
     * @see
     **/
    public List<FinExercisePlan> selectFinExercisePlanData(Map<String, Object> map){
        map.put("exerciseType", DictCodeList.行权方式_回售.getValue());
        if (map.containsKey("sysDate")) {
            map.put("sysDate", DateUtil.parse((String) map.get("sysDate")));
        }

        return etrExchangeTradeMapper.selectFinExercisePlanData(map);
    }
    // add by etliu 20211207 end

    /**
     * 查询多组合名称下拉框
     * @param map
     * @return
     */
    public Page<SelectOptionDto> selectPortfolioTreeId(Map<String,Object> map, int page, int length) {
        return super.queryForPageForSelectReturn(page,length,()->etrExchangeTradeMapper.selectPortfolioTreeId(map));
    }

    /**
     * 查询多组合树关联的组合
     * @param map
     * @return
     */
    public List<SelectOptionDto> selectPortfolioId(Map<String,Object> map){
        return etrExchangeTradeMapper.selectPortfolioId(map);
    }

    /**
     * 查询多组合明细
     * @param map
     * @return
     */
    public List<EtrExchangeTradeMultiPtl001Dto> selectPortfolioTreeDetail(Map<String,Object> map) {
        map.put("PS_T100",DictCodeListExt3.交易方向_调整到.getValue());
        List<EtrExchangeTradeMultiPtl001Dto> list = etrExchangeTradeMapper.selectPortfolioTreeDetail(map);
        return list;
    }
    /**
     * 获取多组合分配资金账户
     * @param paraMap
     * @return
     */
    public Page<SelectOptionDto> selectMultiAcctNo(Map<String,Object> paraMap, int page, int length) {
        return super.queryForPageForSelectReturn(page,length,()->etrExchangeTradeMapper.selectMultiAcctNo(paraMap));
    }

    /**
     * 查询组合+资金账户+交易场所对应的证券账户（多组合）
     * @param paraMap
     * @return
     */
    public String selectMultiSecAcctId(Map<String,Object> paraMap) {
        return etrExchangeTradeMapper.selectMultiSecAcctId(paraMap);
    }

    /**
     * 查询多组合详情
     * @param param
     * @return
     */
    public EtrExchangeTradeMulti001Dto selectMultDetail(Map<String, Object> param){
        param.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        param.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        param.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        param.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        return etrExchangeTradeMapper.selectMultDetail(param);
    }

}
