package com.joyintech.tams.invest.common.position.dao;

import com.joyintech.tams.invest.common.position.dto.InterfPositionConfigDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 头寸接口配置Mapper
 *
 * @author yflv
 * @version 1.0 2022年2月21日
 * @see InterfPositionConfigMapper
 * @since 1.0
 */
@Repository
public interface InterfPositionConfigMapper {


    /**
     * 查询头寸接口配置
     *
     * @return
     */
    List<InterfPositionConfigDto> searchInterfPositionConfig();

    /**
     * 查询头寸接口配置
     *
     * @param where
     * @return
     */
    List<InterfPositionConfigDto> searchInterfPositionConfigByWhere(InterfPositionConfigDto where);

}
