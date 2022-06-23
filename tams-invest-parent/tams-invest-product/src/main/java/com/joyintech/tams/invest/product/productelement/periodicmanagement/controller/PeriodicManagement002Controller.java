package com.joyintech.tams.invest.product.productelement.periodicmanagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.service.PeriodicManagement001Service;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;
import com.joyintech.tams.common.base.entity.FinFloatCalcRuleDetail;
import com.joyintech.tams.component.business.common.service.DateCalService;
//import com.joyintech.tams.component.business.prod.BussFinProductFloatFeeService;
import com.joyintech.tams.component.business.prod.dto.BussBenchMarkInfoDto;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFloatFeeDto;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.impl.dao.BaseGetBussinessIdMapper;
import com.joyintech.tams.invest.product.messageadjust.finfloatmgtexpquotation.service.FinFloatMgtExpQuotation001Service;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.service.PeriodicManagement002Service;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 期次管理-新建
 *
 * @author zhaojintao
 */
@Controller("periodicmanagement002Controller")
@PageId("PeriodicManagement002")
@RequestMapping("/invest/product/productelement/periodicmanagement/PeriodicManagement002")
public class PeriodicManagement002Controller extends BaseController {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(PeriodicManagement002Controller.class);

    @Autowired
    private PeriodicManagement002Service periodicManagement002Service;

    @Autowired
    private FinFloatMgtExpQuotation001Service finFloatMgtExpQuotation001Service;

//	@Autowired
//	private BussFinProductFloatFeeService bussFinProductFloatFeeService;

    @Autowired
    private BaseGetBussinessIdMapper baseGetBussinessIdMapper;

    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.利率类型, DictCode.是或否, DictCode.生效规则,
                DictCode.利率行情, DictCode.披露类型, DictCode.币种, DictCode.重置规则, DictCode.计费基数_费用品种, DictCode.计息基础);
        // 浮动利率下拉框（含利率行情、复合基准四个部分）
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();
        // 利率行情
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.利率行情, null);
        for (SelectOptionDto dto : selectDto.getOptions()) {
            SelectOptionDto dto1 = new SelectOptionDto();
            dto1.setId(dto.getId());
            dto1.setText(dto.getText());
            dto1.setObject(DictCodeListExt1.基准类型_2_利率);
            listFloatRate.add(dto1);
        }
        // 复合基准
        List<SelectOptionDto> finBaseRuleInfoList = finFloatMgtExpQuotation001Service.getFinBaseRuleInfo();
        for (SelectOptionDto dto : finBaseRuleInfoList) {
            SelectOptionDto dto1 = new SelectOptionDto();
            dto1.setId(dto.getId());
            dto1.setText(dto.getText());
            dto1.setObject(DictCodeListExt1.基准类型_2_复合);
            listFloatRate.add(dto1);
        }
        model.addAttribute("INDEX_ID_SOURCE", JsonUtils.objectArray2Json(listFloatRate.toArray()));
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (StringUtil.isNotEmpty(workflowId) && !WorkFlowUtil.isNotFromToDoList()) {
            PeriodicManagement001Dto dto = periodicManagement002Service.getBussinessEntityByWorkflowId(workflowId);
            if (dto.getStartDate() == null) {
                dto.setStartDate(iPlatformDate.getSysDate());
            }
            model.addAttribute("fromToDoList", !WorkFlowUtil.isNotFromToDoList());
            model.addAttribute("entity", dto);
        } else if (WorkFlowUtil.isNotFromToDoList()) {
            PeriodicManagement001Dto entity = new PeriodicManagement001Dto();
            BussBenchMarkInfoDto bussBenchMarkInfoDto = new BussBenchMarkInfoDto();
            BussFinProductFloatFeeDto bussFinProductFloatFeeDto = new BussFinProductFloatFeeDto();
            try {
                entity = (PeriodicManagement001Dto) JsonUtils.map2entity(RequestUtil.getPageParam(), PeriodicManagement001Dto.class);
                if (StringUtil.equals(entity.getOperateType(), "confrim")) {
                    BeanUtil.copyPropertiesIgnoreNull(bussBenchMarkInfoDto, entity);
                    if (entity.getBranch() == null) {
                        entity.setBranch("0");
                    }
                }
//				if(StringUtil.equals(entity.getOperateType(), "look")){
                // 期次管理-双击查看 产品披露业绩基准 和 浮动管理费业绩基准 add by wangcheng 20191105 start
                FinBaseRuleInfo finBaseRuleInfo产品披露 = periodicManagement002Service.queryFinBaseRuleInfo(entity, DictCodeListExt1.业绩基准类型_产品披露.getValue());
                FinBaseRuleInfo finBaseRuleInfo浮动管理费 = periodicManagement002Service.queryFinBaseRuleInfo(entity, DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
                if (StringUtil.isNotEmpty(finBaseRuleInfo产品披露)) {
                    BeanUtil.copyPropertiesIgnoreNull(bussBenchMarkInfoDto, finBaseRuleInfo产品披露);
                }
                if (StringUtil.isNotEmpty(finBaseRuleInfo浮动管理费)) {
                    BeanUtil.copyPropertiesIgnoreNull(bussFinProductFloatFeeDto, finBaseRuleInfo浮动管理费);
                }
                // 期次管理-双击查看 产品披露业绩基准 和 浮动管理费业绩基准 add by wangcheng 20191105 end
// 				}
                entity.setBussBenchMarkInfoDto(bussBenchMarkInfoDto);
                entity.setBussFinProductFloatFeeDto(bussFinProductFloatFeeDto);
                Map<String, Object> map = new HashMap<>();
                map.put("periodProdId", entity.getPeriodProdId());
                map.put("periodId", entity.getPeriodId());
                List<FinFloatCalcRuleDetail> calcRuleDetail = periodicManagement002Service.selectFloatFeeList(map);
                entity.setFinFloatCalcRuleDetailList(calcRuleDetail);
                PeriodicManagement001Dto pmDto = selectInfoByfinprodId(entity.getFinprodId(), null, null);
                if (StringUtil.isNotEmpty(pmDto)) {
                    entity.setCcy(pmDto.getCcy());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (entity.getStartDate() == null) {
                entity.setStartDate(iPlatformDate.getSysDate());
            }
            model.addAttribute("entity", entity);
        }

        return super.init(model);
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto    实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) PeriodicManagement001Dto dto, BindingResult result) {
        if (!(result.getErrorCount() > 0)) {
            String str = "";
            if (dto.getStartDate() != null
                    && dateCalService.isDealCalendarHoliday(dto.getStartDate(), "01")) {
                str = "期次开始日/期次结束日为节假日，是否继续";
            }

            if (dto.getEndDate() != null
                    && dateCalService.isDealCalendarHoliday(dto.getEndDate(), "01")) {
                str = "期次开始日/期次结束日为节假日，是否继续";
            }

            if (StringUtil.equals(dto.getOperateType(), "new") && periodicManagement002Service.checkPeriodTradeDate(dto)) {
                str = "该产品该期次开始日结束日区间已存在，是否继续?";
            }

            if (StringUtil.isNotBlank(str)) {
                AjaxResult wr = new AjaxResult();
                wr.setData(str);
                return wr;
            }
        }
        return commonCheck(result, dto.getOperateType());
    }


    /**
     * 保存
     *
     * @param dto 保存对象
     * @param br  验证信息
     * @return 操作结果
     * @throws WorkFlowException 异常信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated PeriodicManagement001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if (StringUtil.isEmpty(dto.getPeriodId())) {
            //期次代码 = 期次开始日+投资周期（天）+6位序号
            String sequence_6 = CommonUtil.getSequenceIndexData(DataIndexSequenceType.期次管理, "%06d");
            String periodId = "";
            if (StringUtil.equals(dto.getTermType(), DictCodeListExt2.期限类型_多固定期限.getValue())) {
                periodId = DateUtil.format(dto.getStartDate(), "yyyyMMdd") + dto.getInvestCycle() + sequence_6;
            } else {
                periodId = DateUtil.format(dto.getStartDate(), "yyyyMMdd") + dto.getInvestCycle2() + sequence_6;
            }
            dto.setPeriodId(periodId);
        }
        if (dto.getPageModel().equals(GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            //分支序号 0代表自增无位数限制
            dto.setBranch(baseGetBussinessIdMapper.getNextValBySequenceName("QCGL_FZXH_SEQUENCE"));
        }
        ProcessParameter<PeriodicManagement001Dto> processParameter = new ProcessParameter<PeriodicManagement001Dto>();
        WorkFlowParameter<PeriodicManagement001Dto> params = new WorkFlowParameter<PeriodicManagement001Dto>();
        params.setElementId(dto.getElementId());
        params.setPageId(this.getPageId());
        params.setBusinessCode(dto.getPeriodId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_产品期次管理.getValue());
        params.setComment(
                entity -> "产品期次新建"
                        + this.getMessage("FinProductEstablish002.finprodId") + "("
                        + entity.getFinprodId() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = periodicManagement002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 查询基本信息
     *
     * @param finprodId
     * @return
     */
    @ResponseBody
    @RequestMapping("selectInfoByfinprodId")
    public PeriodicManagement001Dto selectInfoByfinprodId(String finprodId, String branch, String periodId) {
        return periodicManagement002Service.selectInfoByfinprodId(finprodId, branch, periodId);
    }

    /**
     * 查询浮动管理费列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("selectFloatFeeList")
    public List<FinFloatCalcRuleDetail> selectFloatFeeList() {
        //update by wangcheng 20191029 期次管理-新建期次浮动管理费的业绩比较基准有数据，提交期次到复核审批时，该部分数据为空
        if (StringUtil.isNotEmpty(this.getParam(GlobalConstant.WORKFLOW_ID))) {
            ProcessMark pro = periodicManagement002Service.getBusinessInfo(
                    this.getParam(GlobalConstant.WORKFLOW_ID));
            if (pro != null) {
                PeriodicManagement001Dto entity = JsonUtils.json2Object(
                        pro.getBusinessData(), PeriodicManagement001Dto.class);
                return entity.getFinFloatCalcRuleDetailList();
            }
        }
        return periodicManagement002Service.selectFloatFeeList(RequestUtil.getPageParam());
    }


    /**
     * 投资周期下拉框
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getInvestCycle")
    public Page<SelectOptionDto> getInvestCycle() {
        return periodicManagement002Service.getInvestCycle(RequestUtil.getPageParam());
    }

    /**
     * 查询期限代码
     */
    @ResponseBody
    @RequestMapping("selectPeriodProdId")
    public PeriodicManagement001Dto selectPeriodProdId(String finprodId, String investCycle) {
        return periodicManagement002Service.selectPeriodProdId(finprodId, investCycle);
    }

    /**
     * 根据期限产品代码 查询 业绩基准 浮动管理费
     *
     * @param periodProdId
     * @return
     */
    @ResponseBody
    @RequestMapping("selectAllInfo")
    public PeriodicManagement001Dto selectAllInfo(String periodProdId) {
        //增加参数 解决 BSTOT-689 期次确认界面，浮动管理费部分字段没有联动出来；
        String branch = this.getParam("branch");
        String termType = this.getParam("termType");
        return periodicManagement002Service.selectAllInfo(periodProdId, branch, termType);
    }
}

