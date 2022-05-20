/*
 * 文件名：FinAssetRateAdj001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.business.asset.bond.dto.FinAssetRateAdjRequest;
import com.joyintech.tams.business.asset.bond.dto.FinAssetRateAdjResponse;
import com.joyintech.tams.business.asset.bond.service.FinAssetRateAdjValidatorService;
import com.joyintech.tams.business.asset.bond.service.FinBondAsset002Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率调整
 * @author wangyang1
 * @version 1.0 2022年4月19日
 * @see FinBondAsset003Controller
 * @since 1.0
 */
@Controller
@PageId("FinBondAsset003")
@RequestMapping("/business/asset/bond/FinBondAsset003")
@Api(tags = "利率调整")
public class FinBondAsset003Controller extends BaseController{

    @Autowired
    private FinBondAsset002Service finBondAsset002Service;
    
    @Autowired
    private IPlatformDate iPlatformDate;
    
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private FinAssetRateAdjValidatorService finAssetRateAdjValidatorService;

    @ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
    @ResponseBody
    @PostMapping("/init")
    public Message<Map<String,Object>> init(FinAssetRateAdjRequest request){
        Map<String, Object> model = new HashMap<>();
        model.put("finprodId", request.getFinprodId());
        model.put("finprodName", request.getFinprodAbbr());
        model.put("finprodType2", request.getFinprodType2());
        model.put("finprodMarketId", request.getFinprodMarketId());
        model.put("finprodAbbr", request.getFinprodAbbr());
        model.put("workflowId", request.getWorkflowId());
        model.put("rateType", request.getRateType());
        model.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.put("FINPROD_TYPE_TG_F07",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.put("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.put("FINPROD_TYPE_TG_F18",DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.put("FINPROD_TYPE_TG_F28", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        model.put("RATE_TYPE_01", DictCodeList.利率类型_固定利率.getValue());
        model.put("isInitial", DictCodeList.是否_否.getValue());
        // 平台日期
        model.put("iPlatformDate", iPlatformDate.getSysDate());
        setEntity(model);
        return new Message<Map<String, Object>>(model).success();
    }

    private void setEntity(Map<String, Object> model) {
        //待办
        if(StringUtil.isNotEmpty(model.get("workflowId"))){
            String workflowId = model.get("workflowId").toString();
            FinAssetRateAdjResponse finAssetRateAdjResponse = finBondAsset002Service.getBussinessEntityByWorkflowId(workflowId);
            model.put("finprodId", finAssetRateAdjResponse.getFinprodId());
            model.put("finprodType2", finAssetRateAdjResponse.getFinprodType2());
        }
    }

    /**
     *  获取流程中信息
     * @param request
     * @return
     */
    @ApiOperation(value = "获取流程中信息", notes = "获取流程中信息")
    @ResponseBody
    @PostMapping("/getApproveRateAdjEntity")
    public Message<FinAssetRateAdjResponse> getApproveEntity(@RequestBody FinAssetRateAdjResponse request)
    {
        try{
            String workflowId = request.getWorkflowId();
            FinAssetRateAdjResponse entity = new FinAssetRateAdjResponse();
            if (StringUtil.isNotEmpty(workflowId)) {
                //流程代办checkImportData
                entity = finBondAsset002Service.getBussinessEntityByWorkflowId(workflowId);
            }
            return new Message<FinAssetRateAdjResponse>().success().setData(entity);
        } catch(Exception e){
            e.printStackTrace();
            return new Message<FinAssetRateAdjResponse>().fail().setMessage("查询异常");
        }
    }

    /**
     * 初始化
     * <功能详细描述>
     * @param request
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ApiOperation(value="初始化数据查询", notes = "查询页面初始化参数")
    @PostMapping( "/selectList")
    @ResponseBody
    public Message<List<FinAssetRateAdjResponse>> initInfo(@RequestBody FinAssetRateAdjRequest request) throws ParseException {
        String finprodId = request.getFinprodId();
        String workflowId = request.getWorkflowId();
        String finprodType2 = request.getFinprodType2();
        List<FinAssetRateAdjResponse> finAssetRateAdjList = new ArrayList<FinAssetRateAdjResponse>();
            //来自代办
            if(StringUtil.isNotEmpty(workflowId)){
                //根据工作流与留痕处理提供的共通方法获取业务实体
                FinAssetRateAdjResponse response = finBondAsset002Service.getBussinessEntityByWorkflowId(workflowId);
                finAssetRateAdjList = response.getSelectedItem();
            }else{
                if(StringUtil.isNotEmpty(finprodId)){
                    Map<String,Object> paramMap = new HashMap<String,Object>();
                    paramMap.put("finprodId", finprodId);
                    paramMap.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
                    if (DictCodeListExt1.金融产品类型_投管用_通道.getValue().equals(finprodType2)) {
                    	paramMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
                    }
                    paramMap.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
                    paramMap.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
                    paramMap.put("finprodType2", finprodType2);
                    finAssetRateAdjList = finBondAsset002Service.getfinAssetRateAdjByProductId(paramMap);
                }
            }
        return Message.success("初始化数据查询",finAssetRateAdjList);
    }

    /**
     * 该资产是否有交易数据check
     * <功能详细描述>
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value="该资产是否有交易数据check", notes = "该资产是否有交易数据check")
    @ResponseBody
    @PostMapping("/isTransInAssetCheck")
    public Message<Boolean> isTransInAssetCheck(@RequestBody FinAssetRateAdjRequest request) {
        if(commonBusinessService.isTransInAsset(request.getFinprodId())!=0){
            return Message.success("该资产是否有交易数据check",true);
        }
        return  Message.success("该资产是否有交易数据check",false);
    }
    
    /**
     * check--保存
     * @param response
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value="保存check", notes = "保存check")
    @ResponseBody
    @PostMapping("/check")
    public Message<AjaxResult> check(@RequestBody FinAssetRateAdjResponse response, BindingResult result) {
        List<ValidMessage> validMessages = finAssetRateAdjValidatorService.valid(response);
        if (CollectionUtils.isNotEmpty(validMessages)){
            AjaxResult ajax = new AjaxResult();
            ajax.setErrorList(validMessages);
            return Message.success("保存check报错",ajax);
        }
        return Message.success("保存check",commonCheck(result, response.getOperateType()));
    }
    
    /**
     * 保存
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="保存", notes = "保存")
    @ResponseBody
    @PostMapping(PAGE_SAVE)
    public Message<AjaxResult> save(@RequestBody FinAssetRateAdjResponse dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return Message.fail("保存错误",new AjaxResult(br));
        }
        WorkFlowParameter<FinAssetRateAdjResponse> params = new WorkFlowParameter<FinAssetRateAdjResponse>();
        params.setPageId("FinBondAsset003");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(FinAssetRateAdjResponse::getFinprodMarketId);
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产利率调整.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment("利率调整，债券代码"+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId()+GlobalConstant.CLOSE_PARENTHESIS,
                "债券简称"+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinAssetRateAdjResponse> parameter = new ProcessParameter<FinAssetRateAdjResponse>();
        parameter.setOperationType(OperationType.NEW);
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = finBondAsset002Service.start(parameter, dto);
        return Message.success(msg,new AjaxResult(msg));
    }
}
