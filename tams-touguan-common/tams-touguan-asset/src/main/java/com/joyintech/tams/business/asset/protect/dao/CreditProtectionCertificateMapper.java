package com.joyintech.tams.business.asset.protect.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate002Dto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.common.base.entity.FinProductTypeConfig;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wangshengzhi
 * @version 1.0 2022年5月9日
 * @see CreditProtectionCertificateMapper
 * @since 1.0
 */
@Repository
public interface CreditProtectionCertificateMapper {

    List<CreditProtectionCertificateDto> queryForPage(Map<String, Object> pageParam);

    CreditProtectionCertificate002Dto getByFinProdId(Map<String, Object> pageParam);

    List<String> getRefList(Map<String, Object> pageParam);

    String getMstCustomerInfo(Map<String, Object> pageParam);

    int insertFileSelective(SysUploadFileInfo var1);

    int updateFileByPrimaryKey(SysUploadFileInfo var1);

    /**
     * 查询资产代码列表
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getFinprodIdForSelect(Map<String, Object> param);

    /**
     * 根据查询参数查询留痕表数据
     *
     * @param wflValidInputParameter
     * @return
     */
    List<String> getBusinessData(WflValidInputParameter wflValidInputParameter);

    /**
     * 查询金融产品代码
     *
     * @param param
     * @return
     */
    String selectFinprodId(Map<String, String> param);

    /**
     * 删除时判断金融产品代码是否使用过
     *
     * @param
     * @return
     */
    Integer countFinprodIdUse(Map<String, Object> map);

    /**
     * 根据行内三级分类获取wind分类
     * @param param
     * @return
     */
    FinProductTypeConfig getWindType(FinProductTypeConfig param);


}
