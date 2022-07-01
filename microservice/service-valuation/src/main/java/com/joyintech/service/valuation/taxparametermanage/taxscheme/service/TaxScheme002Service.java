/*
 * 文件名：TaxScheme002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.taxparametermanage.taxscheme.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dao.TaxScheme002Mapper;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dto.TaxScheme002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 税务方案
 * 
 * @author zhangwei
 * @version 1.0 2018年10月11日
 * @see TaxScheme002Service
 * @since 1.0
 */
@Service(value="serviceTaxScheme002Service")
@Transactional(rollbackFor = Exception.class)
public class TaxScheme002Service extends CommonExportService<TaxScheme002Dto> implements BaseProcessService<TaxScheme002Dto> {

    @Autowired
    TaxScheme002Mapper taxScheme002Mapper;

    /**
     * 流程审核完成时执行 {@inheritDoc}
     */
    @Override
    public void doComplete(TaxScheme002Dto entity, OperationType type) {
        if (OperationType.NEW.equals(type)) {
            if(StringUtils.isEmpty(entity.getRefSchId())) { // 新建
                this.insertSelective(entity);
            }else { // 复制
                this.copyByPrimaryKey(entity);
            }
        }
        else if (OperationType.MODIFY.equals(type)) {
            this.updateByPrimaryKeySelective(entity);
        }
        else if (OperationType.DELETE.equals(type)) {
            this.deleteByPrimaryKey(entity.getSchId());
        }
    }

    /**
     * 查询
     * 
     * @param dto
     */
    public TaxScheme002Dto findBySchId(String schId) {
        return taxScheme002Mapper.selectByPrimaryKey(schId);
    }

    /**
     * 新增
     * 
     * @param dto
     */
    public void insertSelective(TaxScheme002Dto dto) {
        // ZG-065 : 当出现新的默认方案，将已存在默认方案设置为非默认。
        if (DictCodeList.是否_是.getValue().equals(dto.getIsDefault())) {
            TaxScheme002Dto updateDto = new TaxScheme002Dto();
            updateDto.setIsDefault(DictCodeList.是否_否.getValue());
            this.updateIsDefault(updateDto);
        }
        taxScheme002Mapper.insertSelective(dto);
    }

    /**
     * 修改
     * 
     * @param dto
     */
    public void updateByPrimaryKeySelective(TaxScheme002Dto dto) {
        // ZG-065 : 当出现新的默认方案，将已存在默认方案设置为非默认。
        if (DictCodeList.是否_是.getValue().equals(dto.getIsDefault())) {
            TaxScheme002Dto updateDto = new TaxScheme002Dto();
            updateDto.setIsDefault(DictCodeList.是否_否.getValue());
            this.updateIsDefault(updateDto);
        }
        taxScheme002Mapper.updateByPrimaryKeySelective(dto);
    }

    /**
     * 复制
     * 
     * @param dto
     */
    public void copyByPrimaryKey(TaxScheme002Dto dto) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("schId", dto.getSchId());
        map.put("refSchId", dto.getRefSchId());
        List<String> copyRange = dto.getCopyRange();

        // ZG-065 : 当出现新的默认方案，将已存在默认方案设置为非默认。
        if (DictCodeList.是否_是.getValue().equals(dto.getIsDefault())) {
            TaxScheme002Dto updateDto = new TaxScheme002Dto();
            updateDto.setIsDefault(DictCodeList.是否_否.getValue());
            this.updateIsDefault(updateDto);
        }

        taxScheme002Mapper.insertSelective(dto); // 复制 账套税务配置 -- 税务方案

        if (CollectionUtils.isNotEmpty(copyRange)) {
            if (copyRange.contains(DictCodeList.复制范围_增值税则表.getValue())) {
                taxScheme002Mapper.copyTaxVatSchedule(map);
            }
            if (copyRange.contains(DictCodeList.复制范围_附加税则表.getValue())) {
                taxScheme002Mapper.copyTaxAdtSchedule(map);
            }
            if (copyRange.contains(DictCodeList.复制范围_资产应税信息.getValue())) {
                taxScheme002Mapper.copyTaxTaxableInfo(map);
            }
            if (copyRange.contains(DictCodeList.复制范围_参数设置.getValue())) {
                taxScheme002Mapper.copyBokSchemeConfigVal(map);
            }
            if (copyRange.contains(DictCodeList.复制范围_会计场景计税配置.getValue())) {
                taxScheme002Mapper.copyTaxSchaBokConfig(map);
            }
        }
    }

    /**
     * 通过 查询账套关联表 中 的税务方案;
     * 
     * @param schName
     * @return
     */
    public int countBokBookSetPlan(String schId) {
        return taxScheme002Mapper.countBokBookSetPlan(schId);
    }

    /**
     * 删除税务方案 及其 挂靠表信息
     * 
     * @param schName
     * @return
     */
    public int deleteByPrimaryKey(String schId) {

        taxScheme002Mapper.deleteTaxVatBySchId(schId); // 删除 增值税则表
        taxScheme002Mapper.deleteTaxAdtBySchId(schId); // 删除 附加税则表
        taxScheme002Mapper.deleteTaxAbleBySchId(schId); // 删除 应税信息表
        taxScheme002Mapper.deleteConfigBySchId(schId); // 删除 方案配置值信息表 - 参数设置信息
        taxScheme002Mapper.deleteTaxConfigBySchId(schId); // 删除 方案配置值信息表 - 会计场景计税配置

        return taxScheme002Mapper.deleteByPrimaryKey(schId);
    }

    /**
     * 修改是否默认
     * 
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int updateIsDefault(TaxScheme002Dto dto) {
        return taxScheme002Mapper.updateIsDefault(dto);
    }

}
