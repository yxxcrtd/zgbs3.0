/*
 * 文件名：EtrInvestBondInfoMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.bondfirstbid.dao;

import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 一级投标指令mapper
 */
@Repository
public interface CfetsEtrBondFirstBid001Mapper {

    /**
     * 指令管理查询分页查询
     * @param pageParam 查询参数
     * @return 分页结果
     */
    List<CfetsEtrIBondFirstBidFindEtrInfoDto> findEtrInfoPage(Map<String, Object> pageParam);

    /**
     * 查询业务模式
     * @param param
     * @return
     */
    String findInvAimInEtr(Map<String, Object> param);

    /**
     * 查询待发行信息分页数据
     * @param pageParam
     * @return
     */
    List<CfetsEtrIBondFirstBidFindIssueInfoDto> findIssueInfoPage(Map<String, Object> pageParam);
}
