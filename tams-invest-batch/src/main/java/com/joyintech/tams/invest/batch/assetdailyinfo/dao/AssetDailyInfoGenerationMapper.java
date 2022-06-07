/*
 * 文件名：SecPosAutoGenerationMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.assetdailyinfo.dao;

import com.joyintech.tams.business.common.dto.SecInfoModelDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 资产每日信息生成批处理Mapper
 *
 * @author wangyang1
 * @version 1.0 2022年4月12日
 * @see AssetDailyInfoGenerationMapper
 * @since 1.0
 */
@Repository
public interface AssetDailyInfoGenerationMapper {

    int deleteTodayAssetDailyInfo(Map<String, Object> paramMap);

    List<String> getAssetInfo(Map<String, Object> paramMap);

    SecInfoModelDto getSecInfoModel(Map<String, Object> param);

}
