package com.joyintech.tams.invest.period.trdbondduration.controller;

import static com.joyintech.tams.invest.period.common.service.PeriodCommonService.change现金流类型To交易方向;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
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
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdbondduration.dto.TrdBondDuration002ProcessDto;
import com.joyintech.tams.invest.period.trdbondduration.service.TrdBondDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.controller
 * @Description: 债券存续期 确认
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@PageId("TrdBondDuration002")
@RequestMapping("/invest/period/trdbondduration/TrdBondDuration002")
public class TrdBondDuration002Controller extends BaseController {

    @Autowired
    private TrdBondDuration002Service trdBondDuration002Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DictCode.资产来源);
        TrdBondDuration002ProcessDto entity = null;
        Map<String, Object> params = RequestUtil.getPageParam();
        String statusValid = (String) params.get("statusValid");
        params.put("statusValid", statusValid);
        if ((!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(RequestUtil.getParam("workflowId")))
                || DictCodeListExt1.画面数据状态_流程中.getValue().equals(statusValid)) {
            String workflowId = RequestUtil.getParam("workflowId");
            if (StringUtil.isNotEmpty(workflowId)) {
                entity = trdBondDuration002Service.getBussinessEntityByWorkflowId(workflowId);
                model.addAttribute("workflowId",workflowId);
                if(entity != null) {
                    model.addAttribute("isTodo", commonBusinessService.isBackCheckNode(entity.getBusiCode()));
                }
            }
            // 资金后台打开流程中的数据，处理自动批产生的流程中的数据
            else {
                String bizCode = RequestUtil.getParam("bizCode");
                if (StringUtil.isBlank(bizCode)) {
                    bizCode = RequestUtil.getParam("busiCode");
                }
                if (StringUtil.isNotEmpty(bizCode)) {
                    params.put("bizCode", bizCode);
                    entity = trdBondDuration002Service.findEffectiveData(params);
                    if (entity != null) {
                        entity.setNeedSkipInsertWfl(true);
                        entity.setBussTrdTransferOrder(periodCommonService.selectPayOrders(entity.getBusiCode()));
                        entity.setFinAssetIntInfoList(trdBondDuration002Service.findAssetIntInfo(trdBondDuration002Service.genParams(entity)));
                    }
                }
            }
            model.addAttribute("deal", JsonUtils.object2Json(TrdBondDuration002Service.getTrdProductDeal(entity)));
        }
        else {
            entity = trdBondDuration002Service.findEffectiveData(params);
            if (entity != null) {
                if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid)) {
                    entity.setTradeAmt(entity.getFpriceAmt());
                    model.addAttribute("deal", JsonUtils.object2Json(TrdBondDuration002Service.getTrdProductDeal(entity)));
                    model.addAttribute("needInit", true);
                    entity.setTradeDate(DateUtil.format(DateUtil.platDateFromCache()));
                }
                else {
                    entity.setBussTrdTransferOrder(periodCommonService.selectPayOrders(entity.getBusiCode()));
                }
                entity.setFinAssetIntInfoList(trdBondDuration002Service.findAssetIntInfo(trdBondDuration002Service.genParams(entity)));
            }
        }
        if (entity != null) {
            model.addAttribute("ps", change现金流类型To交易方向(entity.getCashType()));
            PeriodCommonService.setEntityBussOrderToModel(model, entity.getBussTrdTransferOrder());
            model.addAttribute("tradeId",entity.getBusiCode());
        }
//       2.0处理
//        if (entity != null) {
//            entity.setStatusValid(statusValid);
//            if (entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
//                model.addAttribute("entity_bussTrdTransferOrderDto",
//                    JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
//            }
//        }
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        model.addAttribute(GlobalConstant.ENTITY, entity);
        this.setSelectOptionToModel(model, DictCode.是否, DictCode.平安划款类型,DictCode.币种,DictCode.投资目的);
        model.addAttribute("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
        model.addAttribute("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
        
        model.addAttribute("COLLECT_TYPE_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        model.addAttribute("acctNoList", JsonUtils.object2Json(periodCommonService.queryAcctList(null)));
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
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdBondDuration002ProcessDto dto,
                            BindingResult result) {
        if(result.getErrorCount() == 0) {
            //如果是交易所债券，则不提示
            if(dto.getTradeDate().compareTo(dto.getmDate()) < 0 && !(DictCodeListExt1.交易场所_银行间.getValue().equals(dto.getTradeMarket()))){
                AjaxResult ar = new AjaxResult();
                ar.setData("请确认本次付息截止日前不会再发生买卖或还本交易，请确认是否继续？");
                return ar;
            }
        }
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
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdBondDuration002ProcessDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdBondDuration002ProcessDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondDuration002ProcessDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setBusiCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产存续期.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(trdBondDuration002Service.生成业务说明());
        processParameter.setWorkFlowParameter(params);
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
//            trdBondDuration002Service.genDataForInsertDto(dto);
//        	2.0
        	dto.setBaseTotalTradeInfoDto(trdBondDuration002Service.genFinProductTradeDto2(dto));
        }
        // 将附件和当前业务对象绑定（更新附件的业务代码）
        fileAttachmentProcess.updateBussinessNo(
                dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                        Collectors.toList()),
                dto.getBusiCode());
        String msg = trdBondDuration002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

}
