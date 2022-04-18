/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.*;
import com.joyintech.background.component.order.exchange.common.constant.UnitEnum;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.dto.FinStockInfoDto;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.CalcDeviationDegreeRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeMiniLimitPriceRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.PsOptionsRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SelectOptionRequestDto;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dao.EtrExchangeTradeSecRepoMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeOperateSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeQuotationHqSecRepoResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeRiskcalSecRepoDto;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.position.service.PositionRequestServiceEtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;
import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.*;

/**
 * 交易所指令交易（现券+通用质押式回购）
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeSecRepo001Service
 * @since 1.0
 */
@Service("etrExchangeTradeSecRepo001Service")
public class EtrExchangeTradeSecRepo001Service extends CommonExportService<EtrExchangeTradeSaveSecRepoRequest>
        implements BaseProcessService<EtrExchangeTradeSaveSecRepoRequest> {

    public static String queryIntegratedTick = "/joyin/quote/tick";
    /**
     * 指令共通Service
     */
    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    EtrExchangeTradeSecRepo002Service etrExchangeTrade002Service;

    @Autowired
    FinProductAddMapper finProductAddMapper;

    @Autowired
    FinProductMapper finProductMapper;

    @Autowired
    EtrExchangeTradeSecRepoMapper etrExchangeTradeSecRepoMapper;
    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

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
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;
    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    @Autowired
    private PositionRequestServiceEtrExchangeTradeSaveSecRepoRequest positionRequestService;

    public static final String ENTRUST_RECORD_MODIFY = "modify";

    public static final String ENTRUST_RECORD_NEW = "new";

    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrComponentCommService<EtrBlockTradeStockDto> componentService;

    /**
     * 委托指令共通
     */
    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;
    /**
     * 业务编号组件
     */
    @Autowired
    private BaseGetBusinessId bussGetBussinessIdService;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    /**
     * 指令废弃表
     */
    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;

    @Autowired
    private EtrEntrustDetailBackUpMapper etrEntrustDetailBackUpMapper;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(EtrExchangeTradeSaveSecRepoRequest dto, OperationType arg1) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<>();
        // 新增交易对手席位
        if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getSeatId())) {
            map.put("counterId", dto.getCounterId());
            map.put("counterSeatId", dto.getSeatId());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
                    Object.class);
        }
        // 新增交易对手交易员
        if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getCounterTrader())) {
            map.put("counterId", dto.getCounterId());
            map.put("counterTraderId", dto.getCounterTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
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
            } else {
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
        if (StringUtil.equals("AC", dto.getBusinessModel())) {
            dto.setInvAim("AC");
            etrEntrust.setInvAim("AC");
        } else if (StringUtil.equals("FVTPL", dto.getBusinessModel())) {
            dto.setInvAim("FVTPL");
            etrEntrust.setInvAim("FVTPL");
        }
        // 自动分发
        List<EtrDistribute> etrDistributeList = new ArrayList<>();
        if (busiAutoEtrDispService != null) {
            // 检测是否设置交易员
            if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                // 跳出自动分发
                etrEntrust.setEntrustType(etrSinglePortfolioDto.getEntrustType());
                entity.setEtrDistributeList(etrDistributeList);
                isPushMsg = DictCodeList.是否_是.getValue();
            } else {
                // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                // 设置指令日期 add by yufei 20200808 start
                if (etrEntrust.getEntrustDate() == null) {
                    etrEntrust.setEntrustDate(iPlatformDate.getSysDate());
                }
                // 设置指令日期 add by yufei 20200808 end
                if (busiAutoEtrDispService.checkEntrustTime(etrEntrust)) {
                    // 自动分发
                    etrEntrust.setEntrustType(etrSinglePortfolioDto.getEntrustType());
                    entity.setEtrDistributeList(busiAutoEtrDispService.selectTrader(etrEntrust, etrEntrustAdd));
                    isPushMsg = DictCodeList.是否_是.getValue();
                }
            }
        }
        if (StringUtil.isEmptyList(entity.getEtrDistributeList())) {//无分发人员
            baseDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
            etrSinglePortfolioDto.setEntrustStatus(DictCodeListExt3.委托状态_待分发.getValue());// 指令状态
        }

        // 债券现券交易指令
        EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(baseDto.getEntrustType())) {
            BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
            etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100), 8)); // 到期收益率
            etrBondBiddingDto.setFullPrice(dto.getFprice()); // 全价
            etrBondBiddingDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            etrBondBiddingDto.setBusiTypePosition(DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue());
        }

        // 通用式质押回购指令
        EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
        if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
            etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(), new BigDecimal(100), 6));
            BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
            etrBondRepoBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
            etrBondRepoBiddingDto.setBusiTypePosition(DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue());
        }

        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);

        if (null != dto.getEntrustId() && null != etrEntrustMapper.selectByPrimaryKey(dto.getEntrustId())) {
            dto.setOptType(ENTRUST_RECORD_MODIFY);
        } else {
            dto.setOptType(ENTRUST_RECORD_NEW);
        }
        if (StringUtil.equals(dto.getOptType(), ENTRUST_RECORD_NEW)) {//
            if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustNew(etrBondBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
                //质押式回购 设置 AssetType1->平安资产类型_质押式回购
                etrBondRepoBiddingDto.setAssetType1(DictCodeList.回购类型_质押式竞价回购.getValue());
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustNew(etrBondRepoBiddingDto);
            }
        } else {
            if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustEdit(etrBondBiddingDto);
            } else if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustEdit(etrBondRepoBiddingDto);
            }
        }
        doDeleteFlow(dto.getWorkflowId());
        //插入指令明细表
        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildMultiEtrFlow(dto);
        if (!StringUtil.isEmptyList(baseEtrInvestInfoDto.getEtrEntrustDetailList())) {
            etrEntrustDetailMapper.deleteByEntrustId(baseEtrInvestInfoDto.getEtrEntrustDetailList().get(0).getEntrustId());
            for (EtrEntrustDetail etrEntrustDetail : baseEtrInvestInfoDto.getEtrEntrustDetailList()) {
                etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
            }
        }
        // 自动分发数据入库 add by yufei 20200808 start
        if (!StringUtil.isEmptyList(entity.getEtrDistributeList())) {
            baseEtrInvestInfoService.distributeDelegate(entity.getEtrDistributeList());
        }
        // 自动分发数据入库 add by yufei 20200808 end
        // add by tanxuan on 20200514 指令分发推送信息到交易系统
        if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", dto.getEntrustId());
            etrCommandDispatcherService.pushCommandMsg(param);
        }
        //处理履历
        updateCfetsOprHis(dto.getEntrustId(), dto.getOptType());
    }

    /**
     * 配置用户
     */
    public void setUser(EtrParameterDto paraDto) {
        User user = new User();
        user.setUserName(LoginUserUtil.getUserName());
        user.setDeptCode(LoginUserUtil.getDeptCode());
        paraDto.setUser(user);
    }

    /**
     * 构造单组合单资产DTO
     *
     * @param dto
     * @return
     */
    public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrExchangeTradeSaveSecRepoRequest dto) {
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
        etrSinglePortfolioDto.setBusinessModel(dto.getBusinessModel());
        /***********************************************************************/
        etrSinglePortfolioDto.setFinprodType2(dto.getFinprodType2());// 金融产品分类
        etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
        /***********************************************************************/
        etrSinglePortfolioDto.setDealMode(dto.getDealMode());//交易方式
        etrSinglePortfolioDto.setPriceType(dto.getPriceType());//计价方式
        // 构造单组合单资产DTO end
        return etrSinglePortfolioDto;
    }

    @Override
    public void doBack(EtrExchangeTradeSaveSecRepoRequest entity) {
        doDeleteEtrFlowMethod(entity.getEntrustId());
        //从废弃表还原
        backReRoll(entity.getEntrustId());
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
        AllTradeCommonUtil.deletePosition(null, entity.getEntrustId());
        //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
    }

    @Override
    public void doUpdate(EtrExchangeTradeSaveSecRepoRequest entity) {
        // nothing to do just for aspect
        Map<String, Object> map = new HashMap<>();
        // 新增交易对手席位
        if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getSeatId())) {
            map.put("counterId", entity.getCounterId());
            map.put("counterSeatId", entity.getSeatId());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
                    Object.class);
        }
        // 新增交易对手交易员
        if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getCounterTrader())) {
            map.put("counterId", entity.getCounterId());
            map.put("counterTraderId", entity.getCounterTrader());
            restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
                    Object.class);
        }
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            //退回的移到备份表
            doInsertBackupQuery(entity);
            // 先删除流程中数据
            doDeleteFlow(entity.getWorkflowId());
            // 重新insert
            doInsertFlow(entity);
            //add by zznn 20210824 start
            //保存不需要处理头寸
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
                doInsetOrUpdateForEtrPositionHoldMulti(entity);
                //产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
            }
            //add by zznn 20210824 start
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrExchangeTradeSaveSecRepoRequest.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //保存不需要处理头寸
            if (!StringUtil.equals(entity.getOperateType(), "save")) {
                //增加调用头寸引擎代码start
                String decisionAct = 系统动作_修改.getValue();
                String systemAct = 系统动作_修改.getValue();
                EtrEntrust etrEntrust = etrEntrustBackupMapper.selectByPrimaryKey(entity.getEntrustId());
                SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                    if (etrEntrust == null) {
                        systemAct = 系统动作_新建.getValue();
                        decisionAct = 系统动作_修改.getValue();
                    }
                } else {
                    if (etrEntrust == null) {
                        systemAct = 系统动作_新建.getValue();
                        decisionAct = 系统动作_新建.getValue();
                    }
                }
                if (StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                    entity.setSoftSkipMode("Y");
                }
                try {
                    entity.setPositionSeqno(null);
                    positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct);
                } catch (PositionException e) {
                    Message message = PositionUtil.ajaxErrorReturn(e);
                    if (StringUtil.isNotBlank(message.getMessage())) {
                        WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                    } else {
                        if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                            BaseMap bm = (BaseMap) message.getData();
                            StringBuffer messageStr = (StringBuffer) bm.get("message");
                            WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                        }
                    }
                }
                //增加调用头寸引擎代码end
            }
        }
    }

    /**
     * 构造投资指令DTO
     *
     * @param dto
     * @return
     */
    public EtrBaseDto getEtrBaseDto(EtrExchangeTradeSaveSecRepoRequest dto) {
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
        if (StringUtil.equals(dto.getIsMulti(), DictCodeList.是否_是.getValue())) {
            baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
        } else {
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
     * 流程判断
     */
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        EtrExchangeTradeSaveSecRepoRequest dto = this.getBussinessEntity(entity);
        CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
        String 平安资产类型 = null;
        String finprodId = dto.getFinprodId();
        if (StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
            平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
        } else {
            平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
            finprodId = null;
        }

        if (taskId != null) {
            try {
                Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
                        平安资产类型, "", dto.getTradeMarket(), dto.getPs()
                        , finprodId, entity.getWorkflowId()
                );
                params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
                return commonBusinessService.是否需要上级审批_委托用(params);
            } catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        return null;
    }

    @Override
    public void doInsert(EtrExchangeTradeSaveSecRepoRequest entity, OperationType type) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        //退回的移到备份表
        doInsertBackupQuery(entity);

        doInsertFlow(entity);
        //插入和修改时，指令持仓流水 、现金流水处理,支持多组合
        doInsetOrUpdateForEtrPositionHoldMulti(entity);
        //增加调用头寸引擎代码start
        String decisionAct = 系统动作_修改.getValue();
        String systemAct = 系统动作_修改.getValue();
        if (ENTRUST_RECORD_NEW.equals(entity.getOptType())) {
            systemAct = 系统动作_新建.getValue();
            decisionAct = 系统动作_新建.getValue();
        }
        positionRequestService.sendEtrExchangeTradeSaveRequest(entity, decisionAct, systemAct);
        //增加调用头寸引擎代码end
    }

    @Override
    public void doDelete(String workflowId) {
        doDeleteFlow(workflowId);
        //增加调用头寸引擎代码start
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            EtrExchangeTradeSaveSecRepoRequest entity = getBussinessEntityByWorkflowId(workflowId);
            positionRequestService.sendDelete(entity);
        }
        //增加调用头寸引擎代码end
    }

    private void doDeleteFlow(String workflowId) {
        Map<String, Object> param = new HashMap<>();
        param.put("workflowId", workflowId);
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
                Boolean.class);
    }

    private void doInsertFlow(EtrExchangeTradeSaveSecRepoRequest dto) {
        BaseEtrInvestInfoDto entity = buildMultiEtrFlow(dto);
        dto.setAssetType2(entity.getEtrEntrustAdd().getAssetType1());
        // 设置指令创建来源
        entity.getEtrEntrustAdd().setEntrustCreateSource(DictCodeListExt3.指令创建来源_场内实时.getValue());
        entity.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
        entity.setWorkFlowId(dto.getWorkflowId());
        restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
                Boolean.class);
    }

    /**
     * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> getWorkFlowParameter(EtrExchangeTradeSaveSecRepoRequest dto) {
        WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> parameter = new WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest>();
        // TODO 根据实际指令类型来生成流程信息
        return parameter;
    }

    /**
     * 查询交易对手【非内部交易对手】
     *
     * @return
     */
    public List<SelectOptionDto> selectCounterIdByCounterType(SelectOptionRequestDto dto) {
        Map<String, Object> map = BeanUtil.object2Map(dto);
        if (map.containsKey("search")) {
            map.put("counterId", map.get("search"));
        }
        if (map.containsKey("initValue") && StringUtil.isNotEmpty(map.get("initValue"))) {
            map.put("counterId", map.get("initValue"));
        }
        Object object = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/selectMstCounterParty",
                map, Object.class);
        List<SelectOptionDto> resultList = new ArrayList<>();
        if (object instanceof List<?>) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) object;
            mapList.forEach(item -> {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(item.get("counterId").toString());
                selectOptionDto.setText(item.get("counterName").toString());
                selectOptionDto.setObject(item);
                resultList.add(selectOptionDto);
            });
        }
        return resultList;
    }

    /**
     * 查询证券代码
     *
     * @return
     */
    public Page<SelectOptionDto> getFinprodMarketInfo(Map<String, Object> map) {
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfoSecRepo", map,
                SelectOptionDto.class);
    }


    /**
     * @return
     */
    public String IsBondAsset(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/IsBondAsset", map,
                String.class);
    }

    /**
     * 查询标的
     *
     * @return
     */
    public List<SelectOptionDto> getFinprodId(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodId", map,
                SelectOptionDto.class);
    }

    /**
     * 查询组合资金账户
     *
     * @return
     */
    public List<SelectOptionDto> getPtlSecAcctInfo(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getPtlSecAcctInfo", map,
                SelectOptionDto.class);
    }

    /**
     * 查询证券账户
     *
     * @return
     */
    public List<SelectOptionDto> getSecAcctInfo(Map<String, Object> map) {
        return restTemplate.postForList(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getSecAcctInfo", map,
                SelectOptionDto.class);
    }


    public Page<EtrExchangeTradeOperateSecRepoRequest> selectEtrExchange(Map<String, Object> param) {
        param.put("userId", LoginUserUtil.getUserName());
        param.put("sysDate", iPlatformDate.getSysDateStr());
        return restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectEtrExchange",
                param, EtrExchangeTradeOperateSecRepoRequest.class);
    }

    /**
     * 对手方交易商联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterDealer(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectCounterDealer",
                map, SuggestDto.class);
    }

    /**
     * 对手方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SelectOptionDto> selectCounterTrader(Map<String, Object> map) {
        map.put("counterTraderId", map.get("param"));
        map.put("counterId", map.get("counterId"));
        List<Map<String, Object>> returnList = etrExchangeTradeSecRepoMapper.selectCounterTraderId(map);
        List<SelectOptionDto> resultList = new ArrayList<>();
        returnList.forEach(item -> {
            SelectOptionDto selectOptionDto = new SelectOptionDto();
            selectOptionDto.setId(item.get("counterTraderId").toString());
            selectOptionDto.setText(item.get("counterTraderId").toString());
            selectOptionDto.setObject(item);
            resultList.add(selectOptionDto);
        });
        return resultList;
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SelectOptionDto> selectTrader(Map<String, Object> map) {
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        List<SuggestDto> suggestDtos = etrExchangeTradeSecRepoMapper.selectTrader(map);
        List<SelectOptionDto> resultList = new ArrayList<>();
        suggestDtos.forEach(item -> {
            SelectOptionDto selectOptionDto = new SelectOptionDto();
            selectOptionDto.setId(item.getCode());
            selectOptionDto.setText(item.getCode());
            selectOptionDto.setObject(item);
            resultList.add(selectOptionDto);
        });
        return resultList;
    }

    /**
     * 查询债券对应的最新面值
     *
     * @param param 查询参数
     * @return 最新面值
     * @author dubaojun
     */
    public BigDecimal selectSecFaceValue(Map<String, Object> param) {
        Date sysDate = DateUtil.platDateFromCache();
        String finprodId = (String) param.get("finprodId");
        return (BigDecimal) etrCommonService.select券面面值(finprodId, DateUtil.format(sysDate));
    }

    /**
     * 根据画面的信息计算价格和到期收益率。<br/>
     * 如果以净价为基准，则计算全价和到期收益率<br/>
     * 如果以全价为基准，则计算净价和到期收益率<br/>
     * 如果以到期收益率为基准，则计算净价和全价<br/>
     * 注意：该方法支持返回结果如下：<br/>
     * <table style="border: 1px solid;">
     * <tr>
     * <th style="border: 1px solid;">字段中文</th>
     * <th style="border: 1px solid;">字段英文</th>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位应计利息</td>
     * <td style="border: 1px solid;">unitInt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">应计利息总额</td>
     * <td style="border: 1px solid;">intAmt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位净价</td>
     * <td style="border: 1px solid;">cprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">净价总额</td>
     * <td style="border: 1px solid;">cpriceAmt</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">单位全价</td>
     * <td style="border: 1px solid;">fprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">全价总额</td>
     * <td style="border: 1px solid;">fprice</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">到期收益率</td>
     * <td style="border: 1px solid;">yield</td>
     * </tr>
     * <tr>
     * <td style="border: 1px solid;">行权收益率</td>
     * <td style="border: 1px solid;">exerYield</td>
     * </tr>
     * </table>
     *
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    public Map<String, BigDecimal> calcPriceAndYield(Map<String, Object> param) {
        // 获取所有前台取得的参数
        Date settleDate = DateUtil.platDateFromCache();
        String finprodId = (String) param.get("finprodId");
        BigDecimal prinAmt = new BigDecimal(100);
        if (StringUtil.isNotEmpty(param.get("prinAmt"))) {
            prinAmt = new BigDecimal(param.get("prinAmt").toString());
        }
        String tradeMarket = (String) param.get("tradeMarket");
        String ps = (String) param.get("ps");
        String calculateField = (String) param.get("calculateField");

        // 定义需要返回的参数
        BigDecimal unitInt = null;// 单位应计利息
        BigDecimal intAmt = null;// 应计利息总额
        BigDecimal cprice = null;
        if (StringUtil.isNotEmpty(param.get("price"))) {
            cprice = new BigDecimal(param.get("price").toString());// 单位净价
        }
        BigDecimal cpriceAmt = null;// 净价总额
        BigDecimal fprice = null;// 单位全价
        if (StringUtil.isNotEmpty(param.get("fprice"))) {
            fprice = new BigDecimal(param.get("fprice").toString());// 单位全价
        }
        BigDecimal fpriceAmt = null;// 全价总额
        BigDecimal yield = null;// 到期收益率
        if (StringUtil.isNotEmpty(param.get("yield"))) {
            yield = new BigDecimal(param.get("yield").toString()).divide(new BigDecimal(100));// 到期收益率
        }
        BigDecimal exerYield = null;// 行权收益率
        if (StringUtil.isNotEmpty(param.get("exerYield"))) {
            exerYield = new BigDecimal(param.get("exerYield").toString()).divide(new BigDecimal(100));// 行权收益率
        }

        // 如果交易场所为上交所、深交所以外的情况
        boolean isJiaoYiSuo = true;
        if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), tradeMarket)
                && !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), tradeMarket)) {
            isJiaoYiSuo = false;
        }
        final int SCALE = 12;

        Map<String, Object> tempParamMap = new HashMap<>();
        tempParamMap.put("finprodId", finprodId);

        // b.获取计息区间投资端利率报价信息（债券的利率重置信息）

        param.put("finprodType", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        List<FinCashCalcRule> finCashCalcRuleList = etrExchangeTradeSecRepoMapper.selectBondsIntResetList(param);

        // c.获取债券的付息计划信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        List<FinCashPlanDto> finCashPlanList = etrExchangeTradeSecRepoMapper.getFinCashPlan(map);

        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }

        // d.获取债券基本资料信息
        // 计算行权收益率，非行权时，取大于当日的行权日
        tempParamMap.put("ps", ps);
        tempParamMap.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        tempParamMap.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        tempParamMap.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        tempParamMap.put("sysDate", DateUtil.platDateFromCache());
        tempParamMap.put("YES_NO_Y", DictCodeList.是否_是.getValue());
        tempParamMap.put("EXEC_MODE02", DictCodeList.行权方式_回售.getValue());
        tempParamMap.put("EXEC_MODE01", DictCodeList.行权方式_赎回.getValue());
        tempParamMap.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        tempParamMap.put("psT39", DictCodeList.交易方向_行权_赎回.getValue());
        SecInfoModelDto secInfoModelDto = etrExchangeTradeSecRepoMapper.getSecInfoModel(tempParamMap);
        SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);
        if (secInfoModelDto != null && secInfoModelDto.getStartDate() != null
                && DateUtil.compareTo(secInfoModelDto.getStartDate(), settleDate) > 0) {
            settleDate = secInfoModelDto.getStartDate();
        }
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息债的计息基础使用A_365F
            if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
                secInfoModel.setBasis(Basis.A_365F);
            }
        }

        // e.获取债券的还本计划信息
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (FinCashPlanDto item : finCashPlanList) {
            if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
                schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }

        List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        YieldPriceInParameter yieldPriceInParameter = new YieldPriceInParameter();
        yieldPriceInParameter.setCalDate(toLocalDate(settleDate));
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        PriceYieldInParameter priceYieldInParameter = new PriceYieldInParameter();
        priceYieldInParameter.setCalDate(toLocalDate(settleDate));
        priceYieldInParameter.setSecInfoModel(secInfoModel);
        priceYieldInParameter.setSecIntScheduleList(invScheduleList);
        priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
        priceYieldInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
            if (isNotFomartSec(intSchList, secInfoModelDto)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
                priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
        priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
            priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }

        // 百元面值应计利息
        BigDecimal accurInt = BigDecimal.ZERO;
        // 交易所债券特殊处理start----------------------------------------------------------------
        Date tempSettleDate = settleDate;
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 行权-回售 行权-赎回 交割日不加1
            if (!StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())
                    && !StringUtil.equals(ps, DictCodeList.交易方向_行权_赎回.getValue())) {
                tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
            }
        }
        // 交易所债券特殊处理end----------------------------------------------------------------
        // a.当前债券面额取得（根据还本计划计算）
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("finprodId", finprodId);
        paraMap.put("settleDate", DateUtil.format(tempSettleDate));
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
//		String settleDate = (String) param.get("settleDate");
        param.put("settleDate", settleDate);
        BigDecimal curSecAmt = etrExchangeTradeSecRepoMapper.getCurSecAmt(param);
        AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
        // 计算日
        accurIntCalInParameter.setCalDate(toLocalDate(tempSettleDate));
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        // 债券基本资料
        accurIntCalInParameter.setSecInfoModel(secInfoModel);
        // 付息计划
        accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
        // 利率重置信息
        accurIntCalInParameter.setPaperIrResetList(paperIrResetList);
        // 交易所债券特殊处理start----------------------------------------------------------------
        AccurIntCalOutParameter out = null;
        // 如果交易场所为上交所、深交所的情况
        if (isJiaoYiSuo) {
            // 付息计划截止日对应的利息
            Map<String, BigDecimal> intMap = new HashMap<>();
            if (null != invScheduleList && !invScheduleList.isEmpty()) {
                for (SecIntSchedule intSche : invScheduleList) {
                    intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
                }
            }
            if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
                    && !((Variety.贴现.equals(secInfoModel.getBondsType()))
                    || Variety.零息.equals(secInfoModel.getBondsType()))) {
                out = new AccurIntCalOutParameter();
                out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString())
                        .multiply(new BigDecimal("100")).divide(curSecAmt, 12, RoundingMode.HALF_UP));
                accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
                AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
                if (accurIntCalInParameter.getPaperIrResetList() != null
                        && accurIntCalInParameter.getPaperIrResetList().size() > 1) {
                    accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
                }
                out = calculator2.calculate(accurIntCalInParameter);
            }
        }
        // 交易所债券特殊处理end----------------------------------------------------------------
        if (out != null) {
            accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
        } else {
            // 从计算工厂中创建应计利息计算器
            AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
            // 计算利息
            AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
            accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
        }
        // 单位应计利息
        if (isJiaoYiSuo) {
            unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")) ? "8"
                            : CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
                    RoundingMode.HALF_UP);
        } else {
            unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
                    Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12"
                            : CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
                    RoundingMode.HALF_UP);
        }
        intAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(unitInt, prinAmt), 100, 2);
        if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps))) {
            if (unitInt.compareTo(BigDecimal.ZERO) < 0) {
                unitInt = BigDecimal.ZERO;
            }
            if (intAmt.compareTo(BigDecimal.ZERO) < 0) {
                intAmt = BigDecimal.ZERO;
            }
        }
        try {
            // 单位净价
            if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位净价.getValue())) {
                cpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(cprice, prinAmt), new BigDecimal(100), 2);
                // 全价总额 = 净价总额+ 利息总额
                fpriceAmt = ArithmeticUtil.doAdd(intAmt, cpriceAmt);
                // round（全价总额/券面总额，10）*100
                // 全价 = 单位应计利息 + 单位净价 (保持横向的一致性)
                fprice = ArithmeticUtil.doAdd(cprice, unitInt);
            }
            YieldPriceOutParameter yieldPriceOutParameter = null;
            PriceYieldOutParameter priceYieldOutParameter = null;
            switch (calculateField) {
                // 单位净价
                case "01":
                    // 全价总额
                case "02":
                    // 全价总额==>到期收益率
                    yieldPriceInParameter.setFullPrice(fprice);
                    yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                    yield = yieldPriceOutParameter.getMatYield();
                    if (secInfoModelDto.getNextExeDate() != null) {
                        // 全价总额==>行权收益率
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            exerYield = yieldPriceOutParameter.getExeYield();
                        }
                    }
                    break;
                // 到期收益率
                case "03":
                    // 到期收益率==>全价总额
                    priceYieldInParameter.setMatYield(yield);
                    priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                    fprice = priceYieldOutParameter.getFullPrice();
                    // 全价总额==>行权收益率
                    if (secInfoModelDto.getNextExeDate() != null) {
                        yieldPriceInParameter.setFullPrice(fprice);
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        if (yieldPriceInParameter.getNextExePrice() != null) {
                            yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                            exerYield = yieldPriceOutParameter.getExeYield();
                        }
                    }
                    break;
                // 行权收益率（暂时不存在）
                case "04":
                    // 行权收益率==>全价总额
                    if (secInfoModelDto.getNextExeDate() != null) {
                        priceYieldInParameter.setMatYield(exerYield);
                        yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
                        yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
                        priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
                        fprice = priceYieldOutParameter.getFullPrice();
                        // 全价总额==>到期收益率
                        yieldPriceInParameter.setFullPrice(fprice);
                        yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
                        yield = yieldPriceOutParameter.getMatYield();
                    }
                    break;
                default:
                    break;
            }
            // 如果是全价、到期收益率 或 行权收益率
            if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位全价.getValue())
                    || StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_到期收益率.getValue())
                /*
                 * || StringUtil.equals(calculateField,
                 * DictCodeListExt3.债券计算模式_行权收益率.getValue())
                 */) {
                // 全价总额 = 单位全价 * 券面总额 / 100
                fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fprice, prinAmt), new BigDecimal(100), 2);
                cpriceAmt = ArithmeticUtil.subtract(fpriceAmt, intAmt);
                // round（净价总额/券面总额，10）*100
                // 单位净价 = 全价-单位应计利息 (保持横向的一致性)
                cprice = ArithmeticUtil.subtract(fprice, unitInt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (yield != null) {
            yield = ArithmeticUtil.multiply(yield, new BigDecimal(100));
        }
        if (exerYield != null) {
            exerYield = ArithmeticUtil.multiply(exerYield, new BigDecimal(100));
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("unitInt", unitInt);
        result.put("intAmt", intAmt);
        result.put("cprice", cprice);
        result.put("cpriceAmt", cpriceAmt);
        result.put("fprice", fprice);
        result.put("fpriceAmt", fpriceAmt);
        result.put("yield", yield == null ? new BigDecimal(0.00) : yield);
        result.put("exerYield", exerYield);
        return result;
    }
    /**
     * 查询竞价交易行情信息
     **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EtrExchangeTradeQuotationHqSecRepoResponse findQuotations(String secId) {
        String param = "?stock_code=" + secId;
        Map<String, Object> quoteList;
        Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick + param, Map.class);
        if (quoteMap != null && quoteMap.get("data") != null) {
            quoteList = (Map<String, Object>) quoteMap.get("data");
            return BeanUtil.mapToObject(quoteList, EtrExchangeTradeQuotationHqSecRepoResponse.class);
        }
        return new EtrExchangeTradeQuotationHqSecRepoResponse();
    }

    /**
     * 修改/双击查看详情查询信息 <功能详细描述>
     *
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    public EtrExchangeTradeSaveSecRepoRequest getEtrExchangeTradeInfo(String entrustId) {
        try {
            EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = new EtrExchangeTradeSaveSecRepoRequest();
            //通过指令ID获取委托指令表的DTO,获取委托类型
            EtrEntrust etrEntrust = etrExchangeTradeSecRepoMapper.selectEntrustByKey(entrustId);
            if (StringUtil.isNotEmpty(etrEntrust)) {
                // 债券现券交易指令
                if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(etrEntrust.getEntrustType())) {
                    EtrBondBiddingDto etrBondBiddingDto = EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustSelect(entrustId);
                    if (StringUtil.isNotEmpty(etrBondBiddingDto)) {
                        BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondBiddingDto);
                        etrExchangeTrade001Dto.setYield(etrBondBiddingDto.getMatureYield().multiply(new BigDecimal(100))); // 到期收益率
                        etrExchangeTrade001Dto.setFprice(etrBondBiddingDto.getFullPrice()); // 全价
                        etrExchangeTrade001Dto.setCalculateField(etrBondBiddingDto.getCalMode());// 计算方式
                        etrExchangeTrade001Dto.setFinprodType2(etrBondBiddingDto.getFinprodType2());// 金融产品类型（投管）
                    }
                }
                // 通用式质押回购指令
                else if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(etrEntrust.getEntrustType())) {
                    EtrBondRepoBiddingDto etrBondRepoBiddingDto = EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustSelect(entrustId);
                    BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, etrBondRepoBiddingDto);
                    etrExchangeTrade001Dto.setPrice(etrBondRepoBiddingDto.getPrice().multiply(new BigDecimal(100)));
                    etrExchangeTrade001Dto.setFinprodType2(etrBondRepoBiddingDto.getFinprodType2());// 金融产品类型（投管）
                    etrExchangeTrade001Dto.setEntrustPrice(etrExchangeTrade001Dto.getPrice());//指令价格
                    etrExchangeTrade001Dto.setPrice(null);//净价需要置空
                }
                etrExchangeTrade001Dto.setEntrustBussType(etrEntrust.getEntrustType());
                EtrEntrustAdd etrEntrustAdd = etrExchangeTradeSecRepoMapper.selectEntrustAddByKey(etrEntrust.getEntrustId());
                if (null != etrEntrustAdd && StringUtils.isNotEmpty(etrEntrustAdd.getDealCurAcct())) {
                    etrExchangeTrade001Dto.setAcctNo(etrEntrustAdd.getDealCurAcct());
                }
            }
            return etrExchangeTrade001Dto;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 委托关闭
     *
     * @param entity
     */
    public void closeDelegate(EtrExchangeTradeOperateSecRepoRequest entity) {
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        componentService.entrustClose(etrBaseDto);
        // 处理指令持仓和头寸
        if (baseEtrCashPositionCommService != null
                && DictCodeList.委托状态_已接收.getValue().equals(entity.getEntrustStatus())) {
            baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId(), DictCodeList.是否_是.getValue());
        }
        //处理履历
        updateCfetsOprHis(entity.getEntrustId(), "close");
        positionRequestService.sendClose(entity);
    }

    /**
     * 委托撤销
     *
     * @param entity
     */
    public void recallDelegate(EtrExchangeTradeOperateSecRepoRequest entity) {
        EtrBaseDto etrBaseDto = new EtrBaseDto();
        etrBaseDto.setEntrustId(entity.getEntrustId());
        EtrParameterDto paraDto = new EtrParameterDto();
        setUser(paraDto);
        componentService.updateSettings(paraDto);
        //关闭委托
        componentService.entrustRevoke(etrBaseDto);
        //处理指令持仓和头寸(仅有未被接收的指令才可以撤会,所以一般只可能存在下指令时生成的持仓和流水)
        if (baseEtrCashPositionCommService != null && (DictCodeList.委托状态_待接收.getValue().equals(entity.getEntrustStatus())
                || DictCodeListExt3.委托状态_待分发.getValue().equals(entity.getEntrustStatus()))) {
            baseEtrCashPositionCommService.entrustCloseExecute(entity.getEntrustId(), DictCodeList.是否_是.getValue());
        }
        //处理履历
        updateCfetsOprHis(entity.getEntrustId(), "revoke");
    }

    /**
     * 保存check
     *
     * @param entity
     */
    public AjaxResult checkSave(EtrExchangeTradeSaveSecRepoRequest entity, BindingResult result) {
        try {
            EtrParameterDto paraDto = new EtrParameterDto();
            setUser(paraDto);
            if (null != entity.getEntrustPrice()) {
                entity.setPrice(entity.getEntrustPrice());// 指令价格 或者净价
            }
            // 构造单组合单资产DTO
            EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);
            //设置交易买卖默认参数
            setCheckParams(entity, paraDto);
            if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                // 债券竞价指令
                EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
                BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
                etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(), new BigDecimal(100), 8)); // 到期收益率
                etrBondBiddingDto.setFullPrice(entity.getFprice()); // 全价
                etrBondBiddingDto.setCalMode(entity.getCalculateField());// 计算方式
                etrBondBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
                etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustCheck(etrBondBiddingDto, result);
            }
            if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
                // 债券质押式回购竞价指令
                EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
                BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
                etrBondRepoBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
                etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
                EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustCheck(etrBondRepoBiddingDto, result);
            }
            AjaxResult ajax = new AjaxResult(result);
            Field[] fields = ReflectUtil.getDeclaredFields(ajax);
            for (Field field : fields) {
                if (StringUtil.equals(field.getName(), "messageType")) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, ajax, MessageType.错误消息.getCode());
                    break;
                }
            }
            return ajax;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 有效价格范围check
     * @param entity
     * @param result
     * @return
     */
     public List<ValidMessage> checkSavePriceRange(EtrExchangeTradeSaveSecRepoRequest entity, BindingResult result) {
        try {
            // 当前系统时间
            Date nowDate = DateUtil.platDateFromCache();
            // 业务品种
            String finprodType = null;
            // 标的信息
            FinProduct finproduct = new FinProduct();
            Map<String, Object> map = new HashMap<>();
            map.put("initValue", entity.getStockId());
            Page<SelectOptionDto> dtoPage = getFinprodMarketInfo(map);
            if (null != dtoPage && !StringUtil.isEmptyList(dtoPage.getRoot())) {
                finproduct = BeanUtil.mapToObject((Map) dtoPage.getRoot().get(0).getObject(), FinProduct.class);
            }
            // 检查价格
            BigDecimal checkPrice = entity.getPrice();
            // 行情信息
            EtrExchangeTradeQuotationHqSecRepoResponse quotation = findQuotations(entity.getStockId());
            if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(entity.getEntrustBussType())) {
                // 计价方式  全价时,使用全价校验
                if(StringUtil.equals(entity.getPriceType(),DictCodeListExt3.计价方式_全价.getValue())){
                    checkPrice = entity.getFprice();
                }
                finprodType = DictCodeListExt1.金融产品类型_投管用_债券.getValue();
                finproduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            }
            else if(DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(entity.getEntrustBussType())) {
                finprodType = DictCodeList.回购类型_质押式竞价回购.getValue();
                finproduct = null;
            }
            //
            List<ValidMessage> validMessageList = checkPriceRangeCommon(finprodType,finproduct,entity,checkPrice,nowDate,quotation);

            return validMessageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 有效价格范围check  共通
     * @param finprodType 业务品种
     * @param finproduct 标的信息
     * @param entity 指令信息
     * @param checkPrice 检查价格
     * @param nowDate 当前时间
     * @param quotation 行情信息
     * @return
     */
    private List<ValidMessage> checkPriceRangeCommon(String finprodType, FinProduct finproduct, EtrExchangeTradeSaveSecRepoRequest entity,
                                                    BigDecimal checkPrice, Date nowDate, EtrExchangeTradeQuotationHqSecRepoResponse quotation) {
        List<ValidMessage> validMessageList = new ArrayList<>();
        FinProductType finProductType = new FinProductType();
        if(finproduct!=null&&finproduct.getFinprodId()!=null){
            FinProductTypeKey  finProductTypeKey= new FinProductTypeKey();
            finProductTypeKey.setBranch(0L);
            finProductTypeKey.setFinprodId(finproduct.getFinprodId());
            finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
        }
        Map<String,Object> paraMap =new HashMap<>();
        paraMap.put("ETR_TRAD","ETR");
        if(finproduct!=null){
            paraMap.put("ASSET_CLASSIFY1",finproduct.getFinprodType2());
        }else{
            paraMap.put("ASSET_CLASSIFY1","ZYSHG");

        }
        paraMap.put("TRADE_PLAT",entity.getTradePlat()); // 指定交易方式
        paraMap.put("NOW_DATE",nowDate);
        // 价格价格时间配置
        TrdPriceTimeConfig trdPriceTimeConfig = getTrdPriceTimeConfig(paraMap);
        // 无匹配交易价格时间配置
        if(trdPriceTimeConfig==null){
            validMessageList.add(new ValidMessage("PRICE_RANGE_001"));
        }
        // 开盘集合竞价
        else if(StringUtil.equals(trdPriceTimeConfig.getPriceTimeType(),DictCodeListExt3.价格申报范围类型_开盘集合竞价.getValue())){
            if(StringUtil.equals(finprodType,DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                if(quotation!=null
                        &&(checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(1.3),quotation.getPreClosePrice()))>0
                            ||checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(0.7),quotation.getPreClosePrice()))<0)){
                    validMessageList.add(new ValidMessage("PRICE_RANGE_002"));
                }
            }
            else if(StringUtil.equals(finprodType,DictCodeList.回购类型_质押式竞价回购.getValue())){
                if(quotation!=null
                        &&(checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(2),quotation.getPreClosePrice()))>0
                        ||checkPrice.compareTo(BigDecimal.ZERO)<0)){
                    validMessageList.add(new ValidMessage("PRICE_RANGE_002"));
                }
            }
        }
        // 连续竞价
        else if(StringUtil.equals(trdPriceTimeConfig.getPriceTimeType(),DictCodeListExt3.价格申报范围类型_连续竞价.getValue())){
            if(StringUtil.equals(finprodType,DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                if(StringUtil.equals(finProductType.getType10(), BussinessDictCodeList.行内资产三级分类_国债.getValue())
                        ||StringUtil.equals(finProductType.getType10(), BussinessDictCodeList.行内资产三级分类_地方政府债.getValue())
                        ||StringUtil.equals(finProductType.getType10(), BussinessDictCodeList.行内资产三级分类_政策性金融债.getValue())
                        ||StringUtil.equals(finProductType.getType10(), BussinessDictCodeList.行内资产三级分类_政府支持机构债券.getValue())){
                    if(quotation!=null
                            &&(checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(1.1),quotation.getLastPrice()))>0
                            ||checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(0.9),quotation.getLastPrice()))<0)){
                        validMessageList.add(new ValidMessage("PRICE_RANGE_002"));
                    }
                }else{
                    if(quotation!=null
                            &&(checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(1.2),quotation.getLastPrice()))>0
                            ||checkPrice.compareTo(ArithmeticUtil.multiply(new BigDecimal(0.8),quotation.getLastPrice()))<0)){
                        validMessageList.add(new ValidMessage("PRICE_RANGE_002"));
                    }
                }
            }
            else if(StringUtil.equals(finprodType,DictCodeList.回购类型_质押式竞价回购.getValue())){
                if(quotation!=null
                        &&(checkPrice.compareTo(ArithmeticUtil.doAdd(new BigDecimal(0.01),quotation.getLastPrice()))>0
                        ||checkPrice.compareTo(BigDecimal.ZERO)<0)){
                    validMessageList.add(new ValidMessage("PRICE_RANGE_002"));
                }
            }
        }
        return validMessageList;
     }

    /**
     * 交易所价格时间配置查询
     * 指令交易 ETR_TRAD
     * 资产分类一 ASSET_CLASSIFY1
     * 资产分类二 ASSET_CLASSIFY2
     * 资产分类三 ASSET_CLASSIFY3
     * 交易方式 TRADE_PLAT
     * 查询时间 NOW_TIME
     * @param paraMap
     * @return
     */
    private TrdPriceTimeConfig getTrdPriceTimeConfig(Map<String, Object> paraMap) {
        List<TrdPriceTimeConfig> trdPriceTimeConfigList = etrExchangeTradeSecRepoMapper.getTrdPriceTimeConfig(paraMap);
        if(!StringUtil.isEmptyList(trdPriceTimeConfigList)){
            return trdPriceTimeConfigList.get(0);
        }
        return null;
    }

    /**
     * //根据金融产品代码获取投管一级分类
     *
     * @param entity
     * @return
     */
    public EtrExchangeTradeSaveSecRepoRequest setAssetTypeById(EtrExchangeTradeSaveSecRepoRequest entity) {
        // 根据资产获取资产类型
        if (StringUtil.isNotEmpty(entity.getFinprodId())) {
            FinProductTypeKey key = new FinProductTypeKey();
            key.setFinprodId(entity.getFinprodId());
            key.setBranch(new Long(0));
            FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
            if (null != finProductType) {//获取资产一级分类
//				entity.setAssetsType(finProductType.getType6());
            }
        }
        return entity;
    }

    public void setCheckParams(EtrExchangeTradeSaveSecRepoRequest entity, EtrParameterDto paraDto) {
        /********************************/
        setAssetTypeById(entity);
        //设置默认最小价差、最小交易单位；否则指令组件抛异常；
        paraDto.setMinDiffPrice(new BigDecimal(0.001).setScale(3, BigDecimal.ROUND_HALF_UP));
        paraDto.setMinDealUnit(new BigDecimal(1));

        Map<String, UnitEnum> numUnit = new HashMap<String, UnitEnum>();
        numUnit.put(entity.getTradeMarket(), UnitEnum.张);
        paraDto.setNumUnit(numUnit);

    }

    /***
     * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
     * @return
     */
    public String getInvAim() {
        return etrExchangeTradeSecRepoMapper.getInvAim();
    }

    /**
     * @param map
     * @return
     */
    public int selectBondExecPlan(Map<String, Object> map) {
        // 交易场所。上海证券交易所
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        // 行权方式.回售
        map.put("EXEC_MODE_02", DictCodeList.行权方式_回售.getValue());
        // 现金流二级类型.本金
        map.put("CASH_TYPE_2_C01", DictCodeListExt1.现金流二级类型_本金.getValue());
        return etrExchangeTradeSecRepoMapper.selectBondExecPlan(map);
    }

    /**
     * 根据画面的信息计算委托金额和委托数量。<br/>
     * 如果目标类型为“数量”，则根据数量计算金额<br/>
     * 如果目标类型为“金额”，则根据数量计算数量<br/>
     *
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    public Map<String, BigDecimal> calcEntrustNumAndAmt(Map<String, Object> param) {
        String resetFlag = param.get("resetFlag") == null ? null : param.get("resetFlag").toString();
        if (resetFlag != null) {
            Map<String, BigDecimal> resultMap = new HashMap<>();
            resultMap.put("entrustNum", null);
            resultMap.put("entrustAmt", null);
            return resultMap;
        }
        Map<String, BigDecimal> resultMap = new HashMap<>();
        BigDecimal entrustAmt = this.toBigDecimal(param.get("entrustAmt"));
        BigDecimal entrustNum = this.toBigDecimal(param.get("entrustNum"));
        String ps = param.get("ps") == null ? null : param.get("ps").toString();
//        String currentTab = param.get("currentTab") == null ? null : param.get("currentTab").toString();
        String currentTab = (String) param.get("dealMode");
        BigDecimal entrustPrice = this.toBigDecimal(param.get("entrustPrice"));
        String targetType = (String) param.get("targetType");
        String finprodId = (String) param.get("finprodId");
        String finprodType2 = (String) param.get("finprodType2");
        BigDecimal availableNum = param.get("availableNum") == null ? null : new BigDecimal(param.get("availableNum").toString());
        // 如果是质押式回购，则默认为价格为100
        if (StringUtil.equals(finprodType2, "ZYSHG")) {
            entrustPrice = new BigDecimal(100);
        }
        String tradeMarket = (String) param.get("tradeMarket");
        if (StringUtil.equals(targetType, DictCodeListExt3.目标类型_金额.getValue())) {
            entrustNum = ArithmeticUtil.divide(entrustAmt, entrustPrice, 0, RoundingMode.DOWN);
        }

        if (null != param.get("minTrdUnitLimitDto")) {
            MinTrdUnitLimitDto unit = (MinTrdUnitLimitDto) param.get("minTrdUnitLimitDto");
            entrustNum = this.formatEntrustNum(entrustNum, unit);
        } else {
            BigDecimal minNum = new BigDecimal(1);
            MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(finprodId, currentTab, tradeMarket, finprodType2, ps);
            if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
                minNum = minTrdUnitLimitDto.getMinStep();
            }
            if (null != availableNum && availableNum.compareTo(BigDecimal.ZERO) > 0) {
                resultMap.put("minNum", minNum);
            }
            if (entrustNum == null || entrustNum.compareTo(BigDecimal.ZERO) == 0) {
            } else {
                    entrustNum = ArithmeticUtil
                            .multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN), minNum);
            }
        }
        // int rs = entrustNum.compareTo(BigDecimal.ZERO);
        // rs == -1 entrustNum小于0 例如：entrustNum = -10.00
        // rs == 0 entrustNum等于0 例如：entrustNum = 0.00
        // rs == 1 entrustNum大于0 例如：entrustNum = 10.00
        if (StringUtil.equals(targetType, DictCodeListExt3.目标类型_数量.getValue())) {
            entrustAmt = ArithmeticUtil.multiply(entrustNum, entrustPrice);
        }
        resultMap.put("entrustNum", entrustNum);
        resultMap.put("entrustAmt", entrustAmt);
        return resultMap;
    }

    /**
     * @param entrustNum         指令数量
     * @param minTrdUnitLimitDto 最小交易单位对象
     * @return 格式化指令数量
     */
    private BigDecimal formatEntrustNum(BigDecimal entrustNum, MinTrdUnitLimitDto minTrdUnitLimitDto) {
        if (entrustNum == null || entrustNum.compareTo(BigDecimal.ZERO) == 0) {
            return entrustNum;
        }
        BigDecimal minNum = new BigDecimal(1);
        if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
            minNum = minTrdUnitLimitDto.getMinStep();
        }
        entrustNum = ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN), minNum);
        return entrustNum;
    }

    /**
     * 获取最小交易单元
     *
     * @param finprodId 金融产品代码
     * @param tradePlat 交易平台
     * @param assetType 资产大类
     * @return 获取最小交易单元
     */

    @SuppressWarnings({"unchecked"})
    public MinTrdUnitLimitDto getInterfSysMinTrdUnit(String finprodId, String tradePlat, String tradeMarket,
                                                     String assetType, String ps) {
        MinTrdUnitLimitDto minTrdUnitLimitDto = null;
//        tradePlat = getTradePlat(tradePlat, tradeMarket);
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
            List<MinTrdUnitLimitDto> list = (List<MinTrdUnitLimitDto>) res.get("data");
            if (list != null && list.size() > 0) {
                minTrdUnitLimitDto = JSON.parseObject(JSON.toJSONString(list.get(0)), MinTrdUnitLimitDto.class);
            }
        }
        return minTrdUnitLimitDto;
    }

    /**
     * @param map
     * @return 获取最小价差
     */
    @SuppressWarnings("unchecked")
    public List<MinPirceDto> getInterfSysMinPriceAll(Map<String, Object> map) {
        List<MinPirceDto> minPirceList = new ArrayList<MinPirceDto>();
        Map<String, Object> res = restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinPrice", map, Map.class);
        if (res != null && res.get("data") != null) {
            List<MinPirceDto> list = (List<MinPirceDto>) res.get("data");
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    minPirceList.add(JSON.parseObject(JSON.toJSONString(list.get(i)), MinPirceDto.class));
                }
            }
        }
        return minPirceList;
    }

    /**
     * @param finprodType2
     * @return
     */
    private String getPAAssetType(String finprodType2) {
        String assetType = "";
        if (StringUtils.isNotEmpty(finprodType2)) {
            if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                assetType = DictCodeList.平安资产类型_债券.getValue();
            } else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                    || StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                    || StringUtils.equals(finprodType2, "ZYSHG")) {
                assetType = DictCodeList.平安资产类型_质押式回购.getValue();
            }
        }
        return assetType;
    }

    /**
     * 转为数值类型
     *
     * @param object Object
     * @return 数值
     */
    private BigDecimal toBigDecimal(Object object) {
        if (object == null || object == "") {
            return null;
        }
        return new BigDecimal(object.toString());
    }

    /**
     * 判断指令是否分发
     *
     * @param entrustId 指令编码
     * @return 分发结果
     */
    public EtrDistribute getDistributeByEntrustId(String entrustId) {
        Map<String, Object> map = new HashMap<>();
        map.put("entrustId", entrustId);
        return etrExchangeTradeSecRepoMapper.getDistributeByEntrustId(map);
    }

    /**
     * @param param
     * @return
     */
    public String selectStdsecConverseRatio(Map<String, Object> param) {
        param.put("date", iPlatformDate.getSysDate());
        String converseRatio = etrExchangeTradeSecRepoMapper.selectStdsecConverseRatio(param);
        if (StringUtils.isNotEmpty(converseRatio)) {
            // 画面上乘以100后展示
            BigDecimal converseRatios = ArithmeticUtil.multiply(new BigDecimal(converseRatio), BigDecimal.valueOf(100));
            converseRatio = converseRatios.toString();
        }/*else{
			converseRatio="100";
		}*/
        return converseRatio;
    }

    /**
     * 查询最小交易单元
     *
     * @param param
     * @return
     */
    public BigDecimal getMinNumStep(Map<String, Object> param) {
        String ps = !StringUtil.isNotEmpty(param.get("ps")) ? "T01" : param.get("ps").toString();
        String currentTab = param.get("currentTab") == null ? null : param.get("currentTab").toString();
        String finprodId = (String) param.get("finprodId");
        String finprodType2 = (String) param.get("finprodType2");
        String tradeMarket = (String) param.get("tradeMarket");
        BigDecimal minNum = new BigDecimal(1);
        MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(finprodId, currentTab, tradeMarket, finprodType2, ps);
        if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
            minNum = minTrdUnitLimitDto.getMinStep();
        }
        return minNum;
    }

    /**
     * 场内指令-查询资产代码信息
     *
     * @param pageParam
     * @return
     */
    public Page<SelectOptionDto> getFinprodMarketInfoPosition(Map<String, Object> pageParam) {
        pageParam.put("searchType","SecRepo");
        return restTemplate.postForPage(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfoPosition", pageParam,
                SelectOptionDto.class);
    }

    /**
     * 场内指令-查询质押券再折算率
     *
     * @param pageParam
     * @return
     */
    public TrdMoreConvrateDto getMoreConvrate(Map<String, Object> pageParam) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("acctNo", (String) pageParam.get("acctNo"));
        Map<String, Object> res = restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMoreConvrate", param, Map.class);
        if (res != null && res.get("data") != null) {
            List<TrdMoreConvrateDto> list = (List<TrdMoreConvrateDto>) res.get("data");
            if (list != null && list.size() > 0) {
                return JSON.parseObject(JSON.toJSONString(list.get(0)), TrdMoreConvrateDto.class);
            }
        }
        return null;
    }

    /**
     * 插入和修改时，指令持仓流水 、现金流水处理
     * add by zhangpei
     * 20201128
     *
     * @param dto
     */
    public void doInsetOrUpdateForEtrPositionHold(EtrExchangeTradeSaveSecRepoRequest dto) {
        // 构造单组合单资产DTO
        EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(dto);
        // 指令持仓流水 、现金流水处理
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();// 组合持仓流水、组合现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();// 现金流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();// 持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDtoUnit = new EtrSecPositionFlowDto();// 组合持仓流水DTO
        EtrCashPositionFlowDto etrCashPositionFlowDtoUnit = new EtrCashPositionFlowDto();// 组合现金流水DTO
        if (DictCodeList.交易方向_卖出.getValue().equals(etrSinglePortfolioDto.getPs())
                || DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs())) {
            /********************** 持仓流水DTO构造 *************************************/
            etrSecPositionFlowDtoUnit.setSeqno(getSeqNo());
            etrSecPositionFlowDtoUnit.setPortfolioId(etrSinglePortfolioDto.getPortfolioId());// 组合代码
            etrSecPositionFlowDtoUnit.setSecAcctId(etrSinglePortfolioDto.getSecAcctId());// 证券账户
            etrSecPositionFlowDtoUnit.setFinprodId(etrSinglePortfolioDto.getFinprodId());// 金融产品代码
            if (DictCodeListExt3.交易方向_正回购.getValue().equals(etrSinglePortfolioDto.getPs())) {
                etrSecPositionFlowDtoUnit.setFinprodId("XN_PRODID999");
            }
            etrSecPositionFlowDtoUnit.setBranch(new Long(0));// 分支序号
            etrSecPositionFlowDtoUnit.setInvAim(StringUtils.isNotEmpty(etrSinglePortfolioDto.getInvAim()) ? etrSinglePortfolioDto.getInvAim() : "999999");
            etrSecPositionFlowDtoUnit.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());// 持仓类型
            etrSecPositionFlowDtoUnit.setCdate(iPlatformDate.getSysDate());// 日期
            etrSecPositionFlowDtoUnit.setCcy(DictCodeList.币种_人民币_CNY.getValue());// 币种
            etrSecPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustNum().multiply(new BigDecimal(-1)));// 数量
            etrSecPositionFlowDtoUnit.setpFinprodId(null);// 母金融产品代码
            etrSecPositionFlowDtoUnit.setTradeId(etrSinglePortfolioDto.getEntrustId());// 指令编号
            etrSecPositionFlows.add(etrSecPositionFlowDtoUnit);
            /*************************************************************************/
        } else if (DictCodeList.交易方向_买入.getValue().equals(etrSinglePortfolioDto.getPs())
                || DictCodeListExt3.交易方向_逆回购.getValue().equals(etrSinglePortfolioDto.getPs())) {
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
                if (StringUtil.isNotEmpty(etrSinglePortfolioDto.getEntrustAmt())) {
                    etrCashPositionFlowDtoUnit.setAmount(etrSinglePortfolioDto.getEntrustAmt().multiply(new BigDecimal(-1)));// 数量
                }
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

        // modify by etliu 20220308 start
        etrPositionHoldDto.getPtlCashPositionFlows().forEach(etrCashPositionFlowDto -> {
            etrCashPositionFlowDto.setCdate(DateUtil.platDateFromCache());
            etrCashPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
        });
        etrPositionHoldDto.getPtlSecPositionFlows().forEach(etrSecPositionFlowDto -> {
            etrSecPositionFlowDto.setCdate(DateUtil.platDateFromCache());
            etrSecPositionFlowDto.setIsExistNew(DictCodeList.是否_是.getValue());
        });
        // modify by etliu 20220308 end

        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
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
     * 1.6计算偏离度
     *
     * @param request 画面参数
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public String calcDeviationDegree(CalcDeviationDegreeRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        // 平台日
        param.put("sysDate", DateUtil.format(iPlatformDate.getSysDate()));
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
            } else if (StringUtil.equals(DictCodeList.登记托管机构_中债_上海清算所.getValue(),
                    finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_上清所_SCH.getValue());
            } else if (StringUtil.equals(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue(),
                    finProduct.getRegistOrg())) {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中证登_CSD.getValue());
            } else {
                param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
            }
        } else {
            param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
        }
        // 查询上一估值日的净价
        BigDecimal 上一估值日净价 = etrExchangeTradeSecRepoMapper.getLastNetPrice(param);
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
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取交易方向
     *
     * @param dto 画面参数 SelectDto selectDto
     * @return 偏离度
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPsSelectOptions(PsOptionsRequest dto, SelectDto selectDto) {
        List<SelectOptionDto> selectOptionDtos = new ArrayList<>();
        String finprodType2 = dto.getFinprodType2();
        if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
            //债券默认买入、卖出
            selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
                return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                        || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
            }).collect(Collectors.toList());
        } else if (StringUtil.equals(finprodType2, "ZYSHG")) {
            selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
                return StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
            }).collect(Collectors.toList());
        }
        return selectOptionDtos;
    }


    public String IsSpeedTOneOrZero(Map<String, Object> map) {
        return etrExchangeTradeSecRepoMapper.IsSpeedTOneOrZero(map);
    }

    /**
     * 计价方式
     * @param map
     * @return
     */
    public String getPriceType(Map<String, Object> map) {
        String isNetPrice = etrExchangeTradeSecRepoMapper.getIsNetPrice(map);
        if(StringUtil.equals(isNetPrice,DictCodeList.是否_是.getValue())){
            return DictCodeListExt3.计价方式_净价.getValue();
        }else{
            return DictCodeListExt3.计价方式_全价.getValue();
        }
    }

    public String doRiskcalCheckMethod(EtrExchangeTradeSaveSecRepoRequest dto) {
        //参数不能为空
        if (StringUtil.isEmptyList(dto.getPortfolioList())) {
            return "组合分配列表不能为空";
        }
        List<String> messages = new ArrayList<>();
        if (StringUtil.isNotEmpty(dto.getPortfolioList())) {
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                if (!StringUtil.isNotEmpty(item.getPortfolioId())) {
                    messages.add("请输入组合名称。");
                }
            }
        }
        if (!StringUtil.isNotEmpty(dto.getFinprodId())) {
            messages.add("请输入资产代码。");
        }
        if (messages.size() > 0) {
            return StringUtil.joinString(messages, com.joyintech.framework.constant.GlobalConstant.VERTICAL_LINE);
        }
        return null;
    }

    /**
     * 风险试算时，后台数据转换
     *
     * @return
     */
    public Map<String, Object> doRiskcalPackageEquity(EtrExchangeTradeSaveSecRepoRequest dto) {
        //风险试算封装dto
        EtrExchangeTradeRiskcalSecRepoDto d = new EtrExchangeTradeRiskcalSecRepoDto();

        BeanUtil.copyPropertiesIgnoreNull(d, dto);
        List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
        for (EtrExchangeTradePortfolioSecRepoRequest pt : dto.getPortfolioList()) {
            if (StringUtil.isNotEmpty(pt.getFinprodId())) {
                pt.setFinprodId(null);
            }
            Map<String, Object> map = BeanUtil.object2Map(pt);
            map.entrySet().removeIf(item -> !StringUtil.isNotEmpty(item.getValue()));

            bussTrdTransfenpeiDto.add(map);
        }
        d.setBussTrdTransfenpeiDto(bussTrdTransfenpeiDto);
        d.setBusiType("02");
        d.setTradeMarket(dto.getTradeMarket());
        d.setFinprodType2(dto.getFinprodType2());
        d.setTradeDirection(dto.getPs());
        d.setIsEnabled("01");
        d.setAssetCode(dto.getFinprodId());
        d.setAssetType(dto.getFinprodType2());
        d.setVdate(dto.getEntrustDate());
        d.setMdate(dto.getEntrustDate());
        d.setFromEtrExchange("Y");
        d.setCcy(dto.getCcy());
        d.setRepPs(dto.getRepPs());
        d.setAssetType2(dto.getAssetType2());
        d.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        Map<String, Object> dataMap = BeanUtil.object2Map(d);
        return dataMap;
    }

    public String getKriStatusCnByKriStatus(String kriStatus) {
        if (StringUtil.isNotEmpty(kriStatus)) {
            if (StringUtil.equals(kriStatus, "0")) {
                return "正常";
            } else if (StringUtil.equals(kriStatus, "1")) {
                return "预警";
            } else if (StringUtil.equals(kriStatus, "2")) {
                return "审批";
            } else if (StringUtil.equals(kriStatus, "3")) {
                return "违规";
            } else if (StringUtil.equals(kriStatus, "4")) {
                return "异常";
            }
        }
        return null;
    }

    /**
     * 查找组合对应的证券账户
     *
     * @return
     * @author meifaxin
     * @date 2021/12/25 0013
     */
    public List<EtrExchangeTradeSaveSecRepoRequest> selectEtrPortfolioList(EtrExchangeTradeSaveSecRepoRequest dto) {
        return etrExchangeTradeSecRepoMapper.selectEntrustDetail(dto);
    }

    /**
     * 组合分配实体转换成
     *
     * @param dto
     * @param multiPtl001Dto
     * @return
     */
    public EtrExchangeTradeSaveSecRepoRequest genEtrExchangeTradeSaveSecRepoRequest(EtrExchangeTradeSaveSecRepoRequest dto, EtrExchangeTradePortfolioSecRepoRequest multiPtl001Dto) {
        EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = new EtrExchangeTradeSaveSecRepoRequest();
        BeanUtil.copyPropertiesIgnoreNull(etrExchangeTrade001Dto, dto);
        etrExchangeTrade001Dto.setPortfolioId(multiPtl001Dto.getPortfolioId());
        etrExchangeTrade001Dto.setAcctNo(multiPtl001Dto.getAcctNo());
        etrExchangeTrade001Dto.setSecAcctId(multiPtl001Dto.getSecAcctId());
        etrExchangeTrade001Dto.setEntrustAmt(multiPtl001Dto.getEntrustAmt());
        etrExchangeTrade001Dto.setEntrustNum(multiPtl001Dto.getEntrustNum());
        etrExchangeTrade001Dto.setAccAvailableCash(multiPtl001Dto.getAccAvailableCash());
        etrExchangeTrade001Dto.setAccAvailablePos(multiPtl001Dto.getAccAvailablePos());
        etrExchangeTrade001Dto.setTargetType(dto.getTargetType());
        etrExchangeTrade001Dto.setMdate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setVdate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setStockId(dto.getStockId());
        etrExchangeTrade001Dto.setEntrustDate(DateUtil.parse(DateUtil.format(dto.getEntrustDate(), DateUtil.DEFAULT_DATE_FORMAT_EN)));
        etrExchangeTrade001Dto.setOptType(dto.getOptType());

        return etrExchangeTrade001Dto;
    }

    public BaseEtrInvestInfoDto buildMultiEtrFlow(EtrExchangeTradeSaveSecRepoRequest dto) {

        BaseEtrInvestInfoDto baseEtrInvestInfoDto = this.buildEtrFlow(dto);

        EtrEntrust etrEntrust = baseEtrInvestInfoDto.getEtrEntrust();
        if (!CollectionUtils.isEmpty(dto.getPortfolioList())) {
            dto.setSecAcctId(dto.getPortfolioList().get(0).getSecAcctId());
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
                etrEntrustDetail.setPortfolioId(item.getPortfolioId());
                etrEntrustDetail.setEntrustId(etrEntrust.getEntrustId());
                etrEntrustDetail.setEntrustAmt(item.getEntrustAmt());
                etrEntrustDetail.setEntrustNum(item.getEntrustNum());
                etrEntrustDetail.setDisRate(item.getDisRate());
                etrEntrustDetail.setTargetType(dto.getTargetType());
                etrEntrustDetailList.add(etrEntrustDetail);
            }
            baseEtrInvestInfoDto.setEtrEntrustDetailList(etrEntrustDetailList);
        }
        return baseEtrInvestInfoDto;
    }


    public BaseEtrInvestInfoDto buildEtrFlow(EtrExchangeTradeSaveSecRepoRequest dto) {
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
            } else {
                etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            }
        } else {
            etrEntrustAdd.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
        }
        if (DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue().equals(baseDto.getEntrustType())) {
            EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
            BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
            etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100), 8)); // 到期收益率
            etrBondBiddingDto.setFullPrice(dto.getFprice()); // 全价
            etrBondBiddingDto.setCalMode(dto.getCalculateField());// 计算方式
            etrBondBiddingDto.setFinprodType2(dto.getFinprodType2());// 金融产品类型（投管）
            etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
            BeanUtil.copyPropertiesIgnoreNull(etrEntrust, etrBondBiddingDto);
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAdd, etrBondBiddingDto);
        }
        if (DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue().equals(baseDto.getEntrustType())) {
            EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
            etrSinglePortfolioDto.setPrice(ArithmeticUtil.divide(etrSinglePortfolioDto.getPrice(), new BigDecimal(100), 6));
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

    //双击修改check
    public AjaxResult dbClickCheck(EtrExchangeTradeSaveSecRepoRequest dto) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //双击修改check
        if (!StringUtil.equals(dto.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue()) && !StringUtil.equals(dto.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())) {
            String entrustStatusStr = SysDataUtil.getNameFromDataDict(DictCode.委托状态, dto.getEntrustStatus());
            errorList.add(new ValidMessage(entrustStatusStr + "的指令不允许修改!"));
        }else{
            //check流程中是否有数据
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setBusinessDataPk1(dto.getEntrustId());
            if (WorkFlowUtil.isNotFromToDoList() && mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errorList.add(new ValidMessage("所选对象存在流程中的数据不能修改"));
            }
        }
        if (!CollectionUtils.isEmpty(errorList)) {
            webResult.setErrorList(errorList);
        } else {
            return null;
        }
        return webResult;
    }

    public String setEntrustBussType(EtrExchangeTradeSaveSecRepoRequest dto) {
        //业务类型
        dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券现券交易指令.getValue());
        String businessType = DictCodeListExt3.共通业务类型_场内债券现券指令.getValue();
        if (DictCodeListExt3.交易方向_正回购.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_逆回购.getValue().equals(dto.getPs()))
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue());
        return businessType;
    }

    public BussinessCodeParameter genBussinessCodeParameter(EtrExchangeTradeSaveSecRepoRequest dto) {
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_场内债券现券指令));
        return param;
    }

    /**
     * 插入和修改时，指令持仓流水 、现金流水处理,支持多组合
     * add by zhangpei
     * 20201128
     *
     * @param dto
     */
    public void doInsetOrUpdateForEtrPositionHoldMulti(EtrExchangeTradeSaveSecRepoRequest dto) {
        if (StringUtil.equals(dto.getIsMulti(), DictCodeList.是否_是.getValue())) {
            List<EtrExchangeTradeSaveSecRepoRequest> EtrExchangeTrade001DtoList = new ArrayList<>();
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = this.genEtrExchangeTradeSaveSecRepoRequest(dto, item);
                doInsetOrUpdateForEtrPositionHold(etrExchangeTrade001Dto);
            }
        } else {
            doInsetOrUpdateForEtrPositionHold(dto);
        }

    }

    /**
     * 处理履历
     */
    public void updateCfetsOprHis(String entrustId, String optType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        if (ENTRUST_RECORD_NEW.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        } else if (ENTRUST_RECORD_NEW.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        } else if ("close".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
        } else if ("revoke".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        } else if ("export".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_导出.getValue());
        }
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }



    /**
     * 查找组合对应的证券账户
     *
     * @return
     * @author meifaxin
     * @date 2021/12/25 0013
     */
    public List<EtrExchangeTradeSaveSecRepoRequest> selectWflEtrPortfolioList(EtrExchangeTradeSaveSecRepoRequest dto) {
        return etrExchangeTradeSecRepoMapper.selectWflEntrustDetail(dto);
    }

    //来自代办提交封装对象
    public EtrExchangeTradeSaveSecRepoRequest buildDtoFromTodoSubmit(EtrExchangeTradeSaveSecRepoRequest dto) {
        String businessType = setEntrustBussType(dto);
        dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
        Map<String, List<EtrExchangeTradePortfolioSecRepoRequest>> groupMap = dto.getPortfolioList().stream().collect(
                Collectors.groupingBy(item -> item.getAcctNo()));            //如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
        if (groupMap.keySet().size() == 1 && dto.getPortfolioList().size() > 1) {
            EtrExchangeTradePortfolioSecRepoRequest item = dto.getPortfolioList().get(0);
            //业务编号生成规则参数
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = genBussinessCodeParameter(dto);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
            BigDecimal totalEntrustAmt = null;
            if (dto.getPortfolioList().get(0).getEntrustAmt() != null) {
                totalEntrustAmt = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustAmt).reduce(BigDecimal::add).get();
            }
            BigDecimal totalEntrustNum = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustNum).reduce(BigDecimal::add).get();
            //算比例
            BigDecimal tempSumDisRate = BigDecimal.ZERO;
            for (int i = 0; i < dto.getPortfolioList().size(); i++) {
                EtrExchangeTradePortfolioSecRepoRequest tempItem = dto.getPortfolioList().get(i);
                BigDecimal disRate = BigDecimal.ZERO;
                if (i < dto.getPortfolioList().size() - 1) {
                    disRate = ArithmeticUtil.divide(tempItem.getEntrustNum(), totalEntrustNum, 2);
                    tempSumDisRate = tempSumDisRate.add(disRate);
                } else {
                    disRate = ArithmeticUtil.subtract(new BigDecimal("1"), tempSumDisRate);
                }
                tempItem.setDisRate(disRate);
            }
            dto.setEntrustAmt(totalEntrustAmt);
            dto.setEntrustNum(totalEntrustNum);
            dto.setIsMulti(DictCodeList.是否_是.getValue());
            dto.setVdate(dto.getEntrustDate());
            dto.setMdate(dto.getEntrustDate());
            dto.setAcctNo(item.getAcctNo());
            dto.setSecAcctId(item.getSecAcctId());
            dto.setStockId(dto.getStockId());
            dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
            dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
            dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
            dto.setCreateTime(new Date());// 创建时间
            dto.setPortfolioId(item.getPortfolioId()); //让指令分发能查出来
        }
        //多组合指令
        else {
            EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = null;
            for (EtrExchangeTradePortfolioSecRepoRequest item : dto.getPortfolioList()) {
                etrExchangeTrade001Dto = genEtrExchangeTradeSaveSecRepoRequest(dto, item);
                if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                } else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                }
                if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
                    BussinessCodeParameter param = genBussinessCodeParameter(dto);
                    String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                    etrExchangeTrade001Dto.setEntrustId(code);
                }
                etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
                etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
                etrExchangeTrade001Dto.setOptType(ENTRUST_RECORD_NEW);
                etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
                etrExchangeTrade001Dto.setFromEtrExchange("Y");
                etrExchangeTrade001Dto.setDisRate(item.getDisRate());
                List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList = new ArrayList<>();
                EtrExchangeTradePortfolioSecRepoRequest portfolioRequest = new EtrExchangeTradePortfolioSecRepoRequest();
                BeanUtil.copyPropertiesIgnoreNull(portfolioRequest, etrExchangeTrade001Dto);
                portfolioList.add(portfolioRequest);
                etrExchangeTrade001Dto.setPortfolioList(portfolioList);
            }
            dto = etrExchangeTrade001Dto;
        }
        return dto;
    }

    private void doInsertBackupQuery(EtrExchangeTradeSaveSecRepoRequest dto) {
        BaseEtrInvestInfoDto entity = buildMultiEtrFlow(dto);
        WFlowCommonService.inserBackuptWithQuery(entity);
    }


    public List<RiskTrialRetDto> selectRiskList(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradeId", tradeId);
        List<RiskTrialRetDto> list = etrExchangeTradeSecRepoMapper.selectRiskList(map);
        return list;
    }

    public boolean doDeleteEtrFlowMethod(String entrustId) {
        WflEtrEntrustKey wflEtrEntrustKey = new WflEtrEntrustKey();
        wflEtrEntrustKey.setEntrustId(entrustId);
        wflEtrEntrustKey.setWorkflowStatus(DictCodeListExt3.委托状态_流程中.getValue());

        WflEtrEntrustAddKey wflEtrEntrustAddKey = new WflEtrEntrustAddKey();
        wflEtrEntrustAddKey.setEntrustId(entrustId);
        wflEtrEntrustAddKey.setWorkflowStatus(DictCodeListExt3.委托状态_流程中.getValue());

        etrExchangeTradeSecRepoMapper.deleteWflEtrEntrustAddByEntrustId(entrustId);
        etrExchangeTradeSecRepoMapper.deleteWflEtrEntrustByEntrustId(entrustId);
        etrExchangeTradeSecRepoMapper.deleteWflEtrEntrustDetailByEntrustId(entrustId);
        return true;
    }

    /**
     * 修改指令回退时,从废弃表将数据取出重新插入
     *
     * @param entrustId 指令代码
     */
    public void backReRoll(String entrustId) {
        //从废弃表中获取，转移到指令表
        etrEntrustBackupMapper.insertEtrFromQuery(entrustId);
        etrEntrustBackupMapper.insertEtrAddFromQuery(entrustId);
        List<EtrEntrustDetailBackUp> list = etrEntrustDetailBackUpMapper.selectList(entrustId);
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(list)) {
            for (EtrEntrustDetailBackUp etrEntrustDetailBackUp : list) {
                EtrEntrustDetail etrEntrustDetail = new EtrEntrustDetail();
                BeanUtil.copyPropertiesIgnoreNull(etrEntrustDetail, etrEntrustDetailBackUp);
                etrEntrustDetailMapper.insert(etrEntrustDetail);
            }
        }
        // 删除废弃表
        etrEntrustBackupMapper.deleteByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteAddByPrimaryKey(entrustId);
        etrEntrustBackupMapper.deleteDetailByPrimaryKey(entrustId);
    }

    public MinTrdUnitLimitDto getMinTrdUnitLimitPrice(EtrExchangeMiniLimitPriceRequest request) {
        MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(request.getFinprodId(), request.getTradePlat(), request.getTradeMarket(), request.getFinprodType2(), request.getPs());
        return minTrdUnitLimitDto;
    }

}
