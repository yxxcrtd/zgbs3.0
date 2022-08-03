/*
 * 文件名：EtrInvestAllInfoController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrinvestallinfo.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoExportPdfDto;
import com.joyintech.tams.business.common.pojo.Message;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoDto;
import com.joyintech.service.entrust.etrinvestallinfo.service.EtrInvestAllInfoService;
/**
 * 
 * 所有投资委托信息微服务controller入口
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月10日
 * @see EtrInvestAllInfoController
 * @since 1.0
 */
@RestController
@RequestMapping("/EtrInvestAllInfo")
public class EtrInvestAllInfoController {
    
    /**
     * 所有投资委托微服务service
     */
    @Autowired
    private EtrInvestAllInfoService etrInvestAllInfoService;
    /**
     * 
     * 所有投资委托分页查询
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<EtrInvestAllInfoDto> selectForPage(@RequestBody Map<String, Object> param, @PathVariable("page") int page, @PathVariable("length") int length) 
    {
        Page<EtrInvestAllInfoDto> result = etrInvestAllInfoService.selectForPage(
            page, length, param);
        return result;
    }

    @ApiOperation(value = "获取债券基本信息", notes = "获取债券基本信息")
    @ResponseBody
    @PostMapping("/getById")
    public EtrInvestAllInfoDto getById(@RequestBody Map<String, Object> param){
        EtrInvestAllInfoDto resultDto = etrInvestAllInfoService.getById(param);
        return resultDto;
    }

    /**
     * 
     * 获取组合代码联想文本框信息
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @GetMapping("getentrustId")
    public List<SuggestDto> getentrustId(String entrustId)
    {
        List<SuggestDto> temp = etrInvestAllInfoService.getentrustId(entrustId);
        return temp;
    }
    /**
     * 
     * 获取资产代码联想文本框信息
     * <功能详细描述>
     * @return 
     * @throws Exception
     * @see
     * @since 1.0
     */
    @GetMapping("getfinprodId")
    public List<SuggestDto> getfinprodId(String finprodId)
    {
        List<SuggestDto> temp = etrInvestAllInfoService.getfinprodId(finprodId);
        return temp;
    }
    /**
     * 
     * 查询PB导出信息 add by caoxue 2019-12-31
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("selectExportPB")
    public List<EtrInvestAllInfoDto> selectExportPB(@RequestBody Map<String, Object> param) 
    {
    	List<EtrInvestAllInfoDto> result = etrInvestAllInfoService.selectExportPB( param);
        return result;
    }

    /**
     * 查询导出pdf数据 add by zengchengfa 20210730
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("selectExportPdf")
    public List<EtrInvestAllInfoExportPdfDto> selectExportPdf(@RequestBody Map<String, Object> param)
            throws Exception {
        List<EtrInvestAllInfoExportPdfDto> result = etrInvestAllInfoService.selectExportPdf(param);
        return result;
    }
}
