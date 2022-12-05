package com.joyintech.service.basicdata.marketdata.externaloptions.dao;

import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions001Dto;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2019- 12-31 10:22
 */
@Repository("serviceComExternalOptionsMapper")
public interface ComExternalOptionsMapper {

    ComExternalOptions002Dto selectByPrimaryKey(Map<String, Object> map);

    List<SuggestDto> findFinRateInfoByBenchmarkId(Map<String, Object> params);

    String findByBenchmarkId(String finprodId2);

    List<ComExternalOptions001Dto> selectList(Map<String, Object> map);


    List<SuggestDto> findTargetId(Map<String, Object> params);

    List<SuggestDto> findFinprodId(Map<String, Object> params);

    ComExternalOptions002Dto dateCheck(Map<String, Object> params);

    List<ComExternalOptions002Dto> getTargetInfo(Map<String, Object> param);
}
