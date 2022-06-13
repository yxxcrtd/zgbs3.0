/*
 * 文件名：TrdLendInvestTrade001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Dto;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.service.TrdOutRightRepoValuationTrade001Service;
import com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.service.TrdOutRightRepoValuationTrade002Service;
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
 * 买断式回购交易查询
 */
@Controller
@PageId("TrdOutRightRepoValuationTrade001")
@RequestMapping("/valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001")
public class TrdOutRightRepoValuationTrade001Controller extends BaseController {

    /**
     * TrdOutRightRepoValuationTrade001Service
     */
    @Autowired
    private TrdOutRightRepoValuationTrade001Service trdOutRightRepoValuationTrade001Service;

    /**
     * TrdOutRightRepoValuationTrade002Service
     */
    @Autowired
    private TrdOutRightRepoValuationTrade002Service trdOutRightRepoValuationTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.拆借方向, DictCode.拆借类型,DictCode.交易方向,
                DictCode.交易流程状态);
        return super.init(model);
    }

    /**
     * @Description:页面查询，表格分页
     */

    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdOutRightRepoValuationTrade001Dto> findPage()
            throws Exception {
        Page<TrdOutRightRepoValuationTrade001Dto> page = trdOutRightRepoValuationTrade001Service.findPage(
                RequestUtil.getParameters(getRequest()));
        return page;
    }

    /**
     * 导出数据
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export()
            throws ParseException, IOException {

        String datas = this.getParam("datas");
        Map<String, Object> params = RequestUtil.getPageParam();
        List<TrdOutRightRepoValuationTrade001Dto> listdto;
        if (datas != null) {
            listdto = JsonUtils.json2List(datas, TrdOutRightRepoValuationTrade001Dto.class);
        } else {
            listdto = trdOutRightRepoValuationTrade001Service.queryPageList(params);
        }
        File outFile = trdOutRightRepoValuationTrade001Service.makeFile(
                "/com/joyintech/tams/valuation/trademanage/trdbuyoutrepovaluation/conf/export_conf.xml",
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
    public AjaxResult check(@RequestBody @Valid TrdOutRightRepoValuationTrade001Dto dto,
                            BindingResult result) throws ParseException {

        List<TrdOutRightRepoValuationTrade001Dto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdOutRightRepoValuationTrade001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdOutRightRepoValuationTrade001Service.checkBusinessDataInFlow(item)) {
                    //流程存在情况
                    return new AjaxResult("交易编号：" + item.getTradeId() + "已存在流程中", MessageType.错误消息);
                }

            }
        }else if("edit".equals(dto.getOperateType())){
            for (TrdOutRightRepoValuationTrade001Dto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if(trdOutRightRepoValuationTrade001Service.checkBusinessDataInFlow(item)){
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
    public AjaxResult delete(@RequestBody @Valid TrdOutRightRepoValuationTrade001Dto dto, BindingResult br)
    {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<TrdOutRightRepoValuationTrade001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdOutRightRepoValuationTrade001Dto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_买断式回购交易.getValue());
        // 页面id
        params.setPageId("TrdOutRightRepoValuationTrade002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("买断式回购交易删除：", entity.getTradeId(),
                DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdOutRightRepoValuationTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdOutRightRepoValuationTrade001Dto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
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
        param.put("assetType","F01");
        return commonBusinessService.getAssetId(param);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getRepoId")
    public List<SelectOptionDto> getRepoId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        String finprodType=null;
        String ps=(String) param.get("ps");
        if(ps.equals("03")){
             finprodType="F04";
        }else if(ps.equals("04")){
             finprodType="F05";
        }else {
             finprodType="F04,F05";
        }
        param.put("finprodTypeArr", StringUtil.splitToList(GlobalConstant.COMMA,finprodType));
        return commonBusinessService.getRepoId(param);
    }

}
