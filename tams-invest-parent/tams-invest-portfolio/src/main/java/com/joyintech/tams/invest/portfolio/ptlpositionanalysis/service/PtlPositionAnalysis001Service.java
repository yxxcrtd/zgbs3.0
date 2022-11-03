/*
 * 文件名：PtlPositionAnalysis001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wanghb
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.portfolio.ptlpositionanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import org.springframework.stereotype.Service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis001Dto;
import com.joyintech.tams.invest.portfolio.ptlpositionanalysis.dto.PtlPositionAnalysis002Dto;

/**
 * 组合持仓分析
 * @author wanghb
 * @version 1.0 2019年1月25日
 * @see PtlPositionAnalysis001Service
 * @since 1.0
 */
@Service
public class PtlPositionAnalysis001Service extends CommonExportService<PtlPositionAnalysis001Dto> {
    /**
     * 组合持仓明细查询
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<PtlPositionAnalysis001Dto> selectForPage(Map<String, Object> map){
        handleProdManager(map);
        return restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findPage",
                map, PtlPositionAnalysis001Dto.class);
    }
    
    /**
     * 组合持仓明细查询
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<PtlPositionAnalysis001Dto> selectAllList(Map<String, Object> map){
    	if(!StringUtil.isNotEmpty(map.get("prodManager"))) {
    		List<String> prodManagers = new ArrayList<>();
    		//产品经理不存在时获取所有符合的产品经理
    		Page<SelectOptionDto> users = restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/getUserName", new HashMap<String,Object>(),
            		SelectOptionDto.class);
    		users.getRoot().stream().forEach(user->prodManagers.add(user.getId()));
    		map.put("prodManagers",prodManagers);
    	}
        List<PtlPositionAnalysis001Dto> list=restTemplate.postForList(
            ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findList",
            map, PtlPositionAnalysis001Dto.class);
        return list;
    }
    /**
     * 组合持仓汇总查询
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    /*public Page<PtlPositionAnalysis002Dto> selectAllAsset(Map<String, Object> map){
        Page<PtlPositionAnalysis002Dto> page = restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findPageForAsset",
                map, PtlPositionAnalysis002Dto.class);
        return page;
    }*/
    /**
     * 组合持仓汇总查询
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<PtlPositionAnalysis002Dto> selectAllAssetList(Map<String, Object> map){
    	if(!StringUtil.isNotEmpty(map.get("prodManager"))) {
    		List<String> prodManagers = new ArrayList<>();
    		//产品经理不存在时获取所有符合的产品经理
    		Page<SelectOptionDto> users = restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/getUserName", new HashMap<String,Object>(),
            		SelectOptionDto.class);
    		users.getRoot().stream().forEach(user->prodManagers.add(user.getId()));
    		map.put("prodManagers",prodManagers);
    	}
        List<PtlPositionAnalysis002Dto> list = restTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findListForAsset",
                map, PtlPositionAnalysis002Dto.class);
        return list;
    }
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            exportParam.config(
                    "/com/joyintech/tams/invest/portfolio/ptlpositionanalysis/conf/export_conf_ls.xml").dataSource(
                    DataSourceType.MASERT);
        }else{
            exportParam.config(
                    "/com/joyintech/tams/invest/portfolio/ptlpositionanalysis/conf/export_conf.xml").dataSource(
                    DataSourceType.MASERT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PtlPositionAnalysis001Dto> selectPageData(Map<String, Object> selectParam) {
        return selectForExportNew(selectParam);
    }
    
    /**
     * 导出查询方法
     *
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<PtlPositionAnalysis001Dto> selectForExportNew(Map<String, Object> map){
        handleProdManager(map);
        Page<PtlPositionAnalysis001Dto> page = restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findPageForExportNew",
                map, PtlPositionAnalysis001Dto.class);
        return page;
    }

    private void handleProdManager(Map<String, Object> map) {
        if (!StringUtil.isNotEmpty(map.get("prodManager"))) {
            List<String> prodManagers = new ArrayList<>();
            //产品经理不存在时获取所有符合的产品经理
            List<SelectOptionDto> users = restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/getUserNameList", new HashMap<String, Object>(),
                    SelectOptionDto.class);
            users.stream().forEach(user -> prodManagers.add(user.getId()));
            map.put("prodManagers", prodManagers);
        }
    }

    /**
     * 导出查询方法
     *
     * @param map 画面参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<PtlPositionAnalysis001Dto> selectForExport(Map<String, Object> map){
    	if(!StringUtil.isNotEmpty(map.get("prodManager"))) {
    		List<String> prodManagers = new ArrayList<>();
    		//产品经理不存在时获取所有符合的产品经理
    		Page<SelectOptionDto> users = restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/getUserName", new HashMap<String,Object>(),
            		SelectOptionDto.class);
    		users.getRoot().stream().forEach(user->prodManagers.add(user.getId()));
    		map.put("prodManagers",prodManagers);
    	}
        Page<PtlPositionAnalysis001Dto> page = restTemplate.postForPage(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/findPageForExport",
                map, PtlPositionAnalysis001Dto.class);
        return page;
    }
    /**
     * 资产代码的联想下拉框事件
     *
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortFolioInfo(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
                        + "/PtlPositionAnalysis/getPortFolioInfo" , map,
                SelectOptionDto.class);
    }

    /**
     * 资产代码的联想下拉框事件
     *
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getPortFolioInfoWW(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.PORTFOLIO_SERVICE_NAME
                        + "/PtlPositionAnalysis/getPortFolioInfoWW" , map,
                SelectOptionDto.class);
    }

    /**
     * 剩余金额的信息
     * @param map
     * @return
     */
    public List<PtlPositionAnalysis002Dto> selectRemain(Map<String,Object> map ){
        List<PtlPositionAnalysis002Dto> typelist = simpleRestTemplate.postForList(
                ServiceApplication.PORTFOLIO_SERVICE_NAME + "/PtlPositionAnalysis/selectPeiZhiRemain",
                map, PtlPositionAnalysis002Dto.class);
        return typelist;
    }
}
