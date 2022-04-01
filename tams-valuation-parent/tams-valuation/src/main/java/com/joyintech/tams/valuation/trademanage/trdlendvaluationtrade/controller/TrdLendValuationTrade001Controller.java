/*
 * 文件名：TrdLendInvestTrade001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;

import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.service.TrdLendValuationTrade001Service;
import com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.service.TrdLendValuationTrade002Service;
import com.joyintech.workflow.engine.constant.OperationType;
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
 * 拆借投资交易查询
 */
@Controller
@PageId("TrdLendValuationTrade001")
@RequestMapping("/valuation/trademanage/trdlendvaluationtrade/TrdLendValuationTrade001")
public class TrdLendValuationTrade001Controller extends BaseController {

    /**
     * TrdLendInvestTrade001Service
     */
    @Autowired
    private TrdLendValuationTrade001Service trdLendValuationTrade001Service;

    /**
     * TrdLendInvestTrade002Service
     */
    @Autowired
    private TrdLendValuationTrade002Service trdLendValuationTrade002Service;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.拆借方向, DictCode.拆借类型,DictCode.回购拆借方向,
                DictCode.交易流程状态);
        return super.init(model);
    }

    /**
     * @Description:页面查询，表格分页
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdLendValuationTradeDto> findPage()
            throws Exception {
        Page<TrdLendValuationTradeDto> page = trdLendValuationTrade001Service.findPage(
                RequestUtil.getParameters(getRequest()));
        return page;
    }

    //删除
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdLendValuationTradeDto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdLendValuationTradeDto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdLendValuationTradeDto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_拆借投资交易.getValue());
        // 页面id
        params.setPageId("BondTradeQuery001");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("质押式回购交易删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdLendValuationTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdLendValuationTradeDto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }


    /**
     * 导出数据
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws ParseException, IOException {

        String datas = this.getParam("datas");
        Map<String, Object> params = RequestUtil.getPageParam();
        List<TrdLendValuationTradeDto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdLendValuationTradeDto.class);
        } else {
            listdto = trdLendValuationTrade001Service.queryPageList(params);
        }
        File outFile = trdLendValuationTrade001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdlendvaluationtrade/conf/export_conf.xml",
                listdto);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(new Date())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);
    }

}
