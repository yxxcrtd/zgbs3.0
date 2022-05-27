package com.joyintech.service.basicdata.standardcouponreconversionrate.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.basicdata.standardcouponreconversionrate.dto.StandardCouponReConversionRateDto;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BasicStandardCouponReConversionRateMapper {

    /**
     *
     * 模糊查询
     * <功能详细描述>
     * @param pageParam pageParam
     * @return List<StandardCouponReConversionRateDto>
     * @see
     * @since 1.0
     */
    List<StandardCouponReConversionRateDto> select(Map<String, Object> pageParam);

    /**
     * 券商
     *
     * @return List<SuggestDto>
     */
    List<SelectOptionDto> getMstCustomerInfo(Map<String, Object> map);


    StandardCouponReConversionRateDto selectByPrimaryKey(StandardCouponReConversionRateDto dto);

    List<StandardCouponReConversionRateDto> getList(StandardCouponReConversionRateDto dto);

    List<StandardCouponReConversionRateDto> getBokerList(StandardCouponReConversionRateDto dto);

    List<String> getBusinessData(WflValidInputParameter dto);

    int deleteByPrimaryKey(StandardCouponReConversionRateDto dto);

    int insertSelective(StandardCouponReConversionRateDto mstSeatInfo);

    int updateByPrimaryKeySelective(StandardCouponReConversionRateDto mstSeatInfo);

    int updateByPrimaryKey(StandardCouponReConversionRateDto mstSeatInfo);

    /**
     * 组合资金账户查询
     * @param map
     * @return
     */
    List<SelectOptionDto> getPtlSecAcctInfo(Map<String,Object> map);

    List<SelectOptionDto> brokerIdChange(Map<String,Object> map);

    List<SelectOptionDto> getFinprodIdInfo(Map<String,Object> map);

    String checkMstCustomerInfo(Map<String, Object> map);

    String checkAccountTypeInfo(Map<String, Object> map);

    String checkFinprodIdInfo(Map<String, Object> map);
}
