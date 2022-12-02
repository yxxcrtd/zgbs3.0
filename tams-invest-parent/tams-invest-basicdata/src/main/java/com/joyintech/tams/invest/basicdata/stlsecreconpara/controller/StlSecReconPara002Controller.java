package com.joyintech.tams.invest.basicdata.stlsecreconpara.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.basicdata.stlsecreconpara.dto.StlSecReconParaDto;
import com.joyintech.tams.invest.basicdata.stlsecreconpara.service.StlSecReconParaService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.joyintech.tams.constant.DictCodeListStl.*;

@Controller
@PageId("StlSecReconPara002")
@RequestMapping("/invest/basicdata/stlsecreconpara/StlSecReconPara002")
public class StlSecReconPara002Controller extends BaseController {
    /**
     * 对账参数设置 - service
     */
    @Autowired
    private StlSecReconParaService stlSecReconPara001Service;

    // 平台日期接口
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 查询页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        setSelectOptionToModel(model, new DictCode[]{
                DictCode.参数代码, DictCode.对账维度, DictCode.佣金对账方式
        });
        // 获取流程ID
        String workflowId = getParam("workflowId");

        // 主键
        String seqNo = getParam("seqNo");
        StlSecReconParaDto entity = null;
        // 不来自于代办
        if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(seqNo)) {
            // 通过主键查询记录
            entity = stlSecReconPara001Service.findEntityByPrimarykey(seqNo);
        }
        // 来自代办
        else if (StringUtil.isNotEmpty(workflowId)) {
            entity = stlSecReconPara001Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("workflowId", workflowId);
        }
        // 生成一个ID
        if (entity == null) {
            entity = new StlSecReconParaDto();
            entity.setSeqNo(IDGenerator.generate());
        }
        // 平台日
        model.addAttribute("iPlatformDate", iPlatformDate.getSysDate());
        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute("RECON_PARA_CODE_01", 参数代码_误差值.getValue());
        model.addAttribute("RECON_PARA_CODE_02", 参数代码_对账维度.getValue());
        model.addAttribute("RECON_PARA_CODE_03", 参数代码_佣金对账方式.getValue());
        return super.init(model);
    }

    /**
     * 保存时check数据
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ ValidateGroupEdit.class }) StlSecReconParaDto dto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return new AjaxResult(result);
        }
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存按钮点击
     *
     * @param dto dto
     * @param result BindingResult
     * @throws Exception Exception
     * @return check结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings("deprecation")
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) StlSecReconParaDto dto, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            return new AjaxResult();
        }

        ProcessParameter<StlSecReconParaDto> processParameter =
                new ProcessParameter<>();
        WorkFlowParameter<StlSecReconParaDto> params =
                new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());

        // 主键
        if (StringUtil.isEmpty(dto.getSeqNo())) {
            dto.setSeqNo(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getSeqNo());
        params.setBusinessType(DictCodeListExt3.共通业务类型_券商对账参数设置.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> entity.getParaCodeStr() + ":" + entity.getParaValueStr());
        processParameter.setWorkFlowParameter(params);
        String msg = stlSecReconPara001Service.start (processParameter,dto);
        return new AjaxResult(msg);
    }
}
