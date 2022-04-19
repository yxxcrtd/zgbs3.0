package com.joyintech.tams.business.common.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.business.common.dto.CashPositionFlowDto;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.business.common.dto.SecInfoModelDto;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 投管业务共通公共方法mapper
 */
@Repository
public interface TamsBusinessCommonMapper {

    /**
     * 检索产品关联资金账户共通
     * @param portfolioId 组合代码
     * @param ccy 币种
     * @param capacctType 资金账户类型
     * @return 产品关联资金账户
     */
    List<StlCapacctInfo> selectProdCapacctInfoList(@Param("portfolioId") String portfolioId, @Param("ccy") String ccy, @Param("capacctType") String capacctType);
}
