/*
 * 文件名：TaxBalanceQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dao.TrdRepoValuationTrade001Mapper;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTradeResult;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(value="serviceTrdRepoValuationTrade001Service")
public class TrdRepoValuationTrade001Service extends CommonExportService<TrdRepoValuationTrade001Dto>{

    @Autowired
    private TrdRepoValuationTrade001Mapper trdRepoValuationTrade001Mapper;

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
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 16:47
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(int page, int length,Map<String, Object> params) {
        return this.queryForPageForSelectReturn(page, length, ()->trdRepoValuationTrade001Mapper.findPortfolioId(params));
    }




    /**
     *
     * 工作流完结之后的操作
     * <功能详细描述>
     * @param entity 实体
     * @see
     * @since 1.0
     */
    public void doComplete(TrdRepoValuationTrade001Dto entity) {

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
    public Page<TrdRepoValuationTrade001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {
        //产品代码
        String portfolioId = (String)pageParam.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        pageParam.put("portfolioIdArr", portfolioIdArr);

        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        pageParam.put("psArr", psArr);

        // 获取交易场所
        String tradeMarket = (String)pageParam.get("tradeMarket");
        // 交易场所多选，转为数组
        String[] tradeMarketArr = null;
        if (StringUtil.isNotEmpty(tradeMarket)) {
            tradeMarketArr = tradeMarket.split(",");
        }
        pageParam.put("tradeMarketArr", tradeMarketArr);

        return this.queryForPage(page, length,
                () -> trdRepoValuationTrade001Mapper.selectDeal(pageParam));
    }


    /**
     *
     * 获取不分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     */
    public List<TrdRepoValuationTrade001Dto> queryPageList(Map<String, Object> params)  {

        //产品代码
        String portfolioId = (String)params.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        params.put("portfolioIdArr", portfolioIdArr);

        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        // 获取交易场所
        String tradeMarket = (String)params.get("tradeMarket");
        // 交易场所多选，转为数组
        String[] tradeMarketArr = null;
        if (StringUtil.isNotEmpty(tradeMarket)) {
            tradeMarketArr = tradeMarket.split(",");
        }
        params.put("tradeMarketArr", tradeMarketArr);

        return trdRepoValuationTrade001Mapper.selectDeal(params);
    }



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
            String nextId = this.getNextId("REPOTRADEID_SEQUENCE");
            //平台日
            String dateStr= DateUtil.format(iPlatformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);//yyyyMMdd
            // 拼接
            tradeId = "JY"+"REPO"+dateStr+ nextId;
            // 查询是否重复
            int num = trdRepoValuationTrade001Mapper.selectCountByTradeId(tradeId);
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
        return trdRepoValuationTrade001Mapper.getNextId(sequenceName);
    }


    //根据交易编号查询
    public TrdRepoValuationTrade001Dto queryTradeInfoByTradeId(String tradeId) {
        return trdRepoValuationTrade001Mapper.queryTradeInfoByTradeId(tradeId);
    }

    //删除
    public int delete(TrdRepoValuationTrade001Dto dto) {
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

    public int insert(TrdRepoValuationTradeResult result){

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
        List<StlOrders> listValOrders = result.getListValOrders();
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
        List<StlOrdersExt> listValOrdersExt = result.getListValOrdersExt();
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

    public int update(TrdRepoValuationTradeResult dto){
        TrdRepoValuationTrade001Dto dto1 = new TrdRepoValuationTrade001Dto();
        dto1.setPortfolioId(dto.getTrdRepoValuationTrade001Dto().getPortfolioId());
        dto1.setTradeId(dto.getTrdRepoValuationTrade001Dto().getTradeId());
        delete(dto1);
        return insert(dto);
    }

}
