/*
 * 文件名：BondAutoDurDealGenerateMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autodurdealgenerate.exchangecommonrepo.dao;

import com.joyintech.tams.common.base.entity.EtrCashPosition;
import com.joyintech.tams.common.base.entity.PtlCashPosition;
import com.joyintech.tams.common.base.entity.TrdProductTempDeal;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Mapper
 *
 * @author etliu
 * @version 1.0 2018年12月11日
 * @see ExCommonRepoAutoDurDealGenerateMapper
 * @since 1.0
 */
@Repository
public interface ExCommonRepoAutoDurDealGenerateMapper {

    List<TrdProductTempDeal> selectTrdProductTempDealRevokeData(Map<String, Object> paramMap);

    int deleteStlPayTempOrdersData(Map<String, Object> deleteMap);

    int deleteTrdProductTempDeal(Map<String, Object> deleteMap);

    int deleteTrdProductTempDealAdd(Map<String, Object> paramMap);

    int deletePtlCashFlow(Map<String, Object> paramMap);

    int deleteEtrCashFlow(Map<String, Object> paramMap);

    List<TrdRepoDurationDto> selectAllData(Map<String, Object> paramMap);

    int updatePtlCashPosition(PtlCashPosition item);

    int updateEtrCashPosition(EtrCashPosition item);


    List<EtrCashPosition> listEtrCashPositionFlow(Map<String, Object> deleteMap);

    List<PtlCashPosition> listPtlCashPositionFlow(Map<String, Object> deleteMap);




}
