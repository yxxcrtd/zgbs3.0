package com.joyintech.tams.settle.batch.dealrevoke.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tams.settle.batch.dealrevoke.dao.InvestDealRevokeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 何阳阳
 * @date 2020/8/4
 * @see BondRevokeService
 * @since 1.0
 */
@Service("01" + BaseRevokeService.REVOKE_BEAN_NAME)
@Lazy
public class BondRevokeService implements BaseRevokeService {

    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    // modify by etliu 20220227 start
    // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
    @Autowired
    private InvestDealRevokeMapper investDealRevokeMapper;
    // modify by etliu 20220227 end

    /**
     * 债券交易撤销
     * @param tradeIdMap 交易编号
     * @param totalTradeIdMap 汇总交易编号
     */
    @Override
    public void doRevoke(Map<String,String> tradeIdMap, Map<String,String> totalTradeIdMap) {
        // 参考债券交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        if(!totalTradeIdMap.isEmpty()) {
//            List<String> totalTradeIdList =  totalTradeIdMap.entrySet().stream().filter(item-> !StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue()))
//                    .map(item->item.getKey()).collect(Collectors.toList());
            // GNS-929：上市流通、质押券出入库的头寸撤销特殊处理 modify by fuyongsheng 20220226 start
            List<String> totalTradeIdList =  totalTradeIdMap.entrySet().stream()
                    .filter(item-> !StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue())
                            && !StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_质押券入库.getValue())
                            && !StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_质押券出库.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(totalTradeIdList)){
                // GNS-929：上市流通、质押券出入库的头寸撤销特殊处理 modify by fuyongsheng 20220226 end
                Map<String, Object> param = new HashMap<>();
                param.put("totalTradeIdList", totalTradeIdList);
                param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
                param.put("busiType", DictCodeList.共通业务类型_债券类资产交易.getValue());

                // modify by etliu 20220227 start
                // 广农商二阶段 GNS-890:【广农商场内清算】场内债券存续期交易重复跑批时，未对原交易数据进行撤销操作
                List<TrdFinProductDealTotal> trdFinProductDealTotalList = investDealRevokeMapper.selectSubTotalTradeData(param);
                if (!StringUtil.isEmptyList(trdFinProductDealTotalList)) {
                    Map<String, List<TrdFinProductDealTotal>> tempTotalTradeIdMap = trdFinProductDealTotalList.stream().collect(Collectors.groupingBy(x -> x.getpTotleTradeId()));
                    List<String> tempTotalTradeIdList = new ArrayList<>();
                    for (String tempTotalTradeId: totalTradeIdList) {
                        if (tempTotalTradeIdMap.containsKey(tempTotalTradeId)) {
                            for (TrdFinProductDealTotal trdFinProductDealTotal : tempTotalTradeIdMap.get(tempTotalTradeId)) {
                                tempTotalTradeIdList.add(trdFinProductDealTotal.getTotleTradeId());
                            }
                        }
                        else {
                            tempTotalTradeIdList.add(tempTotalTradeId);
                        }
                    }
                    param.put("totalTradeIdList", tempTotalTradeIdList);
                }
                // modify by etliu 20220227 end

                stlDataGatherService.excutePosition(param);

                investDealRevokeService.revokeDealTotal(totalTradeIdList);
            }
            List<String> ssltTotalTradeIdList =  totalTradeIdMap.entrySet().stream().filter(item-> StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            //GNS-886:解决撤销报错 modify by fuyongsheng 20220216 start
            if(CollectionUtils.isNotEmpty(ssltTotalTradeIdList)){
                List<String> tradeIds = investDealRevokeService.searchTradeIdsByTotalTradeIds(ssltTotalTradeIdList);
                if(CollectionUtils.isNotEmpty(tradeIds)){
                    // 处理持仓
                    investDealRevokeService.executeSecPositionFlow(tradeIds);

                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 start
                    Map<String, Object> param = new HashMap<>();
                    param.put("tradeIdList", tradeIds);
                    param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
                    param.put("busiType", DictCodeList.共通业务类型_债券类资产交易.getValue());
                    // 处理老头存标识,N:不处理老头存
                    param.put("dealOldPositionFlag", DictCodeList.是否_否.getValue());
                    stlDataGatherService.excutePosition(param);
                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 end
                }
                investDealRevokeService.revokeDealTotal(ssltTotalTradeIdList);
            }
            //GNS-886:解决撤销报错 modify by fuyongsheng 20220216 end

            // GNS-929:质押券出入库的头寸撤销特殊处理 modify by fuyongsheng 20220226 start
            List<String> zyqTotalTradeIdList =  totalTradeIdMap.entrySet().stream()
                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_质押券入库.getValue())
                    || StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_质押券出库.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(zyqTotalTradeIdList)){
                List<String> tradeIds = investDealRevokeService.searchTradeIdsByTotalTradeIds(zyqTotalTradeIdList);
                if(CollectionUtils.isNotEmpty(tradeIds)){
                    // 处理持仓
                    investDealRevokeService.executeSecPositionFlow(tradeIds);
                    // 删除标准券出入库表
                    investDealRevokeService.deleteZyTrdStasecFlows(tradeIds);
                    // 删除标准券出入库明细表
                    investDealRevokeService.deleteZyTrdStasecFlowDetails(tradeIds);

                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 start
                    Map<String, Object> param = new HashMap<>();
                    param.put("tradeIdList", tradeIds);
                    param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
                    param.put("busiType", DictCodeList.共通业务类型_债券类资产交易.getValue());
                    // 处理老头存标识,Y:处理老头存,N:不处理老头存
                    param.put("dealOldPositionFlag", DictCodeList.是否_否.getValue());
                    stlDataGatherService.excutePosition(param);
                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 end
                }
                investDealRevokeService.revokeDealTotal(zyqTotalTradeIdList);
            }
            // GNS-929:质押券出入库的头寸撤销特殊处理 modify by fuyongsheng 20220226 end
        }
    }


    @Override
    public String getBusiType() {
        return DictCodeListExt1.证券品种_债券.getValue();
    }
}
