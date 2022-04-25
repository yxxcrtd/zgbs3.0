/*
 * 文件名：ValBondRealRate001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2019年1月19日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.assets.bondrate.service;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.assets.bondrate.dto.ValBondRealRate001Dto;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;

/**
 * 债券实际利率查询
 * 
 * @author HuangJian
 * @version 1.0 2019年1月19日
 * @see ValBondRealRate001Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ValBondRealRate001Service extends CommonExportService<ValBondRealRate001Dto> {

    /**
     * ValBondRealRate001Mapper
     */
//    @Autowired
//    private ValBondRealRate001Mapper valBondRealRate001Mapper;

    /**
     * 查询
     * 
     * @param params Map<String, Object>
     * @return Page<ValBondRealRate001Dto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<ValBondRealRate001Dto> findPage(Map<String, Object> params)
         {
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
        params.put("invAim", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
//        return super.queryForPage(() -> valBondRealRate001Mapper.findPage(params));
//        
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/ValBondRealRate001/findPage",
                params, ValBondRealRate001Dto.class);
        
    }

    /**
     * 导出
     * 
     * @param params Map<String, Object>
     * @return List<ValBondRealRate001Dto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public List<ValBondRealRate001Dto> findAll(Map<String, Object> params)
         {
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
        params.put("invAim", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
//        return valBondRealRate001Mapper.findPage(params);
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/ValBondRealRate001/findAll",
                params, ValBondRealRate001Dto.class);
    }

    /**
     * 账套代码
     * 
     * @param params Map<String, Object>
     * @return Page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBooksetId(Map<String, Object> params) {
        params.put("booksetType", DictCodeList.账套类别_账套模板.getValue());
//        return super.queryForPageForSelectReturn(
//            () -> valBondRealRate001Mapper.getBooksetId(params));

        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/ValBondRealRate001/getBooksetId",
                params, SelectOptionDto.class);
    }

    /**
     * 债券代码
     * 
     * @param params Map<String, Object>
     * @return Page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getBondId(Map<String, Object> params) {
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
//        return super.queryForPageForSelectReturn(() -> valBondRealRate001Mapper.getBondId(params));
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/ValBondRealRate001/getBondId",
                params, SelectOptionDto.class);
    }

}
