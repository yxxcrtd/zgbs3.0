/*
 * 文件名：BookAccountGenLogic.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.accounting;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.service.FinProductCommonBatchService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountEntry;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.dao.BaseAccountWriterMapper;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokBussDetsubMem;
import com.joyintech.tams.valuation.batch.common.cache.service.entity.ValBokSubjectDetailMem;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import com.joyintech.tams.valuation.common.utils.ValStringUtil;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.entity.BokBussDetsub;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokSubject;
import com.joyintech.tams.common.base.entity.BokSubjectDetail;

/**
 * 记账凭证生成类
 *
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see BookAccountGenLogic
 * @since 1.0
 */
@Service
public class BookAccountGenLogic {

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * BokKeepaccountdetailMapper
     */
    @Autowired
    private BaseAccountWriterMapper baseAccountWriterMapper;

    @Autowired
    private AccountingCommonService accountingCommonService;
    
    @Autowired
    private BookAccountCommonService bookAccountCommonService;
    
    @Autowired
    private BokDetailIdMapper valBokDetailIdMapper;
    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private FinProductCommonBatchService finProductCommonBatchService;

    /**
     * 记账凭证生成
     *
     * @param accountDriver 记账驱动信息，包含该笔记账所需使用的所有信息。
     * @param batchinfo 批的相关信息
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult genAccount(AccountDriver accountDriver, SysBatchExecInfo batchinfo) {
        // 根据驱动信息，生成一条记账凭证主表信息
        // 获取记账场景要素信息
        Map<String, Object> accountSceneMap = accountDriver.getAccountSceneMap();
        // 获取记账金额要素信息
        Map<String, BigDecimal> accountAmountMap = accountDriver.getAccountAmountMap();
        // 获取金额元素币种信息
        Map<String, Object> accountCurrencyMap = accountDriver.getAccountCurrencyMap();
        // 获取基础科目性质信息
        Map<String, Map<String, Object>> accountNatrueMap = accountDriver.getAccountNatrueMap();

        BookGenerateResult result = new BookGenerateResult();

        List<ValBokBookdetailBatch> bookDetailList = new ArrayList<>();
        result.setValBokBookdetailBatch(bookDetailList);
        // 根据驱动信息，生成记账凭证明细信息。
        // 获取记账场景树节点
        TreeNode treeNode = accountDriver.getTreeNode();
        // 定义分录信息变量
        List<AccountEntry> accountEntrys = new ArrayList<AccountEntry>();
        // 获取分录信息
        accountSceneMap.put("ACCOUNTEVENTS", accountDriver.getTableId());
        getAccountEntryFromTreeNode(treeNode, accountSceneMap, accountEntrys);
        // 遍历步骤获取的列表，循环生成明细凭证信息
        // 先借后贷
        Collections.sort(accountEntrys, new Comparator<AccountEntry>() {
            @Override
            public int compare(AccountEntry o1, AccountEntry o2) {
                return o2.getCdFlag().compareTo(o1.getCdFlag());
            }
        });
        // 定义记账凭证明细信息变量
        Map<String, ValBokBookdetailBatch> tempDetailMap = new HashMap<>();
        for (AccountEntry accountEntry : accountEntrys) {
        	if(accountAmountMap.get(accountEntry.getFormElement() + BookConstant.BCCYAMT)== null){
                if (accountAmountMap.get(accountEntry.getFormElement()) == null
                        || (!DictCodeList.是否_是.getValue().equals(
                            accountSceneMap.get(
                                BookConstant.IS_ZERO_ACCOUNT))
                            && BigDecimal.ZERO.compareTo(
                                accountAmountMap.get(accountEntry.getFormElement())) == 0)) {
                        continue;
                    }
        	}else{
               if (accountAmountMap.get(accountEntry.getFormElement()) == null
                        || (!DictCodeList.是否_是.getValue().equals(
                            accountSceneMap.get(
                                BookConstant.IS_ZERO_ACCOUNT))
                            && BigDecimal.ZERO.compareTo(
                                accountAmountMap.get(accountEntry.getFormElement())) == 0
                            && BigDecimal.ZERO.compareTo(
                                accountAmountMap.get(accountEntry.getFormElement() + BookConstant.BCCYAMT)) == 0)) {
                        continue;
                    }
        	}
//            if (accountAmountMap.get(accountEntry.getFormElement()) == null
//                || (!DictCodeList.是否_是.getValue().equals(
//                    accountSceneMap.get(
//                        BookConstant.IS_ZERO_ACCOUNT))
//                    && BigDecimal.ZERO.compareTo(
//                        accountAmountMap.get(accountEntry.getFormElement())) == 0
//                    && BigDecimal.ZERO.compareTo(
//                        accountAmountMap.get(accountEntry.getFormElement() + BookConstant.BCCYAMT)) == 0)) {
//                continue;
//            }
            ValBokBookdetailBatch bookDetail = new ValBokBookdetailBatch();

            // 流水 <- 自动生成，规则为BOOK + "YYYYMMDD"+20位序列号
            bookDetail.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
            // 账套代码 <- 场景要素.账套代码
            bookDetail.setBooksetId((String)accountSceneMap.get(BookConstant.BOOKSET_ID));
            // 场景代码 <- 场景要素.场景代码
            bookDetail.setTableId(accountDriver.getTableId());
            // 交易编号 <- 场景要素.交易编号
            bookDetail.setTradeId((String)accountSceneMap.get(BookConstant.TRADE_ID));
            // 任务代码 <- 场景要素.任务代码
            bookDetail.setTaskId((String)accountSceneMap.get(BookConstant.TASK_ID));
            // 任务代码 <- 场景要素.任务优先级
            if (accountSceneMap.get(BookConstant.TASK_PRI) != null) {
                bookDetail.setTaskPri(
                    (Long)accountSceneMap.get(BookConstant.TASK_PRI));
            }
            // 发生日期 <- 场景要素.发生日期
            bookDetail.setHappenDate((Date)accountSceneMap.get(BookConstant.HAPPEN_DATE));
            // 入账日期 <- 场景要素.入账日期
            bookDetail.setBookDate((Date)accountSceneMap.get(BookConstant.BOOK_DATE));
            // 账套日期 <- 场景要素.账套日期
            bookDetail.setBooksetDate((Date)accountSceneMap.get(BookConstant.BOOKSET_DATE));
            // 基础科目性质 <- 会计分录配置信息.基础科目性质
            bookDetail.setBaseSubjectNature(accountEntry.getBaseSubjectNature());
            SubjectBalance subjectBalance = new SubjectBalance();
            BokDetailId valBokDetailId = (BokDetailId)accountSceneMap.get(
                accountEntry.getBokDetailId());
            if (valBokDetailId != null) {
                // 业务明细代码 <- 场景要素.业务明细代码
                bookDetail.setBusiId(valBokDetailId.getBusiId());
                // 账务明细代码 <- 场景要素.账务明细代码
                bookDetail.setBokDetailId(valBokDetailId.getBokDetailId());
                // 账套代码
                valBokDetailId.setBooksetId(bookDetail.getBooksetId());
                subjectBalance.setBusiId(valBokDetailId.getBusiId());
                subjectBalance.setBusiDetailType(valBokDetailId.getBusiDetailType());
                subjectBalance.setInvAim(valBokDetailId.getInvAim());
            }
            // 分支序号,当前默认为0
            Map<String, Object> accountNatrueSceneMap = accountNatrueMap == null ? null : accountNatrueMap.get(
                accountEntry.getBaseSubjectNature());
            Long branch = accountNatrueSceneMap != null ? (Long)accountNatrueSceneMap.get(
                BookConstant.BRANCH) : BookConstant.DEFALUT_BRANCH;
            bookDetail.setBranch(branch != null ? branch : BookConstant.DEFALUT_BRANCH);
            // 科目号 <- 会计分录配置信息.科目号
            bookDetail.setSubjectNo(accountEntry.getSubjectId());
            // 查找账务明细代码表
            BokDetailId bokDetailId = valBokDetailIdMapper.selectByPrimaryKey(valBokDetailId);
            if(bokDetailId != null){
            	subjectBalance.setBusiId(bokDetailId.getBusiId());
                subjectBalance.setBusiDetailType(bokDetailId.getBusiDetailType());
                subjectBalance.setInvAim(bokDetailId.getInvAim());
            }
            // 是否生成四级科目
            if (DictCodeList.是否_是.getValue().equals(accountEntry.getGenDetsubFlag())) {

                // 设置明细科目
                BokSubjectDetail bokSubjectDetail = genDetailSubjectCode(accountSceneMap,
                    accountEntry, bokDetailId);
                bookDetail.setValBokSubjectDetail(bokSubjectDetail);
                // 设置业务明细科目
                BokBussDetsub bokBussDetsub = genBokBussDetsub(accountSceneMap, accountEntry,
                    bokDetailId);
                bookDetail.setValBokBussDetsub(bokBussDetsub);
                // 设置四级科目号
                bookDetail.setDetailSubjectNo(
                    bokBussDetsub != null ? bokBussDetsub.getDetailSubjectNo() : null);
            }
            // 借贷方向 <- 会计分录配置信息.借贷方向
            bookDetail.setCdFlag(accountEntry.getCdFlag());
            // 原币 <- 金额币种.币种
            bookDetail.setoCcy((String)accountCurrencyMap.get(accountEntry.getFormElement()));
            // 原币发生额 <- 金额元素.金额
            bookDetail.setoHappenAmt(accountAmountMap.get(accountEntry.getFormElement()));
            // 本位币 <- 金额币种.币种
            //MODIFY JINMAODONG 20190718 修改本位币币种获取方式
            bookDetail.setbCcy((String)accountSceneMap.get(BookConstant.B_CCY));
            // 本位币发生额 <- 金额元素.金额
            //MODIFY JINMAODONG 20190718 修改本位币金额获取方式
            String bccyElement = accountEntry.getFormElement() + BookConstant.BCCYAMT;
            BigDecimal bccyAmt = accountAmountMap.get(bccyElement);
            //ADD JINMAODONG 增加本位币金额处理 20190718 START
            //1.如果调用账务引擎时，已经塞入了本币金额，则不做任何处理
            //1.1如果原币币种和本币币种一样，则本币发生额 =  原币发生额
            //1.2如果原币币种和本币币种不一样，则调用汇率查询的共通去查询小于或等于入账日当日的日终汇率， 本币发生额 = 入账日日终汇率 * 原币发生额
            //1.2.1//如果查不到最新汇率，则直接使用原币金额
            {
                if(null == bccyAmt){
                    if(bookDetail.getoCcy().equals(bookDetail.getbCcy())){
                        bccyAmt = bookDetail.getoHappenAmt();
                    }else{
                        List<CurrencyMarketDto> currMarkets = finProductCommonBatchService.getCurrencyMarket(bookDetail.getBookDate(), bookDetail.getBooksetId(), bookDetail.getbCcy(),
                                bookDetail.getoCcy());
                        if(CollectionUtils.isNotEmpty(currMarkets)){
                            CurrencyMarketDto currMarket = currMarkets.get(0);
                            bccyAmt = ArithmeticUtil.round(currMarket.getClosePrice().multiply(bookDetail.getoHappenAmt()), NumberEnum.MONEY);
                            // 折算汇率 <- NULL
                            bookDetail.setExRate(currMarket.getClosePrice());
                        }else{
                            bccyAmt = bookDetail.getoHappenAmt();
                        }
                    }
                }
            }
            //ADD JINMAODONG 增加本位币金额处理 20190718 END
            bookDetail.setbHappenAmt(bccyAmt);
            // 换汇日期 <- 场景要素.换汇日期
            bookDetail.setExDate((Date)accountSceneMap.get(BookConstant.EX_DATE));



            // 发生数量 <- 场景要素.发生数量
            if (DictCodeList.是否_是.getValue().equals(accountEntry.getAccQuaFlag())) {
                BigDecimal happenNumber = accountNatrueSceneMap != null ? (BigDecimal)accountNatrueSceneMap.get(
                    BookConstant.HAPPEN_NUMBER) : null;
                if (happenNumber != null) {
                    bookDetail.setHappenNumber(happenNumber);
                }
                else {
                    bookDetail.setHappenNumber(
                        (BigDecimal)accountSceneMap.get(BookConstant.HAPPEN_NUMBER));
                }
            }
            // 结息日 <- 场景要素.结息日
            bookDetail.setMdate((Date)accountSceneMap.get(BookConstant.MDATE));
            // 冲正标识 <- 场景要素.冲正标识
            bookDetail.setOffsetFlag((String)accountSceneMap.get(BookConstant.OFFSET_FLAG));
            // 凭证编号
            if (StringUtil.isEmpty((String)accountSceneMap.get(BookConstant.BOOK_ID))) {
                bookAccountCommonService.genBookId(bookDetail);
            }
            else {
                bookDetail.setBookId((String)accountSceneMap.get(BookConstant.BOOK_ID));
            }
            // 凭证说明 <- 场景要素.凭证说明
            bookDetail.setBookRemark((String)accountSceneMap.get(BookConstant.BOOK_REMARK));
            // 凭证类型 <- 场景要素.凭证类型
            bookDetail.setBookType((String)accountSceneMap.get(BookConstant.BOOK_TYPE));
            // 处理序列 <- 自动生成的序列号20位
            bookDetail.setBookOrder(
                    new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
            
            
            /**modify by wujie on 20201130 start reamrk:增加部分科目余额更新处理
            if (DictCodeList.是否_是.getValue().equals(
                accountSceneMap.get(BookConstant.IS_ACC_BALANCE))) {**/
            	
        	/**add by wujie on 20201130 start reamrk:增加部分科目余额更新处理**/
            boolean isAccBalance = false;
            if (DictCodeList.是否_是.getValue().equals(
                    accountSceneMap.get(BookConstant.IS_ACC_BALANCE))) {
            	isAccBalance = true;
            }
            else if (DictCodeListExt3.累计余额_部分.getValue().equals(accountSceneMap.get(BookConstant.IS_ACC_BALANCE))) {
            	Map<String,String> accountNatrueBalanceMap = accountDriver.getAccountNatrueBalanceMap();
            	if(accountNatrueBalanceMap!=null
            			&& DictCodeList.是否_是.getValue().equals(accountNatrueBalanceMap.get(accountEntry.getBaseSubjectNature()))){
            			 isAccBalance = true;
            	}
            }
            if(isAccBalance){
            /**add by wujie on 20201130 end**/
            	
            	
            	/**add by wujie on 20201111 start remark:指定基础科目性质科目进行余额更新**/
            	//科目余额_本次发生前
            	BigDecimal balance = accountAmountMap.get(accountEntry.getFormElement()+BookConstant.BALANCE);
            	//科目余额本位币_本次发生前
            	BigDecimal balance_bccy = accountAmountMap.get(accountEntry.getFormElement()+BookConstant.BALANCE+BookConstant.BCCYAMT);
            	//科目数量_本次发生前
    			BigDecimal balanceNum = accountAmountMap.get(accountEntry.getFormElement()+BookConstant.BALANCE_NUMBER);
            	if(balance!=null){
            		if(null == balance_bccy){
	                    if(bookDetail.getoCcy().equals(bookDetail.getbCcy())){
	                    	balance_bccy = balance;
	                    }else{
	                        List<CurrencyMarketDto> currMarkets = finProductCommonBatchService.getCurrencyMarket(bookDetail.getBookDate(),
	                        		bookDetail.getBooksetId(), bookDetail.getbCcy(),
	                                bookDetail.getoCcy());
	                        if(CollectionUtils.isNotEmpty(currMarkets)){
	                            CurrencyMarketDto currMarket = currMarkets.get(0);
	                            balance_bccy = ArithmeticUtil.round(currMarket.getClosePrice().multiply(balance), NumberEnum.MONEY);
	                        }else{
	                        	balance_bccy = balance;
	                        }
	                    }
	                }
            		Map<String, Object> mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(
        					//科目余额方向
        					accountEntry.getBalFlag(),
        					//实际余额方向
        					(String)accountSceneMap.get(accountEntry.getFormElement()+BookConstant.BAL_FLAG), 
        					//实际余额
        					balance,
        					//实际数量
        					balanceNum, 
                            //发生额方向
                            bookDetail.getCdFlag(), 
                            //原币发生额
                            bookDetail.getoHappenAmt(),
                            //发生数量
                            bookDetail.getHappenNumber(),
                            //本币实际余额
                            balance_bccy,
                            //本币发生额
                            bookDetail.getbHappenAmt()
                		);
					 // 余额
			        bookDetail.setoAmtHappenDate(
			                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("balance"), NumberEnum.MONEY));
			        // 本币余额
			        bookDetail.setbAmtHappenDate(
			                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
			        // 数量
			        bookDetail.setNumAmtHappenDate(
			                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("num"), NumberEnum.MONEY));
			        // 余额方向
			        bookDetail.setBalFlagHappenDate((String)mapHappenDate.get("balDirect"));
            	}
            	else{
            	/**add by wujie on 20201111 end remark:指定基础科目性质科目进行余额更新**/
            	
	                String key = bookDetail.getBooksetId() + GlobalConstant.COMMA + bookDetail.getBokDetailId() + GlobalConstant.COMMA
	                    + bookDetail.getBaseSubjectNature() + GlobalConstant.COMMA + bookDetail.getHappenDate() + GlobalConstant.COMMA
	                    + bookDetail.getBookDate();
	                // 计算原币余额、数量、余额方向
	                calBalance(accountEntry, bookDetail, tempDetailMap.get(key));
	                tempDetailMap.put(key, bookDetail);
	                // 原币余额
	                bookDetail.setoAmtHappenDate(bookDetail.getoAmtHappenDate());
	                // 本币余额
	                bookDetail.setbAmtHappenDate(bookDetail.getbAmtHappenDate());
	                
	            /**add by wujie on 20201111 start remark:指定基础科目性质科目进行余额更新**/
            	}
            	/**add by wujie on 20201111 end remark:指定基础科目性质科目进行余额更新**/
            }
            bookDetail.setOnwayDate((Date)accountSceneMap.get(BookConstant.ONWAY_DATE));
            // 科目类别
            bookDetail.setSubjectType(accountEntry.getSubjectType());

            // 0金额是否处理 <- 场景要素.是否0入账
            bookDetail.setIsexeZeroAmt((String) accountSceneMap.get(BookConstant.IS_ZERO_ACCOUNT));
            //add by yanya 20191218 增加收息已结清份额 <-	金额元素.收息已结清份额 **/
            bookDetail.setShareAmtClean(accountAmountMap.get(BookConstant.SHARE_AMT_CLEAN));
            
            subjectBalance.setBooksetId(bookDetail.getBooksetId());
            subjectBalance.setBokDetailId(bookDetail.getBokDetailId());
            
            subjectBalance.setTableId(bookDetail.getTableId());
            subjectBalance.setSubjectNo(bookDetail.getSubjectNo());
            
            subjectBalance.setOCcy(bookDetail.getoCcy());
            subjectBalance.setbCcy(bookDetail.getbCcy());
            subjectBalance.setBalFlagHappenDate(bookDetail.getBalFlagHappenDate());
            subjectBalance.setBalance(bookDetail.getoAmtHappenDate());
            subjectBalance.setbAmt(bookDetail.getbAmtHappenDate());
            subjectBalance.setNum(bookDetail.getNumAmtHappenDate());
            subjectBalance.setShareAmtClean(bookDetail.getShareAmtClean());
            subjectBalance.setHappenDate(DateUtil.format(bookDetail.getHappenDate()));
            subjectBalance.setBaseSubjectNature(bookDetail.getBaseSubjectNature());
            subjectBalance.setDetailSubjectNo(bookDetail.getDetailSubjectNo());
            subjectBalance.setBalFlag(accountEntry.getBalFlag());
            subjectBalance.setMdate(bookDetail.getMdate());
            subjectBalance.setBranch(bookDetail.getBranch());
            subjectBalance.setTaskPri(bookDetail.getTaskPri());
            subjectBalance.setBtTaskPri(bookDetail.getTaskPri());
            subjectBalance.setOffsetDate(bookDetail.getOffsetDate());
            subjectBalance.setBookDate(bookDetail.getBookDate());
            subjectBalance.setBookOrder(bookDetail.getBookOrder());
            subjectBalance.setSubjectType(bookDetail.getSubjectType());
            bookDetail.setSubjectBalance(subjectBalance);
            // 添加到list中
            bookDetailList.add(bookDetail);
        }

        // list无数据，返回初始化值
        if (bookDetailList.isEmpty()) {
            return new BookGenerateResult();
        }

        result.setValBokBookdetailBatch(bookDetailList);

//        filterUnfairness(result);

        return result;
    }

    /**
     * 累计余额
     *
     * @param bookDetailTemp
     * @param accountEntry
     * @param bookDetail
     * @see
     * @since 1.0
     */
    private void calBalance(AccountEntry accountEntry,ValBokBookdetailBatch bookDetail, ValBokBookdetailBatch bookDetailTemp) {
        calBalance(bookDetail, accountEntry.getBalFlag(), accountEntry.getCdFlag(),
            bookDetailTemp);
    }
    
    /**
     * 累计余额
     *
     * @param bookDetail
     * @param balFlag
     * @param cdFlag
     * @param bookDetailTemp
     * @see
     * @since 1.0
     */
    public void calBalance(ValBokBookdetailBatch bookDetail, String balFlag, String cdFlag, ValBokBookdetailBatch bookDetailTemp) {
        // 获取历史科目余额 科目余额-当前表
        // 设值查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        // 账套代码
        searchDto.setBooksetId(bookDetail.getBooksetId());
        // 账务明细代码
        searchDto.setBokDetailId(bookDetail.getBokDetailId());
        // 科目号
        searchDto.setSubjectNo(bookDetail.getSubjectNo());
        // 入账日
        searchDto.setBookDate(bookDetail.getBookDate());
        // 发生日
        searchDto.setHappenDate(bookDetail.getHappenDate());
        // 基础科目性质
        searchDto.setBaseSubjectNatureList(Arrays.asList(bookDetail.getBaseSubjectNature()));
        // 任务优先级
        searchDto.setTaskPri(bookDetail.getTaskPri());
        // 分支序号
        searchDto.setBranch(bookDetail.getBranch());
        // 科目号
        searchDto.setSubjectNo(bookDetail.getSubjectNo());
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectLatestBalance(
            searchDto);
        Map<String, Object> mapHappenDate = null;
        if (bookDetailTemp != null) {
            mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
                    bookDetailTemp.getBalFlagHappenDate(), bookDetailTemp.getoAmtHappenDate(),
                    bookDetailTemp.getNumAmtHappenDate(), bookDetail.getCdFlag(),
                    bookDetail.getoHappenAmt(), bookDetail.getHappenNumber(),
                    /**MODIFY BY WUJIE ON 20190724 START
                     * null,null
                     */
                    bookDetailTemp.getbAmtHappenDate(),bookDetail.getbHappenAmt()
                    /**MODIFY BY WUJIE ON 20190724 END**/
            		);
        }
        else if (subjectBalanceList == null || subjectBalanceList.isEmpty()) {
            mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
                    cdFlag, BigDecimal.ZERO, BigDecimal.ZERO, bookDetail.getCdFlag(),
                    bookDetail.getoHappenAmt(), bookDetail.getHappenNumber(),
                    /**MODIFY BY WUJIE ON 20190724 START
                     * null,null
                     */
                    BigDecimal.ZERO,bookDetail.getbHappenAmt()
                    /**MODIFY BY WUJIE ON 20190724 END**/
            		);
        }
        else {
            SubjectBalance subjectBalance = subjectBalanceList.get(0);
            mapHappenDate = accountingCommonService.balanceDirectDealAccrualAddBalance(balFlag,
                    subjectBalance.getBalFlagHappenDate(), subjectBalance.getBalance(),
                    subjectBalance.getNum(), bookDetail.getCdFlag(), bookDetail.getoHappenAmt(),
                    bookDetail.getHappenNumber(),
                    /**MODIFY BY WUJIE ON 20190724 START
                     * null,null
                     */
                    subjectBalance.getbAmt(),bookDetail.getbHappenAmt()
                    /**MODIFY BY WUJIE ON 20190724 END**/
            		);
        }
        // 余额
        bookDetail.setoAmtHappenDate(
                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("balance"), NumberEnum.MONEY));
        /**MODIYF BY WUJIE ON 20190724 START**/
        // 本币余额
        bookDetail.setbAmtHappenDate(
                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("benbalance"), NumberEnum.MONEY));
        /**MODIYF BY WUJIE ON 20190724 END**/
        // 数量
        bookDetail.setNumAmtHappenDate(
                ArithmeticUtil.round((BigDecimal)mapHappenDate.get("num"), NumberEnum.MONEY));
        // 余额方向
        bookDetail.setBalFlagHappenDate((String)mapHappenDate.get("balDirect"));
    }
    
    /**
     * 非标外科目借贷不平过滤方法
     *
     * @param result 记账凭证主表及明细信息
     * @see
     * @since 1.0
     */
//    private static void filterUnfairness(BookGenerateResult result) {
//        List<ValBokBookdetailBatch> details = result.getValBokBookdetailBatch();
//        Map<String, BigDecimal> filterMap = new HashMap<>();
//        for (ValBokBookdetailBatch detail : details) {
//            if (!DictCodeList.科目类型_表外科目.getValue().equals(detail.getSubjectType())) {
//                String ccy = detail.getoCcy();
//                if (DictCodeList.借贷方向_借.getValue().equals(detail.getCdFlag())) {
//                    filterMap.put(ccy,
//                        ArithmeticUtil.doAdd(filterMap.get(ccy), detail.getoHappenAmt()));
//                }
//                else {
//                    filterMap.put(ccy,
//                        ArithmeticUtil.doSub(filterMap.get(ccy), detail.getoHappenAmt()));
//                }
//            }
//        }
//        boolean unfairnessFlag = false;
//        Iterator<String> it = filterMap.keySet().iterator();
//        while (it.hasNext()) {
//            if (!ArithmeticUtil.equals(filterMap.get(it.next()), BigDecimal.ZERO)) {
//                unfairnessFlag = true;
//                break;
//            }
//        }
//        if (unfairnessFlag) {
//            result.setValBokBookdetailBatch(null);
//        }
//        // if (!handan) {
//        // BokKeepaccountErr err = new BokKeepaccountErr();
//        // BeanUtil.copyPropertiesIgnoreNull(err, result.getBokKeepaccount());
//        // err.setIsCorrect(DictCodeList.凭证处理状态_未处理.getValue());
//        // result.setBokKeepaccountErr(err);
//        // List<BokKeepaccountdetailErrSelf> errList = new ArrayList<>();
//        // for (BokKeepaccountdetailBatch detail : result.getBokKeepaccountdetail()) {
//        // BokKeepaccountdetailErrSelf errDetail = new BokKeepaccountdetailErrSelf();
//        // BeanUtil.copyPropertiesIgnoreNull(errDetail, detail);
//        // errList.add(errDetail);
//        // }
//        // result.setBokKeepaccountdetailErr(errList);
//
//    }

    /**
     * 从场景树节点中获取记账分录信息
     *
     * @param treeNode 树
     * @param accountSceneMap 场景要素信息
     * @param accountEntrys 会计分录配置信息
     * @see
     * @since 1.0
     */
    private void getAccountEntryFromTreeNode(TreeNode treeNode,
                                             Map<String, Object> accountSceneMap,
                                             List<AccountEntry> accountEntrys) {
        if (treeNode == null) {
            return;
        }
        // 1 获取节点上的分录信息
        List<AccountEntry> list = treeNode.getAccountEntrys();
        if (list != null && !list.isEmpty()) {
            accountEntrys.addAll(list);
        }

        // 2 获取下级节点值域
        String childrenRange = treeNode.getChildrenRange();

        // 3 根据步骤2中的下级节点值域获取分支的下级节点
        TreeNode treeNode_child = treeNode.getChildByNodeValue(
            (String)accountSceneMap.get(childrenRange));
        if (treeNode_child == null) {
            treeNode_child = treeNode.getChildByNodeValue("OTHERBRANCH");
        }
        // 4 如果下级节点存在的话，迭代调用
        if (treeNode_child != null) {
            getAccountEntryFromTreeNode(treeNode_child, accountSceneMap, accountEntrys);
        }
    }

    /**
     * 插入记账凭证主表信息 和 插入记账凭证明细表信息（针对账务明细当前表）
     *
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    public int insertKeepAccount(BookGenerateResult result) {
        int count = 0;
        if (result == null) {
            return count;
        }
        if (result.getValBokBookdetailBatch() != null
            && !result.getValBokBookdetailBatch().isEmpty()) {
//            List<BokSubjectDetail> subjectDetails = getValBokSubjectDetail(result);
//            insertValBokSubjectDetail(subjectDetails);
            List<BokBussDetsub> bussDetsubs = getValBokBussDetsub(result);
            insertValBokBussDetsub(bussDetsubs);
            
            insertValBokBookdetailCurrent(result.getValBokBookdetailBatch());
            
            count = result.getValBokBookdetailBatch().size();
        }
        return count;
    }

    /**
     * 插入记账凭证主表信息 和 插入记账凭证明细表信息 （针对账务明细历史表）
     *
     * @param result BookGenerateResult
     * @return 插入的数量
     * @see
     * @since 1.0
     */
    public int insertKeepAccountForAll(BookGenerateResult result) {
        int count = 0;
        if (result == null) {
            return count;
        }
        if (result.getValBokBookdetailBatch() != null
            && !result.getValBokBookdetailBatch().isEmpty()) {
//            List<BokSubjectDetail> subjectDetails = getValBokSubjectDetail(result);
//            insertSD(subjectDetails);
//            
//            List<BokBussDetsub> bussDetsubs = getValBokBussDetsub(result);
//            insertBD(bussDetsubs);
            insertValBokBookdetailAll(result.getValBokBookdetailBatch());
            count = result.getValBokBookdetailBatch().size();
        }
        return count;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param valBokBookdetailBatch
     * @see
     * @since 1.0
     */
    public void insertValBokBookdetailCurrent(List<ValBokBookdetailBatch> valBokBookdetailBatch) {
        if (valBokBookdetailBatch == null || valBokBookdetailBatch.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ValBokBookdetailBatch>> list = DataHandleUtil.splitList(valBokBookdetailBatch);
        for (List<ValBokBookdetailBatch> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                baseAccountWriterMapper.insertValBokBookdetailCurrent(listInsert);
            }
        }
//        List<ValBokBookdetailBatch> listInsert = new ArrayList<>();
//        valBokBookdetailBatch.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                baseAccountWriterMapper.insertValBokBookdetailCurrent(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            baseAccountWriterMapper.insertValBokBookdetailCurrent(listInsert);
//        }
//        baseAccountWriterMapper.insertValBokBookdetailCurrent(
//            result.getValBokBookdetailBatch());
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param valBokBookdetailBatch
     * @see
     * @since 1.0
     */
    public void insertValBokBookdetailAll(List<ValBokBookdetailBatch> valBokBookdetailBatch) {
        if (valBokBookdetailBatch == null || valBokBookdetailBatch.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ValBokBookdetailBatch>> list = DataHandleUtil.splitList(valBokBookdetailBatch);
        for (List<ValBokBookdetailBatch> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                baseAccountWriterMapper.insertValBokBookdetailAll(listInsert);
            }
        }
//        List<ValBokBookdetailBatch> listInsert = new ArrayList<>();
//        valBokBookdetailBatch.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                baseAccountWriterMapper.insertValBokBookdetailAll(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            baseAccountWriterMapper.insertValBokBookdetailAll(listInsert);
//        }
//      baseAccountWriterMapper.insertValBokBookdetailAll(result.getValBokBookdetailBatch());
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param bussDetsubs
     * @see
     * @since 1.0
     */
    private void insertValBokBussDetsub(List<BokBussDetsub> bussDetsubs) {
        if (bussDetsubs == null || bussDetsubs.isEmpty()) {
            return;
        }
        List<List<BokBussDetsub>> list = DataHandleUtil.splitList(bussDetsubs);
        for (List<BokBussDetsub> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
//                baseAccountWriterMapper.insertValBokBussDetsub(listInsert);
                baseAccountWriterMapper.mergeValBokBussDetsub(listInsert);
            }
        }
//        List<BokBussDetsub> listInsert = new ArrayList<>();
//        bussDetsubs.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                baseAccountWriterMapper.insertValBokBussDetsub(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            baseAccountWriterMapper.insertValBokBussDetsub(listInsert);
//        }
        // baseAccountWriterMapper.insertValBokBussDetsub(bussDetsub);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param subjectDetails
     * @see
     * @since 1.0
     */
//    private void insertValBokSubjectDetail(List<BokSubjectDetail> subjectDetails) {
//        if (subjectDetails == null || subjectDetails.isEmpty()) {
//            return;
//        }
//        List<List<BokSubjectDetail>> list = DataHandleUtil.splitList(subjectDetails);
//        for (List<BokSubjectDetail> listInsert : list) {
//            if (CollectionUtils.isNotEmpty(listInsert)) {
//                baseAccountWriterMapper.insertValBokSubjectDetail(listInsert);
//            }
//        }
////        List<BokSubjectDetail> listInsert = new ArrayList<>();
////        subjectDetails.forEach(x -> {
////            // 小于最大值
////            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
////                listInsert.add(x);
////            }
////            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
////                baseAccountWriterMapper.insertValBokSubjectDetail(listInsert);
////                listInsert.clear();
////                listInsert.add(x);
////            }
////        });
//        // 如果循环结束，小于最大值
////        if (CollectionUtils.isNotEmpty(listInsert)) {
////            baseAccountWriterMapper.insertValBokSubjectDetail(listInsert);
////        }
//        // baseAccountWriterMapper.insertValBokSubjectDetail(subjectDetails);
//    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @see
     * @since 1.0
     */
//    private List<BokSubjectDetail> getValBokSubjectDetail(BookGenerateResult result) {
//        Set<ValBokSubjectDetailMem> subjectDetailMems = CacheUtil.getAppCache(
//            GlobalConstant.DETAIL_SUBJECT, Set.class);
//        List<BokSubjectDetail> subjectDetails = new ArrayList<>();
//        BokSubjectDetail subjectDetail = null;
//        ValBokSubjectDetailMem subjectDetailMem = null;
//        for (ValBokBookdetailBatch bookDetail : result.getValBokBookdetailBatch()) {
//            if (bookDetail.getValBokSubjectDetail() == null) {
//                continue;
//            }
//            subjectDetailMem = new ValBokSubjectDetailMem();
//            subjectDetail = bookDetail.getValBokSubjectDetail();
//            subjectDetailMem.setBooksetId(subjectDetail.getBooksetId());
//            subjectDetailMem.setBaseSubjectNature(subjectDetail.getBaseSubjectNature());
//            subjectDetailMem.setBokDetailId(subjectDetail.getBokDetailId());
//            if (!subjectDetailMems.contains(subjectDetailMem)) {
//                subjectDetails.add(subjectDetail);
//                subjectDetailMems.add(subjectDetailMem);
//            }
//        }
//        return subjectDetails;
//    }

    /**
     * <一句话功能简述> <功能详细描述>
     *
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    private synchronized List<BokBussDetsub> getValBokBussDetsub(BookGenerateResult result) {
        Set<ValBokBussDetsubMem> bussDetsubMems = CacheUtil.getAppCache(GlobalConstant.BUSS_DETSUB,
            Set.class);
        List<BokBussDetsub> bussDetsubs = new ArrayList<>();
        //add by yinya 去重处理后得到最终的结果集
        List<BokBussDetsub> resultList = new ArrayList<>();
        BokBussDetsub bussDetsub = null;
        ValBokBussDetsubMem bussDetsubMem = null;
        for (ValBokBookdetailBatch bookDetail : result.getValBokBookdetailBatch()) {
            if (bookDetail.getValBokBussDetsub() == null) {
                continue;
            }
            bussDetsubMem = new ValBokBussDetsubMem();
            bussDetsub = bookDetail.getValBokBussDetsub();
            bussDetsubMem.setBooksetId(bussDetsub.getBooksetId());
            bussDetsubMem.setDetailSubjectNo(bussDetsub.getDetailSubjectNo());
            if (!bussDetsubMems.contains(bussDetsubMem)) {
                bussDetsubs.add(bussDetsub);
                bussDetsubMems.add(bussDetsubMem);
            }
        }
        //add by yinya 去重处理
        bussDetsubs.stream(). collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getBooksetId()+f.getDetailSubjectNo()))), ArrayList::new))
                .forEach(f->resultList.add(f));
        return resultList;
    }

    /**
     * 更新账务明细（历史表）
     *
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    public int updateValBokBookDetailAll(BookGenerateResult result) {
        int count = 0;

        if (result == null) {
            return count;
        }

        // 获取账务明细历史数据
        List<ValBokBookdetailBatch> valBokBookdetailBatchList = result.getValBokBookdetailBatch();

        // 进行批量更新
        for (ValBokBookdetailBatch vo : valBokBookdetailBatchList) {
            baseAccountWriterMapper.updateValBokBookDetailAll(vo);
            count++ ;
        }

        return count;
    }

    /**
     * 更新账务明细（当前表）
     *
     * @param result
     * @return 个数
     * @see
     * @since 1.0
     */
    public int updateValBokBookDetailCurrent(BookGenerateResult result) {
        int count = 0;

        if (result == null) {
            return count;
        }

        // 获取账务明细历史数据
        List<ValBokBookdetailBatch> valBokBookdetailBatchList = result.getValBokBookdetailBatch();

        // 进行批量更新
        for (ValBokBookdetailBatch vo : valBokBookdetailBatchList) {
            baseAccountWriterMapper.updateValBokBookDetailCurrent(vo);
            count++ ;
        }

        return count;
    }

    /**
     * 生成明细科目
     *
     * @param accountSceneMap 场景要素信息
     * @param accountEntry 会计分录配置信息
     * @param valBokDetailId 账务明细
     * @see
     * @since 1.0
     */
    public BokSubjectDetail genDetailSubjectCode(Map<String, Object> accountSceneMap,
                                                    AccountEntry accountEntry,
                                                    BokDetailId valBokDetailId) {
        if (valBokDetailId == null
            || StringUtil.isEmpty(valBokDetailId.getDetailSubjectNoTail())) {
            return null;
        }
        // 组装明细科目号
        BokSubjectDetail valBokSubjectDetail = new BokSubjectDetail();
        // 账套代码
        valBokSubjectDetail.setBooksetId(accountEntry.getBooksetId());
        // 账务明细代码
        valBokSubjectDetail.setBokDetailId(valBokDetailId.getBokDetailId());
        // 科目号
        valBokSubjectDetail.setSubjectNo(accountEntry.getSubjectId());
        // 基础科目性质
        valBokSubjectDetail.setBaseSubjectNature(accountEntry.getBaseSubjectNature());
        // 四级科目号
        valBokSubjectDetail.setDetailSubjectNo(
            accountEntry.getSubjectId() + ValStringUtil.getNotNullStr(
                valBokDetailId.getDetailSubjectNoTail()));
        // 四级科目号尾号
        valBokSubjectDetail.setDetailSubjectNoTail(valBokDetailId.getDetailSubjectNoTail());
        // 是否计息
        valBokSubjectDetail.setAccIntFlag(accountEntry.getAccIntFlag());
        // 是否允许透支
        valBokSubjectDetail.setOverdrawnFlag(accountEntry.getOverdrawnFlag());
        return valBokSubjectDetail;
    }

    /**
     * 生成明细科目
     *
     * @param accountSceneMap 场景要素信息
     * @param accountEntry 会计分录配置信息
     * @param valBokDetailId 账务明细
     * @see
     * @since 1.0
     */
    public BokBussDetsub genBokBussDetsub(Map<String, Object> accountSceneMap,
                                             AccountEntry accountEntry,
                                             BokDetailId valBokDetailId) {
        if (valBokDetailId == null || StringUtil.isEmpty(valBokDetailId.getDetailSubjectNoTail())) {
            return null;
        }
        // 组装明细科目号
        BokBussDetsub valBokBussDetsub = new BokBussDetsub();
        // 账套代码
        valBokBussDetsub.setBooksetId(valBokDetailId.getBooksetId());
        // 四级科目号
        valBokBussDetsub.setDetailSubjectNo(
            accountEntry.getSubjectId() + ValStringUtil.getNotNullStr(
                valBokDetailId.getDetailSubjectNoTail()));
        // 四级科目尾号
        valBokBussDetsub.setDetailSubjectNoTail(valBokDetailId.getDetailSubjectNoTail());
        // 四级科目尾号名称
        valBokBussDetsub.setDetailSubnameTail(valBokDetailId.getDetailSubnameTail());
        // 四级科目号名称
        valBokBussDetsub.setDetailSubjectName(
            accountEntry.getSubjectName() + ValStringUtil.getNotNullStr(
                valBokDetailId.getDetailSubnameTail()));
        // 上级科目号
        valBokBussDetsub.setFsubjectId(accountEntry.getSubjectId());
        return valBokBussDetsub;
    }

    
    /**
     * 生成明细科目
     *
     * @param accountSceneMap 场景要素信息
     * @param accountEntry 会计分录配置信息
     * @param valBokBookdetailBatch 账务明细
     * @see
     * @since 1.0
     */
    public BokBussDetsub genBokBussDetsub(ValBokBookdetailBatch valBokDetailId){
    	BokBussDetsub valBokBussDetsub=new BokBussDetsub();
    	AccountEntry accountEntry =new AccountEntry();
    	accountEntry.setSubjectId(valBokDetailId.getSubjectNo());
    	Map<String,Object> map=new HashMap<String,Object>();
    	map.put("subjectNo", valBokDetailId.getSubjectNo());
    	map.put("booksetId", valBokDetailId.getBooksetId());
    	List<BokSubject> boksubjectinfo=accountingCommonService.getSubjectInfo(map);
    	BokSubject bokSubject=new BokSubject();
    	if(boksubjectinfo.size()>0){
    		bokSubject=boksubjectinfo.get(0);
    	}
		if(DictCodeList.是否_是.getValue().equals(bokSubject.getGenDetsubFlag())){
	    	//根据当前账务明细代码、账套信息获取账务明细代码信息
	        List<BokDetailId> listValBokBookdetail = finProductCommonService.bookdetailInforQuery(valBokDetailId.getBooksetId(), valBokDetailId.getBokDetailId());
	        BokDetailId valBokBookdetail = new BokDetailId();
	        if (listValBokBookdetail != null && listValBokBookdetail.size() > 0) {
	            valBokBookdetail = listValBokBookdetail.get(0);
	        }
	        //生成明细科目
	        accountEntry.setSubjectName(bokSubject.getSubjectName());
	        valBokBussDetsub= this.genBokBussDetsub(new HashMap<String,Object>(),accountEntry, valBokBookdetail);
		}else{
            // 组装明细科目号
            // 账套代码
            valBokBussDetsub.setBooksetId(valBokDetailId.getBooksetId());
            // 上级科目号
            valBokBussDetsub.setFsubjectId(accountEntry.getFsubjectId());
		}
    	return valBokBussDetsub;

    }
}
