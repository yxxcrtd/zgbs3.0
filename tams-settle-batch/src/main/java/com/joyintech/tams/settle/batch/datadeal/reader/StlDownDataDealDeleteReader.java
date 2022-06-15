package com.joyintech.tams.settle.batch.datadeal.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.SettleTransDataTotalMatch;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealDeleteOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealReaderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据清算批处理step1数据删除reader
 * 
 * @author chenzhuo
 * @date 2020/7/7
 * @see StlDownDataDealDeleteReader
 * @since 1.0
 */
@Service
@Lazy
public class StlDownDataDealDeleteReader extends BaseReader<StlDownDataDealDeleteOperatorDto> {
    
	@Autowired
    private StlDownDataDealReaderService stlDownDataDealService;
	
	@Override
    public List<StlDownDataDealDeleteOperatorDto> createList() {
		// 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 数据清算参数表
        //List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(execInfo.getExecuteDate());
        // 组合代码List
        List<String> portfolioIdList = new ArrayList<String>();
        // 证券品种List
        List<String> secTypeList = new ArrayList<String>();
        // 其他业务类型List
        List<String> otherBusinessTypeList = new ArrayList<String>();
        // 新股申购业务类型List
        List<String> xgsgBusinessTypeList = new ArrayList<String>();
        
        /*if (list != null && list.size() > 0) {
	        for (SettleBatchPara settleBatchPara : list) {
	        	// 组合代码
	        	if (StringUtil.isNotEmpty(settleBatchPara.getPortfolioId())) {
		        	if (!portfolioIdList.contains(settleBatchPara.getPortfolioId())) {
		        		portfolioIdList.add(settleBatchPara.getPortfolioId());
		        	}
	        	}
	        	// 证券品种
	        	if (StringUtil.isNotEmpty(settleBatchPara.getSecType())) {
		        	if (!secTypeList.contains(settleBatchPara.getSecType())) {
		        		// 证券品种是债券、股票、基金、回购时才处理
		        		if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), settleBatchPara.getSecType()) ||
		        			StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), settleBatchPara.getSecType()) ||	
		        			StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), settleBatchPara.getSecType()) ||	
		        			StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), settleBatchPara.getSecType()))
		        		secTypeList.add(settleBatchPara.getSecType());
		        	}
	        	}
	        	// 债券的话，变量.其他业务类型List.add(数据字典：金融产品其他代码类型.配债)
	        	if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), settleBatchPara.getSecType())) {
	        		otherBusinessTypeList.add(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
	        	}
	        	// 股票的话，变量.其他业务类型List.add(数据字典：金融产品其他代码类型.配股)。变量.新股申购业务类型List.add(数据字典：金融产品其他代码类型.新股申购)
	        	if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), settleBatchPara.getSecType())) {
	        		otherBusinessTypeList.add(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
	        		xgsgBusinessTypeList.add(DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
	        	}
	        }*/
			secTypeList = Arrays.asList(DictCodeListExt1.证券品种_债券.getValue(),DictCodeListExt1.证券品种_基金.getValue(),
					DictCodeListExt1.证券品种_股票.getValue(),DictCodeListExt1.证券品种_回购.getValue(),
					DictCodeListStl.证券品种_信用保护凭证.getValue());
			otherBusinessTypeList = Arrays.asList(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue()
					// GNS-853:重复跑批，删除配股数据 modify by fuyongsheng 20220214 start
					,DictCodeListExt3.金融产品其他代码类型_配股代码.getValue()
					// GNS-853:重复跑批，删除配股数据 modify by fuyongsheng 20220214 end
			);
			// GZYHTZJY-1136:删除上市流通数据 modify fuyongsheng 20220115 start
			xgsgBusinessTypeList = Arrays.asList(DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue(), DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue());
			// GZYHTZJY-1136:删除上市流通数据 modify fuyongsheng 20220115 end

			// 删除清算相关表
			List<SettleTransDataTotalMatch> settleTransDataTotalMatches = stlDownDataDealService.selectNeedRevokeSettleTransDataTotals(
					execInfo.getExecuteDate(), portfolioIdList, secTypeList);
			stlDownDataDealService.transactionMacthRevokeBatch(settleTransDataTotalMatches);

	        // 删除【成交数据与投管交易关系】
	        stlDownDataDealService.deleteSettleTransTgMapping(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
	        
	        // 删除【成交数据明细质押品信息表】
	        stlDownDataDealService.deleteSettleTransDataPledge(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
	        
	        // 删除【成交数据明细附属表】
	        stlDownDataDealService.deleteSettleTransDataAdd(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
	        
	        // 删除【佣金计算过程表】
	        stlDownDataDealService.deleteCommisionCalcProcess(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
	        
	        // 删除【成交数据明细表】
	        stlDownDataDealService.deleteSettleTransData(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
	        
	        // 删除交易所品种额度（其他业务类型List）
	        stlDownDataDealService.deleteFinProdQuotByOthBussType(execInfo.getExecuteDate(), portfolioIdList, otherBusinessTypeList);
	        
	        // 删除交易所品种额度（新股申购业务类型List）
	        stlDownDataDealService.deleteFinProdQuotByXgsgBussType(execInfo.getExecuteDate(), xgsgBusinessTypeList);

			// 删除【送股权益登记】
		    stlDownDataDealService.deleteShareBonusRegByCdate(execInfo.getExecuteDate());

			// 删除【基金分红公告】
		    stlDownDataDealService.deletemstFundBonusNoticeByCdate(execInfo.getExecuteDate());
        	return new ArrayList<StlDownDataDealDeleteOperatorDto>();
	}
}
