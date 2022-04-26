package com.joyintech.tams.settle.batch.transdatagather.service;

import com.joyintech.tams.common.base.dao.SettleTransDataTotalAddMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.transdatagather.dao.StlSettleTransDataGatherMapper;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

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
        String matchStatus = DictCodeListExt3.下行交易匹配状态_未匹配.getValue();
        String checkResult1=DictCodeListExt3.对账状态结果_一致.getValue();
        String checkResult3=DictCodeListExt3.对账状态结果_手工完成.getValue();
        String PS_T05= DictCodeList.交易方向_申购确认.getValue();
        return stlSettleTransDataGatherMapper.selectNeedTotalDataList(dealDate,matchStatus,checkResult1,checkResult3,PS_T05);
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

    /**
     * 深交所协议回购_到期续作查询首期(券商文件中vdate字段为空,所以传null)
     * @param item
     * @return
     */
    public List<SettleTransData> selectSqSettleTransDatas(StlSettleTransDataGatherDto item) {
        return trdCommonBaseService.selectSqSettleTransDatas(item.getfCjbh(), item.getSeatNumber(), item.getSecAcctId(), null);
    }
}
