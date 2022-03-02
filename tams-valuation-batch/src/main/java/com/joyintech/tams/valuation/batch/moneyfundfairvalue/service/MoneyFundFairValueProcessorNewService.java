/*
 * 文件名：MoneyFundFairValueProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：zhangzhao
 * 修改时间：2019年10月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.moneyfundfairvalue.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokAssetMarketInfo;
import com.joyintech.tams.common.base.entity.BokBatchMarketwarningInfo;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 场内货币基金公允价值变动-账务明细批处理 process相关业务处理
 * 
 * @author zhangzhao
 * @version 1.0 2019年10月24日
 * @see MoneyFundFairValueProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class MoneyFundFairValueProcessorNewService {

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
     * 公用方法services
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private CalculationCommonService calculationCommonService;
    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
	public BookGenerateResultWithBokSummary execute(Map<String, Object> param1) {
        // 获取reader返回值
    	DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param1.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListDrawBaseInfoSelectComDTO() == null || acctEntity.getListDrawBaseInfoSelectComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();  // 发生日
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息list
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        // 定义变量，获取科目余额相关数据
        BigDecimal varCostSubjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal varCostSubjectAmtBccy = BigDecimal.ZERO;// 变量.成本科目余额_本位币
        BigDecimal varCostSubjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal varGyjzbdSubjectAmt = BigDecimal.ZERO;// 变量.公允价值变动科目余额
        BigDecimal varGyjzbdSubjectAmtBccy = BigDecimal.ZERO;// 变量.公允价值变动科目余额_本位币
        BigDecimal varGyjzbdAmt = BigDecimal.ZERO;	// 变量.公允价值变动
        BigDecimal varGyjzbdAmtBccy = BigDecimal.ZERO;	// 变量.公允价值变动_本位币
        BigDecimal varYjlxSubjectAmt =  BigDecimal.ZERO; // 变量.应计利息科目余额										 
        BigDecimal marketPrice = null ; // 变量.市价 
        BigDecimal lastDaymarketPrice = null; //变量.前一日市价 	
		BokBatchMarketwarningInfo bokBatchMarketwarningInfo= new BokBatchMarketwarningInfo(); //定义 变量.行情价格警告信息DTO	
		Date valDate = null;  // 变量.估值日期
        
        // 获取标的信息(相同账务明细对应相同的金融产品代码)
        TargetFinProduct targetFinProduct = this.getTargetFinProduct(acctEntity.getBusiId()); 
        // IF 变量.标的信息 IS NULL OR 变量.标的信息.交易场所 = 数据字典：估值交易场所.OTC场外
        if(targetFinProduct == null || DictCodeListExt1.交易场所_OTC场外.getValue().equals(targetFinProduct.getTradeMarket())){
            return null;
        }
        
        // 行情处理-调用共通取货币基金最新市价
        // 调用共通方法【详细设计-标的类共通方法设计-市价查询-基金SHEET】
       
    	MarketPriceDto  marketPriceDto = new MarketPriceDto();
    	marketPriceDto = finProductCommonService.getFundMarketPrice(happenDateNow, booksetIdNow,acctEntity.getBusiId());
    	// 变量.市价 = 市价			
    	marketPrice = marketPriceDto.getPrice();
    	// 变量.估值日期 = 估值日期							
    	valDate = marketPriceDto.getValDate();
        	
    	// 汇总信息查询-变量.当日统计分析账务明细汇总信息								
    	BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
    		   acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(), 
    		   acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
    	
    	
    	// 公允价值变动科目余额
    	// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
    	SubjectBalance gyjzbd = this.getSubjectBalance(booksetIdNow, acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
    			DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue(), null, taskPri);
    	if(gyjzbd != null){
    		// 变量.公允价值变动科目余额 = DTO.科目余额 
    		varGyjzbdSubjectAmt = gyjzbd.getBalance() == null ? BigDecimal.ZERO : gyjzbd.getBalance();
    		// 变量.公允价值变动科目余额_本位币 = DTO.本位币科目余额 
    		varGyjzbdSubjectAmtBccy = gyjzbd.getbAmt() == null ? BigDecimal.ZERO : gyjzbd.getbAmt();
    	}
    	// 成本类科目余额
    	// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
    	SubjectBalance cblkmye = this.getSubjectBalance(booksetIdNow, acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
    			DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue(), null, taskPri);
    	if(cblkmye != null){
    		// 变量.成本科目余额 = DTO.科目余额 	
        	varCostSubjectAmt = cblkmye.getBalance() == null ? BigDecimal.ZERO : cblkmye.getBalance();
        	// 变量.成本科目_本位币 = DTO.科目余额 _本位币
        	varCostSubjectAmtBccy = cblkmye.getbAmt() == null ? BigDecimal.ZERO : cblkmye.getbAmt();
        	// 变量.成本科目数量 = DTO.数量 									
        	varCostSubjectNum = cblkmye.getNum() == null ? BigDecimal.ZERO : cblkmye.getNum();
    	}
    	// 应计利息科目余额
    	// 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET   	
    	SubjectBalance yjlxlmye = this.getSubjectBalance(booksetIdNow, acctEntity.getBokDetailId(), happenDateNow, bookDateNow,
    			DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, taskPri);
    	if(yjlxlmye != null){
    		// 变量.应计利息科目余额 = DTO.科目余额											
        	varYjlxSubjectAmt = yjlxlmye.getBalance() == null ? BigDecimal.ZERO : yjlxlmye.getBalance();
    	}
        	
        	
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow){
        	/**add by taoran 20191231 start**/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(targetFinProduct.getCcy()) && !targetFinProduct.getCcy().equals(dto.getStandardCcy())){
                // 调用共通汇率查询
            	//changeRate = calculationCommonService.queryExchangeRateForTrade(targetFinProduct.getCcy(), dto.getStandardCcy(), acctEntity.getHappenDate());
				/** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start**/
				List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDateNow, booksetIdNow,
						dto.getStandardCcy(), targetFinProduct.getCcy());
				if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
					changeRate = currencyMarketList.get(0).getClosePrice();
				}
				/** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end**/
            }
            /**add by taoran 20191231 end**/
        	// 公允价值变动计算 	
        	// IF 变量.成本科目余额 = 0 and 变量.成本科目数量 = 0																														
	   		if(BigDecimal.ZERO.equals(varCostSubjectAmt) || BigDecimal.ZERO.equals(varCostSubjectNum)){
				// 变量.公允价值变动 = -1 * 变量.公允价值变动科目余额	
				varGyjzbdAmt = varGyjzbdSubjectAmt.negate();
        	} else {
        		if(marketPrice == null){
        		// 变量.市价 = 变量.成本科目余额/变量.成本科目数量
        		marketPrice = ArithmeticUtil.divide(varCostSubjectAmt, varCostSubjectNum);
	        	} else {
	        		// 变量.公允价值变动 = （变量.市价*变量.成本科目数量 -变量.成本科目余额） -变量.公允价值变动科目余额
	        		varGyjzbdAmt = ArithmeticUtil.doSub(
	        				ArithmeticUtil.multiply(marketPrice,varCostSubjectNum),varCostSubjectAmt,
	        				varGyjzbdSubjectAmt);
	        		//变量.公允价值变动_本位币 = 变量.市价*变量.成本科目数量 * 变量.汇率 - 变量.公允价值变动科目余额_本位币 -变量.成本科目余额_本位币
	        		varGyjzbdAmtBccy = ArithmeticUtil.doSub(calculationCommonService.doMultiply(
	        				marketPrice,varCostSubjectNum,changeRate),varCostSubjectAmtBccy,varGyjzbdSubjectAmtBccy);
	        	}
        	}
	   		// 调用金额共通方法设置有效小数位	 
	   		varGyjzbdAmt = ArithmeticUtil.round(varGyjzbdAmt, NumberEnum.MONEY);
	   		varGyjzbdAmtBccy = ArithmeticUtil.round(varGyjzbdAmtBccy, NumberEnum.MONEY);
		   // 初始化定义 - 记账场景要素管理
		   AccountDriver ad = new AccountDriver();	
		   // 初始化定义 - 存放该笔记账的场景要素 map定义
	       Map<String, Object> accountSceneMap = new HashMap<>();
	       // 初始化定义 - 存放该笔记账的相关金额要素 map定义
	       Map<String, BigDecimal> accountAmountMap = new HashMap<>();
	       // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
	       Map<String, Object> accountCurrencyMap = new HashMap<>();
           
	       // 塞入记账场景树节点 = 会计场景
	       ad.setTreeNode(bookSetTreeNodNow);
	       // 场景代码
	       ad.setTableId(DictCodeListTableId.会计场景代码_货币基金_公允价值变动.getValue());
	       
	       // accountSceneMap设置如下
	       // 境内外	 <-	变量.标的信息.境内外		
	       accountSceneMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());
	       // 分类2  <-	变量.标的信息.分类2			
	       accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
	       // 备注      <-	货币基金-公允价值变动						
	       accountSceneMap.put(BookConstant.BOOK_REMARK, "货币基金-公允价值变动");	
	       // 其他场景元素参看设计【详细设计-共用场景元素赋值】计提类SHEET场景元素 
	       accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);																													
	       
	       // accountAmountMap设置如下
	       if (BigDecimal.ZERO.compareTo(varGyjzbdAmt) < 0) {
	           // 公允价值变动_借  <-  变量.公允价值变动 FLOAT_D
	           accountAmountMap.put(BookConstant.FLOAT_D, varGyjzbdAmt);
	           // 公允价值变动损益_贷  <- 变量.公允价值变动 FLOAT_INGPL_C
	           accountAmountMap.put(BookConstant.FLOAT_INGPL_C, varGyjzbdAmt);
	           /**20200102 taoran add start**/
	           // 公允价值变动_借_本位币  <-  变量.公允价值变动_本位币 FLOAT_D
	           accountAmountMap.put(BookConstant.FLOAT_D + BookConstant.BCCYAMT, varGyjzbdAmtBccy);
	           // 公允价值变动损益_贷_本位币  <- 变量.公允价值变动_本位币 FLOAT_INGPL_C
	           accountAmountMap.put(BookConstant.FLOAT_INGPL_C + BookConstant.BCCYAMT, varGyjzbdAmtBccy);
	           /**20200102 taoran add end**/
	       }
	       else {
	           // 公允价值变动_贷  <- 变量.公允价值变动*-1	 FLOAT_C
	           accountAmountMap.put(BookConstant.FLOAT_C, varGyjzbdAmt.negate());
	           // 公允价值变动损益_借  <-  变量.公允价值变动*-1 FLOAT_INGPL_D
	           accountAmountMap.put(BookConstant.FLOAT_INGPL_D, varGyjzbdAmt.negate());
	           /**20200102 taoran add start**/
	           // 公允价值变动_贷_本位币  <- 变量.公允价值变动_本位币*-1	 FLOAT_C
	           accountAmountMap.put(BookConstant.FLOAT_C, varGyjzbdAmtBccy.negate());
	           // 公允价值变动损益_借_本位币  <-  变量.公允价值变动_本位币*-1 FLOAT_INGPL_D
	           accountAmountMap.put(BookConstant.FLOAT_INGPL_D, varGyjzbdAmtBccy.negate());
	           /**20200102 taoran add end**/
	       }
	       
	       // 记账金额元素币种信息塞值
	       // 因为与记账金额元素map的key及数量一致，故采用循环
	       for (String keyNow : accountAmountMap.keySet()) {
	           accountCurrencyMap.put(keyNow, dto.getoCcy());// value值塞入当前交易币种 （当前为获取计提里面的原币）
	       }  
	       accountCurrencyMap.put(BookConstant.B_CCY, dto.getStandardCcy());
	       // map塞入场景要素管理中
	       ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
	       ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
	       ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
			// add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
			ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
			// add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
	       // 记账凭证生成
	       BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
	       // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
	       returnResult.getValBokBookdetailBatch().addAll(
	           resultNowNow.getValBokBookdetailBatch());
	       
	       // 统计分析账务明细汇总信息赋值
	       BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
	       // if 变量.当日统计分析账务明细汇总信息 is null		
	       if(bokSummaryTableYestdy == null || StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())){
	    	   // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.成本科目数量			
	    	   bokSummaryTableNew.setPosAmt(varCostSubjectNum);
	    	   // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.成本科目数量
	    	   bokSummaryTableNew.setTdyPosition(varCostSubjectNum);
	    	   // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的	
	    	   bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
	    	   // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额 
	    	   bokSummaryTableNew.setTdyCostAmt(varCostSubjectAmt);
	    	   // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动 
	    	   bokSummaryTableNew.setTdyFloatIngpl(varGyjzbdAmt);
	    	   // 变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额  + 变量.公允价值变动 		
	    	   bokSummaryTableNew.setTdyFloatIngplExp(ArithmeticUtil.doAdd(varGyjzbdSubjectAmt,varGyjzbdAmt));
	    	   // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.金融产品类型			
	    	   bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
	    	   // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日			
	    	   bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
	    	   // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日		
	    	   bokSummaryTableNew.setMdate(targetFinProduct.getMdate());
	    	   // 变量.当日统计分析账务明细汇总信息.行情日期 = 变量.估值日期
	    	   bokSummaryTableNew.setValDate(valDate); 
	    	   // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 交易类账务处理结果.业务明细代码	
	    	   bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
	    	   //变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
	    	   bokSummaryTableNew.setTdyFloatIngplB(varGyjzbdAmtBccy);
	    	   //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币  + 变量.公允价值变动_本位币
	    	   bokSummaryTableNew.setTdyFloatIngplExpB(ArithmeticUtil.doAdd(varGyjzbdAmtBccy, varGyjzbdSubjectAmtBccy));
			} else {
				// 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动 	
			   bokSummaryTableNew.setTdyFloatIngpl(varGyjzbdAmt);                                                                   
			   // 变量.当日统计分析账务明细汇总信息.行情日期 =  变量.估值日期
			   bokSummaryTableNew.setValDate(valDate);
			   // 变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额  + 变量.公允价值变动 																													
			   bokSummaryTableNew.setTdyFloatIngplExp(ArithmeticUtil.doAdd(varGyjzbdSubjectAmt,varGyjzbdAmt));
			   //变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
	    	   bokSummaryTableNew.setTdyFloatIngplB(varGyjzbdAmtBccy);
	    	   //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币  + 变量.公允价值变动_本位币
	    	   bokSummaryTableNew.setTdyFloatIngplExpB(ArithmeticUtil.doAdd(varGyjzbdAmtBccy, varGyjzbdSubjectAmtBccy));
			}
																													
																															
	        // 行情价格警告信息赋值																						
			// 变量.行情价格警告信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
	        bokBatchMarketwarningInfo.setFinprodId(targetFinProduct.getFinprodId());
			// 变量.行情价格警告信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
	        bokBatchMarketwarningInfo.setFinprodType(targetFinProduct.getFinprodType());
			// 变量.行情价格警告信息DTO.投资目的 = 计提类账务处理结果.投资目的
	        bokBatchMarketwarningInfo.setInvAim(acctEntity.getInvAim());
			// 变量.行情价格警告信息DTO.市场代码 = 变量.标的信息.市场代码
	        bokBatchMarketwarningInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
			// 变量.行情价格警告信息DTO.估值来源 = 市价共通返回.估值来源
	        bokBatchMarketwarningInfo.setValueSource(marketPriceDto.getValueSource());
			// 变量.行情价格警告信息DTO.理论行情日期 = 市价共通返回.理论行情日期
	        bokBatchMarketwarningInfo.setTheValueDate(marketPriceDto.getSchemeValDate());
			// 变量.行情价格警告信息DTO.实际行情日期 = 市价共通返回.实际行情日期
	        bokBatchMarketwarningInfo.setActValueDate(marketPriceDto.getValDate());
			// 变量.行情价格警告信息DTO.是否一致 = 市价共通返回.是否一致
	        bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_是.getValue());
	        //如果不一致插入警告表
        	if(marketPriceDto.getValDate()==null ||marketPriceDto.getSchemeValDate().compareTo(marketPriceDto.getValDate())!=0){
        		bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_否.getValue());
	        	// 添加到返回值中
	        	returnResult.getBokBatchMarketwarningInfoList().add(bokBatchMarketwarningInfo); 
        	}

        	// add by weipeng 20210807：增加资产行情信息入库 start
        	// 变量.资产行情信息DTO
			BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
			//变量.资产行情信息DTO.流水号
			bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
			// 变量.资产行情信息DTO.账套代码
			bokAssetMarketInfo.setBooksetId(acctEntity.getBooksetId());
			// 变量.资产行情信息DTO.发生日期
			bokAssetMarketInfo.setHappenDate(acctEntity.getHappenDate());
			// 变量.资产行情信息DTO.入账日期
			bokAssetMarketInfo.setBookDate(acctEntity.getBookDate());
			// 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
			bokAssetMarketInfo.setFinprodId(targetFinProduct.getFinprodId());
			// 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
			bokAssetMarketInfo.setFinprodType(targetFinProduct.getFinprodType());
			// 变量.资产行情信息DTO.投资目的 = 计提类账务处理结果.投资目的
			bokAssetMarketInfo.setInvAim(acctEntity.getInvAim());
			// 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
			bokAssetMarketInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
			// 变量.资产行情信息DTO.市价 = 变量.市价
			bokAssetMarketInfo.setMarketPrice(marketPrice);
			// 变量.资产行情信息DTO.是否来源于行情数据
			// if 市价共通返回.市价== null || 变量.市价 != 市价共通返回.市价
			if(marketPriceDto == null || marketPriceDto.getPrice() == null ||  marketPrice.compareTo(marketPriceDto.getPrice()) != 0){
				// 变量.资产行情信息DTO.行情日期 = 变量.发生日期
				bokAssetMarketInfo.setValueDate(happenDateNow);
				bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
			}
			else{
				// 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
				bokAssetMarketInfo.setValueSource(marketPriceDto.getValueSource());
				// 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
				bokAssetMarketInfo.setValueDate(marketPriceDto.getValDate());
				// 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
				bokAssetMarketInfo.setSchemeId(marketPriceDto.getSchemeId());
				// 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
				bokAssetMarketInfo.setSchemeDetailId(marketPriceDto.getSchemeDetailId());
				bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
			}
			// 添加到返回值中
			returnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
			// add by weipeng 20210807：增加资产行情信息入库 end
        }
		return returnResult;
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
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, String invAim,
                                             Long taskPri) {

        // 定义返回的model
        SubjectBalance subjectBalance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 塞入投资目的
        List<String> invAimList = null;
        if (invAim != null && invAim.length() > 0) {
            invAimList = new ArrayList<String>();
            invAimList.add(invAim);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setInvAimList(invAimList);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

   
    
    
    /**
     * 获取标的基本信息
     * 
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProduct(String busiId) {
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

        return targetFinProduct;
    }
    
    
}
