package com.joyintech.service.basicdata.marketdata.externaloptions.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.service.basicdata.marketdata.externaloptions.dao.ComExternalOptionsMapper;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions001Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 姚轶文
 * @create 2020- 01-06 16:07
 */
@Service("serviceComExternalOptionsService001")
public class ComExternalOptionsService001 extends BaseService {

    @Autowired
    ComExternalOptionsMapper comExternalOptionsMapper;

    /**
     * 列表查询 - 分页
     *
     * @param page
     * @param length
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public Page<ComExternalOptions001Dto> queryPageList(int page, int length, Map<String, Object> params) {
        return super.queryForPage(page, length, () -> comExternalOptionsMapper.selectList(params));
    }
}
