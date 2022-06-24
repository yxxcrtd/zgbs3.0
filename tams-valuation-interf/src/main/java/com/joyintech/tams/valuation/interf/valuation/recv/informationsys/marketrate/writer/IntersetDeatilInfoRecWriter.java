package com.joyintech.tams.valuation.interf.valuation.recv.informationsys.marketrate.writer;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValIntRateBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.informationsys.marketrate.dto.InterestDetailInfoProcessorResult;
import com.joyintech.tams.common.base.entity.MstIntRateInfo;
import com.joyintech.tams.common.base.entity.ValInterflog;

/**
 * 利率行情接口Writer
* @author dongxiangyang
* @version 1.0 2018年7月06日
* @see BookGenerateResultForCurrentCommonWriter
* @since 1.0
*/
@Service
@Scope("prototype")
public class IntersetDeatilInfoRecWriter extends InterfBaseWriter<BaseEntity> {

	 /**
     * 利率行情mapper
     */
	@Autowired
    private ValIntRateBatchMapper valIntRateBatchMapper;
//	 /**
//     * 行情信息mapper
//     */
//	@Autowired
//    private ValRateInfoBatchMapper valRateInfoBatchMapper;
	 /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
	 /**
     * 参数
     */
    @SuppressWarnings("unused")
    private Map<String, Object> param;

    /**
     * 设置 param
     * 
     * @param param param
     */
    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends BaseEntity> arg0) throws Exception {
    	// 获取process返回组合的list值
		List<InterestDetailInfoProcessorResult> resultList=(List<InterestDetailInfoProcessorResult>)arg0;
		
		// 循环processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
		for(InterestDetailInfoProcessorResult result :resultList ){
			//利率行情
			List<MstIntRateInfo> valIntRateQuoteList=result.getListvalIntRateQuote();
			// 入库
            if (CollectionUtils.isNotEmpty(valIntRateQuoteList)){
            	valIntRateBatchMapper.insertBatch(valIntRateQuoteList);
            }
            // 日志
            List<ValInterflog> valInterflog = result.getListValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
                valInterflogBatchMapper.insertBatch(valInterflog);
            }
		}
	}

}
