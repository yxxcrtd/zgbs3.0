/*
 * 文件名：LendBookDetailProcessDealService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
package com.joyintech.tams.valuation.batch.lendbookdetail.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.*;
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
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 拆借交易账务处理 process逻辑处理
 * 
 * @author yangjian
 * @version 1.0 2018年6月9日
 * @see LendBookDetailProcessDealService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class LendBookDetailProcessDealService {

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
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    /**
     * 减值准备共通方法
     */
    @Autowired
    private LossCommonService lossCommonService;
    
    /**
     * 共通方法service
     */
    @Autowired
    private LossCommonBatchService lossCommonBatchService;

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
        
        /**add by wujie on 20201020 start remark:资产减值**/
        if (rtnResult.getBokStatLossPosiitonList() == null) {
        	rtnResult.setBokStatLossPosiitonList(new ArrayList<BokStatLossPosiiton>());
        }
        /**add by wujie on 20201020 end**/

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        // String invAimNow = acctEntity.getInvAim();// 投资目的
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 交易基础信息列表
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();//任务优先级（交易基础信息列表的任务优先级都是一致的，故取第一个）
        String finprodTypeNow = listFinProductDealInfoComDTONow.get(0).getFinprodType();//金融产品类型（取第一个）

        // 定义变量
        BigDecimal glb_subjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal glb_subjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应计利息科目余额
        Map<String, BigDecimal> varQtyskxSubjectAmt = new HashMap<>();

        //获取期待余额方向（case 拆入 then 贷方 else 借方）
        String nowDcForCb = DictCodeList.余额方向_借方.getValue();
        if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(finprodTypeNow)){
            nowDcForCb = DictCodeList.余额方向_贷方.getValue();
        }
        // 调用公用方法：（科目余额查询 - 期待方向）,获取成本类科目余额
        SubjectBalance subjectBalanceForAmt = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
            happenDateNow, bookDateNow, nowDcForCb,
            DictCodeBaseSubjectNatrue.成本.getValue(), taskPri);
        if (subjectBalanceForAmt != null) {
            glb_subjectAmt = subjectBalanceForAmt.getBalance() == null ? 
                BigDecimal.ZERO : subjectBalanceForAmt.getBalance();// 赋值.成本科目余额
            glb_subjectNum = subjectBalanceForAmt.getNum() == null ? 
                BigDecimal.ZERO : subjectBalanceForAmt.getNum();// 赋值.成本科目数量
        }

        // 默认 科目性质设为 应计利息
        String baseSubjectNatureForProfit = DictCodeBaseSubjectNatrue.应计利息.getValue();
        //获取期待余额方向（case 拆入 then 贷方 else 借方）
        String nowDcForXl = DictCodeList.余额方向_借方.getValue();
        if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(finprodTypeNow)){
            nowDcForXl = DictCodeList.余额方向_贷方.getValue();
        }
        // 调用公用方法：（科目余额查询 - 期待方向）,获取应计利息类科目余额
        SubjectBalance subjectBalanceForProfit = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow, nowDcForXl, baseSubjectNatureForProfit,taskPri);
        if (subjectBalanceForProfit != null) {
            glb_subjectProfit = subjectBalanceForProfit.getBalance() == null ? 
                BigDecimal.ZERO : subjectBalanceForProfit.getBalance();// 赋值.应计利息科目余额
        }
        
        /**add by wujie on 20201020 start remark:资产减值**/
        BigDecimal glb_jzzbBlance = BigDecimal.ZERO;//变量.减值准备科目余额
        if (DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finprodTypeNow)) {
        	SubjectBalance balanceDto = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                    happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue(), taskPri);
        	if(balanceDto!=null){
        		// 变量.减值准备科目余额 = DTO.科目余额
            	glb_jzzbBlance = balanceDto.getBalance();
        	}
         }
        
        //定义 变量.其他应收款本金
        Map<String, BigDecimal> varQtyskxBJSubjectAmt = new HashMap<>();
        //定义 变量.逾期资产应计利息
        Map<String, BigDecimal> varYqfxSubjiectAmt = new HashMap<>();
        
        TargetFinProduct targetFinProduct = null;
         /**add by wujie on 20201020 end**/
        
        /**add by wujie on 20201030 start remark:还本收息场景增加资产减值处理逻辑**/
        
        //变量.其他应收款本金汇总余额
        BigDecimal glb_qtyskbjsum = BigDecimal.ZERO;
        //变量.其他应收款利息汇总余额
        BigDecimal glb_qtysklxsum = BigDecimal.ZERO;
        //变量.变量.逾期利息汇总余额
        BigDecimal glb_qilxsum = BigDecimal.ZERO;
        
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), 
        		paramCodeList);
        //定义 变量.还本收息冲抵减值准备处理日 = 返回值
        String offsetImptDateType  = null;
        if(!CollectionUtils.isEmpty(valBokBooksetExt)){
        	offsetImptDateType = valBokBooksetExt.get(0).getParamValue();
        }
        //还本到账收息到账是否冲抵资产减值
        boolean OFFSETIMPT = false;
        if(DictCodeListExt1.金融产品类型_同业借款.getValue().equals(listFinProductDealInfoComDTONow.get(0).getFinprodType())
        		&& DictCodeListExt3.还本收息冲抵减值准备处理日_资金到账日.getValue().equals(offsetImptDateType)){
        	OFFSETIMPT = true;
        	
        	SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(acctEntity.getBooksetId());
            searchDao.setBokDetailId(acctEntity.getBokDetailId());
            searchDao.setHappenDate(acctEntity.getHappenDate());
            searchDao.setBookDate(acctEntity.getBookDate());
            
        	//2.5.其他应收款本金余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
            glb_qtyskbjsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            //2.6.其他应收款利息余额
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
            glb_qtysklxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            //2.7. 逾期利息余额查询
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
            glb_qilxsum = this.getSubjectBalanceSum(searchDao, DictCodeList.余额方向_借方.getValue());
            
            OFFSETIMPT = true;
        	
        }
        /**add by wujie on 20201030 end**/
        // 循环 交易基础信息列表 进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {
        	/**add by wujie on 20201021 start remark:资产减值:多币种改造**/
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
            /**add by wujie on 20201020 end**/
           
        	// 定义变量：分类1
            String type1 = "";
            // 定义变量：分类2
            String type2 = "";
            /** 增加金融产品类型、境内外 by yanya on 20200117 start**/
            String finprodType = "";
            String blnArea = "";
            /** 增加金融产品类型、境内外 by yanya on 20200117 end**/
            // 变量 - 交易信息（标的交易信息）
            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dealDto.getFinprodId(),dealDto.getFinprodType(),dealDto.getBranch());

            if(dealInfoForTarget != null){
            	type1 = dealInfoForTarget.getType1();// 分类1 - 变量.交易信息.分类1
            	type2 = dealInfoForTarget.getType2();// 分类2 - 变量.交易信息.分类2
                /** 增加金融产品类型、境内外 by yanya on 20200117 start**/
                finprodType = dealDto.getFinprodType();
                /** 增加金融产品类型、境内外 by yanya on 20200117 end**/
            }
            // 上述查询结果为空时，调用标的信息查询。
            else{
            	// 调用共通设计->【详细设计-标的类共通方法设计.xlsx】->标的信息查询SHEET
            	targetFinProduct = this.getTargetFinProductInfo(dealDto.getFinprodId());
            	if(targetFinProduct!=null){
	            	type1 = targetFinProduct.getType1();// 分类1 - 标的信息.分类1
	            	type2 = targetFinProduct.getType2();// 分类2 - 标的信息.分类2
                    /** 增加金融产品类型、境内外 by yanya on 20200117 start**/
                    finprodType = targetFinProduct.getFinprodType();
                    blnArea = targetFinProduct.getBlnArea();
                    /** 增加金融产品类型、境内外 by yanya on 20200117 end**/
            	}
            }
            
            
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
            //accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借交易登账");// 备注
            accountSceneMap.put(BookConstant.TYPE_1, type1);// 分类1 
            accountSceneMap.put(BookConstant.TYPE_2, type2);// 分类2

            // 根据交易方向，设置不同的记账元素
            if (DictCodeList.交易方向_买入.getValue().equals(dealDto.getPs())) {// 交易方向 - 买入
                   
                // 根据金融产品类型，判断当前分录场景是【拆放同业（拆入）】还是【拆放同业（同业借款）】
                if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealDto.getFinprodType())) {// 分录场景为【拆放同业（拆入）】
                    ad.setTableId(DictCodeListTableId.会计场景代码_拆入_起息.getValue());// 会计场景代码 = "拆入起息";

                } // 分录场景为【拆放同业（同业借款）】，即拆出
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_起息_转仓买入.getValue());// 会计场景代码 = "起息";
                }
                //定义 变量.应计利息 = 交易明细.利息总额
                BigDecimal intamt = dealDto.getIntAmt();
                //定义 变量.面值 = 交易明细.交易本金
                BigDecimal prinamt = dealDto.getPrinAmt();
                /**add by wujie on 20201020 start remark:资产减值**/
                BigDecimal 减值准备 = BigDecimal.ZERO;
                if (DictCodeListExt1.金融产品类型_同业借款.getValue().equals(finprodType)
                		&& DictCodeListExt3.阶段_买入后出现减值_P3.getValue().equals(dealDto.getLossStage())) {
                	
                	//账套核算参数查询-调用账务类共通
                    paramCodeList = new ArrayList<>();
                    paramCodeList.add(ValDictCodeList.账套配置表参数项_拆借计提方式.getValue());
                    valBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(), 
                    		paramCodeList);
                    //定义 变量.计提方式 = 返回值
                    String accrType = null;
                    if(!CollectionUtils.isEmpty(valBokBooksetExt)){
                    	accrType = valBokBooksetExt.get(0).getParamValue();
                    }
                    intamt = lossCommonService.calCommonIntAmt(targetFinProduct, dealDto.getShareAmt(), 
                			accrType, dealDto.getTradeDate());
                    intamt = ArithmeticUtil.round(intamt, NumberEnum.MONEY);
                    //变量.面值	=	交易明细.交易份额					
                    prinamt = dealDto.getShareAmt();
                    //变量.减值准备	=		交易明细.交易金额 - (变量.面值 + 变量.应计利息)														
                    减值准备 = ArithmeticUtil.doSub(dealDto.getTradeAmt(),prinamt,intamt);
                    减值准备 = ArithmeticUtil.round(减值准备, NumberEnum.MONEY);
                }
                //减值准备	<- 变量.减值准备 *-1				
                accountAmountMap.put(BookConstant.IMPAMT,ArithmeticUtil.round(减值准备.negate(),NumberEnum.MONEY));
                
                /**add by wujie on 20201020 end**/
                
                // 记账金额要素 map塞值
                accountAmountMap.put("ACCQUA", dealDto.getPrinAmt());// 核算数量 - 交易明细.交易本金
                accountAmountMap.put("COST", prinamt);// 本金金额 - 交易明细.交易本金
                accountAmountMap.put("INTAMT", intamt);// 应计利息 -> 变量.应计利息
                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 本息和 - 交易明细.交易金额
                
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借交易登账");
                
                
                /**add by wujie on 20201020 start remark:资产减值：多币种改造**/
                
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                // 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 + 减值准备_本位币
                BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                    				, accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT));
                
                //本息和_本位币<-变量.本息和_本位币		
                accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
                		ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201020 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                
                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 + 变量.面值
                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, prinamt);
                // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 + 变量.应计利息
                glb_subjectProfit = ArithmeticUtil.doAdd(glb_subjectProfit, intamt);
                
                /**add by wujie on 20201019 start remark:资产减值**/
                //变量.减值准备科目余额  =	变量.减值准备科目余额 + 变量.减值准备											
                glb_jzzbBlance = ArithmeticUtil.doAdd(glb_jzzbBlance,减值准备);
                
                if(减值准备.compareTo(BigDecimal.ZERO) != 0){
                	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
                	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
                	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoAccr(valBokBookdetailBatchDto, 
                				accountAmountMap.get(BookConstant.IMPAMT).negate(), 
                				accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT).negate(), 
                				glb_jzzbBlance, 
                				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,glb_jzzbBlance, changeRate),
                				dealDto.getTradeId(),
                				null);
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                /**add by wujie on 20201019 end**/

            } // 交易方向 = "卖出"
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealDto.getPs())) {
            	
               	// 分录初始值已在上面设置过。
                
            	// 会计场景代码 = "会计场景代码_拆放同业_转仓卖出";
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_转仓卖出.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业-转仓卖出");
                /** modify by yanya on 20200106 实付通道费修改 start**/
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealDto.getValTradeFeeDetailList(),
                        dealDto.getProdId(), dealDto.getPortfolioId(), dealDto.getFinprodId());
                //通道费账务明细代码	<-	变量.交易各种费用.通道费账务明细代码
                //通道费业务明细代码	<-	变量.交易各种费用.通道费业务明细代码
                BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, chlFeeCode);

                // 实付通道费 <-变量.交易各种费用.通道费
                accountAmountMap.put(BookConstant.REAL_CHLAMT,
                        ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealDto.getCcy());
                /** modify by yanya on 20200106 实付通道费修改 end**/
                
                // 定义变量.冲减应计利息
                BigDecimal cjProfit = BigDecimal.ZERO;
                //变量.成本科目余额 = 交易明细.交易本金
                if(glb_subjectAmt.compareTo(dealDto.getPrinAmt()) == 0){
                    //变量.冲减应计利息 = 变量.应计利息科目余额 
                    cjProfit = glb_subjectProfit;
                }
                else {
                    //变量.冲减应计利息 = 变量.应计利息科目余额 * 交易明细.交易本金/变量.成本科目数量
                    cjProfit = ArithmeticUtil.divide(ArithmeticUtil.multiply(glb_subjectProfit, dealDto.getPrinAmt()), glb_subjectNum);
                }
                 
                // 定义变量.冲减应计利息 - 调用金额共通方法设置有效小数位
                cjProfit = ArithmeticUtil.round(cjProfit, NumberEnum.MONEY);
                
                // 变量.冲减成本 = 变量.成本科目余额 * 交易明细.交易本金/变量.成本科目数量
                BigDecimal cjAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(glb_subjectAmt, dealDto.getPrinAmt()), glb_subjectNum);
                // 变量.冲减成本  - 调用金额共通方法设置有效小数位
                cjAmt = ArithmeticUtil.round(cjAmt, NumberEnum.MONEY);
                
                // 变量.冲减数量 = 交易明细.交易本金
                BigDecimal cjNum = dealDto.getPrinAmt();
                
                /**add by wujie on 20201020 start remark:资产减值**/
                //定义  变量.冲减减值准备 =	变量.减值准备科目余额 * 交易明细.交易份额/变量.成本科目数量																	
//                BigDecimal cjjzzb = ArithmeticUtil.divide(ArithmeticUtil.multiply(glb_jzzbBlance, dealDto.getShareAmt()), glb_subjectNum);
                BigDecimal cjjzzb = BigDecimal.ZERO;
                LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealDto, 
                		null, 
                		//本次发生额 = 变量.冲减应计利息 + 变量.冲减成本
                		ArithmeticUtil.doAdd(cjAmt,cjProfit), 
                		//减值准备余额
                		glb_jzzbBlance, 
                		//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                		ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                		//应收款项余额
                		null);
            	//发生份额
                inputdto.setHappenNum(dealDto.getPrinAmt());
            	//总份额
                inputdto.setTotalNum(glb_subjectNum);
                cjjzzb = lossCommonBatchService.calcLossAmtForDeal(inputdto);
                
                cjjzzb = ArithmeticUtil.round(cjjzzb, NumberEnum.MONEY);
                //减值使用					<-		变量.冲减减值准备						
                accountAmountMap.put(BookConstant.IMPAMT,cjjzzb.negate());
                
                /**add by wujie on 20201020 end**/

                // 记账金额要素 map塞值
                accountAmountMap.put("ACCQUA", cjAmt);// 金额要素:核算数量 <- 变量.冲减成本
                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());//  金额要素:本息和 <- 交易明细.交易金额
                accountAmountMap.put("COST", cjAmt);//  金额要素:本金金额 <- 变量.冲减成本
                accountAmountMap.put("INTAMT", cjProfit);//  金额要素:应计利息/应收利息 <- 变量.冲减应计利息

                /** modify by yanya on 20200106 实付通道费修改 start**/
                // 获取差值 = 交易明细.利息总额 - 变量.冲减应计利息 - 实付通道费
                //BigDecimal subResult = ArithmeticUtil.doSub(dealDto.getIntAmt(), cjProfit);
                BigDecimal subResult = ArithmeticUtil.doSub(dealDto.getIntAmt(), cjProfit, dealTypesFeeDto.getChlFee());
                /** modify by yanya on 20200106 实付通道费修改 end**/
                // 利息支出差额设置（借负，贷正）
                if (subResult.compareTo(BigDecimal.ZERO) > 0) {
                    accountAmountMap.put("INCOMEAMT_C", subResult.abs());//  金额要素:利息差额（贷）
                }
                else {
                    accountAmountMap.put("INCOMEAMT_D", subResult.abs());//  金额要素:利息差额（借）
                }
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                //定义 变量.本息和_本位币 
                BigDecimal dealAmtBccy = null;
                if (subResult.compareTo(BigDecimal.ZERO) > 0) {
                	// 变量.本息和_本位币 = 	本金金额_本位币 + 应收利息_本位币 + 应付通道费_本位币 + 利息差额(贷)_本位币
                	dealAmtBccy = ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)
            				, accountAmountMap.get(BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT));
                }
                else{
                	// 变量.本息和_本位币 = 本金金额_本位币 + 应收利息_本位币 + 应付通道费_本位币 - 利息差额(借方)_本位币
                	dealAmtBccy = ArithmeticUtil.doSub(
                			ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
	            				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
	            				, accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT))
            				, accountAmountMap.get(BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT));
                }
                //本息和_本位币<-变量.本息和_本位币		
                accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
                		ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, cjAmt);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, cjNum);
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, cjProfit);
                /**add by wujie on 20201020 start remark:减值准备**/
                //变量.减值准备科目余额	=	变量.减值准备科目余额 - 变量.减值使用												
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance, cjjzzb);
                if(cjjzzb.compareTo(BigDecimal.ZERO) != 0){
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
                /**add by wujie on 20201019 end**/
                
            } 
            //交易明细.金融产品类型 in （“拆入”，“拆出”） and 交易方向 = "到期" 
            else if(
            		(DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealDto.getFinprodType()) 
            		|| DictCodeListExt1.金融产品类型_拆出.getValue().equals(dealDto.getFinprodType()))
            		&& DictCodeList.交易方向_到期.getValue().equals(dealDto.getPs())){
            	
            	// 分录初始值已在上面设置过。
            	
                // 变量.利息金额元素 
                String lxJeYs = "";
                // 变量.利息差额（贷）金额元素
                String lxCeJeYs_C = "";
                // 利息差额（借）金额元素
                String lxCeJeYs_D = "";
                // 交易明细.金融产品类型 = “拆入”
                if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealDto.getFinprodType())){
                	// 变量.利息金额元素 = 数据字典.金额元素.应计利息
                	lxJeYs = "INTAMT";
                    // 变量.利息差额（贷）金额元素 = 数据字典.金额元素.利息支出差额
                    lxCeJeYs_C = "OUTCOMEAMT_C";
                    // 利息差额（借）金额元素 = 数据字典.金额元素.利息支出差额
                    lxCeJeYs_D = "OUTCOMEAMT_D";
                	// ad.会计场景代码="拆放同业（拆入）-到期"
                	ad.setTableId(DictCodeListTableId.会计场景代码_拆入_到期.getValue());
                	// 备注
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "拆入_到期交易登账");
                }
                else{
                	// 变量.利息金额元素 = 数据字典.金额元素.应收利息
                	lxJeYs = "INTAMT";
                    // 变量.利息差额（贷）金额元素 =  数据字典.金额元素.利息差额
                    lxCeJeYs_C = "INCOMEAMT_C";
                    // 利息差额（借）金额元素 =  数据字典.金额元素.利息差额
                    lxCeJeYs_D = "INCOMEAMT_D";

                	// ad.会计场景代码="1111：拆放同业-到期（拆出）"
                	ad.setTableId(DictCodeListTableId.会计场景代码_拆出_到期.getValue());
                	// 备注
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业-拆出到期交易登账");
                }


                // 定义变量.冲减应计利息
                BigDecimal cjProfit = BigDecimal.ZERO;
                //变量.成本科目余额 = 交易明细.交易本金
                if(glb_subjectAmt.compareTo(dealDto.getPrinAmt()) == 0){
                    //变量.冲减应计利息 = 变量.应计利息科目余额
                    cjProfit = glb_subjectProfit;
                }
                else {
                    //变量.冲减应计利息 = 变量.应计利息科目余额 * 交易明细.交易本金/变量.成本科目数量
                    cjProfit = ArithmeticUtil.divide(ArithmeticUtil.multiply(glb_subjectProfit, dealDto.getPrinAmt()), glb_subjectNum);
                }
                // 定义变量.冲减应计利息 - 调用金额共通方法设置有效小数位
                cjProfit = ArithmeticUtil.round(cjProfit, NumberEnum.MONEY);

                // 变量.冲减成本 = 变量.成本科目余额 * 交易明细.交易本金/变量.成本科目数量
                BigDecimal cjAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(glb_subjectAmt, dealDto.getPrinAmt()), glb_subjectNum);
                // 变量.冲减成本  - 调用金额共通方法设置有效小数位
                cjAmt = ArithmeticUtil.round(cjAmt, NumberEnum.MONEY);

                // 变量.冲减数量 = 交易明细.交易本金
                BigDecimal cjNum = dealDto.getPrinAmt();

                // 记账金额要素 map塞值
                accountAmountMap.put("ACCQUA", cjAmt);// 金额要素:核算数量 <- 变量.冲减成本
                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());//  金额要素:本息和 <- 交易明细.交易金额
                accountAmountMap.put("COST", cjAmt);//  金额要素:本金金额 <- 变量.冲减成本
                accountAmountMap.put(lxJeYs, cjProfit);//  金额要素:变量.利息金额元素 <- 变量.冲减应计利息


                // 获取差值 = 交易明细.利息总额 - 变量.冲减应计利息
                BigDecimal subResult = ArithmeticUtil.doSub(dealDto.getIntAmt(), cjProfit);

                //update by xr for TISHKBS-687 start
                if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealDto.getFinprodType())){
                    // 利息支出差额设置（借负，贷正）
                    if (subResult.compareTo(BigDecimal.ZERO) > 0) {
                        //变量.利息差额金额元素 <- 交易明细.利息总额 - 变量.冲减应计利息
                        accountAmountMap.put(lxCeJeYs_D, subResult.abs());
                    }
                    else {
                        //变量.利息差额金额元素 <- (交易明细.利息总额 - 变量.冲减应计利息)*-1
                        accountAmountMap.put(lxCeJeYs_C, subResult.abs());
                    }
                }else{
                    // 利息支出差额设置（借正，贷负）
                    if (subResult.compareTo(BigDecimal.ZERO) > 0) {
                        //变量.利息差额金额元素 <- 交易明细.利息总额 - 变量.冲减应计利息
                        accountAmountMap.put(lxCeJeYs_C, subResult.abs());
                    }
                    else {
                        //变量.利息差额金额元素 <- (交易明细.利息总额 - 变量.冲减应计利息)*-1
                        accountAmountMap.put(lxCeJeYs_D, subResult.abs());
                    }
                }
                //update by xr for TISHKBS-687 end
                // 定义  变量.利息差额_本位币  = 	实收金额_本位币 - (本金金额 _本位币 + 利息金额_本位币)
                BigDecimal subResultBccy = ArithmeticUtil.doSub(
                    accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                    ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                        , accountAmountMap.get(lxJeYs + BookConstant.BCCYAMT)));
                if(DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealDto.getFinprodType())){
                    // 利息支出差额设置（借负，贷正）
                    if (subResultBccy.compareTo(BigDecimal.ZERO) > 0) {

                        //变量.利息差额金额元素 <- 交易明细.利息总额 - 变量.冲减应计利息
                        accountAmountMap.put(lxCeJeYs_D + BookConstant.BCCYAMT, subResultBccy.abs());
                    }
                    else {
                        //变量.利息差额金额元素 <- (交易明细.利息总额 - 变量.冲减应计利息)*-1
                        accountAmountMap.put(lxCeJeYs_C+ BookConstant.BCCYAMT, subResult.abs());
                    }
                }else{
                    // 利息支出差额设置（借正，贷负）
                    if (subResultBccy.compareTo(BigDecimal.ZERO) > 0) {
                        //变量.利息差额金额元素 <- 交易明细.利息总额 - 变量.冲减应计利息
                        accountAmountMap.put(lxCeJeYs_C+ BookConstant.BCCYAMT, subResult.abs());
                    }
                    else {
                        //变量.利息差额金额元素 <- (交易明细.利息总额 - 变量.冲减应计利息)*-1
                        accountAmountMap.put(lxCeJeYs_D+ BookConstant.BCCYAMT, subResult.abs());
                    }
                }

                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                // 记账金额元素币种信息塞值
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, cjAmt);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, cjNum);
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, cjProfit);
            }
            //交易方向 = "收息到账" and 交易明细.金融产品类型 in （拆出， 同业借款）
            else if (DictCodeList.交易方向_收息到账.getValue().equals(dealDto.getPs())
                && (DictCodeListExt2.金融产品类型_同业借款.getValue().equals(dealDto.getFinprodType())
                || DictCodeListExt1.金融产品类型_拆出.getValue().equals(dealDto.getFinprodType()))
                ) {
                
                //ad.会计场景代码="利息到账（同业借款）"，与黄静沟通【收息到账】会计场景为【2726：资金交收_资金收款_同业借款中间收息】
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_同业借款中间收息.getValue());// 会计场景代码 = "资金交收_资金收款_同业借款中间收息";
                
                // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = varQtyskxSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                
                if (qtyskxSubjectAmt == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(acctEntity.getBooksetId());
                    searchDao.setBokDetailId(acctEntity.getBokDetailId());
                    searchDao.setHappenDate(acctEntity.getHappenDate());
                    searchDao.setBookDate(acctEntity.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.应计利息余额 = 科目余额
                        qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                        varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()),
                            qtyskxSubjectAmt);
                    }
                }
                
                // 定义 变量.应计利息 = 0
                BigDecimal varIntAmt = BigDecimal.ZERO;
                /*设计变更324,提前收息支持-详细设计-拆借交易账务处理.xlsx*/
                if(dealDto.getTradeDate().compareTo(dealDto.getMdate())<0){
                	//应计利息<-交易明细.交易金额
                	varIntAmt= dealDto.getTradeAmt();
                }else{
                    // if（交易明细.是否结清 = "否"）
                    if (DictCodeList.是否_否.getValue().equals(dealDto.getIsClean())) {
                    	//变量.应计利息 = MIN(交易明细.交易金额,变量.其他应收款-利息科目余额)
                        varIntAmt = dealDto.getTradeAmt().compareTo(qtyskxSubjectAmt)<=0?dealDto.getTradeAmt():qtyskxSubjectAmt;
                    }
                    else {
                        // 变量.应计利息 = 变量.其他应收款-利息科目余额
                        varIntAmt = qtyskxSubjectAmt;
                    }
                }

                /** modify by yanya on 20200106 实付通道费修改 start**/
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealDto.getValTradeFeeDetailList(),
                        dealDto.getProdId(), dealDto.getPortfolioId(), dealDto.getFinprodId());
                // 实付通道费 <-变量.交易各种费用.通道费
                accountAmountMap.put(BookConstant.REAL_CHLAMT,
                        ArithmeticUtil.round(dealTypesFeeDto.getChlFee(), NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.REAL_CHLAMT, dealDto.getCcy());
                //通道费账务明细代码 <-	变量.交易各种费用.通道费账务明细代码
                //通道费业务明细代码 <- 变量.交易各种费用.通道费业务明细代码
                BokDetailId chlFeeCode = dealTypesFeeDto.getChlFeeCode();
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID3, chlFeeCode);
                /** modify by yanya on 20200106 实付通道费修改 end**/
                
                //应收金额 <- 变量.应计利息
                BigDecimal ysje = varIntAmt;
                accountAmountMap.put(BookConstant.RECEITAMT, ArithmeticUtil.round(ysje, NumberEnum.MONEY));
                
                //实收金额 <- 交易明细.交易金额
                BigDecimal ssje = dealDto.getTradeAmt();
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(ssje, NumberEnum.MONEY));

                /** modify by yanya on 20200106 实付通道费修改 start**/
                // 利息差额 设置
                BigDecimal varCe = ArithmeticUtil.doSub(ssje, varIntAmt, dealTypesFeeDto.getChlFee());
                /** modify by yanya on 20200106 实付通道费修改 end**/
                if(varCe.compareTo(BigDecimal.ZERO) >= 0){
                    //利息差额(贷)  <- 金额元素.实收金额 - 变量.应计利息       
                    BigDecimal je = ArithmeticUtil.doSub(ssje,varIntAmt);
                    accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.round(je, NumberEnum.MONEY));

                }
                else{
                    //利息差额(借) <- (金额元素.实收金额 - 变量.应计利息 )* -1                                            
                    BigDecimal je = ArithmeticUtil.doSub(ssje,varIntAmt).negate();
                    accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(je, NumberEnum.MONEY));
                    
                }
                
                /**add by wujie on 20201030 start remark:资产减值**/
                BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_利息.getValue(), 
                			//发生金额
                			varIntAmt, 
                			//减值准备余额
                			glb_jzzbBlance, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskxSubjectAmt);
                	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
                
                /**add by wujie on 20201030 end**/
                
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借收息到账交易登账");
                
                
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(),
                    accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);

                //定义 变量.利息差额_本位币 
                BigDecimal incomeamtBccy = null;
                if (varCe.compareTo(BigDecimal.ZERO) >= 0) {
                	// 变量.利息差额_本位币  = 	实收金额_本位币 - (通道费_本位币 + 应收金额)
                	incomeamtBccy = ArithmeticUtil.doSub(
                			accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                			ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.RECEITAMT + BookConstant.BCCYAMT)
	            				, accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT)));
                	//利息差额(贷)_本位币<-变量.本息和_本位币		
                    accountAmountMap.put(BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT, 
                    		ArithmeticUtil.round(incomeamtBccy, NumberEnum.MONEY.getCode(), null));
                }
                else{
                	// 变量.利息差额_本位币  = 	 (通道费_本位币 + 应收金额) - 实收金额_本位币
                	incomeamtBccy = ArithmeticUtil.doSub(
                			ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.RECEITAMT + BookConstant.BCCYAMT)
	            				, accountAmountMap.get(BookConstant.REAL_CHLAMT + BookConstant.BCCYAMT))
                			,accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT));
                	
                	//利息差额(借)_本位币<-变量.利息差额_本位币	
                    accountAmountMap.put(BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT, 
                    		ArithmeticUtil.round(incomeamtBccy, NumberEnum.MONEY.getCode(), null));
                }
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                // 全局变量的更新
                // 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 变量.应计利息
                qtyskxSubjectAmt = ArithmeticUtil.subtract(qtyskxSubjectAmt, varIntAmt);
                // 将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);
                
                /**add by wujie on 20201030 start remark:资产减值**/
                //变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.冲减减值金额												
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance,冲抵减值金额);
                //变量.其他应收款利息汇总余额	=	变量.其他应收款利息汇总余额 - 变量.应计利息												
                glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,varIntAmt);
                
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
                				,null);
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                /**add by wujie on 20201030 end**/
            
            }// 交易方向 = "还本到账" and 交易明细.金融产品类型 in （拆出， 同业借款）
            else if(DictCodeList.交易方向_还本到账.getValue().equals(dealDto.getPs())
                    && (DictCodeListExt2.金融产品类型_同业借款.getValue().equals(dealDto.getFinprodType())
                    || DictCodeListExt1.金融产品类型_拆出.getValue().equals(dealDto.getFinprodType()))
                ){
                
                //ad.会计场景代码="资金收款-同业借款还本到期"
                ad.setTableId(DictCodeListTableId.会计场景代码_资金收款_同业借款还本到期.getValue());// 会计场景代码 = "资金收款-同业借款还本到期";
                
                //实收本金 <- 交易金额
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealDto.getTradeAmt(), NumberEnum.MONEY));
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借还本到账交易登账");
                
                /**add by wujie on 20201030 start remark:资产减值**/
                // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
                BigDecimal qtyskxSubjectAmt = varQtyskxBJSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                if (qtyskxSubjectAmt == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(dealDto.getBooksetId());
                    searchDao.setBokDetailId(dealDto.getBokDetailId());
                    searchDao.setHappenDate(dealDto.getHappenDate());
                    searchDao.setBookDate(dealDto.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.其他应收款-本金科目余额 = 科目余额
                        qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                    }
                }
                
                BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_本金.getValue(), 
                			//发生金额
                			dealDto.getTradeAmt(), 
                			//减值准备余额
                			glb_jzzbBlance, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskxSubjectAmt);
                	冲抵减值金额 = lossCommonBatchService.calcLossAmtForDur(dto);
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
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
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                /**add by wujie on 20201021 end**/
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                /**add by wujie on 20201020 start remark:资产减值**/
                //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.全价总额
                qtyskxSubjectAmt = ArithmeticUtil.doSub(qtyskxSubjectAmt,ArithmeticUtil.round(dealDto.getTradeAmt(), NumberEnum.MONEY));
                //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
                varQtyskxBJSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);
                
                //变量.减值准备科目余额 = 变量.减值准备科目余额 - 变量.冲减减值金额												
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance,冲抵减值金额);
                //变量.其他应收款本金汇总余额	=	变量.其他应收款本金汇总余额 - 变量.应计利息												
                glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,dealDto.getTradeAmt());	
                
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
                				,null);
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                
                /**add by wujie on 20201020 end**/
                
            }
            //else if（交易方向 = "应收款项转入" and 交易明细.金融产品类型 = "同业借款"）  add by yanya on 20191226
            else if (DictCodeList.交易方向_应收款项转入.getValue().equals(dealDto.getPs())
                    && DictCodeListExt2.金融产品类型_同业借款.getValue().equals(dealDto.getFinprodType())) {
                //ad.会计场景代码="应收款项转仓(转入)"
                ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转入.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借" +
                        SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealDto.getPs()) + "交易登账");

                // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = varQtyskxSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                //if（变量.其他应收款-利息科目余额 == null）
                if(qtyskxSubjectAmt == null) {
                    qtyskxSubjectAmt = this.getReceivablesSubjectBalanceExpectDirection(dealDto.getHappenDate(), dealDto.getBookDate(),
                            dealDto.getBokDetailId(), dealDto.getBranch(), dealDto.getBooksetId(),
                            DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), dealDto.getTaskPri(), DictCodeList.余额方向_借方.getValue());
                    //变量.其他应收款-利息科目余额MAP.put(交易明细.分支序号，变量.其他应收款-利息科目余额)
                    varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);
                }
                // accountAmountMap设置
                //应收款项-利息 <- 交易明细.应计利息总额
                accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(dealDto.getIntAmt(), NumberEnum.MONEY));
                //逾期款项-本金 <-交易明细.交易本金
                accountAmountMap.put(BookConstant.COST, ArithmeticUtil.round(dealDto.getPrinAmt(), NumberEnum.MONEY));
                //实收金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealDto.getTradeAmt(), NumberEnum.MONEY));

                
                //基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(), accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
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
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                //全局变量的更新
                //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 + 金额元素.应收款项 - 利息
                qtyskxSubjectAmt  = ArithmeticUtil.doAdd(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.INTAMT));
                //将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);
                
                
                /**add by wujie on 20201020 start remark:资产减值**/
                // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
                BigDecimal qtyskxBJSubjectAmt = varQtyskxBJSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                if (qtyskxBJSubjectAmt == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(dealDto.getBooksetId());
                    searchDao.setBokDetailId(dealDto.getBokDetailId());
                    searchDao.setHappenDate(dealDto.getHappenDate());
                    searchDao.setBookDate(dealDto.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.其他应收款-本金科目余额 = 科目余额
                    	qtyskxBJSubjectAmt = listSubjectBalance.get(0).getBalance();
                    }
                }
                //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 + 交易明细.交易本金
                qtyskxBJSubjectAmt = ArithmeticUtil.doAdd(qtyskxBJSubjectAmt,ArithmeticUtil.round(dealDto.getPrinAmt(), NumberEnum.MONEY));
                //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
                varQtyskxBJSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxBJSubjectAmt);
                
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 + 交易明细.利息总额
                glb_qtyskbjsum = ArithmeticUtil.doAdd(glb_qtyskbjsum,dealDto.getPrinAmt());
                //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 + 交易明细.交易本金
                glb_qtysklxsum = ArithmeticUtil.doAdd(glb_qtysklxsum,dealDto.getIntAmt());
               
                /**add by wujie on 20201020 end**/

                
            }
            //else if（交易方向 = "应收款项转出" and 交易明细.金融产品类型 = "同业借款"） add by yanya on 20191226
            else if (DictCodeList.交易方向_应收款项转出.getValue().equals(dealDto.getPs())
                    && DictCodeListExt2.金融产品类型_同业借款.getValue().equals(dealDto.getFinprodType())) {
                //ad.会计场景代码="应收款项转仓(转入)"
                ad.setTableId(DictCodeListTableId.会计场景代码_应收款项转仓转出.getValue());

                /** modif by yanya on 20201217 应收款项转出增加记账元素 是否其他债权 **/
                accountSceneMap.put(BookConstant.IS_QTZQ, DictCodeList.是否_否.getValue());

                /** add by yanya on 20200117 增加类型：资产类型、境内外 start**/
                // 境内外 = 变量.交易信息.境内外
                accountSceneMap.put(BookConstant.BLN_AREA, blnArea);
                //资产类型
                accountSceneMap.put(BookConstant.ASSET_TYPE, finprodType);
                /** add by yanya on 20200117 增加类型：资产类型、境内外 end**/
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆借" +
                        SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealDto.getPs()) + "交易登账");

                // 定义 变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = varQtyskxSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                //if（变量.其他应收款-利息科目余额 == null）
                if(qtyskxSubjectAmt == null) {
                    qtyskxSubjectAmt = this.getReceivablesSubjectBalanceExpectDirection(dealDto.getHappenDate(), dealDto.getBookDate(),
                            dealDto.getBokDetailId(), dealDto.getBranch(), dealDto.getBooksetId(),
                            DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), dealDto.getTaskPri(), DictCodeList.余额方向_借方.getValue());
                    varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);
                }
                // accountAmountMap设置
                //定义		变量.应计利息
                BigDecimal 应计利息 = BigDecimal.ZERO;
                //定义		变量.轧差金额
                BigDecimal 轧差金额 = BigDecimal.ZERO;
                //if（交易明细.是否结清 = "否"）
                if (DictCodeList.是否_否.getValue().equals(dealDto.getIsClean())) {
                    //变量.应计利息	<-		交易明细.应计利息总额
                    应计利息 = dealDto.getIntAmt();
                    //变量.轧差金额	<-		0
                    轧差金额 = BigDecimal.ZERO;
                }
                else {
                    //变量.应计利息					<-		变量.其他应收款-利息科目余额
                    应计利息 = qtyskxSubjectAmt;
                    //变量.轧差金额 = 交易明细.应计利息总额 - 变量.应计利息
                    轧差金额 = ArithmeticUtil.doSub(dealDto.getIntAmt(), qtyskxSubjectAmt);
                    //if  变量.轧差金额 >= 0
                    if(轧差金额 != null && 轧差金额.compareTo(BigDecimal.ZERO) >= 0) {
                        //轧差金额(贷)					<-		变量.利息差额
                        accountAmountMap.put(BookConstant.NETAMT_C, 轧差金额);
                    }
                    else {
                        //轧差金额(借)					<-		变量.利息差额*-1
                        accountAmountMap.put(BookConstant.NETAMT_D, ArithmeticUtil.multiply(轧差金额, new BigDecimal(-1)));
                    }

                }

                //应收款项-利息 <- 应计利息
                accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(应计利息, NumberEnum.MONEY));
                //逾期款项-本金 <-交易明细.交易本金
                accountAmountMap.put(BookConstant.COST, ArithmeticUtil.round(dealDto.getPrinAmt(), NumberEnum.MONEY));
                //实收金额 <- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealDto.getTradeAmt(), NumberEnum.MONEY));
                
                /**add by wujie on 20201030 start remark:资产减值**/
                // 定义 变量.其他应收款-本金科目余额   =	变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)															
                BigDecimal qtyskxBJSubjectAmt = varQtyskxBJSubjectAmt.get(String.valueOf(dealDto.getBranch()));
                if (qtyskxBJSubjectAmt == null) {
                    // 查询交易的到期日对应的应计利息
                    SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                    searchDao.setBooksetId(dealDto.getBooksetId());
                    searchDao.setBokDetailId(dealDto.getBokDetailId());
                    searchDao.setHappenDate(dealDto.getHappenDate());
                    searchDao.setBookDate(dealDto.getBookDate());
                    searchDao.setTaskPri(dealDto.getTaskPri());
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
                    searchDao.setBranch(dealDto.getBranch());
                    
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.其他应收款-本金科目余额 = 科目余额
                    	qtyskxBJSubjectAmt = listSubjectBalance.get(0).getBalance();
                    }
                }
                
                BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
                
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance.subtract(冲抵减值金额)) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_利息.getValue(), 
                			//发生金额
                			应计利息, 
                			//减值准备余额
                			glb_jzzbBlance, 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskxSubjectAmt);
                	冲抵减值金额 = ArithmeticUtil.doAdd(冲抵减值金额,lossCommonBatchService.calcLossAmtForDur(dto));
                }
                
                
                if(OFFSETIMPT && BigDecimal.ZERO.compareTo(glb_jzzbBlance.subtract(冲抵减值金额)) != 0){
                	LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealDto, 
                			DictCodeListExt1.现金流一级类型_本金.getValue(), 
                			//发生金额
                			dealDto.getPrinAmt(), 
                			//减值准备余额
                			glb_jzzbBlance.subtract(冲抵减值金额), 
                			//当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                			ArithmeticUtil.doAdd(glb_subjectAmt,glb_subjectProfit,glb_qtyskbjsum,glb_qtysklxsum,glb_qilxsum), 
                			//应收款余额
                			qtyskxBJSubjectAmt);
                	冲抵减值金额 = ArithmeticUtil.doAdd(冲抵减值金额,lossCommonBatchService.calcLossAmtForDur(dto));
                }
                //减值准备  <- 变量.冲抵减值金额 * -1
                accountAmountMap.put(BookConstant.IMPAMT,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
                
                /**add by wujie on 20201030 end remark:资产减值**/

                //基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                // accountNatrueMap.put(基础科目性质.其他应收款_本金, accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(), accountNatrueBranchMap);
                //accountNatrueMap.put(基础科目性质.其他应收款_利息, accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(), accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);

                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
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
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                //全局变量的更新
                //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项 - 利息
                qtyskxSubjectAmt  = ArithmeticUtil.doSub(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.INTAMT));
                //将变更后的余额更新到变量.其他应收款-利息科目余额MAP中
                varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxSubjectAmt);

                /**add by wujie on 20201020 start remark:资产减值**/
                //变量.其他应收款-本金科目余额 = 变量.其他应收款-本金科目余额 - 交易明细.交易本金
                qtyskxBJSubjectAmt = ArithmeticUtil.doSub(qtyskxBJSubjectAmt,ArithmeticUtil.round(dealDto.getPrinAmt(), NumberEnum.MONEY));
                //将变更后的余额更新到变量.其他应收款-本金科目余额MAP中															
                varQtyskxBJSubjectAmt.put(String.valueOf(dealDto.getBranch()), qtyskxBJSubjectAmt);
                
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
                glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,应计利息);
                //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
                glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,dealDto.getPrinAmt());
                //变量.减值准备科目余额 =	变量.减值准备科目余额 - 变量.冲减减值金额												
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance,冲抵减值金额);
                
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
                				,null);
                		// 统计分析明细表（资产减值）
                		rtnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
                	}
                }
                
                /**add by wujie on 20201020 end**/
            }
            /**add by wujie on 20201019 start remark:资产减值**/
            else if (DictCodeListExt3.交易方向_清理核销.getValue().equals(dealDto.getPs())) {
            	// 会计场景代码 = "拆放同业-减值清理";
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_减值清理.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业-减值清理");
                
                // nvl(交易明细.汇总交易编号,交易明细.交易编号);
                if (StringUtil.isNotEmpty(dealDto.getTotleTradeId())) {
                    accountSceneMap.put(BookConstant.TRADE_ID, dealDto.getTotleTradeId());
                }
                
                // 定义 变量.清理应计利息 = 0						
                BigDecimal cjProfit = BigDecimal.ZERO;
                // 定义		变量.清理面值 = 0					
                BigDecimal cjAmt = BigDecimal.ZERO;
                // 变量.冲减数量 = 交易明细.交易本金
                BigDecimal cjNum = dealDto.getPrinAmt();
                //定义		变量.清理减值准备 = 0						
                BigDecimal qljzzb = BigDecimal.ZERO;
                
                //if (交易明细.是否结清 = "是")
                if(DictCodeList.是否_是.getValue().equals(dealDto.getIsClean())){
                	//变量.清理应计利息 = 变量.应计利息科目余额
                	cjProfit = glb_subjectProfit;
                	//变量.清理面值 = 变量.成本科目余额 
                	cjAmt = glb_subjectAmt;
                	//变量.清理减值准备 = 变量.减值准备科目余额
                	qljzzb = glb_jzzbBlance;
                	//变量.清理数量 = 变量.成本科目数量
                	cjNum = glb_subjectNum;
                }
                else{
                	//TODO 对象外
                }
                //变量.减值损失 = 变量.清理面值+变量.清理减值准备+变量.清理应计利息
                BigDecimal 减值损失 = ArithmeticUtil.doAdd(cjAmt,cjProfit,qljzzb);
                减值损失 = ArithmeticUtil.round(减值损失, NumberEnum.MONEY);
                
                // 记账金额要素 map塞值
                //数量				<-			变量.清理数量			
                accountAmountMap.put("ACCQUA", qljzzb);
                //面值				<-			变量.清理面值			
                accountAmountMap.put("COST", cjAmt);
                //应计利息				<-			变量.清理应计利息			
                accountAmountMap.put("INTAMT", cjProfit);
                //减值准备	<-变量.冲减减值准备*-1					
                accountAmountMap.put(BookConstant.IMPAMT,ArithmeticUtil.round(qljzzb.negate(), NumberEnum.MONEY));
                
                // f（变量.减值损失 >= 0）
                if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_D, 减值损失);
                }
                else {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(减值损失.negate(), NumberEnum.MONEY));
                }
                
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币)
            	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.IMPAMT + BookConstant.BCCYAMT),
                				ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT)
                        				, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)));
                
            	
                if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
                	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币						
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));
                }
                else {
                	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
                }
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, cjAmt);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, cjNum);
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, cjProfit);
                //变量.减值准备科目余额	=	变量.减值准备科目余额 - 变量.清理减值准备												
                glb_jzzbBlance = ArithmeticUtil.doSub(glb_jzzbBlance, qljzzb);
                if(qljzzb.compareTo(BigDecimal.ZERO) != 0){
                	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
                	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.拆放同业减值准备.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
                	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoClean(valBokBookdetailBatchDto, 
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
            }
			else if (DictCodeListExt3.交易方向_其他应收款利息清理核销.getValue().equals(dealDto.getPs())) {
				// 会计场景代码 = "拆放同业-减值清理";
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_减值清理.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业-减值清理");
                
                // nvl(交易明细.汇总交易编号,交易明细.交易编号);
                if (StringUtil.isNotEmpty(dealDto.getTotleTradeId())) {
                    accountSceneMap.put(BookConstant.TRADE_ID, dealDto.getTotleTradeId());
                }
                
                //定义 变量.其他应收款-利息科目余额       =   变量.其他应收款-利息科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxSubjectAmt = varQtyskxSubjectAmt.get(String.valueOf(dealDto.getBranch()));

                // 查询交易的到期日对应的应计利息
                SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                searchDao.setBooksetId(dealDto.getBooksetId());
                searchDao.setBokDetailId(dealDto.getBokDetailId());
                searchDao.setHappenDate(dealDto.getHappenDate());
                searchDao.setBookDate(dealDto.getBookDate());
                searchDao.setTaskPri(dealDto.getTaskPri());
                searchDao.setBranch(dealDto.getBranch());
                
                if (qtyskxSubjectAmt == null) {
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue())));
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.应计利息余额 = 科目余额
                        qtyskxSubjectAmt = listSubjectBalance.get(0).getBalance();
                        varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()),
                            qtyskxSubjectAmt);
                    }
                }
                
                //定义 变量.逾期罚息利息科目余额
                BigDecimal yqfxSubjectAmt = varYqfxSubjiectAmt.get(dealDto.getBranch());
                if(yqfxSubjectAmt==null){
                	searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                            Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
                    List<SubjectBalance> listSubjectBalanceYQ = accountingCommonService.getSubjectBalanceExpectDirection(
                            searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalanceYQ != null && listSubjectBalanceYQ.size() > 0) {
                    	//  变量.逾期罚息利息科目余额 = 科目余额
                    	yqfxSubjectAmt = listSubjectBalanceYQ.get(0).getBalance();
                    	varYqfxSubjiectAmt.put(String.valueOf(dealDto.getBranch()),yqfxSubjectAmt);
                    }
                }
                
                //变量.清理其他应收款-利息		
                BigDecimal qlqtysk = BigDecimal.ZERO;
                //变量.清理罚息-利息
                BigDecimal qilfx = BigDecimal.ZERO;
                
                //if (交易明细.是否结清 = "是")
                if(DictCodeList.是否_是.getValue().equals(dealDto.getIsClean())){
                	qlqtysk = qtyskxSubjectAmt;
                	qilfx = yqfxSubjectAmt;
                }
                else{
                	//TODO
                }
                
                //变量.减值损失 = 变量.清理其他应收款-利息+变量.清理罚息-利息
                BigDecimal 减值损失 = ArithmeticUtil.doAdd(qlqtysk,qilfx);
                减值损失 = ArithmeticUtil.round(减值损失, NumberEnum.MONEY);
                
                // 记账金额要素 map塞值
                //应收款项-利息					<-		变量.清理其他应收款-利息							
                accountAmountMap.put(BookConstant.OTHER_INTAMT, qlqtysk);
                //逾期资产应计利息					<-		变量.清理罚息-利息					
                accountAmountMap.put(BookConstant.OVERINTAMT, qilfx);
                
                // f（变量.减值损失 >= 0）
                if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_D, 减值损失);
                }
                else {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(减值损失.negate(), NumberEnum.MONEY));
                }
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币)
            	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_INTAMT + BookConstant.BCCYAMT),
                				accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
                
                if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
                	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币						
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));
                }
                else {
                	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
                }
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_利息.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue(),
                        accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
                
                // 全局变量的更新
                //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项 - 利息
                varQtyskxSubjectAmt.put(String.valueOf(dealDto.getBranch()), ArithmeticUtil.doSub(qtyskxSubjectAmt, accountAmountMap.get(BookConstant.OTHER_INTAMT)));
                //变量.逾期罚息科目余额 = 变量.逾期罚息科目余额 - 金额元素.逾期资产应计利息
                varYqfxSubjiectAmt.put(String.valueOf(dealDto.getBranch()), ArithmeticUtil.doSub(yqfxSubjectAmt, accountAmountMap.get(BookConstant.OVERINTAMT)));
                
                //变量.其他应收款利息汇总余额 = 变量.其他应收款利息汇总余额 - 金额元素.应收款项-利息
                glb_qtysklxsum = ArithmeticUtil.doSub(glb_qtysklxsum,qlqtysk);
                //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
                glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum,qilfx);
                
			}
			else if (DictCodeListExt3.交易方向_其他应收款本金清理核销.getValue().equals(dealDto.getPs())) {
				// 会计场景代码 = "拆放同业-减值清理";
                ad.setTableId(DictCodeListTableId.会计场景代码_拆放同业_减值清理.getValue());
                // 备注
                accountSceneMap.put(BookConstant.BOOK_REMARK, "拆放同业-减值清理");
                
                // nvl(交易明细.汇总交易编号,交易明细.交易编号);
                if (StringUtil.isNotEmpty(dealDto.getTotleTradeId())) {
                    accountSceneMap.put(BookConstant.TRADE_ID, dealDto.getTotleTradeId());
                }
                
                //定义 变量.其他应收款-本金科目余额       =   变量.其他应收款-本金科目余额MAP.get(交易明细.分支序号)
                BigDecimal qtyskxBJSubjectAmt = varQtyskxSubjectAmt.get(String.valueOf(dealDto.getBranch()));

                // 查询交易的到期日对应的应计利息
                SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
                searchDao.setBooksetId(dealDto.getBooksetId());
                searchDao.setBokDetailId(dealDto.getBokDetailId());
                searchDao.setHappenDate(dealDto.getHappenDate());
                searchDao.setBookDate(dealDto.getBookDate());
                searchDao.setTaskPri(dealDto.getTaskPri());
                searchDao.setBranch(dealDto.getBranch());
                
                if (qtyskxBJSubjectAmt == null) {
                    searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                        Arrays.asList(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue())));
                    List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                        // 变量.应计利息余额 = 科目余额
                    	qtyskxBJSubjectAmt = listSubjectBalance.get(0).getBalance();
                        varQtyskxBJSubjectAmt.put(String.valueOf(dealDto.getBranch()),
                        		qtyskxBJSubjectAmt);
                    }
                }
                
                //定义 变量.逾期罚息利息科目余额
                BigDecimal yqfxSubjectAmt = varYqfxSubjiectAmt.get(dealDto.getBranch());
                if(yqfxSubjectAmt==null){
                	searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                            Arrays.asList(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue())));
                    List<SubjectBalance> listSubjectBalanceYQ = accountingCommonService.getSubjectBalanceExpectDirection(
                            searchDao, DictCodeList.余额方向_借方.getValue());
                    if (listSubjectBalanceYQ != null && listSubjectBalanceYQ.size() > 0) {
                    	//  变量.逾期罚息利息科目余额 = 科目余额
                    	yqfxSubjectAmt = listSubjectBalanceYQ.get(0).getBalance();
                    	varYqfxSubjiectAmt.put(String.valueOf(dealDto.getBranch()),yqfxSubjectAmt);
                    }
                }
                //变量.清理其他应收款-本金		
                BigDecimal qlqtysk = BigDecimal.ZERO;
                //变量.清理罚息-利息
                BigDecimal qilfx = BigDecimal.ZERO;
                
                //if (交易明细.是否结清 = "是")
                if(DictCodeList.是否_是.getValue().equals(dealDto.getIsClean())){
                	qlqtysk = qtyskxBJSubjectAmt;
                	qilfx = yqfxSubjectAmt;
                }
                else{
                	//TODO
                }
                
                //变量.减值损失 = 变量.清理其他应收款-利息+变量.清理罚息-利息
                BigDecimal 减值损失 = ArithmeticUtil.doAdd(qlqtysk,qilfx);
                减值损失 = ArithmeticUtil.round(减值损失, NumberEnum.MONEY);
                
                // 记账金额要素 map塞值
                //应收款项-本金					<-		变量.清理其他应收款-利息							
                accountAmountMap.put(BookConstant.OTHER_COST, qlqtysk);
                //逾期资产应计利息					<-		变量.清理罚息-利息					
                accountAmountMap.put(BookConstant.OVERINTAMT, qilfx);
                
                // f（变量.减值损失 >= 0）
                if (减值损失.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_D, 减值损失);
                }
                else {
                    accountAmountMap.put(BookConstant.IMPAMTLOSS_C, ArithmeticUtil.round(减值损失.negate(), NumberEnum.MONEY));
                }
                
                /**add by wujie on 20201021 start remark:资产减值：多币种改造**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                // 变量.减值损失_本位币 = 金额元素.减值准备_本位币 - (金额元素.面值_本位币 + 金额元素.应计利息_本位币)
            	BigDecimal 减值损失_本位币 = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.OTHER_COST + BookConstant.BCCYAMT),
                				accountAmountMap.get(BookConstant.OVERINTAMT + BookConstant.BCCYAMT));
                
                if(减值损失_本位币.compareTo(BigDecimal.ZERO) > 0) {
                	//减值损失(贷)_本位币	<-	变量.买卖价差_本位币						
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币, NumberEnum.MONEY.getCode(), null));
                }
                else {
                	//减值损失（借）_本位币 <- 变量.买卖价差_本位币 * -1							
                	accountAmountMap.put(BookConstant.IMPAMTLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(减值损失_本位币.negate(), NumberEnum.MONEY.getCode(), null));
                }
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                
                /**add by wujie on 20201021 end**/
                
                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String keyNow : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
                }
                
                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.其他应收款_本金.getValue(),
                    accountNatrueBranchMap);
                accountNatrueMap.put(DictCodeBaseSubjectNatrue.逾期资产应计利息.getValue(),
                        accountNatrueBranchMap);
                ad.setAccountNatrueMap(accountNatrueMap);
                
                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
 
                // 全局变量的更新
                //变量.其他应收款-利息科目余额 = 变量.其他应收款-利息科目余额 - 金额元素.应收款项 - 利息
                varQtyskxBJSubjectAmt.put(String.valueOf(dealDto.getBranch()), ArithmeticUtil.doSub(qtyskxBJSubjectAmt, accountAmountMap.get(BookConstant.OTHER_COST)));
                //变量.逾期罚息科目余额 = 变量.逾期罚息科目余额 - 金额元素.逾期资产应计利息
                varYqfxSubjiectAmt.put(String.valueOf(dealDto.getBranch()), ArithmeticUtil.doSub(yqfxSubjectAmt, accountAmountMap.get(BookConstant.OVERINTAMT)));
			
              //变量.其他应收款本金汇总余额 = 变量.其他应收款本金汇总余额 - 金额元素.逾期款项-本金
                glb_qtyskbjsum = ArithmeticUtil.doSub(glb_qtyskbjsum,qlqtysk);
                //变量.逾期利息汇总余额 = 变量.逾期利息汇总余额 - 金额元素.逾期资产应计利息																			
                glb_qilxsum = ArithmeticUtil.doSub(glb_qilxsum,qilfx);
			}
            /**add by wujie on 20201020 end**/
        }

        return rtnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate, String expBalanceDirect, String baseSubjectNature, Long taskPri) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
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
     * 获取标的基本信息
     * 
     * @param busiId 业务明显代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProductInfo(String busiId) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(busiId, targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }

    /**
     * 获取应收款应计利息-期待方向
     * @param happenDate
     * @param bookDate
     * @param bokDetailId
     * @param branch
     * @param booksetId
     * @param baseSubjectNatrue
     * @param taskPri
     * @param expBalanceDirect
     * @return
     */
    private BigDecimal getReceivablesSubjectBalanceExpectDirection(Date happenDate, Date bookDate, String bokDetailId,
                                                                   Long branch, String booksetId, String baseSubjectNatrue,
                                                                   Long taskPri, String expBalanceDirect) {
        BigDecimal lxSubjectAmt = BigDecimal.ZERO;
        //查询交易的到期日对应的应计利息
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        //发生日 = 交易类账务处理结果.发生日
        searchDao.setHappenDate(happenDate);
        //入账日 = 交易类账务处理结果.入账日
        searchDao.setBookDate(bookDate);
        //账务明细代码 = 交易类账务处理结果.账务明细代码
        searchDao.setBokDetailId(bokDetailId);
        //分支序号 = 交易明细.分支序号
        searchDao.setBranch(branch);
        //账套代码 = 交易类账务处理结果.账套代码
        searchDao.setBooksetId(booksetId);
        //基础科目性质 = 其他应收款-利息
        searchDao.setBaseSubjectNatureList(Arrays.asList(baseSubjectNatrue));
        //任务优先级 = 批处理参数.任务优先级
        searchDao.setTaskPri(taskPri);
        //期待余额方向 = 借方
        List<SubjectBalance> listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(searchDao, expBalanceDirect);
        if(!StringUtil.isEmptyList(listSubjectBalance)) {
            //变量.其他应收款-利息科目余额 =	上述共通返回的科目余额
            lxSubjectAmt = listSubjectBalance.get(0).getBalance();
        }
        return lxSubjectAmt;
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
     * add by wujie on 20201031
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
    	return dto;
    }

}
