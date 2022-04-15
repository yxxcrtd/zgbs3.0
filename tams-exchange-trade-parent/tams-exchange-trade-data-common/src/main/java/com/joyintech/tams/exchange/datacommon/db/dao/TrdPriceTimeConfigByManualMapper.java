package com.joyintech.tams.exchange.datacommon.db.dao;

import com.joyintech.tams.exchange.common.db.domain.TrdPriceTimeConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author swwan
 * @date 2022/4/14 - 11:10
 */
@Repository
public interface TrdPriceTimeConfigByManualMapper {

    /**
     * 根据 指令/交易 查询对象列表
     * @param etrTrad 指令/交易
     * @return 对象列表
     */
    List<TrdPriceTimeConfig> selectByEtrTrad(String etrTrad);
}
