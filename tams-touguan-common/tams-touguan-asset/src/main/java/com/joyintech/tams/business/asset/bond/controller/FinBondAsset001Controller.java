/*
 * 文件名：FinBondTargetAsset001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.controller;

import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.*;
import com.joyintech.tams.business.asset.bond.service.FinBondAsset001Service;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.riskrds.service.RskNodeConfigService;
import com.joyintech.tams.constant.DictCodeListExt1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetService;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetValidatorService;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * 债券标的
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAsset001Controller
 * @since 1.0
 */
@Controller
@PageId("FinBondAsset001")
@RequestMapping("/business/asset/bond/FinBondAsset001")
@Api(tags = "债券标的")
public class FinBondAsset001Controller extends BaseController{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FinBondAssetService finBondAssetService;

    @Autowired
    private FinBondAsset001Service finBondAsset001Service;

    @Autowired
    private FinBondAssetValidatorService finBondAssetValidatorService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private RskNodeConfigService rskNodeConfigService;

    @ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String,Object>> init(){
        Map<String, Object> model = new HashMap<>();
        model.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        model.put("CUSTOM_ASSET_TYPE_TWO", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));
        model.put("CUSTOM_ASSET_TYPE_THREE", this.getSelectDataFromDataDic(DictCode.行内资产三级分类, null));
        model.put("CUSTOM_ASSET_TYPE_FOUR", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        model.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
        model.put("FREQUENCY_UNIT", this.getSelectDataFromDataDic(DictCode.频率, null));
        model.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        model.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        model.put("EXEC_MODE", this.getSelectDataFromDataDic(DictCode.行权方式, null));
        model.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        model.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        model.put("MAIN_GRADE_RESULT", this.getSelectDataFromDataDic(DictCode.评级结果, null));
        model.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.put("iPlatformDate", iPlatformDate.getSysDateStr());
        model.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //是否新增债券查询条件添加004控制  modify by liupeilin 2020-01-15  begin
        model.put("YES",DictCodeList.是否_是.getValue());
        model.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        if("004".equals(CacheUtil.getSysParam("CUSTOMER_BANK_CODE"))){
            model.put("BANK_CODE", "004");
        }
        model.put("FinBondAsset001_list_refresh_period", CacheUtil.getSysParam("FinBondAsset001_list_refresh_period"));
        //是否新增债券查询条件添加004控制  modify by liupeilin 2020-01-15  end
        return new Message<Map<String, Object>>(model).success();
    }
    
    
    /**
     * 初始化查询
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询债券标的信息",notes = "查询债券标的信息")
    @ResponseBody
    @PostMapping("findPage")
    public Message<Page<FinBondAssetResponse>> findPage(@RequestBody FinBondAssetRequest request) {
        try {
            Page<FinBondAssetResponse> page = finBondAsset001Service.selectForPage(request);
            return new Message<Page<FinBondAssetResponse>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询债券标的信息分页数据异常",e);
            return new Message<Page<FinBondAssetResponse>>().fail().setMessage("查询债券标的信息异常");
        }
    }

    /**
     * 债券简称的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value="债券简称的联想下拉框",notes = "债券简称的联想下拉框")
    @ResponseBody
    @PostMapping("getAssetId")
    public Message<Page<SelectOptionDto>> getAssetId(@RequestBody SelectOptionCommonRequest request){
        try {

            Page<SelectOptionDto> page = finBondAsset001Service.getAssetId(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("债券简称下拉框数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("债券简称的联想下拉框异常");
        }
    }


    /**
     * 发行人的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value="发行人的联想下拉框",notes = "发行人的联想下拉框")
    @ResponseBody
    @PostMapping("getIssuerId")
    public Message<Page<SelectOptionDto>> getIssuerId(@RequestBody SelectOptionCommonRequest request){
        try {
            Page<SelectOptionDto> page = finBondAsset001Service.getIssuerId(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询发行人的联想下拉框数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("发行人的联想下拉框异常");
        }
    }


    /**
     * 跨市场代码的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value="跨市场代码的联想下拉框",notes = "跨市场代码的联想下拉框")
    @ResponseBody
    @PostMapping("getCrossMarketCode")
    public Message<Page<SelectOptionDto>> getCrossMarketCode(@RequestBody SelectOptionCommonRequest request){
        try {
            Page<SelectOptionDto> page = finBondAsset001Service.getCrossMarketCode(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("跨市场代码联想下拉框数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("跨市场代码下拉框异常");
        }
    }

    /**
     * 删除按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "删除按钮点击时的check", notes = "删除按钮点击时的check")
    @ResponseBody
    @PostMapping("check")
    public Message<AjaxResult> check(@RequestBody FinBondAssetSaveRequest dto, BindingResult result) {
        //新增撤销校验，不允许删除上市债券  add by mashukai 20210425 start
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        errorList = finBondAssetValidatorService.valid(dto);
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()) {
            for (FinBondAssetSaveRequest entity : dto.getSelectedItem()) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("FinBondAsset002");
                parameter.setBusinessDataPk1(entity.getFinprodMarketId());
                if (finBondAssetService.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("", getMessage("所选资产存在流程中的数据！,不可删除!")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }
            }

        }
        if (dto.getSelectedItem() != null && dto.getSelectedItem().size()>0
                && dto.getSelectedItem().get(0).getFinprodMarketId() != null){
            String finprodId = commonBusinessService.getFinProdId(dto.getSelectedItem().get(0).getFinprodMarketId(),DictCodeListExt1.金融产品类型_投管用_债券.getValue(),null) ;
            int count = finBondAssetService.selectIsIssueAsset(finprodId);
            if (count > 0){
                ValidMessage validMessage = new ValidMessage("上市债券不允许删除,请先撤销对应的上市流通交易");
                errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return new Message<AjaxResult>().success().setData(res);
            }
            int count1 = finBondAssetService.isTransInAssetTwo(finprodId);
            if (count1 > 0){
                ValidMessage validMessage = new ValidMessage("存在流程中/待执行指令交易数据，不可删除");
                errorList.add(validMessage);
                res.setErrorList(errorList);
                res.setMessageType(MessageType.错误消息);
                return new Message<AjaxResult>().success().setData(res);
            }
        }
        //新增撤销校验，不允许删除上市债券  add by mashukai 20210425 end
        AjaxResult ajaxResult = commonCheck(result, dto.getOperateType());
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ApiOperation(value = "修改按钮点击时的check", notes = "修改按钮点击时的check")
    @ResponseBody
    @PostMapping("editCheck")
    public Message<AjaxResult> editCheck(@RequestBody FinBondAssetSaveRequest request){
        Map<String,Object> param = BeanUtil.object2Map(request);
        //判断是否存在交易
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        AjaxResult wr = new AjaxResult();
        if(null!=request.getSelectedItem()&&!request.getSelectedItem().isEmpty()){
            for(FinBondAssetSaveRequest entity : request.getSelectedItem()) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("FinBondAsset002");
                parameter.setBusinessDataPk1(entity.getFinprodMarketId());
                if (finBondAssetService.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("", getMessage("所选资产存在流程中的数据！,不可修改!")));
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
            }
            for(FinBondAssetSaveRequest entity : request.getSelectedItem()) {
                String finProdId = commonBusinessService.getFinProdId(entity.getFinprodMarketId(),DictCodeListExt1.金融产品类型_投管用_债券.getValue(),null);
                if(finBondAssetService.selectCountByfinprodId(finProdId)==0){
                    errorList.add(new ValidMessage("",
                            getMessage("该数据已被删除!")));
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
                int count1 = finBondAssetService.isTransInAssetTwo(finProdId);
                if (count1 > 0){
                    ValidMessage validMessage = new ValidMessage("存在流程中/待执行指令交易数据，不可修改");
                    errorList.add(validMessage);
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
            }
        }
        String isRightStr = request.getIsRight();
        if (CollectionUtils.isNotEmpty(errorList)){
            wr.setErrorList(errorList);
            return new Message<AjaxResult>().success().setData(wr);
        }
        if(null != commonBusinessService){
            for(FinBondAssetSaveRequest entity : request.getSelectedItem()) {
                String finProdId = commonBusinessService.getFinProdId(entity.getFinprodMarketId(), DictCodeListExt1.金融产品类型_投管用_债券.getValue(), null);
                if (finBondAssetService.isTransInAsset(finProdId) > 0 && (DictCodeList.是否_否.getCodeName().equals(isRightStr))) {
                    JoyinMessage ms = new JoyinMessage(getMessage("FIN_ASSET_W_0002"),
                            MessageType.确认消息);
                    joyinMessages.add(ms);
                    wr.setJoyinMessages(joyinMessages);
                    return new Message<AjaxResult>().success().setData(wr);
                } else {
                    return new Message<AjaxResult>().success().setData(wr);
                }
            }
            return new Message<AjaxResult>().success().setData(wr);
        }
        else{
            return new Message<AjaxResult>().success().setData(wr);
        }
        //区分含权债债券标的；含权债债券标的做完含权交易硬控不可改标的信息 add by yujinfa end
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
    @ApiOperation(value = "删除确认", notes = "删除确认")
    @ResponseBody
    @PostMapping(ACTION_DELETE)
    public Message<AjaxResult> delete(@RequestBody FinBondAssetSaveRequest dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
        }
        ProcessParameter<FinBondAssetSaveRequest> processParameter = new ProcessParameter<FinBondAssetSaveRequest>();
        WorkFlowParameter<FinBondAssetSaveRequest> params = new WorkFlowParameter<FinBondAssetSaveRequest>();
        FinBondAssetSaveRequest entity = new FinBondAssetSaveRequest();
        List<FinBondAssetSaveRequest> selectedItem = new ArrayList<>();
        entity.setElementId(dto.getElementId());
        for (FinBondAssetSaveRequest saveRequest:dto.getSelectedItem()){
            String finProdId = commonBusinessService.getFinProdId(saveRequest.getFinprodMarketId(), DictCodeListExt1.金融产品类型_投管用_债券.getValue(), null);
            FinBondAssetSaveRequest request = finBondAssetService.getBasicInfos(finProdId, null);
            selectedItem.add(request);
        }
        entity.setOperateType(OperationType.DELETE.name());
        entity.setSelectedItem(selectedItem);
        params.setPageId("FinBondAsset002");
        params.setElementId(entity.getElementId());
        params.setBusinessCode(FinBondAssetSaveRequest::getFinprodMarketId);
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity1 -> {
            return WorkFlowUtil.concatComment("债券代码"+
                GlobalConstant.OPEN_PARENTHESIS+entity1.getFinprodMarketId()+GlobalConstant.CLOSE_PARENTHESIS
            +"债券简称"+GlobalConstant.OPEN_PARENTHESIS+entity1.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setOperationType(OperationType.DELETE);
        processParameter.setWorkFlowParameter(params);
        String msg = finBondAssetService.start(processParameter,
                entity.getSelectedItem().toArray(new FinBondAssetSaveRequest[entity.getSelectedItem().size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }
    
    /**
     * 导出
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="导出债券标的信息",notes = "导出债券标的信息")
    @ResponseBody
    @PostMapping("exportAssetInfo")
    public void exportAssetInfo(@RequestBody FinBondAssetExportRequest requestDto, HttpServletResponse response){
        // 导出
        ExcelUitl.doExport(requestDto, response, "债券标的", () -> {
            FinBondAssetRequest dto = requestDto.getDto(FinBondAssetRequest.class);
            List<FinBondAssetResponse> dataList = finBondAsset001Service.selectForPage(dto).getRoot();
            for(FinBondAssetResponse data:dataList){
                finBondAssetService.updateCfetsOprHis(data.getFinprodId(),"export","");
            }
            return dataList;
        });
    }

    /**
     * 操作记录查询
     *
     * @param dto
     * @return Page<FieldEtrInfoOperateListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "操作记录查询", notes = "操作记录查询")
    @PostMapping("/getSearchTabInfoQueryOperate")
    @ResponseBody
    public Message<Page<SearchInfoOperateListResponse>> getSearchTabInfoQueryOperate(@RequestBody SearchOperateListRequest dto){
        try {
            Page<SearchInfoOperateListResponse> page = finBondAsset001Service.getSearchTabInfoQueryOperate(dto);
            return new Message<Page<SearchInfoOperateListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询操作记录分页数据异常", e);
            return new Message<Page<SearchInfoOperateListResponse>>().fail().setMessage("查询操作记录分页数据异常");
        }
    }

    @PostMapping("/test")
    @ResponseBody
    public Message test(@RequestBody BaseMap param) {
       /* BaseMap param = BaseMap.newMap().put("interfaceId","Bond001")
               .put("busiAct","01")
                .put("ps","T02")
                .put("tradePlace","01")
                .put("decisionAct","01")
                .put("flowStatus","01")
                .put("busiType","02");*/
        return Message.success("",rskNodeConfigService.getNodeConfig(param));
    }
}
