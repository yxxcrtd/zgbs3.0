/*
 * 文件名：MoneyFundDealCountingDrawingProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年7月10日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.moneyfunddealcountingdrawing.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
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
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.moneyfunddealcountingdrawing.dao.MoneyFundDealCountingDrawingMapper;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPayRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 货币基金计提-账务明细批处理 - process逻辑处理
 * 
 * @author yangjian
 * @version 1.0 2018年7月10日
 * @see MoneyFundDealCountingDrawingProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class MoneyFundDealCountingDrawingProcessorNewService {

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    @Autowired
    private MoneyFundDealCountingDrawingMapper moneyFundDealCountingDrawingMapper;

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
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数或计提基础信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListDrawBaseInfoSelectComDTO() == null
            || acctEntity.getListDrawBaseInfoSelectComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        String busiId = acctEntity.getBusiId();// 业务明细代码
        // String invAimNow = acctEntity.getInvAim();// 投资目的
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPriNow = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（计提基础信息列表的任务优先级都是一致的，故取第一个）
        // 定义变量
        // BigDecimal costSjtAmtVar = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal interestBlance = BigDecimal.ZERO;// 变量.应计利息科目余额
        BigDecimal interestBlanceBccy = BigDecimal.ZERO;// 变量.本币应计利息科目余额
        // BigDecimal interestBlanceForYstVar = BigDecimal.ZERO;// 变量.应计利息前一日余额
        BigDecimal interestVar = BigDecimal.ZERO;// 变量.应计利息
        // 定义 变量.场景代码列表
        List<String> listTableId = new ArrayList<>();
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_认购申请.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_认购确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_认购返款.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_现金分红.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_红利再投.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_赎回确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_赎回到账.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购返款.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调增.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调减.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_返款利息确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_基金转换入确认交易.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_基金转换出确认交易.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金及货币型资产_基金拆分.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_手工凭证.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_申购确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_转仓买入.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_分红除权.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利发放.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_赎回到期.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_到期赎回到账.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_卖出转仓.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_场内申购.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_场内申购确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_场内赎回确认.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_场内赎回到账.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_买入_转仓买入划款.getValue());
        listTableId.add(DictCodeListTableId.会计场景代码_货币基金_卖出_转仓卖出到账.getValue());

        // 2.2.当日余额：应计利息科目余额
        /**MODIFY BY WUJIE ON 20200103 START
        interestBlance = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow,
            bookDateNow, DictCodeList.余额方向_借方.getValue(),
            DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, taskPriNow);
            **/
        interestBlance = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow,
                bookDateNow, DictCodeList.余额方向_借方.getValue(),
                DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, taskPriNow).getBalance();
		interestBlanceBccy = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow,
                bookDateNow, DictCodeList.余额方向_借方.getValue(),
                DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, taskPriNow).getbAmt();
        /**MODIFY BY WUJIE ON 20200103 END**/

        // 定义 变量.计算应计利息 = 0
        BigDecimal clsYjlx = BigDecimal.ZERO;

        // 获取标的信息.金融产品类型
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto1 = new TargetSearchDto();
        // 查询类型: 标的信息
        targetSearchDto1.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 生效日期 = 计提类账务处理结果.发生日
        targetSearchDto1.setEffdateStart(happenDateNow);
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto1);

        // 查询类型：现金流支付规则
        TargetSearchDto targetSearchDto2 = new TargetSearchDto();
        targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        // 生效日期 = 计提类账务处理结果.发生日
        targetSearchDto2.setEffdateStart(happenDateNow);
        targetSearchDto2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto2);
        // 调用共通【查询标的信息】
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(busiId,
            targetSearchDtoList);
        // 获取标的信息
        if (targetMap.get(targetSearchDto1) == null) {
            return null;
        }
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto1);
        // 获取支付规则
        List<TargetCashPayRule> listTargetCashPayRule = (List<TargetCashPayRule>)targetMap.get(
            targetSearchDto2);
        TargetCashPayRule targetCashPayRule = new TargetCashPayRule();
        if (!CollectionUtils.isEmpty(listTargetCashPayRule)) {
            targetCashPayRule = listTargetCashPayRule.get(0);
        }
        // 获取标的信息.境内外
        String blnAreaNow = targetFinProduct.getBlnArea();
        // 交易信息.分类1
        String type1Now = targetFinProduct.getType1();
        // 交易信息.分类2
        String type2Now = targetFinProduct.getType2();

        // 2.3.最新红利再投或分红交易查询
        List<BokBookdetailCurrent> listMdate = new ArrayList<>();
        if(DictCodeListExt1.金融产品类型_基金.getValue().equals(listDrawBaseInfoSelectComDTONow.get(0).getBusiDetailType())){
             listMdate = accountingCommonService.getNewestMdate(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收股利.getValue())),
                new ArrayList<String>(Arrays.asList(DictCodeListTableId.会计场景代码_货币基金及货币型资产_现金分红.getValue(),
                    DictCodeListTableId.会计场景代码_货币基金及货币型资产_红利再投.getValue())),
                null, acctEntity.getHappenDate(), null, null, acctEntity.getHappenDate());
        }else{
            listMdate = accountingCommonService.getNewestMdate(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应收股利.getValue())),
                new ArrayList<String>(Arrays.asList(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利发放.getValue(),
                    DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getValue())),
                null, acctEntity.getHappenDate(), null, null, acctEntity.getHappenDate());

        }
       
        // 变量.计提开始日 = 上述返回LIST.get（0）.结息日+1
        Date drawStartDate = null;
        if (listMdate != null && listMdate.size() > 0) {
            drawStartDate = DateUtils.addDays(listMdate.get(0).getMdate(), 1);
        }

        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {
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
            // 变量.账务明细辅助列表(每进一次循环进行一次重置)
            List<BokBookdetailCurrentExt> bokBookdetailCurrentExtList = new ArrayList<BokBookdetailCurrentExt>();
            
            //定义 变量.成本科目性质
            String baseSubject = DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue();;
//            if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getBusiDetailType())){
//                baseSubject =  DictCodeBaseSubjectNatrue.基金投资_成本.getValue();
//            }else{
//                baseSubject =  DictCodeBaseSubjectNatrue.计划类_货币型资产_成本.getValue();
//            }
            // 万分收益：调用共通方法【标的类共通方法 -万份收益-基金】获取;
            // 【注：因为计提process为相同账务明细代码和业务明细代码,也就是对一条数据进行计提，故循环放里面和外面一样】。
            List<FinProductValuation> valFinProductValuationList = finProductCommonService.getValFinProductValuationList(
                acctEntity.getBusiId(), targetFinProduct.getCalendarId(), happenDateNow,
                drawStartDate);
            // 定义 变量.估值日期
            Date valDate = null;
            // 定义 变量.首次Flag = true
            boolean firstFlag = true;
            // 定义 变量.账务日期
            Date accoutingDate = null;
            // 定义 变量.估值单位 = 10000 / (变量.标的信息.面值 IS NULL? 1 : 变量.标的信息.面值) 
            BigDecimal valUnit = ArithmeticUtil.divide(new BigDecimal(10000),targetFinProduct.getFaceValue() == null ? BigDecimal.ONE:targetFinProduct.getFaceValue()) ;

            if (!CollectionUtils.isEmpty(valFinProductValuationList)) {
                // 循环万分收益，获取应计利息
                for (FinProductValuation vo : valFinProductValuationList) {
                    // 定义-变量.账务明细辅助
                    BokBookdetailCurrentExt bokBookdetailCurrentExtNow = new BokBookdetailCurrentExt();

                    // 定义处理参数
                    Long taskPriForPa = null;
                    // 估值日期 = 参数.发生日时，传入处理参数.任务优先级，否则为空
                    if (vo.getValDate().compareTo(happenDateNow) == 0) {
                        taskPriForPa = taskPriNow;
                    }


                    // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    /**MODIFY BY WUJIE ON 20200103 START
                    // 定义 变量DTO.科目余额 = 返回值.科目余额
                    BigDecimal dtoBalance = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                            vo.getValDate(), bookDateNow, DictCodeList.余额方向_借方.getValue(),
                            baseSubject, null, taskPriForPa);
                            **/
                    SubjectBalance DTO = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                            vo.getValDate(), bookDateNow, DictCodeList.余额方向_借方.getValue(),
                            baseSubject, null, taskPriForPa);
                    BigDecimal dtoBalance = DTO.getBalance();
                    /**MODIFY BY WUJIE ON 20200103 END**/
                    
                    // 变量.计算基数 = DTO.科目余额 调用共通科目余额期待方向数据
                    BigDecimal calBasic =BigDecimal.ZERO;
                    //IF (计提明细.业务明细类型 = 基金){
                    if(DictCodeListExt1.金融产品类型_基金.getValue().equals(listDrawBaseInfoSelectComDTONow.get(0).getBusiDetailType())){
                        // 变量.计算基数 = DTO.数量余额
                    	/**MODIFY BY WUJIE ON 20200103 START
                         calBasic = dto.getNumAmtHappenDate();
                         **/
                    	calBasic = DTO.getNum();
                    	 /**MODIFY BY WUJIE ON 20200103 END**/
                    } else {
                       // 变量.计算基数 = DTO.科目余额
                    	/**MODIFY BY WUJIE ON 20200103 START
                         calBasic = dtoBalance;
                         **/
                    	calBasic = DTO.getBalance();
                    	/**MODIFY BY WUJIE ON 20200103 END**/
                    }


                        // 定义 变量.当日发生额 = 0
                    BigDecimal tdyHappenAmt = BigDecimal.ZERO;
                    // if 变量.支付规则.支付频率 = 数据字典.基金分红频率的 "日"
                    if (firstFlag) {
                        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->账务明细辅助查询SHEET
                        BokBookdetailCurrentExt bokBookdetailCurrentExt = accountingCommonService.queryBokBookdetailCurrentExt(
                            vo.getValDate(), bookDateNow, booksetIdNow, bokDetailIdNow);
                        if (StringUtil.isNotEmpty(bokBookdetailCurrentExt)) {
                            clsYjlx = bokBookdetailCurrentExt.getTdyIntincexp();
                            /**MODIFY BY WUJIE ON 20200102 START
                            accoutingDate = bokBookdetailCurrentExt.getHappenDate();
                            **/
                            accoutingDate = DateUtil.addDay(bokBookdetailCurrentExt.getAccrDate(), 1);
                            /**MODIFY BY WUJIE ON 20200102 END**/
                        }
                        else {
                            // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                            // 变量.计算应计利息 = DTO.原币发生额
                        	/**MODIFY BY WUJIE ON 20200103 START
                            clsYjlx = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                                DateUtil.addDay(happenDateNow, -1), bookDateNow,
                                DictCodeList.余额方向_借方.getValue(),
                                DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, null);
                                **/
                        	clsYjlx = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                                    DateUtil.addDay(happenDateNow, -1), bookDateNow,
                                    DictCodeList.余额方向_借方.getValue(),
                                    DictCodeBaseSubjectNatrueNew.应收股利.getValue(), null, null).getBalance();
                            if (ArithmeticUtil.equals(dtoBalance, BigDecimal.ZERO)) {
                            	clsYjlx = BigDecimal.ZERO;
                            	accoutingDate = vo.getValDate();
                            }
                            /**ADD BY WUJIE ON 20200103 END**/
                        }
                        firstFlag = false;
                    }
                    // if(变量.账务日期 != null and 万分收益信息Dto.估值日期 >= 变量.账务日期 )
                    if(accoutingDate != null && DateUtil.compareDate(vo.getValDate(), accoutingDate) >= 0){
                    	 /**ADD BY WUJIE ON 20200102 START**/
                    	Date startDate = accoutingDate;
                    	while(DateUtil.compareDate(vo.getValDate(), startDate) >= 0){
                    	/**ADD BY WUJIE ON 20200102 END**/
                    		// 估值日非计提List = 调用SHEET【估值日非计提数据查询】
                            Map<String, Object> params = new HashMap<>();
                            // sql语句固定值：借贷方向 -> 借
                            params.put("cdFlag", DictCodeList.借贷方向_借.getValue());
                            // sql语句固定值：冲正标识 -> 正常交易
                            params.put("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());
                            // 账套代码 = 计提类账务处理结果.账套代码
                            params.put("booksetId", booksetIdNow);
                            // 入账日 = 计提类账务处理结果.入账日
                            params.put("bookDate", bookDateNow);
                            /**MODIFY BY WUJIE ON 20200102 START
                            // 发生日 = 万分收益信息Dto.估值日期
                            params.put("happenDate", vo.getValDate());
                            **/
                            params.put("happenDate", startDate);
                            /**MODIFY BY WUJIE ON 20200102 END**/
                            
                            // 账务明细代码 = 计提类账务处理结果.账务明细代码
                            params.put("bokDetailId", bokDetailIdNow);
                            // 场景代码列表 = 变量.场景代码列表
                            params.put("listTableId", listTableId);
                            // 基础科目性质 = 应收红利
                            params.put("baseSubjectNature", DictCodeBaseSubjectNatrueNew.应收股利.getValue());
                            // 估值日非计提List
                            List<BokBookdetailCurrent> listBookdetailCurrent = moneyFundDealCountingDrawingMapper.queryValDateNoDrawData(
                                params);
                            // if（估值日非计提List != null or 估值日非计提List.size >0）
                            if (listBookdetailCurrent != null && listBookdetailCurrent.size() > 0) {
                                // 变量.当日发生额 = 估值日非计提List.get(0).发生额
                                tdyHappenAmt = listBookdetailCurrent.get(0).getoHappenAmt();
                            }
                    	/**ADD BY WUJIE ON 20200102 START**/
                    		startDate = DateUtil.addDay(startDate, 1);
                    	}
                    	/**ADD BY WUJIE ON 20200102 END**/
                    }
                    // modified by wangzhiang 【一体化改造】基金结转频率数据字典更换 201800413 start
                    if (DictCodeList.收益结转频率_按日结转.getValue().equals(targetCashPayRule.getPayCycle())
                        || (!DictCodeList.收益结转频率_按日结转.getValue().equals(
                            targetCashPayRule.getPayCycle()) && DictCodeList.是否_是.getValue().equals(
                                targetFinProduct.getIsCompoundInt()))) {
                        if(DictCodeListExt1.金融产品类型_基金.getValue().equals(listDrawBaseInfoSelectComDTONow.get(0).getBusiDetailType())){
                            // 变量.计算基数 = 变量.计算基数  + (变量.应计利息 +变量.计算应计利息 + 变量.当日发生额)/变量.单位面值 
                            calBasic = ArithmeticUtil.doAdd(calBasic,
                                    ArithmeticUtil.divide(ArithmeticUtil.doAdd(interestVar,
                                                    ArithmeticUtil.doAdd(clsYjlx,tdyHappenAmt)),
                                    		targetFinProduct.getFaceValue()==null?BigDecimal.ONE:targetFinProduct.getFaceValue()));
                        } else {
                            // 变量.计算基数 = 变量.计算基数  + 变量.应计利息 +变量.计算应计利息 + 变量.当日发生额
                            calBasic = ArithmeticUtil.doAdd(
                                            ArithmeticUtil.doAdd(calBasic,interestVar),
                                            ArithmeticUtil.doAdd(clsYjlx,tdyHappenAmt));
                        }
                    }
                    // 变量.应计利息 = 变量.应计利息 + 变量.计算基数*万分收益Dto.万分收益/变量.估值单位
                    interestVar = ArithmeticUtil.doAdd(interestVar,
                            ArithmeticUtil.divide(
                                    ArithmeticUtil.multiply(calBasic,vo.getValuation())
                                    ,valUnit));
                    // 变量.估值日期 = 万分收益信息Dto.估值日期
                    valDate = vo.getValDate();

                    // 账务明细辅助数据塞值，其他【同账务明细当前表同字段】数据在后续【变量.账务明细辅助列表】循环中进行处理
                    // 变量.账务明细辅助.流水 = 自动生成
                    bokBookdetailCurrentExtNow.setSeqno(
                        dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                    // 变量.账务明细辅助.计提日 = 变量.估值日期
                    bokBookdetailCurrentExtNow.setAccrDate(valDate);
                    // 变量.账务明细辅助.当日累计应计利息余额 = 变量.应计利息 + 变量.计算应计利息 + 变量.当日发生额
                    bokBookdetailCurrentExtNow.setTdyIntincexp(
                        ArithmeticUtil.doAdd(interestVar, clsYjlx, tdyHappenAmt));

                    // if（DTO.成本科目余额 = 0）
                    if (ArithmeticUtil.equals(dtoBalance, BigDecimal.ZERO)) {
                        // 变量.账务明细辅助.当日累计应计利息余额 = 0
                        bokBookdetailCurrentExtNow.setTdyIntincexp(BigDecimal.ZERO);
                    }
                    // 变量.账务明细辅助列表.add(变量.账务明细辅助)
                    bokBookdetailCurrentExtList.add(bokBookdetailCurrentExtNow);

                    // add by weipeng 20210807：增加资产行情信息入库
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
                    bokAssetMarketInfo.setMarketPrice(vo.getValuation());
                    // 变量.资产行情信息DTO.是否来源于行情数据
                    // if 市价共通返回.市价== null
                    if(vo == null || vo.getValuation() == null){
                        // 变量.资产行情信息DTO.行情日期 = 变量.发生日期
                        bokAssetMarketInfo.setValueDate(acctEntity.getHappenDate());
                        bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
                    }
                    else{
                        // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                        bokAssetMarketInfo.setValueDate(vo.getValDate());
                        bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
                    }

                    // 添加到返回值中
                    returnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
                }
            }

            // if（明细.成本科目余额 = 0）
            if (ArithmeticUtil.equals(dto.getoAmtHappenDate(), BigDecimal.ZERO)) {
                // 变量.应计利息 = 0
                interestVar = BigDecimal.ZERO;
            }

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
            if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getBusiDetailType())){
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_每日红利计提.getValue());// ad.会计场景代码
            }else{
                ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_每日计提红利.getValue());// ad.会计场景代码
            }
            

            // 记账场景要素map塞值,(调用共通方法-计提类场景元素赋值)
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            if(DictCodeListExt1.金融产品类型_基金.getValue().equals(dto.getBusiDetailType())){
                accountSceneMap.put(BookConstant.BOOK_REMARK, "货币基金每日红利计提");// 备注
            }else{
                accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类货币型资产每日红利计提");// 备注
            }
            accountSceneMap.put(BookConstant.BLN_AREA, blnAreaNow);// 境内外
            accountSceneMap.put(BookConstant.TYPE_1, type1Now);// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, type2Now);// 分类2 - 变量.交易信息.分类2

            // 金额元素塞值
            if (interestVar.compareTo(BigDecimal.ZERO) > 0) {
                accountAmountMap.put("INTAMT_D", interestVar);// 红利计提（借）- 变量.应计利息
                accountAmountMap.put("INTAMT1_C", interestVar);// 红利计提1（贷）- 变量.应计利息
            	//红利计提（借) _本位币    <-	红利计提（借) * 变量.汇率
                accountAmountMap.put("INTAMT_D" + BookConstant.BCCYAMT, calculationCommonService.doMultiply(
                		NumberEnum.MONEY.getCode(), null, interestVar, changeRate));
                //红利计提1（贷) _本位币  <-	红利计提（借) _本位币
                accountAmountMap.put("INTAMT1_C" + BookConstant.BCCYAMT, accountAmountMap.get("INTAMT_D" + BookConstant.BCCYAMT));
            }
            else {
                accountAmountMap.put("INTAMT_C", interestVar.abs());// 红利计提（贷）- 变量.应计利息*-1
                accountAmountMap.put("INTAMT1_D", interestVar.abs());// 红利计提1（借）- 变量.应计利息*-1
                //红利计提（贷) _本位币    <-	红利计提（贷) * 变量.汇率
                accountAmountMap.put("INTAMT_C" + BookConstant.BCCYAMT, calculationCommonService.doMultiply(
                		NumberEnum.MONEY.getCode(), null, interestVar, changeRate));
                //红利计提1（借) _本位币  <-	红利计提（贷) _本位币
                accountAmountMap.put("INTAMT1_D" + BookConstant.BCCYAMT, accountAmountMap.get("INTAMT_C" + BookConstant.BCCYAMT));
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
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 完善【账务明细辅助列表】中数据。
            // 账务明细（当前表）数据
            List<ValBokBookdetailBatch> ValBokBookdetailBatchList = resultNowNow.getValBokBookdetailBatch();
            if(bokBookdetailCurrentExtList != null && !bokBookdetailCurrentExtList.isEmpty()){
                // 定义新的账务明细辅助表列表数据，用于过滤。
                List<BokBookdetailCurrentExt> bokBookdetailCurrentExtListForFillter = new ArrayList<BokBookdetailCurrentExt>();

                if (ValBokBookdetailBatchList != null && !ValBokBookdetailBatchList.isEmpty()) {
                    // 变量.账务明细辅助列表 设置同账务明细当前表同字段
                    for (BokBookdetailCurrentExt vo : bokBookdetailCurrentExtList) {

                        // 备注：已经与设计确认，生成的借贷2条数据【应收红利】【基金利息收入】，取【应收红利】的那条数据，作为主数据。
                        ValBokBookdetailBatch valBokBookdetailBatchNow = null;
                        for (ValBokBookdetailBatch voNow : ValBokBookdetailBatchList) {
                            if (DictCodeBaseSubjectNatrueNew.应收股利.getValue().equals(
                                    voNow.getBaseSubjectNature())) {
                                valBokBookdetailBatchNow = voNow;
                            }
                        }

                        // 存在应收红利数据
                        if (valBokBookdetailBatchNow != null) {
                            // 变量.账套代码 = 同账务明细当前表同字段
                            vo.setBooksetId(valBokBookdetailBatchNow.getBooksetId());
                            // 变量.账务明细辅助.发生日期 = 同账务明细当前表同字段
                            vo.setHappenDate(valBokBookdetailBatchNow.getHappenDate());
                            // 变量.账务明细辅助.入账日期 = 同账务明细当前表同字段
                            vo.setBookDate(valBokBookdetailBatchNow.getBookDate());
                            // 变量.账务明细辅助.账套日期 = 同账务明细当前表同字段
                            vo.setBooksetDate(valBokBookdetailBatchNow.getBooksetDate());
                            // 变量.账务明细辅助.账务明细流水 = 同账务明细当前表【流水】
                            vo.setfSeqno(valBokBookdetailBatchNow.getSeqno());
                            // 变量.账务明细辅助.处理序列 = 同账务明细当前表同字段
                            vo.setBookSummaryOrder(valBokBookdetailBatchNow.getBookOrder());
                            // 变量.账务明细辅助.账务明细代码 = 同账务明细当前表同字段
                            vo.setBokDetailId(valBokBookdetailBatchNow.getBokDetailId());
                            // 变量.账务明细辅助.业务明细代码 = 同账务明细当前表同字段
                            vo.setBusiId(valBokBookdetailBatchNow.getBusiId());
                            // 符合条件的数据，保留。
                            bokBookdetailCurrentExtListForFillter.add(vo);
                        }
                    }
                }
                else{
                    // 变量.账务明细辅助列表 设置同账务明细当前表同字段
                    for (BokBookdetailCurrentExt vo : bokBookdetailCurrentExtList) {
                        // 变量.账套代码 = 账套代码
                        vo.setBooksetId(dto.getBooksetId());
                        // 变量.账务明细辅助.发生日期 = 发生日期
                        vo.setHappenDate(dto.getHappenDate());
                        // 变量.账务明细辅助.入账日期 = 入账日期
                        vo.setBookDate(bookDateNow);
                        // 变量.账务明细辅助.账套日期 = 发生日期
                        vo.setBooksetDate(dto.getBooksetDate());
                        // 变量.账务明细辅助.账务明细流水 = 自动生成
                        vo.setfSeqno(
                                dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
                        // 变量.账务明细辅助.处理序列 = 自动生成
                        vo.setBookSummaryOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
                        // 变量.账务明细辅助.账务明细代码 = 账务明细代码
                        vo.setBokDetailId(dto.getBokDetailId());
                        // 变量.账务明细辅助.业务明细代码 = 业务明细代码
                        vo.setBusiId(dto.getBusiId());
                        // 符合条件的数据，保留。
                        bokBookdetailCurrentExtListForFillter.add(vo);
                    }
                }

                // 重新保留，符合条件的数据
                bokBookdetailCurrentExtList = bokBookdetailCurrentExtListForFillter;

                // 账务明细辅助列表添加到返回值
                returnResult.getBokBookdetailCurrentExtList().addAll(bokBookdetailCurrentExtList);
            }

            // 3.3 统计分析账务明细汇总信息赋值
            // 调用共通：统计分析账务明细汇总查询
            // 变量.当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = accountingCommonService
                    .getCountAnalysis(acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
            if(bokSummaryTableNew == null) {
                bokSummaryTableNew = new BokStatDetPosiiton();
                // 变量.当日统计分析账务明细汇总信息.账套代码 = 计提类账务处理结果.账套代码
                bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
                // 变量.当日统计分析账务明细汇总信息.发生日期 = 计提类账务处理结果.发生日期
                bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
                // 变量.当日统计分析账务明细汇总信息.入账日期 = 计提类账务处理结果.入账日期
                bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
                // 变量.当日统计分析账务明细汇总信息.账务明细代码 = 计提类账务处理结果.账务明细代码
                bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
                // 变量.当日统计分析账务明细汇总信息.处理序列 = 调用序列生成共通
                bokSummaryTableNew.setBookSummaryOrder(
                        new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
                // 变量.当日统计分析账务明细汇总信息.账套日期 = 计提类账务处理结果.账套日期
                bokSummaryTableNew.setBooksetDate(listDrawBaseInfoSelectComDTONow.get(0).getBooksetDate());
                // 变量.当日统计分析账务明细汇总信息.币种 = 变量.标的信息.币种
                bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
                // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
                // 变量.当日统计分析账务明细汇总信息.本位币 = 计提类账务处理结果.本位币
                bokSummaryTableNew.setbCcy(listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy());
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
                bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
                // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
                bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
                // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
                bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
            }
            // 定义：变量.计提利率
            BigDecimal jTrate = BigDecimal.ZERO;
            if (valFinProductValuationList != null) {
                for (FinProductValuation rule : valFinProductValuationList) {
                    // 报价.生效日 > 计提类账务处理结果.发生日
                    if (rule.getValDate().compareTo(acctEntity.getHappenDate()) > 0) {
                        break;
                    }

                    // 变量.计提利率 = 报价temp.生效利率
                    jTrate = rule.getValuation();
                }
            }
            // 定义当日统计分析账务明细汇总信息
//            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(dto.getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(jTrate);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(interestVar);
            // // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // interestVar);
            // }
            // else {
            // tdyintincexpNow = interestVar;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息科目余额 + 变量.应计利息
            BigDecimal tdyintincexpNow = ArithmeticUtil.doAdd(interestBlance, interestVar);
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(dto.getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
            bokSummaryTableNew.setTdyCostAmt(dto.getoAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(bokDetailIdNow);
            // 账套日期
            bokSummaryTableNew.setBooksetDate(dto.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 起息日
            // bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
            // 到期日
            // bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
            // 变量.当日统计分析账务明细汇总信息.行情日期 = 变量.估值日期
            bokSummaryTableNew.setValDate(valDate);
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            // bokSummaryTableNew.setBusiId(acctEntity.getBusiId());

            /** modify by yanya on 20190924 start **/
            bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
            /** modify by yanya on 20190924 end **/

            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            //本位币
            // bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 变量.当日统计分析账务明细汇总信息.当日成本_本位币 = 变量.当日成本科目余额*变量.汇率
            bokSummaryTableNew.setTdyCostAmtB(
                    calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, dto.getoAmtHappenDate(), changeRate));
            // 当日成本_本位币
            // bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end
            
            //本位币
            // bokSummaryTableNew.setbCcy(dto.getStandardCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, interestVar, changeRate));
            //当日累计应计利息余额_本位币
            BigDecimal tdyintincexpNowBccy = ArithmeticUtil.doAdd(interestBlanceBccy, calculationCommonService.doMultiply(interestVar, changeRate));
            bokSummaryTableNew.setTdyIntincexpAddB(ArithmeticUtil.round(tdyintincexpNowBccy, NumberEnum.MONEY.getCode(), null));
            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);

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
     * @return 返回值类型由BigDecimal 改为 SubjectBalance MODIFY BY WUJIE ON 20200103
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, String invAim, Long taskPri) {
    	/**MODIFY  BY WUJIE ON 20200103 START
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;
        **/
        SubjectBalance vo = new SubjectBalance();
        /**ADD BY WUJIE ON 20200103 END**/

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

        /**MODIFY  BY WUJIE ON 20200103 START
        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getBalance();
        }
        return balance;
        **/
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            vo = subjectBalanceList.get(0);
        }
        return vo;
        /**ADD BY WUJIE ON 20200103 END**/
    }

}
