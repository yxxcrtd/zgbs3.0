/*
 * 文件名：EtrBondFirstBiddingMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.repo.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.cfets.etr.repo.dto.*;
import com.joyintech.tams.common.base.entity.EtrEntrustPledge;
import com.joyintech.tams.common.base.entity.EtrSecStock;
import com.joyintech.tams.common.base.entity.EtrSecStockConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *  场内交易委托
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年11月14日
 * @see CfetsEtrIbRepoinfoMapper
 * @since 1.0
 */
@Repository
public interface CfetsEtrIbRepoinfoMapper {
    /**
     * 指令管理分页查询
     * <功能详细描述>
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrRepoZLCXResponse> findZlglPage(Map<String, Object> pageParam);
    /**
     * 指令管理查询分页查询
     *
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<CfetsEtrRepoZLCXPositionResponse> findZlglPositionPage(Map<String, Object> pageParam);

    /**
     * 根据组合代码，查询证券托管户：联想框文本框
     *
     * @param param
     * @return
     */
    List<SelectOptionDto> getSecManageAcctId(Map<String, Object> param);


    /**
     * 查询质押库对应的自动入库配置
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    List<EtrSecStockConfig> getAssetDetailBySeqno(Map<String, Object> param);
    
    int deleteByPrimaryKey(String configId);
    
    int delete(String pledgeStockId);
    
    int checkIsExistByPledgeStockName(Map<String, Object> map);
    
    CfetsEtrIbRepoRkDto getBusinessInfoByPledgeStockId(@Param("pledgeStockId") String pledgeStockId);
    
    int checkIsExistDetail(@Param("pledgeStockId") String pledgeStockId);
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<CfetsTreePortfolioResponse> findAllTask(Map<String, Object> params);
    
    CfetsEtrIbRepoRkDto getName(@Param("pledgeStockId") String pledgeStockId);
    
    Long deleteEtrEntrustPledge(EtrEntrustPledge etrEntrustPledgeKey);
    
    List<CfetsEtrRepoZLCXPositionResponse> selectF01List(Map<String, Object> params);
    
    int deleteEtrEntrustPledgeByPledgeStockId(@Param("pledgeStockId") String pledgeStockId);
    
    /**
     * 根据组合ID 查询质押库
     * @return
     */
    List<SelectOptionDto> getPledgeStockIdByPortfolioId(Map<String, Object> param);

    List<SelectOptionDto> getPledgeStockId(Map<String, Object> param);
    
    /**
     * 查询登录用户是否满足指定的角色岗位 
     *
     * @param param
     * @return
     */
    Long selectUserRolePosList(Map<String, Object> param);
    
    int deleteWflEtrEntrustByEntrustId(String entrustId);

    int deleteWflEtrEntrustAddByEntrustId(String entrustId);

    CfetsEtribRepoInfo003Dto findEtrinfo(Map<String,Object> map);

    BigDecimal getPledgeable(Map<String,Object> map);

    List<EtrSecStock> selectEtrSecStockByUser(Map<String, Object> param);

    List<CfetsEtrIbRepoRkDto> selectRepoRkInfoAndCofigs(Map<String, Object> param);

    CfetsEtrSecStockConfig selectFinprodInfo(Map<String, Object> param);

    CfetsEtrIbRepoLibDto findSecPosition(Map<String, Object> param);
}
