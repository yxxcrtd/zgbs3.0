/*
 * 文件名：ValBondRealRate001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2019年1月19日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.assets.bondrate.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.assets.bondrate.dao.ValBondRealRate001Mapper;
import com.joyintech.service.valuation.assets.bondrate.dto.ValBondRealRate001Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 债券实际利率查询
 * 
 * @author HuangJian
 * @version 1.0 2019年1月19日
 * @see ValBondRealRate001Service
 * @since 1.0
 */
@Service(value="serviceValBondRealRate001Service")
@Transactional(rollbackFor = Exception.class)
public class ValBondRealRate001Service extends CommonExportService<ValBondRealRate001Dto> {

    /**
     * ValBondRealRate001Mapper
     */
    @Autowired
    private ValBondRealRate001Mapper valBondRealRate001Mapper;

    /**
     * 查询
     * 
     * @param params Map<String, Object>
     * @return Page<ValBondRealRate001Dto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<ValBondRealRate001Dto> findPage(int page, int length, Map<String, Object> params){
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
        params.put("invAim", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        return super.queryForPageForSelectReturn(page, length, () -> valBondRealRate001Mapper.findPage(params));
    }
    
    
    public List<ValBondRealRate001Dto> findAll(Map<String, Object> params){
            params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
            params.put("invAim", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
            return valBondRealRate001Mapper.findPage(params);
        }

  
    /**
     * 账套代码
     * 
     * @param params Map<String, Object>
     * @return Page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBooksetId(int page, int length,Map<String, Object> params) {
        params.put("booksetType", DictCodeList.账套类别_账套模板.getValue());
        return this.queryForPageForSelectReturn(page, length, ()->valBondRealRate001Mapper.getBooksetId(params));
    }

    /**
     * 债券代码
     * 
     * @param params Map<String, Object>
     * @return Page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBondId(int page, int length,Map<String, Object> params) {
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
        return this.queryForPageForSelectReturn(page, length, ()->valBondRealRate001Mapper.getBondId(params));

    }

}
