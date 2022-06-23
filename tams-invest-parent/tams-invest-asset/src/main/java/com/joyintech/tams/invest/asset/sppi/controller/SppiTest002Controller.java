/*
 * 文件名：SppiTest002Controller
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-09
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.asset.sppi.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.asset.sppi.dto.SppiTemplateDto;
import com.joyintech.tams.invest.asset.sppi.service.SppiTest002Service;
import com.joyintech.tams.invest.asset.sppi.util.SppiUtils;
import com.joyintech.tams.invest.asset.sppi.valid.SppiTestValidator;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * SPPI测试新建&查看控制类
 *
 * @author Jiaoxujin
 * @version 1.0 2020-10-09
 */
//@PageId("SppiTest002")
// 使用html引入方式，更符合目前框架，如： <div class="page-component" id="SppiTemplateBondDtt" th:data-path="@{/invest/asset/sppi/SppiTest002/init}"
//                     th:data-params="|{sppiPageId: 'SppiTemplateBondDtt', assetType: 'F01', entity:'${entity_sppiTest001Dto}'|"></div>
@Controller
@RequestMapping("/invest/asset/sppi/SppiTest002")
public class SppiTest002Controller extends BaseController {
    //private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String VIRTUAL_PAGE_ID = "SppiTest002";

    @Autowired
    private SppiTest002Service sppiTest002Service;

    @Autowired
    private SppiTestValidator sppiTestValidator;

    /**
     * 先执行子类 @ModelAttribute 注解的方法，再执行父类中 @ModelAttribute 注解的方法。
     */
    @ModelAttribute
    public void dynamicPageId() {
        String assetType = getParam("assetType");
        // 可由前端决定使用哪个SPPI模板，主要是调试用
        String sppiPageId = getParam("sppiPageId");
        String pageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);

        if (StringUtils.isBlank(sppiPageId)) {
            sppiPageId = SppiUtils.selectPageId(assetType,
                    CacheUtil.getSysParam(BussinessConstant.BOND_SPPI_USE_TEMPLATE),
                    CacheUtil.getSysParam(BussinessConstant.NOT_BOND_SPPI_USE_TEMPLATE));
        }

        if (StringUtils.isNotBlank(workflowId) && !WorkFlowUtil.isNotFromToDoList()) {
            SppiTemplateDto dto = sppiTest002Service.getBussinessEntityByWorkflowId(workflowId);

/*                sppiPageId = SppiUtils.selectPageId(dto.getAssetType(),
                    CacheUtil.getSysParam(BussinessConstant.BOND_SPPI_USE_TEMPLATE),
                    CacheUtil.getSysParam(BussinessConstant.NOT_BOND_SPPI_USE_TEMPLATE));*/

            //JIRA #XMTOF-80，这个实际商用环境中 应该不会发生的。它们部署时就决定了系统参数，后面不会变更。
            if (StringUtils.equals(dto.getSppiTmpType(), BussinessConstant.SPPI_TEMPLATE_TYPE_BOND)) {
                sppiPageId = SppiUtils.selectPageId(DictCodeList.平安资产类型_债券.getValue(),
                        dto.getSppiTmpNo(),
                        dto.getSppiTmpNo());
            } else {
                sppiPageId = SppiUtils.selectPageId(DictCodeList.平安资产类型_项目类资产.getValue(),
                        dto.getSppiTmpNo(),
                        dto.getSppiTmpNo());
            }
        }
        //JIRA #XMTOF-80，这个实际商用环境中 应该不会发生的。它们部署时就决定了系统参数，后面不会变更。
        else if (StringUtils.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
            String sppiTmpType = getParam("sppiTmpType");
            String sppiTmpNo = getParam("sppiTmpNo");

            if (StringUtils.equals(sppiTmpType, BussinessConstant.SPPI_TEMPLATE_TYPE_BOND)) {
                sppiPageId = SppiUtils.selectPageId(DictCodeList.平安资产类型_债券.getValue(),
                        sppiTmpNo,
                        sppiTmpNo);
            } else {
                sppiPageId = SppiUtils.selectPageId(DictCodeList.平安资产类型_项目类资产.getValue(),
                        sppiTmpNo,
                        sppiTmpNo);
            }
        }

        if (StringUtils.isNotBlank(sppiPageId)) {
            setSuperField(this, "pageId", sppiPageId);
        }
    }

    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        String finprodId = getParam("finprodId");
        String finprodName = getParam("finprodName");
        String assetType = getParam("assetType");
        SppiTemplateDto dto = new SppiTemplateDto();

        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            dto = sppiTest002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("businessId", workflowId);

            //覆盖框架的设置(page已设置为相应的html,仅是覆盖pageId) for 提交--》驳回--》修改--》再提交 --》再打开审核时，打不开
            model.addAttribute("realPageId", this.getPageId());
            model.addAttribute(GlobalConstant.PAGE_ID, VIRTUAL_PAGE_ID);
        }
        else if (GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE.equals(this.getPageModel())) {
            String sppiTestId = getParam("sppiTestId");

            // 从数据库SPPI_查询详细信息
            dto = sppiTest002Service.selectSppiTestDetail(sppiTestId, finprodId);
        }
        else if (GlobalConstant.DETAIL_PAGE_MODEL_NEW.equals(this.getPageModel())) {
            dto.setFinprodId(finprodId);
            dto.setFinprodName(finprodName);
            dto.setAssetType(assetType);

            if (StringUtils.equals(assetType, DictCodeList.平安资产类型_债券.getValue())
                    ||StringUtils.equals(assetType, DictCodeList.平安资产类型_一级市场债.getValue())) {
                dto.setSppiTmpType(BussinessConstant.SPPI_TEMPLATE_TYPE_BOND);
                dto.setSppiTmpNo(CacheUtil.getSysParam(BussinessConstant.BOND_SPPI_USE_TEMPLATE));
            }
            else if (StringUtils.equals(assetType, DictCodeList.平安资产类型_项目类资产.getValue()) ||
                    StringUtils.equals(assetType, DictCodeList.平安资产类型_计划类资产_利率型.getValue())) {
                dto.setSppiTmpType(BussinessConstant.SPPI_TEMPLATE_TYPE_NOT_BOND);
                dto.setSppiTmpNo(CacheUtil.getSysParam(BussinessConstant.NOT_BOND_SPPI_USE_TEMPLATE));
            }
        }

        sppiTest002Service.genFinprodMarketId(dto);

        model.addAttribute(GlobalConstant.ENTITY, dto);
        model.addAttribute("SPPI_TESTING", DictCodeListExt3.SPPI测试状态_测试中.getValue());
        model.addAttribute("SPPI_PASS", DictCodeListExt3.SPPI测试状态_测试通过.getValue());
        model.addAttribute("SPPI_NOT_PASS", DictCodeListExt3.SPPI测试状态_测试不通过.getValue());
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        model.addAttribute("NO", DictCodeList.是否_否.getValue());
        model.addAttribute("NA", DictCodeListExt3.是否不适用_不适用.getValue());
        model.addAttribute("FIXED_RATE", DictCodeList.利率类型_固定利率.getValue());
        model.addAttribute("FLOAT_RATE", DictCodeList.利率类型_浮动利率.getValue());

        //region SPPI测试状态
        SelectOptionDto passDto = new SelectOptionDto();
        SelectOptionDto notPassDto = new SelectOptionDto();
        passDto.setId(DictCodeListExt3.SPPI测试状态_测试通过.getValue());
        passDto.setText(DictCodeListExt3.SPPI测试状态_测试通过.getCodeName());
        notPassDto.setId(DictCodeListExt3.SPPI测试状态_测试不通过.getValue());
        notPassDto.setText(DictCodeListExt3.SPPI测试状态_测试不通过.getCodeName());
        List<SelectOptionDto> statusList = Arrays.asList(passDto, notPassDto);
        model.addAttribute(DictCode.SPPI测试状态.getValue(), JsonUtils.objectArray2Json(statusList.toArray()));
        //endregion

        //region 利率类型
        SelectOptionDto fixRateDto = new SelectOptionDto();
        SelectOptionDto floatRateDto = new SelectOptionDto();
        fixRateDto.setId(DictCodeList.利率类型_固定利率.getValue());
        fixRateDto.setText(DictCodeList.利率类型_固定利率.getCodeName());
        floatRateDto.setId(DictCodeList.利率类型_浮动利率.getValue());
        floatRateDto.setText(DictCodeList.利率类型_浮动利率.getCodeName());
        List<SelectOptionDto> rateList = Arrays.asList(floatRateDto, fixRateDto);
        model.addAttribute(DictCode.利率类型.getValue(), JsonUtils.objectArray2Json(rateList.toArray()));
        //endregion

        //region 收益类型
        SelectOptionDto benifitDtob = new SelectOptionDto();
        SelectOptionDto benifitDtoe = new SelectOptionDto();
        SelectOptionDto benifitDton = new SelectOptionDto();
        benifitDtob.setId("B");
        benifitDtob.setText(getMessage("SppiTest002.breakEvenBenifit"));
        benifitDtoe.setId("E");
        benifitDtoe.setText(getMessage("SppiTest002.expectBenifit"));
        benifitDton.setId("N");
        benifitDton.setText(getMessage("SppiTest002.notConfirmedBenifit"));
        List<SelectOptionDto> benifitList = Arrays.asList(benifitDton, benifitDtoe, benifitDtob);
        model.addAttribute("BENEFIT", JsonUtils.objectArray2Json(benifitList.toArray()));
        //endregion

        //region 管理方式
        SelectOptionDto manageDtod = new SelectOptionDto();
        SelectOptionDto manageDtos = new SelectOptionDto();
        manageDtod.setId("D");
        manageDtod.setText(getMessage("SppiTest002.dynamicManage"));
        manageDtos.setId("S");
        manageDtos.setText(getMessage("SppiTest002.staticManage"));
        List<SelectOptionDto> manageList = Arrays.asList(manageDtos, manageDtod);
        model.addAttribute("MANAGE_TYPE", JsonUtils.objectArray2Json(manageList.toArray()));
        //endregion

        //region 是否匹配
        SelectOptionDto matchDtom = new SelectOptionDto();
        SelectOptionDto matchDton = new SelectOptionDto();
        matchDtom.setId(DictCodeList.是否_是.getValue());
        matchDtom.setText(getMessage("SppiTest002.match"));
        matchDton.setId(DictCodeList.是否_否.getValue());
        matchDton.setText(getMessage("SppiTest002.notMatch"));
        List<SelectOptionDto> matchTypeList = Arrays.asList(matchDtom, matchDton);
        model.addAttribute("MATCH_TYPE", JsonUtils.objectArray2Json(matchTypeList.toArray()));
        //endregion

        this.setSelectOptionToModel(model, DictCode.是否, DictCode.是否不适用);

        return super.init(model);
    }

    /**
     * 获取当前的PageId
     * @return String
     */
    @ResponseBody
    @RequestMapping("getCurrentPageId")
    public String getCurrentPageId(){
        return super.getPageId();
    }

    /**
     * 提交验证
     * @param dto
     * @param br
     * @return AjaxResult
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) SppiTemplateDto dto, BindingResult br) {
        AjaxResult result = validate(dto, br);
        if (result != null) {
            return result;
        }
        return commonCheck(br, dto.getOperateType());
    }

    /**
     * 保存
     * @param dto
     * @param br
     * @return AjaxResult
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class})SppiTemplateDto dto, BindingResult br) throws Exception {
        AjaxResult result = validate(dto, br);
        if (result != null) {
            return result;
        }

        ProcessParameter<SppiTemplateDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<SppiTemplateDto> params = new WorkFlowParameter<>();
        //params.setPageId(this.getPageId());
        //params.setPageId(MapUtils.getString(formData,"pageId"));
        params.setPageId(VIRTUAL_PAGE_ID);
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getFinprodId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_SPPI测试.getValue());

        // 既然是Deprecated方法，为什么还必须要求设置？
        params.setComment(entity -> SpringContextUtil.getTextValue("SppiTest002.finprodId") + "(" + entity.getFinprodId() + ")" + "-"
                + SpringContextUtil.getTextValue("SppiTest002.finprodName") + "("
                + entity.getFinprodName() + ")");

        processParameter.setWorkFlowParameter(params);
        String msg = sppiTest002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    private AjaxResult validate(SppiTemplateDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        SppiUtils.setQaList(dto, msg -> this.getMessage(msg));
        List<ValidMessage> msgList = sppiTestValidator.validData(dto);
        if (CollectionUtils.isNotEmpty(msgList)) {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.setErrorList(msgList);
            return ajaxResult;
        }
        return null;
    }

    private static void setSuperField(Object paramClass, String paramString,
                                      Object newClass) {
        Field field = null;
        try {
            field = paramClass.getClass().getSuperclass().getDeclaredField(paramString);
            field.setAccessible(true);
            field.set(paramClass, newClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

}
