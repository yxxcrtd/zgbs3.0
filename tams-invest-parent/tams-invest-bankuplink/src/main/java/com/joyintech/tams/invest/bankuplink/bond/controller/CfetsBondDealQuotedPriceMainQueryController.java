/*
 * 文件名：CfetsBondDealQuotedPrice001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.controller.FileUploadController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteImportPreviewDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice001Service;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice002Service;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice003Service;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPrice004Service;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPriceSingleService;
import com.joyintech.tams.invest.bankuplink.bond.validator.CfetsBondDealDialogueQuoteDtoValidator;
import com.joyintech.tams.invest.bankuplink.bond.validator.CfetsBondDealRequestQuoteDtoValidator;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.bankuplink.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * CFETS现券买卖报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月7日
 * @see CfetsBondDealQuotedPriceMainQueryController
 * @since 1.0
 */
@Controller
@PageId("CfetsBondDealQuotedPriceMainQuery")
@RequestMapping("/invest/bankuplink/bond/CfetsBondDealQuotedPriceMainQuery")
public class CfetsBondDealQuotedPriceMainQueryController extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice001Service cfetsBondDealQuotedPrice001Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPrice004Service cfetsBondDealQuotedPrice004Service;


    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPriceSingleService cfetsBondDealQuotedPriceSingleService;
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Value("${application.rocketmq.topic.bond:trcms_pfms_tbank_bond}")
    private String bondTopic;

    @Autowired
    private CfetsBondDealQuotedPrice002Controller cfetsBond002Controller;

    @Autowired
    private CfetsBondDealQuotedPrice003Controller cfetsBond003Controller;

    @Autowired
    private CfetsBondDealDialogueQuoteDtoValidator cfetsDialogueValidator;

    @Autowired
    private CfetsBondDealRequestQuoteDtoValidator cfetsRequestValidator;

    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;

    @Autowired
    private BaseGetBussinessId bussGetBussinessIdService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfetsBondDealQuotedPrice003Service cfetsBondDealQuotedPrice003Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度,BussinessDictCode.交易类型);
        model.addAttribute("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        model.addAttribute("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        model.addAttribute("quoteTypeDialog", BussinessDictCodeList.交易类型_对话报价.getValue());
        model.addAttribute("quoteTypeRequest", BussinessDictCodeList.交易类型_请求报价.getValue());
        model.addAttribute("quoteRoleTypeXJ", BussinessDictCodeList.银行间上行本方角色_询价方.getValue());
        model.addAttribute("quoteRoleTypeHF", BussinessDictCodeList.银行间上行本方角色_回复方.getValue());
        model.addAttribute("quoteRoleTypeFZS", BussinessDictCodeList.银行间上行本方角色_非做市方.getValue());
        model.addAttribute("quoteRoleTypeZS", BussinessDictCodeList.银行间上行本方角色_做市方.getValue());
        model.addAttribute("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));
        model.addAttribute("ps_pay_name", BussinessDictCodeList.交易方向_买入.getCodeName());
        model.addAttribute("ps_sell_name", BussinessDictCodeList.交易方向_卖出.getCodeName());
        model.addAttribute("sendStatus_fail", BussinessDictCodeList.银行间上行发送状态_失败.getCodeName());
        model.addAttribute("sendStatus_unsend", BussinessDictCodeList.银行间上行发送状态_待发送.getCodeName());
        model.addAttribute("sendStatus_send", BussinessDictCodeList.银行间上行发送状态_已发送.getCodeName());
        model.addAttribute("quoteStatus_added", BussinessDictCodeList.银行间上行报价状态_新增中.getCodeName());
        this.setSelectOptionToModel(model, BussinessDictCode.匹配状态,BussinessDictCode.交易类型_CSTP);
        model.addAttribute("loginTraderId", cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        // 定义topic
        String plateDate = iPlatformDate.getSysDateStr();
        model.addAttribute("plateDate", DateUtil.parse(plateDate));
        model.addAttribute("bondTopic", bondTopic);
        return super.init(model);
    }

    /**
     * 根据查询符合条件的委托列表
     *
     * @param param 入参
     * @return 委托列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryEntrustOrderList")
    public List<CfetsBondDealEntrustOrderDto> queryEntrustOrderList() {
        return cfetsBondDealQuotedPrice001Service.queryEntrustOrderList(
                RequestUtil.getPageParam());
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @param parameter 页面参数
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export() throws Exception {
        Map<String, Object> param = RequestUtil.getPageParam();
//		if (null != param.get("exportType") && StringUtil.equals(param.get("exportType").toString(), "cstp")) {
//			return cfetsBondDealQuotedPrice004Service.export(this.getPageId() + "_04", RequestUtil.getPageParam());
//		} else {
//			return cfetsBondDealQuotedPrice001Service.export(this.getPageId() + "_01", RequestUtil.getPageParam());
//		}
        if (null != param.get("exportType") && StringUtil.equals(param.get("exportType").toString(), "EntrustOrder")) {
            return cfetsBondDealQuotedPrice001Service.export("现券报价_指令执行"+ SystemGlobalConstant.OPEN_PARENTHESIS
                    + DateUtil.format(DateUtil.platDateFromCache())
                    + SystemGlobalConstant.CLOSE_PARENTHESIS, RequestUtil.getPageParam());
        }
        else if (null != param.get("exportType") && StringUtil.equals(param.get("exportType").toString(), "dayOffer")) {
            String datas = this.getParam("datas");
            List<BondQuoteInfoDto> param1 = cfetsBondDealQuotedPrice001Service.getDayOfferTableList(param);
            File outFile =  cfetsBondDealQuotedPriceSingleService.makeFile(
                    "/com/joyintech/tams/invest/bankuplink/bond/conf/export_conf_dayOffer.xml",
                    param1);
            return ExportUtil.export(
                    "现券报价_委托查询" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);
        }
        else if (null != param.get("exportType") && StringUtil.equals(param.get("exportType").toString(), "CSTPSelect")) {
            return cfetsBondDealQuotedPrice004Service.export("现券报价_成交查询"+ SystemGlobalConstant.OPEN_PARENTHESIS
                    + DateUtil.format(DateUtil.platDateFromCache())
                    + SystemGlobalConstant.CLOSE_PARENTHESIS, RequestUtil.getPageParam());
        } else {
            return cfetsBondDealQuotedPrice001Service.export(this.getPageId() + "_01", RequestUtil.getPageParam());
        }
    }

    /**
     * 查询报价编号列表
     *
     * @return 报价编号列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/queryCfetsQuoteIdList")
    public Page<SuggestDto> queryCfetsQuoteIdList() {
        return cfetsBondDealQuotedPrice001Service.queryCfetsQuoteIdList(
                RequestUtil.getPageParam());
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getDayOfferTableList")
    public List<BondQuoteInfoDto> getDayOfferTableList() {
        return cfetsBondDealQuotedPrice001Service.getDayOfferTableList(RequestUtil.getPageParam());
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getOrderOfferTableList")
    public List<BondQuoteInfoDto> getOrderOfferTableList() {
        return cfetsBondDealQuotedPrice001Service.getOrderOfferTableList(
                RequestUtil.getPageParam());
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getMarketOfferTableList")
    public List<BondQuoteInfoDto> getMarketOfferTableList() {
        return cfetsBondDealQuotedPrice001Service.getMarketOfferTableList(
                RequestUtil.getPageParam());
    }

    /**
     * 现券报价查询-按指定条件分页查询<br />
     * 根据传入的不为空的变量进行查询，以分页形式返回查询结果，需支持数据权限
     *
     * @return Page<BondQuoteInfoDto>
     */
    @ResponseBody
    @RequestMapping("/getCstpQryTableList")
    public List<CstpSelectDto> getCstpQryTableList() {
        return cfetsBondDealQuotedPrice001Service.getCstpQryTableList(
                RequestUtil.getPageParam());
    }

    /**
     * 根据查询符合条件的质押券管理列表
     *
     * @return 质押券管理数据列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/gencjcxTable")
    public List<CstpSelectDto> gencjcxTableParam() {
        return cfetsBondDealQuotedPrice001Service.gencjcxTable(
                RequestUtil.getPageParam());
    }


    /**
     * check操作是否合规
     *
     * @param bondQuoteInfoDto 入参对象
     * @return 判断结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/checkOperationAllow")
    public AjaxResult checkOperationAllow(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        return cfetsQuotedCommonService.checkOperationAllow(bondQuoteInfoDto);
    }

    /**
     * check操作是否合规
     *
     * @param bondQuoteInfoDto 入参对象
     * @return 判断结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/checkOperationAllowList")
    public AjaxResult checkOperationAllowList(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        List<BondQuoteInfoDto> bondQuoteInfoDtoList = bondQuoteInfoDto.getBondQuoteInfoDtoList();
        AjaxResult aj = null;
        for(BondQuoteInfoDto bondQuote:bondQuoteInfoDtoList){
            aj =cfetsQuotedCommonService.checkOperationAllow(bondQuote);
            if(!StringUtil.isEmptyList(aj.getErrorList())){
                return aj;
            }
        }
        return aj;
    }

    /**
     * 撤回
     *
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doReCall")
    public AjaxResult doReCall(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        AjaxResult ar = null;
        if (cfetsBondDealQuotedPrice001Service.doReCall(bondQuoteInfoDto)) {
            ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
        } ;

        return ar;
    }

    /**
     * 撤回
     *
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doReCallList")
    public AjaxResult doReCallList(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        List<BondQuoteInfoDto> bondQuoteInfoDtoList = bondQuoteInfoDto.getBondQuoteInfoDtoList();
        AjaxResult ar = null;
        for(BondQuoteInfoDto bondQuote:bondQuoteInfoDtoList){
            if (cfetsBondDealQuotedPrice001Service.doReCall(bondQuote)) {
                ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
            } ;
        }
        return ar;
    }

    /**
     * 回退
     *
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doBack")
    public AjaxResult doBack(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        AjaxResult ar = null;
        if (cfetsBondDealQuotedPrice001Service.doBack(bondQuoteInfoDto)) {
            ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
        } ;

        return ar;
    }

    /**
     * 删除
     *
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doDel")
    public AjaxResult doDel(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        AjaxResult ar = null;
        if (cfetsBondDealQuotedPrice001Service.doDel(bondQuoteInfoDto)) {
            ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
        } ;

        return ar;
    }

    /**
     * 刷新
     *
     * @param bondQuoteInfoDto 选择记录
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("timeOutQuoteRefreshBond")
    public AjaxResult timeOutQuoteRefreshBond(@RequestBody BondQuoteInfoDto bondQuoteInfoDto) {
        cfetsBondDealQuotedPrice001Service.timeOutQuoteRefreshBond(bondQuoteInfoDto.getQuoteId());
        return new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
    }


    /**
     * 报价导入时的check
     *
     * @param fileName String
     * @return List<BondQuoteImportPreviewDto>
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("importCheck")
    public List<BondQuoteImportPreviewDto> importCheck(String fileName) throws Exception {
        List<BondQuoteImportPreviewDto> checkList = cfetsBondDealQuotedPrice001Service.importCheck(fileName);
        List<BondQuoteImportPreviewDto> retList = new ArrayList<BondQuoteImportPreviewDto>();
        for(int i=0;i<checkList.size();i++) {
            BondQuoteImportPreviewDto bondQuoteImportPreviewDto = checkList.get(i);
            String counterId = bondQuoteImportPreviewDto.getCounterId();
            String counterName = bondQuoteImportPreviewDto.getCounterName();
            String counterTraderName = bondQuoteImportPreviewDto.getCounterTraderName();
            if(StringUtils.isEmpty(counterTraderName)) {
                counterTraderName = "";
            }
            if(BussinessDictCodeList.交易类型_请求报价.getValue().equals(bondQuoteImportPreviewDto.getQuoteType())&&StringUtils.isNotEmpty(bondQuoteImportPreviewDto.getEntrustId())) {
                for(;i+1<checkList.size();) {
                    BondQuoteImportPreviewDto temp = checkList.get(i+1);
                    if(StringUtils.isEmpty(temp.getEntrustId())&&StringUtils.isNotEmpty(temp.getCounterId())) {
                        counterId = counterId + "," + temp.getCounterId();
                        counterName = counterName + "," + temp.getCounterName();
                        String name = "";
                        if(StringUtils.isNotEmpty(temp.getCounterTraderName())) {
                            name = temp.getCounterTraderName();
                        }
                        counterTraderName = counterTraderName + "," + name;
                        i++;
                    }else {
                        break;
                    }
                }
                bondQuoteImportPreviewDto.setCounterId(counterId);
                bondQuoteImportPreviewDto.setCounterName(counterName);
                if(",".equals(counterTraderName)) {
                    bondQuoteImportPreviewDto.setCounterTraderName("");
                }else {
                    bondQuoteImportPreviewDto.setCounterTraderName(counterTraderName);
                }

            }
            retList.add(bondQuoteImportPreviewDto);
        }
        return retList;
    }

    /**
     * 报价导入
     *
     * @param dataRows List<BondQuoteImportPreviewDto>
     * @return AjaxResult 处理结果
     * @throws Exception 异常
     */
    @SuppressWarnings("null")
    @ResponseBody
    @RequestMapping("importFileCheck")
    public AjaxResult importFileCheck(@RequestBody List<BondQuoteImportPreviewDto> dataRows, BindingResult result)
            throws Exception {
        List<ValidMessage> messages = new ArrayList<>();
        AjaxResult ajaxResult = new AjaxResult();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dataRows.size(); i++ ) {
            if(StringUtil.equals(dataRows.get(i).getQuoteType(), BussinessDictCodeList.交易类型_请求报价.getValue())){
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto, dataRows.get(i));
                cfetsBondDealRequestQuoteDto.setAvaNum(dataRows.get(i).getEntrustAmt());
                cfetsBondDealRequestQuoteDto.setEntrustAmt(dataRows.get(i).getEntrustAmt().multiply(new BigDecimal(10000)));
                cfetsBondDealRequestQuoteDto.setOperatorType("submit");
                List<ValidMessage> resultList = cfetsRequestValidator.valid(cfetsBondDealRequestQuoteDto);
                if (!StringUtil.isEmptyList(resultList)) {
                    StringBuffer message = new StringBuffer();
                    message.append(getMessage("CFETS_COMMON_001", String.valueOf(i + 1)));
                    for (ValidMessage validMessage : resultList) {
                        message.append(GlobalConstant.HTML_NEWLINE);
                        message.append(validMessage.getMessage());
                    }
                    messages.add(new ValidMessage(message.toString()));
                }
                AjaxResult ajaxResult1 = cfetsBond003Controller.check(cfetsBondDealRequestQuoteDto, result);
                if(StringUtil.isNotEmpty(ajaxResult1.getData())){
                    if(ajaxResult1.getData().toString().contains("<")){
                        sb.append(getMessage("CFETS_COMMON_001", String.valueOf(i + 1)));
                        sb.append(GlobalConstant.HTML_NEWLINE);
                        sb.append(ajaxResult1.getData().toString());
                    }else if(i == dataRows.size()-1){
                        sb.append(ajaxResult1.getData().toString());
                    }
                }

            }else if(StringUtil.equals(dataRows.get(i).getQuoteType(), BussinessDictCodeList.交易类型_对话报价.getValue())){
                CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, dataRows.get(i));
                cfetsBondDealDialogueQuoteDto.setAvaNum(dataRows.get(i).getEntrustAmt());
                cfetsBondDealDialogueQuoteDto.setEntrustAmt(dataRows.get(i).getEntrustAmt().multiply(new BigDecimal(10000)));
                cfetsBondDealDialogueQuoteDto.setQuotaPageModel("new");
                cfetsBondDealDialogueQuoteDto.setOperatorType("submit");
                List<ValidMessage> resultList = cfetsDialogueValidator.valid(cfetsBondDealDialogueQuoteDto);
                if (!StringUtil.isEmptyList(resultList)) {
                    StringBuffer message = new StringBuffer();
                    message.append(getMessage("CFETS_COMMON_001", String.valueOf(i + 1)));
                    for (ValidMessage validMessage : resultList) {
                        message.append(GlobalConstant.HTML_NEWLINE);
                        message.append(validMessage.getMessage());
                    }
                    messages.add(new ValidMessage(message.toString()));
                }
                AjaxResult ajaxResult1 = cfetsBond002Controller.check(cfetsBondDealDialogueQuoteDto, result);
                if(StringUtil.isNotEmpty(ajaxResult1.getData())){
                    if(ajaxResult1.getData().toString().contains("<")){
                        sb.append(getMessage("CFETS_COMMON_001", String.valueOf(i + 1)));
                        sb.append(GlobalConstant.HTML_NEWLINE);
                        sb.append(ajaxResult1.getData().toString());
                    }else if(i == dataRows.size()-1){
                        sb.append(ajaxResult1.getData().toString());
                    }
                }
            }
        }
        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        if (sb.length() > 0){
            ajaxResult.setData(sb);
        }else{
            String msg = getMessage("SysCommon.submit");
            ajaxResult.setData(getMessage("COMMON_C_0001", msg));
        }
        return ajaxResult;
    }



    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody BondQuoteImportPreviewDto allDto, BindingResult br, HttpServletRequest requestq)
            throws Exception {
        String msg = null;
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        List<BondQuoteImportPreviewDto> dataRows = allDto.getBondQuoteImportPreviewDtoList();
        List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
        List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList = new ArrayList<>();
        for(BondQuoteImportPreviewDto bondQuoteImportPreviewDto : dataRows){
            if(StringUtil.equals(bondQuoteImportPreviewDto.getQuoteType(), BussinessDictCodeList.交易类型_请求报价.getValue())){
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto, bondQuoteImportPreviewDto);
                cfetsBondDealRequestQuoteDto.setAvaNum(bondQuoteImportPreviewDto.getEntrustAmt());
                cfetsBondDealRequestQuoteDto.setEntrustAmt(bondQuoteImportPreviewDto.getEntrustAmt().multiply(new BigDecimal(10000)));
                cfetsBondDealRequestQuoteDto.setQuotaPageModel("new");
                cfetsBondDealRequestQuoteDto.setOperatorType("submit");
                cfetsBondDealRequestQuoteDto.setPageId("CfetsBondDealQuotedPrice003");
                cfetsBondDealRequestQuoteDto.setElementId("CfetsBondDealQuotedPrice003_btn_submit");
                cfetsBondDealRequestQuoteDto.setSettleDate(iPlatformDate.getSysDate());
                if (!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getQuoteId())) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    codeParameter.setSeqNo("IBU_002");
                    String code = bussGetBussinessIdService != null
                            ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
                    cfetsBondDealRequestQuoteDto.setQuoteId(code);
                }
                //请求报价多个对手方拆分
                String counterId = cfetsBondDealRequestQuoteDto.getCounterId();
                String[] counterIdArray = counterId.split(",");
                if(counterIdArray.length>=2) {
                    cfetsBondDealRequestQuoteDto.getCounterIdList().clear();
                    for(String id : counterIdArray) {
                        cfetsBondDealRequestQuoteDto.getCounterIdList().add(id);
                    }
                }

                if (StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getEntrustId())) {
                    CfetsBondDealRequestQuoteDto tempDto = cfetsBondDealQuotedPrice003Service.queryEntrustInfoByEntrustId(cfetsBondDealRequestQuoteDto.getEntrustId());
                    cfetsBondDealRequestQuoteDto.setEffDate(tempDto.getEffDate());
                }

                cfetsBondDealRequestQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券请求报价.getValue());
                cfetsBondDealRequestQuoteDto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                cfetsBondDealRequestQuoteDto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                cfetsBondDealRequestQuoteDto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                cfetsBondDealRequestQuoteDto.setCreateTime(new Date());// 创建时间
                cfetsBondDealRequestQuoteDtoList.add(cfetsBondDealRequestQuoteDto);

                //cfetsBondDealQuotedPrice003Service.insertWflBondRequestQuote(cfetsBondDealRequestQuoteDto);
            }else if(StringUtil.equals(bondQuoteImportPreviewDto.getQuoteType(), BussinessDictCodeList.交易类型_对话报价.getValue())){
                CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, bondQuoteImportPreviewDto);
                cfetsBondDealDialogueQuoteDto.setAvaNum(bondQuoteImportPreviewDto.getEntrustAmt());
                cfetsBondDealDialogueQuoteDto.setEntrustAmt(bondQuoteImportPreviewDto.getEntrustAmt().multiply(new BigDecimal(10000)));
                cfetsBondDealDialogueQuoteDto.setQuotaPageModel("new");
                cfetsBondDealDialogueQuoteDto.setOperatorType("submit");
                cfetsBondDealDialogueQuoteDto.setPageId("CfetsBondDealQuotedPrice002");
                cfetsBondDealDialogueQuoteDto.setElementId("CfetsBondDealQuotedPrice002_btn_submit");
                cfetsBondDealDialogueQuoteDto.setSettleDate(iPlatformDate.getSysDate());
                if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    codeParameter.setSeqNo("IBU_001");
                    String code = bussGetBussinessIdService != null
                            ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
                    cfetsBondDealDialogueQuoteDto.setQuoteId(code);
                }
                cfetsBondDealDialogueQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券对话报价.getValue());

                cfetsBondDealDialogueQuoteDto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
                cfetsBondDealDialogueQuoteDto.setCreateUser(LoginUserUtil.getUserName());// 用户名
                cfetsBondDealDialogueQuoteDto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
                cfetsBondDealDialogueQuoteDto.setCreateTime(new Date());// 创建时间
                cfetsBondDealDialogueQuoteDtoList.add(cfetsBondDealDialogueQuoteDto);
            }
        }
        if(!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDtoList)){
            ProcessParameter<CfetsBondDealRequestQuoteDto> processParameter = new ProcessParameter<CfetsBondDealRequestQuoteDto>();
            WorkFlowParameter<CfetsBondDealRequestQuoteDto> params =  new WorkFlowParameter<CfetsBondDealRequestQuoteDto>();
            params.setPageId(cfetsBondDealRequestQuoteDtoList.get(0).getPageId());
            params.setElementId(cfetsBondDealRequestQuoteDtoList.get(0).getElementId());
            params.setBusinessCode(CfetsBondDealRequestQuoteDto::getQuoteId);
            params.setBusinessType(DictCodeListExt3.共通业务类型_银行间上行.getValue());
            params.setComment(entity -> getMessage("请求报价编号") + "(" + entity.getQuoteId() + ")-"
                    + "业务类型" + "(" + entity.getQuoteTypeStr() + ")-" + "平台日" + "("
                    + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            msg = cfetsBondDealQuotedPrice003Service.start(processParameter, cfetsBondDealRequestQuoteDtoList.toArray(new CfetsBondDealRequestQuoteDto[cfetsBondDealRequestQuoteDtoList.size()]));
        }

        if(!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDtoList)){
            ProcessParameter<CfetsBondDealDialogueQuoteDto> processParameter = new ProcessParameter<CfetsBondDealDialogueQuoteDto>();
            WorkFlowParameter<CfetsBondDealDialogueQuoteDto> params =  new WorkFlowParameter<CfetsBondDealDialogueQuoteDto>();
            params.setPageId(cfetsBondDealDialogueQuoteDtoList.get(0).getPageId());
            params.setElementId(cfetsBondDealDialogueQuoteDtoList.get(0).getElementId());
            params.setBusinessType(DictCodeListExt3.共通业务类型_银行间上行.getValue());
            params.setBusinessCode(CfetsBondDealDialogueQuoteDto::getQuoteId);
            params.setComment(entity -> getMessage("对话报价编号") + "(" + entity.getQuoteId() + ")-"
                    + "业务类型" + "(" + entity.getQuoteTypeStr() + ")-" + "平台日" + "("
                    + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            try {
                msg = cfetsBondDealQuotedPrice002Service.start(processParameter, cfetsBondDealDialogueQuoteDtoList.toArray(new CfetsBondDealDialogueQuoteDto[cfetsBondDealDialogueQuoteDtoList.size()]));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return new AjaxResult(msg);
    }

    /**
     * 文件上传公共画面
     *
     * @param model Model
     * @return 画面路径
     */
    @RequestMapping("framework/fileUpload/init")
    private String uploadInit(Model model) {
        // 画面ID
        String pageId = StringUtil.trim(RequestUtil.getParam(GlobalConstant.PAGE_ID));
        // 上传文件控件的类型
        String type = StringUtil.trim(
                StringUtil.defaultString(RequestUtil.getParam("type"), "drag"));
        // 支持文件类型
        String exts = getExtFile();
        String legend = StringUtil.EMPTY;
        boolean hideTpl = Boolean.valueOf(StringUtil.trim(
                StringUtil.defaultString(RequestUtil.getParam("hideTpl"), Boolean.FALSE.toString())));
        switch (type) {
            case "singleImage":
                legend = "普通图片上传";
                exts = "jpg|jpeg|png|gif|bmp";
                break;
            case "mutiImage":
                legend = "上传多张图片";
                exts = "jpg|jpeg|png|gif|bmp";
                break;
            case "allowFile":
                legend = "只允许上传指定类型的文件";
                break;
            case "allowSize":
                legend = "设定文件大小限制";
                break;
            case "drag":
                legend = "拖拽上传";
                break;
            case "list":
                legend = "多文件列表";
                break;
            default:
                break;
        }
        model.addAttribute("type", type);
        model.addAttribute("hideTpl", hideTpl);
        model.addAttribute("legend", legend);
        model.addAttribute("exts", exts);
        model.addAttribute("currentPageId", pageId);
        model.addAttribute("sendStatus_fail", BussinessDictCodeList.银行间上行发送状态_失败.getCodeName());
        model.addAttribute("sendStatus_unsend", BussinessDictCodeList.银行间上行发送状态_待发送.getCodeName());
        model.addAttribute("sendStatus_sent", BussinessDictCodeList.银行间上行发送状态_已发送.getCodeName());

        return "invest/bankuplink/bond/fileupload";
    }

    /**
     * 获取支持文件的类型
     *
     * @return 支持文件类型
     */
    private String getExtFile() {
        return StringUtil.trim(StringUtil.defaultString(RequestUtil.getParam("exts"), "xls|xlsx"));
    }

    /**
     * 模板文件下载
     *
     * @return 将系统所有模板文件进行打包下载
     * @throws IOException 异常
     */
    @RequestMapping("framework/fileUpload/downloadTemplate")
    public ResponseEntity<byte[]> download() throws IOException{
        // 支持文件类型
        String exts = getExtFile();
        String pageId = RequestUtil.getParam(GlobalConstant.PAGE_ID);
        TemplateName tempName = getTemplateFileNameByPageId(pageId, exts);
        // 如果当前画面的模板存在，则下载对应模板
        if (null != tempName) {
            File tplFile = new File(FileNameUtil.getTplFile(tempName.getFileName()));
            String targetFileName = tempName.getOutFileName() + GlobalConstant.POINT
                    + FileNameUtil.getFileExtName(tempName.getFileName());
            InputStream in = null;
            if (!tplFile.exists()) {
                in = FileUploadController.class.getResourceAsStream("/tpl/" + tplFile.getName());
            }
            else {
                in = new FileInputStream(tplFile);
            }
            return ExportUtil.export(targetFileName, in);
        }

        return null;
    }

    /**
     * 根据画面ID获取对应的模板名
     *
     * @param pageId 画面ID
     * @param exts 模板后缀
     * @return 模板文件和文件名
     * @throws IOException 异常
     */
    private TemplateName getTemplateFileNameByPageId(String pageId, String exts)
            throws IOException {
        if (null != pageId) {
            List<String> extList = StringUtil.splitToList(GlobalConstant.VERTICAL_LINE, exts);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resource = resolver.getResources("classpath*:page_tpl.properties");
            for (Resource res : resource) {
                Properties properties = PropertiesLoaderUtils.loadProperties(res);
                for (Object key : properties.keySet()) {
                    String keyStr = key.toString();
                    String template = properties.getProperty(keyStr);
                    String[] templateNames = StringUtil.split(StringUtil.trim(template),
                            GlobalConstant.COMMA);
                    for (String tpl : templateNames) {
                        String[] fileNames = StringUtil.split(tpl, GlobalConstant.VERTICAL_LINE);
                        if (StringUtil.equals(keyStr, pageId) && fileNames.length == 2) {
                            if (StringUtil.isEmpty(exts)
                                    || extList.contains(FileNameUtil.getFileExtName(fileNames[1]))) {
                                return new TemplateName(pageId, fileNames[0], fileNames[1]);
                            }
                        }
                    }
                }
            }
        }
        return new TemplateName(pageId, SpringContextUtil.getTextValue(pageId),
                pageId + GlobalConstant.FILE_POSTFIX_XLSX);
    }

    class TemplateName {

        /**
         * 输出文件名称
         */
        private String outFileName;

        /**
         * 模板名对应的画面ID
         */
        private String pageId;

        /**
         * 模板文件名
         */
        private String fileName;

        /**
         * @param pageId 画面ID
         * @param outFileName 导出文件名
         * @param fileName 模板文件名
         */
        TemplateName(String pageId, String outFileName, String fileName) {
            this.outFileName = outFileName;
            this.pageId = pageId;
            this.fileName = fileName;
        }

        /**
         * 获取 outFileName
         *
         * @return outFileName.
         */
        public String getOutFileName() {
            return outFileName;
        }

        /**
         * 设置 outFileName
         *
         * @param outFileName outFileName
         */
        public void setOutFileName(String outFileName) {
            this.outFileName = outFileName;
        }

        /**
         * 获取 pageId
         *
         * @return pageId.
         */
        public String getPageId() {
            return pageId;
        }

        /**
         * 设置 pageId
         *
         * @param pageId pageId
         */
        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        /**
         * 获取 fileName
         *
         * @return fileName.
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * 设置 fileName
         *
         * @param fileName fileName
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }


    @RequestMapping("exportPdf")
    public ResponseEntity<byte[]> exportPdf(){
        return cfetsBondDealQuotedPriceSingleService.exportPdf(this.getPageId(),RequestUtil.getPageParam());
    }

}
