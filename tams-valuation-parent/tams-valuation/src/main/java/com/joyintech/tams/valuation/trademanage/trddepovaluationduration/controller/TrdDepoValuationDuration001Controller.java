/*
 * 文件名：TrdRepoTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trddepovaluationduration.controller;

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

import com.joyintech.tams.valuation.trademanage.trddepovaluationduration.dto.TrdDepoValuationDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trddepovaluationduration.service.TrdDepoValuationDuration001Service;
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
 * 存款类存续期
 * 〈功能详细描述〉
 * @see TrdDepoValuationDuration001Controller
 */
@PageId("TrdDepoValuationDuration001")
@RequestMapping("/valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001")
@Controller
public class TrdDepoValuationDuration001Controller extends BaseController {


    @Autowired
    private TrdDepoValuationDuration001Service trdDepoValuationDuration001Service;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model,DictCode.交易方向,DictCode.交易场所,
                DictCode.交易流程状态);
        return super.init(model);
    }


    @RequestMapping("findPage")
    @ResponseBody
    public Page<TrdDepoValuationDuration001Dto> findPage()
            throws Exception {
        Page<TrdDepoValuationDuration001Dto> page = trdDepoValuationDuration001Service.findPage(
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
        return trdDepoValuationDuration001Service.findPortfolioId(param);
    }


    /**
     * 自定义资产代码下拉框
     */
    @RequestMapping("/findFinprodId")
    @ResponseBody
    public Page<SelectOptionDto> findFinprodId(HttpServletRequest request) {
        Map<String, Object> param =RequestUtil.getPageParam();
        //param.put("finprodType", BussinessDictCodeList.金融产品类型_计划类资产.getValue()); //F17
        param.put("finprodType", "F01"); //测试
        return trdDepoValuationDuration001Service.findFinprodId(param);
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
    public AjaxResult check(@RequestBody @Valid TrdDepoValuationDuration001Dto dto,
                            BindingResult result) throws ParseException {

        List<TrdDepoValuationDuration001Dto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdDepoValuationDuration001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdDepoValuationDuration001Service.checkBusinessDataInFlow(item)) {
                    //流程存在情况
                    return new AjaxResult("交易编号：" + item.getTradeId() + "已存在流程中", MessageType.错误消息);
                }

            }
        }else if("edit".equals(dto.getOperateType())){
            for (TrdDepoValuationDuration001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if(trdDepoValuationDuration001Service.checkBusinessDataInFlow(item)){
                    //流程存在情况
                    return new AjaxResult("交易编号："+item.getTradeId()+"已存在流程中", MessageType.错误消息);
                }
            }
        }
        return new AjaxResult();
    }


    //删除
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Valid TrdDepoValuationDuration001Dto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdDepoValuationDuration001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdDepoValuationDuration001Dto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_存款类资产存续期.getValue());
        // 页面id
        params.setPageId("TrdDepoValuationDuration002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("存款类存续期删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdDepoValuationDuration001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdDepoValuationDuration001Dto[dto.getSelectedItem().size()]));
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
        List<TrdDepoValuationDuration001Dto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdDepoValuationDuration001Dto.class);
        } else {
            listdto = trdDepoValuationDuration001Service.queryPageList(params);
        }
        File outFile = trdDepoValuationDuration001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trddepovaluationduration/conf/export_conf.xml",
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
