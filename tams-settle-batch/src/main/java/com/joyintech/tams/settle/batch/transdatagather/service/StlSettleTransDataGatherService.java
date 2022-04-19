package com.joyintech.tams.settle.batch.transdatagather.service;

import com.joyintech.tams.common.base.dao.SettleTransDataTotalAddMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.transdatagather.dao.StlSettleTransDataGatherMapper;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meifaxin
 * @date 2021/10/29
 * @see StlSettleTransDataGatherService
 * @since 1.0
 */
@Service
public class StlSettleTransDataGatherService {


    @Autowired
    private StlSettleTransDataGatherMapper stlSettleTransDataGatherMapper;
    @Autowired
    private SettleTransDataTotalMapper settleTransDataTotalMapper;
    @Autowired
    private SettleTransDataTotalAddMapper settleTransDataTotalAddMapper;

    @Autowired
    private StlDownDataDealMapper stlDownDataDealMapper;
    /**
     * @param dealDate 执行日期
     * @return 为支持重复跑批，先将上次批处理生出的数据删除。
     */
    public int deleteTotalTradeByLastDeal(Date dealDate) {
        String matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();
        return stlSettleTransDataGatherMapper.deleteTotalTradeByLastDeal(dealDate,matchStatus);
    }

    /**
     * @param dealDate 执行日期
     * @return 对账一致或者不需要对账的成交数据明细表数据
     */
    public List<StlSettleTransDataGatherDto> selectNeedTotalDataList(Date dealDate) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("dealDate", dealDate);
        paramMap.put("matchStatus", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        paramMap.put("checkResult1", DictCodeListExt3.对账状态结果_一致.getValue());
        paramMap.put("checkResult3", DictCodeListExt3.对账状态结果_手工完成.getValue());
        paramMap.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        paramMap.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        paramMap.put("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
        paramMap.put("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
        paramMap.put("REPO_TYPE_H02", DictCodeList.回购类型_质押式竞价回购.getValue());
        paramMap.put("DELIVERY_RESULT_01", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        paramMap.put("DELIVERY_RESULT_02", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());

        return stlSettleTransDataGatherMapper.selectNeedTotalDataList(paramMap);
    }

    public int batchInsetSettleTransDataTotal(List<SettleTransDataTotal> inputList) {
        return stlSettleTransDataGatherMapper.batchInsetSettleTransDataTotal(inputList);
    }

    public int batchInsetSettleTransDataTotalAdd(List<SettleTransDataTotalAdd> inputList) {
        return stlSettleTransDataGatherMapper.batchInsetSettleTransDataTotalAdd(inputList);
    }

    /**
     * @param dealDate 执行日期
     * @return 为支持重复跑批，先将上次批处理生出的数据删除。
     */
    public int deleteTotalTradeAddByLastDeal(Date dealDate) {
        String matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();
        return stlSettleTransDataGatherMapper.deleteTotalTradeAddByLastDeal(dealDate,matchStatus);
    }
    /**
     * @param dealDate 执行日期
     * @return 为支持重复跑批，先将上次批处理生出的数据删除。
     */
    public int deleteTotalTradeStatusByLastDeal() {
        String matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();
        return stlSettleTransDataGatherMapper.deleteTotalTradeStatusByLastDeal(matchStatus);
    }

    public int insetSettleTransDataTotalStatus(SettleTransDataTotalStatus settleTransDataTotalStatus) {
        return stlSettleTransDataGatherMapper.insetSettleTransDataTotalStatus(settleTransDataTotalStatus);
    }

    public int insetSettleTransDataTotal(SettleTransDataTotal settleTransDataTotal) {
        return settleTransDataTotalMapper.insert(settleTransDataTotal);
    }

    public int insetSettleTransDataTotalAdd(SettleTransDataTotalAdd settleTransDataTotalAdd) {
        return settleTransDataTotalAddMapper.insert(settleTransDataTotalAdd);
    }

    public int insetSettleTransDataTotalPledge(SettleTransDataTotalPledge settleTransDataTotalPledge) {
        return stlSettleTransDataGatherMapper.insetSettleTransDataTotalPledge(settleTransDataTotalPledge);
    }

    public List<SettleTransData> selectSqSettleTransDatas(StlSettleTransDataGatherDto item) {
        return stlDownDataDealMapper.selectSqSettleTransDatas(item.getfCjbh(), item.getSeatNumber(), item.getSecAcctId());
    }
}
