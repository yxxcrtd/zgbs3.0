/*
 * 文件名：PtlDvpFundAllocationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2019年3月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.portfolio.ptldvpfundallocation.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.portfolio.ptldvpfundallocation.dao.PtlDvpFundAllocationMapper;
import com.joyintech.service.portfolio.ptldvpfundallocation.dto.PtlDvpFundAllocationDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.service.TradeProductDealCommService;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DVP资金调拨Service
 *
 * @author etliu
 * @version 1.0 2019年3月29日
 * @see PtlDvpFundAllocationService
 * @since 1.0
 */
@Service
public class PtlDvpFundAllocationService extends BaseService<PtlDvpFundAllocationDto> {

    /**
     * PtlDvpFundAllocationMapper
     */
    @Autowired
    private PtlDvpFundAllocationMapper ptlDvpFundAllocationMapper;

    /**
     * 注入金融产品mapper
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    /**
     * 注入金融产品mapper
     */
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    /**
     * PaTrdProductDealMapper
     */
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    /**
     * 注入组合交易mapper
     */
    @Autowired
    private PtlTradeMapper ptlTradeMapper;

    /**
     * 组合现金流水
     */
    @Autowired(required = false)
    private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired(required = false)
    private TradeProductDealCommService tradeProductDealCommService;

    @Autowired
    private MapperDelegateService mapperDelegateService;

    /**
     * StlDvpOrdersMapper
     */
    @Autowired
    private StlDvpOrdersMapper stlDvpOrdersMapper;

    /**
     * StlDvpTradeMatchMapper
     */
    @Autowired
    private StlDvpTradeMatchMapper stlDvpTradeMatchMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    /**
     * 画面查询方法
     * 
     * @param map 请求参数
     * @param page 页面
     * @param length 长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    public Page<PtlDvpFundAllocationDto> findPage(int page, int length, Map<String, Object> map)
    {

        // 组合代码
        if (map.get("portfolioId") != null
            && StringUtil.isNotEmpty((String)map.get("portfolioId"))) {
            map.replace("portfolioId",
                Arrays.asList(map.get("portfolioId").toString().split(",")));
        }
        else {
            map.replace("portfolioId", null);
        }

        // 划款类型→交易方向
        String payType = (String)map.get("payType");
        List<String> psList = new ArrayList<String>();
        if (StringUtil.isBlank(payType)) {
            String[] psArr = {BussinessDictCodeList.交易方向_托管户_上清DVP.getValue(),
                BussinessDictCodeList.交易方向_托管户_中债DVP.getValue(),
                BussinessDictCodeList.交易方向_上清DVP_托管户.getValue(),
                BussinessDictCodeList.交易方向_中债DVP_托管户.getValue(),
                BussinessDictCodeList.交易方向_上清DVP_中债DVP.getValue(),
                BussinessDictCodeList.交易方向_中债DVP_上清DVP.getValue()};
            psList = Arrays.asList(psArr);
        }
        else {
            psList.add(payType);
        }
        map.put("psList", psList);

        // 有效状态转交易状态
        String dataStatus = (String)map.get("dataStatus");
        List<String> tradeStatusList = new ArrayList<String>();
        if (StringUtil.equals(BussinessDictCodeList.画面数据状态_有效.getValue(), dataStatus)) {
            tradeStatusList.add(BussinessDictCodeList.交易状态_初始.getValue());
        }
        else if (StringUtil.equals(BussinessDictCodeList.画面数据状态_撤销.getValue(), dataStatus)) {
            tradeStatusList.add(BussinessDictCodeList.交易状态_撤销.getValue());
        }
        else if (StringUtil.equals(BussinessDictCodeList.画面数据状态_流程中.getValue(), dataStatus)) {

        }
        else {
            tradeStatusList.add(BussinessDictCodeList.交易状态_初始.getValue());
            tradeStatusList.add(BussinessDictCodeList.交易状态_撤销.getValue());
        }
        map.put("tradeStatusList", tradeStatusList);

        // 转账日期
        if (map.get("dataDateStart") == null
            || StringUtil.isEmpty((String)map.get("dataDateStart"))) {
            map.replace("dataDateStart", null);
        }

        map.put("yesNo_no", BussinessDictCodeList.是否_否.getValue());
        map.put("IE_FLAG_02", BussinessDictCodeList.收支方向_支出.getValue());
        map.put("effective", BussinessDictCodeList.画面数据状态_有效.getValue());
        map.put("inworkflow", BussinessDictCodeList.画面数据状态_流程中.getValue());
        map.put("revoked", BussinessDictCodeList.画面数据状态_撤销.getValue());
        map.put("TRADE_STATUS_01", BussinessDictCodeList.交易状态_初始.getValue());
        map.put("TRADE_STATUS_03", BussinessDictCodeList.交易状态_撤销.getValue());
        Page<PtlDvpFundAllocationDto> pageData = super.queryForPage(page, length,
            () -> ptlDvpFundAllocationMapper.findPage(map));
        /*
         * if (pageData != null && !StringUtil.isEmptyList(pageData.getRoot())) { for
         * (PtlDvpFundAllocationDto dataDto : pageData.getRoot()) { if
         * (StringUtil.equals(BussinessDictCodeList.交易状态_初始.getValue(), dataDto.getOrderStatus()))
         * { dataDto.setOrderStatus(BussinessDictCodeList.画面数据状态_有效.getValue()); } else if
         * (StringUtil.equals(BussinessDictCodeList.交易状态_撤销.getValue(), dataDto.getOrderStatus()))
         * { dataDto.setOrderStatus(BussinessDictCodeList.画面数据状态_撤销.getValue()); } } }
         */
        return pageData;
    }

    /**
     * 查询收款账号
     * 
     * @param map 请求参数
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCounterAcctNo(Map<String, Object> map) {
        String payType = (String)map.get("payType");
        String portfolioId = (String)map.get("portfolioId");

        if (StringUtil.isBlank(payType) || StringUtil.isBlank(portfolioId)) {
            return null;
        }

        if (StringUtil.equals(BussinessDictCodeList.交易方向_上清DVP_托管户.getValue(), payType)
            || StringUtil.equals(BussinessDictCodeList.交易方向_中债DVP_托管户.getValue(), payType)) {
            map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
            return ptlDvpFundAllocationMapper.getPtlTghCounterAcctNo(map);
        }

        String REGIST_ORG = null;
        //add by yujiaping 2020-12-10 TISHKBS-1680【DVP资金调拨】划款类型选择托管户-DVP账户，收款账户下拉框应该只有组合所绑定的中债DVP账户
        String acctType = null;
        if (StringUtil.equals(BussinessDictCodeList.交易方向_托管户_上清DVP.getValue(), payType)
            || StringUtil.equals(BussinessDictCodeList.交易方向_中债DVP_上清DVP.getValue(), payType)) {
            REGIST_ORG = BussinessDictCodeList.登记托管机构_上海清算所.getValue();
            acctType =  BussinessDictCodeList.资金账户类型_上清所DVP账户.getValue();
        }
        else if (StringUtil.equals(BussinessDictCodeList.交易方向_托管户_中债DVP.getValue(), payType)
                 || StringUtil.equals(BussinessDictCodeList.交易方向_上清DVP_中债DVP.getValue(),
                     payType)) {
            REGIST_ORG = BussinessDictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue();
            acctType =  BussinessDictCodeList.资金账户类型_中债登DVP账户.getValue();
        }

        map.put("REGIST_ORG", REGIST_ORG);
        map.put("acctType", acctType);
       // add by yujiaping 2020-12-10 TISHKBS-1680【DVP资金调拨】划款类型选择托管户-DVP账户，收款账户下拉框应该只有组合所绑定的中债DVP账户

        return ptlDvpFundAllocationMapper.getPtlSecTrdAcctNo(map);
    }

    /**
     * 查询付款账号
     * 
     * @param map 请求参数
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getMyAcctNo(Map<String, Object> map) {
        String payType = (String)map.get("payType");
        String portfolioId = (String)map.get("portfolioId");

        if (StringUtil.isBlank(payType) || StringUtil.isBlank(portfolioId)) {
            return null;
        }

        if (StringUtil.equals(BussinessDictCodeList.交易方向_托管户_上清DVP.getValue(), payType)
            || StringUtil.equals(BussinessDictCodeList.交易方向_托管户_中债DVP.getValue(), payType)) {
            map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
            return ptlDvpFundAllocationMapper.getPtlTghCounterAcctNo(map);
        }

        String REGIST_ORG = null;
        //add by yujiaping 2020-12-10 TISHKBS-1680【DVP资金调拨】划款类型选择托管户-DVP账户，收款账户下拉框应该只有组合所绑定的中债DVP账户
        String acctType = null;
        if (StringUtil.equals(BussinessDictCodeList.交易方向_上清DVP_托管户.getValue(), payType)
            || StringUtil.equals(BussinessDictCodeList.交易方向_上清DVP_中债DVP.getValue(), payType)) {
            REGIST_ORG = BussinessDictCodeList.登记托管机构_上海清算所.getValue();
            acctType =  BussinessDictCodeList.资金账户类型_上清所DVP账户.getValue();
        }
        else if (StringUtil.equals(BussinessDictCodeList.交易方向_中债DVP_托管户.getValue(), payType)
                 || StringUtil.equals(BussinessDictCodeList.交易方向_中债DVP_上清DVP.getValue(),
                     payType)) {
            REGIST_ORG = BussinessDictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue();
            acctType =  BussinessDictCodeList.资金账户类型_中债登DVP账户.getValue();
        }
        map.put("REGIST_ORG", REGIST_ORG);
        map.put("acctType", acctType);
        // add by yujiaping 2020-12-10 TISHKBS-1680【DVP资金调拨】划款类型选择托管户-DVP账户，收款账户下拉框应该只有组合所绑定的中债DVP账户

        return ptlDvpFundAllocationMapper.getPtlSecTrdAcctNo(map);
    }

    /**
     * 查询交易编号(根据指令编号)
     * 
     * @param map 请求参数
     * @return 交易编号
     * @see
     * @since 1.0
     */
    public String getTradeId(Map<String, Object> map) {
        return ptlDvpFundAllocationMapper.getTradeId(map);
    }

    /**
     * 查询dvp指令信息
     *
     * @param bizCode 交易编号
     * @return dvp指令信息
     * @see
     * @since 1.0
     */
    public List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode) {
        return ptlDvpFundAllocationMapper.selectOrdersByBizCode(bizCode,
            BussinessDictCodeList.收支方向_支出.getValue());
    }

    /**
     * 新建资金划拨
     * 
     * @param entity 资金划拨实体类
     * @return 是否成功
     * @see
     * @since 1.0
     */
    public int insertData(PtlDvpFundAllocationDto entity) {
        /**
         * 插入金融产品交易表
         */

//        trdProductDealMapper.insert(entity.getTrdProductDeal());

        // 交易附属表
//        trdProductDealAddMapper.insert(entity.getTrdProductDealAdd());

        // 金融产品交易扩展表
//        paTrdProductDealMapper.insert(entity.getPaTrdProductDeal());

        /**
         * 插入组合交易表
         */
        if (entity.getPtlTrade() != null) {
            entity.getPtlTrade().forEach(ptlTrade -> ptlTradeMapper.insert(ptlTrade));
        }
        mapperDelegateService.insertBatch(TrdProductDealMapper.class, entity.getTrdProductDeal(), TrdProductDealAddMapper.class,
                entity.getTrdProductDealAdd(), PaTrdProductDealMapper.class, entity.getPaTrdProductDeal());

        /**
         * 插入DVP划款及DVP指令交易匹配表
         */
        if (entity.getStlPayOrdersList() != null) {
            for (StlPayOrders stlPayOrders : entity.getStlPayOrdersList()) {
                StlDvpOrders stlDvpOrders = new StlDvpOrders();
                BeanUtil.copyPropertiesIgnoreNull(stlDvpOrders, stlPayOrders);

                // 处理ps，收支方向，以及是否需要发送
                if (StringUtil.equals(BussinessDictCodeList.交易方向_托管户_上清DVP.getValue(),
                    entity.getTrdProductDeal().getPs())
                    || StringUtil.equals(BussinessDictCodeList.交易方向_托管户_中债DVP.getValue(),
                        entity.getTrdProductDeal().getPs())) {
                    stlDvpOrders.setMyAcctNo(stlPayOrders.getMyAcctNo());
                    stlDvpOrders.setCounterAcctNo(stlPayOrders.getCounterAcctNo());
                    stlDvpOrders.setPs(BussinessDictCodeList.收支方向_支出.getValue());
                }
                else if (StringUtil.equals(BussinessDictCodeList.交易方向_上清DVP_托管户.getValue(),
                    entity.getTrdProductDeal().getPs())
                         || StringUtil.equals(BussinessDictCodeList.交易方向_中债DVP_托管户.getValue(),
                             entity.getTrdProductDeal().getPs())) {
                    stlDvpOrders.setMyAcctNo(stlPayOrders.getCounterAcctNo());
                    stlDvpOrders.setCounterAcctNo(stlPayOrders.getMyAcctNo());
                    stlDvpOrders.setPs(BussinessDictCodeList.收支方向_收入.getValue());
                }
                else {
                    stlDvpOrders.setMyAcctNo(stlPayOrders.getMyAcctNo());
                    stlDvpOrders.setCounterAcctNo(stlPayOrders.getCounterAcctNo());
                    stlDvpOrders.setPs(BussinessDictCodeList.收支方向_支出.getValue());
                }

                mapperDelegateService.insert(StlDvpOrdersMapper.class,stlDvpOrders);
//                stlDvpOrdersMapper.insert(stlDvpOrders);

                StlDvpTradeMatch stlDvpTradeMatch = new StlDvpTradeMatch();
                stlDvpTradeMatch.setOrderId(stlDvpOrders.getOrderId());
                stlDvpTradeMatch.setIsTotalTrade(stlPayOrders.getIsTotalTrade());
                stlDvpTradeMatch.setTradeId(stlPayOrders.getTradeId());
                mapperDelegateService.insert(StlDvpTradeMatchMapper.class,stlDvpTradeMatch);
//                stlDvpTradeMatchMapper.insert(stlDvpTradeMatch);
            }
        }

        /**
         * 付款账户处理（新建付款信息或修改付款账户金额）
         */
        // 调用组合可用现金和实际现金查询更新组件，将组合现金流水改为实际，并处理组合现金表数据
//        if (entity.getPtlCashPositionFlow() != null) {
//            entity.getPtlCashPositionFlow().forEach(ptlCashPositionFlow -> {
//                PtlCashPositionFlowDto ptlCashPositionFlowDto_FuKuan = new PtlCashPositionFlowDto();
//                BeanUtil.copyPropertiesIgnoreNull(ptlCashPositionFlowDto_FuKuan, ptlCashPositionFlow);
//                ptlCashPositionFlowDto_FuKuan.setHodingType(BussinessDictCodeList.持仓类型_实际.getValue());
//                ptlCashPositionFlowDto_FuKuan.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
//                basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlowDto_FuKuan);
//
//                /**
//                 * 收款账户处理（新建付款信息或修改付款账户金额）
//                 */
//                String accountId_ShouKuan = entity.getCounterAcctNo();
//                // if (!StringUtil.isEmptyList(entity.getStlPayOrdersList())) {
//                // accountId_ShouKuan = entity.getStlPayOrdersList().get(0).getCounterAcctNo();
//                // }
//                if (StringUtil.isNotBlank(accountId_ShouKuan)) {
//                    ptlCashPositionFlowDto_FuKuan.setAcctId(accountId_ShouKuan);
//                    ptlCashPositionFlowDto_FuKuan.setAmount(
//                            ptlCashPositionFlowDto_FuKuan.getAmount().negate());
//                    ptlCashPositionFlowDto_FuKuan.setHodingType(
//                            BussinessDictCodeList.持仓类型_实际.getValue());
//                    ptlCashPositionFlowDto_FuKuan.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
//                    basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlowDto_FuKuan);
//                }
//            });
//        }
        // // 调用估值接口 start
        // if (!StringUtil.isEmptyList(entity.getStlPayOrdersList())) {
        // for (StlPayOrders stlPayOrders : entity.getStlPayOrdersList()) {
        // AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        // inputDto.setProdid(entity.getPtlTrade().getPortfolioId());
        // inputDto.setProdFinprodType(BussinessDictCodeList.金融产品类型_投资组合.getValue());
        // inputDto.setProdVdate(null);
        // inputDto.setStlPayOrders(stlPayOrders);
        // inputDto.setTrdProductDeal(entity.getTrdProductDeal());
        // AllTradeCommonUtil.stlOrdersBokTradeBuild(inputDto);
        // }
        // }
        // // 调用估值接口 end

        return 1;
    }

    /**
     * 撤销资金划拨
     * 
     * @param entity 资金划拨实体类
     * @return 是否
     * @see
     * @since 1.0
     */
    public int revokeData(PtlDvpFundAllocationDto entity) {
        String tradeId = entity.getTradeId();
        String orderId = entity.getOrderId();
        String revokeReason = entity.getRevokeReason();
        String tradeIdNew = tradeId + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR;
        String orderIdNew = orderId + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR;

        // 撤销金融产品交易表数据
        TrdProductDeal trdProductDeal = trdProductDealMapper.selectByPrimaryKey(tradeId);
        trdProductDeal.setIsCancel(BussinessDictCodeList.是否_是.getValue());
        mapperDelegateService.updateByPrimaryKey(TrdProductDealMapper.class,trdProductDeal);
//        trdProductDealMapper.updateByPrimaryKey(trdProductDeal);

        TrdProductDeal trdProductDealNew = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealNew, trdProductDeal);
        trdProductDealNew.setTradeStatus(BussinessDictCodeList.交易状态_撤销.getValue());
        trdProductDealNew.setTradeId(tradeIdNew);
        trdProductDealNew.setIsCancel(BussinessDictCodeList.是否_否.getValue());
        trdProductDealNew.setCancelRemark(revokeReason);
        mapperDelegateService.insert(TrdProductDealMapper.class,trdProductDealNew);
//        trdProductDealMapper.insert(trdProductDealNew);

        // 交易附属表
        TrdProductDealAdd trdProductDealAdd = trdProductDealAddMapper.selectByPrimaryKey(tradeId);
        if (trdProductDealAdd != null) {
            trdProductDealAdd.setTradeId(tradeIdNew);
            mapperDelegateService.insert(TrdProductDealAddMapper.class,trdProductDealAdd);
//            trdProductDealAddMapper.insert(trdProductDealAdd);
        }

        // 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = paTrdProductDealMapper.selectByPrimaryKey(tradeId);
        if (paTrdProductDeal != null) {
            paTrdProductDeal.setTradeId(tradeIdNew);
            mapperDelegateService.insert(PaTrdProductDealMapper.class,paTrdProductDeal);
//            paTrdProductDealMapper.insert(paTrdProductDeal);
        }

        // 撤销组合交易表数据
        PtlTradeKey ptlTradeKey = new PtlTradeKey();
        ptlTradeKey.setPortfolioId(trdProductDealAdd.getPortfolioId());
        ptlTradeKey.setBusiNo(tradeId);
        ptlTradeKey.setBusiTable(BussinessDictCodeList.业务表_交易资金指令表.getValue());

        PtlTrade ptlTrade = ptlTradeMapper.selectByPrimaryKey(ptlTradeKey);
        if (ptlTrade != null) {
            ptlTrade.setBusiNo(tradeIdNew);
            mapperDelegateService.insert(PtlTradeMapper.class,ptlTrade);
        }

//        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
//            ptlTradeKey.setPortfolioId(entity.getActPortfolioId());
//            ptlTradeKey.setBusiNo(tradeId);
//            ptlTrade = ptlTradeMapper.selectByPrimaryKey(ptlTradeKey);
//            if (ptlTrade != null) {
//                ptlTrade.setBusiNo(tradeIdNew);
//                mapperDelegateService.insert(PtlTradeMapper.class, ptlTrade);
//            }
//        }

        // DVP划款及DVP指令交易匹配表
        StlDvpOrders stlDvpOrders = stlDvpOrdersMapper.selectByPrimaryKey(orderId);
        mapperDelegateService.deleteByPrimaryKey(StlDvpOrdersMapper.class,orderId);
//        stlDvpOrdersMapper.deleteByPrimaryKey(orderId);

        stlDvpOrders.setOrderId(orderIdNew);
        stlDvpOrders.setpTradeId(tradeIdNew);
        stlDvpOrders.setOrderStatus(BussinessDictCodeList.指令状态_撤销.getValue());
        mapperDelegateService.insert(StlDvpOrdersMapper.class,stlDvpOrders);
//        stlDvpOrdersMapper.insert(stlDvpOrders);

        StlDvpTradeMatchKey stlDvpTradeMatchKey = new StlDvpTradeMatchKey();
        stlDvpTradeMatchKey.setTradeId(tradeId);
        stlDvpTradeMatchKey.setOrderId(orderId);
        mapperDelegateService.deleteByPrimaryKey(StlDvpTradeMatchMapper.class,stlDvpTradeMatchKey);
//        stlDvpTradeMatchMapper.deleteByPrimaryKey(stlDvpTradeMatchKey);

        StlDvpTradeMatch stlDvpTradeMatch = new StlDvpTradeMatch();
        stlDvpTradeMatch.setTradeId(tradeIdNew);
        stlDvpTradeMatch.setOrderId(orderIdNew);
        stlDvpTradeMatch.setIsTotalTrade(BussinessDictCodeList.是否_否.getValue());
        mapperDelegateService.insert(StlDvpTradeMatchMapper.class,stlDvpTradeMatch);
//        stlDvpTradeMatchMapper.insert(stlDvpTradeMatch);

        // 撤销组合现金余额数据 收付款账户
        // 根据指令编号查询之前的组合现金流水记录，插入相反的流水记录
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("orderId", orderId);
//        List<PtlCashPositionFlow> ptlCashPositionFlowList = ptlDvpFundAllocationMapper.selectPtlCashPositionFlowList(
//            paramMap);
//        if (!StringUtil.isEmptyList(ptlCashPositionFlowList)) {
//            for (PtlCashPositionFlow ptlCashPositionFlow : ptlCashPositionFlowList) {
//                PtlCashPositionFlowDto ptlCashPositionFlowDto = new PtlCashPositionFlowDto();
//                BeanUtil.copyPropertiesIgnoreNull(ptlCashPositionFlowDto, ptlCashPositionFlow);
//                ptlCashPositionFlowDto.setAmount(ptlCashPositionFlow.getAmount().negate());
//                ptlCashPositionFlowDto.setOrderId(orderIdNew);
//                basePtlCashPositionInfoService.insertCashPositionFlow(ptlCashPositionFlowDto);
//            }
//        }

        // // 调用估值接口 start
        // if (!StringUtil.isEmptyList(entity.getStlPayOrdersList())) {
        // for (StlPayOrders stlPayOrders : entity.getStlPayOrdersList()) {
        // AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        // inputDto.setProdid(entity.getPtlTrade().getPortfolioId());
        // inputDto.setProdFinprodType(BussinessDictCodeList.金融产品类型_投资组合.getValue());
        // inputDto.setProdVdate(null);
        // inputDto.setStlPayOrders(stlPayOrders);
        // inputDto.setTrdProductDeal(entity.getTrdProductDeal());
        // AllTradeCommonUtil.stlOrdersBokTradeBuild(inputDto);
        // }
        // }
        // // 调用估值接口 end
        return 1;
    }

    /**
     * 从3025接口表查询余额
     * 
     * @param paramMap 参数map
     * @return 余额
     */
    public String getAmount(Map<String, Object> paramMap) {
        return ptlDvpFundAllocationMapper.getAmount(paramMap);
    }

    public int doUpdate(PtlDvpFundAllocationDto entity) {
        // 先删除流程中数据
        tradeProductDealCommService.delete(entity.getWorkflowId());

//        if (entity.getPtlCashPositionFlow() != null) {
//            entity.getPtlCashPositionFlow().forEach(ptlCashPositionFlow -> {
//                if (StringUtil.isNotBlank(ptlCashPositionFlow.getAcctId())) {
//                    mapperDelegateService.insert(PtlCashPositionFlowMapper.class, ptlCashPositionFlow);
//                }
//            });
//        }

        if (!StringUtil.equals("revoke", entity.getOperateType())
            && !StringUtil.equals("delete", entity.getOperateType())) {
            // 重新insert
            this.insertBussFlowData(entity, OperationType.NEW);
        }
        else {
            // 重新insert
            this.insertBussFlowData(entity, OperationType.REVOKE);
        }
        return 1;
    }

    public int doInsert(PtlDvpFundAllocationDto entity) {
        String operType = entity.getOperateType();
//        if (!StringUtil.equals(operType, OperationType.REVOKE.name())
//            && !StringUtil.equals(operType, OperationType.DELETE.name())) {
//            if (entity.getPtlCashPositionFlow() != null) {
//                entity.getPtlCashPositionFlow().forEach(ptlCashPositionFlow -> {
//                    if (StringUtil.isNotBlank(ptlCashPositionFlow.getAcctId())) {
//                        mapperDelegateService.insert(PtlCashPositionFlowMapper.class, ptlCashPositionFlow);
//                    }
//                });
//            }
//        }
        // 插入业务流程表
        if (!StringUtil.equals(operType, OperationType.DELETE.name())) {
            this.insertBussFlowData(entity, OperationType.valueOf(operType));
        }
        return 1;
    }

    public CommonResult doDelete(String workflowId) {
        CommonResult delete = tradeProductDealCommService.delete(workflowId);
        return delete;
    }

    private void insertBussFlowData(PtlDvpFundAllocationDto entity, OperationType type) {
        // 产品交易DTO
        List<WflTrdProductDeal> wflTrdProductDeals = new ArrayList<>();
        // 金融产品交易扩展表Entity
        List<WflPaTrdProductDeal> wflPaTrdProductDeals = new ArrayList<>();
        // 金融产品交易附属表Entity
        List<WflTrdProductDealAdd> wflTrdProductDealAdds = new ArrayList<>();
        // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
        List<WflStlPayOrders> wflStlPayOrdersDtos = new ArrayList<>();

        if (entity.getTrdProductDeal() != null) {
            /**
             * 金融产品交易业务流程表
             */
            WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
            wflTrdProductDeal.setTradeStatus(BussinessDictCodeList.交易状态_初始.getValue());
            wflTrdProductDeal.setWorkflowStatus(BussinessDictCodeList.交易流程状态_流程中.getValue());
            wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            if(StringUtils.isBlank(wflTrdProductDeal.getFinprodId())){
                wflTrdProductDeal.setFinprodId(entity.getTradeId());
            }
            wflTrdProductDeals.add(wflTrdProductDeal);
        }

        if (entity.getTrdProductDealAdd() != null) {
            // 交易附属业务流程表
            WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDeal());
            wflTrdProductDealAdd.setWorkflowStatus(BussinessDictCodeList.交易流程状态_流程中.getValue());
            wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
            wflTrdProductDealAdd.setPortfolioId(entity.getActPortfolioId());
            wflTrdProductDealAdds.add(wflTrdProductDealAdd);
        }
        if (entity.getPaTrdProductDeal() != null) {
            // 金融产品交易扩展业务流程表
            WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
            wflPaTrdProductDeal.setWorkflowStatus(BussinessDictCodeList.交易流程状态_流程中.getValue());
            wflPaTrdProductDeal.setWorkflowId(entity.getWorkflowId());
            wflPaTrdProductDeals.add(wflPaTrdProductDeal);
        }
        if (entity.getStlPayOrdersList() != null && !entity.getStlPayOrdersList().isEmpty()) {
            for (StlPayOrdersDto stl : entity.getStlPayOrdersList()) {
                WflStlPayOrders wflStlPayOrders = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(wflStlPayOrders, stl);
                wflStlPayOrders.setWorkflowStatus(BussinessDictCodeList.交易流程状态_流程中.getValue());
                wflStlPayOrders.setWorkflowId(entity.getWorkflowId());
                wflStlPayOrdersDtos.add(wflStlPayOrders);
            }
        }
        if (type == OperationType.REVOKE) {
            wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                wflTrdProductDeal.setpTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDeal.setTradeStatus(BussinessDictCodeList.交易状态_撤销.getValue());
                wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + "R");
            });
            wflPaTrdProductDeals.forEach(wflTrdProductDeal -> wflTrdProductDeal.setTradeId(
                wflTrdProductDeal.getTradeId() + "R"));

            wflTrdProductDealAdds.forEach(wflTrdProductDeal -> wflTrdProductDeal.setTradeId(
                wflTrdProductDeal.getTradeId() + "R"));

            wflStlPayOrdersDtos.forEach(order -> {
                order.setTradeId(order.getTradeId() + "R");
                order.setOrderStatus(BussinessDictCodeList.指令状态_撤销.getValue());
                order.setOrderId(order.getOrderId() + "R");
            });

        }
        // 组合交易表Entity
        // 划款指令表Entity(调用划款指令共通业务组件的保存处理，拿到输出的划款指令结果DTO。加工形成金融产品交易DTO的要素数据)
        // 插入Entity对应的业务流程DB中
        mapperDelegateService.insertAutoClassifyBatch(wflStlPayOrdersDtos, wflTrdProductDeals,
            wflPaTrdProductDeals, wflTrdProductDealAdds);
    }
}
