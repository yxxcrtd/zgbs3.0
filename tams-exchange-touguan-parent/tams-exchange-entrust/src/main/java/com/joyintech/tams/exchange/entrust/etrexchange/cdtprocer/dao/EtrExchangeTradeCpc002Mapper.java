package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dao;

import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateHoldResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto.EtrCreditProtectionCertificateResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EtrExchangeTradeCpc002Mapper {
    /**
     * 场内交易委托列表查询
     * @param param
     * @return
     */
    List<EtrCreditProtectionCertificateResponse> selectEtrExchanges(Map<String,Object> param);

    /**
     * 根据指令编号获取分发指令编号
     *
     * @param map
     * @return
     */
    EtrDistribute getDistributeByEntrustId(Map<String, Object> map);

    EtrCreditProtectionCertificateHoldResponse getPrice(Map<String, Object> map);

    /**
     * 根据委托id查询委托
     * @param entrustId
     * @return
     */
    EtrEntrustAdd selectEntrustAddByKey(@Param("entrustId") String entrustId);

    /**
     * 产品持仓信息分页查询
     * <功能详细描述>
     * @param pageParam 查询参数
     * @return 分页结果
     * @see
     * @since 1.0
     */
    List<EtrCreditProtectionCertificateHoldResponse> findPositionsPage(Map<String, Object> pageParam);

    /**
     * 当日交易数据查询
     * @param baseMap
     * @return
     */
    List<BaseMap> selectCurrTrdData(BaseMap baseMap);

    List<String> isOverMDate(Map<String, Object> map);

    List<String> getSecAcctIdZJH(Map<String, Object> map);

    /**
     * 查询前一日持仓指标
     * @param baseMap
     * @return
     */
    List<BaseMap> selectLastCostAmt(BaseMap baseMap);

    /**
     * 根据委托id查询委托
     * @param entrustId
     * @return
     */
    EtrEntrust selectEntrustByKey(@Param("entrustId")String entrustId);

}
