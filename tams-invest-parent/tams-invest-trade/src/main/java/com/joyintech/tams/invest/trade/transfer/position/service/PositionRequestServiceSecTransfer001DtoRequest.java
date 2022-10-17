package com.joyintech.tams.invest.trade.transfer.position.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.position.servive.PositionRequestHandle;
import com.joyintech.tams.systemtools.interf.IPlatformDate;

@Service
public class PositionRequestServiceSecTransfer001DtoRequest {
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
    public void sendSecTransfer001DtoRequest(SecTransfer001Dto entity, String decisionAct, String systemAct,String flowStatus) {
        log.info("sendSecTransfer001DtoRequest() called with parameters => 【entity = {}】, 【decisionAct = {}】, 【systemAct = {}】", JsonUtils.object2Json(entity), decisionAct, systemAct);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", decisionAct);
        resultMap.put("systemAct", systemAct);
        resultMap.put("flowStatus", flowStatus);
        this.setCommonField(entity, resultMap,decisionAct);//设置公共字段，个性化的需要在具体接口中覆盖
        this.Oth001(entity, resultMap);
        positionRequestHandle.send(resultMap);
    }

    /**
     * doDelete时使用---作废指令-共通接口
     *
     * @param entity
     */
    public void sendDelete(SecTransfer001Dto entity) {
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
    private void Oth001(SecTransfer001Dto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", "Oth001");
        resultMap.put("busiAct", DictCodeListExt3.业务动作_交易下单.getValue());
        resultMap.put("deliveryType", commonPositionEngineService.getDeliveryType(entity.getFinprodId()));
        resultMap.put("settleSpeed", commonPositionEngineService.parseSettleSpreed((String) resultMap.get("deliveryType")));
        resultMap.put("finprodId", entity.getFinprodId());

        //转出和转入数据集
        if (CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())) {
            int bussTrdTransferOrderSize = entity.getBussTrdTransferOrder().size();
            List<Map<String, Object>> ptlOutList = new ArrayList<>(bussTrdTransferOrderSize);
            List<Map<String, Object>> ptlInList = new ArrayList<>(bussTrdTransferOrderSize);
            for (SecTransferDto secTransferDto : entity.getSecResoluteDtoList()) {
                // 发新头寸转出转入 tradeId 都用转出的totalTradeId。 撤销的时候传转出的totalTradeId即可
                String totalTradeId = "";
                for (BaseTradeInfoDto info : secTransferDto.getBaseTradeInfoDtos()) {
                    TrdProductDeal trdProductDeal = info.getTrdProductDeal();
                    if ("T02".equals(trdProductDeal.getPs())) {
                        totalTradeId = trdProductDeal.getTotleTradeId();
                    }
                }
                for (BaseTradeInfoDto item : secTransferDto.getBaseTradeInfoDtos()) {
                    Map<String, Object> result = new HashMap<>();
                    StlPayOrders order = item.getStlPayOrders();
                    result.put("portfolioId", order.getPortfolioId());  //组合代码
                    result.put("tradeId", totalTradeId);  //业务编号
                    result.put("cashAcctId", order.getMyAcctNo());   //资金账号
                    result.put("secAcctId", entity.getSecManageAcctId()); //证券管理账户
                    result.put("secAmount", secTransferDto.getTradeAmt()); //券面总额
                    result.put("cashAmount", secTransferDto.getTradeRepAmt());  //交易金额
                    result.put("ownSecAmount", secTransferDto.getTradeAmt());   //本方券券面总额
                    result.put("finprodId", entity.getFinprodId());
                    if ("T02".equals(item.getTrdProductDeal().getPs())) {
                        ptlOutList.add(result);
                    } else {
                        ptlInList.add(result);
                    }
                }
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
    private void setCommonField(SecTransfer001Dto entity, Map<String, Object> resultMap,String decisionAct) {
        String balanceCheck = "Y";
        if (StringUtil.isNotEmpty(entity.getSoftSkipMode()) && StringUtil.equals(entity.getSoftSkipMode(), "Y")) {
            balanceCheck = "N";
        }
        resultMap.put("userId", SecurityUtil.getLoginUser().getUserName());
        resultMap.put("totalTradeId", entity.getZcId());
        resultMap.put("balanceCheck", balanceCheck);
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        resultMap.put("computerId", null);
        
        //设置头寸业务品种
        String finprodType = "";
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodId().split("_")[0])) {
            if (DictCodeListExt1.交易场所_商业银行柜台.getValue().equals(entity.getTradeMarket())
            		|| DictCodeListExt1.交易场所_银行间.getValue().equals(entity.getTradeMarket())) {
            	finprodType = "Bond001";
            } else if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())
            		|| DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            	finprodType = "Bond002";
            } 
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
