/*
 * 文件名：SettlementInstructionService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ruanhongming
 * 修改时间：2018年6月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.settlementinstruction.service;


import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.base.entity.SysBatchTask;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokBooksetCapacctInfo;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.settlementinstruction.dao.SettlementInstructionMapper;
import com.joyintech.tams.valuation.batch.settlementinstruction.dto.SettlementInstructionDTO;
import com.joyintech.tams.valuation.batch.settlementinstruction.dto.SettlementInstructionResult;
import com.joyintech.tams.valuation.batch.settlementinstruction.dto.TrdProductDealDTO;
import com.joyintech.tams.valuation.batch.vatdraw.service.VatDrawCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 记账凭证查询service
 * 
 * @author ruanhongming
 * @version 1.0 2018年6月8日
 * @see SettlementInstructionNewService
 * @since 1.0
 */
@Service
public class SettlementInstructionNewService {
    /**
     * 公用方法services
     */
    @Autowired
    private VatDrawCommonService vatDrawCommonService;
    /**
     * 查询处理对象信息Mapper
     */
    @Autowired
    private SettlementInstructionMapper settlementInstructionMapper;
    
    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 共通方法service
     * add by yanya on 20201216
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    

    /**
     * {@inheritDoc}
     */
    public List<SettlementInstructionResult> execute(Map<String, Object> param) {
        //1、处理变量设置
        List<SettlementInstructionResult> result =  new ArrayList<SettlementInstructionResult>();
//        List<BokBatchTaskTodo> listValBatchTaskTodo =  new ArrayList<BokBatchTaskTodo>();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        SysBatchTask batchTask = (SysBatchTask)param.get("BATCHTASK");
      
               
        //2、取得业务类型的账务场景分录
        //任务代码    
        sqlMap.put("taskId", batchExecuteInfo.getTaskId());
        //业务表
        sqlMap.put("busiTable", DictCodeList.业务表_结算指令表.getValue());
        //是否随交易出账
        sqlMap.put("isTrade", DictCodeList.是否_否.getValue());
        //交易状态
        sqlMap.put("orderStatus", DictCodeListExt1.交易状态_撤销.getValue());

        //结算状态 add by yinya 增加结算状态的判断，只处理结算成功的
        sqlMap.put("settleStatus", DictCodeList.成功失败标志_成功.getValue());
        
        sqlMap.putAll(param);
        //查询处理对象信息
        List<SettlementInstructionDTO> listSettlementInstruction = settlementInstructionMapper.selectDealInfo(sqlMap);
        
        if (CollectionUtils.isEmpty(listSettlementInstruction)) {
            return result;
        }
        // 获取分录信息
        Set<String> bookSetIds = new HashSet<String>();
        listSettlementInstruction.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getBooksetId())) {
                bookSetIds.add(item.getBooksetId());
            }
        });
        Map<String, TreeNode> booksetTreeNodeMap = bookCommonLogic.commonForGetTreeNodeMap(
            bookSetIds, batchTask.getTaskId());

        // 2.拼接为返回的数据结构
        Map<String, Object> resultMap = new HashMap<>();
        for (SettlementInstructionDTO dto : listSettlementInstruction) {
            String strKey = dto.getBooksetId().trim();
            dto.setTaskPri(batchTask.getTaskPri());// 添加任务优先级
            if (resultMap.get(strKey) == null) {
                SettlementInstructionResult newSettlementInstructionResult = new SettlementInstructionResult();
                newSettlementInstructionResult.setTaskId(batchExecuteInfo.getTaskId());
                newSettlementInstructionResult.setBooksetId(dto.getBooksetId());
                newSettlementInstructionResult.setHappenDate(
                    (Date)param.get(BookConstant.HAPPEN_DATE));
                newSettlementInstructionResult.setBooksetDate(dto.getBooksetDate());
                newSettlementInstructionResult.setBookDate(dto.getBookDate());
                newSettlementInstructionResult.setBookSetTreeNode(
                    (TreeNode)booksetTreeNodeMap.get(dto.getBooksetId()));
                newSettlementInstructionResult.setOrderId(dto.getOrderId());
                newSettlementInstructionResult.getListSettlementInstructionDTO().add(dto);
                resultMap.put(strKey, newSettlementInstructionResult);
            }
            else {
                SettlementInstructionResult settlementInstructionResult = (SettlementInstructionResult)resultMap.get(
                    strKey);
                // 是否是相同交易标识
                boolean samFlg = false;
                for (SettlementInstructionDTO dao : settlementInstructionResult.getListSettlementInstructionDTO()) {
                    if (dao.getTradeId().equals(dto.getTradeId())) {
                        samFlg = true;
                        break;
                    }
                }
                // 不是相同的交易，则添加入集合中
                if (!samFlg) {
                    settlementInstructionResult.getListSettlementInstructionDTO().add(dto);
                    resultMap.put(strKey, settlementInstructionResult);
                }
            }
        }
      
        // 添加返回数据
        for (String skey : resultMap.keySet()) {
            result.add((SettlementInstructionResult)resultMap.get(skey));
        }
        return result;
    }
    
    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return SettlementInstructionResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult executeProcessor(Map<String, Object> param) {
        //获取reader返回值
        SettlementInstructionResult acctEntity = (SettlementInstructionResult)param.get(GlobalConstant.READER_RESULT);
        
        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取从reader中拿到的值
        String taskId = acctEntity.getTaskId();//任务代码
        String booksetId = acctEntity.getBooksetId(); //账套代码
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        List<SettlementInstructionDTO> settlementInstructionDTO = acctEntity.getListSettlementInstructionDTO(); // 金融产品交易信息list

        // 循环金融产品交易信息list,进行逻辑处理
        for (SettlementInstructionDTO dto : settlementInstructionDTO) {
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            // 设置账务引擎参数
            ad.setTableId(dto.getOrderType());// 会计场景代码
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值
            accountSceneMap.put(BookConstant.BOOKSET_ID, booksetId);// 账套代码
            accountSceneMap.put(BookConstant.TRADE_ID, dto.getOrderId());// 交易编号
            accountSceneMap.put(BookConstant.HAPPEN_DATE, dto.getHappenDate());// 发生日
            accountSceneMap.put(BookConstant.BOOK_DATE, dto.getBookDate());// 入账日
            accountSceneMap.put(BookConstant.BOOKSET_DATE, dto.getBooksetDate());// 账套日期
            accountSceneMap.put(BookConstant.TASK_ID, taskId);// 任务代码
            accountSceneMap.put(BookConstant.B_CCY, dto.getStandardCcy());// 本位币
            accountSceneMap.put(BookConstant.CCY, dto.getCcy());// 交易币种
            accountSceneMap.put(BookConstant.BUSI_ID1, dto.getBusiId());//业务明细代码
            
            
            //定义本方资金账户信息
            BokDetailId valBokDetailIdForMy = new BokDetailId();
            valBokDetailIdForMy.setBokDetailId(dto.getBokDetailId());//账务明细代码
            valBokDetailIdForMy.setBusiId(dto.getBusiId());//业务明细代码
            //定义对方资金账户信息
            BokDetailId valBokDetailIdForOther = new BokDetailId();
            // 定义 对手账务明细代码 = 交易明细.对方资金账户 == null? Null:调用账务明细代码共通（交易明细.对方资金账户,“现金类”，交易明细.币种)
            if(StringUtil.isNotEmpty(dto.getCounterAcctNo())){
            	
            	if(DictCodeListExt3.指令类型_债券类资产_申购划款.getValue().equals(dto.getOrderType()) 
                		|| DictCodeListExt3.指令类型_债券类资产_申购退款.getValue().equals(dto.getOrderType())){
            		valBokDetailIdForOther = interfCommonService.getBokDetailId(dto.getCounterAcctNo(), null,
            				DictCodeListExt1.资金账户类型_交易保证金账户.getValue(), null, null, dto.getCcy());
            	}else{
            		valBokDetailIdForOther = interfCommonService.getBokDetailId(dto.getCounterAcctNo(), null,
                            dto.getCounterAcctNoType(), null, null, dto.getCcy());
            	}
                if(StringUtil.isNotEmpty(dto.getCounterAcctNoType())&&dto.getCounterAcctNoType().equals(DictCodeListExt1.资金账户类型_待清算款.getValue())){
                	valBokDetailIdForOther = interfCommonService.getBokDetailId(dto.getFinprodId(), null,
                            dto.getFinprodType(), dto.getInvAim(), null, null);
                	if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getFinprodType())){
                        // 获取标的信息.境内外
                        accountSceneMap.put(BookConstant.VALUATION_TRADE_MARKET, dto.getTradeMarket());// 交易场所
                	}
                }
            }
            
            //结算指令明细记录.收付方向 = 收入
            if(DictCodeList.收付方向_收入.getValue().equals(dto.getPs())){
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,valBokDetailIdForOther);//账务明细代码1
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,valBokDetailIdForMy);//账务明细代码2
                
            }//结算指令明细记录.收付方向 = 支出
            else{
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID1,valBokDetailIdForMy);//账务明细代码1
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,valBokDetailIdForOther);//账务明细代码2
            }
            
            accountSceneMap.put(BookConstant.DEALTYPE, dto.getOrderType());// 交易类型 -结算指令并发DTO.指令类型 
            accountSceneMap.put(BookConstant.HAPPEN_NUMBER, "");// 发生数量
            accountSceneMap.put(BookConstant.BOOK_REMARK,SysDataUtil.getNameFromDataDict(DictCode.指令类型, dto.getOrderType()) + "入账");
            accountSceneMap.put(BookConstant.ACCT_TYPE, dto.getBusiDetailType());// 资金账户类型 - 交易明细.资金账户类型                         
            accountSceneMap.put(BookConstant.BOOK_TYPE, DictCodeList.凭证类型_日终.getValue());// 凭证类型 -> 【日终:01】
            accountSceneMap.put(BookConstant.TASK_PRI, dto.getTaskPri()); // 任务优先级 -> 交易明细.任务优先级
            accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue()); // 冲正标识 - 默认塞入正常
            accountSceneMap.put(BookConstant.ASSET_TYPE, dto.getFinprodType()); 
            //金额要素 和 金额元素 对应的币种 塞值
            accountAmountMap.put(BookConstant.DEALAMT, dto.getSettleAmt());//结算金额         
            accountCurrencyMap.put(BookConstant.DEALAMT, dto.getCcy());

            // add by weipeng on 20220302：增加辅助核算项配置 start
            // 定义 变量.辅助核算项Map，用于会计分录对应辅助核算项
            Map<String, SubjectMappingConfig> configMap = new HashMap<>();
            // 定义 变量.辅助核算项
            SubjectMappingConfig config = new SubjectMappingConfig();
            // add by weipeng on 20220221 区分资金账户金额元素 start
            // 定义 变量.本方资金账户类型
            String myAcctType= dto.getBusiDetailType();
            // 定义 变量.对方资金账户类型
            String counterAcctType = dto.getCounterAcctNoType();
            // 本方资金账户类型 或 对方资金账户类型对应资金账户类型，赋值对应金额元素
            if(DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(myAcctType)
               || DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(counterAcctType)){
                accountAmountMap.put(BookConstant.DEALAMT_HQ, dto.getSettleAmt());
                accountCurrencyMap.put(BookConstant.DEALAMT_HQ, dto.getCcy());
                // 增加辅助核算项配置
                config = new SubjectMappingConfig();
                config.setAccountType(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                configMap.put(DictCodeBaseSubjectNatrueNew.活期存款_本金.getValue(), config);
            }
            if(DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(myAcctType)
               || DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(counterAcctType)){
                accountAmountMap.put(BookConstant.DEALAMT_JS, dto.getSettleAmt());
                accountCurrencyMap.put(BookConstant.DEALAMT_JS, dto.getCcy());
                // 增加辅助核算项配置
                config = new SubjectMappingConfig();
                config.setAccountType(DictCodeListExt1.资金账户类型_结算备付金账户.getValue());
                configMap.put(DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue(), config);
            }
            // 券商资金账户现划分在结算备付金科目性质下
            if(DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(myAcctType)
               || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(counterAcctType)){
                accountAmountMap.put(BookConstant.DEALAMT_JS, dto.getSettleAmt());
                accountCurrencyMap.put(BookConstant.DEALAMT_JS, dto.getCcy());
                // 增加辅助核算项配置
                config = new SubjectMappingConfig();
                config.setAccountType(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                configMap.put(DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue(), config);
            }
            // 券商资金账户现划分在结算备付金科目性质下
            if(DictCodeListExt1.资金账户类型_交易保证金账户.getValue().equals(myAcctType)
               || DictCodeListExt1.资金账户类型_交易保证金账户.getValue().equals(counterAcctType)){
                accountAmountMap.put(BookConstant.DEALAMT_CC, dto.getSettleAmt());
                accountCurrencyMap.put(BookConstant.DEALAMT_CC, dto.getCcy());
                // 增加辅助核算项配置
                config = new SubjectMappingConfig();
                config.setAccountType(DictCodeListExt1.资金账户类型_交易保证金账户.getValue());
                configMap.put(DictCodeBaseSubjectNatrueNew.存出保证金_本金.getValue(), config);
            }
            // add by weipeng on 20220221 区分资金账户金额元素 end

            // add by weipeng on 20220221 区分资金账户金额元素 start
            // 定义 资金账户对应金额
            BigDecimal 资金账户金额 = dto.getSettleAmt();
            // 定义 变量.资金账户币种
            String 资金账户币种 = dto.getCcy();
            // add by weipeng on 20220221 区分资金账户金额元素 end

            /**针对2740 业务场景特殊处理**/
            if(DictCodeListTableId.会计场景代码_资金划款_债券类资产申购确认.getValue().equals(dto.getOrderType())){
            	TrdProductDealDTO deal = this.getTrdProductDealDto(booksetId, dto.getTradeId());
            	if(deal!=null){
            		
            		//清算款科目
            		accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.doAdd(BigDecimal.ZERO, deal.getFpriceAmt()));  
            		
            		BigDecimal depositAmt = ArithmeticUtil.doSub(deal.getFpriceAmt(), deal.getTradeAmt());
	            	//保证金金额--账务明细代码3
	            	accountAmountMap.put(BookConstant.DEALAMT_CC, depositAmt);
	                accountCurrencyMap.put(BookConstant.DEALAMT_CC, deal.getCcy());
	                
	            	//补缴款金额
                    // add by weipeng on 20220221 区分资金账户金额元素 start
                    if(DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(myAcctType)
                       || DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(counterAcctType)){
                        accountAmountMap.put(BookConstant.DEALAMT_HQ, deal.getTradeAmt());
                        accountCurrencyMap.put(BookConstant.DEALAMT_HQ, dto.getCcy());
                        // 增加辅助核算项配置
                        config = new SubjectMappingConfig();
                        config.setAccountType(DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        configMap.put(DictCodeBaseSubjectNatrueNew.活期存款_本金.getValue(), config);
                    }
                    if(DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(myAcctType)
                       || DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(counterAcctType)){
                        accountAmountMap.put(BookConstant.DEALAMT_JS, deal.getTradeAmt());
                        accountCurrencyMap.put(BookConstant.DEALAMT_JS, dto.getCcy());
                        // 增加辅助核算项配置
                        config = new SubjectMappingConfig();
                        config.setAccountType(DictCodeListExt1.资金账户类型_结算备付金账户.getValue());
                        configMap.put(DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue(), config);
                    }
                    // 券商资金账户现划分在结算备付金科目性质下
                    if(DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(myAcctType)
                       || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(counterAcctType)){
                        accountAmountMap.put(BookConstant.DEALAMT_JS, deal.getTradeAmt());
                        accountCurrencyMap.put(BookConstant.DEALAMT_JS, dto.getCcy());
                        // 增加辅助核算项配置
                        config = new SubjectMappingConfig();
                        config.setAccountType(DictCodeListExt1.资金账户类型_券商资金账户.getValue());
                        configMap.put(DictCodeBaseSubjectNatrueNew.结算备付金_本金.getValue(), config);
                    }
                    // add by weipeng on 20220221 区分资金账户金额元素 end

	                //账务明细代码3
	                accountSceneMap.put(BookConstant.BOK_DETAIL_ID3,deal.getBokDetailId3());
	                
	                /**add by wujie 20210528 start**/
	                if(deal.getBokDetailId3()==null || StringUtil.isEmpty(deal.getBokDetailId3().getBokDetailId())
	                		|| (StringUtil.isNotEmpty(dto.getCounterAcctNo()) 
	                				&& StringUtil.isNotEmpty(dto.getCounterAcctNoType())
	                				&& !dto.getCounterAcctNoType().equals(DictCodeListExt1.资金账户类型_待清算款.getValue()))
	                		){
	                	accountSceneMap.put(BookConstant.BOK_DETAIL_ID3,interfCommonService.getBokDetailId(dto.getCounterAcctNo(), null,
	            				DictCodeListExt1.资金账户类型_交易保证金账户.getValue(), null, null, dto.getCcy()));
		                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,interfCommonService.getBokDetailId(dto.getFinprodId(), null,
	                            dto.getFinprodType(), dto.getInvAim(), null, null));
	                }
	                /**add by wujie 20210528 end**/
            	}
            }

            /**针对【资金划款-拆出到期2735】业务场景特殊处理 by yanya on 20201216 start**/
            if(StringUtil.equals(DictCodeListTableId.会计场景代码_资金划款_拆出到期.getValue(), dto.getOrderType())
                    && StringUtil.isNotEmpty(dto.getFinprodId()) && StringUtil.isNotEmpty(dto.getFinprodType())) {
                //获取交易类标的信息-拆出
                TargetFinProduct targetFinProduct = this.getTargetInfo(dto.getFinprodId(), dto.getFinprodType(), BookConstant.DEFALUT_BRANCH);
                accountCurrencyMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
            }
            /**针对【资金划款-拆出到期2735】业务场景特殊处理 by yanya on 20201216 end**/

            /**针对【2752、2753、2754、2755、2756、2757】业务场景特殊处理 by yanya on 20201217 start**/
            if((StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_摊余成本类其他债权投资收息.getValue(), dto.getOrderType())
                    || StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类其他债权投资收息.getValue(), dto.getOrderType())
                    || StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_综合收益类其他债权投资收息.getValue(), dto.getOrderType())
                    || StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_摊余成本类其他债权投资还本.getValue(), dto.getOrderType())
                    || StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_公允价值变动类其他债权投资还本.getValue(), dto.getOrderType())
                    || StringUtil.equals(DictCodeListTableId.会计场景代码_资金收款_综合收益类其他债权投资还本.getValue(), dto.getOrderType())
                ) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                //获取其他债权投资标的信息
                TargetFinProduct targetFinProduct = this.getTargetInfo(dto.getFinprodId(), dto.getFinprodType(),null);
                accountCurrencyMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());
            }
            /**针对【2752、2753、2754、2755、2756、2757】业务场景特殊处理 by yanya on 20201217 end **/

            /**针对【2901】业务场景特殊处理 by yanya on 20201217 start**/
            if(StringUtil.equals(DictCodeListTableId.会计场景代码_逾期款收款.getValue(), dto.getOrderType())
                && StringUtil.isNotEmpty(dto.getFinprodId())) {
                //获取标的信息
                TargetFinProduct targetFinProduct = this.getTargetInfo(dto.getFinprodId(), dto.getFinprodType(),null);
                accountCurrencyMap.put(BookConstant.IS_QTZQ, targetFinProduct.getIsQtzq());
            }
            /**针对【2901】业务场景特殊处理 by yanya on 20201217 end **/

            /**针对【2903】业务场景特殊处理 by yanya on 20201217 start**/
            if(StringUtil.equals(DictCodeListTableId.会计场景代码_逾期款转出.getValue(), dto.getOrderType())
                    && StringUtil.isNotEmpty(dto.getFinprodId())) {
                //获取标的信息
                TargetFinProduct targetFinProduct = this.getTargetInfo(dto.getFinprodId(), dto.getFinprodType(),null);
                accountCurrencyMap.put(BookConstant.IS_QTZQ, targetFinProduct.getIsQtzq());
                accountCurrencyMap.put(BookConstant.INV_AIM, dto.getInvAim());
                accountCurrencyMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());
                accountCurrencyMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());
            }
            /**针对【2903】业务场景特殊处理 by yanya on 20201217 end **/

            // 增加清算款对应的辅助核算项
            config = new SubjectMappingConfig();
            config.setTradeMarket(dto.getTradeMarket());
            configMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), config);
            ad.setSubjectMappingConfigMap(configMap);
            // add by weipeng on 20220302：增加辅助核算项配置 end
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            // 增加活期存款对应的辅助核算项
//            ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        }

        return returnResult;
    }
    
    
    private TrdProductDealDTO getTrdProductDealDto(String booksetId,String tradeId){
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("tradeId", tradeId);
    	TrdProductDealDTO dto = settlementInstructionMapper.selectTrdProductDealDTO(map);
    	if(dto==null){
    		return null;
    	}
    	
    	if(dto.getbTradeId() != null){
    		map = new HashMap<String,Object>();
        	map.put("tradeId", dto.getbTradeId());
    		
        	StlPayOrders stlPayOrders =  settlementInstructionMapper.selectStlPayOrders(map);
        	if(stlPayOrders!=null){
        		BokDetailId bokDetailId3 =  interfCommonService.getBokDetailId(stlPayOrders.getCounterAcctNo(), null,
        				DictCodeListExt1.资金账户类型_交易保证金账户.getValue(),null, null, stlPayOrders.getCcy());
        		dto.setBokDetailId3(bokDetailId3);
        	}
    	}
    	else{
    		//根据账套代码、资金账户类型、币种查询 资金账户
            BokBooksetCapacctInfo capacctInfo = interfCommonService.queryBokBooksetCapacctInfo(booksetId,null,dto.getCcy(),DictCodeListExt1.资金账户类型_交易保证金账户.getValue());
        	if(capacctInfo!=null){
        		BokDetailId bokDetailId3 =  interfCommonService.getBokDetailId(capacctInfo.getAcctNo(), null,
        				capacctInfo.getAcctType(), null, null, capacctInfo.getCcy());
        		dto.setBokDetailId3(bokDetailId3);
        	}
    	}
    	return dto;
    }


    /**
     * 调用共通方法：[标的类共通方法-查询标的信息]
     * add by yanya on 20201217
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 查询条件
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
        // 标的信息条件
        TargetSearchDto searchBaseinfo = new TargetSearchDto();
        searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
        if(StringUtil.isNotEmpty(finprodType)) {
            searchBaseinfo.setFinprodType(finprodType);
        }
        if(StringUtil.isNotEmpty(branch)) {
            searchBaseinfo.setBranch(branch);
        }
        queryCriteria.add(searchBaseinfo);
        Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(finprodId, queryCriteria);
        TargetFinProduct targetFinProduct = (TargetFinProduct) targetSearchMap.get(searchBaseinfo);
        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }
        return targetFinProduct;
    }

}
