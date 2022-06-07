package com.joyintech.service.valuation.basicdata.asset.issue.dao;

import com.joyintech.service.valuation.basicdata.asset.issue.dto.FinProductIssueDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 债券标的信息
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinProductIssueMapper
 * @since 1.0
 */
@Repository
public interface FinProductIssueMapper {

    /**
     * 分页查询
     *
     * @param pageParam
     * @return
     */
    List<FinProductIssueDto> queryForPage(Map<String, Object> pageParam);

}
