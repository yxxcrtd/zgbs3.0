package com.joyintech.tams.business.common.service;

import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 页面只读表单展示配置service
 */
@Service
public class CfgReadonlyFormService {

    @Autowired
    private CfgReadonlyFormShowMapper cfgReadonlyFormShowMapper;

    /**
     * 根据pageCode，dtCode和spcdCode查询页面展示设置
     *
     * @param pageCode
     * @param dtCode
     * @return
     */
    public List<CfgReadonlyFormShow> getReadonlyForm(String pageCode, String dtCode, String spcdCode) {
        return cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode,spcdCode);
    }
}
