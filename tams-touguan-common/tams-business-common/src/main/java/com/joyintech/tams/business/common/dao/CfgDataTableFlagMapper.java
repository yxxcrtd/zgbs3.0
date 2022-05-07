package com.joyintech.tams.business.common.dao;

import com.joyintech.tams.business.common.entity.CfgDataTableShowFlag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CfgDataTableFlagMapper {

    /**
     * 查询数据表格设置记录，查询到数据表示用户已设置，否则用户未设置
     * @param pageCode
     * @param dtCode
     * @param rangeId
     * @return
     */
    CfgDataTableShowFlag getByPageCodeAndDtCode(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                                @Param("spcdCode") String spcdCode, @Param("rangeId") String rangeId);

    /**
     * 新增数据表格设置记录
     * @param cfgDataTableFlag
     * @return
     */
    int insertCfgDataTableFlag(CfgDataTableShowFlag cfgDataTableFlag);

    /**
     * 删除用户自定义设置
     * @param pageCode
     * @param dtCode
     * @param rangeId
     */
    void deleteDataTable(@Param("pageCode")String pageCode,@Param("dtCode") String dtCode,
                         @Param("spcdCode") String spcdCode,@Param("rangeId") String rangeId);

    /**
     * 查询数据表格设置记录，查询到数据表示用户已设置，否则用户未设置
     *
     * @param pageCode
     * @param dtCode
     * @param spcdCodes
     * @param rangeId
     * @return com.joyintech.tams.business.common.entity.CfgDataTableShowFlag
     * @author jiangpeng
     * @date 2022/5/7 13:36
     */
    CfgDataTableShowFlag getByPageCodeAndDtCodes(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                                 @Param("spcdCodes") List<String> spcdCodes, @Param("rangeId") String rangeId);

    /**
     * 删除用户自定义设置
     *
     * @param pageCode
     * @param dtCode
     * @param spcdCodes
     * @param rangeId
     * @author jiangpeng
     * @date 2022/5/7 13:36
     */
    void deleteDataTables(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                          @Param("spcdCodes") List<String> spcdCodes, @Param("rangeId") String rangeId);
}
