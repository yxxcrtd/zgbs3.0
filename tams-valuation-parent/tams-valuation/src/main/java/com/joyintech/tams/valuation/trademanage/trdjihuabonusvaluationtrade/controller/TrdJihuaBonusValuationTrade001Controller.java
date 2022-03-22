/*
 * 文件名：TrdPlanRateInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.service.TrdJihuaBonusValuationTrade001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:计划类资产存续期查询
 * @auther: yuzj
 * @date: 2022年02月14日
 * @param:
 * @return:
 */
@Controller
@PageId("TrdJihuaBonusValuationTrade001")
@RequestMapping("/valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001")
public class TrdJihuaBonusValuationTrade001Controller extends BaseController {
    /**
     * 注入基金service001
     */
    @Autowired
    private TrdJihuaBonusValuationTrade001Service trdJihuaBonusValuationTrade001Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 初始化方法
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.交易场所);
        return super.init(model);
    }

    /**
     * 产品代码下拉框
     *
     * @return 产品代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodId")
    public List<SelectOptionDto> getFinprodId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodType","F16");
        return commonBusinessService.getProdName(param);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public List<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("assetType","F17");
        param.put("profitType","01");
        return commonBusinessService.getAssetId(param);
    }

    /**
     * @Description:页面查询，表格分页
     * @auther: yuzj
     * @date: 2021/12/30
     * @param:
     * @return:
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdJihuaBonusValuationTradeDto> findPage()
            throws Exception {
        Page<TrdJihuaBonusValuationTradeDto> page = trdJihuaBonusValuationTrade001Service.selectForPage(
                RequestUtil.getParameters(getRequest()));
        return page;
    }

    /**
     * 导出数据
     *
     * @return ResponseEntity<byte[]>
     * @throws Exception ExportUtil.export
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws IOException {
        String datas = this.getParam("datas");
        List<TrdJihuaBonusValuationTradeDto> listdto;
        if (datas == null) {
            listdto = trdJihuaBonusValuationTrade001Service.findAll(RequestUtil.getPageParam());
        }
        else {
            listdto = JsonUtils.json2List(datas, TrdJihuaBonusValuationTradeDto.class);
        }
        File outFile = trdJihuaBonusValuationTrade001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdjihuabonusvaluationtrade/conf/export.xml",
                listdto);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(new Date())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);
    }

    /**
     * 删除前判断
     *
     * @param dto
     * @param result
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Valid TrdJihuaBonusValuationTradeDto dto,
                            BindingResult result) throws ParseException {

        List<TrdJihuaBonusValuationTradeDto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdJihuaBonusValuationTradeDto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdJihuaBonusValuationTrade001Service.checkBusinessDataInFlow(item)) {
                    //流程存在情况
                    return new AjaxResult("交易编号：" + item.getTradeId() + "已存在流程中", MessageType.错误消息);
                }
            }
        }else if("edit".equals(dto.getOperateType())){
            for (TrdJihuaBonusValuationTradeDto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if(trdJihuaBonusValuationTrade001Service.checkBusinessDataInFlow(item)){
                    //流程存在情况
                    return new AjaxResult("交易编号："+item.getTradeId()+"已存在流程中", MessageType.错误消息);
                }
            }
        }
        return new AjaxResult();
    }

    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdJihuaBonusValuationTradeDto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdJihuaBonusValuationTradeDto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdJihuaBonusValuationTradeDto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_存续期.getValue());
        // 页面id
        params.setPageId("TrdJihuaBonusValuationTrade002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("计划类资产交易删除：交易编号：", entity.getTradeId(),
                "交易日期：",DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdJihuaBonusValuationTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdJihuaBonusValuationTradeDto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }


}
