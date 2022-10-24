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

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 场内指令共通审批
 *
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon004FJYYWController
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeCommon004FJYYW")
@RequestMapping("/etrexchange/common/EtrExchangeTradeCommon004FJYYW")
@Api(tags = "场内交易-指令下单共通审批接口")
public class EtrExchangeTradeCommon004FJYYWController extends BaseController {

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTrade001CommonService;

    public static final String ENTRUST_RECORD_NEW = "new";

    public static final String ENTRUST_RECORD_MODIFY = "modify";
    @Value("${quote.flag:false}")
    private boolean useInterfQuoteFlag;

    @Value("${quote.ws.host}")
    private String wsHost;

    @Value("${quote.ws.url}")
    private String wsUrl;

    @Autowired
    private EtrExchangeTradeCommon004Controller etrExchangeTradeCommon004Controller;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所, DictCode.目标类型, DictCode.申报类型, DictCode.业务模式);
        etrExchangeTradeCommon004Controller.setInvAimSelectOption(model);
        etrExchangeTradeCommon004Controller.setPsSelectOptions(model);
        etrExchangeTradeCommon004Controller.setPriceModelSelectOptions(model, null);
        etrExchangeTradeCommon004Controller.setDeclaTypeSelectOptions(model);
        // 设置固定的数据字典明细参数
        etrExchangeTradeCommon004Controller.setConstSelectOption(model);
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
                etrExchangeTradeCommon004Controller.setPriceModelSelectOptions(model, Boolean.TRUE);
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
                entity.setRiskTrialRetDto(riskTrialRetList);
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
            if(StringUtil.equals(entity.getPs(),DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtil.equals(entity.getPs(),DictCodeListExt3.交易方向_质押券出库.getValue())){
                model.addAttribute("BOND_SCALE", 0);
            }else {
                model.addAttribute("BOND_SCALE", entity.getEntrustPrice().scale());
            }
        } else {
            model.addAttribute("BOND_SCALE", 3);
        }
        if (null != entity && null != entity.getCalculateField()) {
            model.addAttribute("calculateField", entity.getCalculateField());
        }
        //最小价差List
        List<MinPirceDto> minPriceWhole = etrExchangeTradeCommon004Controller.getInterfSysMinPriceAll(new HashMap<>());
        model.addAttribute("minPriceWhole", minPriceWhole);
        model.addAttribute("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        model.addAttribute("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        model.addAttribute("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        model.addAttribute("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());

        //清算速度
        etrExchangeTradeCommon004Controller.setSettleSpreedSelectOptions(model);
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

}

