package com.joyintech.tams.cfets.trdconfirm.dao;


import com.joyintech.tams.cfets.trdconfirm.dto.BankSysParaSub;
import com.joyintech.tams.cfets.trdconfirm.dto.MatchBusType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 银行间交易成交确认-匹配元素
 *
 * @author jiangpeng
 * @date 2022/9/27 9:38
 */
@Repository
public interface MatchFeaturesMapper {
    /**
     * 匹配元素 查询
     *
     * @param param
     * @return java.util.List<com.joyintech.tams.cfets.trdconfirm.dto.matchBusType>
     * @author jiangpeng
     * @date 2022/9/27 10:49
     */
    List<MatchBusType> queryMatchFeatures(Map<String, Object> param);

    /**
     * 更新匹配元素
     *
     * @param updateList
     * @return int
     * @author jiangpeng
     * @date 2022/9/27 10:59
     */
    int updateMatchFeatures(@Param("updateList") List<BankSysParaSub> updateList);
}
