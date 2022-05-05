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
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice002Service;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
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
 * 本币交易对话报价
 */
@Controller
@PageId("IbuQuoteBond002")
@RequestMapping("/cfets/bond/IbuQuoteBond002")
@Api(tags = "CFETS债券交易-对话")
public class IbuQuoteBond002Controller extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;

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
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种,DictCode.分配方式, DictCode.业务模式,DictCode.价格模式);

        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_2", BussinessDictCodeList.清算速度_T加2.getValue());
        model.addAttribute("yesNoFlag_yes", BussinessDictCodeList.是否_是.getValue());
        model.addAttribute("assetTypeOne_abs", BussinessDictCodeList.平安资产一级分类_资产支持证券.getValue());
        model.addAttribute("calcTypeUnitCprice", BussinessDictCodeList.计算选择方式_净价.getValue());
        model.addAttribute("calcTypeYield", BussinessDictCodeList.计算选择方式_到期收益率.getValue());
        model.addAttribute("calcTypeExecYield", BussinessDictCodeList.计算选择方式_行权收益率.getValue());
        // 本方交易员是否可改
        model.addAttribute("ouTraderIdCanEditFlag", CacheUtil.getSysParam("ibuFlag_ouTraderIdCanEdit"));
        // 无委托指令是否允许回复
        model.addAttribute("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));

        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();

        // 目前流程中场合只适用撤销逻辑,因为新建没走流程拿不到数据留痕表的记录，只能根据报价代码反查  20201225开始新增走流程了
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute(GlobalConstant.WORKFLOW_ID,getParam(GlobalConstant.WORKFLOW_ID));
//            cfetsBondDealDialogueQuoteDto.setAvaNum(cfetsBondDealDialogueQuoteDto.getAvaNum().multiply(new BigDecimal(10000)));
            /*if (cfetsBondDealDialogueQuoteDto.getQuoteId() != null){
                cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId());*/
            // 此处判断做市方是为了联动显示回复量
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_做市方.getValue())){
                cfetsBondDealDialogueQuoteDto.setQuotaPageModel("marketResponse");
            }
            if(StringUtils.isEmpty(cfetsBondDealDialogueQuoteDto.getPriceModel())){
                CfetsBondDealDialogueQuoteDto entrustInfo = cfetsBondDealQuotedPrice002Service.queryEntrustInfoById(
                        cfetsBondDealDialogueQuoteDto.getEntrustId());
                if (entrustInfo != null && entrustInfo.getPriceModel() != null) {
                    cfetsBondDealDialogueQuoteDto.setPriceModel(entrustInfo.getPriceModel());
                }
            }
            if (StringUtils.isEmpty(cfetsBondDealDialogueQuoteDto.getAssetTypeOne()) && StringUtils.isNotEmpty(cfetsBondDealDialogueQuoteDto.getFinprodId())) {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("initValue",cfetsBondDealDialogueQuoteDto.getFinprodId());
                List<SelectOptionDto> lst = cfetsQuotedCommonService.queryFinprodIdList(param);
                if(null != lst && lst.size() > 0){
                    JSONObject json = (JSONObject) JSONObject.toJSON(lst.get(0).getObject());
                    cfetsBondDealDialogueQuoteDto.setAssetTypeOne(json.get("assetType1") != null ? json.get("assetType1").toString() :"");
                    cfetsBondDealDialogueQuoteDto.setIsRight(json.get("isRight") != null ? json.get("isRight").toString() :"");
                }
            }
            /*}*/
        }
        else if (StringUtil.isNotEmpty(this.getParam("entrustIdList"))) {
            List <String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, (String)this.getParam("entrustIdList"));
            List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList = new ArrayList<>();
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.queryEntrustInfoByEntrustId(entrustIdList.get(0));
            BigDecimal avaNum = BigDecimal.ZERO;
            BigDecimal price = cfetsBondDealDialogueQuoteDto.getPrice();
            for(String entrustId:entrustIdList){
                CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote = cfetsBondDealQuotedPrice002Service.queryEntrustInfoByEntrustId(entrustId);
//            	cfetsBondDealDialogueQuote.
                avaNum = com.joyintech.calculation.util.ArithmeticUtil.doAdd(avaNum,cfetsBondDealDialogueQuote.getAvaNum());
                cfetsBondDealDialogueQuote.setEntrustAvaNum(cfetsBondDealDialogueQuote.getAvaNum());
                cfetsBondDealDialogueQuote.setAvaNum(null);
                if(StringUtil.equals(cfetsBondDealDialogueQuoteDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                    if(price.compareTo(cfetsBondDealDialogueQuote.getPrice())>0){
                        price=cfetsBondDealDialogueQuote.getPrice();
                    }
                }else{
                    if(price.compareTo(cfetsBondDealDialogueQuote.getPrice())<0){
                        price=cfetsBondDealDialogueQuote.getPrice();
                    }
                }
                cfetsBondDealDialogueQuoteDtoList.add(cfetsBondDealDialogueQuote);
            }
            cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealDialogueQuoteDtoList);
            cfetsBondDealDialogueQuoteDto.setAvaNum(avaNum);
            cfetsBondDealDialogueQuoteDto.setPrice(price);;
        }
        else if (StringUtil.isNotEmpty(this.getParam("quoteId"))) {
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                    this.getParam("quoteId"),this.getParam("statusValid"));
            cfetsBondDealDialogueQuoteDto.setAvaNum(com.joyintech.calculation.util.ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000),2));
            //组合分配列表
            List <CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList =cfetsBondDealQuotedPrice002Service.getCfetsBondDealDialogueQuoteDtoList(this.getParam("quoteId"));
            for(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote:cfetsBondDealDialogueQuoteDtoList){
                cfetsBondDealDialogueQuote.setMatureYield(cfetsBondDealDialogueQuoteDto.getMatureYield());
                cfetsBondDealDialogueQuote.setExerYield(cfetsBondDealDialogueQuoteDto.getExerYield());
            }
            cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealDialogueQuoteDtoList);
            // 补充本方交易员(做市报价回复的场景)
            if (StringUtil.equals(this.getParam("quotaPageModel"), "marketResponse")) {
                if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getMySideTrader())) {
                    cfetsBondDealDialogueQuoteDto.setMySideTrader(LoginUserUtil.getUserName());
                }
                if (cfetsBondDealDialogueQuoteDto.getResponseAmt() == null) {
                    cfetsBondDealDialogueQuoteDto.setResponseAmt(cfetsBondDealDialogueQuoteDto.getAvaNum());
                }
                // add by zhangliang 2020-07-20  此处特殊处理是因为撤回我方回复做市报价原来的委托代码依然会记录
                if (!StringUtil.equals(this.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
                    cfetsBondDealDialogueQuoteDto.setEntrustId(null);
                }
            }
            // 复制模式打开时需要把报价编号置空
            if (StringUtil.equals(this.getParam("openModel"), "copy")) {
                cfetsBondDealDialogueQuoteDto.setQuoteId(null);
                // 复制的场合券面总额默认为剩余额度
                if (cfetsBondDealDialogueQuoteDto.getEntrustId() != null) {
                    CfetsBondDealDialogueQuoteDto entrustInfo = cfetsBondDealQuotedPrice002Service.queryEntrustInfoById(
                            cfetsBondDealDialogueQuoteDto.getEntrustId());
                    if (entrustInfo != null && entrustInfo.getEntrustNum() != null) {
                        cfetsBondDealDialogueQuoteDto.setAvaNum(entrustInfo.getAvaNum());
                    }
                }
                else if (!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())) {
                    // 多组合场景
                    // 券面总额：默认未完成各单笔指令剩余额度总和
                    // 组合信息：根据各指令带出，券面总额默认为各指令剩余额度。已完成指令不再显示在列表中。
                    BigDecimal tempSumAvaAmt = BigDecimal.ZERO;
                    List<CfetsBondDealDialogueQuoteDto> tempEntrustList = new ArrayList<>();
                    for (CfetsBondDealDialogueQuoteDto tempDto : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                        if (BigDecimal.ZERO.compareTo(tempDto.getEntrustAvaNum()) < 0) {
                            tempDto.setAvaNum(tempDto.getEntrustAvaNum());
                            tempSumAvaAmt = com.joyintech.framework.util.ArithmeticUtil.doAdd(tempSumAvaAmt, tempDto.getEntrustAvaNum());
                            tempEntrustList.add(tempDto);
                        }
                    }
                    cfetsBondDealDialogueQuoteDto.setAvaNum(tempSumAvaAmt);
                    cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(tempEntrustList);
                }
            }
        }
        // 无委托模式
        else {
            // 设置默认的银行间交易员为登录用户
            cfetsBondDealDialogueQuoteDto.setMySideTrader(cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        }

        // 设置默认值
        // 结算方式
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleMethod())) {
            cfetsBondDealDialogueQuoteDto.setSettleMethod(
                    BussinessDictCodeList.结算方式_券款对付.getValue());
        }
        // 清算类型
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleType())) {
            cfetsBondDealDialogueQuoteDto.setSettleType(
                    BussinessDictCodeList.清算类型_全额清算.getValue());
        }
        // 结算币种
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleCcy())) {
            cfetsBondDealDialogueQuoteDto.setSettleCcy(BussinessDictCodeList.币种_人民币.getValue());
        }
        // 清算速度
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleSpreed())) {
            cfetsBondDealDialogueQuoteDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
        }
        // 清算速度
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleDate())) {
            cfetsBondDealDialogueQuoteDto.setSettleDate(iPlatformDate.getSysDate());
        }

        // 画面模式
        if (StringUtil.isNotEmpty(this.getParam("quotaPageModel"))) {
            cfetsBondDealDialogueQuoteDto.setQuotaPageModel(this.getParam("quotaPageModel"));
        }

        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
            cfetsBondDealDialogueQuoteDto.setCalculateField(DictCodeListExt3.债券计算模式_单位净价.getValue());
        }
        model.addAttribute("assetTypeOne",cfetsBondDealDialogueQuoteDto.getAssetTypeOne());

        model.addAttribute("PRICE_MODEL",cfetsBondDealDialogueQuoteDto.getPriceModel());
        model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute(GlobalConstant.ENTITY, cfetsBondDealDialogueQuoteDto);
        model.addAttribute("cfetsBondDealDialogueQuoteDtoList", JsonUtils.object2Json(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()));
        model.addAttribute("quotaPageModel",this.getParam("quotaPageModel"));
        return super.init(model);
    }

    @ApiOperation(value = "对话报价初始化", notes = "对话报价初始化")
    @PostMapping("/initPrice")
    @ResponseBody
    public Message<CfetsBondDealQuoteCommonDto> initPrice(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto){
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        String quoteId = cfetsBondDealQuoteCommonDto.getQuoteId();
        String quotaPageModel = cfetsBondDealQuoteCommonDto.getQuotaPageModel();
        cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                quoteId,cfetsBondDealQuoteCommonDto.getValidStatus());
        cfetsBondDealDialogueQuoteDto.setAvaNum(com.joyintech.calculation.util.ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(10000),2));
        //组合分配列表
        List <CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList =cfetsBondDealQuotedPrice002Service.getCfetsBondDealDialogueQuoteDtoList(quoteId);
        /*for(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote:cfetsBondDealDialogueQuoteDtoList){
            cfetsBondDealDialogueQuote.setMatureYield(cfetsBondDealDialogueQuoteDto.getMatureYield());
            cfetsBondDealDialogueQuote.setExerYield(cfetsBondDealDialogueQuoteDto.getExerYield());
        }*/
        cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealDialogueQuoteDtoList);
        // 补充本方交易员(做市报价回复的场景)
        if (StringUtil.equals(quotaPageModel, "marketResponse")) {
            if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getMySideTrader())) {
                cfetsBondDealDialogueQuoteDto.setMySideTrader(LoginUserUtil.getUserName());
            }
            if (cfetsBondDealDialogueQuoteDto.getResponseAmt() == null) {
                cfetsBondDealDialogueQuoteDto.setResponseAmt(cfetsBondDealDialogueQuoteDto.getAvaNum());
            }
            // add by zhangliang 2020-07-20  此处特殊处理是因为撤回我方回复做市报价原来的委托代码依然会记录
            if (!StringUtil.equals(this.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
                cfetsBondDealDialogueQuoteDto.setEntrustId(null);
            }
        }
        // 复制模式打开时需要把报价编号置空
        if (StringUtil.equals(cfetsBondDealQuoteCommonDto.getOpenModel(), "copy")) {
            cfetsBondDealDialogueQuoteDto.setQuoteId(null);
            // 复制的场合券面总额默认为剩余额度
            if (cfetsBondDealDialogueQuoteDto.getEntrustId() != null) {
                CfetsBondDealDialogueQuoteDto entrustInfo = cfetsBondDealQuotedPrice002Service.queryEntrustInfoById(
                        cfetsBondDealDialogueQuoteDto.getEntrustId());
                if (entrustInfo != null && entrustInfo.getEntrustNum() != null) {
                    cfetsBondDealDialogueQuoteDto.setAvaNum(entrustInfo.getAvaNum());
                }
            }
            else if (!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())) {
                // 多组合场景
                // 券面总额：默认未完成各单笔指令剩余额度总和
                // 组合信息：根据各指令带出，券面总额默认为各指令剩余额度。已完成指令不再显示在列表中。
                BigDecimal tempSumAvaAmt = BigDecimal.ZERO;
                List<CfetsBondDealDialogueQuoteDto> tempEntrustList = new ArrayList<>();
                for (CfetsBondDealDialogueQuoteDto tempDto : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                    if (BigDecimal.ZERO.compareTo(tempDto.getEntrustAvaNum()) < 0) {
                        tempDto.setAvaNum(tempDto.getEntrustAvaNum());
                        tempSumAvaAmt = com.joyintech.framework.util.ArithmeticUtil.doAdd(tempSumAvaAmt, tempDto.getEntrustAvaNum());
                        tempEntrustList.add(tempDto);
                    }
                }
                cfetsBondDealDialogueQuoteDto.setAvaNum(tempSumAvaAmt);
                cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(tempEntrustList);
            }
        }
        //修改
        if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getOpenModel(), "edit")){
            for (CfetsBondDealDialogueQuoteDto item : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                //恢复指令剩余额度
                item.setEntrustAvaNum(ArithmeticUtil.doAdd(item.getEntrustAvaNum(),item.getAvaNum()));
            }
        }
        //复制操作 返回前台
        cfetsBondDealQuoteCommonDto = cfetsQuotedCommonServiceVue.createDialogue2(cfetsBondDealDialogueQuoteDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())){
            cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsQuotedCommonServiceVue.createPtlAllocationList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()));
        }
        //信贷资产支持证券、资产支持票据锚定值应默认净价且不可修改
        if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getType11(),DictCodeListExt3.行内资产四级分类_交易商协会ABN.getValue())||StringUtil.equals(cfetsBondDealQuoteCommonDto.getType11(),DictCodeListExt3.行内资产四级分类_银保监会主管ABS.getValue())){
            cfetsBondDealQuoteCommonDto.setCalculateField("01");
        }
        cfetsBondDealQuoteCommonDto.setMatureYield(ArithmeticUtil.divide(cfetsBondDealQuoteCommonDto.getMatureYield(),new BigDecimal(100),6));
        cfetsBondDealQuoteCommonDto.setExerYield(ArithmeticUtil.divide(cfetsBondDealQuoteCommonDto.getExerYield(),new BigDecimal(100),6));
        return new Message<CfetsBondDealQuoteCommonDto>().success().setData(cfetsBondDealQuoteCommonDto);
    }

     /**
     * 保存验证
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    @ApiOperation(value = "报价提交检查", notes = "报价提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody  CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        AjaxResult ar = new AjaxResult();
        ar = cfetsBondDealQuotedPrice002Service.checkNotEmpty(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(ar.getErrorList())){
            return new Message<AjaxResult>().success().setData(ar);
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.checkcheckAnchoring(cfetsBondDealQuoteCommonDto);
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.positionCheck(cfetsBondDealQuoteCommonDto);
        }
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = cfetsQuotedCommonServiceVue.createDialogue(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealDialogueQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        }
        StringBuffer sb = new StringBuffer();
        String isDzh = "Y";
        List<ValidMessage> valid = cfetsBondDealQuotedPrice002Service.valid(cfetsBondDealDialogueQuoteDto);
        if(CollectionUtils.isNotEmpty(valid)){
            ar.setErrorList(valid);
            return new Message<AjaxResult>().success().setData(ar);
        }
        // 委托剩余额度
        List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuotes = cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList();
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")){
            if(StringUtil.equals(isDzh,"Y")){
                if(null!=cfetsBondDealDialogueQuotes){
                    cfetsBondDealDialogueQuotes.stream().forEach(item -> {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto2 = new CfetsBondDealDialogueQuoteDto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto2,cfetsBondDealDialogueQuoteDto);
                        cfetsBondDealDialogueQuoteDto2.setEntrustId(item.getEntrustId());
                        cfetsBondDealDialogueQuoteDto2.setAvaNum(item.getAvaNum());
                        cfetsBondDealQuotedPrice002Service.checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto2, null, sb, false);
                        if (sb.length() > 0){
                            return;
                        }
                    });
                }
            }else{
                cfetsBondDealQuotedPrice002Service.checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto, null, sb, false);
            }
        }

        // 偏离度
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_dealDeviation"), "02")
                && StringUtil.isNotEmpty(CacheUtil.getSysParam("ibuVar_dealDeviationVal"))){
            cfetsBondDealQuotedPrice002Service.checkDealDeviation(cfetsBondDealDialogueQuoteDto, null, sb, false);
        }

        // 限价
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            if (StringUtil.equals(isDzh, "Y")) {
                if (null != cfetsBondDealDialogueQuotes) {
                    cfetsBondDealDialogueQuotes.stream().forEach(item -> {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto2 = new CfetsBondDealDialogueQuoteDto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto2, cfetsBondDealDialogueQuoteDto);
                        cfetsBondDealDialogueQuoteDto2.setEntrustId(item.getEntrustId());
                        cfetsBondDealDialogueQuoteDto2.setPrice(item.getPrice());
                        cfetsBondDealQuotedPrice002Service.checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto2, null, sb, false);
                    });
                }
            }
            else {
                cfetsBondDealQuotedPrice002Service.checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto, null, sb, false);
            }
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar= cfetsQuotedCommonServiceVue.reclasCheck(cfetsBondDealQuoteCommonDto);
        }
        /*if (sb.length() > 0){
            sb.append("请确认是否继续？");
            ar.setData(sb);
        }
        else{
            String msg = this.getMessage("SysCommon.save");
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "submit")) {
                msg = this.getMessage("SysCommon.submit");
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                msg = "成交";
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "refuse")) {
                msg = "拒绝";
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
    public AjaxResult checkByHome(@RequestBody  CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        AjaxResult ar = new AjaxResult();
        ar = cfetsBondDealQuotedPrice002Service.checkNotEmpty(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(ar.getErrorList())){
            return ar;
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.checkcheckAnchoring(cfetsBondDealQuoteCommonDto);
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar = cfetsQuotedCommonServiceVue.positionCheck(cfetsBondDealQuoteCommonDto);
        }
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = cfetsQuotedCommonServiceVue.createDialogue(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealDialogueQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
        }
        StringBuffer sb = new StringBuffer();
        String isDzh = "Y";
        List<ValidMessage> valid = cfetsBondDealQuotedPrice002Service.valid(cfetsBondDealDialogueQuoteDto);
        if(CollectionUtils.isNotEmpty(valid)){
            ar.setErrorList(valid);
            return ar;
        }
        // 委托剩余额度
        List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuotes = cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList();
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")){
            if(StringUtil.equals(isDzh,"Y")){
                if(null!=cfetsBondDealDialogueQuotes){
                    cfetsBondDealDialogueQuotes.stream().forEach(item -> {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto2 = new CfetsBondDealDialogueQuoteDto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto2,cfetsBondDealDialogueQuoteDto);
                        cfetsBondDealDialogueQuoteDto2.setEntrustId(item.getEntrustId());
                        cfetsBondDealDialogueQuoteDto2.setAvaNum(item.getAvaNum());
                        cfetsBondDealQuotedPrice002Service.checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto2, null, sb, false);
                        if (sb.length() > 0){
                            return;
                        }
                    });
                }
            }else{
                cfetsBondDealQuotedPrice002Service.checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto, null, sb, false);
            }
        }

        // 偏离度
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_dealDeviation"), "02")
                && StringUtil.isNotEmpty(CacheUtil.getSysParam("ibuVar_dealDeviationVal"))){
            cfetsBondDealQuotedPrice002Service.checkDealDeviation(cfetsBondDealDialogueQuoteDto, null, sb, false);
        }

        // 限价
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            if (StringUtil.equals(isDzh, "Y")) {
                if (null != cfetsBondDealDialogueQuotes) {
                    cfetsBondDealDialogueQuotes.stream().forEach(item -> {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto2 = new CfetsBondDealDialogueQuoteDto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto2, cfetsBondDealDialogueQuoteDto);
                        cfetsBondDealDialogueQuoteDto2.setEntrustId(item.getEntrustId());
                        cfetsBondDealDialogueQuoteDto2.setPrice(item.getPrice());
                        cfetsBondDealQuotedPrice002Service.checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto2, null, sb, false);
                    });
                }
            }
            else {
                cfetsBondDealQuotedPrice002Service.checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto, null, sb, false);
            }
        }
        if(CollectionUtils.isEmpty(ar.getErrorList())){
            ar= cfetsQuotedCommonServiceVue.reclasCheck(cfetsBondDealQuoteCommonDto);
        }
        /*if (sb.length() > 0){
            sb.append("请确认是否继续？");
            ar.setData(sb);
        }
        else{
            String msg = this.getMessage("SysCommon.save");
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "submit")) {
                msg = this.getMessage("SysCommon.submit");
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                msg = "成交";
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "refuse")) {
                msg = "拒绝";
            }

            ar.setData(this.getMessage("COMMON_C_0001", msg));
        }*/
        return ar;
    }

    @ApiOperation(value = "对话报价拒绝", notes = "对话报价拒绝")
    @ResponseBody
    @RequestMapping("saveData")
    public Message<AjaxResult> saveData(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {

        cfetsBondDealQuotedPrice002Service.save(cfetsBondDealDialogueQuoteDto);

        return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS")));
    }

    @ApiOperation(value = "对话报价提交(保存)", notes = "对话报价提交(保存)")
    @ResponseBody
    @RequestMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto, BindingResult br) throws WorkFlowException {
      /*  if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }*/
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = cfetsQuotedCommonServiceVue.createDialogue(cfetsBondDealQuoteCommonDto);
        if(CollectionUtils.isNotEmpty(cfetsBondDealQuoteCommonDto.getPtlAllocatios())){
            cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsQuotedCommonServiceVue.createCfetsBondDealDialogueQuoteDtoList(cfetsBondDealQuoteCommonDto.getPtlAllocatios()));
                //处理下组合的可用现金和可用持仓  带到审批画面
                for (CfetsBondDealDialogueQuoteDto item : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
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
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setSeqNo("IBU_001");
            String code = bussGetBussinessIdService != null
                    ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
            cfetsBondDealDialogueQuoteDto.setQuoteId(code);
        }
        ProcessParameter<CfetsBondDealDialogueQuoteDto> processParameter = new ProcessParameter<CfetsBondDealDialogueQuoteDto>();
        WorkFlowParameter<CfetsBondDealDialogueQuoteDto> params = cfetsBondDealQuotedPrice002Service.getWorkFlowParameter(cfetsBondDealDialogueQuoteDto);
        params.setPageId("IbuQuoteBond002");
        params.setElementId(cfetsBondDealDialogueQuoteDto.getElementId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_对话.getValue());
        params.setBusinessCode(cfetsBondDealDialogueQuoteDto.getQuoteId());
        cfetsBondDealDialogueQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券对话报价.getValue());
        //报价操作
        String quoteOperatorType = "发起";
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "marketResponse")) {
            quoteOperatorType = "回复";
        }
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "dialogueResponse")) {
            // 对话回应
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                quoteOperatorType = "成交";
                cfetsBondDealDialogueQuoteDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
            } else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "refuse")) {
                cfetsBondDealDialogueQuoteDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
            }
        }
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")) {
            quoteOperatorType = "发起";
        }
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "edit")) {
            quoteOperatorType = "修改";
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "submit")) {
                cfetsBondDealDialogueQuoteDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
            }
        }
        cfetsBondDealDialogueQuoteDto.setQuoteOperatorType(quoteOperatorType);
        DecimalFormat  df = new DecimalFormat("###,###.00");
        params.setComment(entity -> "报价日期"+ "(" + DateUtil.format(entity.getSettleDate()) + ")-"
                + "资产简称("+entity.getFinprodAbbr()+")-" + "对手方("+entity.getCounterName()+")-"+"报价操作("+entity.getQuoteOperatorType()+")-"+"券面总额("+df.format(entity.getAvaNum().setScale(2))+"万元)");
        processParameter.setWorkFlowParameter(params);

        cfetsBondDealDialogueQuoteDto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
        cfetsBondDealDialogueQuoteDto.setCreateUser(LoginUserUtil.getUserName());// 用户名
        cfetsBondDealDialogueQuoteDto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
        cfetsBondDealDialogueQuoteDto.setCreateTime(new Date());// 创建时间
        String msg = null;
        try{
            msg = cfetsBondDealQuotedPrice002Service.start(processParameter, cfetsBondDealDialogueQuoteDto);
        }catch (Exception e){
            e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


    /**
     * 现券买卖历史报价 撤销
     * @param cfetsBondDealQuoteCommonDto
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation(value = "对话历史报价撤销", notes = "对话历史报价撤销")
    @ResponseBody
    @RequestMapping("bondRevoke")
    public Message<AjaxResult> bondRevoke(@RequestBody CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) throws WorkFlowException {
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,cfetsBondDealQuoteCommonDto);
        ProcessParameter<CfetsBondDealDialogueQuoteDto> processParameter = new ProcessParameter<CfetsBondDealDialogueQuoteDto>();
        WorkFlowParameter<CfetsBondDealDialogueQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsBondDealDialogueQuoteDto>();
        workFlowParameter.setPageId("CfetsBondDealQuotedPrice002");
        workFlowParameter.setElementId("CfetsBondDealQuotedPrice002_btn_bondRevoke");
        workFlowParameter.setBusinessCode(cfetsBondDealDialogueQuoteDto.getQuoteId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_对话.getValue());
        //调用初始化接口  供描述用
        CfetsBondDealDialogueQuoteDto bondDialogueQuote = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(cfetsBondDealQuoteCommonDto.getQuoteId(), BussinessDictCodeList.有效状态_有效.getValue());
        cfetsBondDealDialogueQuoteDto.setSettleDate(bondDialogueQuote.getSettleDate());
        cfetsBondDealDialogueQuoteDto.setFinprodAbbr(bondDialogueQuote.getFinprodAbbr());
        cfetsBondDealDialogueQuoteDto.setCounterName(bondDialogueQuote.getCounterName());
        cfetsBondDealDialogueQuoteDto.setAvaNum(bondDialogueQuote.getAvaNum());
        DecimalFormat df = new DecimalFormat("###,###.00");
        workFlowParameter.setComment(entity -> "报价日期"+ "(" + DateUtil.format(entity.getSettleDate()) + ")-"
                + "资产简称("+entity.getFinprodAbbr()+")-" + "对手方("+entity.getCounterName()+")-"+"报价操作(撤销)-"+"券面总额("+df.format(entity.getAvaNum().setScale(2))+"万元)");
        processParameter.setWorkFlowParameter(workFlowParameter);

        String msg = cfetsBondDealQuotedPrice002Service.start(processParameter, cfetsBondDealDialogueQuoteDto);

        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

}

