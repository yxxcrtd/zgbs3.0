/*
 * 文件名：BasePtlCashPositionInfoMapperService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年9月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.component.base.group;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;

/**
 * 组合可用现金和实际现金查询更新组件
 * 
 * @author qiangchengxi
 * @version 1.0 2018年9月3日
 * @see BasePtlCashPositionInfoService
 * @since 1.0
 */
public interface BasePtlCashPositionInfoService {

    /**
     * 查询可用现金
     *
     * @param positionQueryDto PtlPositionFlowQueryDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    BigDecimal queryUsableCashPosition(PtlCashPositionFlowDto positionQueryDto);

    /**
     * 查询在途、实际、质押现金
     *
     * @param positionQueryDto PtlPositionFlowQueryDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    BigDecimal queryOtherCashPosition(PtlCashPositionFlowDto positionQueryDto);

    /**
     * 插入现金流水
     *
     * @param positionFlowDto PtlCashPositionFlowDto
     * @return boolean
     * @see
     * @since 1.0
     */
    boolean insertCashPositionFlow(PtlCashPositionFlowDto positionFlowDto);

    /**
     * 删除现金流水
     *
     * @param positionQueryDto PtlPositionFlowQueryDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    boolean deleteCashPosition(PtlCashPositionFlowDto positionQueryDto);
    
    /**
     * 查询下个工作日
     * @param tradeMarketId
     * @param cdate
     * @param finprodId
     * @return
     */
    Date selectNextWorkDay(String tradeMarketId, Date cdate, String finprodId);
    
    /**
     * 查询可取现金
     *
     * @param positionQueryDto PtlPositionFlowQueryDto
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    BigDecimal queryUsableCashPositionOther(PtlCashPositionFlowDto positionQueryDto);
    
    /**
     * 查询资产附加信息，查询产品是否纳入净额结算
     * @param finprodId
     * @return
     */
    FinProductAdd getFinProductAdd(String finprodId);

    BigDecimal queryTrdAvailableCashNew(PtlCashPositionFlowDto positionQueryDto);

    BigDecimal queryActCashPosition(PtlCashPositionFlowDto positionQueryDto);

}
