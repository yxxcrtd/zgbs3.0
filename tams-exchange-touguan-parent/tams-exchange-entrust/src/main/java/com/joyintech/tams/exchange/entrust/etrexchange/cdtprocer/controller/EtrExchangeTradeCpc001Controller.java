package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.common.aop.ExchangeEntrustRdsCommonAop;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateApplyRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.service.EtrExchangeTradeCpc001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeMiniLimitPriceRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.FinprodMarketInfoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.MinNumStepRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.controller.EtrExchangeTradeSec001Controller;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场内债券指令（现券 下单及审批）
 * @author qmh
 * @version 1.0 2022年4月15日
 * @see EtrExchangeTradeSec001Controller
 * @since 1.0
 */
@Controller
@PageId("etrCreditProtectionCertificate")
@RequestMapping("/etrexchange/cpc/etrCreditProtectionCertificate")
@Api(tags = "场内交易-信用保护凭证指令—下单审批接口")
public class EtrExchangeTradeCpc001Controller extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    @Autowired
    private EtrExchangeTradeCpc001Service etrExchangeTradeCpc001Service;

    public static final String ENTRUST_RECORD_NEW = "new";

    public static final String ENTRUST_RECORD_MODIFY = "modify";
    @Autowired
    ExchangeEntrustRdsCommonAop exchangeEntrustRdsCommonAop;

    @ApiOperation(value = "场内现券下单参数", notes = "场内现券下单参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("TARGET_CLASS", this.getSelectDataFromDataDic(DictCode.目标类型, null));
        initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
        initPageParams.put("PRICE_TYPE", this.getSelectDataFromDataDic(DictCode.计价方式, null));
        initPageParams.put("ASSIGN_DEAL_MODE", this.getSelectDataFromDataDic(DictCode.指定交易方式, null));
        this.setInvAimSelectOption(initPageParams);
        this.setPsSelectOptions(initPageParams);
        this.setPriceModelSelectOptions(initPageParams);
        // 设置固定的数据字典明细参数
        this.setConstSelectOption(initPageParams);
        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID))?null:getParam(GlobalConstant.WORKFLOW_ID);
        initPageParams.put("workflowId", workflowId);

        //委托代码
        String entrustId = "null".equals(getParam("entrustId"))?null:getParam("entrustId");
        initPageParams.put("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());

        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        initPageParams.put("parentPageModel", parentPageModel);
        initPageParams.put("PAGE_MODEL_NEW", GlobalConstant.DETAIL_PAGE_MODEL_NEW);
        initPageParams.put("PAGE_MODEL_EDIT", GlobalConstant.DETAIL_PAGE_MODEL_EDIT);
        //是否_是
        initPageParams.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());

        String isFromToDoList = getParam("isFromToDoList");
        EtrCreditProtectionCertificateApplyRequest entity = new EtrCreditProtectionCertificateApplyRequest();
        if (StringUtil.isNotEmpty(workflowId)) {
            //流程代办checkImportData
            entity = etrExchangeTradeCpc001Service.getBussinessEntityByWorkflowId(
                    workflowId);
            //add by chenjiaxing 流程中不显示资产名称 start
            String[] secIds = StringUtil.split(entity.getFinprodId(),"_");
            if(secIds!=null){
                if(secIds.length == 2 && StringUtil.isEmpty(entity.getStockId())){
                    entity.setStockId(secIds[1]);
                }
            }
            //add by chenjiaxing 流程中不显示资产名称 end
            initPageParams.put(GlobalConstant.ENTITY, entity);
            initPageParams.put("workflowId", workflowId);
        }
        else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
            //查看
            entity = etrExchangeTradeCpc001Service.getEtrExchangeTradeInfo(entrustId);
        }
        else{
            //新建页面初始化
            entity.setEntrustDate(platformDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
        }

        initPageParams.put("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        initPageParams.put("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
        initPageParams.put(GlobalConstant.ENTITY, entity);
        if (null != entity && null != entity.getEntrustPrice()) {
            initPageParams.put("BOND_SCALE", entity.getEntrustPrice().scale());
        } else {
            initPageParams.put("BOND_SCALE", 3);
        }
        if(null != entity && null != entity.getCalculateField()) {
            initPageParams.put("calculateField", entity.getCalculateField());
        }
        //最小价差List
        List<MinPirceDto> minPriceWhole = getInterfSysMinPriceAll(new HashMap<>());
        initPageParams.put("minPriceWhole", minPriceWhole);
        initPageParams.put("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
        initPageParams.put("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
        initPageParams.put("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
        initPageParams.put("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());


        // 场内交易指令画面行情区域自动刷新时间（毫秒）
        long etrExchange_HqArea_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(
                BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal)
                && !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD,
                tempSysParaVal)) {
            try {
                etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
            }
            catch (Exception e) {
            }
        }
        initPageParams.put("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);
        initPageParams.put("riskMathSwitch", CacheUtil.getSysParam("risk_math_switch"));
        initPageParams.put("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
        initPageParams.put("IS_RISKMATH_SOFTCONTROL", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));
        initPageParams.put("ETR_TRADE_TIME_SWITCH", CacheUtil.getSysParam("etr_trade_time_switch"));
        initPageParams.put("ETR_CHECK_POS_SWITCH",CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH));
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     *  获取流程中信息
     * @param workflowId
     * @return
     */
    @ApiOperation(value = "获取流程中信息", notes = "获取流程中信息")
    @ResponseBody
    @PostMapping("/getApproveEntity")
    public Message<EtrCreditProtectionCertificateApplyRequest> getApproveEntity(@RequestBody String workflowId)
    {
        try{
            workflowId = workflowId.replaceAll("\"","");
            EtrCreditProtectionCertificateApplyRequest entity = new EtrCreditProtectionCertificateApplyRequest();
            if (StringUtil.isNotEmpty(workflowId)) {
                entity = etrExchangeTradeCpc001Service.getBussinessEntityByWorkflowId(workflowId);
            }
            return new Message<EtrCreditProtectionCertificateApplyRequest>().success().setData(entity);
        } catch(Exception e){
            return new Message<EtrCreditProtectionCertificateApplyRequest>().fail().setMessage("查询异常");
        }

    }


    @ResponseBody
    @PostMapping("/getFinprodMarketInfo")
    @ApiOperation(value = "查询现券资产列表", notes = "查询现券资产列表")
    public Message<Page<SelectOptionDto>> getFinprodMarketInfo(@RequestBody FinprodMarketInfoRequest dto){
        logger.debug("getFinprodMarketInfo() called with parameters => 【dto = {}】",JsonUtils.object2Json(dto));
        try {
            Map<String, Object> map = BeanUtil.object2Map(dto);
            map.put("acctTradeMarket", dto.getTradeMarket());
            Page<SelectOptionDto> tempList = etrExchangeTradeCpc001Service.getFinprodMarketInfo(map);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }


    /**
     * 获取1/最小价差的倍数
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/getInterfSysMinPriceAll")
    public List<MinPirceDto> getInterfSysMinPriceAll(@RequestBody Map<String, Object> param) {
        List<MinPirceDto> minPirceList = etrExchangeTradeCpc001Service.getInterfSysMinPriceAll(param);
        return minPirceList;
    }

    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     * @param initPageParams initPageParams
     */
    private void setPsSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        List<SelectOptionDto> ps = selectDto.getOptions().stream().filter(option -> {
            return  StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(ps);
        initPageParams.put("PS", selectDto);
    }

    /**
     * 设置投资目的
     * @param initPageParams initPageParams
     */
    private void setInvAimSelectOption(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
        List<SelectOptionDto> invAimList = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_摊余成本类.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(invAimList);
        initPageParams.put("INV_AIM", selectDto);
    }

    /**
     * 价格模式
     * @param initPageParams initPageParams
     */
    private void setPriceModelSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.价格模式, null);
        List<SelectOptionDto> priceModel = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PRICE_MODEL", JsonUtils.objectArray2Json(priceModel.toArray()));
    }

    /**
     * 设置画面的一些固定的数据字典明细
     * @param initPageParams initPageParams
     */
    private void setConstSelectOption(Map<String, Object> initPageParams) {
        // 金融产品类型(投管用)
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 交易方向
        initPageParams.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        // 价格模式
        initPageParams.put("PRICE_MODEL_PRM02", DictCodeListExt2.价格模式_限价指令.getValue());
        initPageParams.put("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
        // 交易场所
        initPageParams.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        initPageParams.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        // 债券计算模式
        initPageParams.put("SEC_CALC_MODE_01", DictCodeListExt3.债券计算模式_单位净价.getValue());
        initPageParams.put("SEC_CALC_MODE_02", DictCodeListExt3.债券计算模式_单位全价.getValue());
        initPageParams.put("SEC_CALC_MODE_03", DictCodeListExt3.债券计算模式_到期收益率.getValue());
        // 指令业务类型
        initPageParams.put("ENTRUST_BUSS_TYPE_30", DictCodeListExt3.上农商_指令业务类型_信用保护凭证指令.getValue());
        // 目标类型
        initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        initPageParams.put("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());
    }

    /**
     * 保存按钮点击时的check
     *
     * @param  dto
     * @param  br
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "保存提交check", notes = "保存提交check")
    @PostMapping("checkSaveError")
    @ResponseBody
    public Message<AjaxResult> checkSaveError(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrCreditProtectionCertificateApplyRequest dto, BindingResult br)
            throws Exception {

        AjaxResult ajax=null;
        try {
            if("dbclick".equals(dto.getOptType())){
                ajax=etrExchangeTradeCpc001Service.dbClickCheck(dto);
            }
            List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=dto.getPortfolioList();
            if(!CollectionUtils.isEmpty(portfolioList)) {
                for (int i = 0; i < portfolioList.size(); i++) {
                    EtrExchangeTradePortfolioSecRepoRequest item = portfolioList.get(i);
                    dto.setVdate(dto.getEntrustDate());
                    dto.setMdate(dto.getEntrustDate());
                    dto.setPortfolioId(item.getPortfolioId());
                    dto.setAcctNo(item.getAcctNo());
                    dto.setEntrustNum(item.getEntrustNum());
                    dto.setEntrustAmt(item.getEntrustAmt());
                    dto.setSecAcctId(item.getSecAcctId()); // 证券账户
                    ajax = check(dto, br);
                    if (null != ajax) {
                        break;
                    }
                }
            }
            if (null != ajax) {
                return new Message<AjaxResult>().success().setData(ajax);
            } else {
                return new Message<AjaxResult>().success().setMessage("校验通过");
            }
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    /**
     * 单组合保存按钮点击时的check
     *
     * @param  dto
     * @param  br
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody EtrCreditProtectionCertificateApplyRequest dto, BindingResult br)
            throws Exception {
        etrExchangeTradeCpc001Service.setEntrustBussType(dto);
        AjaxResult ajax= etrExchangeTradeCpc001Service.checkSave(dto,br);
//        List<ValidMessage> validList = etrExchangeTradeSecRepoValidatorService.valid(dto);
//        if (!CollectionUtils.isEmpty(validList)){
//            ajax.setErrorList(validList);
//        }
        if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList()) && !StringUtil.equals(dto.getOperateType(),"save")) {
            return ajax;
        }
        AjaxResult ajaxResult = new AjaxResult();
        if(ajax != null){
            if (StringUtil.isNotEmpty(ajax) && StringUtil.isNotEmpty(ajax.getMessageType())
                    && StringUtil.isNotEmpty(ajax.getData())) {
                if (ajax.getMessageType().equals("错误消息")){
                    ajaxResult.setMessageType(MessageType.错误消息);
                }
                ajaxResult.setData(ajax.getData());
                return ajaxResult;
            }
        }
        return ajaxResult;
    }

    /**
     * 保存/提交
     *
     * @param dto
     *            dto
     * @param br
     *            br
     * @return 结果
     * @throws Exception
     *             异常
     */
    @ApiOperation(value = "保存提交", notes = "保存提交")
    @PostMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody EtrCreditProtectionCertificateApplyRequest dto, BindingResult br){
        try {
            if (br.hasErrors()) {
                return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
            }
            String msg ="";
            List<EtrCreditProtectionCertificateApplyRequest> riskEtrExchangeTrade001DtoList = new ArrayList<>();
            String businessType = etrExchangeTradeCpc001Service.setEntrustBussType(dto); //-y
            dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
            if (StringUtil.equals("save", dto.getOperateType())) {  //保存没有check和任何限制
                if(!CollectionUtils.isEmpty(dto.getPortfolioList())){
                    boolean flag=false;
                    for(EtrExchangeTradePortfolioSecRepoRequest temp:dto.getPortfolioList()){
                        if(!StringUtil.isNotEmpty(temp.getAcctNo())){
                            flag=true;
                            break;
                        }
                    }
                    Map<String,List<EtrExchangeTradePortfolioSecRepoRequest>> groupMap=null;
                    if(!flag){
                        groupMap= dto.getPortfolioList().stream().collect(
                                Collectors.groupingBy(item->item.getAcctNo()));
                    }
                    //如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
                    if(groupMap!=null&&groupMap.keySet().size() == 1 && dto.getPortfolioList().size()>1) {
                        //业务编号生成规则参数
                        if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                            BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                            String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                            dto.setEntrustId(code);
                        }
                        if (StringUtil.isNotEmpty(dto.getIsDoRkRedisFlag()) && StringUtil.equals(dto.getIsDoRkRedisFlag(),"true")) {
                            // ----------add by zhangrui1 20200703 预占功能 start----------
                            String finprodType2 = DictCodeList.平安资产类型_信用保护凭证.getValue();
                            //债转股需要走批量接口
                            String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                            dto.setFromEtrExchange("Y");//场内指令
                            AjaxResult ar = ExchangeEntrustRdsCommonAop.doPreOccupy(dto, infCode, finprodType2);
                            if (StringUtil.isNotEmpty(ar)) {
                                return new Message<AjaxResult>().success().setData(ar);
                            }
                        }
                        ProcessParameter<EtrCreditProtectionCertificateApplyRequest> processParameter = new ProcessParameter<EtrCreditProtectionCertificateApplyRequest>();
                        WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> params = new WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest>();
                        params.setPageId(dto.getPageId());
                        params.setElementId(dto.getElementId());
                        params.setBusinessType(businessType);
                        params.setBusinessCode(dto.getEntrustId());
                        String tempBusinessTypeName= StringUtils.isNotEmpty(dto.getBusinessTypeName())?dto.getBusinessTypeName():"";
                        params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                                + "-" + "业务类型" + "(" + tempBusinessTypeName + ")-"
                                + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
                        processParameter.setWorkFlowParameter(params);
                        msg = etrExchangeTradeCpc001Service.start(processParameter, dto);
                    }
                    //多组合指令
                    else {
                        List<EtrCreditProtectionCertificateApplyRequest> etrExchangeTrade001DtoList = new ArrayList<>();
                        for(EtrExchangeTradePortfolioSecRepoRequest item:dto.getPortfolioList()) {
                            EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = etrExchangeTradeCpc001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item);
                            if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
                                BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                                etrExchangeTrade001Dto.setEntrustId(code);
                            }
                            etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                            etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                            etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                            etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
                            etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
                            etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
                            etrExchangeTrade001Dto.setFromEtrExchange("Y");
                            etrExchangeTrade001Dto.setDisRate(item.getDisRate());
                            etrExchangeTrade001Dto.setOptType(dto.getOptType());
                            etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt2.平安资产类型_信用保护凭证.getValue());
                            List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
                            EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
                            BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
                            portfolioList.add(portfolioRequest);
                            etrExchangeTrade001Dto.setPortfolioList(portfolioList);

                            etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
                        }
                        if (StringUtil.isNotEmpty(dto.getIsDoRkRedisFlag()) && StringUtil.equals(dto.getIsDoRkRedisFlag(),"true")) {
                            // ----------预占功能 start----------
                            String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                            // add by zhangrui1 start
                            AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", etrExchangeTrade001DtoList,
                                    infCode);
                            if (null != ar) {
                            }
                            ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList,
                                    (etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
                            // ----------预占功能 end----------
                        }
                        ProcessParameter<EtrCreditProtectionCertificateApplyRequest> processParameter = new ProcessParameter<EtrCreditProtectionCertificateApplyRequest>();
                        WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> params = etrExchangeTradeCpc001Service.getWorkFlowParameter(dto);
                        params.setPageId(dto.getPageId());
                        params.setElementId(dto.getElementId());
                        params.setBusinessType(businessType);
                        params.setBusinessCode(item->item.getEntrustId());
                        params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                                + "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
                                + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
                        processParameter.setWorkFlowParameter(params);
                        msg = etrExchangeTradeCpc001Service.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrCreditProtectionCertificateApplyRequest[etrExchangeTrade001DtoList.size()]));
                    }
                    return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
                }else{
                    if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                        BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                        String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                        dto.setEntrustId(code);
                    }
                    //业务编号生成规则参数
                    ProcessParameter<EtrCreditProtectionCertificateApplyRequest> processParameter = new ProcessParameter<EtrCreditProtectionCertificateApplyRequest>();
                    WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> params = etrExchangeTradeCpc001Service.getWorkFlowParameter(dto);
                    params.setPageId(dto.getPageId());
                    params.setElementId(dto.getElementId());
                    params.setBusinessType(businessType);
                    params.setBusinessCode(dto.getEntrustId());
                    String entrustIdName=StringUtils.isNotEmpty(dto.getEntrustId())?dto.getEntrustId():"";
                    params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entrustIdName + ")"
                            + "-" + "业务类型"+ "(" + entity.getBusinessTypeName() + ")-"
                            + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
                    processParameter.setWorkFlowParameter(params);
                    msg = etrExchangeTradeCpc001Service.start(processParameter, dto);
                    return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
                }
            }
            Map<String,List<EtrExchangeTradePortfolioSecRepoRequest>> groupMap= dto.getPortfolioList().stream().collect(
                    Collectors.groupingBy(item->item.getAcctNo()));			//如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
            if(groupMap.keySet().size() == 1 && dto.getPortfolioList().size()>1) {
                EtrExchangeTradePortfolioSecRepoRequest item=dto.getPortfolioList().get(0);
                //业务编号生成规则参数
                if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                    BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                    String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                    dto.setEntrustId(code);
                }
                BigDecimal totalEntrustAmt = null;
                if(dto.getPortfolioList().get(0).getEntrustAmt()!=null){
                    totalEntrustAmt = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustAmt).reduce(BigDecimal::add).get();
                }
                BigDecimal totalEntrustNum = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustNum).reduce(BigDecimal::add).get();
                //算比例
                BigDecimal tempSumDisRate=BigDecimal.ZERO;
                for(int i=0;i<dto.getPortfolioList().size();i++){
                    EtrExchangeTradePortfolioSecRepoRequest tempItem=dto.getPortfolioList().get(i);
                    BigDecimal disRate=BigDecimal.ZERO;
                    if(i<dto.getPortfolioList().size()-1) {
                        disRate=ArithmeticUtil.divide(tempItem.getEntrustNum(),totalEntrustNum,2);
                        tempSumDisRate=tempSumDisRate.add(disRate);
                    }else {
                        disRate=ArithmeticUtil.subtract(new BigDecimal("1"),tempSumDisRate);
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
                    List<EtrCreditProtectionCertificateApplyRequest> etrExchangeTrade001DtoList = new ArrayList<>();
                    List<EtrCreditProtectionCertificateApplyRequest> riskPreEtrExchangeTrade001DtoList = new ArrayList<>();

                    for(EtrExchangeTradePortfolioSecRepoRequest item2:dto.getPortfolioList()) {
                        EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = etrExchangeTradeCpc001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item2);
                        if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
                            BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                            String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                            etrExchangeTrade001Dto.setEntrustId(code);
                        }
                        etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                        etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                        etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                        etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
                        etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
                        etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
                        etrExchangeTrade001Dto.setFromEtrExchange("Y");
                        etrExchangeTrade001Dto.setDisRate(item2.getDisRate());

                        List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
                        EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
                        BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
                        portfolioList.add(portfolioRequest);
                        etrExchangeTrade001Dto.setPortfolioList(portfolioList);

                        etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
                        EtrCreditProtectionCertificateApplyRequest preRiskEtrExchangeTrade001Dto=new EtrCreditProtectionCertificateApplyRequest();
                        BeanUtil.copyPropertiesIgnoreNull(preRiskEtrExchangeTrade001Dto,etrExchangeTrade001Dto);
                        riskPreEtrExchangeTrade001DtoList.add(preRiskEtrExchangeTrade001Dto);
                    }
                    riskEtrExchangeTrade001DtoList=riskPreEtrExchangeTrade001DtoList;

                    String infCode= BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                    dto.setFromEtrExchange("Y");//场内指令
                    AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", riskPreEtrExchangeTrade001DtoList,
                            infCode);
                    if (StringUtil.isNotEmpty(ar)) {
                        return new Message<AjaxResult>().success().setData(ar);
                    }
                    ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), riskPreEtrExchangeTrade001DtoList,
                            (etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
                }
                ProcessParameter<EtrCreditProtectionCertificateApplyRequest> processParameter = new ProcessParameter<EtrCreditProtectionCertificateApplyRequest>();
                WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> params = new WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest>();
                params.setPageId(dto.getPageId());
                params.setElementId(dto.getElementId());
                params.setBusinessType(businessType);
                params.setBusinessCode(dto.getEntrustId());
                params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                        + "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
                        + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
                processParameter.setWorkFlowParameter(params);
                try {
                    msg = etrExchangeTradeCpc001Service.start(processParameter, dto);
                    AjaxResult m =new AjaxResult(msg);
                    return new Message<AjaxResult>().success().setData(m);
                }catch (Exception e) {
                    logger.error("场内交易指令新建失败",e);
                    //撤销风险预占
                    riskEtrExchangeTrade001DtoList.stream().forEach(itemTemp->{
                        Map<String,Object> marTrd= JsonUtils.object2Map(itemTemp);
                        exchangeEntrustRdsCommonAop.revokeBackRiskCommonInteface(marTrd,"etrExchangeTrade001CommonService");
                    });
                    return PositionUtil.ajaxErrorReturn(e);
                }
            }
            //多组合指令
            else {
                List<EtrCreditProtectionCertificateApplyRequest> etrExchangeTrade001DtoList = new ArrayList<>();
                List<EtrCreditProtectionCertificateApplyRequest> riskPreEtrExchangeTrade001DtoList = new ArrayList<>();
                for(EtrExchangeTradePortfolioSecRepoRequest item:dto.getPortfolioList()) {
                    EtrCreditProtectionCertificateApplyRequest etrExchangeTrade001Dto = etrExchangeTradeCpc001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item);
                    if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
                        BussinessCodeParameter param = etrExchangeTradeCpc001Service.genBussinessCodeParameter(dto);
                        String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                        etrExchangeTrade001Dto.setEntrustId(code);
                    }
                    etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                    etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                    etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                    etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
                    etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
                    etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
                    etrExchangeTrade001Dto.setFromEtrExchange("Y");
                    etrExchangeTrade001Dto.setDisRate(item.getDisRate());

                    List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
                    EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
                    BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
                    portfolioList.add(portfolioRequest);
                    etrExchangeTrade001Dto.setPortfolioList(portfolioList);
                    //设置头寸使用序列号
                    Long positionSeqno = (long) Arrays.deepHashCode(new Object[] { etrExchangeTrade001Dto.getEntrustId(), "ETR",new Date().getTime(),Thread.currentThread().getId() });
                    etrExchangeTrade001Dto.setPositionSeqno(positionSeqno);
                    etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
                    EtrCreditProtectionCertificateApplyRequest preRiskEtrExchangeTrade001Dto=new EtrCreditProtectionCertificateApplyRequest();
                    BeanUtil.copyPropertiesIgnoreNull(preRiskEtrExchangeTrade001Dto,etrExchangeTrade001Dto);
                    riskPreEtrExchangeTrade001DtoList.add(preRiskEtrExchangeTrade001Dto);
                }


                // ----------预占功能 start----------
                if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
                    riskEtrExchangeTrade001DtoList=riskPreEtrExchangeTrade001DtoList;
                    String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
                    //风险用新的list

                    // add by zhangrui1 start
                    AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", riskPreEtrExchangeTrade001DtoList,
                            infCode);
                    if (null != ar) {
                        //return new Message<AjaxResult>().success().setData(ar);
                    }

                    ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), riskEtrExchangeTrade001DtoList,
                            (etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
                }
                // ----------预占功能 end----------

                ProcessParameter<EtrCreditProtectionCertificateApplyRequest> processParameter = new ProcessParameter<EtrCreditProtectionCertificateApplyRequest>();
                WorkFlowParameter<EtrCreditProtectionCertificateApplyRequest> params = etrExchangeTradeCpc001Service.getWorkFlowParameter(dto);
                params.setPageId(dto.getPageId());
                params.setElementId(dto.getElementId());
                params.setBusinessType(businessType);
                params.setBusinessCode(item->item.getEntrustId());
                params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
                        + "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
                        + "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
                processParameter.setWorkFlowParameter(params);
                try {
                    msg = etrExchangeTradeCpc001Service.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrCreditProtectionCertificateApplyRequest[etrExchangeTrade001DtoList.size()]));
                    AjaxResult m =new AjaxResult(msg);
                    return new Message<AjaxResult>().success().setData(m);
                }catch (Exception e) {
                    for(EtrCreditProtectionCertificateApplyRequest etrExchangeTradeSaveSecRepoRequest :etrExchangeTrade001DtoList) {
                        logger.warn("场内交易多组合指令头寸发送失败，EntrustId:{},PositionSeqno:{}", etrExchangeTradeSaveSecRepoRequest.getEntrustId(), etrExchangeTradeSaveSecRepoRequest.getPositionSeqno().toString());
                        PositionHttpRequest.sendRevoke(etrExchangeTradeSaveSecRepoRequest.getPositionSeqno().toString(), "ETR", "场内交易多组合指令头寸发送失败");
                    }
                    //撤销风险预占
                    riskEtrExchangeTrade001DtoList.stream().forEach(itemTemp->{
                        Map<String,Object> marTrd= JsonUtils.object2Map(itemTemp);
                        exchangeEntrustRdsCommonAop.revokeBackRiskCommonInteface(marTrd,"etrExchangeTrade001CommonService");
                    });
                    logger.error("场内交易多组合指令新建失败",e);
                    return PositionUtil.ajaxErrorReturn(e);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Message<AjaxResult>().fail().setData(new AjaxResult("保存失败"));
        }

    }

    //add wsz 20220610 添加方法查询最小交易额
    @ApiOperation(value = "最小价格", notes = "最小价格")
    @PostMapping("/getMinTrdUnitLimit")
    @ResponseBody
    public Message<MinTrdUnitLimitDto> getMinTrdUnitLimit(@RequestBody EtrExchangeMiniLimitPriceRequest request){
        try {
            MinTrdUnitLimitDto minNum= etrExchangeTradeCpc001Service.getMinTrdUnitLimitPrice(request);
            return new Message<MinTrdUnitLimitDto>().success().setData(minNum);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message<MinTrdUnitLimitDto>().fail().setMessage("查询失败");
        }
    }

    /**
     * 获取最小交易单元
     * @param request
     * @return
     */
    @ApiOperation(value = "获取最小交易单元", notes = "获取最小交易单元")
    @ResponseBody
    @PostMapping("/getMinNumStep")
    public Message<String> getMinNumStep(@RequestBody MinNumStepRequest request){
        try{
            Map<String, Object> param = BeanUtil.object2Map(request);
            BigDecimal minNum = etrExchangeTradeCpc001Service.getMinNumStep(param);
            if(minNum == null) {
                minNum= BigDecimal.ZERO;
            }
            String minNumStr=minNum.setScale(2).toString();
            return new Message<>(minNumStr);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<String>().fail().setMessage("查询异常");
        }
    }


}
