/*
 * 文件名：TrdPlanRateInvestTrade001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.controller;

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
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.service.TrdPlanValuationTrade001Service;
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
 * @Description:计划类资产投资交易查询
 * @auther: yuzj
 * @date: 2021/12/30
 * @param:
 * @return:
 */
@Controller
@PageId("TrdPlanValuationTrade001")
@RequestMapping("/valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade001")
public class TrdPlanValuationTrade001Controller extends BaseController {
    /**
     * 注入计划类（利率型）service1
     */
    @Autowired
    private TrdPlanValuationTrade001Service trdPlanValuationTrade001Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 初始化方法
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向,DictCode.生成方式);
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
        param.put("finprodType", DictCodeListExt1.金融产品类型_理财产品.getValue());
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
        param.put("assetType",DictCodeListExt1.金融产品类型_计划类资产.getValue());
        String[] arr = {DictCodeList.收益类型_净值型.getValue(),DictCodeList.收益类型_货币型.getValue()};
        param.put("profitTypeArr",arr);
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
    public Page<TrdPlanValuationTradeDto> findPage()
            throws Exception {
        Page<TrdPlanValuationTradeDto> page = trdPlanValuationTrade001Service.selectForPage(
                RequestUtil.getParameters(getRequest()));
        return page;
    }

    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export(){
        Map<String, Object> param = RequestUtil.getParameters(getRequest());
        return trdPlanValuationTrade001Service.export(this.getPageId(),param);
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
    public AjaxResult check(@RequestBody @Valid TrdPlanValuationTradeDto dto,
                            BindingResult result) throws ParseException {

        List<TrdPlanValuationTradeDto> list = dto.getSelectedItem();
        if(list==null||list.size()<=0){
            return new AjaxResult("请选择数据", MessageType.错误消息);
        }
        //删除时的校验
        if(StringUtil.isNotEmpty(list) && "delete".equals(dto.getOperateType())) {
            for (TrdPlanValuationTradeDto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if (trdPlanValuationTrade001Service.checkBusinessDataInFlow(item)) {
                    //流程存在情况
                    return new AjaxResult("交易编号：" + item.getTradeId() + "已存在流程中", MessageType.错误消息);
                }
            }
        }else if("edit".equals(dto.getOperateType())){
            for (TrdPlanValuationTradeDto item : list) {
                //1.判断是否在流程中
                //判断当前流程是否存在
                if(trdPlanValuationTrade001Service.checkBusinessDataInFlow(item)){
                    //流程存在情况
                    return new AjaxResult("交易编号："+item.getTradeId()+"已存在流程中", MessageType.错误消息);
                }
            }
        }
        return new AjaxResult();
    }

    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody  TrdPlanValuationTradeDto dto)
    {
        // 启动流程
        ProcessParameter<TrdPlanValuationTradeDto> proParam = new ProcessParameter<>();
        WorkFlowParameter<TrdPlanValuationTradeDto> params = new WorkFlowParameter<>();

        // 业务代码（凭证流水号）
        params.setBusinessCode(item -> item.getTradeId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
        // 页面id
        params.setPageId("TrdPlanValuationTrade002");
        // 元素id
        params.setElementId(dto.getElementId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("计划类资产删除：交易编号：", entity.getTradeId(),
                "交易日期：",DateUtil.format(entity.getTradeDate(),DateUtil.DEFAULT_DATETIME_FORMAT)
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = trdPlanValuationTrade001Service.start(proParam, dto.getSelectedItem().toArray(
                    new TrdPlanValuationTradeDto[dto.getSelectedItem().size()]));
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }


}
