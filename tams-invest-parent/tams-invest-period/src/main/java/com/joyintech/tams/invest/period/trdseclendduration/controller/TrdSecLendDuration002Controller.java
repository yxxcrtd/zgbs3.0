package com.joyintech.tams.invest.period.trdseclendduration.controller;

import com.joyintech.framework.exception.MessageException;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendDuration002Dto;
import com.joyintech.tams.invest.period.trdseclendduration.service.TrdSecLendDuration002Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdrepoduration.dto.TrdRepoDuration002Dto;
import com.joyintech.tams.invest.period.trdrepoduration.service.TrdRepoDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * @author : 裴家俊
 * @version V1.0 债券借贷存续期 确认
 * @date 2019年07月1日
 */
@Controller
@PageId("TrdSecLendDuration002")
@RequestMapping("/invest/period/trdseclendduration/TrdSecLendDuration002")
public class TrdSecLendDuration002Controller extends BaseController {

    /**
     * TrdSecLendDuration002Service
     */
    @Autowired
    private TrdSecLendDuration002Service trdSecLendDuration002Service;

    /**
     * 编号生成
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    @Autowired
    private PeriodCommonService periodCommonService;

    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate platformDateImpl;

    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] { DictCode.回购拆借方向, DictCode.交易场所,
        DictCode.有效状态, DictCode.争议解决方式, DictCode.币种,
        DictCode.结算方式_债券借贷, DictCode.期限品种_债券借贷, DictCode.清算速度,DictCode.是否,DictCode.投资目的};

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        String isTodoList = RequestUtil.getParam("isTodoList");
        TrdSecLendDuration002Dto entity;
        String workflowId = RequestUtil.getParam("workflowId");
        if (StringUtil.isNotEmpty(isTodoList) && StringUtil.equals(isTodoList, "true") || StringUtil.isNotEmpty(workflowId)) {
            entity = trdSecLendDuration002Service.getBussinessEntityByWorkflowId(workflowId);
        }
        else {
            entity = trdSecLendDuration002Service.findEffectiveData(RequestUtil.getPageParam());
            String statusValid = RequestUtil.getParam("statusValid");
            if (entity != null) {
                entity.setStatusValid(statusValid);
                if (DictCodeListExt1.画面数据状态_待处理.getValue().equals(statusValid) && entity != null) {
                    //entity.setSettleDate(platformDateImpl.getSysDateStr());
                    //entity.setTradeAmt(entity.getTheoryAmt());
                }
                else {
                    entity.setBussTrdTransferOrder(periodCommonService.selectPayOrders(entity.getBusiCode()));
                }
            }
        }
        TrdProductDeal deal = trdSecLendDuration002Service.getTrdProductDeal(entity);
        deal.setTradeId(StringUtil.EMPTY);
        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute("deal", JsonUtils.object2Json(deal));
        model.addAttribute("isTodoList", isTodoList);
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        if (entity != null && entity.getBussTrdTransferOrder() != null && !entity.getBussTrdTransferOrder().isEmpty()) {
            model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        return super.init(model);
    }

    /**
     * 检索债券质押信息
     *
     * @return 债券质押信息
     */
    @ResponseBody
    @RequestMapping("findRepoInfo")
    public Page<RepoInfo> findRepoInfo() {
        return trdSecLendDuration002Service.findRepoInfo();
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
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) TrdSecLendDuration002Dto dto, BindingResult result) {
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
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) TrdSecLendDuration002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdSecLendDuration002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdSecLendDuration002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();

        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setBusiCode(baseGetBusinessIdImpl.buildBussinessCode(parameter));
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeListExt2.共通业务类型_债券借贷存续期.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment(
            "交易编号:" + entity.getBusiCode() + "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型,
                DictCodeListExt2.共通业务类型_债券借贷存续期.getValue()) + ")"));
        processParameter.setWorkFlowParameter(params);
        trdSecLendDuration002Service.prepareEntity(dto);
        String msg = trdSecLendDuration002Service.start(processParameter, dto);
        return new AjaxResult(msg);
//        try{
//            String msg = trdSecLendDuration002Service.start(processParameter, dto);
//            return new AjaxResult(msg);
//        }catch (MessageException e){
//            AjaxResult m =new AjaxResult(e.getMessage(),e.getMessageType());
//            return m ;
//        }
    }

}
