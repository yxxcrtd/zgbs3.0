package com.joyintech.tams.model.valuation.batch.incomemethodmodel.service;//package com.joyintech.tams.model.valuation.batch.incomemethodmodel.service;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModCurveResult;
import com.joyintech.tams.common.base.entity.ModRiskPremium;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.valuation.batch.incomemethodmodel.dao.IncomeMethodMapper;
import com.joyintech.tams.model.valuation.batch.incomemethodmodel.dto.*;
import com.joyintech.tams.valuation.common.dto.modelDto.*;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.batch.service.ModValuationCommonService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:收益法估值批处理Service
 * @auther: yuzj
 * @date: 2021/7/6
 * @param:
 * @return:
 */
@Service
public class ProductAssetsIncomeMethodProcessorService {
    @Autowired
    private IncomeMethodMapper incomeMethodMapper;
    //引入共通service
    @Autowired
    private ModValuationCommonService modValuationCommonService;
    @Autowired
    private ModLogsService modLogsService;

    private Logger logger = LoggerFactory.getLogger(ProductAssetsIncomeMethodProcessorService.class);
    /**
     * 执行收益法计算过程，最终产物为资产定价数据和估值行情数据
     * 核心计算公式：FV = D0 (1 + g)/(r - g)
     * FV - 资产价格；
     * D0 - 年化股息，指当期股息；取票面利率计算的利息；
     * g  - 预期股息增长率；通过手工维护；
     * r  - 折现率：折现率=估值日国债最大期限即期收益率+(（D0*（1+g）/PV+g）-基准日国债最大期限即期收益率），其中PV为发行价格；
     * @param item
     * @param mapParam
     * @param stepExecution
     * @return
     */
    public ProductAssetsIncomeMethodResultDto convertResultFromReader(ProductAssetsIncomeMethodReaderDto item, Map<String, Object> mapParam,StepExecution stepExecution){
        ProductAssetsIncomeMethodResultDto resultDto = new ProductAssetsIncomeMethodResultDto();//从reader 返回结果result
        List<ModBatchLogs> bBatchLogResultList = new ArrayList<>();

        Date plateDate = (Date)mapParam.get(ValComponentConstant.EXECUTE_DATE);//平台日
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)mapParam.get(ValComponentConstant.BATCH_EXECUTEINFO);
        String taskId =batchExecuteInfo.getTaskId();

        ModAssetInfoDto modAssetInfoDto = item.getModAssetInfoDto();
        String finprodId=modAssetInfoDto.getFinprodId();

        //计算变量-股息预期股息增长率
        BigDecimal g = item.getModStockdivitrateConfig().getStockIntrate();

        //计算变量-发行时票面利率
        BigDecimal issuePaperRate = BigDecimal.ZERO;
        if(item!=null && item.getFinCashCalcRuleDtoList()!=null && item.getFinCashCalcRuleDtoList().size()>0){
            issuePaperRate = item.getFinCashCalcRuleDtoList().get(0).getYield();
        }else{
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：债券不存在票面利率.");
            return resultDto;
        }

        //设置全价为发行价
        BigDecimal targetPrice = modAssetInfoDto.getIssuePrice();
        //设置基准日为起息日
        Date baseDate = modAssetInfoDto.getVdate();
        //债券面值
        BigDecimal faceValue = new BigDecimal(100);

        BigDecimal D0 = new BigDecimal(100);
        //1.计算年股息
        if(CollectionUtils.isNotEmpty(item.getFinCashPlanDtoList())){
            BigDecimal paidPrice = BigDecimal.ZERO;
            //计算已还本面值
            for (ModCashFlowDto modCashFlowDto : item.getFinCashPlanDtoList()) {
                if (modCashFlowDto.getCashType2().equals(DictCodeListExt1.现金流二级类型_本金.getValue())
                        && modCashFlowDto.getMdate().getTime() <= plateDate.getTime()) {
                    paidPrice = ArithmeticUtil.doAdd(paidPrice,modCashFlowDto.getPrinAmt());
                }
            }
            faceValue = ArithmeticUtil.subtract(faceValue,paidPrice);//贴现债不考虑还本
        }
        //年化股息D0
        D0 = ArithmeticUtil.multiply(faceValue,issuePaperRate);


        //2计算折现率
        //2.1取估值日中债国债收益率曲线的最大年限点的利率
        BigDecimal valuationGovernmentDebtYield = queryYieldByDate(plateDate, batchExecuteInfo, taskId, finprodId);
        if(valuationGovernmentDebtYield==null){
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：债券不存在估值日国债曲线收益率行情.");
            return resultDto;
        }else{
            modLogsService.info((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：债券估值日国债曲线收益率："+valuationGovernmentDebtYield);
        }

        //2.2取基准日中债国债收益率曲线的最大年限点的利率
        BigDecimal baseDateGovernmentDebtYield = queryYieldByDate(baseDate, batchExecuteInfo, taskId, finprodId);

        if(baseDateGovernmentDebtYield==null){
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：债券不存在基准日国债曲线收益率行情.");
            return resultDto;
        }else{
            modLogsService.info((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：债券基准日国债曲线收益率："+baseDateGovernmentDebtYield);

        }

        //2.3折算率
        //算法==> 变量.折现率 = 变量.估值日国债收益率  + （（变量.当前面值*（1+ 变量.股息预期股息增长率）/ 变量.买入全价 + 变量.股息预期股息增长率）- 变量.基准日国债收益率 ）
        //拆分算法，方便检查阅读
        //估值日国债最大期限即期收益率+(（D0*（1+g）/PV+g）-基准日国债最大期限即期收益率）

        //deci1=（1+ 变量.股息预期股息增长率）
        BigDecimal deciRate1= ArithmeticUtil.doAdd(BigDecimal.ONE,g);
        //deci2=（变量.年股息*deci1/ 变量.贴现目标价 + 变量.股息预期股息增长率）
        BigDecimal deciRate2 = ArithmeticUtil.doAdd(ArithmeticUtil.divide(ArithmeticUtil.multiply(D0,deciRate1),targetPrice,12),g);
        //deci3=变量.估值日国债收益率  +（deci2- 变量.基准日国债收益率 ）
        BigDecimal r= ArithmeticUtil.doAdd(valuationGovernmentDebtYield,ArithmeticUtil.subtract(deciRate2,baseDateGovernmentDebtYield));
//        varDto.setYearDiscountRate(deciRate3);
        modLogsService.info((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),finprodId, "【收益法模型批处理】：折现率："+r);

        /**
         * 3.估值全价、净价计算
         * 变量.估值全价 = 变量.年股息*（1 + 变量.股息预期股息增长率）/ (变量.折现率 - 变量.股息预期股息增长率)
         */
        // deciPrice1 = 变量.年股息*（1 + 变量.股息预期股息增长率）
        BigDecimal deciPrice1 = ArithmeticUtil.multiply(D0, ArithmeticUtil.doAdd(BigDecimal.ONE, g)
        );
        // deciPrice1 / (变量.折现率 - 变量.股息预期股息增长率)
        //变量.估值净价
        BigDecimal netPrice = ArithmeticUtil.divide(deciPrice1, ArithmeticUtil.subtract(r, g), 14);

        // 变量.单位应计利息 = 返回值.单位应计利息
        BigDecimal interest = getCalculate(item, plateDate).getInterest();

        // 变量.估值全价 = 变量.估值净价 + 变量.单位应计利息
        BigDecimal fullprice = ArithmeticUtil.doAdd(netPrice, interest);

        //封装结果
        //1封装债券估值行情DTO
        ModCurveResult vResultDto = new ModCurveResult();
        vResultDto.setSeqno(IDGenerator.generate());
        vResultDto.setFinprodId(modAssetInfoDto.getFinprodId());
        vResultDto.setCdate(plateDate);
        vResultDto.setValModel(ModelDictCodeList.模型_估值模型名称_收益率法.getValue());//数据字典.估值来源.模型
        vResultDto.setFullPrice(fullprice);
        vResultDto.setNetPrice(netPrice);

        //2封装资产定价DTO
//        ModRiskPremium mResultDto = new ModRiskPremium();
//        mResultDto.setSeqno(IDGenerator.generate());
//        mResultDto.setSecTypeModel(DictCodeListExt1.证券品种_债券.getValue());
//        mResultDto.setSecDetailTypeModel(modAssetInfoDto.getSecTypeDetail());
//        mResultDto.setFinprodId(modAssetInfoDto.getFinprodId());
//        mResultDto.setFinprodName(modAssetInfoDto.getFinprodAbbr());
//        mResultDto.setBasedate(baseDate);
//        mResultDto.setbFullprice(targetPrice);
//        mResultDto.setModMethod(ModelDictCodeList.模型_估值模型名称_收益率法.getValue());

        MstBondValuationInfo mstBondValuationInfo = new MstBondValuationInfo();
        mstBondValuationInfo.setSecId(modAssetInfoDto.getFinprodMarketId()); //市场代码
        mstBondValuationInfo.setValueDate(plateDate);//估值日
        mstBondValuationInfo.setValueSource(DictCodeListExt1.估值来源_模型法_MOD.getValue());//估值来源
        mstBondValuationInfo.setPrice(fullprice.setScale(2, RoundingMode.HALF_UP)); //估值全价
        mstBondValuationInfo.setNetprice(netPrice.setScale(2, RoundingMode.HALF_UP));//估值净价
        mstBondValuationInfo.setInputType(DictCodeListExt3.录入方式_模型生成.getValue()); //录入方式 待定 测试 interf
        mstBondValuationInfo.setCreateUser("batch"); //
        mstBondValuationInfo.setUpdateUser("batch");
        mstBondValuationInfo.setCreateTime(DateUtil.now());
        mstBondValuationInfo.setUpdateTime(DateUtil.now());

        resultDto.setMstBondValuationInfo(mstBondValuationInfo);
        resultDto.setModCurveResult(vResultDto);
//        resultDto.setModRiskPremium(mResultDto);


        return resultDto;
    }

    /**
     * 查询指定日期国债曲线最大期限收益率
     * @param plateDate
     * @param batchExecuteInfo
     * @param taskId
     * @param finprodId
     * @return
     */
    private BigDecimal queryYieldByDate(Date plateDate, SysBatchExecInfo batchExecuteInfo, String taskId, String finprodId) {
        BigDecimal valuationGovernmentDebtYield = null;
        ModYeildCurveParamsDto modYeildCurveParamsVDto = new ModYeildCurveParamsDto();
        List<String> yeildIdVList = new ArrayList<>();
        yeildIdVList.add(ModelDictCodeList.曲线代码_中债国债收益率曲线.getValue());//数据字典.曲线代码.1231
        modYeildCurveParamsVDto.setYeildIdList(yeildIdVList);
        modYeildCurveParamsVDto.setSearchDate(plateDate);
        List<ModYieldCurveInfoDto> modYieldCurveInfoVList =  new ArrayList<>();
        Map<String,List<ModYieldCurveInfoDto>> mapVList = modValuationCommonService.getCurveInfo(modYeildCurveParamsVDto);
        if(mapVList!=null&&mapVList.get(ModelDictCodeList.曲线代码_中债国债收益率曲线.getValue())!=null&&mapVList.get(ModelDictCodeList.曲线代码_中债国债收益率曲线.getValue()).size()>0){//数据字典.曲线代码.1231
            modYieldCurveInfoVList =(List<ModYieldCurveInfoDto>)mapVList.get(ModelDictCodeList.曲线代码_中债国债收益率曲线.getValue());
            valuationGovernmentDebtYield = modYieldCurveInfoVList.get(modYieldCurveInfoVList.size()-1).getYield();
        }else{
            //取不到行情就报错，在外层报错
            logger.error("曲线代码："+ModelDictCodeList.曲线代码_中债国债收益率曲线.getValue()+",行情日期："+plateDate+"，无行情.");
        }
        return valuationGovernmentDebtYield;
    }

    private AccurIntCalOutParameter getCalculate(ProductAssetsIncomeMethodReaderDto entity, Date platDate) {
        // 组装参数
        AccurIntCalInParameter parameter = getCalInParam(entity, platDate);
        // 计算百元面值的债券的利息
        AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
    }

    /**
     * 算法包.计算百元面值的债券的利息-记账用传参封装
     * @param entity
     * @param platDate
     * @return
     */
    private AccurIntCalInParameter getCalInParam(ProductAssetsIncomeMethodReaderDto entity, Date platDate) {
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        //计算日
//        Date date = DateUtil.parse("2021-09-25");
        //platDate
        parameter.setCalDate(BasicUtil.dateToLocal(platDate));

        // 计算日
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        // 债券起息日
        secInfoModel.setStartDate(BasicUtil.dateToLocal(entity.getModAssetInfoDto().getVdate()));
        // 债券到期日
        secInfoModel.setEndDate(BasicUtil.dateToLocal(entity.getModAssetInfoDto().getMdate()));
        // 债券首次付息日
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(entity.getModCashFlowPayRulesList().get(0).getFirstPayDate()));
        // 发行价
        secInfoModel.setIssuePrice(entity.getModAssetInfoDto().getIssuePrice());
        // 债券类型（息票品种）
        Variety bondsType = null;
        if (DictCodeList.息票品种_贴现.getValue().equals(entity.getModAssetInfoDto().getCouponSpecies())) {
            bondsType = Variety.贴现;
        }
        else if (DictCodeList.息票品种_零息.getValue().equals(entity.getModAssetInfoDto().getCouponSpecies())) {
            bondsType = Variety.零息;
        }
        else if (DictCodeList.息票品种_附息.getValue().equals(entity.getModAssetInfoDto().getCouponSpecies())) {
            bondsType = Variety.附息;
        }
        else if (DictCodeList.息票品种_利随本清.getValue().equals(entity.getModAssetInfoDto().getCouponSpecies())) {
            bondsType = Variety.利随本清;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = entity.getFinCashCalcRuleDtoList().get(0).getBasis();
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        }
        else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        }
        else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        }
//        else if (DictCodeList.计息基础_30U_360.getValue().equals(basisString)) {
//            basis = Basis._30U_360;
//        }

        secInfoModel.setBasis(basis);
        // 利率类型
        IntRateType intRateType = IntRateType.固定利率;
        secInfoModel.setIntRateType(intRateType);
        // 利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        String secIntSchCalMethodString = entity.getModCashFlowPayRulesList().get(0).getCalcRule();
        if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        String yearIntRateString = entity.getModCashFlowPayRulesList().get(0).getPayCycle();
        if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.半年付;
        }
        else {
            yearIntRate = IntFrequency.年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划列表
        List<SecIntSchedule> invScheduleList = new ArrayList<>();
        List<ModCashFlowDto> intSchList = new ArrayList<ModCashFlowDto>();
        for (ModCashFlowDto item : entity.getFinCashPlanDtoList()) {
            if (StringUtil.equals(item.getCashType2(), DictCodeListExt1.现金流二级类型_利息.getValue())
                    && item.getVdateUnadjust() != null) {
                SecIntSchedule schedule = new SecIntSchedule();
                schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
                schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
                schedule.setActualStartDate(toLocalDate(item.getVdate()));
                schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
                schedule.setAmount(item.getIntAmt());
                schedule.setCorpus(new BigDecimal(100));
                invScheduleList.add(schedule);
                intSchList.add(item);
            }
        }
        parameter.setSecIntScheduleList(invScheduleList);

        // 债券利率重置信息
        List<PaperIrReset> paperIrResetList = entity.getFinCashCalcRuleDtoList().stream().map(item -> {
            PaperIrReset ret = new PaperIrReset();
            // BeanUtil.copyPropertiesIgnoreNull(ret, item);
            ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            ret.setParRate(item.getYield());
            ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
            return ret;
        }).collect(Collectors.toList());
        if (paperIrResetList != null && paperIrResetList.size() > 0) {
            paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(entity.getModAssetInfoDto().getVdate()));
        }
        if (paperIrResetList != null && paperIrResetList.size() > 1) {
            // 多重利率则计算按浮动利率算
            secInfoModel.setIntRateType(IntRateType.浮动利率);
        }
        parameter.setPaperIrResetList(paperIrResetList);

        // 当前面值
        BigDecimal currentValue = getCurrentValue(entity, platDate);
        parameter.setPaperAmt(currentValue);
        return parameter;
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
    /**
     * 获取当前面值
     * @param entity
     * @param platDate
     * @return
     */
    private BigDecimal getCurrentValue(ProductAssetsIncomeMethodReaderDto entity, Date platDate) {
        BigDecimal currentValue = BigDecimal.valueOf(100);
        //已还本面值
        BigDecimal printValue = BigDecimal.ZERO;
        for (ModCashFlowDto dto : entity.getFinCashPlanDtoList()){
            //If 明细.现金流类型 = 数据字典.现金流类型.中间还本 or 到期还本 and 明细.调整后的计算结束日 <= 跑批日
            if (StringUtil.equals(dto.getCashType2(), DictCodeListExt1.现金流二级类型_本金.getValue())
                    && dto.getMdate().compareTo(platDate) <= 0) {
                //变量.已还本面值 = 变量.已还本面值 + 明细.计划支付本金
                printValue = ArithmeticUtil.doAdd(printValue, dto.getPrinAmt());
            }
        }
        //变量.当前面值 = 变量.当前面值 - 变量.已还本面值
        currentValue = ArithmeticUtil.subtract(currentValue, printValue);
        return currentValue;
    }

}
