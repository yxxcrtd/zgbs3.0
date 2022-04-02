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
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade001Result;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dao.TrdRepoValuationTrade001Mapper;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTrade001Result;
import com.joyintech.service.valuation.trademanage.trdrepovaluationtrade.dto.TrdRepoValuationTradeResult;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt3;
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
     * 交易类金融产品Mapper
     */
    @Autowired
    private ValTradeFinProductBatchMapper valTradeFinProductBatchMapper;

    /**
     * 金融产品分类BatchMapper
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 交易类金融产品附属表batchMapper
     */
    @Autowired
    private FinTradeProductAddBatchMapper finTradeProductAddBatchMapper;


    /**
     * 质押品产品信息batchMapper
     */
    @Autowired
    private FinfDealPledgeBatchMapper finfDealPledgeBatchMapper;

    /**
     * 金融产品估值参数表
     * add by wujie on 20190420
     */
    @Autowired
    private ValFinProductValuationParaBatchMapper valFinProductValuationParaBatchMapper;


    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;



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


    public int insert01(TrdRepoValuationTrade001Result result){

        int maxSqlNum =100;
        // 交易类金融产品
        List<FinTradeProduct> listValTradeFinProduct = result.getListValTradeFinProduct();
        if (CollectionUtils.isNotEmpty(listValTradeFinProduct)) {
            Map<String,FinTradeProduct> map_CQ = new HashMap<String,FinTradeProduct>();
            List<FinTradeProduct> listValTradeFinProduct_QC= new ArrayList<FinTradeProduct>();
            for(FinTradeProduct obj :listValTradeFinProduct){
                if(map_CQ.get(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch()) ==null){
                    listValTradeFinProduct_QC.add(obj);
                }
                map_CQ.put(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch(), obj);
            }
            // 定义一个固定大小插入存放集合
            List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
            for (FinTradeProduct e : listValTradeFinProduct_QC) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valTradeFinProductBatchMapper.deleteBatch(listInsert);
                    valTradeFinProductBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeListExt3.共通业务类型_回购标的信息.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valTradeFinProductBatchMapper.deleteBatch(listInsert);
                valTradeFinProductBatchMapper.insertBatch(listInsert);
            }
            if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                List<String> editOrgCodeList = new ArrayList<>();
                for (FinTradeProduct e : listValTradeFinProduct_QC) {
                    editOrgCodeList.clear();
                    if(com.github.pagehelper.util.StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                        editOrgCodeList.add(e.getBelongInstitutionCode());
                    }
                    multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                            DictCodeListExt3.共通业务类型_回购类资产.getValue());
                }
            }
        }

        // 金融产品分类
        List<FinProductType> listValFinProductType = result.getListValFinProductType();
        if (CollectionUtils.isNotEmpty(listValFinProductType)) {
            Map<String,FinProductType> map_CQ = new HashMap<String,FinProductType>();
            List<FinProductType> listValFinProductType_CQ= new ArrayList<FinProductType>();
            for(FinProductType obj :listValFinProductType){
                if(map_CQ.get(obj.getFinprodId()) ==null){
                    listValFinProductType_CQ.add(obj);
                }
                map_CQ.put(obj.getFinprodId(), obj);
            }
            // 定义一个固定大小插入存放集合
            List<FinProductType> listInsert = new ArrayList<FinProductType>();
            for (FinProductType e : listValFinProductType_CQ) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valFinProductTypeBatchMapper.deleteBatch(listInsert);
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valFinProductTypeBatchMapper.deleteBatch(listInsert);
                valFinProductTypeBatchMapper.insertBatch(listInsert);
            }
        }

        // 交易类金融产品附属表
        List<FinTradeProductAdd> listFinTradeProductAdd = result.getListFinTradeProductAdd();
        if (CollectionUtils.isNotEmpty(listFinTradeProductAdd)) {
            Map<String,FinTradeProductAdd> map_CQ = new HashMap<String,FinTradeProductAdd>();
            List<FinTradeProductAdd> listFinTradeProductAdd_CQ= new ArrayList<FinTradeProductAdd>();
            for(FinTradeProductAdd obj :listFinTradeProductAdd){
                if(map_CQ.get(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch()) ==null){
                    listFinTradeProductAdd_CQ.add(obj);
                }
                map_CQ.put(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch(), obj);
            }
            // 定义一个固定大小插入存放集合
            List<FinTradeProductAdd> listInsert = new ArrayList<FinTradeProductAdd>();
            for (FinTradeProductAdd e : listFinTradeProductAdd_CQ) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    finTradeProductAddBatchMapper.deleteBatch(listInsert);
                    finTradeProductAddBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                finTradeProductAddBatchMapper.deleteBatch(listInsert);
                finTradeProductAddBatchMapper.insertBatch(listInsert);
            }
        }
        //金融产品估值参数表
        List<FinProductValuationPara> listFinProductValuationPara = result.getListFinProductValuationPara();
        // 入库
        if (CollectionUtils.isNotEmpty(listFinProductValuationPara)) {
            // 定义一个固定大小插入存放集合
            List<FinProductValuationPara> listInsert = new ArrayList<FinProductValuationPara>();
            for (FinProductValuationPara e : listFinProductValuationPara) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
                    valFinProductValuationParaBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
                valFinProductValuationParaBatchMapper.insertBatch(listInsert);
            }
        }

        // 质押品产品信息扩展表
        List<FinDealPledge> listFinDealPledge = result.getListFinDealPledge();
        // 入库
        if (CollectionUtils.isNotEmpty(listFinDealPledge)) {
            Map<String,FinDealPledge> mapDeal = new HashMap<String,FinDealPledge>();
            List<FinDealPledge> delList = new ArrayList<FinDealPledge>();

            Map<String,FinDealPledge> map_CQ = new HashMap<String,FinDealPledge>();
            List<FinDealPledge> listFinDealPledge_CQ= new ArrayList<FinDealPledge>();
            for(FinDealPledge obj :listFinDealPledge){
                String key = obj.getFinprodId()+obj.getBranch()+obj.getPledgeFinprodId();
                if(map_CQ.get(key) ==null){
                    listFinDealPledge_CQ.add(obj);
                }
                map_CQ.put(key, obj);

                if(mapDeal.get(obj.getFinprodId()) == null){
                    delList.add(obj);
                }
                mapDeal.put(obj.getFinprodId(), obj);
            }
            //根据金融产品代码删除抵押品
            valCommonBatchMapper.deleteBatchPledgeById(delList);


            // 定义一个固定大小插入存放集合
            List<FinDealPledge> listInsert = new ArrayList<FinDealPledge>();
            for (FinDealPledge e : listFinDealPledge_CQ) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    finfDealPledgeBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                finfDealPledgeBatchMapper.insertBatch(listInsert);
            }
        }
        // 日志
        List<ValInterflog> valInterflog = result.getValInterflog();
        // 入库
        if (CollectionUtils.isNotEmpty(valInterflog)) {
            // 定义一个固定大小插入存放集合
            List<ValInterflog> listInsert = new ArrayList<>();
            for (ValInterflog e : valInterflog) {
                // 小于最大值
                if (listInsert.size() < maxSqlNum) {
                    listInsert.add(e);
                }
                else if (listInsert.size() >= maxSqlNum) {
                    valInterflogBatchMapper.insertBatch(listInsert);
                    listInsert.clear();
                    listInsert.add(e);
                }
            }
            // 如果循环结束，小于最大值
            if (CollectionUtils.isNotEmpty(listInsert)) {
                valInterflogBatchMapper.insertBatch(listInsert);
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

    public int update01(TrdRepoValuationTrade001Result dto){
        TrdRepoValuationTrade001Dto dto1 = new TrdRepoValuationTrade001Dto();
        dto1.setPortfolioId(dto.getTrdRepoValuationTrade001Dto().getPortfolioId());
        dto1.setTradeId(dto.getTrdRepoValuationTrade001Dto().getTradeId());
        delete(dto1);
        return insert01(dto);
    }

}
