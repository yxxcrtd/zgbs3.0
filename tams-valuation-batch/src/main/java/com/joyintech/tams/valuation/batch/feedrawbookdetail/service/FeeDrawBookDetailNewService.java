/*
 * 文件名：SecDealAccountsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：chengzhong
 * 修改时间：2018年6月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.feedrawbookdetail.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokFeeTableSet;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.feedrawbookdetail.dao.FeeDrawBookDetailMapper;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易费用计提批处理
 * 
 * @author chenlei1
 * @version 1.0 2019年11月22日
 * @see FeeDrawBookDetailNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FeeDrawBookDetailNewService {

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
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;


	/**
	 * 
	 */
	@Autowired
	private FeeDrawBookDetailMapper feeDrawBookDetailMapper;
	

    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(
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
		// 获取待处理任务信息
		SysBatchExecInfo batchExecInfo = (SysBatchExecInfo) param.get("BATCHINFO");
        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 1.数据准备
        // 1.1 变量定义       
        Map<String, Object> feeSetMap = new HashMap<>();//key：金融产品类型 + 费用类型 + 交易方向    value:费用配置信息Dto
     
		// 1.2查询费用配置信息
		List<BokFeeTableSet> listFeeTableSet = getFeeTableSetList(DictCodeListExt3.交易方向_费用计提.getValue());

		for (BokFeeTableSet detail : listFeeTableSet) {
			feeSetMap.put(detail.getFeeTypeId()+detail.getPs(), detail);
	    }
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景

		//2.生成计提账务凭证
		// 交易明细 ： 交易类账务处理结果.交易明细列表
		for (FinProductDealInfoComDTO item : acctEntityList) {
			for(TrdTradeFeeDetail feedetail : item.getValTradeFeeDetailList()){				
				//定义 变量.费用场景配置Dto = 	变量.费用配置信息Map.get(费用明细.费用类型 + 交易明细.交易方向)		
				BokFeeTableSet dto=(BokFeeTableSet) feeSetMap.get(feedetail.getFeeType()+item.getPs());

		        if (dto==null) {
					continue;
				}
				String remark ="交易费用计提"+SysDataUtil.getNameFromDataDict(DictCode.会计场景代码,dto.getTableId()) + "入账";																																																																																																															
				//定义 变量.金额元素集合 = 变量.费用场景配置Dto.记账金额元素.split(",")
				String[] amtArray=dto.getMoneyElement().split(",");
				AccountDriver ad = new AccountDriver();																																																										
				
	            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

				//ad.会计场景代码=变量.费用场景配置Dto.场景代码	
				ad.setTableId(dto.getTableId());

																																																														
	            Map<String, Object> accountSceneMap = new HashMap<>();
				Map<String, BigDecimal> accountAmountMap = new HashMap<>();
				Map<String, Object>   accountCurrencyMap = new HashMap<>();
				
			    
		        // 调用共通场景元素赋值-计提类场景元素 
	            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, item);	   
	            
				//accountSceneMap设置如下：		
				//备注	<-变量.备注REMARK	
			    accountSceneMap.put(BookConstant.BOOK_REMARK, remark);

				//费用类型 <-费用明细.费用类型																																																		
			    accountSceneMap.put(BookConstant.TYPE_1,feedetail.getFeeType());																																																									
			
	            //登记托管机构 DTO.登记托管机构
	            accountSceneMap.put(BookConstant.REGIST_ORG, item.getRegistOrg());
			
                //accountAmountMap设置如下：费用计提 <-费用明细.费用金额																																																													
		        //accountAmountMap.put(BookConstant.INTAMT, feedetail.getFeeAmt());
	            for (String amt : amtArray) {
					accountAmountMap.put(amt,feedetail.getFeeAmt());
	            }
	            
				//accountCurrencyMap设置如下： 费用计提币种 <-交易明细.币种	
	            //accountCurrencyMap.put(BookConstant.B_CCY, item.getCcy());

	            
	            // 记账金额元素币种信息塞值
	            // 因为与记账金额元素map的key及数量一致，故采用循环
//	            for (String keyNow : accountAmountMap.keySet()) {
//	                accountCurrencyMap.put(keyNow, item.getCcy());// value值塞入当前交易币种 （当前为获取计提里面的原币）
//	            }
	            for (String amt : amtArray) {
	                accountCurrencyMap.put(amt,item.getCcy());
	            }
	            ad.setAccountSceneMap(accountSceneMap);
	            ad.setAccountAmountMap(accountAmountMap);
	            ad.setAccountCurrencyMap(accountCurrencyMap);

				// add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
				SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
				mappingConfig.setCashType2(feedetail.getFeeType());
				ad.setSubjectMappingConfigDefault(mappingConfig);
				// add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end

	            // 记账凭证生成 : 调用记账引擎公共记账方法，生成记账凭证
	            BookGenerateResult bookGenerateResult = bookLogicNew.genAccount(ad, batchExecInfo);
	            returnResult.getValBokBookdetailBatch().addAll(bookGenerateResult.getValBokBookdetailBatch());
			}
		}
        return returnResult;
    }

    
    /**
	 * 查询费用配置信息
	 * 
	 * @param ps
	 *            交易方向
	 * @return List<FeeTableSet>
	 * @see
	 * @since 1.0
	 */
	private List<BokFeeTableSet> getFeeTableSetList(String ps) {
		// 费用配置信息
		return feeDrawBookDetailMapper.getFeeTableSetList(ps);
	}
    
}
