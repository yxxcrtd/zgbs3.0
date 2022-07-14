/*
 * 文件名：AutoPeriodCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.autoperiod.common;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.dao.AutoPeriodCommonMapper;
import com.joyintech.tams.invest.common.component.base.group.impl.dao.BasePtlSecPositionInfoMapper;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTrdDurDbInertDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurDbInertDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动存续期共通service
 *
 * @author etliu
 * @version 1.0 2018年12月13日
 * @see AutoPeriodCommonService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class AutoPeriodCommonService extends BaseService {

    /**
     * WflTrdFinProductDealTotalMapper
     */
    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    /**
     * WflStlPayOrdersMapper
     */
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    /**
     * WflPaTrdFinDealTotalMapper
     */
    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    /**
     * WflTrdProductDealMapper
     */
    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;
    
    /**
     * TrdFinProductDealTotalMapper
     */
    @Autowired
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Autowired
    private TrdDealTotalAddMapper trdDealTotalAddMapper;
    
    /**
     * StlPayOrdersMapper
     */
    @Autowired
    private StlPayOrdersMapper stlPayOrdersMapper;

    /**
     * StlPayOrdersMapper
     */
    @Autowired
    private StlPayTempOrdersMapper stlPayTempOrdersMapper;
    
    /**
     * PaTrdFinProductDealTotalMapper
     */
    @Autowired
    private PaTrdFinProductDealTotalMapper paTrdFinProductDealTotalMapper;
    
    /**
     * TrdProductDealMapper
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;
    
    /**
     * TrdProductDealAddMapper
     */
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    
    /**
     * PtlTradeMapper
     */
    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    @Autowired
    private BasePtlSecPositionInfoMapper basePtlSecPositionInfoMapper;
    /**
     * 持仓头寸
     */
    @Autowired
    private PtlSecPositionMapper ptlSecPositionMapper;

    /**
     * AutoPeriodCommonMapper
     */
    @Autowired
    private AutoPeriodCommonMapper autoPeriodCommonMapper;

    @Autowired
    private PaTrdProdTempDealTotalMapper paTrdProdTempDealTotalMapper;

    @Autowired
    private TrdProductTempDealTotalMapper trdProductTempDealTotalMapper;

    @Autowired
    private TrdProductTempDealMapper trdProductTempDealMapper;

    @Autowired
    private TrdProductTempDealAddMapper trdProductTempDealAddMapper;


    public static String change现金流类型To交易方向(String cashType) {
        String ps = null;
        if (StringUtil.isNotEmpty(cashType)) {
            if (cashType.contains(DictCodeList.现金流类型_到期还本.getValue())) {
                ps = DictCodeList.交易方向_到期.getValue();
            }
            else if (cashType.contains(DictCodeList.现金流类型_中间还本.getValue())) {
                ps = DictCodeList.交易方向_还本.getValue();
            }
            else if(cashType.contains(DictCodeList.现金流类型_中间收息.getValue())||cashType.contains(DictCodeList.现金流类型_到期收息.getValue())){
                ps = DictCodeList.交易方向_收息.getValue();
            }
            else {
                ps = DictCodeList.交易方向_提前支取.getValue();
            }
        }
        return ps;
    }

    public static Long changePsToBranch(String mDate, String ps) {
        String date = DateUtil.format(DateUtil.parse(mDate), DateUtil.DEFAULT_OTHER_FORMAT);
        if (DictCodeList.交易方向_到期.getValue().equals(ps) || DictCodeList.交易方向_还本.getValue().equals(ps)
        || DictCodeList.交易方向_到期续做.getValue().equals(ps)) {
            return Long.parseLong(date + "01");
        }
        else if (DictCodeList.交易方向_收息.getValue().equals(ps)||DictCodeList.交易方向_收息到账.getValue().equals(ps)) {
            return Long.parseLong(date + "02");
        }
        return null;
    }

    public static String change交易方向To投管类型(String ps) {
        String tgBusiType = null;
        if (StringUtil.isNotEmpty(ps)) {
            if (ps.contains(DictCodeList.交易方向_到期.getValue())) {
                tgBusiType = DictCodeListExt1.平安业务类型_债券到期.getValue();
            }
            else if (ps.contains(DictCodeList.交易方向_还本.getValue())) {
                tgBusiType = DictCodeListExt1.平安业务类型_债券中间还本.getValue();
            }
            else if(ps.contains(DictCodeList.交易方向_收息.getValue())){
                tgBusiType = DictCodeListExt1.平安业务类型_债券中间收息.getValue();
            }
            else if(ps.contains(DictCodeList.交易方向_行权_赎回.getValue())){
                tgBusiType = DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
            }
        }
        return tgBusiType;
    }

    /**
     * 生成业务编号
     *
     * @param parameter 共通业务类型
     * @return 业务编号
     * @throws Exception
     */
    public String buildBussinessCode(BussinessCodeParameter parameter) {
        BussinessCodeParameter req = new BussinessCodeParameter();
        req.setMapInfo(parameter.initMapInfo());
        // 生成的业务编号
        return restTemplate.postForObject(
            ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRule", req,
            String.class);
    }

    /**
     *
     * 根据汇总交易编号查询交易
     * <功能详细描述>
     * @param busiCode 汇总交易编号
     * @return
     * @see
     * @since 1.0
     */
    public List<TrdProductDeal> findDealByTotalId( String busiCode){
        return restTemplate.postForList(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdDepoDuration/findDealByTotalId",
            busiCode, TrdProductDeal.class);
    }

    /**
     * 登录存续期流程数据
     *
     * @param baseTrdDurDbInertDto
     * @see
     * @since 1.0
     */
    public void insertWflData(BaseTrdDurDbInertDto baseTrdDurDbInertDto) {
        if (baseTrdDurDbInertDto == null) {
            return;
        }

        if (baseTrdDurDbInertDto.getWflTrdFinProductDealTotal() != null) {
            wflTrdFinProductDealTotalMapper.insert(baseTrdDurDbInertDto.getWflTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(baseTrdDurDbInertDto.getWflStlPayOrdersList())) {
            for (WflStlPayOrders wflStlPayOrders : baseTrdDurDbInertDto.getWflStlPayOrdersList()) {
                wflStlPayOrdersMapper.insert(wflStlPayOrders);
            }
        }

        if (baseTrdDurDbInertDto.getWflPaTrdFinDealTotal() != null) {
            wflPaTrdFinDealTotalMapper.insert(baseTrdDurDbInertDto.getWflPaTrdFinDealTotal());
        }

        if (!StringUtil.isEmptyList(baseTrdDurDbInertDto.getWflTrdProductDealList())) {
            for (WflTrdProductDeal wflTrdProductDeal : baseTrdDurDbInertDto.getWflTrdProductDealList()) {
                wflTrdProductDealMapper.insert(wflTrdProductDeal);
            }
        }
    }

    /**
     * 登录交易相关有效数据
     *
     * @param baseTrdDurDbInertDto BaseTrdDurDbInertDto
     * @param finProductTradeDto FinProductTradeDto
     * @see
     * @since 1.0
     */
    public void insertDealData(BaseTrdDurDbInertDto baseTrdDurDbInertDto, FinProductTradeDto finProductTradeDto) {
        if (baseTrdDurDbInertDto == null) {
            return;
        }

        if (baseTrdDurDbInertDto.getTrdFinProductDealTotal() != null) {
            trdFinProductDealTotalMapper.insert(baseTrdDurDbInertDto.getTrdFinProductDealTotal());
            deletePledgeSecPosition(finProductTradeDto,baseTrdDurDbInertDto.getTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(baseTrdDurDbInertDto.getStlPayOrdersList())) {
            for (StlPayOrders stlPayOrders : baseTrdDurDbInertDto.getStlPayOrdersList()) {
                stlPayOrdersMapper.insert(stlPayOrders);
            }
        }

        if (baseTrdDurDbInertDto.getPaTrdFinProductDealTotal() != null) {
            paTrdFinProductDealTotalMapper.insert(baseTrdDurDbInertDto.getPaTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(baseTrdDurDbInertDto.getTrdProductDealList())) {
            for (int i = 0; i < baseTrdDurDbInertDto.getTrdProductDealList().size(); i++ ) {
                TrdProductDeal trdProductDeal = baseTrdDurDbInertDto.getTrdProductDealList().get(
                    i);
                TrdProductDealAdd trdProductDealAdd = baseTrdDurDbInertDto.getTrdProductDealAdds().get(
                    i);
                PtlTrade ptlTrade = baseTrdDurDbInertDto.getPtlTrades().get(i);
                trdProductDealMapper.insert(trdProductDeal);
                trdProductDealAddMapper.insert(trdProductDealAdd);
                ptlTradeMapper.insert(ptlTrade);
                List<PtlTrade> ptlTradeList = new ArrayList<PtlTrade>();
                ptlTradeList.add(ptlTrade);
                if (StringUtil.isNotBlank(baseTrdDurDbInertDto.getActPortfolioId())
                    && StringUtil.isNotBlank(trdProductDealAdd.getPortfolioId())
                    && !StringUtil.equals(trdProductDealAdd.getPortfolioId(),
                        baseTrdDurDbInertDto.getActPortfolioId())) {
                    PtlTrade ptlTrade2 = new PtlTrade();
                    BeanUtil.copyPropertiesIgnoreNull(ptlTrade2, ptlTrade);
                    ptlTrade2.setPortfolioId(baseTrdDurDbInertDto.getActPortfolioId());
                    ptlTradeMapper.insert(ptlTrade2);
                    ptlTradeList.add(ptlTrade2);
                }
            }
            // 回购到期 调用lendMdateAdd估值接口
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(
                baseTrdDurDbInertDto.getTrdProductDealAdds().get(0).getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDealList(baseTrdDurDbInertDto.getTrdProductDealList());
            inputDto.setRepoType(finProductTradeDto.getAssetType2());
            inputDto.setStlPayOrders(baseTrdDurDbInertDto.getStlPayOrdersList().get(0));
            AllTradeCommonUtil.lendMdateAdd(inputDto);
            if (StringUtil.isNotEmpty(baseTrdDurDbInertDto.getActPortfolioId())
                && !StringUtil.equals(
                    baseTrdDurDbInertDto.getTrdProductDealAdds().get(0).getPortfolioId(),
                    baseTrdDurDbInertDto.getActPortfolioId())) {
                baseTrdDurDbInertDto.getTrdProductDealList().forEach(item -> {
                    item.setTradeId(item.getTradeId() + 'S');
                });
                inputDto.setProdid(baseTrdDurDbInertDto.getActPortfolioId());
                AllTradeCommonUtil.lendMdateAdd(inputDto);
            }
        }

    }

    /**
     * 处理质押券持仓
     * @param trdFinProductDealTotal
     */
    public void deletePledgeSecPosition(FinProductTradeDto finProductTradeDto,TrdFinProductDealTotal trdFinProductDealTotal) {
        String updateUser = trdFinProductDealTotal.getUpdateUser();
        Date updateTime = new Date();
        // add by yujiaping 20210205 ZGBSS-489质押式回购交易—银行间正回购交易已做到期交易，质押式回购新建交易添加质押券，查看该债券信息有误start
        if(StringUtil.equals(trdFinProductDealTotal.getPs(),DictCodeList.交易方向_到期.getValue())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", trdFinProductDealTotal.getFinprodId());
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            List<PtlSecPositionFlow> flows = ptlSecPositionFlowMapper.selectSecFlow(params);
            if (flows != null && !flows.isEmpty()) {
                flows.forEach(flow -> {
                    if (trdFinProductDealTotal.getVdate().equals(DateUtil.format(flow.getCdate()))){
                        flow.setAmount(flow.getAmount());
                        flow.setCdate(trdFinProductDealTotal.getSettleDate());
                        ptlSecPositionFlowMapper.insertSelect04Sec(flow);
                        params.put("seqNo", IDGenerator.generate());
                        params.put("mDate", trdFinProductDealTotal.getSettleDate());
                        params.put("vDate", trdFinProductDealTotal.getVdate());
                        params.put("finprodId", flow.getFinprodId());
                        params.put("tradeId", trdFinProductDealTotal.getFinprodId());
                        //生成持仓流水数据，如果没有生成流水，重复跑当天的批处理会导致持仓数据错误
                        ptlSecPositionFlowMapper.insertSelect04SecFlowByDate(params);
                    }
                });
            }
        }
        //add by yujiaping 20210205 ZGBSS-489质押式回购交易—银行间正回购交易已做到期交易，质押式回购新建交易添加质押券，查看该债券信息有误end
        if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdFinProductDealTotal.getFinprodType2())
                && DictCodeList.回购类型_质押式协议回购.getValue().equals(finProductTradeDto.getAssetType2())) {
            Map<String, Object> params = new HashMap<>();
            params.put("finprodId", trdFinProductDealTotal.getFinprodId());
            params.put("holdingType", DictCodeList.持仓类型_质押.getValue());
            if(StringUtil.equals(trdFinProductDealTotal.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())){//质押券提前终止是终止日，其他是资金到账日
                params.put("mDate", trdFinProductDealTotal.getMdate());
            }
            else{
                params.put("mDate", trdFinProductDealTotal.getSettleDate());
            }

            /**质押式正回购释放判断***chenlei1***add*start*/
            Map<String, Object> statusMap = ptlSecPositionFlowMapper.selectTradeReleaseStatus(trdFinProductDealTotal.getFinprodId());
            String releaseStatus = statusMap == null ? null : (String)statusMap.get("RELEASE_STATUS");
            String isManualRelease = statusMap == null ? null : (String)statusMap.get("IS_MANUAL_RELEASE");
            if(!DictCodeList.释放状态_已释放.getValue().equals(releaseStatus)){
                /**质押式正回购释放判断***chenlei1***add*end*/
                List<PtlSecPositionFlow> flows = ptlSecPositionFlowMapper.selectSecFlow(params);
                if (flows != null && !flows.isEmpty()) {
                    flows.forEach(flow -> {
                        if(StringUtil.equals(trdFinProductDealTotal.getPs(),DictCodeListExt3.交易方向_投资端_提前终止.getValue())){
                            flow.setCdate(trdFinProductDealTotal.getMdate());
                        }
                        else{
                            flow.setCdate(trdFinProductDealTotal.getSettleDate());
                        }
                        flow.setCreateTime(updateTime);
                        flow.setCreateUser(updateUser);
                        flow.setUpdateTime(updateTime);
                        //                   trdRepoDurationMapper.insertSelect04Sec(flow);
                        flow.setSeqno(IDGenerator.generate());
                        flow.setTradeId(trdFinProductDealTotal.getFinprodId());
                        ptlSecPositionFlowMapper.insert(flow);
                        PtlSecPositionFlowDto secPositionFlowDto =new PtlSecPositionFlowDto();//汇总头寸表
                        PtlSecPosition secPosition= new PtlSecPosition();
                        BeanUtil.copyPropertiesIgnoreNull(secPositionFlowDto,flow);
                        BeanUtil.copyPropertiesIgnoreNull(secPosition,flow);
                        BigDecimal amount = basePtlSecPositionInfoMapper.selectSecPosition(secPositionFlowDto);
                        if(amount==null) {//当天没余额
                            // 如果当天的持仓数据没有，取当天之前最近一天的数据
                            // 查询最近的那一天
                            Date maxCdate = basePtlSecPositionInfoMapper.selectSecPositionMaxCdate(secPositionFlowDto);
                            if (maxCdate == null) {
                                ptlSecPositionMapper.insertSelective(secPosition);
                            } else {
                                PtlSecPositionFlowDto tempPositionFlowDto = new PtlSecPositionFlowDto();
                                BeanUtils.copyProperties(secPositionFlowDto, tempPositionFlowDto);
                                tempPositionFlowDto.setCdate(maxCdate);
                                BigDecimal tempAmount = basePtlSecPositionInfoMapper.selectSecPosition(tempPositionFlowDto);
                                secPosition.setAmount(ArithmeticUtil.doAdd(secPosition.getAmount(), tempAmount));
                                ptlSecPositionMapper.insertSelective(secPosition);
                            }
                        }
                        else{//当天有余额
                            ptlSecPositionFlowMapper.insertSelect04Sec(flow);
                        }

                    });
                }
            }
        }
    }

    /**
     * 删除流程数据
     *
     * @param busiType 业务类型
     * @param finProdType2 金融产品类型（投管）
     * @see
     * @since 1.0
     */
    public void deleteWflData(String busiType, String finProdType2) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("busiType", busiType);
        paramMap.put("finProdType2", finProdType2);

        autoPeriodCommonMapper.deleteWflStlPayOrdersData(paramMap);
        autoPeriodCommonMapper.deleteWflPaTrdFinDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteWflTrdFinProductDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteWflTrdProductDeal(paramMap);
    }
    /**
     * 根据组合信息获取子组合利息明息
     * @param map 参数
     * @return 组装结果
     */
    public List<FinAssetIntInfo> distributeDataByPercent(Map<String, Object> map) {
        return restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanCurrDuration/distributeDataByPercent", map,
                FinAssetIntInfo.class);
    }
    /**
     * 一个交易明细一个划款指令
     *
     * @param stlPayOrdersList 划款指令list
     * @param total 汇总交易entity
     * @param workflowId 工作流编号
     * @return 生成结果
     */
    public StlPayOrders gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdFinProductDealTotal total,
                                   String workflowId) {

        StlPayOrders stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if(StringUtil.equals(dto.getInvAim(),"999999")&&StringUtil.equals(total.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){ // 生成划款指令，无投资目的时设置成卖空，因为汇总表total就是这么处理的
                dto.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
            }
            if (((dto.getInvAim() != null && dto.getInvAim().equals(total.getInvAim())) || (StringUtil.isEmpty(dto.getInvAim()) && StringUtil.isEmpty(total.getInvAim())))
                    && dto.getPortfolioId().equals(total.getPortfolioId())) {
                stlPayOrders = dto;
                if(StringUtil.isNotEmpty(stlPayOrders.getSettleAmt())){
                    stlPayOrders.setSettleAmt(StringUtil.isNotEmpty(total.getFpriceAmt())?total.getFpriceAmt():total.getTradeAmt());
                }
                stlPayOrders.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                stlPayOrders.setTradeId(total.getTotleTradeId());
                stlPayOrders.setWorkflowId(workflowId);
            }
        }
        return stlPayOrders;
    }
    public List<BussTrdTransferOrderDto> save(List<BussTrdTransferOrderDto> dto) {
        return dto.stream().map(item -> {
            BussTrdTransferOrderDto orders = new BussTrdTransferOrderDto();
            BeanUtil.copyPropertiesIgnoreNull(orders, item);
            if (StringUtil.equals(item.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                orders.setMyAcctNo(item.getPayAcctNo());
                orders.setCounterAcctNo(item.getRecAcctNo());
            }
            else {
                orders.setMyAcctNo(item.getRecAcctNo());
                orders.setCounterAcctNo(item.getPayAcctNo());
            }
            if (StringUtil.isNotEmpty(orders.getIsNeedSend())) {
                if (DictCodeList.是否_是.getCodeName().equals(orders.getIsNeedSend())) {
                    orders.setIsNeedSend(DictCodeList.是否_是.getValue());
                }
                else if (DictCodeList.是否_否.getCodeName().equals(orders.getIsNeedSend())) {
                    orders.setIsNeedSend(DictCodeList.是否_否.getValue());
                }
            }
            return orders;
        }).collect(Collectors.toList());
    }

    /**
     * 查询交易本金（通过估值）
     * @param map 参数
     * @return
     */
    public BigDecimal genPrinAmtByVal(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoBuyoutDuration/genPrinAmtByVal", map,
                BigDecimal.class);
    }

    public List<PtlSecPosition> findAllProvided(Map<String, Object> params) {
        return autoPeriodCommonMapper.findAllProvided(params);
    }

    /**
     * 登录存续期流程数据
     *
     * @param trdBondDurDbInertDto
     * @see
     * @since 1.0
     */
    public void insertCfetsDealData(TrdBondDurDbInertDto trdBondDurDbInertDto) {
        if (trdBondDurDbInertDto == null) {
            return;
        }

        if (trdBondDurDbInertDto.getTrdFinProductDealTotal() != null) {
            trdFinProductDealTotalMapper.insert(trdBondDurDbInertDto.getTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getStlPayOrdersList())) {
            for (StlPayOrders StlPayOrders : trdBondDurDbInertDto.getStlPayOrdersList()) {
                stlPayOrdersMapper.insert(StlPayOrders);
            }
        }

        if (trdBondDurDbInertDto.getPaTrdFinProductDealTotal() != null) {
            paTrdFinProductDealTotalMapper.insert(trdBondDurDbInertDto.getPaTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealList())) {
            for (TrdProductDeal trdProductDeal : trdBondDurDbInertDto.getTrdProductDealList()) {
                trdProductDealMapper.insert(trdProductDeal);
            }
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealAdds())) {
            for (TrdProductDealAdd trdProductDealAdd : trdBondDurDbInertDto.getTrdProductDealAdds()) {
                trdProductDealAddMapper.insert(trdProductDealAdd);
            }
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getPtlTrades())) {
            for (PtlTrade ptlTrade : trdBondDurDbInertDto.getPtlTrades()) {
                ptlTradeMapper.insert(ptlTrade);
            }
        }
    }


    /**
     * 登录存续期流程临时表数据
     *
     * @param trdBondDurDbInertDto
     * @see
     * @since 1.0
     */
    public void insertCfetsTempDealData(TrdBondDurDbInertDto trdBondDurDbInertDto) {
        if (trdBondDurDbInertDto == null) {
            return;
        }
        if (trdBondDurDbInertDto.getTrdFinProductDealTotal() != null) {
            trdProductTempDealTotalMapper.insert(trdBondDurDbInertDto.getTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getStlPayOrdersList())) {
//            for (StlPayOrders StlPayOrders : trdBondDurDbInertDto.getStlPayOrdersList()) {
//                stlPayOrdersMapper.insert(StlPayOrders);
//            }
            List<StlPayTempOrders> stlPayTempOrders = new ArrayList<>();
            trdBondDurDbInertDto.getStlPayOrdersList().forEach(
                    stlPayOrders -> {
                        StlPayTempOrders tempOrders = new StlPayTempOrders();
                        BeanUtil.copyPropertiesIgnoreNull(tempOrders,stlPayOrders);
                        stlPayTempOrders.add(tempOrders);
                    }
            );
            stlPayTempOrdersMapper.insertBatch(stlPayTempOrders);
        }

        if (trdBondDurDbInertDto.getPaTrdFinProductDealTotal() != null) {
            paTrdProdTempDealTotalMapper.insert(trdBondDurDbInertDto.getPaTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealList())) {
            for (TrdProductDeal trdProductDeal : trdBondDurDbInertDto.getTrdProductDealList()) {
                TrdProductTempDeal tempDeal = new TrdProductTempDeal();
                BeanUtil.copyPropertiesIgnoreNull(tempDeal,trdProductDeal);
                trdProductTempDealMapper.insert(tempDeal);
            }
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealAdds())) {
            for (TrdProductDealAdd trdProductDealAdd : trdBondDurDbInertDto.getTrdProductDealAdds()) {
                TrdProductTempDealAdd dealAdd = new TrdProductTempDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(dealAdd,trdProductDealAdd);
                trdProductTempDealAddMapper.insert(dealAdd);
            }
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getPtlTrades())) {
            for (PtlTrade ptlTrade : trdBondDurDbInertDto.getPtlTrades()) {
                ptlTradeMapper.insert(ptlTrade);
            }
        }
    }


    /**
     * 登录存续期流程数据
     *
     * @param trdBondDurDbInertDto
     * @see
     * @since 1.0
     */
    public void insertCfetsShDealData(TrdBondDurDbInertDto trdBondDurDbInertDto) {
        if (trdBondDurDbInertDto == null) {
            return;
        }

        if (trdBondDurDbInertDto.getTrdFinProductDealTotal() != null) {
            trdFinProductDealTotalMapper.insert(trdBondDurDbInertDto.getTrdFinProductDealTotal());
        }
        if (trdBondDurDbInertDto.getTrdDealTotalAdd() != null) {
            trdDealTotalAddMapper.insert(trdBondDurDbInertDto.getTrdDealTotalAdd());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getStlPayOrdersList())) {
            for (StlPayOrders StlPayOrders : trdBondDurDbInertDto.getStlPayOrdersList()) {
                stlPayOrdersMapper.insert(StlPayOrders);
            }
        }

        if (trdBondDurDbInertDto.getPaTrdFinProductDealTotal() != null) {
            paTrdFinProductDealTotalMapper.insert(trdBondDurDbInertDto.getPaTrdFinProductDealTotal());
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealList())) {
            for (TrdProductDeal trdProductDeal : trdBondDurDbInertDto.getTrdProductDealList()) {
                trdProductDealMapper.insert(trdProductDeal);
            }
        }

        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealAdds())) {
            for (TrdProductDealAdd trdProductDealAdd : trdBondDurDbInertDto.getTrdProductDealAdds()) {
                trdProductDealAddMapper.insert(trdProductDealAdd);
            }
        }
        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getPtlTrades())) {
            for (PtlTrade ptlTrade : trdBondDurDbInertDto.getPtlTrades()) {
                ptlTradeMapper.insert(ptlTrade);
            }
        }


    }

    /**
     * 登录存续期临时表流程数据
     *
     * @param trdBondDurDbInertDto
     * @see
     * @since 1.0
     */
    public void insertCfetsShTempDealData(TrdBondDurDbInertDto trdBondDurDbInertDto) {
        if (trdBondDurDbInertDto == null) {
            return;
        }
        if (trdBondDurDbInertDto.getTrdFinProductDealTotal() != null) {
            trdProductTempDealTotalMapper.insert(trdBondDurDbInertDto.getTrdFinProductDealTotal());
        }
        if (trdBondDurDbInertDto.getTrdDealTotalAdd() != null) {
           trdDealTotalAddMapper.insert(trdBondDurDbInertDto.getTrdDealTotalAdd());
        }
        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getStlPayOrdersList())) {
//            for (StlPayOrders StlPayOrders : trdBondDurDbInertDto.getStlPayOrdersList()) {
//                stlPayOrdersMapper.insert(StlPayOrders);
//            }
            List<StlPayTempOrders> stlPayTempOrders = new ArrayList<>();
            trdBondDurDbInertDto.getStlPayOrdersList().forEach(
                    stlPayOrders -> {
                        StlPayTempOrders tempOrders = new StlPayTempOrders();
                        BeanUtil.copyPropertiesIgnoreNull(tempOrders,stlPayOrders);
                        stlPayTempOrders.add(tempOrders);
                    }
            );
            stlPayTempOrdersMapper.insertBatch(stlPayTempOrders);
        }
        if (trdBondDurDbInertDto.getPaTrdFinProductDealTotal() != null) {
            paTrdProdTempDealTotalMapper.insert(trdBondDurDbInertDto.getPaTrdFinProductDealTotal());
        }
        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealList())) {
            for (TrdProductDeal trdProductDeal : trdBondDurDbInertDto.getTrdProductDealList()) {
                TrdProductTempDeal tempDeal = new TrdProductTempDeal();
                BeanUtil.copyPropertiesIgnoreNull(tempDeal,trdProductDeal);
                trdProductTempDealMapper.insert(tempDeal);
            }
        }
        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getTrdProductDealAdds())) {
            for (TrdProductDealAdd trdProductDealAdd : trdBondDurDbInertDto.getTrdProductDealAdds()) {
                TrdProductTempDealAdd dealAdd = new TrdProductTempDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(dealAdd,trdProductDealAdd);
                trdProductTempDealAddMapper.insert(dealAdd);
            }
        }
        if (!StringUtil.isEmptyList(trdBondDurDbInertDto.getPtlTrades())) {
            for (PtlTrade ptlTrade : trdBondDurDbInertDto.getPtlTrades()) {
                ptlTradeMapper.insert(ptlTrade);
            }
        }
    }

    /**
     * 将临时交易表的数据插入正式交易表中
     * @param totalTradeId
     */
    public void insertDealDataFromTemp(String totalTradeId) {
        trdProductTempDealTotalMapper.insertToDealTotal(totalTradeId);
        paTrdProdTempDealTotalMapper.insertToPaDealTotal(totalTradeId);
        trdProductTempDealMapper.insertToDeal(totalTradeId);
        trdProductTempDealAddMapper.insertToDealAdd(totalTradeId);
    }

    /**
     * 更新交易表状态为撤销状态
     * @param totalTradeId
     * @param 'Y or N'
     */
    public void updateDealDataCancel(String totalTradeId,String cancel) {
        TrdFinProductDealTotal total = new TrdFinProductDealTotal();
        TrdProductDeal deal = new TrdProductDeal();
        total.setTotleTradeId(totalTradeId);
        total.setIsCancel(cancel);
        if(cancel.equals("Y")) {
            total.setTradeStatus("03");
            deal.setTradeStatus("03");
        }else {
            total.setTradeStatus("01");
            deal.setTradeStatus("01");
        }
        total.setUpdateTime(new Date());
        total.setUpdateUser("system");
        trdFinProductDealTotalMapper.updateByPrimaryKeySelective(total);
        deal.setTotleTradeId(totalTradeId);
        deal.setIsCancel(cancel);
        deal.setUpdateTime(new Date());
        deal.setUpdateUser("system");
        trdProductDealMapper.updateByTotleTradeId(deal);
    }

    /**
     * 删除实际表数据
     *
     * @param busiType 业务类型
     * @param finProdType2 金融产品类型（投管）
     * @see
     * @since 1.0
     */
    public void deleteActData(String busiType, String finProdType2,Date batchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("busiType", busiType);
        paramMap.put("finProdType2", finProdType2);
        paramMap.put("updateUser", "system");
        paramMap.put("batchDate", batchDate);

        autoPeriodCommonMapper.deletePaTrdFinDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteTrdFinProductDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteTrdProductDeal(paramMap);
        autoPeriodCommonMapper.deleteTrdProductDealAdd(paramMap);
        if(StringUtil.equals(finProdType2,DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            autoPeriodCommonMapper.deleteStlPayOrdersData(paramMap);
        }
    }

    /**
     * 删除Temp表数据
     *
     * @param busiType 业务类型
     * @param finProdType2 金融产品类型（投管）
     * @see
     * @since 1.0
     */
    public void deleteActTempData(String busiType, String finProdType2,Date batchDate,String tradeMarket) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("busiType", busiType);
        paramMap.put("finProdType2", finProdType2);
        paramMap.put("updateUser", "system");
        paramMap.put("batchDate", batchDate);
        paramMap.put("tradeMarket",tradeMarket);

        autoPeriodCommonMapper.deletePaTrdFinTempDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteTrdFinProductTempDealTotalData(paramMap);
        autoPeriodCommonMapper.deleteTrdProductTempDeal(paramMap);
        autoPeriodCommonMapper.deleteTrdProductTempDealAdd(paramMap);
        if(StringUtil.equals(finProdType2,DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            autoPeriodCommonMapper.deleteStlPayOrdersData(paramMap);
        }
    }

    /**
     * 检索交易关联资金账户共通
     * @param secManageAcctId 证券托管户ID
     * @param ccy 币种
     * @return 交易关联资金账户
     */
    public String selectDealCapacctInfo(String secManageAcctId, String ccy) {
        return autoPeriodCommonMapper.selectDealCapacctInfo(secManageAcctId,ccy);
    }
}
