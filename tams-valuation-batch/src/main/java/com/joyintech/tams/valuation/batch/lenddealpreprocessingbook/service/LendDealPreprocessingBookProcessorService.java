/*
 * 文件名：LendDealPreprocessingBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
package com.joyintech.tams.valuation.batch.lenddealpreprocessingbook.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
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
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;

/**
 * 拆借预交易账务处理 process逻辑处理
 * 
 * @author chengzhong
 * @version 1.0 2018年6月9日
 * @see LendDealPreprocessingBookProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class LendDealPreprocessingBookProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    
    /**
     * 共通方法service
     */
    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    
    /**
     * 共通方法service
     */
    @Autowired
    private LossCommonService lossCommonService;
    
    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        //获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(GlobalConstant.READER_RESULT);
        
        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义此process的返回变量
        BookGenerateResult rtnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (rtnResult.getValBokBookdetailBatch() == null) {
            rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景

        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 交易基础信息列表

        /**
         * add by wujie on 20201030 start
         */
        // 变量.成本科目余额
        BigDecimal glb_subjectAmt = BigDecimal.ZERO;
        // 变量.应计利息科目余额
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;
        //变量.减值准备科目余额
        BigDecimal glb_jzzbBlance = BigDecimal.ZERO;
        //变量.减值准备科目余额_本位币
        BigDecimal glb_jzzbBlance_bccy = BigDecimal.ZERO;
        //变量.其他应收款本金汇总余额
        BigDecimal glb_qtyskbjsum = BigDecimal.ZERO;
        //变量.其他应收款利息汇总余额
        BigDecimal glb_qtysklxsum = BigDecimal.ZERO;
        //变量.变量.逾期利息汇总余额
        BigDecimal glb_qilxsum = BigDecimal.ZERO;
        //是否冲抵资产减值
        boolean OFFSETIMPT = false;
        //变量.其他应收款-分支序号
        Map<Long,BigDecimal> glb_qtyskMap = new HashMap<>();
        
        /**add by wujie on 20201124 start remar:资产减值**/
        BigDecimal glb_tdyqtyskAmtSum = BigDecimal.ZERO;
        /**add by wujie on 20201124 end remar:资产减值**/
        
        //账套核算参数查询-调用账务类共通
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), paramCodeList);
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        if(!CollectionUtils.isEmpty(valBokBooksetExt)){
        	offsetImptDateType = valBokBooksetExt.get(0).getParamValue();
        }
        
        //2.查询科目余额
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(acctEntity.getHappenDate());
        searchDao.setBookDate(acctEntity.getBookDate());
        List<SubjectBalance> listSubjectBalance = null;
        
        if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(listFinProductDealInfoComDTONow.get(0).getFinprodType())
        		&& DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue().equals(offsetImptDateType)){
        	//2.1. 成本科目余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, DictCodeList.余额方向_借方.getValue());
            if(listSubjectBalance!=null && listSubjectBalance.size()>0){
            	glb_subjectAmt = listSubjectBalance.get(0).getBalance();
            }
            //2.2.应计利息科目余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, DictCodeList.余额方向_借方.getValue());
            if(listSubjectBalance!=null && listSubjectBalance.size()>0){
            	glb_subjectProfit = listSubjectBalance.get(0).getBalance();
            }
            //2.3.减值准备科目余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, DictCodeList.余额方向_借方.getValue());
            if(listSubjectBalance!=null && listSubjectBalance.size()>0){
            	glb_jzzbBlance = listSubjectBalance.get(0).getBalance();
            	glb_jzzbBlance_bccy = listSubjectBalance.get(0).getbAmt();
            }
            //2.4.其他应收款本金余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            glb_qtyskbjsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            //2.5.其他应收款利息余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            glb_qtysklxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            //2.6. 逾期利息余额查询
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
            glb_qilxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            OFFSETIMPT = true;
            
        }
        /**add by wujie on 20201030 end**/
        
        
        // 循环 交易基础信息列表 进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {
            //交易明细.金融产品类型 != “拆出”
            if (!DictCodeListExt1.金融产品类型_拆出.getValue().equals(dealDto.getFinprodType())&&!DictCodeListExt1.金融产品类型_同业借款.getValue().equals(dealDto.getFinprodType())) {
                continue;
            }
            
            // 变量 - 交易信息（标的交易信息）
            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dealDto.getFinprodId(),dealDto.getFinprodType(),dealDto.getBranch());
            // 保证获取的标的交易信息不为空
            if (dealInfoForTarget == null) {
                dealInfoForTarget = new TradeInfoDto();
            }
            
            /**add by wujie on 20201030 start remark:资产减值**/
        	// 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(dealDto.getCcy()) && !dealDto.getCcy().equals(acctEntity.getStandardCcy())){
                // 调用共通汇率查询
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                        acctEntity.getStandardCcy(), dealDto.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
            }
            /**add by wujie on 20201030 end**/

            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
            // 是否累计余额->是
            accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());
            
            // 根据交易方向，设置不同的记账元素
            if (DictCodeList.交易方向_收息.getValue().equals(dealDto.getPs())) {
                // ad.会计场景代码="拆放同业（同业借款）-中间收息"
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_中间收息.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业(同业借款)中间付息交易");// 备注
                
                //(1) 查询交易的到期日对应的应计利息
                BigDecimal varIntAmt = BigDecimal.ZERO;
                searchDao = new SubjectBalanceSearchDto();
                searchDao.setBooksetId(acctEntity.getBooksetId());
                searchDao.setBokDetailId(acctEntity.getBokDetailId());
                searchDao.setHappenDate(DateUtil.addDay(dealDto.getMdate(), -1));
                searchDao.setBookDate(acctEntity.getBookDate());
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrue.应计利息.getValue())));
                listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
                if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                    // 变量.应计利息                  =   上述共通返回的科目余额
                    varIntAmt = listSubjectBalance.get(0).getBalance();
                }
                
                //交易金额  <- 变量.应计利息
                accountAmountMap.put(BookConstant.INTAMT,
                    ArithmeticUtil.round(varIntAmt, NumberEnum.MONEY));
                
                /**add by wujie on 20201030 start remark:资产减值**/
                // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = glb_qtyskMap.get(dealDto.getBranch());
                if(qtyskxSubjectAmt==null){
                    searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(acctEntity.getBooksetId());
                    searchDao.setBokDetailId(acctEntity.getBokDetailId());
                    searchDao.setHappenDate(acctEntity.getHappenDate());
                    searchDao.setBookDate(acctEntity.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                        glb_qtyskMap.put(dealDto.getBranch(), qtyskxSubjectAmt);
                    }
                }
                
                BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_利息.getValue(), 
                			//发生金额
                			varIntAmt, 
                			//减值准备余额
                			glb_jzzbBlance, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额 -  变量.其他应收款当前发生余额
                			ArithmeticUtil.doSub(
	                			ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum),
	                			glb_tdyqtyskAmtSum
                			), 
                			//应收款余额= 发生金额
                			varIntAmt,null);
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
                
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201030 end**/
                
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                /**add by wujie on 20201111 start remark:减值准备科目进行余额更新**/
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, glb_jzzbBlance);//科目余额_本次发生前
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, glb_jzzbBlance_bccy);//科目本位币余额_本次发生前
                accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
                /**add by wujie on 20201111 end remark:减值准备科目进行余额更新**/
                
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                /**add by wujie on 20201030 start**/
                //全局变量更新
                //变量.应计利息科目余额  =	变量.应计利息科目余额 - 变量.应计利息											
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit,varIntAmt);
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 + 变量.应计利息
                glb_qtysklxsum = ArithmeticUtil.doAdd(glb_qtysklxsum,varIntAmt);
                //变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.冲减减值金额
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance,冲抵减值金额);
                //变量.其他应收款科目余额更新Map
                glb_qtyskMap.put(dealDto.getBranch(), ArithmeticUtil.doAdd(qtyskxSubjectAmt,varIntAmt));
                
                //变量.减值准备科目余额_本位币 = 变量.减值准备科目余额 + 金额元素.减值金额_本位币
                glb_jzzbBlance_bccy = ArithmeticUtil.doAdd(glb_jzzbBlance_bccy,accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT));
                
                /**add by wujie on 20201124 start remar:资产减值**/
                //变量.其他应收款当前发生余额 = 变量.其他应收款当前发生余额 + 变量.应计利息
                glb_tdyqtyskAmtSum = ArithmeticUtil.doAdd(glb_tdyqtyskAmtSum,varIntAmt);
                /**add by wujie on 20201124 end remar:资产减值**/
                
                if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
                	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
                	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
                	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
                				accountAmountMap.get(BookConstant.IMPAMT), 
                				accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT), 
                				glb_jzzbBlance, 
                				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
                						glb_jzzbBlance, changeRate)
                				,dealDto.getTradeId());
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                /**add by wujie on 20201030 end**/

            }
            else if(DictCodeList.交易方向_还本.getValue().equals(dealDto.getPs()) || DictCodeList.交易方向_到期.getValue().equals(dealDto.getPs())){
                //ad.会计场景代码="拆放同业-到期"
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_到期.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业(同业借款)还本到期交易");// 备注
                
                //调用公用方法【科目余额查询-期待方向】
                searchDao = new SubjectBalanceSearchDto();
                searchDao.setBooksetId(acctEntity.getBooksetId());
                searchDao.setBokDetailId(acctEntity.getBokDetailId());
                searchDao.setHappenDate(DateUtil.addDay(dealDto.getMdate(), -1));
                searchDao.setBookDate(acctEntity.getBookDate());
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.成本.getValue())));
                listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, DictCodeList.余额方向_借方.getValue());
                //定义 变量.结转日成本科目余额 = 科目余额
                BigDecimal jzrkmye = BigDecimal.ZERO;
                //定义 变量.结转日成本科目数量 = 数量
                BigDecimal jzrkmsl = BigDecimal.ZERO;
                
                if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                    //变量.结转日成本科目余额 = 科目余额
                    jzrkmye = listSubjectBalance.get(0).getBalance();
                    //变量.结转日成本科目数量 = 数量
                    jzrkmsl = listSubjectBalance.get(0).getNum();
                }
                
                //变量.交易份额 = 变量.成本科目数量 * 交易明细.单位净价
                BigDecimal jyFe = ArithmeticUtil.multiply(jzrkmsl,dealDto.getUnitCprice());
                //变量.冲减成本 = 变量.结转日成本科目余额 * 变量.交易份额/变量.成本科目数量
                BigDecimal cjCb = ArithmeticUtil.divide(ArithmeticUtil.multiply(jzrkmye,jyFe),jzrkmsl);
                accountSceneMap.put(BookConstant.HAPPEN_NUMBER, jyFe);
                //本金金额 = 变量.冲减成本
                accountAmountMap.put(BookConstant.COST, ArithmeticUtil.round(cjCb, NumberEnum.MONEY));
                
                
                /**add by wujie on 20201030 start**/
                BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
                // 定义 变量.其他应收款-本金科目余额 = 变量.其他应收款科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = glb_qtyskMap.get(dealDto.getBranch());
                if(qtyskxSubjectAmt==null){
                    searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(acctEntity.getBooksetId());
                    searchDao.setBokDetailId(acctEntity.getBokDetailId());
                    searchDao.setHappenDate(acctEntity.getHappenDate());
                    searchDao.setBookDate(acctEntity.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                        glb_qtyskMap.put(dealDto.getBranch(), qtyskxSubjectAmt);
                    }
                }
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_本金.getValue(), 
                			//发生金额
                			cjCb, 
                			//减值准备余额
                			glb_jzzbBlance, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额- 变量.其他应收款当前发生余额
                			ArithmeticUtil.doSub(
                					ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum),
                					glb_tdyqtyskAmtSum
                			), 
                			//应收款余额 = 发生金额
                			cjCb,jzrkmsl);
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
                
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201030 end**/
                
                
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                
                /**add by wujie on 20201111 start remark:减值准备科目进行余额更新**/
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, glb_jzzbBlance);//科目余额_本次发生前
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
                accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, glb_jzzbBlance_bccy);//科目本位币余额_本次发生前
                accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
                /**add by wujie on 20201111 end remark:减值准备科目进行余额更新**/

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                
                /**add by wujie on 20201030 start remark：资产减值**/
                //全局变量更新
                //变量.成本科目余额  =	变量.成本科目余额 - 变量.冲减成本									
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt,cjCb);
                //变量.其他应收款本金汇总余额  = 变量.其他应收款本金汇总余额  + 变量.冲减成本
                glb_qtyskbjsum = ArithmeticUtil.doAdd(glb_qtyskbjsum,cjCb);
                //变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.冲减减值金额
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance,冲抵减值金额);
                //变量.其他应收款科目余额更新Map
                glb_qtyskMap.put(dealDto.getBranch(), ArithmeticUtil.doAdd(qtyskxSubjectAmt,cjCb));
                
                //变量.减值准备科目余额_本位币 = 变量.减值准备科目余额 + 金额元素.减值金额_本位币
                glb_jzzbBlance_bccy = ArithmeticUtil.doAdd(glb_jzzbBlance_bccy,accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT));
                
                
                /**add by wujie on 20201124 start remar:资产减值**/
                //变量.其他应收款当前发生余额 = 变量.其他应收款当前发生余额 + 变量.冲减成本
                glb_tdyqtyskAmtSum = ArithmeticUtil.doAdd(glb_tdyqtyskAmtSum,cjCb);
                /**add by wujie on 20201124 end remar:资产减值**/
                
                if(BigDecimal.ZERO.compareTo(冲抵减值金额)!=0){
                	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
                	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
                	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto, 
                				accountAmountMap.get(BookConstant.IMPAMT), 
                				accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT), 
                				glb_jzzbBlance, 
                				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, 
                						glb_jzzbBlance, changeRate)
                				,dealDto.getTradeId());
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                /**add by wujie on 20201030 end**/
            }
        }

        return rtnResult;
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
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     * 
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
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
