package com.joyintech.tams.settle.batch.dealrevoke.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
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
 * @see SharesRevokeService
 * @since 1.0
 */
@Service("06" + BaseRevokeService.REVOKE_BEAN_NAME)
@Lazy
public class SharesRevokeService implements BaseRevokeService {

    @Autowired
    private InvestDealRevokeService investDealRevokeService;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    /**
     * 股票交易撤销
     * @param tradeIdMap 交易编号 交易方向
     * @param totalTradeIdMap 汇总交易编号 交易方向
     */
    @Override
    public void doRevoke(Map<String,String> tradeIdMap, Map<String,String> totalTradeIdMap) {
        // 参考股票交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        if(tradeIdMap != null && !tradeIdMap.isEmpty() && tradeIdMap.size()>0) {
            List<String> tradeIdList =  tradeIdMap.entrySet().stream().filter(item-> !StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            Map<String, Object> param = new HashMap<>();
            param.put("tradeIdList", tradeIdList);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeListExt2.共通业务类型_股票投资交易.getValue());
            stlDataGatherService.excutePosition(param);
            investDealRevokeService.revokeDeal(tradeIdList);
            // GNS-902:共通里已经处理过一次PaTrdProductDeal modify by fuyongsheng 20220226 start
//            for (String tradeId : tradeIdList) {
//                PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(tradeId);
//                // GZYHTZJY-1084:判断paTrdProductDeal是否为空 modify fuyongsheng 20220112 start
//                if(paTrdProductDeal != null){
//                    paTrdProductDeal.setTradeId(paTrdProductDeal.getTradeId() + "R");
//                    paTrdProductDeal.setCreateUser(GlobalConstant.SYSTEM_USER);
//                    paTrdProductDeal.setCreateTime(new Date());
//                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
//                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
//                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
//                }
//                // GZYHTZJY-1084:判断paTrdProductDeal是否为空 modify fuyongsheng 20220112 end
//            }
            // GNS-902:共通里已经处理过一次PaTrdProductDeal modify by fuyongsheng 20220226 end
        }
        // 参考股票分红送转交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        if(totalTradeIdMap != null && !totalTradeIdMap.isEmpty() && totalTradeIdMap.size()>0) {
            List<String> totalTradeIdList =  totalTradeIdMap.entrySet().stream()
                    .filter(item-> !StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            Map<String, Object> param = new HashMap<>();
            param.put("totalTradeIdList", totalTradeIdList);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeListExt2.共通业务类型_股票投资交易.getValue());
            stlDataGatherService.excutePosition(param);
            investDealRevokeService.revokeDealTotal(totalTradeIdList);

            List<String> ssltTotalTradeIdList = totalTradeIdMap.entrySet().stream()
                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeList.交易方向_上市流通.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            //GNS-886:解决撤销报错 modify by fuyongsheng 20220216 start
            if(CollectionUtils.isNotEmpty(ssltTotalTradeIdList)){
                List<String> tradeIds = investDealRevokeService.searchTradeIdsByTotalTradeIds(ssltTotalTradeIdList);
                if(CollectionUtils.isNotEmpty(tradeIds)){
                    // 处理持仓
                    investDealRevokeService.executeSecPositionFlow(tradeIds);
                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 start
                    param = new HashMap<>();
                    param.put("tradeIdList", tradeIds);
                    param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
                    param.put("busiType", DictCodeListExt2.共通业务类型_股票投资交易.getValue());
                    // 处理老头存标识,Y:处理老头存,N:不处理老头存
                    param.put("dealOldPositionFlag", DictCodeList.是否_否.getValue());
                    stlDataGatherService.excutePosition(param);
                    // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库撤销新头寸需特殊处理 modify by fuyongsheng 20220327 end
                }
                investDealRevokeService.revokeDealTotal(ssltTotalTradeIdList);
            }
            //GNS-886:解决撤销报错 modify by fuyongsheng 20220216 end
        }
    }

    @Override
    public String getBusiType() {
        return DictCodeListExt1.证券品种_股票.getValue();
    }
}
