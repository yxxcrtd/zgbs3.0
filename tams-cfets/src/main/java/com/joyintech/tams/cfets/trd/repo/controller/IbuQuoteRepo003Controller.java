package com.joyintech.tams.cfets.trd.repo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.repo.dto.QuotedPriceRequest;
import com.joyintech.tams.cfets.trd.repo.dto.RepoEntrustPledgeDto;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoDealQuotedPrice002Service;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 现券买卖(对话)
 */
@Controller
@PageId("CfetsQuoteRepo003")
@RequestMapping("/cfets/bankuplink/repo/IbuQuoteRepo003")
@Api(tags = "CFETS质押式回购交易-对话接口")
public class IbuQuoteRepo003Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IbuQuoteRepo003Controller.class);

    @Autowired
    private CfetsRepoDealQuotedPrice002Service cfetsRepoDealQuotedPrice002Service;

    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private BaseGetBussinessId bussGetBussinessIdService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.清算速度, DictCode.回购方式, DictCode.价格模式,
                BussinessDictCode.结算方式, BussinessDictCode.清算类型, BussinessDictCode.币种, DictCode.期限品种_回购,  DictCode.分配方式);
        model.addAttribute("ACCOUNT_SPEED_0", BussinessDictCodeList.清算速度_T加0.getValue());
        model.addAttribute("ACCOUNT_SPEED_1", BussinessDictCodeList.清算速度_T加1.getValue());
        model.addAttribute("ACCOUNT_SPEED_2", BussinessDictCodeList.清算速度_T加2.getValue());
        model.addAttribute("yesNoFlag_yes", BussinessDictCodeList.是否_是.getValue());
        CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
        if (StringUtil.equals(this.getParam("workflowStatus"), DictCodeListExt1.有效状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList()
                && StringUtil.isNotBlank(getParam(GlobalConstant.WORKFLOW_ID)))) {
            model.addAttribute(GlobalConstant.WORKFLOW_ID, getParam(GlobalConstant.WORKFLOW_ID));
            cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.getBussinessEntityByWorkflowId(
                    getParam(GlobalConstant.WORKFLOW_ID));
            if (cfetsRepoDealQuotedPrice001Dto.getQuoteId() != null) {
                // 画面模式
                cfetsRepoDealQuotedPrice001Dto.setDialogueRecord(cfetsRepoDealQuotedPrice002Service.selectDialogueRecord(cfetsRepoDealQuotedPrice001Dto).getDialogueRecord());
                if (StringUtil.isNotEmpty(this.getParam("quotaPageModel"))) {
                    cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel(this.getParam("quotaPageModel"));
                }
                model.addAttribute(GlobalConstant.ENTITY, cfetsRepoDealQuotedPrice001Dto);
            }
        }
        model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        model.addAttribute("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
        model.addAttribute("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
        model.addAttribute("quotaPageModel", "new");
        model.addAttribute("ptlAllocations", cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList());
        model.addAttribute("repoEntrustPledgeDtos", cfetsRepoDealQuotedPrice001Dto.getPledgeBondList());
        model.addAttribute("ps", cfetsRepoDealQuotedPrice001Dto.getPs());
        //分配方式
        model.addAttribute("distribution", cfetsRepoDealQuotedPrice001Dto.getDistribution());
        // 对手方是否可编辑
        String isEdit = DictCodeList.是否_是.getValue();
        List<String> entrustIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList())) {
            cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList().forEach(item -> entrustIdList.add(item.getEntrustId()));
        }
        if (cfetsRepoQuotedPrice004Service.getIsExistCounterId(entrustIdList) > 0) {
            isEdit = DictCodeList.是否_否.getValue();
        }
        model.addAttribute("isEdit", isEdit);
        return super.init(model);
    }

    /**
     * 拒绝
     *
     * @param param 条件
     * @return 处理结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "报价拒绝", notes = "报价拒绝")
    @ResponseBody
    @PostMapping("/reject")
    public Message<AjaxResult> reject(@RequestBody CfetsRepoDealQuoteCommonDto pageDto) {
        try {
            CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.page2BackDto(pageDto);
            cfetsRepoDealQuotedPrice002Service.reject(cfetsRepoDealQuotedPrice001Dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS")));
        } catch (Exception e) {
            logger.error("报价拒绝异常", e);
            return new Message<AjaxResult>().fail().setMessage("报价拒绝异常");
        }
    }

    @ApiOperation(value = "报价提交检查", notes = "报价提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto) {
        try {
            //check必填项
            AjaxResult ajaxResult = cfetsRepoDealQuotedPrice002Service.checkSave(cfetsRepoDealQuoteCommonDto);
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("报价提交检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("报价提交检查异常");
        }
    }

    /**
     * 代办页面提交校验
     *
     * @param cfetsRepoDealQuoteCommonDto
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/3/4 14:03
     */
    @PostMapping("/checkByHome")
    @ResponseBody
    public AjaxResult checkByHome(@RequestBody CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {

        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = cfetsRepoDealQuotedPrice002Service.back2PageDto(cfetsRepoDealQuotedPrice001Dto);
        //check必填项
        AjaxResult ajaxResult = cfetsRepoDealQuotedPrice002Service.checkSave(cfetsRepoDealQuoteCommonDto);
        return ajaxResult;
    }

    @ApiOperation(value = "报价成交提交检查", notes = "报价成交提交检查")
    @PostMapping("/checkRepoDeal")
    @ResponseBody
    public Message<AjaxResult> checkRepoDeal(@RequestBody CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto) {
        try {
            //check必填项
            AjaxResult ajaxResult = cfetsRepoDealQuotedPrice002Service.checkSave(cfetsRepoDealQuoteCommonDto);
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("报价成交提交检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("报价成交提交检查异常");
        }
    }

    @ApiOperation(value = "查看信息界面", notes = "根据交易编号查询报价查看信息界面")
    @PostMapping("/queryViewPage/{quoteId}")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> queryViewPage(@PathVariable("quoteId") String quoteId) {
        try {
            CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
            if (StringUtil.isNotEmpty(quoteId)) {
                cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.queryRepoInfoByQuoteId(quoteId, null, null);
                //多组合
                Map<String, Object> map = new HashMap<>();
                map.put("quoteId", quoteId);
                //组合分配列表初始化
                cfetsRepoDealQuotedPrice001Dto.setPtlAllocationList(cfetsRepoDealQuotedPrice002Service.selectPortfolioInit(map));
            }
            CfetsRepoDealQuoteCommonDto pageDto = cfetsRepoDealQuotedPrice002Service.back2PageDto(cfetsRepoDealQuotedPrice001Dto);
            return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(pageDto);
        } catch (Exception e) {
            logger.error("查看信息界面初始化异常", e);
            return new Message<CfetsRepoDealQuoteCommonDto>().fail().setMessage("查看信息界面初始化异常");
        }
    }

    @ApiOperation(value = "报价初始化", notes = "报价初始化")
    @ResponseBody
    @PostMapping("initPrice")
    public Message<CfetsRepoDealQuoteCommonDto> initPrice(@RequestBody QuotedPriceRequest request) {
        try {
            String operation = request.getOperatorType();
            String quoteId = request.getQuoteId();
            String quotaPageModel = request.getQuotaPageModel();

            CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
            if (StringUtil.isNotEmpty(quoteId)) {
                cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.queryRepoInfoByQuoteId(quoteId, operation, quotaPageModel);
                // 画面模式
                if (StringUtil.isNotEmpty(quotaPageModel)) {
                    cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel(quotaPageModel);
                }
                //多组合
                Map<String, Object> map = new HashMap<>();
                map.put("quoteId", quoteId);
                //组合分配列表初始化
                cfetsRepoDealQuotedPrice001Dto.setPtlAllocationList(cfetsRepoDealQuotedPrice002Service.selectPortfolioInit(map));
            }
        /*else if (StringUtil.isNotEmpty(entrustId)) {
            //多组合
            Map<String,Object> map = new HashMap<>();
            List<String> entrustIdList = Arrays.asList(entrustId.split(","));
            map.put("entrustIdList", entrustIdList);
            cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.queryEntrustInfoByEntrustId(entrustIdList.get(0), null);
            //组合分配列表初始化
            cfetsRepoDealQuotedPrice001Dto.setPtlAllocationList(cfetsRepoDealQuotedPrice003Service.selectPortfolioInit(map));
            // 画面模式
            if (StringUtil.isNotEmpty(this.getParam("quotaPageModel"))) {
                cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel(this.getParam("quotaPageModel"));
            }
        }*/
            else {
                cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
                // 画面模式
                if (StringUtil.isNotEmpty(quotaPageModel)) {
                    cfetsRepoDealQuotedPrice001Dto.setQuotaPageModel(quotaPageModel);
                }
            }
//        cfetsRepoDealQuotedPrice002Service.单独处理页面质押券列表(cfetsRepoDealQuotedPrice001Dto);
            CfetsRepoDealQuoteCommonDto pageDto = cfetsRepoDealQuotedPrice002Service.back2PageDto(cfetsRepoDealQuotedPrice001Dto);
            if (StringUtil.equals(quotaPageModel, "dialogueResponse")
                    && StringUtil.isEmpty(cfetsRepoDealQuotedPrice001Dto.getDialogueRecord())
                    && StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())
                    && StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_待回复.getValue())
                    && StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType(), BussinessDictCodeList.银行间上行本方角色_回复方.getValue())) {
                List<RepoEntrustPledgeDto> list
                        = JsonUtils.json2List(JsonUtils.object2Json(pageDto.getRepoEntrustPledgeDtos()), RepoEntrustPledgeDto.class);
                pageDto.getRepoEntrustPledgeDtos().clear();
                pageDto.setCountPledgeBondList(list);
            }
            return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(pageDto);
        } catch (Exception e) {
            logger.error("报价初始化异常", e);
            return new Message<CfetsRepoDealQuoteCommonDto>().fail().setMessage("报价初始化异常");
        }
    }

    @ResponseBody
    @PostMapping("/save")
    @ApiOperation(value = "报价提交(保存)", notes = "报价提交(保存)")
    public Message<AjaxResult> save(@RequestBody CfetsRepoDealQuoteCommonDto pageDto, BindingResult br) throws WorkFlowException {
        try {
            if (br.hasErrors()) {
                return new Message<AjaxResult>().success().setData(new AjaxResult(br));
            }
            CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = cfetsRepoDealQuotedPrice002Service.page2BackDto(pageDto);
            cfetsRepoDealQuotedPrice001Dto.setPageId("CfetsQuoteRepo003");
            if (!StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteId())) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setSeqNo("IBU_003");
                String code = bussGetBussinessIdService != null
                        ? bussGetBussinessIdService.buildBussinessCode(codeParameter) : IDGenerator.generate();
                cfetsRepoDealQuotedPrice001Dto.setQuoteId(code);
            }
            ProcessParameter<CfetsRepoDealQuotedPrice001Dto> processParameter = new ProcessParameter<CfetsRepoDealQuotedPrice001Dto>();
            WorkFlowParameter<CfetsRepoDealQuotedPrice001Dto> params = cfetsRepoDealQuotedPrice002Service.getWorkFlowParameter(cfetsRepoDealQuotedPrice001Dto);
            params.setPageId(cfetsRepoDealQuotedPrice001Dto.getPageId());
            params.setElementId(cfetsRepoDealQuotedPrice001Dto.getElementId());
            params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务交易_质押式回购_对话.getValue());
            params.setBusinessCode(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
            params.setComment(entity -> getMessage("对话报价编号") + "(" + entity.getQuoteId() + ")-"
                    + "业务类型" + "(" + DictCodeListExt3.报价系统区分类型_质押式回购对话报价.getCodeName() + ")-" + "平台日" + "("
                    + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);

            cfetsRepoDealQuotedPrice001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
            cfetsRepoDealQuotedPrice001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
            cfetsRepoDealQuotedPrice001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
            cfetsRepoDealQuotedPrice001Dto.setCreateTime(new Date());// 创建时间
            String msg = cfetsRepoDealQuotedPrice002Service.start(processParameter, cfetsRepoDealQuotedPrice001Dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("报价提交异常", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

    @ResponseBody
    @PostMapping("/revert")
    @ApiOperation(value = "对话报价撤回", notes = "对话报价撤回")
    public Message<Boolean> revert(@RequestBody QuotedPriceRequest request) {
        try {
            Boolean flag = cfetsRepoDealQuotedPrice002Service.revert(request);
            return new Message<Boolean>().success().setData(flag);
        } catch (Exception e) {
            logger.error("撤回异常", e);
            return new Message<Boolean>().fail().setMessage("撤回异常");
        }
    }

    @ResponseBody
    @PostMapping("/restoreDayOff")
    @ApiOperation(value = "对话报价回退", notes = "对话报价回退")
    public Message<Boolean> restoreDayOff(@RequestBody QuotedPriceRequest request) {
        try {
            return new Message<Boolean>().success().setData(cfetsRepoDealQuotedPrice002Service.restoreDayOff(request));
        } catch (Exception e) {
            logger.error("回退异常", e);
            return new Message<Boolean>().fail().setMessage("回退异常");
        }
    }

    @ResponseBody
    @PostMapping("deleteDayOff")
    @ApiOperation(value = "对话报价删除", notes = "对话报价删除")
    public Message<Boolean> deleteDayOff(@RequestBody QuotedPriceRequest request) {
        try {
            return new Message<Boolean>().success().setData(cfetsRepoDealQuotedPrice002Service.deleteDayOff(request));
        } catch (Exception e) {
            logger.error("删除异常", e);
            return new Message<Boolean>().fail().setMessage("删除异常");
        }
    }

    @ResponseBody
    @PostMapping("/checkEditList")
    @ApiOperation(value = "对话报价校验", notes = "回退、撤回，回复、复制初始化校验")
    public Message<AjaxResult> checkEditList(@RequestBody QuotedPriceRequest request) {
        try {
            AjaxResult ar = ar = cfetsRepoDealQuotedPrice002Service.checkEditList(request);
            return new Message<AjaxResult>().success().setData(ar);
        } catch (Exception e) {
            logger.error("对话报价初始化验证异常", e);
            return new Message<AjaxResult>().fail().setMessage("对话报价初始化验证异常");
        }
    }

    @ResponseBody
    @PostMapping("/checkEdit")
    @ApiOperation(value = "对话报价校验", notes = "回退、撤回，回复、复制初始化校验")
    public Message<AjaxResult> checkEdit(@RequestBody QuotedPriceRequest request) {
        try {
            AjaxResult ar = ar = cfetsRepoDealQuotedPrice002Service.checkEdit(request);
            return new Message<AjaxResult>().success().setData(ar);
        } catch (Exception e) {
            logger.error("对话报价初始化验证异常", e);
            return new Message<AjaxResult>().fail().setMessage("对话报价初始化验证异常");
        }
    }

    /**
     * 根据报价编号查询错误信息
     *
     * @param quoteId String 报价代码
     * @return String 错误信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @PostMapping("/selectErrlog/{quoteId}")
    @ApiOperation(value = "错误信息", notes = "根据报价编号查询错误信息")
    public Message<AjaxResult> selectErrlog(@PathVariable("quoteId") String quoteId) {
        try {
            AjaxResult ar = new AjaxResult();
            ar.setData(cfetsRepoDealQuotedPrice002Service.selectErrlog(quoteId));
            return new Message<AjaxResult>().success().setData(ar);
        } catch (Exception e) {
            logger.error("根据报价编号查询错误信息异常", e);
            return new Message<AjaxResult>().fail().setMessage("根据报价编号查询错误信息异常");
        }
    }

}

