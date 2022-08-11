package com.joyintech.tams.invest.settle.dvporder.controller;

import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.grid.Page;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.settle.dvporder.DvpOrdersDto;
import com.joyintech.tams.invest.settle.dvporder.dto.DvpOrdersManagement001Dto;
import com.joyintech.tams.invest.settle.dvporder.dto.DvpOrdersProcessDto;
import com.joyintech.tams.invest.settle.dvporder.service.DvpOrdersManagement001Service;
import com.joyintech.tams.invest.settle.dvporder.service.DvpOrdersManagement002Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * @program: tams-invest-parent
 * @description: DVP指令管理
 * @author: 强成西
 * @create: 2018-11-15 11:28
 **/
@Controller
@PageId("DvpOrdersManagement001")
@RequestMapping("/invest/settle/dvporder/DvpOrdersManagement001")
public class DvpOrdersManagement001Controller extends BaseController {

    /**
     * 需要初始化的下拉菜单
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.登记托管机构, DictCode.平安业务类型,
        DictCode.处理状态, DictCode.成功失败标志};

    @Autowired
    private DvpOrdersManagement001Service dvpOrdersManagement001Service;
    
    @Autowired
    private DvpOrdersManagement002Service dvpOrdersManagement002Service;
    

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        model.addAttribute("DISPOSE_STATUS_01", DictCodeList.处理状态_已处理.getValue());
        model.addAttribute("PAGE_STATUS_05", DictCodeListExt1.画面数据状态_有效.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_09", DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_10", DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_11", DictCodeListExt1.平安业务类型_债券类资产行权.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_19", DictCodeListExt1.平安业务类型_逆回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_20", DictCodeListExt1.平安业务类型_正回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_19", DictCodeListExt1.平安业务类型_逆回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_52", DictCodeListExt1.平安业务类型_正回购到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_51", DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_74", DictCodeListExt1.平安业务类型_买断式正回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_75", DictCodeListExt1.平安业务类型_买断式逆回购.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_77", DictCodeListExt1.平安业务类型_买断式逆回购到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_76", DictCodeListExt1.平安业务类型_买断式正回购到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_107", DictCodeListExt2.平安业务类型_债券借贷融入到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_108", DictCodeListExt2.平安业务类型_债券借贷融出到期.getValue());
        model.addAttribute("PINAN_BUSINESS_TYPE_81", BussinessDictCodeList.平安业务类型_DVP划款.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F01", DictCodeList.平安资产类型_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F32", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        return super.init(model);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("findTransferTableData")
    public Page<DvpOrdersManagement001Dto> findTransferTableData() {
        return dvpOrdersManagement001Service.findTransferTableData(RequestUtil.getPageParam());
    }

    /**
     * 分页查询
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("findDvpTransferTableData")
    public Page<DvpOrdersManagement001Dto> findDvpTransferTableData() {
    	Page<DvpOrdersManagement001Dto> findDvpTransferTableData = dvpOrdersManagement001Service.findDvpTransferTableData(RequestUtil.getPageParam());
        return findDvpTransferTableData;
    }

    /**
     * 打印划款单
     *
     * @return
     */
    @RequestMapping("printPayOrders")
    public String printPayOrders(Model model) {
        model.addAttribute(GlobalConstant.ENTITY, dvpOrdersManagement001Service.printPayOrders(RequestUtil.getPageParam()));
        return "invest/settle/dvporder/DvpPrint";
    }

    /**
     * 导出
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export() throws IOException {
        Map<String, Object> searchParams = RequestUtil.getPageParam();
        String datas = this.getParam("datas");

        String fileName = "划款信息查询" + "(" + DateUtil.getCurrentDate() + ").xls";
        if (searchParams.get("isDvp") != null && Boolean.parseBoolean((String)searchParams.get("isDvp"))) {
            fileName = "DVP清算划款" + "(" + DateUtil.getCurrentDate() + ").xls";
        }
        if(datas==null) {
            return dvpOrdersManagement001Service.export(fileName, RequestUtil.getParameters(getRequest()));
        }

        if (searchParams.get("isDvp") != null && Boolean.parseBoolean((String)searchParams.get("isDvp"))) {
            List<DvpOrdersManagement001Dto> param1 = JsonUtils.json2List(datas, DvpOrdersManagement001Dto.class);
            File outFile = dvpOrdersManagement001Service.makeFile(
                    "/com/joyintech/tams/invest/settle/dvporder/config/export_confDvp.xml",
                    param1);
            return ExportUtil.export(
                    "DVP清算划款" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);
        }else{
            List<DvpOrdersManagement001Dto> param1 = JsonUtils.json2List(datas, DvpOrdersManagement001Dto.class);
            File outFile = dvpOrdersManagement001Service.makeFile(
                    "/com/joyintech/tams/invest/settle/dvporder/config/export_conf.xml",
                    param1);
            return ExportUtil.export(
                    "划款信息查询" + SystemGlobalConstant.OPEN_PARENTHESIS
                            + DateUtil.format(DateUtil.platDateFromCache())
                            + SystemGlobalConstant.CLOSE_PARENTHESIS
                            + SystemGlobalConstant.POINT
                            + FileNameUtil.getFileExtName(outFile),
                    outFile, true);

        }
    }
    
    /**
     * 撤销前check add by libaisong 2019-10-14
     *
     * @param inputDto 画面输入参数dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @RequestMapping("checkRevoke")
    @ResponseBody
    public AjaxResult check(@Valid @RequestBody DvpOrdersProcessDto inputDto,
                            BindingResult result) {
        return commonCheck(result, inputDto.getOperateType());
    }
    
    /**
     * 撤销
     * @param inputDto 画面输入参数dto
     * @return 撤销结果
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("doRevoke")
    @ResponseBody
    public AjaxResult doRevoke(@RequestBody DvpOrdersProcessDto inputDto) throws WorkFlowException {

        ProcessParameter<DvpOrdersProcessDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<DvpOrdersProcessDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setPageId("DvpOrdersManagement002");
        processParameter.setOperationType(OperationType.REVOKE);

        workFlowParameter.setPageId("DvpOrdersManagement002");
        workFlowParameter.setElementId(inputDto.getElementId());
        workFlowParameter.setBusinessCode(inputDto.getOrderId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_DVP清算指令管理.getValue());
        workFlowParameter.setComment(entity -> WorkFlowUtil.concatComment(
        		"DVP清算指令撤销, 指令编号：" + inputDto.getOrderId()));
        processParameter.setWorkFlowParameter(workFlowParameter);
        String msg = dvpOrdersManagement002Service.start(processParameter, inputDto);

        return new AjaxResult(msg);
    }
    /**
     * 双击查看时，对勾选数据进行检查
     *
     * @param
     * @param
     * @return check结果
     * @see
     * @since 1.0
     */
    @RequestMapping("checkCreatOrder")
    @ResponseBody
    public AjaxResult checkCreatOrder(@Valid @RequestBody List<String> bizCodes,
                            BindingResult br) {
        boolean isInTrade=true;
        if(StringUtil.isNotEmpty(bizCodes) && bizCodes.size()>0){
            for(String bizCode:bizCodes){
                Map<String, Object> params=new HashMap<String, Object>();
                params.put("bizCode",(Object) bizCode);
                List<DvpOrdersManagement001Dto> list1 =  dvpOrdersManagement001Service.findDvpTransferTableData(params).getRoot();
                for (DvpOrdersManagement001Dto dto:list1){
                    if(StringUtil.isNotEmpty(dto.getDealStatus()) && "01".equals(dto.getDealStatus())){
                        isInTrade=false;
                    }
                }
            }
        }
        if(!isInTrade){
            AjaxResult result =new AjaxResult();
            // 提示信息检查
            List<ValidMessage> validMessages = new ArrayList<>();
            validMessages.add(
                    new ValidMessage(this.getMessage("已处理的交易数据无法再进行指令生成。")));
            result.setErrorList(validMessages);
            return result;
        }else{
            return new AjaxResult();
        }

    }
}
