/*
 * 文件名：AccountingMethodMapper.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.dao;

import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see BookAccountGenLogicNewMapper
 * @since 1.0
 */
@Repository
public interface BookAccountGenLogicNewMapper {

    /**
     * 根据内部编码查询对应科目信息
     * @param booksetId 账套代码
     * @param listSubjectInternalCode 内部科目编码
     * @param ccy 币种
     * @return
     */
    List<BokSubject> getSubjectByInternalCode(@Param("booksetId") String booksetId,
                                              @Param("listSubjectInternalCode") List<String> listSubjectInternalCode,
                                              @Param("ccy") String ccy);

    /**
     * 查询科目映射关系配置项
     * @param booksetId 账套代码
     * @param listBokDetailId 账务明细代码
     * @param finprodTypeCk 金融产品类型_存款类资产
     * @return
     */
    List<SubjectMappingConfig> getSubjectMappingConfig(@Param("booksetId") String booksetId,
                                                                    @Param("listBokDetailId") List<String> listBokDetailId,
                                                                    @Param("finprodTypeCk") String finprodTypeCk);
}
