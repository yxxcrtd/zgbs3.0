/*
 * 文件名：TaxBalanceQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdintovaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdintovaluationduration.dao.TrdIntoValuationDuration001Mapper;
import com.joyintech.service.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDuration001Dto;
import com.joyintech.service.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDurationResult;
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


@Service(value="serviceTrdIntoValuationDuration001Service")
public class TrdIntoValuationDuration001Service extends CommonExportService<TrdIntoValuationDuration001Dto>{

    @Autowired
    private TrdIntoValuationDuration001Mapper trdIntoValuationDuration001Mapper;

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
     *
     * 获取分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     * @param pageParam Map<String, Object>
     * @return Page<MstSharePriceDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<TrdIntoValuationDuration001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {
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
        }
        pageParam.put("psArr", psArr);

        //传递默认值
        pageParam.put("PS_T29", DictCodeList.交易方向_利息返还.getValue());
        pageParam.put("PS_T32",DictCodeList.交易方向_本金返还.getValue());
        pageParam.put("busiType",DictCodeList.业务类型_债券借贷标的债券交易.getValue());
        pageParam.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());

        return this.queryForPage(page, length,
                () -> trdIntoValuationDuration001Mapper.selectDeal(pageParam));
    }

    /**
     *
     * 获取不分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     */
    public List<TrdIntoValuationDuration001Dto> queryPageList(Map<String, Object> params)  {
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);
        //资产代码
        String asset = (String)params.get("asset");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(asset)){
            assetIdArr = asset.split(",");
        }
        params.put("assetIdArr", assetIdArr);
        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        //传递默认值
        params.put("PS_T29", DictCodeList.交易方向_利息返还.getValue());
        params.put("PS_T32",DictCodeList.交易方向_本金返还.getValue());
        params.put("busiType",DictCodeList.业务类型_债券借贷标的债券交易.getValue());
        params.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());

        return trdIntoValuationDuration001Mapper.selectDeal(params);
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
            String nextId = this.getNextId("RONGRUTRADEID_SEQUENCE");
            //平台日
            String dateStr= DateUtil.format(iPlatformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);//yyyyMMdd
            // 拼接
            tradeId = "JY"+"RONGRU"+dateStr+ nextId;
            // 查询是否重复
            int num = trdIntoValuationDuration001Mapper.selectCountByTradeId(tradeId);
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
        return trdIntoValuationDuration001Mapper.getNextId(sequenceName);
    }


    //根据交易编号查询
    public TrdIntoValuationDuration001Dto queryTradeInfoByTradeId(Map<String, Object> param) {
        return trdIntoValuationDuration001Mapper.queryTradeInfoByTradeId(param);
    }

    //删除
    public int delete(TrdIntoValuationDuration001Dto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getProdId());   //产品代码
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 2.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 3.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 4.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
        // 5.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 6.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 7.账务明细代码表 BOK_DETAIL_ID
        count += trdBondTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }

    public int insert(TrdIntoValuationDurationResult result){
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
        //账务明细代码表
        List<BokDetailId> listValBokDetailId =
                interfCommonService.toRepeatBokDetailId(result.getListValBokDetailId());
        //入库
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
        //结算指令表
        List<StlOrders>  ValOrdersList=result.getValOrdersList();
        if (CollectionUtils.isNotEmpty(ValOrdersList)) {
            // 定义一个固定大小插入存放集合
            List<StlOrders> listInsert = new ArrayList<StlOrders>();

            for (StlOrders e : ValOrdersList) {
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
        //结算指令拓展表
        List<StlOrdersExt> ValOrdersExtList=result.getValOrdersExtList();

        if (CollectionUtils.isNotEmpty(ValOrdersExtList)) {
            // 定义一个固定大小插入存放集合
            List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();

            for (StlOrdersExt e : ValOrdersExtList) {
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
        return 1;
    }

    public int update(TrdIntoValuationDurationResult dto){
        TrdIntoValuationDuration001Dto dto1 = new TrdIntoValuationDuration001Dto();
        dto1.setProdId(dto.getTrdIntoValuationDuration001Dto().getProdId());
        dto1.setTradeId(dto.getTrdIntoValuationDuration001Dto().getTradeId());
        delete(dto1);
        return insert(dto);
    }
}
