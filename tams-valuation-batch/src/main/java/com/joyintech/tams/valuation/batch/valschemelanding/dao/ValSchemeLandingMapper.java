/*
 * 文件名：DeltRightCurCalAmortMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.dao;

import com.joyintech.tams.common.base.entity.BokFinprodSchemeInfo;
import com.joyintech.tams.valuation.batch.valschemelanding.dto.ValSchemeLandingReaderDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author weipeng
 * @version 1.0 2022年03月25日
 * @see ValSchemeLandingMapper
 * @since 1.0
 */
@Repository
public interface ValSchemeLandingMapper {

    /**
     * 查询当天的所有重分类交易
     * 
     * @param param
     * @return
     */
    public List<ValSchemeLandingReaderDto> selectReclsDeal(Map<String, Object> param);

    /**
     *  查询所有待批处理的账套和资产信息
     *
     * @param param
     * @return
     */
    public List<ValSchemeLandingReaderDto> selectValSchemeTaskTodo(Map<String, Object> param);

    /**
     *  批量删除资产估值方案信息表
     *
     * @param list 待处理信息
     * @return 删除条数
     */
    public int deleteBatch(List<BokFinprodSchemeInfo> list);

    /**
     *  批量插入资产估值方案信息表
     *
     * @param list 待处理信息
     * @return 删除条数
     */
    public int insertBatch(List<BokFinprodSchemeInfo> list);

}
