package com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.MstCustomerInfoAddDto;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.dto.SeniorExecutiveInfDto;
import com.joyintech.tams.invest.risk.customerrelationtree.agencydetailsinf.service.AgencyDetailsInf002Service;

@Controller
@PageId("AgencyDetailsInf002")
@RequestMapping("/invest/risk/customerrelationtree/agencydetailsinf/AgencyDetailsInf002")
public class AgencyDetailsInf002Controller extends BaseController {

	/**
	 * rskAuthorityManageService
	 */
	@Autowired
	private AgencyDetailsInf002Service agencyDetailsInf002Service;
	/**
	 * 初始化的查询条件
	 */
	private static final IDictCode[] DICT_CODES = new DictCode[] { DictCode.关联分类, DictCode.关联机构类型, DictCode.录入方式 };

	@Override
	public String init(Model model) {
		setSelectOptionToModel(model, DICT_CODES);
		List<SelectOptionDto> orgList = makeAllRelateType();
		model.addAttribute("CorOrgLst", JsonUtils.objectArray2Json(orgList.toArray()));
		String orgNo = getParam("orgNo");
		model.addAttribute("ORG_NO", orgNo);
		return super.init(model);
	}

	/**
	 * 关联机构代码
	 * 
	 * @return List List
	 * @throws Exception
	 *             Exception
	 */
	@ResponseBody
	@RequestMapping("/getRelateOrgInf")
	public Page<SelectOptionDto> getRelateOrgInf() {
		return agencyDetailsInf002Service.getRelateOrgInf(RequestUtil.getPageParam());
	}

	/**
	 * 获取当前选中企业信息
	 * 
	 * @return List List
	 * @throws Exception
	 *             Exception
	 */
	@ResponseBody
	@RequestMapping("/getMstCustomerInfoAdd")
	public List<MstCustomerInfoAddDto> getMstCustomerInfoAdd() {
		return agencyDetailsInf002Service.getMstCustomerInfoAdd(RequestUtil.getParameters(getRequest()));
	}

	/**
	 * 获取当前选中机构高管信息
	 * 
	 * @return List List
	 * @throws Exception
	 *             Exception
	 */
	@ResponseBody
	@RequestMapping("/getSeniorExecutiveInf")
	public Page<SeniorExecutiveInfDto> getSeniorExecutiveInf() {
		Page<SeniorExecutiveInfDto> page = new Page<>();
		List<SeniorExecutiveInfDto> lst = agencyDetailsInf002Service.getCorrelationInf(RequestUtil.getParameters(getRequest()));
		page.setRoot(lst);
		page.setRecords(lst.size());
		return page;
	}

	/**
	 * 生成交易方向的枚举值 <功能详细描述>
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	private List<SelectOptionDto> makeAllRelateType() {
		List<SelectOptionDto> listRelateType = new ArrayList<SelectOptionDto>();
		SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.关联机构类型, null);
		for (SelectOptionDto dto : selectDto1.getOptions()) {
			if (!dto.getId().equals("99"))// 关联机构类型去除高管
				listRelateType.add(dto);
		}
		return listRelateType;
	}

	/**
	 * 保存按钮点击时的check
	 *
	 * @param dto
	 *            dto
	 * @param result
	 *            BindingResult
	 * @return check结果
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Validated({ ValidateGroupEdit.class }) SeniorExecutiveInfDto dto,
			BindingResult result) {
		if (result.getAllErrors().isEmpty()) {
			AjaxResult ar = commonCheck(result, dto.getOperateType());
			return ar;
		}
		return commonCheck(result, dto.getOperateType());
	}

	/**
	 * 保存
	 * 
	 * @param dto
	 *            dto
	 * @param br
	 *            br
	 * @return 结果
	 * @throws Exception
	 *             异常
	 */
	@ResponseBody
	@RequestMapping("save")
	public boolean save(@RequestBody SeniorExecutiveInfDto dto) {
		return agencyDetailsInf002Service.save(dto);
	}

}
