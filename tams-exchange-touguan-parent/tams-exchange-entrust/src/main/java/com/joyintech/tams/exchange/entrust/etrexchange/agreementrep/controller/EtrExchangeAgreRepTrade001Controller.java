package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.controller;

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
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.component.base.core.MstWorkflowCommonService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade002Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommonValidatorService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@PageId("EtrExchangeAgreRepTrade001")
@RequestMapping("/etrexchange/agreementrep/EtrExchangeAgreRepTrade001")
@Api(tags = "场内质押式协议回购指令下单")
public class EtrExchangeAgreRepTrade001Controller extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;


    @Autowired
    private EtrExchangeAgreRepTrade001Service etrExchangeAgreRepTrade001Service;

    @Autowired
    private EtrExchangeAgreRepTrade002Service etrExchangeAgreRepTrade002Service;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

    @Autowired
    private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private EtrExchangeTradeCommonValidatorService etrExchangeTradeCommonValidatorService;

    /**
     * 流程组件
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;

    public static final String ENTRUST_RECORD_NEW = "new";

    public static final String ENTRUST_RECORD_MODIFY = "modify";

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.交易场所, DictCode.回购拆借方向, DictCode.交易单位);


        Date platformDate = DateUtil.platDateFromCache();
        model.addAttribute("iPlatformDate", DateUtil.format(platformDate));

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("workflowId", workflowId);
        String entrustId = getParam("entrustId");

        //委托代码
        //  String entrustId = "null".equals(getParam("entrustId"))?null:getParam("entrustId");
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());

        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        model.addAttribute("parentPageModel", parentPageModel);

        EtrExchangeAgreRepTrade001Dto entity = new EtrExchangeAgreRepTrade001Dto();
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            //流程代办
            entity = etrExchangeAgreRepTrade001Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
        } else if (StringUtil.isNotEmpty(entrustId)) {
            entity = etrExchangeAgreRepTrade001Service.getEntrustInfoByEntrustId(RequestUtil.getPageParam());
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("entityStr", JsonUtils.object2Json(entity));
        }
        model.addAttribute("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        model.addAttribute("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        model.addAttribute("TRADE_MARKET_04", DictCodeList.交易流通场所_深圳证券交易所.getValue());
        model.addAttribute("TRADE_MARKET_03", DictCodeList.交易流通场所_上海证券交易所.getValue());

        model.addAttribute("UNIT_01", DictCodeListExt3.交易单位_张.getValue());
        model.addAttribute("UNIT_04", DictCodeListExt3.交易单位_手.getValue());
        return super.init(model);
    }


    @ApiOperation(value = "下单查询页面初始化参数", notes = "下单查询页面初始化参数")
    @PostMapping("/getPageParams001")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams001() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("UNIT", this.getSelectDataFromDataDic(DictCode.交易单位, null));
        initPageParams.put("BACK_LEND_DIRECTION", this.getSelectDataFromDataDic(DictCode.回购拆借方向, null));

        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
        initPageParams.put("workflowId", workflowId);
        initPageParams.put("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        initPageParams.put("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        initPageParams.put("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        initPageParams.put("parentPageModel", parentPageModel);
        initPageParams.put("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        initPageParams.put("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        initPageParams.put("TRADE_MARKET_04", DictCodeList.交易流通场所_深圳证券交易所.getValue());

        initPageParams.put("risk_math_switch", CacheUtil.getSysParam("risk_math_switch"));
        initPageParams.put("IS_RISKMATH_SOFTCONTROL", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));

        // 非公开行情请求类型下拉列表数据
        List<SelectOptionDto> options = new ArrayList();
        SelectOptionDto option = new SelectOptionDto();
        option.setId(DictCodeListExt3.交易方向_正回购.getValue());
        option.setText(DictCodeListExt3.交易方向_正回购.getCodeName());
        options.add(option);

        option = new SelectOptionDto();
        option.setId(DictCodeListExt3.交易方向_到期续做.getValue());
        option.setText(DictCodeListExt3.交易方向_到期续做.getCodeName());
        options.add(option);

        option = new SelectOptionDto();
        option.setId(DictCodeListExt3.交易方向_解除质押.getValue());
        option.setText(DictCodeListExt3.交易方向_解除质押.getCodeName());
        options.add(option);

        option = new SelectOptionDto();
        option.setId(DictCodeListExt3.交易方向_中途换券.getValue());
        option.setText(DictCodeListExt3.交易方向_中途换券.getCodeName());
        options.add(option);

        option = new SelectOptionDto();
        option.setId(DictCodeListExt3.交易方向_提前购回.getValue());
        option.setText(DictCodeListExt3.交易方向_提前购回.getCodeName());
        options.add(option);

        SelectDto select = new SelectDto();
        select.setCode("REQUEST_TYPE");
        select.setName("请求类型");
        select.setOptions(options);
        initPageParams.put("REQUEST_TYPE", select);

        return new Message<Map<String, Object>>(initPageParams).success();
    }

    @ApiOperation(value = "持仓页面初始化参数", notes = "持仓页面初始化参数")
    @PostMapping("/getPageParams002")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams002() {
        Map<String, Object> initPageParams = new HashMap<>();

        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        initPageParams.put("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
        initPageParams.put("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_15", DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
        initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        initPageParams.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        initPageParams.put("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        initPageParams.put("UNOPENQUOTA_REFRESH_PERIOD", CacheUtil.getSysParam(BussinessConstant.UNOPENQUOTA_REFRESH_PERIOD));
        initPageParams.put("POSTABLE_REFRESH_PERIOD", CacheUtil.getSysParam(BussinessConstant.POSTABLE_REFRESH_PERIOD));
        initPageParams.put("ONWAYREP_REFRESH_PERIOD", CacheUtil.getSysParam(BussinessConstant.ONWAYREP_REFRESH_PERIOD));
        initPageParams.put("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
        initPageParams.put("TRADE_MARKET_03", DictCodeList.交易流通场所_上海证券交易所.getValue());
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    @ApiOperation(value = "指令查询页面初始化参数", notes = "指令查询页面初始化参数")
    @PostMapping("/getPageParams003")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams003() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
        initPageParams.put("ENTRUST_STATUS_07", DictCodeList.委托状态_已撤回.getValue());
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "查询本方交易员", notes = "查询本方交易员")
    @ResponseBody
    @PostMapping("/selectTrader")
    public Message<List<SelectOptionDto>> selectTrader(@RequestBody Map<String, Object> pageParam) {
//        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return new Message<List<SelectOptionDto>>().success().setData(etrExchangeAgreRepTrade001Service.selectTrader(pageParam));
    }

    /**
     * 本方交易员联想文本框初始化(审批页面,使用)
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectTraderWfl")
    public List<SelectOptionDto> selectTraderWfl() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeAgreRepTrade001Service.selectTrader(pageParam);
    }

    /**
     * 对手方交易员联想文本框初始化。
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "查询对手方交易员", notes = "查询对手方交易员")
    @ResponseBody
    @PostMapping("/selectCounterTrader")
    public Message<List<SelectOptionDto>> selectCounterTrader(@RequestBody Map<String, Object> pageParam) {
//        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return new Message<List<SelectOptionDto>>().success().setData(etrExchangeAgreRepTrade001Service.selectCounterTrader(pageParam));
    }

    /**
     * 对手方交易员联想文本框初始化(审批页面,使用)
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectCounterTraderWfl")
    public List<SelectOptionDto> selectCounterTraderWfl() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeAgreRepTrade001Service.selectCounterTrader(pageParam);
    }

    /**
     * 获取到期结算日
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "获取到期结算日", notes = "获取到期结算日")
    @ResponseBody
    @PostMapping("/addDate")
    public Message<Map<String, Object>> addDate(@RequestBody Map<String, Object> pageParam) {
//        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return new Message<Map<String, Object>>().success().setData(etrExchangeAgreRepTrade001Service.addDate(pageParam));
    }

    /**
     * 获取到期结算日
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/addDateWfl")
    public Map<String, Object> addDateWfl() {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeAgreRepTrade001Service.addDate(pageParam);

    }

    /**
     * 获取面值
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "获取面值", notes = "获取面值")
    @ResponseBody
    @PostMapping("/getFaceValue")
    public Message<String> getFaceValue(@RequestBody Map<String, Object> pageParam) {
        String repSecId = (String) pageParam.get("repSecId");
        String iPlatformDate = (String) pageParam.get("iPlatformDate");
        BigDecimal price = trdCommonBaseService.getFaceValue(repSecId, DateUtil.parse(iPlatformDate));
        return new Message<String>().success().setData(price != null ? price.toString() : "");
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/getFaceValueWfl")
    public String getFaceValueWfl() {
        String repSecId = getParam("repSecId");
        String iPlatformDate = getParam("iPlatformDate");
        BigDecimal price = trdCommonBaseService.getFaceValue(repSecId, DateUtil.parse(iPlatformDate));
        if (price != null) {
            return price.toString();
        }
        return "";
    }

    /**
     * 查询首期成交信息(成交编号)
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "查询首期成交信息(成交编号)", notes = "查询首期成交信息(成交编号)")
    @ResponseBody
    @PostMapping("/getFirstRepEntrustInfo/{tradeId}")
    public Message<EtrExchangeAgreRepTrade001Dto> getEntrustInfoByTradeId(@ApiParam(value = "成交编号", required = false) @PathVariable String tradeId) {
        //成交编号
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("tradeId", tradeId);
        return new Message<EtrExchangeAgreRepTrade001Dto>().success().setData(etrExchangeAgreRepTrade001Service.getEntrustInfoByTradeId(pageParam));
    }

    /**
     * 查询原成交信息
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/getEntrustInfoByTradeIdWfl")
    public EtrExchangeAgreRepTrade001Dto getEntrustInfoByTradeIdWfl() {
        //成交编号
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeAgreRepTrade001Service.getEntrustInfoByTradeId(pageParam);
    }

    /**
     * 查询原成交信息(委托编号)
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "查询首期成交信息(委托编号)", notes = "查询首期成交信息(委托编号)")
    @ResponseBody
    @PostMapping("/getEntrustInfoByEntrustId/{entrustId}")
    public Message<EtrExchangeAgreRepTrade001Dto> getEntrustInfoByEntrustId(@ApiParam(value = "委托编号", required = false) @PathVariable String entrustId) {
        //委托编号
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("entrustId", entrustId);
        return new Message<EtrExchangeAgreRepTrade001Dto>().success().setData(etrExchangeAgreRepTrade001Service.getEntrustInfoByEntrustId(pageParam));
    }

    /**
     * 查询原成交信息
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/getEntrustInfoByEntrustIdWfl")
    public EtrExchangeAgreRepTrade001Dto getEntrustInfoByEntrustIdWfl() {
        //成交编号
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        return etrExchangeAgreRepTrade001Service.getEntrustInfoByEntrustId(pageParam);
    }


    /**
     * 获取可用可取资金
     */
    @ResponseBody
    @GetMapping("/getCashAmount/{portfolioId}/{acctNo}/{sysDate}")
    public Message<Map<String, Object>> getCashAmount(@ApiParam(value = "组合代码", required = false) @PathVariable String portfolioId,
                                                      @ApiParam(value = "资金账户", required = false) @PathVariable String acctNo,
                                                      @ApiParam(value = "系统时间", required = false) @PathVariable String sysDate) {
        Map<String, Object> param = RequestUtil.getPageParam();
        return new Message<Map<String, Object>>().success().setData(etrExchangeAgreRepTrade001Service.getCashAmount(portfolioId, acctNo, sysDate));
    }


    /**
     * 获取可用可取资金
     */
    @ResponseBody
    @RequestMapping("/getCashAmountWfl")
    public Map<String, Object> getCashAmountWfl() {
        Map<String, Object> param = RequestUtil.getPageParam();
        String sysDate = (String) param.get("sysDate");
        String portfolioId = (String) param.get("portfolioId");
        String acctNo = (String) param.get("acctNo");
        return etrExchangeAgreRepTrade001Service.getCashAmount(portfolioId, acctNo, sysDate);
    }

    /**
     * 获取组合资产账户列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPtlSecAcctInfoWfl")
    public List<SelectOptionDto> getPtlSecAcctInfoWfl() {
        return etrExchangeTradeCommon001Service.getPtlSecAcctInfo(RequestUtil.getPageParam());
    }

    /**
     * 查询资产列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getFinprodMarketInfoWfl")
    public Page<SelectOptionDto> getFinprodMarketInfoWfl() {
        return etrExchangeTradeCommon001Service.getFinprodMarketInfo(RequestUtil.getPageParam());
    }


    /**
     * 查询证券账户
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSecAcctInfoWfl")
    public List<SelectOptionDto> getSecAcctInfoWfl() {
        return etrExchangeTradeCommon001Service.getSecAcctInfo(RequestUtil.getPageParam());
    }


    @ResponseBody
    @RequestMapping("/selectCounterIdByCounterTypeWfl")
    public List<SelectOptionDto> selectCounterIdByCounterTypeWfl() {
        return etrExchangeAgreRepTrade001Service.selectCounterIdByCounterType(RequestUtil.getPageParam());
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
    public AjaxResult checkSaveError(@RequestBody @Validated({ValidateGroupEdit.class}) EtrExchangeAgreRepTrade001Dto dto, BindingResult result)
            throws Exception {
        return this.check(dto, result).getData();
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto    dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "协议回购保存提交check", notes = "协议回购保存提交check")
    @ResponseBody
    @PostMapping("check")
    public Message<AjaxResult> check(@RequestBody @Validated() EtrExchangeAgreRepTrade001Dto dto,
                                     BindingResult result) throws Exception {
        if (!StringUtil.equals("submit", dto.getOperateType())) {
            return new Message<AjaxResult>().success().setData(commonCheck(result, dto.getOperateType()));
        }

        if (result != null && result.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(result));
        }

        dto.setInvManagerId(LoginUserUtil.getUserName());

        if (DictCodeListExt3.交易方向_提前购回.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
        } else if (DictCodeListExt3.交易方向_到期购回.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
        } else if (DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
        } else if (DictCodeListExt3.交易方向_解除质押.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
        } else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
        } else {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
        }
        AjaxResult ajax = etrExchangeAgreRepTrade001Service.checkSave(dto, result);
        //共通check
        List<ValidMessage> valid = etrExchangeTradeCommonValidatorService.valid(dto);
        if (StringUtil.isNotEmpty(ajax) && !CollectionUtils.isEmpty(valid)) {
            valid.addAll(ajax.getErrorList());
            ajax.setErrorList(valid);
        } else if (!StringUtil.isNotEmpty(ajax) && !CollectionUtils.isEmpty(valid)) {
            ajax = new AjaxResult();
            ajax.setErrorList(valid);
        }
        if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList())) {
            List<ValidMessage> errorList = new ArrayList<>();
            for (ValidMessage validMessage : ajax.getErrorList()) {
                errorList.add(new ValidMessage(validMessage.getField(), this.getMessage(validMessage.getMessage())));
            }
            ajax.setErrorList(errorList);
            return new Message<AjaxResult>().success().setData(ajax);
        }
//        //正回购、质押券出库
//        if (DictCodeListExt3.交易方向_首期.getValue().equals(dto.getPs()) && StringUtil.equals(dto.getRepPs(),DictCodeList.回购拆借方向_正回购.getValue())) {
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("portfolioId", dto.getPortfolioId());
//            param.put("finprodId", dto.getFinprodId());
//            if (null != dto.getSecAcctId()) {
//                param.put("secAcctId", dto.getSecAcctId());
//            }
//            param.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
//            dto.setAccAvailablePos(etrExchangeTradeCommon001Service.selectAvailableNum(param));
//            if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getRepSecNum())) {
//                if (dto.getAccAvailablePos().compareTo(dto.getRepSecNum()) == -1) {
//                    if(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())) {
//                        errorList.add(new ValidMessage("repSecNum", "质押数量不能大于可用持仓！"));
//                    }
//                    else{
//                        AjaxResult ajaxResult = new AjaxResult();
//                        ajaxResult.setData("质押数量大于可用持仓,是否继续？");
//                        ajaxResult.setMessageType(MessageType.确认消息);
//                        return new Message<AjaxResult>().success().setData(ajaxResult);
//                    }
//                }
//            }
//        }
//        // 中途换券
//        if (DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())) {
//            Map<String, Object> param = new HashMap<String, Object>();
//            param.put("portfolioId", dto.getPortfolioId());
//            param.put("finprodId", dto.getNewfinprodId());
//            if (null != dto.getSecAcctId()) {
//                param.put("secAcctId", dto.getSecAcctId());
//            }
//            param.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
//            dto.setAccAvailablePos(etrExchangeTradeCommon001Service.selectAvailableNum(param));
//            if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getNewRepSecNum())) {
//                if (dto.getAccAvailablePos().compareTo(dto.getNewRepSecNum()) == -1) {
//                    if(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())) {
//                        errorList.add(new ValidMessage("newRepSecNum", "质押数量不能大于可用持仓！"));
//                    }
//                    else{
//                        AjaxResult ajaxResult = new AjaxResult();
//                        ajaxResult.setData("质押数量大于可用持仓,是否继续？");
//                        ajaxResult.setMessageType(MessageType.确认消息);
//                        return new Message<AjaxResult>().success().setData(ajaxResult);
//                    }
//                }
//            }
//        }
//        // 逆回购
//        if (DictCodeListExt3.交易方向_首期.getValue().equals(dto.getPs()) && StringUtil.equals(dto.getRepPs(),DictCodeList.回购拆借方向_逆回购.getValue())) {
//            Map<String,Object> cashAmountMap =  etrExchangeAgreRepTrade001Service.getCashAmount(dto.getPortfolioId(),dto.getAcctNo(),dto.getVdate());
//            BigDecimal accAvailableCash = (BigDecimal)cashAmountMap.get("cashUse");
//            if (StringUtil.isNotEmpty(accAvailableCash) && StringUtil.isNotEmpty(dto.getTradeAmt())) {
//                if (accAvailableCash.compareTo(dto.getTradeAmt()) == -1) {
//                    if(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())) {
//                        errorList.add(new ValidMessage("tradeAmt", this.getMessage("ETR_ERROR_0019")));
//                    }
//                    else{
//                        AjaxResult ajaxResult = new AjaxResult();
//                        ajaxResult.setData("回购金额大于指令可用现金,是否继续？");
//                        ajaxResult.setMessageType(MessageType.确认消息);
//                        return new Message<AjaxResult>().success().setData(ajaxResult);
//                    }
//                }
//            }
//        }

        return new Message<AjaxResult>().success().setData(commonCheck(result, dto.getOperateType()));
    }


    /**
     * 保存/提交
     *
     * @param dto dto
     * @param br  br
     * @return 结果
     * @throws Exception 异常
     */
    @ApiOperation(value = "协议回购保存提交方法", notes = "协议回购保存提交方法")
    @SuppressWarnings("deprecation")
    @ResponseBody
    @PostMapping("save")
    public Message<AjaxResult> save(@RequestBody EtrExchangeAgreRepTrade001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }

        // 操作类型
        OperationType operationType = OperationType.NEW;
        if (StringUtil.isNotBlank(dto.getEntrustId())) {
            operationType = OperationType.MODIFY;
        }

        //业务类型
        String businessType = String.valueOf("");
        //投管业务类型
        String busiTypePosition = String.valueOf("");
        if (DictCodeListExt3.交易方向_提前购回.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购提前购回指令.getValue();
            busiTypePosition=DictCodeListExt3.投管业务类型_提前购回.getValue();
        } else if (DictCodeListExt3.交易方向_到期购回.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购到期购回指令.getValue();
            busiTypePosition=DictCodeListExt3.投管业务类型_到期购回.getValue();
        } else if (DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购中途换券指令.getValue();
            busiTypePosition=DictCodeListExt3.投管业务类型_中途换券.getValue();
        } else if (DictCodeListExt3.交易方向_解除质押.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购解除质押指令.getValue();
            busiTypePosition=DictCodeListExt3.投管业务类型_解除质押.getValue();
        } else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())) {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购到期续做指令.getValue();
            busiTypePosition=DictCodeListExt3.投管业务类型_到期续做.getValue();
        } else {
            dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
            dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
            businessType = DictCodeListExt3.共通业务类型_协议回购首期指令.getValue();
        }
        dto.setBusiTypePosition(busiTypePosition);
        //add end
        if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            if (DictCodeListExt3.交易方向_提前购回.getValue().equals(dto.getPs())) {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购提前购回指令));
            } else if (DictCodeListExt3.交易方向_到期购回.getValue().equals(dto.getPs())) {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购到期购回指令));
            } else if (DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())) {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购中途换券指令));
            } else if (DictCodeListExt3.交易方向_解除质押.getValue().equals(dto.getPs())) {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购解除质押指令));

            } else if (DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())) {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购到期续做指令));
            } else {
                param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_协议回购首期指令));
            }
            //add end
            String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            dto.setEntrustId(code);
        }

        if (StringUtil.equals(dto.getRepPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        }

        dto.setFromEtrExchange(DictCodeList.是否_是.getValue());
        dto.setBusiPs(dto.getRepPs());
        dto.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());
        // ----------add by zhangrui1 20200703 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())
                && (DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_逆回购.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_正回购.getValue().equals(dto.getPs())
                || DictCodeListExt3.交易方向_首期.getValue().equals(dto.getPs()))) {
            Boolean isShouQi = StringUtil.equals(DictCodeListExt3.交易方向_首期.getValue(), dto.getPs());
            if (isShouQi) {
                dto.setPs(dto.getRepPs());
            }
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(), DictCodeList.平安资产类型_质押式回购.getValue());
            if (isShouQi) {
                dto.setPs(DictCodeListExt3.交易方向_首期.getValue());
            }
            if (StringUtil.isNotEmpty(ar)) {
                return new Message<AjaxResult>().success().setData(ar);
            }
        }
        // ----------add by zhangrui1 20200703 预占功能 end----------
        /*
         * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
         * 数据字典.共通业务类型.股票竞价指令
         *
         * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
         * 数据字典.共通业务类型.基金竞价指令
         *
         * if 画面.委托方向 = （数据字典.交易方向.正回购 or 逆回购） and 画面.交易平台 = 数据字典.交易平台.大宗交易平台
         * 指令业务类型 = 数据字典.共通业务类型.大宗债券质押式回购指令
         *
         * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
         * 数据字典.共通业务类型.大宗股票指令
         *
         * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
         * 数据字典.共通业务类型.大宗基金指令
         *
         * if 画面.金融产品类型（投管）.债券and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
         * 数据字典.共通业务类型.大宗债券指令
         */
        //dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
        ProcessParameter<EtrExchangeAgreRepTrade001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<EtrExchangeAgreRepTrade001Dto> params = new WorkFlowParameter<>();
        params.setPageId("EtrExchangeAgreRepTrade002");
        params.setElementId("EtrExchangeAgreRepTrade002_btn_submit");
        params.setBusinessType(businessType);
        params.setBusinessCode(dto.getEntrustId());
        params.setComment(entity -> getMessage("EtrExchangeAgreRepTrade001.entrustId") + "(" + entity.getEntrustId() + ")-"
                + "-" + "业务类型" + "(" + SysDatadictUtil.getNameFromDataDict(DictCode.上农商_指令业务类型,
                entity.getBusinessTypeName()) + ")-"
                + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(operationType);
        dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
        dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
        dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
        dto.setCreateTime(new Date());// 创建时间
//        String msg = etrExchangeAgreRepTrade001Service.start(processParameter, dto);
//        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        try {
            String msg = etrExchangeAgreRepTrade001Service.start(processParameter, dto);
            AjaxResult m = new AjaxResult(msg);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("场内交易指令新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }


    /**
     * 查询持仓
     *
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "查询持仓", notes = "查询持仓")
    @ResponseBody
    @PostMapping("findPositionsPage")
    public Message<Page<EtrExchangeAgreRepTrade002Dto>> findPositionsPage(@RequestBody EtrExchangeTradePositionsCommonRequest request) {

        try {
            return new Message<Page<EtrExchangeAgreRepTrade002Dto>>().success().setData(etrExchangeAgreRepTrade002Service.findPositionsPage(request));
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<EtrExchangeAgreRepTrade002Dto>>().fail().setMessage("查询异常");
        }
    }

    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 start
//    /**
//     * 查询债券质押持仓
//     *
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @ApiOperation(value = "查询债券质押持仓", notes = "查询债券质押持仓")
//    @ResponseBody
//    @PostMapping("findZqzyPositionsPage")
//    public Message<Page<EtrExchangeTradePositionsCommonResponse>> findZqzyPositionsPage(@RequestBody EtrExchangeTradePositionsCommonRequest request){
//        return new Message<Page<EtrExchangeTradePositionsCommonResponse>>().success().setData(etrExchangeTradeCommon002Service.findZqzyPositionsPage(request));
//    }
    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 end

    /**
     * 查询证券代码联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "查询证券代码联想框文本框选项", notes = "查询证券代码联想框文本框选项")
    @PostMapping("getSecAcctId")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getSecAcctId(@RequestBody SelectOptionRequestDto request) {
        Page<SelectOptionDto> tempList = etrExchangeTradeCommon002Service.getSecAcctId(request);
        return new Message<Page<SelectOptionDto>>().success().setData(tempList);
    }


    /**
     * 查询上交所和深交所标准券数量
     *
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "查询上交所和深交所标准券数量", notes = "查询上交所和深交所标准券数量")
    @PostMapping("loadSecNum")
    @ResponseBody
    public Message<Map<String, String>> loadSecNum(@RequestBody SelectOptionRequestDto request) {
        Map<String, String> map = etrExchangeTradeCommon002Service.loadSecNum(request);
        return new Message<Map<String, String>>().success().setData(map);
    }

    /**
     * 查询在途回购
     *
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "查询在途回购", notes = "查询在途回购")
    @ResponseBody
    @PostMapping("findOnWayRepPage")
    public Message<Page<EtrExchangeOnWayRep002Dto>> findOnWayRepPage(@RequestBody EtrExchangeOnWayRepRequestDto request) {
        return new Message<Page<EtrExchangeOnWayRep002Dto>>().success().setData(etrExchangeAgreRepTrade002Service.findOnWayRepPage(request));
    }

    /**
     * 查询非公开行情列表
     *
     * @return
     */
    @ApiOperation(value = "查询非公开行情列表", notes = "查询非公开行情列表")
    @ResponseBody
    @PostMapping("findUnOpenQuotaPage")
    public Message<Page<EtrExchangeUnOpenQuota002Dto>> findUnOpenQuotaPage(@RequestBody EtrExchangeUnOpenQuotaRequestDto request) {

        return new Message<Page<EtrExchangeUnOpenQuota002Dto>>(etrExchangeAgreRepTrade002Service.findUnOpenQuotaPage(request));
    }

    /**
     * 查询委托列表
     *
     * @return
     */
    @ApiOperation(value = "查询委托列表", notes = "查询委托列表")
    @ResponseBody
    @PostMapping("selectEtrEntrust")
    public Message<Page<EtrExchangeAgreRepTrade003Dto>> selectEtrEntrust(@RequestBody EtrExchangeAgreRepTrade003RequestDto request) {
        Page<EtrExchangeAgreRepTrade003Dto> result = etrExchangeAgreRepTrade002Service.selectEtrEntrust(request);
        return new Message<Page<EtrExchangeAgreRepTrade003Dto>>().success().setData(result);
    }


    /**
     * 关闭指令check
     *
     * @param dto
     * @param result
     * @return
     */
    @ApiOperation(value = "关闭指令check", notes = "关闭指令check")
    @ResponseBody
    @PostMapping("closeCheck")
    public Message<AjaxResult> closeCheck(@RequestBody EtrExchangeAgreRepTrade003Dto dto, BindingResult result) {
        AjaxResult webResult = new AjaxResult();
        if (result.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(result));
        }

        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();

        List<ValidMessage> errList = new ArrayList<>();
        List<EtrExchangeAgreRepTrade003Dto> EtrExchangeAgreRepTrade003Dto = dto.getSelectedItem();
        for (EtrExchangeAgreRepTrade003Dto item : EtrExchangeAgreRepTrade003Dto) {
            //银行间保持一致
            if (!StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())
                    && !StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已退回.getValue())
                    && !StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_已撤回.getValue())) {
                errList.add(new ValidMessage("该委托状态不是已退回/已接收/已撤回，无法关闭!"));
            }

            wflValidInputParameter.setBusinessDataPk1(item.getEntrustId());
            if (WorkFlowUtil.isNotFromToDoList() && mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errList.add(new ValidMessage("所选对象存在有效或流程中的数据不能关闭"));
            }
        }
        if (null != errList && errList.size() > 0) {
            webResult.setErrorList(errList);
            return new Message<AjaxResult>().success().setData(webResult);
        }
        return new Message<AjaxResult>().success().setData(webResult);
    }


    /**
     * 撤回指令check
     *
     * @param dto
     * @param result
     * @return
     */
    @ApiOperation(value = "撤回指令check", notes = "撤回指令check")
    @ResponseBody
    @PostMapping("revokeCheck")
    public Message<AjaxResult> revokeCheck(@RequestBody @Validated({ValidateGroupEdit.class}) EtrExchangeAgreRepTrade003Dto dto,
                                           BindingResult result) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
        List<EtrExchangeAgreRepTrade003Dto> EtrExchangeAgreRepTrade003Dto = dto.getSelectedItem();
        for (EtrExchangeAgreRepTrade003Dto item : EtrExchangeAgreRepTrade003Dto) {
            if (!(StringUtil.equals(item.getEntrustStatus(), DictCodeList.委托状态_待接收.getValue())
                    || StringUtil.equals(item.getEntrustStatus(), DictCodeListExt3.委托状态_待分发.getValue()))) {
                errList.add(new ValidMessage("该委托状态不是待接收、待分发，无法撤回!"));
            }
        }
        if (null != errList && errList.size() > 0) {
            webResult.setErrorList(errList);
            return new Message<AjaxResult>().success().setData(webResult);
        }
        return new Message<AjaxResult>().success().setData(webResult);
    }


    /**
     * 指令关闭
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("entrustClose")
    public Message<AjaxResult> close(@RequestBody EtrExchangeAgreRepTrade003Dto dto) throws Exception {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();
        //判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
        List<EtrExchangeAgreRepTrade003Dto> etrDistributeList = new ArrayList<EtrExchangeAgreRepTrade003Dto>();
        for (EtrExchangeAgreRepTrade003Dto selectedItem : dto.getSelectedItem()) {
            EtrDistribute etrDistribute = etrExchangeTradeCommon001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
            if (etrDistribute != null && StringUtils.equals(etrDistribute.getEntrustId(), selectedItem.getEntrustId())) {
                etrDistributeList.add(selectedItem);
            }
        }
        //调用【交易系统指令状态变更接口】
        if (etrDistributeList != null && etrDistributeList.size() > 0) {
            ValidMessage validMessage = etrCommonService.validEtrClose(dto.getSelectedItem(), DictCodeList.委托状态_已关闭.getValue());
            if (null != validMessage) {
                errList.add(validMessage);
                webResult.setErrorList(errList);
                return new Message<AjaxResult>().success().setData(webResult);
            }
        }
        List<String> entrustIdList = new ArrayList<>();
        for (EtrExchangeAgreRepTrade003Dto item : dto.getSelectedItem()) {
            item.setFromEtrExchange(dto.getFromEtrExchange());
            item.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());

            EtrExchangeTradeOperateRequest etrExchangeTradeOperateRequest = new EtrExchangeTradeOperateRequest();
            BeanUtil.copyPropertiesIgnoreNull(etrExchangeTradeOperateRequest, item);
            etrExchangeTradeCommon001Service.closeDelegate(etrExchangeTradeOperateRequest);
            // 指令持仓流水、现金流水处理
            etrExchangeAgreRepTrade001Service.revokeOrCloseEntrust(item);
            //释放风险接口释放
            entrustIdList.add(item.getEntrustId());
        }
        EntrustCompleteUtil.doComplete(entrustIdList);
        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("ETR_X_003")));
    }


    /**
     * 指令撤回
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("entrustRevoke")
    public Message<AjaxResult> revoke(@RequestBody EtrExchangeAgreRepTrade003Dto dto) throws Exception {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errList = new ArrayList<>();

        //判断指令是否已经分发，没分发就不调用交易系统指令状态变更接口；
        List<EtrExchangeAgreRepTrade003Dto> etrDistributeList = new ArrayList<EtrExchangeAgreRepTrade003Dto>();
        for (EtrExchangeAgreRepTrade003Dto selectedItem : dto.getSelectedItem()) {
            EtrDistribute etrDistribute = etrExchangeTradeCommon001Service.getDistributeByEntrustId(selectedItem.getEntrustId());
            if (etrDistribute != null && StringUtils.equals(etrDistribute.getEntrustId(), selectedItem.getEntrustId())) {
                etrDistributeList.add(selectedItem);
            }
        }
        // 调用【交易系统指令状态变更接口】
        if (etrDistributeList != null && etrDistributeList.size() > 0) {
            ValidMessage validMessage = etrCommonService.validEtrClose(etrDistributeList, DictCodeList.委托状态_已撤回.getValue());
            if (null != validMessage) {
                errList.add(validMessage);
                webResult.setErrorList(errList);
                return new Message<AjaxResult>().success().setData(webResult);
            }
        }

        for (EtrExchangeAgreRepTrade003Dto item : dto.getSelectedItem()) {
            item.setFromEtrExchange(dto.getFromEtrExchange());
            item.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());

            EtrExchangeTradeOperateRequest etrExchangeTradeOperateRequest = new EtrExchangeTradeOperateRequest();
            BeanUtil.copyPropertiesIgnoreNull(etrExchangeTradeOperateRequest, item);
            etrExchangeTradeCommon001Service.recallDelegate(etrExchangeTradeOperateRequest);
            // 指令持仓流水、现金流水处理
            etrExchangeAgreRepTrade001Service.revokeOrCloseEntrust(item);
            //查询首期指令信息
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("entrustId", item.getEntrustId());
            EtrExchangeAgreRepTrade001Dto EtrExchangeAgreRepTrade001Dto = etrExchangeAgreRepTrade001Service.getEntrustInfoByEntrustId(keyMap);
            item.setTradeId(EtrExchangeAgreRepTrade001Dto.getTradeId());
            item.setIntiTradeId(EtrExchangeAgreRepTrade001Dto.getIntiTradeId());
            item.setBusiPs(EtrExchangeAgreRepTrade001Dto.getBusiPs());
            // 释放风险接口释放
            EntrustRdsCommonAop.doPreOccupy(item, BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(), DictCodeList.平安资产类型_质押式回购.getValue());
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("撤回成功")));
    }

    /**
     * 质押券查询下拉框
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "质押券查询下拉框", notes = "质押券查询下拉框")
    @ResponseBody
    @PostMapping("selectPledgeInfo")
    public Message<Page<SelectOptionDto>> selectPledgeInfo(@RequestBody SelectOptionRequestDto dto) {
        return new Message<Page<SelectOptionDto>>().success().setData(etrExchangeAgreRepTrade001Service.selectPledgeInfo(dto));
    }

    /**
     * 交易编号下拉框
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "交易编号下拉框", notes = "交易编号下拉框")
    @ResponseBody
    @PostMapping("selectTradeId")
    public Message<Page<SelectOptionDto>> selectTradeId(@RequestBody SelectOptionRequestDto dto) {
        return new Message<Page<SelectOptionDto>>().success().setData(etrExchangeAgreRepTrade001Service.selectTradeId(dto));
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @return 联想文本框
     */
    @ResponseBody
    @RequestMapping("/selectPs")
    public List<SelectOptionDto> selectPs() {
        String tradeMarket = getParam("tradeMarket");
        String psList = getParam("psList");

        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        if (StringUtil.isNotEmpty(psList)) {
            String[] str = psList.split(",");

            List<SelectOptionDto> PS = selectDto.getOptions().stream().filter(option -> {
                for (String s : str) {
                    if (StringUtil.equals(option.getId(), s)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            return PS;
        } else if (StringUtil.isNotEmpty(tradeMarket) && tradeMarket.equals("04")) {
            List<SelectOptionDto> PS_SZ = selectDto.getOptions().stream().filter(option -> {
                return StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_提前购回.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_到期购回.getValue())
                        || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_到期续做.getValue());
            }).collect(Collectors.toList());
            return PS_SZ;
        }
        return selectDto.getOptions();
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("export")
    @ResponseBody
    @ApiOperation(value = "持仓列表导出", notes = "持仓列表导出")
    public void exportPosition(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "持仓列表导出", () -> {
            EtrExchangeTradePositionsCommonRequest dto = requestDto.getDto(EtrExchangeTradePositionsCommonRequest.class);
            List dataList = etrExchangeAgreRepTrade002Service.findPositionsPage(dto).getRoot();
            return dataList;
        });
    }

    @PostMapping("export1")
    @ResponseBody
    @ApiOperation(value = "非公开行情列表导出", notes = "非公开行情列表导出")
    public void exportPosition1(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "非公开行情列表导出", () -> {
            EtrExchangeUnOpenQuotaRequestDto dto = requestDto.getDto(EtrExchangeUnOpenQuotaRequestDto.class);
            List dataList = etrExchangeAgreRepTrade002Service.findUnOpenQuotaPage(dto).getRoot();
            return dataList;
        });
    }

    @PostMapping("export2")
    @ResponseBody
    @ApiOperation(value = "在途回购列表导出", notes = "在途回购列表导出")
    public void exportPosition2(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "在途回购列表导出", () -> {
            EtrExchangeOnWayRepRequestDto dto = requestDto.getDto(EtrExchangeOnWayRepRequestDto.class);
            List dataList = etrExchangeAgreRepTrade002Service.findOnWayRepPage(dto).getRoot();
            return dataList;
        });
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("export3")
    @ResponseBody
    @ApiOperation(value = "指令列表导出", notes = "指令列表导出")
    public void exportPosition3(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "指令列表导出", () -> {
            EtrExchangeAgreRepTrade003RequestDto dto = requestDto.getDto(EtrExchangeAgreRepTrade003RequestDto.class);
            List dataList = etrExchangeAgreRepTrade002Service.selectEtrEntrust(dto).getRoot();
            return dataList;
        });
    }

    /**
     * 查询证券代码联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "查询指令编号联想框文本框选项", notes = "查询指令编号联想框文本框选项")
    @PostMapping("getId")
    @ResponseBody
    public Message<Page<SelectOptionDto>> getId(@RequestBody SelectOptionRequestDto request) {
        Page<SelectOptionDto> tempList = etrExchangeTradeCommon002Service.getId(request);
        return new Message<Page<SelectOptionDto>>().success().setData(tempList);
    }
}
