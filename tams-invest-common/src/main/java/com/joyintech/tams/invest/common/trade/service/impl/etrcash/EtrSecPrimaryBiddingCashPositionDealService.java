package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.PtlCashPositionFlowMapper;
import com.joyintech.tams.common.base.entity.BidOrderBidPrice;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.joyintech.tams.constant.DictCodeListExt3.头寸业务类型_债券一级投标;

/**
 * @program: 2.1.0
 * @description:债券一级投标
 * @author: 
 * @create: 2019-10-14 
 **/
@Service
public class EtrSecPrimaryBiddingCashPositionDealService extends EtrCashPositionBaseService {


	private static BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;

	private static PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

	public static void deal债券一级投标EntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
		EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
		// 委托生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
            entrustCommonDto);
        String acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(),
                etrEntrust.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
        // 减少投标券面总额对应的可用现金 add by xxg entrustNum 非空判断  20210802
        etrCashPositionFlowDto.setAmount(ArithmeticUtil.multiply(etrEntrust.getEntrustNum() ==null?new BigDecimal(0):etrEntrust.getEntrustNum(), new BigDecimal(100)).negate());
        etrCashPositionFlowDto.setAcctId(acctId);
        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
	}
	
	public static void deal债券一级投标EntrustEnd(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
		// 一级投标指令 退回时不处理
		EtrEntrustAdd etrEntrustAdd = entrustCommonDto.getEtrEntrustAdd();
		if (StringUtil.equals(etrEntrustAdd.getBusiTypePosition(), 头寸业务类型_债券一级投标.getValue())) {
			return;
		}
		EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
		// 委托生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
            entrustCommonDto);
        String acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(),
                etrEntrust.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
        //释放投标券面总额对应的可用现金
        etrCashPositionFlowDto.setAmount(ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), new BigDecimal(100)));
        etrCashPositionFlowDto.setAcctId(acctId);
        etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
		etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
	}

	public static void 中标结果维护(BidOrderBidPrice bidPriceDto, EtrPositionHoldDto etrPositionHoldDto) {
		// 委托代码
		String entrustId = bidPriceDto.getEntrustId();
		// 委托dto
		EntrustCommonDto entrustCommonDto = EtrCommonCashPositionDealService.findEntrust(entrustId);
		EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
		// 交易生成的现金流水
		EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService
				.getEtrCashPositionFlowDtoFromEtr(entrustCommonDto);
		String acctId = EtrCommonCashPositionDealService.select组合托管户(etrEntrust.getPortfolioId(), etrEntrust.getCcy(),
				DictCodeListExt2.资金账户类型_托管户.getValue());
		// 释放未中标券面总额对应的可用现金
		BigDecimal 投标券面总额 = bidPriceDto.getTheWinPrinAmt();
		BigDecimal 实际中标券面总额 = bidPriceDto.getWinPrinAmt();
        etrCashPositionFlowDto.setAmount(ArithmeticUtil.subtract(实际中标券面总额, 投标券面总额));
		etrCashPositionFlowDto.setAcctId(acctId);
		etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
		/**add by sunhui 20210719 重庆银行：中标结果维护，【是否头寸冻结】为是时，冻结组合可用现金   start**/
		if(StringUtil.equals(bidPriceDto.getIsFrozenPosition(), BussinessDictCodeList.是否_是.getValue())){
			//1.指令可用冻结
			EtrCashPositionFlowDto etrCashPositionFlowDtoFrozen = new EtrCashPositionFlowDto();
			BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDtoFrozen, etrCashPositionFlowDto);
			etrCashPositionFlowDtoFrozen.setSeqno(IDGenerator.generate());
			etrCashPositionFlowDtoFrozen.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());//冻结的现金
			//冻结的头寸 = 中标券面总额*100*净价
			etrCashPositionFlowDtoFrozen.setAmount(ArithmeticUtil.multiply(ArithmeticUtil.divide(实际中标券面总额,new BigDecimal("100"),2, RoundingMode.HALF_UP), new BigDecimal("-1"),bidPriceDto.getWinCprice()));
			etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDtoFrozen);
			//2.组合可用冻结
			PtlCashPositionFlowDto PtlCashPositionFlowDto = new PtlCashPositionFlowDto();
			BeanUtil.copyPropertiesIgnoreNull(PtlCashPositionFlowDto,etrCashPositionFlowDtoFrozen);
			PtlCashPositionFlowDto.setSeqno(IDGenerator.generate());
			PtlCashPositionFlowDto.setHodingType(DictCodeList.持仓类型_可用.getValue());
			getBasePtlCashPositionInfoServiceImpl().insertCashPositionFlow(PtlCashPositionFlowDto);
			//在缴款日生成一条负金额数据，用于解除冻结
			PtlCashPositionFlowDto.setSeqno(IDGenerator.generate());
			PtlCashPositionFlowDto.setCdate(bidPriceDto.getPayDate());
			PtlCashPositionFlowDto.setAmount(PtlCashPositionFlowDto.getAmount().negate());
			basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(PtlCashPositionFlowDto);
		}
		/**add by sunhui 20210719 重庆银行：中标结果维护，【是否头寸冻结】为是时，冻结组合可用现金   end**/
	}

	public void getPtlCashPositionFlowMapper(PtlCashPositionFlowMapper ptlCashPositionFlowMapper) {
		EtrSecPrimaryBiddingCashPositionDealService.ptlCashPositionFlowMapper = ptlCashPositionFlowMapper;
	}

	private static synchronized PtlCashPositionFlowMapper getPtlCashPositionFlowMapper() {
		if(null == ptlCashPositionFlowMapper) {
			ptlCashPositionFlowMapper = SpringContextUtil.getBean(PtlCashPositionFlowMapper.class);
		}
		return ptlCashPositionFlowMapper;
	}

	private static synchronized BasePtlCashPositionInfoService getBasePtlCashPositionInfoServiceImpl() {
		if(null == basePtlCashPositionInfoServiceImpl) {
			basePtlCashPositionInfoServiceImpl = SpringContextUtil.getBean(BasePtlCashPositionInfoService.class);
		}
		return basePtlCashPositionInfoServiceImpl;
	}
}
