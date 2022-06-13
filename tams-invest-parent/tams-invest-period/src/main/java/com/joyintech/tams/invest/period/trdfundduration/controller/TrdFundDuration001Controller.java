/*
 * 文件名：TrdFundDuration001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdfundduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdfundduration.dto.TrdFundDuration001Dto;
import com.joyintech.tams.invest.period.trdfundduration.service.TrdFundDuration001Service;
import com.joyintech.tams.invest.period.trdfundobversion.dto.TrdFundObversionDto;
import com.joyintech.tams.invest.period.trdfundobversion.service.TrdFundObversion001Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存续期基金分红
 * 
 * @author dingzhipeng
 * @version 1.0 2018年10月15日
 * @see TrdFundDuration001Controller
 * @since 1.0
 */
@PageId("TrdFundDuration001")
@RequestMapping("/invest/period/trdfundduration/TrdFundDuration001")
@Controller
public class TrdFundDuration001Controller extends BaseController {
    /**
     * 基金存续期service
     */
    @Autowired
    private TrdFundDuration001Service trdFundDuration001Service;

    @Autowired
    private TrdFundObversion001Service trdFundObversion001Service;
    /**
     * PeriodCommonService
     */
    @Autowired
    private PeriodCommonService periodCommonService;
    @Autowired
    private CommonExportService commonExportService;
    /**
     * 画面初始化
     */
    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, BussinessDictCode.画面数据状态,BussinessDictCode.基金类资产类型
                ,BussinessDictCode.分红方式,DictCode.折算类型);
        model.addAttribute("BONUS_MODE_02", BussinessDictCodeList.分红方式_红利再投.getValue());
        model.addAttribute("BONUS_MODE_01", BussinessDictCodeList.分红方式_现金分红.getValue());
        model.addAttribute("PS_T11",BussinessDictCodeList.交易方向_现金分红.getValue());
        model.addAttribute("PS_T12",BussinessDictCodeList.交易方向_红利再投.getValue());
        model.addAttribute("PS_T13", BussinessDictCodeList.交易方向_除权登记.getValue());
        model.addAttribute("F01", BussinessDictCodeList.基金类资产类型_货币基金.getValue());
        model.addAttribute("PAGE_DATA_STATUS_05", BussinessDictCodeList.画面数据状态_有效.getValue());
        model.addAttribute("AUTHORITY_TYPE", CacheUtil.getSysParam("AUTHORITY_TYPE"));
        model.addAttribute("ISChongQing", CacheUtil.getSysParam("ISChongQing"));
        return super.init(model);
    }
    /**
     * 获取所有组合代码
     *
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public Page<SelectOptionDto> getPortfolioId() {
        return trdFundDuration001Service.getPortfolioId();
    }
    /**
     * 分页查询 市场代码
     *
     * @return 下拉框集合
     */
    @ResponseBody
    @RequestMapping("getFinprodMarketId")
    public Page<SelectOptionDto> getFinprodMarketId() {
        return trdFundDuration001Service.getFinprodMarketId();
    }
    /**
     * 分页查询 有效、撤销或待处理的债券存续期交易数据
     *
     * @return 查询结果
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<TrdFundDuration001Dto> findPage() {
        return trdFundDuration001Service.findPage(RequestUtil.getPageParam());
    }

    @ResponseBody
    @RequestMapping("findPageObversion")
    public Page<TrdFundObversionDto> findPageObversion() {
        return trdFundObversion001Service.findPage(RequestUtil.getPageParam());
    }
    
    /**
     * 导出
     *
     * @return 导出的文件流
     * @throws Exception 文件到处异常
     * @since 1.0
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export() throws IOException {
        String datas = this.getParam("datas");
        if(datas==null) {
            return trdFundDuration001Service.export(this.getPageId(),
                    RequestUtil.getParameters(getRequest()));
        }
        List<TrdFundDuration001Dto> param1 = JsonUtils.json2List(datas, TrdFundDuration001Dto.class);

        String configxml = "";
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            configxml = "/com/joyintech/tams/invest/period/trdfundduration/conf/export_conf_ls.xml";
        }else{
            configxml = "/com/joyintech/tams/invest/period/trdfundduration/conf/export_conf.xml";
        }

        File outFile =  commonExportService.makeFile( configxml,param1);
        return ExportUtil.export(
                this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(DateUtil.platDateFromCache())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);


    }

    @RequestMapping("exportConversion")
    public ResponseEntity<byte[]> exportConversion() throws IOException {
        String datas = this.getParam("datas");
        if(datas==null) {
            return trdFundObversion001Service.export("TrdFundObversion001",
                    RequestUtil.getParameters(getRequest()));
        }

        List<TrdFundObversionDto> param1 = JsonUtils.json2List(datas, TrdFundObversionDto.class);

        String configxml = "";
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            configxml = "/com/joyintech/tams/invest/period/trdfundobversion/conf/export_conf_ls.xml";
        }else{
            configxml = "/com/joyintech/tams/invest/period/trdfundobversion/conf/export_conf.xml";
        }

        File outFile =  trdFundObversion001Service.makeFile( configxml,param1);
        return ExportUtil.export(
                this.getMessage("TrdFundObversion001") + SystemGlobalConstant.OPEN_PARENTHESIS
                        + DateUtil.format(DateUtil.platDateFromCache())
                        + SystemGlobalConstant.CLOSE_PARENTHESIS
                        + SystemGlobalConstant.POINT
                        + FileNameUtil.getFileExtName(outFile),
                outFile, true);

    }
    /**
     * 撤销
     *
     * @return 撤销的入口
     */
    @ResponseBody
    @RequestMapping("revoke")
    public AjaxResult revoke(@RequestBody @Validated({
            ValidateGroupDelete.class}) TrdFundDuration001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("statusValid", dto.getStatusValid());
        paramMap.put("totleTradeId", dto.getTotleTradeId());
//        构建撤销dto
//        查询有效的数据
        TrdFundDuration001Dto temp = trdFundDuration001Service.findEffectiveData(paramMap);
        paramMap.put("busiCode", dto.getTotleTradeId());
        temp.setBussTrdTransferOrder(periodCommonService.selectPayOrders(temp.getTotleTradeId()));
        paramMap.put("registDate", DateUtil.format(temp.getRegistDate()));
        temp.setFinAssetIntInfoList(trdFundDuration001Service.findAssetIntInfo(paramMap));
        temp.setBaseTotalTradeInfoDto(trdFundDuration001Service.genFinProductTradeDto(temp));
        //        构建流程参数
        ProcessParameter<TrdFundDuration001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdFundDuration001Dto> params = new WorkFlowParameter<>();
        params.setPageId("TrdFundDuration002");
        params.setElementId(dto.getElementId());
//        模式为撤销
        temp.setMode(OperationType.REVOKE.toString());
        temp.setpTradeId(temp.getTotleTradeId());
        temp.setTotleTradeId(temp.getTotleTradeId()+'R');
        params.setBusinessCode(temp.getTotleTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_基金类资产存续期.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment(
                this.getMessage("TrdDepoDuration001.finprodName"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getFinprodAbbr()
                        + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDuration001.assetType"),
                GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(
                        DictCode.基金类资产类型, entity.getAssetType()) + GlobalConstant.CLOSE_PARENTHESIS,
                this.getMessage("TrdFundDuration001.tradeId"),
                GlobalConstant.OPEN_PARENTHESIS + entity.getTotleTradeId()
                        + GlobalConstant.CLOSE_PARENTHESIS));
        processParameter.setWorkFlowParameter(params);
        if(StringUtil.isEmpty(temp.getFinprodMarketName())){
            temp.setFinprodMarketName(temp.getFinprodAbbr());
        }
        String msg = trdFundDuration001Service.start(processParameter, temp);
        return new AjaxResult(msg);
    }
    /**
     * 撤销check
     *
     * @return 返回check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) TrdFundDuration001Dto dto, BindingResult result) {
        if (StringUtil.equals(dto.getOperateType(), "revoke")) {
            if (periodCommonService.isAlreadyRevoked(dto.getTotleTradeId())) {
                ValidMessage validMessage = new ValidMessage(getMessage("TRD_W_0001",
                        getMessage("pingan.common.revoke"), getMessage("pingan.common.revoke")));
                ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
                errorList.add(validMessage);
                AjaxResult ar = new AjaxResult();
                ar.setErrorList(errorList);
                return ar;
            }
        }
        return commonCheck(result, dto.getOperateType());
    }
}
