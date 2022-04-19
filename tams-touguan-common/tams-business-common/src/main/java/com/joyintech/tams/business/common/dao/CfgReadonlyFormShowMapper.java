package com.joyintech.tams.business.common.dao;

import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.entity.CfgReadonlyFormShow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CfgReadonlyFormShowMapper {
    int deleteByPrimaryKey(CfgReadonlyFormShow cfgReadonlyFormShow);

    int insert(CfgReadonlyFormShow record);

    int insertSelective(CfgReadonlyFormShow record);

    CfgReadonlyFormShow selectByPrimaryKey(CfgReadonlyFormShow cfgReadonlyFormShow);

    int updateByPrimaryKeySelective(CfgReadonlyFormShow record);

    int updateByPrimaryKey(CfgReadonlyFormShow record);

    /**
     * 查询页面表单设置
     * @param pageCode
     * @param dtCode
     * @return
     */
    List<CfgReadonlyFormShow> getReadonlyForm(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                         @Param("spcdCode") String spcdCode);

    /**
     * 查询页面表单设置
     * @param param
     * @return
     */
    List<CfgReadonlyFormShow> getReadonlyForms(Map<String,Object> param);



}