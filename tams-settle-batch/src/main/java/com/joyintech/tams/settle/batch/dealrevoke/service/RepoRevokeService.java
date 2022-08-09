package com.joyintech.tams.settle.batch.dealrevoke.service;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
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

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    @Autowired
    private EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    @Autowired
    private InvestDealRevokeMapper investDealRevokeMapper;

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

            // 筛选协议回购特殊处理
            List<String> tradeIdListH01 = new ArrayList<>();
            List<String> tradeIdListOther = new ArrayList<>();
            for(String tradeId : tradeIdList){
                TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(tradeId);
                if(trdProductDealAdd != null && StringUtil.isNotEmpty(trdProductDealAdd.getEntrustId())){
                    EtrEntrustAdd entrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
                    if(entrustAdd != null && DictCodeList.回购类型_质押式协议回购.getValue().equals(entrustAdd.getAssetType1())){
                        tradeIdListH01.add(tradeId);
                        continue;
                    }
                }
                tradeIdListOther.add(tradeId);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("tradeIdList", tradeIdListOther);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 处理非协议回购头寸
            stlDataGatherService.excutePosition(param);
            // 处理协议回购头寸
            this.excuteH01Position(tradeIdListH01, new ArrayList<>());
            investDealRevokeService.revokeDeal(tradeIdList);
        }
        // 参考债券质押式回购到期续做、提前终止、到期交易撤销逻辑，生成已生效的撤销交易数据，同时反向更新头寸信息、持仓信息以及委托完成信息。注意不要调用对外发送逻辑。
        //中途换券撤销，交易撤销同普通交易撤销，同时更新回购首期的质押品信息为【质押券置换结果】表的置换前的质押券信息，条件为质押券置换结果.原始业务编号 = 撤销数据.金融产品代码
        if(totalTradeIdMap != null && !totalTradeIdMap.isEmpty() && totalTradeIdMap.size()>0) {
            List<String> totalTradeIdList =  totalTradeIdMap.entrySet().stream()
//                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeList.交易方向_买入.getValue()))
                    .map(item->item.getKey()).collect(Collectors.toList());
            // 筛选协议回购特殊处理
            List<String> totalTradeIdListH01 = new ArrayList<>();
            List<String> totalTradeIdListOther = new ArrayList<>();
            for(String totalTradeId : totalTradeIdList){
                TrdDealTotalAdd trdDealTotalAdd = trdDealTotalAddMapper.selectByPrimaryKey(totalTradeId);
                if(trdDealTotalAdd != null && StringUtil.isNotEmpty(trdDealTotalAdd.getEntrustId())){
                    EtrEntrustAdd entrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdDealTotalAdd.getEntrustId());
                    if(entrustAdd != null && DictCodeList.回购类型_质押式协议回购.getValue().equals(entrustAdd.getAssetType1())){
                        totalTradeIdListH01.add(totalTradeId);
                        continue;
                    }
                }
                totalTradeIdListOther.add(totalTradeId);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("totalTradeIdList", totalTradeIdListOther);
            param.put("status", DictCodeListExt1.交易流程状态_撤销.getValue());
            param.put("busiType", DictCodeList.共通业务类型_质押式回购交易.getValue());
            // 处理非协议回购头寸
            stlDataGatherService.excutePosition(param);
            // 处理协议回购头寸
            this.excuteH01Position(new ArrayList<>(), totalTradeIdListH01);
            investDealRevokeService.revokeDealTotal(totalTradeIdList);
//            List<String> tqzzTotalTradeIdList =  totalTradeIdMap.entrySet().stream()
//                    .filter(item-> StringUtil.equals(item.getValue(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())
//                    || StringUtil.equals(item.getValue(), DictCodeList.交易方向_到期续做.getValue())
//                    || StringUtil.equals(item.getValue(), DictCodeList.交易方向_到期.getValue()))
//                    .map(Map.Entry::getKey).collect(Collectors.toList());
//            if(CollectionUtils.isNotEmpty(tqzzTotalTradeIdList)){
//                investDealRevokeService.deletePtlSecPositionFlow(tqzzTotalTradeIdList);
//                investDealRevokeService.deleteEtrSecPositionFlow(tqzzTotalTradeIdList);
//                investDealRevokeService.deleteEtrCashPositionFlow(tqzzTotalTradeIdList);
//                investDealRevokeService.deletePtlCashPositionFlow(tqzzTotalTradeIdList);
//            }
        }
    }

    /**
     * 处理协议回购头寸
     * @param tradeIdList
     * @param totalTradeIdList
     */
    private void excuteH01Position(List<String> tradeIdList, List<String> totalTradeIdList){
        if(CollectionUtils.isEmpty(tradeIdList) && CollectionUtils.isEmpty(totalTradeIdList)){
            return;
        }
        if(CollectionUtils.isEmpty(tradeIdList) && CollectionUtils.isNotEmpty(totalTradeIdList)){
            tradeIdList = investDealRevokeService.searchTradeIdsByTotalTradeIds(totalTradeIdList);
        }
        List<PtlSecPositionFlowDto> ptlSecPositionFlows = investDealRevokeService.getPtlSecPositionFlowsByTradeIds(tradeIdList);
        // 撤销登录反向流水
        if(CollectionUtils.isNotEmpty(ptlSecPositionFlows)){
            for(PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows){
                PtlSecPositionFlowDto ptlSecPositionFlowDto = new PtlSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(ptlSecPositionFlowDto, ptlSecPositionFlow);
                ptlSecPositionFlowDto.setSeqno(IDGenerator.generate());
                ptlSecPositionFlowDto.setTradeId(ptlSecPositionFlowDto.getTradeId() + "R");
                ptlSecPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                ptlSecPositionFlowDto.setAmount(ptlSecPositionFlowDto.getAmount().negate());
                ptlSecPositionFlowMapper.insertSelective(ptlSecPositionFlowDto);
            }

            //更新余额
            ptlSecPositionFlows.forEach(item ->{
                if(null!=item.getAmount()){
                    item.setAmount(item.getAmount().negate());
                    PtlSecPosition ptlSecPosition = new PtlSecPosition();
                    BeanUtil.copyPropertiesIgnoreNull(ptlSecPosition, item);
                    investDealRevokeMapper.updatePtlSecPosition(ptlSecPosition);
                }
            });
        }

        List<EtrSecPositionFlowDto> etrSecPositionFlows = investDealRevokeService.getEtrSecPositionFlowsByTradeIds(tradeIdList);
        if(CollectionUtils.isNotEmpty(etrSecPositionFlows)){
            // 撤销登录反向流水
            for(EtrSecPositionFlowDto etrSecPositionFlow : etrSecPositionFlows){
                EtrSecPositionFlowDto etrSecPositionFlowDto = new EtrSecPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto, etrSecPositionFlow);
                etrSecPositionFlowDto.setSeqno(IDGenerator.generate());
                etrSecPositionFlowDto.setTradeId(etrSecPositionFlowDto.getTradeId() + "R");
                etrSecPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                etrSecPositionFlowDto.setAmount(etrSecPositionFlowDto.getAmount().negate());
                etrSecPositionFlowMapper.insertSelective(etrSecPositionFlowDto);
            }

            // 更新余额
            etrSecPositionFlows.forEach(item ->{
                if(null!=item.getAmount()){
                    item.setAmount(item.getAmount().negate());
                    EtrSecPosition etrSecPosition = new EtrSecPosition();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPosition, item);
                    investDealRevokeMapper.updateEtrSecPosition(etrSecPosition);
                }
            });
        }

        List<StlPayOrders> stlPayOrders = investDealRevokeService.getStlPayOrderIdsByTradeIds(tradeIdList);
        if(CollectionUtils.isNotEmpty(stlPayOrders)){
            List<String> orderIds = stlPayOrders.stream().map(StlPayOrders::getOrderId).collect(Collectors.toList());
            List<PtlCashPositionFlowDto> ptlCashPositionFlowDtos = investDealRevokeService.getPtlCashPositionFlowsByOrderIds(orderIds);
            if(CollectionUtils.isNotEmpty(ptlCashPositionFlowDtos)){
                // 撤销登录反向流水
                for(PtlCashPositionFlowDto ptlCashPositionFlow : ptlCashPositionFlowDtos){
                    PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(ptlCashPositionFlowDto, ptlCashPositionFlow);
                    ptlCashPositionFlowDto.setSeqno(IDGenerator.generate());
                    ptlCashPositionFlowDto.setOrderId(ptlCashPositionFlowDto.getOrderId() + "R");
                    ptlCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                    ptlCashPositionFlowDto.setAmount(ptlCashPositionFlowDto.getAmount().negate());
                    ptlCashPositionFlowMapper.insertSelective(ptlCashPositionFlowDto);
                }
                // 更新余额
                ptlCashPositionFlowDtos.forEach(item ->{
                    if(null!=item.getAmount()){
                        item.setAmount(item.getAmount().negate());
                        PtlCashPosition ptlCashPosition = new PtlCashPosition();
                        BeanUtil.copyPropertiesIgnoreNull(ptlCashPosition, item);
                        investDealRevokeMapper.updatePtlCashPosition(ptlCashPosition);
                    }
                });
            }

            List<EtrCashPositionFlowDto> etrCashPositionFlows = investDealRevokeService.getEtrCashPositionFlowsByOrderIds(orderIds);
            if(CollectionUtils.isNotEmpty(etrCashPositionFlows)){
                // 撤销登录反向流水
                for(EtrCashPositionFlowDto etrCashPositionFlow : etrCashPositionFlows){
                    EtrCashPositionFlowDto etrCashPositionFlowDto = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto, etrCashPositionFlow);
                    etrCashPositionFlowDto.setSeqno(IDGenerator.generate());
                    etrCashPositionFlowDto.setOrderId(etrCashPositionFlowDto.getOrderId() + "R");
                    etrCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                    etrCashPositionFlowDto.setAmount(etrCashPositionFlowDto.getAmount().negate());
                    etrCashPositionFlowMapper.insertSelective(etrCashPositionFlowDto);
                }
                // 更新余额
                etrCashPositionFlows.forEach(item ->{
                    if(null!=item.getAmount()){
                        item.setAmount(item.getAmount().negate());
                        EtrCashPosition etrCashPosition = new EtrCashPosition();
                        BeanUtil.copyPropertiesIgnoreNull(etrCashPosition, item);
                        investDealRevokeMapper.updateEtrCashPosition(etrCashPosition);
                    }
                });
            }
        }
        // 删除委托完成度
        Map<String, Object> param = new HashMap<>();
        param.put("trdIdList", tradeIdList);
        investDealRevokeMapper.deleteEtrFinishStatusByTrdId(param);
    }

    @Override
    public String getBusiType() {
        return DictCodeListExt1.证券品种_回购.getValue();
    }

}
