package com.joyintech.tams.invest.period.trdprojduration.controller;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.change现金流类型To交易方向;
import static com.joyintech.tams.invest.period.trdprojduration.service.TrdProjectDuration002Service.genParams;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.workflow.engine.exception.WorkFlowException;
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
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdprojduration.dto.TrdProjectDurationProcessDto;
import com.joyintech.tams.invest.period.trdprojduration.service.TrdProjectDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.controller
 * @Description: 项目类资产 存续期确认
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@PageId("TrdProjectDuration002")
@RequestMapping("/invest/period/trdprojduration/TrdProjectDuration002")
public class TrdProjectDuration002Controller extends BaseController {

    @Autowired
    private TrdProjectDuration002Service trdProjectDuration002Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private IPlatformDate platformDateImpl;
    /**
     * BussTrdTransferOrderService
     */
    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderService;
    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.币种, DictCode.是否, DictCode.资产来源,DictCode.投资目的};

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        TrdProjectDurationProcessDto entity = null;
        String statusValid = RequestUtil.getParam("statusValid");
        if ((!WorkFlowUtil.isNotFromToDoList() || DictCodeListExt1.画面数据状态_流程中.getValue().equals(statusValid))
        	&&StringUtil.isNotEmpty(RequestUtil.getParam("workflowId"))) {
            String workflowId = RequestUtil.getParam("workflowId");
            if (StringUtil.isNotEmpty(workflowId)) {
                model.addAttribute("workflowId",workflowId);
                entity = trdProjectDuration002Service.getBussinessEntityByWorkflowId(workflowId);
                if(entity != null) {
                    model.addAttribute("isTodo", commonBusinessService.isBackCheckNode(entity.getBusiCode()));
                }
            }
            // 资金后台打开流程中的数据，处理自动批产生的流程中的数据
            else {
                String bizCode = RequestUtil.getParam("bizCode");
                if (StringUtil.isNotEmpty(bizCode)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("busiCode", bizCode);
                    params.put("statusValid", statusValid);
                    entity = trdProjectDuration002Service.findEffectiveData(RequestUtil.getPageParam());
                    if (entity != null) {
                        entity.setNeedSkipInsertWfl(true);
                        entity.setBussTrdTransferOrder(periodCommonService.transformOrderDtoBack(periodCommonService.selectPayOrders(entity.getBusiCode())));
                        entity.setFinAssetIntInfoList(trdProjectDuration002Service.findAssetIntInfo(genParams(entity)));
                    }
                }
            }
        }
        else {
            Map<String, Object> params = new HashMap<>();
            params.putAll(RequestUtil.getPageParam());
            if (DictCodeListExt1.画面数据状态_撤销.getValue().equals(statusValid)) {
                String bizCode = RequestUtil.getParam("bizCode");
                params.put("bizCode", bizCode + "R");
            }
            entity = trdProjectDuration002Service.findEffectiveData(RequestUtil.getPageParam());
            entity.setCashType("02");
            if (entity != null) {
                entity.setStatusValid(statusValid);
                if (!DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid) && entity != null) {
                    entity.setBussTrdTransferOrder(periodCommonService.transformOrderDtoBack(periodCommonService.selectPayOrders(entity.getBusiCode())));
                } else {
                    entity.setArrivalDate(platformDateImpl.getSysDateStr());
                    entity.setTheoryAmt(ArithmeticUtil.doAdd(entity.getIntAmtTheory(), entity.getReturnAmount()));
                }
                entity.setFinAssetIntInfoList(trdProjectDuration002Service.findAssetIntInfo(genParams(entity)));
                if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid) && entity != null) {
                    if (entity.getFinAssetIntInfoList() != null && entity.getFinAssetIntInfoList().size() > 0) {
                        BigDecimal payUnpaidintAmt = BigDecimal.ZERO;//累计还本未付息金额
                        for (FinAssetIntInfo info : entity.getFinAssetIntInfoList()) {
                            payUnpaidintAmt = payUnpaidintAmt.add(info.getRepaymentUnpayInterest());//区间未付还本金额
                            info.setFpriceAmt(info.getTheoryAmt().add(info.getRepaymentUnpayInterest()));//实际本息总额=理论本息总额+区间未付利息
                        }
                        entity.setPayUnpaidintAmt(payUnpaidintAmt);
                        if (entity.getFpriceAmt() != null) {
                            entity.setFpriceAmt(entity.getFpriceAmt().add(payUnpaidintAmt));
                        }
                    }
                }
            }
        }
        if (entity != null) {
            model.addAttribute("ps", change现金流类型To交易方向(entity.getCashType()));
            PeriodCommonService.setEntityBussOrderToModel(model, periodCommonService.transformOrderDto(entity.getBussTrdTransferOrder()));
        }
        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("F17", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("statusValid", statusValid);
        model.addAttribute("statusValid06", DictCodeListExt1.画面数据状态_撤销.getValue());
        model.addAttribute("COLLECT_TYPE_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
     // 一体化对应etliu start
//        setSelectOptionToModel(model, DictCode.是否, DictCode.币种, DictCode.划款类型);
        setSelectOptionToModel(model, DictCode.是否, DictCode.币种, DictCode.平安划款类型);
     // 一体化对应etliu end
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute("acctNoList", JsonUtils.object2Json(bussTrdTransferOrderService.queryAcctList(null)));
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        return super.init(model);
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto 实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectDurationProcessDto dto,
                            BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     *
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectDurationProcessDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdProjectDurationProcessDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdProjectDurationProcessDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setBusiCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产存续期.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(trdProjectDuration002Service.生成业务说明());
        processParameter.setWorkFlowParameter(params);
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
            dto.setBaseTotalTradeInfoDto(trdProjectDuration002Service.genFinProductTradeDto(dto));
        }
        String msg = trdProjectDuration002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    /**
     *  资产是否还本带息，若还本带息，还本日是否等于付息日
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("isPayIntDate")
    public Boolean isPayIntDate(@RequestBody Map<String,Object> map) {
        return trdProjectDuration002Service.isPayIntDate(map);
    }
}
