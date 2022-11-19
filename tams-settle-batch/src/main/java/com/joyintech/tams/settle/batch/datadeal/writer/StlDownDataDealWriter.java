package com.joyintech.tams.settle.batch.datadeal.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.systemtools.util.TamsDbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 结算数据批处理step2 Writer
 * 
 * @author 陈卓
 * @date 2020/7/7
 * @see StlDownDataDealWriter
 * @since 1.0
 */
@Service
@Lazy
public class StlDownDataDealWriter extends BaseWriter<NeedToWriteEntity> {

//	@Autowired
//	private SettleTransDataMapper settleTransDataMapper;
//	@Autowired
//	private SettleTransDataAddMapper settleTransDataAddMapper;
//	@Autowired
//	private SettleTransDataPledgeMapper settleTransDataPledgeMapper;
//	@Autowired
//	private SettleTransTgMappingMapper settleTransTgMappingMapper;
//	@Autowired
//	private CommissionCaclProcessMapper commissionCaclProcessMapper;
//	@Autowired
//	private FinProductQutoMapper finProductQutoMapper;
//	@Autowired
//	private ShareBonusRegMapper shareBonusRegMapper;
//	@Autowired
//	private MstFundBonusNoticeMapper mstFundBonusNoticeMapper;
//
//	@Autowired
//	private FinProductTypeMapper finProductTypeMapper;

	@Autowired
	private StlDownDataDealMapper stlDownDataDealMapper;

	@Autowired
	private SqlSessionTemplate template;

	@Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
		/*
		成交数据明细表
		成交数据明细附属表
		成交数据明细质押品表
		佣金计算过程表
		成交数据与投管交易关系（成交数据List做loop）
			成交数据与投管交易关系.成交编号 = 成交数据.成交编号
			成交数据与投管交易关系.投管交易编号 = null
			成交数据与投管交易关系.投管交易编号 = 数据字典.投管交易数据状态.待处理
		 */
		// 成交数据
		List<SettleTransData> settleTransInsertList = new ArrayList<>(items.size());
		List<SettleTransDataAdd> settleTransDataAddInsertList = new ArrayList<>(items.size());
		List<SettleTransDataPledge> settleTransDataPledgeInsertList = new ArrayList<>(items.size());
		// 佣金数据
		List<CommissionCaclProcess> commissionCaclInsertList = new ArrayList<>(items.size());
		// 交易所品种额度
		List<FinProductQuto> finProductQutoInsertList = new ArrayList<>(items.size());
		// 送股权益登记
		List<ShareBonusReg> shareBonusRegInsertList = new ArrayList<>(items.size());
		// 基金分红通告
		List<MstFundBonusNotice> mstFundBonusNoticeInsertList = new ArrayList<>(items.size());
		for (NeedToWriteEntity item : items) {
			if(item instanceof StlDownDataDealWriteDto) {
				StlDownDataDealWriteDto stlDownDataDealWriteDto = (StlDownDataDealWriteDto)item;
				for(StlDownDataDealWriteDto dealWriteDto : stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList()) {
					// 成交数据List
					List<StlDownDataDealWriteBaseDto> stlDownDataDealWriteBaseDtoList = dealWriteDto.getStlDownDataDealWriteBaseDtoList();

					// 佣金计算过程List
//					List<CommissionCaclProcess> commissionCaclProcessList = dealWriteDto.getCommissionCaclProcessList();

					// 成交数据入库
					if(CollectionUtils.isNotEmpty(stlDownDataDealWriteBaseDtoList)) {
						stlDownDataDealWriteBaseDtoList.forEach(dataDealWriteBaseDto -> {
							this.saveDealData(dataDealWriteBaseDto);
							settleTransInsertList.add(dataDealWriteBaseDto.getSettleTransData());
							settleTransDataAddInsertList.add(dataDealWriteBaseDto.getSettleTransDataAdd());
							settleTransDataPledgeInsertList.addAll(dataDealWriteBaseDto.getSettleTransDataPledgeList());
						});
					}

					// 佣金计算过程入库
					commissionCaclInsertList.addAll(dealWriteDto.getCommissionCaclProcessList());
//					if(CollectionUtils.isNotEmpty(commissionCaclProcessList)) {
////						commissionCaclProcessList.forEach(commissionCaclProcessMapper::insertSelective);
//						for (CommissionCaclProcess commissionCaclProcess : commissionCaclProcessList) {
//							try {
//								commissionCaclProcessMapper.insertSelective(commissionCaclProcess);
//							} catch (Exception e) {
//								commissionCaclProcessMapper.deleteByPrimaryKey(commissionCaclProcess.getBusiNo());
//								commissionCaclProcessMapper.insertSelective(commissionCaclProcess);
//							}
//						}
//					}

					// 交易所品种额度
					finProductQutoInsertList.addAll(dealWriteDto.getFinProductQutoList());
//					if (dealWriteDto.getFinProductQutoList() != null && dealWriteDto.getFinProductQutoList().size() > 0) {
//						for (FinProductQuto finProductQuto : dealWriteDto.getFinProductQutoList()) {
//							finProductQutoMapper.insertSelective(finProductQuto);
//						}
//					}
					// 送股权益登记
					shareBonusRegInsertList.addAll(dealWriteDto.getShareBonusRegList());
//					if (dealWriteDto.getShareBonusRegList() != null && dealWriteDto.getShareBonusRegList().size() > 0) {
//						for (ShareBonusReg shareBonusReg : dealWriteDto.getShareBonusRegList()) {
//							shareBonusRegMapper.insertSelective(shareBonusReg);
//						}
//					}
					// 基金分红通告
					mstFundBonusNoticeInsertList.addAll(dealWriteDto.getMstFundBonusNoticeList());
//					if (dealWriteDto.getMstFundBonusNoticeList() != null && dealWriteDto.getMstFundBonusNoticeList().size() > 0) {
//						for (MstFundBonusNotice mstFundBonusNotice : dealWriteDto.getMstFundBonusNoticeList()) {
//							try {
//								mstFundBonusNoticeMapper.insertSelective(mstFundBonusNotice);
//							}catch (Exception ex){
//
//							}
//						}
//					}
				}
			}
		}
		/* 修改为批量插入 */
		// 成交数据入库
		TamsDbUtil.deleteAndInsertSelectiveBatch(template, SettleTransDataMapper.class, settleTransInsertList, e -> ((SettleTransData) e).getTradeId());
		TamsDbUtil.deleteAndInsertSelectiveBatch(template, SettleTransDataAddMapper.class, settleTransDataAddInsertList, e -> ((SettleTransDataAdd) e).getTradeId());
		TamsDbUtil.deleteAndInsertSelectiveBatch(template, SettleTransDataPledgeMapper.class, settleTransDataPledgeInsertList, e -> ((SettleTransDataPledge) e).getTradeId());
		// 佣金计算过程入库
		TamsDbUtil.deleteAndInsertSelectiveBatch(template, CommissionCaclProcessMapper.class, commissionCaclInsertList, e -> ((CommissionCaclProcess) e).getBusiNo());
		// 交易所品种额度
		TamsDbUtil.insertSelectiveBatch(template, FinProductQutoMapper.class, finProductQutoInsertList);
		// 送股权益登记
		TamsDbUtil.insertSelectiveBatch(template, ShareBonusRegMapper.class, shareBonusRegInsertList);
		// 基金分红通告
		TamsDbUtil.insertSelectiveBatch(template, MstFundBonusNoticeMapper.class, mstFundBonusNoticeInsertList);
	}

	/**
	 * 成交数据入库处理
	 * @param dataDealWriteBaseDto 成交数据
	 */
	private void saveDealData(StlDownDataDealWriteBaseDto dataDealWriteBaseDto) {
		if(null != dataDealWriteBaseDto) {
			// 成交数据明细表
			SettleTransData settleTransData = dataDealWriteBaseDto.getSettleTransData();
			// ZGCPCN-1441: 修改可转债和一级投标债成交明细赋值 modify by fuyongsheng 20220616 start
			if(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue().equals(settleTransData.getFinprodType2())){
				String finProdMarketId = settleTransData.getFinprodId().substring(4);
				List<FinProduct> finProducts = stlDownDataDealMapper.getFinproduct(finProdMarketId, settleTransData.getFinprodType2());
				// 实例：FinprodId = F32_2121211_20220618142523.IB
				// 取时间戳最新的那条
				List<FinProduct> sortedFinProducts = finProducts
						.stream()
						.sorted((x, y) -> y.getFinprodId().substring(y.getFinprodId().length() - 17, y.getFinprodId().length()-3)
								.compareTo(x.getFinprodId().substring(x.getFinprodId().length() - 17, x.getFinprodId().length()-3)))
						.collect(Collectors.toList());
				settleTransData.setFinprodId(sortedFinProducts.get(0).getFinprodId());
			}
			// ZGCPCN-1441: 修改可转债和一级投标债成交明细赋值 modify by fuyongsheng 20220616 end
			// 成交数据明细附属表
//			SettleTransDataAdd settleTransDataAdd = dataDealWriteBaseDto.getSettleTransDataAdd();

			// 成交数据明细质押品信息表
//			List<SettleTransDataPledge> settleTransDataPledgeList = dataDealWriteBaseDto.getSettleTransDataPledgeList();

			// 成交数据与投管交易关系
//			SettleTransTgMapping settleTransTgMapping = dataDealWriteBaseDto.getSettleTransTgMapping();

//			if(null != settleTransData && StringUtil.isNotEmpty(settleTransData.getTradeId())) {
//				try {
//					settleTransDataMapper.insertSelective(settleTransData);
//				} catch (Exception e) {
//					settleTransDataMapper.deleteByPrimaryKey(settleTransData.getTradeId());
//					settleTransDataMapper.insertSelective(settleTransData);
//				}
//			}
//			if(null != settleTransDataAdd && StringUtil.isNotEmpty(settleTransDataAdd.getTradeId())) {
//				try {
//					settleTransDataAddMapper.insertSelective(settleTransDataAdd);
//				} catch (Exception e) {
//					settleTransDataAddMapper.deleteByPrimaryKey(settleTransDataAdd.getTradeId());
//					settleTransDataAddMapper.insertSelective(settleTransDataAdd);
//				}
//			}
//			if(CollectionUtils.isNotEmpty(settleTransDataPledgeList)) {
////				settleTransDataPledgeList.forEach(settleTransDataPledgeMapper::insertSelective);
//				for (SettleTransDataPledge settleTransDataPledge : settleTransDataPledgeList) {
//					try {
//						settleTransDataPledgeMapper.insertSelective(settleTransDataPledge);
//					} catch (Exception e) {
//						settleTransDataPledgeMapper.deleteByPrimaryKey(settleTransDataPledge.getTradeId());
//						settleTransDataPledgeMapper.insertSelective(settleTransDataPledge);
//					}
//				}
//			}
//			if(null != settleTransTgMapping && StringUtil.isNotEmpty(settleTransTgMapping.getTradeId())) {
//				settleTransTgMappingMapper.insertSelective(settleTransTgMapping);
//			}
		}
	}
}
