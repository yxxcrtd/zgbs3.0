package com.joyintech.tams.cfets.trd.bond.controller;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice003Service;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CounterInfoDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 本币交易请求报价
 */
@Controller
@PageId("IbuQuoteBond003")
@RequestMapping("/cfets/bond/IbuQuoteBond003")
@Api(tags = "CFETS债券交易-请求")
public class IbuQuoteBond003Controller extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice003Service cfetsBondDealQuotedPrice003Service;

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
    private BaseGetBussinessId bussGetBussinessIdService;

    @Autowired
    private com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService cfetsQuotedCommonServiceVue;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private StlSecAcctInfoMapper stlSecAcctInfoMapper;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;


    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度,
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种,  DictCode.分配方式,DictCode.业务模式,DictCode.价格模式);

        model.addAttribute("test123", "01");
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("CNY", BussinessDictCodeList.币种_人民币.getValue());
        model.addAttribute("SETT_MODE_03", BussinessDictCodeList.结算方式_券款对付.getValue());
        model.addAttribute("CLEAR_TYPE_02", BussinessDictCodeList.清算类型_全额清算.getValue());
        model.addAttribute("isRevert", BussinessDictCodeList.是否_是.getValue());
        model.addAttribute("effTime", CacheUtil.getSysParam("ibuVar_quoteEffTime"));
        // 本方交易员是否可改
        model.addAttribute("ouTraderIdCanEditFlag", CacheUtil.getSysParam("ibuFlag_ouTraderIdCanEdit"));

        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            model.addAttribute("busWorkflowId",getParam(GlobalConstant.WORKFLOW_ID));
            cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
//            cfetsBondDealRequestQuoteDto.setAvaNum(cfetsBondDealRequestQuoteDto.getAvaNum().multiply(new BigDecimal(10000)));
            if ((null == cfetsBondDealRequestQuoteDto.getCounterInfoList()|| org.springframework.util.CollectionUtils.isEmpty(cfetsBondDealRequestQuoteDto.getCounterInfoList()))
                    && null!=cfetsBondDealRequestQuoteDto.getCounterIdList() && cfetsBondDealRequestQuoteDto.getCounterIdList().size() > 0) {
                List<CounterInfoDto> counterInfoList = new ArrayList<CounterInfoDto>();
                for (String counterId : cfetsBondDealRequestQuoteDto.getCounterIdList()) {
                    CounterInfoDto counterInfoDto = new CounterInfoDto();
                    counterInfoDto.setCounterId(counterId);
                    Map<String, Object> param = new HashMap<String, Object>();
                    param.put("initValue", counterId);
                    List<SelectOptionDto> lst = cfetsQuotedCommonService.queryMySideList(param);
                    if (null != lst && lst.size() > 0) {
                        counterInfoDto.setCounterValue(lst.get(0).getText());
                    }
                    counterInfoList.add(counterInfoDto);
                    cfetsBondDealRequestQuoteDto.setCounterInfoList(counterInfoList);
                }
            } else if (null != cfetsBondDealRequestQuoteDto.getCounterInfoList()
                    && cfetsBondDealRequestQuoteDto.getCounterInfoList().size() > 0) {
                for (CounterInfoDto counterInfoDto : cfetsBondDealRequestQuoteDto.getCounterInfoList()) {
                    counterInfoDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_已回复.getValue());
                }
                model.addAttribute("isRevert", BussinessDictCodeList.是否_是.getValue());
                model.addAttribute("YES_OR_NO_Y", BussinessDictCodeList.是否_是.getValue());
                model.addAttribute("tradeStatus12", BussinessDictCodeList.银行间上行报价状态_已回复.getCodeName());
            }
            if (StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getFinprodId()) && StringUtils.isEmpty(cfetsBondDealRequestQuoteDto.getFinprodMarketId())) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("initValue",cfetsBondDealRequestQuoteDto.getFinprodId());
                List<SelectOptionDto> lst = cfetsQuotedCommonService.queryFinprodIdList(param);
                if(null != lst && lst.size() > 0){
                    JSONObject json = (JSONObject) JSONObject.toJSON(lst.get(0).getObject());
                    cfetsBondDealRequestQuoteDto.setFinprodMarketId(json.get("finprodMarketId") != null ? json.get("finprodMarketId").toString() :"");
                }
            }
            /*if (cfetsBondDealRequestQuoteDto.getQuoteId() != null) {
                cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(
                    cfetsBondDealRequestQuoteDto.getQuoteId());
            }*/
        }
        else if (StringUtil.isNotEmpty(this.getParam("entrustIdList"))) {
            List <String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, (String)this.getParam("entrustIdList"));
            List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
            cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.queryEntrustInfoByEntrustId(entrustIdList.get(0));
            BigDecimal avaNum = BigDecimal.ZERO;
            BigDecimal price = cfetsBondDealRequestQuoteDto.getPrice();
            for(String entrustId:entrustIdList){
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuote = cfetsBondDealQuotedPrice003Service.queryEntrustInfoByEntrustId(entrustId);
//            	cfetsBondDealDialogueQuote.
                avaNum =ArithmeticUtil.doAdd(avaNum,cfetsBondDealRequestQuote.getAvaNum());
                cfetsBondDealRequestQuote.setEntrustAvaNum(cfetsBondDealRequestQuote.getAvaNum());
                cfetsBondDealRequestQuote.setAvaNum(null);
                if(StringUtil.equals(cfetsBondDealRequestQuoteDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                    if(price.compareTo(cfetsBondDealRequestQuote.getPrice())>0){
                        price=cfetsBondDealRequestQuote.getPrice();
                    }
                }else{
                    if(price.compareTo(cfetsBondDealRequestQuote.getPrice())<0){
                        price=cfetsBondDealRequestQuote.getPrice();
                    }
                }
                cfetsBondDealRequestQuoteDtoList.add(cfetsBondDealRequestQuote);
            }
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsBondDealRequestQuoteDtoList);
            cfetsBondDealRequestQuoteDto.setAvaNum(avaNum);
            cfetsBondDealRequestQuoteDto.setPrice(price);
        }
        else if (StringUtil.isNotEmpty(this.getParam("quoteId"))) {
            cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(this.getParam("quoteId"),this.getParam("statusValid"));

            cfetsBondDealRequestQuoteDto.setAvaNum(ArithmeticUtil.divide(cfetsBondDealRequestQuoteDto.getAvaNum(),new BigDecimal(10000),2));
            //组合分配列表
            List <CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList =cfetsBondDealQuotedPrice003Service.getCfetsBondDealRequestQuoteDtoList(this.getParam("quoteId"));
            for(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuote:cfetsBondDealRequestQuoteDtoList){
                cfetsBondDealRequestQuote.setMatureYield(cfetsBondDealRequestQuoteDto.getMatureYield());
                cfetsBondDealRequestQuote.setExerYield(cfetsBondDealRequestQuoteDto.getExerYield());
            }
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsBondDealRequestQuoteDtoList);


            // 画面打开模式(区分做市报价还是请求报价)
            if (StringUtil.isNotEmpty(this.getParam("quotaPageModel"))) {
                cfetsBondDealRequestQuoteDto.setQuotaPageModel(this.getParam("quotaPageModel"));
            }
            // 区分校验类型
            if (StringUtil.isNotEmpty(this.getParam("checkType"))) {
                cfetsBondDealRequestQuoteDto.setCheckType(this.getParam("checkType"));
            }
            // 复制模式打开时需要把报价编号置空
            if (StringUtil.equals(this.getParam("openModel"), "copy")) {
                cfetsBondDealRequestQuoteDto.setQuoteId(null);
                // 复制的场合券面总额默认为剩余额度
                if (cfetsBondDealRequestQuoteDto.getEntrustId() != null) {
                    CfetsBondDealRequestQuoteDto entrustInfo = cfetsBondDealQuotedPrice003Service.queryEntrustInfoByEntrustId(
                            cfetsBondDealRequestQuoteDto.getEntrustId());
                    if (entrustInfo != null && entrustInfo.getEntrustNum() != null) {
                        cfetsBondDealRequestQuoteDto.setAvaNum(entrustInfo.getAvaNum());
                    }
                }
                else if (!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList())) {
                    // 多组合场景
                    // 券面总额：默认未完成各单笔指令剩余额度总和
                    // 组合信息：根据各指令带出，券面总额默认为各指令剩余额度。已完成指令不再显示在列表中。
                    BigDecimal tempSumAvaAmt = BigDecimal.ZERO;
                    List<CfetsBondDealRequestQuoteDto> tempEntrustList = new ArrayList<>();
                    for (CfetsBondDealRequestQuoteDto tempDto : cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()) {
                        if (BigDecimal.ZERO.compareTo(tempDto.getEntrustAvaNum()) < 0) {
                            tempDto.setAvaNum(tempDto.getEntrustAvaNum());
                            tempSumAvaAmt = com.joyintech.framework.util.ArithmeticUtil.doAdd(tempSumAvaAmt, tempDto.getEntrustAvaNum());
                            tempEntrustList.add(tempDto);
                        }
                    }
                    cfetsBondDealRequestQuoteDto.setAvaNum(tempSumAvaAmt);
                    cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(tempEntrustList);
                }
            }
            //add by yzg 多机构请求报价分开展示 20210126 start
            /*if(StringUtil.equals(this.getParam("openMode"), "5") && null != cfetsBondDealRequestQuoteDto.getCounterInfoList()
					&& cfetsBondDealRequestQuoteDto.getCounterInfoList().size() > 1) {
            	List<String> arr = StringUtil.splitToList("",this.getParam("cpPartyId").toString());
            	List<CounterInfoDto> list = new ArrayList<>();
            	for(CounterInfoDto counterInfoDto : cfetsBondDealRequestQuoteDto.getCounterInfoList()) {
            		if(arr.contains(counterInfoDto.getCounterId())) {
            			list.add(counterInfoDto);
            		}
            	}
            	cfetsBondDealRequestQuoteDto.setCounterInfoList(list);
            }*/
            //add by yzg 多机构请求报价分开展示 20210126 end
        }
        // 无委托模式
        else {
            cfetsBondDealRequestQuoteDto.setMySideTrader(cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        }
        // 结算方式
        if (!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getSettleMethod())) {
            cfetsBondDealRequestQuoteDto.setSettleMethod(
                    BussinessDictCodeList.结算方式_券款对付.getValue());
        }
        // 清算类型
        if (!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getSettleType())) {
            cfetsBondDealRequestQuoteDto.setSettleType(
                    BussinessDictCodeList.清算类型_全额清算.getValue());
        }
        // 结算币种
        if (!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getSettleCcy())) {
            cfetsBondDealRequestQuoteDto.setSettleCcy(BussinessDictCodeList.币种_人民币.getValue());
        }
        // 画面模式
        if (StringUtil.isNotEmpty(this.getParam("quotaPageModel"))) {
            cfetsBondDealRequestQuoteDto.setQuotaPageModel(this.getParam("quotaPageModel"));
        }

        model.addAttribute(GlobalConstant.ENTITY, cfetsBondDealRequestQuoteDto);

        // 判断是否是回复按钮跳转的标志
        String isRevert = this.getParam("isRevert");
        //流程中判断
        if(StringUtil.isEmpty(isRevert)&&null!=cfetsBondDealRequestQuoteDto&&StringUtil.equals(cfetsBondDealRequestQuoteDto.getQuotaPageModel(),"response")){
            isRevert = BussinessDictCodeList.是否_是.getValue();
        }
        if (StringUtil.isNotEmpty(isRevert)) {
            model.addAttribute("isRevert", isRevert);
            model.addAttribute("YES_OR_NO_Y", BussinessDictCodeList.是否_是.getValue());
            model.addAttribute("tradeStatus12", BussinessDictCodeList.银行间上行报价状态_已回复.getCodeName());
        } else if (StringUtils.equals(cfetsBondDealRequestQuoteDto.getTradeStatus(),
                BussinessDictCodeList.银行间上行报价状态_成交中.getValue()) ||
                StringUtils.equals(cfetsBondDealRequestQuoteDto.getTradeStatus(),
                        BussinessDictCodeList.银行间上行报价状态_已回复.getValue())) {
            model.addAttribute("isRevert", BussinessDictCodeList.是否_是.getValue());
            model.addAttribute("YES_OR_NO_Y", BussinessDictCodeList.是否_是.getValue());
            model.addAttribute("tradeStatus12", BussinessDictCodeList.银行间上行报价状态_已回复.getCodeName());
        }
        // 判断
        String quoteRoleType = this.getParam("quoteRoleType");
        String sendStatus = this.getParam("sendStatus");
        String tradeStatus = this.getParam("tradeStatus");
        if (StringUtil.isNotEmpty(quoteRoleType)) {
            model.addAttribute("initQuoteRoleType", quoteRoleType);
        }
        if (StringUtil.isNotEmpty(sendStatus)) {
            model.addAttribute("initSendStatus", sendStatus);
        }
        if (StringUtil.isNotEmpty(tradeStatus)) {
            model.addAttribute("initTradeStatus", tradeStatus);
        }

        // 获取画面选中债券是否为含权债
        if (cfetsBondDealRequestQuoteDto != null
                && StringUtil.isNotBlank(cfetsBondDealRequestQuoteDto.getFinprodId())) {
            model.addAttribute("assetIsRight",
                    cfetsBondDealQuotedPrice003Service.selectAssetIsRight(
                            cfetsBondDealRequestQuoteDto.getFinprodId()));
        }

        model.addAttribute("cfetsBondDealRequestQuoteDtoList", JsonUtils.object2Json(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()));
        model.addAttribute("quotaPageModel",this.getParam("quotaPageModel"));
        return super.init(model);
    }

    @ApiOperation(value = "请求报价初始化", notes = "请求报价初始化")
    @PostMapping("/initPrice")
    @ResponseBody
    public Message<CfetsBondDealQuoteCommonDto> initPrice(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
        String quoteId = cfetsBondDealQuoteCommonDto.getQuoteId();
        String quotaPageModel = cfetsBondDealQuoteCommonDto.getQuotaPageModel();
        cfetsBondDealRequestQuoteDto = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(quoteId,cfetsBondDealQuoteCommonDto.getValidStatus());

        cfetsBondDealRequestQuoteDto.setAvaNum(ArithmeticUtil.divide(cfetsBondDealRequestQuoteDto.getAvaNum(),new BigDecimal(10000),2));
        //组合分配列表
        List <CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList =cfetsBondDealQuotedPrice003Service.getCfetsBondDealRequestQuoteDtoList(quoteId);
        /*for(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuote:cfetsBondDealRequestQuoteDtoList){
            cfetsBondDealRequestQuote.setMatureYield(cfetsBondDealRequestQuoteDto.getMatureYield());
            cfetsBondDealRequestQuote.setExerYield(cfetsBondDealRequestQuoteDto.getExerYield());
        }*/
        cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsBondDealRequestQuoteDtoList);


        // 画面打开模式(区分做市报价还是请求报价)
        if (StringUtil.isNotEmpty(quotaPageModel)) {
            cfetsBondDealRequestQuoteDto.setQuotaPageModel(quotaPageModel);
        }
        // 区分校验类型
        if (StringUtil.isNotEmpty(this.getParam("checkType"))) {
            cfetsBondDealRequestQuoteDto.setCheckType(this.getParam("checkType"));
        }
        // 复制模式打开时需要把报价编号置空
        if (StringUtil.equals(cfetsBondDealQuoteCommonDto.getOpenModel(), "copy")) {
            cfetsBondDealRequestQuoteDto.setQuoteId(null);
            // 复制的场合券面总额默认为剩余额度
            if (cfetsBondDealRequestQuoteDto.getEntrustId() != null) {
                CfetsBondDealRequestQuoteDto entrustInfo = cfetsBondDealQuotedPrice003Service.queryEntrustInfoByEntrustId(
                        cfetsBondDealRequestQuoteDto.getEntrustId());
                if (entrustInfo != null && entrustInfo.getEntrustNum() != null) {
                    cfetsBondDealRequestQuoteDto.setAvaNum(entrustInfo.getAvaNum());
                }
            }
            else if (!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList())) {
                // 多组合场景
                // 券面总额：默认未完成各单笔指令剩余额度总和
                // 组合信息：根据各指令带出，券面总额默认为各指令剩余额度。已完成指令不再显示在列表中。
                BigDecimal tempSumAvaAmt = BigDecimal.ZERO;
                List<CfetsBondDealRequestQuoteDto> tempEntrustList = new ArrayList<>();
                for (CfetsBondDealRequestQuoteDto tempDto : cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()) {
                    if (BigDecimal.ZERO.compareTo(tempDto.getEntrustAvaNum()) < 0) {
                        tempDto.setAvaNum(tempDto.getEntrustAvaNum());
                        tempSumAvaAmt = com.joyintech.framework.util.ArithmeticUtil.doAdd(tempSumAvaAmt, tempDto.getEntrustAvaNum());
                        tempEntrustList.add(tempDto);
                    }
                }
                cfetsBondDealRequestQuoteDto.setAvaNum(tempSumAvaAmt);
                cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(tempEntrustList);
            }
        }
        //回复操作
        if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getQuotaPageModel(),"response")){
            //恢复指令额度
            for (CfetsBondDealRequestQuoteDto item : cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()) {
                //恢复指令剩余额度
                item.setEntrustAvaNum(ArithmeticUtil.doAdd(item.getEntrustAvaNum(),item.getAvaNum()));
            }
        }
        //复制操作 返回前台
        cfetsBondDealQuoteCommonDto = cfetsQuotedCommonServiceVue.createReueust2(cfetsBondDealRequestQuoteDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList())){
            cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsQuotedCommonServiceVue.createRequestPtlAllocationList(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()));
        }
        return new Message<CfetsBondDealQuoteCommonDto>().success().setData(cfetsBondDealQuoteCommonDto);
    }

    /**
     * 保存验证
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    @ApiOperation(value = "请求报价提交检查", notes = "请求报价提交检查")
    @ResponseBody
    @RequestMapping("check")
    public Message<AjaxResult> check(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        AjaxResult ar = new AjaxResult();
        StringBuffer sb = new StringBuffer();
        ar = cfetsBondDealQuotedPrice003Service.checkNotEmpty(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(ar.getErrorList())){
            return new Message<AjaxResult>().success().setData(ar);
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.positionCheck(cfetsBondDealQuoteCommonDto);
        }
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsQuotedCommonServiceVue.createReueust(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealRequestQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        }
        List<ValidMessage> valid = cfetsBondDealQuotedPrice003Service.valid(cfetsBondDealRequestQuoteDto);
        if(CollectionUtils.isNotEmpty(valid)){
            ar.setErrorList(valid);
            return new Message<AjaxResult>().success().setData(ar);
        }

        // 银行间报价-现券买卖报价-超委托的价格校验开关（01 硬阻挡 02 软阻挡）
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            cfetsBondDealQuotedPrice003Service.checkCalcFieldEntrust(cfetsBondDealRequestQuoteDto,
                    null, sb, false);
        }

        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")) {
            List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList();
            for(CfetsBondDealRequestQuoteDto dto : cfetsBondDealRequestQuoteDtoList) {
                if (dto.getEntrustAvaNum() == null
                        || dto.getAvaNum().compareTo(dto.getEntrustAvaNum()) > 0) {
                    sb.append(this.getMessage("CFETS_COMMON_E_041",dto.getEntrustId()));
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
            }
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar= cfetsQuotedCommonServiceVue.reclasCheck(cfetsBondDealQuoteCommonDto);
        }
        /*if (sb.length() > 0) {
            sb.append("请确认是否继续？");
            ar.setData(sb);
        }
        else {
            String msg = this.getMessage("SysCommon.save");
            if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getOperatorType(), "submit")) {
                msg = this.getMessage("SysCommon.submit");
            }
            ar.setData(this.getMessage("COMMON_C_0001", msg));
        }*/
        return new Message<AjaxResult>().success().setData(ar);
    }

    /**
     * 待办事项页面，保存验证
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    @RequestMapping("checkByHome")
    @ResponseBody
    public AjaxResult checkByHome(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        AjaxResult ar = new AjaxResult();
        StringBuffer sb = new StringBuffer();
        ar = cfetsBondDealQuotedPrice003Service.checkNotEmpty(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(ar.getErrorList())){
            return ar;
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.positionCheck(cfetsBondDealQuoteCommonDto);
        }
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsQuotedCommonServiceVue.createReueust(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealRequestQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        }
        List<ValidMessage> valid = cfetsBondDealQuotedPrice003Service.valid(cfetsBondDealRequestQuoteDto);
        if(CollectionUtils.isNotEmpty(valid)){
            ar.setErrorList(valid);
            return ar;
        }

        // 银行间报价-现券买卖报价-超委托的价格校验开关（01 硬阻挡 02 软阻挡）
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            cfetsBondDealQuotedPrice003Service.checkCalcFieldEntrust(cfetsBondDealRequestQuoteDto,
                    null, sb, false);
        }

        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")) {
            List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList();
            for(CfetsBondDealRequestQuoteDto dto : cfetsBondDealRequestQuoteDtoList) {
                if (dto.getEntrustAvaNum() == null
                        || dto.getAvaNum().compareTo(dto.getEntrustAvaNum()) > 0) {
                    sb.append(this.getMessage("CFETS_COMMON_E_041",dto.getEntrustId()));
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
            }
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar= cfetsQuotedCommonServiceVue.reclasCheck(cfetsBondDealQuoteCommonDto);
        }
        /*if (sb.length() > 0) {
            sb.append("请确认是否继续？");
            ar.setData(sb);
        }
        else {
            String msg = this.getMessage("SysCommon.save");
            if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getOperatorType(), "submit")) {
                msg = this.getMessage("SysCommon.submit");
            }
            ar.setData(this.getMessage("COMMON_C_0001", msg));
        }*/
        return ar;
    }

    /**
     * 成交验证
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    @ApiOperation(value = "请求报价成交检查", notes = "请求报价成交检查")
    @ResponseBody
    @RequestMapping("clinchDealCheck")
    public Message<AjaxResult> clinchDealCheck(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        AjaxResult ar = new AjaxResult();
        StringBuffer sb = new StringBuffer();
        List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsQuotedCommonServiceVue.createReueust(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealRequestQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        }
        List<com.joyintech.tams.cfets.trd.bond.dto.CounterInfoDto> counterInfoList = cfetsBondDealQuoteCommonDto.getCounterInfoList();
        if(CollectionUtils.isEmpty(counterInfoList)){
            sb.append(this.getMessage("请选择可选机构"));
        }else{
            if(counterInfoList.size()>1){
                sb.append(this.getMessage("COMMON_E_0058"));
            }else{
                counterInfoList.forEach(item ->{
                    //状态会否变更
                    if(!StringUtil.equals(item.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已回复.getValue())){
                        sb.append(this.getMessage("CFETS_COMMON_R_028"));
                        return;
                    }
                    if(StringUtil.isNotEmpty(cfetsBondDealQuoteCommonDto.getQuoteId())&&StringUtil.isNotEmpty(item.getCpPartyId())){
                        if(!StringUtil.equals(item.getTradeStatus(),cfetsIbuQuoteBondMapper.getTradeStatus(cfetsBondDealQuoteCommonDto.getQuoteId(),item.getCpPartyId()))){
                            sb.append("可选机构交易状态已变更，无法成交!");
                            return;
                        }
                    }
                });
            }
        }
        // 银行间报价-现券买卖报价-超委托剩余额度校验开关（01 硬阻挡 02 软阻挡）
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")
                && StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getEntrustId())) {
            List<CounterInfoDto> list = cfetsBondDealRequestQuoteDto.getCounterInfoList();
            if (!StringUtil.isEmptyList(list)) {
                CounterInfoDto counterInfoDto = list.get(0);
                BigDecimal shareAmt = counterInfoDto.getShareAmt();
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", cfetsBondDealRequestQuoteDto.getEntrustId());
                param.put("quoteId", cfetsBondDealRequestQuoteDto.getQuoteId());
                BigDecimal dealedAmt = cfetsBondDealQuotedPrice003Service.queryEntrustDealedAmt(
                        param);
                if (StringUtil.isNotEmpty(shareAmt)
                        && ArithmeticUtil.multiply(shareAmt, new BigDecimal(10000)).compareTo(
                        ArithmeticUtil.subtract(cfetsBondDealRequestQuoteDto.getEntrustAmt(),
                                dealedAmt)) > 0) {
                    sb.append(this.getMessage("CFETS_COMMON_E_021"));
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
            }
        }

        // 银行间报价-现券买卖报价-超委托的价格校验开关（01 硬阻挡 02 软阻挡）
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            cfetsBondDealQuotedPrice003Service.checkCalcFieldEntrust(cfetsBondDealRequestQuoteDto, null, sb, false);
        }
        if(sb.length()>0){
            ar.appendMessage(new ValidMessage(sb.toString()));
        }
        // 委托为空的场合只有新建模式且系统参数为软校验
        /*if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"), "02")
                && !StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getEntrustId())) {
            sb.append(this.getMessage("CFETS_COMMON_E_039"));
            sb.append(GlobalConstant.HTML_NEWLINE);
        }*/

        /*if (sb.length() > 0) {
            sb.append("请确认是否继续？");
            ar.setData(sb);
        }
        else {
            ar.setData(this.getMessage("COMMON_C_0001", this.getMessage("SysCommon.save")));
        }*/

        return new Message<AjaxResult>().success().setData(ar);
    }

    /**
     * 保存提交
     * @param cfetsBondDealRequestQuoteDto
     * @return
     */
    @ResponseBody
    @RequestMapping("saveData")
    public AjaxResult saveData(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {

        cfetsBondDealQuotedPrice003Service.save(cfetsBondDealRequestQuoteDto);

        return new AjaxResult(this.getMessage("CFETS_COMMON_SAVE"));
    }

    /**
     * 保存提交
     * @param cfetsBondDealRequestQuoteDto
     * @return
     */
    @ResponseBody
    @RequestMapping("submit")
    public AjaxResult submit(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {

        cfetsBondDealQuotedPrice003Service.submit(cfetsBondDealRequestQuoteDto);

        return new AjaxResult(this.getMessage("CFETS_COMMON_SUBMIT"));
    }

    /**
     * 成交
     * @param cfetsBondDealRequestQuoteDto
     * @return
     */
    @ResponseBody
    @RequestMapping("clinchDeal")
    public AjaxResult clinchDeal(@RequestBody CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {

        cfetsBondDealQuotedPrice003Service.clinchDeal(cfetsBondDealRequestQuoteDto);

        return new AjaxResult(this.getMessage("CFETS_COMMON_TRADE"));
    }

    /**
     * 现券买卖历史报价 撤销
     * @param cfetsBondDealQuoteCommonDto
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation(value = "请求历史报价撤销", notes = "请求历史报价撤销")
    @ResponseBody
    @RequestMapping("bondRevoke")
    public Message<AjaxResult> bondRevoke(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto)
            throws WorkFlowException {
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,cfetsBondDealQuoteCommonDto);
        ProcessParameter<CfetsBondDealRequestQuoteDto> processParameter = new ProcessParameter<CfetsBondDealRequestQuoteDto>();
        WorkFlowParameter<CfetsBondDealRequestQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsBondDealRequestQuoteDto>();
        workFlowParameter.setPageId("CfetsBondDealQuotedPrice003");
        workFlowParameter.setElementId("CfetsBondDealQuotedPrice003_btn_bondRevoke");
        workFlowParameter.setBusinessCode(cfetsBondDealRequestQuoteDto.getQuoteId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_请求.getValue());
        //调用初始化接口  供描述用
        CfetsBondDealRequestQuoteDto bondRequestQuote = cfetsBondDealQuotedPrice003Service.getBondRequestQuote(cfetsBondDealQuoteCommonDto.getQuoteId(), BussinessDictCodeList.有效状态_有效.getValue());
        cfetsBondDealRequestQuoteDto.setSettleDate(bondRequestQuote.getSettleDate());
        cfetsBondDealRequestQuoteDto.setFinprodAbbr(bondRequestQuote.getFinprodAbbr());
        cfetsBondDealRequestQuoteDto.setAvaNum(bondRequestQuote.getAvaNum());
        DecimalFormat df = new DecimalFormat("###,###.##");
        workFlowParameter.setComment(entity -> "报价日期"+ "(" + DateUtil.format(entity.getSettleDate()) + ")-"
                + "资产简称("+entity.getFinprodAbbr()+")-" +"报价操作("+entity.getQuoteOperatorType()+")-"+"券面总额("+df.format(entity.getAvaNum().setScale(2))+"万元)");
        processParameter.setWorkFlowParameter(workFlowParameter);

        String msg = cfetsBondDealQuotedPrice003Service.start(processParameter,
                cfetsBondDealRequestQuoteDto);

        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


    @ApiOperation(value = "请求报价提交(保存)", notes = "请求报价登记提交(保存)")
    @ResponseBody
    @RequestMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto, BindingResult br) throws WorkFlowException {
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = cfetsQuotedCommonServiceVue.createReueust(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealRequestQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
            //处理下组合的可用现金和可用持仓  带到审批画面
            for (CfetsBondDealRequestQuoteDto item : cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList()) {
                try{
                    Map<String,String> accnoParm = new HashMap<>();
                    accnoParm.put("ccy",cfetsBondDealQuoteCommonDto.getCcy());
                    accnoParm.put("portfolioId",item.getPortfolioId());
                    accnoParm.put("acctType", DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    String accNo = cfetsIbuQuoteBondEtrMapper.getAccNo(accnoParm);
                    if(StringUtil.isEmpty(accNo)){
                        accNo = stlSecAcctInfoMapper.selectByPrimaryKey(cfetsBondDealQuoteCommonDto.getSecManageAcctId()).getTrdAcctId();
                    }
                    BaseMap baseMap = positionInfoService.queryTrdAvailableCash(Arrays.asList(item.getPortfolioId()), DateUtil.format(cfetsBondDealQuoteCommonDto.getTradeDate()), accNo, cfetsBondDealQuoteCommonDto.getCcy(), "03", Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue()), null);
                    if(null!=baseMap&&baseMap.containsKey("cashT")){
                        item.setProAvailableCash((BigDecimal) baseMap.get("cashT"));
                    }
                    BaseMap baseMapSec = positionInfoService.queryTrdPosition(Arrays.asList(item.getPortfolioId()), DateUtil.format(cfetsBondDealQuoteCommonDto.getTradeDate()), cfetsBondDealQuoteCommonDto.getFinprodId());
                    if(null!=baseMapSec&&baseMapSec.containsKey("ZCTRKYCC")){
                        item.setSecManageAvailablePosition((BigDecimal)baseMapSec.get("ZCTRKYCC"));
                    }
                }catch (Exception e){

                }
            }
        }
        
        
        if (!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getQuoteId())) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setSeqNo("IBU_002");
            String code = bussGetBussinessIdService != null
                    ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
            cfetsBondDealRequestQuoteDto.setQuoteId(code);
        }
        ProcessParameter<CfetsBondDealRequestQuoteDto> processParameter = new ProcessParameter<CfetsBondDealRequestQuoteDto>();
        WorkFlowParameter<CfetsBondDealRequestQuoteDto> params = cfetsBondDealQuotedPrice003Service.getWorkFlowParameter(cfetsBondDealRequestQuoteDto);
        params.setPageId("IbuQuoteBond003");
        params.setElementId(cfetsBondDealRequestQuoteDto.getElementId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_请求.getValue());
        params.setBusinessCode(cfetsBondDealRequestQuoteDto.getQuoteId());
        cfetsBondDealRequestQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券请求报价.getValue());
        //报价操作
        String quoteOperatorType = "发起";
        if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getQuotaPageModel(), "response")&&StringUtil.equals(cfetsBondDealRequestQuoteDto.getOperatorType(), "done")) {
            quoteOperatorType = "成交";
            cfetsBondDealRequestQuoteDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
        }
        cfetsBondDealRequestQuoteDto.setQuoteOperatorType(quoteOperatorType);
        DecimalFormat  df = new DecimalFormat("###,###.00");
        params.setComment(entity -> "报价日期"+ "(" + DateUtil.format(entity.getSettleDate()) + ")-"
                + "资产简称("+entity.getFinprodAbbr()+")-" +"报价操作("+entity.getQuoteOperatorType()+")-"+"券面总额("+df.format(entity.getAvaNum().setScale(2))+"万元)");
        processParameter.setWorkFlowParameter(params);
        cfetsBondDealRequestQuoteDto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
        cfetsBondDealRequestQuoteDto.setCreateUser(LoginUserUtil.getUserName());// 用户名
        cfetsBondDealRequestQuoteDto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
        cfetsBondDealRequestQuoteDto.setCreateTime(new Date());// 创建时间
        String msg = null;
        try{
            msg = cfetsBondDealQuotedPrice003Service.start(processParameter, cfetsBondDealRequestQuoteDto);
        }catch (Exception e){
            e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

}

