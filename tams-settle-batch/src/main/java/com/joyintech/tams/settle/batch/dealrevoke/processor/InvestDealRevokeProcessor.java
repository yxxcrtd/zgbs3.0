package com.joyintech.tams.settle.batch.dealrevoke.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.settle.batch.dealrevoke.dto.DealRevokeParamDto;
import com.joyintech.tams.settle.batch.dealrevoke.service.BaseRevokeService;
import com.joyintech.tams.settle.batch.dealrevoke.service.InvestDealRevokeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 何阳阳
 * @date 2020/8/3
 * @see InvestDealRevokeProcessor
 * @since 1.0
 */
@Service
@Lazy
public class InvestDealRevokeProcessor extends BaseProcessor<NeedOperateEntity, NeedToWriteEntity> {
    /**
     *
     */
    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Override
    public NeedToWriteEntity process(NeedOperateEntity entity) throws Exception {
        DealRevokeParamDto item = (DealRevokeParamDto)entity;
        // 证券品种
        String secType = item.getSecType();
        // 交易编号
        List<String> tradeIdList = item.getTradeIdList();
        // 汇总交易编号
        List<String> totalTradeIdList = item.getTotalTradeIdList();
        // 交易撤销逻辑
        BaseRevokeService revokeService = (BaseRevokeService)SpringContextUtil.getBean(secType + BaseRevokeService.REVOKE_BEAN_NAME);
        if(CollectionUtils.isNotEmpty(tradeIdList) || CollectionUtils.isNotEmpty(totalTradeIdList)) {
            revokeService.doRevoke(item.getTradeIdMap(), item.getTotalTradeIdMap());
        }
        List<String> orderIds = new ArrayList<>();
        if (!StringUtil.isEmptyList(tradeIdList)){
            orderIds.addAll(tradeIdList);
        }
        if (!StringUtil.isEmptyList(totalTradeIdList)){
            orderIds.addAll(totalTradeIdList);
        }
        // 删除日间成交数据形成的头寸流水信息
        investDealRevokeService.deletePositionFlow(item.getTradeDate(), item.getPortfolioIdList(), revokeService.getBusiType(),orderIds);
        // 根据交易编号删除日间指令现金流水（此逻辑添加时因为日间委托现金流水表的orderId存在值是交易编号的记录）
        investDealRevokeService.deleteEtrCashPositionFlowByTrdId(tradeIdList, totalTradeIdList);

        //删除日终交易占用的委托完成度信息前，先更新指令状态-》修改为已接收
        investDealRevokeService.updateEtrStatusByTrdId(tradeIdList,totalTradeIdList);
        // 删除日终交易占用的委托完成度信息（此逻辑添加时因为日终批处理生成的数据要删除）
        investDealRevokeService.deleteEtrFinishStatusByTrdId(tradeIdList, totalTradeIdList);

        // modify by etliu 20220227 start
        // 广农商二阶段 GNS-963:【广农商场内清算】协议回购上交所存续期（解除质押、到期购回、到期续做、提前购回）指令提交后，指令持仓更新不对
        // 删除日间生成的交易类金融产品
        if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), secType)) {
            investDealRevokeService.deleteDayFinTradeProductData(getBatchExecuteInfo().getExecuteDate());
        }
        // modify by etliu 20220227 end
        return null;
    }
}
