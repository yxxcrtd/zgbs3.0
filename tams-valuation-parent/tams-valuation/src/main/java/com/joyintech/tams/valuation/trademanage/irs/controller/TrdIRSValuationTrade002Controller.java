/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.irs.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.irs.dto.TrdIRSValuationTrade002Dto;
import com.joyintech.tams.valuation.trademanage.irs.service.TrdIRSValuationTrade002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 利率互换交易新建
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月13日
 * @see TrdIRSValuationTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdIRSValuationTrade002")
@RequestMapping("/valuation/trademanage/irs/TrdIRSValuationTrade002")
public class TrdIRSValuationTrade002Controller extends BaseController {

    @Autowired
    private TrdIRSValuationTrade002Service trdIRSValuationTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model,DictCode.交易方向, DictCode.币种,
                DictCode.互换方式, DictCode.计息基础, DictCode.频率, DictCode.利率行情);
        // 付息频率 （重置频率 枚举值没有到期一次 需要特殊处理）
        this.setSelectOptionToModelEx(DictCode.频率, model);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        //工作流
        if(StringUtil.isNotEmpty(workflowId)){
            TrdIRSValuationTrade002Dto entity= trdIRSValuationTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){//编辑页面
            TrdIRSValuationTrade002Dto entity = trdIRSValuationTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }


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
        return commonBusinessService.getProdId(param);
    }
    /**
     * 确认校验
     *
     * @param trdIRSValuationTrade002Dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody TrdIRSValuationTrade002Dto trdIRSValuationTrade002Dto,
                              BindingResult result) {
        return commonCheck(result, trdIRSValuationTrade002Dto.getOperateType());
    }


    /**
     * 保存提交
     *
     * @param dto
     * @return 画面输入参数
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody @Valid TrdIRSValuationTrade002Dto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdIRSValuationTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdIRSValuationTrade002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYSL", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.业务类型_债券借贷交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("交易日") + GlobalConstant.COLON + entity.getTradeDate()));

        String msg = trdIRSValuationTrade002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdIRSValuationTrade002Service.querySequences();
        }
        // 组装4位序列号 不足补0
        String sequence = StringUtils.leftPad(String.valueOf(sequences), 4, '0');
        // 日期格式化yyymmdd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formatDate = sdf.format(date);

        String tradeIdStr = str + formatDate + sequence;
        return tradeIdStr;
    }

    /**
     * 利率互换  两个控件 公用一个参数  但是下拉枚举值不同 需要特殊处理
     * <功能详细描述>
     * @param dictCode
     * @param model
     * @see
     * @since 1.0
     */
    public void setSelectOptionToModelEx(IDictCode dictCode, Model model) {
        SelectDto selectDto = this.getSelectDataFromDataDic(dictCode, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();

            //频率
            if(StringUtil.equals(DictCode.频率.getValue(),dictCode.getValue())){
                // 付息频率
                List<SelectOptionDto> fixOptions = new ArrayList<SelectOptionDto>();
                fixOptions.addAll(options);
                model.addAttribute(dictCode.getValue(), JsonUtils.objectArray2Json(fixOptions.toArray()));

                // 付息营业日准则
                List<SelectOptionDto> floatOptions = new ArrayList<SelectOptionDto>();
                floatOptions.addAll(options);
                for(int index=0;index<floatOptions.size();index++) {
                    SelectOptionDto selectOptionDto = floatOptions.get(index);
                    if(StringUtil.equals(selectOptionDto.getId(),DictCodeList.频率_到期一次.getValue())) {
                        floatOptions.remove(index);
                    }
                }
                model.addAttribute(dictCode.getValue() + "_RESET", JsonUtils.objectArray2Json(floatOptions.toArray()));
            }
        }
    }


}
