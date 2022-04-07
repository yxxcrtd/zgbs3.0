/*
 * 文件名：ValBatchLog001Controller.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管业务监管报送平台
 * 修改人：ww1
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管业务监管报送平台
 */
 
package com.joyintech.tams.valuation.system.log.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.annotation.LogMethod;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.system.constant.SystemDictCode;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.valuation.system.log.dto.ValBatchLog001Dto;
import com.joyintech.tams.valuation.system.log.service.ValBatchLog001Service;


/**
 * 批处理日志controller
 * 
 * @author ww1
 * @version 1.0 2019年2月21日
 * @see ValBatchLog001Controller
 * @since 1.0
 */
@Controller
@PageId("ValBatchLog001")
@RequestMapping("/valuation/system/log/ValBatchLog001")
public class ValBatchLog001Controller extends BaseController {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    @Autowired
    private ValBatchLog001Service valBatchLog001Service;
  
    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, SystemDictCode.执行状态);
        return super.init(model);
    }

    /**
     * 查询任务组
     * 
     * @return List<SelectOptionDto>
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "getTaskGroup", method = RequestMethod.GET)
    public List<SuggestDto> getTaskGroup(String param){
        return valBatchLog001Service.getTaskGroup(param);
    }

    /**
     * 查询任务
     * 
     * @return List<SelectOptionDto>
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "getTask", method = RequestMethod.GET)
    public List<SuggestDto> getTask(String param){
        return valBatchLog001Service.getTask(param);
    }

    /**
     * 日志详情
     *
     * @return List<SelectOptionDto>
     * @throws Exception
     */
    @LogMethod(action="init",pageId="ValBatchLog003")
    @RequestMapping(value = "ValBatchLog003", method = RequestMethod.GET)
    public String valBatchLog003(String logSeqNo, Model model){
        ValBatchLog001Dto log = valBatchLog001Service.findLogDetail(logSeqNo);
        model.addAttribute("log", log);
        return "/valuation/system/log/ValBatchLog003";
    }
    

    /**
     * 查询产品组
     * 
     * @return List<SelectOptionDto>
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "getPrdGroup", method = RequestMethod.GET)
    public List<SuggestDto> getPrdGroup(String param){
        return valBatchLog001Service.getPrdGroup(param);
    }

    /**
     * 查询日志
     * 
     * @return 日志page
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<ValBatchLog001Dto> selectBatchLog() {
        return valBatchLog001Service.selectForPage(RequestUtil.getPageParam());
    }
    
    /**
     * 导出信息excel
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping(ACTION_EXPORT)
    public ResponseEntity<byte[]> export() {
        List<ValBatchLog001Dto> dtoList = valBatchLog001Service.selectForExport(RequestUtil.getPageParam());
        File outFile = valBatchLog001Service.makeFile(
            "/com/joyintech/system/log/conf/export_batchlog.xml", dtoList);
        return ExportUtil.export(
            this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
                                 + DateUtil.format(new Date()) + SystemGlobalConstant.CLOSE_PARENTHESIS
                                 + SystemGlobalConstant.POINT + FileNameUtil.getFileExtName(outFile),
            outFile, true);
    }
}
