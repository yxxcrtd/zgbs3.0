package com.joyintech.tams.business.common.dao;

import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CfgDataTableMapper {

    /**
     * 表格设置批量插入
     * @param list
     */
    void insertCfgDataTableBatch(List<CfgDataTableShow> list);

    /**
     * 查询数据表格式设置
     *
     * @param cfgDataTableShow
     * @return
     */
    List<CfgDataTableShow> getCfgDataTableList(CfgDataTableShow cfgDataTableShow);

    /**
     * 查询整个页面数据表格式设置
     *
     * @param pageCode
     * @return
     */
    @Select("select * from CFG_DATATABLE_SHOW where PAGECODE = #{pageCode} and status = 1")
    List<CfgDataTableShow> listByPageCode(String pageCode);

    /**
     * 查询页面数据表格式设置
     * @param pageCode
     * @param dtCode
     * @param effeRange
     * @param rangeId
     * @return
     */
    List<CfgDataTableShow> getByPageCode(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                         @Param("spcdCode") String spcdCode,
                                          @Param("effeRange") Integer effeRange, @Param("rangeId") String rangeId);

    /**
     * 查询页面数据表格编码
     * @param pageCode
     * @return
     */
    List<String> getDtCodeByPageCode(@Param("pageCode") String pageCode);

    /**
     * 表格设置批量更新
     * @param dataList
     */
    void updateCfgDataTableBatch(List<CfgDataTableShow> dataList);

    /**
     * 查询页面数据表格式设置
     * @param pageCode
     * @param dtCode
     * @param effeRange
     * @param rangeId
     * @return
     */
    List<CfgDataTableShow> getBySpcdCodes(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                         @Param("spcdCodes") List<String> spcdCodes,
                                         @Param("effeRange") Integer effeRange, @Param("rangeId") String rangeId);

    /**
     * 查询页面数据表格式设置
     * @param pageCode
     * @param dtCode
     * @param effeRange
     * @param rangeId
     * @return
     */
    List<CfgDataTableShow> getBySpcdCodesshowInvaim(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                          @Param("spcdCodes") List<String> spcdCodes,
                                          @Param("effeRange") Integer effeRange, @Param("rangeId") String rangeId, @Param("showInvaimOrBussinssmodel") String showInvaimOrBussinssmodel);


    /**
     * 查询页面隐藏域数据表格式设置
     *
     * @param pageCode
     * @param dtCode
     * @param isHide
     * @param spcdCodes
     * @return
     */
    List<CfgDataTableShow> getBySpcdCodesByIsHide(@Param("pageCode") String pageCode, @Param("dtCode") String dtCode,
                                                  @Param("spcdCodes") List<String> spcdCodes,
                                                  @Param("effeRange") Integer effeRange, @Param("rangeId") String rangeId,@Param("isHide") String isHide);

}
