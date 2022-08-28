/*
 * 文件名：BondAutoDurDealGenerateProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autodurdealgenerate.exchangecommonrepo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.StlPayTempOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdProductTempDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductTempDealMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.impl.BussTrdTransferOrderImplService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.autodurdealgenerate.exchangecommonrepo.dao.ExCommonRepoAutoDurDealGenerateMapper;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.TrdRepoDurationDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * processor相关业务处理
 *
 * @author zznn
 * @version 1.0 2022年01月11日
 * @see ExCommonRepoAutoDurDealProcessorService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class ExCommonRepoAutoDurDealProcessorService extends BaseService {


    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExCommonRepoAutoDurDealGenerateMapper exCommonRepoAutoDurDealGenerateMapper;

    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private BaseGetBussinessId baseGetBussinessId;

    @Autowired
    private BussTrdTransferOrderImplService bussTrdTransferOrderImplService;
    @Autowired
    private StlPayTempOrdersMapper stlPayTempOrdersMapper;

    @Autowired
    private TrdProductTempDealMapper trdProductTempDealMapper;

    @Autowired
    private TrdProductTempDealAddMapper trdProductTempDealAddMapper;

    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;



    /**
     * process 数据处理
     *
     * @param batchDate 传递参数
     * @param productIdList 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public void execute(Date batchDate, List<String> productIdList) {
        List<Map<String, Object>> rollMapList = new ArrayList<>();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("busiType", DictCodeListExt3.头寸业务类型_通用回购到期.getValue());
        paramMap.put("batchDate", batchDate);
        //需要撤销的数据
        List<TrdProductTempDeal> trdProductTempDealList = exCommonRepoAutoDurDealGenerateMapper.selectTrdProductTempDealRevokeData(paramMap);
        try {
            //发送撤销报文
            List<String> tradeIdList = sendRevokePosition(trdProductTempDealList, rollMapList);
            //删除当日数据与头寸
            deleteTradeAndOldPosition( tradeIdList);
            //查询需要处理的数据源
            List<TrdRepoDurationDto> tradeDataList = exCommonRepoAutoDurDealGenerateMapper.selectAllData(paramMap);
            if(StringUtil.isEmptyList(tradeDataList)){//无数据  不在处理
                return;
            }
            for (TrdRepoDurationDto dto : tradeDataList) {
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
                dto.setBusiCode(autoPeriodCommonService.buildBussinessCode(parameter));
                TrdProductTempDeal tpd = genTrdProductTempDeal(dto);
                TrdProductTempDealAdd tpda = genTrdProductTempDealAdd(dto);
                StlPayTempOrders spo = genStlPayTempOrders(tpd, tpda);
                //入库数据及占用老头寸
                stlPayTempOrdersMapper.insert(spo);
                trdProductTempDealAddMapper.insert(tpda);
                trdProductTempDealMapper.insert(tpd);
                genOldPosition(dto, spo);
                //发送占用报文
                rollMapList.add(sendPositionMethod(dto, spo));
            }
        }catch (Exception e){
            //回滚
            logger.error("场内通用回购到期提前生成现金流水失败",e);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(rollMapList)) {
                rollMapList.forEach(item -> {
                    String revokeId = String.valueOf(item.get("seqno"));
                    String modelType = (String) item.get("modelType");
                    PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
                });
            }
            throw e;
        }


    }

    private List<String> sendRevokePosition(List<TrdProductTempDeal> trdProductTempDealList,List<Map<String, Object>> rollMapList) {
        List<String> tradeIdList = new ArrayList<>();
        if(StringUtil.isEmptyList(trdProductTempDealList)){
            return tradeIdList;
        }
        Map<String, Object> batchRevoke = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for(TrdProductTempDeal deal: trdProductTempDealList){
            Map<String, Object> data = new HashMap<>();
            data.put("busiType", deal.getBusiType());
            data.put("ps", deal.getPs());
            data.put("closeId", deal.getTradeId());
            data.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
            data.put("busiAct", DictCodeListExt3.业务动作_待交收.getValue());
            dataList.add(data);
            tradeIdList.add(deal.getTradeId());
        }
        batchRevoke.put("data", dataList);
        batchRevoke.put("userId", LoginUserUtil.getUserName());
        batchRevoke.put("totalTradeId", IDGenerator.generate());
        batchRevoke.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
        rollMapList.add(PositionHttpRequest.batchRevoke(batchRevoke));
        return tradeIdList;
    }

    private void deleteTradeAndOldPosition( List<String> tradeIdList) {
        if(StringUtil.isEmptyList(tradeIdList)){
            return;
        }
        Map<String,Object> deleteMap = new HashMap<>();
        deleteMap.put("tradeIdList", tradeIdList);
        List<EtrCashPosition> etrCashPositions =exCommonRepoAutoDurDealGenerateMapper.listEtrCashPositionFlow(deleteMap);
        List<PtlCashPosition> ptlCashPositions =exCommonRepoAutoDurDealGenerateMapper.listPtlCashPositionFlow(deleteMap);
        exCommonRepoAutoDurDealGenerateMapper.deleteStlPayTempOrdersData(deleteMap);
        exCommonRepoAutoDurDealGenerateMapper.deleteTrdProductTempDeal(deleteMap);
        exCommonRepoAutoDurDealGenerateMapper.deleteTrdProductTempDealAdd(deleteMap);
        exCommonRepoAutoDurDealGenerateMapper.deletePtlCashFlow(deleteMap);
        exCommonRepoAutoDurDealGenerateMapper.deleteEtrCashFlow(deleteMap);
        //更新余额
        ptlCashPositions.stream().forEach(item ->{
            if(null!=item.getAmount()){
                item.setAmount(item.getAmount().negate());
                exCommonRepoAutoDurDealGenerateMapper.updatePtlCashPosition(item);
            }
        });
        etrCashPositions.stream().forEach(item ->{
            if(null!=item.getAmount()){
                item.setAmount(item.getAmount().negate());
                exCommonRepoAutoDurDealGenerateMapper.updateEtrCashPosition(item);
            }
        });
    }

    private void genOldPosition(TrdRepoDurationDto dto,StlPayTempOrders spo){
        PtlCashPositionFlow ptlCashPositionFlow = new PtlCashPositionFlow();
        ptlCashPositionFlow.setPortfolioId(dto.getPortfolioId());
        ptlCashPositionFlow.setAcctId(spo.getMyAcctNo());
        ptlCashPositionFlow.setAmount(spo.getSettleAmt());
        ptlCashPositionFlow.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
        if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
            ptlCashPositionFlow.setAmount(ArithmeticUtil.multiply(dto.getTradeAmt(),new BigDecimal("-1")));
        }
        ptlCashPositionFlow.setCcy(dto.getCcy());
        ptlCashPositionFlow.setHodingType(DictCodeList.持仓类型_可用.getValue());
        ptlCashPositionFlow.setCdate(DateUtil.parse(dto.getTradeDate()));
        ptlCashPositionFlow.setOrderId(spo.getOrderId());
        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
        bussinessCodeParameter.setSeqNo("9999");
        String seqno = baseGetBussinessId !=null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate();
        ptlCashPositionFlow.setSeqno(seqno);
        ptlCashPositionFlow.setIsExistNew(DictCodeList.是否_是.getValue());
        PtlCashPositionFlowDto flowDto = new PtlCashPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(flowDto, ptlCashPositionFlow);
        basePtlCashPositionInfoService.insertCashPositionFlow(flowDto);
        PtlPositionHoldDto ptlHoldDto = new PtlPositionHoldDto();
        ptlHoldDto.setPtlCashPositionFlows(Arrays.asList(flowDto));
        baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlHoldDto, false);
    }

    private Map sendPositionMethod(TrdRepoDurationDto dto,StlPayTempOrders spo) {
        Map<String,Object> req = new HashMap<>();
        List<Map<String,Object>> ptlCashList = new ArrayList<>();
        req.put("interfaceType","REPO003");
        req.put("userId","system");
        req.put("totalTradeId",IDGenerator.generate());
        req.put("flowStatus",DictCodeListExt3.流程节点_审批生效.getValue());
        req.put("modelType",DictCodeListExt1.子系统代码_交易.getValue());
        req.put("balanceCheck","N");
        req.put("busiType",DictCodeListExt3.头寸业务类型_通用回购到期.getValue());
        req.put("systemAct",DictCodeListExt3.系统动作_新建.getValue());
        req.put("decisionAct",DictCodeListExt3.系统动作_新建.getValue());
        req.put("busiAct",DictCodeListExt3.业务动作_待交收.getValue());
        if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
            req.put("ps","03");
        }else{
            req.put("ps","04");
        }
        req.put("tradePlace",dto.getTradeMarket());
        req.put("cdate",dto.getSettleDate());
        req.put("ccy",dto.getCcy());
        Map<String, Object> ptlCash = new HashMap<>();
        ptlCash.put("portfolioId", dto.getPortfolioId());
        ptlCash.put("tradeId", dto.getBusiCode());
        ptlCash.put("cashAmount", dto.getTradeAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
        ptlCash.put("cashAcctId", spo.getMyAcctNo());
        ptlCashList.add(ptlCash);
        req.put("ptlCashList",ptlCashList);
        return PositionHttpRequest.send(req);
    }

    private TrdProductTempDeal genTrdProductTempDeal(TrdRepoDurationDto dto){
        TrdProductTempDeal temp = new TrdProductTempDeal();
        temp.setBusiType(DictCodeListExt3.头寸业务类型_通用回购到期.getValue());
        temp.setTradeId(dto.getBusiCode());
        temp.setFinprodId(dto.getFinprodId());
        temp.setFinprodType2(dto.getFinprodType2());
        temp.setCcy(dto.getCcy());
        temp.setPs(DictCodeList.交易方向_到期.getValue());
        temp.setTradeDate(DateUtil.parse(dto.getSettleDate()));
        temp.setSettleDate(DateUtil.parse(dto.getSettleDate()));
        temp.setMdate(DateUtil.parse(dto.getSettleDate()));
        temp.setVdate(dto.getvDate() ==null ? null : DateTime.parse(dto.getvDate()).toDate());
        temp.setShareAmt(dto.getTheoryPrin());
//        修改为交易本金 20191127 start
        temp.setPrinAmt(dto.getTheoryPrin());
//        修改为交易本金 20191127 end
        temp.setIntAmt(ArithmeticUtil.subtract(dto.getTheoryAmt(), dto.getTheoryPrin()));
        temp.setTradeAmt(dto.getTradeAmt());
        temp.setTradeMarket(dto.getTradeMarket());
        temp.setCounterId(dto.getCounterId());
        temp.setTrader(dto.getTrader());
        temp.setCounterTrader(dto.getCounterTrader());
        temp.setSecManageAcctId(dto.getSecManageAcctId());
        return temp;
    }

    private TrdProductTempDealAdd genTrdProductTempDealAdd(TrdRepoDurationDto dto){
        TrdProductTempDealAdd temp = new TrdProductTempDealAdd();
        temp.setTradeId(dto.getBusiCode());
        temp.setPortfolioId(dto.getPortfolioId());
        return temp;
    }

    private StlPayTempOrders genStlPayTempOrders(TrdProductTempDeal deal,  TrdProductTempDealAdd dealAdd) {
//        StlPayTempOrders wflstlPayOrders = new StlPayTempOrders();
//        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
//        bussinessCodeParameter.setSeqNo("9998");
//        String orderId;
//        if (baseGetBussinessId != null) {
//            orderId = baseGetBussinessId.buildBussinessCode(bussinessCodeParameter);
//        } else {
//            orderId = IDGenerator.generate();
//        }
//        wflstlPayOrders.setOrderId(orderId);
//        wflstlPayOrders.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
////        String acctNo = autoPeriodCommonService.selectDealCapacctInfo(trdProductDeal.getSecManageAcctId(), trdProductDeal.getCcy());
//        String acctId = EtrCommonCashPositionDealService.select组合托管户(trdProductDealAdd.getPortfolioId(),
//                trdProductDeal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
//        wflstlPayOrders.setMyAcctNo(acctId);
//        wflstlPayOrders.setSettleDate(trdProductDeal.getSettleDate() == null ? trdProductDeal.getTradeDate() : trdProductDeal.getSettleDate());
//        wflstlPayOrders.setPs(DictCodeListExt1.收支方向_收入.getValue());
//        wflstlPayOrders.setSettleAmt(trdProductDeal.getTradeAmt());
//        wflstlPayOrders.setCcy(trdProductDeal.getCcy());
//        wflstlPayOrders.setSettleStatus(DictCodeList.结算状态_已结算.getValue());
//        wflstlPayOrders.setIsNeedSend(DictCodeListExt1.是否_否.getValue());
//        wflstlPayOrders.setIsTotalTrade(DictCodeListExt1.是否_否.getValue());
//        wflstlPayOrders.setTradeId(trdProductDeal.getTradeId());
//        wflstlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
//        wflstlPayOrders.setPortfolioId(trdProductDealAdd.getPortfolioId());
        TrdProductDeal deal1 = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(deal1, deal);
        BussSettleOrderParameterDto bsParameter = new BussSettleOrderParameterDto();
        bsParameter.setDeal(deal1);
        bsParameter.setIsTotalTrade(DictCodeList.是否_是.getValue());
        bsParameter.setPortfolioId(dealAdd.getPortfolioId());
        BussTrdTransferOrderDto bussTrdTransferOrder = bussTrdTransferOrderImplService.generateOrder(bsParameter);
        List<BussTrdTransferOrderDto> spolist= bussTrdTransferOrderImplService.save(Arrays.asList(bussTrdTransferOrder));
        StlPayTempOrders spo = new StlPayTempOrders();
        BeanUtil.copyPropertiesIgnoreNull(spo, spolist.get(0));
        spo.setOrderId(deal.getTradeId());//orderId与交易id保持一致，方便后期删除
        return spo;
    }

}
