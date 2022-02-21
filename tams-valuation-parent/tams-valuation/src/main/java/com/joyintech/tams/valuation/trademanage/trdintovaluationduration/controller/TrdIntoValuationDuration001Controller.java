/*
 * 文件名：TrdRepoTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdintovaluationduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.service.TrdIntoValuationDuration001Service;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 融入券存续期
 * 〈功能详细描述〉
 * @see TrdIntoValuationDuration001Controller
 */
@PageId("TrdIntoValuationDuration001")
@RequestMapping("/valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001")
@Controller
public class TrdIntoValuationDuration001Controller extends BaseController {


    @Autowired
    private TrdIntoValuationDuration001Service trdIntoValuationDuration001Service;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model,DictCode.交易方向,DictCode.交易场所,
                DictCode.交易流程状态);
        return super.init(model);
    }


    @RequestMapping("findPage")
    @ResponseBody
    public Page<TrdIntoValuationDuration001Dto> findPage()
            throws Exception {
        Page<TrdIntoValuationDuration001Dto> page = trdIntoValuationDuration001Service.findPage(
                RequestUtil.getParameters(getRequest()));
        return page;
    }


    /**
     * 自定义产品代码下拉框
     * @param request
     * @return
     */
    @RequestMapping("/findPortfolioId")
    @ResponseBody
    public Page<SelectOptionDto> findPortfolioId(HttpServletRequest request) {
        Map<String, Object> param =RequestUtil.getPageParam();
        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("finprodType", "F16"); //测试
        return trdIntoValuationDuration001Service.findPortfolioId(param);
    }


    /**
     * 自定义资产代码下拉框
     */
    @RequestMapping("/findFinprodId")
    @ResponseBody
    public Page<SelectOptionDto> findFinprodId(HttpServletRequest request) {
        Map<String, Object> param =RequestUtil.getPageParam();
        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
//        param.put("finprodType", "F16"); //测试
        return trdIntoValuationDuration001Service.findFinprodId(param);
    }



    //删除
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdIntoValuationDuration001Dto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdIntoValuationDuration001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdIntoValuationDuration001Dto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeListExt2.共通业务类型_债券借贷存续期.getValue());
        // 页面id
        params.setPageId("TrdIntoValuationDuration002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("融入券存续期删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdIntoValuationDuration001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdIntoValuationDuration001Dto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }
//
//
    /**
     * 导出数据
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws ParseException, IOException {

        String datas = this.getParam("datas");
        Map<String, Object> params = RequestUtil.getPageParam();
        List<TrdIntoValuationDuration001Dto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdIntoValuationDuration001Dto.class);
        } else {
            listdto = trdIntoValuationDuration001Service.queryPageList(params);
        }
        File outFile = trdIntoValuationDuration001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdintovaluationduration/conf/export_conf.xml",
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
