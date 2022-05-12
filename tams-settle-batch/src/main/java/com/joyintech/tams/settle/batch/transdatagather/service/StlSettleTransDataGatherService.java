package com.joyintech.tams.settle.batch.transdatagather.service;

import com.joyintech.tams.common.base.dao.SettleTransDataTotalAddMapper;
import com.joyintech.tams.common.base.dao.SettleTransDataTotalMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
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
import java.util.*;

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
        Map<String,Object> map = new HashMap<>();
        map.put("matchStatus", DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
        map.put("checkResult1", DictCodeListExt3.对账状态结果_一致.getValue());
        map.put("checkResult3", DictCodeListExt3.对账状态结果_手工完成.getValue());
        map.put("REPO_TYPE_H02", DictCodeList.回购类型_质押式竞价回购.getValue());
        map.put("DELIVERY_RESULT_01", DictCodeListExt4.交收结果_正常交收.getValue());
        map.put("DELIVERY_RESULT_02", DictCodeListExt4.交收结果_部分交收.getValue());
        map.put("DELIVERY_RESULT_05", DictCodeListExt4.交收结果_交收通知.getValue());
        map.put("dealDate", dealDate);
        // 债券存续期
        map.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());
        map.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        map.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        map.put("PS_T09", DictCodeList.交易方向_收息.getValue());

        // 协议回购逆回购到期
        map.put("BUSI_SCENE_CLASS_0902RB02", DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue());
        // 竞价回购存续期
        map.put("BUSI_SCENE_CLASS_0901RB02", DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_到期.getValue());
        map.put("BUSI_SCENE_CLASS_0901RS04", DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_到期.getValue());

        map.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        map.put("PS_T11", DictCodeList.交易方向_现金分红.getValue());
        map.put("PS_T12", DictCodeList.交易方向_红利再投.getValue());
        map.put("PS_T13", DictCodeList.交易方向_除权登记.getValue());

        return stlSettleTransDataGatherMapper.selectNeedTotalDataList(map);
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
