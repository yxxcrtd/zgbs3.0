/*
 * 文件名：DeltRightDealPreprocessingBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年6月6日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.deltrightdealpreprocessingbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.secdealaccounts.dto.DealParamterDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债权类交易账务批处理 Processor逻辑Service
 * 
 * @author chengzhong
 * @version 1.0 2018年6月6日
 * @see DeltRightDealPreprocessingBookProcessorNewService
 * @since 1.0
 */
@Service
public class DeltRightDealPreprocessingBookProcessorNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

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
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private CalculationCommonService calculationCommonService;

    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    
    @Autowired
    private LossCommonService lossCommonService;
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
        
        // 变量.其他应收款-本金科目余额 = MAP（KEY：分支序号，VALUE:科目余额）
        param.setVarQtyskxBJSubjectAmt(new HashMap<>());

        // 2.调用共通查询发生日前一日余额
        quarySubjectBalance(acctEntity, param);
        
        // 3.处理交易
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            quaryAc(returnResult, acctEntity, param);
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
            quaryFvtpl(returnResult, acctEntity, param);
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(acctEntity.getInvAim())) {
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
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
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
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
            listSubjectBalanceInt = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalanceInt != null && listSubjectBalanceInt.size() > 0) {
            // 变量.应计利息余额 = 科目余额
            param.setVarLxSubjectAmt(listSubjectBalanceInt.get(0).getBalance());
        }

        // 2.3.公允价值变动类科目余额
        List<SubjectBalance> listSubjectBalanceFV = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
            listSubjectBalanceFV = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue())));
            listSubjectBalanceFV = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalanceFV != null && listSubjectBalanceFV.size() > 0) {
            // 变量.应计利息余额 = 科目余额
            param.setVarGySubjectAmt(listSubjectBalanceFV.get(0).getBalance());
        }

        // 2.4.溢折价类科目余额
        List<SubjectBalance> listSubjectBalanceEX = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalanceEX != null && listSubjectBalanceEX.size() > 0) {
            // 变量.溢折价科目余额 = 科目余额
            param.setVarYzjSubjectAmt(listSubjectBalanceEX.get(0).getBalance());
        }
        
        // 2.5.减值类科目余额
        List<SubjectBalance> listSubjectBalanceJZ = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())));
            listSubjectBalanceJZ = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())));
            listSubjectBalanceJZ = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalanceJZ != null && listSubjectBalanceJZ.size() > 0) {
        	// 变量.减值类科目余额 = 科目余额
        	param.setVarJzzbSubjectAmt(listSubjectBalanceJZ.get(0).getBalance());
        	// 变量.减值类科目余额_本位币 = 科目余额_本位币
        	param.setVarJzzbSubjectAmtBccy(listSubjectBalanceJZ.get(0).getbAmt());
        }
        else{
        	param.setVarJzzbSubjectAmt(BigDecimal.ZERO);
        	param.setVarJzzbSubjectAmtBccy(BigDecimal.ZERO);
        }
    	//2.6.其他应收款本金汇总余额
//        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue())));
        // 变量.其他应收款本金汇总余额 = 科目余额
//        param.setVarQtyskBjSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        
        //2.7.其他应收款利息汇总余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue())));
        // 变量.其他应收款利息汇总余额 = 科目余额
        param.setVarQtyskLxSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        
        //2.8. 逾期利息汇总余额
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.逾期资产应计利息.getValue())));
        // 变量.逾期利息汇总余额 = 科目余额
        param.setVarYqfxSubjectSumAmt(this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue()));
        
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
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), 
        		paramCodeList);
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        if(!CollectionUtils.isEmpty(valBokBooksetExt)){
        	offsetImptDateType = valBokBooksetExt.get(0).getParamValue();
        }
        
    	// 交易明细 ：交易类账务处理结果.交易明细列表
        for (FinProductDealInfoComDTO dealNow : acctEntity.getListFinProductDealInfoComDTO()) {
            TargetFinProduct rstBaseinfo = getTargetFinProduct(dealNow);
            if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                quaryFvtplInt(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
                quaryAcRed(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
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
            if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                quaryFvtplInt(rstBaseinfo, returnResult, dealNow, acctEntity, param,null);
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
                quaryFvtplRed(rstBaseinfo, returnResult, dealNow, acctEntity, param);
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
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), 
        		paramCodeList);
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        if(!CollectionUtils.isEmpty(valBokBooksetExt)){
        	offsetImptDateType = valBokBooksetExt.get(0).getParamValue();
        }
        
    	// 交易明细 ：交易类账务处理结果.交易明细列表
        for (FinProductDealInfoComDTO dealNow : acctEntity.getListFinProductDealInfoComDTO()) {
            TargetFinProduct rstBaseinfo = getTargetFinProduct(dealNow);
            if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                quaryFvtplInt(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
            else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
                quaryFvociRed(rstBaseinfo, returnResult, dealNow, acctEntity, param,offsetImptDateType);
            }
        }
    }
    
    
    
    /**
     * 公允价值的情况场景处理(收息)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvtplInt(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                               FinProductDealInfoComDTO dealNow,
                               FinProductDealInfoResult acctEntity, DealParamterDto param,String offsetImptDateType) {
    	// 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
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
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类其他债权投资资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类其他债权投资资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_收益型资产_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
            //如果为其他债券
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类其他债权投资资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            else {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_收息.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_收息.getCodeName() + "交易登账");
            }
            /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        }
        // accountSceneMap设置
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 共通元素记账场景要素map设值 accountSceneMap
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
        // 是否累计余额->是
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(DateUtil.addDay(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());

        //(1) 查询交易的到期日对应的应计利息、成本科目数量
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal varCbNum = BigDecimal.ZERO;
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        List<SubjectBalance> listSubjectBalance_CB = new ArrayList<SubjectBalance>();
        
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(dealNow.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
            listSubjectBalance_CB = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            
            
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(dealNow.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance_CB = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            dealNow.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
            listSubjectBalance_CB = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.应计利息余额 = 科目余额
        	interest = listSubjectBalance.get(0).getBalance();
        }
        
        if (listSubjectBalance_CB != null && listSubjectBalance_CB.size() > 0) {
            // 变量.应计利息余额 = 科目余额
        	varCbNum = listSubjectBalance_CB.get(0).getNum();
        }
        
        //(2) 查询交易的到期日是否为付息截止日
        boolean payDeadlineFlag = finProductCommonService.payDeadlineJudgment(dealNow.getFinprodId(),
            dealNow.getMdate()).isfDeadline();
        
        
        FinCashPlanDto ginCashPlanDto = finProductCommonService.payDeadlineJudgment(dealNow.getFinprodId(),
                dealNow.getMdate());
        //现金流计划表DTO
        FinCashPlanEx finCashPlanEx = ginCashPlanDto.getFinCashPlanEx();
        
        if (!payDeadlineFlag) {
        	
            //变量.当前本金 = 调用共通计算类共通【当前未还本金（DTO.账套日期,DTO.金融产品代码,变量.标的信息.发行规模）】
            BigDecimal varPrinAmt = finProductCommonService.getUnpaidPrinAmt(
                dealNow.getBooksetDate(), dealNow.getFinprodId(),
                rstBaseinfo.getIssueAmt());

            //变量.交易份额 = 现金流计划表DTO.计划支付本金 / 变量.当前本金
            BigDecimal varShareAmt = ArithmeticUtil.divide(finCashPlanEx.getPrinAmt() == null 
                ? BigDecimal.ZERO : finCashPlanEx.getPrinAmt(), varPrinAmt, NumberEnum.ALL_PRECISION.getCode());
            varShareAmt = ArithmeticUtil.multiply(varShareAmt, varCbNum);
            
            //变量.应计利息 = 变量.应计利息 * 变量.交易份额 / 变量.成本科目数量
            interest = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(interest, varShareAmt), varCbNum,NumberEnum.MONEY.getCode());
        }

        // accountAmountMap设置
        // 应计利息
        accountAmountMap.put(BookConstant.INTAMT,
            ArithmeticUtil.round(interest, NumberEnum.MONEY));
        accountAmountMap.put(BookConstant.OTHER_INTAMT, ArithmeticUtil.round(interest, NumberEnum.MONEY));
        // 	应计利息_本位币	<- 应计利息 * 变量.汇率
        accountAmountMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT, calculationCommonService.doMultiply(interest, changeRate));
        accountAmountMap.put(BookConstant.OTHER_INTAMT + BookConstant.BCCYAMT, calculationCommonService.doMultiply(interest, changeRate));
        
        
        
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        if (param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch())) == null) {
            searchDao.setHappenDate(dealNow.getHappenDate());
            searchDao.setBranch(dealNow.getBranch());
            searchDao.setTaskPri(dealNow.getTaskPri());
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if(listSubjectBalance!=null && listSubjectBalance.size()>0){
            	 param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()), listSubjectBalance.get(0).getBalance());
            }
        }
        
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(param.getVarJzzbSubjectAmt()) != 0){
        	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow, 
        			DictCodeListExt1.现金流一级类型_利息.getValue(), 
        			//发生金额
        			interest, 
        			//减值准备余额
        			param.getVarJzzbSubjectAmt(), 
        			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额- 变量.其他应收款当前发生余额
        			ArithmeticUtil.doSub(
    					ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                			param.getVarLxSubjectAmt(),
                			param.getVarYzjSubjectAmt(),
                			param.getVarQtyskBjSubjectSumAmt(),
                			param.getVarQtyskLxSubjectSumAmt(),
                			param.getVarYqfxSubjectSumAmt()),
    					param.getGlb_tdyqtyskAmtSum()
        			),
        			//应收款余额 = 发生金额
        			interest,null);
	
        	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
        }
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        /**add by wujie on 20201111 start remark:减值准备科目进行余额更新**/
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /**add by wujie on 20201111 end remark:减值准备科目进行余额更新**/
        
        
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
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
            accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);

        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        
        // 全局变量的更新
        // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.应计利息
        param.setVarLxSubjectAmt(
            ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), ArithmeticUtil.round(interest, NumberEnum.MONEY)));
        // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
        param.getVarQtyskxSubjectAmt().put(String.valueOf(dealNow.getBranch()),
            ArithmeticUtil.doAdd(
                param.getVarQtyskxSubjectAmt().get(String.valueOf(dealNow.getBranch())),
                interest));
        //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 + 变量.应计利息
        param.setVarQtyskLxSubjectSumAmt(ArithmeticUtil.doAdd(param.getVarQtyskLxSubjectSumAmt(),interest));
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        //变量.减值准备科目余额_本位币 = 变量.减值准备科目余额 + 金额元素.减值金额_本位币
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        
        /**add by wujie on 20201124 start remar:资产减值**/
        //变量.其他应收款当前发生余额 = 变量.其他应收款当前发生余额 + 变量.应计利息
        param.setGlb_tdyqtyskAmtSum(ArithmeticUtil.doAdd(param.getGlb_tdyqtyskAmtSum(),interest));
        /**add by wujie on 20201124 end remar:资产减值**/
        
        if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
    		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
    			if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(dealNow.getInvAim())) {
    				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())) {
    	    			valBokBookdetailBatchResult.add(dto);
    	    		 }
    			}
    			else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(dealNow.getInvAim())) {
	   				 if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())) {
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
     * 公允价值的情况场景处理(还本到期)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvtplRed(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
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
        
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());

        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_还本到期.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "公允价值变动类其他债权投资资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_还本到期.getValue());

            // accountSceneMap设置
            // 备注
            if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_还本.getCodeName() + "交易登账");
            } else {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "公允价值变动类计划类(收益型)资产" + DictCodeList.交易方向_到期.getCodeName() + "交易登账");
            }
        }

        // accountSceneMap设置
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 共通元素记账场景要素map设值 accountSceneMap
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
        // 是否累计余额->是
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        
        // 变量.结转日成本科目余额
        BigDecimal varZjrCbye = BigDecimal.ZERO;
        // 变量.结转日成本科目数量
        BigDecimal varZjrCbsl = BigDecimal.ZERO;

        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(dealNow.getBooksetId());
        searchDao.setBokDetailId(dealNow.getBokDetailId());
        searchDao.setHappenDate(DateUtil.addDay(dealNow.getMdate(), -1));
        searchDao.setBookDate(dealNow.getBookDate());
        
        //结转日成本科目余额
        searchDao.setBaseSubjectNatureList(
            new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        
        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.结转日成本科目余额 = 科目余额
            varZjrCbye = listSubjectBalance.get(0).getBalance();
            // 变量.结转日成本科目数量 = 数量
            varZjrCbsl = listSubjectBalance.get(0).getNum();
        }

        // 结转日公允价值变动科目余额
        BigDecimal varGyjzbdye = BigDecimal.ZERO;
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(
            Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
        List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
            // 变量.结转日公允价值变动科目余额 = 科目余额
            varGyjzbdye = listSubjectBalanceEx.get(0).getBalance();
        }
        
        //变量.交易份额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varShareAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易金额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varDealAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易本金 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varPrinAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        
        // 定义 变量.冲减成本 = 0
        BigDecimal scourCost = BigDecimal.ZERO;
        // 定义 变量.冲减数量 = 0
        BigDecimal scourQuantity = BigDecimal.ZERO;
        // 定义 变量.买卖价差 = 0
        BigDecimal saleDiffer = BigDecimal.ZERO;
        // 变量.冲减公允价值变动
        BigDecimal carCjgyjzbd = BigDecimal.ZERO;
        
        // 交易方向 = "到期"
        if (DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
        	// 变量.冲减公允价值变动 = 变量.结转日公允价值变动科目余额
        	carCjgyjzbd = varGyjzbdye;
            // 变量.冲减成本 = 变量.结转日成本科目余额
            scourCost = varZjrCbye;
            // 变量.冲减数量 <- 变量.结转日成本科目数量
            scourQuantity = varZjrCbsl;
        }
        else {
        	
        	// 变量.冲减公允价值变动 = 变量.变量.结转日公允价值变动科目余额 * 变量.交易份额/变量.成本科目数量  调用金额共通方法设置有效小数位
        	carCjgyjzbd = ArithmeticUtil.round(ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(varGyjzbdye, varShareAmt),
                    varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);

            // 变量.冲减成本 = 变量.结转日成本科目余额 * 变量.交易份额/变量.成本科目数量  调用金额共通方法设置有效小数位
            scourCost = ArithmeticUtil.round(ArithmeticUtil.divide(
                ArithmeticUtil.multiply(varZjrCbye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
            // 变量.冲减数量 <- 变量.交易份额
            scourQuantity = ArithmeticUtil.round(varShareAmt, NumberEnum.MONEY);
        }
        // 变量.买卖价差 = 变量.交易本金 - 变量.冲减成本 取两位小
        saleDiffer = ArithmeticUtil.round(ArithmeticUtil.doSub(varPrinAmt, scourCost), NumberEnum.MONEY);
        
        // accountAmountMap设置
        // 交易金额 <- 变量.交易金额
        accountAmountMap.put(BookConstant.OTHER_COST,
            ArithmeticUtil.round(varDealAmt, NumberEnum.MONEY));
        
        // 面值 <- 变量.冲减成本
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(scourCost, NumberEnum.MONEY));

        // if（变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）<- 变量.买卖价差
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
        if (carCjgyjzbd.compareTo(BigDecimal.ZERO) >= 0) {
            // 公允价值变动(贷) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C,
                ArithmeticUtil.round(carCjgyjzbd, NumberEnum.MONEY));
            // 冲减公允价值变动(借) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D,
                ArithmeticUtil.round(carCjgyjzbd, NumberEnum.MONEY));
        }
        else {
            // 公允价值变动(借) <-变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(carCjgyjzbd, new BigDecimal(-1)), NumberEnum.MONEY));
            // 冲减公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(carCjgyjzbd, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(scourQuantity, NumberEnum.QTY));
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        
        // 	变量.买卖价差_本位币 = 	交易金额_本位币 - 面值_本位币
        BigDecimal saleDifferBccy = ArithmeticUtil.round(ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
        						accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)),NumberEnum.MONEY);
        	
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) >= 0) {
        	//买卖价差（贷）_本位币   <- 变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币	<-	变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
        }
        //本位币	<- 计提类账务处理结果.本位币
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
//        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
//        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
//        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
//            accountNatrueBranchMap);
//        ad.setAccountNatrueMap(accountNatrueMap);

        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        scourCost = ArithmeticUtil.round(scourCost, NumberEnum.MONEY);
        scourQuantity = ArithmeticUtil.round(scourQuantity, NumberEnum.QTY);
        carCjgyjzbd = ArithmeticUtil.round(carCjgyjzbd, NumberEnum.MONEY);
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), scourCost));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), scourQuantity));
        // 变量.公允价值变动科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), carCjgyjzbd));
    }
    
    /**
     * 摊余成本的情况场景处理(还本到期)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryAcRed(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                            FinProductDealInfoComDTO dealNow, FinProductDealInfoResult acctEntity,
                            DealParamterDto param,String offsetImptDateType) {
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        
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
        
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_还本到期.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "摊余成本类其他债权投资资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_收益型资产_还本到期.getValue());

            // accountSceneMap设置
            if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_还本.getCodeName() + "交易登账");
            } else {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "摊余成本类计划类(收益型)资产" + DictCodeList.交易方向_到期.getCodeName() + "交易登账");
            }
        }
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
        
        // accountSceneMap设置
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 共通元素记账场景要素map设值 accountSceneMap
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
        // 是否累计余额->是
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        
        // 变量.结转日成本科目余额
        BigDecimal varZjrCbye = BigDecimal.ZERO;
        // 变量.结转日成本科目数量
        BigDecimal varZjrCbsl = BigDecimal.ZERO;

        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(dealNow.getBooksetId());
        searchDao.setBokDetailId(dealNow.getBokDetailId());
        searchDao.setHappenDate(DateUtil.addDay(dealNow.getMdate(), -1));
        searchDao.setBookDate(dealNow.getBookDate());
        searchDao.setBaseSubjectNatureList(
            new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        
        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.结转日成本科目余额 = 科目余额
            varZjrCbye = listSubjectBalance.get(0).getBalance();
            // 变量.结转日成本科目数量 = 数量
            varZjrCbsl = listSubjectBalance.get(0).getNum();
        }

        // 结转日溢折价科目余额
        BigDecimal varZjrYzjye = BigDecimal.ZERO;
        searchDao.setBaseSubjectNatureList(
            new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
        List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
            // 变量.结转日溢折价科目余额 = 科目余额
            varZjrYzjye = listSubjectBalanceEx.get(0).getBalance();
        }
        
        
        //变量.交易份额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varShareAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易金额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varDealAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易本金 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varPrinAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());

        
        // 定义 变量.冲减溢折价 = 0
        BigDecimal scourSpilledPrice =BigDecimal.ZERO;
        // 定义 变量.冲减成本 = 0
        BigDecimal scourCost = BigDecimal.ZERO;
        // 定义 变量.冲减数量 = 0
        BigDecimal scourQuantity = BigDecimal.ZERO;
        // 定义 变量.买卖价差 = 0
        BigDecimal saleDiffer = BigDecimal.ZERO;
       
        // 交易方向 = "到期"
        if (DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
        	// 变量.冲减溢折价 = 变量.结转日溢折价科目余额
            scourSpilledPrice = varZjrYzjye;
            // 变量.冲减成本 = 变量.结转日成本科目余额
            scourCost = varZjrCbye;
            // 变量.冲减数量 <- 变量.结转日成本科目数量
            scourQuantity = varZjrCbsl;
        }
        else {
        	// 变量.冲减溢折价 = 变量.结转日溢折价科目余额 * 变量.交易份额/变量.成本科目数量  调用金额共通方法设置有效小数位
            scourSpilledPrice = ArithmeticUtil.round(ArithmeticUtil.divide(
                ArithmeticUtil.multiply(varZjrYzjye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
            // 变量.冲减成本 = 变量.结转日成本科目余额 * 变量.交易份额/变量.成本科目数量  调用金额共通方法设置有效小数位
            scourCost = ArithmeticUtil.round(
            		ArithmeticUtil.divide(ArithmeticUtil.multiply(varZjrCbye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
            // 变量.冲减数量 <- 变量.交易份额
            scourQuantity = ArithmeticUtil.round(varShareAmt, NumberEnum.MONEY);

        }

        // 变量.买卖价差 = 变量.交易本金 - （变量.冲减成本+变量.冲减溢折价） 调用金额共通方法设置有效小数位
        saleDiffer = ArithmeticUtil.round(ArithmeticUtil.doSub(varPrinAmt, scourCost,scourSpilledPrice), NumberEnum.MONEY);

        // 交易金额 <- 变量.交易金额
        accountAmountMap.put(BookConstant.OTHER_COST,
            ArithmeticUtil.round(varDealAmt, NumberEnum.MONEY));
        // 面值 <- 变量.冲减成本
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(scourCost, NumberEnum.MONEY));

        // if（变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）<- 变量.买卖价差
            accountAmountMap.put(BookConstant.INCOME_C, saleDiffer);
        }
        else {
            // 买卖价差（借）= 变量.买卖价差*-1
            accountAmountMap.put(BookConstant.INCOME_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(saleDiffer, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
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
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(scourQuantity, NumberEnum.QTY));
        
        
        // 定义 变量.其他应收款-本金科目余额 = 变量.其他应收款科目余额MAP.get(交易明细.分支序号)
//        if(param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()))==null){
//            searchDao = new SubjectBalanceSearchDto();
//            searchDao.setBooksetId(acctEntity.getBooksetId());
//            searchDao.setBokDetailId(acctEntity.getBokDetailId());
//            searchDao.setHappenDate(acctEntity.getHappenDate());
//            searchDao.setBookDate(acctEntity.getBookDate());
//            searchDao.setTaskPri(dealNow.getTaskPri());
//            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
//                Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue())));
//            searchDao.setBranch(dealNow.getBranch());
//            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
//                searchDao, DictCodeList.余额方向_借方.getValue());
//            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
//                param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()), listSubjectBalance.get(0).getBalance());
//            }
//        }
        
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(param.getVarJzzbSubjectAmt()) != 0){
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                DictCodeListExt1.现金流一级类型_本金.getValue(),
                // 发生金额 = 变量.冲减成本
                scourCost,
                // 减值准备余额
                param.getVarJzzbSubjectAmt(),
                // 当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额- 变量.其他应收款当前发生余额
                ArithmeticUtil.doSub(
	                ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(), param.getVarLxSubjectAmt(),
	                    param.getVarYzjSubjectAmt(), param.getVarQtyskBjSubjectSumAmt(),
	                    param.getVarQtyskLxSubjectSumAmt(), param.getVarYqfxSubjectSumAmt()),
	                param.getGlb_tdyqtyskAmtSum()
                ),
                // 应收款余额 = 发生金额
                scourCost,
                varZjrCbsl);
            //溢折价科目
            dto.setVarYzjSubjectAmt(scourSpilledPrice);
        	
        	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
        }
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, 
        	    calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
                
        
        //定义 变量.买卖价差_本位币
        BigDecimal saleDifferBccy = BigDecimal.ZERO;
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            //变量.买卖价差_本位币 = 	交易金额_本位币 - (面值_本位币 + 溢折价(贷)_本位币)
        	saleDifferBccy = ArithmeticUtil.round(ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
        						ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT), 
    									accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT))),NumberEnum.MONEY);
        }
        else {
        	// 	变量.买卖价差_本位币 = 	交易金额_本位币 + 溢折价(借)_本位币 - 面值_本位币
        	saleDifferBccy = ArithmeticUtil.round(ArithmeticUtil.doSub(
        				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT), 
							accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)),
        						accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)),NumberEnum.MONEY);
        }
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) >= 0) {
        	//买卖价差（贷）_本位币   <- 变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币	<-	变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }

        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        
        /**add by wujie on 20201111 start remark:减值准备科目进行余额更新**/
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /**add by wujie on 20201111 end remark:减值准备科目进行余额更新**/
        
        
        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
//        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
//        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
//        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
//        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
//            accountNatrueBranchMap);
//        ad.setAccountNatrueMap(accountNatrueMap);

        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        scourCost = ArithmeticUtil.round(scourCost, NumberEnum.MONEY);
        scourQuantity = ArithmeticUtil.round(scourQuantity, NumberEnum.QTY);
        scourSpilledPrice = ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY);
        // 全局变量的更新scourSpilledPrice
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), scourCost));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), scourQuantity));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), scourSpilledPrice));
        
        //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()),
        		ArithmeticUtil.doAdd(param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch())),scourCost));
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 + 变量.冲减成本
        param.setVarQtyskBjSubjectSumAmt(ArithmeticUtil.doAdd(param.getVarQtyskBjSubjectSumAmt(),scourCost));
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        
        //变量.减值准备科目余额_本位币 = 变量.减值准备科目余额 + 金额元素.减值金额_本位币
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        
        
        /**add by wujie on 20201124 start remar:资产减值**/
        //变量.其他应收款当前发生余额 = 变量.其他应收款当前发生余额 + 变量.冲减成本
        param.setGlb_tdyqtyskAmtSum(ArithmeticUtil.doAdd(param.getGlb_tdyqtyskAmtSum(),scourCost));
        /**add by wujie on 20201124 end remar:资产减值**/
        
        
        if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
    		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
    			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())) {
	    			valBokBookdetailBatchResult.add(dto);
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
     * 综合收益的情况场景处理(还本到期)
     * 
     * @param rstBaseinfo 标的信息
     * @param returnResult 场景返回结果
     * @param dealNow 交易明细
     * @param acctEntity acctEntity
     * @param param param
     * @see
     * @since 1.0
     */
    private void quaryFvociRed(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
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
        
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
        //如果为其他债券
        if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_还本到期.getValue());
            accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "综合收益类其他债权投资资产" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs()) + "交易登账");
        }
        else {
            ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_还本到期.getValue());

            // accountSceneMap设置
            // 备注
            if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_还本.getCodeName() + "交易登账");
            } else {
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "综合收益类计划类(收益型)资产" + DictCodeList.交易方向_到期.getCodeName() + "交易登账");
            }
        }

        // accountSceneMap设置
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 共通元素记账场景要素map设值 accountSceneMap
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
        // 是否累计余额->是
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
        

        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(dealNow.getBooksetId());
        searchDao.setBokDetailId(dealNow.getBokDetailId());
        searchDao.setHappenDate(DateUtil.addDay(dealNow.getMdate(), -1));
        searchDao.setBookDate(dealNow.getBookDate());
        
        //.结转日成本科目余额
        searchDao.setBaseSubjectNatureList(
            new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
        // 变量.结转日成本科目余额
        BigDecimal varZjrCbye = BigDecimal.ZERO;
        // 变量.结转日成本科目数量
        BigDecimal varZjrCbsl = BigDecimal.ZERO;
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.结转日成本科目余额 = 科目余额
            varZjrCbye = listSubjectBalance.get(0).getBalance();
            varZjrCbsl = listSubjectBalance.get(0).getNum();
        }

        // 结转日溢折价科目余额
        BigDecimal varZjrYzjye = BigDecimal.ZERO;
        searchDao.setBaseSubjectNatureList(
            new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
        List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
            // 变量.结转日溢折价科目余额 = 科目余额
            varZjrYzjye = listSubjectBalanceEx.get(0).getBalance();
        }

        // 结转日公允价值变动科目余额
        BigDecimal varZjrGyjzbdye = BigDecimal.ZERO;
        searchDao.setBaseSubjectNatureList(new ArrayList<String>(
            Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue())));
        List<SubjectBalance> listSubjectBalanceGy = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDao, DictCodeList.余额方向_借方.getValue());
        if (listSubjectBalanceGy != null && listSubjectBalanceGy.size() > 0) {
            // 变量.结转日溢折价科目余额 = 科目余额
        	varZjrGyjzbdye = listSubjectBalanceGy.get(0).getBalance();
        }
        
        //变量.交易份额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varShareAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易金额 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varDealAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        //变量.交易本金 = 变量.成本科目数量 * 交易明细.单位净价
        BigDecimal varPrinAmt = ArithmeticUtil.multiply(varZjrCbsl, dealNow.getUnitCprice());
        
        // 定义 变量.冲减溢折价= 0
        BigDecimal scourSpilledPrice = BigDecimal.ZERO;
        // 定义 变量.冲减成本 = 0
        BigDecimal varCjcb = BigDecimal.ZERO;
        // 定义 变量.冲减数量 = 0
        BigDecimal scourQuantity = BigDecimal.ZERO;
        // 定义 变量.冲减公允价值变动
        BigDecimal varCjgyjzbd = BigDecimal.ZERO;
        // 定义 变量.买卖价差
        BigDecimal saleDiffer = BigDecimal.ZERO;
      
        
        // 交易方向 = "到期"
        if (DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
            // 变量.冲减公允价值变动 = 变量.结转日溢折价科目余额
            varCjgyjzbd = varZjrGyjzbdye;
            // 变量.冲减溢折价 = 变量.结转日溢折价科目余额
            scourSpilledPrice = varZjrYzjye;
            // 变量.冲减成本 = 变量.结转日成本科目余额
            varCjcb = varZjrCbye;
            // 变量.冲减数量 = 变量.结转日成本数量
            scourQuantity = varZjrCbsl;
        }
        else {
        	// 变量.冲减公允价值变动= 变量.结转日公允价值变动科目余额 * 变量.交易份额/变量.成本科目数量																			
        	varCjgyjzbd = ArithmeticUtil.round(ArithmeticUtil.divide(
                ArithmeticUtil.multiply(varZjrGyjzbdye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
        	// 变量.冲减溢折价= 变量.结转日溢折价科目余额 * 变量.交易份额/变量.成本科目数量																	
            scourSpilledPrice = ArithmeticUtil.round(ArithmeticUtil.divide(
                ArithmeticUtil.multiply(varZjrYzjye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
            // 变量.冲减成本 = 变量.结转日成本科目余额 * 变量.交易份额/变量.成本科目数量	
            varCjcb = ArithmeticUtil.round(ArithmeticUtil.divide(
                ArithmeticUtil.multiply(varZjrCbye, varShareAmt),
                varZjrCbsl, NumberEnum.MONEY.getCode()), NumberEnum.MONEY);
            
            // 变量.冲减数量 <- 变量.交易份额
            scourQuantity = ArithmeticUtil.round(varShareAmt, NumberEnum.MONEY);
        }

        // 变量.买卖价差 = 变量.交易本金 - （变量.冲减成本+变量.冲减溢折价） 
        saleDiffer = ArithmeticUtil.round(ArithmeticUtil.doSub(varPrinAmt, varCjcb,
            scourSpilledPrice), NumberEnum.MONEY);

        // 交易金额 <- 变量.交易金额
        accountAmountMap.put(BookConstant.OTHER_COST,
            ArithmeticUtil.round(varDealAmt, NumberEnum.MONEY));
        
        // 面值 <- 变量.冲减成本
        accountAmountMap.put(BookConstant.COST,
            ArithmeticUtil.round(varCjcb, NumberEnum.MONEY));

        // if（变量.买卖价差>= 0）
        if (saleDiffer.compareTo(BigDecimal.ZERO) >= 0) {
            // 买卖价差（贷）<- 变量.买卖价差
            accountAmountMap.put(BookConstant.INCOME_C, saleDiffer);
        }
        else {
            // 买卖价差（借）= 变量.买卖价差*-1
            accountAmountMap.put(BookConstant.INCOME_D,
                ArithmeticUtil.round(
                    ArithmeticUtil.multiply(saleDiffer, new BigDecimal("-1")),
                    NumberEnum.MONEY));
        }
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

        // 变量.冲减公允价值变动 >= 0
        if (varCjgyjzbd.compareTo(BigDecimal.ZERO) >= 0) {
            // 公允价值变动(贷) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C,
                ArithmeticUtil.round(varCjgyjzbd, NumberEnum.MONEY));
            // 冲减公允价值变动(借) <-变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D,
                ArithmeticUtil.round(varCjgyjzbd, NumberEnum.MONEY));
        }
        else {
            // 公允价值变动(借) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(varCjgyjzbd, new BigDecimal(-1)), NumberEnum.MONEY));
            // 冲减公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(varCjgyjzbd, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(scourQuantity, NumberEnum.QTY));
        
        
     // 定义 变量.其他应收款-本金科目余额 = 变量.其他应收款科目余额MAP.get(交易明细.分支序号)
//        if(param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch()))==null){
//            searchDao = new SubjectBalanceSearchDto();
//            searchDao.setBooksetId(acctEntity.getBooksetId());
//            searchDao.setBokDetailId(acctEntity.getBokDetailId());
//            searchDao.setHappenDate(acctEntity.getHappenDate());
//            searchDao.setBookDate(acctEntity.getBookDate());
//            searchDao.setTaskPri(dealNow.getTaskPri());
//            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
//                Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue())));
//            searchDao.setBranch(dealNow.getBranch());
//            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
//                searchDao, DictCodeList.余额方向_借方.getValue());
//            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
//                param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()), listSubjectBalance.get(0).getBalance());
//            }
//        }
        
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        if(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue().equals(offsetImptDateType)
        		&& BigDecimal.ZERO.compareTo(param.getVarJzzbSubjectAmt()) != 0){
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                DictCodeListExt1.现金流一级类型_本金.getValue(),
                // 发生金额 = 变量.冲减成本
                varCjcb,
                // 减值准备余额
                param.getVarJzzbSubjectAmt(),
                // 当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.溢折价科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月
                // + 变量.逾期利息汇总余额 - 变量.其他应收款当前发生余额
                ArithmeticUtil.doSub(
	                ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(), param.getVarLxSubjectAmt(),
	                    param.getVarYzjSubjectAmt(), param.getVarQtyskBjSubjectSumAmt(),
	                    param.getVarQtyskLxSubjectSumAmt(), param.getVarYqfxSubjectSumAmt()),
	                param.getGlb_tdyqtyskAmtSum()
                ),
                // 应收款余额 = 发生金额
                varCjcb,
                varZjrCbsl);
            //溢折价科目
            dto.setVarYzjSubjectAmt(scourSpilledPrice);
            
        	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
        }
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
            ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        
        // 定义 本币金额元素Map
        Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
        for(String key : accountAmountMap.keySet()){
        	bccyAmountMap.put(key + BookConstant.BCCYAMT, 
        	    calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
        }
        accountAmountMap.putAll(bccyAmountMap);
        
        //定义 变量.买卖价差_本位币
        BigDecimal saleDifferBccy = BigDecimal.ZERO;
        if (scourSpilledPrice.compareTo(BigDecimal.ZERO) >= 0) {
            //变量.买卖价差_本位币 = 	交易金额_本位币 - (面值_本位币 + 溢折价(贷)_本位币)
        	saleDifferBccy = ArithmeticUtil.round(ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
        						ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT), 
    									accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT))),NumberEnum.MONEY);
        }
        else {
        	// 	变量.买卖价差_本位币 = 	交易金额_本位币 + 溢折价(借)_本位币 - 面值_本位币
        	saleDifferBccy = ArithmeticUtil.round(ArithmeticUtil.doSub(
        				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT), 
							accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)),
        						accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)),NumberEnum.MONEY);
        }
        if(saleDifferBccy.compareTo(BigDecimal.ZERO) >= 0) {
        	//买卖价差（贷）_本位币   <- 变量.买卖价差_本位币						
        	accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy, NumberEnum.MONEY.getCode(), null));
        }
        else {
        	//买卖价差（借）_本位币	<-	变量.买卖价差_本位币 * -1							
        	accountAmountMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT, 
        			ArithmeticUtil.round(saleDifferBccy.negate(), NumberEnum.MONEY.getCode(), null));
        }
        
        
        
        // accountCurrencyMap设置如下：
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());
        }
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        
        
        
        /**add by wujie on 20201111 start remark:减值准备科目进行余额更新**/
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /**add by wujie on 20201111 end remark:减值准备科目进行余额更新**/
        
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
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
            accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);

        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        varCjcb = ArithmeticUtil.round(varCjcb, NumberEnum.MONEY);
        scourQuantity = ArithmeticUtil.round(scourQuantity, NumberEnum.QTY);
        scourSpilledPrice = ArithmeticUtil.round(scourSpilledPrice, NumberEnum.MONEY);
        varCjgyjzbd = ArithmeticUtil.round(varCjgyjzbd, NumberEnum.MONEY);
        // 全局变量的更新
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), varCjcb));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), scourQuantity));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), scourSpilledPrice));
        // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), varCjgyjzbd));
        
        //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中
        param.getVarQtyskxBJSubjectAmt().put(String.valueOf(dealNow.getBranch()),
        		ArithmeticUtil.doAdd(param.getVarQtyskxBJSubjectAmt().get(String.valueOf(dealNow.getBranch())),varCjcb));
        
        //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 + 变量.冲减成本
        param.setVarQtyskBjSubjectSumAmt(ArithmeticUtil.doAdd(param.getVarQtyskBjSubjectSumAmt(),varCjcb));
        //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
        param.setVarJzzbSubjectAmt(ArithmeticUtil.doSub(param.getVarJzzbSubjectAmt(),冲抵减值金额));
        
        //变量.减值准备科目余额_本位币 = 变量.减值准备科目余额 + 金额元素.减值金额_本位币
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        
        /**add by wujie on 20201124 start remar:资产减值**/
       //变量.其他应收款当前发生余额 = 变量.其他应收款当前发生余额 + 变量.冲减成本
       param.setGlb_tdyqtyskAmtSum(ArithmeticUtil.doAdd(param.getGlb_tdyqtyskAmtSum(),varCjcb));
       /**add by wujie on 20201124 end remar:资产减值**/
       
        if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
        	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
        	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
    		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
    			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())) {
	    			valBokBookdetailBatchResult.add(dto);
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
        // 项目类资产不走其他债权分录，是否其他债权设置为否
        rstBaseinfo.setIsRight(DictCodeList.是否_否.getValue());
        return rstBaseinfo;
    }
    
    
    /**
     * 科目余额汇总
     * add by wujie on 20201030
     */
    private BigDecimal getSubjectBalanceSum(SubjectBalanceSearchDto searchDao,String expBalanceDirect) {
        Map<String, BigDecimal> map = accountingCommonService
            .getSubjectBalanceCollection(searchDao, DictCodeList.余额方向_借方.getValue(),null);
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
    		BigDecimal receAmt, //应收款项余额
    		BigDecimal totalMz //当前总面值
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
    	//当前总面值
        dto.setTotalMz(totalMz);
        //业务类型
        dto.setBusiType(dealNow.getFinprodType());
    	return dto;
    }
}
