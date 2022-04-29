/*
 * 文件名：ProBenefitPrdValuationFeeBeforeWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.investratechange.writer;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.dao.MstValIrsMapper;
import com.joyintech.tams.common.base.entity.MstValIrs;
import com.joyintech.tams.common.base.entity.MstValIrsKey;
import com.joyintech.tams.valuation.batch.investratechange.dto.InvestRateChangeResult;

/**
 * (利率交换) - writer
 * 
 * @author 陶然
 * @version 1.0 20200114
 * @see InvestRateChangeWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class InvestRateChangeWriter extends BaseWriter<NeedToWriteEntity> {

	@Autowired
	private MstValIrsMapper mstValIrsMapper;

	@Override
	public void write(List<? extends NeedToWriteEntity> items) throws Exception {
		List<InvestRateChangeResult> resultList = (List<InvestRateChangeResult>) items;
		for (InvestRateChangeResult dto : resultList) {
			List<MstValIrs> list = dto.getMstValIrsList();
			for(MstValIrs mstValIrs : list) {
				MstValIrsKey mstValIrsKey = new MstValIrsKey();
				mstValIrsKey.setFinprodId2(mstValIrs.getFinprodId2());
				mstValIrsKey.setValDate(mstValIrs.getValDate());
				mstValIrsKey.setDatasourceType(mstValIrs.getDatasourceType());
				mstValIrsKey.setValmodelId(mstValIrs.getValmodelId());
				mstValIrsMapper.deleteByPrimaryKey(mstValIrsKey);
				mstValIrsMapper.insertSelective(mstValIrs);
			}
		}
	}
}
