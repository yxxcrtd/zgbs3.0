/*
 * 文件名：AssetIntdateCarryoverReaderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.assetimpairmentprovision.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dao.AssetImpairmentProvisionMapper;
import com.joyintech.tams.valuation.batch.assetimpairmentprovision.dto.AssetImpairmentProvisionReaderResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;

/**
 * 资产减值计提批处理 - reader相关业务处理
 * 
 * @author chufengjie
 * @version 1.0 2020年10月13日
 * @see AssetImpairmentProvisionReaderService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class AssetImpairmentProvisionReaderService {

    /**
     * reader方法的 Service
     */
    @Autowired
    private AssetImpairmentProvisionMapper assetImpairmentProvisionMapper;
    
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 获取数据库数据
     * 
     * @param param 传递参数
     * @return List<BookDetailCompareIntoReadResult>
     * @see
     * @since 1.0
     */
    public List<AssetImpairmentProvisionReaderResult> execute(Map<String, Object> param) {
    	
        // 获取待处理任务信息
        //SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");  
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");
        //计提日批处理参数.发生日
        Date happenDate = (Date)param.get(BookConstant.HAPPEN_DATE);
        int year = DateUtil.getYear(DateUtil.format(happenDate));
        int month = DateUtil.getMonth(DateUtil.format(happenDate));
        int weekDay = DateUtil.getWeekDay(happenDate);
        //年末
        Date yearDate = DateUtil.parse(String.valueOf(year)+"-12-31");
        //季末
        Date seasonDate = DateUtil.addMonths(DateUtil.parse(String.valueOf(year)+ "-01-31"),((month-1)/3+1)*3-1);
        //月末
        Date monthDate = DateUtil.addMonths(DateUtil.parse(String.valueOf(year)+ "-01-31"),month-1);
        //测算频率列表
        List<String> lossTestRates = new ArrayList<String>();
        lossTestRates.add(DictCodeListExt3.减值测算频率_日.getValue());
        if(DateUtil.compareTo(happenDate,yearDate) == 0){
            lossTestRates.add(DictCodeListExt3.减值测算频率_年末.getValue());
        }
        if(DateUtil.compareTo(happenDate,seasonDate) == 0){
            lossTestRates.add(DictCodeListExt3.减值测算频率_季末.getValue());
        }
        if(DateUtil.compareTo(happenDate,monthDate) == 0){
            lossTestRates.add(DictCodeListExt3.减值测算频率_月末.getValue());
        }
        
        param.put("lossTestRates", lossTestRates);
        param.put("LOSS_TEST_RATE_04", DictCodeListExt3.减值测算频率_周.getValue());
        param.put("LOSS_TEST_RATE_06", DictCodeListExt3.减值测算频率_每次指定交易.getValue());
        param.put("LOSS_TEST_RATE_07", DictCodeListExt3.减值测算频率_每个开放日.getValue());
        param.put("LOSS_TEST_RATE_08", DictCodeListExt3.减值测算频率_不规则.getValue());
        param.put("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_债券.getValue());
        param.put("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        param.put("FINPROD_TYPE_F03", DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
        param.put("FINPROD_TYPE_F05", DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue());
        param.put("FINPROD_TYPE_F07", DictCodeListExt1.金融产品类型_存款.getValue());
        param.put("FINPROD_TYPE_F21", DictCodeListExt1.金融产品类型_同业借款.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("LOSS_TEST_RATE", ValDictCodeList.账套配置表参数项_减值测算频率.getValue());
        param.put("LOSS_TEST_DATE", ValDictCodeList.账套配置表参数项_减值测算日.getValue());
        param.put("LOSS_TEST_TREAD", ValDictCodeList.账套配置表参数项_触发减值交易类型.getValue());
        param.put("AFT_EXP_IS_CAL", ValDictCodeList.账套配置表参数项_资产到期之后是否计提减值.getValue());
        param.put("YES_OR_NO_N", DictCodeListExt1.是否_否.getValue());
        param.put("YES_OR_NO_Y", DictCodeListExt1.是否_是.getValue());
        param.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        param.put("OPENMODE_02", DictCodeList.开放模式_高频开放.getValue());
        
        param.put("weekDay", weekDay);
    	//获取查询参数
    	//减值测算频率
//    	String lossTestRate = CacheUtil.getSysParam("LOSS_TEST_RATE");
//    	//减值测算日
//    	String lossTestDate = CacheUtil.getSysParam("LOSS_TEST_DATE");
//    	//触发减值交易类型
//    	String lossTestTrade = CacheUtil.getSysParam("LOSS_TEST_TREAD");
//    	//计提日批处理参数.发生日
//    	//String happenDate =DateUtil.format(batchExecuteInfo.getExecuteDate());
//    	
//    	//定义 变量.计提日
//    	Date impairmentDate = null;
//    	//if 系统参数.减值测算频率 ="年" 
//		if (StringUtil.isNotEmpty(lossTestRate)
//				&& StringUtil.equals(DictCodeListExt1.周期单位_年.getValue(), lossTestRate)) {
//		    //变量.计提日 = 计提日批处理参数.发生日的年 拼接 系统参数.减值测算日	
//			impairmentDate =DateUtil.parse(DateUtil.getYear(happenDate) + "-" + lossTestDate);	
//		}
//		//else if 系统参数.减值测算频率 ="月" 
//		else if (StringUtil.isNotEmpty(lossTestRate)
//				&& StringUtil.equals(DictCodeListExt1.周期单位_月.getValue(), lossTestRate)){
//		    //变量.计提日 = 计提日批处理参数.发生日的年月 拼接 系统参数.减值测算日	
//			impairmentDate =DateUtil.parse(DateUtil.getYear(happenDate) + "-" + DateUtil.getMonth(happenDate) + "-" + lossTestDate);		
//		}
//        //变量.交易方向列表 = 触发减值交易类型按逗号分隔
//		List<String> tradeDirection = Arrays.asList(lossTestTrade.split(GlobalConstant.COMMA));
//        // 定义返回list
//       = new ArrayList<AssetImpairmentProvisionReaderResult>();
//		//if (计提日批处理参数.发生日 = 变量.计提日 or 系统参数.减值测算频率 =日)
//		 param.put("executeDate", batchExecuteInfo.getExecuteDate());
//		 param.put("tradeDirection", tradeDirection);		
//		if (DateUtil.compareTo((batchExecuteInfo.getExecuteDate()), impairmentDate) == 0
//				|| StringUtil.equals(DictCodeListExt1.周期单位_日.getValue(), lossTestRate)) {
//			resultList = assetImpairmentProvisionMapper.queryTrdAssetLossProvisionReader(param);
//		} else {
        // add by weipeng 20210915：增加模型法减值计提查询逻辑 start
        // 查询系统采用的减值方案
        List<AssetImpairmentProvisionReaderResult> resultList = null;
        String lossType = CacheUtil.getSysParam(DictCode.减值方案类型.getValue());
        if(lossType!=null && DictCodeListExt3.减值方案类型_普华.getValue().equals(lossType)){
            resultList = assetImpairmentProvisionMapper.queryTrdAssetLossReaderPH(param);
        }
        else  if(lossType!=null && DictCodeListExt3.减值方案类型_毕马威.getValue().equals(lossType)){
            resultList = assetImpairmentProvisionMapper.queryTrdAssetLossReaderBMW(param);
        }
        //  否则，继续走原来的逻辑
        else{
           resultList = assetImpairmentProvisionMapper.queryTrdAssetLossReader(param);
        }
        // add by weipeng 20210915：增加模型法减值计提查询逻辑 end
//		}
		
		// 获取场景代码会计分录
        if (CollectionUtils.isNotEmpty(resultList)) {
            // 获取分录信息
            Set<String> bookSetIds = new HashSet<String>();
            resultList.forEach(item -> {
                if (StringUtil.isNotEmpty(item.getBooksetId())) {
                    bookSetIds.add(item.getBooksetId());
                }
            });
            Map<String, TreeNode> nodes = bookCommonLogic.commonForGetTreeNodeMap(bookSetIds, batchTask.getTaskId());
            for (AssetImpairmentProvisionReaderResult currDTO : resultList) {
                currDTO.setBookSetTreeNode(nodes.get(currDTO.getBooksetId()));
            }
        }						
        return resultList;
    }
}
