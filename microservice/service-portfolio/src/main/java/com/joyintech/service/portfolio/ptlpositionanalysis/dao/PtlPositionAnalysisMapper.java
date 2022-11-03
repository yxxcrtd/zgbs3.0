/*
 * 文件名：PtlPositionAnalysisMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wanghb
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlpositionanalysis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis003Dto;
import com.joyintech.tams.common.base.entity.TargetDetail;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis001Dto;
import com.joyintech.service.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis002Dto;
import com.joyintech.service.portfolio.ptlsingleassetpositionanalysis.dto.PtlSingleAssetPositionAnalysis001Dto;


/**
 * 组合持仓分析
 * @author wanghb
 * @version 1.0 2019年1月25日
 * @see PtlPositionAnalysisMapper
 * @since 1.0
 */
@Repository
public interface PtlPositionAnalysisMapper {
    /**
     *组合持仓明细查询
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis001Dto> select(Map<String, Object> pageParam);
    /**
     *组合持仓明细查询
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis001Dto> selectAll(Map<String, Object> pageParam);
    /**
     * 组合持仓汇总查询
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis002Dto> selectAsset(Map<String, Object> pageParam);
    /**
     * 组合持仓汇总查询-穿透
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis002Dto> selectAssetPierce(Map<String, Object> pageParam);
    /**
     * 组合的联想下拉框事件
     * @param map 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getPortFolioInfo(Map<String, Object> map);

    /**
     * 组合的联想下拉框事件
     * @param map 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getPortFolioInfoWW(Map<String, Object> map);
    /**
     * 查询界面导出事件
     *
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis001Dto> selectForExport(Map<String, Object> pageParam);

    /**
     * 获取动态列-》组合持仓分析报表
     * @param map 输入参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    List<TargetDetail> getDataTableCols(Map<String, Object> map);

    // 获取默认显示列
    List<TargetDetail> getDefaultTableCols(Map<String, Object> map);

    /**
     * 指标设置树结构数据-》组合持仓分析报表
     * @return 查询结果
     * @see
     * @since 1.0
     */
    List<TargetDetail> getAllTreeNode(Map<String, Object> map);

    /**
     *组合持仓分析报表查询
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis003Dto> secReportPage(Map<String, Object> pageParam);

    /**
     *根据groupby 条件获取对象下的分类
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis003Dto> getFinpordType(Map<String, Object> pageParam);

    /**
     *查询组合对应的剩余现金
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis003Dto> selectRemain(Map<String, Object> pageParam);

    /**
     *查询组合对应的剩余现金
     * @param pageParam 页面参数
     * @return 结果
     * @see
     * @since 1.0
     */
    List<PtlPositionAnalysis002Dto> selectPeiZhiRemain(Map<String, Object> pageParam);

    List<TargetDetail> getTableColumn();
    
    

    /**
     * 导出改造新增
     * @param pageParam
     * @return
     */
    List<PtlPositionAnalysis001Dto> selectForExportALL(Map<String, Object> pageParam);    
    List<Map<String,Object>> selectMessage(Map<String, Object> pageParam);
}
