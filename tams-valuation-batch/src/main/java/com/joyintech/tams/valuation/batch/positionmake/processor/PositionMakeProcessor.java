package com.joyintech.tams.valuation.batch.positionmake.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeReaderResult;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeWriterResult;
import com.joyintech.tams.valuation.batch.positionmake.service.PositionMakeProcessorNewService;
import com.joyintech.tams.valuation.batch.positionmake.service.PositionMakeProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头寸生成批处理 - processor
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class PositionMakeProcessor extends BaseProcessor<NeedOperateEntity, PositionMakeWriterResult> {

	@Autowired
	private PositionMakeProcessorService positionMakeProcessorService;

	@Autowired
	private PositionMakeProcessorNewService positionMakeProcessorNewService;

	@Override
	public PositionMakeWriterResult process(NeedOperateEntity items) throws Exception {

		// 获取reader方法的返回值
		PositionMakeReaderResult acctEntity = (PositionMakeReaderResult) items;
		Map<String, Object> thisParams = new HashMap<>();
		thisParams.putAll(this.getParams());
		// 将返回值塞入参数中
		thisParams.put(GlobalConstant.READER_RESULT, acctEntity);

		// process返回值
		PositionMakeWriterResult result = new PositionMakeWriterResult();
		// modify by weipeng on 20220312 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 start
		// 获取系统参数：会计分录类型：01 按科目分录；02 按内部科目编码分录 start
		String accountEntryType =  CacheUtil.getSysParam("ACCOUNT_ENTRY_TYPE");
		// 若按照内部科目编码分录，则需要映射分录对应科目信息.
		//调用service方法进行逻辑逻辑
		if(DictCodeListExt4.会计分录类型_按内部科目编码分录.getValue().equals(accountEntryType)){
			result = positionMakeProcessorNewService.execute(thisParams);
		}
		else{
			result = positionMakeProcessorService.execute(thisParams);
		}
		// modify by weipeng on 20220312 会计分录生成凭证通过系统参数区分新旧逻辑，保持原有逻辑不动 end

		// 需要插入
		result.setProdId(acctEntity.getBooksetId());

		return result;
	}

}
