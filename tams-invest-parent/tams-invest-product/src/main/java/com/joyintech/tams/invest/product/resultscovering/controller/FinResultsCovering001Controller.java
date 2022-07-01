package com.joyintech.tams.invest.product.resultscovering.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.product.resultscovering.dto.FinResultsCovering001Dto;
import com.joyintech.tams.invest.product.resultscovering.dto.FinResultsCovering002Dto;
import com.joyintech.tams.invest.product.resultscovering.service.FinResultsCovering001Service;
import com.joyintech.tams.invest.product.resultscovering.service.FinResultsCovering002Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@PageId("FinResultsCovering001")
@RequestMapping("/invest/product/resultscovering/FinResultsCovering001")
public class FinResultsCovering001Controller extends BaseController {
	@Autowired
	private IPlatformDate iPlatformDate;
	@Autowired
	private FinResultsCovering001Service finResultsCovering001Service;
	@Autowired
	private FinResultsCovering002Service finResultsCovering002Service;

	@Autowired
	private  PreTrdCommonBaseService preTrdCommonBaseService;
	@Override
	public String init(Model model) {
		this.setSelectOptionToModel(model, DictCode.回补方式, DictCode.回补规则,DictCode.有效状态);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date syaDate = iPlatformDate.getSysDate();
		Date date1 = DateUtils.addMonths(syaDate, -1);
		String sysDateStr = format.format(syaDate);
		String dateStr = format.format(date1);
		model.addAttribute("coveringDate", dateStr + " - " + sysDateStr);
		return super.init(model);
	}

	/**
	 * 画面查詢方法
	 * 
	 * @return 所有数据
	 * @throws Exception
	 *             异常
	 */
	@ResponseBody
	@RequestMapping("selectForPage")
	public Page<FinResultsCovering001Dto> selectForPage() {
		return finResultsCovering001Service.selectForPage(RequestUtil.getPageParam());
	}

	
    /**
     * 产品代码下拉框
     * @param param 过滤参数
     * @return 下拉框列表
     * @throws Exception Exception
     */
    @RequestMapping("queryProdIdInfo")
    @ResponseBody
    public Page<SelectOptionDto> queryProdIdInfo(@RequestParam Map<String, Object> param)
	{
        return finResultsCovering001Service.queryProdIdInfo(param);
    }
	
    /**
     * 产品款型下拉框
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("queryProdStyleInfo")
    @ResponseBody
    public Page<SelectOptionDto> queryProdStyleInfo(@RequestParam Map<String, Object> param) {
        return finResultsCovering001Service.queryProdStyleInfo(param);
        
    }
	
	/**
	 * 导出信息excel
	 * 
	 * @return excel
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("export")
	public ResponseEntity<byte[]> export() {
		return finResultsCovering001Service.export(this.getPageId(), RequestUtil.getParameters(getRequest()));
	}

	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@Valid @RequestBody FinResultsCovering002Dto dto, BindingResult result) {
		String msg = "";
		if ("02".equals(dto.getCoveringForm())) {
			msg = "自动回补不允许撤销";
			return new AjaxResult(msg, MessageType.错误消息);
		}
		List<FinResultsCovering001Dto> list1 = finResultsCovering001Service.findPtradeid(dto.getTradeId());
		if (CollectionUtils.isNotEmpty(list1)) {
			for (FinResultsCovering001Dto finResultsCovering001Dto : list1) {
				String tradeId = finResultsCovering001Dto.getTradeId();
				if (StringUtil.isNotEmpty(tradeId)) {
					if (tradeId.endsWith("R")) {
						msg = "该数据已撤销。";
						return new AjaxResult(msg, MessageType.错误消息);
					}
				}
				if (StringUtil.isNotEmpty(finResultsCovering001Dto.getCoveringDate())) {
					if (finResultsCovering001Dto.getCoveringDate().compareTo(iPlatformDate.getSysDate()) < 0) {
						msg = "仅允许撤销回补日大于等于平台日期的交易";
						return new AjaxResult(msg, MessageType.错误消息);
					}
				}

			}
		}

		if (StringUtil.equals(dto.getOperateType(), "revoke")) {
			if (preTrdCommonBaseService.isAlreadyRevoked(dto.getTradeId())) {
				ValidMessage validMessage = new ValidMessage(getMessage("TRD_W_0001",
						getMessage("pingan.common.revoke"), getMessage("pingan.common.revoke")));
				ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
				errorList.add(validMessage);
				AjaxResult ar = new AjaxResult();
				ar.setErrorList(errorList);
				return ar;
			}
		}
		return commonCheck(result, dto.getOperateType());
	}

	@ResponseBody
	@RequestMapping("doRevoke")
	public AjaxResult doRevoke(@RequestBody @Validated({ ValidateGroupEdit.class }) FinResultsCovering002Dto dto,
			BindingResult br) throws WorkFlowException {
		if (br.hasErrors()) {
			return new AjaxResult(br);
		}
		// 新建留痕表参数和工作流参数
		ProcessParameter<FinResultsCovering002Dto> processParameter = new ProcessParameter<FinResultsCovering002Dto>();
		processParameter.setOperationType(OperationType.REVOKE);
		WorkFlowParameter<FinResultsCovering002Dto> params = new WorkFlowParameter<FinResultsCovering002Dto>();
		params.setPageId("FinResultsCovering002");
		params.setElementId(dto.getElementId());
		// params.setBusinessCode(dto.getProdId());
		BussinessCodeParameter parameter = new BussinessCodeParameter();
		parameter.setCommonBusinessExtType(DictCodeListExt1.fromDictCodeList(DictCodeListExt1.共通业务类型_回补交易));
		// dto.setTradeId(baseGetBusinessIdImpl.buildBussinessCode(parameter));
		params.setBusinessCode(dto.getTradeId());
		params.setBusinessType(DictCodeListExt1.共通业务类型_回补交易.getValue());
		// 设置流程业务说明, entity即传入的dto
		// "产品代码"+"("+产品代码+")"+"-""产品简称"+"("+产品简称+")"
		params.setComment(entity -> this.getMessage("FinResultsCovering001.prodId") + "(" + entity.getProdId() + ")"
				+ this.getMessage("FinResultsCovering002.tradeId") + "(" + entity.getTradeId() + ")"
				+ this.getMessage("FinResultsCovering001.coveringDate") + "("
				+ DateUtil.format(entity.getCoveringDate()) + ")");
		// 塞入工作流参数
		processParameter.setWorkFlowParameter(params);
		// 流程开始
		String msg = finResultsCovering002Service.start(processParameter, dto);
		return new AjaxResult(msg);
	}

}
