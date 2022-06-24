package com.joyintech.tams.invest.batch.assetintdatecarryover.service;

//import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dao.AssetIntdateCarryForwardMapper;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dto.AssetIntdateCarryForwardReaderResult;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dto.AssetIntdateCarryForwardResult;
import com.joyintech.tams.invest.batch.assetintdatecarryover.dto.AssetIntdateCarryForwardDto;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 资产理论日结转
 *
 * @author 金茂东
 */
@Service
public class AssetIntdateCarryForwardProcessorService {
    @Autowired
    AssetIntdateCarryForwardMapper assetIntdateCarryForwardMapper;

    public AssetIntdateCarryForwardResult execute(AssetIntdateCarryForwardReaderResult item, Map<String, Object> params) {
        if (!CollectionUtils.isNotEmpty(item.getOverdueFinTradeProductForwardDtoList())) {
            return null;
        }
        AssetIntdateCarryForwardResult result = new AssetIntdateCarryForwardResult();

        Date platDate = (Date) params.get(BatchConstant.PLATE_DATE);


        Map<String, FinTradeProduct> resultMap = new HashMap<String, FinTradeProduct>();

        for (AssetIntdateCarryForwardDto dto : item.getOverdueFinTradeProductForwardDtoList()) {

            String TASK_ID = "TASK_1024";
            if (params.containsKey("TASK_ID")) {
                TASK_ID = (String) params.get("TASK_ID");
            }
            Long branchPri;
            String branchType;
            BigDecimal tradeAmt;
            if (dto.getCashType1().equals(DictCodeListExt1.现金流一级类型_本金.getValue())) {
                branchPri = new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + DictCodeListExt1.现金流一级类型_本金.getValue());
                branchType = DictCodeList.分支类型_应收本金.getValue();
                tradeAmt = dto.getPrinAmt();
            } else {
                branchPri = new Long(DateUtil.format(dto.getMdate(), "yyyyMMdd") + DictCodeListExt1.现金流一级类型_利息.getValue());
                branchType = DictCodeList.分支类型_应收利息.getValue();
                tradeAmt = dto.getIntAmt();
            }
            String key = dto.getFinprodId() + branchPri;
            if (!resultMap.containsKey(key) || resultMap.get(key) == null) {
                FinTradeProduct ftp = new FinTradeProduct();
                ftp.setFinprodId(dto.getFinprodId());
                ftp.setFinprodType(dto.getFinprodType());
                ftp.setFinprodType2(dto.getFinprodType2());
                ftp.setBranch(branchPri);
                ftp.setBranchType(branchType);
                ftp.setChlAgrtId(dto.getChlFinprodId());
                ftp.setCcy(dto.getCcy());
                ftp.setVdate(dto.getVdate());
                ftp.setMdate(dto.getMdate());
                ftp.setmPrinAmt(dto.getPrinAmt());
                ftp.setmIntAmt(dto.getIntAmt());
                ftp.setmTradeAmt(tradeAmt);
                ftp.setoFinprodId(dto.getFinprodId());
                ftp.setCreateUser(TASK_ID);
                ftp.setUpdateUser(TASK_ID);
                resultMap.put(dto.getFinprodId() + branchPri, ftp);
            }
        }
        for (String skey : resultMap.keySet()) {
            result.getFinTradeProductList().add(resultMap.get(skey));
        }
        return result;
    }
//
//
//	/**
//	 * 当前未还本金
//	 *
//	 * @param happenDate 发生日
//	 * @param printAmt 本金
//	 * @param finprodId 标的代码
//	 * @return 市价净价
//	 * @see
//	 * @since 1.0
//	 */
//	public BigDecimal getUnpaidPrinAmt(Date happenDate, String finprodId, BigDecimal printAmt) {
//		List<FinCashPlan> targetCashPlanList = assetIntdateCarryForwardMapper.queryCashPlan(finprodId,DictCodeListExt1.现金流一级类型_本金.getValue());
//		BigDecimal allPayAmt = BigDecimal.ZERO;
//		if (targetCashPlanList.size() > 0) {
//			for (FinCashPlan plan : targetCashPlanList) {
//				if (plan.getMdate().before(happenDate)) {
//					allPayAmt = allPayAmt.add(plan.getPrinAmt());
//				}
//			}
//		}
//		printAmt = printAmt.subtract(allPayAmt);
//		return printAmt;
//	}
}
