package com.joyintech.tams.invest.risk.quota.rskspecificassettype.contorller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.risk.quota.rskspecificassettype.dto.RskSpecificAssetTypeDto;
import com.joyintech.tams.invest.risk.quota.rskspecificassettype.service.RskSpecificAssetTypeService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 特定资产类型设置
 * <p>
 * zhangnian  20200331
 */
@Controller
@PageId("RskSpecificAssetType002")
@RequestMapping("/invest/risk/quota/rskspecificassettype/RskSpecificAssetType002")
public class RskSpecificAssetType002Controller extends BaseController {

    @Autowired
    private RskSpecificAssetTypeService rskSpecificAssetTypeService;


    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[]{DictCode.是否,DictCode.授权类型一级, DictCode.授权类型二级, DictCode.授权类型三级, DictCode.授权类型四级};

    @Override
    public String init(Model model) {
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String specificId = this.getParam("specificId");
        String workflowId = getParam("workflowId");
        model.addAttribute("YES", DictCodeList.是否_是.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        //不来自于代办
        if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(specificId)) {
            RskSpecificAssetTypeDto rskSpecificAssetTypeDto = rskSpecificAssetTypeService.getBasicInfo(
                    specificId);
            model.addAttribute("entity", rskSpecificAssetTypeDto);
        }//来自于代办
        else if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            //根据工作流与留痕处理提供的共通方法获取业务实体
            RskSpecificAssetTypeDto rskSpecificAssetTypeDto = rskSpecificAssetTypeService.getBussinessEntityByWorkflowId(workflowId);
            //设置到model中
            model.addAttribute("entity", rskSpecificAssetTypeDto);
        }
        return super.init(model);
    }

    /**
     * check--保存
     *
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) RskSpecificAssetTypeDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     *
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) RskSpecificAssetTypeDto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            dto.setSpecificId(dto.getFinprodId());
        }
        WorkFlowParameter<RskSpecificAssetTypeDto> params = new WorkFlowParameter<RskSpecificAssetTypeDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getSpecificId());
        params.setBusinessType(BussinessDictCodeListRds.共同业务类型_特定资产类型设置.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("特定资产类型设置编码") +
                    GlobalConstant.OPEN_PARENTHESIS + entity.getSpecificId(), GlobalConstant.CLOSE_PARENTHESIS
            );
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<RskSpecificAssetTypeDto> parameter = new ProcessParameter<RskSpecificAssetTypeDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = rskSpecificAssetTypeService.start(parameter, dto);
        return new AjaxResult(msg);
    }
}
