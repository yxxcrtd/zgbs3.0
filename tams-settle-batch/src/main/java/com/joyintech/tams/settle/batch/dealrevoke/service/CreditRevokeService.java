package com.joyintech.tams.settle.batch.dealrevoke.service;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信用保护凭证交易撤销服务
 * @author 傅永胜
 * @date 2022/05/17
 * @see CreditRevokeService
 * @since 1.0
 */
@Service("14" + BaseRevokeService.REVOKE_BEAN_NAME)
@Lazy
public class CreditRevokeService implements BaseRevokeService {

    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    /**
     * 信用保护凭证交易撤销
     * @param tradeIdMap 交易编号
     * @param totalTradeIdMap 汇总交易编号
     */
    @Override
    public void doRevoke(Map<String,String> tradeIdMap, Map<String,String> totalTradeIdMap) {
        if(!totalTradeIdMap.isEmpty()) {
            List<String> totalTradeIdList = new ArrayList<>(totalTradeIdMap.keySet());
            Map<String, Object> param = new HashMap<>();
            param.put("totalTradeIdList", totalTradeIdList);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeListStl.共通业务类型_信用保护凭证交易.getValue());

            stlDataGatherService.excutePosition(param);
            investDealRevokeService.revokeDealTotal(totalTradeIdList);
        }
    }

    @Override
    public String getBusiType() {
        return DictCodeListStl.证券品种_信用保护凭证.getValue();
    }
}
