package com.joyintech.tams.valuation.batch.positionmake.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.positionmake.dao.PositionMakeWithoutDispatchMapper;
import com.joyintech.tams.valuation.batch.positionmake.dto.PositionMakeWriterResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 头寸生成批处理 - writer
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class PositionMakeWriter extends BaseWriter<NeedToWriteEntity> {

	@Autowired
	private PositionMakeWithoutDispatchMapper positionMakeWithoutDispatchMapper;

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends NeedToWriteEntity> items) throws Exception {
		
		// 获取task传递参数
		Map<String, Object> params = this.getParams();
		// 删除日
		Date cdateNow =  (Date) params.get(BookConstant.BOOKSET_DATE);
		// 保证日期不能为空 
		if(cdateNow == null){
			return;
		}
		
		// 获取process返回值
		List<PositionMakeWriterResult> resultList = (List<PositionMakeWriterResult>) items;

		// 组合资产估值【PTL_SEC_VALUTION】
		List<PtlSecValution> ptlSecValutionList = new ArrayList<>();
		// 产品费用头寸【BOK_POSITION_PRDFEE】
		List<BokPositionPrdfee> bokPositionPrdfeeList = new ArrayList<>();
		// 产品头寸【BOK_POSITION_PRD】
		List<BokPositionPrd> bokPositionPrdList = new ArrayList<>();
		// 组合费用头寸【PTL_FEE_POSITION】
		List<PtlFeePosition> ptlFeePositionList = new ArrayList<>();
		// 组合估值头寸【PTL_PORTFOLIO_VALUATION】
		List<PtlPortfolioValuation> ptlPortfolioValuationList = new ArrayList<>();
		//组合资产应收款头寸LIST 【PTL_SEC_RECEIVABLES】 add by yanya on 20191225
		List<PtlSecReceivables> ptlSecReceivablesList = new ArrayList<>();

		// reader返回值中只有一个，故只有一个值，不存在多个。
		if(resultList!=null && !resultList.isEmpty()){
			for(PositionMakeWriterResult item : resultList){
				// 组合资产估值【PTL_SEC_VALUTION】
				if(CollectionUtils.isNotEmpty(item.getPtlSecValutionList())){
					ptlSecValutionList.addAll(item.getPtlSecValutionList());
				}
				// 产品费用头寸【BOK_POSITION_PRDFEE】
				if(CollectionUtils.isNotEmpty(item.getBokPositionPrdfeeList())){
					bokPositionPrdfeeList.addAll(item.getBokPositionPrdfeeList());
				}
				// 产品头寸【BOK_POSITION_PRD】
				if(CollectionUtils.isNotEmpty(item.getBokPositionPrdList())){
					bokPositionPrdList.addAll(item.getBokPositionPrdList());
				}
				// 组合费用头寸【PTL_FEE_POSITION】
				if(CollectionUtils.isNotEmpty(item.getPtlFeePositionList())){
					ptlFeePositionList.addAll(item.getPtlFeePositionList());
				}
				// 组合估值头寸【PTL_PORTFOLIO_VALUATION】
				if(CollectionUtils.isNotEmpty(item.getPtlPortfolioValuationList())){
					ptlPortfolioValuationList.addAll(item.getPtlPortfolioValuationList());
				}
				//组合资产应收款头寸LIST 【PTL_SEC_RECEIVABLES】
				if(CollectionUtils.isNotEmpty(item.getPtlSecReceivablesList())){
					ptlSecReceivablesList.addAll(item.getPtlSecReceivablesList());
				}
			}


			List<List<PtlSecValution>> listForPtlSecValution = DataHandleUtil.splitList(ptlSecValutionList);
			// 进行删除操作
			for (List<PtlSecValution> listInsert : listForPtlSecValution) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deletePtlSecValution(listInsert,cdateNow,DictCodeListExt1.生成方式_批处理生成.getValue());
				}
			}
			// 进行插入操作
			for (List<PtlSecValution> listInsert : listForPtlSecValution) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
				    listInsert = 
				        listInsert.stream().filter(item->StringUtil.isNotEmpty(item.getFinprodId())).collect(Collectors.toList());
					positionMakeWithoutDispatchMapper.insertPtlSecValution(listInsert);
				}
			}

			// 产品费用头寸【BOK_POSITION_PRDFEE】
			List<List<BokPositionPrdfee>> listForBokPositionPrdfee = DataHandleUtil.splitList(bokPositionPrdfeeList);
			// 进行删除操作
			for (List<BokPositionPrdfee> listInsert : listForBokPositionPrdfee) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deleteBokPositionPrdfee(listInsert,cdateNow);
				}
			}
			// 进行插入操作
			for (List<BokPositionPrdfee> listInsert : listForBokPositionPrdfee) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.insertBokPositionPrdfee(listInsert);
				}
			}

			// 产品头寸【BOK_POSITION_PRD】
			List<List<BokPositionPrd>> listForBokPositionPrd = DataHandleUtil.splitList(bokPositionPrdList);
			// 进行删除操作
			for (List<BokPositionPrd> listInsert : listForBokPositionPrd) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deleteBokPositionPrd(listInsert,cdateNow);
				}
			}
			// 进行插入操作
			for (List<BokPositionPrd> listInsert : listForBokPositionPrd) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.insertBokPositionPrd(listInsert);
				}
			}

			// 组合费用头寸【PTL_FEE_POSITION】
			List<List<PtlFeePosition>> listForPtlFeePosition = DataHandleUtil.splitList(ptlFeePositionList);
			// 进行删除操作
			for (List<PtlFeePosition> listInsert : listForPtlFeePosition) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deletePtlFeePosition(listInsert,cdateNow);
				}
			}
			// 进行插入操作
			for (List<PtlFeePosition> listInsert : listForPtlFeePosition) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.insertPtlFeePosition(listInsert);
				}
			}

			// 组合估值头寸【PTL_PORTFOLIO_VALUATION】
			List<List<PtlPortfolioValuation>> listForPtlPortfolioValuation = DataHandleUtil.splitList(ptlPortfolioValuationList);
			// 进行删除操作
			for (List<PtlPortfolioValuation> listInsert : listForPtlPortfolioValuation) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deletePtlPortfolioValuation(listInsert,cdateNow);
				}
			}
			// 进行插入操作
			for (List<PtlPortfolioValuation> listInsert : listForPtlPortfolioValuation) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.insertPtlPortfolioValuation(listInsert);
				}
			}

			//组合资产应收款头寸LIST 【PTL_SEC_RECEIVABLES】 add by yanya on 20191225
			List<List<PtlSecReceivables>> listForPtlSecReceivables = DataHandleUtil.splitList(ptlSecReceivablesList);
			// 进行删除操作
		//del by xr  删除逻辑当调整交易 重新跑批时，此时若reader返回为空会导致旧数据无法删除 start
	/*		for (List<PtlSecReceivables> listInsert : listForPtlSecReceivables) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.deletePtlSecReceivables(listInsert, cdateNow);
				}
			}*/
			//del by xr  删除逻辑当调整交易 重新跑批时，此时若reader返回为空会导致旧数据无法删除  end
			// 进行插入操作
			for (List<PtlSecReceivables> listInsert : listForPtlSecReceivables) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					positionMakeWithoutDispatchMapper.insertPtlSecReceivables(listInsert);
				}
			}
			
		}

	}

}
