package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoDto;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service.MskCreditMarketInfoPro001Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;


@Controller
@PageId("MstCreditMarketInfo001")
@RequestMapping("/exchange/basicdata/bussiness/mskcreditinfo/MstCreditMarketInfo001")
@Api(tags = "信用保护凭证行情信息")
public class MskCreditMarketInfo001Controller extends BaseController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MskCreditMarketInfoPro001Service mskCreditMarketInfoPro001Service;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 入口
     */
    @ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String,Object>> init() {
        Map<String, Object> model = new HashMap<>();
        model.put("VALUE_SOURCE",this.getSelectDataFromDataDic(DictCode.估值来源, null));
        model.put("CREDIT_TYPE",this.getSelectDataFromDataDic(DictCode.信用类型, null));
        model.put("iPlatformDate", iPlatformDate.getSysDateStr());
        //刷新时间
        model.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        return new Message<Map<String, Object>>(model).success();
    }


    /**
     * 初始化查询
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="信用保护凭证行情信息",notes = "信用保护凭证行情信息")
    @ResponseBody
    @PostMapping("findPage")
    public Message<Page<MskCreditInfoProSaveRequest>> findPage(@RequestBody MskCreditInfoProRequest request) {
        try {
            Page<MskCreditInfoProSaveRequest> page = mskCreditMarketInfoPro001Service.selectForPage(request);
            return new Message<Page<MskCreditInfoProSaveRequest>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询信用保护凭证行情信息分页数据异常",e);
            return new Message<Page<MskCreditInfoProSaveRequest>>().fail().setMessage("信用保护凭证行情信息");
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
  /*  @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) MskCreditInfoProSaveRequest dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }*/


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
    public Message<AjaxResult> check(@RequestBody MskCreditInfoProSaveRequest dto, BindingResult result) {
        AjaxResult res= new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        Date platDate = DateUtil.platDateFromCache();
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()) {
            for (MskCreditInfoProSaveRequest entity : dto.getSelectedItem()) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("MstCreditMarketInfo002");
                parameter.setBusinessDataPk1(entity.getSecid());
                if (mskCreditMarketInfoPro001Service.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("", getMessage("所选信用保护凭证存在流程中的数据！,不可删除!")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }
                if (!(DateUtil.format(platDate)).equals(entity.getCdate())) {
                    errorList.add(new ValidMessage(this.getMessage("只可删除估值日期等于平台日的数据")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }
                if(mskCreditMarketInfoPro001Service.selectByKey(entity.getSecid(), entity.getCdate(),entity.getValueSource()) == null){
                    errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0022")));
                    res.setErrorList(errorList);
                    res.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(res);
                }
            }
        }
        return new Message<AjaxResult>().success().setData(res);
    }

    @ApiOperation(value = "修改按钮点击时的check", notes = "修改按钮点击时的check")
    @ResponseBody
    @PostMapping("editCheck")
    public Message<AjaxResult> editCheck(@RequestBody MskCreditInfoProSaveRequest request){
        Map<String,Object> param = BeanUtil.object2Map(request);
        List<ValidMessage> errorList = new ArrayList<ValidMessage>();
        AjaxResult wr = new AjaxResult();
        Date platDate = DateUtil.platDateFromCache();
//        if(null!=request.getSelectedItem()&&!request.getSelectedItem().isEmpty()){
//            for(MskCreditInfoProSaveRequest entity : request.getSelectedItem()) {
                WflValidInputParameter parameter = new WflValidInputParameter();
                parameter.setPageId("MstCreditMarketInfo002");
                parameter.setBusinessDataPk1((String) param.get("secid"));
                if (mskCreditMarketInfoPro001Service.isInWflProcess(parameter)) {
                    errorList.add(new ValidMessage("", getMessage("所选信用保护凭证存在流程中的数据！,不可修改!")));
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
                if (!(DateUtil.format(platDate)).equals(param.get("cdate"))) {
                    errorList.add(new ValidMessage(this.getMessage("只可修改估值日期等于平台日的数据")));
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
                if(mskCreditMarketInfoPro001Service.selectByKey((String) param.get("secid"), (String)param.get("cdate"),(String)param.get("valueSource")) == null){
                    errorList.add(new ValidMessage(this.getMessage("BUSS_COM_E_0022")));
                    wr.setErrorList(errorList);
                    wr.setMessageType(MessageType.错误消息);
                    return new Message<AjaxResult>().success().setData(wr);
                }
           // }
       // }
        return new Message<AjaxResult>().success().setData(wr);
    }


    /**
     *  删除
     * @param dto 页面选中的数据
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
            ValidateGroupDelete.class}) MskCreditInfoProSaveRequest dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<MskCreditInfoProSaveRequest> processParameter = new ProcessParameter<MskCreditInfoProSaveRequest>();
        WorkFlowParameter<MskCreditInfoProSaveRequest> params = new WorkFlowParameter<MskCreditInfoProSaveRequest>();
        params.setPageId("MstCreditMarketInfo002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getSecid());
        params.setBusinessType(DictCodeList.共通业务类型_信用保护凭证行情.getValue());
        // 设置流程业务说明, entity即传入的dto
        // 业务说明：债券代码 + "(" + 债券代码 + ")" + "-" + "估值日" + "-" + "估值日"
        params.setComment(entity -> SpringContextUtil.getTextValue("MstCreditMarketInfo001.secid") + "(" + entity.getSecid() + ")" + "-"
                + SpringContextUtil.getTextValue("MstCreditMarketInfo001.cdate") + "("
                + entity.getCdate() + ")"+ this.getMessage("MstCreditMarketInfo001.valueSource") + "("
                + entity.getValueSourceStr() + ")");
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.DELETE);
        String msg = mskCreditMarketInfoPro001Service.start(processParameter,
                dto.getSelectedItem().toArray(
                        new MskCreditInfoProSaveRequest[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);

    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
/*
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
            throws Exception {
        *//*String datas = this.getParam("datas");
        Map<String, Object> params= RequestUtil.getPageParam();
        List<MskCreditInfoProSaveRequest> list = null;
        if (StringUtil.isNotEmpty(datas)) {
            list = JsonUtils.json2List(datas, MskCreditInfoProSaveRequest.class);
        }
        else {
            list = mskCreditMarketInfoPro001Service.selectAllList(params);
        }*//*
       *//* File outFile = mskCreditMarketInfoPro001Service.makeFile(
                "/com/joyintech/tams/common/basicdata/business/mskcreditinfo/conf/export_conf.xml",
                list);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(new Date())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);*//*

        //}
    }*/


}
