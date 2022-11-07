/*
 * 文件名：PtlPositionAnalysisController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wanghb
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlpositionanalysis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis003Dto;
import com.joyintech.service.portfolio.ptlpositionanalysis.service.PtlPositionReportService;
import com.joyintech.tams.common.base.entity.TargetDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis001Dto;
import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis002Dto;
import com.joyintech.service.portfolio.ptlpositionanalysis.service.PtlPositionAnalysisService;
import com.joyintech.service.portfolio.ptlsingleassetpositionanalysis.dto.PtlSingleAssetPositionAnalysis001Dto;

/**
 * 组合持仓分析
 * @author wanghb
 * @version 1.0 2019年1月25日
 * @see PtlPositionAnalysisController
 * @since 1.0
 */
@RestController
@RequestMapping("/PtlPositionAnalysis")
public class PtlPositionAnalysisController {
    /**
     * 计划类资产（净值型）Service
     */
    @Autowired
    private PtlPositionAnalysisService ptlPositionAnalysisService;
    @Autowired
    private PtlPositionReportService ptlPositionReportService;

    /**
     * 组合持仓明细查询
     * @param param 条件
     * @param page 第几页
     * @param length 长度
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<PtlPositionAnalysis001Dto> selectAll(@RequestBody Map<String, Object> param,
                                                                @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return ptlPositionAnalysisService.selectForPage(page,length,param);
    }
    /**
     * 组合持仓明细查询
     * @param param 条件
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("findList")
    public List<PtlPositionAnalysis001Dto> selectAllData(@RequestBody Map<String, Object> param)
    {
        return ptlPositionAnalysisService.selectAllData(param);
    }
    /**
     * 组合持仓汇总查询
     * @param param 条件
     * @param page 第几页
     * @param length 长度
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
   /* @PostMapping("findPageForAsset/{page}/{length}")
    public Page<PtlPositionAnalysis002Dto> selectAllAsset(@RequestBody Map<String, Object> param,
                                                                @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return ptlPositionAnalysisService.selectAssetForPage(page,length,param);
    }*/
    /**
     * 组合持仓汇总查询
     * @param param 条件
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("findListForAsset")
    public List<PtlPositionAnalysis002Dto> findListForAsset(@RequestBody Map<String, Object> param)
    {
        return ptlPositionAnalysisService.selectAssetForList(param);
    }
    /**
     * 组合的联想下拉框事件
     * @param param 输入参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @RequestMapping("getPortFolioInfo")
    public List<SelectOptionDto> getPortFolioInfo(@RequestBody Map<String,Object> param){
        return ptlPositionAnalysisService.getPortFolioInfo(param);
    }

    /**
     * 组合的联想下拉框事件
     * @param param 输入参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @RequestMapping("getPortFolioInfoWW")
    public List<SelectOptionDto> getPortFolioInfoWW(@RequestBody Map<String,Object> param){
        return ptlPositionAnalysisService.getPortFolioInfoWW(param);
    }
    
    /**
     * 导出查询方法
     *
     * @param param 画面参数
     * @param page 页码数
     * @param length 页面长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPageForExport/{page}/{length}")
    public Page<PtlPositionAnalysis001Dto> selectForExport(@RequestBody Map<String, Object> param,
            @PathVariable("page") int page, @PathVariable("length") int length)
    {
    	return ptlPositionAnalysisService.selectForExport(page, length, param);	
    }
    
    /**
     * 导出查询方法
     *
     * @param param 画面参数
     * @param page 页码数
     * @param length 页面长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPageForExportNew/{page}/{length}")
    public Page<PtlPositionAnalysis001Dto> selectForExportNew(@RequestBody Map<String, Object> param,
            @PathVariable("page") int page, @PathVariable("length") int length)
    {
    	return ptlPositionAnalysisService.selectForExportNew(page, length, param);	
    }

    /**
     * 获取动态列-》组合持仓分析报表
     * @param param 输入参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getDataTableCols")
    public List<TargetDetail> getDataTableCols(@RequestBody String param){
        return ptlPositionAnalysisService.getDataTableCols(param);
    }
    @PostMapping("getDefaultTableCols")
    public List<TargetDetail> getDefaultTableCols(@RequestBody String param){
        return ptlPositionAnalysisService.getDefaultTableCols(param);
    }
    @PostMapping("getTableColumn")
    public List<TargetDetail> getTableColumn(){
        List<TargetDetail> tableColumn = ptlPositionAnalysisService.getTableColumn();
        return tableColumn;
    }

    /**
     * 指标设置树结构数据-》组合持仓分析报表
     * @param param 输入参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getAllTreeNode")
    public List<TargetDetail> getAllTreeNode(@RequestBody Map<String, Object> param){
        return ptlPositionAnalysisService.getAllTreeNode(param);
    }

    /**
     * 组合持仓分析报表查询
     * @param param 条件
     * @param page 第几页
     * @param length 长度
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("secReportPage/{page}/{length}")
    public Page<PtlPositionAnalysis003Dto> secReportPage(@RequestBody Map<String, Object> param,
                                                         @PathVariable("page") int page, @PathVariable("length") int length)
    {

        return ptlPositionReportService.secReportPage(page,length,param);
    }

    /**
     * 根据groupby 条件获取对象下的分类
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("getFinpordType")
    public List<PtlPositionAnalysis003Dto> getFinpordType(@RequestBody  Map<String, Object> pageParam) {
        return ptlPositionAnalysisService.getFinpordType(pageParam);
    }

    /**
     * 查询组合对应的剩余现金
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("selectRemain")
    public List<PtlPositionAnalysis003Dto> selectRemain(@RequestBody  Map<String, Object> pageParam) {
        return ptlPositionAnalysisService.selectRemain(pageParam);
    }

    /**
     * 查询组合对应的剩余现金
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @PostMapping("selectPeiZhiRemain")
    public List<PtlPositionAnalysis002Dto> selectPeiZhiRemain(@RequestBody  Map<String, Object> pageParam) {
        return ptlPositionAnalysisService.selectPeiZhiRemain(pageParam);
    }
}
