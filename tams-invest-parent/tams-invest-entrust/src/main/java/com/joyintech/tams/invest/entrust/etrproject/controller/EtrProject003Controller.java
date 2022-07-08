package com.joyintech.tams.invest.entrust.etrproject.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrproject.service.EtrProjectService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.tools.util.ValidationUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 项目类多对多指令新建
 * 〈功能详细描述〉
 * @author zhuyongkang
 * @version 1.0 2019年12月30日
 * @see EtrProject003Controller
 * @since 1.0
 */
@Controller
@PageId("EtrProject003")
@RequestMapping("/invest/entrust/etrproject/EtrProject003")
public class EtrProject003Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;

    /**
     * 计划类净值委托service
     */
    @Autowired
    private EtrProjectService etrProjectService;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.项目类一级分类);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类);
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        String operateType = getParam("operateType");
        RequestUtil.getPageParam();
        //导入
        if (StringUtil.equals(operateType,"import")) {
        String tempList = getParam("tempList");
        List<EtrProjectDto> entrustListData= etrProjectService.getEtrDoubleProjectDtoListData(tempList);
        model.addAttribute("etrProjectDtoList", StringUtil.isEmptyList(entrustListData) ? null : JsonUtils.object2Json(entrustListData));
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }

    /**
     * 保存按钮点击时的check
     *
     * @param allDto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody  EtrProjectDto allDto, BindingResult result) {
        List<EtrProjectDto> dtoList = allDto.getSelectedItem();
        List<ValidMessage> messages = new ArrayList<>();
        StringBuffer confirmMessage = new StringBuffer();
        for (int i = 0; i < dtoList.size(); i++ ) {
            EtrProjectDto dto = dtoList.get(i);
            ValidationUtil.ValidResult itemResult = ValidationUtil.validateBean(dto,
                    ValidateGroupEdit.class);

            // 可用现金软校验
            if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue()))
                    && dto.getProAvailableCash() != null && dto.getEntrustAmt() != null
                    && dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
                if (StringUtil.isNotBlank(confirmMessage)) {
                    confirmMessage.append("<br />");
                }
                confirmMessage.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                confirmMessage.append("<br />");
                confirmMessage.append(getMessage("TRD_C_0002"));
            }
            // 可用持仓软校验
            else if (StringUtil.equals(
                    CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue()))
                    && dto.getAssetAvailablePosition() != null && dto.getEntrustNum() != null
                    && dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                if (StringUtil.isNotBlank(confirmMessage)) {
                    confirmMessage.append("<br />");
                }
                confirmMessage.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                confirmMessage.append("<br />");
                confirmMessage.append(getMessage("ETR_X_019"));
            }

            if (!StringUtil.isEmptyList(itemResult.getAllErrors())) {
                StringBuffer message = new StringBuffer();
                message.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
                for (ValidationUtil.ErrorMessage errorMessage : itemResult.getAllErrors()) {
                    message.append("<br />");
                    message.append(errorMessage.getMessage());
                }
                messages.add(new ValidMessage(message.toString()));
            }

        }
        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        if (StringUtil.isNotEmpty(confirmMessage.toString())) {
            AjaxResult wr = new AjaxResult();
            wr.setData(confirmMessage.toString());
            return wr;
        }
        return commonCheck(result, dtoList.get(0).getOperateType());
    }
    /**
     *excel导入数据check
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkImportData")
    public AjaxResult checkImportData(String fileName) {
        return etrProjectService.checkImportData(fileName);
    }

    /**
     * 保存/提交
     * 
     * @param br br
     * @return 结果
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrProjectDto allDto, BindingResult br) throws Exception {
        AjaxResult ajaxResult = this.check(allDto, br);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return ajaxResult;
        }
        List<EtrProjectDto> dtoList = allDto.getSelectedItem();
        for(EtrProjectDto dto:dtoList) {
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_项目类资产投资委托);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
            dto.setWorkflowParameter("new|||1|EtrProject002|EtrProject002_btn_submit|true");
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, allDto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(allDto.getSelectedItem(),
                    DictCodeList.平安资产类型_项目类资产.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                    false, null);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        // add by zhangrui1 批量提交设置风控指标明细 start
        EntrustRdsCommonAop.riskListToChildren(allDto.getRiskTrialRetDto(), dtoList,
                (dto, list) -> dto.setRiskTrialRetDto(list));
        // add by zhangrui1 批量提交设置风控指标明细 end
        ProcessParameter<EtrProjectDto> processParameter = new ProcessParameter<EtrProjectDto>();
        WorkFlowParameter<EtrProjectDto> params = new WorkFlowParameter<EtrProjectDto>();
        params.setPageId("EtrProject002");
        params.setElementId(dtoList.get(0).getElementId());
        params.setBusinessCode(EtrProjectDto::getEntrustId);
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrProject001") + "(" + entity.getPortfolioId() + ")"
                      + "-" + this.getMessage("EtrProject001.ps") + "-" + "("
                      + this.getMessage("EtrProject001.vdate") + ":" +DateUtil.format(entity.getVdate())
                      + ")-(" + this.getMessage("EtrProject001.mdate") + ":"
                      +DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrProject001.entrustAmt") + ":"
                      + entity.getEntrustAmt());
        processParameter.setWorkFlowParameter(params);
        String msg = etrProjectService.start(processParameter, dtoList.toArray(new EtrProjectDto[dtoList.size()]));
        return new AjaxResult(msg);
    }
}
