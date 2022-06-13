/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：sjj
 * 修改时间：2021年11月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etrinfoquery.dao;

import com.joyintech.tams.cfets.etrinfoquery.dto.CfetsEtrInfoQueryListResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 银行间指令查询
 * @author sjj
 * @version 1.0 2021年11月26日
 * @see CfetsEtrInfoQueryMapper
 * @since 1.0
 */
@Repository
public interface CfetsEtrInfoQueryMapper {

    /**
     * 指令列表分页查询
     * @param paraMap 参数
     * @return List<CfetsEtrInfoQueryListResponse>
     */
    List<CfetsEtrInfoQueryListResponse> findAllData(Map<String,Object> paraMap);

    //查询登录用户是否是领导、交易处长岗位
    int queryRolePosByLoginUser(Map<String, Object> map);
}
