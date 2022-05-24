package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dao;

import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialSelectOptionQueryRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 债券特殊信息mapper
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Repository
public interface FinProductSpecial001Mapper {

    /**
     * 特殊债券信息分页查询
     *
     * @param request
     * @return
     */
    List<FinProductSpecialDto> queryForPage(FinProductSpecialQueryRequest request);

    /**
     * 查询
     *
     * @param finprodMarketId
     * @return
     */
    FinProductSpecialDto queryByFinprodMarketId(@Param("finprodMarketId") String finprodMarketId);

    /**
     * 查询下拉框
     *
     * @param request
     * @return
     */
    List<FinProductSpecialSelectOptionDto> querySelectOption(FinProductSpecialSelectOptionQueryRequest request);


    /**
     * 判断金融产品代码是否使用过
     *
     * @param finprodId
     * @param iPlatformDate
     * @return
     */
    int countFinprodIdProcessing(@Param("finprodId") String finprodId, @Param("iPlatformDate") Date iPlatformDate);

    /**
     * 接口表简单数据查询
     *
     * @param request
     * @return
     */
    FinProductSpecialSelectOptionDto querySampleInterf(FinProductSpecialSelectOptionQueryRequest request);

    /**
     * 业务表简单数据查询
     *
     * @param request
     * @return
     */
    FinProductSpecialSelectOptionDto querySampleFinProduct(FinProductSpecialSelectOptionQueryRequest request);


}
