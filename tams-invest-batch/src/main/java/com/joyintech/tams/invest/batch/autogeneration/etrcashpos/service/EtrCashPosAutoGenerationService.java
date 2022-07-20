/*
 * 文件名：CashPosAutoGenerationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autogeneration.etrcashpos.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrCashPositionFlowMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autogeneration.etrcashpos.dao.EtrCashPosAutoGenerationMapper;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dao.EtrCommonCashPositionMapper;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrSecCashPositionDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组合现金余额自动生成Service
 *
 * @author 刘二通
 * @version 1.0 2018年11月20日
 * @see EtrCashPosAutoGenerationService
 * @since 1.0
 */
@Service
public class EtrCashPosAutoGenerationService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * CashPosAutoGenerationMapper
     */
    @Autowired
    private EtrCashPosAutoGenerationMapper cashPosAutoGenerationMapper;

    /**
     * EtrCommonCashPositionMapper
     */
    @Autowired
    private EtrCommonCashPositionMapper etrCommonCashPositionMapper;

    /**
     * 现金流水DAO
     */
    @Autowired
    private EtrCashPositionFlowMapper etrCashPositionFlowMapper;

    /**
     * 组合现金余额自动生成
     *
     * @param batchDate 执行日
     * @param productIdList
     * @see
     * @since 1.0
     */
    public void autoGeneration(Date batchDate, List<String> productIdList) {

        // 删除当日持仓数据
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("batchDate", batchDate);
        if (productIdList.size() < 1000) {
            paramMap.put("productIdList", productIdList);
        }
        else {
            paramMap.put("productIdList", null);
        }
        cashPosAutoGenerationMapper.deleteTodayCashPosData(paramMap);
        // 处理市价模式流水
        // 查询债券、股票未关闭的委托
        paramMap.put("PRICE_MODEL_PRICE_MODEL", DictCodeListExt2.价格模式_市价指令.getValue());
        paramMap.put("ENTRUST_STATUS_02", DictCodeList.委托状态_待接收.getValue());
        paramMap.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_待分发.getValue());
        paramMap.put("ENTRUST_STATUS_03", DictCodeList.委托状态_已接收.getValue());
        paramMap.put("FINPROD_TYPE2_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        paramMap.put("FINPROD_TYPE2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        List<EntrustCommonDto> etrEntrustList = cashPosAutoGenerationMapper.findEnableEntrust(paramMap);
        if (etrEntrustList != null) {
            etrEntrustList.forEach(etrEntrustComm -> {
                EtrEntrust etrEntrust = etrEntrustComm.getEtrEntrust();
                // 查询已完成委托
                BigDecimal etrFinish = etrCommonCashPositionMapper.getEtrFinishAmt(etrEntrust.getEntrustId());
                // 剩余数量
                BigDecimal 剩余数量 = ArithmeticUtil.subtract(etrEntrust.getEntrustNum(), etrFinish == null ? BigDecimal.ZERO : etrFinish);
                BigDecimal 最新市价 = etrEntrust.getPrice();
                logger.info("债券:{}",etrEntrust.getFinprodId());
                if (etrEntrust.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                	//添加新的参数  add by zhangpei 20201119 start
                    //最新市价 = EtrSecCashPositionDealService.gen委托全价(etrEntrust.getEntrustId(), batchDate, etrEntrust.getTradeMarket());
                    最新市价 = EtrSecCashPositionDealService.gen委托全价(etrEntrust.getEntrustId(), batchDate, etrEntrust.getTradeMarket(),null);
                    //添加新的参数  add by zhangpei 20201119 end
                } else if (etrEntrust.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    最新市价 = EtrCommonCashPositionDealService.getClosePrice(etrEntrust.getEntrustId(), batchDate);
                }
                EtrCashPositionFlowDto flow = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                        etrEntrustComm);
                // 可用现金
                BigDecimal 可用现金 = ArithmeticUtil.multiply(剩余数量, 最新市价);
                String chlId = etrEntrust.getChlId();
                String acctId;
                if (StringUtil.isNotEmpty(etrEntrust.getChlId()) && DictCodeListExt2.划款方式_一次性划款.getValue().equals(
                        EtrCommonCashPositionDealService.queryChlType(etrEntrust.getChlId()))) {
                    acctId = EtrCommonCashPositionDealService.select通道资金账户(etrEntrust.getChlId());
                }
                else {

                    //修改账户取值逻辑，保持和指令交易逻辑统一 20200712 start
                    /*
                    1、通道且为一次性划款的，找通道资金账户；
                    2、非通道交易，如果是通过证券账户，找证券账户的资金账户；
                    3、非通道和证券账户交易，找组合的托管户；
                    */
                    // modify by etliu 20220331 start
                    //
//                    if(StringUtil.isNotEmpty(etrEntrustComm.getEtrEntrustAdd().getSecAcctId()) ){
//                        acctId = EtrCommonCashPositionDealService.select资金账户(etrEntrustComm.getEtrEntrustAdd().getSecAcctId(), etrEntrust.getCcy());
//                    }else{
//                        acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
//                                DictCodeListExt2.资金账户类型_托管户.getValue());
//                    }
                    acctId = EtrSecCashPositionDealService.gen账号(etrEntrust.getTradeMarket(), etrEntrust.getChlId(), etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
                            etrEntrustComm.getEtrEntrustAdd().getSecAcctId(),
                            etrEntrustComm.getEtrEntrust().getCounterId(), flow);
                    // modify by etliu 20220331 end
                    //修改账户取值逻辑，保持和指令交易逻辑统一 20200712 end
//                    acctId = EtrCommonCashPositionDealService.select资金账户(etrEntrustComm.getEtrEntrustAdd().getSecAcctId(), etrEntrust.getCcy());
                    flow.setChlFinprodId(null);
                }
                flow.setAcctId(acctId);
                flow.setAmount(可用现金);
                flow.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
                flow.setCdate(batchDate);
                etrCashPositionFlowMapper.insertSelective(flow);
                // 冲销剩余
                EtrCashPositionFlowDto flow2 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(flow2, flow);
                flow2.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                flow2.setAmount(ArithmeticUtil.multiply(cashPosAutoGenerationMapper.findReCash(etrEntrust.getEntrustId()),
                        new BigDecimal(-1)));
                flow2.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                flow2.setCdate(batchDate);
                etrCashPositionFlowMapper.insertSelective(flow2);
            });
        }
        // 生成当日现金余额
        paramMap.put("POSITION_TYPE_E01", DictCodeListExt1.持仓类型_委托指令可用.getValue());
        paramMap.put("POSITION_TYPE_E02", DictCodeListExt1.持仓类型_委托指令预增.getValue());
        paramMap.put("lastDate", DateUtil.addDay(batchDate, -1));
        cashPosAutoGenerationMapper.insertTodayCashPosData(paramMap);
    }
}
