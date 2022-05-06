/*
 * 文件名：DateCalcMapper.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：何阳阳 修改时间：2018年9月21日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.common.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.dto.AssetCommonInvRateAdjustDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 业务共通组件.资产头寸现金持仓共通组件的Mapper
 *
 * @author 强成西
 * @version 1.0 2018年9月21日
 * @see CommonBusinessMapper
 * @since 1.0
 */
@Repository
public interface CommonBusinessMapper {

    Map<String, Object> getAuthorizeLimitAmt(Map<String, String> params);
    
    Boolean isBackCheckNode(Map<String, Object> params);
    
    BigDecimal getAuthorizeLimitAmtForFuZongCai(Map<String, String> params);
    
    List<StlPayOrders> selectStlPayOrdersList(Map<String, Object> params);
    
    List<StlDvpOrders> selectStlDvpOrdersList(Map<String, Object> params);

    Boolean checkPortChlAcctNoSame(@Param("portfolioId") String portfolioId, @Param("chlId") String chlId);

    // 资产类型改为授权类型 add by renping 2019-08-19  start
    /**
     * 汇率行情查询
     * @param map 参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    MstExchargeQuote getExchangeRate(Map<String, Object> map);

    int deletePledgeBond(Map<String, Object> params);
    // 资产类型改为授权类型 add by renping 2019-08-19  end

    void insertNewFileInfo(Map<String, Object> params);

    BigDecimal selectFaceValue(Map<String, Object> params);
    
    /**
     * 
     * 组合资金账户信息
     * @param params
     * @return
     * @see
     * @since 1.0
     */
    List<PtlCapacctInfo> selectPtlCapacctInfo(Map<String, Object> params);
	
   /**
     *查询证券账户信息
     * @param params
     * @return
     */
    String querySecAcctId(Map<String, Object> params);
    
    /**  20200730 njl add
     *判断债券wind接过来的有没有维护过
     * @param finprodId
     * @return int
     */
    int selectBondNum(@Param("finprodId") String finprodId);


    /**  20200730 njl add
     *判断基金wind接过来的有没有维护过
     * @param finprodId
     * @return int
     */
    int selectFundNum(@Param("finprodId") String finprodId);

	String selectCustomer(Map<String, Object> map);

    /**
     * 登录用户是否是交易处长
     * @return
     */
	int isTraDirector(Map<String,Object> param);

    List<AssetCommonInvRateAdjustDto> getMstRateInfo(@Param("indexId")String indexId);

}
