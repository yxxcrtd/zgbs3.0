package com.joyintech.tams.model.impairment.batch.pdcalculate.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModEclResultProcess;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.model.impairment.batch.common.searchinfo.service.ImpairmentCommonService;
import com.joyintech.tams.model.impairment.batch.pdcalculate.dao.PdCalculateMapper;
import com.joyintech.tams.model.impairment.batch.pdcalculate.dto.*;
import com.joyintech.tams.valuation.common.service.CalCommonService;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:减值模型-PD计算批处理Service
 * @auther: yuzj
 * @date: 2021/7/14
 * @param:
 * @return:
 */
@Service
public class PdCalculateProcessService {
    @Autowired
    private PdCalculateMapper pdCalculateMapper;

    @Autowired
    private ImpairmentCommonService impairmentCommonService;

    private final String BATCH_PARAMS_KEY = "params";

    private final String BATCH_LOG_RESULTS = "logResults";
    @Autowired
    private ModLogsService modLogsService;

    @Autowired
    private CalCommonService calCommonService;
    /**
     * @Description:获取待处理资产信息List
     * @auther: yuzj
     * @date: 2021/7/5
     * @param:
     * @return:
     */
    public AssetImpairmentPdCalculateResultDto execute(AssetGroupPdCalculateReaderDto readerDto,Map<String, Object> mapParams,StepExecution stepExecution){

        Date plateDate = (Date)mapParams.get("PLATE_DATE");//平台日
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)mapParams.get("BATCH_INFO");
        String taskId =(String)mapParams.get("TASK_ID");
        String logMsg = "";

        AssetImpairmentPdCalculateResultDto resultDto = new AssetImpairmentPdCalculateResultDto();
        //1.1=====初始化变量Dto=====start=====
        VariablePdCalculateDto variablePdCalculateDto = new VariablePdCalculateDto();
        //交易数据列表  === 资产交易信息DTO.get交易信息列表();
        variablePdCalculateDto.setAssetImpairmentPdCalculateList(readerDto.getAssetImpairmentPdCalculateList());
        //季度列表
        variablePdCalculateDto.setQuarterList(Arrays.asList(ModelDictCodeList.季度_第一季度.getValue(),ModelDictCodeList.季度_第二季度.getValue(),ModelDictCodeList.季度_第三季度.getValue(),ModelDictCodeList.季度_第四季度.getValue()));

        try {
            if(!StringUtil.isEmptyList(variablePdCalculateDto.getAssetImpairmentPdCalculateList())){
                //评级类型（=最终使用评级类型）
                variablePdCalculateDto.setoType(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getoTypeFinal());
                //评级机构
                variablePdCalculateDto.setGradeOrgId(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getGradeOrgId());
                //评级结果
                variablePdCalculateDto.setGradeResult(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getResultCurrent());
                //主体是否持有金融牌照
                variablePdCalculateDto.setIsFinLicence(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getIsFinLicence());
                //到期日
                variablePdCalculateDto.setMdate(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getMdate());
                //资产到期日所在年月
                if(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getMdate() != null){
                    variablePdCalculateDto.setMdateYearMonStr(DateUtil.format(variablePdCalculateDto.getAssetImpairmentPdCalculateList().get(0).getMdate(),"yyyyMM"));
                }
                //当前年月
                variablePdCalculateDto.setNowYearMonStr(DateUtil.format(plateDate,"yyyyMM"));
                //12个月后所在年月  从（当前月份+1月）开始往后推第12个月的年份月份
                variablePdCalculateDto.setEndYearMonStr(DateUtil.format(DateUtil.addMonthDate(plateDate,12),"yyyyMM"));
                //资产是否有交易处于二三阶段 false
                variablePdCalculateDto.setTtStage(false);
                //遍历 交易数据
                for(ModEclResultProcess aiPdDto :variablePdCalculateDto.getAssetImpairmentPdCalculateList()){
                    if(StringUtil.isNotEmpty(aiPdDto.getFinStage()) && !ModelDictCodeList.阶段_初始买入时_P1.getValue().equals(aiPdDto.getFinStage())){
                        //资产是否有交易处于二三阶段
                        variablePdCalculateDto.setTtStage(true);
                        break;
                    }
                }
                //资产剩余期限是否不足一年 false
                variablePdCalculateDto.setMaturityYear(false);
                if(StringUtil.isNotEmpty(variablePdCalculateDto.getMdateYearMonStr()) && DateUtil.compareDate(variablePdCalculateDto.getEndYearMonStr(),variablePdCalculateDto.getMdateYearMonStr())>=0){
                    variablePdCalculateDto.setMaturityYear(true);
                }

            }

            //评级结果--最新评级结果
            if(variablePdCalculateDto.getGradeResult() ==null){
                //生成错误日志  --【批处理名称】：【机构名称/资产代码】无评级信息
                //=====记录计算错误日志=====start=====
                String gradeOrgName = SysDataUtil.getNameFromDataDict(ModelDictCode.评级机构, variablePdCalculateDto.getGradeOrgId());
                logMsg ="【PD计算批处理】：【"+gradeOrgName+"/"+readerDto.getAssetId()+"】无评级信息";
                modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);
                //=====记录计算错误日志=====end=====
                return resultDto;
            }

            //=====1.1初始化变量Dto=====end=====

            //1.2 查询前瞻因子权重配置
            ModPdWeightConfigDto modPdWeightConfigDto  = pdCalculateMapper.selectModPdWeightConfigInfo();
            if(modPdWeightConfigDto==null){
                //生成错误日志
                //=====记录计算错误日志=====start=====
                logMsg ="【PD计算批处理】：Z值权重未维护";
                modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);

                //=====记录计算错误日志=====end=====
                return resultDto;
            }

            //2、查询计算基础[前瞻前年度边际PD]、[前瞻因子值]
            //2.1 判断需要查询PD的年份
            //定义 年份列表 = 估值日(跑批日)所在年份(含) -> 资产到期日所在年份(含) 中的所有年份
            variablePdCalculateDto.setYearList(getYearMonList(plateDate,variablePdCalculateDto.getMdate(),VariablePdCalculateDto.YEAR_TYPE));
            if(!variablePdCalculateDto.getTtStage() && !variablePdCalculateDto.getMaturityYear()){
                Date endYearMonDate = DateUtil.parse(variablePdCalculateDto.getEndYearMonStr());
                variablePdCalculateDto.setYearList(getYearMonList(plateDate,endYearMonDate,VariablePdCalculateDto.YEAR_TYPE));
            }

            //2.2查询每一年的PD值 列表
            List<ModGradePdConfigDto> modGradePdConfigList = new ArrayList<>();
            // 定义 评级PD查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("gradeResult", variablePdCalculateDto.getGradeResult());
            params.put("pdConfigType", DictCodeListExt3.评级PD类型_债券_其他债权.getValue());
            params.put("configType", ModelDictCodeList.模型_减值配置类型_评级PD配置.getValue());
            // 获取当前跑批日期所处年份
            String tdyYear = DateUtil.getYearStr(DateUtil.format(plateDate));
            if(variablePdCalculateDto.getYearList() !=null){
                for(String yearStr:variablePdCalculateDto.getYearList()){
                    params.put("yearNum", new BigDecimal(yearStr));
                    ModGradePdConfigDto configDto = pdCalculateMapper.selectModGradePdConfigList(params);
                    //获取年份的评级PD配置列表
                    if(configDto !=null){
                        modGradePdConfigList.add(configDto);
                    }
                    // 若当前年份未维护PD，则属于有问题数据，给出异常结束
                    else if(yearStr.equals(tdyYear)){
                        //生成警告日志
                        //=====记录计算错误日志=====start=====
                        logMsg ="【PD计算批处理】：【"+yearStr+"】年对应评级结果【"+SysDataUtil.getNameFromDataDict(DictCode.内部评级, variablePdCalculateDto.getGradeResult())+"】的PD未维护";
                        modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);
                        //=====记录计算错误日志=====end=====
                        return resultDto;
                    }
                }
            }
            //2.3定义 [PD计算过程DTO]列表 = 获取2.2查询结果列表
            variablePdCalculateDto.setModGradePdConfigList(modGradePdConfigList);

            //2.4生成配置使用情况入库结果
            //配置使用情况DTO列表
            variablePdCalculateDto.setModGradePdConfigUsedList(variablePdCalculateDto.getModGradePdConfigList());


            //3、计算加载PDstress
            List<ModGradePdConfigDto> pdConfigList = new ArrayList<ModGradePdConfigDto>();
            // 定义 变量.评级PD配置Map：key:年份
            Map<String, ModGradePdConfigDto>pdConfigDtoMap = new HashMap<>();
            // 循环遍历评级PD配置集合
            for(ModGradePdConfigDto pdConfigDto:variablePdCalculateDto.getModGradePdConfigList()){
                //3.1 计算每一年的风险暴露相关系数R
                BigDecimal R=new BigDecimal("0");
                //deciR3 = 1-(1/(e^(50*PD)))
                BigDecimal deciR1 = ArithmeticUtil.multiply(new BigDecimal(50),pdConfigDto.getForwardYearPd());
                Double deciR2 =Math.pow(Math.E, deciR1.doubleValue());
                BigDecimal deciR3 = ArithmeticUtil.subtract(BigDecimal.ONE,ArithmeticUtil.divide(BigDecimal.ONE,new BigDecimal(Double.toString(deciR2)),12));
                //deciR5=1-(1/e^50)
                Double deciR4=Math.pow(Math.E, new BigDecimal(50).doubleValue());
                BigDecimal deciR5=ArithmeticUtil.subtract(BigDecimal.ONE,ArithmeticUtil.divide(BigDecimal.ONE,new BigDecimal(Double.toString(deciR4)),12));
                //deciR6=deciR3/deciR5
                BigDecimal deciR6=ArithmeticUtil.divide(deciR3,deciR5,12);
                BigDecimal deciR7 =ArithmeticUtil.multiply(new BigDecimal("0.12"),deciR6);
                BigDecimal deciR8 = ArithmeticUtil.multiply(new BigDecimal("0.24"),ArithmeticUtil.subtract(BigDecimal.ONE,deciR6));
//                if(variablePdCalculateDto.getIsFinLicence() ==null){
//                    //=====记录计算错误日志=====start=====
//                    logMsg ="【PD计算批处理】：无法判断【机构名称】是否金融机构";
//                    modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);
//
//                    //=====记录计算错误日志=====end=====
//                    return resultDto;
//                }

                if(DictCodeList.是否_是.getValue().equals(variablePdCalculateDto.getIsFinLicence())){
                    //使用金融机构风险暴露的相关系数为
                    R =ArithmeticUtil.multiply(new BigDecimal("1.25"),ArithmeticUtil.doAdd(deciR7,deciR8));
                }else{
                    //主权、一般公司风险暴露的相关系数为
                    R =ArithmeticUtil.doAdd(deciR7,deciR8);
                }
                pdConfigDto.setR(R);

                //3.2 遍历当年的每个季度，计算每个季度的ZtStress、PDStress
                //遍历季度列表
                for(String quarterStr :variablePdCalculateDto.getQuarterList()){
                    ModPdFactorConfigDto modPdFactorConfigDto =
                            pdCalculateMapper.selectModPdFactorConfig(pdConfigDto.getYearStr(),quarterStr);
                    String quarterStrName =getQuarterName(quarterStr);
                    if(modPdFactorConfigDto !=null){
                        //3.2.2 计算调整后Z值
                        if(ModelDictCodeList.季度_第一季度.getValue().equals(quarterStr)){//一季度
                            //乐观因子-一季度 Zstress
                            pdConfigDto.setOptimisticFactorOne(getZtStress(modPdFactorConfigDto.getOptimisticFactor(),pdConfigDto));
                            // 乐观因子一季度 PDstress
                            pdConfigDto.setOptimisticFactorPdOne(getPdStress(pdConfigDto.getOptimisticFactorOne(),pdConfigDto));
                            //基础因子-一季度 Zstress
                            pdConfigDto.setBasicFactorOne(getZtStress(modPdFactorConfigDto.getBasicFactor(),pdConfigDto));
                            //基础因子-一季度 PDstress
                            pdConfigDto.setBasicFactorPdOne(getPdStress(pdConfigDto.getBasicFactorOne(),pdConfigDto));
                            //悲观因子-一季度 Zstress
                            pdConfigDto.setPessimismFactorOne(getZtStress(modPdFactorConfigDto.getPessimismFactor(),pdConfigDto));
                            // 悲观因子-一季度 PDstress
                            pdConfigDto.setPessimismFactorPdOne(getPdStress(pdConfigDto.getPessimismFactorOne(),pdConfigDto));
                        }else if(ModelDictCodeList.季度_第二季度.getValue().equals(quarterStr)){ //二季度
                            //乐观因子-二季度
                            pdConfigDto.setOptimisticFactorTwo(getZtStress(modPdFactorConfigDto.getOptimisticFactor(),pdConfigDto));
                            pdConfigDto.setOptimisticFactorPdTwo(getPdStress(pdConfigDto.getOptimisticFactorTwo(),pdConfigDto));
                            //基础因子-二季度
                            pdConfigDto.setBasicFactorTwo(getZtStress(modPdFactorConfigDto.getBasicFactor(),pdConfigDto));
                            pdConfigDto.setBasicFactorPdTwo(getPdStress(pdConfigDto.getBasicFactorTwo(),pdConfigDto));
                            //悲观因子-二季度
                            pdConfigDto.setPessimismFactorTwo(getZtStress(modPdFactorConfigDto.getPessimismFactor(),pdConfigDto));
                            pdConfigDto.setPessimismFactorPdTwo(getPdStress(pdConfigDto.getPessimismFactorTwo(),pdConfigDto));
                        }else if(ModelDictCodeList.季度_第三季度.getValue().equals(quarterStr)){ //三季度
                            //乐观因子-三季度
                            pdConfigDto.setOptimisticFactorThree(getZtStress(modPdFactorConfigDto.getOptimisticFactor(),pdConfigDto));
                            pdConfigDto.setOptimisticFactorPdThree(getPdStress(pdConfigDto.getOptimisticFactorThree(),pdConfigDto));
                            //基础因子-三季度
                            pdConfigDto.setBasicFactorThree(getZtStress(modPdFactorConfigDto.getBasicFactor(),pdConfigDto));
                            pdConfigDto.setBasicFactorPdThree(getPdStress(pdConfigDto.getBasicFactorThree(),pdConfigDto));
                            //悲观因子-三季度
                            pdConfigDto.setPessimismFactorThree(getZtStress(modPdFactorConfigDto.getPessimismFactor(),pdConfigDto));
                            pdConfigDto.setPessimismFactorPdThree(getPdStress(pdConfigDto.getPessimismFactorThree(),pdConfigDto));
                        }else if(ModelDictCodeList.季度_第四季度.getValue().equals(quarterStr)){ //四季度
                            //乐观因子-四季度
                            pdConfigDto.setOptimisticFactorFour(getZtStress(modPdFactorConfigDto.getOptimisticFactor(),pdConfigDto));
                            pdConfigDto.setOptimisticFactorPdFour(getPdStress(pdConfigDto.getOptimisticFactorFour(),pdConfigDto));
                            //基础因子-四季度
                            pdConfigDto.setBasicFactorFour(getZtStress(modPdFactorConfigDto.getBasicFactor(),pdConfigDto));
                            pdConfigDto.setBasicFactorPdFour(getPdStress(pdConfigDto.getBasicFactorFour(),pdConfigDto));
                            //悲观因子-四季度
                            pdConfigDto.setPessimismFactorFour(getZtStress(modPdFactorConfigDto.getPessimismFactor(),pdConfigDto));
                            pdConfigDto.setPessimismFactorPdFour(getPdStress(pdConfigDto.getPessimismFactorFour(),pdConfigDto));
                        }

                        //3.2.5 加载配置使用情况(在已有数据List基础上加，后会一起存入数据库)
                        ModGradePdConfigDto factorConfigDto = new ModGradePdConfigDto();
                        factorConfigDto.setSeqNo(modPdFactorConfigDto.getSeqNo());
                        factorConfigDto.setConfigType(modPdFactorConfigDto.getConfigType());
                        pdConfigList.add(factorConfigDto);

                    }
                    else{
                        //生成警告日志
                        //=====记录计算错误日志=====start=====
                        logMsg ="【PD计算批处理】：【"+pdConfigDto.getYearStr()+"】年【"+quarterStrName+"】季度前瞻因子未维护";
                        modLogsService.warn((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);
                         //=====记录计算错误日志=====end=====
                    }
                }
                // PD配置最终结果存入Map
                pdConfigDtoMap.put(pdConfigDto.getYearStr(), pdConfigDto);
            }
            if(pdConfigList.size()>0){
                variablePdCalculateDto.getModGradePdConfigUsedList().addAll(pdConfigList);
            }

            //4、计算加载调整后月度累积PD
            //4.1 判断需要计算PD的年月列表
            //定义 年月列表 = 估值日所在年份 + 1月(含) -> 资产到期日所在年月(含) 中的所有年份 + 月份
            variablePdCalculateDto.setYearMonList(getYearMonList(plateDate,variablePdCalculateDto.getMdate(),VariablePdCalculateDto.YEARMON_TYPE));
            // 资产所处一阶段时，估值日所处月不统计，估值月下一月作为第一个月；
            if(!variablePdCalculateDto.getTtStage()){
                // 估值日距离到期日不足一年则取资产到期月份对应累积PD
                Date endYearMonDate = DateUtil.parse(variablePdCalculateDto.getEndYearMonStr());
                if(variablePdCalculateDto.getMaturityYear()){
                    endYearMonDate = variablePdCalculateDto.getMdate();
                }
                // 开始日期取下个月作为第一个月
                Date startDate = DateUtil.addDate(plateDate,0,1,0);
                // 若下个月大于到期日，即到期日在当月，去到期日所在月份
                if(DateUtil.compareDate(startDate,  variablePdCalculateDto.getMdate()) >0){
                    startDate = variablePdCalculateDto.getMdate();
                }
                variablePdCalculateDto.setYearMonList(getYearMonList(startDate,endYearMonDate,VariablePdCalculateDto.YEARMON_TYPE));
            }

            //4.2 计算每个累积PD
            // 定义 变量.有效月度边际PD
            String PD违约概率小数位 = StringUtil.isEmpty(CacheUtil.getSysParam(
                    "SYS_PARA_PD_DIGIT")) ? "6" : CacheUtil.getSysParam(
                    "SYS_PARA_PD_DIGIT");
            String PD违约概率小数尾数处理方式 = StringUtil.isEmpty(CacheUtil.getSysParam(
                    "SYS_PARA_PD_DIGIT_TYPE")) ? DictCodeList.估值舍入规则_四舍五入.getValue() : CacheUtil.getSysParam(
                    "SYS_PARA_PD_DIGIT_TYPE");
            // 遍历年月列表
            // 定义 变量.最新PD配置信息Dto
            ModGradePdConfigLatestDto latestDto = new ModGradePdConfigLatestDto();
            for(int i = 0;  i < variablePdCalculateDto.getYearMonList().size() ; i++){
                String yearMonStr = variablePdCalculateDto.getYearMonList().get(i);//年月 202107
                // 当前年份
                String yearStr = variablePdCalculateDto.getYearMonList().get(i).substring(0,4);
                // 当前月份
                String monStr = variablePdCalculateDto.getYearMonList().get(i).substring(4);

                // 遍历 [PD计算过程DTO]列表--每年对应的PD值
                // 取对应年份PD，若取不到，则pdConfigDto用前一条结果的配置
                // 当对应剩余期限年份/月份无对应前瞻PD和Z因子值，则采用最后一条有效月度边际PD继续按月向后累积至对应资产剩余期限时点
                if(pdConfigDtoMap.get(yearStr)!=null){
                    // 更新 变量.最新PD配置信息Dto
                    latestDto = initLatestDto(pdConfigDtoMap.get(yearStr), monStr);
                }
                //先计算前瞻前的
                // 计算前瞻前月度累积
                if(i==0){
                    // 前瞻前累积PD（PD1A） = PD1M
                    variablePdCalculateDto.setPreCdfPd(getPdm(latestDto.getForwardYearPd()));
                }else{
                    // 前瞻前累积PD = 调用前瞻前月度累积计算公式：得出累计PDt
                    variablePdCalculateDto.setPreCdfPd(ArithmeticUtil.doAdd(variablePdCalculateDto.getPreCdfPd(),
                            ArithmeticUtil.multiply(ArithmeticUtil.subtract(BigDecimal.ONE, variablePdCalculateDto.getPreCdfPd()),
                                    getPdm(latestDto.getForwardYearPd()))));
                }


                if(i==0){
                    // 一季度乐观因子 PDstress 计算出 PDm
                    variablePdCalculateDto.setOptimisticCdfPd(getPdm(latestDto.getOptimisticFactorPd()));
                    // 一季度基础因子 PDstress 计算出 PDm
                    variablePdCalculateDto.setBasicCdfPd(getPdm(latestDto.getBasicFactorPd()));
                    // 一季度悲观因子 PDstress 计算出 PDm
                    variablePdCalculateDto.setPessimismCdfPd(getPdm(latestDto.getPessimismFactorPd()));
                }else{
                    variablePdCalculateDto.setOptimisticCdfPd(getCdfPd(variablePdCalculateDto.getOptimisticCdfPd(),latestDto.getOptimisticFactorPd()));
                    variablePdCalculateDto.setBasicCdfPd(getCdfPd(variablePdCalculateDto.getBasicCdfPd(),latestDto.getBasicFactorPd()));
                    variablePdCalculateDto.setPessimismCdfPd(getCdfPd(variablePdCalculateDto.getPessimismCdfPd(),latestDto.getPessimismFactorPd()));
                }

                if(i == 11){
                    //第12个月累积加权PD
                    // 第12个月累积加权PD=乐观因子PDm *  乐观因子权重+基础因子PDm *  基础因子权重+悲观因子PDm * 悲观因子权重
                    variablePdCalculateDto.setTwelveMonWeightedPd(calcPdRight(variablePdCalculateDto, modPdWeightConfigDto));
                    // 前瞻前第12个月累积加权PD = 前瞻前累积PD
                    variablePdCalculateDto.setPreTwelveMonWeightedPd(variablePdCalculateDto.getPreCdfPd());
                   // 主体是否持有金融牌照 = 是
                    if(DictCodeList.是否_是.getValue().equals(variablePdCalculateDto.getIsFinLicence())){
                        // 金融债加权PD = 第12个月累积加权PD * 0.7 = 乐观情景累积PD*0.7*乐观因子权重+悲观情景累积PD*0.7*悲观因子权重+基础情景累积PD*0.7*基础因子权重
                        variablePdCalculateDto.setTwelveMonWeightedPd(ArithmeticUtil.multiply(variablePdCalculateDto.getTwelveMonWeightedPd(), BigDecimal.valueOf(0.7)));
                    }
                }
                if(i == variablePdCalculateDto.getYearMonList().size() -1 ){
                    // 到期日累积加权PD = 乐观累积PD*乐观因子权重 + 基础累积PD*基础因子权重 + 悲观累积PD*悲观因子权重
                    variablePdCalculateDto.setMdateWeightedPd(calcPdRight(variablePdCalculateDto, modPdWeightConfigDto));
                    // 前瞻前到期日累积加权PD = 前瞻前累积PD
                    variablePdCalculateDto.setPreMdateWeightedPd(variablePdCalculateDto.getPreCdfPd());
                    // 主体是否持有金融牌照 = 是
                    if(DictCodeList.是否_是.getValue().equals(variablePdCalculateDto.getIsFinLicence())){
                        // 金融债加权PD = 到期日累积加权PD * 0.7 = 乐观情景累积PD*0.7*乐观因子权重+悲观情景累积PD*0.7*悲观因子权重+基础情景累积PD*0.7*基础因子权重
                        variablePdCalculateDto.setMdateWeightedPd(ArithmeticUtil.multiply(variablePdCalculateDto.getMdateWeightedPd(), BigDecimal.valueOf(0.7)));
                    }
                }
            }

            //5、加载每一笔交易数据的加权PD
            for(ModEclResultProcess aipcDto:variablePdCalculateDto.getAssetImpairmentPdCalculateList()){
                // if 资产剩余期限是否不足一年 == false && 交易数据i.减值阶段 == 一阶段
                if(!variablePdCalculateDto.getTtStage()&& !variablePdCalculateDto.getMaturityYear()){
                    // 交易数据i.前瞻前PD违约概率% = 前瞻前第12个月累积加权PD
                    aipcDto.setPreProspectivePd(variablePdCalculateDto.getPreTwelveMonWeightedPd());
                    //交易数据i.前瞻后PD违约概率% = 第12个月累积加权PD
                    aipcDto.setProspectivePd(calCommonService.round(variablePdCalculateDto.getTwelveMonWeightedPd(), PD违约概率小数尾数处理方式, PD违约概率小数位));
                }
                else{
                    // 交易数据i.前瞻前PD违约概率% = 到期日累积加权PD
                    aipcDto.setPreProspectivePd(variablePdCalculateDto.getPreMdateWeightedPd());
                    //交易数据i.前瞻后PD违约概率% = 到期日累积加权PD
                    aipcDto.setProspectivePd(calCommonService.round(variablePdCalculateDto.getMdateWeightedPd(), PD违约概率小数尾数处理方式, PD违约概率小数位));
                }

            }

            //6、加载减值配置使用情况表
            List<ModConfigUseageResultDto> modConfigUseageResultList= new ArrayList<>();
            for(ModGradePdConfigDto pdConfigDto:variablePdCalculateDto.getModGradePdConfigUsedList()){
                int countConfigUseAge =pdCalculateMapper.selectCountConfigUseAge(pdConfigDto.getSeqNo(),pdConfigDto.getConfigType());
                //若6.1查询结果大于0，则说明该配置已存在表中，不用加载减值配置使用情况表入库数，不入库。
                //若6.1查询结果==0，则加载减值配置使用情况表入库数据，加载逻辑参考输出结果-减值配置使用情况表，
                if(countConfigUseAge>0){
                    //不入库
                }else{
                    ModConfigUseageResultDto  configUseageResult = new ModConfigUseageResultDto();
                    configUseageResult.setSeqNo(IDGenerator.generate());
                    configUseageResult.setConfigSeqNo(pdConfigDto.getSeqNo());
                    configUseageResult.setConfigType(pdConfigDto.getConfigType());
                    configUseageResult.setUseProcess(ModelDictCodeList.模型_减值过程_PD计算.getValue());//04 PD计算

                    modConfigUseageResultList.add(configUseageResult);
                }
            }
            //待入库 减值配置使用情况表
            resultDto.setModConfigUseageResultList(modConfigUseageResultList);


            //待入库 减值结果过程表
            List<ModEclResultProcess> eclResultProcessResultList = new ArrayList<>();
            for(ModEclResultProcess assetImpairmentPdCalculateDto: variablePdCalculateDto.getAssetImpairmentPdCalculateList()){
                ModEclResultProcess modEclResultProcessResult = new ModEclResultProcess();
                BeanUtils.copyProperties(assetImpairmentPdCalculateDto, modEclResultProcessResult);
                modEclResultProcessResult.setSeqno(IDGenerator.generate());//流水号
                modEclResultProcessResult.setDecDate(plateDate);//减值日期
                modEclResultProcessResult.setLgd(null);//LGD违约损失率%
                modEclResultProcessResult.setPreProspectiveEcl(null);//前瞻前ECL预期信用损失
                modEclResultProcessResult.setProspectiveEcl(null);//前瞻后ECL预期信用损失
                modEclResultProcessResult.setProcess(ModelDictCodeList.模型_减值过程_PD计算.getValue());//过程  04

                eclResultProcessResultList.add(modEclResultProcessResult);

            }
            resultDto.setModEclResultProcessResultList(eclResultProcessResultList);
        } catch (Exception e) {
            //=====记录计算错误日志=====start=====
            logMsg ="【PD计算批处理】：计算异常";
            modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),readerDto.getAssetId(), logMsg);

            e.printStackTrace();
            //=====记录计算错误日志=====end=====
            return resultDto;
        }

        return resultDto;
    }

    /**
     * @Description:根据开始日期和结束日期，获取日期间年份/年月集合，包含开始日期和结束日期当年年份
     * @auther: yuzj
     * @date: 2021/7/16
     * @param:type 1-年份{2019,2020,2021} ，  2-年月{201901,201902...,202107}
     * @return:
     */
    public List<String> getYearMonList(Date startDate,Date endDate,String type){
        List<String> yearList = new ArrayList<>();
        if(endDate ==null){
            return yearList;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        if("1".equals(type)){
            sdf = new SimpleDateFormat("yyyy");
        }else if("2".equals(type)){
            sdf = new SimpleDateFormat("yyyyMM");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        // 初始日期设为本月1号
        cal.set(Calendar.DAY_OF_MONTH, 1);
        // 若为年份时，将日期设为本年1月1号
        if("1".equals(type)){
            cal.set(Calendar.MONTH, 0);
        }
        for (long d = cal.getTimeInMillis(); d <= endDate.getTime(); d = getYearMonAddOne(cal,type)) {
            yearList.add(sdf.format(d));
        }
        return yearList;

    }
    /**
     * @Description:给日期加1年
     * @auther: yuzj
     * @date: 2021/7/17
     * @param:
     * @return:
     */
    public  long getYearMonAddOne(Calendar c,String type) {
        if("1".equals(type)){
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        }else if("2".equals(type)){
            c.set(Calendar.MONTH, c.get(Calendar.MONTH) + 1);
        }
        return c.getTimeInMillis();
    }

    /**
     * @Description:3.2.3 ZtStress算法公式
     * ZtStress算法公式
     * @auther: yuzj
     * @date: 2021/7/17
     * @param:factor 前瞻因子
     * @return:
     */
    public BigDecimal  getZtStress(BigDecimal factor,ModGradePdConfigDto pdConfigDto){
        BigDecimal ztStress = BigDecimal.ZERO;
        NormalDistribution normalDistribution = new NormalDistribution();
        //deciZt1 =根号（1-R）
        double deciZt1 = Math.sqrt(ArithmeticUtil.subtract(BigDecimal.ONE,pdConfigDto.getR()).doubleValue());
        //deciZt2= N^-1(PD)
//        BigDecimal deciZt2 =ArithmeticUtil.divide(BigDecimal.ONE,normsdist(pdConfigDto.getForwardYearPd().doubleValue()),12);
        BigDecimal deciZt2 = new BigDecimal(Double.toString(normalDistribution.inverseCumulativeProbability(pdConfigDto.getForwardYearPd().doubleValue())));
        //deciZt3=(1-deciZt1)*deciZt2
        BigDecimal deciZt3 =ArithmeticUtil.multiply( ArithmeticUtil.subtract(BigDecimal.ONE,new BigDecimal(Double.toString(deciZt1))),deciZt2);
        ztStress= ArithmeticUtil.doAdd(factor,ArithmeticUtil.divide(deciZt3, BigDecimal.valueOf(Math.sqrt(pdConfigDto.getR().doubleValue())),12));

        return ztStress;
    }

    /**
     * @Description:  3.2.4 PDStress算法公式
     * PdStress算法公式
     * @auther: yuzj
     * @date: 2021/7/17
     * @param:factor 前瞻因子
     * @return:
     */
    public BigDecimal  getPdStress(BigDecimal ztStress,ModGradePdConfigDto pdConfigDto){
        BigDecimal pdStress = BigDecimal.ZERO;
        NormalDistribution normalDistribution = new NormalDistribution();
        //拆分算法，方便检查与理解
        //deciPd1 = 𝜑^-1(PD)
//        BigDecimal deciPd1 =ArithmeticUtil.divide(BigDecimal.ONE,normsdist(pdConfigDto.getForwardYearPd().doubleValue()),12);
        BigDecimal deciPd1 =new BigDecimal(Double.toString(normalDistribution.inverseCumulativeProbability(pdConfigDto.getForwardYearPd().doubleValue())));
        //deciPd2 =根号R* ztStress
        BigDecimal deciPd2 =ArithmeticUtil.multiply(BigDecimal.valueOf(Math.sqrt(pdConfigDto.getR().doubleValue())),ztStress);
        //deciPd3=根号(1-R)
        BigDecimal deciPd3 =new BigDecimal(Double.toString(Math.sqrt(ArithmeticUtil.subtract(BigDecimal.ONE,pdConfigDto.getR()).doubleValue())));
        //deciPd4 =(deciPd1-deciPd2)/deciPd3
        BigDecimal deciPd4 =ArithmeticUtil.divide(ArithmeticUtil.subtract(deciPd1,deciPd2),deciPd3,12);
//        pdStress=normsdist(deciPd4.doubleValue());
        pdStress=new BigDecimal(Double.toString(normalDistribution.cumulativeProbability(deciPd4.doubleValue())));
        return pdStress;
    }


    
    /**
     * @Description:PD计算
     * @auther: yuzj
     * @date: 2021/7/17
     * @param: 
     * @return: 
     */
    public BigDecimal getPdm(BigDecimal pdFactor){
        BigDecimal pdm = BigDecimal.ZERO;
        pdm = ArithmeticUtil.subtract(BigDecimal.ONE, BigDecimal.valueOf(Math.pow(ArithmeticUtil.subtract(BigDecimal.ONE,pdFactor).doubleValue(), 1.0/12)));
        return pdm;
    }

    /**
     * @Description:根据条件获取累计PD值
     * @auther: yuzj
     * @date: 2021/7/17
     * @param: 
     * @return: 
     */
    public BigDecimal getCdfPd(BigDecimal cdfPd,BigDecimal factorPd){
        return ArithmeticUtil.doAdd(cdfPd,ArithmeticUtil.multiply(ArithmeticUtil.subtract(BigDecimal.ONE,cdfPd),getPdm(factorPd)));
    }

    /**
     * @auther: 根据季度编码获取季度名称
     * @date: 2021/7/27 22:54
     * @param:
     * @return: 季度名称
     */
    
    public String getQuarterName(String quarterStr){
        String quarterName="";
        if(ModelDictCodeList.季度_第一季度.getValue().equals(quarterStr)){//一季度
            quarterName=ModelDictCodeList.季度_第一季度.getCodeName();
        }else if(ModelDictCodeList.季度_第二季度.getValue().equals(quarterStr)){ //二季度
            quarterName=ModelDictCodeList.季度_第二季度.getCodeName();
        }else if(ModelDictCodeList.季度_第三季度.getValue().equals(quarterStr)){ //三季度
            quarterName=ModelDictCodeList.季度_第三季度.getCodeName();
        }else if(ModelDictCodeList.季度_第四季度.getValue().equals(quarterStr)){ //四季度
            quarterName=ModelDictCodeList.季度_第四季度.getCodeName();
        }
        return quarterName;
    }

    /**
     * 计算前瞻因子加权PD
     * @param variablePdCalculateDto
     * @return
     */
    public BigDecimal calcPdRight(VariablePdCalculateDto variablePdCalculateDto, ModPdWeightConfigDto modPdWeightConfigDto){
        // 乐观因子累计PDm *  乐观因子权重/100
        BigDecimal deciTwelve1  =ArithmeticUtil.divide(ArithmeticUtil.multiply(variablePdCalculateDto.getOptimisticCdfPd(),modPdWeightConfigDto.getOptimisticWeight()), new BigDecimal(100));
        // 基础因子累计PDm *  基础因子权重/100
        BigDecimal deciTwelve2  =ArithmeticUtil.divide(ArithmeticUtil.multiply(variablePdCalculateDto.getBasicCdfPd(),modPdWeightConfigDto.getBasicWeight()), new BigDecimal(100));
        // 悲观因子累计PDm * 悲观因子权重/100
        BigDecimal deciTwelve3  =ArithmeticUtil.divide(ArithmeticUtil.multiply(variablePdCalculateDto.getPessimismCdfPd(),modPdWeightConfigDto.getPessimismWeight()), new BigDecimal(100));
        // 第12个月累积加权PD=乐观因子PDm *  乐观因子权重+基础因子PDm *  基础因子权重+悲观因子PDm * 悲观因子权重
       return ArithmeticUtil.doAdd(deciTwelve1,deciTwelve2,deciTwelve3);
    }

    /**
     * 初始化最新评级PD配置信息
     * @param modGradePdConfigDto 评级PD配置信息
     * @param monStr 月份
     * @return
     */
    public ModGradePdConfigLatestDto initLatestDto(ModGradePdConfigDto modGradePdConfigDto, String monStr){
        // 变量.最新PD配置信息Dto 重新初始化
        ModGradePdConfigLatestDto latestDto = new ModGradePdConfigLatestDto();
        //这里开始计算前瞻后PD
        int monInt = Integer.valueOf(monStr).intValue();
        // 前瞻前年度边际PD = 变量.PD配置信息.前瞻前年度边际PD
        latestDto.setForwardYearPd(modGradePdConfigDto.getForwardYearPd());
        if(monInt>=1 && monInt<=3){
            // 乐观因子季度年化边际PD = 乐观因子季度年化边际PD-一季度
            latestDto.setOptimisticFactorPd(modGradePdConfigDto.getOptimisticFactorPdOne());
            // 基础因子季度年化边际PD = 基础因子季度年化边际PD-一季度
            latestDto.setBasicFactorPd(modGradePdConfigDto.getBasicFactorPdOne());
            // 悲观因子季度年化边际PD = 悲观因子季度年化边际PD-一季度
            latestDto.setPessimismFactorPd(modGradePdConfigDto.getPessimismFactorPdOne());
        }else if(monInt>=4 && monInt<=6){
            // 乐观因子季度年化边际PD = 乐观因子季度年化边际PD-二季度
            latestDto.setOptimisticFactorPd(modGradePdConfigDto.getOptimisticFactorPdTwo());
            // 基础因子季度年化边际PD = 基础因子季度年化边际PD-二季度
            latestDto.setBasicFactorPd(modGradePdConfigDto.getBasicFactorPdTwo());
            // 悲观因子季度年化边际PD = 悲观因子季度年化边际PD-二季度
            latestDto.setPessimismFactorPd(modGradePdConfigDto.getPessimismFactorPdTwo());

        }else if(monInt>=7 && monInt<=9){
            // 乐观因子季度年化边际PD = 乐观因子季度年化边际PD-三季度
            latestDto.setOptimisticFactorPd(modGradePdConfigDto.getOptimisticFactorPdThree());
            // 基础因子季度年化边际PD = 基础因子季度年化边际PD-三季度
            latestDto.setBasicFactorPd(modGradePdConfigDto.getBasicFactorPdThree());
            // 悲观因子季度年化边际PD = 悲观因子季度年化边际PD-三季度
            latestDto.setPessimismFactorPd(modGradePdConfigDto.getPessimismFactorPdThree());

        }else if(monInt>=10 && monInt<=12){
            // 乐观因子季度年化边际PD = 乐观因子季度年化边际PD-四季度
            latestDto.setOptimisticFactorPd(modGradePdConfigDto.getOptimisticFactorPdFour());
            // 基础因子季度年化边际PD = 基础因子季度年化边际PD-四季度
            latestDto.setBasicFactorPd(modGradePdConfigDto.getBasicFactorPdFour());
            // 悲观因子季度年化边际PD = 悲观因子季度年化边际PD-四季度
            latestDto.setPessimismFactorPd(modGradePdConfigDto.getPessimismFactorPdFour());
        }
        return latestDto;
    }
}
