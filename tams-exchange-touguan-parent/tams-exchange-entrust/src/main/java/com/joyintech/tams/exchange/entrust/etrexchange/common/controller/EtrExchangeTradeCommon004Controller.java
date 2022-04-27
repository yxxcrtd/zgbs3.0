/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.common.aop.ExchangeEntrustRdsCommonAop;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.service.EtrExchangeBlockTrade001ValidatorService;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePortfolioRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommonValidatorService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场内指令共通审批
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon004Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeCommon004")
@RequestMapping("/etrexchange/common/EtrExchangeTradeCommon004")
@Api(tags = "场内交易-指令下单共通审批接口")
public class EtrExchangeTradeCommon004Controller extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTrade001CommonService;

    @Autowired
    private BusiFinExercisePlanService busiFinExercisePlanService;

    public static final String ENTRUST_RECORD_NEW = "new";

    public static final String ENTRUST_RECORD_MODIFY = "modify";
    @Value("${quote.flag:false}")
    private boolean useInterfQuoteFlag;

    @Value("${quote.ws.host}")
    private String wsHost;

    @Value("${quote.ws.url}")
    private String wsUrl;

    @Autowired
    private EtrExchangeTradeCommonMapper etrExchangeTradeCommonMapper;

    @Autowired
    private EtrExchangeTradeCommonValidatorService etrExchangeTradeCommonValidatorService;

    @Autowired
    private EtrExchangeBlockTrade001ValidatorService etrExchangeBlockTrade001ValidatorService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所, DictCode.目标类型, DictCode.申报类型, DictCode.业务模式);
        this.setInvAimSelectOption(model);
        this.setPsSelectOptions(model);
        this.setPriceModelSelectOptions(model, null);
        this.setDeclaTypeSelectOptions(model);
        // 设置固定的数据字典明细参数
        this.setConstSelectOption(model);
        Date platformDate = DateUtil.platDateFromCache();
        model.addAttribute("iPlatformDate", DateUtil.format(platformDate));

        //配置单位 add by yeming 20210728 start
        // moddify by etliu 20220301 bond_traded_unit系统参数删除，默认张 start
//        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
//        model.addAttribute("bond_traded_unit", bondTradedUnit );
        model.addAttribute("bond_traded_unit", BussinessConstant.BOND_TRADED_UNIT_DEFAULT);
        // moddify by etliu 20220301 end
        //配置单位 add by yeming 20210728 end

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("workflowId", workflowId);

        //委托代码
        String entrustId = "null".equals(getParam("entrustId")) ? null : getParam("entrustId");
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());

        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        model.addAttribute("parentPageModel", parentPageModel);
        //是否_是
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());

        String isFromToDoList = getParam("isFromToDoList");
        EtrExchangeTradeSaveRequest entity = new EtrExchangeTradeSaveRequest();
        if (StringUtil.isNotEmpty(workflowId)) {
            //流程代办checkImportData
            entity = etrExchangeTrade001CommonService.getBussinessEntityByWorkflowId(workflowId);
            if (entity.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                this.setPriceModelSelectOptions(model, Boolean.TRUE);
            }
            model.addAttribute("ps", entity.getPs());
            //add by chenjiaxing 流程中不显示资产名称 start
            String[] secIds = StringUtil.split(entity.getFinprodId(), "_");
            if (secIds.length == 2 && (StringUtil.isEmpty(entity.getStockId()))) {
                entity.setStockId(secIds[1]);
            }
            if (StringUtil.isNotEmpty(entity.getRiskTrialRetDto())) {
                model.addAttribute("entity_RiskTrialRetList", entity.getRiskTrialRetDto());
                boolean riskTrialFlag = true;
                for (RiskTrialRetDto riskTrialRetDto : entity.getRiskTrialRetDto()) {
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus())
                            && riskTrialRetDto.getKriStatus().equals("3"))
                            && (StringUtil.isNotEmpty(riskTrialRetDto.getKriTrend())
                            && riskTrialRetDto.getKriTrend().equals("向坏"))) {
                        riskTrialFlag = false;
                    }
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus())
                            && riskTrialRetDto.getKriStatus().equals("3"))
                            && !StringUtil.isNotEmpty(riskTrialRetDto.getKriTrend())) {
                        riskTrialFlag = false;
                    }
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus())
                            && riskTrialRetDto.getKriStatus().equals("4"))) {
                        riskTrialFlag = false;
                    }
                }
                model.addAttribute("riskTrialSpan", riskTrialFlag ? "试算通过" : "试算不通过");
            } else {
                String entrustIdWfl = entity.getEntrustId();
                List<RiskTrialRetDto> riskTrialRetList;
                if (StringUtil.equals(entity.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
                    entrustIdWfl = entrustIdWfl + DictCodeList.交易方向_卖出.getValue();
                    riskTrialRetList = etrExchangeTrade001CommonService.selectRiskList(entrustIdWfl);
                    entrustIdWfl = entity.getEntrustId() + DictCodeList.交易方向_买入.getValue();
                    riskTrialRetList.addAll(etrExchangeTrade001CommonService.selectRiskList(entrustIdWfl));
                } else {
                    riskTrialRetList = etrExchangeTrade001CommonService.selectRiskList(entrustIdWfl);
                }
                boolean riskTrialFlag = true;
                for (RiskTrialRetDto riskTrialRetDto : riskTrialRetList) {
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus()) && riskTrialRetDto.getKriStatus().equals("3")) && (StringUtil.isNotEmpty(riskTrialRetDto.getKriTrend()) && riskTrialRetDto.getKriTrend().equals("向坏"))) {
                        riskTrialFlag = false;
                    }
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus())
                            && riskTrialRetDto.getKriStatus().equals("3"))
                            && !StringUtil.isNotEmpty(riskTrialRetDto.getKriTrend())) {
                        riskTrialFlag = false;
                    }
                    if ((StringUtil.isNotEmpty(riskTrialRetDto.getKriStatus())
                            && riskTrialRetDto.getKriStatus().equals("4"))) {
                        riskTrialFlag = false;
                    }
                }
                model.addAttribute("riskTrialSpan", riskTrialFlag ? "试算通过" : "试算不通过");
                model.addAttribute("entity_RiskTrialRetList", riskTrialRetList);
            }
            //add by chenjiaxing 流程中不显示资产名称 end
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("entity_BussCombDistribtList", JsonUtils.object2Json(entity.getPortfolioList()));

        } else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
            //查看
            entity = etrExchangeTrade001CommonService.getEtrExchangeTradeInfo(entrustId);
        } else {
            //新建页面初始化
            entity.setEntrustDate(platformDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
        }

        model.addAttribute("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        model.addAttribute("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        // 根据价格模式确定当前的数据来源
        if (entity != null && StringUtil.equals(entity.getPriceModel(), DictCodeListExt3.申报类型_点击成交申报.getValue())) {
            entity.setDataSource("01");
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);
        if (null != entity && null != entity.getEntrustPrice()) {
            model.addAttribute("BOND_SCALE", entity.getEntrustPrice().scale());
        } else {
            model.addAttribute("BOND_SCALE", 3);
        }
        if (null != entity && null != entity.getCalculateField()) {
            model.addAttribute("calculateField", entity.getCalculateField());
        }
        //最小价差List
        List<MinPirceDto> minPriceWhole = getInterfSysMinPriceAll(new HashMap<>());
        model.addAttribute("minPriceWhole", minPriceWhole);
        model.addAttribute("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        model.addAttribute("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        model.addAttribute("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        model.addAttribute("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());

        //清算速度
        setSettleSpreedSelectOptions(model);
        model.addAttribute("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        model.addAttribute("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
        model.addAttribute("isRiskmathSoftcontrol", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));


        // 场内交易指令画面行情区域自动刷新时间（毫秒）
        long etrExchange_HqArea_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal) && !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD, tempSysParaVal)) {
            try {
                etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
            } catch (Exception e) {
            }
        }
        model.addAttribute("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);

        model.addAttribute("useInterfQuoteFlag", useInterfQuoteFlag);
        model.addAttribute("wsUri", wsHost + wsUrl);

        model.addAttribute("currentTab", "jj");
        if (StringUtil.isNotEmpty(entity.getTradePlat()) && entity.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
            model.addAttribute("currentTab", "dz");
        }
        if (StringUtil.isNotEmpty(entity.getTradePlat()) && entity.getTradePlat().equals(DictCodeList.交易平台_固定收益平台.getValue())) {
            model.addAttribute("currentTab", "gd");
        }
        model.addAttribute("ETR_CHECK_POS_SWITCH", CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH));
        return super.init(model);
    }

    /**
     * 设置投资目的
     *
     * @param model
     */
    private void setInvAimSelectOption(Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
        List<SelectOptionDto> invAimList = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_摊余成本类.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(invAimList);
        model.addAttribute("INV_AIM", JsonUtils.objectArray2Json(invAimList.toArray()));
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param model Model
     */
    private void setSettleSpreedSelectOptions(Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.清算速度, null);
        // 固定收益
        List<SelectOptionDto> settleSpreed_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.清算速度_T_0.getValue()) || StringUtil.equals(option.getId(), DictCodeList.清算速度_T_1.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("ACCOUNT_SPEED_GD", JsonUtils.objectArray2Json(settleSpreed_gd.toArray()));
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param model Model
     */
    private void setPsSelectOptions(Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        // 竞价平台
        List<SelectOptionDto> ps_jj = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_行权_回售.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt2.交易方向_配股.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_配债.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_债转股.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券出库.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("PS_JJ", JsonUtils.objectArray2Json(ps_jj.toArray()));
        // 大宗平台
        List<SelectOptionDto> ps_dz = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("PS_DZ", JsonUtils.objectArray2Json(ps_dz.toArray()));
        // 固定收益
        List<SelectOptionDto> ps_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("PS_GD", JsonUtils.objectArray2Json(ps_gd.toArray()));
    }

    /**
     * 价格模式
     *
     * @param model
     * @param isF10
     */
    private void setPriceModelSelectOptions(Model model, Boolean isF10) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.价格模式, null);
        List<SelectOptionDto> priceModel_jj = selectDto.getOptions().stream().filter(option ->
                StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_指定价格.getValue())
                        || (!Objects.isNull(isF10) && isF10 && StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()))
        ).collect(Collectors.toList());
        model.addAttribute("PRICE_MODEL_JJ", JsonUtils.objectArray2Json(priceModel_jj.toArray()));

        List<SelectOptionDto> priceModel = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("PRICE_MODEL_DZ", JsonUtils.objectArray2Json(priceModel.toArray()));
        model.addAttribute("PRICE_MODEL_GD", JsonUtils.objectArray2Json(priceModel.toArray()));
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的申报类型
     *
     * @param model Model
     */
    private void setDeclaTypeSelectOptions(Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.申报类型, null);
        // 大宗平台
        List<SelectOptionDto> declaType_dz = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_定价申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("DECLA_TYPE_DZ", JsonUtils.objectArray2Json(declaType_dz.toArray()));
        // 固定收益
        List<SelectOptionDto> declaType_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        }).collect(Collectors.toList());
        model.addAttribute("DECLA_TYPE_GD", JsonUtils.objectArray2Json(declaType_gd.toArray()));
    }

    /**
     * 设置画面的一些固定的数据字典明细
     *
     * @param model Model
     */
    private void setConstSelectOption(Model model) {
        // 金融产品类型(投管用)
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        model.addAttribute("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
        // 交易方向
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        model.addAttribute("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        model.addAttribute("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        model.addAttribute("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        model.addAttribute("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        model.addAttribute("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        model.addAttribute("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        // 价格模式
        model.addAttribute("PRICE_MODEL_PRM02", DictCodeListExt2.价格模式_限价指令.getValue());
        model.addAttribute("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
        model.addAttribute("PRICE_MODEL_PRM10", DictCodeListExt3.申报类型_收盘价固定价格申报.getValue());
        model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
        model.addAttribute("DECLA_TYPE_PRM06", DictCodeListExt3.申报类型_意向申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM07", DictCodeListExt3.申报类型_定价申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM08", DictCodeListExt3.申报类型_点击成交申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM09", DictCodeListExt3.申报类型_成交申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM10", DictCodeListExt3.申报类型_收盘价固定价格申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM11", DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM12", DictCodeListExt3.申报类型_最优价成交申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM13", DictCodeListExt3.申报类型_可转换成交申报.getValue());
        model.addAttribute("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        // 交易场所
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        // 债券计算模式
        model.addAttribute("SEC_CALC_MODE_01", DictCodeListExt3.债券计算模式_单位净价.getValue());
        model.addAttribute("SEC_CALC_MODE_02", DictCodeListExt3.债券计算模式_单位全价.getValue());
        model.addAttribute("SEC_CALC_MODE_03", DictCodeListExt3.债券计算模式_到期收益率.getValue());
        // 指令业务类型
        model.addAttribute("ENTRUST_BUSS_TYPE_01", DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_02", DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_03", DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_04", DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_05", DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_06", DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_07", DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_08", DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_09", DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_10", DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_11", DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_12", DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
        model.addAttribute("ENTRUST_BUSS_TYPE_13", DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
        // 目标类型
        model.addAttribute("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        model.addAttribute("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());

        // 交易平台
        model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_05", DictCodeList.交易平台_综合协议交易平台.getValue());

        // 金融产品其他代码类型
        model.addAttribute("FINPROD_OTH_CODE_TYPE_06", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
        model.addAttribute("FINPROD_OTH_CODE_TYPE_05", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());

        // add by etliu 20211013 start
        // TISHKS-5 bug修复：在场内下指令时，资产选了万科之后，出现申购选项，但理论上不应该出现(万科很早就上市了)，我在申报信息维护中也没有看到万科的新股申报
        model.addAttribute("FINPROD_OTH_CODE_TYPE_07", DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
        // add by etliu 20211013 end

        // 金融产品其他代码类型
        model.addAttribute("WINDTYPEONE_B07", DictCodeList.WIND一级分类_含债券一级分类等_公司债.getValue());
        model.addAttribute("WINDTYPEONE_B13", DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue());
        model.addAttribute("WINDTYPEONE_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        model.addAttribute("WINDTYPEONE_B15", DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());

        //上市板
        model.addAttribute("BOARD_BD03", DictCodeList.上市板_科创板.getValue());
        model.addAttribute("BOARD_BD04", DictCodeList.上市板_创业板.getValue());

    }

    @ApiOperation(value = "下单查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        initPageParams.put("INV_AIM", this.getSelectDataFromDataDic(DictCode.投资目的, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("TARGET_CLASS", this.getSelectDataFromDataDic(DictCode.目标类型, null));
        initPageParams.put("DECLA_TYPE", this.getSelectDataFromDataDic(DictCode.申报类型, null));
        initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
        this.setPsSelectOptions(initPageParams);
        this.setPriceModelSelectOptions(initPageParams);
        this.setDeclaTypeSelectOptions(initPageParams);
        // 设置固定的数据字典明细参数
        this.setConstSelectOption(initPageParams);
        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        //配置单位 add by yeming 20210728 start
        // moddify by etliu 20220301 bond_traded_unit系统参数删除，默认张 start
//        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
//        initPageParams.put("bond_traded_unit", bondTradedUnit );
        initPageParams.put("bond_traded_unit", BussinessConstant.BOND_TRADED_UNIT_DEFAULT);
        // moddify by etliu 20220301 end
        //配置单位 add by yeming 20210728 end

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
        initPageParams.put("workflowId", workflowId);

        //委托代码
        String entrustId = "null".equals(getParam("entrustId")) ? null : getParam("entrustId");
        initPageParams.put("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());

        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        initPageParams.put("parentPageModel", parentPageModel);
        //是否_是
        initPageParams.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());

        String isFromToDoList = getParam("isFromToDoList");
        EtrExchangeTradeSaveRequest entity = new EtrExchangeTradeSaveRequest();
        if (StringUtil.isNotEmpty(workflowId)) {
            //流程代办checkImportData
            entity = etrExchangeTrade001CommonService.getBussinessEntityByWorkflowId(workflowId);
            //add by chenjiaxing 流程中不显示资产名称 start
            String[] secIds = StringUtil.split(entity.getFinprodId(), "_");
            if (secIds != null) {
                if (secIds.length == 2 && StringUtil.isEmpty(entity.getStockId())) {
                    entity.setStockId(secIds[1]);
                }
            }
            //add by chenjiaxing 流程中不显示资产名称 end
            initPageParams.put(GlobalConstant.ENTITY, entity);
            initPageParams.put("workflowId", workflowId);
        } else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
            //查看
            entity = etrExchangeTrade001CommonService.getEtrExchangeTradeInfo(entrustId);
        } else {
            //新建页面初始化
            entity.setEntrustDate(platformDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
        }

        initPageParams.put("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        initPageParams.put("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        // 根据价格模式确定当前的数据来源
        if (entity != null && StringUtil.equals(entity.getPriceModel(), DictCodeListExt3.申报类型_点击成交申报.getValue())) {
            entity.setDataSource("01");
        }
        initPageParams.put(GlobalConstant.ENTITY, entity);
        if (null != entity && null != entity.getEntrustPrice()) {
            initPageParams.put("BOND_SCALE", entity.getEntrustPrice().scale());
        } else {
            initPageParams.put("BOND_SCALE", 3);
        }
        if (null != entity && null != entity.getCalculateField()) {
            initPageParams.put("calculateField", entity.getCalculateField());
        }
        //最小价差List
        List<MinPirceDto> minPriceWhole = getInterfSysMinPriceAll(new HashMap<>());
        initPageParams.put("minPriceWhole", minPriceWhole);
        initPageParams.put("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        initPageParams.put("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        initPageParams.put("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        initPageParams.put("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());

        //清算速度
        setSettleSpreedSelectOptions(initPageParams);
        initPageParams.put("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
        initPageParams.put("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        initPageParams.put("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);

        // 场内交易指令画面行情区域自动刷新时间（毫秒）
        long etrExchange_HqArea_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal) && !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD, tempSysParaVal)) {
            try {
                etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
            } catch (Exception e) {
            }
        }
        initPageParams.put("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param initPageParams initPageParams
     */
    private void setSettleSpreedSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.清算速度, null);
        // 固定收益
        List<SelectOptionDto> settleSpreed_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.清算速度_T_0.getValue()) || StringUtil.equals(option.getId(), DictCodeList.清算速度_T_1.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("ACCOUNT_SPEED_GD", JsonUtils.objectArray2Json(settleSpreed_gd.toArray()));
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param initPageParams initPageParams
     */
    private void setPsSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        // 竞价平台
        List<SelectOptionDto> ps_jj = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_行权_回售.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt2.交易方向_配股.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_配债.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_债转股.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券出库.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PS_JJ", JsonUtils.objectArray2Json(ps_jj.toArray()));
        // 大宗平台
        List<SelectOptionDto> ps_dz = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PS_DZ", JsonUtils.objectArray2Json(ps_dz.toArray()));
        // 固定收益
        List<SelectOptionDto> ps_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PS_GD", JsonUtils.objectArray2Json(ps_gd.toArray()));
    }

    /**
     * 价格模式
     *
     * @param initPageParams initPageParams
     */
    private void setPriceModelSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.价格模式, null);
//		List<SelectOptionDto> priceModel_jj = selectDto.getOptions().stream().filter(option -> {
//			return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue())
//					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue())
//					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_指定价格.getValue());
//		}).collect(Collectors.toList());


        List<SelectOptionDto> priceModel = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PRICE_MODEL_JJ", JsonUtils.objectArray2Json(priceModel.toArray()));
        initPageParams.put("PRICE_MODEL_DZ", JsonUtils.objectArray2Json(priceModel.toArray()));
        initPageParams.put("PRICE_MODEL_GD", JsonUtils.objectArray2Json(priceModel.toArray()));
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的申报类型
     *
     * @param initPageParams initPageParams
     */
    private void setDeclaTypeSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.申报类型, null);
        // 大宗平台
        List<SelectOptionDto> declaType_dz = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_定价申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("DECLA_TYPE_DZ", JsonUtils.objectArray2Json(declaType_dz.toArray()));
        // 固定收益
        List<SelectOptionDto> declaType_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("DECLA_TYPE_GD", JsonUtils.objectArray2Json(declaType_gd.toArray()));
    }

    /**
     * 设置画面的一些固定的数据字典明细
     *
     * @param initPageParams initPageParams
     */
    private void setConstSelectOption(Map<String, Object> initPageParams) {
        // 金融产品类型(投管用)
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        initPageParams.put("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
        // 交易方向
        initPageParams.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        initPageParams.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        initPageParams.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        initPageParams.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        initPageParams.put("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
        initPageParams.put("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
        initPageParams.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        initPageParams.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        initPageParams.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        initPageParams.put("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
        initPageParams.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        // 价格模式
        initPageParams.put("PRICE_MODEL_PRM02", DictCodeListExt2.价格模式_限价指令.getValue());
        initPageParams.put("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
        initPageParams.put("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        initPageParams.put("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
        initPageParams.put("DECLA_TYPE_PRM06", DictCodeListExt3.申报类型_意向申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM07", DictCodeListExt3.申报类型_定价申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM08", DictCodeListExt3.申报类型_点击成交申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM09", DictCodeListExt3.申报类型_成交申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM10", DictCodeListExt3.申报类型_收盘价固定价格申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM11", DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM12", DictCodeListExt3.申报类型_最优价成交申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM13", DictCodeListExt3.申报类型_可转换成交申报.getValue());
        initPageParams.put("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        // 交易场所
        initPageParams.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        initPageParams.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        // 债券计算模式
        initPageParams.put("SEC_CALC_MODE_01", DictCodeListExt3.债券计算模式_单位净价.getValue());
        initPageParams.put("SEC_CALC_MODE_02", DictCodeListExt3.债券计算模式_单位全价.getValue());
        initPageParams.put("SEC_CALC_MODE_03", DictCodeListExt3.债券计算模式_到期收益率.getValue());
        // 指令业务类型
        initPageParams.put("ENTRUST_BUSS_TYPE_01", DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_02", DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_03", DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_04", DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_05", DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_06", DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_07", DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_08", DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_09", DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_10", DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_11", DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_12", DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_13", DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
        // 目标类型
        initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        initPageParams.put("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());
        // 交易平台
        initPageParams.put("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        initPageParams.put("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
        initPageParams.put("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        initPageParams.put("SHARES_TRADE_PLATFORM_05", DictCodeList.交易平台_综合协议交易平台.getValue());
        // 金融产品其他代码类型
        initPageParams.put("FINPROD_OTH_CODE_TYPE_06", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
        initPageParams.put("FINPROD_OTH_CODE_TYPE_05", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
        initPageParams.put("FINPROD_OTH_CODE_TYPE_07", DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
        // 金融产品其他代码类型
        initPageParams.put("WINDTYPEONE_B07", DictCodeList.WIND一级分类_含债券一级分类等_公司债.getValue());
        initPageParams.put("WINDTYPEONE_B13", DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue());
        initPageParams.put("WINDTYPEONE_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        initPageParams.put("WINDTYPEONE_B15", DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
    }

    @ResponseBody
    @RequestMapping("/getFinprodMarketInfo")
    public Page<SelectOptionDto> getFinprodMarketInfo() {
        return etrExchangeTrade001CommonService.getFinprodMarketInfo(RequestUtil.getPageParam());
    }

    public List<MinPirceDto> getInterfSysMinPriceAll(@RequestBody Map<String, Object> param) {
        List<MinPirceDto> minPirceList = etrExchangeTrade001CommonService.getInterfSysMinPriceAll(param);
        return minPirceList;
    }

    @ResponseBody
    @RequestMapping("/getPtlSecAcctInfo")
    public List<SelectOptionDto> getPtlSecAcctInfo(@RequestParam Map<String, Object> param) {
        return etrExchangeTrade001CommonService.getPtlSecAcctInfo(param);
    }

    @ResponseBody
    @RequestMapping("/getPtlSecAcctInfoJson")
    public List<SelectOptionDto> getPtlSecAcctInfoJson(@RequestBody Map<String, Object> param) {
        return etrExchangeTrade001CommonService.getPtlSecAcctInfo(param);
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "保存提交check", notes = "保存提交check")
    @PostMapping("checkSaveError")
    @ResponseBody
    public AjaxResult checkSaveError(@RequestBody @Validated({ValidateGroupEdit.class}) EtrExchangeTradeSaveRequest dto, BindingResult result) throws Exception {
        AjaxResult ajax = new AjaxResult();
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue()) || StringUtil.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue()) || StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
            dto.setVdate(dto.getEntrustDate());
            dto.setMdate(dto.getEntrustDate());
            ajax = check(dto, result);
            return ajax;
        }

        List<EtrExchangeTradePortfolioRequest> portfolioList = dto.getPortfolioList();
        if (!CollectionUtils.isEmpty(portfolioList)) {
            for (int i = 0; i < portfolioList.size(); i++) {
                EtrExchangeTradePortfolioRequest item = portfolioList.get(i);
                dto.setVdate(dto.getEntrustDate());
                dto.setMdate(dto.getEntrustDate());
                dto.setPortfolioId(item.getPortfolioId());
                dto.setAcctNo(item.getAcctNo());
                dto.setEntrustNum(item.getEntrustNum());
                dto.setEntrustAmt(item.getEntrustAmt());
                //默认证券账户
                if (StringUtil.isNotEmpty(item.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", item.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    map.put("tradePlat", dto.getTradeMarket());
                    String secAcctId = etrExchangeTradeCommonMapper.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        dto.setSecAcctId(secAcctId); // 证券账户
                        dto.setStockId(secAcctId);
                    }
                }
                ajax = check(dto, result);
                if (null != ajax) {
                    break;
                }
            }
        }
        return ajax == null || CollectionUtils.isEmpty(ajax.getErrorList()) ?
                commonCheck(result, dto.getOperateType()): ajax;
    }

    /**
     * 单组合保存按钮点击时的check
     *
     * @param dto
     * @return check结果
     * @see
     * @since 1.0
     */
    public AjaxResult check(EtrExchangeTradeSaveRequest dto, BindingResult result) throws Exception {
        AjaxResult ajax = etrExchangeTrade001CommonService.checkSave(dto, result);
        List<ValidMessage> validList = null;
        if (StringUtil.equals(dto.getCurrentTab(),"dz")){
            validList = etrExchangeBlockTrade001ValidatorService.valid(dto);
        }else{
            validList = etrExchangeTradeCommonValidatorService.valid(dto);
        }
        if (!CollectionUtils.isEmpty(validList)) {
            ajax.setErrorList(validList);
        }
        return ajax;
    }

    /**
     * 保存/提交
     *
     * @param dto dto
     * @param br  br
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("save")
    @ResponseBody
    public AjaxResult save(@RequestBody EtrExchangeTradeSaveRequest dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        String msg = "";
        String businessType = etrExchangeTrade001CommonService.setEntrustBussType(dto);
        dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
        etrExchangeTrade001CommonService.genBusiTypeTg(dto);

        if (StringUtil.equals("save", dto.getOperateType())) {  //保存没有check和任何限制
            //业务编号生成规则参数
            ProcessParameter<EtrExchangeTradeSaveRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveRequest>();
            WorkFlowParameter<EtrExchangeTradeSaveRequest> params = etrExchangeTrade001CommonService.getWorkFlowParameter(dto);
            params.setPageId(dto.getPageId());
            params.setElementId(dto.getElementId());
            params.setBusinessType(businessType);
            params.setBusinessCode(dto.getEntrustId());
            String entrustIdName = StringUtils.isNotEmpty(dto.getEntrustId()) ? dto.getEntrustId() : "";
            params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entrustIdName + ")" + "-" + "业务类型" + "(" + entity.getBusinessTypeName() + ")-" + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            msg = etrExchangeTrade001CommonService.start(processParameter, dto);
            return new AjaxResult(msg);
        }
        Map<String, List<EtrExchangeTradePortfolioRequest>> groupMap = dto.getPortfolioList().stream().collect(Collectors.groupingBy(item -> item.getAcctNo()));            //如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
        if (groupMap.keySet().size() == 1 && dto.getPortfolioList().size() > 1) {
            EtrExchangeTradePortfolioRequest item = dto.getPortfolioList().get(0);
            //业务编号生成规则参数
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = etrExchangeTrade001CommonService.genBussinessCodeParameter(dto);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
            BigDecimal totalEntrustAmt = null;
            if (dto.getPortfolioList().get(0).getEntrustAmt() != null) {
                totalEntrustAmt = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioRequest::getEntrustAmt).reduce(BigDecimal::add).get();
            }
            BigDecimal totalEntrustNum = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioRequest::getEntrustNum).reduce(BigDecimal::add).get();
            //算比例
            BigDecimal tempSumDisRate = BigDecimal.ZERO;
            for (int i = 0; i < dto.getPortfolioList().size(); i++) {
                EtrExchangeTradePortfolioRequest tempItem = dto.getPortfolioList().get(i);
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
            // ----------add by zhangrui1 20200703 预占功能 start----------
            if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
                String finprodType2 = "";
                if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    finprodType2 = DictCodeList.平安资产类型_债券.getValue();
                } else if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    finprodType2 = DictCodeList.平安资产类型_基金.getValue();
                } else if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    finprodType2 = DictCodeListExt2.平安资产类型_股票.getValue();
                } else if ((dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue()))) {
                    finprodType2 = DictCodeList.平安资产类型_质押式回购.getValue();
                }
                //债转股需要走批量接口
                String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                dto.setFromEtrExchange("Y");//场内指令
                AjaxResult ar = ExchangeEntrustRdsCommonAop.doPreOccupy(dto, infCode, finprodType2);
                if (StringUtil.isNotEmpty(ar)) {
                    return ar;
                }
            }
            ProcessParameter<EtrExchangeTradeSaveRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveRequest>();
            WorkFlowParameter<EtrExchangeTradeSaveRequest> params = new WorkFlowParameter<EtrExchangeTradeSaveRequest>();
            params.setPageId(dto.getPageId());
            params.setElementId(dto.getElementId());
            params.setBusinessType(businessType);
            params.setBusinessCode(dto.getEntrustId());
            params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")" + "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-" + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            msg = etrExchangeTrade001CommonService.start(processParameter, dto);
        }
        //多组合指令
        else {
            List<EtrExchangeTradeSaveRequest> etrExchangeTrade001DtoList = new ArrayList<>();
            for (EtrExchangeTradePortfolioRequest item : dto.getPortfolioList()) {
                EtrExchangeTradeSaveRequest etrExchangeTrade001Dto = etrExchangeTrade001CommonService.genEtrExchangeTradeSaveRequest(dto, item);
                if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                } else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
                    etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                }
                if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
                    BussinessCodeParameter param = etrExchangeTrade001CommonService.genBussinessCodeParameter(dto);
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
                List<EtrExchangeTradePortfolioRequest> portfolioList = new ArrayList<>();
                EtrExchangeTradePortfolioRequest portfolioRequest = new EtrExchangeTradePortfolioRequest();
                BeanUtil.copyPropertiesIgnoreNull(portfolioRequest, etrExchangeTrade001Dto);
                portfolioList.add(portfolioRequest);
                etrExchangeTrade001Dto.setPortfolioList(portfolioList);

                etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
            }

            // ----------预占功能 start----------
            if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
                // add by zhangrui1 start
                AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", etrExchangeTrade001DtoList, BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
                if (null != ar) {
                    return ar;
                }
                ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList, (etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
            }
            // ----------预占功能 end----------

            ProcessParameter<EtrExchangeTradeSaveRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveRequest>();
            WorkFlowParameter<EtrExchangeTradeSaveRequest> params = etrExchangeTrade001CommonService.getWorkFlowParameter(dto);
            params.setPageId(dto.getPageId());
            params.setElementId(dto.getElementId());
            params.setBusinessType(businessType);
            params.setBusinessCode(item -> item.getEntrustId());
            params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")" + "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-" + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            msg = etrExchangeTrade001CommonService.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrExchangeTradeSaveRequest[etrExchangeTrade001DtoList.size()]));
        }
        return new AjaxResult(msg);
    }

    /**
     * 保存/提交
     *
     * @param dto dto
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("buildDtoFromTodoSubmit")
    @ResponseBody
    public EtrExchangeTradeSaveRequest buildDtoFromTodoSubmit(@RequestBody EtrExchangeTradeSaveRequest dto) {
        return etrExchangeTrade001CommonService.buildDtoFromTodoSubmit(dto);
    }


}

