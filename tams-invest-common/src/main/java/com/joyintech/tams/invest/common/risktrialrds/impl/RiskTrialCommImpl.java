package com.joyintech.tams.invest.common.risktrialrds.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.RskIsupLogMapper;
import com.joyintech.tams.common.base.entity.RskIndividAssetDel;
import com.joyintech.tams.common.base.entity.RskIndividSpecialStage;
import com.joyintech.tams.common.base.entity.RskPortfolioPackageDtl;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.indic.entity.IIndicatorResult;
import com.joyintech.tams.invest.common.risktrialrds.RiskTrialComm;
import com.joyintech.tams.invest.common.risktrialrds.dao.RiskTrialCommMapper;
import com.joyintech.tams.invest.common.risktrialrds.dict.LogLvl;
import com.joyintech.tams.invest.common.risktrialrds.dict.SupervisionStat;
import com.joyintech.tams.invest.common.risktrialrds.dto.*;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClass;
import com.joyintech.tams.invest.common.risktrialrds.reflect.PluginClassDao;
import com.joyintech.tams.invest.common.risktrialrds.service.IndividuationInfoSvc;
import com.joyintech.tams.invest.common.risktrialrds.service.RiskTrialCommCallable;
import com.joyintech.tams.invest.common.risktrialrds.service.SupervisionCalcSvc;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 风险试算入口 〈功能详细描述〉
 *
 * @author 裴家俊
 * @version 1.0 2018年11月28日
 * @see RiskTrialCommImpl
 * @since 1.0
 */
@Component("riskTrialCommImplRds")
public class RiskTrialCommImpl  implements RiskTrialComm {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SupervisionCalcSvc supervisionCalcSvc;

    @Autowired
    private IndividuationInfoSvc<RskIndividSpecialStage> individSpecialStageSvc;

    @Autowired
    private IndividuationInfoSvc<RskIndividAssetDel> individAssetDelSvc;

    @Autowired
    private RiskTrialCommMapper riskTrialCommMapper;

    @Autowired
    private RiskTrialCommCallable riskTrialCommCallable;

    @Autowired
    private RskIsupLogMapper rskIsupLogMapper;

    /**
     *  风险日间或日终批处理逻辑
     * @param mapOther  条件包括指标信息及组合
     * @param task   任务信息
     * @param SupFrequency   监督频率(日间或日终)
     * @return
     */
    @Override
    public RiskTrialRetDto afterRiskTrialDeal(Map<String, Object> mapOther, Task task, String SupFrequency) {
        RiskTrialRetDto retTrialEntity = new RiskTrialRetDto();

        if (task != null) {
            // 构造
            SystemParam sysParam = new SystemParam();
            sysParam.setValDate(Const.DateFmt.format(task.getDate()));

            // supervision point
            String objectCode = StringUtil.EMPTY;
            SupervisionTargetDto point = task.getPoint();
            if (point.getPortfolioLevel().equals(BussinessDictCodeListRds.组合层次_组合包.getValue())) {
                List<String> portList = point.getPortfList().stream().map(RskPortfolioPackageDtl::getPortfolioId).collect(Collectors.toList());
                objectCode = Joiner.on(",").join(portList);
            } else {
                objectCode = point.getPortfolioId();
            }
            try {
                sysParam.setPortfolioCode(RiskUtil.convertPortfCode(objectCode));// 单个组合成分/组合包成分
                // 调用基础算法运算符
                ExprTreeMath exprTree = new ExprTreeMath(point.getExpr());
                Set<String> paramNames = exprTree.getParamList();
                Map<String, Object> params = new HashMap<String, Object>();
                boolean logFlag = true;
                Map<String, Object> logMap = new HashMap<String, Object>();
                for (String paramName : paramNames) {
                    List<SupervisionParameterDto> supervisionParameterDtoList = RiskUtil.getMapOtherObj(mapOther, Const.SUP_PARAM, SupervisionParameterDto.class);
                    List<SupervisionParameterDto> filterSupervisionParamList = supervisionParameterDtoList.stream().filter(item -> {
                        if (SupFrequency.equals(BussinessDictCodeListRds.监督频率_日间.getValue())) {
                            return item.getParaCode().equals(paramName) && (item.getParaGranularity().equals(BussinessDictCodeListRds.参数粒度_事后_日间.getValue()) || item.getParaGranularity().equals(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue()));
                        } else {
                            return item.getParaCode().equals(paramName) && (item.getParaGranularity().equals(BussinessDictCodeListRds.参数粒度_事后_日终.getValue()) || item.getParaGranularity().equals(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue()));
                        }
                    }).collect(Collectors.toList());
                    //wangyang1  风险二级法人改造 start
                    List<SupervisionParameterDto> filterFixParamList = filterSupervisionParamList.stream().filter(item -> {
                        if (item.getParaGranularity().equals(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue())) {
                             return StringUtil.equals(task.getInstitutionCode(), item.getInstitutionCode());
                        }
                        return true;
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(filterFixParamList)) {
                         filterFixParamList = filterSupervisionParamList.stream().filter(item -> {
                            if (item.getParaGranularity().equals(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue())) {
                                return StringUtil.equals(BussinessDictCodeListRds.指标参数实现类型_固定值.getValue(), item.getInstitutionCode());
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }
                    if (!CollectionUtils.isEmpty(filterFixParamList)) {
                        filterSupervisionParamList = filterFixParamList;
                    }
                   //wangyang1  风险二级法人改造 end
                    if (StringUtil.isEmptyList(filterSupervisionParamList)) {
                        logFlag = false;
                        logMap.put("eMsg", " 参数" + paramName + "在指标参数集合不存在\n");
                        break;
                    }
                    SupervisionParameterDto indicatorParam = filterSupervisionParamList.get(0);
					/*SupervisionParameterDto indicatorParam = supervisionCalcSvc.getIndicatorParam(paramName,
							SupFrequency.equals(BussinessDictCodeListRds.监督频率_日间.getValue())? BussinessDictCodeListRds.参数粒度_事后_日间.getValue():BussinessDictCodeListRds.参数粒度_事后_日终.getValue());*/
                    if (BussinessDictCodeListRds.指标参数实现类型_SQL.getValue().equals(indicatorParam.getParaDisplayType())) {
                        String sql = indicatorParam.getContent();
                        Object paramValue = supervisionCalcSvc.getIndicatorParamValue(sql, sysParam, point.getIndType(), point.getFoulCond(), "03");// 获取每个参数实际值
                        logFlag = paramLogDeal(paramValue, logMap, paramName);
                        if(!logFlag){
                            break;
                        }
                        params.put(paramName, paramValue);
                    }
                    if (BussinessDictCodeListRds.指标参数实现类型_类名.getValue().equals(indicatorParam.getParaDisplayType())) {
                        if (SupFrequency.equals(BussinessDictCodeListRds.监督频率_日间.getValue())) {
                            sysParam.setSupFrequency(BussinessDictCodeListRds.监督频率_日间.getValue());
                        }else{
                            sysParam.setSupFrequency(BussinessDictCodeListRds.监督频率_日终.getValue());
                        }
                        SystemParam sysDuration=new SystemParam();
                        BeanUtil.copyPropertiesIgnoreNull(sysDuration,sysParam);
                        //赋组合代码
                        sysDuration.setPortfolioCode(sysDuration.getPortfolioCode().replace("(",StringUtil.EMPTY).replace(")",StringUtil.EMPTY).replace("'",StringUtil.EMPTY));
                        String className = indicatorParam.getContent();
                        PluginClass pluginClass =  RiskUtil.getBean(indicatorParam.getContent().trim());
                        pluginClass.setSysParameter(null, sysDuration);
                        Object paramValue = pluginClass.getParameterValue();// 获取每个参数实际值
                        logFlag = paramLogDeal(paramValue, logMap, paramName);
                        if(!logFlag){
                            break;
                        }
                        params.put(paramName, paramValue);
                    }
                    if (BussinessDictCodeListRds.指标参数实现类型_固定值.getValue().equals(indicatorParam.getParaDisplayType())) {
                        params.put(paramName, indicatorParam.getContent());
                    }
                    if (BussinessDictCodeListRds.指标参数实现类型_自定义.getValue().equals(indicatorParam.getParaDisplayType())) {
                        JsonNode node = JsonUtils.toJsonNode(indicatorParam.getContent());
                        if (!StringUtil.isNotEmpty(node)) {
                            logFlag = false;
                            logMap.put("eMsg", " 自定义参数" + paramName + "内容不存在\n");
                            break;
                        }
                        JsonNode sql = node.findValue("sql");
                        String content = sql == null ? "" : sql.asText();
                        Object paramValue = supervisionCalcSvc.getIndicatorParamValue(content, sysParam, point.getIndType(), point.getFoulCond(), "03");// 获取每个参数实际值
                        logFlag = paramLogDeal(paramValue, logMap, paramName);
                        params.put(paramName, paramValue);
                    }
                }
                if (!logFlag) {
                    //日志文件
                    insertLogTrial(SupFrequency, point, LogLvl.WARNING, StringUtil.isNotEmpty(logMap.get("eMsg")) ? logMap.get("eMsg").toString() : StringUtil.EMPTY, sysParam);
                    return null;
                }
                //Object indicatorValue = exprTree.calc(params);// 实际指标值
                boolean mxFlag = exprTree.checkListByParams(params);
                List<Map<String, Object>> proParamList = exprTree.productListByParams(params);
                List<Double> mainIndValue = new ArrayList<Double>();
                List<String> mainIndProcess = new ArrayList<String>();

                //处理个性化特殊阈值
                individSpecialStageSvc.dealIndSpecialStage(mapOther, Const.IND_SPE_STAGE, point, RskIndividSpecialStage.class);

                //处理个性化特殊资产阈值
                List<RskIndividSpecialStage> rskIndSpecialStageAstList = individSpecialStageSvc.dealIndividuationInfo(mapOther, Const.IND_SPE_STAGE_AST, point, RskIndividSpecialStage.class);

                //处理个性化特殊资产阈值
                List<RskIndividAssetDel> rskIndividAssetDelList = individAssetDelSvc.dealIndividuationInfo(mapOther, Const.IND_AST_DEL, point, RskIndividAssetDel.class);

                //调用阈值解析
                PointDetail pointDetail = new PointDetail(point.getFoulCond(), point.getApproveCond(), point.getWarnCond(), point.getIndType());

                //处理明细资产
                List<RiskTrialRetDetailDto> riskTrialDetList = new ArrayList<>();
                int logCount = 0;
                List<SupervisionStat> statList = new ArrayList<SupervisionStat>();
				boolean stageAstVal=false;
                for (Map<String, Object> param : proParamList) {
                    if (!mxFlag) {
                        //判断是否存在特殊资产阈值
                        point.setFinprodId(null != param.get(Const.parAstCode) ? param.get(Const.parAstCode).toString() : null);
                        //判断是否存在剔除资产
                        if (individAssetDelSvc.dealIndAstDel(rskIndividAssetDelList, point)) {//存在剔除集合中
                            continue;//执行下一个资产
                        }
						SupervisionTargetDto pointStageAst=new SupervisionTargetDto();
                        BeanUtil.copyPropertiesIgnoreNull(pointStageAst,point);
                        if (individSpecialStageSvc.dealIndSpecialStageAst(rskIndSpecialStageAstList, pointStageAst)) {//存在新的阈值
                            stageAstVal=true;
                            pointDetail = new PointDetail(pointStageAst.getFoulCond(), pointStageAst.getApproveCond(), pointStageAst.getWarnCond(), pointStageAst.getIndType());
                        }else{
                                //标识之前是有资产阈值的
                                if(stageAstVal) {
                                    pointDetail = new PointDetail(point.getFoulCond(), point.getApproveCond(), point.getWarnCond(), point.getIndType());
                                    stageAstVal=false;
                                }
                        }
                        //判断是否存在特殊资产阈值
                    }

                    String exprMath = exprTree.replaceParamByMath(point.getExpr(), param, point.getIndType());
                    Object indicatorValue = null;
                    try {
                        indicatorValue = exprTree.calc(exprMath);// 实际指标值
                    } catch (Exception e) {
                        if (logCount == 0) {//插入一次即可
                            insertLogTrial(SupFrequency, point, LogLvl.ERROR, "组合(包)代码:" + objectCode + "指标名称:" + point.getIndName() + "监督实际值非法或为空，可能出现分母为0的情况", sysParam);
                        }
                        logCount++;
                        //task = taskQueue.pop();
                        //----update by jjpei 2019-12-01------------------
                        //continue;//中止当前进程
                        if (!mxFlag) {
                            //调用插入明细方法
                            riskTrialDetList.add(insertRiskTrialDetail(param, point, sysParam, 0, SupervisionStat.ERROR, SupFrequency, null));
                            statList.add(SupervisionStat.ERROR);
                        } else {
                            retTrialEntity = insertRiskTrial(point, sysParam, 0, SupervisionStat.ERROR, SupFrequency, retTrialEntity);
                        }
                        continue;
                        //----update by jjpei 2019-12-01------------------
                    }
                    if (indicatorValue instanceof Double) {
                        Double checkDou = (Double) indicatorValue;
                        if (Double.isInfinite(checkDou) || Double.isNaN(checkDou)) {
                            insertLogTrial(SupFrequency, point, LogLvl.WARNING, "组合(包)代码:" + objectCode + "指标名称:" + point.getIndName() + "监督实际值非法或为空，可能出现分母为0的情况", sysParam);
                            //task = taskQueue.pop();
                            continue;//中止当前进程
                        }
                    }

                    //保留计算过程
                    String mathProcess = StringUtil.EMPTY;
                    if (param.keySet().size() > 0 && StringUtil.isNotEmpty(exprMath)) {
                        Map<String, Object> mapParam = new LinkedHashMap<>();
                        mapParam.put(Const.PARAM, param);
                        mapParam.put(Const.EXPR, exprMath);
                        mathProcess = JsonUtils.object2Json(mapParam);
                    }

                    //判断出监督状态(正常，违规，审批，预警)
                    SupervisionStat supStat = pointDetail.check(indicatorValue);
                    // add by zhangrui1 指标结果绝对值显示 start
                    indicatorValue = changeToAbs(indicatorValue, pointDetail.isAbs(), point.getIndType());
                    // add by zhangrui1 指标结果绝对值显示 end

                    if (!mxFlag) {
                        //调用插入明细方法
                        riskTrialDetList.add(insertRiskTrialDetail(param, point, sysParam, indicatorValue, supStat, SupFrequency, mathProcess));
                        mainIndValue.add(Double.parseDouble(indicatorValue.toString()));
                        mainIndProcess.add(mathProcess + "||" + Double.parseDouble(indicatorValue.toString()));
                        statList.add(supStat);
                    } else {
                        if (StringUtil.isNotEmpty(mathProcess)) {
                            retTrialEntity.setMathProcess(mathProcess);
                        }
                        retTrialEntity = insertRiskTrial(point, sysParam, indicatorValue, supStat, SupFrequency, retTrialEntity);
                        //insertLogTrial(point,LogLvl.INFO,"自动监督成功",sysParam);
                    }


                }
                //赋值明细到结果集中
                if (riskTrialDetList.size() > 0) {
                    retTrialEntity.setRiskTrialRetDetailDtoList(riskTrialDetList);
                }
                if (!mxFlag && mainIndValue.size() > 0) {
                    //根据阈值判断出最大 或 最小值
                    Object indicatorValue = supervisionCalcSvc.getMaxMinValueByListRisk(point.getIndType(), point.getFoulCond(), mainIndValue);
                    List<String> mathProList = mainIndProcess.stream().filter(item -> item.contains("||" + indicatorValue)).collect(Collectors.toList());
                    //找出最大值对应的表达式
                    if (!StringUtil.isEmptyList(mathProList)) {
                        retTrialEntity.setMathProcess(mathProList.get(0).substring(0, mathProList.get(0).indexOf("||")));
                    }
                    //判断出监督状态(正常，违规，预警)
                    SupervisionStat supStat = !StringUtil.isEmptyList(statList)?SupervisionStat.valueOf(RiskUtil.getMaxState(statList.stream().map(item->item.toString()).collect(Collectors.toList()))):pointDetail.check(indicatorValue);
                    retTrialEntity = insertRiskTrial(point, sysParam, indicatorValue, supStat, SupFrequency, retTrialEntity);
                    //insertLogTrial(point,LogLvl.INFO,"自动监督成功",sysParam);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                insertLogTrial(SupFrequency, point, LogLvl.ERROR, "自动监督失败：" + e.getMessage(), sysParam);
                return null;
            }
            //task = taskQueue.pop();
        }
        return retTrialEntity;
    }

    /*
     *插入监督值操作
     */
    private RiskTrialRetDto insertRiskTrial(SupervisionTargetDto point, SystemParam sysParam, Object indicatorValue,
                                            SupervisionStat supStat, String SupFrequency, RiskTrialRetDto riskTrialRetDto) {
        // 构造结果实体
        riskTrialRetDto.setPortfolioId(point.getPortfolioId());
        riskTrialRetDto.setIndId(point.getIndId());
        riskTrialRetDto.setSupDate(DateUtil.parse(sysParam.getValDate()));
        riskTrialRetDto.setSupFrequency(SupFrequency);
        if (null != indicatorValue)
            riskTrialRetDto.setSupVal(indicatorValue.toString());
        riskTrialRetDto.setSupStat(supStat.name());
        riskTrialRetDto.setWarnCond(point.getWarnCond());
        riskTrialRetDto.setApproveCond(point.getApproveCond());
        riskTrialRetDto.setFoulCond(point.getFoulCond());
        return riskTrialRetDto;
        //supervisionCalcSvc.deletePointSupervisionValue(riskTrialRetDto);
        //supervisionCalcSvc.addPointSupervisionValue(riskTrialRetDto);
    }

    /*
     *插入监督值明细操作
     */
    private RiskTrialRetDetailDto insertRiskTrialDetail(Map<String, Object> param, SupervisionTargetDto point, SystemParam sysParam, Object indicatorValue, SupervisionStat supStat, String SupFrequency, String mathProcess) {
        //构造结果实体
        RiskTrialRetDetailDto riskTrialRetDto = new RiskTrialRetDetailDto();
        riskTrialRetDto.setPortfolioId(point.getPortfolioId());
        riskTrialRetDto.setIndId(point.getIndId());
        riskTrialRetDto.setSupFrequency(SupFrequency);
        if (null != param.get(Const.parAstCode))
            riskTrialRetDto.setAssetCode(param.get(Const.parAstCode).toString());
        if (null != param.get(Const.parAstName))
            riskTrialRetDto.setAssetName(param.get(Const.parAstName).toString());
        riskTrialRetDto.setSupDate(DateUtil.parse(sysParam.getValDate()));
        if (null != indicatorValue)
            riskTrialRetDto.setSupVal(indicatorValue.toString());
        riskTrialRetDto.setSupStat(supStat.name());
        if (StringUtil.isNotEmpty(mathProcess)) {
            riskTrialRetDto.setMathProcess(mathProcess);
        }
        return riskTrialRetDto;
        //supervisionCalcSvc.deleteMxPointSupervisionValue(riskTrialRetDto);
        //supervisionCalcSvc.addMxPointSupervisionValue(riskTrialRetDto);
    }

    /*
     *写日志操作
     */
    private void insertLogTrial(String SupFrequency, SupervisionTargetDto point, LogLvl logLvl, String message, SystemParam sysParam) {
        //日志文件
        IsupLogDto logdto = new IsupLogDto();
        logdto.setLogId(IDGenerator.generate());
        logdto.setFunctionId(SupFrequency.equals(BussinessDictCodeListRds.监督频率_日间.getValue()) ? BussinessDictCodeListRds.批机能类型_日间指标计算.getValue() : BussinessDictCodeListRds.批机能类型_日终指标计算.getValue());
        logdto.setPortfolioId(point.getPortfolioId());
        logdto.setMessage(message);
        logdto.setLogLvl(logLvl.toString());
        logdto.setIndId(point.getIndId());
        logdto.setLogTime(DateUtil.parse(sysParam.getValDate()));
        rskIsupLogMapper.insertSelective(logdto);
    }

    /**
     * 处理异常参数日志
     *
     * @param paramValue
     * @param logMap
     * @param paramName
     * @return
     */
    private boolean paramLogDeal(Object paramValue, Map<String, Object> logMap, String paramName) {
        boolean logFlag = true;
        if (paramValue == null) {
            logFlag = false;
            if (logMap.get("eMsg") == null) {
                logMap.put("eMsg", " 参数" + paramName + "值为null\n");
            } else {
                logMap.put("eMsg", logMap.get("eMsg").toString() + " 参数" + paramName + "值为null\n");
            }
        }
        return logFlag;
    }

    @Override
    public String selectWorkflowActionType(String seqNo) {
        return riskTrialCommMapper.selectWorkflowActionType(seqNo);
    }

    // add by zhangrui1 指标结果绝对值显示 start
    /**
     * 修改指标结果为绝对值显示
     * 
     * @param res 指标结果
     * @param isAbs 是否绝对值
     * @param indType 指标类型
     */
    private final Object changeToAbs(Object res, boolean isAbs, String indType) {
        // 结果不存在、非绝对值、枚举型指标排除
        if (!isAbs || null == res || StringUtil.contains(indType, RiskUtil.FORBID)) {
            return res;
        }
        return Math.abs(Double.parseDouble(res.toString()));
    }
    // add by zhangrui1 指标结果绝对值显示 end
}
