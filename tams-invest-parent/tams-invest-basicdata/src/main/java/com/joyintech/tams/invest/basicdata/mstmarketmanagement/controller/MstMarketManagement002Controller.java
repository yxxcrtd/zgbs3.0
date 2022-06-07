package com.joyintech.tams.invest.basicdata.mstmarketmanagement.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.dto.MstMarketManagementDto;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.service.MstMarketManagement001Service;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  
 * 基础数据管理行情管理 
 * @author dongzhikang
 * @version 1.0 2019年10月10日
 * @since 1.0
 * 
 */
@Controller
@PageId("MstMarketManagement002")
@RequestMapping("/invest/basicdata/mstmarketmanagement/MstMarketManagement002")
public class MstMarketManagement002Controller extends BaseController{
	@Autowired
	MstMarketManagement001Service  mstMarketManagement001Service;
	
	@Autowired
    private BusiProductRatingService busiProductRatingService;
	
  	@Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.评级类型);
        this.setSelectOptionToModel(model, DictCode.长期短期);
        this.setSelectOptionToModel(model, DictCode.评级机构);
        this.setSelectOptionToModel(model, DictCode.评级结果);
        this.setSelectOptionToModel(model, DictCode.评级展望, DictCode.评级机构, DictCode.变动方向);
        model.addAttribute("MAIN_GRADE_TYPE_01", DictCodeList.评级类型_主体评级.getValue());
        model.addAttribute("MAIN_GRADE_TYPE_02", DictCodeList.评级类型_债项评级.getValue());
        String workflowId = getParam("workflowId");
        String oType = getParam("oType");
        String oCode = getParam("oCode");
        String gradeDate = getParam("gradeDate");
        String gradeOrgId = getParam("gradeOrgId");
        if (WorkFlowUtil.isNotFromToDoList()) {
            MstMarketManagementDto entity = new MstMarketManagementDto();
            entity.setGradeOrgId(gradeOrgId);
            entity.setGradeDate(gradeDate);
            entity.setoCode(oCode);
            entity.setoType(oType);
            entity = mstMarketManagement001Service.selectDetail(entity);
            if (entity != null){
                entity.setOldGradeResult(entity.getGradeResult());
            }
            model.addAttribute("entity", entity);
        } else if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            MstMarketManagementDto entity  = mstMarketManagement001Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
        }
        return super.init(model);
    }
    /**
     * 资产代码的联想文本框事件
     * 
     * @return 查询的资产代码和资产名称列表
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCodeName")
    public Page<SelectOptionDto> getCodeName(){
    	  Map<String, Object> param = RequestUtil.getPageParam();
    	  if (!StringUtil.isNotEmpty(param.get("oType"))){
    	      return new Page<SelectOptionDto>();
          }
          // 获取根据输入值检索到的结果
        return mstMarketManagement001Service.getCodeName(param);
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
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) MstMarketManagementDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param dto MstMarketInstitutionsDto
     * @param br  BindingResult
     * @return AjaxResult
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) MstMarketManagementDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<MstMarketManagementDto> processParameter = new ProcessParameter<MstMarketManagementDto>();
        WorkFlowParameter<MstMarketManagementDto> params = new WorkFlowParameter<MstMarketManagementDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getoCode());
        params.setBusinessType(DictCodeListExt4.共通业务类型_评级信息.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> "对象类型" + "(" + entity.getoTypeName() + ")" + "-" + "对象名称" + "(" + entity.getoCodeName() + ")"
                        + "-" + "对象代码" + "(" + entity.getoCode() + ")"+ "-" + "评级结果" + "(" + entity.getGradeResultDis() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = mstMarketManagement001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

}
