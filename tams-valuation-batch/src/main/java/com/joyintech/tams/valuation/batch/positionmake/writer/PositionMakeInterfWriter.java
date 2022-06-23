package com.joyintech.tams.valuation.batch.positionmake.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeInterfWriterResult;
import com.joyintech.tams.valuation.batch.positionmake.service.PositionMakeInterfWriterService;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头寸生成接口 - writer
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class PositionMakeInterfWriter extends BaseWriter<PositionMakeInterfWriterResult> {

	@Autowired
	private PositionMakeInterfWriterService positionMakeInterfWriterService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends PositionMakeInterfWriterResult> items) throws Exception {
		
		// 获取process返回值
		List<PositionMakeInterfWriterResult> resultList = (List<PositionMakeInterfWriterResult>) items;
		if(CollectionUtils.isEmpty(resultList)){
			return;
		}
		PositionMakeInterfWriterResult result = resultList.get(0);
		Map<String,Object> parameter = new HashMap<String,Object>();
		if(CollectionUtils.isNotEmpty(result.getPortfolioIdList())) {
        	parameter.put("portfolioList",result.getPortfolioIdList());
        }else{
            parameter.put("portfolioList",new String []{});
        }
		if(CollectionUtils.isNotEmpty(result.getProdList())) {
        	parameter.put("prodList",result.getProdList());
        }else{
            parameter.put("prodList",new String []{});
        }
        parameter.put("cdate",DateUtil.format(result.getCdate()));
		
		positionMakeInterfWriterService.interfTaskRun(parameter, result.getCdate(), result.getSeqNo());
	}

}
