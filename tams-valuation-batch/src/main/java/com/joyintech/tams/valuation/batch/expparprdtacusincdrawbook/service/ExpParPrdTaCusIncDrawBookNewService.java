/*
 * 文件名：ExpParPrdTaCusIncDrawBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：lihuadong
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrent;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.expparprdtacusincdrawbook.dao.ExpParPrdTaCusIncDrawBookMapper;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预期收益型母产品TA客户收益计提入账 Processor 的 Service
 * 
 * @author lihuadong
 * @version 1.0 2018年9月29日
 * @see ExpParPrdTaCusIncDrawBookNewService
 * @since 1.0
 */
@Service
public class ExpParPrdTaCusIncDrawBookNewService {

    /**
     * 标的类共通
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

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
    /**
     * 记账引擎
     */
    @Autowired
    private ExpParPrdTaCusIncDrawBookMapper expParPrdTaCusIncDrawBookMapper;

    @Autowired
    private BookAccountCommonService bookAccountCommonService;
    /**
     * process 数据处理
     * 
     * @param params 参数
     * @return BookGenerateResult
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResult execute(Map<String, Object> params) {
        // 获取reader返回值
        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)params.get(GlobalConstant.READER_RESULT); 
        // 若reader传递的参数为 null，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        //调用共通方法：账套参数查询
        List<String> paramCodeList = Lists.newArrayList();
        //产品利息计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品利息计提方式.getValue());
        //产品是否本系统计提
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品是否本系统计提.getValue());
        //获取账务明细代码
        List<String> bokDetailIdList=Lists.newArrayList();
        
        bokDetailIdList.add(acctEntity.getBokDetailId());
        //赋值当前账套代码
        params.put("booksetId", acctEntity.getBooksetId());
        // 账套参数查询
        Map<String, Object>  booksetExt = accountingCommonService.getValBokBooksetExtForMap(acctEntity.getBooksetId(),paramCodeList);
        //帐套配置信息.产品是否本系统计提=否
        if(StringUtil.equals(booksetExt.get(ValDictCodeList.账套配置表参数项_产品是否本系统计提.getValue()), DictCodeList.是否_否.getValue())){
            return null;
        }   
        
        //产品利息计提方式
        Object calType = booksetExt.get(ValDictCodeList.账套配置表参数项_产品利息计提方式.getValue());
        BookGenerateResult rtnResult = new BookGenerateResult(); // 此 process 返回值

        if (rtnResult.getValBokBookdetailBatch() == null) {// 账务明细若为null赋初始值
            rtnResult.setValBokBookdetailBatch(Lists.newArrayList());
        }

        // 获取从 task 拿到的参数
        SysBatchExecInfo batchExecInfo = (SysBatchExecInfo)params.get("BATCHINFO"); // 待处理任务信息
        String taskId = batchExecInfo.getTaskId();
        Date happenDate = (Date)params.get(BookConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        Date mdateNow = happenDate; // 本次计提结束日
        
        
        // 设计变更：到期日(包含)之后或起息日之前不计提
        // 当【DTO.到期日 <= 批处理参数.发生日 or DTO.起息日 > 批处理参数.发生日】，进行后续处理
        if(acctEntity.getActmdate().compareTo(happenDate) <= 0 || acctEntity.getVdate().compareTo(happenDate) > 0){
            return null;
        }   
        

        // 标的信息查询
        Map<String, Object> targetInfoMap = this.getTargetInfo(acctEntity, happenDate);

        // 标的——现金流计算规则
        List<TargetCashCalcRule> targetCashCalcRuleList = (List<TargetCashCalcRule>)targetInfoMap.get(
            ValDictCodeList.标的_现金流计算规则.getValue());

      
        Date vdateNow; // 本次计提开始日
        Date calDate;//计提日

        // if（变量.计提方式 = 【数据字典】.计提方式."区间倒减"）
        if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
            //变量.计提开始日 = 上述返回LIST.get（0）.结息日+1
            //最新结息日查询
            List<BokBookdetailCurrent> listMdate = accountingCommonService.getNewestMdate(acctEntity.getBooksetId(),
                    acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应付利润.getValue())),
                    new ArrayList<String>(Arrays.asList(DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getValue())),
                    null, happenDate, null, null, happenDate);
            if(StringUtil.isEmptyList(listMdate)){
                // 本次计提开始日 = DTO.起息日
                vdateNow = acctEntity.getVdate(); 
            }else{
                BokBookdetailCurrent current=listMdate.get(0);
                if(StringUtil.isNotEmpty(current.getMdate())){
                    vdateNow=current.getMdate();
                }else{
                 // 本次计提开始日 = DTO.起息日
                    vdateNow = acctEntity.getVdate(); 
                }
            }


        }
        else {
            vdateNow = happenDate; // 本次计提开始日 = 批处理参数.发生日
        }

        // 客户收益计提
        // 客户收益计提- 1）利率变更列表处理
        BigDecimal yield = BigDecimal.ZERO; // 生效利率
        QuoteInfo quoteInfo = new QuoteInfo(); // 利率变更
        List<QuoteInfo> quoteInfoList = Lists.newArrayList(); // 利率变更列表
        for (TargetCashCalcRule rule : targetCashCalcRuleList) {
            if(StringUtil.isNotEmpty(rule.getYield())){
                if (yield.compareTo(rule.getYield()) != 0) {
                    quoteInfo = new QuoteInfo(); // 定义利率变更。
                    quoteInfo.setIssueDate(rule.getEffDate());
                    quoteInfo.setInterest(rule.getYield());
                    quoteInfo.setAccountBasis(rule.getBasis());
                    yield = rule.getYield();
                    quoteInfoList.add(quoteInfo);
                   
                }
            }
        }
        // 客户收益计提 - 2）计费基数变更列表处理
        List<PrincipalInfo> principalInfoList = Lists.newArrayList();// 计费基数变更列表
        BigDecimal feeBaseLast = BigDecimal.ZERO;// 上次计费基数
        PrincipalInfo principalInfo = new PrincipalInfo();// 计费基数变更
        // String baseDateType = DictCodeList.基数日期类型_当日.getValue();// 计费基数日期类型
        String feebaseType = DictCodeList.基数类型_实收资本.getValue();// 计费基础类型 - 预期收益型产品固定为当日实收资本
        BokStatBokPositionDto position=new BokStatBokPositionDto();
        //查询日开始
        position.setSdateStart(vdateNow);
        //查询结束日
        position.setSdateEnd(mdateNow);
        //入账日
        position.setBookDate( acctEntity.getBookDate());
        //账套
        position.setBooksetId(acctEntity.getBooksetId());
        //分层代码 = 分层代码
        position.setLayeringId(acctEntity.getLayeringId());
        //业务明细代码
//        position.setFinprodId(acctEntity.getFinprodId());
        // 帐套资产负债信息集合 = 调用共通方法查询：帐套资产负债查询（本次计提开始日，本次计提结束日，DTO.帐套代码，DTO.金融产品代码）
/*        List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(
            vdateNow, mdateNow, null, acctEntity.getBooksetId(), null, acctEntity.getFinprodId());
*/
        List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(position);

        Map<Date, BokStatBokPosition> valStatBokPositionMap = Maps.newHashMap();
        for (BokStatBokPosition item : valStatBokPositionList) {// 以发生日作为key的帐套资产负债信息
            valStatBokPositionMap.put(item.getHappenDate(), item);
        }

        // FOR(计提日 <- 本次计提开始日 ： 本次计提结束日)
        calDate = vdateNow; // 计提日：出事赋值本次计提开始日
        do {
            // 调用【详细设计-计算类共通方法设计】产品费用计提基数计算（根据发生日=计提日匹配）
            BigDecimal feeBase = calculationCommonService.calProductFee(feebaseType,
                valStatBokPositionMap.get(calDate), acctEntity.getBooksetId(),
                acctEntity.getBookDate(), acctEntity.getStandardCcy(), happenDate, happenDate,
                acctEntity.getTaskPri(),bokDetailIdList);

            if (feeBaseLast != null && feeBaseLast.compareTo(feeBase) != 0) {// IF(上次计费基数 != 计费基数）
                principalInfo = new PrincipalInfo(); // 计费基数变更
                principalInfo.setChangeDate(calDate); // 计费基数变更.变更日 = 计提日
                principalInfo.setPrinAmount(feeBase); // 计费基数变更.变更金额 = 计费基数
                feeBaseLast = feeBase; // 上次计费基数 = 计费基数
                principalInfoList.add(principalInfo); // 添加到技术变更列表中
            }

            calDate = DateUtil.addDay(calDate, 1); // 变量.计提日+1

        }
        while (DateUtil.compareDate(mdateNow, calDate) >= 0);

        // 调用共通方法：通用计算
        CalCommonResult calCommonResult = calculationCommonService.calCommon(vdateNow, DateUtil.addDay(mdateNow, 1),
            principalInfoList, quoteInfoList);

        BigDecimal feeCal = calCommonResult.getTotalInterestAmt(); // 费用计提 = 返回值.总利息金额
        feeCal = ArithmeticUtil.round(feeCal, NumberEnum.MONEY); // 调用金额共通方法设置有效小数位

        if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
            SubjectBalance subjectBalance = this.getSubjectBalanceExpectDirectionCommon(acctEntity,
                happenDate, DictCodeList.余额方向_贷方.getValue());
            // 费用计提 = 费用计提 - DTO.科目余额
            feeCal = ArithmeticUtil.subtract(feeCal,subjectBalance.getBalance()); 
            //定义 赎回利息凭证 = 调用共通方法查询：凭证最新账务查询（场景代码=“8005”，发生开始日=“本次计提开始日+1天”，发生结束日=“dto.发生日”，
            //基础科目性质=“应付利润”，入账日=DTO.入账日，账套代码=DTO.账套代码）
			List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
					acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
					new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.应付利润.getValue())),
					DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getValue(), null, null, null, DateUtils.addDays(vdateNow, 1),
					happenDate);
			// 变量.赎回利息 = 赎回利息凭证.本位币发生额 求和
			if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
				for (BookLatestInfoDto dto : listBookLatestInfoDto) {
					feeCal = ArithmeticUtil.subtract(feeCal, dto.getoHappenAmt());
				}
			}
        }

        // 客户收益计提 - 3）生成记账凭证
        AccountDriver ad = new AccountDriver(); // 记账场景要素管理
        Map<String, Object> accountSceneMap = new HashMap<>(); // 存放该笔记账的场景要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>(); // 存放该笔记账的相关金额要素 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>(); // 存放该笔记账的相关金额对应的币种 map定义

        // 共通元素设值 - 设置账务引擎参数
        ad.setTreeNode(acctEntity.getBookSetTreeNode());// 塞入记账场景树节点 = 会计场景

        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate,
            acctEntity);
        // 是否累计余额
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_否.getValue());
        ad.setTableId(DictCodeListTableId.会计场景代码_产品_预期收益型_客户收益计提.getValue());// // 设置账务引擎参数：ad.会计场景代码;
        
        accountSceneMap.put(BookConstant.BRANCH, acctEntity.getBranch()); //分支序号
        
        accountSceneMap.put(BookConstant.BOOK_REMARK, "预期收益型TA客户收益计提登账");// 备注 <- "TA销售服务费计提登账"
        accountAmountMap.put(BookConstant.INTAMT, feeCal); // 费用计提 <- 最终计提金额
        //获取当前账套账务明细代码
        StayDrawSubprdEntity prdEntity=expParPrdTaCusIncDrawBookMapper.queryBookdetailId(params);
        if(StringUtil.isNotEmpty(prdEntity)){
//            if(DictCodeList.特殊核算模型_普通分层核算.getValue().equals(prdEntity.getBokSpecMode())){
                accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,
                    bookCommonLogic.getValBokDetailId(prdEntity.getFinprodId(), prdEntity.getBokDetailId(), prdEntity.getFinprodType(),prdEntity.getStandardCcy()));
//            }
        }

        // 记账金额元素币种信息塞值
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, acctEntity.getStandardCcy()); // 费用计提 <- 原币
        }
        
        //凭证编号
        BokBookdetailCurrent bookDetail = new BokBookdetailCurrent();
        // 计提类：场景代码 + 发生日 + 账务明细代码 + 冲销标识
        // 场景代码
        bookDetail.setTableId(ad.getTableId());
        // 发生日期 <- 场景要素.发生日期
        bookDetail.setHappenDate((Date)accountSceneMap.get(BookConstant.HAPPEN_DATE));
        // 账务明细代码
        BokDetailId valBokDetailId = (BokDetailId) accountSceneMap.get(BookConstant.BOK_DETAIL_ID1);
        bookDetail.setBokDetailId(valBokDetailId.getBokDetailId());
        // 冲销标识
        bookDetail.setOffsetFlag((String) accountSceneMap.get(BookConstant.OFFSET_FLAG));
        String bookId = bookAccountCommonService.genBookId(bookDetail);
        accountSceneMap.put(BookConstant.BOOK_ID, bookId);
        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

        // 记账凭证生成 : 调用记账引擎公共记账方法，生成记账凭证
        BookGenerateResult bookGenerateResult = bookLogicNew.genAccount(ad, batchExecInfo);
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        rtnResult.getValBokBookdetailBatch().addAll(bookGenerateResult.getValBokBookdetailBatch());

        return rtnResult;
    }

    /**
     * 调用共通方法-科目余额查询 - 期待方向 注：具体类型根据DTO.现金流类型判断
     * 
     * @param acctEntity reader 返回值
     * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
     * @param expBalanceDirect 期待余额方向
     * @return SubjectBalance
     */
    private SubjectBalance getSubjectBalanceExpectDirectionCommon(StayDrawSubprdEntity acctEntity,
                                                                  Date happenDate,
                                                                  String expBalanceDirect) {
        // 设置基础科目性质
        String baseSubjectNature =  DictCodeBaseSubjectNatrueNew.应付利润.getValue();; // 基础科目性质

        // 塞入 基础科目性质
        List<String> baseSubjectNatureList = null;
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = Lists.newArrayList();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setHappenDate(happenDate);// 发生日
        searchDto.setBookDate(acctEntity.getBookDate());// 入账日
        searchDto.setBokDetailId(acctEntity.getBokDetailId());// 账务明细代码
        searchDto.setBooksetId(acctEntity.getBooksetId());// 账套代码
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 基础科目性质
        searchDto.setTaskPri(acctEntity.getTaskPri());// 任务优先级

        // 调用共通方法 - 科目余额查询
        List<SubjectBalance> subjectLatestBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        SubjectBalance subjectBalance = new SubjectBalance(); // 返回的实体类
        if (CollectionUtils.isNotEmpty(subjectLatestBalanceList)) {
            subjectBalance = subjectLatestBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 获取标的相关信息
     * 
     * @param acctEntity reader 返回值
     * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
     * @return Map<String, Object>
     */
    private Map<String, Object> getTargetInfo(StayDrawSubprdEntity acctEntity, Date happenDate) {
        Map<String, Object> returnMap = Maps.newHashMap(); // 返回值

        List<TargetSearchDto> queryCriteria = Lists.newArrayList(); // 共同查询类型

        // 传入 标的_现金流计算规则 Dto
        TargetSearchDto targetSearchDto1 = new TargetSearchDto();
        targetSearchDto1.setFinprodId( acctEntity.getFinprodId());
        targetSearchDto1.setFinprodType( acctEntity.getFinprodType());
        targetSearchDto1.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        targetSearchDto1.setCashType1(acctEntity.getCashType1());// 现金流一级类型
        targetSearchDto1.setCashType2(acctEntity.getCashType2());// 现金流二级类型
        targetSearchDto1.setCashId(acctEntity.getCashId());// 现金流代码
        targetSearchDto1.setEffdateStart(acctEntity.getVdate());// 起息日
        targetSearchDto1.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
        targetSearchDto1.setBranch(acctEntity.getBranch());
        queryCriteria.add(targetSearchDto1);

        // 传入 标的_现金流计划 Dto
        TargetSearchDto targetSearchDto2 = new TargetSearchDto();
        targetSearchDto2.setFinprodId( acctEntity.getFinprodId());
        targetSearchDto2.setFinprodType( acctEntity.getFinprodType());
        targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        targetSearchDto2.setCashType1(acctEntity.getCashType1());// 现金流一级类型
        targetSearchDto2.setCashType2(acctEntity.getCashType2());// 现金流二级类型
        targetSearchDto2.setCashId(acctEntity.getCashId());// 现金流代码
        targetSearchDto2.setEffdateStart(acctEntity.getVdate());// 起息日
        targetSearchDto2.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
        targetSearchDto2.setBranch(acctEntity.getBranch());
        queryCriteria.add(targetSearchDto2);

        // 传入 标的_业绩报酬计提规则
        TargetSearchDto targetSearchDto3 = new TargetSearchDto();
        targetSearchDto3.setFinprodId( acctEntity.getFinprodId());
        targetSearchDto3.setFinprodType( acctEntity.getFinprodType());
        targetSearchDto3.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        targetSearchDto3.setCashType1(acctEntity.getCashType1());// 现金流一级类型
        targetSearchDto3.setCashType2(acctEntity.getCashType2());// 现金流二级类型
        targetSearchDto3.setCashId(acctEntity.getCashId());// 现金流代码
        targetSearchDto3.setEffdateStart(acctEntity.getVdate());// 起息日
        targetSearchDto3.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
        targetSearchDto3.setBranch(acctEntity.getBranch());
        queryCriteria.add(targetSearchDto3);

        // 传入 标的_业绩报酬计提计划
        TargetSearchDto targetSearchDto4 = new TargetSearchDto();
        targetSearchDto4.setFinprodId( acctEntity.getFinprodId());
        targetSearchDto4.setFinprodType( acctEntity.getFinprodType());
        targetSearchDto4.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
        targetSearchDto4.setCashType1(acctEntity.getCashType1());// 现金流一级类型
        targetSearchDto4.setCashType2(acctEntity.getCashType2());// 现金流二级类型
        targetSearchDto4.setCashId(acctEntity.getCashId());// 现金流代码
        targetSearchDto4.setEffdateStart(acctEntity.getVdate());// 起息日
        targetSearchDto4.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
        targetSearchDto4.setBranch(acctEntity.getBranch());
        queryCriteria.add(targetSearchDto4);

        // 传入[标的_信息]Dto
        TargetSearchDto targetSearchDto5 = new TargetSearchDto();
        targetSearchDto5.setFinprodId( acctEntity.getFinprodId());
        targetSearchDto5.setFinprodType( acctEntity.getFinprodType());
        targetSearchDto5.setQueryType(ValDictCodeList.标的_信息.getValue());
        targetSearchDto5.setBranch(acctEntity.getBranch());
        queryCriteria.add(targetSearchDto5);

        // 获取标的信息Map，共通 标的信息查询-现金流计算规则
        Map<TargetSearchDto, Object> map = finProductCommonService.getTargetInfo(
            acctEntity.getFinprodId(), queryCriteria);

        if (MapUtils.isNotEmpty(map)) {
            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), map.get(targetSearchDto1)); // 标的现金流计算规则list
            returnMap.put(ValDictCodeList.标的_现金流计划.getValue(), map.get(targetSearchDto2)); // 标的现金流计划list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), map.get(targetSearchDto3)); // 标的业绩报酬计提规则list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), map.get(targetSearchDto4)); // 标的业绩报酬计提计划list
            returnMap.put(ValDictCodeList.标的_信息.getValue(), map.get(targetSearchDto5)); // 获取标的信息
        }

        return returnMap;
    }

}
