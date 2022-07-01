/*
 * 文件名：SppiTest002Service
 * 版权：Copyright 2017-2020 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020-10-16
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.service.asset.sppi.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.service.asset.sppi.dao.SppiTest001Mapper;
import com.joyintech.service.asset.sppi.dto.SppiTemplateDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.SppiTestMapper;
import com.joyintech.tams.common.base.dao.SppiTestQaMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.SppiTestQa;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SPPI测试的新增、查看详细、审核服务类
 * @author Jiaoxujin
 * @version 1.0 2020-10-16
 */
@Service("backSppiTest002Service")
public class SppiTest002Service extends BaseService<SppiTemplateDto> {
    @Autowired
    private SppiTest001Mapper sppiTest001Mapper;

    @Autowired
    private SppiTestMapper sppiTestMapper;

    @Autowired
    private SppiTestQaMapper sppiTestQaMapper;

    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 嵌套查询SPPI测试详细信息
     * @param sppiTestId String
     * @param finprodId String
     * @return SppiTemplateDto
     */
    public SppiTemplateDto selectSppiTestDetail(String sppiTestId, String finprodId) {
        return sppiTest001Mapper.selectSppiTestDetail(sppiTestId, finprodId);
    }

    /**
     * 新增保存SPPI测试结果
     * @param dto SppiTemplateDto
     * @return int
     */
    public int saveSppiTest(SppiTemplateDto dto) {
        int result = 0;

        // 本方法只用来处理新增（无编辑）,新增待审核就是“测试中”
        dto.setTestStatus(DictCodeListExt3.SPPI测试状态_测试中.getValue());
        result += sppiTestMapper.insertSelective(dto);

        if (CollectionUtils.isNotEmpty(dto.getQaList())) {
            result += sppiTest001Mapper.batchInsertSppiTestQa(dto.getQaList());
        }

        return result;
    }

    /**
     * 更新sppi测试结果
     * @param dto
     * @return int
     */
    public int updateSppiTest(SppiTemplateDto dto) {
        int result = 0;

        result += sppiTestMapper.updateByPrimaryKeySelective(dto);
        if (CollectionUtils.isNotEmpty(dto.getQaList())) {
            for (SppiTestQa qa : dto.getQaList()) {
                result += sppiTestQaMapper.updateByPrimaryKeySelective(qa);
            }
        }

        return result;
    }

    /**
     * SPPI测试审核完成处理
     * @param dto SppiTemplateDto
     * @return int
     */
    public int auditCompleteSppiTest(SppiTemplateDto dto) {
        int result = 0;

        result += updateSppiTestStatus(dto);

        FinProduct finProduct = new FinProduct();
        finProduct.setFinprodId(dto.getFinprodId());
        finProduct.setSppiTestStatus(dto.getTestStatus());
        // 更新人、更新时间由框架自动设定

        result += finProductMapper.updateByPrimaryKeySelective(finProduct);

        return result;
    }

    /**
     * 删除本次SPPI测试记录
     * @param dto
     * @return
     */
    public int deleteSppiTest(SppiTemplateDto dto) {
        int result = 0;

        result += sppiTest001Mapper.deleteByTestId(dto.getSppiTestId());
        result += sppiTestMapper.deleteByPrimaryKey(dto.getSppiTestId());

        return result;
    }

    /**
     * 更新SPPI测试主表的状态
     * @param dto
     * @return int
     */
    public int updateSppiTestStatus(SppiTemplateDto dto) {
        int result = 0;

        SppiTemplateDto upperDto = new SppiTemplateDto();
        upperDto.setSppiTestId(dto.getSppiTestId());
        upperDto.setTestStatus(dto.getTestStatus());
        // 更新人、更新时间由框架自动设定

        result += sppiTestMapper.updateByPrimaryKeySelective(upperDto);

        return result;
    }

    /**
     * 查询金融产品有效交易数
     * @param finprodId
     * @return int
     */
    public int queryValidTradeCount(String finprodId) {
        return sppiTest001Mapper.queryValidTradeCount(finprodId);
    }
}
