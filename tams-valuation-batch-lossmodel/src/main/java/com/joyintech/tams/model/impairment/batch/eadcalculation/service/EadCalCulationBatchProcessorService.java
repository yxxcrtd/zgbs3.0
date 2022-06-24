package com.joyintech.tams.model.impairment.batch.eadcalculation.service;

import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.model.impairment.batch.common.utils.ArithmeticUtil1;
import com.joyintech.tams.model.impairment.batch.eadcalculation.dao.EadCalculationBatchMapper;
import com.joyintech.tams.model.impairment.batch.eadcalculation.dto.*;
import com.joyintech.tams.valuation.common.dto.modelDto.ModCashFlowCalRuleDto;
import com.joyintech.tams.valuation.common.dto.modelDto.ModCashFlowDto;
import com.joyintech.tams.valuation.common.dto.modelDto.ModCashFlowPayRulesDto;
import com.joyintech.tams.common.base.entity.ModEclResultProcess;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EAD计算Processor业务层
 *
 * @author zhangfei
 * @date 2021年7月14日17:18:50
 */
@Service
@Scope("prototype")
public class EadCalCulationBatchProcessorService {

    private Logger logger = LoggerFactory.getLogger(EadCalCulationBatchProcessorService.class);
    @Autowired
    private EadCalculationBatchMapper eadCalculationBatchMapper;

    private StepExecution stepExecution;
    @Autowired
    private ModLogsService modLogsService;

    public EadCalCulationResultDto execute(Map<String, Object> param, StepExecution execution) {

        logger.info("---------EAD计算Processor业务层开始---------");
        stepExecution = execution;
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo) param.get("BATCH_INFO");
        SysBatchTask batchtask = (SysBatchTask) param.get("BATCHTASK");
        // 定义返回list
        EadCalCulationResultDto result = new EadCalCulationResultDto();

        // 获取reader返回值
        EadCalculationReaderDto readerDto = (EadCalculationReaderDto) param.get(GlobalConstant.READER_RESULT);
        Date excuteDate = (Date) param.get(GlobalConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        // 获取买入交易列表并排序 升序
        List<EadModEclResultProcess> tradeInList = readerDto.getTradeInList();
        if (CollectionUtils.isNotEmpty(tradeInList)) {
            Collections.sort(tradeInList, Comparator.comparing(EadModEclResultProcess::getTradeDate, (t1, t2) -> t2.compareTo(t1)));
        }
        // 获取卖出交易列表并排序 升序
        List<EadModEclResultProcess> tradeOutList = readerDto.getTradeOutList();
        // 定义 Map<String,BigDecimal> 交易对应应收未收本息和Map
        Map<String, BigDecimal> unPrincipleAndInterest = new HashMap<String, BigDecimal>();
        try {
            if (CollectionUtils.isNotEmpty(tradeOutList)) {
                Collections.sort(tradeOutList, Comparator.comparing(EadModEclResultProcess::getTradeDate, (t1, t2) -> t2.compareTo(t1)));
                // 处理每一笔买入交易的移动平均数量
                tradeOutList.forEach(itemOut -> {
                    //移动数量总额 = 0
                    BigDecimal mobileNum = BigDecimal.ZERO;
                    // 已处理买入交易数量 = 0
                    BigDecimal dealTradeInNum = BigDecimal.ZERO;
                    // 已扣减卖出交易数量 = 0
                    BigDecimal deductTradeOutNum = BigDecimal.ZERO;
                    // 遍历 买入交易i : 买入交易列表
                    if (CollectionUtils.isNotEmpty(tradeInList)) {
                        for (EadModEclResultProcess itemIn : tradeInList) {
                            if (DateUtil.compareDate(itemOut.getTradeDate(), itemIn.getTradeDate()) >= 0) {
                                mobileNum = ArithmeticUtil.doAdd(mobileNum, itemIn.getEmaShareAmt());
                            }
                        }
                        for (EadModEclResultProcess itemIn : tradeInList) {
                            if (DateUtil.compareDate(itemOut.getTradeDate(), itemIn.getTradeDate()) >= 0) {
                                deductTradeOutNum = ArithmeticUtil.doAdd(dealTradeInNum, itemIn.getEmaShareAmt());
                                if (dealTradeInNum.compareTo(mobileNum) == 0) {
                                    itemIn.setEmaShareAmt(ArithmeticUtil1.doSub(itemIn.getEmaShareAmt(), ArithmeticUtil1.doSub(itemOut.getEmaShareAmt(), deductTradeOutNum)));
                                    break;
                                }
                                // 定义 扣减份额 = 卖出交易i.交易份额 * 买入交易i.移动平均后份额 / 移动数量总额（四舍五入，保留两位小数）
                                BigDecimal deductNum = BigDecimal.ZERO;
                                if (mobileNum.compareTo(BigDecimal.ZERO) != 0) {
                                    deductNum = ArithmeticUtil.divide(ArithmeticUtil.multiply(itemOut.getShareAmt(), itemIn.getEmaShareAmt()), mobileNum, 2, RoundingMode.HALF_UP);
                                }
                                itemIn.setEmaShareAmt(isNullBigDecimal(itemIn.getEmaShareAmt()).subtract(deductNum));
                                deductTradeOutNum = ArithmeticUtil.doAdd(deductTradeOutNum, deductNum);
                            }
                        }
                    }
                });
            }
            // 查询存续期交易列表
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("assetId", readerDto.getAssetId());
            paraMap.put("PS_T22", DictCodeList.交易方向_收息到账.getValue());
            paraMap.put("PS_T23", DictCodeList.交易方向_还本到账.getValue());
            List<ModEclResultProcess> durationList = eadCalculationBatchMapper.selectDurationTradeByAssetId(paraMap);
            //资产还本交易列表 取上述查询结果交易方向为'T23 还本到账'的数据
            List<ModEclResultProcess> listT23 = new ArrayList<ModEclResultProcess>();
            // 资产收息交易列表 取上述查询结果交易方向为'T22 收息到账'的数据
            List<ModEclResultProcess> listT22 = new ArrayList<ModEclResultProcess>();
            if (CollectionUtils.isNotEmpty(durationList)) {
                listT23 = durationList.stream().filter(item -> DictCodeList.交易方向_还本到账.getValue().equals(item.getPs())).collect(Collectors.toList());
                listT22 = durationList.stream().filter(item -> DictCodeList.交易方向_收息到账.getValue().equals(item.getPs())).collect(Collectors.toList());
            }
            // 定义 资产还本计划列表 = 变量.现金流计划列表.还本计划
            List<ModCashFlowDto> assetDebtList = new ArrayList<ModCashFlowDto>();
            // 定义 资产付息计划列表 = 变量.现金流计划列表.付息计划
            List<ModCashFlowDto> assetServicingList = new ArrayList<ModCashFlowDto>();
            if (CollectionUtils.isNotEmpty(readerDto.getCashFlowList())) {
                assetDebtList = readerDto.getCashFlowList().stream().filter(i -> DictCodeListExt1.现金流二级类型_本金.getValue().equals(i.getCashType2())).collect(Collectors.toList());
                assetServicingList = readerDto.getCashFlowList().stream().filter(i -> DictCodeListExt1.现金流二级类型_利息.getValue().equals(i.getCashType2())).collect(Collectors.toList());
            }
            // 3.2 获取资产逾期列表数据
            List<OverDueTradeAssetDto> AssetLimitsList = new ArrayList<OverDueTradeAssetDto>();
            // 资产付息计划列表
            if (CollectionUtils.isNotEmpty(assetServicingList)) {
                for (ModCashFlowDto itemServicing : assetServicingList) {
                    // 定义 利息是否有交易 = 否
                    boolean isInterestTrade = false;
                    if (DateUtil.compareDate(itemServicing.getPayDate(), excuteDate) < 0) {
                        // 资产收息交易列表比那里
                        for (ModEclResultProcess item22 : listT22) {
                            //if (资产付息交易i.计息截止日 == 资产付息计划i.计息截止日 && 资产付息交易i.是否结清 == 已结清)
                            if (DateUtil.compareDate(item22.getPlanEndDate(), itemServicing.getMdate()) == 0 &&
                                    DictCodeList.是否_是.getValue().equals(item22.getIsClean())) {
                                isInterestTrade = true;
                                break;
                            }
                            // 如果存在交易且未结清
                            //if (资产付息交易i.计息截止日 == 资产付息计划i.计息截止日 && 资产付息交易i.是否结清 == 未结清)
                            if (DateUtil.compareDate(item22.getPlanEndDate(), itemServicing.getMdate()) == 0 &&
                                    DictCodeList.是否_否.getValue().equals(item22.getIsClean())) {
                                isInterestTrade = true;
                                OverDueTradeAssetDto dto = new OverDueTradeAssetDto();
                                dto.setPortfolioId(readerDto.getPortfolioId());
                                dto.setAssetId(readerDto.getAssetId());
                                dto.setModelAssetType(readerDto.getModelAssetType());
                                dto.setCashType(DictCodeListExt1.现金流二级类型_利息.getValue());
                                dto.setPlanEndDate(itemServicing.getMdate());
                                dto.setAmtReceived(isNullBigDecimal(item22.getTradeAmt()));
                                dto.setAccountAmt(isNullBigDecimal(itemServicing.getIntAmt()));
                                AssetLimitsList.add(dto);
                                break;
                            }
                        }
                        // 利息是否有交易 = 否
                        if (!isInterestTrade) {
                            OverDueTradeAssetDto dto = new OverDueTradeAssetDto();
                            dto.setPortfolioId(readerDto.getPortfolioId());
                            dto.setAssetId(readerDto.getAssetId());
                            dto.setModelAssetType(readerDto.getModelAssetType());
                            dto.setCashType(DictCodeListExt1.现金流二级类型_利息.getValue());
                            dto.setPlanEndDate(itemServicing.getMdate());
                            dto.setAccountAmt(isNullBigDecimal(itemServicing.getIntAmt()));
                            AssetLimitsList.add(dto);
                        }
                    }
                }
            }
            // 遍历 资产还本计划i : 资产还本计划列表
            if (CollectionUtils.isNotEmpty(assetDebtList)) {
                for (ModCashFlowDto itemdebt : assetDebtList) {
                    // 定义 本金是否有交易 = 否
                    boolean isInterestTrade = false;
                    if (DateUtil.compareDate(itemdebt.getPayDate(), excuteDate) < 0) {
                        for (ModEclResultProcess item23 : listT23) {
                            if (DateUtil.compareDate(item23.getPlanEndDate(), itemdebt.getMdate()) == 0 &&
                                    DictCodeList.是否_否.getValue().equals(item23.getIsClean())) {
                                isInterestTrade = true;
                                break;
                            }
                            // 如果存在交易且未结清
                            if (DateUtil.compareDate(item23.getPlanEndDate(), itemdebt.getMdate()) == 0 &&
                                    DictCodeList.是否_否.getValue().equals(item23.getIsClean())) {
                                isInterestTrade = true;
                                OverDueTradeAssetDto dto = new OverDueTradeAssetDto();
                                dto.setPortfolioId(readerDto.getPortfolioId());
                                dto.setAssetId(readerDto.getAssetId());
                                dto.setModelAssetType(readerDto.getModelAssetType());
                                dto.setCashType(DictCodeListExt1.现金流二级类型_本金.getValue());
                                dto.setPlanEndDate(itemdebt.getMdate());
                                dto.setAmtReceived(isNullBigDecimal(item23.getTradeAmt()));
                                dto.setAccountAmt(isNullBigDecimal(itemdebt.getIntAmt()));
                                AssetLimitsList.add(dto);
                                break;
                            }
                        }
                        // 本金是否有交易 = 否
                        if (!isInterestTrade) {
                            OverDueTradeAssetDto dto = new OverDueTradeAssetDto();
                            dto.setPortfolioId(readerDto.getPortfolioId());
                            dto.setAssetId(readerDto.getAssetId());
                            dto.setModelAssetType(readerDto.getModelAssetType());
                            dto.setCashType(DictCodeListExt1.现金流二级类型_本金.getValue());
                            dto.setPlanEndDate(itemdebt.getMdate());
                            dto.setAccountAmt(isNullBigDecimal(itemdebt.getIntAmt()));
                            AssetLimitsList.add(dto);
                        }
                    }
                }
            }
            // 3.3 计算逾期应收未收本息和
            if (CollectionUtils.isNotEmpty(AssetLimitsList)) {
                AssetLimitsList.forEach(item -> {
                    List<EadModEclResultProcess> buyList = new ArrayList<EadModEclResultProcess>();
                    List<EadModEclResultProcess> sellList = new ArrayList<EadModEclResultProcess>();
                    // 查询出该笔逾期交易对应的买入交易列表
                    if (CollectionUtils.isNotEmpty(tradeInList)) {
                        tradeInList.forEach(itemIn -> {
                            if (DateUtil.compareDate(itemIn.getTradeDate(), item.getPlanEndDate()) >= 0)
                                return;
                            else
                                itemIn.setLimitEmaShareAmt(isNullBigDecimal(itemIn.getShareAmt()));
                            buyList.add(itemIn);
                        });
                    }
                    // 查询出该笔逾期交易对应的卖出交易列表
                    if (CollectionUtils.isNotEmpty(tradeOutList)) {
                        tradeOutList.forEach(itemOut -> {
                            // 如果卖出交易是在逾期交易之后或当天,则不会影响该笔买入交易的EAD
                            if (DateUtil.compareDate(itemOut.getTradeDate(), item.getPlanEndDate()) >= 0)
                                return;
                            else
                                sellList.add(itemOut);
                        });
                    }
                    // 计算出该笔逾期交易时点对应的每一笔买入交易的移动平均数量
                    sellList.forEach(itemSell -> {
                        //定义 逾期时点移动数量总额 = 0
                        BigDecimal limitMobileNum = BigDecimal.ZERO;
                        // 定义 逾期时点已处理买入交易数量 = 0
                        BigDecimal limitDealTradeInNum = BigDecimal.ZERO;
                        // 定义 逾期时点已扣减卖出交易数量 = 0
                        BigDecimal limitDeductTradeOutNum = BigDecimal.ZERO;
                        for (EadModEclResultProcess itemBuy : buyList) {
                            if (DateUtil.compareDate(itemSell.getTradeDate(), itemBuy.getTradeDate()) >= 0) {
                                limitMobileNum = ArithmeticUtil.doAdd(limitMobileNum, itemBuy.getLimitEmaShareAmt());
                            }
                        }

                        for (EadModEclResultProcess itemBuy : buyList) {
                            if (DateUtil.compareDate(itemSell.getTradeDate(), itemBuy.getTradeDate()) >= 0) {
                                // 逾期时点已处理买入交易数量 =  逾期时点已处理买入交易数量 + 逾期时点买入交易i.逾期时点移动平均后份额
                                limitDealTradeInNum = ArithmeticUtil.doAdd(limitDealTradeInNum, itemBuy.getLimitEmaShareAmt());
                                //加上份额后等于移动数量总额，说明该笔交易是最后一笔买入交易，不需要再进行计算，将剩余未扣减的卖出份额全部扣减在该笔交易
                                if (limitDealTradeInNum.compareTo(limitMobileNum) == 0) {
                                    itemBuy.setLimitEmaShareAmt(ArithmeticUtil1.doSub(itemBuy.getLimitEmaShareAmt(), ArithmeticUtil1.doSub(itemSell.getShareAmt(), limitDeductTradeOutNum)));
                                    break;
                                }
                                BigDecimal deductShare = BigDecimal.ZERO;
                                if (limitMobileNum.compareTo(BigDecimal.ZERO) != 0) {
                                    // 定义 扣减份额 = 逾期时点卖出交易i.交易份额 * 逾期时点买入交易i.逾期时点移动平均后份额 / 逾期时点移动数量总额（四舍五入，保留两位小数）
                                    deductShare = ArithmeticUtil.divide(ArithmeticUtil.multiply(itemSell.getShareAmt(), itemBuy.getLimitEmaShareAmt()), limitMobileNum, 2, RoundingMode.HALF_UP);
                                }
                                itemBuy.setLimitEmaShareAmt(ArithmeticUtil.subtract(itemBuy.getLimitEmaShareAmt(), deductShare));
                                limitDeductTradeOutNum = ArithmeticUtil.doAdd(limitDeductTradeOutNum, deductShare);
                            }

                        }
                    });
                    // 计算：每一笔逾期交易对应买入交易的移动平均份额总和
                    //定义  逾期时点买入移动数量总额 = 0
                    BigDecimal limitTradeInMobileNum = BigDecimal.ZERO;
                    // 资产发行规模
                    BigDecimal assetIntAmt = BigDecimal.ZERO;
                    if (CollectionUtils.isNotEmpty(buyList)) {
                        assetIntAmt = buyList.get(0).getIssueAmt();
                    }
                    for (EadModEclResultProcess itemIn : buyList) {
                        limitTradeInMobileNum = ArithmeticUtil.doAdd(limitTradeInMobileNum, itemIn.getLimitEmaShareAmt());
                    }
                    // 增加应收未收金额计算方法：按前台计算方式计算（和需求确认了的）
                    // 定义应收金额
                    BigDecimal receivableAmt = BigDecimal.ZERO;
                    if (DictCodeListExt1.证券品种_其他债权类.getValue().equals(item.getModelAssetType())) {
                        //定义变量： 每百元应收金额  = 资产逾期交易.应收金额 / 资产发行规模 * 100
                        BigDecimal perhandrudAmt = BigDecimal.ZERO;
                        if (assetIntAmt.compareTo(BigDecimal.ZERO) != 0) {
//                            perhandrudAmt = ArithmeticUtil.divide(item.getAccountAmt(), ArithmeticUtil.multiply(assetIntAmt, BigDecimal.valueOf(100)));
                            perhandrudAmt = ArithmeticUtil.divide(item.getAccountAmt(), assetIntAmt);
                        }
                        receivableAmt = ArithmeticUtil.doAdd(receivableAmt, ArithmeticUtil.multiply( NumberEnum.MONEY.getCode(),null, limitTradeInMobileNum, perhandrudAmt));
                    } else {
                        receivableAmt = ArithmeticUtil.round(ArithmeticUtil.doAdd(receivableAmt, ArithmeticUtil.multiply(limitTradeInMobileNum, item.getAccountAmt())), NumberEnum.MONEY);
                    }
                    // 资产逾期交易.应收未收本息和 = 变量.应收金额 - 资产逾期交易.已收金额
                    item.setPrinciAndInterAmt(ArithmeticUtil.round(ArithmeticUtil1.doSub(receivableAmt, item.getAmtReceived()), NumberEnum.MONEY));
                    // 计算：每一笔逾期交易对应买入交易应该占有的金额
                    // 定义 剩余逾期本息和 = 资产逾期交易.应收未收本息和
                    BigDecimal remainAmt = item.getPrinciAndInterAmt();
                    if (CollectionUtils.isNotEmpty(buyList)) {
                        for (EadModEclResultProcess itemBuy : buyList) {
                            // 定义 买入交易应收未收本息和 = 交易对应应收未收本息和Map.get(逾期时点买入交易i.交易编号)
                            BigDecimal buyTradeAmtCt = isNullBigDecimal(unPrincipleAndInterest.get(itemBuy.getTradeId()));
                            if (buyList.indexOf(itemBuy) == (buyList.size() - 1)) {
                                buyTradeAmtCt = ArithmeticUtil.doAdd(buyTradeAmtCt, remainAmt);
                                unPrincipleAndInterest.put(itemBuy.getTradeId(), buyTradeAmtCt);
                            } else {
                                // 定义 扣减本息和 = 资产逾期交易.应收未收本息和 * 逾期时点买入交易i.逾期时点移动平均后份额 / 逾期时点买入移动数量总额（四舍五入，保留两位小数）
                                BigDecimal deductShareAmtCt = BigDecimal.ZERO;
                                if (limitTradeInMobileNum.compareTo(BigDecimal.ZERO) != 0) {
                                    deductShareAmtCt = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getPrinciAndInterAmt(), itemBuy.getLimitEmaShareAmt()), limitTradeInMobileNum, 2, RoundingMode.HALF_UP);
                                }
                                buyTradeAmtCt = ArithmeticUtil.doAdd(buyTradeAmtCt, deductShareAmtCt);
                                unPrincipleAndInterest.put(itemBuy.getTradeId(), buyTradeAmtCt);
//                                // 剩余逾期本息和 = 剩余逾期本息和 + 扣减本息和
//                                remainAmt = ArithmeticUtil.doAdd(remainAmt, deductShareAmtCt);
                                // 剩余逾期本息和 = 剩余逾期本息和 - 扣减本息和
                                remainAmt = ArithmeticUtil.doSub(remainAmt, deductShareAmtCt);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            String msg = "【EAD计算批处理】：计算错误";
            logger.error(msg);
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), msg);
            return result;
        }
        // 四、计算每笔交易的单位EAD(债券是百元、项目类资产是单位)
        // 定义 买入成本总额
        BigDecimal buyBaseAmt = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(tradeInList)) {
            for (EadModEclResultProcess itemIn : tradeInList) {
                buyBaseAmt = ArithmeticUtil.doAdd(buyBaseAmt, itemIn.getShareAmt());
            }
            for (EadModEclResultProcess itemIn : tradeInList) {
                itemIn.setSeqno(IDGenerator.generate());
                itemIn.setDecDate(excuteDate);
                //买入交易.票面利率 = 变量.现金流计算规则中最新的利率。
                itemIn.setPaperRate(readerDto.getCashFlowCalRuleList().get(0).getYield());
                //nvl(买入交易i.到期收益率，买入交易.票面利率)
                if (null == itemIn.getYield() && null == itemIn.getPaperRate()) {
                    String msg = "【EAD计算批处理】：资产无折现率";
                    logger.error(msg);
                    modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), msg);
                }
                BigDecimal tempRate;
//                if (ModelDictCodeList.模型_证券品种_债券.getValue().equals(itemIn.getType8())) {
                if (DictCodeListExt1.证券品种_债券.getValue().equals(itemIn.getModelAssetType())) {
                    tempRate = itemIn.getYield();
                } else {// 如果 证券品种为其他
                    tempRate = itemIn.getPaperRate();
                }
                PriceYieldOutParameter yieldPriceOutParameter = calaYieldByPriceCalculator(readerDto, excuteDate, tempRate, param);
                if (null == yieldPriceOutParameter) {
                    break;
                }
                // 获取买入全价
                BigDecimal fullPrice = yieldPriceOutParameter.getFullPrice();
                BigDecimal EAD = BigDecimal.ZERO;
                //4.4 计算每笔交易对应EAD
                if(DictCodeListExt1.证券品种_债券.getValue().equals(readerDto.getModelAssetType())){
                    // EAD = 移动平均后份额 * 全价 + 应收未收本息和
                    EAD = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(itemIn.getEmaShareAmt(), fullPrice), unPrincipleAndInterest.get(itemIn.getTradeId()));
                }
                else{
                    // EAD = 全价/发行规模 * 移动平均后份额 + 应收未收本息和
                    EAD = ArithmeticUtil.doAdd(ArithmeticUtil.multiply(itemIn.getEmaShareAmt(), ArithmeticUtil.divide(fullPrice, itemIn.getIssueAmt())), unPrincipleAndInterest.get(itemIn.getTradeId()));
                }
                // 保留4位小数，四舍五入
                itemIn.setEad(ArithmeticUtil.round(EAD,4, RoundingMode.HALF_UP));
                // '“03 EAD计算”，此批处理主要加载数据。
                itemIn.setProcess(ModelDictCodeList.模型_减值过程_EAD计算.getValue());
            }
        }
        // 获取资产代码
        result.setFinprodId(readerDto.getAssetId());
        result.setEadResultList(tradeInList);
        logger.info("---------EAD计算Processor业务层结束---------");
        return result;
    }

    /**
     * 4.2 调用算法根据到期收益率计算债券的全价
     *
     * @param entity
     * @param excuteDate
     * @return
     */
    private PriceYieldOutParameter calaYieldByPriceCalculator(EadCalculationReaderDto entity, Date excuteDate, BigDecimal yeild, Map<String, Object> param) {
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo) param.get("BATCH_INFO");
        SysBatchTask batchtask = (SysBatchTask) param.get("BATCHTASK");
        PriceYieldOutParameter priceYieldOutParameter = null;
        //变量.现金流计算规则  = 资产信息.现金流计算规则.get（0）
        ModCashFlowCalRuleDto modCashFlowCalRule = entity.getCashFlowCalRuleList().get(0);
        //变量.现金流支付规则  = 资产信息.现金流支付规则.get（0）
        ModCashFlowPayRulesDto modCashFlowPayRule = entity.getModCashFlowPayRulesList().get(0);
        PriceYieldInParameter yieldPriceInParameter = new PriceYieldInParameter();
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(toLocalDate(entity.getVdate()));
        secInfoModel.setEndDate(toLocalDate(entity.getMdate()));
        secInfoModel.setFirstPayDate(toLocalDate(modCashFlowPayRule.getFirstPayDate()));
        // 利率类型
        secInfoModel.setIntRateType(IntRateType.固定利率);
        if (StringUtil.isNotBlank(modCashFlowCalRule.getBasis())) {
            String basis = modCashFlowCalRule.getBasis();
            if (StringUtil.equals(basis, DictCodeList.计息基础_30_360.getValue())) {
                secInfoModel.setBasis(Basis._30_360);
            } else if (StringUtil.equals(basis, DictCodeList.计息基础_A_360.getValue())) {
                secInfoModel.setBasis(Basis.A_360);
            } else if (StringUtil.equals(basis, DictCodeList.计息基础_A_365.getValue())) {
                secInfoModel.setBasis(Basis.A_365);
            } else if (StringUtil.equals(basis, DictCodeList.计息基础_A_365F.getValue())) {
                secInfoModel.setBasis(Basis.A_365F);
            } else if (StringUtil.equals(basis, DictCodeList.计息基础_A_A.getValue())
                    || StringUtil.equals(basis, DictCodeList.计息基础_A_A_平均分配.getValue())
                    || StringUtil.equals(basis, DictCodeList.计息基础_A_A_按实际天数分配.getValue())) {
                secInfoModel.setBasis(Basis.A_A);
            }
//            else if (StringUtil.equals(basis, DictCodeList.计息基础_30U_360.getValue())) {
//                secInfoModel.setBasis(Basis._30U_360);
//            }
            if (StringUtil.equals(basis, DictCodeList.计息基础_A_A_平均分配.getValue())) {
                secInfoModel.setAllocationType(AllocationType.平均分配);
            } else {
                secInfoModel.setAllocationType(AllocationType.按实际天数分配);
            }
        } else {
            String msg = "【EAD计算批处理】：资产无计息基础";
            logger.error(msg);
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),entity.getAssetId(), msg);
            return priceYieldOutParameter;
        }
        DictCodeList bondsTypeDict = DictCodeList.from(DictCode.息票品种, entity.getCouponSpecies());
        switch (bondsTypeDict) {
            case 息票品种_零息:
                secInfoModel.setBondsType(Variety.零息);
                break;
            case 息票品种_利随本清:
                secInfoModel.setBondsType(Variety.利随本清);
                break;
            case 息票品种_贴现:
                secInfoModel.setBondsType(Variety.贴现);
                break;
            case 息票品种_附息:
                secInfoModel.setBondsType(Variety.附息);
                break;
            default:
                break;
        }

        if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(), modCashFlowPayRule.getCalcRule())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.起息日向后);
        } else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(), modCashFlowPayRule.getCalcRule())) {
            secInfoModel.setCalMethod(SecIntSchCalMethod.到期日向前);
        } else {
            secInfoModel.setCalMethod(SecIntSchCalMethod.首次付息日);
        }
        // 判断资产有无付息频率
        if (StringUtil.isEmpty(modCashFlowPayRule.getPayCycle())) {
            String msg = "【EAD计算批处理】：资产无付息频率";
            logger.error(msg);
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),entity.getAssetId(), msg);

            return priceYieldOutParameter;
        }
        if (StringUtil.equals(DictCodeList.频率_月.getValue(), modCashFlowPayRule.getPayCycle())) {
            secInfoModel.setYearIntRate(IntFrequency.月付);
        } else if (StringUtil.equals(DictCodeList.频率_季.getValue(), modCashFlowPayRule.getPayCycle())) {
            secInfoModel.setYearIntRate(IntFrequency.季付);
        } else if (StringUtil.equals(DictCodeList.频率_半年.getValue(), modCashFlowPayRule.getPayCycle())) {
            secInfoModel.setYearIntRate(IntFrequency.半年付);
        }
        // 一体化对应etliu end
        else {
            secInfoModel.setYearIntRate(IntFrequency.年付);
        }

        secInfoModel.setIssuePrice(entity.getIssuePrice());

        //付息计划
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<ModCashFlowDto> intSchList = new ArrayList<ModCashFlowDto>();
        for (ModCashFlowDto item : entity.getCashFlowList()) {
            if (StringUtil.equals(item.getCashType2(), DictCodeListExt1.现金流二级类型_利息.getValue())) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }
        //还本计划
        List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
        for (ModCashFlowDto item : entity.getCashFlowList()) {
            if (StringUtil.equals(item.getCashType2(), DictCodeListExt1.现金流二级类型_本金.getValue())) {
                SecPrinSchedule schedule = new SecPrinSchedule();
                schedule.setAmount(item.getIntAmt());
//            schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
                schedule.setPrincipal(item.getPrinAmt());
                schedule.setInterestEndDate(toLocalDate(item.getMdate()));
                secPrinScheduleList.add(schedule);
            }
        }
        //利率信息
        List<PaperIrReset> paperIrResetList = entity.getCashFlowCalRuleList().stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(entity.getVdate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        yieldPriceInParameter.setSecInfoModel(secInfoModel);
        yieldPriceInParameter.setCalDate(toLocalDate(excuteDate));

        yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
        yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
        yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
        yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
        yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
        if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), entity.getCouponSpecies())) {
            if (isNotFomartSec(intSchList, modCashFlowPayRule)) {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
            } else {
                yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
                yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
            }
        }
        yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());

        // 后续区间利息计算方案
        if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
        } else {
            yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
            yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
        }

        yieldPriceInParameter.setMatYield(yeild);
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        priceYieldOutParameter = priceByYieldCalculator.calculate(yieldPriceInParameter);
        return priceYieldOutParameter;
    }


    public static boolean isNotFomartSec(List<ModCashFlowDto> secIntScheduleList, ModCashFlowPayRulesDto modCashFlowPayRule) {
        // 根据【实际起始日】排序付息计划
        Collections.sort(secIntScheduleList, (o1, o2) -> o1.getVdate().compareTo(o2.getVdate()));

        if (StringUtil.equals(DictCodeList.频率_月.getValue(), modCashFlowPayRule.getPayCycle())) {
            for (ModCashFlowDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdate(), 1);
                if (mDate.compareTo(she.getMdate()) != 0) {
                    return false;
                }
            }
        } else if (StringUtil.equals(DictCodeList.频率_季.getValue(), modCashFlowPayRule.getPayCycle())) {
            for (ModCashFlowDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdate(), 3);
                if (mDate.compareTo(she.getMdate()) != 0) {
                    return false;
                }
            }
        } else if (StringUtil.equals(DictCodeList.频率_半年.getValue(), modCashFlowPayRule.getPayCycle())) {
            for (ModCashFlowDto she : secIntScheduleList) {
                Date mDate = DateUtil.addMonthDate(she.getVdate(), 6);
                if (mDate.compareTo(she.getMdate()) != 0) {
                    return false;
                }
            }
        } else {
            for (ModCashFlowDto she : secIntScheduleList) {
                Date mDate = DateUtil.addYear(she.getVdate(), 1);
                if (mDate.compareTo(she.getMdate()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 转换数据
     *
     * @param date 日期
     * @return LocalDate
     * @see
     * @since 1.0
     */
    private LocalDate toLocalDate(Date date) {
        if (date != null) {
            return LocalDate.parse(DateUtil.format(date));
        }
        return null;
    }

    private BigDecimal isNullBigDecimal(Object obj) {
        if (obj == null || "".equals(obj)) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) obj;
        }
    }


}
