package com.joyintech.tams.invest.riskredis.quota.portfolioindicator.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPortfolioPackage003Dto;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.service.RskSupervisionPortfPoint003Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 新增组合包信息
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionPortfPoint003Controller
 * @since 1.0
 */
@Controller
@PageId("RskSupervisionPortfPoint003")
@RequestMapping("/invest/riskredis/quota/portfolioindicator/RskSupervisionPortfPoint003")
public class RskSupervisionPortfPoint003Controller extends BaseController {
	
	/**
     * RskSupervisionIndicator001Service
     */
    @Autowired(required = false)
    private RskSupervisionPortfPoint003Service rskSupervisionPortfPoint003Service;
    /**
     * 初始化的查询条件
     */
    private static final IDictCode[] DICT_CODES = new BussinessDictCodeRds[] {BussinessDictCodeRds.分类维度,BussinessDictCodeRds.收益类型,BussinessDictCodeRds.开放类型,BussinessDictCodeRds.募集方式,
	        BussinessDictCodeRds.投资性质,BussinessDictCodeRds.连接符
       };

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        setSelectOptionToModel(model, DictCode.组合类型);
        List<SelectOptionDto> listSortType = addSortType();
        String workflowId = this.getParam("workflowId");
        if (!getParam("pageModel").equals(GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
        	RskPortfolioPackage003Dto rskPortfolioPackage003Dto = new RskPortfolioPackage003Dto();
        	if(StringUtil.isNotEmpty(getParam("workflowId"))&&!WorkFlowUtil.isNotFromToDoList()){
        		rskPortfolioPackage003Dto = rskSupervisionPortfPoint003Service.getBussinessEntityByWorkflowId(workflowId);
        	}else{
        		rskPortfolioPackage003Dto=rskSupervisionPortfPoint003Service.queryPortfBagInfoByBagId(this.getParam("ptlBagId"));
            	rskPortfolioPackage003Dto.setPtlBagId(this.getParam("ptlBagId"));
            	rskPortfolioPackage003Dto.setPtlBagName(this.getParam("ptlBagName"));
        	}
        	rskPortfolioPackage003Dto.getRskPortfolioPoolConfigDto().getPtlParamPoolConfigList().forEach(item ->{
        		String[] arrayStr=item.getClassifyDimensionVal().split(GlobalConstant.COMMA);
        		listSortType.stream().filter(
	                option ->(StringUtil.equals(option.getParentId(),BussinessDictCodeRds.分类维度.getValue() + GlobalConstant.POINT + item.getClassifyDimension()))
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
            model.addAttribute("entity", rskPortfolioPackage003Dto);
        }else{
        	RskPortfolioPackage003Dto rskPortfolioPackage003Dto = new RskPortfolioPackage003Dto();
        	rskPortfolioPackage003Dto.setPtlBagId(UUID.randomUUID().toString().replaceAll("-", ""));
        	model.addAttribute("entity", rskPortfolioPackage003Dto);
        }
        model.addAttribute("CLASS_DIMENSION_SOURCE", JsonUtils.objectArray2Json(listSortType.toArray()));
        return super.init(model);
    }
    
    /**
     * @return List<SelectOptionDto>
     */
	private List<SelectOptionDto> addSortType() {
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
        SelectDto selectDto5 = this.getSelectDataFromDataDic(BussinessDictCodeRds.是否现金管理类产品, null);
        for (SelectOptionDto dto : selectDto5.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        SelectDto selectDto06 = this.getSelectDataFromDataDic(DictCode.组合类型, null);
        for (SelectOptionDto dto : selectDto06.getOptions()) {
            dto.setId(dto.getCol() + GlobalConstant.POINT + dto.getId());
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }

	/**
     * 保存按钮点击时的check
     * @param dto dto
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) RskPortfolioPackage003Dto dto, BindingResult result ) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存按钮点击时的check
     * @param dto dto
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkDelete")
    public AjaxResult checkDelete(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskPortfolioPackage003Dto dto, BindingResult result ) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存
     * @param dto
     * @throws Exception
     * @since 1.0
     */
    @DTXTransaction
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) RskPortfolioPackage003Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
     // 监督指标代码生成
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_限额组合关联监督指标管理);
        ProcessParameter<RskPortfolioPackage003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<RskPortfolioPackage003Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        // 金融产品代码作为业务编号
        String businessCode = dto.getPtlBagId();
        params.setBusinessCode(businessCode);
        params.setBusinessType(BussinessDictCodeListRds.共通业务类型_限额组合关联监督指标管理.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("RskSupervisionPortfPoint002.portfolioId"),
                GlobalConstant.OPEN_PARENTHESIS, businessCode, GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("RskSupervisionPortfPoint002.portfolioName"), GlobalConstant.OPEN_PARENTHESIS,
                entity.getPtlBagName(), GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = rskSupervisionPortfPoint003Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    @DTXTransaction
    @ResponseBody
	@RequestMapping(value = ACTION_DELETE)
	public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskPortfolioPackage003Dto dto, BindingResult br) throws WorkFlowException {
    	if (br.hasErrors()) {
            return new AjaxResult(br);
        }
    	ProcessParameter<RskPortfolioPackage003Dto> processParameter = new ProcessParameter<RskPortfolioPackage003Dto>();
        WorkFlowParameter<RskPortfolioPackage003Dto> params = new WorkFlowParameter<RskPortfolioPackage003Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getPtlBagId());
        params.setBusinessType(BussinessDictCodeListRds.共通业务类型_限额组合关联监督指标管理.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setBusinessCode(item -> item.getPtlBagId());
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("RskSupervisionPortfPoint002.portfolioId"),
                GlobalConstant.OPEN_PARENTHESIS, entity.getPtlBagId(), GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("RskSupervisionPortfPoint002.portfolioName"), GlobalConstant.OPEN_PARENTHESIS,
                entity.getPtlBagName(), GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = rskSupervisionPortfPoint003Service.start(processParameter,
            dto.getSelectedItem().toArray(
                new RskPortfolioPackage003Dto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
	}
    
}
