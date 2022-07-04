/*
 * 文件名：PtlSingleAssetPositionAnalysisService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wanghb
 * 修改时间：2019年1月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.ptlsingleassetpositionanalysis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptlsingleassetpositionanalysis.dao.PtlSingleAssetPositionAnalysisMapper;
import com.joyintech.service.portfolio.ptlsingleassetpositionanalysis.dto.PtlSingleAssetPositionAnalysis001Dto;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;

/**
 * 单一资产持仓分析Service
 * @author wanghb
 * @version 1.0 2019年1月23日
 * @see PtlSingleAssetPositionAnalysisService
 * @since 1.0
 */
@Service
public class PtlSingleAssetPositionAnalysisService extends BaseService<PtlSingleAssetPositionAnalysis001Dto>{
    /**
     * 定义债券质押查询mapper
     */
    @Autowired
    private PtlSingleAssetPositionAnalysisMapper ptlSingleAssetPositionAnalysisMapper;

    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * @param page 初始
     * @param length 长度
     * @param pageParam 条件
     * @return 结果
     * @throws Exception  异常
     * @see
     * @since 1.0
     */
    public Page<PtlSingleAssetPositionAnalysis001Dto> selectForPage(int page,int length,Map<String, Object> pageParam)
    {
        //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
        List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
        pageParam.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
        if(StringUtil.isNotEmpty(pageParam.get("finprodId"))){
            pageParam.put("finprodIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("finprodId")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("parentId")) && !StringUtil.equals(pageParam.get("parentId"),"root")) {
            List<PtlSingleAssetPositionAnalysis001Dto> resultList = ptlSingleAssetPositionAnalysisMapper.selectAll(pageParam);
            Page<PtlSingleAssetPositionAnalysis001Dto> pagelist = new Page<>();
            pagelist.setRoot(resultList);
            return pagelist;
        }

        Page<PtlSingleAssetPositionAnalysis001Dto> list=super.queryForPage(page, length,() -> ptlSingleAssetPositionAnalysisMapper.selectAll(pageParam));
        return list;
    }
    /**
     * 查询list。
     * @param pageParam pageParam
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public List<PtlSingleAssetPositionAnalysis001Dto> selectForList(Map<String, Object> pageParam)
    {
        if(StringUtil.isNotEmpty(pageParam.get("finprodId"))){
            pageParam.put("finprodIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("finprodId")));
        }
        List<PtlSingleAssetPositionAnalysis001Dto> list=ptlSingleAssetPositionAnalysisMapper.select(pageParam);
        return list;
    }
    /**
     * 资产代码的联想下拉框事件
     *
     * @param map 输入参数
     * @return 结果
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getFinprodMarketId(Map<String,Object> map, int page, int length) {
        List<String> list = new ArrayList<>();
        list.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        map.put("FINTYPE_F01_LIST",list);
        list = new ArrayList<>();
        list.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_债券借贷.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        // add by etliu 20210421 权益类资产相关代码合并 start
        list.add(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue());
        // add by etliu 20210421 权益类资产相关代码合并 end
        list.add(DictCodeListExt1.金融产品类型_投管用_其他.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_同业借款.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue());
        list.add(DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue());
        /*modify by yujiaping 2020-11-24 ZGBUGTWO-244【单一资产持仓分析】改为不可以查询到通道的信息*/
        // list.add(DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        map.put("FINTYPE_F02_LIST",list);
        // 多级法人改造 add by jingqiongqiong 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
            map.put("institutionCode",createInst);
            map.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
        }
        // 多级法人改造 add by jingqiongqiong 20200615
        return this.queryForPageForSelectReturn(page, length,()->ptlSingleAssetPositionAnalysisMapper.getFinprodMarketId(map));
    }
    
    /**
     * 导出查询list。
     * @param pageParam pageParam
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public Page<PtlSingleAssetPositionAnalysis001Dto> selectForExport(int page, int length, Map<String, Object> pageParam)
    {
        if(StringUtil.isNotEmpty(pageParam.get("finprodId"))){
            pageParam.put("finprodIdList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("finprodId")));
        }
        // 多级法人改造 add by jingqiongqiong 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
            pageParam.put("institutionCode",createInst);
            pageParam.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
        }
        // 多级法人改造 add by jingqiongqiong 20200615
        List<PtlSingleAssetPositionAnalysis001Dto> resultList = new ArrayList<>();
        Page<PtlSingleAssetPositionAnalysis001Dto> pageRoot = super.queryForPage(page, length, () -> ptlSingleAssetPositionAnalysisMapper.selectForExport(pageParam));
        List<PtlSingleAssetPositionAnalysis001Dto> listRoot = pageRoot.getRoot();
        for(PtlSingleAssetPositionAnalysis001Dto entity : listRoot) {
            if(StringUtil.equals(entity.getParentInfo(), "root")) {
                resultList.add(entity);
                for(PtlSingleAssetPositionAnalysis001Dto entity2 : listRoot) {
                    if(StringUtil.equals(entity2.getParentInfo(), entity.getChildInfo())) {
                        resultList.add(entity2);
                    }
                }
            }
        }
        pageRoot.setRoot(resultList);
        return pageRoot;
    }
}
