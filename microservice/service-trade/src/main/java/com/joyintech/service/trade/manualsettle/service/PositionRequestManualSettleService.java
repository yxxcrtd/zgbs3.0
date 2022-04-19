package com.joyintech.service.trade.manualsettle.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.entrustmsgbymq.dto.TrdTempDto;
import com.joyintech.service.trade.manualsettle.dao.ManualSettleMapper;
import com.joyintech.service.trade.manualsettle.dto.ManualSettleDto;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.position.dto.EtrEntrustAddDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.trade.dto.PortfolioCalculateDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeList.是否_否;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestManualSettleService {
    private static final Logger logger = LoggerFactory.getLogger(PositionRequestManualSettleService.class);

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private TrdProductTempDealMapper trdProductTempDealMapper;

    @Autowired
    private ManualSettleMapper manualSettleMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    public static final String TRADE_PLAT = "TRD_MANUAL_SETTLE";

    /**
     * EtrExchangeTradeSaveRequest请求对象使用
     *
     * @param entity
     */
    public void sendRequest(ManualSettleDto entity) {
        logger.debug("【发送手工交收头寸请求】entity: {}", JSON.toJSONString(entity));
        Map<String, Object> resultMap = new HashMap<>();
        setCommonField(entity, resultMap);
        String finprodType = entity.getFinprodType2();
        String type6 = commonPositionEngineService.getType6(entity.getFinprodId());
        if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)
                // ZGTFCP-1372:增加债券资产类型条件 modify by fuyongsheng 20220319 start
                && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)) {
            // ZGTFCP-1372:增加债券资产类型条件 modify by fuyongsheng 20220319 end
            finprodType += "-" + type6;
        }
        TrdProductTempDeal trdProductTempDeal = trdProductTempDealMapper.selectByPrimaryKey(entity.getTradeId());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getEntrustNo());
        if(etrEntrustAdd != null){
            if (DictCodeList.回购类型_质押式协议回购.getValue().equals(etrEntrustAdd.getAssetType1())
                    || (DictCodeList.回购类型_质押式竞价回购.getValue().equals(etrEntrustAdd.getAssetType1()))) {
                finprodType += "-" + etrEntrustAdd.getAssetType1();
            }
        }
        logger.debug("【手工交收查询头寸接口参数】finprodType:{},ps:{}", finprodType, trdProductTempDeal.getPs());
        String interType = commonPositionEngineService.getPositionInterType("TRD", TRADE_PLAT, finprodType, trdProductTempDeal.getPs(), DictCodeListExt3.共通业务类型_手工交收.getValue(), entity.getClass().getName());
        logger.debug("【手工交收查询头寸接口结果】interType:{}", interType);
        if (INTERF_TYPE_BOND002.equals(interType)) {
            this.bond002(entity, resultMap);
        } else if (INTERF_TYPE_BOND003.equals(interType)) {
            this.bond003(entity, resultMap);
        } else if (INTERF_TYPE_REPO002.equals(interType)) {
            this.repo002(entity, resultMap);
        }
        if(StringUtil.isNotEmpty(resultMap.get(INTERFACE_TYPE))){
            PositionHttpRequest.send(resultMap);
        }else{
            logger.debug("[手工交收未找到对应的接口配置]");
        }
    }

    /**
     * 场内现券-指令
     *
     * @param entity
     * @param resultMap
     */
    private void bond002(ManualSettleDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_BOND002);
        TrdProductTempDeal trdProductTempDeal = trdProductTempDealMapper.selectByPrimaryKey(entity.getTradeId());
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 start
        // String busiType = commonPositionEngineService.getPositionBusiType(trdProductTempDeal.getFinprodType2(), trdProductTempDeal.getPs(), null);
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustNo());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getEntrustNo());
        String busiType = commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                etrEntrustAdd.getAssetType1());
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 end
        resultMap.put("busiType", busiType);
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
        resultMap.put("totalTradeId", entity.getSettleSeqNo());
        if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收确认.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_回溯.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收回溯.getValue());
            resultMap.put("bTotalTradeId", entity.getSettleSeqNo());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收失败.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        }
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
        resultMap.put("ps", trdProductTempDeal.getPs());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put("settleSpeed", parseSettleSpreed(deliveryType));
        resultMap.put("bidResult", 是否_否.getValue());
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", entity.getTradeMarket());
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, trdProductTempDeal.getPs(), INTERF_TYPE_BOND002));
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getSettleDate());
        resultMap.put("ccy", trdProductTempDeal.getCcy());
        resultMap.put("finprodId", entity.getFinprodId());

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("dealNo", entity.getTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);

        List<Map<String, Object>> ptlList = new ArrayList<>();
        for (PortfolioCalculateDto dto:list) {
            Map<String, Object> pltMap = new HashMap<>();
            pltMap.put("portfolioId", dto.getPortfolioId());
            pltMap.put("tradeId", entity.getSettleSeqNo());
            pltMap.put("etrId", etrEntrust.getEntrustId());
            pltMap.put("ordEntrustId", null);
            pltMap.put("etrSecAmount", dto.getEtrSecAmount());
            pltMap.put("ordId", dto.getOrderId());
            pltMap.put("ordSecAmount", dto.getOrdSecAmount());
            // ZGTFCP-1461:修改赋值 modify by fuyongsheng 20220322 start
//            pltMap.put("cashAcctId", entity.getAcctNo());
//            pltMap.put("secAcctId", entity.getSecAcctId());
            pltMap.put("cashAcctId", dto.getCashAcctId());
            pltMap.put("secAcctId", dto.getSecAcctId());
            // ZGTFCP-1461:修改赋值 modify by fuyongsheng 20220322 end
            pltMap.put("secAmount", dto.getSecAmount());
            pltMap.put("cashAmount", dto.getCashAmount());
            pltMap.put("invAim", trdProductTempDeal.getInvAim());
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
    private void bond003(ManualSettleDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_BOND003);

        TrdProductTempDeal trdProductTempDeal = trdProductTempDealMapper.selectByPrimaryKey(entity.getTradeId());

        // String busiType = commonPositionEngineService.getPositionBusiType(trdProductTempDeal.getFinprodType2(), trdProductTempDeal.getPs(), null);
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 start
        // String busiType = commonPositionEngineService.getPositionBusiType(trdProductTempDeal.getFinprodType2(), trdProductTempDeal.getPs(), null);
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustNo());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getEntrustNo());
        String busiType = commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                etrEntrustAdd.getAssetType1());
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 end
        resultMap.put("busiType", busiType);
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
        resultMap.put("totalTradeId", entity.getSettleSeqNo());
        if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收确认.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_回溯.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收回溯.getValue());
            resultMap.put("bTotalTradeId", entity.getSettleSeqNo());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收失败.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        }
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
        resultMap.put("ps", trdProductTempDeal.getPs());
        String deliveryType = commonPositionEngineService.getDeliveryType(entity.getFinprodId());
        resultMap.put("settleSpeed", parseSettleSpreed(deliveryType));
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", entity.getTradeMarket());
        // 交易平台
        resultMap.put("sharesTradePlatform", this.getSharesTradePlatform(entity, trdProductTempDeal.getPs(), INTERF_TYPE_BOND003));
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getSettleDate());
        resultMap.put("ccy", trdProductTempDeal.getCcy());

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("dealNo", entity.getTradeId());
        List<PortfolioCalculateDto> list = trdCommonBaseService.queryPortfolioCalculate(paraMap);
        List<Map<String, Object>> ptlCashList = new ArrayList<>(4);
        List<Map<String, Object>> bondSecList = new ArrayList<>(4);
        for (PortfolioCalculateDto dto : list){
            Map<String, Object> ptlCash = new HashMap<>(8);
            ptlCash.put("portfolioId", dto.getPortfolioId());
            ptlCash.put("cashAcctId", dto.getCashAcctId());
            ptlCash.put("tradeId", entity.getTradeId());
            ptlCash.put("etrId", etrEntrust.getEntrustId());
            ptlCash.put("cashAmount", dto.getCashAmount());
            ptlCash.put("etrSecAmount", dto.getEtrSecAmount());
            ptlCash.put("ordId", dto.getOrderId());
            ptlCash.put("ordSecAmount", dto.getOrdSecAmount());
            ptlCash.put("secAmount", dto.getSecAmount());
            ptlCashList.add(ptlCash);

            Map<String, Object> bondSec = new HashMap<>();
            bondSec.put("portfolioId", dto.getPortfolioId());
            bondSec.put("finprodId", entity.getFinprodId());
            bondSec.put("secAcctId", dto.getSecAcctId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
            bondSec.put("tradeId", entity.getTradeId());
            bondSec.put("etrId", etrEntrust.getEntrustId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
            bondSec.put("integrateSecAmount", new BigDecimal(0));
            bondSec.put("ownSecAmount", dto.getOwnSecAmount());
            bondSec.put("secAmount", dto.getSecAmount());
            bondSecList.add(bondSec);
        }
        resultMap.put("ptlCashList", ptlCashList);
        resultMap.put("bondSecList", bondSecList);
    }

    /**
     * 质押式协议回购-指令
     *
     * @param entity
     * @param resultMap
     */
    private void repo002(ManualSettleDto entity, Map<String, Object> resultMap) {
        resultMap.put("interfaceType", INTERF_TYPE_REPO002);

        InfContractDetailfile infContractDetailfile = manualSettleMapper.selectInfContractDetailfile(entity.getEntrustNo(), entity.getDealNo());

        // String busiType = commonPositionEngineService.getPositionBusiType(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), infContractDetailfile.getPs(), DictCodeList.回购类型_质押式协议回购.getValue());
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 start
        // String busiType = commonPositionEngineService.getPositionBusiType(trdProductTempDeal.getFinprodType2(), trdProductTempDeal.getPs(), null);
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getEntrustNo());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getEntrustNo());
        String busiType = commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                etrEntrustAdd.getAssetType1());
        // ZGTFCP-1281:修改获取业务类型方法 modify by fuyongsheng 20220318 end
        resultMap.put("busiType", busiType);
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
        resultMap.put("totalTradeId", entity.getSettleSeqNo());
        if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收确认.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_回溯.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收回溯.getValue());
            resultMap.put("bTotalTradeId", entity.getSettleSeqNo());
        } else if (StringUtil.equals(entity.getPageModel(), DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            resultMap.put("busiAct", DictCodeListExt3.业务动作_交收失败.getValue());
            resultMap.put("bTotalTradeId", entity.getTradeId());
        }
        // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
        // resultMap.put("ps", infContractDetailfile.getPs());
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
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", entity.getSettleDate());
        resultMap.put("ccy", infContractDetailfile.getCcy());

        // 首期组合现金数据集
        if (DictCodeListExt3.交易方向_正回购.getValue().equals(infContractDetailfile.getPs())
                // ZGTFCP-1417:添加逆回购方向 modify by fuyongsheng 20220321 start
                || DictCodeListExt3.交易方向_逆回购.getValue().equals(infContractDetailfile.getPs())
                // ZGTFCP-1417:添加逆回购方向 modify by fuyongsheng 20220321 end
                || DictCodeListExt3.交易方向_首期.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())
                || (DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs()))
                || (DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs()))) {
            List<Map<String, Object>> list = new ArrayList<>(4);
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", entity.getPortfolioId());
            if(DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())){
                map.put("cashAcctId", getPtlCapAcctNo(infContractDetailfile));
                // 查询首期交易信息
                // ZGTFCP-2097:需要查询首期 modif by fuyongsheng 20220329 start
                FinTradeProduct finTradeProduct = getSqFinTradeProduct(infContractDetailfile);
                // ZGTFCP-2097:需要查询首期 modif by fuyongsheng 20220329 end
                map.put("cashAmount", finTradeProduct != null ? finTradeProduct.getmTradeAmt() : new BigDecimal(0));
            }
            // ZGTFCP-2045:到期购回需要登录首期的到期结算金额 modify by fuyongsheng 20220327 start
            else if(DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())
                    || DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())){
                map.put("cashAcctId", infContractDetailfile.getAcctNo());
                // ZGTFCP-2114:登陆当前的到期结算金额 modify by fuyongsheng 20220329 start
                map.put("cashAmount", entity.getmTradeAmt());
                // ZGTFCP-2114:登陆当前的到期结算金额 modify by fuyongsheng 20220329 end
            }
            // ZGTFCP-2045:到期购回需要登录首期的到期结算金额 modify by fuyongsheng 20220327 end
            else{
                map.put("cashAmount", entity.getPrin());
                map.put("cashAcctId", infContractDetailfile.getAcctNo());
            }
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
            map.put("tradeId", entity.getTradeId());
            map.put("etrId", etrEntrust.getEntrustId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
            list.add(map);
            resultMap.put("ptlCashList", list);
        }

        // 轧差组合现金数据集
        if (DictCodeListExt3.交易方向_到期续做.getValue().equals(infContractDetailfile.getPs())) {
            List<Map<String, Object>> list = new ArrayList<>(4);
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", entity.getPortfolioId());
            map.put("cashAcctId", infContractDetailfile.getAcctNo());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
            map.put("tradeId", entity.getTradeId());
            map.put("etrId", etrEntrust.getEntrustId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end

            // "IF 回购类型 = 正回购 THEN 指令.到期续作金额 - 指令.首期的到期结算金额
            // IF 回购类型 = 逆回购  and 成交编号找不到对应的首期 THEN 指令.到期续作金额
            // IF 回购类型 = 逆回购  and 成交编号找到对应的首期 THEN  指令.到期续作金额 - 指令.首期的到期结算金额"
            // 查询首期交易信息
            // ZGTFCP-2097:需要查询首期 modif by fuyongsheng 20220329 start
            FinTradeProduct finTradeProduct = getSqFinTradeProduct(infContractDetailfile);
            map.put("cashAmount", finTradeProduct != null ? ArithmeticUtil.subtract(entity.getPrin(),
                    finTradeProduct.getmTradeAmt()) : entity.getPrin());
            // ZGTFCP-2097:需要查询首期 modif by fuyongsheng 20220329 end
            list.add(map);
            resultMap.put("ptlOffsetCashList", list);
        }

        // 质押券数据集	ptlSecList
        // if (DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())) {
        if (DictCodeListExt3.交易方向_正回购.getValue().equals(infContractDetailfile.getPs())
            || DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())
            || DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())) {
            List<Map<String, Object>> list = new ArrayList<>(4);
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", entity.getPortfolioId());
            map.put("secAcctId", infContractDetailfile.getBondId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
            map.put("tradeId", entity.getTradeId());
            map.put("etrId", etrEntrust.getEntrustId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
            map.put("integrateSecAmount", new BigDecimal(0));
            if(DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())){
                FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(infContractDetailfile, DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                if(null != temp){
                    map.put("finprodId", temp.getFinprodId());
                    map.put("ownSecAmount", temp.getPledgeDisplaceAmt().multiply(new BigDecimal(100)));
                    map.put("secAmount", temp.getPledgeDisplaceAmt().multiply(new BigDecimal(100)));
                }
            }else{
                map.put("finprodId", entity.getPledgeId());
                map.put("ownSecAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
                map.put("secAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
            }
            list.add(map);
            resultMap.put("ptlSecList", list);
        }

        // 解除质押券数据集	ptlRelieveSecList
        if (DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_到期续做.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_提前购回.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_到期购回.getValue().equals(infContractDetailfile.getPs())
                || DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs()))
        {
            List<Map<String, Object>> list = new ArrayList<>(4);
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", entity.getPortfolioId());
            map.put("finprodId", entity.getPledgeId());
            map.put("secAcctId", infContractDetailfile.getBondId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 start
            map.put("tradeId", entity.getTradeId());
            map.put("etrId", etrEntrust.getEntrustId());
            // ZGTFCP-1372:修改字段赋值 modify by fuyongsheng 20220319 end
            map.put("integrateSecAmount", new BigDecimal(0));
            if(DictCodeListExt3.交易方向_解除质押.getValue().equals(infContractDetailfile.getPs())){
                map.put("ownSecAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
                map.put("secAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
            }else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(infContractDetailfile.getPs())){
                FinPledgeDispDetailTemp temp = this.getFinPledgeDispDetailTemp(infContractDetailfile, DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                if(null != temp){
                    map.put("finprodId", temp.getFinprodId());
                    map.put("ownSecAmount", temp.getPledgeDisplaceAmt().multiply(new BigDecimal(100)));
                    map.put("secAmount", temp.getPledgeDisplaceAmt().multiply(new BigDecimal(100)));
                }
            }
            // ZGTFCP-2059:提前购回、到期购回、提前续作使用当前质押券信息 modify by fuyongsheng 20220329 start
            else {
                map.put("ownSecAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
                map.put("secAmount", entity.getPledgeNumber().multiply(new BigDecimal(100)));
            }
            // ZGTFCP-2059:提前购回、到期购回、提前续作使用当前质押券信息 modify by fuyongsheng 20220329 end
            list.add(map);
            resultMap.put("ptlRelieveSecList", list);
        }
    }

    /**
     * 解析清算速度
     * <p>
     * 清算速度	见数据字典：清算速度
     * "IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.净额结算 THEN 数据字典.清算速度.T+1
     * ELSE IF 变量.结算方式 = 数据字典.DELIVERY_TYPE.RTGS THEN 数据字典.清算速度.T+0
     * ELSE 数据字典.清算速度.T+1
     *
     * @param deliveryType 封装集合
     * @return 清算速度
     */
    private String parseSettleSpreed(String deliveryType) {
        String settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        if (DictCodeListExt3.结算方式_头寸引擎_净额结算.getValue().equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
        } else if ((DictCodeListExt3.结算方式_头寸引擎_RTGS.getValue()).equals(deliveryType)) {
            settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
        }
        return settleSpreed;
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(ManualSettleDto entity, Map<String, Object> resultMap) {
        resultMap.put("userId", LoginUserUtil.getUserName());
        resultMap.put("totalTradeId", entity.getTradeId());
        // resultMap.put("balanceCheck", "Y");
        resultMap.put("balanceCheck", "N");
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
    }

    /**
     * 查询产品托管户
     * @param infContractDetailfile
     * @return
     */
    private String getPtlCapAcctNo(InfContractDetailfile infContractDetailfile){
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", infContractDetailfile.getPortfolioId());
        map.put("ccy", infContractDetailfile.getCcy());
        map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        PtlCapacctInfo ptlCapacctInfo = trdCommonBaseService.getPtlCapAcctInfo(map);
        return ptlCapacctInfo != null ? ptlCapacctInfo.getAcctNo() : StringUtil.EMPTY;
    }

    /**
     * 查询换入换出质押券
     * @param entity
     * @param tradeType
     * @return
     */
    private FinPledgeDispDetailTemp getFinPledgeDispDetailTemp(InfContractDetailfile entity, String tradeType){
        Map<String, Object> map = new HashMap<>();
        // ZGTFCP-1988:修改查询换入换出质押券方法 modify by fuyongsheng 20220328 start
        map.put("fTradeId", entity.getvTradeId());
        // ZGTFCP-1988:修改查询换入换出质押券方法 modify by fuyongsheng 20220328 end
        List<FinPledgeDispDetailTemp> finPledgeDispDetailTemps = trdCommonBaseService.getFinPledgeDispDetailTemps(map);
        if(!StringUtil.isEmptyList(finPledgeDispDetailTemps)){
            for(FinPledgeDispDetailTemp finPledgeDispDetailTemp : finPledgeDispDetailTemps){
                if(tradeType.equals(finPledgeDispDetailTemp.getTradeType())){
                    return finPledgeDispDetailTemp;
                }
            }
        }
        return null;
    }

    /**
     * 查询首期交易信息
     * @param infContractDetailfile
     * @return
     */
    private FinTradeProduct getSqFinTradeProduct(InfContractDetailfile infContractDetailfile){
        // 首期成交编号
        String finprodId = infContractDetailfile.getvTradeId();
        String entrustId = infContractDetailfile.getEntrustId();
        // 指令附属信息
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        EtrEntrustAddDto etrEntrustAddDto = new EtrEntrustAddDto();
        if (etrEntrustAdd != null){
            BeanUtil.copyPropertiesIgnoreNull(etrEntrustAddDto,etrEntrustAdd);
            if (etrEntrustAdd.getTradeId() != null) {
                finprodId = etrEntrustAdd.getTradeId();
            }
        }
        String hgsqFinprodId = trdCommonBaseService.selectHgsqFinprodId(infContractDetailfile.getPortfolioId(), finprodId);
        if (StringUtil.isNotBlank(hgsqFinprodId)) {
            finprodId = hgsqFinprodId;
        }
        FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
        finTradeProductKey.setFinprodId(finprodId);
        finTradeProductKey.setBranch(0L);
        return finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
    }

    /**
     * 获取交易平台
     * @param entity
     * @param ps
     * @param interfaceType
     * @return
     */
    private String getSharesTradePlatform(ManualSettleDto entity, String ps, String interfaceType){
        return commonPositionEngineService.getExchangeTradePlatform(entity.getEntrustNo(),
                entity.getTradePlat(), ps, entity.getFinprodId(), interfaceType);
    }

}
