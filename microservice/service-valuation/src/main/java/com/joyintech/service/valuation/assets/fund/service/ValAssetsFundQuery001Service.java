/*
 * 文件名：ValAssetFundQueryService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：zhangwei
 * 修改时间：2018年7月19日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.assets.fund.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.assets.fund.dao.ValAssetsFundQuery001Mapper;
import com.joyintech.service.valuation.assets.fund.dto.ValAssetsFundQuery001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 基金类资产查询
 * 
 * @author zhangwei
 * @version 1.0 2018年7月19日
 * @see ValAssetFundQuery001Service
 * @since 1.0
 */
@Service(value="serviceValAssetsFundQuery001Service")
public class ValAssetsFundQuery001Service extends CommonExportService<ValAssetsFundQuery001Dto> implements BaseProcessService<ValAssetsFundQuery001Dto> {

    @Autowired
    private ValAssetsFundQuery001Mapper valAssetsFundQuery001Mapper;

    /**
     * 根据条件获取表格信息
     * 
     * @param params 查询参数
     * @return 分页的表格信息
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<ValAssetsFundQuery001Dto> findPage(int page, int length, Map<String, Object> params){
        params.put("finprodType", "F06");// 仅查询基金型资产信息
        return super.queryForPageForSelectReturn(page, length, () -> valAssetsFundQuery001Mapper.selectList(params));
    }
    
    
    public List<ValAssetsFundQuery001Dto> findAll(Map<String, Object> params) {
            params.put("finprodType", "F06");// 仅查询基金型资产信息
            return valAssetsFundQuery001Mapper.selectList(params);
        }


    /**
     * 下拉框获取：资产
     * 
     * @param params
     * @return select节点对象
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAssets(Map<String, Object> params) {
        params.put("finprodType", "F06");// 仅查询基金型资产信息
        List<ValAssetsFundQuery001Dto> resultList = valAssetsFundQuery001Mapper.selectList(params);
        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (ValAssetsFundQuery001Dto single : resultList) { // 组装select节点对象
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getFinprodId());
                selectOptionDto.setText(single.getFinprodAbbr());
                selectOptionDtoList.add(selectOptionDto);
            }
        }
        return selectOptionDtoList;
    }

    /**
     * 根据资产代码查询该资产是否存在交易（修改、删除校验）
     * 
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkFindprodId(Map<String, Object> params) {
    	boolean ddd= valAssetsFundQuery001Mapper.checkFindprodId(params);
        return ddd;
    }

    /**
     * 删除
     * 
     * @param finprodId
     * @param fundType
     * @return
     * @see
     * @since 1.0
     */
    public int delete(String finprodId, String fundType) {
        // 删除标的类金融产品扩展表信息
        valAssetsFundQuery001Mapper.deleteByFindprodId2(finprodId);
        // 删除金融产品分类表
        valAssetsFundQuery001Mapper.deleteByFindprodId3(finprodId);
        if (fundType.equals(DictCodeList.基金类型_货币基金.getValue())) {
            // 删除现金流信息表
            valAssetsFundQuery001Mapper.deleteByFindprodId4(finprodId);
            // 删除现金流支付规则表
            valAssetsFundQuery001Mapper.deleteByFindprodId5(finprodId);
            // // 删除现金流计划表
            // valAssetsFundQuery001Mapper.deleteByFindprodId6(finprodId);
        }

        // 删除标的类金融产品表信息
        return valAssetsFundQuery001Mapper.deleteByFindprodId(finprodId);
    }

    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getBusinessDataInFlow(ProcessMark processMark) {
        return valAssetsFundQuery001Mapper.getBusinessDataInFlow(processMark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(ValAssetsFundQuery001Dto entity, OperationType type) {
//        // 删除操作
//        if (OperationType.DELETE.equals(type)) {
//            String finprodId = entity.getFinprodId();
//            String fundType = entity.getFundType();
//            // 删除标的类金融产品扩展表信息
//            valAssetsFundQuery001Mapper.deleteByFindprodId2(finprodId);
//            // 删除金融产品分类表
//            valAssetsFundQuery001Mapper.deleteByFindprodId3(finprodId);
//            if (fundType.equals(DictCodeList.基金类型_货币基金.getValue())) {
//                // 删除现金流信息表
//                valAssetsFundQuery001Mapper.deleteByFindprodId4(finprodId);
//                // 删除现金流支付规则表
//                valAssetsFundQuery001Mapper.deleteByFindprodId5(finprodId);
//                // // 删除现金流计划表
//                // valAssetsFundQuery001Mapper.deleteByFindprodId6(finprodId);
//            }
//
//            // 删除标的类金融产品表信息
//            valAssetsFundQuery001Mapper.deleteByFindprodId(finprodId);
//        }
//
    }
}
