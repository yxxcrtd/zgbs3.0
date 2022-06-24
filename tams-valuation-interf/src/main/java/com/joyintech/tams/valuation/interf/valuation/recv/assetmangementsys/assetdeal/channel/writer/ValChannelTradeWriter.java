/*
 * 文件名：ValChannelTradeWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2019年3月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.channel.writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jixu.msg.dto.JJBkCashAcctDto;
import com.jixu.msg.dto.JJPortfolioDto;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductDealBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersExtBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTrdProductDealAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.channel.dto.ValChannelTradeProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.common.service.FrontDeskCommonService;

/**
 * 通道产品-交易数据接口 Writer
 * 
 * @author zhangwei
 * @version 1.0 2019年3月12日
 * @see ValChannelTradeWriter
 * @since 1.0
 */
@Service
public class ValChannelTradeWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 金融产品交易BatchMapper
     */
    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    /**
     * 金融产品交易扩展表BatchMapper
     */
    @Autowired
    private ValTrdProductDealAddBatchMapper valFinProductDealExtBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;
    
    /**
     * 通道核算信息_几许接口
     * add by zhangquan 20200107
     */
    @Autowired
    private FrontDeskCommonService frontDeskCommonService;
    
    /**
     * 入账信息BatchMapper
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 账务明细代码
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 指令
     */
    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    /**
     * 指令扩展
     */
    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;
    
    /**
     * 入账账务明细BatchMapper
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;
    

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    @Override
    @SuppressWarnings("unchecked")
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        String interfDate = new SimpleDateFormat("yyyyMMdd").format(interfExecuteInfo.getInterfDate());
        // 获取process返回组合的list值
        List<ValChannelTradeProcessorResult> resultList = (List<ValChannelTradeProcessorResult>)items;
        List<JJPortfolioDto> jJPortfolioDtoList = new ArrayList<JJPortfolioDto>();
        List<JJBkCashAcctDto> jJBkCashAcctDtoList = new ArrayList<JJBkCashAcctDto>();
        for (ValChannelTradeProcessorResult result : resultList) {
        	
        	/**add by zhangquan 20200107*/
        	if(!StringUtil.isEmptyList(jJPortfolioDtoList)) {
        		jJPortfolioDtoList.clear();
        	}
        	if(!StringUtil.isEmptyList(jJBkCashAcctDtoList)) {
        		jJBkCashAcctDtoList.clear();
        	}
        	//由于valuation-common无法访问几许中的dto，所以采用copy方式来定义对应的dto
            /** modify by yanya on 20210207 增加为空判定 start**/
            if(!StringUtil.isEmptyList(result.getjJProtfioList())) {
                BeanUtil.copyPropertiesIgnoreNull(jJPortfolioDtoList, result.getjJProtfioList());
                frontDeskCommonService.jixuPushProductInfo(jJPortfolioDtoList);
            }
            if(!StringUtil.isEmptyList(result.getjJBkCashAcctList())) {
                BeanUtil.copyPropertiesIgnoreNull(jJBkCashAcctDtoList, result.getjJBkCashAcctList());
                frontDeskCommonService.jixuPushFundAccountInfo(jJBkCashAcctDtoList);
            }
            /** modify by yanya on 20210207 增加为空判定 end**/
        	
            // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
            if (CollectionUtils.isNotEmpty(valInterflog)) {
                // 定义一个固定大小插入存放集合
                List<ValInterflog> listInsert = new ArrayList<ValInterflog>();

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

            String isDealTrade = result.getParamMap().get(InterfCommonConstant.读取文件状态) == null? DictCodeList.是否_否.getValue():(String)result.getParamMap().get(InterfCommonConstant.读取文件状态);
            //如果没有数据就跳出循环
            if(result.getDatanum() == 0 || CollectionUtils.isEmpty(result.getListValTrdProductDeal())) {
                if(DictCodeList.是否_是.getValue().equals(isDealTrade)){
                    interfCommonService.dealBatchTrdFinprodDeal(result.getParamMap(),interfDate);
                }
                continue;
            }

            // 重复导入删除逻辑调整
            interfCommonService.dealBatchTrdFinprodDeal(result.getParamMap(), interfDate);

            // 金融产品交易
            List<TrdProductDeal> listValFinProductDeal = result.getListValTrdProductDeal();
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
            List<TrdProductDealAdd> listValFinProductDealExt = result.getListValTrdProductDealExt();
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
                        valFinProductDealExtBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductDealExtBatchMapper.insertBatch(listInsert);
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
            List<BokDetailId> listValBokDetailId = interfCommonService.toRepeatBokDetailId(
                result.getListValBokDetailId());
            // 入库
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

            // 指令
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

            // 指令扩展
            List<StlOrdersExt> listValOrdersExt = result.getListValOrdersExt();
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
            /** modify by wujie on 20190320
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->初始化通道核算信息SHEET
            List<ValChannelProdDealDto> listValChannelProdDealDto = result.getListValChannelProdDealDto();
            if (CollectionUtils.isNotEmpty(listValChannelProdDealDto)) {
                interfCommonService.initChanneAaccounting(listValChannelProdDealDto, result.getResovleDate());
            }
            **/
        }

    }

}
