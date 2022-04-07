/*
 * 文件名：TrdDepositInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年10月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.service;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;

import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dao.TrdDepositValuationTrade001Mapper;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trddepositvaluationtrade.dto.TrdDepositValuationTradeResult;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;

import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 存款类投资交易Service
 * @author fanshuanghu
 * @version 1.0 2018年10月10日
 * @see TrdDepositValuationTrade001Service
 * @since 1.0
 */
@Service("backTrdDepositValuationTrade001Service")
@Transactional(rollbackFor=Exception.class)
public class TrdDepositValuationTrade001Service extends BaseService<TrdDepositValuationTrade001Dto> {

    /**
     * 注入存款类投资交易mapper
     */
    @Autowired
    private TrdDepositValuationTrade001Mapper trdDepositValuationTrade001Mapper;


    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;

    @Autowired
    private TrdBondTradeMapper trdBondTradeMapper;


    /**
     * 入账信息BatchMapper
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 结算指令BatchMapper
     */
    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    /**
     * 结算指令拓展BatchMapper
     */
    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;

    /**
     * 金融产品交易BatchMapper
     */
    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    /**
     * 金融产品交易扩展表BatchMapper
     */
    @Autowired
    private ValTrdProductDealAddBatchMapper valTrdProductDealAddBatchMapper;

    /**
     * 金融产品实物交易表BatchMapper
     */
    @Autowired
    private ValTrdFinProductBarterBatchMapper valTrdFinProductBarterBatchMapper;

    /**
     * 交易类金融产品表BatchMapper
     */
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;

    /**
     * 账务明细BatchBatchMapper
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    /**
     * 账务明细代码BatchBatchMapper add by wujie on 20180712
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 交易费用明细BatchBatchMapper
     */
    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 策略代码生成
     *
     * @return
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        String tradeId = null;
        while (true) {
            // 获取自增数
            String nextId = this.getNextId("DEPOSITTRADEID_SEQUENCE");
            //平台日
            String dateStr= DateUtil.format(iPlatformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);//yyyyMMdd
            // 拼接
            tradeId = "JY"+"FICC"+dateStr+ nextId;
            // 查询是否重复
            int num = trdDepositValuationTrade001Mapper.selectCountByTradeId(tradeId);
            // 如果自增的策略代码不存在，则跳出循环
            if (num==0) {
                break;
            }
        }
        return tradeId;
    }

    /**
     * 获取自增序列值
     *
     * @param sequenceName 序列名
     * @return
     */
    public String getNextId(String sequenceName) {
        return trdDepositValuationTrade001Mapper.getNextId(sequenceName);
    }




    /**
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 16:47
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(int page, int length,Map<String, Object> params) {
        return this.queryForPageForSelectReturn(page, length, ()->trdDepositValuationTrade001Mapper.findPortfolioId(params));
    }

    //资产代码下拉框
    public Page<SelectOptionDto> findFinprodId(int page, int length,Map<String, Object> params){
        return this.queryForPageForSelectReturn(page, length, ()->trdDepositValuationTrade001Mapper.findFinprodId(params));
    }

    //交易对手
    public Page<SelectOptionDto> getCounterId(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdDepositValuationTrade001Mapper.getCounterId(param));
    }

    /**
     *
     * 获取分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     * @param pageParam Map<String, Object>
     * @return Page<MstSharePriceDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<TrdDepositValuationTrade001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {

        //产品代码
        String portfolioId = (String)pageParam.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        pageParam.put("portfolioIdArr", portfolioIdArr);
        //资产代码
        String finprodMarketId = (String)pageParam.get("finprodMarketId");
        String[] finprodMarketIdArr = null;
        if(StringUtil.isNotEmpty(finprodMarketId)){
            finprodMarketIdArr = finprodMarketId.split(",");
        }
        pageParam.put("finprodMarketIdArr", finprodMarketIdArr);
        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        pageParam.put("psArr", psArr);

        //传递默认值
        pageParam.put("PS_T01",DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02",DictCodeList.交易方向_卖出.getValue());
        pageParam.put("busiType",DictCodeList.业务类型_存款类资产交易.getValue());
        pageParam.put("finprodType", DictCodeListExt1.金融产品类型_存款.getValue());

        return this.queryForPage(page, length,
                () -> trdDepositValuationTrade001Mapper.selectDeal(pageParam));
    }


    /**
     *
     * 获取不分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     */
    public List<TrdDepositValuationTrade001Dto> queryPageList(Map<String, Object> params)  {

        //产品代码
        String portfolioId = (String)params.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        params.put("portfolioIdArr", portfolioIdArr);
        //资产代码
        String finprodMarketId = (String)params.get("finprodMarketId");
        String[] finprodMarketIdArr = null;
        if(StringUtil.isNotEmpty(finprodMarketId)){
            finprodMarketIdArr = finprodMarketId.split(",");
        }
        params.put("finprodMarketIdArr", finprodMarketIdArr);
        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        //传递默认值
        params.put("PS_T01",DictCodeList.交易方向_买入.getValue());
        params.put("PS_T02",DictCodeList.交易方向_卖出.getValue());
        params.put("busiType",DictCodeList.业务类型_存款类资产交易.getValue());
        params.put("finprodType", DictCodeListExt1.金融产品类型_存款.getValue());
        return trdDepositValuationTrade001Mapper.selectDeal(params);
    }


    //根据交易编号查询
    public TrdDepositValuationTrade001Dto queryTradeInfoByTradeId(Map<String, Object> params) {
        return trdDepositValuationTrade001Mapper.queryTradeInfoByTradeId(params);
    }



    //删除
    public int delete(TrdDepositValuationTrade001Dto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getPortfolioId());   //产品代码
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 2.交易费用明细表 TRD_TRADE_FEE_DETAIL
        count += valCommonBatchMapper.delTradeFeeDetailByTradeId(listDeal);
        // 3.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 4.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 5.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
        // 6.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 7.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 8.金融产品实物交易 TRD_FIN_PRODUCT_BARTER
        count += valCommonBatchMapper.delTrdFinProductBarterByTradeId(listDeal);
        // 9.账务明细代码表 BOK_DETAIL_ID
        count += trdBondTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


    public int insert(TrdDepositValuationTradeResult result){

        int maxSqlNum =100;
        // 金融产品交易
        List<TrdProductDeal> listValFinProductDeal = result.getListValFinProductDeal();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();

            for (TrdProductDeal e : listValFinProductDeal) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valFinProductDealBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valFinProductDealBatchMapper.insertBatch(listInsert);
            }
        }

        // 估值核算交易扩展表
        List<TrdProductDealAdd> listValFinProductDealExt = result.getListValFinProductDealExt();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDealExt)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDealAdd> listInsert = new ArrayList<TrdProductDealAdd>();

            for (TrdProductDealAdd e : listValFinProductDealExt) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valTrdProductDealAddBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valTrdProductDealAddBatchMapper.insertBatch(listInsert);
            }
        }

        /**add by tanxuan on 20191227 start**/
        // 交易类金融产品
        List<FinTradeProduct> listValFinTradeProduct = result.getListValFinTradeProduct();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinTradeProduct)) {
            // 定义一个固定大小插入存放集合
            List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();

            for (FinTradeProduct e : listValFinTradeProduct) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valFinTradeProductBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valFinTradeProductBatchMapper.insertBatch(listInsert);
            }
        }
        /**add by tanxuan on 20191227 end**/

        // 入账信息表
        List<BokDealAccounting> listValDealAccounting = result.getListValDealAccounting();
        // 入库
        if (CollectionUtils.isNotEmpty(listValDealAccounting)) {
            // 定义一个固定大小插入存放集合
            List<BokDealAccounting> listInsert = new ArrayList<BokDealAccounting>();

            for (BokDealAccounting e : listValDealAccounting) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valDealAccountingBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valDealAccountingBatchMapper.insertBatch(listInsert);
            }
        }

        // 账务明细表
        List<BokDealAccountingDetail> listValDealAccountingDetail = result.getListValDealAccountingDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValDealAccountingDetail)) {
            // 定义一个固定大小插入存放集合
            List<BokDealAccountingDetail> listInsert = new ArrayList<BokDealAccountingDetail>();

            for (BokDealAccountingDetail e : listValDealAccountingDetail) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valDealAccountingDetailBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valDealAccountingDetailBatchMapper.insertBatch(listInsert);
            }
        }
        // 账务明细代码表
        List<BokDetailId> listValBokDetailId =
                interfCommonService.toRepeatBokDetailId(result.getListValBokDetailId());
        if (CollectionUtils.isNotEmpty(listValBokDetailId)) {
            // 定义一个固定大小插入存放集合
            List<BokDetailId> listInsert = new ArrayList<BokDetailId>();

            for (BokDetailId e : listValBokDetailId) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valBokDetailIdBatchMapper.deleteBatch(listInsert);
                    valBokDetailIdBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valBokDetailIdBatchMapper.deleteBatch(listInsert);
                valBokDetailIdBatchMapper.insertBatch(listInsert);
            }
        }
        // 交易费用明细表
        List<TrdTradeFeeDetail> listValTradeFeeDetail = result.getListValTradeFeeDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValTradeFeeDetail)) {
            // 定义一个固定大小插入存放集合
            List<TrdTradeFeeDetail> listInsert = new ArrayList<TrdTradeFeeDetail>();

            for (TrdTradeFeeDetail e : listValTradeFeeDetail) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valTradeFeeDetailBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valTradeFeeDetailBatchMapper.insertBatch(listInsert);
            }
        }
        //指令
        List<StlOrders> listValOrders = result.getValOrdersList();
        // 入库
        if (CollectionUtils.isNotEmpty(listValOrders)) {
            // 定义一个固定大小插入存放集合
            List<StlOrders> listInsert = new ArrayList<StlOrders>();

            for (StlOrders e : listValOrders) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valOrdersBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valOrdersBatchMapper.insertBatch(listInsert);
            }
        }

        List<StlOrdersExt> listValOrdersExt = result.getValOrdersExtList();
        // 入库
        if (CollectionUtils.isNotEmpty(listValOrdersExt)) {
            // 定义一个固定大小插入存放集合
            List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();

            for (StlOrdersExt e : listValOrdersExt) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valOrdersExtBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valOrdersExtBatchMapper.insertBatch(listInsert);
            }
        }

        return 1;
    }

    public int update(TrdDepositValuationTradeResult dto){
        TrdDepositValuationTrade001Dto dto1 = new TrdDepositValuationTrade001Dto();
        dto1.setPortfolioId(dto.getTrdDepositValuationTrade001Dto().getPortfolioId());
        dto1.setTradeId(dto.getTrdDepositValuationTrade001Dto().getTradeId());
        delete(dto1);
        return insert(dto);
    }
}
