package com.joyintech.tams.invest.basicdata.stlsecreconpara.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.basicdata.stlsecreconpara.dto.StlSecReconParaDto;
import com.joyintech.tams.invest.basicdata.stlsecreconpara.service.StlSecReconParaService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对账参数设置- 查询画面
 */
@Controller
@PageId("StlSecReconPara001")
@RequestMapping("/invest/basicdata/stlsecreconpara/StlSecReconPara001")
public class StlSecReconPara001Controller extends BaseController {

    /**
     * 对账参数设置 - service
     */
    @Autowired
    private StlSecReconParaService stlSecReconPara001Service;

    /**
     * 查询页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        setSelectOptionToModel(model, new DictCode[]{
            DictCode.参数代码, DictCode.对账维度, DictCode.佣金对账方式
        });
        model.addAttribute("RECON_PARA_CODE_01", DictCodeListStl.参数代码_误差值.getValue());
        model.addAttribute("RECON_PARA_CODE_02", DictCodeListStl.参数代码_对账维度.getValue());
        model.addAttribute("RECON_PARA_CODE_03", DictCodeListStl.参数代码_佣金对账方式.getValue());
        return super.init(model);
    }

    /**
     * 删除按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) StlSecReconParaDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 删除
     * @param dto
     * @param br
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
            ValidateGroupDelete.class}) StlSecReconParaDto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<StlSecReconParaDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<StlSecReconParaDto> params = new WorkFlowParameter<>();
        params.setPageId("StlSecReconPara002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getSeqNo());
        params.setBusinessType(DictCodeListExt3.共通业务类型_券商对账参数设置.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> entity.getParaCodeStr() + ":" + entity.getParaValueStr());
        processParameter.setWorkFlowParameter(params);
        String msg = stlSecReconPara001Service.start(processParameter,
                dto.getSelectedItem().toArray(new StlSecReconParaDto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
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
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export() throws Exception {
        return stlSecReconPara001Service.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));
    }

    /**
     * 分页查询
     * @return
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<StlSecReconParaDto> findPage() {
        return stlSecReconPara001Service.findPage(RequestUtil.getPageParam());
    }

    /**
     * 查询所有券商ID
     * @return
     */
    @ResponseBody
    @RequestMapping("findAllBrokerIds")
    public List<SuggestDto> findAllBrokerIds() {
        Map<String, Object> param = new HashMap<String, Object>();
        String customerId = super.getRequest().getParameter("param");
        param.put("customerId", customerId);
        return stlSecReconPara001Service.findAllBrokerIds(param);
    }

    /**
     * 查询所有券商ID - 下拉框
     * @return
     */
    @ResponseBody
    @RequestMapping("findAllBrokerIdsSelect")
    public List<SelectOptionDto> findAllBrokerIdsSelect() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return stlSecReconPara001Service.findAllBrokerIdsSelect(param);
    }
}
