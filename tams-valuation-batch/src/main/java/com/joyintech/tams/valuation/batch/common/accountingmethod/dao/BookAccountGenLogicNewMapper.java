/*
 * 文件名：AccountingMethodMapper.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.dao;

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
     * 查询科目映射关系配置项
     * @param booksetId 账套代码
     * @param listBokDetailId 账务明细代码
     * @return
     */
    List<SubjectMappingConfig> getSubjectMappingConfig(@Param("booksetId") String booksetId,
                                                                    @Param("listBokDetailId") List<String> listBokDetailId);
}
