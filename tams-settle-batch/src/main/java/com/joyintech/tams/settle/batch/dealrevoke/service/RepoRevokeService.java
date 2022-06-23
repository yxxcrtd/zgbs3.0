package com.joyintech.tams.settle.batch.dealrevoke.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/8/4
 * @see RepoRevokeService
 * @since 1.0
 */
@Service("09" + BaseRevokeService.REVOKE_BEAN_NAME)
@Lazy
public class RepoRevokeService implements BaseRevokeService {

    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    /**
     * 回购交易撤销
     * @param tradeIdMap 交易编号 交易方向
     * @param totalTradeIdMap 汇总交易编号 交易方向
     */
    @Override
    public void doRevoke(Map<String,String> tradeIdMap, Map<String,String> totalTradeIdMap) {
        // 交易方向 = 买入 参考债券质押式回购首期交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        // 交易方向= 质押券入库、质押券出库是参照普通交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。
        if(null != tradeIdMap && !tradeIdMap.isEmpty() && tradeIdMap.size()>0) {
            List<String> tradeIdList =  tradeIdMap.entrySet().stream().map(item->item.getKey()).collect(Collectors.toList());
            Map<String, Object> param = new HashMap<>();
            param.put("tradeIdList", tradeIdList);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeList.共通业务类型_质押式回购交易.getValue());
            stlDataGatherService.excutePosition(param);
            investDealRevokeService.revokeDeal(tradeIdList);
        }
        // 参考债券质押式回购到期续做、提前终止、到期交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        //中途换券撤销，交易撤销同普通交易撤销，同时更新回购首期的质押品信息为【质押券置换结果】表的置换前的质押券信息，条件为质押券置换结果.原始业务编号 = 撤销数据.金融产品代码
        if(totalTradeIdMap != null && !totalTradeIdMap.isEmpty() && totalTradeIdMap.size()>0) {
            List<String> totalTradeIdList =  totalTradeIdMap.entrySet().stream()
//                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeList.交易方向_买入.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            Map<String, Object> param = new HashMap<>();
            param.put("totalTradeIdList", totalTradeIdList);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeList.共通业务类型_质押式回购交易.getValue());
            stlDataGatherService.excutePosition(param);
            investDealRevokeService.revokeDealTotal(totalTradeIdList);
            List<String> tqzzTotalTradeIdList =  totalTradeIdMap.entrySet().stream()
                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())
                    || StringUtil.equals(item.getValue(), DictCodeList.交易方向_到期续做.getValue())
                    || StringUtil.equals(item.getValue(), DictCodeList.交易方向_到期.getValue()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(tqzzTotalTradeIdList)){
                investDealRevokeService.deletePtlSecPositionFlow(tqzzTotalTradeIdList);
                investDealRevokeService.deleteEtrSecPositionFlow(tqzzTotalTradeIdList);
                investDealRevokeService.deleteEtrCashPositionFlow(tqzzTotalTradeIdList);
                investDealRevokeService.deletePtlCashPositionFlow(tqzzTotalTradeIdList);
            }
        }
    }

    @Override
    public String getBusiType() {
        return DictCodeListExt1.证券品种_回购.getValue();
    }

}
