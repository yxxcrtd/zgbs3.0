/*
 * 文件名：ShadowPriceScheme001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
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
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dao.ShadowPriceScheme001Mapper;
import com.joyintech.service.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.tams.common.base.entity.BokShadowPriceDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 影子定价方案service 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author lishicheng
 * @version 1.0 2018年11月22日
 * @see ShadowPriceScheme001Service
 * @since 1.0
 */
@Service(value="serviceShadowPriceScheme001Service")
public class ShadowPriceScheme001Service extends CommonExportService<ShadowPriceScheme001Dto> implements BaseProcessService<ShadowPriceScheme001Dto> {

    @Autowired
    private ShadowPriceScheme001Mapper shadowPriceScheme001Mapper;

    /**
     * 影子定价方案左侧查询service <一句话功能简述> <功能详细描述>
     * 
     * @param pageParam
     * @return
     * @throws Exception
     */
    public Page<ShadowPriceScheme001Dto> findShadowPrice(int page, int length, Map<String, Object> pageParam){
        // TODO Auto-generated method stub
        return super.queryForPage(page, length,() -> shadowPriceScheme001Mapper.findShadowPrice(pageParam));
    }

    /**
     * 影子定价方案左侧新建 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     */
    public void insert(ShadowPriceScheme001Dto dto) {
        shadowPriceScheme001Mapper.insert(dto);
    }

    /**
     * 影子定价方案左侧修改 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     */
    public void updateByPrimaryKeySelective(ShadowPriceScheme001Dto dto) {
        shadowPriceScheme001Mapper.updateByPrimaryKeySelective(dto);
    }

    /**
     * 修改时根据方案名称查询 <一句话功能简述> <功能详细描述>
     * 
     * @param schName
     * @return
     */
    public ShadowPriceScheme001Dto selectSchemeBySchId(String schId) {
        // TODO Auto-generated method stub
        return shadowPriceScheme001Mapper.selectSchemeBySchId(schId);
    }

    /**
     * 左侧影子方案删除 <一句话功能简述> <功能详细描述>
     * 
     * @param schName
     * @return
     */
    public int deleteShadowPriceSch(String schId) {
        // TODO Auto-generated method stub
        return shadowPriceScheme001Mapper.deleteShadowPriceSch(schId);
    }

    /**
     * 估值方案联动 <一句话功能简述> <功能详细描述>
     * 
     * @return
     */
    public List<SelectOptionDto> getSchemeForSelect(Map<String, Object> parameters) {
        List<ShadowPriceScheme001Dto> resultList = shadowPriceScheme001Mapper.findShadowPrice(
            parameters);
        List<SelectOptionDto> selectOptionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (ShadowPriceScheme001Dto single : resultList) {
                SelectOptionDto selectOptionDto = new SelectOptionDto();
                selectOptionDto.setId(single.getSchId());
                selectOptionDto.setText(single.getSchName());
                selectOptionList.add(selectOptionDto);
            }
        }
        return selectOptionList;
    }

    /**
     * 影子定价方案删除验证 <一句话功能简述> <功能详细描述>
     * 
     * @param params
     * @return
     */
    public List<ShadowPriceScheme001Dto> findScheme(Map<String, Object> params) {
        // TODO Auto-generated method stub
        return shadowPriceScheme001Mapper.findScheme(params);
    }

    @Override
    public void doComplete(ShadowPriceScheme001Dto entity, OperationType type) {
        // 影子方案新建复制
        if (OperationType.NEW.equals(type)) {
            // 复制操作，明细也要一起复制
            if (StringUtil.equals("copy", entity.getOperateType())) {
                List<BokShadowPriceDetail> listDetails = shadowPriceScheme001Mapper.getShadowPriceDetialCopy(
                    entity.getSchId(), entity.getOrgSchId());
                // 批量入库
                if (listDetails != null && listDetails.size() > 0) {
                    listDetails.forEach(item -> {
                        // 方案明细代码重新赋值
                        item.setSchemeDetailId(IDGenerator.generate());
                    });
                    shadowPriceScheme001Mapper.insertBatch(listDetails);
                }
                shadowPriceScheme001Mapper.insert(entity);
            }
            else{
                OperationType operationType = (StringUtil.isNotEmpty(entity.getOperateType())) ? OperationType.valueOf(
                    entity.getOperateType().toUpperCase()) : null;
                // 设为默认方案操作
                if (OperationType.MODIFY.equals(operationType)) {
                    // 原默认方案是否默认字段改为否
                    ShadowPriceScheme001Dto defalutScheme = this.getDefaultScheme();
                    if (defalutScheme != null) {
                        defalutScheme.setIsDefault(DictCodeList.是否_否.getValue());
                        shadowPriceScheme001Mapper.updateByPrimaryKeySelective(defalutScheme);
                    }
                    shadowPriceScheme001Mapper.updateByPrimaryKeySelective(entity);
                }
                // 新建入库操作
                else {
                    shadowPriceScheme001Mapper.insert(entity);
                }
            }
            
        }
        // 影子方案修改
        if (OperationType.MODIFY.equals(type)) {
            shadowPriceScheme001Mapper.updateByPrimaryKeySelective(entity);
        }
        // 影子方案删除
        if (OperationType.DELETE.equals(type)) {
            shadowPriceScheme001Mapper.deleteShadowPriceSch(entity.getSchId());
        }

    }

    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param processMark
     * @return
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getBusinessDataInFlow(ProcessMark processMark) {
        return shadowPriceScheme001Mapper.getBusinessDataInFlow(processMark);
    }

    /**
     * 查询默认方案
     * 
     * @return 查询默认方案
     * @see
     * @since 1.0
     */
    public ShadowPriceScheme001Dto getDefaultScheme() {
        return shadowPriceScheme001Mapper.getDefaultScheme(DictCodeListExt1.配置方案类型_影子定价方案.getValue(),
            DictCodeList.是否_是.getValue());
    }

}
