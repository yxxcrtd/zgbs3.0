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
import org.apache.commons.collections.CollectionUtils;
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

	@Autowired
	private SettleTransDataMapper settleTransDataMapper;
	@Autowired
	private SettleTransDataAddMapper settleTransDataAddMapper;
	@Autowired
	private SettleTransDataPledgeMapper settleTransDataPledgeMapper;
	@Autowired
	private SettleTransTgMappingMapper settleTransTgMappingMapper;
	@Autowired
	private CommissionCaclProcessMapper commissionCaclProcessMapper;
	@Autowired
	private FinProductQutoMapper finProductQutoMapper;
	@Autowired
	private ShareBonusRegMapper shareBonusRegMapper;
	@Autowired
	private MstFundBonusNoticeMapper mstFundBonusNoticeMapper;

	@Autowired
	private FinProductTypeMapper finProductTypeMapper;

	@Autowired
	private StlDownDataDealMapper stlDownDataDealMapper;
	
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
		for (NeedToWriteEntity item : items) {
			if(item instanceof StlDownDataDealWriteDto) {
				StlDownDataDealWriteDto stlDownDataDealWriteDto = (StlDownDataDealWriteDto)item;
				for(StlDownDataDealWriteDto dealWriteDto : stlDownDataDealWriteDto.getStlDownDataDealWriteDtoList()) {
					// 成交数据List
					List<StlDownDataDealWriteBaseDto> stlDownDataDealWriteBaseDtoList = dealWriteDto.getStlDownDataDealWriteBaseDtoList();

					// 佣金计算过程List
					List<CommissionCaclProcess> commissionCaclProcessList = dealWriteDto.getCommissionCaclProcessList();

					// 成交数据入库
					if(CollectionUtils.isNotEmpty(stlDownDataDealWriteBaseDtoList)) {
						stlDownDataDealWriteBaseDtoList.forEach(this::saveDealData);
					}

					// 佣金计算过程入库
					if(CollectionUtils.isNotEmpty(commissionCaclProcessList)) {
//						commissionCaclProcessList.forEach(commissionCaclProcessMapper::insertSelective);
						for (CommissionCaclProcess commissionCaclProcess : commissionCaclProcessList) {
							try {
								commissionCaclProcessMapper.insertSelective(commissionCaclProcess);
							} catch (Exception e) {
								commissionCaclProcessMapper.deleteByPrimaryKey(commissionCaclProcess.getBusiNo());
								commissionCaclProcessMapper.insertSelective(commissionCaclProcess);
							}
						}
					}
					
					// 交易所品种额度
					if (dealWriteDto.getFinProductQutoList() != null && dealWriteDto.getFinProductQutoList().size() > 0) {
						for (FinProductQuto finProductQuto : dealWriteDto.getFinProductQutoList()) {
							finProductQutoMapper.insertSelective(finProductQuto);
						}
					}
					// 送股权益登记
					if (dealWriteDto.getShareBonusRegList() != null && dealWriteDto.getShareBonusRegList().size() > 0) {
						for (ShareBonusReg shareBonusReg : dealWriteDto.getShareBonusRegList()) {
							shareBonusRegMapper.insertSelective(shareBonusReg);
						}
					}
					// 基金分红通告
					if (dealWriteDto.getMstFundBonusNoticeList() != null && dealWriteDto.getMstFundBonusNoticeList().size() > 0) {
						for (MstFundBonusNotice mstFundBonusNotice : dealWriteDto.getMstFundBonusNoticeList()) {
							try {
								mstFundBonusNoticeMapper.insertSelective(mstFundBonusNotice);
							}catch (Exception ex){

							}
						}
					}
				}
			}
		}
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
				List<FinProduct> sortedFinProducts = finProducts
						.stream()
						.sorted((x, y) -> y.getFinprodId().substring(y.getFinprodId().length() - 11, y.getFinprodId().length()-3)
								.compareTo(x.getFinprodId().substring(x.getFinprodId().length() - 11, x.getFinprodId().length()-3)))
						.collect(Collectors.toList());
				settleTransData.setFinprodId(sortedFinProducts.get(0).getFinprodId());
			}
			// ZGCPCN-1441: 修改可转债和一级投标债成交明细赋值 modify by fuyongsheng 20220616 end
			// 成交数据明细附属表
			SettleTransDataAdd settleTransDataAdd = dataDealWriteBaseDto.getSettleTransDataAdd();

			// 成交数据明细质押品信息表
			List<SettleTransDataPledge> settleTransDataPledgeList = dataDealWriteBaseDto.getSettleTransDataPledgeList();

			// 成交数据与投管交易关系
//			SettleTransTgMapping settleTransTgMapping = dataDealWriteBaseDto.getSettleTransTgMapping();

			if(null != settleTransData && StringUtil.isNotEmpty(settleTransData.getTradeId())) {
				try {
					settleTransDataMapper.insertSelective(settleTransData);
				} catch (Exception e) {
					settleTransDataMapper.deleteByPrimaryKey(settleTransData.getTradeId());
					settleTransDataMapper.insertSelective(settleTransData);
				}
			}
			if(null != settleTransDataAdd && StringUtil.isNotEmpty(settleTransDataAdd.getTradeId())) {
				try {
					settleTransDataAddMapper.insertSelective(settleTransDataAdd);
				} catch (Exception e) {
					settleTransDataAddMapper.deleteByPrimaryKey(settleTransDataAdd.getTradeId());
					settleTransDataAddMapper.insertSelective(settleTransDataAdd);
				}
			}
			if(CollectionUtils.isNotEmpty(settleTransDataPledgeList)) {
//				settleTransDataPledgeList.forEach(settleTransDataPledgeMapper::insertSelective);
				for (SettleTransDataPledge settleTransDataPledge : settleTransDataPledgeList) {
					try {
						settleTransDataPledgeMapper.insertSelective(settleTransDataPledge);
					} catch (Exception e) {
						settleTransDataPledgeMapper.deleteByPrimaryKey(settleTransDataPledge.getTradeId());
						settleTransDataPledgeMapper.insertSelective(settleTransDataPledge);
					}
				}
			}
//			if(null != settleTransTgMapping && StringUtil.isNotEmpty(settleTransTgMapping.getTradeId())) {
//				settleTransTgMappingMapper.insertSelective(settleTransTgMapping);
//			}
		}
	}
}
