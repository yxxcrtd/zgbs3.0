/*
 * 文件名：FinSubmittedModule002Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年8月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.product.productelement.disclosuremodule.finsubmittedmodule.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.joyintech.service.product.productelement.disclosuremodule.finsubmittedmodule.dto.FinSubmittedModuleDetailDto;

/**
 * 申报附件方案Mapper
 * 可以进行添加申报附件模板以及文件的上传和下载
 * @author liuwei
 * @version 1.0 2018年8月22日
 * @see FinSubmittedModuleDetailMapper
 * @since 1.0
 */
@Repository
public interface FinSubmittedModuleDetailMapper {
    
    /**
     * 附件模板添加界面保存点击check
     * 添加保存时，需验证附件类型+模板类型在申报附件方案中唯一
     * @param dto 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    String selectByDocAndTpl(FinSubmittedModuleDetailDto dto);

    /**
     * 查询附件类型和模板类型
     * 根据输入的附件类型和模板类型判断是否是唯一
     * @param map 附件类型和模板类型
     * @return 查询列表
     * @see
     * @since 1.0
     */
    List<FinSubmittedModuleDetailDto> selectDocTplType(Map<String, Object> map);

    /**
     * 将数据插入到申报附件方案表
     * 
     * @param dto 插入数据
     * @see
     * @since 1.0
     */
    int insert(FinSubmittedModuleDetailDto dto);

    /**
     * 删除申报附件方案表信息
     * 
     * @param schemeId 申报附件方案代码
     * @return 结果
     * @see
     * @since 1.0
     */
    int delete(String schemeId);

    /**
     * 表格查询操作
     * 查询附件类型、模板类型和备注等数据（根据申报附件方案代码查询）
     * @param map 查询参数
     * @return 附件类型、模板类型和备注
     * @see
     * @since 1.0
     */
    List<FinSubmittedModuleDetailDto> select(Map<String, Object> map);

    /**
     * 查询附件类型、模板类型和备注等数据（根据申报附件方案流水号查询）
     * 
     * @param seqno 流水号
     * @return 附件类型、模板类型和备注
     * @see
     * @since 1.0
     */
    FinSubmittedModuleDetailDto selectBySeqno(String seqno);

    /**
     * 根据点击行的附件、模板类型和申报方案代码查询对应的流水号
     * 
     * @param dto 查询参数
     * @return 流水号
     * @see
     * @since 1.0
     */
    String selectSeqno(FinSubmittedModuleDetailDto dto);

    /**
     * 附件明细表格查询操作
     * 
     * @param map 查询参数
     * @return 文件名和业务编码
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    FinSubmittedModuleDetailDto selectFile( String businessId);

    /**
     * 根据业务编码删除文件信息
     * 
     * @param businessId 业务编码
     * @see
     * @since 1.0
     */
    int deleteFile(String businessId);

    /**
     * 根据申报附件方案流水号查询上传文件信息流水号
     * 
     * @param businessId 申报附件方案流水号
     * @return 上传文件信息流水号
     * @see
     * @since 1.0
     */
    String selectSeqNo(String businessId);

    /**
     * 检索金融产品代码
     * 根据选中数据信息检查该附件模板方案是否被理财产品使用
     * @param schemeId 查询参数
     * @return 金融产品代码
     * @see
     * @since 1.0
     */
    List<String> selectFinProdId(String schemeId);
}
