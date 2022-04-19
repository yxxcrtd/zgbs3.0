package com.joyintech.tams.settle.batch.transdatagather.dao;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author meifaxin
 * @date 2021/10/29
 * @see StlSettleTransDataGatherMapper
 * @since 1.0
 */
@Repository
public interface StlSettleTransDataGatherMapper {

    int deleteTotalTradeByLastDeal(@Param("dealDate")Date dealDate,@Param("matchStatus")String matchStatus);

    /**
     * 查询数据
     * @param paramMap 查询参数
     * @return 交易编号列表
     */
    List<StlSettleTransDataGatherDto> selectNeedTotalDataList(Map<String, Object> paramMap);

    int batchInsetSettleTransDataTotal(@Param("inputList") List<SettleTransDataTotal> inputList);


    int batchInsetSettleTransDataTotalAdd(@Param("inputList") List<SettleTransDataTotalAdd> inputList);


    int deleteTotalTradeAddByLastDeal(@Param("dealDate")Date dealDate,@Param("matchStatus")String matchStatus);

    int deleteTotalTradeStatusByLastDeal(@Param("matchStatus")String matchStatus);

    int insetSettleTransDataTotalStatus(SettleTransDataTotalStatus settleTransDataTotalStatus);

    int  insetSettleTransDataTotalPledge(SettleTransDataTotalPledge settleTransDataTotalPledge);



}
