package com.joyintech.tams.invest.period.trdprojnetduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdprojnetduration.dto.TrdProjectNetDurationDto;
import com.joyintech.tams.invest.period.trdprojnetduration.service.TrdProjectNetDuration001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@PageId("TrdProjectNetDuration002")
@RequestMapping("/invest/period/trdprojnetduration/TrdProjectNetDuration002")
@Controller
public class TrdProjectNetDuration002Controller extends BaseController {
    /**
     * 基金存续期service
     */
    @Autowired
    private TrdProjectNetDuration001Service trdProjectNetDuration001Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private PeriodCommonService periodCommonService;

    @Autowired
    private PlatformDate platformDate;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        // 获取画面所有参数
        Map<String, Object> param = RequestUtil.getPageParam();
        // 获取业务类型
        String type = (String)param.get("type");
        String workflowId = (String)param.get(GlobalConstant.WORKFLOW_ID);
        // 交易人
        String trader = SecurityUtil.getLoginUser().getUserName();
        setSelectOptionToModel(model, DictCode.分红方式, DictCode.是否, DictCode.平安划款类型, DictCode.币种, DictCode.是否);
        model.addAttribute("FINPRODTYPE_DEFAULT", DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        // 现金分红用
        model.addAttribute("PS_T11", DictCodeList.交易方向_现金分红.getValue());
        model.addAttribute("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        model.addAttribute("PS_T13", DictCodeList.交易方向_除权登记.getValue());
        model.addAttribute("BONUS_MODE_02", DictCodeList.分红方式_红利再投.getValue());
        model.addAttribute("BONUS_MODE_01", DictCodeList.分红方式_现金分红.getValue());
        String statusValid = (String)param.get("statusValid");
        TrdProjectNetDurationDto dto = null;
        // 资金后台
        if (StringUtil.equals(DictCodeListExt1.画面数据状态_待处理.getValue(), statusValid)) {
            dto = trdProjectNetDuration001Service.findCashDividend(param);
            // 处理类型及交易员
            type = dto.getType();
            trader = dto.getTrader();
            model.addAttribute("fromCheck", true);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("busiCode", dto.getTotleTradeId());
            pageParam.put("statusValid", DictCodeListExt1.画面数据状态_流程中.getValue());
            dto.setBussTrdTransferOrder(periodCommonService.selectPayOrders(dto.getTotleTradeId()));
            // 查询利息信息
            dto.setFinAssetIntInfoList(trdProjectNetDuration001Service.findAssetIntInfo(pageParam));
        }
        else if (StringUtil.equals(DictCodeListExt1.画面数据状态_流程中.getValue(), statusValid) || StringUtil.isNotEmpty(workflowId)) {
            if (StringUtil.isNotEmpty(workflowId)) {
                dto = trdProjectNetDuration001Service.getBussinessEntityByWorkflowId(workflowId);
                model.addAttribute("workflowId", workflowId);
            }
            else {
                dto = trdProjectNetDuration001Service.findCashDividend(param);
                Map<String, Object> pageParam = new HashMap<>();
                pageParam.put("busiCode", dto.getTotleTradeId());
                pageParam.put("statusValid", statusValid);
                pageParam.put("chlId", dto.getChlId());
                // 查询利息信息
                dto.setFinAssetIntInfoList(trdProjectNetDuration001Service.findAssetIntInfo(pageParam));
                dto.setBussTrdTransferOrder(periodCommonService.selectPayOrders(dto.getTotleTradeId()));
            }
            trader = dto.getTrader();
            type = dto.getType();
        }
        else if (StringUtil.isNotEmpty(statusValid)) {
            dto = trdProjectNetDuration001Service.findEffectiveData(param);
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("busiCode", dto.getTotleTradeId());
            pageParam.put("statusValid", statusValid);
            pageParam.put("chlId", dto.getChlId());
            // 查询利息信息
            dto.setFinAssetIntInfoList(trdProjectNetDuration001Service.findAssetIntInfo(pageParam));
            dto.setBussTrdTransferOrder(periodCommonService.selectPayOrders(dto.getTotleTradeId()));
            type = StringUtil.isNotEmpty(type) ? type : dto.getType();
            trader = dto.getTrader();
            if ("04".equals(type)) {
                // 单位净值、分红份额重新设置
                BigDecimal unitPrice = periodCommonService.getLatestValuation(dto.getFinprodId(),
                        DateUtil.format(dto.getExRightsDate()));
                dto.setQty(ArithmeticUtil.divide(dto.getTradeAmt(), unitPrice, 4));
            }
        }
        else {
            dto = new TrdProjectNetDurationDto();
        }
        model.addAttribute("type", type);
        model.addAttribute("trader", trader);
        if (dto.getTotleTradeId() != null && dto.getTotleTradeId().endsWith("R")){
            model.addAttribute("busiCode", dto.getTotleTradeId().substring(0,dto.getTotleTradeId().length()-1));
        }else {
            model.addAttribute("busiCode", dto.getTotleTradeId());
        }
        model.addAttribute("acctNoList", JsonUtils.object2Json(periodCommonService.queryAcctList(null)));
        model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(
                CollectionUtils.isNotEmpty(dto.getBussTrdTransferOrder()) ? dto.getBussTrdTransferOrder().get(0) : ""));
        // 画面的业务类型，用于判断画面处于何种模式
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        model.addAttribute(GlobalConstant.ENTITY, dto);
        model.addAttribute("defaultDate",platformDate.getSysDate());
        return super.init(model);
    }

    /**
     * 资产代码联动处理
     *
     * @return 联动结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("finprodMarketIdLink")
    public AjaxResult finprodMarketIdLink() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdProjectNetDuration001Service.finprodMarketIdLink(RequestUtil.getPageParam()));
        return ar;
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
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectNetDurationDto dto,
                            BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 获取利息明细信息 <功能详细描述>
     *
     * @return 查询到的结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findAssetIntInfo")
    public AjaxResult findAssetIntInfo() {
        AjaxResult ar = new AjaxResult();
        List<FinAssetIntInfo> assetIntInfo = trdProjectNetDuration001Service.findAssetIntInfo(RequestUtil.getPageParam());
        ar.setData(assetIntInfo);
        return ar;
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
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdProjectNetDurationDto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdProjectNetDurationDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdProjectNetDurationDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        if (StringUtil.isEmpty(dto.getbTradeId())) {
            // 为非货币现金分红及非货币红利再投时 设置前置交易编号
            if (StringUtil.equals(dto.getType(), "03") || StringUtil.equals(dto.getType(), "04")) {
                dto.setbTradeId(dto.getTotleTradeId());
            }
            dto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(parameter));
        }
        else {
            periodCommonService.deleteWflByTradeId(dto.getTotleTradeId());
        }
        params.setBusinessCode(dto.getTotleTradeId());
        params.setBusinessType(DictCodeListExt3.共通业务类型_权益类资产存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment(this.getMessage("TrdDepoDuration001.finprodName") +
                        GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodMarketName() + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdPlanNetDuration001.assetType") +
                        GlobalConstant.OPEN_PARENTHESIS +
                        SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, DictCodeListExt3.平安资产类型_权益类资产.getValue())
                        + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdPlanNetDuration001.tradeId") +
                        GlobalConstant.OPEN_PARENTHESIS + entity.getTotleTradeId() + GlobalConstant.CLOSE_PARENTHESIS));
        processParameter.setWorkFlowParameter(params);
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
            dto.setBaseTotalTradeInfoDto(trdProjectNetDuration001Service.genFinProductTradeDto(dto));
        }
        String msg = trdProjectNetDuration001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
