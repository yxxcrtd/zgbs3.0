package com.joyintech.tams.invest.risk.creditmanagement.ceassetgroup.controller;

import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.risk.creditmanagement.ceassetgroup.dto.CreditLinesAstGrpDto;
import com.joyintech.tams.invest.risk.creditmanagement.ceassetgroup.service.CeAssetGroup002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 资产系列授信 
 * @author 裴家俊
 * @version 1.0 2019年10月23日
 * @see CeAssetGroup002Controller
 * @since 1.0
 */
@Controller
@PageId("CeAssetGroup002")
@RequestMapping("/invest/risk/creditmanagement/ceassetgroup/CeAssetGroup002")
public class CeAssetGroup002Controller extends BaseController {
	
	/**
     * CeAssetGroup002Service
     */
	 @Autowired(required = false)
	 private CeAssetGroup002Service ceAssetGroup002Service;
	 
	 @Autowired(required = false)
	 private BaseGetBusinessId baseGetBusinessId;
	    
	 @Autowired 
	 private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 初始化的查询条件
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.指标类型,
        DictCode.是否
       };

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        String workflowId = getParam("workflowId");
        // 待办跳转
        if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
            model.addAttribute("workflowId", workflowId);
            CreditLinesAstGrpDto creditLinesDto = ceAssetGroup002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", creditLinesDto);
        } else if (WorkFlowUtil.isNotFromToDoList() &&StringUtil.isNotEmpty(this.getParam("seqno"))){
            String seqno = this.getParam("seqno");
            CreditLinesAstGrpDto creditLinesDto = ceAssetGroup002Service.getBussinessEntityByIndId(seqno);
            model.addAttribute(GlobalConstant.ENTITY, creditLinesDto);
        }
        // add by yushilong “是否循环额度”字段没人值设置 20200630 start
        // 新建画面默认值设置
        else if (WorkFlowUtil.isNotFromToDoList() &&
                !StringUtil.isNotEmpty(this.getParam("seqno")) &&
                !StringUtil.isNotEmpty(workflowId)){
            CreditLinesAstGrpDto creditLinesDto = new CreditLinesAstGrpDto();
            // 设置“是否循环额度”默认值
            creditLinesDto.setIsLoopAmt(DictCodeList.是否_是.getValue());
            model.addAttribute(GlobalConstant.ENTITY, creditLinesDto);
        }
        // add by yushilong “是否循环额度”字段没人值设置 20200630 end
        return super.init(model);
    }
  
    /**
     * 保存按钮点击时的check
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) CreditLinesAstGrpDto dto, BindingResult result ) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存
     * @param dto
     * @throws Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody CreditLinesAstGrpDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
       	 if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产系列授信);
                dto.setSeqno(baseGetBusinessId.buildBussinessCode(codeParameter));

                dto.setCustomerId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                dto.setSeqno(IDGenerator.generate());
                dto.setCustomerId(IDGenerator.generate());
            }
       }
       //文件信息
       if (CollectionUtils.isNotEmpty(dto.getUploadFileList())
           && StringUtil.isNotEmpty(dto.getSeqno())) {
           // 将附件和当前业务对象绑定（更新附件的业务代码）
           fileAttachmentProcess.updateBussinessNo(
               dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                   Collectors.toList()),
               dto.getSeqno());
       }
       BussinessCodeParameter parameter = new BussinessCodeParameter();
       parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产系列授信);
       ProcessParameter<CreditLinesAstGrpDto> processParameter = new ProcessParameter<>();
       WorkFlowParameter<CreditLinesAstGrpDto> params = new WorkFlowParameter<>();
       params.setPageId(this.getPageId());
       params.setElementId(dto.getElementId());
       // 授权代码作为业务编号
       String businessCode = dto.getSeqno();
       params.setBusinessCode(businessCode);
       params.setBusinessType(DictCodeList.共通业务类型_资产系列授信.getValue());

       // 设置流程业务说明, entity即传入的dto
       params.setComment(entity -> {
           return WorkFlowUtil.concatComment(getMessage("CeAssetGroup002.seqno"),
               GlobalConstant.OPEN_PARENTHESIS, businessCode, GlobalConstant.CLOSE_PARENTHESIS,
               getMessage("CeAssetGroup002.customerId"), GlobalConstant.OPEN_PARENTHESIS,
               entity.getCustomerId(), GlobalConstant.CLOSE_PARENTHESIS);
       });
       processParameter.setWorkFlowParameter(params);
       String msg = ceAssetGroup002Service.start(processParameter, dto);
       return new AjaxResult(msg);
    }
}
