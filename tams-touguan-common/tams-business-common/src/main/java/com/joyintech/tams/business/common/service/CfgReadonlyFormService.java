package com.joyintech.tams.business.common.service;

import com.joyintech.tams.business.common.dao.CfgReadonlyFormShowMapper;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


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
        return cfgReadonlyFormShowMapper.getReadonlyForm(pageCode, dtCode, spcdCode);
    }

    /**
     * 根据pageCode，dtCode和spcdCode查询页面展示设置
     *
     * @param pageCode     页面id
     * @param dtCode       页面区块id
     * @param spcdCodeList 条件(列表)
     * @return java.util.List<com.joyintech.tams.business.common.entity.CfgReadonlyFormShow> 表单信息
     * @author jiangpeng
     * @date 2022/4/21 14:50
     */
    public List<CfgReadonlyFormShow> getReadonlyForms(String pageCode, String dtCode, List<String> spcdCodeList) {
        Map<String, Object> param = new HashMap<>();
        param.put("pageCode", pageCode);
        param.put("dtCode", dtCode);
        param.put("spcdCodes", spcdCodeList);
        return cfgReadonlyFormShowMapper.getReadonlyForms(param);
    }
}
