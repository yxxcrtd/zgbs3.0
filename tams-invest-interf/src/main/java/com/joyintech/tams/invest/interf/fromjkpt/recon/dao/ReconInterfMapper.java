package com.joyintech.tams.invest.interf.fromjkpt.recon.dao;

import com.joyintech.tams.common.base.entity.InterfFundBonusFjy;
import com.joyintech.tams.common.base.entity.StlSecRecon;
import com.joyintech.tams.common.base.entity.StlSecReconPara;
import com.joyintech.tams.invest.interf.fromjkpt.recon.dto.StlCashReconDto;
import com.joyintech.tams.invest.interf.fromjkpt.recon.dto.StlSecReconDealDto;
import com.joyintech.tams.invest.interf.fromjkpt.recon.dto.StlSecpReconDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReconInterfMapper {

    int deleteStlCashRecon(Map<String,Object> param);

    int deleteStlSecpRecon(Map<String,Object> param);

    int deleteStlSecpReconDeal(Map<String,Object> param);

    int deleteStlSecRecon(Map<String,Object> param);

    int deleteStlSecReconDetail(Map<String,Object> param);

    /**
     * 资金余额
     * @param param
     * @return
     */
    List<StlCashReconDto> listStlCashRecon(Map<String,Object> param);

    /**
     * 持仓余额
     * @param param
     * @return
     */
    List<StlSecpReconDto> listStlSecpRecon(Map<String,Object> param);

    /**
     * 对账单参数
     * @param param
     * @return
     */
    List<StlSecReconPara> listStlSecReconPara(Map<String,Object> param);

    /**
     * 佣金对账参数
     * @param param
     * @return
     */
    List<StlSecReconPara> listStlSecReconCommissionPara(Map<String,Object> param);

    /**
     * 对账单明细
     * @param param
     * @return
     */
    List<StlSecReconDealDto> listStlSecReconDealDetaile(Map<String,Object> param);

    /**
     * 证券代码汇总
     * @param param
     * @return
     */
    List<StlSecReconDealDto> listStlSecReconDealTotalByMarketId(Map<String,Object> param);

    /**
     * 委托代码汇总
     * @param param
     * @return
     */
    List<StlSecReconDealDto> listStlSecReconDealTotalByEntrustId(Map<String,Object> param);
    /**
     * 对账结果
     * @return
     */
    List<StlSecRecon> listStlSecRecon(Map<String,Object> param);
    /**
     * 协议回购委托代码汇总
     * @param param
     * @return
     */
    List<StlSecReconDealDto> listStlSecReconDealIntegratedRepTotal(Map<String,Object> param);
    /**
     * 协议回购明细
     * @param param
     * @return
     */
    List<StlSecReconDealDto> listStlSecReconDealIntegratedRepDetaile(Map<String,Object> param);



}
