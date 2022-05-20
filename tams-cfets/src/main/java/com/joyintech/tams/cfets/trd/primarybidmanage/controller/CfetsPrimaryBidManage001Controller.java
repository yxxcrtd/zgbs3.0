package com.joyintech.tams.cfets.trd.primarybidmanage.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.*;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManage001Service;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManageService;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManageValidatorService;
import com.joyintech.tams.common.base.entity.BidOrder;
import com.joyintech.tams.common.base.entity.BidResultTotal;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中标结果维护
 *
 * @author wangyang1
 * @version 1.0 2022年5月7日
 * @see CfetsPrimaryBidManage001Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsPrimaryBidManage001")
@RequestMapping("/cfets/trd/primarybidmanage/CfetsPrimaryBidManage001")
@Api(tags = "中标结果维护")
public class CfetsPrimaryBidManage001Controller extends BaseController{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CfetsPrimaryBidManageService cfetsPrimaryBidManageService;

    @Autowired
    private CfetsPrimaryBidManage001Service cfetsPrimaryBidManage001Service;

    @Autowired
    private CfetsPrimaryBidManageValidatorService cfetsPrimaryBidManageValidatorService;


    @ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String,Object>> init(){
        Map<String, Object> model = new HashMap<>();
        model.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        model.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
        model.put("BID_STATUS", this.getSelectDataFromDataDic(DictCode.中标方式, null));
        model.put("WIN_TYPE", this.getSelectDataFromDataDic(DictCode.投标方式, null));
        model.put("PAY_TYPE", this.getSelectDataFromDataDic(DictCode.缴款方式, null));
        model.put("BID_TYPE", this.getSelectDataFromDataDic(DictCode.中标方式, null));
        model.put("CfetsPrimaryBidManage_list_refresh_period", CacheUtil.getSysParam("CfetsPrimaryBidManage_list_refresh_period"));
        return Message.success("查询页面初始化参数",model);
    }
    
    
    /**
     * 查询投标确认列表
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询投标确认列表",notes = "查询投标确认列表")
    @ResponseBody
    @PostMapping("findCfmPage")
    public Message<Page<CfetsPrimaryBidManageCfmResponse>> findCfmPage(@RequestBody CfetsPrimaryBidManageCfmRequest request) {
        try {
            Page<CfetsPrimaryBidManageCfmResponse> page = cfetsPrimaryBidManage001Service.selectCfmForPage(request);
            return new Message<Page<CfetsPrimaryBidManageCfmResponse>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询投标确认列表数据异常",e);
            return new Message<Page<CfetsPrimaryBidManageCfmResponse>>().fail().setMessage("查询投标确认列表异常");
        }
    }

    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="导出投标确认列表",notes = "导出投标确认列表")
    @ResponseBody
    @PostMapping("exportBidManageCfmInfo")
    public void exportBidManageCfmInfo(@RequestBody CfetsPrimaryBidManageExportRequest requestDto, HttpServletResponse response){
        // 导出
        ExcelUitl.doExport(requestDto, response, "投标确认列表", () -> {
            CfetsPrimaryBidManageCfmRequest dto = requestDto.getDto(CfetsPrimaryBidManageCfmRequest.class);
            List<CfetsPrimaryBidManageCfmResponse> dataList = cfetsPrimaryBidManage001Service.selectCfmForPage(dto).getRoot();
            return dataList;
        });
    }

    /**
     * 查询投标确认信息
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询投标确认信息",notes = "查询投标确认信息")
    @ResponseBody
    @PostMapping("getBidManageCfmInfo")
    public Message<Map<String,Object>> getBidManageCfmInfo(@RequestBody CfetsPrimaryBidManageCfmRequest request) {
        try {
            Map<String,Object> response = cfetsPrimaryBidManageService.getBidManageCfmInfo(request);
            return new Message<Map<String,Object>>().success().setData(response);
        }catch (Exception e) {
            logger.error("查询投标确认信息数据异常",e);
            return new Message<Map<String,Object>>().fail().setMessage("查询投标确认信息异常");
        }
    }


    /**
     * 交易对手下拉框
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value="交易对手的联想下拉框",notes = "交易对手的联想下拉框")
    @ResponseBody
    @PostMapping("getCounterId")
    public Message<Page<SelectOptionDto>> getCounterId(@RequestBody SelectOptionCommonRequest request){
        try {
            Page<SelectOptionDto> page = cfetsPrimaryBidManage001Service.getCounterId(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("交易对手的联想下拉框异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("交易对手的联想下拉框异常");
        }
    }

    /**
     * 查询中标结果列表
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询中标结果列表",notes = "查询中标结果列表")
    @ResponseBody
    @PostMapping("findPage")
    public Message<Page<CfetsPrimaryBidManageResponse>> findPage(@RequestBody CfetsPrimaryBidManageRequest request) {
        try {
            Page<CfetsPrimaryBidManageResponse> page = cfetsPrimaryBidManage001Service.selectForPage(request);
            return new Message<Page<CfetsPrimaryBidManageResponse>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询投标确认列表数据异常",e);
            return new Message<Page<CfetsPrimaryBidManageResponse>>().fail().setMessage("查询中标结果列表异常");
        }
    }

    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="导出中标结果列表",notes = "导出中标结果列表")
    @ResponseBody
    @PostMapping("exportBidManageInfo")
    public void exportBidManageInfo(@RequestBody CfetsPrimaryBidManageExportRequest requestDto, HttpServletResponse response){
        // 导出
        ExcelUitl.doExport(requestDto, response, "中标结果", () -> {
            CfetsPrimaryBidManageRequest dto = requestDto.getDto(CfetsPrimaryBidManageRequest.class);
            List<CfetsPrimaryBidManageResponse> dataList = cfetsPrimaryBidManage001Service.selectForPage(dto).getRoot();
            return dataList;
        });
    }


    /**
     * 中标结果明细列表
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询中标结果执行明细列表",notes = "查询中标结果执行明细列表")
    @ResponseBody
    @PostMapping("findDetailPage")
    public Message<Page<CfetsPrimaryBidManageResponse>> findDetailPage(@RequestBody CfetsPrimaryBidManageRequest request) {
        try {
            Page<CfetsPrimaryBidManageResponse> page = cfetsPrimaryBidManage001Service.selectDetailForPage(request);
            return new Message<Page<CfetsPrimaryBidManageResponse>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询中标结果执行明细列表异常",e);
            return new Message<Page<CfetsPrimaryBidManageResponse>>().fail().setMessage("查询中标结果执行明细列表异常");
        }
    }

    /**
     * 中标撤销的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "中标的check", notes = "中标的check")
    @ResponseBody
    @PostMapping("winCheck")
    public Message<AjaxResult> winCheck(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult result) {
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(StringUtil.isNotEmpty(dto.getBidId())) {
            WflValidInputParameter parameter = new WflValidInputParameter();
            parameter.setPageId("CfetsPrimaryBidManage002");
            parameter.setBusinessDataPk2(dto.getBidId());
            if (cfetsPrimaryBidManageService.isInWflProcess(parameter)) {
                errorList.add(new ValidMessage("存在流程中中标结果维护数据，请重新选择！"));
            }
        }
        if(StringUtil.isNotEmpty(dto.getBidId())) {
            BidOrder bidOrder = cfetsPrimaryBidManageService.getBidOrder(dto.getBidId());
//            if (StringUtil.isNotEmpty(bidOrder) && bidOrder.getWinType().equals("01")) {
//                errorList.add(new ValidMessage("选择数据须已投标且不存在流程中数据，请重新选择！"));
//            }
        }
        AjaxResult ajaxResult = commonCheck(result, dto.getOperateType());
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

        /**
         * 中标撤销的check
         *
         * @param dto dto
         * @param result BindingResult
         * @return check结果
         * @see
         * @since 1.0
         */
        @ApiOperation(value = "未中标的check", notes = "未中标的check")
        @ResponseBody
        @PostMapping("unWinCheck")
        public Message<AjaxResult> unWinCheck(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult result) {
            AjaxResult res= new AjaxResult();
            List<ValidMessage> errorList = new ArrayList<>();
            if(StringUtil.isNotEmpty(dto.getBidId())) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("CfetsPrimaryBidManage002");
                parameter.setBusinessDataPk2(dto.getBidId());
                if (cfetsPrimaryBidManageService.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("存在流程中数据，不可进行中标结果维护！"));
                }
            }
            AjaxResult ajaxResult = commonCheck(result, dto.getOperateType());
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }

        /**
         * 中标撤销的check
         *
         * @param dto dto
         * @param result BindingResult
         * @return check结果
         * @see
         * @since 1.0
         */
        @ApiOperation(value = "中标撤销的check", notes = "中标撤销的check")
        @ResponseBody
        @PostMapping("check")
        public Message<AjaxResult> check(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult result) {
            AjaxResult res= new AjaxResult();
            List<ValidMessage> errorList = new ArrayList<>();
            dto.setOperateType("revoke");
            List<ValidMessage> valid = cfetsPrimaryBidManageValidatorService.valid(dto);
            errorList.addAll(valid);
            if(CollectionUtils.isNotEmpty(dto.getSelectedItem())) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("CfetsPrimaryBidManage002");
                for (CfetsPrimaryBidManageSaveRequest request:dto.getSelectedItem()) {
                    parameter.setBusinessDataPk2(request.getTotalWinId());
                    if (cfetsPrimaryBidManageService.isInWflProcess(parameter)) {
                        errorList.add(new ValidMessage("存在流程中数据，不可进行中标结果维护！"));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errorList)){
                res.setErrorList(errorList);
                return new Message<AjaxResult>().success().setData(res);
            }
            AjaxResult ajaxResult = commonCheck(result, dto.getOperateType());
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }

        /**
         * 删除
         * @param dto
         * @param br
         * @return
         * @throws Exception
         * @see
         * @since 1.0
         */
        @ApiOperation(value = "撤销确认", notes = "撤销确认")
        @ResponseBody
        @PostMapping("revoke")
        public Message<AjaxResult> revoke(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult br) throws WorkFlowException {
            if (br.hasErrors()) {
                return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
            }
            ProcessParameter<CfetsPrimaryBidManageSaveRequest> processParameter = new ProcessParameter<CfetsPrimaryBidManageSaveRequest>();
            WorkFlowParameter<CfetsPrimaryBidManageSaveRequest> params = new WorkFlowParameter<CfetsPrimaryBidManageSaveRequest>();
            params.setPageId("CfetsPrimaryBidManage002");
            params.setElementId(dto.getElementId());
            params.setBusinessCode(item -> item.getTotalWinId());
            params.setBusinessType(DictCodeListExt3.共通业务类型_一级市场债中标结果维护.getValue());
            // 设置流程业务说明, entity即传入的dto
            params.setComment(entity -> {
                return WorkFlowUtil.concatComment("投标编号"+
                        GlobalConstant.OPEN_PARENTHESIS+entity.getTotalWinId()+GlobalConstant.CLOSE_PARENTHESIS+
                        "中标结果编号"+GlobalConstant.OPEN_PARENTHESIS+entity.getTotalWinId()+GlobalConstant.CLOSE_PARENTHESIS);
            });
            processParameter.setOperationType(OperationType.DELETE);
            processParameter.setWorkFlowParameter(params);
            //债券交易自动加券、上市流通交易自动加券，加的券没有在留痕表存值，导致删除报错特殊处理
            String msg =null;
            ProcessMark pk = new ProcessMark();
            pk.setPageId("CfetsPrimaryBidManage002");
            msg = cfetsPrimaryBidManageService.start(processParameter,
                    dto.getSelectedItem().toArray(new CfetsPrimaryBidManageSaveRequest[dto.getSelectedItem().size()]));
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        }

    /**
     * 未中标
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "未中标保存", notes = "未中标保存")
    @ResponseBody
    @PostMapping("unbid")
    public Message<AjaxResult> unbid(@RequestBody CfetsPrimaryBidManageCfmRequest request){
        try {
            AjaxResult response = cfetsPrimaryBidManageService.unBidSave(request);
            return new Message<AjaxResult>().success().setData(response);
        }catch (Exception e) {
            logger.error("未中标保存数据异常",e);
            return new Message<AjaxResult>().fail().setMessage("未中标保存信息异常");
        }
    }
  }
