package com.joyintech.tams.touguan.system.bond.special.dao;

import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.system.bond.special.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.system.bond.special.dto.req.FinProductSpecialSelectOptionQueryRequest;
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
public interface FinProductSpecialAssetMapper {

    /**
     * 特殊债券信息分页查询
     *
     * @param request
     * @return
     */
    List<FinProductSpecialDto> queryForPage(FinProductSpecialQueryRequest request);

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
    Integer countFinprodIdProcessing(@Param("finprodId") String finprodId, @Param("iPlatformDate") Date iPlatformDate);
}
