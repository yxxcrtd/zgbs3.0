package com.joyintech.tams.valuation.batch.common.bookgenerateresult;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.dao.BokStatBokPositionMapper;
import com.joyintech.tams.common.base.dao.BokStatFeePosiitonMapper;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.common.base.entity.BokStatFeePosiiton;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokStatFeePosition;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * 记账凭证返回实体类 - 公用方法写入 (针对账务明细当前表) 包括 插入【统计分析明细表（费用）】数据
 * 
 * @author Administrator
 *
 */
@Service
@Scope("prototype")
public class BookGenerateResultForCurrentCommonWriterWithBokStatFeePosition extends BaseWriter<NeedToWriteEntity> {

	/**
	 * 记账凭证生成类
	 */
	@Autowired
	private BookAccountGenLogic bookAccountGenLogic;

	/**
	 * 【统计分析明细表（费用）】数据操作
	 */
	@Autowired
	private BokStatFeePosiitonMapper bokStatFeePosiitonMapper;
	
	/**
     * 统计分析明细表Mapper
     */
    @Autowired
    private BokStatBokPositionMapper bokStatBokPositionMapper;

	/**
	 * 记账凭证数据写入数据库(针对账务明细当前表) {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends NeedToWriteEntity> items) throws Exception {

		// 获取process返回组合的list值 - 记账凭证list
		List<BookGenerateResultWithBokStatFeePosition> resultList = (List<BookGenerateResultWithBokStatFeePosition>) items;

		// 第一步：此处和【BookGenerateResultForCurrentCommonWriter】逻辑一致
		// 循环记账凭证list，对每个记账凭证进行操作，
		for (BookGenerateResultWithBokStatFeePosition bookGenerateResult : resultList) {
			// 插入记账凭证主表信息 和 插入记账凭证明细表信息
			bookAccountGenLogic.insertKeepAccount(bookGenerateResult);
			/**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  start**/
            BokStatBokPosition bokStatBokPosition = bookGenerateResult.getBokStatBokPosition();
            if(bokStatBokPosition != null){
                bokStatBokPositionMapper.updateByPrimaryKeySelective(bokStatBokPosition);      
            }
            /**add by yanya on 20190924 邮储个性化：净值产品记录年化收益率R 根据ycbranch20181218版本合并  end**/
		}

		// 第二步：循环数据，插入【统计分析明细表（费用）】表
		for (BookGenerateResultWithBokStatFeePosition bookGenerateResult : resultList) {
			this.insertBokStatFeePosition(bookGenerateResult.getBokStatFeePosiitonList());
		}
	}

	/**
	 * 插入【统计分析明细表（费用）】数据
	 * 
	 * @param bokStatFeePosiitonList
	 * @return
	 */
	private int insertBokStatFeePosition(List<BokStatFeePosiiton> bokStatFeePosiitonList) {

		int sucessNum = 0;

		// 数据存在
		if (bokStatFeePosiitonList != null && !bokStatFeePosiitonList.isEmpty()) {
			// 循环列表进行插入
//			for (BokStatFeePosiiton vo : bokStatFeePosiitonList) {
//				// 根据主键判断是否存在
//				BokStatFeePosiiton haveVo = bokStatFeePosiitonMapper.selectByPrimaryKey(vo);
//
//				// 已经存在，进行更新操作
//				if (haveVo != null) {
//					sucessNum = sucessNum + bokStatFeePosiitonMapper.updateByPrimaryKey(vo);
//				}
//				// 不存在，进行插入操作
//				else {
//					sucessNum = sucessNum + bokStatFeePosiitonMapper.insert(vo);
//				}
//			}
			/****update by yinya on 2021-12-22 批处理性能优化：分批次合并 start ***/
			List<List<BokStatFeePosiiton>> list = DataHandleUtil.splitList(bokStatFeePosiitonList);
			for (List<BokStatFeePosiiton> listInsert : list) {
				if (CollectionUtils.isNotEmpty(listInsert)) {
					sucessNum=bokStatFeePosiitonMapper.mergeValBokStatFeePosition(listInsert);
				}
			}
			/****update by yinya on 2021-12-22 批处理性能优化：分批次合并 end ***/
		}

		return sucessNum;
	}

}
