package com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto.RskPortAutoRelatePoint002Dto;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto.RskPortAutoRelatePointDto;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.service.RskPortAutoRelatePoint002Service;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.service.RskPortAutoRelatePointService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 组合自动关联监控指标-规则新建
 * @author 王泽慧
 * @version 1.0 2019年8月1日
 * @see RskPortAutoRelatePoint001Controller
 * @since 1.0
 */
@Controller
@PageId("RskPortAutoRelatePoint002")
@RequestMapping("/invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002")
public class RskPortAutoRelatePoint002Controller extends BaseController {
	
	@Autowired
	private RskPortAutoRelatePointService rskPortAutoRelatePointService;
	
	@Autowired
	private RskPortAutoRelatePoint002Service rskPortAutoRelatePoint002Service;
	
	/**
     * 初始化的查询条件
     */
   private static final IDictCode[] DICT_CODES = new BussinessDictCodeRds[] {BussinessDictCodeRds.连接符, BussinessDictCodeRds.分类维度};
	@Override
    public String init(Model model) {
		setSelectOptionToModel(model, DICT_CODES);
		List<SelectOptionDto> classifyDimensionValList = makeClassifyDimensionVal();
        model.addAttribute("CLASS_DIMENSION_SOURCE", JsonUtils.objectArray2Json(classifyDimensionValList.toArray()));
        String id = RequestUtil.getParam("id");
        String name = RequestUtil.getParam("name");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
		if(workflowId != null && !StringUtil.equals(workflowId, "")){
			RskPortAutoRelatePoint002Dto dto = rskPortAutoRelatePoint002Service.getBussinessEntityByWorkflowId(workflowId);
			RskPortAutoRelatePointDto entity = new RskPortAutoRelatePointDto();
			entity.setRuleId(dto.getRuleId());
			entity.setWorkflowId(workflowId);
			model.addAttribute("entity", dto);
		}
        //编辑页面显示
		else if (!StringUtil.isNotEmpty(id)) {
        	RskPortAutoRelatePointDto dto = new RskPortAutoRelatePointDto();
        	dto.setRuleId(IDGenerator.generate());
        	model.addAttribute("entity", dto);
        }else if(StringUtil.isNotEmpty(id)&&StringUtil.isNotEmpty(name)){
        	RskPortAutoRelatePointDto dto = new RskPortAutoRelatePointDto();
        	dto.setRuleId(id);
        	dto.setRuleName(name);
        	model.addAttribute("entity", dto);
        }
		return super.init(model);
	}
	private List<SelectOptionDto> makeClassifyDimensionVal() {
		List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();

        SelectDto selectDto1 = this.getSelectDataFromDataDic(BussinessDictCodeRds.收益类型, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(BussinessDictCodeRds.募集方式, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(BussinessDictCodeRds.开放类型, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto4 = this.getSelectDataFromDataDic(BussinessDictCodeRds.投资性质, null);
        for (SelectOptionDto dto : selectDto4.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto5 = this.getSelectDataFromDataDic(BussinessDictCodeRds.开放周期, null);
        for (SelectOptionDto dto : selectDto5.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto6 = this.getSelectDataFromDataDic(BussinessDictCodeRds.是否面向单一投资者, null);
        for (SelectOptionDto dto : selectDto6.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto7 = this.getSelectDataFromDataDic(BussinessDictCodeRds.说明书是否披露风险标志, null);
        for (SelectOptionDto dto : selectDto7.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        return listFloatRate;
	}
	
	/**
     * 规则修改查询数据
     * 
     * @return Page<PaRskCustomerRatingDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskPortAutoRelatePoint002Dto> selectAllRule()
         {
    	Page<RskPortAutoRelatePoint002Dto> page = new Page<RskPortAutoRelatePoint002Dto>();
    	String workflowId = (String)RequestUtil.getPageParam().get("workflowId");
    	if(StringUtil.isNotEmpty(workflowId)){
    		RskPortAutoRelatePoint002Dto dto = rskPortAutoRelatePoint002Service.getBussinessEntityByWorkflowId(workflowId);
    		if(dto!=null&&StringUtil.isNotEmpty(dto.getSelectedItem())){
    			page.setRoot(dto.getSelectedItem());
    			page.setRecords(dto.getSelectedItem().size());
    		}
    		
    	}else{
    		page = rskPortAutoRelatePointService.selectAllRule(
                    RequestUtil.getPageParam());
    	}
    	List<SelectOptionDto> classifyDimensionValList = makeClassifyDimensionVal();
    	
    	if(page!=null&&!StringUtil.isEmptyList(page.getRoot())){
    		page.getRoot().forEach(item ->{
        		String[] arrayStr=item.getClassifyDimensionVal().split(GlobalConstant.COMMA);
        		classifyDimensionValList.stream().filter(
	                option -> StringUtil.equals(option.getParentId(), BussinessDictCodeRds.分类维度.getValue() + GlobalConstant.POINT + item.getClassifyDimension())
	               &&(item.getClassifyDimensionVal().indexOf(option.getId().replace(option.getCol() + GlobalConstant.POINT,""))>=0)
                       ).forEach(
                              option -> {
                                  List<String> listStrings = Stream.of(arrayStr).collect(Collectors.toList());
                                  List<String> listStrNew=new ArrayList<String>();
                                  for(String dtoStr:listStrings){
                                	  listStrNew.add(option.getCol() + GlobalConstant.POINT+dtoStr);
                                  }
                                  item.setClassifyDimensionVal(String.join(",", listStrNew));
                              });
        	} );
    	}
        return page;
    }
    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) RskPortAutoRelatePoint002Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @DTXTransaction
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) RskPortAutoRelatePoint002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<RskPortAutoRelatePoint002Dto> processParameter = new ProcessParameter<RskPortAutoRelatePoint002Dto>();
        WorkFlowParameter<RskPortAutoRelatePoint002Dto> params = new WorkFlowParameter<RskPortAutoRelatePoint002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
      //修改时为画面所选记录的规则编号。
        params.setBusinessCode(dto.getRuleName());
        params.setBusinessType(BussinessDictCodeListRds.共同业务类型_组合自动关联指标.getValue());
     // 设置流程业务说明, entity即传入的dto
        // 业务说明：“规则编号：”+画面.规则编号t
        params.setComment(entity -> "规则编号:" + "(" + entity.getRuleId() + ")-规则名称:("+entity.getRuleName()+")");
        processParameter.setWorkFlowParameter(params);
        String msg = rskPortAutoRelatePoint002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    /**
     *  页面删除
     * @param dto 费用品种
     * @param br br
     * @return 删除结果
     * @throws Exception Exception
     */
    @DTXTransaction
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskPortAutoRelatePoint002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<RskPortAutoRelatePoint002Dto> processParameter = new ProcessParameter<RskPortAutoRelatePoint002Dto>();
        WorkFlowParameter<RskPortAutoRelatePoint002Dto> params = new WorkFlowParameter<RskPortAutoRelatePoint002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getRuleId());
        params.setBusinessType(BussinessDictCodeListRds.共同业务类型_组合自动关联指标.getValue());
        // 设置流程业务说明, entity即传入的dto
        // 业务说明：“流水号：” + 上述取得的流水号+“用户：”+画面.用户+“授权额度：”+画面.授权额度
        params.setComment(entity -> "规则编号:" + "(" + entity.getRuleId() + ")-规则名称:(" + entity.getRuleName() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = rskPortAutoRelatePoint002Service.start(processParameter,
            dto.getSelectedItem().toArray(new RskPortAutoRelatePoint002Dto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
    }
    
}
