package com.joyintech.service.trade.entrustmsgbymq.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.entrustmsgbymq.dto.TrdMoreConvrate;
import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.trade.dto.PortfolioCalculateDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeList.是否_否;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

/**
 * 成交回报头寸service
 */
@Service
public class PositionRequestTradeReportService {
    private static final Logger logger = LoggerFactory.getLogger(PositionRequestTradeReportService.class);

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    public static final String STANDAR_SEC_ID = "XN_PRODID999";

    public static final String TRADE_PLAT = "TRD_TRADE_REPORT";

    /**
     * 发送成交回报头寸请求
     * @param trdTempDto
     */
    public void sendTradeReportPositionRequest(TrdTempDto trdTempDto) {
        logger.debug("【发送成交回报头寸请求】trdTempDto: {}", JSON.toJSONString(trdTempDto));
        Map<String, Object> resultMap = new HashMap<>();
        // 设置公共字段
        this.setCommonField(trdTempDto, resultMap);
        String finprodType = trdTempDto.getFinprodType2();
        String ps = trdTempDto.getPs();
        String type6 = commonPositionEngineService.getType6(trdTempDto.getFinprodId());
        if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)
                && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)) {
            finprodType += "-" + type6;
        }
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdTempDto.getEntrustId());
        if(etrEntrustAdd != null){
            if (DictCodeList.回购类型_质押式协议回购.getValue().equals(etrEntrustAdd.getAssetType1())
                    || (DictCodeList.回购类型_质押式竞价回购.getValue().equals(etrEntrustAdd.getAssetType1()))) {
                finprodType += "-" + etrEntrustAdd.getAssetType1();
            }
        }
        logger.debug("【成交回报查询头寸接口参数】finprodType:{},ps:{}", finprodType, ps);
        String interType = commonPositionEngineService.getPositionInterType(POSITION_TYPE_TRD, TRADE_PLAT, finprodType, ps, DictCodeListExt3.业务动作_成交回报.getValue(), trdTempDto.getClass().getName());
        logger.debug("【成交回报查询头寸接口结果】interType:{}", interType);
        if (INTERF_TYPE_BOND002.equals(interType)) {
            this.bond002(trdTempDto, resultMap);
        } else if (INTERF_TYPE_BOND003.equals(interType)) {
            this.bond003(trdTempDto, resultMap);
        } else if (INTERF_TYPE_REPO003.equals(interType)) {
            this.repo003(trdTempDto, resultMap);
        } else if (INTERF_TYPE_REPO002.equals(interType)) {
            this.repo002(trdTempDto, resultMap);
        } else if (INTERF_TYPE_FUND002.equals(interType)) {
            this.fund002(trdTempDto, resultMap);
        } else if (INTERF_TYPE_STOCK001.equals(interType)) {
            this.stock001(trdTempDto, resultMap);
        }
        if(StringUtil.isNotEmpty(resultMap.get(INTERFACE_TYPE))){
            PositionHttpRequest.send(resultMap);
        }else{
            logger.debug("[成交回报未找到对应的接口配置]");
        }
    }

    /**
     * 场内现券-指令
     *
     * @param entity
     * @param resultMap
     */
    private void bond002(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_BOND002);
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, INTERF_TYPE_BOND002));
        resultMap.put("ps", entity.getPs());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put("settleSpeed", parseSettleSpreed(deliveryType));
        resultMap.put("bidResult", 是否_否.getValue());
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getTradeDate());
        resultMap.put("ccy", entity.getCcy());
        resultMap.put("finprodId", entity.getFinprodId());
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);

        List<Map<String, Object>> ptlList = new ArrayList<>();
        for (PortfolioCalculateDto dto:list) {
            Map<String, Object> pltMap = new HashMap<>();
            pltMap.put("portfolioId", dto.getPortfolioId());
            pltMap.put("tradeId", entity.getTradeId());
            pltMap.put("etrId", entity.getEntrustId());
            pltMap.put("ordEntrustId", null);
            pltMap.put("etrSecAmount", dto.getEtrSecAmount());
            pltMap.put("ordId", dto.getOrderId());
            pltMap.put("ordSecAmount", dto.getOrdSecAmount());
            pltMap.put("cashAcctId", dto.getCashAcctId());
            // ZGTFCP-1126:证券管理账户赋值 modify by fuyongsheng 20220316 start
            // pltMap.put("secAcctId", entity.getSecAcctId());
            pltMap.put("secAcctId", dto.getSecAcctId());
            // ZGTFCP-1126:证券管理账户赋值 modify by fuyongsheng 20220316 end
            pltMap.put("secAmount", dto.getSecAmount());
            pltMap.put("cashAmount", dto.getCashAmount());
            pltMap.put("invAim", entity.getInvAim());
            pltMap.put("integrateSecAmount", dto.getIntegrateSecAmount());
            pltMap.put("ownSecAmount", dto.getOwnSecAmount());
            ptlList.add(pltMap);
        }
        resultMap.put("ptlList", ptlList);
    }

    /**
     * 可转债-指令
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_BOND003);
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, INTERF_TYPE_BOND003));
        resultMap.put("ps", entity.getPs());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put("settleSpeed", parseSettleSpreed(deliveryType));
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getSettleDate());
        resultMap.put("ccy", entity.getCcy());
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        List<Map<String, Object>> ptlCashList = new ArrayList<>(4);
        List<Map<String, Object>> bondSecList = new ArrayList<>(4);
        for (PortfolioCalculateDto dto : list){
            Map<String, Object> ptlCash = new HashMap<>(8);
            ptlCash.put("portfolioId", dto.getPortfolioId());
            ptlCash.put("cashAcctId", dto.getCashAcctId());
            ptlCash.put("tradeId", entity.getTradeId());
            ptlCash.put("etrId", entity.getEntrustId());
            ptlCash.put("cashAmount", dto.getCashAmount());
            ptlCash.put("etrSecAmount", dto.getEtrSecAmount());
            ptlCash.put("ordId", dto.getOrderId());
            ptlCash.put("ordSecAmount", dto.getOrdSecAmount());
            // ZGTFCP-947:增加secAmount,ownSecAmount赋值 modify by fuyongsheng 20220317 start
            ptlCash.put("secAmount", dto.getSecAmount());
            ptlCash.put("ownSecAmount", dto.getOwnSecAmount());
            // ZGTFCP-947:增加secAmount,ownSecAmount赋值 modify by fuyongsheng 20220317 end
            ptlCashList.add(ptlCash);

            Map<String, Object> bondSec = new HashMap<>();
            bondSec.put("portfolioId", dto.getPortfolioId());
            bondSec.put("finprodId", entity.getFinprodId());
            bondSec.put("secAcctId", dto.getSecAcctId());
            bondSec.put("tradeId", entity.getTradeId());
            bondSec.put("etrId", entity.getEntrustId());
            bondSec.put("integrateSecAmount", dto.getIntegrateSecAmount());
            bondSec.put("ownSecAmount", dto.getOwnSecAmount());
            bondSec.put("secAmount", dto.getSecAmount());
            bondSecList.add(bondSec);
        }
        resultMap.put("ptlCashList", ptlCashList);
        resultMap.put("bondSecList", bondSecList);
    }

    /**
     * 质押式通用回购-指令
     *
     * @param entity
     * @param resultMap
     */
    private void repo003(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_REPO003);
        resultMap.put("ps", entity.getPs());
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("settleSpeed", null);
        resultMap.put("sharesTradePlatform", null);
        resultMap.put("deliveryType", null);
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getTradeDate());
        resultMap.put("ccy", entity.getCcy());
        // resultMap.put("totalTradeId", null);
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        paraMap.put("interfaceType", INTERF_TYPE_REPO003);
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        List<Map<String, Object>> ptlList = new ArrayList<>(4);
        List<Map<String, Object>> ptlSecList = new ArrayList<>(4);
        for (PortfolioCalculateDto dto : list){
            Map<String, Object> plt = new HashMap<>(8);
            plt.put("portfolioId", dto.getPortfolioId());
            plt.put("cashAcctId", dto.getCashAcctId());
            plt.put("secAcctId", dto.getSecAcctId());
            plt.put("tradeId", entity.getTradeId());
            plt.put("etrId", entity.getEntrustId());
            // ZGTFCP-1250:新增字段赋值 modify by fuyongsheng 20220318 start
            plt.put("etrSecAmount", dto.getEtrSecAmount());
            plt.put("ordId", dto.getOrderId());
            plt.put("ordSecAmount", dto.getOrdSecAmount());
            // ZGTFCP-1250:新增字段赋值 modify by fuyongsheng 20220318 end
            // ZGTFCP-1303:需要增加finprodId字段 modify by fuyongsheng 20220318 start
            plt.put("finprodId", entity.getFinprodId());
            // ZGTFCP-1303:需要增加finprodId字段 modify by fuyongsheng 20220318 end
            plt.put("ordEntrustId", null);
            plt.put("standardSecId", STANDAR_SEC_ID);
            plt.put("standardSecAmount", dto.getStandardSecAmount());
            if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_质押券出库.getValue().equals(entity.getPs())){
                plt.put("cashAmount", null);
            }else{
                plt.put("cashAmount", dto.getCashAmount());
            }
            TrdMoreConvrate moreConvrate = this.getMoreConvrate(dto.getCashAcctId());
            if (Objects.nonNull(moreConvrate)) {
                plt.put("reConversionRate", moreConvrate.getMoreConvrate());
            }
            ptlList.add(plt);

            if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_质押券出库.getValue().equals(entity.getPs())){
                Map<String, Object> ptlSec = new HashMap<>();
                ptlSec.put("portfolioId", dto.getPortfolioId());
                ptlSec.put("finprodId", entity.getFinprodId());
                ptlSec.put("secAcctId", dto.getSecAcctId());
                ptlSec.put("tradeId", entity.getTradeId());
                ptlSec.put("etrId", entity.getEntrustId());
                ptlSec.put("integrateSecAmount", dto.getIntegrateSecAmount());
                ptlSec.put("ownSecAmount", dto.getOwnSecAmount());
                ptlSec.put("secAmount", dto.getSecAmount());
                ptlSecList.add(ptlSec);
            }
        }
        resultMap.put("ptlList", ptlList);
        resultMap.put("ptlSecList", ptlSecList);
    }

    // ZGTFCP-1041:新增成交回报协议回购 add by fuyongsheng 20220317 start
    /**
     * 质押式协议回购
     *
     * @param entity
     * @param resultMap
     */
    private void repo002(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_REPO002);
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId());
        // ZGTFCP-1644:协议回购新头寸交易方向通过资产类型判断 modify by fuyongsheng 20220323 start
        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(etrEntrust.getFinprodType2())){
            resultMap.put("ps", DictCodeListExt3.交易方向_正回购.getValue());
        }else{
            resultMap.put("ps", DictCodeListExt3.交易方向_逆回购.getValue());
        }
        // ZGTFCP-1644:协议回购新头寸交易方向通过资产类型判断 modify by fuyongsheng 20220323 end
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("settleSpeed", null);
        resultMap.put("sharesTradePlatform", null);
        resultMap.put("deliveryType", null);
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getTradeDate());
        resultMap.put("ccy", entity.getCcy());
        // resultMap.put("totalTradeId", null);
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        // ZGTFCP-1246:协议回购需要传前次交易编号(下单的委托编号) modify by fuyongsheng 20220318 start
        // 前次交易编号
        if(!StringUtil.isEmptyList(list)){
            resultMap.put("bTotalTradeId", list.get(0).getOrderId());
        }
        // ZGTFCP-1246:协议回购需要传前次交易编号(下单的委托编号) modify by fuyongsheng 20220318 end
        // 首期组合现金数据集
        List<Map<String, Object>> ptlCashList = new ArrayList<>();
        // 轧差组合现金数据集
        List<Map<String, Object>> ptlOffsetCashList = new ArrayList<>();
        // 质押券数据集
        List<Map<String, Object>> ptlSecList = new ArrayList<>();
        // 解除质押券数据集
        List<Map<String, Object>> ptlRelieveSecList = new ArrayList<>();
        for (PortfolioCalculateDto dto : list){
            if(DictCodeListExt3.交易方向_正回购.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_逆回购.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_提前购回.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_到期购回.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())
            ){
                Map<String, Object> ptlCash = new HashMap<>();
                ptlCash.put("portfolioId", dto.getPortfolioId());
                if(DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())){
                    ptlCash.put("cashAcctId", this.getPtlCapAcctNo(etrEntrust));
                    ptlCash.put("cashAmount", entity.getPositionBaseData().getFinTradeProductDto().getmTradeAmt());
                }
                // ZGTFCP-2114:到期购回、提前购回登录当前交易的到期结算金额 modify by fuyongsheng 20220327 start
                else if(DictCodeListExt3.交易方向_到期购回.getValue().equals(entity.getPs()) || DictCodeListExt3.交易方向_提前购回.getValue().equals(entity.getPs())){
                    ptlCash.put("cashAcctId", dto.getCashAcctId());
                    ptlCash.put("cashAmount", entity.getPositionBaseData().getEtrEntrustAddDto().getmTradeAmt());
                }
                // ZGTFCP-2114:到期购回、提前购回登录当前交易的到期结算金额 modify by fuyongsheng 20220327 end
                else{
                    ptlCash.put("cashAcctId", dto.getCashAcctId());
                    ptlCash.put("cashAmount", dto.getCashAmount());
                }
                ptlCash.put("tradeId", entity.getTradeId());
                ptlCash.put("etrId", entity.getEntrustId());
                ptlCashList.add(ptlCash);
            }
            if(DictCodeListExt3.交易方向_到期续做.getValue().equals(entity.getPs())){
                Map<String, Object> ptlOffsetCash = new HashMap<>();
                ptlOffsetCash.put("portfolioId", dto.getPortfolioId());
                ptlOffsetCash.put("cashAcctId", dto.getCashAcctId());
                ptlOffsetCash.put("tradeId", entity.getTradeId());
                ptlOffsetCash.put("etrId", entity.getEntrustId());
                // ZGTFCP-2022:赋值交易金额 modify by fuyongsheng 20220325 start
                // 到期的本金-首期的到期结算金额
                ptlOffsetCash.put("cashAmount", ArithmeticUtil.subtract(entity.getPrinAmt(),
                        entity.getPositionBaseData().getFinTradeProductDto().getmTradeAmt()));
                // ZGTFCP-2022:赋值交易金额 modify by fuyongsheng 20220325 end
                ptlOffsetCash.put("ownSecAmount", dto.getOwnSecAmount());
                ptlOffsetCash.put("secAmount", dto.getSecAmount());
                ptlOffsetCashList.add(ptlOffsetCash);
            }
            if(DictCodeListExt3.交易方向_正回购.getValue().equals(entity.getPs())
            || DictCodeListExt3.交易方向_中途换券.getValue().equals(entity.getPs())
            || DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())){
                Map<String, Object> ptlSec = new HashMap<>();
                ptlSec.put("portfolioId", dto.getPortfolioId());
                ptlSec.put("secAcctId", dto.getSecAcctId());
                ptlSec.put("tradeId", entity.getTradeId());
                ptlSec.put("etrId", entity.getEntrustId());
                // 融入券券面总额
                ptlSec.put("integrateSecAmount", dto.getIntegrateSecAmount());
                if(DictCodeListExt3.交易方向_中途换券.getValue().equals(entity.getPs())){
                    FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(entity, DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                    if(null != temp){
                        ptlSec.put("finprodId", temp.getFinprodId());
                        // 本方券券面总额
                        ptlSec.put("ownSecAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                        // 券面总额
                        ptlSec.put("secAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                    }
                }
                else if(DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())){
                    // ZGTFCP-1900:修改解除质押字段赋值 modify by fuyongsheng 20220327 start
                    FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(entity, DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    if(null != temp){
                        ptlSec.put("finprodId", temp.getFinprodId());
                        // 本方券券面总额
                        ptlSec.put("ownSecAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                        // 券面总额
                        ptlSec.put("secAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                    }
                    // ZGTFCP-1900:修改解除质押字段赋值 modify by fuyongsheng 20220327 end
                }else{
                    ptlSec.put("finprodId", entity.getFinDealPledge().getPledgeFinprodId());
                    // 本方券券面总额
                    ptlSec.put("ownSecAmount", ArithmeticUtil.multiply(entity.getFinDealPledge().getPledgeNumber(), new BigDecimal(100)));
                    // 券面总额
                    ptlSec.put("secAmount", ArithmeticUtil.multiply(entity.getFinDealPledge().getPledgeNumber(), new BigDecimal(100)));
                }
                ptlSecList.add(ptlSec);
            }
            if(DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_到期续做.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_到期购回.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_提前购回.getValue().equals(entity.getPs())
                    || DictCodeListExt3.交易方向_中途换券.getValue().equals(entity.getPs())){
                Map<String, Object> ptlRelieveSec = new HashMap<>();
                ptlRelieveSec.put("portfolioId", dto.getPortfolioId());
                ptlRelieveSec.put("secAcctId", dto.getSecAcctId());
                ptlRelieveSec.put("tradeId", entity.getTradeId());
                ptlRelieveSec.put("etrId", entity.getEntrustId());
                // 融入券券面总额
                ptlRelieveSec.put("integrateSecAmount", dto.getIntegrateSecAmount());
                if(DictCodeListExt3.交易方向_解除质押.getValue().equals(entity.getPs())){
                    // ZGTFCP-1900:修改解除质押字段赋值 modify by fuyongsheng 20220327 start
                    FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(entity, DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    if(null != temp){
                        ptlRelieveSec.put("finprodId", temp.getFinprodId());
                        // 本方券券面总额
                        ptlRelieveSec.put("ownSecAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                        // 券面总额
                        ptlRelieveSec.put("secAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                    }
                    // ZGTFCP-1900:修改解除质押字段赋值 modify by fuyongsheng 20220327 end
                }else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(entity.getPs())){
                    FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(entity, DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                    if(null != temp){
                        ptlRelieveSec.put("finprodId", temp.getFinprodId());
                        // 本方券券面总额
                        ptlRelieveSec.put("ownSecAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                        // 券面总额
                        ptlRelieveSec.put("secAmount", ArithmeticUtil.multiply(temp.getPledgeDisplaceAmt(), new BigDecimal(100)));
                    }
                }else{
                    ptlRelieveSec.put("finprodId", entity.getPositionBaseData().getFinDealPledge().getPledgeFinprodId());
                    // ZGTFCP-1739:需要乘以100 modify by fuyongsheng 20220325 start
                    ptlRelieveSec.put("ownSecAmount", ArithmeticUtil.multiply(entity.getPositionBaseData()
                            .getFinDealPledge().getPledgeNumber(), new BigDecimal(100)));
                    ptlRelieveSec.put("secAmount", ArithmeticUtil.multiply(entity.getPositionBaseData()
                            .getFinDealPledge().getPledgeNumber(), new BigDecimal(100)));
                    // ZGTFCP-1739:需要乘以100 modify by fuyongsheng 20220325 end
                }
                ptlRelieveSecList.add(ptlRelieveSec);
            }
        }
        resultMap.put("ptlCashList", ptlCashList);
        resultMap.put("ptlOffsetCashList", ptlOffsetCashList);
        resultMap.put("ptlSecList", ptlSecList);
        resultMap.put("ptlRelieveSecList", ptlRelieveSecList);
    }
    // ZGTFCP-1041:新增成交回报协议回购 add by fuyongsheng 20220317 end

    /**
     * 基金-指令
     *
     * @param entity
     * @param resultMap
     */
    private void fund002(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_FUND002);
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, INTERF_TYPE_FUND002));
        resultMap.put("ps", entity.getPs());
        resultMap.put("settleSpeed", null);
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("deliveryType", null);
        resultMap.put("workFlowId", null);
        resultMap.put("sameDayTurn", 是否_否.getValue());
        resultMap.put("ETFRedemptionMethod", null);
        resultMap.put("cdate", entity.getTradeDate());
        resultMap.put("ccy", entity.getCcy());
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        List<Map<String, Object>> ptlList = new ArrayList<>(4);
        for (PortfolioCalculateDto dto : list){
            Map<String, Object> plt = new HashMap<>(8);
            plt.put("portfolioId", dto.getPortfolioId());
            plt.put("tradeId", entity.getTradeId());
            plt.put("etrId", entity.getEntrustId());
            plt.put("finprodId", entity.getFinprodId());
            plt.put("cashAcctId", dto.getCashAcctId());
            plt.put("secAcctId", dto.getSecAcctId());
            plt.put("secAmount", dto.getSecAmount());
            plt.put("cashAmount", dto.getCashAmount());
            plt.put("ordEntrustId", null);
            plt.put("etrSecAmount", dto.getEtrSecAmount());
            plt.put("ordId", dto.getOrderId());
            plt.put("ordSecAmount", dto.getOrdSecAmount());
            ptlList.add(plt);
        }
        resultMap.put("ptlList", ptlList);
    }

    /**
     * 股票指令
     *
     * @param entity
     * @param resultMap
     */
    private void stock001(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_STOCK001);
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, INTERF_TYPE_STOCK001));
        resultMap.put("ps", entity.getPs());
        resultMap.put("tradePlace", entity.getTradeMarket());
        resultMap.put("settleSpeed", null);
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getTradeDate());
        resultMap.put("ccy", entity.getCcy());
        resultMap.put("bTotalTradeId", null);

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("fTradeId", entity.getfTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        List<Map<String, Object>> ptlList = new ArrayList<>(4);
        List<Map<String, Object>> ptlRoList = new ArrayList<>(4);
        for (PortfolioCalculateDto dto : list){
            Map<String, Object> plt = new HashMap<>(8);
            plt.put("portfolioId", dto.getPortfolioId());
            plt.put("finprodId", entity.getFinprodId());
            plt.put("tradeId", entity.getTradeId());
            plt.put("etrId", entity.getEntrustId());
            plt.put("etrSecAmount", dto.getEtrSecAmount());
            plt.put("ordId", dto.getOrderId());
            plt.put("ordSecAmount", dto.getOrdSecAmount());
            plt.put("ordEntrustId", null);
            plt.put("cashAcctId", dto.getCashAcctId());
            plt.put("secAcctId", dto.getSecAcctId());
            plt.put("secAmount", dto.getSecAmount());
            plt.put("cashAmount", dto.getCashAmount());
            ptlList.add(plt);

            if(DictCodeListExt2.交易方向_配股.getValue().equals(entity.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(entity.getPs())){
                Map<String, Object> ptlRo = new HashMap<>();
                ptlRo.put("portfolioId", dto.getPortfolioId());
                ptlRo.put("finprodId", entity.getFinprodId());
                ptlRo.put("tradeId", entity.getTradeId());
                ptlRo.put("etrId", entity.getEntrustId());
                ptlRo.put("etrSecAmount", dto.getEtrSecAmount());
                ptlRo.put("ordId", dto.getOrderId());
                ptlRo.put("ordSecAmount", dto.getOrdSecAmount());
                ptlRo.put("ordEntrustId", null);
                ptlRo.put("cashAcctId", dto.getCashAcctId());
                ptlRo.put("secAcctId", dto.getSecAcctId());
                ptlRo.put("secAmount", dto.getSecAmount());
                ptlRo.put("cashAmount", dto.getCashAmount());
                ptlRoList.add(ptlRo);
            }
        }
        resultMap.put("ptlList", ptlList);
        resultMap.put("ptlRoList", ptlRoList);
    }

    /**
     * 解析清算速度
     * <p>
     * 清算速度	见数据字典：清算速度
     * "IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.净额结算 THEN 数据字典.清算速度.T+1
     * ELSE 数据字典.清算速度.T+0
     *
     * @param deliveryType 封装集合
     * @return 清算速度
     */
    private String parseSettleSpreed(String deliveryType) {
        String settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
        if (DictCodeListExt3.结算方式_头寸引擎_净额结算.getValue().equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        }
        return settleSpreed;
    }

    /**
     * 查询再折算率
     * @param acctNo
     * @return
     */
    private TrdMoreConvrate getMoreConvrate(String acctNo){
        // 再折算率	reConversionRate "调用【再折算率查询】参数：资金账户 = 指令资金账户
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("acctNo", acctNo);
        Map<String, Object> res = simpleRestTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMoreConvrate", param, Map.class);
        if (res != null && res.get("data") != null) {
            List<TrdMoreConvrate> list = (List<TrdMoreConvrate>) res.get("data");
            if (list != null && list.size() > 0) {
                return JSON.parseObject(JSON.toJSONString(list.get(0)), TrdMoreConvrate.class);
            }
        }
        return null;
    }

    /**
     * 获取交易平台
     * @param entity
     * @param interfaceType
     * @return
     */
    private String getSharesTradePlatform(TrdTempDto entity, String interfaceType){
        return commonPositionEngineService.getExchangeTradePlatform(entity.getEntrustId(), entity.getTradePlat(),
                entity.getPs(), entity.getFinprodId(), interfaceType);
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(TrdTempDto entity, Map<String, Object> resultMap) {
        resultMap.put("userId", LoginUserUtil.getUserName());
        resultMap.put("totalTradeId", entity.getTradeId());
        // resultMap.put("balanceCheck", "Y");
        resultMap.put("balanceCheck", "N");
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustId());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getEntrustId());
        String busiType = commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                etrEntrustAdd.getAssetType1());
        // 业务类型
        resultMap.put("busiType", busiType);
        // 业务动作
        resultMap.put("busiAct", DictCodeListExt3.业务动作_成交回报.getValue());
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
    }

    /**
     * 查询换入换出质押券
     * @param entity
     * @param tradeType
     * @return
     */
    private FinPledgeDispDetailTemp getFinPledgeDispDetailTemp(TrdTempDto entity, String tradeType){
        List<FinPledgeDispDetailTemp> finPledgeDispDetailTemps = entity.getFinPledgeDispDetailTempList();
        if(!StringUtil.isEmptyList(finPledgeDispDetailTemps)){
            for(FinPledgeDispDetailTemp temp : finPledgeDispDetailTemps){
                if(tradeType.equals(temp.getTradeType())){
                    return temp;
                }
            }
        }
        return null;
    }

    /**
     * 查询产品托管户
     * @param etrEntrust
     * @return
     */
    private String getPtlCapAcctNo(EtrEntrust etrEntrust){
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", etrEntrust.getPortfolioId());
        map.put("ccy", etrEntrust.getCcy());
        map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        PtlCapacctInfo ptlCapacctInfo = trdCommonBaseService.getPtlCapAcctInfo(map);
        return ptlCapacctInfo != null ? ptlCapacctInfo.getAcctNo() : StringUtil.EMPTY;
    }
}
