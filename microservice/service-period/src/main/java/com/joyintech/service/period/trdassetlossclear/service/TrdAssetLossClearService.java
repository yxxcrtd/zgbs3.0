/*
 * 文件名：TrdDepoDurationService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2018年10月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.period.trdassetlossclear.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.util.DBUtil;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.period.trdassetlossclear.dao.TrdAssetLossClearMapper;
import com.joyintech.service.period.trdassetlossclear.dto.TrdAssetLossClearDto;
import com.joyintech.service.period.trddepoduration.dto.TrdDepoDurationDto;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.TrdDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.entity.EtrSecPositionFlow;
import com.joyintech.tams.common.base.entity.PtlRecePositionFlow;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.common.base.entity.PtlTrade;
import com.joyintech.tams.common.base.entity.TrdDealTotalAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.impl.BasePtlSecPositionInfoServiceImpl;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;

/**
 * 存续期微服务 〈功能详细描述〉
 *
 * @author Administrator
 * @version 1.0 2018年10月11日
 * @see TrdAssetLossClearService
 * @since 1.0
 */
@Service
public class TrdAssetLossClearService extends BaseService<TrdDepoDurationDto> {

	@Autowired
	private TrdAssetLossClearMapper trdAssetLossClearMapper;

	@Autowired
	private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

	@Autowired
	private TrdDealTotalAddMapper trdDealTotalAddMapper;

	@Autowired
	private TrdProductDealAddMapper trdProductDealAddMapper;

	@Autowired
	private TrdProductDealMapper trdProductDealMapper;

	@Autowired
	private PtlTradeMapper ptlTradeMapper;

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private BasePtlRecePositionService basePtlRecePositionService;

	@Autowired
	private EtrCommonCashPositionDealService etrCommonCashPositionDealService;

	@Autowired
	private BasePtlSecPositionInfoServiceImpl basePtlSecPositionInfoServiceImpl;

	/**
	 * 业务编号取得共通
	 */
	@Autowired
	private BaseGetBussinessId baseGetBussinessIdimpl;

	public void doComplete(TrdAssetLossClearDto durationDto, String operationType) {
		// 画面.清理日
		Date clealDate = durationDto.getTradeDate();
		if (StringUtil.equals(operationType, "NEW")) {
			// 3.DB 登录
			// 3.1 以资产代码维度登录汇总表
			// 交易编号自动生成
			String tradeId = this.createStrategyId();
			// 登录金融产品交易汇总表
			TrdFinProductDealTotal 金融产品交易汇总表 = this.insertTfpdt(durationDto, tradeId);
			trdFinProductDealTotalMapper.insert(金融产品交易汇总表);
			// 登录金融产品交易汇总附属表
			TrdDealTotalAdd 金融产品交易汇总附属表 = this.insertTdta(durationDto, tradeId);
			trdDealTotalAddMapper.insert(金融产品交易汇总附属表);
			// 3.2 以组合代码维度登录交易表
			for (TrdAssetLossClearDto resultDto : durationDto.getSelectedItem()) {
				// 采番
				String tradeComeId = dbUtil.getSequenceIndexData(DataIndexSequenceType.交易编号);
				// 采番2
				String tradeComeIdTwo = dbUtil.getSequenceIndexData(DataIndexSequenceType.交易编号);
				// 采番3
				//String tradeComeIdThr = dbUtil.getSequenceIndexData(DataIndexSequenceType.交易编号);
				// 登录金融产品交易汇总表2
				resultDto.setTradeDate(clealDate);
				TrdFinProductDealTotal 金融产品交易汇总表2 = this.insertTfpdtTwo(resultDto, tradeComeId, tradeId);
				trdFinProductDealTotalMapper.insert(金融产品交易汇总表2);
				// 登录金融产品交易汇总附属表2
				TrdDealTotalAdd 金融产品交易汇总附属表2 = this.insertTdtaTwo(resultDto, tradeComeId);
				trdDealTotalAddMapper.insert(金融产品交易汇总附属表2);
				// 登录金融产品交易表
				TrdProductDeal 金融产品交易表 = this.insertTpd(resultDto, tradeComeIdTwo, tradeComeId, clealDate);
				trdProductDealMapper.insert(金融产品交易表);
				// 登录金融产品交易附属表
				TrdProductDealAdd 金融产品交易附属表 = this.inserTpda(resultDto, tradeComeIdTwo, clealDate);
				trdProductDealAddMapper.insert(金融产品交易附属表);
				// 登录组合交易表
				PtlTrade 组合交易表 = this.inserPt(resultDto, tradeComeIdTwo, clealDate);
				ptlTradeMapper.insert(组合交易表);

				// ※※※※指令持仓流水DTO ※参照卖出（指令持仓流水提交登录）start※※※※
				// 组装指令持仓流水查询参数
				Map<String, Object> paramEspf = new HashMap<>();
				paramEspf.put("portfolioId", resultDto.getPortfolioId());
				paramEspf.put("finprodId", resultDto.getFinprodId());
				if (resultDto.getSecAcctId().equals("999999")) {
					paramEspf.put("secAcctId", null);					
				} else {
					paramEspf.put("secAcctId", resultDto.getSecAcctId());
				}
				if (!StringUtil.isEmpty(resultDto.getInvAim())) {
					paramEspf.put("invAim", resultDto.getInvAim());
				} else {
					paramEspf.put("invAim", "999999");
				}
				paramEspf.put("cdate", resultDto.getTradeDate());
				paramEspf.put("ccy", resultDto.getCcy());
				// 指令持仓流水查询
				List<TrdAssetLossClearDto> espfDtoList = trdAssetLossClearMapper.selectEspf(paramEspf);
				for (TrdAssetLossClearDto espfDto : espfDtoList) {
					EtrSecPositionFlowDto etrSecPositionFlowDto = this.getEtrSecPositionFlowDto(espfDto, tradeComeIdTwo,
							clealDate);
					EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
					List<EtrSecPositionFlowDto> list = new ArrayList<>();
					list.add(etrSecPositionFlowDto);
					etrPositionHoldDto.setPtlSecPositionFlows(list);
					etrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
				}
				// ※※※※指令持仓流水DTO ※参照卖出（指令持仓流水提交登录）end※※※※

				// ※※※※组合持仓流水 ※参照卖出（组合持仓流水提交登录为可用，生效为实际）start※※※※
				// 组装组合持仓流水查询参数
				Map<String, Object> paramPspf = new HashMap<>();
				paramPspf.put("portfolioId", resultDto.getPortfolioId());
				paramPspf.put("finprodId", resultDto.getFinprodId());
				if (resultDto.getSecAcctId().equals("999999")) {					
					paramPspf.put("secAcctId", null);
				} else {
					paramPspf.put("secAcctId", resultDto.getSecAcctId());
				}
				if (!StringUtil.isEmpty(resultDto.getInvAim())) {
					paramPspf.put("invAim", resultDto.getInvAim());
				} else {
					paramPspf.put("invAim", "999999");
				}
				paramPspf.put("cdate", resultDto.getTradeDate());
				paramPspf.put("ccy", resultDto.getCcy());
				// 指令持仓流水查询
				List<TrdAssetLossClearDto> pspfDtoList = trdAssetLossClearMapper.selectPspf(paramPspf);
				for (TrdAssetLossClearDto pspfDto : pspfDtoList) {
					PtlSecPositionFlowDto ptlSecPositionFlowDto = this.getPtlSecPositionFlowDto(pspfDto, tradeComeIdTwo,
							clealDate);
					basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlowDto);
				}
				// ※※※※组合持仓流水 ※参照卖出（组合持仓流水提交登录为可用，生效为实际）end※※※※

				// ※※※※调用估值接口登入账信息start※※※※
				AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
				// 所有交易生效后调用的共通方法
				inputDto.setPageId("TrdAssetLossClear002");
				inputDto.setProdid(resultDto.getPortfolioId());
				inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
				inputDto.setTrdProductDeal(金融产品交易表);
				inputDto.setTrdProductDealAdd(金融产品交易附属表);
				inputDto.setListTrdTradeFeeDetail(null);
				inputDto.setTrdTradeFeeDetail(null);
				inputDto.setTurnInvAim(resultDto.getInvAimName());
				inputDto.setStlPayOrders(null);
				List<PtlTrade> 组合交易表list = new ArrayList<>();
				组合交易表list.add(组合交易表);
				AllTradeCommonUtil.setDeals(inputDto, 金融产品交易表, 金融产品交易附属表, null, null, 组合交易表list, null, null);
				inputDto.setActPortfolioId(null);
				inputDto.setPtlTradeList(组合交易表list);
				inputDto.setIsNotAutoGenerateReverseDeal(DictCodeList.是否_是.getValue());
				AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
				// ※※※※调用估值接口登入账信息end※※※※

				// if 明细.已到期本息 不为空
				if (resultDto.getClearRecAmt() != null) {
					// 调用应收款查询
					Map<String, Object> param = new HashMap<>();
					param.put("portfolioId", resultDto.getPortfolioId());
					if (resultDto.getSecAcctId() != "999999") {
						param.put("secAcctId", resultDto.getSecAcctId());
					} else {
						param.put("secAcctId", null);
					}
					param.put("invAim", resultDto.getInvAim());
					param.put("finprodId", resultDto.getFinprodId());
					param.put("cdate", DateUtil.addDay(clealDate, -1));
					List<TrdAssetLossClearDto> resultList = trdAssetLossClearMapper.selectAccountReceivableEnquiry(param);
					for (TrdAssetLossClearDto dto : resultList) {
						// 采番3
						String tradeComeIdFor = dbUtil.getSequenceIndexData(DataIndexSequenceType.交易编号);
						// 登录金融产品交易表2
						TrdProductDeal 金融产品交易表2 = this.insertTpd2(resultDto, dto, tradeComeIdFor, tradeComeId,
								clealDate);
						trdProductDealMapper.insert(金融产品交易表2);
						// 登录金融产品交易附属表
						TrdProductDealAdd 金融产品交易附属表2 = this.inserTpda2(dto, tradeComeIdFor, clealDate);
						trdProductDealAddMapper.insert(金融产品交易附属表2);
						// 登录组合交易表2
						PtlTrade 组合交易表2 = this.inserPt2(resultDto, dto, tradeComeIdFor, clealDate);
						ptlTradeMapper.insert(组合交易表2);
						// 应收款持仓流水 ※参照卖出（组合持仓流水提交登录为可用，生效为实际）						
						Map<String, Object> resultParam = new HashMap<>();
						resultParam.put("portfolioId", dto.getPortfolioId());
						resultParam.put("finprodId", dto.getFinprodId());
						if (dto.getSecAcctId().equals("999999")) {					
							resultParam.put("secAcctId", null);
						} else {
							resultParam.put("secAcctId", dto.getSecAcctId());
						}
						if (!StringUtil.isEmpty(dto.getInvAim())) {
							resultParam.put("invAim", dto.getInvAim());
						} else {
							resultParam.put("invAim", "999999");
						}
						resultParam.put("cdate", clealDate);
						resultParam.put("ccy", dto.getCcy());
						resultParam.put("branch", dto.getBranch());
						List<TrdAssetLossClearDto> recDtoList = trdAssetLossClearMapper.selectRecType(resultParam);
						for (TrdAssetLossClearDto recDto : recDtoList) {
							PtlRecePositionFlowDto positionFlowDto = this.getPtlRecePositionFlowDto(resultDto, recDto,
							    tradeComeIdFor, clealDate, recDto.getRecType());
							basePtlRecePositionService.inertRecePositionFlow(positionFlowDto);							
						}


						// ※※※※调用估值接口登入账信息start※※※※
						AllTradeCommonInputDto inputDto2 = new AllTradeCommonInputDto();
						// 所有交易生效后调用的共通方法
						inputDto2.setPageId("TrdAssetLossClear002");
						inputDto2.setProdid(dto.getPortfolioId());
						inputDto2.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
						inputDto2.setTrdProductDeal(金融产品交易表2);
						inputDto2.setTrdProductDealAdd(金融产品交易附属表2);
						inputDto2.setListTrdTradeFeeDetail(null);
						inputDto2.setTrdTradeFeeDetail(null);
						inputDto2.setTurnInvAim(resultDto.getInvAimName());
						inputDto2.setStlPayOrders(null);
						List<PtlTrade> 组合交易表list2 = new ArrayList<>();
						组合交易表list.add(组合交易表2);
						AllTradeCommonUtil.setDeals(inputDto, 金融产品交易表2, 金融产品交易附属表2, null, null, 组合交易表list2, null, null);
						inputDto2.setActPortfolioId(null);
						inputDto2.setPtlTradeList(组合交易表list2);
						inputDto2.setIsNotAutoGenerateReverseDeal(DictCodeList.是否_是.getValue());
						AllTradeCommonUtil.handleAfterTradeComplete(inputDto2);
						// ※※※※调用估值接口登入账信息end※※※※
					}

				}
			}
		}
		if (StringUtil.equals(operationType, "REVOKE")) {
			// 取得交易编号
			String totleTradeId = durationDto.getTotleTradeId();
			// 以资产代码维度查询汇总表

			// 定义查询参数
			Map<String, Object> param = new HashMap<>();
			param.put("totleTradeId", totleTradeId);
			// 根据交易编号查询金融产品交易汇总表
			TrdFinProductDealTotal 金融产品交易汇总表 = trdAssetLossClearMapper.selectTfpdtById(param);
			if (金融产品交易汇总表 != null) {
				// 状态更新为撤销
				trdAssetLossClearMapper.updateTfpdtById(param);
				// 插入反向记录
				金融产品交易汇总表.setpTradeId(totleTradeId);
				金融产品交易汇总表.setTotleTradeId(totleTradeId + "R");
				金融产品交易汇总表.setIsCancel(DictCodeList.是否_否.getValue());
				金融产品交易汇总表.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
				trdFinProductDealTotalMapper.insert(金融产品交易汇总表);
			}
			// 根据交易编号查询金融产品交易汇总附属表
			TrdDealTotalAdd 金融产品交易汇总附属表 = trdAssetLossClearMapper.selectTdtaById(param);
			if (金融产品交易汇总附属表 != null) {
				// 插入反向记录
				金融产品交易汇总附属表.setTotleTradeId(totleTradeId + "R");
				trdDealTotalAddMapper.insert(金融产品交易汇总附属表);
			}
			// 根据交易编号查询金融产品交易汇总表2
			List<TrdFinProductDealTotal> 金融产品交易汇总表2 = trdAssetLossClearMapper.selectTfpdtById2(param);
			if (CollectionUtils.isNotEmpty(金融产品交易汇总表2)) {
				// 状态更新为撤销
				trdAssetLossClearMapper.updateTfpdtById2(param);
				for(TrdFinProductDealTotal 金融产品交易汇总表明细 :金融产品交易汇总表2){
				    // 插入反向记录
				    金融产品交易汇总表明细.setpTradeId(金融产品交易汇总表明细.getTotleTradeId());
				    金融产品交易汇总表明细.setTotleTradeId(金融产品交易汇总表明细.getTotleTradeId() + "R");
				    金融产品交易汇总表明细.setIsCancel(DictCodeList.是否_否.getValue());
				    金融产品交易汇总表明细.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
	                trdFinProductDealTotalMapper.insert(金融产品交易汇总表明细);
				}
				
			}
			// 根据交易编号查询金融产品交易汇总附属表2
			List<TrdDealTotalAdd> 金融产品交易汇总附属表2 = trdAssetLossClearMapper.selectTdtaById2(param);
			if (CollectionUtils.isNotEmpty(金融产品交易汇总附属表2)) {
			    for(TrdDealTotalAdd 金融产品交易汇总附属表明细 : 金融产品交易汇总附属表2){
			       // 插入反向记录
			        金融产品交易汇总附属表明细.setTotleTradeId(金融产品交易汇总附属表明细.getTotleTradeId() + "R");
	                trdDealTotalAddMapper.insert(金融产品交易汇总附属表明细);
			    }
				
			}

			// 根据交易编号查询金融产品交易表
			List<TrdProductDeal> 金融产品交易表list = trdAssetLossClearMapper.selectTpdById(param);
			if (金融产品交易表list != null && 金融产品交易表list.size() > 0) {
				for (TrdProductDeal 金融产品交易表 : 金融产品交易表list) {
					Map<String, Object> tpdParam = new HashMap<>();
					tpdParam.put("tradeId", 金融产品交易表.getTradeId());
					// 状态更新为撤销
					trdAssetLossClearMapper.updateTpdById(tpdParam);
					// 插入反向记录
					金融产品交易表.setpTradeId(金融产品交易表.getTradeId());
					金融产品交易表.setTradeId(金融产品交易表.getTradeId() + "R");
					金融产品交易表.setIsCancel(DictCodeList.是否_否.getValue());
					金融产品交易表.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
					金融产品交易表.setTotleTradeId(金融产品交易表.getTotleTradeId() + "R");					
					trdProductDealMapper.insert(金融产品交易表);
				}
			}
			// 根据交易编号查询金融产品交易附属表
			List<TrdProductDealAdd> 金融产品交易附属表list = trdAssetLossClearMapper.selectTpdaById(param);
			if (金融产品交易附属表list != null && 金融产品交易附属表list.size() > 0) {
				for (TrdProductDealAdd 金融产品交易附属表 : 金融产品交易附属表list) {
					金融产品交易附属表.setTradeId(金融产品交易附属表.getTradeId() + "R");
					trdProductDealAddMapper.insert(金融产品交易附属表);
				}
			}
			// 根据交易编号查询组合交易表
			List<PtlTrade> 组合交易表list = trdAssetLossClearMapper.selectPtById(param);
			if (组合交易表list != null && 组合交易表list.size() > 0) {
				for (PtlTrade 组合交易表 : 组合交易表list) {
					组合交易表.setBusiNo(组合交易表.getBusiNo() + "R");
					ptlTradeMapper.insert(组合交易表);
				}
			}
			// 撤销查询方法
			List<EtrSecPositionFlow> etrList = trdAssetLossClearMapper.getEtrSecPositionFlow(param);
			if(CollectionUtils.isNotEmpty(etrList)){
			    for(EtrSecPositionFlow flow:etrList){
			     // 指令持仓流水按原来登反向
		            EtrSecPositionFlowDto etrSecPositionFlowDto = this.getEtrSecPositionFlowDto(flow,
		                flow.getTradeId()+ "R" );
		            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
		            List<EtrSecPositionFlowDto> list = new ArrayList<>();
		            list.add(etrSecPositionFlowDto);

		            etrPositionHoldDto.setPtlSecPositionFlows(list);
		            etrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
			    }
			}
			
			List<PtlSecPositionFlow> ptlList = trdAssetLossClearMapper.getPtlSecPositionFlow(param);
			if(CollectionUtils.isNotEmpty(ptlList)){
                for(PtlSecPositionFlow flow:ptlList){
                 // 指令持仓流水按原来登反向
                    PtlSecPositionFlowDto ptlSecPositionFlowDto = this.getPtlSecPositionFlowDto(flow,
                        flow.getTradeId()+ "R" );
                    basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlowDto);
                }
            }
			List<PtlRecePositionFlow> ptlReceList = trdAssetLossClearMapper.getPtlRecePositionFlow(param);
            if(CollectionUtils.isNotEmpty(ptlReceList)){
                for(PtlRecePositionFlow flow:ptlReceList){
                    PtlRecePositionFlowDto positionFlowDto = this.getPtlRecePositionFlowDto(flow,
                        flow.getTradeId()+"R");
                    basePtlRecePositionService.inertRecePositionFlow(positionFlowDto);
                }
            }

		}

	}

	/**
	 * 指令持仓流水DTO封装
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	private EtrSecPositionFlowDto getEtrSecPositionFlowDto(EtrSecPositionFlow flow, String tradeComeIdTwo) {
		EtrSecPositionFlowDto position = new EtrSecPositionFlowDto();
		position.setPortfolioId(flow.getPortfolioId());
		position.setSecAcctId(flow.getSecAcctId());
		position.setFinprodId(flow.getFinprodId());
		position.setBranch(flow.getBranch());
	    position.setInvAim(flow.getInvAim());
	
		position.setHodingType(flow.getHodingType());
		position.setCdate(flow.getCdate());
		position.setCcy(flow.getCcy());
		position.setAmount(flow.getAmount().multiply(new BigDecimal(-1)));
		position.setpFinprodId(flow.getpFinprodId());
		// 调用业务编号共通组件获取流水号
		BussinessCodeParameter parameter = new BussinessCodeParameter();
		parameter.setSeqNo("9999");
		String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
		position.setSeqno(seqNo);
		position.setTradeId(tradeComeIdTwo);
		return position;

	}
	/**
     * 指令持仓流水DTO封装
     * 
     * @return
     * @see
     * @since 1.0
     */
    private EtrSecPositionFlowDto getEtrSecPositionFlowDto(TrdAssetLossClearDto resultDto, String tradeComeIdTwo,
            Date clealDate) {
        EtrSecPositionFlowDto position = new EtrSecPositionFlowDto();
        position.setPortfolioId(resultDto.getPortfolioId());
        position.setSecAcctId(resultDto.getSecAcctId());
        position.setFinprodId(resultDto.getFinprodId());
        position.setBranch(new Long(0));
        if (!StringUtil.isEmpty(resultDto.getInvAim())) {
            position.setInvAim(resultDto.getInvAim());
        } else {
            position.setInvAim("999999");
        }
        position.setHodingType(resultDto.getHodingType());
        position.setCdate(clealDate);
        position.setCcy(resultDto.getCcy());
        position.setAmount(resultDto.getShareAmt().multiply(new BigDecimal(-1)));
        position.setpFinprodId(null);
        // 调用业务编号共通组件获取流水号
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setSeqNo("9999");
        String seqNo = baseGetBussinessIdimpl.buildBussinessCode(parameter);
        position.setSeqno(seqNo);
        position.setTradeId(tradeComeIdTwo);
        return position;

    }
	/**
	 * 组合持仓流水DTO封装
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	private PtlSecPositionFlowDto getPtlSecPositionFlowDto(TrdAssetLossClearDto resultDto, String tradeComeIdTwo,
			Date clealDate) {
		PtlSecPositionFlowDto position = new PtlSecPositionFlowDto();
		position.setPortfolioId(resultDto.getPortfolioId());
		position.setSecAcctId(resultDto.getSecAcctId());
		position.setFinprodId(resultDto.getFinprodId());
		position.setBranch(new Long(0));
		if (!StringUtil.isEmpty(resultDto.getInvAim())) {
			position.setInvAim(resultDto.getInvAim());
		} else {
			position.setInvAim("999999");
		}
		position.setHodingType(DictCodeList.持仓类型_实际.getValue());
		position.setCdate(clealDate);
		position.setCcy(resultDto.getCcy());
		position.setAmount(resultDto.getShareAmt().multiply(new BigDecimal(-1)));
		position.setpFinprodId(null);
		position.setTradeId(tradeComeIdTwo);
		return position;

	}
	/**
     * 组合持仓流水DTO封装
     * 
     * @return
     * @see
     * @since 1.0
     */
    private PtlSecPositionFlowDto getPtlSecPositionFlowDto(PtlSecPositionFlow flow, String tradeComeIdTwo) {
        PtlSecPositionFlowDto position = new PtlSecPositionFlowDto();
        position.setPortfolioId(flow.getPortfolioId());
        position.setSecAcctId(flow.getSecAcctId());
        position.setFinprodId(flow.getFinprodId());
        position.setBranch(flow.getBranch());
        position.setInvAim(flow.getInvAim());
        position.setHodingType(flow.getHodingType());
        position.setCdate(flow.getCdate());
        position.setCcy(flow.getCcy());
        position.setAmount(flow.getAmount().multiply(new BigDecimal(-1)));
        position.setpFinprodId(null);
        position.setTradeId(tradeComeIdTwo);
        return position;

    }

	/**
	 * 应收款持仓流水DTO封装
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	private PtlRecePositionFlowDto getPtlRecePositionFlowDto(TrdAssetLossClearDto resultDto, TrdAssetLossClearDto dto,
			String tradeComeId, Date clealDate, String recType) {
		PtlRecePositionFlowDto position = new PtlRecePositionFlowDto();
		position.setPortfolioId(dto.getPortfolioId());
		position.setSecAcctId(dto.getSecAcctId());
		position.setFinprodId(resultDto.getFinprodId());
		position.setBranch(new Long(dto.getBranch()));
		if (!StringUtil.isEmpty(resultDto.getInvAim())) {
			position.setInvAim(resultDto.getInvAim());
		} else {
			position.setInvAim("999999");
		}
		position.setHodingType(DictCodeList.持仓类型_实际.getValue());
		position.setCdate(clealDate);
		position.setCcy(dto.getCcy());
		position.setAmount(dto.getReceAmt().multiply(new BigDecimal(-1)));
		position.setpFinprodId(null);
		position.setTradeId(tradeComeId);
		position.setRecType(recType);
		position.setPayDate(dto.getTradeDate());
		return position;
	}
	/**
     * 应收款持仓流水DTO封装
     * 
     * @return
     * @see
     * @since 1.0
     */
    private PtlRecePositionFlowDto getPtlRecePositionFlowDto(PtlRecePositionFlow flow,String tradeComeId) {
        PtlRecePositionFlowDto position = new PtlRecePositionFlowDto();
        position.setPortfolioId(flow.getPortfolioId());
        position.setSecAcctId(flow.getSecAcctId());
        position.setFinprodId(flow.getFinprodId());
        position.setBranch(flow.getBranch());
        position.setInvAim(flow.getInvAim());
 
        position.setHodingType(flow.getHodingType());
        position.setCdate(flow.getCdate());
        position.setCcy(flow.getCcy());
        position.setAmount(flow.getAmount().multiply(new BigDecimal(-1)));
        position.setpFinprodId(null);
        position.setTradeId(tradeComeId);
        position.setRecType(flow.getRecType());
        position.setPayDate(flow.getPayDate());
        return position;
    }
	/**
	 * 登录金融产品交易汇总表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdFinProductDealTotal insertTfpdt(TrdAssetLossClearDto durationDto, String tradeId) {
		TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
		List<TrdAssetLossClearDto> list = durationDto.getSelectedItem();
		// 定义交易份额
		BigDecimal shareAmt = new BigDecimal(0);
		// 交易本金
		BigDecimal prinAmt = new BigDecimal(0);
		// 利息总额
		BigDecimal intAmt = new BigDecimal(0);
		// 汇总交易编号 = 交易编号
		trdFinProductDealTotal.setTotleTradeId(tradeId);
		// 金融产品代码 = 画面.资产代码
		trdFinProductDealTotal.setFinprodId(durationDto.getFinprodId());
		// 金融产品类型（投管分类）= 画面.资产代码对应.金融产品类型(投管分类)
		trdFinProductDealTotal.setFinprodType(list.get(0).getFinprodType());
		// 金融产品类型（估值核算）= 画面.资产代码对应.金融产品类型(估值核算)
		trdFinProductDealTotal.setFinprodType2(list.get(0).getFinprodType2());
		// 分支序号 = 0
		trdFinProductDealTotal.setBranch(new Long(0));
		// 交易方向=数据字典.交易方向.清理核销
		trdFinProductDealTotal.setPs(DictCodeListExt3.交易方向_清理核销.getValue());
		// 交易状态 = 数据字典.交易状态.有效
		trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.有效状态_有效.getValue());
		// 是否已撤销 = 数据字典.是否.否
		trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
		// 投管业务类型=数据字典.投管业务类型.清理核销
		trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_资产减值清理_清理核销.getValue());
		// 业务类型=数据字典.投管业务类型.清理核销
		trdFinProductDealTotal.setBusiType(DictCodeListExt3.共通业务类型_清理核销.getValue());
		for (TrdAssetLossClearDto dto : list) {
			shareAmt = ArithmeticUtil.doAdd(shareAmt, dto.getShareAmt());
			prinAmt = ArithmeticUtil.doAdd(prinAmt, dto.getPrinAmt());
			intAmt = ArithmeticUtil.doAdd(intAmt, dto.getIntAmt());
		}
		// 交易份额 =SUM（明细.持仓份额）
		trdFinProductDealTotal.setShareAmt(shareAmt);
		// 交易本金=SUM（明细.持仓成本）
		trdFinProductDealTotal.setPrinAmt(prinAmt);
		// 利息总额=SUM（明细.持仓应收利息）
		trdFinProductDealTotal.setIntAmt(intAmt);
		trdFinProductDealTotal.setTradeDate(durationDto.getTradeDate());
		trdFinProductDealTotal.setSettleDate(durationDto.getTradeDate());
		trdFinProductDealTotal.setCcy(list.get(0).getCcy());
		trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());

		return trdFinProductDealTotal;
	}

	/**
	 * 登录金融产品交易汇总附属表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdDealTotalAdd insertTdta(TrdAssetLossClearDto durationDto, String tradeId) {
		TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
		List<TrdAssetLossClearDto> list = durationDto.getSelectedItem();
		// 清理应收款
		BigDecimal clearRecAmt = new BigDecimal(0);
		// 罚息金额
		BigDecimal penaltyIntamt = new BigDecimal(0);
		// 清理减值
		BigDecimal clearLossAmt = new BigDecimal(0);
		// 汇总交易编号 =2.交易编号
		trdDealTotalAdd.setTotleTradeId(tradeId);
		for (TrdAssetLossClearDto dto : list) {
			clearRecAmt = ArithmeticUtil.doAdd(clearRecAmt, dto.getClearRecAmt());
			penaltyIntamt = ArithmeticUtil.doAdd(penaltyIntamt, dto.getPenaltyIntAmt());
			clearLossAmt = ArithmeticUtil.doAdd(clearLossAmt, dto.getClearLossAmt());
		}
		// 清理应收款=SUM（明细.已到期本息）
		trdDealTotalAdd.setClearRecAmt(clearRecAmt);
		// 罚息金额=SUM（明细.应收逾期罚息）
		trdDealTotalAdd.setPenaltyIntamt(penaltyIntamt);
		// 清理减值=SUM（明细.当前账面减值余额）
		trdDealTotalAdd.setClearLossAmt(clearLossAmt);
		return trdDealTotalAdd;
	}

	/**
	 * 登录金融产品交易汇总表2
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdFinProductDealTotal insertTfpdtTwo(TrdAssetLossClearDto durationDto, String tradeComeId, String tradeId) {
		TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
		// 汇总交易编号 = 交易编号
		trdFinProductDealTotal.setTotleTradeId(tradeComeId);
		// 金融产品代码 = 画面.资产代码
		trdFinProductDealTotal.setFinprodId(durationDto.getFinprodId());
		// 金融产品类型（投管分类）= 画面.资产代码对应.金融产品类型(投管分类)
		trdFinProductDealTotal.setFinprodType(durationDto.getFinprodType());
		// 金融产品类型（估值核算）= 画面.资产代码对应.金融产品类型(估值核算)
		trdFinProductDealTotal.setFinprodType2(durationDto.getFinprodType2());
		// 交易方向=数据字典.交易方向.清理核销
		trdFinProductDealTotal.setPs(DictCodeListExt3.交易方向_清理核销.getValue());
		// 分支序号 = 0
		trdFinProductDealTotal.setBranch(new Long(0));
		// 交易状态 = 数据字典.交易状态.有效
		trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.有效状态_有效.getValue());
		// 是否已撤销 = 数据字典.是否.否
		trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
		// 投管业务类型=数据字典.投管业务类型.清理核销
		trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_资产减值清理_清理核销.getValue());
		// 业务类型=数据字典.投管业务类型.清理核销
		trdFinProductDealTotal.setBusiType(DictCodeListExt3.共通业务类型_清理核销.getValue());
		// 交易份额 =SUM（明细.持仓份额）
		trdFinProductDealTotal.setShareAmt(durationDto.getShareAmt());
		// 交易本金=SUM（明细.持仓成本）
		trdFinProductDealTotal.setPrinAmt(durationDto.getPrinAmt());
		// 利息总额=SUM（明细.持仓应收利息）
		trdFinProductDealTotal.setIntAmt(durationDto.getIntAmt());
		// 上级汇总交易编号=2.交易编号
		trdFinProductDealTotal.setpTotleTradeId(tradeId);
		// 投资组合代码 =明细.组合代码
		trdFinProductDealTotal.setPortfolioId(durationDto.getPortfolioId());
		trdFinProductDealTotal.setTradeDate(durationDto.getTradeDate());
		trdFinProductDealTotal.setSettleDate(durationDto.getTradeDate());
		// 通道代码 = 调用【通道查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", durationDto.getSecAcctId());
		int a = trdAssetLossClearMapper.selectChannelQuery(param);
		if (a > 0) {
			trdFinProductDealTotal.setChlId(durationDto.getSecAcctId());
		} else {
			trdFinProductDealTotal.setChlId(null);
		}
		trdFinProductDealTotal.setInvAim(durationDto.getInvAim());
		trdFinProductDealTotal.setCcy(durationDto.getCcy());
		trdFinProductDealTotal.setIsClean(DictCodeList.是否_是.getValue());
		return trdFinProductDealTotal;
	}

	/**
	 * 登录金融产品交易汇总附属表2
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdDealTotalAdd insertTdtaTwo(TrdAssetLossClearDto durationDto, String tradeId) {
		TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
		// 汇总交易编号 =金融产品交易汇总表.汇总交易编号
		trdDealTotalAdd.setTotleTradeId(tradeId);
		// 清理应收款=SUM（明细.已到期本息）
		trdDealTotalAdd.setClearRecAmt(durationDto.getClearRecAmt());
		// 罚息金额=SUM（明细.应收逾期罚息）
		trdDealTotalAdd.setPenaltyIntamt(durationDto.getPenaltyIntAmt());
		// 清理减值=SUM（明细.当前账面减值余额）
		trdDealTotalAdd.setClearLossAmt(durationDto.getClearLossAmt());
		return trdDealTotalAdd;
	}

	/**
	 * 登录金融产品交易表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdProductDeal insertTpd(TrdAssetLossClearDto durationDto, String tradeComeIdTwo, String tradeComeId,
			Date clearDate) {
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		// 交易编号 = 采番
		trdProductDeal.setTradeId(tradeComeIdTwo);
		// 业务类型 = if 明细.金融产品类型（估值） = 债券 then 数据字典.业务类型.债券交易
		// if 明细.金融产品类型（估值） = 存款 then 数据字典.业务类型.存款类资产交易
		// if 明细.金融产品类型（估值） = 计划类资产 then 数据字典.业务类型.计划类资产交易
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_债券交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
				|| durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_存款类资产交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_计划类资产预交易.getValue());
		}
		// 金融产品代码 = 明细.金融产品代码 、
		trdProductDeal.setFinprodId(durationDto.getFinprodId());
		// 金融产品类型（估值核算）= 明细.金融产品类型（估值核算）
		trdProductDeal.setFinprodType(durationDto.getFinprodType());
		// 金融产品类型（投管分类）= 明细.金融产品类型（投管分类）
		trdProductDeal.setFinprodType2(durationDto.getFinprodType2());
		// 分支序号 = 0
		trdProductDeal.setBranch(new Long(0));
		// 交易日 = 画面.清理日
		trdProductDeal.setTradeDate(clearDate);
		// 交割日 = 画面.清理日
		trdProductDeal.setSettleDate(clearDate);
		// 清算速度 = 0
		trdProductDeal.setSettleSpeed(new Long(0));
		// 交易币种 = 明细.原币
		trdProductDeal.setCcy(durationDto.getCcy());
		// 单位面值 = 明细.单位面值
		trdProductDeal.setParValue(durationDto.getUnitValue());
		// 交易份额 = 明细.持仓份额
		trdProductDeal.setShareAmt(durationDto.getShareAmt());
		// 交易本金 = 明细.持仓成本
		trdProductDeal.setPrinAmt(durationDto.getPrinAmt());
		// 利息总额 = 明细.持仓应收利息
		trdProductDeal.setIntAmt(durationDto.getIntAmt());
		// 交易方向 = 数据字典.交易方向.清理核销
		trdProductDeal.setPs(DictCodeListExt3.交易方向_清理核销.getValue());
		// 投资目的 = 明细.投资目的
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
             || durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
		     trdProductDeal.setInvAim(null);
		}else{
		     trdProductDeal.setInvAim(durationDto.getInvAim());
		}
		
		// 交易状态=数据字典.交易状态.有效
		trdProductDeal.setTradeStatus(DictCodeListExt1.有效状态_有效.getValue());
		// 是否已撤销 = 数据字典.是否.否
		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		// 是否结清 = if 画面.清理方式 = 一次清理 then 数据字典.是否.是
		trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
		// 汇总交易编号 = 金融产品交易汇总表.汇总交易编号
		trdProductDeal.setTotleTradeId(tradeComeId);
		// 通道代码 = 调用【通道查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", durationDto.getSecAcctId());
		int a = trdAssetLossClearMapper.selectChannelQuery(param);
		if (a > 0) {
			trdProductDeal.setChlId(durationDto.getSecAcctId());
		} else {
			trdProductDeal.setChlId(null);
		}
		// 是否外部交易 = 数据字典.是否.是
		trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
		// 交易场所 = 明细.交易场所
		trdProductDeal.setTradeMarket(durationDto.getTradeMarket());
		// 证券管理户代码 调用【证券账户查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> paramb = new HashMap<>();
		paramb.put("finprodId", durationDto.getSecAcctId());
		int b = trdAssetLossClearMapper.selectSecuritiesAccountEnquiry(paramb);
		if (b > 0) {
			trdProductDeal.setSecManageAcctId(durationDto.getSecAcctId());
		} else {
			trdProductDeal.setSecManageAcctId("999999");
		}
		// 投管业务类型 = 数据字典.投管业务类型.清理核销
		trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_资产减值清理_清理核销.getValue());
		return trdProductDeal;
	}

	/**
	 * 登录金融产品交易附属表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdProductDealAdd inserTpda(TrdAssetLossClearDto durationDto, String tradeId, Date clearDate) {
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		// 交易编号 = 金融产品交易.交易编号
		trdProductDealAdd.setTradeId(tradeId);
		// 投资组合代码 = 明细.组合代码
		trdProductDealAdd.setPortfolioId(durationDto.getPortfolioId());
		// 生成方式 = 数据字典.生成方式.画面录入
		trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		return trdProductDealAdd;
	}

	/**
	 * 登录组合交易表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public PtlTrade inserPt(TrdAssetLossClearDto durationDto, String tradeId, Date clearDate) {
		PtlTrade ptlTrade = new PtlTrade();
		// 组合代码 = 明细.组合代码
		ptlTrade.setPortfolioId(durationDto.getPortfolioId());
		// 业务编号 = 金融产品交易.交易编号
		ptlTrade.setBusiNo(tradeId);
		// 业务表 = 数据字典.业务表.交易表
		ptlTrade.setBusiTable("02");
		// 业务明细代码 = 明细.金融产品代码
		ptlTrade.setBusiId(durationDto.getFinprodId());
		// 业务类型 = if 明细.金融产品类型（估值） = 债券 then 数据字典.业务类型.债券交易
		// if 明细.金融产品类型（估值） = 存款 then 数据字典.业务类型.存款类资产交易
		// if 明细.金融产品类型（估值） = 计划类资产 then 数据字典.业务类型.计划类资产交易
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_债券交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
				|| durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_存款类资产交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_计划类资产预交易.getValue());
		}
		// 投资目的 = 明细.投资目的
		ptlTrade.setInvAim(durationDto.getInvAim());
		// 证券托管户代码 = 调用【证券账户查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", durationDto.getSecAcctId());
		int b = trdAssetLossClearMapper.selectSecuritiesAccountEnquiry(param);
		if (b > 0) {
			ptlTrade.setSecManageAcctId(durationDto.getSecAcctId());
		} else {
			ptlTrade.setSecManageAcctId(null);
		}
		// 实际发生日 = 画面.清理日
		ptlTrade.setSettleDate(clearDate);
		return ptlTrade;
	}

	/**
	 * 登录金融产品交易表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdProductDeal insertTpd2(TrdAssetLossClearDto durationDto, TrdAssetLossClearDto dto, String tradeId1,
			String tradeId2, Date clearDate) {
		TrdProductDeal trdProductDeal = new TrdProductDeal();
		// 交易编号 = 采番
		trdProductDeal.setTradeId(tradeId1);
		// 业务类型 = if 明细.金融产品类型（估值） = 债券 then 数据字典.业务类型.债券交易
		// if 明细.金融产品类型（估值） = 存款 then 数据字典.业务类型.存款类资产交易
		// if 明细.金融产品类型（估值） = 计划类资产 then 数据字典.业务类型.计划类资产交易
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_债券交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
				|| durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_存款类资产交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())) {
			trdProductDeal.setBusiType(DictCodeList.业务类型_计划类资产预交易.getValue());
		}
		// 金融产品代码 = 明细.金融产品代码 、
		trdProductDeal.setFinprodId(durationDto.getFinprodId());
		// 金融产品类型（估值核算）= 明细.金融产品类型（估值核算）
		trdProductDeal.setFinprodType(durationDto.getFinprodType());
		// 金融产品类型（投管分类）= 明细.金融产品类型（投管分类）
		trdProductDeal.setFinprodType2(durationDto.getFinprodType2());
		// 分支序号 = 0
		trdProductDeal.setBranch(new Long(dto.getBranch()));
		// 交易日 = 画面.清理日
		trdProductDeal.setTradeDate(clearDate);
		// 交割日 = 画面.清理日
		trdProductDeal.setSettleDate(clearDate);
		// 清算速度 = 0
		trdProductDeal.setSettleSpeed(new Long(0));
		// 交易币种 = 明细.原币
		trdProductDeal.setCcy(durationDto.getCcy());
		// 单位面值 = 明细.单位面值
		trdProductDeal.setParValue(durationDto.getUnitValue());
		// 交易份额 = 明细.持仓份额
		// trdProductDeal.setShareAmt(durationDto.getShareAmt());
		// 交易本金=if 应收款明细.分支类型 = 数据字典.分支类型.本金 then 应收款明细.应收款 else null
		if (dto.getBranchType().equals(DictCodeList.分支类型_应收本金.getValue())) {
			trdProductDeal.setPrinAmt(dto.getReceAmt());
		} else {
			trdProductDeal.setPrinAmt(null);
		}
		// if 应收款明细.分支类型 = 数据字典.分支类型.利息 then 应收款明细.应收款 else null
		if (dto.getBranchType().equals(DictCodeList.分支类型_应收利息.getValue())) {
			trdProductDeal.setIntAmt(dto.getReceAmt());
		} else {
			trdProductDeal.setIntAmt(null);
		}
		// 交易方向
		// if 应收款明细.分支类型 = 数据字典.分支类型.本金 then 数据字典.交易方向.其他应收款本金-清理核销
		// if 应收款明细.分支类型 = 数据字典.分支类型.利息 then 数据字典.交易方向.其他应收款利息-清理核销
		if (dto.getBranchType().equals(DictCodeList.分支类型_应收本金.getValue())) {
			trdProductDeal.setPs(DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue());
		} else if (dto.getBranchType().equals(DictCodeList.分支类型_应收利息.getValue())) {
			trdProductDeal.setPs(DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue());
		}
		// 投资目的 = 明细.投资目的
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
            || durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
            trdProductDeal.setInvAim(null);
        }else{
            trdProductDeal.setInvAim(dto.getInvAim());
        }
		
		// 交易状态=数据字典.交易状态.有效
		trdProductDeal.setTradeStatus(DictCodeListExt1.有效状态_有效.getValue());
		// 是否已撤销 = 数据字典.是否.否
		trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
		// 是否结清 = if 画面.清理方式 = 一次清理 then 数据字典.是否.是
		trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());
		// 汇总交易编号 = 金融产品交易汇总表.汇总交易编号
		trdProductDeal.setTotleTradeId(tradeId2);
		// 通道代码 = 调用【通道查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", durationDto.getSecAcctId());
		int a = trdAssetLossClearMapper.selectChannelQuery(param);
		if (a > 0) {
			trdProductDeal.setChlId(durationDto.getSecAcctId());
		} else {
			trdProductDeal.setChlId(null);
		}
		// 是否外部交易 = 数据字典.是否.是
		trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
		// 交易场所 = 明细.交易场所
		trdProductDeal.setTradeMarket(durationDto.getTradeMarket());
		// 证券管理户代码 调用【证券账户查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> paramb = new HashMap<>();
		paramb.put("finprodId", dto.getSecAcctId());
		int b = trdAssetLossClearMapper.selectSecuritiesAccountEnquiry(paramb);
		if (b > 0) {
			trdProductDeal.setSecManageAcctId(durationDto.getSecAcctId());
		} else {
			trdProductDeal.setSecManageAcctId(null);
		}
		// 投管业务类型 = 数据字典.投管业务类型.清理核销
		trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_资产减值清理_清理核销.getValue());
		return trdProductDeal;
	}

	/**
	 * 登录金融产品交易附属表
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public TrdProductDealAdd inserTpda2(TrdAssetLossClearDto durationDto, String tradeId, Date clearDate) {
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		// 交易编号 = 金融产品交易.交易编号
		trdProductDealAdd.setTradeId(tradeId);
		// 投资组合代码 = 明细.组合代码
		trdProductDealAdd.setPortfolioId(durationDto.getPortfolioId());
		// 生成方式 = 数据字典.生成方式.画面录入
		trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		// 罚息金额= 明细.罚息金额
		trdProductDealAdd.setPenaltyIntamt(durationDto.getPenaltyIntAmt());
		return trdProductDealAdd;
	}

	/**
	 * 登录组合交易表2
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public PtlTrade inserPt2(TrdAssetLossClearDto durationDto, TrdAssetLossClearDto dto, String tradeId,
			Date clearDate) {
		PtlTrade ptlTrade = new PtlTrade();
		// 组合代码 = 明细.组合代码
		ptlTrade.setPortfolioId(dto.getPortfolioId());
		// 业务编号 = 金融产品交易.交易编号
		ptlTrade.setBusiNo(tradeId);
		// 业务表 = 数据字典.业务表.交易表
		ptlTrade.setBusiTable("02");
		// 业务明细代码 = 明细.金融产品代码
		ptlTrade.setBusiId(dto.getFinprodId());
		// 业务类型 = if 明细.金融产品类型（估值） = 债券 then 数据字典.业务类型.债券交易
		// if 明细.金融产品类型（估值） = 存款 then 数据字典.业务类型.存款类资产交易
		// if 明细.金融产品类型（估值） = 计划类资产 then 数据字典.业务类型.计划类资产交易
		if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_债券交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_存款.getValue())
				|| durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_同业借款.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_存款类资产交易.getValue());
		} else if (durationDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_计划类资产.getValue())) {
			ptlTrade.setBusiType(DictCodeList.业务类型_计划类资产预交易.getValue());
		}
		// 投资目的 = 明细.投资目的
		ptlTrade.setInvAim(dto.getInvAim());
		// 证券托管户代码 = 调用【证券账户查询】 参数：明细.证券账户/通道代码
		// if 返回值>0 则 明细.证券账户/通道代码 else null
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", durationDto.getSecAcctId());
		int b = trdAssetLossClearMapper.selectSecuritiesAccountEnquiry(param);
		if (b > 0) {
			ptlTrade.setSecManageAcctId(durationDto.getSecAcctId());
		} else {
			ptlTrade.setSecManageAcctId(null);
		}
		// 实际发生日 = 画面.清理日
		ptlTrade.setSettleDate(clearDate);
		return ptlTrade;
	}

	/**
	 * 交易编号自动生成
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public String createStrategyId() {
		String strategyId = null;
		while (true) {
			// 获取自增数
			String nextId = this.getNextId("LOSS_SEQUENCE");
			// 获取平台日
			String sysDate = DateUtil.format(DateUtil.platDateFromCache(), "yyyyMMdd");
			// 拼接
			strategyId = "TED" + "LOSSCLEAR" + sysDate + nextId;
			return strategyId;
		}
	}

	/**
	 * 已清理资产查询
	 * 
	 * @return
	 * @see
	 * @since 1.0
	 */
	public int selectAredyLossAsset(Map<String, Object> param) {
		int count = trdAssetLossClearMapper.selectAredyLossAsset(param);
		return count;
	}

	/**
	 * 获取自增序列值
	 * 
	 * @param sequenceName
	 *            序列名
	 * @return
	 */
	public String getNextId(String sequenceName) {
		return trdAssetLossClearMapper.getNextId(sequenceName);
	}

	/**
	 * 加载查询列表
	 *
	 * @param page
	 *            分页用变量
	 * @param length
	 *            分页用变量
	 * @param param
	 *            查询参数
	 * @return 查询结果
	 * @see
	 * @since 1.0
	 */
	public Page<TrdAssetLossClearDto> findPage(int page, int length, Map<String, Object> param) {
		return queryForPageForSelectReturn(page, length, () -> trdAssetLossClearMapper.findPage(param));
	}

	/**
	 * 加载查询列表
	 *
	 * @param page
	 *            分页用变量
	 * @param length
	 *            分页用变量
	 * @param param
	 *            查询参数
	 * @return 查询结果
	 * @see
	 * @since 1.0
	 */
	public Page<TrdAssetLossClearDto> findNewPage(int page, int length, Map<String, Object> param) {
		return queryForPageForSelectReturn(page, length, () -> trdAssetLossClearMapper.findNewPage(param));
	}

	/**
	 * 
	 * 双击查看、修改取得数据源 <功能详细描述>
	 * 
	 * @param param
	 *            Map<String, Object>
	 * @return FinProductRateQuotationDto
	 * @see
	 * @since 1.0
	 */
	public List<TrdAssetLossClearDto> findNewList(Map<String, Object> param) {
		return trdAssetLossClearMapper.findNewPage(param);
	}

	/**
	 * 
	 * 双击查看、修改取得数据源 <功能详细描述>
	 * 
	 * @param param
	 *            Map<String, Object>
	 * @return FinProductRateQuotationDto
	 * @see
	 * @since 1.0
	 */
	public List<TrdAssetLossClearDto> selectDetail(Map<String, Object> param) {
		return trdAssetLossClearMapper.selectDetail(param);
	}

	/**
	 * 
	 * 双击查看、修改取得数据源 <功能详细描述>
	 * 
	 * @param param
	 *            Map<String, Object>
	 * @return FinProductRateQuotationDto
	 * @see
	 * @since 1.0
	 */
	public Page<TrdAssetLossClearDto> selectByKey(int page, int length, Map<String, Object> param) {
		return queryForPageForSelectReturn(page, length, () -> trdAssetLossClearMapper.selectByKey(param));
	}

	/**
	 * 查询组合代码 <功能详细描述>
	 * 
	 * @param page
	 * @param length
	 * @param param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Page<SelectOptionDto> finprodMarketId(int page, int length, Map<String, Object> param) {
		return queryForPageForSelectReturn(page, length, () -> trdAssetLossClearMapper.finprodMarketId(param));
	}

	/**
	 *
	 * 导出查询 <功能详细描述>
	 * 
	 * @param
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<TrdAssetLossClearDto> selectForExport(Map<String, Object> param) {
		return trdAssetLossClearMapper.findPage(param);
	}

}
