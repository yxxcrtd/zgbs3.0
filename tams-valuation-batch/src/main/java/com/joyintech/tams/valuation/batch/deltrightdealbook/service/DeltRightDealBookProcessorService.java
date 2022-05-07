/*
 * 文件名：DeltRightDealBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月6日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.deltrightdealbook.service;

import org.apache.commons.collections.CollectionUtils;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.secdealaccounts.dto.DealParamterDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债权类交易账务批处理 Processor逻辑Service
 * 
 * @author weipeng
 * @version 1.0 2018年6月6日
 * @see DeltRightDealBookProcessorService
 * @since 1.0
 */
@Service
public class DeltRightDealBookProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的类共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    /**
     * 减值准备共通方法
     */
    @Autowired
    private LossCommonService lossCommonService;
    
    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    
    @Autowired
    private AccountingCommonService accountCommonService;
    
    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // reader传递的参数的明细信息
        List<FinProductDealInfoComDTO> acctEntityList = acctEntity.getListFinProductDealInfoComDTO();
        // 没有明细
        if (acctEntityList == null || acctEntityList.size() == 0) {
            return null;
        }
        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 1.数据准备
        // 1.1 变量定义
        DealParamterDto param = new DealParamterDto();
        
        // 变量.成本科目余额
        param.setVarCostSubjectAmt(BigDecimal.ZERO);
        
        // 变量.成本科目数量
        param.setVarCostSubjectNum(BigDecimal.ZERO);
        
        // 变量.应计利息科目余额
        param.setVarLxSubjectAmt(BigDecimal.ZERO);
        
        // 变量.公允价值变动科目余额
        param.setVarGySubjectAmt(BigDecimal.ZERO);
        
        // 变量.溢折价科目余额
        param.setVarYzjSubjectAmt(BigDecimal.ZERO);
        
        // 变量.其他应收款-利息科目余额 = MAP（KEY：分支序号，VALUE:科目余额）
        param.setVarQtyskxSubjectAmt(new HashMap<>());
        
        /**add by wujie on 20201019 start remark:资产减值**/
        // 变量.其他应收款-本金科目余额 = MAP（KEY：分支序号，VALUE:科目余额）
        param.setVarQtyskxBJSubjectAmt(new HashMap<>());
        // 变量.逾期罚息科目余额 = MAP（KEY：分支序号，VALUE:科目余额）
        param.setVarYqfxSubjectAmt(new HashMap<>());
        /**add by wujie on 20201019 end**/
        
        // 2.调用共通查询发生日前一日余额
        quarySubjectBalance(acctEntity, param);

        // 3.处理交易
        // 3.1 投资目的 = 公允价值的情况
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            quaryFvtpl(returnResult, acctEntity, param);
        }
        // 3.2 投资目的 = 摊余成本的情况
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            quaryAc(returnResult, acctEntity, param);
        }
        // 3.3 投资目的 = 综合收益的情况
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            quaryFvoci(returnResult, acctEntity, param);
        }
        return returnResult;
    }
    /**
     * 调用共通查询发生日前一日余额
     * 
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quarySubjectBalance(FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(acctEntity.getHappenDate());
        searchDao.setBookDate(acctEntity.getBookDate());
        searchDao.setTaskPri(acctEntity.getListFinProductDealInfoComDTO().get(0).getTaskPri());
        // 2.1.成本类科目余额
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.计划类资产_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.成本科目余额 = 科目余额
            param.setVarCostSubjectAmt(listSubjectBalance.get(0).getBalance());
            // 变量.成本科目数量 = 数量
            param.setVarCostSubjectNum(listSubjectBalance.get(0).getNum());
        }

        // 2.2.应计利息类科目余额
        List<SubjectBalance> listSubjectBalanceInt = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalanceInt != null && listSubjectBalanceInt.size() > 0) {
            // 变量.应计利息余额 = 科目余额
            param.setVarLxSubjectAmt(listSubjectBalanceInt.get(0).getBalance());
        }

        // 2.3.公允价值变动类科目余额
        List<SubjectBalance> listSubjectBalanceFV = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue())));
            listSubjectBalanceFV = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue())));
            listSubjectBalanceFV = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalanceFV != null && listSubjectBalanceFV.size() > 0) {
            // 变量.公允价值变动科目余额 = 科目余额
            param.setVarGySubjectAmt(listSubjectBalanceFV.get(0).getBalance());
        }

        // 2.4.溢折价类科目余额
        List<SubjectBalance> listSubjectBalanceEX = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalanceEX != null && listSubjectBalanceEX.size() > 0) {
            // 变量.溢折价科目余额 = 科目余额
            param.setVarYzjSubjectAmt(listSubjectBalanceEX.get(0).getBalance());
        }
        
        // 2.5.减值准备科目余额
        List<SubjectBalance> listSubjectBalanceJz = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())));
            listSubjectBalanceJz = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())));
            listSubjectBalanceJz = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalanceJz != null && listSubjectBalanceJz.size() > 0) {
            // 变量.减值准备科目余额 = 科目余额
            param.setVarJzzbSubjectAmt(listSubjectBalanceJz.get(0).getBalance());
        }else{
        	param.setVarJzzbSubjectAmt(BigDecimal.ZERO);
        }
        
        //2.6.其他应收款本金汇总余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
        // 变量.其他应收款本金汇总余额 = 科目余额
        param.setVarQtyskBjSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        
        //2.7.其他应收款利息汇总余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
        // 变量.其他应收款利息汇总余额 = 科目余额
        param.setVarQtyskLxSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        
        //2.8. 逾期利息汇总余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
        
        // 变量.逾期利息汇总余额 = 科目余额
        param.setVarYqfxSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        /**add by wujie on 20201019 end**/
    }
    
    
    /**
     * 摊余成本的情况场景处理
     * 
     * @param returnResult 场景返回结果
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAc(BookGenerateResult returnResult, FinProductDealInfoResult acctEntity,
                         DealParamterDto param) {
        //账套核算参数查询-调用账务类共通
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        
        //定义 变量.计提方式 = 返回值
        String accrType = null;
        
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        
        Map<String, Object> getValBokBooksetExtMap = accountCommonService
                .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
        
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue())!=null){
        	accrType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());
        }
        
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue())!=null){
        	offsetImptDateType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        }
        
        // 交易明细 ：交易类账务处理结果.交易明细列表
        for (FinProductDealInfoComDTO dealNow : acctEntity.getListFinProductDealInfoComDTO()) {
            TargetFinProduct rstBaseinfo = getTargetFinProduct(dealNow);
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                quaryAcBuy(rstBaseinfo, returnResult, dealNow, acctEntity, param,accrType);
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())) {
                quaryAcSell(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplIntAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_还本到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplRedAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_应收款项转入.getValue().equals(dealNow.getPs())) {
                getReceivablesIn(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_应收款项转出.getValue().equals(dealNow.getPs())) {
                getReceivablesOut(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            /**add by wujie on 20201019 start remark:资产减值**/
            else if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQtysklxQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQtyskbjQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            /**add by wujie on 20201019 end**/
        }
    }
    
    
    /**
     * 公允价值的情况场景处理
     * 
     * @param returnResult 场景返回结果
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvtpl(BookGenerateResult returnResult, FinProductDealInfoResult acctEntity,
                            DealParamterDto param) {
        // 交易明细 ：交易类账务处理结果.交易明细列表
        for (FinProductDealInfoComDTO dealNow : acctEntity.getListFinProductDealInfoComDTO()) {
            TargetFinProduct rstBaseinfo = getTargetFinProduct(dealNow);
            if (rstBaseinfo == null) {
                return;
            }
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                quaryFvtplBuy(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())) {
                quaryFvtplSell(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplIntAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,null);
            }
            else if (DictCodeList.交易方向_还本到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplRedAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,null);
            }
            else if (DictCodeList.交易方向_应收款项转入.getValue().equals(dealNow.getPs())) {
                this.getReceivablesIn(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_应收款项转出.getValue().equals(dealNow.getPs())) {
                this.getReceivablesOut(rstBaseinfo, returnResult, dealNow, acctEntity, param,null);
            }
        }
    }

    
    
    /**
     * 综合收益的情况场景处理
     * 
     * @param returnResult 场景返回结果
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvoci(BookGenerateResult returnResult, FinProductDealInfoResult acctEntity,
                            DealParamterDto param) {
    	
    	//账套核算参数查询-调用账务类共通
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        
        //定义 变量.计提方式 = 返回值
        String accrType = null;
        
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        
        Map<String, Object> getValBokBooksetExtMap = accountCommonService
                .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
        
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue())!=null){
        	accrType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());
        }
        
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue())!=null){
        	offsetImptDateType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        }
        
    	// 交易明细 ：交易类账务处理结果.交易明细列表
        for (FinProductDealInfoComDTO dealNow : acctEntity.getListFinProductDealInfoComDTO()) {
            TargetFinProduct rstBaseinfo = getTargetFinProduct(dealNow);
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {
                quaryFvociBuy(rstBaseinfo, returnResult, dealNow, acctEntity, param,accrType);
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())) {
                quaryFvociSell(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplIntAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_还本到账.getValue().equals(dealNow.getPs())) {
                quaryFvtplRedAcc(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_应收款项转入.getValue().equals(dealNow.getPs())) {
                this.getReceivablesIn(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeList.交易方向_应收款项转出.getValue().equals(dealNow.getPs())) {
                this.getReceivablesOut(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            /**add by wujie on 20201019 start remark:资产减值**/
            else if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQtysklxQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            else if (DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue().equals(dealNow.getPs())) {
            	quaryAcQtyskbjQlHx(rstBaseinfo, returnResult, dealNow, acctEntity, param);
            }
            /**add by wujie on 20201019 end**/
        }
    }
    /**
     * 摊余成本的情况场景处理(买入)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param accrType  计提方式
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcBuy(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
            FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
            DealParamterDto param,String accrType) {
    	
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_买入.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "摊余成本类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_收益型资产_买入.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);

        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        //定义 变量.应计利息 = 交易明细.利息总额
        BigDecimal intAmt = ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY);
        
        //定义 变量.面值 = 交易明细.交易份额
        BigDecimal faceVaule = ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY);
        
        // 记账金额元素map塞值
        // 定义 变量.溢折价 = 交易明细.交易金额 - 交易明细.交易本金 - 交易明细.利息总额
        BigDecimal spilledPrice = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
            dealNow.getShareAmt(), dealNow.getIntAmt());
        
        /**modify by wujie on 202011125 remark:买入不处理减值准备
        BigDecimal 减值准备 = BigDecimal.ZERO;
        if(DictCodeListExt3.阶段_买入后出现减值_P3.getValue().equals(acctEntity.getStage())){
        	intAmt = lossCommonService.calCommonIntAmt(rstBaseinfo, dealNow.getShareAmt(), 
        			accrType, dealNow.getTradeDate(), batchExecuteInfo.getBatchExedetailSeqNo(), acctEntity.getBooksetId());
        	intAmt = ArithmeticUtil.round(intAmt, NumberEnum.MONEY);
        	//变量.面值 =	交易明细.交易份额				
        	faceVaule = ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY);
        	//变量.减值准备 =  （变量.面值 + 变量.应计利息) - 交易明细.交易金额																			
        	减值准备 =  ArithmeticUtil.doSub(ArithmeticUtil.doAdd(faceVaule,intAmt),dealNow.getTradeAmt());
        	减值准备 = ArithmeticUtil.round(减值准备, NumberEnum.MONEY);
        }
        //减值准备	<- 变量.减值准备 *-1				
        accountAmountMap.put(BookConstant.IMPAMT,ArithmeticUtil.round(减值准备.negate(),NumberEnum.MONEY));
        **/
        /** 增加交易费用的处理 modify by yanya on 20210104 start**/
        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        accountAmountMap.put(BookConstant.TRADEFEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        if(dealTypesFeeDto.getTransactionFeeCode() != null) {
            accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
        }
        /** 增加交易费用的处理 modify by yanya on 20210104 end**/
        
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));// 本息和 = 交易金额
        
        accountAmountMap.put(BookConstant.COST,faceVaule);// 本金金额 = 变量.面值
        
        accountAmountMap.put(BookConstant.INTAMT,intAmt);// 应收利息=变量.应计利息
        
        // IF 变量.溢折价 >= 0
        if (spilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 溢折价_借 = 变量.溢折价
            accountAmountMap.put(BookConstant.DISCNT_PREAMT_D,
                ArithmeticUtil.round(spilledPrice, NumberEnum.MONEY));
        }
        else {
            // 溢折价_贷 = 变量.溢折价*-1
            accountAmountMap.put(BookConstant.DISCNT_PREAMT_C,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(spilledPrice, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.本息和_本位币
        BigDecimal dealAmtBccy = BigDecimal.ZERO;
        
        if (spilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 + 溢折价(借)_本位币
        	dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.DISCNT_PREAMT_D + BookConstant.BCCYAMT));
        }
        else {
        	// 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 - 溢折价(贷)_本位币
        	dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT))
            				, accountAmountMap.get(BookConstant.DISCNT_PREAMT_C + BookConstant.BCCYAMT));
        }
        /**买入交易不处理减值 modify by wujie on 20201125
        //本息和_本位币	= 	本金金额_本位币 + 应收利息_本位币  - 减值准备_本位币																					
        dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT))
        		, accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT));
        **/
        //本息和_本位币<-变量.本息和_本位币		
        accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
        		ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 + 变量.面值
        param.setVarCostSubjectAmt(ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
        		faceVaule));
        // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
        param.setVarCostSubjectNum(
            ArithmeticUtil.doAdd(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 + 变量.溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.doAdd(param.getVarYzjSubjectAmt(), 
        		ArithmeticUtil.round(spilledPrice, NumberEnum.MONEY)));
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 + 变量.应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.doAdd(param.getVarLxSubjectAmt(), intAmt));
        
        /** modify by wujie on 20201125 remark:买入时不处理减值
         //变量.减值准备科目余额	=	变量.减值准备科目余额 + 变量.减值准备	
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmt(),减值准备));		
        if(减值准备.compareTo(BigDecimal.ZERO) != 0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
        	
        	if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoAccr(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId()
        				,null);
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
       **/
    }
    
    /**
     * 摊余成本的情况场景处理(卖出)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcSell(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                             FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
                             DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_卖出.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "摊余成本类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_收益型资产_卖出.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_卖出.getCodeName() + "交易登账");
        }

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        //通道费账务明细代码	<-	变量.交易各种费用.通道费账务明细代码
        //通道费业务明细代码	<-	变量.交易各种费用.通道费业务明细代码
        BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, chlFeeCode);

        accountAmountMap.put(BookConstant.REAL_CHLAMT,
                ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
        accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealNow.getCcy());

        /** 增加交易费用的处理 modify by yanya on 20210104 start**/
        accountAmountMap.put(BookConstant.REAL_DEAL_FEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        /** 增加交易费用的处理 modify by yanya on 20210104 end**/

        // 记账金额元素map塞值
        
        // 定义 变量.冲减溢折价 = 变量.溢折价科目余额 * 交易明细.交易份额/变量.成本科目数量 调用金额共通方法设置有效小数位
        BigDecimal scourSpilledPrice = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(param.getVarYzjSubjectAmt(), dealNow.getShareAmt()), param.getVarCostSubjectNum(),
            NumberEnum.MONEY.getCode());
        
        //定义  变量.买卖价差=交易明细.交易金额 - (交易明细.交易份额 + 变量.冲减溢折价)  - 交易明细.利息总额 - 各种变量.交易各种费用.通道费
        BigDecimal saleDiffer = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
            dealNow.getShareAmt(), scourSpilledPrice, dealNow.getIntAmt(),
                accountAmountMap.get(BookConstant.REAL_CHLAMT));
        
        //定义   变量.减值准备 = 变量.减值准备科目余额 * 交易明细.交易份额 / 变量.成本科目数量							
        BigDecimal 减值准备 = BigDecimal.ZERO;//ArithmeticUtil.divide(ArithmeticUtil.multiply(param.getVarJzzbSubjectAmt(),dealNow.getShareAmt()), param.getVarCostSubjectNum());
        
        LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealNow, 
        		null, 
        		//本次发生额 = 变量.冲减应计利息 + 变量.冲减成本 + 变量.冲减溢折价
        		ArithmeticUtil.doAdd(dealNow.getShareAmt(),dealNow.getIntAmt(),scourSpilledPrice), 
        		//减值准备余额
        		param.getVarJzzbSubjectAmt(), 
        		//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        		ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                		param.getVarLxSubjectAmt(),param.getVarYzjSubjectAmt(),
                		param.getVarQtyskBjSubjectSumAmt(),
                		param.getVarQtyskLxSubjectSumAmt(),param.getVarYqfxSubjectSumAmt()), 
        		//应收款项余额
        		null);
        //发生份额
        inputdto.setHappenNum(dealNow.getShareAmt());
    	//总份额
        inputdto.setTotalNum(param.getVarCostSubjectNum());
        
        减值准备 = lossCommonBatchService.calcLossAmtForDeal(inputdto);
        
        减值准备 = ArithmeticUtil.round(减值准备, NumberEnum.MONEY);
        // 减值准备 <- 变量.减值准备
        accountAmountMap.put(BookConstant.IMPAMT,ArithmeticUtil.round(减值准备.negate(), NumberEnum.MONEY));
        
        /**modify by wujie on 20201019 end**/
        
        // 实收金额 <- 交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        // 应收利息 <- 交易明细.利息总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY));
        
        // 本金金额 <-交易明细.交易份额
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
        
        // IF 变量.冲减溢折价 >= 0
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 溢折价_贷 <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY));
        }
        else {
            // 溢折价_借 <- 变量.冲减溢折价*-1
            accountAmountMap.put(BookConstant.DSCINC_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(scourSpilledPrice, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }

        // IF （变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）= 变量.买卖价差
            accountAmountMap.put(BookConstant.INCOME_C, saleDiffer);
        }
        else {
            // 买卖价差（借）= 变量.买卖价差*-1
            accountAmountMap.put(BookConstant.INCOME_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(saleDiffer, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.买卖价差_本位币	
        BigDecimal saleDifferBccy = BigDecimal.ZERO;
        
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 变量.买卖价差_本位币 = 实收金额_本位币 + 实付通道费_本位币 - (本金金额_本位币 + 溢折价(贷)_本位币 + 应收利息_本位币)
        	saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
        					accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
            				//, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)),
                            , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)),
            				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        }
        else {
        	//变量.买卖价差_本位币 = 	实收金额_本位币 + 实付通道费_本位币 + 溢折价(借)_本位币 - (本金金额_本位币 + 应收利息_本位币)																											
        	saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
					accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
    				//, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)
                    , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)
    				, accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)),
    				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        }
    	
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) > 0) {
        	//买卖价差（贷）_本位币	<-	变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
       
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), dealNow.getPrinAmt()));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), dealNow.getIntAmt()));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.溢折价科目余额
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 
        		ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY)));
        //变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.减值准备
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),减值准备));
        
        if(减值准备.compareTo(BigDecimal.ZERO) != 0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
        	
        	if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId());
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
    }
    
    /**
     * 公允价值的情况场景处理(收息到账)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvtplIntAcc(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                  FinProductDealInfoComDTO dealNow,
                                  FinProductDealInfoResult acctEntity, DealParamterDto param,String offsetImptDateType) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        
        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类其他债权投资收息.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类收益型资产_收息到账.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_收息到账.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_摊余成本类其他债权投资收息.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_收益型资产收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_收息到账.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_综合收益类其他债权投资收息.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_综合收益类收益型资产_收息到账.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_收息到账.getCodeName() + "交易登账");
            }
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        // 查询交易的到期日对应的应计利息
        // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxSubjectAmt);
                
            }
        }
        // 记账金额元素map塞值 accountAmountMap设置如下：
        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        // 实付通道费 <-变量.交易各种费用.通道费
        accountAmountMap.put(BookConstant.REAL_CHLAMT,
            ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
        accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealNow.getCcy());
        //通道费账务明细代码 <-	变量.交易各种费用.通道费账务明细代码
        //通道费业务明细代码 <- 变量.交易各种费用.通道费业务明细代码
        BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID3, chlFeeCode);

        // 实际利息 = 交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());
        // 定义 变量.应计利息 = 0
        BigDecimal varInterestAccBal = BigDecimal.ZERO;
        
        if(dealNow.getTradeDate().compareTo(dealNow.getMdate())<0){
        	varInterestAccBal = dealNow.getFpriceAmt();
            // 应计利息 = 交易明细.全价总额
            accountAmountMap.put(BookConstant.INTAMT, varInterestAccBal);
        }else{
        	 // IF 交易明细.是否结清 = "否"
            if (DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())) {
            	varInterestAccBal = dealNow.getFpriceAmt().compareTo(qtyskxSubjectAmt)<0?dealNow.getFpriceAmt():qtyskxSubjectAmt;
                // 应计利息 = 交易明细.全价总额
                accountAmountMap.put(BookConstant.INTAMT, varInterestAccBal);
            }
            else {
            	varInterestAccBal = qtyskxSubjectAmt;
            }
        }
        // 应计利息 = 变量.应计利息
        accountAmountMap.put(BookConstant.INTAMT, varInterestAccBal);
        // 定义 变量.利息差额 = 交易明细.全价总额 - 变量.应计利息 - 变量.交易各种费用.通道费
        BigDecimal interstSpread = ArithmeticUtil.doSub(dealNow.getFpriceAmt(), varInterestAccBal, dealTypesFeeDto.getChlFee());
        // if 变量.利息差额 >= 0
        if (interstSpread.compareTo(BigDecimal.ZERO) >= 0) {
            // 利息差额(贷) <- 变量.利息差额
            accountAmountMap.put(BookConstant.NETAMT_C,
                ArithmeticUtil.round(interstSpread, NumberEnum.MONEY));
        }else {
            // 利息差额(借) <- 变量.利息差额*-1
            accountAmountMap.put(BookConstant.NETAMT_D,
                ArithmeticUtil.round(ArithmeticUtil.multiply(interstSpread, new BigDecimal(-1)),NumberEnum.MONEY));
        }
        
        
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(param.getVarJzzbSubjectAmt()) != 0){
        	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
        			DictCodeListExt1.现金流一级类型_利息.getValue(), 
        			//发生金额
        			varInterestAccBal, 
        			//减值准备余额
        			param.getVarJzzbSubjectAmt(), 
        			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.溢折价科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        			ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                			param.getVarLxSubjectAmt(),
                			param.getVarYzjSubjectAmt(),
                			param.getVarQtyskBjSubjectSumAmt(),
                			param.getVarQtyskLxSubjectSumAmt(),
                			param.getVarYqfxSubjectSumAmt()), 
        			//应收款余额
        			param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch())));
        	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
        }
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.利息差额_本位币 = 实际利息_本位币 + 实付通道费_本位币 - 应收利息_本位币
        BigDecimal interstSpreadBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
							accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
							//, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)),
                            , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)),
						 	accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
        if(interstSpreadBccy.compareTo(BigDecimal.ZERO) > 0) {
        	//利息差额（贷）_本位币  <- 变量.利息差额_本位币	
        	accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(interstSpreadBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(interstSpreadBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(),
            accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        qtyskxSubjectAmt = ArithmeticUtil.round(qtyskxSubjectAmt, NumberEnum.MONEY);
        varInterestAccBal = ArithmeticUtil.round(varInterestAccBal, NumberEnum.MONEY);
        
        // 全局变量的更新
        // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 变量.应计利息
        qtyskxSubjectAmt = ArithmeticUtil.subtract(qtyskxSubjectAmt, varInterestAccBal);
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxSubjectAmt);
        
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额  -  变量.应计利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskLxSubjectSumAmt(),varInterestAccBal));
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        
        if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
    		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
    			if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(dealNow.getInvAim())) {
    				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
    	    			valBokBookdetailBatchResult.add(dto);
    	    		 }
    			}
    			else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(dealNow.getInvAim())) {
	   				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
	   	    			valBokBookdetailBatchResult.add(dto);
	   	    		 }
    			}
    		}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId());
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
    }
    
    /**
     * 公允价值的情况场景处理(还本到账)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvtplRedAcc(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                  FinProductDealInfoComDTO dealNow,
                                  FinProductDealInfoResult acctEntity, DealParamterDto param,String offsetImptDateType) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类其他债权投资还本.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类收益型资产_还本到账.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_还本到账.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_摊余成本类其他债权投资还本.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_收益型资产还本.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_还本到账.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
            acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_综合收益类其他债权投资还本.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_综合收益类收益型资产_还本到账.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_还本到账.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }

        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
            dealNow.getValTradeFeeDetailList(),dealNow.getProdId(),dealNow.getPortfolioId());
        // 实付通道费 <-变量.交易各种费用.通道费
        accountAmountMap.put(BookConstant.CHLAMT,
            ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
        accountCurrencyMap.put(BookConstant.CHLAMT, dealNow.getCcy());

        // 交易金额 <- 交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        accountAmountMap.put(BookConstant.RECEITAMT,
            ArithmeticUtil.round(dealNow.getFpriceAmt(), NumberEnum.MONEY));

        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(param.getVarJzzbSubjectAmt()) != 0){
        	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
        			DictCodeListExt1.现金流一级类型_本金.getValue(), 
        			//发生金额
        			dealNow.getTradeAmt(), 
        			//减值准备余额
        			param.getVarJzzbSubjectAmt(), 
        			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        			ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                			param.getVarLxSubjectAmt(),
                			param.getVarYzjSubjectAmt(),
                			param.getVarQtyskBjSubjectSumAmt(),
                			param.getVarQtyskLxSubjectSumAmt(),
                			param.getVarYqfxSubjectSumAmt()), 
        			//应收款余额
        			param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch())));
        	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
        }
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        /**add by wujie on 20201019 start remark:资产减值**/
        //全局变量更新
        // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.其他应收款-本金科目余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
            }
        }
        /**add by wujie on 20201019 end**/
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 交易金额_本位币 <-	交易本金_本位币 + 实付通道费_本位币
        BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.RECEITAMT + BookConstant.BCCYAMT)
											, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT));
        accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
        
        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(),
            accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        /**add by wujie on 20201019 start remark:资产减值**/
        //全局变量更新
        //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()),
                ArithmeticUtil.doSub(
                    param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch())),
                    dealNow.getFpriceAmt()));
        
        //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
        qtyskxSubjectAmt = ArithmeticUtil.doSub(qtyskxSubjectAmt,ArithmeticUtil.round(dealNow.getFpriceAmt(), NumberEnum.MONEY));
        
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 变量.冲减成本
        param.setVarQtyskBjSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskBjSubjectSumAmt(),dealNow.getFpriceAmt()));
        
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        
        if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
    		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
    			if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(dealNow.getInvAim())) {
    				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
    	    			valBokBookdetailBatchResult.add(dto);
    	    		 }
    			}
    			else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(dealNow.getInvAim())) {
	   				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
	   	    			valBokBookdetailBatchResult.add(dto);
	   	    		 }
    			}
    		}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId());
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
        /**add by wujie on 20201019 end**/
    }
    
    /**
     * 应收款项转入
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    private void getReceivablesIn(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult, FinProductDealInfoComDTO dealNow,
                                  FinProductDealInfoResult acctEntity, DealParamterDto param) {
    	//初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        
        //ad.会计场景代码="应收款项转仓(转入)"
        ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转入.getValue());
        
        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        // 备注
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        //定义 变量.其他应收款-利息科目余额                                            =   变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxSubjectAmt);
            }
        }
        
        //应收款项-利息                   <-      交易明细.应计利息总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY));
        //逾期款项-本金                   <-      交易明细.交易本金
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
        //实收金额                    <-      交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 实收金额_本位币 <-	应收款项-利息_本位币 + 逾期款项-本金_本位币
        BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
											, accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT));
        accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
        
        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        //全局变量的更新
        //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 + 金额元素.应收款项 - 利息
        qtyskxSubjectAmt  = ArithmeticUtil.doAdd(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.INTAMT));
        //将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxSubjectAmt);
        
        /**add by wujie on 20201019 start remark:资产减值**/
        // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
        BigDecimal qtyskxBJSubjectAmt = param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.其他应收款-本金科目余额 = 科目余额
            	qtyskxBJSubjectAmt = listSubjectBalance.get(0).getBalance();
            }
        }
        //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 + 金额元素.逾期款项-本金
        qtyskxBJSubjectAmt = ArithmeticUtil.doAdd(qtyskxBJSubjectAmt,ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
        //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxBJSubjectAmt);
        
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doAdd(param.getVarQtyskLxSubjectSumAmt(),accountAmountMap.get(BookConstant.INTAMT)));
        
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
        param.setVarQtyskBjSubjectSumAmt(ArithmeticUtil.doAdd(param.getVarQtyskBjSubjectSumAmt(),dealNow.getPrinAmt()));
        
        /**add by wujie on 20201019 end**/
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        //基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(), accountNatrueBranchMap);
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
    }
    
    /**
     * 应收款项转仓转出
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    private void getReceivablesOut(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult, FinProductDealInfoComDTO dealNow,
                                   FinProductDealInfoResult acctEntity, DealParamterDto param,String offsetImptDateType) {
    	//初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        
        //ad.会计场景代码="应收款项转仓(转入)"
        ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转出.getValue());
        
        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);

        /** modif by yanya on 20201217 应收款项转出增加记账元素 是否其他债权 **/
        accountSceneMap.put(BookConstant.IS_QTZQ, rstBaseinfo.getIsQtzq());

        // 备注
        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        //定义 变量.其他应收款-利息科目余额       =   变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()),
                    qtyskxSubjectAmt);
            }
        }
        
        /**add by wujie on 20201019 start remark:资产减值**/
        // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
        BigDecimal qtyskxBJSubjectAmt = param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        if (qtyskxSubjectAmt == null) {
            // 查询交易的到期日对应的应计利息
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(dealNow.getBooksetId());
            searchDao.setBokDetailId(dealNow.getBokDetailId());
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBookDate(dealNow.getBookDate());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            searchDao.setBranch(dealNow.getBranch());
            
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.其他应收款-本金科目余额 = 科目余额
            	qtyskxBJSubjectAmt = listSubjectBalance.get(0).getBalance();
            }
        }
        /**add by wujie on 20201019 end remark:资产减值**/
        //定义        变量.应计利息
        BigDecimal varYjlx = BigDecimal.ZERO;
        //定义      变量.轧差金额
        BigDecimal varZcje = BigDecimal.ZERO;
        //if（交易明细.是否结清 = "否"）
        if (DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())) {
            //变量.应计利息                   <-      交易明细.应计利息总额
            varYjlx = dealNow.getIntAmt();
            //变量.轧差金额                 <-      0
            varZcje = BigDecimal.ZERO;
        } else {
            //变量.应计利息                   <-      变量.其他应收款-利息科目余额
            varYjlx = qtyskxSubjectAmt;
            //变量.轧差金额 = 交易明细.应计利息总额 - 变量.应计利息                         
            varZcje = ArithmeticUtil.subtract(dealNow.getIntAmt(), varYjlx);
            //if  变量.轧差金额 >= 0                      
            if (varZcje.compareTo(BigDecimal.ZERO) >= 0) {
                //轧差金额(贷)                   <-      变量.利息差额
                accountAmountMap.put(BookConstant.NETAMT_C,
                    ArithmeticUtil.round(varZcje, NumberEnum.MONEY));
            } else {
                //轧差金额(借)                   <-      变量.利息差额*-1
                accountAmountMap.put(BookConstant.NETAMT_D,
                    ArithmeticUtil.round(varZcje.negate(), NumberEnum.MONEY));
            }
        }

        //应收款项-利息                   <-      交易明细.应计利息总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(varYjlx, NumberEnum.MONEY));
        //逾期款项-本金                   <-      交易明细.交易本金
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
        //实收金额                    <-      交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        
        
        /**add by wujie on 20201031 start remark:资产减值：卖出、还本到账、收息到账**/
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额)) != 0){
        	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
        			DictCodeListExt1.现金流一级类型_利息.getValue(), 
        			//发生金额
        			varYjlx, 
        			//减值准备余额
        			param.getVarJzzbSubjectAmt(), 
        			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.溢折价科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        			ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                			param.getVarLxSubjectAmt(),
                			param.getVarYzjSubjectAmt(),
                			param.getVarQtyskBjSubjectSumAmt(),
                			param.getVarQtyskLxSubjectSumAmt(),
                			param.getVarYqfxSubjectSumAmt()), 
        			//应收款余额
        			qtyskxSubjectAmt);
        	冲抵减值金额 = ArithmeticUtil.doAdd(冲抵减值金额,lossCommonBatchService.calcLossAmtForDur(dto));
        }
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额)) != 0){
        	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
        			DictCodeListExt1.现金流一级类型_本金.getValue(), 
        			//发生金额
        			dealNow.getPrinAmt(), 
        			//减值准备余额
        			ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额), 
        			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.溢折价科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        			ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                			param.getVarLxSubjectAmt(),
                			param.getVarYzjSubjectAmt(),
                			param.getVarQtyskBjSubjectSumAmt(),
                			param.getVarQtyskLxSubjectSumAmt(),
                			param.getVarYqfxSubjectSumAmt()), 
        			//应收款余额
        			qtyskxBJSubjectAmt);
        	冲抵减值金额 = ArithmeticUtil.doAdd(冲抵减值金额,lossCommonBatchService.calcLossAmtForDur(dto));
        }
        
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        /**add by wujie on 20201031 end remark:资产减值：卖出、还本到账、收息到账**/
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.轧差金额_本位币    = 	实收金额_本位币 - (应收款项-利息_本位币 + 逾期款项-本金)	
        BigDecimal varZcjeBccy = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT), 
        							ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT), 
        									accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)));
        if(varZcjeBccy.compareTo(BigDecimal.ZERO)>0) {
        	//	轧差金额（贷）_本位币 <-	变量.轧差金额_本位币	
        	accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varZcjeBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//轧差金额（借）_本位币    <-	变量.轧差金额_本位币 * -1	
        	accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varZcjeBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        //全局变量的更新
        //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项 - 利息
        qtyskxSubjectAmt  = ArithmeticUtil.doSub(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.INTAMT));
        //将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxSubjectAmt);
        
        
        /**add by wujie on 20201019 start remark:资产减值**/
        //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 金额元素.逾期款项-本金
        qtyskxBJSubjectAmt = ArithmeticUtil.doSub(qtyskxBJSubjectAmt,ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));
        //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()), qtyskxBJSubjectAmt);
        
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskLxSubjectSumAmt(),accountAmountMap.get(BookConstant.INTAMT)));
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
        param.setVarQtyskBjSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskBjSubjectSumAmt(),dealNow.getPrinAmt()));
        
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        
        /**add by wujie on 20201019 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        //基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), accountNatrueBranchMap);
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
    }
    
   
    /**
     * 公允价值的情况场景处理(买入)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @param param varSecDealFeeSubject 费用损益计入方式
     * @see
     * @since 1.0
     */
    private void quaryFvtplBuy(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                               FinProductDealInfoComDTO dealNow,
                               FinProductDealInfoResult acctEntity, DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_买入.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "公允价值变动类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_买入.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "公允价值变动类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/

        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        // 记账金额元素map塞值
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));// 本息和 = 交易金额
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(dealNow.getCpriceAmt(), NumberEnum.MONEY));// 本金金额 = 净价总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY));// 应收利息=交易明细.利息总额

        /** 增加交易费用的处理 modify by yanya on 20201228 start**/
        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        // 交易手续费支出 <-交易各类费用处理.交易手续费 + 随交易支付交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE_OUT,
                ArithmeticUtil.round(ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getTransactionFee(), dealTypesFeeDto.getTransactionFeeForDealPay()), NumberEnum.MONEY));
        // 交易手续费 <-交易各类费用处理.交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        /** 增加交易费用的处理 modify by yanya on 20201228 end**/

        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);

        /** modify by yanya on 20210104 增加交易手续费**/
        // 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 + 溢折价(借)_本位币 + 交易手续费_本位币
        BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, dealTypesFeeDto.getTransactionFeeForDealPay(), changeRate));
        
        //本息和_本位币<-变量.本息和_本位币		
        accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
        		ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 + 交易明细.交易本金
        param.setVarCostSubjectAmt(ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),dealNow.getPrinAmt()));
        // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
        param.setVarCostSubjectNum(
            ArithmeticUtil.doAdd(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 + 变量.应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.doAdd(param.getVarLxSubjectAmt(), dealNow.getIntAmt()));
    }
    
    

    
    
    /**
     * 公允价值的情况场景处理(卖出)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param param param
     * @param param varSecDealFeeSubject 费用损益计入方式
     * @see
     * @since 1.0
     */
    private void quaryFvtplSell(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                FinProductDealInfoComDTO dealNow,
                                FinProductDealInfoResult acctEntity, DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_卖出.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "公允价值变动类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_卖出.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_卖出.getCodeName() + "交易登账");
        }

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);

        
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        //通道费账务明细代码	<-	变量.交易各种费用.通道费账务明细代码
        //通道费业务明细代码	<-	变量.交易各种费用.通道费业务明细代码
        BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, chlFeeCode);

        accountAmountMap.put(BookConstant.REAL_CHLAMT,
                ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
        accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealNow.getCcy());
        /** modify by yanya on 20201231 增加手续费支出 start**/
        accountAmountMap.put(BookConstant.REAL_DEAL_FEE_OUT,
                ArithmeticUtil.round(ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getTransactionFee(), dealTypesFeeDto.getTransactionFeeForDealPay()), NumberEnum.MONEY));
        accountAmountMap.put(BookConstant.REAL_DEAL_FEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        /** modify by yanya on 20201231 增加手续费支出 end**/

        // 记账金额元素map塞值
        
        // 变量.冲减公允价值变动 = 变量.公允价值变动科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal cjGyJzBd = ArithmeticUtil.multiply(param.getVarGySubjectAmt(), dealNow.getShareAmt());
        cjGyJzBd = ArithmeticUtil.divide(cjGyJzBd, param.getVarCostSubjectNum(),
            NumberEnum.MONEY.getCode());
        
        
        // 变量.冲减成本 = 变量.成本科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal cjCb = ArithmeticUtil.multiply(param.getVarCostSubjectAmt(), dealNow.getShareAmt());
        cjCb = ArithmeticUtil.divide(cjCb, param.getVarCostSubjectNum(),
            NumberEnum.MONEY.getCode());

        /** modify by yanya on 买卖价差减去随交易支付费用 start**/
        //定义  变量.买卖价差=交易明细.交易金额 - 变量.冲减成本  - 交易明细.利息总额 - 各种变量.交易各种费用.通道费 - 交易各种费用.随交易支付手续费
//        BigDecimal saleDiffer = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
//                cjCb, dealNow.getIntAmt(),
//                accountAmountMap.get(BookConstant.REAL_CHLAMT));
        BigDecimal saleDiffer = ArithmeticUtil.doAdd(
                ArithmeticUtil.doSub(dealNow.getTradeAmt(),
        		    cjCb, dealNow.getIntAmt(),
                    accountAmountMap.get(BookConstant.REAL_CHLAMT)
                ),
                dealTypesFeeDto.getTransactionFeeForDealPay());
        /** modify by yanya on 买卖价差减去随交易支付费用 end**/
        // 实收金额 <- 交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        // 应收利息 <- 交易明细.利息总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY));
        // 本金金额 <-变量.冲减成本
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(cjCb, NumberEnum.MONEY));
        
        // IF （变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）= 变量.买卖价差
            accountAmountMap.put(BookConstant.INCOME_C, saleDiffer);
        }
        else {
            // 买卖价差（借）= 变量.买卖价差*-1
            accountAmountMap.put(BookConstant.INCOME_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(saleDiffer, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 变量.冲减公允价值变动 >= 0
        if (cjGyJzBd.compareTo(BigDecimal.ZERO) >= 0) {
            // 公允价值变动(贷) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C,
                ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY));
            // 冲减公允价值变动(借) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D,
                ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY));
        }
        else {
            // 公允价值变动(借) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(cjGyJzBd, new BigDecimal(-1)), NumberEnum.MONEY));
            // 冲减公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(cjGyJzBd, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);

        /** modify by yanya on 买卖价差减去随交易支付费用 start**/
        // 变量.买卖价差_本位币 = 实收金额_本位币 + 实付通道费_本位币 - (本金金额_本位币  + 应收利息_本位币)
//        BigDecimal saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
//        					accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
//                            , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)),
//            				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
//                    				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        BigDecimal saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
                accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
                ,accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)
                ,calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, dealTypesFeeDto.getTransactionFeeForDealPay(), changeRate)
                ),
                ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                        , accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        /** modify by yanya on 买卖价差减去随交易支付费用 end**/
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) > 0) {
        	//买卖价差（贷）_本位币	<-	变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        cjGyJzBd = ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY);
       
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), cjCb));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), dealNow.getIntAmt()));
        // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), cjGyJzBd));
    }
    
    
    /**
     * 综合收益的情况场景处理(买入)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @param accrType 计提方式
     * @see
     * @since 1.0
     */
    private void quaryFvociBuy(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                               FinProductDealInfoComDTO dealNow,
                               FinProductDealInfoResult acctEntity, DealParamterDto param,
                               String accrType) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_买入.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "综合收益类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_买入.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/

        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        /** 增加交易费用的处理 modify by yanya on 20210104 start**/
        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        accountAmountMap.put(BookConstant.TRADEFEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        /** 增加交易费用的处理 modify by yanya on 20210104 end**/

        // 记账金额元素map塞值
        //定义 变量.应计利息 = 交易明细.利息总额											
        BigDecimal intAmt = ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY);
        //定义 变量.面值 = 交易明细.交易份额 								
        BigDecimal faceValue = ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY);
        
        // 定义 变量.溢折价 = 交易明细.交易金额 - 交易明细.交易份额 - 交易明细.利息总额
        BigDecimal spilledPrice = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
            dealNow.getShareAmt(), dealNow.getIntAmt());
        
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));// 本息和 = 交易金额
        accountAmountMap.put(BookConstant.COST,faceValue);// 本金金额 = 交易本金
        accountAmountMap.put(BookConstant.INTAMT,intAmt);// 应收利息=交易明细.利息总额
        
        // IF 变量.溢折价 >= 0
        if (spilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 溢折价_借 = 变量.溢折价
            accountAmountMap.put(BookConstant.DISCNT_PREAMT_D,
                ArithmeticUtil.round(spilledPrice, NumberEnum.MONEY));
        }
        else {
            // 溢折价_贷 = 变量.溢折价*-1
            accountAmountMap.put(BookConstant.DISCNT_PREAMT_C,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(spilledPrice, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.本息和_本位币
        BigDecimal dealAmtBccy = BigDecimal.ZERO;
        
        if (spilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 + 溢折价(借)_本位币
        	dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.DISCNT_PREAMT_D + BookConstant.BCCYAMT));
        }
        else {
        	// 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 - 溢折价(贷)_本位币
        	dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT))
            				, accountAmountMap.get(BookConstant.DISCNT_PREAMT_C + BookConstant.BCCYAMT));
        }
        //本息和_本位币<-变量.本息和_本位币		
        accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
        		ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 + 变量.面值
        param.setVarCostSubjectAmt(ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),faceValue));
        // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
        param.setVarCostSubjectNum(
            ArithmeticUtil.doAdd(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 + 变量.溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.doAdd(param.getVarYzjSubjectAmt(), 
        		ArithmeticUtil.round(spilledPrice, NumberEnum.MONEY)));
        
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 + 变量.应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.doAdd(param.getVarLxSubjectAmt(), intAmt));
    }
    
    /**
     * 综合收益的情况场景处理(卖出)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvociSell(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                FinProductDealInfoComDTO dealNow,
                                FinProductDealInfoResult acctEntity, DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_卖出.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "综合收益类其他债权投资资产"
                            + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                            + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_卖出.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_卖出.getCodeName() + "交易登账");
        }

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        // 费用处理共通
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                dealNow.getProdId(), dealNow.getPortfolioId(), dealNow.getFinprodId());
        //通道费账务明细代码	<-	变量.交易各种费用.通道费账务明细代码
        //通道费业务明细代码	<-	变量.交易各种费用.通道费业务明细代码
        BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, chlFeeCode);

        accountAmountMap.put(BookConstant.REAL_CHLAMT,
                ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
        accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealNow.getCcy());

        /** 增加交易费用的处理 modify by yanya on 20210104 start**/
        accountAmountMap.put(BookConstant.TRADEFEE,
                ArithmeticUtil.round(dealTypesFeeDto.getTransactionFee(), NumberEnum.MONEY));
        /** 增加交易费用的处理 modify by yanya on 20210104 end**/

        // 记账金额元素map塞值
        
        // 定义 变量.冲减溢折价 = 变量.溢折价科目余额 * 交易明细.交易份额/变量.成本科目数量 调用金额共通方法设置有效小数位
        BigDecimal scourSpilledPrice = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(param.getVarYzjSubjectAmt(), dealNow.getShareAmt()), param.getVarCostSubjectNum(),
            NumberEnum.MONEY.getCode());
        
        // 变量.冲减公允价值变动 = 变量.公允价值变动科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal cjGyJzBd = ArithmeticUtil.multiply(param.getVarGySubjectAmt(), dealNow.getShareAmt());
        cjGyJzBd = ArithmeticUtil.divide(cjGyJzBd, param.getVarCostSubjectNum(),
            NumberEnum.MONEY.getCode());
        
        //定义  变量.买卖价差=交易明细.交易金额 - (交易明细.交易份额 + 变量.冲减溢折价)  - 交易明细.利息总额 - 各种变量.交易各种费用.通道费
        BigDecimal saleDiffer = ArithmeticUtil.doSub(dealNow.getTradeAmt(),
            dealNow.getShareAmt(), scourSpilledPrice, dealNow.getIntAmt(),
                accountAmountMap.get(BookConstant.REAL_CHLAMT));
        
        //定义   变量.减值准备 = 变量.减值准备科目余额 * 交易明细.交易份额 / 变量.成本科目数量							
        BigDecimal 减值准备 = BigDecimal.ZERO;//ArithmeticUtil.divide(ArithmeticUtil.multiply(param.getVarJzzbSubjectAmt(),dealNow.getShareAmt()), param.getVarCostSubjectNum());
        LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealNow, 
        		null, 
        		//本次发生额 = 变量.冲减应计利息 + 变量.冲减成本 + 变量.冲减溢折价
        		ArithmeticUtil.doAdd(dealNow.getShareAmt(),dealNow.getIntAmt(),scourSpilledPrice), 
        		//减值准备余额
        		param.getVarJzzbSubjectAmt(), 
        		//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        		ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                		param.getVarLxSubjectAmt(),param.getVarYzjSubjectAmt(),
                		param.getVarQtyskBjSubjectSumAmt(),
                		param.getVarQtyskLxSubjectSumAmt(),param.getVarYqfxSubjectSumAmt()), 
        		//应收款项余额
        		null);
        //发生份额
        inputdto.setHappenNum(dealNow.getShareAmt());
    	//总份额
        inputdto.setTotalNum(param.getVarCostSubjectNum());
        
        减值准备 = lossCommonBatchService.calcLossAmtForDeal(inputdto);
        
        减值准备 = ArithmeticUtil.round(减值准备, NumberEnum.MONEY);
        
        
        // 减值准备 <- 变量.减值准备 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(减值准备.negate(), NumberEnum.MONEY));
        
        /**modify by wujie on 20201019 end**/
        
        // 实收金额 <- 交易明细.交易金额
        accountAmountMap.put(BookConstant.DEALAMT,
            ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        // 应收利息 <- 交易明细.利息总额
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(dealNow.getIntAmt(), NumberEnum.MONEY));
        // 本金金额 <-交易明细.交易份额
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
        // IF 变量.冲减溢折价 >= 0
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 溢折价_贷 <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY));
        }
        else {
            // 溢折价_借 <- 变量.冲减溢折价*-1
            accountAmountMap.put(BookConstant.DSCINC_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(scourSpilledPrice, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }

        // IF （变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）= 变量.买卖价差
            accountAmountMap.put(BookConstant.INCOME_C, saleDiffer);
        }
        else {
            // 买卖价差（借）= 变量.买卖价差*-1
            accountAmountMap.put(BookConstant.INCOME_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(saleDiffer, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 变量.冲减公允价值变动 >= 0
        if (cjGyJzBd.compareTo(BigDecimal.ZERO) >= 0) {
            // 公允价值变动(贷) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C,
                ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY));
            // 冲减公允价值变动(借) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D,
                ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY));
        }
        else {
            // 公允价值变动(借) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(cjGyJzBd, new BigDecimal(-1)), NumberEnum.MONEY));
            // 冲减公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(cjGyJzBd, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //定义 变量.买卖价差_本位币	
        BigDecimal saleDifferBccy = BigDecimal.ZERO;
        
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            // 变量.买卖价差_本位币 = 实收金额_本位币 + 实付通道费_本位币 - (本金金额_本位币 + 溢折价(贷)_本位币 + 应收利息_本位币)
        	saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
        					accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
            				//, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)),
                            , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)),
            				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        }
        else {
        	//变量.买卖价差_本位币 = 	实收金额_本位币 + 实付通道费_本位币 + 溢折价(借)_本位币 - (本金金额_本位币 + 应收利息_本位币)																											
        	saleDifferBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(
					accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT)
    				//, accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)
                    , accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)
    				, accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)),
    				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
        }
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) > 0) {
        	//买卖价差（贷）_本位币	<-	变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
       
        cjGyJzBd = ArithmeticUtil.round(cjGyJzBd, NumberEnum.MONEY);
        
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), dealNow.getPrinAmt()));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), dealNow.getShareAmt()));
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), dealNow.getIntAmt()));
        // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), cjGyJzBd));
        
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.溢折价科目余额
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 
        		ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY)));
        
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),减值准备));
        
        if(减值准备.compareTo(BigDecimal.ZERO) != 0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
        	
        	if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId());
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
        /**modify by wujie on 20201019 end**/
    }
    
    /**
     * 摊余成本的情况场景处理(清理核销)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcQlHx(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                             FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
                             DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // nvl(交易明细.汇总交易编号,交易明细.交易编号);
        if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
            accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
        }
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {

            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类收益型计划类资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                acctEntity.getInvAim())) {

            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 记账金额元素map塞值
        //定义		变量.清理应计利息 = 0											
        BigDecimal qlyjlx = BigDecimal.ZERO;
		//定义	变量.清理面值 = 0	
        BigDecimal qlmz = BigDecimal.ZERO;
		//定义         变量.清理减值准备 = 0	
        BigDecimal qljzzb = BigDecimal.ZERO;
		//定义        变量.清理数量 = 交易明细.交易份额	
        BigDecimal qlsl = dealNow.getShareAmt();
        //定义      变量.清理公允价值变动 = 0							
        BigDecimal glgyzjbd = BigDecimal.ZERO;
        //定义 变量.清理溢折价
        BigDecimal qlyzj = BigDecimal.ZERO;
        

        //if (交易明细.是否结清 = "是")
        if(DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())){
        	//变量.清理应计利息 = 变量.应计利息科目余额
        	qlyjlx = ArithmeticUtil.round(param.getVarLxSubjectAmt(), NumberEnum.MONEY);
        	//变量.清理面值 = 变量.成本科目余额 
        	qlmz = ArithmeticUtil.round(param.getVarCostSubjectAmt(), NumberEnum.MONEY);
        	//变量.清理减值准备 = 变量.减值准备科目余额
        	qljzzb = ArithmeticUtil.round(param.getVarJzzbSubjectAmt(), NumberEnum.MONEY);
        	//变量.清理数量 = 变量.成本科目数量
            //modify by yanya on 20201231 这里应该取成本科目数量，现在取成了应计利息科目余额
        	//qlsl = ArithmeticUtil.round(param.getVarLxSubjectAmt(), NumberEnum.QTY);
            qlsl = ArithmeticUtil.round(param.getVarCostSubjectNum(), NumberEnum.QTY);
        	//变量.清理公允价值变动 = 变量.公允价值变动科目余额
        	glgyzjbd = ArithmeticUtil.round(param.getVarGySubjectAmt(), NumberEnum.MONEY);
        	//变量.清理溢折价 = 变量.溢折价科目余额											
        	qlyzj = ArithmeticUtil.round(param.getVarYzjSubjectAmt(), NumberEnum.MONEY);
        }
        else{
        	//TODO 对象外
        }
        //modify by yanya on 20200106 减值计算损失增加溢折价
        //定义	变量.减值损失 =	变量.清理减值准备 + (变量.清理面值 + 变量.清理应计利息) + 变量.溢折价
        //BigDecimal 减值损失 = ArithmeticUtil.doAdd(qljzzb,qlmz,qlyjlx);
        BigDecimal 减值损失 = ArithmeticUtil.doAdd(qljzzb, qlmz, qlyjlx, qlyzj);
        
        // 面值	<-	变量.清理面值			
        accountAmountMap.put(BookConstant.COST,qlmz);
        
        // 发生数量->交易明细.交易份额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, qlsl);
        
        //应计利息				<-			变量.清理应计利息		
        accountAmountMap.put(BookConstant.INTAMT,qlyjlx);
        
        //减值准备				<-			变量.清理减值准备 *-1						
        accountAmountMap.put(BookConstant.IMPAMT,ArithmeticUtil.round(qljzzb.negate(), NumberEnum.MONEY));
        
        //if（变量.减值损失 >= 0）
        if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借)<- 减值损失(借)
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                ArithmeticUtil.round(减值损失, NumberEnum.MONEY));
        }
        else {
            // 减值损失(贷) <- 减值损失(借)*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(减值损失, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // if（变量.冲减溢折价>= 0）
        if (qlyzj.compareTo(BigDecimal.ZERO) >= 0) {
            // 溢折价(贷)				<-		变量.清理溢折价				
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(qlyzj, NumberEnum.MONEY));
        }
        else {
            // 溢折价(借)				<-		变量.清理溢折价*-1				
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(qlyzj, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        
        // 变量.冲减公允价值变动 >= 0
        if (glgyzjbd.compareTo(BigDecimal.ZERO) >= 0) {
            // 公允价值变动(贷) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C,
                ArithmeticUtil.round(glgyzjbd, NumberEnum.MONEY));
            // 冲减公允价值变动(借) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D,
                ArithmeticUtil.round(glgyzjbd, NumberEnum.MONEY));
        }
        else {
            // 公允价值变动(借) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(glgyzjbd, new BigDecimal(-1)), NumberEnum.MONEY));
            // 冲减公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(glgyzjbd, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        //修正计算逻辑 modify by yanya on 20210106
        // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币+金额元素.溢折价_贷_本位币)+金额元素.溢折价_借_本位币
    	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(
                            ArithmeticUtil.doAdd(
	    					    accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT),
                                accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)
                            ),
	        				ArithmeticUtil.doAdd(
	        						accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT), 
	        						accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
	        						accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
	                		)
        				);
        
        if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
            //modify by yanya on 20210106 金额元素赋值错误修正
        	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));

        }
        else {
            //modify by yanya on 20210106 金额元素赋值错误修正
        	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
       
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.清理面值
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), qlmz));
        
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.清理数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), qlsl));
        
        // 变量.应计利息科目余额 - 变量.清理应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), qlyjlx));
        
        //变量.公允价值变动科目余额	=	变量.公允价值变动科目余额 - 变量.清理公允价值变动														
        param.setVarGySubjectAmt(ArithmeticUtil.doSub(param.getVarGySubjectAmt(),glgyzjbd));
        
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.doSub(param.getVarYzjSubjectAmt(), qlyzj));
        
        //变量.减值准备科目余额	=	变量.减值准备科目余额 - 变量.清理减值准备			
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),qljzzb));
        
        if(qljzzb.compareTo(BigDecimal.ZERO) != 0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
        	
        	if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
        		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
        			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
        				valBokBookdetailBatchResult.add(dto);
        			}
        		}
        	}
        	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
        		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
        		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoClean(valBokBookdetailBatchDto, 
        				accountAmountMap.get(BookConstant.IMPAMT), 
        				accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT), 
        				param.getVarJzzbSubjectAmt(), 
        				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
        						param.getVarJzzbSubjectAmt(), changeRate)
        				,dealNow.getTradeId());
        		// 统计分析明细表（资产减值）
        		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
        	}
        }
    }
    
    
    /**
     * 摊余成本的情况场景处理(其他应收款利息-清理核销)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcQtysklxQlHx(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                             FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
                             DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // nvl(交易明细.汇总交易编号,交易明细.交易编号);
        if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
            accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
        }
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {

            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类收益型计划类资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        
        //定义 变量.其他应收款-利息科目余额       =   变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        
        // 查询交易的到期日对应的应计利息
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(dealNow.getBooksetId());
        searchDao.setBokDetailId(dealNow.getBokDetailId());
        searchDao.setHappenDate(dealNow.getHappenDate());
        searchDao.setBookDate(dealNow.getBookDate());
        searchDao.setTaskPri(dealNow.getTaskPri());
        searchDao.setBranch(dealNow.getBranch());
        
        if (qtyskxSubjectAmt == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()),
                    qtyskxSubjectAmt);
            }
        }
        
        //定义 变量.逾期罚息利息科目余额
        BigDecimal yqfxSubjectAmt = param.getVarYqfxSubjectAmt().get(dealNow.getBranch());
        if(yqfxSubjectAmt==null){
        	searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
            List<SubjectBalance> listSubjectBalanceYQ = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceYQ != null && listSubjectBalanceYQ.size() > 0) {
            	//  变量.逾期罚息利息科目余额 = 科目余额
            	yqfxSubjectAmt = listSubjectBalanceYQ.get(0).getBalance();
            	param.getVarYqfxSubjectAmt().put(String.valueOf(dealNow.getBranch()),yqfxSubjectAmt);
            }
        }
        // 记账金额元素map塞值
        
        //定义 变量.清理其他应收款-利息								
        BigDecimal qlqtyksLX = BigDecimal.ZERO;
        
		//定义 变量.清理罚息-利息
        BigDecimal qlytfxLX = BigDecimal.ZERO;
		

        //if (交易明细.是否结清 = "是")
        if(DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())){
        	//变量.清理其他应收款-利息	=	变量.其他应收款-利息科目余额							
        	qlqtyksLX = ArithmeticUtil.round(qtyskxSubjectAmt, NumberEnum.MONEY);
        	//变量.清理罚息-利息	=	变量.逾期利息科目余额						
        	qlytfxLX = ArithmeticUtil.round(yqfxSubjectAmt, NumberEnum.MONEY);
        }
        else{
        	//TODO 对象外
        }
        //定义 变量.减值损失 = 变量.清理其他应收款-利息+变量.清理罚息-利息
        BigDecimal 减值损失 = ArithmeticUtil.doAdd(qlqtyksLX,qlytfxLX);
        
        //应收款项-利息					<-		变量.清理其他应收款-利息								
        accountAmountMap.put(BookConstant.OTHER_INTAMT,qlqtyksLX);
        
        //逾期资产应计利息					<-		变量.清理罚息-利息	
        accountAmountMap.put(BookConstant.OVERINTAMT,qlytfxLX);

        
        //if（变量.减值损失 >= 0）
        if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借)<- 减值损失(借)
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                ArithmeticUtil.round(减值损失, NumberEnum.MONEY));
        }
        else {
            // 减值损失(贷) <- 减值损失(借)*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(减值损失, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);

        /** modify by yanya on 20210107 减值损失_本位币应该为 其他应收款-利息_本位币	+ 逾期资产应计利息_本位币 start**/
        // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币)
//    	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_INTAMT + BookConstant.BCCYAMT),
//        				accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        BigDecimal 减值损失_本位币 = ArithmeticUtil.doAdd(
                accountAmountMap.get(BookConstant.OTHER_INTAMT + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        /** modify by yanya on 20210107 减值损失_本位币应该为 其他应收款-利息_本位币	+ 逾期资产应计利息_本位币 end**/
        
        if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
        	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(),
            accountNatrueBranchMap);
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue(),
                accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
       
        // 全局变量的更新
        //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项 - 利息
        param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), ArithmeticUtil.doSub(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.OTHER_INTAMT)));
        //变量.逾期罚息科目余额 = 变量.逾期罚息科目余额 - 金额元素.逾期资产应计利息
        param.getVarYqfxSubjectAmt().put(String.valueOf(dealNow.getBranch()), ArithmeticUtil.doSub(yqfxSubjectAmt, accountAmountMap.get(BookConstant.OVERINTAMT)));
        
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项 - 利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskLxSubjectSumAmt(),accountAmountMap.get(BookConstant.OTHER_INTAMT)));
        //变量.逾期罚息科目汇总余额 = 变量.其逾期罚息科目汇总余额 - 金额元素.应收款项 - 利息
        param.setVarYqfxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarYqfxSubjectSumAmt(),accountAmountMap.get(BookConstant.OVERINTAMT)));
    }
    
    /**
     * 摊余成本的情况场景处理(其他应收款本金-清理核销)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcQtyskbjQlHx(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                             FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
                             DealParamterDto param) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        
        // nvl(交易明细.汇总交易编号,交易明细.交易编号);
        if (StringUtil.isNotEmpty(dealNow.getTotleTradeId())) {
            accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId());
        }
        
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        // accountSceneMap设置
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {

            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类收益型计划类资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
        }
        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(
                acctEntity.getInvAim())) {

            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_减值清理.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产"
                                + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs())
                                + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_减值清理.getValue());

                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
            }
        }
        
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        
        
        //定义 变量.其他应收款-本金科目余额       =   变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)
        BigDecimal qtyskxSubjectAmt = param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()));
        
        // 查询交易的到期日对应的本金
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(dealNow.getBooksetId());
        searchDao.setBokDetailId(dealNow.getBokDetailId());
        searchDao.setHappenDate(dealNow.getHappenDate());
        searchDao.setBookDate(dealNow.getBookDate());
        searchDao.setTaskPri(dealNow.getTaskPri());
        searchDao.setBranch(dealNow.getBranch());
        
        if (qtyskxSubjectAmt == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.本金科目余额 = 科目余额
                qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()),
                    qtyskxSubjectAmt);
            }
        }
        
        //定义 变量.逾期罚息利息科目余额
        BigDecimal yqfxSubjectAmt = param.getVarYqfxSubjectAmt().get(dealNow.getBranch());
        if(yqfxSubjectAmt==null){
        	searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
            List<SubjectBalance> listSubjectBalanceYQ = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceYQ != null && listSubjectBalanceYQ.size() > 0) {
            	//  变量.逾期罚息利息科目余额 = 科目余额
            	yqfxSubjectAmt = listSubjectBalanceYQ.get(0).getBalance();
            	param.getVarYqfxSubjectAmt().put(String.valueOf(dealNow.getBranch()),yqfxSubjectAmt);
            }
        }
        // 记账金额元素map塞值
        
        //定义 变量.清理其他应收款-本金								
        BigDecimal qlqtyksBJ = BigDecimal.ZERO;
        
		//定义 变量.清理罚息-利息
        BigDecimal qlytfxLX = BigDecimal.ZERO;
		

        //if (交易明细.是否结清 = "是")
        if(DictCodeList.是否_是.getValue().equals(dealNow.getIsClean())){
        	//变量.清理其他应收款-本金	=	变量.其他应收款-本金科目余额							
        	qlqtyksBJ = ArithmeticUtil.round(qtyskxSubjectAmt, NumberEnum.MONEY);
        	//变量.清理罚息-利息	=	变量.逾期利息科目余额						
        	qlytfxLX = ArithmeticUtil.round(yqfxSubjectAmt, NumberEnum.MONEY);
        }
        else{
        	//TODO 对象外
        }
        //定义 变量.减值损失 = 变量.清理其他应收款-本金息+变量.清理罚息-利息
        BigDecimal 减值损失 = ArithmeticUtil.doAdd(qlqtyksBJ,qlytfxLX);
        
        //应收款项-本金					<-		变量.清理其他应收款-本金								
        accountAmountMap.put(BookConstant.OTHER_COST,qlqtyksBJ);
        
        //逾期资产应计利息					<-		变量.清理罚息-利息	
        accountAmountMap.put(BookConstant.OVERINTAMT,qlytfxLX);

        
        //if（变量.减值损失 >= 0）
        if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
            // 减值损失(借)<- 减值损失(借)
            accountAmountMap.put(BookConstant.IMPAMTLOSS_D,
                ArithmeticUtil.round(减值损失, NumberEnum.MONEY));
        }
        else {
            // 减值损失(贷) <- 减值损失(借)*-1
            accountAmountMap.put(BookConstant.IMPAMTLOSS_C,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(减值损失, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);

        /** modify by yanya on 20210107 减值损失_本位币应该为 其他应收款-本金_本位币	+ 清理罚息-利息_本位币 start**/
        // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币)
//    	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
//        				accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        BigDecimal 减值损失_本位币 = ArithmeticUtil.doAdd(
                accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
        /** modify by yanya on 20210107 减值损失_本位币应该为 其他应收款-本金_本位币	+ 清理罚息-利息_本位币 end**/

        if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
        	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 记账金额元素币种信息塞值--key是随记账金额map的
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(),
            accountNatrueBranchMap);
        accountNatrueMap.put(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue(),
                accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
       
        // 全局变量的更新
        //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 金额元素.应收款项 - 利息
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()), ArithmeticUtil.doSub(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.OTHER_COST)));
        //变量.逾期罚息科目余额 = 变量.逾期罚息科目余额 - 金额元素.逾期资产应计利息
        param.getVarYqfxSubjectAmt().put(String.valueOf(dealNow.getBranch()), ArithmeticUtil.doSub(yqfxSubjectAmt, accountAmountMap.get(BookConstant.OVERINTAMT)));
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.应收款项 - 利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarQtyskLxSubjectSumAmt(),accountAmountMap.get(BookConstant.OTHER_INTAMT)));
        //变量.逾期罚息科目汇总余额 = 变量.其逾期罚息科目汇总余额 - 金额元素.应收款项 - 利息
        param.setVarYqfxSubjectSumAmt(ArithmeticUtil.doSub(param.getVarYqfxSubjectSumAmt(),accountAmountMap.get(BookConstant.OVERINTAMT)));
    }
    
    private void getAccountSceneMap(Map<String, Object> accountSceneMap,
            FinProductDealInfoComDTO dealNow,
            TargetFinProduct rstBaseinfo) {
    	// 境内外 = 变量.交易信息.境内外
        accountSceneMap.put(BookConstant.BLN_AREA, rstBaseinfo.getBlnArea());
        //资产类型
        accountSceneMap.put(BookConstant.ASSET_TYPE, rstBaseinfo.getFinprodType());
        //分类1
        accountSceneMap.put(BookConstant.TYPE_1, rstBaseinfo.getType1());
        //分类2
        accountSceneMap.put(BookConstant.TYPE_2, rstBaseinfo.getType2());
        //分类3
        accountSceneMap.put(BookConstant.TYPE_3, rstBaseinfo.getType3());
        //分类4
        accountSceneMap.put(BookConstant.TYPE_4, rstBaseinfo.getType4());
        //分类5
        accountSceneMap.put(BookConstant.TYPE_5, rstBaseinfo.getType5());
        //分类6
        accountSceneMap.put(BookConstant.TYPE_6, rstBaseinfo.getType6());
        
        if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())
            || DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())) {
            // 发生数量->交易明细.交易份额
            accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.QTY));
        }
        else {
            // 发生数量->交易明细.交易份额
            accountSceneMap.put(BookConstant.HAPPEN_NUMBER, BigDecimal.ZERO);
        }
        /**add by wujie on 20201019 start remark:资产减值**/
        if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealNow.getPs())
        		|| DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue().equals(dealNow.getPs())
        		|| DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue().equals(dealNow.getPs())) {
        	//ad.交易编号=nvl(交易明细.汇总交易编号,交易明细.交易编号);
        	accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTotleTradeId()==null?dealNow.getTradeId():dealNow.getTotleTradeId());
        }
        /**add by wujie on 20201019 end**/
    }
    
    
    /**
     * 获取标的信息
     * 
     * @param dealNow 交易明细
     * @return TargetFinProduct
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProduct(FinProductDealInfoComDTO dealNow) {
        // 1.2.标的类金融产品信息
        // 查询条件
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
        // 标的信息条件
        TargetSearchDto searchBaseinfo = new TargetSearchDto();
        searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(searchBaseinfo);

        Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
            dealNow.getFinprodId(), queryCriteria);

        // 变量.标的信息 = 上述共通返回标的信息
        TargetFinProduct rstBaseinfo = (TargetFinProduct)targetSearchMap.get(searchBaseinfo);
        return rstBaseinfo;
    }
    
    
    /**
     * 科目余额汇总
     * add by wujie on 20201030
     */
    private BigDecimal getSubjectBalanceSum(SubjectBalanceSearchDto searchDao,String expBalanceDirect) {
        Map<String, BigDecimal> map = accountingCommonService
                .getSubjectBalanceCollection(searchDao, DictCodeList.余额方向_借方.getValue(),null,searchDao.getTaskPri());
        // 变量.其他应收本金 = 科目余额
        BigDecimal balance = BigDecimal.ZERO;
        if(map !=null && map.size()>0){ 
            for(BigDecimal b:map.values()){
                balance = b;
                break;
            }
        }else{
            balance = BigDecimal.ZERO;
        }
        return balance;
    }
    
    /**
     * 冲减减值计算DTO初始化
     * @param acctEntity
     * @param dealNow
     * @return
     */
    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity,FinProductDealInfoComDTO dealNow,
    		String flag,//01 本金  02 利息
    		BigDecimal happenAmt,//发生金额
    		BigDecimal lossBalAmt,//减值准备余额
    		BigDecimal EADAmt, //当前EAD金额
    		BigDecimal receAmt //应收款项余额
    		){
    	LossAmtInputDto dto = new LossAmtInputDto();
    	//本息标识
    	dto.setFlag(flag);
    	//账套代码
    	dto.setBooksetId(acctEntity.getBooksetId());
    	//账务明细代码
    	dto.setBokDetailId(acctEntity.getBokDetailId());
    	//批处理优先级
    	dto.setTaskPri(dealNow.getTaskPri());
    	//分支序号
    	dto.setBranch(dealNow.getBranch());
    	//发生日
    	dto.setHappenDate(acctEntity.getHappenDate());
    	//记账日
    	dto.setBookDate(acctEntity.getBookDate());
    	//发生金额
    	dto.setHappenAmt(ArithmeticUtil.round(happenAmt, NumberEnum.MONEY));
    	//减值准备余额
    	dto.setLossBalAmt(lossBalAmt);
    	//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额  + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
    	dto.setEADAmt(EADAmt);
    	//应收款余额
    	dto.setReceAmt(receAmt);
    	//业务类型
        dto.setBusiType(dealNow.getFinprodType());
        //投资面对
        dto.setInvAim(dealNow.getInvAim());
        
    	return dto;
    }
}
