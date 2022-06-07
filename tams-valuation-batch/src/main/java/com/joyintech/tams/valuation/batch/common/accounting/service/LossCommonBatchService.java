/*
 * 文件名：LossCommonBatchService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：caoguo
 * 修改时间：2020年10月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.common.accounting.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.common.batch.finproduct.service.AccountingCommonBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.common.base.entity.SysBatchTask;
import com.joyintech.tams.valuation.batch.common.accounting.dao.LossCommonBatchMapper;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author caoguo
 * @version 1.0 2020年10月30日
 * @see LossCommonBatchService
 * @since 1.0
 */
@Service
public class LossCommonBatchService {

    private static String LOSS_BATCH_ID = "assetImpairmentProvisionTask";
    
    /*
     * 减值计算位数
     */
    public static int LOSS_CAL_SCALE = 12;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 减值共通后台Mapper
     */
    @Autowired
    private LossCommonBatchMapper lossCommonBatchMapper;
    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonBatchService accountingCommonBatchService;

    /**
     * 冲减减值计算(存续期) 计算存续期时冲减的减值金额
     * 
     * @param inputDto 输入参数
     * @return 减值金额
     * @see
     * @since 1.0
     */
    public BigDecimal calcLossAmtForDur(LossAmtInputDto inputDto) {
        // 减值日理论余额
        BigDecimal lossAccrDateBalAmt = inputDto.getLossAccrDateBalAmt();
        // 减值日EAD金额
        BigDecimal lossAccrDateEADAmt = inputDto.getLossAccrDateEADAmt();
        // 减值金额
        BigDecimal lossAmt = BigDecimal.ZERO;

        if (inputDto.getEADAmt() == null || inputDto.getEADAmt().compareTo(BigDecimal.ZERO) == 0) {
            return lossAmt;
        }
        if (inputDto.getLossBalAmt() == null
            || inputDto.getLossBalAmt().compareTo(BigDecimal.ZERO) == 0) {
            return lossAmt;
        }
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_收息冲抵减值准备处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_收息是否冲抵减值准备.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            inputDto.getBooksetId(), paramCodeList);
        // 变量.计算方式
        String calcType = "";
        if (getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_收息冲抵减值准备处理方式.getValue()) != null) {
            calcType = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_收息冲抵减值准备处理方式.getValue());
        }
        String isCalcLx = "";
        if (getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_收息是否冲抵减值准备.getValue()) != null) {
            isCalcLx = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_收息是否冲抵减值准备.getValue());
        }
        // if 变量.是否处理利息 = 否 and 本息标识 = 利息
        if (DictCodeList.是否_否.getValue().equals(isCalcLx)
            && DictCodeListExt1.现金流一级类型_利息.getValue().equals(inputDto.getFlag())) {
            return lossAmt;
        }
        // if 变量.冲抵方式 = 02：原金额占比
        if (DictCodeListExt3.收息冲抵减值准备处理方式_原金额占比.getValue().equals(calcType)) {
            // if 入参.减值日理论余额 = null or 入参.减值日EAD金额 = null
            if (lossAccrDateEADAmt == null || lossAccrDateBalAmt == null) {
                // 调用共通最新统计分析明细表（资产减值）查询
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("booksetId", inputDto.getBooksetId());
                param.put("happenDateEnd", inputDto.getHappenDate());
                param.put("bookDate", inputDto.getBookDate());
                param.put("bokDetailId", inputDto.getBokDetailId());
                param.put("lossGenType", DictCodeListExt3.减值生成方式_减值计提批处理.getValue());
                BokStatLossPosiiton loss = lossCommonBatchMapper.queryLossDetailInfo(param);
                // if 入参.减值日EAD金额 = null and 上述返回统计分析明细表（资产减值） !=null
                if (lossAccrDateEADAmt == null && loss != null) {
                    lossAccrDateEADAmt = ArithmeticUtil.doAdd(loss.getTdyCostAmt(),
                        loss.getTdyIntAmt());
                }
                SubjectBalance subjectBalance = new SubjectBalance();
                // if 入参.减值日理论余额 = null and 上述返回统计分析明细表（资产减值） !=null and 本息标识 = 利息
                if (lossAccrDateBalAmt == null && loss != null
                    && DictCodeListExt1.现金流一级类型_利息.getValue().equals(inputDto.getFlag())) {
                    subjectBalance = getBalance(inputDto, loss,
                        inputDto.getBranch(),
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue()));
                    // if 上述返回值不为空
                    if (subjectBalance != null) {
                        lossAccrDateBalAmt = subjectBalance.getBalance();
                    }
                    else {
                        // 科目性质分组查询
                        List<String> intSubjectNatureList = accountingCommonService.getValSubjectNatureList(
                            DictCodeListNatureGroup.分组代码_减值计提利息类对应科目查询.getValue());
                        // 入参.减值日理论余额 = 上述返回值.科目余额
                        subjectBalance = getBalance(inputDto,loss, null,
                            intSubjectNatureList);
                        if (subjectBalance != null) {
                            lossAccrDateBalAmt = subjectBalance.getBalance();
                        }
                    }

                }

            }
            // if 入参.减值日EAD金额 = null
            if (lossAccrDateEADAmt == null) {
                // 入参.减值日EAD金额 = 入参.当前EAD金额
                lossAccrDateEADAmt = inputDto.getEADAmt();
            }
            // if 入参.减值日理论余额 = null
            if (lossAccrDateBalAmt == null) {
                // 入参.减值日理论余额 = 入参.应收款余额
                lossAccrDateBalAmt = inputDto.getReceAmt();
            }
            // if 入参.本息标识 = 本金
            if (DictCodeListExt1.现金流一级类型_本金.getValue().equals(inputDto.getFlag())) {
                // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生金额 / 入参.减值日EAD金额
                lossAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(inputDto.getLossBalAmt(), ArithmeticUtil.doAdd(inputDto.getHappenAmt(),inputDto.getVarYzjSubjectAmt())
                    		),
                    lossAccrDateEADAmt, NumberEnum.MONEY.getCode());
            }
            else if (DictCodeListExt1.现金流一级类型_利息.getValue().equals(inputDto.getFlag())) {
                // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生金额 * 入参.减值日理论余额 / 入参.应收款余额 / 入参.减值日EAD金额
                lossAmt = ArithmeticUtil.multiply(inputDto.getLossBalAmt(),
                    inputDto.getHappenAmt(), lossAccrDateBalAmt);
                lossAmt = ArithmeticUtil.divide(lossAmt,
                    ArithmeticUtil.multiply(inputDto.getReceAmt(), lossAccrDateEADAmt),
                    NumberEnum.MONEY.getCode());
            }

        }
        else if (DictCodeListExt3.收息冲抵减值准备处理方式_金额占比.getValue().equals(calcType))  {
            // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生金额 / 入参.当前EAD金额
            lossAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(inputDto.getLossBalAmt(), ArithmeticUtil.doAdd(inputDto.getHappenAmt(),inputDto.getVarYzjSubjectAmt())),
                inputDto.getEADAmt(), NumberEnum.MONEY.getCode());
        } else {
            // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生金额 / 入参.当前面值
            lossAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(inputDto.getLossBalAmt(), inputDto.getHappenAmt()),
                inputDto.getTotalMz(), NumberEnum.MONEY.getCode());
        }

        return lossAmt;
    }

    /**
     * 冲减减值计算(卖出) 计算交易时冲减的减值金额
     * 
     * @param inputDto 输入参数
     * @return 减值金额
     * @see
     * @since 1.0
     */
    public BigDecimal calcLossAmtForDeal(LossAmtInputDto inputDto) {
        // 减值日EAD金额
        BigDecimal lossAccrDateEADAmt = inputDto.getLossAccrDateEADAmt();
        // 减值金额
        BigDecimal lossAmt = BigDecimal.ZERO;

        if (inputDto.getEADAmt() == null || inputDto.getEADAmt().compareTo(BigDecimal.ZERO) == 0) {
            return lossAmt;
        }
        if (inputDto.getLossBalAmt() == null
            || inputDto.getLossBalAmt().compareTo(BigDecimal.ZERO) == 0) {
            return lossAmt;
        }
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_卖出冲抵减值准备处理方式.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            inputDto.getBooksetId(), paramCodeList);
        // 变量.计算方式
        String calcType = "";
        if (getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_卖出冲抵减值准备处理方式.getValue()) != null) {
            calcType = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_卖出冲抵减值准备处理方式.getValue());
        }
        // if 变量.冲抵方式 = 02：金额占比
        if (DictCodeListExt3.卖出冲抵减值准备处理方式_金额占比.getValue().equals(calcType)) {
            // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生金额 / 入参.当前EAD金额
            lossAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(inputDto.getLossBalAmt(), inputDto.getHappenAmt()),
                inputDto.getEADAmt(), NumberEnum.MONEY.getCode());
        }
        else if (DictCodeListExt3.卖出冲抵减值准备处理方式_原金额占比.getValue().equals(calcType)) {
            // 减值日冲减本息和
            BigDecimal lossAccrDateSumAmt = BigDecimal.ZERO;
            // 减值日冲减本息和
            BigDecimal lossAccrDateAmt = BigDecimal.ZERO;
            
            // 调用共通最新统计分析明细表（资产减值）查询
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("booksetId", inputDto.getBooksetId());
            param.put("happenDateEnd", inputDto.getHappenDate());
            param.put("bookDate", inputDto.getBookDate());
            param.put("bokDetailId", inputDto.getBokDetailId());
            param.put("lossGenType", DictCodeListExt3.减值生成方式_减值计提批处理.getValue());
            BokStatLossPosiiton loss = lossCommonBatchMapper.queryLossDetailInfo(param);
            // if 入参.减值日EAD金额 = null and 上述返回统计分析明细表（资产减值） !=null
            if (lossAccrDateEADAmt == null && loss != null) {
                lossAccrDateEADAmt = ArithmeticUtil.doAdd(loss.getTdyCostAmt(),
                    loss.getTdyIntAmt());
            }
            // if 入参.减值日理论余额 = null and 上述返回统计分析明细表（资产减值） !=null and 本息标识 = 利息
            if (loss != null) {
                List<String> cbSubjectNatureList = accountingCommonService.getValSubjectNatureList(
                    DictCodeListNatureGroup.分组代码_减值计提成本类对应科查询.getValue());
                List<String> lxtzSubjectNatureList = accountingCommonService.getValSubjectNatureList(
                    DictCodeListNatureGroup.分组代码_减值计提利息调整类对应科目查询.getValue());
                // 调用账务类共通【科目余额查询-期待方向】 成本
                SubjectBalance cbBalance = getBalance(inputDto, loss,
                    inputDto.getBranch(), cbSubjectNatureList);
                // 调用账务类共通【科目余额查询-期待方向】 利息调整
                SubjectBalance lxtzBalance = getBalance(inputDto, loss,
                    inputDto.getBranch(), lxtzSubjectNatureList);
                // 调用账务类共通【凭证最新账务查询】 查询是否发生收息场景
                String tableId = "";
                List<String> baseSubjectNatureList = new ArrayList<String>();
                if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(inputDto.getInvAim())
                    && DictCodeListExt1.金融产品类型_债券.getValue().equals(inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.摊余成本类债券应计利息.getValue());
                }
                else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                    inputDto.getInvAim())
                         && DictCodeListExt1.金融产品类型_债券.getValue().equals(inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_综合收益类债券_收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类应计利息.getValue());
                }
                else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(inputDto.getInvAim())
                         && DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(
                             inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_收益型资产_收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.应计利息.getValue());
                }
                else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                    inputDto.getInvAim())
                         && DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(
                             inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_综合收益类收益型资产_收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类应计利息.getValue());
                }
                else if (DictCodeListExt1.金融产品类型_存款.getValue().equals(inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_存放同业_收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.应计利息.getValue());
                }
                else if (DictCodeListExt1.金融产品类型_同业借款.getValue().equals(inputDto.getBusiType())) {
                    tableId = DictCodeListTableId.会计场景代码_拆放同业_中间收息.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.应计利息.getValue());
                }
                // 调用账务共通设计->【凭证最新账务查询】
                List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                    inputDto.getBooksetId(), inputDto.getBookDate(), inputDto.getBokDetailId(),
                    null, tableId, null, null, null, loss.getHappenDate(),
                    inputDto.getHappenDate());
                BigDecimal cbAmt = BigDecimal.ZERO;
                BigDecimal lxtzAmt = BigDecimal.ZERO;
                BigDecimal lxAmt = BigDecimal.ZERO;
                if (StringUtil.isEmptyList(listBookLatestInfoDto)) {
                    SubjectBalance lxBalance = getBalance(inputDto,  loss,
                        inputDto.getBranch(), baseSubjectNatureList);
                    if (lxBalance != null) {
                        lxAmt = lxBalance.getBalance();
                    }
                }
                if (cbBalance != null) {
                    cbAmt = cbBalance.getBalance();
                }
                if (lxtzBalance != null) {
                    lxtzAmt = lxtzBalance.getBalance();
                }
                // 变量.减值日本息和 = 变量.减值日当日成本 + 变量.减值日当日利息调整 + 变量.减值日当日利息
                lossAccrDateSumAmt = ArithmeticUtil.doAdd(cbAmt,lxtzAmt,lxAmt);
                // 变量.冲减本息和 = 变量.减值日本息和 * 入参.发生份额 / 入参.总份额
                lossAccrDateAmt = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(lossAccrDateSumAmt, inputDto.getHappenNum()),
                    inputDto.getTotalNum(), NumberEnum.MONEY.getCode());
            }
            // 变量.减值准备金额 = 入参.减值准备余额 * 变量.冲减本息和 / 入参.减值日EAD金额
            lossAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(inputDto.getLossBalAmt(), lossAccrDateAmt),
                lossAccrDateEADAmt, NumberEnum.MONEY.getCode());
            

        }
        else if(DictCodeListExt3.卖出冲抵减值准备处理方式_数量占比.getValue().equals(calcType)){
            // 变量.减值准备金额 = 入参.减值准备余额 * 入参.发生份额 / 入参.总份额
            lossAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(inputDto.getLossBalAmt(), inputDto.getHappenNum()),
                inputDto.getTotalNum(), NumberEnum.MONEY.getCode());
        }
        return lossAmt;
    }

    /**
     * 查询科目余额
     * 
     * @param inputDto
     * @param loss
     * @see
     * @since 1.0
     */
    private SubjectBalance getBalance(LossAmtInputDto inputDto,
                                      BokStatLossPosiiton loss, Long branch,
                                      List<String> baseSubjectNatureList) {
        // 查询其他应收款-利息
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        // 上述返回统计分析明细表（资产减值）.当日账面价值.发生日
        searchDao.setHappenDate(loss.getHappenDate());
        // 入参.入账日
        searchDao.setBookDate(inputDto.getBookDate());
        // 入参.账务明细代码
        searchDao.setBokDetailId(inputDto.getBokDetailId());
        // 入参.账套代码
        searchDao.setBooksetId(inputDto.getBooksetId());
        // 入参.分支序号
        searchDao.setBranch(branch);
        // 基础科目性质列表
        searchDao.setBaseSubjectNatureList(baseSubjectNatureList);

        SysBatchTask taskInfo = lossCommonBatchMapper.getTaskInfo(LOSS_BATCH_ID);
        // 减值计提对应的批处理任务优先级
        searchDao.setTaskPri(taskInfo.getTaskPri());
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao,
            DictCodeList.余额方向_借方.getValue());
        if (!StringUtil.isEmptyList(listSubjectBalance)) {
            return listSubjectBalance.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * 获取三级段减值流水
     * @param happenDate 发生日
     * @param secId 资产代码
     * @return
     */
    public BigDecimal getStage3Vaule(Date happenDate,String secId){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("HAPPEN_DATE", happenDate);
        param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
        param.put("secId", secId);
        param.put("stageP3", DictCodeListExt3.阶段_买入后出现减值_P3.getValue());
        // 若减值方案存在，则采用模型计算结果
        List<BigDecimal> vauleList = new ArrayList<>();
        vauleList = lossCommonBatchMapper.getStage3Vaule(param);

        if(StringUtil.isEmptyList(vauleList)){
            return BigDecimal.ZERO;
        }else{
            return vauleList.get(0);
        }
        
    }

    /**
     * 获取三级段减值流水
     * @param happenDate 发生日
     * @param secId 资产代码
     * @return
     */
    public BigDecimal getStage3VauleMod(Date happenDate,String finprodId, String booksetId){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("happenDate", happenDate);
        param.put("finprodId", finprodId);
        param.put("booksetId", booksetId);
        param.put("stageP3", DictCodeListExt3.阶段_买入后出现减值_P3.getValue());
        // 若减值方案存在，则采用模型计算结果
        List<BigDecimal> vauleList = new ArrayList<>();
        vauleList = lossCommonBatchMapper.getStage3VauleMod(param);
        if(StringUtil.isEmptyList(vauleList)){
            return BigDecimal.ZERO;
        }else{
            return vauleList.get(0);
        }

    }
}
