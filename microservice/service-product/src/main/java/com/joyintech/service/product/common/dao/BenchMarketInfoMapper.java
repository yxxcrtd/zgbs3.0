/*
 * 文件名：BenchMarketInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：陈卓
 * 修改时间：2018年10月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.product.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.product.common.dto.BussBenchMarkInfoDto;

/**
 * 业绩基准业务组件mapper
 *
 * @author 陈卓
 * @version 1.0 2018年10月08日
 * @see BenchMarketInfoMapper
 * @since 1.0
 */
@Repository
public interface BenchMarketInfoMapper {

    /**
     * 基准信息取得
     *
     * @return 基准信息
     */
    List<SelectOptionDto> getFinBaseRuleInfo();

    /**
     * 根据金融产品代码和生效日查询数据
     * <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    BussBenchMarkInfoDto selectByPrimaryKey(Map<String, Object> param);

    BussBenchMarkInfoDto selectByPrimaryKey2(Map<String, Object> param);

    /**
     * 根据金融产品代码和生效日查询数据
     * <功能详细描述>
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<BussBenchMarkInfoDto> selectByFinprodidList(Map<String, Object> param);

    /**
     * 根据基准代码删除
     * <功能详细描述>
     *
     * @param baseRuleId
     * @return
     * @see
     * @since 1.0
     */
    int delete(String baseRuleId);

    /**
     * 根据金融产品代码删除业绩基准信息
     *
     * @param finprodId 金融产品代码
     * @return
     */
    int deleteBaseRuleInfoByFinprodId(String finprodId);

    /**
     * 根据金融产品代码删除业绩基准信息
     *
     * @param finprodId 金融产品代码
     * @param branch    批次
     */
    int deleteBaseRuleInfoByFinprodIdAndBranch(@Param("finprodId") String finprodId,
                                               @Param("branch") long branch);
}
