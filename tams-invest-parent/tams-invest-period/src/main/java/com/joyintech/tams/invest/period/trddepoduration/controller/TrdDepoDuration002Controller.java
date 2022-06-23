/*
 * 文件名：TrdDepoDuration001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trddepoduration.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
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
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.period.trddepoduration.dto.TrdDepoDuration001Dto;
import com.joyintech.tams.invest.period.trddepoduration.service.TrdDepoDuration001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 存款类提前支取与到期
 * 〈功能详细描述〉
 * @author dingzhipeng
 * @version 1.0 2018年10月11日
 * @see TrdDepoDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdDepoDuration002")
@RequestMapping("/invest/period/trddepoduration/TrdDepoDuration002")
public class TrdDepoDuration002Controller extends BaseController{
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
        model.addAttribute("pACode_40",DictCodeListExt1.平安业务类型_存款到期.getValue());
        model.addAttribute("pACode_39",DictCodeListExt1.平安业务类型_存款中间付息.getValue());
        model.addAttribute("DEPO_ASSET_TYPE_01",DictCodeList.存款类资产类型_存放同业.getValue());
        setSelectOptionToModel(model, DictCode.币种,DictCode.计息基础,DictCode.平安业务类型,DictCode.存款类资产类型);
        String statusValid = (String)param.get("statusValid");
        TrdDepoDuration001Dto dto;
        String workflowId = (String)param.get(GlobalConstant.WORKFLOW_ID);
        if ((StringUtil.equals(DictCodeListExt1.画面数据状态_流程中.getValue(), statusValid)
             || !WorkFlowUtil.isNotFromToDoList())
            && StringUtil.isNotBlank(workflowId)) {
            dto = trdDepoDuration001Service.getBussinessEntityByWorkflowId(workflowId);
//            设置显示实际交易组合
            if(StringUtil.isNotEmpty(dto.getActPortfolioId())){
                dto.setPortfolioId(dto.getActPortfolioId());
            }
            if(null!=dto && StringUtil.isNotEmpty(dto.getPortfolioId())){
            	//根据组合代码，查询组合名称
            	Map<String, Object> pageParampf =new HashMap<String, Object>();
            	pageParampf.put("portfolioId", dto.getPortfolioId());
            	Map<String,Object> listpf=trdDepoDuration001Service.portfolioIdLink(pageParampf);
            	if(null!=listpf && null!=listpf.get("PORTFOLIONAME")){
            		dto.setPortfolioName(listpf.get("PORTFOLIONAME").toString());
            	}
            	
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
        model.addAttribute("tradeId",dto.getBusiCode());
        model.addAttribute("opType","draw");
        model.addAttribute("deal", JsonUtils.object2Json(dto));
        model.addAttribute(GlobalConstant.ENTITY,dto);
        model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(CollectionUtils.isNotEmpty(dto.getBussTrdTransferOrder())?dto.getBussTrdTransferOrder().get(0):""));
        model.addAttribute("FINPRODTYPE_DEFAULT",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        // add by renping 2019-10-15 添加通道费 begin
        model.addAttribute("COLLECT_TYPE_01", DictCodeListExt2.收取方式_回款前扣除.getValue());
        model.addAttribute("IS_CAL_LOADRATE", CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE));
        // add by renping 2019-10-15 添加通道费 end
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam("GEN_PAY_ORDERS_AUTO"));
        if(StringUtil.equals("draw", opType)) {
            model.addAttribute("PS_DEFAULT",DictCodeList.交易方向_提前支取.getValue());
        }
        else {
            model.addAttribute("PS_DEFAULT",dto.getPs());
        }
        return super.init(model);
    }
    //add by yujiaping 2020-12-5 BS-1562【存款类存续期】同一资产同一计息结束日只能做一次活期存放结息start
    @ResponseBody
    @RequestMapping("getTradeId")
    public AjaxResult getTradeId() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdDepoDuration001Service.getTradeId(RequestUtil.getPageParam()));
        return ar;
    }
    //add by yujiaping 2020-12-5 BS-1562【存款类存续期】同一资产同一计息结束日只能做一次活期存放结息end
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
     * 理论应计利息联动处理
     *
     * @return 联动结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("calTheoryIntDemand")
    public AjaxResult calTheoryIntDemand() {
        AjaxResult ar = new AjaxResult();
        ar.setData(trdDepoDuration001Service.calTheoryIntDemand(RequestUtil.getPageParam()));
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
