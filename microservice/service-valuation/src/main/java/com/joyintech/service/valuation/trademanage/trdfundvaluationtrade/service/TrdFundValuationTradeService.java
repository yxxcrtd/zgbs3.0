/*
 * 文件名：TaxBalanceQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.component.common.constants.BussinessDictCodeList;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dao.TrdFundValuationTradeMapper;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundTradeResult;
import com.joyintech.service.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
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


/**
 * @Description:基金交易查询
 * @auther: yuzj
 * @date: 2022/1/4
 * @param:
 * @return:
 */
@Service(value="serviceTrdFundValuationTradeService")
public class TrdFundValuationTradeService extends CommonExportService<TrdFundValuationTradeDto>{

    @Autowired
    private TrdFundValuationTradeMapper trdFundValuationTradeMapper;

    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;

    @Autowired
    private TrdBondTradeMapper trdBondTradeMapper;

    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;

    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;

    @Autowired
    private ValTrdProductDealAddBatchMapper valTrdProductDealAddBatchMapper;


    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

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
    public Page<TrdFundValuationTradeDto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);
        //资产代码
        String asset = (String)pageParam.get("asset");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(asset)){
            assetIdArr = asset.split(",");
        }
        pageParam.put("assetIdArr", assetIdArr);
        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
            pageParam.put("psArr", psArr);
        }else{
            String[] psArr1 = {DictCodeList.交易方向_买入.getValue()
                    ,DictCodeList.交易方向_卖出.getValue()
                    ,DictCodeList.交易方向_申购.getValue()
                    ,DictCodeList.交易方向_申购确认.getValue()
                    ,DictCodeList.交易方向_认购确认.getValue()
                    ,DictCodeList.交易方向_赎回确认.getValue()
                    ,DictCodeList.交易方向_赎回到账.getValue()
                    ,DictCodeList.交易方向_认购.getValue()};
            pageParam.put("psArr", psArr1);
        }

        // 获取交易方向
        String tradeMarket = (String)pageParam.get("tradeMarket");
        // 交易方向多选，转为数组
        String[] tradeMarketArr = null;
        if (StringUtil.isNotEmpty(tradeMarket)) {
            tradeMarketArr = tradeMarket.split(",");
        }
        pageParam.put("tradeMarketArr", tradeMarketArr);

        // 获取数据来源
        String dataSource = (String)pageParam.get("dataSource");
        // 数据来源多选，转为数组
        String[] dataSourceArr = null;
        if (StringUtil.isNotEmpty(dataSource)) {
            dataSourceArr = dataSource.split(",");
        }
        pageParam.put("dataSourceArr", dataSourceArr);

        //传递默认值
        pageParam.put("busiType_06",DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        pageParam.put("busiType_07",DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        pageParam.put("finprodType", DictCodeListExt1.金融产品类型_基金.getValue());
        pageParam.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        pageParam.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        pageParam.put("PS_T04", DictCodeList.交易方向_申购.getValue());
        pageParam.put("PS_T05", DictCodeList.交易方向_申购确认.getValue());
        pageParam.put("PS_T18", DictCodeList.交易方向_认购确认.getValue());
        pageParam.put("PS_T19", DictCodeList.交易方向_赎回确认.getValue());
        pageParam.put("PS_T24", DictCodeList.交易方向_赎回到账.getValue());
        pageParam.put("PS_T52", DictCodeList.交易方向_认购.getValue());
        pageParam.put("YES_NO_Y",DictCodeList.是否_是.getValue());
        pageParam.put("YES_NO_N",DictCodeList.是否_否.getValue());
        pageParam.put("FEE_TYPE_B05", BussinessDictCodeList.费用类型_佣金.getValue());

        return super.queryForPage(page,length,()-> trdFundValuationTradeMapper.selectDeal(pageParam));
    }

    public TrdFundValuationTradeDto queryTradeInfoByTradeId(Map<String, Object> params) {
        params.put("busiType_06",DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        params.put("busiType_07",DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        params.put("YES_NO_Y",DictCodeList.是否_是.getValue());
        params.put("YES_NO_N",DictCodeList.是否_否.getValue());
        params.put("FEE_TYPE_B05", BussinessDictCodeList.费用类型_佣金.getValue());
        return trdFundValuationTradeMapper.queryTradeInfoByTradeId(params);
    }

    public TrdFundValuationTradeDto queryRedInfoByTradeId(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId",tradeId);
        params.put("busiType_06",DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        params.put("busiType_07",DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        return trdFundValuationTradeMapper.queryRedInfoByTradeId(params);
    }

    /**
     * 赎回到账双击明细页面
     */
    public TrdFundValuationTradeDto queryRedInfoByOutTradeId(String tradeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId",tradeId);
        params.put("busiType_06",DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        params.put("busiType_07",DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
        return trdFundValuationTradeMapper.queryRedInfoByOutTradeId(params);
    }

    /**
     *
     * 工作流完结之后的操作
     * <功能详细描述>
     * @param result 实体
     * @see
     * @since 1.0
     */
    public int insert(TrdFundTradeResult result) {
        // 金融产品交易
        List<TrdProductDeal> listValFinProductDeal = result.getListValFinProductDeal();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();
            for (TrdProductDeal e : listValFinProductDeal) {
                listInsert.add(e);
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
               listInsert.add(e);
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
                listInsert.add(e);
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
                listInsert.add(e);
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valDealAccountingDetailBatchMapper.insertBatch(listInsert);
            }
        }

        //  账务明细代码表
        List<BokDetailId> listValBokDetailId = result.getListValBokDetailId();
        listValBokDetailId =
                interfCommonService.toRepeatBokDetailId(listValBokDetailId);
        // 入库
        if (CollectionUtils.isNotEmpty(listValBokDetailId)) {
            // 定义一个固定大小插入存放集合
            List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
            for (BokDetailId e : listValBokDetailId) {
                listInsert.add(e);
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
                listInsert.add(e);
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
                listInsert.add(e);
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
                listInsert.add(e);
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valOrdersExtBatchMapper.insertBatch(listInsert);
            }
        }
        return 1;
    }

    public int update(TrdFundTradeResult dto){
        TrdFundValuationTradeDto dto1 = new TrdFundValuationTradeDto();
        dto1.setProdId(dto.getTrdFundValuationTradeDto().getProdId());
        dto1.setTradeId(dto.getTrdFundValuationTradeDto().getTradeId());
        delete(dto1);
        return insert(dto);
    }

    public int delete(TrdFundValuationTradeDto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getProdId());
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 2.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 3.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 4.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 5.账务明细代码表 BOK_DETAIL_ID
        count += trdBondTradeMapper.delBokDetailIdByTradeId(params);
        // 6.交易费用明细表 TRD_TRADE_FEE_DETAIL
        count += valCommonBatchMapper.delTradeFeeDetailByTradeId(listDeal);
        // 7.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 8.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);

        return count;
    }




}
