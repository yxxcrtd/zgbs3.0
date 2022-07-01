package com.joyintech.tams.valuation.common.jixu.positionmake.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.PtlSecValution;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.jixu.positionmake.dao.JxPtlSecValutionMapper;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 头寸入库的接口（供几许调用）
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class JxPtlSecValutionService {

	@Autowired
	private JxPtlSecValutionMapper jxPtlSecValutionMapper;

	
	/**
	 * PTL_SEC_VALUTION表入值
	 * @param ptlSecValutionList
	 * 需要入库的组合资产估值头寸数据
	 * @param cdateNow
	 * 数据对应的日期
	 */
	public void insertBatch(List<PtlSecValution> ptlSecValutionList,Date cdateNow){
		List<List<PtlSecValution>> listForPtlSecValution = DataHandleUtil.splitList(ptlSecValutionList);
		// 进行删除操作
		for (List<PtlSecValution> listInsert : listForPtlSecValution) {
			if (CollectionUtils.isNotEmpty(listInsert)) {
				jxPtlSecValutionMapper.deletePtlSecValution(listInsert,cdateNow,DictCodeListExt1.生成方式_几许生成.getValue());
			}
		}
		// 进行插入操作
		for (List<PtlSecValution> listInsert : listForPtlSecValution) {
			if (CollectionUtils.isNotEmpty(listInsert)) {
				jxPtlSecValutionMapper.insertPtlSecValution(listInsert);
			}
		}
	}
	
	/**
	 * 根据账套代码查询组合代码
	 * @param booksetId
	 * @return
	 */
	public String queryPortFolioId(String booksetId){
		return jxPtlSecValutionMapper.queryPortFolioId(booksetId);
	}
}
