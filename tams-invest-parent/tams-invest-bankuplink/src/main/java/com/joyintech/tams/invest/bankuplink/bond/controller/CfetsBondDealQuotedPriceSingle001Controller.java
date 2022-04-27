package com.joyintech.tams.invest.bankuplink.bond.controller;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.service.CfetsBondDealQuotedPriceSingle001Service;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@PageId("CfetsBondDealQuotedPriceSingle001")
@RequestMapping("/invest/bankuplink/bond/CfetsBondDealQuotedPriceSingle001")
public class CfetsBondDealQuotedPriceSingle001Controller extends BaseController {

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsBondDealQuotedPriceSingle001Service cfetsBondDealQuotedPriceSingle001Service;

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

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度,
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种, DictCode.业务模式);

        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_2", BussinessDictCodeList.清算速度_T加2.getValue());
        model.addAttribute("yesNoFlag_yes", BussinessDictCodeList.是否_是.getValue());
        model.addAttribute("assetTypeOne_abs", BussinessDictCodeList.平安资产一级分类_资产支持证券.getValue());
        model.addAttribute("assetTypeTwo_abs", BussinessDictCodeList.行内资产三级分类_资产支持证券.getValue());
        model.addAttribute("calcTypeUnitCprice", BussinessDictCodeList.计算选择方式_净价.getValue());
        model.addAttribute("calcTypeYield", BussinessDictCodeList.计算选择方式_到期收益率.getValue());
        model.addAttribute("calcTypeExecYield", BussinessDictCodeList.计算选择方式_行权收益率.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 本方交易员是否可改
        model.addAttribute("ouTraderIdCanEditFlag", CacheUtil.getSysParam("ibuFlag_ouTraderIdCanEdit"));
        // 无委托指令是否允许回复
        model.addAttribute("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));

        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));

        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        boolean isInit = false;
        model.addAttribute("TodoList", false);
        // 目前流程中场合只适用撤销逻辑,因为新建没走流程拿不到数据留痕表的记录，只能根据报价代码反查  20201225开始新增走流程了
        if (StringUtil.equals(this.getParam("workflowStatus"), BussinessDictCodeList.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPriceSingle001Service.getBussinessEntityByWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute(GlobalConstant.WORKFLOW_ID,getParam(GlobalConstant.WORKFLOW_ID));
            //cfetsBondDealDialogueQuoteDto.setAvaNum(cfetsBondDealDialogueQuoteDto.getAvaNum().multiply(new BigDecimal(10000)));
            /*if (cfetsBondDealDialogueQuoteDto.getQuoteId() != null){
                cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPriceSingle001Service.getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId());*/
            // 此处判断做市方是为了联动显示回复量
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_做市方.getValue())){
                cfetsBondDealDialogueQuoteDto.setQuotaPageModel("marketResponse");
            }
            if(StringUtils.isEmpty(cfetsBondDealDialogueQuoteDto.getPriceModel())){
                CfetsBondDealDialogueQuoteDto entrustInfo = cfetsBondDealQuotedPriceSingle001Service.queryEntrustInfoById(
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
            model.addAttribute("TodoList", true);
            /*}*/
        }
        else if (StringUtil.isNotEmpty(this.getParam("entrustId"))) {
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPriceSingle001Service.queryEntrustInfoByEntrustId(
                    this.getParam("entrustId"));
        }
        else if (StringUtil.isNotEmpty(this.getParam("quoteId"))) {
            cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPriceSingle001Service.getBondDialogueQuote(
                    this.getParam("quoteId"),this.getParam("statusValid"));

            // add by etliu 20210313 银行间上下行多组合 start
            if (cfetsBondDealDialogueQuoteDto != null && cfetsBondDealDialogueQuoteDto.getAvaNum() != null) {
                // 券面总额是万元单位
                cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.divide(
                        cfetsBondDealDialogueQuoteDto.getAvaNum(), BigDecimal.valueOf(10000), 2));
            }
            // add by etliu 20210313 银行间上下行多组合 end

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
                    CfetsBondDealDialogueQuoteDto entrustInfo = cfetsBondDealQuotedPriceSingle001Service.queryEntrustInfoById(
                            cfetsBondDealDialogueQuoteDto.getEntrustId());
                    if (entrustInfo != null && entrustInfo.getEntrustNum() != null) {
                        cfetsBondDealDialogueQuoteDto.setAvaNum(entrustInfo.getAvaNum());
                    }
                }
            }
        }
        // 无委托模式
        else {
            // 设置默认的银行间交易员为登录用户
            isInit = true;
            cfetsBondDealDialogueQuoteDto.setMySideTrader(cfetsQuotedCommonService.queryLoginTraderId(RequestUtil.getPageParam()));
        }

        model.addAttribute("businessId", StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())?cfetsBondDealDialogueQuoteDto.getQuoteId(): IDGenerator.generate());
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

        if(!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuotaPageModel())){
            cfetsBondDealDialogueQuoteDto.setQuotaPageModel("new");
        }

        if (!isInit&&!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
            cfetsBondDealDialogueQuoteDto.setCalculateField(DictCodeListExt3.债券计算模式_单位净价.getValue());
        }

        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("assetTypeOne",cfetsBondDealDialogueQuoteDto.getAssetTypeOne());

        model.addAttribute("PRICE_MODEL",cfetsBondDealDialogueQuoteDto.getPriceModel());
        model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute(GlobalConstant.ENTITY, cfetsBondDealDialogueQuoteDto);
        model.addAttribute("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        // 是否主界面打开  用于隐藏按钮
        String isMain = StringUtil.equals(this.getParam("isMain"),DictCodeList.是否_否.getValue())?DictCodeList.是否_否.getValue():DictCodeList.是否_是.getValue();
        model.addAttribute("isMain", isMain);
        return super.init(model);
    }

    /**
     * 根据委托代码查询委托基本信息
     *
     * @param entrustId 委托代码
     * @return 委托基本信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("queryEntrustInfoByEntrustId")
    public AjaxResult queryEntrustInfoByEntrustId(String entrustId) {
        AjaxResult ar = new AjaxResult();
        ar.setData(cfetsBondDealQuotedPriceSingle001Service.queryEntrustInfoByEntrustId(entrustId));
        return ar;
    }

    /**
     * 保存验证
     *
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     * @param 处理结果
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto,
                            BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }

        AjaxResult ar = new AjaxResult();

        StringBuffer sb = new StringBuffer();
        // 委托为空的场合只有新建模式且系统参数为软校验
        if (StringUtil.isEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())){
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")
                    && StringUtil.equals(CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"), "02")){
                sb.append(this.getMessage("CFETS_COMMON_E_001"));
                sb.append(GlobalConstant.HTML_NEWLINE);
            }
            else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")
                    && StringUtil.equals(CacheUtil.getSysParam("ibuFlag_answerQuoteWithOutEntrust"), "02")) {
                sb.append(this.getMessage("CFETS_COMMON_E_009"));
                sb.append(GlobalConstant.HTML_NEWLINE);
            }
        }

        // 委托剩余额度
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")){
            cfetsBondDealQuotedPriceSingle001Service.checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto, null, sb, false);
        }

        // 偏离度
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_dealDeviation"), "02")
                && StringUtil.isNotEmpty(CacheUtil.getSysParam("ibuVar_dealDeviationVal"))){
            cfetsBondDealQuotedPriceSingle001Service.checkDealDeviation(cfetsBondDealDialogueQuoteDto, null, sb, false);
        }

        // 限价
        if (StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
            cfetsBondDealQuotedPriceSingle001Service.checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto, null, sb, false);
        }

        if (sb.length() > 0){
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
        }

        return ar;
    }

    /**
     * 委托报价验证
     *
     * @param param 参数
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkEntrust")
    public AjaxResult checkEntrust(@RequestBody Map<String, Object> param) {
        Boolean flag = SecurityUtil.hasAuthenticated();
        if(!flag) {
            HttpServletResponse rsp = this.getResponse();
            rsp.setStatus(401);
            return null;
        }
        return cfetsBondDealQuotedPriceSingle001Service.checkEntrust(param);
    }

    /**
     * 保存提交
     *
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     * @return 处理结果
     * @throws WorkFlowException
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("saveData")
    public AjaxResult saveData(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {

        cfetsBondDealQuotedPriceSingle001Service.save(cfetsBondDealDialogueQuoteDto);

        return new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
    }

    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setSeqNo("IBU_001");
            String code = bussGetBussinessIdService != null
                    ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
            cfetsBondDealDialogueQuoteDto.setQuoteId(code);
        }
        List<String> fileNos = new ArrayList<>();
        if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getBusinessId())){
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(cfetsBondDealDialogueQuoteDto.getBusinessId());
            for(UploadFile file : files){
                fileNos.add(file.getFileNo());
            }
            // 文件与指令ID绑定
            fileAttachmentProcess.updateBussinessNo(fileNos,cfetsBondDealDialogueQuoteDto.getQuoteId());
        }
        ProcessParameter<CfetsBondDealDialogueQuoteDto> processParameter = new ProcessParameter<CfetsBondDealDialogueQuoteDto>();
        WorkFlowParameter<CfetsBondDealDialogueQuoteDto> params = cfetsBondDealQuotedPriceSingle001Service.getWorkFlowParameter(cfetsBondDealDialogueQuoteDto);
        params.setPageId(cfetsBondDealDialogueQuoteDto.getPageId());
        params.setElementId(cfetsBondDealDialogueQuoteDto.getElementId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_银行间上行.getValue());
        params.setBusinessCode(cfetsBondDealDialogueQuoteDto.getQuoteId());
        cfetsBondDealDialogueQuoteDto.setQuoteType(DictCodeListExt3.报价系统区分类型_现券对话报价.getValue());
        params.setComment(entity -> getMessage("对话报价编号") + "(" + entity.getQuoteId() + ")-"
                + "业务类型" + "(现券买卖" + entity.getQuoteTypeStr() + ")-" + "平台日" + "("
                + DateUtil.format(iPlatformDate.getSysDate()) + ")");
        processParameter.setWorkFlowParameter(params);

        cfetsBondDealDialogueQuoteDto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
        cfetsBondDealDialogueQuoteDto.setCreateUser(LoginUserUtil.getUserName());// 用户名
        cfetsBondDealDialogueQuoteDto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
        cfetsBondDealDialogueQuoteDto.setCreateTime(new Date());// 创建时间
        String msg = cfetsBondDealQuotedPriceSingle001Service.start(processParameter, cfetsBondDealDialogueQuoteDto);
        //cfetsBondDealQuotedPriceSingle001Service.insertWflBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
        return new AjaxResult(msg);
    }

    /**
     * 调用算法包计算金额要素
     *
     * @param cfetsBondDealDialogueQuoteDto 入参
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("calcAmt")
    public AjaxResult calcAmt(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        return cfetsBondDealQuotedPriceSingle001Service.calcAmt(cfetsBondDealDialogueQuoteDto);
    }

    /**
     * 现券买卖历史报价 撤销
     *
     * @param dto
     * @param br
     * @return
     * @throws WorkFlowException
     */
    @ResponseBody
    @RequestMapping("bondRevoke")
    public AjaxResult bondRevoke(@RequestBody CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) throws WorkFlowException {
        ProcessParameter<CfetsBondDealDialogueQuoteDto> processParameter = new ProcessParameter<CfetsBondDealDialogueQuoteDto>();
        WorkFlowParameter<CfetsBondDealDialogueQuoteDto> workFlowParameter = new WorkFlowParameter<CfetsBondDealDialogueQuoteDto>();
        workFlowParameter.setPageId("CfetsBondDealQuotedPrice002");
        workFlowParameter.setElementId("CfetsBondDealQuotedPrice002_btn_bondRevoke");
        workFlowParameter.setBusinessCode(cfetsBondDealDialogueQuoteDto.getQuoteId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_银行间上行.getValue());
        workFlowParameter.setComment(entity -> getMessage("CFETSHIS_QUOTEID") + "(" + entity.getQuoteId() + ")");
        processParameter.setWorkFlowParameter(workFlowParameter);

        String msg = cfetsBondDealQuotedPriceSingle001Service.start(processParameter, cfetsBondDealDialogueQuoteDto);

        return new AjaxResult(msg);
    }
}
