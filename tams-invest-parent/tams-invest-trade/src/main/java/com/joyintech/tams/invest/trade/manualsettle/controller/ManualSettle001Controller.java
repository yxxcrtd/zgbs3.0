package com.joyintech.tams.invest.trade.manualsettle.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.tams.invest.trade.manualsettle.dto.ManualSettleFlowDto;
import com.joyintech.tams.invest.trade.manualsettle.service.ManualSettleService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * @author zhangnian
 * @ClassName ManualSettle001Controller
 * @description TODO
 * @date 2020/10/21 0021 15:18
 **/
@Controller
@PageId("ManualSettle001")
@RequestMapping("/invest/trade/manualsettle/ManualSettle001")
public class ManualSettle001Controller extends BaseController {

    private static final IDictCode[] DICT_CODES = new DictCode[]{DictCode.交易场所, DictCode.交收状态, DictCode.交收业务类别};
    @Autowired
    private ManualSettleService manualSettleService;

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("DELIVERY_STATUS_01", DictCodeListExt3.交收状态_未交收.getValue());
        model.addAttribute("DELIVERY_STATUS_02", DictCodeListExt3.交收状态_已交收.getValue());
        model.addAttribute("DELIVERY_STATUS_03", DictCodeListExt3.交收状态_交收延期.getValue());
        model.addAttribute("DELIVERY_STATUS_04", DictCodeListExt3.交收状态_交收失败.getValue());
        return super.init(model);
    }

    @ResponseBody
    @RequestMapping("getIPlatformDate")
    public String getIPlatformDate() {
        Date platformDate = DateUtil.platDateFromCache();
        return DateUtil.format(platformDate);
    }

    @ResponseBody
    @RequestMapping("findPage")
    public Page<ManualSettleDto> select()
            throws Exception {
        Page<ManualSettleDto> page = manualSettleService.selectForPage(RequestUtil.getPageParam());
        return page;
    }

    /**
     * 交收流水
     *
     * @param * @param
     * @return com.joyintech.framework.grid.Page<com.joyintech.tams.invest.settle.manualsettle.dto.ManualSettleDto>
     * @author zhangnian
     * @date 2020/10/22 0022
     */
    @ResponseBody
    @RequestMapping("queryForFlowPage")
    public List<ManualSettleFlowDto> selectTransDetailPage() throws Exception {
        return manualSettleService.queryForFlowPage(RequestUtil.getPageParam());
    }

    @ResponseBody
    @RequestMapping("settle")
    public AjaxResult settle(@RequestBody @Validated({
            ValidateGroupDelete.class}) ManualSettleDto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<ManualSettleDto> processParameter = new ProcessParameter<ManualSettleDto>();
        WorkFlowParameter<ManualSettleDto> params = new WorkFlowParameter<ManualSettleDto>();
        params.setPageId("ManualSettle001");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getSettleSeqNo());
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("FinBondTargetAsset002.finprodId") +
                    GlobalConstant.OPEN_PARENTHESIS + GlobalConstant.CLOSE_PARENTHESIS +
                    getMessage("FinBondTargetAsset002.finprodName") + GlobalConstant.OPEN_PARENTHESIS +
                    GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = manualSettleService.start(processParameter,
                dto.getSelectedItem().toArray(
                        new ManualSettleDto[dto.getSelectedItem().size()]));
        return new AjaxResult("处理完成");
    }

    /**
     * 文件导出 <功能详细描述>
     *
     * @return 导出返回参数
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
            throws Exception {
        return manualSettleService.export(this.getPageId(), RequestUtil.getParameters(getRequest()));
    }
} 

