package com.joyintech.tams.invest.trade.transfer.position.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import com.joyintech.tams.systemtools.interf.IPlatformDate;

@Service
public class PositionRequestServiceFundAssetTransfer001DtoRequest {
    /**
     *
     1、新建转仓 发起流程提交 调用头寸引擎
     2、新建转仓 退回流程修改提交 调用头寸引擎
     3、新建转仓 保存到待办提交 调用头寸引擎
     4、新建转仓 流程审批结束 调用头寸引擎
     5、转仓撤销 流程审批结束 调用头寸引擎

     1、新建转仓 保存到待办 不调用头寸引擎
     2、转仓撤销 发起流程提交 不调用头寸引擎
     *
     *
     */


    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CommonPositionEngineService commonPositionEngineService;
    @Autowired
    private IPlatformDate iPlateFormDate;
    /**
     * 头寸引擎
     */
    @Autowired
    private PositionRequestHandle positionRequestHandle;

    /**
     * SecTransfer001Dto请求对象使用
     *
     * @param entity
     */
    public void sendFundAssetTransfer001DtoRequest(FundAssetTransfer001Dto entity, String decisionAct, String systemAct,String flowStatus,String balanceCheck) {
        log.info("sendSecTransfer001DtoRequest() called with parameters => 【entity = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        resultMap.put("flowStatus", flowStatus);
        resultMap.put("balanceCheck", balanceCheck);
        this.setCommonField(entity, resultMap, decisionAct);//设置公共字段，个性化的需要在具体接口中覆盖
        this.Oth001(entity, resultMap);
        positionRequestHandle.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param entity
     */
    public void sendDelete(FundAssetTransfer001Dto entity) {
        log.info("sendDelete() called with parameters => 【entity = {}】", JsonUtils.object2Json(entity));
//        String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(), entity.getPs(), entity.getAssetType2());
        positionRequestHandle.doDeleteByWorkFlowId(entity.getWorkflowId(), "TRD", null, null);
    }

    /**
     * 撤销时使用---撤销指令-共通接口
     *
     */
    public void sendRevoke(String tradeId) {
        log.info("sendRevoke() called with parameters => 【tradeId = {}】", tradeId);
        positionRequestHandle.dorevoke(tradeId, "TRD", null, null);
    }

    /**
     * 场内现券-指令    bond002
     *
     * @param entity
     * @param resultMap
     */
    private void Oth001(FundAssetTransfer001Dto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", "Oth001");
        resultMap.put("busiAct", DictCodeListExt3.业务动作_交易下单.getValue());
        resultMap.put("deliveryType", commonPositionEngineService.getDeliveryType(entity.getFinprodId()));
        resultMap.put("settleSpeed", commonPositionEngineService.parseSettleSpreed((String) resultMap.get("deliveryType")));
        resultMap.put("finprodId", entity.getFinprodMarketId());

        //转出和转入数据集
        if (CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())) {
            int bussTrdTransferOrderSize = entity.getBussTrdTransferOrder().size();
            Map<String, FundAssetTransfer001Dto> fundAssetTransfer001DtoMap = entity.getFunAssetTransferDtoList().stream().collect(Collectors.toMap(FundAssetTransfer001Dto::getTradeId, Function.identity(), (v1, v2) -> v1));
            List<Map<String, Object>> ptlOutList = new ArrayList<>(bussTrdTransferOrderSize);
            List<Map<String, Object>> ptlInList = new ArrayList<>(bussTrdTransferOrderSize);
            for (BussTrdTransferOrderDto item : entity.getBussTrdTransferOrder()) {
                Map<String, Object> ptlOutMap = new HashMap<String, Object>();
                Map<String, Object> ptlInMap = new HashMap<String, Object>();
                //组合代码
                ptlOutMap.put("portfolioId", entity.getInvTransPortf());
                ptlInMap.put("portfolioId", fundAssetTransfer001DtoMap.get(item.getTradeId()).getPortfolioId());
                //业务编号
                ptlOutMap.put("tradeId", item.getTradeId());
                ptlInMap.put("tradeId", item.getTradeId());
                //资金账号
                ptlOutMap.put("cashAcctId", item.getPayAcctNo());
                ptlInMap.put("cashAcctId", item.getRecAcctNo());
                //证券管理账户
                ptlOutMap.put("secAcctId", entity.getSecManageAcctId());
                ptlInMap.put("secAcctId", entity.getSecManageAcctId());
                //券面总额
                ptlOutMap.put("secAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradePrin());
                ptlInMap.put("secAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradePrin());
                //交易金额
                ptlOutMap.put("cashAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradeAmt());
                ptlInMap.put("cashAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradeAmt());
                //本方券券面总额
                ptlOutMap.put("ownSecAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradeAmt());
                ptlInMap.put("ownSecAmount", fundAssetTransfer001DtoMap.get(item.getTradeId()).getTradeAmt());
                
                ptlOutMap.put("finprodId", fundAssetTransfer001DtoMap.get(item.getTradeId()).getFinprodId());
            	ptlInMap.put("finprodId", fundAssetTransfer001DtoMap.get(item.getTradeId()).getFinprodId());

                ptlOutList.add(ptlOutMap);
                ptlInList.add(ptlInMap);
            }
            resultMap.put("ptlOutList", ptlOutList);
            resultMap.put("ptlInList", ptlInList);
        }
    }


    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(FundAssetTransfer001Dto entity, Map<String, Object> resultMap, String decisionAct) {
//        String balanceCheck = "N";
//        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
//            balanceCheck = "N";
//        }
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getZcId());
//        resultMap.put("balanceCheck", balanceCheck);
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        resultMap.put("computerId", null);

        //设置头寸业务品种
        String finprodType = "";
        if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
            finprodType = "Fund002";
        }
        resultMap.put("finprodType", finprodType);

        // 交易方向
        resultMap.put("ps", entity.getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put("tradePlace", entity.getTradeMarket());
        // 流程编号	指令的工作流ID
        resultMap.put("workFlowId", entity.getWorkflowId());
        // 日期
        Date plateFormDate = iPlateFormDate.getSysDate();
        resultMap.put("cdate", DateUtil.format(plateFormDate));
        // 币种
        if (StringUtils.isNotEmpty(entity.getCcy())) {
            resultMap.put("ccy", entity.getCcy());
        } else {
            resultMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        resultMap.put("bTotalTradeId", entity.getbTotalTradeId());
    }


}
