package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.SzdSjsfw;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author huwei1
 * @description
 * @createDate 2022/6/7
 */
@Repository
public interface SzdSjsfwMapper {

    /**
     * 首期质押品SJSFW信息查询
     */
    List<SzdSjsfw> selectPledgeSjsfw(Map<String, Object> params);
}
