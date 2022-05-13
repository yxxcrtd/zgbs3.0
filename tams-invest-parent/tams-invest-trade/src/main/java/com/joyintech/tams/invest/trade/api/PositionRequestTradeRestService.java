package com.joyintech.tams.invest.trade.api;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.dto.PortfolioCalculateDto;
import com.joyintech.tams.invest.common.trade.dto.PortfolioCalculateDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.bean.PositionRequestBaseBean;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.constant.PositionConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.joyintech.tams.constant.DictCodeList.是否_否;

import java.util.*;

import static com.joyintech.tams.invest.common.position.constant.PositionCommonConstant.*;

@Service
public class PositionRequestTradeRestService {
    private static final Logger logger = LoggerFactory.getLogger(PositionRequestTradeRestService.class);

    @Autowired
    private CommonPositionEngineService commonPositionEngineService;

    @Autowired
    private CommonPositionService commonPositionService;

    @Autowired
    private BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    public static final String TRADE_PLAT = "TRANS_MATCH";

    public static final String STANDAR_SEC_ID = "XN_PRODID999";

    // 结算数据下行上交所交易日历ID
    public static final String STL_DOWNDATA_DEAL_SHJYS_CALID = "STL_DOWNDATA_DEAL_SHJYS_CALID";
    // 结算数据下行深交所交易日历ID
    public static final String STL_DOWNDATA_DEAL_SZJYS_CALID = "STL_DOWNDATA_DEAL_SZJYS_CALID";

    /**
     * 发送交易Rest头寸请求
     * @param finProductTradeDto
     */
    public void sendTradeRestPositionRequest(FinProductTradeDto finProductTradeDto) {
        logger.debug("【发送交易Rest头寸请求】entity: {}", JSON.toJSONString(finProductTradeDto));
        if(CollectionUtils.isEmpty(finProductTradeDto.getBaseTradeProductDealDtos())){
            return;
        }
        if(DictCodeListExt1.交易流程状态_撤销.getValue().equals(finProductTradeDto.getWorkflowStatus())){
            this.revoke(finProductTradeDto);
        }else if(DictCodeListExt1.交易流程状态_有效.getValue().equals(finProductTradeDto.getWorkflowStatus())){
            for (BaseTradeProductDealDto entity:finProductTradeDto.getBaseTradeProductDealDtos()) {
                this.sendPositionHttpRequest(entity);
            }
        }

    }

    /**
     * 撤销新头寸接口
     * @param finProductTradeDto
     */
    private void revoke(FinProductTradeDto finProductTradeDto){
        for (BaseTradeProductDealDto entity:finProductTradeDto.getBaseTradeProductDealDtos()) {
            TrdProductDeal deal = entity.getTrdProductDeal();
            TrdProductDealAdd dealAdd = entity.getTrdProductDealAdd();
            TrdFinProductDealTotal dealTotal = entity.getTrdFinProductDealTotal();
            // 资产二级类型
            String assetType2 = StringUtil.EMPTY;
            String ps = StringUtil.EMPTY;
            String finprodType2 = StringUtil.EMPTY;
            String busiType = StringUtil.EMPTY;
            if(StringUtil.isNotEmpty(dealAdd.getEntrustId())){
                EtrEntrust entrust = etrEntrustMapper.selectByPrimaryKey(dealAdd.getEntrustId());
                EtrEntrustAdd entrustAdd = etrEntrustAddMapper.selectByPrimaryKey(dealAdd.getEntrustId());
                assetType2 = entrustAdd.getAssetType1();
                ps = entrust.getPs();
                finprodType2 = entrust.getFinprodType2();
                busiType = commonPositionEngineService.getPositionBusiType(finprodType2, ps, assetType2);
            }else{
                assetType2 = StringUtil.isNotEmpty(finProductTradeDto.getAssetType2()) ?
                        finProductTradeDto.getAssetType2() : baseAssetCashPositionCommMapper.selectFundType(deal.getFinprodId());
                ps = deal.getPs();
                finprodType2 = deal.getFinprodType2();
                busiType = commonPositionEngineService.getDurationPositionBusiType(finprodType2, ps, assetType2);
            }
            // ZGTFCP-1577: add by fuyongsheng 20220315 end
            String tradeId= deal.getTradeId();
            // ZGTFCP-1883:中途换券成交匹配发送的tradeId是totleTradeId,撤销时保持一致 modify by fuyongsheng 20220327 start
            if(dealTotal != null &&  DictCodeListExt3.交易方向_中途换券.getValue().equals(dealTotal.getPs())){
                tradeId = dealTotal.getTotleTradeId();
                // 中途换券有两个交易:换入和换出,传totalTradeId,一次就把换入和换出都处理了,故二者只需处理一次,这里只处理换入
                if(DictCodeListExt3.质押券置换_置换方向_换出.getValue().equals(deal.getPs())){
                    continue;
                }
            }
            // ZGTFCP-1883:中途换券成交匹配发送的tradeId是totleTradeId,撤销时保持一致 modify by fuyongsheng 20220327 end
            PositionHttpRequest.dorevoke(tradeId, PositionConstant.POSITION_TYPE_TRD, busiType, ps);
        }
    }


    /**
     * BaseTradeProductDealDto请求对象使用
     * @param entity
     */
    public void sendPositionHttpRequest(BaseTradeProductDealDto entity) {
        logger.debug("【发送存续期头寸请求】entity: {}", JSON.toJSONString(entity));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        String repoType = baseAssetCashPositionCommMapper.selectFundType(entity.getTrdProductDeal().getFinprodId());
        entity.setAssetType2(repoType);
        this.setCommonField(entity, resultMap);
        String finprodType = entity.getTrdProductDeal().getFinprodType2();
        String type6 = commonPositionEngineService.getType6(entity.getTrdProductDeal().getFinprodId());
        if (DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue().equals(type6)
                && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType)) {
            finprodType += "-" + type6;
        }
        if (DictCodeList.回购类型_质押式协议回购.getValue().equals(repoType)
                || (DictCodeList.回购类型_质押式竞价回购.getValue().equals(repoType))) {
            finprodType += "-" + repoType;
        }
        logger.debug("【存续期查询头寸接口参数】finprodType:{},ps:{}", finprodType, entity.getTrdProductDeal().getPs());
        String interType = commonPositionEngineService.getPositionInterType(PositionConstant.POSITION_TYPE_TRD, TRADE_PLAT,
                finprodType, entity.getTrdProductDeal().getPs(), DictCodeListExt3.业务动作_成交匹配.getValue(), entity.getClass().getName());
        logger.debug("【存续期查询头寸接口结果】interType:{}", interType);
        if (PositionConstant.INTERF_TYPE_FUND002.equals(interType)) {
            this.fund002(entity, resultMap);
        } else if (PositionConstant.INTERF_TYPE_STOCK001.equals(interType)) {
            this.stock001(entity, resultMap);
        }else if (PositionConstant.INTERF_TYPE_REPO003.equals(interType)) {
            this.repo003(entity, resultMap);
        }else if (PositionConstant.INTERF_TYPE_REPO002.equals(interType)) {
            this.repo002(entity, resultMap);
        }else if (PositionConstant.INTERF_TYPE_BOND002.equals(interType)) {
            this.bond002(entity, resultMap);
        }else if (PositionConstant.INTERF_TYPE_BOND003.equals(interType)) {
            this.bond003(entity, resultMap);
        }
        if(StringUtil.isNotEmpty(resultMap.get(INTERFACE_TYPE))){
            PositionHttpRequest.send(resultMap);
        }else{
            logger.debug("[存续期未找到对应的接口配置]");
            // 债券的可转债申购上市流通、股票的红利再投上市流通需要调用上市流通专用接口
            if(DictCodeList.交易方向_上市流通.getValue().equals(entity.getTrdProductDeal().getPs())){
                this.sendSsltPositionHttpRequest(entity);
            }
        }
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
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_REPO002));

        // 业务动作
        resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        // 清算速度
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getTrdProductDeal().getFinprodType2())){
            resultMap.put("ps", DictCodeListExt3.交易方向_正回购.getValue());
        } else {
            resultMap.put("ps", DictCodeListExt3.交易方向_逆回购.getValue());
        }
        PositionRequestBaseBean bean;
        // 首期组合现金数据集
        List<Map<String, Object>> list = new ArrayList<>();
        bean = new PositionRequestBaseBean();
        bean.setPortfolioId(trdProductDealAdd.getPortfolioId());
        bean.setSecAcctId(dealEntity.getSecManageAcctId());
        bean.setCashAcctId(baseAssetCashPositionCommMapper.queryAccountNo(bean.getSecAcctId()));
        bean.setTradeId(dealEntity.getTradeId());
        bean.setEtrId(trdProductDealAdd.getEntrustId());
        bean.setCashAmount(dealEntity.getTradeAmt());
        list.add(BeanUtil.object2Map(bean));
        resultMap.put("ptlCashList", list);
    }


    /**
     * 基金-指令
     *
     * @param entity
     * @param resultMap
     */
    private void fund002(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_FUND002);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_FUND002));
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
        TrdProductDealAdd dealAddEntity = entity.getTrdProductDealAdd();
        String accountNo = baseAssetCashPositionCommMapper.queryAccountNo(dealEntity.getSecManageAcctId());
        if (StringUtil.isNotEmpty(dealEntity)) {
            PositionRequestBaseBean bean = new PositionRequestBaseBean();
            List<Map<String, Object>> ptlList = new ArrayList<>();
            bean.setPortfolioId(dealAddEntity.getPortfolioId());
            // 业务编号
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(entity.getEntrustId());
            bean.setFinprodId(dealEntity.getFinprodId());
            bean.setCashAcctId(accountNo);
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setSecAmount(dealEntity.getShareAmt());
            bean.setCashAmount(dealEntity.getTradeAmt());
            Map<String, Object> map = BeanUtil.object2Map(bean);
            map.put(ORD_ENTRUST_ID,null);
            // 指令份额	etrSecAmount
            map.put("etrSecAmount",null);
            // 下单编号	ordId
            map.put("ordId",null);
            // 下单份额	ordSecAmount
            map.put("ordSecAmount",null);
            ptlList.add(map);
            resultMap.put("ptlList", ptlList);
            // 组合附属数据集
            List<Map<String, Object>> ptlSubList = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(dealAddEntity.getPortfolioId());
            bean.setFinprodId(dealEntity.getFinprodId());
            // 业务编号
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(entity.getEntrustId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setSecAmount(dealEntity.getShareAmt());
            Map<String, Object> paramMap = BeanUtil.object2Map(bean);
            ptlSubList.add(paramMap);
            resultMap.put("ptlSubList", ptlSubList);
        }
    }

    /**
     * 股票-指令
     *
     * @param entity
     * @param resultMap
     */
    private void stock001(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, PositionConstant.INTERF_TYPE_STOCK001);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_STOCK001));

        TrdProductDeal dealEntity = entity.getTrdProductDeal();
        TrdProductDealAdd dealAddEntity = entity.getTrdProductDealAdd();
        String accountNo = baseAssetCashPositionCommMapper.queryAccountNo(dealEntity.getSecManageAcctId());
        // 查询申购交易对应的指令
        TrdProductDealAdd trdProductDealAdd1 = trdProductDealAddMapper.selectByPrimaryKey(dealEntity.getbTradeId());

        // ZGTFCP-1635:修改申购确认相关字段赋值 modify by fuyongsheng 20220322 start
        // 业务动作
        if(DictCodeList.交易方向_申购确认.getValue().equals(dealEntity.getPs())){
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_缴款.getValue());
            resultMap.put("bTotalTradeId", dealEntity.getbTradeId());
        }
        else if(DictCodeList.交易方向_上市流通.getValue().equals(dealEntity.getPs())){
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_上市.getValue());
        }
        else{
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        }
        // ZGTFCP-1635:修改申购确认相关字段赋值 modify by fuyongsheng 20220322 end
        // 清算速度
        resultMap.put(SETTLE_SPEED, null);
        // 结算方式
        resultMap.put(DELIVERY_TYPE, null);
        if (StringUtil.isNotEmpty(dealEntity)) {
            String ps = dealEntity.getPs();
            // 组合数据集
            List<Map<String, Object>> ptlList = new ArrayList<>();
            PositionRequestBaseBean  bean = new PositionRequestBaseBean();
            bean.setPortfolioId(dealAddEntity.getPortfolioId());
            bean.setFinprodId(dealEntity.getFinprodId());
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(trdProductDealAdd1 != null ? trdProductDealAdd1.getEntrustId() : StringUtil.EMPTY);
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            // ZGTFCP-1667:修改股票送转字段赋值 modify by fuyongsheng 20220323 start
            bean.setSecAmount(dealEntity.getShareAmt());
            bean.setCashAcctId(accountNo);
            bean.setCashAmount(dealEntity.getTradeAmt());
            Map<String, Object> map = BeanUtil.object2Map(bean);
            map.put("ownSecAmount", dealEntity.getShareAmt());
            map.put("integrateSecAmount", new BigDecimal(0));
            map.put("etrSecAmount", null);
            map.put("ordId", null);
            map.put("ordSecAmount", null);
            // ZGTFCP-1667:修改股票送转字段赋值 modify by fuyongsheng 20220323 end
            map.put(ORD_ENTRUST_ID,null);
            ptlList.add(map);
            resultMap.put(PTL_LIST, ptlList);
            // 组合配股/新股申购数据集	ptlRoList
            List<Map<String, Object>> ptlRoList = new ArrayList<>();
            bean = new PositionRequestBaseBean();
            bean.setPortfolioId(dealAddEntity.getPortfolioId());
            bean.setTradeId(dealEntity.getTradeId());
            bean.setEtrId(entity.getEntrustId());
            bean.setSecAcctId(dealEntity.getSecManageAcctId());
            bean.setSecAmount(dealEntity.getShareAmt());
            bean.setCashAcctId(accountNo);
            // 配股/申购代码
            if(DictCodeList.交易方向_上市流通.getValue().equals(dealEntity.getPs())){
                bean.setFinprodId(dealEntity.getRemark());
            }else{
                // ZGTFCP-2190:修改金融产品代码赋值 modify by fuyongsheng 20220330 start
                bean.setFinprodId(commonPositionService.getStockId(dealEntity.getFinprodId(), dealEntity.getFinprodType2(), dealEntity.getPs()));
                // ZGTFCP-2190:修改金融产品代码赋值 modify by fuyongsheng 20220330 end
            }
            bean.setCashAmount(dealEntity.getTradeAmt());
            Map<String, Object> datamap = BeanUtil.object2Map(bean);
            datamap.put("ownSecAmount", dealEntity.getShareAmt());
            datamap.put("integrateSecAmount", new BigDecimal(0));
            datamap.put("etrSecAmount",null);
            datamap.put("ordId",null);
            datamap.put("ordSecAmount",null);
            datamap.put(ORD_ENTRUST_ID,null);
            ptlRoList.add(datamap);
            resultMap.put(PTL_RO_LIST, ptlRoList);
        }
    }

    private FinProductQuto getFinProductQuto(String finprodId, Date tradeDate){
        Map<String, Object> map = new HashMap<>();
        map.put("finprodId", finprodId);
        map.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue());
        map.put("cdate", tradeDate);
        return commonPositionService.selectFinProductQuto(map);
    }

    /**
     * 设置公共字段
     *
     * @param entity    实例对象
     * @param resultMap 封装集合
     */
    private void setCommonField(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put("userId", SecurityUtil.getLoginUser() != null ? SecurityUtil.getLoginUser().getUserName() : "system");
        resultMap.put("totalTradeId", entity.getTrdProductDeal().getTradeId());
        resultMap.put("balanceCheck", "N");
        resultMap.put("flowStatus", DictCodeListExt3.流程节点_审批生效.getValue());
        resultMap.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
        //设置头寸业务类型
        String busiType = commonPositionEngineService.getDurationPositionBusiType(entity.getTrdProductDeal().getFinprodType2(),
                entity.getTrdProductDeal().getPs(), entity.getAssetType2());
        resultMap.put("busiType", busiType);
        // 结算方式	见数据字典：结算方式
        // 交易方向
        resultMap.put(PS, entity.getTrdProductDeal().getPs());
        // 交易场所	见数据字典：交易场所
        resultMap.put(TRADE_PLACE, entity.getTrdProductDeal().getTradeMarket());
        // 流程编号	指令的工作流ID
        resultMap.put(WORK_FLOW_ID, entity.getTrdProductDeal().getWorkflowId());
        // 日期
        resultMap.put("cdate", DateUtil.format(this.getTradeDate(entity.getTrdProductDeal())));
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
     * 质押式通用回购
     *
     * @param entity
     * @param resultMap
     */
    private void repo003(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_REPO003);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_REPO003));

        TrdFinProductDealTotal trdProductDealTotal = entity.getTrdFinProductDealTotal();
        TrdProductDeal trdProductDeal = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        String accountNo = baseAssetCashPositionCommMapper.queryAccountNo(trdProductDealTotal.getSecManageAcctId());
        // 业务类型
        resultMap.put("busiType", DictCodeListExt3.头寸业务类型_通用回购到期.getValue());
        // 业务动作
        resultMap.put("busiAct", DictCodeListExt3.业务动作_成交匹配.getValue());
        if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(trdProductDeal.getFinprodType2())){
            resultMap.put("ps", DictCodeListExt3.交易方向_正回购.getValue());
        }else if(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(trdProductDeal.getFinprodType2())){
            resultMap.put("ps", DictCodeListExt3.交易方向_逆回购.getValue());
        }
        resultMap.put("tradePlace", trdProductDeal.getTradeMarket());
        resultMap.put("settleSpeed", null);
        resultMap.put("sharesTradePlatform", null);
        resultMap.put("deliveryType", null);
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("cdate", trdProductDeal.getTradeDate());
        resultMap.put("ccy", trdProductDeal.getCcy());
        resultMap.put("bTotalTradeId", null);

        List<Map<String, Object>> ptlList = new ArrayList<>(4);

        Map<String, Object> plt = new HashMap<>(8);
        plt.put("portfolioId", trdProductDealAdd.getPortfolioId());
        plt.put("cashAcctId", accountNo);
        plt.put("secAcctId", trdProductDealTotal.getSecManageAcctId());
        plt.put("tradeId", trdProductDeal.getTradeId());
        plt.put("etrId", null);
        plt.put("ordEntrustId", null);
        plt.put("standardSecId", STANDAR_SEC_ID);
        plt.put("standardSecAmount", trdProductDeal.getPrinAmt());
        plt.put("cashAmount", trdProductDeal.getTradeAmt());
        ptlList.add(plt);
        resultMap.put("ptlList", ptlList);
    }

    /**
     * 场内现券
     *
     * @param entity
     * @param resultMap
     */
    private void bond002(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND002);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_BOND002));

        TrdFinProductDealTotal trdProductDealTotal = entity.getTrdFinProductDealTotal();
        TrdProductDeal trdProductDeal = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        String accountNo = baseAssetCashPositionCommMapper.queryAccountNo(trdProductDealTotal.getSecManageAcctId());
        resultMap.put("busiAct", getBusiAct(trdProductDealTotal));
        // ZGTFCP-1608:还本到账/收息到账交易方向转换 modify by fuyongsheng 20220322 start
        if(DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal.getPs())){
            resultMap.put("ps", DictCodeList.交易方向_还本.getValue());
        }else if(DictCodeList.交易方向_收息到账.getValue().equals(trdProductDeal.getPs())){
            resultMap.put("ps", DictCodeList.交易方向_收息.getValue());
        }else{
            resultMap.put("ps", trdProductDeal.getPs());
        }
        // ZGTFCP-1608:还本到账/收息到账交易方向转换 modify by fuyongsheng 20220322 end
        String deliveryType = commonPositionEngineService.getDeliveryType(trdProductDeal.getFinprodId());
        resultMap.put("settleSpeed", this.parseSettleSpreed(deliveryType));
        resultMap.put("bidResult", 是否_否.getValue());
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", trdProductDeal.getTradeMarket());
        resultMap.put("sharesTradePlatform", null);
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("payType", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(trdProductDealTotal.getFinprodType2()) ?
                BussinessDictCodeList.缴款方式_一级分销.getValue() : null);
        resultMap.put("cdate", trdProductDeal.getTradeDate());
        resultMap.put("ccy", trdProductDeal.getCcy());
        resultMap.put("finprodId", trdProductDeal.getFinprodId());
        resultMap.put("bTotalTradeId", trdProductDeal.getbTradeId());

        List<Map<String, Object>> ptlList = new ArrayList<>();
        Map<String, Object> pltMap = new HashMap<>();
        pltMap.put("portfolioId", trdProductDealAdd.getPortfolioId());
        pltMap.put("tradeId", trdProductDeal.getTradeId());
        pltMap.put("etrId", null);
        pltMap.put("ordEntrustId", null);
        pltMap.put("etrSecAmount", BigDecimal.ZERO);
        pltMap.put("ordId", null);
        pltMap.put("ordSecAmount", BigDecimal.ZERO);
        pltMap.put("cashAcctId", accountNo);
        pltMap.put("secAcctId", trdProductDealTotal.getSecManageAcctId());
        pltMap.put("secAmount", trdProductDeal.getShareAmt());
        pltMap.put("cashAmount", trdProductDeal.getTradeAmt());
        pltMap.put("invAim", trdProductDeal.getInvAim());
        pltMap.put("integrateSecAmount", BigDecimal.ZERO);
        pltMap.put("ownSecAmount", BigDecimal.ZERO);
        ptlList.add(pltMap);
        resultMap.put("ptlList", ptlList);
    }

    private Object getBusiAct(TrdFinProductDealTotal trdProductDealTotal) {
        String busiAct = DictCodeListExt3.业务动作_成交匹配.getValue();
        if (DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue().equals(trdProductDealTotal.getFinprodType2())
                && StringUtil.isNotEmpty(trdProductDealTotal.getbTradeId())) {
            busiAct = DictCodeListExt3.业务动作_成交确认.getValue();
        }
        return busiAct;
    }

    /**
     * 可转债
     *
     * @param entity
     * @param resultMap
     */
    private void bond003(BaseTradeProductDealDto entity, Map<String, Object> resultMap) {
        resultMap.put(INTERFACE_TYPE, INTERF_TYPE_BOND003);
        // 交易平台	见数据字典：交易平台
        resultMap.put(SHARES_TRADE_PLATFORM, this.getSharesTradePlatform(entity, INTERF_TYPE_BOND003));

        TrdFinProductDealTotal trdProductDealTotal = entity.getTrdFinProductDealTotal();
        TrdProductDeal trdProductDeal = entity.getTrdProductDeal();
        TrdProductDealAdd trdProductDealAdd = entity.getTrdProductDealAdd();
        // 查询申购交易对应的指令
        TrdProductDealAdd trdProductDealAdd1 = trdProductDealAddMapper.selectByPrimaryKey(trdProductDeal.getbTradeId());
        String accountNo = baseAssetCashPositionCommMapper.queryAccountNo(trdProductDealTotal.getSecManageAcctId());
        // ZGTFCP-1596:修改申购确认相关字段赋值 modify by fuyongsheng 20220322 start
        // 业务动作
        if(DictCodeList.交易方向_申购确认.getValue().equals(trdProductDeal.getPs())){
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_缴款.getValue());
            resultMap.put("bTotalTradeId", trdProductDeal.getbTradeId());
        }
        else if(DictCodeList.交易方向_上市流通.getValue().equals(trdProductDeal.getPs())){
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_上市.getValue());
        }
        else{
            resultMap.put(BUSI_ACT, DictCodeListExt3.业务动作_成交匹配.getValue());
        }
        if(DictCodeList.交易方向_还本到账.getValue().equals(trdProductDeal.getPs())){
            resultMap.put("ps", DictCodeList.交易方向_还本);
        }else if(DictCodeList.交易方向_收息到账.getValue().equals(trdProductDeal.getPs())){
            resultMap.put("ps", DictCodeList.交易方向_收息);
        }else{
            resultMap.put("ps", trdProductDeal.getPs());
        }
        // ZGTFCP-1596:修改申购确认相关字段赋值 modify by fuyongsheng 20220322 end
        String deliveryType = commonPositionEngineService.getDeliveryType(trdProductDeal.getFinprodId());
        resultMap.put("settleSpeed", parseSettleSpreed(deliveryType));
        resultMap.put("purchModle", null);
        resultMap.put("tradePlace", trdProductDeal.getTradeMarket());
        resultMap.put("sharesTradePlatform", null);
        resultMap.put("deliveryType", deliveryType);
        resultMap.put("decisionAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("systemAct", DictCodeListExt3.系统动作_新建.getValue());
        resultMap.put("workFlowId", null);
        resultMap.put("ccy", trdProductDeal.getCcy());

        List<Map<String, Object>> ptlCashList = new ArrayList<>(4);
        List<Map<String, Object>> bondSecList = new ArrayList<>(4);
        List<Map<String, Object>> applyBondSecList = new ArrayList<>(4);

        Map<String, Object> ptlCash = new HashMap<>(8);
        ptlCash.put("portfolioId", trdProductDealAdd.getPortfolioId());
        ptlCash.put("cashAcctId", accountNo);
        ptlCash.put("tradeId", trdProductDeal.getTradeId());
        ptlCash.put("etrId", trdProductDealAdd1 != null ? trdProductDealAdd1.getEntrustId() : StringUtil.EMPTY);
        ptlCash.put("cashAmount", trdProductDeal.getTradeAmt());
        ptlCash.put("etrSecAmount", null);
        ptlCash.put("ordId", null);
        ptlCash.put("ordSecAmount", null);
        ptlCashList.add(ptlCash);
        resultMap.put("ptlCashList", ptlCashList);

        //可转债数据集
        Map<String, Object> bondSec = new HashMap<>();
        bondSec.put("portfolioId", trdProductDealAdd.getPortfolioId());
        bondSec.put("finprodId", trdProductDeal.getFinprodId());
        bondSec.put("secAcctId", trdProductDealTotal.getSecManageAcctId());
        bondSec.put("tradeId", trdProductDeal.getTradeId());
        bondSec.put("etrId", trdProductDealAdd1 != null ? trdProductDealAdd1.getEntrustId() : StringUtil.EMPTY);
        bondSec.put("integrateSecAmount", new BigDecimal(0));
        bondSec.put("ownSecAmount", ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        bondSec.put("secAmount", ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        bondSecList.add(bondSec);
        resultMap.put("bondSecList", bondSecList);

        // 配债申购数据集
        Map<String, Object> applyBondSec = new HashMap<>();
        applyBondSec.put("portfolioId", trdProductDealAdd.getPortfolioId());
        if(DictCodeList.交易方向_上市流通.getValue().equals(trdProductDeal.getPs())){
            applyBondSec.put("finprodId", trdProductDeal.getRemark());
        }else{
            applyBondSec.put("finprodId", commonPositionService.getStockId(trdProductDeal.getFinprodId(), trdProductDeal.getFinprodType2(), trdProductDeal.getPs()));
        }
        applyBondSec.put("secAcctId", trdProductDealTotal.getSecManageAcctId());
        applyBondSec.put("tradeId", trdProductDeal.getTradeId());
        applyBondSec.put("etrId", trdProductDealAdd1 != null ? trdProductDealAdd1.getEntrustId() : StringUtil.EMPTY);
        applyBondSec.put("integrateSecAmount", new BigDecimal(0));
        applyBondSec.put("ownSecAmount", ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        applyBondSec.put("secAmount", ArithmeticUtil.multiply(trdProductDeal.getShareAmt(), new BigDecimal(100)));
        applyBondSecList.add(applyBondSec);
        resultMap.put("applyBondSecList", applyBondSecList);
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



    // ZGTFCP-1593:上市流通特殊处理 modify by fuyongsheng 20220323 start
    /**
     * 发送上市流通接口
     * @param entity
     */
    public void sendSsltPositionHttpRequest(BaseTradeProductDealDto entity) {
        logger.debug("【发送上市流通头寸请求】entity: {}", JSON.toJSONString(entity));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", LoginUserUtil.getUserName());
        resultMap.put("totalTradeId", IDGenerator.generate());
        resultMap.put("finprodId", entity.getTrdProductDeal().getFinprodId());
        PositionHttpRequest.sendSslt(resultMap);
    }
    // ZGTFCP-1593:上市流通特殊处理 modify by fuyongsheng 20220323 end

    private Date getTradeDate(TrdProductDeal trdProductDeal){
        Date date = trdProductDeal.getTradeDate();
        if(DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(trdProductDeal.getFinprodType2())){
            if(DictCodeList.交易方向_红利再投.getValue().equals(trdProductDeal.getPs())
                    || DictCodeList.交易方向_现金分红.getValue().equals(trdProductDeal.getPs())){
                List<SettleTransData> settleTransData = commonPositionService.selectSettleTransData(trdProductDeal.getTotleTradeId());
                if(!CollectionUtils.isEmpty(settleTransData)){
                    date = settleTransData.get(0).getTradeDate();
                }
            }
        }
        if(DictCodeList.交易方向_上市流通.getValue().equals(trdProductDeal.getPs())){
            date =  commonPositionService.getLastWorkDate(trdProductDeal.getTradeDate(), this.getCalendarId(trdProductDeal.getTradeMarket()));
        }
        return date;
    }

    private String getCalendarId(String tradeMarket){
        if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)){
            return CacheUtil.getSysParam(STL_DOWNDATA_DEAL_SHJYS_CALID);
        }else{
            return CacheUtil.getSysParam(STL_DOWNDATA_DEAL_SZJYS_CALID);
        }
    }

    /**
     * 获取交易平台
     * @param entity
     * @param interfaceType
     * @return
     */
    private String getSharesTradePlatform(BaseTradeProductDealDto entity, String interfaceType){
        return commonPositionEngineService.getExchangeTradePlatform(StringUtil.EMPTY,
                entity.getTrdProductDeal().getTradePlat(), entity.getTrdProductDeal().getPs(),
                entity.getTrdProductDeal().getFinprodId(), interfaceType);
    }
}
