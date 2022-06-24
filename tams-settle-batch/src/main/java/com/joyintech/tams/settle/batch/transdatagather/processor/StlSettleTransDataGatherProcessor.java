package com.joyintech.tams.settle.batch.transdatagather.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.entity.SettleTransDataTotal;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalAdd;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalPledge;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalStatus;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.transdatagather.dto.StlSettleTransDataGatherParamDto;
import com.joyintech.tams.settle.batch.transdatagather.service.StlSettleTransDataGatherService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author meifaxin
 * @date 2021/10/29
 * @see StlSettleTransDataGatherProcessor
 * @since 1.0
 */
@Service
@Lazy
public class StlSettleTransDataGatherProcessor extends BaseProcessor<NeedOperateEntity, NeedToWriteEntity> {
    @Autowired
    private StlSettleTransDataGatherService stlSettleTransDataGatherService;
    @Override
    public NeedToWriteEntity process(NeedOperateEntity entity) throws Exception {
        StlSettleTransDataGatherParamDto item = (StlSettleTransDataGatherParamDto)entity;
        // 交易编号
        List<SettleTransDataTotal> transDataTotalList = item.getSettleTransDataTotalList();
        // 汇总交易编号
        List<SettleTransDataTotalAdd> transDataTotalAddList = item.getSettleTransDataTotalAddList();
        transDataTotalList.forEach(settleTransDataTotal -> {
            stlSettleTransDataGatherService.insetSettleTransDataTotal(settleTransDataTotal);
        });
        transDataTotalAddList.forEach(settleTransDataTotalAdd -> {
            stlSettleTransDataGatherService.insetSettleTransDataTotalAdd(settleTransDataTotalAdd);
        });
        //批量写入汇总状态表
        transDataTotalList.forEach(settleTransDataTotal -> {
            SettleTransDataTotalStatus status=new SettleTransDataTotalStatus();
            status.setSeqNo(settleTransDataTotal.getSeqId());
            status.setMatchStatus(DictCodeListExt3.下行交易匹配状态_未匹配.getValue());
            stlSettleTransDataGatherService.insetSettleTransDataTotalStatus(status);
        });
        // 汇总质押品
        List<SettleTransDataTotalPledge> transDataTotalPledgeList = item.getSettleTransDataTotalPledge();
        if(CollectionUtils.isNotEmpty(transDataTotalPledgeList)){
            transDataTotalPledgeList.forEach(pledge -> {
                stlSettleTransDataGatherService.insetSettleTransDataTotalPledge(pledge);
            });
        }
        return null;
    }
}
