/*
 * 文件名：TrdDepoDuration004Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trddepoduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.period.trddepoduration.dto.TrdDepoDuration001Dto;
import com.joyintech.tams.invest.period.trddepoduration.service.TrdDepoDuration001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
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

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活期存放结息
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年12月11日
 * @see TrdDepoDuration004Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepoDuration004")
@RequestMapping("/invest/period/trddepoduration/TrdDepoDuration004")
public class TrdDepoDuration004Controller extends BaseController {
    /**
     * service
     */
    @Autowired
    private TrdDepoDuration001Service trdDepoDuration001Service;

    @Autowired
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        Map<String,Object>param = RequestUtil.getPageParam();
        String opType = (String)param.get("opType");
        model.addAttribute("pACode_10", DictCodeListExt1.平安业务类型_存款提前支取.getValue());
        model.addAttribute("pACode_40", DictCodeListExt1.平安业务类型_存款到期.getValue());
        model.addAttribute("pACode_39", DictCodeListExt1.平安业务类型_存款中间付息.getValue());
        model.addAttribute("DEPO_ASSET_TYPE_03", DictCodeListExt3.存款类资产类型_活期存放.getValue());
        setSelectOptionToModel(model, DictCode.币种, DictCode.计息基础, DictCode.平安业务类型, DictCode.存款类资产类型, DictCode.频率, DictCode.日期号, DictCode.月, DictCode.结息方式);
        String statusValid = (String)param.get("statusValid");
        Date sysDate = DateUtil.platDateFromCache();
        String formatDate = DateUtil.format(sysDate);
        model.addAttribute("formatDate",formatDate);
        model.addAttribute("formatDate_1",DateUtil.format(DateUtil.addDay(sysDate,-1)));
        model.addAttribute("opType",opType);
        TrdDepoDuration001Dto dto;
        String workflowId = (String)param.get(GlobalConstant.WORKFLOW_ID);
        if ((StringUtil.equals(DictCodeListExt1.画面数据状态_流程中.getValue(), statusValid)
                || !WorkFlowUtil.isNotFromToDoList())
                && StringUtil.isNotBlank(workflowId)) {
            dto = trdDepoDuration001Service.getBussinessEntityByWorkflowId(workflowId);
//            设置显示实际交易组合
            model.addAttribute("opType",dto.getOpType());
            if(StringUtil.isNotEmpty(dto.getActPortfolioId())){
                dto.setPortfolioId(dto.getActPortfolioId());
            }
        }
        else if(StringUtil.isNotEmpty(statusValid)){
            dto = trdDepoDuration001Service.findEffectiveData(param);
            //            设置显示实际交易组合
            if(StringUtil.isNotEmpty(dto.getActPortfolioId())){
                dto.setPortfolioId(dto.getActPortfolioId());
            }
        }
        else {
            dto = new TrdDepoDuration001Dto();
        }
        dto.setCashType(DictCodeListExt1.平安业务类型_存款中间付息.getValue());
        model.addAttribute("tradeId",dto.getBusiCode());
        model.addAttribute("deal", JsonUtils.object2Json(dto));
        model.addAttribute(GlobalConstant.ENTITY,dto);
        model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(CollectionUtils.isNotEmpty(dto.getBussTrdTransferOrder())?dto.getBussTrdTransferOrder().get(0):""));
        model.addAttribute("FINPRODTYPE_DEFAULT", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("PS_T22", DictCodeList.交易方向_收息到账.getValue());
        model.addAttribute("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        model.addAttribute("INT_SETT_TYPE_01", DictCodeListExt3.结息方式_结转为本金.getValue());
        model.addAttribute("INT_SETT_TYPE_02", DictCodeListExt3.结息方式_现金结息.getValue());
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
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
        ar.setData(trdDepoDuration001Service.finprodMarketIdLink(RequestUtil.getPageParam()));
        return ar;
    }
    /**
     * 组合代码联动处理
     *
     * @return 联动结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("portfolioIdLink")
    public AjaxResult portfolioIdLink() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdDepoDuration001Service.portfolioIdLink(RequestUtil.getPageParam()));
        return ar;
    }
    /**
     * 组合代码联动处理
     *  add by jiangpeng on 20201215 ZGBS-69
     * @return 联动结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioName")
    public AjaxResult getPortfolioName() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdDepoDuration001Service.getPortfolioName(RequestUtil.getPageParam()));
        return ar;
    }
    /**
     * 剩余本金联动处理
     *
     * @return 联动结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("calResiPrincipal")
    public AjaxResult calResiPrincipal() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdDepoDuration001Service.calResiPrincipal(RequestUtil.getPageParam()));
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
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdDepoDuration001Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 保存
     *
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdDepoDuration001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdDepoDuration001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdDepoDuration001Dto> params = new WorkFlowParameter<>();
        params.setPageId(dto.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setBusiCode(baseGetBusinessId.buildBussinessCode(parameter));
        dto.setTotleTradeId(dto.getBusiCode());
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产存续期.getValue());

        if (CollectionUtils.isNotEmpty(dto.getUploadFileList())
                && StringUtil.isNotEmpty(dto.getBusiCode())){
            fileAttachmentProcess.updateBussinessNo(
                    dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    dto.getBusiCode());
        }
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment(
                this.getMessage("TrdDepoDuration001.finprodName"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodMarketName()
                        + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdDepoDuration001.assetType"),
                GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(
                        DictCode.存款类资产类型, entity.getAssetType()) + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdDepoDuration001.busiCode"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getBusiCode()
                        + GlobalConstant.CLOSE_PARENTHESIS));
        processParameter.setWorkFlowParameter(params);
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType())) {
            dto.setBaseTotalTradeInfoDto(trdDepoDuration001Service.genFinProductTradeDto(dto));
        }
        String msg = trdDepoDuration001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
