package com.joyintech.tams.invest.trade.exchangetransmatch.position.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.StlSecAcctInfoMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonRevokeDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dao.ExchangeTransMatchCommonMapper;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.bean.PositionRequestBaseBean;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.bean.TrdMoreConvrateDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.constant.PositionConstant;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import static com.joyintech.tams.constant.DictCodeList.是否_否;
import static com.joyintech.tams.constant.DictCodeListExt3.交易单位_份;
import static com.joyintech.tams.constant.DictCodeListExt3.交易单位_张;
import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestServiceFinProductTradeService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(PositionRequestServiceFinProductTradeService.class);

    public static final String STANDAR_SEC_ID = "XN_PRODID999";

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private ExchangeTransMatchCommonMapper exchangeTransMatchCommonMapper;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private CommonPositionService commonPositionService;
  
    public static final String TRADE_PLAT = "TRANS_MATCH";

    /**
     * BaseTradeProductDealDto请求对象使用
     * @param finProductTradeDto
     */
    public void sendFinProductTradeDto(FinProductTradeDto finProductTradeDto) {
        logger.debug("【发送成交匹配头寸请求】finProductTradeDto:{}", JSON.toJSONString(finProductTradeDto));
        for (BaseTradeProductDealDto entity:finProductTradeDto.getBaseTradeProductDealDtos()) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
            resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
            this.setCommonField(entity, resultMap);//设置公共字段，个性化的需要在具体接口中覆盖
            String type6 = commonPositionEngineService.getType6(entity.getTrdProductDeal().getFinprodId());
            String finprodType = entity.getTrdProductDeal().getFinprodType2();
            if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)
                    && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)) {
                finprodType += "-" + type6;
            }
            // ZGTFCP-1164:修改判断协议回购方法 modify by fuyongsheng 20220317 start
            EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getTrdProductDealAdd().getEntrustId());
            if(etrEntrustAdd != null){
                if (DictCodeList.回购类型_质押式协议回购.getValue().equals(etrEntrustAdd.getAssetType1())
                        || (DictCodeList.回购类型_质押式竞价回购.getValue().equals(etrEntrustAdd.getAssetType1()))) {
                    finprodType += "-" + etrEntrustAdd.getAssetType1();
                }
            }
            // ZGTFCP-1164:修改判断协议回购方法 modify by fuyongsheng 20220317 end
            String interType = commonPositionEngineService.getPositionInterType(PositionConstant.POSITION_TYPE_TRD, TRADE_PLAT,
                    finprodType, entity.getTrdProductDeal().getPs(), DictCodeListExt3.业务动作_成交匹配.getValue(), entity.getClass().getName());
            if (PositionConstant.INTERF_TYPE_BOND002.equals(interType)) {
                this.bond002(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_BOND003.equals(interType)) {
                this.bond003(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_FUND002.equals(interType)) {
                this.fund002(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_REPO003.equals(interType)) {
                this.repo003(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_REPO002.equals(interType)) {
                this.repo002(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_STOCK001.equals(interType)) {
                this.stock001(entity, resultMap);
            } else if (PositionConstant.INTERF_TYPE_CREDIT001.equals(interType)) {
                this.credit001(entity, resultMap);
            }
            if(StringUtil.isNotEmpty(resultMap.get(INTERFACE_TYPE))){
                PositionHttpRequest.send(resultMap);
            }else{
                logger.debug("[成交匹配未找到对应的接口配置]");
            }
        }
    }

    /**
     * 撤销时使用---撤销指令-共通接口
     *
     * @param exchangeTransMatchCommonRevokeDtoList
     */
    public void sendRevoke(List<ExchangeTransMatchCommonRevokeDto> exchangeTransMatchCommonRevokeDtoList) {
        for (ExchangeTransMatchCommonRevokeDto entity:exchangeTransMatchCommonRevokeDtoList) {
            String busiType = commonPositionEngineService.getPositionBusiType(entity.getFinprodType2(),entity.getPs(), null);
            PositionHttpRequest.dorevoke(entity.getTradeId(), PositionConstant.POSITION_TYPE_TRD, busiType, entity.getPs());
        }
    }

    /**
     * 场内现券-指令
     *
     * @param entity
     * @param resultMap
     */
    private void bond002(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        TrdProductDeal trdProductDeal = entity.getTrdProductDeal();
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_BOND002);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_BOND002));
        resultMap.put(BUSI_ACT, getBusiAct(trdProductDeal));
        resultMap.put(BID_RESULT, 是否_否.getValue());
        resultMap.put(PURCH_MODLE, null);

        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();

        resultMap.put(FINPROD_ID, dealEntity.getFinprodId());
        String deliveryType = commonPositionEngineService.getDeliveryType(dealEntity.getFinprodId());
        resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed(deliveryType));
        resultMap.put(TRADE_PLACE, dealEntity.getTradeMarket());
        resultMap.put(DELIVERY_TYPE, deliveryType);
        resultMap.put(WORK_FLOW_ID, dealEntity.getWorkflowId());
        resultMap.put("payType", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(trdProductDeal.getFinprodType2()) ?
                BussinessDictCodeList.缴款方式_一级分销.getValue() : null);
        resultMap.put("cdate", DateUtil.format(dealEntity.getTradeDate()));
        resultMap.put(FINPROD_ID, dealEntity.getFinprodId());
        resultMap.put(B_TOTAL_TRADE_ID, null);

        List<Map<String, Object>> ptlList = new ArrayList<>();
        Map<String, Object> pltMap = new HashMap<>();
        pltMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
        pltMap.put("tradeId", dealEntity.getTradeId());
        pltMap.put("etrId", trdProductDealAdd.getEntrustId());
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        if(etrEntrust != null){
            pltMap.put("etrSecAmount", etrEntrust.getEntrustNum());
        }
        pltMap.put("ordId", null);
        pltMap.put("ordSecAmount", null);
        // ZGTFCP-1699: 修改资金账户赋值 modify by fuyongsheng 20220323 start
//        if(CollectionUtils.isNotEmpty(stlPayOrdersList)){
//            pltMap.put("cashAcctId", stlPayOrdersList.get(0).getMyAcctNo());
//        }
        pltMap.put("cashAcctId", exchangeTransMatchCommonMapper.queryAccountNo(dealEntity.getSecManageAcctId()));
        // ZGTFCP-1699: 修改资金账户赋值 modify by fuyongsheng 20220323 end
        pltMap.put("secAcctId", dealEntity.getSecManageAcctId());
        pltMap.put("secAmount", dealEntity.getShareAmt());
        pltMap.put("cashAmount", dealEntity.getTradeAmt());
        pltMap.put("invAim", dealEntity.getInvAim());
        pltMap.put("integrateSecAmount", BigDecimal.ZERO);
        pltMap.put("ownSecAmount", dealEntity.getShareAmt());
        ptlList.add(pltMap);
        resultMap.put("ptlList", ptlList);
    }

    private Object getBusiAct(TrdProductDeal trdProductDeal) {
        String busiAct = DictCodeListExt3.业务动作_成交匹配.getValue();
        if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(trdProductDeal.getFinprodType2())
                && StringUtil.isNotEmpty(trdProductDeal.getbTradeId())) {
            busiAct = DictCodeListExt3.业务动作_成交确认.getValue();
        }
        return busiAct;
    }

    /**
     * 根据指令单位是张还是手来处理交易金额
     * 债券使用
     *
     * @param entrustNum 委托数量
     */
    private BigDecimal getSecAmountByBondUnit(BigDecimal entrustNum) {
        BigDecimal secAmount = BigDecimal.ZERO;
        if (Objects.isNull(entrustNum)) {
            return secAmount;
        }
        String bondTradedUnit = CacheUtil.getSysParam("bond_traded_unit");
        if (交易单位_张.getValue().equals(bondTradedUnit)) {
            secAmount = entrustNum.multiply(BigDecimal.valueOf(100L));
        } else if (交易单位_份.getValue().equals(bondTradedUnit)) {
            secAmount = entrustNum.multiply(BigDecimal.valueOf(1000L));
        }
        return secAmount;
    }

    /**
     * 可转债-指令
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_BOND003);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_BOND003));
        // 业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());

        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        // 金融产品实物交易表
        List<TrdFinProductBarter> trdFinProductBarters = entity.getTrdFinProductBarterList();

        resultMap.put(DELIVERY_TYPE, commonPositionEngineService.getDeliveryType(dealEntity.getFinprodId()));
        String ps = dealEntity.getPs();
        if(DictCodeListExt1.交易方向_债转股.getValue().equals(ps)){
            resultMap.put(SETTLE_SPEED, BussinessDictCodeList.清算速度_T加0.getValue());
        }else{
            resultMap.put(SETTLE_SPEED, commonPositionEngineService.parseSettleSpreed((String) resultMap.get(DELIVERY_TYPE)));
        }
        // 申购方式	见数据字典：申购方式
        if (DictCodeList.交易方向_申购.getValue().equals(dealEntity.getPs())) {
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_中签.getValue());
            resultMap.put(PURCH_MODLE, DictCodeListExt2.申购方式_网上申购.getValue());
        }else if(DictCodeList.交易方向_申购确认.getValue().equals(dealEntity.getPs())){
            resultMap.put("busiType", DictCodeListExt3.头寸业务类型_网上申购.getValue());
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_缴款.getValue());
        }
        PositionRequestBaseBean bean;

        // 组合现金数据集
        List<Map<String, Object>> ptlCashList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", trdProductDealAdd.getPortfolioId());
        // 业务编号
        map.put("tradeId", dealEntity.getTradeId());
        // 资金账号
        map.put("cashAcctId", exchangeTransMatchCommonMapper.queryAccountNo(dealEntity.getSecManageAcctId()));
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        // 指令券面总额
        if(etrEntrust != null){
            map.put("etrSecAmount", ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), new BigDecimal(100)));
        }
        // 指令编号
        map.put("etrId", trdProductDealAdd.getEntrustId());
        // ZGTFCP-1718: modify by fuyongsheng 20220323 start
        // 券面总额
        map.put("secAmount", ArithmeticUtil.multiply(dealEntity.getShareAmt() ,new BigDecimal(100)));
        // 本方券券面总额
        map.put("ownSecAmount", ArithmeticUtil.multiply(dealEntity.getShareAmt() ,new BigDecimal(100)));
        // ZGTFCP-1718: modify by fuyongsheng 20220323 end
        // 交易金额
        map.put("cashAmount", dealEntity.getTradeAmt());
        // 下单编号
        map.put("ordId", null);
        // 下单券面总额
        map.put("ordSecAmount", null);
        ptlCashList.add(map);
        resultMap.put("ptlCashList", ptlCashList);

        if (DictCodeListExt3.交易方向_债转股.getValue().equals(ps)) {
            if(CollectionUtils.isNotEmpty(trdFinProductBarters)){
                // 金融产品实物交易表
                TrdFinProductBarter trdFinProductBarter = trdFinProductBarters.get(0);
                // 股票持仓数据集
                List<Map<String, Object>> sharesSecList = new ArrayList<>();
                bean = new PositionRequestBaseBean();
                bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
                bean.setFinprodId(trdFinProductBarter.getFinprodId());
                bean.setSecAcctId(dealEntity.getSecManageAcctId());
                bean.setTradeId(dealEntity.getTradeId());
                bean.setEtrId(trdProductDealAdd.getEntrustId());
                bean.setSecAmount(trdFinProductBarter.getShareAmt());
                sharesSecList.add(BeanUtil.object2Map(bean));
                resultMap.put(SHARES_SEC_LIST, sharesSecList);
            }
        }

        // 可转债数据集
        List<Map<String, Object>> bondSecList = new ArrayList<>();
        bean = new PositionRequestBaseBean();
        bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
        bean.setFinprodId(dealEntity.getFinprodId());
        bean.setSecAcctId(dealEntity.getSecManageAcctId());
        bean.setTradeId(dealEntity.getTradeId());
        bean.setEtrId(trdProductDealAdd.getEntrustId());
        bean.setSecAmount(ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
        Map<String, Object> bondSecMap = BeanUtil.object2Map(bean);
        //融入券券面总额
        bondSecMap.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
        //本方券券面总额
        bondSecMap.put(OWN_SEC_AMOUNT, ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
        bondSecList.add(bondSecMap);
        resultMap.put("bondSecList", bondSecList);
        if (DictCodeListExt3.交易方向_配债.getValue().equals(ps)
                || DictCodeList.交易方向_申购.getValue().equals(ps)) {
            // 配债申购数据集
            List<Map<String, Object>> applyBondSecList = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            // 配股/申购代码
            bean.setFinprodId(getStockId(dealEntity.getFinprodId(), dealEntity.getFinprodType2(), dealEntity.getPs()));
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            bean.setSecAmount(ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
            Map<String, Object> applyBondSecMap = BeanUtil.object2Map(bean);
            applyBondSecMap.put(OWN_SEC_AMOUNT, ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
            applyBondSecMap.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
            applyBondSecList.add(applyBondSecMap);
            resultMap.put("applyBondSecList", applyBondSecList);
        }
    }

    /**
     * 基金-指令    fund001
     *
     * @param entity
     * @param resultMap
     */
    private void fund002(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_FUND002);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_FUND002));
        // 业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        // 当日回转
        resultMap.put(SAME_DAY_TURN, 是否_否.getValue());
        // ETF申赎方式
        resultMap.put(ETFREDEMPTION_METHOD, null);

        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        List<StlPayOrdersDto> stlPayOrdersList = entity.getStlPayOrdersList();

        // 组合数据集
        List<Map<String, Object>> ptlList = new ArrayList<>();
        PositionRequestBaseBean bean = new PositionRequestBaseBean();
        bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
        // 业务编号
        bean.setTradeId(dealEntity.getTradeId());
        // 指令编号
        bean.setEtrId(trdProductDealAdd.getEntrustId());
        bean.setFinprodId(dealEntity.getFinprodId());
        // 证券管理账户
        bean.setSecAcctId(dealEntity.getSecManageAcctId());
        // 资金账户
        bean.setCashAcctId(exchangeTransMatchCommonMapper.queryAccountNo(bean.getSecAcctId()));
        bean.setSecAmount(dealEntity.getShareAmt());
        bean.setCashAmount(dealEntity.getTradeAmt());
        // "IF 指令.指令单位 = 手 THEN 指令.指令数量*10000
        //IF 指令.指令单位 = 份 THEN 指令.指令数量"
        Map<String, Object> map = BeanUtil.object2Map(bean);
        // 下单委托编号	ordEntrustId
        map.put(ORD_ENTRUST_ID, null);
        // 指令份额	etrSecAmount
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        if(etrEntrust != null){
            map.put("etrSecAmount", etrEntrust.getEntrustNum());
        }
        // 下单编号	ordId
        map.put("ordId", null);
        // 下单份额	ordSecAmount
        map.put("ordSecAmount", null);
        ptlList.add(map);
        resultMap.put("ptlList", ptlList);
    }

    /**
     * 信用保护凭证交易-指令
     */
    private void credit001(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        TrdProductDeal trdProductDeal = entity.getTrdProductDeal();
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_CREDIT001);
        resultMap.put(BUSI_ACT, getBusiAct(trdProductDeal));
        resultMap.put(PS, trdProductDeal.getPs());
        resultMap.put(TRADE_PLACE, trdProductDeal.getTradeMarket());
        resultMap.put(WORK_FLOW_ID, trdProductDeal.getWorkflowId());
        resultMap.put("ceSettleType", null);
        resultMap.put("cdate", DateUtil.format(trdProductDeal.getTradeDate()));
        resultMap.put(CCY, trdProductDeal.getCcy());
        resultMap.put("bTotalTradeId", null);

        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();

        List<Map<String, Object>> ptlList = new ArrayList<>();
        Map<String, Object> pltMap = new HashMap<>();
        pltMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
        pltMap.put(FINPROD_ID, trdProductDeal.getFinprodId());
        pltMap.put("tradeId", trdProductDeal.getTradeId());
        pltMap.put("etrId", trdProductDealAdd.getEntrustId());
        // 指令份额	etrSecAmount
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        if(etrEntrust != null){
            pltMap.put("etrSecAmount", etrEntrust.getEntrustNum());
        }
        pltMap.put("cashAcctId", exchangeTransMatchCommonMapper.queryAccountNo(trdProductDeal.getSecManageAcctId()));
        pltMap.put("secAcctId", trdProductDeal.getSecManageAcctId());
        pltMap.put("secAmount", ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        pltMap.put("cashAmount", trdProductDeal.getTradeAmt());
        ptlList.add(pltMap);
        resultMap.put("ptlList", ptlList);
    }

    /**
     * 质押式通用回购
     *
     * @param entity
     * @param resultMap
     */
    private void repo003(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_REPO003);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_REPO003));
        // 业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        // ZGTFCP-1042: add by fuyongsheng 20220315 start
        // 回购首期特殊处理,需传正回购/逆回购
        if(DictCodeList.交易方向_买入.getValue().equals(entity.getTrdProductDeal().getPs())){
            if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getTrdProductDeal().getFinprodType2())){
                resultMap.put("ps", DictCodeListExt3.交易方向_正回购.getValue());
            }else if(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getTrdProductDeal().getFinprodType2())){
                resultMap.put("ps", DictCodeListExt3.交易方向_逆回购.getValue());
            }
        }
        // ZGTFCP-1042: add by fuyongsheng 20220315 end
        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getTrdProductDealAdd().getEntrustId());
        String ps = dealEntity.getPs();
        String cashAcctId = exchangeTransMatchCommonMapper.queryAccountNo(dealEntity.getSecManageAcctId());
        PositionRequestBaseBean bean;
        // 组合数据集
        List<Map<String, Object>> ptlList = new ArrayList<>();
        bean = new PositionRequestBaseBean();
        bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
        bean.setFinprodId(dealEntity.getFinprodId());
        // 证券管理账户
        bean.setSecAcctId(dealEntity.getSecManageAcctId());
        // 资金账户
        bean.setCashAcctId(cashAcctId);
        // ZGTFCP-1445:划款指令为空时,查询资金账户 modify by fuyongsheng 20220321 end
        bean.setTradeId(dealEntity.getTradeId());
        bean.setEtrId(trdProductDealAdd.getEntrustId());
        bean.setCashAmount(dealEntity.getTradeAmt());

        Map<String, Object> map = BeanUtil.object2Map(bean);
        //下单委托编号
        map.put(ORD_ENTRUST_ID, null);
        // 标准券代码
        map.put(STANDARD_SEC_ID, STANDAR_SEC_ID);
        //查询当日折算率
        BigDecimal converseRadio = exchangeTransMatchCommonMapper.queryConverseRadio(dealEntity.getFinprodId(), dealEntity.getTradeDate());
        if(converseRadio==null) {
            converseRadio = BigDecimal.ONE;
        }
        // 质押式回购出入库计算时，标准券数量=质押数量*标准折算率*面值/100
        BigDecimal 百元券面 = trdCommonBaseService.getFaceValue(dealEntity.getFinprodId(), dealEntity.getTradeDate());
        if (百元券面 == null) {
            百元券面 = BigDecimal.valueOf(100);
        }
        BigDecimal hundred = BigDecimal.valueOf(100);
        // 标准券总额
        map.put(STANDARD_SEC_AMOUNT, ArithmeticUtil.divide(ArithmeticUtil.multiply(dealEntity.getShareAmt(),
                converseRadio, 百元券面), hundred, 2, RoundingMode.HALF_UP));
        // 再折算率	reConversionRate "调用【再折算率查询】参数：资金账户 = 指令资金账户
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("acctNo", cashAcctId);
        TrdMoreConvrateDto moreConvrate = this.getMoreConvrate(paramMap);
        if (StringUtil.isNotEmpty(moreConvrate)) {
            map.put("reConversionRate", moreConvrate.getMoreConvrate());
        }
        if (DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps)) {
            map.put("cashAmount", new BigDecimal(0));
        }
        // 指令券面总额
        map.put("etrSecAmount", ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), hundred));
        ptlList.add(map);
        resultMap.put(PTL_LIST, ptlList);
        // 质押券数据集	ptlSecList
        if (DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps)) {
            List<Map<String, Object>> ptlSecList = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setFinprodId(dealEntity.getFinprodId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(entity.getEntrustId());
            bean.setSecAmount(ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
            Map<String, Object> ptlSecMap = BeanUtil.object2Map(bean);
            //融入券券面总额
            ptlSecMap.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
            //本方券券面总额 指令对应组合.指令数量*100
            ptlSecMap.put(OWN_SEC_AMOUNT, ArithmeticUtil.multiply(dealEntity.getShareAmt(), new BigDecimal(100)));
            ptlSecList.add(ptlSecMap);
            resultMap.put(PTL_SEC_LIST, ptlSecList);
        }
    }

    /**
     * 场内指令-查询质押券再折算率
     * @param pageParam
     * @return
     */
    public TrdMoreConvrateDto getMoreConvrate(Map<String, Object> pageParam) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("acctNo", (String) pageParam.get("acctNo"));
        Map<String, Object> res = restTemplate.postForObject(
                ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMoreConvrate", param, Map.class);
        if (res != null && res.get("data") != null) {
            List<TrdMoreConvrateDto> list = (List<TrdMoreConvrateDto>) res.get("data");
            if (list != null && list.size() > 0) {
                return JSON.parseObject(JSON.toJSONString(list.get(0)), TrdMoreConvrateDto.class);
            }
        }
        return null;
    }

    /**
     * 质押式协议回购-指令
     *
     * @param entity
     * @param resultMap
     */
    private void repo002(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_REPO002);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_REPO002));
        // 业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        // 清算速度
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        // 交易平台
//        resultMap.put("tradePlace", null);
        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        TrdFinProductDealTotal trdFinProductDealTotal = entity.getTrdFinProductDealTotal();
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        String ps = etrEntrust.getPs();
         // resultMap.put("ps", dealEntity.getPs());
        // ZGTFCP-1042: add by fuyongsheng 20220315 start
        // 协议回购特殊处理,需传正回购/逆回购
        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(etrEntrust.getFinprodType2())){
            resultMap.put("ps", DictCodeListExt3.交易方向_正回购.getValue());
        } else {
            resultMap.put("ps", DictCodeListExt3.交易方向_逆回购.getValue());
        }
        // ZGTFCP-1042: add by fuyongsheng 20220315 end
        List<FinPledgeDisplaceResult> finPledgeDisplaceResultList = new ArrayList<>();
        if(DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)){
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("tradeId", dealEntity.getTotleTradeId());
            queryMap.put("branch", 0);
            finPledgeDisplaceResultList = exchangeTransMatchCommonMapper.getFinPledgeDisplaceResult(queryMap);
        }
        // ZGTFCP-1848:换入换出的交易头寸都在换入的交易进行处理,换出不处理新头寸 modify by fuyongsheng 20220325 start
        if(DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)){
            resultMap.put("totalTradeId", dealEntity.getTotleTradeId());
            if(DictCodeListExt3.质押券置换_置换方向_换出.getValue().equals(dealEntity.getPs())){
                resultMap.put(INTERFACE_TYPE, null);
                return;
            }
        }
        // ZGTFCP-1848:换入换出的交易头寸都在换入的交易进行处理,换出不处理新头寸 modify by fuyongsheng 20220325 end
        PositionRequestBaseBean bean;
        // 首期组合现金数据集
        if (DictCodeListExt3.交易方向_正回购.getValue().equals(ps)
                || DictCodeListExt3.交易方向_逆回购.getValue().equals(ps)
                || DictCodeListExt3.交易方向_首期.getValue().equals(ps)
                || DictCodeListExt3.交易方向_提前购回.getValue().equals(ps)
                || (DictCodeListExt3.交易方向_到期购回.getValue().equals(ps))
                || (DictCodeListExt3.交易方向_解除质押.getValue().equals(ps))) {
            List<Map<String, Object>> list = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            if(DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)){
                bean.setCashAcctId(this.getPtlCapAcctNo(entity));
                FinTradeProduct finTradeProduct = exchangeTransMatchCommonMapper.getFinTradeProduct(trdFinProductDealTotal.getFinprodId());
                if(finTradeProduct != null){
                    bean.setCashAmount(finTradeProduct.getmTradeAmt());
                }
            }else{
                bean.setCashAcctId(exchangeTransMatchCommonMapper.queryAccountNo(bean.getSecAcctId()));
                bean.setCashAmount(dealEntity.getTradeAmt());
            }
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            list.add(BeanUtil.object2Map(bean));
            resultMap.put("ptlCashList", list);
        }
        // 轧差组合现金数据集
        if (DictCodeListExt3.交易方向_到期续做.getValue().equals(ps)) {
            List<Map<String, Object>> list = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setCashAcctId(exchangeTransMatchCommonMapper.queryAccountNo(bean.getSecAcctId()));
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            // ZGTFCP-1672:到期续作金额赋值 modify by fuyongsheng 20220323 start
            if(DictCodeList.交易方向_买入.getValue().equals(dealEntity.getPs())){
                bean.setCashAmount(dealEntity.getTradeAmt());
            }else{ // 收息到账、还本到账
                bean.setCashAmount(dealEntity.getTradeAmt().negate());
            }
            // ZGTFCP-1672:到期续作金额赋值 modify by fuyongsheng 20220323 end
            list.add(BeanUtil.object2Map(bean));
            resultMap.put("ptlOffsetCashList", list);
        }
        // 质押券数据集	ptlSecList
        // if (DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)) {
        if ((DictCodeListExt3.交易方向_正回购.getValue().equals(ps)
                || DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)
                || DictCodeListExt3.交易方向_解除质押.getValue().equals(ps))) {
            List<Map<String, Object>> list = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            if(DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)){
                bean.setTradeId(dealEntity.getTotleTradeId());
            }else{
                bean.setTradeId(dealEntity.getTradeId());
            }
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            if(DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)){
                // ZGTFCP-1805:修改中途换券字段赋值 modify by fuyongsheng 20220324 start
                FinPledgeDisplaceResult temp = this.getFinPledgeDisplaceResult(entity, DictCodeListExt3.质押券置换_置换结果_置换后.getValue());
                if(null != temp){
                    bean.setFinprodId(temp.getFinprodId());
                    bean.setSecAmount(temp.getPledgeAmt());
                    bean.setOwnSecAmount(temp.getPledgeAmt());
                }
                // ZGTFCP-1805:修改中途换券字段赋值 modify by fuyongsheng 20220324 end
            }
            else if(DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)){
                bean.setFinprodId(CollectionUtils.isNotEmpty(finPledgeDisplaceResultList) ? finPledgeDisplaceResultList.get(0).getFinprodId() : StringUtil.EMPTY);
                bean.setSecAmount(finPledgeDisplaceResultList.get(0).getPledgeAmt());
                bean.setOwnSecAmount(finPledgeDisplaceResultList.get(0).getPledgeAmt());
            }
            else if(DictCodeListExt3.交易方向_正回购.getValue().equals(ps)){
                List<FinDealPledge> finDealPledges = exchangeTransMatchCommonMapper.getFinDealPledge(dealEntity.getTradeId());
                if(CollectionUtils.isNotEmpty(finDealPledges)){
                    bean.setFinprodId(finDealPledges.get(0).getPledgeFinprodId());
                    bean.setSecAmount(ArithmeticUtil.multiply(finDealPledges.get(0).getPledgeAmt(), new BigDecimal(100)));
                    bean.setOwnSecAmount(ArithmeticUtil.multiply(finDealPledges.get(0).getPledgeNumber(), new BigDecimal(100)));
                }
            }
            Map<String, Object> map = BeanUtil.object2Map(bean);
            //融入券券面总额
            map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
            list.add(map);
            resultMap.put("ptlSecList", list);
        }
        // 解除质押券数据集	ptlRelieveSecList
        if ((DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)
                // ZGTFCP-1753:到期续做不传 modify by fuyongsheng 20220324 start
                // || DictCodeListExt3.交易方向_到期续做.getValue().equals(ps)
                // ZGTFCP-1753:到期续做不传 modify by fuyongsheng 20220324 end
                || DictCodeListExt3.交易方向_提前购回.getValue().equals(ps)
                || DictCodeListExt3.交易方向_到期购回.getValue().equals(ps)
                || DictCodeListExt3.交易方向_中途换券.getValue().equals(ps))) {
            List<Map<String, Object>> list = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            if(DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)){
                bean.setTradeId(dealEntity.getTotleTradeId());
            }else{
                bean.setTradeId(dealEntity.getTradeId());
            }
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            // ZGTFCP-1672:修改部分字段赋值 modify by fuyongsheng 20220323 start
            if(DictCodeListExt3.交易方向_解除质押.getValue().equals(ps)){
                bean.setFinprodId(CollectionUtils.isNotEmpty(finPledgeDisplaceResultList) ? finPledgeDisplaceResultList.get(0).getFinprodId() : StringUtil.EMPTY);
                bean.setSecAmount(ArithmeticUtil.multiply(finPledgeDisplaceResultList.get(0).getPledgeAmt(), new BigDecimal(100)));
                bean.setOwnSecAmount(ArithmeticUtil.multiply(finPledgeDisplaceResultList.get(0).getPledgeAmt(), new BigDecimal(100)));
            }else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(ps)){
                FinPledgeDisplaceResult temp = this.getFinPledgeDisplaceResult(entity, DictCodeListExt3.质押券置换_置换结果_置换前.getValue());
                if(null != temp){
                    bean.setFinprodId(temp.getFinprodId());
                    bean.setSecAmount(temp.getPledgeAmt());
                    bean.setOwnSecAmount(temp.getPledgeAmt());
                }
            }else{
                // 获取首期质押券信息
                FinDealPledge finDealPledge = getSqFinDealPledge(trdProductDealAdd);
                if(null != finDealPledge){
                    bean.setFinprodId(finDealPledge.getPledgeFinprodId());
                    // ZGTFCP-1753:还本到账、收息到账分别处理 modify by fuyongsheng 20220324 start
                    if(DictCodeList.交易方向_收息到账.getValue().equals(dealEntity.getPs())){
                        bean.setSecAmount(new BigDecimal(0));
                        bean.setOwnSecAmount(new BigDecimal(0));
                    }else{
                        bean.setSecAmount(ArithmeticUtil.multiply(finDealPledge.getPledgeNumber(),new BigDecimal(100)));
                        bean.setOwnSecAmount(ArithmeticUtil.multiply(finDealPledge.getPledgeNumber(),new BigDecimal(100)));
                    }
                    // ZGTFCP-1753:还本到账、收息到账分别处理 modify by fuyongsheng 20220324 end
                }
            }
            // ZGTFCP-1672:修改部分字段赋值 modify by fuyongsheng 20220323 end
            Map<String, Object> map = BeanUtil.object2Map(bean);
            //融入券券面总额
            map.put(INTEGRATE_SEC_AMOUNT, BigDecimal.ZERO);
            if(DictCodeList.交易方向_收息到账.getValue().equals(dealEntity.getPs())){
                map.put("secAmount", new BigDecimal(0));
                map.put("ownSecAmount", new BigDecimal(0));
            }
            list.add(map);
            resultMap.put("ptlRelieveSecList", list);
        }
    }

    /**
     * 股票-指令
     *
     * @param entity
     * @param resultMap
     */
    private void stock001(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        // 业务动作
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_STOCK001);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, PositionConstant.INTERF_TYPE_STOCK001));

        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();

        String ps = dealEntity.getPs();
        String cashAcctId = exchangeTransMatchCommonMapper.queryAccountNo(dealEntity.getSecManageAcctId());
        // ZGTFCP-1615:申购、申购确认业务动作特殊处理 modify by fuyongsheng 20220322 start
        if (DictCodeList.交易方向_申购.getValue().equals(dealEntity.getPs())) {
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_中签.getValue());
        }else if(DictCodeList.交易方向_申购确认.getValue().equals(dealEntity.getPs())){
            resultMap.put(BUSI_TYPE, DictCodeListExt3.头寸业务类型_网上申购.getValue());
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_缴款.getValue());
        }else{
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        }
        // ZGTFCP-1615:申购、申购确认业务动作特殊处理 modify by fuyongsheng 20220322 end

        // 清算速度
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);

        PositionRequestBaseBean bean;
        // 组合数据集
        List<Map<String, Object>> ptlList = new ArrayList<>();
        bean = new PositionRequestBaseBean();
        bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
        bean.setFinprodId(dealEntity.getFinprodId());
        bean.setTradeId(dealEntity.getTradeId());
        bean.setEtrId(trdProductDealAdd.getEntrustId());
        bean.setSecAcctId(dealEntity.getSecManageAcctId());
        bean.setSecAmount(dealEntity.getShareAmt());
        bean.setCashAcctId(cashAcctId);
        bean.setCashAmount(dealEntity.getTradeAmt());
        Map<String, Object> map = BeanUtil.object2Map(bean);
        // 指令份额	etrSecAmount
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        if(etrEntrust != null){
            map.put("etrSecAmount", etrEntrust.getEntrustNum());
        }
        // ZGTFCP-1615:增加本方券/融入券赋值 modify by fuyongsheng 20220322 start
        map.put("ownSecAmount", dealEntity.getShareAmt());
        map.put("integrateSecAmount", new BigDecimal(0));
        // ZGTFCP-1615:增加本方券/融入券赋值 modify by fuyongsheng 20220322 end
        map.put("ordId", null);
        map.put("ordSecAmount", null);
        map.put(ORD_ENTRUST_ID,null);
        ptlList.add(map);
        resultMap.put(PTL_LIST, ptlList);

        // 组合配股/新股申购数据集	ptlRoList
        if (DictCodeListExt2.交易方向_配股.getValue().equals(ps) || DictCodeList.交易方向_申购.getValue().equals(ps)) {
            List<Map<String, Object>> ptlRoList = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(trdProductDealAdd.getEntrustId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setSecAmount(dealEntity.getShareAmt());
            bean.setCashAcctId(cashAcctId);
            // 配股/申购代码
            bean.setFinprodId(getStockId(dealEntity.getFinprodId(), dealEntity.getFinprodType2(), dealEntity.getPs()));
            bean.setCashAmount(dealEntity.getTradeAmt());
            Map<String, Object> ptlRoMap = BeanUtil.object2Map(bean);
            if(etrEntrust != null){
                ptlRoMap.put("etrSecAmount", etrEntrust.getEntrustNum());
            }
            ptlRoMap.put("ordId", null);
            ptlRoMap.put("ordSecAmount", null);
            // ZGTFCP-1615:增加本方券/融入券赋值 modify by fuyongsheng 20220322 start
            ptlRoMap.put("ownSecAmount", dealEntity.getShareAmt());
            ptlRoMap.put("integrateSecAmount", new BigDecimal(0));
            // ZGTFCP-1615:增加本方券/融入券赋值 modify by fuyongsheng 20220322 end
            ptlRoMap.put(ORD_ENTRUST_ID,null);
            ptlRoList.add(ptlRoMap);
            resultMap.put(PTL_RO_LIST, ptlRoList);
        }
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put("userId", SecurityUtil.getLoginUser() != null ? SecurityUtil.getLoginUser().getUserName() : "system");
        // resultMap.put("totalTradeId", entity.getEntrustId());
        resultMap.put("totalTradeId", entity.getTrdProductDeal().getTradeId());
        resultMap.put("balanceCheck", "N");
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());

        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entity.getTrdProductDealAdd().getEntrustId());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entity.getTrdProductDealAdd().getEntrustId());
        //设置头寸业务类型
        String busiType = commonPositionEngineService.getPositionBusiType(etrEntrust.getFinprodType2(), etrEntrust.getPs(),
                etrEntrustAdd.getAssetType1());
        resultMap.put("busiType", busiType);

        // 交易方向
        resultMap.put(PS, entity.getTrdProductDeal().getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put(TRADE_PLACE, entity.getTrdProductDeal().getTradeMarket());
        // 流程编号	指令的工作流ID
        resultMap.put(WORK_FLOW_ID, entity.getTrdProductDeal().getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(entity.getTrdProductDeal().getTradeDate()));
        // 币种
        if(StringUtil.isNotEmpty(entity.getTrdProductDeal().getCcy())) {
            resultMap.put(CCY, entity.getTrdProductDeal().getCcy());
        }else {
            resultMap.put(CCY, DictCodeList.币种_人民币_CNY.getValue());
        }
        // 前次交易编号
        resultMap.put(B_TOTAL_TRADE_ID, null);
    }

    /**
     * 获取交易平台
     * @param entity
     * @param interfaceType
     * @return
     */
    private String getSharesTradePlatform(BaseTradeProductDealDto entity, String interfaceType){
        return commonPositionEngineService.getExchangeTradePlatform(entity.getTrdProductDealAdd().getEntrustId(),
                entity.getTrdProductDeal().getTradePlat(), entity.getTrdProductDeal().getPs(),
                entity.getTrdProductDeal().getFinprodId(), interfaceType);
    }

    /**
     * 查询首期质押券信息
     * @param trdProductDealAdd
     * @return
     */
    private FinDealPledge getSqFinDealPledge(TrdProductDealAdd trdProductDealAdd){
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(trdProductDealAdd.getEntrustId());
        String hgsqFinprodId = trdCommonBaseService.selectHgsqFinprodId(trdProductDealAdd.getPortfolioId(), etrEntrustAdd.getTradeId());
        if (StringUtil.isNotBlank(hgsqFinprodId)) {
            // 质押券信息
            FinDealPledgeKey finDealPledgeKey = new FinDealPledgeKey();
            finDealPledgeKey.setFinprodId(hgsqFinprodId);
            finDealPledgeKey.setBranch(0L);
            return exchangeTransMatchCommonMapper.selectFinDealPledgeByPrimaryKey(finDealPledgeKey);
        }
        return null;
    }

    private FinPledgeDisplaceResult getFinPledgeDisplaceResult(BaseTradeProductDealDto entity, String resultType){
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("tradeId", entity.getTrdFinProductDealTotal().getTotleTradeId());
        paraMap.put("branch", 0);
        List<FinPledgeDisplaceResult> resultList = exchangeTransMatchCommonMapper.getFinPledgeDisplaceResult(paraMap);
        if(CollectionUtils.isNotEmpty(resultList)){
            for(FinPledgeDisplaceResult temp : resultList){
                if(resultType.equals(temp.getResultType())){
                    return temp;
                }
            }
        }
        return null;
    }

    /**
     * 查询产品托管户
     * @param entity
     * @return
     */
    private String getPtlCapAcctNo(BaseTradeProductDealDto entity){
        Map<String, Object> map = new HashMap<>();
        map.put("portfolioId", entity.getTrdProductDealAdd().getPortfolioId());
        map.put("ccy", entity.getTrdProductDeal().getCcy());
        map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        PtlCapacctInfo ptlCapacctInfo = trdCommonBaseService.getPtlCapAcctInfo(map);
        return ptlCapacctInfo != null ? ptlCapacctInfo.getAcctNo() : StringUtil.EMPTY;
    }

    /**
     * 获取申报代码
     * @param finprodId
     * @param ps
     * @return
     */
    private String getStockId(String finprodId, String finprodType2, String ps){
        // 查询申报信息
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        if(DictCodeList.交易方向_申购.getValue().equals(ps) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)){
            map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_可转债信用申购代码.getValue());
        }
        else if(DictCodeList.交易方向_申购.getValue().equals(ps) && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)){
            map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
        }
        else if(DictCodeListExt3.交易方向_配债.getValue().equals(ps)){
            map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
        }else if(DictCodeListExt2.交易方向_配股.getValue().equals(ps)){
            map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
        }
        FinProductOthCode finProductOthCode = commonPositionService.selectFinProductOthCode(map);
        return finProductOthCode != null ? finProductOthCode.getOthCode() : StringUtil.EMPTY;
    }
}
