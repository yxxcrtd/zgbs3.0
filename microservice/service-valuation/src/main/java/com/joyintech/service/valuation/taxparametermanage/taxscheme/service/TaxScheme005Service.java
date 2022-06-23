/*
 * 文件名：TaxScheme005Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.valuation.taxparametermanage.taxscheme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dao.TaxScheme005Mapper;
import com.joyintech.service.valuation.taxparametermanage.taxscheme.dto.TaxScheme005Dto;
import com.joyintech.tams.common.base.dao.TaxTaxableInfoMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 资产应税信息
 * 
 * @author zhangwei
 * @version 1.0 2018年10月16日
 * @see TaxScheme005Service
 * @since 1.0
 */
@Service(value="serviceTaxScheme005Service")
@Transactional(rollbackFor = Exception.class)
public class TaxScheme005Service extends CommonExportService<TaxScheme005Dto> implements BaseProcessService<TaxScheme005Dto> {

    @Autowired
    private TaxScheme005Mapper taxScheme005Mapper;

    /**
     * 资产应税信息
     */
    @Autowired
    private TaxTaxableInfoMapper taxTaxableInfoMapper;

    /**
     * 流程审核完成时执行 {@inheritDoc}
     */
    @Override
    public void doComplete(TaxScheme005Dto entity, OperationType type) {
        if(StringUtils.isNotEmpty(entity.getAssetId()) && !OperationType.DELETE.equals(type)) {
//            () 是转义字符
            /** modify by yanya on 20191031 后台存储的是金融产品代码非市场代码**/
            String[] arr = entity.getAssetId().split("\\(");
            if(arr.length > 1) {
                String assetId = arr[0];
                String finprodType = arr[1];
                Map<String,Object> pageParam = new HashMap<String, Object>();
                pageParam.put("assetId", assetId);
                pageParam.put("finprodType", finprodType);
                TaxScheme005Dto taxScheme005Dto =  taxScheme005Mapper.getAssetInfo(pageParam);
                if(taxScheme005Dto != null) {
                    entity.setAssetId(taxScheme005Dto.getOriginalFinprodId());
                }
                else {
                    entity.setAssetId(entity.getAssetId().split("\\(")[0]); // 主键是 finprod_id + finprod_type： finprod_id(finprod_type
                }
            }
            else {
                entity.setAssetId(entity.getAssetId().split("\\(")[0]); // 主键是 finprod_id + finprod_type： finprod_id(finprod_type
            }
        }
        
        if (OperationType.NEW.equals(type)) {
            this.insertSelective(entity);
        }
        else if (OperationType.MODIFY.equals(type)) {
            this.updateByPrimaryKeySelective(entity);
        }
        else if (OperationType.DELETE.equals(type)) {
            this.deleteByBatch(entity.getSeqNo());
        }
    }
    
    /**
     * 新建 资产应税信息
     * 
     * @param dto
     * @return
     */
    public int insertSelective(TaxScheme005Dto dto) {
        dto.setMaintenanceMode(DictCodeListExt1.维护方式_手工维护.getValue());
        return taxScheme005Mapper.insertSelective(dto);
    }

    /**
     * 查询 资产应税信息
     * 
     * @param seqNo
     * @return
     */
    public TaxScheme005Dto findByPrimaryKey(String seqNo) {
        TaxScheme005Dto dto = taxScheme005Mapper.selectByPrimaryKey(seqNo);
        dto.setAssetId(dto.getAssetId() + "(" + dto.getFinprodType()); // 主键是 finprod_id + finprod_type： finprod_id(finprod_type
        return dto;
    }

    /**
     * 修改 资产应税信息
     * 
     * @param dto
     * @return
     */
    public int updateByPrimaryKeySelective(TaxScheme005Dto dto) {
        return taxScheme005Mapper.updateByPrimaryKeySelective(dto);
    }

    /**
     * 删除、批量删除 资产应税信息
     * 
     * @param seqNo
     * @return
     */
    public int deleteByBatch(String seqNo) {
        int row = 0;
        for (String item : seqNo.split(",")) {
            row += taxTaxableInfoMapper.deleteByPrimaryKey(item);
        }
        return row;
    }

    /**
     * 获取资产代码 - 下拉框
     * 
     * @return
     * @throws Exception
     */
    public Page<SelectOptionDto> findAsset(int page, int length, Map<String, Object> pageParam) {
        pageParam.put("InstCode", LoginUserUtil.getInstCode());
        return super.queryForPageForSelectReturn(page,length,() -> taxScheme005Mapper.selectAsset(pageParam));
    }

    /**
     * 通过资产代码，获取资产信息
     * 
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public TaxScheme005Dto getAssetInfo(Map<String, Object> pageParam) {
        return taxScheme005Mapper.getAssetInfo(pageParam);
    }

}
