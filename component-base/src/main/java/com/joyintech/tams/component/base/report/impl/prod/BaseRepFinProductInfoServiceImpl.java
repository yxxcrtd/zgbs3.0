/*
 * 文件名：RepFinProductInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.base.report.impl.prod;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.RepProdDeclareMapper;
import com.joyintech.tams.common.base.dao.RepProdIssueMapper;
import com.joyintech.tams.component.base.report.dto.BaseRepFinProductDto;
import com.joyintech.tams.component.base.report.dto.BaseRepFinProductInfoDto;
import com.joyintech.tams.component.base.report.impl.dao.BaseRepFinProductInfoMapper;
import com.joyintech.tams.component.base.report.prod.BaseRepFinProductInfoService;
import com.joyintech.tools.util.ListUtils;

/**
 * 产品端中债报备信息组件应用接口 
 * 〈功能详细描述〉
 * @author huwei
 * @version 1.0 2018年8月28日
 * @see BaseRepFinProductInfoServiceImpl
 * @since 1.0
 */
@Service
public class BaseRepFinProductInfoServiceImpl  implements BaseRepFinProductInfoService {

    /**
     * 产品申报报送信息Mapper
     */
    @Autowired
    private RepProdDeclareMapper repProdDeclareMapper;

    /**
     * 产品发行报送信息Mapper
     */
    @Autowired
    private RepProdIssueMapper repProdIssueMapper;

    /**
     * 产品端中债报备信息组件Mapper
     */
    @Autowired
    private BaseRepFinProductInfoMapper baseRepFinProductInfoMapper;

    /**
     * 产品端中债报备信息组件接口类
     */
    @Autowired
    private BaseRepFinProductInfoService baseRepFinProductInfoService;
    

    /**
     * 新建产品中债报备信息 <一句话功能简述> <功能详细描述>
     * 
     * @param dtos
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean newRepFinProductInfo(List<BaseRepFinProductInfoDto> dtos) {
        // 循环输入参数List<产品端中债报送DTO>
        if (dtos != null && dtos.size() > 0) {
            for (BaseRepFinProductInfoDto baseRepFinProductInfoDto : dtos) {
                // 将产品端中债报送Dto.产品申报报送信息Entity插入产品申报报送信息
                repProdDeclareMapper.insertSelective(baseRepFinProductInfoDto.getRepProdDeclare());
                // 将产品端中债报送Dto.产品发行报送信息
                repProdIssueMapper.insertSelective(baseRepFinProductInfoDto.getRepProdIssue());
                
                //标的类金融产品
                baseRepFinProductInfoMapper.updateFinProduct(baseRepFinProductInfoDto.getFinProduct());
            }
            return true;
        }
        return false;
    };

    /**
     * 删除产品中债报备信息 
     * <一句话功能简述> 
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean deleteRepFinProductInfo(BaseRepFinProductDto dto) {
        if (dto.getRepFinprodIds() != null && dto.getRepFinprodIds().size() > 0) {
            List<List<String>> list= ListUtils.splitList(dto.getRepFinprodIds());
            // 按照输入参数删除产品申报报送信息和产品发行报送信息。
            for (List<String> repFinprodIdLists : list) {
                baseRepFinProductInfoMapper.deleteRepProdDeclare(repFinprodIdLists);
                baseRepFinProductInfoMapper.deleteRepProdIssue(repFinprodIdLists);
            }
            return true;
        }
        return false;
    }

    /**
     * 修改产品中债报备信息 
     * <一句话功能简述> 
     * <功能详细描述>
     * @param dto
     * @param dtos
     * @return
     * @see
     * @since 1.0
     */
    @Override
    public boolean updateRepFinProductInfo(BaseRepFinProductDto dto,
                                           List<BaseRepFinProductInfoDto> dtos) {
        // 调用删除产品中债报备信息
        boolean flag1 = baseRepFinProductInfoService.deleteRepFinProductInfo(dto);
        // 调用新建产品中债报备信息
        boolean flag2 = baseRepFinProductInfoService.newRepFinProductInfo(dtos);
        if (flag1 && flag2) {
            return true;
        }
        return false;
    }
}
