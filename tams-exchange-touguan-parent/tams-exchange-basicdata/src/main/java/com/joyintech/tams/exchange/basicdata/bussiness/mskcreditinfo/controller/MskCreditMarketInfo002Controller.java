/*
 * 文件名：MarketInformationController002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service.MskCreditMarketInfoPro001Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.*;


@Controller
@PageId("MstCreditMarketInfo002")
@RequestMapping("/exchange/basicdata/bussiness/mskcreditinfo/MstCreditMarketInfo002")
public class MskCreditMarketInfo002Controller extends BaseController {

    @Autowired
    private MskCreditMarketInfoPro001Service mskCreditMarketInfoPro001Service;



    @ApiOperation(value="新建页面初始化参数", notes = "新建页面初始化参数")
    @PostMapping("/getInitPageParams")
    @ResponseBody
    public Message<Map<String,Object>> getInitPageParams() {
        //初始化基本信息画面数据字典
        Map<String,Object> model = new HashMap<>();
        String secId = getParam("secid");
        String valueDate = getParam("cate");
        String valueSource = getParam("valueSource");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            MskCreditInfoProSaveRequest dto = mskCreditMarketInfoPro001Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.put(GlobalConstant.ENTITY, dto);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && 
            StringUtil.isNotEmpty(secId) && StringUtil.isNotEmpty(valueDate)
            && StringUtil.isNotEmpty(valueSource)) {
            //通过主键查询数据
            MskCreditInfoProSaveRequest dto = mskCreditMarketInfoPro001Service.selectByKey(null,
                    secId, valueDate, valueSource);
            model.put(GlobalConstant.ENTITY, dto);
        }
        model.put("VALUE_SOURCE",this.getSelectDataFromDataDic(DictCode.估值来源, null));
        model.put("CREDIT_TYPE",this.getSelectDataFromDataDic(DictCode.信用类型, null));
        model.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        return new Message<Map<String, Object>>(model).success();
    }

    /**
     * 画面点击保存按钮时check
     * 
     * @param dto 实体类
     * @param result 
     * @return check结果
     */
   /* @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) MskCreditInfoProSaveRequest dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }*/


    /**
     * check--保存
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "提交保存check", notes = "提交保存check")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody MskCreditInfoProSaveRequest dto, BindingResult result) {
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(!StringUtil.equals(dto.getOperateStatus(), OperationType.MODIFY.name()) && mskCreditMarketInfoPro001Service.selectByKey(null, dto.getSecid(), dto.getCdate(),null) != null){
            errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0006","同一资产","同一交易日期")));
            res.setErrorList(errorList);
            res.setMessageType(MessageType.错误消息);
            return new Message<AjaxResult>().success().setData(res);
        }
        return new Message<AjaxResult>().success().setData(res);
    }
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public Message<AjaxResult> save(@RequestBody @Validated({
        ValidateGroupEdit.class}) MskCreditInfoProSaveRequest dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
        }
        ProcessParameter<MskCreditInfoProSaveRequest> processParameter = new ProcessParameter<MskCreditInfoProSaveRequest>();
        WorkFlowParameter<MskCreditInfoProSaveRequest> params = new WorkFlowParameter<MskCreditInfoProSaveRequest>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getSecid())) {
            dto.setSecid(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getSecid());
        params.setBusinessType(DictCodeList.共通业务类型_信用保护凭证行情.getValue());

        params.setComment(entity1 -> {
            return WorkFlowUtil.concatComment("资产代码"+
                    GlobalConstant.OPEN_PARENTHESIS+entity1.getSecid()+GlobalConstant.CLOSE_PARENTHESIS
                    +"估值日期"+GlobalConstant.OPEN_PARENTHESIS+entity1.getCdate()+GlobalConstant.CLOSE_PARENTHESIS
                    +"估值来源"+GlobalConstant.OPEN_PARENTHESIS+entity1.getValueSourceStr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
        dto.setCreateTime(sysDate);
        String msg = mskCreditMarketInfoPro001Service.start(processParameter, dto);
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


    /**
     * 新建时根据市场代码查询wind接口债券信息
     *
     * @param request 市场代码
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "获取信用保护基本信息", notes = "获取信用保护基本信息")
    @ResponseBody
    @PostMapping("/getBasicInfo")
    public Message<MskCreditInfoProSaveRequest> getBasicInfo(@RequestBody MskCreditInfoProSaveRequest request){
//        String secid = null;
//        if (StringUtil.isNotEmpty(request.getSecid())) {
//            secid = request.getSecid();
//        }
        MskCreditInfoProSaveRequest resultDto = mskCreditMarketInfoPro001Service.getBasicInfos(request);
        return new Message<MskCreditInfoProSaveRequest>().success().setData(resultDto);
    }


    /**
     *  获取流程中信息
     * @param request
     * @return
     */
    @ApiOperation(value = "获取流程中信息", notes = "获取流程中信息")
    @ResponseBody
    @PostMapping("/getApproveEntity")
    public Message<MskCreditInfoProSaveRequest> getApproveEntity(@RequestBody MskCreditInfoProSaveRequest request)
    {
        try{
            String workflowId = request.getWorkflowId();
            MskCreditInfoProSaveRequest entity = new MskCreditInfoProSaveRequest();
            if (StringUtil.isNotEmpty(workflowId)) {
                //流程代办checkImportData
                entity = mskCreditMarketInfoPro001Service.getBussinessEntityByWorkflowId(workflowId);
            }
            return new Message<MskCreditInfoProSaveRequest>().success().setData(entity);
        } catch(Exception e){
            e.printStackTrace();
            return new Message<MskCreditInfoProSaveRequest>().fail().setMessage("查询异常");
        }
    }


    @ResponseBody
    @PostMapping("/getFinprodMarketInfo")
    @ApiOperation(value = "信用保护查询资产列表", notes = "信用保护查询资产列表")
    public Message<Page<SelectOptionDto>> getFinprodMarketInfo(@RequestBody MskCreditInfoProSaveRequest dto){
        try {
            Page<SelectOptionDto> tempList = mskCreditMarketInfoPro001Service.getFinprodMarketInfo(dto);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }
    
}
