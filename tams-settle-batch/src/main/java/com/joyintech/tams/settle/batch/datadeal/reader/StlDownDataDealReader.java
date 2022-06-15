package com.joyintech.tams.settle.batch.datadeal.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StepContext;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealReaderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据清算批处理step2 reader
 * 
 * @author chenzhuo
 * @date 2020/7/7
 * @see StlDownDataDealReader
 * @since 1.0
 */
@Service
@Lazy
public class StlDownDataDealReader extends BaseReader<StlDownDataDealOperatorDto> {
    
	@Autowired
    private StlDownDataDealReaderService stlDownDataDealService;
	
	@Override
    public List<StlDownDataDealOperatorDto> createList() {
		// 批处理执行信息
        SysBatchExecInfo execInfo = getBatchExecuteInfo();
        // 数据清算参数表
        //List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(execInfo.getExecuteDate());
        // 组合代码List
        List<String> portfolioIdList = new ArrayList<String>();
        // 证券品种List
        List<String> secTypeList = new ArrayList<String>();
        
        List<StlDownDataDealOperatorDto> result = new ArrayList<StlDownDataDealOperatorDto>();
        
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
						StepContext.start(settleBatchPara.getSecType());
		        	}
	        	}
	        }
	        result = stlDownDataDealService.generatorStlDownDataDealOperatorDto(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
        }*/

		secTypeList = Arrays.asList(DictCodeListExt1.证券品种_债券.getValue(),DictCodeListExt1.证券品种_基金.getValue(),
				DictCodeListExt1.证券品种_股票.getValue(),DictCodeListExt1.证券品种_回购.getValue(),
				DictCodeListStl.证券品种_信用保护凭证.getValue());
		StepContext.start(DictCodeListExt1.证券品种_债券.getValue());
		StepContext.start(DictCodeListExt1.证券品种_基金.getValue());
		StepContext.start(DictCodeListExt1.证券品种_股票.getValue());
		StepContext.start(DictCodeListExt1.证券品种_回购.getValue());
		StepContext.start(DictCodeListStl.证券品种_信用保护凭证.getValue());
		result = stlDownDataDealService.generatorStlDownDataDealOperatorDto(execInfo.getExecuteDate(), portfolioIdList, secTypeList);
        if(CollectionUtils.isNotEmpty(result)) {
        	// 根据证券品种初始化
        	result.forEach(item -> {
				if(CollectionUtils.isNotEmpty(portfolioIdList)) {
					StepContext.get(item.getFinprodType()).setPortfolioId(portfolioIdList);
				}
			});
		}
        return result;
	}
}
